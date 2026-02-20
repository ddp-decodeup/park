package com.parkloyalty.lpr.scan.ui.boot

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullOrEmptySafety
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.boot.model.*
import com.parkloyalty.lpr.scan.ui.check_setup.activity.AddTimeRecordActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetails2Activity
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ScofflawDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.printer.PrinterType
import com.parkloyalty.lpr.scan.ui.printer.ZebraPrinterUseCase
import com.parkloyalty.lpr.scan.util.*
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.setListOnly
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.util.Util.getIndexOfLot
import com.parkloyalty.lpr.scan.util.Util.getIndexOfSpaceName
import com.parkloyalty.lpr.scan.util.permissions.BluetoothPermissionUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern
import kotlin.getValue

@AndroidEntryPoint
class BootActivity : BaseActivity(), CustomDialogHelper, PrintInterface {
    @JvmField
    @BindView(R.id.input_officer_name)
    var mTextInputOfficerName: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_officer_name)
    var mEditTextOfficerName: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.input_date_time)
    var mTextInputDateTime: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_date_time)
    var mEditTextDateTime: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.input_citation)
    var mTextInputCitation: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_citation)
    var mEditTextCitation: AppCompatEditText? = null

   /* @JvmField
    @BindView(R.id.input_block)
    var mTextInputBlock: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_block)
    var mEditTextBlock: AppCompatEditText? = null*/

