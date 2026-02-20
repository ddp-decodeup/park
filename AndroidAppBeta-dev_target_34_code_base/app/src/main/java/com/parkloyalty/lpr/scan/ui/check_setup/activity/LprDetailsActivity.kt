package com.parkloyalty.lpr.scan.ui.check_setup.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.media.AudioManager
import android.media.ExifInterface
import android.media.ToneGenerator
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.camera2.CameraActivity
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerRequest
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerResponse
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerViewModel
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivityLoginBinding
import com.parkloyalty.lpr.scan.databinding.ActivityLprDetailsBinding
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.buildInvoiceFeeStructure
import com.parkloyalty.lpr.scan.extensions.buildLocationDetails
import com.parkloyalty.lpr.scan.extensions.buildTicketType
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpen
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpenGOA
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForTimingDataClickOnScanResultScreenForAutoFillRemark
import com.parkloyalty.lpr.scan.extensions.getSettingFileValuesForRemarkAutoFilledWithElapsedTime
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_DEFAULT_REGULATION_TIME_AUTO
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_IS_AUTO_TIMING
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.vehiclestickerscan.model.VehicleInfoModel
import com.parkloyalty.lpr.scan.vehiclestickerscan.VehicleStickerScanActivity
import com.parkloyalty.lpr.scan.ui.boot.BootActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.CameraRawFeedDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.LastSecondCheckResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.LastSecondCheckViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.ResultsItemCameraRaw
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.*
import com.parkloyalty.lpr.scan.ui.check_setup.license.LprScanActivity
import com.parkloyalty.lpr.scan.ui.check_setup.model.*
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.*
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.CitationResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ExemptResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CommentsDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.HeaderDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.VehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.ViolationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingViewModel
import com.parkloyalty.lpr.scan.ui.dashboard.DashboardActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.ImageCache
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.ticket.AddNotesAndImagesActivity
import com.parkloyalty.lpr.scan.util.*
import com.parkloyalty.lpr.scan.util.AppUtils.compareDates
import com.parkloyalty.lpr.scan.util.AppUtils.getLocalDateFromUTC
import com.parkloyalty.lpr.scan.util.AppUtils.getRegexFromSetting
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.isTimingExpired
import com.parkloyalty.lpr.scan.util.AppUtils.maxImageCount
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialogOnce
import com.parkloyalty.lpr.scan.util.AppUtils.sixActionButtonVisibilityCheck
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPRTime
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSGForErrorWarning
import com.parkloyalty.lpr.scan.util.Util.calculateInSampleSize
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule
import kotlin.text.contains
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class LprDetailsActivity : BaseActivity(), TextWatcher, CustomDialogHelper {
    lateinit var drawerLy: DrawerLayout
    lateinit var TextInputLayoutLprNumber: TextInputLayout
    lateinit var TextViewLprNumber: AppCompatEditText
    lateinit var mTextViewTabStatus: AppCompatTextView
    lateinit var mTextViewTabHistory: AppCompatTextView
    lateinit var mViewStatus: View
    lateinit var mViewHistory: View
    lateinit var mLayStatus: LinearLayoutCompat
    lateinit var mLayHistory: LinearLayoutCompat
    lateinit var mLayHide: LinearLayoutCompat
    lateinit var mLayPayment: LinearLayoutCompat
    lateinit var mLayHistoryTextSection: LinearLayoutCompat
    lateinit var mLayPermit: LinearLayoutCompat
    lateinit var mLayScofflaw: LinearLayoutCompat
    lateinit var mLayStolen: LinearLayoutCompat
    lateinit var mLayExempt: LinearLayoutCompat
    lateinit var mLayCameraRawFeedData: LinearLayoutCompat
    lateinit var mEmptyLayout: LinearLayoutCompat
    lateinit var mEmptyLayoutStatus: LinearLayoutCompat
    lateinit var mLayTiming: LinearLayoutCompat
    lateinit var mImageViewNumberPlate: AppCompatImageView
    lateinit var mImageViewRight: AppCompatImageView
    lateinit var btnPayment: AppCompatButton
    lateinit var btnPermit: AppCompatButton
    lateinit var btnExempt: AppCompatButton
    lateinit var btnScofflaw: AppCompatButton
    lateinit var btnTimings: AppCompatButton
    lateinit var btnStolen: AppCompatButton
    lateinit var btnReScan: AppCompatButton
    lateinit var btnCheck: AppCompatButton
    lateinit var btnIssueStatus: AppCompatButton
    lateinit var btnIssueHistory: AppCompatButton
    lateinit var btnCameraRawFeedData: AppCompatButton
    lateinit var mTvReverseCoded: AppCompatTextView
    lateinit var mAppTextTimeName: AppCompatTextView
    lateinit var mAutoComTextViewVehColor: AppCompatAutoCompleteTextView
    lateinit var mAutoComTextViewVehModel: AppCompatAutoCompleteTextView
    lateinit var mAutoComTextViewMakeVeh: AppCompatAutoCompleteTextView
    lateinit var textInputLayoutVehColor: TextInputLayout
    lateinit var textInputLayoutVehModel: TextInputLayout
    lateinit var textInputLayoutVehMake: TextInputLayout
    lateinit var mRecylerViewHistory: RecyclerView
    lateinit var mRecylerViewPayment: RecyclerView
    lateinit var mRecylerViewPermit: RecyclerView
    lateinit var mRecylerViewScofflaw: RecyclerView
    lateinit var mRecylerViewStolen: RecyclerView
    lateinit var mRecylerViewExempt: RecyclerView
    lateinit var mRecylerViewTiming: RecyclerView
    lateinit var mRecylerViewCameraRawFeedData: RecyclerView
    lateinit var linearLayoutCompatTimingIcon: LinearLayoutCompat
    lateinit var linearLayoutCompatMake: LinearLayoutCompat
    lateinit var linearLayoutCompatModel: LinearLayoutCompat
    lateinit var linearLayoutCompatColor: LinearLayoutCompat
    lateinit var linearLayoutCompatHistory: LinearLayoutCompat
    lateinit var linearLayoutCompatFuzzy: LinearLayoutCompat
    lateinit var appCompatTextViewFuzzy1: AppCompatTextView
    lateinit var appCompatTextViewFuzzy2: AppCompatTextView
    lateinit var appCompatTextViewFuzzy3: AppCompatTextView
    lateinit var appCompatTextViewFuzzy4: AppCompatTextView
    lateinit var appCompatTextViewFuzzy5: AppCompatTextView
    lateinit var appCompatTextViewFuzzy6: AppCompatTextView
    lateinit var appCompatTextViewFuzzy7: AppCompatTextView
    lateinit var appCompatTextViewFuzzy8: AppCompatTextView
    lateinit var appCompatTextViewFuzzy9: AppCompatTextView
    lateinit var appCompatTextViewPaymentFuzzyPlate: AppCompatTextView
    lateinit var appCompatImageViewLock: AppCompatImageView
    lateinit var textInputLayoutVehLot: TextInputLayout
    lateinit var mAutoComTextViewLot: AppCompatAutoCompleteTextView
    lateinit var linearLayoutCompatZone: LinearLayoutCompat
    lateinit var AppCompatTextViewFineAmountSum: AppCompatTextView
    lateinit var btnWhiteSticker: Button
    lateinit var btnRedSticker: Button
    lateinit var btnReScanSticker: AppCompatButton

    private var mDb: AppDatabase? = null
    private var mSelectedMakeFromVehicleSticker = ""
    private var mSelectedMake = ""
    private var mSelectedModel: String? = ""
    private var mSelectedVin: String? = ""
    private var mSelectedColor: String? = ""
    private var mSelectedMakeValue: String? = ""
    var isPayment = false
    var isTiming = false
    private var mPath: String? = null
    private var mCopyLprScanPath: String? = null
    private var mContext: Context? = null
    private var picUri: Uri? = null
    private var tempUri: String? = null
    private var mTimingRecordRemarkValue: String? = ""
    private var mTimingRecordRemarkValueCameraRawFeed: String? = ""
    private var mTimingTireStem: String? = ""
    private var mListPayment: List<PaymentDataResponse>? = ArrayList()
    private var mListPaymentFuzzy: List<PaymentDataResponse>? = ArrayList()
    private var mListPermit: List<PermitDataResponse>? = ArrayList()
    private var mListExempt: List<ExemptDataResponse>? = ArrayList()
    private var mListStolen: List<StolenDataResponse>? = ArrayList()
    private var mListScofflaw: List<ScofflawDataResponse>? = ArrayList()
    private var mListScofflawFirstIndex: List<ScofflawDataResponse>? = ArrayList()
    private var mListTiming: List<TimingMarkData>? = ArrayList()
    private var mListCitation: List<CitationDataResponse>? = ArrayList()
    private var mListMakeModelColor: List<MakeModelColorData>? = ArrayList()
    private var mHistoryAdapter: HistoryAdapter? = null

    //    private var mDatasetList: DatasetDatabaseModel? = DatasetDatabaseModel()
    private val mModelList: MutableList<DatasetResponse>? = ArrayList()
    private var mImageCount = 0
    private var mPayment = false
    private val mTiming = false
    private val mCitation = false
    private var isMakeLoader = false
    private var isCameraRawFeedAPICallingFlag = false
    private var isFineSumVisible = false
    private var isTireStemWithImageView = false
    private var mWelcomeForm: WelcomeForm? = null
    private var paymentResponse: PaymentResponse? = PaymentResponse()
    private var permitResponse: PermitResponse? = PermitResponse()
    private var permitFuzzyResponse: PermitResponse? = PermitResponse()
    private var exemptResponse: ExemptResponse? = ExemptResponse()
    private var stolenResponse: StolenResponse? = StolenResponse()
    private var scofflawResponse: ScofflawResponse? = ScofflawResponse()
    private var citationResponse: CitationResponse? = CitationResponse()
    private var modelColorResponse: MakeModelColorResponse? = MakeModelColorResponse()
    private var timingMarkResponse: TimingMarkResponse? = TimingMarkResponse()
    private var PaymentLength: Long? = null
    private var PaymentFuzzyLength: Long? = null
    private var modelColorLength: Long? = null
    private var CitationDataLength: Long? = null
    private var ScofflawDataLength: Long? = null
    private var StolenDataLength: Long? = null
    private var ExemptDataLength: Long? = null
    private var PermitDataLength: Long? = null
    private var PermitDataFuzzyLength: Long? = null
    private var settingsList: List<DatasetResponse>? = null
    private var adapterModel: ArrayAdapter<String?>? = null
    private var separated: Array<String>? = null
    private var separatedEventLogger: Array<String>? = null
    private var addressGeo: String? = null
    private val uniqueDataSet: MutableSet<String>? = HashSet()
    private var mTicketNumber: List<CitationBookletModel?>? = ArrayList()
    private var mDataSetTimeObject: TimestampDatatbase? = null
    private val fuzzyStringList = arrayOf(
            "0", "O", "Q", "I", "1", "5", "S", "Z", "2", "B", "8",
            "T", "H", "M", "K", "V", "Y", "F", "P", "R", "E", "3", "D", "A", "4","6","G"
    )
    private var mEscalatedState: String? = ""
    private var mEscalatedLprNumber: String? = ""
    private var lpNumberForLoggerAPI: String? = ""
    private var mLotItem: String? = ""
    private var mSpaceId: String? = null
    private var mMeterNameItem: String? = null
    private var mPrintQuery: String? = null
    private var mPBCZone: String? = null
    private var mStreetItem: String? = null
    private var mBlock: String? = null
    private var mDirectionItem: String? = null
    private var mFromScreen: String? = "lpr_details"
    private var mSideItem: String? = ""
    private var mNoteItem: String? = ""
    private var mNoteItem1: String? = ""
    private var timingId: String? = ""
    private var mState: String? = ""
    private var mTypeOfHit: String? = ""
    private var mViolation: String? = ""
    private var mVin: String? = ""
    private var mVendorName: String? = ""
    private var mTimingImages: MutableList<String> = ArrayList()
    private var geoLat: Double = 0.0
    private var geoLon: Double = 0.0
    private var mFineAmountSum: Double = 0.00
    private var mCamera2Setting: String? = "NO"
    private var mBodyStyleItem: String? = ""
    private var mPaymentDataFuzzy: String? = "NO"
    private var mPaymentByZone: String? = "NO"
    private var mAllPaymentsInZoneFuzzy: String? = "NO"
    private var mPermitByZone: String? = "NO"
    private var mTimeStampOnImage: String? = "NO"
    private var mT2LPRSCAN: String? = "NO"
    private var mV2LPRSCAN: String? = "NO"
    private var mAutoTimingRegulationTime: String? = ""
    private var mAutoTiming: String? = "NO"
    private var mTimingClick: Int? = 0 // timing 1 and citation history 2
    private var fuzzyPlates = StringBuilder()
    private var heightHistory = 0
    private var citationHistoryCount = 0
    private var mViolationCode = ""
    private var isActivityInForeground = false
    private var eventLoggerAPIDelay = 2.5

    private var mZone = "CST"
    private var mHour = ""


    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val pERMISSION_ID = 42
    private lateinit var locationManager: LocationManager
    val formatter = DecimalFormat("00")

    private val mDataFromLprViewModel: DataFromLprViewModel? by viewModels()
    private val mLoggerViewModel: LprScanLoggerViewModel? by viewModels()
    private val mTimigMarkViewModel: TimigMarkViewModel? by viewModels()
    private val mInactiveMeterBuzzerViewModelLpr: InactiveMeterBuzzerViewModel? by viewModels()
    private val mLastSecondCheckViewModel: LastSecondCheckViewModel? by viewModels()
    private val mAddTimingViewModel: AddTimingViewModel? by viewModels()
    private val mCreateTicketViewModel: CreateTicketViewModel? by viewModels()

    private var vehicleStickerInfo: VehicleInfoModel? = null

    private val scope = CoroutineScope(
            Job() + Dispatchers.IO)

    private var permitApiFailureCallCount: Int = 1;
    private val maxPermitApiRetryCallCount: Int = 1;

    private lateinit var binding: ActivityLprDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLprDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        mContext = this
        mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
        addObservers()
        init()
        setToolbar()
        setCapField()
        deleteImages()

        if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true))
        {
            findViewById<AppCompatButton>(R.id.btnValid).setOnClickListener(View.OnClickListener {
                callLastSecondCheckAPI()
            })
        }
        sharedPreference.writeOverTimeParkingTicketDetails(
            SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
            AddTimingRequest()!!
        )

        TextInputLayoutLprNumber!!.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = pInfo.versionName //Version Name

            ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!," Scan Result $versionName ")
            ImageCache.base64Images?.clear()

            /**
             * Citation form delete from stack and remove saved images
             */
            LprDetails2Activity.instanceLprDetails2Activity?.finish()
            mDb?.dbDAO?.deleteTempImages()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setAccessibilityForComponents()

    }

    private fun findViewsByViewBinding() {
        drawerLy = binding.drawerLy
        TextInputLayoutLprNumber = binding.layoutContentLprDetails.inputText
        TextViewLprNumber = binding.layoutContentLprDetails.tvLprNumber
        mTextViewTabStatus = binding.layoutContentLprDetails.tvTabStatus
        mTextViewTabHistory = binding.layoutContentLprDetails.tvTabHistory
        mViewStatus = binding.layoutContentLprDetails.viewStatus
        mViewHistory = binding.layoutContentLprDetails.viewHistory
        mLayStatus = binding.layoutContentLprDetails.layStatus
        mLayHistory = binding.layoutContentLprDetails.layHistory
        mLayHide = binding.layoutContentLprDetails.layHide
        mLayPayment = binding.layoutContentLprDetails.layPayment
        mLayHistoryTextSection = binding.layoutContentLprDetails.layhistorytext
        mLayPermit = binding.layoutContentLprDetails.layPermit
        mLayScofflaw = binding.layoutContentLprDetails.layScofflaw
        mLayStolen = binding.layoutContentLprDetails.layStolen
        mLayExempt = binding.layoutContentLprDetails.layExempt
        mLayCameraRawFeedData = binding.layoutContentLprDetails.layCameraRawFeedData
        mEmptyLayout = binding.layoutContentLprDetails.layoutContentEmptyLayout.emptyLayout
        mEmptyLayoutStatus =
            binding.layoutContentLprDetails.layoutContentEmptyLayoutStatus.emptyLayoutStatus
        mLayTiming = binding.layoutContentLprDetails.layTiming
        mImageViewNumberPlate = binding.layoutContentLprDetails.ivNumberPlate
        mImageViewRight = binding.layoutContentLprDetails.ivRight
        btnPayment = binding.layoutContentLprDetails.btnPayment
        btnPermit = binding.layoutContentLprDetails.btnPermit
        btnExempt = binding.layoutContentLprDetails.btnExempt
        btnScofflaw = binding.layoutContentLprDetails.btnScofflaw
        btnTimings = binding.layoutContentLprDetails.btnTimings
        btnStolen = binding.layoutContentLprDetails.btnStolen
        btnReScan = binding.layoutContentLprDetails.btnReScan
        btnCheck = binding.layoutContentLprDetails.btnCheck
        btnIssueStatus = binding.layoutContentLprDetails.btnIssueStatus
        btnIssueHistory = binding.layoutContentLprDetails.btnIssueHistory
        btnCameraRawFeedData = binding.layoutContentLprDetails.btnCameraRawFeedData
        mTvReverseCoded = binding.layoutContentLprDetails.reverseCoded
        mAppTextTimeName = binding.layoutContentLprDetails.appTextTimeName
        mAutoComTextViewVehColor = binding.layoutContentLprDetails.AutoComTextViewVehColor
        mAutoComTextViewVehModel = binding.layoutContentLprDetails.AutoComTextViewVehModel
        mAutoComTextViewMakeVeh = binding.layoutContentLprDetails.AutoComTextViewMakeVeh
        textInputLayoutVehColor = binding.layoutContentLprDetails.textInputLayoutVehColor
        textInputLayoutVehModel = binding.layoutContentLprDetails.textInputLayoutVehModel
        textInputLayoutVehMake = binding.layoutContentLprDetails.textInputLayoutVehMake
        mRecylerViewHistory = binding.layoutContentLprDetails.rvHistory
        mRecylerViewPayment = binding.layoutContentLprDetails.rvPayment
        mRecylerViewPermit = binding.layoutContentLprDetails.rvPermit
        mRecylerViewScofflaw = binding.layoutContentLprDetails.rvScofflaw
        mRecylerViewStolen = binding.layoutContentLprDetails.rvStolen
        mRecylerViewExempt = binding.layoutContentLprDetails.rvExempt
        mRecylerViewTiming = binding.layoutContentLprDetails.rvTiming
        mRecylerViewCameraRawFeedData = binding.layoutContentLprDetails.rvCameraRawFeedData
        linearLayoutCompatTimingIcon = binding.layoutContentLprDetails.llTimingIcon
        linearLayoutCompatMake = binding.layoutContentLprDetails.llMake
        linearLayoutCompatModel = binding.layoutContentLprDetails.llModel
        linearLayoutCompatColor = binding.layoutContentLprDetails.llColor
        linearLayoutCompatHistory = binding.layoutContentLprDetails.tabHistory
        linearLayoutCompatFuzzy =
            binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.llFuzzyLogic
        appCompatTextViewFuzzy1 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt1
        appCompatTextViewFuzzy2 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt2
        appCompatTextViewFuzzy3 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt3
        appCompatTextViewFuzzy4 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt4
        appCompatTextViewFuzzy5 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt5
        appCompatTextViewFuzzy6 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt6
        appCompatTextViewFuzzy7 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt7
        appCompatTextViewFuzzy8 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt8
        appCompatTextViewFuzzy9 = binding.layoutContentLprDetails.layoutContentLprFuzzyLogicBox.txt9
        appCompatTextViewPaymentFuzzyPlate = binding.layoutContentLprDetails.txtPaymentFuzzyPlate
        appCompatImageViewLock = binding.layoutContentLprDetails.ivLock
        textInputLayoutVehLot = binding.layoutContentLprDetails.textInputLayoutVehLot
        mAutoComTextViewLot = binding.layoutContentLprDetails.AutoComTextViewVehLot
        linearLayoutCompatZone = binding.layoutContentLprDetails.llZone
        AppCompatTextViewFineAmountSum = binding.layoutContentLprDetails.tvFineAmountSum
        btnWhiteSticker =
            binding.layoutContentLprDetails.layoutContentWhiteRedButtonBootTow.btnWhiteSticker
        btnRedSticker =
            binding.layoutContentLprDetails.layoutContentWhiteRedButtonBootTow.btnRedSticker
        btnReScanSticker = binding.layoutContentLprDetails.btnReScanSticker
    }

    private fun setupClickListeners() {

        btnIssueHistory.setOnClickListener {
            moveToNext("none")
        }

        btnIssueStatus.setOnClickListener {
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
            ) {
                mBlock = ""
                mStreetItem = ""
            }
            mDb?.dbDAO?.insertWelcomeForm(mWelcomeForm!!)
            moveToNext("none")
        }

        binding.layoutContentLprDetails.ivCamera.setOnClickListener {
            requestPermission()
        }

        btnReScan.setOnClickListener {
            val mIntent1 = Intent(this@LprDetailsActivity, LprScanActivity::class.java)
            mIntent1.putExtra("Lot", mLotItem)
            mIntent1.putExtra("Location", mLotItem)
            mIntent1.putExtra("Space_id", mSpaceId)
            mIntent1.putExtra("Meter", mMeterNameItem)
            mIntent1.putExtra("Zone", mPBCZone)
            mIntent1.putExtra("Street", mStreetItem)
            mIntent1.putExtra("Block", mBlock)
            mIntent1.putExtra("Direction", mDirectionItem)
            mIntent1.putExtra("from_scr", mFromScreen)
            startActivity(mIntent1)
            finish()
        }

        btnCheck.setOnClickListener {
            if (TextViewLprNumber.editableText.toString().trim() != null) {
                if (TextViewLprNumber.editableText.toString().trim() != "") {
                    setFuzzyLogicSetTextView()
                    setFuzzyLogicTextViewClick()
                    mAutoComTextViewMakeVeh.setText("")
                    mAutoComTextViewVehModel.setText("")
                    mAutoComTextViewVehColor.setText("")
                    linearLayoutCompatFuzzy.visibility = View.VISIBLE
                    mTimingImages?.clear()
                    ImageCache.base64Images?.clear()
                    PaymentLength = 0
                    PaymentFuzzyLength = 0
                    modelColorLength = 0
                    CitationDataLength = 0
                    ScofflawDataLength = 0
                    StolenDataLength = 0
                    ExemptDataLength = 0
                    PermitDataLength = 0
                    PermitDataFuzzyLength = 0
                    mListTiming = ArrayList()
                    setAdapterForTiming(mListTiming)

                    //                        showProgressLoader("Please Wait..!!");
                    if (btnCheck!!.isEnabled) {
                        btnCheck!!.isEnabled = false
                        val alpha = 0.45f
                        val alphaUp = AlphaAnimation(alpha, alpha)
                        alphaUp.fillAfter = true
                        btnCheck!!.startAnimation(alphaUp)
                        callAllTypeApi()
                    }

                } else {
                    printToastMSG(
                        this@LprDetailsActivity,
                        getString(R.string.err_msg_plate_is_empty)
                    )
                }
            } else {
                printToastMSG(
                    this@LprDetailsActivity,
                    getString(R.string.err_msg_plate_is_empty)
                )
            }
        }

        binding.layoutContentLprDetails.tabStatus.setOnClickListener {
            mLayHistory.visibility = View.GONE
            mLayStatus.visibility = View.VISIBLE
            mTextViewTabStatus.setTextColor(resources.getColor(R.color.deep_blue))
            mViewStatus.setBackgroundColor(resources.getColor(R.color.deep_blue))
            mTextViewTabHistory.setTextColor(resources.getColor(R.color.app_gray))
            mViewHistory.setBackgroundColor(resources.getColor(R.color.app_gray))

            //Set ADA
            mTextViewTabStatus.contentDescription =
                getString(
                    R.string.ada_content_description_tab_selected,
                    mTextViewTabStatus.text.toString()
                )


            mTextViewTabHistory.contentDescription =
                getString(
                    R.string.ada_content_description_tab_unselected,
                    mTextViewTabHistory.text.toString()
                )


            AccessibilityUtil.announceForAccessibility(
                mTextViewTabStatus,
                getString(
                    R.string.ada_content_description_tab_selected,
                    mTextViewTabStatus.text.toString()
                )
            )
        }

        binding.layoutContentLprDetails.tabHistory.setOnClickListener {
            mLayHistory.visibility = View.VISIBLE
            mLayStatus.visibility = View.GONE
            //setHeightOfHistoryRecycleView()
            mTextViewTabHistory.setTextColor(resources.getColor(R.color.deep_blue))
            mViewHistory.setBackgroundColor(resources.getColor(R.color.deep_blue))
            mTextViewTabStatus.setTextColor(resources.getColor(R.color.app_gray))
            mViewStatus.setBackgroundColor(resources.getColor(R.color.app_gray))

            //Set ADA

            mTextViewTabStatus.contentDescription =
                getString(
                    R.string.ada_content_description_tab_unselected,
                    mTextViewTabStatus.text.toString()
                )


            mTextViewTabHistory.contentDescription =
                getString(
                    R.string.ada_content_description_tab_selected,
                    mTextViewTabHistory.text.toString()
                )


            AccessibilityUtil.announceForAccessibility(
                mTextViewTabHistory,
                getString(
                    R.string.ada_content_description_tab_selected,
                    mTextViewTabHistory.text.toString()
                )
            )
        }

        btnTimings.setOnClickListener {
            if (!mTiming) {
                try {
                    val mIntent = Intent(this, AddTimeRecordActivity::class.java)
                    mIntent.putExtra(
                        "lpr_number",
                        TextViewLprNumber.text.toString().trim()
                    )
                    if (mListTiming!!.size > 0) {
                        mIntent.putExtra(
                            "regulation",
                            mListTiming!![0].regulationTime.toString()
                        )
                    } else {
                        mIntent.putExtra("regulation", 0.toString())
                    }
                    mIntent.putExtra(
                        "make",
                        mAutoComTextViewMakeVeh.editableText.toString().trim()
                    )
                    if (mSelectedModel != null) {
                        mIntent.putExtra(
                            "model",
                            mAutoComTextViewVehModel.editableText.toString().trim()
                        )
                    }
                    if (mSelectedVin != null) {
                        mIntent.putExtra(
                            "vinNumber",
                            mSelectedVin
                        )
                    }
                    mIntent.putExtra(
                        "color",
                        mAutoComTextViewVehColor.editableText.toString().trim()
                    )
//                mIntent.putExtra("address", mTvReverseCoded.text.toString())
                    mIntent.putExtra("address", separated!![0])
                    mIntent.putExtra("state", mState)
                    startActivity(mIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                moveToNext("1330-388.6")
            }
        }

        btnExempt.setOnClickListener {
            //moveToNext("none")
        }

        btnPermit.setOnClickListener {
            //moveToNext("1130-388.25")
            //callGetDataFromLprApi(TextViewLprNumber.getText().toString().trim(),"Permit_Data");
        }

        binding.layoutContentLprDetails.imgPayment.setOnClickListener {
            //                if (mPayment) {
//                    moveToNext("1130-378C")
//                } else {
//                    moveToNext("1130-378A")
//                }
        }

        btnPayment.setOnClickListener {
            //                if (mPayment) {
//                    moveToNext("1130-378C")
//                } else {
//                    moveToNext("1130-378A")
//                }
            //callGetDataFromLprApi(TextViewLprNumber.getText().toString().trim(),"Payment_Data");
        }

        btnStolen.setOnClickListener {
            moveToNext("none")
        }

        val sharedClickForScofflaw = View.OnClickListener { v ->
            when (v?.id) {
                binding.layoutContentLprDetails.imSccoflaw.id,
                binding.layoutContentLprDetails.llScofflawIcon.id,
                binding.layoutContentLprDetails.btnScofflaw.id -> {
                    try {
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_RUTGERS,
                                ignoreCase = true
                            ) && (mListScofflaw != null && mListScofflaw!!.size > 0)
                        ) {
                            if (Constants.stickerList.contains(mListScofflaw!!.get(0).type)) {
                                showWhiteAndRedStickerPopup(btnScofflaw, mListScofflaw?.get(0))
                            } else {
//                            LogUtil.printSnackBar(linearLayoutCompatMainLayoutView!!,this@LprDetailsActivity,"There is no White and Red Sticker")
                            }
                        } else {
                            val intent = Intent(this@LprDetailsActivity, BootActivity::class.java)
                            intent.putExtra(
                                "SCCOFFLAW",
                                if (mListScofflaw != null && mListScofflaw!!.size > 0) mListScofflaw!![0] else null
                            )
                            intent.putExtra("MAKE", mAutoComTextViewMakeVeh.text.toString())
                            intent.putExtra("MODEL", mAutoComTextViewVehModel.text.toString())
                            intent.putExtra("COLOR", mAutoComTextViewVehColor.text.toString())
                            intent.putExtra("LPNUMBER", TextViewLprNumber.text.toString())

                            //It is used for boot tow notice printing
                            if (BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_SEPTA,
                                    true
                                )
                            ) {
                                intent.putExtra(
                                    INTENT_KEY_CITATION_NUMBER,
                                    mListCitation?.firstOrNull()?.ticketNo.nullSafety()
                                )
                                intent.putExtra(
                                    INTENT_KEY_VIOLATION_DATE,
                                    getLocalDateFromUTC(
                                        mListCitation?.firstOrNull()?.citationIssueTimestamp.nullSafety(),
                                        SDF_FULL_DATE_UTC,
                                        SDF_MM_DD_YYYY
                                    )
                                )
                            }

                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        binding.layoutContentLprDetails.imSccoflaw.setOnClickListener(sharedClickForScofflaw)
        binding.layoutContentLprDetails.llScofflawIcon.setOnClickListener(sharedClickForScofflaw)
        binding.layoutContentLprDetails.btnScofflaw.setOnClickListener(sharedClickForScofflaw)

        appCompatImageViewLock.setOnClickListener {
            updateLockButtonIcon()
        }

        btnWhiteSticker.setOnClickListener {
            MoveToCitationFormWithScofflawData(1)
        }

        btnRedSticker.setOnClickListener {
            MoveToCitationFormWithScofflawData(2)
        }

        btnReScanSticker.setOnClickListener {
            val intent = Intent(this, VehicleStickerScanActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setAccessibilityForComponents(){
        setAccessibilityForTextInputLayoutDropdownButtons(this@LprDetailsActivity, textInputLayoutVehLot)
        setAccessibilityForTextInputLayoutDropdownButtons(this@LprDetailsActivity, textInputLayoutVehMake)
        setAccessibilityForTextInputLayoutDropdownButtons(this@LprDetailsActivity, textInputLayoutVehModel)
        setAccessibilityForTextInputLayoutDropdownButtons(this@LprDetailsActivity, textInputLayoutVehColor)
        setAccessibilityForTextInputLayoutCrossButtons(this@LprDetailsActivity, TextInputLayoutLprNumber)

        linearLayoutCompatTimingIcon.setCustomAccessibility(contentDescription = getString(R.string.scr_lbl_timing_detail), role = getString(R.string.ada_role_button))
        findViewById<AppCompatImageView>(R.id.ivCamera).contentDescription = getString(R.string.ada_content_description_camera)
        mTextViewTabStatus.contentDescription = getString(R.string.ada_content_description_tab_selected, mTextViewTabStatus.text.toString())
        mTextViewTabHistory.contentDescription =
            getString(
                R.string.ada_content_description_tab_unselected,
                mTextViewTabHistory.text.toString()
            )
        mImageViewNumberPlate.contentDescription = getString(R.string.ada_content_description_license_plate_image)
        TextViewLprNumber.contentDescription = getString(R.string.ada_content_description_license_plate_image)
    }


    private fun init() {
        if (showAndEnableScanVehicleStickerModule) {
            btnReScanSticker.showView()
        }else{
            btnReScanSticker.hideView()
        }

        mDb = BaseApplication.instance?.getAppDatabase()
        mEventStartTimeStamp = AppUtils.getDateTime()
        mWelcomeForm = mDb?.dbDAO?.getWelcomeForm()
        TextViewLprNumber.addTextChangedListener(this)
        setLayoutVisibilityBasedOnSettingResponse()
        vehicalDetailsFromDb()
        val intent = intent
        if (intent != null) {

            if (intent.hasExtra(INTENT_KEY_VEHICLE_INFO)) {
                vehicleStickerInfo =
                    intent.getSerializableExtra(INTENT_KEY_VEHICLE_INFO) as VehicleInfoModel?

                AppUtils.setVehicleStickerData(vehicleStickerInfo?.plateNumber.nullSafety(), vehicleStickerInfo)
                setVehicleStickerData()
            }

            val mLprNumber = intent.getStringExtra("lpr_number")
            TextViewLprNumber.filters = arrayOf<InputFilter>(AllCaps())
            TextViewLprNumber.setText(mLprNumber)
            TextViewLprNumber.setSelection(TextViewLprNumber.text!!.length)
            setFuzzyLogicSetTextView()
            setFuzzyLogicTextViewClick()
            linearLayoutCompatFuzzy.visibility = View.VISIBLE

            var imageName = ""
            if (intent.hasExtra(INTENT_KEY_VEHICLE_STICKER_URL)) {
                imageName = "ny_sticker_$mLprNumber.jpg"
                mPath = intent.getStringExtra(INTENT_KEY_VEHICLE_STICKER_URL)
            } else {
                imageName = "anpr_$mLprNumber.jpg"
                mPath = if (getIntent().hasExtra("screen") && getIntent().getStringExtra("screen")
                        .equals("ContinuosResultActivity", ignoreCase = true)
                ) {
                    mPath + Constants.COTINOUS + "/" + imageName
                } else {
                    mPath + Constants.SCANNER + "/" + imageName
                }
            }

            val imgFile = File(mPath)

            val destFile = File(
                    Environment.getExternalStorageDirectory().absolutePath,
                    Constants.FILE_NAME + Constants.LPRSCANIMAGES)
            if (!destFile.exists()) {
                if (!destFile.mkdirs()) {
                    Log.e("getOutputPhotoFile", "Failed to create storage directory.")
                }
            }

            if (destFile != null && imgFile.exists()) {
                try {
                    mCopyLprScanPath =  destFile!!.absolutePath + "/" + imageName

                    mainScope.launch {
                        copyFileOrDirectory(imgFile.absolutePath, destFile.absolutePath)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

             mainScope.launch {
                 val imgFileCopy = mCopyLprScanPath?.let { File(it) }
                if (imgFileCopy!=null && imgFileCopy!!.exists()) {
                    @Suppress("KotlinConstantConditions")
                    if (BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_SEPTA){
                        mImageViewNumberPlate.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    mImageViewNumberPlate.setImageURI(Uri.fromFile(imgFileCopy))
                    //save image to db
                    mDb?.dbDAO?.deleteTempImages()
                    mImageCount = mDb?.dbDAO?.getCountImages().nullSafety()
                    val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
                    val pathDb = Uri.fromFile(imgFileCopy).path
                    val mImage = CitationImagesModel()
                    mImage.citationImage = pathDb
                    mImage.id = id.toInt()
                    getMyDatabase()?.dbDAO?.insertCitationImage(mImage)


                    //Can be done something here if required after image is loaded
                    if (intent.hasExtra(INTENT_KEY_VEHICLE_STICKER_URL)) {
                        //See if you have LPR image exist or not
                        val pairForLprImage = FileUtil.isLprImageExists(mLprNumber)
                        if (pairForLprImage.first) {
                            val idForLpr = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
                            val lprImage = CitationImagesModel()
                            lprImage.status = 0
                            lprImage.citationImage = pairForLprImage.second
                            lprImage.id = idForLpr.toInt() + Random().nextInt(1000)

                            getMyDatabase()?.dbDAO?.insertCitationImage(lprImage)
                        }
                    } else {
                        //See if you have Vehicle Sticker details exist or not, If exist then set details to fields
                        if (AppUtils.findVehicleStickerDetailsByKey(mLprNumber.nullSafety())?.plateNumber?.isNotEmpty()
                                .nullSafety()
                        ) {
                            vehicleStickerInfo =
                                AppUtils.findVehicleStickerDetailsByKey(mLprNumber.nullSafety())
                            setVehicleStickerData()
                        }

                        //See if you have Vehicle Sticker image exist or not
                        val pairForVehicleStickerImage = FileUtil.isVehicleStickerImageExists(mLprNumber)
                        if (pairForVehicleStickerImage.first) {
                            val idForVehicleSticker = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())

                            val vehicleStickerImage = CitationImagesModel()
                            vehicleStickerImage.status = 0
                            vehicleStickerImage.citationImage = pairForVehicleStickerImage.second
                            vehicleStickerImage.id = idForVehicleSticker.toInt() + Random().nextInt(1000)

                            getMyDatabase()?.dbDAO?.insertCitationImage(vehicleStickerImage)
                        }
                    }
                }
            }
            //"CAE597"
            if (intent.getStringExtra("from_scr") != null && intent.getStringExtra("from_scr") == "PAYBYSPACE") {
                mFromScreen = intent.getStringExtra("from_scr")
                mLotItem = intent.getStringExtra("Lot")
                mLotItem = intent.getStringExtra("Location")
                mSpaceId = intent.getStringExtra("Space_id")
                mMeterNameItem = intent.getStringExtra("Meter")
                mPBCZone = intent.getStringExtra("Zone")
                mStreetItem = intent.getStringExtra("Street")
                mBlock = intent.getStringExtra("Block")
                mDirectionItem = intent.getStringExtra("Direction")
            }
            if (intent.getStringExtra("from_scr") != null && intent.getStringExtra("from_scr") == Constants.DIRECTED_ENFORCEMENT) {
                mFromScreen = intent.getStringExtra("from_scr")
                mLotItem = intent.getStringExtra("Lot")
                mLotItem = intent.getStringExtra("Location")
                mSpaceId = intent.getStringExtra("Space_id")
                mMeterNameItem = intent.getStringExtra("Meter")
                mPBCZone = intent.getStringExtra("Zone")
                mStreetItem = intent.getStringExtra("Street")
                mBlock = intent.getStringExtra("Block")
                mDirectionItem = intent.getStringExtra("Direction")
                mState = intent.getStringExtra("State")
                mTypeOfHit = intent.getStringExtra("type_of_hit")
//                mViolationCode = intent.getStringExtra("violation_code")!!

            } else {
                sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
            }
        }

            mDataSetTimeObject = mDb?.dbDAO?.getUpdateTimeResponse()

            ioScope.launch {
                setDropdownMakeVehicle("","")
                setDropdownVehicleColour("")
//                setDropdownVehicleModel("",false)
            }

        linearLayoutCompatTimingIcon.setOnClickListener {
            if (mListTiming != null && mListTiming!!.isNotEmpty()) {
//                mTimingRecordValue = splitDateLPR(mListTiming!![0].markIssueTimestamp.toString())
//                sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "true")
//                moveToNext("time")
            }
        }
        try {
            if (sharedPreference.read(SharedPrefKey.LOCK_GEO_ADDRESS, "").equals("unlock", true)) {
                appCompatImageViewLock.tag = "unlock"
                appCompatImageViewLock.setImageDrawable(ContextCompat.getDrawable(this@LprDetailsActivity!!, R.drawable.ic_baseline_lock_open_24))

                //Set ADA
                appCompatImageViewLock.contentDescription = getString(R.string.ada_content_description_location_unlocked)
            } else {
                addressGeo = sharedPreference.read(SharedPrefKey.LOCK_GEO_SAVE_ADDRESS, "")
                appCompatImageViewLock.tag = "lock"
                appCompatImageViewLock.setImageDrawable(ContextCompat.getDrawable(this@LprDetailsActivity!!, R.drawable.ic_baseline_lock_24))

                //Set ADA
                appCompatImageViewLock.contentDescription = getString(R.string.ada_content_description_location_locked)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (TextViewLprNumber != null && !TextUtils.isEmpty(TextViewLprNumber.text.toString())) {
            try {
                 mLotItem = mWelcomeForm?.lot

                if (mLotItem.isNullOrEmpty()) {
                    mLotItem = sharedPreference.read(SharedPrefKey.SCAN_RESULT_LOT, "")
                }
            } catch (e: Exception) {
            }
            callAllTypeApi()
        }

        setCrossClearButton(
            context = this@LprDetailsActivity,
            textInputLayout = textInputLayoutVehColor,
            appCompatAutoCompleteTextView = mAutoComTextViewVehColor
        )
        setCrossClearButton(
            context = this@LprDetailsActivity,
            textInputLayout = textInputLayoutVehMake,
            appCompatAutoCompleteTextView = mAutoComTextViewMakeVeh
        )
        setCrossClearButton(
            context = this@LprDetailsActivity,
            textInputLayout = textInputLayoutVehModel,
            appCompatAutoCompleteTextView = mAutoComTextViewVehModel
        )

    }

    /**
     * Set vehicle sticker data to fields
     */
    private fun setVehicleStickerData(){
            mSelectedMakeFromVehicleSticker = Singleton.getDataSetList(DATASET_CAR_MAKE_LIST, mDb)?.firstOrNull { it.makeText?.contains(vehicleStickerInfo?.make.nullSafety(), ignoreCase = true) == true }?.makeText.nullSafety()
            mSelectedModel = Singleton.getDataSetList(DATASET_CAR_MODEL_LIST, mDb)?.firstOrNull { it.model?.contains(vehicleStickerInfo?.model.nullSafety(), ignoreCase = true) == true }?.model.nullSafety()
            mBodyStyleItem = Singleton.getDataSetList(DATASET_CAR_BODY_STYLE_LIST, mDb)?.firstOrNull { it.body_style?.contains(vehicleStickerInfo?.bodyStyle.nullSafety(), ignoreCase = true) == true }?.body_style.nullSafety()
            mSelectedVin = vehicleStickerInfo?.vin
            mVin = vehicleStickerInfo?.vin
            mState = STATE_NEW_YORK
    }

    override fun onResume() {
        super.onResume()
        sharedPreference.write(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
//      getLocation()
        startLocationUpdates()
        registerBroadcastReceiver()
        isActivityInForeground = true

    }
    override fun onPause() {
        super.onPause()
        isActivityInForeground = false
    }
    private fun setCapField() {
        mAutoComTextViewMakeVeh.isAllCaps = true
        mAutoComTextViewVehModel.isAllCaps = true
        mAutoComTextViewVehColor.isAllCaps = true
    }

    private fun vehicalDetailsFromDb() {
//        mDatasetList = mDb?.dbDAO?.getDataset()
    }

    private fun setToolbar() {
        initToolbar(
                0,
                this,
                R.id.layHome,
                R.id.layTicketing,
                R.id.layMyActivity,
                R.id.laySetting,
                R.id.layReport,
                R.id.layLogout,
                R.id.drawerLy,
                R.id.imgBack,
                R.id.imgOptions,
                R.id.imgCross,
                R.id.cardTicketing,
                R.id.layIssue,
                R.id.layLookup,
                R.id.layScan,
                R.id.layMunicipalCitation,
                R.id.layGuideEnforcement,
                R.id.laySummary,
                R.id.cardMyActivity,
                R.id.layMap,
                R.id.layContinue,
                R.id.cardGuide,
                R.id.laypaybyplate,
                R.id.laypaybyspace,
                R.id.cardlookup,
                R.id.laycitation,
                R.id.laylpr,
                R.id.layClearcache,
                R.id.laySuperVisorView,
                R.id.layAllReport,
                R.id.layBrokenMeterReport,
                R.id.layCurbReport,
                R.id.layFullTimeReport,
                R.id.layHandHeldMalfunctionReport,
                R.id.laySignReport,
                R.id.layVehicleInspectionReport,
                R.id.lay72HourMarkedVehiclesReport,
                R.id.layBikeInspectionReport,
                R.id.cardAllReport,
                R.id.lay_eow_supervisor_shift_report,
                R.id.layPartTimeReport,
            R.id.layLprHits,
            R.id.laySpecialAssignmentReport,
            R.id.layQRCode,
            R.id.cardQRCode,
            R.id.layGenerateQRCode,
            R.id.layScanQRCode,
            R.id.laySunlight,
            R.id.imgSunlight,
            R.id.lay72hrNoticeToTowReport,
            R.id.layTowReport,
            R.id.laySignOffReport,
            R.id.layNFL,
            R.id.layHardSummer,
            R.id.layAfterSeven,
            R.id.layPayStationReport,
            R.id.laySignageReport,
            R.id.layHomelessReport,
            R.id.laySafetyReport,
            R.id.layTrashReport,
            R.id.layLotCountVioRateReport,
            R.id.layLotInspectionReport,
            R.id.layWordOrderReport,
            R.id.txtlogout,
            R.id.laycameraviolation,
            R.id.layScanSticker,
            R.id.laygenetichit,
            R.id.layDirectedEnforcement,
            R.id.layOwnerBill
        )
    }

    private val dataFromLprResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                DynamicAPIPath.GET_DATA_FROM_LPR
        )
    }

    private val dataFromLprResponseCameraRawFeedObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                "CameraRawFeed"
        )
    }

    private val dataFromLprResponseScofflawObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                "ScofflawData"
        )
    }

    private val dataFromLprResponseExemptObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                "ExemptData"
        )
    }

    private val dataFromLprResponseStolenObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                "StolenData"
        )
    }

    private val dataFromLprResponsePaymentObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                "PaymentData"
        )
    }

    private val permitDataFromLprResponseObserver =
        Observer { apiResponse : ApiResponse ->
            consumeResponseForPermitAPI(
                apiResponse,
                DynamicAPIPath.GET_DATA_FROM_LPR
            )
        }


    private val loggerResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                DynamicAPIPath.POST_LPR_SCAN_LOGGER
        )
    }
    private val timingMarkResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                DynamicAPIPath.GET_TIMING_MARK
        )
    }
    private val inActiveMeterBuzzerResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                DynamicAPIPath.POST_INACTIVE_METER_BUZZER
        )
    }
    private val lastSecondCheckResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.GET_LAST_SECOND_CHECK)
    }

    private val addTimingResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.POST_ADD_TIMING)
    }
    private val createTicketResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CREATE_TICKET)
    }

    private fun addObservers() {
        mDataFromLprViewModel?.response?.observe(this, dataFromLprResponseObserver)
        mDataFromLprViewModel?.responseForLprPermitData?.observe(this, permitDataFromLprResponseObserver)
        mDataFromLprViewModel?.responseForCameraRawFeedData?.observe(this, dataFromLprResponseCameraRawFeedObserver)
        mDataFromLprViewModel?.responseForLprPaymentData?.observe(this, dataFromLprResponsePaymentObserver)
        mDataFromLprViewModel?.responseForLprScofflawData?.observe(this, dataFromLprResponseScofflawObserver)
        mDataFromLprViewModel?.responseForLprExemptData?.observe(this, dataFromLprResponseExemptObserver)
        mDataFromLprViewModel?.responseForLprStolenData?.observe(this, dataFromLprResponseStolenObserver)
        mLoggerViewModel?.response?.observe(this, loggerResponseObserver)
        mTimigMarkViewModel?.response?.observe(this, timingMarkResponseObserver)
        mInactiveMeterBuzzerViewModelLpr?.response?.observe(this, inActiveMeterBuzzerResponseObserver)
        mLastSecondCheckViewModel?.response?.observe(this, lastSecondCheckResponseObserver)
        mAddTimingViewModel?.response?.observe(this, addTimingResponseObserver)
        mCreateTicketViewModel?.response?.observe(this, createTicketResponseObserver)

    }

    override fun removeObservers() {
        super.removeObservers()
        mDataFromLprViewModel?.response?.removeObserver(dataFromLprResponseObserver)
        mDataFromLprViewModel?.responseForCameraRawFeedData?.removeObserver(dataFromLprResponseCameraRawFeedObserver)
        mDataFromLprViewModel?.responseForLprPaymentData?.removeObserver(dataFromLprResponsePaymentObserver)
        mDataFromLprViewModel?.responseForLprScofflawData?.removeObserver(dataFromLprResponseScofflawObserver)
        mDataFromLprViewModel?.responseForLprExemptData?.removeObserver(dataFromLprResponseExemptObserver)
        mDataFromLprViewModel?.responseForLprStolenData?.removeObserver(dataFromLprResponseStolenObserver)
        mDataFromLprViewModel?.responseForLprPermitData?.removeObserver(permitDataFromLprResponseObserver)
        mLoggerViewModel?.response?.removeObserver(loggerResponseObserver)
        mTimigMarkViewModel?.response?.removeObserver(timingMarkResponseObserver)
        mInactiveMeterBuzzerViewModelLpr?.response?.removeObserver(inActiveMeterBuzzerResponseObserver)
        mLastSecondCheckViewModel?.response?.removeObserver(lastSecondCheckResponseObserver)
        mAddTimingViewModel?.response?.removeObserver(addTimingResponseObserver)
        mCreateTicketViewModel?.response?.removeObserver(createTicketResponseObserver)
    }// this will contain "Fruit"// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
    @Throws(IOException::class)
    private fun getGeoAddress() {
        try {
            if (appCompatImageViewLock.getTag().equals("unlock")) {
                var mLat = geoLat
                var mLong = geoLon
                if (mLat > 0 && mLong > 0) {
                    mLat = geoLat
                    mLong = geoLon
                } else {
                    mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
                    mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
                }
                val geocoder: Geocoder
                var addresses: List<Address>? = null
                geocoder = Geocoder(this, Locale.getDefault())
                addresses = geocoder.getFromLocation(
                        mLat,
                        mLong,
                        1
                )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                addressGeo = addresses.firstOrNull()?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                separated = addressGeo.nullSafety().split(",").toTypedArray()
                separatedEventLogger = addressGeo.nullSafety().split(",").toTypedArray()
                val printAddress = separated!![0] // this will contain "Fruit"
                mTvReverseCoded.text = if(!printAddress.equals("null")) printAddress else ""
//                lifecycleScope.launch {
//                    showProgressLoader(getString(R.string.loader_message_fetching_address))
//                    val firstAddress = getAddressFromLatLng(this@LprDetailsActivity, mLat, mLong)
//                    dismissLoader()
//
//                    if (firstAddress.nullSafety().isNotBlank()) {
//                        //addressGeo = addresses.firstOrNull()?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                        addressGeo = firstAddress
//                        separated = addressGeo.nullSafety().split(",").toTypedArray()
//                        separatedEventLogger = addressGeo.nullSafety().split(",").toTypedArray()
//                        val printAddress = separated!![0] // this will contain "Fruit"
//                        mTvReverseCoded.text = if(!printAddress.equals("null")) printAddress else ""
//                    } else {
//                        showToast(this@LprDetailsActivity, getString(R.string.err_msg_failed_to_fetch_address))
//                    }
//                }
            } else {
                //            separated = arrayOf(addressGeo.nullSafety().split(",")!![0])
                addressGeo = sharedPreference.read(SharedPrefKey.LOCK_GEO_SAVE_ADDRESS, "")
                separated = addressGeo.nullSafety().split(",").toTypedArray()
                val printAddress = separated!![0].replace("#"," ") // this will contain "Fruit"
                separatedEventLogger = separated!![0].split("#").toTypedArray()// this will contain "Fruit"
//                val printAddressStreet = separated!![1] // this will contain "Fruit"
                if(printAddress.isEmpty())
                {
                    shadAddressIsEmpty()
                }else {
                    mTvReverseCoded.setText(if(!printAddress.equals("null"))printAddress.removePrefix("null") else "");
}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shadAddressIsEmpty(){
        try {
            var mLat = geoLat
            var mLong = geoLon
            if (mLat > 0 && mLong > 0) {
                mLat = geoLat
                mLong = geoLon
            } else {
                mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
                mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            }
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                mLat,
                mLong,
                1
            )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addressGeo =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            separated = addressGeo.nullSafety().split(",").toTypedArray()
            val printAddress = separated!![0] // this will contain "Fruit"
            separatedEventLogger = separated
            mTvReverseCoded.text = if(!printAddress.equals("null")) printAddress else ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapterForHistory(mResList: List<CitationDataResponse>?) {
        //test values

        mHistoryAdapter = HistoryAdapter(
                this@LprDetailsActivity!!,
                mResList,
                "ResultScreen",
                object :
                        HistoryAdapter.ListItemSelectListener {
                    override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                                             mStatus: Boolean, position: Int) {
                        //TODO we do not need to allow user to click issued citation as of now - Sri 23-06-2022 (7:43pm)
                        if (mResList != null && mResList.get(position) != null &&
                            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,
                                ignoreCase = true)&&
                            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,
                                ignoreCase = true)) {
                            try {
                                if (mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,
                                                    ignoreCase = true)||mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,
                                                    ignoreCase = true)||mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,
                                                    ignoreCase = true)||mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,
                                                    ignoreCase = true)||mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,
                                                    ignoreCase = true)||mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,
                                                    ignoreCase = true)||mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,
                                                    ignoreCase = true)||mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN,
                                                    ignoreCase = true)) {
    //                                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,
    //                                                ignoreCase = true)) {
    //                                    mTimingTireStem = "Prior Warning No. ".plus(
    //                                            TextViewLprNumber.text.toString().plus(" on " +
    //                                                    AppUtils.dateFormate(mResList?.get(position).citationIssueTimestamp!!.split(Regex("T"),
    //                                                            0).toTypedArray().get(0))))
    //                                }else {
                                    mTimingTireStem = "Prior Warning No. ".plus(
                                            mResList?.get(position)?.ticketNo.plus(" on " +
                                                    AppUtils.dateFormateWithSlash(mResList?.get(position)!!.citationIssueTimestamp!!.split(Regex("T"),
                                                            0).toTypedArray().get(0))))
    //                                    }
                                }else if(mResList?.get(position)?.ticketType.equals("Warning")
                                        && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,
                                    ignoreCase = true)&& mResList?.get(position)?.code.equals("EMPLOYEE PARKING VIOLATION")){
                                    mTimingTireStem = (if(mResList.size==1) "1st " else if(mResList.size==2) "2nd "
                                    else if(mResList.size==3) "3rd & Final Warning - Subject to tow, " else "Final Warning - Subject to tow, ") .plus("Prior Warning No. ").plus(
                                        mResList?.get(position)?.ticketNo.plus(" on " +
                                                AppUtils.dateFormateWithSlash(mResList?.get(position)!!.citationIssueTimestamp!!.split(Regex("T"),
                                                    0).toTypedArray().get(0))))
                                }

                                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,
                                                ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,
                                                ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,
                                                ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,
                                                ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,
                                                ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,
                                                ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,
                                                ignoreCase = true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,
                                                ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)) {
                                    mStreetItem = ""
                                    mBlock = ""
                                    mSideItem = ""
                                    mNoteItem = mResList.get(position).commentDetails!!.note_1
                                    mNoteItem1 = mResList.get(position).commentDetails!!.note_2
                                }else{
                                    mStreetItem = mResList.get(position).location!!.street
                                    mBlock = mResList.get(position).location!!.block
                                    mSideItem = mResList.get(position).location!!.side
                                    mNoteItem = mResList.get(position).commentDetails!!.note_1
                                    mNoteItem1 = mResList.get(position).commentDetails!!.note_2
                                }
                                mEscalatedLprNumber = mResList.get(position).vehicleDetails!!.lprNo
                                mEscalatedState = mResList.get(position).vehicleDetails!!.state
                                mSpaceId = mResList.get(position).location!!.spaceId
                                mMeterNameItem = mResList.get(position).location!!.meter
                                mPrintQuery = mResList.get(position).printQuery
                                mTimingClick = 2

