package com.parkloyalty.lpr.scan.ui.municipalcitation

import android.Manifest
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission

//import androidmads.library.qrgenearator.QRGContents
//import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
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
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.getSettingFileValuesForCMDPrinting
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.invisibleView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.*
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_OFFICER_NAME_FORMAT_FOR_PRINT
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.*
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationIssuranceModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLocationModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationOfficerModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationVehicleModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationVoilationModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.MunicipalCitationMotoristDetailsModel
import com.parkloyalty.lpr.scan.ui.printer.PrinterType
import com.parkloyalty.lpr.scan.ui.printer.StarPrinterUseCase
import com.parkloyalty.lpr.scan.ui.printer.ZebraPrinterUseCase
import com.parkloyalty.lpr.scan.ui.reprint.model.DataItem
import com.parkloyalty.lpr.scan.ui.reprint.model.FacsimileImagesViewModel
import com.parkloyalty.lpr.scan.ui.reprint.model.ResponseFacsimileImages
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import com.parkloyalty.lpr.scan.ui.xfprinter.XfPrinterUseCase
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.checkPrintLayoutOrder
import com.parkloyalty.lpr.scan.util.AppUtils.checkPrintLayoutOrderForTwoColumn
import com.parkloyalty.lpr.scan.util.AppUtils.drawableElements
import com.parkloyalty.lpr.scan.util.CitationPrintSectionUtils
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.FileUtil.getSignatureFileNameWithExt
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.util.PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils.isPrintLprImageInFacsimilePrint
import com.parkloyalty.lpr.scan.util.QRCodeUtils
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils
import com.parkloyalty.lpr.scan.util.commandprint.CanvasUtils
import com.parkloyalty.lpr.scan.util.getPrintOrder
import com.parkloyalty.lpr.scan.util.getSectionTitle
import com.parkloyalty.lpr.scan.util.permissions.BluetoothPermissionUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.getValue


//    class ReprintReuploadActivity : SwitchPrinterActivity(), PrintInterface, CustomDialogHelper {
    class MunicipalCitationReprintReuploadActivity : BaseActivity(), PrintInterface, CustomDialogHelper {
//    class ReprintReuploadActivity : XfPrinterActivity(), PrintInterface, CustomDialogHelper {
//class ReprintReuploadActivity : StarPrinterActivity(), PrintInterface, CustomDialogHelper {
//
    @BindView(R.id.layMainPrint)
    lateinit var linearLayoutMainPrint: RelativeLayout

    @BindView(R.id.ll_child_container)
    lateinit var linearLayoutCompatChildContainer: LinearLayoutCompat

    @BindView(R.id.ll_numberview_1)
    lateinit var relativeLayoutPrintAmountSection: RelativeLayout

    @BindView(R.id.ll_numberview_2)
    lateinit var relativeLayoutPrintCitationSection: RelativeLayout

    @BindView(R.id.ll_numberview_3)
    lateinit var relativeLayoutPrintPhilaSection: RelativeLayout

    @BindView(R.id.tv_top_message)
    lateinit var appCompatTextViewTopMessage: AppCompatTextView

    @BindView(R.id.ll_stateview_1_1)
    lateinit var relativeLayoutPrintStateSection: RelativeLayout

    @BindView(R.id.ivCommandBasedFacsimile)
    lateinit var ivCommandBasedFacsimile: AppCompatImageView

//    @BindView(R.id.imgSignature)
//    lateinit var mImageViewSignature: AppCompatImageView
//
//    @BindView(R.id.txtPersonSignature)
//    lateinit var mTextViewSignName: AppCompatTextView

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

    private var linearLayoutCompatBarCode: LinearLayoutCompat? = null
    private var mImageViewBarcodePrint: AppCompatImageView? = null

    private var mPrintLayoutMap: HashMap<String, Int> = HashMap<String, Int>()
    private val mPrintLayoutTitle = arrayOfNulls<String>(7)
//    private var mOfficerList = ArrayList<VehicleListModel>()
//    private var mVehicleList = ArrayList<VehicleListModel>()
//    private var mViolationList = ArrayList<VehicleListModel>()
//    private var mCitationList = ArrayList<VehicleListModel>()
//    private var mCommentsList = ArrayList<VehicleListModel>()
    private val mUploadImagesLink: MutableList<String> = ArrayList()
    private var mCitaionListAdapter: MunicipalCitationAdapter? = null
    private var mOfficerListAdapter: MunicipalOfficerListAdapter? = null
    private var mVehicalListAdapter: MunicipalVehicalListAdapter? = null
    private var mViolationsAdapter: MunicipalViolationsAdapter? = null
    private var commentesAdapter: MunicipalCommentesAdapter? = null
    private var motoristInformationAdapter: MunicipalMotoristInformationAdapter? = null
    private var settingsList: List<DatasetResponse>? = null


    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var mFinalAmount:String = "000000";
    private var gridLayoutCount = 3
    private var gridLayoutCountForCitation = 3
    private var recursiveIndex = 0
    private var mCitationNumber: String? = ""
    private var mCitationId: String? = ""
    private var mQRCodeValue: String? = ""
    private var isBarCodePrintlayout: Boolean = true
    private var isQRCodePrintlayout: Boolean = false
    private var isWarningSelected : Boolean = false
    private var isErrorOccurDuringGenerateFacsimile = false
    private var mPath: String? = ""
    private var mSignaturePath: String? = ""
    private var officerNameFormatForPrint: String? = null

    private var mIssuranceModel: CitationInsurranceDatabaseModel? =
            CitationInsurranceDatabaseModel()

    private val mFacsimileImagesViewModel: FacsimileImagesViewModel? by viewModels()
    private val addImageViewModel: AddImageViewModel? by viewModels()
    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()

    private var printcommand:StringBuilder?=null

    private var mOfficerList = java.util.ArrayList<VehicleListModel>()
    private var mVehicleList = java.util.ArrayList<VehicleListModel>()
    private var mViolationList = java.util.ArrayList<VehicleListModel>()
    private var mCitationList = java.util.ArrayList<VehicleListModel>()
    private var mCommentsList = java.util.ArrayList<VehicleListModel>()
    private var mMotoristInformationList = java.util.ArrayList<VehicleListModel>()


    private var zebraPrinterUseCase: ZebraPrinterUseCase? = null
    private var starPrinterUseCase: StarPrinterUseCase? = null
    private var xfPrinterUseCase: XfPrinterUseCase? = null

    private var bannerList = ArrayList<CitationImagesModel>()

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Nothing to implement here
            } else {
                BluetoothPermissionUtil.requestBluetoothSetting(this@MunicipalCitationReprintReuploadActivity)
            }
        }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_municipal_citation_reprint_reupload)

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
            LogUtil.printLog("==>Printer","Inside")
            starPrinterUseCase = StarPrinterUseCase()
            starPrinterUseCase?.setPrintInterfaceCallback(this)
            starPrinterUseCase?.initialize(this@MunicipalCitationReprintReuploadActivity)
        } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
            LogUtil.printLog("==>Printer","Inside")
            xfPrinterUseCase = XfPrinterUseCase(this)
            lifecycle.addObserver(xfPrinterUseCase!!)
            xfPrinterUseCase?.setPrintInterfaceCallback(this)
            xfPrinterUseCase?.initialize(this@MunicipalCitationReprintReuploadActivity)
        } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            zebraPrinterUseCase = ZebraPrinterUseCase()
            zebraPrinterUseCase?.setPrintInterfaceCallback(this)
            zebraPrinterUseCase?.initialize(
                activity = this@MunicipalCitationReprintReuploadActivity,
                contentResolver = contentResolver,
                sharedPreference = sharedPreference
            )
        }

        setFullScreenUI()
        ButterKnife.bind(this)
        printcommand = StringBuilder()
        init()

        BluetoothPermissionUtil.checkBluetoothPermissions(
            activity = this,
            permissionLauncher = bluetoothPermissionLauncher
        ) {
            //Nothing to implement here in onCreate, This will check bluetooth permission and redirect user to allow bluetooth permission, in case misssed
        }
    }

    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        addObservers()
        setToolbar()
        setLayoutVisibilityBasedOnSettingResponse()

//        getCitationNumberId()

//        //GenerateBarCode(mCitationNumberId);
//        linearLayoutMainPrint.visibility = View.GONE
//        getCitationDataFromDb()
//        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)
        ) {
            setMarginLeft(relativeLayoutPrintAmountSection, 280)
        }

        bannerList = mDb?.dbDAO?.getCitationImage() as ArrayList<CitationImagesModel>
    }