//    @JvmField
//    @BindView(R.id.input_Street)
//    var mTextInputStreet: TextInputLayout? = null
//
//    @JvmField
//    @BindView(R.id.et_street)
//    var mEditTextStrret: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.AutoComTextViewVehState)
    var mAutoComTextViewState: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.AutoComTextViewVehblock)
    var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.AutoComTextViewVehStreet)
    var mEditTextViewTextViewStreet: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.AutoComTextViewVehSideOfState)
    var mEditTextViewTextViewSideOfState: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.AutoComTextViewMakeVeh)
    var mEditTextViewTextViewMake: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.AutoComTextViewVehModel)
    var mEditTextViewTextViewModel: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.AutoComTextViewVehColor)
    var mEditTextViewTextViewColor: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.AutoComTextViewVehRemark)
    var mEditTextViewTextViewRemark: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.textInputLayoutLot)
    var textInputLayoutLot: TextInputLayout? = null

    @JvmField
    @BindView(R.id.inputTextSpace)
    var textInputLayoutSpace: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutVehBlock)
    var textInputLayoutVehBlock: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutVehState)
    var textInputLayoutVehState: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutVehStreet)
    var textInputLayoutVehStreet: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutVehSideOfStreet)
    var textInputLayoutVehSideOfStreet: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutVehMake)
    var textInputLayoutVehMake: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutVehModel)
    var textInputLayoutVehModel: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutVehColor)
    var textInputLayoutVehColor: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutVehRemark)
    var textInputLayoutVehRemark: TextInputLayout? = null

    @JvmField
    @BindView(R.id.input_textNote)
    var textInputLayoutNote: TextInputLayout? = null

    @JvmField
    @BindView(R.id.etNote)
    var mEditTextViewTextViewNote: TextInputEditText? = null

    @JvmField
    @BindView(R.id.check_regular)
    var appCompatCheckBoxRegular: AppCompatCheckBox? = null

    @JvmField
    @BindView(R.id.check_heavy)
    var appCompatCheckBoxHeavy: AppCompatCheckBox? = null

    @JvmField
    @BindView(R.id.check_medium)
    var appCompatCheckBoxMedium: AppCompatCheckBox? = null

    @JvmField
    @BindView(R.id.ll_checkbox)
    var linearLayoutCompatCheckBox: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.ll_model)
    var linearLayoutCompatllModel: LinearLayoutCompat? = null

    //Start for field specific to Septa
    @JvmField
    @BindView(R.id.llSpaceNumber)
    var llSpaceNumber: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.llLotNumber)
    var llLotNumber: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.ll_side)
    var llSideOfStreet: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.etLot)
    var mTextViewLot: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.etSpace)
    var mTextViewSpace: AppCompatAutoCompleteTextView? = null
    //End for field specific to Septa

    @JvmField
    @BindView(R.id.bottomView)
    var viewBottom: View? = null

    @JvmField
    @BindView(R.id.btn_submit_time)
    var btnSubmitTime: AppCompatButton? = null

    @JvmField
    @BindView(R.id.btn_submit)
    var btnSubmit : AppCompatButton? = null

    @JvmField
    @BindView(R.id.btn_submit_issue)
    var btnSubmitIssue : AppCompatButton? = null

    @JvmField
    @BindView(R.id.viewPrintDivider)
    var viewPrintDivider: View? = null

    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private val mModelList: MutableList<DatasetResponse> = ArrayList()
    private var mSelectedMake: String? = ""
    private var mSelectedModel: String? = ""
    private var mSelectedMakeValue: String? = ""
    private var mSelectedColor: String? = ""
    private var mSelectedLpNumber: String? = ""
    private val mSideItem = ""
    private var clientTime: String? = null
    private var apiResultTag = ""
    private var mStateItem = "Pennsylvania"
    private var mState2DigitCode = ""
    private var btnClickedEvent = "Submit"

    private var mWelcomeFormData: WelcomeForm? = null
    private var scofflawDataResponse: ScofflawDataResponse? = null
    private val bootViewModel: BootViewModel? by viewModels()
    private val mCitationNumberModel: CitationNumberModel? by viewModels()

    //Start for field specific to Septa
    private var citationNumber: String? = null
    private var violationDate: String? = null
    private var lotNumber: String? = null
    private var spaceName: String? = null
    //End for field specific to Septa
    private var mBackgroundWhiteBitmap : Bitmap?= null
    var adjustableHeight = 0f

    private var mStreetItem: String? = ""
    private var mCitationNumberId: String? = ""

    val printBootNoticeModel = PrintBootNoticeModel()

    private var zebraPrinterUseCase: ZebraPrinterUseCase? = null

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Nothing to implement here
            } else {
                BluetoothPermissionUtil.requestBluetoothSetting(this@BootActivity)
            }
        }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)
        setFullScreenUI()
        ButterKnife.bind(this)
        addObservers()
        init()

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            zebraPrinterUseCase = ZebraPrinterUseCase()
            zebraPrinterUseCase?.setPrintInterfaceCallback(this)
            zebraPrinterUseCase?.initialize(
                activity = this@BootActivity,
                contentResolver = contentResolver,
                sharedPreference = sharedPreference
            )
        }

        BluetoothPermissionUtil.checkBluetoothPermissions(
            activity = this,
            permissionLauncher = bluetoothPermissionLauncher
        ) {
            //Nothing to implement here in onCreate, This will check bluetooth permission and redirect user to allow bluetooth permission, in case misssed
        }

        setAccessibilityForComponents()
    }

    private fun setAccessibilityForComponents() {
        mTextInputOfficerName?.setAccessibilityForTextInputLayoutWithHintOnly()
        mTextInputDateTime?.setAccessibilityForTextInputLayoutWithHintOnly()
        mTextInputCitation?.setAccessibilityForTextInputLayoutWithHintOnly()

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutLot
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutSpace
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehBlock
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehState
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehStreet
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehSideOfStreet
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehMake
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehModel
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehColor
        )

        setDoNothingAccessibilityForTextInputLayoutDropdownButtons(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehRemark
        )
    }


    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        setToolbar()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        getIntentData()
        setLayoutVisibilityBasedOnSettingResponse()
        setLayoutVisibilityBasedOnSite()


        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13())) {
            linearLayoutCompatCheckBox?.visibility = View.GONE
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true)) {
            val drawableBitmap = BitmapFactory.decodeResource(resources, R.drawable.white_print)
            mBackgroundWhiteBitmap = drawableBitmap.copy(Bitmap.Config.ARGB_8888, true)
            Canvas(mBackgroundWhiteBitmap!!)
        }

        setCrossClearButtonForAllFields()

        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SEPTA,
                true
            )
        ) {
            getLatestCitationNumber()
        }
    }

    private fun setCrossClearButtonForAllFields(){
        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = mTextInputCitation,
            appCompatEditText = mEditTextCitation
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutLot,
            appCompatAutoCompleteTextView = mTextViewLot
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutSpace,
            appCompatAutoCompleteTextView = mTextViewSpace
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehBlock,
            appCompatAutoCompleteTextView = mAutoComTextViewBlock
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehState,
            appCompatAutoCompleteTextView = mAutoComTextViewState
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehStreet,
            appCompatAutoCompleteTextView = mEditTextViewTextViewStreet
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehSideOfStreet,
            appCompatAutoCompleteTextView = mEditTextViewTextViewSideOfState
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehMake,
            appCompatAutoCompleteTextView = mEditTextViewTextViewMake
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehModel,
            appCompatAutoCompleteTextView = mEditTextViewTextViewModel
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehColor,
            appCompatAutoCompleteTextView = mEditTextViewTextViewColor
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutVehRemark,
            appCompatAutoCompleteTextView = mEditTextViewTextViewRemark
        )

        setCrossClearButton(
            context = this@BootActivity,
            textInputLayout = textInputLayoutNote,
            textInputEditText = mEditTextViewTextViewNote
        )
    }

    /**
     * As we need fresh citation number for notice print & ticket creation from here, we are using below function and its associates
     */
    private fun getLatestCitationNumber() {
        val mFetched = mDb?.dbDAO?.getCitationBooklet(0)

        if (!mFetched.isNullOrEmpty()) {
            mCitationNumberId = mFetched.first()?.citationBooklet
        } else {
            //No Implementation is needed
        }

        //Call New Booklet API
        val isBookletExits = mDb?.dbDAO?.getCitationBooklet(0)
        if (isBookletExits!!.size < 3) {
            callCitationNumberApi()
        }
    }

    private fun callCitationNumberApi() {
        if (isInternetAvailable(this@BootActivity)) {
            val mCitationNumberRequest = CitationNumberRequest()
            val uniqueID = AppUtils.getDeviceId(mContext!!)
            val welcomeForm: WelcomeForm? = mDb!!.dbDAO!!.getWelcomeForm()
            if (welcomeForm != null && !welcomeForm.officerDeviceName.isNullOrEmpty()) {
                mCitationNumberRequest.deviceId = uniqueID + "-" + welcomeForm.officerDeviceName
            } else {
                mCitationNumberRequest.deviceId = uniqueID + "-" + "Device"
            }
            mCitationNumberModel!!.hitGetCitationNumberApi(mCitationNumberRequest)
        } else {
            printToastMSG(this@BootActivity, getString(R.string.err_msg_connection_was_refused))
        }
    }

    private fun getIntentData() {
        try {
            mSelectedMake = intent.getStringExtra(INTENT_KEY_MAKE)
            mSelectedModel = intent.getStringExtra(INTENT_KEY_MODEL)
            mSelectedColor = intent.getStringExtra(INTENT_KEY_COLOR)
            mSelectedLpNumber = intent.getStringExtra(INTENT_KEY_LPNUMBER)
            scofflawDataResponse = intent.getParcelableExtra(INTENT_KEY_SCCOFFLAW)
            mStateItem = if (scofflawDataResponse != null) scofflawDataResponse!!.state!! else ""

            //We only have to get citation number & violation date when we are in SEPTA
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SEPTA,
                    true
                )
            ) {
                citationNumber = intent.getStringExtra(INTENT_KEY_CITATION_NUMBER).nullSafety()
                violationDate = intent.getStringExtra(INTENT_KEY_VIOLATION_DATE).nullSafety()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        registerBroadcastReceiver()
        CheckKeyBoardView()
    }


    private val bootResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.SUMIT_BOOT
        )
    }

    private val bootInstanceTicketResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.SUBMIT_BOOT_INSTANCE_TICKET
        )
    }

    private val citationNumberResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CITATION_NUMBER)
    }

    private fun addObservers() {
        bootViewModel?.response?.observe(this, bootResponseObserver)
        bootViewModel?.responseBootInstanceTicketAPI?.observe(this, bootInstanceTicketResponseObserver)
        mCitationNumberModel?.response?.observe(this, citationNumberResponseObserver)

    }

    override fun removeObservers() {
        super.removeObservers()
        bootViewModel?.response?.removeObserver(bootResponseObserver)
        bootViewModel?.responseBootInstanceTicketAPI?.removeObserver(bootInstanceTicketResponseObserver)
        mCitationNumberModel?.response?.removeObserver(citationNumberResponseObserver)
    }

    private fun setDropDowns() {
        try {
            setDropdownMakeVehicle(mSelectedMake)
            setDropdownVehicleModel(mSelectedModel)
            setDropdownVehicleColour(mSelectedColor)
            if (Singleton.getDataSetList(DATASET_REMARKS_LIST, mDb) != null) {
                setDropdownRemark(Singleton.getDataSetList(DATASET_REMARKS_LIST, mDb))
            }
            if (Singleton.getDataSetList(DATASET_SIDE_LIST, mDb) != null) {
                setDropdownSide(Singleton.getDataSetList(DATASET_SIDE_LIST, mDb))
            } else {
                val mApplicationList: MutableList<DatasetResponse> = ArrayList()
                mApplicationList.add(DatasetResponse.setSideName("E",0))
                mApplicationList.add(DatasetResponse.setSideName("N",0))
                mApplicationList.add(DatasetResponse.setSideName("S",0))
                mApplicationList.add(DatasetResponse.setSideName("W",0))
                setDropdownSide(mApplicationList)
            }
            if (Singleton.getDataSetList(DATASET_STREET_LIST, mDb) != null) {
                setDropdownStreet(Singleton.getDataSetList(DATASET_STREET_LIST, mDb))
            }
            if (Singleton.getDataSetList(DATASET_STATE_LIST, mDb) != null) {
                setDropdownState(Singleton.getDataSetList(DATASET_STATE_LIST, mDb))
            }
            if (Singleton.getDataSetList(DATASET_BLOCK_LIST, mDb) != null) {
                setDropdownBlock(Singleton.getDataSetList(DATASET_BLOCK_LIST, mDb))
            }
            getGeoAddress()
            mEditTextCitation?.setText(mSelectedLpNumber)
            mEditTextOfficerName?.setText(
                    mWelcomeFormData?.officerFirstName + " " +
                            mWelcomeFormData?.officerLastName
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //init toolbar navigation
    private fun setToolbar() {
//        if(imgOptions)
        initToolbar(
            2,
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
    }// this will contain "Fruit"// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
    private fun getGeoAddress() {
        mEditTextDateTime?.setText(AppUtils.getCurrentDateTimeforBoot("UI"))
        clientTime = AppUtils.getCurrentDateTimeforBoot("Normal").trim()
            .replace(" ", "T")
        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(
            mLat,
            mLong,
            1
        )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        val address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val separated = address.split(",").toTypedArray()
        val printAddress = separated[0] // this will contain "Fruit"
        mAutoComTextViewBlock?.setText(
            if (printAddress.split(" ").toTypedArray().size > 1) printAddress.split(" ")
                .toTypedArray()[0] else printAddress
        )
        try {
            val count = printAddress.split(" ").toTypedArray().size
            if (count > 2) {
                mEditTextViewTextViewStreet?.setText(
                    if (count > 0) printAddress.split(
                        Pattern.compile(" "),
                        count - 1.coerceAtLeast(0)
                    ).toTypedArray().get(1) else printAddress
                )
            } else {
                mEditTextViewTextViewStreet?.setText(
                    if (count > 1) printAddress.split(" ").toTypedArray()[1] else printAddress
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to State dropdown
    private fun setDropdownState(mApplicationList: List<DatasetResponse>?) {
        var pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                    return lhs?.state_name!!.compareTo(rhs?.state_name!!)
                }
            })
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].state_name.toString()
                if (mApplicationList[i].state_name.equals(mStateItem, ignoreCase = true) ||
                    mApplicationList[i].state_abbreviated.equals(mStateItem, ignoreCase = true)
                ) {
                    pos = i
                    try {
                        mAutoComTextViewState!!.setText(mDropdownList[pos])
                        mState2DigitCode = mApplicationList[i].state_abbreviated!!
                    } catch (e: java.lang.Exception) {
                    }
                }
            }
//            if (pos == 0) {
//                if (mStateItem != null && mStateItem != mApplicationList[0].state_name) {
//                    for (i in mApplicationList.indices) {
//                        mDropdownList[i] = mApplicationList[i].state_name.toString()
//                        if (mApplicationList[i].state_name.equals(
//                                "California",
//                                ignoreCase = true
//                            )
//                        ) {
//                            pos = i
//                            try {
//                                mAutoComTextViewState!!.setText(mDropdownList[pos])
//                            } catch (e: java.lang.Exception) {
//                            }
//                        }
//                    }
//                }
//            }
            //            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mAutoComTextViewState!!.threshold = 1
                mAutoComTextViewState!!.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mAutoComTextViewState!!.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        hideSoftKeyboard(this@BootActivity)
                        //                        mState2DigitCode = mApplicationList.get(position).getState_abbreviated();
                        val index = getIndexOfState(
                            mApplicationList,
                            parent.getItemAtPosition(position).toString()
                        )
                        mState2DigitCode = mApplicationList[index].state_abbreviated!!
                    }

                // listonly
                if (mAutoComTextViewState!!.tag != null && mAutoComTextViewState!!.tag == "listonly") {
                    setListOnly(this@BootActivity, mAutoComTextViewState!!)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
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


    //set value to Street dropdown
    private fun setDropdownStreet(mApplicationList: List<DatasetResponse>?) {
//        hideSoftKeyboard(BootActivity.this);
        var pos = -1
        if (mApplicationList != null && mApplicationList.size > 0) {
//            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
//            for (i in mApplicationList.indices) {
//                mDropdownList[i] = mApplicationList[i].street_name.toString()
//            }

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

            if (pos >= 0)
                mEditTextViewTextViewStreet?.setText(mDropdownList[pos])

            //mAutoComTextViewDirection.setText(mApplicationList.get(pos).getDirection());
//            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mEditTextViewTextViewStreet!!.threshold = 1
                mEditTextViewTextViewStreet!!.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewStreet!!.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> //mAutoComTextViewDirection.setText(mApplicationList.get(position).getDirection()); ;
                        hideSoftKeyboard(this@BootActivity)
                    }
                // listonly
//                if(mEditTextViewTextViewStreet.getTag()!=null && mEditTextViewTextViewStreet.getTag().equals("listonly")) {
//                    AppUtils.setListOnly(BootActivity.this,mEditTextViewTextViewStreet);
//                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
        }
    }

    //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?) {
        try {
            var adapterModel: ArrayAdapter<String?>? = null
            mModelList.clear()
            val mApplicationList = Singleton.getDataSetList(DATASET_CAR_MAKE_LIST, mDb)
//            Collections.sort(mApplicationList) { lhs, rhs ->
//                lhs.model.nullSafety().compareTo(rhs.model.nullSafety())
//            }
            for (i in mApplicationList?.indices!!) {
                if (mApplicationList.get(i).make != null && mApplicationList[i].make == mSelectedMake) {
                    val mDatasetResponse = DatasetResponse()
                    mDatasetResponse.model = mApplicationList[i].model
                    mDatasetResponse.make = mApplicationList[i].make
                    mDatasetResponse.makeText = mApplicationList[i].makeText
                    mModelList.add(mDatasetResponse)
                }
            }
            var pos = 0
            if (mModelList != null && mModelList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mModelList.size)
                for (i in mModelList.indices) {
                    mDropdownList[i] = mModelList[i].model.toString()
                    if (value != "") {
                        if (mModelList[i].make == value || mModelList[i].model == value) {
                            pos = i
                            try {
                                mEditTextViewTextViewModel!!.setText(mDropdownList[pos])
                                mSelectedModel = mModelList[pos].model
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
                //                Arrays.sort(mDropdownList);
                adapterModel = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_lpr_details_item,
                    mDropdownList
                )
                try {
                    mEditTextViewTextViewModel?.threshold = 1
                    mEditTextViewTextViewModel?.setAdapter<ArrayAdapter<String?>>(adapterModel)
                    mEditTextViewTextViewModel?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            mSelectedModel = mModelList[position].model
                            AppUtils.hideSoftKeyboard(this@BootActivity)
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    mEditTextViewTextViewModel?.setText("")
                    mEditTextViewTextViewModel?.setAdapter(null)
                    adapterModel!!.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle(value: String?) {
        //init array list
        try {
            val mApplicationList = Singleton.getDataSetList(DATASET_CAR_MAKE_LIST, mDb)
            val uniqueDataSet: MutableSet<String> = HashSet()
            if (uniqueDataSet == null || uniqueDataSet.size < 1) {
                if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                    for (i in mApplicationList.indices) {
                        uniqueDataSet.add(mApplicationList[i].make.toString() + "#" + mApplicationList[i].makeText.toString())
                    }
                }
            }
            val Geeks = uniqueDataSet.toTypedArray()
            var pos = 0
            Arrays.sort(Geeks)
            if (uniqueDataSet != null && uniqueDataSet.size > 0) {
                val mDropdownList = arrayOfNulls<String>(uniqueDataSet.size)
                for (i in Geeks.indices) {
                    mDropdownList[i] = Geeks[i].split("#").toTypedArray()[1]
                    if (value != "") {
                        val splitValue = Geeks[i].split("#").toTypedArray()
                        if (splitValue[0] == value || splitValue[1] == value) {
                            pos = i
                            mEditTextViewTextViewMake?.setText(splitValue[1])
                        }
                    }
                }
                //                Arrays.sort(mDropdownList);
                val adapter = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_lpr_details_item,
                    mDropdownList
                )
                try {
                    mEditTextViewTextViewMake?.threshold = 1
                    mEditTextViewTextViewMake?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mSelectedMake = Geeks[pos].split("#").toTypedArray()[0]
                    mSelectedMakeValue = Geeks[pos].split("#").toTypedArray()[1]
                    mEditTextViewTextViewMake?.onItemClickListener =
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
                                setDropdownVehicleModel(mSelectedMake)
                            } else {
                                setDropdownVehicleModel("")
                            }
                            AppUtils.hideSoftKeyboard(this@BootActivity)
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
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
        val mApplicationList = Singleton.getDataSetList(DATASET_CAR_COLOR_LIST, mDb)
        var pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].description.toString()
                if (value != "") {
                    if (mDropdownList[i] == value) {
                        pos = i
                        try {
                            mEditTextViewTextViewColor?.setText(mDropdownList[pos])
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_lpr_details_item,
                mDropdownList
            )
            try {
                mEditTextViewTextViewColor?.threshold = 1
                mEditTextViewTextViewColor?.setAdapter<ArrayAdapter<String?>>(adapter)
                mSelectedColor = mDropdownList[pos]
                mEditTextViewTextViewColor?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        mSelectedColor = mDropdownList[position]
                        AppUtils.hideSoftKeyboard(this@BootActivity)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownRemark(mApplicationList: List<DatasetResponse>?) {
        val addZoneValueInZeroIndex = DatasetResponse()
        Collections.sort(mApplicationList) { lhs, rhs ->
            lhs.remark.nullSafety().compareTo(rhs.remark.nullSafety())
        }
        val pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].remark.toString()
            }

//            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mEditTextViewTextViewRemark?.threshold = 1
                mEditTextViewTextViewRemark?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewRemark?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        AppUtils.hideSoftKeyboard(this@BootActivity)
                        mEditTextViewTextViewRemark?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
        }
    }

    //set value to side dropdown
    private fun setDropdownSide(mApplicationList: List<DatasetResponse>?) {
        var pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].sideName.toString()
                if (mApplicationList[i].sideName.equals(mSideItem, ignoreCase = true)) {
                    pos = i
                    try {
                        mEditTextViewTextViewSideOfState?.setText(mDropdownList[pos])
                    } catch (e: Exception) {
                    }
                }
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mEditTextViewTextViewSideOfState?.threshold = 1
                mEditTextViewTextViewSideOfState?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewSideOfState?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        AppUtils.hideSoftKeyboard(this@BootActivity)
                        mEditTextViewTextViewSideOfState?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
        }
    }



    //set value to Meter Name dropdown
    private fun setDropdownBlock(mApplicationList: List<DatasetResponse>?) {
//        hideSoftKeyboard(this@LprDetails2Activity)
        var pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].blockName.toString()
//                mAutoComTextViewBlock?.setText(mBlock)
                /*if (mApplicationList[i].blockName.equals(mBlock, ignoreCase = true)) {
                    pos = i
                    try {
                        mAutoComTextViewBlock?.setText(mDropdownList[pos])
                    } catch (e: Exception) {
                    }
                }*/
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mAutoComTextViewBlock?.threshold = 1
                mAutoComTextViewBlock?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mAutoComTextViewBlock?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> hideSoftKeyboard(this@BootActivity) }
                // listonly
                if (mAutoComTextViewBlock?.tag != null && mAutoComTextViewBlock?.tag == "listonly") {
                    setListOnly(this@BootActivity, mAutoComTextViewBlock!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*perform click actions*/
    @OnClick(R.id.btn_submit, R.id.btn_submit_issue, R.id.btn_submit_time)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btn_submit -> if (isFormValid()) {
                BluetoothPermissionUtil.checkBluetoothPermissions(
                    activity = this,
                    permissionLauncher = bluetoothPermissionLauncher
                ) {
                    btnClickedEvent = "Submit"
                    setRequest(btnClickedEvent)
                }
            }

            R.id.btn_submit_issue -> if (isFormValid()) {
                BluetoothPermissionUtil.checkBluetoothPermissions(
                    activity = this,
                    permissionLauncher = bluetoothPermissionLauncher
                ) {
                    btnClickedEvent = "Issue"
                    setRequest(btnClickedEvent)
                }

            }
            R.id.btn_submit_time -> if (isFormValid()) {
                BluetoothPermissionUtil.checkBluetoothPermissions(
                    activity = this,
                    permissionLauncher = bluetoothPermissionLauncher
                ) {
                    btnClickedEvent = "Time"
                    setRequest(btnClickedEvent)
                }
            }
        }
    }

    private fun printFinalNotice(printBootNoticeModel: PrintBootNoticeModel) {
        mainScope.launch {
            val (printerCommand, printHeight) = ZebraCommandPrintUtils.getPrintCommandForBootTow(
                context = this@BootActivity,
                printBootNoticeModel = printBootNoticeModel,
                //citationNumber = citationNumber,
                citationNumber = mCitationNumberId,
                violationDate = SDF_MM_DD_YYYY.format(Calendar.getInstance().time),
                lotName = lotNumber,
                spaceNumber = spaceName
            )

            zebraPrinterUseCase?.printFinalTowNoticeWithWhiteBackgroundBitmap(mBackgroundWhiteBitmap,
                printerCommand,
                printHeight
            )
        }
    }

    private fun isFormValid(): Boolean {
        //This variable is to check wheather we need lot & space valicate is required or not before executing anything
        val isLotAndSpaceValidationRequired = BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SEPTA,
            true
        )

        if (TextUtils.isEmpty(
                mTextViewLot?.text.toString().trim()
            ) && llLotNumber?.isVisible.nullSafety()
        ) {
            mTextViewLot?.requestFocus()
            mTextViewLot?.isFocusable = true
            mTextViewLot?.error = getString(R.string.val_msg_please_enter_lot)
            AppUtils.showKeyboard(this@BootActivity)
            return false
        }

        if (TextUtils.isEmpty(
                mTextViewSpace?.text.toString().trim()
            ) && llSpaceNumber?.isVisible.nullSafety()
        ) {
            mTextViewSpace?.requestFocus()
            mTextViewSpace?.isFocusable = true
            mTextViewSpace?.error = getString(R.string.val_msg_please_enter_space)
            AppUtils.showKeyboard(this@BootActivity)
            return false
        }

        if (TextUtils.isEmpty(mAutoComTextViewState?.text.toString().trim())) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mAutoComTextViewState?.requestFocus()
            mAutoComTextViewState?.isFocusable = true
            mAutoComTextViewState?.error = getString(R.string.val_msg_please_enter_state)
            AppUtils.showKeyboard(this@BootActivity)
            return false
        }
        if (TextUtils.isEmpty(
                mEditTextViewTextViewSideOfState?.text.toString().trim()
            ) && llSideOfStreet?.isVisible.nullSafety()
        ) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mEditTextViewTextViewSideOfState?.requestFocus()
            mEditTextViewTextViewSideOfState?.isFocusable = true
            mEditTextViewTextViewSideOfState?.error =
                getString(R.string.err_lbl_side_of_street)
            AppUtils.showKeyboard(this@BootActivity)
            return false
        }
        if (TextUtils.isEmpty(
                mEditTextViewTextViewStreet?.text.toString().trim()
            )
        ) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mEditTextViewTextViewStreet?.requestFocus()
            mEditTextViewTextViewStreet?.isFocusable = true
            mEditTextViewTextViewStreet?.error = getString(R.string.val_msg_please_enter_street)
            AppUtils.showKeyboard(this@BootActivity)
            return false
        }
        if (!appCompatCheckBoxHeavy?.isChecked.nullSafety() && !appCompatCheckBoxRegular?.isChecked.nullSafety()
            && !appCompatCheckBoxMedium?.isChecked.nullSafety() && !BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DUNCAN, true)&& !BuildConfig.FLAVOR.equals(
                DuncanBrandingApp13()) && linearLayoutCompatCheckBox?.isVisible.nullSafety()
        ) {
            LogUtil.printToastMSGForErrorWarning(
                applicationContext,
                getString(R.string.err_lbl_select_any_one)
            )
            AppUtils.showKeyboard(this@BootActivity)
            return false
        }
        return true
    }

    private fun setRequest(clickEvent : String) {
        if (isInternetAvailable(this@BootActivity)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val bootRequest = BootRequest()
            bootRequest.dispatchType = "boot"
            bootRequest.isOnGroundDispatch = false
            bootRequest.citationNumber = "";//mEditTextCitation?.text.toString()
            bootRequest.clientTimestamp = clientTime
            bootRequest.bootTowReason = "tow away zone"
            bootRequest.remarks = mEditTextViewTextViewRemark?.text.toString()
            if (appCompatCheckBoxRegular?.isChecked.nullSafety()) {
                bootRequest.bootTowType = "Regular"
            } else if (appCompatCheckBoxMedium?.isChecked.nullSafety()) {
                bootRequest.bootTowType = "Medium"
            } else if (appCompatCheckBoxHeavy?.isChecked.nullSafety()) {
                bootRequest.bootTowType = "Heavy"
            } else {
                bootRequest.bootTowType = "Regular"
            }
            val officerDetails = OfficerDetails()
            officerDetails.officerName = mWelcomeFormData?.officerFirstName + " " +
                    mWelcomeFormData?.officerLastName
            officerDetails.siteOfficerId = mWelcomeFormData?.siteOfficerId
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            //            officerDetails.setShift(mWelcomeFormData.getShift());
            officerDetails.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            officerDetails.squad = mWelcomeFormData?.officerSquad
            bootRequest.officerDetails = officerDetails
            val vehicleDetails = VehicleDetails()
            vehicleDetails.color = mEditTextViewTextViewColor?.text.toString()
            vehicleDetails.lpNumber = mEditTextCitation?.text.toString()
            vehicleDetails.make = mEditTextViewTextViewMake?.text.toString()
            vehicleDetails.model = mEditTextViewTextViewModel?.text.toString()
            //vehicleDetails.state = if (scofflawDataResponse != null) scofflawDataResponse?.state else ""
            //vehicleDetails.state = mStateItem
            vehicleDetails.state = mState2DigitCode
            bootRequest.vehicleDetails = vehicleDetails
            val violationDetails = ViolationDetails()
            violationDetails.violationCode = ""
            violationDetails.violationFine = 0.0
            violationDetails.violationDescription = ""
            bootRequest.violationDetails = violationDetails
            val locationDetails = LocationDetails()
            locationDetails.block = mAutoComTextViewBlock?.text.toString()
            locationDetails.latitude = mLat
            locationDetails.longitude = mLong
            locationDetails.side = mEditTextViewTextViewSideOfState?.text.toString()
            locationDetails.street = mEditTextViewTextViewStreet?.text.toString()
            bootRequest.locationDetails = locationDetails


            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)) {
                //Setting Print Layout Data for Notice print
                printBootNoticeModel.officerDetails = officerDetails
                printBootNoticeModel.vehicleDetails = vehicleDetails
                printBootNoticeModel.remarks = bootRequest.remarks

                //Setting up boot metadata request
                val bootMetadataRequest = BootMetadataRequest()
                val bootInstanceTicketRequest = BootInstanceTicketRequest()
                bootInstanceTicketRequest.bootType = API_CONSTANT_BOOT_TYPE_HANDHELD_INITIATED
                //bootInstanceTicketRequest.deploymentId = ""
                //bootInstanceTicketRequest.deviceId = ""
                bootInstanceTicketRequest.licensePlateNumber = mEditTextCitation?.text.toString()
                //bootInstanceTicketRequest.operatorId = ""
                bootInstanceTicketRequest.plateType = API_CONSTANT_PLATE_TYPE_PERSONAL
                //bootInstanceTicketRequest.releaseCode = ""
                bootInstanceTicketRequest.licensePlateState = mState2DigitCode
                //bootInstanceTicketRequest.notes = mEditTextViewTextViewNote?.text.toString()
                bootInstanceTicketRequest.notes = mEditTextViewTextViewRemark?.text.toString()
                //bootInstanceTicketRequest.ticketNo = citationNumber
                bootInstanceTicketRequest.ticketNo = mCitationNumberId

                bootMetadataRequest.bootMetadata = bootInstanceTicketRequest
                bootViewModel?.hitBootInstanceTicketAPI(bootMetadataRequest)
            } else {
                bootViewModel?.hitBootSubmitApi(bootRequest)
            }