//                                if(mResList.get(position)!!.ticketType!!.contains("Warning")) {
//                                    mTimingRecordRemarkValue =
//                                        mResList.get(position)!!.commentDetails!!.remark_1
//                                }
//                            mState = mResList.get(position).
//                                mTimingImages = mResList.get(position).images as MutableList<String>

                                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)){
                                    try {
                                        if (PermissionUtils.requestCameraAndStoragePermission(this@LprDetailsActivity)) {
                                            val mIntent = Intent(
                                                mContext,
                                                CitationHistoryPrinterActivity::class.java
                                            )
                                            if (mResList!![position]?.images?.size!! > 0) {
                                                sharedPreference.write(
                                                    SharedPrefKey.CITATION_NUMBER_FOR_PRINT,
                                                    mResList!![position].ticketNo
                                                )
                                                mIntent.putExtra(
                                                    "lpr_number",
                                                    mResList!![position].lpNumber
                                                )
                                                mIntent.putExtra(
                                                    "printerQuery",
                                                    mResList!![position].printQuery
                                                )
                                                mIntent.putExtra("State", "")
                                                mIntent.putExtra(
                                                    "citationNumber",
                                                    mResList!![position].ticketNo
                                                )

                                                var printBitmapUrl: String? = ""
                                                mResList[position].images?.firstOrNull {
                                                    it.contains(
                                                        FILE_NAME_FACSIMILE_PRINT_BITMAP
                                                    )
                                                }?.let {
                                                    printBitmapUrl = it
                                                }

                                                mIntent.putExtra("print_bitmap", printBitmapUrl)

                                                mContext!!.startActivity(mIntent)
//                            finish()
                                            }
                                        }
                                        //holder.mTicketAddress.setText(mListData.get(position).getLocation().getBlock()+","+mListData.get(position).getLocation().getStreet());
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }else {
                                    moveToNext("time")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }
                })
        mRecylerViewHistory.setHasFixedSize(true)
        mRecylerViewHistory.isNestedScrollingEnabled = false
        mRecylerViewHistory.layoutManager =
                LinearLayoutManager(this@LprDetailsActivity, RecyclerView.VERTICAL, false)
        mRecylerViewHistory.adapter = mHistoryAdapter

        citationHistoryCount = mResList!!.size
    }

    private fun setAdapterForPayment(mResList: List<PaymentDataResponse>?) {
        //test values
        if (mResList != null && mResList.size > 0) {
            val mList: List<PaymentDataResponse> = mResList
            mWelcomeForm?.paymentZoneName = mResList[mResList.size - 1].mZoneName
            mWelcomeForm?.paymentZoneID = mResList[mResList.size - 1].zoneId
            val mPaymentAdapter =
                    PaymentAdapter(this@LprDetailsActivity!!, mList, object : PaymentAdapter.ListItemSelectListener {
                        override fun onItemClick(
                                rlRowMain: LinearLayoutCompat?,
                                mStatus: Boolean,
                                position: Int
                        ) {
                        }
                    })
            mRecylerViewPayment.isNestedScrollingEnabled = false
            mRecylerViewPayment.setHasFixedSize(true)
            mRecylerViewPayment.layoutManager =
                    LinearLayoutManager(this@LprDetailsActivity, RecyclerView.VERTICAL, false)
            mRecylerViewPayment.adapter = mPaymentAdapter
        }
    }

    private fun setAdapterForPermit(mResList: List<PermitDataResponse>?) {
        //test values
        val mPermitAdapter = PermitAdapter(
                this@LprDetailsActivity!!,
                mResList,
                object :
                        PermitAdapter.ListItemSelectListener {
                    override fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean,
                                             position: Int) {

                    }
                })
        mRecylerViewPermit.isNestedScrollingEnabled = false
        mRecylerViewPermit.setHasFixedSize(true)
        mRecylerViewPermit.layoutManager =
                LinearLayoutManager(this@LprDetailsActivity, RecyclerView.VERTICAL, false)
        mRecylerViewPermit.adapter = mPermitAdapter
    }

    private fun setAdapterForScofflaw(mResList: List<ScofflawDataResponse>?) {
        //test values
        val mScofflawAdapter = ScofflawAdapter(this@LprDetailsActivity!!, mResList,
                object : ScofflawAdapter.ListItemSelectListener {
                    override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                            mStatus: Boolean, position: Int) {

                    }
                })
        mRecylerViewScofflaw.isNestedScrollingEnabled = false
        mRecylerViewScofflaw.setHasFixedSize(true)
        mRecylerViewScofflaw.layoutManager =
                LinearLayoutManager(this@LprDetailsActivity, RecyclerView.VERTICAL, false)
        mRecylerViewScofflaw.adapter = mScofflawAdapter

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)) {
            mainScope.launch {
                delay(100)
                val intent = Intent(this@LprDetailsActivity, BootActivity::class.java)
                intent.putExtra("SCCOFFLAW",
                        if (mListScofflaw != null && mListScofflaw!!.size > 0) mListScofflaw!![0] else null
                )
                intent.putExtra("MAKE", mAutoComTextViewMakeVeh.text.toString())
                intent.putExtra("MODEL", mAutoComTextViewVehModel.text.toString())
                intent.putExtra("COLOR", mAutoComTextViewVehColor.text.toString())
                intent.putExtra("LPNUMBER", TextViewLprNumber.text.toString())
                startActivity(intent)
            }
        }
    }

    private fun setAdapterForStolen(mResList: List<StolenDataResponse>?) {
        //test values
        val mStolenAdapter = StolenAdapter(this@LprDetailsActivity!!, mResList,
                object : StolenAdapter.ListItemSelectListener {
                    override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                            mStatus: Boolean,position: Int) {
                    }
                })
        mRecylerViewStolen.isNestedScrollingEnabled = false
        mRecylerViewStolen.setHasFixedSize(true)
        mRecylerViewStolen.layoutManager =
                LinearLayoutManager(this@LprDetailsActivity, RecyclerView.VERTICAL, false)
        mRecylerViewStolen.adapter = mStolenAdapter
    }

    private fun setAdapterForExempt(mResList: List<ExemptDataResponse>?) {
        //test values
        val mExemptAdapter = ExemptAdapter(this@LprDetailsActivity!!, mResList,
                object : ExemptAdapter.ListItemSelectListener {
                    override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                            mStatus: Boolean,position: Int) {
                    }
                })
        mRecylerViewExempt.isNestedScrollingEnabled = false
        mRecylerViewExempt.setHasFixedSize(true)
        mRecylerViewExempt.layoutManager =
                LinearLayoutManager(this@LprDetailsActivity, RecyclerView.VERTICAL, false)
        mRecylerViewExempt.adapter = mExemptAdapter
    }

    private fun setAdapterForCameraRawFeedData(mResList: List<ResultsItemCameraRaw>?) {
        //test values
        val mCameraRawFeedDataAdapter = CameraRawFeedDataAdapter(this@LprDetailsActivity!!, mResList,
                object : CameraRawFeedDataAdapter.ListItemSelectListener {
                    override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                            mStatus: Boolean,position: Int) {

                        try {
                            val inTime = mResList!![position].inCarImageTimestamp?.let {
                                "vehicle in time " + AppUtils.formatDateTimeForCameraRaw(it.toString())
                            } ?: ""

//                            val outTime = mResList!![position].outCarImageTimestamp?.let {
//                                "vehicle out time " + AppUtils.formatDateTimeForCameraRaw(it.toString())
//                            } ?: ""

                            val result = "$inTime"

                            ImageCache.base64Images.clear()
                            if (!mResList?.get(position)?.outCarImage.isNullOrEmpty() && mResList?.get(position)?.outCarImage!!.length > 100) {
                                ImageCache.base64Images.add(mResList?.get(position)?.outCarImage ?: "")
                            }
                            if (!mResList?.get(position)?.outPlateImage.isNullOrEmpty() && mResList?.get(position)?.outPlateImage!!.length > 100) {
                                ImageCache.base64Images.add(mResList?.get(position)?.outPlateImage ?: "")
                            }
                            if (!mResList?.get(position)?.inCarImage.isNullOrEmpty() && mResList?.get(position)?.inCarImage!!.length > 100) {
                                ImageCache.base64Images.add(mResList?.get(position)?.inCarImage ?: "")
                            }
                            if (!mResList?.get(position)?.inPlateImage.isNullOrEmpty() && mResList?.get(position)?.inPlateImage!!.length > 100) {
                                ImageCache.base64Images.add(mResList?.get(position)?.inPlateImage ?: "")
                            }

                            mStreetItem = mResList!!.get(position).street
                            mBlock = if(mResList!!.get(position).block!=null) mResList!!.get(position).block.toString() else ""
                            mSideItem = if(mResList!!.get(position).side!=null) mResList!!.get(position).side.toString() else ""
                            mDirectionItem = if(mResList!!.get(position).side!=null) mResList!!.get(position).side.toString() else ""
                            mLotItem = if(mResList!!.get(position).lot!=null) mResList!!.get(position).lot.toString() else ""
                            mMeterNameItem = if(mResList!!.get(position).meter!=null) mResList!!.get(position).meter.toString() else ""
                            mSelectedMake = if(mResList!!.get(position).make!=null) mResList!!.get(position).make.toString() else ""
                            mSelectedModel = if(mResList!!.get(position).model!=null) mResList!!.get(position).model.toString() else ""
                            mAutoComTextViewVehColor.setText(if(mResList!!.get(position).color!=null) mResList!!.get(position).color.toString() else "")
                            mAutoComTextViewMakeVeh.setText(if(mResList!!.get(position).make!=null) mResList!!.get(position).make.toString() else "")
                            mAutoComTextViewVehModel.setText(if(mResList!!.get(position).model!=null) mResList!!.get(position).model.toString() else "")
                            mBodyStyleItem = (if(mResList!!.get(position).bodyStyle!=null) mResList!!.get(position).bodyStyle.toString() else "")
                            mViolationCode = if(mResList!!.get(position).violationNumber!=null) mResList!!.get(position).violationNumber.toString() else ""
                            mTimingRecordRemarkValueCameraRawFeed = result
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        moveToNext("time")
                    }
                })
        mRecylerViewCameraRawFeedData.isNestedScrollingEnabled = false
        mRecylerViewCameraRawFeedData.setHasFixedSize(true)
        mRecylerViewCameraRawFeedData.layoutManager =
                LinearLayoutManager(this@LprDetailsActivity, RecyclerView.VERTICAL, false)
        mRecylerViewCameraRawFeedData.adapter = mCameraRawFeedDataAdapter
    }

    private fun setAdapterForTiming(mResList: List<TimingMarkData>?) {
        //test values
        //Collections.reverse(mList);
        val mTimingAdapter = TimingAdapter(this@LprDetailsActivity!!, mResList,
                "LprResultScreen", isTiming,isTireStemWithImageView, object :
                TimingAdapter.ListItemSelectListener {
            override fun onItemClick(markData: TimingMarkData?) {
                if (isTiming) {
                    if (checkBuildConfigForTimingDataClickOnScanResultScreenForAutoFillRemark()||
                        getSettingFileValuesForRemarkAutoFilledWithElapsedTime()) {
                        if (markData!!.vendorName.equals("Vigilant", ignoreCase = true)) {
//                            mTimingRecordRemarkValue = markData!!.firstObservedTimestamp
                            mTimingRecordRemarkValue =
                                splitDateLPRTime(if(markData!!.firstObservedTimestamp!=null)markData!!.firstObservedTimestamp.toString() else markData!!.markStartTimestamp!!.toString()) + " elapsed: " +
                                        AppUtils.isElapsTime(
                                            if(markData!!.firstObservedTimestamp!=null) markData!!.firstObservedTimestamp!! else markData!!.markStartTimestamp!!,
                                            markData!!.regulationTime!!, this@LprDetailsActivity
                                        )
                        } else {
                            mTimingRecordRemarkValue =
                                splitDateLPRTime(markData!!.markStartTimestamp.toString()) + " elapsed: " +
                                        AppUtils.isElapsTime(
                                            markData!!.markStartTimestamp!!,
                                            markData!!.regulationTime!!, this@LprDetailsActivity
                                        )
                        }
                    } else {
                        mTimingRecordRemarkValue =
                                splitDateLPRTime(markData!!.markStartTimestamp.toString())
                    }
                    val aFormattedFront: String = formatter.format(mListTiming!![0].tireStemFront!!.toLong())
                    val aFormattedBack: String = formatter.format(mListTiming!![0].tireStemBack!!.toLong())
                    if (aFormattedFront != null && aFormattedFront.isNotEmpty() && !aFormattedFront.equals(
                            "00"
                        )
                    ) {
                        mTimingTireStem =
                            "Tire Stem :".plus(
                                (aFormattedFront.toString() + "/"
                                        + aFormattedBack.toString())
                            )
                    } else
//                            (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) )
                    {
                        mTimingTireStem = ""
                    }
                    /**
                    * As discused with Sir,
                     * 1. Turn off auto timing after enforcement
                     * 2. Turn off enforced flag and GOA flag on  timing records after enforcement
                     */
                    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)){
                        sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "false")
                    }else {
                        sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "true")
                    }
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                        (BuildConfig.FLAVOR.equals(DuncanBrandingApp13())&&
                        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)&&
                        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA, ignoreCase = true)) ||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)) {
                        mTimingRecordRemarkValue = markData!!.remark
                        mTimingTireStem = markData!!.remark2
                    }
                    if (markData != null) {
                        mStreetItem = markData.street
                        mBlock = markData.block
                        mSideItem = markData.side
                        mDirectionItem = markData.side
                        timingId = markData.id
                        mState = markData.lpState
                        mVin = markData.vinNumber
                        mVendorName = markData.vendorName
                        if(markData.lot!=null && markData!!.lot!!.isNotEmpty()
                            && mLotItem!=null && mLotItem!!.isEmpty()) {
                            mLotItem = markData.lot
                        }
                        mViolation = "YES"//markData.violation
                        if(markData!!.images!=null) {
                            mTimingImages = markData.images as MutableList<String>
                        }
                        if(markData!!.images!=null && markData!!.images!!.size>0 &&
                            markData!!.images!!.get(0).length>100) {
                            ImageCache.base64Images.addAll(markData.images as MutableList<String>)
                        }
                        sharedPreference.write(
                                SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME,
                                AppUtils.observedTime(markData.markIssueTimestamp.nullSafety()))
                        //3. When issue ticket button is clicked, observed time and remarks should not carry over for timing violation. 03-jun
                    }
                    OVerTimeParkingForTicketDetailsCharleston(markData!!)
                    mTimingClick = 1
                    moveToNext("time")
                }
            }
        })
        mRecylerViewTiming.isNestedScrollingEnabled = false
        mRecylerViewTiming.setHasFixedSize(true)
        mRecylerViewTiming.layoutManager =
                LinearLayoutManager(this@LprDetailsActivity, RecyclerView.VERTICAL, false)
        mRecylerViewTiming.adapter = mTimingAdapter

        if (mResList != null && mResList.size > 0 && mResList!!.get(0)!!.images!!.size > 0) {
            mTimingImages = mResList!!.get(0)!!.images!! as MutableList<String>
        }
    }

    //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?,isAutoSelect: Boolean?) {
        try {
            if(linearLayoutCompatModel.visibility == View.VISIBLE) {
                //init array list
                mainScope.async {
                    if (mModelList != null) {
                        mModelList.clear()
                    }
                    val mApplicationList = Singleton.getDataSetList(DATASET_CAR_MODEL_LIST, mDb)

                    for (i in mApplicationList!!.indices) {
                        try {
                            if (mApplicationList!![i].make != null && mApplicationList!![i].make == mSelectedMake) {
                                val mDatasetResponse = DatasetResponse()
                                mDatasetResponse.model = mApplicationList!![i].model
                                mDatasetResponse.make = mApplicationList!![i].make
                                mDatasetResponse.makeText = mApplicationList!![i].makeText
                                mModelList!!.add(mDatasetResponse)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    var pos = -1
                    if (mModelList != null && mModelList.size > 0) {
                        val mDropdownList = arrayOfNulls<String>(mModelList.size)
                        for (i in mModelList.indices) {
                            mDropdownList[i] = mModelList[i].model.toString()
                            if (value != "") {
                                if (mModelList[i].make.equals(value,true)) {
                                    pos = i
                                } else if (mDropdownList[i] == value) {
                                    pos = i
                                }
                            }
                        }
                        mAutoComTextViewVehModel.post {
                            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true))
                            {
                                mAutoComTextViewVehModel.setText(value)
                                mSelectedModel = value
                            }else {
                                if (pos >= 0 && isAutoSelect == true) {
                                    mAutoComTextViewVehModel.setText(mDropdownList[pos])
                                    mSelectedModel = mModelList[pos].model
                                }
                            }

                            //Arrays.sort(mDropdownList)
                            adapterModel = ArrayAdapter(
                                    this@LprDetailsActivity,
                                    R.layout.row_dropdown_lpr_details_item,
                                    mDropdownList
                            )
                            mAutoComTextViewVehModel.threshold = 1
                            mAutoComTextViewVehModel.setAdapter<ArrayAdapter<String?>>(adapterModel)
                            mAutoComTextViewVehModel.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id ->
                                        mSelectedModel = mModelList[position].model
                                        hideSoftKeyboard(this@LprDetailsActivity)
                                    }
                        }
                    } else {
                        try {
                            mAutoComTextViewVehModel.setText("")
                            mAutoComTextViewVehModel.setAdapter(null)
                            adapterModel!!.notifyDataSetChanged()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle(value: String?,valueModel: String?) {
        //init array list
        try {
            mainScope.async {
                val mApplicationList = Singleton.getDataSetList(DATASET_CAR_MAKE_LIST, mDb)
                if (mApplicationList != null && mApplicationList?.size.nullSafety() > 0) {
                    if (uniqueDataSet == null || uniqueDataSet.size < 1) {
                        if (mApplicationList != null && mApplicationList?.size.nullSafety() > 0) {
                            for (i in mApplicationList!!.indices) {
                                uniqueDataSet!!.add(mApplicationList!![i].make.toString() + "#" + mApplicationList!![i].makeText.toString())
                            }
                        }
                    }
                    val Geeks = uniqueDataSet!!.toTypedArray()
                    var pos = -1
                    var makeValue: String = ""
                    Arrays.sort(Geeks)
                    if (uniqueDataSet != null && uniqueDataSet.size > 0) {
                        val mDropdownList = arrayOfNulls<String>(uniqueDataSet.size)
                        for (i in Geeks.indices) {
                            mDropdownList[i] = Geeks[i].split("#").toTypedArray()[1]
                            if (value != "") {
                                val splitValue = Geeks[i].split("#").toTypedArray()
                                if (splitValue[0].equals(value,true) || splitValue[1].equals(value,true)) {
                                    pos = i
                                    makeValue = splitValue[1].toString()
                                }
                            }
                        }
                        //                Arrays.sort(mDropdownList);
//                Arrays.sort(Geeks);
                        mAutoComTextViewMakeVeh.post {
                            if (pos >= 0) {
                                mAutoComTextViewMakeVeh?.setText(makeValue)
                                mSelectedMake = Geeks[pos].split("#").toTypedArray()[0]
                                mSelectedMakeValue = Geeks[pos].split("#").toTypedArray()[1]
                                setDropdownVehicleModel(valueModel,true)
                            }
                            val adapter = ArrayAdapter(
                                    this@LprDetailsActivity,
                                    R.layout.row_dropdown_lpr_details_item,
                                    mDropdownList
                            )
                            mAutoComTextViewMakeVeh.threshold = 1
                            mAutoComTextViewMakeVeh.setAdapter<ArrayAdapter<String?>>(adapter)
                            mAutoComTextViewMakeVeh.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id -> //                        mSelectedMake = mDropdownList[position];
//                        mSelectedMakeValue = mApplicationList.get(position).getMake();
                                        CoroutineScope(Dispatchers.IO).async {
                                            val index = getIndexOf(
                                                    mApplicationList!!,
                                                    parent.getItemAtPosition(position).toString())
                                            mSelectedMake = mApplicationList!![index].make!!
                                            mSelectedMakeValue = mApplicationList!![index].makeText
                                            hideSoftKeyboard(this@LprDetailsActivity)
                                            MainScope().launch {
                                                if (mSelectedMakeValue != null) {
                                                    setDropdownVehicleModel(mSelectedMake,false)
                                                } else {
                                                    setDropdownVehicleModel("",false)
                                                }
                                            }
                                        }
                                    }
                        }
                    }
                } else {
//                    if (mDataSetTimeObject != null) {
//                        mDataSetTimeObject?.timeList?.carMakeList?.status = false
//                        mDb?.dbDAO?.insertUpdatedTime(mDataSetTimeObject!!)
//                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOf(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.makeText, ignoreCase = true)) return pos
            pos++
        }
        return 0
    }

    //set value to vehicle colour dropdown
    private fun setDropdownVehicleColour(value: String?) {
        try {
            //init array list
            //val mApplicationList = mDatasetList?.dataset?.carColorList
            mainScope.async {
                var pos = -1
                val mApplicationList = Singleton.getDataSetList(DATASET_CAR_COLOR_LIST, mDb)

                try {
                    Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                        override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                            return lhs?.description.nullSafety()
                                    .compareTo(rhs?.description.nullSafety())
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList?.size.nullSafety())
                    for (i in mApplicationList!!.indices) {
                        mDropdownList[i] = mApplicationList!![i].description.toString()
                        if (value != "") {
                            if (mDropdownList[i] == value ||
                                    mApplicationList!![i].color_code != null && mApplicationList!![i].color_code.equals(
                                            value, ignoreCase = true)) {
                                pos = i

                            }
                        }
                    }
                    mAutoComTextViewVehColor.post {
//                 3rd option for TODO       mAutoComTextViewVehColor.post { mAutoComTextViewVehColor.setText(mDropdownList[pos])  }
                        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)) {
                            mAutoComTextViewVehColor.setText(value)
                            mSelectedColor = value
                        }else {
                            if (pos >= 0) {
                                mAutoComTextViewVehColor.setText(mDropdownList[pos])
                                mSelectedColor = mDropdownList[pos]
                            }
                            }
                        val adapter = ArrayAdapter(
                                this@LprDetailsActivity,
                                R.layout.row_dropdown_lpr_details_item,
                                mDropdownList
                        )
                        mAutoComTextViewVehColor.threshold = 1
                        mAutoComTextViewVehColor.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewVehColor.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    hideSoftKeyboard(this@LprDetailsActivity)
                                    CoroutineScope(Dispatchers.IO).async {
                                        val index: Int =
                                                getIndexOcolor(mApplicationList!!, mAutoComTextViewVehColor?.text.toString())
                                        mSelectedColor = mDropdownList[index]
                                    }
                                }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOcolor(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.description, ignoreCase = true)) return pos
            pos++
        }
        return 0
    }

    private fun setDropdownLot() {
        //init array list
        hideSoftKeyboard(this@LprDetailsActivity)
        var pos = -1
            ioScope.launch {
                val mApplicationList = Singleton.getDataSetList(DATASET_LOT_LIST, mDb)
                if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].location.toString()
                    try {
                        if(mWelcomeForm!!.lot!!.isNotEmpty() && mApplicationList[i].location.equals(mWelcomeForm!!.lot,ignoreCase = true)
                            || mApplicationList[i].location.equals(sharedPreference.read(SharedPrefKey.SCAN_RESULT_LOT, "")))
                        {
                            pos = i
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
//                Arrays.sort(mDropdownList)
                mAutoComTextViewLot?.post {
                    if(pos>=0)
                    {
                        try {
                            mAutoComTextViewLot!!.setText(mDropdownList[pos])
                            mLotItem = mApplicationList[pos].lot.toString()
                        } catch (e: Exception) {
                        }
                    }
                    val adapter = ArrayAdapter(this@LprDetailsActivity,
                        R.layout.row_dropdown_menu_popup_item, mDropdownList)

                    try {
                        mAutoComTextViewLot!!.threshold = 1
                        mAutoComTextViewLot!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewLot!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = getIndexOfLocation(
                                    mApplicationList,
                                    parent.getItemAtPosition(position).toString()
                                )

                                mLotItem = mApplicationList[index].lot.toString()
                                sharedPreference.write(SharedPrefKey.SCAN_RESULT_LOT, mLotItem)
                                hideSoftKeyboard(this@LprDetailsActivity)
                                scope.launch() {
                                    isMakeLoader = true
                                    btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                                    btnPayment.setTextColor(resources.getColor(R.color.white))
                                    if(linearLayoutCompatZone!!.visibility == View.VISIBLE) {
                                        if (mPaymentByZone.equals("YES")) {
                                            withContext(Dispatchers.Default) {
                                                callGetDataFromLprApiForPayment(
                                                    TextViewLprNumber.editableText.toString()
                                                        .trim(), "PaymentDataByZone"
                                                )
                                            }
                                        }
                                        if (mAllPaymentsInZoneFuzzy.equals("YES")) {
                                            withContext(Dispatchers.Default) {
                                                callGetDataFromLprApiForPayment(
                                                    TextViewLprNumber.editableText.toString()
                                                        .trim(), "AllPaymentsInZoneFuzzy"
                                                )
                                            }
                                        }
                                        if (mPermitByZone.equals("YES")) {
                                            withContext(Dispatchers.Default) {
//                                                callGetDataFromLprApi(
//                                                    TextViewLprNumber.editableText.toString()
//                                                        .trim(), "PermitDataByZone"
//                                                )

                                                callGetDataFromLprApiForPermit(
                                                    TextViewLprNumber.editableText.toString()
                                                        .trim(), "PermitDataByZone"
                                                )
                                            }
                                        }
                                    }

                                }
                            }
                        if (mAutoComTextViewLot!!.tag != null && mAutoComTextViewLot!!.tag == "listonly") {
                            AppUtils.setListOnly(this@LprDetailsActivity, mAutoComTextViewLot!!)

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getIndexOfLocation(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.location, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    @Throws(FileNotFoundException::class)
    private fun readCsv() {
        val mScanner = Scanner(File("$mPath/parkingList.csv"))
        val mCsvDataList = ArrayList<String>()
        while (mScanner.hasNextLine()) {
            mCsvDataList.add(mScanner.nextLine().split(",").toTypedArray()[0])
        }
        mScanner.close()
        printLog("csv", mCsvDataList)
        //LogUtil.printToastMSG(LprDetailsActivity.this,"csv last name"+mCsvDataList.get(mCsvDataList.size()-1));
    }

    /* Call Api to get DataFromLpr */
    private fun callGetDataFromLprApi(mLprNumber: String, mType: String) {
        if (isInternetAvailable(this@LprDetailsActivity)) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            //            mDataFromLprRequest.setLocation(mDataFromLprLocation);
            mDataFromLprRequest.page = 1.toLong()
                if (mType.equals("PermitDataByZone", ignoreCase = true) && mLotItem != null &&
                    mAutoComTextViewLot!=null && mAutoComTextViewLot.text!!.isNotEmpty()) {
                    mDataFromLprRequest.zone = mLotItem!!
                }
                if (mType.equals("PaymentDataByZone", ignoreCase = true) && mLotItem != null&&
                    mAutoComTextViewLot!=null && mAutoComTextViewLot.text!!.isNotEmpty()) {
                    mDataFromLprRequest.zone = mLotItem!!
                }
            /**
             * for sanibel Varun remove empty check
             */
//                if (mType.equals("AllPaymentsInZoneFuzzy", ignoreCase = true) && mLotItem != null&&
//                    mAutoComTextViewLot!=null && mAutoComTextViewLot.text!!.isNotEmpty()) {
            if (mType.equals("AllPaymentsInZoneFuzzy", ignoreCase = true)) {
                    mDataFromLprRequest.zone = if(mLotItem!=null && mAutoComTextViewLot!=null)mLotItem!! else ""
                }
                mDataFromLprViewModel!!.hitGetDataFromLprApi(mDataFromLprRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE){
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"------------Get Data LPR API-----------------")
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"REQUEST: "+ObjectMapperProvider.instance.writeValueAsString(mDataFromLprRequest))
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            printToastMSG(
                    this@LprDetailsActivity,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling all permit related query separately
     */
    private fun callGetDataFromLprApiForPermit(mLprNumber: String, mType: String) {
        if (isInternetAvailable(this@LprDetailsActivity)) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            if (mType.equals("PermitDataByZone", ignoreCase = true) && mLotItem != null &&
                mAutoComTextViewLot != null
                || mType.equals("PermitDataV2", ignoreCase = true) && mLotItem != null &&
                mAutoComTextViewLot != null) {
                mDataFromLprRequest.zone = mLotItem!!
            }

            mDataFromLprViewModel?.hitGetPermitDataFromLprApi(
                mDataFromLprRequest,
                mType,
                mLprNumber
            )

            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "------------Get Data LPR API Permit -----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: Permit " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            printToastMSG(
                this@LprDetailsActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling all payment related query separately
     */
    private fun callGetDataFromLprApiForPayment(mLprNumber: String, mType: String) {
        if (isInternetAvailable(this@LprDetailsActivity)) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            if (mType.equals("PaymentDataByZone", ignoreCase = true) && mLotItem != null &&
                mAutoComTextViewLot != null && mAutoComTextViewLot.text!!.isNotEmpty()
            ) {
                mDataFromLprRequest.zone = mLotItem!!
            }

            mDataFromLprViewModel?.hitGetPaymentDataFromLprApi(
                mDataFromLprRequest,
                mType,
                mLprNumber
            )

            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "------------Get Data LPR API Payment-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: Payment " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            printToastMSG(
                this@LprDetailsActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }
    /**
     * We are calling all Scofflaw related query separately
     */
    private fun callGetDataFromLprApiForScofflaw(mLprNumber: String, mType: String) {
        if (isInternetAvailable(this@LprDetailsActivity)) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            mDataFromLprViewModel?.hitGetScofflawDataFromLprApi(
                mDataFromLprRequest,
                mType,
                mLprNumber
            )

            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "------------Get Data LPR API Scofflaw-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: Scofflaw " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            printToastMSG(
                this@LprDetailsActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }
    /**
     * We are calling all Scofflaw related query separately
     */
    private fun callGetDataFromLprApiForExempt(mLprNumber: String, mType: String) {
        if (isInternetAvailable(this@LprDetailsActivity)) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            mDataFromLprViewModel?.hitGetExemptDataFromLprApi(
                mDataFromLprRequest,
                mType,
                mLprNumber
            )

            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "------------Get Data LPR API Exempt-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: Exempt " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            printToastMSG(
                this@LprDetailsActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling all Scofflaw related query separately
     */
    private fun callGetDataFromLprApiForStolen(mLprNumber: String, mType: String) {
        if (isInternetAvailable(this@LprDetailsActivity)) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            mDataFromLprViewModel?.hitGetStolenDataFromLprApi(
                mDataFromLprRequest,
                mType,
                mLprNumber
            )

            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "------------Get Data LPR API Exempt-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: Exempt " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            printToastMSG(
                this@LprDetailsActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
    * We are calling Camera Raw Feed API separately
    */
    private fun callGetDataFromLprForCameraRawFeedApi(mLprNumber: String, mType: String) {
        if (isInternetAvailable(this@LprDetailsActivity)) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            mDataFromLprViewModel!!.hitGetCameraRawFeedApi(mDataFromLprRequest,mType,
                mLprNumber)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "------------Get Data LPR API Camera Raw Feed-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST Camera Raw Feed : " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            printToastMSG(
                this@LprDetailsActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api to get DataFromLpr */
    private fun callGetTimingApi(mLprNumber: String) {
        if (isInternetAvailable(this@LprDetailsActivity)) {

            //Returns current time in millis
            var timeMilli1=""
            var timeMilli2=""

            if(mHour.isNotEmpty()){
                timeMilli2 = AppUtils.getStartTDateWithSetting(mZone)
                timeMilli1 = AppUtils.getStartTDateAddSettingHourValue(mZone,mHour)
            }else{
                timeMilli1 = AppUtils.getStartTDate(mZone)
                timeMilli2 = AppUtils.getEndTDate(mZone)

            }

//            val mReq =
//                "issue_ts_from=" + timeMilli1 + "&issue_ts_to=" + timeMilli2 + "&arrival_status=Open&lp_number=" + mLprNumber
            if (checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpenGOA()) {
                val mReq = "arrival_status=Open,GOA,Violated&" +
                        "issue_ts_from=" + timeMilli1 + "&issue_ts_to=" + timeMilli2 + "&lp_number=" + mLprNumber
                mTimigMarkViewModel?.hitTimigMarkApi(mReq)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "------------Get Data LPR API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(mReq)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpen()) {

                val mReq = "arrival_status=Open&" +
                        "issue_ts_from=" + timeMilli1 + "&issue_ts_to=" + timeMilli2 + "&lp_number=" + mLprNumber
                mTimigMarkViewModel?.hitTimigMarkApi(mReq)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "------------Get Data LPR API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(mReq)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val mReq = "issue_ts_from=" + timeMilli1 + "&issue_ts_to=" + timeMilli2 + "&lp_number=" + mLprNumber
                mTimigMarkViewModel?.hitTimigMarkApi(mReq)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "------------Get Data LPR API Timing-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: Timing " + ObjectMapperProvider.instance.writeValueAsString(
                                mReq
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            printToastMSG(
                    this@LprDetailsActivity,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun callLastSecondCheckAPI() {
//        {{local}}/analytics/mobile/pay_by_plate?zone=Indigo&Park&lp_number=VIM112
        if (NetworkCheck.isInternetAvailable(this@LprDetailsActivity)) {
            var endPoint = ""
            endPoint =
                "lp_number=" + TextViewLprNumber.editableText.toString().trim()

            mLastSecondCheckViewModel?.hitLastSecondCheckApi(endPoint)
        } else {
            LogUtil.printToastMSG(this@LprDetailsActivity,
                getString(R.string.err_msg_connection_was_refused))
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> if (tag.equals(
                            DynamicAPIPath.GET_TIMING_MARK,
                            ignoreCase = true
                    )|| tag.equals(
                            DynamicAPIPath.GET_LAST_SECOND_CHECK,
                            ignoreCase = true
                    )|| isMakeLoader
            ) showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS ->                 //
                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                            ApiLogsClass.writeApiPayloadTex(
                                BaseApplication.instance?.applicationContext!!,
                                "------------Response -----------------" + tag
                            )
                            ApiLogsClass.writeApiPayloadTex(
                                BaseApplication.instance?.applicationContext!!,
                                "Response: " + ObjectMapperProvider.instance.writeValueAsString(
                                    apiResponse.data
                                )
                            )
                        }
                        } catch (e: Exception) {
                        e.printStackTrace()
                        LogUtil.printLogHeader("get data lpr error top ",tag)
                    }
                    try {
                        if (tag.equals(DynamicAPIPath.GET_DATA_FROM_LPR, ignoreCase = true)) {
                            //TODO create response model
                            try {
                                citationResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CitationResponse::class.java)


                                if (citationResponse != null && citationResponse!!.status.nullSafety()) {
                                    if ((citationResponse!!.dataCitation!![0].response!!.type == "CitationData")||
                                        citationResponse!!.dataCitation!![0].response!!.type == "CitationDataT2") {
                                        mDataSetTimeObject = mDb?.dbDAO?.getUpdateTimeResponse()
                                        CitationDataLength =
                                            citationResponse!!.dataCitation!![0].response!!.length
                                        mListCitation =
                                            citationResponse!!.dataCitation!![0].response!!.results
                                        if (isFineSumVisible) {
                                            mFineAmountSum =
                                                citationResponse!!.dataCitation!![0].response!!.fineAmountSum.nullSafety(
                                                    0.00)
                                        }

                                        mainScope.launch {
                                            if (mListCitation!!.size > 0) {
                                                mLayHistoryTextSection.visibility = View.VISIBLE
                                                mRecylerViewHistory.visibility = View.VISIBLE
                                                mEmptyLayout.visibility = View.GONE
                                                mTextViewTabHistory.text =
                                                    getString(R.string.scr_lbl_cite_history) + " - " + mListCitation?.size
                                                if (isFineSumVisible) {
                                                    AppCompatTextViewFineAmountSum.visibility =
                                                        View.VISIBLE
                                                    AppCompatTextViewFineAmountSum.text =
                                                        getString(R.string.scr_lbl_fine_amount_sum) + " $" + (String.format("%.2f", mFineAmountSum))
                                                } else {
                                                    AppCompatTextViewFineAmountSum.visibility =
                                                        View.GONE
                                                }

                                                /*if(mListCitation?.get(0)?.ticketType.equals("Warning")) {
                                                    mTimingTireStem = "Prior Warning No. ".plus(
                                                            TextViewLprNumber.text.toString().plus(" on "+
                                                                    AppUtils.dateFormate(mListCitation!!.get(0).citationIssueTimestamp!!.split(Regex("T"),
                                                                            0).toTypedArray().get(0))))
                                                }*/
                                                setAdapterForHistory(mListCitation)
//                                                if(mListCitation!!.size>0 && mLayHistory.visibility == View.VISIBLE)
//                                                {
//                                                    setHeightOfHistoryRecycleView()
//                                                }
//                                                if (BuildConfig.FLAVOR.equals(
//                                                        Constants.FLAVOR_TYPE_DUNCAN,
//                                                        ignoreCase = true
//                                                    )||BuildConfig.FLAVOR.equals(
//                                                        Constants.FLAVOR_TYPE_MEMORIALHERMAN,
//                                                        ignoreCase = true
//                                                    )||BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
//                                                ) {
//                                                    setHeightOfHistoryRecycleView()
//                                                }
                                            } else {
                                                mTextViewTabHistory.text =
                                                    getString(R.string.scr_lbl_cite_history) + " - 0"
                                                mRecylerViewHistory.visibility = View.GONE
                                                mEmptyLayout.visibility = View.VISIBLE
                                                mLayHistoryTextSection.visibility = View.GONE

//                                                try {
//                                                    //mLayHistory.layoutParams.height = 200
//                                                    mLayHistory.layoutParams.height = getOneThirdOfDeviceHeight(this@LprDetailsActivity)
//                                                    mLayHistory.invalidate()
//                                                    mRecylerViewHistory.invalidate()
//                                                    btnIssueHistory.invalidate()
//                                                } catch (e: Exception) {
//                                                }
                                            }

                                            //Set ADA
                                            mTextViewTabHistory.contentDescription = getString(R.string.ada_content_description_tab_unselected, mTextViewTabHistory.text.toString())
                                        }
                                    } else {
                                        //mRecylerViewHistory.setVisibility(View.GONE);
                                        //mEmptyLayout.setVisibility(View.VISIBLE);
                                        /* btnStolen.setText(getString(R.string.scr_btn_no_stolen));
                                                btnStolen.setBackgroundResource(R.drawable.button_round_corner_shape_greyy);
                                                btnStolen.setTextColor(getResources().getColor(R.color.white));*/
                                    }
                                } else if ((citationResponse != null && !citationResponse!!.status!!&&
                                    citationResponse!!.dataCitation!![0].response!!.type == "CitationData")||
                                    (citationResponse != null && !citationResponse!!.status!!&&
                                    citationResponse!!.dataCitation!![0].response!!.type == "CitationDataT2")) {
                                    val message: String?
                                    if (citationResponse!!.response != null && citationResponse!!.response != "") {
                                        message = citationResponse!!.response
                                        showCustomAlertDialog(
                                                this@LprDetailsActivity, "GET_DATA_FROM_LPR-Citation",
                                                message, "Ok", "Cancel", this
                                        )
                                    } else {
                                        citationResponse!!.response =
                                                "Not getting response from server..!!"
                                        message = citationResponse!!.response
                                        showCustomAlertDialog(
                                                this@LprDetailsActivity, "GET_DATA_FROM_LPR-Citation",
                                                message, "Ok", "Cancel", this
                                        )
                                    }
                                } else {
                                    showCustomAlertDialog(
                                            this@LprDetailsActivity, "GET_DATA_FROM_LPR-Citation",
                                            "Something wen't wrong..!!", "Ok", "Cancel",
                                            this
                                    )
                                    hideAPILoader()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                                LogUtil.printLogHeader("get data lpr error ",tag+" citationResponse")
                            }
                            try {

                                modelColorResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), MakeModelColorResponse::class.java)

                                if (modelColorResponse != null && modelColorResponse!!.status!!) {
                                    if (modelColorResponse!!.dataMakeModelColor!![0].response!!.type == "MakeModelColorData") {
                                        hideAPILoader()
                                        isMakeLoader = false
                                        mListMakeModelColor =
                                                modelColorResponse!!.dataMakeModelColor!![0].response!!.results
                                        modelColorLength =
                                                modelColorResponse!!.dataMakeModelColor!![0].response!!.length

                                        if (mListMakeModelColor!!.size > 0) {
                                            setDropdownVehicleColour(modelColorResponse!!.dataMakeModelColor!![0].response!!.results!![0].color)
                                            setDropdownMakeVehicle(modelColorResponse!!.dataMakeModelColor!![0].response!!.results!![0].make,
                                                    modelColorResponse!!.dataMakeModelColor!![0].response!!.results!![0].model)
                                            mBodyStyleItem = modelColorResponse!!.dataMakeModelColor!![0].response!!.results!![0].bodyStyle
                                            mSelectedModel = modelColorResponse!!.dataMakeModelColor!![0].response!!.results!![0].model
                                            mSelectedVin = modelColorResponse!!.dataMakeModelColor!![0].response!!.results!![0].vinNumber
//                                            setDropdownVehicleModel(modelColorResponse!!.dataMakeModelColor!![0].response!!.results!![0].model,true)
                                        }

                                        if (TextViewLprNumber.editableText.toString().trim() != null &&
                                                TextViewLprNumber.editableText.toString() != "") {
                                            mainScope.launch {
                                                delay((eventLoggerAPIDelay * 1000).toLong())
                                                try {
                                                    callPushEventApi(lpNumberForLoggerAPI!!)
//                                                    Auto Timing for Laz LB
                                                    if(mAutoTiming.equals("YES",ignoreCase = true)) {
                                                        callAddTimingApi()
                                                    }
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                } catch (e: ParseException) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                    "Please Enter Lpr Number",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else if (modelColorResponse != null && !modelColorResponse!!.status!!&&
                                    modelColorResponse!!.dataMakeModelColor!![0].response!!.type == "MakeModelColorData") {
                                    val message: String?
                                    isMakeLoader = false
                                    if (modelColorResponse!!.response != null && modelColorResponse!!.response != "") {
                                        message = modelColorResponse!!.response
                                        showCustomAlertDialog(
                                                this@LprDetailsActivity, "GET_DATA_FROM_LPR-MakeModelColor",
                                                message, "Ok", "Cancel", this
                                        )
                                    } else {
                                        modelColorResponse!!.response =
                                                "Not getting response from server..!!"
                                        message = modelColorResponse!!.response
                                        showCustomAlertDialog(
                                                this@LprDetailsActivity, "GET_DATA_FROM_LPR-MakeModelColor",
                                                message, "Ok", "Cancel", this
                                        )
                                    }
                                } else {
                                    isMakeLoader = false
                                    showCustomAlertDialog(
                                            this@LprDetailsActivity, "GET_DATA_FROM_LPR-MakeModelColor",
                                            "Something wen't wrong..!!", "Ok", "Cancel",
                                            this
                                    )
                                    hideAPILoader()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                                LogUtil.printLogHeader("get data lpr error ",tag+" modelColorResponse")
                            }

                        }else  if (tag.equals("PaymentData", ignoreCase = true)) {
                            try {
                                 paymentResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), PaymentResponse::class.java)

                                if (paymentResponse != null && paymentResponse!!.status!!) {
                                    if (paymentResponse!!.dataPayment!![0].response!!.type == "PaymentData"||
                                        paymentResponse!!.dataPayment!![0].response!!.type == "PaymentDataByZone"||
                                        paymentResponse!!.dataPayment!![0].response!!.type == "AllPaymentsInZoneFuzzy") {
                                        PaymentLength =
                                            paymentResponse!!.dataPayment!![0].response!!.length
                                        mListPayment =
                                            paymentResponse!!.dataPayment!![0].response!!.results
                                        val platesArray =
                                            paymentResponse!!.dataPayment!![0].response!!.plates
                                        if (sixActionButtonVisibilityCheck("HAS_PAYMENTS") && mListPayment!!.isNotEmpty()) {
                                            sharedPreference.write(
                                                SharedPrefKey.PAY_BY_ZONE_SPACE,
                                                if (mListPayment!![0].mZoneName != null) mListPayment!![0].mZoneName else ""
                                            )
                                            isPayment = false
                                            mLayPayment.visibility = View.VISIBLE
                                            mLayHide.visibility = View.VISIBLE
                                            mLayHide.setBackgroundResource(R.drawable.round_corner_shape_without_fill_green)
                                            setAdapterForPayment(mListPayment)
//                                            val mPay =
//                                                paymentResponse!!.dataPayment!![0].response!!.results!![0]
                                            if (mListPayment != null && mListPayment!!.size > 0) {
                                                for (index in paymentResponse!!.dataPayment!![0].response!!.results!!.indices) {
                                                    if (paymentResponse!!.dataPayment!![0].response!!.results!![0].mExpiryTimeStamp != null && compareDates(
                                                            paymentResponse!!.dataPayment!![0].response!!.results!![0].mExpiryTimeStamp!!) == 1) {
                                                        isPayment = true
                                                        btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                                                        btnPayment.setTextColor(resources.getColor(R.color.white))
                                                        mLayHide.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                                                    }else {
                                                        btnPayment.text =
                                                            getString(R.string.scr_btn_payment)
                                                        btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_green)
                                                        btnPayment.setTextColor(resources.getColor(R.color.black))
                                                        break
                                                    }
                                                }
                                            }
                                        } else {
                                            isPayment = false
                                            mLayPayment.visibility = View.GONE
                                            mLayHide.visibility = View.GONE
                                        }
                                        /**
                                         * ALL paymentdata fuzzy
                                         */
                                        if (platesArray != null && platesArray.size > 0) {
                                            for (index in platesArray!!.indices) {
                                                fuzzyPlates.append(platesArray!!.get(index).toString() + ", ")
                                            }
                                            if (platesArray!!.size > 0 && mListPayment != null && mListPayment!!.size == 0) {

                                                if (mListPayment!!.size == 0) {
                                                    btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                                                    btnPayment.setTextColor(resources.getColor(R.color.black))

                                                    appCompatTextViewPaymentFuzzyPlate.visibility =
                                                        View.VISIBLE
                                                }
                                                if (platesArray!!.size > 1) {
                                                    btnPayment.text =
                                                        getString(R.string.scr_btn_payment)
                                                    appCompatTextViewPaymentFuzzyPlate.text =
                                                        (getString(R.string.scr_lbl_payment_for_similar_plates) +
                                                                fuzzyPlates)
                                                } else {
                                                    btnPayment.text =
                                                        getString(R.string.scr_btn_payment)
                                                    appCompatTextViewPaymentFuzzyPlate.text =
                                                        (getString(R.string.scr_lbl_payment_for_similar_plate) +
                                                                fuzzyPlates)
                                                }
                                            }
                                        }
                                    } else if (paymentResponse!!.dataPayment!![0].response!!.type == "PaymentDataFuzzy") {
                                        PaymentFuzzyLength =
                                            paymentResponse!!.dataPayment!![0].response!!.length
                                        mListPaymentFuzzy =
                                            paymentResponse!!.dataPayment!![0].response!!.results



//                                        var paymentPlates:java.lang.StringBuilder = ""
//                                        val paymentPlates = StringBuilder()
                                        for(index in mListPaymentFuzzy!!.indices)
                                        {
                                            fuzzyPlates.append(mListPaymentFuzzy!!.get(index).lpNumber.toString()+", ")
                                        }
                                        if(mListPaymentFuzzy!!.size>0) {
                                            if(mListPayment != null && mListPayment!!.size == 0) {
                                                btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                                                btnPayment.setTextColor(resources.getColor(R.color.black))

                                                appCompatTextViewPaymentFuzzyPlate.visibility =
                                                    View.VISIBLE

                                                if (mListPaymentFuzzy!!.size > 1) {
                                                    btnPayment.text =
                                                        getString(R.string.scr_btn_payment)
                                                    appCompatTextViewPaymentFuzzyPlate.text =
                                                        (getString(R.string.scr_lbl_payment_for_similar_plates) +
                                                                fuzzyPlates)
                                                } else {
                                                    btnPayment.text =
                                                        getString(R.string.scr_btn_payment)
                                                    appCompatTextViewPaymentFuzzyPlate.text =
                                                        (getString(R.string.scr_lbl_payment_for_similar_plate) +
                                                                fuzzyPlates)
                                                }
                                            }
                                        }else{
//                                            btnPayment.text =
//                                                getString(R.string.scr_btn_no_payment)
//                                            btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
//                                            btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
//                                            btnPayment.setTextColor(resources.getColor(R.color.white))
                                        }
                                    }
                                } else if (paymentResponse != null && !paymentResponse!!.status!! &&
                                    paymentResponse!!.dataPayment!![0].response!!.type == "PaymentDataFuzzy"||
                                    paymentResponse != null && !paymentResponse!!.status!! &&
                                    paymentResponse!!.dataPayment!![0].response!!.type == "PaymentData") {
                                    val message: String?
                                    if (paymentResponse!!.response != null && paymentResponse!!.response != "") {
                                        message = paymentResponse!!.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, getString(R.string.scr_lbl_lpr_payment),
                                            message, getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel), this
                                        )
                                    } else {
                                        paymentResponse!!.response =
                                            getString(R.string.err_msg_something_went_wrong)
                                        message = paymentResponse!!.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, getString(R.string.scr_lbl_lpr_payment),
                                            message, getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel), this
                                        )
                                    }
                                } else {
                                    showCustomAlertDialog(
                                        this@LprDetailsActivity,
                                        getString(R.string.scr_lbl_lpr_payment),
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                    hideAPILoader()
                                }
                                hideAPILoader()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                                LogUtil.printLogHeader("get data lpr error ",tag+" paymentResponse")
                            }
                        }else if (tag.equals("ScofflawData", ignoreCase = true)) {
                            try {

                                 scofflawResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ScofflawResponse::class.java)

                                if (scofflawResponse != null && scofflawResponse!!.status!!) {
                                    if (scofflawResponse!!.dataScofflaw!![0].response!!.type == "ScofflawData"||
                                        scofflawResponse!!.dataScofflaw!![0].response!!.type == "ScofflawDataT2") {
                                        ScofflawDataLength =
                                            scofflawResponse!!.dataScofflaw!![0].response!!.length
                                        mListScofflaw =
                                            scofflawResponse!!.dataScofflaw!![0].response!!.results
                                        if (sixActionButtonVisibilityCheck("HAS_SCOFFLAW") && mListScofflaw!!.size > 0) {
                                            mLayScofflaw.visibility = View.VISIBLE
                                            mLayScofflaw.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                                            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)){
                                                /**For rutgers we are display all data and rest of the app only first index
                                                mListScofflawFirstIndex =
                                                listOf(mListScofflaw!!.get(0))**/
                                                setAdapterForScofflaw(mListScofflaw)
                                                if (Constants.stickerList.contains(mListScofflaw!!.get(0).type)) {
                                                    btnWhiteSticker.visibility = View.VISIBLE
                                                    btnRedSticker.visibility = View.VISIBLE
                                                }else{
                                                    btnWhiteSticker.visibility = View.GONE
                                                    btnRedSticker.visibility = View.GONE
                                                }
                                            }else {
                                                mListScofflawFirstIndex =
                                                    listOf(mListScofflaw!!.get(0))
                                                setAdapterForScofflaw(mListScofflawFirstIndex)
                                            }
                                            btnScofflaw.text =
                                                getString(R.string.scr_btn_scofflaw)
                                            btnScofflaw.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                                            btnScofflaw.setTextColor(resources.getColor(R.color.white))

//                                            if (scofflawResponse.getDataScofflaw().get(0).getResponse().getResults().get(0).getVendorName().equalsIgnoreCase("Vigilant")) {
//                                                if (scofflawResponse.getDataScofflaw().get(0).getResponse().getResults().get(0).getmIsViolation()) {
//                                                    if (scofflawResponse.getDataScofflaw().get(0).getResponse().getResults().get(0).getAlertType().equalsIgnoreCase("ScofflawAlert")) {
//                                                        set6ButtonColor(mLayScofflaw, btnScofflaw, 1);//RED
//                                                    } else if (scofflawResponse.getDataScofflaw().get(0).getResponse().getResults().get(0).getAlertType().equalsIgnoreCase("Unauthorized Vehicle;ScofflawAlert")) {
//                                                        set6ButtonColor(mLayScofflaw, btnScofflaw, 1);//RED
//                                                    } else if (scofflawResponse.getDataScofflaw().get(0).getResponse().getResults().get(0).getAlertType().equalsIgnoreCase("Expired Parking;ScofflawAlert")) {
//                                                        set6ButtonColor(mLayScofflaw, btnScofflaw, 1);//RED
//                                                    } else if (scofflawResponse.getDataScofflaw().get(0).getResponse().getResults().get(0).getAlertType().equalsIgnoreCase("Expired Parking")) {
//                                                        set6ButtonColor(mLayScofflaw, btnScofflaw, 1);//RED
//                                                    } else {
//                                                        set6ButtonColor(mLayScofflaw, btnScofflaw, 1);//RED
//                                                    }
//                                                } else if (!scofflawResponse.getDataScofflaw().get(0).getResponse().getResults().get(0).getmIsViolation()) {
//                                                    set6ButtonColor(mLayScofflaw, btnScofflaw, 0);//ORANGE
//                                                }
//                                            }
                                        } else {
                                            mLayScofflaw.visibility = View.GONE
                                            btnScofflaw.text =
                                                getString(R.string.scr_btn_no_scofflaw)
                                            btnScofflaw.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                                            btnScofflaw.setTextColor(resources.getColor(R.color.white))
                                        }
                                    }
                                } else if (scofflawResponse != null && !scofflawResponse!!.status!!&&
                                    scofflawResponse!!.dataScofflaw!![0].response!!.type == "ScofflawData"||
                                    scofflawResponse != null && !scofflawResponse!!.status!!&&
                                    scofflawResponse!!.dataScofflaw!![0].response!!.type == "ScofflawDataT2") {
                                    val message: String?
                                    if (scofflawResponse!!.response != null && scofflawResponse!!.response != "") {
                                        message = scofflawResponse!!.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, "GET_DATA_FROM_LPR-Scofflaw",
                                            message, "Ok", "Cancel", this
                                        )
                                    } else {
                                        scofflawResponse!!.response =
                                            "Not getting response from server..!!"
                                        message = scofflawResponse!!.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, "GET_DATA_FROM_LPR-Scofflaw",
                                            message, "Ok", "Cancel", this
                                        )
                                    }
                                } else {
                                    showCustomAlertDialog(
                                        this@LprDetailsActivity, "GET_DATA_FROM_LPR-Scofflaw",
                                        "Something wen't wrong..!!", "Ok", "Cancel",
                                        this
                                    )
                                    hideAPILoader()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                                LogUtil.printLogHeader("get data lpr error ",tag+" scofflawResponse")
                            }
                        }else if (tag.equals("ExemptData", ignoreCase = true)) {
                            try {
                                 exemptResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ExemptResponse::class.java)

                                if (exemptResponse != null && exemptResponse!!.status!!) {
                                    if (exemptResponse!!.dataExempt!![0].response!!.type == "ExemptData") {
                                        ExemptDataLength =
                                            exemptResponse!!.dataExempt!![0].response!!.length
                                        mListExempt =
                                            exemptResponse!!.dataExempt!![0].response!!.results
                                        if (sixActionButtonVisibilityCheck("HAS_EXEMPT") && mListExempt!!.size > 0) {
                                            mLayExempt!!.visibility = View.VISIBLE
                                            mLayExempt!!.invalidate()
                                            btnExempt!!.visibility = View.VISIBLE
                                            mLayExempt.setBackgroundResource(R.drawable.round_corner_shape_without_fill_green)
                                            setAdapterForExempt(mListExempt)
                                            btnExempt.text = getString(R.string.scr_btn_exempt)
                                            btnExempt.setBackgroundResource(R.drawable.button_round_corner_shape_green)
                                            btnExempt.setTextColor(resources.getColor(R.color.black))
                                        } else {
                                            mLayExempt.visibility = View.GONE
                                            mLayExempt!!.invalidate()
//                                            btnExempt!!.visibility = View.GONE
                                            btnExempt.text = getString(R.string.scr_btn_no_exempt)
                                            btnExempt.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                                            btnExempt.setTextColor(resources.getColor(R.color.white))
                                        }
                                    }
                                } else if (exemptResponse != null && !exemptResponse!!.status!! &&
                                    exemptResponse!!.dataExempt!![0].response!!.type == "ExemptData") {
                                    val message: String?
                                    if (exemptResponse!!.response != null && exemptResponse!!.response != "") {
                                        message = exemptResponse!!.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, getString(R.string.scr_lbl_lpr_exempt),
                                            message, getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel), this
                                        )
                                    } else {
                                        exemptResponse!!.response =
                                            getString(R.string.err_msg_something_went_wrong)
                                        message = exemptResponse!!.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, getString(R.string.scr_lbl_lpr_exempt),
                                            message, getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel), this
                                        )
                                    }
                                } else {
                                    showCustomAlertDialog(
                                        this@LprDetailsActivity,
                                        getString(R.string.scr_lbl_lpr_exempt),
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                    hideAPILoader()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                                LogUtil.printLogHeader("get data lpr error ",tag+" exemptResponse")
                            }
                        }else if (tag.equals("StolenData", ignoreCase = true)) {
                            try {

                                 stolenResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), StolenResponse::class.java)

                                if (stolenResponse != null && stolenResponse!!.status!!) {
                                    if (stolenResponse!!.dataScofflaw!![0].response!!.type == "StolenData") {
                                        StolenDataLength =
                                            stolenResponse!!.dataScofflaw!![0].response!!.length
                                        mListStolen =
                                            stolenResponse!!.dataScofflaw!![0].response!!.results
                                        if (sixActionButtonVisibilityCheck("HAS_STOLEN") && mListStolen!!.size > 0) {
                                            mLayStolen.visibility = View.VISIBLE
                                            mLayStolen.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                                            setAdapterForStolen(mListStolen)
                                            btnStolen.text = getString(R.string.scr_btn_stolen)
                                            btnStolen.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                                            btnStolen.setTextColor(resources.getColor(R.color.white))
                                        } else {
                                            mLayStolen.visibility = View.GONE
                                            btnStolen.text = getString(R.string.scr_btn_no_stolen)
                                            btnStolen.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                                            btnStolen.setTextColor(resources.getColor(R.color.white))
                                        }
                                    }
                                } else if (stolenResponse != null && !stolenResponse!!.status!!&&
                                    stolenResponse!!.dataScofflaw!![0].response!!.type == "StolenData") {
                                    val message: String?
                                    if (stolenResponse!!.response != null && stolenResponse!!.response != "") {
                                        message = stolenResponse!!.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, getString(R.string.scr_lbl_lpr_stolen),
                                            message, getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel), this
                                        )
                                    } else {
                                        stolenResponse!!.response =
                                            getString(R.string.err_msg_something_went_wrong)
                                        message = stolenResponse!!.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, getString(R.string.scr_lbl_lpr_stolen),
                                            message, getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel), this
                                        )
                                    }
                                } else {
                                    showCustomAlertDialog(
                                        this@LprDetailsActivity,
                                        getString(R.string.scr_lbl_lpr_stolen),
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                    hideAPILoader()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                                LogUtil.printLogHeader("get data lpr error ",tag+" stolenResponse")
                            }
                        }else if (tag.equals("CameraRawFeed", ignoreCase = true)) {
                            try {

                                val cameraFeedRawData: CameraRawFeedDataResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CameraRawFeedDataResponse::class.java)

                                if (cameraFeedRawData!!.data!![0]!!.response!!.type == "CameraRawFeedData") {
                                    if (cameraFeedRawData != null && cameraFeedRawData!!.status!!) {
                                        hideAPILoader()
                                        isMakeLoader = false
                                        try {
                                            if (cameraFeedRawData!!.data != null &&
                                                cameraFeedRawData!!.data!![0]!!.response!!.results != null &&
                                                cameraFeedRawData!!.data!![0]!!.response!!.results!!.size > 0
                                            ) {
                                                mLayCameraRawFeedData!!.visibility = View.VISIBLE
                                                mLayCameraRawFeedData!!.invalidate()
                                                btnCameraRawFeedData!!.visibility = View.VISIBLE
                                                mLayCameraRawFeedData.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                                                btnCameraRawFeedData.text =
                                                    getString(R.string.scr_btn_no_camera_raw_feed_data)
                                                btnCameraRawFeedData.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                                                btnCameraRawFeedData.setTextColor(
                                                    resources.getColor(
                                                        R.color.black
                                                    )
                                                )

                                                setAdapterForCameraRawFeedData(cameraFeedRawData!!.data!![0]!!.response!!.results!!)
                                            } else {
                                                mLayCameraRawFeedData!!.visibility = View.GONE
                                                mLayCameraRawFeedData!!.invalidate()
//                                                btnCameraRawFeedData!!.visibility = View.GONE
                                                mLayCameraRawFeedData.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                                                btnCameraRawFeedData.text =
                                                    getString(R.string.scr_btn_no_camera_raw_feed_data)
                                                btnCameraRawFeedData.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                                                btnCameraRawFeedData.setTextColor(
                                                    resources.getColor(
                                                        R.color.white
                                                    ))
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            LogUtil.printLogHeader("get data lpr error ",tag+" cameraFeedRawData")
                                        }

                                    } else if (modelColorResponse != null && !modelColorResponse!!.status!! &&
                                        modelColorResponse!!.dataMakeModelColor!![0].response!!.type == "CameraRawFeedData"
                                    ) {
                                        val message: String?
                                        isMakeLoader = false
                                        if (modelColorResponse!!.response != null && modelColorResponse!!.response != "") {
                                            message = modelColorResponse!!.response
                                            showCustomAlertDialog(
                                                this@LprDetailsActivity,
                                                "GET_DATA_FROM_LPR-CameraRawFeedData",
                                                message,
                                                "Ok",
                                                "Cancel",
                                                this
                                            )
                                        } else {
                                            modelColorResponse!!.response =
                                                "Not getting response from server..!!"
                                            message = modelColorResponse!!.response
                                            showCustomAlertDialog(
                                                this@LprDetailsActivity,
                                                "GET_DATA_FROM_LPR-CameraRawFeedData",
                                                message,
                                                "Ok",
                                                "Cancel",
                                                this
                                            )
                                        }
                                    }
                                } else {
                                    isMakeLoader = false
                                    hideAPILoader()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                            }
                        } else if (tag.equals(DynamicAPIPath.GET_TIMING_MARK, ignoreCase = true)) {
                            try {
                                hideAPILoader()
                                // Call meter outage API

                                 timingMarkResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TimingMarkResponse::class.java)

                                if (timingMarkResponse != null && timingMarkResponse!!.success.nullSafety()) {
                                        mainScope.launch {
//                                            val s7 = "0"
                                            if (timingMarkResponse!!.data != null) {
                                                if (timingMarkResponse!!.data!!.size > 0) {
                                                    isTiming = false
                                                    mListTiming = timingMarkResponse!!.data
                                                    try {
                                                        mLayTiming.visibility = View.VISIBLE

                                                        val mTime = timingMarkResponse!!.data!![0]
                                                        if (mTime != null) {
//                                                            mStreetItem = mTime.street
//                                                            mBlock = mTime.block
//                                                            mSideItem = mTime.side
                                                            timingId = mTime.id
                                                        }
//                                                        sharedPreference.write(
//                                                                SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME,
//                                                                AppUtils.observedTime(mTime.markStartTimestamp.nullSafety()))
//                                                        3.03-jun When issue ticket button is clicked, observed time and remarks should not carry over for timing violation.

//                                                        if(true)

//                                                  mTime!!.isViolation : genetec hit parameter
                                                        if (isTimingExpired(
                                                                        mTime.markStartTimestamp!!,
                                                                        mTime.regulationTime!!.toFloat()
                                                                )|| mTime!!.isViolation == true) {
                                                            isTiming = true
                                                            btnTimings.text = getString(R.string.scr_btn_timings)
                                                            btnTimings.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                                                            btnTimings.setTextColor(
                                                                    resources.getColor(R.color.white))
                                                            mLayTiming.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                                                        } else {
                                                            isTiming = false
                                                            //btnTimings.setText(getString(R.string.scr_btn_no_timings));
                                                            btnTimings.text = getString(R.string.scr_btn_timings)
                                                            btnTimings.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                                                            btnTimings.setTextColor(
                                                                    resources.getColor(R.color.white))
                                                            mLayTiming.setBackgroundResource(R.drawable.round_corner_shape_without_fill_orange_)
                                                        }
                                                        mState = mListTiming!!.get(0)!!.lpState
                                                        setAdapterForTiming(mListTiming)
                                                        if (timingMarkResponse!!.data!![0].vendorName.equals("Vigilant",
                                                                        ignoreCase = true)) {
                                                            if (timingMarkResponse!!.data!![0].isViolation.nullSafety()) {
                                                                if (timingMarkResponse!!.data!![0].alertType.equals(
                                                                                "Unauthorized Vehicle",
                                                                                ignoreCase = true)) {
                                                                    set6ButtonColor(mLayTiming,
                                                                            btnTimings, 1) //RED
                                                                } else if (timingMarkResponse!!.data!![0].alertType.equals(
                                                                                "Expired Parking",
                                                                                ignoreCase = true)) {
                                                                    set6ButtonColor(mLayTiming,
                                                                            btnTimings, 1) //RED
                                                                } else if (timingMarkResponse!!.data!![0].alertType.equals(
                                                                                "ScofflawAlert",
                                                                                ignoreCase = true)) {
//                                                            set6ButtonColor(mLayTiming, btnTimings, 1);//RED
                                                                    set6ButtonColor(mLayScofflaw,
                                                                            btnScofflaw, 1) //RED
                                                                } else if (timingMarkResponse!!.data!![0].alertType.equals(
                                                                                "Expired Parking;ScofflawAlert",
                                                                                ignoreCase = true)
                                                                ) {
                                                                    set6ButtonColor(mLayTiming,
                                                                            btnTimings, 1) //RED
                                                                    set6ButtonColor(mLayScofflaw,
                                                                            btnScofflaw, 1) //RED
                                                                } else if (timingMarkResponse!!.data!![0].alertType.equals(
                                                                                "Unauthorized Vehicle;ScofflawAlert",
                                                                                ignoreCase = true)){
                                                                    set6ButtonColor(mLayTiming,
                                                                            btnTimings, 1) //RED
                                                                    set6ButtonColor(mLayScofflaw,
                                                                            btnScofflaw, 1) //RED
                                                                } else {
                                                                    set6ButtonColor(mLayTiming,
                                                                            btnTimings, 1) //RED
                                                                }
                                                            } else if (!timingMarkResponse!!.data!![0].isViolation.nullSafety()) {
                                                                set6ButtonColor(mLayTiming,
                                                                        btnTimings, 0) //ORANGE
                                                            }
                                                        }
                                                        /**
                                                         * Abandon vehicle condition
                                                         */
                                                        if(mListTiming!!.get(0).isAbandonVehicle == true)
                                                        {
                                                            val mAddTimingRequest: AddTimingRequest?= AddTimingRequest()
                                                            mAddTimingRequest!!.lprState = mListTiming!!.get(0).lpState
                                                            mAddTimingRequest!!.lprNumber = mListTiming!!.get(0).lpNumber
//                                                            mAppTextTimeName.text = getString(R.string.scr_lbl_abandoned_vehicle_details)

                                                            val mIntent = Intent(this@LprDetailsActivity, AbandonedVehicleActivity::class.java)
                                                            val myJson = ObjectMapperProvider.toJson(mAddTimingRequest)
                                                            mIntent.putExtra("timeData", myJson)
                                                            startActivity(mIntent)
                                                            finish()
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                        hideAPILoader()
                                                        LogUtil.printLogHeader("get data lpr error ",tag+" timingMarkResponse")
                                                    }
                                                }else{
                                                    isTiming = false
                                                    mLayTiming.visibility = View.GONE
                                                    btnTimings.text =
                                                        getString(R.string.scr_btn_no_timings)
                                                    btnTimings.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                                                    btnTimings.setTextColor(resources.getColor(R.color.white))
                                                }
                                            } else {
                                                isTiming = false
                                                mLayTiming.visibility = View.GONE
                                                btnTimings.text =
                                                        getString(R.string.scr_btn_no_timings)
                                                btnTimings.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                                                btnTimings.setTextColor(resources.getColor(R.color.white))
                                            }
                                        }

                                } else if (timingMarkResponse != null && !timingMarkResponse!!.success!!) {
                                    val message: String?
                                    if (timingMarkResponse!!.response != null && timingMarkResponse!!.response != "") {
                                        message = timingMarkResponse!!.response
                                        showCustomAlertDialog(
                                                this@LprDetailsActivity, "GET_TIMING_MARK",
                                                message, "Ok", "Cancel", this
                                        )
                                    } else {
                                        timingMarkResponse!!.response =
                                                "Not getting response from server..!!"
                                        message = timingMarkResponse!!.response
                                        showCustomAlertDialog(
                                                this@LprDetailsActivity, "GET_TIMING_MARK",
                                                message, "Ok", "Cancel", this
                                        )
                                    }
                                } else {
                                    showCustomAlertDialog(
                                            this@LprDetailsActivity, "GET_TIMING_MARK",
                                            "Something wen't wrong..!!", "Ok", "Cancel",
                                            this
                                    )
                                    hideAPILoader()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                                LogUtil.printLogHeader("get data lpr error ",tag+" timingMarkResponse")
                            }
                        } else if (tag.equals(DynamicAPIPath.POST_LPR_SCAN_LOGGER,
                                        ignoreCase = true)) {
                            hideAPILoader()

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LprScanLoggerResponse::class.java)

                            if (responseModel != null && responseModel.success!!) {
                                hideAPILoader()
                            } else if (responseModel != null && !responseModel.success!!) {
                                val message: String?
                                if (responseModel.response != null && responseModel.response != "") {
                                    message = responseModel.response
                                    showCustomAlertDialog(
                                            this@LprDetailsActivity, "POST_LPR_SCAN_LOGGER",
                                            message, "Ok", "Cancel", this
                                    )
                                } else {
                                    responseModel.response = "Not getting response from server..!!"
                                    message = responseModel.response
                                    showCustomAlertDialog(
                                            this@LprDetailsActivity, "POST_LPR_SCAN_LOGGER",
                                            message, "Ok", "Cancel", this
                                    )
                                }
                            } else {
                                showCustomAlertDialog(
                                        this@LprDetailsActivity, "POST_LPR_SCAN_LOGGER",
                                        "Something wen't wrong..!!", "Ok", "Cancel",
                                        this
                                )
                                hideAPILoader()
                            }
                        }
                        else if(stolenResponse != null && stolenResponse!!.status!!) {
                            if (tag.equals(DynamicAPIPath.POST_LPR_SCAN_LOGGER,
                                        ignoreCase = true)) {
                                hideAPILoader()

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LprScanLoggerResponse::class.java)

                                if (responseModel != null && responseModel.success!!) {
                                    hideAPILoader()
                                } else if (responseModel != null && !responseModel.success!!) {
                                    val message: String?
                                    if (responseModel.response != null && responseModel.response != "") {
                                        message = responseModel.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, "POST_LPR_SCAN_LOGGER",
                                            message, "Ok", "Cancel", this
                                        )
                                    } else {
                                        responseModel.response =
                                            "Not getting response from server..!!"
                                        message = responseModel.response
                                        showCustomAlertDialog(
                                            this@LprDetailsActivity, "POST_LPR_SCAN_LOGGER",
                                            message, "Ok", "Cancel", this
                                        )
                                    }
                                } else {
                                    showCustomAlertDialog(
                                        this@LprDetailsActivity, "POST_LPR_SCAN_LOGGER",
                                        "Something wen't wrong..!!", "Ok", "Cancel",
                                        this
                                    )
                                    hideAPILoader()
                                }
                            }
                        }
                        if (tag.equals(DynamicAPIPath.POST_INACTIVE_METER_BUZZER ,ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), InactiveMeterBuzzerResponse::class.java)

                            if (responseModel != null && responseModel.status===true) {
                                if(responseModel!!.data!!.inactive == true)
                                {
                                    sharedPreference.write(SharedPrefKey.IS_METER_ACTIVE, "ACTIVE")
                                    ToneGenerator(AudioManager.STREAM_MUSIC, 100)?.
                                    startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1000)
                                    val vibrator = this@LprDetailsActivity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    if (Build.VERSION.SDK_INT >= 26) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                                    } else {
                                        vibrator.vibrate(500)
                                    }
                                    btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_green)
                                    btnPayment.setTextColor(resources.getColor(R.color.black))
                                }else{
                                    sharedPreference.write(SharedPrefKey.IS_METER_ACTIVE, "DEACTIVE")
                                }
                            }else{
                                sharedPreference.write(SharedPrefKey.IS_METER_ACTIVE, "DEACTIVE")
                            }
                        }

                         if (tag.equals( DynamicAPIPath.GET_LAST_SECOND_CHECK,
                                ignoreCase = true)) {
                             hideAPILoader()

                             val responseLastSecondCheckModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LastSecondCheckResponse::class.java)

                             if (responseLastSecondCheckModel != null && responseLastSecondCheckModel!!.isStatus) {
                                 showCustomAlertDialog(
                                     mContext,
                                     getString(R.string.scr_btn_no_payment),
                                     responseLastSecondCheckModel!!.data!!.lastSecondCheck!!.message,
                                     getString(R.string.alt_lbl_OK),
                                     getString(R.string.scr_btn_cancel),
                                     this
                                 )
}
                        }
                        if (tag.equals(DynamicAPIPath.POST_CREATE_TICKET, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateTicketResponse::class.java)

                            try {
                                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                    ApiLogsClass.writeApiPayloadTex(
                                        BaseApplication.instance?.applicationContext!!,
                                        "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(
                                            responseModel
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                LogUtil.printLogHeader("get data lpr error ",tag+" POST_CREATE_TICKET")
                            }
                            if (responseModel != null && responseModel.success!!) {
                                try {
                                    getMyDatabase()?.dbDAO?.updateCitationBooklet(1, mTicketNumber!![0]!!.citationBooklet)
                                    showCustomAlertDialog(mContext,
                                        getString(R.string.scr_lbl_auto_warning_ticket_response),
                                        getString(R.string.scr_lbl_auto_warning_success),
                                        getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel),this)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    LogUtil.printLogHeader("get data lpr error ",tag)
                                }
                            } else {
                                dismissLoader()
                                showCustomAlertDialog(mContext,
                                    APIConstant.POST_CREATE_TICKET,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel),this)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        hideAPILoader()
                        LogUtil.printLogHeader("get data lpr error bottom ",tag)
                    }
                }
            Status.ERROR -> {
                hideAPILoader()
                showCustomAlertDialog(
                        this@LprDetailsActivity, "Error: Network. ",
                        apiResponse.error?.message.toString()+" "+tag, "Ok", "Cancel",
                        this)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "------------Error Response -----------------" + tag
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "ERROR : " + ObjectMapperProvider.instance.writeValueAsString(
                                apiResponse.error?.message
                            )
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "ERROR : " + ObjectMapperProvider.instance.writeValueAsString(
                                apiResponse.data
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            else -> {}
        }
    }

    private fun consumeResponseForPermitAPI(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))

            Status.SUCCESS -> {
                hideAPILoader()

                if (!apiResponse.data!!.isNull) {

                    printLog(tag, apiResponse.data.toString())
                    try {
                        if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                            ApiLogsClass.writeApiPayloadTex(
                                BaseApplication.instance?.applicationContext!!,
                                "------------Response -----------------" + tag
                            )
                            ApiLogsClass.writeApiPayloadTex(
                                BaseApplication.instance?.applicationContext!!,
                                "Response: " + ObjectMapperProvider.instance.writeValueAsString(
                                    apiResponse.data
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (tag.equals(DynamicAPIPath.GET_DATA_FROM_LPR, ignoreCase = true)) {
                            try {

                                 permitResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), PermitResponse::class.java)

                                if (permitResponse != null && permitResponse!!.status!!) {
                                    //1/21/20 10:00 AM
                                    if (permitResponse!!.dataPermit!![0].response!!.type == "PermitData" ||
                                        permitResponse!!.dataPermit!![0].response!!.type == "PermitDataT2" ||
                                        permitResponse!!.dataPermit!![0].response!!.type == "PermitDataV2" ||
                                        permitResponse!!.dataPermit!![0].response!!.type == "PermitDataByZone"
                                    ) {
                                        PermitDataLength =
                                            permitResponse!!.dataPermit!![0].response!!.length
                                        mListPermit =
                                            permitResponse!!.dataPermit!![0].response!!.results


                                        if (sixActionButtonVisibilityCheck("HAS_PERMIT") && mListPermit!!.size > 0) {
                                            mLayPermit.visibility = View.VISIBLE
                                            mLayPermit.setBackgroundResource(R.drawable.round_corner_shape_without_fill_green)
                                            setAdapterForPermit(mListPermit)
                                            val mPermit =
                                                permitResponse!!.dataPermit!![0].response!!.results!![0]
                                            btnPermit.text = getString(R.string.scr_btn_permit)
                                            btnPermit.setBackgroundResource(R.drawable.button_round_corner_shape_green)
                                            btnPermit.setTextColor(resources.getColor(R.color.black))
                                            //TODO Add time compare and comment this code for all site
                                        } else {
                                            mLayPermit.visibility = View.GONE
                                            btnPermit.text = getString(R.string.scr_btn_no_permit)

                                        }
                                        //this is for genetic hit isViolation is ture then mark button red
                                        if (mListPermit!!.size > 0 && mListPermit!!.get(0).isViolation == true) {
                                            mLayPermit.visibility = View.VISIBLE
                                            mLayPermit.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                                            btnPermit.text = getString(R.string.scr_btn_permit)
                                            btnPermit.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                                            btnPermit.setTextColor(resources.getColor(R.color.black))
                                        }

                                    } else if (permitResponse!!.dataPermit!![0].response!!.type == "PermitDataFuzzy") {

                                         permitFuzzyResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), PermitResponse::class.java)

                                        val mListPermitFuzzy =
                                            permitFuzzyResponse!!.dataPermit!![0].response!!.results
                                        PermitDataFuzzyLength =
                                            permitFuzzyResponse!!.dataPermit!![0].response!!.length
                                        for (index in mListPermitFuzzy!!.indices) {
                                            fuzzyPlates.append(mListPermitFuzzy!!.get(index).lpNumber.toString() + ", ")
                                        }
                                        if (mListPermitFuzzy!!.size > 0) {
                                            if (mListPermit != null && mListPermit!!.size == 0) {
                                                btnPermit.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                                                btnPermit.setTextColor(resources.getColor(R.color.black))
                                                appCompatTextViewPaymentFuzzyPlate.visibility =
                                                    View.VISIBLE

                                            if (mListPermitFuzzy!!.size > 1) {
                                                appCompatTextViewPaymentFuzzyPlate.post {
                                                    btnPermit.text =
                                                        getString(R.string.scr_btn_permit)
                                                    appCompatTextViewPaymentFuzzyPlate.text =
                                                        (getString(R.string.scr_lbl_permit_for_similar_plates) +
                                                                fuzzyPlates)
                                                }
                                            } else {
                                                appCompatTextViewPaymentFuzzyPlate.post {
                                                    btnPermit.text =
                                                        getString(R.string.scr_btn_permit)
                                                    appCompatTextViewPaymentFuzzyPlate.text =
                                                        (getString(R.string.scr_lbl_permit_for_similar_plate) +
                                                                fuzzyPlates)
                                                }
                                            }
                                        }
                                        } else {
                                        }
                                    } else if (permitResponse != null && !permitResponse!!.status!! &&
                                        permitResponse!!.dataPermit!![0].response!!.type == "PermitData" ||
                                        permitResponse != null && !permitResponse!!.status!! &&
                                        permitResponse!!.dataPermit!![0].response!!.type == "PermitDataT2"||
                                        permitResponse != null && !permitResponse!!.status!! &&
                                        permitResponse!!.dataPermit!![0].response!!.type == "PermitDataV2" ||
                                        permitResponse != null && !permitResponse!!.status!! &&
                                        permitResponse!!.dataPermit!![0].response!!.type == "PermitDataByZone" ||
                                        permitResponse != null && !permitResponse!!.status!! &&
                                        permitResponse!!.dataPermit!![0].response!!.type == "PermitDataFuzzy"
                                    ) {
                                        val message: String?
                                        if (permitResponse!!.response != null && permitResponse!!.response != "") {
                                            message = permitResponse!!.response
                                            showCustomAlertDialog(
                                                this@LprDetailsActivity,
                                                getString(R.string.scr_lbl_lpr_permit),
                                                message,
                                                getString(R.string.alt_lbl_OK),
                                                getString(R.string.scr_btn_cancel),
                                                this
                                            )
                                        } else {
                                            permitResponse!!.response =
                                                getString(R.string.err_msg_something_went_wrong)
                                            message = permitResponse!!.response
                                            showCustomAlertDialog(
                                                this@LprDetailsActivity,
                                                getString(R.string.scr_lbl_lpr_permit),
                                                message,
                                                getString(R.string.alt_lbl_OK),
                                                getString(R.string.scr_btn_cancel),
                                                this
                                            )
                                        }
                                    } else {
//                                        showCustomAlertDialog(
//                                            this@LprDetailsActivity,
//                                            getString(R.string.scr_lbl_lpr_permit),
//                                            getString(R.string.err_msg_something_went_wrong),
//                                            getString(R.string.alt_lbl_OK),
//                                            getString(R.string.scr_btn_cancel),
//                                            this
//                                        )
                                        hideAPILoader()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hideAPILoader()
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        hideAPILoader()
                    }
                } else {
                    retryPermitAPICall(
                        apiResponse.lprNumber.nullSafety(),
                        apiResponse.type.nullSafety()
                    )
                }
            }

            Status.ERROR -> {
                hideAPILoader()
                retryPermitAPICall(
                    apiResponse.lprNumber.nullSafety(),
                    apiResponse.type.nullSafety()
                )
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "------------Error Response -----------------" + tag
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "ERROR : " + ObjectMapperProvider.instance.writeValueAsString(
                                apiResponse.error?.message
                            )
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "ERROR : " + ObjectMapperProvider.instance.writeValueAsString(
                                apiResponse.data
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            else -> {
                hideAPILoader()
            }
        }
    }

    private fun hideAPILoader() {
        // Use
        Timer().schedule(1000){
            dismissLoader()
        }
    }

    /**
     * This function is used to retry permit API once before showing dialog to user
     */
    private fun retryPermitAPICall(lprNumber: String, mType: String) {
        if (permitApiFailureCallCount <= maxPermitApiRetryCallCount) {
            permitApiFailureCallCount++
            callGetDataFromLprApiForPermit(lprNumber, mType)
        } else {
            permitApiFailureCallCount = 1
            showCustomAlertDialogOnce(
                this@LprDetailsActivity,
                getString(R.string.dialog_title_permit_api_failed),
                getString(R.string.dialog_desc_permit_api_failed),
                "Ok",
                "Cancel",
                object : CustomDialogHelper {
                    override fun onYesButtonClick() {
                        //Nothing to implement
                    }

                    override fun onNoButtonClick() {
                        //Nothing to implement
                    }

                    override fun onYesButtonClickParam(msg: String?) {
                        //Nothing to implement
                    }

                }
            )
        }
    }


    //request camera and storage permission
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.requestCameraAndStoragePermission(this@LprDetailsActivity)) {
                if(mCamera2Setting.equals("YES",ignoreCase = true))
                {
                    camera2Intent()
                }else {
                    cameraIntent()
                }
            }
        } else {
            if(mCamera2Setting.equals("YES",ignoreCase = true))
            {
                camera2Intent()
            }else {
                cameraIntent()
            }
        }
    }

    private fun camera2Intent() {
        mImageCount = mDb?.dbDAO?.getCountImages().nullSafety()
        val mIMageCount = maxImageCount("MAX_IMAGES")
        if (mImageCount < mIMageCount) {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, REQUEST_CAMERA2)
        } else {
            printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.msg_max_image).replace("#", mIMageCount.toString())
            )
        }
    }

    private fun cameraIntent() {
        mImageCount = mDb?.dbDAO?.getCountImages().nullSafety()
        val mIMageCount = maxImageCount("MAX_IMAGES")
        // mImageCount = mList.size();
        if (mImageCount < mIMageCount - 1) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            picUri = getOutputPhotoFile() //Uri.fromFile(getOutputPhotoFile());
            //tempUri=picUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            //intent.putExtra("URI", picUri);
            startActivityForResult(intent, REQUEST_CAMERA)
            //.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
//            String errorMsg = (getString(R.string.msg_max_image)).replace("#", mIMageCount + "");
            printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.msg_max_image).replace("#", mIMageCount.toString() + "")
            )
        }
    }

    private fun getOutputPhotoFile(): Uri? {
        val directory = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA)
        tempUri = directory.path
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                printLog("getOutputPhotoFile", "Failed to create storage directory.")
                return null
            }
        }
        val path: Uri
        if (Build.VERSION.SDK_INT > 23) {
            val oldPath = File(directory.path + File.separator + "IMG_temp.jpg")
            var fileUrl = oldPath.path
            if (fileUrl.substring(0, 7).matches(Regex("file://"))) {
                fileUrl = fileUrl.substring(7)
            }
            val file = File(fileUrl)
            path = FileProvider.getUriForFile(mContext!!, this.packageName + ".provider", file)
        } else {
            path = Uri.fromFile(File(directory.path + File.separator + "IMG_temp.jpg"))
        }
        return path
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        if(tempUri==null)
                        {
                            getOutputPhotoFile()
                        }
                        val file = File("$tempUri/IMG_temp.jpg")
                        var mImgaeBitmap: Bitmap? = null
                        try {
                            /* mImgaeBitmap = new Compressor(this)
                                //.setMaxWidth(640)
                                //.setMaxHeight(480)
                                .setQuality(10)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .compressToBitmap(file);*/
                            //passing bitmap for converting it to base64
                            //mImageViewNumberPlate.setImageBitmap(mImgaeBitmap);


//                        mImgaeBitmap =  new Compressor(this).compressToBitmap(file);
                            val options = BitmapFactory.Options()
                            options.inSampleSize = 4;
                            options.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(file.absolutePath, options)

                            // Calculate inSampleSize
                            options.inSampleSize = calculateInSampleSize(options, 300, 300)

                            // Decode bitmap with inSampleSize set
                            options.inJustDecodeBounds = false
                            // /storage/emulated/0/ParkLoyalty/CameraImages/IMG_temp.jpg
                            // /external_files/ParkLoyalty/CameraImages/IMG_temp.jpg
                            // content://com.fiveexceptions.lpr.scan.provider/external_files/ParkLoyalty/CameraImages/IMG_temp.jpg
                            val scaledBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

                            //check the rotation of the image and display it properly
                            val exif: ExifInterface
                            exif = ExifInterface(file.absolutePath)
                            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                            val matrix = Matrix()
                            if (orientation == 6) {
                                matrix.postRotate(90f)
                            } else if (orientation == 3) {
                                matrix.postRotate(180f)
                            } else if (orientation == 8) {
                                matrix.postRotate(270f)
                            }
                            mImgaeBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                                    scaledBitmap.width, scaledBitmap.height, matrix, true)


                            //passing bitmap for converting it to base64
                            //mImageViewNumberPlate.setImageBitmap(mImgaeBitmap);
                            if(mTimeStampOnImage.equals("YES",ignoreCase = true)) {
                                val timeStampBitmap = AppUtils.timestampItAndSave(mImgaeBitmap);
                                SaveImageMM(timeStampBitmap)
                            }else{
                                SaveImageMM(mImgaeBitmap)
                            }
