package com.parkloyalty.lpr.scan.ui.guide_enforcement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.license.LprScanActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.*
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import java.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PayBySpaceActivity : BaseActivity(), CustomDialogHelper {
    @BindView(R.id.rv_paybyspace)
    lateinit var recyclerViewPayBySpace: RecyclerView

    @BindView(R.id.et_lot)
    lateinit var mTextViewLot: AppCompatAutoCompleteTextView

    @BindView(R.id.et_zone)
    lateinit var mTextViewZone: AppCompatAutoCompleteTextView

    @BindView(R.id.input_textlot)
    lateinit var textInputLayoutLot: TextInputLayout

    @BindView(R.id.input_text_zone)
    lateinit var textInputLayoutZone: TextInputLayout

    @BindView(R.id.txt_nodata)
    lateinit var appCompatTextViewNoData: AppCompatTextView

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

    private val payByPlateViewModel: PayBySpaceViewModel? by viewModels()
    private val payBySpaceDataSetViewModel: PayBySpaceDataSetViewModel? by viewModels()

    private var unBinder : Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_by_space)
        unBinder = ButterKnife.bind(this)
        addObservers()
        init()
    }


    private val payByPlateResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_PAY_BY_SPACE
        )
    }
    private val payBySpaceDataSetResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_PAY_BY_SPACE_DATA_SET
        )
    }

    private fun addObservers() {
        payByPlateViewModel?.response?.observe(this , payByPlateResponseObserver)
        payBySpaceDataSetViewModel?.response?.observe(this, payBySpaceDataSetResponseObserver)
    }

    override fun removeObservers() {
        //super.removeObservers()
        payByPlateViewModel?.response?.removeObserver(payByPlateResponseObserver)
        payBySpaceDataSetViewModel?.response?.removeObserver(payBySpaceDataSetResponseObserver)
    }

    private fun init() {
        mContext = applicationContext
        mDb = BaseApplication.instance?.getAppDatabase()
        //for full screen toolbar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setToolbar()
        setDropdownZone()
        callPayBySpaceDataSetAPI()
//        callPayBySpaceAPI()
        //        getIntentData();

        setCrossClearButton(context = this@PayBySpaceActivity, textInputLayout = textInputLayoutLot, appCompatAutoCompleteTextView = mTextViewLot)
        setCrossClearButton(context = this@PayBySpaceActivity, textInputLayout = textInputLayoutZone, appCompatAutoCompleteTextView = mTextViewZone)
    }

    //set value to Status dropdown
    private fun setDropdownZone() {
        try {
            val mWelcomeList = Singleton.getWelcomeDbObject(mDb);
            val mApplicationList =
                if (mWelcomeList!!.welcomeList!!.pbcZoneStats != null) mWelcomeList.welcomeList!!.pbcZoneStats else mWelcomeList.welcomeList!!.zoneStats
            val pos = 0
            Collections.sort(mApplicationList) { lhs, rhs -> lhs.zoneName!!.compareTo(rhs.zoneName!!) }
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].zoneName.toString()
                }
                val adapter = ArrayAdapter(this, R.layout.row_dropdown_menu_popup_item,
                    mDropdownList)
                try {
                    mTextViewZone.threshold = 1
                    mTextViewZone.setAdapter<ArrayAdapter<String?>>(adapter)
                    //mSelectedShiftStat = mApplicationList.get(pos);
                    mTextViewZone.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            hideSoftKeyboard(this@PayBySpaceActivity)
                            mLimit = 1
                            collectionsItemList.clear()
                            //                            sharedPreference.getInstance(PayBySpaceActivity.this).write(SharedPrefKey.PAY_BY_ZONE_SPACE, mTextViewZone.getText().toString());
                            callPayBySpaceAPI()
                            //                        mTextViewFiltterZone.setText(mDropdownList[position]);
//                            callPayByPlateAPI();
                        }
                } catch (e: Exception) {
                }
            } else {
            }