//    private fun setTopMargin(){
//        relativeLayoutPrintAmountSection.
//    }

    fun setMarginLeft(v: View, top: Int) {
        val params = v.layoutParams as MarginLayoutParams
        params.setMargins(
            params.leftMargin, top,
            params.rightMargin, params.bottomMargin
        )
    }
    private fun getCitationNumberId() {
        val intent = intent
        if (intent != null) {
            mCitationNumber = intent.getStringExtra("ticket_number")
            mCitationId = intent.getStringExtra("ticket_id")

            if(mCitationNumber!=null && !mCitationNumber.isNullOrEmpty())
            {
                mIssuranceModel = mDb!!.dbDAO!!.getCitationWithTicket(mCitationNumber)
            }

            if(mIssuranceModel!=null && !mIssuranceModel?.citationData?.ticketNumber.isNullOrEmpty())
            {
                //TODO for philli BarCode
                GenerateBarCode(mCitationNumber,isBarCodePrintlayout)
//                mCitationNumber?.let { callFacsimileImageAPI(it) }

            }else{
                mCitationNumber?.let { callFacsimileImageAPI(it) }
            }
        }
    }

    private fun addObservers() {
        mFacsimileImagesViewModel?.response?.observe(this,facsimileImageResponseObserver)
        addImageViewModel?.response?.observe(this, addImageResponseObserver)
        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mFacsimileImagesViewModel?.response?.removeObserver(facsimileImageResponseObserver)
        addImageViewModel?.response?.removeObserver(addImageResponseObserver)
        mUploadImageViewModel?.response?.removeObserver(uploadImageResponseObserver)
    }

    private val facsimileImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,DynamicAPIPath.GET_STATIC_FASCIMILE_IMAGE)
    }
    private val addImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,DynamicAPIPath.POST_ADD_IMAGE + "Image")
    }
    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,DynamicAPIPath.POST_IMAGE)
    }

    private var isOCRTextPrintlayout: Boolean = false
    private var mOCRFormatValue: String? = ""
    private fun setLayoutVisibilityBasedOnSettingResponse() {
        try {
            settingsList = ArrayList()
            ioScope.async {
                settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
                mainScope.async {
                    if (settingsList != null && settingsList!!.size > 0) {
                        for (i in settingsList!!.indices) {
                            if (settingsList!![i].type.equals("BARCODE_URL", ignoreCase = true)
                                && settingsList!![i].mValue.equals("NO", ignoreCase = true)
                            ) {
                                isBarCodePrintlayout = false
                            } else if (settingsList!![i].type.equals("QRCODE_URL", ignoreCase = true)
                                && !settingsList!![i].mValue!!.isEmpty() &&
                                !settingsList!![i].mValue.equals("NO", ignoreCase = true)
                                || settingsList!![i].type.equals("QRCODE_URL", ignoreCase = true)&&
                                settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                isQRCodePrintlayout = true
                                mQRCodeValue = settingsList!![i].mValue
                            }else if (settingsList!![i].type.equals("OCR_SCANNER", ignoreCase = true)) {
                                isOCRTextPrintlayout = true
                                mOCRFormatValue = settingsList!![i].mValue

                            }else if (settingsList!![i].type.equals("SIMILAR_CITATION_BYPASS", ignoreCase = true)&&
                                settingsList!![i].mValue.equals("YES",ignoreCase = true)) {
//                                isByPassSimilarCitation = true
                            }else if (settingsList!![i].type.equals(
                                    SETTINGS_FLAG_OFFICER_NAME_FORMAT_FOR_PRINT, ignoreCase = true)) {
                                officerNameFormatForPrint = settingsList!![i].mValue.nullSafety()
                            }
                        }
//                        GenerateBarCode(mCitationNumber, isBarCodePrintlayout)
                        if(isOCRTextPrintlayout)
                        {
                            relativeLayoutPrintPhilaSection!!.visibility = View.VISIBLE
                            relativeLayoutPrintCitationSection!!.visibility = View.VISIBLE
                            relativeLayoutPrintAmountSection!!.visibility = View.VISIBLE
                            relativeLayoutPrintStateSection!!.visibility = View.VISIBLE
                            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
                                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true)
                                )
                            {
                                relativeLayoutPrintCitationSection!!.visibility = View.INVISIBLE
                                relativeLayoutPrintAmountSection!!.visibility = View.INVISIBLE
                                relativeLayoutPrintStateSection!!.visibility = View.INVISIBLE
                                relativeLayoutPrintPhilaSection!!.visibility = View.INVISIBLE
                            }
                        }else{
                            relativeLayoutPrintPhilaSection!!.visibility = View.INVISIBLE
                            relativeLayoutPrintStateSection!!.visibility = View.INVISIBLE
                            relativeLayoutPrintCitationSection!!.visibility = View.INVISIBLE
                            relativeLayoutPrintAmountSection!!.visibility = View.INVISIBLE
                        }
                    }
                    getCitationNumberId()
//                    callSimilarAndLastSecondCheckAPI()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callFacsimileImageAPI(ticketId: String) {
//        http://{{local}}/citations-issuer/citation_similarity_check
        if (NetworkCheck.isInternetAvailable(this@MunicipalCitationReprintReuploadActivity)) {
            mFacsimileImagesViewModel!!.hitFacsimileImagesApi(ticketId)
        } else {
            LogUtil.printToastMSG(
                    this@MunicipalCitationReprintReuploadActivity,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For update profile */
    private fun callUploadImages(file: File?, num: Int) {
        if (NetworkCheck.isInternetAvailable(this@MunicipalCitationReprintReuploadActivity)) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
            val files = MultipartBody.Part.createFormData(
                    "files",
                    if (file != null) file.name else "",
                    requestFile
            )
//            val mDropdownList = arrayOf(mTicketNumber + "_" + (num + totalImageCount))
            val mDropdownList =  arrayOf(mCitationId + "_" + 1 + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
            val mRequestBodyType =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
            mUploadImageViewModel?.hitUploadImagesApi(mDropdownList, mRequestBodyType, files)
        } else {
            LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For update profile */
    private fun callUploadImagesUrl() {
        if (NetworkCheck.isInternetAvailable(this@MunicipalCitationReprintReuploadActivity)) {
            val endPoint = "$mCitationId/images"
            val addImageRequest = AddImageRequest()
            addImageRequest.images = mUploadImagesLink
            addImageViewModel!!.hitAddImagesApi(addImageRequest, endPoint)
        } else {
            LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {

                        if (tag.equals(DynamicAPIPath.GET_STATIC_FASCIMILE_IMAGE, ignoreCase = true)) {
                            val responseModelFacsimile = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ResponseFacsimileImages::class.java)


                            if (responseModelFacsimile != null && responseModelFacsimile.isSuccess.nullSafety()) {
//                                mIssuranceModel = mDb!!.dbDAO!!.getCitationWithTicket(mCitationNumberId)
                                setCitationDataOnObject(responseModelFacsimile)

                            } else {
                                dismissLoader()
                                AppUtils.showCustomAlertDialog(
                                        mContext,
                                        APIConstant.POST_CREATE_MUNICIPAL_CITATION_TICKET,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                )
                            }
                        }else if (tag.equals(DynamicAPIPath.POST_ADD_IMAGE + "Image",ignoreCase = true)) {
                            dismissLoader()

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddNotesResponse::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
                                AppUtils.showCustomAlertDialog(
                                        mContext,
                                        APIConstant.UPLOAD_IMAGE,
                                        if (responseModel.message != null) responseModel.message else getString(
                                                R.string.err_msg_something_went_wrong
                                        ),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                )
                            } else {
                                AppUtils.showCustomAlertDialog(
                                        mContext,
                                        APIConstant.UPLOAD_IMAGE,
                                        if (responseModel!!.message != null) responseModel.message else getString(
                                                R.string.err_msg_something_went_wrong
                                        ),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                )
                            }
                        }else if (tag.equals(DynamicAPIPath.POST_IMAGE, ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status.nullSafety()) {
                                    if (responseModel.data != null && responseModel.data?.size!! > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response?.links != null && responseModel.data!![0].response?.links?.size!! > 0) {
                                        mUploadImagesLink.add(responseModel.data!![0].response?.links!![0])
                                        callUploadImagesUrl()
                                    } else {
                                        AppUtils.showCustomAlertDialog(
                                                mContext,
                                                APIConstant.POST_IMAGE,
                                                getString(R.string.err_msg_something_went_wrong_imagearray),
                                                getString(R.string.alt_lbl_OK),
                                                getString(R.string.scr_btn_cancel),
                                                this
                                        )
                                    }
                                } else {
                                    dismissLoader()
                                    AppUtils.showCustomAlertDialog(
                                            mContext,
                                            APIConstant.POST_IMAGE,
                                            getString(R.string.err_msg_something_went_wrong),
                                            getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel),
                                            this
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    } catch (e: Exception) {
                        dismissLoader()
                        e.printStackTrace()
                        LogUtil.printToastMSG(this, e.message)
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
            }

            else -> {}
        }
    }

    fun setCitationDataOnObject(createTicketResponse : ResponseFacsimileImages)
    {
        val dataItem:DataItem = createTicketResponse.data!!.get(0)

        if(mIssuranceModel==null){
            mIssuranceModel = CitationInsurranceDatabaseModel()
            mIssuranceModel!!.citationData = CitationIssuranceModel()
            mIssuranceModel!!.citationData!!.location = CitationLocationModel()
            mIssuranceModel!!.citationData!!.vehicle = CitationVehicleModel()
            mIssuranceModel!!.citationData!!.voilation = CitationVoilationModel()
            mIssuranceModel!!.citationData!!.officer = CitationOfficerModel()
            mIssuranceModel!!.citationData!!.municipalCitationMotoristDetailsModel = MunicipalCitationMotoristDetailsModel()
        }
        /**
         * Location
         */
        mIssuranceModel!!.citationData!!.location!!.spaceName =dataItem.location?.spaceId.nullSafety()
        mIssuranceModel!!.citationData!!.location!!.street =dataItem.location?.street.nullSafety()
        mIssuranceModel!!.citationData!!.location!!.block =dataItem.location?.block.nullSafety()
        mIssuranceModel!!.citationData!!.location!!.direction =dataItem.location?.direction.nullSafety()
        mIssuranceModel!!.citationData!!.location!!.lot =dataItem.location?.lot.nullSafety()
        mIssuranceModel!!.citationData!!.location!!.side =dataItem.location?.side.nullSafety()
        mIssuranceModel!!.citationData!!.location!!.meterName =dataItem.location?.meter.nullSafety()


        /**
         * Vehicle
         */
        mIssuranceModel!!.citationData!!.vehicle!!.vinNumber =dataItem.vehicleDetails?.vinNumber.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.decalNumber =dataItem.vehicleDetails?.decalNumber.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.bodyStyle =dataItem.vehicleDetails?.bodyStyle.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.color =dataItem.vehicleDetails?.color.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.colorCodeFullName =dataItem.vehicleDetails?.tempColorFull.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.licensePlate =dataItem.vehicleDetails?.lpNumber.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.bodyStyleDescription =dataItem.vehicleDetails?.bodyStyle.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.bodyStyle =dataItem.vehicleDetails?.bodyStyle.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.expiration =dataItem.vehicleDetails?.licenseExpiry.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.make =dataItem.vehicleDetails?.make.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.makeFullName =dataItem.vehicleDetails?.tempMakeFull.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.model =dataItem.vehicleDetails?.model.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.state =dataItem.vehicleDetails?.state.nullSafety()
        mIssuranceModel!!.citationData!!.vehicle!!.decalYear =dataItem.vehicleDetails?.decalYear.nullSafety()

        /*Motorist Information*/
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstName = dataItem.motoristDetails?.motoristFirstName.nullSafety()
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleName = dataItem.motoristDetails?.motoristMiddleName.nullSafety()
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristLastName = dataItem.motoristDetails?.motoristLastName.nullSafety()
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirth = dataItem.motoristDetails?.motoristDateOfBirth.nullSafety()
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumber = dataItem.motoristDetails?.motoristDlNumber.nullSafety()

        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlock = dataItem.motoristDetails?.motoristAddressBlock.nullSafety()
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreet = dataItem.motoristDetails?.motoristAddressStreet.nullSafety()
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCity = dataItem.motoristDetails?.motoristAddressCity.nullSafety()
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressState = dataItem.motoristDetails?.motoristAddressState.nullSafety()
        mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZip = dataItem.motoristDetails?.motoristAddressZip.nullSafety()

        /**
         * Violation
         */
        mIssuranceModel!!.citationData!!.voilation!!.amount = dataItem.violationDetails?.fine.toString()
//        mIssuranceModel!!.citationData!!.voilation!!.mLateFineDays =dataItem!!.violationDetails!!.lateFine
        mIssuranceModel?.citationData?.voilation?.amountDueDate = dataItem.violationDetails?.lateFine.toString()
        mIssuranceModel!!.citationData!!.voilation!!.code =dataItem.violationDetails?.code.nullSafety()
        mIssuranceModel!!.citationData!!.voilation!!.locationDescr =dataItem.violationDetails?.description.nullSafety()
        mIssuranceModel!!.citationData!!.voilation!!.violationCode =dataItem.violationDetails?.violation.nullSafety()

        /**
         * REMARK
         */
        mIssuranceModel?.citationData?.locationRemarks = dataItem.commentDetails?.remark1.nullSafety()
        mIssuranceModel?.citationData?.locationRemarks1 = dataItem.commentDetails?.remark2.nullSafety()
        mIssuranceModel?.citationData?.locationNotes = dataItem.commentDetails?.note1.nullSafety()
        mIssuranceModel?.citationData?.locationNotes1 = dataItem.commentDetails?.note2.nullSafety()
        /**
         * OFFICER
         */
        mIssuranceModel?.citationData?.officer!!.agency = dataItem.officerDetails?.agency.nullSafety()
        mIssuranceModel?.citationData?.officer!!.badgeId = dataItem.officerDetails?.badgeId.nullSafety()
        mIssuranceModel?.citationData?.officer!!.beat = dataItem.officerDetails?.beat.nullSafety()
        mIssuranceModel?.citationData?.officer!!.observationTime = dataItem.timeLimitEnforcementObservedTime.nullSafety()
        mIssuranceModel?.citationData?.officer!!.officerDetails = dataItem.officerDetails?.peoName.nullSafety()
        mIssuranceModel?.citationData?.officer!!.officerId = dataItem.officerDetails?.badgeId.nullSafety()
        mIssuranceModel?.citationData?.officer!!.shift = dataItem.officerDetails?.shift?.nullSafety()
        mIssuranceModel?.citationData?.ticketDatePrint = AppUtils.dateFormateForFacsimil(dataItem.headerDetails?.timestamp.nullSafety())
        mIssuranceModel?.citationData?.ticketNumber = dataItem.headerDetails?.citationNumber.nullSafety()
        val type = dataItem!!.type!!.split(",").toTypedArray()
        if(type.size==1) {
            mIssuranceModel?.citationData?.ticketType = type[0].nullSafety()
        }else if(type.size==2) {
            mIssuranceModel?.citationData?.ticketType = type[0].nullSafety()
            mIssuranceModel?.citationData?.ticketType2 = type[1].nullSafety()
        }else if(type.size==3) {
            mIssuranceModel?.citationData?.ticketType = type[0].nullSafety()
            mIssuranceModel?.citationData?.ticketType2 = type[1].nullSafety()
            mIssuranceModel?.citationData?.ticketType3 = type[2].nullSafety()
        }
//        readCSVFile();
        //TODO for philli BarCode
        GenerateBarCode(dataItem!!.headerDetails!!.citationNumber,isBarCodePrintlayout)
//        moveNextScreen()
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
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)) {
//                        codeWriter.encode(ticketId, BarcodeFormat.CODE_128, 560, 44, hintMap)
                        codeWriter.encode(ticketId, BarcodeFormat.CODE_128, 240, 44, hintMap)
                    }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)){
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
                    linearLayoutCompatBarCode = this@MunicipalCitationReprintReuploadActivity.layoutInflater.inflate(
                            R.layout.content_print_bar_code_layout,
                            null
                    ) as LinearLayoutCompat
                    mImageViewBarcodePrint = linearLayoutCompatBarCode!!.findViewById(R.id.ivBarcodePrint)
                    mImageViewBarcodePrint!!.visibility = View.GONE
                    val mLinerlayoutBarcodePrint: LinearLayoutCompat? = linearLayoutCompatBarCode!!.findViewById(R.id.llBarcodePrint)
                    if (isPrint) {
                        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)) {
                            mImageViewBarcodePrint!!.visibility = View.INVISIBLE
                            mImageViewBarcodePrint?.setImageBitmap(bitmap)
                            mLinerlayoutBarcodePrint!!.visibility = View.VISIBLE
//                        val dr: Drawable = BitmapDrawable(bitmap)
//                        mLinerlayoutBarcodePrint!!.setBackground(dr)
                        }else{
                            mImageViewBarcodePrint!!.visibility = View.VISIBLE
                            mLinerlayoutBarcodePrint!!.visibility = View.VISIBLE
                            mImageViewBarcodePrint?.setImageBitmap(bitmap)
                            val dr: Drawable = BitmapDrawable(bitmap)
                            mLinerlayoutBarcodePrint!!.setBackground(dr)
                        }
                    } else {
//                        mImageViewBarcode.setImageBitmap(bitmap)
                    }
                    createViewToPrint()
                }

        } catch (e: Exception) {
            LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, e.message)
        }
    }


