package com.parkloyalty.lpr.scan.views

import DialogUtil
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputFilter
import android.view.View
import android.widget.ImageView
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.material.textfield.TextInputEditText
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerRequest
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerResponse
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import com.parkloyalty.lpr.scan.databasetable.ActivityImageTable
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.databinding.ActivityMainBinding
import com.parkloyalty.lpr.scan.extensions.findNav
import com.parkloyalty.lpr.scan.extensions.getAndroidID
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.getInitials
import com.parkloyalty.lpr.scan.extensions.getRandomID
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.isGPSEnabled
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_API_PAYLOAD
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.UNUPLOAD_IMAGE_TYPE_FACSIMILE
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.locationservice.NewBackgroundLocationUpdateService
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.LogoutNoteForEquipmentRequest
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ActivityImageUploadRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ActivityLogResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.TextFileResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UpdatePayload
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.UnUploadFacsimileImage
import com.parkloyalty.lpr.scan.ui.login.model.ActivityLogImageResponse
import com.parkloyalty.lpr.scan.ui.login.model.ActivityUpdateRequest
import com.parkloyalty.lpr.scan.ui.printer.PrinterType.XF2T_PRINTER
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.AddNotesResponse
import com.parkloyalty.lpr.scan.ui.uploadfacsimile.MissingFacsimileActivity
import com.parkloyalty.lpr.scan.ui.xfprinter.XfPrinterUseCase
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogsFile
import com.parkloyalty.lpr.scan.util.SDF_MM_dd_YYYY_HH_mm_ss
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.ACTIVITY_TYPE_ACTIVITY_UPDATE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ACTIVITY_LOG
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_NOTE_FOR_NOT_CHECKED_IN_EQUIPMENT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_INACTIVE_METER_BUZZER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_ACTIVITY_IMAGE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_TEXT_FILE
import com.parkloyalty.lpr.scan.utils.AppConstants
import com.parkloyalty.lpr.scan.utils.AppConstants.DEFAULT_VALUE_ZERO_DOT_ZERO_DBL
import com.parkloyalty.lpr.scan.utils.AppConstants.DEFAULT_VALUE_ZERO_DOT_ZERO_STR
import com.parkloyalty.lpr.scan.utils.AppConstants.FOLDER_ACTIVITY_IMAGES
import com.parkloyalty.lpr.scan.utils.AppConstants.STR_FALSE
import com.parkloyalty.lpr.scan.utils.AppConstants.TIME_INTERVAL_5_MINUTES
import com.parkloyalty.lpr.scan.utils.AppConstants.TIME_INTERVAL_5_SECONDS
import com.parkloyalty.lpr.scan.utils.BundleConstants.BUNDLE_KEY_REPORT_TYPE
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_72_HOURS_MARKED_VEHICLE_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_AFTER_SEVEN_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_BIKE_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_BROKEN_METER_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_CURB_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_EOW_OFFICER_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_EOW_SUPERVISOR_SHIFT_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_HAND_HELD_MALFUNCTION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_HARD_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_NFL_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PART_EOW_OFFICER_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_HOMELESS_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_LOT_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_LOT_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_SAFETY_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_SIGNAGE_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_STATION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_TRASH_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_SIGN_OFF_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_SIGN_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_SPECIAL_ASSIGNMENT_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_TOW_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_VEHICLE_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_WORK_ORDER_REPORT
import com.parkloyalty.lpr.scan.utils.DateTimeUtils
import com.parkloyalty.lpr.scan.utils.InventoryModuleUtil
import com.parkloyalty.lpr.scan.utils.NoInternetDialogListener
import com.parkloyalty.lpr.scan.utils.NoInternetDialogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.utils.permissions.PermissionUtils
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_BROKEN_ASSET_REPORTS
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_GUIDE_ENFORCEMENT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_HOME
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_HOT_RESTART
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_LOOKUP
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_MY_ACTIVITY
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_PAPER_FEED
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_QR_CODE
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_REPORT_SERVICE
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_SETTING
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MAIN_MENU_TICKETING
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_72_HOURS_MARKED_VEHICLE_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_72_HOURS_NOTICE_TOW_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_AFTER_SEVEN_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_BIKE_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_BROKEN_METER_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_CAMERA_FEED
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_CITATION_RESULT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_CURB_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_DAILY_SUMMARY
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_DIRECTED_ENFORCEMENT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_EOW_OFFICER_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_EOW_SUPERVISOR_SHIFT_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_GENERATE_QR_CODE
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_GENETIC_LIST
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_GRAPH_VIEW
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_HAND_HELD_MALFUNCTION_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_HARD_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_ISSUE
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_LPR_HITS
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_LPR_MODE
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_LPR_RESULT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_MUNICIPAL_CITATION
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_NFL_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_OWNER_BILL
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PART_EOW_OFFICER_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_BY_PLATE
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_BY_SPACE
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_HOMELESS_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_LOT_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_LOT_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_SAFETY_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_SIGNAGE_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_STATION_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_PAY_TRASH_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_SCAN
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_SCAN_QR_CODE
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_SCAN_STICKER
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_SIGN_OFF_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_SIGN_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_SPECIAL_ASSIGNMENT_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_SUPERVISOR_VIEW
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_TOW_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_VEHICLE_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HEM_MENU_WORK_ORDER_REPORT
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HemMenuAdapter
import com.parkloyalty.lpr.scan.views.hemmenudrawer.HemMenuItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.regex.Pattern
import javax.inject.Inject


