package com.parkloyalty.lpr.scan.ui.continuousmode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetailsActivity
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.continuousmode.model.*
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.AppUtils.getCurrenTime
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ContinuousResultActivity : BaseActivity(), CustomDialogHelper {
    private var mContext: Context? = null
    private var mDb: AppDatabase? = null

    @BindView(R.id.txt_address)
    lateinit var appCompatTextViewAddress: AppCompatTextView

    @BindView(R.id.txt_regulation)
    lateinit var appCompatTextViewRegulation: AppCompatTextView

    @BindView(R.id.txt_zone)
    lateinit var appCompatTextViewZone: AppCompatTextView

    @BindView(R.id.txt_start_time)
    lateinit var appCompatTextViewStartTime: AppCompatTextView

    @BindView(R.id.txt_end_time)
    lateinit var appCompatTextViewEndTime: AppCompatTextView

    @BindView(R.id.recycler_result)
    lateinit var recyclerViewResult: RecyclerView

    @BindView(R.id.nestscrollview)
    lateinit var nestedScrollView: NestedScrollView

    private var mWelcomeFormData: WelcomeForm? = null
    private var continousResultAdapter: ContinousResultAdapter? = null
    private val resultDataObjectList: MutableList<ResultDataObject> = ArrayList()
    private val mBackgroudColorArray = intArrayOf(0, 1, 1, 0)
    private var mFileName: String? = null
    private var mSessionID: String? = null
    private var file: File? = null

    private val uploadCSVFileViewModel: UploadCSVFileViewModel? by viewModels()
    private val uploadCSVFileStaticViewModel: UploadCSVFileStaticViewModel? by viewModels()
    private val lprEndSessionViewModel: LprEndSessionViewModel? by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continuous_result)
        ButterKnife.bind(this)
        addObservers()
        init()
    }

    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        //for full screen toolbar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setToolbar()
        if (intent.hasExtra("KEY_FILE_NAME")) {
            mFileName = intent.getStringExtra("KEY_FILE_NAME")
        }
        if (intent.hasExtra("KEY_SESSION_ID")) {
            mSessionID = intent.getStringExtra("KEY_SESSION_ID")
            callEndSessionApi(mSessionID)
        }
        file = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.COTINOUS + "/vehicleList_" + mFileName + ".csv"
        )
        readFileData(file!!)
        setDataOnUi()
        //        callUploadImages(file);
