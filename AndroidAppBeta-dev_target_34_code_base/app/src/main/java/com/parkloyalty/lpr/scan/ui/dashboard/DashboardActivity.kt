package com.parkloyalty.lpr.scan.ui.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import com.parkloyalty.lpr.scan.BuildConfig
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.databinding.ActivityDashboardBinding
import com.parkloyalty.lpr.scan.databinding.ActivityLoginBinding
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.CheckSetupActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetails2Activity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetailsActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.MyActivityActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.Datum
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.GetBarCountResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.GetBarCountViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.license.LprScanActivity
import com.parkloyalty.lpr.scan.ui.dashboard.model.DashboardViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.ticket.SearchActivity
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSGForErrorWarning
import com.parkloyalty.lpr.scan.util.setAsAccessibilityHeading
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.vehiclestickerscan.VehicleStickerScanActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import kotlin.getValue

@AndroidEntryPoint
class DashboardActivity : BaseActivity(), CustomDialogHelper {
    lateinit var btnCheckSetup: AppCompatButton
    lateinit var drawerLy: DrawerLayout
    lateinit var TextViewScanCount: AppCompatTextView
    lateinit var TextViewEnforcementCount: AppCompatTextView
    lateinit var tvDashboard: AppCompatTextView
    lateinit var btnScanSticker: AppCompatButton

    private lateinit var binding: ActivityDashboardBinding

    private val mRequestTimeStart: Long = 0
    private var mResponseTimeEnd: Long = 0
    private var mResponseTime: Long = 0
    private var mActivity: AppCompatActivity? = null

    private var mDb: AppDatabase? = null
    private var mContext: Context? = null
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 2111

    private val mDashboardViewModel: DashboardViewModel? by viewModels()
    private val mGetBarCountViewModel: GetBarCountViewModel? by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        mActivity = this
        mContext = this@DashboardActivity
        mDb = BaseApplication.instance?.getAppDatabase()
        addObservers()
        init()
        setToolbar()