//            if (BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_SEPTA,
//                    true
//                ) && (clickEvent.equals("Issue", true) || clickEvent.equals("Submit", true))
//            ) {
//
//                printFinalNotice(printBootNoticeModel)
//            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse?, tag: String) {
        when (apiResponse?.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.SUMIT_BOOT, ignoreCase = true)) {

                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ResponseBoot::class.java)


                                if (responseModel != null && responseModel.isSuccess) {
                                    try {
                                        apiResultTag = "success"
                                        AppUtils.showCustomAlertDialog(
                                            mContext,
                                            "Boot",
                                            "Submit boot successfully",
                                            getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel),
                                            this
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {


                                    val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ResponseBootError::class.java)


                                    apiResultTag = "fail"
                                    AppUtils.showCustomAlertDialog(
                                        mContext,
                                        "Boot API Failed",
                                        responseModel.description.nullSafety(getString(R.string.err_msg_something_went_wrong)),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                }
                            } catch (e: Exception) {


                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ResponseBootError::class.java)

                                apiResultTag = "fail"
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "Boot API Failed",
                                    responseModel.description.nullSafety(getString(R.string.err_msg_something_went_wrong)),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            }
                        }

                        if (tag.equals(
                                DynamicAPIPath.SUBMIT_BOOT_INSTANCE_TICKET,
                                ignoreCase = true
                            )
                        ) {
                            try {


                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), BootInstanceTicketResponse::class.java)

                                if (responseModel?.data != null && responseModel.status.nullSafety()) {
                                    try {
                                        //Update
                                        mDb?.dbDAO?.updateCitationBooklet(1, mCitationNumberId)

                                        //Printing the ticket on API success only
                                        printFinalNotice(printBootNoticeModel)

                                        apiResultTag = "success_for_boot_instance"
                                        AppUtils.showCustomAlertDialogWithPositiveButton(
                                            mContext,
                                            getString(R.string.err_title_api_successful),
                                            responseModel.message.nullSafety("Ticket Created Successfully"),
                                            getString(R.string.alt_lbl_OK),
                                            this
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {

                                    val responseErrorModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), BootInstanceTicketErrorResponse::class.java)


                                    apiResultTag = "fail_for_boot_instance"
                                    AppUtils.showCustomAlertDialogWithPositiveButton(
                                        mContext,
                                        getString(R.string.err_title_api_failed),
                                        (responseErrorModel?.data as String).nullOrEmptySafety(
                                            getString(R.string.err_msg_something_went_wrong)
                                        ),
                                        getString(R.string.alt_lbl_OK),
                                        this
                                    )
                                }
                            } catch (e: Exception) {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), BootInstanceTicketErrorResponse::class.java)

                                apiResultTag = "fail_for_boot_instance"
                                AppUtils.showCustomAlertDialogWithPositiveButton(
                                    mContext,
                                    getString(R.string.err_title_api_failed),
                                    (responseModel?.data as String).nullOrEmptySafety(responseModel.message.nullSafety(getString(R.string.err_msg_something_went_wrong))),
                                    getString(R.string.alt_lbl_OK),
                                    this
                                )
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_CITATION_NUMBER, ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CitationNumberResponse::class.java)

                            if (responseModel != null && responseModel.status!!) {
                                saveBookletWithStatus(responseModel.data!![0])
                            } else {
                                dismissLoader()
                                //lastSecondTag = ""
                                showCustomAlertDialog(
                                    mContext,
                                    APIConstant.POST_CITATION_NUMBER,
                                    responseModel.data!!.get(0)!!.metadata.toString(),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        apiResultTag = "fail"
                        AppUtils.showCustomAlertDialog(
                            mContext,
                            "Boot",
                            getString(R.string.err_msg_something_went_wrong),
                            getString(R.string.alt_lbl_OK),
                            getString(R.string.scr_btn_cancel),
                            this
                        )
                    }
                }
            }

            Status.ERROR -> {
                dismissLoader()
                if (tag.equals(DynamicAPIPath.SUMIT_BOOT, ignoreCase = true)) {
                    try {

                        if (apiResponse.error is HttpException) {
                            val errorBody = apiResponse.error.response()?.errorBody()
                            val errorJson = errorBody?.string()


                            val responseModel = ObjectMapperProvider.fromJson(errorJson.nullSafety(), ResponseBootError::class.java)


                            apiResultTag = "fail"
                            AppUtils.showCustomAlertDialog(
                                mContext,
                                "Boot API Failed",
                                responseModel.description.nullSafety(getString(R.string.err_msg_something_went_wrong)),
                                getString(R.string.alt_lbl_OK),
                                getString(R.string.scr_btn_cancel),
                                this
                            )
                        } else {
                            apiResultTag = "fail"
                            AppUtils.showCustomAlertDialog(
                                mContext,
                                "Boot",
                                getString(R.string.err_msg_something_went_wrong),
                                getString(R.string.alt_lbl_OK),
                                getString(R.string.scr_btn_cancel),
                                this
                            )
                        }
                    } catch (e: Exception) {
                        apiResultTag = "fail"
                        AppUtils.showCustomAlertDialog(
                            mContext,
                            "Boot",
                            getString(R.string.err_msg_something_went_wrong),
                            getString(R.string.alt_lbl_OK),
                            getString(R.string.scr_btn_cancel),
                            this
                        )
                    }
                } else {
                    apiResultTag = "fail"
                    AppUtils.showCustomAlertDialog(
                        mContext,
                        "Boot",
                        getString(R.string.err_msg_something_went_wrong),
                        getString(R.string.alt_lbl_OK),
                        getString(R.string.scr_btn_cancel),
                        this
                    )
                }
            }

            else -> {
            }
        }
    }


    private fun CheckKeyBoardView() {
        mEditTextViewTextViewRemark?.viewTreeObserver?.addOnGlobalLayoutListener {
            if (keyboardShown(mEditTextViewTextViewRemark?.rootView!!)) {
                mEditTextViewTextViewRemark?.isFocusable = true
                //                    viewBottom.setMinimumHeight((int) adjustableHeight*2);
                val params = viewBottom?.layoutParams
                params?.height = (adjustableHeight + 20).toInt() * 2
                viewBottom?.requestLayout()
            } else {
                val params = viewBottom?.layoutParams
                params?.height = 30
                viewBottom?.requestLayout()
            }
        }
    }

    /*private fun keyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        val heightDiff: Int = rootView.bottom - r.bottom
        adjustableHeight = softKeyboardHeight * dm.density
        return heightDiff > softKeyboardHeight * dm.density
    }*/
    override fun onYesButtonClick() {
        if (apiResultTag.equals("success", ignoreCase = true)) {
            if (btnClickedEvent.equals("Submit", ignoreCase = true)) {
                val intent = Intent(mContext, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                var mIntent: Intent? = null
                if (btnClickedEvent.equals("Issue", ignoreCase = true)) {
                    sharedPreference.writeOverTimeParkingTicketDetails(
                        SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                        AddTimingRequest()!!
                    )
                     mIntent = Intent(this@BootActivity, LprDetails2Activity::class.java)
                }else{
                     mIntent = Intent(this@BootActivity, AddTimeRecordActivity::class.java)
                }
                mIntent.putExtra(
                    "make",
                    mEditTextViewTextViewMake?.editableText.toString().trim()
                )
                mIntent.putExtra("from_scr", "BOOTACTIVITY")
                if (mSelectedModel != null) {
                    mIntent.putExtra(
                        "model",
                        mEditTextViewTextViewModel?.editableText.toString().trim()
                    )
                }
                mIntent.putExtra(
                    "color",
                    mEditTextViewTextViewColor?.editableText.toString().trim()
                )
                mIntent.putExtra(
                    "lpr_number",
                    mEditTextCitation?.text.toString().trim()
                )
                mIntent.putExtra(
                    "Street",
                    mEditTextViewTextViewStreet?.text.toString().trim()
                )
                mIntent.putExtra("SideItem", mEditTextViewTextViewSideOfState?.text.toString())
                mIntent.putExtra("Block", mAutoComTextViewBlock?.text.toString())
                mIntent.putExtra("State", mAutoComTextViewState?.text.toString())
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,ignoreCase = true))
                {
                    mIntent.putExtra("Remark", "")
                }else {
                    mIntent.putExtra("Remark", mEditTextViewTextViewRemark?.text.toString())
                }
                startActivity(mIntent)
            }
//            val intent = Intent(mContext, WelcomeActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun setLayoutVisibilityBasedOnSettingResponse() {
        try {
            var settingsList: List<DatasetResponse>? = ArrayList()
            settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())
            if (settingsList != null && settingsList.size > 0) {
                for (i in settingsList.indices) {
                    if (settingsList[i].type.equals("HAS_MODEL", ignoreCase = true)
                            && settingsList[i].mValue.equals("NO", ignoreCase = true)
                    ) {
                        linearLayoutCompatllModel!!.visibility = View.GONE
                    }
                    if (settingsList!![i].type.equals("DEFAULT_STATE", ignoreCase = true) && mStateItem.isEmpty()) {
                        mStateItem = settingsList[i].mValue!!
                    }
                }
            }
            setDropDowns()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            setDropDowns()
        }
    }

    //This function is used to show hide some view based on sites
    private fun setLayoutVisibilityBasedOnSite() {
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SEPTA,
                true
            )
        ) {
            llLotNumber?.showView()

            setDropdownForLotNumber()

            llSpaceNumber?.hideView()
            //setDropdownForSpace()

            llSideOfStreet?.hideView()
            linearLayoutCompatCheckBox?.hideView()
            //Need to hide these two buttons
            btnSubmitTime?.hideView()
            viewPrintDivider?.hideView()
            btnSubmit?.hideView()

            //Text Changes Needed
            btnSubmit?.setText(R.string.scr_btn_print)
            btnSubmitIssue?.setText(R.string.scr_btn_print_and_cite)
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13())) {
            linearLayoutCompatCheckBox?.visibility = View.GONE
        }
    }


    /**
     * Function used to set dropdown for lot number
     */
    private fun setDropdownForLotNumber() {
        if (mTextViewLot != null) {
            ioScope.launch {
                val mApplicationList = mDb?.let { Singleton.getDataSetList(DATASET_LOT_LIST, it) }

                if (!mApplicationList.isNullOrEmpty()) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].lot.toString()
                    }

                    mTextViewLot?.post {
                        val adapter = ArrayAdapter(
                            this@BootActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                        )
                        try {
                            mTextViewLot?.threshold = 1
                            mTextViewLot?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mTextViewLot?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    hideSoftKeyboard(this@BootActivity)

                                    mTextViewSpace?.error = null

                                    val indexOfLot = getIndexOfLot(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    lotNumber = mApplicationList[indexOfLot].lot.toString()


                                    val index = getIndexOfLocation(mApplicationList,
                                        parent.getItemAtPosition(position).toString())
                                    try {
                                        mAutoComTextViewBlock?.setText(mApplicationList[index].block.nullSafety())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    val currentString = mApplicationList[index].street.toString()
                                    val separated = currentString.split(" ").toTypedArray()
                                    mStreetItem =
                                        mApplicationList[index].street.toString()//separated[0]
                                    //mStreetItem = String.valueOf(mApplicationList.get(position).getStreet());
                                    setDropdownStreet(Singleton.getDataSetList(DATASET_STREET_LIST, mDb))
//                                    mDirectionItem = mApplicationList[index].direction.toString()
//                                    try {
//                                        mAutoComTextViewDirection?.setText(mApplicationList[index].direction.toString())
//                                    } catch (e: Exception) {
//                                        e.printStackTrace()
//                                    }
//                                    setDropdownDirection()
//                                    setDropdownZone(mAutoComTextViewLot?.text.toString())
//                                    try {
//                                        mViolationExtraAmount = mApplicationList[index].violation!!.toInt()
//                                        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
//                                            && mViolationListSelectedItem!=null) {
//                                            setViolationBaseData(mViolationListSelectedItem, pos)
//                                        }
//                                    } catch (e: Exception) {
//                                        e.printStackTrace()
//                                    }


                                }
                            if (mTextViewLot?.tag != null && mTextViewLot?.tag == "listonly") {
                                setListOnly(this@BootActivity, mTextViewLot!!)
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


    /**
     * Function used to set dropdown for space number
     */
    private fun setDropdownForSpace() {
        if (mTextViewSpace != null) {
            ioScope.launch {
                val mApplicationList = mDb?.let { Singleton.getDataSetList(DATASET_SPACE_LIST, it) }

                if (!mApplicationList.isNullOrEmpty()) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].spaceName.toString()
                    }
                    mTextViewSpace?.post {

                        val adapter = ArrayAdapter(
                            this@BootActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                        )
                        try {
                            mTextViewSpace?.threshold = 1
                            mTextViewSpace?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mTextViewSpace?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    hideSoftKeyboard(this@BootActivity)

                                    val index = getIndexOfSpaceName(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    spaceName = mApplicationList[index].spaceName.toString()
                                }
                            if (mTextViewSpace?.tag != null && mTextViewSpace?.tag == "listonly") {
                                setListOnly(this@BootActivity, mTextViewSpace!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    /*private fun CheckKeyBoardView() {
        mEditTextViewTextViewRemark?.viewTreeObserver?.addOnGlobalLayoutListener {
            if (keyboardShown(mEditTextViewTextViewRemark?.rootView!!)) {
                mEditTextViewTextViewRemark?.isFocusable = true
                //                    viewBottom.setMinimumHeight((int) adjustableHeight*2);
                val params = viewBottom?.layoutParams
                params?.height = (adjustableHeight + 20).toInt() * 2
                viewBottom?.requestLayout()
            } else {
                val params = viewBottom?.layoutParams
                params?.height = 30
                viewBottom?.requestLayout()
            }
        }
    }*/


    private fun keyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        val heightDiff: Int = rootView.bottom - r.bottom
        adjustableHeight = softKeyboardHeight * dm.density
        return heightDiff > softKeyboardHeight * dm.density
    }
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {
         if (apiResultTag.equals("success", ignoreCase = true)) {
            if (btnClickedEvent.equals("Submit", ignoreCase = true)) {
//                val intent = Intent(mContext, WelcomeActivity::class.java)
//                startActivity(intent)
                finish()
            } else {
                 var mIntent: Intent? = null
                if (btnClickedEvent.equals("Issue", ignoreCase = true)) {
                    sharedPreference.writeOverTimeParkingTicketDetails(
                        SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                        AddTimingRequest()!!
                    )
                    mIntent = Intent(this@BootActivity, LprDetails2Activity::class.java)

                mIntent.putExtra(
                    "make",
                    mEditTextViewTextViewMake?.editableText.toString().trim()
                )
                mIntent.putExtra("from_scr", "BOOTACTIVITY")
                if (mSelectedModel != null) {
                    mIntent.putExtra(
                        "model",
                        mEditTextViewTextViewModel?.editableText.toString().trim()
                    )
                }
                mIntent.putExtra(
                    "color",
                    mEditTextViewTextViewColor?.editableText.toString().trim()
                )
                mIntent.putExtra(
                    "lpr_number",
                    mEditTextCitation?.text.toString().trim()
                )
                mIntent.putExtra(
                    "Street",
                    mEditTextViewTextViewStreet?.text.toString().trim()
                )
                mIntent.putExtra("SideItem", mEditTextViewTextViewSideOfState?.text.toString())
                mIntent.putExtra("Block", mAutoComTextViewBlock?.text.toString())
                mIntent.putExtra("State", mAutoComTextViewState?.text.toString())
                mIntent.putExtra("address", mAutoComTextViewBlock?.text.toString()+"#"+mEditTextViewTextViewStreet?.text.toString().trim())
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,ignoreCase = true))
                {
                    mIntent.putExtra("Remark", "")
                }else {
                    mIntent.putExtra("Remark", mEditTextViewTextViewRemark?.text.toString())
                }
//                mIntent.putExtra("Remark", mEditTextViewTextViewRemark?.text.toString())
                startActivity(mIntent)
                }else{
//                    mIntent = Intent(this@BootActivity, AddTimeRecordActivity::class.java)
                    finish()
                }
            }
//            val intent = Intent(mContext, WelcomeActivity::class.java)
//            startActivity(intent)
        }
    }

    override fun onActionSuccess(errorMessage: String) {
        //TODO implementation is not required
    }

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }


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

    override fun onStop() {
        super.onStop()
        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            zebraPrinterUseCase?.disconnect()
        }
    }
}