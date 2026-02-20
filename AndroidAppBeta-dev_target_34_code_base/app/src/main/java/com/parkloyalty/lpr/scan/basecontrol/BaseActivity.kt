package com.parkloyalty.lpr.scan.basecontrol

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.display.DisplayManager
import android.location.Location
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.*
import android.os.strictmode.Violation
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.circularreveal.CircularRevealRelativeLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.common.OnFragmentInteractionListener
import com.parkloyalty.lpr.scan.common.model.*
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databasetable.ActivityImageTable
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.dialog.ViewDialog
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.intToBool
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toBooleanFromYesNo
import com.parkloyalty.lpr.scan.interfaces.*
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_API_PAYLOAD
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_HEADER_FOOTER_IN_FACSIMILE
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_INVENTORY_MODULE
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.UNUPLOAD_IMAGE_TYPE_FACSIMILE
import com.parkloyalty.lpr.scan.locationservice.BackgroundLocationUpdateService
import com.parkloyalty.lpr.scan.network.*
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.LogoutNoteForEquipmentRequest
import com.parkloyalty.lpr.scan.qrcode.GenerateQRCodeActivity
import com.parkloyalty.lpr.scan.qrcode.QRCodeScanner
import com.parkloyalty.lpr.scan.qrcode.model.InventoryViewModel
import com.parkloyalty.lpr.scan.ui.allreport.AllReportActivity
import com.parkloyalty.lpr.scan.ui.brokenmeter.BrokenMeterActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetails2Activity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetailsActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.LprHitsActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.MyActivityActivity
import com.parkloyalty.lpr.scan.ui.check_setup.license.LprScanActivity
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.*
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.continuousmode.ContinuousModeActivity
import com.parkloyalty.lpr.scan.ui.dashboard.DashboardActivity
import com.parkloyalty.lpr.scan.ui.dashboard.SettingActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.CameraGuidedEnforcementActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.CameraViolationActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.DirectedEnforcementActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.GuideEnforecementActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.PayBySpaceActivity
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.*
import com.parkloyalty.lpr.scan.ui.login.LoginActivity
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.ui.login.model.ActivityLogImageResponse
import com.parkloyalty.lpr.scan.ui.login.model.ActivityLogViewModel
import com.parkloyalty.lpr.scan.ui.login.model.ActivityUpdateRequest
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.municipalcitation.MunicipalCitationDetailsActivity
import com.parkloyalty.lpr.scan.ui.officerdailysummary.OfficerDailySummaryActivity
import com.parkloyalty.lpr.scan.ui.honorbill.HonorBillActivity
import com.parkloyalty.lpr.scan.ui.printer.PrinterType
import com.parkloyalty.lpr.scan.ui.supervisor.SupervisorActivity
import com.parkloyalty.lpr.scan.ui.ticket.LprContinuousResultActivity
import com.parkloyalty.lpr.scan.ui.ticket.SearchActivity
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageViewModel
import com.parkloyalty.lpr.scan.ui.ticket.model.AddNotesResponse
import com.parkloyalty.lpr.scan.ui.uploadfacsimile.MissingFacsimileActivity
import com.parkloyalty.lpr.scan.ui.xfprinter.XfPrinterUseCase
import com.parkloyalty.lpr.scan.util.*
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.announceForAccessibility
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.getMyDatabase
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.mQrCodeSizeForCommandFacsimile
import com.parkloyalty.lpr.scan.util.LogUtil.isInvestigateAppPerformance
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.util.app_performance.PerformanceMonitor
import com.parkloyalty.lpr.scan.util.app_performance.ViolationTracker
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.vehiclestickerscan.VehicleStickerScanActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.getValue

