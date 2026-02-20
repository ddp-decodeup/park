package com.parkloyalty.lpr.scan.ui.continuousmode

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.continuousmode.model.ContinuousDataObject
import com.parkloyalty.lpr.scan.ui.continuousmode.model.DownloadAlertFileViewModel
import com.parkloyalty.lpr.scan.ui.continuousmode.model.LprStartSessionResponse
import com.parkloyalty.lpr.scan.ui.continuousmode.model.LprStartSessionViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.AppUtils.getCurrenTime
import com.parkloyalty.lpr.scan.util.AppUtils.getCurrenTimefile
import com.parkloyalty.lpr.scan.util.AppUtils.getCurrentDateTimeforBoot
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.AppUtils.showKeyboard
import com.parkloyalty.lpr.scan.util.DATASET_REGULATION_TIME_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SIDE_LIST
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ContinuousModeActivity : BaseActivity(), CustomDialogHelper {
    private var mContext: Context? = null
    private var mDb: AppDatabase? = null

    @BindView(R.id.input_block)
    lateinit var mTextInputBlock: TextInputLayout

    @BindView(R.id.et_block)
    lateinit var mEditTextBlock: AppCompatEditText

    @BindView(R.id.input_Street)
    lateinit var mTextInputStreet: TextInputLayout

    @BindView(R.id.et_street)
    lateinit var mEditTextStrret: AppCompatEditText

    @BindView(R.id.inputSideOfState)
    lateinit var inputSideOfState: TextInputLayout

    @BindView(R.id.AutoComTextViewVehSideOfState)
    lateinit var mEditTextViewTextViewSideOfState: AppCompatAutoCompleteTextView

    @BindView(R.id.inputVehZone)
    lateinit var inputVehZone: TextInputLayout

    @BindView(R.id.AutoComTextViewVehzone)
    lateinit var mEditTextViewTextViewZone: AppCompatAutoCompleteTextView

    @BindView(R.id.inputRegulation)
    lateinit var inputRegulation: TextInputLayout

    @BindView(R.id.AutoComTextViewRegulation)
    lateinit var mEditTextViewTextViewRegulation: AppCompatAutoCompleteTextView

    @BindView(R.id.txt_times)
    lateinit var appCompatTextViewTimes: AppCompatTextView

    @BindView(R.id.txt_ofcid)
    lateinit var appCompatTextViewOfficerId: AppCompatTextView

    @BindView(R.id.radio_rpp)
    lateinit var appCompatRadioButtonRpp: AppCompatRadioButton

    @BindView(R.id.radio_timing)
    lateinit var appCompatRadioButtonTiming: AppCompatRadioButton

    @BindView(R.id.radio_meter)
    lateinit var appCompatRadioButtonMeter: AppCompatRadioButton

    @BindView(R.id.radio_all)
    lateinit var appCompatRadioButtonAll: AppCompatRadioButton

    @BindView(R.id.radiogroup)
    lateinit var radioGroup: RadioGroup
    private val mSideItem = ""
    private var clientTime = ""
    private var mRegulationTime: String? = ""
    private var mSelectedRadioOption = "All"
    private var mFileName = ""
    private var isRadioButtonSelected = false
    private var mWelcomeFormData: WelcomeForm? = null
    //private var mDatasetList: DatasetDatabaseModel? = DatasetDatabaseModel()

    private val lprStartSessionViewModel: LprStartSessionViewModel? by viewModels()
    private val downloadAlertFileViewModel: DownloadAlertFileViewModel? by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continuous_mode)
        ButterKnife.bind(this)
        addObservers()
        init()
        downloadAlertFileViewModel!!.downloadAlertFileApi()

        setCrossClearButtonForAllFields()
    }

    private fun setCrossClearButtonForAllFields(){
        setCrossClearButton(
            context = this@ContinuousModeActivity,
            textInputLayout = mTextInputBlock,
            appCompatEditText = mEditTextBlock
        )

        setCrossClearButton(
            context = this@ContinuousModeActivity,
            textInputLayout = mTextInputStreet,
            appCompatEditText = mEditTextStrret
        )

        setCrossClearButton(
            context = this@ContinuousModeActivity,
            textInputLayout = inputSideOfState,
            appCompatAutoCompleteTextView = mEditTextViewTextViewSideOfState
        )

        setCrossClearButton(
            context = this@ContinuousModeActivity,
            textInputLayout = inputVehZone,
            appCompatAutoCompleteTextView = mEditTextViewTextViewZone
        )

        setCrossClearButton(
            context = this@ContinuousModeActivity,
            textInputLayout = inputRegulation,
            appCompatAutoCompleteTextView = mEditTextViewTextViewRegulation
        )
    }

    override fun onResume() {
        super.onResume()
        mFileName = getCurrenTimefile("File")
    }

    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        //mDatasetList = mDb?.dbDAO?.getDataset()
        //for full screen toolbar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setToolbar()
        try {
            getGeoAddress()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        setDropDowns()
        //        callAPI();
//        getIntentData();
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_rpp -> {
                    isRadioButtonSelected = true
                    mSelectedRadioOption = "Rpp"
                }
                R.id.radio_meter -> {
                    isRadioButtonSelected = true
                    mSelectedRadioOption = "Meter"
                }
                R.id.radio_all -> {
                    isRadioButtonSelected = true
                    mSelectedRadioOption = "All"
                }
                R.id.radio_timing -> {
                    isRadioButtonSelected = true
                    mSelectedRadioOption = "Time"
                }
            }
        }
    }

    private val lprStartSessionResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_LPR_START_SESSION
        )
    }
    private val downloadAlertFileResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_DOWNLOAD_ALERT_FILE
        )
    }

    private fun addObservers() {
        lprStartSessionViewModel!!.response.observe(this, lprStartSessionResponseObserver)
        downloadAlertFileViewModel!!.response.observe(this, downloadAlertFileResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        lprStartSessionViewModel!!.response.removeObserver(lprStartSessionResponseObserver)
        downloadAlertFileViewModel!!.response.removeObserver(downloadAlertFileResponseObserver)
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
    }// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()// Here 1 represent max location result to returned, by documents it recommended 1 to 5

    // this will contain "Fruit"
    @Throws(IOException::class)
    private fun getGeoAddress() {
        appCompatTextViewTimes.text = getCurrenTime("UI")
        var printAddress: String? = null // this will contain "Fruit"
        try {
            appCompatTextViewOfficerId.text = mWelcomeFormData!!.officerBadgeId
            clientTime = getCurrentDateTimeforBoot("Normal").trim()
                .replace(" ", "T")
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!
                .toDouble()
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
            printAddress = separated[0]
            mEditTextBlock.setText(
                if (printAddress.split(" ").toTypedArray().size > 1) printAddress.split(" ")
                    .toTypedArray()[0] else printAddress
            )
            mEditTextBlock.setSelection(mEditTextBlock.length())
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            val count = printAddress!!.split(" ").toTypedArray().size
            if (count > 2) {
                mEditTextStrret.setText(
                    if (count > 0) printAddress.split(
                        Pattern.compile(" "),
                        count - 1.coerceAtLeast(0)
                    ).toTypedArray().get(1) else printAddress
                )
            } else {
                mEditTextStrret.setText(
                    if (count > 1) printAddress.split(" ").toTypedArray()[1] else printAddress
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDropDowns() {
        try {
            setDropdownZone()
            setDropdownRegulation()
            setDropdownSide(Singleton.getDataSetList(DATASET_SIDE_LIST, mDb))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to side dropdown
    private fun setDropdownSide(mApplicationList: List<DatasetResponse>?) {
        var pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].sideName.toString()
                if (mApplicationList[i].sideName.equals(mSideItem, ignoreCase = true)) {
                    pos = i
                    try {
                        mEditTextViewTextViewSideOfState.setText(mDropdownList[pos])
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
                mEditTextViewTextViewSideOfState.threshold = 1
                mEditTextViewTextViewSideOfState.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewSideOfState.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        hideSoftKeyboard(this@ContinuousModeActivity)
                        mEditTextViewTextViewSideOfState.error = null
                    }
            } catch (e: Exception) {
            }
        } else {
        }
    }

    //set value to Status dropdown
    private fun setDropdownZone() {
        try {
            val mWelcomeList = Singleton.getWelcomeDbObject(mDb);
            val mApplicationList =
                if (mWelcomeList!!.welcomeList!!.pbcZoneStats != null) mWelcomeList.welcomeList!!.pbcZoneStats else mWelcomeList.welcomeList!!.zoneStats
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
                    mEditTextViewTextViewZone.threshold = 1
                    mEditTextViewTextViewZone.setAdapter<ArrayAdapter<String?>>(adapter)
                    //mSelectedShiftStat = mApplicationList.get(pos);
                    mEditTextViewTextViewZone.onItemClickListener =
                        OnItemClickListener { parent, view, position, id -> hideSoftKeyboard(this@ContinuousModeActivity) }
                } catch (e: Exception) {
                }
            } else {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to side dropdown
    private fun setDropdownRegulation() {
        val mApplicationList = Singleton.getDataSetList(DATASET_REGULATION_TIME_LIST, mDb)
        val pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].regulation.toString()
                /*if (mApplicationList.get(i).getSideName().equalsIgnoreCase(mSideItem)) {
                    pos = i;
                }*/
            }
            //mAutoComTextViewTimeLimit.setText(mDropdownList[pos]);
            val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
            )
            try {
                mEditTextViewTextViewRegulation.threshold = 1
                mEditTextViewTextViewRegulation.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewRegulation.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        mRegulationTime = mApplicationList[position].mTime
                        hideSoftKeyboard(this@ContinuousModeActivity)
                    }
            } catch (e: Exception) {
            }
        } else {
        }
    }

    /*perform click actions*/
    @OnClick(R.id.btn_submit)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btn_submit -> if (isFormValid()) {
                setRequest()
            }
        }
    }// no radio buttons are checked//mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));//mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));//mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));

    //        if (TextUtils.isEmpty(mEditTextViewTextViewRegulation.getText().toString().trim())) {
