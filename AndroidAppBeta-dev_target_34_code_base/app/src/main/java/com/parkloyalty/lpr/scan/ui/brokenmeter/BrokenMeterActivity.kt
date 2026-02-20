package com.parkloyalty.lpr.scan.ui.brokenmeter

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivityBrokenMeterBinding
import com.parkloyalty.lpr.scan.databinding.ActivityLoginBinding
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.*
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.DATASET_BLOCK_LIST
import com.parkloyalty.lpr.scan.util.DATASET_METER_LIST
import com.parkloyalty.lpr.scan.util.DATASET_REMARKS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SIDE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_STREET_LIST
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.regex.Pattern
import kotlin.getValue

@AndroidEntryPoint
class BrokenMeterActivity : BaseActivity(), CustomDialogHelper {
    var mEditTextOfficerName: AppCompatTextView? = null
    var mEditTextDateTime: AppCompatTextView? = null
    var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewStreet: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewSideOfState: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewMeterOutage: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewRemark: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewMeterNo: AppCompatAutoCompleteTextView? = null
    var textInputLayoutVehMeterNo: TextInputLayout? = null
    var textInputLayoutVehBlock: TextInputLayout? = null
    var textInputLayoutVehStreet: TextInputLayout? = null
    var textInputLayoutVehSideOfState: TextInputLayout? = null
    var textInputLayoutMeterOutage: TextInputLayout? = null
    var textInputLayoutVehRemark: TextInputLayout? = null

    private val mSideItem = ""
    private var clientTime = ""
    private val meterOutageList = arrayOf("Sign outage", "Meter outage")
    private val meterOutageListValue = arrayOf("sign_outage", "meter_outage")
    private val meterOutageListTypeValue = arrayOf("broken_sign", "broken_meter")
    private var outageValue = "sign_outage"
    private var outageTypeValue = "broken_sign"
    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var mWelcomeFormData: WelcomeForm? = null

    private val brokenMeterViewModel: BrokenMeterViewModel? by viewModels()