//            mEditTextStatus.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
//                    if(actionId == EditorInfo.IME_ACTION_DONE){
//                        // Your action on done
//                        callPayByPlateAPI();
//                        return true;
//                    }
//                    return false;
//                }
//            });
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to Lot dropdown
    private fun setDropdownSpaceId(spaceCollectionsDataSet: List<SpaceCollectionsDataSetItem>?) {
        val pos = 0
        if (spaceCollectionsDataSet != null && spaceCollectionsDataSet.size > 0) {
            val mDropdownList = arrayOfNulls<String>(spaceCollectionsDataSet.size)
            for (i in spaceCollectionsDataSet.indices) {
//                mDropdownList[i] = String.valueOf(mApplicationList.get(i).getLot());//TODO TIN
                mDropdownList[i] = spaceCollectionsDataSet[i].spaceId.toString()
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mTextViewLot.threshold = 1
                mTextViewLot.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mTextViewLot.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        try {
                            mLimit = 1
                            collectionsItemList.clear()
                            callPayBySpaceAPI()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            } catch (e: Exception) {
            }
        }
    }

    private fun setAdapterForPayBySpace(mResList: SpaceData?) {
        var payBySpaceAdapter: PayBySpaceAdapter? = null
        if (mResList != null && mResList.spaceCollections != null && mResList.spaceCollections!!.size > 0) {
            collectionsItemList.addAll(mResList.response!!)
            appCompatTextViewNoData.setVisibility(View.GONE);
            recyclerViewPayBySpace.visibility = View.VISIBLE
            payBySpaceAdapter = PayBySpaceAdapter(
                mContext!!,
                collectionsItemList,
                object : PayBySpaceAdapter.ListItemSelectListener{
                    override fun onItemClick(position: Int, responseItem: ResponseItem?) {
                        //                    CitationInsurranceDatabaseModel databaseModel = new CitationInsurranceDatabaseModel();
//                    CitationVoilationModel voilationModel = new CitationVoilationModel();
//                    CitationVehicleModel vehicleModel = new CitationVehicleModel();
//                    CitationOfficerModel officerModel = new CitationOfficerModel();
//                    CitationLocationModel locationModel = new CitationLocationModel();
//                    CitationIssuranceModel issuranceModel = new CitationIssuranceModel();
//                    Intent mIntent = new Intent(mContext, LprDetails2Activity.class);
////                    mIntent.putExtra("ticket_number", String.valueOf(mDataTicket.getTicketNo()));
////                    mIntent.putExtra("ticket_date", String.valueOf(splitDate(mDataTicket.getCitationIssueTimestamp())));
//                    sharedPreference.getInstance(mContext).write(SharedPrefKey.isReissueTicket, "false");
//
//                    issuranceModel.setOfficer(officerModel);
//                    issuranceModel.setOfficer(officerModel);
//                    issuranceModel.setVehicle(vehicleModel);
//                    issuranceModel.setVoilation(voilationModel);
//                    issuranceModel.setLocation(locationModel);
//                    databaseModel.setCitationData(issuranceModel);
//
//                    sharedPreference.getInstance(PayBySpaceActivity.this).write(SharedPrefKey.CITATION_DATAL,databaseModel);
//
//                    startActivity(mIntent);


//                    Intent mIntent = new Intent(mContext, LprDetails2Activity.class);
                        sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, responseItem?.zone)
                        val mIntent = Intent(mContext, LprScanActivity::class.java)
                        mIntent.putExtra("make", "")
                        mIntent.putExtra("model", "")
                        mIntent.putExtra("color", "")
                        mIntent.putExtra("lpr_number", "")
                        mIntent.putExtra("violation_code", "")
                        mIntent.putExtra("address", "")
                        mIntent.putExtra("timing_record_value", "")
                        mIntent.putExtra("from_scr", "PAYBYSPACE")
                        mIntent.putExtra("Lot", responseItem?.lot)
                        mIntent.putExtra("Location", responseItem?.location)
                        mIntent.putExtra("Space_id", responseItem?.spaceId)
                        mIntent.putExtra("Meter", responseItem?.meter)
                        mIntent.putExtra("Zone", responseItem?.zone)
                        mIntent.putExtra("Street", responseItem?.street)
                        mIntent.putExtra("Block", responseItem?.block)
                        mIntent.putExtra("Direction", responseItem?.direction)
                        startActivity(mIntent)
                    }
                }
            )

            recyclerViewPayBySpace.isNestedScrollingEnabled = false
            recyclerViewPayBySpace.setHasFixedSize(true)
            //            recyclerViewPayBySpace.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            val mLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            recyclerViewPayBySpace.layoutManager = mLayoutManager
            recyclerViewPayBySpace.adapter = payBySpaceAdapter
            if (mScrolledStatus) {
                if (mResList.spaceCollections!!.size > 0) {
                    //use to focus the item with index of 2nd last row
                    recyclerViewPayBySpace.scrollToPosition(mResList.spaceCollections!!.size)
                } else {
                    //mRecylerViewHistory.scrollToPosition(mResList.size() - 1);
                }
                payBySpaceAdapter.notifyDataSetChanged()
            }
            recyclerViewPayBySpace.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            recyclerViewPayBySpace.visibility = View.GONE
        }
    }

    private fun callPayBySpaceAPI() {
//        {{local}}/analytics/mobile/pay_by_plate?zone=Indigo&Park&lp_number=VIM112
        var endPoint = ""
        if (!TextUtils.isEmpty(mTextViewLot.text.toString()) &&
            !TextUtils.isEmpty(mTextViewZone.text.toString())
        ) {
            endPoint = ("zone=" + mTextViewZone.text.toString()
                    + "&space_id=" + mTextViewLot.text.toString()
                    + "&from_ts=2022-01-31T06:00:00Z" + "&page=" + mLimit)
            payByPlateViewModel!!.hitPayBySpaceeApi(endPoint)
        } else if (!TextUtils.isEmpty(mTextViewLot.text.toString())) {
            endPoint = ("space_id=" + mTextViewLot.text.toString()
                    + "&from_ts=2022-01-31T06:00:00Z" + "&page=" + mLimit)
            payByPlateViewModel!!.hitPayBySpaceeApi(endPoint)
        } else if (!TextUtils.isEmpty(mTextViewZone.text.toString())) {
            endPoint = ("zone=" + mTextViewZone.text.toString()
                    + "&from_ts=2022-01-31T06:00:00Z" + "&page=" + mLimit)
            payByPlateViewModel!!.hitPayBySpaceeApi(endPoint)
        } else {
            endPoint = "from_ts=2022-01-31T06:00:00Z&page=$mLimit"
            payByPlateViewModel!!.hitPayBySpaceeApi(endPoint)
        }
    }

    private fun callPayBySpaceDataSetAPI() {
//        {{local}}/analytics/mobile/pay_by_plate?zone=Indigo&Park&lp_number=VIM112
        var endPoint = ""
        if (!TextUtils.isEmpty(mTextViewZone.text.toString())) {
            endPoint = "zone=" + mTextViewZone.text.toString()
            payBySpaceDataSetViewModel?.hitPayBySpaceDataSetApi(endPoint)
        } else {
            payBySpaceDataSetViewModel?.hitPayBySpaceDataSetApi("")
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
                        if (tag.equals(DynamicAPIPath.GET_PAY_BY_SPACE, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), PayBySpaceResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                pageLimit = responseModel.data!![0].data!!.totalPages
                                itemCount = responseModel.data!![0].data!!.recordCount
                                setAdapterForPayBySpace(responseModel.data!![0].data)
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
                        } else if (tag.equals(
                                DynamicAPIPath.GET_PAY_BY_SPACE_DATA_SET,
                                ignoreCase = true
                            )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), PayBySpaceDataSetResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                if (responseModel.payBySpacedataSetArray != null && responseModel.payBySpacedataSetArray!!.size > 0 && responseModel.payBySpacedataSetArray!![0].payBySpacedataSetObject != null && responseModel.payBySpacedataSetArray!![0].payBySpacedataSetObject!!.spaceCollectionsDataSet != null) {
//                                    Collections.sort(responseModel.payBySpacedataSetArray!![0].payBySpacedataSetObject!!.spaceCollectionsDataSet) { lhs, rhs ->
//                                        lhs.spaceId.nullSafety().compareTo(rhs.spaceId.nullSafety())
//                                    }

                                    Collections.sort(responseModel.payBySpacedataSetArray!![0].payBySpacedataSetObject!!.spaceCollectionsDataSet, object : Comparator<SpaceCollectionsDataSetItem?> {
                                        override fun compare(u1: SpaceCollectionsDataSetItem?, u2: SpaceCollectionsDataSetItem?): Int {
                                            return u1?.spaceId.nullSafety().compareTo(u2?.spaceId.nullSafety())
                                        }
                                    })


                                    setDropdownSpaceId(responseModel.payBySpacedataSetArray!![0].payBySpacedataSetObject!!.spaceCollectionsDataSet)
                                }
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

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
    private fun setData() {}

    override fun onDestroy() {
        unBinder?.unbind()
        mContext = null
        super.onDestroy()
    }
}