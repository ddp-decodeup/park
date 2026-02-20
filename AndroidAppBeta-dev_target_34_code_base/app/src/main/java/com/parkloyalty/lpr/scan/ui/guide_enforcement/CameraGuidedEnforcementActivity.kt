package com.parkloyalty.lpr.scan.ui.guide_enforcement

import CameraGuidedEnforcementResponse
import CameraGuidedItem
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.Unbinder
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivityCameraGuidedEnforementBinding
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetails2Activity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.CameraGuidedEnforcementViewModel
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.ImageCache
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.INTENT_KEY_TIMING_IMAGES_BASE64
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraGuidedEnforcementActivity: BaseActivity(), CustomDialogHelper {
    // --- view binding ---
    private lateinit var binding: ActivityCameraGuidedEnforementBinding
    lateinit var recyclerViewPayBySpace: RecyclerView
    lateinit var mTextViewZone: AppCompatAutoCompleteTextView
    lateinit var textInputLayoutZone: TextInputLayout
    lateinit var appCompatTextViewNoData: AppCompatTextView
    lateinit var mSearchViewPlate: SearchView
    lateinit var mSearchViewSpace: SearchView

    private val collectionsItemList: MutableList<CameraGuidedItem>? = ArrayList()
    private val cameraGuidedEnforcementViewModel: CameraGuidedEnforcementViewModel? by viewModels()
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

    private var unBinder : Unbinder? = null
    private var mZone = "CST"
    private var mHour = ""
    private var mLpNumber = ""
    private var mSpaceumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraGuidedEnforementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        addObservers()
        init()
        setLayoutVisibilityBasedOnSettingResponse()
        setSearchView()
        /**
         * Citation form delete from stack and remove saved images
         */
        LprDetails2Activity.instanceLprDetails2Activity?.finish()
        mDb?.dbDAO?.deleteTempImages()

        callCameraViolationAPI()

    }

    private fun findViewsByViewBinding() {
        recyclerViewPayBySpace = binding.layoutContentCameraGuidedEnforcement.rvCameraGuidedEnforcement
        mTextViewZone = binding.layoutContentCameraGuidedEnforcement.etZone
        textInputLayoutZone = binding.layoutContentCameraGuidedEnforcement.inputTextZone
        appCompatTextViewNoData = binding.txtNodata
        mSearchViewPlate = binding.layoutContentCameraGuidedEnforcement.searchView
        mSearchViewSpace = binding.layoutContentCameraGuidedEnforcement.searchViewSpace
    }


    private val payBySpaceDataSetResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_CAMERA_GUIDED_ENFORCEMENT
        )
    }

    private fun addObservers() {
        cameraGuidedEnforcementViewModel?.response?.observe(this, payBySpaceDataSetResponseObserver)
    }

    override fun removeObservers() {
        //super.removeObservers()
        cameraGuidedEnforcementViewModel?.response?.removeObserver(payBySpaceDataSetResponseObserver)
    }

    private fun init() {
        mContext = applicationContext
        mDb = BaseApplication.instance?.getAppDatabase()
        //for full screen toolbar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setToolbar()

        setCrossClearButton(context = this@CameraGuidedEnforcementActivity, textInputLayout = textInputLayoutZone, appCompatAutoCompleteTextView = mTextViewZone)
    }



    private fun setAdapterForCameraGuided(mResList: List<CameraGuidedItem>?) {
        var cameraGuildEnforcementAdapter: CameraGuidedEnforcementAdapter? = null

        val safeList = mResList?.filterNotNull() ?: emptyList()

        if (safeList.isNotEmpty()) {
            collectionsItemList?.clear()
            collectionsItemList?.addAll(safeList)

            appCompatTextViewNoData.visibility = View.GONE
            recyclerViewPayBySpace.visibility = View.VISIBLE

            cameraGuildEnforcementAdapter = collectionsItemList?.let {
                CameraGuidedEnforcementAdapter(
                    mContext!!,
                    it,
                    object : CameraGuidedEnforcementAdapter.ListItemSelectListener {
                        override fun onItemClick(position: Int, responseItem: CameraGuidedItem?) {
                            val inTime = responseItem?.receivedTimestamp?.let {
                                "In Time " + AppUtils.formatDateCameraViolation(it.toString())
                            } ?: ""

                            ImageCache.base64Images.clear()
                            responseItem?.mediaFiles
                                ?.mapNotNull { it.image }  // take all image fields, skip nulls
                                ?.filter { it.isNotEmpty() } // skip empty strings
                                ?.forEach { ImageCache.base64Images.add(it) }

                            val mIntent = Intent(mContext, LprDetails2Activity::class.java).apply {
                                putExtra("make", responseItem?.vehicle?.make ?: "")
                                putExtra("model", responseItem?.vehicle?.model ?: "")
                                putExtra("color", responseItem?.vehicle?.color ?: "")
                                putExtra("lpr_number", responseItem?.vehicle?.plate ?: "")
                                putExtra("State", responseItem?.vehicle?.state ?: "")
                                putExtra("violation_code_camera", responseItem?.violationId ?: "")
                                putExtra(INTENT_KEY_TIMING_IMAGES_BASE64, "YES")
                                putExtra("address", "")
                                putExtra("timing_record_value", "")
                                putExtra("from_scr", "CAMERAFEEDVIOLATION")
                                putExtra("timing_record_value_camera", inTime)
                                putExtra("Lot", "")
                                putExtra("Direction", responseItem?.direction ?: "")
                            }

                            startActivity(mIntent)
                            finish()
                        }
                    }
                )
            }

            recyclerViewPayBySpace.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = cameraGuildEnforcementAdapter
            }

            if (mScrolledStatus && safeList.isNotEmpty()) {
                recyclerViewPayBySpace.scrollToPosition(safeList.size)
                cameraGuildEnforcementAdapter?.notifyDataSetChanged()
            }

            recyclerViewPayBySpace.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        mScrolledStatus = true
                    }

                    if (dy > 0) { // Scroll down
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()

                        if (loading && visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            if (safeList.size > itemCount - 2 && mLimit < pageLimit) {
                                Toast.makeText(applicationContext, "loading more data", Toast.LENGTH_SHORT).show()
                                mLimit++
                                callCameraViolationAPI()
                                loading = false
                            }
                        }
                    }
                }
            })
        } else {
            // Even if no data, still clear adapter for UI consistency
            collectionsItemList?.clear()
            recyclerViewPayBySpace.adapter?.notifyDataSetChanged()

            appCompatTextViewNoData.visibility = View.VISIBLE
            appCompatTextViewNoData.text = getString(R.string.err_msg_there_is_no_data_found)
            recyclerViewPayBySpace.visibility = View.GONE
        }
    }


    private fun setSearchView() {
        mSearchViewPlate.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        mSearchViewPlate.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mLpNumber = query
                collectionsItemList?.clear()
                callCameraViolationAPI()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mLpNumber = newText
                if(newText.length==0) {
                    collectionsItemList?.clear()
                    callCameraViolationAPI()
                }
                return false
            }

        })
        mSearchViewPlate.isHorizontalScrollBarEnabled = true
        mSearchViewPlate.queryHint = "Lp Number"


        mSearchViewSpace.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        mSearchViewSpace.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mSpaceumber = query
                collectionsItemList?.clear()
                callCameraViolationAPI()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mSpaceumber = newText
                if(newText.length==0) {
                    collectionsItemList?.clear()
                    callCameraViolationAPI()
                }
                return false
            }

        })
        mSearchViewSpace.isHorizontalScrollBarEnabled = true
        mSearchViewSpace.queryHint = "Space Number"
    }

    private fun callCameraViolationAPI() {
        val (timeFrom, timeTo) = AppUtils.getDateRange()
//            println("time_from=$timeFrom, time_to=$timeTo")

        cameraGuidedEnforcementViewModel!!.hitCameraGuidedEnforcementApi(
            is_violation = true,
            time_from = "$timeFrom",
            time_to = "$timeTo",
            plate_number = "$mLpNumber",
            space_number = "$mSpaceumber",
            page = mLimit.toString(),
            limit = "100")
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {

                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.GET_CAMERA_GUIDED_ENFORCEMENT, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CameraGuidedEnforcementResponse::class.java)

                            if (responseModel != null &&
                                (responseModel.data?.size ?: 0) > 0) {
                                pageLimit = responseModel.page!!
                                itemCount = responseModel.totalRecords!!
                                setAdapterForCameraGuided(responseModel!!.data?.filterNotNull())
                                loading = true
                            }else{
                                setAdapterForCameraGuided(emptyList())
                            }
                        }
                        dismissLoader()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dismissLoader()
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

    private fun setLayoutVisibilityBasedOnSettingResponse() {
        try {
            var settingsList: List<DatasetResponse>? = ArrayList()
            settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
            if (settingsList != null && settingsList!!.isNotEmpty()) {
                for (i in settingsList!!.indices) {
                    LogUtil.printLog("Setting",settingsList!![i].type.toString())

                    if (settingsList!![i].type.equals("TIMING_RECORD_LOOKUP_THRESHOLD",
                            ignoreCase = true )) {
                        try {
                            mHour = settingsList!![i].mValue.toString()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                }
            }
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