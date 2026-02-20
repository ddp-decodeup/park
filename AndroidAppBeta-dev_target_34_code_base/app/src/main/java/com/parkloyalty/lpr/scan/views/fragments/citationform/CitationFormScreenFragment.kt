package com.parkloyalty.lpr.scan.views.fragments.citationform

import DialogUtil
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.camera2.CameraActivity
import com.parkloyalty.lpr.scan.crop.OcrActivity
import com.parkloyalty.lpr.scan.database.DataBaseUtil
import com.parkloyalty.lpr.scan.databinding.FragmentCitationFormScreenBinding
import com.parkloyalty.lpr.scan.datepicker.MonthYearPickerDialog
import com.parkloyalty.lpr.scan.extensions.*
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.PPA_SAN_DIEGO_LPR_EMPTY_THEN_VIN_MIN
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.*
import com.parkloyalty.lpr.scan.network.models.ScannedImageUploadResponse
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.NoFilterArrayAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ViewPagerBannerAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.filtrationadapter.AutoCompleteAdapter
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.ImageCache
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.*
import com.parkloyalty.lpr.scan.ui.login.model.BeatStat
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.ZoneStat
import com.parkloyalty.lpr.scan.ui.printer.PrintLayoutModel
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import com.parkloyalty.lpr.scan.util.*
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.setAccessibilityForCheckboxUnderLinearLayout
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.setCustomContentDescription
import com.parkloyalty.lpr.scan.util.AppUtils.getDayOfWeek
import com.parkloyalty.lpr.scan.util.AppUtils.isValidFileToUpload
import com.parkloyalty.lpr.scan.util.AppUtils.maxImageCount
import com.parkloyalty.lpr.scan.util.AppUtils.setListOnly
import com.parkloyalty.lpr.scan.util.AppUtils.setLprLock
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.AppUtils.splitDoller
import com.parkloyalty.lpr.scan.util.AppUtils.splitID
import com.parkloyalty.lpr.scan.util.AppUtils.textWatcherForLicensePlate
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.CheckTypeOfField
import com.parkloyalty.lpr.scan.util.FileUtil.getSignatureFileNameWithExt
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSGForErrorWarning
import com.parkloyalty.lpr.scan.util.Util.calculateInSampleSize
import com.parkloyalty.lpr.scan.util.Util.setFieldCaps
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_BITMAP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_LAST_SECOND_CHECK
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_WELCOME
import com.parkloyalty.lpr.scan.utils.MultipartUtils
import com.parkloyalty.lpr.scan.utils.NewConstructLayoutBuilder
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.SettingsUtils
import com.parkloyalty.lpr.scan.utils.camerahelper.CameraHelper
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.vehiclestickerscan.model.VehicleInfoModel
import com.parkloyalty.lpr.scan.views.MainActivityAction
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.NewLprScanActivity
import com.parkloyalty.lpr.scan.views.NewOcrActivity
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import se.warting.signatureview.views.SignaturePad
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class CitationFormScreenFragment : BaseFragment<FragmentCitationFormScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    //Can be used later for settings specific logic
    private val citationFormScreenViewModel: CitationFormScreenViewModel by viewModels()

    @Inject
    lateinit var sharedPreference: SharedPref

    @Inject
    lateinit var settingsUtils: SettingsUtils

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var constructLayoutBuilder: NewConstructLayoutBuilder

    @Inject
    lateinit var cameraHelper: CameraHelper

    private var session: CameraHelper.Session? = null

    private lateinit var ocrActivityLauncher: ActivityResultLauncher<Intent>





    private var mAutoComTextViewState: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewBodyStyle: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewDecalYear: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewDecalNum: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewVinNum: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewLicensePlate: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewSide: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewStreet: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewDirection: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewMeterName: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewZone: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewLot: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewLocation: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewAbbrCode: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewCode: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewVioTypeCode: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewVioTypeDescription: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewVioType: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewAmount: AppCompatAutoCompleteTextView? = null
    private var AutoComTextViewDueDate: AppCompatAutoCompleteTextView? = null
    private var AutoComTextViewDueDate30: AppCompatAutoCompleteTextView? = null
    private var AutoComTextViewDueDate45: AppCompatAutoCompleteTextView? = null
    private var AutoComTextViewTotalDue: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewAmountDueDate: AppCompatAutoCompleteTextView? = null
    private var mTvOfficerName: AppCompatAutoCompleteTextView? = null
    private var mTvOfficerId: AppCompatAutoCompleteTextView? = null
    private var mTvBadgeId: AppCompatAutoCompleteTextView? = null
    private var mTvBeat: AppCompatAutoCompleteTextView? = null
    private var mTvSquad: AppCompatAutoCompleteTextView? = null
    private var mTvZone: AppCompatAutoCompleteTextView? = null
    private var mTvAgency: AppCompatAutoCompleteTextView? = null
    private var mTvOfficerMake: AppCompatAutoCompleteTextView? = null
    private var mTvOfficerModel: AppCompatAutoCompleteTextView? = null
    private var mTvOfficerColor: AppCompatAutoCompleteTextView? = null
    private var mEtLocationRemarks: AppCompatAutoCompleteTextView? = null
    private var mEtLocationRemarks1: AppCompatAutoCompleteTextView? = null
    private var mEtLocationRemarks2: AppCompatAutoCompleteTextView? = null
    private var mEtLocationNotes: AppCompatAutoCompleteTextView? = null
    private var mEtLocationNotes1: AppCompatAutoCompleteTextView? = null
    private var mEtLocationNotes2: AppCompatAutoCompleteTextView? = null
    private var mEtMarkTime: AppCompatAutoCompleteTextView? = null
    private var mEtLocationDescr: AppCompatAutoCompleteTextView? = null
    private var mAutoCompleteDate: AppCompatAutoCompleteTextView? = null
    private var mAutoCompleteExpiryMonth: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewPBCZone: AppCompatAutoCompleteTextView? = null
    private var mAutoConTextViewObservationTime: AppCompatAutoCompleteTextView? = null
    private var mAutoConTextViewSpace: AppCompatAutoCompleteTextView? = null

    //Start of UI Views
    lateinit var linearLayoutVehicleDetails: LinearLayoutCompat
    lateinit var appCompatTextViewTicketNumberTitle: AppCompatTextView
    lateinit var linearLayoutLocationDetails: LinearLayoutCompat
    lateinit var layTicketDate: LinearLayoutCompat
    lateinit var layTicketNum: LinearLayoutCompat
    lateinit var linearLayoutOfficerDetails: LinearLayoutCompat
    lateinit var linearLayoutInternalNote: LinearLayoutCompat
    lateinit var linearLayoutCitationDetails: LinearLayoutCompat
    lateinit var linearLayoutVoilationDetails: LinearLayoutCompat
    lateinit var layCheckbox1: LinearLayoutCompat
    lateinit var layCheckbox2: LinearLayoutCompat
    lateinit var layCheckbox3: LinearLayoutCompat
    lateinit var tvVoilationDetailsFocus: AppCompatTextView
    lateinit var tvLocationDetailsFocus: AppCompatTextView
    lateinit var viewLocationDetails: View
    lateinit var viewVoilationDetails: View
    lateinit var mTvType1: AppCompatTextView
    lateinit var mTvType2: AppCompatTextView
    lateinit var mTvType3: AppCompatTextView
    lateinit var mCheckboxType1: AppCompatCheckBox
    lateinit var mCheckboxType2: AppCompatCheckBox
    lateinit var mCheckboxType3: AppCompatCheckBox
    lateinit var mTextViewSignName: AppCompatTextView
    lateinit var mTvTicketNumber: AppCompatTextView
    lateinit var mTvTicketDate: AppCompatTextView
    lateinit var mTvTicketDetailsHide: AppCompatTextView
    lateinit var tvEnforcementTitle: AppCompatTextView
    lateinit var layTicketDetailsHide: LinearLayoutCompat
    lateinit var mlinearLayoutCheckBox: LinearLayoutCompat
    lateinit var imageViewSignature: AppCompatImageView
    lateinit var appCompatTextViewTicketType: AppCompatTextView
    lateinit var fabClearButton: FloatingActionButton
    lateinit var mViewPagerBanner: ViewPager
    lateinit var pagerIndicator: LinearLayoutCompat
    //End of UI Views

    private var mBannerAdapter: ViewPagerBannerAdapter? = null
    private var mShowBannerCount = 0
    private var mDotsCount = 0
    private var mDots: Array<ImageView?>? = null
    private var mTimer: Timer? = null
    private var mFetched: List<CitationBookletModel?>? = ArrayList()
    private var mTicketType: String? = null
    private var mTicketType2: String? = null
    private var mTicketType3: String? = null
    private var mSelectedMake: String? = ""
    private var mSelectedModel: String? = ""
    private var model_lookup_code: String? = ""
    private var mSelectedColor: String? = ""
    private var mStreetLookupCode: String? = ""
    private var mLprNumber: String? = ""
    private var mViolationCode: String? = ""
    private var mViolationCodeCamera: String? = ""
    private var mGisAddress: String? = ""
    private var mTimingRecordRemarkValue: String? = ""
    private var mTimingRecordRemarkValueCameraFeed: String? = ""
    private var mbuttonClickScanPage: Int? = -1
    private var mTimingTireStem: String? = ""
    private var mLockCitation = ""
    private var isCheckNumberPlateFormat = ""
    private var mSpaceId: String? = ""
    private var picUri: Uri? = null
    private var tempUri: String? = null
    private var mExpiryYear: String? = "00"
    private var mExpiryMonth: String? = "00"
    private var mLastSecondCheckMessage: String? = ""
    private var mImageCount = 0
    private var counter = 1
    private val myCalendar = Calendar.getInstance()
    private var mViolationListSelectedItem: DatasetResponse? = null
    private var mWelcomeFormData: WelcomeForm? = null
    private var lockLprModel: LockLprModel? = null
    private var isIssue = false
    private var reIssueTimeImageLinkAgain = false
    private var isLastSecondCheck = false
    private var isCallLastSecondCheckAPI = false
    private var mCitationNumberId: String? = null
    private var mTicketActionButtonEvent: String? = null
    private var mState2DigitCode: String? = ""
    private var mSideOfStreet2DigitCode: String? = null
    private var mStartTimeStamp = ""
    private var mZone: String? = "CST"
    private var isOverTimeParkingViolationForTimeLimit: String? = "0"
    private var mTimingID: String? = ""
    private var mStateItem: String? = ""
    private var mCamera2Setting: String? = "NO"
    private var mSendStateEmptyForFullVinNumberAndCopyLast8DigitVinForLpr: String? = "NO"
    private var mDefaultStateItem: String? = ""
    private var mDirectionItem: String? = ""
    private var mBodyStyleItem: String? = ""
    private var mDecalYearItem: String? = ""
    private var mStreetItem: String? = ""
    private var mBeatItem: String? = ""
    private var mSideItem: String? = ""
    private var mMeterNameItem: String? = ""
    private var mRemarkItem: String? = ""
    private var mRemark1Item: String? = ""
    private var mRemark2Item: String? = ""
    private var mNoteItem: String? = ""
    private var mNote1Item: String? = ""
    private var mNote2Item: String? = ""
    private var mEscalatedLprNumber: String? = ""
    private var mEscalatedState: String? = ""
    private var mVendorName: String? = ""
    private var mSelectedMakeValue: String? = ""
    private var mLotItem: String? = ""
    private var mPBCZone: String? = ""
    private var mLocationItem: String? = ""
    private var mViolationCodetem: String? = ""
    private var mCityZone: String? = ""
    private var mSelectedCityZone: String? = ""
    private var mBlock: String? = ""
    private var mFromScreen: String? = ""
    private var mVin: String? = ""
    private var mViolation: String? = ""
    private var mBodyStyleCodeItem: String? = ""
    private var body_style_lookup_code: String? = ""
    private var mPayBySpaceExpireMeterValue: String? = "NO"
    private var mSaveRemark: String? = ""
    private var lateFineDays: Int = 0
    private var mZoneMandatoryForViolationCode: String = "NO"
    private var lateFineDaysCitationForm: Int = 0
    private var amountDaysCitationForm: Int = 0
    private var lateFine30Days: Int = 0
    private var lateFine45Days: Int = 0
    private var lateFine15Days: Int = 0
    private var amountDays: Int = 0
    private var sanctionsType: Int = 0
    private var lateFine15DaysCitationForm: Int = 0
    private var mCitationImagesLinks: MutableList<String> = ArrayList()
    private var selectedCityZone: String? = ""
    private var selectedZoneCode: String? = ""

    //    private var mDatasetList: DatasetDatabaseModel? = DatasetDatabaseModel()
    private var mDatasetStreetList: List<DatasetResponse>? = ArrayList()
    private val mModelList: MutableList<DatasetResponse>? = ArrayList()
    private var mCitationLayout: List<CitationLayoutData>? = ArrayList()
    private var mApplicationVinList: MutableList<DatasetResponse> = ArrayList()
    private var latestLayoutComments = 0
    private var latestLayoutOfficer = 0
    private var latestLayoutVehicle = 0
    private var latestLayoutLocation = 0
    private var latestLayoutViolation = 0
    private val name = arrayOfNulls<AppCompatAutoCompleteTextView>(10)
    private var isMeterReqiredViolation = false
    private var isPBCReqiredViolation = false
    private val unUploadFacsimileImage = UnUploadFacsimileImage()

    private var cit_numPrint = false
    private var timestampPrint = false
    private var warningPrint = false
    private var bootPrint = false
    private var drive_offPrint = false
    private var officer_namePrint = false
    private var badge_idPrint = false
    private var beatPrint = false
    private var squadPrint = false
    private var agencyPrint = false
    private var zonePrint = false
    private var ObservationTimePrint = false
    private var TimeFieldPrint = false
    private var DayOfWeekPrint = false
    private var codePrint2010 = false
    private var hearingDate = false
    private var hearingThresholdDay: Int = 25
    private var hearingDescription = false
    private var isOfficerDescription = false
    private var bottomLabelPrint = false
    private var isImageAsBase64String = false
    private var isImageAsBase64Count = 1
    private var MaxCharcterLimitForComment = 80
    val makePrint = BooleanArray(1)
    val statePrint = BooleanArray(1)
    val modelPrint = BooleanArray(1)
    val colorPrint = BooleanArray(1)
    val lp_numberPrint = BooleanArray(1)
    val expiryPrint = BooleanArray(1)
    val body_stylePrint = BooleanArray(1)
    val due_45_daysPrint = BooleanArray(1)
    val vio_type_codePrint = BooleanArray(1)
    val vio_type_descriptionPrint = BooleanArray(1)
    val vio_typePrint = BooleanArray(1)
    val costPrint = BooleanArray(1)
    val parkingFeePrint = BooleanArray(1)
    val citationFeePrint = BooleanArray(1)
    val markTimePrint = BooleanArray(1)
    val total_due_now = BooleanArray(1)
    val pay_at_online = BooleanArray(1)
    val decal_yearPrint = BooleanArray(1)
    val due_30_daysPrint = BooleanArray(1)
    val vin_numberPrint = BooleanArray(1)
    val due_15_daysPrint = BooleanArray(1)
    val decal_numberPrint = BooleanArray(1)
    val late_finePrint = BooleanArray(1)
    val streetPrint = BooleanArray(1)
    val descriptionPrint = BooleanArray(1)
    val lotPrint = BooleanArray(1)
    val codePrint = BooleanArray(1)
    val blockPrint = BooleanArray(1)
    val violationPrint = BooleanArray(1)
    val directionPrint = BooleanArray(1)
    val note_2Print = BooleanArray(1)
    val note_3Print = BooleanArray(1)
    val sidePrint = BooleanArray(1)
    val finePrint = BooleanArray(1)
    val meterPrint = BooleanArray(1)
    val spacePrint = BooleanArray(1)
    val cityZonePrint = BooleanArray(1)
    val note_1Print = BooleanArray(1)
    val remark_1Print = BooleanArray(1)
    val remark_2Print = BooleanArray(1)
    val remark_3Print = BooleanArray(1)
    val locationPrint = BooleanArray(1)

    private var dueCost: String = "0.0"
    private var dueParkingFee: String = "0.0"
    private var dueCitationFee: String = "0.0"
    private var dueTotalNow: String = "0.0"
    private var payAtOnline: String = ""
    private var exportCode: String = "0.0"
    private var code2010: String = "2010"
    private var mCitationCountForWarning: String = "-1"
    private var mWarningMessageForLaMetro: String = ""

    private var isActivityInForeground = false

    private val mPrintOrderMap = HashMap<String, PrintLayoutModel>()
    private var mApplicationListZone: List<ZoneStat>? = null
    private var bannerList: MutableList<CitationImagesModel?>? = ArrayList()
    private var downloadedTimingBannerList: MutableList<CitationImagesModel?>? = ArrayList()
    private var mList: List<CitationImagesModel> = ArrayList()
    private var unpaidCitationCount: Int = -1
    private var fineCalculatedField: String = ""
    private var mTimingImages: MutableList<String> = ArrayList()
    private var settingsList: List<DatasetResponse>? = ArrayList()
    private val mCameraImages: MutableList<String> = ArrayList()
    private var mViolationExtraAmount: Double = 0.0
    private var mLotBranchId: String = ""
    private var mLotlookupCode: String = ""

    private var mSelectedBeatStat: BeatStat? = null

    private var vehicleStickerInfo: VehicleInfoModel? = null

    fun registerActivityResultLauncher() {
        ocrActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    //you will get result here in result.data
                    val vinOCRText = result!!.data!!.getStringExtra("OCR_TEXT")
                    //mAutoComTextViewVinNum!!.setText(vinOCRText)
                    setCameraImages()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager =
            permissionFactory.create(caller = this, context = requireContext(), fragment = this)

        // create a session tied to this fragment lifecycle
        session = cameraHelper.createSession(fragment = this)

        registerActivityResultLauncher()
    }


    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCitationFormScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        linearLayoutVehicleDetails = binding.llVehicleDetails
        appCompatTextViewTicketNumberTitle = binding.txtTicketNumber
        linearLayoutLocationDetails = binding.layoutContentLprDetails2InternalNote.llLocationDetails
        layTicketDate = binding.layTicketDate
        layTicketNum = binding.layTicketNum
        linearLayoutOfficerDetails = binding.llOfficerDetails
        linearLayoutInternalNote = binding.layoutContentLprDetails2InternalNote.llMainInternalNote
        linearLayoutCitationDetails =
            binding.layoutContentLprDetails2CitationDetails.llCitationDetails
        linearLayoutVoilationDetails =
            binding.layoutContentLprDetails2InternalNote.llVoilationDetails
        layCheckbox1 = binding.layCheckbox1
        layCheckbox2 = binding.layCheckbox2
        layCheckbox3 = binding.layCheckbox3
        tvVoilationDetailsFocus =
            binding.layoutContentLprDetails2InternalNote.tvVoilationDetailsFocus
        tvLocationDetailsFocus = binding.layoutContentLprDetails2InternalNote.tvLocationDetailsFocus
        viewLocationDetails = binding.layoutContentLprDetails2InternalNote.viewLocationDetails
        viewVoilationDetails = binding.layoutContentLprDetails2InternalNote.viewVoilationDetails
        mTvType1 = binding.tvType1
        mTvType2 = binding.tvType2
        mTvType3 = binding.tvType3
        mCheckboxType1 = binding.checkboxType1
        mCheckboxType2 = binding.checkboxType2
        mCheckboxType3 = binding.checkboxType3
        mTextViewSignName = binding.layoutContentLprDetails2CitationDetails.txtPersonSignature
        mTvTicketNumber = binding.tvTicketNumber
        mTvTicketDate = binding.tvTicketDate
        mTvTicketDetailsHide = binding.tvTicketDetailsHide
        tvEnforcementTitle = binding.tvEnforcementTitle
        layTicketDetailsHide = binding.layTicketDetailsHide
        mlinearLayoutCheckBox = binding.linearLayoutCheckBox
        imageViewSignature = binding.layoutContentLprDetails2CitationDetails.imgSignature
        appCompatTextViewTicketType = binding.textTicketType
        fabClearButton = binding.fabClear
        mViewPagerBanner =
            binding.layoutContentLprDetails2CitationDetails.layoutContentBanner.pagerBanner
        pagerIndicator =
            binding.layoutContentLprDetails2CitationDetails.layoutContentBanner.viewPagerCountDots
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun initViewLifecycleScope() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainActivityViewModel.setToolbarVisibility(true)
                    mainActivityViewModel.setToolbarComponents(
                        showBackButton = true, showLogo = true, showHemMenu = false
                    )
                }

                launch {
                    citationFormScreenViewModel.lastSecondCheckResponse.collect(::consumeResponse)
                }

                launch {
                    citationFormScreenViewModel.downloadBitmapImageResponse.collect(::consumeResponse)
                }

                launch {
                    citationFormScreenViewModel.uploadAllImagesResponse.collect(::consumeResponseForImageUpload)
                }
            }
        }
    }

    override fun initialiseData() {
        // Observe result from back stack
        nav.observeFragmentResult("bundle") { bundle ->
            // Handle the result here
            val requiredText = bundle?.getString("edit")

            try {
                if (requiredText != null) {
                    if (requiredText.equals("location", ignoreCase = true)) {
                        linearLayoutLocationDetails.requestFocus()
                        linearLayoutLocationDetails.isFocusable = true
                    } else if (requiredText.equals("voilation", ignoreCase = true)) {
                        linearLayoutVoilationDetails.requestFocus()
                        linearLayoutVoilationDetails.isFocusable = true
                    } else if (requiredText.equals("remark", ignoreCase = true)) {
                        try {
                            mEtLocationRemarks?.requestFocus()
                            mEtLocationRemarks?.isFocusable = true
                        } catch (e: Exception) {
                        }
                    } else if (requiredText.equals("note", ignoreCase = true)) {
                        try {
                            mEtLocationNotes?.requestFocus()
                            mEtLocationNotes?.isFocusable = true
                        } catch (e: Exception) {
                        }
                    } else if (requiredText.equals("edit_form", ignoreCase = true)) {
                        tvEnforcementTitle.requestFocus()
                        tvEnforcementTitle.isFocusable = true
                    } else if (requiredText.equals("vehicle", ignoreCase = true)) {
                        linearLayoutVehicleDetails.requestFocus()
                        linearLayoutVehicleDetails.isFocusable = true
                    }
                    // TODO LOCK BY CODE
//                    SM only when they come back from preview to citation form again then enable the lock features
//                    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)
//                        ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)
//                        ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
//                        ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)
//                        ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)
//                        ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true)
//                        ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true)){
                    if (sharedPreference.read(
                            SharedPrefKey.CITATION_LOCK_PARTIALLY_LOCK, ""
                        ).equals("unlock")
                    ) {
                        mLockCitation = "lock"
                        setLprLock(lockLprModel, requireContext(), sharedPreference)
                        sharedPreference.write(
                            SharedPrefKey.LOCKED_LPR_BOOL, true
                        )//true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        clearShardValueForPrintValue(sharedPreference)

        init()
        AppUtils.printQueryStringBuilder.clear()
        AppUtils.clearYAxisSet()
        AppUtils.clearDrawableElementList()
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)
        ) {
            fabClearButton!!.visibility = View.VISIBLE
        }
//        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true))
//        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true))
//        {
//            mLockLotBasedOnAgencyKansasCity()
//        }

        try {
            ApiLogsClass.writeApiPayloadTex(
                requireContext(), " Citation form ${requireContext().getAppVersionName()}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setAccessibilityForComponents()
    }

    override fun setupClickListeners() {
        binding.layoutContentLprDetails2CitationDetails.btnPreview.setOnClickListener {
            try {
                val mAddTimingRequest = AddTimingRequest()
                mAddTimingRequest!!.block =
                    if (mAutoComTextViewBlock != null && mAutoComTextViewBlock!!.maxEms == 1) mAutoComTextViewBlock!!.text.toString() else ""
                mAddTimingRequest!!.street =
                    if (mAutoComTextViewStreet != null && mAutoComTextViewStreet!!.maxEms == 1) mAutoComTextViewStreet!!.text.toString() else ""
                mAddTimingRequest!!.mLot =
                    if (mAutoComTextViewLot != null && mAutoComTextViewLot!!.maxEms == 1) mAutoComTextViewLot!!.text.toString() else ""
                mAddTimingRequest!!.side =
                    if (mAutoComTextViewDirection != null && mAutoComTextViewDirection!!.maxEms == 1) mAutoComTextViewDirection!!.text.toString() else ""
                mAddTimingRequest!!.mViolation =
                    if (mAutoComTextViewAbbrCode != null && mAutoComTextViewAbbrCode!!.maxEms == 1) mAutoComTextViewAbbrCode!!.text.toString() else ""
                mAddTimingRequest!!.remark =
                    if (mSaveRemark != null && mEtLocationRemarks != null && mEtLocationRemarks!!.maxEms == 1) mEtLocationRemarks!!.text.toString() else ""
                mAddTimingRequest!!.remark1 =
                    if (mEtLocationRemarks1 != null && mEtLocationRemarks1!!.maxEms == 1) mEtLocationRemarks1!!.text.toString() else ""
                mAddTimingRequest!!.remark2 =
                    if (mEtLocationRemarks2 != null && mEtLocationRemarks2!!.maxEms == 1) mEtLocationRemarks2!!.text.toString() else ""
                mAddTimingRequest!!.mMarkTime =
                    if (mEtMarkTime != null && mEtMarkTime!!.maxEms == 1) mEtMarkTime!!.text.toString() else ""
                sharedPreference.write(SharedPrefKey.TIMING_DATA, mAddTimingRequest)
            } catch (e: Exception) {
                e.printStackTrace()
            }
//                }
            // Check selected state is default state and lpr number format is correct
            isDefaultStateAndNumberPlateFormatValidation()
        }

        binding.ivCamera.setOnClickListener {
            requestPermission()
        }

        binding.layoutContentLprDetails2CitationDetails.ivCameraBottom.setOnClickListener {
            requestPermission()
        }

        fabClearButton.setOnClickListener {
            ClearFileds()
        }

        binding.layoutContentLprDetails2CitationDetails.btnClear.setOnClickListener {
            ClearFileds()
        }

        imageViewSignature.setOnClickListener {
            setSignatureView()
        }

        mCheckboxType1.setOnClickListener {
            if (mCheckboxType1.isChecked) {
                mTicketType = mTvType1.text.toString()
                setViolationBaseData(mViolationListSelectedItem, -1)

                sharedPreference.write(
                    SharedPrefKey.LA_METRO_WARNING_MESSAGE_PRINT, mWarningMessageForLaMetro
                )
                //                    mCheckboxType2.setChecked(false);
//                    mCheckboxType3.setChecked(false);
            } else {
                mTicketType = ""
                setViolationBaseData(mViolationListSelectedItem, -1)
                sharedPreference.write(
                    SharedPrefKey.LA_METRO_WARNING_MESSAGE_PRINT, ""
                )
            }
        }

        mCheckboxType2.setOnClickListener {
            if (mCheckboxType2.isChecked) {
                mTicketType2 = mTvType2.text.toString()
                setViolationBaseData(mViolationListSelectedItem, -1)
                //                    mCheckboxType1.setChecked(false);
//                    mCheckboxType3.setChecked(false);
            } else {
                mTicketType2 = ""
                setViolationBaseData(mViolationListSelectedItem, -1)
            }
        }

        mCheckboxType3.setOnClickListener {
            mTicketType3 = if (mCheckboxType3.isChecked) {
                mTvType3.text.toString()
                //                    mCheckboxType2.setChecked(false);
//                    mCheckboxType1.setChecked(false);
            } else {
                ""
            }
        }
    }

    private fun setAccessibilityForComponents() {
        layCheckbox1.setAccessibilityForCheckboxUnderLinearLayout(mCheckboxType1, mTvType1)
        layCheckbox2.setAccessibilityForCheckboxUnderLinearLayout(mCheckboxType2, mTvType2)
        layCheckbox3.setAccessibilityForCheckboxUnderLinearLayout(mCheckboxType3, mTvType3)
    }

    private fun init() {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = citationFormScreenViewModel.getCitationLayout()
            sharedPreference.write(
                SharedPrefKey.CITATION_LOCK_PARTIALLY_LOCK, ""
            )
            if (response != null) {
                if (response.data!!.isNotEmpty()) {
                    mCitationLayout = response.data
                    createDynamicView()
                }
            }
            try {
                mStartTimeStamp = splitDateLpr("")
                isDataSetListEmpty()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    private fun callPayByPlateAPI() {
//        {{local}}/analytics/mobile/pay_by_plate?zone=Indigo&Park&lp_number=VIM112
        val endPoint =
            ("zone=" + mAutoComTextViewZone?.text.toString() + "&Park&lp_number=" + mAutoComTextViewLicensePlate?.text.toString())

        citationFormScreenViewModel.getLastSecondCheckAPI(
            zone = mAutoComTextViewZone?.text.toString(),
            lpNumber = mAutoComTextViewLicensePlate?.text.toString(),
            park = ""
        )
    }//set image list adapter//

    //set image list adapter
    private fun getLprVehicleDataFromIntent() {
        val bundle = arguments
        if (bundle != null) {
            //DialogUtil.showLoader(requireContext(), getString(R.string.scr_message_please_wait))

            if (bundle.containsKey(INTENT_KEY_VEHICLE_INFO)) {
                vehicleStickerInfo =
                    bundle.getSerializable(INTENT_KEY_VEHICLE_INFO) as VehicleInfoModel?
            }

            if (bundle.getString("from_scr") != null) {
                mFromScreen = bundle.getString("from_scr")
                if (bundle.getString("from_scr") == "ticket_details") {
                    isIssue = true
                    reIssueTimeImageLinkAgain = true
                    mCitationNumberId = bundle.getString("booklet_id")
                    mTvTicketDate.text = AppUtils.getCurrentDateTimeForCitationForm(mStartTimeStamp)
                    mTvTicketDate.tag = AppUtils.getCurrentDateTimeForPrint(mStartTimeStamp)
                    val mId = mCitationNumberId
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(2000)
                        mTicketActionButtonEvent = bundle.getString("btn_action")
                        getDatasetFromDb(mId, isIssue)
                        setBannerImageAdapter()
                        //set image list adapter
                        setCameraImages()
                        setCitationSignatureImageData()

                        //We are locking user when it comes from void & reissue and the site is New Orleans
                        if (mTicketActionButtonEvent.equals(
                                "VoidReissue", true
                            ) && BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
                            )
                        ) {
                            mLockCitation = "lock"
                        }

                        /*** When get citation value is lock and officer will select violation then
                         * locked this citation
                         */
                        if (mLockCitation.equals("lock", ignoreCase = true)) {
                            setLprLock(lockLprModel, requireContext(), sharedPreference)
                            sharedPreference.write(
                                SharedPrefKey.LOCKED_LPR_BOOL, true
                            )//true
                        } else {
                            sharedPreference.write(
                                SharedPrefKey.LOCKED_LPR_BOOL, false
                            )
                        }

                        //dismissLoader()
                    }
                } else if (bundle.getString("from_scr").equals("WelcomeUnUpload")) {


                    viewLifecycleOwner.lifecycleScope.launch {
                        val mIssuranceModelList =
                            citationFormScreenViewModel.getCitationInsurranceUnuploadCitation() as List<CitationInsurranceDatabaseModel>
                        mCitationNumberId = mIssuranceModelList.get(0).citationNumber
                        mTvTicketDate.text =
                            AppUtils.getCurrentDateTimeForCitationForm(mStartTimeStamp)
                        mTvTicketDate.tag = AppUtils.getCurrentDateTimeForPrint(mStartTimeStamp)

//                    mLockCitation = "lock"
//                    sharedPreference.write(
//                        SharedPrefKey.LOCKED_LPR_BOOL,
//                        true
//                    )
//                    sharedPreference.write(
//                        SharedPrefKey.CITATION_LOCK_PARTIALLY_LOCK,"lock")
                        val mId = mCitationNumberId
                        isIssue = true
                        mTicketActionButtonEvent = "UnUpload"

                        delay(2000)
                        getDatasetFromDb(mId, true)
                        setBannerImageAdapter()
                        //set image list adapter
                        setCameraImages()
                        setCitationSignatureImageData()
                        //dismissLoader()
                    }
                } else {
                    sharedPreference.write(SharedPrefKey.isReissueTicket, "false")
                }
            }
            if (!isIssue) {
                mSelectedMake = bundle.getString("make")
                mSelectedModel = bundle.getString("model")
                mSelectedColor = bundle.getString("color")
                mLprNumber = bundle.getString("lpr_number")
                mVin = bundle.getString("Vin")
                mVin = bundle.getString("vinNumber")
                mViolationCode = bundle.getString("violation_code")
                mViolationCodeCamera = bundle.getString("violation_code_camera")
                mGisAddress = bundle.getString("address")
                mBodyStyleItem = bundle.getString("BodyStyle")
                mTimingRecordRemarkValue = bundle.getString("timing_record_value")

                val remarkValue = bundle.getString("timing_record_value_camera")
                if (!remarkValue.isNullOrEmpty() && remarkValue.length > 6) {
                    mTimingRecordRemarkValueCameraFeed = remarkValue
                }
                mbuttonClickScanPage = bundle.getInt("SanctionButtonClick", 0)
                mbuttonClickScanPage?.let {
                    if (it > 0) {
                        when (it) {
                            1 -> mCheckboxType1.isChecked = true
                            2 -> mCheckboxType1.isChecked = true
                            else -> ""
                        }
                    } else ""
                } ?: ""
                mPayBySpaceExpireMeterValue = bundle.getString("VIOLATION")
                mTimingTireStem = bundle.getString("timing_tire_stem_value")
                mSideItem = bundle.getString("SideItem")
                mStateItem = if (bundle.containsKey("State")) bundle.getString("State") else ""
                lockLprModel = LockLprModel()
                lockLprModel?.mLprNumber = mLprNumber
                lockLprModel?.mMake = mSelectedMake
                lockLprModel?.mModel = mSelectedModel
                lockLprModel?.mColor = mSelectedColor
                lockLprModel?.mAddress = mGisAddress
                lockLprModel?.mViolationCode = mViolationCode
                lockLprModel?.ticketCategory = API_CONSTANT_TICKET_CATEGORY_CITATION
                mViolationCodetem = mViolationCode
                if (bundle.containsKey("Street")) {
                    mStreetItem = bundle.getString("Street")
                }
                if (bundle.containsKey("Block")) {
                    mBlock = bundle.getString("Block")
                }
                if (bundle.containsKey("TimingID")) {
                    mTimingID = bundle.getString("TimingID")
                }
                if (bundle.containsKey("Lot")) {
                    mLotItem = bundle.getString("Lot")
                }
                if (bundle.containsKey(INTENT_KEY_TIMING_IMAGES)) {
                    mTimingImages =
                        bundle.getSerializable(INTENT_KEY_TIMING_IMAGES) as MutableList<String>
                    logD(
                        "==>TimingImageList:",
                        ObjectMapperProvider.instance.writeValueAsString(mTimingImages)
                    )
                } else {

                }
                if (bundle.containsKey(INTENT_KEY_TIMING_IMAGES_BASE64)) {
                    mTimingImages.clear()
                    val base64List = ImageCache.base64Images
                    mTimingImages = base64List

                }

                if (bundle.containsKey(INTENT_KEY_UNPAID_CITATION_COUNT)) {
                    unpaidCitationCount = bundle.getInt(INTENT_KEY_UNPAID_CITATION_COUNT, 0)
                }

                if (bundle.getString("from_scr") != null && bundle.getString("from_scr") == "PAYBYSPACE") {
                    mLotItem = bundle.getString("Lot")
                    mLotItem = bundle.getString("Location")
                    mSpaceId = bundle.getString("Space_id")
                    mMeterNameItem = bundle.getString("Meter")
                    mPBCZone = bundle.getString("Zone")
                    mStreetItem = bundle.getString("Street")
                    mBlock = bundle.getString("Block")
                    mDirectionItem = bundle.getString("Direction")
                    mPayBySpaceExpireMeterValue = "METER EXPIRED"
                    mPayBySpaceExpireMeterValue = ""
                }
                if (bundle.getString("from_scr") != null && bundle.getString("from_scr") == Constants.DIRECTED_ENFORCEMENT) {
                    mLotItem = bundle.getString("Lot")
                    mLotItem = bundle.getString("Location")
                    mSpaceId = bundle.getString("Space_id")
                    mMeterNameItem = bundle.getString("Meter")
                    mPBCZone = bundle.getString("Zone")
                    mStreetItem = bundle.getString("Street")
                    mBlock = bundle.getString("Block")
                    mDirectionItem = bundle.getString("Direction")
                    mPayBySpaceExpireMeterValue = "METER EXPIRED"
                    mPayBySpaceExpireMeterValue = ""
                    mTimingImages.clear()
                    mTimingImages = ImageCache.baseLinkImage
                } else if (bundle.getString("from_scr") != null && bundle.getString("from_scr")
                        .equals("BOOTACTIVITY")
                ) {
                    mStateItem = bundle.getString("State")
                    mPBCZone = bundle.getString("Zone")
                    mStreetItem = bundle.getString("Street")
                    mSideItem = bundle.getString("SideItem")
                    mBlock = bundle.getString("Block")
                    mRemarkItem = bundle.getString("Remark")
                }
                if (bundle.containsKey("Note")) {
                    mNoteItem = bundle.getString("Note")
                }
                if (bundle.containsKey("Note1")) {
                    mNote1Item = bundle.getString("Note1")
                }
                if (bundle.containsKey("Note2")) {
                    mNote2Item = bundle.getString("Note2")
                }
                if (bundle.containsKey("EscalatedLprNumber")) {
                    mEscalatedLprNumber = bundle.getString("EscalatedLprNumber")
                }
                if (bundle.containsKey("EscalatedState")) {
                    mEscalatedState = bundle.getString("EscalatedState")
                }

                if (bundle.containsKey("vendor_name")) {
                    mVendorName = bundle.getString("vendor_name")
                }
                if (bundle.containsKey("ExpireYear_CameraViolation")) {
                    val expireYearCameraViolation = bundle.getString("ExpireYear_CameraViolation")
                    mAutoCompleteDate?.setText(
                        expireYearCameraViolation?.getMonthForSetText()
                    )
                    mExpiryYear = expireYearCameraViolation?.getYearFotSetText()
                }

                //TODO Vigilant image download
                if (mVendorName!!.equals(
                        "Vigilant", ignoreCase = true
                    ) && mTimingImages.isNotEmpty()
                ) {
                    try {
                        for (model in mTimingImages!!) {
                            DownloadingVigilantBitmapFromUrl().execute(
                                model!!.toString()
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (mTimingImages.isNotEmpty()) {
                    if (mTimingImages.get(0).length > 200) {
                        isImageAsBase64String = true
                        decodeBase64StringToBitmapAsyn().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, mTimingImages.get(0)
                        )
                    } else {
                        try {
                            callDownloadBitmapApi()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    delay(2000)
                    getDatasetFromDb("0", false)
                    setBannerImageAdapter()
                    //set image list adapter
                    setCameraImages()
                    setCitationSignatureImageData()
                    //dismissLoader()
                }
            }
        }
    }

    fun setBannerImageAdapter() {
        mBannerAdapter = ViewPagerBannerAdapter(
            requireContext(), object : ViewPagerBannerAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        citationFormScreenViewModel.deleteTempImagesWithId(bannerList!![position]!!.id)
                        bannerList!![position]!!.citationImage?.let {
                            citationFormScreenViewModel.deleteUnUploadCitationImages(
                                it
                            )
                        }
                    }

                    if (bannerList!![position]!!.status == 1) {
                        try {
                            for ((linkIndex, linkImage) in mTimingImages.withIndex()) {
                                logD(
                                    "delete link images",
                                    linkImage + " --  " + bannerList!![position]!!.citationImage
                                )

                                if (linkImage.equals(bannerList!![position]!!.timeImagePath)) {
                                    mTimingImages!!.removeAt(linkIndex)

                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    setCameraImages()

                }
            })
    }

    private fun callDownloadBitmapApi() {
        if (NetworkCheck.isInternetAvailable(requireContext())) {
            if (mTimingImages != null && mTimingImages.size > 0) {
                val downloadBitmapRequest = DownloadBitmapRequest()

                if (mFromScreen!!.equals("CAMERAFEEDVIOLATION", ignoreCase = true)) {
                    downloadBitmapRequest.downloadType =
                        API_CONSTANT_DOWNLOAD_TYPE_RAW_CAMERA_FEED_IMAGES
                } else {
                    downloadBitmapRequest.downloadType = API_CONSTANT_DOWNLOAD_TYPE_TIMING_IMAGES
                }

                //downloadBitmapRequest.downloadType = "CitationImages"
                val links = Links()
                if (mTimingImages.size > 0) links.img1 = mTimingImages[0]
                if (mTimingImages.size > 1) links.img2 = mTimingImages[1]
                if (mTimingImages.size > 2) links.img3 = mTimingImages[2]
                if (mTimingImages.size > 3) links.img4 = mTimingImages[3]
                if (mTimingImages.size > 4) links.img5 = mTimingImages[4]
                if (mTimingImages.size > 5) links.img6 = mTimingImages[5]
                if (mTimingImages.size > 6) links.img7 = mTimingImages[6]
                if (mTimingImages.size > 7) links.img8 = mTimingImages[7]
                if (mTimingImages.size > 8) links.img9 = mTimingImages[8]
                if (mTimingImages.size > 9) links.img10 = mTimingImages[9]
                downloadBitmapRequest.links = links

                citationFormScreenViewModel?.callDownloadBitmapAPI(downloadBitmapRequest)

            } else {
                requireContext().toast(
                    getString(R.string.err_msg_down_load_image)
                )
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun setCitationSignatureImageData() {
        mTvTicketDate.text = AppUtils.getCurrentDateTimeForCitationForm(mStartTimeStamp)
        mTvTicketDate.tag = AppUtils.getCurrentDateTimeForPrint(mStartTimeStamp)

        //getting signature path
        try {
            var mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
            mPath = mPath + Constants.CAMERA + "/" + getSignatureFileNameWithExt()
            val file = File(mPath)
            if (file.exists() && !TextUtils.isEmpty(mPath)) {
                imageViewSignature.setImageURI(Uri.fromFile(File(mPath)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /***
     * fetch All Dataset dropdown value from Database when come back from Ticket Page
     */
    private fun getDatasetFromDb(id: String?, status: Boolean) {
        try {
            //get Activity log data
            mWelcomeFormData = mainActivityViewModel.getWelcomeForm()
            try {
                mTvOfficerName?.setText(
                    mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName
                )
                mTvOfficerName?.isEnabled = false
                mTvOfficerName?.isFocusable = false
            } catch (e: Exception) {
            }
            try {
                mTvOfficerId?.setText(splitID(mWelcomeFormData!!.siteOfficerId!!))
                mTvOfficerId?.isEnabled = false
                mTvOfficerId?.isFocusable = false
            } catch (e: Exception) {
            }
            try {
                mTvBadgeId?.setText(mWelcomeFormData!!.officerBadgeId)
                mTvBadgeId?.isEnabled = false
                mTvBadgeId?.isFocusable = false
            } catch (e: Exception) {
            }
            try {
                mTvBeat?.setText(mWelcomeFormData!!.officerBeatName)
                mTvBeat?.isEnabled = true
                mTvBeat?.isFocusable = true
                mTvBeat?.isClickable = true
                mTvBeat?.setSelection(mTvBeat!!.length());
            } catch (e: Exception) {
            }
            try {
                mTvSquad?.setText(mWelcomeFormData!!.officerSquad)
                mTvSquad?.isEnabled = false
                mTvSquad?.isFocusable = false
            } catch (e: Exception) {
            }
            try {
                mTvZone?.setText(mWelcomeFormData!!.officerZoneName)
                mTvZone?.isEnabled = false
                mTvZone?.isFocusable = false
            } catch (e: Exception) {
            }
            try {
                mTvAgency?.setText(mWelcomeFormData?.agency)
                mTvAgency?.isEnabled = false
                mTvAgency?.isFocusable = false
            } catch (e: Exception) {
            }
            try {
                if (mAutoComTextViewPBCZone != null) {
                    mAutoComTextViewPBCZone?.setText(if (mWelcomeFormData?.paymentZoneName != null) mWelcomeFormData?.paymentZoneName else "")
                }
                if (mAutoComTextViewZone != null) {
                    mAutoComTextViewZone?.setText(if (mWelcomeFormData!!.officerZone != null) mWelcomeFormData?.officerZone else "")
                }
            } catch (e: Exception) {
            }
            //mTvExpiration.setText("");
            mTextViewSignName.text =
                mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName
            mTextViewSignName.isEnabled = false
            mTextViewSignName.isFocusable = false

            //Set ADA
            mTvOfficerName?.setCustomContentDescription()
            mTvOfficerId?.setCustomContentDescription()
            mTvBadgeId?.setCustomContentDescription()
            mTvBeat?.setCustomContentDescription()
            mTvSquad?.setCustomContentDescription()
            mTvZone?.setCustomContentDescription()
            mTvAgency?.setCustomContentDescription()
            mAutoComTextViewPBCZone?.setCustomContentDescription()
            mAutoComTextViewZone?.setCustomContentDescription()
            mTextViewSignName?.setCustomContentDescription()


            val idNew: String = id.nullSafety()
            //mAutoComTextViewDirection.setText("E");
            try {
                mAutoComTextViewBlock?.setText(mBlock)
            } catch (e: Exception) {
            }
            try {
                mAutoComTextViewStreet?.setText(mStreetItem)
            } catch (e: Exception) {
            }
            //mAutoComTextViewStreet.setText(mTvReverseCoded.getText().toString());
            if (status) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(100)
                    var mIssuranceModel: CitationInsurranceDatabaseModel? =
                        CitationInsurranceDatabaseModel()
                    if (idNew != "0") {
                        mIssuranceModel = citationFormScreenViewModel.getCitationWithTicket(idNew)
                    }
                    /** Check void and reissue and cancel ticket data in on DB or not
                     * if not then get from shrad and set on view
                     * ShardPereference get object and set click on look up screen
                     */
                    /** Check void and reissue and cancel ticket data in on DB or not
                     * if not then get from shrad and set on view
                     * ShardPereference get object and set click on look up screen
                     */
                    if (mIssuranceModel == null) {
                        mIssuranceModel = sharedPreference.read(SharedPrefKey.CITATION_DATAL)
                    }

                    /**
                     * Unupload citation
                     */
                    val mIssuranceModelList =
                        citationFormScreenViewModel.getCitationInsurranceUnuploadCitation()
                    if (mIssuranceModelList!!.size > 0) {
                        mIssuranceModel = mIssuranceModelList[0]
                    }

                    setCitationDataOnUI(mIssuranceModel, idNew)
                }
            }
            if (!status) {
                try {
                    try {
                        var mTimeData: AddTimingRequest? =
                            sharedPreference.readTime(SharedPrefKey.TIMING_DATA, "")
                        if (mTimeData != null) {
                            if (mAutoComTextViewBlock != null && mAutoComTextViewBlock!!.maxEms == 1) {
                                mAutoComTextViewBlock!!.post {
                                    mAutoComTextViewBlock!!.setText(
                                        mBlock
                                    )
                                }
                                mBlock = mTimeData!!.block
                            }
                            if (mAutoComTextViewStreet != null && mAutoComTextViewStreet!!.maxEms == 1) {
                                mAutoComTextViewStreet!!.post {
                                    mAutoComTextViewStreet!!.setText(
                                        mStreetItem
                                    )
                                }
                                mStreetItem = mTimeData!!.street
                            }
                            if (mAutoComTextViewLot != null && mAutoComTextViewLot!!.maxEms == 1) mAutoComTextViewLot!!.post {
                                mAutoComTextViewLot!!.setText(
                                    mTimeData!!.mLot
                                )
                            }
                            if (mAutoComTextViewDirection != null && mAutoComTextViewDirection!!.maxEms == 1) mAutoComTextViewDirection!!.post {
                                mAutoComTextViewDirection!!.setText(
                                    mTimeData!!.side
                                )
                            }
                            if (mAutoComTextViewAbbrCode != null && mAutoComTextViewAbbrCode!!.maxEms == 1) mAutoComTextViewAbbrCode!!.post {
                                mAutoComTextViewAbbrCode!!.setText(
                                    mTimeData!!.mViolation
                                )
                            }
                            if (mEtLocationRemarks != null && mEtLocationRemarks!!.maxEms == 1) mEtLocationRemarks!!.post {
                                mEtLocationRemarks!!.setText(
                                    mTimeData!!.remark
                                )
                            }
                            if (mEtLocationRemarks1 != null && mEtLocationRemarks1!!.maxEms == 1) mEtLocationRemarks1!!.post {
                                mEtLocationRemarks1!!.setText(
                                    mTimeData!!.remark1
                                )
                            }
                            if (mEtLocationRemarks2 != null && mEtLocationRemarks2!!.maxEms == 1) mEtLocationRemarks2!!.post {
                                mEtLocationRemarks2!!.setText(
                                    mTimeData!!.remark2
                                )
                            }
                            if (mEtMarkTime != null && mEtMarkTime!!.maxEms == 1) mEtMarkTime!!.post {
                                mEtMarkTime!!.setText(
                                    mTimeData!!.mMarkTime
                                )
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (mLprNumber != null && mLprNumber!!.length > 3) {
                        mAutoComTextViewLicensePlate?.setText(mLprNumber)
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewDecalNum?.setText(mLprNumber)
                        }
                        mAutoComTextViewLicensePlate?.isAllCaps = true
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewLicensePlate?.isEnabled = true
                            mTvAgency?.isEnabled = true
                            mTvAgency?.isFocusable = true
                        } else {
                            mAutoComTextViewLicensePlate?.isEnabled = false
                            mTvAgency?.isEnabled = false
                            mTvAgency?.isFocusable = false
                        }
                        /*** When get citation value is lock and officer will select violation then
                         * locked this citation
                         */
                        if (mLockCitation.equals("lock", ignoreCase = true)) {
                            setLprLock(lockLprModel, requireContext(), sharedPreference)
                            sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, true)//true
                        } else {
                            sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                        }
                    }

                    /**
                     * Memorial Herman
                     * ticket should be warning when came from scn page
                     */
                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true
                        ) && arguments?.getString("from_scr").toString()
                            .equals("lpr_details") || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true
                        )
                    ) {
                        mCheckboxType1.isChecked = true
                        if (arguments?.containsKey("CitationCount").nullSafety()) {
                            mCitationCountForWarning =
                                arguments?.getString("CitationCount").toString()
                            mRemarkItem = ""
                        }
                    } else if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true
                        ) && arguments?.getString("from_scr").toString().equals("lpr_details")
                    ) {
                        if (arguments?.containsKey("CitationCount").nullSafety()) {
                            mCitationCountForWarning =
                                arguments?.getString("CitationCount").toString()
                            mRemarkItem = ""
                            if (mCitationCountForWarning.equals("1")) {
                                mCheckboxType1.isChecked = true
                            }
                        }
                    }
                    //                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mTvOfficerColor?.setText(mSelectedColor)
                } catch (e: Exception) {
                }
                try {
                    mTvOfficerMake?.setText(mSelectedMake)
                    mTvOfficerMake?.isAllCaps = true
                } catch (e: Exception) {
                }
                try {
                    mTvOfficerModel?.setText(mSelectedModel)
                } catch (e: Exception) {
                }

                setDropdownMakeVehicle(mSelectedMake)
                setDropdownVehicleColour(mSelectedColor)

                //get lastes issue citation booklet
                viewLifecycleOwner.lifecycleScope.launch {
                    mFetched = citationFormScreenViewModel.getCitationBooklet(0)
                    try {
                        if (mFetched != null && mFetched!!.size != 0) {
                            mTvTicketNumber.text = mFetched!![0]!!.citationBooklet
                        } else {
                            //Toast.makeText(this, "size-L:-"+mFetched.size(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                getDatasetList(false)

                // //dismissLoader();
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCitationDataOnUI(
        mIssuranceModel: CitationInsurranceDatabaseModel?, idNew: String
    ) {

        if (mIssuranceModel != null) {
            if (mIssuranceModel.citationData?.vehicle != null) {
                mStateItem = mIssuranceModel.citationData?.vehicle?.state
                mDirectionItem = mIssuranceModel.citationData?.location?.direction
                mBodyStyleItem = mIssuranceModel.citationData?.vehicle?.bodyStyle
                mDecalYearItem = mIssuranceModel.citationData?.vehicle?.decalYear
            }

            if (mIssuranceModel.citationData?.voilation != null && mTicketActionButtonEvent?.equals(
                    "VoidReissue", true
                )
                    .nullSafety() || mIssuranceModel.citationData?.voilation != null && mTicketActionButtonEvent?.equals(
                    "UnUpload", true
                ).nullSafety()
            ) {

                try {
                    mAutoComTextViewCode?.setText(mIssuranceModel.citationData?.voilation?.code.nullSafety())
                    mAutoComTextViewAbbrCode?.setText(mIssuranceModel.citationData?.voilation?.violationCode.nullSafety())
                    /*** When get citation value is lock and officer will select violation then
                     * locked this citation
                     */
                    if (mLockCitation.equals("lock", ignoreCase = true)) {
                        setLprLock(lockLprModel, requireContext(), sharedPreference)
                        sharedPreference.write(
                            SharedPrefKey.LOCKED_LPR_BOOL, true
                        )//true
                    } else {
                        sharedPreference.write(
                            SharedPrefKey.LOCKED_LPR_BOOL, false
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewAmount?.setText(
                        getString(R.string.doller_sign) + " " + mIssuranceModel.citationData?.voilation?.amount
                    )
                } catch (e: Exception) {
                }
                try {
                    mAutoComTextViewAmountDueDate?.setText(
                        getString(R.string.doller_sign) + " " + mIssuranceModel.citationData?.voilation?.amountDueDate
                    )
                } catch (e: Exception) {
                }
                try {
                    mEtLocationDescr?.setText(mIssuranceModel.citationData?.voilation?.locationDescr)
                } catch (e: Exception) {
                }
                try {
//                                    mAutoComTextViewCode.setText(mApplicationList.get(position).getViolationCode());
                } catch (e: Exception) {
                }
                try {
                    AutoComTextViewDueDate?.setText(
                        getString(R.string.doller_sign) + " " + mIssuranceModel.citationData?.voilation?.dueDate
                    )
                } catch (e: Exception) {
                }
                try {
                    AutoComTextViewDueDate30?.setText(
                        getString(R.string.doller_sign) + " " + mIssuranceModel.citationData?.voilation?.dueDate30
                    )
                } catch (e: Exception) {
                }
                try {
                    AutoComTextViewDueDate45?.setText(
                        getString(R.string.doller_sign) + " " + mIssuranceModel.citationData?.voilation?.dueDate45
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewVioTypeCode?.setText(
                        getString(R.string.doller_sign) + " " + mIssuranceModel.citationData?.voilation?.vioTypeCode
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewVioTypeDescription?.setText(
                        getString(R.string.doller_sign) + " " + mIssuranceModel.citationData?.voilation?.vioTypeDescription
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewVioType?.setText(
                        getString(R.string.doller_sign) + " " + mIssuranceModel.citationData?.voilation?.vioType
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            mBeatItem = mIssuranceModel.citationData?.officer?.beat
            mStreetItem = mIssuranceModel.citationData?.location?.street
            mSideItem = mIssuranceModel.citationData?.location?.side
            mRemarkItem = mIssuranceModel.citationData?.locationRemarks
            mRemark1Item = mIssuranceModel.citationData?.locationRemarks1
            mRemark2Item = mIssuranceModel.citationData?.locationRemarks2
            if (mIssuranceModel.citationData?.locationRemarksObserved != null && mIssuranceModel.citationData?.locationRemarksObserved!!.isNotEmpty()) {
                mTimingRecordRemarkValue = mIssuranceModel.citationData?.locationRemarksObserved
            }
            if (mIssuranceModel.citationData?.locationRemarks1Observed != null && mIssuranceModel.citationData?.locationRemarks1Observed!!.isNotEmpty()) {
                mTimingTireStem = mIssuranceModel.citationData?.locationRemarks1Observed
            }
            mNoteItem = mIssuranceModel.citationData?.locationNotes
            mNote1Item = mIssuranceModel.citationData?.locationNotes1
            mNote2Item = mIssuranceModel.citationData?.locationNotes2
            mPBCZone = mIssuranceModel.citationData?.location?.pcbZone
            mCityZone = mIssuranceModel.citationData?.officer?.zone
            mSelectedCityZone = mIssuranceModel.citationData?.officer?.zone
            mMeterNameItem = mIssuranceModel.citationData?.location?.meterName
            mLotItem = mIssuranceModel.citationData?.location?.lot
            mLocationItem = mIssuranceModel.citationData?.location?.location
            mBlock = mIssuranceModel.citationData?.location?.block
            mSpaceId = mIssuranceModel.citationData?.location?.spaceName

            mSelectedMake = mIssuranceModel.citationData?.vehicle?.make
            mSelectedMakeValue = mIssuranceModel.citationData?.vehicle?.makeFullName
            mSelectedModel = mIssuranceModel.citationData?.vehicle?.model
            try {
                mTvOfficerColor?.setText(mIssuranceModel.citationData?.vehicle?.color)
            } catch (e: Exception) {
            }
            try {
                mTvOfficerMake?.setText(mIssuranceModel.citationData?.vehicle?.make)
            } catch (e: Exception) {
            }
            try {
                mTvOfficerModel?.setText(mIssuranceModel.citationData?.vehicle?.model)
            } catch (e: Exception) {
            }
            try {
                if (mAutoCompleteExpiryMonth != null) {
                    mAutoCompleteExpiryMonth?.setText(
                        mIssuranceModel.citationData?.vehicle?.expiration?.getMonthForSetTextByIndex()
                    )
                    mExpiryMonth =
                        mIssuranceModel.citationData?.vehicle?.expiration?.getMonthForSetText()
                    mAutoCompleteDate?.setText(
                        mIssuranceModel.citationData?.vehicle?.expiration?.getYearFotSetText()
                    )
                    mExpiryYear =
                        mIssuranceModel.citationData?.vehicle?.expiration?.getYearFotSetText()
                } else {
                    mAutoCompleteDate?.setText(
                        mIssuranceModel.citationData?.vehicle?.expiration
                    )
                    mExpiryYear = mIssuranceModel.citationData?.vehicle?.expiration
                }
            } catch (e: Exception) {
            }

            try {
                mAutoComTextViewLicensePlate?.setText(
                    mIssuranceModel.citationData?.vehicle?.licensePlate
                )
                mAutoComTextViewLicensePlate?.isAllCaps = true
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                    )
                ) {
                    mAutoComTextViewLicensePlate?.isEnabled = true
                    mTvAgency?.isEnabled = true
                    mTvAgency?.isFocusable = true

                } else {
                    mAutoComTextViewLicensePlate?.isEnabled =
                        sharedPreference.read(SharedPrefKey.VOIDANDREISSUEBYPLATE, false)
                    mTvAgency?.isEnabled =
                        sharedPreference.read(SharedPrefKey.VOIDANDREISSUEBYPLATE, false)
                    mTvAgency?.isFocusable = false
                }

            } catch (e: Exception) {
            }
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    if (!mTicketActionButtonEvent?.equals("UnUpload", true).nullSafety()) {
                        val mLatestBooklet =
                            citationFormScreenViewModel.getCitationBooklet(0) as List<CitationBookletModel>
                        var newTicketNumber = mLatestBooklet.get(0).citationBooklet
                        mLatestBooklet.forEach {
                            if (!newTicketNumber.equals(idNew).nullSafety()) {
                                mTvTicketNumber.text = newTicketNumber
                            } else {
                                citationFormScreenViewModel.updateCitationBooklet(1, idNew)
                                newTicketNumber = mLatestBooklet.get(0).citationBooklet
                            }
                        }
                    } else {
                        mFetched = citationFormScreenViewModel.getCitationBookletByCitation(idNew)
                        mTvTicketNumber.text = idNew
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("newcitation-ticketError", e.message.nullSafety())
                }
            }

//                            mTvTicketDate.setText(mIssuranceModel?.citationData?.getTicketDate());

            try {
                mAutoComTextViewDecalNum?.setText(
                    mIssuranceModel.citationData?.vehicle?.decalNumber
                )
            } catch (e: Exception) {
            }
            try {
                mAutoComTextViewStreet?.setText(
                    mIssuranceModel.citationData?.location?.street
                )
            } catch (e: Exception) {
            }// Najib
            try {
                mAutoComTextViewStreet?.setText(
                    mIssuranceModel.citationData?.location?.street
                )
            } catch (e: Exception) {
            }
            try {
                mAutoComTextViewBlock?.setText(
                    mIssuranceModel.citationData?.location?.block
                )

            } catch (e: Exception) {
            }
            try {
                mAutoComTextViewVinNum?.setText(
                    mIssuranceModel.citationData?.vehicle?.vinNumber
                )
            } catch (e: Exception) {
            }
            try {
                mAutoComTextViewSide?.setText(
                    mIssuranceModel.citationData?.location?.side
                )
            } catch (e: Exception) {
            }
            try {
                mAutoComTextViewLot?.setText(mIssuranceModel.citationData?.location?.lot)
            } catch (e: Exception) {
            }
            try {
                mAutoComTextViewLocation?.setText(
                    mIssuranceModel.citationData?.location?.location
                )
            } catch (e: Exception) {
            }
            try {
                mAutoComTextViewDirection?.setText(
                    mIssuranceModel.citationData?.location?.direction
                )
            } catch (e: Exception) {
            }
            try {
                mAutoConTextViewSpace?.setText(
                    mIssuranceModel.citationData?.location?.spaceName
                )
            } catch (e: Exception) {
            }
            if (mIssuranceModel.citationData?.ticketType != null) {
                mTicketType = mIssuranceModel.citationData?.ticketType
                if (mIssuranceModel.citationData?.ticketType?.equals("Warning", true)
                        .nullSafety() || mIssuranceModel.citationData?.ticketType?.equals(
                        "TAZ", true
                    ).nullSafety()
                ) {
                    mCheckboxType1.isChecked = true
                } else if (mIssuranceModel.citationData?.ticketType?.equals("Boot and Tow", true)
                        .nullSafety() || (mIssuranceModel.citationData?.ticketType?.equals(
                        "Tow", true
                    ).nullSafety()) || mIssuranceModel.citationData?.ticketType?.equals("PD", true)
                        .nullSafety()
                ) {
                    mCheckboxType2.isChecked = true
                } else if (mIssuranceModel.citationData?.ticketType?.equals("Drive Off", true)
                        .nullSafety() || mIssuranceModel.citationData?.ticketType?.equals(
                        "Rpp", true
                    ).nullSafety()
                ) {
                    mCheckboxType3.isChecked = true
                }
            }
            setDropdownMakeVehicle(mIssuranceModel.citationData?.vehicle?.make)
            setDropdownVehicleColour(mIssuranceModel.citationData?.vehicle?.color)
//            setDropdownVehicleModel(mIssuranceModel.citationData?.vehicle?.model,true)
            try {
                val model: CitationBookletModel = CitationBookletModel()
                val mIssModel: CitationIssuranceModel = CitationIssuranceModel()
                // int counter= Integer.parseInt(idNew)

                if (idNew != "0" && !mTicketActionButtonEvent.equals("UnUpload", true)) {
                    //counter++; //najib
                    //Log.e("dncsdc", String.valueOf(counter));// najib


                    //old code
                    // idNew= String.valueOf(counter);
                    //   model.setCitationBooklet(idNew);
                    // mFetched.add(model);


                    //new code
                    viewLifecycleOwner.lifecycleScope.launch {
                        mFetched = citationFormScreenViewModel.getCitationBooklet(0)

                        if (mFetched != null && mFetched!!.size != 0) {
                            mTvTicketNumber.text = mFetched?.get(0)?.citationBooklet
                        } else {
                            //Toast.makeText(this, "size-L:-"+mFetched.size(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
                if (mAutoComTextViewAbbrCode?.text.toString().isEmpty()) {
                    mAutoComTextViewAbbrCode?.setText(mPayBySpaceExpireMeterValue) //come from pay by space screen then default value
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            /**
             * Ticket Screen IF click on void reissue then violation details set on UI
             * If click on cancel then violoation should be empty
             */
            if (mTicketActionButtonEvent.equals(
                    "VoidReissue", true
                ) || mTicketActionButtonEvent.equals("UnUpload", true)
            ) {
                getDatasetList(false)
                // //dismissLoader();
            } else {
                getDatasetList(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityInForeground = true
    }

    override fun onPause() {
        super.onPause()
        isActivityInForeground = false
    }

    override fun onStop() {
        // TODO LOCK BY CODE
        if (mTicketActionButtonEvent.equals("VoidReissue", true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
            && sharedPreference.read(
                SharedPrefKey.CITATION_LOCK_PARTIALLY_LOCK, ""
            ).equals("lock")
        ) {
            val mLatestCitation = mFetched!![0]
            saveCitationIssurance(mLatestCitation)
        }

        super.onStop()
    }

    private fun getDatasetList(status: Boolean) {
        val stat: Boolean = status
        Thread {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    mCityZone = mWelcomeFormData?.officerZone //TODO city Zone CC
                    mSelectedCityZone =
                        if (mSelectedCityZone!!.isEmpty()) mWelcomeFormData?.officerZone.toString()
                            .toString() else mSelectedCityZone //TODO city Zone CC
                    selectedZoneCode = mWelcomeFormData?.cityZoneNameCode //TODO city Zone CC
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                setDropdownState()
                setDropdownBodyStyle()
                setDropdownDecalYear()
                if (!BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                    )
                ) {
                    setDropdownBlock()
//
                }
                setDropdownLocation()
                setDropdownLot()
                setDropdownMeterName()
                setDropdownSpaceName()
                setDropdownStreet()
                setDropdownDirection()
                //void and reissue mBeatItem not empty
                if (mBeatItem!!.isNotEmpty()) {
                    setDropdownBeat(mBeatItem!!)
                } else {
                    setDropdownBeat(mWelcomeFormData!!.officerBeatName!!)
                }
                if (checkBuildConfigForLprDetails2ForVinNumberSupportDropDown()) {
                    setDropdownVinNumber()
                }

                /**
                 * Violation list logic based on city zone
                 */
                viewLifecycleOwner.lifecycleScope.async {
                    var mApplicationList = mainActivityViewModel.getViolationListFromDataSet()
                    mApplicationList = mApplicationList?.filter { it.mIsVisible != 0 }
                    if (mApplicationList != null && mCityZone == null || mApplicationList != null && mCityZone != null && mCityZone!!.isEmpty() || mAutoComTextViewZone == null) {
                        val violationList: MutableList<DatasetResponse> = ArrayList()
                        mCityZone = mWelcomeFormData?.officerZone //TODO city Zone CC
                        selectedZoneCode = mWelcomeFormData?.cityZoneNameCode //TODO city Zone CC


                        for (violation in mApplicationList!!) {
                            if (violation.mZoneId != null && mCityZone != null && !TextUtils.isEmpty(
                                    mCityZone
                                )
                            ) {
                                if (violation.mZoneId != null && violation.mZoneId.equals(
                                        selectedZoneCode, true
                                    )
                                ) {
                                    violationList.add(violation)
                                }
                            } else {
                                logD("Violation list", "Zone id is null in violation data");
                                violationList.addAll(mApplicationList)
                                break
                            }
                        }
                        if (violationList!!.size == 0) {
                            logD("Violation list", "Zone id is null in violation data");
                            violationList.addAll(mApplicationList)
                        }
                        setDropdownAbbrCode(violationList)
                    } else if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_LAMETRO, true
                        ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)
                    ) {
                        setDropdownAbbrCode(mApplicationList)
                    } else {
                        setDropdownAbbrCode(mApplicationList)
                    }
                }

                setDropdownVioType()
                setDropdownRemark()
                setDropdownRemark1()
                setDropdownRemark2()
                setDropdownNote()
                setDropdownNote1()
                setDropdownNote2()
                if (mAutoComTextViewPBCZone != null) {
                    setDropdownPBCZone(mPBCZone)
                }
                if (mAutoComTextViewZone != null) {
                    setDropdownZone(mSelectedCityZone)
                }
                if (stat) {
                    try {
                        mAutoComTextViewAbbrCode?.setText("")
                    } catch (e: java.lang.Exception) {
                    }
                    try {
                        mAutoComTextViewCode?.setText("")
                    } catch (e: java.lang.Exception) {
                    }
                    try {
                        mAutoComTextViewAmount?.setText("")
                    } catch (e: java.lang.Exception) {
                    }
                    try {
                        mAutoComTextViewAmountDueDate?.setText("")
                    } catch (e: java.lang.Exception) {
                    }
                    try {
                        mEtLocationDescr?.setText("")
                    } catch (e: java.lang.Exception) {
                    }
                    try {
                        AutoComTextViewDueDate?.setText("")
                    } catch (e: java.lang.Exception) {
                    }
                    try {
                        AutoComTextViewDueDate30?.setText("")
                    } catch (e: java.lang.Exception) {
                    }
                    try {
                        AutoComTextViewDueDate45?.setText("")
                    } catch (e: java.lang.Exception) {
                    }

                    try {
                        mAutoComTextViewVioType?.setText("")
                    } catch (e: java.lang.Exception) {
                    }

                    try {
                        mAutoComTextViewVioTypeCode?.setText("")
                    } catch (e: java.lang.Exception) {
                    }

                    try {
                        mAutoComTextViewVioTypeDescription?.setText("")
                    } catch (e: java.lang.Exception) {
                    }

                }
                setDropdownSide()
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true
                    )
                ) {
                    mLockLotBasedOnAgencyKansasCity()
                }

                //dismissLoader()
                if (mAutoComTextViewLicensePlate != null) {
                    mAutoComTextViewLicensePlate?.onFocusChangeListener =
                        OnFocusChangeListener { v, hasFocus ->
                            if (mAutoComTextViewLicensePlate?.text?.length.nullSafety() > 5) {
                                if (BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                                    )
                                ) {
                                    mAutoComTextViewLicensePlate?.isEnabled = true
                                    mTvAgency?.isEnabled = true
                                    mTvAgency?.isFocusable = true
                                } else {
                                    mAutoComTextViewLicensePlate?.isEnabled = sharedPreference.read(
                                        SharedPrefKey.VOIDANDREISSUEBYPLATE, false
                                    )
                                    mTvAgency?.isEnabled = sharedPreference.read(
                                        SharedPrefKey.VOIDANDREISSUEBYPLATE, false
                                    )
                                    mTvAgency?.isFocusable = sharedPreference.read(
                                        SharedPrefKey.VOIDANDREISSUEBYPLATE, false
                                    )
                                }
                                /*** When get citation value is lock and officer will select violation then
                                 * locked this citation
                                 */
                                if (mLockCitation.equals("lock", ignoreCase = true)) {
                                    setLprLock(lockLprModel, requireContext(), sharedPreference)
                                    sharedPreference.write(
                                        SharedPrefKey.LOCKED_LPR_BOOL, true
                                    )//true
                                } else {
                                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                                }
                            }
                        }
                }

                if (vehicleStickerInfo != null) {
                    if (vehicleStickerInfo?.expiryMonth.nullSafety()
                            .isNotEmpty() && vehicleStickerInfo?.expiryYear.nullSafety()
                            .isNotEmpty()
                    ) {
                        if (checkBuildConfigForMonthYearPickerDialog()) {
                            val finalYear =
                                getExpiryDateFormatForMonthYearPickerDialog().getYearForExpiryDate(
                                    vehicleStickerInfo?.expiryYear.nullSafety("00").toInt()
                                )
                            setExpiryDate(vehicleStickerInfo?.expiryMonth.nullSafety(), finalYear)

                        } else {
                            val finalYear =
                                getExpiryDateFormatForDatePickerDialog().getYearForExpiryDate(
                                    vehicleStickerInfo?.expiryYear.nullSafety("00").toInt()
                                )
                            setExpiryDate(vehicleStickerInfo?.expiryMonth.nullSafety(), finalYear)
                        }
                    }
                }
            }
        }.start()
    }

    private fun setExpiryDate(month: String, year: String) {
        if (mAutoCompleteExpiryMonth != null) {

            mAutoCompleteExpiryMonth?.setText(month)
            mExpiryMonth = month

            mAutoCompleteDate?.setText(year)
            mExpiryYear = year
        } else {
            mExpiryYear = year
            mAutoCompleteDate?.setText("$month/$year")
        }
    }

    //set images to viewpager
    private fun setCameraImages() {
        viewLifecycleOwner.lifecycleScope.launch {
            bannerList?.clear()
            if (downloadedTimingBannerList?.isNotEmpty().nullSafety()) {
                for (model in downloadedTimingBannerList!!) {
                    try {
                        val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
                        //save image to db
                        val mImage = CitationImagesModel()
                        mImage.status = 1
                        mImage.citationImage = model?.citationImage
                        mImage.id = id.toInt() + counter
                        mImage.timeImagePath = model?.timeImagePath
                        citationFormScreenViewModel.insertCitationImage(mImage)
                        counter++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                downloadedTimingBannerList?.clear()
            }
            bannerList?.addAll(citationFormScreenViewModel.getCitationImage() as MutableList)
            if (reIssueTimeImageLinkAgain) {
                var imageIndex = 0
                for (i in bannerList?.indices!!.reversed()) {
                    if (bannerList!!.get(i)!!.status == 1) {
                        mTimingImages.add(bannerList!!.get(i)!!.timeImagePath.toString())
                        imageIndex++
//                   bannerList!!.removeAt(i)
                    }
                }
            }
            if (bannerList!!.size > 0) {
                showImagesBanner(bannerList!!)
                mViewPagerBanner.visibility = View.VISIBLE
            } else {
                mViewPagerBanner.visibility = View.GONE
            }
        }
    }

    //request camera and storage permission
    private fun requestPermission() {
        if (PermissionUtils.requestCameraAndStoragePermission(requireActivity())) {
            if (mCamera2Setting.equals("YES", ignoreCase = true)) {
                camera2Intent()
            } else {
                cameraIntent()
            }
        }
//            if(mCamera2Setting.equals("YES",ignoreCase = true))
//            {
//                camera2Intent()
//            }else {
//                cameraIntent()
//            }
    }

    private fun cameraIntent() {
        viewLifecycleOwner.lifecycleScope.launch {
            mImageCount = citationFormScreenViewModel.getCountImages().nullSafety()
            val mIMageCount = maxImageCount("MAX_IMAGES")
            if (mImageCount < mIMageCount) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                picUri = getOutputPhotoFile() //Uri.fromFile(getOutputPhotoFile());
                //tempUri=picUri;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //intent.putExtra("URI", picUri);
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                printToastMSGForErrorWarning(
                    requireContext(),
                    getString(R.string.msg_max_image).replace("#", mIMageCount.toString())
                )
            }
        }

    }

    private fun camera2Intent() {
        viewLifecycleOwner.lifecycleScope.launch {
            mImageCount = citationFormScreenViewModel.getCountImages().nullSafety()
            val mIMageCount = maxImageCount("MAX_IMAGES")
            if (mImageCount < mIMageCount) {
                val intent = Intent(requireContext(), CameraActivity::class.java)
                startActivityForResult(intent, REQUEST_CAMERA2)
            } else {
                printToastMSGForErrorWarning(
                    requireContext(),
                    getString(R.string.msg_max_image).replace("#", mIMageCount.toString())
                )
            }
        }

    }

    private fun getOutputPhotoFile(): Uri? {
        val directory = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
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
            path = FileProvider.getUriForFile(
                requireContext()!!, requireContext().packageName + ".provider", file
            )
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
                    viewLifecycleOwner.lifecycleScope.launch {
                        if (tempUri == null) {
                            getOutputPhotoFile()
                        }
                        val file = File("$tempUri/IMG_temp.jpg")
                        if (file.exists() && file.length() > 0) {
                            var mImgaeBitmap: Bitmap? = null
                            try {
//                            LogUtil.printToastMSG(requireContext(),"options image")
                                val options = BitmapFactory.Options()
                                options.inSampleSize = 4
                                options.inJustDecodeBounds = true
                                BitmapFactory.decodeFile(file.absolutePath, options)

                                // Calculate inSampleSize
                                options.inSampleSize = calculateInSampleSize(options, 300, 300)

                                // Decode bitmap with inSampleSize set
                                options.inJustDecodeBounds = false
                                val scaledBitmap =
                                    BitmapFactory.decodeFile(file.absolutePath, options)
//                            LogUtil.printToastMSG(requireContext(),"scaledBitmap")
                                //check the rotation of the image and display it properly
                                val exif: ExifInterface
                                exif = ExifInterface(file.absolutePath)
                                val orientation =
                                    exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
//                            LogUtil.printToastMSG(requireContext(),"orientation "+ orientation)
                                val matrix = Matrix()
                                if (orientation == 6) {
                                    matrix.postRotate(90f)
                                } else if (orientation == 3) {
                                    matrix.postRotate(180f)
                                } else if (orientation == 8) {
                                    matrix.postRotate(270f)
                                }
                                mImgaeBitmap = Bitmap.createBitmap(
                                    scaledBitmap,
                                    0,
                                    0,
                                    scaledBitmap.width,
                                    scaledBitmap.height,
                                    matrix,
                                    true
                                )

                                viewLifecycleOwner.lifecycleScope.launch {
                                    if (settingsUtils.isTimestampNeedOnImage()) {
                                        val timeStampBitmap =
                                            AppUtils.timestampItAndSave(mImgaeBitmap);
                                        SaveImageMM(timeStampBitmap)
                                    } else {
                                        SaveImageMM(mImgaeBitmap)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            logD("Camera", "Captured file is corrupted or empty")
                            requireContext().toast(
                                getString(R.string.wrn_lbl_capture_image)
                            )
                            file.delete() // cleanup corrupted image
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (requestCode == REQUEST_CAMERA2) {
            if (resultCode == RESULT_OK) {
                try {
                    viewLifecycleOwner.lifecycleScope.async {
                        try {
                            setCameraImages()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //TODO will add comments later
    private suspend fun SaveImageMM(finalBitmap: Bitmap?) {
        if (finalBitmap == null || !isActivityInForeground) {
            logD("SaveImageMM", "Bitmap is null, skipping save")
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            return
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            logD("SaveImageMM", "Storage not available for writing")
            return
        }
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        myDir.mkdirs()

        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname = "Image_${timeStamp}_capture.jpg"
        val file = File(myDir, fname)

        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)
            val quality = 30
            val success = finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.flush()
            out.close()

            if (!success) {
                logD("SaveImageMM", "Bitmap compression failed")
                file.delete()
                return
            }

            if (file.length() == 0L) {
                logD("SaveImageMM", "Saved file is 0 bytes, deleting")
                file.delete()
                return
            }

            val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
            val pathDb = file.path
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = pathDb
            mImage.id = id.toInt() + counter

            citationFormScreenViewModel.insertCitationImage(mImage)
            counter++
            setCameraImages()
            finalBitmap.recycle()
        } catch (e: Exception) {
            logD("SaveImageMM", "Exception: ${e.message}")
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            e.printStackTrace()
        }
    }

    /**
     * Save base 64 image
     */
    private suspend fun SaveImageMMBase64(finalBitmap: Bitmap?) {
        if (finalBitmap == null) {
            logD("SaveImageMMBase64", "Bitmap is null. Skipping save.")
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            return
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            logD("SaveImageMMBase64", "External storage not mounted.")
            return
        }

        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val timeStamp =
            SimpleDateFormat("yyyMMdd_HHmmss_${isImageAsBase64Count}", Locale.US).format(Date())
        val fname = "Image_${timeStamp}_capture.jpg"
        val file = File(myDir, fname)

        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)
            val compressQuality =
                if (checkBuildConfigForLprDetails2ForBase64ImageCompress()) 30 else 20
            val isCompressed =
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)

            out.flush()
            out.close()

            if (!isCompressed) {
                logD("SaveImageMMBase64", "Bitmap compression failed")
                file.delete()
                return
            }

            if (file.length() == 0L) {
                logD("SaveImageMMBase64", "File saved is 0 bytes. Deleting file.")
                file.delete()
                return
            }

            // Delete old temp file if exists
            val oldFile = File(myDir, "IMG_temp.jpg")
            if (oldFile.exists()) oldFile.delete()

            // Save to DB
            val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
            val pathDb = file.path
            val mImage = CitationImagesModel().apply {
                status = 0
                citationImage = pathDb
                this.id = id.toInt() + counter
            }

            citationFormScreenViewModel.insertCitationImage(mImage)
            counter++
            setCameraImages()
            finalBitmap.recycle()

            if (mTimingImages.size > isImageAsBase64Count) {
                logD("SONU INDEX", " $isImageAsBase64Count")
                decodeBase64StringToBitmapAsyn().execute(mTimingImages[isImageAsBase64Count])
                isImageAsBase64Count++
            }
        } catch (e: Exception) {
            logD("SaveImageMMBase64", "Exception: ${e.message}")
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            e.printStackTrace()
        }
    }


    //show banner images
    private fun showImagesBanner(mImageList: List<CitationImagesModel?>) {
        mList = mImageList as List<CitationImagesModel>
        //if (mBannerAdapter == null) {
        if (mList != null && mList.size > 0 && mBannerAdapter != null) {
            mBannerAdapter!!.setAnimalBannerList(mList)
            mViewPagerBanner.adapter = mBannerAdapter
            mViewPagerBanner.currentItem = 0
        }
        mViewPagerBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (mList == null || mList.size == 0) {
                    // Log.e("length--",""+animalInfo.getImageList().size());
                    return
                }
                try {
                    for (i in mList.indices) {
                        mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                    }
                    mDots!![position]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        if (mList != null && mList.size > 0 && mBannerAdapter != null) {
            setUiPageViewController(mBannerAdapter!!.count)
        }
    }

    //managing view pager ui
    private fun setUiPageViewController(count: Int) {
        try {
            mDotsCount = count
            mDots = arrayOfNulls(mDotsCount)
            pagerIndicator.removeAllViews()
            for (i in 0 until mDotsCount) {
                mDots!![i] = ImageView(requireContext())
                mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                mDots!![i]?.setPadding(8, 0, 8, 0)
                params.setMargins(4, 0, 4, 0)
                pagerIndicator.addView(mDots!![i], params)
            }
            if (mShowBannerCount == 0) {
                mShowBannerCount += 1
            }
            mDots!![mShowBannerCount - 1]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setServerTimingImageOnUI(metadataItems: List<MetadataItem>?) {
        downloadedTimingBannerList?.clear()
        var index = 0
        for (metadata in metadataItems!!) {
            val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
            val model = CitationImagesModel()
            model.status = 1
            model.id = id.toInt()
            model.citationImage = metadata.url
            if (mTimingImages.size >= index) {
                model.timeImagePath = mTimingImages[index]
            }
            downloadedTimingBannerList?.add(model)
            index++
        }
        setCameraImages()
    }

    private fun removeFocus() {
        linearLayoutLocationDetails.clearFocus()
        try {
            mEtLocationRemarks?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mEtLocationNotes?.clearFocus()
        } catch (e: Exception) {
        }
        tvEnforcementTitle.clearFocus()
        linearLayoutVehicleDetails.clearFocus()
        linearLayoutVoilationDetails.clearFocus()
        try {
            mTvOfficerName?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mTvBadgeId?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mTvOfficerMake?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mTvOfficerModel?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mTvOfficerColor?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mTvZone?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewLicensePlate?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewState?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewVinNum?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewStreet?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewBlock?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewAbbrCode?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewCode?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mEtLocationDescr?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewAmount?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mAutoComTextViewAmountDueDate?.clearFocus()
        } catch (e: Exception) {
        }

        try {
            mAutoComTextViewVioType?.clearFocus()
        } catch (e: Exception) {
        }

        try {
            mAutoComTextViewVioTypeCode?.clearFocus()
        } catch (e: Exception) {
        }

        try {
            mAutoComTextViewVioTypeDescription?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mEtLocationRemarks?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mEtLocationNotes1?.clearFocus()
        } catch (e: Exception) {
        }
        try {
            mEtLocationNotes2?.clearFocus()
        } catch (e: Exception) {
        }
    }

    fun previewButtonClicked() {
        removeFocus()
        if (isFormValid()) {
            if (mFetched != null) {
                try {

                    callBulkImageUpload()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set date picker view
    private fun openDataPicker(datePickerField: AppCompatAutoCompleteTextView?) {
        if (checkBuildConfigForMonthYearPickerDialog()) {
            MonthYearPickerDialog().apply {
                setListener { view, year, month, dayOfMonth ->
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = month
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val myFormat = getExpiryDateFormatForMonthYearPickerDialog()
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    datePickerField!!.setText(sdf.format(myCalendar.time))
//                    Toast.makeText(requireContext(), "Set date: $year/$month/$dayOfMonth", Toast.LENGTH_LONG).show()
                }
                show(childFragmentManager, "MonthYearPickerDialog")
            }
        } else {
            val date =
                OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = monthOfYear
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val myFormat = getExpiryDateFormatForDatePickerDialog()
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    datePickerField!!.setText(sdf.format(myCalendar.time))
                }
            DatePickerDialog(
                requireContext(),
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

    }


    //----------set dropdown-------------------------------------------
    //set value to State dropdown
    private fun setDropdownState() {
        requireActivity().hideSoftKeyboard()
        if (mAutoComTextViewState != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val mApplicationList = mainActivityViewModel.getStateListFromDataSet()

                if (mApplicationList != null && mApplicationList.size > 0) {
                    try {
                        Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return lhs?.state_name.nullSafety()
                                    .compareTo(rhs?.state_name.nullSafety())
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)

                    var pos = -1
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].state_name.nullSafety().toString()
                        if (mApplicationList[i].state_name.equals(
                                mStateItem, true
                            ) || mApplicationList[i].state_abbreviated.equals(mStateItem, true)
                        ) {
                            pos = i

                        }
                    }
//                    if (pos == 0) {
//                        if (mStateItem != null && mStateItem != mApplicationList[0].state_name
//                        ) {
//                            for (i in mApplicationList.indices) {
//                                mDropdownList[i] =
//                                    java.lang.String.valueOf(mApplicationList[i].state_name)
//                                if (mApplicationList[i].state_name.equals("California", true)
//                                ) {
//                                    pos = i
////                                    try {
////                                        mAutoComTextViewState?.setText(mDropdownList[pos])
////                                    } catch (e: java.lang.Exception) {
////                                    }
//                                }
//                            }
//                        }
//                    }
                    mAutoComTextViewState?.post {
                        if (pos >= 0) {
                            try {
                                mAutoComTextViewState?.setText(mDropdownList[pos])
                                mState2DigitCode = mApplicationList[pos].state_abbreviated
                            } catch (e: java.lang.Exception) {
                            }
                        }
                        val adapter = ArrayAdapter(
                            requireContext()!!, R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewState?.threshold = 1
                            mAutoComTextViewState?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewState?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()
                                    //                        mState2DigitCode = mApplicationList.get(position).getState_abbreviated();
                                    val index = getIndexOfState(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    mState2DigitCode = mApplicationList[index].state_abbreviated
                                }

                            // listonly
                            if (mAutoComTextViewState?.tag != null && mAutoComTextViewState?.tag == "listonly") {
                                setListOnly(
                                    requireContext(), mAutoComTextViewState!!
                                )
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun getIndexOfState(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.state_name, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to Body Style dropdown
    private fun setDropdownRemark() {
        requireActivity().hideSoftKeyboard()
        if (mEtLocationRemarks != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                //        val mApplicationList = mDatasetList?.dataset?.remarksList
                val mApplicationList = mainActivityViewModel.getRemarkListFromDataSet()

                if (mApplicationList != null && mApplicationList.size > 0) {
                    try {
                        Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return lhs?.remark!!.nullSafety()
                                    .compareTo(rhs?.remark!!.nullSafety())
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (mTimingRecordRemarkValue != null && !TextUtils.isEmpty(
                            mTimingRecordRemarkValue
                        )
                    ) {
                        if (checkBuildConfigForAutoFilledRemark() || getSettingFileValuesForRemarkAutoFilledWithElapsedTime()) {
                            val datasetResponse = DatasetResponse()
                            datasetResponse.remark =
                                "First Marked time: ".plus(mTimingRecordRemarkValue)
                            mApplicationList.toMutableList().add(0, datasetResponse)
                        }
                    }
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    var pos = -1
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].remark.toString()
                        try {
                            if (mRemarkItem != null && mRemarkItem!!.isNotEmpty()) {
                                if (mApplicationList[i].remark.equals(
                                        mRemarkItem, ignoreCase = true
                                    )
                                ) {
                                    pos = i
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mEtLocationRemarks?.post {
                        if (mTimingRecordRemarkValue != null && !TextUtils.isEmpty(
                                mTimingRecordRemarkValue
                            )
                        ) {
                            if (checkBuildConfigForAutoFilledRemark() || getSettingFileValuesForRemarkAutoFilledWithElapsedTime()) {
                                mEtLocationRemarks?.setText(
                                    "First Marked time: ".plus(
                                        if (mTimingRecordRemarkValue != null && !mTimingRecordRemarkValue.equals(
                                                "null"
                                            )
                                        ) mTimingRecordRemarkValue.toString() else ""
                                    )
                                )
                                if (!BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
                                    ) && !BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true
                                    ) && !BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                                    ) && !BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                                    )
                                ) {
                                    mEtLocationRemarks?.setClickable(false)
                                    mEtLocationRemarks?.setFocusable(false)
                                    mEtLocationRemarks?.setFocusableInTouchMode(false)
                                }
                            } else if ((BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_PRRS, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                                ))
                            ) {
                                mEtLocationRemarks?.setText(mTimingRecordRemarkValue.toString())
                            }
                        } else if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
                            ) && mRemarkItem != null && !TextUtils.isEmpty(mRemarkItem) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true
                            ) && mRemarkItem != null && !TextUtils.isEmpty(mRemarkItem) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                            ) && mRemarkItem != null && !TextUtils.isEmpty(mRemarkItem) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                            ) && mRemarkItem != null && !TextUtils.isEmpty(mRemarkItem) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true
                            ) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
                            ) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true
                            ) && mRemarkItem != null && !TextUtils.isEmpty(mRemarkItem) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true
                            ) && mRemarkItem != null && !TextUtils.isEmpty(mRemarkItem) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true
                            ) && mRemarkItem != null && !TextUtils.isEmpty(mRemarkItem)
                        ) {
                            try {
                                mEtLocationRemarks?.setText(mRemarkItem)
                            } catch (e: java.lang.Exception) {
                            }
                        } else if (mTimingRecordRemarkValueCameraFeed != null && !TextUtils.isEmpty(
                                mTimingRecordRemarkValueCameraFeed
                            )
                        ) {
                            /**
                             * Auto fill for camera raw feed
                             */
                            try {
                                mEtLocationRemarks?.setText(mTimingRecordRemarkValueCameraFeed)
                            } catch (e: java.lang.Exception) {
                            }
                        }
                        if (pos >= 0) {
                            try {
                                mEtLocationRemarks?.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

//                        mEtLocationRemarks?.setText("Lorem ipsum dolor sit amet, consectetur adip iscing elit.")

                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mEtLocationRemarks?.threshold = 1
                            mEtLocationRemarks?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mEtLocationRemarks?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()
                                    mSaveRemark = mEtLocationRemarks!!.text!!.toString()
                                }
                            // listonly
                            if (mEtLocationRemarks?.tag != null && mEtLocationRemarks?.tag == "listonly" && BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true
                                )
                            ) {
                                setListOnly(
                                    requireContext(), mEtLocationRemarks!!
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownRemark1() {
        requireActivity().hideSoftKeyboard()
        if (mEtLocationRemarks1 != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                //        val mApplicationList = mDatasetList?.dataset?.remarksList
                val mApplicationList = mainActivityViewModel.getRemarkListFromDataSet()

                var pos = -1
                if (mApplicationList != null && mApplicationList.size > 0) {
                    if (mTimingTireStem != null && !TextUtils.isEmpty(mTimingTireStem) && mTimingTireStem!!.contains(
                            "Prior Warning No."
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
                    ) {
                        val datasetResponse = DatasetResponse()
                        datasetResponse.remark = mTimingTireStem
                        mApplicationList.toMutableList().add(0, datasetResponse)
                    } else if (mTimingTireStem != null && !TextUtils.isEmpty(mTimingTireStem) && !mTimingTireStem.equals(
                            "Tire Stem :0/0"
                        )
                    ) {
                        val datasetResponse = DatasetResponse()

                        val address =
                            "Marked LOC: " + mBlock.nullSafety() + " " + (if (mSideItem != null && !mSideItem.equals(
                                    "null"
                                )
                            ) mSideItem else "") + " " + mStreetItem.nullSafety() + " " + mTimingTireStem

                        datasetResponse.remark = address
                        mApplicationList.toMutableList().add(0, datasetResponse)

                    }
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].remark.toString()
                        try {
                            if (mRemark1Item != null) {
                                if (mApplicationList[i].remark.equals(
                                        mRemark1Item, ignoreCase = true
                                    )
                                ) {
                                    pos = i

                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    Arrays.sort(mDropdownList)
                    mEtLocationRemarks1?.post {
                        if (mTimingTireStem != null && !TextUtils.isEmpty(mTimingTireStem) && mTimingTireStem!!.contains(
                                "Prior Warning No."
                            ) || (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
                            ) || BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                            ))
                        ) {
                            mEtLocationRemarks1?.setText(mTimingTireStem)
                        } else if (mTimingTireStem != null && !TextUtils.isEmpty(mTimingTireStem) && !mTimingTireStem.equals(
                                "Tire Stem :0/0"
                            )
                        ) {
                            mEtLocationRemarks1?.setText(
                                "Marked LOC: " + mBlock.nullSafety() + " " + (if (mSideItem != null && !mSideItem.equals(
                                        "null"
                                    )
                                ) mSideItem else "") + " " + mStreetItem.nullSafety() + " " + mTimingTireStem
                            )
                            if (!BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
                                ) && !BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true
                                ) && !BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                                ) && !BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                                )
                            ) {
                                mEtLocationRemarks1?.setClickable(false)
                                mEtLocationRemarks1?.setFocusable(false)
                                mEtLocationRemarks1?.setFocusableInTouchMode(false)
                            }
                        } else if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
                            ) && mTimingTireStem != null && !TextUtils.isEmpty(mTimingTireStem) || BuildConfig.FLAVOR.equals(
                                DuncanBrandingApp13()
                            ) && mTimingTireStem != null && !TextUtils.isEmpty(mTimingTireStem) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                            ) && mTimingTireStem != null && !TextUtils.isEmpty(mTimingTireStem)
                        ) {
                            mEtLocationRemarks1?.setText(mTimingTireStem.toString())
                        } else if (pos >= 0) {
                            try {
                                mEtLocationRemarks1?.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                            }
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mEtLocationRemarks1?.threshold = 1
                            mEtLocationRemarks1?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mEtLocationRemarks1?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()
                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownRemark2() {
        requireActivity().hideSoftKeyboard()
        if (mEtLocationRemarks2 != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val mApplicationList = mainActivityViewModel.getRemarkListFromDataSet()

                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].remark.toString()

                    }
                    Arrays.sort(mDropdownList)
                    mEtLocationRemarks2?.post {
                        if (mRemark2Item != null && mRemark2Item!!.isNotEmpty()) {
                            mEtLocationRemarks2?.setText(mRemark2Item)
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mEtLocationRemarks2?.threshold = 1
                            mEtLocationRemarks2?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mEtLocationRemarks2?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()
                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownNote() {
        requireActivity().hideSoftKeyboard()
        try {
            if (mEtLocationNotes != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val mApplicationList = mainActivityViewModel.getNotesListFromDataSet()

                    var pos = -1
                    if (mApplicationList != null && mApplicationList.size > 0) {
                        try {
                            Collections.sort(
                                mApplicationList, object : Comparator<DatasetResponse?> {
                                    override fun compare(
                                        lhs: DatasetResponse?, rhs: DatasetResponse?
                                    ): Int {
                                        return lhs?.note!!.nullSafety()
                                            .compareTo(rhs?.note!!.nullSafety())
                                    }
                                })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                        for (i in mApplicationList.indices) {
                            mDropdownList[i] = mApplicationList[i].note.toString()/* try {
                                if (mNoteItem != null) {
                                    if (mApplicationList[i].note.equals(
                                            mNoteItem,
                                            ignoreCase = true
                                        )
                                    ) {
                                        pos = i
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }*/
                        }
                        mEtLocationNotes?.post {
//                            if (pos >= 0) {
                            try {
//                                    mEtLocationNotes?.setText(mDropdownList[pos])
                                mEtLocationNotes?.setText(mNoteItem)
                            } catch (e: Exception) {
                            }
//                            }

                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.row_dropdown_menu_popup_item,
                                mDropdownList
                            )
                            try {
                                mEtLocationNotes?.threshold = 1
                                mEtLocationNotes?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mEtLocationNotes?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                        requireActivity().hideSoftKeyboard()
                                    }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownNote1() {
        requireActivity().hideSoftKeyboard()
        try {
            if (mEtLocationNotes1 != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val mApplicationList = mainActivityViewModel.getNotesListFromDataSet()

                    var pos = -1
                    if (mApplicationList != null && mApplicationList.size > 0) {
                        try {
                            Collections.sort(
                                mApplicationList, object : Comparator<DatasetResponse?> {
                                    override fun compare(
                                        lhs: DatasetResponse?, rhs: DatasetResponse?
                                    ): Int {
                                        return lhs?.note!!.nullSafety()
                                            .compareTo(rhs?.note!!.nullSafety())
                                    }
                                })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                        for (i in mApplicationList.indices) {
                            mDropdownList[i] = mApplicationList[i].note.toString()
                        }
                        mEtLocationNotes1?.post {
                            try {
                                mEtLocationNotes1?.setText(mNote1Item)
                            } catch (e: Exception) {
                            }
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.row_dropdown_menu_popup_item,
                                mDropdownList
                            )
                            try {
                                mEtLocationNotes1?.threshold = 1
                                mEtLocationNotes1?.setAdapter<ArrayAdapter<String?>>(adapter)
                                mEtLocationNotes1?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                        requireActivity().hideSoftKeyboard()
                                    }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownNote2() {
        requireActivity().hideSoftKeyboard()
        try {
            if (mEtLocationNotes2 != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val mApplicationList = mainActivityViewModel.getNotesListFromDataSet()

                    var pos = -1
                    if (mApplicationList != null && mApplicationList.size > 0) {
                        try {
                            Collections.sort(
                                mApplicationList, object : Comparator<DatasetResponse?> {
                                    override fun compare(
                                        lhs: DatasetResponse?, rhs: DatasetResponse?
                                    ): Int {
                                        return lhs?.note!!.nullSafety()
                                            .compareTo(rhs?.note!!.nullSafety())
                                    }
                                })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                        for (i in mApplicationList.indices) {
                            mDropdownList[i] = mApplicationList[i].note.toString()
                        }
                        mEtLocationNotes2?.post {
                            try {
                                mEtLocationNotes2?.setText(mNote2Item)
                            } catch (e: Exception) {
                            }
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.row_dropdown_menu_popup_item,
                                mDropdownList
                            )
                            try {
                                mEtLocationNotes2?.threshold = 1
                                mEtLocationNotes2?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mEtLocationNotes2?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                        requireActivity().hideSoftKeyboard()
                                    }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownBodyStyle() {
        requireActivity().hideSoftKeyboard()
        try {
            if (mAutoComTextViewBodyStyle != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val mApplicationList = mainActivityViewModel.getCarBodyStyleListFromDataSet()
                    var pos = -1
                    if (mApplicationList != null && mApplicationList.size > 0) {
                        Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                //                        return  lhs.getModel().compareTo(rhs.getModel());
                                return (if (lhs?.details != null) lhs.details else "")!!.compareTo((if (rhs?.details != null) rhs.details else "")!!)
                            }
                        })
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
                            ) && mBodyStyleItem == null || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
                            ) && mBodyStyleItem != null && mBodyStyleItem!!.isEmpty() || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
                            ) && mBodyStyleItem == null || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
                            ) && mBodyStyleItem != null && mBodyStyleItem!!.isEmpty()
                        ) {
                            mBodyStyleItem =
                                sharedPreference.read(SharedPrefKey.LOGIN_HEARING_TIME, "")
                        }

                        val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                        for (i in mApplicationList.indices) {
                            //                mDropdownList[i] = String.valueOf(mApplicationList.get(i).getBody_style());
                            mDropdownList[i] = mApplicationList[i].details.toString()
                            try {
                                if (mBodyStyleItem != null) {
                                    if (mApplicationList[i].body_style != null && mApplicationList[i].body_style.equals(
                                            mBodyStyleItem, ignoreCase = true
                                        ) || mApplicationList[i].details != null && mApplicationList[i].details.equals(
                                            mBodyStyleItem, ignoreCase = true
                                        )
                                    ) {
                                        pos = i
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        //            Arrays.sort(mDropdownList);
                        mAutoComTextViewBodyStyle?.post {
                            if (pos >= 0) {
                                try {
                                    mAutoComTextViewBodyStyle?.setText(mDropdownList[pos])
                                    mBodyStyleCodeItem = mApplicationList[pos].body_style
                                    body_style_lookup_code =
                                        mApplicationList[pos].body_style_lookup_code
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
                                ) || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
                                )
                            ) {
                                mAutoComTextViewBodyStyle!!.setText(
                                    sharedPreference.read(
                                        SharedPrefKey.LOGIN_HEARING_TIME, ""
                                    )
                                )
                                mBodyStyleCodeItem =
                                    sharedPreference.read(SharedPrefKey.LOGIN_HEARING_TIME, "")
                                body_style_lookup_code =
                                    sharedPreference.read(SharedPrefKey.LOGIN_HEARING_TIME, "")
                            }
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.row_dropdown_menu_popup_item,
                                mDropdownList
                            )
                            try {
                                mAutoComTextViewBodyStyle?.threshold = 1
                                mAutoComTextViewBodyStyle?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewBodyStyle?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                        requireActivity().hideSoftKeyboard()
                                        val index = getIndexOfBodyStyle(
                                            mApplicationList,
                                            parent.getItemAtPosition(position).toString()
                                        )
                                        mBodyStyleCodeItem = mApplicationList[index].body_style
                                        body_style_lookup_code =
                                            mApplicationList[index].body_style_lookup_code
                                    }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            if (mAutoComTextViewBodyStyle?.tag != null && mAutoComTextViewBodyStyle?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewBodyStyle!!)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOfBodyStyle(list: List<DatasetResponse>?, name: String): Int {
        var pos = 0
        for (myObj in list!!) {
            if (name.equals(myObj.details, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?, isAutoSelect: Boolean?) {
        try {
            var adapterModel: ArrayAdapter<String?>? = null
            requireActivity().hideSoftKeyboard()
            if (mTvOfficerModel != null) {
                mModelList?.clear()
                viewLifecycleOwner.lifecycleScope.launch {
//                var mApplicationList: List<DatasetResponse>? = null

                    val mApplicationList = mainActivityViewModel.getCarModelListFromDataSet()
                    if (mApplicationList != null && mApplicationList.size > 0) {
                        try {
                            Collections.sort(
                                mApplicationList, object : Comparator<DatasetResponse?> {
                                    override fun compare(
                                        lhs: DatasetResponse?, rhs: DatasetResponse?
                                    ): Int {
//                        return  lhs.getModel().compareTo(rhs.getModel());
                                        return (if (lhs?.model != null) lhs.model else "")!!.compareTo(
                                            (if (rhs?.model != null) rhs.model else "")!!
                                        )
                                    }
                                })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        for (i in mApplicationList.indices) {
                            try {
                                if (mApplicationList[i].make != null && mApplicationList[i].make == mSelectedMake) {
                                    val mDatasetResponse = DatasetResponse()
                                    mDatasetResponse.model = mApplicationList[i].model
                                    mDatasetResponse.model_lookup_code =
                                        mApplicationList[i].model_lookup_code.nullSafety("")
                                    mDatasetResponse.make = mApplicationList[i].make
                                    mDatasetResponse.makeText = mApplicationList[i].makeText
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
                                    if (mModelList[i].make == value || mModelList[i].model == value) {
                                        pos = i

                                    }
                                }
                            }
                            mTvOfficerModel?.post {

                                if (pos >= 0 && isAutoSelect == true) {
                                    try {
                                        mTvOfficerModel?.setText(mDropdownList[pos])
                                        mSelectedModel = mModelList[pos].model
                                        model_lookup_code = mModelList[pos].model_lookup_code
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    mTvOfficerModel?.setText("")
                                }
                                adapterModel = ArrayAdapter(
                                    requireContext(),
                                    R.layout.row_dropdown_lpr_details_item,
                                    mDropdownList
                                )
                                try {
                                    mTvOfficerModel?.threshold = 1
                                    mTvOfficerModel?.setAdapter<ArrayAdapter<String?>>(adapterModel)
                                    mTvOfficerModel?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id ->
                                            val index = getIndexOfModel(
                                                mDropdownList!!,
                                                parent.getItemAtPosition(position).toString()
                                            )
                                            mSelectedModel = mModelList[index].model
                                            model_lookup_code = mModelList[index].model_lookup_code
                                            requireActivity().hideSoftKeyboard()
                                        }
                                    if (mTvOfficerModel?.tag != null && mTvOfficerModel?.tag == "listonly") {
                                        setListOnly(requireContext(), mTvOfficerModel!!)
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        } else {
                            try {
                                mTvOfficerModel?.setText("")
                                mTvOfficerModel?.setAdapter(null)
                                adapterModel?.notifyDataSetChanged()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOfModel(list: Array<String?>, name: String): Int {
        var pos = 0
        for (myObj in list!!) {
            if (name.equals(myObj, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle(value: String?) {
        //init array list
        try {
            var makeValue: String = ""
            requireActivity().hideSoftKeyboard()
            viewLifecycleOwner.lifecycleScope.launch {
//            val mApplicationList = mDatasetList?.dataset?.carMakeList
                val mApplicationList = mainActivityViewModel.getCarMakeListFromDataSet()
                val uniqueDataSet: MutableSet<String> = HashSet()
                if (uniqueDataSet == null || uniqueDataSet.size < 1) {
                    if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                        for (i in mApplicationList.indices) {
                            uniqueDataSet.add(mApplicationList[i].make.toString() + "#" + mApplicationList[i].makeText.toString())
                        }
                    }
                }
                val Geeks = uniqueDataSet.toTypedArray()
                var pos = -1

                Arrays.sort(Geeks)
                if (uniqueDataSet != null && uniqueDataSet.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(uniqueDataSet.size)
                    for (i in Geeks.indices) {
                        mDropdownList[i] = Geeks[i].split("#").toTypedArray()[1]
                        if (value != "") {
                            val splitValue = Geeks[i].split("#").toTypedArray()
                            if (splitValue[0] == (value) || splitValue[1] == (value)) {
                                pos = i
                                makeValue = splitValue[1]
                            }
                        }
                    }
                    //                Arrays.sort(mDropdownList);
                    mTvOfficerMake?.post {
                        if (pos >= 0) {
                            mTvOfficerMake?.setText(makeValue)
                            mSelectedMake = Geeks[pos].split("#").toTypedArray()[0]
                            mSelectedMakeValue = Geeks[pos].split("#").toTypedArray()[1]
                            setDropdownVehicleModel(mSelectedModel, true)
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_lpr_details_item, mDropdownList
                        )
                        try {
                            mTvOfficerMake?.threshold = 1
                            mTvOfficerMake?.setAdapter<ArrayAdapter<String?>>(adapter)

                            mTvOfficerMake?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> //                        mSelectedMake = mDropdownList[position];
//                        mSelectedMakeValue = mApplicationList.get(position).getMake();
//                                                        parent.getItemAtPosition(position)
                                    val index = getIndexOf(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    mSelectedMake = mApplicationList!![index].make
                                    mSelectedMakeValue = mApplicationList[index].makeText
                                    if (mSelectedMakeValue != null) {
                                        setDropdownVehicleModel(mSelectedMake, false)
                                    } else {
                                        setDropdownVehicleModel("", false)
                                    }
                                }
                            if (mTvOfficerMake?.tag != null && mTvOfficerMake?.tag == "listonly") {
                                setListOnly(requireContext(), mTvOfficerMake!!)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOf(list: List<DatasetResponse>?, name: String): Int {
        var pos = 0
        for (myObj in list!!) {
            if (name.equals(myObj.makeText, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to vehicle colour dropdown
    private fun setDropdownVehicleColour(value: String?) {
        //init array list
//        val mApplicationList = mDatasetList!!.dataset!!.carColorList

        viewLifecycleOwner.lifecycleScope.launch {
            if (mTvOfficerColor != null) {
                val mApplicationList = mainActivityViewModel.getCarColorListFromDataSet()
                var pos = -1
                if (mApplicationList != null && mApplicationList.size > 0) {
                    Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                        override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
//                        return  lhs.getModel().compareTo(rhs.getModel());
                            return (if (lhs?.description != null) lhs.description else "")!!.compareTo(
                                (if (rhs?.description != null) rhs.description else "")!!
                            )
                        }
                    })
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].description.toString()
                        if (value != "") {
                            if (mDropdownList[i].equals(
                                    value, ignoreCase = true
                                ) || mApplicationList[i].color_code != null && mApplicationList[i].color_code.equals(
                                    value, ignoreCase = true
                                )
                            ) {
                                pos = i

                            }
                        }
                    }
                    mTvOfficerColor?.post {
                        try {
                            if (pos >= 0) {
                                mTvOfficerColor?.setText(mDropdownList[pos])
                                mSelectedColor = mApplicationList[pos].color_code
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_lpr_details_item, mDropdownList
                        )
                        try {
                            mTvOfficerColor?.threshold = 1
                            mTvOfficerColor?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mTvOfficerColor?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    val index: Int = getIndexOcolor(
                                        mApplicationList, mTvOfficerColor?.text.toString()
                                    )
                                    mSelectedColor = mApplicationList[index].color_code
//                        }
                                    requireActivity().hideSoftKeyboard()
                                }
                            if (mTvOfficerColor?.tag != null && mTvOfficerColor?.tag == "listonly") {
                                setListOnly(requireContext(), mTvOfficerColor!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
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


    /**set value to Decal Year dropdown
     * FOR HPA Decal year use as plate type
     */
    private fun setDropdownDecalYear() {
        requireActivity().hideSoftKeyboard()
        viewLifecycleOwner.lifecycleScope.launch {
            if (mAutoComTextViewDecalYear != null) {
                val mApplicationList = mainActivityViewModel.getDecalYearListFromDataSet()

                var pos = -1
                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].year.toString()
                        if (mApplicationList[i].year.equals(mDecalYearItem, ignoreCase = true)) {
                            pos = i

                        }
                    }
                    Arrays.sort(mDropdownList)
                    mAutoComTextViewDecalYear?.post {
                        if (pos >= 0) {
                            try {
                                mAutoComTextViewDecalYear?.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                            }
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewDecalYear?.threshold = 1
                            mAutoComTextViewDecalYear?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewDecalYear?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()
                                }
                            if (mAutoComTextViewDecalYear?.tag != null && mAutoComTextViewDecalYear?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewDecalYear!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet() {
        requireActivity().hideSoftKeyboard()
        viewLifecycleOwner.lifecycleScope.launch {
            if (mAutoComTextViewStreet == null) return@launch

            val fullStreetList = mainActivityViewModel.getStreetListFromDataSet() ?: emptyList()
            val mApplicationList: MutableList<DatasetResponse> = ArrayList()
//        val mApplicationList = mDatasetStreetList
            mApplicationList.addAll(
                fullStreetList!!.filter { it.zoneName.equals(selectedCityZone, ignoreCase = true) })

            // If filtered list is empty, fall back to full list as a fallback
            if (mApplicationList.isEmpty()) {
                logD("Street Filter", "No matching lot found, using full list as fallback.")
                mApplicationList.addAll(fullStreetList)
            }
            var pos = -1
            if (mApplicationList.isNotEmpty()) {
                try {
                    try {
                        mApplicationList.sortWith(compareBy { it.street_name.nullSafety() })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].street_name.toString()
                    try {
                        if (mStreetItem != null) {
                            val currentString = mApplicationList[i].street_name.toString()
                            if (currentString.equals(mStreetItem, ignoreCase = true)) {
                                pos = i
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                mAutoComTextViewStreet?.post {
                    try {
                        if (pos >= 0) mAutoComTextViewStreet?.setText(mDropdownList[pos])
                        val index: Int = getIndexStreet(
                            mApplicationList, mAutoComTextViewStreet?.text.toString()
                        )
                        mStreetLookupCode = mApplicationList[index].street_lookup_code
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val adapter = ArrayAdapter(
                        requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                    )
                    try {
                        mAutoComTextViewStreet?.threshold = 1
                        mAutoComTextViewStreet?.setAdapter<ArrayAdapter<String?>>(adapter)
                        //mSelectedShiftStat = mApplicationList.get(pos);
                        mAutoComTextViewStreet?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id -> //mAutoComTextViewDirection.setText(mApplicationList.get(position).getDirection()); ;
                                requireActivity().hideSoftKeyboard()
                                val index: Int = getIndexStreet(
                                    mApplicationList, mAutoComTextViewStreet?.text.toString()
                                )
                                mStreetLookupCode = mApplicationList[index].street_lookup_code

                            }
                        // listonly
                        if (mAutoComTextViewStreet?.tag != null && mAutoComTextViewStreet?.tag == "listonly") {
                            setListOnly(requireContext(), mAutoComTextViewStreet!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    private fun getIndexStreet(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.street_name, ignoreCase = true)) return pos
            pos++
        }
        return 0
    }

    //set value to beat dropdown
    private suspend fun setDropdownBeat(value: String) {
        //init array list
//        hideSoftKeyboard(requireContext()@)
        if (mTvBeat != null) {
            val mWelcomeList = mainActivityViewModel.getWelcomeObject()
            if (mWelcomeList != null) {
                val mApplicationList = mWelcomeList.welcomeList!!.beatStats
                if (mApplicationListZone == null || mApplicationListZone != null && mApplicationListZone!!.size < 0) {
                    mApplicationListZone =
                        if (mWelcomeList != null && mWelcomeList.welcomeList != null) mWelcomeList.welcomeList!!.zoneStats else null
                }
                var pos = -1
                if (mApplicationList != null && mApplicationList.size > 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                        for (i in mApplicationList.indices) {
                            mDropdownList[i] = mApplicationList[i].beatName.toString()
                            try {
                                if (value != null) {
                                    if (mApplicationList[i].beatName.equals(
                                            value, ignoreCase = true
                                        )
                                    ) {
                                        pos = i
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        //  Arrays.sort(mDropdownList);
                        mTvBeat?.post {
                            if (pos >= 0) {
                                try {
                                    mTvBeat!!.setText("" + mDropdownList[pos])
                                    mSelectedBeatStat = mApplicationList[pos]
                                } catch (e: Exception) {
                                }
                            }
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.row_dropdown_menu_popup_item,
                                mDropdownList
                            )
                            try {
                                mTvBeat!!.threshold = 1
                                mTvBeat!!.setAdapter<ArrayAdapter<String?>>(adapter)

                                mTvBeat!!.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id ->
                                        val index = mApplicationList.indexOfFirst {
                                            it.beatName == mTvBeat?.text.toString()
                                        }

                                        mSelectedBeatStat = mApplicationList[index]
                                        for (i in mApplicationListZone!!.indices) {
                                            if (mApplicationList[index].zone != null && mApplicationList[index].zone!!.trim()
                                                    .equals(
                                                        mApplicationListZone!![i].mCityZoneName,
                                                        ignoreCase = true
                                                    )
                                            ) {
                                                setDropdownZone(mApplicationListZone!![i].zoneName)
                                            }
                                        }

                                        requireActivity().hideSoftKeyboard()
                                    }
                                if (mTvBeat!!.tag != null && mTvBeat!!.tag == "listonly") {
                                    setListOnly(requireContext(), mTvBeat!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownMeterName() {
        requireActivity().hideSoftKeyboard()
        var pos = -1
        viewLifecycleOwner.lifecycleScope.launch {
            if (mAutoComTextViewMeterName != null) {
                val mApplicationList = mainActivityViewModel.getMeterListFromDataSet()
                viewLifecycleOwner.lifecycleScope?.launch {
                    mAutoComTextViewMeterName!!.setText(mMeterNameItem)

                    val meterAdapter = AutoCompleteAdapter(
                        requireContext()!!,
                        R.layout.autocomplete_items,
                        mApplicationList!!,
                        object : AutoCompleteAdapter.ListItemSelectListener {
                            override fun onItemClick(position: Int, meterObject: DatasetResponse) {
                                requireActivity().hideSoftKeyboard()
                                mAutoComTextViewMeterName!!.setText(meterObject?.name.nullSafety(""))

                                try {
                                    mAutoComTextViewMeterName?.clearFocus()
                                    mAutoComTextViewBlock?.setText(meterObject?.block.nullSafety(""))
                                    mAutoComTextViewDirection?.setText(
                                        meterObject?.direction.nullSafety(
                                            ""
                                        )
                                    )
                                    mAutoComTextViewStreet?.setText(meterObject?.street.nullSafety(""))
                                    //TODO we are commenting this line because we are getting street null here & it cause to reset street when meter is selected
                                    if (BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true
                                        )
                                    ) {
                                        mStreetItem = meterObject?.street.nullSafety("")
                                        setDropdownStreet()
                                    }
                                    if (meterObject?.mPbcZone != null) {
                                        mAutoComTextViewPBCZone?.setText(
                                            meterObject.mPbcZone.nullSafety(
                                                ""
                                            )
                                        )
                                    }
//                            }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        })
                    mAutoComTextViewMeterName!!.setAdapter(meterAdapter)
                    mAutoComTextViewMeterName!!.setThreshold(1);
                    meterAdapter!!.setNotifyOnChange(true);
                }
            }
        }
    }

    private fun getIndexOfMeter(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.name, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }


    //set value to Meter Name dropdown
    private fun setDropdownSpaceName() {
        requireActivity().hideSoftKeyboard()
        if (mAutoConTextViewSpace != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                var pos = -1
                val mApplicationList = mainActivityViewModel.getSpaceListFromDataSet()

                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        if (mApplicationList[i].spaceName != null) {
                            mDropdownList[i] = mApplicationList[i].spaceName.toString()
                            if (mApplicationList[i].spaceName.equals(
                                    mMeterNameItem, ignoreCase = true
                                ) || mApplicationList[i].spaceName.equals(
                                    mSpaceId, ignoreCase = true
                                )
                            ) {
                                pos = i

                            }
                        }
                    }
                    mAutoConTextViewSpace?.post {
                        if (pos >= 0) {
                            try {
                                mAutoConTextViewSpace?.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoConTextViewSpace?.threshold = 1
                            mAutoConTextViewSpace?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoConTextViewSpace?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    requireActivity().hideSoftKeyboard()
//                                    mSpaceId = mAutoConTextViewSpace?.text.toString()
                                    try {
                                        val index: Int = getIndexOfSpace(
                                            mApplicationList, mAutoConTextViewSpace?.text.toString()
                                        )
//                            if (mApplicationList[index].block != null && !TextUtils.isEmpty(mApplicationList[index].block)) {
                                        mAutoComTextViewBlock?.setText(mApplicationList[index].block.toString())
//                            }
//                            mAutoConTextViewSpace.setText(String.valueOf(mApplicationList.get(position).getBlock()));
                                        mAutoComTextViewDirection?.setText(mApplicationList[index].direction.toString())

                                        mAutoComTextViewStreet?.setText(mApplicationList[index].street.toString())

//                            if(mApplicationList[index]!!.lot!!.toString().isNotEmpty()){
                                        mAutoComTextViewLot?.setText(mApplicationList[index].lot!!.toString())
//                        }
                                    } catch (e: java.lang.Exception) {
                                    }
                                }
                            // listonly
//                if(mAutoConTextViewSpace.getTag()!=null && mAutoConTextViewSpace.getTag().equals("listonly")) {
//                    AppUtils.setListOnly(LprDetails2Activity.requireContext(),mAutoConTextViewSpace);
//                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun getIndexOfSpace(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.spaceName, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }


    //set value to Meter Name dropdown
    private fun setDropdownBlock() {
        requireActivity().hideSoftKeyboard()
        if (mAutoComTextViewBlock != null) {
            viewLifecycleOwner.lifecycleScope.launch {

                val fullBlockList = mainActivityViewModel.getBlockListFromDataSet() ?: emptyList()
                val mApplicationList: MutableList<DatasetResponse> = ArrayList()
//        val mApplicationList = mDatasetStreetList
                mApplicationList.addAll(
                    fullBlockList!!.filter {
                        it.zoneName.equals(
                            selectedCityZone, ignoreCase = true
                        )
                    })

                // If filtered list is empty, fall back to full list as a fallback
                if (mApplicationList.isEmpty()) {
                    logD("Street Filter", "No matching lot found, using full list as fallback.")
                    mApplicationList.addAll(fullBlockList)
                }

                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].blockName.toString()

                    }
//                    Arrays.sort(mDropdownList)
                    mAutoComTextViewBlock?.post {
                        mAutoComTextViewBlock?.setText(mBlock)

                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewBlock?.threshold = 1
                            mAutoComTextViewBlock?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewBlock?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->

                                    requireActivity().hideSoftKeyboard()
                                }
                            // listonly
                            if (mAutoComTextViewBlock?.tag != null && mAutoComTextViewBlock?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewBlock!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


    //set value to zone dropdown
    private fun setDropdownZone(value: String?) {
        try {
            viewLifecycleOwner.lifecycleScope.launch {
                if (mAutoComTextViewZone != null) {
                    val mWelcomeList = mainActivityViewModel.getWelcomeObject()
                    if (mApplicationListZone == null || mApplicationListZone != null && mApplicationListZone!!.size < 0) {
                        mApplicationListZone =
                            if (mWelcomeList != null && mWelcomeList.welcomeList != null) mWelcomeList.welcomeList!!.zoneStats else null
                    }
                    var pos = -1
                    if (mApplicationListZone != null && mApplicationListZone!!.size > 0) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationListZone!!.size)
                        for (i in mApplicationListZone!!.indices) {
                            mDropdownList[i] = mApplicationListZone!![i].zoneName.toString()
                            if (value.equals(mDropdownList[i], ignoreCase = true)) {
                                pos = i
                                selectedZoneCode = mApplicationListZone!![i].mCityZoneName

                            }
                        }
                        mAutoComTextViewZone?.post {
                            if (pos >= 0) {
                                mAutoComTextViewZone?.setText(mDropdownList[pos])
                                if (BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                    )
                                ) {
                                    mTvAgency!!.setText("CPA " + mDropdownList[pos])
                                }

                                viewLifecycleOwner.lifecycleScope.launch {
                                    var mApplicationList =
                                        mainActivityViewModel.getViolationListFromDataSet()

                                    mApplicationList =
                                        mApplicationList?.filter { it.mIsVisible != 0 }
                                    if (mApplicationList != null) {
                                        val violationList: MutableList<DatasetResponse> =
                                            ArrayList()
                                        for (violation in mApplicationList) {
                                            if (violation.mZoneId != null && selectedZoneCode != null && !TextUtils.isEmpty(
                                                    selectedZoneCode
                                                )
                                            ) {
                                                if (violation.mZoneId != null && violation.mZoneId.equals(
                                                        selectedZoneCode, true
                                                    )
                                                ) {
                                                    violationList.add(violation)
                                                }
                                            } else {
                                                logD(
                                                    "Violation list",
                                                    "Zone id is null in violation data"
                                                );
                                                violationList.addAll(mApplicationList)
                                                break
                                            }
                                        }
                                        setDropdownAbbrCode(violationList)
                                    }
                                }
                            }
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.row_dropdown_menu_popup_item,
                                mDropdownList
                            )
                            try {
                                mAutoComTextViewZone?.threshold = 1
                                mAutoComTextViewZone?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewZone?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id ->
                                        requireActivity().hideSoftKeyboard()

                                        val index = getIndexOfCityZone(
                                            mApplicationListZone!!,
                                            mAutoComTextViewZone?.text.toString()
                                        )
                                        viewLifecycleOwner.lifecycleScope.launch {
                                        var mApplicationList =
                                            mainActivityViewModel.getViolationListFromDataSet()
                                        mApplicationList =
                                            mApplicationList?.filter { it.mIsVisible != 0 }
                                        if (mApplicationList != null) {
                                            val violationList: MutableList<DatasetResponse> =
                                                ArrayList()

                                            if (!selectedCityZone.equals(mAutoComTextViewZone?.text.toString())) {
                                                resetViolationSectionField()
                                            }
                                            selectedCityZone =
                                                mApplicationListZone!![index].zoneName
                                            selectedZoneCode =
                                                mApplicationListZone!![index].mCityZoneName
                                            if (BuildConfig.FLAVOR.equals(
                                                    Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                                )
                                            ) {
                                                mTvAgency!!.setText("CPA " + selectedCityZone)
                                            }
                                            /**
                                             * Woodstock street and block narrow down to selection of zone
                                             * manage by setting NARROW_DOWN_STREET_BLOCK_TO_SELECTION_ZONE :: YES
                                             */
                                            if (getSettingFileValuesForNarrowDownStreetAndBlockListBasedOnSelectionZone()) {
                                                setDropdownStreet()
                                                setDropdownBlock()
                                            }
                                            mAutoComTextViewStreet?.setText("")
                                            mAutoComTextViewBlock?.setText("")
                                            for (violation in mApplicationList!!) {
                                                if (selectedZoneCode != null && !TextUtils.isEmpty(
                                                        selectedZoneCode
                                                    ) && mApplicationListZone!![index].mCityZoneName != null
                                                ) {
                                                    if (violation.mZoneId.equals(
                                                            mApplicationListZone!![index].mCityZoneName,
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        violationList.add(violation)
                                                    }
                                                } else {
                                                    violationList.addAll(mApplicationList!!)
                                                    break
                                                }
                                            }
                                            setDropdownAbbrCode(violationList)

                                        }
                                    }
                                    }
                                // listonly
                                if (mAutoComTextViewZone?.tag != null && mAutoComTextViewZone?.tag == "listonly") {
                                    setListOnly(requireContext(), mAutoComTextViewZone!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOfCityZone(list: List<ZoneStat>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.zoneName, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to zone dropdown
    private fun setDropdownPBCZone(vlaue: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (mAutoComTextViewPBCZone != null) {
                var mApplicationList: List<ZoneStat>? = null
                val mWelcomeList = mainActivityViewModel.getWelcomeObject()
                if (mWelcomeList != null) {
                    mApplicationList = mWelcomeList.welcomeList!!.pbcZoneStats
                }
                var pos = -1
                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
//                mDropdownList[i] = String.valueOf(mApplicationList.get(i).getmCityZoneName());
                        mDropdownList[i] = mApplicationList[i].zoneName.toString()
                        try {
                            if (vlaue != null) {
                                if (mApplicationList[i].mCityZoneName.equals(
                                        vlaue, ignoreCase = true
                                    )
                                ) {
                                    pos = i
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    //insertFormToDb(true, false, null);
//                Arrays.sort(mDropdownList)
                    mAutoComTextViewPBCZone?.post {
                        if (pos >= 0) {
                            try {
                                mAutoComTextViewPBCZone?.setText(mApplicationList[pos].mCityZoneName)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewPBCZone?.threshold = 1
                            mAutoComTextViewPBCZone?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mAutoComTextViewPBCZone?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    requireActivity().hideSoftKeyboard()
//                                mAutoComTextViewMeterName!!.setText(mAutoComTextViewPBCZone!!.text.toString())
                                }
                            // listonly
                            if (mAutoComTextViewPBCZone?.tag != null && mAutoComTextViewPBCZone?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewPBCZone!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    //set value to Lot dropdown
    private fun setDropdownLocation() {
//        requireActivity().hideSoftKeyboard()
        var pos = -1
        if (mAutoComTextViewLocation != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                //        val mApplicationList = mDatasetList?.dataset?.lotList
                val mApplicationList = mainActivityViewModel.getLotListFromDataSet()


                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].location.toString()
                        try {
                            if (mLocationItem != null) {
                                if (mApplicationList.get(i).location.equals(
                                        mLocationItem, ignoreCase = true
                                    )
                                ) {
                                    pos = i
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    //            Arrays.sort(mDropdownList);
                    mAutoComTextViewLocation?.post {
                        if (pos >= 0) {
                            try {
                                mAutoComTextViewLocation?.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            try {
                                mAutoComTextViewLot?.setText(mApplicationList.get(pos).lot.toString())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            try {
                                mAutoComTextViewBlock?.setText(mApplicationList.get(pos).block!!.nullSafety())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            val currentString = mApplicationList.get(pos).street.toString()
                            val separated = currentString.split(" ").toTypedArray()
                            mStreetItem = separated[0]
                            setDropdownStreet()
                            mDirectionItem = mApplicationList.get(pos).direction.toString()
                            try {
                                mAutoComTextViewDirection?.setText(mApplicationList.get(pos).direction.toString())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            setDropdownDirection()
                        }
                        // mAutoComTextViewDirection.setText();
//                    }

                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewLocation?.threshold = 1
                            mAutoComTextViewLocation?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewLocation?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()
                                    val index = getIndexOfLocation(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )

                                    mLotItem = mApplicationList[index].lot.toString()
                                    try {
                                        mAutoComTextViewLot?.setText(mApplicationList[index].lot.toString())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    try {
                                        mAutoComTextViewBlock?.setText(mApplicationList[index].block.nullSafety())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    val currentString = mApplicationList[index].street.toString()
                                    val separated = currentString.split(" ").toTypedArray()
                                    mStreetItem = separated[0]
                                    //mStreetItem = String.valueOf(mApplicationList.get(position).getStreet());
                                    setDropdownStreet()
                                    mDirectionItem = mApplicationList[index].direction.toString()
                                    try {
                                        mAutoComTextViewDirection?.setText(mApplicationList[index].direction.toString())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    setDropdownDirection()
                                }
                            if (mAutoComTextViewLocation?.tag != null && mAutoComTextViewLocation?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewLocation!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
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


    //set value to Lot dropdown.
    private fun setDropdownLot() {
        var pos = -1
        if (mAutoComTextViewLot != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                //        val mApplicationList = mDatasetList?.dataset?.lotList
                val mApplicationList = mainActivityViewModel.getLotListFromDataSet()

                if (mApplicationList != null && mApplicationList.size > 0) {
                    try {
                        Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return lhs?.location!!.nullSafety()
                                    .compareTo(rhs?.location!!.nullSafety())
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
//                mDropdownList[i] = String.valueOf(mApplicationList.get(i).getLot());//TODO TIN
                        mDropdownList[i] = mApplicationList[i].location.toString()
                        try {
                            if (mLotItem != null) {
                                if (mApplicationList[i].lot != null && mApplicationList[i].lot!!.isNotEmpty() && mApplicationList[i].lot.equals(
                                        mLotItem, ignoreCase = true
                                    ) || mApplicationList[i].location != null && mApplicationList[i].location!!.isNotEmpty() && mApplicationList[i].location.equals(
                                        mLotItem, ignoreCase = true
                                    ) || mLotItem!!.isNotEmpty() && mApplicationList[i].location != null && mApplicationList[i].location!!.isNotEmpty() && mApplicationList[i].location.equals(
                                        mWelcomeFormData!!.lot, ignoreCase = true
                                    )
                                ) {
                                    pos = i
                                    try {
                                        mViolationExtraAmount =
                                            mApplicationList[i].violation?.takeIf { it.isNotBlank() }   // Only take if not blank
                                                ?.toDoubleOrNull()             // Convert safely to Double
                                                ?: 0.0
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    //            Arrays.sort(mDropdownList);
                    mAutoComTextViewLot?.post {
                        if (pos >= 0) {
                            try {
                                mAutoComTextViewLot?.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            try {
                                mStreetItem = mApplicationList[pos].street.toString()
                                mAutoComTextViewBlock?.setText(mApplicationList[pos].block.nullSafety())
                                mAutoComTextViewStreet?.setText(mApplicationList[pos].street.nullSafety())
                                mApplicationList[pos].agency_name?.takeIf { it.isNotEmpty() }?.let {
                                    mTvAgency?.setText(it)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (!BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true
                                )
                            ) {
                                val currentString = mApplicationList[pos].street.toString()
                                val separated = currentString.split(" ").toTypedArray()
                                mStreetItem = separated[0]
                            }
                            setDropdownStreet()
                            mDirectionItem = mApplicationList[pos].direction.toString()
                            try {
                                mAutoComTextViewDirection?.setText(mApplicationList[pos].direction.toString())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            setDropdownDirection()
                            try {
                                mLotBranchId =
                                    mApplicationList[pos].lotBranchId?.takeIf { it.isNotBlank() }   // Only take if not blank
                                        ?.toString()         // Convert safely to Double
                                        ?: ""
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            try {
                                mLotlookupCode =
                                    mApplicationList[pos].lotLookupCode?.takeIf { it.isNotBlank() }   // Only take if not blank
                                        ?.toString()         // Convert safely to Double
                                        ?: ""
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        // mAutoComTextViewDirection.setText();
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewLot?.threshold = 1
                            mAutoComTextViewLot?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewLot?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    val index = getIndexOfLocation(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    try {
                                        mAutoComTextViewBlock?.setText(mApplicationList[index].block.nullSafety())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    val currentString = mApplicationList[index].street.toString()
//                                val separated = currentString.split(" ").toTypedArray()
                                    mStreetItem =
                                        mApplicationList[index].street.toString()//separated[0]
                                    //mStreetItem = String.valueOf(mApplicationList.get(position).getStreet());
                                    setDropdownStreet()
                                    mDirectionItem = mApplicationList[index].direction.toString()
                                    try {
                                        mAutoComTextViewDirection?.setText(mApplicationList[index].direction.toString())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    setDropdownDirection()
                                    setDropdownZone(mAutoComTextViewLot?.text.toString())
                                    try {
                                        val isCartaFlavor = BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        )
                                        val item = mViolationListSelectedItem

                                        mViolationExtraAmount =
                                            mApplicationList[index].violation?.takeIf { it.isNotBlank() }
                                                ?.toDoubleOrNull() ?: 0.0

                                        if (item != null && (isCartaFlavor || item.mFlatFineByLot == 1)) {
                                            setViolationBaseData(item, index)
                                        }
                                        mApplicationList[index].agency_name?.takeIf { it.isNotEmpty() }
                                            ?.let {
                                                mTvAgency?.setText(it)
                                            }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    try {
                                        mLotBranchId =
                                            mApplicationList[index].lotBranchId?.takeIf { it.isNotBlank() }   // Only take if not blank
                                                ?.toString()         // Convert safely to Double
                                                ?: ""
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    try {
                                        mLotlookupCode =
                                            mApplicationList[index].lotLookupCode?.takeIf { it.isNotBlank() }   // Only take if not blank
                                                ?.toString()         // Convert safely to Double
                                                ?: ""
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            if (mAutoComTextViewLot?.tag != null && mAutoComTextViewLot?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewLot!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    //set value to side dropdown
    private fun setDropdownSide() {
        requireActivity().hideSoftKeyboard()
        viewLifecycleOwner.lifecycleScope.launch {
            if (mAutoComTextViewSide != null) {
                var pos = -1
                val mApplicationList = mainActivityViewModel.getSideListFromDataSet()

                if (mApplicationList != null && mApplicationList.size > 0) {
                    try {
                        Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return lhs?.sideName!!.nullSafety()
                                    .compareTo(rhs?.sideName!!.nullSafety())
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].sideName.toString()
                        if (mApplicationList[i].sideName != null && mApplicationList[i].sideName.equals(
                                mSideItem, ignoreCase = true
                            ) || mApplicationList[i].sideshort != null && mApplicationList[i].sideshort.equals(
                                mSideItem, ignoreCase = true
                            )
                        ) {
                            pos = i
                        }
                    }
                    //            Arrays.sort(mDropdownList);
                    mAutoComTextViewSide?.post {
                        if (pos >= 0) {
                            try {
                                mAutoComTextViewSide?.setText(mDropdownList[pos])
                                mSideOfStreet2DigitCode = mApplicationList[pos].sideshort
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewSide?.threshold = 1
                            mAutoComTextViewSide?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewSide?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()
                                    val index: Int = getIndexOfSide(
                                        mApplicationList, mAutoComTextViewSide!!.text.toString()
                                    )
                                    mSideOfStreet2DigitCode =
                                        if (mApplicationList[index].sideshort != null) mApplicationList[index].sideshort else mApplicationList[index].sideName
                                }
                            if (mAutoComTextViewSide?.tag != null && mAutoComTextViewSide?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewSide!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun getIndexOfSide(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.sideName, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to Direction dropdown
    private fun setDropdownDirection() {
        requireActivity().hideSoftKeyboard()
        viewLifecycleOwner.lifecycleScope.launch {
            if (mAutoComTextViewDirection != null) {
//            val mApplicationList: MutableList<DatasetResponse> = ArrayList()
//            mApplicationList.add(DatasetResponse("E"))
//            mApplicationList.add(DatasetResponse("N"))
//            mApplicationList.add(DatasetResponse("S"))
//            mApplicationList.add(DatasetResponse("W"))
                var pos = -1
                val mApplicationList = mainActivityViewModel.getSideListFromDataSet()

                if (mApplicationList != null && mApplicationList.size > 0) {
                    try {
                        Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return lhs?.sideName!!.nullSafety()
                                    .compareTo(rhs?.sideName!!.nullSafety())
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].sideName.toString()
                        if (mApplicationList[i].sideName != null && mApplicationList[i].sideName.equals(
                                mSideItem, ignoreCase = true
                            ) || mApplicationList[i].sideshort != null && mApplicationList[i].sideshort.equals(
                                mSideItem, ignoreCase = true
                            ) || mApplicationList[i].direction.equals(
                                mDirectionItem, ignoreCase = true
                            )
                        ) {
                            pos = i
                        }
                    }
                    mAutoComTextViewDirection?.post {
                        try {
                            if (pos >= 0) {
                                mAutoComTextViewDirection?.setText(mApplicationList[pos].sideName.toString())
                                mSideOfStreet2DigitCode = mApplicationList[pos].sideshort
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewDirection?.threshold = 1
                            mAutoComTextViewDirection?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewDirection?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()
                                    val index: Int = getIndexOfSide(
                                        mApplicationList,
                                        mAutoComTextViewDirection!!.text.toString()
                                    )
                                    mSideOfStreet2DigitCode =
                                        if (mApplicationList[index].sideshort != null) mApplicationList[index].sideshort else mApplicationList[index].sideName

                                }
                            if (mAutoComTextViewDirection?.tag != null && mAutoComTextViewDirection?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewDirection!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    //set value to Direction dropdown
    private fun setDropdownVinNumber() {
        requireActivity().hideSoftKeyboard()

        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true
            ) || getSettingFileValuesForVinFieldAsDropdownOption().equals(Constants.VIN_DROPDOWN_OPTION_CUU)
        ) {
            mApplicationVinList.add(DatasetResponse.setDirection("COVERED"))
            mApplicationVinList.add(DatasetResponse.setDirection("UNKNOWN"))
            mApplicationVinList.add(DatasetResponse.setDirection("UNREADABLE"))
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOUTH_LAKE, true
            ) || getSettingFileValuesForVinFieldAsDropdownOption().equals(Constants.VIN_DROPDOWN_OPTION_UVV)
        ) {
            mApplicationVinList.add(DatasetResponse.setDirection("UNKNOWN"))
            mApplicationVinList.add(DatasetResponse.setDirection("VIN COVERED"))
            mApplicationVinList.add(DatasetResponse.setDirection("VIN UNREADABLE"))
        } else {
            mApplicationVinList.add(DatasetResponse.setDirection("Not Visible"))
            mApplicationVinList.add(DatasetResponse.setDirection("Partially Covered"))
        }
        val pos = 0
        if (mApplicationVinList != null && mApplicationVinList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationVinList.size)
            for (i in mApplicationVinList.indices) {
                mDropdownList[i] = mApplicationVinList[i].direction.toString()
            }
            //            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mAutoComTextViewVinNum?.threshold = 1
                mAutoComTextViewVinNum?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mAutoComTextViewVinNum?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        requireActivity().hideSoftKeyboard()
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to side dropdown
    private fun setDropdownExpiryYear() {
        requireActivity().hideSoftKeyboard()
        viewLifecycleOwner.lifecycleScope.launch {
            if (mAutoCompleteDate != null) {
                var pos = -1
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                val years = (currentYear - 15..currentYear + 15).toList()
//                    val mDropdownList = arrayOfNulls<String>(years.size)
                mAutoCompleteDate?.post {

                    val adapter = ArrayAdapter(
                        requireContext(), R.layout.row_dropdown_menu_popup_item, years
                    )
                    try {
                        mAutoCompleteDate?.threshold = 1
                        mAutoCompleteDate?.setAdapter<ArrayAdapter<Int?>>(adapter)
                        //mSelectedShiftStat = mApplicationList.get(pos);
                        mAutoCompleteDate?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                requireActivity().hideSoftKeyboard()
                                mExpiryYear = (mAutoCompleteDate?.text.toString().substring(2, 4))
                            }
                        if (mAutoCompleteDate?.tag != null && mAutoCompleteDate?.tag == "listonly") {
                            setListOnly(requireContext(), mAutoCompleteDate!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to side dropdown
    private fun setDropdownExpiryMonth() {
        requireActivity().hideSoftKeyboard()
        viewLifecycleOwner.lifecycleScope.launch {
            if (mAutoCompleteExpiryMonth != null) {
                var pos = -1
                val months = arrayOf(
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December",
                    ""
                )

//                    val mDropdownList = arrayOfNulls<String>(years.size)
                mAutoCompleteExpiryMonth?.post {

                    val adapter = NoFilterArrayAdapter(
                        requireContext(), R.layout.row_dropdown_menu_popup_item, months
                    )
                    try {
                        mAutoCompleteExpiryMonth?.threshold = 0
//                            mAutoCompleteExpiryMonth?.setAdapter<ArrayAdapter<String?>>(adapter)
                        mAutoCompleteExpiryMonth?.setAdapter(adapter)
                        //mSelectedShiftStat = mApplicationList.get(pos);
                        mAutoCompleteExpiryMonth?.inputType = InputType.TYPE_NULL
                        mAutoCompleteExpiryMonth?.keyListener = null
                        mAutoCompleteExpiryMonth?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                requireActivity().hideSoftKeyboard()
                                // Get the selected item index
                                val selectedMonth = mAutoCompleteExpiryMonth?.text.toString()
                                val selectedIndex = months.indexOfFirst {
                                    it.equals(
                                        selectedMonth, ignoreCase = true
                                    )
                                }
                                mExpiryMonth = if (selectedMonth.equals("", ignoreCase = true)) {
                                    "00"
                                } else {
                                    String.format("%02d", selectedIndex + 1)
                                }
                            }
                        if (mAutoCompleteExpiryMonth?.tag != null && mAutoCompleteExpiryMonth?.tag == "listonly") {
                            setListOnly(requireContext(), mAutoCompleteExpiryMonth!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to Code dropdown
    private suspend fun setDropdownVioType() {
        requireActivity().hideSoftKeyboard()
        if (mAutoComTextViewVioType != null) {
            val mApplicationList = mainActivityViewModel.getVioListFromDataSet()
            var mViolationList = mainActivityViewModel.getViolationListFromDataSet()

//            if(mApplicationList != null && mApplicationList.size > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                var pos = -1
                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].mvio_type.toString()
                    }

                    //  Arrays.sort(mDropdownList);
                    mAutoComTextViewVioType?.post {
                        if (pos >= 0) {
                            mAutoComTextViewVioType?.setText(mApplicationList[pos].mvio_type)
                            mViolationListSelectedItem = mApplicationList[pos]
                            setViolationBaseData(mViolationListSelectedItem, pos)

                            // Mark time field visible for particular violation which have mark value 1
                            if (mEtMarkTime != null && mViolationListSelectedItem!!.mMarkTime.equals(
                                    "1"
                                )
                            ) {
                                mEtMarkTime!!.visibility = View.VISIBLE
                            } else {
                                if (mEtMarkTime != null) mEtMarkTime!!.visibility = View.GONE
                            }
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewVioType?.threshold = 1
                            mAutoComTextViewVioType?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mAutoComTextViewVioType?.onItemClickListener =
                                OnItemClickListener { parent, view, index, id ->
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        val position = getIndexOfViotionaType(
                                            mApplicationList,
                                            parent.getItemAtPosition(index).toString()
                                        )
                                        val vioTYpe = mApplicationList[position].mvio_type

                                        try {
                                            mApplicationList?.get(position)?.mvio_type_code?.let {
                                                mAutoComTextViewVioTypeCode?.setText(
                                                    it.toString()
                                                )
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        try {
                                            mAutoComTextViewVioTypeDescription?.setText(
                                                mApplicationList?.get(position)?.mvio_type_description
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        try {
                                            mAutoComTextViewVioType?.setText(
                                                mApplicationList?.get(
                                                    position
                                                )?.mvio_type
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                        val finalViolationList: MutableList<DatasetResponse> =
                                            ArrayList()
                                        for (violation in mViolationList!!) {
                                            if (violation.mvio_type != null && vioTYpe != null) {
                                                if (violation.mvio_type != null && violation.mvio_type.equals(
                                                        vioTYpe, true
                                                    ) && (selectedZoneCode != null && selectedZoneCode!!.isNotEmpty() && violation.mZoneId.equals(
                                                        selectedZoneCode, true
                                                    ) || mAutoComTextViewZone != null && !mAutoComTextViewZone?.text?.toString()
                                                        ?.isNullOrEmpty()!! && violation.mZoneId.equals(
                                                        mAutoComTextViewZone?.text?.toString(), true
                                                    ))
                                                ) {
                                                    finalViolationList.add(violation)
                                                }
                                            } else {
                                                logD(
                                                    "Violation list",
                                                    "Zone id is null in violation data"
                                                );
                                                finalViolationList.addAll(mApplicationList)
                                                break
                                            }
                                        }
                                        if (finalViolationList!!.size == 0) {
                                            logD(
                                                "Violation list",
                                                "Zone id is null in violation data"
                                            );
                                            finalViolationList.addAll(mViolationList)
                                        }
                                        setDropdownAbbrCode(finalViolationList)

                                    }
                                }
                            if (mAutoComTextViewVioType?.tag != null && mAutoComTextViewVioType?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewVioType!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun getIndexOfViotionaType(list: List<DatasetResponse>, name: String): Int {

        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.mvio_type, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }


    //set value to Code dropdown
    private fun setDropdownAbbrCode(mApplicationList: List<DatasetResponse>?) {
        if (mAutoComTextViewAbbrCode != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                var pos = -1
                var isMatched = false
                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
//                    val mDropdownList2 = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].violation.toString()
//                        mDropdownList2[i] = mApplicationList[i].violationDescription.toString()
                        printLog(
                            "check",
                            mApplicationList[i].violation.toString() + "  " + mApplicationList[i].mJustInTimeCheck
                        )
                        if ((!isMatched && mAutoComTextViewAbbrCode != null && !TextUtils.isEmpty(
                                mAutoComTextViewAbbrCode?.text.toString()
                            ) && mAutoComTextViewAbbrCode?.text.toString().equals(
                                mApplicationList[i].violation, ignoreCase = true
                            )) || mPayBySpaceExpireMeterValue.equals("YES") && mApplicationList[i].timelimitVio != null && mApplicationList[i].timelimitVio.equals(
                                "1"
                            ) || mApplicationList[i].mWarningViolation != null && mApplicationList[i].mWarningViolation.equals(
                                mCitationCountForWarning
                            ) || mApplicationList[i].mSanctionsSticker != null && mbuttonClickScanPage!! > 0 && mApplicationList[i].mSanctionsSticker == mbuttonClickScanPage || mViolationCodeCamera != null && mApplicationList[i].violationCode.equals(
                                mViolationCodeCamera
                            )
                        ) {
                            pos = i
                            isMatched = true
                            /***
                             * Call Last second check api If justintime is  1.0
                             ***/
                            isCallLastSecondCheckAPI =
                                mApplicationList.get(pos).mJustInTimeCheck != null && mApplicationList.get(
                                    pos
                                ).mJustInTimeCheck.equals(
                                    "1.0", true
                                ) || mApplicationList[pos].mJustInTimeCheck != null && mApplicationList[pos].mJustInTimeCheck.equals(
                                    "1", ignoreCase = true
                                )
                            /***
                             * Call meter list validation is  1.0
                             ***/
                            isMeterReqiredViolation =
                                mApplicationList.get(pos).meterlistReq != null && mApplicationList.get(
                                    pos
                                ).meterlistReq.equals(
                                    "1.0", true
                                ) || mApplicationList.get(pos).meterlistReq != null && mApplicationList.get(
                                    pos
                                ).meterlistReq.equals(
                                    "1", true
                                )

                            /*** When get citation value is lock and officer will select violation then
                             * locked this citation
                             */
                            if (mLockCitation.equals("lock", ignoreCase = true)) {
                                setLprLock(lockLprModel, requireContext(), sharedPreference)
                                sharedPreference.write(
                                    SharedPrefKey.LOCKED_LPR_BOOL, true
                                )//true
                            } else {
                                sharedPreference.write(
                                    SharedPrefKey.LOCKED_LPR_BOOL, false
                                )
                            }
                            /**
                             * If selected violation have timelimtvio =1 then we create timing recored
                             * Click red box timing recored
                             * selected those recored which have timelimitvio = 1
                             * then auto timing recored create in ticket details screen
                             */
                            try {
                                isOverTimeParkingViolationForTimeLimit =
                                    mApplicationList[pos].timelimitVio
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            /**
                             * For sanibel if after days value get from violation
                             */
                            try {
                                amountDays =
                                    if (mApplicationList[pos].mAmountDays != null && mApplicationList[pos].mAmountDays!!.isNotEmpty()) mApplicationList[pos].mAmountDays!!.toInt() else amountDaysCitationForm
                                lateFineDays =
                                    if (mApplicationList[pos].mLateFineDays != null && mApplicationList[pos].mLateFineDays!!.isNotEmpty()) mApplicationList[pos].mLateFineDays!!.toInt() else lateFineDaysCitationForm
                                lateFine15Days =
                                    if (mApplicationList[pos].mDue15DateDays != null && mApplicationList[pos].mDue15DateDays!!.isNotEmpty()) mApplicationList[pos].mDue15DateDays!!.toInt() else lateFine15DaysCitationForm
                                lateFine30Days =
                                    if (mApplicationList[pos].mDue30DateDays != null && mApplicationList[pos].mDue30DateDays!!.isNotEmpty()) mApplicationList[pos].mDue30DateDays!!.toInt() else lateFine30Days
                                mZoneMandatoryForViolationCode =
                                    if (mApplicationList[pos].mZoneMandatory != null && mApplicationList[pos].mZoneMandatory!!.isNotEmpty()) mApplicationList[pos].mZoneMandatory!! else "NO"
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            /**
                             * For Rutgers sanction type base on white and red button click
                             */
                            try {
                                sanctionsType =
                                    if (mApplicationList[pos].mSanctionsSticker != null && mApplicationList[pos].mSanctionsSticker!! > 0) mApplicationList[pos].mSanctionsSticker!!.toInt() else 0
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    //  Arrays.sort(mDropdownList);
                    mAutoComTextViewAbbrCode?.post {
                        if (pos >= 0) {
                            mAutoComTextViewAbbrCode?.setText(mApplicationList[pos].violation)
                            mViolationListSelectedItem = mApplicationList[pos]
                            setViolationBaseData(mViolationListSelectedItem, pos)

                            // Mark time field visible for particular violation which have mark value 1
                            if (mEtMarkTime != null && mViolationListSelectedItem!!.mMarkTime.equals(
                                    "1"
                                )
                            ) {
                                mEtMarkTime!!.visibility = View.VISIBLE
                            } else {
                                if (mEtMarkTime != null) mEtMarkTime!!.visibility = View.GONE
                            }
                        }
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mAutoComTextViewAbbrCode?.threshold = 1
                            mAutoComTextViewAbbrCode?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewAbbrCode?.onItemClickListener =
                                OnItemClickListener { parent, view, index, id ->
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        val position = getIndexOfViotiona(
                                            mApplicationList,
                                            parent.getItemAtPosition(index).toString()
                                        )
                                        viewLifecycleOwner.lifecycleScope.async {
                                            mViolationListSelectedItem = mApplicationList[position]
                                            requireActivity().hideSoftKeyboard()
                                            setViolationBaseData(
                                                mViolationListSelectedItem, position
                                            )
                                            try {
                                                mEtLocationDescr?.maxLines = 6
                                                mEtLocationDescr?.setImeOptions(EditorInfo.IME_ACTION_DONE);
                                                mEtLocationDescr?.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                                mEtLocationDescr?.setElegantTextHeight(true);
                                                mEtLocationDescr?.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                                mEtLocationDescr?.setSingleLine(false);

                                                mEtLocationDescr?.setText(mApplicationList[position].violationDescription)

                                                if (mEtMarkTime != null && mViolationListSelectedItem!!.mMarkTime.equals(
                                                        "1"
                                                    )
                                                ) {
                                                    mEtMarkTime!!.visibility = View.VISIBLE
                                                } else {
                                                    if (mEtMarkTime != null) mEtMarkTime!!.visibility =
                                                        View.GONE
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            try {
                                                mAutoComTextViewCode?.setText(mApplicationList[position].violationCode)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            try {
                                                isOverTimeParkingViolationForTimeLimit =
                                                    mApplicationList[position].timelimitVio
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            try {
                                                mZoneMandatoryForViolationCode =
                                                    mApplicationList[position].mZoneMandatory!!
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                        /***
                                         * Call Last second check api If justintime is  1.0
                                         */
                                        isCallLastSecondCheckAPI =
                                            mApplicationList[position].mJustInTimeCheck != null && mApplicationList[position].mJustInTimeCheck.equals(
                                                "1.0", ignoreCase = true
                                            ) || mApplicationList[position].mJustInTimeCheck != null && mApplicationList[position].mJustInTimeCheck.equals(
                                                "1", ignoreCase = true
                                            )

                                        /***
                                         * Call meter list validation is  1.0
                                         ***/
                                        isMeterReqiredViolation =
                                            mApplicationList.get(position).meterlistReq != null && mApplicationList.get(
                                                position
                                            ).meterlistReq.equals(
                                                "1.0", true
                                            ) || mApplicationList.get(position).meterlistReq != null && mApplicationList.get(
                                                position
                                            ).meterlistReq.equals(
                                                "1", true
                                            )

                                        /*** When get citation value is lock and officer will select violation then
                                         * locked this citation
                                         */
                                        if (mLockCitation.equals("lock", ignoreCase = true)) {
                                            lockLprModel?.apply {
                                                mLprNumber = mLprNumber
                                                mMake = mSelectedMake
                                                mModel = mSelectedModel
                                                mColor = mSelectedColor
                                                mAddress = mGisAddress
                                                mViolationCode = mViolationCode
                                                ticketCategory =
                                                    API_CONSTANT_TICKET_CATEGORY_CITATION
                                            }?.let { model ->
                                                setLprLock(
                                                    model, requireContext(), sharedPreference
                                                )
                                                sharedPreference.write(
                                                    SharedPrefKey.LOCKED_LPR_BOOL, true
                                                )
                                            }
                                        } else {
                                            sharedPreference.write(
                                                SharedPrefKey.LOCKED_LPR_BOOL, false
                                            )
                                        }
                                        /***
                                         * PBC  field validation TODO static value from string
                                         */
                                        isPBCReqiredViolation =
                                            (mApplicationList[position].pbcReq != null && mApplicationList[position].pbcReq.equals(
                                                "1.0", ignoreCase = true
                                            ))

                                        /**
                                         * For sanibel if after days value get from violation
                                         */
                                        try {
                                            amountDays =
                                                if (mApplicationList[position].mAmountDays != null && mApplicationList[position].mAmountDays!!.isNotEmpty()) mApplicationList[position].mAmountDays!!.toInt() else amountDaysCitationForm
                                            lateFineDays =
                                                if (mApplicationList[position].mLateFineDays != null && mApplicationList[position].mLateFineDays!!.isNotEmpty()) mApplicationList[position].mLateFineDays!!.toInt() else lateFineDaysCitationForm
                                            lateFine15Days =
                                                if (mApplicationList[position].mDue15DateDays != null && mApplicationList[position].mDue15DateDays!!.isNotEmpty()) mApplicationList[position].mDue15DateDays!!.toInt() else lateFine15DaysCitationForm
                                            lateFine30Days =
                                                if (mApplicationList[position].mDue30DateDays != null && mApplicationList[position].mDue30DateDays!!.isNotEmpty()) mApplicationList[position].mDue30DateDays!!.toInt() else lateFine30Days
                                            AppUtils.autoFilledRemarkBasedOnExpiryMonthAndYear(
                                                requireContext(),
                                                mEtLocationRemarks,
                                                mAutoCompleteExpiryMonth,
                                                mAutoCompleteDate
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                        /**
                                         * For Rutgers sanction type base on white and red button click
                                         */
                                        try {
                                            sanctionsType =
                                                if (mApplicationList[position].mSanctionsSticker != null && mApplicationList[position].mSanctionsSticker!! > 0) mApplicationList[position].mSanctionsSticker!!.toInt() else 0
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            if (mAutoComTextViewAbbrCode?.tag != null && mAutoComTextViewAbbrCode?.tag == "listonly") {
                                setListOnly(requireContext(), mAutoComTextViewAbbrCode!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun getIndexOfViotiona(list: List<DatasetResponse>, name: String): Int {

        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.violation, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }


    private fun setViolationBaseData(mApplicationList: DatasetResponse?, position: Int) {
        try {
            if (mApplicationList != null) {
                val dollerSign: String = getString(R.string.doller_sign)

                if (mCheckboxType1 != null && mCheckboxType1.tag == "Amount0" || mCheckboxType3 != null && mCheckboxType3.tag == "Amount0" || mCheckboxType2 != null && mCheckboxType2.tag == "Amount0" || position >= 0) {
                    if (mCheckboxType1 != null && mCheckboxType1.tag == "Amount0" && mCheckboxType1.isChecked || mCheckboxType3 != null && mCheckboxType3.tag == "Amount0" && mCheckboxType3.isChecked || mCheckboxType2 != null && mCheckboxType2.tag == "Amount0" && mCheckboxType2.isChecked) {
                        if (mAutoComTextViewAmount != null) {
                            mAutoComTextViewAmount?.setAmount(dollerSign, DEFAULT_AMOUNT_VALUE)
                        }
                        if (mAutoComTextViewAmountDueDate != null) {
                            mAutoComTextViewAmountDueDate?.setAmount(
                                dollerSign, DEFAULT_AMOUNT_VALUE
                            )
                        }
                        if (AutoComTextViewDueDate != null) {
                            AutoComTextViewDueDate?.setAmount(dollerSign, DEFAULT_AMOUNT_VALUE)
                        }
                        if (AutoComTextViewDueDate30 != null) {
                            AutoComTextViewDueDate30?.setAmount(dollerSign, DEFAULT_AMOUNT_VALUE)
                        }
                        if (AutoComTextViewDueDate45 != null) {
                            AutoComTextViewDueDate45?.setAmount(dollerSign, DEFAULT_AMOUNT_VALUE)
                        }
                        try {
                            dueCost = "0.0"
                            dueParkingFee = "0.0"
                            dueCitationFee = "0.0"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            if (mAutoComTextViewAmount != null) {
                                if (fineCalculatedField == API_CONSTANT_CALCULATED_FIELD_ESCALATED && mEscalatedLprNumber!!.equals(
                                        mAutoComTextViewLicensePlate!!.text.toString()
                                    ) && (mEscalatedState!!.equals(mAutoComTextViewState!!.text) || mEscalatedState!!.equals(
                                        mState2DigitCode
                                    ))
                                ) {
                                    mAutoComTextViewAmount?.setAmount(
                                        dollerSign,
                                        mApplicationList.getEscalatedFine(unpaidCitationCount)
                                    )
                                } else {
                                    // Carta Off street only add extera lot amount in fine
                                    if (mAutoComTextViewZone != null && mAutoComTextViewZone?.text.toString()
                                            .equals("OFF STREET", ignoreCase = true)
                                    ) {
                                        if (BuildConfig.FLAVOR.equals(
                                                Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                            ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                                .equals("ILLEGAL HANDICAPPED") || BuildConfig.FLAVOR.equals(
                                                Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                            ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                                .equals("ILLEGAL HANDICAPPED PAID")
                                        ) {
                                            mAutoComTextViewAmount?.setAmount(
                                                dollerSign,
                                                mApplicationList.violationFine.nullSafety()
                                                    .toString()
                                            )
                                        } else {
                                            mAutoComTextViewAmount?.setAmount(
                                                dollerSign,
                                                (mApplicationList.violationFine!!.toDouble() + mViolationExtraAmount!!.toInt()).toString()
                                            )
                                        }
                                    } else if (mApplicationList.mFlatFineByLot == 1) {
                                        mAutoComTextViewAmount?.setAmount(
                                            dollerSign,
                                            (mApplicationList.violationFine!!.toDouble() + mViolationExtraAmount).toString()
                                        )
                                    } else {
                                        mAutoComTextViewAmount?.setAmount(
                                            dollerSign,
                                            mApplicationList.violationFine.nullSafety().toString()
                                        )
                                    }

                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            if (mAutoComTextViewAmountDueDate != null) {
                                if (mAutoComTextViewZone != null && mAutoComTextViewZone?.text.toString()
                                        .equals("OFF STREET", ignoreCase = true)
                                ) {
                                    if (BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                            .equals("ILLEGAL HANDICAPPED") || BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                            .equals("ILLEGAL HANDICAPPED PAID")
                                    ) {
                                        mAutoComTextViewAmountDueDate?.setAmount(
                                            dollerSign,
                                            mApplicationList.mViolationLateFine.nullSafety()
                                                .toString()
                                        )
                                    } else {
                                        mAutoComTextViewAmountDueDate?.setAmount(
                                            dollerSign,
                                            (mApplicationList.mViolationLateFine!!.toDouble() + mViolationExtraAmount!!.toInt()).toString()
                                        )
                                    }
                                } else {
                                    mAutoComTextViewAmountDueDate?.setAmount(
                                        dollerSign,
                                        mApplicationList.mViolationLateFine.nullSafety().toString()
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            if (AutoComTextViewDueDate != null) {

                                if (mAutoComTextViewZone != null && mAutoComTextViewZone?.text.toString()
                                        .equals("OFF STREET", ignoreCase = true)
                                ) {
                                    if (BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                            .equals("ILLEGAL HANDICAPPED") || BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                            .equals("ILLEGAL HANDICAPPED PAID")
                                    ) {
                                        AutoComTextViewDueDate?.setAmount(
                                            dollerSign,
                                            mApplicationList.due_15_days.nullSafety().toString()
                                        )
                                    } else {
                                        AutoComTextViewDueDate?.setAmount(
                                            dollerSign,
                                            (mApplicationList.due_15_days!!.toDouble() + mViolationExtraAmount!!.toInt()).toString()
                                        )
                                    }
                                } else {
                                    AutoComTextViewDueDate?.setAmount(
                                        dollerSign,
                                        mApplicationList.due_15_days.nullSafety().toString()
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            if (AutoComTextViewDueDate30 != null) {

                                if (mAutoComTextViewZone != null && mAutoComTextViewZone?.text.toString()
                                        .equals("OFF STREET", ignoreCase = true)
                                ) {
                                    if (BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                            .equals("ILLEGAL HANDICAPPED") || BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                            .equals("ILLEGAL HANDICAPPED PAID")
                                    ) {
                                        AutoComTextViewDueDate30?.setAmount(
                                            dollerSign,
                                            mApplicationList.due_30_days.nullSafety().toString()
                                        )
                                    } else {
                                        AutoComTextViewDueDate30?.setAmount(
                                            dollerSign,
                                            (mApplicationList.due_30_days!!.toDouble() + mViolationExtraAmount!!.toInt()).toString()
                                        )
                                    }
                                } else {
                                    AutoComTextViewDueDate30?.setAmount(
                                        dollerSign,
                                        mApplicationList.due_30_days.nullSafety().toString()
                                    )
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            if (AutoComTextViewDueDate45 != null) {
                                if (mAutoComTextViewZone != null && mAutoComTextViewZone?.text.toString()
                                        .equals("OFF STREET", ignoreCase = true)
                                ) {
                                    if (BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                            .equals("ILLEGAL HANDICAPPED") || BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                                        ) && mAutoComTextViewAbbrCode!!.text!!.toString()
                                            .equals("ILLEGAL HANDICAPPED PAID")
                                    ) {
                                        AutoComTextViewDueDate45?.setAmount(
                                            dollerSign,
                                            mApplicationList.due_45_days.nullSafety().toString()
                                        )
                                    } else {
                                        AutoComTextViewDueDate45?.setAmount(
                                            dollerSign,
                                            (mApplicationList.due_45_days!!.toDouble() + mViolationExtraAmount!!.toInt()).toString()
                                        )
                                    }
                                } else {
                                    AutoComTextViewDueDate45?.setAmount(
                                        dollerSign,
                                        mApplicationList.due_45_days.nullSafety().toString()
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            dueCost = (mApplicationList.cost.toString())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            dueParkingFee = (mApplicationList.parkingFee.toString())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            dueCitationFee = (mApplicationList.citationFee.toString())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    try {
                        mEtLocationDescr?.setText(mApplicationList.violationDescription)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        mAutoComTextViewCode?.setText(mApplicationList.violationCode)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {
                        dueTotalNow = (mApplicationList.total_due_now.toString())
                        if (AutoComTextViewTotalDue != null) {
                            AutoComTextViewTotalDue?.setAmount(
                                dollerSign, dueTotalNow.nullSafety().toString()
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        exportCode = mApplicationList.mExportCode.nullSafety()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetViolationSectionField() {
        if (mAutoComTextViewAmount != null) {
            mAutoComTextViewAmount?.setText("")
        }
        if (mEtLocationDescr != null) {
            mEtLocationDescr?.setText("")
        }
        if (mAutoComTextViewAmountDueDate != null) {
            mAutoComTextViewAmountDueDate?.setText("")
        }
        if (AutoComTextViewDueDate != null) {
            AutoComTextViewDueDate?.setText("")
        }
        if (AutoComTextViewDueDate30 != null) {
            AutoComTextViewDueDate30?.setText("")
        }
        if (AutoComTextViewDueDate45 != null) {
            AutoComTextViewDueDate45?.setText("")
        }
        if (mAutoComTextViewAbbrCode != null) {
            mAutoComTextViewAbbrCode?.setText("")
        }
        if (mAutoComTextViewCode != null) {
            mAutoComTextViewCode?.setText("")
        }
        if (mAutoComTextViewVioType != null) {
            mAutoComTextViewVioType?.setText("")
        }
        if (mAutoComTextViewVioTypeCode != null) {
            mAutoComTextViewVioTypeCode?.setText("")
        }
        if (mAutoComTextViewVioTypeDescription != null) {
            mAutoComTextViewVioTypeDescription?.setText("")
        }
    }

    //save citation issurance to Databse
    private fun saveCitationIssurance(mLatestCitation: CitationBookletModel?) {

        DialogUtil.showLoader(requireContext(), getString(R.string.scr_message_please_wait))

        class SaveCitationIssuranceTask : AsyncTask<Void?, Int?, String?>() {
            override fun doInBackground(vararg voids: Void?): String? {

                viewLifecycleOwner.lifecycleScope.launch {
                var mBookletStatus = 0
                //get citation issurance model
                mBookletStatus =
                    citationFormScreenViewModel.getBookletStatus(mLatestCitation!!.citationBooklet)
                val mIssDatabase = CitationInsurranceDatabaseModel()
                val mIssModel = CitationIssuranceModel()
                mIssModel.timingId = if (mTimingID != null) mTimingID else ""
                mIssModel.ticketNumber = mTvTicketNumber.text.toString()
                mIssModel.isStatus_ticketNumber = cit_numPrint
                mIssModel.mPrintOrderTickerNumber =
                    if (mPrintOrderMap.containsKey("citNumber")) mPrintOrderMap["citNumber"]!!.printOrder else 0.0
                mIssModel.mPrintLayoutOrderTickerNumber =
                    if (mPrintOrderMap.containsKey("citNumber")) mPrintOrderMap["citNumber"]!!.layoutOrder else ""
                mIssModel.ticketNumberLabel =
                    if (mPrintOrderMap.containsKey("citNumber")) mPrintOrderMap["citNumber"]!!.labelName else ""
                mIssModel.mTicketNumberFontSize =
                    if (mPrintOrderMap.containsKey("citNumber")) mPrintOrderMap["citNumber"]!!.printFont?.toInt()!! else 0
                mIssModel.mTicketNumberX =
                    if (mPrintOrderMap.containsKey("citNumber")) mPrintOrderMap["citNumber"]!!.printAxisX?.toDouble()!! else 0.0
                mIssModel.mTicketNumberY =
                    if (mPrintOrderMap.containsKey("citNumber")) mPrintOrderMap["citNumber"]!!.printAxisY?.toDouble()!! else 0.0
                mIssModel.mTicketNumberColumnSize =
                    if (mPrintOrderMap.containsKey("citNumber")) mPrintOrderMap["citNumber"]!!.printColumnSize!!.toInt() else 1

                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
                    ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
                ) {
                    sharedPreference.write(
                        SharedPrefKey.CITATION_NUMBER_FOR_PRINT, mIssModel.ticketNumber
                    )

                    sharedPreference.write(
                        SharedPrefKey.CITATION_NUMBER_LABEL_FOR_PRINT, mIssModel.ticketNumberLabel
                    )

                    sharedPreference.write(
                        SharedPrefKey.CITATION_NUMBER_FOR_PRINT_X,
                        mPrintOrderMap["citNumber"]!!.printAxisX
                    )

                    sharedPreference.write(
                        SharedPrefKey.CITATION_NUMBER_FOR_PRINT_Y,
                        mPrintOrderMap["citNumber"]!!.printAxisY
                    )

                    sharedPreference.write(
                        SharedPrefKey.CITATION_NUMBER_FOR_PRINT_FONT,
                        mPrintOrderMap["citNumber"]!!.printFont
                    )
                }

                mIssModel.ticketDate = mTvTicketDate.text.toString()
                mIssModel.ticketDatePrint = mTvTicketDate.tag.toString()
                mIssModel.isStatus_ticket_date = timestampPrint
                mIssModel.mPrintOrderTicketDate =
                    if (mPrintOrderMap.containsKey("timestamPrint")) mPrintOrderMap["timestamPrint"]!!.printOrder else 0.0
                mIssModel.mPrintLayoutOrderTicketDate =
                    if (mPrintOrderMap.containsKey("timestamPrint")) mPrintOrderMap["timestamPrint"]!!.layoutOrder else ""
                mIssModel.ticketDateLabel =
                    if (mPrintOrderMap.containsKey("timestamPrint")) mPrintOrderMap["timestamPrint"]!!.labelName else ""
                mIssModel.mticketDateFont =
                    if (mPrintOrderMap.containsKey("timestamPrint")) mPrintOrderMap["timestamPrint"]!!.printFont!!.toInt()!! else 0
                mIssModel.mTicketDateX =
                    if (mPrintOrderMap.containsKey("timestamPrint")) mPrintOrderMap["timestamPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                mIssModel.mTicketDateY =
                    if (mPrintOrderMap.containsKey("timestamPrint")) mPrintOrderMap["timestamPrint"]!!.printAxisY!!.toDouble() else 0.0
                mIssModel.mTicketDateColumnSize =
                    if (mPrintOrderMap.containsKey("timestamPrint")) mPrintOrderMap["timestamPrint"]!!.printColumnSize!!.toInt() else 1

                mIssModel.ticketTime = mTvTicketDate.text.toString()
                mIssModel.ticketTimePrint = mTvTicketDate.tag.toString()
                mIssModel.isStatus_ticket_time = TimeFieldPrint
                mIssModel.mPrintOrderTicketTime =
                    if (mPrintOrderMap.containsKey("TimeFieldPrint")) mPrintOrderMap["TimeFieldPrint"]!!.printOrder else 0.0
                mIssModel.mPrintLayoutOrderTicketTime =
                    if (mPrintOrderMap.containsKey("TimeFieldPrint")) mPrintOrderMap["TimeFieldPrint"]!!.layoutOrder else ""
                mIssModel.ticketTimeLabel =
                    if (mPrintOrderMap.containsKey("TimeFieldPrint")) mPrintOrderMap["TimeFieldPrint"]!!.labelName else ""
                mIssModel.mticketTimeFont =
                    if (mPrintOrderMap.containsKey("TimeFieldPrint")) mPrintOrderMap["TimeFieldPrint"]!!.printFont!!.toInt()!! else 0
                mIssModel.mTicketTimeX =
                    if (mPrintOrderMap.containsKey("TimeFieldPrint")) mPrintOrderMap["TimeFieldPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                mIssModel.mticketTimeY =
                    if (mPrintOrderMap.containsKey("TimeFieldPrint")) mPrintOrderMap["TimeFieldPrint"]!!.printAxisY!!.toDouble() else 0.0
                mIssModel.mTicketTimeColumnSize =
                    if (mPrintOrderMap.containsKey("TimeFieldPrint")) mPrintOrderMap["TimeFieldPrint"]!!.printColumnSize!!.toInt() else 1

                mIssModel.ticketWeek = getDayOfWeek()
                mIssModel.ticketWeekPrint = mIssModel.ticketWeek
                mIssModel.isStatus_ticket_week = DayOfWeekPrint
                mIssModel.mPrintOrderTicketWeek =
                    if (mPrintOrderMap.containsKey("DayOfWeekFieldPrint")) mPrintOrderMap["DayOfWeekFieldPrint"]!!.printOrder else 0.0
                mIssModel.mPrintLayoutOrderTicketWeek =
                    if (mPrintOrderMap.containsKey("DayOfWeekFieldPrint")) mPrintOrderMap["DayOfWeekFieldPrint"]!!.layoutOrder else ""
                mIssModel.ticketWeekLabel =
                    if (mPrintOrderMap.containsKey("DayOfWeekFieldPrint")) mPrintOrderMap["DayOfWeekFieldPrint"]!!.labelName else ""
                mIssModel.mTicketWeekFont =
                    if (mPrintOrderMap.containsKey("DayOfWeekFieldPrint")) mPrintOrderMap["DayOfWeekFieldPrint"]!!.printFont!!.toInt()!! else 0
                mIssModel.mTicketWeekX =
                    if (mPrintOrderMap.containsKey("DayOfWeekFieldPrint")) mPrintOrderMap["DayOfWeekFieldPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                mIssModel.mTicketWeekY =
                    if (mPrintOrderMap.containsKey("DayOfWeekFieldPrint")) mPrintOrderMap["DayOfWeekFieldPrint"]!!.printAxisY!!.toDouble() else 0.0
                mIssModel.mTicketWeekColumnSize =
                    if (mPrintOrderMap.containsKey("DayOfWeekFieldPrint")) mPrintOrderMap["DayOfWeekFieldPrint"]!!.printColumnSize!!.toInt() else 1

                try {
                    mIssModel.code2010 =
                        if (code2010 != null && code2010.isNotEmpty()) code2010 else "2010"
                    mIssModel.code2010Print = mIssModel.code2010Print
                    mIssModel.isStatus_code2010 = codePrint2010
                    mIssModel.mPrintOrdercode2010 =
                        if (mPrintOrderMap.containsKey("codePrint2010")) mPrintOrderMap["codePrint2010"]!!.printOrder else 0.0
                    mIssModel.mPrintLayoutOrdercode2010 =
                        if (mPrintOrderMap.containsKey("codePrint2010")) mPrintOrderMap["codePrint2010"]!!.layoutOrder else ""
                    mIssModel.code2010Label =
                        if (mPrintOrderMap.containsKey("codePrint2010")) mPrintOrderMap["codePrint2010"]!!.labelName else ""
                    mIssModel.mCode2010Font =
                        if (mPrintOrderMap.containsKey("codePrint2010")) mPrintOrderMap["codePrint2010"]!!.printFont!!.toInt()!! else 0
                    mIssModel.mCode2010X =
                        if (mPrintOrderMap.containsKey("codePrint2010")) mPrintOrderMap["codePrint2010"]!!.printAxisX!!.toDouble()!! else 0.0
                    mIssModel.mCode2010Y =
                        if (mPrintOrderMap.containsKey("codePrint2010")) mPrintOrderMap["codePrint2010"]!!.printAxisY!!.toDouble() else 0.0
                    mIssModel.mCode2010ColumnSize =
                        if (mPrintOrderMap.containsKey("codePrint2010")) mPrintOrderMap["codePrint2010"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true)) {
                        mIssModel.hearingDate = AppUtils.hearingDateAfterThresholdDifferenceDays(
                            requireContext(), HEARING_DATE_DAY_DIFFERENCE_THRESHOLD
                        )
                    } else if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ORLEANS, true
                        ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true)
                    ) {
                        mIssModel.hearingDate =
                            mAutoComTextViewDecalNum?.text?.toString() + " " + mAutoComTextViewBodyStyle?.text?.toString()!!
                                .replace(" ", "")
                    } else {
                        mIssModel.hearingDate =
                            if (hearingThresholdDay != null && hearingThresholdDay > 0) {
                                AppUtils.hearingDateAfterThresholdDay(
                                    requireContext(), hearingThresholdDay
                                )
                            } else {
                                ""
                            }
                    }
                    mIssModel.hearingDatePrint = mIssModel.hearingDatePrint
                    mIssModel.isStatus_hearingDate = hearingDate
                    mIssModel.mPrintOrderHearingDate =
                        if (mPrintOrderMap.containsKey("HearingDate")) mPrintOrderMap["HearingDate"]!!.printOrder else 0.0
                    mIssModel.mPrintLayoutOrderHearingDate =
                        if (mPrintOrderMap.containsKey("HearingDate")) mPrintOrderMap["HearingDate"]!!.layoutOrder else ""
                    mIssModel.hearingDateLabel =
                        if (mPrintOrderMap.containsKey("HearingDate")) mPrintOrderMap["HearingDate"]!!.labelName else ""
                    mIssModel.mHearingDateFont =
                        if (mPrintOrderMap.containsKey("HearingDate")) mPrintOrderMap["HearingDate"]!!.printFont!!.toInt()!! else 0
                    mIssModel.mHearingDateX =
                        if (mPrintOrderMap.containsKey("HearingDate")) mPrintOrderMap["HearingDate"]!!.printAxisX!!.toDouble()!! else 0.0
                    mIssModel.mHearingDateY =
                        if (mPrintOrderMap.containsKey("HearingDate")) mPrintOrderMap["HearingDate"]!!.printAxisY!!.toDouble() else 0.0
                    mIssModel.mHearingDateColumnSize =
                        if (mPrintOrderMap.containsKey("HearingDate")) mPrintOrderMap["HearingDate"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mIssModel.hearingDatePrint = mIssModel.hearingDescriptionPrint
                    mIssModel.isStatus_hearingDescription = hearingDescription
                    mIssModel.mPrintOrderHearingDescription =
                        if (mPrintOrderMap.containsKey("HearingDescription")) mPrintOrderMap["HearingDescription"]!!.printOrder else 0.0
                    mIssModel.mPrintLayoutOrderHearingDescription =
                        if (mPrintOrderMap.containsKey("HearingDescription")) mPrintOrderMap["HearingDescription"]!!.layoutOrder else ""
                    mIssModel.hearingDescriptionLabel =
                        if (mPrintOrderMap.containsKey("HearingDescription")) mPrintOrderMap["HearingDescription"]!!.labelName else ""
                    mIssModel.hearingDescription =
                        if (mPrintOrderMap.containsKey("HearingDescription")) mPrintOrderMap["HearingDescription"]!!.labelName else ""
                    mIssModel.mHearingDescriptionFont =
                        if (mPrintOrderMap.containsKey("HearingDescription")) mPrintOrderMap["HearingDescription"]!!.printFont!!.toInt()!! else 0
                    mIssModel.mHearingDescriptionX =
                        if (mPrintOrderMap.containsKey("HearingDescription")) mPrintOrderMap["HearingDescription"]!!.printAxisX!!.toDouble()!! else 0.0
                    mIssModel.mHearingDescriptionY =
                        if (mPrintOrderMap.containsKey("HearingDescription")) mPrintOrderMap["HearingDescription"]!!.printAxisY!!.toDouble() else 0.0
                    mIssModel.mHearingDesriptionColumnSize =
                        if (mPrintOrderMap.containsKey("HearingDescription")) mPrintOrderMap["HearingDescription"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mIssModel.officerDescriptionPrint = ""
                    mIssModel.isStatus_OfficerDescription = isOfficerDescription
                    mIssModel.mPrintOrderOfficerDescription =
                        if (mPrintOrderMap.containsKey("officerDescription")) mPrintOrderMap["officerDescription"]!!.printOrder else 0.0
                    mIssModel.mPrintLayoutOrderOfficerDescription =
                        if (mPrintOrderMap.containsKey("officerDescription")) mPrintOrderMap["officerDescription"]!!.layoutOrder else ""
                    mIssModel.officerDescriptionLabel =
                        if (mPrintOrderMap.containsKey("officerDescription")) mPrintOrderMap["officerDescription"]!!.labelName else ""
                    mIssModel.officerDescription =
                        if (mPrintOrderMap.containsKey("officerDescription")) mPrintOrderMap["officerDescription"]!!.labelName else ""
                    mIssModel.mOfficerDescriptionFont =
                        if (mPrintOrderMap.containsKey("officerDescription")) mPrintOrderMap["officerDescription"]!!.printFont!!.toInt()!! else 0
                    mIssModel.mOfficerDescriptionX =
                        if (mPrintOrderMap.containsKey("officerDescription")) mPrintOrderMap["officerDescription"]!!.printAxisX!!.toDouble()!! else 0.0
                    mIssModel.mOfficerDescriptionY =
                        if (mPrintOrderMap.containsKey("officerDescription")) mPrintOrderMap["officerDescription"]!!.printAxisY!!.toDouble() else 0.0
                    mIssModel.mOfficerDescriptionColumnSize =
                        if (mPrintOrderMap.containsKey("officerDescription")) mPrintOrderMap["officerDescription"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mIssModel.ticketType = mTicketType
                mIssModel.ticketType2 = mTicketType2
                mIssModel.ticketType3 = mTicketType3
                mIssModel.isStatus_ticket_type = warningPrint
                mIssModel.mPrintOrderTicketType =
                    if (mPrintOrderMap.containsKey("warningPrint")) mPrintOrderMap["warningPrint"]!!.printOrder else 0.0
                mIssModel.mPrintLayoutOrderTicketType =
                    if (mPrintOrderMap.containsKey("warningPrint")) mPrintOrderMap["warningPrint"]!!.layoutOrder else ""
                mIssModel.mTicketTypeFont =
                    if (mPrintOrderMap.containsKey("warningPrint")) mPrintOrderMap["warningPrint"]!!.printFont!!.toInt()!! else 0
                mIssModel.mTicketTypeX =
                    if (mPrintOrderMap.containsKey("warningPrint")) mPrintOrderMap["warningPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                mIssModel.mTicketTypeY =
                    if (mPrintOrderMap.containsKey("warningPrint")) mPrintOrderMap["warningPrint"]!!.printAxisY!!.toDouble() else 0.0
                mIssModel.mTicketTypeColumnSize =
                    if (mPrintOrderMap.containsKey("warningPrint")) mPrintOrderMap["warningPrint"]!!.printColumnSize!!.toInt() else 1

                /**
                 * Citation Data
                 */
                val officerModel = CitationOfficerModel()

                officerModel.officerDetails =
                    mWelcomeFormData!!.officerFirstName + " " + mWelcomeFormData!!.officerLastName
                officerModel.isStatus_officer_details = officer_namePrint
                officerModel.mPrintOrderOfficerDetails =
                    if (mPrintOrderMap.containsKey("officernamePrint")) mPrintOrderMap["officernamePrint"]!!.printOrder else 0.0
                officerModel.mPrintLayoutOrderOfficerDetails =
                    if (mPrintOrderMap.containsKey("officernamePrint")) mPrintOrderMap["officernamePrint"]!!.layoutOrder else ""
                officerModel.officerDetailsLabel =
                    if (mPrintOrderMap.containsKey("officernamePrint")) mPrintOrderMap["officernamePrint"]!!.labelName else ""
                officerModel.mOfficerDetailsFont =
                    if (mPrintOrderMap.containsKey("officernamePrint")) mPrintOrderMap["officernamePrint"]!!.printFont!!.toInt()!! else 0
                officerModel.mOfficerDetailsX =
                    if (mPrintOrderMap.containsKey("officernamePrint")) mPrintOrderMap["officernamePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                officerModel.mOfficerDetailsY =
                    if (mPrintOrderMap.containsKey("officernamePrint")) mPrintOrderMap["officernamePrint"]!!.printAxisY!!.toDouble() else 0.0
                officerModel.mOfficerDetailsColumnSize =
                    if (mPrintOrderMap.containsKey("officernamePrint")) mPrintOrderMap["officernamePrint"]!!.printColumnSize!!.toInt() else 1


                officerModel.officerId = mWelcomeFormData!!.siteOfficerId
                officerModel.isStatus_officer_id = false
                officerModel.badgeId =
                    if (mTvBadgeId != null) mTvBadgeId!!.text.toString() else "" // mWelcomeFormData!!.officerBadgeId
                officerModel.isStatus_badge_id = badge_idPrint
                officerModel.mPrintOrderBadgeId =
                    if (mPrintOrderMap.containsKey("badgePrint")) mPrintOrderMap["badgePrint"]!!.printOrder else 0.0
                officerModel.mPrintLayoutOrderBadgeId =
                    if (mPrintOrderMap.containsKey("badgePrint")) mPrintOrderMap["badgePrint"]!!.layoutOrder else ""
                officerModel.badgeIdLabel =
                    if (mPrintOrderMap.containsKey("badgePrint")) mPrintOrderMap["badgePrint"]!!.labelName else ""
                officerModel.mBadgeIdFont =
                    if (mPrintOrderMap.containsKey("badgePrint")) mPrintOrderMap["badgePrint"]!!.printFont!!.toInt()!! else 0
                officerModel.mBadgeIdX =
                    if (mPrintOrderMap.containsKey("badgePrint")) mPrintOrderMap["badgePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                officerModel.mBadgeIdY =
                    if (mPrintOrderMap.containsKey("badgePrint")) mPrintOrderMap["badgePrint"]!!.printAxisY!!.toDouble() else 0.0
                officerModel.mBadgeIdColumnSize =
                    if (mPrintOrderMap.containsKey("badgePrint")) mPrintOrderMap["badgePrint"]!!.printColumnSize!!.toInt() else 1

//                officerModel.beat = mWelcomeFormData!!.officerBeatName
                if (mTvBeat != null) {
                    officerModel.beat = mTvBeat!!.text.toString()
                } else {
                    officerModel.beat = ""
                }
                officerModel.isStatus_beat = beatPrint
                officerModel.mPrintOrderBeat =
                    if (mPrintOrderMap.containsKey("beatPrint")) mPrintOrderMap["beatPrint"]!!.printOrder else 0.0
                officerModel.mPrintLayoutOrderBeat =
                    if (mPrintOrderMap.containsKey("beatPrint")) mPrintOrderMap["beatPrint"]!!.layoutOrder else ""
                officerModel.beatLabel =
                    if (mPrintOrderMap.containsKey("beatPrint")) mPrintOrderMap["beatPrint"]!!.labelName else ""
                officerModel.mBeatFont =
                    if (mPrintOrderMap.containsKey("beatPrint")) mPrintOrderMap["beatPrint"]!!.printFont!!.toInt()!! else 0
                officerModel.mBeatX =
                    if (mPrintOrderMap.containsKey("beatPrint")) mPrintOrderMap["beatPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                officerModel.mBeatY =
                    if (mPrintOrderMap.containsKey("beatPrint")) mPrintOrderMap["beatPrint"]!!.printAxisY!!.toDouble() else 0.0
                officerModel.mBeatColumnSize =
                    if (mPrintOrderMap.containsKey("beatPrint")) mPrintOrderMap["beatPrint"]!!.printColumnSize!!.toInt() else 1

                officerModel.squad = mWelcomeFormData!!.officerSquad
                officerModel.isStatus_squad = squadPrint
                officerModel.mPrintOrderSquad =
                    if (mPrintOrderMap.containsKey("squadPrint")) mPrintOrderMap["squadPrint"]!!.printOrder else 0.0
                officerModel.mPrintLayoutOrderSquad =
                    if (mPrintOrderMap.containsKey("squadPrint")) mPrintOrderMap["squadPrint"]!!.layoutOrder else ""
                officerModel.squadLable =
                    if (mPrintOrderMap.containsKey("squadPrint")) mPrintOrderMap["squadPrint"]!!.labelName else ""
                officerModel.mSquadFont =
                    if (mPrintOrderMap.containsKey("squadPrint")) mPrintOrderMap["squadPrint"]!!.printFont!!.toInt()!! else 0
                officerModel.mSquadX =
                    if (mPrintOrderMap.containsKey("squadPrint")) mPrintOrderMap["squadPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                officerModel.mSquadY =
                    if (mPrintOrderMap.containsKey("squadPrint")) mPrintOrderMap["squadPrint"]!!.printAxisY!!.toDouble() else 0.0
                officerModel.mSquadColumnSize =
                    if (mPrintOrderMap.containsKey("squadPrint")) mPrintOrderMap["squadPrint"]!!.printColumnSize!!.toInt() else 1

                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)) {
                    officerModel.agency =
                        AppUtils.removeSpecialCharacterFromString(mTvAgency!!.text.toString())
                } else if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
                    ) || BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
                    ) || BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true
                    )
                ) {
                    officerModel.agency = (if (mTvAgency != null && mTvAgency!!.text.toString()
                            .isNotEmpty()
                    ) mTvAgency!!.text.toString() else mWelcomeFormData!!.agency!!)
                } else {
                    officerModel.agency = AppUtils.removeSpecialCharacterFromString(
                        if (mTvAgency != null && mTvAgency!!.text.toString()
                                .isNotEmpty()
                        ) mTvAgency!!.text.toString() else mWelcomeFormData!!.agency!!
                    )
                }
                officerModel.isStatus_agency = agencyPrint
                officerModel.mPrintOrderAgency =
                    if (mPrintOrderMap.containsKey("agencyPrint")) mPrintOrderMap["agencyPrint"]!!.printOrder else 0.0
                officerModel.mPrintLayoutOrderAgency =
                    if (mPrintOrderMap.containsKey("agencyPrint")) mPrintOrderMap["agencyPrint"]!!.layoutOrder else ""
                officerModel.agencyLabel =
                    if (mPrintOrderMap.containsKey("agencyPrint")) mPrintOrderMap["agencyPrint"]!!.labelName else ""
                officerModel.mAgencyFont =
                    if (mPrintOrderMap.containsKey("agencyPrint")) mPrintOrderMap["agencyPrint"]!!.printFont!!.toInt()!! else 0
                officerModel.mAgencyX =
                    if (mPrintOrderMap.containsKey("agencyPrint")) mPrintOrderMap["agencyPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                officerModel.mAgencyY =
                    if (mPrintOrderMap.containsKey("agencyPrint")) mPrintOrderMap["agencyPrint"]!!.printAxisY!!.toDouble() else 0.0
                officerModel.mAgencyColumnSize =
                    if (mPrintOrderMap.containsKey("agencyPrint")) mPrintOrderMap["agencyPrint"]!!.printColumnSize!!.toInt() else 1


                if (mAutoComTextViewZone != null && !TextUtils.isEmpty(mAutoComTextViewZone?.text)) {
                    officerModel.zone = mAutoComTextViewZone?.text.toString()
                } else {
                    officerModel.zone = mWelcomeFormData!!.officerZoneName
                }
                try {
//                    officerModel.setZone(mWelcomeFormData.getOfficerZone());
                    officerModel.isStatus_zone = zonePrint
                    officerModel.mPrintOrderZone =
                        if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printOrder else 0.0
                    officerModel.mPrintLayoutOrderZone =
                        if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.layoutOrder else ""
                    officerModel.zoneLabel =
                        if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.labelName else ""
                    officerModel.mZoneFont =
                        if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printFont!!.toInt()!! else 0
                    officerModel.mZoneX =
                        if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    officerModel.mZoneY =
                        if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printAxisY!!.toDouble() else 0.0
                    officerModel.mZoneColumnSize =
                        if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
//                    officerModel.setShift(mWelcomeFormData.getShift());
                    officerModel.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                    officerModel.mPrintOrderShift =
                        if (mPrintOrderMap.containsKey("shiftPrint")) mPrintOrderMap["shiftPrint"]!!.printOrder else 0.0
                    officerModel.mPrintLayoutOrderShift =
                        if (mPrintOrderMap.containsKey("shiftPrint")) mPrintOrderMap["shiftPrint"]!!.layoutOrder else ""
                    officerModel.shiftLabel =
                        if (mPrintOrderMap.containsKey("shiftPrint")) mPrintOrderMap["shiftPrint"]!!.labelName else ""
                    officerModel.mShiftFont =
                        if (mPrintOrderMap.containsKey("shiftPrint")) mPrintOrderMap["shiftPrint"]!!.printFont!!.toInt()!! else 0
                    officerModel.mShiftX =
                        if (mPrintOrderMap.containsKey("shiftPrint")) mPrintOrderMap["shiftPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    officerModel.mShiftY =
                        if (mPrintOrderMap.containsKey("shiftPrint")) mPrintOrderMap["shiftPrint"]!!.printAxisY!!.toDouble() else 0.0
                    officerModel.mShiftColumnSize =
                        if (mPrintOrderMap.containsKey("shiftPrint")) mPrintOrderMap["shiftPrint"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true)) {
                        officerModel.observationTime =
                            if (mEtMarkTime != null && mEtMarkTime?.visibility == View.VISIBLE) mEtMarkTime!!.text.toString() else ""
                    } else if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_GREENBURGH_NY, true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, true
                        )
                    ) {
                        officerModel.observationTime =
                            AppUtils.getCurrentDateForObserveTimeFieldForGreenburgh()
                    } else {
                        officerModel.observationTime =
                            sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
                    }
//                    officerModel.observationTime = sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
                    officerModel.isStatus_observationtime = ObservationTimePrint
                    officerModel.mPrintOrderObservationtime =
                        if (mPrintOrderMap.containsKey("ObervationTimePrint")) mPrintOrderMap["ObervationTimePrint"]!!.printOrder else 0.0
                    officerModel.mPrintLayoutOrderObservationTime =
                        if (mPrintOrderMap.containsKey("ObervationTimePrint")) mPrintOrderMap["ObervationTimePrint"]!!.layoutOrder else ""
                    officerModel.observationTimeLabel =
                        if (mPrintOrderMap.containsKey("ObervationTimePrint")) mPrintOrderMap["ObervationTimePrint"]!!.labelName else ""
                    officerModel.mObservationTimeFont =
                        if (mPrintOrderMap.containsKey("ObervationTimePrint")) mPrintOrderMap["ObervationTimePrint"]!!.printFont!!.toInt()!! else 0
                    officerModel.mObservationTimeX =
                        if (mPrintOrderMap.containsKey("ObervationTimePrint")) mPrintOrderMap["ObervationTimePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    officerModel.mObservationTimeY =
                        if (mPrintOrderMap.containsKey("ObervationTimePrint")) mPrintOrderMap["ObervationTimePrint"]!!.printAxisY!!.toDouble() else 0.0
                    officerModel.mObservationColumnSize =
                        if (mPrintOrderMap.containsKey("ObervationTimePrint")) mPrintOrderMap["ObervationTimePrint"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mIssModel.officer = officerModel

                /**
                 * Vehicle Data
                 */
                val vehicleModel = CitationVehicleModel()
                vehicleModel.make = AppUtils.removeSpecialCharacterFromString(mSelectedMake!!)
                vehicleModel.makeFullName = mSelectedMakeValue
                vehicleModel.isStatus_make = makePrint[0]
                vehicleModel.mPrintOrderMake =
                    if (mPrintOrderMap.containsKey("makePrint")) mPrintOrderMap["makePrint"]!!.printOrder else 0.0
                vehicleModel.mPrintLayoutOrderMake =
                    if (mPrintOrderMap.containsKey("makePrint")) mPrintOrderMap["makePrint"]!!.layoutOrder else ""
                vehicleModel.makeFullNameLabel =
                    if (mPrintOrderMap.containsKey("makePrint")) mPrintOrderMap["makePrint"]!!.labelName else ""
                vehicleModel.mMakeFont =
                    if (mPrintOrderMap.containsKey("makePrint")) mPrintOrderMap["makePrint"]!!.printFont!!.toInt()!! else 0
                vehicleModel.mMakeX =
                    if (mPrintOrderMap.containsKey("makePrint")) mPrintOrderMap["makePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                vehicleModel.mMakeY =
                    if (mPrintOrderMap.containsKey("makePrint")) mPrintOrderMap["makePrint"]!!.printAxisY!!.toDouble() else 0.0
                vehicleModel.mMakeColumnSize =
                    if (mPrintOrderMap.containsKey("makePrint")) mPrintOrderMap["makePrint"]!!.printColumnSize!!.toInt() else 1

                vehicleModel.color = AppUtils.removeSpecialCharacterFromString(mSelectedColor!!)
                vehicleModel.colorCodeFullName = mTvOfficerColor?.text.toString()
                vehicleModel.isStatus_color = colorPrint[0]
                vehicleModel.mPrintOrderColor =
                    if (mPrintOrderMap.containsKey("colorPrint")) mPrintOrderMap["colorPrint"]!!.printOrder else 0.0
                vehicleModel.mPrintLayoutOrderColor =
                    if (mPrintOrderMap.containsKey("colorPrint")) mPrintOrderMap["colorPrint"]!!.layoutOrder else ""
                vehicleModel.colorLabel =
                    if (mPrintOrderMap.containsKey("colorPrint")) mPrintOrderMap["colorPrint"]!!.labelName else ""
                vehicleModel.mColorFont =
                    if (mPrintOrderMap.containsKey("colorPrint")) mPrintOrderMap["colorPrint"]!!.printFont!!.toInt()!! else 0
                vehicleModel.mColorX =
                    if (mPrintOrderMap.containsKey("colorPrint")) mPrintOrderMap["colorPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                vehicleModel.mColorY =
                    if (mPrintOrderMap.containsKey("colorPrint")) mPrintOrderMap["colorPrint"]!!.printAxisY!!.toDouble() else 0.0
                vehicleModel.mColorColumnSize =
                    if (mPrintOrderMap.containsKey("colorPrint")) mPrintOrderMap["colorPrint"]!!.printColumnSize!!.toInt() else 1

//                if (setLayoutVisibilityBasedOnSettingResponse()) {
//                vehicleModel.model = AppUtils.removeSpecialCharacterFromString(mSelectedModel!!)
                val finalModel = if (!mSelectedModel.isNullOrBlank() && mSelectedModel.equals(
                        mTvOfficerModel?.text?.toString(), ignoreCase = true
                    )
                ) {
                    mSelectedModel
                } else {
                    mTvOfficerModel?.text?.toString()
                }

                vehicleModel.model = AppUtils.removeSpecialCharacterFromString(finalModel.orEmpty())

                val finalModelLookupCode = if (!model_lookup_code.isNullOrBlank()) {
                    model_lookup_code
                } else {
                    mTvOfficerModel?.text?.toString()
                }

                vehicleModel.model_lookup_code = finalModelLookupCode.orEmpty()
//                vehicleModel.model_lookup_code = model_lookup_code
//                } else {
//                    vehicleModel.model = ""
//                }
                vehicleModel.isStatus_model = modelPrint[0]
                vehicleModel.mPrintOrderModel =
                    if (mPrintOrderMap.containsKey("modelPrint")) mPrintOrderMap["modelPrint"]!!.printOrder else 0.0
                vehicleModel.mPrintLayoutOrderModel =
                    if (mPrintOrderMap.containsKey("modelPrint")) mPrintOrderMap["modelPrint"]!!.layoutOrder else ""
                vehicleModel.modelLabel =
                    if (mPrintOrderMap.containsKey("modelPrint")) mPrintOrderMap["modelPrint"]!!.labelName else ""
                vehicleModel.mModelFont =
                    if (mPrintOrderMap.containsKey("modelPrint")) mPrintOrderMap["modelPrint"]!!.printFont!!.toInt()!! else 0
                vehicleModel.mModelX =
                    if (mPrintOrderMap.containsKey("modelPrint")) mPrintOrderMap["modelPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                vehicleModel.mModelY =
                    if (mPrintOrderMap.containsKey("modelPrint")) mPrintOrderMap["modelPrint"]!!.printAxisY!!.toDouble() else 0.0
                vehicleModel.mModelColumnSize =
                    if (mPrintOrderMap.containsKey("modelPrint")) mPrintOrderMap["modelPrint"]!!.printColumnSize!!.toInt() else 1

                try {
                    mAutoCompleteDate?.let {
                        if (mAutoCompleteExpiryMonth != null) {
                            //TODO JANAK DATE
                            vehicleModel.expiration = mExpiryMonth.toString().trim()
                                .verifyAndConvertMonth() + "/" + mExpiryYear.toString().trim()
                                .verifyAndConvertYear()
                        } else {
                            vehicleModel.expiration =
                                mAutoCompleteDate?.editableText.toString().trim()
                        }
                        vehicleModel.isStatus_expiration = expiryPrint[0]
                        vehicleModel.mPrintOrderExpiration =
                            if (mPrintOrderMap.containsKey("expiryyearPrint")) mPrintOrderMap["expiryyearPrint"]!!.printOrder else 0.0
                        vehicleModel.mPrintLayoutOrderExpiration =
                            if (mPrintOrderMap.containsKey("expiryyearPrint")) mPrintOrderMap["expiryyearPrint"]!!.layoutOrder else ""
                        vehicleModel.expirationLabel =
                            if (mPrintOrderMap.containsKey("expiryyearPrint")) mPrintOrderMap["expiryyearPrint"]!!.labelName else ""
                        vehicleModel.mExpirationFont =
                            if (mPrintOrderMap.containsKey("expiryyearPrint")) mPrintOrderMap["expiryyearPrint"]!!.printFont!!.toInt()!! else 0
                        vehicleModel.mExpirationX =
                            if (mPrintOrderMap.containsKey("expiryyearPrint")) mPrintOrderMap["expiryyearPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        vehicleModel.mExpirationY =
                            if (mPrintOrderMap.containsKey("expiryyearPrint")) mPrintOrderMap["expiryyearPrint"]!!.printAxisY!!.toDouble() else 0.0
                        vehicleModel.mExpirationColumnSize =
                            if (mPrintOrderMap.containsKey("expiryyearPrint")) mPrintOrderMap["expiryyearPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    vehicleModel.expiration = ""
                    vehicleModel.isStatus_expiration = false
                    e.printStackTrace()
                }
                try {
                    val inputTextForVin = mAutoComTextViewVinNum?.text.toString()
                    if (mSendStateEmptyForFullVinNumberAndCopyLast8DigitVinForLpr.equals("YES")) {
                        if (mAutoComTextViewVinNum != null && (!mApplicationVinList.any { it.direction == inputTextForVin }) && mAutoComTextViewVinNum?.text.toString().length >= 17) {
                            vehicleModel.state = ""
                        } else {
                            vehicleModel.state = mState2DigitCode
                        }
                    } else {
                        vehicleModel.state = mState2DigitCode
                    }

                    vehicleModel.isStatus_state = statePrint[0]
                    vehicleModel.mPrintOrderState =
                        if (mPrintOrderMap.containsKey("statePrint")) mPrintOrderMap["statePrint"]!!.printOrder else 0.0
                    vehicleModel.mPrintLayoutOrderState =
                        if (mPrintOrderMap.containsKey("statePrint")) mPrintOrderMap["statePrint"]!!.layoutOrder else ""
                    vehicleModel.stateLabel =
                        if (mPrintOrderMap.containsKey("statePrint")) mPrintOrderMap["statePrint"]!!.labelName else ""
                    vehicleModel.mStateFont =
                        if (mPrintOrderMap.containsKey("statePrint")) mPrintOrderMap["statePrint"]!!.printFont!!.toInt()!! else 0
                    vehicleModel.mStateX =
                        if (mPrintOrderMap.containsKey("statePrint")) mPrintOrderMap["statePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    vehicleModel.mStateY =
                        if (mPrintOrderMap.containsKey("statePrint")) mPrintOrderMap["statePrint"]!!.printAxisY!!.toDouble() else 0.0
                    vehicleModel.mStateColumnSize =
                        if (mPrintOrderMap.containsKey("statePrint")) mPrintOrderMap["statePrint"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    vehicleModel.state = ""
                    vehicleModel.isStatus_state = false
                    e.printStackTrace()
                }
                try {
                    mBodyStyleCodeItem?.let {
                        vehicleModel.bodyStyle =
                            AppUtils.removeSpecialCharacterFromString(mBodyStyleCodeItem!!)
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
                            ) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
                            )
                        ) {
                            vehicleModel.bodyStyle = mBodyStyleCodeItem
                        }
                        vehicleModel.body_style_lookup_code = body_style_lookup_code
                        vehicleModel.bodyStyleDescription =
                            mAutoComTextViewBodyStyle?.editableText.toString().trim()
                        vehicleModel.isStatus_body_style = body_stylePrint[0]
                        vehicleModel.mPrintOrderBodyStyle =
                            if (mPrintOrderMap.containsKey("bodystylePrint")) mPrintOrderMap["bodystylePrint"]!!.printOrder else 0.0
                        vehicleModel.mPrintLayoutOrderBodyStyle =
                            if (mPrintOrderMap.containsKey("bodystylePrint")) mPrintOrderMap["bodystylePrint"]!!.layoutOrder else ""
                        vehicleModel.bodyStyleLabel =
                            if (mPrintOrderMap.containsKey("bodystylePrint")) mPrintOrderMap["bodystylePrint"]!!.labelName else ""
                        vehicleModel.mBodyFont =
                            if (mPrintOrderMap.containsKey("bodystylePrint")) mPrintOrderMap["bodystylePrint"]!!.printFont!!.toInt()!! else 0
                        vehicleModel.mBodyStyleX =
                            if (mPrintOrderMap.containsKey("bodystylePrint")) mPrintOrderMap["bodystylePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        vehicleModel.mBodyStyleY =
                            if (mPrintOrderMap.containsKey("bodystylePrint")) mPrintOrderMap["bodystylePrint"]!!.printAxisY!!.toDouble() else 0.0
                        vehicleModel.mBodyColumnSize =
                            if (mPrintOrderMap.containsKey("bodystylePrint")) mPrintOrderMap["bodystylePrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    vehicleModel.bodyStyle = ""
                    vehicleModel.isStatus_body_style = false
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewDecalYear?.let {
                        vehicleModel.decalYear =
                            mAutoComTextViewDecalYear?.editableText.toString().trim()
                        vehicleModel.isStatus_decal_year = decal_yearPrint[0]
                        vehicleModel.mPrintOrderDecalYear =
                            if (mPrintOrderMap.containsKey("decalyearPrint")) mPrintOrderMap["decalyearPrint"]!!.printOrder else 0.0
                        vehicleModel.mPrintLayoutOrderDecalYear =
                            if (mPrintOrderMap.containsKey("decalyearPrint")) mPrintOrderMap["decalyearPrint"]!!.layoutOrder else ""
                        vehicleModel.decalYearLabel =
                            if (mPrintOrderMap.containsKey("decalyearPrint")) mPrintOrderMap["decalyearPrint"]!!.labelName else ""
                        vehicleModel.mDecalYearFont =
                            if (mPrintOrderMap.containsKey("decalyearPrint")) mPrintOrderMap["decalyearPrint"]!!.printFont!!.toInt()!! else 0
                        vehicleModel.mDecalYearX =
                            if (mPrintOrderMap.containsKey("decalyearPrint")) mPrintOrderMap["decalyearPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        vehicleModel.mDecalYearY =
                            if (mPrintOrderMap.containsKey("decalyearPrint")) mPrintOrderMap["decalyearPrint"]!!.printAxisY!!.toDouble() else 0.0
                        vehicleModel.mDecalYearColumnSize =
                            if (mPrintOrderMap.containsKey("decalyearPrint")) mPrintOrderMap["decalyearPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    vehicleModel.decalYear = ""
                    vehicleModel.isStatus_decal_year = false
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewDecalNum?.let {
                        vehicleModel.decalNumber =
                            mAutoComTextViewDecalNum?.editableText.toString().trim()
                        vehicleModel.isStatus_decal_number = decal_numberPrint[0]
                        vehicleModel.mPrintOrderDecalNumber =
                            if (mPrintOrderMap.containsKey("decalnumberPrint")) mPrintOrderMap["decalnumberPrint"]!!.printOrder else 0.0
                        vehicleModel.mPrintLayoutOrderDecalNumber =
                            if (mPrintOrderMap.containsKey("decalnumberPrint")) mPrintOrderMap["decalnumberPrint"]!!.layoutOrder else ""
                        vehicleModel.decalNumberLabel =
                            if (mPrintOrderMap.containsKey("decalnumberPrint")) mPrintOrderMap["decalnumberPrint"]!!.labelName else ""
                        vehicleModel.mDecalNumberFont =
                            if (mPrintOrderMap.containsKey("decalnumberPrint")) mPrintOrderMap["decalnumberPrint"]!!.printFont!!.toInt()!! else 0
                        vehicleModel.mDecalNumberX =
                            if (mPrintOrderMap.containsKey("decalnumberPrint")) mPrintOrderMap["decalnumberPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        vehicleModel.mDecalNumberY =
                            if (mPrintOrderMap.containsKey("decalnumberPrint")) mPrintOrderMap["decalnumberPrint"]!!.printAxisY!!.toDouble() else 0.0
                        vehicleModel.mDecalNumberColumnSize =
                            if (mPrintOrderMap.containsKey("decalnumberPrint")) mPrintOrderMap["decalnumberPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    vehicleModel.decalNumber = ""
                    vehicleModel.isStatus_decal_number = false
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewVinNum?.let {
                        vehicleModel.vinNumber =
                            mAutoComTextViewVinNum?.editableText.toString().trim()
                        vehicleModel.isStatus_vin_number = vin_numberPrint[0]
                        vehicleModel.mPrintOrderVinNumber =
                            if (mPrintOrderMap.containsKey("vinnumberPrint")) mPrintOrderMap["vinnumberPrint"]!!.printOrder else 0.0
                        vehicleModel.mPrintLayoutOrderVinNumber =
                            if (mPrintOrderMap.containsKey("vinnumberPrint")) mPrintOrderMap["vinnumberPrint"]!!.layoutOrder else ""
                        vehicleModel.vinNumberLabel =
                            if (mPrintOrderMap.containsKey("vinnumberPrint")) mPrintOrderMap["vinnumberPrint"]!!.labelName else ""
                        vehicleModel.mVinNumberFont =
                            if (mPrintOrderMap.containsKey("vinnumberPrint")) mPrintOrderMap["vinnumberPrint"]!!.printFont!!.toInt()!! else 0
                        vehicleModel.mVinNumberX =
                            if (mPrintOrderMap.containsKey("vinnumberPrint")) mPrintOrderMap["vinnumberPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        vehicleModel.mVinNumberY =
                            if (mPrintOrderMap.containsKey("vinnumberPrint")) mPrintOrderMap["vinnumberPrint"]!!.printAxisY!!.toDouble() else 0.0
                        vehicleModel.mVinNumberColumnSize =
                            if (mPrintOrderMap.containsKey("vinnumberPrint")) mPrintOrderMap["vinnumberPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    vehicleModel.vinNumber = "."
                    vehicleModel.isStatus_vin_number = false
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewLicensePlate?.let {
                        vehicleModel.licensePlate = AppUtils.removeSpecialCharacterFromString(
                            mAutoComTextViewLicensePlate?.editableText.toString().trim()
                        )
                        vehicleModel.isStatus_license_plate = lp_numberPrint[0]
                        vehicleModel.mPrintOrderLicensePlate =
                            if (mPrintOrderMap.containsKey("lpnumnerPrint")) mPrintOrderMap["lpnumnerPrint"]!!.printOrder else 0.0
                        vehicleModel.mPrintLayoutOrderLicensePlate =
                            if (mPrintOrderMap.containsKey("lpnumnerPrint")) mPrintOrderMap["lpnumnerPrint"]!!.layoutOrder else ""
                        vehicleModel.licensePlateLabel =
                            if (mPrintOrderMap.containsKey("lpnumnerPrint")) mPrintOrderMap["lpnumnerPrint"]!!.labelName else ""
                        vehicleModel.mLicenseFont =
                            if (mPrintOrderMap.containsKey("lpnumnerPrint")) mPrintOrderMap["lpnumnerPrint"]!!.printFont!!.toInt()!! else 0
                        vehicleModel.mLicensePlateX =
                            if (mPrintOrderMap.containsKey("lpnumnerPrint")) mPrintOrderMap["lpnumnerPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        vehicleModel.mLicensePlateY =
                            if (mPrintOrderMap.containsKey("lpnumnerPrint")) mPrintOrderMap["lpnumnerPrint"]!!.printAxisY!!.toDouble() else 0.0
                        vehicleModel.mLicenseColumnSize =
                            if (mPrintOrderMap.containsKey("lpnumnerPrint")) mPrintOrderMap["lpnumnerPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    vehicleModel.licensePlate = ""
                    vehicleModel.isStatus_license_plate = false
                    e.printStackTrace()
                }
                mIssModel.vehicle = vehicleModel
                val locationModel = CitationLocationModel()
                try {
                    mAutoComTextViewBlock?.let {
                        locationModel.block = mAutoComTextViewBlock?.text.toString().trim()
                        locationModel.isStatus_block = blockPrint[0]
                        locationModel.mPrintOrderblock =
                            if (mPrintOrderMap.containsKey("blockPrint")) mPrintOrderMap["blockPrint"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderblock =
                            if (mPrintOrderMap.containsKey("blockPrint")) mPrintOrderMap["blockPrint"]!!.layoutOrder else ""
                        locationModel.blockLabel =
                            if (mPrintOrderMap.containsKey("blockPrint")) mPrintOrderMap["blockPrint"]!!.labelName else ""
                        locationModel.mBlockFont =
                            if (mPrintOrderMap.containsKey("blockPrint")) mPrintOrderMap["blockPrint"]!!.printFont!!.toInt()!! else 0
                        locationModel.mBlockX =
                            if (mPrintOrderMap.containsKey("blockPrint")) mPrintOrderMap["blockPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mBlockY =
                            if (mPrintOrderMap.containsKey("blockPrint")) mPrintOrderMap["blockPrint"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mBlockColumnSize =
                            if (mPrintOrderMap.containsKey("blockPrint")) mPrintOrderMap["blockPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    locationModel.block = ""
                    locationModel.isStatus_block = false
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewStreet?.let {
                        locationModel.street =
                            mAutoComTextViewStreet?.editableText.toString().trim()
                        locationModel.mStreetLookupCode = mStreetLookupCode
                        locationModel.isStatus_street = streetPrint[0]
                        locationModel.mPrintOrderStreet =
                            if (mPrintOrderMap.containsKey("streetPrint")) mPrintOrderMap["streetPrint"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderStreet =
                            if (mPrintOrderMap.containsKey("streetPrint")) mPrintOrderMap["streetPrint"]!!.layoutOrder else ""
                        locationModel.streetLabel =
                            if (mPrintOrderMap.containsKey("streetPrint")) mPrintOrderMap["streetPrint"]!!.labelName else ""
                        locationModel.mStreetFont =
                            if (mPrintOrderMap.containsKey("streetPrint")) mPrintOrderMap["streetPrint"]!!.printFont!!.toInt()!! else 0
                        locationModel.mStreetX =
                            if (mPrintOrderMap.containsKey("streetPrint")) mPrintOrderMap["streetPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mStreetY =
                            if (mPrintOrderMap.containsKey("streetPrint")) mPrintOrderMap["streetPrint"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mStreetColumnSize =
                            if (mPrintOrderMap.containsKey("streetPrint")) mPrintOrderMap["streetPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    locationModel.street = ""
                    locationModel.isStatus_street = false
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewDirection?.let {
                        locationModel.direction =
                            mAutoComTextViewDirection?.editableText.toString().trim()
                        locationModel.isStatus_direction = directionPrint[0]
                        locationModel.mPrintOrderDirection =
                            if (mPrintOrderMap.containsKey("directionPrint")) mPrintOrderMap["directionPrint"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderDirection =
                            if (mPrintOrderMap.containsKey("directionPrint")) mPrintOrderMap["directionPrint"]!!.layoutOrder else ""
                        locationModel.directionLabel =
                            if (mPrintOrderMap.containsKey("directionPrint")) mPrintOrderMap["directionPrint"]!!.labelName else ""
                        locationModel.mDirectionFont =
                            if (mPrintOrderMap.containsKey("directionPrint")) mPrintOrderMap["directionPrint"]!!.printFont!!.toInt()!! else 0
                        locationModel.mDirectionX =
                            if (mPrintOrderMap.containsKey("directionPrint")) mPrintOrderMap["directionPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mDirectionY =
                            if (mPrintOrderMap.containsKey("directionPrint")) mPrintOrderMap["directionPrint"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mDirectionColumnSize =
                            if (mPrintOrderMap.containsKey("directionPrint")) mPrintOrderMap["directionPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    locationModel.direction = ""
                    locationModel.isStatus_direction = false
                }
                try {
//                    locationModel.setSide(mAutoComTextViewSide.getEditableText().toString().trim());
                    mSideOfStreet2DigitCode?.let {
                        locationModel.side = mSideOfStreet2DigitCode
                        locationModel.isStatus_side = sidePrint[0]
                        locationModel.mPrintOrderSide =
                            if (mPrintOrderMap.containsKey("sidePrint")) mPrintOrderMap["sidePrint"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderSide =
                            if (mPrintOrderMap.containsKey("sidePrint")) mPrintOrderMap["sidePrint"]!!.layoutOrder else ""
                        locationModel.sideLabel =
                            if (mPrintOrderMap.containsKey("sidePrint")) mPrintOrderMap["sidePrint"]!!.labelName else ""
                        locationModel.mSideFont =
                            if (mPrintOrderMap.containsKey("sidePrint")) mPrintOrderMap["sidePrint"]!!.printFont!!.toInt()!! else 0
                        locationModel.mStreetX =
                            if (mPrintOrderMap.containsKey("sidePrint")) mPrintOrderMap["sidePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mStreetY =
                            if (mPrintOrderMap.containsKey("sidePrint")) mPrintOrderMap["sidePrint"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mStreetColumnSize =
                            if (mPrintOrderMap.containsKey("sidePrint")) mPrintOrderMap["sidePrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    locationModel.side = ""
                    locationModel.isStatus_side = false
                }
                try {
                    mAutoComTextViewMeterName?.let {
                        locationModel.meterName =
                            mAutoComTextViewMeterName?.editableText.toString().trim()
                        locationModel.isStatus_meter_name = meterPrint[0]
                        locationModel.mPrintOrderMeterName =
                            if (mPrintOrderMap.containsKey("meterPrint")) mPrintOrderMap["meterPrint"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderMeterName =
                            if (mPrintOrderMap.containsKey("meterPrint")) mPrintOrderMap["meterPrint"]!!.layoutOrder else ""
                        locationModel.meterNameLabel =
                            if (mPrintOrderMap.containsKey("meterPrint")) mPrintOrderMap["meterPrint"]!!.labelName else ""
                        locationModel.mMeterFont =
                            if (mPrintOrderMap.containsKey("meterPrint")) mPrintOrderMap["meterPrint"]!!.printFont!!.toInt()!! else 0
                        locationModel.mMeterX =
                            if (mPrintOrderMap.containsKey("meterPrint")) mPrintOrderMap["meterPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mMeterY =
                            if (mPrintOrderMap.containsKey("meterPrint")) mPrintOrderMap["meterPrint"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mMeterColumnSize =
                            if (mPrintOrderMap.containsKey("meterPrint")) mPrintOrderMap["meterPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    locationModel.meterName = ""
                    locationModel.isStatus_meter_name = false
                }
                try {
                    locationModel.spaceName =
                        if (mAutoConTextViewSpace != null && mAutoConTextViewSpace?.text.toString()
                                .isNotEmpty()
                        ) AppUtils.removeSpecialCharacterFromString(
                            mAutoConTextViewSpace?.editableText.toString().trim()
                        )
                        else ""

                    locationModel.isStatus_space_name = spacePrint[0]
                    locationModel.mPrintOrderSpaceName =
                        if (mPrintOrderMap.containsKey("spacePrint")) mPrintOrderMap["spacePrint"]!!.printOrder else 0.0
                    locationModel.mPrintLayoutOrderSpaceName =
                        if (mPrintOrderMap.containsKey("spacePrint")) mPrintOrderMap["spacePrint"]!!.layoutOrder else ""
                    locationModel.spaceNameLabel =
                        if (mPrintOrderMap.containsKey("spacePrint")) mPrintOrderMap["spacePrint"]!!.labelName else ""
                    locationModel.mSpaceFont =
                        if (mPrintOrderMap.containsKey("spacePrint")) mPrintOrderMap["spacePrint"]!!.printFont!!.toInt()!! else 0
                    locationModel.mSpaceX =
                        if (mPrintOrderMap.containsKey("spacePrint")) mPrintOrderMap["spacePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    locationModel.mSpaceY =
                        if (mPrintOrderMap.containsKey("spacePrint")) mPrintOrderMap["spacePrint"]!!.printAxisY!!.toDouble() else 0.0
                    locationModel.mSpaceColumnSize =
                        if (mPrintOrderMap.containsKey("spacePrint")) mPrintOrderMap["spacePrint"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
                    locationModel.spaceName = ""
                    locationModel.isStatus_space_name = false
                }
                try {
//                    mAutoComTextViewZone?.let {
                    mAutoComTextViewPBCZone?.let {
                        locationModel.pcbZone = AppUtils.removeSpecialCharacterFromString(
                            mAutoComTextViewPBCZone?.editableText.toString().trim()
                        )
                        locationModel.isStatus_PcbZone = cityZonePrint[0]
                        locationModel.mPrintOrderPcbZone =
                            if (mPrintOrderMap.containsKey("city_zone")) mPrintOrderMap["city_zone"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderPcbZone =
                            if (mPrintOrderMap.containsKey("city_zone")) mPrintOrderMap["city_zone"]!!.layoutOrder else ""
                        locationModel.pcbZoneLabel =
                            if (mPrintOrderMap.containsKey("city_zone")) mPrintOrderMap["city_zone"]!!.labelName else ""
                        locationModel.mPcbZoneFont =
                            if (mPrintOrderMap.containsKey("city_zone")) mPrintOrderMap["city_zone"]!!.printFont!!.toInt()!! else 0
                        locationModel.mPcbZoneX =
                            if (mPrintOrderMap.containsKey("city_zone")) mPrintOrderMap["city_zone"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mPcbZoneY =
                            if (mPrintOrderMap.containsKey("city_zone")) mPrintOrderMap["city_zone"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mPcbZoneColumnSize =
                            if (mPrintOrderMap.containsKey("city_zone")) mPrintOrderMap["city_zone"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    locationModel.cityZone = ""
                    locationModel.isStatus_CityZone = false
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewLot?.let {
                        locationModel.lot = mAutoComTextViewLot?.editableText.toString().trim()
                        locationModel.lotBranchId = mLotBranchId
                        locationModel.lotLookupCode = mLotlookupCode
                        locationModel.isStatus_lot = lotPrint[0]
                        locationModel.mPrintOrderLot =
                            if (mPrintOrderMap.containsKey("lotPrint")) mPrintOrderMap["lotPrint"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderLot =
                            if (mPrintOrderMap.containsKey("lotPrint")) mPrintOrderMap["lotPrint"]!!.layoutOrder else ""
                        locationModel.lotLabel =
                            if (mPrintOrderMap.containsKey("lotPrint")) mPrintOrderMap["lotPrint"]!!.labelName else ""
                        locationModel.mLotFont =
                            if (mPrintOrderMap.containsKey("lotPrint")) mPrintOrderMap["lotPrint"]!!.printFont!!.toInt()!! else 0
                        locationModel.mLotX =
                            if (mPrintOrderMap.containsKey("lotPrint")) mPrintOrderMap["lotPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mLotY =
                            if (mPrintOrderMap.containsKey("lotPrint")) mPrintOrderMap["lotPrint"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mLotColumnSize =
                            if (mPrintOrderMap.containsKey("lotPrint")) mPrintOrderMap["lotPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    locationModel.lot = ""
                    locationModel.isStatus_lot = false
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewLocation?.let {
                        locationModel.location =
                            mAutoComTextViewLocation?.editableText.toString().trim()
                        locationModel.isStatus_location = locationPrint[0]
                        locationModel.mPrintOrderLocation =
                            if (mPrintOrderMap.containsKey("locationPrint")) mPrintOrderMap["locationPrint"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderLocation =
                            if (mPrintOrderMap.containsKey("locationPrint")) mPrintOrderMap["locationPrint"]!!.layoutOrder else ""
                        locationModel.locationLabel =
                            if (mPrintOrderMap.containsKey("locationPrint")) mPrintOrderMap["locationPrint"]!!.labelName else ""
                        locationModel.mLocationFont =
                            if (mPrintOrderMap.containsKey("locationPrint")) mPrintOrderMap["locationPrint"]!!.printFont!!.toInt()!! else 0
                        locationModel.mLocationX =
                            if (mPrintOrderMap.containsKey("locationPrint")) mPrintOrderMap["locationPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mLocationY =
                            if (mPrintOrderMap.containsKey("locationPrint")) mPrintOrderMap["locationPrint"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mLocationColumnSize =
                            if (mPrintOrderMap.containsKey("locationPrint")) mPrintOrderMap["locationPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    locationModel.location = ""
                    locationModel.isStatus_location = false
                    e.printStackTrace()
                }
                try {
//                    mAutoComTextViewPBCZone?.let {
                    mAutoComTextViewZone?.let {
                        locationModel.cityZone = mAutoComTextViewZone?.text.toString()
                        locationModel.isStatus_CityZone = zonePrint
                        locationModel.mPrintOrderCityZone =
                            if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printOrder else 0.0
                        locationModel.mPrintLayoutOrderCityZone =
                            if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.layoutOrder else ""
                        locationModel.cityZoneLabel =
                            if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.labelName else ""
                        locationModel.mCityZoneFont =
                            if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printFont!!.toInt()!! else 0
                        locationModel.mCityZoneX =
                            if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        locationModel.mCityZoneY =
                            if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printAxisY!!.toDouble() else 0.0
                        locationModel.mCityZoneColumnSize =
                            if (mPrintOrderMap.containsKey("zonePrint")) mPrintOrderMap["zonePrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mIssModel.location = locationModel
                val voilationModel = CitationVoilationModel()
                voilationModel.violationCode =
                    if (mViolationListSelectedItem?.violation != null && mViolationListSelectedItem?.violation!!.isNotEmpty()) mViolationListSelectedItem?.violation.nullSafety() else mAutoComTextViewAbbrCode!!.text.toString()
                voilationModel.code =
                    if (mViolationListSelectedItem?.violationCode != null && mViolationListSelectedItem?.violationCode!!.isNotEmpty()) mViolationListSelectedItem?.violationCode.nullSafety() else mAutoComTextViewCode!!.text.toString()
                voilationModel.timeLimitVio = if (mVendorName!!.equals(
                        "Vigilant", ignoreCase = true
                    )
                ) "0" else if (mViolationListSelectedItem?.timelimitVio != null && mViolationListSelectedItem?.timelimitVio!!.isNotEmpty()) mViolationListSelectedItem?.timelimitVio.nullSafety() else "0"
                voilationModel.isStatus_code = violationPrint[0]
                voilationModel.mPrintOrderCode =
                    if (mPrintOrderMap.containsKey("codePrint")) mPrintOrderMap["codePrint"]!!.printOrder else 0.0
                voilationModel.mPrintLayoutOrderCode =
                    if (mPrintOrderMap.containsKey("codePrint")) mPrintOrderMap["codePrint"]!!.layoutOrder else ""
                voilationModel.codeLabel =
                    if (mPrintOrderMap.containsKey("codePrint")) mPrintOrderMap["codePrint"]!!.labelName else ""
                voilationModel.mViolationFont =
                    if (mPrintOrderMap.containsKey("codePrint")) mPrintOrderMap["codePrint"]!!.printFont!!.toInt()!! else 0
                voilationModel.mViolationX =
                    if (mPrintOrderMap.containsKey("codePrint")) mPrintOrderMap["codePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                voilationModel.mViolationY =
                    if (mPrintOrderMap.containsKey("codePrint")) mPrintOrderMap["codePrint"]!!.printAxisY!!.toDouble() else 0.0
                voilationModel.mViolationColumnSize =
                    if (mPrintOrderMap.containsKey("codePrint")) mPrintOrderMap["codePrint"]!!.printColumnSize!!.toInt() else 1



                try {
                    mAutoComTextViewAmount?.let {
                        voilationModel.amount = splitDoller(
                            mAutoComTextViewAmount?.text.toString().trim()
                        ).toString()
                        voilationModel.isStatus_amount = finePrint[0]
                        voilationModel.mUnpaidCitationCount = unpaidCitationCount
                        voilationModel.amountDay = amountDays
                        voilationModel.mPrintOrderAmount =
                            if (mPrintOrderMap.containsKey("finePrint")) mPrintOrderMap["finePrint"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderAmount =
                            if (mPrintOrderMap.containsKey("finePrint")) mPrintOrderMap["finePrint"]!!.layoutOrder else ""
                        voilationModel.amountLabel =
                            if (mPrintOrderMap.containsKey("finePrint")) mPrintOrderMap["finePrint"]!!.labelName else ""
                        voilationModel.mAmountFont =
                            if (mPrintOrderMap.containsKey("finePrint")) mPrintOrderMap["finePrint"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mAmountX =
                            if (mPrintOrderMap.containsKey("finePrint")) mPrintOrderMap["finePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mAmountY =
                            if (mPrintOrderMap.containsKey("finePrint")) mPrintOrderMap["finePrint"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mAmountColumnSize =
                            if (mPrintOrderMap.containsKey("finePrint")) mPrintOrderMap["finePrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    AutoComTextViewDueDate?.let {
                        voilationModel.dueDate = splitDoller(
                            AutoComTextViewDueDate?.editableText.toString().trim()
                        ).toString()
                        voilationModel.isStatus_due_date = due_15_daysPrint[0]
                        voilationModel.dueDateDay = lateFine15Days
                        voilationModel.mPrintOrderDueDate =
                            if (mPrintOrderMap.containsKey("due15dayPrint")) mPrintOrderMap["due15dayPrint"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderDueDate =
                            if (mPrintOrderMap.containsKey("due15dayPrint")) mPrintOrderMap["due15dayPrint"]!!.layoutOrder else ""
                        voilationModel.dueDateLabel =
                            if (mPrintOrderMap.containsKey("due15dayPrint")) mPrintOrderMap["due15dayPrint"]!!.labelName else ""
                        voilationModel.mDueDateFont =
                            if (mPrintOrderMap.containsKey("due15dayPrint")) mPrintOrderMap["due15dayPrint"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mDueDateX =
                            if (mPrintOrderMap.containsKey("due15dayPrint")) mPrintOrderMap["due15dayPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mDueDateY =
                            if (mPrintOrderMap.containsKey("due15dayPrint")) mPrintOrderMap["due15dayPrint"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mDueDateColumnSize =
                            if (mPrintOrderMap.containsKey("due15dayPrint")) mPrintOrderMap["due15dayPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                }
                try {
                    AutoComTextViewDueDate30?.let {
                        voilationModel.dueDate30 = splitDoller(
                            AutoComTextViewDueDate30?.editableText.toString().trim()
                        ).toString()
                        // Carta Off street only add extera lot amount in fine
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                            ) && mAutoComTextViewZone != null && mAutoComTextViewZone?.text.toString()
                                .equals("OFF STREET", ignoreCase = true)
                        ) {
                            voilationModel.isStatus_due_date_30 = false
                        } else {
                            voilationModel.isStatus_due_date_30 = due_30_daysPrint[0]
                        }
                        voilationModel.dueDate30Days = lateFine30Days
                        voilationModel.mPrintOrderDueDate30 =
                            if (mPrintOrderMap.containsKey("due30dayPrint")) mPrintOrderMap["due30dayPrint"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderDueDate30 =
                            if (mPrintOrderMap.containsKey("due30dayPrint")) mPrintOrderMap["due30dayPrint"]!!.layoutOrder else ""
                        voilationModel.dueDate30Label =
                            if (mPrintOrderMap.containsKey("due30dayPrint")) mPrintOrderMap["due30dayPrint"]!!.labelName else ""
                        voilationModel.mDueDate30Font =
                            if (mPrintOrderMap.containsKey("due30dayPrint")) mPrintOrderMap["due30dayPrint"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mDueDate30X =
                            if (mPrintOrderMap.containsKey("due30dayPrint")) mPrintOrderMap["due30dayPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mDueDate30Y =
                            if (mPrintOrderMap.containsKey("due30dayPrint")) mPrintOrderMap["due30dayPrint"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mDueDate30ColumnSize =
                            if (mPrintOrderMap.containsKey("due30dayPrint")) mPrintOrderMap["due30dayPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    AutoComTextViewDueDate45?.let {
                        voilationModel.dueDate45 = splitDoller(
                            AutoComTextViewDueDate45?.editableText.toString().trim()
                        ).toString()
                        voilationModel.isStatus_due_date_45 = due_45_daysPrint[0]
                        voilationModel.dueDate45Days = lateFine45Days
                        voilationModel.mPrintOrderDueDate45 =
                            if (mPrintOrderMap.containsKey("due45dayPrint")) mPrintOrderMap["due45dayPrint"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderDueDate45 =
                            if (mPrintOrderMap.containsKey("due45dayPrint")) mPrintOrderMap["due45dayPrint"]!!.layoutOrder else ""
                        voilationModel.dueDate45Label =
                            if (mPrintOrderMap.containsKey("due45dayPrint")) mPrintOrderMap["due45dayPrint"]!!.labelName else ""
                        voilationModel.mDueDate45Font =
                            if (mPrintOrderMap.containsKey("due45dayPrint")) mPrintOrderMap["due45dayPrint"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mDueDate45X =
                            if (mPrintOrderMap.containsKey("due45dayPrint")) mPrintOrderMap["due45dayPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mDueDate45Y =
                            if (mPrintOrderMap.containsKey("due45dayPrint")) mPrintOrderMap["due45dayPrint"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mDueDate45ColumnSize =
                            if (mPrintOrderMap.containsKey("due45dayPrint")) mPrintOrderMap["due45dayPrint"]!!.printColumnSize!!.toInt() else 1


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
//                    voilationModel.setDueDate45(String.valueOf(mViolationList.getDue_45_days()));
//                    voilationModel.setStatus_due_date_45(false);
                }

                try {
                    mAutoComTextViewVioType?.let {
                        voilationModel.vioType =
                            mAutoComTextViewVioType?.editableText.toString().trim()
                        voilationModel.isStatus_VioType = vio_typePrint[0]
                        voilationModel.mPrintOrderVioType =
                            if (mPrintOrderMap.containsKey("vio_type")) mPrintOrderMap["vio_type"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderVioType =
                            if (mPrintOrderMap.containsKey("vio_type")) mPrintOrderMap["vio_type"]!!.layoutOrder else ""
                        voilationModel.vioTypeLabel =
                            if (mPrintOrderMap.containsKey("vio_type")) mPrintOrderMap["vio_type"]!!.labelName else ""
                        voilationModel.mVioTypeFont =
                            if (mPrintOrderMap.containsKey("vio_type")) mPrintOrderMap["vio_type"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mVioTypeX =
                            if (mPrintOrderMap.containsKey("vio_type")) mPrintOrderMap["vio_type"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mVioTypeY =
                            if (mPrintOrderMap.containsKey("vio_type")) mPrintOrderMap["vio_type"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mVioTypeColumnSize =
                            if (mPrintOrderMap.containsKey("vio_type")) mPrintOrderMap["v"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    mAutoComTextViewVioTypeCode?.let {
                        voilationModel.vioTypeCode =
                            mAutoComTextViewVioTypeCode?.editableText.toString().trim()
                        voilationModel.isStatus_VioTypeCode = vio_type_codePrint[0]
                        voilationModel.mPrintOrderVioTypeCode =
                            if (mPrintOrderMap.containsKey("vio_type_code")) mPrintOrderMap["vio_type_code"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderVioTypeCode =
                            if (mPrintOrderMap.containsKey("vio_type_code")) mPrintOrderMap["vio_type_code"]!!.layoutOrder else ""
                        voilationModel.vioTypeCodeLabel =
                            if (mPrintOrderMap.containsKey("vio_type_code")) mPrintOrderMap["vio_type_code"]!!.labelName else ""
                        voilationModel.mVioTypeCodeFont =
                            if (mPrintOrderMap.containsKey("vio_type_code")) mPrintOrderMap["vio_type_code"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mVioTypeCodeX =
                            if (mPrintOrderMap.containsKey("vio_type_code")) mPrintOrderMap["vio_type_code"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mVioTypeCodeY =
                            if (mPrintOrderMap.containsKey("vio_type_code")) mPrintOrderMap["vio_type_code"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mVioTypeCodeColumnSize =
                            if (mPrintOrderMap.containsKey("vio_type_code")) mPrintOrderMap["vio_type_code"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewVioTypeDescription?.let {
                        voilationModel.vioTypeDescription =
                            mAutoComTextViewVioTypeDescription?.editableText.toString().trim()
                        voilationModel.isStatus_VioTypeDescription = vio_type_descriptionPrint[0]
                        voilationModel.mPrintOrderVioTypeDescription =
                            if (mPrintOrderMap.containsKey("vio_type_description")) mPrintOrderMap["vio_type_description"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderVioTypeDescription =
                            if (mPrintOrderMap.containsKey("vio_type_description")) mPrintOrderMap["vio_type_description"]!!.layoutOrder else ""
                        voilationModel.vioTypeDescriptionLabel =
                            if (mPrintOrderMap.containsKey("vio_type_description")) mPrintOrderMap["vio_type_description"]!!.labelName else ""
                        voilationModel.mVioTypeDescriptionFont =
                            if (mPrintOrderMap.containsKey("vio_type_description")) mPrintOrderMap["vio_type_description"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mVioTypeDescriptionX =
                            if (mPrintOrderMap.containsKey("vio_type_description")) mPrintOrderMap["vio_type_description"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mVioTypeDescriptionY =
                            if (mPrintOrderMap.containsKey("vio_type_description")) mPrintOrderMap["vio_type_description"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mVioTypeDescriptionColumnSize =
                            if (mPrintOrderMap.containsKey("vio_type_description")) mPrintOrderMap["vio_type_description"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    voilationModel.export_code = exportCode.nullSafety().trim()
                    voilationModel.mSanctionsType = sanctionsType.nullSafety(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    voilationModel.dueDateCost = dueCost.trim()
//                        voilationModel.dueDateCost = ##
                    voilationModel.isStatus_due_date_cost = costPrint[0]
                    voilationModel.mPrintOrderDueDateCost =
                        if (mPrintOrderMap.containsKey("costPrint")) mPrintOrderMap["costPrint"]!!.printOrder else 0.0
                    voilationModel.mPrintLayoutOrderDueDateCost =
                        if (mPrintOrderMap.containsKey("costPrint")) mPrintOrderMap["costPrint"]!!.layoutOrder else ""
                    voilationModel.dueDateCostLabel =
                        if (mPrintOrderMap.containsKey("costPrint")) mPrintOrderMap["costPrint"]!!.labelName else ""
                    voilationModel.mDueDateCostFont =
                        if (mPrintOrderMap.containsKey("costPrint")) mPrintOrderMap["costPrint"]!!.printFont!!.toInt()!! else 0
                    voilationModel.mDueDateCostX =
                        if (mPrintOrderMap.containsKey("costPrint")) mPrintOrderMap["costPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    voilationModel.mDueDateCostY =
                        if (mPrintOrderMap.containsKey("costPrint")) mPrintOrderMap["costPrint"]!!.printAxisY!!.toDouble() else 0.0
                    voilationModel.mDueDateCostColumnSize =
                        if (mPrintOrderMap.containsKey("costPrint")) mPrintOrderMap["costPrint"]!!.printColumnSize!!.toInt() else 1

                } catch (e: Exception) {
                    e.printStackTrace()
//                    voilationModel.setDueDate45(String.valueOf(mViolationList.getDue_45_days()));
//                    voilationModel.setStatus_due_date_45(false);
                }
                try {
                    voilationModel.dueDateParkingFee = dueParkingFee.trim()
//                        voilationModel.dueDateCost = ##
                    voilationModel.isStatus_due_date_parking_fee = parkingFeePrint[0]
                    voilationModel.mPrintOrderDueDateParkingFee =
                        if (mPrintOrderMap.containsKey("parkingFeePrint")) mPrintOrderMap["parkingFeePrint"]!!.printOrder else 0.0
                    voilationModel.mPrintLayoutOrderDueDateParkingFee =
                        if (mPrintOrderMap.containsKey("parkingFeePrint")) mPrintOrderMap["parkingFeePrint"]!!.layoutOrder else ""
                    voilationModel.dueDateParkingFeeLabel =
                        if (mPrintOrderMap.containsKey("parkingFeePrint")) mPrintOrderMap["parkingFeePrint"]!!.labelName else ""
                    voilationModel.mDueDateParkingFeeFont =
                        if (mPrintOrderMap.containsKey("parkingFeePrint")) mPrintOrderMap["parkingFeePrint"]!!.printFont!!.toInt()!! else 0
                    voilationModel.mDueDateParkingFeeX =
                        if (mPrintOrderMap.containsKey("parkingFeePrint")) mPrintOrderMap["parkingFeePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    voilationModel.mDueDateParkingFeeY =
                        if (mPrintOrderMap.containsKey("parkingFeePrint")) mPrintOrderMap["parkingFeePrint"]!!.printAxisY!!.toDouble() else 0.0
                    voilationModel.mDueDateParkingFeeColumnSize =
                        if (mPrintOrderMap.containsKey("parkingFeePrint")) mPrintOrderMap["parkingFeePrint"]!!.printColumnSize!!.toInt() else 1


                } catch (e: Exception) {
                    e.printStackTrace()
//                    voilationModel.setDueDate45(String.valueOf(mViolationList.getDue_45_days()));
//                    voilationModel.setStatus_due_date_45(false);
                }
                try {
                    voilationModel.dueDateCitationFee = dueCitationFee.trim()
                    voilationModel.isStatus_due_date_citation_fee = citationFeePrint[0]
                    voilationModel.mPrintOrderDueDateCitationFee =
                        if (mPrintOrderMap.containsKey("citationFeePrint")) mPrintOrderMap["citationFeePrint"]!!.printOrder else 0.0
                    voilationModel.mPrintLayoutOrderDueDateCitationFee =
                        if (mPrintOrderMap.containsKey("citationFeePrint")) mPrintOrderMap["citationFeePrint"]!!.layoutOrder else ""
                    voilationModel.dueDateCitationFeeLabel =
                        if (mPrintOrderMap.containsKey("citationFeePrint")) mPrintOrderMap["citationFeePrint"]!!.labelName else ""
                    voilationModel.mDueDateCitationFeeFont =
                        if (mPrintOrderMap.containsKey("citationFeePrint")) mPrintOrderMap["citationFeePrint"]!!.printFont!!.toInt()!! else 0
                    voilationModel.mDueDateCitationFeeX =
                        if (mPrintOrderMap.containsKey("citationFeePrint")) mPrintOrderMap["citationFeePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    voilationModel.mDueDateCitationFeeY =
                        if (mPrintOrderMap.containsKey("citationFeePrint")) mPrintOrderMap["citationFeePrint"]!!.printAxisY!!.toDouble() else 0.0
                    voilationModel.mDueDateCitationFeeColumnSize =
                        if (mPrintOrderMap.containsKey("citationFeePrint")) mPrintOrderMap["citationFeePrint"]!!.printColumnSize!!.toInt() else 1


                } catch (e: Exception) {
                    e.printStackTrace()
//                    voilationModel.setDueDate45(String.valueOf(mViolationList.getDue_45_days()));
//                    voilationModel.setStatus_due_date_45(false);
                }
                try {
                    voilationModel.dueDateTotal = dueTotalNow.trim()
//                        voilationModel.dueDateCost = ##
                    voilationModel.isStatus_due_date_total = total_due_now[0]
                    voilationModel.mPrintOrderDueDateTotal =
                        if (mPrintOrderMap.containsKey("total_due_now")) mPrintOrderMap["total_due_now"]!!.printOrder else 0.0
                    voilationModel.mPrintLayoutOrderDueDateTotal =
                        if (mPrintOrderMap.containsKey("total_due_now")) mPrintOrderMap["total_due_now"]!!.layoutOrder else ""
                    voilationModel.dueDateTotalLabel =
                        if (mPrintOrderMap.containsKey("total_due_now")) mPrintOrderMap["total_due_now"]!!.labelName else ""
                    voilationModel.mDueDateTotalFont =
                        if (mPrintOrderMap.containsKey("total_due_now")) mPrintOrderMap["total_due_now"]!!.printFont!!.toInt()!! else 0
                    voilationModel.mDueDateTotalX =
                        if (mPrintOrderMap.containsKey("total_due_now")) mPrintOrderMap["total_due_now"]!!.printAxisX!!.toDouble()!! else 0.0
                    voilationModel.mDueDateTotalY =
                        if (mPrintOrderMap.containsKey("total_due_now")) mPrintOrderMap["total_due_now"]!!.printAxisY!!.toDouble() else 0.0
                    voilationModel.mDueDateTotalColumnSize =
                        if (mPrintOrderMap.containsKey("total_due_now")) mPrintOrderMap["total_due_now"]!!.printColumnSize!!.toInt() else 1


                } catch (e: Exception) {
                    e.printStackTrace()
//                    voilationModel.setDueDate45(String.valueOf(mViolationList.getDue_45_days()));
//                    voilationModel.setStatus_due_date_45(false);
                }
                try {
                    voilationModel.payAtOnline = payAtOnline.trim()
                    voilationModel.isStatus_pay_at_online = pay_at_online[0]
                    voilationModel.mPrintOrderPayAtOnline =
                        if (mPrintOrderMap.containsKey("pay_at_online")) mPrintOrderMap["pay_at_online"]!!.printOrder else 0.0
                    voilationModel.mPrintLayoutOrderPayAtOnline =
                        if (mPrintOrderMap.containsKey("pay_at_online")) mPrintOrderMap["pay_at_online"]!!.layoutOrder else ""
                    voilationModel.payAtOnlineLabel =
                        if (mPrintOrderMap.containsKey("pay_at_online")) mPrintOrderMap["pay_at_online"]!!.labelName else ""
                    voilationModel.mPayOnlineFont =
                        if (mPrintOrderMap.containsKey("pay_at_online")) mPrintOrderMap["pay_at_online"]!!.printFont!!.toInt()!! else 0
                    voilationModel.mPayOnlineX =
                        if (mPrintOrderMap.containsKey("pay_at_online")) mPrintOrderMap["pay_at_online"]!!.printAxisX!!.toDouble()!! else 0.0
                    voilationModel.mPayOnlineY =
                        if (mPrintOrderMap.containsKey("pay_at_online")) mPrintOrderMap["pay_at_online"]!!.printAxisY!!.toDouble() else 0.0
                    voilationModel.mPayOnlineColumnSize =
                        if (mPrintOrderMap.containsKey("pay_at_online")) mPrintOrderMap["pay_at_online"]!!.printColumnSize!!.toInt() else 1


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mAutoComTextViewAmountDueDate?.let {
                        voilationModel.amountDueDate = splitDoller(
                            mAutoComTextViewAmountDueDate?.editableText.toString().trim()
                        ).toString()
                        voilationModel.isStatus_amount_due_date = late_finePrint[0]
                        voilationModel.mLateFineDays = lateFineDays
                        voilationModel.mPrintOrderAmountDueDate =
                            if (mPrintOrderMap.containsKey("latfinePrint")) mPrintOrderMap["latfinePrint"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderAmountDueDate =
                            if (mPrintOrderMap.containsKey("latfinePrint")) mPrintOrderMap["latfinePrint"]!!.layoutOrder else ""
                        voilationModel.amountDueDateLabel =
                            if (mPrintOrderMap.containsKey("latfinePrint")) mPrintOrderMap["latfinePrint"]!!.labelName else ""
                        voilationModel.mLateFineFont =
                            if (mPrintOrderMap.containsKey("latfinePrint")) mPrintOrderMap["latfinePrint"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mLateFineX =
                            if (mPrintOrderMap.containsKey("latfinePrint")) mPrintOrderMap["latfinePrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mLateFineY =
                            if (mPrintOrderMap.containsKey("latfinePrint")) mPrintOrderMap["latfinePrint"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mLateFineColumnSize =
                            if (mPrintOrderMap.containsKey("latfinePrint")) mPrintOrderMap["latfinePrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
//                    voilationModel.setAmountDueDate(String.valueOf(mViolationList.getViolationLateFee()));
//                    voilationModel.setStatus_amount_due_date(false);
                }
                try {
                    mEtLocationDescr?.let {
                        voilationModel.locationDescr =
                            mEtLocationDescr?.editableText.toString().trim()
                        voilationModel.isStatus_location_descr = descriptionPrint[0]
                        voilationModel.mPrintOrderLocationDescr =
                            if (mPrintOrderMap.containsKey("descriptionPrint")) mPrintOrderMap["descriptionPrint"]!!.printOrder else 0.0
                        voilationModel.mPrintLayoutOrderLocationDescr =
                            if (mPrintOrderMap.containsKey("descriptionPrint")) mPrintOrderMap["descriptionPrint"]!!.layoutOrder else ""
                        voilationModel.locationDescrLabel =
                            if (mPrintOrderMap.containsKey("descriptionPrint")) mPrintOrderMap["descriptionPrint"]!!.labelName else ""
                        voilationModel.mDescrFont =
                            if (mPrintOrderMap.containsKey("descriptionPrint")) mPrintOrderMap["descriptionPrint"]!!.printFont!!.toInt()!! else 0
                        voilationModel.mLocationDescrX =
                            if (mPrintOrderMap.containsKey("descriptionPrint")) mPrintOrderMap["descriptionPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                        voilationModel.mLocationDescrY =
                            if (mPrintOrderMap.containsKey("descriptionPrint")) mPrintOrderMap["descriptionPrint"]!!.printAxisY!!.toDouble() else 0.0
                        voilationModel.mDescrColumnSize =
                            if (mPrintOrderMap.containsKey("descriptionPrint")) mPrintOrderMap["descriptionPrint"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
//                    voilationModel.setLocationDescr(""); voilationModel.setStatus_location_descr(false);
                }
                try {
                    mEtLocationRemarks?.let {
                        mIssModel.locationRemarks =
                            mEtLocationRemarks?.editableText.toString().trim()
                        mIssModel.locationRemarksObserved = mTimingRecordRemarkValue
                        mIssModel.isStatus_location_remarks = remark_1Print[0]
                        mIssModel.mPrintOrderLocationRemarks =
                            if (mPrintOrderMap.containsKey("remark1Print")) mPrintOrderMap["remark1Print"]!!.printOrder else 0.0
                        mIssModel.mPrintLayoutOrderLocationRemarks =
                            if (mPrintOrderMap.containsKey("remark1Print")) mPrintOrderMap["remark1Print"]!!.layoutOrder else ""
                        mIssModel.locationRemarksLabel =
                            if (mPrintOrderMap.containsKey("remark1Print")) mPrintOrderMap["remark1Print"]!!.labelName else ""
                        mIssModel.mRemarkFont =
                            if (mPrintOrderMap.containsKey("remark1Print")) mPrintOrderMap["remark1Print"]!!.printFont!!.toInt()!! else 0
                        mIssModel.mRemarkX =
                            if (mPrintOrderMap.containsKey("remark1Print")) mPrintOrderMap["remark1Print"]!!.printAxisX!!.toDouble()!! else 0.0
                        mIssModel.mRemarkY =
                            if (mPrintOrderMap.containsKey("remark1Print")) mPrintOrderMap["remark1Print"]!!.printAxisY!!.toDouble() else 0.0
                        mIssModel.mRemarkColumnSize =
                            if (mPrintOrderMap.containsKey("remark1Print")) mPrintOrderMap["remark1Print"]!!.printColumnSize!!.toInt() else 1

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mIssModel.locationRemarks = ""
                    mIssModel.isStatus_location_remarks = false
                }
                try {
                    mEtLocationRemarks1?.let {
                        val mEtLocationRemarks11 = mEtLocationRemarks1
                        mIssModel.locationRemarks1 =
                            mEtLocationRemarks11?.editableText.toString().trim()
                        mIssModel.locationRemarks1Observed = mTimingTireStem
                        mIssModel.isStatus_location_remarks1 = remark_2Print[0]
                        mIssModel.mPrintOrderLocationRemarks1 =
                            if (mPrintOrderMap.containsKey("remark2Print")) mPrintOrderMap["remark2Print"]!!.printOrder else 0.0
                        mIssModel.mPrintLayoutOrderLocationRemarks1 =
                            if (mPrintOrderMap.containsKey("remark2Print")) mPrintOrderMap["remark2Print"]!!.layoutOrder else ""
                        mIssModel.locationRemarks1Label =
                            if (mPrintOrderMap.containsKey("remark2Print")) mPrintOrderMap["remark2Print"]!!.labelName else ""
                        mIssModel.mRemark1Font =
                            if (mPrintOrderMap.containsKey("remark2Print")) mPrintOrderMap["remark2Print"]!!.printFont!!.toInt()!! else 0
                        mIssModel.mRemark1X =
                            if (mPrintOrderMap.containsKey("remark2Print")) mPrintOrderMap["remark2Print"]!!.printAxisX!!.toDouble()!! else 0.0
                        mIssModel.mRemark1Y =
                            if (mPrintOrderMap.containsKey("remark2Print")) mPrintOrderMap["remark2Print"]!!.printAxisY!!.toDouble() else 0.0
                        mIssModel.mRemark1ColumnSize =
                            if (mPrintOrderMap.containsKey("remark2Print")) mPrintOrderMap["remark2Print"]!!.printColumnSize!!.toInt() else 1

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    mIssModel.locationRemarks1 = ""
                    mIssModel.isStatus_location_remarks1 = false
                }

                try {
                    mEtLocationRemarks2?.let {
                        val mEtLocationRemarks2 = mEtLocationRemarks2
                        mIssModel.locationRemarks2 =
                            mEtLocationRemarks2?.editableText.toString().trim()
                        mIssModel.isStatus_location_remarks2 = remark_3Print[0]
                        mIssModel.mPrintOrderLocationRemarks2 =
                            if (mPrintOrderMap.containsKey("remark_3Print")) mPrintOrderMap["remark_3Print"]!!.printOrder else 0.0
                        mIssModel.mPrintLayoutOrderLocationRemarks2 =
                            if (mPrintOrderMap.containsKey("remark_3Print")) mPrintOrderMap["remark_3Print"]!!.layoutOrder else ""
                        mIssModel.locationRemarks2Label =
                            if (mPrintOrderMap.containsKey("remark_3Print")) mPrintOrderMap["remark_3Print"]!!.labelName else ""
                        mIssModel.mRemark2Font =
                            if (mPrintOrderMap.containsKey("remark_3Print")) mPrintOrderMap["remark_3Print"]!!.printFont!!.toInt()!! else 0
                        mIssModel.mRemark2X =
                            if (mPrintOrderMap.containsKey("remark_3Print")) mPrintOrderMap["remark_3Print"]!!.printAxisX!!.toDouble()!! else 0.0
                        mIssModel.mRemark2Y =
                            if (mPrintOrderMap.containsKey("remark_3Print")) mPrintOrderMap["remark_3Print"]!!.printAxisY!!.toDouble() else 0.0
                        mIssModel.mRemark2ColumnSize =
                            if (mPrintOrderMap.containsKey("remark_3Print")) mPrintOrderMap["remark_3Print"]!!.printColumnSize!!.toInt() else 1

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    mIssModel.locationRemarks2 = ""
                    mIssModel.isStatus_location_remarks2 = false
                }
                try {
                    mEtLocationNotes?.let {
                        mIssModel.locationNotes = mEtLocationNotes?.editableText.toString().trim()
                        mIssModel.isStatus_location_notes = note_1Print[0]
                        mIssModel.mPrintOrderLocationNotes =
                            if (mPrintOrderMap.containsKey("note1Print")) mPrintOrderMap["note1Print"]!!.printOrder else 0.0
                        mIssModel.mPrintLayoutOrderLocationNotes =
                            if (mPrintOrderMap.containsKey("note1Print")) mPrintOrderMap["note1Print"]!!.layoutOrder else ""
                        mIssModel.locationNotesLabel =
                            if (mPrintOrderMap.containsKey("note1Print")) mPrintOrderMap["note1Print"]!!.labelName else ""

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mIssModel.locationNotes = ""
                    mIssModel.isStatus_location_notes = false
                }
                try {
                    mEtLocationNotes1?.let {
                        mIssModel.locationNotes1 = mEtLocationNotes1?.editableText.toString().trim()
                        mIssModel.isStatus_location_notes1 = note_2Print[0]
                        mIssModel.mPrintOrderLocationNotes1 =
                            if (mPrintOrderMap.containsKey("note2Print")) mPrintOrderMap["note2Print"]!!.printOrder else 0.0
                        mIssModel.mPrintLayoutOrderLocationNotes1 =
                            if (mPrintOrderMap.containsKey("note2Print")) mPrintOrderMap["note2Print"]!!.layoutOrder else ""
                        mIssModel.locationNotes1Label =
                            if (mPrintOrderMap.containsKey("note2Print")) mPrintOrderMap["note2Print"]!!.labelName else ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mIssModel.locationNotes1 = ""
                    mIssModel.isStatus_location_notes1 = false
                }
                try {
                    mEtLocationNotes2?.let {
                        mIssModel.locationNotes2 = mEtLocationNotes2?.editableText.toString().trim()
                        mIssModel.isStatus_location_notes2 = note_2Print[0]
                        mIssModel.mPrintOrderLocationNotes2 =
                            if (mPrintOrderMap.containsKey("note3Print")) mPrintOrderMap["note3Print"]!!.printOrder else 0.0
                        mIssModel.mPrintLayoutOrderLocationNotes2 =
                            if (mPrintOrderMap.containsKey("note3Print")) mPrintOrderMap["note3Print"]!!.layoutOrder else ""
                        mIssModel.locationNotes2Label =
                            if (mPrintOrderMap.containsKey("note3Print")) mPrintOrderMap["note3Print"]!!.labelName else ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mIssModel.locationNotes2 = ""
                    mIssModel.isStatus_location_notes2 = false
                }
                try {

                    mIssModel.locationBottomText =
                        if (mPrintOrderMap.containsKey("BottomLabelPrint")) mPrintOrderMap["BottomLabelPrint"]!!.labelName else ""
                    mIssModel.isStatus_location_bottomtext = bottomLabelPrint
                    mIssModel.mPrintOrderLocationBottomText =
                        if (mPrintOrderMap.containsKey("BottomLabelPrint")) mPrintOrderMap["BottomLabelPrint"]!!.printOrder else 0.0
                    mIssModel.mPrintLayoutOrderLocationBottomText =
                        if (mPrintOrderMap.containsKey("BottomLabelPrint")) mPrintOrderMap["BottomLabelPrint"]!!.layoutOrder else ""
                    mIssModel.locationBottomTextLabel =
                        if (mPrintOrderMap.containsKey("BottomLabelPrint")) mPrintOrderMap["BottomLabelPrint"]!!.labelName else ""
                    mIssModel.mBottomTextFont =
                        if (mPrintOrderMap.containsKey("BottomLabelPrint")) mPrintOrderMap["BottomLabelPrint"]!!.printFont!!.toInt()!! else 0
                    mIssModel.mBottomTextX =
                        if (mPrintOrderMap.containsKey("BottomLabelPrint")) mPrintOrderMap["BottomLabelPrint"]!!.printAxisX!!.toDouble()!! else 0.0
                    mIssModel.mBottomTextY =
                        if (mPrintOrderMap.containsKey("BottomLabelPrint")) mPrintOrderMap["BottomLabelPrint"]!!.printAxisY!!.toDouble() else 0.0


                } catch (e: Exception) {
                    e.printStackTrace()
                    mIssModel.locationBottomText = ""
                    mIssModel.isStatus_location_bottomtext = false
                }

                mIssModel.voilation = voilationModel
                mIssModel.startTime = mStartTimeStamp
                try {
                    mIssModel.issueTime = splitDateLpr(mZone)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                mIssDatabase.citationData = mIssModel
                mIssDatabase.formStatus = 0
                mIssDatabase.citationNumber = mLatestCitation.citationBooklet

                    if (mBookletStatus == 0) {
                        try {
                            citationFormScreenViewModel.updateCitationBooklet(
                                1, mLatestCitation.citationBooklet
                            )
                            citationFormScreenViewModel.insertCitationInsurance(mIssDatabase)
                            citationFormScreenViewModel.updateCitationUploadStatus(
                                2, mLatestCitation.citationBooklet
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (mBookletStatus == 1) {
                        try {
                            citationFormScreenViewModel.updateCitationInsurance(
                                mIssModel, mLatestCitation.citationBooklet
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            printLog("ticketnoerror", e.message)
                        }
                    }
                }
                return "Done"
            }

            override fun onPostExecute(result: String?) {
                DialogUtil.hideLoader()
                //dismissLoader()
                //val count = mDb?.dbDAO?.countCitationIssurrance
                //  LogUtil.printToastMSG(requireContext(),"Citation Issurance saved!"+mTicketType+"count -"+count);

//            val mIntent = Intent(requireContext(), LprPreviewActivity::class.java)
//            mIntent.putExtra("booklet_id", mLatestCitation!!.citationBooklet)
//            mIntent.putExtra("btn_action", mTicketActionButtonEvent)
//            mIntent.putExtra("Citation_Images_Link", mCitationImagesLinks as ArrayList<String>)
//            startActivityForResult(mIntent, 2020)
            }
        }
        SaveCitationIssuranceTask().execute()
    }

//    import kotlinx.coroutines.CoroutineScope
//    import kotlinx.coroutines.Dispatchers
//    import kotlinx.coroutines.launch
//    import kotlinx.coroutines.withContext

//    fun saveCitationAndNavigate(mLatestCitation: CitationBookletModel, mIssDatabase: CitationInsurranceDatabaseModel, onComplete: () -> Unit) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                getMyDatabase()?.dbDAO?.updateCitationBooklet(1, mLatestCitation.citationBooklet)
//                getMyDatabase()?.dbDAO?.insertCitationInssurrance(mIssDatabase)
//                getMyDatabase()?.dbDAO?.updateCitationUploadStatus(2, mLatestCitation.citationBooklet)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            withContext(Dispatchers.Main) {
//                onComplete() // Call navigation here
//            }
//        }
//    }


    //to create dynamic layout components
    private fun createDynamicView() {
        if (mCitationLayout!!.size > 0) {
            DialogUtil.showLoader(requireContext(), getString(R.string.scr_message_please_wait))
            for (iCit in mCitationLayout!!.indices) {
                tvEnforcementTitle.isFocusable = true
                tvEnforcementTitle.isFocusableInTouchMode = true
                if (iCit == mCitationLayout!!.size - 1) {
                    DialogUtil.hideLoader()
                    //dismissLoader();
                    getLprVehicleDataFromIntent()
//                    getVehicalDetailsFromDb()
                }
                if (mCitationLayout!![iCit].component.equals("Header", ignoreCase = true)) {
                    mTvTicketDetailsHide.visibility = View.VISIBLE
                    layTicketDetailsHide.visibility = View.VISIBLE
                    val mHeaderSize = mCitationLayout!![iCit].fields!!.size
                    if (mHeaderSize == 1) {
                        cit_numPrint = mCitationLayout!![iCit].fields!![0].print!!
                        val number: Double =
                            if (mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber =
                            if (mCitationLayout!![iCit].fields!![0].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mFormLayoutOrder else "0" else "0"
//                        mPrintOrderMap["citNumber"] = PrintLayoutModel(number,1, layoutNumber,
//                                getString(R.string.scr_lbl_ticket_number))

                        mPrintOrderMap["citNumber"] = PrintLayoutModel(
                            number,
                            1,
                            layoutNumber,
                            mCitationLayout!![iCit].fields!![0].repr,
                            mCitationLayout!![iCit].fields!![0].mPositionXYFont
                        )
                        appCompatTextViewTicketNumberTitle!!.setText(mCitationLayout!![iCit].fields!![0].repr)
//                        appCompatTextViewTicketNumberTitle!!.setTag(mCitationLayout!![iCit].fields!![0].mDataTypeValidation)
                        layTicketNum.visibility = View.VISIBLE
                        layTicketDate.visibility = View.GONE
                    }
                    if (mHeaderSize == 2) {
                        cit_numPrint = mCitationLayout!![iCit].fields!![0].print!!
                        timestampPrint = mCitationLayout!![iCit].fields!![1].print!!
                        val number: Double =
                            if (mCitationLayout!![iCit].fields!![1].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![1].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![1].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber =
                            if (mCitationLayout!![iCit].fields!![1].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![1].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![1].mFormLayoutOrder else "0" else "0"
                        mPrintOrderMap["timestamPrint"] = PrintLayoutModel(
                            number,
                            1,
                            layoutNumber,
                            mCitationLayout!![iCit].fields!![1].repr,
                            mCitationLayout!![iCit].fields!![1].mPositionXYFont
                        )
                        val number2: Double =
                            if (mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber2 =
                            if (mCitationLayout!![iCit].fields!![0].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mFormLayoutOrder else "0" else "0"

                        val columnSize: Int =
                            mCitationLayout?.get(iCit)?.fields?.get(0)?.mDisplayColumn?.takeIf {
                                !TextUtils.isEmpty(it)
                            }?.toIntOrNull() ?: 1

//                        mPrintOrderMap["citNumber"] = PrintLayoutModel(number2,1, layoutNumber2,getString(R.string.scr_lbl_ticket_number))
                        mPrintOrderMap["citNumber"] = PrintLayoutModel(
                            number2,
                            1,
                            layoutNumber2,
                            mCitationLayout!![iCit].fields!![0].repr,
                            mCitationLayout!![iCit].fields!![0].mPositionXYFont,
                            columnSize
                        )
                        appCompatTextViewTicketNumberTitle!!.setText(mCitationLayout!![iCit].fields!![0].repr)
                        layTicketNum.visibility = View.VISIBLE
                        layTicketDate.visibility = View.VISIBLE
                    }
                }
                //else { mTvTicketDetailsHide.setVisibility(View.GONE); }
                if (mCitationLayout!![iCit].component.equals(
                        "Citation Type", ignoreCase = true
                    ) || mCitationLayout!![iCit].component.equals("CHECK_BOXES", ignoreCase = true)
                ) {

                    var mHeaderSize = mCitationLayout!![iCit].fields!!.size
                    if (mHeaderSize > 0) {
                        mlinearLayoutCheckBox.visibility = View.VISIBLE
                        layCheckbox1.visibility = View.VISIBLE
                        layCheckbox2.visibility = View.VISIBLE
                        layCheckbox3.visibility = View.VISIBLE
                    } else {
                        mlinearLayoutCheckBox.visibility = View.GONE
                        layCheckbox1.visibility = View.GONE
                        layCheckbox2.visibility = View.GONE
                        layCheckbox3.visibility = View.GONE
                    }
                    if (mHeaderSize > 0 && mCitationLayout!![iCit].fields!![0].optionsCheckBox != null) {
                        mHeaderSize = mCitationLayout!![iCit].fields!![0].optionsCheckBox!!.size
                    }
                    if (mHeaderSize == 1) {
                        appCompatTextViewTicketType.text = mCitationLayout!![iCit].fields!![0].repr
                        warningPrint = mCitationLayout!![iCit].fields!![0].print!!
                        mTvType1.text = mCitationLayout!![iCit].fields!![0].repr
                        mCheckboxType1.tag =
                            if (mCitationLayout!![iCit].fields!![0].mCalculatedField != null) mCitationLayout!![iCit].fields!![0].mCalculatedField else ""
                        val number: Double =
                            if (mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber =
                            if (mCitationLayout!![iCit].fields!![0].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mFormLayoutOrder else "0" else "0"
                        mPrintOrderMap["warningPrint"] = PrintLayoutModel(
                            number,
                            1,
                            layoutNumber,
                            mCitationLayout!![iCit].fields!![0].repr,
                            mCitationLayout!![iCit].fields!![0].mPositionXYFont
                        )
                        layCheckbox2.visibility = View.INVISIBLE
                        layCheckbox3.visibility = View.INVISIBLE
                    }
                    if (mHeaderSize == 2) {
                        appCompatTextViewTicketType.text = mCitationLayout!![iCit].fields!![0].repr
                        warningPrint = mCitationLayout!![iCit].fields!![0].print!!
                        bootPrint = mCitationLayout!![iCit].fields!![1].print!!
                        mTvType1.text = mCitationLayout!![iCit].fields!![0].repr
                        mTvType2.text = mCitationLayout!![iCit].fields!![1].repr
                        mCheckboxType1.tag =
                            if (mCitationLayout!![iCit].fields!![0].mCalculatedField != null) mCitationLayout!![iCit].fields!![0].mCalculatedField else ""
                        mCheckboxType2.tag =
                            if (mCitationLayout!![iCit].fields!![1].mCalculatedField != null) mCitationLayout!![iCit].fields!![1].mCalculatedField else ""
                        val number: Double =
                            if (mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber =
                            if (mCitationLayout!![iCit].fields!![0].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mFormLayoutOrder else "0" else "0"
                        mPrintOrderMap["warningPrint"] = PrintLayoutModel(
                            number,
                            1,
                            layoutNumber,
                            mCitationLayout!![iCit].fields!![0].repr,
                            mCitationLayout!![iCit].fields!![0].mPositionXYFont
                        )
                        val number2: Double =
                            if (mCitationLayout!![iCit].fields!![1].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![1].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![1].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber2 =
                            if (mCitationLayout!![iCit].fields!![1].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![1].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![1].mFormLayoutOrder else "0" else "0"
                        mPrintOrderMap["bootPrint"] = PrintLayoutModel(
                            number2,
                            1,
                            layoutNumber2,
                            mCitationLayout!![iCit].fields!![1].repr,
                            mCitationLayout!![iCit].fields!![1].mPositionXYFont
                        )
                        layCheckbox3.visibility = View.INVISIBLE
                    }
                    if (mHeaderSize == 3) {
                        appCompatTextViewTicketType.text = mCitationLayout!![iCit].fields!![0].repr
                        warningPrint = mCitationLayout!![iCit].fields!![0].print!!
                        bootPrint = mCitationLayout!![iCit].fields!![0].print!!
                        drive_offPrint = mCitationLayout!![iCit].fields!![0].print!!
                        if (checkBuildConfigForLprDetails2ForGetWarningFromObject()) {

                            mTvType1.text = mCitationLayout!![iCit].fields!![0].repr
                            mTvType2.text = mCitationLayout!![iCit].fields!![1].repr
                            mTvType3.text = mCitationLayout!![iCit].fields!![2].repr

                        } else {
                            mTvType1.text =
                                mCitationLayout!![iCit].fields!![0].optionsCheckBox!![0].mTitle
                            mTvType2.text =
                                mCitationLayout!![iCit].fields!![0].optionsCheckBox!![1].mTitle
                            mTvType3.text =
                                mCitationLayout!![iCit].fields!![0].optionsCheckBox!![2].mTitle

                        }

                        if (mCitationLayout!![iCit].fields!!.size > 0) {
                            mCheckboxType1.tag =
                                if (mCitationLayout!![iCit].fields!![0].mCalculatedField != null) mCitationLayout!![iCit].fields!![0].mCalculatedField else ""
                        }
                        if (mCitationLayout!![iCit].fields!!.size > 1) {
                            mCheckboxType2.tag =
                                if (mCitationLayout!![iCit].fields!![1].mCalculatedField != null) mCitationLayout!![iCit].fields!![1].mCalculatedField else ""
                        }
                        if (mCitationLayout!![iCit].fields!!.size > 2) {
                            mCheckboxType2.tag =
                                if (mCitationLayout!![iCit].fields!![2].mCalculatedField != null) mCitationLayout!![iCit].fields!![2].mCalculatedField else ""
                        }
                        val number: Double =
                            if (mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber =
                            if (mCitationLayout!![iCit].fields!![0].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mFormLayoutOrder else "0" else "0"
                        mPrintOrderMap["warningPrint"] = PrintLayoutModel(
                            number,
                            1,
                            layoutNumber,
                            mCitationLayout!![iCit].fields!![0].repr,
                            mCitationLayout!![iCit].fields!![0].mPositionXYFont
                        )
                        val number2: Double =
                            if (mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber2 =
                            if (mCitationLayout!![iCit].fields!![0].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mFormLayoutOrder else "0" else "0"

                        mPrintOrderMap["bootPrint"] = PrintLayoutModel(
                            number2,
                            1,
                            layoutNumber2,
                            mCitationLayout!![iCit].fields!![0].repr,
                            mCitationLayout!![iCit].fields!![0].mPositionXYFont
                        )
                        val number3: Double =
                            if (mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mPrintLayoutOrder.nullSafety()
                                .toDouble() else "0".toDouble() else "0".toDouble()
                        val layoutNumber3 =
                            if (mCitationLayout!![iCit].fields!![0].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                    mCitationLayout!![iCit].fields!![0].mFormLayoutOrder
                                )
                            ) mCitationLayout!![iCit].fields!![0].mFormLayoutOrder else "0" else "0"
                        mPrintOrderMap["bootPrint"] = PrintLayoutModel(
                            number3,
                            1,
                            layoutNumber3,
                            mCitationLayout!![iCit].fields!![0].repr,
                            mCitationLayout!![iCit].fields!![0].mPositionXYFont
                        )
                    }
                }
                if (mCitationLayout!![iCit].component.equals("Officer", ignoreCase = true)) {
                    linearLayoutOfficerDetails.visibility = View.VISIBLE
                    for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "officer_name", ignoreCase = true
                            )
                        ) {
                            officer_namePrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            mPrintOrderMap["officernamePrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )

                            mTvOfficerName = CheckTypeOfField(
                                mCitationLayout!![iCit].fields!![iOff],
                                linearLayoutOfficerDetails,
                                mCitationLayout!![iCit].component!!,
                                requireContext()
                            )
                        } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "badge_id", ignoreCase = true
                            )
                        ) {
                            badge_idPrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"

                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["badgePrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                            mTvBadgeId = CheckTypeOfField(
                                mCitationLayout!![iCit].fields!![iOff],
                                linearLayoutOfficerDetails,
                                mCitationLayout!![iCit].component!!,
                                requireContext()
                            )
                        } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "beat", ignoreCase = true
                            )
                        ) {
                            beatPrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"

                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1

                            mPrintOrderMap["beatPrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )

                            mTvBeat = CheckTypeOfField(
                                mCitationLayout!![iCit].fields!![iOff],
                                linearLayoutOfficerDetails,
                                mCitationLayout!![iCit].component!!,
                                requireContext()
                            )
                            mTvBeat?.setEnabled(true)
                            mTvBeat?.setFocusable(true)
                            mTvBeat?.setFocusableInTouchMode(true)
                        } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "signature", ignoreCase = true
                            )
                        ) {
                            //officer_namePrint = mCitationLayout.get(iCit).getFields().get(iOff).getPrint();
                            mTextViewSignName.visibility = View.VISIBLE
                            imageViewSignature.visibility = View.VISIBLE

                            sharedPreference.write(
                                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )

                            sharedPreference.write(
                                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )

                            sharedPreference.write(
                                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_WIDTH,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "200"
                            )
                        } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "squad", ignoreCase = true
                            )
                        ) {
                            squadPrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["squadPrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                            mTvSquad = CheckTypeOfField(
                                mCitationLayout!![iCit].fields!![iOff],
                                linearLayoutOfficerDetails,
                                mCitationLayout!![iCit].component!!,
                                requireContext()
                            )
                        } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "zone", ignoreCase = true
                            )
                        ) {
                            zonePrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["zonePrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                            mTvZone = CheckTypeOfField(
                                mCitationLayout!![iCit].fields!![iOff],
                                linearLayoutOfficerDetails,
                                mCitationLayout!![iCit].component!!,
                                requireContext()
                            )
                        } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "agency", ignoreCase = true
                            )
                        ) {
                            agencyPrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["agencyPrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                            mTvAgency = CheckTypeOfField(
                                mCitationLayout!![iCit].fields!![iOff],
                                linearLayoutOfficerDetails,
                                mCitationLayout!![iCit].component!!,
                                requireContext()
                            )
                        } else {
                            try {
                                latestLayoutOfficer++
                                name[latestLayoutOfficer] = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutOfficerDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(1000)
                    getStateAndCamera2Setting()
                    if (mCitationLayout!![iCit].component.equals("Vehicle", ignoreCase = true)) {
                        linearLayoutVehicleDetails.visibility = View.VISIBLE
                        for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                            if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "make", ignoreCase = true
                                )
                            ) {
                                makePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["makePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mTvOfficerMake = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                setFieldCaps(requireContext(), mTvOfficerMake!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "model", ignoreCase = true
                                )
                            ) {
                                modelPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["modelPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mTvOfficerModel = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                setFieldCaps(requireContext(), mTvOfficerModel!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "color", ignoreCase = true
                                )
                            ) {
                                colorPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["colorPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mTvOfficerColor = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                setFieldCaps(requireContext(), mTvOfficerColor!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lp_number", ignoreCase = true
                                )
                            ) {
                                lp_numberPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"

                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1

                                mPrintOrderMap["lpnumnerPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewLicensePlate = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mAutoComTextViewLicensePlate?.isAllCaps = true
                                mAutoComTextViewLicensePlate?.inputType =
                                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                                mLockCitation =
                                    if (mCitationLayout!![iCit].fields!![iOff].mDataTypeValidation != null && mCitationLayout!![iCit].fields!![iOff].mDataTypeValidation.equals(
                                            "lock", ignoreCase = true
                                        )
                                    ) {
                                        "lock"
                                    } else {
                                        if (mFromScreen.nullSafety().equals("WelcomeUnUpload")) {
                                            "lock"
                                        } else {
                                            "unlock"
                                        }//VINOD lock code here when come form offline citation
                                    }
                                sharedPreference.write(
                                    SharedPrefKey.CITATION_LOCK_PARTIALLY_LOCK, mLockCitation
                                )
                                try {
                                    settingsList =
                                        mainActivityViewModel.getSettingsListFromDataSet()
                                    if (settingsList != null) {
                                        textWatcherForLicensePlate(
                                            mAutoComTextViewLicensePlate!!,
                                            settingsList!!,
                                            requireContext()!!,
                                            object : AppUtils.TextWatcherCallBackForAPICall {
                                                override fun callActivityLogAPI() {
                                                    val buttonAction =
                                                        arguments?.getString("btn_action")
                                                            .nullSafety()
                                                    if (LogUtil.isEnableActivityLogs && buttonAction.equals(
                                                            "VoidReissue", true
                                                        ) && mAutoComTextViewLicensePlate?.hasFocus()
                                                            .nullSafety()
                                                    ) {
                                                        lifecycleScope.launch {
                                                            mainActivityViewModel.sendActionToMain(
                                                                MainActivityAction.EventActivityLogAPI(
                                                                    ACTIVITY_LOG_UPDATE_PLATE
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            })
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (mCitationLayout!![iCit].fields!![iOff].mCalculatedField != null && mCitationLayout!![iCit].fields!![iOff].mCalculatedField.equals(
                                        "format", ignoreCase = true
                                    )
                                ) {
                                    isCheckNumberPlateFormat = "format"
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "state", ignoreCase = true
                                )
                            ) {
                                statePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["statePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewState = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                try {
//                                    getStateAndCamera2Setting()
                                    setFieldCaps(requireContext(), mAutoComTextViewState!!)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "body_style", ignoreCase = true
                                )
                            ) {
                                body_stylePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()
                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["bodystylePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewBodyStyle = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                setFieldCaps(
                                    requireContext(), mAutoComTextViewBodyStyle!!
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "decal_year", ignoreCase = true
                                )
                            ) {
                                decal_yearPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()
                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["decalyearPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewDecalYear = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(
                                    requireContext(), mAutoComTextViewDecalYear!!
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "vin_number", ignoreCase = true
                                )
                            ) {
                                vin_numberPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["vinnumberPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                if (BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true
                                    )
                                ) {
                                    mCitationLayout!![iCit].fields!![iOff].tag = "editviewcamera"
                                    mAutoComTextViewVinNum = CheckTypeOfField(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        linearLayoutVehicleDetails,
                                        mCitationLayout!![iCit].component!!,
                                        requireContext()
                                    )
                                } else {
                                    mAutoComTextViewVinNum = CheckTypeOfField(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        linearLayoutVehicleDetails,
                                        mCitationLayout!![iCit].component!!,
                                        requireContext()
                                    )
                                }
                                var maxL = 17
                                var minL = 7
                                try {
                                    minL =
                                        mCitationLayout!![iCit].fields!![iOff].mMinLength.nullSafety()
                                            .toInt()
                                    maxL =
                                        mCitationLayout!![iCit].fields!![iOff].mMaxLength.nullSafety()
                                            .toInt()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (mVin != null && !mVin!!.isEmpty()) {
                                    mAutoComTextViewVinNum?.setText(mVin)
                                }
                                mAutoComTextViewVinNum?.tag = minL
                                mAutoComTextViewVinNum?.filters =
                                    arrayOf<InputFilter>(LengthFilter(maxL))
                                mAutoComTextViewVinNum?.inputType =
                                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                                Util.setFieldCaps(
                                    requireContext(), mAutoComTextViewVinNum!!
                                )

//                                Copy last 8 should not happen for Glendale & Phli.
//                                if (checkBuildConfigForCitationFormVinNumberNotCopyInLprField()) {
                                if (mSendStateEmptyForFullVinNumberAndCopyLast8DigitVinForLpr.equals(
                                        "YES"
                                    )
                                ) {
                                    mAutoComTextViewVinNum?.onFocusChangeListener =
                                        OnFocusChangeListener { v, hasFocus ->
                                            val inputTextForVin =
                                                mAutoComTextViewVinNum?.text.toString()
                                            if (!hasFocus && mAutoComTextViewVinNum?.text.toString().length > 16 && (!mApplicationVinList.any { it.direction == inputTextForVin })) {
                                                val vinNumber =
                                                    mAutoComTextViewVinNum?.text.toString()
                                                mAutoComTextViewLicensePlate?.setText(
                                                    vinNumber.substring(
                                                        9, vinNumber.length
                                                    )
                                                )
                                            }
                                        }
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "decal_number", ignoreCase = true
                                )
                            ) {
                                decal_numberPrint[0] =
                                    mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["decalnumberPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewDecalNum = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                if (BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
                                    )
                                ) {
                                    mAutoComTextViewDecalNum?.setText(
                                        sharedPreference.read(
                                            SharedPrefKey.LOGIN_HEARING_DATE, ""
                                        )
                                    )
                                    mAutoComTextViewDecalNum?.setOnClickListener {
                                        requireActivity().hideSoftKeyboard()
                                        openDataPicker(mAutoComTextViewDecalNum)
                                    }
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "expiry_year", ignoreCase = true
                                )
                            ) {
                                decal_numberPrint[0] =
                                    mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["expiryyearPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoCompleteDate = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                expiryPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!

//                                mAutoCompleteDate?.setOnTouchListener { view, motionEvent ->
//                                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                                        requireActivity().hideSoftKeyboard()
//                                        openDataPicker(mAutoCompleteDate)
//                                    }
//                                    false
//                                }
                                if (AppUtils.setExpiryYearBasedOnSettingResponse()) {
                                    setDropdownExpiryYear()
                                } else {
                                    mAutoCompleteDate?.setOnClickListener {
                                        requireActivity().hideSoftKeyboard()
                                        openDataPicker(mAutoCompleteDate)
                                    }
                                }

                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "expiry_month", ignoreCase = true
                                )
                            ) {
                                mAutoCompleteExpiryMonth = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVehicleDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )

                                setDropdownExpiryMonth()
//                                mAutoCompleteExpiryMonth?.setOnTouchListener { view, motionEvent ->
//                                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                                        requireActivity().hideSoftKeyboard()
//                                        MonthPickerDialogFragment { monthIndex, monthName ->
//                                            mAutoCompleteExpiryMonth?.setText(
//                                                String.format(
//                                                    "%02d",
//                                                    (monthIndex + 1)
//                                                )
//                                            )
//                                            requireActivity().hideSoftKeyboard()
//                                        }.show(supportFragmentManager, "MonthPicker")
//                                    }
//                                        false
//                                }
//                                    mAutoCompleteExpiryMonth?.setOnClickListener {
////                                        requireActivity().hideSoftKeyboard()
//                                        MonthPickerDialogFragment { monthIndex, monthName ->
//                                            mAutoCompleteExpiryMonth?.setText(String.format("%02d",(monthIndex+1)))
//                                            requireActivity().hideSoftKeyboard()
//                                        }.show(supportFragmentManager, "MonthPicker")
//                                }
                            } else {
                                try {
                                    latestLayoutVehicle++
                                    name[latestLayoutVehicle] = CheckTypeOfField(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        linearLayoutVehicleDetails,
                                        mCitationLayout!![iCit].component!!,
                                        requireContext()
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    if (mCitationLayout!![iCit].component.equals("Location", ignoreCase = true)) {
                        linearLayoutLocationDetails.visibility = View.VISIBLE
                        viewLocationDetails.visibility = View.VISIBLE
                        for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                            if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "street", ignoreCase = true
                                )
                            ) {
                                streetPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["streetPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewStreet = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mAutoComTextViewStreet?.isAllCaps = true
                                setFieldCaps(requireContext(), mAutoComTextViewStreet!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "block", ignoreCase = true
                                )
                            ) {
                                blockPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["blockPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewBlock = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(requireContext(), mAutoComTextViewBlock!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "direction", ignoreCase = true
                                )
                            ) {
                                directionPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["directionPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewDirection = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                setFieldCaps(requireContext(), mAutoComTextViewDirection!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "side", ignoreCase = true
                                )
                            ) {
                                sidePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()
                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1

                                mPrintOrderMap["sidePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewSide = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(requireContext(), mAutoComTextViewSide!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "meter", ignoreCase = true
                                )
                            ) {
                                meterPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1

                                mPrintOrderMap["meterPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewMeterName = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(
                                    requireContext(), mAutoComTextViewMeterName!!
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "space", ignoreCase = true
                                )
                            ) {
                                spacePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["spacePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoConTextViewSpace = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(requireContext(), mAutoConTextViewSpace!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "zone", ignoreCase = true
                                )
                            ) {
                                zonePrint = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["zonePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewZone = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mAutoComTextViewZone?.isAllCaps = true
                                setFieldCaps(requireContext(), mAutoComTextViewZone!!)
                                setDropdownZone(mSelectedCityZone)
                                Util.setFieldCaps(requireContext(), mAutoComTextViewZone!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "city_zone", ignoreCase = true
                                ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "pbc_zone", ignoreCase = true
                                )
                            ) {
                                cityZonePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["city_zone"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewPBCZone = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mAutoComTextViewPBCZone?.isAllCaps = true
                                setFieldCaps(requireContext(), mAutoComTextViewPBCZone!!)
                                setDropdownPBCZone(mPBCZone)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "location", ignoreCase = true
                                )
                            ) {
                                locationPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["locationPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewLocation = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(
                                    requireContext(), mAutoComTextViewLocation!!
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lot", ignoreCase = true
                                )
                            ) {
                                lotPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["lotPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewLot = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutLocationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(requireContext(), mAutoComTextViewLot!!)
                            } else {
                                try {
                                    latestLayoutLocation++
                                    name[latestLayoutLocation] = CheckTypeOfField(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        linearLayoutLocationDetails,
                                        mCitationLayout!![iCit].component!!,
                                        requireContext()
                                    )
                                    if (mCitationLayout!![iCit].fields!![iOff].fieldName.equals(
                                            "street_name", ignoreCase = true
                                        )
                                    ) {
                                        name[latestLayoutLocation]!!.setText(mGisAddress)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(1000)
                    if (mCitationLayout!![iCit].component.equals("Violation", ignoreCase = true)) {
                        linearLayoutVoilationDetails.visibility = View.VISIBLE
                        viewVoilationDetails.visibility = View.VISIBLE
                        for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                            if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "violation", ignoreCase = true
                                ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "violation_abbr", ignoreCase = true
                                )
                            ) {
                                codePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["violationPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewAbbrCode = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                setFieldCaps(requireContext(), mAutoComTextViewAbbrCode!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "code", ignoreCase = true
                                )
                            ) {
                                violationPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["codePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewCode = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(requireContext(), mAutoComTextViewCode!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "description", ignoreCase = true
                                )
                            ) {
                                descriptionPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["descriptionPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mEtLocationDescr = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(requireContext(), mEtLocationDescr!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "fine", ignoreCase = true
                                )
                            ) {
                                finePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                if (!mCitationLayout!![iCit].fields!![iOff].mCalculatedField!!.isEmpty()) {
                                    amountDaysCitationForm =
                                        mCitationLayout!![iCit].fields!![iOff].mCalculatedField?.toInt()!!
                                }
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["finePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewAmount = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )

                                fineCalculatedField =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDataTypeValidation.nullSafety()
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "late_fine", ignoreCase = true
                                )
                            ) {
                                late_finePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                if (!mCitationLayout!![iCit].fields!![iOff].mCalculatedField!!.isEmpty()) {
                                    lateFineDaysCitationForm =
                                        mCitationLayout!![iCit].fields!![iOff].mCalculatedField?.toInt()!!
                                }
//                                if(mCitationLayout!![iCit].fields!![iOff].mCalculatedField?.isNotEmpty() == true)
//                                    mCitationLayout!![iCit].fields!![iOff].mCalculatedField?.toInt()!!
//                                else 0
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["latfinePrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewAmountDueDate = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "due_15_days", ignoreCase = true
                                )
                            ) {
                                due_15_daysPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                if (!mCitationLayout!![iCit].fields!![iOff].mCalculatedField!!.isEmpty()) {
                                    lateFine15DaysCitationForm =
                                        mCitationLayout!![iCit].fields!![iOff].mCalculatedField?.toInt()!!
                                }
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["due15dayPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                AutoComTextViewDueDate = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                AutoComTextViewDueDate?.setOnClickListener {
                                    openDataPicker(AutoComTextViewDueDate)
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "due_30_days", ignoreCase = true
                                )
                            ) {
                                due_30_daysPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                if (!mCitationLayout!![iCit].fields!![iOff].mCalculatedField!!.isEmpty()) {
                                    lateFine30Days =
                                        mCitationLayout!![iCit].fields!![iOff].mCalculatedField?.toInt()!!
                                }
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["due30dayPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                AutoComTextViewDueDate30 = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                AutoComTextViewDueDate30?.setOnClickListener {
                                    openDataPicker(AutoComTextViewDueDate30)
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "due_45_days", ignoreCase = true
                                )
                            ) {
                                due_45_daysPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                if (!mCitationLayout!![iCit].fields!![iOff].mCalculatedField!!.isEmpty()) {
                                    lateFine45Days =
                                        mCitationLayout!![iCit].fields!![iOff].mCalculatedField?.toInt()!!
                                }
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["due45dayPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                AutoComTextViewDueDate45 = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                AutoComTextViewDueDate45?.setOnClickListener {
                                    openDataPicker(AutoComTextViewDueDate30)
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "vio_type_code", ignoreCase = true
                                )
                            ) {
                                vio_type_codePrint[0] =
                                    mCitationLayout!![iCit].fields!![iOff].print!!

                                val printOrder =
                                    mCitationLayout?.getOrNull(iCit)?.fields?.getOrNull(iOff)?.mPrintLayoutOrder.safeDoubleFromLayout()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["vio_type_code"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mCitationLayout!![iCit].fields!![iOff].tag = "dropdown"
                                mAutoComTextViewVioTypeCode = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(requireContext(), mAutoComTextViewVioTypeCode!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "vio_type_description", ignoreCase = true
                                )
                            ) {
                                vio_type_descriptionPrint[0] =
                                    mCitationLayout!![iCit].fields!![iOff].print!!
//                            val printOrder: Double =
//                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
//                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
//                                    )) if(mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains("#"))
//                                    mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety().split("#")[0].toDouble()
//                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
//                                    .toDouble() else "0".toDouble() else "0".toDouble()

                                val printOrder =
                                    mCitationLayout?.getOrNull(iCit)?.fields?.getOrNull(iOff)?.mPrintLayoutOrder.safeDoubleFromLayout()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"

                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["vio_type_description"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewVioTypeDescription = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(
                                    requireContext(), mAutoComTextViewVioTypeDescription!!
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "vio_type", ignoreCase = true
                                )
                            ) {
                                vio_typePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
//                            val printOrder: Double =
//                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
//                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
//                                    )) if(mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains("#"))
//                                    mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety().split("#")[0].toDouble()
//                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
//                                    .toDouble() else "0".toDouble() else "0".toDouble()

                                val printOrder =
                                    mCitationLayout?.getOrNull(iCit)?.fields?.getOrNull(iOff)?.mPrintLayoutOrder.safeDoubleFromLayout()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["vio_type"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mAutoComTextViewVioType = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                Util.setFieldCaps(requireContext(), mAutoComTextViewVioType!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "Cost", ignoreCase = true
                                )
                            ) {
                                costPrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["costPrint"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "total_due_now", ignoreCase = true
                                )
                            ) {
                                total_due_now[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1

                                mPrintOrderMap["total_due_now"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                AutoComTextViewTotalDue = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
//                                AutoComTextViewDueDate45?.setOnClickListener {
//                                    openDataPicker(AutoComTextViewDueDate30)
//                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "pay_at_online", ignoreCase = true
                                )
                            ) {
                                pay_at_online[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                payAtOnline = mCitationLayout!![iCit].fields!![iOff].name!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1

                                mPrintOrderMap["pay_at_online"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                AutoComTextViewTotalDue = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutVoilationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                            } else {
                                try {
                                    latestLayoutViolation++
                                    name[latestLayoutViolation] = CheckTypeOfField(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        linearLayoutVoilationDetails,
                                        mCitationLayout!![iCit].component!!,
                                        requireContext()
                                    )
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                    if (mCitationLayout!![iCit].component.equals("Comments", ignoreCase = true)) {
                        for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                            if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark_1", ignoreCase = true
                                )
                            ) {
                                remark_1Print[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                MaxCharcterLimitForComment =
                                    if (mCitationLayout!![iCit].fields!![iOff].mMaxLength != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mMaxLength
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mMaxLength!!.toInt() else 0 else 0

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["remark1Print"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mEtLocationRemarks = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutInternalNote,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mEtLocationRemarks?.isAllCaps = true
                                mEtLocationRemarks?.inputType =
                                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                                Util.setFieldCaps(requireContext(), mEtLocationRemarks!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark_2", ignoreCase = true
                                )
                            ) {
                                remark_2Print[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["remark2Print"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mEtLocationRemarks1 = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutInternalNote,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mEtLocationRemarks1?.isAllCaps = true
                                mEtLocationRemarks1?.inputType =
                                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                                Util.setFieldCaps(requireContext(), mEtLocationRemarks1!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark_3", ignoreCase = true
                                )
                            ) {
                                remark_3Print[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["remark_3Print"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mEtLocationRemarks2 = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutInternalNote,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mEtLocationRemarks2?.isAllCaps = true
                                mEtLocationRemarks2?.inputType =
                                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                                Util.setFieldCaps(requireContext(), mEtLocationRemarks2!!)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "note_1", ignoreCase = true
                                ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "note_2", ignoreCase = true
                                ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "note_3", ignoreCase = true
                                )
                            ) {
                            } else {
                                try {
                                    latestLayoutComments++
                                    name[latestLayoutComments] = CheckTypeOfField(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        linearLayoutInternalNote,
                                        mCitationLayout!![iCit].component!!,
                                        requireContext()
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    if (mCitationLayout!![iCit].component.equals("Comments", ignoreCase = true)) {
                        for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                            if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "note_1", ignoreCase = true
                                )
                            ) {
                                note_1Print[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()
                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["note1Print"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mEtLocationNotes = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutCitationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mEtLocationNotes?.isAllCaps = true
                                mEtLocationNotes?.inputType =
                                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "note_2", ignoreCase = true
                                )
                            ) {
                                note_2Print[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()
                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["note2Print"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mEtLocationNotes1 = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutCitationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mEtLocationNotes1?.isAllCaps = true
                                mEtLocationNotes1?.inputType =
                                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "note_3", ignoreCase = true
                                )
                            ) {
                                note_3Print[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()
                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["note3Print"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mEtLocationNotes2 = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutCitationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )
                                mEtLocationNotes2?.isAllCaps = true
                                mEtLocationNotes2?.inputType =
                                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark_1", ignoreCase = true
                                ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark_2", ignoreCase = true
                                ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark_3", ignoreCase = true
                                )
                            ) {
                            } else {
                                try {
                                    latestLayoutComments++
                                    name[latestLayoutComments] = CheckTypeOfField(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        linearLayoutCitationDetails,
                                        mCitationLayout!![iCit].component!!,
                                        requireContext()
                                    )
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                }
                if (mCitationLayout!![iCit].component.equals(
                        "Text_field", ignoreCase = true
                    ) || mCitationLayout!![iCit].component.equals(
                        "SELECT_FIELD", ignoreCase = true
                    ) || mCitationLayout!![iCit].component.equals("PARAGRAPH", ignoreCase = true)
                ) {
                    for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "textview", ignoreCase = true
                            ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "dropdown", ignoreCase = true
                            ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "observetime", ignoreCase = true
                            )
                        ) {
                            ObservationTimePrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["ObervationTimePrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                            mCitationLayout!![iCit].fields!![iOff].tag = "textview"
                            //                            mAutoConTextViewObservationTime = ConstructLayoutBuilder.CheckTypeOfField(mCitationLayout.get(iCit).getFields().get(iOff),
//                                    linearLayoutOfficerDetails, mCitationLayout.get(iCit).getComponent(), requireContext());
//                            mAutoConTextViewObservationTime.setVisibility(View.GONE);
                        }
                    }
                }
                if (mCitationLayout!![iCit].component.equals("Time", ignoreCase = true)) {
                    for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "label", ignoreCase = true
                            )
                        ) {
                            TimeFieldPrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["TimeFieldPrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                            mCitationLayout!![iCit].fields!![iOff].tag = "textview"
                            //                            mAutoConTextViewObservationTime = ConstructLayoutBuilder.CheckTypeOfField(mCitationLayout.get(iCit).getFields().get(iOff),
//                                    linearLayoutOfficerDetails, mCitationLayout.get(iCit).getComponent(), requireContext());
//                            mAutoConTextViewObservationTime.setVisibility(View.GONE);
                        }
                    }
                }
                if (mCitationLayout!![iCit].component.equals("Day of Week", ignoreCase = true)) {
                    for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "label", ignoreCase = true
                            )
                        ) {
                            DayOfWeekPrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["DayOfWeekFieldPrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                            mCitationLayout!![iCit].fields!![iOff].tag = "textview"
                            //                            mAutoConTextViewObservationTime = ConstructLayoutBuilder.CheckTypeOfField(mCitationLayout.get(iCit).getFields().get(iOff),
//                                    linearLayoutOfficerDetails, mCitationLayout.get(iCit).getComponent(), requireContext());
//                            mAutoConTextViewObservationTime.setVisibility(View.GONE);
                        }
                    }
                }
                if (mCitationLayout!![iCit].component.equals("BOTTOM_TEXT", ignoreCase = true)) {
                    for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "label", ignoreCase = true
                            )
                        ) {
                            bottomLabelPrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            mPrintOrderMap["BottomLabelPrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont
                            )
                            mCitationLayout!![iCit].fields!![iOff].tag = "textview"
                            //                            mAutoConTextViewObservationTime = ConstructLayoutBuilder.CheckTypeOfField(mCitationLayout.get(iCit).getFields().get(iOff),
//                                    linearLayoutOfficerDetails, mCitationLayout.get(iCit).getComponent(), requireContext());
//                            mAutoConTextViewObservationTime.setVisibility(View.GONE);
                        }
                    }
                }
                if (mCitationLayout!![iCit].component.equals("SINGLE_FIELD", ignoreCase = true)) {
//                    sharedPreference.write(SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT, "")
//                    var mBottomValueForPrint: String? = ""
                    for (iOff in mCitationLayout!![iCit].fields!!.indices) {
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "label", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT,
                                mCitationLayout!![iCit].fields!![iOff].repr
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "header1", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.HEADER_1_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )

                            sharedPreference.write(
                                SharedPrefKey.HEADER_1_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.HEADER_1_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.HEADER_1_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )

                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "header2", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.HEADER_2_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.HEADER_2_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.HEADER_2_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.HEADER_2_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "qr_code", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.QRCODE_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.QRCODE_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )

                            sharedPreference.write(
                                SharedPrefKey.QRCODE_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.QRCODE_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "qr_code_label", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.QRCODE_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "bar_code", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.BAR_CODE_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.BAR_CODE_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )

                            sharedPreference.write(
                                SharedPrefKey.BAR_CODE_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.BAR_CODE_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "50"
                            )
                        }

                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer2", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer3", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer4", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }

                        //Adding Footer 5 to the pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer5", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }

                        //Adding footer 6 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer6", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }

                        //Adding footer 7 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer7", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }

                        //Adding footer 8 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer8", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER8_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }

                        //Adding footer 9 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer9", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER9_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }

                        //Adding footer 10 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "footer10", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.FOOTER10_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }


                        //Adding Line 1 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line1", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_1_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_1_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_1_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_1_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 2 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line2", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_2_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_2_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_2_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_2_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 3 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line3", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_3_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_3_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_3_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_3_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 4 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line4", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_4_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_4_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_4_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_4_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 5 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line5", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_5_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_5_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_5_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_5_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 6 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line6", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_6_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_6_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_6_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_6_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 7 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line7", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_7_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_7_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_7_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_7_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 8 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line8", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_8_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_8_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_8_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_8_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 9 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line9", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_9_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_9_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_9_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_9_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }

                        //Adding Line 10 to pref
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "line10", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LINE_10_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_10_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_10_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.LINE_10_FOR_PRINT_HEIGHT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "1"
                            )
                        }


                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "fee_schedule", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )
                            sharedPreference.write(
                                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_FONT,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "0"
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "code", ignoreCase = true
                            )
                        ) {
                            try {
                                codePrint2010 = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["codePrint2010"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mCitationLayout!![iCit].fields!![iOff].tag = "textview"
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "Hearing_Date", ignoreCase = true
                            )
                        ) {
                            try {
                                hearingDate = mCitationLayout!![iCit].fields!![iOff].print!!
                                val hearingThresholdDayValue =
                                    mCitationLayout!![iCit].fields!![iOff].mCalculatedField.nullSafety()
                                hearingThresholdDay = if (hearingThresholdDayValue.isEmpty()) {
                                    // Case 1: if "save", get integer value from setting file
                                    getSettingFileValuesForHearingDateThresholdDays()
                                } else {
                                    // Case 2: directly parse numeric value
                                    hearingThresholdDayValue.toIntOrNull() ?: 25
                                }

                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["HearingDate"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mCitationLayout!![iCit].fields!![iOff].tag = "textview"
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "Hearing_Description", ignoreCase = true
                            )
                        ) {
                            try {
                                hearingDescription = mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["HearingDescription"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mCitationLayout!![iCit].fields!![iOff].tag = "textview"
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "Officer_Description", ignoreCase = true
                            )
                        ) {
                            try {
                                isOfficerDescription =
                                    mCitationLayout!![iCit].fields!![iOff].print!!
                                val printOrder: Double =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[0].toDouble()
                                    else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .toDouble() else "0".toDouble() else "0".toDouble()

                                val column: Int =
                                    if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                        )
                                    ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                            "#"
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                        .split("#")[1].toInt()
                                    else "1".toInt() else "1".toInt() else "1".toInt()

                                val layoutOrder =
                                    if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                            mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                        )
                                    ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                                val columnSize: Int =
                                    mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                        !TextUtils.isEmpty(it)
                                    }?.toIntOrNull() ?: 1
                                mPrintOrderMap["officerDescription"] = PrintLayoutModel(
                                    printOrder,
                                    column,
                                    layoutOrder,
                                    mCitationLayout!![iCit].fields!![iOff].repr,
                                    mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                    columnSize
                                )
                                mCitationLayout!![iCit].fields!![iOff].tag = "textview"
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "Warning", ignoreCase = true
                            )
                        ) {
                            mWarningMessageForLaMetro =
                                mCitationLayout!![iCit].fields!![iOff].repr.toString()

                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "location_description", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.SM_LOCATION_DESCRIPTION_PRINT,
                                mCitationLayout!![iCit].fields!![iOff].repr.toString()
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "meter_description", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.SM_METER_DESCRIPTION_PRINT,
                                mCitationLayout!![iCit].fields!![iOff].repr.toString()
                            )
                        }

                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "parking_fee", ignoreCase = true
                            )
                        ) {
                            parkingFeePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["parkingFeePrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "citation_fee", ignoreCase = true
                            )
                        ) {
                            citationFeePrint[0] = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["citationFeePrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
                        }
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "mark_time", ignoreCase = true
                            ) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "observetime", ignoreCase = true
                            )
                        ) {
                            ObservationTimePrint = mCitationLayout!![iCit].fields!![iOff].print!!
                            val printOrder: Double =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[0].toDouble()
                                else mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .toDouble() else "0".toDouble() else "0".toDouble()

                            val column: Int =
                                if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder
                                    )
                                ) if (mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder!!.contains(
                                        "#"
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mPrintLayoutOrder.nullSafety()
                                    .split("#")[1].toInt()
                                else "1".toInt() else "1".toInt() else "1".toInt()

                            val layoutOrder =
                                if (mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder != null) if (!TextUtils.isEmpty(
                                        mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder
                                    )
                                ) mCitationLayout!![iCit].fields!![iOff].mFormLayoutOrder else "0" else "0"
                            val columnSize: Int =
                                mCitationLayout?.get(iCit)?.fields?.get(iOff)?.mDisplayColumn?.takeIf {
                                    !TextUtils.isEmpty(it)
                                }?.toIntOrNull() ?: 1
                            mPrintOrderMap["ObervationTimePrint"] = PrintLayoutModel(
                                printOrder,
                                column,
                                layoutOrder,
                                mCitationLayout!![iCit].fields!![iOff].repr,
                                mCitationLayout!![iCit].fields!![iOff].mPositionXYFont,
                                columnSize
                            )
//                            mCitationLayout!![iCit].fields!![iOff].tag = "dropdown"
                            if (mCitationLayout!![iCit].fields!![iOff].tag.equals("dropdown") || mCitationLayout!![iCit].fields!![iOff].tag.equals(
                                    "editview"
                                )
                            ) {
                                mEtMarkTime = CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    linearLayoutCitationDetails,
                                    mCitationLayout!![iCit].component!!,
                                    requireContext()
                                )

                                if (mCitationLayout!![iCit].fields!![iOff].tag.equals("dropdown") || mCitationLayout!![iCit].fields!![iOff].tag.equals(
                                        "editview"
                                    )
                                ) {
                                    mEtMarkTime?.setOnClickListener {
                                        requireActivity().hideSoftKeyboard()
                                        openTimePicker(
                                            mEtMarkTime, requireContext(), Constants.TIME_FORMAT
                                        )
                                    }
                                } else {
                                    if (mEtMarkTime != null) {
                                        mEtMarkTime!!.visibility = View.GONE
                                        mEtMarkTime!!.filters = arrayOf(InputFilter.LengthFilter(4))
                                        mEtMarkTime!!.setInputType(InputType.TYPE_CLASS_NUMBER)
                                    }
                                }
                            }

                        }

                        //check if we got app_logo in citation form from backend or not
                        //If yes, then we will print it on ticket
                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "app_logo", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.APP_LOGO_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.APP_LOGO_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )

                            sharedPreference.write(
                                SharedPrefKey.APP_LOGO_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )

                            sharedPreference.write(
                                SharedPrefKey.APP_LOGO_FOR_PRINT_WIDTH,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "200"
                            )
                        }

                        if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                "lpr_image", ignoreCase = true
                            )
                        ) {
                            sharedPreference.write(
                                SharedPrefKey.LPR_IMAGE_FOR_PRINT,
                                (mCitationLayout!![iCit].fields!![iOff].repr)
                            )
                            sharedPreference.write(
                                SharedPrefKey.LPR_IMAGE_FOR_PRINT_X,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[0]) else "0"
                            )

                            sharedPreference.write(
                                SharedPrefKey.LPR_IMAGE_FOR_PRINT_Y,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[1]) else "0"
                            )

                            sharedPreference.write(
                                SharedPrefKey.LPR_IMAGE_FOR_PRINT_WIDTH,
                                if (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont != null && mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.isNotEmpty()) (mCitationLayout!![iCit].fields!![iOff].mPositionXYFont!!.split(
                                    "#"
                                )[2]) else "300"
                            )
                        }
                    }

                }
            }
        }
    }//mEtLocationNotes.setError(getString(R.string.val_msg_please_enter_note1));//mEtLocationNotes.setError(getString(R.string.val_msg_please_enter_note1));//mAutoComTextViewAbbrCode.setError(getString(R.string.val_msg_please_enter_code));//mAutoComTextViewAbbrCode.setError(getString(R.string.val_msg_please_enter_code));//mEtLocationDescr.setError(getString(R.string.val_msg_please_enter_description));

// Comments layout form validation


// Violation layout form validation
//mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));//mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));//mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));//mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));//mAutoComTextViewStreet.setError(getString(R.string.val_msg_please_enter_street));//mTvOfficerMake.setError(getString(R.string.val_msg_please_enter_make));//mTvZone.setError(getString(R.string.val_msg_please_enter_zone));

// Location layout form validation
//mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));//mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));//mAutoComTextViewVinNum.setError(getString(R.string.val_msg_please_enter_vin_number));
//                                }
//                                        TextUtils.isEmpty(mAutoComTextViewState.getText().toString().trim()) &&//                                if (citationLayout.getFields().get(iOff).isRequired.nullSafety()) {//mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));//mAutoComTextViewState.setError(getString(R.string.val_msg_please_enter_state));//                                            && !TextUtils.isEmpty(mAutoComTextViewState.getText().toString().trim())//mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));//mTvOfficerModel.setError(getString(R.string.val_msg_please_enter_model));
//AppUtils.showKeyboard(LprDetails2Activity.requireContext());
    /*else {
if(!checkDateFormat(mAutoCompleteDate.getText().toString().trim())){
mAutoCompleteDate.requestFocus();
mAutoCompleteDate.setFocusable(true);
//AppUtils.showKeyboard(LprDetails2Activity.requireContext());
LogUtil.printToastMSG(requireContext(), getString(R.string.val_msg_please_enter_valid_expiry_date));
return false;
}
}*/
//mTvOfficerModel.setError(getString(R.string.val_msg_please_enter_model));//mTvOfficerMake.setError(getString(R.string.val_msg_please_enter_make));//mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));

    // Vehicle layout validation
//mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));//mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));//mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));//mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));//mTvOfficerName.setError(getString(R.string.val_msg_please_enter_officer_name));
// Officer layout validation
    fun isFormValid(): Boolean {
        try {
            if (mCitationLayout != null) {
                val mImageCountTotal = bannerList!!.size//mDb?.dbDAO?.getCountImages()
                var mMinImageCount = maxImageCount("MIN_IMAGES")
                if (LogUtil.isEnableAPILogs == true) {
                    mMinImageCount = 0
                }
                for (citationLayout in mCitationLayout!!) {
                    // Officer layout validation
                    if (citationLayout.component.equals("Officer", ignoreCase = true)) {
                        for (iOff in citationLayout.fields!!.indices) {
                            if (citationLayout.fields!![iOff].name.equals(
                                    "officer_name", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvOfficerName?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvOfficerName.setError(getString(R.string.val_msg_please_enter_officer_name));
                                        mTvOfficerName?.requestFocus()
                                        mTvOfficerName?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_officer_name)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "badge_id", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvBadgeId?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                                        mTvBadgeId?.requestFocus()
                                        mTvBadgeId?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_badge_id)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "beat", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvBeat?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                                        mTvBeat?.requestFocus()
                                        mTvBeat?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_beat)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "squad", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvSquad?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                                        mTvSquad?.requestFocus()
                                        mTvSquad?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_squad)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "agency", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvAgency?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                                        mTvAgency?.requestFocus()
                                        mTvAgency?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_agency)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "zone", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvZone?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                                        mTvZone?.requestFocus()
                                        mTvZone?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_zone)
                                        )
                                        return false
                                    }
                                }
                            }
                        }
                    }

                    // Vehicle layout validation
                    if (citationLayout.component.equals("Vehicle", ignoreCase = true)) {
                        for (iOff in citationLayout.fields!!.indices) {
                            if (citationLayout.fields!![iOff].name.equals(
                                    "make", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvOfficerMake?.text.toString().trim()
                                        ) || mSelectedMake == null || mSelectedMake!!.isEmpty()
                                    ) {
                                        //mTvOfficerMake.setError(getString(R.string.val_msg_please_enter_make));
                                        mTvOfficerMake?.requestFocus()
                                        mTvOfficerMake?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_make)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "model", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvOfficerModel?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvOfficerModel.setError(getString(R.string.val_msg_please_enter_model));
                                        if (mModelList != null && mModelList.size > 0) {
                                            mTvOfficerModel?.requestFocus()
                                            mTvOfficerModel?.isFocusable = true

                                            printToastMSGForErrorWarning(
                                                requireContext(),
                                                getString(R.string.val_msg_please_enter_model)
                                            )
                                            return false
                                        }
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "expiry_year", ignoreCase = true
                                ) && mAutoComTextViewVinNum?.text.toString().length < 17
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoCompleteDate?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvOfficerModel.setError(getString(R.string.val_msg_please_enter_model));
                                        mAutoCompleteDate?.requestFocus()
                                        mAutoCompleteDate?.isFocusable = true
                                        //AppUtils.showKeyboard(LprDetails2Activity.requireContext());
                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_expiry_date)
                                        )
                                        return false
                                    }
                                }
                                if (BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true
                                    ) && !ExpiryDateTextWatcher.validateExpiryDate(mAutoCompleteDate!!.text.toString())
                                ) {
                                    printToastMSGForErrorWarning(
                                        requireContext(),
                                        getString(R.string.val_msg_please_enter_valid_expiry_date)
                                    )
                                    return false
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "expiry_month", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoCompleteExpiryMonth?.text.toString().trim()
                                        )
                                    ) {
                                        mAutoCompleteExpiryMonth?.requestFocus()
                                        mAutoCompleteExpiryMonth?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_expiry_date)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "color", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mTvOfficerColor?.text.toString().trim()
                                        ) || mSelectedColor == null || mSelectedColor!!.isEmpty()
                                    ) {
                                        //mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));
                                        mTvOfficerColor?.requestFocus()
                                        mTvOfficerColor?.isFocusable = true

                                        printToastMSGForErrorWarning(
                                            requireContext(),
                                            getString(R.string.val_msg_please_enter_color)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "lp_number", ignoreCase = true
                                )
                            ) {
                                if (BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
                                    ) || BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) || BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                                    )
                                ) {
                                    if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                        if ((TextUtils.isEmpty(
                                                mAutoComTextViewLicensePlate?.text.toString().trim()
                                            ))
                                        ) {
                                            mAutoComTextViewLicensePlate?.requestFocus()
                                            mAutoComTextViewLicensePlate?.isFocusable = true
                                            mAutoComTextViewLicensePlate?.isAllCaps = true

                                            requireContext().toast(
                                                getString(R.string.val_msg_please_enter_lpr_number)
                                            )
                                            return false
                                        }
                                    }
                                } else if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if ((TextUtils.isEmpty(
                                            mAutoComTextViewLicensePlate?.text.toString().trim()
                                        ) && mAutoComTextViewVinNum != null && TextUtils.isEmpty(
                                            mAutoComTextViewVinNum?.text.toString().trim()
                                        )) || mAutoComTextViewVinNum == null && TextUtils.isEmpty(
                                            mAutoComTextViewLicensePlate?.text.toString().trim()
                                        )
                                    ) {

                                        //mAutoComTextViewState.setError(getString(R.string.val_msg_please_enter_state));
                                        mAutoComTextViewLicensePlate?.requestFocus()
                                        mAutoComTextViewLicensePlate?.isFocusable = true
                                        mAutoComTextViewLicensePlate?.isAllCaps = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_lpr_number)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "state", ignoreCase = true
                                )
                            ) {
                                if (mAutoComTextViewVinNum != null && mAutoComTextViewVinNum?.text.toString().length < 17 || mAutoComTextViewVinNum != null && mAutoComTextViewVinNum?.text?.toString()
                                        .equals("Partially Covered") == true
                                ) {
                                    if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                        if (TextUtils.isEmpty(
                                                mAutoComTextViewVinNum?.text.toString().trim()
                                            ) && TextUtils.isEmpty(
                                                mAutoComTextViewState?.text.toString().trim()
                                            ) || mState2DigitCode!!.isEmpty()
                                        ) {
                                            //mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));
                                            mAutoComTextViewState?.requestFocus()
                                            mAutoComTextViewState?.isFocusable = true

                                            requireContext().toast(
                                                getString(R.string.val_msg_please_enter_state)
                                            )
                                            return false
                                        }
                                    }
                                } else if (mAutoComTextViewVinNum == null) {
                                    if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                        mAutoComTextViewState?.requestFocus()
                                        if (TextUtils.isEmpty(
                                                mAutoComTextViewState?.text.toString().trim()
                                            ) || mState2DigitCode!!.isEmpty()
                                        ) {
                                            //mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));
                                            mAutoComTextViewState?.isFocusable = true

                                            requireContext().toast(
                                                getString(R.string.val_msg_please_enter_state)
                                            )
                                            return false
                                        }
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "vin_number", ignoreCase = true
                                )
                            ) {
                                val minL = mAutoComTextViewVinNum?.tag as Int
                                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO)) {

                                    if (mAutoComTextViewVinNum?.text.toString()
                                            .trim().length < minL
                                    ) {
                                        mAutoComTextViewVinNum?.requestFocus()
                                        mAutoComTextViewVinNum?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_vin_number_min).replace(
                                                "[]", minL.toString() + ""
                                            )
                                        )
                                        return false
                                    }
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewLicensePlate?.text.toString().trim()
                                        ) && mAutoComTextViewVinNum?.text.toString()
                                            .trim().length < PPA_SAN_DIEGO_LPR_EMPTY_THEN_VIN_MIN
                                    ) {
                                        mAutoComTextViewVinNum?.requestFocus()
                                        mAutoComTextViewVinNum?.isFocusable = true
                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_vin_number_min).replace(
                                                "[]",
                                                PPA_SAN_DIEGO_LPR_EMPTY_THEN_VIN_MIN.toString() + ""
                                            )
                                        )
                                        return false
                                    }
                                } else {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewLicensePlate?.text.toString().trim()
                                        ) && TextUtils.isEmpty(
                                            mAutoComTextViewVinNum?.text.toString().trim()
                                        ) || (BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
                                        ) && TextUtils.isEmpty(
                                            mAutoComTextViewVinNum?.text.toString().trim()
                                        ))
                                    ) {
                                        mAutoComTextViewVinNum?.requestFocus()
                                        mAutoComTextViewVinNum?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_vin_number)
                                        )
                                        return false
                                    }


                                    if (!TextUtils.isEmpty(
                                            mAutoComTextViewVinNum?.text.toString().trim()
                                        ) && mAutoComTextViewVinNum?.text.toString()
                                            .trim().length < minL
                                    ) {
                                        mAutoComTextViewVinNum?.requestFocus()
                                        mAutoComTextViewVinNum?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_vin_number_min).replace(
                                                "[]", minL.toString() + ""
                                            )
                                        )
                                        return false
                                    }
                                }

                                /*
                             *4. Can't enter vin "Not Visible". The validation should not be there.
                             *   a. When license number is there state and expiry date are required
                             *   b. When only Vin is entered and it is 17 digits State and Expiry date are not mandatory
                             *   c. Copy last 8 should not happen for Glendale & Phli.
                             */
                                val inputTextForVin = mAutoComTextViewVinNum?.text.toString()

                                if (!mApplicationVinList.any { it.direction == inputTextForVin } && !BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true
                                    ) && Util.isVinHaveIOQ(
                                        mAutoComTextViewVinNum?.text.toString()
                                    )) {
                                    mAutoComTextViewVinNum?.requestFocus()
                                    mAutoComTextViewVinNum?.isFocusable = true

                                    requireContext().toast(
                                        getString(R.string.val_msg_vin_number_IOQ_Validation).replace(
                                            "[]", minL.toString() + ""
                                        )
                                    )
                                    return false
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "body_style", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewBodyStyle?.text.toString().trim()
                                        ) || mBodyStyleCodeItem == null || mBodyStyleCodeItem!!.isEmpty()
                                    ) {
                                        //mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));
                                        mAutoComTextViewBodyStyle?.requestFocus()
                                        mAutoComTextViewBodyStyle?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_body_style)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "decal_year", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewDecalYear?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvOfficerColor.setError(getString(R.string.val_msg_please_enter_color));
                                        mAutoComTextViewDecalYear?.requestFocus()
                                        mAutoComTextViewDecalYear?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_decal_year)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "decal_number", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewDecalNum?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvZone.setError(getString(R.string.val_msg_please_enter_zone));
                                        mAutoComTextViewDecalNum?.requestFocus()
                                        mAutoComTextViewDecalNum?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_decal_num)
                                        )
                                        return false
                                    }
                                }
                            }
                        }
                    }

                    // Location layout form validation
                    if (citationLayout.component.equals("Location", ignoreCase = true)) {
                        for (iOff in citationLayout.fields!!.indices) {
                            if (citationLayout.fields!![iOff].name.equals(
                                    "lot", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewLot?.text.toString().trim()
                                        )
                                    ) {
                                        //mTvOfficerMake.setError(getString(R.string.val_msg_please_enter_make));
                                        mAutoComTextViewLot?.requestFocus()
                                        mAutoComTextViewLot?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_lot)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "street", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewStreet?.text.toString().trim()
                                        )
                                    ) {
                                        //mAutoComTextViewStreet.setError(getString(R.string.val_msg_please_enter_street));
                                        mAutoComTextViewStreet?.requestFocus()
                                        mAutoComTextViewStreet?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_street)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "block", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewBlock?.text.toString().trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));
                                        mAutoComTextViewBlock?.requestFocus()
                                        mAutoComTextViewBlock?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_block)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "direction", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewDirection?.text.toString().trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));
                                        mAutoComTextViewDirection?.requestFocus()
                                        mAutoComTextViewDirection?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_direction)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "side", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewSide?.text.toString().trim()
                                        ) || mSideOfStreet2DigitCode == null || mSideOfStreet2DigitCode!!.isEmpty()
                                    ) {
                                        //mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));
                                        mAutoComTextViewSide?.requestFocus()
                                        mAutoComTextViewSide?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_side)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "meter", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety() || isMeterReqiredViolation) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewMeterName?.text.toString().trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));
                                        mAutoComTextViewMeterName?.requestFocus()
                                        mAutoComTextViewMeterName?.isFocusable = true

                                        val newText =
                                            getString(R.string.val_msg_please_enter_meter_name).replace(
                                                "[]", citationLayout.fields!![iOff].repr!!
                                            )
                                        requireContext().toast(newText)
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "zone", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety() || mZoneMandatoryForViolationCode.equals(
                                        "YES"
                                    )
                                ) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewZone?.text.toString().trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));
                                        mAutoComTextViewZone?.requestFocus()
                                        mAutoComTextViewZone?.isFocusable = true
//                                        mAutoComTextViewZone?.setError("")

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_zone)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields?.get(iOff)?.name.equals(
                                    "pbc_zone", true
                                ) || citationLayout.fields?.get(iOff)?.name.equals(
                                    "city_zone", true
                                )
                            ) {
                                if (citationLayout.fields?.get(iOff)?.isRequired.nullSafety() || isPBCReqiredViolation) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewPBCZone?.text.toString().trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));
                                        mAutoComTextViewPBCZone?.requestFocus()
                                        mAutoComTextViewPBCZone?.isFocusable = true
                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_zone)
                                        )
                                        return false
                                    }
                                }
                            }
                        }
                    }


                    // Violation layout form validation
                    if (citationLayout.component.equals("Violation", ignoreCase = true)) {
                        for (iOff in citationLayout.fields!!.indices) {
                            if (citationLayout.fields!![iOff].name.equals(
                                    "violation", ignoreCase = true
                                ) || citationLayout.fields!![iOff].name.equals(
                                    "violation_abbr", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewAbbrCode?.text.toString().trim()
                                        )
                                    ) {
                                        mAutoComTextViewAbbrCode?.requestFocus()
                                        mAutoComTextViewAbbrCode?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_abbr_code)
                                        )
                                        return false
                                    }
                                    if (mAutoComTextViewAbbrCode?.text!!.toString().equals(
                                            "METER EXPIRED", ignoreCase = true
                                        ) && TextUtils.isEmpty(
                                            mAutoComTextViewPBCZone?.text.toString().trim()
                                        )
                                    ) {
                                        mAutoComTextViewZone?.requestFocus()
                                        mAutoComTextViewZone?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_zone)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "code", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewCode?.text.toString().trim()
                                        )
                                    ) {
                                        mAutoComTextViewCode?.requestFocus()
                                        mAutoComTextViewCode?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_code)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "description", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mEtLocationDescr?.text.toString().trim()
                                        )
                                    ) {
                                        mEtLocationDescr?.requestFocus()
                                        mEtLocationDescr?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_description)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "fine", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewAmount?.text.toString().trim()
                                        )
                                    ) {
                                        mAutoComTextViewAmount?.requestFocus()
                                        mAutoComTextViewAmount?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_fine)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "late_fine", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewAmountDueDate?.text.toString().trim()
                                        )
                                    ) {
                                        mAutoComTextViewAmountDueDate?.requestFocus()
                                        mAutoComTextViewAmountDueDate?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_late_fine)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "due_15_days", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            AutoComTextViewDueDate?.text.toString().trim()
                                        )
                                    ) {
                                        AutoComTextViewDueDate?.requestFocus()
                                        AutoComTextViewDueDate?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_due_15_days)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "due_30_days", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            AutoComTextViewDueDate30?.text.toString().trim()
                                        )
                                    ) {
                                        AutoComTextViewDueDate30?.requestFocus()
                                        AutoComTextViewDueDate30?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_due_30_days)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "due_45_days", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            AutoComTextViewDueDate45?.text.toString().trim()
                                        )
                                    ) {
                                        AutoComTextViewDueDate45?.requestFocus()
                                        AutoComTextViewDueDate45?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_due_45_days)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "vio_type_code", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewVioTypeCode?.text.toString().trim()
                                        )
                                    ) {
                                        mAutoComTextViewVioTypeCode?.requestFocus()
                                        mAutoComTextViewVioTypeCode?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_code)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "vio_type_description", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewVioTypeDescription?.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        mAutoComTextViewVioTypeDescription?.requestFocus()
                                        mAutoComTextViewVioTypeDescription?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_description)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "vio_type", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewVioType?.text.toString().trim()
                                        )
                                    ) {
                                        mAutoComTextViewVioType?.requestFocus()
                                        mAutoComTextViewVioType?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_vio_type)
                                        )
                                        return false
                                    }
                                }
                            }
                        }
                    }

                    // Comments layout form validation
                    if (citationLayout.component.equals("Comments", ignoreCase = true)) {
                        for (iOff in citationLayout.fields!!.indices) {
                            if (citationLayout.fields!![iOff].name.equals(
                                    "remark_1", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mEtLocationRemarks?.text.toString().trim()
                                        )
                                    ) {
                                        mEtLocationRemarks?.requestFocus()
                                        mEtLocationRemarks?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_remark1)
                                        )
                                        return false
                                    }
                                }
                                if (MaxCharcterLimitForComment != 0 && mEtLocationRemarks?.text.toString()
                                        .trim().length > MaxCharcterLimitForComment
                                ) {
                                    requireContext().toast(
                                        getString(R.string.val_msg_please_enter_remark_max_limit)
                                    )
                                    return false
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "remark_2", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mEtLocationRemarks1?.text.toString().trim()
                                        )
                                    ) {
                                        mEtLocationRemarks1?.requestFocus()
                                        mEtLocationRemarks1?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_remark2)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "note_1", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mEtLocationNotes?.text.toString().trim()
                                        )
                                    ) {
                                        //mEtLocationNotes.setError(getString(R.string.val_msg_please_enter_note1));
                                        mEtLocationNotes?.requestFocus()
                                        mEtLocationNotes?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_note1)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "note_2", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mEtLocationNotes1?.text.toString().trim()
                                        )
                                    ) {
                                        //mEtLocationNotes.setError(getString(R.string.val_msg_please_enter_note1));
                                        mEtLocationNotes1?.requestFocus()
                                        mEtLocationNotes1?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_note2)
                                        )
                                        return false
                                    }
                                }
                            } else if (citationLayout.fields!![iOff].name.equals(
                                    "note_3", ignoreCase = true
                                )
                            ) {
                                if (citationLayout.fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mEtLocationNotes2?.text.toString().trim()
                                        )
                                    ) {
                                        //mEtLocationNotes.setError(getString(R.string.val_msg_please_enter_note1));
                                        mEtLocationNotes2?.requestFocus()
                                        mEtLocationNotes2?.isFocusable = true

                                        requireContext().toast(
                                            getString(R.string.val_msg_please_enter_note3)
                                        )
                                        return false
                                    }
                                }
                            }
                        }
                    }
                }
                if (mImageCountTotal.nullSafety() < mMinImageCount.nullSafety()) {
                    LogUtil.printToastMSGForErrorWarning(
                        requireContext(), getString(R.string.msg_min_image).replace(
                            "#", mMinImageCount.nullSafety().toString() + ""
                        )
                    )
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewState?.text.toString().trim()
                    )
                ) {
                    mState2DigitCode = ""
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.scr_btn_no_payment),
                    message = mLastSecondCheckMessage.nullSafety(),
                    positiveButtonText = getString(R.string.button_text_ok),
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
        return true
    }

    override fun onDestroy() {
        linearLayoutOfficerDetails.removeAllViews()
        linearLayoutVehicleDetails.removeAllViews()
        linearLayoutLocationDetails.removeAllViews()
        linearLayoutVoilationDetails.removeAllViews()
        linearLayoutInternalNote.removeAllViews()
        linearLayoutCitationDetails.removeAllViews()
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CAMERA = 0
        private const val REQUEST_CAMERA2 = 1
        private const val PERMISSION_REQUEST_CODE = 2
    }

    fun isDefaultStateAndNumberPlateFormatValidation() {
        if (mAutoComTextViewState?.text.toString()
                .equals(mDefaultStateItem) && !plateNumberFormatCheck() && isCheckNumberPlateFormat.equals(
                "format", ignoreCase = true
            )
        ) {

            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = "LPR Number Format Check",
                message = getString(R.string.is_default_state_plate_validation),
                icon = R.drawable.icon_warning,
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_cancel),
                listener = object : AlertDialogListener {
                    override fun onPositiveButtonClicked() {
                        previewButtonClicked()
                    }
                })
        } else {
            previewButtonClicked()
        }
    }

    fun plateNumberFormatCheck(): Boolean {
        try {
            val mRegex = AppUtils.getRegexFromSetting("LICENSE_PLATE_FORMAT", settingsList!!)
            val s = mAutoComTextViewLicensePlate!!.text.toString()
            val p = Pattern.compile(mRegex)
            val m = p.matcher(s)
            val b = m.matches()
            val p1 = Pattern.compile(mRegex) //EPC8201
            val m1 = p1.matcher(s)
            val b1 = m1.matches()
            if (b) {
                return true
            } else if (b1) {
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }


//    class MonthYearPickerDialog : DialogFragment() {
//        private var listener: OnDateSetListener? = null
//        fun setListener(listener: OnDateSetListener?) {
//            this.listener = listener
//        }
//
//        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//            val builder = AlertDialog.Builder(requireActivity())
//            // Get the layout inflater
//            val inflater = requireActivity().layoutInflater
//            val cal = Calendar.getInstance()
//            val dialog: View = inflater.inflate(R.layout.date_picker_dialog, null)
//            val monthPicker = dialog.findViewById<View>(R.id.picker_month) as NumberPicker
//            val yearPicker = dialog.findViewById<View>(R.id.picker_year) as NumberPicker
//            monthPicker.minValue = 0
//            monthPicker.maxValue = 12
//            monthPicker.value = cal[Calendar.MONTH]
//
//            val year = cal[Calendar.YEAR]
//            yearPicker.minValue = year - YEARS_IN_PAST
//            yearPicker.maxValue = year + YEARS_IN_FUTURE
//            yearPicker.value = year
//            builder.setView(dialog) // Add action buttons
//                    .setPositiveButton(R.string.alt_lbl_OK, DialogInterface.OnClickListener { dialog, id -> listener!!.onDateSet(null, yearPicker.value, monthPicker.value, 0) })
//                    .setNegativeButton(R.string.scr_btn_cancel, DialogInterface.OnClickListener { dialog, id -> this@MonthYearPickerDialog.dialog!!.cancel() })
//            return builder.create()
//        }
//    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(requireContext(), "landscape", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(requireContext(), "portrait", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImagesNameList(imageList: MutableList<CitationImagesModel?>?): Array<String?> {
        val imageNameList = arrayOfNulls<String>(imageList!!.size)

        imageList!!.forEachIndexed { index, scanDataModel ->
            imageNameList.set(
                index, getCitaitonImageFormat(mTvTicketNumber!!.text.toString(), index)
            )
        }

        return imageNameList
    }

    private fun createImageMultipart(imageList: MutableList<CitationImagesModel?>?): List<MultipartBody.Part?> {
        val imageMultipartList = ArrayList<MultipartBody.Part?>()

        imageList!!.forEach {
            val tempFile: File = File(it!!.citationImage.nullSafety())
            val requestFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), tempFile!!)
            val files = MultipartBody.Part.createFormData(
                "files", tempFile.name, requestFile
            )
            imageMultipartList.add(files)
        }

        return imageMultipartList
    }

    /**
     * When edit citation then check all images already uploaded then no
     * need to call upload API again
     */
    private fun callBulkImageUpload() {
        if (mCitationImagesLinks!!.size != bannerList?.size) {
            if (mCitationImagesLinks != null && mCitationImagesLinks.size > 1) {
                mCitationImagesLinks.clear()
            }
            if (isImageAsBase64String) {
//                bannerList!!.forEach {
//                    mCitationImagesLinks.add(it.toString())
//                }
            } else {
                mTimingImages!!.forEach {
                    mCitationImagesLinks.add(it)
                }
            }


            var bannerListWithoutHttps: MutableList<CitationImagesModel?>? = ArrayList()
            bannerList!!.forEach {
                if (it!!.citationImage.nullSafety()
                        .contains("https", ignoreCase = true) || it!!.citationImage.nullSafety()
                        .startsWith("gs://")
                ) {
//                mCitationImagesLinks.add(it!!.citationImage.toString())
                } else {
                    bannerListWithoutHttps!!.add(it)
                }
            }

            // Use
            val handler = Handler()
            handler.postDelayed({
                if (NetworkCheck.isInternetAvailable(requireContext())) {
                    val validImages = bannerListWithoutHttps!!.filter {
                        val file = File(it?.citationImage.nullSafety())
                        isValidFileToUpload(file!!.absolutePath)
                    }.toMutableList()

                    if (validImages!!.size > 0) {
                        val uploadType = API_CONSTANT_DOWNLOAD_TYPE_CITATION_IMAGES.toRequestBody("text/plain".toMediaTypeOrNull())

                        val mRequestBodyImages = ObjectMapperProvider.instance.writeValueAsString(
                            MultipartUtils.createImagesNameList(
                                requireContext(),
                                mTvTicketNumber.text.toString(),
                                false,
                                "---------Citation Image Upload--------",
                                bannerList
                            )
                        ).toRequestBody("text/plain".toMediaTypeOrNull())

                        citationFormScreenViewModel.callUploadAllImagesInBulkAPI(
                            data = mRequestBodyImages,
                            uploadType = uploadType,
                            files = MultipartUtils.getImageMultipart(isStatusCheck = false, imageList = bannerList)
                        )
                    } else {
                        movePreviewScreen()
                    }

                } else {
                    requireContext().toast(
                        getString(R.string.err_msg_connection_was_refused)
                    )
                }
            }, 300)

        } else {
            movePreviewScreen()

        }
    }

    private fun movePreviewScreen() {
        try {
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
            ) {
                sharedPreference.write(
                    SharedPrefKey.LOCK_GEO_SAVE_ADDRESS,
                    mAutoComTextViewBlock?.text.toString()
                        .trim() + "#" + mAutoComTextViewStreet?.text.toString().trim() + ", A"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        sharedPreference.write(
            SharedPrefKey.LAST_SECOND_CHECK, isCallLastSecondCheckAPI
        )

        val mLatestCitation = mFetched!![0]
        saveCitationIssurance(mLatestCitation)

        val bundle = Bundle().apply {
            putString("booklet_id", mLatestCitation!!.citationBooklet)
            putString("btn_action", mTicketActionButtonEvent)
            putStringArrayList("Citation_Images_Link", mCitationImagesLinks as ArrayList<String>)
        }


        nav.safeNavigate(
            R.id.action_citationFormScreenFragment_to_citationPreviewScreenFragment, bundle
        )
    }

    private fun saveUploadImagesInDataBase(imagePath: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val exists = citationFormScreenViewModel.isImagePathExists(imagePath)
                if (exists == 0) {
                    unUploadFacsimileImage.imagePath = imagePath
                    unUploadFacsimileImage.imageLink = ""
                    unUploadFacsimileImage.lprNumber =
                        if (mAutoComTextViewLicensePlate!!.text.toString()
                                .isNotEmpty()
                        ) mAutoComTextViewLicensePlate!!.text.toString() else mAutoComTextViewVinNum!!.text.toString()
                    unUploadFacsimileImage.dateTime =
                        SimpleDateFormat("HHmmssSSS", Locale.US).format(Date()).toLong()
                    unUploadFacsimileImage.ticketNumberText = mTvTicketNumber!!.text.toString()
                    unUploadFacsimileImage.status = 0
                    unUploadFacsimileImage.imageCount = bannerList!!.size
                    unUploadFacsimileImage.uploadedCitationId = ""
                    unUploadFacsimileImage.imageType = "CameraImage"

                    citationFormScreenViewModel.insertFacsimileImageObject(unUploadFacsimileImage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun moveTOPreviewScreen() {

        try {
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
            ) {
                sharedPreference.write(
                    SharedPrefKey.LOCK_GEO_SAVE_ADDRESS,
                    mAutoComTextViewBlock?.text.toString()
                        .trim() + "#" + mAutoComTextViewStreet?.text.toString().trim() + ", A"
                )
            }


            sharedPreference.write(
                SharedPrefKey.LAST_SECOND_CHECK, isCallLastSecondCheckAPI
            )
            //** check before calling api  *//*
            val mLatestCitation = mFetched!![0]
            saveCitationIssurance(mLatestCitation)

            val bundle = Bundle().apply {
                putString("booklet_id", mLatestCitation!!.citationBooklet)
                putString("btn_action", mTicketActionButtonEvent)
                putStringArrayList(
                    "Citation_Images_Link", mCitationImagesLinks as ArrayList<String>
                )
            }

            nav.safeNavigate(
                R.id.action_citationFormScreenFragment_to_citationPreviewScreenFragment, bundle
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun getStateAndCamera2Setting() {
        try {
            settingsList = mainActivityViewModel.getSettingsListFromDataSet()
            if (settingsList != null && settingsList!!.size > 0) {
                for (i in settingsList!!.indices) {
                    if (settingsList!![i].type.equals(
                            "DEFAULT_STATE", ignoreCase = true
                        )
                    ) {
                        try {
                            if (mStateItem.nullSafety().isEmpty()) {
                                mStateItem = settingsList!![i].mValue.nullSafety()
                                mDefaultStateItem = settingsList!![i].mValue.nullSafety()
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (settingsList!![i].type.equals(
                            "IS_CAMERA2_ENABLE", ignoreCase = true
                        ) && settingsList!![i].mValue.equals("YES")
                    ) {
                        try {
                            mCamera2Setting = "YES"
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (settingsList!![i].type.equals(
                            "FULL_VIN_COPY8_LPR_EMPTY_STATE", ignoreCase = true
                        ) && settingsList!![i].mValue.equals("YES")
                    ) {
                        try {
                            mSendStateEmptyForFullVinNumberAndCopyLast8DigitVinForLpr = "YES"
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var vigilantImageUploadCount = 0

    inner class DownloadingVigilantBitmapFromUrl : AsyncTask<String?, Int?, String?>() {
        public override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg url: String?): String? {
            if (!TextUtils.isEmpty(url[0])) {
                try {
//                    val mydir = File(
//                            Environment.getExternalStorageDirectory().absolutePath,
//                            Constants.FILE_NAME + Constants.CAMERA
//                    )
                    val mydir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Constants.FILE_NAME + Constants.CAMERA
                    )
                    mydir.deleteRecursively()
                    if (!mydir.exists()) {
                        mydir.mkdirs()
                    }

                    val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
                    val fname = "Image_" + timeStamp + "_capture.jpg"

                    val file = File(mydir.absolutePath, fname)
                    if (file.exists()) file.delete()
                    val manager =
                        requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val downloadUri = Uri.parse(url[0])
                    val request = DownloadManager.Request(downloadUri)
                    request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                    ).setAllowedOverRoaming(false).setTitle("Downloading")
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            Constants.FILE_NAME + Constants.CAMERA + File.separator + fname
                        )
//                            .setDestinationInExternalPublicDir(
//                                    Constants.FILE_NAME + "" + Constants.CAMERA,
//                                    fname)
                    manager.enqueue(request)
                    MediaScannerConnection.scanFile(
                        requireContext(), arrayOf<String>(file.toString()), null
                    ) { path, uri -> }
                    return mydir.absolutePath + File.separator + fname
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                return ""
            }
            return ""
        }

        public override fun onPostExecute(s: String?) {
            try {
                if (s != null && s.length > 6) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        vigilantImageUploadCount++
                        val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
                        //save image to db
                        val pathDb = s
                        val mImage = CitationImagesModel()
                        mImage.status = 0
                        mImage.citationImage = pathDb
                        mImage.id = id.toInt() + counter
                        citationFormScreenViewModel.insertCitationImage(mImage)
                        counter++
                        if (mTimingImages!!.size < vigilantImageUploadCount) {
                            setCameraImages()
                        }
                    }
                }
            } catch (e: Exception) {
                logD("error mesg", e.message.nullSafety())
            }
            super.onPostExecute(s)
        }
    }

    //dialog for signature view
    private fun setSignatureView() {
        // custom dialog
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_signature)
        dialog.setTitle("Title...")

        // set the custom dialog components - text, image and button
        val signatureView = dialog.findViewById<View>(R.id.signature_view) as SignaturePad
        val clear = dialog.findViewById<View>(R.id.clear) as Button
        val save = dialog.findViewById<View>(R.id.save) as Button
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        clear.setOnClickListener { signatureView.clear() }
        save.setOnClickListener {
            dialog.dismiss()
            imageViewSignature.setImageBitmap(signatureView.getSignatureBitmap())
            SaveSignature(signatureView.getSignatureBitmap())
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    //TODO will add comments later
    private fun SaveSignature(finalBitmap: Bitmap) {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        myDir.mkdirs()
//        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(myDir, getSignatureFileNameWithExt())
        if (file.exists()) file.delete()
        try {
            //new ImageCompression(requireContext(),file.getAbsolutePath()).execute(finalBitmap);
            val out = FileOutputStream(file)
            //finalBitmap = Bitmap.createScaledBitmap(finalBitmap,(int)1080/2,(int)1920/2, true);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * only get status 1 citation which is fail by API
     * 2 Only preview screen citation
     */
    inner class decodeBase64StringToBitmapAsyn : AsyncTask<String?, Int?, Bitmap?>() {
        override fun doInBackground(vararg voids: String?): Bitmap? {
            try {
                return decodeBase64StringToBitmap(voids[0].toString())
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        @SuppressLint("WrongThread")
        override fun onPostExecute(result: Bitmap?) {
            try {
                result?.let {
                    viewLifecycleOwner.lifecycleScope.launch {
                        SaveImageMMBase64(result)

                    }

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun decodeBase64StringToBitmap(encodedString: String): Bitmap {
        val imageBytes = android.util.Base64.decode(encodedString, android.util.Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        return decodedImage
    }

    fun OnVinCamera(view: View) {
        val intent = Intent(requireContext(), NewOcrActivity::class.java)
        ocrActivityLauncher.launch(intent)
    }

    private fun ClearFileds() {
        viewLifecycleOwner.lifecycleScope.launch {

            if (mAutoComTextViewBodyStyle != null) mAutoComTextViewBodyStyle!!.setText("")
            if (mAutoComTextViewLot != null) mAutoComTextViewLot!!.setText("")
            if (mAutoComTextViewAmount != null) mAutoComTextViewAmount!!.setText("")
            if (mAutoComTextViewAmountDueDate != null) mAutoComTextViewAmountDueDate!!.setText(
                ""
            )
            if (mAutoComTextViewAbbrCode != null) mAutoComTextViewAbbrCode!!.setText("")
            if (mAutoComTextViewBlock != null) mAutoComTextViewBlock!!.setText("")
            if (mAutoComTextViewCode != null) mAutoComTextViewCode!!.setText("")
            if (mAutoComTextViewDirection != null) mAutoComTextViewDirection!!.setText("")
            if (mAutoComTextViewDecalNum != null) mAutoComTextViewDecalNum!!.setText("")
            if (mAutoComTextViewDecalYear != null) mAutoComTextViewDecalYear!!.setText("")
            if (AutoComTextViewDueDate != null) AutoComTextViewDueDate!!.setText("")
            if (AutoComTextViewDueDate30 != null) AutoComTextViewDueDate30!!.setText("")
            if (AutoComTextViewDueDate45 != null) AutoComTextViewDueDate45!!.setText("")
            if (mEtLocationDescr != null) mEtLocationDescr!!.setText("")
            if (mAutoComTextViewLocation != null) mAutoComTextViewLocation!!.setText("")
            if (mAutoComTextViewMeterName != null) mAutoComTextViewMeterName!!.setText("")
            if (mAutoComTextViewPBCZone != null) mAutoComTextViewPBCZone!!.setText("")
            if (mAutoComTextViewZone != null) mAutoComTextViewZone!!.setText("")
            if (mAutoComTextViewSide != null) mAutoComTextViewSide!!.setText("")
            if (mAutoComTextViewStreet != null) mAutoComTextViewStreet!!.setText("")
            if (mAutoComTextViewVinNum != null) mAutoComTextViewVinNum!!.setText("")
            if (mTvOfficerMake != null) mTvOfficerMake!!.setText("")
            if (mTvOfficerModel != null) mTvOfficerModel!!.setText("")
            if (mTvOfficerColor != null) mTvOfficerColor!!.setText("")
            if (mEtLocationRemarks != null) mEtLocationRemarks!!.setText("")
            if (mEtLocationRemarks1 != null) mEtLocationRemarks1!!.setText("")
            if (mEtLocationRemarks2 != null) mEtLocationRemarks2!!.setText("")
            if (mEtLocationNotes != null) mEtLocationNotes!!.setText("")
            if (mEtLocationNotes1 != null) mEtLocationNotes1!!.setText("")
            if (mEtLocationNotes2 != null) mEtLocationNotes2!!.setText("")

            if (mAutoComTextViewVioTypeCode != null) mAutoComTextViewVioTypeCode!!.setText("")
            if (mAutoComTextViewVioTypeDescription != null) mAutoComTextViewVioTypeDescription!!.setText(
                ""
            )
            if (mAutoComTextViewVioType != null) mAutoComTextViewVioType!!.setText("")
        }
    }

    private fun mLockLotBasedOnAgencyKansasCity() {
        try {
            viewLifecycleOwner.lifecycleScope.launch {
//                if(mTvAgency!=null && mTvAgency!!.text!!.toString().equals("KCI TCO",ignoreCase = true)||
//                    mTvAgency!=null && mTvAgency!!.text!!.toString().equals("OFF STREET",ignoreCase = true)||
//                    mTvAgency!=null && mTvAgency!!.text!!.toString().equals("TCO")) {
                if (sharedPreference.read(
                        SharedPrefKey.LOCK_LOCATION_BASED_ON_AGENCY, false
                    ) && mAutoComTextViewLot != null
                ) {
                    mLotItem = "8502000"
                    if (mTvAgency != null && mTvAgency!!.text!!.toString()
                            .equals("OFF STREET", ignoreCase = true)
                    ) {
                        mLotItem = "850107"
                    }


                    mAutoComTextViewLot!!.isClickable = false
                    mAutoComTextViewLot!!.setOnClickListener(null)
                    mAutoComTextViewLot!!.dismissDropDown()
                    mAutoComTextViewLot!!.setDropDownHeight(0)
                    mAutoComTextViewLot!!.setFocusable(false)

                    mAutoComTextViewBlock!!.isClickable = false
                    mAutoComTextViewBlock!!.setOnClickListener(null)
                    mAutoComTextViewBlock!!.dismissDropDown()
                    mAutoComTextViewBlock!!.setDropDownHeight(0)
                    mAutoComTextViewBlock!!.setFocusable(false)

                    mAutoComTextViewStreet!!.isClickable = false
                    mAutoComTextViewStreet!!.setOnClickListener(null)
                    mAutoComTextViewStreet!!.dismissDropDown()
                    mAutoComTextViewStreet!!.setDropDownHeight(0)
                    mAutoComTextViewStreet!!.setFocusable(false)

                    viewLifecycleOwner.lifecycleScope.async {
                        var mApplicationList = mainActivityViewModel.getViolationListFromDataSet()
                        mApplicationList = mApplicationList?.filter { it.mIsVisible != 0 }
                        if (mApplicationList != null) {
                            val violationList: MutableList<DatasetResponse> = ArrayList()
                            for (violation in mApplicationList) {
                                if (violation.mZoneId != null && violation.mZoneId.equals(
                                        mTvAgency!!.text!!.toString(), true
                                    ) || violation.mZoneId != null && violation.mZoneId!!.toString()
                                        .contains("#") && violation.mZoneId.toString()
                                        .split("#")[0].equals(
                                        mTvAgency!!.text!!.toString(), true
                                    ) || violation.mZoneId != null && violation.mZoneId!!.toString()
                                        .contains("#") && violation.mZoneId.toString()
                                        .split("#")[1].equals(
                                        mTvAgency!!.text!!.toString(), true
                                    ) || violation.mZoneId != null && violation.mZoneId!!.toString()
                                        .contains("#") && violation.mZoneId.toString()
                                        .split("#").size > 2 && violation.mZoneId.toString()
                                        .split("#")[2].equals(
                                        mTvAgency!!.text!!.toString(), true
                                    )
                                ) {
                                    violationList.add(violation)
                                }
                            }
                            setDropdownAbbrCode(violationList)
                            setDropdownLot()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun isDataSetListEmpty() {
        lifecycleScope.launch {
            delay(1000L) // Wait for 2 seconds
            /**
             * Checks if the violation list is empty by calling [DataBaseUtil.getViolationListEmpty].
             * If the list is empty, it also clears the related dataset tables.
             * and refill again
             */

            if (DataBaseUtil.isViolationListEmpty()) {
                mainActivityViewModel.deleteDatasetAndActivityTables(isManual = false)

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    cancelable = false,
                    title = getString(R.string.warnings_lbl_dataset_empty_header),
                    message = getString(R.string.warnings_lbl_dataset_empty_message),
                    positiveButtonText = getString(R.string.button_text_ok),
                    negativeButtonText = getString(R.string.button_text_cancel),
                    listener = object : AlertDialogListener {
                        override fun onPositiveButtonClicked() {
                            lifecycleScope.launch {
                                mainActivityViewModel.sendActionToMain(MainActivityAction.EventProcessLogout)
                            }
                        }
                    })
            }
        }
    }

    private fun consumeResponse(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(context = requireContext())
            }

            is NewApiResponse.Success -> {
                when (newApiResponse.apiNameTag) {
                    API_TAG_NAME_GET_LAST_SECOND_CHECK -> {
                        handleLastSecondCheckResponse(newApiResponse.data as JsonNode)
                    }

                    API_TAG_NAME_DOWNLOAD_BITMAP -> {
                        handleDownloadBitmapResponse(newApiResponse.data as JsonNode)
                    }
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


    private fun consumeResponseForImageUpload(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(context = requireContext())
            }

            is NewApiResponse.Success -> {
                DialogUtil.hideLoader()
                try {
                    val response = ObjectMapperProvider.fromJson(
                        (newApiResponse.data as JsonNode).toString(),
                        ScannedImageUploadResponse::class.java
                    )


                    when (response.status) {
                        true -> {
                            try {
                                /** TESTING PURPOSE NEED TO REMOVE WHEN PROVIDE THE BUILD
                                If Image upload api return error then save all images in DB and upload it in baseactivity
                                 */
                                /* if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                                 ignoreCase = true) && sharedPreference.read(SharedPrefKey.USER_NAME, "").equals("sbreyer")&&
                                     LogUtil.isEnableAPILogs) {
                                 bannerList!!.forEach {
                                     if (it!!.citationImage.nullSafety().contains("https", ignoreCase = true)
                                         || it!!.citationImage.nullSafety().startsWith("gs://")) {
                                     } else {
                                         val imagePath = it!!.citationImage.nullSafety("")
                                         saveUploadImagesInDataBase(imagePath)
                                     }
                                 }
                                 }*/
                                if(response!!.data!!.size>0) {
                                    mCitationImagesLinks.addAll(response!!.data!!.get(0)!!.response!!.links)
                                }
                                moveTOPreviewScreen()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        else -> {
                            requireContext().toast(
                                message = getString(R.string.wrn_lbl_image_upload_service)
                            )
                            /**
                            If Image upload api return error then save all images in DB and upload it in baseactivity
                             */
                            bannerList!!.forEach {
                                if (it!!.citationImage.nullSafety().contains("https", ignoreCase = true)
                                    || it!!.citationImage.nullSafety().startsWith("gs://")) {
                                } else {
                                    val imagePath = it!!.citationImage.nullSafety("")
                                    saveUploadImagesInDataBase(imagePath)
                                }
                            }
                            moveTOPreviewScreen()
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
                    positiveButtonText = getString(R.string.button_text_ok))

            }

            is NewApiResponse.NetworkError -> {
                DialogUtil.hideLoader()

                /**
                If Image upload api return error then save all images in DB and upload it in baseactivity
                 */
                bannerList!!.forEach {
                    if (it!!.citationImage.nullSafety().contains("https", ignoreCase = true)
                        || it!!.citationImage.nullSafety().startsWith("gs://")) {
                    } else {
                        val imagePath = it!!.citationImage.nullSafety("")
                        saveUploadImagesInDataBase(imagePath)
                    }
                }

                moveTOPreviewScreen()
                requireContext().toast(message = getString(
                    R.string.error_desc_network_error,
                    newApiResponse.exception.message.nullSafety(getString(R.string.error_desc_something_went_wrong))
                ))
            }

            is NewApiResponse.UnknownError -> {
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

    private fun handleLastSecondCheckResponse(jsonNodeData: JsonNode) {
        //Nothing to handle for now
    }

    private fun handleDownloadBitmapResponse(jsonNodeData: JsonNode) {
        try {
            val responseModel = ObjectMapperProvider.fromJson(
                jsonNodeData.toString(),
                DownloadBitmapResponse::class.java
            )

            if (responseModel != null && responseModel.isStatus) {
                if (responseModel.metadata!![0].url!!.length > 0) {
                    setServerTimingImageOnUI(responseModel.metadata)
                    if(mBannerAdapter!=null)
                    {
                        mBannerAdapter?.notifyDataSetChanged()
                    }
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
}