//    private fun GenerateQRCodeInnovaPrint(ticketId: String, imageView: AppCompatImageView) {
//        //https://learningprogramming.net/mobile/android/create-and-scan-barcode-in-android/
//        try {
//
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
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)||
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)||
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
//                    if((AppUtils.getDeviceTypePhoneOrTablet(this@ReprintReuploadActivity)).equals("TABLET"))
//                    {
//                        dimen = dimen * 2 / 20
//                    }else {
//                        dimen = dimen * 2 / 12
//                    }
//                }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true))
//
//                {
//                    dimen = dimen * 2 / 12
//                }
//                else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true))
//                {
//                    dimen = dimen * 2 / 15
//                }
//                else {
//                    dimen = dimen * 2 / 10
//                }
//
//                // setting this dimensions inside our qr code
//                // encoder to generate our qr code.
//                //val qrgEncoder = QRGEncoder(ticketId, null, QRGContents.Type.TEXT, dimen)
//                try {
//                    // getting our qrcode in the form of bitmap.
//                   // val bitmap = qrgEncoder.encodeAsBitmap()
//
//                    val bitmapQrCode = if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)){
//                        QRCodeUtils.getQRCode(ticketId, dimen, .0f)
//                    }else{
//                        QRCodeUtils.getQRCode(ticketId, dimen, .3f)
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
//            LogUtil.printToastMSG(this@ReprintReuploadActivity, e.message)
//        }
//    }

    private fun GenerateQRCodeInnovaPrint(ticketId: String, imageView: AppCompatImageView) {
        //https://learningprogramming.net/mobile/android/create-and-scan-barcode-in-android/
        try {
            val finalQR = QRCodeUtils.getFinalQRCodeValue(
                ticketId,
                mCitationNumber.nullSafety(),
                mIssuranceModel?.citationData?.vehicle?.licensePlate.nullSafety(),
                mIssuranceModel?.citationData?.vehicle?.state.nullSafety()
            )
            AppUtils.mFinalQRCodeValue = finalQR

            try {
                // getting our qrcode in the form of bitmap
                // val bitmapQrCode = qrgEncoder.encodeAsBitmap()
                val bitmapQrCode =
                    QRCodeUtils.generateQRCodeForPrint(this@MunicipalCitationReprintReuploadActivity, finalQR)

                mainScope.async {
                    imageView.setImageBitmap(bitmapQrCode)
                }
            } catch (e: WriterException) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            printToastMSG(this@MunicipalCitationReprintReuploadActivity, e.message)
        }
    }

    private fun createViewToPrint() {
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

            val linearLayoutCompatMotoristInformation = this.layoutInflater.inflate(
                R.layout.content_print_motorist_information,null) as LinearLayoutCompat

            var appComTextViewPrintUrl: AppCompatTextView? = null
            var appCompatImageViewPrintUrl: AppCompatImageView? = null
            var appCompatImageViewLprImage: AppCompatImageView? = null
            var linearLayoutCompatPrintUrl: LinearLayoutCompat? = null
            var appComTextViewQRCodeLabel: AppCompatTextView? = null

            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ) {
                appComTextViewPrintUrl =
                    linearLayoutCompatRemark!!.findViewById(R.id.text_print_url)
                appCompatImageViewPrintUrl =
                    linearLayoutCompatRemark!!.findViewById(R.id.iv_print_url)
                appCompatImageViewLprImage =
                    linearLayoutCompatRemark!!.findViewById(R.id.iv_lprImage)
                linearLayoutCompatPrintUrl =
                    linearLayoutCompatRemark!!.findViewById(R.id.ll_qr_bottomview)
                appComTextViewQRCodeLabel =
                    linearLayoutCompatRemark!!.findViewById(R.id.text_qr_code_label)
            }else {
                appComTextViewPrintUrl = linearLayoutCompatBarCode!!.findViewById(R.id.text_print_url)
                appCompatImageViewPrintUrl =
                    linearLayoutCompatBarCode!!.findViewById(R.id.iv_print_url)
                appCompatImageViewLprImage =
                    linearLayoutCompatBarCode!!.findViewById(R.id.iv_lprImage)
                linearLayoutCompatPrintUrl =
                    linearLayoutCompatBarCode!!.findViewById(R.id.ll_qr_bottomview)
                appComTextViewQRCodeLabel =
                    linearLayoutCompatBarCode!!.findViewById(R.id.text_qr_code_label)
            }
//            var appComTextViewPrintUrl: AppCompatTextView = linearLayoutCompatBarCode!!.findViewById(R.id.text_print_url)
//            var appCompatImageViewPrintUrl: AppCompatImageView = linearLayoutCompatBarCode!!.findViewById(R.id.iv_print_url)
//            var linearLayoutCompatPrintUrl: LinearLayoutCompat = linearLayoutCompatBarCode!!.findViewById(R.id.ll_qr_bottomview)


            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true)
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
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true)
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
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true)) {

                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)||
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
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)||
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
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)
                    ){

                    appCompatTextViewTopMessage.visibility = View.VISIBLE
//                     appCompatTextViewTopMessage!!.text = "PENALTY ASSESSMENT NOTICE"
                }
                var lines = sharedPreference.read(SharedPrefKey.INNOVA_BOTTOM_URL_FOR_PRINT, "")?.replace("#", "\n")
//                lines = " https://follybeach-payment-staging.netlify.app/[ticket_no]"
                appComTextViewPrintUrl.text = lines.toString().replace("[ticket_no]",mCitationNumber.toString())