/* base activity for all activity
* please use this naming convention
*
 public static final int SOME_CONSTANT = 42;
    public int publicField;
    private static MyClass sSingleton;
    int mPackagePrivate;
    private int mPrivate;
    protected int mProtected;
    boolean isBoolean;
    boolean hasBoolean;
    View mMyView;
*
*
* */
@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity(), ServiceCallBackInterface, LifecycleObserver,
    OnFragmentInteractionListener, CustomDialogHelper {
    private val mWindow: Window? = null
    private var customAnimationUtil: CustomAnimationUtil? = null
    private var viewDialog: ViewDialog? = null
    private var mBound = false
    private var mPositive = false
    private var drawerLy: DrawerLayout? = null
    private var myReceiver: MyReceiver? = null
    var location: Location? = null
    var mEventStartTimeStamp: String? = null
    private var mDb: AppDatabase? = null
    private var mTimeStatus = false
    private var mHandler = Handler(Looper.getMainLooper())
    private var context: Context? = null
    private var mActivityLoggerAPITag = ""
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var resultFacsimileImage: UnUploadFacsimileImage? = null
    private var mFacsimileImagesLink: MutableList<String> = ArrayList()

    @Inject
    lateinit var sharedPreference: SharedPref

    private var whichImageUploadCall: String = "citation"

    private val activityImageUpdateRequest = ActivityImageUploadRequest()
    private val updateActivityImagePayLoad = UpdatePayload()
    private lateinit var mActivityImage: ActivityImageTable
    private var mActivityImageCount: Int? = 0
    private var mAndroidId: String? = ""

    //This flag is used to show hide inventory module in the app
    var showAndEnableInventoryModule: Boolean = false

    var showAndEnableHeaderFooterInFacsimile: Boolean = false

    var mQrCodeSizeForCommandFacsimile = 0

    var showAndEnableScanVehicleStickerModule: Boolean = false

    var showAndEnableDirectedEnforcementModule: Boolean = false

    var showAndEnableCameraFeedUnderGuideEnforecement: Boolean = false

    var showAndEnableCameraGuidedEnforcementModule: Boolean = false//leaven worth


    //This flag is used to show hide cross clear icon with input field
    //private var isEnableCrossClearButton : Boolean = true


    private val mPushEventViewModel: PushEventViewModel? by viewModels()
    private val mEventLoggerViewModel: EventLoggerViewModel? by viewModels()
    private val mInactiveMeterBuzzerViewModel: InactiveMeterBuzzerViewModel? by viewModels()
    private val mActivityLogViewModel: ActivityLogViewModel? by viewModels()
    private val addFacsimileImageViewModel: AddImageViewModel? by viewModels()
    private val mFacsimileUploadLinkImageViewModel: UploadImageViewModel? by viewModels()
    private val mUploadAPITextFileViewModel: UploadImageViewModel? by viewModels()
    private val mUploadActivityImage: ActivityImageViewModel? by viewModels()
    private val inventoryViewModel: InventoryViewModel? by viewModels()

    val ioScope = CoroutineScope(
        Job() + Dispatchers.IO
    )

    val mainScope = CoroutineScope(
        Job() + Dispatchers.Main
    )

    private val mRunnableTask: Runnable = object : Runnable {
        override fun run() {
            mTimeStatus = true
            // this will repeat this task again at specified time interval
            sharedPreference.write(SharedPrefKey.PRE_LAT, 0.0.toString())
            sharedPreference.write(SharedPrefKey.PRE_LONG, 0.0.toString())
            mHandler.postDelayed(this, Constants.INTERVAL)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isInvestigateAppPerformance){
            //Start Of App Performance Monitoring
            activityStartTime = System.currentTimeMillis()
            activityCount.incrementAndGet()

            // Initialize performance monitoring only once
            if (BuildConfig.DEBUG && !isInitialized) {
                initializePerformanceMonitoring()
                isInitialized = true
            }


            // Log activity creation
            logActivityEvent("CREATED")
            performanceMonitor?.logMemoryUsage("${getActivityName()} Created")
            //End Of App Performance Monitoring
        }

        context = this

        addObservers()
        myReceiver = getMyReceiver()
        mDb = BaseApplication.instance?.getAppDatabase()

        // Call this to start the task first time
        mHandler.postDelayed(mRunnableTask, 0)

        uploadMissingFacsimileImages()

        getSettingFileValues()

        try {
            mAndroidId = AppUtils.getDeviceId(this)

//-----------------------bottom Navigation Bar-----

            /*window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)

            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
                    startLockTask() // Enters screen pinning mode
                }
            }

            WindowCompat.setDecorFitsSystemWindows(window, false)

            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars()) // hides nav + status bar
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupStrictMode() {
        Log.i(TAG, "Initializing StrictMode for performance monitoring")

        // Thread Policy - detects main thread violations
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectCustomSlowCalls()
                .detectResourceMismatches()
                .penaltyLog()
                .penaltyFlashScreen() // Visual indicator
                // .penaltyDialog() // Uncomment if you want dialog boxes
                .build()
        )

        // VM Policy - detects memory leaks and resource issues
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects()
                .detectActivityLeaks()
                .detectCleartextNetwork()
                .detectContentUriWithoutPermission()
                .detectFileUriExposure()
                .penaltyLog()
                .penaltyDropBox()
                .build()
        )

        Log.i(TAG, "StrictMode initialized - violations will appear in Logcat")
    }

    override fun onStop() {
        super.onStop()
        if (isInvestigateAppPerformance){
            logActivityEvent("STOPPED")
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializePerformanceMonitoring() {
        Log.i(TAG, "🚀 Initializing complete performance monitoring system")

        // Initialize components
        performanceMonitor = PerformanceMonitor()
        violationTracker = ViolationTracker()

        // Setup StrictMode with comprehensive monitoring
        setupComprehensiveStrictMode()

        // Start monitoring
        performanceMonitor?.startMonitoring()

        Log.i(TAG, "✅ Performance monitoring system initialized")
        Log.i(TAG, "📊 Check Logcat with filters: tag:BaseActivity OR tag:StrictMode OR tag:PerformanceMonitor")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupComprehensiveStrictMode() {
        val violationExecutor = Executors.newSingleThreadExecutor()


        // Thread Policy - Detect main thread violations
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()                    // Detect all thread violations
                .penaltyListener(violationExecutor) { violation ->
                    handleViolation("THREAD", violation)
                }
                .penaltyLog()                   // Log to Logcat
                .penaltyFlashScreen()          // Visual red flash
                .build()
        )

        // VM Policy - Detect memory leaks and resource violations
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()                    // Detect all VM violations
                .penaltyListener(violationExecutor) { violation ->
                    handleViolation("VM", violation)
                }
                .penaltyLog()                   // Log to Logcat
                .penaltyDropBox()              // Send to system DropBox
                .build()
        )

        Log.i(TAG, "🔍 StrictMode enabled - violations will flash screen red and appear in Logcat")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun handleViolation(type: String, violation: Violation) {
        totalViolations.incrementAndGet()

        Log.e(TAG, "🚨 =============== STRICTMODE $type VIOLATION ===============")
        Log.e(TAG, "🏠 Activity: ${getActivityName()}")
        Log.e(TAG, "⚠️  Violation: ${violation.javaClass.simpleName}")
        Log.e(TAG, "💬 Message: ${violation.message}")
        Log.e(TAG, "🧵 Thread: ${Thread.currentThread().name}")
        Log.e(TAG, "⏰ Time: ${Date()}")
        Log.e(TAG, "📊 Total violations so far: ${totalViolations.get()}")
        Log.e(TAG, "📱 Active activities: ${activityCount.get()}")
        Log.e(TAG, "🔥 ===================================================")

        // Track violation
        violationTracker?.recordViolation(type, violation, getActivityName())

        // Log memory state during violation
        performanceMonitor?.logMemoryUsage("During $type Violation in ${getActivityName()}")

        // Save violation to file for later analysis
        saveViolationToFile(type, violation)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun saveViolationToFile(type: String, violation: Violation) {
        Thread {
            try {
                val file = File(getExternalFilesDir(null), "performance_violations.log")
                val logEntry = """
                    ========================================
                    VIOLATION: $type
                    TIME: ${Date()}
                    ACTIVITY: ${getActivityName()}
                    THREAD: ${Thread.currentThread().name}
                    TYPE: ${violation.javaClass.simpleName}
                    MESSAGE: ${violation.message}
                    STACK TRACE:
                    ${violation.stackTraceToString()}
                    ========================================
                    
                """.trimIndent()

                file.appendText(logEntry)
                Log.d(TAG, "📝 Violation saved to: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to save violation to file", e)
            }
        }.start()
    }

    private fun logActivityEvent(event: String) {
        Log.d(TAG, "🔄 ${getActivityName()} - $event (Active: ${activityCount.get()}, Violations: ${totalViolations.get()})")
    }

    private fun logPerformanceSummary() {
        val summary = """
            📊 PERFORMANCE SUMMARY for ${getActivityName()}:
            ⏱️  Activity lifetime: ${System.currentTimeMillis() - activityStartTime}ms
            🏠 Active activities: ${activityCount.get()}
            🚨 Total violations: ${totalViolations.get()}
            ${violationTracker?.getSummary() ?: "No violation data"}
            ${performanceMonitor?.getMemorySummary() ?: "No memory data"}
        """.trimIndent()

        Log.i(TAG, summary)
    }

    private fun getActivityName(): String = this::class.java.simpleName

    // Public methods for manual performance checks
    protected fun logMemorySnapshot(tag: String = "Manual Check") {
        performanceMonitor?.logMemoryUsage("$tag in ${getActivityName()}")
    }

    protected fun getPerformanceReport(): String {
        return """
            Performance Report for ${getActivityName()}:
            ${performanceMonitor?.getMemorySummary() ?: "No memory data"}
            ${violationTracker?.getSummary() ?: "No violation data"}
        """.trimIndent()
    }

    protected fun forceGarbageCollection() {
        Log.d(TAG, "🗑️ Forcing garbage collection in ${getActivityName()}")
        System.gc()
        performanceMonitor?.logMemoryUsage("After Manual GC")
    }

    /**
     * Function used to fetch setting files values
     */
    fun getSettingFileValues() {
        val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

        if (settingsList != null) {
            showAndEnableInventoryModule = settingsList?.firstOrNull {
                it.type.equals(
                    SETTINGS_FLAG_INVENTORY_MODULE,
                    true
                ) && it.mValue.toBooleanFromYesNo()
            }?.mValue.toBooleanFromYesNo()

            showAndEnableHeaderFooterInFacsimile = settingsList?.firstOrNull {
                it.type?.trim().equals(
                    SETTINGS_FLAG_HEADER_FOOTER_IN_FACSIMILE,
                    true
                ) && it.mValue?.trim().toBooleanFromYesNo()
            }?.mValue?.trim().toBooleanFromYesNo()


            mQrCodeSizeForCommandFacsimile = settingsList?.firstOrNull {
                it.type?.trim().equals(
                    Constants.SETTINGS_FLAG_QR_CODE_SIZE,
                    true
                )
            }?.mValue.nullSafety("0").toInt()

            showAndEnableScanVehicleStickerModule = settingsList?.firstOrNull {
                it.type.equals(
                    Constants.SETTINGS_FLAG_SCAN_VEHICLE_REGISTRATION_STICKER,
                    true
                ) && it.mValue.toBooleanFromYesNo()
            }?.mValue.toBooleanFromYesNo()

            showAndEnableDirectedEnforcementModule = settingsList?.firstOrNull {
                it.type.equals(
                    Constants.SETTINGS_FLAG_DIRECTED_ENFORCEMENT_MODULE,
                    true
                ) && it.mValue.toBooleanFromYesNo()
            }?.mValue.toBooleanFromYesNo()
//            showAndEnableDirectedEnforcementModule = false

            showAndEnableCameraFeedUnderGuideEnforecement = settingsList?.firstOrNull {
                it.type.equals(
                    Constants.SETTINGS_FLAG_CAMERA_FEED_GUIDE_ENFORCEMENT,
                    true
                ) && it.mValue.toBooleanFromYesNo()
            }?.mValue.toBooleanFromYesNo()

            showAndEnableCameraGuidedEnforcementModule = settingsList?.firstOrNull {
                it.type.equals(
                    Constants.SETTINGS_FLAG_CAMERA_GUIDE_ENFORCEMENT,//leaven worth
                    true
                ) && it.mValue.toBooleanFromYesNo()
            }?.mValue.toBooleanFromYesNo()

        }
//        isEnableCrossClearButton = settingsList?.firstOrNull {
//            it.type.equals(
//                SETTINGS_FLAG_SHOW_CLEAR_ICON_FOR_INPUT_FIELDS,
//                true
//            ) && it.mValue.toBooleanFromYesNo()
//        }?.mValue.toBooleanFromYesNo()
//
//        isEnableCrossClearButton = false
    }

    /**
     * This function will check mDb variable &
     * if it is null then it will initialize it & then return
     * else it will return initialized variable
     */
    fun getMyDatabase(): AppDatabase? {
        if (mDb == null) {
            mDb = BaseApplication.instance?.getAppDatabase()
        }

        return mDb
    }

    protected fun setFullScreenUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    //protected void addObservers() {}
    protected open fun removeObservers() {}

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {

    }

    /**
     * Receiver for broadcasts sent by [BackgroundLocationUpdateService].
     */
    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            location = intent.getParcelableExtra(BackgroundLocationUpdateService.EXTRA_LOCATION)
            if (location != null) {
                var mZone: String? = "CST"
//                try {
//                    val model = mDb?.dbDAO?.getDataset()
//                    mZone = "CST"
//                    if (model != null && model.dataset?.settingsList != null) {
//                        mZone = model.dataset?.settingsList?.get(0)?.mValue
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
                LogUtil.printLog("lat base", location?.latitude.toString() + "")
                LogUtil.printLog("long base", location?.longitude.toString() + "")
                sharedPreference.write(SharedPrefKey.LAT, location?.latitude.toString())
                sharedPreference.write(SharedPrefKey.LONG, location?.longitude.toString())
                //user event logging - update Location every 5 min
                //callPushEventLogin(LOCATION_UPDATE, AppUtils.getDateTime());
                val mLocationUpdateRequest = LocUpdateRequest()
                mLocationUpdateRequest.activityType = Constants.ACTIVITY_TYPE_LOCATION_UPDATE
                mLocationUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
                mLocationUpdateRequest.locationUpdateType = Constants.LOCATION_UPDATE_TYPE_REGULAR
                mLocationUpdateRequest.latitude = location?.latitude
                mLocationUpdateRequest.longitude = location?.longitude
//                val timeMilli1 = AppUtils.getClientTimeStamp(mZone)
                //                mLocationUpdateRequest.setClientTimestamp(timeMilli1);
                try {
                    mLocationUpdateRequest.clientTimestamp = AppUtils.splitDateLpr(mZone)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                if (mTimeStatus) {
                    mTimeStatus = false
                    val mLat = sharedPreference.read(SharedPrefKey.PRE_LAT, "0.0")?.toDouble()
                    val mLong = sharedPreference.read(SharedPrefKey.PRE_LONG, "0.0")?.toDouble()
                    if (mLat != location?.latitude && mLong != location?.longitude || mLat == 0.0 && mLong == 0.0) {
                        sharedPreference.write(SharedPrefKey.PRE_LAT, location?.latitude.toString())
                        sharedPreference.write(
                            SharedPrefKey.PRE_LONG,
                            location?.longitude.toString()
                        )
                        /* LogsFile.writeFileOnInternalStorage(
                                this@BaseActivity,
                                "/n/n-----------------------------------------/n/n"
                        )
                        LogsFile.writeFileOnInternalStorage(this@BaseActivity, "Loation API")*/

                        //runOnUiThread {
                        callLocationApi(mLocationUpdateRequest)

                        isMeterActivteAPI()

                        LogUtil.printLog(
                            "location_base",
                            location?.latitude.toString() + " " + location?.longitude
                        )
                        //}

                        //LogUtil.printLog(" base", location.getLongitude() + "");
                    }
                }
            }

        }
    }

    fun isMeterActivteAPI() {
        try {
            if (getMeterBuzzerAPICallStatusFormSettingFile()) {
                val inactiveMeterBuzzerRequest = InactiveMeterBuzzerRequest()
                inactiveMeterBuzzerRequest.latitude = location?.latitude.toString()
                inactiveMeterBuzzerRequest.longitude =
                    location?.longitude.toString()
                mInactiveMeterBuzzerViewModel!!.hitInactiveMeterBuzzerApi(
                    inactiveMeterBuzzerRequest
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addObservers() {
        mPushEventViewModel?.response?.observe(this) { apiResponse: ApiResponse ->
            consumeResponseBase(apiResponse, DynamicAPIPath.POST_PUSH_EVENT)
        }

        mUploadActivityImage?.response?.observe(this) { apiResponse: ApiResponse ->
            consumeResponseBase(apiResponse, DynamicAPIPath.POST_UPDATE_ACTIVITY_IMAGE)
        }

        mInactiveMeterBuzzerViewModel?.response?.observe(this) { apiResponse: ApiResponse ->
            consumeResponseBase(apiResponse, DynamicAPIPath.POST_INACTIVE_METER_BUZZER)
        }

        mActivityLogViewModel?.response?.observe(this) { apiResponse: ApiResponse ->
            consumeResponseBase(apiResponse, DynamicAPIPath.POST_ACTIVITY_LOG)
        }

        mFacsimileUploadLinkImageViewModel?.responseBaseActivity?.observe(this) { apiResponse: ApiResponse ->
            consumeResponseBase(apiResponse, "IMAGE_UPLOADED_BASEACTIVITY")
        }

        mUploadAPITextFileViewModel?.responseTextFile?.observe(this) { apiResponse: ApiResponse ->
            consumeResponseBase(apiResponse, "TEXT_FILE_UPLOAD")
        }

        addFacsimileImageViewModel?.responseBaseActivityActivity?.observe(this) { apiResponse: ApiResponse ->
            consumeResponseBase(apiResponse, "IMAGE_LINK_UPLOADED")
        }

        inventoryViewModel?.responseForAddNoteForNotCheckedInEquipment?.observe(this) { apiResponse: ApiResponse ->
            consumeResponseBase(
                apiResponse, API_CONSTANT_ADD_LOG_FOR_LOGOUT
            )
        }
    }


    /* Call Api For Location event */
    fun callPushEventApi(mPushEventRequest: PushEventRequest?) {
        if (NetworkCheck.isInternetAvailable(this)) {
            mPushEventViewModel?.hitPushEventApi(mPushEventRequest)
        } else {
            LogUtil.printToastMSGForErrorWarning(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For Location event */
    fun callLocationApi(mPushEventRequest: LocUpdateRequest) {
        if (NetworkCheck.isInternetAvailable(this)) {
            try {

//                mPushEventRequest.setmShift(mWelcomeFormData!=null?mWelcomeFormData.getShift():"");
//                if(mPushEventRequest.getmShift().isEmpty()){
                mPushEventRequest.mShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                val welcomeForm: WelcomeForm? = mDb!!.dbDAO!!.getWelcomeForm()
                mPushEventRequest.mDeviceId = welcomeForm!!.officerDeviceName
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            LogsFile.writeFileOnInternalStorage(this@BaseActivity, mPushEventRequest.toString())
            mEventLoggerViewModel?.hitLocationUpdateApi(mPushEventRequest)
        } else {
            LogUtil.printToastMSGForErrorWarning(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For Login and logout event */
    fun callPushEventLogin(screenType: String?, mEventStartTimeStamp: String?) {
        try {
            //user event logging
//            WelcomeForm mWelcomeForm = mDb.getDbDAO().getWelcomeForm();
//            PushEventRequest mPushEventRequest = new PushEventRequest();
//            mPushEventRequest.setEventType(screenType);
//            PushEventMetadata mPushEventMetadata = new PushEventMetadata();
//            mPushEventMetadata.setData(screenType);
//            mPushEventMetadata.setEventStartTimestamp(mEventStartTimeStamp);
//            mPushEventMetadata.setEventFinishTimestamp(getDateTime());
//            mPushEventMetadata.setDescription(screenType);
//            PushEventInitiatorMetadata mData = new PushEventInitiatorMetadata();
//            if (mWelcomeForm != null) {
//                mData.setBadgeId(mWelcomeForm.getOfficerBadgeId());
//                mData.setBeat(mWelcomeForm.getOfficerBeat());
//                mData.setRadio(mWelcomeForm.getOfficerRadio());
//                mData.setSquad(mWelcomeForm.getOfficerSquad());
//                mData.setZone(mWelcomeForm.getOfficerZone()); //TODO change
//                mData.setOfficerId(mWelcomeForm.getSiteOfficerId());
//                mData.setDeviceId(AppUtils.getDeviceModel());
//                mData.setShift(mWelcomeForm.getOfficerShift());
//
//            } else {
//                mData.setBadgeId("Test");
//                mData.setBeat("Test");
//                mData.setRadio("Test");
//                mData.setSquad("Test");
//                mData.setZone("Test"); //TODO change
//                mData.setOfficerId("Test");
//                mData.setDeviceId("Test");
//                mData.setShift("Test");
//            }
//            mPushEventMetadata.setInitiatorMetadata(mData);
//            PushEventLocation mPushEventLocation = new PushEventLocation();
//            if (location != null) {
//                Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
//                List<Address> myList = myLocation.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                Address address = myList.get(0);
//                mPushEventLocation.setLat(String.valueOf(location.getLatitude()));
//                mPushEventLocation.setLng(String.valueOf(location.getLongitude()));
//                mPushEventLocation.setReverseGeoCodedAddress(address.getCountryName());
//                mPushEventRequest.setmEventLat(location.getLatitude());
//                mPushEventRequest.setmEventLng(location.getLongitude());
//            } else {
//                Double mLat = Double.parseDouble(sharedPreference.getInstance(getApplicationContext()).read(SharedPrefKey.LAT, "0.0"));
//                Double mLong = Double.parseDouble(sharedPreference.getInstance(getApplicationContext()).read(SharedPrefKey.LONG, "0.0"));
//                Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
//                List<Address> myList = myLocation.getFromLocation(mLat, mLong, 1);
//                Address address = myList.get(0);
//                mPushEventLocation.setLat(String.valueOf(mLat));
//                mPushEventLocation.setLng(String.valueOf(mLong));
//                mPushEventLocation.setReverseGeoCodedAddress(address.getCountryName());
//                mPushEventRequest.setmEventLat(mLat);
//                mPushEventRequest.setmEventLng(mLong);
//            }
//            mPushEventMetadata.setLocation(mPushEventLocation);
//            mPushEventRequest.setEventMetadata(mPushEventMetadata);
//            callPushEventApi(mPushEventRequest);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*Api response */
    private fun consumeResponseBase(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> {
                //We need to show loader only when we are calling API for adding a log before logout
                if (tag.equals(API_CONSTANT_ADD_LOG_FOR_LOGOUT, true)) {
                    showProgressLoader(getString(R.string.scr_message_please_wait))
                }
            }

            Status.SUCCESS ->                 //dismissLoader();
                if (!apiResponse.data!!.isNull) {

                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.POST_PUSH_EVENT, ignoreCase = true)) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), PushEventResponse::class.java)

                            if (responseModel != null && responseModel.status.nullSafety()) {
                                //LogUtil.printToastMSG(this, responseModel.getMessage());
                            } else {
                                //LogUtil.printToastMSG(this, responseModel.getMessage());
                            }
                        }
                        if (tag.equals(DynamicAPIPath.POST_EVENT_LOGGER, ignoreCase = true)) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LocationUpdateResponse::class.java)

                            if (responseModel != null && responseModel.success.nullSafety()) {
                                locationEventCallBack
                                //LogUtil.printToastMSG(this, "location working!");
                            } else {
                                locationEventCallBack
                                //LogUtil.printToastMSG(this, responseModel.getMessage());
                            }
                        }
                        if (tag.equals(DynamicAPIPath.POST_ACTIVITY_LOG, ignoreCase = true)) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LprScanLoggerResponse::class.java)

//                            dismissLoader()
                            if (responseModel != null && responseModel.success.nullSafety()) {
                                if (mActivityLoggerAPITag.equals("Logout", ignoreCase = true)) {
                                    logout(this@BaseActivity)
                                }
                            } else {
                                val message: String
                                message = responseModel.response.nullSafety()
                                //                                AppUtils.showCustomAlertDialog(BaseActivity.this, getString(R.string.scr_lbl_activity),
//                                        message, getString(R.string.alt_lbl_OK),
//                                        getString(R.string.scr_btn_cancel), BaseActivity.this);
                            }
                            if (LogUtil.isEnableGoogleAnalytics) {
                                val json = ObjectMapperProvider.toJson(responseModel)
//                                getInstanceOfAnalytics(
//                                    json,
//                                    "BASEACTIVITY",
//                                    "ACTIVITY LOGGER API",
//                                    "Response"
//                                )
                            }
                        }
                        if (tag.equals("IMAGE_UPLOADED_BASEACTIVITY", ignoreCase = true)) {
                            try {
                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status.nullSafety()) {
                                    if (whichImageUploadCall.equals("Citation")) {
                                        if (responseModel.data != null && responseModel.data?.size!! > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response?.links != null && responseModel.data!![0].response?.links?.size!! > 0) {
                                            mFacsimileImagesLink.add(responseModel.data!![0].response?.links!![0])
                                            callUploadImagesUrl()
                                        }
                                    } else if (whichImageUploadCall.equals("Activity")) {

                                        mActivityImageCount = mActivityImageCount!! - 1
                                        if (updateActivityImagePayLoad!!.image1!!.isEmpty()) updateActivityImagePayLoad!!.image1 =
                                            responseModel.data!![0].response?.links!![0]
                                        else if (updateActivityImagePayLoad!!.image2!!.isEmpty()) updateActivityImagePayLoad!!.image2 =
                                            responseModel.data!![0].response?.links!![0]
                                        else if (updateActivityImagePayLoad!!.image3!!.isEmpty()) updateActivityImagePayLoad!!.image3 =
                                            responseModel.data!![0].response?.links!![0]
                                        activityImageUpdateRequest!!.updatePayload =
                                            updateActivityImagePayLoad

                                        if (mActivityImageCount == 0) {
                                            callUploadActivityImagesUrl()
                                        }

                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (tag.equals("TEXT_FILE_UPLOAD", ignoreCase = true)) {
                            try {
                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TextFileResponse::class.java)

//                                try {
//                                    ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"---------BASE ACTIVITY API LOG Response--------")
//                                    ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"Response "+" :- "+responseModel)
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
                                if (responseModel != null && responseModel.status.nullSafety()) {
                                    try {
                                        val localFolder =
                                            File(
                                                Environment.getExternalStorageDirectory().absolutePath,
                                                Constants.FILE_NAME
                                            )

                                        val fileName1 =
                                            FILE_NAME_API_PAYLOAD + ".txt" //like 2016_01_12.txt
                                        val file = File(localFolder, fileName1)
                                        file.delete()

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (tag.equals("IMAGE_LINK_UPLOADED", ignoreCase = true)) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddNotesResponse::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
                                resultFacsimileImage?.dateTime?.let {
                                    getMyDatabase()?.dbDAO?.updateFacsimileStatus(
                                        1,
                                        resultFacsimileImage?.ticketNumberText.toString(),
                                        it
                                    )
                                }
//                                mDb!!.dbDAO!!.deleteFacsimileData(resultFacsimileImage!!.ticketNumber!!.toString())
//                                AppUtils.showCustomAlertDialog(this@BaseActivity,
//                                        APIConstant.UPLOAD_IMAGE,
//                                        if (responseModel.message != null) responseModel.message else getString(
//                                                R.string.err_msg_something_went_wrong
//                                        ),
//                                        "SONU", getString(R.string.scr_btn_cancel), this)
//                            } else {
                            }
                        }
                        if (tag.equals(
                                DynamicAPIPath.POST_INACTIVE_METER_BUZZER,
                                ignoreCase = true
                            )
                        ) {
//                            dismissLoader()
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), InactiveMeterBuzzerResponse::class.java)


                            if (responseModel != null && responseModel.status === true) {
                                if (responseModel!!.data!!.inactive == true) {
                                    sharedPreference.write(SharedPrefKey.IS_METER_ACTIVE, "ACTIVE")
                                    ToneGenerator(AudioManager.STREAM_MUSIC, 100)?.startTone(
                                        ToneGenerator.TONE_CDMA_HIGH_L,
                                        1000
                                    )
                                    val vibrator =
                                        context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    if (Build.VERSION.SDK_INT >= 26) {
                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(
                                                500,
                                                VibrationEffect.DEFAULT_AMPLITUDE
                                            )
                                        )
                                    } else {
                                        vibrator.vibrate(500)
                                    }
                                } else {
                                    sharedPreference.write(
                                        SharedPrefKey.IS_METER_ACTIVE,
                                        "DEACTIVE"
                                    )
                                }
                            } else {
                                sharedPreference.write(SharedPrefKey.IS_METER_ACTIVE, "DEACTIVE")
                            }
                        }
                        if (tag.equals(
                                DynamicAPIPath.POST_UPDATE_ACTIVITY_IMAGE,
                                ignoreCase = true
                            )
                        ) {
//                            dismissLoader()

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ActivityLogImageResponse::class.java)

                            if (responseModel != null && responseModel.status == true) {
//                                mActivityImage = mDb!!.dbDAO!!.getActivityImageData()
                                mActivityImage!!.activityResponseId?.let {
                                    mDb!!.dbDAO!!.deleteActivityImageData(
                                        it
                                    )
                                }
                            }
                        }

                        //This is to handle API response of Add Note/Log before logout,
                        //If we wanted to handle proper API response use following class LogoutNoteForEquipmentResponse
                        if (tag.equals(API_CONSTANT_ADD_LOG_FOR_LOGOUT, ignoreCase = true)) {
                            dismissLoader()
                        }
                    } catch (e: Exception) {
                        //logout();
//                        LogUtil.printLog("BaseActivity", apiResponse.data.toString())
                        if (tag.equals(DynamicAPIPath.POST_EVENT_LOGGER, ignoreCase = true)) {
                            locationEventCallBack
                        }
                        if (tag.equals(DynamicAPIPath.POST_ACTIVITY_LOG, ignoreCase = true)) {
                            if (mActivityLoggerAPITag.equals("Logout", ignoreCase = true)) {
                                logout(this@BaseActivity)
                            }
                        }
                        e.printStackTrace()

                    }
                }

            Status.ERROR -> {
                if (tag.equals(DynamicAPIPath.POST_EVENT_LOGGER, ignoreCase = true)) {
                    locationEventCallBack
                }
                if (tag.equals(DynamicAPIPath.POST_ACTIVITY_LOG, ignoreCase = true)) {
                    if (mActivityLoggerAPITag.equals("Logout", ignoreCase = true)) {
                        logout(this@BaseActivity)
                    }
                }
                LogUtil.printLog("BaseActivity", apiResponse.error.toString())
                LogsFile.writeFileOnInternalStorage(this, "Base Activity")
                LogsFile.writeFileOnInternalStorage(
                    this,
                    tag + "  ###  " + apiResponse.error?.message.toString()
                )
                LogUtil.printLog(
                    "Base Activity API ERROR",
                    tag.toString() + "  " + apiResponse?.error.toString()
                )
            }

            else -> {
            }
        }
    }

    /**
     * show error when edit text is empty
     *
     * @param textInputLayout
     * @param error
     */
    fun showError(textInputLayout: TextInputLayout, error: String?) {
        textInputLayout.error = error
        textInputLayout.requestFocus()
        makeMeShake(textInputLayout, 20, 5)
    }

    // Monitors the state of the connection to the service.
    var mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mBound = true
            isServiceActive(true, service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBound = false
            isServiceActive(false, null)
        }
    }

    fun isServiceActive(`is`: Boolean, service: IBinder?) {}
    override fun isServiceActiveCall(`is`: Boolean, service: IBinder?) {
//        if (BaseApplication.getInstance().isApplicationBackgruond || mBound) {
//            unbindService(mServiceConnection);
//            mBound = false;
//        }
    }

    fun setErrorMessage(
        inputLayouts: TextInputLayout,
        editText: AppCompatEditText,
        errorMsg: String?
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (editText.editableText.toString().trim().isEmpty()) {
                    inputLayouts.error = errorMsg
                    editText.requestFocus()
                    makeMeShake(editText, 20, 5)
                } else {
                    inputLayouts.isErrorEnabled = false
                }
            }
        })
    }

    /*
     * call this method to display full screen progress loader
     * */
    fun showProgressLoader(message: String?) {
        try {
            if (viewDialog == null) {
                viewDialog = ViewDialog(this)
            }
            viewDialog?.showDialog(message)
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }
    }

    /*
     * call this method to dismiss progress loader
     * */
    fun dismissLoader() {
        try {
            if (viewDialog != null) {
                viewDialog?.hideDialog()
            }
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }
        /*try {
            if (!this.isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /*launch screen*/
    fun launchScreenWithFlag(mContext: Context?, mActivity: Class<*>?) {
        val intent = Intent(mContext, mActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

    /*launch screen*/
    fun hotRestart(mContext: Context?, FromWhereClick: String) {
        val iSLoggedIn = sharedPreference.read(SharedPrefKey.IS_LOGGED_IN, false)
        if (iSLoggedIn.nullSafety()) {
            //finishAffinity()
            val mStartActivity = Intent(context, DashboardActivity::class.java)
            //mStartActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (FromWhereClick.equals("SLEEP_MODE", true) &&
                (context as BaseActivity).localClassName.equals(
                    "com.parkloyalty.lpr.scan.ui.ticket.TicketDetailsActivity",
                    true
                )
            ) {
                mStartActivity.putExtra("KEY_HOT_RESTART", "HOTRESTART");
            }
            val mPendingIntentId = 123456
            val mPendingIntent = PendingIntent.getActivity(
                context,
                mPendingIntentId,
                mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val mgr = context!!.getSystemService(ALARM_SERVICE) as AlarmManager
            mgr[AlarmManager.RTC, System.currentTimeMillis()] = mPendingIntent
            //                System.exit(0);
            Runtime.getRuntime().exit(0)
        }
    }

    /*launch screen*/
    fun launchScreen(mContext: Context?, mActivity: Class<*>?) {
        val intent = Intent(mContext, mActivity)
        startActivity(intent)
    }

    fun launchScreen(mContext: Context?, mActivity: Class<*>?, text: String) {
        val intent = Intent(mContext, mActivity)
        intent.putExtra("from_scr", text)
        startActivity(intent)
    }
    /*launch screen*/
    fun launchScreenWithParam(mContext: Context?, mActivity: Class<*>?, fromScreen: String) {
        val intent = Intent(mContext, mActivity)
        intent.putExtra("from_scr", fromScreen)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    /*launch screen*/
    fun launchScreenWithFlagNewTask(mContext: Context?, mActivity: Class<*>?) {
        val intent = Intent(mContext, mActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    /*launch screen*/
    fun launchScreenLogin(mContext: Context?, mActivity: Class<*>?) {
        if (sharedPreference.read(SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB, false)
                .nullSafety()
        ) {
//            mDb.clearAllTables();
            LogUtil.printLog("Newuserlogin", "clear DB")
        }
        val intent = Intent(mContext, mActivity)
        startActivity(intent)
        finishAffinity()
    }

    /*set error in input field if invalid*/
    fun setError(textInputLayout: TextInputLayout, error: String?) {
        textInputLayout.error = error
        textInputLayout.requestFocus()
        if (customAnimationUtil == null) {
            customAnimationUtil = CustomAnimationUtil(this)
        }
        customAnimationUtil?.showErrorEditTextAnimation(textInputLayout, R.anim.shake)

        announceTextInputLayoutError(textInputLayout, error.nullSafety())
    }

    fun getmWindow(): Window? {
        return mWindow
    }

    fun setErrorMessageWithNoInputLayout(editText: AppCompatEditText, errorMsg: String?) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                isValidateFieldForNoInputLayout(editText, errorMsg)
                //                if (editText.getText().toString().trim().isEmpty()) {
//                    inputLayouts.setError(errorMsg);
//                    requestFocus(editText);
//                } else {
//                    inputLayouts.setErrorEnabled(false);
//                }
            }
        })
    }

    fun isValidateFieldForNoInputLayout(editText: AppCompatEditText?, errorMsg: String?): Boolean {
        return true
    }

    fun transperentStatusBar(context: Context?) {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            // edited here
            window.statusBarColor = Color.TRANSPARENT
        }


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun appInResumeState() {
//        Toast.makeText(this,"In Foreground",Toast.LENGTH_LONG).show();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun appInPauseState() {
//        Toast.makeText(this,"In Background",Toast.LENGTH_LONG).show();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun appInStopState() {
        try {
            if (BaseApplication.instance?.isApplicationBackgruond.nullSafety() || mBound) {
                unbindService(mServiceConnection)
                mBound = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * foreground service code
     */
    override fun onStart() {
        super.onStart()
        if (isInvestigateAppPerformance){
            logActivityEvent("STARTED")
        }

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            getMyReceiver(),
            IntentFilter(BackgroundLocationUpdateService.ACTION_BROADCAST)
        )
    }

    private fun getMyReceiver(): MyReceiver {
        if (myReceiver == null) {
            myReceiver = MyReceiver()
        }
        return myReceiver!!
    }

    private var mWakeLock: PowerManager.WakeLock? = null
    override fun onResume() {
        super.onResume()
        if (isInvestigateAppPerformance){
            logActivityEvent("RESUMED")
            performanceMonitor?.logMemoryUsage("${getActivityName()} Resumed")
        }

        LogsFile.writeFileOnInternalStorage(this, "BaseActivity Screen")
        //registerBroadcastReceiver()

        val pm = getSystemService(POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.canonicalName)
        mWakeLock!!.acquire()

    }

    override fun onPause() {
        super.onPause()
        if (isInvestigateAppPerformance){
            logActivityEvent("PAUSED")
            performanceMonitor?.logMemoryUsage("${getActivityName()} Paused")
        }

        //        LogUtil.printLog("IS_SCREEN_OFF","true"+ isScreenOn(this));
        if (isScreenOn(this)) {
            //LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
//            Toast.makeText(BaseActivity.this, "isScreenOn true", Toast.LENGTH_SHORT).show();
        } else {
//            sharedPreference.getInstance(BaseActivity.this).write(SharedPrefKey.IS_SCREEN_OFF, true);
//            LogUtil.printLog("IS_SCREEN_OFF","true");
//            Toast.makeText(BaseActivity.this, "isScreenOn false", Toast.LENGTH_SHORT).show();
        }
        //registerBroadcastReceiver()
        /*try {
            if (!isScreenOn(this)) {
                LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                        new IntentFilter(BackgroundLocationUpdateService.ACTION_BROADCAST));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    override fun onDestroy() {
//        Toast.makeText(this,"onDestroy",Toast.LENGTH_SHORT).show()
//        stopService(Intent(this, BackgroundLocationUpdateService::class.java))
        mHandler.removeCallbacks(mRunnableTask)
        dismissLoader()
        ioScope.cancel()
        mainScope.cancel()
        removeObservers()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(getMyReceiver())
        mWakeLock?.release()
        super.onDestroy()

        if (isInvestigateAppPerformance){
            val activityLifetime = System.currentTimeMillis() - activityStartTime
            activityCount.decrementAndGet()

            logActivityEvent("DESTROYED")
            performanceMonitor?.logMemoryUsage("${getActivityName()} Destroyed (lived ${activityLifetime}ms)")

            // Log performance summary
            logPerformanceSummary()
        }

        /*if (iSLoginLogged) {
            try {
                LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                        new IntentFilter(BackgroundLocationUpdateService.ACTION_BROADCAST));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    public fun registerBroadcastReceiver() {
        /* val theFilter = IntentFilter()
        */
        /** System Defined Broadcast  *//*
        theFilter.addAction(Intent.ACTION_SCREEN_ON)
        theFilter.addAction(Intent.ACTION_SCREEN_OFF)
        theFilter.addAction(Intent.ACTION_USER_PRESENT)
        val screenOnOffReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val strAction = intent.action
                val myKM = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                if (strAction == Intent.ACTION_USER_PRESENT || strAction == Intent.ACTION_SCREEN_OFF || strAction == Intent.ACTION_SCREEN_ON) if (myKM.inKeyguardRestrictedInputMode()) {
                    println("Screen off " + "LOCKED")
                } else {
                    println("Screen off " + "UNLOCKED")
                    */
        /**
         * WHen ofcer on in citation form and preview form then not reset app,
         *//*
                    */
        /**
         * WHen ofcer on in citation form and preview form then not reset app,
         *//*
                    try {
                        hotRestart(context, "SLEEP_MODE")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        applicationContext.registerReceiver(screenOnOffReceiver, theFilter)*/
    }

    /**
     * Is the screen of the device on.
     *
     * @param context the context
     * @return true when (at least one) screen is on
     */
    fun isScreenOn(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val dm = context.getSystemService(DISPLAY_SERVICE) as DisplayManager
            var screenOn = false
            for (display in dm.displays) {
                if (display.state != Display.STATE_OFF) {
                    screenOn = true
                }
            }
            screenOn
        } else {
            val pm = context.getSystemService(POWER_SERVICE) as PowerManager
            pm.isScreenOn
        }
    }

    //Toolbar navigation , val 1 for welcome screen dialog
    //val 2 lpr details screen back button to delete images
    fun initToolbar(
        `val`: Int,
        mContext: Context,
        layHomeId: Int,
        layTicketingId: Int,
        LayMyActivityId: Int,
        laySettingId: Int,
        layReportId: Int,
        LayLogoutId: Int,
        layDrawerId: Int,
        imgCloseId: Int,
        imgOpenId: Int,
        imgCrossId: Int,
        layCircularRevealRelativeLayoutId: Int,
        layIssueId: Int,
        layLookupId: Int,
        layScanId: Int,
        layMunicipalCitationId: Int,
        layGuideEnfId: Int,
        dailySummary: Int,
        layMyActivity: Int,
        LayMapView: Int,
        LayContinuousMode: Int,
        layCardGuide: Int,
        laypaybyplate: Int,
        laypaybyspace: Int,
        cardLookUp: Int,
        layCitationResult: Int,
        layLprResult: Int,
        layClearcache: Int,
        laySuperVisorView: Int,
        layAllReport: Int,
        layBrokenMeterReport: Int,
        layCurbReport: Int,
        layFullTimeReport: Int,
        layHandHeldMalfunctionReport: Int,
        laySignReport: Int,
        layVehicleInspectionReport: Int,
        lay72HourMarkedVehiclesReport: Int,
        layBikeInspectionReport: Int,
        cardAllReport: Int,
        layEOWSupervisorShiftReport: Int,
        layPartTimeReport: Int,
        layLprHits: Int,
        laySpecialAssignmentReport: Int,
        layQRCode: Int,
        cardQRCode: Int,
        layGenerateQRCode: Int,
        layScanQRCode: Int,
        laySunlight: Int,
        imgSunlight: Int,
        lay72hrNoticeToTowReport: Int,
        layTowReport: Int,
        laySignOffReport: Int,
        layNFL: Int,
        layHardSummer: Int,
        layAfterSeven: Int,
        layPayStationReport: Int,
        laySignageReport: Int,
        layHomelessReport: Int,
        laySafetyReport: Int,
        layTrashReport: Int,
        layLotCountVioRateReport: Int,
        layLotInspectionReport: Int,
        layWordOrderReport: Int,
        txtlogout: Int,
        laycameraviolation:Int,
        layScanStickerId:Int,
        layGeneticHitList:Int,
        layDirectedEnforcement:Int,
        layOwnerBill:Int
    ) {

        drawerLy = findViewById(layDrawerId)
        drawerLy?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//TODO ISSUE
        val imgClose = findViewById<AppCompatImageView>(imgCloseId)
        val imgOpen = findViewById<AppCompatImageView>(imgOpenId)
        val imgCross = findViewById<AppCompatImageView>(imgCrossId)
        val LayLogout = findViewById<LinearLayoutCompat>(LayLogoutId)
        val LayMyActivity = findViewById<LinearLayoutCompat>(LayMyActivityId)
        val layBack = findViewById<LinearLayoutCompat>(layHomeId)
        val layTicketing = findViewById<LinearLayoutCompat>(layTicketingId)
        val layIssue = findViewById<LinearLayoutCompat>(layIssueId)
        val layScan = findViewById<LinearLayoutCompat>(layScanId)
        val layScanSticker = findViewById<LinearLayoutCompat>(layScanStickerId)
        val layMunicipalCitation = findViewById<LinearLayoutCompat>(layMunicipalCitationId)
        val layLookup = findViewById<LinearLayoutCompat>(layLookupId)
        val layGuideEnf = findViewById<LinearLayoutCompat>(layGuideEnfId)
        val laySetting = findViewById<LinearLayoutCompat>(laySettingId)
        val layReport = findViewById<LinearLayoutCompat>(layReportId)
        val laySummary = findViewById<LinearLayoutCompat>(dailySummary)
        val layPayByPlate = findViewById<LinearLayoutCompat>(laypaybyplate)
        val layPayBySpace = findViewById<LinearLayoutCompat>(laypaybyspace)
        val layDirectedEnforcement = findViewById<LinearLayoutCompat>(layDirectedEnforcement)
        val layMapView = findViewById<LinearLayoutCompat>(LayMapView)
        val layContinuousMode = findViewById<LinearLayoutCompat>(LayContinuousMode)
        val layCitationResults = findViewById<LinearLayoutCompat>(layCitationResult)
        val layLprResults = findViewById<LinearLayoutCompat>(layLprResult)
        val layClearCache = findViewById<LinearLayoutCompat>(layClearcache)
        val laySuperVisorView = findViewById<LinearLayoutCompat>(laySuperVisorView)
        val layLprHits = findViewById<LinearLayoutCompat>(layLprHits)

        val layAllReport = findViewById<LinearLayoutCompat>(layAllReport)
        val layBrokenMeterReport = findViewById<LinearLayoutCompat>(layBrokenMeterReport)
        val layCurbReport = findViewById<LinearLayoutCompat>(layCurbReport)
        val layFullTimeReport = findViewById<LinearLayoutCompat>(layFullTimeReport)
        val layHandHeldMalfunctionReport =
            findViewById<LinearLayoutCompat>(layHandHeldMalfunctionReport)
        val laySignReport = findViewById<LinearLayoutCompat>(laySignReport)
        val layVehicleInspectionReport =
            findViewById<LinearLayoutCompat>(layVehicleInspectionReport)
        val lay72HourMarkedVehiclesReport =
            findViewById<LinearLayoutCompat>(lay72HourMarkedVehiclesReport)
        val layBikeInspectionReport = findViewById<LinearLayoutCompat>(layBikeInspectionReport)
        val cardAllReport = findViewById<CircularRevealRelativeLayout>(cardAllReport)
        val layEOWSupervisorShiftReport =
            findViewById<LinearLayoutCompat>(layEOWSupervisorShiftReport)
        val layPartTimeReport = findViewById<LinearLayoutCompat>(layPartTimeReport)
        val laySpecialAssignmentReport =
            findViewById<LinearLayoutCompat>(laySpecialAssignmentReport)


        val cardQRCode = findViewById<CircularRevealRelativeLayout>(cardQRCode)
        val layQRCode = findViewById<LinearLayoutCompat>(layQRCode)
        val layGenerateQRCode = findViewById<LinearLayoutCompat>(layGenerateQRCode)
        val layScanQRCode = findViewById<LinearLayoutCompat>(layScanQRCode)

        val laySunlight = findViewById<LinearLayoutCompat>(laySunlight)
        val imgSunlight = findViewById<AppCompatImageView>(imgSunlight)

        val lay72hrNoticeToTowReport = findViewById<LinearLayoutCompat>(lay72hrNoticeToTowReport)
        val laySignOffReport = findViewById<LinearLayoutCompat>(laySignOffReport)
        val layTowReport = findViewById<LinearLayoutCompat>(layTowReport)

        val layNFL = findViewById<LinearLayoutCompat>(layNFL)
        val layHardSummer = findViewById<LinearLayoutCompat>(layHardSummer)
        val layAfterSeven = findViewById<LinearLayoutCompat>(layAfterSeven)

        val layPayStationReport = findViewById<LinearLayoutCompat>(layPayStationReport)
        val laySignageReport = findViewById<LinearLayoutCompat>(laySignageReport)
        val layHomelessReport = findViewById<LinearLayoutCompat>(layHomelessReport)

        val laySafetyReport = findViewById<LinearLayoutCompat>(laySafetyReport)
        val layTrashReport = findViewById<LinearLayoutCompat>(layTrashReport)
        val layLotCountVioRateReport = findViewById<LinearLayoutCompat>(layLotCountVioRateReport)
        val layLotInspectionReport = findViewById<LinearLayoutCompat>(layLotInspectionReport)
        val layWordOrderReport = findViewById<LinearLayoutCompat>(layWordOrderReport)

        val txtlogout = findViewById<AppCompatTextView>(txtlogout)
        val laycameraviolation = findViewById<LinearLayoutCompat>(laycameraviolation)
        val laygeneticHitList = findViewById<LinearLayoutCompat>(layGeneticHitList)
        val layOwnerbill = findViewById<LinearLayoutCompat>(layOwnerBill)

//        CircularRevealRelativeLayout layCircularRevealRelativeLayout = findViewById(layCircularRevealRelativeLayoutId);
        val layCircularRevealRelativeLayout =
            findViewById<CircularRevealRelativeLayout>(layCircularRevealRelativeLayoutId)
        val layCircularRevealMyActivity = findViewById<CircularRevealRelativeLayout>(layMyActivity)
        val layCircularRevealCardGuide = findViewById<CircularRevealRelativeLayout>(layCardGuide)
        val layCircularRevealCardLookUp = findViewById<CircularRevealRelativeLayout>(cardLookUp)
        layLprResults.visibility = View.GONE
        layContinuousMode.visibility = View.GONE


        //Start of setAccessibilityForComponents
        imgClose.contentDescription = getString(R.string.ada_content_description_back_button)
        imgOpen.contentDescription = getString(R.string.ada_content_description_hamburger_menu)
        //End of setAccessibilityForComponents

        if (showAndEnableScanVehicleStickerModule) {
            layScanSticker.showView()
        } else {
            layScanSticker.hideView()
        }

        if (showAndEnableDirectedEnforcementModule) {
            layDirectedEnforcement.showView()
        } else {
            layDirectedEnforcement.hideView()
        }
        if (showAndEnableCameraFeedUnderGuideEnforecement) {
            laycameraviolation.showView()
        } else {
            laycameraviolation.hideView()
        }
        if (showAndEnableCameraGuidedEnforcementModule) {
            laygeneticHitList.showView()
        } else {
            laygeneticHitList.hideView()
        }

        laySunlight.visibility = View.GONE
        layQRCode!!.visibility = View.GONE
        layBrokenMeterReport.visibility = View.GONE
        layCurbReport.visibility = View.GONE
        laySpecialAssignmentReport.visibility = View.GONE
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)) {
            lay72hrNoticeToTowReport.visibility = View.GONE
            laySignOffReport.visibility = View.GONE
            layTowReport.visibility = View.GONE
            layNFL.visibility = View.GONE
            layHardSummer.visibility = View.GONE
            layAfterSeven.visibility = View.GONE
            layPayStationReport.visibility = View.GONE
            laySignageReport.visibility = View.GONE
            layHomelessReport.visibility = View.GONE
            laySafetyReport.visibility = View.GONE
            layTrashReport.visibility = View.GONE
            layLotCountVioRateReport.visibility = View.GONE
            layLotInspectionReport.visibility = View.GONE
            //layQRCode.visibility = View.VISIBLE
            layWordOrderReport.visibility = View.GONE

            laySpecialAssignmentReport.visibility = View.VISIBLE
            layBikeInspectionReport.visibility = View.VISIBLE
            layBrokenMeterReport.visibility = View.VISIBLE
            layCurbReport.visibility = View.VISIBLE
        }
        //XF 2T printer
        if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
            laySunlight.visibility = View.VISIBLE
        }


        if (getGenerateQRCodeVisibilityStatusFormSettingFile()) {
            layGenerateQRCode.visibility = View.VISIBLE
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
        ) {
            layAllReport.visibility = View.VISIBLE
        } else {
            layAllReport.visibility = View.GONE
        }

        //Start of Enabling of Municipal Citation Option
        if (LogUtil.isMunicipalCitationEnabled()) {
            layMunicipalCitation.showView()
        } else {
            layMunicipalCitation.hideView()
        }
        //Start of Enabling of Municipal Citation Option
        if (LogUtil.isOwnerBillEnabled()) {
            layOwnerbill.showView()
        } else {
            layOwnerbill.hideView()
        }

        //End of Enabling of Municipal Citation Option
        LayLogout.setOnClickListener {
            callUploadAPILogsTextFile()
            if (showAndEnableInventoryModule) {
                if (isAllEquipmentCheckedIn(mDb?.dbDAO?.getInventoryToShowData() as MutableList<InventoryToShowTable?>?)) {
                    txtlogout!!.visibility = View.VISIBLE
                    layCircularRevealRelativeLayout.visibility = View.GONE
                    openUserLogoutDialog(mContext)
                } else {
                    showReasonNoteDialogForEquipmentCheckIn()
                }
            } else {
                layCircularRevealRelativeLayout.visibility = View.GONE
                openUserLogoutDialog(mContext)
            }

        }

        layClearCache.setOnClickListener {
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, true)) {
//                startActivity(
//                        Intent(this@BaseActivity, StarPrinterActivity::class.java)
//                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                )
//
//            }else {
//                if (!(context as BaseActivity?)?.localClassName.equals(
//                                "ui.login.activity.WelcomeActivity", ignoreCase = true)) {
            hotRestart(context, "MENU")
//                }
//            }

        }
        layBack.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            //launchScreen(mContext, WelcomeActivity.class);
            startActivity(
                Intent(this@BaseActivity, WelcomeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
        layLookup.setOnClickListener {
            if (isOptionEnabledOrErrorToShowForInventoryModule().isNullOrEmpty()) {
                if (LogUtil.isEnableActivityLogs)
                    callEventActivityLogApiForBaseActivity(
                        mValue = ACTIVITY_LOG_LOOKUP,
                        isDisplay = true
                    )

                layCircularRevealRelativeLayout.visibility = View.GONE
                layCircularRevealMyActivity.visibility = View.GONE
                layCircularRevealCardGuide.visibility = View.GONE
                cardAllReport.visibility = View.GONE
                cardAllReport.visibility = View.GONE
                cardQRCode.visibility = View.GONE
                layCircularRevealCardLookUp.visibility = View.VISIBLE
            } else {
                printToastMSG(
                    this,
                    isOptionEnabledOrErrorToShowForInventoryModule()
                )
            }
        }
        layCitationResults.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            mDb?.dbDAO?.deleteTempImages()
            launchScreen(mContext, SearchActivity::class.java)
        }
        layLprResults.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            launchScreen(mContext, LprContinuousResultActivity::class.java)
        }

        layGuideEnf.setOnClickListener {
            if (isOptionEnabledOrErrorToShowForInventoryModule().isNullOrEmpty()) {
//              closeDrawer();
//              layCircularRevealRelativeLayout.setVisibility(View.GONE);
//              launchScreen(mContext, GuideEnforecementActivity.class);
//              launchScreen(mContext, SupervisorActivity.class);
                layCircularRevealRelativeLayout.visibility = View.GONE
                layCircularRevealMyActivity.visibility = View.GONE
                layCircularRevealCardLookUp.visibility = View.GONE
                cardQRCode.visibility = View.GONE
                cardAllReport.visibility = View.GONE
                layCircularRevealCardGuide.visibility = View.VISIBLE
            } else {
                printToastMSG(
                    this,
                    isOptionEnabledOrErrorToShowForInventoryModule()
                )
            }
        }

        layPayByPlate.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            cardQRCode.visibility = View.GONE
            cardAllReport.visibility = View.GONE
            launchScreen(mContext, GuideEnforecementActivity::class.java)
