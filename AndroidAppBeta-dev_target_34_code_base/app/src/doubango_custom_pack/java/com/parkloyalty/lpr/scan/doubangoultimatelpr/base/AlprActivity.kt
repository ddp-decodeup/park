package com.parkloyalty.lpr.scan.doubangoultimatelpr.base

import android.graphics.RectF
import android.media.ExifInterface
import android.media.Image
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Size
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.doubangoultimatelpr.AlprCameraFragment
import com.parkloyalty.lpr.scan.doubangoultimatelpr.license.AlprLicenseActivator
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.AlprUtils
import com.parkloyalty.lpr.scan.doubangoultimatelpr.views.AlprPlateView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import org.doubango.ultimateAlpr.Sdk.UltAlprSdkParallelDeliveryCallback
import org.doubango.ultimateAlpr.Sdk.UltAlprSdkResult
import org.doubango.ultimateAlpr.Sdk.UltAlprSdkEngine
import org.doubango.ultimateAlpr.Sdk.ULTALPR_SDK_IMAGE_TYPE
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.jvm.Synchronized

//https://github.com/DoubangoTelecom/ultimateALPR-SDK
@AndroidEntryPoint
abstract class AlprActivity : AppCompatActivity(), AlprCameraFragment.AlprCameraFragmentSink {
    /**
     * Gets the base folder defining a path where the application can write private
     * data.
     * @return The path
     */
    protected var debugInternalDataPath: String? = null
    private var mIsProcessing = false
    private var mIsPaused = true

    private val TAG = AlprActivity::class.java.canonicalName

    /**
     * Parallel callback delivery function used by the engine to notify for new deferred results
     */
    internal class MyUltAlprSdkParallelDeliveryCallback : UltAlprSdkParallelDeliveryCallback() {
        var mAlprPlateView: AlprPlateView? = null
        var mImageSize: Size? = null
        var mTotalDuration: Long = 0
        var mOrientation = 0
        fun setAlprPlateView(view: AlprPlateView) {
            mAlprPlateView = view
        }

        fun setImageSize(imageSize: Size, orientation: Int) {
            mImageSize = imageSize
            mOrientation = orientation
        }

        fun setDurationTime(totalDuration: Long) {
            mTotalDuration = totalDuration
        }

        override fun onNewResult(result: UltAlprSdkResult) {
            printLog(TAG, AlprUtils.resultToString(result))
            if (mAlprPlateView != null && mImageSize != null) {
                mAlprPlateView?.setResult(result, mImageSize!!, mTotalDuration, mOrientation)
            }
        }

        companion object {
            val TAG = MyUltAlprSdkParallelDeliveryCallback::class.java.canonicalName
            fun newInstance(): MyUltAlprSdkParallelDeliveryCallback {
                return MyUltAlprSdkParallelDeliveryCallback()
            }
        }
    }

    /**
     * The parallel delivery callback. Set to null to disable parallel mode
     * and enforce sequential mode.
     */
    private var mParallelDeliveryCallback: MyUltAlprSdkParallelDeliveryCallback? = null
    private var mAlprPlateView: AlprPlateView? = null

    //Needed AppDatabase & WelcomeListDatatbase for AlprVideoParallelActivity
//    var mDb: AppDatabase? = null
//    var mWelcomeListDataSet: WelcomeListDatatbase? = null

    @Inject
    lateinit var sharedPreference: SharedPref
    var licenseKeyForTheDevice : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        printLog(TAG, "onCreate $this")
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        setContentView(layoutResId)

//        mDb = BaseApplication.instance?.getAppDatabase()
//        mWelcomeListDataSet = mDb?.let { Singleton.getWelcomeDbObject(it) }

        licenseKeyForTheDevice = sharedPreference.read(SharedPrefKey.LICENSE_KEY_ALPR, "")


        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        // Create folder to dump input images for debugging
        val dummyFile = File(getExternalFilesDir(null), "dummyFile")
        if (!dummyFile.parentFile.exists() && !dummyFile.parentFile.mkdirs()) {
            printLog(TAG, "mkdir failed: " + dummyFile.parentFile.absolutePath)
        }
        debugInternalDataPath =
            if (dummyFile.parentFile.exists()) dummyFile.parent else Environment.getExternalStorageDirectory().absolutePath
        dummyFile.delete()