//
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
//                    CropImage.activity(picUri).start(this);
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //TODO will add comments later
    private fun SaveImageMM(finalBitmap: Bitmap?) {
        if (finalBitmap == null || !isActivityInForeground) {
            LogUtil.printLog("SaveImageMM", "Bitmap is null. Skipping save.")
            LogUtil.printToastMSG(this@LprDetailsActivity,getString(R.string.wrn_lbl_capture_image))
            return
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            LogUtil.printLog("SaveImageMM", "External storage not mounted.")
            return
        }

        val myDir = File(Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA)
        if (!myDir.exists()) myDir.mkdirs()

        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname = "Image_${timeStamp}_capture.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)

            val compressQuality = 40
            val isSuccess = finalBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)
            out.flush()
            out.close()

            if (!isSuccess || file.length() == 0L) {
                LogUtil.printLog("SaveImageMM", "Compression failed or file is empty.")
                file.delete()
                return
            }

            // Delete temp image if exists
            val oldFile = File(myDir, "IMG_temp.jpg")
            if (oldFile.exists()) oldFile.delete()

            // Save path to DB
            val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
            val mImage = CitationImagesModel().apply {
                citationImage = file.path
                this.id = id.toInt()
            }
            getMyDatabase()?.dbDAO?.insertCitationImage(mImage)
            LogUtil.printLog("SaveImageMM", "Saved image: ${file.path}, size: ${file.length()} bytes")

        } catch (e: Exception) {
            LogUtil.printLog("SaveImageMM", "Exception: ${e.message}")
            LogUtil.printToastMSG(this@LprDetailsActivity,getString(R.string.wrn_lbl_capture_image))
            e.printStackTrace()
        }
    }

    private fun callAllTypeApi() {
        scope.launch() {

            appCompatTextViewPaymentFuzzyPlate.post {
            fuzzyPlates.setLength(0)
            appCompatTextViewPaymentFuzzyPlate.text = ""
            }
//            scope.launch() {
                btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                btnPayment.setTextColor(resources.getColor(R.color.white))

                btnPermit.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                btnPermit.setTextColor(resources.getColor(R.color.white))
//}
            lpNumberForLoggerAPI = TextViewLprNumber.editableText.toString().trim()
            withContext(Dispatchers.Default) { callGetTimingApi(TextViewLprNumber.editableText.toString().trim()) }

            withContext(Dispatchers.Default) {
                callGetDataFromLprApiForExempt(
                    TextViewLprNumber.editableText.toString().trim(), "ExemptData"
                )
            }
            withContext(Dispatchers.Default) {
                callGetDataFromLprApiForStolen(
                    TextViewLprNumber.editableText.toString().trim(), "StolenData"
                )
            }
            if(mT2LPRSCAN.equals("YES")) {
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForScofflaw(
                        TextViewLprNumber.editableText.toString().trim(), "ScofflawDataT2"
                    )
                }

                withContext(Dispatchers.Default) {
                    callGetDataFromLprApi(
                        TextViewLprNumber.editableText.toString().trim(), "CitationDataT2"
                    )
                }

            }else{
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForScofflaw(
                        TextViewLprNumber.editableText.toString().trim(), "ScofflawData"
                    )
                }

                withContext(Dispatchers.Default) {
                    callGetDataFromLprApi(
                        TextViewLprNumber.editableText.toString().trim(), "CitationData"
                    )
                }
            }
            withContext(Dispatchers.Default) {
            isMakeLoader = true
                callGetDataFromLprApi(TextViewLprNumber.editableText.toString().trim(), "MakeModelColorData") }
