package com.parkloyalty.lpr.scan.ui.guide_enforcement

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivityDirectedEnforcementBinding
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.network.mock.MockService
import com.parkloyalty.lpr.scan.pagination.ListPaginationUtils
import com.parkloyalty.lpr.scan.ui.check_setup.license.LprScanActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.GeneticHitListData
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.GeneticHitListViewModel
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.GeneticHitListViewModelMockup
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.GetGeneticHitListResponse
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.ImageCache
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TYPE_OF_HITS_ALL
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlin.getValue

class DirectedEnforcementActivity : BaseActivity(), CustomDialogHelper {
    // --- view binding ---
    private lateinit var binding: ActivityDirectedEnforcementBinding
    private lateinit var textInputLayoutTypeOfHit: TextInputLayout
    private lateinit var autoCompleteTextViewTypeOfHit: AppCompatAutoCompleteTextView
    private lateinit var searchViewLicensePlateNumber: SearchView
    private lateinit var rvDirectedEnforcement: RecyclerView
    private lateinit var tvNoDataView: AppCompatTextView
    private var directedEnforcementListAdapter: DirectedEnforcementListAdapter? = null


    // --- dependencies / database / vm ---
    private var mContext: Context? = null
    private var mDb: AppDatabase? = null

    private val geneticHitListViewModel: GeneticHitListViewModel? by viewModels()
    private val geneticHitListViewModelMockUp: GeneticHitListViewModelMockup? by viewModels()

    // --- adapter / data ---
    private val geneticHitListData: MutableList<GeneticHitListData> = ArrayList()

    // --- pagination state ---
    private lateinit var listPaginationUtils: ListPaginationUtils

    // --- filters / inputs ---
    private var selectedTypeOfHit: String = ""
    private var licensePlateNumber: String = ""

    // Observer
    private val geneticHitListResponseObserver = Observer<ApiResponse> { apiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.GET_GENETIC_HIT_LIST)
    }

    // inside DirectedEnforcementActivity
    private val useMock = false     // toggle mock vs real
    private lateinit var mockService: MockService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectedEnforcementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        addObservers()
        init()
    }

    private fun findViewsByViewBinding() {
        textInputLayoutTypeOfHit =
            binding.layoutContentDirectedEnforcementLayout.textInputLayoutTypeOfHit
        autoCompleteTextViewTypeOfHit =
            binding.layoutContentDirectedEnforcementLayout.autoCompleteTextViewTypeOfHit
        searchViewLicensePlateNumber =
            binding.layoutContentDirectedEnforcementLayout.searchViewLicensePlateNumber
        rvDirectedEnforcement = binding.layoutContentDirectedEnforcementLayout.rvDirectedEnforcement
        tvNoDataView = binding.tvNoDataView
    }

    private fun setupClickListeners() {

    }

    private fun addObservers() {

        if (useMock) {
            geneticHitListViewModelMockUp?.response?.observe(this, geneticHitListResponseObserver)
        } else {
            geneticHitListViewModel?.response?.observe(this, geneticHitListResponseObserver)
        }

    }

    override fun removeObservers() {
        if (useMock) {
            geneticHitListViewModelMockUp?.response?.removeObserver(geneticHitListResponseObserver)
        } else {
            geneticHitListViewModel?.response?.removeObserver(geneticHitListResponseObserver)
        }
    }

    private fun init() {
        mContext = applicationContext
        mDb = BaseApplication.instance?.getAppDatabase()
        //for full screen toolbar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setToolbar()
        setTypeOfHitDropDown()
        setSearchViewForLicensePlateNumber()
        setAdapterForGeneticHitList()
        setCrossClearButton(
            context = this@DirectedEnforcementActivity,
            textInputLayout = textInputLayoutTypeOfHit,
            appCompatAutoCompleteTextView = autoCompleteTextViewTypeOfHit
        )

        listPaginationUtils =
            ListPaginationUtils(rvDirectedEnforcement, geneticHitListData as ArrayList<Any?>)
        listPaginationUtils.setOnListPaginationListener(object :
            ListPaginationUtils.ListPaginationListener {
            override fun onDataLoading(index: Int) {
                callGetGeneticGitListAPI()
            }

            override fun onFinishDataLoading() {

            }

        })

        //listPaginationUtils.startPaginationLoading()


        // ---- Select default ----
        val defaultIndex = 0 // e.g., first option "Scofflaw"
        selectedTypeOfHit = Singleton.getTypeOfHitsForGenetic().getOrElse(defaultIndex) { "" }
        autoCompleteTextViewTypeOfHit.setText(
            selectedTypeOfHit,
            false
        ) // set text without filtering

        // Trigger the callback manually
        autoCompleteTextViewTypeOfHit.post {
            autoCompleteTextViewTypeOfHit.onItemClickListener?.onItemClick(
                null,
                null,
                defaultIndex,
                defaultIndex.toLong()
            )
        }
    }

    private fun setTypeOfHitDropDown() {
        try {
            val typeOfHitsList = Singleton.getTypeOfHitsForGenetic()

            if (typeOfHitsList.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    this, R.layout.row_dropdown_menu_popup_item,
                    typeOfHitsList
                )
                try {
                    autoCompleteTextViewTypeOfHit.threshold = 1
                    autoCompleteTextViewTypeOfHit.setAdapter(adapter)
                    autoCompleteTextViewTypeOfHit.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            hideSoftKeyboard(this@DirectedEnforcementActivity)
                            selectedTypeOfHit = typeOfHitsList[position]

                            listPaginationUtils.resetPaginationLoading()
                            listPaginationUtils.startPaginationLoading()
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSearchViewForLicensePlateNumber() {
        searchViewLicensePlateNumber.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        searchViewLicensePlateNumber.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                licensePlateNumber = query
                listPaginationUtils.resetPaginationLoading()
                listPaginationUtils.startPaginationLoading()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                licensePlateNumber = newText
                if (newText.isEmpty()) {
                    listPaginationUtils.resetPaginationLoading()
                    listPaginationUtils.startPaginationLoading()
                }
                return false
            }
        })
        searchViewLicensePlateNumber.isHorizontalScrollBarEnabled = true
    }

    private fun setAdapterForGeneticHitList() {
        directedEnforcementListAdapter = DirectedEnforcementListAdapter(
            mContext!!,
            geneticHitListData,
            object : DirectedEnforcementListAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int, geneticHitListData: GeneticHitListData?) {
                        ImageCache.baseLinkImage?.clear()
                        val mIntent = Intent(mContext, LprScanActivity::class.java)
                        mIntent.putExtra("make", "")
                        mIntent.putExtra("model", "")
                        mIntent.putExtra("color", "")
                        mIntent.putExtra("lpr_number", geneticHitListData?.lpNumber)
                        mIntent.putExtra("state", geneticHitListData?.lpState)
                        mIntent.putExtra("violation_code", "")
                        mIntent.putExtra("address", "")
                        mIntent.putExtra("Street", geneticHitListData?.street)
                        mIntent.putExtra("type_of_hit", geneticHitListData?.typeOfHit)
                        mIntent.putExtra("timing_record_value", "")
                        mIntent.putExtra("from_scr", Constants.DIRECTED_ENFORCEMENT)
                        // Add all images safely
                        geneticHitListData?.images?.let { images ->
                        if (images.isNotEmpty()) {
                            ImageCache.baseLinkImage.addAll(images)
                        }
                    }
                        startActivity(mIntent)
                }
            }
        )
