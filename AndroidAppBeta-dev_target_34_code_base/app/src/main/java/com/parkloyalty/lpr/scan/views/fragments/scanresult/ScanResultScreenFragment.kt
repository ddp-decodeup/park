package com.parkloyalty.lpr.scan.views.fragments.scanresult

import DialogUtil
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.camera2.CameraActivity
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerRequest
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerResponse
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.databinding.FragmentScanResultsScreenBinding
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.buildInvoiceFeeStructure
import com.parkloyalty.lpr.scan.extensions.buildLocationDetails
import com.parkloyalty.lpr.scan.extensions.buildTicketType
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpen
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpenGOA
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForTimingDataClickOnScanResultScreenForAutoFillRemark
import com.parkloyalty.lpr.scan.extensions.disableButton
import com.parkloyalty.lpr.scan.extensions.enableButton
import com.parkloyalty.lpr.scan.extensions.getAppVersionName
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.getFormattedAmount
import com.parkloyalty.lpr.scan.extensions.getFuzzyStringList
import com.parkloyalty.lpr.scan.extensions.getIndexOfLocation
import com.parkloyalty.lpr.scan.extensions.getIndexOfMakeText
import com.parkloyalty.lpr.scan.extensions.getSettingFileValuesForRemarkAutoFilledWithElapsedTime
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.isFlavorForBlockCitationHistoryItemClick
import com.parkloyalty.lpr.scan.extensions.isFlavorForCitationHistoryPrinter
import com.parkloyalty.lpr.scan.extensions.isFlavorForFinalWarningHandling
import com.parkloyalty.lpr.scan.extensions.isFlavorForNotAssigningStreetBlockSideFromCitationHistoryItemClick
import com.parkloyalty.lpr.scan.extensions.isFlavorForPriorWarningHandling
import com.parkloyalty.lpr.scan.extensions.isFlavorForRedirectToScofflaw
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.logE
import com.parkloyalty.lpr.scan.extensions.nav
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.safeNavigate
import com.parkloyalty.lpr.scan.extensions.setCrossClearButton
import com.parkloyalty.lpr.scan.extensions.setListOnlyDropDown
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_DEFAULT_REGULATION_TIME_AUTO
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_IS_AUTO_TIMING
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.locationservice.LocationUtils.getAddressFromLatLng
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.CameraRawFeedDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.LastSecondCheckResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.ResultsItemCameraRaw
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.CameraRawFeedDataAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ExemptAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.HistoryAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.PaymentAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.PermitAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ScofflawAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.StolenAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.TimingAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.DataFromLprLocation
import com.parkloyalty.lpr.scan.ui.check_setup.model.DataFromLprRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.PaymentDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimingMarkData
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimingMarkResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.CitationDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.CitationResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ExemptDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ExemptResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.MakeModelColorData
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.MakeModelColorResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.PaymentResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.PermitDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.PermitResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ScofflawDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ScofflawResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.StolenDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.StolenResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CommentsDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.HeaderDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.OfficerDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.VehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.ViolationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.ImageCache
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.API_CONSTANT_CITATION_STATUS_VALID
import com.parkloyalty.lpr.scan.util.AccessibilityUtil
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.compareDates
import com.parkloyalty.lpr.scan.util.AppUtils.getLocalDateFromUTC
import com.parkloyalty.lpr.scan.util.AppUtils.isTimingExpired
import com.parkloyalty.lpr.scan.util.AppUtils.sixActionButtonVisibilityCheck
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPRTime
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.AutomaticFuzzyClass
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.INTENT_KEY_CITATION_NUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPR_BUNDLE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPR_NUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNER_TYPE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_TIMING_IMAGES_BASE64
import com.parkloyalty.lpr.scan.util.INTENT_KEY_UNPAID_CITATION_COUNT
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VEHICLE_INFO
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VEHICLE_STICKER_URL
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VIOLATION_DATE
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.PermissionUtils
import com.parkloyalty.lpr.scan.util.SDF_FULL_DATE_UTC
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_ID_TIMESTAMP
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_TIMESTAMP
import com.parkloyalty.lpr.scan.util.SDF_MM_DD_YYYY
import com.parkloyalty.lpr.scan.util.SDF_ddHHmmss
import com.parkloyalty.lpr.scan.util.STATE_NEW_YORK
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutCrossButtons
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
import com.parkloyalty.lpr.scan.util.setCustomAccessibility
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_CAMERA_RAW_FEED_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_CITATION_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_CITATION_T2_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_EXEMPT_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_MAKE_MODEL_COLOR_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_PAYMENT_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_PERMIT_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_SCOFFLAW_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_SUB_TAG_NAME_GET_STOLEN_DATA
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_TIMING
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CREATE_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_DATA_FROM_LPR
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_LAST_SECOND_CHECK
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_TIMING_MARK
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_INACTIVE_METER_BUZZER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LPR_SCAN_LOGGER
import com.parkloyalty.lpr.scan.utils.AppConstants.TEMP_IMAGE_FILE_NAME
import com.parkloyalty.lpr.scan.utils.CitationFormUtils.CITATION_FORM_FIELD_TYPE_LIST_ONLY
import com.parkloyalty.lpr.scan.utils.NewConstructLayoutBuilder
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.camerahelper.CameraHelper
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.utils.permissions.PermissionUtils.getCameraPermission
import com.parkloyalty.lpr.scan.vehiclestickerscan.model.VehicleInfoModel
import com.parkloyalty.lpr.scan.views.MainActivityAction
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.NewLprScanActivity
import com.parkloyalty.lpr.scan.views.NewVehicleStickerScanActivity
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.text.ParseException
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class ScanResultScreenFragment : BaseFragment<FragmentScanResultsScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val scanResultScreenViewModel: ScanResultScreenViewModel by viewModels()

    @Inject
    lateinit var sharedPreference: SharedPref

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var constructLayoutBuilder: NewConstructLayoutBuilder

    @Inject
    lateinit var cameraHelper: CameraHelper

    private var session: CameraHelper.Session? = null

    private lateinit var cameraTwoActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var lprScanActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var vehicleStickerActivityLauncher: ActivityResultLauncher<Intent>

    lateinit var textInputLayoutLprNumber: TextInputLayout
    lateinit var textViewLprNumber: AppCompatEditText
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
    lateinit var btnReScan: MaterialButton
    lateinit var btnCheck: MaterialButton
    lateinit var btnIssueStatus: MaterialButton
    lateinit var btnIssueHistory: MaterialButton
    lateinit var btnCameraRawFeedData: AppCompatButton
    lateinit var mTvReverseCoded: AppCompatTextView
    lateinit var mAppTextTimeName: AppCompatTextView
    lateinit var mAutoComTextViewVehColor: AppCompatAutoCompleteTextView
    lateinit var mAutoComTextViewVehModel: AppCompatAutoCompleteTextView
    lateinit var mAutoComTextViewMakeVeh: AppCompatAutoCompleteTextView
    lateinit var textInputLayoutVehColor: TextInputLayout
    lateinit var textInputLayoutVehModel: TextInputLayout
    lateinit var textInputLayoutVehMake: TextInputLayout
    lateinit var mRecyclerViewHistory: RecyclerView
    lateinit var mRecyclerViewPayment: RecyclerView
    lateinit var mRecyclerViewPermit: RecyclerView
    lateinit var mRecyclerViewScofflaw: RecyclerView
    lateinit var mRecyclerViewStolen: RecyclerView
    lateinit var mRecyclerViewExempt: RecyclerView
    lateinit var mRecyclerViewTiming: RecyclerView
    lateinit var mRecyclerViewCameraRawFeedData: RecyclerView
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
    lateinit var appCompatTextViewFineAmountSum: AppCompatTextView
    lateinit var btnWhiteSticker: Button
    lateinit var btnRedSticker: Button
    lateinit var btnReScanSticker: MaterialButton

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

    private val mModelList: MutableList<DatasetResponse> = ArrayList()
    private var mImageCount = 0
    private var mPayment = false
    private val mTiming = false
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
    private var paymentLength: Long? = null
    private var paymentFuzzyLength: Long? = null
    private var modelColorLength: Long? = null
    private var citationDataLength: Long? = null
    private var scofflawDataLength: Long? = null
    private var stolenDataLength: Long? = null
    private var exemptDataLength: Long? = null
    private var permitDataLength: Long? = null
    private var permitDataFuzzyLength: Long? = null
    private var settingsList: List<DatasetResponse>? = null
    private var adapterModel: ArrayAdapter<String?>? = null
    private var separated: Array<String>? = null
    private var separatedEventLogger: Array<String>? = null
    private var addressGeo: String? = null
    private val uniqueDataSet: MutableSet<String> = HashSet()
    private var mTicketNumber: List<CitationBookletModel?>? = ArrayList()
    private var mDataSetTimeObject: TimestampDatatbase? = null

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
    private var citationHistoryCount = 0
    private var mViolationCode = ""

    //private var isActivityInForeground = false
    private var eventLoggerAPIDelay = 2.5

    private var mZone = "CST"
    private var mHour = ""

    val formatter = DecimalFormat("00")

    private var vehicleStickerInfo: VehicleInfoModel? = null

    private var permitApiFailureCallCount: Int = 1
    private val maxPermitApiRetryCallCount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager =
            permissionFactory.create(caller = this, context = requireContext(), fragment = this)

        // create a session tied to this fragment lifecycle
        session = cameraHelper.createSession(fragment = this)

        registerActivityResultLauncher()
    }


    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentScanResultsScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        textInputLayoutLprNumber = binding.inputText
        textViewLprNumber = binding.tvLprNumber
        mTextViewTabStatus = binding.tvTabStatus
        mTextViewTabHistory = binding.tvTabHistory
        mViewStatus = binding.viewStatus
        mViewHistory = binding.viewHistory
        mLayStatus = binding.layStatus
        mLayHistory = binding.layHistory
        mLayHide = binding.layHide
        mLayPayment = binding.layPayment
        mLayHistoryTextSection = binding.layhistorytext
        mLayPermit = binding.layPermit
        mLayScofflaw = binding.layScofflaw
        mLayStolen = binding.layStolen
        mLayExempt = binding.layExempt
        mLayCameraRawFeedData = binding.layCameraRawFeedData
        mEmptyLayout = binding.layoutContentEmptyLayout.emptyLayout
        mEmptyLayoutStatus = binding.layoutContentEmptyLayoutStatus.emptyLayoutStatus
        mLayTiming = binding.layTiming
        mImageViewNumberPlate = binding.ivNumberPlate
        mImageViewRight = binding.ivRight
        btnPayment = binding.btnPayment
        btnPermit = binding.btnPermit
        btnExempt = binding.btnExempt
        btnScofflaw = binding.btnScofflaw
        btnTimings = binding.btnTimings
        btnStolen = binding.btnStolen
        btnReScan = binding.btnReScan
        btnCheck = binding.btnCheck
        btnIssueStatus = binding.btnIssueStatus
        btnIssueHistory = binding.btnIssueHistory
        btnCameraRawFeedData = binding.btnCameraRawFeedData
        mTvReverseCoded = binding.reverseCoded
        mAppTextTimeName = binding.appTextTimeName
        mAutoComTextViewVehColor = binding.AutoComTextViewVehColor
        mAutoComTextViewVehModel = binding.AutoComTextViewVehModel
        mAutoComTextViewMakeVeh = binding.AutoComTextViewMakeVeh
        textInputLayoutVehColor = binding.textInputLayoutVehColor
        textInputLayoutVehModel = binding.textInputLayoutVehModel
        textInputLayoutVehMake = binding.textInputLayoutVehMake
        mRecyclerViewHistory = binding.rvHistory
        mRecyclerViewPayment = binding.rvPayment
        mRecyclerViewPermit = binding.rvPermit
        mRecyclerViewScofflaw = binding.rvScofflaw
        mRecyclerViewStolen = binding.rvStolen
        mRecyclerViewExempt = binding.rvExempt
        mRecyclerViewTiming = binding.rvTiming
        mRecyclerViewCameraRawFeedData = binding.rvCameraRawFeedData
        linearLayoutCompatTimingIcon = binding.llTimingIcon
        linearLayoutCompatMake = binding.llMake
        linearLayoutCompatModel = binding.llModel
        linearLayoutCompatColor = binding.llColor
        linearLayoutCompatHistory = binding.tabHistory
        linearLayoutCompatFuzzy = binding.layoutContentLprFuzzyLogicBox.llFuzzyLogic
        appCompatTextViewFuzzy1 = binding.layoutContentLprFuzzyLogicBox.txt1
        appCompatTextViewFuzzy2 = binding.layoutContentLprFuzzyLogicBox.txt2
        appCompatTextViewFuzzy3 = binding.layoutContentLprFuzzyLogicBox.txt3
        appCompatTextViewFuzzy4 = binding.layoutContentLprFuzzyLogicBox.txt4
        appCompatTextViewFuzzy5 = binding.layoutContentLprFuzzyLogicBox.txt5
        appCompatTextViewFuzzy6 = binding.layoutContentLprFuzzyLogicBox.txt6
        appCompatTextViewFuzzy7 = binding.layoutContentLprFuzzyLogicBox.txt7
        appCompatTextViewFuzzy8 = binding.layoutContentLprFuzzyLogicBox.txt8
        appCompatTextViewFuzzy9 = binding.layoutContentLprFuzzyLogicBox.txt9
        appCompatTextViewPaymentFuzzyPlate = binding.txtPaymentFuzzyPlate
        appCompatImageViewLock = binding.ivLock
        textInputLayoutVehLot = binding.textInputLayoutVehLot
        mAutoComTextViewLot = binding.AutoComTextViewVehLot
        linearLayoutCompatZone = binding.llZone
        appCompatTextViewFineAmountSum = binding.tvFineAmountSum
        btnWhiteSticker = binding.layoutContentWhiteRedButtonBootTow.btnWhiteSticker
        btnRedSticker = binding.layoutContentWhiteRedButtonBootTow.btnRedSticker
        btnReScanSticker = binding.btnReScanSticker
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun initViewLifecycleScope() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainActivityViewModel.setToolbarVisibility(true)
                    mainActivityViewModel.setToolbarComponents(
                        showBackButton = true, showLogo = true, showHemMenu = true
                    )
                }

                launch {
                    scanResultScreenViewModel.getCitationDataFromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it,
                            apiSubTagName = API_SUB_TAG_NAME_GET_CITATION_DATA
                        )
                    }
                }

                launch {
                    scanResultScreenViewModel.getCitationDataT2FromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it,
                            apiSubTagName = API_SUB_TAG_NAME_GET_CITATION_T2_DATA
                        )
                    }
                }

                launch {
                    scanResultScreenViewModel.getMakeModelColorDataFromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it,
                            apiSubTagName = API_SUB_TAG_NAME_GET_MAKE_MODEL_COLOR_DATA
                        )
                    }
                }

                launch {
                    scanResultScreenViewModel.getCameraRawFeedDataFromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it,
                            apiSubTagName = API_SUB_TAG_NAME_GET_CAMERA_RAW_FEED_DATA
                        )
                    }
                }

                launch {
                    scanResultScreenViewModel.getPaymentDataFromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it, apiSubTagName = API_SUB_TAG_NAME_GET_PAYMENT_DATA
                        )
                    }
                }
                launch {
                    scanResultScreenViewModel.getScofflawDataFromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it, apiSubTagName = API_SUB_TAG_NAME_GET_SCOFFLAW_DATA
                        )
                    }
                }
                launch {
                    scanResultScreenViewModel.getExemptDataFromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it, apiSubTagName = API_SUB_TAG_NAME_GET_EXEMPT_DATA
                        )
                    }
                }
                launch {
                    scanResultScreenViewModel.getStolenDataFromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it, apiSubTagName = API_SUB_TAG_NAME_GET_STOLEN_DATA
                        )
                    }
                }
                launch {
                    scanResultScreenViewModel.getPermitDataFromLprResponse.collect {
                        consumeResponse(
                            newApiResponse = it.first,
                            apiSubTagName = API_SUB_TAG_NAME_GET_PERMIT_DATA,
                            type = it.second,
                            lprNumber = it.third
                        )
                    }
                }
                launch {
                    scanResultScreenViewModel.lprScanLoggerResponse.collect(::consumeResponse)
                }
                launch {
                    scanResultScreenViewModel.timingMarkResponse.collect(::consumeResponse)
                }
                launch {
                    scanResultScreenViewModel.lastSecondCheckResponse.collect(::consumeResponse)
                }
                launch {
                    scanResultScreenViewModel.addTimingResponse.collect(::consumeResponse)
                }
                launch {
                    scanResultScreenViewModel.createTicketResponse.collect(::consumeResponse)
                }
                launch {
                    scanResultScreenViewModel.inactiveMeterBuzzerResponse.collect(::consumeResponse)
                }
            }
        }
    }

    override fun initialiseData() {
        //Put everything in init method, so onActivityResult can also call this method to reinitialize data
        init(arguments)
    }

    override fun setupClickListeners() {
        btnIssueHistory.setOnClickListener {
            moveToNext("none")
        }

        btnIssueStatus.setOnClickListener {
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
                ) || BuildConfig.FLAVOR == DuncanBrandingApp13()
            ) {
                mBlock = ""
                mStreetItem = ""
            }
            viewLifecycleOwner.lifecycleScope.launch {
                scanResultScreenViewModel.insertWelcomeForm(mWelcomeForm!!)
                moveToNext("none")
            }
        }

        binding.ivCamera.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                permissionManager.ensurePermissionsThen(
                    permissions = getCameraPermission(),
                    rationaleMessage = getString(R.string.permission_message_camera_permission_required)
                ) {
                    if (mCamera2Setting.equals("YES", ignoreCase = true)) {
                        camera2Intent()
                    } else {
                        cameraIntent()
                    }
                }
            }
        }

        btnReScan.setOnClickListener {
            val intent = Intent(requireContext(), NewLprScanActivity::class.java)
            intent.putExtra(INTENT_KEY_SCANNER_TYPE, mainActivityViewModel.getLprScannerType())
            intent.putExtra("Lot", mLotItem)
            intent.putExtra("Location", mLotItem)
            intent.putExtra("Space_id", mSpaceId)
            intent.putExtra("Meter", mMeterNameItem)
            intent.putExtra("Zone", mPBCZone)
            intent.putExtra("Street", mStreetItem)
            intent.putExtra("Block", mBlock)
            intent.putExtra("Direction", mDirectionItem)
            intent.putExtra("from_scr", mFromScreen)
            lprScanActivityLauncher.launch(intent)
        }

        btnCheck.setOnClickListener {
            if (textViewLprNumber.editableText.toString().trim() != "") {
                setFuzzyLogicSetTextView()
                setFuzzyLogicTextViewClick()
                mAutoComTextViewMakeVeh.setText("")
                mAutoComTextViewVehModel.setText("")
                mAutoComTextViewVehColor.setText("")
                linearLayoutCompatFuzzy.visibility = View.VISIBLE
                mTimingImages.clear()
                ImageCache.base64Images.clear()
                paymentLength = 0
                paymentFuzzyLength = 0
                modelColorLength = 0
                citationDataLength = 0
                scofflawDataLength = 0
                stolenDataLength = 0
                exemptDataLength = 0
                permitDataLength = 0
                permitDataFuzzyLength = 0
                mListTiming = ArrayList()
                setAdapterForTiming(mListTiming)

                if (btnCheck.isEnabled) {
                    btnCheck.disableButton()
                    callAllTypeApi()
                }

            } else {
                requireContext().toast(getString(R.string.err_msg_plate_is_empty))
            }
        }

        binding.tabStatus.setOnClickListener {
            mLayHistory.visibility = View.GONE
            mLayStatus.visibility = View.VISIBLE
            mTextViewTabStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.deep_blue
                )
            )
            mViewStatus.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.deep_blue
                )
            )
            mTextViewTabHistory.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.app_gray
                )
            )
            mViewHistory.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.app_gray
                )
            )

            //Set ADA
            mTextViewTabStatus.contentDescription = getString(
                R.string.ada_content_description_tab_selected, mTextViewTabStatus.text.toString()
            )

            mTextViewTabHistory.contentDescription = getString(
                R.string.ada_content_description_tab_unselected, mTextViewTabHistory.text.toString()
            )

            AccessibilityUtil.announceForAccessibility(
                mTextViewTabStatus, getString(
                    R.string.ada_content_description_tab_selected,
                    mTextViewTabStatus.text.toString()
                )
            )
        }

        binding.tabHistory.setOnClickListener {
            mLayHistory.visibility = View.VISIBLE
            mLayStatus.visibility = View.GONE
            mTextViewTabHistory.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.deep_blue
                )
            )
            mViewHistory.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.deep_blue
                )
            )
            mTextViewTabStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.app_gray
                )
            )
            mViewStatus.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.app_gray
                )
            )

            //Set ADA
            mTextViewTabStatus.contentDescription = getString(
                R.string.ada_content_description_tab_unselected, mTextViewTabStatus.text.toString()
            )

            mTextViewTabHistory.contentDescription = getString(
                R.string.ada_content_description_tab_selected, mTextViewTabHistory.text.toString()
            )

            AccessibilityUtil.announceForAccessibility(
                mTextViewTabHistory, getString(
                    R.string.ada_content_description_tab_selected,
                    mTextViewTabHistory.text.toString()
                )
            )
        }

        btnTimings.setOnClickListener {
            if (!mTiming) {
                try {
                    val bundle = Bundle()
                    bundle.putString(
                        "lpr_number", textViewLprNumber.text.toString().trim()
                    )
                    if (mListTiming?.isNotEmpty().nullSafety()) {
                        bundle.putString(
                            "regulation", mListTiming?.firstOrNull()?.regulationTime.toString()
                        )
                    } else {
                        bundle.putString("regulation", "0")
                    }
                    bundle.putString(
                        "make", mAutoComTextViewMakeVeh.editableText.toString().trim()
                    )
                    if (mSelectedModel != null) {
                        bundle.putString(
                            "model", mAutoComTextViewVehModel.editableText.toString().trim()
                        )
                    }
                    if (mSelectedVin != null) {
                        bundle.putString(
                            "vinNumber", mSelectedVin
                        )
                    }
                    bundle.putString(
                        "color", mAutoComTextViewVehColor.editableText.toString().trim()
                    )
                    bundle.putString("address", separated?.firstOrNull())
                    bundle.putString("state", mState)

                    nav.safeNavigate(R.id.addTimeRecordScreenFragment, bundle)
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

        binding.imgPayment.setOnClickListener {
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
                binding.imSccoflaw.id, binding.llScofflawIcon.id, binding.btnScofflaw.id -> {
                    try {
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true
                            ) && (mListScofflaw != null && mListScofflaw?.isNotEmpty().nullSafety())
                        ) {
                            if (Constants.stickerList.contains(mListScofflaw?.firstOrNull()?.type)) {
                                showWhiteAndRedStickerPopup(
                                    btnScofflaw, mListScofflaw?.firstOrNull()
                                )
                            } else {
//                            LogUtil.printSnackBar(linearLayoutCompatMainLayoutView!!,requireContext(),"There is no White and Red Sticker")
                            }
                        } else {
                            val bundle = Bundle()
                            bundle.putParcelable(
                                "SCCOFFLAW", mListScofflaw?.firstOrNull()
                            )
                            bundle.putString("MAKE", mAutoComTextViewMakeVeh.text.toString())
                            bundle.putString("MODEL", mAutoComTextViewVehModel.text.toString())
                            bundle.putString("COLOR", mAutoComTextViewVehColor.text.toString())
                            bundle.putString("LPNUMBER", textViewLprNumber.text.toString())

                            //It is used for boot tow notice printing
                            if (BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_SEPTA, true
                                )
                            ) {
                                bundle.putString(
                                    INTENT_KEY_CITATION_NUMBER,
                                    mListCitation?.firstOrNull()?.ticketNo.nullSafety()
                                )
                                bundle.putString(
                                    INTENT_KEY_VIOLATION_DATE, getLocalDateFromUTC(
                                        mListCitation?.firstOrNull()?.citationIssueTimestamp.nullSafety(),
                                        SDF_FULL_DATE_UTC,
                                        SDF_MM_DD_YYYY
                                    )
                                )
                            }

                            nav.safeNavigate(R.id.bootScreenFragment, bundle)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        binding.imSccoflaw.setOnClickListener(sharedClickForScofflaw)
        binding.llScofflawIcon.setOnClickListener(sharedClickForScofflaw)
        binding.btnScofflaw.setOnClickListener(sharedClickForScofflaw)

        appCompatImageViewLock.setOnClickListener {
            updateLockButtonIcon()
        }

        btnWhiteSticker.setOnClickListener {
            moveToCitationFormWithScofflawData(1)
        }

        btnRedSticker.setOnClickListener {
            moveToCitationFormWithScofflawData(2)
        }

        btnReScanSticker.setOnClickListener {
            val intent = Intent(requireContext(), NewVehicleStickerScanActivity::class.java)
            vehicleStickerActivityLauncher.launch(intent)
        }
    }

    fun registerActivityResultLauncher() {
        cameraTwoActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            //Nothing to implement here
        }

        lprScanActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val bundle = intent?.getBundleExtra(INTENT_KEY_LPR_BUNDLE)

                    init(bundle)
                }
            }

        vehicleStickerActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val bundle = Bundle()
                    if (intent?.hasExtra(INTENT_KEY_VEHICLE_INFO).nullSafety()) {
                        bundle.apply {
                            putString(
                                INTENT_KEY_LPR_NUMBER, intent?.getStringExtra(INTENT_KEY_LPR_NUMBER)
                            )
                            putString(
                                INTENT_KEY_VEHICLE_STICKER_URL,
                                intent?.getStringExtra(INTENT_KEY_VEHICLE_STICKER_URL)
                            )
                            putSerializable(
                                INTENT_KEY_VEHICLE_INFO, intent?.getSerializableExtra(
                                    INTENT_KEY_VEHICLE_INFO, VehicleInfoModel::class.java
                                )
                            )
                        }
                    }

                    init(bundle)
                }
            }
    }

    private fun setAccessibilityForComponents() {
        setAccessibilityForTextInputLayoutDropdownButtons(requireContext(), textInputLayoutVehLot)
        setAccessibilityForTextInputLayoutDropdownButtons(requireContext(), textInputLayoutVehMake)
        setAccessibilityForTextInputLayoutDropdownButtons(requireContext(), textInputLayoutVehModel)
        setAccessibilityForTextInputLayoutDropdownButtons(requireContext(), textInputLayoutVehColor)
        setAccessibilityForTextInputLayoutCrossButtons(requireContext(), textInputLayoutLprNumber)

        linearLayoutCompatTimingIcon.setCustomAccessibility(
            contentDescription = getString(R.string.scr_lbl_timing_detail),
            role = getString(R.string.ada_role_button)
        )
        binding.ivCamera.contentDescription = getString(R.string.ada_content_description_camera)
        mTextViewTabStatus.contentDescription = getString(
            R.string.ada_content_description_tab_selected, mTextViewTabStatus.text.toString()
        )
        mTextViewTabHistory.contentDescription = getString(
            R.string.ada_content_description_tab_unselected, mTextViewTabHistory.text.toString()
        )
        mImageViewNumberPlate.contentDescription =
            getString(R.string.ada_content_description_license_plate_image)
        textViewLprNumber.contentDescription =
            getString(R.string.ada_content_description_license_plate_image)
    }

    private fun init(bundle: Bundle?) {
        mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")

        setCapField()
        mainActivityViewModel.deleteTempImages()

        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true
            )
        ) {
            binding.btnValid.setOnClickListener {
                callLastSecondCheckAPI()
            }
        }

        sharedPreference.writeOverTimeParkingTicketDetails(
            SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION, AddTimingRequest()
        )

        textInputLayoutLprNumber.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

        try {
            ApiLogsClass.writeApiPayloadTex(
                requireContext(), " Scan Result ${requireContext().getAppVersionName()} "
            )
            ImageCache.base64Images.clear()

            /**
             * Citation form delete from stack and remove saved images
             */
            //LprDetails2Activity.instanceLprDetails2Activity?.finish()
            mainActivityViewModel.deleteTempImages()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setAccessibilityForComponents()

        if (mainActivityViewModel.showAndEnableScanVehicleStickerModule) {
            btnReScanSticker.showView()
        } else {
            btnReScanSticker.hideView()
        }

        mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()
        mWelcomeForm = mainActivityViewModel.getWelcomeForm()


        textViewLprNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        if (mRegex.isEmpty()) {
                            mRegex = mainActivityViewModel.getLicensePlateFormat()
                        }
                        val p = Pattern.compile(mRegex)
                        val m = p.matcher(s)
                        val b = m.matches()
                        val p1 = Pattern.compile(mRegex) //EPC8201
                        val m1 = p1.matcher(s)
                        val b1 = m1.matches()
                        if (b) {
                            mImageViewRight.setBackgroundDrawable(null)
                            mImageViewRight.setBackgroundDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(), R.drawable.ic_right
                                )
                            )

                            //Set ADA
                            mImageViewRight.contentDescription =
                                getString(R.string.ada_content_description_license_plate_format_matched)
                        } else if (b1) {
                            mImageViewRight.setBackgroundDrawable(null)
                            mImageViewRight.setBackgroundDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(), R.drawable.ic_right
                                )
                            )

                            //Set ADA
                            mImageViewRight.contentDescription =
                                getString(R.string.ada_content_description_license_plate_format_matched)
                        } else {
                            mImageViewRight.setBackgroundDrawable(null)
                            mImageViewRight.setBackgroundDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(), R.drawable.ic_cross_lpr
                                )
                            )

                            //Set ADA
                            mImageViewRight.contentDescription =
                                getString(R.string.ada_content_description_license_plate_format_not_matched)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })


        setLayoutVisibilityBasedOnSettingResponse()

        if (bundle != null) {

            if (bundle.containsKey(INTENT_KEY_VEHICLE_INFO)) {
                vehicleStickerInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getSerializable(INTENT_KEY_VEHICLE_INFO, VehicleInfoModel::class.java)
                } else {
                    bundle.getSerializable(INTENT_KEY_VEHICLE_INFO) as VehicleInfoModel
                }

                AppUtils.setVehicleStickerData(
                    vehicleStickerInfo?.plateNumber.nullSafety(), vehicleStickerInfo
                )
                setVehicleStickerData()
            }

            val mLprNumber =
                bundle.getString("lpr_number") ?: bundle.getString(INTENT_KEY_LPR_NUMBER)
            textViewLprNumber.filters = arrayOf<InputFilter>(AllCaps())
            textViewLprNumber.setText(mLprNumber)
            textViewLprNumber.setSelection(textViewLprNumber.text!!.length)
            setFuzzyLogicSetTextView()
            setFuzzyLogicTextViewClick()
            linearLayoutCompatFuzzy.visibility = View.VISIBLE

            var imageName: String
            if (bundle.containsKey(INTENT_KEY_VEHICLE_STICKER_URL)) {
                imageName = "ny_sticker_$mLprNumber.jpg"
                mPath = bundle.getString(INTENT_KEY_VEHICLE_STICKER_URL)
            } else {
                imageName = "anpr_$mLprNumber.jpg"
                mPath = if (bundle.containsKey("screen") && bundle.getString("screen")
                        .equals("ContinuosResultActivity", ignoreCase = true)
                ) {
                    mPath + Constants.COTINOUS + "/" + imageName
                } else {
                    mPath + Constants.SCANNER + "/" + imageName
                }
            }

            val imgFile = File(mPath.nullSafety())

            val destFile = File(
                Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.LPRSCANIMAGES
            )
            if (!destFile.exists()) {
                if (!destFile.mkdirs()) {
                    Log.e("getOutputPhotoFile", "Failed to create storage directory.")
                }
            }

            if (imgFile.exists()) {
                try {
                    mCopyLprScanPath = destFile.absolutePath + "/" + imageName
                    copyFileOrDirectory(imgFile.absolutePath, destFile.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val imgFileCopy = mCopyLprScanPath?.let { File(it) }
                if (imgFileCopy != null && imgFileCopy.exists()) {
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true)) {
                        mImageViewNumberPlate.setScaleType(ImageView.ScaleType.CENTER_CROP)
                    }

                    mImageViewNumberPlate.setImageURI(Uri.fromFile(imgFileCopy))
                    //save image to db
                    mainActivityViewModel.deleteTempImages()
                    mImageCount = scanResultScreenViewModel.getCountImages().nullSafety()
                    val id = SDF_ddHHmmss.format(Date())
                    val pathDb = Uri.fromFile(imgFileCopy).path
                    val mImage = CitationImagesModel()
                    mImage.citationImage = pathDb
                    mImage.id = id.toInt()
                    scanResultScreenViewModel.insertCitationImage(mImage)

                    //Can be done something here if required after image is loaded
                    if (bundle.containsKey(INTENT_KEY_VEHICLE_STICKER_URL)) {
                        //See if you have LPR image exist or not
                        val pairForLprImage = FileUtil.isLprImageExists(mLprNumber)
                        if (pairForLprImage.first) {
                            val idForLpr = SDF_ddHHmmss.format(Date())
                            val lprImage = CitationImagesModel()
                            lprImage.status = 0
                            lprImage.citationImage = pairForLprImage.second
                            lprImage.id = idForLpr.toInt() + Random().nextInt(1000)

                            scanResultScreenViewModel.insertCitationImage(lprImage)
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
                        val pairForVehicleStickerImage =
                            FileUtil.isVehicleStickerImageExists(mLprNumber)
                        if (pairForVehicleStickerImage.first) {
                            val idForVehicleSticker = SDF_ddHHmmss.format(Date())

                            val vehicleStickerImage = CitationImagesModel()
                            vehicleStickerImage.status = 0
                            vehicleStickerImage.citationImage = pairForVehicleStickerImage.second
                            vehicleStickerImage.id =
                                idForVehicleSticker.toInt() + Random().nextInt(1000)

                            scanResultScreenViewModel.insertCitationImage(vehicleStickerImage)
                        }
                    }
                }
            }

            if (bundle.getString("from_scr") != null && bundle.getString("from_scr") == "PAYBYSPACE") {
                mFromScreen = bundle.getString("from_scr")
                mLotItem = bundle.getString("Lot")
                mLotItem = bundle.getString("Location")
                mSpaceId = bundle.getString("Space_id")
                mMeterNameItem = bundle.getString("Meter")
                mPBCZone = bundle.getString("Zone")
                mStreetItem = bundle.getString("Street")
                mBlock = bundle.getString("Block")
                mDirectionItem = bundle.getString("Direction")
            }
            if (bundle.getString("from_scr") != null && bundle.getString("from_scr") == Constants.DIRECTED_ENFORCEMENT) {
                mFromScreen = bundle.getString("from_scr")
                mLotItem = bundle.getString("Lot")
                mLotItem = bundle.getString("Location")
                mSpaceId = bundle.getString("Space_id")
                mMeterNameItem = bundle.getString("Meter")
                mPBCZone = bundle.getString("Zone")
                mStreetItem = bundle.getString("Street")
                mBlock = bundle.getString("Block")
                mDirectionItem = bundle.getString("Direction")
                mState = bundle.getString("State")
                mTypeOfHit = bundle.getString("type_of_hit")
            } else {
                sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mDataSetTimeObject = scanResultScreenViewModel.getUpdateTimeResponse()

            setDropdownMakeVehicle("", "")
            setDropdownVehicleColour("")
        }

        linearLayoutCompatTimingIcon.setOnClickListener {
            if (mListTiming.isNullOrEmpty().nullSafety()) {
                //Can be uncomment later
//                mTimingRecordValue = splitDateLPR(mListTiming!![0].markIssueTimestamp.toString())
//                sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "true")
//                moveToNext("time")
            }
        }
        try {
            if (sharedPreference.read(SharedPrefKey.LOCK_GEO_ADDRESS, "").equals("unlock", true)) {
                appCompatImageViewLock.tag = "unlock"
                appCompatImageViewLock.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_baseline_lock_open_24
                    )
                )

                //Set ADA
                appCompatImageViewLock.contentDescription =
                    getString(R.string.ada_content_description_location_unlocked)
            } else {
                addressGeo = sharedPreference.read(SharedPrefKey.LOCK_GEO_SAVE_ADDRESS, "")
                appCompatImageViewLock.tag = "lock"
                appCompatImageViewLock.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_baseline_lock_24
                    )
                )

                //Set ADA
                appCompatImageViewLock.contentDescription =
                    getString(R.string.ada_content_description_location_locked)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!TextUtils.isEmpty(textViewLprNumber.text.toString())) {
            try {
                mLotItem = mWelcomeForm?.lot

                if (mLotItem.isNullOrEmpty()) {
                    mLotItem = sharedPreference.read(SharedPrefKey.SCAN_RESULT_LOT, "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            callAllTypeApi()
        }

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehColor,
            appCompatAutoCompleteTextView = mAutoComTextViewVehColor
        )
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehMake,
            appCompatAutoCompleteTextView = mAutoComTextViewMakeVeh
        )
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehModel,
            appCompatAutoCompleteTextView = mAutoComTextViewVehModel
        )
    }

    /**
     * Set vehicle sticker data to fields
     */
    private fun setVehicleStickerData() {
        viewLifecycleOwner.lifecycleScope.launch {
            mSelectedMakeFromVehicleSticker =
                mainActivityViewModel.getCarMakeListFromDataSet()?.firstOrNull {
                    it.makeText?.contains(
                        vehicleStickerInfo?.make.nullSafety(), ignoreCase = true
                    ) == true
                }?.makeText.nullSafety()

            mSelectedModel = mainActivityViewModel.getCarModelListFromDataSet()?.firstOrNull {
                it.model?.contains(
                    vehicleStickerInfo?.model.nullSafety(), ignoreCase = true
                ) == true
            }?.model.nullSafety()

            mBodyStyleItem = mainActivityViewModel.getCarBodyStyleListFromDataSet()?.firstOrNull {
                it.body_style?.contains(
                    vehicleStickerInfo?.bodyStyle.nullSafety(), ignoreCase = true
                ) == true
            }?.body_style.nullSafety()

            mSelectedVin = vehicleStickerInfo?.vin
            mVin = vehicleStickerInfo?.vin
            mState = STATE_NEW_YORK
        }

    }

    override fun onResume() {
        super.onResume()
        getGeoAddress()
        sharedPreference.write(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
    }

    private fun setCapField() {
        mAutoComTextViewMakeVeh.isAllCaps = true
        mAutoComTextViewVehModel.isAllCaps = true
        mAutoComTextViewVehColor.isAllCaps = true
    }

    private fun consumeResponse(
        newApiResponse: NewApiResponse<Any>,
        apiSubTagName: String? = null,
        type: String? = null,
        lprNumber: String? = null
    ) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(
                    context = requireContext(),
                    message = getString(R.string.loader_text_please_wait_we_are_loading_data)
                )
            }

            is NewApiResponse.Success -> {
                DialogUtil.hideLoader()

                logD("APICall==>","Success")
                logD("APICall==>","${apiSubTagName}")


                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "------------Response -----------------$tag"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "Response: " + ObjectMapperProvider.instance.writeValueAsString(
                                (newApiResponse.data as JsonNode)
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logD("get data lpr error top ", tag.nullSafety())
                }

                try {
                    when (newApiResponse.apiNameTag) {
                        API_TAG_NAME_GET_DATA_FROM_LPR -> {
                            when (apiSubTagName) {
                                API_SUB_TAG_NAME_GET_PAYMENT_DATA -> {
                                    handlePaymentDataResponse(newApiResponse.data as JsonNode)
                                }

                                API_SUB_TAG_NAME_GET_SCOFFLAW_DATA -> {
                                    handleScofflawDataResponse(newApiResponse.data as JsonNode)
                                }

                                API_SUB_TAG_NAME_GET_EXEMPT_DATA -> {
                                    handleExemptDataResponse(newApiResponse.data as JsonNode)
                                }

                                API_SUB_TAG_NAME_GET_STOLEN_DATA -> {
                                    handleStolenDataResponse(newApiResponse.data as JsonNode)
                                }

                                API_SUB_TAG_NAME_GET_CAMERA_RAW_FEED_DATA -> {
                                    handleCameraRawFeedDataResponse(newApiResponse.data as JsonNode)
                                }

                                API_SUB_TAG_NAME_GET_PERMIT_DATA -> {
                                    handlePermitDataResponse(newApiResponse.data as JsonNode)
                                }

                                API_SUB_TAG_NAME_GET_CITATION_DATA -> {
                                    handleCitationDataResponse(newApiResponse.data as JsonNode)
                                }

                                API_SUB_TAG_NAME_GET_CITATION_T2_DATA -> {
                                    handleCitationDataResponse(newApiResponse.data as JsonNode)
                                }

                                API_SUB_TAG_NAME_GET_MAKE_MODEL_COLOR_DATA -> {
                                    handleMakeModelColorDataResponse(newApiResponse.data as JsonNode)
                                }
                            }
                        }

                        API_TAG_NAME_LPR_SCAN_LOGGER -> {
                            handleLprScanLoggerDataResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_GET_TIMING_MARK -> {
                            handleGetTimingDataResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_GET_LAST_SECOND_CHECK -> {
                            handleLastSecondCheckDataResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_ADD_TIMING -> {
                            //Nothing to handle
                        }

                        API_TAG_NAME_CREATE_TICKET -> {
                            handleCreateTicketDataResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_INACTIVE_METER_BUZZER -> {
                            handleInActiveMeterBuzzer(newApiResponse.data as JsonNode)
                        }
                    }
                } catch (e: JsonMappingException) {
                    requireContext().toast(message = getString(R.string.error_desc_please_login_again_to_use_the_application))
                    viewLifecycleOwner.lifecycleScope.launch {
                        mainActivityViewModel.sendActionToMain(MainActivityAction.EventProcessLogout)
                    }
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is NewApiResponse.ApiError -> {
                DialogUtil.hideLoader()
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_api_error),
                    message = getString(
                        R.string.error_desc_api_error,
                        newApiResponse.code.toString(),
                        newApiResponse.getErrorMessage()
                            .nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )

                if (newApiResponse.apiNameTag == API_SUB_TAG_NAME_GET_PERMIT_DATA) {
                    retryPermitAPICall(
                        lprNumber.nullSafety(), type.nullSafety()
                    )
                }

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "------------Error Response -----------------$tag"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "ERROR : " + newApiResponse.getErrorMessage()
                                .nullSafety(getString(R.string.error_desc_something_went_wrong))
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "ERROR : " + newApiResponse.getErrorMessage()
                                .nullSafety(getString(R.string.error_desc_something_went_wrong))
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is NewApiResponse.NetworkError -> {
                DialogUtil.hideLoader()

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_network_error),
                    message = getString(
                        R.string.error_desc_network_error,
                        newApiResponse.exception.message.nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            is NewApiResponse.UnknownError -> {
                if (newApiResponse.apiNameTag == API_SUB_TAG_NAME_GET_PERMIT_DATA) {
                    retryPermitAPICall(
                        lprNumber.nullSafety(), type.nullSafety()
                    )
                }

                DialogUtil.hideLoader()

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_unknown_error),
                    message = getString(
                        R.string.error_desc_unknown_error,
                        newApiResponse.throwable.localizedMessage.nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    private fun handleCitationDataResponse(jsonNodeData: JsonNode) {
        val json = jsonNodeData.toString()

        try {
            citationResponse = ObjectMapperProvider.fromJson(json, CitationResponse::class.java)

            val status = citationResponse?.status
            val respObj = citationResponse?.dataCitation?.getOrNull(0)?.response
            val type = respObj?.type

            if (citationResponse != null && status != null && status && (type == "CitationData" || type == "CitationDataT2")) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mDataSetTimeObject = scanResultScreenViewModel.getUpdateTimeResponse()
                    citationDataLength = respObj.length ?: 0
                    mListCitation = respObj.results ?: ArrayList()
                    if (isFineSumVisible) {
                        mFineAmountSum = respObj.fineAmountSum.nullSafety(0.00)
                    }

                    val list = mListCitation ?: emptyList()
                    if (list.isNotEmpty()) {
                        mLayHistoryTextSection.visibility = View.VISIBLE
                        mRecyclerViewHistory.visibility = View.VISIBLE
                        mEmptyLayout.visibility = View.GONE
                        mTextViewTabHistory.text = getString(
                            R.string.scr_lbl_cite_history_with_value,
                            list.size.nullSafety().toString()
                        )
                        if (isFineSumVisible) {
                            appCompatTextViewFineAmountSum.visibility = View.VISIBLE
                            appCompatTextViewFineAmountSum.text = getString(
                                R.string.scr_lbl_fine_amount_sum_with_value,
                                mFineAmountSum.getFormattedAmount()
                            )
                        } else {
                            appCompatTextViewFineAmountSum.visibility = View.GONE
                        }
                        setAdapterForHistory(list)
                    } else {
                        mTextViewTabHistory.text =
                            getString(R.string.scr_lbl_cite_history_with_value, "0")
                        mRecyclerViewHistory.visibility = View.GONE
                        mEmptyLayout.visibility = View.VISIBLE
                        mLayHistoryTextSection.visibility = View.GONE
                    }
                    mTextViewTabHistory.contentDescription = getString(
                        R.string.ada_content_description_tab_unselected,
                        mTextViewTabHistory.text.toString()
                    )
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_lpr_data_api_response) + "Histroy",
                    message = citationResponse?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun handleMakeModelColorDataResponse(jsonNodeData: JsonNode) {
        val json = jsonNodeData.toString()

        try {
            modelColorResponse = ObjectMapperProvider.fromJson(json, MakeModelColorResponse::class.java)

            val status = modelColorResponse?.status
            val respObj = modelColorResponse?.dataMakeModelColor?.getOrNull(0)?.response

            if (modelColorResponse != null && status != null && status && respObj?.type == "MakeModelColorData") {
                viewLifecycleOwner.lifecycleScope.launch {
                    isMakeLoader = false
                    val results = respObj.results ?: emptyList()
                    mListMakeModelColor = results
                    modelColorLength = respObj.length ?: 0

                    if (results.isNotEmpty()) {
                        val first = results[0]
                        //withContext(Dispatchers.Main) {
                        setDropdownVehicleColour(first.color)
                        setDropdownMakeVehicle(first.make, first.model)
                        //}
                        mBodyStyleItem = first.bodyStyle
                        mSelectedModel = first.model
                        mSelectedVin = first.vinNumber
                    }

                    val lprText = textViewLprNumber.editableText.toString().trim()
                    if (lprText.isNotEmpty()) {
                        // run push event and timing on background to avoid blocking Main
                        //withContext(Dispatchers.Default) {
                        delay((eventLoggerAPIDelay * 1000).toLong())
                        try {
                            callPushEventApi(lpNumberForLoggerAPI!!)
                            if (mAutoTiming.equals("YES", ignoreCase = true)) {
                                callAddTimingApi()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //}
                    } else {
                        //withContext(Dispatchers.Main) {
                        requireContext().toast("Please Enter Lpr Number")
                        //}
                    }
                }
            } else {
                isMakeLoader = false
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_lpr_data_api_response) + "Make Model",
                    message = modelColorResponse?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun handlePaymentDataResponse(jsonNodeData: JsonNode) {
        try {
            paymentResponse =
                ObjectMapperProvider.fromJson(jsonNodeData.toString(), PaymentResponse::class.java)

            if (paymentResponse != null && paymentResponse?.status.nullSafety()) {
                val paymentRes = paymentResponse?.dataPayment?.firstOrNull()?.response
                if (paymentRes?.type == "PaymentData" || paymentRes?.type == "PaymentDataByZone" || paymentRes?.type == "AllPaymentsInZoneFuzzy") {
                    paymentLength = paymentRes.length
                    mListPayment = paymentRes.results
                    val platesArray = paymentRes.plates
                    if (sixActionButtonVisibilityCheck("HAS_PAYMENTS") && mListPayment?.isNotEmpty()
                            .nullSafety()
                    ) {
                        sharedPreference.write(
                            SharedPrefKey.PAY_BY_ZONE_SPACE,
                            mListPayment?.firstOrNull()?.mZoneName.nullSafety("")
                        )
                        isPayment = false
                        mLayPayment.visibility = View.VISIBLE
                        mLayHide.visibility = View.VISIBLE
                        mLayHide.setBackgroundResource(R.drawable.round_corner_shape_without_fill_green)
                        setAdapterForPayment(mListPayment)

                        if (!mListPayment.isNullOrEmpty().nullSafety()) {
                            val results = paymentRes.results.orEmpty()
                            //val hasExpired = results.any { it.mExpiryTimeStamp?.let { ts -> compareDates(ts) == 1 } == true }
                            val hasExpired = results.firstOrNull {
                                it.mExpiryTimeStamp?.let { ts ->
                                    compareDates(ts) == 1
                                } == true
                            } != null

                            if (hasExpired) {
                                isPayment = true
                                btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                                btnPayment.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.white
                                    )
                                )
                                mLayHide.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                            } else {
                                isPayment = false
                                btnPayment.text = getString(R.string.scr_btn_payment)
                                btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_green)
                                btnPayment.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.black
                                    )
                                )
                            }
                        }
                    } else {
                        isPayment = false
                        mLayPayment.visibility = View.GONE
                        mLayHide.visibility = View.GONE
                    }


                    if (!platesArray.isNullOrEmpty().nullSafety()) {
                        platesArray?.takeIf { it.isNotEmpty() }?.let { list ->
                            if (fuzzyPlates.isNotEmpty()) fuzzyPlates.append(", ")
                            fuzzyPlates.append(list.joinToString(", ") { it })
                        }

                        if (platesArray?.isNotEmpty().nullSafety() && !mListPayment.isNullOrEmpty()
                                .nullSafety()
                        ) {

                            if (mListPayment?.isEmpty().nullSafety()) {
                                btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                                btnPayment.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.black
                                    )
                                )

                                appCompatTextViewPaymentFuzzyPlate.visibility = View.VISIBLE
                            }
                            if (platesArray?.size.nullSafety() > 1) {
                                btnPayment.text = getString(R.string.scr_btn_payment)
                                appCompatTextViewPaymentFuzzyPlate.text = getString(
                                    R.string.scr_lbl_payment_for_similar_plates_with_value,
                                    fuzzyPlates.toString()
                                )
                            } else {
                                btnPayment.text = getString(R.string.scr_btn_payment)
                                appCompatTextViewPaymentFuzzyPlate.text = getString(
                                    R.string.scr_lbl_payment_for_similar_plate_with_value,
                                    fuzzyPlates.toString()
                                )
                            }
                        }
                    }
                } else if (paymentRes?.type == "PaymentDataFuzzy") {
                    paymentFuzzyLength = paymentRes.length
                    mListPaymentFuzzy = paymentRes.results

                    mListPaymentFuzzy?.takeIf { it.isNotEmpty() }?.let { list ->
                        val plates = list.mapNotNull { it.lpNumber }.joinToString(", ")
                        if (plates.isNotEmpty()) {
                            if (fuzzyPlates.isNotEmpty()) fuzzyPlates.append(", ")
                            fuzzyPlates.append(plates)
                        }
                    }

                    if (mListPaymentFuzzy?.isNotEmpty().nullSafety()) {
                        if (!mListPayment.isNullOrEmpty().nullSafety()) {
                            btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                            btnPayment.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(), R.color.black
                                )
                            )

                            appCompatTextViewPaymentFuzzyPlate.visibility = View.VISIBLE

                            if (mListPaymentFuzzy?.size.nullSafety() > 1) {
                                btnPayment.text = getString(R.string.scr_btn_payment)
                                appCompatTextViewPaymentFuzzyPlate.text = getString(
                                    R.string.scr_lbl_payment_for_similar_plates_with_value,
                                    fuzzyPlates.toString()
                                )
                            } else {
                                btnPayment.text = getString(R.string.scr_btn_payment)
                                appCompatTextViewPaymentFuzzyPlate.text = getString(
                                    R.string.scr_lbl_payment_for_similar_plate_with_value,
                                    fuzzyPlates.toString()
                                )
                            }
                        }
                    }
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.scr_lbl_lpr_payment),
                    message = paymentResponse?.response.nullSafety(getString(R.string.error_desc_something_went_wrong)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logD("get data lpr error ", "$tag paymentResponse")
        }
    }

    private fun handleScofflawDataResponse(jsonNodeData: JsonNode) {
        try {
            val resp = runCatching {
                ObjectMapperProvider.fromJson(jsonNodeData.toString(), ScofflawResponse::class.java)
            }.getOrNull()
            scofflawResponse = resp

            if (resp?.status == true) {
                val responseObj = resp.dataScofflaw?.firstOrNull()?.response
                val type = responseObj?.type
                if (type == "ScofflawData" || type == "ScofflawDataT2") {
                    scofflawDataLength = responseObj.length
                    mListScofflaw = responseObj.results ?: emptyList()

                    val hasResults = !mListScofflaw.isNullOrEmpty()
                    if (sixActionButtonVisibilityCheck("HAS_SCOFFLAW") && hasResults) {
                        mLayScofflaw.visibility = View.VISIBLE
                        mLayScofflaw.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)

                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true
                            )
                        ) {
                            setAdapterForScofflaw(mListScofflaw)
                            val firstType = mListScofflaw?.firstOrNull()?.type
                            val showStickers = Constants.stickerList.contains(firstType)
                            btnWhiteSticker.visibility =
                                if (showStickers) View.VISIBLE else View.GONE
                            btnRedSticker.visibility = if (showStickers) View.VISIBLE else View.GONE
                        } else {
                            mListScofflawFirstIndex =
                                mListScofflaw?.firstOrNull()?.let { listOf(it) } ?: emptyList()
                            setAdapterForScofflaw(mListScofflawFirstIndex)
                        }

                        btnScofflaw.text = getString(R.string.scr_btn_scofflaw)
                        btnScofflaw.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                        btnScofflaw.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.white
                            )
                        )
                    } else {
                        mLayScofflaw.visibility = View.GONE
                        btnScofflaw.text = getString(R.string.scr_btn_no_scofflaw)
                        btnScofflaw.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                        btnScofflaw.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.white
                            )
                        )
                    }
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_scofflaw_data_api_response),
                    message = resp?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logD("get data lpr error ", "$tag scofflawResponse")
        }
    }

    private fun handleExemptDataResponse(jsonNodeData: JsonNode) {
        try {
            val resp = runCatching {
                ObjectMapperProvider.fromJson(jsonNodeData.toString(), ExemptResponse::class.java)
            }.getOrNull()
            exemptResponse = resp

            if (resp?.status == true) {
                val responseObj = resp.dataExempt?.firstOrNull()?.response
                if (responseObj?.type == "ExemptData") {
                    exemptDataLength = responseObj.length
                    mListExempt = responseObj.results ?: emptyList()

                    val hasResults = !mListExempt.isNullOrEmpty()
                    if (sixActionButtonVisibilityCheck("HAS_EXEMPT") && hasResults) {
                        mLayExempt.visibility = View.VISIBLE
                        mLayExempt.invalidate()
                        btnExempt.visibility = View.VISIBLE
                        mLayExempt.setBackgroundResource(R.drawable.round_corner_shape_without_fill_green)
                        setAdapterForExempt(mListExempt)
                        btnExempt.text = getString(R.string.scr_btn_exempt)
                        btnExempt.setBackgroundResource(R.drawable.button_round_corner_shape_green)
                        btnExempt.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.black
                            )
                        )
                    } else {
                        mLayExempt.visibility = View.GONE
                        mLayExempt.invalidate()
                        btnExempt.text = getString(R.string.scr_btn_no_exempt)
                        btnExempt.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                        btnExempt.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.white
                            )
                        )
                    }
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_exempt_data_api_response),
                    message = resp?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logD("get data lpr error ", "$tag exemptResponse")
        }
    }

    private fun handleStolenDataResponse(jsonNodeData: JsonNode) {
        try {
            val resp = runCatching {
                ObjectMapperProvider.fromJson(jsonNodeData.toString(), StolenResponse::class.java)
            }.getOrNull()
            stolenResponse = resp

            if (resp?.status == true) {
                val responseObj = resp.dataScofflaw?.firstOrNull()?.response
                if (responseObj?.type == "StolenData") {
                    stolenDataLength = responseObj.length
                    val results = responseObj.results ?: emptyList()
                    mListStolen = results

                    if (sixActionButtonVisibilityCheck("HAS_STOLEN") && results.isNotEmpty()) {
                        mLayStolen.visibility = View.VISIBLE
                        mLayStolen.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                        setAdapterForStolen(results)
                        btnStolen.text = getString(R.string.scr_btn_stolen)
                        btnStolen.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                        btnStolen.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.white
                            )
                        )
                    } else {
                        mLayStolen.visibility = View.GONE
                        btnStolen.text = getString(R.string.scr_btn_no_stolen)
                        btnStolen.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                        btnStolen.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.white
                            )
                        )
                    }
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_stolen_data_api_response),
                    message = resp?.message.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logD("get data lpr error ", "$tag stolenResponse")
        }
    }

    private fun handleCameraRawFeedDataResponse(jsonNodeData: JsonNode) {
        try {
            val resp = runCatching {
                ObjectMapperProvider.fromJson(
                    jsonNodeData.toString(), CameraRawFeedDataResponse::class.java
                )
            }.getOrNull()

            val responseObj = resp?.data?.firstOrNull()?.response

            // If type mismatch or no response, stop and reset loader flag
            if (responseObj?.type != "CameraRawFeedData") {
                isMakeLoader = false
                return
            }

            if (resp.status.nullSafety()) {
                isMakeLoader = false
                val results = responseObj.results.orEmpty()

                if (results.isNotEmpty()) {
                    mLayCameraRawFeedData.visibility = View.VISIBLE
                    mLayCameraRawFeedData.invalidate()
                    btnCameraRawFeedData.visibility = View.VISIBLE
                    mLayCameraRawFeedData.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                    btnCameraRawFeedData.text = getString(R.string.scr_btn_no_camera_raw_feed_data)
                    btnCameraRawFeedData.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                    btnCameraRawFeedData.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        )
                    )

                    setAdapterForCameraRawFeedData(results)
                } else {
                    mLayCameraRawFeedData.visibility = View.GONE
                    mLayCameraRawFeedData.invalidate()
                    mLayCameraRawFeedData.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                    btnCameraRawFeedData.text = getString(R.string.scr_btn_no_camera_raw_feed_data)
                    btnCameraRawFeedData.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                    btnCameraRawFeedData.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.white
                        )
                    )
                }
            } else {
                isMakeLoader = false
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_camera_raw_feed_data_api_response),
                    message = modelColorResponse?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isMakeLoader = false
        }
    }

    private fun handleGetTimingDataResponse(jsonNodeData: JsonNode) {
        try {
            // parse safely
            timingMarkResponse = runCatching {
                ObjectMapperProvider.fromJson(
                    jsonNodeData.toString(), TimingMarkResponse::class.java
                )
            }.getOrNull()

            if (timingMarkResponse?.success != true) {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_timing_data_api_response),
                    message = timingMarkResponse?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
                return
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val data = timingMarkResponse?.data.orEmpty()

                if (data.isEmpty()) {
                    isTiming = false
                    mLayTiming.visibility = View.GONE
                    btnTimings.text = getString(R.string.scr_btn_no_timings)
                    btnTimings.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                    btnTimings.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    return@launch
                }

                try {
                    mListTiming = data
                    isTiming = false
                    mLayTiming.visibility = View.VISIBLE

                    val first = data.first()
                    timingId = first.id

                    val isExpiredOrViolation = first.let {
                        isTimingExpired(
                            it.markStartTimestamp.nullSafety(), it.regulationTime?.toFloat() ?: 0f
                        ) || it.isViolation == true
                    }

                    if (isExpiredOrViolation) {
                        isTiming = true
                        btnTimings.text = getString(R.string.scr_btn_timings)
                        btnTimings.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                        btnTimings.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.white
                            )
                        )
                        mLayTiming.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                    } else {
                        isTiming = false
                        btnTimings.text = getString(R.string.scr_btn_timings)
                        btnTimings.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                        btnTimings.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.white
                            )
                        )
                        mLayTiming.setBackgroundResource(R.drawable.round_corner_shape_without_fill_orange_)
                    }

                    mState = first.lpState
                    setAdapterForTiming(mListTiming)

                    // Vendor specific coloring/alerts
                    if (first.vendorName.equals("Vigilant", ignoreCase = true)) {
                        if (first.isViolation.nullSafety()) {
                            when (first.alertType) {
                                "Unauthorized Vehicle", "Expired Parking" -> set6ButtonColor(
                                    mLayTiming, btnTimings, 1
                                )

                                "ScofflawAlert" -> set6ButtonColor(mLayScofflaw, btnScofflaw, 1)
                                "Expired Parking;ScofflawAlert", "Unauthorized Vehicle;ScofflawAlert" -> {
                                    set6ButtonColor(mLayTiming, btnTimings, 1)
                                    set6ButtonColor(mLayScofflaw, btnScofflaw, 1)
                                }

                                else -> set6ButtonColor(mLayTiming, btnTimings, 1)
                            }
                        } else {
                            set6ButtonColor(mLayTiming, btnTimings, 0)
                        }
                    }

                    // Abandon vehicle condition
                    if (first.isAbandonVehicle == true) {
                        val mAddTimingRequest = AddTimingRequest().apply {
                            lprState = first.lpState
                            lprNumber = first.lpNumber
                        }
                        val bundle = Bundle().also {
                            it.putString("timeData", ObjectMapperProvider.toJson(mAddTimingRequest))
                        }
                        nav.safeNavigate(R.id.abandonedVehicleScreenFragment, bundle)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logD("get data lpr error ", "$tag timingMarkResponse")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logD("get data lpr error ", "$tag timingMarkResponse")
        }
    }


    private fun handleLprScanLoggerDataResponse(jsonNodeData: JsonNode) {
        val responseModel = ObjectMapperProvider.fromJson(
            jsonNodeData.toString(), LprScanLoggerResponse::class.java
        )

        if (responseModel.success.nullSafety()) {
            //Nothing to handle
        } else {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_lpr_scan_logger_api_response),
                message = responseModel.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }

    private fun handleInActiveMeterBuzzer(jsonNodeData: JsonNode) {
        val responseModel = ObjectMapperProvider.fromJson(
            jsonNodeData.toString(), InactiveMeterBuzzerResponse::class.java
        )

        if (responseModel.status.nullSafety()) {
            if (responseModel.data?.inactive.nullSafety()) {
                sharedPreference.write(SharedPrefKey.IS_METER_ACTIVE, "ACTIVE")
                ToneGenerator(
                    AudioManager.STREAM_MUSIC, 100
                ).startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1000)
                val vibrator =
                    requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        500, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
                btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_green)
                btnPayment.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            } else {
                sharedPreference.write(SharedPrefKey.IS_METER_ACTIVE, "DEACTIVE")
            }
        } else {
            sharedPreference.write(SharedPrefKey.IS_METER_ACTIVE, "DEACTIVE")
        }
    }

    private fun handleLastSecondCheckDataResponse(jsonNodeData: JsonNode) {
        val responseLastSecondCheckModel = ObjectMapperProvider.fromJson(
            jsonNodeData.toString(), LastSecondCheckResponse::class.java
        )

        if (responseLastSecondCheckModel.isStatus.nullSafety()) {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.scr_btn_no_payment),
                message = responseLastSecondCheckModel.data?.lastSecondCheck?.message.nullSafety(),
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }

    private fun handleCreateTicketDataResponse(jsonNodeData: JsonNode) {
        val responseModel =
            ObjectMapperProvider.fromJson(jsonNodeData.toString(), CreateTicketResponse::class.java)

        try {
            if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                ApiLogsClass.writeApiPayloadTex(
                    requireContext(),
                    "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(
                        responseModel
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logD("get data lpr error ", "$tag POST_CREATE_TICKET")
        }
        if (responseModel.success.nullSafety()) {
            try {
                viewLifecycleOwner.lifecycleScope.launch {
                    scanResultScreenViewModel.updateCitationBooklet(
                        1, mTicketNumber?.firstOrNull()?.citationBooklet
                    )
                }

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.scr_lbl_auto_warning_ticket_response),
                    message = getString(R.string.scr_lbl_auto_warning_success),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                logD("get data lpr error ", tag.nullSafety())
            }
        } else {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_citation_api_response),
                message = responseModel.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }

    private fun handlePermitDataResponse(jsonNodeData: JsonNode) {
        try {
            val resp = runCatching {
                ObjectMapperProvider.fromJson(jsonNodeData.toString(), PermitResponse::class.java)
            }.getOrNull()

            // If API not successful show dialog and stop
            if (resp?.status != true) {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_permit_data_api_response),
                    message = resp?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
                permitResponse = resp
                return
            }

            permitResponse = resp

            val responseObj = resp.dataPermit?.firstOrNull()?.response
            val type = responseObj?.type

            when (type) {
                "PermitData", "PermitDataT2", "PermitDataV2", "PermitDataByZone" -> {
                    permitDataLength = responseObj.length
                    val results = responseObj.results.orEmpty()
                    mListPermit = results

                    if (sixActionButtonVisibilityCheck("HAS_PERMIT") && results.isNotEmpty()) {
                        mLayPermit.visibility = View.VISIBLE
                        mLayPermit.setBackgroundResource(R.drawable.round_corner_shape_without_fill_green)
                        setAdapterForPermit(results)
                        btnPermit.text = getString(R.string.scr_btn_permit)
                        btnPermit.setBackgroundResource(R.drawable.button_round_corner_shape_green)
                        btnPermit.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.black
                            )
                        )
                    } else {
                        mLayPermit.visibility = View.GONE
                        btnPermit.text = getString(R.string.scr_btn_no_permit)
                    }

                    // If first result is a violation, override to red
                    if (results.firstOrNull()?.isViolation == true) {
                        mLayPermit.visibility = View.VISIBLE
                        mLayPermit.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
                        btnPermit.text = getString(R.string.scr_btn_permit)
                        btnPermit.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                        btnPermit.setTextColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.black
                            )
                        )
                    }
                }

                "PermitDataFuzzy" -> {
                    // reuse parsed response rather than parsing again
                    permitFuzzyResponse = resp
                    val fuzzyResults = responseObj.results.orEmpty()
                    permitDataFuzzyLength = responseObj.length

                    // build comma separated plates
                    fuzzyPlates.clear()
                    if (fuzzyResults.isNotEmpty()) {
                        fuzzyPlates.append(fuzzyResults.joinToString(", ") { it.lpNumber.toString() })

                        // only show fuzzy info if there are no exact permits
                        if (mListPermit.isNullOrEmpty()) {
                            btnPermit.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                            btnPermit.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(), R.color.black
                                )
                            )
                            appCompatTextViewPaymentFuzzyPlate.visibility = View.VISIBLE

                            appCompatTextViewPaymentFuzzyPlate.post {
                                btnPermit.text = getString(R.string.scr_btn_permit)
                                val label = if (fuzzyResults.size > 1) getString(
                                    R.string.scr_lbl_permit_for_similar_plates_with_value,
                                    fuzzyPlates.toString()
                                )
                                else getString(
                                    R.string.scr_lbl_permit_for_similar_plate_with_value,
                                    fuzzyPlates.toString()
                                )

                                appCompatTextViewPaymentFuzzyPlate.text = label
                            }
                        }
                    }
                }

                else -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_permit_data_api_response),
                        message = resp.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Throws(IOException::class)
    private fun getGeoAddress() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (appCompatImageViewLock.tag.equals("unlock")) {
                    val (lat, lng) = if (geoLat > 0 && geoLon > 0) {
                        geoLat to geoLon
                    } else {
                        val savedLat = sharedPreference.read(SharedPrefKey.LAT, "0.0").nullSafety()
                            .toDoubleOrNull() ?: 0.0
                        val savedLlg = sharedPreference.read(SharedPrefKey.LONG, "0.0").nullSafety()
                            .toDoubleOrNull() ?: 0.0
                        savedLat to savedLlg
                    }

                    addressGeo = getAddressFromLatLng(requireContext(), lat, lng)
                    separated = addressGeo.nullSafety().split(",").toTypedArray()
                    separatedEventLogger = addressGeo.nullSafety().split(",").toTypedArray()
                    val printAddress = separated?.firstOrNull()
                    mTvReverseCoded.text = printAddress.nullSafety()
                } else {
                    addressGeo = sharedPreference.read(SharedPrefKey.LOCK_GEO_SAVE_ADDRESS, "")
                    separated = addressGeo.nullSafety().split(",").toTypedArray()
                    val printAddress = separated?.firstOrNull()?.replace("#", " ")
                    separatedEventLogger = separated?.firstOrNull()?.split("#")?.toTypedArray()
                    if (printAddress?.isEmpty().nullSafety()) {
                        shadAddressIsEmpty()
                    } else {
                        mTvReverseCoded.text =
                            if (!printAddress.equals("null")) printAddress?.removePrefix(
                                "null"
                            ) else ""
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun shadAddressIsEmpty() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val (lat, lng) = if (geoLat > 0 && geoLon > 0) {
                    geoLat to geoLon
                } else {
                    val savedLat = sharedPreference.read(SharedPrefKey.LAT, "0.0").nullSafety()
                        .toDoubleOrNull() ?: 0.0
                    val savedLlg = sharedPreference.read(SharedPrefKey.LONG, "0.0").nullSafety()
                        .toDoubleOrNull() ?: 0.0
                    savedLat to savedLlg
                }

                val addressLine = getAddressFromLatLng(requireContext(), lat, lng)
                separated = addressLine.nullSafety().split(",").toTypedArray()
                val printAddress = separated?.firstOrNull()

                separatedEventLogger = separated
                mTvReverseCoded.text = printAddress.nullSafety()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setAdapterForHistory(mResList: List<CitationDataResponse>?) {
        val items = mResList.orEmpty()

        val listener = object : HistoryAdapter.ListItemSelectListener {
            override fun onItemClick(
                rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int
            ) {
                val item = items.getOrNull(position) ?: return

                // block clicks for specific flavors
                if (isFlavorForBlockCitationHistoryItemClick()) return

                try {
                    // Warning / prior-warning handling
                    if (item.ticketType == "Warning" && isFlavorForPriorWarningHandling()) {
                        val date = item.citationIssueTimestamp?.split(Regex("T"))?.getOrNull(0)
                        val formatted = date?.let { AppUtils.dateFormateWithSlash(it) } ?: ""
                        mTimingTireStem = "Prior Warning No. ${item.ticketNo} on $formatted"
                    } else if (item.ticketType == "Warning" && isFlavorForFinalWarningHandling() && item.code == "EMPLOYEE PARKING VIOLATION") {
                        val prefix = when (items.size) {
                            1 -> "1st "
                            2 -> "2nd "
                            3 -> "3rd & Final Warning - Subject to tow, "
                            else -> "Final Warning - Subject to tow, "
                        }
                        val date = item.citationIssueTimestamp?.split(Regex("T"))?.getOrNull(0)
                        val formatted = date?.let { AppUtils.dateFormateWithSlash(it) } ?: ""
                        mTimingTireStem = "$prefix Prior Warning No. ${item.ticketNo} on $formatted"
                    }

                    // Location / notes handling per flavor
                    if (isFlavorForNotAssigningStreetBlockSideFromCitationHistoryItemClick()) {
                        mStreetItem = ""
                        mBlock = ""
                        mSideItem = ""
                        mNoteItem = item.commentDetails?.note_1.orEmpty()
                        mNoteItem1 = item.commentDetails?.note_2.orEmpty()
                    } else {
                        mStreetItem = item.location?.street.orEmpty()
                        mBlock = item.location?.block.orEmpty()
                        mSideItem = item.location?.side.orEmpty()
                        mNoteItem = item.commentDetails?.note_1.orEmpty()
                        mNoteItem1 = item.commentDetails?.note_2.orEmpty()
                    }

                    mEscalatedLprNumber = item.vehicleDetails?.lprNo.orEmpty()
                    mEscalatedState = item.vehicleDetails?.state.orEmpty()
                    mSpaceId = item.location?.spaceId.orEmpty()
                    mMeterNameItem = item.location?.meter.orEmpty()
                    mPrintQuery = item.printQuery.orEmpty()
                    mTimingClick = 2

                    if (isFlavorForCitationHistoryPrinter()) {
                        try {
                            if (PermissionUtils.requestCameraAndStoragePermission(requireActivity())) {
                                if (item.images?.isNotEmpty().nullSafety()) {
                                    sharedPreference.write(
                                        SharedPrefKey.CITATION_NUMBER_FOR_PRINT, item.ticketNo
                                    )
                                    val bundle = Bundle().apply {
                                        putString("lpr_number", item.lpNumber)
                                        putString("printerQuery", item.printQuery)
                                        putString("State", "")
                                        putString("citationNumber", item.ticketNo)
                                        val printBitmapUrl = item.images?.firstOrNull {
                                            it.contains(FILE_NAME_FACSIMILE_PRINT_BITMAP)
                                        }
                                        putString("print_bitmap", printBitmapUrl)
                                    }
                                    nav.safeNavigate(
                                        R.id.citationHistoryPrinterScreenFragment, bundle
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        moveToNext("time")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        mHistoryAdapter = HistoryAdapter(requireContext(), items, "ResultScreen", listener)

        mRecyclerViewHistory.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            }
            adapter = mHistoryAdapter
        }

        citationHistoryCount = items.size
    }

    private fun setAdapterForPayment(mResList: List<PaymentDataResponse>?) {
        val items = mResList.orEmpty()
        if (items.isEmpty()) return

        items.lastOrNull()?.let { last ->
            mWelcomeForm?.paymentZoneName = last.mZoneName
            mWelcomeForm?.paymentZoneID = last.zoneId
        }

        val listener = object : PaymentAdapter.ListItemSelectListener {
            override fun onItemClick(
                rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int
            ) { /* no-op */
            }
        }
        val adapter = PaymentAdapter(requireContext(), items, listener)

        mRecyclerViewPayment.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            }
            this.adapter = adapter
        }
    }

    private fun setAdapterForPermit(mResList: List<PermitDataResponse>?) {
        val items = mResList.orEmpty()
        val listener = object : PermitAdapter.ListItemSelectListener {
            override fun onItemClick(
                rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int
            ) {
                // no-op
            }
        }
        val adapter = PermitAdapter(requireContext(), items, listener)

        mRecyclerViewPermit.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            }
            this.adapter = adapter
        }
    }

    private fun setAdapterForScofflaw(mResList: List<ScofflawDataResponse>?) {
        val items = mResList.orEmpty()
        val localAdapter = ScofflawAdapter(
            requireContext(), items, object : ScofflawAdapter.ListItemSelectListener {
                override fun onItemClick(
                    rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int
                ) { /* no-op */
                }
            })

        mRecyclerViewScofflaw.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            }
            adapter = localAdapter
        }

        if (isFlavorForRedirectToScofflaw()) {
            val bundle = Bundle().apply {
                putParcelable("SCCOFFLAW", items.firstOrNull())
                putString("MAKE", mAutoComTextViewMakeVeh.text?.toString().orEmpty())
                putString("MODEL", mAutoComTextViewVehModel.text?.toString().orEmpty())
                putString("COLOR", mAutoComTextViewVehColor.text?.toString().orEmpty())
                putString("LPNUMBER", textViewLprNumber.text?.toString().orEmpty())
            }
            nav.safeNavigate(R.id.bootScreenFragment, bundle)
        }
    }

    private fun setAdapterForStolen(mResList: List<StolenDataResponse>?) {
        val items = mResList.orEmpty()
        val listener = object : StolenAdapter.ListItemSelectListener {
            override fun onItemClick(
                rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int
            ) { /* no-op */
            }
        }
        val adapter = StolenAdapter(requireContext(), items, listener)

        mRecyclerViewStolen.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            }
            this.adapter = adapter
        }
    }

    private fun setAdapterForExempt(mResList: List<ExemptDataResponse>?) {
        val items = mResList.orEmpty()
        val listener = object : ExemptAdapter.ListItemSelectListener {
            override fun onItemClick(
                rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int
            ) {
            }
        }

        val adapter = ExemptAdapter(requireContext(), items, listener)

        mRecyclerViewExempt.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            }
            this.adapter = adapter
        }
    }

    private fun setAdapterForCameraRawFeedData(mResList: List<ResultsItemCameraRaw>?) {
        val mCameraRawFeedDataAdapter = CameraRawFeedDataAdapter(
            requireContext(), mResList, object : CameraRawFeedDataAdapter.ListItemSelectListener {
                override fun onItemClick(
                    rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int
                ) {
                    val item = mResList?.getOrNull(position) ?: return
                    try {
                        val inTime = item.inCarImageTimestamp?.let {
                            "vehicle in time ${AppUtils.formatDateTimeForCameraRaw(it)}"
                        } ?: ""

                        ImageCache.base64Images.clear()
                        listOf(
                            item.outCarImage, item.outPlateImage, item.inCarImage, item.inPlateImage
                        ).filter { !it.isNullOrBlank() && it.length > 100 }
                            .forEach { ImageCache.base64Images.add(it.nullSafety()) }

                        mStreetItem = item.street
                        mBlock = item.block.toString().nullSafety()
                        mSideItem = item.side.toString().nullSafety()
                        mDirectionItem = item.side.toString().nullSafety()
                        mLotItem = item.lot.toString().nullSafety()
                        mMeterNameItem = item.meter.toString().nullSafety()
                        mSelectedMake = item.make.toString().nullSafety()
                        mSelectedModel = item.model.toString().nullSafety()

                        mAutoComTextViewVehColor.setText(item.color.toString().nullSafety())
                        mAutoComTextViewMakeVeh.setText(item.make.toString().nullSafety())
                        mAutoComTextViewVehModel.setText(item.model.toString().nullSafety())

                        mBodyStyleItem = item.bodyStyle.toString().nullSafety()
                        mViolationCode = item.violationNumber.toString().nullSafety()
                        mTimingRecordRemarkValueCameraRawFeed = inTime
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    moveToNext("time")
                }
            })

        mRecyclerViewCameraRawFeedData.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = mCameraRawFeedDataAdapter
        }
    }

    private fun setAdapterForTiming(mResList: List<TimingMarkData>?) {
        val mTimingAdapter = TimingAdapter(
            requireContext(),
            mResList,
            "LprResultScreen",
            isTiming,
            isTireStemWithImageView,
            object : TimingAdapter.ListItemSelectListener {
                override fun onItemClick(markData: TimingMarkData?) {
                    if (!isTiming || markData == null) return

                    // Remark value (auto-fill if setting enabled)
                    mTimingRecordRemarkValue =
                        if (checkBuildConfigForTimingDataClickOnScanResultScreenForAutoFillRemark() || getSettingFileValuesForRemarkAutoFilledWithElapsedTime()) {
                            val timestamp =
                                markData.firstObservedTimestamp ?: markData.markStartTimestamp
                            val datePart =
                                if (markData.vendorName.equals("Vigilant", ignoreCase = true)) {
                                    splitDateLPRTime(timestamp.toString())
                                } else {
                                    splitDateLPRTime(markData.markStartTimestamp.toString())
                                }
                            "$datePart elapsed: ${
                                AppUtils.isElapsTime(
                                    timestamp!!, markData.regulationTime!!, requireContext()
                                )
                            }"
                        } else {
                            splitDateLPRTime(markData.markStartTimestamp.toString())
                        }

                    // Tire stem from first timing item if present
                    mListTiming?.firstOrNull()?.let { first ->
                        val front = first.tireStemFront?.toLongOrNull() ?: 0L
                        val back = first.tireStemBack?.toLongOrNull() ?: 0L
                        val aFormattedFront = formatter.format(front)
                        val aFormattedBack = formatter.format(back)
                        mTimingTireStem = if (aFormattedFront != "00") {
                            "Tire Stem :${aFormattedFront}/${aFormattedBack}"
                        } else {
                            ""
                        }
                    } ?: run {
                        mTimingTireStem = ""
                    }

                    // Toggle enforcement flag per flavor
                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true
                        )
                    ) {
                        sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "false")
                    } else {
                        sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "true")
                    }

                    // Branding-specific override for remark/tireStem
                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
                        ) || (BuildConfig.FLAVOR.equals(
                            DuncanBrandingApp13(), true
                        ) && !BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true
                        ) && !BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_KENOSHA, ignoreCase = true
                        )) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true
                        )
                    ) {
                        mTimingRecordRemarkValue = markData.remark
                        mTimingTireStem = markData.remark2
                    }

                    // Populate fragment fields from selected timing record
                    mStreetItem = markData.street
                    mBlock = markData.block
                    mSideItem = markData.side
                    mDirectionItem = markData.side
                    timingId = markData.id
                    mState = markData.lpState
                    mVin = markData.vinNumber
                    mVendorName = markData.vendorName

                    if (!markData.lot.isNullOrEmpty() && mLotItem.isNullOrEmpty()) {
                        mLotItem = markData.lot
                    }

                    mViolation = "YES"

                    markData.images?.let { imgs ->
                        mTimingImages = imgs.toMutableList()
                        if (imgs.isNotEmpty() && imgs.first().length > 100) {
                            ImageCache.base64Images.addAll(imgs)
                        }
                    }

                    sharedPreference.write(
                        SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME,
                        AppUtils.observedTime(markData.markIssueTimestamp.nullSafety())
                    )

                    overTimeParkingForTicketDetailsCharleston(markData)
                    mTimingClick = 1
                    moveToNext("time")
                }
            })

        mRecyclerViewTiming.isNestedScrollingEnabled = false
        mRecyclerViewTiming.setHasFixedSize(true)
        mRecyclerViewTiming.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        mRecyclerViewTiming.adapter = mTimingAdapter

        // Ensure timing images set from first item if available
        mResList?.firstOrNull()?.images?.let { imgs ->
            if (imgs.isNotEmpty()) mTimingImages = imgs.toMutableList()
        }
    }

    //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?, isAutoSelect: Boolean?) {
        if (linearLayoutCompatModel.visibility != View.VISIBLE) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // clear existing list
                mModelList.clear()

                // fetch and filter off the main thread
                val appList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getCarModelListFromDataSet() ?: emptyList()
                }

                val filtered = appList.filter { it.make == mSelectedMake }

                // populate mModelList safely
                filtered.forEach { item ->
                    val ds = DatasetResponse().apply {
                        model = item.model
                        make = item.make
                        makeText = item.makeText
                    }
                    mModelList.add(ds)
                }

                if (mModelList.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        try {
                            mAutoComTextViewVehModel.setText("")
                            mAutoComTextViewVehModel.setAdapter(null)
                            adapterModel?.notifyDataSetChanged()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    return@launch
                }

                val dropdown = mModelList.map { it.model.orEmpty() }.toTypedArray()

                val pos = if (!value.isNullOrBlank()) {
                    val byMake = mModelList.indexOfFirst { it.make.equals(value, true) }
                    if (byMake >= 0) byMake else mModelList.indexOfFirst {
                        it.model.equals(
                            value, true
                        )
                    }
                } else -1

                withContext(Dispatchers.Main) {
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)) {
                        mAutoComTextViewVehModel.setText(value)
                        mSelectedModel = value
                    } else if (pos >= 0 && isAutoSelect == true) {
                        mAutoComTextViewVehModel.setText(dropdown[pos])
                        mSelectedModel = mModelList.getOrNull(pos)?.model
                    }

                    adapterModel = ArrayAdapter(
                        requireContext(), R.layout.row_dropdown_lpr_details_item, dropdown
                    )
                    mAutoComTextViewVehModel.threshold = 1
                    mAutoComTextViewVehModel.setAdapter(adapterModel)
                    mAutoComTextViewVehModel.onItemClickListener =
                        OnItemClickListener { _, _, position, _ ->
                            mSelectedModel = mModelList.getOrNull(position)?.model
                            requireActivity().hideSoftKeyboard()
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle(value: String?, valueModel: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val mApplicationList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getCarMakeListFromDataSet()
            } ?: return@launch

            if (mApplicationList.isEmpty()) return@launch

            // populate uniqueDataSet once on background thread
            if (uniqueDataSet.isEmpty()) {
                val items = mApplicationList.map {
                    "${it.make.nullSafety()}#${it.makeText.nullSafety()}"
                }
                uniqueDataSet.addAll(items)
            }

            // build sorted list (sorted by display text)
            val geeks =
                uniqueDataSet.map { it }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) {
                    it.split(
                        "#", limit = 2
                    )[1]
                })

            // prepare dropdown entries and find preferred position
            val dropdown = geeks.map { it.split("#", limit = 2)[1] }.toTypedArray()
            val pos = if (!value.isNullOrBlank()) {
                geeks.indexOfFirst {
                    val parts = it.split("#", limit = 2)
                    parts[0].equals(value, true) || parts[1].equals(value, true)
                }
            } else -1

            // update UI
            mAutoComTextViewMakeVeh.post {
                if (pos >= 0 && pos < geeks.size) {
                    val parts = geeks[pos].split("#", limit = 2)
                    val makeText = parts[1]
                    mAutoComTextViewMakeVeh.setText(makeText)
                    mSelectedMake = parts[0]
                    mSelectedMakeValue = parts[1]
                    setDropdownVehicleModel(valueModel, true)
                }

                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_lpr_details_item, dropdown)
                mAutoComTextViewMakeVeh.threshold = 1
                mAutoComTextViewMakeVeh.setAdapter(adapter)

                mAutoComTextViewMakeVeh.onItemClickListener =
                    OnItemClickListener { parent, _, position, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            val selectedText = parent.getItemAtPosition(position).toString()
                            val index =
                                mApplicationList.getIndexOfMakeText(selectedText).coerceAtLeast(0)
                            val selectedItem = mApplicationList.getOrNull(index)
                            mSelectedMake = selectedItem?.make.nullSafety()
                            mSelectedMakeValue = selectedItem?.makeText
                            requireActivity().hideSoftKeyboard()
                            setDropdownVehicleModel(mSelectedMake.ifEmpty { "" }, false)
                        }
                    }
            }
        }
    }


    //set value to vehicle colour dropdown
    private fun setDropdownVehicleColour(value: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val colors = withContext(Dispatchers.Default) {
                    mainActivityViewModel.getCarColorListFromDataSet().orEmpty()
                        .sortedBy { it.description.nullSafety().lowercase(Locale.getDefault()) }
                }

                if (colors.isEmpty()) return@launch

                val dropdown = colors.map { it.description.nullSafety() }.toTypedArray()

                val preferredIndex = if (value.isNullOrBlank()) {
                    -1
                } else {
                    colors.indexOfFirst { ds ->
                        val desc = ds.description.nullSafety()
                        val code = ds.color_code.nullSafety()
                        desc.equals(value, ignoreCase = true) || code.equals(
                            value, ignoreCase = true
                        )
                    }
                }

                withContext(Dispatchers.Main) {
                    try {
                        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)) {
                            mAutoComTextViewVehColor.setText(value.nullSafety())
                            mSelectedColor = value
                        } else if (preferredIndex >= 0) {
                            mAutoComTextViewVehColor.setText(dropdown[preferredIndex])
                            mSelectedColor = dropdown[preferredIndex]
                        }

                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_lpr_details_item, dropdown
                        )
                        mAutoComTextViewVehColor.threshold = 1
                        mAutoComTextViewVehColor.setAdapter(adapter)

                        mAutoComTextViewVehColor.onItemClickListener =
                            OnItemClickListener { parent, _, position, _ ->
                                requireActivity().hideSoftKeyboard()
                                mSelectedColor = parent.getItemAtPosition(position).toString()
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setDropdownLot() {
        requireActivity().hideSoftKeyboard()
        viewLifecycleOwner.lifecycleScope.launch {
            val appList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getLotListFromDataSet() ?: emptyList()
            }

            if (appList.isEmpty()) return@launch

            val dropdown = appList.map { it.location.orEmpty() }.toTypedArray()
            val prefer =
                mWelcomeForm?.lot ?: sharedPreference.read(SharedPrefKey.SCAN_RESULT_LOT, "")
            val pos = dropdown.indexOfFirst { it.equals(prefer, ignoreCase = true) }

            mAutoComTextViewLot.post {
                if (pos >= 0) {
                    // safe access
                    dropdown.getOrNull(pos)?.let { text ->
                        mAutoComTextViewLot.setText(text)
                        mLotItem = appList.getOrNull(pos)?.lot.orEmpty()
                    }
                }

                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdown)
                try {
                    mAutoComTextViewLot.threshold = 1
                    mAutoComTextViewLot.setAdapter(adapter)

                    mAutoComTextViewLot.setOnItemClickListener { parent, _, position, _ ->
                        val selected = parent.getItemAtPosition(position).toString()
                        val index = appList.getIndexOfLocation(selected).coerceAtLeast(0)
                        mLotItem = appList.getOrNull(index)?.lot.orEmpty()
                        sharedPreference.write(SharedPrefKey.SCAN_RESULT_LOT, mLotItem)
                        requireActivity().hideSoftKeyboard()

                        viewLifecycleOwner.lifecycleScope.launch {
                            isMakeLoader = true
                            btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
                            btnPayment.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(), R.color.white
                                )
                            )

                            if (linearLayoutCompatZone.isVisible) {
                                try {
                                    val lp = textViewLprNumber.editableText.toString().trim()
                                    if (mPaymentByZone.equals("YES")) {
                                        withContext(Dispatchers.Default) {
                                            callGetDataFromLprApiForPayment(lp, "PaymentDataByZone")
                                        }
                                    }
                                    if (mAllPaymentsInZoneFuzzy.equals("YES")) {
                                        withContext(Dispatchers.Default) {
                                            callGetDataFromLprApiForPayment(
                                                lp, "AllPaymentsInZoneFuzzy"
                                            )
                                        }
                                    }
                                    if (mPermitByZone.equals("YES")) {
                                        withContext(Dispatchers.Default) {
                                            callGetDataFromLprApiForPermit(lp, "PermitDataByZone")
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }

                    if (mAutoComTextViewLot.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                        mAutoComTextViewLot.setListOnlyDropDown(
                            context = requireContext(), textInputLayout = textInputLayoutVehLot
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    /* Call Api to get DataFromLpr */
    private fun callGetDataFromLprApi(mLprNumber: String, mType: String, apiSubTagName: String?) {
        if (isInternetAvailable(requireContext())) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0").nullSafety().toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0").nullSafety().toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            //            mDataFromLprRequest.setLocation(mDataFromLprLocation);
            mDataFromLprRequest.page = 1.toLong()
            if (mType.equals(
                    "PermitDataByZone", ignoreCase = true
                ) && mLotItem != null && mAutoComTextViewLot.text.isNotEmpty()
            ) {
                mDataFromLprRequest.zone = mLotItem
            }
            if (mType.equals(
                    "PaymentDataByZone", ignoreCase = true
                ) && mLotItem != null && mAutoComTextViewLot.text.isNotEmpty()
            ) {
                mDataFromLprRequest.zone = mLotItem
            }
            /**
             * for sanibal Varun remove empty check
             */
//                if (mType.equals("AllPaymentsInZoneFuzzy", ignoreCase = true) && mLotItem != null&&
//                    mAutoComTextViewLot!=null && mAutoComTextViewLot.text!!.isNotEmpty()) {
            if (mType.equals("AllPaymentsInZoneFuzzy", ignoreCase = true)) {
                mDataFromLprRequest.zone = mLotItem.nullSafety()
            }

            when(apiSubTagName){
                API_SUB_TAG_NAME_GET_CITATION_DATA -> {
                    scanResultScreenViewModel.getCitationDataFromLprAPI(mDataFromLprRequest, mType, mLprNumber)
                }

                API_SUB_TAG_NAME_GET_CITATION_T2_DATA -> {
                    scanResultScreenViewModel.getCitationDataT2FromLprAPI(mDataFromLprRequest, mType, mLprNumber)
                }

                API_SUB_TAG_NAME_GET_MAKE_MODEL_COLOR_DATA -> {
                    scanResultScreenViewModel.getMakeModelColorDataFromLprAPI(mDataFromLprRequest, mType, mLprNumber)
                }
            }


            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "------------Get Data LPR API-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling all permit related query separately
     */
    private fun callGetDataFromLprApiForPermit(mLprNumber: String, mType: String) {
        if (isInternetAvailable(requireContext())) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0").nullSafety().toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0").nullSafety().toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            if (mType.equals(
                    "PermitDataByZone", ignoreCase = true
                ) && mLotItem != null || mType.equals(
                    "PermitDataV2", ignoreCase = true
                ) && mLotItem != null
            ) {
                mDataFromLprRequest.zone = mLotItem
            }

            scanResultScreenViewModel.getPermitDataFromLprAPI(
                mDataFromLprRequest, mType, mLprNumber
            )

            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "------------Get Data LPR API Permit -----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: Permit " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling all payment related query separately
     */
    private fun callGetDataFromLprApiForPayment(mLprNumber: String, mType: String) {
        if (isInternetAvailable(requireContext())) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            if (mType.equals(
                    "PaymentDataByZone", ignoreCase = true
                ) && mLotItem != null && mAutoComTextViewLot.text.isNotEmpty()
            ) {
                mDataFromLprRequest.zone = mLotItem!!
            }

            scanResultScreenViewModel.getPaymentDataFromLprAPI(
                mDataFromLprRequest, mType, mLprNumber
            )

            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "------------Get Data LPR API Payment-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: Payment " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling all Scofflaw related query separately
     */
    private fun callGetDataFromLprApiForScofflaw(mLprNumber: String, mType: String) {
        if (isInternetAvailable(requireContext())) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            scanResultScreenViewModel.getScofflawDataFromLprAPI(
                mDataFromLprRequest, mType, mLprNumber
            )

            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "------------Get Data LPR API Scofflaw-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: Scofflaw " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling all Scofflaw related query separately
     */
    @Suppress("SameParameterValue")
    private fun callGetDataFromLprApiForExempt(mLprNumber: String, mType: String) {
        if (isInternetAvailable(requireContext())) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            scanResultScreenViewModel.getExemptDataFromLprAPI(
                mDataFromLprRequest, mType, mLprNumber
            )

            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "------------Get Data LPR API Exempt-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: Exempt " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling all Scofflaw related query separately
     */
    @Suppress("SameParameterValue")
    private fun callGetDataFromLprApiForStolen(mLprNumber: String, mType: String) {
        if (isInternetAvailable(requireContext())) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            scanResultScreenViewModel.getStolenDataFromLprAPI(
                mDataFromLprRequest, mType, mLprNumber
            )

            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "------------Get Data LPR API Exempt-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: Exempt " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * We are calling Camera Raw Feed API separately
     */
    @Suppress("SameParameterValue")
    private fun callGetDataFromLprForCameraRawFeedApi(mLprNumber: String, mType: String) {
        if (isInternetAvailable(requireContext())) {
            val mDataFromLprRequest = DataFromLprRequest()
            mDataFromLprRequest.lpNumber = mLprNumber
            mDataFromLprRequest.type = mType
            val mDataFromLprLocation = DataFromLprLocation()
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            mDataFromLprLocation.lat = mLat
            mDataFromLprLocation.long = mLong
            mDataFromLprRequest.page = 1.toLong()

            scanResultScreenViewModel.getCameraRawFeedDataFromLprAPI(
                mDataFromLprRequest, mType, mLprNumber
            )
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "------------Get Data LPR API Camera Raw Feed-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST Camera Raw Feed : " + ObjectMapperProvider.instance.writeValueAsString(
                            mDataFromLprRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api to get DataFromLpr */
    private fun callGetTimingApi(mLprNumber: String) {
        if (requireContext().isInternetAvailable()) {

            //Returns current time in millis
            var timeMilli1: String
            var timeMilli2: String

            if (mHour.isNotEmpty()) {
                timeMilli2 = AppUtils.getStartTDateWithSetting(mZone)
                timeMilli1 = AppUtils.getStartTDateAddSettingHourValue(mZone, mHour)
            } else {
                timeMilli1 = AppUtils.getStartTDate(mZone)
                timeMilli2 = AppUtils.getEndTDate(mZone)

            }

            if (checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpenGOA()) {
                val mReq =
                    "arrival_status=Open,GOA,Violated&issue_ts_from=$timeMilli1&issue_ts_to=$timeMilli2&lp_number=$mLprNumber"

                scanResultScreenViewModel.timingMarkAPI(
                    arrivalStatus = "Open,GOA,Violated",
                    issueTsFrom = timeMilli1,
                    issueTsTo = timeMilli2,
                    lpNumber = mLprNumber
                )
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "------------Get Data LPR API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(mReq)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpen()) {

                val mReq =
                    "arrival_status=Open&issue_ts_from=$timeMilli1&issue_ts_to=$timeMilli2&lp_number=$mLprNumber"

                scanResultScreenViewModel.timingMarkAPI(
                    arrivalStatus = "Open",
                    issueTsFrom = timeMilli1,
                    issueTsTo = timeMilli2,
                    lpNumber = mLprNumber
                )
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "------------Get Data LPR API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(mReq)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val mReq = "issue_ts_from=$timeMilli1&issue_ts_to=$timeMilli2&lp_number=$mLprNumber"

                scanResultScreenViewModel.timingMarkAPI(
                    issueTsFrom = timeMilli1,
                    issueTsTo = timeMilli2,
                    lpNumber = mLprNumber,
                )
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "------------Get Data LPR API Timing-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
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
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun callLastSecondCheckAPI() {
        if (requireContext().isInternetAvailable()) {
            scanResultScreenViewModel.getLastSecondCheckAPI(
                lpNumber = textViewLprNumber.editableText.toString().trim(),
            )
        } else {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
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

            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.dialog_title_permit_api_failed),
                message = getString(R.string.dialog_desc_permit_api_failed),
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }


    //request camera and storage permission
    private fun camera2Intent() {
        viewLifecycleOwner.lifecycleScope.launch {
            mImageCount = scanResultScreenViewModel.getCountImages().nullSafety()
            val maxCount = mainActivityViewModel.getMaxImageCount()
            if (mImageCount >= maxCount) {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_max_image_reached),
                    message = getString(
                        R.string.error_desc_max_image_reached, maxCount.toString()
                    ),
                    positiveButtonText = getString(R.string.button_text_ok),
                )
                return@launch
            }

            val intent = Intent(requireContext(), CameraActivity::class.java)
            cameraTwoActivityLauncher.launch(intent)
        }
    }

    private fun cameraIntent() {
        viewLifecycleOwner.lifecycleScope.launch {
            mImageCount = scanResultScreenViewModel.getCountImages().nullSafety()
            val maxCount = mainActivityViewModel.getMaxImageCount()
            if (mImageCount >= maxCount) {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_max_image_reached),
                    message = getString(
                        R.string.error_desc_max_image_reached, maxCount.toString()
                    ),
                    positiveButtonText = getString(R.string.button_text_ok),
                )
                return@launch
            }

            val mySession = session ?: return@launch
            mySession.takePicture(
                TEMP_IMAGE_FILE_NAME, CameraHelper.SaveLocation.APP_EXTERNAL_FILES
            ) { bmp ->
                bmp?.let {
                    //passing bitmap for converting it to base64
                    //mImageViewNumberPlate.setImageBitmap(mImgaeBitmap);
                    if (mTimeStampOnImage.equals("YES", ignoreCase = true)) {
                        val timeStampBitmap = AppUtils.timestampItAndSave(bmp)
                        saveImageMM(timeStampBitmap)
                    } else {
                        saveImageMM(bmp)
                    }
                }
            }
        }

    }

    private fun saveImageMM(finalBitmap: Bitmap?) {
        if (finalBitmap == null) {
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            return
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            return
        }

        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        if (!myDir.exists()) myDir.mkdirs()

        val timeStamp = SDF_IMAGE_TIMESTAMP.format(Date())
        val fileName = "Image_${timeStamp}_capture.jpg"
        val file = File(myDir, fileName)
        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)

            val compressQuality = 40
            val isSuccess = finalBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)
            out.flush()
            out.close()

            if (!isSuccess || file.length() == 0L) {
                logD("SaveImageMM", "Compression failed or file is empty.")
                file.delete()
                return
            }

            // Delete temp image if exists
            val oldFile = File(myDir, "IMG_temp.jpg")
            if (oldFile.exists()) oldFile.delete()

            // Save path to DB
            val id = SDF_IMAGE_ID_TIMESTAMP.format(Date())
            val mImage = CitationImagesModel().apply {
                citationImage = file.path
                this.id = id.toInt()
            }
            viewLifecycleOwner.lifecycleScope.launch {
                scanResultScreenViewModel.insertCitationImage(mImage)
            }

            logD(
                "SaveImageMM", "Saved image: ${file.path}, size: ${file.length()} bytes"
            )

        } catch (e: Exception) {
            logD("SaveImageMM", "Exception: ${e.message}")
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            e.printStackTrace()
        }
    }

    private fun callAllTypeApi() {
        viewLifecycleOwner.lifecycleScope.launch {

            appCompatTextViewPaymentFuzzyPlate.post {
                fuzzyPlates.setLength(0)
                appCompatTextViewPaymentFuzzyPlate.text = ""
            }

            btnPayment.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
            btnPayment.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            btnPermit.setBackgroundResource(R.drawable.button_round_corner_shape_greyy)
            btnPermit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            lpNumberForLoggerAPI = textViewLprNumber.editableText.toString().trim()

            withContext(Dispatchers.Default) {
                callGetTimingApi(
                    textViewLprNumber.editableText.toString().trim()
                )
            }

            withContext(Dispatchers.Default) {
                callGetDataFromLprApiForExempt(
                    textViewLprNumber.editableText.toString().trim(), "ExemptData"
                )
            }
            withContext(Dispatchers.Default) {
                callGetDataFromLprApiForStolen(
                    textViewLprNumber.editableText.toString().trim(), "StolenData"
                )
            }
            if (mT2LPRSCAN.equals("YES")) {
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForScofflaw(
                        textViewLprNumber.editableText.toString().trim(), "ScofflawDataT2"
                    )
                }

                withContext(Dispatchers.Default) {
                    callGetDataFromLprApi(
                        textViewLprNumber.editableText.toString().trim(), "CitationDataT2", API_SUB_TAG_NAME_GET_CITATION_T2_DATA
                    )
                }

            } else {
//                withContext(Dispatchers.Default) {
//                    callGetDataFromLprApiForScofflaw(
//                        textViewLprNumber.editableText.toString().trim(), "ScofflawData"
//                    )
//                }
//
//                withContext(Dispatchers.Default) {
//                    callGetDataFromLprApi(
//                        textViewLprNumber.editableText.toString().trim(), "CitationData"
//                    )
//                }

                callGetDataFromLprApiForScofflaw(
                    textViewLprNumber.editableText.toString().trim(), "ScofflawData"
                )

                callGetDataFromLprApi(
                    textViewLprNumber.editableText.toString().trim(), "CitationData", API_SUB_TAG_NAME_GET_CITATION_DATA
                )
            }

            isMakeLoader = true
            callGetDataFromLprApi(
                textViewLprNumber.editableText.toString().trim(), "MakeModelColorData",API_SUB_TAG_NAME_GET_MAKE_MODEL_COLOR_DATA
            )

//            withContext(Dispatchers.Default) {
//
//            }
            /**
             * PAYMENTS_BY_ZONE Yes in setting file then call  PermitDataByZone otherwise call permitData
             */
            if (linearLayoutCompatZone.isVisible.nullSafety() && mLotItem != null && mLotItem?.isNotEmpty()
                    .nullSafety()
            ) {
                if (mV2LPRSCAN.equals("YES")) {
                    withContext(Dispatchers.Default) {
                        callGetDataFromLprApiForPermit(
                            textViewLprNumber.editableText.toString().trim(), "PermitDataV2"
                        )

                    }
                } else {
                    withContext(Dispatchers.Default) {
                        callGetDataFromLprApiForPermit(
                            textViewLprNumber.editableText.toString().trim(), "PermitDataByZone"
                        )
                    }
                }
            } else {
                if (mT2LPRSCAN.equals("YES")) {
                    withContext(Dispatchers.Default) {
                        callGetDataFromLprApiForPermit(
                            textViewLprNumber.editableText.toString().trim(), "PermitDataT2"
                        )
                    }
                } else {
                    withContext(Dispatchers.Default) {
                        callGetDataFromLprApiForPermit(
                            textViewLprNumber.editableText.toString().trim(), "PermitData"
                        )
                    }
                }
            }


//            payment data
            if (mPaymentByZone.equals(
                    "YES", ignoreCase = true
                ) && mLotItem != null && mLotItem?.isNotEmpty()
                    .nullSafety() && mAutoComTextViewLot.text.isNotEmpty()
            ) {
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForPayment(
                        textViewLprNumber.editableText.toString().trim(), "PaymentDataByZone"
                    )
                }
            } else if (mAllPaymentsInZoneFuzzy.equals("YES", ignoreCase = true)) {
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForPayment(
                        textViewLprNumber.editableText.toString().trim(), "AllPaymentsInZoneFuzzy"
                    )
                }
            } else {
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForPayment(
                        textViewLprNumber.editableText.toString().trim(), "PaymentData"
                    )
                }
            }

            if (mPaymentDataFuzzy.equals("YES", ignoreCase = true)) {
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForPayment(
                        textViewLprNumber.editableText.toString().trim(), "PaymentDataFuzzy"
                    )
                }
                withContext(Dispatchers.Default) {
                    callGetDataFromLprApiForPermit(
                        textViewLprNumber.editableText.toString().trim(), "PermitDataFuzzy"
                    )
                }
            }


            withContext(Dispatchers.Default) {
                if (isCameraRawFeedAPICallingFlag) callGetDataFromLprForCameraRawFeedApi(
                    textViewLprNumber.editableText.toString().trim(), "CameraRawFeedData"
                )
            }
            if (sharedPreference.read(SharedPrefKey.IS_METER_ACTIVE, "")
                    .equals("ACTIVE", ignoreCase = true)
            ) {
                try {
                    if (mainActivityViewModel.getMeterBuzzerAPICallStatusFormSettingFile()) {
                        val inactiveMeterBuzzerRequest = InactiveMeterBuzzerRequest()
                        val mLat =
                            sharedPreference.read(SharedPrefKey.LAT, "0.0").nullSafety().toDouble()
                        val mLong =
                            sharedPreference.read(SharedPrefKey.LONG, "0.0").nullSafety().toDouble()
                        inactiveMeterBuzzerRequest.latitude = mLat.toString()
                        inactiveMeterBuzzerRequest.longitude = mLong.toString()
                        scanResultScreenViewModel.callInactiveMeterBuzzerAPI(
                            inactiveMeterBuzzerRequest
                        )
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            delay((eventLoggerAPIDelay * 1000).toLong())
            mPayment = true
            btnCheck.enableButton()
        }
    }

    private fun moveToCitationFormWithScofflawData(whichButtonClick: Int) {
        if ((mListScofflaw != null && mListScofflaw?.isNotEmpty().nullSafety())) {
            val sanctionsType = whichButtonClick.let {
                if (it > 0) {
                    when (it) {
                        1 -> "White Sticker"
                        2 -> "Red Sticker"
                        else -> ""
                    }
                } else ""
            }
            val inTime = mListScofflaw?.firstOrNull()?.receivedTimestamp.let {
                sanctionsType + " Time " + AppUtils.formatDateTimeForSanctionType(it.toString())
            }


            val bundle = Bundle()

            if (vehicleStickerInfo != null && mSelectedMakeFromVehicleSticker.isNotBlank()) {
                bundle.putString("make", mSelectedMakeFromVehicleSticker)
            } else {
                bundle.putString("make", mAutoComTextViewMakeVeh.editableText.toString().trim())
            }

            bundle.putString("from_scr", mFromScreen)
            if (mSelectedModel != null) {
                bundle.putString("model", mSelectedModel)
            }
            if (mSelectedVin != null) {
                bundle.putString("vinNumber", mSelectedVin)
            }
            bundle.putString("Street", mStreetItem)
            bundle.putString("timing_record_value_camera", inTime)
            bundle.putString(
                "SideItem",
                if (mSideItem != null && !mSideItem!!.isEmpty()) mSideItem else mDirectionItem
            )
            bundle.putString("Block", mBlock)
            bundle.putString("Direction", mDirectionItem)
            bundle.putString("BodyStyle", mBodyStyleItem)

            bundle.putString("color", mAutoComTextViewVehColor.editableText.toString().trim())
            bundle.putString("lpr_number", textViewLprNumber.text.toString().trim())
            bundle.putString("State", mState)
            bundle.putString("violation_code", mViolationCode)
            bundle.putInt("SanctionButtonClick", whichButtonClick)
            bundle.putString(
                "CitationCount",
                if ((citationHistoryCount + 1) < 4) (citationHistoryCount + 1).toString() else "3"
            )

            //Extra intent for Vehicle Sticker Info
            if (vehicleStickerInfo != null) {
                bundle.putSerializable(INTENT_KEY_VEHICLE_INFO, vehicleStickerInfo)
            }

            nav.safeNavigate(R.id.citationFormScreenFragment, bundle)
        }
    }

    private fun updateLockButtonIcon() {
        try {
            if (appCompatImageViewLock.tag != null && appCompatImageViewLock.tag == "lock") {
                appCompatImageViewLock.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_baseline_lock_open_24
                    )
                )
                appCompatImageViewLock.tag = "unlock"
                sharedPreference.write(SharedPrefKey.LOCK_GEO_ADDRESS, "unlock")
                addressGeo = ""
                sharedPreference.write(
                    SharedPrefKey.LOCK_GEO_SAVE_ADDRESS, addressGeo
                )
                getGeoAddress()

                //Set ADA
                appCompatImageViewLock.contentDescription =
                    getString(R.string.ada_content_description_location_unlocked)
                AccessibilityUtil.announceForAccessibility(
                    appCompatImageViewLock,
                    getString(R.string.ada_content_description_location_unlocked)
                )
            } else {
                appCompatImageViewLock.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_baseline_lock_24
                    )
                )
                appCompatImageViewLock.tag = "lock"
                sharedPreference.write(SharedPrefKey.LOCK_GEO_ADDRESS, "lock")
                sharedPreference.write(
                    SharedPrefKey.LOCK_GEO_SAVE_ADDRESS, addressGeo
                )

                //Set ADA
                appCompatImageViewLock.contentDescription =
                    getString(R.string.ada_content_description_location_locked)
                AccessibilityUtil.announceForAccessibility(
                    appCompatImageViewLock,
                    getString(R.string.ada_content_description_location_locked)
                )
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
                textViewLprNumber.clearFocus()
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                    ) && appCompatImageViewLock.tag.equals("lock") && separated != null
                ) {
                    try {
                        mBlock = separated?.firstOrNull().toString().split("#")[0]
                        if (separated?.size!! > 1) {
                            mStreetItem = separated?.firstOrNull().toString().split("#")[1]
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mBlock = ""
                        mStreetItem = ""
                    }
                }

                val bundle = Bundle()
                if (vehicleStickerInfo != null && mSelectedMakeFromVehicleSticker.isNotBlank()) {
                    bundle.putString("make", mSelectedMakeFromVehicleSticker)
                } else {
                    bundle.putString("make", mAutoComTextViewMakeVeh.editableText.toString().trim())
                }
                bundle.putString("from_scr", mFromScreen)
                if (mSelectedModel != null) {
                    bundle.putString("model", mSelectedModel)
                }
                if (mSelectedVin != null) {
                    bundle.putString("vinNumber", mSelectedVin)
                }
                bundle.putString("color", mAutoComTextViewVehColor.editableText.toString().trim())
                bundle.putString("lpr_number", textViewLprNumber.text.toString().trim())
                bundle.putString("violation_code", violationCode)
                bundle.putString("violation_code_camera", mViolationCode)
                bundle.putString("address", mTvReverseCoded.text.toString().trim())
                bundle.putString("timing_record_value", mTimingRecordRemarkValue)
                bundle.putString(
                    "timing_record_value_camera", mTimingRecordRemarkValueCameraRawFeed
                )
                bundle.putString("timing_tire_stem_value", mTimingTireStem)
                bundle.putString("Lot", mLotItem)
                bundle.putString("Location", mLotItem)
                bundle.putString("Space_id", mSpaceId)
                bundle.putString("Meter", mMeterNameItem)
                bundle.putString("printerQuery", mPrintQuery)
                bundle.putString("Zone", mPBCZone)
                bundle.putString("Street", mStreetItem)
                bundle.putString(
                    "SideItem",
                    if (mSideItem != null && !mSideItem!!.isEmpty()) mSideItem else mDirectionItem
                )
                bundle.putString("Block", mBlock)
                bundle.putString("Direction", mDirectionItem)
                bundle.putString("BodyStyle", mBodyStyleItem)
                bundle.putString("TimingID", timingId)
                bundle.putString("State", mState)
                bundle.putString("Vin", mVin)
                bundle.putString("Note", mNoteItem)
                bundle.putString("Note1", mNoteItem1)
                bundle.putString("EscalatedLprNumber", mEscalatedLprNumber)
                bundle.putString("EscalatedState", mEscalatedState)
                bundle.putString(
                    "CitationCount",
                    if ((citationHistoryCount + 1) < 4) (citationHistoryCount + 1).toString() else "3"
                )
                bundle.putInt("ClickType", mTimingClick.nullSafety())
                bundle.putString("VIOLATION", mViolation)
                if (ImageCache.base64Images.isNotEmpty()) {
                    bundle.putString(INTENT_KEY_TIMING_IMAGES_BASE64, "YES")
                }

                if (mListCitation?.isNotEmpty().nullSafety()) {
                    bundle.putInt(
                        INTENT_KEY_UNPAID_CITATION_COUNT,
                        mListCitation?.count { it.status == API_CONSTANT_CITATION_STATUS_VALID }
                            .nullSafety())
                }
                bundle.putString("State", mState)
                bundle.putString("vendor_name", mVendorName)

                //Extra intent for Vehicle Sticker Info
                bundle.putSerializable(INTENT_KEY_VEHICLE_INFO, vehicleStickerInfo)

                nav.safeNavigate(R.id.citationFormScreenFragment, bundle)
            } else {
                textViewLprNumber.requestFocus()
                textViewLprNumber.isFocusable = true
                requireContext().toast(getString(R.string.err_msg_plate_is_empty))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var mRegex = ""

    /* Call Api For Lpr scan details */
    @Throws(IOException::class, ParseException::class)
    private fun callPushEventApi(lprNumber: String) {
        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0").nullSafety().toDouble()
        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0").nullSafety().toDouble()
        try {
            if (isInternetAvailable(requireContext())) {
                val mPushEventRequest = LprScanLoggerRequest()
                mPushEventRequest.activityType = "LPRScan"
                mPushEventRequest.lpNumber = lprNumber
                mPushEventRequest.logType = Constants.LOG_TYPE_NODE_PORT
                mPushEventRequest.latitude = mLat
                mPushEventRequest.longitude = mLong
                mPushEventRequest.clientTimestamp = splitDateLpr("CST")
                mPushEventRequest.siteOfficerName =
                    mWelcomeForm!!.officerFirstName + " " + mWelcomeForm!!.officerLastName
                mPushEventRequest.supervisorName = mWelcomeForm!!.officerSupervisor
                mPushEventRequest.badgeId = mWelcomeForm!!.officerBadgeId
                mPushEventRequest.zone = mWelcomeForm!!.officerZone
                mPushEventRequest.beat = mWelcomeForm!!.officerBeatName
                mPushEventRequest.mAgency = mWelcomeForm!!.agency
                mPushEventRequest.mShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                mPushEventRequest.mSiteOfficerName =
                    mWelcomeForm?.officerFirstName + " " + mWelcomeForm?.officerLastName
                mPushEventRequest.block =
                    if (separatedEventLogger != null && !separatedEventLogger.toString()
                            .isEmpty() && separatedEventLogger!!.size > 1
                    ) separatedEventLogger!![0] else " "
                mPushEventRequest.street =
                    if (separatedEventLogger != null && separatedEventLogger.toString()
                            .isNotEmpty() && separatedEventLogger!!.size >= 2
                    ) separatedEventLogger!![1] else addressGeo

                //new added params in phase2 lpr_scan api
                logD("Event logger after scan page", citationDataLength.toString())
                mPushEventRequest.scofflawData =
                    (if (scofflawDataLength != null) if (scofflawDataLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.citationData =
                    (if (citationDataLength != null) if (citationDataLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.exemptData =
                    (if (exemptDataLength != null) if (exemptDataLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.paymentData =
                    (if (paymentLength != null) if (paymentLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.paymentDataFuzzy =
                    (if (paymentFuzzyLength != null) if (paymentFuzzyLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.permitData =
                    (if (permitDataLength != null) if (permitDataLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.permitDataFuzzy =
                    (if (permitDataFuzzyLength != null) if (permitDataFuzzyLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.stolenData =
                    (if (stolenDataLength != null) if (stolenDataLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                mPushEventRequest.makeModelColorData =
                    (if (modelColorLength != null) if (modelColorLength.nullSafety() > 0) 1 else 0 else 0).toLong()
                var timingLenght: Long = 0
                try {
                    timingLenght =
                        if (timingMarkResponse!!.data != null) timingMarkResponse!!.data!!.size.toLong() else 0.toLong()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mPushEventRequest.timingData = (if (timingLenght > 0) 1 else 0).toLong()
                scanResultScreenViewModel.lprScanLoggerAPI(mPushEventRequest)
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "------------Data Count API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                mPushEventRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true
                    ) && permitDataLength?.toInt() == 0 && citationDataLength?.toInt() == 0
                ) {
                    showPopupForAutoWarningTicket()
                }
            } else {
                requireContext().toast(
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
        } catch (e: Exception) {
            logE("mLoggerViewModel", e.message.nullSafety())
            e.printStackTrace()
        }
    }

    fun copyFileOrDirectory(srcDir: String?, dstDir: String?) {
        try {
            val src = File(srcDir.nullSafety())
            val dst = File(dstDir, src.name)
            if (src.isDirectory) {
                val files = src.list().orEmpty()
                val filesLength = files.size.nullSafety()
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
        if (!destFile.parentFile?.exists().nullSafety()) destFile.parentFile?.mkdirs()
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

    private fun setLayoutVisibilityBasedOnSettingResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                settingsList = ArrayList()
                settingsList = mainActivityViewModel.getSettingsListFromDataSet()
                if (settingsList != null && settingsList!!.isNotEmpty()) {
                    for (i in settingsList!!.indices) {
                        logD("Setting", settingsList!![i].type.toString())
                        if (settingsList!![i].type.equals(
                                "HAS_MAKE", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            linearLayoutCompatMake.visibility = View.GONE
                        } else if (settingsList!![i].type.equals(
                                "HAS_MODEL", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            linearLayoutCompatModel.visibility = View.GONE
                            if (BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    DuncanBrandingApp13(), true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                                )
                            ) {
                                linearLayoutCompatMake.visibility = View.GONE
                                linearLayoutCompatColor.visibility = View.GONE
                            }
                        } else if (settingsList!![i].type.equals(
                                "HAS_COLOR", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            linearLayoutCompatColor.visibility = View.GONE
                        } else if (settingsList!![i].type.equals(
                                "HAS_PAYMENTS", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            btnPayment.visibility = View.GONE
                        } else if (settingsList!![i].type.equals(
                                "HAS_SCOFFLAW", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            btnScofflaw.visibility = View.GONE
                        } else if (settingsList!![i].type.equals(
                                "HAS_TIMING", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            btnTimings.visibility = View.GONE
                        } else if (settingsList!![i].type.equals(
                                "HAS_PERMIT", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            btnPermit.visibility = View.GONE
                        } else if (settingsList!![i].type.equals(
                                "HAS_STOLEN", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            btnStolen.visibility = View.GONE

                        } else if (settingsList!![i].type.equals(
                                "HAS_EXEMPT", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            btnExempt.visibility = View.GONE
                        } else if (settingsList!![i].type.equals(
                                "HAS_CAMERA_RAW_FEED_DATA", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            isCameraRawFeedAPICallingFlag = true
                            btnCameraRawFeedData.visibility = View.VISIBLE
                        } else if (settingsList!![i].type.equals(
                                "HISTORY", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            linearLayoutCompatHistory.visibility = View.GONE
                        }
                        if (settingsList!![i].type.equals(
                                "TIMEZONE", ignoreCase = true
                            )
                        ) {
                            try {
                                mZone = settingsList!![i].mValue.toString()
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "TIMING_RECORD_LOOKUP_THRESHOLD", ignoreCase = true
                            )
                        ) {
                            try {
                                mHour = settingsList!![i].mValue.toString()
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "IS_TIRE_STEM_ICON", ignoreCase = true
                            )
                        ) {
                            if (settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                try {
                                    isTireStemWithImageView = true
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "IS_CAMERA2_ENABLE", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            try {
                                mCamera2Setting = "YES"
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "IS_PAYMENT_DATA_FUZZY", ignoreCase = true
                            ) && settingsList!![i].mValue.equals(
                                "YES", ignoreCase = true
                            ) || settingsList!![i].type.equals(
                                "IS_FUZZY_SEARCH", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            try {
                                mPaymentDataFuzzy = "YES"
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "PAYMENTS_BY_ZONE", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            try {

                                linearLayoutCompatZone.visibility = View.VISIBLE
                                mPaymentByZone = "YES"
                                setDropdownLot()

                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "EXPIRED_PAYMENTS_IN_LPR", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            try {

                                mAllPaymentsInZoneFuzzy = "YES"
//                                        if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,true)&&
//                                            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,true)) {
//                                            linearLayoutCompatZone.visibility = View.VISIBLE
//                                            setDropdownLot()
//                                        }

                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "PERMITS_BY_ZONE", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            try {
                                //viewLifecycleOwner.lifecycleScope.launch {
                                linearLayoutCompatZone.visibility = View.VISIBLE
                                mPermitByZone = "YES"
                                setDropdownLot()
                                //}
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "T2_LPR_SCAN", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            try {
                                mT2LPRSCAN = "YES"
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "V2_LPR_SCAN", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            try {
                                mV2LPRSCAN = "YES"
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
//                            Default_Regulation_time_auto	DEFAULT_REGULATION_TIME_AUTO	8 HOURS#480
//                            Is_Auto_timing	IS_AUTO_TIMING	YES
                        if (settingsList!![i].type.equals(
                                SETTINGS_FLAG_DEFAULT_REGULATION_TIME_AUTO, ignoreCase = true
                            )
                        ) {
                            try {
                                mAutoTimingRegulationTime = settingsList!![i].mValue
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                SETTINGS_FLAG_IS_AUTO_TIMING, ignoreCase = true
                            )
                        ) {
                            try {
                                mAutoTiming = settingsList!![i].mValue
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (settingsList!![i].type.equals(
                                "IS_FINE_SUM_VISIBLE", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            isFineSumVisible = true
                        }
                        if (settingsList!![i].type.equals(
                                "IMAGE_TIMESTAMP", ignoreCase = true
                            ) && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                        ) {
                            mTimeStampOnImage = "YES"
                        }
                        if (settingsList!![i].type.equals(
                                "EVENT_LOGGER_API_DELAY", ignoreCase = true
                            )
                        ) {
                            eventLoggerAPIDelay =
                                settingsList!![i].mValue?.toDoubleOrNull() // safely parse string to Double
                                    ?: 2.5
                        }
                    }
                }
                updateButtonVisibilityPermitPayment(btnPermit, btnPayment)
                updateButtonVisibilityForExemptStolen(btnExempt, btnStolen)
                updateButtonVisibilityScofflawTimings(btnScofflaw, btnTimings)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setFuzzyLogicSetTextView() {
        textViewLprNumber.post {
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
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text?.isNotEmpty().nullSafety()
                ) {
                    appCompatTextViewFuzzy1.visibility = View.VISIBLE
                    appCompatTextViewFuzzy1.text =
                        textViewLprNumber.text?.firstOrNull().toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy1.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy1.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy1.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy1.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
                        appCompatTextViewFuzzy1.tag = "GREEN"
                    }
                }
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text!!.length > 1
                ) {
                    appCompatTextViewFuzzy2.visibility = View.VISIBLE
                    appCompatTextViewFuzzy2.text =
                        textViewLprNumber.text?.get(1)?.toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy2.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy2.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy2.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy2.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
                        appCompatTextViewFuzzy2.tag = "GREEN"
                    }
                }
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text!!.length > 2
                ) {
                    appCompatTextViewFuzzy3.visibility = View.VISIBLE
                    appCompatTextViewFuzzy3.text =
                        textViewLprNumber.text?.get(2)?.toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy3.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy3.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy3.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy3.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
                        appCompatTextViewFuzzy3.tag = "GREEN"
                    }
                }
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text!!.length > 3
                ) {
                    appCompatTextViewFuzzy4.visibility = View.VISIBLE
                    appCompatTextViewFuzzy4.text =
                        textViewLprNumber.text?.get(3).toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy4.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy4.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy4.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy4.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
                        appCompatTextViewFuzzy4.tag = "GREEN"
                    }
                }
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text!!.length > 4
                ) {
                    appCompatTextViewFuzzy5.visibility = View.VISIBLE
                    appCompatTextViewFuzzy5.text =
                        textViewLprNumber.text?.get(4).toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy5.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy5.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy5.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy5.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
                        appCompatTextViewFuzzy5.tag = "GREEN"
                    }
                }
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text!!.length > 5
                ) {
                    appCompatTextViewFuzzy6.visibility = View.VISIBLE
                    appCompatTextViewFuzzy6.text =
                        textViewLprNumber.text?.get(5).toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy6.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy6.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy6.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy6.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
                        appCompatTextViewFuzzy6.tag = "GREEN"
                    }
                }
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text!!.length > 6
                ) {
                    appCompatTextViewFuzzy7.visibility = View.VISIBLE
                    appCompatTextViewFuzzy7.text =
                        textViewLprNumber.text?.get(6).toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy7.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy7.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy7.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy7.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
                        appCompatTextViewFuzzy7.tag = "GREEN"
                    }
                }
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text!!.length > 7
                ) {
                    appCompatTextViewFuzzy8.visibility = View.VISIBLE
                    appCompatTextViewFuzzy8.text =
                        textViewLprNumber.text?.get(7).toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy8.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy8.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy8.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy8.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
                        appCompatTextViewFuzzy8.tag = "GREEN"
                    }
                }
                if (!textViewLprNumber.text.toString()
                        .isEmpty() && textViewLprNumber.text!!.length > 8
                ) {
                    appCompatTextViewFuzzy9.visibility = View.VISIBLE
                    appCompatTextViewFuzzy9.text =
                        textViewLprNumber.text?.get(8).toString().nullSafety()
                    if (getFuzzyStringList().contains(
                            appCompatTextViewFuzzy9.text.toString()
                        )
                    ) {
                        appCompatTextViewFuzzy9.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_red
                        )
                        appCompatTextViewFuzzy9.tag = "RED"
                    } else {
                        appCompatTextViewFuzzy9.background = ContextCompat.getDrawable(
                            requireContext(), R.drawable.round_corner_shape_without_fill_thin_green
                        )
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
            appCompatTextViewFuzzy2.setOnClickListener {
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
            (appCompatTextViewFuzzy1.text.toString() + "" + appCompatTextViewFuzzy2.text + "" + appCompatTextViewFuzzy3.text + "" + appCompatTextViewFuzzy4.text + "" + appCompatTextViewFuzzy5.text + "" + appCompatTextViewFuzzy6.text + "" + appCompatTextViewFuzzy7.text + "" + appCompatTextViewFuzzy8.text + "" + appCompatTextViewFuzzy9.text)
        textViewLprNumber.setText(updateLprNumber.trim())
        mAutoComTextViewMakeVeh.setText("")
        mAutoComTextViewVehModel.setText("")
        mAutoComTextViewVehColor.setText("")
        callAllTypeApi()
    }

    private fun set6ButtonColor(
        linearLayout: LinearLayoutCompat?, button: AppCompatButton?, colorCode: Int
    ) {
        try {
            if (colorCode == 0) {
                button?.setBackgroundResource(R.drawable.button_round_corner_shape_orange)
                linearLayout?.setBackgroundResource(R.drawable.round_corner_shape_without_fill_orange_)
            }

            if (colorCode == 1) {
                button?.setBackgroundResource(R.drawable.button_round_corner_shape_red)
                linearLayout?.setBackgroundResource(R.drawable.round_corner_shape_without_fill_red)
            }

            button?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } catch (e: Exception) {
            e.printStackTrace()
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

            mAddTimingRequest.lprState = ""
            mAddTimingRequest.lprNumber = textViewLprNumber.text.toString()
            mAddTimingRequest.meterNumber = ""
            mAddTimingRequest.mLot = ""
            mAddTimingRequest.mLocation = mTvReverseCoded.text.toString()
            mAddTimingRequest.block = mTvReverseCoded.text.toString()
            try {
                mAddTimingRequest.regulationTime =
                    if (mAutoTimingRegulationTime?.nullSafety()?.contains("#")
                            .nullSafety()
                    ) mAutoTimingRegulationTime!!.split(
                        "#"
                    )[1].toLong() else 480
                mAddTimingRequest.regulationTimeValue =
                    if (mAutoTimingRegulationTime?.nullSafety()?.contains("#")
                            .nullSafety()
                    ) mAutoTimingRegulationTime!!.split(
                        "#"
                    )[0] else "8 HOURS"
            } catch (e: Exception) {
                e.printStackTrace()
                mAddTimingRequest.regulationTime = "480".toLong()
            }
            mAddTimingRequest.street = ""
            mAddTimingRequest.side = ""
            mAddTimingRequest.zone = ""
            mAddTimingRequest.pbcZone = ""
            mAddTimingRequest.remark = ""
            mAddTimingRequest.remark2 = ""
            mAddTimingRequest.mTireStemFront = 0
            mAddTimingRequest.mTireStemBack = 0
            mAddTimingRequest.mVin = ""
            mAddTimingRequest.status = "Open"
            mAddTimingRequest.latitude = mLat
            mAddTimingRequest.longitiude = mLong
            mAddTimingRequest.source = "officer"
            mAddTimingRequest.officerName =
                mWelcomeForm?.officerFirstName.nullSafety() + " " + mWelcomeForm?.officerLastName.nullSafety()
            mAddTimingRequest.badgeId = mWelcomeForm?.officerBadgeId.nullSafety()
            mAddTimingRequest.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            mAddTimingRequest.supervisor = mWelcomeForm?.officerSupervisor.nullSafety()
            mAddTimingRequest.markStartTimestamp = splitDateLpr(mZone)
            mAddTimingRequest.markIssueTimestamp = splitDateLpr(mZone)
            mAddTimingRequest.mMake = mSelectedMake
            mAddTimingRequest.mModel = mSelectedModel
            mAddTimingRequest.mColor = mSelectedColor
            mAddTimingRequest.mAddress = mAddTimingRequest.block + " " + mAddTimingRequest.street
            mAddTimingRequest.imageUrls = null
            if (requireContext().isInternetAvailable()) {
                scanResultScreenViewModel.callAddTimingAPI(mAddTimingRequest)
            } else {
                requireContext().toast(getString(R.string.err_msg_connection_was_refused))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun overTimeParkingForTicketDetailsCharleston(timeMarkData: TimingMarkData) {
        try {
            /**
             * Save selected block and street when address is locked
             */
            viewLifecycleOwner.lifecycleScope.launch {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble()

                val mAddTimingRequest = AddTimingRequest()

                mAddTimingRequest.lprState = timeMarkData.lpState
                mAddTimingRequest.lprNumber = textViewLprNumber.text.toString()
                mAddTimingRequest.meterNumber = timeMarkData.meterNumber
                mAddTimingRequest.mLot = timeMarkData.lot
                mAddTimingRequest.mLocation = timeMarkData.location.toString()
                mAddTimingRequest.block = timeMarkData.block
                try {
                    mAddTimingRequest.regulationTime = timeMarkData.regulationTime
                    mAddTimingRequest.regulationTimeValue =
                        timeMarkData.regulationTimeValue//timeMarkData.regulationTime
                } catch (e: Exception) {
                    e.printStackTrace()
                    mAddTimingRequest.regulationTime = "480".toLong()
                }
                mAddTimingRequest.street = timeMarkData.street
                mAddTimingRequest.side = timeMarkData.side
                mAddTimingRequest.zone = timeMarkData.zone
                mAddTimingRequest.remark = timeMarkData.remark
                mAddTimingRequest.remark2 = timeMarkData.remark2
                mAddTimingRequest.mTireStemFront = timeMarkData.tireStemFront!!.toInt()
                mAddTimingRequest.mTireStemBack = timeMarkData.tireStemBack!!.toInt()
                mAddTimingRequest.mVin = timeMarkData.vinNumber
                mAddTimingRequest.status = "Open"
                mAddTimingRequest.latitude = mLat
                mAddTimingRequest.longitiude = mLong
                mAddTimingRequest.source = "officer"
                mAddTimingRequest.officerName =
                    mWelcomeForm?.officerFirstName.nullSafety() + " " + mWelcomeForm?.officerLastName.nullSafety()
                mAddTimingRequest.badgeId = mWelcomeForm?.officerBadgeId.nullSafety()
                mAddTimingRequest.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                mAddTimingRequest.supervisor = mWelcomeForm?.officerSupervisor.nullSafety()
                mAddTimingRequest.markStartTimestamp = splitDateLpr(mZone)
                mAddTimingRequest.markIssueTimestamp = splitDateLpr(mZone)
                mAddTimingRequest.mMake = mSelectedMake
                mAddTimingRequest.mModel = mSelectedModel
                mAddTimingRequest.mColor = mSelectedColor
                mAddTimingRequest.mAddress =
                    mAddTimingRequest.block + " " + mAddTimingRequest.street
                mAddTimingRequest.imageUrls = timeMarkData.images
                sharedPreference.writeOverTimeParkingTicketDetails(
                    SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION, mAddTimingRequest
                )
//            if (requireContext().isInternetAvailable()) {
//                mAddTimingViewModel?.hitAddTimingApi(mAddTimingRequest)
//            } else {
//                LogUtil.printToastMSGForErrorWarning(
//                    requireContext(),
//                    getString(R.string.err_msg_connection_was_refused)
//                )
//            }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPopupForAutoWarningTicket() {
        AlertDialogUtils.showDialog(
            context = requireContext(),
            title = getString(R.string.scr_lbl_auto_warning_ticket),
            message = getString(R.string.scr_lbl_auto_warning_desc),
            positiveButtonText = getString(R.string.button_text_yes),
            negativeButtonText = getString(R.string.button_text_no),
            listener = object : AlertDialogListener {
                override fun onPositiveButtonClicked() {
                    if (requireContext().isInternetAvailable()) {
                        val request = buildCreateTicketRequest()
                        scanResultScreenViewModel.callCreateCitationTicketAPI(request)
                        try {
                            if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                ApiLogsClass.writeApiPayloadTex(
                                    requireContext(),
                                    "------------Scan auto Create API-----------------"
                                )
                                ApiLogsClass.writeApiPayloadTex(
                                    requireContext(), "REQUEST: ${
                                        ObjectMapperProvider.instance.writeValueAsString(request)
                                    }"
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
    }

    private fun buildCreateTicketRequest(): CreateTicketRequest {

        return CreateTicketRequest().apply {
            lifecycleScope.launch {
                mTicketNumber = scanResultScreenViewModel.getCitationBooklet(0)
                if (mTicketNumber?.isNotEmpty().nullSafety()) {
                    val mCitaionImagesLinks: MutableList<String> = ArrayList()
                    val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble()
                    val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble()
                    val mApplicationList = mainActivityViewModel.getViolationListFromDataSet()

                    locationDetails = buildLocationDetails()
                    vehicleDetails = buildVehicleDetails()

                    violationDetails = buildViolationDetails(mApplicationList)
                    invoiceFeeStructure = buildInvoiceFeeStructure()
                    officerDetails = buildOfficerDetails()
                    commentsDetails = buildCommentsDetails()
                    headerDetails = buildHeaderDetails()
                    lprNumber = textViewLprNumber.text.toString()
                    code = violationDetails?.code
                    hearingDate = ""
                    ticketNo = mTicketNumber?.firstOrNull()?.citationBooklet
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
                    mLatitude = mLat
                    mLongitiude = mLong
                    val jsonObject = JSONObject().apply {
                        put("print_query", AppUtils.printQueryStringBuilder.toString())
                        put(
                            "print_height",
                            sharedPreference.readInt(SharedPrefKey.LAST_PRINTOUT_HEIGHT, 1000)
                        )
                    }
                    printQuery = jsonObject.toString()
                } else {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.scr_lbl_booklet),
                        message = getString(R.string.scr_lbl_booklet_desc),
                        cancelable = false,
                        positiveButtonText = getString(R.string.button_text_ok),
                        listener = object : AlertDialogListener {
                            override fun onPositiveButtonClicked() {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    mainActivityViewModel.sendActionToMain(MainActivityAction.EventProcessLogout)
                                }
                            }
                        })
                }
            }
        }
    }

    private fun buildVehicleDetails(): VehicleDetails {
        return VehicleDetails().apply {
            lprNo = textViewLprNumber.text.toString()
            make = mAutoComTextViewMakeVeh.text.toString()
            model = mAutoComTextViewVehModel.text.toString()
            model_lookup_code = ""
            color = mAutoComTextViewVehColor.text.toString()
            state = "TX"
            body_style = ""
            body_style_lookup_code = ""
            mLicenseExpiry = ""
            vin_number = ""
            decal_year = ""
            decal_number = ""
        }
    }

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
            }
        }

        if (details.description.isNullOrEmpty()) {
            requireContext().toast(getString(R.string.scr_lbl_violation_list))
        }
        return details
    }

    private fun buildOfficerDetails(): OfficerDetails {
        return OfficerDetails().apply {
            agency = mWelcomeForm?.agency.nullSafety()
            badgeId = mWelcomeForm?.officerBadgeId.nullSafety()
            beat = mWelcomeForm?.officerBeat.nullSafety()
            officer_lookup_code = mWelcomeForm?.officer_lookup_code.nullSafety()
            peo_fname = ""
            peo_lname = ""
            peo_name = ""
            squad = ""
            zone = ""
            mShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            mDdeviceId = mWelcomeForm?.officerDeviceId.nullSafety()
            mDdeviceFriendlyName = mWelcomeForm?.officerDeviceName.nullSafety()

            officer_name = AppUtils.getOfficerName(
                mWelcomeForm?.officerFirstName + " " + mWelcomeForm?.officerLastName
            )
        }
    }

    private fun buildCommentsDetails(): CommentsDetails {
        return CommentsDetails().apply {
            remark1 =
                "IF YOU ARE A VISITOR, PLEASE VISIT THE RECEPTIONIST OF YOUR PROVIDER TO ACQUIRE A “VISITOR PERMIT” TO AVOID PARKING ENFORCEMENT ACTION. IF YOU ARE AN EMPLOYEE, PLEASE ACQUIRE AN EMPLOYEE PERMIT AND PARK IN THE DESIGNATED EMPLOYEE PARKING LOT TO AVOID VEHICLE IMMOBILIZATION AND SUBJECT TO A $50 RELEASE FEE"
            remark2 = ""
            note1 = ""
            note2 = ""
            note3 = ""
        }
    }

    private fun buildHeaderDetails(): HeaderDetails {
        return HeaderDetails().apply {
            citationNumber = mTicketNumber?.firstOrNull()?.citationBooklet
            timestamp = AppUtils.getCurrentDateTimeForCitationForm(splitDateLpr(""))
        }
    }

    fun showWhiteAndRedStickerPopup(anchorView: View, item: ScofflawDataResponse?) {
        val context = anchorView.context
        val popupView =
            LayoutInflater.from(context).inflate(R.layout.popup_white_red_button_boot_tow, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 10f

        val btnWhite = popupView.findViewById<Button>(R.id.btnWhite)
        val btnRed = popupView.findViewById<Button>(R.id.btnRed)

        btnWhite.setOnClickListener {
            popupWindow.dismiss()
            moveToCitationFormWithScofflawData(1)
        }

        btnRed.setOnClickListener {
            popupWindow.dismiss()
            moveToCitationFormWithScofflawData(2)
        }

        popupWindow.showAsDropDown(anchorView, 0, 0)
    }

    fun updateButtonVisibilityForExemptStolen(
        btnExemptView: AppCompatButton, btnStolenView: AppCompatButton
    ) {
        if (btnExemptView.isGone) {
            btnExempt.visibility = View.GONE
            val params = btnStolen.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, params.topMargin, 0, params.bottomMargin)
            btnStolen.layoutParams = params
        } else if (btnStolenView.isGone) {
            btnStolen.visibility = View.GONE
            val params = btnExempt.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, params.topMargin, 0, params.bottomMargin)
            btnExempt.layoutParams = params
        } else {
            // Both visible, restore original margins
            btnExempt.visibility = View.VISIBLE
            btnStolen.visibility = View.VISIBLE

            val exemptParams = btnExempt.layoutParams as LinearLayout.LayoutParams
            exemptParams.setMargins(
                0,
                exemptParams.topMargin,
                resources.getDimensionPixelSize(R.dimen._10dp),
                exemptParams.bottomMargin
            )
            btnExempt.layoutParams = exemptParams

            val stolenParams = btnStolen.layoutParams as LinearLayout.LayoutParams
            stolenParams.setMargins(
                resources.getDimensionPixelSize(R.dimen._10dp),
                stolenParams.topMargin,
                0,
                stolenParams.bottomMargin
            )
            btnStolen.layoutParams = stolenParams
        }
    }

    fun updateButtonVisibilityScofflawTimings(
        btnScofflawView: AppCompatButton, btnTimingView: AppCompatButton
    ) {
        if (btnScofflawView.isGone) {
            btnScofflaw.visibility = View.GONE
            val timingsParams = btnTimings.layoutParams as LinearLayoutCompat.LayoutParams
            timingsParams.setMargins(0, timingsParams.topMargin, 0, timingsParams.bottomMargin)
            btnTimings.layoutParams = timingsParams
        } else if (btnTimingView.isGone) {
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
            scofflawParams.setMargins(
                0, scofflawParams.topMargin, margin10, scofflawParams.bottomMargin
            )
            btnScofflaw.layoutParams = scofflawParams

            val timingsParams = btnTimings.layoutParams as LinearLayoutCompat.LayoutParams
            timingsParams.setMargins(
                margin10, timingsParams.topMargin, 0, timingsParams.bottomMargin
            )
            btnTimings.layoutParams = timingsParams
        }
    }

    fun updateButtonVisibilityPermitPayment(
        btnPermitView: AppCompatButton, btnPaymentView: AppCompatButton
    ) {
        if (btnPermitView.isGone) {
            btnPermit.visibility = View.GONE
            val timingsParams = btnPayment.layoutParams as LinearLayoutCompat.LayoutParams
            timingsParams.setMargins(0, timingsParams.topMargin, 0, timingsParams.bottomMargin)
            btnPayment.layoutParams = timingsParams
        } else if (btnPaymentView.isGone) {
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
            scofflawParams.setMargins(
                0, scofflawParams.topMargin, margin10, scofflawParams.bottomMargin
            )
            btnPermit.layoutParams = scofflawParams

            val timingsParams = btnPayment.layoutParams as LinearLayoutCompat.LayoutParams
            timingsParams.setMargins(
                margin10, timingsParams.topMargin, 0, timingsParams.bottomMargin
            )
            btnPayment.layoutParams = timingsParams
        }
    }
}