//            withContext(Dispatchers.Default) { callGetDataFromLprApi(TextViewLprNumber.editableText.toString().trim(), "AbandonVehicles") }
            /**
             * PAYMENTS_BY_ZONE Yes in setting file then call  PermitDataByZone ohterwise call permitData
             */
            if (linearLayoutCompatZone.visibility == View.VISIBLE && mLotItem!=null && mLotItem!!.isNotEmpty() &&
                mAutoComTextViewLot!=null) {
                if(mV2LPRSCAN.equals("YES")) {
                    withContext(Dispatchers.Default) {
//                    callGetDataFromLprApi(
//                    TextViewLprNumber.editableText.toString().trim(), "PermitDataByZone")

                        callGetDataFromLprApiForPermit(
                            TextViewLprNumber.editableText.toString().trim(), "PermitDataV2"
                        )

                    }
                }else{
                    withContext(Dispatchers.Default) {
                        callGetDataFromLprApiForPermit(
                            TextViewLprNumber.editableText.toString().trim(), "PermitDataByZone"
                        )

                    }
                }
            }else{
                if(mT2LPRSCAN.equals("YES")) {
                    withContext(Dispatchers.Default) {
//                        callGetDataFromLprApi(
//                            TextViewLprNumber.editableText.toString().trim(), "PermitDataT2"
//                        )

                        callGetDataFromLprApiForPermit(
                            TextViewLprNumber.editableText.toString().trim(), "PermitDataT2"
                        )
                    }
                }else {
                    withContext(Dispatchers.Default) {
//                        callGetDataFromLprApi(
//                            TextViewLprNumber.editableText.toString().trim(), "PermitData"
//                        )

                        callGetDataFromLprApiForPermit(
                            TextViewLprNumber.editableText.toString().trim(), "PermitData"
                        )
                    }
                }
            }


