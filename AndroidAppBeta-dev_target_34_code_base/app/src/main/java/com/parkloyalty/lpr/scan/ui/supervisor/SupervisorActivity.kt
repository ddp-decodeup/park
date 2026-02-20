package com.parkloyalty.lpr.scan.ui.supervisor

import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.supervisor.model.SupervisorViewModel
import com.parkloyalty.lpr.scan.util.LogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SupervisorActivity : BaseActivity(), CustomDialogHelper {

    @JvmField
    @BindView(R.id.recyclersuppervisor1)
    var recyclerViewSuppervisor1: RecyclerView? = null

    @JvmField
    @BindView(R.id.recyclersuppervisor2)
    var recyclerViewSuppervisor2: RecyclerView? = null

    @JvmField
    @BindView(R.id.recyclersuppervisor3)
    var recyclerViewSuppervisor3: RecyclerView? = null

    @JvmField
    @BindView(R.id.recyclersuppervisor4)
    var recyclerViewSuppervisor4: RecyclerView? = null

    @JvmField
    @BindView(R.id.ll_suppervisor1)
    var linearLayoutSuppervisor1: LinearLayout? = null

    @JvmField
    @BindView(R.id.ll_suppervisor2)
    var linearLayoutSuppervisor2: LinearLayout? = null

    @JvmField
    @BindView(R.id.ll_suppervisor3)
    var linearLayoutSuppervisor3: LinearLayout? = null

    @JvmField
    @BindView(R.id.ll_suppervisor4)
    var linearLayoutSuppervisor4: LinearLayout? = null

    private val supervisorViewModel: SupervisorViewModel? by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor)
        setFullScreenUI()
        ButterKnife.bind(this)

        addObservers()
        init()
        callAPI()
    }

    private fun init() {
        setToolbar()
        //        callAPI();
//        mDatasetList = mDb.getDbDAO().getDataset();
//        mWelcomeFormData = mDb.getDbDAO().getWelcomeForm();
//        getIntentData();
//        setDropDowns();
    }


    private val supervisorResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_SUPERVISOR
        )
    }

    private fun addObservers() {
        supervisorViewModel!!.response.observe(this, supervisorResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        supervisorViewModel!!.response.removeObserver(supervisorResponseObserver)
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
            R.id.layOwnerBill
        )
    }

    private fun callAPI() {
        if (isInternetAvailable(this@SupervisorActivity)) {
            val endPoint = "shift=" + sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            supervisorViewModel!!.hitSupervisorAPI(endPoint)
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
                        if (tag.equals(DynamicAPIPath.GET_SUPERVISOR, ignoreCase = true)) {
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
                LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
        }
    }

    //     private void setAdapterForGuideEnforecement(List<DataItemItem> mResList) {
    //        EnforecementAdapter mMusicAdapter = null;
    //        if(mResList!=null && mResList.size()>0) {
    //            appCompatTextViewNoData.setVisibility(View.GONE);
    //            recyclerViewEnforecement.setVisibility(View.VISIBLE);
    //             mMusicAdapter = new EnforecementAdapter(mContext, mResList, new EnforecementAdapter.ListItemSelectListener() {
    //                @Override
    //                public void onItemClick(LinearLayoutCompat rlRowMain, boolean mStatus, int position) {
    //
    //                }
    //            });
    //            recyclerViewEnforecement.setNestedScrollingEnabled(false);
    //            recyclerViewEnforecement.setHasFixedSize(true);
    //            recyclerViewEnforecement.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
    //            recyclerViewEnforecement.setAdapter(mMusicAdapter);
    //        }else {
    //            appCompatTextViewNoData.setVisibility(View.VISIBLE);
    //            recyclerViewEnforecement.setVisibility(View.GONE);
    //        }
    //    }
    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
}