//                if (appComTextViewPrintUrl!!.text!!.toString().length < 1) {
//                    linearLayoutCompatPrintUrl.visibility = View.GONE
//                }
//                 isQRCodePrintlayout= true
                if (isQRCodePrintlayout) {
                    appComTextViewPrintUrl.visibility = View.VISIBLE
                    linearLayoutCompatPrintUrl.visibility = View.VISIBLE
                    appCompatImageViewPrintUrl.visibility = View.VISIBLE
                    mQRCodeValue?.let { GenerateQRCodeInnovaPrint(it, appCompatImageViewPrintUrl) }
//                    appCompatImageViewPrintUrl!!.setImageBitmap(qrCodePrintBitmap)

                    val qrCodeLabel = sharedPreference.read(
                        SharedPrefKey.QRCODE_LABEL_FOR_PRINT,"").nullSafety()
                    if (qrCodeLabel.isNotEmpty()){
                        appComTextViewQRCodeLabel.showView()
                        appComTextViewQRCodeLabel.text = qrCodeLabel
                    }else{
                        appComTextViewQRCodeLabel.hideView()
                    }
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
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)||
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
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)||
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
            } else {
                appComTextViewPrintUrl.visibility = View.GONE
                linearLayoutCompatPrintUrl.visibility = View.GONE
            }

            if (appComTextViewPrintUrl.text.isNullOrEmpty()){
                appComTextViewPrintUrl.hideView()
            }


            try {
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)) {
                    val bannerList =
                        mDb?.dbDAO?.getCitationImage() as ArrayList<CitationImagesModel>
//            val destFile = File(
//                Environment.getExternalStorageDirectory().absolutePath,
//                Constants.FILE_NAME + Constants.LPRSCANIMAGES)
//            val mLprNumber = mIssuranceModel?.citationData?.vehicle!!.licensePlate!!
//            val imageName = "anpr_$mLprNumber.jpg"
//            val mCopyLprScanPath =  destFile!!.absolutePath + "/" + imageName
//            val imgFileCopy = mCopyLprScanPath?.let { File(it) }
                    if (bannerList!!.get(0)!!.citationImage!!.contains("anpr_")) {
                        val imgFileCopy = bannerList!!.get(0)!!.citationImage?.let { File(it) }
                        if (imgFileCopy != null && imgFileCopy!!.exists()) {
                            appCompatImageViewLprImage.setImageURI(Uri.fromFile(imgFileCopy))
                        }
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
                        val citation = CitationPrintSectionUtils.getTicketNumber(mIssuranceModel,Constants.MUNICIPAL_ACTIVITY)

                        val orderNumber =
                            if (mIssuranceModel?.citationData?.mPrintLayoutOrderTickerNumber != null) mIssuranceModel?.citationData?.mPrintLayoutOrderTickerNumber else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                        val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put("CITATION", sequence)// Layout order
                        mPrintLayoutTitle[sequence] = layoutSectionTitle
                        citation.mSectionHeader = layoutSectionTitle
//                        printcommand = AppUtils.setXYforPrint(citation,sequence,"citation",printcommand!!)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(citation)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.isStatus_ticket_date.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.ticketDate.nullSafety())) {
                        try{
                            val (citation, citationTime) = CitationPrintSectionUtils.getTicketDate(this@MunicipalCitationReprintReuploadActivity, mIssuranceModel)

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
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)&&
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
                            } else if (sequence == 6) {
                                mMotoristInformationList.add(citation)
                                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)&&
                                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
                                    mMotoristInformationList.add(citationTime)
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
                                } else if (sequence == 6) {
                                    mMotoristInformationList.add(citation)
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
                                } else if (sequence == 6) {
                                    mMotoristInformationList.add(citation)
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
                            } else if (sequence == 6) {
                                mMotoristInformationList.add(citation)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(citation)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(citation)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(citation)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (mIssuranceModel?.citationData?.isStatus_ticket_type.nullSafety()) {
                    //                if (!TextUtils.isEmpty(mIssuranceModel.getCitationData().getTicketType())) {

                    val (isWarningSelected, citation) = CitationPrintSectionUtils.getTicketType(this@MunicipalCitationReprintReuploadActivity,mIssuranceModel)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(citation)
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
                                layoutSectionTitle.contentEquals("OFFICER DETAIL", ignoreCase = true)) {
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
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
                            } else if (sequence == 6) {
                                mMotoristInformationList.add(data)
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
                            } else if (sequence == 6) {
                                mMotoristInformationList.add(data)
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
                            } else if (sequence == 6) {
                                mMotoristInformationList.add(data)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(data)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /****
             * MOTORIST INFORMATION
             * TODO MOTORIST INFORMATION PRINT SECTION
             */
            try{
                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristFirstName.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstName.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristFirstName(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristFirstName != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristFirstName.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()
                        val layoutSectionTitle = orderNumber.getSectionTitle()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order
                        mPrintLayoutTitle[sequence] = layoutSectionTitle.toString()
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }

                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristMiddleName.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleName.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristMiddleName(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristMiddleName != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristMiddleName.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristLastName.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristLastName.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristLastName(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristLastName != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristLastName.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristDateOfBirth.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirth.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristDateOfBirth(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristDateOfBirth != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristDateOfBirth.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristDlNumber.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumber.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristDlNumber(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristDlNumber != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristDlNumber.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristAddressBlock.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlock.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristAddressBlock(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressBlock != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressBlock.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristAddressStreet.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreet.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristAddressStreet(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressStreet != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressStreet.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristAddressCity.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCity.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristAddressCity(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressCity != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressCity.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristAddressState.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressState.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristAddressState(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressState != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressState.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
                    }
                }

                if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.isStatusMotoristAddressZip.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZip.nullSafety())) {
                        val data = CitationPrintSectionUtils.getMotoristAddressZip(mIssuranceModel)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressZip != null) mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintLayoutOrderMotoristAddressZip.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
                        val firstPosition = orderNumber.getPrintOrder()
                        val sequence = firstPosition.toInt()

                        mPrintLayoutMap.put(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION, sequence)// Layout order

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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
                        }
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

            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)) {
                linearLayoutCompatSignature =
                    linearLayoutCompatOfficer.findViewById(R.id.ll_signature)
                appCompatImageViewSignature =
                    linearLayoutCompatOfficer.findViewById(R.id.imgSignature_print)
            }else{
                 linearLayoutCompatSignature =
                    linearLayoutCompatRemark.findViewById(R.id.ll_signature)
                 appCompatImageViewSignature =
                    linearLayoutCompatRemark.findViewById(R.id.imgSignature_print)
            }


            val appCompatTextViewCommentsTitle: AppCompatTextView =
                linearLayoutCompatRemark.findViewById(R.id.textview_comments_title)
            try { //**Signature**/
                linearLayoutCompatSignature.visibility = View.GONE
//                val file = File(mPath)
//                if (!TextUtils.isEmpty(mPath) && file.exists()) {
//                    mImageViewSignature.visibility = View.VISIBLE
//                    mTextViewSignName.visibility = View.VISIBLE
                val signaturePath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")

                mPath = signaturePath + Constants.CAMERA + "/" + getSignatureFileNameWithExt()
                if (!TextUtils.isEmpty(mPath) && (File(mPath)).exists()) {
                    appCompatImageViewSignature.setImageURI(Uri.fromFile(File(mPath)))
                }
//                }
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
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
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                        var value = mIssuranceModel?.citationData?.mPrintOrderTicketDate.nullSafety()
                        value += 1.0
                        val dataExtraSpace = VehicleListModel()
                        dataExtraSpace.offNameFirst = " "
                        dataExtraSpace.offTypeFirst = " "
                        dataExtraSpace.mPrintOrder = value
                        mPrintLayoutMap.put("COMMENT", sequence)// Layout order
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
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
                                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)) {
//                                mCommentsList.add(dataExtraSpace)
                            }
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(data)
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
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
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
            } else {
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
                val (orderNumber, location) = CitationPrintSectionUtils.getLocation(this@MunicipalCitationReprintReuploadActivity, sharedPreference, mLocation, mIssuranceModel)

                val firstPosition = orderNumber!!.split("#").toTypedArray()[0]
                val sequence = firstPosition.toInt()


                if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)&&
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
                    val layoutSectionTitle:String = orderNumber.split("#").toTypedArray()[1]
                    if(layoutSectionTitle.contains("LOCATION",ignoreCase = true)) {
                        mPrintLayoutMap.put("LOCATION", sequence)// Layout order
                    }
                    mPrintLayoutTitle[sequence] = layoutSectionTitle
                    location.mSectionHeader = layoutSectionTitle
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
                } else if (sequence == 6) {
                    mMotoristInformationList.add(location)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(location)
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
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(location)
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
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)
                && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK,ignoreCase = true)) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.lot.nullSafety())) {
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(location)
                    }
                }
            }