//            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
//            mEditTextViewTextViewRegulation.requestFocus();
//            mEditTextViewTextViewRegulation.setFocusable(true);
//            mEditTextViewTextViewRegulation.setError(getString(R.string.val_msg_please_enter_regulation));
//            AppUtils.showKeyboard(ContinuousModeActivity.this);
//            return false;
//        }
    //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
    fun isFormValid(): Boolean {
        if (TextUtils.isEmpty(mEditTextBlock.text.toString().trim())) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mEditTextBlock.requestFocus()
            mEditTextBlock.isFocusable = true
            mEditTextBlock.error = getString(R.string.val_msg_please_enter_block)
            showKeyboard(this@ContinuousModeActivity)
            return false
        }
        if (TextUtils.isEmpty(mEditTextStrret.text.toString().trim())) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mEditTextStrret.requestFocus()
            mEditTextStrret.isFocusable = true
            mEditTextStrret.error = getString(R.string.val_msg_please_enter_street)
            showKeyboard(this@ContinuousModeActivity)
            return false
        }
        //        if (TextUtils.isEmpty(mEditTextViewTextViewRegulation.getText().toString().trim())) {
//            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
//            mEditTextViewTextViewRegulation.requestFocus();
//            mEditTextViewTextViewRegulation.setFocusable(true);
//            mEditTextViewTextViewRegulation.setError(getString(R.string.val_msg_please_enter_regulation));
//            AppUtils.showKeyboard(ContinuousModeActivity.this);
//            return false;
//        }
        if (TextUtils.isEmpty(
                mEditTextViewTextViewSideOfState.text.toString().trim()
            )
        ) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mEditTextViewTextViewSideOfState.requestFocus()
            mEditTextViewTextViewSideOfState.isFocusable = true
            mEditTextViewTextViewSideOfState.error =
                getString(R.string.err_lbl_enter_side_of_street)
            showKeyboard(this@ContinuousModeActivity)
            return false
        }
        if (TextUtils.isEmpty(mEditTextViewTextViewZone.text.toString().trim())) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mEditTextViewTextViewZone.requestFocus()
            mEditTextViewTextViewZone.isFocusable = true
            mEditTextViewTextViewZone.error = getString(R.string.val_msg_please_enter_zone)
            showKeyboard(this@ContinuousModeActivity)
            return false
        }
        if (isRadioButtonSelected) {
            // no radio buttons are checked
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.err_lbl_select_any_one),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun setRequest() {
        if (isInternetAvailable(this@ContinuousModeActivity)) {
            try {
                val dataObject = ContinuousDataObject()
                dataObject.mBlock=mEditTextBlock.text.toString()
                dataObject.mStreet=mEditTextStrret.text.toString()
                dataObject.mSide=mEditTextViewTextViewSideOfState.text.toString()
                dataObject.mZone=mEditTextViewTextViewZone.text.toString()
                dataObject.mRegulation=mEditTextViewTextViewRegulation.text.toString()
                dataObject.mOfficerID=appCompatTextViewOfficerId.text.toString()
                dataObject.mStartTime=appCompatTextViewTimes.text.toString()
                dataObject.mSelectedRadio="all"
                dataObject.mDeviceId=mWelcomeFormData?.officerDeviceId.nullSafety()
                dataObject.mDeviceFriendlyName=mWelcomeFormData?.officerDeviceName.nullSafety()
                dataObject.mBeat=mWelcomeFormData?.officerBeat.nullSafety()
                dataObject.mInitiatorName=mWelcomeFormData?.initiatorRole.nullSafety()
                dataObject.mSupervisor_name=mWelcomeFormData?.officerSupervisor.nullSafety()
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!
                    .toDouble()
                dataObject.mLatitude=mLat
                dataObject.mLongitude=mLong
                val json = ObjectMapperProvider.toJson(dataObject)
                sharedPreference.write(SharedPrefKey.CONTINOUS_RESULT, json)
                lprStartSessionViewModel!!.hitStartSessionApi(dataObject)
                //            finish();
//            launchScreen(ContinuousModeActivity.this, LprContinuousScanModeActivity.class);
//            launchScreen(ContinuousModeActivity.this, ContinuousResultActivity.class);
//            Intent intent = new Intent(mContext, LprContinuousScanModeActivity.class);
//            intent.putExtra("KEY_FILE_NAME",mFileName);
//            startActivity(intent);
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
                        if (tag.equals(DynamicAPIPath.POST_LPR_START_SESSION, ignoreCase = true)) {
                            try {


                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LprStartSessionResponse::class.java)

                                if (responseModel != null && responseModel.isStatus) {
//                                    mImages.add(responseModel.getData().get(0).getResponse().getLinks().get(0));
                                    //LogUtil.printToastMSG(LprPreviewActivity.this, responseModel.getMessage());
                                    val intent =
                                        Intent(mContext, LprContinuousScanModeActivity::class.java)
                                    intent.putExtra("KEY_FILE_NAME", mFileName)
                                    intent.putExtra(
                                        "KEY_SESSION_ID",
                                        responseModel.metadata!!.sessionId
                                    )
                                    startActivity(intent)
                                } else {
                                    showCustomAlertDialog(
                                        mContext,
                                        APIConstant.POST_IMAGE,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else if (tag.equals(
                                DynamicAPIPath.GET_DOWNLOAD_ALERT_FILE,
                                ignoreCase = true
                            )
                        ) {
                            //DownloadBitmapResponse responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

//                            if (responseModel != null && responseModel.isStatus()) {
//                                if (checkPermission() && responseModel.getMetadata(). get(0).getUrl().length()>0) {
//                                    new TicketDetailsActivity.DownloadingPrintBitmapFromUrl().execute(responseModel.getMetadata().
//                                            get(0).getUrl());
//                                }
//                            }
                            dismissLoader()
                        }
                    } catch (e: Exception) {
                        dismissLoader()
                    }
                }
            }
            Status.ERROR -> dismissLoader()
            else -> {}
        }
    }

    inner class DownloadingPrintBitmapFromUrl : AsyncTask<String?, Int?, String>() {
        public override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg url: String?): String? {
            if (!TextUtils.isEmpty(url[0])) {
                try {
                    val mydir = File(
                        Environment.getExternalStorageDirectory().absolutePath,
                        Constants.FILE_NAME + Constants.COTINOUS
                    )
                    if (!mydir.exists()) {
                        mydir.mkdirs()
                    }
                    val file = File(mydir.absolutePath, "parkingAlerts" + ".csv")
                    if (file.exists()) file.delete()
                    val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val downloadUri = Uri.parse(url[0])
                    val request = DownloadManager.Request(downloadUri)
                    request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                    )
                        .setAllowedOverRoaming(false)
                        .setTitle("Downloading")
                        .setDestinationInExternalPublicDir(
                            Constants.FILE_NAME + "" + Constants.CAMERA,
                            "parkingAlerts" + ".csv"
                        )
                    manager.enqueue(request)
                    return mydir.absolutePath + File.separator + "parkingAlerts" + ".csv"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                return ""
            }
            return ""
        }

        public override fun onPostExecute(s: String) {
            try {
//                if(s!=null && s.length()>6){
//                    SharedPref.getInstance(ContinuousModeActivity.this).write(SharedPrefKey.REPRINT_PRINT_BITMAP, s);
//                }
//                getBitmapFromUrl(s);
            } catch (e: Exception) {
                printLog("error mesg", e.message)
            }
            super.onPostExecute(s)
        }
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }
}