//            payment data
            if(mPaymentByZone.equals("YES",ignoreCase = true)&& mLotItem!=null && mLotItem!!.isNotEmpty() &&
                mAutoComTextViewLot!=null && mAutoComTextViewLot.text!!.isNotEmpty())
            {
                withContext(Dispatchers.Default) {callGetDataFromLprApiForPayment(
                    TextViewLprNumber.editableText.toString().trim(), "PaymentDataByZone")}
            }else if(mAllPaymentsInZoneFuzzy.equals("YES",ignoreCase = true))
            {
                withContext(Dispatchers.Default) {callGetDataFromLprApiForPayment(
                    TextViewLprNumber.editableText.toString().trim(), "AllPaymentsInZoneFuzzy")}
            }else{
                withContext(Dispatchers.Default){ callGetDataFromLprApiForPayment(TextViewLprNumber.editableText.toString().trim(), "PaymentData") }
            }



            if(mPaymentDataFuzzy.equals("YES",ignoreCase = true)) {
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForPayment(
                        TextViewLprNumber.editableText.toString().trim(), "PaymentDataFuzzy"
                    )
                }
                withContext(Dispatchers.Default) {
//                    callGetDataFromLprApi(
//                        TextViewLprNumber.editableText.toString().trim(), "PermitDataFuzzy"
//                    )

                    callGetDataFromLprApiForPermit(
                        TextViewLprNumber.editableText.toString().trim(), "PermitDataFuzzy"
                    )
                }
            }


            withContext(Dispatchers.Default) {
                if (isCameraRawFeedAPICallingFlag)
                    callGetDataFromLprForCameraRawFeedApi(
                        TextViewLprNumber.editableText.toString().trim(), "CameraRawFeedData"
                    )
            }
            if(sharedPreference.read(SharedPrefKey.IS_METER_ACTIVE, "").equals("ACTIVE",ignoreCase = true)) {
                try {
                    if (getMeterBuzzerAPICallStatusFormSettingFile()) {
                        val inactiveMeterBuzzerRequest = InactiveMeterBuzzerRequest()
                        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
                        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
                        inactiveMeterBuzzerRequest.latitude = mLat.toString()
                        inactiveMeterBuzzerRequest.longitude = mLong.toString()
                        mInactiveMeterBuzzerViewModelLpr!!.hitInactiveMeterBuzzerLprApi(
                            inactiveMeterBuzzerRequest
                        )
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            delay((eventLoggerAPIDelay * 1000).toLong())
            mPayment = true
            enableScanButton()
        }

    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun MoveToCitationFormWithScofflawData(whichButtonClick : Int) {
        if((mListScofflaw != null && mListScofflaw!!.size > 0)){
            val sanctionsType = whichButtonClick?.let {
                if (it > 0) {
                    when (it) {
                        1 -> "White Sticker"
                        2 -> "Red Sticker"
                        else -> ""
                    }
                } else ""
            } ?: ""
            val inTime = mListScofflaw!![0]?.receivedTimestamp.let {
                sanctionsType+" Time " + AppUtils.formatDateTimeForSanctionType(it.toString())
            } ?: ""

            val mIntent = Intent(this, LprDetails2Activity::class.java)

            if (vehicleStickerInfo != null && mSelectedMakeFromVehicleSticker.isNotBlank()) {
                mIntent.putExtra("make", mSelectedMakeFromVehicleSticker)
            } else {
                mIntent.putExtra("make", mAutoComTextViewMakeVeh.editableText.toString().trim())
            }

            mIntent.putExtra("from_scr", mFromScreen)
            if (mSelectedModel != null) {
//                    mIntent.putExtra("model", mAutoComTextViewVehModel.editableText.toString().trim())
                mIntent.putExtra("model", mSelectedModel)
            }
            if (mSelectedVin != null) {
                mIntent.putExtra("vinNumber", mSelectedVin)
            }
            mIntent.putExtra("Street", mStreetItem)
            mIntent.putExtra("timing_record_value_camera", inTime)
            mIntent.putExtra("SideItem", if(mSideItem!=null && !mSideItem!!.isEmpty())mSideItem else mDirectionItem)
            mIntent.putExtra("Block", mBlock)
            mIntent.putExtra("Direction", mDirectionItem)
            mIntent.putExtra("BodyStyle", mBodyStyleItem)

            mIntent.putExtra("color", mAutoComTextViewVehColor.editableText.toString().trim())
            mIntent.putExtra("lpr_number", TextViewLprNumber.text.toString().trim())
            mIntent.putExtra("State", mState)
            mIntent.putExtra("violation_code", mViolationCode)
            mIntent.putExtra("SanctionButtonClick", whichButtonClick)
            mIntent.putExtra("CitationCount", if((citationHistoryCount+1)<4)(citationHistoryCount+1).toString() else "3")

            //Extra intent for Vehicle Sticker Info
            if (vehicleStickerInfo != null) {
                mIntent.putExtra(INTENT_KEY_VEHICLE_INFO, vehicleStickerInfo)
            }


            startActivity(mIntent)
            finish()
        }
    }

//    private fun setHeightOfHistoryRecycleView() {
//        if(mListCitation!=null && mListCitation!!.size>0) {
//            val vto: ViewTreeObserver = mRecylerViewHistory.getViewTreeObserver()
//            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    try {
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                            mRecylerViewHistory.getViewTreeObserver()
//                                .removeGlobalOnLayoutListener(this)
//                        } else {
//                            mRecylerViewHistory.getViewTreeObserver()
//                                .removeOnGlobalLayoutListener(this)
//                        }
//                        if (BuildConfig.FLAVOR.equals(
//                                Constants.FLAVOR_TYPE_DUNCAN,
//                                ignoreCase = true
//                            )||BuildConfig.FLAVOR.equals(
//                                Constants.FLAVOR_TYPE_MEMORIALHERMAN,
//                                ignoreCase = true
//                            )||BuildConfig.FLAVOR.equals(DuncanBrandingApp13()
//                            )
//                        ) {
//                            val display = windowManager.defaultDisplay
//                            val size = Point()
//                            display.getSize(size)
//                            val height = size.y
//
//                            heightHistory = if (mListCitation!!.size == 1) (height * 0.57).toInt() else (height * 0.5).toInt()
////                            heightHistory = mRecylerViewHistory.getMeasuredHeight() + 300
//
//                            mLayHistory.layoutParams.height =
//                                (heightHistory * (mListCitation!!.size))
////                            mLayHistory.layoutParams.height =
////                                (heightHistory * (mListCitation!!.size))
//                            mLayHistory.invalidate()
//                            mRecylerViewHistory.invalidate()
//                            btnIssueHistory.invalidate()
//                            if (mHistoryAdapter != null) {
//                                mHistoryAdapter!!.notifyDataSetChanged()
//                            }
//                        } else {
//                            if (heightHistory == 0) {
//                                heightHistory = mRecylerViewHistory.getMeasuredHeight()
//                            }
//                            mLayHistory.layoutParams.height =
//                                (heightHistory * (mListCitation!!.size + 1))
//                            mLayHistory.invalidate()
//                            mRecylerViewHistory.invalidate()
//                            mHistoryAdapter!!.notifyDataSetChanged()
//
//
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            })
//        }
//    }

    private fun enableScanButton(){
        btnCheck.post {
            btnCheck!!.isEnabled = true
            val alpha = 1.00f
            val alphaUp = AlphaAnimation(alpha, alpha)
            alphaUp.fillAfter = true
            btnCheck!!.startAnimation(alphaUp)
        }
    }

    private fun updateLockButtonIcon() {
        try {
            if (appCompatImageViewLock.tag != null && appCompatImageViewLock.tag == "lock") {
                appCompatImageViewLock.setImageDrawable(
                        ContextCompat.getDrawable(
                                this@LprDetailsActivity!!,
                                R.drawable.ic_baseline_lock_open_24
                        )
                )
                appCompatImageViewLock.tag = "unlock"
                sharedPreference.write(SharedPrefKey.LOCK_GEO_ADDRESS, "unlock")
                addressGeo = ""
                sharedPreference.write(
                        SharedPrefKey.LOCK_GEO_SAVE_ADDRESS,
                        addressGeo
                )
                getGeoAddress()

                //Set ADA
                appCompatImageViewLock.contentDescription = getString(R.string.ada_content_description_location_unlocked)
                AccessibilityUtil.announceForAccessibility(appCompatImageViewLock,getString(R.string.ada_content_description_location_unlocked))
            } else {
                appCompatImageViewLock.setImageDrawable(
                        ContextCompat.getDrawable(
                                this@LprDetailsActivity!!,
                                R.drawable.ic_baseline_lock_24
                        )
                )
                appCompatImageViewLock.tag = "lock"
                sharedPreference.write(SharedPrefKey.LOCK_GEO_ADDRESS, "lock")
                sharedPreference.write(
                        SharedPrefKey.LOCK_GEO_SAVE_ADDRESS,
                        addressGeo
                )

                //Set ADA
                appCompatImageViewLock.contentDescription = getString(R.string.ada_content_description_location_locked)
                AccessibilityUtil.announceForAccessibility(appCompatImageViewLock,getString(R.string.ada_content_description_location_locked))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun moveToNext(violationCode: String) {

        try {//        if (!TextUtils.isEmpty(TextViewLprNumber.getText().toString().trim())) {
            if (true) {
                /**
                 * time for comment
                 */
                if (!violationCode.equals("time", true)) {
                    sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "false")
                }
                sharedPreference.write(SharedPrefKey.VOIDANDREISSUEBYPLATE, false)
                TextViewLprNumber.clearFocus()
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true) &&
                    appCompatImageViewLock.getTag().equals("lock") && separated!=null)
                {
                    try {
                        mBlock = separated?.get(0).toString().split("#")[0]
                        if(separated?.size!! >1) {
                            mStreetItem = separated?.get(0).toString().split("#")[1]
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mBlock = ""
                        mStreetItem = ""
                    }
                }


                val mIntent = Intent(this, LprDetails2Activity::class.java)
//                val mIntent = Intent(this, CitationHistoryPrinterActivity::class.java)
                if (vehicleStickerInfo != null && mSelectedMakeFromVehicleSticker.isNotBlank()) {
                    mIntent.putExtra("make", mSelectedMakeFromVehicleSticker)
                } else {
                    mIntent.putExtra("make", mAutoComTextViewMakeVeh.editableText.toString().trim())
                }
                mIntent.putExtra("from_scr", mFromScreen)
                if (mSelectedModel != null) {
//                    mIntent.putExtra("model", mAutoComTextViewVehModel.editableText.toString().trim())
                    mIntent.putExtra("model", mSelectedModel)
                }
                if (mSelectedVin != null) {
                    mIntent.putExtra("vinNumber", mSelectedVin)
                }
                mIntent.putExtra("color", mAutoComTextViewVehColor.editableText.toString().trim())
                mIntent.putExtra("lpr_number", TextViewLprNumber.text.toString().trim())
                mIntent.putExtra("violation_code", violationCode)
                mIntent.putExtra("violation_code_camera", mViolationCode)
                mIntent.putExtra("address", mTvReverseCoded.text.toString().trim())
                mIntent.putExtra("timing_record_value", mTimingRecordRemarkValue)
                mIntent.putExtra("timing_record_value_camera", mTimingRecordRemarkValueCameraRawFeed)
                mIntent.putExtra("timing_tire_stem_value", mTimingTireStem)
                mIntent.putExtra("Lot", mLotItem)
                mIntent.putExtra("Location", mLotItem)
                mIntent.putExtra("Space_id", mSpaceId)
                mIntent.putExtra("Meter", mMeterNameItem)
                mIntent.putExtra("printerQuery", mPrintQuery)
                mIntent.putExtra("Zone", mPBCZone)
                mIntent.putExtra("Street", mStreetItem)
                mIntent.putExtra("SideItem", if(mSideItem!=null && !mSideItem!!.isEmpty())mSideItem else mDirectionItem)
                mIntent.putExtra("Block", mBlock)
                mIntent.putExtra("Direction", mDirectionItem)
                mIntent.putExtra("BodyStyle", mBodyStyleItem)
                mIntent.putExtra("TimingID", timingId)
                mIntent.putExtra("State", mState)
                mIntent.putExtra("Vin", mVin)
                mIntent.putExtra("Note", mNoteItem)
                mIntent.putExtra("Note1", mNoteItem1)
                mIntent.putExtra("EscalatedLprNumber", mEscalatedLprNumber)
                mIntent.putExtra("EscalatedState", mEscalatedState)
                mIntent.putExtra("CitationCount", if((citationHistoryCount+1)<4)(citationHistoryCount+1).toString() else "3")
                mIntent.putExtra("ClickType", mTimingClick)
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)) {
                    mIntent.putExtra("VIOLATION", mViolation)
    //            mIntent.putExtra("VIOLATION", "OVERTIME PARKING")
//                }
//                    mTimingImages.add("")
                if(ImageCache.base64Images.size>0) {
                    mIntent.putExtra(INTENT_KEY_TIMING_IMAGES_BASE64, "YES")
                }

                if (mListCitation?.isNotEmpty().nullSafety()) {
                    mIntent.putExtra(INTENT_KEY_UNPAID_CITATION_COUNT, mListCitation?.count { it.status == API_CONSTANT_CITATION_STATUS_VALID }.nullSafety())
                }
                mIntent.putExtra("State", mState)
                mIntent.putExtra("vendor_name", mVendorName)

                //Extra intent for Vehicle Sticker Info
                mIntent.putExtra(INTENT_KEY_VEHICLE_INFO, vehicleStickerInfo)

                startActivity(mIntent)
                finish()
            } else {
                TextViewLprNumber.requestFocus()
                TextViewLprNumber.isFocusable = true
                printToastMSG(this@LprDetailsActivity, getString(R.string.err_msg_plate_is_empty))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (backpressCloseDrawer()) {
            // close activity when drawer is closed
            //super.onBackPressed();
            closeDrawer()
            // layCircularRevealRelativeLayout.setVisibility(View.GONE);

            //mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            //launchScreen(this@LprDetailsActivity, WelcomeActivity.class);
            startActivity(
                    Intent(this, WelcomeActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
            finish()
        }
    }
    private var mRegex = ""
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        try {
            if(mRegex.isEmpty()) {
                 mRegex = getRegexFromSetting("LICENSE_PLATE_FORMAT", settingsList!!)
            }
            //^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z0-9]{4,12}$
//        Pattern p = Pattern.compile("[0-9]{1}[A-Z]{3}[0-9]{3}");
            val p = Pattern.compile(mRegex)
            val m = p.matcher(s)
            val b = m.matches()
            //        Pattern p1 = Pattern.compile("[A-Z]{3}[0-9]{4}"); //EPC8201
            val p1 = Pattern.compile(mRegex) //EPC8201
            val m1 = p1.matcher(s)
            val b1 = m1.matches()
            if (b) {
                mImageViewRight.setBackgroundDrawable(null)
                mImageViewRight.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_right))

                //Set ADA
                mImageViewRight.contentDescription = getString(R.string.ada_content_description_license_plate_format_matched)
            } else if (b1) {
                mImageViewRight.setBackgroundDrawable(null)
                mImageViewRight.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_right))

                //Set ADA
                mImageViewRight.contentDescription = getString(R.string.ada_content_description_license_plate_format_matched)
            } else {
                mImageViewRight.setBackgroundDrawable(null)
                mImageViewRight.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_cross_lpr))

                //Set ADA
                mImageViewRight.contentDescription = getString(R.string.ada_content_description_license_plate_format_not_matched)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Call Api For Lpr scan details */
    @Throws(IOException::class, ParseException::class)
    private fun callPushEventApi(LprNum: String) {
        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
        //        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
//        List<Address> myList = myLocation.getFromLocation(mLat, mLong, 1);
        try {
//            Address address = myList.get(0);
            if (isInternetAvailable(this@LprDetailsActivity)) {
                val mPushEventRequest = LprScanLoggerRequest()
                mPushEventRequest.activityType = "LPRScan"
                mPushEventRequest.lpNumber = LprNum
                mPushEventRequest.logType = Constants.LOG_TYPE_NODE_PORT
                mPushEventRequest.latitude = mLat
                mPushEventRequest.longitude = mLong
                var mZone: String? = "CST"
                mPushEventRequest.clientTimestamp = splitDateLpr(mZone)
                mPushEventRequest.siteOfficerName =
                        mWelcomeForm!!.officerFirstName + " " + mWelcomeForm!!.officerLastName
                mPushEventRequest.supervisorName = mWelcomeForm!!.officerSupervisor
                mPushEventRequest.badgeId = mWelcomeForm!!.officerBadgeId
                mPushEventRequest.zone = mWelcomeForm!!.officerZone
                mPushEventRequest.beat = mWelcomeForm!!.officerBeatName
                mPushEventRequest.mAgency = mWelcomeForm!!.agency
                //                mPushEventRequest.setmShift(mWelcomeForm.getOfficerShift());
                mPushEventRequest.mShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                mPushEventRequest.mSiteOfficerName =
                        mWelcomeForm?.officerFirstName + " " + mWelcomeForm?.officerLastName
                mPushEventRequest.block =
                        if (separatedEventLogger != null && !separatedEventLogger.toString().isEmpty() && separatedEventLogger!!.size>1) separatedEventLogger!![0] else " "
                mPushEventRequest.street =
                        if (separatedEventLogger != null && separatedEventLogger.toString().isNotEmpty() && separatedEventLogger!!.size>=2) separatedEventLogger!![1] else addressGeo
                //new added params in phase2 lpr_scan api
                printLog("Event logger after scan page", CitationDataLength.toString())
                mPushEventRequest.scofflawData =
                        (if (ScofflawDataLength != null) if (ScofflawDataLength!! > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.citationData =
                        (if (CitationDataLength != null) if (CitationDataLength!! > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.exemptData =
                        (if (ExemptDataLength != null) if (ExemptDataLength!! > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.paymentData =
                        (if (PaymentLength != null) if (PaymentLength!! > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.paymentDataFuzzy =
                        (if (PaymentFuzzyLength != null) if (PaymentFuzzyLength!! > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.permitData =
                        (if (PermitDataLength != null) if (PermitDataLength!! > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.permitDataFuzzy =
                        (if (PermitDataFuzzyLength != null) if (PermitDataFuzzyLength!! > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.stolenData =
                        (if (StolenDataLength != null) if (StolenDataLength!! > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.makeModelColorData =
                        (if (modelColorLength != null) if (modelColorLength!! > 0) 1 else 0 else 0).toLong()
                var timingLenght: Long = 0
                try {
                    timingLenght =
                            if (timingMarkResponse!!.data != null) timingMarkResponse!!.data!!.size.toLong() else 0.toLong()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mPushEventRequest.timingData = (if (timingLenght > 0) 1 else 0).toLong()
                mLoggerViewModel!!.hitLprScanLoggerApi(mPushEventRequest)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "------------Data Count API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                mPushEventRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN,ignoreCase = true)&&
                    PermitDataLength?.toInt()==0 &&CitationDataLength?.toInt()==0) {
                    showPopupForAutoWarningTicket()
                }
            } else {
                printToastMSG(
                        this@LprDetailsActivity,
                        getString(R.string.err_msg_connection_was_refused)
                )
            }
        } catch (e: Exception) {
            Log.e("mLoggerViewModel", e.message!!)
            e.printStackTrace()
        }
    }


    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {
        if(msg!!.trim().equals("Error: Network.")) {
            launchScreen(this@LprDetailsActivity, DashboardActivity::class.java)
        } else if(msg!!.trim().equals(getString(R.string.scr_lbl_auto_warning_ticket))) {
            if (isInternetAvailable(this@LprDetailsActivity)) {
                val request = buildCreateTicketRequest()
                mCreateTicketViewModel?.hitCreateTicketApi(request)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            applicationContext,
                            "------------Scan auto Create API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            applicationContext,
                            "REQUEST: ${ObjectMapperProvider.instance.writeValueAsString(request)}"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else if(msg!!.trim().equals(getString(R.string.scr_lbl_booklet))) {
                logout(this@LprDetailsActivity)
        }
    }


    fun copyFileOrDirectory(srcDir: String?, dstDir: String?) {
        try {
            val src = File(srcDir)
            val dst = File(dstDir, src.name)
            if (src.isDirectory) {
                val files = src.list()
                val filesLength = files.size
                for (i in 0 until filesLength) {
                    val src1 = File(src, files[i]).path
                    val dst1 = dst.path
                    copyFileOrDirectory(src1, dst1)
                }
            } else {
                copyFile(src, dst)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }
//    /**
//     * copies content from source file to destination file
//     *
//     * @param sourceFile
//     * @param destFile
//     * @throws IOException
//     */
//    @Throws(IOException::class)
//    private fun copyFile(sourceFile: File, destFile: File) {
//        if (!sourceFile.exists()) {
//            return
//        }
//        var source: FileChannel? = null
//        var destination: FileChannel? = null
//        source = FileInputStream(sourceFile).getChannel()
//        destination = FileOutputStream(destFile).channel
//        if (destination != null && source != null) {
//            destination.transferFrom(source, 0, source.size())
//        }
//        if (source != null) {
//            source.close()
//        }
//        if (destination != null) {
//            destination.close()
//        }
//    }

    private fun setLayoutVisibilityBasedOnSettingResponse() {
        try {
            settingsList = ArrayList()
                settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
                    if (settingsList != null && settingsList!!.isNotEmpty()) {
                        for (i in settingsList!!.indices) {
                            LogUtil.printLog("Setting",settingsList!![i].type.toString())
                            if (settingsList!![i].type.equals("HAS_MAKE", ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                linearLayoutCompatMake.visibility = View.GONE
                            } else if (settingsList!![i].type.equals("HAS_MODEL", ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                linearLayoutCompatModel.visibility = View.GONE
                                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true))
                                {
                                    linearLayoutCompatMake.visibility = View.GONE
                                    linearLayoutCompatColor.visibility = View.GONE
                                }
                            } else if (settingsList!![i].type.equals("HAS_COLOR", ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                linearLayoutCompatColor.visibility = View.GONE
                            } else if (settingsList!![i].type.equals("HAS_PAYMENTS",
                                            ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                btnPayment.visibility = View.GONE
                            } else if (settingsList!![i].type.equals("HAS_SCOFFLAW",
                                            ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                btnScofflaw.visibility = View.GONE
                            } else if (settingsList!![i].type.equals("HAS_TIMING",
                                            ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                btnTimings.visibility = View.GONE
                            } else if (settingsList!![i].type.equals("HAS_PERMIT",
                                            ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                btnPermit.visibility = View.GONE
                            } else if (settingsList!![i].type.equals("HAS_STOLEN",
                                            ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                btnStolen.visibility = View.GONE

                            } else if (settingsList!![i].type.equals("HAS_EXEMPT",
                                            ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                btnExempt.visibility = View.GONE
                            }  else if (settingsList!![i].type.equals("HAS_CAMERA_RAW_FEED_DATA",
                                            ignoreCase = true)
                                    && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                            ) {
                                isCameraRawFeedAPICallingFlag = true
                                btnCameraRawFeedData.visibility = View.VISIBLE
                            } else if (settingsList!![i].type.equals("HISTORY", ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                linearLayoutCompatHistory.visibility = View.GONE
                            }
                            if (settingsList!![i].type.equals("TIMEZONE",
                                            ignoreCase = true )) {
                                try {
                                    mZone = settingsList!![i].mValue.toString()
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("TIMING_RECORD_LOOKUP_THRESHOLD",
                                            ignoreCase = true )) {
                                try {
                                    mHour = settingsList!![i].mValue.toString()
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("IS_TIRE_STEM_ICON",
                                            ignoreCase = true )) {
                               if(settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                   try {
                                       isTireStemWithImageView = true
                                   } catch (e: java.lang.Exception) {
                                       e.printStackTrace()
                                   }
                               }
                            }
                            if (settingsList!![i].type.equals("IS_CAMERA2_ENABLE",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                try {
                                    mCamera2Setting = "YES"
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("IS_PAYMENT_DATA_FUZZY",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES", ignoreCase = true)||
                                settingsList!![i].type.equals("IS_FUZZY_SEARCH",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                try {
                                    mPaymentDataFuzzy = "YES"
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("PAYMENTS_BY_ZONE",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                try {
                                    ioScope.launch {
                                        linearLayoutCompatZone.visibility = View.VISIBLE
                                        mPaymentByZone = "YES"
                                        setDropdownLot()
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("EXPIRED_PAYMENTS_IN_LPR",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                try {
                                    ioScope.launch {
                                        mAllPaymentsInZoneFuzzy = "YES"
//                                        if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,true)&&
//                                            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,true)) {
//                                            linearLayoutCompatZone.visibility = View.VISIBLE
//                                            setDropdownLot()
//                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("PERMITS_BY_ZONE",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                try {
                                    ioScope.launch {
                                        linearLayoutCompatZone.visibility = View.VISIBLE
                                        mPermitByZone = "YES"
                                        setDropdownLot()
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("T2_LPR_SCAN",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                try {
                                     mT2LPRSCAN = "YES"
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("V2_LPR_SCAN",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                try {
                                     mV2LPRSCAN = "YES"
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
//                            Default_Regulation_time_auto	DEFAULT_REGULATION_TIME_AUTO	8 HOURS#480
//                            Is_Auto_timing	IS_AUTO_TIMING	YES
                            if (settingsList!![i].type.equals(SETTINGS_FLAG_DEFAULT_REGULATION_TIME_AUTO,
                                            ignoreCase = true)) {
                                try {
                                    mAutoTimingRegulationTime = settingsList!![i].mValue
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals(SETTINGS_FLAG_IS_AUTO_TIMING,
                                            ignoreCase = true)) {
                                try {
                                    mAutoTiming = settingsList!![i].mValue
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (settingsList!![i].type.equals("IS_FINE_SUM_VISIBLE",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES",ignoreCase = true)) {
                                isFineSumVisible = true
                            }
                            if (settingsList!![i].type.equals("IMAGE_TIMESTAMP",
                                            ignoreCase = true) && settingsList!![i].mValue.equals("YES",ignoreCase = true)) {
                                mTimeStampOnImage = "YES"
                            }
                            if (settingsList!![i].type.equals("EVENT_LOGGER_API_DELAY",
                                            ignoreCase = true)  ) {
                                eventLoggerAPIDelay = settingsList!![i].mValue
                                    ?.toDoubleOrNull() // safely parse string to Double
                                    ?: 2.5
                            }
                        }
                    }
            updateButtonVisibilityPermitPayment(btnPermit.visibility,btnPayment.visibility)
            updateButtonVisibilityForExmptStolen(btnExempt.visibility,btnStolen.visibility)
            updateButtonVisibilityScofflawTimings(btnScofflaw.visibility,btnTimings.visibility)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setFuzzyLogicSetTextView() {
        TextViewLprNumber.post {
            try {
                appCompatTextViewFuzzy1.text = ""
                appCompatTextViewFuzzy2.text = ""
                appCompatTextViewFuzzy3.text = ""
                appCompatTextViewFuzzy4.text = ""
                appCompatTextViewFuzzy5.text = ""
                appCompatTextViewFuzzy6.text = ""
                appCompatTextViewFuzzy7.text = ""
                appCompatTextViewFuzzy8.text = ""
                appCompatTextViewFuzzy9.text = ""
                appCompatTextViewFuzzy1.visibility = View.GONE
                appCompatTextViewFuzzy2.visibility = View.GONE
                appCompatTextViewFuzzy3.visibility = View.GONE
                appCompatTextViewFuzzy4.visibility = View.GONE
                appCompatTextViewFuzzy5.visibility = View.GONE
                appCompatTextViewFuzzy6.visibility = View.GONE
                appCompatTextViewFuzzy7.visibility = View.GONE
                appCompatTextViewFuzzy8.visibility = View.GONE
                appCompatTextViewFuzzy9.visibility = View.GONE
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 0
                ) {
                    appCompatTextViewFuzzy1.visibility = View.VISIBLE
                    appCompatTextViewFuzzy1.text =
                        TextViewLprNumber.text!![0].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy1.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy1.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy1.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy1.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy1.tag = "GREEN"
                    }
                }
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 1
                ) {
                    appCompatTextViewFuzzy2.visibility = View.VISIBLE
                    appCompatTextViewFuzzy2.text =
                        TextViewLprNumber.text!![1].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy2.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy2.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy2.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy2.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy2.tag = "GREEN"
                    }
                }
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 2
                ) {
                    appCompatTextViewFuzzy3.visibility = View.VISIBLE
                    appCompatTextViewFuzzy3.text =
                        TextViewLprNumber.text!![2].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy3.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy3.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy3.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy3.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy3.tag = "GREEN"
                    }
                }
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 3
                ) {
                    appCompatTextViewFuzzy4.visibility = View.VISIBLE
                    appCompatTextViewFuzzy4.text =
                        TextViewLprNumber.text!![3].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy4.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy4.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy4.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy4.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy4.tag = "GREEN"
                    }
                }
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 4
                ) {
                    appCompatTextViewFuzzy5.visibility = View.VISIBLE
                    appCompatTextViewFuzzy5.text =
                        TextViewLprNumber.text!![4].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy5.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy5.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy5.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy5.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy5.tag = "GREEN"
                    }
                }
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 5
                ) {
                    appCompatTextViewFuzzy6.visibility = View.VISIBLE
                    appCompatTextViewFuzzy6.text =
                        TextViewLprNumber.text!![5].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy6.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy6.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy6.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy6.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy6.tag = "GREEN"
                    }
                }
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 6
                ) {
                    appCompatTextViewFuzzy7.visibility = View.VISIBLE
                    appCompatTextViewFuzzy7.text =
                        TextViewLprNumber.text!![6].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy7.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy7.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy7.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy7.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy7.tag = "GREEN"
                    }
                }
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 7
                ) {
                    appCompatTextViewFuzzy8.visibility = View.VISIBLE
                    appCompatTextViewFuzzy8.text =
                        TextViewLprNumber.text!![7].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy8.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy8.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy8.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy8.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy8.tag = "GREEN"
                    }
                }
                if (!TextViewLprNumber.text.toString()
                        .isEmpty() && TextViewLprNumber.text!!.length > 8
                ) {
                    appCompatTextViewFuzzy9.visibility = View.VISIBLE
                    appCompatTextViewFuzzy9.text =
                        TextViewLprNumber.text!![8].toString() + ""
                    if (Arrays.asList(*fuzzyStringList).contains(
                            appCompatTextViewFuzzy9.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy9.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                        appCompatTextViewFuzzy9.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy9.background =
                            getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                        appCompatTextViewFuzzy9.tag = "GREEN"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setFuzzyLogicTextViewClick() {
        try {
            val automaticFuzzyClass = AutomaticFuzzyClass()
            appCompatTextViewFuzzy1.setOnClickListener {
                if (appCompatTextViewFuzzy1.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy1.text.toString(), 1
                    )
                    appCompatTextViewFuzzy1.text = result
                    updateLprCharacterIndexValue(1)
                }
            }
            appCompatTextViewFuzzy2.setOnClickListener { //                automaticFuzzyClass.resetIndex();
                if (appCompatTextViewFuzzy2.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy2.text.toString(), 2
                    )
                    appCompatTextViewFuzzy2.text = result
                    updateLprCharacterIndexValue(2)
                }
            }
            appCompatTextViewFuzzy3.setOnClickListener {
                if (appCompatTextViewFuzzy3.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy3.text.toString(), 3
                    )
                    appCompatTextViewFuzzy3.text = result
                    updateLprCharacterIndexValue(3)
                }
            }
            appCompatTextViewFuzzy4.setOnClickListener {
                if (appCompatTextViewFuzzy4.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy4.text.toString(), 4
                    )
                    appCompatTextViewFuzzy4.text = result
                    updateLprCharacterIndexValue(4)
                }
            }
            appCompatTextViewFuzzy5.setOnClickListener {
                if (appCompatTextViewFuzzy5.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy5.text.toString(), 5
                    )
                    appCompatTextViewFuzzy5.text = result
                    updateLprCharacterIndexValue(5)
                }
            }
            appCompatTextViewFuzzy6.setOnClickListener {
                if (appCompatTextViewFuzzy6.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy6.text.toString(), 6
                    )
                    appCompatTextViewFuzzy6.text = result
                    updateLprCharacterIndexValue(6)
                }
            }
            appCompatTextViewFuzzy7.setOnClickListener {
                if (appCompatTextViewFuzzy7.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy7.text.toString(), 7
                    )
                    appCompatTextViewFuzzy7.text = result
                    updateLprCharacterIndexValue(7)
                }
            }
            appCompatTextViewFuzzy8.setOnClickListener {
                if (appCompatTextViewFuzzy8.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy8.text.toString(), 8
                    )
                    appCompatTextViewFuzzy8.text = result
                    updateLprCharacterIndexValue(8)
                }
            }
            appCompatTextViewFuzzy9.setOnClickListener {
                if (appCompatTextViewFuzzy9.tag == "RED") {
                    val result = automaticFuzzyClass.matchStringLogic(
                            appCompatTextViewFuzzy9.text.toString(), 8
                    )
                    appCompatTextViewFuzzy9.text = result
                    updateLprCharacterIndexValue(9)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateLprCharacterIndexValue(index: Int) {
        val updateLprNumber =
                (appCompatTextViewFuzzy1.text.toString() + "" + appCompatTextViewFuzzy2.text
                        + "" + appCompatTextViewFuzzy3.text + "" + appCompatTextViewFuzzy4.text
                        + "" + appCompatTextViewFuzzy5.text + "" + appCompatTextViewFuzzy6.text
                        + "" + appCompatTextViewFuzzy7.text + "" + appCompatTextViewFuzzy8.text
                        + "" + appCompatTextViewFuzzy9.text)
        TextViewLprNumber.setText(updateLprNumber.trim())
        mAutoComTextViewMakeVeh.setText("")
        mAutoComTextViewVehModel.setText("")
        mAutoComTextViewVehColor.setText("")
        callAllTypeApi()
    }

    private fun set6ButtonColor(mLayTiming: LinearLayoutCompat?,
            btnTimings: AppCompatButton?, colorCode: Int) {
        try {
            if (colorCode == 0) {
                //            btnTimings.setText(getString(R.string.scr_btn_timings));
                btnTimings!!.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                btnTimings.setTextColor(resources.getColor(R.color.white))
                mLayTiming!!.setBackgroundResource(R.drawable.round_corner_shape_without_fill_orange_)
            }
            if (colorCode == 1) {
                //            btnTimings.setText(getString(R.string.scr_btn_timings));
                btnTimings!!.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                btnTimings.setTextColor(resources.getColor(R.color.white))
                mLayTiming!!.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 2
        private const val REQUEST_CAMERA = 0
        private const val REQUEST_CAMERA2 = 1
//        public var mTimingImagesBase64: MutableList<String> = ArrayList()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        try {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@LprDetailsActivity)

                    if (mFusedLocationClient?.lastLocation != null) {
                        mFusedLocationClient!!.lastLocation.addOnCompleteListener {
                            val result = it.result
                            if (null != result) {
                                geoLat = result.latitude
                                geoLon = result.longitude
                                getGeoAddress()
                                LogUtil.printLog("Location ", "Latitude: " + result.latitude + " , Longitude: " + result.longitude);
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Turn on location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissions()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this@LprDetailsActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this@LprDetailsActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this@LprDetailsActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                pERMISSION_ID
        )
    }


    // What must happen when permission is granted
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == pERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationUpdates()
            }
        }
    }

    /* Call Api to Add timing */
    private fun callAddTimingApi() {
        try {
            /**
             * Save selected block and street when address is locked
             */

            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble()

            val mAddTimingRequest = AddTimingRequest()

                mAddTimingRequest!!.lprState = ""
                mAddTimingRequest!!.lprNumber = TextViewLprNumber!!.text.toString()
                mAddTimingRequest!!.meterNumber = ""
                mAddTimingRequest!!.mLot = ""
                mAddTimingRequest!!.mLocation = mTvReverseCoded!!.text.toString()
                mAddTimingRequest!!.block = mTvReverseCoded!!.text.toString()
            try {
                mAddTimingRequest!!.regulationTime = if(mAutoTimingRegulationTime!!.contains("#")) mAutoTimingRegulationTime!!.split("#")[1].toLong() else 480
                mAddTimingRequest!!.regulationTimeValue = if(mAutoTimingRegulationTime!!.contains("#")) mAutoTimingRegulationTime!!.split("#")[0] else "8 HOURS"
            } catch (e: Exception) {
                mAddTimingRequest!!.regulationTime = "480".toLong()
            }
                mAddTimingRequest!!.street = ""
                mAddTimingRequest!!.side = ""
                mAddTimingRequest!!.zone = ""
                mAddTimingRequest!!.pbcZone = ""
                mAddTimingRequest!!.remark = ""
                mAddTimingRequest!!.remark2 = ""
                mAddTimingRequest!!.mTireStemFront = 0
                mAddTimingRequest!!.mTireStemBack = 0
                mAddTimingRequest!!.mVin = ""
            mAddTimingRequest!!.status = "Open"
            mAddTimingRequest!!.latitude = mLat
            mAddTimingRequest!!.longitiude = mLong
            mAddTimingRequest!!.source = "officer"
            mAddTimingRequest!!.officerName = mWelcomeForm?.officerFirstName.nullSafety() + " " +
                    mWelcomeForm?.officerLastName.nullSafety()
            mAddTimingRequest!!.badgeId = mWelcomeForm?.officerBadgeId.nullSafety()
            //            mAddTimingRequest.setShift(mWelcomeFormData.getOfficerShift());
            mAddTimingRequest!!.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            mAddTimingRequest!!.supervisor = mWelcomeForm?.officerSupervisor.nullSafety()
            mAddTimingRequest!!.markStartTimestamp = AppUtils.splitDateLpr(mZone)
            mAddTimingRequest!!.markIssueTimestamp = AppUtils.splitDateLpr(mZone)
            mAddTimingRequest!!.mMake = mSelectedMake
            mAddTimingRequest!!.mModel = mSelectedModel
            mAddTimingRequest!!.mColor = mSelectedColor
//            mAddTimingRequest.mAddress = mAddress
            mAddTimingRequest!!.mAddress = mAddTimingRequest!!.block+" "+mAddTimingRequest!!.street
            mAddTimingRequest!!.imageUrls = null
            if (isInternetAvailable(this@LprDetailsActivity)) {
                    mAddTimingViewModel?.hitAddTimingApi(mAddTimingRequest)
            } else {
                LogUtil.printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun OVerTimeParkingForTicketDetailsCharleston(timeMarkData: TimingMarkData) {
        try {
            /**
             * Save selected block and street when address is locked
             */
            mainScope.async {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble()

                val mAddTimingRequest = AddTimingRequest()

                mAddTimingRequest!!.lprState = timeMarkData!!.lpState
                mAddTimingRequest!!.lprNumber = TextViewLprNumber!!.text.toString()
                mAddTimingRequest!!.meterNumber = timeMarkData!!.meterNumber
                mAddTimingRequest!!.mLot = timeMarkData!!.lot
                mAddTimingRequest!!.mLocation = timeMarkData!!.location.toString()
                mAddTimingRequest!!.block = timeMarkData!!.block
                try {
//                    mAddTimingRequest!!.regulationTime = 60
//                    mAddTimingRequest!!.regulationTimeValue = "1 Hour"//timeMarkData.regulationTime

                    mAddTimingRequest!!.regulationTime = timeMarkData!!.regulationTime
                    mAddTimingRequest!!.regulationTimeValue = timeMarkData!!.regulationTimeValue//timeMarkData.regulationTime
                } catch (e: Exception) {
                    mAddTimingRequest!!.regulationTime = "480".toLong()
                }
                mAddTimingRequest!!.street = timeMarkData!!.street
                mAddTimingRequest!!.side = timeMarkData!!.side
                mAddTimingRequest!!.zone = timeMarkData!!.zone
                mAddTimingRequest!!.remark = timeMarkData!!.remark
                mAddTimingRequest!!.remark2 = timeMarkData!!.remark2
                mAddTimingRequest!!.mTireStemFront = timeMarkData!!.tireStemFront!!.toInt()
                mAddTimingRequest!!.mTireStemBack = timeMarkData!!.tireStemBack!!.toInt()
                mAddTimingRequest!!.mVin = timeMarkData!!.vinNumber
                mAddTimingRequest!!.status = "Open"
                mAddTimingRequest!!.latitude = mLat
                mAddTimingRequest!!.longitiude = mLong
                mAddTimingRequest!!.source = "officer"
                mAddTimingRequest!!.officerName =
                    mWelcomeForm?.officerFirstName.nullSafety() + " " +
                            mWelcomeForm?.officerLastName.nullSafety()
                mAddTimingRequest!!.badgeId = mWelcomeForm?.officerBadgeId.nullSafety()
                //            mAddTimingRequest.setShift(mWelcomeFormData.getOfficerShift());
                mAddTimingRequest!!.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                mAddTimingRequest!!.supervisor = mWelcomeForm?.officerSupervisor.nullSafety()
                mAddTimingRequest!!.markStartTimestamp = AppUtils.splitDateLpr(mZone)
                mAddTimingRequest!!.markIssueTimestamp = AppUtils.splitDateLpr(mZone)
                mAddTimingRequest!!.mMake = mSelectedMake
                mAddTimingRequest!!.mModel = mSelectedModel
                mAddTimingRequest!!.mColor = mSelectedColor
//            mAddTimingRequest.mAddress = mAddress
                mAddTimingRequest!!.mAddress =
                    mAddTimingRequest!!.block + " " + mAddTimingRequest!!.street
                mAddTimingRequest!!.imageUrls = timeMarkData!!.images
                sharedPreference.writeOverTimeParkingTicketDetails(
                    SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                    mAddTimingRequest!!
                )
//            if (isInternetAvailable(this@LprDetailsActivity)) {
//                mAddTimingViewModel?.hitAddTimingApi(mAddTimingRequest)
//            } else {
//                LogUtil.printToastMSGForErrorWarning(
//                    applicationContext,
//                    getString(R.string.err_msg_connection_was_refused)
//                )
//            }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun showPopupForAutoWarningTicket() {
        showCustomAlertDialog(
            this@LprDetailsActivity,
            getString(R.string.scr_lbl_auto_warning_ticket),
            getString(R.string.scr_lbl_auto_warning_desc),
            getString(R.string.alt_lbl_OK),
            getString(R.string.scr_btn_cancel),
            this
        )
    }


    private  fun buildCreateTicketRequest(): CreateTicketRequest {

        return CreateTicketRequest().apply {
            lifecycleScope.launch {
                mTicketNumber = getMyDatabase()?.dbDAO?.getCitationBooklet(0)
                if (mTicketNumber!!.size > 0) {
                    var mCitaionImagesLinks: MutableList<String> = ArrayList()
                    val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble()
                    val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble()
                    val mApplicationList =
                        Singleton.getDataSetList(DATASET_VIOLATION_LIST, getMyDatabase())

                    locationDetails = buildLocationDetails()
                    vehicleDetails = buildVehicleDetails()

                    violationDetails = buildViolationDetails(mApplicationList)
                    invoiceFeeStructure = buildInvoiceFeeStructure()
                    officerDetails = buildOfficerDetails()
                    commentsDetails = buildCommentsDetails()
                    headerDetails = buildHeaderDetails()
                    lprNumber = TextViewLprNumber?.text.toString()
                    code = violationDetails?.code
                    hearingDate = ""
                    ticketNo = mTicketNumber!![0]!!.citationBooklet
                    type = buildTicketType()
                    timeLimitEnforcementObservedTime = ""
                    imageUrls = mCitaionImagesLinks
                    notes = ""
                    status = "Valid"
                    citationStartTimestamp = splitDateLpr("")
                    citationIssueTimestamp = splitDateLpr("")
                    isReissue = sharedPreference.read(SharedPrefKey.isReissueTicket, "true")
                        .equals("true", true)
                    isTimeLimitEnforcement =
                        sharedPreference.read(SharedPrefKey.isTimeLimitEnforcement, "true")
                            .equals("true", true)
                    timeLimitEnforcementId = ""
//            setLatLongFromPrefsIfMissing()
                    mLatitude = mLat
                    mLongitiude = mLong
                    val jsonObject = JSONObject().apply {
                        put("print_query",AppUtils.printQueryStringBuilder.toString() )
                        put("print_height", sharedPreference.readInt(SharedPrefKey.LAST_PRINTOUT_HEIGHT,1000))
                    }
                    printQuery = jsonObject.toString()
                }else{
                    showCustomAlertDialog(
                        this@LprDetailsActivity,
                        getString(R.string.scr_lbl_booklet),
                        getString(R.string.scr_lbl_booklet_desc),
                        getString(R.string.alt_lbl_OK),
                        getString(R.string.scr_btn_cancel),
                        this@LprDetailsActivity
                    )
                }
            }
    }
  }

    private fun buildVehicleDetails(): VehicleDetails {
        return VehicleDetails().apply {
            lprNo = TextViewLprNumber?.text.toString()
            make = mAutoComTextViewMakeVeh?.text.toString()
            model = mAutoComTextViewVehModel?.text.toString()
            model_lookup_code = ""
            color = mAutoComTextViewVehColor?.text.toString()
            state = "TX"
            body_style =  ""
            body_style_lookup_code =  ""
            mLicenseExpiry =  ""
            vin_number =  ""
            decal_year =  ""
            decal_number =  ""
        }
    }

//    private fun buildViolationDetails(mApplicationList: List<DatasetResponse>?): ViolationDetails {
//        return ViolationDetails().apply {
//            for ((i, item) in mApplicationList!!.withIndex()) {
//                val isWarningViolation = item.mWarningViolation
//                if(isWarningViolation.equals("1")){
//                violation = item.violation ?: ""
//                code = item.violationCode ?: ""
//                description = item.violationDescription ?: ""
//                fine = item.violationFine ?: 0.0
//                late_fine = item.mViolationLateFine ?: 0.0
//                due_15_days = (item.due_15_days ?: 0.0) as Double?
//                due_30_days = (item.due_30_days ?: 0.0) as Double?
//                due_45_days = (item.due_45_days ?: 0.0) as Double?
//                export_code = (item.mExportCode ?: "")
//                mCost = (item.cost ?: 0.0) as Double?
//                }else{
//                    printToastMSG(
//                        this@LprDetailsActivity,
//                        getString(R.string.scr_lbl_violation_list)
//                    )
//                }
//            }
//
//        }
//    }

    private fun buildViolationDetails(mApplicationList: List<DatasetResponse>?): ViolationDetails {
        val details = ViolationDetails()
        if (mApplicationList.isNullOrEmpty()) return details

        for (item in mApplicationList) {
            if (item.mWarningViolation == "1") {
                details.apply {
                    violation = item.violation ?: ""
                    code = item.violationCode ?: ""
                    description = item.violationDescription ?: ""
                    fine = item.violationFine ?: 0.0
                    late_fine = item.mViolationLateFine ?: 0.0
                    due_15_days = (item.due_15_days?.toDouble() ?: 0.0)
                    due_30_days = (item.due_30_days?.toDouble() ?: 0.0)
                    due_45_days = (item.due_45_days?.toDouble() ?: 0.0)
                    export_code = item.mExportCode ?: ""
                    mCost = (item.cost?.toDouble() ?: 0.0)
                }
            } else {
//                printToastMSG(
//                    this@LprDetailsActivity,
//                    getString(R.string.scr_lbl_violation_list)
//                )
            }
        }

        if(details?.description.isNullOrEmpty())
        {
            printToastMSG(
                    this@LprDetailsActivity,
                    getString(R.string.scr_lbl_violation_list)
                )
        }
        return details
    }

    private fun buildOfficerDetails(): com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.OfficerDetails {
        return com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.OfficerDetails().apply {
            agency =  mWelcomeForm?.agency.nullSafety()
            badgeId =  mWelcomeForm?.officerBadgeId.nullSafety()
            beat =  mWelcomeForm?.officerBeat.nullSafety()
            officer_lookup_code =  mWelcomeForm?.officer_lookup_code.nullSafety()
            peo_fname =  ""
            peo_lname =  ""
            peo_name =  ""
            squad =  ""
            zone =  ""
            mShift =  sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            mDdeviceId = mWelcomeForm?.officerDeviceId.nullSafety()
            mDdeviceFriendlyName = mWelcomeForm?.officerDeviceName.nullSafety()

            officer_name =
                AppUtils.getOfficerName(
                    mWelcomeForm?.officerFirstName + " " +
                            mWelcomeForm?.officerLastName)
        }
     }
    private fun buildCommentsDetails(): CommentsDetails {
        return CommentsDetails().apply {
             remark1 = "IF YOU ARE A VISITOR, PLEASE VISIT THE RECEPTIONIST OF YOUR PROVIDER TO ACQUIRE A “VISITOR PERMIT” TO AVOID PARKING ENFORCEMENT ACTION. IF YOU ARE AN EMPLOYEE, PLEASE ACQUIRE AN EMPLOYEE PERMIT AND PARK IN THE DESIGNATED EMPLOYEE PARKING LOT TO AVOID VEHICLE IMMOBILIZATION AND SUBJECT TO A \$50 RELEASE FEE"
             remark2 = ""
             note1 = ""
             note2 = ""
             note3 = ""
        }
     }
    private fun buildHeaderDetails(): HeaderDetails {
        return HeaderDetails().apply {
            citationNumber = mTicketNumber!![0]!!.citationBooklet
            timestamp = AppUtils.getCurrentDateTimeForCitationForm(splitDateLpr(""))
        }
     }

    fun showWhiteAndRedStickerPopup(anchorView: View, item: ScofflawDataResponse?) {
        val context = anchorView.context
        val popupView =  LayoutInflater.from(context).inflate(R.layout.popup_white_red_button_boot_tow, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.elevation = 10f

        val btnWhite = popupView.findViewById<Button>(R.id.btnWhite)
        val btnRed = popupView.findViewById<Button>(R.id.btnRed)

        btnWhite.setOnClickListener {
            popupWindow.dismiss()
            MoveToCitationFormWithScofflawData(1)
        }

        btnRed.setOnClickListener {
            popupWindow.dismiss()
            MoveToCitationFormWithScofflawData(2)
        }

        popupWindow.showAsDropDown(anchorView, 0, 0)
    }

    fun updateButtonVisibilityForExmptStolen(isExemptVisible: Int, isStolenVisible: Int) {
        if (isExemptVisible==8) {
            btnExempt.visibility = View.GONE
            val params = btnStolen.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, params.topMargin, 0, params.bottomMargin)
            btnStolen.layoutParams = params
        } else if (isStolenVisible==8) {
            btnStolen.visibility = View.GONE
            val params = btnExempt.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, params.topMargin, 0, params.bottomMargin)
            btnExempt.layoutParams = params
        } else {
            // Both visible, restore original margins
            btnExempt.visibility = View.VISIBLE
            btnStolen.visibility = View.VISIBLE

            val exemptParams = btnExempt.layoutParams as LinearLayout.LayoutParams
            exemptParams.setMargins(0, exemptParams.topMargin, resources.getDimensionPixelSize(R.dimen._10dp), exemptParams.bottomMargin)
            btnExempt.layoutParams = exemptParams

            val stolenParams = btnStolen.layoutParams as LinearLayout.LayoutParams
            stolenParams.setMargins(resources.getDimensionPixelSize(R.dimen._10dp), stolenParams.topMargin, 0, stolenParams.bottomMargin)
            btnStolen.layoutParams = stolenParams
        }
    }
    fun updateButtonVisibilityScofflawTimings(
        isScofflawVisible: Int,
        isTimingsVisible: Int
    ) {
        if (isScofflawVisible==8) {
            btnScofflaw.visibility = View.GONE
            val timingsParams = btnTimings.layoutParams as LinearLayoutCompat.LayoutParams
            timingsParams.setMargins(0, timingsParams.topMargin, 0, timingsParams.bottomMargin)
            btnTimings.layoutParams = timingsParams
        } else if (isTimingsVisible==8) {
            btnTimings.visibility = View.GONE
            val scofflawParams = btnScofflaw.layoutParams as LinearLayoutCompat.LayoutParams
            scofflawParams.setMargins(0, scofflawParams.topMargin, 0, scofflawParams.bottomMargin)
            btnScofflaw.layoutParams = scofflawParams
        } else {
            // Both visible, restore original margins
            btnScofflaw.visibility = View.VISIBLE
            btnTimings.visibility = View.VISIBLE

            val margin10 = btnScofflaw.context.resources.getDimensionPixelSize(R.dimen._10dp)

            val scofflawParams = btnScofflaw.layoutParams as LinearLayoutCompat.LayoutParams
            scofflawParams.setMargins(0, scofflawParams.topMargin, margin10, scofflawParams.bottomMargin)
            btnScofflaw.layoutParams = scofflawParams

            val timingsParams = btnTimings.layoutParams as LinearLayoutCompat.LayoutParams
            timingsParams.setMargins(margin10, timingsParams.topMargin, 0, timingsParams.bottomMargin)
            btnTimings.layoutParams = timingsParams
        }
    }
    fun updateButtonVisibilityPermitPayment(
        isPermitVisible: Int,
        isPaymentVisible: Int
    ) {
        if (isPermitVisible==8) {
            btnPermit.visibility = View.GONE
            val timingsParams = btnPayment.layoutParams as LinearLayoutCompat.LayoutParams
            timingsParams.setMargins(0, timingsParams.topMargin, 0, timingsParams.bottomMargin)
            btnPayment.layoutParams = timingsParams
        } else if (isPaymentVisible==8) {
            btnPayment.visibility = View.GONE
            val scofflawParams = btnPermit.layoutParams as LinearLayoutCompat.LayoutParams
            scofflawParams.setMargins(0, scofflawParams.topMargin, 0, scofflawParams.bottomMargin)
            btnPermit.layoutParams = scofflawParams
        } else {
            // Both visible, restore original margins
            btnPermit.visibility = View.VISIBLE
            btnPayment.visibility = View.VISIBLE

            val margin10 = btnPermit.context.resources.getDimensionPixelSize(R.dimen._10dp)

            val scofflawParams = btnPermit.layoutParams as LinearLayoutCompat.LayoutParams
            scofflawParams.setMargins(0, scofflawParams.topMargin, margin10, scofflawParams.bottomMargin)
            btnPermit.layoutParams = scofflawParams

            val timingsParams = btnPayment.layoutParams as LinearLayoutCompat.LayoutParams
            timingsParams.setMargins(margin10, timingsParams.topMargin, 0, timingsParams.bottomMargin)
            btnPayment.layoutParams = timingsParams
        }
    }
}