        if (ContextCompat.checkSelfPermission(
                        this@DashboardActivity,
                        Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(
                        this@DashboardActivity,
                        Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                        this@DashboardActivity, arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                ), BLUETOOTH_PERMISSION_REQUEST_CODE
                )
            }
        }

        setAccessibilityForComponents()
    }

    private fun findViewsByViewBinding() {
        btnCheckSetup = binding.btnCheckSetup
        drawerLy = binding.drawerLy
        TextViewScanCount = binding.layoutContentDashboard.tvScanCount
        TextViewEnforcementCount = binding.layoutContentDashboard.tvEnforcementCount
        tvDashboard = binding.layoutContentDashboard.tvDashboard
        btnScanSticker = binding.btnScanSticker
    }

    private fun setupClickListeners() {
        btnCheckSetup.setOnClickListener { launchScreen(mActivity, CheckSetupActivity::class.java) }

        binding.layoutContentDashboard.imgScan.setOnClickListener {
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BISMARCK,
                    ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SEPTA,
                    ignoreCase = true
                )
            ) {
                launchScreen(
                    this@DashboardActivity,
                    LprDetailsActivity::class.java
                )
            } else {
                launchScreen(mActivity, LprScanActivity::class.java)
            }
        }

        binding.layoutContentDashboard.cardTotalScan.setOnClickListener {
            launchScreen(
                mActivity,
                MyActivityActivity::class.java
            )
        }

        binding.layoutContentDashboard.llTotalScan.setOnClickListener {
            launchScreen(
                mActivity,
                MyActivityActivity::class.java
            )
        }

        binding.layoutContentDashboard.cardTotalEnforcement.setOnClickListener {
            launchScreen(
                mActivity,
                SearchActivity::class.java
            )
        }

        binding.layoutContentDashboard.llTotalEnforcement.setOnClickListener {
            launchScreen(
                mActivity,
                SearchActivity::class.java
            )
        }

        btnScanSticker.setOnClickListener {
            launchScreen(mActivity, VehicleStickerScanActivity::class.java)
        }
    }

    fun setAccessibilityForComponents() {
        setAsAccessibilityHeading(tvDashboard)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                            this@DashboardActivity,
                            getString(R.string.error_bluetooth_permission_is_not_granted),
                            Toast.LENGTH_SHORT
                    ).show()
                } else {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
       /* if (sharedPreference.read(SharedPrefKey.IS_LOGGED_IN, false)) {
            //TODO your background code
            val mIssuranceModel: List<CitationInsurranceDatabaseModel> =
                mDb?.dbDAO?.getCitationInsurranceUnuploadCitation() as List<CitationInsurranceDatabaseModel>
            val lockLprModel = AppUtils.getLprLock(this@DashboardActivity)
            if (lockLprModel != null && lockLprModel.mLprNumber != null &&
                !TextUtils.isEmpty(lockLprModel.mLprNumber) && lockLprModel.mLprNumber.nullSafety().length > 1
                || mIssuranceModel.size > 0
            ) {
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, true)
                val mIntent = Intent(this@DashboardActivity, LprDetails2Activity::class.java)
                mIntent.putExtra("make", lockLprModel?.mMake)
                mIntent.putExtra(
                    "from_scr",
                    if (mIssuranceModel.isNotEmpty()) "WelcomeUnUpload" else "lpr_lock"
                )
                mIntent.putExtra("model", lockLprModel?.mModel)
                mIntent.putExtra("color", lockLprModel?.mColor)
                mIntent.putExtra("lpr_number", lockLprModel?.mLprNumber)
                mIntent.putExtra("violation_code", lockLprModel?.mViolationCode)
                mIntent.putExtra("address", lockLprModel?.mAddress)
                startActivity(mIntent)
            } else {
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                deleteImages()
            }
        }*/
    }

    //init toolbar navigation
    private fun setToolbar() {
        initToolbar(
            0,
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

    private fun init() {
        if (showAndEnableScanVehicleStickerModule) {
            btnScanSticker.showView()
        }else{
            btnScanSticker.hideView()
        }

        createFolderForLprImages()
        mEventStartTimeStamp = AppUtils.getDateTime()
        callDashboardApi()

//        Handler().postDelayed({ //saving network details
//            networkUsage()
//            //saving db
//            saveDatabaseToInternal()
//        }, 2000)
    }

    private val dashboardResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_DASHBOARD
        )
    }
    private val getBarCountResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_GET_BAR_COUNT
        )
    }

    private fun addObservers() {
        mDashboardViewModel?.response?.observe(this, dashboardResponseObserver)
        mGetBarCountViewModel?.response?.observe(this, getBarCountResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mDashboardViewModel?.response?.removeObserver(dashboardResponseObserver)
        mGetBarCountViewModel?.response?.removeObserver(getBarCountResponseObserver)
    }

    private fun createFolderForLprImages() {
        val localFolder = File(
            Environment.getExternalStorageDirectory().absolutePath,
            "/ParkLoyalty" + Constants.SCANNER
        )
        if (!localFolder.exists()) {
            localFolder.mkdirs()
        }
        val file = File(localFolder.toString())
        if (!file.exists()) {
            // file.delete(); //you might want to check if delete was successful
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            //if file not exists the download
            //mFilepath = file.getAbsolutePath();
        }
    }

    /* Call Api For Dashboard details */
    private fun callDashboardApi() {
        if (isInternetAvailable(this@DashboardActivity)) {
            val shift = "shift=" + sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            mGetBarCountViewModel?.hitGetBarCountApi(shift)
        } else {
            printToastMSGForErrorWarning(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun setDashboardDetails(mDashboardData: Datum) {
        TextViewScanCount.text = mDashboardData.scans.toString()
        TextViewEnforcementCount.text = mDashboardData.tickets.toString()
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                mResponseTimeEnd = System.currentTimeMillis()
                mResponseTime = mResponseTimeEnd - mRequestTimeStart
                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.POST_GET_BAR_COUNT, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), GetBarCountResponse::class.java)

                            if (responseModel != null && responseModel.status!!) {
                                if (responseModel.data!!.size > 0) {
                                    setDashboardDetails(responseModel.data!![0])
                                    //LogUtil.printToastMSG(mActivity, responseModel.getMessage());
                                }
                            } else if (responseModel != null && !responseModel.status!!) {
                                // Not getting response from server..!!
                                val message: String?
                                if (responseModel.response != null && responseModel.response != "") {
                                    message = responseModel.response
                                    showCustomAlertDialog(
                                        mContext, "POST_GET_BAR_COUNT",
                                        message, "Ok", "Cancel", this
                                    )
                                } else {
                                    responseModel.response = "Not getting response from server..!!"
                                    message = responseModel.response
                                    showCustomAlertDialog(
                                        mContext, "POST_GET_BAR_COUNT",
                                        message, "Ok", "Cancel", this
                                    )
                                }
                            } else {
                                showCustomAlertDialog(
                                    mContext, "POST_GET_BAR_COUNT",
                                    "Something wen't wrong..!!", "Ok", "Cancel",
                                    this
                                )
                                dismissLoader()

                                // LogUtil.printToastMSG(LprDetails2Activity.this, responseModel.getMessage());
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
                mResponseTimeEnd = System.currentTimeMillis()
                mResponseTime = mResponseTimeEnd - mRequestTimeStart
            }

            else -> {}
        }
    }

//    //save network call details to txt file
//    private fun networkUsage() {
//        // Get running processes
//        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
//        val runningApps = manager.runningAppProcesses
//        var mReceived: Long = 0
//        var mSent: Long = 0
//        try {
//            for (runningApp in runningApps) {
//                mReceived = TrafficStats.getUidRxBytes(runningApp.uid)
//                mSent = TrafficStats.getUidTxBytes(runningApp.uid)
//                Log.d(
//                    "LOG_TAG", String.format(
//                        Locale.getDefault(),
//                        "uid: %1d - name: %s: Sent = %1d, Rcvd = %1d",
//                        runningApp.uid,
//                        runningApp.processName,
//                        mSent,
//                        mReceived
//                    )
//                )
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        //double received = mReceived / (1024);
//        // double send = mSent / (1024);
//        val total = ((mReceived + mSent) / 1024).toDouble()
//        Log.d("mbps", String.format("%.2f", total) + " MB")
//        try {
//            createFolder(
//                "Response Time: " + mResponseTime + "ms, Speed: " + String.format(
//                    "%.2f",
//                    total
//                ) + "kb/s, Timestamp: " + AppUtils.getDateTime()
//            )
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        //   }
//    }
//
//    //save db to internal storage
//    private fun saveDatabaseToInternal() {
//        try {
//            backupDatabase()
//            //createCredFolder();
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }

