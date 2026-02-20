package com.parkloyalty.lpr.scan.ui.check_setup.activity


//import androidmads.library.qrgenearator.QRGContents
//import androidmads.library.qrgenearator.QRGEncoder
import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.type.LatLng
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.Writer
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databasetable.ActivityImageTable
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.addTimeLimitEnforcementTimeFromSharedPreferenceInCreateTicket
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForGenerateFacsimileImageMethodForRecursiveCall
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForLoadBitmapPreviewActivityForWhiteImageForPrint
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForPreviewActivityCitationAdapterGrid_3
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForPreviewActivityForQRCode
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForPreviewActivityForTopMessage
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForPreviewActivityLoadBitmapForSaveOCRImage
import com.parkloyalty.lpr.scan.extensions.disableButton
import com.parkloyalty.lpr.scan.extensions.enableButton
import com.parkloyalty.lpr.scan.extensions.getFileNameWithExtension
import com.parkloyalty.lpr.scan.extensions.getSettingFileValuesForCMDPrinting
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.invisibleView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.setXYforPrintBarCode
import com.parkloyalty.lpr.scan.extensions.showToast
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_OCR_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_HEADER_FOOTER_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SAVEPRINTBITMAPDELAYTIME
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_OFFICER_NAME_FORMAT_FOR_PRINT
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SPACE_WITH_COMMA
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.UNUPLOAD_IMAGE_TYPE_FACSIMILE
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Resource
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.network.api.RequestHandler
import com.parkloyalty.lpr.scan.network.models.ScannedImageUploadResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.LastSecondCheckResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.LastSecondCheckViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.SimilarCitationCheckRequest
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.SimilarCitationCheckViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.SimilarCitationResponse
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.CitationAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.CommentesAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ImageListAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.OfficerListAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.VehicalListAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ViolationsAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CommentsDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.HeaderDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.InvoiceFeeStructure
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.LocationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.OfficerDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.VehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.ViolationDetails
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImageModelOffline
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutField
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.OfflineCancelCitationModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.UnUploadFacsimileImage
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.printer.PrinterType
import com.parkloyalty.lpr.scan.ui.printer.StarPrinterUseCase
import com.parkloyalty.lpr.scan.ui.printer.ZebraPrinterUseCase
import com.parkloyalty.lpr.scan.ui.ticket.TicketDetailsActivity
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelViewModel
import com.parkloyalty.lpr.scan.ui.xfprinter.XfPrinterUseCase
import com.parkloyalty.lpr.scan.util.API_CONSTANT_DOWNLOAD_TYPE_CITATION_IMAGES
import com.parkloyalty.lpr.scan.util.API_CONSTANT_SIGNATURE_IMAGES
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.checkPrintLayoutOrder
import com.parkloyalty.lpr.scan.util.AppUtils.checkPrintLayoutOrderComment
import com.parkloyalty.lpr.scan.util.AppUtils.checkPrintLayoutOrderForTwoColumn
import com.parkloyalty.lpr.scan.util.AppUtils.combineBitmap
import com.parkloyalty.lpr.scan.util.AppUtils.drawableElements
import com.parkloyalty.lpr.scan.util.AppUtils.setLprLock
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.AppUtils.splitID
import com.parkloyalty.lpr.scan.util.CitationPrintSectionUtils
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.CheckTypeOfField
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.FileUtil.checkFooterImageFileExist
import com.parkloyalty.lpr.scan.util.FileUtil.checkHeaderImageFileExist
import com.parkloyalty.lpr.scan.util.FileUtil.getFilePathOfTicketWithHeaderFooterDynamic
import com.parkloyalty.lpr.scan.util.FileUtil.getSignatureFileNameWithExt
import com.parkloyalty.lpr.scan.util.LockLprModel
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.util.QRCodeUtils
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils.isPrintLprImageInCmdPrint
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils.isPrintLprImageInFacsimilePrint
import com.parkloyalty.lpr.scan.util.commandprint.CanvasUtils
import com.parkloyalty.lpr.scan.util.permissions.BluetoothPermissionUtil
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Hashtable
import java.util.Locale
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlin.getValue


/*Phase 2*/
//class LprPreviewActivity : SwitchPrinterActivity(), PrintInterface, CustomDialogHelper {
class LprPreviewActivity : BaseActivity(), PrintInterface, CustomDialogHelper {
//class LprPreviewActivity : XfPrinterActivity(), PrintInterface, CustomDialogHelper {
//class LprPreviewActivity : StarPrinterActivity(), PrintInterface, CustomDialogHelper {
//class LprPreviewActivity : SeikoPrinterActivity(), PrintInterface, CustomDialogHelper {

    @BindView(R.id.tvLocationDetails)
    lateinit var mTvLocationDetails: TextInputEditText

    @BindView(R.id.tvVoilationDetails)
    lateinit var mTvVoilationDetails: TextInputEditText

    @BindView(R.id.layRemarks)
    lateinit var layRemarks: LinearLayoutCompat

    @BindView(R.id.layNotes)
    lateinit var layNotes: LinearLayoutCompat

    @BindView(R.id.imgSignature)
    lateinit var mImageViewSignature: AppCompatImageView

    @BindView(R.id.txtPersonSignature)
    lateinit var mTextViewSignName: AppCompatTextView

    @BindView(R.id.tvOfficerName)
    lateinit var mTextViewOfficerName: AppCompatTextView

    @BindView(R.id.textInputNotes)
    lateinit var mTvNotes: TextInputEditText

    @BindView(R.id.layLocationDetails)
    lateinit var layLocationDetails: LinearLayoutCompat

    @BindView(R.id.layVoilationDetails)
    lateinit var layVoilationDetails: LinearLayoutCompat

    @BindView(R.id.layVehicalDetails)
    lateinit var layVehicleDetails: LinearLayoutCompat

    @BindView(R.id.layOfficerDetails)
    lateinit var layOfficerDetails: LinearLayoutCompat

    @BindView(R.id.layTicketDetails)
    lateinit var layTicketDetails: LinearLayoutCompat

    @BindView(R.id.layImage)
    lateinit var layImage: LinearLayoutCompat

    @BindView(R.id.ivEditVoilation)
    lateinit var mTextInputVoilation: AppCompatImageView

    @BindView(R.id.ivEditRemark)
    lateinit var mImageViewRemark: AppCompatImageView

    @BindView(R.id.ivEditCitation)
    lateinit var mImageViewCitation: AppCompatImageView

    @BindView(R.id.ivEditVehicle)
    lateinit var mImageViewEditVehicle: AppCompatImageView

    @BindView(R.id.ivEditLocation)
    lateinit var mImageViewEditLocation: AppCompatImageView

    @BindView(R.id.ivEditNotes)
    lateinit var mImageViewEditNotes: AppCompatImageView

    @BindView(R.id.ll_mainview)
    lateinit var linearLayoutMainScreenView: LinearLayoutCompat

    @BindView(R.id.layMainPrint)
    lateinit var linearLayoutMainPrint: RelativeLayout

    @BindView(R.id.ivBarcode)
    lateinit var mImageViewBarcode: AppCompatImageView

    @BindView(R.id.layButtonsHide)
    lateinit var layButtonsHide: LinearLayoutCompat

    @BindView(R.id.laySignature)
    lateinit var laySignature: LinearLayoutCompat

    @BindView(R.id.btnPrint)
    lateinit var btnPrint: AppCompatButton

    @BindView(R.id.btnWithoutPrint)
    lateinit var btnWithoutPrint: AppCompatButton

    @BindView(R.id.btn_cancel_offline_citation)
    lateinit var btnCurrentCitation: AppCompatButton

    @BindView(R.id.btnCancel)
    lateinit var btnRescind: AppCompatButton

    @BindView(R.id.ll_child_container)
    lateinit var linearLayoutCompatChildContainer: LinearLayoutCompat

    @BindView(R.id.recycler_imageview)
    lateinit var recyclerViewImage: RecyclerView

    @BindView(R.id.ll_numberview_1)
    lateinit var relativeLayoutPrintAmountSection: RelativeLayout

    @BindView(R.id.ll_stateview_1_1)
    lateinit var relativeLayoutPrintStateSection: RelativeLayout

    @BindView(R.id.ll_numberview_2)
    lateinit var relativeLayoutPrintCitationSection: RelativeLayout

    @BindView(R.id.ll_numberview_3)
    lateinit var relativeLayoutPrintPhilaSection: RelativeLayout

//    @BindView(R.id.rl_ocrtext_view)
//    lateinit var relativeLayoutOcrView: RelativeLayout

    @BindView(R.id.tv_top_message)
    lateinit var appCompatTextViewTopMessage: AppCompatTextView

    @BindView(R.id.ivCommandBasedFacsimile)
    lateinit var ivCommandBasedFacsimile: AppCompatImageView

    private var imageAdapter: ImageListAdapter? = null
    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var mImageViewBarcodePrint: AppCompatImageView? = null
    private var linearLayoutCompatBarCode: LinearLayoutCompat? = null
    private var mCitaionListAdapter: CitationAdapter? = null
    private var mOfficerListAdapter: OfficerListAdapter? = null
    private var mVehicalListAdapter: VehicalListAdapter? = null
    private var mViolationsAdapter: ViolationsAdapter? = null
    private var commentesAdapter: CommentesAdapter? = null

    private var mStartTimeStamp = ""
    private var mCitationNumberId: String? = null
    private var mTicketActionButtonEvent: String? = ""
    private var mCitaionImagesLinks: MutableList<String> = ArrayList()
    private val mTextViewBlock: AppCompatAutoCompleteTextView? = null
    private val mTextViewStreet: AppCompatAutoCompleteTextView? = null
    private val mTextViewDirection: AppCompatAutoCompleteTextView? = null
    private val mTextViewMeter: AppCompatAutoCompleteTextView? = null
    private val mTextViewSpace: AppCompatAutoCompleteTextView? = null
    private val mTextViewSide: AppCompatAutoCompleteTextView? = null
    private val mTextViewLot: AppCompatAutoCompleteTextView? = null
    private val mTextViewCode: AppCompatAutoCompleteTextView? = null
    private val mTextViewDescr: AppCompatAutoCompleteTextView? = null
    private val mTextViewFine: AppCompatAutoCompleteTextView? = null
    private val mTextViewLateFine: AppCompatAutoCompleteTextView? = null
    private val mTextViewDue15: AppCompatAutoCompleteTextView? = null
    private val mTextViewDue30: AppCompatAutoCompleteTextView? = null
    private val mTextViewDue45: AppCompatAutoCompleteTextView? = null
    private val mTextViewViolationVioType: AppCompatAutoCompleteTextView? = null
    private val mTextViewViolationVioTypeCode: AppCompatAutoCompleteTextView? = null
    private val mTextViewViolationVioTypeDescription: AppCompatAutoCompleteTextView? = null
    private val mTextViewTotalDue: AppCompatAutoCompleteTextView? = null
    private val mTvOfficerMake: AppCompatAutoCompleteTextView? = null
    private val mTvOfficerModel: AppCompatAutoCompleteTextView? = null
    private val mTvOfficerColor: AppCompatAutoCompleteTextView? = null
    private val mTextViewLprNum: AppCompatAutoCompleteTextView? = null
    private val mTextViewState: AppCompatAutoCompleteTextView? = null
    private val mTextViewBodyStyle: AppCompatAutoCompleteTextView? = null
    private val mTextViewExpiryYear: AppCompatAutoCompleteTextView? = null
    private val mTextViewDecalYear: AppCompatAutoCompleteTextView? = null
    private val mTextViewVinNum: AppCompatAutoCompleteTextView? = null
    private val mTextViewExpireDate: AppCompatAutoCompleteTextView? = null
    private val mTextViewDecalNum: AppCompatAutoCompleteTextView? = null
    private val mTextViewRemarks1: AppCompatAutoCompleteTextView? = null
    private val mTextViewRemarks2: AppCompatAutoCompleteTextView? = null
    private val mTextViewRemarks3: AppCompatAutoCompleteTextView? = null
    private val mTextViewNote1: AppCompatAutoCompleteTextView? = null
    private val mTextViewNote2: AppCompatAutoCompleteTextView? = null
    private val mTvOfficerName: AppCompatAutoCompleteTextView? = null
    private val mTvBadgeId: AppCompatAutoCompleteTextView? = null
    private val mTvSquad: AppCompatAutoCompleteTextView? = null
    private val mTvZone: AppCompatAutoCompleteTextView? = null
    private val mTvAgency: AppCompatAutoCompleteTextView? = null
    private val mTvBeat: AppCompatAutoCompleteTextView? = null
    private val mTvOfficerId: AppCompatAutoCompleteTextView? = null
    private val mTvTicketNumber: AppCompatAutoCompleteTextView? = null
    private val mTvTicketType: AppCompatAutoCompleteTextView? = null
    private val mTvTicketDetails: AppCompatAutoCompleteTextView? = null
    private val mTvPBCZone: AppCompatAutoCompleteTextView? = null
    var linearLayoutCompatQrCodeAndMessage: LinearLayoutCompat? = null

    private var mIssuranceModel: CitationInsurranceDatabaseModel? =
        CitationInsurranceDatabaseModel()
    private var mPath: String? = ""
    private var mViolationDescription: String? = "0"
    private var mSignaturePath: String? = ""
    private var mRescindButton = "false"
    private var mRescindReason = ""
    private var mRescindNote = ""
    private var mTicketLable = "Valid"
    private var bannerList = ArrayList<CitationImagesModel>()
    private var bannerListForUpload = ArrayList<CitationImagesModel>()
    private var mZone: String? = "CST"
    private var mTicketId: String? = ""
    private var mLastSecondCheckMessage: String? = ""
    private val printBitmap: Bitmap? = null
    private val mPrintLayoutTitle = arrayOfNulls<String>(6)
    private var mPrintLayoutMap: HashMap<String, Int> = HashMap<String, Int>()
    private var gridLayoutCount = 3
    private var gridLayoutCountForCitation = 3
    private var lastSecondTag = ""
    private var cancelledStatus = "Rescind"
    private var m4ButtonType: String? = "pbc_cancel"
    private var appCompatTextView1: AppCompatTextView? = null
    private var appCompatTextView12: AppCompatTextView? = null
    private var appCompatTextView13: AppCompatTextView? = null
    private var appCompatTextView14: AppCompatTextView? = null
    private var appCompatTextView15: AppCompatTextView? = null
    private var appCompatTextView16: AppCompatTextView? = null
    private var appCompatTextView17: AppCompatTextView? = null
    private var appCompatTextView18: AppCompatTextView? = null
    private var appCompatTextView19: AppCompatTextView? = null
    private var appCompatTextView1_1: AppCompatTextView? = null
    private var appCompatTextView12_2: AppCompatTextView? = null
    private var appCompatTextView13_3: AppCompatTextView? = null
    private var appCompatTextView14_4: AppCompatTextView? = null
    private var appCompatTextView15_5: AppCompatTextView? = null
    private var appCompatTextView16_6: AppCompatTextView? = null
    private var appCompatTextView17_7: AppCompatTextView? = null
    private var appCompatTextView18_8: AppCompatTextView? = null
    private var appCompatTextView19_9: AppCompatTextView? = null
    private var appCompatTextView21: AppCompatTextView? = null
    private var appCompatTextView22: AppCompatTextView? = null
    private var appCompatTextView23: AppCompatTextView? = null
    private var appCompatTextView24: AppCompatTextView? = null
    private var appCompatTextView25: AppCompatTextView? = null
    private var appCompatTextView26: AppCompatTextView? = null
    private var appCompatTextView27: AppCompatTextView? = null
    private var appCompatTextView28: AppCompatTextView? = null
    private var appCompatTextView29: AppCompatTextView? = null
    private var appCompatTextView31: AppCompatTextView? = null
    private var appCompatTextView32: AppCompatTextView? = null
    private var appCompatTextView33: AppCompatTextView? = null
    private var appCompatTextView34: AppCompatTextView? = null
    private var appCompatTextView35: AppCompatTextView? = null
    private var appCompatTextView36: AppCompatTextView? = null
    private var appCompatTextView37: AppCompatTextView? = null
    private var appCompatTextView38: AppCompatTextView? = null
    private var appCompatTextView39: AppCompatTextView? = null


    private var mLat: Double? = null
    private var mLong: Double? = null
    private var mWelcomeFormData: WelcomeForm? = null
    private var settingsList: List<DatasetResponse>? = null
    private var isBarCodePrintlayout: Boolean = true
    private var isQRCodePrintlayout: Boolean = false
    private var isOCRTextPrintlayout: Boolean = false
    private var isByPassSimilarCitation: Boolean = false
    private var isFacsimileUploaded: Boolean = false
    private var isSkipPrinter: Boolean = false
    private var isWarningSelected : Boolean = false
    private var isSimilarCitationAPIRetrunsSuccess : Boolean = false
    private var isSimilarCitationAPIErrorPopupDisplay : Boolean = false
    private var isLastSecondCheck = false
    private var isEditImageSection = false
    private var mAddoneY = 82
    private var isErrorOccurDuringGenerateFacsimile = false

    private var mQRCodeValue: String? = ""
    private var mOCRFormatValue: String? = ""
    private var imageUploadSuccessCount = 0
    private val ticketTypeValue = StringBuilder()
    private var mFinalAmount:String = "000000";
    private var recursiveIndex = 0
    private var isErrorUploading: String = ""
    private var officerNameFormatForPrint: String? = null
    private val OfflineCancelCitationModel = OfflineCancelCitationModel()
    private val unUploadFacsimileImage = UnUploadFacsimileImage()

    private val mCreateTicketViewModel: CreateTicketViewModel? by viewModels()
    private val mCitationNumberModel: CitationNumberModel? by viewModels()
    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()
    private val mTicketCancelViewModel: TicketCancelViewModel? by viewModels()
    private val mLastSecondCheckViewModel: LastSecondCheckViewModel? by viewModels()
    private val mSimilarCitationCheckViewModel: SimilarCitationCheckViewModel? by viewModels()

    private var responseLastSecondCheckModel: LastSecondCheckResponse? = null
    private var responseSimilarCitationCheckModel:SimilarCitationResponse? = null
    private var printcommand:java.lang.StringBuilder? = null

    private var mOfficerList = java.util.ArrayList<VehicleListModel>()
    private var mVehicleList = java.util.ArrayList<VehicleListModel>()
    private var mViolationList = java.util.ArrayList<VehicleListModel>()
    private var mCitationList = java.util.ArrayList<VehicleListModel>()
    private var mCommentsList = java.util.ArrayList<VehicleListModel>()

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Nothing to implement here
            } else {
                BluetoothPermissionUtil.requestBluetoothSetting(this@LprPreviewActivity)
            }
        }

    private var zebraPrinterUseCase: ZebraPrinterUseCase? = null
    private var starPrinterUseCase: StarPrinterUseCase? = null
    private var xfPrinterUseCase: XfPrinterUseCase? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lpr_preview)

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            starPrinterUseCase = StarPrinterUseCase()
            starPrinterUseCase?.setPrintInterfaceCallback(this)
            starPrinterUseCase?.initialize(this@LprPreviewActivity)
        } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            xfPrinterUseCase = XfPrinterUseCase(this)
            lifecycle.addObserver(xfPrinterUseCase!!)
            xfPrinterUseCase?.setPrintInterfaceCallback(this)
            xfPrinterUseCase?.initialize(this@LprPreviewActivity)
        } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            zebraPrinterUseCase = ZebraPrinterUseCase()
            zebraPrinterUseCase?.setPrintInterfaceCallback(this)
            zebraPrinterUseCase?.initialize(
                activity = this@LprPreviewActivity,
                contentResolver = contentResolver,
                sharedPreference = sharedPreference
            )
        }

        setFullScreenUI()
        ButterKnife.bind(this)
        printcommand = StringBuilder()
        init()
        mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
        mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
        setLayoutVisibilityBasedOnSettingResponse();

        //TODO Uncomment it, if printer don't work again
//        if (ContextCompat.checkSelfPermission(this@LprPreviewActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED
//            ||ContextCompat.checkSelfPermission(this@LprPreviewActivity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                ActivityCompat.requestPermissions(this@LprPreviewActivity, arrayOf(
//                Manifest.permission.BLUETOOTH_SCAN,
//                Manifest.permission.BLUETOOTH_CONNECT), 2)
////                return
//            }
//        }

        BluetoothPermissionUtil.checkBluetoothPermissions(
            activity = this,
            permissionLauncher = bluetoothPermissionLauncher
        ) {
            //Nothing to implement here in onCreate, This will check bluetooth permission and redirect user to allow bluetooth permission, in case misssed
        }

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)) {
            mainScope.launch {
                delay(2000)
                btnPrint.performClick()
            }
        }


        AppUtils.setYaxis()

        //setAppLogoInCommandPrint()

//        if(bannerList!=null && bannerList!!.size>0 && bannerList!!.get(0)!!.citationImage!!.contains("anpr_")&& isPrintLprImageInFacsimilePrint(getMyDatabase())) {
//            val lprFilePath: File = File(bannerList!!.get(0)!!.citationImage)
//            val lprBitmap = BitmapFactory.decodeFile(lprFilePath.absolutePath)
//            val bwBitmap = convertBitmapToBWUsingDefaultARGB(lprBitmap)
//            setLprImageInCommandPrint(bwBitmap)
//        }

    }

    private fun CreatePrinterCommand(prinvalue : String) {
        if(!prinvalue.isNullOrEmpty()) {
            AppUtils.printQueryStringBuilder!!.append(prinvalue + " \r\n")
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun init() {

        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        addObservers()
        setToolbar()
        if (Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase()) != null) {
            mZone = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())!![0].mValue
        }
        try {
            mStartTimeStamp = splitDateLpr(mZone)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        getCitationNumberId()
        linearLayoutMainPrint.visibility = View.GONE
        btnPrint.disableButton()
        getCitationDataFromDb()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        if (sharedPreference.read(SharedPrefKey.LAST_SECOND_CHECK, false)
        ) {
            btnRescind.visibility = View.VISIBLE
        }
        btnCurrentCitation.visibility= View.GONE
        btnWithoutPrint.visibility = View.GONE
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)) {
            btnWithoutPrint.visibility = View.VISIBLE
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)){
            btnCurrentCitation.visibility= View.VISIBLE
        }

    }

    //init toolbar navigation
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
            R.id.layOwnerBill)
    }

    private val createTicketResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CREATE_TICKET)
    }
    private val citationNumberResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CITATION_NUMBER)
    }
    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_IMAGE)
    }
    private val ticketCancelResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CANCEL)
    }
    private val lastSecondCheckResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.GET_LAST_SECOND_CHECK)
    }
    private val similarCitationCheckResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.SIMILAR_CITATION_CHECK)
    }



    //API Call
//    scanResultListViewModel?.callUploadScannedImagesAPI(
//    this@LprPreviewActivity,
//    createImagesNameList(),
//    createImageMultipart()
//    )

    private fun addObservers() {
        mCreateTicketViewModel?.response?.observe(this, createTicketResponseObserver)
        mCitationNumberModel?.response?.observe(this, citationNumberResponseObserver)
        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
        mTicketCancelViewModel?.response?.observe(this, ticketCancelResponseObserver)
        mLastSecondCheckViewModel?.response?.observe(this, lastSecondCheckResponseObserver)
        mSimilarCitationCheckViewModel?.response?.observe(this,
            similarCitationCheckResponseObserver)

        mUploadImageViewModel?.uploadAllImagesAPIStatus?.observe(this, uploadScannedImagesAPIResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mCreateTicketViewModel?.response?.removeObserver(createTicketResponseObserver)
        mCitationNumberModel?.response?.removeObserver(citationNumberResponseObserver)
        mUploadImageViewModel?.response?.removeObserver(uploadImageResponseObserver)
        mTicketCancelViewModel?.response?.removeObserver(ticketCancelResponseObserver)
        mLastSecondCheckViewModel?.response?.removeObserver(lastSecondCheckResponseObserver)
        mSimilarCitationCheckViewModel?.response?.removeObserver(
            similarCitationCheckResponseObserver)

        mUploadImageViewModel?.uploadAllImagesAPIStatus?.observe(this, uploadScannedImagesAPIResponseObserver)
    }

    private fun GenerateBarCode(ticketId: String?, isPrint: Boolean) {
        //https://learningprogramming.net/mobile/android/create-and-scan-barcode-in-android/
        try {

                val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel?>()
                hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
                val codeWriter: Writer
                codeWriter = Code128Writer()
                var byteMatrix: BitMatrix? = null
                byteMatrix = if (isPrint) {
                    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)) {
//                        codeWriter.encode(ticketId, BarcodeFormat.CODE_128, 560, 44, hintMap)
                        codeWriter.encode(ticketId, BarcodeFormat.CODE_128, 240, 44, hintMap)
                    }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)){
                        codeWriter.encode(ticketId, BarcodeFormat.CODE_128, 340, 44, hintMap)
                    }else {
                        codeWriter.encode(ticketId, BarcodeFormat.CODE_128, 160, 44, hintMap)
                    }
                } else {
                    codeWriter.encode(ticketId, BarcodeFormat.CODE_128, 600, 200, hintMap)
                }// multiple by 2
                // width/2 ; height/2
                // quality and png
                val width = byteMatrix.width
                val height = byteMatrix.height
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                for (i in 0 until width) {
                    for (j in 0 until height) {
                        bitmap.setPixel(i, j, if (byteMatrix[i, j]) Color.BLACK else Color.WHITE)
                    }
                }
                mainScope.async {
                    linearLayoutCompatBarCode = this@LprPreviewActivity.layoutInflater.inflate(
                            R.layout.content_print_bar_code_layout,
                            null
                    ) as LinearLayoutCompat
                    mImageViewBarcodePrint = linearLayoutCompatBarCode!!.findViewById(R.id.ivBarcodePrint)
                    mImageViewBarcodePrint!!.visibility = View.VISIBLE
                    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true) &&
                        linearLayoutCompatQrCodeAndMessage!=null) {
                        linearLayoutCompatQrCodeAndMessage!!.visibility = View.VISIBLE
                    } else if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SEPTA,
                            ignoreCase = true
                        )
                    ) {
                        mImageViewBarcodePrint!!.visibility = View.GONE
                    }

                    val mLinerlayoutBarcodePrint: LinearLayoutCompat? = linearLayoutCompatBarCode!!.findViewById(R.id.llBarcodePrint)
                    if (isPrint) {
                        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
                            (BuildConfig.FLAVOR.equals(DuncanBrandingApp13())&&
                            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK,ignoreCase = true)&&
                            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA,ignoreCase = true))||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)) {
                            mImageViewBarcodePrint!!.visibility = View.INVISIBLE
                            mImageViewBarcodePrint?.setImageBitmap(bitmap)
                            mLinerlayoutBarcodePrint!!.visibility = View.VISIBLE
//                        val dr: Drawable = BitmapDrawable(bitmap)
//                        mLinerlayoutBarcodePrint!!.setBackground(dr)
                        }else{
                            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)){
                                mLinerlayoutBarcodePrint!!.visibility = View.VISIBLE
                            }
                            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true) &&
                                linearLayoutCompatQrCodeAndMessage!=null) {
                                linearLayoutCompatQrCodeAndMessage!!.visibility = View.VISIBLE
                            }
                            mLinerlayoutBarcodePrint!!.visibility = View.VISIBLE
                            mImageViewBarcodePrint!!.visibility = View.VISIBLE
                            mImageViewBarcodePrint?.setImageBitmap(bitmap)
                        }
                    } else {
                        mImageViewBarcode.setImageBitmap(bitmap)
                        mLinerlayoutBarcodePrint!!.visibility = View.GONE
                        mImageViewBarcodePrint!!.visibility = View.GONE
                    }
                }

        } catch (e: Exception) {
            printToastMSG(this@LprPreviewActivity, e.message)
        }
    }

//    private fun GenerateQRCodeInnovaPrint(ticketId: String, imageView: AppCompatImageView) {
//        //https://learningprogramming.net/mobile/android/create-and-scan-barcode-in-android/
//        try {
//                // below line is for getting
//                // the windowmanager service.
//                val manager = getSystemService(WINDOW_SERVICE) as WindowManager
//
//                // initializing a variable for default display.
//                val display = manager.defaultDisplay
//
//                // creating a variable for point which
//                // is to be displayed in QR Code.
//                val point = Point()
//                display.getSize(point)
//
//                // getting width and
//                // height of a point
//                val width = point.x
//                val height = point.y
//
//                // generating dimension from width and height.
//                var dimen = if (width < height) width else height
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true))
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true))
//
//                {
//                    dimen = dimen * 2 / 8
//                }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true))
//
//                {
//                    if((AppUtils.getDeviceTypePhoneOrTablet(this@LprPreviewActivity)).equals("TABLET"))
//                    {
//                        dimen = dimen * 2 / 20
//                    }else {
//                        dimen = dimen * 2 / 12
//                    }
//                }
//                else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true))
//                {
//                    dimen = dimen * 2 / 15
//                }
//                else {
//                    dimen = dimen * 2 / 10
//                    }
//
//                val finalQR0 = ticketId.toString().replace("[ticket_no]",mCitationNumberId.toString())
//                val finalQR1 = finalQR0.toString().replace("[lpr_number]",mIssuranceModel!!.citationData!!.vehicle!!.licensePlate.toString())
//                val finalQR = finalQR1.toString().replace("[state]",mIssuranceModel!!.citationData!!.vehicle!!.state.toString())
//                AppUtils.mFinalQRCodeValue = finalQR
////                hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
////                hints.put(EncodeHintType.MARGIN, 0) /* default = 4 */
//
//                // setting this dimensions inside our qr code
//                // encoder to generate our qr code.
//                //val qrgEncoder = QRGEncoder(finalQR, null, QRGContents.Type.TEXT, dimen)
//                try {
//                    // getting our qrcode in the form of bitmap
//                    // val bitmapQrCode = qrgEncoder.encodeAsBitmap()
//                    val bitmapQrCode = if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)){
//                         QRCodeUtils.getQRCode(finalQR, dimen, .0f)
//                    }else{
//                         QRCodeUtils.getQRCode(finalQR, dimen, .3f)
//                    }
//
//                    // the bitmap is set inside our image
//                    // view using .setimagebitmap method.
////                if (isPrint) {
//                    mainScope.async  {
//                        imageView!!.setImageBitmap(bitmapQrCode)
//                    }
////                } else {
////                    mImageViewBarcode.setImageBitmap(bitmap)
////                }
//                } catch (e: WriterException) {
//                    // this method is called for
//                    // exception handling.
//                    e.printStackTrace()
//                }
//
//        } catch (e: Exception) {
//            printToastMSG(this@LprPreviewActivity, e.message)
//        }
//    }

    private fun GenerateQRCodeInnovaPrint(ticketId: String, imageView: AppCompatImageView) {
        //https://learningprogramming.net/mobile/android/create-and-scan-barcode-in-android/
        try {
            val finalQR = QRCodeUtils.getFinalQRCodeValue(
                ticketId,
                mCitationNumberId.toString(),
                mIssuranceModel?.citationData?.vehicle?.licensePlate.nullSafety(),
                mIssuranceModel?.citationData?.vehicle?.state.nullSafety()
            )
            AppUtils.mFinalQRCodeValue = finalQR
            try {
                // getting our qrcode in the form of bitmap
                // val bitmapQrCode = qrgEncoder.encodeAsBitmap()
                val bitmapQrCode =
                    QRCodeUtils.generateQRCodeForPrint(this@LprPreviewActivity, finalQR)

                mainScope.async {
                    imageView.setImageBitmap(bitmapQrCode)
                }
            } catch (e: WriterException) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            printToastMSG(this@LprPreviewActivity, e.message)
        }
    }


    //setting images from DB
    private fun getCitationDataFromDb() {
        mIssuranceModel = mDb?.dbDAO?.getCitationWithTicket(mCitationNumberId)
        if (mIssuranceModel != null) {

            GenerateBarCode(mCitationNumberId, false)
            //            GenerateQRCode(mCitationNumberId,false);
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.ticketNumber)) {
                createTextView(mTvTicketNumber, layTicketDetails,
                        getString(R.string.scr_lbl_ticket_number),
                        mIssuranceModel!!.citationData!!.ticketNumber)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.ticketType)) {
                createTextView(mTvTicketType, layTicketDetails,
                    getString(R.string.scr_lbl_ticket_type),
                    mIssuranceModel!!.citationData!!.ticketType)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.ticketDate)) {
                createTextView(mTvTicketDetails, layTicketDetails,
                    getString(R.string.scr_lbl_ticket_date),
                    mIssuranceModel!!.citationData!!.ticketDate)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.officer!!.officerDetails)) {
                createTextView(mTvOfficerName, layOfficerDetails,
                    getString(R.string.scr_lbl_officer_name),
                    mIssuranceModel!!.citationData!!.officer!!.officerDetails)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.officer!!.officerId)) {
                createTextView(mTvOfficerId, layOfficerDetails,
                    getString(R.string.scr_lbl_officer_id),
                    splitID(mIssuranceModel!!.citationData!!.officer!!.officerId!!))
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.officer!!.badgeId)) {
                createTextView(mTvBadgeId, layOfficerDetails,
                    getString(R.string.scr_lbl_badge_id),
                    mIssuranceModel!!.citationData!!.officer!!.badgeId)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.officer!!.beat)) {
                createTextView(mTvBeat, layOfficerDetails,
                    getString(R.string.scr_lbl_beat),
                    mIssuranceModel!!.citationData!!.officer!!.beat)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.officer!!.squad)) {
                createTextView(mTvSquad, layOfficerDetails,
                    getString(R.string.scr_lbl_squad),
                    mIssuranceModel!!.citationData!!.officer!!.squad)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.officer!!.zone)) {
                createTextView(mTvZone, layOfficerDetails,
                    getString(R.string.scr_lbl_zone),
                    mIssuranceModel!!.citationData!!.location!!.pcbZone)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.officer!!.agency)) {
                createTextView(mTvAgency, layOfficerDetails,
                    getString(R.string.scr_lbl_agency),
                    mIssuranceModel!!.citationData!!.officer!!.agency)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.locationNotes)) {
                createTextView(mTextViewNote1, layNotes,
                    getString(R.string.scr_lbl_note1),
                    mIssuranceModel!!.citationData!!.locationNotes)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.locationNotes1)) {
                createTextView(mTextViewNote2, layNotes,
                    getString(R.string.scr_lbl_note2),
                    mIssuranceModel!!.citationData!!.locationNotes1)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.locationRemarks)) {
                createTextView(mTextViewRemarks1, layRemarks,
                    getString(R.string.scr_lbl_remark1),
                    mIssuranceModel!!.citationData!!.locationRemarks)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.locationRemarks1)) {
                createTextView(mTextViewRemarks2, layRemarks,
                    getString(R.string.scr_lbl_remark2),
                    mIssuranceModel!!.citationData!!.locationRemarks1)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.locationRemarks2)) {
                createTextView(mTextViewRemarks3, layRemarks,
                    getString(R.string.scr_lbl_remark3),
                    mIssuranceModel!!.citationData!!.locationRemarks2)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.make)) {
                createTextView(mTvOfficerMake, layVehicleDetails,
                    getString(R.string.scr_lbl_make),
                    mIssuranceModel!!.citationData!!.vehicle!!.make)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.model)) {
                createTextView(mTvOfficerModel, layVehicleDetails,
                    getString(R.string.scr_lbl_model),
                    mIssuranceModel!!.citationData!!.vehicle!!.model)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.color)) {
                createTextView(mTvOfficerColor, layVehicleDetails,
                    getString(R.string.scr_lbl_color),
                    mIssuranceModel!!.citationData!!.vehicle!!.color)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.licensePlate)) {
                createTextView(mTextViewLprNum, layVehicleDetails,
                    getString(R.string.scr_lbl_lpr_number),
                    mIssuranceModel!!.citationData!!.vehicle!!.licensePlate)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.state)) {
                createTextView(mTextViewState, layVehicleDetails,
                    mIssuranceModel!!.citationData!!.vehicle!!.stateLabel.toString(),
                    mIssuranceModel!!.citationData!!.vehicle!!.state)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.expiration)) {
                createTextView(mTextViewExpiryYear, layVehicleDetails,
                    getString(R.string.scr_lbl_expiray_year),
                    mIssuranceModel!!.citationData!!.vehicle!!.expiration)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.bodyStyle)) {
                createTextView(mTextViewBodyStyle, layVehicleDetails,
                    mIssuranceModel!!.citationData!!.vehicle!!.bodyStyleLabel.toString(),
                    mIssuranceModel!!.citationData!!.vehicle!!.bodyStyle)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.decalYear)) {
                createTextView(mTextViewDecalYear, layVehicleDetails,
                    mIssuranceModel!!.citationData!!.vehicle!!.decalYearLabel.toString(),
                    mIssuranceModel!!.citationData!!.vehicle!!.decalYear)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.decalNumber)) {
                createTextView(mTextViewDecalNum, layVehicleDetails,
                    mIssuranceModel!!.citationData!!.vehicle!!.decalNumberLabel!!.toString(),
                    mIssuranceModel!!.citationData!!.vehicle!!.decalNumber)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.vinNumber)) {
                createTextView(mTextViewVinNum, layVehicleDetails,
                    getString(R.string.scr_lbl_vin_number),
                    mIssuranceModel!!.citationData!!.vehicle!!.vinNumber)
            }
           /* if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.expiration)) {
                createTextView(
                        mTextViewExpireDate,
                    layVehicleDetails,
                    getString(R.string.scr_lbl_expiray_year),
                    mIssuranceModel!!.citationData!!.vehicle!!.expiration
                )
            }*/
//            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.decalNumber)) {
//                createTextView(mTextViewDecalNum, layVehicleDetails,
//                    getString(R.string.scr_lbl_decal_number),
//                    mIssuranceModel!!.citationData!!.vehicle!!.decalNumber)
//            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.lot)) {
                createTextView(mTextViewLot, layLocationDetails,
                    getString(R.string.scr_lbl_lot),
                    mIssuranceModel!!.citationData!!.location!!.lot)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.street)) {
                createTextView(mTextViewStreet, layLocationDetails,
                    getString(R.string.scr_lbl_street),
                    mIssuranceModel!!.citationData!!.location!!.street)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.block)) {
                createTextView(mTextViewBlock, layLocationDetails,
                    getString(R.string.scr_lbl_block),
                    mIssuranceModel!!.citationData!!.location!!.block)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.direction)) {
                createTextView(mTextViewDirection, layLocationDetails,
                    mIssuranceModel!!.citationData!!.location!!.directionLabel.toString(),
                    mIssuranceModel!!.citationData!!.location!!.direction)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.side)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)
                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,ignoreCase = true)){
                createTextView(mTextViewSide, layLocationDetails,
                    getString(R.string.scr_lbl_side_of_street),
                    mIssuranceModel!!.citationData!!.location!!.side)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.meterName)) {
                createTextView(mTextViewMeter, layLocationDetails,
                    getString(R.string.scr_lbl_meter),
                    mIssuranceModel!!.citationData!!.location!!.meterName)
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.spaceName)) {
                createTextView(mTextViewSpace, layLocationDetails,
                    getString(R.string.scr_lbl_space_without_colon),
                    mIssuranceModel!!.citationData!!.location!!.spaceName)
            }
            if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.pcbZone)) {
                createTextView(mTvPBCZone, layLocationDetails,
                    getString(R.string.scr_lbl_pbc_zone),
                    mIssuranceModel?.citationData?.location?.pcbZone)
            }
            if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.cityZone)) {
                createTextView(mTvPBCZone, layLocationDetails,
                    getString(R.string.scr_lbl_zone),
                    mIssuranceModel?.citationData?.location?.cityZone.toString())
            }
            if (mIssuranceModel!!.citationData!!.voilation!!.code != null) {
                if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.code)) {
                    createTextView(mTextViewCode, layVoilationDetails,
                        getString(R.string.scr_lbl_code),
                        mIssuranceModel!!.citationData!!.voilation!!.code)
                }
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.locationDescr)) {
                createTextView(mTextViewDescr, layVoilationDetails,
                    getString(R.string.scr_lbl_description),
                    mIssuranceModel!!.citationData!!.voilation!!.locationDescr)
                mViolationDescription = mIssuranceModel!!.citationData!!.voilation!!.timeLimitVio
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.amount)) {
                createTextView(mTextViewFine, layVoilationDetails,
                    getString(R.string.scr_lbl_fine),
                    getString(R.string.doller_sign) + mIssuranceModel!!.citationData!!.voilation!!.amount
                )
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.amountDueDate)) {
                createTextView(mTextViewLateFine, layVoilationDetails,
                    getString(R.string.scr_lbl_late_fine),
                    getString(R.string.doller_sign) + mIssuranceModel!!.citationData!!.voilation!!.amountDueDate
                )
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.dueDate)) {
                createTextView(mTextViewDue15, layVoilationDetails,
                    getString(R.string.scr_lbl_due_15),
                    getString(R.string.doller_sign) + mIssuranceModel!!.citationData!!.voilation!!.dueDate
                )
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.dueDate30)) {
                createTextView(mTextViewDue30, layVoilationDetails,
                    getString(R.string.scr_lbl_due_30),
                    getString(R.string.doller_sign) + mIssuranceModel!!.citationData!!.voilation!!.dueDate30
                )
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.dueDate45)) {
                createTextView(mTextViewDue45, layVoilationDetails,
                    getString(R.string.scr_lbl_due_45),
                    getString(R.string.doller_sign) + mIssuranceModel!!.citationData!!.voilation!!.dueDate45
                )
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.vioType)) {
                createTextView(mTextViewViolationVioType, layVoilationDetails,
                    getString(R.string.scr_lbl_violation_vio_type),
                     mIssuranceModel!!.citationData!!.voilation!!.vioType
                )
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.vioTypeCode)) {
                createTextView(mTextViewViolationVioTypeCode, layVoilationDetails,
                    getString(R.string.scr_lbl_violation_vio_type_code),
                     mIssuranceModel!!.citationData!!.voilation!!.vioTypeCode
                )
            }
            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.vioTypeDescription)) {
                createTextView(mTextViewViolationVioTypeDescription, layVoilationDetails,
                    getString(R.string.scr_lbl_violation_vio_type_description),
                     mIssuranceModel!!.citationData!!.voilation!!.vioTypeDescription
                )
            }
            if (mIssuranceModel!!.citationData!!.voilation!!.dueDateTotal!=null &&
                mIssuranceModel!!.citationData!!.voilation!!.dueDateTotal!="null" &&
                !TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.dueDateTotal)) {
                createTextView(mTextViewTotalDue, layVoilationDetails,
                    getString(R.string.scr_lbl_total_due_now),
                    getString(R.string.doller_sign) + mIssuranceModel!!.citationData!!.voilation!!.dueDateTotal
                )
            }
            mTvNotes.setText(mIssuranceModel!!.citationData!!.locationNotes + ", " + mIssuranceModel!!.citationData!!.locationNotes1)

            showToast(
                context = this@LprPreviewActivity,
                message = getString(R.string.scr_message_please_wait),
                duration = Toast.LENGTH_SHORT
            )

            mainScope.launch {
                delay(1000)
                createViewToPrint(mIssuranceModel)
                btnPrint.enableButton()
            }
        }
        //setting images from DB
        bannerList = mDb?.dbDAO?.getCitationImage() as ArrayList<CitationImagesModel>
        bannerListForUpload = mDb?.dbDAO?.getCitationImage() as ArrayList<CitationImagesModel>
        if (bannerList.size > 0) {
            try {
                bannerListForUpload.clear();
                /*for (i in mCitaionImagesLinks!!.indices.reversed()) {
                    bannerListForUpload!!.removeAt(i)
                }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
            setAdapterForImageList(bannerList)
            layImage.visibility = View.VISIBLE
        } else {
            layImage.visibility = View.GONE
        }
        //getting signature path
        mSignaturePath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
        val imageName = getSignatureFileNameWithExt()
        mSignaturePath = mSignaturePath + Constants.CAMERA + "/" + imageName
        val file = File(mSignaturePath)
        if (file.exists() && !TextUtils.isEmpty(imageName)) {
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)) {
                mImageViewSignature.visibility = View.VISIBLE
                mTextViewSignName.visibility = View.VISIBLE
                if (file.exists() && !TextUtils.isEmpty(mSignaturePath)) {
                    mImageViewSignature.setImageURI(Uri.fromFile(File(mSignaturePath)))
                }
                try {
                    mTextViewSignName.text =
                        mIssuranceModel!!.citationData!!.officer!!.officerDetails

                    /* firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.ITEM_ID, "001")
                    param(FirebaseAnalytics.Param.ITEM_NAME, "Preview Activity")
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "image Count= "+bannerList.size)
                }*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                    }
        }
    }
    private fun setAdapterForImageList(listData: List<CitationImagesModel>) {
        if(listData!!.size % 2 !=0){
            listData.last().type = 1
        }
        imageAdapter = ImageListAdapter(mContext!!,listData,object : ImageListAdapter.ListItemSelectListener {
                    override fun onItemClick(position: Int) {
                        mDb?.dbDAO?.deleteTempImagesWithId(bannerList!![position]!!.id)
                        bannerList.removeAt(position)
                        imageAdapter?.notifyDataSetChanged()
                    }
                })
        recyclerViewImage.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(mContext, 2)
        gridLayoutManager.scrollToPositionWithOffset(0, 0)
        recyclerViewImage.adapter = imageAdapter
        recyclerViewImage.layoutManager = gridLayoutManager
        recyclerViewImage.visibility = View.VISIBLE

        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (imageAdapter!!.getItemViewType(position)) {
                    ImageListAdapter.FULLSIZE -> 2
                    ImageListAdapter.ITEMTWO -> 1
//                    ImageListAdapter.FULLSIZE -> 3
                    else -> 1
                }
            }
        }
        recyclerViewImage.layoutManager = gridLayoutManager
    }

    private fun getCitationNumberId() {
        val intent = intent
        if (intent != null) {
            mCitationNumberId = intent.getStringExtra("booklet_id")
            mTicketActionButtonEvent = intent.getStringExtra("btn_action")

            mCitaionImagesLinks = intent.getStringArrayListExtra("Citation_Images_Link")!!;
        }
    }

    private fun createTextView(
        mTextViewF: AppCompatAutoCompleteTextView?,
        mLay: LinearLayoutCompat?,
        mText: String,
        mTextValue: String?
    ) {
        var mTextView = mTextViewF
        val mCitationLayout = CitationLayoutField()
        mCitationLayout.tag = "textview" //remove
        mCitationLayout.isEditable = false //remove
        mCitationLayout.repr = mText
        mTextView = CheckTypeOfField(mCitationLayout, mLay, "Lpr", mContext)
        mTextView!!.setText(mTextValue)
    }

    /* Call Api For Citation issue number*/
    private fun callCitationNumberApi() {
        if (isInternetAvailable(this@LprPreviewActivity)) {
            val mCitationNumberRequest = CitationNumberRequest()
//            mCitationNumberRequest.deviceId = getDeviceId(mContext!!)
            val uniqueID = AppUtils.getDeviceId(mContext!!)
            mCitationNumberRequest.deviceId = uniqueID+"-"+mWelcomeFormData?.officerDeviceName.nullSafety()
            //LogUtil.printLog("device_id",AppUtils.getDeviceId(mContext));
            mCitationNumberModel!!.hitGetCitationNumberApi(mCitationNumberRequest)
        } else {
            LogUtil.printToastMSG(
                this@LprPreviewActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun callLastSecondCheckAPI() {
//        {{local}}/analytics/mobile/pay_by_plate?zone=Indigo&Park&lp_number=VIM112
        if (NetworkCheck.isInternetAvailable(this@LprPreviewActivity)) {
            var endPoint = ""
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, true)) {
                val payBySpaceZone = sharedPreference.read(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
                endPoint =
                    "zone=" + (if (payBySpaceZone!!.length > 1) payBySpaceZone else "NULL") + "&lp_number=" + mIssuranceModel?.citationData?.vehicle?.licensePlate +
                            "&space_id=" + mIssuranceModel?.citationData?.location?.spaceName
            } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)){
                val payBySpaceZone = sharedPreference.read(SharedPrefKey.PAY_BY_ZONE_SPACE, "")
                endPoint =
                    "zone=" + (if (payBySpaceZone!!.length > 1) payBySpaceZone else "NOZONE") + "&lp_number=" + mIssuranceModel?.citationData?.vehicle?.licensePlate +
                            "&space_id=" + mIssuranceModel?.citationData?.location?.spaceName
            }else {
                endPoint =
                    "zone=" + (if (mIssuranceModel?.citationData?.location?.pcbZone != null) mIssuranceModel?.citationData?.location?.pcbZone else if (mIssuranceModel?.citationData?.officer?.zone != null) mIssuranceModel?.citationData?.officer?.zone else "NULL") + "&lp_number=" + mIssuranceModel?.citationData?.vehicle?.licensePlate +
                            "&space_id=" + mIssuranceModel?.citationData?.location?.spaceName
            }
            mLastSecondCheckViewModel?.hitLastSecondCheckApi(endPoint)
        } else {
            LogUtil.printToastMSG(this@LprPreviewActivity,
                getString(R.string.err_msg_connection_was_refused))
        }
    }

    private fun callSimilarCitationCheckPopUp() {
//        http://{{local}}/citations-issuer/citation_similarity_check
        /**
         * By pass the similar citation check
         */
        if (isByPassSimilarCitation) {
            if (sharedPreference.read(SharedPrefKey.LAST_SECOND_CHECK, false)) {
                lastSecondCheckResponseForPopup()
            } else {
                moveNextScreen()
            }

        } else {
            similarCitationCheckResponseForPopup()
        }
    }

    private fun callSimilarAndLastSecondCheckAPI()
    {
        /**
         * By pass the similar citation check
         */
        if(isByPassSimilarCitation){
            if (NetworkCheck.isInternetAvailable(this@LprPreviewActivity))
            {
                if (sharedPreference.read(SharedPrefKey.LAST_SECOND_CHECK,false)) {
                    callLastSecondCheckAPI()
                }
            }
        }else {
            if (NetworkCheck.isInternetAvailable(this@LprPreviewActivity)) {
                findViewById<AppCompatImageView>(R.id.imgBack).visibility = View.GONE
                findViewById<AppCompatImageView>(R.id.imgOptions).visibility = View.GONE
                val checkRequest = SimilarCitationCheckRequest()
                checkRequest.zone =
                        if (mIssuranceModel!!.citationData!!.location!!.pcbZone != null) mIssuranceModel!!.citationData!!.location!!.pcbZone else if (mIssuranceModel!!.citationData!!.officer!!.zone != null) mIssuranceModel!!.citationData!!.officer!!.zone else ""
                checkRequest.lpNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                checkRequest.block = mIssuranceModel!!.citationData!!.location!!.block
                checkRequest.street = mIssuranceModel!!.citationData!!.location!!.street
                checkRequest.code = mIssuranceModel!!.citationData!!.voilation!!.code
                checkRequest.description = mIssuranceModel!!.citationData!!.voilation!!.locationDescr
                checkRequest.side = mIssuranceModel!!.citationData!!.location!!.side
                checkRequest.state = mIssuranceModel!!.citationData!!.vehicle!!.state
                checkRequest.ticket_no = mIssuranceModel?.citationData?.ticketNumber
                mSimilarCitationCheckViewModel!!.hitSimilarCitationCheckApi(checkRequest)
            } else {
                LogUtil.printToastMSG(
                        this@LprPreviewActivity,
                        getString(R.string.err_msg_connection_was_refused)
                )
//                moveNextScreen()
            }
        }
    }

    private fun seveImageInOfflineDB() {
//        List<CitationImageModelOffline> modelOfflines = new ArrayList<>();
        try {
            var counter = 0
            printLog("image size", bannerList.size)
            for (model in bannerList) {
                val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
                val offline = CitationImageModelOffline()
                //            offline.setId(model.getId());
                offline.id = id.toInt() + counter
                offline.mCitationNumber = 0
                offline.mCitationNumberText = mCitationNumberId!!
                offline.citationImage = model.citationImage
                offline.status = model.status
                //                modelOfflines.add(offline);
                mDb!!.dbDAO!!.insertCitationImageOffline(offline)
                counter++
            }

            //Adding facsimile with header footer in case of offline mode, this will only add when we have header footer bitmap in the list
            val bannerListForUploadObj = bannerListForUpload.firstOrNull {
                it.citationImage.nullSafety().contains(FILE_NAME_HEADER_FOOTER_BITMAP, true)
            }
            if (bannerListForUploadObj != null) {
                val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
                val offline = CitationImageModelOffline()
                offline.id = id.toInt() + counter
                offline.mCitationNumber = 0
                offline.mCitationNumberText = mCitationNumberId!!
                offline.citationImage = bannerListForUploadObj.citationImage
                offline.status = bannerListForUploadObj.status
                getMyDatabase()?.dbDAO?.insertCitationImageOffline(offline)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    private fun createImagesNameList(): Array<String?> {
        val imageNameList = arrayOfNulls<String>(bannerListForUpload.size)
        var uploadImageName = ""
        bannerListForUpload.forEachIndexed { index, scanDataModel ->
//            if (scanDataModel.citationImage.nullSafety().contains("Image_printer")) {
            if (scanDataModel.citationImage.nullSafety().contains(FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
//                arrayOf(mCitationNumberId + "_" + imageUploadSuccessCount + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP")
                uploadImageName = (mCitationNumberId + "_" + imageUploadSuccessCount + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
            } else if (scanDataModel.citationImage.nullSafety().contains(FILE_NAME_HEADER_FOOTER_BITMAP, true)) {
            uploadImageName = (mCitationNumberId + "_" + imageUploadSuccessCount + "_" + FILE_NAME_HEADER_FOOTER_BITMAP)
        }

        else {
                uploadImageName = (mCitationNumberId + "_" + imageUploadSuccessCount)
            }
            try {
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"---------PREVIEW Request Image Upload--------")
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"Request "+" :- "+uploadImageName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imageNameList.set(index, uploadImageName)
        }
        return imageNameList
    }

    private fun createImageMultipart(): List<MultipartBody.Part?> {
        val imageMultipartList = ArrayList<MultipartBody.Part?>()

        bannerListForUpload.forEach {
            val tempFile: File = File(it.citationImage.nullSafety())
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), tempFile!!)
            val files = MultipartBody.Part.createFormData(
                    "files",
                    tempFile.name,
                    requestFile
            )
            imageMultipartList.add(files)
        }

        return imageMultipartList
    }

    private fun callBulkImageUpload()
    {
        if (isInternetAvailable(this@LprPreviewActivity)) {
            mUploadImageViewModel!!.callUploadScannedImagesAPI(
                    this@LprPreviewActivity,
                    createImagesNameList(),
                    createImageMultipart(), API_CONSTANT_DOWNLOAD_TYPE_CITATION_IMAGES, "PreviewActivity")
        }else {
            LogUtil.printToastMSG(
                    this@LprPreviewActivity,
                    getString(R.string.err_msg_connection_was_refused)
            )
            saveCitationStatus1()
        }
    }
    private fun createSignatureImagesNameList(): Array<String?> {
        val imageNameList = arrayOfNulls<String>(bannerListForUpload.size)
        bannerListForUpload.forEachIndexed { index, scanDataModel ->
                arrayOf(mCitationNumberId + "_" + imageUploadSuccessCount + "_" + "SignatureImages"+"_"+
                        mIssuranceModel?.citationData?.officer?.badgeId.nullSafety())
            imageNameList.set(
                    index,
                    scanDataModel.citationImage.nullSafety().getFileNameWithExtension()
            )
        }

        return imageNameList
    }

    private fun createSignatureImageMultipart(): List<MultipartBody.Part?> {
        val imageMultipartList = ArrayList<MultipartBody.Part?>()
//        bannerListForUpload.forEach {
            val tempFile: File = File(mSignaturePath.nullSafety())
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), tempFile!!)
            val files = MultipartBody.Part.createFormData(
                    "files",
                    tempFile.name,
                    requestFile
            )
            imageMultipartList.add(files)
//        }

        return imageMultipartList
    }

    private fun callSignatureBulkImageUpload()
    {
        if (isInternetAvailable(this@LprPreviewActivity)) {
            mUploadImageViewModel!!.callUploadScannedImagesAPI(
                    this@LprPreviewActivity,
                    createSignatureImagesNameList(),
                    createSignatureImageMultipart(), API_CONSTANT_SIGNATURE_IMAGES, "PreviewActivity")
        }else {
            LogUtil.printToastMSG(
                    this@LprPreviewActivity,
                    getString(R.string.err_msg_connection_was_refused)
            )
//            saveCitationStatus1()
        }
    }

    private fun saveCitationStatus1()
    {
        mDb?.dbDAO?.updateCitationUploadStatus(1, mCitationNumberId)
        mIssuranceModel = mDb?.dbDAO?.getCitationWithTicket(mCitationNumberId)
        isErrorUploading = "UploadingError"
        moveNextWithId("none")
    }

    private fun saveUnUploadImages()
    {
        class SaveTask : AsyncTask<Void?, Int?, String>() {
            override fun doInBackground(vararg voids: Void?): String? {
                try {
                    val mSelectedImageFileUriList = ArrayList<String>()
                    val activityImageTable = ActivityImageTable()
                    activityImageTable!!.activityResponseId = mCitationNumberId
                    activityImageTable!!.id = mCitationNumberId!!.toInt()
                    activityImageTable!!.uploadStatus = "false"
//                    for (index in bannerList!!.indices) {
//                        mSelectedImageFileUriList.add(bannerList!!.get(index)!!.citationImage.toString())
                        if(bannerList!!.size<= 0)
                        activityImageTable!!.image1 = bannerList!!.get(0)!!.citationImage.toString()
                        if(bannerList!!.size<= 1)
                        activityImageTable!!.image2 = bannerList!!.get(1)!!.citationImage.toString()
                        if(bannerList!!.size<= 2)
                        activityImageTable!!.image3 = bannerList!!.get(2)!!.citationImage.toString()
//                    }
//                    activityImageTable.imagesList = mSelectedImageFileUriList
                    mDb?.dbDAO?.insertActivityImageData(activityImageTable)
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

    private val uploadScannedImagesAPIResponseObserver = Observer<Any> {

        when (it) {
            is Resource.Error<*> -> {
                showToast(context = this@LprPreviewActivity, message = it.message.nullSafety())
                callCreateTicketApi()
//                saveCitationStatus1()
            }

            is Resource.Success<*> -> {
                val response = it.data as ScannedImageUploadResponse

                when (response.status) {
                    true -> {
                        for (i in response.data.get(0).response!!.links!!.indices) {
                            try {
                                mCitaionImagesLinks.add(response.data.get(0).response!!.links.get(i))
                                unUploadFacsimileImage?.dateTime?.let { it1 ->
                                    mDb!!.dbDAO!!.updateFacsimileImageLink(response.data.get(0).response!!
                                        .links.get(0).toString(),mIssuranceModel!!.citationData!!.ticketNumber!!.toString(),
                                        it1
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        try {
                            ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"Image Upload Response"+" :- "+it.data)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        callCreateTicketApi()

                    }
                    else -> {
                        showToast(
                                context = this@LprPreviewActivity,
                                message = getString(R.string.err_msg_something_went_wrong)
                        )
//                        saveUnUploadImages()
//                        saveCitationStatus1()
                        callCreateTicketApi()
                        try {
                            ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"ERROR : "+"Image Upload"+" :- "+it.data.message)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            is Resource.Loading<*> -> {
                it.isLoadingShow.let {
                    if (it as Boolean) {
                        showProgressLoader(getString(R.string.scr_message_please_wait))
                    } else {
                        dismissLoader()
                    }
                }
            }

            is Resource.NoInternetError<*> -> {
                showToast(context = this@LprPreviewActivity, message = it.message.nullSafety())
//                saveUnUploadImages()
//                saveCitationStatus1()
                callCreateTicketApi()
            }
        }
    }

    /* Call Api For citation layout details */
    private fun callCreateTicketApi() {
        if (isInternetAvailable(this@LprPreviewActivity)) {
//            val createTicketRequest = CreateTicketRequest()
//            val locationDetails = LocationDetails()
//            locationDetails.street = mIssuranceModel?.citationData?.location?.street.nullSafety()
//            locationDetails.street_lookup_code = mIssuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
//            locationDetails.block = mIssuranceModel?.citationData?.location?.block.nullSafety()
//
//            locationDetails.meter = mIssuranceModel?.citationData?.location?.meterName.nullSafety()
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)) {
//                locationDetails.mImpoundCode =
//                    mIssuranceModel?.citationData?.location?.side.nullSafety()
//            }else{
//                locationDetails.side = mIssuranceModel?.citationData?.location?.side.nullSafety()
//                locationDetails.direction =
//                    mIssuranceModel?.citationData?.location?.direction.nullSafety()
//            }
//
//            locationDetails.lot = mIssuranceModel?.citationData?.location?.lot.nullSafety()
//            locationDetails.mSpaceId = mIssuranceModel?.citationData?.location?.spaceName.nullSafety().trim()
//            createTicketRequest.locationDetails = locationDetails
//            val vehicleDetails = VehicleDetails()
//            vehicleDetails.body_style =
//                mIssuranceModel?.citationData?.vehicle?.bodyStyle.nullSafety()
//            vehicleDetails.body_style_lookup_code =
//                mIssuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
//            vehicleDetails.decal_year =
//                mIssuranceModel?.citationData?.vehicle?.decalYear.nullSafety()
//            vehicleDetails.decal_number =
//                mIssuranceModel?.citationData?.vehicle?.decalNumber.nullSafety()
//            vehicleDetails.vin_number =
//                mIssuranceModel?.citationData?.vehicle?.vinNumber.nullSafety()
//            vehicleDetails.make = mIssuranceModel?.citationData?.vehicle?.make.nullSafety()
//            vehicleDetails.color = mIssuranceModel?.citationData?.vehicle?.color.nullSafety()
//            vehicleDetails.model = mIssuranceModel?.citationData?.vehicle?.model.nullSafety()
//            vehicleDetails.model_lookup_code = mIssuranceModel?.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
//            vehicleDetails.lprNo = mIssuranceModel?.citationData?.vehicle?.licensePlate.nullSafety()
//            vehicleDetails.state = mIssuranceModel?.citationData?.vehicle?.state.nullSafety()
//            vehicleDetails.mLicenseExpiry =
//                mIssuranceModel?.citationData?.vehicle?.expiration.nullSafety()
//            createTicketRequest.vehicleDetails = vehicleDetails
//            val violationDetails = ViolationDetails()
//            violationDetails.code =
//                mIssuranceModel?.citationData?.voilation?.code.nullSafety() //mAutoComTextViewCode.getEditableText().toString().trim());
//            violationDetails.violation =
//                    mIssuranceModel?.citationData?.voilation?.violationCode.nullSafety()
//            violationDetails.description =
//                mIssuranceModel?.citationData?.voilation?.locationDescr.nullSafety()
//            try {
//                violationDetails.fine =
//                    mIssuranceModel?.citationData?.voilation?.amount.nullSafety("0").toDouble()
//                violationDetails.late_fine =
//                    mIssuranceModel?.citationData?.voilation?.amountDueDate.nullSafety("0")
//                        .toDouble()
//                violationDetails.due_15_days =
//                    mIssuranceModel?.citationData?.voilation?.dueDate.nullSafety("0").toDouble()
//                violationDetails.due_30_days =
//                    mIssuranceModel?.citationData?.voilation?.dueDate30.nullSafety("0").toDouble()
//                violationDetails.due_45_days =
//                    mIssuranceModel?.citationData?.voilation?.dueDate45.nullSafety("0").toDouble()
//                violationDetails.export_code =
//                    mIssuranceModel?.citationData?.voilation?.export_code.nullSafety()
//                violationDetails.mCost =
//                    if(mIssuranceModel?.citationData?.voilation?.dueDateCost!=null && !mIssuranceModel?.citationData?.voilation?.dueDateCost.equals("null")) mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety("0").toDouble() else 0.0
//
//                val sanctionsType = mIssuranceModel?.citationData?.voilation?.mSanctionsType?.let {
//                    if (it > 0) {
//                        when (it) {
//                            1 -> "White Sticker"
//                            2 -> "Red Sticker"
//                            else -> ""
//                        }
//                    } else ""
//                } ?: ""
//                violationDetails.mSanctionsType = sanctionsType
//
//                try {//Volusia and ocmd and merrick park for invocie object implement
//                    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true))
//                    {
//                        val unpaidCount = mIssuranceModel?.citationData?.voilation?.mUnpaidCitationCount.nullSafety(0)
//                        val citationCount = if (unpaidCount <= 0) 1 else (unpaidCount + 1).coerceAtMost(3)
//
//                        val basicRate = if (mIssuranceModel?.citationData?.voilation?.dueDateParkingFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.equals(
//                                "null"
//                            )
//                        ) mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.nullSafety(
//                            "0"
//                        ).toDouble() else 0.0
//
//
//                        val baseRateOfCitationCount = basicRate*citationCount
//// Calculate 7% of totalRate
//                        val totalRate7Percent = (baseRateOfCitationCount * 7) / 100
//
//// Calculate dueDateCost safely
//                        val transactionFee = mIssuranceModel?.citationData?.voilation?.dueDateCitationFee
//                            ?.takeIf { !it.equals("null", ignoreCase = true) }
//                            ?.nullSafety("0")
//                            ?.toDouble()
//                            ?.takeIf { it != 0.0 } ?: 0.10
//
//// Final basicRate calculation
////                        val basicRate = (totalRate - totalRate7Percent) - transactionFee
//
//// Build InvoiceFeeStructure
//                        val invoiceFeeStructure = InvoiceFeeStructure().apply {
//                            mSaleTax = totalRate7Percent
//                            mCitationFee = transactionFee
//                            mParkingFee = baseRateOfCitationCount
//                        }
//
//                        createTicketRequest.invoiceFeeStructure = invoiceFeeStructure
//
//                    }else {
//                        val invoiceFeeStructure = InvoiceFeeStructure()
//                        invoiceFeeStructure.mSaleTax =
//                            if (mIssuranceModel?.citationData?.voilation?.dueDateCost != null && !mIssuranceModel?.citationData?.voilation?.dueDateCost.equals(
//                                    "null"
//                                )
//                            ) mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety("0")
//                                .toDouble() else 0.0
//                        invoiceFeeStructure.mCitationFee =
//                            if (mIssuranceModel?.citationData?.voilation?.dueDateCitationFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.equals(
//                                    "null"
//                                )
//                            ) mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.nullSafety(
//                                "0"
//                            ).toDouble() else 0.0
//                        invoiceFeeStructure.mParkingFee =
//                            if (mIssuranceModel?.citationData?.voilation?.dueDateParkingFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.equals(
//                                    "null"
//                                )
//                            ) mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.nullSafety(
//                                "0"
//                            ).toDouble() else 0.0
//                        createTicketRequest.invoiceFeeStructure = invoiceFeeStructure
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            createTicketRequest.violationDetails = violationDetails
//            val officerDetails = OfficerDetails()
//            //officerDetails.setOfficerId(mData.getSiteOfficerId());
//            officerDetails.badgeId = mIssuranceModel?.citationData?.officer?.badgeId.nullSafety()
//            officerDetails.officer_lookup_code = mIssuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
//            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true)) {
//                officerDetails.officer_name =
//                    Util.officerNameForBurbank(mIssuranceModel?.citationData?.officer?.officerDetails.nullSafety())
//            } else {
//                officerDetails.officer_name =
//                    AppUtils.getOfficerName(
//                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
//                            .nullSafety())
//                    }
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)||
//                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true)) {
//                officerDetails.peo_fname = AppUtils.getPOEName(mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim().nullSafety(), 0)
//                officerDetails.peo_lname = AppUtils.getPOEName(mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim().nullSafety(), 1)
//                officerDetails.peo_name = officerDetails.peo_fname + ", " + officerDetails.peo_lname
//            }
//            officerDetails.zone =
//                if (mIssuranceModel?.citationData?.location?.pcbZone != null) mIssuranceModel?.citationData?.location?.pcbZone.nullSafety()
//                else if (mIssuranceModel?.citationData?.officer?.zone != null) mIssuranceModel?.citationData?.officer?.zone.nullSafety()
//                else ""
//            //            officerDetails.setZone(mIssuranceModel.getCitationData().getLocation().getPcbZone());
//            officerDetails.signature = ""
//            officerDetails.squad = mIssuranceModel?.citationData?.officer?.squad.nullSafety()
//            officerDetails.beat = mIssuranceModel?.citationData?.officer?.beat.nullSafety()
//            officerDetails.agency = if(mIssuranceModel?.citationData?.officer?.agency!=null && mIssuranceModel?.citationData?.officer?.agency!!.isNotEmpty()) mIssuranceModel?.citationData?.officer?.agency.nullSafety() else mWelcomeFormData!!.agency
//            //            officerDetails.setmShift(mIssuranceModel.getCitationData().getOfficer().getShift());
//            officerDetails.mShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
////            officerDetails.mDdeviceId = mWelcomeFormData?.officerDeviceId.nullSafety()
//            officerDetails.mDdeviceId = mWelcomeFormData?.officerDeviceName.nullSafety()
//            officerDetails.mDdeviceFriendlyName = mWelcomeFormData?.officerDeviceName.nullSafety()
//            val commentsDetails = CommentsDetails()
//            commentsDetails.note1 = mIssuranceModel?.citationData?.locationNotes.nullSafety()
//            commentsDetails.note2 = mIssuranceModel?.citationData?.locationNotes1.nullSafety()
//            commentsDetails.note3 = mIssuranceModel?.citationData?.locationNotes2.nullSafety()
//            commentsDetails.remark1 = mIssuranceModel?.citationData?.locationRemarks.nullSafety()
//            commentsDetails.remark2 = mIssuranceModel?.citationData?.locationRemarks1.nullSafety()+" "+
//                    mIssuranceModel?.citationData?.locationRemarks2.nullSafety()
//            createTicketRequest.commentsDetails = commentsDetails
//            createTicketRequest.officerDetails = officerDetails
//            val headerDetails = HeaderDetails()
//            headerDetails.citationNumber = mIssuranceModel?.citationData?.ticketNumber.nullSafety()
//            headerDetails.timestamp = mIssuranceModel?.citationData?.ticketDate.nullSafety()
//            createTicketRequest.headerDetails = headerDetails
//            createTicketRequest.lprNumber =
//                mIssuranceModel?.citationData?.vehicle?.licensePlate.nullSafety()
//            createTicketRequest.code =
//                mIssuranceModel?.citationData?.voilation?.code.nullSafety()
//            createTicketRequest.hearingDate =
//                mIssuranceModel?.citationData?.hearingDate //mIssuranceModel.getCitationData().getCode());
//            createTicketRequest.ticketNo = mIssuranceModel?.citationData?.ticketNumber.nullSafety()
//
//            if(mIssuranceModel?.citationData?.ticketType!=null && !mIssuranceModel?.citationData?.ticketType!!.isEmpty())
//            {
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true)) {
//
//                    ticketTypeValue.append("Warning")
//                  }else{
//                    ticketTypeValue.append(mIssuranceModel?.citationData?.ticketType.nullSafety())
//                   }
//            }
//            if(mIssuranceModel?.citationData?.ticketType2!=null && !mIssuranceModel?.citationData?.ticketType2!!.isEmpty()) {
//                if (ticketTypeValue.isEmpty()) {
//                    ticketTypeValue.append(mIssuranceModel?.citationData?.ticketType2.nullSafety())
//                } else {
//                    ticketTypeValue.append(", " + mIssuranceModel?.citationData?.ticketType2.nullSafety())
//                }
//            }
//            if(mIssuranceModel?.citationData?.ticketType3!=null && !mIssuranceModel?.citationData?.ticketType3!!.isEmpty())
//            {
//                if(ticketTypeValue.isEmpty())
//                {
//                    ticketTypeValue.append(mIssuranceModel?.citationData?.ticketType3.nullSafety())
//                }else {
//                    ticketTypeValue.append(", "+mIssuranceModel?.citationData?.ticketType3.nullSafety())
//                }
//            }
//            createTicketRequest.type = ticketTypeValue.toString()
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,true)){
//                createTicketRequest.timeLimitEnforcementObservedTime  = mIssuranceModel?.citationData?.officer?.observationTime
//            }else {
//                createTicketRequest.timeLimitEnforcementObservedTime =  sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
//            }
//
////            createTicketRequest.imageUrls = mImages
//            createTicketRequest.imageUrls = mCitaionImagesLinks
//            createTicketRequest.notes = mIssuranceModel?.citationData?.locationNotes.nullSafety()
//            createTicketRequest.status = "Valid"
//            createTicketRequest.citationStartTimestamp =
//                mIssuranceModel?.citationData?.startTime.nullSafety()
//            createTicketRequest.citationIssueTimestamp =
//                mIssuranceModel?.citationData?.issueTime.nullSafety()
//            createTicketRequest.isReissue =
//                sharedPreference.read(SharedPrefKey.isReissueTicket, "true")
//                    .equals("true", ignoreCase = true)
//
//            createTicketRequest.isTimeLimitEnforcement =
//                sharedPreference.read(SharedPrefKey.isTimeLimitEnforcement, "true").equals(
//                    "true",
//                    ignoreCase = true
//                )
//
//            createTicketRequest.timeLimitEnforcementId =
//                mIssuranceModel?.citationData?.timingId.nullSafety()
//            if (mLat.nullSafety() <= 0 || mLong.nullSafety() <= 0) {
//                mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
//                mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
//            }
//            createTicketRequest.mLatitude = mLat
//            createTicketRequest.mLongitiude = mLong
//            createTicketRequest.printQuery = AppUtils.printQueryStringBuilder.toString()

            //New Fresh Impl : Janak
            val timeLimitEnforcementObservedTime = if (!addTimeLimitEnforcementTimeFromSharedPreferenceInCreateTicket()) {
                mIssuranceModel?.citationData?.officer?.observationTime
            } else {
                sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
            }

            val createTicketRequest = RequestHandler.getCreateCitationTicketRequest(
                welcomeForm = mWelcomeFormData,
                insuranceModel = mIssuranceModel,
                mImages = mCitaionImagesLinks,
                isReissue = sharedPreference.read(SharedPrefKey.isReissueTicket, "true")
                    .equals("true", ignoreCase = true),
                isTimeLimitEnforcement = sharedPreference.read(
                    SharedPrefKey.isTimeLimitEnforcement,
                    "true"
                ).equals(
                    "true",
                    ignoreCase = true
                ),
                timeLimitEnforcementObservedTime = timeLimitEnforcementObservedTime.nullSafety(),
                lat = mLat,
                lng = mLong,
                printQuery = AppUtils.printQueryStringBuilder.toString()
            )
            mCreateTicketViewModel?.hitCreateTicketApi(createTicketRequest)

            try {
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"------------PREVIEW Create API-----------------")
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"REQUEST: "+ObjectMapperProvider.instance.writeValueAsString(createTicketRequest))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            mDb?.dbDAO?.updateCitationUploadStatus(1, mCitationNumberId)
            mIssuranceModel = mDb?.dbDAO?.getCitationWithTicket(mCitationNumberId)
            moveNextWithId("none")

            LogUtil.printToastMSG(
                this@LprPreviewActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun moveToTicketDetails() {
        try {
            //delete temporary images list
            mDb?.dbDAO?.deleteTempImages()
            mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
            val fname = mCitationNumberId + "_"+FILE_NAME_FACSIMILE_PRINT_BITMAP+".jpg"
            mPath = mPath + Constants.CAMERA + "/" + fname
            val addPrinterImage = CitationImagesModel()
            addPrinterImage.citationImage = mPath
            //addPrinterImage.set();
            bannerList.add(addPrinterImage)
            bannerListForUpload.add(addPrinterImage)

            //Use this if you want static header footer code
//            if (BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_SURF_CITY,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
//                    ignoreCase = true
//                ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
//                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)
//                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
//                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
//            ) {
//                //Upload image with Header Header
//                val path = getFilePathOfTicketWithHeaderFooter(
//                    context = this@LprPreviewActivity,
//                    imagePostFix = FILE_NAME_HEADER_FOOTER_BITMAP,
//                    citationNumber = mCitationNumberId.nullSafety(),
//                    path = mPath.nullSafety()
//                )
//
//                if (path.isNotEmpty()) {
//                    val addPrinterImageUpload = CitationImagesModel()
//                    addPrinterImageUpload.citationImage = path
//                    bannerListForUpload.add(addPrinterImageUpload)
//                }
//            }


            //Use this if you want dynamic header footer code
            if (showAndEnableHeaderFooterInFacsimile && checkHeaderImageFileExist() && checkFooterImageFileExist() && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DUNCAN,
                    ignoreCase = true
                )
            ) {
                //Upload image with Header Header
                val pathOfTicketWithHeaderFooter = getFilePathOfTicketWithHeaderFooterDynamic(
                    imagePostFix = FILE_NAME_HEADER_FOOTER_BITMAP,
                    citationNumber = mCitationNumberId.nullSafety(),
                    path = mPath.nullSafety()
                )

                if (pathOfTicketWithHeaderFooter.isNotEmpty()) {
                    val addPrinterImageUpload = CitationImagesModel()
                    addPrinterImageUpload.citationImage = pathOfTicketWithHeaderFooter
                    bannerListForUpload.add(addPrinterImageUpload)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            //TODO OCR image path
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)) {
                //delete temporary images list
//            mDb?.dbDAO?.deleteTempImages()
                mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
                val fname = mCitationNumberId +"_"+FILE_NAME_FACSIMILE_OCR_BITMAP+ ".jpg"
                mPath = mPath + Constants.CAMERA + "/" + fname
                val addPrinterImage = CitationImagesModel()
                addPrinterImage.citationImage = mPath
                //addPrinterImage.set();
                bannerList.add(addPrinterImage)
                bannerListForUpload.add(addPrinterImage)

                //Use this if you want static header footer code
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)) {
//                    //Upload image with Header Header
//                    val pathOfTicketWithHeaderFooter = getFilePathOfTicketWithHeaderFooter(
//                        context = this@LprPreviewActivity,
//                        imagePostFix = FILE_NAME_HEADER_FOOTER_BITMAP,
//                        citationNumber = mCitationNumberId.nullSafety(),
//                        path = mPath.nullSafety()
//                    )
//
//                    if (pathOfTicketWithHeaderFooter.isNotEmpty()) {
//                        val addPrinterImageUploadWithHeaderFooter = CitationImagesModel()
//                        addPrinterImageUploadWithHeaderFooter.citationImage =
//                            pathOfTicketWithHeaderFooter
//                        bannerListForUpload.add(addPrinterImageUploadWithHeaderFooter)
//                    }
//                }

                //Use this if you want dynamic header footer code
                if (showAndEnableHeaderFooterInFacsimile && checkHeaderImageFileExist() && checkFooterImageFileExist() && BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_DUNCAN,
                        ignoreCase = true
                    )
                ) {
                    //Upload image with Header Header
                    val pathOfTicketWithHeaderFooter = getFilePathOfTicketWithHeaderFooterDynamic(
                        imagePostFix = FILE_NAME_HEADER_FOOTER_BITMAP,
                        citationNumber = mCitationNumberId.nullSafety(),
                        path = mPath.nullSafety()
                    )

                    if (pathOfTicketWithHeaderFooter.isNotEmpty()) {
                        val addPrinterImageUploadWithHeaderFooter = CitationImagesModel()
                        addPrinterImageUploadWithHeaderFooter.citationImage =
                            pathOfTicketWithHeaderFooter
                        bannerListForUpload.add(addPrinterImageUploadWithHeaderFooter)
                    }
                }
            }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        val isBookletExits = mDb?.dbDAO?.getCitationBooklet(0)
        if (isBookletExits!!.size < 3) {
            callCitationNumberApi()
        }
        if (bannerListForUpload.size == 0) {
            callCreateTicketApi()
        } else {
            if (bannerListForUpload.size>= 0) {
                imageUploadSuccessCount = mCitaionImagesLinks!!.size
                callBulkImageUpload()
//                For Signature upload
//                if(File(mSignaturePath!!).exists()){
//                    callSignatureBulkImageUpload()
//                }
//                callUploadImages(File(bannerList[0].citationImage), mCitaionImagesLinks.size)
            }
        }
        //dismissLoader();
    }

    /*perform click actions*/
    @OnClick(
        R.id.btnPrint,
        R.id.btnEdit,
        R.id.btnCancel,
        R.id.ivEditLocation,
        R.id.ivEditVoilation,
        R.id.ivEditRemark,
        R.id.ivEditNotes,
        R.id.ivEditCitation,
        R.id.ivEditVehicle,
        R.id.btn_rescind,
        R.id.btnWithoutPrint,
        R.id.btn_cancel_offline_citation
    )
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btnPrint -> {
                btnPrint.disableButton()
                BluetoothPermissionUtil.checkBluetoothPermissions(
                    activity = this,
                    permissionLauncher = bluetoothPermissionLauncher
                ) {
                    /**If setting file getting true then we are checking similar citation response if
                    success then move to next screen otherwise stay same screen*/
                    if (isSimilarCitationAPIErrorPopupDisplay) {
                        if (isSimilarCitationAPIRetrunsSuccess) {
                            isSkipPrinter = false
                            mDb?.dbDAO?.updateCitationUploadStatus(1, mCitationNumberId)
                            callSimilarCitationCheckPopUp()
                        } else {
                            showCustomAlertDialog(
                                mContext, APIConstant.SIMILAR_CITATION_CHECK,
                                getString(R.string.err_msg_similar_citation_check_api_failed),
                                getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this
                            )
                        }
                    } else {
                        isSkipPrinter = false
                        mDb?.dbDAO?.updateCitationUploadStatus(1, mCitationNumberId)
                        callSimilarCitationCheckPopUp()
                    }
                }
//                btnPrint.enableButton()
            }
            R.id.btnWithoutPrint -> {
                isSkipPrinter = true
                mDb?.dbDAO?.updateCitationUploadStatus(1, mCitationNumberId)
                callSimilarCitationCheckPopUp()
            }
            R.id.btnEdit -> {
                val dataEdit = Intent()
                dataEdit.putExtra("edit", "edit_form")
                setResult(RESULT_OK, dataEdit)
                finish()
            }
            R.id.btn_rescind, R.id.btnCancel -> showRescindDialog("")
            R.id.ivEditLocation -> {
                /*mTvLocationDetails.setEnabled(true);
                mTvLocationDetails.requestFocus();
                AppUtils.showKeyboard(mContext);
                if (mTvLocationDetails.getText() != null) {
                    mTvLocationDetails.setSelection(mTvLocationDetails.getText().length());
                }*/
                val data = Intent()
                data.putExtra("edit", "location")
                setResult(RESULT_OK, data)
                finish()
            }
            R.id.ivEditVoilation -> {
                val dataVoi = Intent()
                dataVoi.putExtra("edit", "voilation")
                setResult(RESULT_OK, dataVoi)
                finish()
            }
            R.id.ivEditVehicle -> {
                val dataVeh = Intent()
                dataVeh.putExtra("edit", "vehicle")
                setResult(RESULT_OK, dataVeh)
                finish()
            }
            R.id.ivEditRemark -> {
                val dataOth = Intent()
                dataOth.putExtra("edit", "remark")
                setResult(RESULT_OK, dataOth)
                finish()
            }
            R.id.ivEditNotes -> {
                val dataNote = Intent()
                dataNote.putExtra("edit", "note")
                setResult(RESULT_OK, dataNote)
                finish()
            }
            R.id.ivEditCitation -> {
                if (bannerList!!.size > 0) {
                    for (i in bannerList.indices) {
                        if (!isEditImageSection) {
                            bannerList.get(i).edit = 1;
                        } else {
                            bannerList.get(i).edit = 0;
                        }
                    }
                    imageAdapter!!.notifyDataSetChanged()
                    isEditImageSection = !isEditImageSection
                }
            }
            R.id.btn_cancel_offline_citation -> {
                mRescindButton = "true"
                mTicketLable = "Cancelled"
                cancelledStatus = "Cancelled"
                mTicketActionButtonEvent = "Cancelled"
                mRescindReason = "Cancelled"
                mRescindNote = "The officer decided not to upload the citation, so it was marked as 'Cancelled' on the preview screen."
                isSkipPrinter = true


                generateFacsimileImage()
                mainScope.launch {
                    addSomeTweakBeforeCommandBasedFacsimile()
                    delay(SAVEPRINTBITMAPDELAYTIME)
                    loadBitmapFromView(linearLayoutMainPrint, false) //layOfficerDetails);
                    val lockLprModel = LockLprModel()
                    lockLprModel.mLprNumber = ""
                    lockLprModel.mMake = ""
                    lockLprModel.mModel = ""
                    lockLprModel.mColor = ""
                    lockLprModel.mAddress = ""
                    lockLprModel.mViolationCode = ""
                    lockLprModel.ticketCategory = ""
                    setLprLock(lockLprModel, this@LprPreviewActivity, sharedPreference)
                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                }
//                callCreateTicketApi()
            }
        }
    }

    private fun moveNextScreen() {
        Log.e("print 2", "print button click")
        showProgressLoader(getString(R.string.scr_message_please_wait))
//        createViewToPrint()
        generateFacsimileImage()
        mainScope.launch {
            addSomeTweakBeforeCommandBasedFacsimile()
            delay(SAVEPRINTBITMAPDELAYTIME)
            mRescindButton = "false"
            //                mTicketLable = "Valid";
            loadBitmapFromView(linearLayoutMainPrint,false) //layOfficerDetails);
            val lockLprModel = LockLprModel()
            lockLprModel.mLprNumber = ""
            lockLprModel.mMake = ""
            lockLprModel.mModel = ""
            lockLprModel.mColor = ""
            lockLprModel.mAddress = ""
            lockLprModel.mViolationCode = ""
            lockLprModel.ticketCategory = ""
            setLprLock(lockLprModel, this@LprPreviewActivity, sharedPreference)
            sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
            btnPrint.enableButton()
        }
    }

    private fun setDeleteOption(imgView: AppCompatImageView?, imgDelete: AppCompatImageView?) {
        try {
            if (imgView!!.drawable.constantState != null) {
                if (imgView.drawable.constantState != resources.getDrawable(R.drawable.no_image).constantState) {
                    imgDelete!!.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun moveToNext() {
        finish()
        val mIntent = Intent(this, LprDetails2Activity::class.java)
        mIntent.putExtra("make", mTvOfficerMake!!.text.toString())
        mIntent.putExtra("from_scr", "lpr_details")
        mIntent.putExtra("model", mTvOfficerModel!!.text.toString())
        mIntent.putExtra("color", mTvOfficerColor!!.text.toString())
        mIntent.putExtra(
            "lpr_number",
            mIssuranceModel!!.citationData!!.vehicle!!.licensePlate.toString()
        )
        startActivity(mIntent)
    }

    override fun onBackPressed() {
        if (backpressCloseDrawer()) {
            // close activity when drawer is closed
//            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        if(sharedPreference.read(
                SharedPrefKey.CITATION_LOCK_PARTIALLY_LOCK,"").equals("unlock")){
            sharedPreference.write(
                SharedPrefKey.LOCKED_LPR_BOOL, true)
        }
        dismissLoader()
    }


    override fun onStop() {

        super.onStop()
        dismissLoader()
        // TODO LOCK BY CODE
//        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)) {

       /* settingsList = null
        mWelcomeFormData = null
        bannerList.clear()*/

        /**
         * Added a tablet check condition here, because in tablet on stop in getting called with a delay & it is creating a problem
         * Due to this, it is locking the LPR bool and due to this, back arrow, hemburger menu stopped working
         */
        if (!AppUtils.isTablet(this@LprPreviewActivity)){
            if(sharedPreference.read(
                    SharedPrefKey.CITATION_LOCK_PARTIALLY_LOCK,"").equals("unlock")){
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, true)
            }
        }

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            zebraPrinterUseCase?.disconnect()
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.POST_CREATE_TICKET, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateTicketResponse::class.java)

                            try {
                                ApiLogsClass.writeApiPayloadTex(
                                    BaseApplication.instance?.applicationContext!!,
                                    "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(responseModel)
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            if (responseModel != null && responseModel.success == true) {
                                lifecycleScope.launch {
                                    try {
                                        // ✅ Do all DB work on IO thread
                                        withContext(Dispatchers.IO) {
                                            // update citation + facsimile citationId
                                            mDb!!.dbDAO!!.updateCitationUploadStatus(0, mCitationNumberId)
                                            mDb!!.dbDAO!!.updateFacsimileUploadCitationId(
                                                responseModel.data!!.id.toString(),
                                                mIssuranceModel!!.citationData!!.ticketNumber!!
                                            )

                                            // save images if available
                                            bannerList?.let { list ->
                                                if (list.size == 1) list.removeAt(0)
                                                if (list.size > 1) {
                                                    list.removeAt(list.size - 1)
                                                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
                                                        BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
                                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
                                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
                                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true) ||
                                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
                                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
                                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
                                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true) ||
                                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)
                                                    ) {
                                                        list.removeAt(list.size - 1)
                                                    }

                                                    // save remaining images
                                                    list.forEachIndexed { i, item ->
                                                        try {
                                                            val timeStamp = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
                                                            val mImage = CitationImagesModel().apply {
                                                                citationImage = item.citationImage
                                                                id = timeStamp.toInt() + i
                                                                status = item.status
                                                                timeImagePath = item.timeImagePath
                                                            }
                                                            getMyDatabase()?.dbDAO!!.insertCitationImage(mImage)
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                    }
                                                }
                                            }

                                            // facsimile status update
                                            for (image in responseModel.data!!.images!!) {
                                                if (image.contains("_${FILE_NAME_FACSIMILE_PRINT_BITMAP}.jpg")) {
                                                    try {
                                                        isFacsimileUploaded = true
                                                        unUploadFacsimileImage?.dateTime?.let {
                                                            getMyDatabase()?.dbDAO!!.updateFacsimileStatus(
                                                                1,
                                                                mIssuranceModel!!.citationData!!.ticketNumber!!.toString(),
                                                                it
                                                            )
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                            }
                                        } // end withContext(IO)

                                        // ✅ All DB work done → update UI safely
                                        linearLayoutMainPrint.visibility = View.GONE
                                        linearLayoutMainScreenView.visibility = View.VISIBLE
                                        btnPrint.visibility = View.VISIBLE
                                        layButtonsHide.visibility = View.VISIBLE

                                        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
                                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
                                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)
                                        ) {
                                            laySignature.visibility = View.VISIBLE
                                        }

                                        mTicketId = responseModel.data!!.id
                                        if (mRescindButton.equals("true", ignoreCase = true)) {
                                            callTicketRescindApi(mTicketId, mRescindNote, mRescindReason)
                                        } else {
                                            moveNextWithId(responseModel.data!!.id)
                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                dismissLoader()
                                lastSecondTag = ""
                                showCustomAlertDialog(
                                    mContext,
                                    APIConstant.POST_CREATE_TICKET,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_CITATION_NUMBER, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CitationNumberResponse::class.java)

                            if (responseModel != null && responseModel.status!!) {
                                saveBookletWithStatus(responseModel.data!![0])
//                                printToastMSG(
//                                    this@LprPreviewActivity,
//                                    responseModel.message + responseModel.data!![0].metadata.toString()
//                                )
                            } else {
                                dismissLoader()
                                lastSecondTag = ""
                                showCustomAlertDialog(mContext,
                                    APIConstant.POST_CITATION_NUMBER,
                                        responseModel.data!!.get(0)!!.metadata.toString(),
                                    getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this)
                            }
                        }
                        if (tag.equals(DynamicAPIPath.POST_CANCEL, ignoreCase = true)) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TicketCancelResponse::class.java)

                            try {
                                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"RESPONSE CANCEL API: "+ObjectMapperProvider.instance.writeValueAsString(responseModel))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (responseModel != null && responseModel.success!!) {
                                if (mRescindButton.equals("true", ignoreCase = true)) {
//                                    mDb!!.dbDAO!!.deleteOfflineRescindCitation(mIssuranceModel!!.citationData!!.ticketNumber.toString())
                                    OfflineCancelCitationModel!!?.let { mDb!!.dbDAO!!.deleteOfflineRescindCitation(it) }
                                }
                                moveNextWithId(mTicketId)
                            } else {
                                dismissLoader()
                                lastSecondTag = ""
                                showCustomAlertDialog(mContext, APIConstant.POST_RESCIND,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),getString(R.string.scr_btn_cancel),this)
                            }
                        }
                        try {
                            if (tag.equals( DynamicAPIPath.GET_LAST_SECOND_CHECK,
                                    ignoreCase = true)) {
                                 responseLastSecondCheckModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LastSecondCheckResponse::class.java)
                            }
                            if (tag.equals(DynamicAPIPath.SIMILAR_CITATION_CHECK,
                                    ignoreCase = true)) {
                                isSimilarCitationAPIRetrunsSuccess = true

                                responseSimilarCitationCheckModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), SimilarCitationResponse::class.java)

                                if (sharedPreference.read(
                                                SharedPrefKey.LAST_SECOND_CHECK,false)) {
                                    callLastSecondCheckAPI()
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dismissLoader()
                        }
                    } catch (e: Exception) {
                        dismissLoader()
                        e.printStackTrace()
                        printToastMSG(this, e.message)
                        if (tag.equals(DynamicAPIPath.POST_IMAGE, ignoreCase = true)) {
                            showCustomAlertDialog(mContext, APIConstant.POST_IMAGE,
                                getString(R.string.err_msg_something_went_wrong),
                                getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel),this )
                        }
                        if (tag.equals(DynamicAPIPath.POST_CREATE_TICKET, ignoreCase = true)) {
                            mDb!!.dbDAO!!.updateCitationUploadStatus(1, mCitationNumberId)
                            mIssuranceModel = mDb!!.dbDAO!!.getCitationWithTicket(mCitationNumberId)

                            if (bannerList != null) {
                                if (bannerList.size == 1) {
                                    bannerList.removeAt(0)
                                }
                                if (bannerList.size > 1) {
                                    bannerList.removeAt(bannerList.size - 1)
                                    //TODO OCR image remove from list
                                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)||
                                        BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true)||
                                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)) {
                                        bannerList.removeAt(bannerList.size - 1)
                                    }
                                    var i = 0
                                    while (i < bannerList.size) {
                                        val mImage = CitationImagesModel()
                                        mImage.citationImage =
                                                bannerList[i].citationImage
                                        mImage.id = i
                                        mImage.status = bannerList[i].status
                                        mImage.timeImagePath = bannerList[i].timeImagePath
                                        getMyDatabase()?.dbDAO?.insertCitationImage(mImage)
                                        i++
                                    }
                                }
                            }

                            moveNextWithId("none")
                        }
                    }
                }
            }
            Status.ERROR -> {
                if (tag.equals(DynamicAPIPath.POST_CREATE_TICKET, ignoreCase = true)) {
                    mDb!!.dbDAO!!.updateCitationUploadStatus(1, mCitationNumberId)
                    mIssuranceModel = mDb!!.dbDAO!!.getCitationWithTicket(mCitationNumberId)

                    if (bannerList != null) {
                        if (bannerList.size == 1) {
                            bannerList.removeAt(0)
                        }
                        if (bannerList.size > 1) {
                            bannerList.removeAt(bannerList.size - 1)
                            //TODO OCR image remove from list
                            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)||
                                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)) {
                                bannerList.removeAt(bannerList.size - 1)
                            }
                            var i = 0
                            while (i < bannerList.size) {
                                val mImage = CitationImagesModel()
                                mImage.citationImage = bannerList[i].citationImage
                                mImage.id = i
                                mImage.status = bannerList[i].status
                                mImage.timeImagePath = bannerList[i].timeImagePath
                                getMyDatabase()?.dbDAO?.insertCitationImage(mImage)
                                i++
                            }
                        }
                    }
                    moveNextWithId("none")
                } else if (tag.equals(DynamicAPIPath.SIMILAR_CITATION_CHECK, ignoreCase = true)) {
                    if (sharedPreference.read(SharedPrefKey.LAST_SECOND_CHECK, false)
                    ) {
                        callLastSecondCheckAPI()
                    } else {
//                        moveNextScreen()
                    }
                    isSimilarCitationAPIRetrunsSuccess = false
                    showCustomAlertDialog(mContext, "SIMILARCITATIONAPIERROR",
                        getString(R.string.err_msg_similar_citation_check_api_failed),
                        getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel),this)
                } else if (tag.equals(DynamicAPIPath.POST_IMAGE, ignoreCase = true)) {
                    lastSecondTag =  DynamicAPIPath.POST_IMAGE
                    showCustomAlertDialog(mContext, APIConstant.POST_IMAGE,
                        getString(R.string.err_msg_something_went_wrong),
                        getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel),this)
                }
                dismissLoader()
                try {
                    ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"---------Response ERROR--------"+tag)
                    ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"ERROR : "+tag+" :- "+apiResponse!!.error!!.message)
                    ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"ERROR : "+tag+" :- "+apiResponse!!.data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            else -> {}
        }
    }

    private fun moveNextWithId(mTicketId: String?) {
        try {

                val mIntent = Intent(this@LprPreviewActivity, TicketDetailsActivity::class.java)
                showProgressLoader(getString(R.string.scr_message_please_wait))
                mainScope.launch {
                    delay(500)
                    try {
                        CoroutineScope(Dispatchers.IO).async {
                            mRescindButton = "false"
                            if (mRescindReason.equals("PBC Cancel", true)) {
                                mTicketLable = "Cancelled"
                                m4ButtonType = "pbc_cancel"
                            } else if (mRescindReason.equals("Rescind", true) ||
                                mRescindReason.equals("CITIZEN APPROACHED", true)
                            ) {
                                mTicketLable = "Rescind"
                            } else if (mRescindReason.equals("Same Citation", true)) {
                                mTicketLable = "Cancelled"
                                m4ButtonType = "similar_citation_cancel"
                            } else if (mRescindReason.equals("Cancelled", true)) {
                                mTicketLable = "Cancelled"
                            } else {
                                mTicketLable = "Valid"
                            }
                            val mLocationUpdateRequest = LocUpdateRequest()
                            mLocationUpdateRequest.activityType = "LocationUpdate"
                            mLocationUpdateRequest.logType = "NodePort"
                            mLocationUpdateRequest.locationUpdateType = "citation"
                            mLocationUpdateRequest.latitude = mLat
                            mLocationUpdateRequest.longitude = mLong
                            var Zone = "CST"
//                    val modelZone = mDb?.dbDAO?.getDataset()
//                    if (modelZone != null && modelZone.dataset!!.settingsList != null) {
//                        Zone = modelZone.dataset?.settingsList!![0].mValue.nullSafety()
//                    }
//                    val timeMilli1 = getClientTimeStamp(Zone)
                            //        mLocationUpdateRequest.setClientTimestamp(timeMilli1);
                            mLocationUpdateRequest.clientTimestamp = splitDateLpr(Zone)

                            mIntent.putExtra("booklet_id", mCitationNumberId.toString())
                            mIntent.putExtra("ticket_id", mTicketId.toString())
                            mIntent.putExtra("from_scr", "preview")
                            mIntent.putExtra("image_size", 0)
                            mIntent.putExtra("event", mRescindButton)
                            mIntent.putExtra("ticket_status", mTicketLable)
                            mIntent.putExtra(
                                "ticket_number",
                                mIssuranceModel!!.citationData!!.ticketNumber.toString()
                            )
                            mIntent.putExtra(
                                "ticket_date",
                                mIssuranceModel!!.citationData!!.ticketDate.toString()
                            )
                            mIntent.putExtra(
                                "make",
                                mIssuranceModel!!.citationData!!.vehicle!!.make.toString()
                            )
                            mIntent.putExtra(
                                "model",
                                mIssuranceModel!!.citationData!!.vehicle!!.model.toString()
                            )
                            mIntent.putExtra(
                                "color",
                                mIssuranceModel!!.citationData!!.vehicle!!.color.toString()
                            )
                            mIntent.putExtra(
                                "state",
                                mIssuranceModel!!.citationData!!.vehicle!!.state.toString()
                            )
                            mIntent.putExtra(
                                "lpr_number",
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate.toString()
                            )
                            mIntent.putExtra(
                                "expire_date",
                                mIssuranceModel!!.citationData!!.vehicle!!.expiration.toString()
                            )
                            var code: String? = ""
                            var desc: String? = ""
                            var fine = ""
                            var lateFine = ""
                            var due15 = ""
                            var due30 = ""
                            var due45 = ""
                            var vinNumber: String? = ""
                            var lot: String? = ""
                            var street = ""
                            var block: String? = ""
                            var side = ""
                            var sideText = ""
                            var meter = ""
                            var zone = ""
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.lot)) {
                                lot = mIssuranceModel!!.citationData!!.location!!.lot
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.street)) {
                                street =
                                    SPACE_WITH_COMMA + " " + mIssuranceModel!!.citationData!!.location!!.street
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.block)) {
                                block = mIssuranceModel!!.citationData!!.location!!.block
                            }

                            /* if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)){
                        sideText = SPACE_WITH_COMMA+"Direction:"
                    }else{
                        sideText = SPACE_WITH_COMMA+"Side:"
                    }*/
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.direction)) {
                                side =
                                    mIssuranceModel!!.citationData!!.location!!.directionLabel + " " + mIssuranceModel!!.citationData!!.location!!.direction
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.side)) {
                                side =
                                    mIssuranceModel!!.citationData!!.location!!.directionLabel + " " + mIssuranceModel!!.citationData!!.location!!.side
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.meterName)) {
                                meter =
                                    SPACE_WITH_COMMA + "Meter #" + mIssuranceModel!!.citationData!!.location!!.meterName
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.officer!!.zone)) {
                                zone =
                                    SPACE_WITH_COMMA + "" + mIssuranceModel!!.citationData!!.officer!!.zone
                            }

//        mIntent.putExtra("print_bitmap",byteArray);
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.pcbZone)) {
                                zone =
                                    SPACE_WITH_COMMA + "" + mIssuranceModel!!.citationData!!.location!!.pcbZone
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.code)) {
                                code = mIssuranceModel!!.citationData!!.voilation!!.code
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.locationDescr)) {
                                desc =
                                    SPACE_WITH_COMMA + "" + mIssuranceModel!!.citationData!!.voilation!!.locationDescr

                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.amount)) {
                                fine =
                                    SPACE_WITH_COMMA + "Fine:$" + mIssuranceModel!!.citationData!!.voilation!!.amount
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.amountDueDate)) {
                                lateFine =
                                    SPACE_WITH_COMMA + "Late fine:$" + mIssuranceModel!!.citationData!!.voilation!!.amountDueDate
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.dueDate)) {
                                due15 =
                                    SPACE_WITH_COMMA + mIssuranceModel!!.citationData!!.voilation!!.dueDateLabel + " :$" + mIssuranceModel!!.citationData!!.voilation!!.dueDate
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.dueDate30)) {
                                due30 =
                                    SPACE_WITH_COMMA + mIssuranceModel!!.citationData!!.voilation!!.dueDate30Label + " :$" + mIssuranceModel!!.citationData!!.voilation!!.dueDate30
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.voilation!!.dueDate45)) {
                                due45 =
                                    SPACE_WITH_COMMA + mIssuranceModel!!.citationData!!.voilation!!.dueDate30Label + " :$" + mIssuranceModel!!.citationData!!.voilation!!.dueDate45
                            }
                            if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.vehicle!!.vinNumber)) {
                                vinNumber =
                                    mIssuranceModel!!.citationData!!.vehicle!!.vinNumber.toString();
                            }
                            mIntent.putExtra("meter_", side + meter + zone)
                            mIntent.putExtra("vinNumber_", vinNumber)
                            mIntent.putExtra(
                                "fineAmount_",
                                mIssuranceModel!!.citationData!!.voilation!!.amount
                            )
                            mIntent.putExtra(
                                "space_",
                                mIssuranceModel!!.citationData!!.location!!.spaceName
                            )
                            mIntent.putExtra(
                                "lot_",
                                mIssuranceModel!!.citationData!!.location!!.lot
                            )
                            mIntent.putExtra(
                                "bodystyle_",
                                mIssuranceModel!!.citationData!!.vehicle!!.bodyStyle
                            )
                            mIntent.putExtra("address_", block + street)
                            mIntent.putExtra(
                                "voilation_details",
                                code + desc + fine + lateFine + due15 + due30 + due45
                            )
                            mIntent.putExtra("voilation_description", mViolationDescription)
                            mIntent.putExtra("print_bitmap", printBitmap)
                            mIntent.putExtra("ticket_type", ticketTypeValue.toString())
                            if (!isFacsimileUploaded) {
                                mIntent.putExtra(
                                    "Citation_Images_Link",
                                    mCitaionImagesLinks as ArrayList<String>
                                )
                            }
                            //Convert to byte array
                            /**
                             * Save All images in db to sync next time
                             */
                            mainScope.async {
                                callLocationApi(mLocationUpdateRequest)
                                seveImageInOfflineDB()
                                dismissLoader()
                            }
//                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            startActivityForResult(mIntent, 2020)
                            finish()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    //save Booklet with status in Databse
    private fun saveBookletWithStatus(mResponse: CitationNumberData) {
        class SaveBookletTask : AsyncTask<Void?, Int?, CitationNumberDatabaseModel?>() {
            override fun doInBackground(vararg voids: Void?): CitationNumberDatabaseModel {
                val citationBookletModelList: MutableList<CitationBookletModel> = ArrayList()
                val mData = CitationNumberDatabaseModel()
                try {
                    for (i in mResponse.response!!.citationBooklet!!.indices) {
                        if (mResponse.response!!.citationBooklet!![i] != null) {
                            val bookletModel = CitationBookletModel()
                            bookletModel.citationBooklet = mResponse.response!!.citationBooklet!![i]
                            bookletModel.mStatus = 0
                            if (bookletModel != null) {
                                citationBookletModelList.add(bookletModel)
                            }
                        }
                    }
                    mDb!!.dbDAO!!.insertCitationBooklet(citationBookletModelList)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return mData
            }

            override fun onPostExecute(result: CitationNumberDatabaseModel?) {
                val isBookletExits = mDb!!.dbDAO!!.getCitationBooklet(0)
                printToastMSG(mContext, "Booklet saved - " + isBookletExits!!.size)
                //getString(R.string.msg_new_citation_added);
                printLog("Booklet saved -", isBookletExits.size)
            }
        }
        SaveBookletTask().execute()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2020) {
            var requiredText: String? = ""
            try {
                requiredText = data!!.extras!!.getString("edit")
                if (requiredText != null) {
                    if (requiredText == "edit_note") {
                        mImageViewEditVehicle.isClickable = false
                        mTextInputVoilation.isClickable = false
                        mImageViewEditLocation.isClickable = false
                        mImageViewRemark.isClickable = false
                        mImageViewCitation.isClickable = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun capturePicture() {
    }

    fun addSomeTweakBeforeCommandBasedFacsimile() {
//        try {
        if (LogUtil.isEnableCommandBasedFacsimile) {
            linearLayoutCompatChildContainer.invisibleView()
//            relativeLayoutPrintAmountSection.invisibleView()
//            relativeLayoutPrintStateSection.invisibleView()
//            relativeLayoutPrintCitationSection.invisibleView()
//            relativeLayoutPrintPhilaSection.invisibleView()
        }
//            else {
////                SaveFacsimleImage(b, mCitationNumberId + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
//            }
//        } catch (e: Exception) {
////            SaveFacsimleImage(b, mCitationNumberId + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
//        }
    }

    fun loadBitmapFromView(view: View?,isOCR: Boolean) {
        try {
            val b = Bitmap.createBitmap(view!!.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(b)
            view.draw(canvas)

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                (BuildConfig.FLAVOR.equals(DuncanBrandingApp13())&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA, ignoreCase = true))) {
                relativeLayoutPrintAmountSection!!.visibility = View.INVISIBLE
                relativeLayoutPrintStateSection!!.visibility = View.INVISIBLE
                relativeLayoutPrintCitationSection!!.visibility = View.INVISIBLE

                linearLayoutCompatQrCodeAndMessage!!.visibility = View.INVISIBLE
                relativeLayoutPrintPhilaSection!!.visibility = View.INVISIBLE
            }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)  ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)  ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)  ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)  ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true))
            {
                relativeLayoutPrintAmountSection!!.visibility = View.INVISIBLE
                relativeLayoutPrintStateSection!!.visibility = View.INVISIBLE
                relativeLayoutPrintCitationSection!!.visibility = View.INVISIBLE
                relativeLayoutPrintPhilaSection!!.visibility = View.INVISIBLE

            }else if((BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)) && !LogUtil.isEnableCommandBasedFacsimile)
            {
                relativeLayoutPrintAmountSection!!.visibility = View.VISIBLE
                relativeLayoutPrintStateSection!!.visibility = View.VISIBLE
                relativeLayoutPrintCitationSection!!.visibility = View.VISIBLE
                relativeLayoutPrintPhilaSection!!.visibility = View.VISIBLE
            }

            try {
                if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER && AppUtils.isSiteSupportCommandPrinting(getSettingFileValuesForCMDPrinting())){
                    ZebraCommandPrintUtils.getFromPrefAndSetToPrintComment(sharedPreference)
                    //Setting up extra parameter which should be there in print rather then officer, vehicle, violation, citation & comment details
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintHeaderAndLines(sharedPreference, AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setAppLogoInCommandPrint(this@LprPreviewActivity,sharedPreference, AppUtils.printQueryStringBuilder)
                    val lprBitmapFile = FileUtil.getLprImageFileFromBannerList(bannerList)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setLprImageInCommandPrint(getMyDatabase(), lprBitmapFile,sharedPreference, AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setSignatureInCommandPrint(this@LprPreviewActivity,sharedPreference, AppUtils.printQueryStringBuilder)

                    //Below statement we can get in old zebra class
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintCitationHeader(AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintQRCode(this@LprPreviewActivity, AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintQRCodeLabel( AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintBarCode( getMyDatabase(),mCitationNumberId.nullSafety(), AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setBottomAddressInCommand( AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYForAddressLines( AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYForLines(sharedPreference, AppUtils.printQueryStringBuilder)
                }


                if (LogUtil.isEnableCommandBasedFacsimile){
                    val bitmap = CanvasUtils.drawElementsToBitmapAutoSize(this@LprPreviewActivity, drawableElements)

//                    val imageView = ImageView(this).apply {
//                        layoutParams = LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT // height in pixels or use WRAP_CONTENT
//                        ).apply {
//                            setMargins(16, 16, 16, 16)
//                        }
//                        scaleType = ImageView.ScaleType.FIT_XY
//                        setImageBitmap(bitmap) // or setImageBitmap(bitmap)
//                    }

                    ivCommandBasedFacsimile.showView()
                    ivCommandBasedFacsimile.setImageBitmap(bitmap)

                    SaveFacsimleImage(bitmap, mCitationNumberId + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)

                    AppUtils.clearDrawableElementList()
                }else{
                    SaveFacsimleImage(b, mCitationNumberId + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
                }
            }catch (e:Exception){
                linearLayoutCompatChildContainer.showView()
//                relativeLayoutPrintAmountSection.showView()
//                relativeLayoutPrintStateSection.showView()
//                relativeLayoutPrintCitationSection.showView()
//                relativeLayoutPrintPhilaSection.showView()
                SaveFacsimleImage(b, mCitationNumberId + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
            }

            //TODO OCR visible for duncan
            if (checkBuildConfigForPreviewActivityLoadBitmapForSaveOCRImage()) {

                Handler().postDelayed({

                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SATELLITE,
                            ignoreCase = true
                        )||BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
                            ignoreCase = true
                        )||BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CFFB,
                            ignoreCase = true
                        )
                    ) {
                        //TODO: no need to anything when it is Satellite beach site
                    } else {
                        linearLayoutCompatQrCodeAndMessage!!.visibility = View.VISIBLE
                        mImageViewBarcodePrint!!.visibility = View.VISIBLE
                        if (!LogUtil.isEnableCommandBasedFacsimile && isOCRTextPrintlayout){
                            relativeLayoutPrintAmountSection.visibility = View.VISIBLE
                            relativeLayoutPrintStateSection.visibility = View.VISIBLE
                            relativeLayoutPrintCitationSection.visibility = View.VISIBLE
                            relativeLayoutPrintPhilaSection.visibility = View.VISIBLE
                        }
                    }

                    if(checkBuildConfigForLoadBitmapPreviewActivityForWhiteImageForPrint()) {
//                        val drawableBitmap = BitmapFactory.decodeResource(resources, R.drawable.white_print_840)
                        val drawableRes = if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)) {
                            R.drawable.white_print_hartford
                        } else {
                            R.drawable.white_print
                        }
                        val drawableBitmap = BitmapFactory.decodeResource(resources, drawableRes)
                        val result: Bitmap = drawableBitmap.copy(Bitmap.Config.ARGB_8888, true)
                        val canvas = Canvas(result)
//                        val X = sharedPreference.read(
//                            SharedPrefKey.QRCODE_FOR_PRINT_X,"")
//                        val Y = sharedPreference.read(
//                            SharedPrefKey.QRCODE_FOR_PRINT_Y,"")
//                        canvas.drawBitmap(bitmapQrCode!!, X!!.toFloat(), Y!!.toFloat(), null)

                        canvas.drawBitmap(result!!, 0.0f, 0.0f, null)

                        if(bannerList!=null && bannerList!!.size>0 &&
                            bannerList!!.get(0)!!.citationImage!!.contains("anpr_")&&
                            isPrintLprImageInCmdPrint()) {
                            val lprFilePath: File = File(bannerList!!.get(0)!!.citationImage)
                            val lprBitmap = BitmapFactory.decodeFile(lprFilePath.absolutePath)
                            val finalBitmap = drawableBitmap.combineBitmap(lprBitmap)
                            SaveImageOCRMM(finalBitmap, mCitationNumberId + "_" + FILE_NAME_FACSIMILE_OCR_BITMAP)
                        }else {
                            SaveImageOCRMM(drawableBitmap, mCitationNumberId + "_" + FILE_NAME_FACSIMILE_OCR_BITMAP)
                        }
                    }else {
                        val bitmap = Bitmap.createBitmap(
                            view!!.width, view.height,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        linearLayoutMainPrint.draw(canvas)
                        SaveImageOCRMM(bitmap, mCitationNumberId + "_" + FILE_NAME_FACSIMILE_OCR_BITMAP)
                    }
                }, 800)
            }

            mainScope.launch {
                delay(1000)
                if (mTicketActionButtonEvent == null ||
                    mTicketActionButtonEvent.equals("UnUpload", true)
                ) {
                    mTicketActionButtonEvent = ""
                } else if (mTicketActionButtonEvent.equals("VoidReissue", true)) {
                    mTicketActionButtonEvent = ""
                }
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13()))
                {
                    if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
                        starPrinterUseCase?.mPrintFacsimileImage(
                            savePrintImagePath!!,
                            this@LprPreviewActivity,
                            "previewScreen",
                            mTicketActionButtonEvent,
                            mCitationNumberId,
                            mFinalAmount,
                            mIssuranceModel!!.citationData!!.vehicle!!.state,
                            mIssuranceModel!!.citationData!!.vehicle!!.licensePlate,
                            isErrorUploading,
                            printcommand!!
                        )
                    } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
                        xfPrinterUseCase?.mPrintFacsimileImage(
                            savePrintImagePath!!,
                            this@LprPreviewActivity,
                            "previewScreen",
                            mTicketActionButtonEvent,
                            mCitationNumberId,
                            mFinalAmount,
                            mIssuranceModel!!.citationData!!.vehicle!!.state,
                            mIssuranceModel!!.citationData!!.vehicle!!.licensePlate,
                            isErrorUploading,
                            printcommand!!
                        )
                    } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
                        zebraPrinterUseCase?.mPrintFacsimileImage(
                            savePrintImagePath!!,
                            this@LprPreviewActivity,
                            "previewScreen",
                            mTicketActionButtonEvent,
                            mCitationNumberId,
                            mFinalAmount,
                            mIssuranceModel!!.citationData!!.vehicle!!.state,
                            mIssuranceModel!!.citationData!!.vehicle!!.licensePlate,
                            isErrorUploading,
                            printcommand!!
                        )
                    }
                }else {
                    // No need to print for carta
                    if (isSkipPrinter) {
                        onActionSuccess("")
                    } else {
                        //TODO print bitmap path
//                mTicketActionButtonEvent = "R"

                        if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
                            starPrinterUseCase?.mPrintFacsimileImage(
                                savePrintImagePath!!,
                                this@LprPreviewActivity,
                                "previewScreen",
                                mTicketActionButtonEvent,
                                mCitationNumberId,
                                mFinalAmount,
                                mIssuranceModel!!.citationData!!.vehicle!!.state,
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate,
                                isErrorUploading,
                                printcommand!!
                            )
                        } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
                            xfPrinterUseCase?.mPrintFacsimileImage(
                                savePrintImagePath!!,
                                this@LprPreviewActivity,
                                "previewScreen",
                                mTicketActionButtonEvent,
                                mCitationNumberId,
                                mFinalAmount,
                                mIssuranceModel!!.citationData!!.vehicle!!.state,
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate,
                                isErrorUploading,
                                AppUtils.printQueryStringBuilder!!
                            )
                        } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
                           zebraPrinterUseCase?.mPrintFacsimileImage(
                                savePrintImagePath!!,
                                this@LprPreviewActivity,
                                "previewScreen",
                                mTicketActionButtonEvent,
                                mCitationNumberId,
                                mFinalAmount,
                                mIssuranceModel!!.citationData!!.vehicle!!.state,
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate,
                                isErrorUploading,
                                printcommand!!
                            )
//                            getPrintFacsimileImage(
//                                savePrintImagePath!!,
//                                this@LprPreviewActivity,
//                                "previewScreen",
//                                mTicketActionButtonEvent,
//                                mCitationNumberId,
//                                mFinalAmount,
//                                mIssuranceModel!!.citationData!!.vehicle!!.state,
//                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate,
//                                isErrorUploading
//                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun SaveFacsimleImage(finalBitmap: Bitmap?, imageNmae: String?) {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )

        myDir.mkdirs()
        val fname = "${imageNmae?.trim()}.jpg"
        savePrintImagePath = File(myDir, fname)
        if (savePrintImagePath!!.exists()) savePrintImagePath!!.delete()
        try {
            sharedPreference.write(SharedPrefKey.REPRINT_PRINT_BITMAP,
                savePrintImagePath!!.absoluteFile.toString())

            val out = FileOutputStream(savePrintImagePath)
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS)) {
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            }else {
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            }

            out.flush()
            out.close()


            unUploadFacsimileImage.imagePath = savePrintImagePath!!.absoluteFile.toString()
            unUploadFacsimileImage.imageLink = ""
            unUploadFacsimileImage.lprNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
            unUploadFacsimileImage.dateTime =  SimpleDateFormat("HHmmssSSS", Locale.US).format(Date()).toLong()
            unUploadFacsimileImage.ticketNumberText = mIssuranceModel!!.citationData!!.ticketNumber!!
            unUploadFacsimileImage.status = 0
            unUploadFacsimileImage.imageCount = bannerList.size
            unUploadFacsimileImage.uploadedCitationId = ""
            unUploadFacsimileImage.imageType = UNUPLOAD_IMAGE_TYPE_FACSIMILE

            mDb!!.dbDAO!!.insertFacsimileImageObject(unUploadFacsimileImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun SaveImageOCRMM(finalBitmap: Bitmap?, imageNmae: String?) {

        val myDir = File(Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA)
        myDir.mkdirs()
        val fname = "${imageNmae?.trim()}.jpg"
        var savePrintImage: File? = null
        savePrintImage = File(myDir, fname)
        if (savePrintImage!!.exists()) savePrintImage!!.delete()
        try {
            sharedPreference.write(
                SharedPrefKey.REPRINT_PRINT_BITMAP_OCR,
                    savePrintImage!!.absoluteFile.toString()
            )
            val out = FileOutputStream(savePrintImage)
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS)) {
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            }else {
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            }

            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var removeIndex = -3
    private fun setAdapterForCitationList(citaitonList: List<VehicleListModel>,
        linearLayoutCompatCitaion: LinearLayoutCompat) {
        try {
            val recyclerVehical: RecyclerView =
                linearLayoutCompatCitaion.findViewById(R.id.recycler_citation)
            var spanCount = 3

            if (checkBuildConfigForPreviewActivityCitationAdapterGrid_3()) {
                gridLayoutCountForCitation = 3
                removeIndex = -3
                spanCount = 3
            } else if (
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                    ignoreCase = true
                ) ||  BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                    ignoreCase = true
                ) ||  BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
            ) {
                gridLayoutCountForCitation = 1
                removeIndex = -1
                spanCount = 1
            } else {
                gridLayoutCountForCitation = 2
                removeIndex = -2
                spanCount = 2
            }
            if (citaitonList.size > 0) {
                var i = 0
                val mFinalList: MutableList<VehicleListModel> = ArrayList()
                for (listModel in citaitonList) {
                    if (i >= 0) {
                        mFinalList.add(listModel)
                    }
                    if (listModel.type == 3) {
                        i = -3
                    } else if (listModel.type == 2) {
                        i = -2
                    }
                    i++
                }
                recyclerVehical.post {
                    mCitaionListAdapter = CitationAdapter(mContext!!, mFinalList,
                        object : CitationAdapter.ListItemSelectListener {
                            override fun onItemClick(
                                rlRowMain: LinearLayoutCompat?,
                                mStatus: Boolean, position: Int
                            ) {
                            }
                        })
                    recyclerVehical.setHasFixedSize(true)

                    val gridLayoutManager = GridLayoutManager(mContext, spanCount)
                    gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            val totalSpan = gridLayoutManager.spanCount
                            val adapterName = mCitaionListAdapter?.javaClass?.simpleName

                            // Safely get viewType (guard against invalid positions)
                            val viewType = try {
                                mCitaionListAdapter?.getItemViewType(position)
                            } catch (e: Exception) {
                                LogUtil.printLog("SpanDebug", " citation adapter Invalid position=$position for $adapterName", e)
                                return 1
                            }

                            // Your normal span logic
                            var span = when (viewType) {
                                CitationAdapter.ONECOLUMN -> 1
                                CitationAdapter.TWOCOLUMN -> 2
                                CitationAdapter.THREECOLUMN -> 3
                                else -> 1
                            }

                            // Step 4: Auto-correct invalid spans and log
                            if (span > totalSpan) {
                                LogUtil.printLog(
                                    "SpanDebug",
                                    " citation adapter Invalid span detected: adapter=$adapterName position=$position " +
                                            "requires $span but GridLayoutManager has only $totalSpan spans. Auto-correcting."
                                )
                                span = totalSpan
                            }

                            return span
                        }
                    }
                    recyclerVehical.adapter = mCitaionListAdapter
                    recyclerVehical.layoutManager = gridLayoutManager
                    recyclerVehical.visibility = View.VISIBLE
                }
            } else {
                recyclerVehical.visibility = View.GONE
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setAdapterForOfficerList(mOfficerList: List<VehicleListModel>,
        linearLayoutCompatOfficer: LinearLayoutCompat) {
        try{
        val recyclerOfficer: RecyclerView =
                linearLayoutCompatOfficer.findViewById(R.id.recyclerOfficer)

        gridLayoutCount =
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)){
                       1
                    } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)){
                        2
                    }else {
                        3
                    }
            if (mOfficerList.size > 0) {
                var i = 0
                val mFinalList: MutableList<VehicleListModel> = ArrayList()
                for (listModel in mOfficerList) {
                    if (gridLayoutCount == 1) {
                        if (!listModel.offNameFirst!!.isEmpty()) {
                            mFinalList.add(listModel)
                        }
                    } else {
                        if (i >= 0) {
                            mFinalList.add(listModel)
                        }
                        if (listModel.type == 3) {
                            i = -3
                        } else if (listModel.type == 2) {
                            i = -2
                        }
                        i++
                    }
                }
                recyclerOfficer.post {
                    mOfficerListAdapter = OfficerListAdapter(mContext!!, mFinalList,
                            object : OfficerListAdapter.ListItemSelectListener {
                                override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                                        mStatus: Boolean, position: Int) {
                                }
                            })
                    recyclerOfficer.setHasFixedSize(true)
                    if (gridLayoutCount == 1) {
                        recyclerOfficer.layoutManager =
                                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                    } else {
                        val gridLayoutManager = GridLayoutManager(mContext, gridLayoutCount)
                        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                val totalSpan = gridLayoutManager.spanCount
                                val adapterName = mOfficerListAdapter?.javaClass?.simpleName

                                // Safely get viewType (guard against invalid positions)
                                val viewType = try {
                                    mOfficerListAdapter?.getItemViewType(position)
                                } catch (e: Exception) {
                                    LogUtil.printLog("SpanDebug", "Officer Adapter Invalid position=$position for $adapterName", e)
                                    return 1
                                }

                                // Your normal span logic
                                var span = when (viewType) {
                                    OfficerListAdapter.ONECOLUMN -> 1
                                    OfficerListAdapter.TWOCOLUMN -> 2
                                    OfficerListAdapter.THREECOLUMN -> 3
                                    else -> 1
                                }

                                // Step 4: Auto-correct invalid spans and log
                                if (span > totalSpan) {
                                    LogUtil.printLog(
                                        "SpanDebug",
                                        "Officer Adapter Invalid span detected: adapter=$adapterName position=$position " +
                                                "requires $span but GridLayoutManager has only $totalSpan spans. Auto-correcting."
                                    )
                                    span = totalSpan
                                }

                                return span
                            }
                        }
                        recyclerOfficer.layoutManager = gridLayoutManager
                    }
                    recyclerOfficer.adapter = mOfficerListAdapter
                    recyclerOfficer.visibility = View.VISIBLE
                }
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setAdapterForVehicalList(mOfficerList: List<VehicleListModel>,
                                         linearLayoutCompatVehicle: LinearLayoutCompat) {

        try {
            val recyclerVehical: RecyclerView =
                    linearLayoutCompatVehicle.findViewById(R.id.recyclerVehical)
            var spanCount = 3
            var i = 0
            if (mOfficerList.size > 0) {
                val mFinalList: MutableList<VehicleListModel> = ArrayList()
                for (listModel in mOfficerList) {
                    if (i >= 0) {
                        mFinalList.add(listModel)
                    }
                    if (listModel.type == 3) {
                        i = -3
                    } else if (listModel.type == 2) {
                        i = -2
                    }
                    i++
                }
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)){
                    spanCount = 2
                }else if(
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)
                    ){
                    spanCount = 1
                }

                recyclerVehical.post {
                    mVehicalListAdapter = VehicalListAdapter(mContext!!, mFinalList,
                            object : VehicalListAdapter.ListItemSelectListener {
                                override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                                        mStatus: Boolean, position: Int) {
                                }
                            })
                    recyclerVehical.setHasFixedSize(true)
                    val mLayoutManager = GridLayoutManager(mContext, spanCount)
                    mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            val totalSpan = mLayoutManager.spanCount
                            val adapterName = mVehicalListAdapter?.javaClass?.simpleName

                            // Safely get viewType (guard against invalid positions)
                            val viewType = try {
                                mVehicalListAdapter?.getItemViewType(position)
                            } catch (e: Exception) {
                                LogUtil.printLog("SpanDebug", "Vehicle adapter Invalid position=$position for $adapterName", e)
                                return 1
                            }

                            // Your normal span logic
                            var span = when (viewType) {
                                VehicalListAdapter.ONECOLUMN -> 1
                                VehicalListAdapter.TWOCOLUMN -> 2
                                VehicalListAdapter.THREECOLUMN -> 3
                                else -> 1
                            }

                            // Step 4: Auto-correct invalid spans and log
                            if (span > totalSpan) {
                                LogUtil.printLog(
                                    "SpanDebug",
                                    " Vehicle Adapter Invalid span detected: adapter=$adapterName position=$position " +
                                            "requires $span but GridLayoutManager has only $totalSpan spans. Auto-correcting."
                                )
                                span = totalSpan
                            }

                            return span
                        }
                    }
                    recyclerVehical.adapter = mVehicalListAdapter
                    recyclerVehical.layoutManager = mLayoutManager
                    recyclerVehical.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapterForViolationList(mOfficerList: List<VehicleListModel>,
                                           linearLayoutCompatViolation: LinearLayoutCompat) {
        try {
            val recyclerViolation: RecyclerView =
                linearLayoutCompatViolation.findViewById(R.id.recyclerViolation)
            var spanCount = 3
//        Log.e("vehicle size ",""+mOfficerList.size);
            if (mOfficerList.size > 0) {
                var i = 0
                val mFinalList: MutableList<VehicleListModel> = ArrayList()
                for (listModel in mOfficerList) {
                    if (i >= 0) {
                        mFinalList.add(listModel)
                    }
                    if (listModel.type == 3) {
                        i = -3
                    } else if (listModel.type == 2) {
                        i = -2
                    }
                    i++
                }

                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_MACKAY_SAMPLE,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ISLEOFPALMS,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)
                ) {
                    spanCount = 2
                } else if (
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_KANSAS_CITY,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PRIME_PARKING,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_SOUTH_LAKE,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_WINPARK_TX,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_DANVILLE_VA,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
                ) {
                    spanCount = 1
                }

                recyclerViolation.post {
                    mViolationsAdapter = ViolationsAdapter(mContext!!, mFinalList,
                        object : ViolationsAdapter.ListItemSelectListener {
                            override fun onItemClick(
                                rlRowMain: LinearLayoutCompat?,
                                mStatus: Boolean, position: Int
                            ) {
                            }
                        })
                    recyclerViolation.setHasFixedSize(true)
                    //            recyclerViolation.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    val mLayoutManager = GridLayoutManager(mContext, spanCount)
                    mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            val totalSpan = mLayoutManager.spanCount
                            val adapterName = mViolationsAdapter?.javaClass?.simpleName

                            // Safely get viewType (guard against invalid positions)
                            val viewType = try {
                                mViolationsAdapter?.getItemViewType(position)
                            } catch (e: Exception) {
                                LogUtil.printLog("SpanDebug", "Violation adapter Invalid position=$position for $adapterName", e)
                                return 1
                            }

                            // Your normal span logic
                            var span = when (viewType) {
                                ViolationsAdapter.ONECOLUMN -> 1
                                ViolationsAdapter.TWOCOLUMN -> 2
                                ViolationsAdapter.THREECOLUMN -> 3
                                else -> 1
                            }

                            // ✅ Step 4: Auto-correct invalid spans and log
                            if (span > totalSpan) {
                                LogUtil.printLog(
                                    "SpanDebug",
                                    "️Violaiton Adapter Invalid span detected: adapter=$adapterName position=$position " +
                                            "requires $span but GridLayoutManager has only $totalSpan spans. Auto-correcting."
                                )
                                span = totalSpan
                            }

                            return span
                        }
                    }

                    recyclerViolation.adapter = mViolationsAdapter
                    recyclerViolation.layoutManager = mLayoutManager
                    recyclerViolation.setHasFixedSize(true)
                    recyclerViolation.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapterForCommentesList(mCommentesList: List<VehicleListModel>,
                                           linearLayoutCompatComments: LinearLayoutCompat) {
        try {
            val recyclerViewComments: RecyclerView =
                linearLayoutCompatComments.findViewById(R.id.recyclercommentes)
            if (mCommentesList.size > 0 && mContext != null) {
                commentesAdapter = CommentesAdapter(mContext!!, mCommentesList,
                    object : CommentesAdapter.ListItemSelectListener {
                        override fun onItemClick(
                            rlRowMain: LinearLayoutCompat?,
                            mStatus: Boolean, position: Int
                        ) {
                        }
                    })
                recyclerViewComments.setHasFixedSize(true)
                var mLayoutManager = GridLayoutManager(mContext, 1)
                //            recyclerViolation.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_HARTFORD,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_MACKAY_SAMPLE,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_KALAMAZOO,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ISLEOFPALMS,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_CHARLESTON,
                        ignoreCase = true
                    )
                ) {
                    mLayoutManager = GridLayoutManager(mContext, 2)
                }

//            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,
//                    ignoreCase = true)){
//                mLayoutManager = GridLayoutManager(mContext, 3)
//            }
                recyclerViewComments.layoutManager = mLayoutManager
                mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        val totalSpan = mLayoutManager.spanCount
                        val adapterName = commentesAdapter?.javaClass?.simpleName

                        // Safely get viewType (guard against invalid positions)
                        val viewType = try {
                            commentesAdapter?.getItemViewType(position)
                        } catch (e: Exception) {
                            LogUtil.printLog("SpanDebug", "Comment adapter Invalid position=$position for $adapterName", e)
                            return 1
                        }

                        // Your normal span logic
                        var span = when (viewType) {
                            CommentesAdapter.ONECOLUMN -> 1
                            CommentesAdapter.TWOCOLUMN -> 2
                            CommentesAdapter.THREECOLUMN -> 3
                            else -> 1
                        }

                        //Step 4: Auto-correct invalid spans and log
                        if (span > totalSpan) {
                            LogUtil.printLog(
                                "SpanDebug",
                                " comment adapter Invalid span detected: adapter=$adapterName position=$position " +
                                        "requires $span but GridLayoutManager has only $totalSpan spans. Auto-correcting."
                            )
                            span = totalSpan
                        }

                        return span
                    }
                }

                recyclerViewComments.adapter = commentesAdapter
//            recyclerViewComments.layoutManager = mLayoutManager
//            recyclerViewComments.setHasFixedSize(true)
                recyclerViewComments.visibility = View.VISIBLE
            } else {
                recyclerViewComments.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun refreshLayout(layoutCompat: LinearLayoutCompat) {
        layoutCompat.invalidate()
        layoutCompat.requestLayout()
        layoutCompat.setBackgroundResource(R.color.white)
    }

    private fun createViewToPrint(mIssuranceModel : CitationInsurranceDatabaseModel?) {
        //mIssuranceModel = mDb!!.dbDAO!!.getCitationWithTicket(mCitationNumberId)
        if (mIssuranceModel != null) {
            val linearLayoutCompatVehicle = this.layoutInflater.inflate(
                    R.layout.content_print_vehicle,null) as LinearLayoutCompat

            val linearLayoutCompatCitation = this.layoutInflater.inflate(
                    R.layout.content_print_citation_layout,null) as LinearLayoutCompat

            val linearLayoutCompatOfficer = this.layoutInflater.inflate(
                    R.layout.content_print_officer_details,null) as LinearLayoutCompat

            val linearLayoutCompatRemark = this.layoutInflater.inflate(
                    R.layout.content_print_remark_notes_signature,null) as LinearLayoutCompat

            val linearLayoutCompatViolation = this.layoutInflater.inflate(
                    R.layout.content_print_violation,null) as LinearLayoutCompat

            var appComTextViewPrintUrl: AppCompatTextView? = null
            var appCompatImageViewQRCode: AppCompatImageView? = null
            var appCompatImageViewLprImage: AppCompatImageView? = null
            var linearLayoutCompatPrintUrl: LinearLayoutCompat? = null
            var appComTextViewQRCodeLabel: AppCompatTextView? = null


            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ) {
                appComTextViewPrintUrl =
                    linearLayoutCompatRemark!!.findViewById(R.id.text_print_url)
                appCompatImageViewQRCode =
                    linearLayoutCompatRemark!!.findViewById(R.id.iv_print_url)
                appCompatImageViewLprImage =
                    linearLayoutCompatRemark!!.findViewById(R.id.iv_lprImage)
                linearLayoutCompatPrintUrl =
                    linearLayoutCompatRemark!!.findViewById(R.id.ll_qr_bottomview)
                linearLayoutCompatQrCodeAndMessage =
                    linearLayoutCompatRemark!!.findViewById(R.id.ll_qrcode_message)

                appComTextViewQRCodeLabel =
                    linearLayoutCompatRemark!!.findViewById(R.id.text_qr_code_label)
            }else {
                appComTextViewPrintUrl = linearLayoutCompatBarCode!!.findViewById(R.id.text_print_url)
                appCompatImageViewQRCode =
                    linearLayoutCompatBarCode!!.findViewById(R.id.iv_print_url)
                appCompatImageViewLprImage =
                    linearLayoutCompatBarCode!!.findViewById(R.id.iv_lprImage)
                linearLayoutCompatPrintUrl =
                    linearLayoutCompatBarCode!!.findViewById(R.id.ll_qr_bottomview)
                linearLayoutCompatQrCodeAndMessage =
                    linearLayoutCompatBarCode!!.findViewById(R.id.ll_qrcode_message)

                appComTextViewQRCodeLabel =
                    linearLayoutCompatBarCode!!.findViewById(R.id.text_qr_code_label)
            }

//            var appComTextViewPrintUrl: AppCompatTextView = linearLayoutCompatBarCode!!.findViewById(R.id.text_print_url)
//            var appCompatImageViewBarCode: AppCompatImageView = linearLayoutCompatBarCode!!.findViewById(R.id.iv_print_url)
//            var linearLayoutCompatPrintUrl: LinearLayoutCompat = linearLayoutCompatBarCode!!.findViewById(R.id.ll_qr_bottomview)


             if (checkBuildConfigForPreviewActivityForQRCode()) {

                 if(checkBuildConfigForPreviewActivityForTopMessage()){

                     appCompatTextViewTopMessage.visibility = View.VISIBLE
//                     appCompatTextViewTopMessage!!.text = "PENALTY ASSESSMENT NOTICE"
                 }
                var lines = sharedPreference.read(SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT, "")?.replace("#", "\n")
//                lines = " https://follybeach-payment-staging.netlify.app/[ticket_no]"
                 appComTextViewPrintUrl.text = lines.toString().replace("[ticket_no]",mCitationNumberId.toString())
//                if (appComTextViewPrintUrl!!.text!!.toString().length < 1) {
//                    linearLayoutCompatPrintUrl.visibility = View.GONE
//                }
//                 isQRCodePrintlayout= true
                if (isQRCodePrintlayout) {
                    appComTextViewPrintUrl.visibility = View.VISIBLE
                    linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                    appCompatImageViewQRCode.visibility = View.VISIBLE
                    mQRCodeValue?.let { GenerateQRCodeInnovaPrint(it, appCompatImageViewQRCode) }
//                    mQRCodeValue?.let { generateQR(it,appCompatImageViewPrintUrl) }
//                    appCompatImageViewPrintUrl!!.setImageBitmap(qrCodePrintBitmap)

                    val qrCodeLabel = sharedPreference.read(
                        SharedPrefKey.QRCODE_LABEL_FOR_PRINT,"").nullSafety()
                    if (qrCodeLabel.isNotEmpty()){
                        appComTextViewQRCodeLabel.showView()
                        appComTextViewQRCodeLabel.text = qrCodeLabel
                    }else{
                        appComTextViewQRCodeLabel.hideView()
                    }
                }else if(isBarCodePrintlayout && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA,true)){
                    linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                }else{
                    appComTextViewPrintUrl.visibility = View.GONE
                    linearLayoutCompatPrintUrl.visibility = View.GONE
                    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true))
                    {
                        appComTextViewPrintUrl.visibility = View.VISIBLE
                        linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                    }
                }
                 if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)||
                     BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)){
                     appCompatTextViewTopMessage.visibility = View.VISIBLE
                     appComTextViewPrintUrl.visibility = View.VISIBLE
//                     if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
//                         linearLayoutCompatPrintUrl.visibility = View.GONE
//                     }else{
                         linearLayoutCompatPrintUrl.visibility = View.VISIBLE
//                     }
                     appCompatTextViewTopMessage!!.text =
                         sharedPreference.read(SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT, "")?.split("#")
                             ?.get(0) ?: ""

                     try {
                         if(sharedPreference.read(SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT, "")?.split("#")!!.size>1) {
                             appComTextViewPrintUrl!!.text =
                                 sharedPreference.read(
                                     SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT,
                                     ""
                                 )?.split("#")
                                     ?.get(1) ?: ""
                         }
                     } catch (e: Exception) {
                         e.printStackTrace()
                         appComTextViewPrintUrl!!.text = ""
                         appComTextViewPrintUrl.hideView()
                     }
                 }
            } else {
                appComTextViewPrintUrl.visibility = View.GONE
                linearLayoutCompatPrintUrl.visibility = View.GONE
            }

            if (appComTextViewPrintUrl.text.isNullOrEmpty()){
                appComTextViewPrintUrl.hideView()
            }

            try {
                if(isPrintLprImageInFacsimilePrint(getMyDatabase()!!)){

                    if(bannerList!=null && bannerList!!.size>0 &&
                        bannerList!!.get(0)!!.citationImage!!.contains("anpr_"))
                    {
                        val imgFileCopy = bannerList!!.get(0)!!.citationImage?.let { File(it) }
                        if (imgFileCopy!=null && imgFileCopy!!.exists()) {
                            linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                            appCompatImageViewLprImage.visibility = View.VISIBLE
                            appCompatImageViewLprImage.setImageURI(Uri.fromFile(imgFileCopy))
                        }
                    }else{
                        appCompatImageViewLprImage.visibility = View.GONE
                        (linearLayoutCompatQrCodeAndMessage as LinearLayoutCompat.LayoutParams).weight = 2f
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /****
             * CITATION TICKET DATA
             * TODO CITATION PRINT SECTION
             */
            val appCompatTextViewCitaitonTitle: AppCompatTextView =
                    linearLayoutCompatCitation.findViewById(R.id.textview_citation_title)
            try {
                if (mIssuranceModel?.citationData?.isStatus_ticketNumber.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.ticketNumber.nullSafety())) {
                        val citation = CitationPrintSectionUtils.getTicketNumber(mIssuranceModel)

                        val orderNumber = mIssuranceModel?.citationData?.mPrintLayoutOrderTickerNumber
                            ?: Constants.PRINT_LAYOUT_ORDER_SPARATER

                        val parts = orderNumber.split("#")

                        val firstPosition = parts.getOrNull(0)?.toIntOrNull() ?: 0   // default to 0 if invalid
                        val layoutSectionTitle = parts.getOrNull(1) ?: ""            // default to empty string

                        if (firstPosition > 0) {
                            mPrintLayoutMap["CITATION"] = firstPosition
                            mPrintLayoutTitle[firstPosition] = layoutSectionTitle
                            citation.mSectionHeader = layoutSectionTitle

                            when (firstPosition) {
                                1 -> mCitationList.add(citation)
                                2 -> mVehicleList.add(citation)
                                3 -> mViolationList.add(citation)
                                4 -> mOfficerList.add(citation)
                                5 -> mCommentsList.add(citation)
                            }
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.isStatus_ticket_date.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.ticketDate.nullSafety())) {
                        try{
                            val (citation, citationTime) = CitationPrintSectionUtils.getTicketDate(this@LprPreviewActivity, mIssuranceModel)

                            val orderNumber =
                                if (mIssuranceModel?.citationData?.mPrintLayoutOrderTicketDate != null) mIssuranceModel?.citationData?.mPrintLayoutOrderTicketDate else Constants.PRINT_LAYOUT_ORDER_SPARATER
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                            val sequence = firstPosition.toInt()

                            if (sequence == 1) {
                                mCitationList.add(citation)
                                if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
                                    mCitationList.add(citationTime)
                                }
                            } else if (sequence == 2) {
                                mVehicleList.add(citation)
                                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
                                    mVehicleList.add(citationTime)
                                }
                            } else if (sequence == 3) {
                                mViolationList.add(citation)
                                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
                                    mViolationList.add(citationTime)
                                }
                            } else if (sequence == 4) {
                                mOfficerList.add(citation)
                                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
                                    mOfficerList.add(citationTime)
                                }
                            } else if (sequence == 5) {
                                mCommentsList.add(citation)
                                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
                                    mCommentsList.add(citationTime)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                if(BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)) {
                if (mIssuranceModel?.citationData?.isStatus_ticket_time.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.ticketTime.nullSafety())) {
//                    if (true) {
//                        if (true) {
                        try{
                            val citation = CitationPrintSectionUtils.getTicketTime(mIssuranceModel)
//                                    mIssuranceModel?.citationData?.mPrintOrderTicketTime.nullSafety()
                                val orderNumber =
                                    if (mIssuranceModel?.citationData?.mPrintLayoutOrderTicketTime != null) mIssuranceModel?.citationData?.mPrintLayoutOrderTicketTime else Constants.PRINT_LAYOUT_ORDER_SPARATER

                                val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                                val sequence = firstPosition.toInt()
//                                printcommand = AppUtils.setXYforPrint(citation,sequence,"citation",printcommand!!)

                                if (sequence == 1) {
                                    mCitationList.add(citation)
                                } else if (sequence == 2) {
                                    mVehicleList.add(citation)
                                } else if (sequence == 3) {
                                    mViolationList.add(citation)
                                } else if (sequence == 4) {
                                    mOfficerList.add(citation)
                                } else if (sequence == 5) {
                                    mCommentsList.add(citation)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                if (mIssuranceModel?.citationData?.isStatus_ticket_week.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.ticketWeek.nullSafety())) {
                            try {
                               val citation = CitationPrintSectionUtils.getTicketWeek(mIssuranceModel)
//                                    mIssuranceModel?.citationData?.mPrintOrderTicketWeek.nullSafety()
                                val orderNumber =
                                    if (mIssuranceModel?.citationData?.mPrintLayoutOrderTicketWeek != null) mIssuranceModel?.citationData?.mPrintLayoutOrderTicketWeek else Constants.PRINT_LAYOUT_ORDER_SPARATER

                                val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                                val sequence = 1//lfirstPosition.toInt()
//                                printcommand = AppUtils.setXYforPrint(citation,sequence,"citation",printcommand!!)
                                if (sequence == 1) {
                                    mCitationList.add(citation)
                                } else if (sequence == 2) {
                                    mVehicleList.add(citation)
                                } else if (sequence == 3) {
                                    mViolationList.add(citation)
                                } else if (sequence == 4) {
                                    mOfficerList.add(citation)
                                } else if (sequence == 5) {
                                    mCommentsList.add(citation)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                } 
                if (mIssuranceModel?.citationData?.isStatus_code2010.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.code2010.nullSafety())) {
                            try {
                                val citation = CitationPrintSectionUtils.getCode2010(mIssuranceModel)
                                val orderNumber =
                                    if (mIssuranceModel?.citationData?.mPrintLayoutOrdercode2010 != null) mIssuranceModel?.citationData?.mPrintLayoutOrdercode2010 else Constants.PRINT_LAYOUT_ORDER_SPARATER

                                val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                                val sequence = 1//lfirstPosition.toInt()
//                                printcommand = AppUtils.setXYforPrint(citation,sequence,"citation",printcommand!!)
                                if (sequence == 1) {
                                    mCitationList.add(citation)
                                } else if (sequence == 2) {
                                    mVehicleList.add(citation)
                                } else if (sequence == 3) {
                                    mViolationList.add(citation)
                                } else if (sequence == 4) {
                                    mOfficerList.add(citation)
                                } else if (sequence == 5) {
                                    mCommentsList.add(citation)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                }
                if (mIssuranceModel?.citationData?.isStatus_hearingDate.nullSafety()) {
                            try {
                                val citation = CitationPrintSectionUtils.getHearingDate(mIssuranceModel)
                                val orderNumber =
                                    if (mIssuranceModel?.citationData?.mPrintLayoutOrderHearingDate != null) mIssuranceModel?.citationData?.mPrintLayoutOrderHearingDate else Constants.PRINT_LAYOUT_ORDER_SPARATER

                                val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                                val sequence = firstPosition.toInt()

                                if (sequence == 1) {
                                    mCitationList.add(citation)
                                } else if (sequence == 2) {
                                    mVehicleList.add(citation)
                                } else if (sequence == 3) {
                                    mViolationList.add(citation)
                                } else if (sequence == 4) {
                                    mOfficerList.add(citation)
                                } else if (sequence == 5) {
                                    mCommentsList.add(citation)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                }
                if (mIssuranceModel?.citationData?.isStatus_hearingDescription.nullSafety()) {
                            try {
                                val citation = CitationPrintSectionUtils.getHearingDescription(mIssuranceModel)
                                val orderNumber =
                                    if (mIssuranceModel?.citationData?.mPrintLayoutOrderHearingDescription != null) mIssuranceModel?.citationData?.mPrintLayoutOrderHearingDescription else Constants.PRINT_LAYOUT_ORDER_SPARATER

                                val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                                val sequence = firstPosition.toInt()

                                if (sequence == 1) {
                                    mCitationList.add(citation)
                                } else if (sequence == 2) {
                                    mVehicleList.add(citation)
                                } else if (sequence == 3) {
                                    mViolationList.add(citation)
                                } else if (sequence == 4) {
                                    mOfficerList.add(citation)
                                } else if (sequence == 5) {
                                    mCommentsList.add(citation)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                }
                if (mIssuranceModel?.citationData?.isStatus_OfficerDescription.nullSafety()) {
                            try {
                                val citation = CitationPrintSectionUtils.getOfficerDescription(mIssuranceModel)
                                val orderNumber =
                                    if (mIssuranceModel?.citationData?.mPrintLayoutOrderOfficerDescription != null) mIssuranceModel?.citationData?.mPrintLayoutOrderOfficerDescription else Constants.PRINT_LAYOUT_ORDER_SPARATER

                                val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                                val sequence = firstPosition.toInt()

                                if (sequence == 1) {
                                    mCitationList.add(citation)
                                } else if (sequence == 2) {
                                    mVehicleList.add(citation)
                                } else if (sequence == 3) {
                                    mViolationList.add(citation)
                                } else if (sequence == 4) {
                                    mOfficerList.add(citation)
                                } else if (sequence == 5) {
                                    mCommentsList.add(citation)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                }

                if (mIssuranceModel?.citationData?.isStatus_ticket_type.nullSafety()) {
    //                if (!TextUtils.isEmpty(mIssuranceModel.getCitationData().getTicketType())) {

                    val (isWarningSelected, citation) = CitationPrintSectionUtils.getTicketType(this@LprPreviewActivity,mIssuranceModel)
                    this.isWarningSelected = isWarningSelected

                    val orderNumber =
                            if (mIssuranceModel?.citationData?.mPrintLayoutOrderTicketType != null) mIssuranceModel?.citationData?.mPrintLayoutOrderTicketType else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                    val sequence = firstPosition.toInt()

                    if (sequence == 1) {
                        mCitationList.add(citation)
                    } else if (sequence == 2) {
                        mVehicleList.add(citation)
                    } else if (sequence == 3) {
                        mViolationList.add(citation)
                    } else if (sequence == 4) {
                        mOfficerList.add(citation)
                    } else if (sequence == 5) {
                        mCommentsList.add(citation)
                    }
                    //                } else {
    ////                    mTextViewTypeValue.setVisibility(View.GONE);
    //                }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            /****
             * OFFICER DATA
             * TODO OFFICER PRINT SECTION
             */
            val appCompatTextViewOfficerTitle: AppCompatTextView =
                    linearLayoutCompatOfficer.findViewById(R.id.textview_officer_title)

            try {
                if (mIssuranceModel?.citationData?.officer?.isStatus_officer_details.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.officer?.officerDetails)) {
                        val data = CitationPrintSectionUtils.getOfficerDetails(officerNameFormatForPrint,mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderOfficerDetails != null) mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderOfficerDetails.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.split("#").toTypedArray()[0]
                        val sequence = firstPosition.toInt()
                        try {
                            val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            if (layoutSectionTitle.contentEquals("Officer", ignoreCase = true)||
                                layoutSectionTitle.contentEquals("OFFICER INFORMATION", ignoreCase = true)||
                                layoutSectionTitle.contentEquals("OFFICER DETAIL", ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL)) {
                                mPrintLayoutMap.put("OFFICER", sequence)// Layout order
                                mPrintLayoutTitle[sequence] = layoutSectionTitle
                            }
                            data.mSectionHeader = layoutSectionTitle

                        }catch (e:Exception)
                            {
                                e.printStackTrace()
                            }

                        if (sequence == 1) {
                            mCitationList.add(data)
                        } else if (sequence == 2) {
                            mVehicleList.add(data)
                        } else if (sequence == 3) {
                            mViolationList.add(data)
                        } else if (sequence == 4) {
                            mOfficerList.add(data)
                        } else if (sequence == 5) {
                            mCommentsList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.officer?.isStatus_officer_id.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.officer?.officerId.nullSafety())) {
                        val data = CitationPrintSectionUtils.getOfficerID(mIssuranceModel)
                        //                    mOfficerList.add(data);
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderOfficerId != null) mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderOfficerId else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
//                        printcommand = AppUtils.setXYforPrint(data,sequence,"Officer",printcommand!!)
                        if (sequence == 1) {
                            mCitationList.add(data)
                        } else if (sequence == 2) {
                            mVehicleList.add(data)
                        } else if (sequence == 3) {
                            mViolationList.add(data)
                        } else if (sequence == 4) {
                            mOfficerList.add(data)
                        } else if (sequence == 5) {
                            mCommentsList.add(data)
                        }
                    }
                }
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)) {
                    if (mIssuranceModel?.citationData?.officer?.isStatus_badge_id.nullSafety()) {
                        if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.officer?.badgeId.nullSafety())) {
                            val data = CitationPrintSectionUtils.getBadgeID(mIssuranceModel)
                            val orderNumber =
                                    if (mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderBadgeId != null) mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderBadgeId.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                            val firstPosition = orderNumber.split("#").toTypedArray()[0]
                            val sequence = firstPosition.toInt()
                            val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]

                            mPrintLayoutMap.put("OFFICER", sequence)// Layout order
                            mPrintLayoutTitle[sequence] = layoutSectionTitle
                            if (sequence == 1) {
                                mCitationList.add(data)
                            } else if (sequence == 2) {
                                mVehicleList.add(data)
                            } else if (sequence == 3) {
                                mViolationList.add(data)
                            } else if (sequence == 4) {
                                mOfficerList.add(data)
                            } else if (sequence == 5) {
                                mCommentsList.add(data)
                            }
                        }
                    }
                }

                else {
                    if (mIssuranceModel?.citationData?.officer?.isStatus_badge_id.nullSafety()) {
                        if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.officer?.badgeId.nullSafety())) {
                            val data = CitationPrintSectionUtils.getBadgeID(mIssuranceModel)

                            val orderNumber =
                                    if (mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderBadgeId != null) mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderBadgeId.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                            val firstPosition = orderNumber.split("#").toTypedArray()[0]
                            val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            val sequence = firstPosition.toInt()

    //                        mPrintLayoutMap.put("OFFICER", sequence)// Layout order
                            if (layoutSectionTitle.contentEquals("Officer", ignoreCase = true)||
                                layoutSectionTitle.contentEquals("OFFICER INFORMATION", ignoreCase = true)||
                                layoutSectionTitle.contentEquals("OFFICER DETAIL", ignoreCase = true)) {
                                mPrintLayoutMap.put("OFFICER", sequence)// Layout order
                            }
                            mPrintLayoutTitle[sequence] = layoutSectionTitle
                            data.mSectionHeader = layoutSectionTitle
//                            printcommand = AppUtils.setXYforPrint(data,sequence,"Officer",printcommand!!)
                            if (sequence == 1) {
                                mCitationList.add(data)
                            } else if (sequence == 2) {
                                mVehicleList.add(data)
                            } else if (sequence == 3) {
                                mViolationList.add(data)
                            } else if (sequence == 4) {
                                mOfficerList.add(data)
                            } else if (sequence == 5) {
                                mCommentsList.add(data)
                            }
                        }
                    }
                    if (mIssuranceModel?.citationData?.officer?.isStatus_agency.nullSafety()) {
                        if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.officer?.agency.nullSafety())) {
                            val data = CitationPrintSectionUtils.getAgency(mIssuranceModel)

                            val orderNumber =
                                    if (mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderAgency != null) mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderAgency else Constants.PRINT_LAYOUT_ORDER_SPARATER
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                            val layoutSectionTitle = if(orderNumber.split("#").size>0) orderNumber.split("#").toTypedArray()[1] else ""
                            val sequence = firstPosition.toInt()
                            if (layoutSectionTitle.contentEquals("Officer", ignoreCase = true)||
                                layoutSectionTitle.contentEquals("OFFICER INFORMATION", ignoreCase = true)||
                                layoutSectionTitle.contentEquals("OFFICER DETAIL", ignoreCase = true)) {
                                mPrintLayoutMap.put("OFFICER", sequence)// Layout order
                            }
                            mPrintLayoutTitle[sequence] = layoutSectionTitle
                            data.mSectionHeader = layoutSectionTitle
//                            printcommand = AppUtils.setXYforPrint(data,sequence,"officer",printcommand!!)
                            if (sequence == 1) {
                                mCitationList.add(data)
                            } else if (sequence == 2) {
                                mVehicleList.add(data)
                            } else if (sequence == 3) {
                                mViolationList.add(data)
                            } else if (sequence == 4) {
                                mOfficerList.add(data)
                            } else if (sequence == 5) {
                                mCommentsList.add(data)
                            }
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.officer?.isStatus_beat.nullSafety()) {
                       val data = CitationPrintSectionUtils.getBeat(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderBeat != null) mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderBeat.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.split("#").toTypedArray()[0]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(data)
                        } else if (sequence == 2) {
                            mVehicleList.add(data)
                        } else if (sequence == 3) {
                            mViolationList.add(data)
                        } else if (sequence == 4) {
                            mOfficerList.add(data)
                        } else if (sequence == 5) {
                            mCommentsList.add(data)
                        }
                }
                if (mIssuranceModel?.citationData?.officer?.isStatus_squad.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.officer?.squad.nullSafety())) {
                        val data = CitationPrintSectionUtils.getSquad(mIssuranceModel)

                        val orderNumber =
                                if (mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderSquad != null) mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderSquad else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                        val sequence = firstPosition.toInt()
                        if (sequence == 1) {
                            mCitationList.add(data)
                        } else if (sequence == 2) {
                            mVehicleList.add(data)
                        } else if (sequence == 3) {
                            mViolationList.add(data)
                        } else if (sequence == 4) {
                            mOfficerList.add(data)
                        } else if (sequence == 5) {
                            mCommentsList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.officer?.isStatus_observationtime.nullSafety()) {
                    val data = CitationPrintSectionUtils.getObservationTimeLabel(sharedPreference, mIssuranceModel)
                    val orderNumber =
                            if (mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderObservationTime != null) mIssuranceModel?.citationData?.officer?.mPrintLayoutOrderObservationTime else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                    val sequence = firstPosition.toInt()
                    if (sequence == 1) {
                        mCitationList.add(data)
                    } else if (sequence == 2) {
                        mVehicleList.add(data)
                    } else if (sequence == 3) {
                        mViolationList.add(data)
                    } else if (sequence == 4) {
                        mOfficerList.add(data)
                    } else if (sequence == 5) {
                        mCommentsList.add(data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /****
             * REMARK & NOTES DATA
             * TODO REMARK PRINT SECTION
             */
            var linearLayoutCompatSignature:LinearLayoutCompat?= null
            var appCompatImageViewSignature: AppCompatImageView? = null
            var appCompatTextViewCommentsTitle: AppCompatTextView? = null
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
                 linearLayoutCompatSignature =
                    linearLayoutCompatCitation.findViewById(R.id.ll_signature)
                 appCompatImageViewSignature =
                    linearLayoutCompatCitation.findViewById(R.id.imgSignature_print)
            }else {
                 linearLayoutCompatSignature =
                    linearLayoutCompatRemark.findViewById(R.id.ll_signature)
                appCompatImageViewSignature =
                    linearLayoutCompatRemark.findViewById(R.id.imgSignature_print)
                 appCompatTextViewCommentsTitle =
                    linearLayoutCompatRemark.findViewById(R.id.textview_comments_title)
            }
            try { //**Signature**/
                if(linearLayoutCompatSignature!=null) {
                    linearLayoutCompatSignature.visibility = View.GONE
                    val file = File(mPath)
                    if (!TextUtils.isEmpty(mPath) && file.exists()) {
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
                                ignoreCase = true
                            ) ||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_HARTFORD,
                                ignoreCase = true
                            ) ||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_MACKAY_SAMPLE,
                                ignoreCase = true
                            ) ||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_BELLINGHAM,
                                ignoreCase = true
                            ) ||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_CLIFTON,
                                ignoreCase = true
                            ) ||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_EPHRATA,
                                ignoreCase = true
                            ) ||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_Easton,
                                ignoreCase = true
                            ) ||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_OCEANCITY,
                                ignoreCase = true
                            )||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_DUNCAN,
                                ignoreCase = true
                            )||
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_BOSTON,
                                ignoreCase = true
                            )
                        ) {
                            mImageViewSignature.visibility = View.VISIBLE
                            mTextViewSignName.visibility = View.VISIBLE
                            appCompatImageViewSignature.setImageURI(Uri.fromFile(File(mPath)))
                        }
                    }
 }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (mIssuranceModel?.citationData?.isStatus_location_notes.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.locationNotes.nullSafety())) {
                        val data = CitationPrintSectionUtils.getLocationNotes(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.mPrintLayoutOrderLocationNotes != null) mIssuranceModel?.citationData?.mPrintLayoutOrderLocationNotes else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        var layoutSectionTitle: String? = null
                        var sequence = 0
                        try {
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                        layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            sequence = firstPosition.toInt()
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                        if (sequence == 1) {
                            mCitationList.add(data)
                        } else if (sequence == 2) {
                            mVehicleList.add(data)
                        } else if (sequence == 3) {
                            mViolationList.add(data)
                        } else if (sequence == 4) {
                            mOfficerList.add(data)
                        } else if (sequence == 5) {
                            mCommentsList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.isStatus_location_notes1.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.locationNotes1.nullSafety())) {
                        val data = CitationPrintSectionUtils.getLocationNotes1(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.mPrintLayoutOrderLocationNotes1 != null) mIssuranceModel?.citationData?.mPrintLayoutOrderLocationNotes1 else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                        val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        mPrintLayoutTitle[firstPosition.toInt()] = layoutSectionTitle
                        data.mSectionHeader = layoutSectionTitle
                        mCommentsList.add(data)
                    }
                }
                if (mIssuranceModel?.citationData?.isStatus_location_bottomtext.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.locationBottomText.nullSafety())) {
//                        val data = VehicleListModel()
    //                    data.offNameFirst = getString(R.string.scr_lbl_note1)
//                        data.offNameFirst = mIssuranceModel?.citationData?.locationBottomTextLabel
                        mPrintLayoutTitle[5] = mIssuranceModel?.citationData?.locationBottomTextLabel
//                        data.offTypeFirst = mIssuranceModel?.citationData?.locationBottomText
//                        data.mPrintOrder =
//                                mIssuranceModel?.citationData?.mPrintOrderLocationBottomText.nullSafety()
//                        val orderNumber =
//                                if (mIssuranceModel?.citationData?.mPrintLayoutOrderLocationBottomText != null) mIssuranceModel?.citationData?.mPrintLayoutOrderLocationBottomText else Constants.PRINT_LAYOUT_ORDER_SPARATER
//                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
//                        val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
//                        mPrintLayoutTitle[5] = layoutSectionTitle
//                        mCommentsList.add(data)
                    }
                }
                if (mIssuranceModel?.citationData?.isStatus_location_remarks.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.locationRemarks.nullSafety())) {
                        val data = CitationPrintSectionUtils.getLocationRemarks(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.mPrintLayoutOrderLocationRemarks != null) mIssuranceModel?.citationData?.mPrintLayoutOrderLocationRemarks else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        var layoutSectionTitle: String? = null
                        var sequence = 0
                        try {
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                            layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            sequence = firstPosition.toInt()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        var value = mIssuranceModel?.citationData?.mPrintOrderTicketDate.nullSafety()
                        value += 1.0
                        val dataExtraSpace = VehicleListModel()
                        dataExtraSpace.offNameFirst = " "
                        dataExtraSpace.offTypeFirst = " "
                        dataExtraSpace.mPrintOrder = value
                        if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA,ignoreCase = true)){
                            mPrintLayoutMap.put("COMMENT", sequence)// Layout order
                        }

                        mPrintLayoutTitle[sequence] = layoutSectionTitle
                        data.mSectionHeader = if(layoutSectionTitle!=null)layoutSectionTitle else ""
//                        printcommand = AppUtils.setXYforPrint(data,sequence,"remark",printcommand!!)
                        if (sequence == 1) {
                            mCitationList.add(data)
                        } else if (sequence == 2) {
                            mVehicleList.add(data)
                        } else if (sequence == 3) {
                            mViolationList.add(data)
                        } else if (sequence == 4) {
                            mOfficerList.add(data)
//                            mCommentsList.add(data)
                        } else if (sequence == 5) {
                            mCommentsList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.isStatus_location_remarks1.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.locationRemarks1.nullSafety())||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true
                        )) {
                        val data = CitationPrintSectionUtils.getLocationRemark1(mIssuranceModel)
    //                    data.type = mIssuranceModel?.citationData?.locationRemarks1Column!!.toInt()
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.mPrintLayoutOrderLocationRemarks1 != null) mIssuranceModel?.citationData?.mPrintLayoutOrderLocationRemarks1 else Constants.PRINT_LAYOUT_ORDER_SPARATER
//                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
//                        val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        var layoutSectionTitle: String? = null
                        var sequence = 0
                        try {
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                            layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            sequence = firstPosition.toInt()
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }

//                        val sequence = firstPosition.toInt()
                        var value = mIssuranceModel?.citationData?.mPrintOrderLocationRemarks1.nullSafety()
                        value += 1.0
                        val dataExtraSpace = VehicleListModel()
                        dataExtraSpace.offNameFirst = " ";
                        dataExtraSpace.offTypeFirst = " "
                        dataExtraSpace.mPrintOrder = value
                        mPrintLayoutMap.put("COMMENT", sequence)// Layout order
                        mPrintLayoutTitle[sequence] = layoutSectionTitle

                        if (sequence == 1) {
                            mCitationList.add(data)
//                            mCitationList.add(dataExtraSpace)
                        } else if (sequence == 2) {
                            mVehicleList.add(data)
//                            mVehicleList.add(dataExtraSpace)
                        } else if (sequence == 3) {
                            mViolationList.add(data)
//                            mViolationList.add(dataExtraSpace)
                        } else if (sequence == 4) {
                            mOfficerList.add(data)
                            if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)&&
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)&&
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)&&
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)&&
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)) {
//                                mOfficerList.add(dataExtraSpace)
                            }
                        } else if (sequence == 5) {
                            mCommentsList.add(data)
                            if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)&&
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) &&
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)
                                ) {
                                //mCommentsList.add(dataExtraSpace)
                            }
                        }
                    }
                }

                if (mIssuranceModel?.citationData?.isStatus_location_remarks2.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.locationRemarks2.nullSafety())) {
                        val data = CitationPrintSectionUtils.getLocationRemark2(mIssuranceModel)

                        val orderNumber =
                                if (mIssuranceModel?.citationData?.mPrintLayoutOrderLocationRemarks2 != null) mIssuranceModel?.citationData?.mPrintLayoutOrderLocationRemarks2 else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        var layoutSectionTitle: String? = null
                        var sequence = 0
                        try {
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                            layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            sequence = firstPosition.toInt()
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                        var value = mIssuranceModel?.citationData?.mPrintOrderTicketDate.nullSafety()
                        value += 1.0
                        val dataExtraSpace = VehicleListModel()
                        dataExtraSpace.offNameFirst = " "
                        dataExtraSpace.offTypeFirst = " "
                        dataExtraSpace.mPrintOrder = value
//                        mPrintLayoutMap.put("COMMENT", sequence)// Layout order
                        mPrintLayoutTitle[sequence] = layoutSectionTitle
                        data.mSectionHeader = if(layoutSectionTitle!=null) layoutSectionTitle else ""

//                        printcommand = AppUtils.setXYforPrint(data,sequence,"remark",printcommand!!)
                        if (sequence == 1) {
                            mCitationList.add(data)
                        } else if (sequence == 2) {
                            mVehicleList.add(data)
                        } else if (sequence == 3) {
                            mViolationList.add(data)
                        } else if (sequence == 4) {
                            mOfficerList.add(data)
                        } else if (sequence == 5) {
                            mCommentsList.add(data)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            /****
             * LOCATION DATA
             * TODO LOCATION PRINT SECTION
             */
            var mLocation = ""
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,ignoreCase = true)) {
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_block) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.block)) {
                        mLocation = mIssuranceModel!!.citationData!!.location!!.block.toString()
                    }
                }
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_direction) {
                    mLocation =
                            mLocation + " " + mIssuranceModel!!.citationData!!.location!!.direction
                }
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_street) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.street)) {
                        mLocation =
                                mLocation + " " + mIssuranceModel!!.citationData!!.location!!.street
                    }
                }
            } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VISTA_CA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK,ignoreCase = true))
            {
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_lot) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.lot)) {
                        mLocation = mLocation + mIssuranceModel!!.citationData!!.location!!.lot+" "
                    }
                }

                if (mIssuranceModel!!.citationData!!.location!!.isStatus_block) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.block)) {
                        mLocation = mLocation + mIssuranceModel!!.citationData!!.location!!.block
                    }
                }
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_direction &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)) {
                    mLocation =
                        mLocation + "  " + mIssuranceModel!!.citationData!!.location!!.direction
                }
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_street) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.street)) {
                        mLocation =
                            mLocation + " " + mIssuranceModel!!.citationData!!.location!!.street
                    }
                }
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_side) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.side)) {
                        mLocation =
                            mLocation + " " + mIssuranceModel!!.citationData!!.location!!.side
                    }
                }
            } else if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                    ignoreCase = true
                )
            ) {
                if (mIssuranceModel?.citationData?.location?.isStatus_lot.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.lot)) {
                        mLocation =
                            mLocation + mIssuranceModel!!.citationData!!.location!!.lot + " "
                    }
                }
                if (mIssuranceModel?.citationData?.location?.isStatus_direction.nullSafety()
                ) {
                    mLocation =
                        mLocation + "  " + mIssuranceModel?.citationData?.location?.direction.nullSafety()
                }

                if (mIssuranceModel?.citationData?.location?.isStatus_street.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.street.nullSafety())) {
                        mLocation =
                            mLocation + " " + mIssuranceModel?.citationData?.location?.street.nullSafety()
                    }
                }

                if (mIssuranceModel?.citationData?.location?.isStatus_side.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.side)) {
                        mLocation =
                            mLocation + " " + mIssuranceModel?.citationData?.location?.side.nullSafety()
                    }
                }
            }
            else {
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_block) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.block)) {
                        mLocation = mLocation + mIssuranceModel!!.citationData!!.location!!.block
                    }
                }
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_direction) {
                    mLocation =
                            mLocation + "  " + mIssuranceModel!!.citationData!!.location!!.direction
                }
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_street) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.street)) {
                        mLocation =
                                mLocation + " " + mIssuranceModel!!.citationData!!.location!!.street
                    }
                }
                if (mIssuranceModel!!.citationData!!.location!!.isStatus_side) {
                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.side)) {
                        mLocation =
                                mLocation + " " + mIssuranceModel!!.citationData!!.location!!.side
                    }
                }

//                if (mIssuranceModel!!.citationData!!.location!!.isStatus_lot) {
//                    if (!TextUtils.isEmpty(mIssuranceModel!!.citationData!!.location!!.lot)) {
//                        mLocation = mLocation  + " " +  mIssuranceModel!!.citationData!!.location!!.lot
//                    }
//                }
            }

            try {
                val (orderNumber, location) = CitationPrintSectionUtils.getLocation(this@LprPreviewActivity, sharedPreference, mLocation, mIssuranceModel)

               val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                val sequence = firstPosition.toInt()


                if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)&&
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VISTA_CA, ignoreCase = true) ||
                    !BuildConfig.FLAVOR.equals(DuncanBrandingApp13())&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)) {

                    try {
                        val layoutSectionTitle: String = orderNumber.split("#").toTypedArray()[1]
                        if (layoutSectionTitle.contains("LOCATION", ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA,ignoreCase = true)) {
                            mPrintLayoutMap.put("LOCATION", sequence)// Layout order
                        }
                        mPrintLayoutTitle[sequence] = layoutSectionTitle
                        location.mSectionHeader = layoutSectionTitle
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (sequence == 1) {
                    mCitationList.add(location)
                } else if (sequence == 2) {
                    mVehicleList.add(location)
                } else if (sequence == 3) {
                    mViolationList.add(location)
                } else if (sequence == 4) {
                    mOfficerList.add(location)
                } else if (sequence == 5) {
                    mCommentsList.add(location)
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            if (mIssuranceModel!!.citationData!!.location!!.isStatus_direction &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, true)
            ) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.direction.nullSafety())||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true)) {
                    val location = CitationPrintSectionUtils.getDirection(mIssuranceModel)
                    val orderNumber =
                            if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderDirection != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderDirection.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber.split("#").toTypedArray()[0]
                    val sequence = firstPosition.toInt()

                    if (sequence == 1) {
                        mCitationList.add(location)
                    } else if (sequence == 2) {
                        mVehicleList.add(location)
                    } else if (sequence == 3) {
                        mViolationList.add(location)
                    } else if (sequence == 4) {
                        mOfficerList.add(location)
                    } else if (sequence == 5) {
                        mCommentsList.add(location)
                    }
                }
            }
            if (mIssuranceModel!!.citationData!!.location!!.isStatus_meter_name
//                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
            ) {
//            if (true) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.meterName)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)) {
                    val location = CitationPrintSectionUtils.getMeterName(sharedPreference, mIssuranceModel)

                    val orderNumber =
                            if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderMeterName != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderMeterName.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber.split("#").toTypedArray()[0]
                    val sequence = firstPosition.toInt()

                    if (sequence == 1) {
                        mCitationList.add(location)
                    } else if (sequence == 2) {
                        mVehicleList.add(location)
                    } else if (sequence == 3) {
                        mViolationList.add(location)
                    } else if (sequence == 4) {
                        mOfficerList.add(location)
                    } else if (sequence == 5) {
                        mCommentsList.add(location)
                    }
                }
            }
            if (mIssuranceModel?.citationData?.location?.isStatus_lot.nullSafety()
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VISTA_CA,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK,ignoreCase = true)) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.lot.nullSafety())||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)) {
                    val location = CitationPrintSectionUtils.getLot(mIssuranceModel)
                    val orderNumber =
                            if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderLot != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderLot.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber.split("#").toTypedArray()[0]
//                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                    val sequence = firstPosition.toInt()

                    if (sequence == 1) {
                        mCitationList.add(location)
                    } else if (sequence == 2) {
                        mVehicleList.add(location)
                    } else if (sequence == 3) {
                        mViolationList.add(location)
                    } else if (sequence == 4) {
                        mOfficerList.add(location)
                    } else if (sequence == 5) {
                        mCommentsList.add(location)
                    }
                }
            }

            if (mIssuranceModel?.citationData?.location?.isStatus_block.nullSafety()
                && (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true))) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.block.nullSafety())) {
                    val block = CitationPrintSectionUtils.getBlock(mIssuranceModel)
                    val orderNumber =
                            if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderblock != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderblock.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber.split("#").toTypedArray()[0]
                    val sequence = firstPosition.toInt()

                    if (sequence == 1) {
                        mCitationList.add(block)
                    } else if (sequence == 2) {
                        mVehicleList.add(block)
                    } else if (sequence == 3) {
                        mViolationList.add(block)
                    } else if (sequence == 4) {
                        mOfficerList.add(block)
                    } else if (sequence == 5) {
                        mCommentsList.add(block)
                    }
                }
            }

            if (mIssuranceModel?.citationData?.location?.isStatus_space_name.nullSafety()
//                    && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
            ) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.spaceName)) {
                    val location = CitationPrintSectionUtils.getSpaceName(mIssuranceModel)
                    val orderNumber =
                            if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderSpaceName != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderSpaceName.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber.split("#").toTypedArray()[0]
//                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                    val sequence = firstPosition.toInt()

                    if (sequence == 1) {
                        mCitationList.add(location)
                    } else if (sequence == 2) {
                        mVehicleList.add(location)
                    } else if (sequence == 3) {
                        mViolationList.add(location)
                    } else if (sequence == 4) {
                        mOfficerList.add(location)
                    } else if (sequence == 5) {
                        mCommentsList.add(location)
                    }
                }
            }

            if (mIssuranceModel?.citationData?.location?.isStatus_CityZone.nullSafety()) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.cityZone)) {
                    val data = CitationPrintSectionUtils.getCityZone(this@LprPreviewActivity,mIssuranceModel)
                    val orderNumber =
                            if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderCityZone != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderCityZone.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber.split("#").toTypedArray()[0]
                    val sequence = firstPosition.toInt()

                    if (sequence == 1) {
                        mCitationList.add(data)
                    } else if (sequence == 2) {
                        mVehicleList.add(data)
                    } else if (sequence == 3) {
                        mViolationList.add(data)
                    } else if (sequence == 4) {
                        mOfficerList.add(data)
                    } else if (sequence == 5) {
                        mCommentsList.add(data)
                    }
                }
            }
            if (mIssuranceModel?.citationData?.location?.isStatus_PcbZone.nullSafety()) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.pcbZone.nullSafety())) {
                    val data = CitationPrintSectionUtils.getPCBZone(this@LprPreviewActivity,mIssuranceModel)
                    val orderNumber =
                            if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderPcbZone != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderPcbZone.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber.split("#").toTypedArray()[0]
                    val sequence = firstPosition.toInt()

                    if (sequence == 1) {
                        mCitationList.add(data)
                    } else if (sequence == 2) {
                        mVehicleList.add(data)
                    } else if (sequence == 3) {
                        mViolationList.add(data)
                    } else if (sequence == 4) {
                        mOfficerList.add(data)
                    } else if (sequence == 5) {
                        mCommentsList.add(data)
                    }
                }
            }

            /****
             * VEHICLE DATA
             * TODO VEHICLE PRINT SECTION
             */
            val appCompatTextViewVehileTitle: AppCompatTextView =
                    linearLayoutCompatVehicle.findViewById(R.id.textview_vehile_title)
            try {
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_license_plate.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.licensePlate.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getLicensePlate(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderLicensePlate != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderLicensePlate else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                        val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
                        mPrintLayoutMap.put("VEHICLE", sequence)// Layout order
                        mPrintLayoutTitle[sequence] = layoutSectionTitle
                        Vehdata.mSectionHeader = layoutSectionTitle

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_state.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.state.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getLicensePlateState(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderState != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderState else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_make.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.make.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getVehicleMake(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderMake != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderMake else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                        val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
                        mPrintLayoutMap.put("VEHICLE", sequence)// Layout order
                        mPrintLayoutTitle[sequence] = layoutSectionTitle
                        Vehdata.mSectionHeader = layoutSectionTitle

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_model.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.model.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getVehicleModel(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderModel != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderModel else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.nullSafety().split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.nullSafety().split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_color.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.color.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getVehicleColor(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderColor != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderColor else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_body_style.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.bodyStyle.nullSafety())||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true)) {

                        val Vehdata = CitationPrintSectionUtils.getVehicleBodyStyle(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderBodyStyle != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderBodyStyle else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_decal_year.nullSafety()) {
//                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.decalYear.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getDecalYear(mIssuranceModel)

                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderDecalYear != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderDecalYear else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
//                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_decal_number.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.decalNumber.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getDecalNumber(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderDecalNumber != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderDecalNumber else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_vin_number.nullSafety()) {
    //                if (!TextUtils.isEmpty(mIssuranceModel.getCitationData().getVehicle().getVinNumber())) {
                    val Vehdata = CitationPrintSectionUtils.getVinNumber(mIssuranceModel)
                    val orderNumber =
                            if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderVinNumber != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderVinNumber else Constants.PRINT_LAYOUT_ORDER_SPARATER
                    val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                    val sequence = firstPosition.toInt()
                    mPrintLayoutMap.put("VEHICLE", sequence)// Layout order

                    if (sequence == 1) {
                        mCitationList.add(Vehdata)
                    } else if (sequence == 2) {
                        mVehicleList.add(Vehdata)
                    } else if (sequence == 3) {
                        mViolationList.add(Vehdata)
                    } else if (sequence == 4) {
                        mOfficerList.add(Vehdata)
                    } else if (sequence == 5) {
                        mCommentsList.add(Vehdata)
                    }
                    //                }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_expiration.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.expiration.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getVehicleExpiration(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderExpiration != null) mIssuranceModel?.citationData?.vehicle?.mPrintLayoutOrderExpiration else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            /****
             * VIOLATION DATA
             * TODO VIOLATION PRINT SECTION
             */
            val appCompatTextViewViolationTitle: AppCompatTextView =
                    linearLayoutCompatViolation.findViewById(R.id.textview_violation_title)
            try {
                if (mIssuranceModel?.citationData?.voilation?.isStatus_code.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.code.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getViolationCode(this@LprPreviewActivity,mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderCode != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderCode else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                        val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
                        mPrintLayoutMap.put("VIOLATION", sequence)// Layout order
                        mPrintLayoutTitle[sequence] = layoutSectionTitle
                        Vehdata.mSectionHeader = layoutSectionTitle

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.voilation?.isStatus_amount.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.amount.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getViolationAmount(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderAmount != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderAmount else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
                        mPrintLayoutMap.put("VIOLATION", sequence)// Layout order
                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }

                //Due Date: If Paid After
                if (mIssuranceModel?.citationData?.voilation?.isStatus_amount_due_date.nullSafety()) {
                    if (mIssuranceModel?.citationData?.voilation?.amountDueDate!=null && !TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.amountDueDate.nullSafety()) &&
                        !mIssuranceModel?.citationData?.voilation?.amountDueDate!!.equals("0.0")||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL,ignoreCase = true)) {
                        val Vehdata = CitationPrintSectionUtils.getAmountDueDate(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderAmountDueDate != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderAmountDueDate else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }

                //Due Date 10/15
                if (mIssuranceModel?.citationData?.voilation?.isStatus_due_date.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.dueDate.nullSafety())&&
                        (mIssuranceModel?.citationData?.voilation?.dueDate.nullSafety().toDouble()>0)) {
                        val Vehdata = CitationPrintSectionUtils.getDueDate(mIssuranceModel)

                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDate != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDate else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }

                //Due Date 30
                if (mIssuranceModel?.citationData?.voilation?.isStatus_due_date_30.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.dueDate30.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getDueDate30(mIssuranceModel)

                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDate30 != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDate30 else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }

                //Due Date 45
                if (mIssuranceModel?.citationData?.voilation?.isStatus_due_date_45.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.dueDate45.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getDueDate45(mIssuranceModel)

                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDate45 != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDate45 else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.voilation?.isStatus_due_date_cost.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getDueDateCost(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDateCost != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDateCost else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }

                if (mIssuranceModel?.citationData?.voilation?.isStatus_due_date_total.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.dueDateTotal.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getDueDateTotal(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDateTotal != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderDueDateTotal else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]

    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
//                        printcommand = AppUtils.setXYforPrint(Vehdata,sequence,"violation",printcommand!!)
                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }

                    if (mIssuranceModel?.citationData?.voilation?.isStatus_VioType.nullSafety()) {
                        if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.vioType.nullSafety())) {
                            val Vehdata = CitationPrintSectionUtils.getViolationVioType(this@LprPreviewActivity,mIssuranceModel)
                            val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderVioType != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderVioType else Constants.PRINT_LAYOUT_ORDER_SPARATER
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                            val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            val sequence = firstPosition.toInt()
                            mPrintLayoutMap.put("VIOLATION", sequence)// Layout order
                            mPrintLayoutTitle[sequence] = layoutSectionTitle
                            Vehdata.mSectionHeader = layoutSectionTitle

                            if (sequence == 1) {
                                mCitationList.add(Vehdata)
                            } else if (sequence == 2) {
                                mVehicleList.add(Vehdata)
                            } else if (sequence == 3) {
                                mViolationList.add(Vehdata)
                            } else if (sequence == 4) {
                                mOfficerList.add(Vehdata)
                            } else if (sequence == 5) {
                                mCommentsList.add(Vehdata)
                            }
                        }
                    }

                    if (mIssuranceModel?.citationData?.voilation?.isStatus_VioTypeCode.nullSafety()) {
                        if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.vioTypeCode.nullSafety())) {
                            val Vehdata = CitationPrintSectionUtils.getViolationVioTypeCode(this@LprPreviewActivity,mIssuranceModel)
                            val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderVioTypeCode != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderVioTypeCode else Constants.PRINT_LAYOUT_ORDER_SPARATER
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                            val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            val sequence = firstPosition.toInt()
                            mPrintLayoutMap.put("VIOLATION", sequence)// Layout order
                            mPrintLayoutTitle[sequence] = layoutSectionTitle
                            Vehdata.mSectionHeader = layoutSectionTitle

                            if (sequence == 1) {
                                mCitationList.add(Vehdata)
                            } else if (sequence == 2) {
                                mVehicleList.add(Vehdata)
                            } else if (sequence == 3) {
                                mViolationList.add(Vehdata)
                            } else if (sequence == 4) {
                                mOfficerList.add(Vehdata)
                            } else if (sequence == 5) {
                                mCommentsList.add(Vehdata)
                            }
                        }
                    }

                    if (mIssuranceModel?.citationData?.voilation?.isStatus_VioTypeDescription.nullSafety()) {
                        if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.vioTypeDescription.nullSafety())) {
                            val Vehdata = CitationPrintSectionUtils.getViolationVioTypeDescription(this@LprPreviewActivity,mIssuranceModel)
                            val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderVioTypeDescription != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderVioTypeDescription else Constants.PRINT_LAYOUT_ORDER_SPARATER
                            val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                            val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                            val sequence = firstPosition.toInt()
                            mPrintLayoutMap.put("VIOLATION", sequence)// Layout order
                            mPrintLayoutTitle[sequence] = layoutSectionTitle
                            Vehdata.mSectionHeader = layoutSectionTitle

                            if (sequence == 1) {
                                mCitationList.add(Vehdata)
                            } else if (sequence == 2) {
                                mVehicleList.add(Vehdata)
                            } else if (sequence == 3) {
                                mViolationList.add(Vehdata)
                            } else if (sequence == 4) {
                                mOfficerList.add(Vehdata)
                            } else if (sequence == 5) {
                                mCommentsList.add(Vehdata)
                            }
                        }
                    }

                if (mIssuranceModel?.citationData?.voilation?.isStatus_pay_at_online.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.payAtOnline.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getPayAtOnline(mIssuranceModel)
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderPayAtOnline != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderPayAtOnline else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]

    //                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
//                        printcommand = AppUtils.setXYforPrint(Vehdata,sequence,"violation",printcommand!!)
                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.voilation?.isStatus_location_descr.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.locationDescr.nullSafety())) {
    //                    appCompatTextViewDescriptions.setText(mIssuranceModel.getCitationData().getVoilation().getLocationDescr());
    //                    appCompatTextViewDescriptions.setVisibility(View.VISIBLE);
                        val Vehdata = CitationPrintSectionUtils.getLocationDesc(mIssuranceModel)
    //                    Vehdata.type = mIssuranceModel?.citationData?.voilation?.locationDescrColumn!!.toInt()
                        //                    mViolationList.add(Vehdata);
                        val orderNumber =
                                if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderLocationDescr != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderLocationDescr else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                        val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()
                        mPrintLayoutMap.put("VIOLATION", sequence)// Layout order
                        mPrintLayoutTitle[sequence] = layoutSectionTitle
                        Vehdata.mSectionHeader = layoutSectionTitle

                        if (sequence == 1) {
                            mCitationList.add(Vehdata)
                        } else if (sequence == 2) {
                            mVehicleList.add(Vehdata)
                        } else if (sequence == 3) {
                            mViolationList.add(Vehdata)
                        } else if (sequence == 4) {
                            mOfficerList.add(Vehdata)
                        } else if (sequence == 5) {
                            mCommentsList.add(Vehdata)
                        }
                    }
                } else {
    //                linearLayoutCompatViolationDesc.setVisibility(View.GONE);
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            try {
                Collections.sort(mViolationList, object : Comparator<VehicleListModel?> {
                    override fun compare(u1: VehicleListModel?, u2: VehicleListModel?): Int {
                        return java.lang.Double.compare(u1?.mPrintOrder.nullSafety(),
                                u2?.mPrintOrder.nullSafety())
                    }
                })
                Collections.sort(mVehicleList, object : Comparator<VehicleListModel?> {
                    override fun compare(u1: VehicleListModel?, u2: VehicleListModel?): Int {
                        return java.lang.Double.compare(u1?.mPrintOrder.nullSafety(),
                                u2?.mPrintOrder.nullSafety())
                    }
                })
                Collections.sort(mCitationList, object : Comparator<VehicleListModel?> {
                    override fun compare(u1: VehicleListModel?, u2: VehicleListModel?): Int {
                        return java.lang.Double.compare(u1?.mPrintOrder.nullSafety(),
                                u2?.mPrintOrder.nullSafety())
                    }
                })
                Collections.sort(mOfficerList, object : Comparator<VehicleListModel?> {
                    override fun compare(u1: VehicleListModel?, u2: VehicleListModel?): Int {
                        return java.lang.Double.compare(u1?.mPrintOrder.nullSafety(),
                                u2?.mPrintOrder.nullSafety())
                    }
                })
                Collections.sort(mCommentsList, object : Comparator<VehicleListModel?> {
                    override fun compare(u1: VehicleListModel?, u2: VehicleListModel?): Int {
                        return java.lang.Double.compare(u1?.mPrintOrder.nullSafety(),
                                u2?.mPrintOrder.nullSafety())
                    }
                })

                if (!BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)) {
                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true
                        )  || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PARKX, ignoreCase = true
                        )
                    ) {
                        mOfficerList =
                            checkPrintLayoutOrderForTwoColumn(
                                mOfficerList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mViolationList =
                            checkPrintLayoutOrderForTwoColumn(
                                mViolationList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mVehicleList =
                            checkPrintLayoutOrderForTwoColumn(
                                mVehicleList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mCitationList =
                            checkPrintLayoutOrderForTwoColumn(
                                mCitationList,
                                ""
                            ) as ArrayList<VehicleListModel>
//                    if (mCommentsList != null && mCommentsList.size > 0)
                        mCommentsList = checkPrintLayoutOrderForTwoColumn(
                            mCommentsList, "Comment"
                        ) as ArrayList<VehicleListModel>


                    } else {
                        mOfficerList =
                            checkPrintLayoutOrder(mOfficerList, "") as ArrayList<VehicleListModel>
                        mViolationList =
                            checkPrintLayoutOrder(mViolationList, "") as ArrayList<VehicleListModel>
                        mVehicleList =
                            checkPrintLayoutOrder(mVehicleList, "") as ArrayList<VehicleListModel>
                        mCitationList =
                            checkPrintLayoutOrder(mCitationList, "") as ArrayList<VehicleListModel>
                        if (mCommentsList != null && mCommentsList.size > 0)
                            if (BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_OCEANCITY,
                                    ignoreCase = true
                                )||
                            BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_EPHRATA,
                                    ignoreCase = true
                                )||
                            BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_Easton,
                                    ignoreCase = true
                                )||
                            BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_CLIFTON,
                                    ignoreCase = true
                                )
                            ) {
                                mCommentsList = checkPrintLayoutOrder(
                                    mCommentsList, "Comment"
                                ) as ArrayList<VehicleListModel>
                            } else {
                                mCommentsList = checkPrintLayoutOrderComment(
                                    mCommentsList, "Comment"
                                ) as ArrayList<VehicleListModel>
                            }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /** 1 mCitationList
     * 2  mVehicleList
     * 3  mViolationList
     * 4 mOfficerList
     * 5 mCommentsList */
    private fun generateFacsimileImage() {

        linearLayoutMainPrint.visibility = View.VISIBLE
        linearLayoutMainScreenView.visibility = View.GONE
        btnPrint.visibility = View.GONE
        layButtonsHide.visibility = View.GONE
        laySignature.visibility = View.GONE
        linearLayoutCompatChildContainer.removeAllViews()

        if (LogUtil.isEnableCommandBasedFacsimile) {
            ivCommandBasedFacsimile.showView()
            linearLayoutCompatChildContainer.invisibleView()
        }

        val linearLayoutCompatVehicle = this.layoutInflater.inflate(
                R.layout.content_print_vehicle, null) as LinearLayoutCompat
        val linearLayoutCompatCitation = this.layoutInflater.inflate(
                R.layout.content_print_citation_layout, null) as LinearLayoutCompat
        val linearLayoutCompatOfficer = this.layoutInflater.inflate(
                R.layout.content_print_officer_details, null) as LinearLayoutCompat
        val linearLayoutCompatRemark = this.layoutInflater.inflate(
                R.layout.content_print_remark_notes_signature, null) as LinearLayoutCompat
        val linearLayoutCompatViolation = this.layoutInflater.inflate(
                R.layout.content_print_violation, null) as LinearLayoutCompat

//        val content_print_height_test = this.layoutInflater.inflate(
//            R.layout.content_print_height_test, null) as LinearLayoutCompat
//
//        val content_print_height_test_vio = this.layoutInflater.inflate(
//            R.layout.content_print_height_test_vio, null) as LinearLayoutCompat

        var appComTextViewPrintUrl: AppCompatTextView? = null
        var appCompatImageViewQRCode: AppCompatImageView? = null
        var appCompatImageViewLprImage: AppCompatImageView? = null
        var linearLayoutCompatPrintUrl: LinearLayoutCompat? = null
        var appComTextViewQRCodeLabel: AppCompatTextView? = null

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)) {
            appComTextViewPrintUrl =
                linearLayoutCompatRemark!!.findViewById(R.id.text_print_url)
            appCompatImageViewQRCode =
                linearLayoutCompatRemark!!.findViewById(R.id.iv_print_url)
            appCompatImageViewLprImage =
                linearLayoutCompatRemark!!.findViewById(R.id.iv_lprImage)
            linearLayoutCompatPrintUrl =
                linearLayoutCompatRemark!!.findViewById(R.id.ll_qr_bottomview)
            linearLayoutCompatQrCodeAndMessage =
                linearLayoutCompatRemark!!.findViewById(R.id.ll_qrcode_message)
            appComTextViewQRCodeLabel =
                linearLayoutCompatRemark!!.findViewById(R.id.text_qr_code_label)
        }else {
            appComTextViewPrintUrl = linearLayoutCompatBarCode!!.findViewById(R.id.text_print_url)
            appCompatImageViewQRCode =
                linearLayoutCompatBarCode!!.findViewById(R.id.iv_print_url)
            appCompatImageViewLprImage =
                linearLayoutCompatBarCode!!.findViewById(R.id.iv_lprImage)
            linearLayoutCompatPrintUrl =
                linearLayoutCompatBarCode!!.findViewById(R.id.ll_qr_bottomview)
            linearLayoutCompatQrCodeAndMessage =
                linearLayoutCompatBarCode!!.findViewById(R.id.ll_qrcode_message)
            appComTextViewQRCodeLabel =
                linearLayoutCompatBarCode!!.findViewById(R.id.text_qr_code_label)
        }

        val appCompatTextViewCitaitonTitle: AppCompatTextView =
                linearLayoutCompatCitation.findViewById(R.id.textview_citation_title)
        val appCompatTextViewViolationTitle: AppCompatTextView =
                linearLayoutCompatViolation.findViewById(R.id.textview_violation_title)
        val appCompatTextViewVehileTitle: AppCompatTextView =
                linearLayoutCompatVehicle.findViewById(R.id.textview_vehile_title)

        var linearLayoutCompatSignature:LinearLayoutCompat?= null
        var appCompatImageViewSignature: AppCompatImageView? = null
        var appCompatTextViewCommentsTitle: AppCompatTextView? = null

        var linearLayoutCompatWarnin:LinearLayoutCompat?= null
        var appCompatTextViewCommentsWarningValue: AppCompatTextView? = null
        var appCompatTextViewCommentsWarningLable: AppCompatTextView? = null

        var linearLayoutCompatFooter3:LinearLayoutCompat?= null
        var appCompatTextViewCommentsFooter3Value: AppCompatTextView? = null
        var appCompatTextViewCommentsFooter3Lable: AppCompatTextView? = null

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM,ignoreCase = true)) {
            linearLayoutCompatSignature =
                linearLayoutCompatCitation.findViewById(R.id.ll_signature)
            appCompatImageViewSignature =
                linearLayoutCompatCitation.findViewById(R.id.imgSignature_print)
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
            linearLayoutCompatSignature =
                linearLayoutCompatOfficer.findViewById(R.id.ll_signature)
            appCompatImageViewSignature =
                linearLayoutCompatOfficer.findViewById(R.id.imgSignature_print)
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)
            ){
            appCompatTextViewCommentsWarningValue =
                linearLayoutCompatRemark.findViewById(R.id.appctextviewWarningValue)
            appCompatTextViewCommentsWarningLable =
                linearLayoutCompatRemark.findViewById(R.id.appctextviewWarningLabel)
            linearLayoutCompatWarnin =
                linearLayoutCompatRemark.findViewById(R.id.ll_warning)

            appCompatTextViewCommentsFooter3Value =
                linearLayoutCompatRemark.findViewById(R.id.appctextviewFooter3Value)
            appCompatTextViewCommentsFooter3Lable =
                linearLayoutCompatRemark.findViewById(R.id.appctextviewFooter3Label)
            linearLayoutCompatFooter3 =
                linearLayoutCompatRemark.findViewById(R.id.ll_footer3)
        }else {
            linearLayoutCompatSignature =
                linearLayoutCompatRemark.findViewById(R.id.ll_signature)
            appCompatImageViewSignature =
                linearLayoutCompatRemark.findViewById(R.id.imgSignature_print)

        }

        appCompatTextViewCommentsTitle =
            linearLayoutCompatRemark.findViewById(R.id.textview_comments_title)

        val appCompatTextViewOfficerTitle: AppCompatTextView =
                linearLayoutCompatOfficer.findViewById(R.id.textview_officer_title)

        try { //**Signature**/
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)){
                linearLayoutCompatSignature!!.visibility = View.VISIBLE
                val file = File(mSignaturePath)
                if (!TextUtils.isEmpty(mSignaturePath) && file.exists()) {
                    mImageViewSignature.visibility = View.VISIBLE
                    mTextViewSignName.visibility = View.VISIBLE
                    appCompatImageViewSignature!!.setImageURI(Uri.fromFile(File(mSignaturePath)))
                }
            } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)) {
                linearLayoutCompatSignature?.hideView()
                val file = File(mSignaturePath)

                //This will only show the signature when there is a signature available
                if (!TextUtils.isEmpty(mSignaturePath) && file.exists()) {
                    linearLayoutCompatSignature?.showView()
                    appCompatImageViewSignature?.showView()
                    appCompatImageViewSignature?.setImageURI(Uri.fromFile(File(mSignaturePath)))
                }
            } else {
                if (linearLayoutCompatSignature != null) linearLayoutCompatSignature!!.visibility =
                    View.GONE
            }



        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (isQRCodePrintlayout) {
            appComTextViewPrintUrl.visibility = View.VISIBLE
            linearLayoutCompatPrintUrl.visibility = View.VISIBLE
            appCompatImageViewQRCode.visibility = View.VISIBLE
            mQRCodeValue?.let { GenerateQRCodeInnovaPrint(it, appCompatImageViewQRCode) }
//            mQRCodeValue?.let { generateQR(it,appCompatImageViewPrintUrl) }
//          appCompatImageViewPrintUrl!!.setImageBitmap(qrCodePrintBitmap)

            val qrCodeLabel = sharedPreference.read(
                SharedPrefKey.QRCODE_LABEL_FOR_PRINT,"").nullSafety()
            if (qrCodeLabel.isNotEmpty()){
                appComTextViewQRCodeLabel.showView()
                appComTextViewQRCodeLabel.text = qrCodeLabel
            }else{
                appComTextViewQRCodeLabel.hideView()
            }
        } else if(isBarCodePrintlayout && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA,true)){
            linearLayoutCompatPrintUrl.visibility = View.VISIBLE
        }else {
            appComTextViewPrintUrl.visibility = View.GONE
            linearLayoutCompatPrintUrl.visibility = View.GONE
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true))
            {
                appComTextViewPrintUrl.visibility = View.VISIBLE
                linearLayoutCompatPrintUrl.visibility = View.VISIBLE
            }
        }
        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)){
            appCompatTextViewTopMessage.visibility = View.VISIBLE
            appComTextViewPrintUrl.visibility = View.VISIBLE
            linearLayoutCompatPrintUrl.visibility = View.VISIBLE
            appCompatTextViewTopMessage!!.text =
                sharedPreference.read(SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT, "")?.split("#")
                    ?.get(0) ?: ""

            try {
                if(sharedPreference.read(SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT, "")?.split("#")!!.size>1) {
                    appComTextViewPrintUrl!!.text =
                        sharedPreference.read(SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT, "")
                            ?.split("#")
                            ?.get(1) ?: ""
                            }
            } catch (e: Exception) {
                e.printStackTrace()
                appComTextViewPrintUrl!!.text = ""
                appComTextViewPrintUrl.hideView()
            }
        }

        if (appComTextViewPrintUrl.text.isNullOrEmpty()){
            appComTextViewPrintUrl.hideView()
        }

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true))
            {
                appCompatTextViewTopMessage.visibility = View.GONE
            }
        if(isPrintLprImageInFacsimilePrint(getMyDatabase()!!))
        {
            if((sharedPreference.read(SharedPrefKey.LA_METRO_WARNING_MESSAGE_PRINT, ""))!!.length>6 && isWarningSelected) {
                linearLayoutCompatWarnin!!.visibility = View.VISIBLE
                appCompatTextViewCommentsWarningLable!!.text =
                    sharedPreference.read(SharedPrefKey.LA_METRO_WARNING_MESSAGE_PRINT, "")
                        ?.split("#")
                        ?.get(0) ?: ""
                appCompatTextViewCommentsWarningValue!!.text =
                    sharedPreference.read(SharedPrefKey.LA_METRO_WARNING_MESSAGE_PRINT, "")
                        ?.split("#")
                        ?.get(1) ?: ""
            }else{
                if(linearLayoutCompatWarnin!=null) linearLayoutCompatWarnin!!.visibility = View.GONE
            }

            try {
                if(bannerList!=null && bannerList!!.size>0 &&
                    bannerList!!.get(0)!!.citationImage!!.contains("anpr_")||bannerList!=null && bannerList!!.size>0 &&
                    bannerList!!.get(0)!!.citationImage!!.contains("Vin_"))
                {
                    val imgFileCopy = bannerList!!.get(0)!!.citationImage?.let { File(it) }
                    if (imgFileCopy!=null && imgFileCopy!!.exists()) {
                        linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                        appCompatImageViewLprImage.visibility = View.VISIBLE
                        appCompatImageViewLprImage.setImageURI(Uri.fromFile(imgFileCopy))
                    }
                }else{
                    appCompatImageViewLprImage.visibility = View.GONE
                    (linearLayoutCompatQrCodeAndMessage as LinearLayoutCompat.LayoutParams).weight = 2f

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


//        val printData : CitationInsurrancePrintData
//        printData!!.citationData!!.vehicle!!= mVehicleList

        /** 1 mCitationList
         * 2  mVehicleList
         * 3  mViolationList
         * 4 mOfficerList
         * 5 mCommentsList */
        if (checkBuildConfigForGenerateFacsimileImageMethodForRecursiveCall()) {

            val linearLayoutCompatBottomMargin = this.layoutInflater.inflate(
                    R.layout.content_print_location_details, null) as LinearLayoutCompat

            /**
             * set OCR Text
             */
            setDuncanSiteId()

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)){
                setDuncanSiteId()
                if (isOCRTextPrintlayout) {
                    setTextViewValueForVallejo()
                }
            }else{
                if (isOCRTextPrintlayout) {
                    setOCRTextValueOnView()
                }
            }
            /** 1 mCitationList
             * 2  mVehicleList
             * 3  mViolationList
             * 4 mOfficerList
             * 5 mCommentsList */
            // Sort the list
//            try {
                /**
                    Height is fixed for comment size comment is selected or not
                 */
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)){
                mPrintLayoutMap.put("COMMENT", 5)// Layout order
            }
                /*val resultd = mPrintLayoutMap.toList().sortedBy { (_, value) -> value }.toMap()
                var duplicate = 0
                var duplicateKey = ""
                val itrd = resultd.keys.iterator()
                while (itrd.hasNext()) {
                    val k = itrd.next()
                    val v: Int? = resultd[k]
                    if (duplicate == v) {
                        duplicateKey = k
                    }
                    duplicate = v!!
                }
                mPrintLayoutMap.remove(duplicateKey);
            }catch (e: Exception) {
                e.printStackTrace()
            }
            val result = try {
                mPrintLayoutMap.toList().sortedBy { (_, value) -> value }.toMap()
            } catch (e: Exception) {
                e.printStackTrace()
            }*/
            try {
                val resultd = mPrintLayoutMap.toList().sortedBy { (_, value) -> value }.toMap(LinkedHashMap())

                var duplicate = -1
                var duplicateKey = ""
                val itrd = resultd.keys.iterator()
                while (itrd.hasNext()) {
                    val k = itrd.next()
                    val v: Int? = resultd[k]
                    if (v == duplicate) {
                        duplicateKey = k
                        break
                    }
                    duplicate = v!!
                }

                if (duplicateKey.isNotEmpty()) {
                    mPrintLayoutMap.remove(duplicateKey)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

// Final sorted map without duplicate
            val result: Map<String, Int>? = try {
                mPrintLayoutMap.toList()
                    .sortedBy { (_, value) -> value }
                    .toMap(LinkedHashMap())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)) {
                //No need to add top margin for these sites
            }else {
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBottomMargin);
            }
            if(
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true)) {
                    linearLayoutCompatChildContainer.addView(appCompatTextViewTopMessage)
                }
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)){
                /**
                 * We are adding new line & barcode for XF print command
                 */
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_SOUTHMIAMI,
                        ignoreCase = true)) {

                    val vehicleListModelBarCode = VehicleListModel()

                    vehicleListModelBarCode.offNameFirst = mCitationNumberId
                    //Axis X Value Used for textAlignment
                    //Axis Y Value used for columnMaxSize
                    //mFontSizeInt Value used for selection of font
                    vehicleListModelBarCode.mAxisX = 2.0
                    vehicleListModelBarCode.mAxisY = 0.0
                    vehicleListModelBarCode.mFontSizeInt = 0
                    vehicleListModelBarCode.type = 3

                    AppUtils.printQueryStringBuilder = setXYforPrintBarCode(
                        vehicleListModelBarCode,
                        1,
                        "barcode",
                        AppUtils.printQueryStringBuilder
                    )
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
            }

            printLayoutRecursive(result as HashMap<String, Int>? , linearLayoutCompatCitation,
                    linearLayoutCompatViolation, linearLayoutCompatVehicle, linearLayoutCompatRemark,
                    linearLayoutCompatOfficer, appCompatTextViewCitaitonTitle, appCompatTextViewViolationTitle,
                    appCompatTextViewVehileTitle, appCompatTextViewCommentsTitle, appCompatTextViewOfficerTitle
            )
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)) {
            //PHILI site only
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mCitationList.size > 0) {
                        setAdapterForCitationList(mCitationList, linearLayoutCompatCitation)
                    }
                }, 200)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mViolationList.size > 0) {
                        setAdapterForViolationList(mViolationList, linearLayoutCompatViolation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mVehicleList.size > 0) {
                        setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mOfficerList.size > 0) {
                        setAdapterForOfficerList(mOfficerList, linearLayoutCompatOfficer)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }

            if (mCommentsList != null && mCommentsList.size > 0)
                Handler(Looper.getMainLooper()).postDelayed({
                    setAdapterForCommentesList(mCommentsList, linearLayoutCompatRemark)
                }, 200)



            try {
                appCompatTextViewCitaitonTitle.text = mPrintLayoutTitle[1]
                appCompatTextViewVehileTitle.text = mPrintLayoutTitle[2]
                appCompatTextViewViolationTitle.text = mPrintLayoutTitle[3]
                appCompatTextViewOfficerTitle.text = mPrintLayoutTitle[4]
                appCompatTextViewCommentsTitle.text = mPrintLayoutTitle[5]
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }

//                setAdapterForViolationList(mViolationList, linearLayoutCompatViolation)
//                setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle)
            setDuncanSiteId()
//            setTextViewValueDuncan()
            if (isOCRTextPrintlayout) {
                setOCRTextValueOnView()
            }

            val linearLayoutCompatBottomMargin = this.layoutInflater.inflate(
                    R.layout.content_print_location_details, null) as LinearLayoutCompat

            Handler(Looper.getMainLooper()).postDelayed({

               if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
                   BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
                   BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
                   BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
                   BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
                   BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)) {
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBottomMargin);
               }
                linearLayoutCompatChildContainer.addView(appCompatTextViewTopMessage)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatCitation)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatVehicle)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatViolation)

//                if (LogUtil.isPrintHeightDynamicFromTicket) {
//                    linearLayoutCompatChildContainer.addView(content_print_height_test)
//                    linearLayoutCompatChildContainer.addView(content_print_height_test_vio)
//                }

                if (mOfficerList.size>0) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                }else{
                    linearLayoutCompatOfficer.visibility= View.GONE
                }

                if (mCommentsList.size>0) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                }else{
                    linearLayoutCompatRemark.visibility= View.GONE
                }

//            QR CODE for ppa
                if (isQRCodePrintlayout) {
                    appComTextViewPrintUrl.visibility = View.VISIBLE
                    linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                    appCompatImageViewQRCode.visibility = View.VISIBLE
                    mQRCodeValue?.let { GenerateQRCodeInnovaPrint(it, appCompatImageViewQRCode) }

                    val qrCodeLabel = sharedPreference.read(
                        SharedPrefKey.QRCODE_LABEL_FOR_PRINT,"").nullSafety()
                    if (qrCodeLabel.isNotEmpty()){
                        appComTextViewQRCodeLabel.showView()
                        appComTextViewQRCodeLabel.text = qrCodeLabel
                    }else{
                        appComTextViewQRCodeLabel.hideView()
                    }
                } else if(isBarCodePrintlayout && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA,true)){
                    linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                }else {
                    appComTextViewPrintUrl.visibility = View.GONE
                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DUNCAN,
                            ignoreCase = true
                        )
                    ) {
                        linearLayoutCompatPrintUrl?.showView()
                        linearLayoutCompatQrCodeAndMessage?.hideView()
                    } else {
                        linearLayoutCompatPrintUrl.visibility = View.GONE
                    }

                    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true))
                    {
                        appComTextViewPrintUrl.visibility = View.VISIBLE
                        linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                    }
                }

                if(isPrintLprImageInFacsimilePrint(getMyDatabase()!!))
                {
                    try {
                        if(bannerList!=null && bannerList!!.size>0 &&
                            bannerList!!.get(0)!!.citationImage!!.contains("anpr_")||bannerList!=null && bannerList!!.size>0 &&
                            bannerList!!.get(0)!!.citationImage!!.contains("Vin_"))
                        {
                            val imgFileCopy = bannerList!!.get(0)!!.citationImage?.let { File(it) }
                            if (imgFileCopy!=null && imgFileCopy!!.exists()) {
                                linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                                appCompatImageViewLprImage.visibility = View.VISIBLE
                                appCompatImageViewLprImage.setImageURI(Uri.fromFile(imgFileCopy))
                            }
                        }else{
                            appCompatImageViewLprImage.visibility = View.GONE
                            (linearLayoutCompatQrCodeAndMessage as LinearLayoutCompat.LayoutParams).weight = 2f

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


                linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
            }, 200)
        } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)){
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mCitationList.size > 0) {
                        setAdapterForCitationList(mCitationList, linearLayoutCompatCitation)
                    }
                }, 200)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mViolationList.size > 0) {
                        setAdapterForViolationList(mViolationList, linearLayoutCompatViolation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mVehicleList.size > 0) {
                        setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mOfficerList.size > 0) {
                        setAdapterForOfficerList(mOfficerList, linearLayoutCompatOfficer)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }

            if (mCommentsList != null && mCommentsList.size > 0)
                Handler(Looper.getMainLooper()).postDelayed({
                    setAdapterForCommentesList(mCommentsList, linearLayoutCompatRemark)
                }, 200)

            try {
                appCompatTextViewCitaitonTitle.text = mPrintLayoutTitle[1]
                appCompatTextViewVehileTitle.text = mPrintLayoutTitle[2]
                appCompatTextViewViolationTitle.text = mPrintLayoutTitle[3]
                appCompatTextViewOfficerTitle.text = mPrintLayoutTitle[4]
                appCompatTextViewCommentsTitle.text = mPrintLayoutTitle[5]
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }

//                setAdapterForViolationList(mViolationList, linearLayoutCompatViolation)
//                setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle)
            setDuncanSiteId()
//            setTextViewValueDuncan()
            if (isOCRTextPrintlayout) {
                setOCRTextValueOnView()
            }

            val linearLayoutCompatBottomMargin = this.layoutInflater.inflate(
                R.layout.content_print_location_details, null) as LinearLayoutCompat

            Handler(Looper.getMainLooper()).postDelayed({
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBottomMargin)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                linearLayoutCompatChildContainer.addView(appCompatTextViewTopMessage)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatCitation)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatVehicle)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatViolation)
                if (mOfficerList.size>0) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                }else{
                    linearLayoutCompatOfficer.visibility= View.GONE
                }

                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                    if (mCommentsList.size==0) {
                        appCompatTextViewCommentsTitle.visibility = View.GONE
                    }
                }else{
                if (mCommentsList.size>0) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                }else{
                    linearLayoutCompatRemark.visibility= View.GONE
                }
                }

            }, 200)
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)) {
            //Glendale site only
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mCitationList.size > 0) {
                        setAdapterForCitationList(mCitationList, linearLayoutCompatCitation)
                    }
                }, 200)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mViolationList.size > 0) {
                        setAdapterForViolationList(mViolationList, linearLayoutCompatViolation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mVehicleList.size > 0) {
                        setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mOfficerList.size > 0) {
                        setAdapterForOfficerList(mOfficerList, linearLayoutCompatOfficer)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }

            if (mCommentsList != null && mCommentsList.size > 0)
                Handler(Looper.getMainLooper()).postDelayed({
                    setAdapterForCommentesList(mCommentsList, linearLayoutCompatRemark)
                }, 200)


            try {
                appCompatTextViewCitaitonTitle.text = mPrintLayoutTitle[1]
                appCompatTextViewVehileTitle.text = mPrintLayoutTitle[2]
                appCompatTextViewViolationTitle.text = mPrintLayoutTitle[3]
                appCompatTextViewOfficerTitle.text = mPrintLayoutTitle[4]
                appCompatTextViewCommentsTitle.text = mPrintLayoutTitle[5]
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity, getString(R.string.error_generate_facsimile_image))
            }
            setDuncanSiteId()
            if (isOCRTextPrintlayout) {
                setOCRTextValueOnView()
            }

            val linearLayoutCompatBottomMargin = this.layoutInflater.inflate(
                    R.layout.content_print_location_details, null) as LinearLayoutCompat

            Handler(Looper.getMainLooper()).postDelayed({
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBottomMargin);
                linearLayoutCompatChildContainer.addView(linearLayoutCompatCitation)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatVehicle)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatViolation)
//                linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
            }, 200)
        }
    }


    fun printLayoutRecursive(result:HashMap<String, Int>?,linearLayoutCompatCitation:LinearLayoutCompat
            ,linearLayoutCompatViolation:LinearLayoutCompat,linearLayoutCompatVehicle:LinearLayoutCompat
            ,linearLayoutCompatRemark:LinearLayoutCompat,linearLayoutCompatOfficer:LinearLayoutCompat
            ,appCompatTextViewCitaitonTitle:AppCompatTextView,appCompatTextViewViolationTitle:AppCompatTextView
            ,appCompatTextViewVehileTitle:AppCompatTextView,appCompatTextViewCommentsTitle:AppCompatTextView
                             ,appCompatTextViewOfficerTitle:AppCompatTextView)
    {
        if (result.isNullOrEmpty()){
            return
        }

        if(result?.keys?.elementAt(0).equals("CITATION",ignoreCase = false))
        {
            try {
                val v = result.get("CITATION")
                if (v == 1) {
                    setAdapterForCitationList(mCitationList, linearLayoutCompatCitation) //
                } else if (v == 2) {
                    setAdapterForCitationList(mVehicleList, linearLayoutCompatCitation) //
                } else if (v == 3) {
                    setAdapterForCitationList(mViolationList, linearLayoutCompatCitation) //
                } else if (v == 4) {
                    setAdapterForCitationList(mOfficerList, linearLayoutCompatCitation) //
                } else if (v == 5) {
                    setAdapterForCitationList(mCommentsList, linearLayoutCompatCitation) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatCitation)
                if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)) {
                    appCompatTextViewCitaitonTitle.text = mPrintLayoutTitle[v!!]
                }
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)) {
                    val mCitationNumberValue = sharedPreference.read(
                        SharedPrefKey.CITATION_NUMBER_FOR_PRINT,"").toString()
                    val mCitationNumberLable= sharedPreference.read(
                        SharedPrefKey.CITATION_NUMBER_LABEL_FOR_PRINT,"").toString()

                    appCompatTextViewCitaitonTitle.text = mCitationNumberLable+" "+mCitationNumberValue
                }
                recursiveIndex++
                result?.remove("CITATION")
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result?.keys?.elementAt(0).equals("VIOLATION",ignoreCase = false)){
            try {
                val v = result.get("VIOLATION")
                if (v == 1) {
                    setAdapterForViolationList(mCitationList, linearLayoutCompatViolation) //
                } else if (v == 2) {
                    setAdapterForViolationList(mVehicleList, linearLayoutCompatViolation) //
                } else if (v == 3) {
                    setAdapterForViolationList(mViolationList, linearLayoutCompatViolation) //
                } else if (v == 4) {
                    setAdapterForViolationList(mOfficerList, linearLayoutCompatViolation) //
                } else if (v == 5) {
                    setAdapterForViolationList(mCommentsList, linearLayoutCompatViolation) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatViolation)
                appCompatTextViewViolationTitle.text = mPrintLayoutTitle[v!!]

                if (recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }

                recursiveIndex++
                result?.remove("VIOLATION")
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result?.keys?.elementAt(0).equals("VEHICLE",ignoreCase = false)){
            try {
                val v = result?.get("VEHICLE")
                if (v == 1) {
                    setAdapterForVehicalList(mCitationList, linearLayoutCompatVehicle) //
                } else if (v == 2) {
                    setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle) //
                } else if (v == 3) {
                    setAdapterForVehicalList(mViolationList, linearLayoutCompatVehicle) //
                } else if (v == 4) {
                    setAdapterForVehicalList(mOfficerList, linearLayoutCompatVehicle) //
                } else if (v == 5) {
                    setAdapterForVehicalList(mCommentsList, linearLayoutCompatVehicle) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatVehicle)
//                if (BuildConfig.FLAVOR.equals(
//                        Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)) {
//                    appCompatTextViewVehileTitle!!.text =
//                        mPrintLayoutTitle[v!!] + " #" + mCitationNumberId
//                }else{
                    appCompatTextViewVehileTitle!!.text =
                        mPrintLayoutTitle[v!!]
//                }

                if (recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }
                recursiveIndex++
                result?.remove("VEHICLE")
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result?.keys?.elementAt(0).equals("COMMENT",ignoreCase = false)){
            try {
                val v = result.get("COMMENT")
                if (v == 1) {
                    setAdapterForCommentesList(mCitationList, linearLayoutCompatRemark) //
                } else if (v == 2) {
                    setAdapterForCommentesList(mVehicleList, linearLayoutCompatRemark) //
                } else if (v == 3) {
                    setAdapterForCommentesList(mViolationList, linearLayoutCompatRemark) //
                } else if (v == 4) {
                    setAdapterForCommentesList(mOfficerList, linearLayoutCompatRemark) //
                } else if (v == 5) {
                    /**
                     * We are going to add addtional comment header to print command
                     */
                    if (AppUtils.commentSectionTitle.isEmpty())
                        AppUtils.commentSectionTitle = mPrintLayoutTitle[v.nullSafety()].nullSafety()

                    setAdapterForCommentesList(mCommentsList, linearLayoutCompatRemark) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                appCompatTextViewCommentsTitle.text = mPrintLayoutTitle[v!!]

                if (recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, true))
                {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }
                recursiveIndex++
                result?.remove("COMMENT")


            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result?.keys?.elementAt(0).equals("OFFICER",ignoreCase = false)){
            try {
                val v = result?.get("OFFICER")
                if (v == 1) {
                    setAdapterForOfficerList(mCitationList, linearLayoutCompatOfficer) //
                } else if (v == 2) {
                    setAdapterForOfficerList(mVehicleList, linearLayoutCompatOfficer) //
                } else if (v == 3) {
                    setAdapterForOfficerList(mViolationList, linearLayoutCompatOfficer) //
                } else if (v == 4) {
                    setAdapterForOfficerList(mOfficerList, linearLayoutCompatOfficer) //
                } else if (v == 5) {
                    setAdapterForOfficerList(mCommentsList, linearLayoutCompatOfficer) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                appCompatTextViewOfficerTitle.text = mPrintLayoutTitle[v!!]

                if (recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }
                recursiveIndex++
                result?.remove("OFFICER")
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result?.containsKey("LOCATION").nullSafety()){
            try {
                val v = result?.get("LOCATION")
                if (v == 1) {
                    setAdapterForOfficerList(mCitationList, linearLayoutCompatOfficer) //
                } else if (v == 2) {
                    setAdapterForOfficerList(mVehicleList, linearLayoutCompatOfficer) //
                } else if (v == 3) {
                    setAdapterForOfficerList(mViolationList, linearLayoutCompatOfficer) //
                } else if (v == 4) {
                    setAdapterForOfficerList(mOfficerList, linearLayoutCompatOfficer) //
                } else if (v == 5) {
                    setAdapterForOfficerList(mCommentsList, linearLayoutCompatOfficer) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                appCompatTextViewOfficerTitle.text = mPrintLayoutTitle[v!!]

                if (recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }
                recursiveIndex++
                result?.remove("LOCATION")
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@LprPreviewActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            if(result.isNotEmpty() && isErrorOccurDuringGenerateFacsimile==false) {
                printLayoutRecursive(result, linearLayoutCompatCitation, linearLayoutCompatViolation, linearLayoutCompatVehicle, linearLayoutCompatRemark, linearLayoutCompatOfficer, appCompatTextViewCitaitonTitle, appCompatTextViewViolationTitle, appCompatTextViewVehileTitle, appCompatTextViewCommentsTitle, appCompatTextViewOfficerTitle)
            }
        }, 200)

    }

    override fun onActionSuccess(mErrorUploading:String) {
        LogUtil.printLog("==>Printer","onActionSuccess:$mErrorUploading")
        if(mErrorUploading.equals("UploadingError",ignoreCase = true) ||
                mErrorUploading.equals("RESCIND",ignoreCase = true)) {

            if(mErrorUploading.equals("RESCIND",ignoreCase = true)) {
                try {
                    //delete temporary images list
//                mDb?.dbDAO?.deleteTempImages()
                    mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
                    val fname = mCitationNumberId + "_"+FILE_NAME_FACSIMILE_PRINT_BITMAP+".jpg"
                    mPath = mPath + Constants.CAMERA + "/" + fname
                    val addPrinterImage = CitationImagesModel()
                    addPrinterImage.citationImage = mPath
                    //addPrinterImage.set();
                    bannerList.add(addPrinterImage)
                    bannerListForUpload.add(addPrinterImage)

                    //Use this if you want static header footer code
//                    if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SURF_CITY,
//                            ignoreCase = true
//                        ) || BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
//                            ignoreCase = true
//                        )|| BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_BURBANK,
//                            ignoreCase = true
//                        )|| BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_VOLUSIA,
//                            ignoreCase = true
//                        )|| BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CARTA,
//                            ignoreCase = true
//                        )|| BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SANDIEGO,
//                            ignoreCase = true
//                        )
//                    ) {
//                        //Upload image with Header Header
//                        val path = getFilePathOfTicketWithHeaderFooter(
//                            context = this@LprPreviewActivity,
//                            imagePostFix = FILE_NAME_HEADER_FOOTER_BITMAP,
//                            citationNumber = mCitationNumberId.nullSafety(),
//                            path = mPath.nullSafety()
//                        )
//
//                        if (path.isNotEmpty()) {
//                            val addPrinterImageUpload = CitationImagesModel()
//                            addPrinterImageUpload.citationImage = path
//                            bannerListForUpload.add(addPrinterImageUpload)
//                        }
//                    }


                    //Use this if you want dynamic header footer code
                    if (showAndEnableHeaderFooterInFacsimile && checkHeaderImageFileExist() && checkFooterImageFileExist() && !BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DUNCAN,
                            ignoreCase = true
                        )
                    ) {
                        //Upload image with Header Header
                        val pathOfTicketWithHeaderFooter = getFilePathOfTicketWithHeaderFooterDynamic(
                            imagePostFix = FILE_NAME_HEADER_FOOTER_BITMAP,
                            citationNumber = mCitationNumberId.nullSafety(),
                            path = mPath.nullSafety()
                        )

                        if (pathOfTicketWithHeaderFooter.isNotEmpty()) {
                            val addPrinterImageUpload = CitationImagesModel()
                            addPrinterImageUpload.citationImage = pathOfTicketWithHeaderFooter
                            bannerListForUpload.add(addPrinterImageUpload)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    //TODO OCR
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)) {
                        //delete temporary images list
//            mDb?.dbDAO?.deleteTempImages()
                        mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
                        val fname = mCitationNumberId +"_"+ FILE_NAME_FACSIMILE_OCR_BITMAP+".jpg"
                        mPath = mPath + Constants.CAMERA + "/" + fname
                        val addPrinterImage = CitationImagesModel()
                        addPrinterImage.citationImage = mPath
                        //addPrinterImage.set();
                        bannerList.add(addPrinterImage)
                        bannerListForUpload.add(addPrinterImage)

                        //Use this if you want static header footer code
//                        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)) {
//                            //Upload image with Header Header
//                            val pathOfTicketWithHeaderFooter = getFilePathOfTicketWithHeaderFooter(
//                                context = this@LprPreviewActivity,
//                                imagePostFix = FILE_NAME_HEADER_FOOTER_BITMAP,
//                                citationNumber = mCitationNumberId.nullSafety(),
//                                path = mPath.nullSafety()
//                            )
//
//                            if (pathOfTicketWithHeaderFooter.isNotEmpty()) {
//                                val addPrinterImageUploadWithHeaderFooter = CitationImagesModel()
//                                addPrinterImageUploadWithHeaderFooter.citationImage =
//                                    pathOfTicketWithHeaderFooter
//                                bannerListForUpload.add(addPrinterImageUploadWithHeaderFooter)
//                            }
//                        }


                        //Use this if you want dynamic header footer code
                        if (showAndEnableHeaderFooterInFacsimile && checkHeaderImageFileExist() && checkFooterImageFileExist() && BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_DUNCAN,
                                ignoreCase = true
                            )
                        ) {
                            //Upload image with Header Header
                            val pathOfTicketWithHeaderFooter =
                                getFilePathOfTicketWithHeaderFooterDynamic(
                                    imagePostFix = FILE_NAME_HEADER_FOOTER_BITMAP,
                                    citationNumber = mCitationNumberId.nullSafety(),
                                    path = mPath.nullSafety()
                                )

                            if (pathOfTicketWithHeaderFooter.isNotEmpty()) {
                                val addPrinterImageUploadWithHeaderFooter = CitationImagesModel()
                                addPrinterImageUploadWithHeaderFooter.citationImage =
                                    pathOfTicketWithHeaderFooter
                                bannerListForUpload.add(addPrinterImageUploadWithHeaderFooter)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mDb?.dbDAO?.updateCitationUploadStatus(1, mCitationNumberId)
            mIssuranceModel = mDb?.dbDAO?.getCitationWithTicket(mCitationNumberId)
            moveNextWithId("none")
        }else{
            callImageUploadAndTicket()
        }
    }

    private fun callImageUploadAndTicket() {
        mainScope.launch {
            delay(600)
            dismissLoader()
            moveToTicketDetails()
        }
    }

    private fun showRescindDialog(id: String) {
        val mDialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
        mDialog.setContentView(R.layout.dialog_add_note)
        val mEditTextNote: TextInputEditText = mDialog.findViewById(R.id.etNote)
        val textInputNote: TextInputLayout = mDialog.findViewById(R.id.input_textNote)
        val mEditTextStatus: AppCompatAutoCompleteTextView = mDialog.findViewById(R.id.etStatus)
        val textInputStatus: TextInputLayout = mDialog.findViewById(R.id.input_textStatus)
        val appCompatButton: AppCompatButton = mDialog.findViewById(R.id.btn_done)
//        val appCompatLayout: LinearLayoutCompat = mDialog.findViewById(R.id.layPopup)
        val tv_dialogTitle: AppCompatTextView = mDialog.findViewById(R.id.tv_dialogTitle)

        setAccessibilityForTextInputLayoutDropdownButtons(this@LprPreviewActivity, textInputStatus)

        appCompatButton.visibility = View.VISIBLE
        textInputNote.visibility = View.VISIBLE
        textInputStatus.visibility = View.VISIBLE
        setDropdownVoidReissue(mEditTextStatus)
        tv_dialogTitle.text = getString(R.string.msg_add_rescind_reasons)
        val appCompatImageView = mDialog.findViewById<ImageView>(R.id.btn_cancel)
        appCompatImageView.setOnClickListener { v: View? -> mDialog.dismiss() }
        appCompatButton.setOnClickListener {
            if(mEditTextStatus!!.text!!.toString().isEmpty()){
                mEditTextStatus?.requestFocus()
                mEditTextStatus?.isFocusable = true
                mEditTextStatus?.error = getString(R.string.val_msg_please_enter_rescind)
                //AppUtils.showKeyboard(LprDetails2Activity.this);
                LogUtil.printToastMSGForErrorWarning(
                        applicationContext,
                        getString(R.string.val_msg_please_enter_rescind)
                )
            }else {
                mDialog.dismiss()
                mRescindButton = "true"
                mTicketLable = "Rescind"
                cancelledStatus = "Rescind"
                mTicketActionButtonEvent = "Rescind"
                mRescindReason = mEditTextStatus.text.toString()
                mRescindNote = mEditTextNote.text.toString()
                isSkipPrinter = true

                try {
                    OfflineCancelCitationModel.uploadedCitationId = "none"
                    OfflineCancelCitationModel.lprNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                    OfflineCancelCitationModel.note = mRescindNote
                    OfflineCancelCitationModel.reason = mRescindReason
                    OfflineCancelCitationModel.type = m4ButtonType
                    OfflineCancelCitationModel.status = cancelledStatus
                    OfflineCancelCitationModel.ticketNumber = mIssuranceModel!!.citationData!!.ticketNumber.nullSafety()

                    mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!isInternetAvailable(this@LprPreviewActivity)) {
                    isErrorUploading = "RESCIND"
                }

                showProgressLoader(getString(R.string.scr_message_please_wait))
//            createViewToPrint()
                generateFacsimileImage()
                mainScope.launch {
                    addSomeTweakBeforeCommandBasedFacsimile()
                    delay(SAVEPRINTBITMAPDELAYTIME)
                    loadBitmapFromView(linearLayoutMainPrint, false) //layOfficerDetails);
                    val lockLprModel = LockLprModel()
                    lockLprModel.mLprNumber = ""
                    lockLprModel.mMake = ""
                    lockLprModel.mModel = ""
                    lockLprModel.mColor = ""
                    lockLprModel.mAddress = ""
                    lockLprModel.mViolationCode = ""
                    lockLprModel.ticketCategory = ""
                    setLprLock(lockLprModel, this@LprPreviewActivity, sharedPreference)
                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                }
            }
        }
        mDialog.show()
        val window = mDialog.window
        window!!.setLayout(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        )
    }

    //set value to Status dropdown
    private fun setDropdownVoidReissue(mEditTextStatus: AppCompatAutoCompleteTextView) {
        val pos = 0
        mEditTextStatus.setText("CITIZEN APPROACHED")
        mEditTextStatus.setSelection(mEditTextStatus.text.length)
        val mDropdownList = arrayOf("CITIZEN APPROACHED")
        val adapter = ArrayAdapter(
            mContext!!,
            R.layout.row_dropdown_menu_popup_item,
            mDropdownList)

        mEditTextStatus.threshold = 1
        mEditTextStatus.setAdapter(adapter)
        mEditTextStatus.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            // mSelectedShiftStat = mApplicationList.get(position);
            mEditTextStatus?.error = null
        }
    }

    /* Call Api For Ticket Rescind */
    private fun callTicketRescindApi(Id: String?, note: String, reason: String) {
        if (isInternetAvailable(this@LprPreviewActivity)) {
            val ticketCancelRequest = TicketCancelRequest()
            ticketCancelRequest.status = cancelledStatus
            ticketCancelRequest.mNote = note
            ticketCancelRequest.mReason = reason
            ticketCancelRequest.mType = m4ButtonType
            mTicketCancelViewModel?.hitTicketCancelApi(ticketCancelRequest, Id)

            try {
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"------------PREVIEW Cancel API-----------------")
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"REQUEST: "+ObjectMapperProvider.instance.writeValueAsString(ticketCancelRequest))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            printToastMSG(this@LprPreviewActivity,
                getString(R.string.err_msg_connection_was_refused))

            OfflineCancelCitationModel.uploadedCitationId = Id
            OfflineCancelCitationModel.lprNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
            OfflineCancelCitationModel.note = note
            OfflineCancelCitationModel.reason = reason
            OfflineCancelCitationModel.type = m4ButtonType
            OfflineCancelCitationModel.status = "Cancelled"
            OfflineCancelCitationModel.ticketNumber = mIssuranceModel!!.citationData!!.ticketNumber!!

            mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)
        }
    }

    /* Call Api For Ticket PBC Cancelled */
    private fun callTicketPBCCancelledApi(Id: String, note: String, reason: String) {
        if (isInternetAvailable(this@LprPreviewActivity)) {
            val ticketCancelRequest = TicketCancelRequest()
            ticketCancelRequest.status = "Cancelled"
            ticketCancelRequest.mNote = note
            ticketCancelRequest.mReason = reason
            mTicketCancelViewModel?.hitTicketCancelApi(ticketCancelRequest, Id)
        } else {
            printToastMSG(this@LprPreviewActivity,
                getString(R.string.err_msg_connection_was_refused))
        }
    }

    override fun onYesButtonClick() {
        if (lastSecondTag.equals("lastsecond", ignoreCase = true)) {
            mRescindButton = "true"
            mTicketLable = "Cancelled"
            mRescindReason = "PBC Cancel"
            cancelledStatus = "Cancelled"
            mRescindNote = "PBC Cancel"
            mTicketActionButtonEvent = "Cancelled"
            isSkipPrinter = false

            OfflineCancelCitationModel.uploadedCitationId = "none"
            OfflineCancelCitationModel.lprNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
            OfflineCancelCitationModel.note = mRescindNote
            OfflineCancelCitationModel.reason = mRescindReason
            OfflineCancelCitationModel.type = m4ButtonType
            OfflineCancelCitationModel.status = cancelledStatus
            OfflineCancelCitationModel.ticketNumber = mIssuranceModel!!.citationData!!.ticketNumber!!

            mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)

            showProgressLoader(getString(R.string.scr_message_please_wait))
//            createViewToPrint()
            generateFacsimileImage()
            mainScope.launch {
                addSomeTweakBeforeCommandBasedFacsimile()
                delay(SAVEPRINTBITMAPDELAYTIME)
                loadBitmapFromView(linearLayoutMainPrint,false) //layOfficerDetails);
                val lockLprModel = LockLprModel()
                lockLprModel.mLprNumber = ""
                lockLprModel.mMake = ""
                lockLprModel.mModel = ""
                lockLprModel.mColor = ""
                lockLprModel.mAddress = ""
                lockLprModel.mViolationCode = ""
                lockLprModel.ticketCategory = ""
                setLprLock(lockLprModel, this@LprPreviewActivity, sharedPreference)
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
            }
        } else if (lastSecondTag.equals("similarcitation", ignoreCase = true)) {
            mRescindButton = "true"
            mTicketLable = "Cancelled"
            mRescindReason = "Same Citation"
            cancelledStatus = "Cancelled"
            mRescindNote = "Same Citation"
            mTicketActionButtonEvent = "Cancelled"
            m4ButtonType = "similar_citation_cancel"
            isSkipPrinter = false

            OfflineCancelCitationModel.uploadedCitationId = "none"
            OfflineCancelCitationModel.lprNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
            OfflineCancelCitationModel.note = mRescindNote
            OfflineCancelCitationModel.reason = mRescindReason
            OfflineCancelCitationModel.type = m4ButtonType
            OfflineCancelCitationModel.status = cancelledStatus
            OfflineCancelCitationModel.ticketNumber = mIssuranceModel!!.citationData!!.ticketNumber!!

            mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)

            showProgressLoader(getString(R.string.scr_message_please_wait))
//            createViewToPrint()
            generateFacsimileImage()
            mainScope.launch {
                addSomeTweakBeforeCommandBasedFacsimile()
                delay(SAVEPRINTBITMAPDELAYTIME)
                loadBitmapFromView(linearLayoutMainPrint,isOCR = false) //layOfficerDetails);
                val lockLprModel = LockLprModel()
                lockLprModel.mLprNumber = ""
                lockLprModel.mMake = ""
                lockLprModel.mModel = ""
                lockLprModel.mColor = ""
                lockLprModel.mAddress = ""
                lockLprModel.mViolationCode = ""
                lockLprModel.ticketCategory = ""
                setLprLock(lockLprModel, this@LprPreviewActivity, sharedPreference)
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
            }
        }
        else if (lastSecondTag.equals(DynamicAPIPath.POST_IMAGE, ignoreCase = true)) {

        }
        else if (lastSecondTag.equals("ERROR", ignoreCase = true)||
            lastSecondTag.equals("lastsecondreturnfalse", ignoreCase = true)) {
            moveNextScreen()
        }

    }

    override fun onNoButtonClick() {
        btnPrint.enableButton()
    }
    override fun onYesButtonClickParam(msg: String?) {
         if (lastSecondTag.equals("lastsecond", ignoreCase = true)) {
            mRescindButton = "true"
            mTicketLable = "Cancelled"
            mRescindReason = "PBC Cancel"
            cancelledStatus = "Cancelled"
            mRescindNote = "PBC Cancel"
            mTicketActionButtonEvent = "Cancelled"
             isSkipPrinter = false

             OfflineCancelCitationModel.uploadedCitationId = "none"
             OfflineCancelCitationModel.lprNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
             OfflineCancelCitationModel.note = mRescindNote
             OfflineCancelCitationModel.reason = mRescindReason
             OfflineCancelCitationModel.type = m4ButtonType
             OfflineCancelCitationModel.status = cancelledStatus
             OfflineCancelCitationModel.ticketNumber = mIssuranceModel!!.citationData!!.ticketNumber!!

             mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)

            showProgressLoader(getString(R.string.scr_message_please_wait))
//            createViewToPrint()
             generateFacsimileImage()
             mainScope.launch {
                 addSomeTweakBeforeCommandBasedFacsimile()
                 delay(SAVEPRINTBITMAPDELAYTIME)
                loadBitmapFromView(linearLayoutMainPrint,isOCR = false) //layOfficerDetails);
                val lockLprModel = LockLprModel()
                lockLprModel.mLprNumber = ""
                lockLprModel.mMake = ""
                lockLprModel.mModel = ""
                lockLprModel.mColor = ""
                lockLprModel.mAddress = ""
                lockLprModel.mViolationCode = ""
                 lockLprModel.ticketCategory = ""
                setLprLock(lockLprModel, this@LprPreviewActivity, sharedPreference)
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
            }
        } else if (lastSecondTag.equals("similarcitation", ignoreCase = true)) {
            mRescindButton = "true"
            mTicketLable = "Cancelled"
            mRescindReason = "Same Citation"
            cancelledStatus = "Cancelled"
            mRescindNote = "Same Citation"
            m4ButtonType = "similar_citation_cancel"
            mTicketActionButtonEvent = "Cancelled"
             isSkipPrinter = false

             OfflineCancelCitationModel.uploadedCitationId = "none"
             OfflineCancelCitationModel.lprNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
             OfflineCancelCitationModel.note = mRescindNote
             OfflineCancelCitationModel.reason = mRescindReason
             OfflineCancelCitationModel.type = m4ButtonType
             OfflineCancelCitationModel.status = cancelledStatus
             OfflineCancelCitationModel.ticketNumber = mIssuranceModel!!.citationData!!.ticketNumber!!

             mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)

            showProgressLoader(getString(R.string.scr_message_please_wait))
//            createViewToPrint()
             generateFacsimileImage()
             mainScope.launch {
                 addSomeTweakBeforeCommandBasedFacsimile()
                 delay(SAVEPRINTBITMAPDELAYTIME)
                loadBitmapFromView(linearLayoutMainPrint,isOCR = false) //layOfficerDetails);
                val lockLprModel = LockLprModel()
                lockLprModel.mLprNumber = ""
                lockLprModel.mMake = ""
                lockLprModel.mModel = ""
                lockLprModel.mColor = ""
                lockLprModel.mAddress = ""
                lockLprModel.mViolationCode = ""
                 lockLprModel.ticketCategory = ""
                setLprLock(lockLprModel, this@LprPreviewActivity, sharedPreference)
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
            }
        }else if (lastSecondTag.equals("SIMILARCITATIONAPIERROR", ignoreCase = true)) {
//            IF similar citation return time out then call API again on popup OK button
             callSimilarAndLastSecondCheckAPI()
         }else if (lastSecondTag.equals(DynamicAPIPath.POST_IMAGE, ignoreCase = true)) {

         }else if (lastSecondTag.equals("ERROR", ignoreCase = true)||
             lastSecondTag.equals("lastsecondreturnfalse", ignoreCase = true)) {
             moveNextScreen()
         }

    }
    private fun setDuncanSiteId() {
//        First section
        appCompatTextView1 = findViewById(R.id.appcomptext_sideId_1_1)
        appCompatTextView12 = findViewById(R.id.appcomptext_sideId_1_2)
        appCompatTextView13 = findViewById(R.id.appcomptext_sideId_1_3)
        appCompatTextView14 = findViewById(R.id.appcomptext_sideId_1_4)
        appCompatTextView15 = findViewById(R.id.appcomptext_sideId_1_5)
        appCompatTextView16 = findViewById(R.id.appcomptext_sideId_1_6)
        appCompatTextView17 = findViewById(R.id.appcomptext_sideId_1_7)
        appCompatTextView18 = findViewById(R.id.appcomptext_sideId_1_8)
        appCompatTextView19 = findViewById(R.id.appcomptext_sideId_1_9)

        //        First sub section(state)
        appCompatTextView1_1 = findViewById(R.id.appcomptext_sideId_1_1_1)
        appCompatTextView12_2 = findViewById(R.id.appcomptext_sideId_1_2_2)
        appCompatTextView13_3 = findViewById(R.id.appcomptext_sideId_1_3_3)
        appCompatTextView14_4 = findViewById(R.id.appcomptext_sideId_1_4_4)
        appCompatTextView15_5 = findViewById(R.id.appcomptext_sideId_1_5_5)
        appCompatTextView16_6 = findViewById(R.id.appcomptext_sideId_1_6_6)
        appCompatTextView17_7 = findViewById(R.id.appcomptext_sideId_1_7_7)
        appCompatTextView18_8 = findViewById(R.id.appcomptext_sideId_1_8_8)
        appCompatTextView19_9 = findViewById(R.id.appcomptext_sideId_1_9_9)
//
//        Second section
        appCompatTextView21 = findViewById(R.id.appcomptext_sideId_2_1)
        appCompatTextView22 = findViewById(R.id.appcomptext_sideId_2_2)
        appCompatTextView23 = findViewById(R.id.appcomptext_sideId_2_3)
        appCompatTextView24 = findViewById(R.id.appcomptext_sideId_2_4)
        appCompatTextView25 = findViewById(R.id.appcomptext_sideId_2_5)
        appCompatTextView26 = findViewById(R.id.appcomptext_sideId_2_6)
        appCompatTextView27 = findViewById(R.id.appcomptext_sideId_2_7)
        appCompatTextView28 = findViewById(R.id.appcomptext_sideId_2_8)
        appCompatTextView29 = findViewById(R.id.appcomptext_sideId_2_9)

//        //Third section
        appCompatTextView31 = findViewById(R.id.appcomptext_sideId_3_1)
        appCompatTextView32 = findViewById(R.id.appcomptext_sideId_3_2)
        appCompatTextView33 = findViewById(R.id.appcomptext_sideId_3_3)
        appCompatTextView34 = findViewById(R.id.appcomptext_sideId_3_4)
        appCompatTextView35 = findViewById(R.id.appcomptext_sideId_3_5)
        appCompatTextView36 = findViewById(R.id.appcomptext_sideId_3_6)
        appCompatTextView37 = findViewById(R.id.appcomptext_sideId_3_7)
        appCompatTextView38 = findViewById(R.id.appcomptext_sideId_3_8)
        appCompatTextView39 = findViewById(R.id.appcomptext_sideId_3_9)

    }

    private fun setTextViewValueDuncan() {
        if (mIssuranceModel != null) {
            try {
                /**
                 * AMOUNT
                 */

                val amount =
                    mIssuranceModel?.citationData?.voilation?.amount.nullSafety().split(Regex("\\."))[0]
                val amountLenght = amount.length
                //                for(int i=1; i<=amountLenght;i++)
//                {
                if (amountLenght == 1) {
                    appCompatTextView14?.text = amount + ""
                } else if (amountLenght == 2) {
                    appCompatTextView13?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 2].toString() + ""
                } else if (amountLenght == 3) {
                    appCompatTextView13?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 2].toString() + ""
                    appCompatTextView15?.text = amount[amountLenght - 3].toString() + ""
                } else if (amountLenght == 4) {
                    appCompatTextView12?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView13?.text = amount[amountLenght - 2].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 3].toString() + ""
                    appCompatTextView15?.text = amount[amountLenght - 4].toString() + ""
                } else if (amountLenght == 5) {
                    appCompatTextView12?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView13?.text = amount[amountLenght - 2].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 3].toString() + ""
                    appCompatTextView15?.text = amount[amountLenght - 4].toString() + ""
                    appCompatTextView16?.text = amount[amountLenght - 5].toString() + ""
                } else if (amountLenght == 6) {
                    appCompatTextView1?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView12?.text = amount[amountLenght - 2].toString() + ""
                    appCompatTextView13?.text = amount[amountLenght - 3].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 4].toString() + ""
                    appCompatTextView15?.text = amount[amountLenght - 5].toString() + ""
                    appCompatTextView16?.text = amount[amountLenght - 6].toString() + ""
                }
                //                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                mFinalAmount = appCompatTextView16!!.text.toString()+appCompatTextView15!!.text.toString()+
                        appCompatTextView14!!.text.toString()+appCompatTextView13!!.text.toString()+
                        appCompatTextView12!!.text.toString()+appCompatTextView1!!.text.toString();
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /***
             * TIcket Number
             */
            try {
                val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                val ticketLenght = ticektNumber!!.length
                //                for(int i=1; i<=ticketLenght;i++)
//                {
                appCompatTextView21?.text = ticektNumber[8].toString() + ""
                appCompatTextView22?.text = ticektNumber[7].toString() + ""
                appCompatTextView23?.text = ticektNumber[6].toString() + ""
                appCompatTextView24?.text = ticektNumber[5].toString() + ""
                appCompatTextView25?.text = ticektNumber[4].toString() + ""
                appCompatTextView26?.text = ticektNumber[3].toString() + ""
                appCompatTextView27?.text = ticektNumber[2].toString() + ""
                appCompatTextView28?.text = ticektNumber[1].toString() + ""
                appCompatTextView29?.text = ticektNumber[0].toString() + ""
                //                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setMarginLeft(v: View, left: Int) {
        val params = v.layoutParams as MarginLayoutParams
        params.setMargins(
            left, params.topMargin,
            params.rightMargin, params.bottomMargin
        )
    }
    private fun setOCRTextValueOnView() {
        if (mIssuranceModel != null) {
            val OCRTextValue = (mOCRFormatValue!!.split("]"))

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)) {
                relativeLayoutPrintStateSection.visibility = View.VISIBLE
                /***
                 * Lpr plate Number
                 */
                try {

                    val plateNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                    val ticketLenght = plateNumber!!.length - 1

                    appCompatTextView1?.visibility = View.GONE
                    appCompatTextView12?.visibility = View.GONE
                    appCompatTextView13?.visibility = View.GONE
                    appCompatTextView14?.visibility = View.GONE
                    appCompatTextView15?.visibility = View.GONE
                    appCompatTextView16?.visibility = View.GONE
                    appCompatTextView17?.visibility = View.GONE
                    appCompatTextView18?.visibility = View.GONE
                    appCompatTextView19?.visibility = View.GONE

                    if(plateNumber.length>0) {
                        appCompatTextView1?.text = plateNumber[ticketLenght].toString() + ""
                        appCompatTextView1?.visibility = View.VISIBLE
                        appCompatTextView12?.text = plateNumber[ticketLenght - 1].toString() + ""
                        appCompatTextView12?.visibility = View.VISIBLE
                        appCompatTextView13?.text = plateNumber[ticketLenght - 2].toString() + ""
                        appCompatTextView13?.visibility = View.VISIBLE
                        appCompatTextView14?.text = plateNumber[ticketLenght - 3].toString() + ""
                        appCompatTextView14?.visibility = View.VISIBLE
                        appCompatTextView15?.text = plateNumber[ticketLenght - 4].toString() + ""
                        appCompatTextView15?.visibility = View.VISIBLE
                        appCompatTextView16?.text = plateNumber[ticketLenght - 5].toString() + ""
                        appCompatTextView16?.visibility = View.VISIBLE
                        appCompatTextView17?.text = plateNumber[ticketLenght - 6].toString() + ""
                        appCompatTextView17?.visibility = View.VISIBLE
                        appCompatTextView18?.text = plateNumber[ticketLenght - 7].toString() + ""
                        appCompatTextView18?.visibility = View.VISIBLE
                        appCompatTextView19?.text = plateNumber[ticketLenght - 8].toString() + ""
                        appCompatTextView19?.visibility = View.VISIBLE
                        }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                /**
                 * State section
                 */
                try {

                    val state = mIssuranceModel!!.citationData!!.vehicle!!.state
                    if(state!!.length>0) {
                        appCompatTextView13_3?.text = (state?.get(1)?.toString() ?: "") + ""
                        appCompatTextView14_4?.text = (state?.get(0)?.toString() ?: "") + ""
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                try {
                    /**
                     * AMOUNT First position from top
                     */

                    val amount =
                        mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                            .split(Regex("\\."))[0]
                    val amountLenght = amount.length
                    if (amountLenght == 1) {
                        appCompatTextView24?.text = amount + ""
                    } else if (amountLenght == 2) {
                        appCompatTextView23?.text = amount[amountLenght - 1].toString() + ""
                        appCompatTextView24?.text = amount[amountLenght - 2].toString() + ""
                    } else if (amountLenght == 3) {
                        appCompatTextView23?.text = amount[amountLenght - 1].toString() + ""
                        appCompatTextView24?.text = amount[amountLenght - 2].toString() + ""
                        appCompatTextView25?.text = amount[amountLenght - 3].toString() + ""
                    } else if (amountLenght == 4) {
                        appCompatTextView22?.text = amount[amountLenght - 1].toString() + ""
                        appCompatTextView23?.text = amount[amountLenght - 2].toString() + ""
                        appCompatTextView24?.text = amount[amountLenght - 3].toString() + ""
                        appCompatTextView25?.text = amount[amountLenght - 4].toString() + ""
                    } else if (amountLenght == 5) {
                        appCompatTextView22?.text = amount[amountLenght - 1].toString() + ""
                        appCompatTextView23?.text = amount[amountLenght - 2].toString() + ""
                        appCompatTextView24?.text = amount[amountLenght - 3].toString() + ""
                        appCompatTextView25?.text = amount[amountLenght - 4].toString() + ""
                        appCompatTextView26?.text = amount[amountLenght - 5].toString() + ""
                    } else if (amountLenght == 6) {
                        appCompatTextView21?.text = amount[amountLenght - 1].toString() + ""
                        appCompatTextView22?.text = amount[amountLenght - 2].toString() + ""
                        appCompatTextView23?.text = amount[amountLenght - 3].toString() + ""
                        appCompatTextView24?.text = amount[amountLenght - 4].toString() + ""
                        appCompatTextView25?.text = amount[amountLenght - 5].toString() + ""
                        appCompatTextView26?.text = amount[amountLenght - 6].toString() + ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                /**
                 * Third row TIcket Number
                 */
                try {
                    val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                    val ticketLenght = ticektNumber!!.length

                    appCompatTextView31?.text = ticektNumber[ticketLenght - 1].toString() + ""
                    appCompatTextView32?.text = ticektNumber[ticketLenght - 2].toString() + ""
                    appCompatTextView33?.text = ticektNumber[ticketLenght - 3].toString() + ""
                    appCompatTextView34?.text = ticektNumber[ticketLenght - 4].toString() + ""
                    appCompatTextView35?.text = ticektNumber[ticketLenght - 5].toString() + ""
                    appCompatTextView36?.text = ticektNumber[ticketLenght - 6].toString() + ""
                    appCompatTextView37?.text = ticektNumber[ticketLenght - 7].toString() + ""

                    appCompatTextView38?.visibility = View.VISIBLE
                    appCompatTextView39?.visibility = View.VISIBLE
                    if (ticketLenght > 7) {
                        appCompatTextView38?.visibility = View.VISIBLE
                        appCompatTextView39?.visibility = View.VISIBLE
                        appCompatTextView38?.text =
                            ticektNumber[ticketLenght - 8].toString() + ""
                        appCompatTextView39?.text =
                            ticektNumber[ticketLenght - 9].toString() + ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            /**
             * Below code is working for rest of site
             *"[CITATION_NO][AMOUNT][STATE][LPR PLATE]"
             */
            else if (OCRTextValue.size > 4) {

                try {
                    relativeLayoutPrintStateSection.visibility = View.VISIBLE
                    if (OCRTextValue.size > 3 && OCRTextValue[3].equals("[AMOUNT")) {

                        val amount =
                            mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                                .split(Regex("\\."))[0]
                        val amountLenght = amount.length

                        appCompatTextView1?.visibility = View.GONE
                        appCompatTextView12?.visibility = View.GONE
                        appCompatTextView13?.visibility = View.GONE
                        appCompatTextView14?.visibility = View.GONE
                        appCompatTextView15?.visibility = View.GONE
                        appCompatTextView16?.visibility = View.GONE
                        appCompatTextView17?.visibility = View.GONE
                        appCompatTextView18?.visibility = View.GONE
                        appCompatTextView19?.visibility = View.GONE

                        appCompatTextView1?.setText("0")
                        appCompatTextView12?.setText("0")
                        appCompatTextView13?.setText("0")
                        appCompatTextView14?.setText("0")
                        appCompatTextView15?.setText("0")
                        appCompatTextView16?.setText("0")
                        appCompatTextView17?.setText("0")
                        appCompatTextView18?.setText("0")
                        appCompatTextView19?.setText("0")

                        if (amountLenght == 1) {
                            appCompatTextView14?.text = amount + ""
                            appCompatTextView14?.visibility = View.VISIBLE
                        } else if (amountLenght == 2) {
                            appCompatTextView13?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView13?.visibility = View.VISIBLE
                            appCompatTextView14?.visibility = View.VISIBLE
                        } else if (amountLenght == 3) {
                            appCompatTextView13?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView15?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView13?.visibility = View.VISIBLE
                            appCompatTextView14?.visibility = View.VISIBLE
                            appCompatTextView15?.visibility = View.VISIBLE
                        } else if (amountLenght == 4) {
                            appCompatTextView12?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView13?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView15?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView12?.visibility = View.VISIBLE
                            appCompatTextView13?.visibility = View.VISIBLE
                            appCompatTextView14?.visibility = View.VISIBLE
                            appCompatTextView15?.visibility = View.VISIBLE
                        } else if (amountLenght == 5) {
                            appCompatTextView12?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView13?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView15?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView16?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView12?.visibility = View.VISIBLE
                            appCompatTextView13?.visibility = View.VISIBLE
                            appCompatTextView14?.visibility = View.VISIBLE
                            appCompatTextView15?.visibility = View.VISIBLE
                            appCompatTextView16?.visibility = View.VISIBLE
                        } else if (amountLenght == 6) {
                            appCompatTextView1?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView12?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView13?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView15?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView16?.text = amount[amountLenght - 6].toString() + ""
                            appCompatTextView1?.visibility = View.VISIBLE
                            appCompatTextView12?.visibility = View.VISIBLE
                            appCompatTextView13?.visibility = View.VISIBLE
                            appCompatTextView14?.visibility = View.VISIBLE
                            appCompatTextView15?.visibility = View.VISIBLE
                            appCompatTextView16?.visibility = View.VISIBLE
                        }
                        try {
                            mFinalAmount =
                                appCompatTextView16!!.text.toString() + appCompatTextView15!!.text.toString() +
                                        appCompatTextView14!!.text.toString() + appCompatTextView13!!.text.toString() +
                                        appCompatTextView12!!.text.toString() + appCompatTextView1!!.text.toString();


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }else if (OCRTextValue.size > 2 && OCRTextValue[2].equals("[AMOUNT")) {

                        val amount =
                            mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                                .split(Regex("\\."))[0]
                        val amountLenght = amount.length

                        appCompatTextView1_1?.visibility = View.GONE
                        appCompatTextView12_2?.visibility = View.GONE
                        appCompatTextView13_3?.visibility = View.GONE
                        appCompatTextView14_4?.visibility = View.GONE
                        appCompatTextView15_5?.visibility = View.GONE
                        appCompatTextView16_6?.visibility = View.GONE
                        appCompatTextView17_7?.visibility = View.GONE
                        appCompatTextView18_8?.visibility = View.GONE
                        appCompatTextView19_9?.visibility = View.GONE

                        appCompatTextView1_1?.setText("0")
                        appCompatTextView12_2?.setText("0")
                        appCompatTextView13_3?.setText("0")
                        appCompatTextView14_4?.setText("0")
                        appCompatTextView15_5?.setText("0")
                        appCompatTextView16_6?.setText("0")
                        appCompatTextView17_7?.setText("0")
                        appCompatTextView18_8?.setText("0")
                        appCompatTextView19_9?.setText("0")

                        if (amountLenght == 1) {
                            appCompatTextView14_4?.text = amount + ""
                            appCompatTextView14_4?.visibility = View.VISIBLE
                        } else if (amountLenght == 2) {
                            appCompatTextView13_3?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView14_4?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView13_3?.visibility = View.VISIBLE
                            appCompatTextView14_4?.visibility = View.VISIBLE
                        } else if (amountLenght == 3) {
                            appCompatTextView13_3?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView14_4?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView15_5?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView13_3?.visibility = View.VISIBLE
                            appCompatTextView14_4?.visibility = View.VISIBLE
                            appCompatTextView15_5?.visibility = View.VISIBLE
                        } else if (amountLenght == 4) {
                            appCompatTextView12_2?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView13_3?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView14_4?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView15_5?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView12_2?.visibility = View.VISIBLE
                            appCompatTextView13_3?.visibility = View.VISIBLE
                            appCompatTextView14_4?.visibility = View.VISIBLE
                            appCompatTextView15_5?.visibility = View.VISIBLE
                        } else if (amountLenght == 5) {
                            appCompatTextView12_2?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView13_3?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView14_4?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView15_5?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView16_6?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView12_2?.visibility = View.VISIBLE
                            appCompatTextView13_3?.visibility = View.VISIBLE
                            appCompatTextView14_4?.visibility = View.VISIBLE
                            appCompatTextView15_5?.visibility = View.VISIBLE
                            appCompatTextView16_6?.visibility = View.VISIBLE
                        } else if (amountLenght == 6) {
                            appCompatTextView1_1?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView12_2?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView13_3?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView14_4?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView15_5?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView16_6?.text = amount[amountLenght - 6].toString() + ""
                            appCompatTextView1_1?.visibility = View.VISIBLE
                            appCompatTextView12_2?.visibility = View.VISIBLE
                            appCompatTextView13_3?.visibility = View.VISIBLE
                            appCompatTextView14_4?.visibility = View.VISIBLE
                            appCompatTextView15_5?.visibility = View.VISIBLE
                            appCompatTextView16_6?.visibility = View.VISIBLE
                        }
                        try {
                            mFinalAmount =
                                appCompatTextView16_6!!.text.toString() + appCompatTextView15_5!!.text.toString() +
                                        appCompatTextView14_4!!.text.toString() + appCompatTextView13_3!!.text.toString() +
                                        appCompatTextView12_2!!.text.toString() + appCompatTextView1_1!!.text.toString();


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 1 && OCRTextValue[1].equals("[AMOUNT")) {
                        /**
                         * Middle View section for amount
                         */

                        val amount =
                            mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                                .split(Regex("\\."))[0]
                        val amountLenght = amount.length

                        appCompatTextView21?.visibility = View.GONE
                        appCompatTextView22?.visibility = View.GONE
                        appCompatTextView23?.visibility = View.GONE
                        appCompatTextView24?.visibility = View.GONE
                        appCompatTextView25?.visibility = View.GONE
                        appCompatTextView26?.visibility = View.GONE
                        appCompatTextView27?.visibility = View.GONE
                        appCompatTextView28?.visibility = View.GONE
                        appCompatTextView29?.visibility = View.GONE

                        appCompatTextView21?.setText("0")
                        appCompatTextView22?.setText("0")
                        appCompatTextView23?.setText("0")
                        appCompatTextView24?.setText("0")
                        appCompatTextView25?.setText("0")
                        appCompatTextView26?.setText("0")
                        appCompatTextView27?.setText("0")
                        appCompatTextView28?.setText("0")
                        appCompatTextView29?.setText("0")

                        if (amountLenght == 1) {
                            appCompatTextView24?.text = amount + ""
                            appCompatTextView24?.visibility = View.VISIBLE
                        } else if (amountLenght == 2) {
                            appCompatTextView23?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                        } else if (amountLenght == 3) {
                            appCompatTextView23?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView25?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.visibility = View.VISIBLE
                        } else if (amountLenght == 4) {
                            appCompatTextView22?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView23?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView25?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView22?.visibility = View.VISIBLE
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.visibility = View.VISIBLE
                        } else if (amountLenght == 5) {
                            appCompatTextView22?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView23?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView25?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView26?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView22?.visibility = View.VISIBLE
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.visibility = View.VISIBLE
                            appCompatTextView26?.visibility = View.VISIBLE
                        } else if (amountLenght == 6) {
                            appCompatTextView21?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView22?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView23?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView25?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView26?.text = amount[amountLenght - 6].toString() + ""

                            appCompatTextView21?.visibility = View.VISIBLE
                            appCompatTextView22?.visibility = View.VISIBLE
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.visibility = View.VISIBLE
                            appCompatTextView26?.visibility = View.VISIBLE
                        }
                        appCompatTextView21?.visibility = View.VISIBLE
                        appCompatTextView22?.visibility = View.VISIBLE
                        appCompatTextView23?.visibility = View.VISIBLE
                        appCompatTextView24?.visibility = View.VISIBLE
                        appCompatTextView25?.visibility = View.VISIBLE
                        appCompatTextView26?.visibility = View.VISIBLE
                        try {
                            mFinalAmount =
                                appCompatTextView26!!.text.toString() + appCompatTextView25!!.text.toString() +
                                        appCompatTextView24!!.text.toString() + appCompatTextView23!!.text.toString() +
                                        appCompatTextView22!!.text.toString() + appCompatTextView21!!.text.toString();


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 0 && OCRTextValue[0].equals("[AMOUNT")) {
                        /**
                         * Middle View section for amount
                         */
                        val amount =
                            mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                                .split(Regex("\\."))[0]
                        val amountLenght = amount.length

                        appCompatTextView31?.visibility = View.GONE
                        appCompatTextView32?.visibility = View.GONE
                        appCompatTextView33?.visibility = View.GONE
                        appCompatTextView34?.visibility = View.GONE
                        appCompatTextView35?.visibility = View.GONE
                        appCompatTextView36?.visibility = View.GONE
                        appCompatTextView37?.visibility = View.GONE
                        appCompatTextView38?.visibility = View.GONE
                        appCompatTextView39?.visibility = View.GONE

                        if (amountLenght == 1) {
                            appCompatTextView34?.text = amount + ""
                            appCompatTextView34?.visibility = View.VISIBLE
                        } else if (amountLenght == 2) {
                            appCompatTextView33?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                        } else if (amountLenght == 3) {
                            appCompatTextView33?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView35?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.visibility = View.VISIBLE
                        } else if (amountLenght == 4) {
                            appCompatTextView32?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView33?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView35?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView32?.visibility = View.VISIBLE
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.visibility = View.VISIBLE
                        } else if (amountLenght == 5) {
                            appCompatTextView32?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView33?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView35?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView36?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView32?.visibility = View.VISIBLE
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.visibility = View.VISIBLE
                            appCompatTextView36?.visibility = View.VISIBLE
                        } else if (amountLenght == 6) {
                            appCompatTextView31?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView32?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView33?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView35?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView36?.text = amount[amountLenght - 6].toString() + ""

                            appCompatTextView31?.visibility = View.VISIBLE
                            appCompatTextView32?.visibility = View.VISIBLE
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.visibility = View.VISIBLE
                            appCompatTextView36?.visibility = View.VISIBLE
                        }
                        appCompatTextView31?.visibility = View.VISIBLE
                        appCompatTextView32?.visibility = View.VISIBLE
                        appCompatTextView33?.visibility = View.VISIBLE
                        appCompatTextView34?.visibility = View.VISIBLE
                        appCompatTextView35?.visibility = View.VISIBLE
                        appCompatTextView36?.visibility = View.VISIBLE
                        try {
                            mFinalAmount =
                                appCompatTextView36!!.text.toString() + appCompatTextView35!!.text.toString() +
                                        appCompatTextView34!!.text.toString() + appCompatTextView33!!.text.toString() +
                                        appCompatTextView32!!.text.toString() + appCompatTextView31!!.text.toString();


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                /**
                 * State section sub first section
                 */
                try {

                    if (OCRTextValue.size > 3 && OCRTextValue[3].equals("[STATE")) {
                        appCompatTextView1!!.visibility = View.GONE
                        appCompatTextView12!!.visibility = View.GONE
                        appCompatTextView13!!.visibility = View.GONE
                        appCompatTextView16!!.visibility = View.GONE
                        appCompatTextView17!!.visibility = View.GONE
                        appCompatTextView18!!.visibility = View.GONE
                        appCompatTextView19!!.visibility = View.GONE

                        appCompatTextView14!!.visibility = View.VISIBLE
                        appCompatTextView15!!.visibility = View.VISIBLE

                        val state = mIssuranceModel!!.citationData!!.vehicle!!.state
                        appCompatTextView14?.text = (state?.get(1)?.toString() ?: "") + ""
                        appCompatTextView15?.text = (state?.get(0)?.toString() ?: "") + ""
                    }else if (OCRTextValue.size > 2 && OCRTextValue[2].equals("[STATE")) {
                        appCompatTextView1_1!!.visibility = View.GONE
                        appCompatTextView12_2!!.visibility = View.GONE
                        appCompatTextView13_3!!.visibility = View.GONE
                        appCompatTextView16_6!!.visibility = View.GONE
                        appCompatTextView17_7!!.visibility = View.GONE
                        appCompatTextView18_8!!.visibility = View.GONE
                        appCompatTextView19_9!!.visibility = View.GONE

                        appCompatTextView13_3!!.visibility = View.VISIBLE
                        appCompatTextView14_4!!.visibility = View.VISIBLE
                        appCompatTextView15_5!!.visibility = View.VISIBLE

                        val state = mIssuranceModel!!.citationData!!.vehicle!!.state
                        appCompatTextView14_4?.text = (state?.get(1)?.toString() ?: "") + ""
                        appCompatTextView15_5?.text = (state?.get(0)?.toString() ?: "") + ""
                    }else if (OCRTextValue.size > 1 && OCRTextValue[1].equals("[STATE")) {
                        appCompatTextView21!!.visibility = View.GONE
                        appCompatTextView22!!.visibility = View.GONE
                        appCompatTextView23!!.visibility = View.GONE
                        appCompatTextView26!!.visibility = View.GONE
                        appCompatTextView27!!.visibility = View.GONE
                        appCompatTextView28!!.visibility = View.GONE
                        appCompatTextView29!!.visibility = View.GONE

                        appCompatTextView24!!.visibility = View.VISIBLE
                        appCompatTextView25!!.visibility = View.VISIBLE

                        val state = mIssuranceModel!!.citationData!!.vehicle!!.state
                        appCompatTextView24?.text = (state?.get(1)?.toString() ?: "") + ""
                        appCompatTextView25?.text = (state?.get(0)?.toString() ?: "") + ""
                    }else if (OCRTextValue.size > 0 && OCRTextValue[0].equals("[STATE")) {
                        appCompatTextView31!!.visibility = View.GONE
                        appCompatTextView32!!.visibility = View.GONE
                        appCompatTextView33!!.visibility = View.GONE
                        appCompatTextView36!!.visibility = View.GONE
                        appCompatTextView37!!.visibility = View.GONE
                        appCompatTextView38!!.visibility = View.GONE
                        appCompatTextView39!!.visibility = View.GONE

                        appCompatTextView34!!.visibility = View.VISIBLE
                        appCompatTextView35!!.visibility = View.VISIBLE

                        val state = mIssuranceModel!!.citationData!!.vehicle!!.state
                        appCompatTextView34?.text = (state?.get(1)?.toString() ?: "") + ""
                        appCompatTextView35?.text = (state?.get(0)?.toString() ?: "") + ""
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                /**
                 * CITATION_NO for all three position
                 */
                try {
                    if (OCRTextValue.size > 0 && OCRTextValue[0].equals("[CITATION_NO")) {

                        val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                        appCompatTextView31?.visibility = View.GONE
                        appCompatTextView32?.visibility = View.GONE
                        appCompatTextView33?.visibility = View.GONE
                        appCompatTextView34?.visibility = View.GONE
                        appCompatTextView35?.visibility = View.GONE
                        appCompatTextView36?.visibility = View.GONE
                        appCompatTextView37?.visibility = View.GONE
                        appCompatTextView38?.visibility = View.GONE
                        appCompatTextView39?.visibility = View.GONE


                        if (ticektNumber!!.length > 8) {
                            appCompatTextView31?.text = ticektNumber[8].toString() + ""
                            appCompatTextView31?.visibility = View.VISIBLE
                        }
                        if (ticektNumber!!.length > 7) {
                            appCompatTextView32?.text = ticektNumber[7].toString() + ""
                            appCompatTextView32?.visibility = View.VISIBLE
                        }
                        appCompatTextView33?.text = ticektNumber[6].toString() + ""
                        appCompatTextView34?.text = ticektNumber[5].toString() + ""
                        appCompatTextView35?.text = ticektNumber[4].toString() + ""
                        appCompatTextView36?.text = ticektNumber[3].toString() + ""
                        appCompatTextView37?.text = ticektNumber[2].toString() + ""
                        appCompatTextView38?.text = ticektNumber[1].toString() + ""
                        appCompatTextView39?.text = ticektNumber[0].toString() + ""

                        appCompatTextView32?.visibility = View.VISIBLE
                        appCompatTextView33?.visibility = View.VISIBLE
                        appCompatTextView34?.visibility = View.VISIBLE
                        appCompatTextView35?.visibility = View.VISIBLE
                        appCompatTextView36?.visibility = View.VISIBLE
                        appCompatTextView37?.visibility = View.VISIBLE
                        appCompatTextView38?.visibility = View.VISIBLE
                        appCompatTextView39?.visibility = View.VISIBLE
                    } else if (OCRTextValue.size > 1 && OCRTextValue[1].equals("[CITATION_NO")) {

                        val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                        if (ticektNumber!!.length > 8) {
                            appCompatTextView21?.text = ticektNumber[8].toString() + ""
                        }
                        if (ticektNumber!!.length > 7) {
                            appCompatTextView22?.text = ticektNumber[7].toString() + ""
                        }
                        appCompatTextView23?.text = ticektNumber[6].toString() + ""
                        appCompatTextView24?.text = ticektNumber[5].toString() + ""
                        appCompatTextView25?.text = ticektNumber[4].toString() + ""
                        appCompatTextView26?.text = ticektNumber[3].toString() + ""
                        appCompatTextView27?.text = ticektNumber[2].toString() + ""
                        appCompatTextView28?.text = ticektNumber[1].toString() + ""
                        appCompatTextView29?.text = ticektNumber[0].toString() + ""
                    } else if (OCRTextValue.size > 2 && OCRTextValue[2].equals("[CITATION_NO")) {
                        val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                        appCompatTextView1_1?.visibility = View.GONE
                        appCompatTextView12_2?.visibility = View.GONE
                        appCompatTextView13_3?.visibility = View.GONE
                        appCompatTextView14_4?.visibility = View.GONE
                        appCompatTextView15_5?.visibility = View.GONE
                        appCompatTextView16_6?.visibility = View.GONE
                        appCompatTextView17_7?.visibility = View.GONE
                        appCompatTextView18_8?.visibility = View.GONE
                        appCompatTextView19_9?.visibility = View.GONE


                        if (ticektNumber!!.length > 8) {
                            appCompatTextView1_1?.text = ticektNumber[8].toString() + ""
                            appCompatTextView1_1?.visibility = View.VISIBLE
                        }
                        if (ticektNumber!!.length > 7) {
                            appCompatTextView12_2?.text = ticektNumber[7].toString() + ""
                            appCompatTextView12_2?.visibility = View.VISIBLE
                        }
                        appCompatTextView13_3?.text = ticektNumber[6].toString() + ""
                        appCompatTextView14_4?.text = ticektNumber[5].toString() + ""
                        appCompatTextView15_5?.text = ticektNumber[4].toString() + ""
                        appCompatTextView16_6?.text = ticektNumber[3].toString() + ""
                        appCompatTextView17_7?.text = ticektNumber[2].toString() + ""
                        appCompatTextView18_8?.text = ticektNumber[1].toString() + ""
                        appCompatTextView19_9?.text = ticektNumber[0].toString() + ""

                        appCompatTextView13_3?.visibility = View.VISIBLE
                        appCompatTextView14_4?.visibility = View.VISIBLE
                        appCompatTextView15_5?.visibility = View.VISIBLE
                        appCompatTextView16_6?.visibility = View.VISIBLE
                        appCompatTextView17_7?.visibility = View.VISIBLE
                        appCompatTextView18_8?.visibility = View.VISIBLE
                        appCompatTextView19_9?.visibility = View.VISIBLE
                    } else if (OCRTextValue.size > 3 && OCRTextValue[3].equals("[CITATION_NO")) {
                        val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                        appCompatTextView1?.visibility = View.GONE
                        appCompatTextView12?.visibility = View.GONE
                        appCompatTextView13?.visibility = View.GONE
                        appCompatTextView14?.visibility = View.GONE
                        appCompatTextView15?.visibility = View.GONE
                        appCompatTextView16?.visibility = View.GONE
                        appCompatTextView17?.visibility = View.GONE
                        appCompatTextView18?.visibility = View.GONE
                        appCompatTextView19?.visibility = View.GONE


                        if (ticektNumber!!.length > 8) {
                            appCompatTextView1?.text = ticektNumber[8].toString() + ""
                            appCompatTextView1?.visibility = View.VISIBLE
                        }
                        if (ticektNumber!!.length > 7) {
                            appCompatTextView12?.text = ticektNumber[7].toString() + ""
                            appCompatTextView12?.visibility = View.VISIBLE
                        }
                        appCompatTextView13?.text = ticektNumber[6].toString() + ""
                        appCompatTextView14?.text = ticektNumber[5].toString() + ""
                        appCompatTextView15?.text = ticektNumber[4].toString() + ""
                        appCompatTextView16?.text = ticektNumber[3].toString() + ""
                        appCompatTextView17?.text = ticektNumber[2].toString() + ""
                        appCompatTextView18?.text = ticektNumber[1].toString() + ""
                        appCompatTextView19?.text = ticektNumber[0].toString() + ""

                        appCompatTextView13?.visibility = View.VISIBLE
                        appCompatTextView14?.visibility = View.VISIBLE
                        appCompatTextView15?.visibility = View.VISIBLE
                        appCompatTextView16?.visibility = View.VISIBLE
                        appCompatTextView17?.visibility = View.VISIBLE
                        appCompatTextView18?.visibility = View.VISIBLE
                        appCompatTextView19?.visibility = View.VISIBLE
                    }

                    /***
                     * Lpr plate Number
                     * */
                    if (OCRTextValue.size > 1 && OCRTextValue[0].equals("[LPR PLATE")) {
                        try {
                            val plateNumber =
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                            val ticketLenght = plateNumber!!.length - 1
                            appCompatTextView31?.visibility = View.GONE
                            appCompatTextView32?.visibility = View.GONE
                            appCompatTextView33?.visibility = View.GONE
                            appCompatTextView34?.visibility = View.GONE
                            appCompatTextView35?.visibility = View.GONE
                            appCompatTextView36?.visibility = View.GONE
                            appCompatTextView37?.visibility = View.GONE
                            appCompatTextView38?.visibility = View.GONE
                            appCompatTextView39?.visibility = View.GONE

                            appCompatTextView31?.text = plateNumber[ticketLenght].toString() + ""
                            appCompatTextView31?.visibility = View.VISIBLE
                            appCompatTextView32?.text =
                                plateNumber[ticketLenght - 1].toString() + ""
                            appCompatTextView32?.visibility = View.VISIBLE
                            appCompatTextView33?.text =
                                plateNumber[ticketLenght - 2].toString() + ""
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.text =
                                plateNumber[ticketLenght - 3].toString() + ""
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.text =
                                plateNumber[ticketLenght - 4].toString() + ""
                            appCompatTextView35?.visibility = View.VISIBLE
                            appCompatTextView36?.text =
                                plateNumber[ticketLenght - 5].toString() + ""
                            appCompatTextView36?.visibility = View.VISIBLE
                            appCompatTextView37?.text =
                                plateNumber[ticketLenght - 6].toString() + ""
                            appCompatTextView37?.visibility = View.VISIBLE
                            if (ticketLenght > 8) {
                                appCompatTextView38?.text =
                                    plateNumber[ticketLenght - 7].toString() + ""
                                appCompatTextView38?.visibility = View.VISIBLE
                            }
                            if (ticketLenght > 9) {
                                appCompatTextView39?.text =
                                    plateNumber[ticketLenght - 8].toString() + ""
                                appCompatTextView39?.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 1 && OCRTextValue[1].equals("[LPR PLATE")) {
                        try {
                            val plateNumber =
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                            val ticketLenght = plateNumber!!.length - 1
                            appCompatTextView21?.visibility = View.GONE
                            appCompatTextView22?.visibility = View.GONE
                            appCompatTextView23?.visibility = View.GONE
                            appCompatTextView24?.visibility = View.GONE
                            appCompatTextView25?.visibility = View.GONE
                            appCompatTextView26?.visibility = View.GONE
                            appCompatTextView27?.visibility = View.GONE
                            appCompatTextView28?.visibility = View.GONE
                            appCompatTextView29?.visibility = View.GONE

                            appCompatTextView21?.text = plateNumber[ticketLenght].toString() + ""
                            appCompatTextView21?.visibility = View.VISIBLE
                            appCompatTextView22?.text =
                                plateNumber[ticketLenght - 1].toString() + ""
                            appCompatTextView22?.visibility = View.VISIBLE
                            appCompatTextView23?.text =
                                plateNumber[ticketLenght - 2].toString() + ""
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.text =
                                plateNumber[ticketLenght - 3].toString() + ""
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.text =
                                plateNumber[ticketLenght - 4].toString() + ""
                            appCompatTextView25?.visibility = View.VISIBLE
                            appCompatTextView26?.text =
                                plateNumber[ticketLenght - 5].toString() + ""
                            appCompatTextView26?.visibility = View.VISIBLE
                            appCompatTextView27?.text =
                                plateNumber[ticketLenght - 6].toString() + ""
                            appCompatTextView27?.visibility = View.VISIBLE
                            if (ticketLenght > 8) {
                                appCompatTextView28?.text =
                                    plateNumber[ticketLenght - 7].toString() + ""
                                appCompatTextView28?.visibility = View.VISIBLE
                            }
                            if (ticketLenght > 9) {
                                appCompatTextView29?.text =
                                    plateNumber[ticketLenght - 8].toString() + ""
                                appCompatTextView29?.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 2 && OCRTextValue[2].equals("[LPR PLATE")) {
                        try {
                            val plateNumber =
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                            val ticketLenght = plateNumber!!.length - 1
                            appCompatTextView1?.visibility = View.GONE
                            appCompatTextView12?.visibility = View.GONE
                            appCompatTextView13?.visibility = View.GONE
                            appCompatTextView14?.visibility = View.GONE
                            appCompatTextView15?.visibility = View.GONE
                            appCompatTextView16?.visibility = View.GONE
                            appCompatTextView17?.visibility = View.GONE
                            appCompatTextView18?.visibility = View.GONE
                            appCompatTextView19?.visibility = View.GONE

                            appCompatTextView1?.text = plateNumber[ticketLenght].toString() + ""
                            appCompatTextView1?.visibility = View.VISIBLE
                            appCompatTextView12?.text =
                                plateNumber[ticketLenght - 1].toString() + ""
                            appCompatTextView12?.visibility = View.VISIBLE
                            appCompatTextView13?.text =
                                plateNumber[ticketLenght - 2].toString() + ""
                            appCompatTextView13?.visibility = View.VISIBLE
                            appCompatTextView14?.text =
                                plateNumber[ticketLenght - 3].toString() + ""
                            appCompatTextView14?.visibility = View.VISIBLE
                            appCompatTextView15?.text =
                                plateNumber[ticketLenght - 4].toString() + ""
                            appCompatTextView15?.visibility = View.VISIBLE
                            appCompatTextView16?.text =
                                plateNumber[ticketLenght - 5].toString() + ""
                            appCompatTextView16?.visibility = View.VISIBLE
                            appCompatTextView17?.text =
                                plateNumber[ticketLenght - 6].toString() + ""
                            appCompatTextView17?.visibility = View.VISIBLE
                            if (ticketLenght > 8) {
                                appCompatTextView18?.text =
                                    plateNumber[ticketLenght - 7].toString() + ""
                                appCompatTextView18?.visibility = View.VISIBLE
                            }
                            if (ticketLenght > 9) {
                                appCompatTextView19?.text =
                                    plateNumber[ticketLenght - 8].toString() + ""
                                appCompatTextView19?.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 3 && OCRTextValue[3].equals("[LPR PLATE")) {
                        try {
                            val plateNumber =
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                            val ticketLenght = plateNumber!!.length - 1
                            appCompatTextView1?.visibility = View.GONE
                            appCompatTextView12?.visibility = View.GONE
                            appCompatTextView13?.visibility = View.GONE
                            appCompatTextView14?.visibility = View.GONE
                            appCompatTextView15?.visibility = View.GONE
                            appCompatTextView16?.visibility = View.GONE
                            appCompatTextView17?.visibility = View.GONE
                            appCompatTextView18?.visibility = View.GONE
                            appCompatTextView19?.visibility = View.GONE

                            appCompatTextView1?.text = plateNumber[ticketLenght].toString() + ""
                            appCompatTextView1?.visibility = View.VISIBLE
                            appCompatTextView12?.text =
                                plateNumber[ticketLenght - 1].toString() + ""
                            appCompatTextView12?.visibility = View.VISIBLE
                            appCompatTextView13?.text =
                                plateNumber[ticketLenght - 2].toString() + ""
                            appCompatTextView13?.visibility = View.VISIBLE
                            appCompatTextView14?.text =
                                plateNumber[ticketLenght - 3].toString() + ""
                            appCompatTextView14?.visibility = View.VISIBLE
                            appCompatTextView15?.text =
                                plateNumber[ticketLenght - 4].toString() + ""
                            appCompatTextView15?.visibility = View.VISIBLE
                            appCompatTextView16?.text =
                                plateNumber[ticketLenght - 5].toString() + ""
                            appCompatTextView16?.visibility = View.VISIBLE
                            appCompatTextView17?.text =
                                plateNumber[ticketLenght - 6].toString() + ""
                            appCompatTextView17?.visibility = View.VISIBLE
                            if (ticketLenght > 8) {
                                appCompatTextView18?.text =
                                    plateNumber[ticketLenght - 7].toString() + ""
                                appCompatTextView18?.visibility = View.VISIBLE
                            }
                            if (ticketLenght > 9) {
                                appCompatTextView19?.text =
                                    plateNumber[ticketLenght - 8].toString() + ""
                                appCompatTextView19?.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            } else {
                try {
                    /**
                     * AMOUNT First position from top
                     * "[CITATION_NO][AMOUNT][LPR PLATE]"
                     */
                    relativeLayoutPrintStateSection!!.visibility = View.GONE
                    if (OCRTextValue.size > 2 && OCRTextValue[2].equals("[AMOUNT")) {

                        val amount =
                            mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                                .split(Regex("\\."))[0]
                        val amountLenght = amount.length
                        //                for(int i=1; i<=amountLenght;i++)
//                {
                        if (amountLenght == 1) {
                            appCompatTextView14?.text = amount + ""
                        } else if (amountLenght == 2) {
                            appCompatTextView13?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 2].toString() + ""
                        } else if (amountLenght == 3) {
                            appCompatTextView13?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView15?.text = amount[amountLenght - 3].toString() + ""
                        } else if (amountLenght == 4) {
                            appCompatTextView12?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView13?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView15?.text = amount[amountLenght - 4].toString() + ""
                        } else if (amountLenght == 5) {
                            appCompatTextView12?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView13?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView15?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView16?.text = amount[amountLenght - 5].toString() + ""
                        } else if (amountLenght == 6) {
                            appCompatTextView1?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView12?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView13?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView14?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView15?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView16?.text = amount[amountLenght - 6].toString() + ""
                        }
                        try {
                            mFinalAmount =
                                appCompatTextView16!!.text.toString() + appCompatTextView15!!.text.toString() +
                                        appCompatTextView14!!.text.toString() + appCompatTextView13!!.text.toString() +
                                        appCompatTextView12!!.text.toString() + appCompatTextView1!!.text.toString();


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 1 && OCRTextValue[1].equals("[AMOUNT")) {
                        /**
                         * Middle View section for amount
                         */

                        val amount =
                            mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                                .split(Regex("\\."))[0]
                        val amountLenght = amount.length

//                        appCompatTextView21?.visibility = View.GONE
//                        appCompatTextView22?.visibility = View.GONE
//                        appCompatTextView23?.visibility = View.GONE
//                        appCompatTextView24?.visibility = View.GONE
//                        appCompatTextView25?.visibility = View.GONE
//                        appCompatTextView26?.visibility = View.GONE
                        appCompatTextView27?.visibility = View.GONE
                        appCompatTextView28?.visibility = View.GONE
                        appCompatTextView29?.visibility = View.GONE

                        appCompatTextView21?.text = "0"
                        appCompatTextView22?.text = "0"
                        appCompatTextView23?.text = "0"
                        appCompatTextView24?.text = "0"
                        appCompatTextView25?.text = "0"
                        appCompatTextView26?.text = "0"

                        if (amountLenght == 1) {
                            appCompatTextView24?.text = amount + ""
                            appCompatTextView24?.visibility = View.VISIBLE
                        } else if (amountLenght == 2) {
                            appCompatTextView23?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                        } else if (amountLenght == 3) {
                            appCompatTextView23?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView25?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.visibility = View.VISIBLE
                        } else if (amountLenght == 4) {
                            appCompatTextView22?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView23?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView25?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView22?.visibility = View.VISIBLE
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.visibility = View.VISIBLE
                        } else if (amountLenght == 5) {
                            appCompatTextView22?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView23?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView25?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView26?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView22?.visibility = View.VISIBLE
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.visibility = View.VISIBLE
                            appCompatTextView26?.visibility = View.VISIBLE
                        } else if (amountLenght == 6) {
                            appCompatTextView21?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView22?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView23?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView24?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView25?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView26?.text = amount[amountLenght - 6].toString() + ""

                            appCompatTextView21?.visibility = View.VISIBLE
                            appCompatTextView22?.visibility = View.VISIBLE
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.visibility = View.VISIBLE
                            appCompatTextView26?.visibility = View.VISIBLE
                        }
                        try {
                            mFinalAmount =
                                appCompatTextView26!!.text.toString() + appCompatTextView25!!.text.toString() +
                                        appCompatTextView24!!.text.toString() + appCompatTextView23!!.text.toString() +
                                        appCompatTextView22!!.text.toString() + appCompatTextView21!!.text.toString();


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 0 && OCRTextValue[0].equals("[AMOUNT")) {
                        /**
                         * Middle View section for amount
                         */
                        val amount =
                            mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                                .split(Regex("\\."))[0]
                        val amountLenght = amount.length

                        appCompatTextView31?.visibility = View.GONE
                        appCompatTextView32?.visibility = View.GONE
                        appCompatTextView33?.visibility = View.GONE
                        appCompatTextView34?.visibility = View.GONE
                        appCompatTextView35?.visibility = View.GONE
                        appCompatTextView36?.visibility = View.GONE
                        appCompatTextView37?.visibility = View.GONE
                        appCompatTextView38?.visibility = View.GONE
                        appCompatTextView39?.visibility = View.GONE

                        if (amountLenght == 1) {
                            appCompatTextView34?.text = amount + ""
                            appCompatTextView34?.visibility = View.VISIBLE
                        } else if (amountLenght == 2) {
                            appCompatTextView33?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                        } else if (amountLenght == 3) {
                            appCompatTextView33?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView35?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.visibility = View.VISIBLE
                        } else if (amountLenght == 4) {
                            appCompatTextView32?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView33?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView35?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView32?.visibility = View.VISIBLE
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.visibility = View.VISIBLE
                        } else if (amountLenght == 5) {
                            appCompatTextView32?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView33?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView35?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView36?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView32?.visibility = View.VISIBLE
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.visibility = View.VISIBLE
                            appCompatTextView36?.visibility = View.VISIBLE
                        } else if (amountLenght == 6) {
                            appCompatTextView31?.text = amount[amountLenght - 1].toString() + ""
                            appCompatTextView32?.text = amount[amountLenght - 2].toString() + ""
                            appCompatTextView33?.text = amount[amountLenght - 3].toString() + ""
                            appCompatTextView34?.text = amount[amountLenght - 4].toString() + ""
                            appCompatTextView35?.text = amount[amountLenght - 5].toString() + ""
                            appCompatTextView36?.text = amount[amountLenght - 6].toString() + ""

                            appCompatTextView31?.visibility = View.VISIBLE
                            appCompatTextView32?.visibility = View.VISIBLE
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.visibility = View.VISIBLE
                            appCompatTextView36?.visibility = View.VISIBLE
                        }
                        try {
                            mFinalAmount =
                                appCompatTextView36!!.text.toString() + appCompatTextView35!!.text.toString() +
                                        appCompatTextView34!!.text.toString() + appCompatTextView33!!.text.toString() +
                                        appCompatTextView32!!.text.toString() + appCompatTextView31!!.text.toString();


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                /**
                 * State section sub first section
                 */
                try {

                    if (OCRTextValue.size > 1 && OCRTextValue[1].equals("[STATE")) {

                        val state = mIssuranceModel!!.citationData!!.vehicle!!.state
                        appCompatTextView14_4?.text = (state?.get(1)?.toString() ?: "") + ""
                        appCompatTextView15_5?.text = (state?.get(0)?.toString() ?: "") + ""
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                /**
                 * CITATION_NO for all three position
                 */
                try {
                    if (OCRTextValue.size > 0 && OCRTextValue[0].equals("[CITATION_NO")) {

                        val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                        appCompatTextView31?.visibility = View.GONE
                        appCompatTextView32?.visibility = View.GONE
                        appCompatTextView33?.visibility = View.GONE
                        appCompatTextView34?.visibility = View.GONE
                        appCompatTextView35?.visibility = View.GONE
                        appCompatTextView36?.visibility = View.GONE
                        appCompatTextView37?.visibility = View.GONE
                        appCompatTextView38?.visibility = View.GONE
                        appCompatTextView39?.visibility = View.GONE


                        if (ticektNumber!!.length > 8) {
                            appCompatTextView31?.text = ticektNumber[8].toString() + ""
                            appCompatTextView31?.visibility = View.VISIBLE
                        }
                        if (ticektNumber!!.length > 7) {
                            appCompatTextView32?.text = ticektNumber[7].toString() + ""
                            appCompatTextView32?.visibility = View.VISIBLE
                        }
                        appCompatTextView33?.text = ticektNumber[6].toString() + ""
                        appCompatTextView34?.text = ticektNumber[5].toString() + ""
                        appCompatTextView35?.text = ticektNumber[4].toString() + ""
                        appCompatTextView36?.text = ticektNumber[3].toString() + ""
                        appCompatTextView37?.text = ticektNumber[2].toString() + ""
                        appCompatTextView38?.text = ticektNumber[1].toString() + ""
                        appCompatTextView39?.text = ticektNumber[0].toString() + ""

                        appCompatTextView32?.visibility = View.VISIBLE
                        appCompatTextView33?.visibility = View.VISIBLE
                        appCompatTextView34?.visibility = View.VISIBLE
                        appCompatTextView35?.visibility = View.VISIBLE
                        appCompatTextView36?.visibility = View.VISIBLE
                        appCompatTextView37?.visibility = View.VISIBLE
                        appCompatTextView38?.visibility = View.VISIBLE
                        appCompatTextView39?.visibility = View.VISIBLE
                    } else if (OCRTextValue.size > 1 && OCRTextValue[1].equals("[CITATION_NO")) {

                        val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                        if (ticektNumber!!.length > 8) {
                            appCompatTextView21?.text = ticektNumber[8].toString() + ""
                        }
                        if (ticektNumber!!.length > 7) {
                            appCompatTextView22?.text = ticektNumber[7].toString() + ""
                        }
                        appCompatTextView23?.text = ticektNumber[6].toString() + ""
                        appCompatTextView24?.text = ticektNumber[5].toString() + ""
                        appCompatTextView25?.text = ticektNumber[4].toString() + ""
                        appCompatTextView26?.text = ticektNumber[3].toString() + ""
                        appCompatTextView27?.text = ticektNumber[2].toString() + ""
                        appCompatTextView28?.text = ticektNumber[1].toString() + ""
                        appCompatTextView29?.text = ticektNumber[0].toString() + ""
                    } else if (OCRTextValue.size > 2 && OCRTextValue[2].equals("[CITATION_NO")) {
                        val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                        appCompatTextView1?.visibility = View.GONE
                        appCompatTextView12?.visibility = View.GONE
                        appCompatTextView13?.visibility = View.GONE
                        appCompatTextView14?.visibility = View.GONE
                        appCompatTextView15?.visibility = View.GONE
                        appCompatTextView16?.visibility = View.GONE
                        appCompatTextView17?.visibility = View.GONE
                        appCompatTextView18?.visibility = View.GONE
                        appCompatTextView19?.visibility = View.GONE


                        if (ticektNumber!!.length > 8) {
                            appCompatTextView1?.text = ticektNumber[8].toString() + ""
                            appCompatTextView1?.visibility = View.VISIBLE
                        }
                        if (ticektNumber!!.length > 7) {
                            appCompatTextView12?.text = ticektNumber[7].toString() + ""
                            appCompatTextView12?.visibility = View.VISIBLE
                        }
                        appCompatTextView13?.text = ticektNumber[6].toString() + ""
                        appCompatTextView14?.text = ticektNumber[5].toString() + ""
                        appCompatTextView15?.text = ticektNumber[4].toString() + ""
                        appCompatTextView16?.text = ticektNumber[3].toString() + ""
                        appCompatTextView17?.text = ticektNumber[2].toString() + ""
                        appCompatTextView18?.text = ticektNumber[1].toString() + ""
                        appCompatTextView19?.text = ticektNumber[0].toString() + ""

                        appCompatTextView13?.visibility = View.VISIBLE
                        appCompatTextView14?.visibility = View.VISIBLE
                        appCompatTextView15?.visibility = View.VISIBLE
                        appCompatTextView16?.visibility = View.VISIBLE
                        appCompatTextView17?.visibility = View.VISIBLE
                        appCompatTextView18?.visibility = View.VISIBLE
                        appCompatTextView19?.visibility = View.VISIBLE
                    }

                    /***
                     * Lpr plate Number
                     * */
                    if (OCRTextValue.size > 1 && OCRTextValue[0].equals("[LPR PLATE")) {
                        try {
                            val plateNumber =
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                            val ticketLenght = plateNumber!!.length - 1
                            appCompatTextView31?.visibility = View.GONE
                            appCompatTextView32?.visibility = View.GONE
                            appCompatTextView33?.visibility = View.GONE
                            appCompatTextView34?.visibility = View.GONE
                            appCompatTextView35?.visibility = View.GONE
                            appCompatTextView36?.visibility = View.GONE
                            appCompatTextView37?.visibility = View.GONE
                            appCompatTextView38?.visibility = View.GONE
                            appCompatTextView39?.visibility = View.GONE

                            appCompatTextView31?.text = plateNumber[ticketLenght].toString() + ""
                            appCompatTextView31?.visibility = View.VISIBLE
                            appCompatTextView32?.text =
                                plateNumber[ticketLenght - 1].toString() + ""
                            appCompatTextView32?.visibility = View.VISIBLE
                            appCompatTextView33?.text =
                                plateNumber[ticketLenght - 2].toString() + ""
                            appCompatTextView33?.visibility = View.VISIBLE
                            appCompatTextView34?.text =
                                plateNumber[ticketLenght - 3].toString() + ""
                            appCompatTextView34?.visibility = View.VISIBLE
                            appCompatTextView35?.text =
                                plateNumber[ticketLenght - 4].toString() + ""
                            appCompatTextView35?.visibility = View.VISIBLE
                            appCompatTextView36?.text =
                                plateNumber[ticketLenght - 5].toString() + ""
                            appCompatTextView36?.visibility = View.VISIBLE
                            appCompatTextView37?.text =
                                plateNumber[ticketLenght - 6].toString() + ""
                            appCompatTextView37?.visibility = View.VISIBLE
                            if (ticketLenght > 8) {
                                appCompatTextView38?.text =
                                    plateNumber[ticketLenght - 7].toString() + ""
                                appCompatTextView38?.visibility = View.VISIBLE
                            }
                            if (ticketLenght > 9) {
                                appCompatTextView39?.text =
                                    plateNumber[ticketLenght - 8].toString() + ""
                                appCompatTextView39?.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 1 && OCRTextValue[1].equals("[LPR PLATE")) {
                        try {
                            val plateNumber =
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                            val ticketLenght = plateNumber!!.length - 1
                            appCompatTextView21?.visibility = View.GONE
                            appCompatTextView22?.visibility = View.GONE
                            appCompatTextView23?.visibility = View.GONE
                            appCompatTextView24?.visibility = View.GONE
                            appCompatTextView25?.visibility = View.GONE
                            appCompatTextView26?.visibility = View.GONE
                            appCompatTextView27?.visibility = View.GONE
                            appCompatTextView28?.visibility = View.GONE
                            appCompatTextView29?.visibility = View.GONE

                            appCompatTextView21?.text = plateNumber[ticketLenght].toString() + ""
                            appCompatTextView21?.visibility = View.VISIBLE
                            appCompatTextView22?.text =
                                plateNumber[ticketLenght - 1].toString() + ""
                            appCompatTextView22?.visibility = View.VISIBLE
                            appCompatTextView23?.text =
                                plateNumber[ticketLenght - 2].toString() + ""
                            appCompatTextView23?.visibility = View.VISIBLE
                            appCompatTextView24?.text =
                                plateNumber[ticketLenght - 3].toString() + ""
                            appCompatTextView24?.visibility = View.VISIBLE
                            appCompatTextView25?.text =
                                plateNumber[ticketLenght - 4].toString() + ""
                            appCompatTextView25?.visibility = View.VISIBLE
                            appCompatTextView26?.text =
                                plateNumber[ticketLenght - 5].toString() + ""
                            appCompatTextView26?.visibility = View.VISIBLE
                            appCompatTextView27?.text =
                                plateNumber[ticketLenght - 6].toString() + ""
                            appCompatTextView27?.visibility = View.VISIBLE
                            if (ticketLenght > 8) {
                                appCompatTextView28?.text =
                                    plateNumber[ticketLenght - 7].toString() + ""
                                appCompatTextView28?.visibility = View.VISIBLE
                            }
                            if (ticketLenght > 9) {
                                appCompatTextView29?.text =
                                    plateNumber[ticketLenght - 8].toString() + ""
                                appCompatTextView29?.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (OCRTextValue.size > 2 && OCRTextValue[2].equals("[LPR PLATE")) {
                        try {
                            val plateNumber =
                                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                            val ticketLenght = plateNumber!!.length - 1
                            appCompatTextView1?.visibility = View.GONE
                            appCompatTextView12?.visibility = View.GONE
                            appCompatTextView13?.visibility = View.GONE
                            appCompatTextView14?.visibility = View.GONE
                            appCompatTextView15?.visibility = View.GONE
                            appCompatTextView16?.visibility = View.GONE
                            appCompatTextView17?.visibility = View.GONE
                            appCompatTextView18?.visibility = View.GONE
                            appCompatTextView19?.visibility = View.GONE

                            appCompatTextView1?.text = plateNumber[ticketLenght].toString() + ""
                            appCompatTextView1?.visibility = View.VISIBLE
                            appCompatTextView12?.text =
                                plateNumber[ticketLenght - 1].toString() + ""
                            appCompatTextView12?.visibility = View.VISIBLE
                            appCompatTextView13?.text =
                                plateNumber[ticketLenght - 2].toString() + ""
                            appCompatTextView13?.visibility = View.VISIBLE
                            appCompatTextView14?.text =
                                plateNumber[ticketLenght - 3].toString() + ""
                            appCompatTextView14?.visibility = View.VISIBLE
                            appCompatTextView15?.text =
                                plateNumber[ticketLenght - 4].toString() + ""
                            appCompatTextView15?.visibility = View.VISIBLE
                            appCompatTextView16?.text =
                                plateNumber[ticketLenght - 5].toString() + ""
                            appCompatTextView16?.visibility = View.VISIBLE
                            appCompatTextView17?.text =
                                plateNumber[ticketLenght - 6].toString() + ""
                            appCompatTextView17?.visibility = View.VISIBLE
                            if (ticketLenght > 8) {
                                appCompatTextView18?.text =
                                    plateNumber[ticketLenght - 7].toString() + ""
                                appCompatTextView18?.visibility = View.VISIBLE
                            }
                            if (ticketLenght > 9) {
                                appCompatTextView19?.text =
                                    plateNumber[ticketLenght - 8].toString() + ""
                                appCompatTextView19?.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (OCRTextValue.size > 2 && OCRTextValue[0].equals("[OCMD"))
            {
                appCompatTextView31!!.text = "D"
                appCompatTextView32!!.text = "M"
                appCompatTextView33!!.text = "C"
                appCompatTextView34!!.text = "O"
            }
          val mOCRText1 = appCompatTextView19!!.text.toString()+""+appCompatTextView18!!.text+""+appCompatTextView17!!.text+
                    appCompatTextView16!!.text.toString()+""+appCompatTextView15!!.text+""+appCompatTextView14!!.text+
                    appCompatTextView13!!.text.toString()+""+appCompatTextView12!!.text+""+appCompatTextView1!!.text
          val mOCRText2 = appCompatTextView19_9!!.text.toString()+""+appCompatTextView18_8!!.text+""+appCompatTextView17_7!!.text+
                    appCompatTextView16_6!!.text.toString()+""+appCompatTextView15_5!!.text+""+appCompatTextView14_4!!.text+
                    appCompatTextView13_3!!.text.toString()+""+appCompatTextView12_2!!.text+""+appCompatTextView1_1!!.text
          val mOCRText3 = appCompatTextView29!!.text.toString()+""+appCompatTextView28!!.text+""+appCompatTextView27!!.text+
                    appCompatTextView26!!.text.toString()+""+appCompatTextView25!!.text+""+appCompatTextView24!!.text+
                    appCompatTextView23!!.text.toString()+""+appCompatTextView22!!.text+""+appCompatTextView21!!.text
          val mOCRText4 = appCompatTextView39!!.text.toString()+""+appCompatTextView38!!.text+""+appCompatTextView37!!.text+
                    appCompatTextView36!!.text.toString()+""+appCompatTextView35!!.text+""+appCompatTextView34!!.text+
                    appCompatTextView33!!.text.toString()+""+appCompatTextView32!!.text+""+appCompatTextView31!!.text

            var Y:Int =  300
            var mYIncrease:Int =  250
            val X:Int =  551
            if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)){
                Y = 400
            }else if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
                ||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)){
                Y = 120
            }else if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_KENOSHA, ignoreCase = true)){
                Y = 120
                mYIncrease =  300
            }

            LogUtil.printLog("==>OCR_LPR:","First")

            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintOCRTEXT(
                mOCRText1!!.toString(),
                1,
                "OCR TEXT",
                AppUtils.printQueryStringBuilder!!,X,Y
            )
            if(mOCRText2!=null && mOCRText2.length>1) {
                LogUtil.printLog("==>OCR_LPR:","Second")

                Y = (Y + mYIncrease)
                AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintOCRTEXT(
                    mOCRText2!!.toString(),
                    1,
                    "OCR TEXT",
                    AppUtils.printQueryStringBuilder!!, X, Y
                )
            }
            if(mOCRText3!=null && mOCRText3.length>1) {
                LogUtil.printLog("==>OCR_LPR:","Third")

                Y = (Y + mYIncrease)
                AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintOCRTEXT(
                    mOCRText3!!.toString(),
                    1,
                    "OCR TEXT",
                    AppUtils.printQueryStringBuilder!!, X, Y
                )
            }
            if(mOCRText4!=null && mOCRText3.length>1) {
                LogUtil.printLog("==>OCR_LPR:","Four")

                Y = (Y + mYIncrease)
                AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintOCRTEXT(
                    mOCRText4!!.toString(),
                    1,
                    "OCR TEXT",
                    AppUtils.printQueryStringBuilder!!, X, Y
                )
 }
        }
    }

    private fun setTextViewValueForVallejo() {
        if (mIssuranceModel != null) {
            try {

                /**
                 * AMOUNT
                 */
                val amount =
                        mIssuranceModel?.citationData?.voilation?.amount.nullSafety().split(Regex("\\."))[0]
                val amountLenght = amount.length

                if (amountLenght == 1) {
                    appCompatTextView14?.text = amount + ""
                } else if (amountLenght == 2) {
                    appCompatTextView13?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 2].toString() + ""
                } else if (amountLenght == 3) {
                    appCompatTextView13?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 2].toString() + ""
                    appCompatTextView15?.text = amount[amountLenght - 3].toString() + ""
                } else if (amountLenght == 4) {
                    appCompatTextView12?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView13?.text = amount[amountLenght - 2].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 3].toString() + ""
                    appCompatTextView15?.text = amount[amountLenght - 4].toString() + ""
                } else if (amountLenght == 5) {
                    appCompatTextView12?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView13?.text = amount[amountLenght - 2].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 3].toString() + ""
                    appCompatTextView15?.text = amount[amountLenght - 4].toString() + ""
                    appCompatTextView16?.text = amount[amountLenght - 5].toString() + ""
                } else if (amountLenght == 6) {
                    appCompatTextView1?.text = amount[amountLenght - 1].toString() + ""
                    appCompatTextView12?.text = amount[amountLenght - 2].toString() + ""
                    appCompatTextView13?.text = amount[amountLenght - 3].toString() + ""
                    appCompatTextView14?.text = amount[amountLenght - 4].toString() + ""
                    appCompatTextView15?.text = amount[amountLenght - 5].toString() + ""
                    appCompatTextView16?.text = amount[amountLenght - 6].toString() + ""
                }
                mFinalAmount = appCompatTextView1!!.text.toString()+appCompatTextView12!!.text.toString()+
                        appCompatTextView13!!.text.toString()+appCompatTextView14!!.text.toString()+
                        appCompatTextView15!!.text.toString()+appCompatTextView15!!.text.toString();
            } catch (e: Exception) {
                e.printStackTrace()
            }
            /***
             * Lpr plate Number
             */
            try {
                val plateNumber = mIssuranceModel!!.citationData!!.vehicle!!.licensePlate
                val ticketLenght = plateNumber!!.length-1
                //                for(int i=1; i<=ticketLenght;i++)
//                {
                appCompatTextView21?.visibility = View.GONE
                appCompatTextView22?.visibility = View.GONE
                appCompatTextView23?.visibility = View.GONE
                appCompatTextView24?.visibility = View.GONE
                appCompatTextView25?.visibility = View.GONE
                appCompatTextView26?.visibility = View.GONE
                appCompatTextView27?.visibility = View.GONE
                appCompatTextView28?.visibility = View.GONE
                appCompatTextView29?.visibility = View.GONE

                appCompatTextView21?.text = plateNumber[ticketLenght].toString() + ""
                appCompatTextView21?.visibility = View.VISIBLE
                appCompatTextView22?.text = plateNumber[ticketLenght-1].toString() + ""
                appCompatTextView22?.visibility = View.VISIBLE
                appCompatTextView23?.text = plateNumber[ticketLenght-2].toString() + ""
                appCompatTextView23?.visibility = View.VISIBLE
                appCompatTextView24?.text = plateNumber[ticketLenght-3].toString() + ""
                appCompatTextView24?.visibility = View.VISIBLE
                appCompatTextView25?.text = plateNumber[ticketLenght-4].toString() + ""
                appCompatTextView25?.visibility = View.VISIBLE
                appCompatTextView26?.text = plateNumber[ticketLenght-5].toString() + ""
                appCompatTextView26?.visibility = View.VISIBLE
                appCompatTextView27?.text = plateNumber[ticketLenght-6].toString() + ""
                appCompatTextView27?.visibility = View.VISIBLE
                appCompatTextView28?.text = plateNumber[ticketLenght-7].toString() + ""
                appCompatTextView28?.visibility = View.VISIBLE
                appCompatTextView29?.text = plateNumber[ticketLenght-8].toString() + ""
                appCompatTextView29?.visibility = View.VISIBLE
                //                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            /***
             * TIcket Number
             */
            try {
                val ticektNumber = mIssuranceModel!!.citationData!!.ticketNumber
                val ticketLenght = ticektNumber!!.length
                //                for(int i=1; i<=ticketLenght;i++)
//                {

                appCompatTextView31?.text = ticektNumber[ticketLenght-1].toString() + ""
                appCompatTextView32?.text = ticektNumber[ticketLenght-2].toString() + ""
                appCompatTextView33?.text = ticektNumber[ticketLenght-3].toString() + ""
                appCompatTextView34?.text = ticektNumber[ticketLenght-4].toString() + ""
                appCompatTextView35?.text = ticektNumber[ticketLenght-5].toString() + ""
                appCompatTextView36?.text = ticektNumber[ticketLenght-6].toString() + ""
                appCompatTextView37?.text = ticektNumber[ticketLenght-7].toString() + ""

                appCompatTextView38?.visibility = View.VISIBLE
                appCompatTextView39?.visibility = View.VISIBLE
                if(ticketLenght>7){
                    appCompatTextView38?.visibility = View.VISIBLE
                    appCompatTextView39?.visibility = View.VISIBLE
                    appCompatTextView38?.text = ticektNumber[ticketLenght-8].toString() + ""
                    appCompatTextView39?.text = ticektNumber[ticketLenght-9].toString() + ""
                }
                //                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var savePrintImagePath: File? = null
    }

    private fun setLayoutVisibilityBasedOnSettingResponse() {
        try {
            settingsList = ArrayList()
            ioScope.async {
                settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())
                mainScope.async {
                    if (settingsList != null && settingsList!!.size > 0) {
                        for (i in settingsList!!.indices) {
                            if (settingsList!![i].type.equals("BARCODE_URL", ignoreCase = true)
                                    && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                isBarCodePrintlayout = false
                            }
                            else if (settingsList!![i].type.equals("QRCODE_URL", ignoreCase = true)
                                    && !settingsList!![i].mValue!!.isEmpty() &&
                                    !settingsList!![i].mValue.equals("NO", ignoreCase = true)
                                    || settingsList!![i].type.equals("QRCODE_URL", ignoreCase = true)&&
                                    settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                isQRCodePrintlayout = true
                                mQRCodeValue = settingsList!![i].mValue
                            }
                            else if (settingsList!![i].type.equals("OCR_SCANNER", ignoreCase = true)) {
                                isOCRTextPrintlayout = true
                                mOCRFormatValue = settingsList!![i].mValue

                            }else if (settingsList!![i].type.equals("SIMILAR_CITATION_BYPASS", ignoreCase = true)&&
                                    settingsList!![i].mValue.equals("YES",ignoreCase = true)) {
                                isByPassSimilarCitation = true
                            }else if (settingsList!![i].type.equals("IS_SIMILAR_CITATION_API_ERROR_POPUP_DISPLAY", ignoreCase = true)&&
                                    settingsList!![i].mValue.equals("YES",ignoreCase = true)) {
                                isSimilarCitationAPIErrorPopupDisplay = true
                            }else if (settingsList!![i].type.equals(SETTINGS_FLAG_OFFICER_NAME_FORMAT_FOR_PRINT, ignoreCase = true)) {
                                officerNameFormatForPrint = settingsList!![i].mValue.nullSafety()
                            }
                        }
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            GenerateBarCode(mCitationNumberId, isBarCodePrintlayout)
                        }, 200)
                        if(isOCRTextPrintlayout)
                        {
                            if (!LogUtil.isEnableCommandBasedFacsimile){
                                relativeLayoutPrintPhilaSection!!.visibility = View.VISIBLE
                                relativeLayoutPrintCitationSection!!.visibility = View.VISIBLE
                                relativeLayoutPrintAmountSection!!.visibility = View.VISIBLE
                                relativeLayoutPrintStateSection!!.visibility = View.VISIBLE
                            }

                            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                                (BuildConfig.FLAVOR.equals(DuncanBrandingApp13())&&
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)&&
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA, ignoreCase = true))||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true))
                            {
                                relativeLayoutPrintCitationSection!!.visibility = View.INVISIBLE
                                relativeLayoutPrintAmountSection!!.visibility = View.INVISIBLE
                                relativeLayoutPrintStateSection!!.visibility = View.INVISIBLE
                                relativeLayoutPrintPhilaSection!!.visibility = View.INVISIBLE
                            }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)){
                                if (!LogUtil.isEnableCommandBasedFacsimile){
                                    relativeLayoutPrintPhilaSection!!.visibility = View.VISIBLE
                                    relativeLayoutPrintStateSection!!.visibility = View.VISIBLE
                                    relativeLayoutPrintCitationSection!!.visibility = View.VISIBLE
                                    relativeLayoutPrintAmountSection!!.visibility = View.VISIBLE
                                }

                            }
                        }else{
                            relativeLayoutPrintPhilaSection!!.visibility = View.INVISIBLE
                            relativeLayoutPrintStateSection!!.visibility = View.INVISIBLE
                            relativeLayoutPrintCitationSection!!.visibility = View.INVISIBLE
                            relativeLayoutPrintAmountSection!!.visibility = View.INVISIBLE
                        }
                    }
                    callSimilarAndLastSecondCheckAPI()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        layTicketDetails.removeAllViews()
        layOfficerDetails.removeAllViews()
        layNotes.removeAllViews()
        layRemarks.removeAllViews()
        layVehicleDetails.removeAllViews()
        layLocationDetails.removeAllViews()
        layVoilationDetails.removeAllViews()

        mContext = null
        super.onDestroy()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        val override = Configuration(newBase.resources.configuration)
        override.fontScale = 1.0f
        applyOverrideConfiguration(override)
    }

    private fun lastSecondCheckResponseForPopup()
    {
        if (responseLastSecondCheckModel != null && responseLastSecondCheckModel!!.isStatus) {
            mLastSecondCheckMessage =
                    responseLastSecondCheckModel!!.data!!.lastSecondCheck!!.message
            isLastSecondCheck =
                    responseLastSecondCheckModel!!.data!!.lastSecondCheck!!.isLastSecondCheckStatus
            /** last second check popup on false */
            if (!isLastSecondCheck) {
                lastSecondTag = "lastsecond"
                showCustomAlertDialog(mContext, getString(R.string.scr_btn_no_payment), mLastSecondCheckMessage, getString(R.string.alt_lbl_OK),
                        getString(R.string.scr_btn_cancel), this)
            } else {
//                moveNextScreen()
                lastSecondTag = "lastsecondreturnfalse"
                showCustomAlertDialog(mContext, getString(R.string.scr_btn_no_payment), getString(R.string.scr_lbl_payment_not_fount_for_selected_violation), getString(R.string.alt_lbl_OK),
                    getString(R.string.scr_btn_cancel), this)
            }
        } else {
            lastSecondTag = "ERROR"
            showCustomAlertDialog(mContext, getString(R.string.scr_btn_no_payment), getString(R.string.err_msg_something_went_wrong),
                    getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this)
            dismissLoader()
        }
    }

    fun similarCitationCheckResponseForPopup()
    {
        try {
            if (responseSimilarCitationCheckModel != null && responseSimilarCitationCheckModel!!.isStatus) {
                if (!responseSimilarCitationCheckModel!!.dataSimilar!!.similarityCheck!!.isSimilarityCheckStatus) {
                    lastSecondTag = "similarcitation"
                    showCustomAlertDialog(mContext, APIConstant.SIMILAR_CITATION_CHECK,
                            responseSimilarCitationCheckModel!!.dataSimilar!!.similarityCheck!!.message,
                            getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this)
                } else {
                    if (sharedPreference.read(SharedPrefKey.LAST_SECOND_CHECK, false)) {
                        lastSecondCheckResponseForPopup()
                    } else {
                        moveNextScreen()
                    }
                }
            } else {
                lastSecondTag = "ERROR"
                showCustomAlertDialog(mContext, APIConstant.SIMILAR_CITATION_CHECK,
                        getString(R.string.err_msg_something_went_wrong),
                        getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this)
                dismissLoader()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
  /*  fun drawTextToBitmap(bitmap: Bitmap?, mText: String?): Bitmap? {
        var bitmap: Bitmap? = bitmap
        try {
            var bitmapConfig: Bitmap.Config? = bitmap!!.config
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true)
            val canvas: Canvas = Canvas(bitmap)
            // new antialised Paint
            val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
            // text color - #3D3D3D
            paint.color = Color.rgb(0, 0, 0)
            // text size in pixels
            paint.textSize = (11 * 2).toFloat()
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.BLACK)

//            paint.getTypeface(context.getResources().getFont(R.font.sf_pro_text_semibold));
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    paint.typeface = Typeface.create(
                            mContext-!!.resources.getFont(R.font.timesnewromanpsmtregular),
                            Typeface.NORMAL
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // draw text to the Canvas center
            val bounds: Rect = Rect()
            paint.getTextBounds(mText, 0, mText!!.length, bounds)
            val x: Int = (bitmap.width)
            val y: Int = bitmap.height - 70
            if (mText.equals("R", ignoreCase = true)) {
                canvas.drawText(
                        (mText),
                        (x - 190).toFloat(),
                        AppUtils.printLabelHeight().toFloat(),
                        paint
                )
            } else {
                canvas.drawText(
                        (mText),
                        (x - 200).toFloat(),
                        AppUtils.printLabelHeight().toFloat(),
                        paint
                )
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return bitmap
        }
    }*/
}