//TODO : Do not show no internet dialog everywhere
//TODO : Put gps & location service here from welcome screen
//TODO : getQRCodeSizeForCommandPrint & its use in ZebraCommandPrintUtils
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
//    // UI & ViewModel
//    private lateinit var binding: ActivityMainBinding
//    private val mainActivityViewModel: MainActivityViewModel by viewModels()
//
//    @Inject
//    lateinit var permissionFactory: PermissionManager.Factory
//
//    private lateinit var permissionManager: PermissionManager
//
//    //Location clients
//    private var mFusedLocationClient: FusedLocationProviderClient? = null
//    private var mSettingsClient: SettingsClient? = null
//    private var mLocationCallback: LocationCallback? = null
//    private var mLocationRequest: LocationRequest? = null
//
//    // local cache to use in callbacks
//    private var backBlockedLocal: Boolean = false
//
//    private var legacyBackCallback: OnBackPressedCallback? = null
//    private var predictiveCallback: OnBackInvokedCallback? = null
//
//    // Adapters
//    private lateinit var hemMenuAdapter: HemMenuAdapter
//
//    // Splash Screens
//    private var splashScreen: SplashScreen? = null
//    private var isShowSplashScreen: Boolean = true
//
//    // Shared Preferences
//    @Inject
//    lateinit var sharedPreference: SharedPref
//
//    //Broadcast
//    private var myReceiver: MyReceiver? = null
//    var location: Location? = null
//    var mEventStartTimeStamp: String? = null
//
//    private var mWakeLock: PowerManager.WakeLock? = null
//
//
//    //Extras
//    private var mTimeStatus = false
//    private var mHandler = Handler(Looper.getMainLooper())
//    private var mActivityLoggerAPITag = ""
//    private var resultFacsimileImage: UnUploadFacsimileImage? = null
//    private var mFacsimileImagesLink: MutableList<String> = ArrayList()
//
//    private var typeOfImageApiCall: String = CITATION_IMAGE_API_CALLED
//
//    private val activityImageUpdateRequest = ActivityImageUploadRequest()
//    private val updateActivityImagePayLoad = UpdatePayload()
//    private lateinit var mActivityImage: ActivityImageTable
//    private var mActivityImageCount: Int? = 0
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        // Set up the splash screen
//        splashScreen = installSplashScreen()
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        permissionManager =
//            permissionFactory.create(caller = this, context = this@MainActivity, activity = this)
//
//        myReceiver = getMyReceiver()
//
//        // Setup edge-to-edge UI
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
//            insets
//        }
//
//        // Control splash screen visibility based on loading stat
//        splashScreen?.setKeepOnScreenCondition { isShowSplashScreen }
//
//        startLocationTicker()
//        initViewLifecycleScope()
//        initialiseData()
//        setupClickListeners()
//    }
//
//    @SuppressLint("ObsoleteSdkInt", "SetTextI18n")
//    private fun initViewLifecycleScope() {
//        // observe ViewModel.stateFlow safely
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch {
//                    mainActivityViewModel.backBlocked.collect { blocked ->
//                        backBlockedLocal = blocked
//                    }
//                }
//
//                launch {
//                    mainActivityViewModel.actions.collect { action ->
//                        logD("==>Janak:-", "MainActivity Action Received: $action")
//                        when (action) {
//                            //is MainAction.ShowLoader -> toggleLoader(action.show)
//                            is MainActivityAction.EventSetupHemMenu -> setupHemMenu()
//                            is MainActivityAction.EventProcessUnUploadedActivityImages -> processUnUploadedActivityImages()
//                            is MainActivityAction.EventProcessLogout -> logout()
//                            is MainActivityAction.EventCallLocationAPI -> callLocationApi(action.locUpdateRequest)
//                            is MainActivityAction.EventSaveActivityLogData -> saveActivityLogData(
//                                action.bannerList, action.activityId
//                            )
//
//                            is MainActivityAction.EventActivityLogAPI -> callEventActivityLogApiForBaseActivity(
//                                action.mValue, action.isDisplay
//                            )
//
//                            is MainActivityAction.EventUploadAPILogTextFile -> callUploadAPILogsTextFile()
//                            else -> {
//                                // no-op
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//        lifecycleScope.launch {
//            mainActivityViewModel.isLoggedIn.collect { isLoggedIn ->
//                if (isLoggedIn != null) {
//                    delay(AppConstants.TIME_INTERVAL_SPLASH_SCREEN)
//                    isShowSplashScreen = false
//                    if (isLoggedIn) {
//                        navigateToWelcomeScreen(isFromLaunch = true)
//                    } else {
//                        navigateToLoginScreen()
//                    }
//                }
//            }
//        }
//
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                launch {
//                    mainActivityViewModel.performSplashOperation()
//                }
//
//                launch {
//                    mainActivityViewModel.backEvent.collect {
//                        handleBackButtonTrigger()
//                    }
//                }
//            }
//        }
//
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                launch {
//                    mainActivityViewModel.toolbarState.collect { state ->
//                        binding.toolbar.isVisible = state.showToolbar
//                        binding.btnBack.isVisible = state.showBackButton
//                        binding.btnHemMenu.isVisible = state.showHemMenu
//                        val sOfficerNameArray = state.officerName.trim().split(" ").toTypedArray()
//                        binding.btnHemMenu.text = "${
//                            sOfficerNameArray.first().uppercase().getInitials()
//                        }${sOfficerNameArray.last().uppercase().getInitials()}"
//                        binding.layoutHemMenuDrawer.tvDrawerHeader.text = state.officerName
//
//                        // Enable or disable hem drawer swipe
//                        binding.drawerLayout.setDrawerLockMode(
//                            if (state.enableHemMenuDrawer) androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
//                            else androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
//                        )
//                    }
//                }
//
//                launch {
//                    mainActivityViewModel.activityLogImageResponse.collect(::consumeResponse)
//                }
//
//                launch {
//                    mainActivityViewModel.inactiveMeterBuzzerResponse.collect(::consumeResponse)
//                }
//
//                launch {
//                    mainActivityViewModel.activityLogResponse.collect(::consumeResponse)
//                }
//
//                launch {
//                    mainActivityViewModel.uploadImageResponse.collect(::consumeResponse)
//                }
//
//                launch {
//                    mainActivityViewModel.uploadTextFileResponse.collect(::consumeResponse)
//                }
//
//                launch {
//                    mainActivityViewModel.addImageResponse.collect(::consumeResponse)
//                }
//
//                launch {
//                    mainActivityViewModel.addNoteForNotCheckedInEquipmentResponse.collect(::consumeResponse)
//                }
//            }
//        }
//
//
//    }
//
//    private fun initialiseData() {
//        mainActivityViewModel.getAndSetSettingFileValues()
//        processUnUploadedFacsimileImages()
//
//        // legacy dispatcher
//        legacyBackCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() = handleBackButtonTrigger()
//        }
//        onBackPressedDispatcher.addCallback(this, legacyBackCallback!!)
//
//        // predictive back (API 33+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            predictiveCallback = OnBackInvokedCallback { handleBackButtonTrigger() }
//            onBackInvokedDispatcher.registerOnBackInvokedCallback(
//                OnBackInvokedDispatcher.PRIORITY_DEFAULT, predictiveCallback!!
//            )
//        }
//
//        setupHemMenu()
//    }
//
//    private fun startLocationTicker() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                while (isActive) {
//                    mTimeStatus = true
//                    withContext(Dispatchers.IO) {
//                        sharedPreference.write(
//                            SharedPrefKey.PRE_LAT,
//                            DEFAULT_VALUE_ZERO_DOT_ZERO_STR
//                        )
//                        sharedPreference.write(
//                            SharedPrefKey.PRE_LONG,
//                            DEFAULT_VALUE_ZERO_DOT_ZERO_STR
//                        )
//                    }
//                    delay(TIME_INTERVAL_5_MINUTES)
//                }
//            }
//        }
//    }
//
//    private fun setupClickListeners() {
//        binding.btnBack.setOnClickListener {
//            handleBackButtonTrigger()
//        }
//
//        binding.btnHemMenu.setOnClickListener {
//            binding.drawerLayout.openDrawer(GravityCompat.END)
//        }
//
//        binding.layoutHemMenuDrawer.ivCloseDrawer.setOnClickListener {
//            binding.drawerLayout.closeDrawer(GravityCompat.END)
//        }
//
//        binding.layoutHemMenuDrawer.btnLogout.setOnClickListener {
//            callUploadAPILogsTextFile()
//            if (mainActivityViewModel.showAndEnableInventoryModule) {
//                if (InventoryModuleUtil.isAllEquipmentCheckedIn(mainActivityViewModel.getInventoryToShowData() as MutableList<InventoryToShowTable?>?)) {
//                    openUserLogoutDialog()
//                } else {
//                    showReasonNoteDialogForEquipmentCheckIn()
//                }
//            } else {
//                openUserLogoutDialog()
//            }
//
//        }
//    }
//
//    //Start of Hem Menu Setup
//    private fun setupHemMenu() {
//        hemMenuAdapter = HemMenuAdapter(
//            getHemMenuOptions(context = this), object : HemMenuAdapter.HemMenuListener {
//                override fun onOptionClicked(option: HemMenuItem.Option) {
//                    binding.drawerLayout.closeDrawers()
//                    handleHemMenuItemClick(option.id)
//                }
//
//                override fun onExpandableClicked(option: HemMenuItem.ExpandableOption) {
//                    // Expand/collapse handled in adapter
//                }
//            })
//        binding.layoutHemMenuDrawer.recyclerHemMenu.layoutManager = LinearLayoutManager(this)
//        binding.layoutHemMenuDrawer.recyclerHemMenu.adapter = hemMenuAdapter
//    }
//
//    fun getHemMenuOptions(context: Context): MutableList<HemMenuItem> {
//        val menuItems = mutableListOf<HemMenuItem>()
//
//        //Home
//        menuItems.add(
//            HemMenuItem.Option(
//                HEM_MAIN_MENU_HOME,
//                getString(R.string.scr_lbl_home),
//                getString(R.string.ada_content_description_home_menu_option),
//                R.drawable.ic_home
//            )
//        )
//
//        //Ticketing
//        val subMenuItemsForTicketing = mutableListOf<HemMenuItem.Option>()
//        subMenuItemsForTicketing.add(
//            HemMenuItem.Option(
//                HEM_MENU_ISSUE,
//                getString(R.string.scr_lbl_issue),
//                getString(R.string.ada_content_description_issue_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForTicketing.add(
//            HemMenuItem.Option(
//                HEM_MENU_SCAN,
//                getString(R.string.scr_btn_scan),
//                getString(R.string.ada_content_description_scan_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForTicketing.add(
//            HemMenuItem.Option(
//                HEM_MENU_SCAN_STICKER,
//                getString(R.string.btn_text_scan_sticker),
//                getString(R.string.ada_content_description_scan_sticker_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForTicketing.add(
//            HemMenuItem.Option(
//                HEM_MENU_LPR_MODE,
//                getString(R.string.scr_lbl_lpr_mode),
//                getString(R.string.ada_content_description_lpr_mode_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForTicketing.add(
//            HemMenuItem.Option(
//                HEM_MENU_MUNICIPAL_CITATION,
//                getString(R.string.scr_btn_municipal_citation),
//                getString(R.string.ada_content_description_municipal_citation_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForTicketing.add(
//            HemMenuItem.Option(
//                HEM_MENU_OWNER_BILL,
//                getString(R.string.scr_btn_owner_bill),
//                getString(R.string.ada_content_description_municipal_citation_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//
//        menuItems.add(
//            HemMenuItem.ExpandableOption(
//                HEM_MAIN_MENU_TICKETING,
//                getString(R.string.scr_lbl_ticketing),
//                getString(R.string.ada_content_description_ticketing_menu_option),
//                R.drawable.ic_ticket_blue,
//                subOptions = subMenuItemsForTicketing
//            )
//        )
//
//        //My Activity
//        val subMenuItemsForMyActivity = mutableListOf<HemMenuItem.Option>()
//        subMenuItemsForMyActivity.add(
//            HemMenuItem.Option(
//                HEM_MENU_GRAPH_VIEW,
//                getString(R.string.scr_lbl_map_view),
//                getString(R.string.ada_content_description_graph_view_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForMyActivity.add(
//            HemMenuItem.Option(
//                HEM_MENU_DAILY_SUMMARY,
//                getString(R.string.scr_lbl_daily_summary),
//                getString(R.string.ada_content_description_daily_summary_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForMyActivity.add(
//            HemMenuItem.Option(
//                HEM_MENU_SUPERVISOR_VIEW,
//                getString(R.string.scr_lbl_supervisor_view),
//                getString(R.string.ada_content_description_supervisor_view_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForMyActivity.add(
//            HemMenuItem.Option(
//                HEM_MENU_LPR_HITS,
//                getString(R.string.scr_lbl_lpr_hits),
//                getString(R.string.ada_content_description_lpr_hits_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//
//        menuItems.add(
//            HemMenuItem.ExpandableOption(
//                HEM_MAIN_MENU_MY_ACTIVITY,
//                getString(R.string.scr_lbl_my_activity),
//                getString(R.string.ada_content_description_my_activity_menu_option),
//                R.drawable.ic_my_activity,
//                subOptions = subMenuItemsForMyActivity
//            )
//        )
//
//        //Lookup
//        val subMenuItemsForLookUp = mutableListOf<HemMenuItem.Option>()
//        subMenuItemsForLookUp.add(
//            HemMenuItem.Option(
//                HEM_MENU_CITATION_RESULT,
//                getString(R.string.scr_lbl_citation_result),
//                getString(R.string.ada_content_description_citation_result_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForLookUp.add(
//            HemMenuItem.Option(
//                HEM_MENU_LPR_RESULT,
//                getString(R.string.scr_lbl_lpr_result),
//                getString(R.string.ada_content_description_lpr_result_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//
//        menuItems.add(
//            HemMenuItem.ExpandableOption(
//                HEM_MAIN_MENU_LOOKUP,
//                getString(R.string.scr_lbl_lookup),
//                getString(R.string.ada_content_description_lookup_menu_option),
//                R.drawable.lookup_icon,
//                subOptions = subMenuItemsForLookUp
//            )
//        )
//
//        //Guide Enforcement
//        val subMenuItemsForGuideEnforcement = mutableListOf<HemMenuItem.Option>()
//        subMenuItemsForGuideEnforcement.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_BY_PLATE,
//                getString(R.string.has_pay_by_plate),
//                getString(R.string.ada_content_description_pay_by_plate_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForGuideEnforcement.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_BY_SPACE,
//                getString(R.string.has_pay_by_space),
//                getString(R.string.ada_content_description_pay_by_space_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForGuideEnforcement.add(
//            HemMenuItem.Option(
//                HEM_MENU_CAMERA_FEED,
//                getString(R.string.has_camera_violation),
//                getString(R.string.ada_content_description_camera_feed_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForGuideEnforcement.add(
//            HemMenuItem.Option(
//                HEM_MENU_DIRECTED_ENFORCEMENT,
//                getString(R.string.lbl_directed_enforcement),
//                getString(R.string.ada_content_description_directed_enforcement),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForGuideEnforcement.add(
//            HemMenuItem.Option(
//                HEM_MENU_GENETIC_LIST,
//                getString(R.string.has_camera_genetic_list),
//                getString(R.string.ada_content_description_camera_feed_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//
//        menuItems.add(
//            HemMenuItem.ExpandableOption(
//                HEM_MAIN_MENU_GUIDE_ENFORCEMENT,
//                getString(R.string.scr_lbl_guide_enforcement),
//                getString(R.string.ada_content_description_guide_enforcement_menu_option),
//                R.drawable.enforcement_icon,
//                subOptions = subMenuItemsForGuideEnforcement
//            )
//        )
//
//        //Settings
//        menuItems.add(
//            HemMenuItem.Option(
//                HEM_MAIN_MENU_SETTING,
//                getString(R.string.scr_lbl_setting),
//                getString(R.string.ada_content_description_setting_menu_option),
//                R.drawable.ic_setting
//            )
//        )
//
//
//        //Broken Asset Reports
//        menuItems.add(
//            HemMenuItem.Option(
//                HEM_MAIN_MENU_BROKEN_ASSET_REPORTS,
//                getString(R.string.scr_lbl_broken_asset_report),
//                getString(R.string.ada_content_description_broken_asset_report_menu_option),
//                R.drawable.ic_report
//            )
//        )
//
//
//        //Hot Restart
//        menuItems.add(
//            HemMenuItem.Option(
//                HEM_MAIN_MENU_HOT_RESTART,
//                getString(R.string.scr_lbl_hot_restart),
//                getString(R.string.ada_content_description_hot_restart_menu_option),
//                R.drawable.ic_baseline_model_training_24
//            )
//        )
//
//
//        //Report Service
//        val subMenuItemsForReportService = mutableListOf<HemMenuItem.Option>()
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_BROKEN_METER_REPORT,
//                getString(R.string.scr_lbl_broken_meter_report),
//                getString(R.string.ada_content_description_broken_meter_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_CURB_REPORT,
//                getString(R.string.scr_lbl_curb_report),
//                getString(R.string.ada_content_description_curb_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_EOW_OFFICER_REPORT,
//                getString(R.string.scr_lbl_EOW_Officer_report),
//                getString(R.string.ada_content_description_full_time_eow_officer_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_PART_EOW_OFFICER_REPORT,
//                getString(R.string.scr_lbl_part_EOW_Officer_report),
//                getString(R.string.ada_content_description_part_time_eow_officer_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_EOW_SUPERVISOR_SHIFT_REPORT,
//                getString(R.string.scr_lbl_eow_supervisor_shift_report),
//                getString(R.string.ada_content_description_eow_supervisor_shift_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_SPECIAL_ASSIGNMENT_REPORT,
//                getString(R.string.scr_lbl_special_assignment_report),
//                getString(R.string.ada_content_description_special_assignment_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_HAND_HELD_MALFUNCTION_REPORT,
//                getString(R.string.scr_lbl_hand_held_malfunction_report),
//                getString(R.string.ada_content_description_hand_held_malfunction_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_SIGN_REPORT,
//                getString(R.string.scr_lbl_sign_report),
//                getString(R.string.ada_content_description_sign_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_VEHICLE_INSPECTION_REPORT,
//                getString(R.string.scr_lbl_vehicle_inspection_report),
//                getString(R.string.ada_content_description_vehicle_inspection_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_72_HOURS_MARKED_VEHICLE_REPORT,
//                getString(R.string.scr_lbl_72_hour_marked_vehicle_report),
//                getString(R.string.ada_content_description_72_hour_marked_vehicles_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_BIKE_INSPECTION_REPORT,
//                getString(R.string.scr_lbl_bike_inspection_report),
//                getString(R.string.ada_content_description_bike_inspection_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_72_HOURS_NOTICE_TOW_REPORT,
//                getString(R.string.scr_lbl_72hr_notice_tow_report),
//                getString(R.string.ada_content_description_72_hours_notice_to_tow_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_TOW_REPORT,
//                getString(R.string.scr_lbl_tow_report),
//                getString(R.string.ada_content_description_tow_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_SIGN_OFF_REPORT,
//                getString(R.string.scr_lbl_sign_off_report),
//                getString(R.string.ada_content_description_sign_off_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_NFL_REPORT,
//                getString(R.string.scr_lbl_nfl_report),
//                getString(R.string.ada_content_description_nfl_special_assignment_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_HARD_REPORT,
//                getString(R.string.scr_lbl_hard_report),
//                getString(R.string.ada_content_description_special_event_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_AFTER_SEVEN_REPORT,
//                getString(R.string.scr_lbl_after_seven_report),
//                getString(R.string.ada_content_description_pch_daily_updates_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_STATION_REPORT,
//                getString(R.string.scr_lbl_pay_station_report),
//                getString(R.string.ada_content_description_pay_station_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_SIGNAGE_REPORT,
//                getString(R.string.scr_lbl_pay_signage_report),
//                getString(R.string.ada_content_description_signage_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_HOMELESS_REPORT,
//                getString(R.string.scr_lbl_pay_homeless_report),
//                getString(R.string.ada_content_description_homeless_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_SAFETY_REPORT,
//                getString(R.string.scr_lbl_pay_safety_report),
//                getString(R.string.ada_content_description_safety_report_immediate_attention_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_TRASH_REPORT,
//                getString(R.string.scr_lbl_pay_trash_report),
//                getString(R.string.ada_content_description_trash_lot_maintenance_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_LOT_REPORT,
//                getString(R.string.scr_lbl_pay_lot_report),
//                getString(R.string.ada_content_description_lot_count_vio_rate_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_PAY_LOT_INSPECTION_REPORT,
//                getString(R.string.scr_lbl_pay_lot_inspection_report),
//                getString(R.string.ada_content_description_lot_inspection_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//        subMenuItemsForReportService.add(
//            HemMenuItem.Option(
//                HEM_MENU_WORK_ORDER_REPORT,
//                getString(R.string.scr_lbl_work_order_report),
//                getString(R.string.ada_content_description_work_order_report_sub_menu_option),
//                android.R.drawable.ic_menu_info_details
//            )
//        )
//
//        menuItems.add(
//            HemMenuItem.ExpandableOption(
//                HEM_MAIN_MENU_REPORT_SERVICE,
//                getString(R.string.scr_lbl_report),
//                getString(R.string.ada_content_description_report_service_menu_option),
//                R.drawable.ic_baseline_report_problem_24,
//                subOptions = subMenuItemsForReportService
//            )
//        )
//
//        if (LogUtil.isShowQRCodeOptionInHemMenu) {
//            //QR Code
//            val subMenuItemsForQRCode = mutableListOf<HemMenuItem.Option>()
//            subMenuItemsForQRCode.add(
//                HemMenuItem.Option(
//                    HEM_MENU_GENERATE_QR_CODE,
//                    getString(R.string.scr_lbl_generate_qr_code_1),
//                    getString(R.string.ada_content_description_generate_qr_code_sub_menu_option),
//                    android.R.drawable.ic_menu_info_details
//                )
//            )
//            subMenuItemsForQRCode.add(
//                HemMenuItem.Option(
//                    HEM_MENU_SCAN_QR_CODE,
//                    getString(R.string.scr_lbl_scan_qr_code),
//                    getString(R.string.ada_content_description_scan_qr_code_sub_menu_option),
//                    android.R.drawable.ic_menu_info_details
//                )
//            )
//
//            menuItems.add(
//                HemMenuItem.ExpandableOption(
//                    HEM_MAIN_MENU_QR_CODE,
//                    getString(R.string.scr_lbl_generate_qr_code),
//                    getString(R.string.ada_content_description_qr_code_menu_option),
//                    R.drawable.baseline_qr_code_24,
//                    subOptions = subMenuItemsForQRCode
//                )
//            )
//        }
//
//
//        //XF 2T printer
//        if (LogUtil.getPrinterTypeForPrint() == XF2T_PRINTER) {
//            //Paper Feed
//            menuItems.add(
//                HemMenuItem.Option(
//                    HEM_MAIN_MENU_PAPER_FEED,
//                    getString(R.string.scr_btn_paper_feed),
//                    getString(R.string.ada_content_description_paper_feed_menu_option),
//                    R.drawable.baseline_feed_24
//                )
//            )
//        }
//
//        return menuItems
//    }
//
//    private fun handleHemMenuItemClick(hemMenuId: Int) {
//        when (hemMenuId) {
//
//            HEM_MAIN_MENU_HOME -> {
//                navigateToWelcomeScreen(isFromLaunch = false)
//            }
//
//            HEM_MENU_GRAPH_VIEW -> {
//                navigateToGraphViewScreen()
//            }
//
//            HEM_MAIN_MENU_SETTING -> {
//                navigateToSettingsScreen()
//            }
//
//            HEM_MAIN_MENU_BROKEN_ASSET_REPORTS -> {
//                navigateToBrokenAssetReportScreen()
//            }
//
//            //Start of Report Service
//            HEM_MENU_BROKEN_METER_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_BROKEN_METER_REPORT)
//            }
//
//            HEM_MENU_CURB_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_CURB_REPORT)
//            }
//
//            HEM_MENU_EOW_OFFICER_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_EOW_OFFICER_REPORT)
//            }
//
//            HEM_MENU_PART_EOW_OFFICER_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_PART_EOW_OFFICER_REPORT)
//            }
//
//            HEM_MENU_EOW_SUPERVISOR_SHIFT_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_EOW_SUPERVISOR_SHIFT_REPORT)
//            }
//
//            HEM_MENU_SPECIAL_ASSIGNMENT_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_SPECIAL_ASSIGNMENT_REPORT)
//            }
//
//            HEM_MENU_HAND_HELD_MALFUNCTION_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_HAND_HELD_MALFUNCTION_REPORT)
//            }
//
//            HEM_MENU_SIGN_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_SIGN_REPORT)
//            }
//
//            HEM_MENU_VEHICLE_INSPECTION_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_VEHICLE_INSPECTION_REPORT)
//            }
//
//            HEM_MENU_72_HOURS_MARKED_VEHICLE_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_72_HOURS_MARKED_VEHICLE_REPORT)
//            }
//
//            HEM_MENU_BIKE_INSPECTION_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_BIKE_INSPECTION_REPORT)
//            }
//
//            HEM_MENU_72_HOURS_NOTICE_TOW_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT)
//            }
//
//            HEM_MENU_TOW_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_TOW_REPORT)
//            }
//
//            HEM_MENU_SIGN_OFF_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_SIGN_OFF_REPORT)
//            }
//
//            HEM_MENU_NFL_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_NFL_REPORT)
//            }
//
//            HEM_MENU_HARD_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_HARD_REPORT)
//            }
//
//            HEM_MENU_AFTER_SEVEN_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_AFTER_SEVEN_REPORT)
//            }
//
//            HEM_MENU_PAY_STATION_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_PAY_STATION_REPORT)
//            }
//
//            HEM_MENU_PAY_SIGNAGE_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_PAY_SIGNAGE_REPORT)
//            }
//
//            HEM_MENU_PAY_HOMELESS_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_PAY_HOMELESS_REPORT)
//            }
//
//            HEM_MENU_PAY_SAFETY_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_PAY_SAFETY_REPORT)
//            }
//
//            HEM_MENU_PAY_TRASH_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_PAY_TRASH_REPORT)
//            }
//
//            HEM_MENU_PAY_LOT_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_PAY_LOT_REPORT)
//            }
//
//            HEM_MENU_PAY_LOT_INSPECTION_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_PAY_LOT_INSPECTION_REPORT)
//            }
//
//            HEM_MENU_WORK_ORDER_REPORT -> {
//                navigateToReportsScreen(REPORT_TYPE_WORK_ORDER_REPORT)
//            }
//            //End of Report Service
//
//            HEM_MAIN_MENU_PAPER_FEED -> {
//                if (LogUtil.getPrinterTypeForPrint() == XF2T_PRINTER) {
//                    var xfPrinterUseCase: XfPrinterUseCase? = null
//                    xfPrinterUseCase = XfPrinterUseCase(this)
//                    xfPrinterUseCase.initialize(this@MainActivity)
//                    xfPrinterUseCase.printerFeedButton()
//                }
//            }
//        }
//    }
//    // End of Hem Menu Setup
//
//    //Start of Screen Navigation Methods
//    private fun navigateToLoginScreen() {
//        // Navigate to LoginScreenFragment using Navigation Component
////        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
////        val navController = navHostFragment.navController
//        findNav(R.id.nav_host_fragment).navigate(R.id.action_splashScreenFragment_to_loginScreenFragment)
//    }
//
//    private fun navigateToWelcomeScreen(isFromLaunch:Boolean) {
//        if (isFromLaunch){
//            findNav(R.id.nav_host_fragment).navigate(R.id.action_splashScreenFragment_to_welcomeScreenFragment)
//        }else{
//            //clear all fragment & load welcome screen fragment
//            val navController = findNav(R.id.nav_host_fragment)
//            if (navController.currentDestination?.id != R.id.welcomeScreenFragment) {
//                navController.navigate(
//                    R.id.welcomeScreenFragment,
//                    null,
//                    NavOptions.Builder()
//                        .setPopUpTo(R.id.nav_graph, true) // clears everything up to the root
//                        //.setLaunchSingleTop(true)
//                        .build()
//                )
//            }
//        }
//    }
//
//    private fun navigateToGraphViewScreen() {
//        val navController = findNavController(R.id.nav_host_fragment)
//        navController.navigate(R.id.myActivityScreenFragment)
//    }
//
//    private fun navigateToSettingsScreen() {
//        val navController = findNavController(R.id.nav_host_fragment)
//        navController.navigate(R.id.settingsScreenFragment)
//    }
//
//    private fun navigateToBrokenAssetReportScreen() {
//        val navController = findNavController(R.id.nav_host_fragment)
//        navController.navigate(R.id.brokenAssetReportScreenFragment)
//    }
//
//    private fun navigateToReportsScreen(reportType: String) {
//        val bundle = bundleOf(BUNDLE_KEY_REPORT_TYPE to reportType)
//        val navController = findNavController(R.id.nav_host_fragment)
//        navController.navigate(R.id.allReportsScreenFragment, bundle)
//    }
//    // End of Screen Navigation Methods
//
//
//
//    //Receiver for broadcasts sent by [NewBackgroundLocationUpdateService].
//    inner class MyReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            location = intent.getParcelableExtra(
//                NewBackgroundLocationUpdateService.EXTRA_LOCATION, Location::class.java
//            )
//            if (location != null) {
//
//                logD("lat base", location?.latitude.toString() + "")
//                logD("long base", location?.longitude.toString() + "")
//
//                sharedPreference.write(SharedPrefKey.LAT, location?.latitude.toString())
//                sharedPreference.write(SharedPrefKey.LONG, location?.longitude.toString())
//
//                val mLocationUpdateRequest = LocUpdateRequest()
//                mLocationUpdateRequest.activityType = Constants.ACTIVITY_TYPE_LOCATION_UPDATE
//                mLocationUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
//                mLocationUpdateRequest.locationUpdateType = Constants.LOCATION_UPDATE_TYPE_REGULAR
//                mLocationUpdateRequest.latitude = location?.latitude
//                mLocationUpdateRequest.longitude = location?.longitude
//
//                try {
//                    mLocationUpdateRequest.clientTimestamp = DateTimeUtils.getClientTimestamp()
//                } catch (e: ParseException) {
//                    e.printStackTrace()
//                }
//
//                if (mTimeStatus) {
//                    mTimeStatus = false
//                    val mLat = sharedPreference.read(
//                        SharedPrefKey.PRE_LAT,
//                        DEFAULT_VALUE_ZERO_DOT_ZERO_STR
//                    )?.toDouble()
//                    val mLong = sharedPreference.read(
//                        SharedPrefKey.PRE_LONG,
//                        DEFAULT_VALUE_ZERO_DOT_ZERO_STR
//                    )?.toDouble()
//
//                    if (mLat != location?.latitude && mLong != location?.longitude || mLat == DEFAULT_VALUE_ZERO_DOT_ZERO_DBL && mLong == DEFAULT_VALUE_ZERO_DOT_ZERO_DBL) {
//                        sharedPreference.write(SharedPrefKey.PRE_LAT, location?.latitude.toString())
//                        sharedPreference.write(
//                            SharedPrefKey.PRE_LONG, location?.longitude.toString()
//                        )
//
//                        mainActivityViewModel.callLocationUpdateAPI(mLocationUpdateRequest)
//
//                        isMeterActivteAPI()
//
//                        logD(
//                            "location_base",
//                            location?.latitude.toString() + " " + location?.longitude
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    private fun getMyReceiver(): MyReceiver = myReceiver ?: MyReceiver().also { myReceiver = it }
//
//    //dialog to enable location service
//    private fun openGPSStatusDialog() {
//        AlertDialogUtils.showDialog(
//            context = this@MainActivity,
//            title = getString(R.string.error_title_gps_service),
//            message = getString(
//                R.string.find_your_location_service_turn_on_services
//            ),
//            positiveButtonText = getString(R.string.button_text_turn_on),
//            listener = object : AlertDialogListener {
//                override fun onPositiveButtonClicked() {
//                    //refresh Activity
//                    lifecycleScope.launch {
//                        delay(TIME_INTERVAL_5_SECONDS)
//                        if (isGPSEnabled()) {
//                            startLocationService()
//                        }
//                    }
//                }
//            })
//    }
//
//    val REQUEST_CHECK_SETTINGS = 0x1
//
//    //sending GPS request
//    fun settingsRequest() {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        mSettingsClient = LocationServices.getSettingsClient(this)
//
//        // Build a sensible LocationRequest using the new Builder API (non-deprecated)
//        mLocationRequest = LocationRequest.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY, Constants.INTERVAL
//        ).setMinUpdateIntervalMillis(Constants.FASTEST_INTERVAL).setWaitForAccurateLocation(false)
//            .build()
//
//        val settingsRequest = mLocationRequest?.let {
//            LocationSettingsRequest.Builder().addLocationRequest(it)
//                .setAlwaysShow(true) // shows dialog if location is off
//                .build()
//        }
//
//
//        val settingsClient = LocationServices.getSettingsClient(this@MainActivity)
//        val task = settingsRequest?.let { settingsClient.checkLocationSettings(it) }
//
//        task?.addOnSuccessListener {
//            //All location settings are satisfied
//            //onEnabled()
//            if (isGPSEnabled()) {
//                startLocationService()
//            }
//        }
//
//        task?.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                //how dialog to enable GPS
//                settingsRequest()
//                //onResolutionRequired(exception.resolution)
//            } else {
//                //Settings can’t be changed programmatically
//                //onNotAvailable()
//            }
//        }
//
//
////        if (googleApiClient == null) {
////            googleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API)
////                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()
////            googleApiClient?.connect()
////        }
////        val locationRequest = LocationRequest.create()
////        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
////        locationRequest.interval = Constants.INTERVAL
////        locationRequest.fastestInterval = Constants.FASTEST_INTERVAL
////        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
////        builder.setAlwaysShow(true) //this is the key ingredient
////
////        val result =
////            LocationServices.SettingsApi.checkLocationSettings(googleApiClient!!, builder.build())
////        result.setResultCallback(object : ResultCallback<LocationSettingsResult?> {
////            override fun onResult(result: LocationSettingsResult) {
////                val status = result?.status
////                val state = result?.locationSettingsStates
////                when (status?.statusCode) {
////                    LocationSettingsStatusCodes.SUCCESS -> logD(
////                        "TAG", "setResultCallback: " + LocationSettingsStatusCodes.SUCCESS
////                    )
////
////                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
////                        logD(
////                            "TAG",
////                            "setResultCallback: " + LocationSettingsStatusCodes.RESOLUTION_REQUIRED
////                        )
////
////                        // Location settings are not satisfied. But could be fixed by showing the user
////                        // a dialog.
////                        try {
////                            // Show the dialog by calling startResolutionForResult(),
////                            // and check the result in onActivityResult().
////                            status.startResolutionForResult(
////                                this@MainActivity, REQUEST_CHECK_SETTINGS
////                            )
////                        } catch (e: SendIntentException) {
////                            // Ignore the error.
////                        }
////                    }
////
////                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> logD(
////                        "TAG",
////                        "setResultCallback: " + LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
////                    )
////                }
////
////            }
////        })
//    }
//
//
//    private fun handleBackButtonTrigger() {
//        // Check global block flag
//        if (backBlockedLocal) return
//
//        // If drawer is open — close it instead of navigating back
//        binding.drawerLayout.let { drawer ->
//            if (drawer.isDrawerOpen(GravityCompat.END)) {
//                drawer.closeDrawer(GravityCompat.END)
//                return
//            }
//        }
//
//        // Check nav destination
//        val navController = findNavController(R.id.nav_host_fragment)
//
//        if (navController.currentDestination?.id == R.id.welcomeScreenFragment) {
//            AlertDialogUtils.showDialog(
//                context = this@MainActivity,
//                icon = R.drawable.icon_exlamation,
//                title = getString(R.string.dialog_title_close_app),
//                message = getString(R.string.dialog_desc_close_app),
//                positiveButtonText = getString(R.string.button_text_yes),
//                negativeButtonText = getString(R.string.button_text_no),
//                listener = object : AlertDialogListener {
//                    override fun onPositiveButtonClicked() {
//                        navController.popBackStack()
//                    }
//                })
//            return
//        } else if (navController.currentDestination?.id == R.id.scanResultScreenFragment) {
//            // Navigate to Welcome Screen
//            navigateToWelcomeScreen(isFromLaunch = false)
//            return
//        } else if (navController.currentDestination?.id == R.id.citationFormScreenFragment) {
//            // Navigate to Welcome Screen
//            mainActivityViewModel.deleteTempImages()
//        }
//
//        // Default back behavior
//        val popped = navController.popBackStack()
//        if (!popped) finish()
//    }
//
//    //Start of Wake Lock Methods
//    @SuppressLint("WakelockTimeout")
//    private fun acquireWakeLock() {
//        val pm = ContextCompat.getSystemService(this, PowerManager::class.java) ?: return
//        mWakeLock = (mWakeLock ?: pm.newWakeLock(
//            PowerManager.PARTIAL_WAKE_LOCK, "${javaClass.simpleName}:WakeLock"
//        )).apply {
//            if (!isHeld) {
//                // use timed acquire to prevent permanent wakelock leaks
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    acquire()
//                } else {
//                    // older APIs still support acquire(timeout)
//                    @Suppress("DEPRECATION") acquire()
//                }
//            }
//        }
//    }
//
//    private fun releaseWakeLock() {
//        mWakeLock?.takeIf { it.isHeld }?.release()
//        mWakeLock = null
//    }
//    // End of Wake Lock Methods
//
//    //Start of Lifecycle Methods
//    override fun onStart() {
//        super.onStart()
//    }
//
//    @SuppressLint("WakelockTimeout")
//    override fun onResume() {
//        super.onResume()
//
//        if (!isGPSEnabled()) {
//            settingsRequest()
//        }
//
//        binding.drawerLayout.let {
//            if (it.isDrawerOpen(GravityCompat.START)) it.closeDrawer(GravityCompat.START)
//        }
//
//        LogsFile.writeFileOnInternalStorage(this, this::class.java.simpleName)
//
//        acquireWakeLock()
//
//        if (this.isGPSEnabled() && !AppUtils.isServiceRunning(
//                "NewBackgroundLocationUpdateService", this
//            ) && !isFinishing
//        ) {
//            startLocationService()
//        }
//
//    }
//
//    override fun onPause() {
//        super.onPause()
//        releaseWakeLock()
//    }
//
//    override fun onDestroy() {
//        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(getMyReceiver())
//
//        super.onDestroy()
//
//        //stop location service
//        stopService(Intent(this, NewBackgroundLocationUpdateService::class.java))
//
//        legacyBackCallback?.remove()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            predictiveCallback?.let { onBackInvokedDispatcher.unregisterOnBackInvokedCallback(it) }
//        }
//    }
//    //End of Lifecycle Methods
//
//    //starting foreground service and registering broadcast for lat long
//    private fun startLocationService() {
//        lifecycleScope.launch {
//            permissionManager.ensurePermissionsThen(
//                permissions = PermissionUtils.getLocationPermissions(),
//                rationaleMessage = getString(R.string.permission_message_all_permission_required_to_login)
//            ) {
//                startService(
//                    Intent(
//                        this@MainActivity,
//                        NewBackgroundLocationUpdateService::class.java
//                    )
//                )
//                LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
//                    myReceiver!!, IntentFilter(NewBackgroundLocationUpdateService.ACTION_BROADCAST)
//                )
//            }
//        }
//    }
//
//
//    //only get status 0 citation which is facsimile not upload, this method will be called once app is launched from welcome screen
//    private fun processUnUploadedActivityImages() {
//        lifecycleScope.launch {
//            val activityList = try {
//                withContext(Dispatchers.IO) {
//                    try {
//                        mainActivityViewModel.getActivityImageData()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        null
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            } ?: return@launch
//
//            try {
//                var candidate = activityList.firstOrNull() ?: return@launch
//                if (candidate.image1.isEmpty() && activityList.isNotEmpty()) {
//                    candidate = activityList.last() as ActivityImageTable
//                }
//
//                // update activity id in payload
//                activityImageUpdateRequest.activityId = candidate.activityResponseId
//
//                // proceed only when uploadStatus explicitly "false"
//                if (candidate.uploadStatus.nullSafety()
//                        .isNotEmpty() && candidate.uploadStatus == STR_FALSE
//                ) {
//                    var addedCount = 0
//                    listOf(candidate.image1, candidate.image2, candidate.image3).forEach { img ->
//                        if (img.isNotEmpty()) {
//                            addedCount++
//                            val fac = UnUploadFacsimileImage().apply {
//                                imagePath = img
//                                uploadedCitationId = candidate.activityResponseId
//                            }
//                            resultFacsimileImage = fac
//                            callUploadImagesAPI(fac, FOLDER_ACTIVITY_IMAGES)
//                        }
//                    }
//                    mActivityImageCount = (mActivityImageCount ?: 0) + addedCount
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    // to be called from Welcome Activity
//    fun saveActivityLogData(
//        bannerList: MutableList<TimingImagesModel?>? = ArrayList(), activity_id: String
//    ) {
//        lifecycleScope.launch {
//            withContext(Dispatchers.IO) {
//                try {
//                    val model = ActivityImageTable().apply {
//                        activityResponseId = activity_id
//                        id = getRandomID()
//
//                        bannerList?.getOrNull(0)?.timingImage?.toString()?.let {
//                            if (it.isNotEmpty()) image1 = it
//                        }
//                        bannerList?.getOrNull(1)?.timingImage?.toString()?.let {
//                            if (it.isNotEmpty()) image2 = it
//                        }
//                        bannerList?.getOrNull(2)?.timingImage?.toString()?.let {
//                            if (it.isNotEmpty()) image3 = it
//                        }
//                    }
//
//                    mainActivityViewModel.insertActivityImageData(model)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//
//
//
//
//    //End of Setting File Methods
//    fun checkMissingFacsimile() {
//        try {
//            val fac = mainActivityViewModel.getUnUploadFacsimile() ?: return
//            val hasCitation = fac.uploadedCitationId.nullSafety().isNotEmpty()
//            val isFacsimile = fac.imageType?.equals(UNUPLOAD_IMAGE_TYPE_FACSIMILE, true) == true
//
//            if (hasCitation && isFacsimile) {
//                val title = getString(R.string.error_title_up_upload_facsimile_alert)
//                val message = listOf(title, fac.lprNumber.nullSafety()).filter { it.isNotEmpty() }
//                    .joinToString(" ")
//
//                AlertDialogUtils.showDialog(
//                    context = this@MainActivity,
//                    icon = R.drawable.icon_warning,
//                    title = title,
//                    message = message,
//                    positiveButtonText = getString(R.string.button_text_yes),
//                    negativeButtonText = getString(R.string.button_text_no),
//                    listener = object : AlertDialogListener {
//                        override fun onPositiveButtonClicked() {
//                            startActivity(
//                                Intent(
//                                    this@MainActivity, MissingFacsimileActivity::class.java
//                                )
//                            )
//                        }
//                    })
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun deleteImages() {
//        try {
//            //delete temporary images list
//            mainActivityViewModel.deleteTempImages()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun processUnUploadedFacsimileImages() {
//        lifecycleScope.launch {
//            val facsimile = try {
//                withContext(Dispatchers.IO) {
//                    try {
//                        mainActivityViewModel.getUnUploadFacsimile()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        null
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//
//            try {
//                facsimile?.let { item ->
//                    if (!item.imageLink.isNullOrEmpty()) {
//                        resultFacsimileImage = item
//                        mFacsimileImagesLink.add(item.imageLink.nullSafety())
//                        callUploadImagesUrlAPI()
//                    } else {
//                        callUploadImagesAPI(item, "CitationImages")
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//    //API Calls
//    fun isMeterActivteAPI() {
//        lifecycleScope.launch {
//            try {
//                if (mainActivityViewModel.getMeterBuzzerAPICallStatusFormSettingFile()) {
//                    val inactiveMeterBuzzerRequest = InactiveMeterBuzzerRequest()
//                    inactiveMeterBuzzerRequest.latitude = location?.latitude.toString()
//                    inactiveMeterBuzzerRequest.longitude = location?.longitude.toString()
//                    mainActivityViewModel.callInactiveMeterBuzzerAPI(
//                        inactiveMeterBuzzerRequest
//                    )
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun callUploadImagesAPI(result: UnUploadFacsimileImage, folderName: String) {
//        resultFacsimileImage = result
//        val file: File? = File(result!!.imagePath)
//        val num: Int = (result.imageCount + 1)
//        if (NetworkCheck.isInternetAvailable(this@MainActivity)) {
//            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
//            val files = MultipartBody.Part.createFormData(
//                "files", if (file != null) file.name else "", requestFile
//            )
//            if (folderName.equals("CitationImages")) {
//                typeOfImageApiCall = CITATION_IMAGE_API_CALLED
//                var mDropdownList =
//                    if (file!!.name.contains("_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
//                        arrayOf(result!!.uploadedCitationId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
//                    } else {
//                        arrayOf(result!!.uploadedCitationId + "_" + num + "_" + result!!.dateTime)
//                    }
//                val mRequestBodyType =
//                    RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
//                mainActivityViewModel?.callUploadImageAPI(
//                    mDropdownList, mRequestBodyType, files
//                )
//            } else {
//                val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
//                typeOfImageApiCall = ACTIVITY_IMAGE_API_CALLED
//                var mDropdownList = arrayOf(id + "_" + Random().nextInt(100) + "_activity_image")
//                val mRequestBodyType =
//                    RequestBody.create("text/plain".toMediaTypeOrNull(), "ActivityImages")
//                mainActivityViewModel?.callUploadImageAPI(
//                    mDropdownList, mRequestBodyType, files
//                )
//            }
//        } else {
//            LogUtil.printToastMSG(
//                applicationContext, getString(R.string.err_msg_connection_was_refused)
//            )
//        }
//    }
//
//
//    //Call Api For add un-upload images
//    private fun callUploadImagesUrlAPI() {
//        if (!isInternetAvailable()) {
//            NoInternetDialogUtil.showDialog(
//                context = this@MainActivity,
//                positiveButtonText = getString(R.string.button_text_ok),
//                negativeButtonText = getString(R.string.button_text_retry),
//                listener = object : NoInternetDialogListener {
//                    override fun onNegativeButtonClicked() {
//                        callUploadImagesUrlAPI()
//                    }
//                })
//            return
//        }
//
//        val uploadId =
//            resultFacsimileImage?.uploadedCitationId.nullSafety().takeIf { it.isNotBlank() }
//                ?: return
//
//        if (mFacsimileImagesLink.isEmpty()) return
//
//        val addImageRequest = AddImageRequest().apply { images = mFacsimileImagesLink }
//
//        runCatching {
//            mainActivityViewModel.callAddImageAPI(uploadId, addImageRequest)
//        }.onFailure { it.printStackTrace() }
//    }
//
//
//    //Call Api For add un-upload images
//    private fun callUploadActivityImagesUrlAPI() {
//        if (!isInternetAvailable()) {
//            NoInternetDialogUtil.showDialog(
//                context = this@MainActivity,
//                positiveButtonText = getString(R.string.button_text_ok),
//                negativeButtonText = getString(R.string.button_text_retry),
//                listener = object : NoInternetDialogListener {
//                    override fun onNegativeButtonClicked() {
//                        callUploadActivityImagesUrlAPI()
//                    }
//                })
//            return
//        }
//
//        runCatching {
//            mainActivityViewModel.callUploadActivityImageAPI(activityImageUpdateRequest)
//        }.onFailure { it.printStackTrace() }
//    }
//
//    fun callLocationApi(locUpdateRequest: LocUpdateRequest) {
//        if (isInternetAvailable()) {
//            try {
//                locUpdateRequest.mShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
//                locUpdateRequest.mDeviceId =
//                    mainActivityViewModel.getWelcomeForm()?.officerDeviceName
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            mainActivityViewModel.callLocationUpdateAPI(locUpdateRequest = locUpdateRequest)
//        } else {
//            NoInternetDialogUtil.showDialog(
//                context = this@MainActivity,
//                positiveButtonText = getString(R.string.button_text_ok),
//                negativeButtonText = getString(R.string.button_text_retry),
//                listener = object : NoInternetDialogListener {
//                    override fun onNegativeButtonClicked() {
//                        callLocationApi(locUpdateRequest)
//                    }
//                })
//        }
//    }
//
//    @Throws(ParseException::class)
//    fun callEventActivityLogApiForBaseActivity(mValue: String, isDisplay: Boolean? = false) {
//        logD("==>Janak:-", "callEventActivityLogApiForBaseActivity called: $mValue")
//
//        val mWelcomePageData = mainActivityViewModel.getWelcomeForm()
//
//        if (isInternetAvailable()) {
//            mActivityLoggerAPITag = mValue
//            val mLat = sharedPreference.read(SharedPrefKey.LAT, DEFAULT_VALUE_ZERO_DOT_ZERO_STR)
//                ?.toDouble()
//            val mLong = sharedPreference.read(SharedPrefKey.LONG, DEFAULT_VALUE_ZERO_DOT_ZERO_STR)
//                ?.toDouble()
//            val activityUpdateRequest = ActivityUpdateRequest()
//            activityUpdateRequest.initiatorId = mWelcomePageData?.initiatorId
//            activityUpdateRequest.initiatorRole = mWelcomePageData?.initiatorRole
//            activityUpdateRequest.activityType = ACTIVITY_TYPE_ACTIVITY_UPDATE
//            activityUpdateRequest.siteId = AppUtils.getSiteId(this@MainActivity)
//            activityUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
//            activityUpdateRequest.mShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
//            try {
//                activityUpdateRequest.activityName = mValue
//            } catch (e: Exception) {
//                activityUpdateRequest.activityName = ""
//            }
//            activityUpdateRequest.latitude = mLat
//            activityUpdateRequest.longitude = mLong
//            activityUpdateRequest.clientTimestamp =
//                DateTimeUtils.getClientTimestamp(checkSettingsFile = true)
//            activityUpdateRequest.androidId = getAndroidID()
//
//            if (isDisplay.nullSafety()) {
//                activityUpdateRequest.isDisplay = true
//            }
//
//            mainActivityViewModel.callActivityLogAPI(activityUpdateRequest)
//        } else {
//            NoInternetDialogUtil.showDialog(
//                context = this@MainActivity,
//                positiveButtonText = getString(R.string.button_text_ok),
//                negativeButtonText = getString(R.string.button_text_retry),
//                listener = object : NoInternetDialogListener {
//                    override fun onNegativeButtonClicked() {
//                        callEventActivityLogApiForBaseActivity(mValue, isDisplay)
//                    }
//                })
//        }
//    }
//
//    //Call Api For update profile this fun use to upload API log file in google could**//*
//    fun callUploadAPILogsTextFile() {
//        lifecycleScope.launch {
//            try {
//                val localDir = getExternalFilesDir(Constants.FILE_NAME) ?: File(
//                    Environment.getExternalStorageDirectory().absolutePath, Constants.FILE_NAME
//                )
//
//                val welcome = mainActivityViewModel.getWelcomeForm()
//                val fileTimestamp = SDF_MM_dd_YYYY_HH_mm_ss.format(Date())
//                val fileName = "$FILE_NAME_API_PAYLOAD.txt"
//                val file = File(localDir, fileName)
//
//                if (!file.exists()) return@launch
//
//                if (!isInternetAvailable()) {
//                    LogUtil.printToastMSG(
//                        applicationContext, getString(R.string.err_msg_connection_was_refused)
//                    )
//                    return@launch
//                }
//
//                // prepare request bodies on IO
//                val requestFile = withContext(Dispatchers.IO) {
//                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                }
//
//                val displayName = buildString {
//                    append(FILE_NAME_API_PAYLOAD)
//                    append("_")
//                    append(welcome?.officerFirstName.nullSafety(""))
//                    append("_")
//                    append(welcome?.officerLastName.nullSafety(""))
//                    append("_")
//                    append(fileTimestamp)
//                    append(".txt")
//                }
//
//                val part = MultipartBody.Part.createFormData("files", displayName, requestFile)
//
//                // prepare other params
//                typeOfImageApiCall = CITATION_IMAGE_API_CALLED
//                val mDropdownList =
//                    arrayOf("${fileTimestamp}_${welcome?.officerFirstName.nullSafety("")}_API_LOGS")
//                val mRequestBodyType =
//                    "APILogAudits".toRequestBody("text/plain".toMediaTypeOrNull())
//
//                // call ViewModel API
//                mainActivityViewModel.callUploadTextFileAPI(mDropdownList, mRequestBodyType, part)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//    //API Response Consumer function for all APIs
//    private fun consumeResponse(newApiResponse: NewApiResponse<Any>) {
//        hideSoftKeyboard()
//
//        when (newApiResponse) {
//            is NewApiResponse.Idle -> {
//                DialogUtil.hideLoader()
//            }
//
//            is NewApiResponse.Loading -> {
//                DialogUtil.showLoader(context = this@MainActivity)
//            }
//
//            is NewApiResponse.Success -> {
//                DialogUtil.hideLoader()
//
//                try {
//                    when (newApiResponse.apiNameTag) {
//                        API_TAG_NAME_UPLOAD_ACTIVITY_IMAGE -> {
//                            val activityLogImageResponse = ObjectMapperProvider.fromJson(
//                                (newApiResponse.data as JsonNode).toString(),
//                                ActivityLogImageResponse::class.java
//                            )
//                            handleActivityLogImageResponse(activityLogImageResponse)
//
//                        }
//
//                        API_TAG_NAME_INACTIVE_METER_BUZZER -> {
//                            val inactiveMeterBuzzerResponse = ObjectMapperProvider.fromJson(
//                                (newApiResponse.data as JsonNode).toString(),
//                                InactiveMeterBuzzerResponse::class.java
//                            )
//
//                            handleInactiveMeterBuzzerResponse(
//                                inactiveMeterBuzzerResponse
//                            )
//                        }
//
//                        API_TAG_NAME_ACTIVITY_LOG -> {
//                            val activityLogResponse = ObjectMapperProvider.fromJson(
//                                (newApiResponse.data as JsonNode).toString(),
//                                ActivityLogResponse::class.java
//                            )
//
//                            handleActivityLogResponse(activityLogResponse)
//
//                        }
//
//                        API_TAG_NAME_UPLOAD_IMAGES -> {
//                            val uploadImagesResponse = ObjectMapperProvider.fromJson(
//                                (newApiResponse.data as JsonNode).toString(),
//                                UploadImagesResponse::class.java
//                            )
//                            handleUploadImagesResponse(uploadImagesResponse)
//
//                        }
//
//                        API_TAG_NAME_UPLOAD_TEXT_FILE -> {
//                            val textFileResponse = ObjectMapperProvider.fromJson(
//                                (newApiResponse.data as JsonNode).toString(),
//                                TextFileResponse::class.java
//                            )
//                            handleUploadTextFileResponse(textFileResponse)
//
//                        }
//
//                        API_TAG_NAME_ADD_IMAGES -> {
//                            val addNotesResponse = ObjectMapperProvider.fromJson(
//                                (newApiResponse.data as JsonNode).toString(),
//                                AddNotesResponse::class.java
//                            )
//                            handleAddNoteResponse(
//                                addNotesResponse
//                            )
//                        }
//
//                        API_TAG_NAME_ADD_NOTE_FOR_NOT_CHECKED_IN_EQUIPMENT -> {
//                            //Nothing to handle here on add note before logout
//                        }
//                    }
//                } catch (e: JsonMappingException) {
//                    toast(message = getString(R.string.error_desc_please_login_again_to_use_the_application))
//                    logout()
//                    e.printStackTrace()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            is NewApiResponse.ApiError -> {
//                DialogUtil.hideLoader()
//
//                if (newApiResponse.apiNameTag == API_TAG_NAME_ACTIVITY_LOG) {
//                    if (mActivityLoggerAPITag.equals("Logout", ignoreCase = true)) {
//                        logout()
//                    }
//                }
//                LogsFile.writeFileOnInternalStorage(this@MainActivity, this::class.java.simpleName)
//                LogsFile.writeFileOnInternalStorage(
//                    this,
//                    newApiResponse.apiNameTag + "  ###  " + newApiResponse.getErrorMessage()
//                        .nullSafety(getString(R.string.error_desc_something_went_wrong))
//                )
//
//                logD(
//                    message = newApiResponse.getErrorMessage()
//                        .nullSafety(getString(R.string.error_desc_something_went_wrong))
//                )
//            }
//
//            is NewApiResponse.NetworkError -> {
//                DialogUtil.hideLoader()
//            }
//
//            is NewApiResponse.UnknownError -> {
//                DialogUtil.hideLoader()
//            }
//        }
//    }
//
//    //Start of API Response handling
//    private fun handleActivityLogImageResponse(activityLogImageResponse: ActivityLogImageResponse) {
//        if (activityLogImageResponse.status.nullSafety()) {
//            mActivityImage.activityResponseId?.let(mainActivityViewModel::deleteActivityImageData)
//        }
//    }
//
//    private fun handleInactiveMeterBuzzerResponse(inactiveMeterBuzzerResponse: InactiveMeterBuzzerResponse) {
//        val isActive =
//            inactiveMeterBuzzerResponse.status.nullSafety() && inactiveMeterBuzzerResponse.data?.inactive.nullSafety()
//
//        sharedPreference.write(
//            SharedPrefKey.IS_METER_ACTIVE, if (isActive) "ACTIVE" else "DEACTIVE"
//        )
//
//        if (isActive) {
//            ToneGenerator(AudioManager.STREAM_MUSIC, 100).also { tg ->
//                tg.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1000)
//                Handler(Looper.getMainLooper()).postDelayed({ tg.release() }, 1100)
//            }
//
//            (getSystemService(VIBRATOR_SERVICE) as? Vibrator)?.takeIf { it.hasVibrator() }
//                ?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
//        }
//    }
//
//    private fun handleActivityLogResponse(activityLogResponse: ActivityLogResponse) {
//        runCatching {
//            if (!activityLogResponse.success.nullSafety()) return
//            if (mActivityLoggerAPITag.equals("Logout", ignoreCase = true)) logout()
//        }.onFailure { it.printStackTrace() }
//    }
//
//    private fun handleUploadImagesResponse(uploadImagesResponse: UploadImagesResponse) {
//        runCatching {
//            // ensure successful response
//            if (!uploadImagesResponse.status.nullSafety()) return
//
//            // get first link if present
//            val link =
//                uploadImagesResponse.data?.firstOrNull()?.response?.links?.firstOrNull() ?: return
//
//            when (typeOfImageApiCall) {
//                CITATION_IMAGE_API_CALLED -> {
//                    mFacsimileImagesLink.add(link)
//                    callUploadImagesUrlAPI()
//                }
//
//                ACTIVITY_IMAGE_API_CALLED -> {
//                    // decrement safely
//                    mActivityImageCount = (mActivityImageCount ?: 0) - 1
//
//                    // place link into first empty slot
//                    when {
//                        updateActivityImagePayLoad.image1.isNullOrEmpty() -> updateActivityImagePayLoad.image1 =
//                            link
//
//                        updateActivityImagePayLoad.image2.isNullOrEmpty() -> updateActivityImagePayLoad.image2 =
//                            link
//
//                        updateActivityImagePayLoad.image3.isNullOrEmpty() -> updateActivityImagePayLoad.image3 =
//                            link
//                    }
//
//                    activityImageUpdateRequest.updatePayload = updateActivityImagePayLoad
//
//                    if ((mActivityImageCount ?: 0) <= 0) {
//                        callUploadActivityImagesUrlAPI()
//                    }
//                }
//
//                else -> {
//                    // unknown type, ignore
//                }
//            }
//        }.onFailure { it.printStackTrace() }
//    }
//
//    private fun handleUploadTextFileResponse(textFileResponse: TextFileResponse) {
//        if (textFileResponse.status.nullSafety()) {
//            runCatching {
//                val localFolder =
//                    File(Environment.getExternalStorageDirectory(), Constants.FILE_NAME)
//                val file = File(localFolder, "$FILE_NAME_API_PAYLOAD.txt")
//                if (file.exists()) file.delete()
//            }.onFailure { it.printStackTrace() }
//        }
//    }
//
//    private fun handleAddNoteResponse(addNotesResponse: AddNotesResponse) {
//        if (addNotesResponse.isSuccess) {
//            resultFacsimileImage?.let { facsimile ->
//                facsimile.dateTime.let { date ->
//                    mainActivityViewModel.updateFacsimileStatus(
//                        1, facsimile.ticketNumberText.orEmpty(), date
//                    )
//                }
//            }
//        }
//    }
//    //End of API Response handling
//
//    //Function used to logout user from anywhere in the app additional parameter logoutNote only used when you have inventory module enabled & you have some equipment checked-out
//    fun logout(logoutNote: String? = null) {
//        //We have to call logout note API only when you have inventory module & when you are equipment which is not checked in yet and you are tring to logout
//        if (mainActivityViewModel.showAndEnableInventoryModule && !InventoryModuleUtil.isAllEquipmentCheckedIn(
//                mainActivityViewModel.getInventoryToShowData() as MutableList<InventoryToShowTable?>?
//            )
//        ) {
//            val logoutNoteForEquipmentRequest = LogoutNoteForEquipmentRequest()
//            logoutNoteForEquipmentRequest.logoutNote =
//                logoutNote.nullSafety(getString(R.string.msg_unexpected_logout))
//            mainActivityViewModel.callAddNoteForNotCheckedInEquipmentAPI(
//                logoutNoteForEquipmentRequest
//            )
//        }
//
//        try {
//
//            //set login status false
//            sharedPreference.write(SharedPrefKey.IS_LOGGED_IN, false)
//
//            //user event logging
//            val mLat = sharedPreference.read(SharedPrefKey.LAT, DEFAULT_VALUE_ZERO_DOT_ZERO_STR)
//                ?.toDouble()
//            val mLong = sharedPreference.read(SharedPrefKey.LONG, DEFAULT_VALUE_ZERO_DOT_ZERO_STR)
//                ?.toDouble()
//            val mLocationUpdateRequest = LocUpdateRequest()
//            mLocationUpdateRequest.activityType = Constants.ACTIVITY_TYPE_LOCATION_UPDATE
//            mLocationUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
//            mLocationUpdateRequest.locationUpdateType = "logout"
//            mLocationUpdateRequest.latitude = mLat
//            mLocationUpdateRequest.longitude = mLong
//            mLocationUpdateRequest.clientTimestamp = DateTimeUtils.getClientTimestamp()
//            callLocationApi(mLocationUpdateRequest)
//            //Call MainActivity via intent
//
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        } finally {
//            //I want to call loginScreenFragment by removing all the fragments in stack
//
//            navigateToLoginClearingBackStack()
//        }
//    }
//
//    // kotlin
//    private fun navigateToLoginClearingBackStack() {
//        val navController = findNavController(R.id.nav_host_fragment)
//
//        val navOptions = androidx.navigation.NavOptions.Builder()
//            // popUpTo the whole graph id (inclusive = true) to remove all destinations from the back stack
//            .setPopUpTo(navController.graph.id, true).build()
//
//        navController.navigate(R.id.loginScreenFragment, null, navOptions)
//    }
//
//
//
//     //This function is used to check if the given menu option is need to be enabled or not based on site & condition
//     //If this will return null means, it can be enabled & proceed
//     //If it returns some message, it means it can be disabled & we have to show that message to user
//    private fun isOptionEnabledOrErrorToShowForInventoryModule(): String? {
//        return if (mainActivityViewModel.showAndEnableInventoryModule) {
//            if (InventoryModuleUtil.isRequiredEquipmentCheckedOut(mainActivityViewModel.getInventoryToShowData() as MutableList<InventoryToShowTable?>?)) {
//                null
//            } else {
//                getString(R.string.error_red_box_qr_code_inventory_is_mandatory_to_proceed)
//            }
//        } else {
//            null
//        }
//    }
//
//
//
//    //This function used to used show a dialog with text input to the user, if you are trying to logout without submitting the equipment back
//    //I mean, without check-in all the equipment back
//    private fun showReasonNoteDialogForEquipmentCheckIn() {
//        val mDialog = Dialog(this@MainActivity, R.style.ThemeDialogCustom)
//        mDialog.setContentView(R.layout.dialog_add_note)
//
//        val etTextNote: TextInputEditText = mDialog.findViewById(R.id.etNote)
//        val tvDialogTitle: AppCompatTextView = mDialog.findViewById(R.id.tv_dialogTitle)
//        val btnDone: AppCompatButton = mDialog.findViewById(R.id.btn_done)
//
//        tvDialogTitle.text = getString(R.string.msg_add_note_before_logout)
//        btnDone.text = getString(R.string.btn_text_submit_and_logout)
//
//        etTextNote.filters =
//            arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.max_length_for_equipment_note)))
//
//        val appCompatImageView = mDialog.findViewById<ImageView>(R.id.btn_cancel)
//        appCompatImageView.setOnClickListener { v: View? -> mDialog.dismiss() }
//        btnDone.setOnClickListener {
//            if (etTextNote.text.isNullOrEmpty()) {
//                etTextNote.requestFocus()
//                etTextNote.isFocusable = true
//                etTextNote.error = getString(R.string.val_msg_please_enter_note)
//                LogUtil.printToastMSGForErrorWarning(
//                    applicationContext, getString(R.string.val_msg_please_enter_note)
//                )
//            } else {
//                mDialog.dismiss()
//                logout(etTextNote.text.toString())
//            }
//        }
//        mDialog.show()
//        val window = mDialog.window
//        window?.setLayout(
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
//        )
//    }
//
//
//    //logout
//    private fun openUserLogoutDialog() {
//        AlertDialogUtils.showDialog(
//            context = this@MainActivity,
//            icon = R.drawable.ic_logout,
//            title = getString(R.string.confirmation_text_title_logout),
//            message = getString(R.string.confirmation_text_desc_logout),
//            positiveButtonText = getString(R.string.button_text_yes),
//            negativeButtonText = getString(R.string.button_text_no),
//            listener = object : AlertDialogListener {
//                override fun onPositiveButtonClicked() {
//                    try {
//                        FileUtil.takeDatabaseBackUpAndSave(this@MainActivity)
//                        DialogUtil.showLoader(this@MainActivity)
//                        callEventActivityLogApiForBaseActivity("Logout")
//                    } catch (e: ParseException) {
//                        e.printStackTrace()
//                    }
//                }
//            })
//    }
//
////    override fun onConnected(p0: Bundle?) {
////        //Nothing to implement here
////    }
////
////    override fun onConnectionSuspended(p0: Int) {
////        //Nothing to implement here
////    }
////
////    override fun onConnectionFailed(p0: ConnectionResult) {
////        //Nothing to implement here
////    }
//
//    companion object {
//        const val CITATION_IMAGE_API_CALLED = "Citation"
//        const val ACTIVITY_IMAGE_API_CALLED = "Activity"
//
//        @JvmField
//        var filter = InputFilter { source, start, end, dest, dstart, dend ->
//            for (i in start until end) {
//                if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*")
//                        .matcher(
//                            source[i].toString()
//                        ).matches()
//                ) {
//                    return@InputFilter ""
//                }
//            }
//            null
//        }
//
//         //Function used to get QR code size from settings file for Zebra Command Print
////        fun getQRCodeSizeForCommandPrint(): Int {
////            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST)
////
////            if (settingsList != null) {
////                if (mQrCodeSizeForCommandFacsimile == 0) {
////                    mQrCodeSizeForCommandFacsimile = settingsList?.firstOrNull {
////                        it.type?.trim().equals(
////                            Constants.SETTINGS_FLAG_QR_CODE_SIZE,
////                            true
////                        )
////                    }?.mValue.nullSafety("0").toInt()
////                }
////            }
////            return mQrCodeSizeForCommandFacsimile
////        }
//    }
}