//        rvDirectedEnforcement.isNestedScrollingEnabled = false
//        rvDirectedEnforcement.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        rvDirectedEnforcement.layoutManager = mLayoutManager
        rvDirectedEnforcement.adapter = directedEnforcementListAdapter
    }

    private fun callGetGeneticGitListAPI() {
        val selectedTypeOfHitsForAPI =
            if (selectedTypeOfHit.equals(API_CONSTANT_TYPE_OF_HITS_ALL, true)) {
                ""
            } else {
                selectedTypeOfHit
            }

        if (useMock) {
            geneticHitListViewModelMockUp?.hitGetGeneticHitListApi(
                typeOfHit = selectedTypeOfHitsForAPI,
                lprNumber = licensePlateNumber,
                page = listPaginationUtils.getCurrentPageIndex().toString(),
                limit = listPaginationUtils.getPerPageCount().toString()
            )

        } else {
            geneticHitListViewModel?.hitGetGeneticHitListApi(
                typeOfHit = selectedTypeOfHitsForAPI,
                lprNumber = licensePlateNumber,
                page = listPaginationUtils.getCurrentPageIndex().toString(),
                limit = listPaginationUtils.getPerPageCount().toString()
            )
        }
    }

    /*Api response */
    @SuppressLint("NotifyDataSetChanged")
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> {
                if (listPaginationUtils.getCurrentPageIndex() == 1) {
                    showProgressLoader(getString(R.string.scr_message_please_wait))
                }
            }

            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.GET_GENETIC_HIT_LIST, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), GetGeneticHitListResponse::class.java)

                            if (responseModel != null && responseModel.status.nullSafety()) {
                                if (listPaginationUtils.getCurrentPageIndex() == 1) {
                                    geneticHitListData.clear()
                                }

                                listPaginationUtils.setTotalDataCount(responseModel.totalCount.nullSafety())
                                listPaginationUtils.completeAPICall()

                                geneticHitListData.addAll(responseModel.data)
                                directedEnforcementListAdapter?.updateList(geneticHitListData)
                                directedEnforcementListAdapter?.notifyDataSetChanged()

                                if (geneticHitListData.isEmpty()) {
                                    tvNoDataView.showView()
                                    tvNoDataView.text = getString(
                                        R.string.errMsgNoDataFoundForGeneticEnforcement,
                                        selectedTypeOfHit
                                    )
                                } else {
                                    tvNoDataView.hideView()
                                }
                            } else {
                                showCustomAlertDialog(
                                    mContext,
                                    APIConstant.GET_GENETIC_HIT_LIST,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }
}