//            if (mIssuranceModel?.citationData?.location?.isStatus_block.nullSafety()
//                && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)) {
//                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.block.nullSafety())) {
//                    val block = CitationPrintSectionUtils.getBlock(mIssuranceModel)
//                    val orderNumber =
//                            if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderblock != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderblock.nullSafety() else Constants.PRINT_LAYOUT_ORDER_SPARATER
//                    val firstPosition = orderNumber.split("#").toTypedArray()[0]
////                    val layoutSectionTitle = orderNumber.split("#").toTypedArray()[1]
//                    val sequence = firstPosition.toInt()
//
//
//                    printcommand = AppUtils.setXYforPrint(block,sequence,"location",printcommand!!)
////                    location.type = mIssuranceModel?.citationData?.location?.lotColumn!!.toInt()
//                    if (sequence == 1) {
//                        mCitationList.add(block)
//                    } else if (sequence == 2) {
//                        mVehicleList.add(block)
//                    } else if (sequence == 3) {
//                        mViolationList.add(block)
//                    } else if (sequence == 4) {
//                        mOfficerList.add(block)
//                    } else if (sequence == 5) {
//                        mCommentsList.add(block)
//                    }
//                }
//            }

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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(location)
                    }
                }
            }

            if (mIssuranceModel?.citationData?.location?.isStatus_CityZone.nullSafety()) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.cityZone)) {
                    val data = CitationPrintSectionUtils.getCityZone(this@MunicipalCitationReprintReuploadActivity,mIssuranceModel)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(data)
                    }
                }
            }
            if (mIssuranceModel?.citationData?.location?.isStatus_PcbZone.nullSafety()) {
                if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.location?.pcbZone.nullSafety())) {
                    val data = CitationPrintSectionUtils.getPCBZone(this@MunicipalCitationReprintReuploadActivity,mIssuranceModel)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(data)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        Vehdata.type = 0
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.vehicle?.isStatus_body_style.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.vehicle?.bodyStyle.nullSafety())||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true)||
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(Vehdata)
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
                        }else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                    } else if (sequence == 6) {
                        mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        val Vehdata = CitationPrintSectionUtils.getViolationCode(this@MunicipalCitationReprintReuploadActivity,mIssuranceModel)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
                        }
                    }
                }
                if (mIssuranceModel?.citationData?.voilation?.isStatus_amount.nullSafety()) {
                    if (!TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.amount.nullSafety())) {
                        val Vehdata = CitationPrintSectionUtils.getViolationAmount(mIssuranceModel,Constants.MUNICIPAL_ACTIVITY)
                        val orderNumber =
                            if (mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderAmount != null) mIssuranceModel?.citationData?.voilation?.mPrintLayoutOrderAmount else Constants.PRINT_LAYOUT_ORDER_SPARATER
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
                        }
                    }
                }

                //Due Date: If Paid After
                if (mIssuranceModel?.citationData?.voilation?.isStatus_amount_due_date.nullSafety()) {
                    if (mIssuranceModel?.citationData?.voilation?.amountDueDate!=null && !TextUtils.isEmpty(mIssuranceModel?.citationData?.voilation?.amountDueDate.nullSafety()) &&
                        !mIssuranceModel?.citationData?.voilation?.amountDueDate!!.equals("0.0")) {
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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
                        } else if (sequence == 6) {
                            mMotoristInformationList.add(Vehdata)
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

                Collections.sort(mMotoristInformationList, object : Comparator<VehicleListModel?> {
                    override fun compare(u1: VehicleListModel?, u2: VehicleListModel?): Int {
                        return java.lang.Double.compare(u1?.mPrintOrder.nullSafety(),
                            u2?.mPrintOrder.nullSafety())
                    }
                })

                if (
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)&&
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
                        Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)
                    ) {
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
                            Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true
                        )

                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true
                        )
                    ) {
                        mOfficerList =
                            AppUtils.checkPrintLayoutOrderForTwoColumn(
                                mOfficerList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mViolationList =
                            AppUtils.checkPrintLayoutOrderForTwoColumn(
                                mViolationList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mVehicleList =
                            AppUtils.checkPrintLayoutOrderForTwoColumn(
                                mVehicleList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mCitationList =
                            AppUtils.checkPrintLayoutOrderForTwoColumn(
                                mCitationList,
                                ""
                            ) as ArrayList<VehicleListModel>

                        mMotoristInformationList =
                            checkPrintLayoutOrderForTwoColumn(
                                mMotoristInformationList,
                                ""
                            ) as ArrayList<VehicleListModel>

                        if (mCommentsList != null && mCommentsList.size > 0)
                            mCommentsList = AppUtils.checkPrintLayoutOrderForTwoColumn(
                                mCommentsList, "Comment"
                            ) as ArrayList<VehicleListModel>


                    } else {
                        mOfficerList =
                            AppUtils.checkPrintLayoutOrder(
                                mOfficerList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mViolationList =
                            AppUtils.checkPrintLayoutOrder(
                                mViolationList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mVehicleList =
                            AppUtils.checkPrintLayoutOrder(
                                mVehicleList,
                                ""
                            ) as ArrayList<VehicleListModel>
                        mCitationList =
                            AppUtils.checkPrintLayoutOrder(
                                mCitationList,
                                ""
                            ) as ArrayList<VehicleListModel>

                        mMotoristInformationList =
                            checkPrintLayoutOrder(mMotoristInformationList, "") as ArrayList<VehicleListModel>

                        if (mCommentsList != null && mCommentsList.size > 0)
                            if (BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_OCEANCITY,
                                    ignoreCase = true
                                )
                                || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_EPHRATA,
                                    ignoreCase = true
                                )
                                || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_Easton,
                                    ignoreCase = true
                                )
                                || BuildConfig.FLAVOR.equals(
                                    Constants.FLAVOR_TYPE_CLIFTON,
                                    ignoreCase = true
                                )
                            ) {
                                mCommentsList = AppUtils.checkPrintLayoutOrder(
                                    mCommentsList, "Comment"
                                ) as ArrayList<VehicleListModel>
                            } else {
                                mCommentsList = AppUtils.checkPrintLayoutOrderComment(
                                    mCommentsList, "Comment"
                                ) as ArrayList<VehicleListModel>
                            }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            generateFacsimileImage()

        }
    }

    /** 1 mCitationList
     * 2  mVehicleList
     * 3  mViolationList
     * 4 mOfficerList
     * 5 mCommentsList */
    private fun generateFacsimileImage() {

        linearLayoutMainPrint.visibility = View.VISIBLE
        linearLayoutCompatChildContainer.removeAllViews();

        if (LogUtil.isEnableCommandBasedFacsimile) {
            ivCommandBasedFacsimile.showView()
            linearLayoutCompatChildContainer.invisibleView()
        }

        val linearLayoutCompatVehicle = this.layoutInflater.inflate(
                R.layout.content_print_vehicle,
                null
        ) as LinearLayoutCompat
        val linearLayoutCompatCitation = this.layoutInflater.inflate(
                R.layout.content_print_citation_layout,
                null
        ) as LinearLayoutCompat
        val linearLayoutCompatOfficer = this.layoutInflater.inflate(
                R.layout.content_print_officer_details,
                null
        ) as LinearLayoutCompat
        val linearLayoutCompatRemark = this.layoutInflater.inflate(
                R.layout.content_print_remark_notes_signature,
                null
        ) as LinearLayoutCompat
        val linearLayoutCompatViolation = this.layoutInflater.inflate(
                R.layout.content_print_violation,
                null
        ) as LinearLayoutCompat

        val linearLayoutCompatMotoristInformation = this.layoutInflater.inflate(
            R.layout.content_print_motorist_information, null) as LinearLayoutCompat


        var appComTextViewPrintUrl: AppCompatTextView? = null
        var appCompatImageViewPrintUrl: AppCompatImageView? = null
        var appCompatImageViewLprImage: AppCompatImageView? = null
        var linearLayoutCompatPrintUrl: LinearLayoutCompat? = null
        var linearLayoutCompatQrCodeAndMessage: LinearLayoutCompat? = null
        var appComTextViewQRCodeLabel: AppCompatTextView? = null

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)) {
            appComTextViewPrintUrl =
                linearLayoutCompatRemark!!.findViewById(R.id.text_print_url)
            appCompatImageViewPrintUrl =
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
            appCompatImageViewPrintUrl =
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

        val appCompatTextViewMotoristInformationTitle: AppCompatTextView =
            linearLayoutCompatMotoristInformation.findViewById(R.id.textview_motorist_information_title)


        var linearLayoutCompatSignature: LinearLayoutCompat? = null
//                linearLayoutCompatRemark.findViewById(R.id.ll_signature)
        var appCompatImageViewSignature: AppCompatImageView? = null
//                linearLayoutCompatRemark.findViewById(R.id.imgSignature_print)
        val appCompatTextViewCommentsTitle: AppCompatTextView =
                linearLayoutCompatRemark.findViewById(R.id.textview_comments_title)
        val appCompatTextViewOfficerTitle: AppCompatTextView =
                linearLayoutCompatOfficer.findViewById(R.id.textview_officer_title)

        var linearLayoutCompatWarnin:LinearLayoutCompat?= null
        var appCompatTextViewCommentsWarningValue: AppCompatTextView? = null
        var appCompatTextViewCommentsWarningLable: AppCompatTextView? = null

        var linearLayoutCompatFooter3:LinearLayoutCompat?= null
        var appCompatTextViewCommentsFooter3Value: AppCompatTextView? = null
        var appCompatTextViewCommentsFooter3Lable: AppCompatTextView? = null

        try { //**Signature**/

            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
                linearLayoutCompatSignature =
                    linearLayoutCompatCitation.findViewById(R.id.ll_signature)
                appCompatImageViewSignature =
                    linearLayoutCompatCitation.findViewById(R.id.imgSignature_print)
            }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)){
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

            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)){
                linearLayoutCompatSignature!!.visibility = View.VISIBLE
                mSignaturePath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
                mSignaturePath = mSignaturePath + Constants.CAMERA + "/" + getSignatureFileNameWithExt()
                val file = File(mSignaturePath)

                if (!TextUtils.isEmpty(mSignaturePath) && file.exists()) {
                    appCompatImageViewSignature!!.visibility = View.VISIBLE
//                    mTextViewSignName.visibility = View.VISIBLE
                    appCompatImageViewSignature!!.setImageURI(Uri.fromFile(File(mSignaturePath)))
                }
            }else{
                linearLayoutCompatSignature!!.visibility = View.GONE
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (isQRCodePrintlayout) {
            appComTextViewPrintUrl.visibility = View.VISIBLE
            linearLayoutCompatPrintUrl.visibility = View.VISIBLE
            appCompatImageViewPrintUrl.visibility = View.VISIBLE
            mQRCodeValue?.let { GenerateQRCodeInnovaPrint(it, appCompatImageViewPrintUrl) }
//                    appCompatImageViewPrintUrl!!.setImageBitmap(qrCodePrintBitmap)

            val qrCodeLabel = sharedPreference.read(
                SharedPrefKey.QRCODE_LABEL_FOR_PRINT,"").nullSafety()
            if (qrCodeLabel.isNotEmpty()){
                appComTextViewQRCodeLabel.showView()
                appComTextViewQRCodeLabel.text = qrCodeLabel
            }else{
                appComTextViewQRCodeLabel.hideView()
            }
        } else {
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
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)||
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
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)||
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

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true))
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
                linearLayoutCompatWarnin!!.visibility = View.GONE
            }

        }

        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
            ) {
            linearLayoutCompatWarnin!!.visibility = View.VISIBLE
            linearLayoutCompatFooter3!!.visibility = View.VISIBLE
            appCompatTextViewCommentsWarningLable!!.text = sharedPreference.read(
                SharedPrefKey.FOOTER_LABEL_FOR_PRINT,"").toString().replace("#","\n")

            appCompatTextViewCommentsWarningValue!!.text = sharedPreference.read(
                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT,"").toString().replace("#","\n")

            appCompatTextViewCommentsFooter3Lable!!.text = sharedPreference.read(
                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT,"").toString().replace("#","\n")

            appCompatTextViewCommentsFooter3Value!!.text = sharedPreference.read(
                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT,"").toString().replace("#","\n")
        }


        try {
            if(isPrintLprImageInFacsimilePrint(getMyDatabase()!!)){
                val bannerList = mDb?.dbDAO?.getCitationImage() as ArrayList<CitationImagesModel>
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

//        try {
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)) {
//                val bannerList = mDb?.dbDAO?.getCitationImage() as ArrayList<CitationImagesModel>
//                if (bannerList!!.get(0)!!.citationImage!!.contains("anpr_")) {
//                    val imgFileCopy = bannerList!!.get(0)!!.citationImage?.let { File(it) }
//                    if (imgFileCopy != null && imgFileCopy!!.exists()) {
//                        linearLayoutCompatPrintUrl.visibility = View.VISIBLE
//                        appCompatImageViewLprImage.setImageURI(Uri.fromFile(imgFileCopy))
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        /** 1 mCitationList
         * 2  mVehicleList
         * 3  mViolationList
         * 4 mOfficerList
         * 5 mCommentsList */
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STRATOS, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA, ignoreCase = true)
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
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true)
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
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, true)
            || BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true)) {

//            relativeLayoutPrintAmountSection!!.visibility = View.INVISIBLE
//            relativeLayoutPrintCitationSection!!.visibility = View.INVISIBLE
//            relativeLayoutPrintPhilaSection!!.visibility = View.INVISIBLE

            val linearLayoutCompatBottomMargin = this.layoutInflater.inflate(
                    R.layout.content_print_location_details,
                    null
            ) as LinearLayoutCompat
//                linearLayoutCompatChildContainer.addView(linearLayoutCompatBottomMargin)
            setDuncanSiteId()
            if (isOCRTextPrintlayout) {
//            if (true) {
                setOCRTextValueOnView()
            }

//           linearLayoutCompatChildContainer.addView(linearLayoutCompatBottomMargin)

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)){
                setDuncanSiteId()
                if (isOCRTextPrintlayout) {
                    setTextViewValueForVallejo()
                }

            }

