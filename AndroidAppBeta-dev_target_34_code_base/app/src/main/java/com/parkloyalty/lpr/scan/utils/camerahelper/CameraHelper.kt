package com.parkloyalty.lpr.scan.utils.camerahelper


import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.parkloyalty.lpr.scan.interfaces.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraHelper @Inject constructor(
    @ApplicationContext private val appContext: Context
) {

    // Optionally keep a nullable var that can be set if you want:
    private var providerAuthorityOverride: String? = null

    // Provide a setter if you want to override from code (optional)
    fun setProviderAuthorityOverride(authority: String?) {
        providerAuthorityOverride = authority
    }

    enum class SaveLocation {
        APP_EXTERNAL_FILES, MEDIA_STORE_PUBLIC
    }

    /**
     * Session: created per Fragment/Activity to register a launcher tied to that lifecycle.
     * It holds a private pending callback/deferred and decodes the image on launcher callback.
     */
    inner class Session internal constructor(
        private val context: Context,
        private val providerAuthority: String,
        private val launcher: ActivityResultLauncher<Uri>
    ) {
        private var currentUri: Uri? = null
        private var pendingCallback: ((Bitmap?) -> Unit)? = null
        private var pendingDeferred: CompletableDeferred<Bitmap?>? = null

        /**
         * Launch camera and get Bitmap via callback.
         */
        fun takePicture(
            suggestedFileName: String,
            saveLocation: SaveLocation = SaveLocation.APP_EXTERNAL_FILES,
            onResult: (Bitmap?) -> Unit
        ) {
            try {
                val uri = createImageUriInternal(context, suggestedFileName, saveLocation)
                if (uri == null) {
                    onResult(null)
                    return
                }
                currentUri = uri
                pendingCallback = onResult
                launcher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }

        /**
         * Suspend variant. Cancelling the coroutine cancels the deferred.
         */
        suspend fun takePictureSuspend(
            suggestedFileName: String,
            saveLocation: SaveLocation = SaveLocation.APP_EXTERNAL_FILES
        ): Bitmap? {
            // avoid nested suspendCancellableCoroutine + CompletableDeferred; use CompletableDeferred
            pendingDeferred = CompletableDeferred()
            takePicture(suggestedFileName, saveLocation) { bmp ->
                // complete the deferred if still active
                pendingDeferred?.complete(bmp)
            }
            try {
                return pendingDeferred?.await()
            } finally {
                pendingDeferred = null
            }
        }

        // called by launcher callback
        internal fun onLauncherResult(success: Boolean) {
            val cb = pendingCallback
            val deferred = pendingDeferred
            pendingCallback = null
            pendingDeferred = null

            if (!success || currentUri == null) {
                cb?.invoke(null)
                deferred?.complete(null)
                return
            }

            try {
                val bmp = decodeBitmapWithFixedOrientation(context, currentUri!!)
                cb?.invoke(bmp)
                deferred?.complete(bmp)
            } catch (e: Exception) {
                e.printStackTrace()
                cb?.invoke(null)
                deferred?.completeExceptionally(e)
            }
        }
    }

    /**
     * Create a Session for a Fragment. Registers launcher attached to the Fragment lifecycle.
     *
     * Usage: val session = cameraHelper.createSession(this)
     * then session.takePicture(...)
     */
    fun createSession(fragment: Fragment, providerAuthority: String? = null): Session {
        val ctx = fragment.requireContext()
        val authority =
            providerAuthority ?: providerAuthorityOverride ?: (ctx.packageName + ".provider")

        // helperRef needs to be assigned after constructing session; we use a two-step pattern
        lateinit var sessionRef: Session
        val launcher = fragment.registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            ActivityResultCallback { success ->
                sessionRef.onLauncherResult(success)
            })
        sessionRef = Session(ctx, authority, launcher)
        return sessionRef
    }

    /**
     * Create a Session for an Activity.
     */
    fun createSession(activity: FragmentActivity, providerAuthority: String? = null): Session {
        val ctx = activity.applicationContext
        val authority = providerAuthority ?: providerAuthorityOverride
        ?: (activity.packageName + ".provider")

        lateinit var sessionRef: Session
        val launcher = activity.registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            ActivityResultCallback { success ->
                sessionRef.onLauncherResult(success)
            })
        sessionRef = Session(ctx, authority, launcher)
        return sessionRef
    }

    // ----------------- Helper internals (file creation, media store, decoding) -----------------

    private fun createImageUriInternal(
        context: Context,
        suggestedFileName: String,
        saveLocation: SaveLocation
    ): Uri? {
        return when (saveLocation) {
            SaveLocation.APP_EXTERNAL_FILES -> {
                val f = createImageFile(context, suggestedFileName) ?: return null
                FileProvider.getUriForFile(
                    context, providerAuthorityOverride ?: (context.packageName + ".provider"), f
                )
            }

            SaveLocation.MEDIA_STORE_PUBLIC -> {
                createMediaStoreImageUri(context, suggestedFileName)
            }
        }
    }

    private fun createImageFile(context: Context, suggestedFileName: String): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val name = suggestedFileName.ifBlank { "IMG_$timeStamp" }
            //val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val storageDir = context.getExternalFilesDir(Environment.getExternalStorageDirectory().absolutePath + File.separator + Constants.FILE_NAME + Constants.CAMERA)
            storageDir?.mkdirs()
            File.createTempFile("${name}_", ".jpg", storageDir)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    private fun createMediaStoreImageUri(context: Context, suggestedFileName: String): Uri? {
        val values = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                suggestedFileName.ifBlank { "IMG_${System.currentTimeMillis()}.jpg" })
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        return resolver.insert(collection, values)
    }

    companion object {
        /**
         * Decode bitmap from uri and fix EXIF orientation.
         * inSampleSize logic avoids out-of-memory for big images.
         */
        fun decodeBitmapWithFixedOrientation(
            context: Context,
            uri: Uri,
            maxDim: Int = 5120
        ): Bitmap? {
            val resolver = context.contentResolver
            var input: InputStream? = null
            try {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                input = resolver.openInputStream(uri)
                BitmapFactory.decodeStream(input, null, options)
                input?.close()

                // compute sample size as power-of-two
                var sampleSize = 1
                val w = options.outWidth
                val h = options.outHeight
                val maxSide = w.coerceAtLeast(h)
                if (maxSide > maxDim) {
                    while (maxSide / sampleSize > maxDim) sampleSize *= 2
                }

                val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                input = resolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input, null, decodeOptions)
                input?.close()

                if (bitmap == null) return null

                val exifInput = resolver.openInputStream(uri)
                if (exifInput != null) {
                    val exif = ExifInterface(exifInput)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    exifInput.close()
                    return rotateBitmapIfRequired(bitmap, orientation)
                }
                return bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    input?.close()
                } catch (_: Exception) {
                }
                return null
            }
        }

        private fun rotateBitmapIfRequired(bitmap: Bitmap, exifOrientation: Int): Bitmap {
            val matrix = Matrix()
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                else -> return bitmap
            }
            val rotated =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            return rotated
        }
    }
}
