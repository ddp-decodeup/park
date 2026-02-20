package com.parkloyalty.lpr.scan.ui.guide_enforcement

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.DataItemItem
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.PayByPlateResponse
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.PayByPlateViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.ZoneStat
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class GuideEnforecementActivity : BaseActivity(), CustomDialogHelper {
    @BindView(R.id.rv_enforecemt)
    lateinit var recyclerViewEnforecement: RecyclerView

    @BindView(R.id.etStatus)
    lateinit var mTextViewFiltterZone: AppCompatAutoCompleteTextView

    @BindView(R.id.input_textStatus)
    lateinit var textInputLayoutStatus: TextInputLayout

    @BindView(R.id.searchView)
    lateinit var mSearchView: SearchView

    @BindView(R.id.txt_nodata)
    lateinit var appCompatTextViewNoData: AppCompatTextView
    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var mWelcomeFormData: WelcomeForm? = null
    private var mLpNumber = ""

    private val payByPlateViewModel: PayByPlateViewModel? by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_emforcement)
        setFullScreenUI()
        ButterKnife.bind(this)
        init()
    }

    private fun init() {
        try {
            mContext = this
            mDb = BaseApplication.instance?.getAppDatabase()
            mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
            addObservers()
            setDropdownZone(mTextViewFiltterZone)

            setSearchView()
            setToolbar()

            setCrossClearButton(context = this@GuideEnforecementActivity, textInputLayout = textInputLayoutStatus, appCompatAutoCompleteTextView = mTextViewFiltterZone)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val payByPlaceResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_PAY_BY_PLATE
        )
    }

    private fun addObservers() {
        payByPlateViewModel!!.response.observe(this, payByPlaceResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        payByPlateViewModel!!.response.removeObserver(payByPlaceResponseObserver)
    }

    private fun callPayByPlateAPI() {
//        {{local}}/analytics/mobile/pay_by_plate?zone=Indigo&Park&lp_number=VIM112
        var endPoint = ""
        endPoint = if (!TextUtils.isEmpty(mLpNumber) && mTextViewFiltterZone!!.text!!.isNotEmpty()) {
            ("zone=" + mTextViewFiltterZone.text.toString()
                    + "&lp_number=" + mLpNumber)
        } else if (!TextUtils.isEmpty(mLpNumber)){
            "lp_number=" + mLpNumber
        }
        else {
            "zone=" + mTextViewFiltterZone.text.toString()
        }
//        if (!mTextViewFiltterZone.text.toString().isEmpty()) {
            payByPlateViewModel!!.hitPayByPlateApi(endPoint)
//        }
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
                        if (tag.equals(DynamicAPIPath.GET_PAY_BY_PLATE, ignoreCase = true)) {
//                        if (tag.equalsIgnoreCase(DynamicAPIPath.POST_CANCEL)) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), PayByPlateResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                setAdapterForGuideEnforecement(responseModel.data as MutableList<DataItemItem>?)
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

    private fun setAdapterForGuideEnforecement(mResList: MutableList<DataItemItem>?) {
        var mMusicAdapter: EnforecementAdapter? = null
        if (mResList != null && mResList.size > 0) {
            appCompatTextViewNoData.visibility = View.GONE
            recyclerViewEnforecement.visibility = View.VISIBLE
            mMusicAdapter = EnforecementAdapter(
                mResList,
                object : EnforecementAdapter.ListItemSelectListener {
                    override fun onItemClick(
                        rlRowMain: LinearLayoutCompat?,
                        mStatus: Boolean,
                        position: Int
                    ) {
                    }
                })
            recyclerViewEnforecement.isNestedScrollingEnabled = false
            recyclerViewEnforecement.setHasFixedSize(true)
            recyclerViewEnforecement.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            recyclerViewEnforecement.adapter = mMusicAdapter
        } else {
            appCompatTextViewNoData.visibility = View.VISIBLE
            recyclerViewEnforecement.visibility = View.GONE
        }
    }

    //set value to Status dropdown
    private fun setDropdownZone(mEditTextStatus: AppCompatAutoCompleteTextView?) {
        try {
            val mWelcomeList = Singleton.getWelcomeDbObject(mDb);

            var mApplicationList: List<ZoneStat>?=null
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)) {
                    mApplicationList = if (mWelcomeList!!.welcomeList!!.zoneStats != null) mWelcomeList.welcomeList!!.zoneStats else mWelcomeList.welcomeList!!.pbcZoneStats
                }else  {
                    mApplicationList = if (mWelcomeList!!.welcomeList!!.pbcZoneStats != null) mWelcomeList.welcomeList!!.pbcZoneStats else mWelcomeList.welcomeList!!.zoneStats
                }
            val pos = 0
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].zoneName.toString()
                }
                val adapter = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mDropdownList
                )
                try {
                    mEditTextStatus!!.threshold = 1
                    mEditTextStatus.setAdapter<ArrayAdapter<String?>>(adapter)
                    //mSelectedShiftStat = mApplicationList.get(pos);
                    mEditTextStatus.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            hideSoftKeyboard(this@GuideEnforecementActivity)
                            //                        mTextViewFiltterZone.setText(mDropdownList[position]);
                            callPayByPlateAPI()
                        }
                } catch (e: Exception) {
                }
            } else {
            }
            mEditTextStatus!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Your action on done
                    callPayByPlateAPI()
                    return@OnEditorActionListener true
                }
                false
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSearchView() {
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mLpNumber = query
                callPayByPlateAPI()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                //filter(newText);
                mLpNumber = newText
                if(newText.length>3) {
                    callPayByPlateAPI()
                }
                return false
            }

        })
        mSearchView.isHorizontalScrollBarEnabled = true
        mSearchView.queryHint = "Lp Number"
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }
}