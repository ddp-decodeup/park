package com.parkloyalty.lpr.scan.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivitySettingsBinding
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SIDE_LIST
import com.parkloyalty.lpr.scan.util.LogUtil
import java.text.SimpleDateFormat
import java.util.*

class SettingActivity : BaseActivity() {
    private var mDb: AppDatabase? = null

    lateinit var mTextViewTimeZoneLabel: AppCompatTextView
    lateinit var mTextViewTimeZoneValue: AppCompatTextView
    lateinit var mLlinearLayoutCompatMain: LinearLayoutCompat
    lateinit var mAppCompatImageViewSetting: AppCompatImageView

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        init()
    }

    private fun findViewsByViewBinding() {
        mTextViewTimeZoneLabel = binding.layoutContentSettings.tvtimeZone
        mTextViewTimeZoneValue = binding.layoutContentSettings.mTextViewTimeZone
        mLlinearLayoutCompatMain = binding.layoutContentSettings.llMainRendringview
        mAppCompatImageViewSetting = binding.layoutContentSettings.appcomimgviewSetting
    }

    private fun setupClickListeners(){

    }

    private fun init() {
        mDb = BaseApplication.instance?.getAppDatabase()
        setToolbar()
        try {
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
            for (i in settingsList!!.indices) {
//                LogUtil.printLog("setting type",settingsList.get(i).getType());
                if (i == 0) {
                    mTextViewTimeZoneLabel!!.text = settingsList[i].type
                    mTextViewTimeZoneValue!!.text = settingsList[i].mValue
                } else {
                    val itemView =
                        layoutInflater.inflate(R.layout.content_setting_dynamic_row, null)
                    val linearLayout =
                        itemView.findViewById<View>(R.id.ll_child_row) as LinearLayoutCompat
                    val linearLayoutText =
                        itemView.findViewById<View>(R.id.ll_child_text) as LinearLayoutCompat
                    val mTextViewTimeZoneLabel =
                        itemView.findViewById<View>(R.id.tvtime_zone) as AppCompatTextView
                    val mTextViewTimeZoneLabelValue =
                        itemView.findViewById<View>(R.id.mTextViewTimeZone) as AppCompatTextView
                    mTextViewTimeZoneLabel.text = settingsList[i].type
                    mTextViewTimeZoneLabelValue.text = settingsList[i].mValue
                    mLlinearLayoutCompatMain!!.addView(itemView)

                }
                LogUtil.printLogHeader("Setting file ",settingsList[i].type+"  "+settingsList[i].mValue+" "+i)

            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            R.id.layOwnerBill
        )
    }

    override fun onResume() {
        super.onResume()
        registerBroadcastReceiver()
    }
}