//            Log.i("vehicle",mVehicleList.size.toString() +"  --  "+ mViolationList.size)
//                mPrintLayoutMap!!.forEach { key, value -> println("$key = $value") }
            // Sort the list
//                linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
            /**
            Height is fixed for comment size comment is selected or not
             */
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)){
                mPrintLayoutMap.put("COMMENT", 5)// Layout order
            }
            /** 1 mCitationList
             * 2  mVehicleList
             * 3  mViolationList
             * 4 mOfficerList
             * 5 mCommentsList */
            // Sort the list
            try {
                val resultd = mPrintLayoutMap.toList().sortedBy { (_, value) -> value }.toMap()
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
//            Log.i("vehicle", mPrintLayoutMap.get(duplicateKey).toString())
                mPrintLayoutMap.remove(duplicateKey);
            }catch(e:Exception)
            {
                e.printStackTrace()
            }
            val result = mPrintLayoutMap.toList().sortedBy { (_, value) -> value }.toMap()
//            val myHandler = Handler()
//            val itr = result.keys.iterator()

             if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true)||
                 BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true)||
                 BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true) ||
                 BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true) ||
                 BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true) ||
                 BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, true) ||
                 BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)) {
                ////No need to add top margin for these sites
            }else {
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBottomMargin);
            }
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)){
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
            }
            printLayoutRecursive(result as HashMap<String, Int>, linearLayoutCompatCitation,
                    linearLayoutCompatViolation, linearLayoutCompatVehicle, linearLayoutCompatRemark,
                    linearLayoutCompatOfficer,linearLayoutCompatMotoristInformation, appCompatTextViewCitaitonTitle, appCompatTextViewViolationTitle,
                    appCompatTextViewVehileTitle, appCompatTextViewCommentsTitle, appCompatTextViewOfficerTitle, appCompatTextViewMotoristInformationTitle
            )

        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)||
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
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mViolationList.size > 0) {
                        setAdapterForViolationList(mViolationList, linearLayoutCompatViolation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }

            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mMotoristInformationList.size > 0) {
                        setAdapterForMotoristInformationList(mMotoristInformationList, linearLayoutCompatMotoristInformation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }

            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mVehicleList.size > 0) {
                        setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mOfficerList.size > 0) {
                        setAdapterForOfficerList(mOfficerList, linearLayoutCompatOfficer)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
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
                appCompatTextViewMotoristInformationTitle.text = mPrintLayoutTitle[6]
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }

            setDuncanSiteId()
            if (isOCRTextPrintlayout) {
                setOCRTextValueOnView()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                linearLayoutCompatChildContainer.addView(appCompatTextViewTopMessage)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatCitation)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatVehicle)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatViolation)

                if (mOfficerList.size>0) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                }else{
                    linearLayoutCompatOfficer.visibility= View.GONE
                }
                ;
                if (mCommentsList.size>0) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                }else{
                    linearLayoutCompatRemark.visibility= View.GONE
                }

                linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                mainScope.launch {
                    addSomeTweakBeforeCommandBasedFacsimile()
                    delay(Constants.SAVEPRINTBITMAPDELAYTIME)
                    LogUtil.printLog("==>Reprint","3629")
                    loadBitmapFromView(linearLayoutMainPrint,isOCR = false) //layOfficerDetails);
                }
            }, 300)
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
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mViolationList.size > 0) {
                        setAdapterForViolationList(mViolationList, linearLayoutCompatViolation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }

            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mMotoristInformationList.size > 0) {
                        setAdapterForMotoristInformationList(mMotoristInformationList, linearLayoutCompatMotoristInformation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }

            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mVehicleList.size > 0) {
                        setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mOfficerList.size > 0) {
                        setAdapterForOfficerList(mOfficerList, linearLayoutCompatOfficer)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
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
                appCompatTextViewMotoristInformationTitle.text = mPrintLayoutTitle[6]
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }

            setDuncanSiteId()
            if (isOCRTextPrintlayout) {
                setOCRTextValueOnView()
            }

            val linearLayoutCompatBottomMargin = this.layoutInflater.inflate(
                R.layout.content_print_location_details, null) as LinearLayoutCompat

            Handler(Looper.getMainLooper()).postDelayed({
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
                }else{
                    if (mCommentsList.size>0) {
                        linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                        if (mCommentsList.size==0) {
                            appCompatTextViewCommentsTitle.visibility = View.GONE
                        }
                    }else{
                        linearLayoutCompatRemark.visibility= View.GONE
                    }
                }
                mainScope.launch {
                    addSomeTweakBeforeCommandBasedFacsimile()
                    delay(Constants.SAVEPRINTBITMAPDELAYTIME)
                    LogUtil.printLog("==>Reprint","3734")
                    loadBitmapFromView(linearLayoutMainPrint,isOCR = false) //layOfficerDetails);
                }
            }, 300)
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)||
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
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mViolationList.size > 0) {
                        setAdapterForViolationList(mViolationList, linearLayoutCompatViolation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mMotoristInformationList.size > 0) {
                        setAdapterForMotoristInformationList(mMotoristInformationList, linearLayoutCompatMotoristInformation)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mVehicleList.size > 0) {
                        setAdapterForVehicalList(mVehicleList, linearLayoutCompatVehicle)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (mOfficerList.size > 0) {
                        setAdapterForOfficerList(mOfficerList, linearLayoutCompatOfficer)
                    }
                }, 200)

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
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
                appCompatTextViewMotoristInformationTitle.text = mPrintLayoutTitle[6]
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity, getString(R.string.error_generate_facsimile_image))
            }

            val linearLayoutCompatBottomMargin = this.layoutInflater.inflate(
                    R.layout.content_print_location_details,
                    null
            ) as LinearLayoutCompat

            Handler(Looper.getMainLooper()).postDelayed({
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBottomMargin);
                linearLayoutCompatChildContainer.addView(linearLayoutCompatCitation)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatVehicle)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatViolation)