//    @Throws(IOException::class)
//    fun backupDatabase() {
//        //Open your local db as the input stream
//        val currentDBPath = getDatabasePath("park_loyalty").absolutePath
//        val dbFile = File(currentDBPath)
//        if (dbFile != null) {
//            val fis = FileInputStream(dbFile)
//            val outFileName = Environment.getExternalStorageDirectory()
//                .toString() + "/" + Constants.FILE_NAME + "/database.db"
//            //Open the empty db as the output stream
//            val output: OutputStream = FileOutputStream(outFileName)
//            //transfer bytes from the inputfile to the outputfile
//            val buffer = ByteArray(1024)
//            var length: Int
//            while (fis.read(buffer).also { length = it } > 0) {
//                output.write(buffer, 0, length)
//            }
//            //Close the streams
//            output.flush()
//            output.close()
//            fis.close()
//        } else {
//            printToastMSG(applicationContext, "failed to save db!")
//        }
//    }

//    //creating a folder and saving txt file with network data
//    @Throws(IOException::class)
//    private fun createFolder(mbps: String) {
//        val localFolder =
//            File(Environment.getExternalStorageDirectory().absolutePath, Constants.FILE_NAME)
//        if (!localFolder.exists()) {
//            localFolder.mkdirs()
//        }
//        /* SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
//        Date now = new Date();*/
//        val fileName = "network_usage" + ".txt" //like 2016_01_12.txt
//        val file = File(localFolder, fileName)
//        val writer = FileWriter(file, true)
//        writer.append(mbps).append("\n\n")
//        writer.flush()
//        writer.close()
//        if (!file.exists()) {
//            // file.delete(); //you might want to check if delete was successful
//            try {
//                file.createNewFile()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                return
//            }
//        }
//        //if file not exists the download
//        val mFilepath = file.absolutePath
//        sharedPreference.write(SharedPrefKey.FILE_PATH, localFolder.absolutePath)
//        printLog("filename", mFilepath)
//    }
//
//    //creating a folder and saving txt file with network data
//    @Throws(IOException::class)
//    private fun createCredFolder() {
//        val mPass = sharedPreference.read(SharedPrefKey.PASSWORD_DB, "")
//        val localFolder =
//            File(Environment.getExternalStorageDirectory().absolutePath, Constants.FILE_NAME)
//        if (!localFolder.exists()) {
//            localFolder.mkdirs()
//        }
//        val fileName = "credentials" + ".txt" //like 2016_01_12.txt
//        val file = File(localFolder, fileName)
//        val writer = FileWriter(file, false)
//        writer.append("Password :- " + "park_loyalty")
//        writer.flush()
//        writer.close()
//        if (!file.exists()) {
//            // file.delete(); //you might want to check if delete was successful
//            try {
//                file.createNewFile()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                return
//            }
//        }
//
//        //if file not exists the download
//        val mFilepath = localFolder.absolutePath
//        sharedPreference.write(SharedPrefKey.FILE_PATH, mFilepath)
//        //LogUtil.printLog("filename",mFilepath);
//    }

    override fun onBackPressed() {
        if (backpressCloseDrawer()) {
            // close activity when drawer is closed
            super.onBackPressed()
        }
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
}