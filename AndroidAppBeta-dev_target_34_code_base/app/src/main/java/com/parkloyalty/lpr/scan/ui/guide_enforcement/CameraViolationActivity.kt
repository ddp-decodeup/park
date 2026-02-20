package com.parkloyalty.lpr.scan.ui.guide_enforcement


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_DEFAULT_REGULATION_TIME_AUTO
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_IS_AUTO_TIMING
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetails2Activity
import com.parkloyalty.lpr.scan.ui.check_setup.license.LprScanActivity
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.*
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.getStartTDateForPhili
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.DATASET_LOT_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.INTENT_KEY_TIMING_IMAGES
import com.parkloyalty.lpr.scan.util.INTENT_KEY_TIMING_IMAGES_BASE64
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.launch
import java.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CameraViolationActivity: BaseActivity(), CustomDialogHelper {
    @BindView(R.id.rv_paybyspace)
    lateinit var recyclerViewPayBySpace: RecyclerView

    @BindView(R.id.et_zone)
    lateinit var mTextViewZone: AppCompatAutoCompleteTextView

    @BindView(R.id.input_text_zone)
    lateinit var textInputLayoutZone: TextInputLayout

    @BindView(R.id.txt_nodata)
    lateinit var appCompatTextViewNoData: AppCompatTextView

    @BindView(R.id.searchView)
    lateinit var mSearchView: SearchView

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
    private val collectionsItemList: MutableList<DataItemCameraViolationFeed>? = ArrayList()
    private var mTimingImages: MutableList<String> = ArrayList()
//    private var cameraViolationViewModel: CameraViolationViewModel? = null
    private val getCameraViolationViewModel: GetCameraViolationViewModel? by viewModels()

    private var unBinder : Unbinder? = null
    private var mZone = "CST"
    private var mHour = ""
    private var mLpNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_violation_guide_enforcement)
        unBinder = ButterKnife.bind(this)
        addObservers()
        init()
        setLayoutVisibilityBasedOnSettingResponse()
        setSearchView()
        /**
         * Citation form delete from stack and remove saved images
         */
        LprDetails2Activity.instanceLprDetails2Activity?.finish()
        mDb?.dbDAO?.deleteTempImages()

    }


    private val payBySpaceDataSetResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_CAMERA_VIOLATION_DATA_SET
        )
    }

    private fun addObservers() {
        getCameraViolationViewModel?.response?.observe(this, payBySpaceDataSetResponseObserver)
    }

    override fun removeObservers() {
        //super.removeObservers()
        getCameraViolationViewModel?.response?.removeObserver(payBySpaceDataSetResponseObserver)
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

        setCrossClearButton(context = this@CameraViolationActivity, textInputLayout = textInputLayoutZone, appCompatAutoCompleteTextView = mTextViewZone)
    }

    //set value to Status dropdown
    private fun setDropdownZone() {
        try {

            val mApplicationList = Singleton.getDataSetList(DATASET_LOT_LIST, getMyDatabase())
            val pos = 0
            try {
                Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                    override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                        return lhs?.location!!.nullSafety().compareTo(rhs?.location!!.nullSafety())
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }

                val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                for (i in mApplicationList?.indices!!) {
                    mDropdownList[i] = mApplicationList?.get(i)?.location.toString()
                }

                val adapter = ArrayAdapter(this, R.layout.row_dropdown_menu_popup_item,
                    mDropdownList)


                try {
                    mTextViewZone.threshold = 1
                    mTextViewZone.setAdapter<ArrayAdapter<String?>>(adapter)
                    //mSelectedShiftStat = mApplicationList.get(pos);
                    mTextViewZone.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            hideSoftKeyboard(this@CameraViolationActivity)
                            mLimit = 1
                            collectionsItemList?.clear()
                            callCameraViolationAPI()
                        }
                } catch (e: Exception) {
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapterForCameraFeed(mResList: List<DataItemCameraViolationFeed>?) {
        var payBySpaceAdapter: CameraViolationFeedAdapter? = null
        if (mResList != null && mResList != null && mResList.size > 0) {
            collectionsItemList?.addAll(mResList)
            appCompatTextViewNoData.setVisibility(View.GONE);
            recyclerViewPayBySpace.visibility = View.VISIBLE
            payBySpaceAdapter = CameraViolationFeedAdapter(
                mContext!!,
                collectionsItemList,
                object : CameraViolationFeedAdapter.ListItemSelectListener{
                    override fun onItemClick(position: Int, responseItem: DataItemCameraViolationFeed?) {

                        val inTime = responseItem?.inCarImageTimestamp.let {
                            "In Time " + AppUtils.formatDateCameraViolation(it.toString())
                        } ?: ""

                        ImageCache.base64Images.clear()
                        if (!responseItem?.outCarImage.isNullOrEmpty() && responseItem?.outCarImage!!.length > 100) {
                            ImageCache.base64Images.add(responseItem?.outCarImage ?: "")
                        }
                        if (!responseItem?.outPlateImage.isNullOrEmpty() && responseItem?.outPlateImage!!.length > 100) {
                            ImageCache.base64Images.add(responseItem?.outPlateImage ?: "")
                        }
                        if (!responseItem?.inCarImage.isNullOrEmpty() && responseItem?.inCarImage!!.length > 100) {
                            ImageCache.base64Images.add(responseItem?.inCarImage ?: "")
                        }
                        if (!responseItem?.inPlateImage.isNullOrEmpty() && responseItem?.inPlateImage!!.length > 100) {
                            ImageCache.base64Images.add(responseItem?.inPlateImage ?: "")
                        }


//                        sharedPreference.write(SharedPrefKey.PAY_BY_ZONE_SPACE, responseItem?.zoneId)
//                        mTimingImages.add(responseItem?.mediaFiles?.get(0)?.image!!)
                        val mIntent = Intent(mContext, LprDetails2Activity::class.java)
                        mIntent.putExtra("make",responseItem?.make?.toString() ?: "")
                        mIntent.putExtra("model", responseItem?.model?.toString() ?:"")
                        mIntent.putExtra("color", responseItem?.color?.toString() ?:"")
                        mIntent.putExtra("lpr_number", responseItem?.lpNumber?.toString() ?:"")
                        mIntent.putExtra("violation_code_camera", responseItem?.violationNumber?.toString() ?:"")
//                        mIntent.putExtra(INTENT_KEY_TIMING_IMAGES, mTimingImages as ArrayList<out Parcelable>)
                        mIntent.putExtra(INTENT_KEY_TIMING_IMAGES_BASE64, "YES")
                        mIntent.putExtra("address", "")
                        mIntent.putExtra("timing_record_value", "")
                        mIntent.putExtra("from_scr", "CAMERAFEEDVIOLATION")
                        mIntent.putExtra("timing_record_value_camera", inTime)
                        mIntent.putExtra("Lot", mTextViewZone?.text?.toString())
                        mIntent.putExtra("Direction", responseItem?.direction?.toString() ?:"")
//                        mIntent.putExtra("vinNumber", responseItem?.vin?.toString() ?:"")
//                        mIntent.putExtra("BodyStyle", responseItem?.bodyType?.toString() ?:"")
//                        mIntent.putExtra("ExpireYear_CameraViolation", responseItem?.vehicle?.vehicleYear?.toString() ?:"")
                        startActivity(mIntent)
                        finish()
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
                if (mResList!!.size > 0) {
                    //use to focus the item with index of 2nd last row
                    recyclerViewPayBySpace.scrollToPosition(mResList!!.size)
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
                                if (mResList!!.size > itemCount - 2 && mLimit < pageLimit) {
                                    Toast.makeText(
                                        applicationContext,
                                        "loading more data",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mLimit++
                                    callCameraViolationAPI()
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

    private fun setSearchView() {
        mSearchView.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mLpNumber = query
                collectionsItemList?.clear()
                callCameraViolationAPI()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                //filter(newText);
                mLpNumber = newText
                if(newText.length==0) {
                    collectionsItemList?.clear()
                    callCameraViolationAPI()
                }
                return false
            }

        })
        mSearchView.isHorizontalScrollBarEnabled = true
        mSearchView.queryHint = "Lp Number"
    }

    private fun callCameraViolationAPI() {
        //Returns current time in millis
        var timeMilli1=""
        var timeMilli2=""

        if(mHour.isNotEmpty()){
            timeMilli2 = AppUtils.getStartTDateWithSetting(mZone)
            timeMilli1 = AppUtils.getStartTDateAddSettingHourValue(mZone,mHour)
        }else{
            timeMilli1 = AppUtils.getStartTDate(mZone)
            timeMilli2 = AppUtils.getEndTDate(mZone)

        }
//        'https://devapi.parkloyalty.com/violations/unlinked_feeds?page=1&limit=100&time_from=2025-06-20T00%3A00%3A00Z&time_to=2025-06-20T11%3A59%3A00Z'
        var endPoint = ""
        if (!TextUtils.isEmpty(mTextViewZone.text.toString())
        ) {
            endPoint = ("zone=" + mTextViewZone.text.toString()
                    + "&lp_number=" + mLpNumber+ "&time_from="+ timeMilli1+ "&time_to="+ timeMilli2 + "&page=" + mLimit+"&limit=100")
            getCameraViolationViewModel!!.hitCameraViolationDataSetApi(endPoint)
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {

                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.GET_CAMERA_VIOLATION_DATA_SET, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CameraViolationResponse::class.java)

                            if (responseModel != null && responseModel!!.status==true) {
                                pageLimit = responseModel.limit!!
                                itemCount = responseModel.totalRecords!!
                                setAdapterForCameraFeed(responseModel!!.data?.filterNotNull())
                                loading = true
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