//        mDatasetList = mDb.getDbDAO().getDataset();
//        mWelcomeFormData = mDb.getDbDAO().getWelcomeForm();
//        getIntentData();
//        setDropDowns();
        nestedScrollView.post { nestedScrollView.fullScroll(View.FOCUS_UP) }
    }


    private val uploadCSVFileResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_CSV
        )
    }
    private val uploadCSVFileStaticResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_IMAGE
        )
    }
    private val lprEndSessionResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_LPR_END_SESSION
        )
    }

    private fun addObservers() {
        uploadCSVFileViewModel!!.response.observe(this, uploadCSVFileResponseObserver)
        uploadCSVFileStaticViewModel!!.response.observe(this, uploadCSVFileStaticResponseObserver)
        lprEndSessionViewModel!!.response.observe(this, lprEndSessionResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        uploadCSVFileViewModel!!.response.removeObserver(uploadCSVFileResponseObserver)
        uploadCSVFileStaticViewModel!!.response.removeObserver(uploadCSVFileStaticResponseObserver)
        lprEndSessionViewModel!!.response.removeObserver(lprEndSessionResponseObserver)
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

    private fun setDataOnUi() {
        try {
            val clientTime = getCurrenTime("Normal").trim().replace(" ", "T")
            val clientTimeUI = getCurrenTime("test").trim()
            val objectString = sharedPreference.read(SharedPrefKey.CONTINOUS_RESULT, "")
            val dataObject = ObjectMapperProvider.fromJson(objectString.nullSafety(), ContinuousDataObject::class.java)
            appCompatTextViewAddress.text =
                dataObject.mBlock + ", " + dataObject.mStreet + ", " + dataObject.mSide
            appCompatTextViewRegulation.text = dataObject.mRegulation
            appCompatTextViewZone.text = dataObject.mZone
            appCompatTextViewStartTime.text = dataObject.mStartTime
            appCompatTextViewEndTime.text = clientTimeUI
            Toast.makeText(
                applicationContext,
                resultDataObjectList.size.toString() + " ",
                Toast.LENGTH_SHORT
            ).show()
            setAdapterForCitationList(resultDataObjectList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapterForCitationList(listData: List<ResultDataObject>) {
        continousResultAdapter = ContinousResultAdapter(
            mContext!!,
            listData,
            object : ContinousResultAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
                    finish()
                    val mIntent =
                        Intent(this@ContinuousResultActivity, LprDetailsActivity::class.java)
                    mIntent.putExtra("lpr_number", listData[position].mLpNumber.nullSafety())
                    mIntent.putExtra("screen", "ContinuosResultActivity")
                    startActivity(mIntent)
                }
            })
        recyclerViewResult.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(mContext, 1)
        gridLayoutManager.scrollToPositionWithOffset(0, 0)
        recyclerViewResult.adapter = continousResultAdapter
        recyclerViewResult.layoutManager = gridLayoutManager
        recyclerViewResult.visibility = View.VISIBLE
    }

    private fun readFileData(file: File) {
        try {
            val mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
            var data: Array<String>
            if (file.exists()) {
                val br = BufferedReader(FileReader(file))
                try {
                    var position = 0
                    var positionColor = -1
                    var csvLine: String
                    while (br.readLine().also { csvLine = it } != null) {
                        data = csvLine.split(",").toTypedArray()
                        try {
                            if (position > 0) {
                                val dataObject = ResultDataObject()
                                try {
                                    dataObject.mLpNumber = data[0].replace("\"", "")
                                    dataObject.mDes = data[1].replace("\"", "")
                                    dataObject.mBackgroundColor =
                                        mBackgroudColorArray[positionColor]
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    if (!data[9].isEmpty() && !data[10].isEmpty()) dataObject.mLat =
                                        data[9].replace("\"", "").toDouble()

                                    dataObject.mLong = data[10].replace("\"", "").toDouble()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    dataObject.mFirstTime = data[7].replace("\"", "")
                                    dataObject.mLastTime = data[13].replace("\"", "")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    val imageName = "anpr_" + dataObject.mLpNumber + ".jpg"
                                    val mImagePath = mPath + Constants.COTINOUS + "/" + imageName
                                    dataObject.mImagePath = mImagePath
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                resultDataObjectList.add(dataObject)
                            }
                            positionColor++
                            position++
                            if (positionColor % 4 == 0) {
                                positionColor = 0
                            }
                        } catch (e: Exception) {
                            Log.e("Problem", e.toString())
                        }
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            } else {
                Toast.makeText(applicationContext, "file not exists", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Call Api For update profile */
    private fun callUploadImageslink(link: String) {
        if (isInternetAvailable(this@ContinuousResultActivity)) {
            val request = UploadCsvLinksRequest()
            request.mLink = link
            uploadCSVFileViewModel?.hitUploadCsvApi(request)
        } else {
            printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For update profile */
    private fun callStaticUploadImages() {
        if (isInternetAvailable(this@ContinuousResultActivity)) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
            val files = MultipartBody.Part.createFormData(
                "files",
                if (file != null) file!!.name else "",
                requestFile
            )
            val mDropdownList: Array<String?>
            mDropdownList = arrayOf(mSessionID)
            val mRequestBodyType =
                RequestBody.create("text/csv".toMediaTypeOrNull(), "LPRSessionResults")
            uploadCSVFileStaticViewModel!!.hitUploadStaticCsvApi(
                mDropdownList,
                mRequestBodyType,
                files
            )
        } else {
            printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For update profile */
    private fun callEndSessionApi(mSessionID: String?) {
        if (isInternetAvailable(this@ContinuousResultActivity)) {
            val request = LprEndSessionRequest()
            request.mSessionId = mSessionID
            lprEndSessionViewModel!!.hitEndSessionApi(request)
        } else {
            printToastMSG(
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
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.POST_LPR_END_SESSION, ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LprStartSessionResponse::class.java)

                                if (responseModel != null && responseModel.isStatus) {
//                                    mImages.add(responseModel.getData().get(0).getResponse().getLinks().get(0));
                                    //LogUtil.printToastMSG(LprPreviewActivity.this, responseModel.getMessage());
                                    callStaticUploadImages()
                                } else {
                                    showCustomAlertDialog(
                                        mContext,
                                        APIConstant.POST_SESSION_END,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else if (tag.equals(DynamicAPIPath.POST_IMAGE, ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadStaticResponse::class.java)

                                if (responseModel != null && responseModel.isStatus) {
//                                    mImages.add(responseModel.getData().get(0).getResponse().getLinks().get(0));
                                    //LogUtil.printToastMSG(LprPreviewActivity.this, responseModel.getMessage());
                                    callUploadImageslink(responseModel.uploaddata!![0].uploadresponse!!.uploadlinks!![0])
                                } else {
                                    showCustomAlertDialog(
                                        mContext,
                                        APIConstant.POST_CSV,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else if (tag.equals(DynamicAPIPath.POST_CSV, ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status.nullSafety()) {
//                                    mImages.add(responseModel.getData().get(0).getResponse().getLinks().get(0));
                                    //LogUtil.printToastMSG(LprPreviewActivity.this, responseModel.getMessage());
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
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dismissLoader()
                        showCustomAlertDialog(
                            mContext, APIConstant.POST_IMAGE,
                            apiResponse.error!!.message, getString(R.string.alt_lbl_OK),
                            getString(R.string.scr_btn_cancel), this
                        )
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                showCustomAlertDialog(
                    mContext, APIConstant.POST_IMAGE,
                    apiResponse.error!!.message, getString(R.string.alt_lbl_OK),
                    getString(R.string.scr_btn_cancel), this
                )
                printLog("ERROR", apiResponse.error)
            }

            else -> {}
        }
    }

    /*perform click actions*/
    @OnClick(R.id.btnDone, R.id.btnContinue)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btnDone -> {
                val mIntent = Intent(this@ContinuousResultActivity, WelcomeActivity::class.java)
                startActivity(mIntent)
                finishAffinity()
            }
            R.id.btnContinue -> {
                val intent = Intent(mContext, LprContinuousScanModeActivity::class.java)
                intent.putExtra("KEY_FILE_NAME", mFileName)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
}