        // Create parallel delivery callback is enabled
        // mParallelDeliveryCallback = if (isParallelDeliveryEnabled()) MyUltAlprSdkParallelDeliveryCallback.newInstance() else null
        //mParallelDeliveryCallback = MyUltAlprSdkParallelDeliveryCallback.newInstance()
        mParallelDeliveryCallback = MyUltAlprSdkParallelDeliveryCallback()

        // Init the engine
        val config = jsonConfig
        // Retrieve previously stored key from internal storage
        var tokenFile = AlprLicenseActivator.tokenFile(this)
        if (!tokenFile.isEmpty()) {
            try {
                config.put("license_token_data", AlprLicenseActivator.tokenData(tokenFile))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val alprResult = AlprUtils.assertIsOk(
            UltAlprSdkEngine.init(
                assets,
                config.toString(),
                mParallelDeliveryCallback
            )
        )
        printLog(TAG, "ALPR engine initialized: " + AlprUtils.resultToString(alprResult))

        // Activate the license
        val isActivationPossible =
            !getActivationServerUrl().isEmpty() && !getActivationMasterOrSlaveKey().isEmpty()
        if (isActivationPossible && tokenFile.isEmpty()) {
            // Generate the license key and store it to the internal storage for next times
            tokenFile = AlprLicenseActivator.activate(
                this,
                getActivationServerUrl().nullSafety(),
                getActivationMasterOrSlaveKey().nullSafety(),
                false
            ).nullSafety()
            if (!tokenFile.isEmpty()) {
                try {
                    config.put("license_token_data", AlprLicenseActivator.tokenData(tokenFile))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                AlprUtils.assertIsOk(
                    UltAlprSdkEngine.init(
                        assets,
                        config.toString(),
                        mParallelDeliveryCallback
                    )
                )
            } else {
                //If you want to show toast to the user alrerting in licensing service, uncomment below comment.
                //showToast(this@AlprActivity,"License is expired or invalid, please contact your service provider", Toast.LENGTH_LONG)
            }
        }

        // WarmUp to speedup first inference
        AlprUtils.assertIsOk(UltAlprSdkEngine.warmUp(ULTALPR_SDK_IMAGE_TYPE.ULTALPR_SDK_IMAGE_TYPE_YUV420P))
    }

    public override fun onDestroy() {
        printLog(TAG, "onDestroy $this")
        // DeInitialize the engine. This will stop all threads and cleanup all pending calls.
        // If you're performing a work in a parallel callback thread, then this function will
        // block until the end.
        val result = AlprUtils.assertIsOk(UltAlprSdkEngine.deInit())
        printLog(TAG, "ALPR engine deInitialized: " + AlprUtils.resultToString(result))
        super.onDestroy()
    }

    @Synchronized
    public override fun onResume() {
        super.onResume()
        mIsPaused = false
    }

    @Synchronized
    public override fun onPause() {
        mIsPaused = true
        super.onPause()
    }

    override fun setAlprPlateView(view: AlprPlateView) {
        mAlprPlateView = view
        if (mParallelDeliveryCallback != null) {
            mParallelDeliveryCallback!!.setAlprPlateView(view)
        }
        val roi = getDetectROI()
        assert(roi.size == 4)
        mAlprPlateView!!.setDetectROI(
            RectF(
                roi[0],
                roi[2],
                roi[1],
                roi[3]
            )
        )
    }

    override fun setImage(image: Image, jpegOrientation: Int) {

        // On sequential mode we just ignore the processing
        if (mIsProcessing || mIsPaused) {
            printLog(TAG, "Inference function not returned yet: Processing or paused")
            image.close()
            return
        }
        mIsProcessing = true
        val imageSize = Size(image.width, image.height)

        // Orientation
        // Convert from degree to real EXIF orientation
        val exifOrientation: Int
        exifOrientation = when (jpegOrientation) {
            90 -> ExifInterface.ORIENTATION_ROTATE_90
            180 -> ExifInterface.ORIENTATION_ROTATE_180
            270 -> ExifInterface.ORIENTATION_ROTATE_270
            0 -> ExifInterface.ORIENTATION_NORMAL
            else -> ExifInterface.ORIENTATION_NORMAL
        }

        // Update image for the async callback
        if (mParallelDeliveryCallback != null) {
            mParallelDeliveryCallback!!.setImageSize(
                if (jpegOrientation % 180 == 0) imageSize else Size(
                    imageSize.height,
                    imageSize.width
                ), jpegOrientation
            )
        }

        // The actual ALPR inference is done here
        // Do not worry about the time taken to perform the inference, the caller
        // (most likely the camera fragment) set the current image using a background thread.
        val planes = image.planes
        val startTimeInMillis = SystemClock.uptimeMillis()
        val result =  /*AlprUtils.assertIsOk*/UltAlprSdkEngine.process(
            ULTALPR_SDK_IMAGE_TYPE.ULTALPR_SDK_IMAGE_TYPE_YUV420P,
            planes[0].buffer,
            planes[1].buffer,
            planes[2].buffer,
            imageSize.width.toLong(),
            imageSize.height.toLong(),
            planes[0].rowStride.toLong(),
            planes[1].rowStride.toLong(),
            planes[2].rowStride.toLong(),
            planes[1].pixelStride.toLong(),
            exifOrientation
        )
        val durationInMillis =
            SystemClock.uptimeMillis() - startTimeInMillis // Total time: Inference + image processing (chroma conversion, rotation...)
        if (mParallelDeliveryCallback != null) {
            mParallelDeliveryCallback!!.setDurationTime(durationInMillis)
        }

        // Release the image and signal the inference process is finished
        image.close()
        mIsProcessing = false
        if (result.isOK) {
            printLog(TAG, AlprUtils.resultToString(result))
        } else {
            printLog(TAG, AlprUtils.resultToString(result))
        }

        // Display the result if sequential mode. Otherwise, let the parallel callback
        // display the result when provided.
        // Starting version 3.2 the callback will be called even if the result is empty
        if (mAlprPlateView != null && (mParallelDeliveryCallback == null || result.numPlates() == 0L && result.numCars() == 0L)) { // means sequential call or no plate/car to expect from the parallel delivery callback
            //mAlprPlateView.setResult(result, (jpegOrientation % 180) == 0 ? imageSize : new Size(imageSize.getHeight(), imageSize.getWidth()), durationInMillis, jpegOrientation);
            mAlprPlateView!!.setResult(
                image,
                result,
                if (jpegOrientation % 180 == 0) imageSize else Size(
                    imageSize.height,
                    imageSize.width
                ),
                durationInMillis,
                jpegOrientation
            )
        }
    }

    /**
     * Gets the server url used to activate the license. Please contact us to get the correct URL.
     * e.g. https://localhost:3600
     * @return The URL
     */
    protected open fun getActivationServerUrl(): String {
        return ""
    }

    /**
     * Gets the master or slave key to use for the activation.
     * You MUST NEVER include your master key in the code or share it with the end user.
     * The master key should be used to generate slaves (one-time activation keys).
     * More information about master/slave keys at https://www.doubango.org/SDKs/LicenseManager/docs/Jargon.html.
     * @return The master of slave key.
     */
    protected open fun getActivationMasterOrSlaveKey(): String {
        return ""
    }

    /**
     * Returns the layout Id for the activity
     * @return
     */
    protected abstract val layoutResId: Int

    /**
     * Returns JSON config to be used to initialize the ALPR/ANPR SDK.
     * @return The JSON config
     */
    protected abstract val jsonConfig: JSONObject

    /**
     */
    protected abstract fun isParallelDeliveryEnabled(): Boolean
    protected abstract fun getDetectROI(): List<Float>
}