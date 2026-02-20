package com.parkloyalty.lpr.scan.ui.check_setup.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivityCheckSetupBinding
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.*
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.CheckSetData
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.CheckSetupRequest
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.CheckSetupResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.CountArrayTimeline
import com.parkloyalty.lpr.scan.ui.check_setup.model.CheckSetupViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.DATASET_METER_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_STREET_LIST
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.setAsAccessibilityHeading
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.getValue

@AndroidEntryPoint
class CheckSetupActivity : BaseActivity(), CustomDialogHelper {

    var mTextViewScofflawCount: AppCompatTextView? = null
    var mTextViewPaymentCount: AppCompatTextView? = null
    var mTextViewStolenCount: AppCompatTextView? = null
    var mTextViewTimingCount: AppCompatTextView? = null
    var mTextViewExtemptCount: AppCompatTextView? = null
    var mTextViewPermitCount: AppCompatTextView? = null
    var mTextViewScofflawDate: AppCompatTextView? = null
    var mTextViewPaymentDate: AppCompatTextView? = null
    var mTextViewStolenDate: AppCompatTextView? = null
    var mTextViewTimingDate: AppCompatTextView? = null
    var mTextViewExtemptDate: AppCompatTextView? = null
    var mTextViewPermitDate: AppCompatTextView? = null
    var mTextViewZoneListCount: AppCompatTextView? = null
    var mTextViewStreetListCount: AppCompatTextView? = null
    var mTextViewMeterListCount: AppCompatTextView? = null
    lateinit var tvCheckSetup: AppCompatTextView
    lateinit var llScofflaw: LinearLayoutCompat
    lateinit var llActivePayment: LinearLayoutCompat
    lateinit var llStolenVehicles: LinearLayoutCompat
    lateinit var llTimingRecords: LinearLayoutCompat
    lateinit var llExempt: LinearLayoutCompat
    lateinit var llPermit: LinearLayoutCompat

    private lateinit var binding: ActivityCheckSetupBinding

    private val mCheckSetupViewModel: CheckSetupViewModel? by viewModels()