//            launchScreen(mContext, SupervisorActivity::class.java)
        }
        layPayBySpace.setOnClickListener {
            closeDrawer()
            sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            cardQRCode.visibility = View.GONE
            cardAllReport.visibility = View.GONE
            launchScreen(mContext, PayBySpaceActivity::class.java)
        }
        laygeneticHitList.setOnClickListener {
            closeDrawer()
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            cardQRCode.visibility = View.GONE
            cardAllReport.visibility = View.GONE
            launchScreen(mContext, CameraGuidedEnforcementActivity::class.java)
        }
        laycameraviolation.setOnClickListener {
            closeDrawer()
//            sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            cardQRCode.visibility = View.GONE
            cardAllReport.visibility = View.GONE
            launchScreen(mContext, CameraViolationActivity::class.java)
        }

        layDirectedEnforcement.setOnClickListener {
            closeDrawer()
//            sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            cardQRCode.visibility = View.GONE
            cardAllReport.visibility = View.GONE
            launchScreen(mContext, DirectedEnforcementActivity::class.java)
        }

        layIssue.setOnClickListener {
            if (LogUtil.isEnableActivityLogs)
                callEventActivityLogApiForBaseActivity(
                    mValue = ACTIVITY_LOG_MENU_ISSUE,
                    isDisplay = true
                )

            closeDrawer()
            sharedPreference.writeOverTimeParkingTicketDetails(
                SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                AddTimingRequest()!!
            )
            sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
            mDb?.dbDAO?.deleteTempImages()
            sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "false")
            sharedPreference.write(SharedPrefKey.VOIDANDREISSUEBYPLATE, false)
            sharedPreference.write(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
            layCircularRevealRelativeLayout.visibility = View.GONE
            val mIntent = Intent(mContext, LprDetails2Activity::class.java)
            mIntent.putExtra("make", "")
            mIntent.putExtra("from_scr", "BaseActivity")
            mIntent.putExtra("model", "")
            mIntent.putExtra("color", "")
            mIntent.putExtra("lpr_number", "")
            startActivity(mIntent)
        }
        layScan.setOnClickListener {
            if (LogUtil.isEnableActivityLogs)
                callEventActivityLogApiForBaseActivity(
                    mValue = ACTIVITY_LOG_MENU_SCAN,
                    isDisplay = true
                )

            closeDrawer()
            sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
            layCircularRevealRelativeLayout.visibility = View.GONE
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)) {
                launchScreen(this, LprDetailsActivity::class.java)
            } else {
                launchScreen(this, LprScanActivity::class.java)
            }
