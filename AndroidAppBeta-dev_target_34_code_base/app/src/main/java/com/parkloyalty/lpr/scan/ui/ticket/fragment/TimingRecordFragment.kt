package com.parkloyalty.lpr.scan.ui.ticket.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.basecontrol.BaseFragment
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.*
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.*
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.pagination.ListPaginationUtils
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetails2Activity
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.TimingAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimigMarkViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimingMarkData
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimingMarkResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.TimingMarkBulkRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.TimingMarkBulkResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.TimingMarkBulkViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.ticket.SearchActivity
import com.parkloyalty.lpr.scan.ui.ticket.model.UpdateMarkRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.UpdateMarkResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.UpdateMarkViewModel
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPRTime
import com.parkloyalty.lpr.scan.util.DATASET_REGULATION_TIME_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SIDE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_STREET_LIST
import com.parkloyalty.lpr.scan.util.INTENT_KEY_TIMING_IMAGES
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * A simple [Fragment] subclass.
 * Use the [TimingRecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TimingRecordFragment : BaseFragment(), CustomDialogHelper {
    private var mContext: Context? = null

    @JvmField
    @BindView(R.id.rvHistory)
    var mRecylerViewHistory: RecyclerView? = null

    @JvmField
    @BindView(R.id.searchView)
    var mSearchView: SearchView? = null

    @JvmField
    @BindView(R.id.ivFilter)
    var mImageViewFilter: AppCompatImageView? = null

    @JvmField
    @BindView(R.id.ll_search_1)
    var mLinearLayoutCompatSearch1: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_1)
    var mTextInputSearch1: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_1)
    var mEditTextSearch1: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.ll_search_2)
    var mLinearLayoutCompatSearch2: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_2)
    var mTextInputSearch2: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_2)
    var mEditTextSearch2: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.ll_search_3)
    var mLinearLayoutCompatSearch3: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_3)
    var mTextInputSearch3: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_3)
    var mEditTextSearch3: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.ll_search_4)
    var mLinearLayoutCompatSearch4: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_4)
    var mTextInputSearch4: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_4)
    var mEditTextSearch4: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.ll_search_5)
    var mLinearLayoutCompatSearch5: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_5)
    var mTextInputSearch5: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_5)
    var mEditTextSearch5: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.tvCountItemSelected)
    var tvCountItemSelected: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.tvSelectAndUnselectAll)
    var tvSelectAndUnselectAll: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.act_markgoa)
    var tvMarkGoa: AppCompatTextView? = null


    private var mDb: AppDatabase? = null
    private var mReqURL: String? = null
    private var searchViewQuery = ""
    private var siteOfficerID  = ""
    private var mDate: String? = null
    private val mResList: List<TimingMarkData> = ArrayList()
    private var mMusicAdapter: TimingAdapter? = null
    private var type = 0
    private var indexAPI = 1
    private var mLimit = 0
    private var loading = true
    var pastVisiblesItems = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private val mListTiming: MutableList<TimingMarkData> = ArrayList()
    private val TypeString = StringBuilder(1000)
    private val arrPackage: HashMap<Int, Boolean>? = HashMap(6)
    private var mTimingRecordRemarkValue: String? = ""
    private var mTimingTireStem: String? = ""
    val formatter = DecimalFormat("00")
    private var isTireStemWithImageView = false
    private var mRegulationTime = ""
    private var mRegulationTimeValue = ""

    private val mTimigMarkViewModel: TimigMarkViewModel? by viewModels()
    private val mTimingMarkBulkViewModel: TimingMarkBulkViewModel? by viewModels()
    private val mUpdateMarkViewModel: UpdateMarkViewModel? by viewModels()

    private lateinit var listPaginationUtils : ListPaginationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.content_timing_record, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObservers()
        mDb = BaseApplication.instance?.getAppDatabase()
        getTimingRecordURL()
        setTimingAdapterExternal()
        //callGetTimingApi(mReqURL)
        // below line is to call set on query text listener method.
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewQuery = query;
                createURLBeforeCallingAPI(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
//                filter(newText);
                searchViewQuery = newText
                if(newText.isEmpty())
                {
                    indexAPI = 1
                    createURLBeforeCallingAPI(searchViewQuery)
                }
                return false
            }
        })

        //open dialog
        arrPackage!![0] = false
        arrPackage[1] = false
        arrPackage[2] = false
        arrPackage[3] = false
        arrPackage[4] = false
        arrPackage[5] = false
        showSelectFilterDialog(1)
        mSearchView?.queryHint = "lpr Number"

        // true if you want to submit, otherwise false
        mSearchView?.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        mSearchView?.clearFocus()

        //default block and street visible and set selected in popup
        val isValueBlock = arrPackage!![0]!!
        arrPackage[0] = !isValueBlock
        mTextInputSearch1!!.hint = "block"
        mEditTextSearch1?.imeOptions = EditorInfo.IME_ACTION_DONE
        editTextViewActionButtonClick()

        val isValueStreet = arrPackage!![1]!!
        arrPackage[1] = !isValueStreet
        mTextInputSearch2!!.hint = "Street"
        mEditTextSearch2?.imeOptions = EditorInfo.IME_ACTION_NONE
        mEditTextSearch2?.let { setDropdownStreet("", it) }
        Util.setFieldCaps(activity, mEditTextSearch2!!)
        manipulateUIBasedOnCheckedTimingItem()

        listPaginationUtils = ListPaginationUtils(mRecylerViewHistory!!,mListTiming as ArrayList<Any?>)
        listPaginationUtils.setOnListPaginationListener(object : ListPaginationUtils.ListPaginationListener{
            override fun onDataLoading(index: Int) {
                ioScope.launch {
                    //mListTiming.clear()
                    indexAPI = index
                    //totalRecordCount = 0
                    var mtypeBlock = ""
                    var mtypeStreet = ""
                    var mtypeTimingType = ""
                    var mtypeStatus = ""
                    var mtypeLpr = ""
                    var isNotSelecteAnyOne = false
                    if (arrPackage != null) {
                        for ((key, value) in arrPackage) {
                            type = key
                            if (type == 0 && value && !TextUtils.isEmpty(mEditTextSearch1!!.text)) {
                                mtypeBlock = "&block=" + mEditTextSearch1!!.text.toString()
                                isNotSelecteAnyOne = true
                            }
                            if (type == 1 && value && !TextUtils.isEmpty(mEditTextSearch2!!.text)) {
                                mtypeStreet = "&street=" + mEditTextSearch2!!.text.toString()
                                isNotSelecteAnyOne = true
                            }
                            if (type == 2 && value && !TextUtils.isEmpty(mEditTextSearch3!!.text)) {
                                mtypeTimingType = "&regulation_time=" + mRegulationTime
                                isNotSelecteAnyOne = true
                            }
                            if (type == 3 && value && !TextUtils.isEmpty(mEditTextSearch4!!.text)) {
                                mtypeStatus = "&arrival_status=" + mEditTextSearch4!!.text.toString()
                                isNotSelecteAnyOne = true
                            }
                            if (type == 4 && value) {
                                mtypeLpr = "&lp_number=$searchViewQuery"
                                isNotSelecteAnyOne = true
                            }
                            if (type == 5 && value && !TextUtils.isEmpty(mEditTextSearch5!!.text)) {
                                mtypeStatus = "&side=" + mEditTextSearch5!!.text.toString()
                                isNotSelecteAnyOne = true
                            }
                            if (!isNotSelecteAnyOne && !TextUtils.isEmpty(searchViewQuery)) {
                                mtypeLpr = "&lp_number=$searchViewQuery"
                            }
                        }
                    }
                    val mtypeUrl = mReqURL + mtypeBlock + mtypeStreet + mtypeTimingType + mtypeStatus + mtypeLpr
                    callGetTimingApi(mtypeUrl)
                }
            }

            override fun onFinishDataLoading() {

            }

        })

        listPaginationUtils.startPaginationLoading()

        setCrossClearButton(mContext = requireContext(), textInputLayout = mTextInputSearch1, appCompatEditText = mEditTextSearch1)
        setCrossClearButton(mContext = requireContext(), textInputLayout = mTextInputSearch2, appCompatAutoCompleteTextView = mEditTextSearch2)
    }

    //Returns current time in millis
    private fun getTimingRecordURL() {

        //Returns current time in millis
        var mZone = "CST"
        var mHour = ""
        //val model = mDb?.dbDAO?.getDataset()
        val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)

        siteOfficerID = mDb?.dbDAO?.getWelcomeForm()?.siteOfficerId.nullSafety()

        if (settingsList != null && settingsList.isNotEmpty()) {
            for (i in settingsList.indices) {
                if (settingsList[i].type.equals(
                        "TIMEZONE",
                        ignoreCase = true
                    )
                ) {
                    try {
                        mZone = settingsList[i].mValue.toString()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                if (settingsList[i].type.equals(
                        "TIMING_RECORD_LOOKUP_THRESHOLD",
                        ignoreCase = true
                    )
                ) {
                    try {
                        mHour = settingsList[i].mValue.toString()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                if (settingsList[i].type.equals(
                        "IS_TIRE_STEM_ICON",
                        ignoreCase = true
                    )
                    && settingsList[i].mValue.equals("YES", ignoreCase = true)
                ) {
                    try {
                        isTireStemWithImageView = true
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


        var timeMilli1 = ""
        var timeMilli2 = ""
        if (mHour.isNotEmpty()) {
            timeMilli2 = AppUtils.getStartTDateWithSetting(mZone)
            timeMilli1 = AppUtils.getStartTDateAddSettingHourValue(mZone, mHour)
        } else {
            timeMilli1 = AppUtils.getStartTDate(mZone)
            timeMilli2 = AppUtils.getEndTDate(mZone)
        }
        mReqURL = "issue_ts_from=$timeMilli1&issue_ts_to=$timeMilli2&limit=25"
    }


    private val updateMarkResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_UPDATE_MARK
        )
    }
    private val timingMarkResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_TIMING_MARK
        )
    }

    private val timingMarkBulkResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.PATCH_TIMING_MARK_BULK
        )
    }

    private fun addObservers() {
        mUpdateMarkViewModel?.response?.observe(viewLifecycleOwner, updateMarkResponseObserver)
        mTimigMarkViewModel?.response?.observe(viewLifecycleOwner, timingMarkResponseObserver)
        mTimingMarkBulkViewModel?.response?.observe(viewLifecycleOwner, timingMarkBulkResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mUpdateMarkViewModel?.response?.removeObserver(updateMarkResponseObserver)
        mTimigMarkViewModel?.response?.removeObserver(timingMarkResponseObserver)
        mTimingMarkBulkViewModel?.response?.removeObserver(timingMarkBulkResponseObserver)
    }



    private fun setTimingAdapterExternal() {
        mMusicAdapter =
            TimingAdapter(mContext!!, mListTiming,"lookUp",false,isTireStemWithImageView, object : TimingAdapter.ListItemSelectListener {
                override fun onItemClick(markData: TimingMarkData?) {
                    if (markData != null) {
                        if (markData.arrialStatus.equals("Enforced", ignoreCase = true)
                                || markData.arrialStatus.equals("GOA", ignoreCase = true)
                                || AppUtils.isTimingExpired(markData.markStartTimestamp!!, markData.regulationTime!!.toFloat())) {
                            showAddNoteDialog(markData)
                        }
                    }
                }

                override fun onItemChecked(position: Int, isChecked: Boolean?) {
                    try {
                        if(mListTiming.size>=position && mListTiming[position]!=null) {
                            mListTiming[position].isChecked = isChecked.nullSafety()
                            mMusicAdapter?.notifyItemChanged(position)
                            manipulateUIBasedOnCheckedTimingItem()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        mRecylerViewHistory?.isNestedScrollingEnabled = false
        mRecylerViewHistory?.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        mRecylerViewHistory?.layoutManager = mLayoutManager
        mRecylerViewHistory?.adapter = mMusicAdapter
//        mRecylerViewHistory?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy > 0) { //check for scroll down
//                    visibleItemCount = mLayoutManager.childCount
//                    totalItemCount = mLayoutManager.itemCount
//                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
//                    if (loading) {
//                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
//                            if (mListTiming.size < totalRecordCount && mLimit >= 9 ) {
//                                callGetTimingApi(mReqURL)
//                                //callAddTimingApi(!mFinalURL.isEmpty()?mFinalURL:mUrl);
//                            }
//                            // Do pagination.. i.e. fetch new data
//                            loading = false
//                        }
//                    }
//                }
//            }
//        })
    }


    /* Call Api For Update Mark */
    private fun callUpdateMarkApi(msg: String, id: String) {
        if (isInternetAvailable(mContext!!)) {
            val updateMarkRequest = UpdateMarkRequest()
            updateMarkRequest.arrivalStatus = msg
            mUpdateMarkViewModel!!.hitUpdateMarkApi(updateMarkRequest, id)
        } else {
            LogUtil.printToastMSGForErrorWarning(
                mContext?.applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    //set value to Status dropdown
    private fun setDropdownStatus(mEditTextStatus: AppCompatAutoCompleteTextView) {
        val pos = 0
        val mApplicationList: MutableList<String> = ArrayList()
        mApplicationList.add("GOA")
//        mApplicationList.add("Not Enforced") As Sri told me 06-apr
//        mApplicationList.add("Enforced")
//        mApplicationList.add("Not Enforced")
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i]
                /* if (mApplicationList.get(i).get_id().equalsIgnoreCase(mWelcomeResponseData.getData().get(0).getUser().getOfficerShift().get_id())) {
                    pos = i;
                }*/
            }
            val adapter = ArrayAdapter(
                mContext!!,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            mEditTextStatus.threshold = 1
            mEditTextStatus.setAdapter<ArrayAdapter<String?>>(adapter)
            mEditTextStatus.onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    // mSelectedShiftStat = mApplicationList.get(position);
                }
        } else {
        }
    }

    private fun showAddNoteDialog(markData : TimingMarkData?) {
        val mDialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
        mDialog.setContentView(R.layout.dialog_add_note)
        mDialog.setCanceledOnTouchOutside(true)
        mDialog.setCancelable(true)
        val mEditTextNote: TextInputEditText = mDialog.findViewById(R.id.etNote)
        val textInputNote: TextInputLayout = mDialog.findViewById(R.id.input_textNote)
        val mEditTextStatus: AppCompatAutoCompleteTextView = mDialog.findViewById(R.id.etStatus)
        val textInputStatus: TextInputLayout = mDialog.findViewById(R.id.input_textStatus)
        val appCompatButtonDone: AppCompatButton = mDialog.findViewById(R.id.btn_done)
        val appCompatButtonCite: AppCompatButton = mDialog.findViewById(R.id.btn_citation)
//        val appCompatLayout: LinearLayoutCompat = mDialog.findViewById(R.id.layPopup)
        val tv_dialogTitle: AppCompatTextView = mDialog.findViewById(R.id.tv_dialogTitle)

        setAccessibilityForTextInputLayoutDropdownButtons(requireContext(), textInputStatus)


        appCompatButtonDone.visibility = View.VISIBLE
        appCompatButtonCite.visibility = View.VISIBLE
        textInputNote.visibility = View.GONE
        textInputStatus.visibility = View.VISIBLE
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13()))
        {
            appCompatButtonCite.visibility = View.GONE
        }else {
            appCompatButtonCite.visibility = View.VISIBLE
        }
        setDropdownStatus(mEditTextStatus)
        tv_dialogTitle.text = getString(R.string.msg_update_status)
        val appCompatImageView: AppCompatImageView = mDialog.findViewById(R.id.btn_cancel)
        appCompatImageView.setOnClickListener { v: View? -> mDialog.dismiss() }
        appCompatButtonDone.setOnClickListener {
            if (TextUtils.isEmpty(mEditTextStatus.text.toString())) {
                LogUtil.printToastMSGForErrorWarning(mContext?.applicationContext,
                    getString(R.string.val_msg_please_enter_status))
            } else {
                callUpdateMarkApi(mEditTextStatus.text.toString(), markData?.id.nullSafety())
                mDialog.dismiss()
            }
        }

        appCompatButtonCite.setOnClickListener {
            mDialog.dismiss()
            moveToNext(markData)
        }
        mDialog.show()
    }

    private fun moveToNext(selectedTimeObject: TimingMarkData?) {
//        if (!TextUtils.isEmpty(TextViewLprNumber.getText().toString().trim())) {
        /**
         * time for comment
         */
//            if(!violationCode.equalsIgnoreCase("time")) {
//                 SharedPref.getInstance(LprDetailsActivity.this).write(SharedPrefKey.isTimeLimitEnforcement, "false");
//            }
//            SharedPref.getInstance(LprDetailsActivity.this).write(SharedPrefKey.VOIDANDREISSUEBYPLATE,false);
//            TextViewLprNumber.clearFocus();
        val aFormattedFront: String = formatter.format(selectedTimeObject!!.tireStemFront!!.toLong())
        val aFormattedBack: String = formatter.format(selectedTimeObject!!.tireStemBack!!.toLong())

        if (aFormattedFront != null && aFormattedFront.isNotEmpty() && !aFormattedFront.equals(
                "00"
            )
        ) {
            mTimingTireStem =
                "Tire Stem :".plus(
                    (aFormattedFront.toString() + "/"
                            + aFormattedBack.toString())
                )
        }else{
            mTimingTireStem = ""
        }
        sharedPreference.writeOverTimeParkingTicketDetails(
            SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
            AddTimingRequest()!!
        )
        sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "true")
        val mIntent = Intent(activity, LprDetails2Activity::class.java)
        mIntent.putExtra("make", selectedTimeObject?.make)
        mIntent.putExtra("from_scr", "lpr_details")
        if (selectedTimeObject?.model != null) {
            mIntent.putExtra("model", selectedTimeObject?.model)
        }
        mIntent.putExtra("color", selectedTimeObject?.color)
        mIntent.putExtra("lpr_number", selectedTimeObject?.lpNumber)
        mIntent.putExtra("address", selectedTimeObject?.address)
        mIntent.putExtra("Meter", selectedTimeObject?.meterNumber)
        //            mIntent.putExtra("Zone", mPBCZone);
        mIntent.putExtra("Street", selectedTimeObject?.street)
        mIntent.putExtra("SideItem", selectedTimeObject?.side)
        mIntent.putExtra("Block", selectedTimeObject?.block)
        mIntent.putExtra("TimingID", selectedTimeObject?.id)
        mIntent.putExtra("timing_record_value", selectedTimeObject?.timeLimitEnforcementObservedTime)
        mIntent.putExtra("timing_record_value", splitDateLPRTime(selectedTimeObject?.markStartTimestamp.toString())
                + " elapsed: " +
                AppUtils.isElapsTime(selectedTimeObject!!.markStartTimestamp!!,
                        selectedTimeObject!!.regulationTime!!, mContext))


        mIntent.putExtra("timing_tire_stem_value",   mTimingTireStem)
        if (selectedTimeObject?.images?.isNotEmpty().nullSafety()){
            mIntent.putExtra(INTENT_KEY_TIMING_IMAGES, selectedTimeObject?.images as ArrayList<out Parcelable>)
        }
        mIntent.putExtra("Vin", selectedTimeObject?.vinNumber)
        startActivity(mIntent)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    //perform click actions
    @SuppressLint("NotifyDataSetChanged")
    @OnClick(R.id.ivFilter, R.id.act_markgoa, R.id.tvSelectAndUnselectAll)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.ivFilter -> {
                showSelectFilterDialog(0)
            }
            R.id.act_markgoa -> {
                createMarkBulkRequestArray()
            }
            R.id.tvSelectAndUnselectAll -> {
                val checkedItemCount = mListTiming.filter { it.isChecked }.size.nullSafety()
                val totalItemCount = mListTiming.size

                if (checkedItemCount == totalItemCount) {
                    mListTiming.map { it.isChecked = false }
                } else {
                    mListTiming.map { it.isChecked = true }
                }
                mMusicAdapter?.notifyDataSetChanged()

                manipulateUIBasedOnCheckedTimingItem()
            }
        }
    }

    /**
     * Function used to enable disable Mark GOA button other UI related manipulation
     */
    private fun manipulateUIBasedOnCheckedTimingItem() {
        try {
            val checkedItemCount = mListTiming.filter { it.isChecked }.size.nullSafety()
            val totalItemCount = mListTiming.size

            if (checkedItemCount > 0) {
                tvMarkGoa?.enableView()
            } else {
                tvMarkGoa?.disableView()
            }

            if (checkedItemCount == totalItemCount) {
                setSelectAndUnselectAllText(false)
            } else {
                setSelectAndUnselectAllText(true)
            }

            setSelectedItemCount(checkedItemCount)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param showSelectAll : value denotes that do we need to show Select all or Unselect all
     * Function used to change text to select & unselect all
     */
    private fun setSelectAndUnselectAllText(showSelectAll: Boolean? = true) {
        if (showSelectAll.nullSafety()) {
            tvSelectAndUnselectAll?.text = getString(R.string.scr_select_all)
        } else {
            tvSelectAndUnselectAll?.text = getString(R.string.scr_unselect_all)
        }
    }

    /**
     * @param count : count value for selected items from the list
     * Function used to show selected item count with text
     */
    private fun setSelectedItemCount(count: Int? = 0) {
        if (count == 0) {
            tvCountItemSelected?.invisibleView()
        } else {
            tvCountItemSelected?.showView()
            tvCountItemSelected?.text =
                getString(R.string.scr_count_item_selected, count.nullSafety().toString())
        }
    }

    /* Call Api to get DataFromLpr */
    private fun callGetTimingApi(mReqURL: String?) {
        var mReqURL = mReqURL
        if (isInternetAvailable(mContext!!)) {
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, true)) {
                mReqURL = "$mReqURL&page=$indexAPI&site_officer_id=$siteOfficerID&enforced=false&arrival_status=Open,GOA" //As Sri told me 06-apr enforeced false
            } else  if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)) {
                mReqURL = "$mReqURL&page=$indexAPI&site_officer_id=$siteOfficerID&enforced=false&arrival_status=Open"
            }else{
                mReqURL = "$mReqURL&page=$indexAPI&site_officer_id=$siteOfficerID&enforced=false" //As Sri told me 06-apr enforeced false
            }
            mTimigMarkViewModel?.hitTimigMarkApi(mReqURL)
        } else {
            LogUtil.printToastMSGForErrorWarning(mContext?.applicationContext,
                getString(R.string.err_msg_connection_was_refused))
        }
    }
    private fun createMarkBulkRequestArray() {
        try {
            if(mListTiming!=null && mListTiming.size>0) {
                val mTimingMarkArray = TimingMarkBulkRequest()
                val mMarkIds: MutableList<String> = ArrayList()
                mTimingMarkArray.arrivalStatus = "GOA"
                for (item in mListTiming) {
                    if (item.isChecked.nullSafety()) {
                        mMarkIds.add(item.id.toString())
                    }
                }
                mTimingMarkArray.markIds = mMarkIds

                callTimingMarkBulkApi(mTimingMarkArray)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Call Api to get DataFromLpr */
    private fun callTimingMarkBulkApi(mReqURL: TimingMarkBulkRequest?) {
        var mReqURL = mReqURL
        if (isInternetAvailable(mContext!!)) {

            mTimingMarkBulkViewModel?.hitTimigMarkBulkApi(mReqURL)
        } else {
            LogUtil.printToastMSGForErrorWarning(mContext?.applicationContext,
                getString(R.string.err_msg_connection_was_refused))
        }
    }

var totalRecordCount =0
    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS ->                 //dismissLoader();
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.GET_TIMING_MARK, ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TimingMarkResponse::class.java)

                            if (responseModel != null && responseModel.success.nullSafety()) {
                                if (responseModel.data != null) {
                                    if (listPaginationUtils.getCurrentPageIndex() == 1) {
                                        mListTiming.clear()
                                    }

                                    listPaginationUtils.setTotalDataCount(responseModel.mLength)
                                    listPaginationUtils.completeAPICall()

                                    mListTiming.addAll(responseModel.data!!)
                                    mMusicAdapter?.updateList(mListTiming,isTireStemWithImageView)
                                    mMusicAdapter?.notifyDataSetChanged()
                                    manipulateUIBasedOnCheckedTimingItem()


                                    //Add all data in list
                                    //notify the adapter

//                                    if (responseModel.data?.size.nullSafety() > 0) {
//                                            totalRecordCount = responseModel.mLength
//                                            mListTiming.addAll(responseModel.data!!)
//                                            //mLayTiming.setVisibility(View.VISIBLE);
//                                            //setAdapterForTiming(mListTiming)
//                                            loading = true
//                                            //TODO we are not getting length from backend so changed to current work around here
//                                            // mLimit = responseModel.getmLength()
//                                            mLimit = responseModel?.data?.size.nullSafety()
//
//                                            if (responseModel.data?.size!! > 9) {
//                                                indexAPI++
//                                            }
//
//                                            mMusicAdapter?.updateList(mListTiming)
//                                            mMusicAdapter?.notifyDataSetChanged()
//                                            manipulateUIBasedOnCheckedTimingItem()
//                                    }else{
//                                        if(mMusicAdapter!=null) {
//                                            val list =  ArrayList<TimingMarkData>()
//                                            mMusicAdapter?.updateList(list)
//                                            mMusicAdapter?.notifyDataSetChanged()
//                                        }
//                                    }
                                } else {
                                    if (mMusicAdapter != null) {
                                        val list = ArrayList<TimingMarkData>()
                                        mMusicAdapter?.updateList(list,isTireStemWithImageView)
                                    }
                                    Toast.makeText(context?.applicationContext,
                                        getString(R.string.error_no_timing_records_found), Toast.LENGTH_LONG).show()
                                }
                            } else if (responseModel != null && !responseModel.success!!) {
                                // Not getting response from server..!!
                                val message: String
                                if (responseModel.response != null && responseModel.response != "") {
                                    message = responseModel.response.nullSafety()
                                    AppUtils.showCustomAlertDialog(mContext, "GET_TIMING_MARK",
                                        message, "Ok", "Cancel", this
                                    )
                                } else {
                                    responseModel.response = "Not getting response from server..!!"
                                    message = responseModel.response.nullSafety()
                                    AppUtils.showCustomAlertDialog(mContext, "GET_TIMING_MARK",
                                        message, "Ok", "Cancel", this
                                    )
                                }
                                //  AppUtils.showCustomAlertDialog(mContext, "GET_TIMING_MARK",
                                //      "Not getting response from server..!!", "Ok", "Cancel", this);
                            } else {
                                AppUtils.showCustomAlertDialog(mContext, "GET_TIMING_MARK",
                                    "Something wen't wrong..!!", "Ok", "Cancel",
                                    this)
                                dismissLoader()

                                // LogUtil.printToastMSG(LprDetails2Activity.this, responseModel.getMessage());
                            }
                        }

                         if (tag.equals(DynamicAPIPath.PATCH_TIMING_MARK_BULK, ignoreCase = true)) {


                             val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TimingMarkBulkResponse::class.java)

                            if (responseModel != null && responseModel.success.nullSafety()) {
                                createURLBeforeCallingAPI("");
                                AppUtils.showCustomAlertDialogWithPositiveButton(
                                        mContext, "MARK GOA",
                                        "Mark GOA Success", "Ok", this)
//
                            }   else {
                                AppUtils.showCustomAlertDialog(mContext, "GET_TIMING_MARK",
                                    "Something wen't wrong..!!", "Ok", "Cancel",
                                    this)
                                dismissLoader()

                                // LogUtil.printToastMSG(LprDetails2Activity.this, responseModel.getMessage());
                            }
                        }
                        if (tag.equals(DynamicAPIPath.POST_UPDATE_MARK, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UpdateMarkResponse::class.java)

                            if (responseModel != null && responseModel.success!!) {
                                LogUtil.printToastMSG(mContext?.applicationContext, responseModel.message)
                            } else if (responseModel != null && !responseModel.success!!) {
                                // Not getting response from server..!!
                                AppUtils.showCustomAlertDialog(
                                    mContext, "POST_UPDATE_MARK",
                                    responseModel.message, "Ok", "Cancel", this
                                )
                            } else {
                                AppUtils.showCustomAlertDialog(mContext, "POST_UPDATE_MARK",
                                    "Something wen't wrong..!!", "Ok", "Cancel",
                                    this)
                                dismissLoader()

                                // LogUtil.printToastMSG(LprDetails2Activity.this, responseModel.getMessage());
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //token expires
                        //dismissLoader();
                        (mContext as BaseActivity?)!!.logout(mContext!!)
                    }
                }
            Status.ERROR -> {
                //dismissLoader();
                (mContext as BaseActivity?)!!.logout(mContext!!)
                LogUtil.printToastMSGForErrorWarning(
                    mContext?.applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
        }
    }

    private fun splitsFormat(Date: String?): String {
        val separated = Date!!.split("-".toRegex()).toTypedArray()
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
        val separated = hours.split(":".toRegex()).toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvert(`val`)
    }

    fun splitDate(date: String): String? {
        return try {
            val separated = date.split("T".toRegex()).toTypedArray()
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

    private fun showSelectFilterDialog(mSearchStatus: Int) {
        val mDialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
        mDialog.setContentView(R.layout.dialog_search_filter)
        val mEditTextCitation: TextInputEditText = mDialog.findViewById(R.id.editTextCitation)
        val mEditTextBlock: TextInputEditText = mDialog.findViewById(R.id.editTextBlock)
        val mEditTextStreet: TextInputEditText = mDialog.findViewById(R.id.editTextStreet)
        val mEditTextRecordType: TextInputEditText = mDialog.findViewById(R.id.editTextRecordType)
        val mEditTextStatus: TextInputEditText = mDialog.findViewById(R.id.editTextStatus)
        val mEditTextNumber: TextInputEditText = mDialog.findViewById(R.id.editTextNumber)
        val mEditTextSide: TextInputEditText = mDialog.findViewById(R.id.editText_side)
        val appCompatButton: AppCompatButton = mDialog.findViewById(R.id.btn_done)
        val cardCitation: CardView = mDialog.findViewById(R.id.cardCitation)
        val cardBlock: CardView = mDialog.findViewById(R.id.cardBlock)
        val cardStreet: CardView = mDialog.findViewById(R.id.cardStreet)
        val cardRecType: CardView = mDialog.findViewById(R.id.cardRecType)
        val cardStatus: CardView = mDialog.findViewById(R.id.cardStatus)
        val cardNum: CardView = mDialog.findViewById(R.id.cardNum)
        val cardSide: CardView = mDialog.findViewById(R.id.card_side)
        cardSide.visibility = View.VISIBLE
        mEditTextStatus.hint = "Arrival status"
        mEditTextRecordType.hint = "Timing type"
        appCompatButton.visibility = View.VISIBLE
        appCompatButton.setOnClickListener {
            mDialog.dismiss()
            setSearchHint()
        }
        checkSelectedItems(cardBlock, mEditTextBlock, 0)
        checkSelectedItems(cardStreet, mEditTextStreet, 1)
        checkSelectedItems(cardRecType, mEditTextRecordType, 2)
        checkSelectedItems(cardStatus, mEditTextStatus, 3)
        checkSelectedItems(cardNum, mEditTextNumber, 4)
        checkSelectedItems(cardSide, mEditTextSide, 5)
        cardCitation.visibility = View.GONE

        //default block and street visible and set selected in popup
        val isClickedBlock = arrPackage!![0]!!
        if(isClickedBlock)
            checkSelectedItems(cardBlock, mEditTextBlock,0)

        val isClickedStreet = arrPackage!![1]!!
        if(isClickedStreet)
            checkSelectedItems(cardStreet, mEditTextBlock,1)

        mEditTextBlock.setOnClickListener {
            type = 0
            val isValue = arrPackage!![0]!!
            arrPackage[0] = !isValue
            checkSelectedItems(cardBlock, mEditTextBlock, 0)
        }
        mEditTextStreet.setOnClickListener {
            type = 1
            val isValue = arrPackage!![1]!!
            arrPackage[1] = !isValue
            checkSelectedItems(cardStreet, mEditTextStreet, 1)
        }
        mEditTextRecordType.setOnClickListener {
            type = 2
            val isValue = arrPackage!![2]!!
            arrPackage[2] = !isValue
            checkSelectedItems(cardRecType, mEditTextRecordType, 2)
        }
        mEditTextStatus.setOnClickListener {
            type = 3
            val isValue = arrPackage!![3]!!
            arrPackage[3] = !isValue
            checkSelectedItems(cardStatus, mEditTextStatus, 3)
        }
        mEditTextNumber.setOnClickListener {
            type = 4
            val isValue = arrPackage!![4]!!
            arrPackage[4] = !isValue
            checkSelectedItems(cardNum, mEditTextNumber, 4)
        }
        mEditTextSide.setOnClickListener {
            type = 5
            val isValue = arrPackage!![5]!!
            arrPackage[5] = !isValue
            checkSelectedItems(cardSide, mEditTextSide, 5)
        }


        //set filter type from shared pref ------------------->
        val filter = sharedPreference.read(SharedPrefKey.SEARCH_TYPE, "0")
        if (mSearchStatus == 0) {
            mDialog.show()
        }
    }

    private fun checkSelectedItems(
        cardView: CardView,
        edtiTextView: TextInputEditText,
        index: Int
    ) {
        val isClicked = arrPackage!![index]!!
        if (isClicked) {
            cardView.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
            edtiTextView.setHintTextColor(resources.getColor(R.color.white))
            sharedPreference.write(SharedPrefKey.SEARCH_TYPE, type.toString())
        } else {
            cardView.setCardBackgroundColor(resources.getColor(R.color.white))
            edtiTextView.setHintTextColor(resources.getColor(R.color.gray))
            sharedPreference.write(SharedPrefKey.SEARCH_TYPE, type.toString())
        }
    }

    override fun onYesButtonClick() {
    }
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
    var lastTrueIndex = 0
    private fun setSearchHint(): String {
        hideFilterLayout()
        var value = ""
        var index = 0
        for ((key, value1) in arrPackage!!) {
            index = key
            if (value1) {
                if (index == 0) {
                    TypeString.append("block,")
                    value = "block"
                    mLinearLayoutCompatSearch1!!.visibility = View.VISIBLE
                    mTextInputSearch1!!.hint = "block"
                    lastTrueIndex = 0
                }
                if (arrPackage[1]!!) {
                    TypeString.append("street,")
                    mLinearLayoutCompatSearch2!!.visibility = View.VISIBLE
                    mTextInputSearch2!!.hint = "street"
                    value = "street"
                    lastTrueIndex = 1
                }
                if (arrPackage[2]!!) {
                    TypeString.append("timing_type,")
                    mLinearLayoutCompatSearch3!!.visibility = View.VISIBLE
                    mTextInputSearch3!!.hint = "Timing type"
                    value = "Timing type"
                    lastTrueIndex = 2
                    setDropdownRegulation()
                }
                if (arrPackage[3]!!) {
                    TypeString.append("arrival_status,")
                    mLinearLayoutCompatSearch4!!.visibility = View.VISIBLE
                    mTextInputSearch4!!.hint = "Arrival status"
                    value = "Arrival status"
                    lastTrueIndex = 3
                }
                if (arrPackage[4]!!) {
                    TypeString.append("lp_number,")
                    value = "lpr Number"
                    mSearchView!!.queryHint = value // true if you want to submit, otherwise false
                    mSearchView!!.clearFocus()
                    lastTrueIndex = 4
                }
                if (arrPackage[5]!!) {
                    TypeString.append("side,")
                    mLinearLayoutCompatSearch5!!.visibility = View.VISIBLE
                    mTextInputSearch5!!.hint = "Side"
                    value = "side"
                    lastTrueIndex = 5
                    setDropdownSide()
                }
            }
        }
        editTextViewActionButtonClick()
        return value
    }

    private fun hideFilterLayout() {
        mLinearLayoutCompatSearch1!!.visibility = View.GONE
        mLinearLayoutCompatSearch2!!.visibility = View.GONE
        mLinearLayoutCompatSearch3!!.visibility = View.GONE
        mLinearLayoutCompatSearch4!!.visibility = View.GONE
        mLinearLayoutCompatSearch5!!.visibility = View.GONE
    }

    private fun editTextViewActionButtonClick() {
        mEditTextSearch1!!.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch1!!.filters = arrayOf<InputFilter>(AllCaps())
        mEditTextSearch2!!.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch2!!.filters = arrayOf<InputFilter>(AllCaps())
        mEditTextSearch3!!.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch3!!.filters = arrayOf<InputFilter>(AllCaps())
        mEditTextSearch4!!.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch4!!.filters = arrayOf<InputFilter>(AllCaps())
        mEditTextSearch5!!.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch5!!.filters = arrayOf<InputFilter>(AllCaps())

        when (lastTrueIndex) {
            0 -> mEditTextSearch1!!.imeOptions = EditorInfo.IME_ACTION_DONE
//            1 -> mEditTextSearch2!!.imeOptions = EditorInfo.IME_ACTION_DONE
            2 -> mEditTextSearch3!!.imeOptions = EditorInfo.IME_ACTION_DONE
            3 -> mEditTextSearch4!!.imeOptions = EditorInfo.IME_ACTION_DONE
            5 -> mEditTextSearch5!!.imeOptions = EditorInfo.IME_ACTION_DONE
        }
        mEditTextSearch1!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                hideSoftKeyboard(requireActivity())
                return@OnEditorActionListener true
            }
            false
        })
        mEditTextSearch2!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                hideSoftKeyboard(requireActivity())
                return@OnEditorActionListener true
            }
            false
        })
        mEditTextSearch3!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                hideSoftKeyboard(requireActivity())
                return@OnEditorActionListener true
            }
            false
        })
        mEditTextSearch4!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                hideSoftKeyboard(requireActivity())
                return@OnEditorActionListener true
            }
            false
        })
        mEditTextSearch5!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                hideSoftKeyboard(requireActivity())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun createURLBeforeCallingAPI(query: String) {
        listPaginationUtils.resetPaginationLoading()
        listPaginationUtils.startPaginationLoading()
//        ioScope.launch {
//            mListTiming.clear()
//            indexAPI = 1
//            totalRecordCount = 0
//            var mtypeBlock = ""
//            var mtypeStreet = ""
//            var mtypeTimingType = ""
//            var mtypeStatus = ""
//            var mtypeLpr = ""
//            var isNotSelecteAnyOne = false
//            if (arrPackage != null) {
//                for ((key, value) in arrPackage) {
//                    type = key
//                    if (type == 0 && value && !TextUtils.isEmpty(mEditTextSearch1!!.text)) {
//                        mtypeBlock = "&block=" + mEditTextSearch1!!.text.toString()
//                        isNotSelecteAnyOne = true
//                    }
//                    if (type == 1 && value && !TextUtils.isEmpty(mEditTextSearch2!!.text)) {
//                        mtypeStreet = "&street=" + mEditTextSearch2!!.text.toString()
//                        isNotSelecteAnyOne = true
//                    }
//                    if (type == 2 && value && !TextUtils.isEmpty(mEditTextSearch3!!.text)) {
//                        mtypeTimingType = "&timing_type=" + mEditTextSearch3!!.text.toString()
//                        isNotSelecteAnyOne = true
//                    }
//                    if (type == 3 && value && !TextUtils.isEmpty(mEditTextSearch4!!.text)) {
//                        mtypeStatus = "&arrival_status=" + mEditTextSearch4!!.text.toString()
//                        isNotSelecteAnyOne = true
//                    }
//                    if (type == 4 && value) {
//                        mtypeLpr = "&lp_number=$query"
//                        isNotSelecteAnyOne = true
//                    }
//                    if (type == 5 && value && !TextUtils.isEmpty(mEditTextSearch5!!.text)) {
//                        mtypeStatus = "&side=" + mEditTextSearch5!!.text.toString()
//                        isNotSelecteAnyOne = true
//                    }
//                    if (!isNotSelecteAnyOne && !TextUtils.isEmpty(query)) {
//                        mtypeLpr = "&lp_number=$query"
//                    }
//                }
//            }
//            val mtypeUrl = mReqURL + mtypeBlock + mtypeStreet + mtypeTimingType + mtypeStatus + mtypeLpr
//            callGetTimingApi(mtypeUrl)
//        }
    }

    companion object {
        fun newInstance(param1: String?, param2: String?): TimingRecordFragment {
            val fragment = TimingRecordFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet(value: String?, mAutoComTextViewStreet: AppCompatAutoCompleteTextView) {
//        val mApplicationList = mDatasetList?.dataset?.streetList
        try {
                ioScope.launch {
                    val mApplicationList = Singleton.getDataSetList(DATASET_STREET_LIST, mDb)
                    var pos = -1
                    if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                        for (i in mApplicationList.indices) {
                            mDropdownList[i] = mApplicationList[i].street_name.toString()
                            try {
                                if (value != null) {
                                    if (mApplicationList[i].street_name.equals(value, ignoreCase = true)) {
                                        pos = i
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }

                        mAutoComTextViewStreet.post {
                            try {
                                if(pos>0)
                                    mAutoComTextViewStreet.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                            }

                            //mAutoComTextViewDirection.setText(mApplicationList.get(pos).getDirection()); ;
                            val adapter = activity?.let {
                                ArrayAdapter(
                                        it,
                                        R.layout.row_dropdown_menu_popup_item,
                                        mDropdownList
                                )
                            }
                            try {
                                mAutoComTextViewStreet.threshold = 1
                                mAutoComTextViewStreet.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewStreet.onItemClickListener =
                                        AdapterView.OnItemClickListener { parent, view, position, id -> //mAutoComTextViewDirection.setText(mApplicationList.get(position).getDirection()); ;
                                            activity?.let { AppUtils.hideSoftKeyboard(it) }
                                            createURLBeforeCallingAPI(searchViewQuery)
                                        }
                                if (mAutoComTextViewStreet.tag != null && mAutoComTextViewStreet.tag == "listonly") {
                                    activity?.let { AppUtils.setListOnly(it, mAutoComTextViewStreet) }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDropdownRegulation() {
        try {
            if (mEditTextSearch3 != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList?.dataset?.regulationTimeList
                    val mApplicationList = Singleton.getDataSetList(DATASET_REGULATION_TIME_LIST, mDb)

                    var pos = -1
                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].regulation.toString()
//                            if(value.equals(mApplicationList!![i].regulation, ignoreCase = true))
//                            {
//                                pos = i
//                            }
                        }
                        //mAutoComTextViewTimeLimit.setText(mDropdownList[pos]);
                        mEditTextSearch3?.post {
//                            if(pos>=0)
//                            {
//                                mEditTextSearch3?.setText(mApplicationList[pos].regulation)
//                                mRegulationTime = mApplicationList!![pos].mTime.nullSafety()
//                                mRegulationTimeValue = mApplicationList!![pos].regulation.nullSafety()
//                            }
                            val adapter = activity?.let {
                                ArrayAdapter(
                                    it,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                                )
                            }
                            try {
                                mEditTextSearch3?.threshold = 1
                                mEditTextSearch3?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mEditTextSearch3?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id ->
                                        val index = getIndexOfRegulation(
                                            mApplicationList,
                                            parent.getItemAtPosition(position).toString())
                                        mRegulationTime = mApplicationList!![index].mTime.nullSafety()
                                        mRegulationTimeValue = mApplicationList!![index].regulation.nullSafety()
                                        AppUtils.hideSoftKeyboard(activity!!)
                                        createURLBeforeCallingAPI(searchViewQuery)
                                    }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOfRegulation(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.regulation, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    private fun setDropdownSide() {
        try {
            if (mEditTextSearch5 != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList?.dataset?.sideList
                    val mApplicationList = Singleton.getDataSetList(DATASET_SIDE_LIST, mDb)

                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].sideName.toString()
                            /*if (mApplicationList.get(i).getSideName().equalsIgnoreCase(mSideItem)) {
                            pos = i;
                        }*/
                        }
                        mEditTextSearch5?.post {
                            val adapter = activity?.let {
                                ArrayAdapter(
                                    it,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                                )
                            }
                            try {
                                mEditTextSearch5?.threshold = 1
                                mEditTextSearch5?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mEditTextSearch5?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                        AppUtils.hideSoftKeyboard(activity!!)
                                        createURLBeforeCallingAPI(searchViewQuery)
                                    }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}