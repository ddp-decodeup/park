package com.parkloyalty.lpr.scan.ui.guide_enforcement

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.ui.boot.model.BootViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ScofflawDataResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.INTENT_KEY_CITATION_NUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_COLOR
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPNUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MAKE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MODEL
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCCOFFLAW
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VIOLATION_DATE
import java.util.ArrayList
import kotlin.getValue

class CameraFeedFormActivity: BaseActivity(), CustomDialogHelper {
    @JvmField
    @BindView(R.id.input_officer_name)
    var mTextInputOfficerName: TextInputLayout? = null

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
    private var mCitationNumberModel: CitationNumberModel? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_feed_form)
        setFullScreenUI()
        ButterKnife.bind(this)

        addObservers()
        init()
    }
    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        setToolbar()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        getIntentData()
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

    private fun addObservers() {
        bootViewModel?.response?.observe(this, bootResponseObserver)

    }

    override fun removeObservers() {
        super.removeObservers()
        bootViewModel?.response?.removeObserver(bootResponseObserver)
    }

    private val bootResponseObserver = Observer { apiResponse: ApiResponse? ->
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

}