//            launchScreen(mContext, LprScanActivity::class.java)

//            mContext.weaActivity = WeakReference<LprScanActivity>(mContext as LprScanActivity?)
//            val intent = Intent(mContext, mActivity)
//            startActivity(intent)
        }

        layScanSticker.setOnClickListener {
            launchScreen(this, VehicleStickerScanActivity::class.java)
        }

        layMunicipalCitation.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            mDb?.dbDAO?.deleteTempImages()

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)) {
            launchScreen(this, MunicipalCitationDetailsActivity::class.java,Constants.HONOR_BILL_ACTIVITY)
            } else {
                launchScreen(this, MunicipalCitationDetailsActivity::class.java,Constants.MUNICIPAL_ACTIVITY)
            }
        }

        layOwnerbill.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            mDb?.dbDAO?.deleteTempImages()

//            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)) {
            launchScreen(this, HonorBillActivity::class.java)
//            } else {
//                launchScreen(this, LprScanActivity::class.java)
//            }
        }

        layTicketing.setOnClickListener {
            if (isOptionEnabledOrErrorToShowForInventoryModule().isNullOrEmpty()) {
                layCircularRevealMyActivity.visibility = View.GONE
                layCircularRevealCardGuide.visibility = View.GONE
                layCircularRevealCardLookUp.visibility = View.GONE
                cardQRCode.visibility = View.GONE
                cardAllReport.visibility = View.GONE
                layCircularRevealRelativeLayout.visibility = View.VISIBLE
            } else {
                printToastMSG(
                    this,
                    isOptionEnabledOrErrorToShowForInventoryModule()
                )
            }
        }
        imgClose.setOnClickListener {
            if (!sharedPreference.read(SharedPrefKey.LOCKED_LPR_BOOL, true)
                    .nullSafety() && `val` == 1
            ) {
                // check if drawer is open
                if (drawerLy?.isDrawerOpen(Gravity.RIGHT).nullSafety()) {
                    // close drawer when it is open
                    closeDrawer()
                    layCircularRevealRelativeLayout.visibility = View.GONE
                }
                openLogoutDialog(mContext)
            }
            if (!sharedPreference.read(SharedPrefKey.LOCKED_LPR_BOOL, true)
                    .nullSafety() && `val` == 0
            ) {
                finish()
            }
            if (!sharedPreference.read(SharedPrefKey.LOCKED_LPR_BOOL, true)
                    .nullSafety() && `val` == 2
            ) {
                finish()
                if (!(mContext as BaseActivity).localClassName.equals(
                        "ui.boot.BootActivity",
                        ignoreCase = true
                    )
                ) {
                    deleteImages()
                }
            }
        }
        imgOpen.setOnClickListener {
            if (!sharedPreference.read(SharedPrefKey.LOCKED_LPR_BOOL, true).nullSafety() ||
                LogUtil.isEnableAPILogs
            ) {
//                cardAllReport.visibility = View.GONE
                layCircularRevealRelativeLayout.visibility = View.GONE
                openDrawer()
            }
        }
        imgCross.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
        }
        laySetting.setOnClickListener { //showSettingsDialog();
            closeDrawer()
            launchScreen(mContext, SettingActivity::class.java)
//            launchScreen(mContext, AbandonedVehicleActivity::class.java)
            layCircularRevealRelativeLayout.visibility = View.GONE
        }
        layReport.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            launchScreen(mContext, BrokenMeterActivity::class.java)