//                linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)

                mainScope.launch {
                    addSomeTweakBeforeCommandBasedFacsimile()
                    delay(Constants.SAVEPRINTBITMAPDELAYTIME)
                    LogUtil.printLog("==>Reprint","3820")
                    loadBitmapFromView(linearLayoutMainPrint,isOCR = false) //layOfficerDetails);
                }
            }, 300)
        }


    }

    fun printLayoutRecursive(result:HashMap<String, Int>,linearLayoutCompatCitation:LinearLayoutCompat
                             ,linearLayoutCompatViolation:LinearLayoutCompat,linearLayoutCompatVehicle:LinearLayoutCompat
                             ,linearLayoutCompatRemark:LinearLayoutCompat,linearLayoutCompatOfficer:LinearLayoutCompat,linearLayoutCompatMotoristInformation:LinearLayoutCompat
                             ,appCompatTextViewCitaitonTitle:AppCompatTextView,appCompatTextViewViolationTitle:AppCompatTextView
                             ,appCompatTextViewVehileTitle:AppCompatTextView,appCompatTextViewCommentsTitle:AppCompatTextView
                             ,appCompatTextViewOfficerTitle:AppCompatTextView, appCompatTextViewMotoristInformationTitle:AppCompatTextView)
    {
        if(result.keys.elementAt(0).equals("CITATION",ignoreCase = false))
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
                } else if (v == 6) {
                    setAdapterForCitationList(mMotoristInformationList, linearLayoutCompatCitation) //
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
                result.remove("CITATION")
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result.keys.elementAt(0).equals("VIOLATION",ignoreCase = false)){
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
                } else if (v == 6) {
                    setAdapterForViolationList(mMotoristInformationList, linearLayoutCompatViolation) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatViolation)
                appCompatTextViewViolationTitle.text = mPrintLayoutTitle[v!!]

                if (recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }

                recursiveIndex++
                result.remove("VIOLATION")

                mainScope.launch {
                    if(result.size==0) {
                        addSomeTweakBeforeCommandBasedFacsimile()
                        delay(Constants.SAVEPRINTBITMAPDELAYTIME)
                        LogUtil.printLog("==>Reprint","3903")
                        loadBitmapFromView(linearLayoutMainPrint, isOCR = false)
                    }
                }
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result.keys.elementAt(0).equals(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION,ignoreCase = false)){
            try {
                val v = result.get(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION)
                if (v == 1) {
                    setAdapterForMotoristInformationList(mCitationList, linearLayoutCompatMotoristInformation) //
                } else if (v == 2) {
                    setAdapterForMotoristInformationList(mVehicleList, linearLayoutCompatMotoristInformation) //
                } else if (v == 3) {
                    setAdapterForMotoristInformationList(mViolationList, linearLayoutCompatMotoristInformation) //
                } else if (v == 4) {
                    setAdapterForMotoristInformationList(mOfficerList, linearLayoutCompatMotoristInformation) //
                } else if (v == 5) {
                    setAdapterForMotoristInformationList(mCommentsList, linearLayoutCompatMotoristInformation) //
                } else if (v == 6) {
                    setAdapterForMotoristInformationList(mMotoristInformationList, linearLayoutCompatMotoristInformation) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatMotoristInformation)
                appCompatTextViewMotoristInformationTitle.text = mPrintLayoutTitle[v!!]

                recursiveIndex++
                result.remove(PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION)

                mainScope.launch {
                    if(result.size==0) {
                        addSomeTweakBeforeCommandBasedFacsimile()
                        delay(Constants.SAVEPRINTBITMAPDELAYTIME)
                        LogUtil.printLog("==>Reprint","3951")
                        loadBitmapFromView(linearLayoutMainPrint, isOCR = false)
                    }
                }
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result.keys.elementAt(0).equals("VEHICLE",ignoreCase = false)){
            try {
                val v = result.get("VEHICLE")
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
                } else if (v == 6) {
                    setAdapterForVehicalList(mMotoristInformationList, linearLayoutCompatVehicle) //
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
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }
                recursiveIndex++
                result.remove("VEHICLE")

                mainScope.launch {
                    if(result.size==0) {
                        addSomeTweakBeforeCommandBasedFacsimile()
                        delay(Constants.SAVEPRINTBITMAPDELAYTIME)
                        LogUtil.printLog("==>Reprint","3951")
                        loadBitmapFromView(linearLayoutMainPrint, isOCR = false)
                    }
                }
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result.keys.elementAt(0).equals("COMMENT",ignoreCase = false)){
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
                    setAdapterForCommentesList(mCommentsList, linearLayoutCompatRemark) //
                } else if (v == 6) {
                    setAdapterForCommentesList(mMotoristInformationList, linearLayoutCompatRemark) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatRemark)
                appCompatTextViewCommentsTitle.text = mPrintLayoutTitle[v!!]

                if (recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, true))
                {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }
                recursiveIndex++
                result.remove("COMMENT")

                mainScope.launch {
                    if(result.size==0) {
                        addSomeTweakBeforeCommandBasedFacsimile()
                        delay(Constants.SAVEPRINTBITMAPDELAYTIME)
                        LogUtil.printLog("==>Reprint","3995")
                        loadBitmapFromView(linearLayoutMainPrint, isOCR = false)
                    }
                }
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result.keys.elementAt(0).equals("OFFICER",ignoreCase = false)){
            try {
                val v = result.get("OFFICER")
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
                } else if (v == 6) {
                    setAdapterForOfficerList(mMotoristInformationList, linearLayoutCompatOfficer) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                appCompatTextViewOfficerTitle.text = mPrintLayoutTitle[v!!]

                if (recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1&& !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }
                recursiveIndex++
                result.remove("OFFICER")
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        else if(result.containsKey("LOCATION")){
            try {
                val v = result.get("LOCATION")
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
                } else if (v == 6) {
                    setAdapterForOfficerList(mMotoristInformationList, linearLayoutCompatOfficer) //
                }
                linearLayoutCompatChildContainer.addView(linearLayoutCompatOfficer)
                appCompatTextViewOfficerTitle.text = mPrintLayoutTitle[v!!]

                if (recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true)&&
                    recursiveIndex >= mPrintLayoutMap.size - 1 && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)) {
                    linearLayoutCompatChildContainer.addView(linearLayoutCompatBarCode)
                }
                recursiveIndex++
                result.remove("LOCATION")
            } catch (e: Exception) {
                isErrorOccurDuringGenerateFacsimile = true
                e.printStackTrace()
                LogUtil.printToastMSG(this@MunicipalCitationReprintReuploadActivity,getString(R.string.error_generate_facsimile_image))

            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            if(result.size>0 && isErrorOccurDuringGenerateFacsimile==false) {
                printLayoutRecursive(result, linearLayoutCompatCitation, linearLayoutCompatViolation, linearLayoutCompatVehicle, linearLayoutCompatRemark, linearLayoutCompatOfficer,linearLayoutCompatMotoristInformation, appCompatTextViewCitaitonTitle, appCompatTextViewViolationTitle, appCompatTextViewVehileTitle, appCompatTextViewCommentsTitle, appCompatTextViewOfficerTitle, appCompatTextViewMotoristInformationTitle)
            }
        }, 200)

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
            R.id.layOwnerBill
        )
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
            val X:Int =  553
            if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)){
                Y = 400
            }else if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)){
                Y = 120
            }

            LogUtil.printLog("==>OCR:","First")
            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintOCRTEXT(
                mOCRText1!!.toString(),
                1,
                "OCR TEXT",
                AppUtils.printQueryStringBuilder!!,X,Y
            )
            if(mOCRText2!=null && mOCRText2.length>1) {
                LogUtil.printLog("==>OCR:","Second")

                Y = (Y + mYIncrease)
                AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintOCRTEXT(
                    mOCRText2!!.toString(),
                    1,
                    "OCR TEXT",
                    AppUtils.printQueryStringBuilder!!, X, Y
                )
            }
            if(mOCRText3!=null && mOCRText3.length>1) {
                LogUtil.printLog("==>OCR:","Third")

                Y = (Y + mYIncrease)
                AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintOCRTEXT(
                    mOCRText3!!.toString(),
                    1,
                    "OCR TEXT",
                    AppUtils.printQueryStringBuilder!!, X, Y
                )
            }
            if(mOCRText4!=null && mOCRText3.length>1) {
                LogUtil.printLog("==>OCR:","Four")

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
             * TIcket Number
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

    var removeIndex = -3
    private fun setAdapterForCitationList(citaitonList: List<VehicleListModel>,
                                          linearLayoutCompatCitaion: LinearLayoutCompat) {
        LogUtil.printLog("Reprint:","setAdapterForCitationList")
        val recyclerVehical: RecyclerView =
            linearLayoutCompatCitaion.findViewById(R.id.recycler_citation)
        var spanCount = 3

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STRATOS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,ignoreCase = true)
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
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,ignoreCase = true)) {
                gridLayoutCountForCitation = 3
                removeIndex = -3
                spanCount = 3
            } else if(
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
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)
                ){
                gridLayoutCountForCitation = 1
                removeIndex = -1
                spanCount = 1
            }else {
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
                    //                    if(listModel.getType() == 1)
//                    {
//                        i=removeIndex ;
//                    }
                    if (listModel.type == 3) {
                        i = -3
                    } else if (listModel.type == 2) {
                        i = -2
                    }
                    i++
                }


                    mCitaionListAdapter = MunicipalCitationAdapter(mContext!!, mFinalList,
                        object : MunicipalCitationAdapter.ListItemSelectListener {
                            override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                                                     mStatus: Boolean, position: Int) {
                            }
                        })
                    recyclerVehical.setHasFixedSize(true)

                    val gridLayoutManager = GridLayoutManager(mContext, spanCount)
                    gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (mCitaionListAdapter!!.getItemViewType(position)) {
                                MunicipalCitationAdapter.ONECOLUMN -> 1
                                MunicipalCitationAdapter.TWOCOLUMN -> 2
                                MunicipalCitationAdapter.THREECOLUMN -> 3
                                else -> 1
                            }
                        }
                    }
                    recyclerVehical.adapter = mCitaionListAdapter
                    recyclerVehical.layoutManager = gridLayoutManager
                    recyclerVehical.visibility = View.VISIBLE

            } else {
                recyclerVehical.visibility = View.GONE
            }

    }

    private fun setAdapterForOfficerList(mOfficerList: List<VehicleListModel>,
                                         linearLayoutCompatOfficer: LinearLayoutCompat) {
        LogUtil.printLog("Reprint:","setAdapterForOfficerList")
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
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)){
                    1
                } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
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


                    mOfficerListAdapter = MunicipalOfficerListAdapter(mContext!!, mFinalList,
                        object : MunicipalOfficerListAdapter.ListItemSelectListener {
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
                                return when (mOfficerListAdapter!!.getItemViewType(position)) {
                                    MunicipalOfficerListAdapter.ONECOLUMN -> 1
                                    MunicipalOfficerListAdapter.TWOCOLUMN -> 2
                                    MunicipalOfficerListAdapter.THREECOLUMN -> 3
                                    else -> 1
                                }
                            }
                        }
                        recyclerOfficer.layoutManager = gridLayoutManager
                    }
                    recyclerOfficer.adapter = mOfficerListAdapter
                    recyclerOfficer.visibility = View.VISIBLE

            }

    }

    private fun setAdapterForVehicalList(mOfficerList: List<VehicleListModel>,
                                         linearLayoutCompatVehicle: LinearLayoutCompat) {
        LogUtil.printLog("Reprint:","setAdapterForVehicalList")
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
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)
                    ){
                    spanCount = 1
                }



                    mVehicalListAdapter = MunicipalVehicalListAdapter(mContext!!, mFinalList,
                        object : MunicipalVehicalListAdapter.ListItemSelectListener {
                            override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                                                     mStatus: Boolean, position: Int) {
                            }
                        })
                    recyclerVehical.setHasFixedSize(true)
                    val mLayoutManager = GridLayoutManager(mContext, spanCount)
                    mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (mVehicalListAdapter!!.getItemViewType(position)) {
                                MunicipalVehicalListAdapter.ONECOLUMN -> 1
                                MunicipalVehicalListAdapter.TWOCOLUMN -> 2
                                MunicipalVehicalListAdapter.THREECOLUMN -> 3
                                else -> 1
                            }
                        }
                    }
                    recyclerVehical.adapter = mVehicalListAdapter
                    recyclerVehical.layoutManager = mLayoutManager
                    recyclerVehical.visibility = View.VISIBLE

            }

    }

    private fun setAdapterForViolationList(mOfficerList: List<VehicleListModel>,
                                           linearLayoutCompatViolation: LinearLayoutCompat) {

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

                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
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
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)
                    ){
                    spanCount = 1
                }


                    mViolationsAdapter = MunicipalViolationsAdapter(mContext!!, mFinalList,
                        object : MunicipalViolationsAdapter.ListItemSelectListener {
                            override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                                                     mStatus: Boolean, position: Int) {
                            }
                        })
                    recyclerViolation.setHasFixedSize(true)
                    //            recyclerViolation.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    val mLayoutManager = GridLayoutManager(mContext, spanCount)
                    mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (mViolationsAdapter!!.getItemViewType(position)) {
                                MunicipalViolationsAdapter.ONECOLUMN -> 1
                                MunicipalViolationsAdapter.TWOCOLUMN -> 2
                                MunicipalViolationsAdapter.THREECOLUMN -> 3
                                else -> 1
                            }
                        }
                    }
                    recyclerViolation.adapter = mViolationsAdapter
                    recyclerViolation.layoutManager = mLayoutManager
                    recyclerViolation.setHasFixedSize(true)
                    recyclerViolation.visibility = View.VISIBLE

            }

    }

    private fun setAdapterForCommentesList(mCommentesList: List<VehicleListModel>,
                                           linearLayoutCompatComments: LinearLayoutCompat) {
        val recyclerViewComments: RecyclerView =
            linearLayoutCompatComments.findViewById(R.id.recyclercommentes)
        if (mCommentesList.size > 0) {
            commentesAdapter = MunicipalCommentesAdapter(mContext!!, mCommentesList,
                object : MunicipalCommentesAdapter.ListItemSelectListener {
                    override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                                             mStatus: Boolean, position: Int) {
                    }
                })
            recyclerViewComments.setHasFixedSize(true)
            var mLayoutManager = GridLayoutManager(mContext, 1)
            //            recyclerViolation.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
                    ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,
                    ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,
                    ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,
                    ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,
                    ignoreCase = true) ) {
                mLayoutManager = GridLayoutManager(mContext, 2)
            }
            recyclerViewComments.layoutManager = mLayoutManager
            mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (commentesAdapter!!.getItemViewType(position)) {
                        MunicipalCommentesAdapter.ONECOLUMN -> 1
                        MunicipalCommentesAdapter.TWOCOLUMN -> 2
                        MunicipalCommentesAdapter.THREECOLUMN -> 3
                        else -> 1
                    }
                }
            }
            recyclerViewComments.adapter = commentesAdapter
