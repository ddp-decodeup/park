package com.parkloyalty.lpr.scan.ui.guide_enforcement

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.databinding.ActivityGeneticHitListBinding
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.GeneticHitListResponse
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.GeneticHitListViewModel
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.ResponseItem
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.SpaceData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlin.getValue

class GeneticHitListActivity  : BaseActivity(), CustomDialogHelper {

    lateinit var drawerLy: DrawerLayout
    lateinit var recyclerViewGeneticHitList: RecyclerView
    lateinit var mTextViewGeneticHitType: AppCompatAutoCompleteTextView
    lateinit var textInputLayoutGeneticHit: TextInputLayout
    lateinit var appCompatTextViewNoData: AppCompatTextView

    private val mActiveInactiveList = arrayOf("Permit", "Timing","Scofflaw")

    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var completedVisibleCount = 0
    private var loading = true
    private var mLimit = 1
    private var pageLimit = 0
    private var itemCount = 0
    var mScrolledStatus = false
    private val collectionsItemList: MutableList<ResponseItem> = ArrayList()

    private val geneticHitListViewModel: GeneticHitListViewModel? by viewModels()

    private lateinit var binding: ActivityGeneticHitListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_genetic_hit_list)
        binding = ActivityGeneticHitListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        addObservers()
        init()
    }

    private val payByPlateResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_GENETIC_HIT_LIST
        )
    }

    private fun addObservers() {
        geneticHitListViewModel?.response?.observe(this , payByPlateResponseObserver)
    }

    override fun removeObservers() {
        //super.removeObservers()
        geneticHitListViewModel?.response?.removeObserver(payByPlateResponseObserver)
    }

    private fun init() {
        mContext = applicationContext
        mDb = BaseApplication.instance?.getAppDatabase()
        //for full screen toolbar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setToolbar()
        setDropdownGeneticHitList()

        setCrossClearButton(context = this@GeneticHitListActivity, textInputLayout = textInputLayoutGeneticHit, appCompatAutoCompleteTextView = mTextViewGeneticHitType)
    }

    //set value to Status dropdown
    private fun setDropdownGeneticHitList() {
        try {
            val mApplicationList = mActiveInactiveList
            val pos = 0
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].toString()
                }
                val adapter = ArrayAdapter(this, R.layout.row_dropdown_menu_popup_item,
                    mDropdownList)
                try {
                    mTextViewGeneticHitType.threshold = 1
                    mTextViewGeneticHitType.setAdapter<ArrayAdapter<String?>>(adapter)
                    mTextViewGeneticHitType.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, view, position, id ->
                            hideSoftKeyboard(this@GeneticHitListActivity)
                            mLimit = 1
                            collectionsItemList.clear()
                            callPayBySpaceAPI()
                        }
                } catch (e: Exception) {
                }
            } else {
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapterForPayBySpace(mResList: SpaceData?) {
        var payBySpaceAdapter: PayBySpaceAdapter? = null
        if (mResList != null && mResList.spaceCollections != null && mResList.spaceCollections!!.size > 0) {
            collectionsItemList.addAll(mResList.response!!)
            appCompatTextViewNoData.setVisibility(View.GONE);
            recyclerViewGeneticHitList.visibility = View.VISIBLE
            payBySpaceAdapter = PayBySpaceAdapter(
                mContext!!,
                collectionsItemList,
                object : PayBySpaceAdapter.ListItemSelectListener{
                    override fun onItemClick(position: Int, responseItem: ResponseItem?) {

                    }
                }
            )

            recyclerViewGeneticHitList.isNestedScrollingEnabled = false
            recyclerViewGeneticHitList.setHasFixedSize(true)
            //            recyclerViewPayBySpace.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            val mLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            recyclerViewGeneticHitList.layoutManager = mLayoutManager
            recyclerViewGeneticHitList.adapter = payBySpaceAdapter
            if (mScrolledStatus) {
                if (mResList.spaceCollections!!.size > 0) {
                    //use to focus the item with index of 2nd last row
                    recyclerViewGeneticHitList.scrollToPosition(mResList.spaceCollections!!.size)
                } else {
                    //mRecylerViewHistory.scrollToPosition(mResList.size() - 1);
                }
                payBySpaceAdapter.notifyDataSetChanged()
            }
            recyclerViewGeneticHitList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        mScrolledStatus = true
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        //scrolled to TOP
                        //showProgressBar();
                        //callBookFilterApi(mPre);
                    }
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = mLayoutManager.childCount
                        completedVisibleCount =
                            mLayoutManager.findLastCompletelyVisibleItemPosition()
                        totalItemCount = mLayoutManager.itemCount
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
                        printLog(
                            "vinod",
                            "$visibleItemCount   $completedVisibleCount  $totalItemCount  $pastVisiblesItems"
                        )
                        if (loading) {
                            if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                                if (mResList.spaceCollections!!.size > itemCount - 2 && mLimit < pageLimit) {
                                    Toast.makeText(
                                        applicationContext,
                                        "loading more data",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mLimit++
                                    //                                        payByPlateViewModel.hitPayBySpaceeApi("");
                                    callPayBySpaceAPI()
                                    loading = false
                                }
                                // Do pagination.. i.e. fetch new data
                            }
                        }
                    }
                }
            })
        } else {
            appCompatTextViewNoData.visibility = View.VISIBLE
            appCompatTextViewNoData.text = getString(R.string.err_msg_there_is_no_data_found)
            recyclerViewGeneticHitList.visibility = View.GONE
        }
    }

    private fun callPayBySpaceAPI() {
//        {{local}}/analytics/mobile/pay_by_plate?zone=Indigo&Park&lp_number=VIM112
        var endPoint = ""
        if (!TextUtils.isEmpty(mTextViewGeneticHitType.text.toString())
        ) {
            endPoint = ("zone=" + mTextViewGeneticHitType.text.toString()
                    + "&from_ts=2022-01-31T06:00:00Z" + "&page=" + mLimit)
//            geneticHitListViewModel!!.hitGeneticHitListApi(endPoint)
        } else if (!TextUtils.isEmpty(mTextViewGeneticHitType.text.toString())) {
            endPoint = ("zone=" + mTextViewGeneticHitType.text.toString()
                    + "&from_ts=2022-01-31T06:00:00Z" + "&page=" + mLimit)
//            geneticHitListViewModel!!.hitGeneticHitListApi(endPoint)
        } else {
            endPoint = "from_ts=2022-01-31T06:00:00Z&page=$mLimit"
//            geneticHitListViewModel!!.hitGeneticHitListApi(endPoint)
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
                        if (tag.equals(DynamicAPIPath.GET_GENETIC_HIT_LIST, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), GeneticHitListResponse::class.java)

                            if (responseModel != null && responseModel.status == true) {
//                                pageLimit = responseModel.data!![0].data!!.totalPages
//                                itemCount = responseModel.data!![0].data!!.recordCount
//                                setAdapterForPayBySpace(responseModel.data!![0].data)
                                loading = true
                            } else {
                                showCustomAlertDialog(
                                    mContext,
                                    APIConstant.GET_Pay_By_Plate,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
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
                printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
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
            R.id.layOwnerBill
        )
    }

    private fun findViewsByViewBinding() {
        drawerLy = binding.drawerLy
        textInputLayoutGeneticHit = binding.layoutContentGeneticHitList.inputTextGeneticHit    // TextInputLayout
        mTextViewGeneticHitType = binding.layoutContentGeneticHitList.etGeneticType            // AutoCompleteTextView
        recyclerViewGeneticHitList = binding.layoutContentGeneticHitList.rvGeneticHit   // RecyclerView
    }
    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
    private fun setData() {}

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }
}