//            launchScreen(mContext, CropCameraViewActivity::class.java)
        }
        laySummary.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            launchScreen(mContext, OfficerDailySummaryActivity::class.java)
        }
        laySuperVisorView.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            launchScreen(mContext, SupervisorActivity::class.java)
        }
        LayMyActivity.setOnClickListener {
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.VISIBLE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            cardQRCode.visibility = View.GONE
            cardAllReport.visibility = View.GONE
            //                layCircularRevealRelativeLayout.setVisibility(View.GONE);
//                launchScreen(mContext, MyActivityActivity.class);
        }
        layMapView.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            launchScreen(mContext, MyActivityActivity::class.java)
        }
        layContinuousMode.setOnClickListener {
            closeDrawer()
            layCircularRevealRelativeLayout.visibility = View.GONE
            launchScreen(mContext, ContinuousModeActivity::class.java)
        }
        layAllReport.setOnClickListener {
//            closeDrawer()
            cardAllReport.visibility = View.VISIBLE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            cardQRCode.visibility = View.GONE

        }

        layBrokenMeterReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Broken Meter")
        }
        layCurbReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Curb")
        }
        layFullTimeReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Full Time Report")
        }
        layHandHeldMalfunctionReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Hand Held Malfunction")
        }
        laySignReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Sign Report")
        }
        layVehicleInspectionReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Vehicle Inspection")
        }
        lay72HourMarkedVehiclesReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(
                mContext,
                AllReportActivity::class.java,
                "72 Hour Marked Vehicles"
            )
        }
        layBikeInspectionReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Bike Inspection")
        }
        layEOWSupervisorShiftReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(
                mContext,
                AllReportActivity::class.java,
                "EOW Supervisor Shift Report"
            )
        }
        layPartTimeReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Part Time Report")
        }
        laySpecialAssignmentReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(
                mContext,
                AllReportActivity::class.java,
                "Special Assignment Report"
            )
        }

        layLprHits.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, LprHitsActivity::class.java, "")
        }

        lay72hrNoticeToTowReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(
                mContext,
                AllReportActivity::class.java,
                "72hrs Notice To Tow Report"
            )
        }

        layTowReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Tow Report")
        }

        laySignOffReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Sign Off Report")
        }

        layNFL.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(
                mContext,
                AllReportActivity::class.java,
                "NFL Special Assignment Report"
            )
        }

        layHardSummer.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "SPECIAL EVENT REPORT")
        }

        layAfterSeven.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "PCH Daily Updates")
        }

        layPayStationReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Pay Station Report")
        }

        laySignageReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Signage Report")
        }

        layHomelessReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Homeless Report")
        }

        layLotInspectionReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Lot Inspection Report")
        }

        layWordOrderReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(mContext, AllReportActivity::class.java, "Work Order Report")
        }

        layLotCountVioRateReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(
                mContext,
                AllReportActivity::class.java,
                "Lot Count Vio Rate Report"
            )
        }

        laySafetyReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(
                mContext,
                AllReportActivity::class.java,
                "Safety Report Immediate Attention Required"
            )
        }

        layTrashReport.setOnClickListener {
            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            launchScreenWithParam(
                mContext,
                AllReportActivity::class.java,
                "Trash Lot Maintenance Report"
            )
//            launchScreenWithParam(mContext, GenerateQRCodeActivity::class.java,"")
        }


        layQRCode.setOnClickListener {
//            closeDrawer()
            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            cardQRCode.visibility = View.VISIBLE

        }
        layGenerateQRCode.setOnClickListener {
            cardAllReport.visibility = View.GONE
            cardQRCode.visibility = View.GONE
            closeDrawer()
            launchScreenWithParam(mContext, GenerateQRCodeActivity::class.java, "")
        }
        layScanQRCode.setOnClickListener {
            cardAllReport.visibility = View.GONE
            cardQRCode.visibility = View.GONE
            closeDrawer()
            if (mDb?.dbDAO?.getInventoryToShowData()!!.size > 0) {
                launchScreenWithParam(mContext, QRCodeScanner::class.java, FROM_EQUIPMENT_CHECKIN)
//                launchScreenWithParam(mContext, GenerateQRCodeActivity::class.java, "LOGOUT")
            } else {
                LogUtil.printToastMSG(
                    this,
                    getString(R.string.error_all_inventory_items_are_scanned)
                )
            }
        }
        laySunlight.setOnClickListener {
            if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
                LogUtil.printLog("==>Printer","Inside")
                var xfPrinterUseCase: XfPrinterUseCase? = null
                xfPrinterUseCase = XfPrinterUseCase(this)
//                lifecycle.addObserver(xfPrinterUseCase!!)
//                xfPrinterUseCase?.setPrintInterfaceCallback(this)
                xfPrinterUseCase?.initialize(this@BaseActivity)
                xfPrinterUseCase?.printerFeedButton()
            }