//            recyclerViewComments.layoutManager = mLayoutManager
//            recyclerViewComments.setHasFixedSize(true)
            recyclerViewComments.visibility = View.VISIBLE
        } else {
            recyclerViewComments.visibility = View.GONE
        }
    }

    private fun setAdapterForMotoristInformationList(mOfficerList: List<VehicleListModel>,
                                                     linearLayoutCompatMotoristInformation: LinearLayoutCompat) {

        val recyclerMotoristInformation: RecyclerView =
            linearLayoutCompatMotoristInformation.findViewById(R.id.recyclerMotoristInformation)
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

            recyclerMotoristInformation.post {
                motoristInformationAdapter = MunicipalMotoristInformationAdapter(mContext!!, mFinalList,
                    object : MunicipalMotoristInformationAdapter.ListItemSelectListener {
                        override fun onItemClick(rlRowMain: LinearLayoutCompat?,
                                                 mStatus: Boolean, position: Int) {
                        }
                    })
                recyclerMotoristInformation.setHasFixedSize(true)
                //            recyclerViolation.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                val mLayoutManager = GridLayoutManager(mContext, spanCount)
                mLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (motoristInformationAdapter!!.getItemViewType(position)) {
                            MunicipalMotoristInformationAdapter.ONECOLUMN -> 1
                            MunicipalMotoristInformationAdapter.TWOCOLUMN -> 2
                            MunicipalMotoristInformationAdapter.THREECOLUMN -> 3
                            else -> 1
                        }
                    }
                }
                recyclerMotoristInformation.adapter = motoristInformationAdapter
                recyclerMotoristInformation.layoutManager = mLayoutManager
                recyclerMotoristInformation.setHasFixedSize(true)
                recyclerMotoristInformation.visibility = View.VISIBLE
            }
        }
    }


    fun addSomeTweakBeforeCommandBasedFacsimile() {
//        try {
        if (LogUtil.isEnableCommandBasedFacsimile) {
            linearLayoutCompatChildContainer.invisibleView()
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
            val b = Bitmap.createBitmap(view!!.width,
                    view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(b)
            view.draw(canvas)

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())) {
                relativeLayoutPrintAmountSection!!.visibility = View.INVISIBLE
                relativeLayoutPrintCitationSection!!.visibility = View.INVISIBLE
                relativeLayoutPrintPhilaSection!!.visibility = View.VISIBLE
            }

            try {
                if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER && AppUtils.isSiteSupportCommandPrinting(getSettingFileValuesForCMDPrinting())){
                    ZebraCommandPrintUtils.getFromPrefAndSetToPrintComment(sharedPreference)

                    //Setting up extra parameter which should be there in print rather then officer, vehicle, voilation, citation & comment details
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintHeaderAndLines(sharedPreference, AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setAppLogoInCommandPrint(this@MunicipalCitationReprintReuploadActivity,sharedPreference, AppUtils.printQueryStringBuilder)
                    val lprBitmapFile = FileUtil.getLprImageFileFromBannerList(bannerList)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setLprImageInCommandPrint(getMyDatabase(), lprBitmapFile,sharedPreference, AppUtils.printQueryStringBuilder)

                    //Below statement we can get in old zebra class
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintCitationHeader(AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintQRCode(this@MunicipalCitationReprintReuploadActivity, AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintQRCodeLabel( AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintBarCode( getMyDatabase(),mCitationNumber.nullSafety(), AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setBottomAddressInCommand( AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYForAddressLines( AppUtils.printQueryStringBuilder)
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYForLines(sharedPreference, AppUtils.printQueryStringBuilder)
                }

                if (LogUtil.isEnableCommandBasedFacsimile){
                    LogUtil.printLog("==>Reprint","Calling")
                    val bitmap = CanvasUtils.drawElementsToBitmapAutoSize(this@MunicipalCitationReprintReuploadActivity, drawableElements)

                    ivCommandBasedFacsimile.showView()
                    ivCommandBasedFacsimile.setImageBitmap(bitmap)

                    SaveImageMM(bitmap, mCitationNumber + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)

                    AppUtils.clearDrawableElementList()
                }else{
                    SaveImageMM(b, mCitationNumber + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
                }
            }catch (e:Exception){
                linearLayoutCompatChildContainer.showView()
                SaveImageMM(b, mCitationNumber + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
            }


            //TODO OCR
           /* if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true))
            {
                Handler().postDelayed({
                    mImageViewBarcodePrint!!.visibility = View.VISIBLE
                    relativeLayoutPrintAmountSection!!.visibility = View.VISIBLE
                    relativeLayoutPrintCitationSection!!.visibility = View.VISIBLE
                    relativeLayoutPrintPhilaSection!!.visibility = View.VISIBLE

                    val bitmap = Bitmap.createBitmap(
                            view!!.width,
                            view.height, Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    view.draw(canvas)
                    SaveImageOCRMM(bitmap, mCitationNumber + "_" + FILE_NAME_FACSIMILE_OCR_BITMAP)
                }, 800)
            }*/

            mainScope.launch {
                //setAppLogoInCommandPrint()
//                if(isPrintLprImageInFacsimilePrint(getMyDatabase()!!)) {
//                    val bannerList =
//                        mDb?.dbDAO?.getCitationImage() as ArrayList<CitationImagesModel>
//                    if (bannerList != null && bannerList!!.size > 0 &&
//                        bannerList!!.get(0)!!.citationImage!!.contains("anpr_")
//                    ) {
//                        val imgFileCopy = bannerList!!.get(0)!!.citationImage?.let { File(it) }
//                        if (imgFileCopy != null && imgFileCopy!!.exists()) {
//                            val lprBitmap = BitmapFactory.decodeFile(imgFileCopy.absolutePath)
//
//                            setLprImageInCommandPrint(lprBitmap)
//                        }
//                    }
//                }

                delay(400)
//                if (mTicketActionButtonEvent == null||
//                        mTicketActionButtonEvent.equals("UnUpload", true)) {
//                    mTicketActionButtonEvent = ""
//                } else if (mTicketActionButtonEvent.equals("VoidReissue", true)) {
//                    mTicketActionButtonEvent = "Valid"
//                }
                if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
                    starPrinterUseCase?.mPrintFacsimileImage(
                        savePrintImagePath!!, this@MunicipalCitationReprintReuploadActivity,
                        "previewScreen", "Valid", mCitationNumber,
                        mFinalAmount, mIssuranceModel?.citationData?.vehicle!!.state,
                        mIssuranceModel?.citationData?.vehicle!!.licensePlate, "", printcommand!!
                    )

                } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
                    xfPrinterUseCase?.mPrintFacsimileImage(
                        savePrintImagePath!!, this@MunicipalCitationReprintReuploadActivity,
                        "previewScreen", "Valid", mCitationNumber,
                        mFinalAmount, mIssuranceModel?.citationData?.vehicle!!.state,
                        mIssuranceModel?.citationData?.vehicle!!.licensePlate, "", printcommand!!
                    )

                } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
                    zebraPrinterUseCase?.mPrintFacsimileImage(
                        savePrintImagePath!!, this@MunicipalCitationReprintReuploadActivity,
                        "previewScreen", "Valid", mCitationNumber,
                        mFinalAmount, mIssuranceModel?.citationData?.vehicle!!.state,
                        mIssuranceModel?.citationData?.vehicle!!.licensePlate, "", printcommand!!
                    )

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        var savePrintImagePath: File? = null
    }

    //TODO will add comments later
    private fun SaveImageMM(finalBitmap: Bitmap?, imageNmae: String?) {
        val myDir = File(
                Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.CAMERA
        )

        myDir.mkdirs()
        val fname = "${imageNmae?.trim()}.jpg" //"print_bitmap.jpg";
        savePrintImagePath = File(myDir, fname)
        if (savePrintImagePath!!.exists()) savePrintImagePath!!.delete()
        try {
            sharedPreference.write(
                    SharedPrefKey.REPRINT_PRINT_BITMAP,
                    savePrintImagePath!!.absoluteFile.toString()
            )
            val out = FileOutputStream(savePrintImagePath)
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,true)) {
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
    //TODO will add comments later
    private fun SaveImageOCRMM(finalBitmap: Bitmap?, imageNmae: String?) {
        val myDir = File(
                Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.CAMERA
        )
        myDir.mkdirs()
        val fname = "${imageNmae?.trim()}.jpg" //"print_bitmap.jpg";
        var savePrintImage: File? = null
        savePrintImage = File(myDir, fname)
        if (savePrintImage!!.exists()) savePrintImage!!.delete()
        try {
            sharedPreference.write(
                    SharedPrefKey.REPRINT_PRINT_BITMAP_OCR,
                    savePrintImage!!.absoluteFile.toString()
            )
            val out = FileOutputStream(savePrintImage)
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,true)) {
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            }else{
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            }

            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onActionSuccess(value: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            callUploadImages(savePrintImagePath, 1)
        }, 200)

    }
    override fun onYesButtonClick() {
        finish()
    }
    override fun onNoButtonClick() {
        finish()
    }
    override fun onYesButtonClickParam(msg: String?) {
        finish()
    }

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            zebraPrinterUseCase?.disconnect()
        }
    }

}