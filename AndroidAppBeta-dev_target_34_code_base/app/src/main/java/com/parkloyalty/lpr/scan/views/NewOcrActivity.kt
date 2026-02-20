package com.parkloyalty.lpr.scan.views

//import com.github.dhaval2404.imagepicker.ImagePicker
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.databinding.ActivityNewOcrActivityBinding
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.mainScope
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class NewOcrActivity : AppCompatActivity() {
    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var sharedPreference: SharedPref

    @Inject
    lateinit var appDatabase: AppDatabase


    private lateinit var binding: ActivityNewOcrActivityBinding

    private lateinit var recognizer: TextRecognizer
    private val TAG = "Testing"
    private val SAVED_TEXT_TAG = "SavedText"

    lateinit var camera: Camera
    var savedBitmap: Bitmap? = null
    private var isButtonClick = false
    private var isImageSaving = false

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var textInImageLayout: MaterialCardView
    private lateinit var textInImage: TextView
    private lateinit var torchButton: MaterialCardView
    private lateinit var torchImage: AppCompatImageView
    private lateinit var extractTextButton: AppCompatImageView
    private lateinit var share: MaterialCardView
    private lateinit var ocrbitmap: AppCompatImageView
    private lateinit var overlayFrameLayout: FrameLayout
    private lateinit var previewImage: ImageView
    private lateinit var copyToClipboard: ImageView
    private lateinit var close: ImageView
    private lateinit var ivBack: ImageView
    private lateinit var viewFinder: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewOcrActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionManager =
            permissionFactory.create(caller = this, context = this@NewOcrActivity, activity = this)

        findViewsByViewBinding()
        setupClickListeners()

        if (savedInstanceState != null) {
            val savedText = savedInstanceState.getString(SAVED_TEXT_TAG)
            textInImageLayout.apply {
                if (isTextValid(savedText)) {
                    textInImageLayout.visibility = View.VISIBLE
                    textInImage.text = savedInstanceState.getString(SAVED_TEXT_TAG)
                }
                if (savedBitmap != null) {
                    previewImage.visibility = View.VISIBLE
                    previewImage.setImageBitmap(savedBitmap)
                    isButtonClick = false
                } else {
                    isButtonClick = true
                }
                //previewImage.setImageBitmap(savedInstanceState.getParcelable(SAVED_IMAGE_BITMAP))
            }
        }
        init()

        val directoryCamera = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.VINCAMERA
        )

        val sdf = SimpleDateFormat("EEEE")
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)
        LogUtil.printLog("day of today", dayOfTheWeek)
        if (dayOfTheWeek.equals("Friday", ignoreCase = true)) {
            FileUtil.deleteRecursive(directoryCamera)
        }
    }

    private fun findViewsByViewBinding() {
        textInImageLayout = binding.textInImageLayout
        textInImage = binding.textInImage
        torchButton = binding.torchButton
        torchImage = binding.torchImage
        extractTextButton = binding.extractTextButton
        share = binding.share
        ocrbitmap = binding.ocrbitmap
        overlayFrameLayout = binding.overlay
        previewImage = binding.previewImage
        copyToClipboard = binding.copyToClipboard
        close = binding.close
        ivBack = binding.ivBack
        viewFinder = binding.viewFinder


    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            finish()
        }

        extractTextButton.apply {
            extractTextButton.setOnClickListener {
                if (isButtonClick == false) {
                    isButtonClick = true
                    when {
                        previewImage.visibility == View.VISIBLE -> {
                            savedBitmap = previewImage.drawable.toBitmap()
                            runTextRecognition(savedBitmap!!)
                        }

                        viewFinder.bitmap != null -> {
                            previewImage.visibility = View.VISIBLE
                            savedBitmap = viewFinder.bitmap
                            previewImage.setImageBitmap(viewFinder.bitmap!!)
                            runTextRecognition(savedBitmap!!)

                            ocrbitmap.setImageBitmap(savedBitmap!!)

                        }

                        else -> {
                            this@NewOcrActivity.toast("camera_error_default_msg")
                        }
                    }
                }
            }

            copyToClipboard.setOnClickListener {
                val textToCopy = textInImage.text
                if (isTextValid(textToCopy.toString())) {
                    copyToClipboard(textToCopy)
                } else {
                    this@NewOcrActivity.toast("no_text_found")
                }
            }

            share.setOnClickListener {
                val textToCopy = textInImage.text.toString()
                if (isTextValid(textToCopy)) {
                    shareText(textToCopy)
                } else {
                    this@NewOcrActivity.toast("no_text_found")
                }
            }

            close.setOnClickListener {
                textInImageLayout.visibility = View.GONE
            }
        }
    }

    private fun init() {

        cameraExecutor = Executors.newSingleThreadExecutor()

        try {
            recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            Log.d("OCR", "Recognizer initialized successfully")
        } catch (e: Exception) {
            e.printStackTrace()
        }


//
//        // Request camera permissions
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            requestPermissions()
//        }

        lifecycleScope.launch {
            permissionManager.ensurePermissionsThen(
                permissions = arrayOf(Manifest.permission.CAMERA),
                rationaleMessage = getString(R.string.permission_message_camera_permission_required)
            ) {
                startCamera()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!

                // Use Uri object instead of File to avoid storage permissions
                previewImage.apply {
                    visibility = View.VISIBLE
                    setImageURI(uri)
                }
                //runTextRecognition(binding.previewImage.drawable.toBitmap())
            }
//            ImagePicker.RESULT_ERROR -> {
//                showToast(data.toString())
//            }
            else -> {
                this@NewOcrActivity.toast("No Image Selected")
            }
        }
    }

    private fun runTextRecognition(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        mainScope.launch {
            if (isImageSaving == false) {
                isImageSaving = true
                saveImageMM(savedBitmap)
            }
        }
        recognizer.process(inputImage).addOnSuccessListener { text ->
            textInImageLayout.visibility = View.VISIBLE
            processTextRecognitionResult(text)
        }.addOnFailureListener { e ->
            e.printStackTrace()
            this@NewOcrActivity.toast(e.localizedMessage ?: ("error_default_msg"))
        }
    }

    private fun processTextRecognitionResult(result: Text) {
        var finalText = ""
        for (block in result.textBlocks) {
            for (line in block.lines) {
//                finalText += line.text + " \n"
                finalText += line.text
                break
            }
//            finalText += "\n"
        }

        logD(TAG, finalText)
        logD(TAG, result.text)

        textInImage.text = finalText.ifEmpty {
            "no_text_found"
        }

        Linkify.addLinks(textInImage, Linkify.ALL)

        val finishIntent = Intent()
        finishIntent.putExtra("OCR_TEXT", textInImage.text)
        setResult(RESULT_OK, finishIntent)
        finish()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview =
                Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build().also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
                title.apply {
//                    if(!allPermissionsGranted()){
//                        requestPermissions()
//                    } else {
//                        previewImage.visibility = View.GONE
//                        savedBitmap = null
//                    }

                    lifecycleScope.launch {
                        permissionManager.ensurePermissionsThen(
                            permissions = arrayOf(Manifest.permission.CAMERA),
                            rationaleMessage = getString(R.string.permission_message_camera_permission_required)
                        ) {
                            previewImage.visibility = View.GONE
                            savedBitmap = null
                        }
                    }
                }

                torchImage.apply {

                    camera.apply {
                        torchImage.setBackgroundColor(
                            ContextCompat.getColor(
                                this@NewOcrActivity,
                                R.color.ripple_material_light
                            )
                        )
                        if (cameraInfo.hasFlashUnit()) {
                            torchButton.setOnClickListener {
                                cameraControl.enableTorch(cameraInfo.torchState.value == TorchState.OFF)
                            }
                        } else {
                            torchButton.setOnClickListener {
                                this@NewOcrActivity.toast("torch_not_available_msg")
                            }
                        }

                        cameraInfo.torchState.observe(this@NewOcrActivity) { torchState ->
                            if (torchState == TorchState.OFF) {
                                torchImage.setImageResource(R.drawable.baseline_flashlight_on_24)
                            } else {
                                torchImage.setImageResource(R.drawable.baseline_flashlight_off_24)
                            }
                        }

                    }

                }
            } catch (exc: Exception) {
                this@NewOcrActivity.toast(getString(R.string.error_default_msg))
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }


    private fun isTextValid(text: String?): Boolean {
        if (text == null) return false

        return text.isNotEmpty() and !text.equals(getString(R.string.no_text_found))
    }

    private fun shareText(text: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text_title)))
    }

    private fun copyToClipboard(text: CharSequence) {
        val clipboard =
            ContextCompat.getSystemService(applicationContext, ClipboardManager::class.java)
        val clip = ClipData.newPlainText("label", text)
        clipboard?.setPrimaryClip(clip)
        this@NewOcrActivity.toast(getString(R.string.clipboard_text))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
        val textInImage = (textInImage.text).toString()
        if (isTextValid(textInImage)) {
            outState.putString(SAVED_TEXT_TAG, textInImage)
        }/*if (binding.previewImage.visibility == View.VISIBLE) {
            outState.putParcelable(SAVED_IMAGE_BITMAP, binding.previewImage.drawable.toBitmap())
        }*/
    }

    //TODO will add comments later
    private fun saveImageMM(finalBitmap: Bitmap?) {

        val bytes = cropImage(finalBitmap!!, viewFinder, overlayFrameLayout)

        val croppedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.VINCAMERA
        )
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname = "Vin_" + timeStamp + "_capture.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            croppedImage!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //20less than 300 kb
            out.flush()
            out.close()
            val oldFname = "IMG_temp.jpg"
            val oldFile = File(myDir, oldFname)
            if (oldFile.exists()) oldFile.delete()
            val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
            //save image to db
            val pathDb = file.path
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = pathDb
            mImage.id = id.toInt()

            lifecycleScope.launch {
                appDatabase.citationDao.insertCitationImage(mImage)
            }

            //set image list adapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cropImage(bitmap: Bitmap, containerImage: View, containerOverlay: View): ByteArray {
        val heightOriginal = containerImage.height
        val widthOriginal = containerImage.width
        val heightFrame = containerOverlay.height
        val widthFrame = containerOverlay.width
        val leftFrame = containerOverlay.left
        val topFrame = containerOverlay.top
        val heightReal = bitmap.height
        val widthReal = bitmap.width
        val widthFinal = widthFrame * widthReal / widthOriginal
        val heightFinal = heightFrame * heightReal / heightOriginal
        val leftFinal = leftFrame * widthReal / widthOriginal
        val topFinal = topFrame * heightReal / heightOriginal
        val bitmapFinal = Bitmap.createBitmap(
            bitmap, leftFinal, topFinal, widthFinal, heightFinal
        )
        val stream = ByteArrayOutputStream()
        bitmapFinal.compress(
            Bitmap.CompressFormat.JPEG, 100, stream
        ) //100 is the best quality possibe
        return stream.toByteArray()
    }
}