    private lateinit var binding: ActivityBrokenMeterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrokenMeterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        addObservers()
        init()


//        Bundle bundle = new Bundle();
//        bundle.putString("SCREEN_NAME", "BrokenMeter");
//        bundle.putString("API_NAME", "BrokenMeter API");
//        bundle.putString("REQUEST", "BrokenMeter REQUEST ");
//        bundle.putString("RESPONSE", "BrokenMeter RESPONSE");
//        getInstanceOfAnalytics(bundle, "BROKENAPI");
    }

    private fun findViewsByViewBinding() {
        mEditTextOfficerName = binding.layoutContentBrokenMeter.officername
        mEditTextDateTime = binding.layoutContentBrokenMeter.txtIssuedate
        mAutoComTextViewBlock = binding.layoutContentBrokenMeter.AutoComTextViewVehblock
        mEditTextViewTextViewStreet = binding.layoutContentBrokenMeter.AutoComTextViewVehStreet
        mEditTextViewTextViewSideOfState = binding.layoutContentBrokenMeter.AutoComTextViewVehSideOfState

        mEditTextViewTextViewMeterOutage = binding.layoutContentBrokenMeter.AutoComTextViewMeterOutage
        mEditTextViewTextViewRemark = binding.layoutContentBrokenMeter.AutoComTextViewVehRemark
        mEditTextViewTextViewMeterNo = binding.layoutContentBrokenMeter.AutoComTextViewVehMeterNo
        textInputLayoutVehMeterNo = binding.layoutContentBrokenMeter.textInputLayoutVehMeterNo
        textInputLayoutVehBlock = binding.layoutContentBrokenMeter.textInputLayoutVehblock

        textInputLayoutVehStreet = binding.layoutContentBrokenMeter.textInputLayoutVehStreet
        textInputLayoutVehSideOfState = binding.layoutContentBrokenMeter.textInputLayoutVehSideOfState
        textInputLayoutMeterOutage = binding.layoutContentBrokenMeter.textInputLayoutMeterOutage
        textInputLayoutVehRemark = binding.layoutContentBrokenMeter.textInputLayoutVehRemark
    }

    private fun setupClickListeners() {
        binding.layoutContentBrokenMeter.btnSubmit.setOnClickListener { view ->
            if (isFormValid()) {
                setRequest()
            }
        }
    }

    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        setToolbar()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        //        getIntentData();
        setDropDowns()
        mEditTextDateTime?.text = AppUtils.getCurrentDateTimeforBoot("UI")
        clientTime =
            AppUtils.getCurrentDateTimeforBoot("Normal").trim().replace(" ", "T")


        setCrossClearButton(context = this@BrokenMeterActivity, textInputLayout = textInputLayoutVehMeterNo, appCompatAutoCompleteTextView  = mEditTextViewTextViewMeterNo)
        setCrossClearButton(context = this@BrokenMeterActivity, textInputLayout = textInputLayoutVehBlock, appCompatAutoCompleteTextView  = mAutoComTextViewBlock)
        setCrossClearButton(context = this@BrokenMeterActivity, textInputLayout = textInputLayoutVehStreet, appCompatAutoCompleteTextView = mEditTextViewTextViewStreet)
        setCrossClearButton(context = this@BrokenMeterActivity, textInputLayout = textInputLayoutVehSideOfState, appCompatAutoCompleteTextView = mEditTextViewTextViewSideOfState)
        setCrossClearButton(context = this@BrokenMeterActivity, textInputLayout = textInputLayoutMeterOutage, appCompatAutoCompleteTextView = mEditTextViewTextViewMeterOutage)
        setCrossClearButton(context = this@BrokenMeterActivity, textInputLayout = textInputLayoutVehRemark, appCompatAutoCompleteTextView = mEditTextViewTextViewRemark)
    }


    private val brokenMeterResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.BROKEN_METER
        )
    }

    private fun addObservers() {
        brokenMeterViewModel?.response?.observe(this, brokenMeterResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        brokenMeterViewModel?.response?.removeObserver(brokenMeterResponseObserver)
    }

    override fun onResume() {
        super.onResume()
        registerBroadcastReceiver()
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

    //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
    private fun isFormValid(): Boolean {
            if (TextUtils.isEmpty(
                    mEditTextViewTextViewSideOfState?.text.toString().trim())
            ) {
                //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                mEditTextViewTextViewSideOfState?.requestFocus()
                mEditTextViewTextViewSideOfState?.isFocusable = true
                mEditTextViewTextViewSideOfState?.error =
                    getString(R.string.err_lbl_side_of_street)
                AppUtils.showKeyboard(this@BrokenMeterActivity)
                return false
            }
            if (TextUtils.isEmpty(
                    mEditTextViewTextViewMeterOutage?.text.toString().trim())
            ) {
                //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                mEditTextViewTextViewMeterOutage?.requestFocus()
                mEditTextViewTextViewMeterOutage?.isFocusable = true
                mEditTextViewTextViewMeterOutage?.error = getString(R.string.err_lbl_meter_outage)
                AppUtils.showKeyboard(this@BrokenMeterActivity)
                return false
            }
            if (TextUtils.isEmpty(
                    mEditTextViewTextViewMeterNo?.text.toString().trim()) && TextUtils.isEmpty(
                            mEditTextViewTextViewMeterOutage?.text.toString().trim())) {
                //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                mEditTextViewTextViewMeterNo?.requestFocus()
                mEditTextViewTextViewMeterNo?.isFocusable = true
                mEditTextViewTextViewMeterNo?.error = getString(R.string.err_lbl_meter_no)
                AppUtils.showKeyboard(this@BrokenMeterActivity)
                return false
            }
            if (TextUtils.isEmpty(
                    mEditTextViewTextViewRemark?.text.toString().trim())
            ) {
                //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
                mEditTextViewTextViewRemark?.requestFocus()
                mEditTextViewTextViewRemark?.isFocusable = true
                mEditTextViewTextViewRemark?.error = getString(R.string.err_lbl_remarks)
                AppUtils.showKeyboard(this@BrokenMeterActivity)
                return false
            }
            return true
        }

    private fun setDropDowns() {
        try {
            setDropdownRemark(Singleton.getDataSetList(DATASET_REMARKS_LIST, mDb))
            setDropdownSide(Singleton.getDataSetList(DATASET_SIDE_LIST, mDb))
            setDropdownMeterName(Singleton.getDataSetList(DATASET_METER_LIST, mDb))
            setDropdownStreet(Singleton.getDataSetList(DATASET_STREET_LIST, mDb))
            setDropdownBlock(Singleton.getDataSetList(DATASET_BLOCK_LIST, mDb))

            setDropdownMeteroutage(meterOutageList)
            geoAddress()
            mEditTextOfficerName?.text = mWelcomeFormData?.officerFirstName + " " +
                    mWelcomeFormData?.officerLastName
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }// this will contain "Fruit"// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
    private fun geoAddress() {
            mEditTextDateTime?.text = AppUtils.getCurrentDateTimeforBoot("UI")
            clientTime = AppUtils.getCurrentDateTimeforBoot("Normal").trim()
                .replace(" ", "T")
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                mLat,
                mLong,
                1
            )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val separated = address.split(",").toTypedArray()
            val printAddress = separated[0] // this will contain "Fruit"
        mAutoComTextViewBlock?.setText(
                if (printAddress.split(" ").toTypedArray().size > 1) printAddress.split(" ")
                    .toTypedArray()[0] else printAddress
            )
            try {
                val count = printAddress.split(" ").toTypedArray().size
                if (count > 2) {
                    mEditTextViewTextViewStreet?.setText(
                        if (count > 0) printAddress.split(
                            Pattern.compile(" "),
                            count - 1.coerceAtLeast(0)
                        ).toTypedArray().get(1) else printAddress
                    )
                } else {
                    mEditTextViewTextViewStreet?.setText(
                        if (count > 1) printAddress.split(" ").toTypedArray()[1] else printAddress
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    //set value to Body Style dropdown
    private fun setDropdownRemark(mApplicationList: List<DatasetResponse>?) {
        val addZoneValueInZeroIndex = DatasetResponse()
        Collections.sort(mApplicationList) { lhs, rhs -> lhs.remark.nullSafety().compareTo(rhs.remark.nullSafety()) }
        val pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].remark.toString()
            }

//            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mEditTextViewTextViewRemark?.threshold = 1
                mEditTextViewTextViewRemark?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewRemark?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        AppUtils.hideSoftKeyboard(this@BrokenMeterActivity)
                        mEditTextViewTextViewRemark?.error = null
                    }
            } catch (e: Exception) {
            }
        } else {
        }
    }

    //set value to side dropdown
    private fun setDropdownSide(mApplicationList: List<DatasetResponse>?) {
        var pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].sideName.toString()
                if (mApplicationList[i].sideName.equals(mSideItem, ignoreCase = true)) {
                    pos = i
                    try {
                        mEditTextViewTextViewSideOfState?.setText(mDropdownList[pos])
                    } catch (e: Exception) {
                    }
                }
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mEditTextViewTextViewSideOfState?.threshold = 1
                mEditTextViewTextViewSideOfState?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewSideOfState?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        AppUtils.hideSoftKeyboard(this@BrokenMeterActivity)
                        mEditTextViewTextViewSideOfState?.error = null
                    }
            } catch (e: Exception) {
            }
        } else {
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownMeterName(mApplicationList: List<DatasetResponse>?) {
        val pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].name.toString()
            }
            //            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mEditTextViewTextViewMeterNo?.threshold = 1
                mEditTextViewTextViewMeterNo?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewMeterNo?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        AppUtils.hideSoftKeyboard(this@BrokenMeterActivity)
                        //                        mEditTextViewTextViewMeterNo.setText(mDropdownList[position]);
                        try {
                            mEditTextViewTextViewMeterNo?.error = null
                            val index: Int = getIndexOfMeter(
                                    mApplicationList,
                                    mEditTextViewTextViewMeterNo!!.text.toString()
                            )

                            if (mApplicationList[index].direction != null && !TextUtils.isEmpty(
                                    mApplicationList[index].direction
                                )
                            ) {
                                mEditTextViewTextViewSideOfState?.setText(mApplicationList[index].direction)
                            }

                            if (mApplicationList[index].block != null && !TextUtils.isEmpty(
                                    mApplicationList[index].block
                                )
                            ) {
                                mAutoComTextViewBlock?.setText(mApplicationList[index].block)
                            }
                            if (mApplicationList[index].street != null && !TextUtils.isEmpty(
                                    mApplicationList[index].street
                                )
                            ) {
                                mEditTextViewTextViewStreet?.setText(mApplicationList[index].street)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

     private fun getIndexOfMeter(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.name, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to Meter Name dropdown
    private fun setDropdownMeteroutage(mApplicationList: Array<String>) {
//        Arrays.sort(mApplicationList);
        val adapter = ArrayAdapter(
            this,
            R.layout.row_dropdown_menu_popup_item,
            mApplicationList
        )
        try {
            mEditTextViewTextViewMeterOutage?.threshold = 1
            mEditTextViewTextViewMeterOutage?.setAdapter(adapter)
            //mSelectedShiftStat = mApplicationList.get(pos);
            mEditTextViewTextViewMeterOutage?.onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    mEditTextViewTextViewMeterOutage?.error = null
                    AppUtils.hideSoftKeyboard(this@BrokenMeterActivity)
                    outageValue = meterOutageListValue[position]
                    outageTypeValue = meterOutageListTypeValue[position]
                }
        } catch (e: Exception) {
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet(mApplicationList: List<DatasetResponse>?) {
//        hideSoftKeyboard(BootActivity.this);
        val pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].street_name.toString()
            }

            //mAutoComTextViewDirection.setText(mApplicationList.get(pos).getDirection());
//            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mDropdownList
            )
            try {
                mEditTextViewTextViewStreet!!.threshold = 1
                mEditTextViewTextViewStreet!!.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewStreet!!.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            mEditTextViewTextViewStreet?.error = null
                            AppUtils.hideSoftKeyboard(this@BrokenMeterActivity)
                        }
                // listonly
//                if(mEditTextViewTextViewStreet.getTag()!=null && mEditTextViewTextViewStreet.getTag().equals("listonly")) {
//                    AppUtils.setListOnly(BootActivity.this,mEditTextViewTextViewStreet);
//                }
            } catch (e: java.lang.Exception) {
            }
        } else {
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownBlock(mApplicationList: List<DatasetResponse>?) {
//        hideSoftKeyboard(this@LprDetails2Activity)
        var pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].blockName.toString()
//                mAutoComTextViewBlock?.setText(mBlock)
                /*if (mApplicationList[i].blockName.equals(mBlock, ignoreCase = true)) {
                    pos = i
                    try {
                        mAutoComTextViewBlock?.setText(mDropdownList[pos])
                    } catch (e: Exception) {
                    }
                }*/
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mDropdownList
            )
            try {
                mAutoComTextViewBlock?.threshold = 1
                mAutoComTextViewBlock?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mAutoComTextViewBlock?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            mAutoComTextViewBlock?.error = null
                            AppUtils.hideSoftKeyboard(this@BrokenMeterActivity) }
                // listonly
                if (mAutoComTextViewBlock?.tag != null && mAutoComTextViewBlock?.tag == "listonly") {
                    AppUtils.setListOnly(this@BrokenMeterActivity, mAutoComTextViewBlock!!)
                }
            } catch (e: Exception) {
            }
        }
    }



    private fun setRequest() {
        if (isInternetAvailable(this@BrokenMeterActivity)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val bootRequest = BrokenMeterRequest()
            bootRequest.clientTimestamp = clientTime
            //            bootRequest.setType(mEditTextViewTextViewMeterOutage.getText().toString());
            bootRequest.type = outageTypeValue
            val detailsBroken = Details()
            detailsBroken.meterNo = mEditTextViewTextViewMeterNo?.text.toString()
            detailsBroken.remarks = mEditTextViewTextViewRemark?.text.toString()
            detailsBroken.reason = outageValue
            bootRequest.details = detailsBroken
            val locationDetails = LocationDetails()
            locationDetails.block = mAutoComTextViewBlock?.text.toString()
            locationDetails.side = mEditTextViewTextViewSideOfState?.text.toString()
            locationDetails.street = mEditTextViewTextViewStreet?.text.toString()
            locationDetails.latitude = mLat
            locationDetails.longitude = mLong
            bootRequest.locationDetails = locationDetails
            val officerDetails = OfficerDetails()
            officerDetails.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.siteOfficerId = mWelcomeFormData?.siteOfficerId
            //            officerDetails.setShift(mWelcomeFormData.getOfficerShift());
            officerDetails.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,true)||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,true)) {
                officerDetails.zone = mWelcomeFormData?.cityZoneName
            } else {
                officerDetails.zone = mWelcomeFormData?.officerZone
            }
            bootRequest.officerDetails = officerDetails
            brokenMeterViewModel?.hitBrokenMeterSubmitApi(bootRequest)
        } else {
            LogUtil.printToastMSG(
                this@BrokenMeterActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse?, tag: String) {
        when (apiResponse!!.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.BROKEN_METER, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), BrokenMeterResponse::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
                                try {
                                    AppUtils.showCustomAlertDialog(
                                        mContext,
                                        "Broken Meter",
                                        "Submit Broken Meter Successfully",
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this@BrokenMeterActivity
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "Broken Meter",
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showCustomAlertDialog(
                            mContext,
                            getString(R.string.scr_btn_no_payment),
                            getString(R.string.err_msg_something_went_wrong),
                            getString(R.string.alt_lbl_OK),
                            getString(R.string.scr_btn_cancel),
                            this
                        )
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                AppUtils.showCustomAlertDialog(
                    mContext,
                    getString(R.string.scr_btn_no_payment),
                    getString(R.string.err_msg_something_went_wrong),
                    getString(R.string.alt_lbl_OK),
                    getString(R.string.scr_btn_cancel),
                    this
                )
            }

            else -> {}
        }
    }

    override fun onYesButtonClick() {
        finish()
        val intent = Intent(mContext, WelcomeActivity::class.java)
        startActivity(intent)
    }

    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }
}