    private var mDate: String? = null
    private var mDb: AppDatabase? = null
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        mContext = this@CheckSetupActivity
        mDb = BaseApplication.instance?.getAppDatabase()
        addObservers()
        init()
        setToolbar()
        setListCount()
    }

    private fun findViewsByViewBinding() {
        mTextViewScofflawCount = binding.layoutContentCheckSetup.tvScofflawCount
        mTextViewPaymentCount = binding.layoutContentCheckSetup.tvPaymentCount
        mTextViewStolenCount = binding.layoutContentCheckSetup.tvStolenCount
        mTextViewTimingCount = binding.layoutContentCheckSetup.tvTimingCount
        mTextViewExtemptCount = binding.layoutContentCheckSetup.tvExtemptCount
        mTextViewPermitCount = binding.layoutContentCheckSetup.tvPermitCount
        mTextViewScofflawDate = binding.layoutContentCheckSetup.tvScofflawDate
        mTextViewPaymentDate = binding.layoutContentCheckSetup.tvPaymentDate
        mTextViewStolenDate = binding.layoutContentCheckSetup.tvStolenDate
        mTextViewTimingDate = binding.layoutContentCheckSetup.tvTimingDate
        mTextViewExtemptDate = binding.layoutContentCheckSetup.tvExtemptDate
        mTextViewPermitDate = binding.layoutContentCheckSetup.tvPermitDate
        mTextViewZoneListCount = binding.layoutContentCheckSetup.zonelistcount
        mTextViewStreetListCount = binding.layoutContentCheckSetup.streetlistcount
        mTextViewMeterListCount = binding.layoutContentCheckSetup.meterlistcount
        tvCheckSetup = binding.layoutContentCheckSetup.tvCheckSetup
        llScofflaw = binding.layoutContentCheckSetup.llScofflaw
        llActivePayment = binding.layoutContentCheckSetup.llActivePayment
        llStolenVehicles = binding.layoutContentCheckSetup.llStolenVehicles
        llTimingRecords = binding.layoutContentCheckSetup.llTimingRecords
        llExempt = binding.layoutContentCheckSetup.llExempt
        llPermit = binding.layoutContentCheckSetup.llPermit
    }

    private fun setupClickListeners() {
        binding.layoutContentCheckSetup.btnRefresh.setOnClickListener {
            callCheckSetupApi()
        }
    }

    private fun init() {
        callCheckSetupApi()
        mEventStartTimeStamp = AppUtils.getDateTime()
    }

    fun setAccessibilityForComponents() {
        setAsAccessibilityHeading(tvCheckSetup)

        llScofflaw.contentDescription =
            "${getString(R.string.scr_lbl_scofflaw)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewScofflawCount?.text} ${getString(R.string.pause_in_talkback_long)} ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewScofflawDate?.text}"

        llActivePayment.contentDescription =
            "${getString(R.string.scr_lbl_active_payment)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewPaymentCount?.text} ${getString(R.string.pause_in_talkback_long)} ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewPaymentDate?.text}"

        llStolenVehicles.contentDescription =
            "${getString(R.string.scr_lbl_stolen_vehicles)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewStolenCount?.text} ${getString(R.string.pause_in_talkback_long)}  ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewStolenDate?.text}"

        llTimingRecords.contentDescription =
            "${getString(R.string.scr_lbl_timing_records)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewTimingCount?.text} ${getString(R.string.pause_in_talkback_long)}  ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewTimingDate?.text}"

        llExempt.contentDescription =
            "${getString(R.string.scr_lbl_exempt)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewExtemptCount?.text} ${getString(R.string.pause_in_talkback_long)}  ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewExtemptDate?.text}"

        llPermit.contentDescription =
            "${getString(R.string.scr_btn_permit)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewPermitCount?.text} ${getString(R.string.pause_in_talkback_long)}  ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewPermitDate?.text}"

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

    private val checkSetupResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_CHECK_SETUP
        )
    }

    private fun addObservers() {
        mCheckSetupViewModel?.response?.observe(this, checkSetupResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mCheckSetupViewModel?.response?.removeObserver(checkSetupResponseObserver)
    }

    /* Call Api For Check setup */
    private fun callCheckSetupApi() {
        if (isInternetAvailable(this@CheckSetupActivity)) {
            var mZone = "CST"
            if (Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase()) != null) {
                mZone = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())!![0].mValue.nullSafety()
            }
            val timeMilli1 = AppUtils.getStartT(mZone)
            val timeMilli2 = AppUtils.getEndT(mZone)
            val checkSetupRequest = CheckSetupRequest()
            val countArrayTimeline = CountArrayTimeline()
            countArrayTimeline.tz = "America/Los_Angeles"
            countArrayTimeline.timestampStart = timeMilli1
            countArrayTimeline.timestampEnd = timeMilli2
            checkSetupRequest.countArrayTimeline = countArrayTimeline
            mCheckSetupViewModel!!.hitCheckSetupApi(checkSetupRequest)
        } else {
            LogUtil.printToastMSGForErrorWarning(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun splitsFormat(Date: String?): String {
        val separated = Date!!.split("-").toTypedArray()
        val YYYY = separated[0]
        val MM = separated[1]
        val DD = separated[2]
        val calendar = Calendar.getInstance()
        calendar[Calendar.MONTH] = MM.toInt()
        val simpleDateFormat = SimpleDateFormat("MMM")
        simpleDateFormat.calendar = calendar
        val monthName = simpleDateFormat.format(calendar.time)
        return MM + "-" + DD + "-" + YYYY.substring(2, 4)
    }

    private fun getHours(hours: String): String? {
        val separated = hours.split(":").toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvert(`val`)
    }

    fun splitDate(date: String): String? {
        return try {
            val separated = date.split("T").toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            getHours(`val`)
        } catch (e: Exception) {
            date
        }
    }

    private fun dateConvert(`val`: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            val _24HourDt = _24HourSDF.parse(`val`)
            return splitsFormat(mDate) + " " + _12HourSDF.format(_24HourDt)
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun checkSetupDetails(mCheckSetupData: CheckSetData) {
        try {
            mTextViewScofflawCount?.text = mCheckSetupData.scofflawData?.totalRecords.toString()
            mTextViewScofflawDate?.text =
                splitDate(mCheckSetupData.scofflawData?.lastModified.toString())
            mTextViewPaymentCount?.text = mCheckSetupData.paymentData?.totalRecords.toString()
            mTextViewPaymentDate?.text =
                splitDate(mCheckSetupData.paymentData?.lastModified.toString())
            mTextViewStolenCount?.text = mCheckSetupData.stolenData?.totalRecords.toString()
            mTextViewStolenDate?.text = splitDate(mCheckSetupData.stolenData?.lastModified.toString())
            mTextViewTimingCount?.text = mCheckSetupData.timingData?.totalRecords.toString()
            mTextViewTimingDate?.text = splitDate(mCheckSetupData.timingData?.lastModified.toString())
            mTextViewExtemptCount?.text = mCheckSetupData.exemptData?.totalRecords.toString()
            mTextViewExtemptDate?.text =
                splitDate(mCheckSetupData.exemptData?.lastModified.toString())
            mTextViewPermitCount?.text = mCheckSetupData.permitData?.totalRecords.toString()
            mTextViewPermitDate?.text =
                splitDate(mCheckSetupData.permitData?.lastModified.toString())


            setAccessibilityForComponents()

        } catch (e: Exception) {
            e.printStackTrace()
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
                        if (tag.equals(DynamicAPIPath.GET_CHECK_SETUP, ignoreCase = true)) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CheckSetupResponse::class.java)

                            if (responseModel != null && responseModel.status!!) {
                                // LogUtil.printToastMSG(CheckSetupActivity.this, responseModel.getMessage());
                                checkSetupDetails(responseModel.data!![0])
                            } else if (responseModel != null && !responseModel.status!!) {
                                val message: String?
                                if (responseModel.response != null && responseModel.response != "") {
                                    message = responseModel.response
                                    AppUtils.showCustomAlertDialog(
                                        mContext, getString(R.string.scr_lbl_check_setup),
                                        message, getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel), this
                                    )
                                } else {
                                    responseModel.response =
                                        getString(R.string.err_msg_something_went_wrong)
                                    message = responseModel.response
                                    AppUtils.showCustomAlertDialog(
                                        mContext, getString(R.string.scr_lbl_check_setup),
                                        message, getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel), this
                                    )
                                }
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    getString(R.string.scr_lbl_check_setup),
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                                dismissLoader()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //token expires
                        dismissLoader()
                        logout(this)
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                LogUtil.printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
        }
    }

    override fun onBackPressed() {
        if (backpressCloseDrawer()) {
            // close activity when drawer is closed
            super.onBackPressed()
        }
    }

    private fun setListCount()
    {
        ioScope.launch {
            try {
                val mStreetListCount=Singleton.getDataSetList(DATASET_STREET_LIST, mDb)
                val mMeterListCount=Singleton.getDataSetList(DATASET_METER_LIST, mDb)
                val mWelcomeList = Singleton.getWelcomeDbObject(mDb)
                mTextViewMeterListCount?.post {
                    mTextViewMeterListCount?.setText(" "+if(mMeterListCount!=null)mMeterListCount!!.size else "0")

                }
                mTextViewStreetListCount?.post {
                    mTextViewStreetListCount?.setText(" "+if(mStreetListCount!=null )mStreetListCount!!.size else "0")

                }
                mTextViewZoneListCount?.post {
                    mTextViewZoneListCount?.setText(" "+if(mWelcomeList!!.welcomeList!!.zoneStats!=null) mWelcomeList!!.welcomeList!!.zoneStats!!.size else "0")

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
}