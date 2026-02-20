package com.parkloyalty.lpr.scan.doubangoultimatelpr

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Size
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.doubangoultimatelpr.base.AlprActivity
import com.parkloyalty.lpr.scan.doubangoultimatelpr.interfaces.FragmentDataIntercepter
import com.parkloyalty.lpr.scan.doubangoultimatelpr.model.VehicleDetailDataModel
import com.parkloyalty.lpr.scan.util.DoubangoConstants.FROM_CONTINUOUS_VEHICLE_MODE
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_FLASH_REQUIRED
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_LENS_STAB
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_VIDEO_STAB
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_PATH_TO_SAVE_IMAGE
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.util.DialogUtils
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_BADGE_ID
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_BEAT
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_BLOCK
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_FROM
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_OFFICER_NAME
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_SIDE
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_SIDE_ID
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_SQUAD
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_STREET
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_TIME_LIMIT_TEXT
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_TIME_LIMIT_VALUE
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_VEHICLE_RUN_METHOD
import com.parkloyalty.lpr.scan.util.DoubangoConstants.DOUBANGO_LPR_SCAN_RESULT
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_VEHICLE_DETAIL_DATA
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MESSAGE
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Main activity
 */
class AlprVideoParallelActivity : AlprActivity(), FragmentDataIntercepter {

    private lateinit var ivBack: AppCompatImageView
    private lateinit var tvToolbarTitle: AppCompatTextView
    private var isContinuousScanStarted = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewsById()
        setupData()
        listeners()
        // At this step, the base class (AlprActivity) already initialized the engine (thanks to "super.onCreate()").
        // Do not try to create the parallel delivery callback in this method. Do it
        // in the constructor or at the declaration (see above). If the engine is initialized without
        // a parallel delivery callback, then it'll run in sequential mode.

        val isLandscapeOrientationRequired = intent.getBooleanExtra(
            KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED, false
        )

        // Add camera fragment to the layout
        val b = Bundle()
        b.putString(KEY_FROM, intent.getStringExtra(KEY_FROM))
        b.putString(KEY_PATH_TO_SAVE_IMAGE, intent.getStringExtra(KEY_PATH_TO_SAVE_IMAGE))
        b.putBoolean(KEY_IS_LENS_STAB, intent.getBooleanExtra(KEY_IS_LENS_STAB, false))
        b.putBoolean(KEY_IS_VIDEO_STAB, intent.getBooleanExtra(KEY_IS_VIDEO_STAB, false))
        b.putBoolean(KEY_IS_FLASH_REQUIRED, intent.getBooleanExtra(KEY_IS_FLASH_REQUIRED, false))
        b.putBoolean(KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED, isLandscapeOrientationRequired)
        if (intent.getStringExtra(KEY_FROM) == FROM_CONTINUOUS_VEHICLE_MODE) {
            b.putString(KEY_OFFICER_NAME, intent.getStringExtra(KEY_OFFICER_NAME))
            b.putString(KEY_BADGE_ID, intent.getStringExtra(KEY_BADGE_ID))
            b.putString(KEY_BEAT, intent.getStringExtra(KEY_BEAT))
            b.putString(KEY_SQUAD, intent.getStringExtra(KEY_SQUAD))
            b.putString(KEY_BLOCK, intent.getStringExtra(KEY_BLOCK))
            b.putString(KEY_STREET, intent.getStringExtra(KEY_STREET))
            b.putString(KEY_SIDE, intent.getStringExtra(KEY_SIDE))
            b.putString(KEY_TIME_LIMIT_TEXT, intent.getStringExtra(KEY_TIME_LIMIT_TEXT))
            b.putString(KEY_TIME_LIMIT_VALUE, intent.getStringExtra(KEY_TIME_LIMIT_VALUE))
            b.putString(KEY_VEHICLE_RUN_METHOD, intent.getStringExtra(KEY_VEHICLE_RUN_METHOD))
        }