//            if(!AppUtils.isSunLightMode) {
//                imgSunlight.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        this@BaseActivity,
//                        R.drawable.baseline_wb_sunny_24_on
//                    )
//                )
//                AppUtils.isSunLightMode = true
//            }else{
//                imgSunlight.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        this@BaseActivity,
//                        R.drawable.baseline_wb_sunny_24_off
//                    )
//                )
//                AppUtils.isSunLightMode = false
//            }

            cardAllReport.visibility = View.GONE
            layCircularRevealRelativeLayout.visibility = View.GONE
            layCircularRevealMyActivity.visibility = View.GONE
            layCircularRevealCardGuide.visibility = View.GONE
            layCircularRevealCardLookUp.visibility = View.GONE
            closeDrawer()

//            val intent = Intent(this@BaseActivity, WelcomeActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)

        }
    }

    /**
     * This function is used to check if the given menu option is need to be enabled or not based on site & condition
     * If this will return null means, it can be enabled & proceed
     * If it returns some message, it means it can be disabled & we have to show that message to user
     */
    private fun isOptionEnabledOrErrorToShowForInventoryModule(): String? {
        return if (showAndEnableInventoryModule) {
            if (isRequiredEquipmentCheckedOut(mDb?.dbDAO?.getInventoryToShowData() as MutableList<InventoryToShowTable?>?)) {
                null
            } else {
                getString(R.string.error_red_box_qr_code_inventory_is_mandatory_to_proceed)
            }
        } else {
            null
        }
    }

    /**
     * This function is used to check if all required equipment is being checked-out for the use or not
     */
    fun isRequiredEquipmentCheckedOut(qrCodeInventoryBannerList: MutableList<InventoryToShowTable?>?): Boolean {
        return qrCodeInventoryBannerList?.firstOrNull { it?.required.intToBool() && it?.checkedOut != EQUIPMENT_CHECKED_OUT } == null
    }

    /**
     * This function is used to check if the QR is already being checked out or not from that category
     */
    fun isScannedEquipmentCategoryAlreadyCheckedOut(
        scannedKey: String,
        qrCodeInventoryBannerList: MutableList<InventoryToShowTable?>?
    ): Boolean {
        return qrCodeInventoryBannerList?.firstOrNull {
            it?.equipmentName.equals(
                scannedKey,
                true
            ) && it?.checkedOut == EQUIPMENT_CHECKED_OUT
        } != null
    }

    /**
     * This function is used to check if the QR is already being checked out or not
     */
    fun isScannedEquipmentAlreadyCheckedOut(
        scannedKey: String,
        scannedValue: String,
        qrCodeInventoryBannerList: MutableList<InventoryToShowTable?>?
    ): Boolean {
        return qrCodeInventoryBannerList?.firstOrNull {
            it?.equipmentName.equals(
                scannedKey,
                true
            ) && it?.equipmentValue.equals(
                scannedValue,
                true
            ) && it?.checkedOut == EQUIPMENT_CHECKED_OUT
        } != null
    }

    /**
     * This function is used to check all equipment is checked-in back or not before logout
     */
    private fun isAllEquipmentCheckedIn(qrCodeInventoryBannerList: MutableList<InventoryToShowTable?>?): Boolean {
        return qrCodeInventoryBannerList?.firstOrNull { it?.checkedOut == EQUIPMENT_CHECKED_OUT } == null
    }

    /**
     * This fuction used to used show a dialog with text input to the user, if you are trying to logout without submitting the equipment back
     * I mean, without check-in all the equipment back
     */
    private fun showReasonNoteDialogForEquipmentCheckIn() {
        val mDialog = Dialog(this@BaseActivity, R.style.ThemeDialogCustom)
        mDialog.setContentView(R.layout.dialog_add_note)

        val etTextNote: TextInputEditText = mDialog.findViewById(R.id.etNote)
        val tvDialogTitle: AppCompatTextView = mDialog.findViewById(R.id.tv_dialogTitle)
        val btnDone: AppCompatButton = mDialog.findViewById(R.id.btn_done)

        tvDialogTitle.text = getString(R.string.msg_add_note_before_logout)
        btnDone.text = getString(R.string.btn_text_submit_and_logout)

        etTextNote.filters =
            arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.max_length_for_equipment_note)))

        val appCompatImageView = mDialog.findViewById<ImageView>(R.id.btn_cancel)
        appCompatImageView.setOnClickListener { v: View? -> mDialog.dismiss() }
        btnDone.setOnClickListener {
            if (etTextNote.text.isNullOrEmpty()) {
                etTextNote.requestFocus()
                etTextNote.isFocusable = true
                etTextNote.error = getString(R.string.val_msg_please_enter_note)
                LogUtil.printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.val_msg_please_enter_note)
                )
            } else {
                mDialog.dismiss()
                logout(this@BaseActivity, etTextNote.text.toString())
            }
        }
        mDialog.show()
        val window = mDialog.window
        window?.setLayout(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        )
    }

    fun deleteImages() {
        try {
            //delete temporary images list
            mDb?.dbDAO?.deleteTempImages()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //logout
    private fun openUserLogoutDialog(mContext: Context) {
        val dialog = Dialog(mContext, R.style.ThemeDialogCustom)
        dialog.setCancelable(true)
        val dialogView = layoutInflater.inflate(R.layout.dialog_gps_alert, null)
        dialog.setContentView(dialogView)
        val mClose: AppCompatImageView = dialogView.findViewById(R.id.img_close)
        val mBtnGo: AppCompatButton = dialogView.findViewById(R.id.btn_continue)
        val mTxtMessage: AppCompatTextView = dialogView.findViewById(R.id.txt_lable)
        val mTxtAlert: AppCompatImageView = dialogView.findViewById(R.id.iv_alert)
        val mTxtMessageSec: AppCompatTextView = dialogView.findViewById(R.id.txt_lable_sec)
        mTxtAlert.visibility = View.GONE
        mTxtMessage.visibility = View.GONE
        mTxtMessageSec.visibility = View.VISIBLE
        mTxtMessageSec.text = getString(R.string.scr_msg_do_you_want_to_logout)
        mBtnGo.text = "OK"
        mClose.setOnClickListener { view: View? -> dialog.dismiss() }
        mBtnGo.setOnClickListener { v: View? ->
            dialog.dismiss()
            try {
                try {
                    takeDataBaseBackup()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                showProgressLoader(getString(R.string.scr_message_please_wait))
                callEventActivityLogApiForBaseActivity("Logout")
            } catch (e: ParseException) {
                e.printStackTrace()
//                logout(mContext)
            }
        }
        dialog.show()
    }

    //dialog - error
    fun openLogoutDialog(mContext: Context?) {
        val dialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
        dialog.setCancelable(true)
        val dialogView = layoutInflater.inflate(R.layout.dialog_gps_alert, null)
        dialog.setContentView(dialogView)
        val mClose: AppCompatImageView = dialogView.findViewById(R.id.img_close)
        val mBtnGo: AppCompatButton = dialogView.findViewById(R.id.btn_continue)
        val mTxtMessage: AppCompatTextView = dialogView.findViewById(R.id.txt_lable)
        val mTxtAlert: AppCompatImageView = dialogView.findViewById(R.id.iv_alert)
        val mTxtMessageSec: AppCompatTextView = dialogView.findViewById(R.id.txt_lable_sec)
        mTxtAlert.visibility = View.GONE
        mTxtMessage.visibility = View.GONE
        mTxtMessageSec.visibility = View.VISIBLE
        mTxtMessageSec.text = getString(R.string.err_msg_logout)
        mBtnGo.text = "OK"
        mClose.setOnClickListener { view: View? -> dialog.dismiss() }
        mBtnGo.setOnClickListener { v: View? ->
            dialog.dismiss()
            finishAffinity()
        }
        dialog.show()
    }

    /**
     * Function used to logout user from anywhere in the app
     * additional parameter logoutNote only used when you have inventory module enabled & you have some equipment checkedout
     */
    fun logout(mContext: Context, logoutNote: String? = null) {
        //We have to call logout note API only when you have inventory module & when you are equipment which is not checked in yet and you are tring to logout
        if (showAndEnableInventoryModule && !isAllEquipmentCheckedIn(mDb?.dbDAO?.getInventoryToShowData() as MutableList<InventoryToShowTable?>?)
        ) {
            val logoutNoteForEquipmentRequest = LogoutNoteForEquipmentRequest()
            logoutNoteForEquipmentRequest.logoutNote =
                logoutNote.nullSafety(getString(R.string.msg_unexpected_logout))
            inventoryViewModel?.addNoteForNotCheckedInEquipment(logoutNoteForEquipmentRequest)
        }

        try {

            //set login status false
            sharedPreference.write(SharedPrefKey.IS_LOGGED_IN, false)
            //stop location service
            stopService(Intent(this, BackgroundLocationUpdateService::class.java))
            //user event logging
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble()
            val mLocationUpdateRequest = LocUpdateRequest()
            mLocationUpdateRequest.activityType = Constants.ACTIVITY_TYPE_LOCATION_UPDATE
            mLocationUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
            mLocationUpdateRequest.locationUpdateType = "logout"
            mLocationUpdateRequest.latitude = mLat
            mLocationUpdateRequest.longitude = mLong
            var Zone = "CST"
//            val modelZone = mDb?.dbDAO?.getDataset()
//            if (modelZone != null && modelZone.dataset?.settingsList != null) {
//                Zone = modelZone.dataset?.settingsList?.get(0)?.mValue.nullSafety()
//            }
//            val timeMilli1 = AppUtils.getClientTimeStamp(Zone)
            //        mLocationUpdateRequest.setClientTimestamp(timeMilli1);
//            DataSetClass.mApplicationListMake = null
//            DataSetClass.mApplicationListModle = null
//            DataSetClass.mApplicationListColor = null
//            DataSetClass.mApplicationListBlock = null
//            DataSetClass.mApplicationListStreet = null
//            DataSetClass.mApplicationListRegulationTimeList = null


            mLocationUpdateRequest.clientTimestamp = AppUtils.splitDateLpr(Zone)
            callLocationApi(mLocationUpdateRequest)
            callPushEventLogin(Constants.SESSION, mEventStartTimeStamp)
            //LogUtil.printToastMSG(DashboardActivity.this, "Logout");
            //launch login screen
            launchScreen(mContext, LoginActivity::class.java)
            finishAffinity()
        } catch (e: ParseException) {
            e.printStackTrace()
            launchScreen(mContext, LoginActivity::class.java)
            finishAffinity()
        }
    }

    override fun openDrawer() {
        drawerLy?.openDrawer(Gravity.RIGHT)
    }

    override fun closeDrawer() {
        drawerLy?.closeDrawers()
    }

    fun backpressCloseDrawer(): Boolean {
        // check if drawer is open
        return if (drawerLy!!.isDrawerOpen(Gravity.RIGHT)) {
            // close drawer when it is open
            closeDrawer()
            false
        } else {
            true
        }
    }

    fun setDropdown(searchAutoComplete: AutoCompleteTextView) {
        searchAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // when the user has clicked on an item in the list, it will trigger onTextChanged.
                // To avoid querying the server and showing the dropdown again, use searchAutoComplete.isPerformingCompletion()
                if (!searchAutoComplete.isPerformingCompletion) {
                    if (s.isNotEmpty()) {
                        //downloadSearchResults(s.toString());
                    } else {
                        searchAutoComplete.dismissDropDown() // hide dropdown after user has deleted characters and there's less than 3 visible
                    }
                } else {
                    // user has clicked on a list item so hide the soft keyboard
                    val `in` = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    `in`.hideSoftInputFromWindow(searchAutoComplete.applicationWindowToken, 0)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun showSettingsDialog() {
        val mDialog = Dialog(this, R.style.ThemeDialogCustom)
        mDialog.setContentView(R.layout.dialog_search_filter)
        val mEditTextCitation: TextInputEditText = mDialog.findViewById(R.id.editTextCitation)
        val mEditTextBlock: TextInputEditText = mDialog.findViewById(R.id.editTextBlock)
        val mEditTextStreet: TextInputEditText = mDialog.findViewById(R.id.editTextStreet)
        val mEditTextRecordType: TextInputEditText = mDialog.findViewById(R.id.editTextRecordType)
        val mEditTextStatus: TextInputEditText = mDialog.findViewById(R.id.editTextStatus)
        val mEditTextNumber: TextInputEditText = mDialog.findViewById(R.id.editTextNumber)
        val mAppCompatTextView: AppCompatTextView = mDialog.findViewById(R.id.tv_dialogTitle)
        val appCompatButton: AppCompatButton = mDialog.findViewById(R.id.btn_done)
        val appCompatLayout: LinearLayoutCompat = mDialog.findViewById(R.id.layPopup)
        mAppCompatTextView.text = "Settings"
        appCompatLayout.visibility = View.GONE
        appCompatButton.visibility = View.VISIBLE
        appCompatButton.setOnClickListener { mDialog.dismiss() }
        try {
            val mTimeZoneString = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)?.get(0)?.mValue
            mEditTextCitation.setText("Timezone Name: $mTimeZoneString")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mDialog.show()
    }

    open val locationEventCallBack: Unit
        get() {}

    //Call Api For Event Activity Log
    @Throws(ParseException::class)
    fun callEventActivityLogApiForBaseActivity(mValue: String, isDisplay: Boolean? = false) {
        val mWelcomePageData = mDb?.dbDAO?.getWelcomeForm()

        if (NetworkCheck.isInternetAvailable(this@BaseActivity)) {
            mActivityLoggerAPITag = mValue
            var mZone: String? = "CST"
            if (Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb) != null) {
                mZone = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)?.get(0)?.mValue
            }
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble()
            val activityUpdateRequest = ActivityUpdateRequest()
            activityUpdateRequest.initiatorId = mWelcomePageData?.initiatorId
            activityUpdateRequest.initiatorRole = mWelcomePageData?.initiatorRole
            activityUpdateRequest.activityType = "ActivityUpdate"
            activityUpdateRequest.siteId = AppUtils.getSiteId(this@BaseActivity)
            activityUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
            activityUpdateRequest.mShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            try {
                activityUpdateRequest.activityName = mValue
            } catch (e: Exception) {
                activityUpdateRequest.activityName = ""
            }
            activityUpdateRequest.latitude = mLat
            activityUpdateRequest.longitude = mLong
            activityUpdateRequest.clientTimestamp = AppUtils.splitDateLpr(mZone)
            activityUpdateRequest.androidId = mAndroidId

            if (isDisplay.nullSafety()) {
                activityUpdateRequest.isDisplay = true
            }

            mActivityLogViewModel?.hitActivityLogApi(activityUpdateRequest)
            try {
                if (LogUtil.isEnableGoogleAnalytics) {
                    val json = ObjectMapperProvider.toJson(activityUpdateRequest)
//                            getInstanceOfAnalytics(json, "BASEACTIVITY", "ACTIVITY LOGGER API", "Request")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    fun getInstanceOfAnalytics(Json: String?, mScreen: String?, mApiName: String?, mType: String) {
        if (LogUtil.isEnableGoogleAnalytics) {
            try {
                try {
                    if (mFirebaseAnalytics == null) {
                        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val bundle = Bundle()
                bundle.putString("SCREEN_NAME", mScreen)
                bundle.putString("API_NAME", mApiName)
                if (mType.equals("Request", ignoreCase = true)) {
                    bundle.putString("API_REQUEST", Json)
                } else {
                    bundle.putString("API_RESPONSE", Json)
                }
                mFirebaseAnalytics?.logEvent(mScreen!!, bundle)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //For App Performance Monitoring
    private var activityStartTime = 0L

    companion object {
        //For Start App Performance Monitoring
        private var isInitialized = false
        private var performanceMonitor: PerformanceMonitor? = null
        private var violationTracker: ViolationTracker? = null
        private const val TAG = "BaseActivity"

        // Statistics
        private val activityCount = AtomicInteger(0)
        private val totalViolations = AtomicInteger(0)
        //For End App Performance Monitoring

        /**
         * show error message according to error type
         */
        /**
         * @param view     view that will be animated
         * @param duration for how long in ms will it shake
         * @param offset   start offset of the animation
         * @return returns the same view with animation properties
         */
        fun makeMeShake(view: View, duration: Int, offset: Int): View {
            val anim: Animation = TranslateAnimation((-offset).toFloat(), offset.toFloat(), 0f, 0f)
            anim.duration = duration.toLong()
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = 5
            view.startAnimation(anim)
            return view
        }

        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }

        @JvmField
        var filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*")
                        .matcher(
                            source[i].toString()
                        ).matches()
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

        /**
         * Function used to get QR code size from settings file for Zebra Command Print
         */
        fun getQRCodeSizeForCommandPrint(): Int {
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

            if (settingsList != null) {
                if (mQrCodeSizeForCommandFacsimile == 0) {
                    mQrCodeSizeForCommandFacsimile = settingsList?.firstOrNull {
                        it.type?.trim().equals(
                            Constants.SETTINGS_FLAG_QR_CODE_SIZE,
                            true
                        )
                    }?.mValue.nullSafety("0").toInt()
                }
            }
            return mQrCodeSizeForCommandFacsimile
        }
    }

    private fun takeDataBaseBackup() {
        val dbFile: File = getDatabasePath("park_loyalty")
        val sDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.DATABASEBACKUP
        )
        val fileName = "park_loyalty"
        val sfPath = sDir.path + File.separator + fileName
        if (!sDir.exists()) {
            sDir.mkdirs()
        }
        val saveFile = File(sfPath)
        if (saveFile.exists()) {
            Log.d("LOGGER ", "File exists. Deleting it and then creating new file.")
            saveFile.delete()
        }
        try {
            if (saveFile.createNewFile()) {
                val bufferSize = 8 * 1024
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                val saveDb: OutputStream = FileOutputStream(sfPath)
                val indDb: InputStream = FileInputStream(dbFile)
                do {
                    bytesRead = indDb.read(buffer, 0, bufferSize)
                    if (bytesRead < 0)
                        break
                    saveDb.write(buffer, 0, bytesRead)
                } while (true)
                saveDb.flush()
                indDb.close()
                saveDb.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }


    //save Citation Layout Databse
    public fun uploadMissingActivityImages() {
        unUploadActivityImages().execute()
    }

    /**
     * only get status 0 citation which is facsimile not upload
     *
     */
    inner class unUploadActivityImages : AsyncTask<Void?, Int?, ActivityImageTable?>() {
        override fun doInBackground(vararg voids: Void?): ActivityImageTable? {
            try {
                var mActitivyMissingImages: List<ActivityImageTable?>? = ArrayList()
                mActitivyMissingImages = mDb!!.dbDAO!!.getActivityImageData()
                mActivityImage =
                    if (mActitivyMissingImages!!.size > 0) mActitivyMissingImages!!.get(0)!! else mActivityImage
                if (mActivityImage!!.image1!!.isEmpty()) {
                    mActivityImage = mActitivyMissingImages!!.get(mActitivyMissingImages.size - 1)!!
                }
                activityImageUpdateRequest!!.activityId = mActivityImage!!.activityResponseId
                if (mActivityImage != null && mActivityImage!!.uploadStatus!!.isNotEmpty() && mActivityImage!!.uploadStatus!!.equals(
                        "false"
                    )
                ) {
                    return mActivityImage
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        @SuppressLint("WrongThread")
        override fun onPostExecute(result: ActivityImageTable?) {
            try {
                if (result != null) {
                    if (result!!.image1!!.isNotEmpty()) {
                        resultFacsimileImage = UnUploadFacsimileImage()
                        mActivityImageCount = mActivityImageCount?.plus(1)
                        resultFacsimileImage!!.imagePath = result!!.image1
                        resultFacsimileImage!!.uploadedCitationId = result!!.activityResponseId

                        resultFacsimileImage?.let {
                            resultFacsimileImage
                            callUploadImages(it, "ActivityImages")
                        }
                    }
                    if (result!!.image2!!.isNotEmpty()) {
                        mActivityImageCount = mActivityImageCount?.plus(1)
                        resultFacsimileImage!!.imagePath = result!!.image2
                        resultFacsimileImage!!.uploadedCitationId = result!!.activityResponseId

                        resultFacsimileImage?.let {
                            resultFacsimileImage
                            callUploadImages(it, "ActivityImages")
                        }
                    }
                    if (result!!.image3!!.isNotEmpty()) {
                        mActivityImageCount = mActivityImageCount?.plus(1)
                        resultFacsimileImage!!.imagePath = result!!.image3
                        resultFacsimileImage!!.uploadedCitationId = result!!.activityResponseId

                        resultFacsimileImage?.let {
                            resultFacsimileImage
                            callUploadImages(it, "ActivityImages")
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    //save Citation Layout Databse
    private fun uploadMissingFacsimileImages() {
        unUploadFacsimileImages().execute()
    }

    /**
     * only get status 0 citation which is facsimile not upload
     *
     */
    inner class unUploadFacsimileImages : AsyncTask<Void?, Int?, UnUploadFacsimileImage?>() {
        override fun doInBackground(vararg voids: Void?): UnUploadFacsimileImage? {
            try {
                var mFacsimileImage: UnUploadFacsimileImage? = mDb!!.dbDAO!!.getUnUploadFacsimile()
                if (mFacsimileImage != null && mFacsimileImage!!.uploadedCitationId!!.isNotEmpty()) {
                    return mFacsimileImage
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        @SuppressLint("WrongThread")
        override fun onPostExecute(result: UnUploadFacsimileImage?) {
            try {
                result?.let {
                    if (it.imageLink!!.isNotEmpty()) {
                        resultFacsimileImage = it
                        mFacsimileImagesLink.add(it.imageLink!!)
                        callUploadImagesUrl()
                    } else {
                        callUploadImages(it, "CitationImages")
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    /* Call Api For update profile */
    private fun callUploadImages(result: UnUploadFacsimileImage, folderName: String) {
        resultFacsimileImage = result
        val file: File? = File(result!!.imagePath)
        val num: Int = (result.imageCount + 1)
        if (NetworkCheck.isInternetAvailable(this@BaseActivity)) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
            val files = MultipartBody.Part.createFormData(
                "files",
                if (file != null) file.name else "",
                requestFile
            )
            if (folderName.equals("CitationImages")) {
                whichImageUploadCall = "Citation"
//                mDropdownList = if (file!!.name.contains("_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
//                    arrayOf(mCitationNumberId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
//                } else {
//                    arrayOf(mCitationNumberId + "_" + num)
//                }
                var mDropdownList  = if (file!!.name.contains("_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
                    arrayOf(result!!.uploadedCitationId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
                }else{
                    arrayOf(result!!.uploadedCitationId + "_" + num +"_"+result!!.dateTime)
                }
                val mRequestBodyType =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
                mFacsimileUploadLinkImageViewModel?.hitUploadImagesApiForBaseActivity(
                    mDropdownList,
                    mRequestBodyType,
                    files
                )
            } else {
                val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
                whichImageUploadCall = "Activity"
                var mDropdownList =
                    arrayOf(id + "_" + Random().nextInt(100) + "_activity_image")
                val mRequestBodyType =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), "ActivityImages")
                mFacsimileUploadLinkImageViewModel?.hitUploadImagesApiForBaseActivity(
                    mDropdownList,
                    mRequestBodyType,
                    files
                )
            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }


    /* Call Api For add un-upload images*/
    private fun callUploadImagesUrl() {
        if (NetworkCheck.isInternetAvailable(this@BaseActivity)) {
            if (resultFacsimileImage != null) {
                val endPoint = "${resultFacsimileImage!!.uploadedCitationId}/images"
                val addImageRequest = AddImageRequest()
                addImageRequest.images = mFacsimileImagesLink
                addFacsimileImageViewModel!!.hitAddImagesApiBaseActivityScreen(addImageRequest, endPoint)
            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For add un-upload images*/
    private fun callUploadActivityImagesUrl() {
        if (NetworkCheck.isInternetAvailable(this@BaseActivity)) {
            if (activityImageUpdateRequest != null) {
                mUploadActivityImage!!.hitUploadActivityImageApi(activityImageUpdateRequest)
            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    fun getMeterBuzzerAPICallStatusFormSettingFile(): Boolean {
        try {
            var settingsList: List<DatasetResponse>? = ArrayList()
            settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
            if (settingsList != null && settingsList!!.isNotEmpty()) {
                for (i in settingsList!!.indices) {
                    if (settingsList!![i].type.equals("IS_METER_BUZZER_ACTIVE", ignoreCase = true)
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                    ) {
                        return true
                    }
                }
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getGenerateQRCodeVisibilityStatusFormSettingFile(): Boolean {
        try {
            var settingsList: List<DatasetResponse>? = ArrayList()
            settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
            if (settingsList != null && settingsList!!.isNotEmpty()) {
                for (i in settingsList!!.indices) {
                    if (settingsList!![i].type.equals(
                            "IS_GENERATE_QR_CODE_VISIBILE",
                            ignoreCase = true
                        )
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                    ) {
                        return true
                    }
                }
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    //save Timing Data form if offline
    public fun saveActivityLogData(
        mResponse: ActivityUpdateRequest,
        bannerList: MutableList<TimingImagesModel?>? = ArrayList(),
        activity_id: String
    ) {
        class SaveTask : AsyncTask<Void?, Int?, String>() {
            override fun doInBackground(vararg voids: Void?): String? {
                try {
                    var model = ActivityImageTable()
                    val mSelectedImageFileUriList = ArrayList<String>()
//                    val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
                    model.activityResponseId = activity_id
                    model.id = Random().nextInt(1000)
//                        for (index in bannerList!!.indices) {
//                            mSelectedImageFileUriList.add(bannerList!!.get(index)!!.timingImage.toString())
//                        }
                    if (bannerList!!.size > 0)
                        model.image1 = bannerList!!.get(0)!!.timingImage.toString()
                    if (bannerList!!.size > 1)
                        model.image2 = bannerList!!.get(1)!!.timingImage.toString()
                    if (bannerList!!.size > 2)
                        model.image3 = bannerList!!.get(2)!!.timingImage.toString()

                    mDb?.dbDAO?.insertActivityImageData(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return "saved"
            }

            protected fun onPostExecute(result: CitationNumberDatabaseModel?) {
                //LogUtil.printToastMSG(mContext,"Booklet saved!");
            }
        }
        SaveTask().execute()
    }

    fun checkMissingFacsimile() {
        ioScope.launch {
            try {
                val mFacsimileImage: UnUploadFacsimileImage? =
                    getMyDatabase()?.dbDAO?.getUnUploadFacsimile()
//                var mFacsimileImage: List<UnUploadFacsimileImage?>? = mDb!!.dbDAO!!.getUnUploadFacsimileAll()
                if (mFacsimileImage != null && mFacsimileImage.uploadedCitationId.nullSafety()
                        .isNotEmpty() && mFacsimileImage?.imageType.equals(UNUPLOAD_IMAGE_TYPE_FACSIMILE)
                ) {
                    mainScope.launch {
                        AppUtils.showCustomAlertDialog(
                            context,
                            "Un-Upload Facsimile Alert",
                            mFacsimileImage.lprNumber + " facsimile Image has not been uploaded to the server, can we process now?",
                            getString(R.string.alt_lbl_OK),
                            getString(R.string.scr_btn_cancel),
                            object : CustomDialogHelper {
                                override fun onYesButtonClick() {
                                    //TODO("Not yet implemented")
                                }

                                override fun onNoButtonClick() {
                                    //TODO("Not yet implemented")
                                }

                                override fun onYesButtonClickParam(msg: String?) {
                                    val mIntent = Intent(
                                        this@BaseActivity,
                                        MissingFacsimileActivity::class.java
                                    )
                                    startActivity(mIntent)
                                }
                            }
                        )
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sunLightMode(
        mContext: Context,
        mTextInputLayout: TextInputLayout?,
        mTextInputEditText: AppCompatEditText?,
        mAppCompatAutoCompleteTextView: AppCompatAutoCompleteTextView?
    ) {
        if (mAppCompatAutoCompleteTextView != null) {
            mAppCompatAutoCompleteTextView.setBackgroundResource(R.drawable.round_corner_shap_sunlight)
//            mAppCompatAutoCompleteTextView.setBackgroundColor(
//                ContextCompat.getColor(
//                    mContext,
//                    R.color._013220
//                )
//            )
            mAppCompatAutoCompleteTextView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
            mAppCompatAutoCompleteTextView.setHintTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
        }
        if (mTextInputEditText != null) {
            mTextInputEditText.setBackgroundResource(R.drawable.round_corner_shap_sunlight)
            mTextInputEditText.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
            mTextInputEditText.setHintTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
        }
        mTextInputLayout!!.defaultHintTextColor = ColorStateList.valueOf(
            ContextCompat.getColor(
                mContext,
                R.color.deep_yellow
            )
        )
//        mTextInputLayout!!.stysetTextA(@style/Widget.MaterialComponents.TextInputLayout.FilledBox.Dense)
        mTextInputLayout!!.setStartIconTintList(ColorStateList.valueOf(Color.BLUE))
        mTextInputLayout!!.setEndIconTintList(ColorStateList.valueOf(Color.WHITE))
    }

    fun MoonLightMode(
        mContext: Context,
        mTextInputLayout: TextInputLayout?,
        mTextInputEditText: AppCompatEditText?,
        mAppCompatAutoCompleteTextView: AppCompatAutoCompleteTextView?
    ) {
        if (mAppCompatAutoCompleteTextView != null) {
            mAppCompatAutoCompleteTextView.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
//            mAppCompatAutoCompleteTextView.setBackgroundColor(
//                ContextCompat.getColor(
//                    mContext,
//                    R.color._013220
//                )
//            )
            mAppCompatAutoCompleteTextView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
            mAppCompatAutoCompleteTextView.setHintTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
        }
        if (mTextInputEditText != null) {
            mTextInputEditText.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
            mTextInputEditText.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
            mTextInputEditText.setHintTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
        }
        mTextInputLayout!!.defaultHintTextColor = ColorStateList.valueOf(Color.BLACK)
        mTextInputLayout!!.setStartIconTintList(ColorStateList.valueOf(Color.BLUE))
        mTextInputLayout!!.setEndIconTintList(ColorStateList.valueOf(Color.WHITE))
    }


    /** Call Api For update profile
    this fun use to upload API log file in google could**/
    fun callUploadAPILogsTextFile() {
        try {
            val localFolder =
                File(
                    Environment.getExternalStorageDirectory().absolutePath,
                    Constants.FILE_NAME
                )
            val welcomeForm: WelcomeForm? = mDb!!.dbDAO!!.getWelcomeForm()
            val dateTime = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US).format(Date())
            val fileName1 = FILE_NAME_API_PAYLOAD + ".txt" //like 2016_01_12.txt
            val file = File(localFolder, fileName1)

            if (file!!.exists()) {
                if (NetworkCheck.isInternetAvailable(this@BaseActivity)) {
                    val requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
                    val files = MultipartBody.Part.createFormData(
                        "files",
                        FILE_NAME_API_PAYLOAD + "_" + welcomeForm?.officerFirstName.nullSafety("") + welcomeForm?.officerLastName.nullSafety("")+"_" + welcomeForm?.officerLastName.nullSafety(
                            ""
                        ) + "_" + dateTime + ".txt",
                        requestFile
                    )
                    whichImageUploadCall = "Citation"
                    var mDropdownList =
                        arrayOf(dateTime +"_"+ welcomeForm?.officerFirstName.nullSafety("")+ "_" + "API_LOGS")
                    val mRequestBodyType =
                        RequestBody.create("text/plain".toMediaTypeOrNull(), "APILogAudits")
                    mUploadAPITextFileViewModel?.hitUploadTextFileApi(
                        mDropdownList,
                        mRequestBodyType,
                        files
                    )
//                    try {
//                        ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"---------BASE ACTIVITY API LOG  Upload--------")
//                        ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"Request "+" :- "+mDropdownList +" "+files.headers?.value(0))
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
                } else {
                    LogUtil.printToastMSG(
                        applicationContext,
                        getString(R.string.err_msg_connection_was_refused)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    /**
//     * Function used to set app logo in command printing
//     * It will append logo in command
//     */
//    fun setAppLogoInCommandPrint() {
//        val appLogoToPrint = sharedPreference.read(
//            SharedPrefKey.APP_LOGO_FOR_PRINT, ""
//        ).nullSafety()
//
//        if (appLogoToPrint.isNotEmpty()) {
//            val appLogoToPrintXPosition = sharedPreference.read(
//                SharedPrefKey.APP_LOGO_FOR_PRINT_X, "0"
//            ).nullSafety()
//
//            val appLogoToPrintYPosition = sharedPreference.read(
//                SharedPrefKey.APP_LOGO_FOR_PRINT_Y, "0"
//            ).nullSafety()
//
//            val appLogoToWidth = sharedPreference.read(
//                SharedPrefKey.APP_LOGO_FOR_PRINT_WIDTH, "200"
//            ).nullSafety()
//
//            var appLogo: Bitmap? = null
//            appLogo =
//                if (
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)
//                ) {
//                    //We have different app logo for PRRS, so defined that here
//                    BitmapFactory.decodeResource(
//                        resources,
//                        R.drawable.app_logo_prrs
//                    )
//                } else {
//                    BitmapFactory.decodeResource(
//                        resources,
//                        R.drawable.app_icon
//                    )
//                }
//
//            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)) {
//                //We have checking signature image is exsit or not and print
//                val imageName = getSignatureFileNameWithExt()
//                val mSignaturePath = sharedPreference.read(
//                    SharedPrefKey.FILE_PATH,
//                    ""
//                ) + Constants.CAMERA + "/" + imageName
//                val file = File(mSignaturePath)
//                if (file.exists()) {
//                    val bitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
//                    appLogo = bitmap
//                } else {
//                    appLogo = BitmapFactory.decodeResource(
//                        resources,
//                        R.drawable.signature_pen_for_print
//                    )
//                }
//            }
//
//            val scaledBitmap =
//                BitmapUtils.scale(appLogo, appLogoToWidth.toInt(), appLogoToWidth.toInt())
//
//            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYForImage(
//                bitmap = scaledBitmap,
//                xPosition = appLogoToPrintXPosition.toInt(),
//                yPosition = appLogoToPrintYPosition.toInt(),
//                AppUtils.printQueryStringBuilder
//            )
//        }
//    }
//
//    /**
//     * Function used to set lpr image in command printing
//     * It will append logo in command
//     */
//    fun setLprImageInCommandPrint(lprImage: Bitmap) {
//        val lprImageToPrint = sharedPreference.read(
//            SharedPrefKey.LPR_IMAGE_FOR_PRINT, ""
//        ).nullSafety()
//
//        if (lprImageToPrint.isNotEmpty()) {
//            val lprImageToPrintXPosition = sharedPreference.read(
//                SharedPrefKey.LPR_IMAGE_FOR_PRINT_X, "0"
//            ).nullSafety()
//
//            val lprImageToPrintYPosition = sharedPreference.read(
//                SharedPrefKey.LPR_IMAGE_FOR_PRINT_Y, "0"
//            ).nullSafety()
//
//            val lprImageToWidth = sharedPreference.read(
//                SharedPrefKey.LPR_IMAGE_FOR_PRINT_WIDTH, "200"
//            ).nullSafety()
//
//            val scaledBitmap =
//                BitmapUtils.scale(lprImage, lprImageToWidth.toInt(), lprImageToWidth.toInt())
//
//            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYForImage(
//                bitmap = scaledBitmap,
//                xPosition = lprImageToPrintXPosition.toInt(),
//                yPosition = lprImageToPrintYPosition.toInt(),
//                AppUtils.printQueryStringBuilder
//            )
//        }
//    }

    fun setCrossClearButton(
        context: Context,
        textInputLayout: TextInputLayout?,
        appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView?,
        isEditable: Boolean? = true
    ) {
        if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
            textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
            textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

            textInputLayout?.setStartIconOnClickListener {
                appCompatAutoCompleteTextView?.text = null
                announceForAccessibility(
                    textInputLayout,
                    context?.getString(R.string.ada_announcement_text_cleared).nullSafety()
                )
            }

            setAccessibilityForTextInputLayoutCrossButtons(context!!, textInputLayout)
        }
    }

    fun setCrossClearButton(
        context: Context,
        textInputLayout: TextInputLayout?,
        textInputEditText: TextInputEditText?,
        isEditable: Boolean? = true
    ) {
        if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
            textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
            textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

            textInputLayout?.setStartIconOnClickListener {
                textInputEditText?.text = null
                announceForAccessibility(
                    textInputLayout,
                    context?.getString(R.string.ada_announcement_text_cleared).nullSafety()
                )
            }
            setAccessibilityForTextInputLayoutCrossButtons(context!!, textInputLayout)
        }
    }

    fun setCrossClearButton(
        context: Context,
        textInputLayout: TextInputLayout?,
        appCompatEditText: AppCompatEditText?,
        isEditable: Boolean? = true
    ) {
        if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
            textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
            textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

            textInputLayout?.setStartIconOnClickListener {
                appCompatEditText?.text = null
                announceForAccessibility(
                    textInputLayout,
                    context?.getString(R.string.ada_announcement_text_cleared).nullSafety()
                )
            }

            setAccessibilityForTextInputLayoutCrossButtons(context!!, textInputLayout)
        }
    }

    /**
     * Function to export network logs to an Excel file
     */
   /* fun exportLogsToExcel(context: Context): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Network Logs")

        // Header Row
        val header = sheet.createRow(0)
        val headers = listOf("Timestamp", "Method", "URL", "Latency (ms)", "Response Code")
        headers.forEachIndexed { i, h ->
            header.createCell(i).setCellValue(h)
        }

        // Data Rows
        NetworkLogger.logs.forEachIndexed { index, log ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(log.timestamp)
            row.createCell(1).setCellValue(log.method)
            row.createCell(2).setCellValue(log.url)
            row.createCell(3).setCellValue(log.latencyMs.toDouble())
            row.createCell(4).setCellValue(log.responseCode.toDouble())
        }

        // Save file
        val file = File(context.getExternalFilesDir(null), "network_logs.xlsx")
        val outputStream = FileOutputStream(file)
        workbook.write(outputStream)
        workbook.close()
        outputStream.close()

        return file
    }*/

    /**
     * Function to send the Excel file via email
     */
    fun sendExcelViaEmail(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("janaksuthar1st@gmail.com")) // optional
            putExtra(Intent.EXTRA_SUBJECT, "Network Logs Report")
            putExtra(Intent.EXTRA_TEXT, "Please find the attached network logs report.")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Launch email chooser
        context.startActivity(
            Intent.createChooser(emailIntent, "Send email using...")
        )
    }

    //How to use this function:
//    val file = exportLogsToExcel(this@BaseActivity)
//    if (file != null) {
//        sendExcelViaEmail(this@BaseActivity, file)
//    } else {
//        Toast.makeText(this@BaseActivity, "File export failed!", Toast.LENGTH_SHORT).show()
//    }



}