        requestedOrientation = if (isLandscapeOrientationRequired) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val alprCameraFragment = AlprCameraFragment.newInstance(PREFERRED_SIZE, this)
        alprCameraFragment.setFragmentDataIntercepter(this)
        alprCameraFragment.arguments = b
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, alprCameraFragment)
            .commit()
    }

    private fun findViewsById() {
        ivBack = findViewById(R.id.ivBack)
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle)
    }

    private fun setupData() {
        tvToolbarTitle.showView()
        tvToolbarTitle.text = getString(R.string.toolbar_title_license_plate__scan)
    }

    private fun listeners() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (intent.getStringExtra(KEY_FROM) == FROM_CONTINUOUS_VEHICLE_MODE && isContinuousScanStarted) {
            DialogUtils.showConfirmationDialog(context = this@AlprVideoParallelActivity,
                message = getString(
                    R.string.confirmation_msg_vehicle_mode_scan_started_do_you_want_to_go_back
                ),
                positiveText = getString(R.string.btn_text_yes),
                negativeText = getString(R.string.btn_text_no),
                callback = { dialog, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        super.onBackPressed()
                    }
                })
        } else {
            super.onBackPressed()
        }
    }

    override fun isContinuousModeStarted(isStarted: Boolean) {
        isContinuousScanStarted = isStarted
    }

    override fun vehicleDetailsInSingleShotMode(
        licensePlateNumber: String?,
        vehicleMakeBrand: String?,
        vehicleModel: String?,
        vehicleColor: String?,
        vehicleBodyStyle: String?,
        licensePlateCountry: String?,
        licensePlateState: String?,
        vehicleImageURL: String?
    ) {
        printLog("PLATE:==>","FOund");
        val vehicleDetailDataModel = VehicleDetailDataModel(
            licensePlateNumber = licensePlateNumber,
            vehicleMakeBrand = vehicleMakeBrand,
            vehicleModel = vehicleModel,
            vehicleColor = vehicleColor,
            vehicleBodyStyle = vehicleBodyStyle,
            licensePlateCountry = licensePlateCountry,
            licensePlateState = licensePlateState,
            vehicleImageURL= vehicleImageURL
        )
        val i = Intent()
        i.putExtra(KEY_VEHICLE_DETAIL_DATA, vehicleDetailDataModel)
        i.putExtra(INTENT_KEY_MESSAGE, DOUBANGO_LPR_SCAN_RESULT)
        setResult(Activity.RESULT_OK, i)
        finish()

        printLog("PLATE:==>","FOund & Finish Called");
    }

    override fun vehicleDetailsInContinuousMode(listOfScannedVehicles: ArrayList<VehicleDetailDataModel?>) {
        //Used in Continuous mode
        val i = Intent()
        //i.putExtra(KEY_VEHICLE_DETAIL_DATA_LIST, vehicleDetailDataModel)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override val layoutResId: Int
        get() = R.layout.activity_alpr

    // More information on the JSON config at https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html
    override val jsonConfig: JSONObject
        get() {
            // More information on the JSON config at https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html
            val config = JSONObject()
            try {
                config.put("debug_level", CONFIG_DEBUG_LEVEL)
                config.put("debug_write_input_image_enabled", CONFIG_DEBUG_WRITE_INPUT_IMAGE)
                config.put("debug_internal_data_path", debugInternalDataPath)
                config.put("num_threads", CONFIG_NUM_THREADS)
                config.put("gpgpu_enabled", CONFIG_GPGPU_ENABLED)
                config.put("charset", CONFIG_CHARSET)
                config.put("max_latency", CONFIG_MAX_LATENCY)
                config.put("ienv_enabled", CONFIG_IENV_ENABLED)
                config.put("openvino_enabled", CONFIG_OPENVINO_ENABLED)
                config.put("openvino_device", CONFIG_OPENVINO_DEVICE)
                config.put("detect_minscore", CONFIG_DETECT_MINSCORE)
                config.put("detect_roi", JSONArray(getDetectROI()))
                config.put("car_noplate_detect_enabled", CONFIG_CAR_NOPLATE_DETECT_ENABLED)
                config.put("car_noplate_detect_min_score", CONFIG_CAR_NOPLATE_DETECT_MINSCORE)
                config.put("pyramidal_search_enabled", CONFIG_PYRAMIDAL_SEARCH_ENABLED)
                config.put("pyramidal_search_sensitivity", CONFIG_PYRAMIDAL_SEARCH_SENSITIVITY)
                config.put("pyramidal_search_minscore", CONFIG_PYRAMIDAL_SEARCH_MINSCORE)
                config.put(
                    "pyramidal_search_min_image_size_inpixels",
                    CONFIG_PYRAMIDAL_SEARCH_MIN_IMAGE_SIZE_INPIXELS
                )
                //TODD: Disable extra feature to reduce the size
//                config.put("klass_lpci_enabled", CONFIG_KLASS_LPCI_ENABLED)
//                config.put("klass_vcr_enabled", CONFIG_KLASS_VCR_ENABLED)
//                config.put("klass_vmmr_enabled", CONFIG_KLASS_VMMR_ENABLED)
//                config.put("klass_vbsr_enabled", CONFIG_KLASS_VBSR_ENABLED)
                config.put("klass_vcr_gamma", CONFIG_KLASS_VCR_GAMMA)
                config.put("recogn_minscore", CONFIG_RECOGN_MINSCORE)
                config.put("recogn_score_type", CONFIG_RECOGN_SCORE_TYPE)
                config.put("recogn_rectify_enabled", CONFIG_RECOGN_RECTIFY_ENABLED)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return config
        }

    /* we want to activated parallel instead of sequential delivery */
    override fun isParallelDeliveryEnabled(): Boolean {
        return true /* we want to activated parallel instead of sequential delivery */
    }

    override fun getDetectROI(): List<Float> {
        return CONFIG_DETECT_ROI as List<Float>
    }

    override fun getActivationServerUrl(): String {
        return ACTIVATION_SERVER_URL
    }

    override fun getActivationMasterOrSlaveKey(): String {
        //Getting device's unique friendly name like ex. SEPTA-01, SEPTA-02, DUNCAN-01 etc.
        //val deviceFriendlyName = mDb?.dbDAO?.getWelcomeForm()?.officerDeviceName
//        val deviceAndroidId = AppUtils.getDeviceId(this@AlprVideoParallelActivity)
//
//        //We are matching above device friendly name wih list if licenses we are getting from backend to get the correct license for the device.
//        //val deviceLicenseObject = mWelcomeListDataSet?.welcomeList?.deviceLicenseStats?.firstOrNull()?.responseDeviceLicense?.firstOrNull { it.deviceFriendlyName  == deviceFriendlyName }
//        val deviceLicenseObject = mWelcomeListDataSet?.welcomeList?.deviceLicenseStats?.firstOrNull()?.responseDeviceLicense?.firstOrNull { it.androidId == deviceAndroidId }
//
//        printLog("==>LICENSE_NUMBER:", deviceLicenseObject?.license.nullSafety("NO LICENSE FOUND"))
//        printLog("==>LICENSE_NUMBER:DeviceID", deviceAndroidId)
//        printLog("==>LICENSE_NUMBER:DeviceID", deviceLicenseObject?.deviceFriendlyName)
//
//        //If device's friendly name matches with any object's friendly name from backend response, then it will return the license for doubango
//        return deviceLicenseObject?.license.nullSafety("NO LICENSE FOUND")
//

        printLog("==>LICENSE_NUMBER_ALPR:", licenseKeyForTheDevice.nullSafety("NO LICENSE FOUND"))
        return licenseKeyForTheDevice.nullSafety()

        //In case of static license key (SLAVE_KEY) use below return statement
//        return ACTIVATION_MASTER_OR_SLAVE_KEY
    }

    /**
     * The server url used to activate the license. Please contact us to get the real URL.
     * e.g. https://localhost:3600
     */
    private val ACTIVATION_SERVER_URL = "https://activation.doubango.org:3600"

    /**
     * The master or slave key to use for the activation.
     * You MUST NEVER include your master key in the code or share it with the end user.
     * The master key should be used to generate slaves (one-time activation keys).
     * More information about master/slave keys at https://www.doubango.org/SDKs/LicenseManager/docs/Jargon.html.
     */
    //Janak's One Plus Andorid 11
    //private val ACTIVATION_MASTER_OR_SLAVE_KEY = "APL+PjwAHQQ3NjdnOTphMGI2Zzdfcno8ZzNramI7NDBQaldgPWI3CD1uNDNkOTxmM2s4eUkrdzc/MEFvGApHMjFpeEwyQHI7QD8YHGBZcD12O29DZHBxcW0gBGV+R3dfWSxRKBVgXDU="

    //Janak's One Plus Android 12
    private val ACTIVATION_MASTER_OR_SLAVE_KEY = "APL+PjwAHQQ3NjdnOTphMGI2Zzdfcno8ZzNramI7NDBQaldgP25vXmk+NDNkOTxmM2s4eUkrdzdrPUFvDgxbXmxvPTBKUDAwXhFlLgdwClpmTkZRa2N+Yzc1KHVKX1RxGjlQdXpiQDU="
    //Sri's Phone
    //private val ACTIVATION_MASTER_OR_SLAVE_KEY = "APL+PjwAHQQ3NjdnOTphMGI2Zzdfcno8ZzNramI7NDBQaldgPW80CWs9NDNkOTxmM2s4eUkrdzc5OEFvLQlwcn1vPXxFdQosPDZmeBxoEk1QOTREZ0F8R308HFdqIVx1JlI3Nh1KMTU=";
    //Kristin's Phone
    //private val ACTIVATION_MASTER_OR_SLAVE_KEY = "APL+PjwAHQQ3NjdnOTphMGI2Zzdfcno8ZzNramI7NDBQaldgPTVgWGs7NDNkOTxmM2s4eUkrdzc5P0FvewRwMmZse1p0dTQtIgcwZGBXTX1uaG9SaVlXaTMnHXs7W11kECQXNWZ7YjU=";

    //SEPTA CUSTOMER FOR TESTING BUILD (25 oct 24) Phone (Version: 18, Name: v 25.03.008)
    //private val ACTIVATION_MASTER_OR_SLAVE_KEY = "APL+PjwAHQQ3NjdnOTphMGI2Zzdfcno8ZzNramI7NDBQaldlbTVvDz9qb2RlYzM/MGszdxktcDY4OUFvIR9yT2x/fFI3TzkuPyY1GCJwbkU4Q2xJYmw8T3ILHh9cLFVhGisvPB5zbjU=";

    //PPA-San-Diego-Tablet-Device-01
    //private val ACTIVATION_MASTER_OR_SLAVE_KEY = "APL+PjwAHQQ3NjdnOTphMGI2Zzdfcno8ZzNramI7NDBQaldlOTZhXG44aTU5ZWUwNWwwI0soI2RuMUFvFSRkTmhfP1JxOTtRRh1hACVFBW9QUW5TMVlKaGF0BXxkI1ZTCgIUPyg0cjU=";

//    TESTING EMPTY KEY FOR STAGING
//    private val ACTIVATION_MASTER_OR_SLAVE_KEY = "";


    /**
     * Defines the Region Of Interest (ROI) for the detector. Any pixels outside region of interest will be ignored by the detector.
     * Defining an WxH region of interest instead of resizing the image at WxH is very important as you'll keep the same quality when you define a ROI while you'll lose in quality when using the later.
     * JSON name: "detect_roi"
     * Default: [0.f, 0.f, 0.f, 0.f]
     * type: float[4]
     * pattern: [left, right, top, bottom]
     * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#detect-roi
     */
    private val CONFIG_DETECT_ROI: List<Float?> = listOf(0f, 0f, 0f, 0f)

    companion object {
        /**
         * TAG used for the debug logs.
         */
        val TAG = AlprVideoParallelActivity::class.java.canonicalName

        /**
         * Preferred size for the video stream. Will select the
         * closest size from the camera capabilities.
         */
        val PREFERRED_SIZE = Size(1280, 720)

        /**
         * Defines the debug level to output on the console. You should use "verbose" for diagnostic, "info" in development stage and "warn" on production.
         * JSON name: "debug_level"
         * Default: "info"
         * type: string
         * pattern: "verbose" | "info" | "warn" | "error" | "fatal"
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#debug-level
         */
        const val CONFIG_DEBUG_LEVEL = "info"

        /**
         * Whether to write the transformed input image to the disk. This could be useful for debugging.
         * JSON name: "debug_write_input_image_enabled"
         * Default: false
         * type: bool
         * pattern: true | false
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#debug-write-input-image-enabled
         */
        const val CONFIG_DEBUG_WRITE_INPUT_IMAGE =
            false // must be false unless you're debugging the code

        /**
         * Defines the maximum number of threads to use.
         * You should not change this value unless you know what you're doing. Set to -1 to let the SDK choose the right value.
         * The right value the SDK will choose will likely be equal to the number of virtual core.
         * For example, on an octa-core device the maximum number of threads will be 8.
         * JSON name: "num_threads"
         * Default: -1
         * type: int
         * pattern: [-inf, +inf]
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#num-threads
         */
        const val CONFIG_NUM_THREADS = -1

        /**
         * Whether to enable GPGPU computing. This will enable or disable GPGPU computing on the computer vision and deep learning libraries.
         * On ARM devices this flag will be ignored when fixed-point (integer) math implementation exist for a well-defined function.
         * For example, this function will be disabled for the bilinear scaling as we have a fixed-point SIMD accelerated implementation.
         * Same for many deep learning parts as we're using QINT8 quantized inference.
         * JSON name: "gpgpu_enabled"
         * Default: true
         * type: bool
         * pattern: true | false
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#gpgpu-enabled
         */
        const val CONFIG_GPGPU_ENABLED = true

        /**
         * The parallel processing method could introduce delay/latency in the delivery callback on low-end CPUs.
         * This parameter controls the maximum latency you can tolerate. The unit is number of frames.
         * The default value is -1 which means auto.
         * JSON name: "max_latency"
         * Default: -1
         * type: int
         * pattern: [0, +inf[
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#max-latency
         */
        const val CONFIG_MAX_LATENCY = -1

        /**
         * Defines a charset (Alphabet) to use for the recognizer.
         * JSON name: "charset"
         * Default: latin
         * type: string
         * pattern: latin | korean | chinese
         * Available since: 2.6.2
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#charset
         */
        const val CONFIG_CHARSET = "latin"

        /**
         * Whether to enable Image Enhancement for Night-Vision (IENV).
         * IENV is explained at https://www.doubango.org/SDKs/anpr/docs/Features.html#features-imageenhancementfornightvision.
         *
         * JSON name: "ienv_enabled"
         * Default: false
         * type: bool
         * pattern: true | false
         * Available since: 3.2.0
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#ienv-enabled
         */
        const val CONFIG_IENV_ENABLED = true

        /**
         * Whether to use OpenVINO instead of Tensorflow as deep learning backend engine. OpenVINO is used for detection and classification but not for OCR.
         * OpenVINO is always faster than Tensorflow on Intel products (CPUs, VPUs, GPUs, FPGAs…) and we highly recommend using it.
         * We require a CPU with support for both AVX2 and FMA features before trying to load OpenVINO plugin (shared library).
         * OpenVINO will be disabled with a fallback on Tensorflow if these CPU features are not detected.
         * JSON name: "openvino_enabled"
         * Default: true
         * type: bool
         * pattern: true | false
         * Available since: 3.0.0
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#openvino-enabled
         */
        const val CONFIG_OPENVINO_ENABLED = false

        /**
         * OpenVINO device to use for computations. We recommend using "CPU" which is always correct.
         * If you have an Intel GPU, VPU or FPGA, then you can change this value.
         * If you try to use any other value than "CPU" without having the right device, then OpenVINO will be completely disabled with a fallback on Tensorflow.
         * JSON name: "openvino_device"
         * Default: "CPU"
         * type: string
         * pattern: "GNA" | "HETERO" | "CPU" | "MULTI" | "GPU" | "MYRIAD" | "HDDL " | "FPGA"
         * Available since: 3.0.0
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#openvino-device
         */
        const val CONFIG_OPENVINO_DEVICE = "CPU"

        /**
         * Define a threshold for the detection score. Any detection with a score below that threshold will be ignored. 0.f being poor confidence and 1.f excellent confidence.
         * JSON name: "detect_minscore"
         * Default: 0.3f
         * type: float
         * pattern: ]0.f, 1.f]
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#detect-minscore
         */
        const val CONFIG_DETECT_MINSCORE = 0.1 // 10%


        /**
         * Whether to return cars with no plate. By default any car without plate will be silently ignored.
         * To filter false-positives: https://www.doubango.org/SDKs/anpr/docs/Known_issues.html#false-positives-for-cars-with-no-plate
         * JSON name: "car_noplate_detect_enabled"
         * Default: false
         * type: bool
         * pattern: true | false
         * Available since: 3.2.0
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#car-noplate-detect-enabled
         */
        const val CONFIG_CAR_NOPLATE_DETECT_ENABLED = false

        /**
         * Defines a threshold for the detection score for cars with no plate. Any detection with a score below that threshold will be ignored. 0.f being poor confidence and 1.f excellent confidence.
         * JSON name: "car_noplate_detect_min_score",
         * Default: 0.8f
         * type: float
         * pattern: [0.f, 1.f]
         * Available since: 3.2.0
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#car-noplate-detect-min-score
         */
        const val CONFIG_CAR_NOPLATE_DETECT_MINSCORE = 0.8 // 80%

        /**
         * Whether to enable pyramidal search. Pyramidal search is an advanced feature to accurately detect very small or far away license plates.
         * JSON name: "pyramidal_search_enabled"
         * Default: true
         * type: bool
         * pattern: true | false
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#pyramidal_search_enabled
         */
        const val CONFIG_PYRAMIDAL_SEARCH_ENABLED = true

        /**
         * Defines how sensitive the pyramidal search anchor resolution function should be. The higher this value is, the higher the number of pyramid levels will be.
         * More levels means better accuracy but higher CPU usage and inference time.
         * Pyramidal search will be disabled if this value is equal to 0.
         * JSON name: "pyramidal_search_sensitivity"
         * Default: 0.28f
         * type: float
         * pattern: [0.f, 1.f]
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#pyramidal_search_sensitivity
         */
        const val CONFIG_PYRAMIDAL_SEARCH_SENSITIVITY = 0.28 // 28%

        /**
         * Defines a threshold for the detection score associated to the plates retrieved after pyramidal search.
         * Any detection with a score below that threshold will be ignored.
         * 0.f being poor confidence and 1.f excellent confidence.
         * JSON name: "pyramidal_search_minscore"
         * Default: 0.3f
         * type: float
         * pattern: ]0.f, 1.f]
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#pyramidal_search_minscore
         */
        const val CONFIG_PYRAMIDAL_SEARCH_MINSCORE = 0.5 // 50%

        /**
         * Minimum image size (max[width, height]) in pixels to trigger pyramidal search.
         * Pyramidal search will be disabled if the image size is less than this value. Using pyramidal search on small images is useless.
         * JSON name: "pyramidal_search_min_image_size_inpixels"
         * Default: 800
         * type: integer
         * pattern: [0, inf]
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#pyramidal_search_min_image_size_inpixels
         */
        const val CONFIG_PYRAMIDAL_SEARCH_MIN_IMAGE_SIZE_INPIXELS = 800 // pixels

        /**
         * Whether to enable License Plate Country Identification (LPCI) function (https://www.doubango.org/SDKs/anpr/docs/Features.html#license-plate-country-identification-lpci).
         * To avoid adding latency to the pipeline only enable this function if you really need it.
         * JSON name: "klass_lpci_enabled"
         * Default: false
         * type: bool
         * pattern: true | false
         * Available since: 3.0.0
         * More info at https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#klass-lpci-enabled
         */
        const val CONFIG_KLASS_LPCI_ENABLED = true

        /**
         * Whether to enable Vehicle Color Recognition (VCR) function (https://www.doubango.org/SDKs/anpr/docs/Features.html#vehicle-color-recognition-vcr).
         * To avoid adding latency to the pipeline only enable this function if you really need it.
         * JSON name: "klass_vcr_enabled"
         * Default: false
         * type: bool
         * pattern: true | false
         * Available since: 3.0.0
         * More info at https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#klass-vcr-enabled
         */
        const val CONFIG_KLASS_VCR_ENABLED = true

        /**
         * Whether to enable Vehicle Make Model Recognition (VMMR) function (https://www.doubango.org/SDKs/anpr/docs/Features.html#vehicle-make-model-recognition-vmmr).
         * To avoid adding latency to the pipeline only enable this function if you really need it.
         * JSON name: "klass_vmmr_enabled"
         * Default: false
         * type: bool
         * pattern: true | false
         * More info at https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#klass-vmmr-enabled
         */
        const val CONFIG_KLASS_VMMR_ENABLED = true

        /**
         * Whether to enable Vehicle Body Style Recognition (VBSR) function (https://www.doubango.org/SDKs/anpr/docs/Features.html#features-vehiclebodystylerecognition).
         * To avoid adding latency to the pipeline only enable this function if you really need it.
         * JSON name: "klass_vbsr_enabled"
         * Default: false
         * type: bool
         * pattern: true | false
         * Available since: 3.2.0
         * More info at https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#klass-vbsr-enabled
         */
        const val CONFIG_KLASS_VBSR_ENABLED = false

        /**
         * 1/G coefficient value to use for gamma correction operation in order to enhance the car color before applying VCR classification.
         * More information on gamma correction could be found at https://en.wikipedia.org/wiki/Gamma_correction.
         * Values higher than 1.0f mean lighter and lower than 1.0f mean darker. Value equal to 1.0f mean bypass gamma correction operation.
         * This parameter in action: https://www.doubango.org/SDKs/anpr/docs/Improving_the_accuracy.html#gamma-correction
         * * JSON name: "recogn_minscore"
         * Default: 1.5
         * type: float
         * pattern: [0.f, inf[
         * Available since: 3.0.0
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#klass-vcr-gamma
         */
        const val CONFIG_KLASS_VCR_GAMMA = 1.5

        /**
         * Define a threshold for the overall recognition score. Any recognition with a score below that threshold will be ignored.
         * The overall score is computed based on "recogn_score_type". 0.f being poor confidence and 1.f excellent confidence.
         * JSON name: "recogn_minscore"
         * Default: 0.3f
         * type: float
         * pattern: ]0.f, 1.f]
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#recogn-minscore
         */
        const val CONFIG_RECOGN_MINSCORE = 0.4 // 40%

        /**
         * Defines the overall score type. The recognizer outputs a recognition score ([0.f, 1.f]) for every character in the license plate.
         * The score type defines how to compute the overall score.
         * - "min": Takes the minimum score.
         * - "mean": Takes the average score.
         * - "median": Takes the median score.
         * - "max": Takes the maximum score.
         * - "minmax": Takes (max + min) * 0.5f.
         * The "min" score is the more robust type as it ensure that every character have at least a certain confidence value.
         * The median score is the default type as it provide a higher recall. In production we recommend using min type.
         * JSON name: "recogn_score_type"
         * Default: "median"
         * Recommended: "min"
         * type: string
         * More info: https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#recogn-score-type
         */
        const val CONFIG_RECOGN_SCORE_TYPE = "min"

        /**
         * Whether to add rectification layer between the detector's output and the recognizer's input. A rectification layer is used to suppress the distortion.
         * A plate is distorted when it's skewed and/or slanted. The rectification layer will deslant and deskew the plate to make it straight which makes the recognition more accurate.
         * Please note that you only need to enable this feature when the license plates are highly distorted. The implementation can handle moderate distortion without a rectification layer.
         * The rectification layer adds many CPU intensive operations to the pipeline which decrease the frame rate.
         * More info on the rectification layer could be found at https://www.doubango.org/SDKs/anpr/docs/Rectification_layer.html#rectificationlayer
         * JSON name: "recogn_rectify_enabled"
         * Default: false
         * Recommended: false
         * type: string
         * More info at https://www.doubango.org/SDKs/anpr/docs/Configuration_options.html#recogn-rectify-enabled
         */
        const val CONFIG_RECOGN_RECTIFY_ENABLED = true

        fun startActivity(
            activity: Activity,
            isFlashLightRequired : Boolean,
            officerName: String,
            badgeId: String,
            squad: String,
            beat: String,
            block: String,
            street: String,
            sideID: String,
            side: String,
            timeLimitText: String,
            timeLimitValue: String,
            vehicleRunMethod : String
        ) {
            val intent = Intent(
                activity,
                AlprVideoParallelActivity::class.java
            )
            intent.putExtra(KEY_FROM, FROM_CONTINUOUS_VEHICLE_MODE)
            intent.putExtra(KEY_IS_VIDEO_STAB, true)
            intent.putExtra(KEY_IS_LENS_STAB, true)
            intent.putExtra(KEY_IS_FLASH_REQUIRED, isFlashLightRequired)
            intent.putExtra(KEY_OFFICER_NAME, officerName)
            intent.putExtra(KEY_BADGE_ID, badgeId)
            intent.putExtra(KEY_SQUAD, squad)
            intent.putExtra(KEY_BEAT, beat)
            intent.putExtra(KEY_BLOCK, block)
            intent.putExtra(KEY_STREET, street)
            intent.putExtra(KEY_SIDE_ID, sideID)
            intent.putExtra(KEY_SIDE, side)
            intent.putExtra(KEY_TIME_LIMIT_TEXT, timeLimitText)
            intent.putExtra(KEY_TIME_LIMIT_VALUE, timeLimitValue)
            intent.putExtra(KEY_VEHICLE_RUN_METHOD, vehicleRunMethod)
            activity.startActivity(intent)
            //activity.finish()
        }
    }
}