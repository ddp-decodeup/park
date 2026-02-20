package com.parkloyalty.lpr.scan.ui.check_setup.activity

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Observer
import butterknife.ButterKnife
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.getSettingFileValuesForCMDPrinting
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SAVEPRINTBITMAPDELAYTIME
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.printer.PrinterType
import com.parkloyalty.lpr.scan.ui.printer.ZebraPrinterUseCase
import com.parkloyalty.lpr.scan.ui.reprint.ReprintReuploadActivity
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapFIleViewModel
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.Links
import com.parkloyalty.lpr.scan.ui.xfprinter.XfPrinterUseCase
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSGForErrorWarning
import com.parkloyalty.lpr.scan.util.PermissionUtils
import com.parkloyalty.lpr.scan.util.permissions.BluetoothPermissionUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.getValue

class CitationHistoryPrinterActivity : BaseActivity(), PrintInterface, CustomDialogHelper {

    private var mContext: Context? = null
    private var mDb: AppDatabase? = null


    private var mLprNumber:String? = ""
    private var mState:String? = ""
    private var mCitationNumber:String? = ""
    private var printcommand:StringBuilder?=null
    private var printBitmapUrl: String? = ""
    private var savePrintImagePath: File? = null

    private val mDownloadBitmapFIleViewModel: DownloadBitmapFIleViewModel? by viewModels()

    private var zebraPrinterUseCase: ZebraPrinterUseCase? = null

    private var xfPrinterUseCase: XfPrinterUseCase? = null


    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Nothing to implement here
            } else {
                BluetoothPermissionUtil.requestBluetoothSetting(this@CitationHistoryPrinterActivity)
            }
        }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citation_history_print)
        setFullScreenUI()
        ButterKnife.bind(this)
//        printcommand = StringBuilder()

        init()

        Handler(Looper.getMainLooper()).postDelayed({
            showProgressLoader("Printing....")
        }, 100)

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            zebraPrinterUseCase = ZebraPrinterUseCase()
            zebraPrinterUseCase?.setPrintInterfaceCallback(this)
            zebraPrinterUseCase?.initialize(
                activity = this@CitationHistoryPrinterActivity,
                contentResolver = contentResolver,
                sharedPreference = sharedPreference
            )
        }else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
            LogUtil.printLog("==>Printer","Inside")
            xfPrinterUseCase = XfPrinterUseCase(this)
            lifecycle.addObserver(xfPrinterUseCase!!)
            xfPrinterUseCase?.setPrintInterfaceCallback(this)
            xfPrinterUseCase?.initialize(this@CitationHistoryPrinterActivity)
        }

        BluetoothPermissionUtil.checkBluetoothPermissions(
            activity = this,
            permissionLauncher = bluetoothPermissionLauncher
        ) {
            LogUtil.printLog("test","test")
            //Nothing to implement here in onCreate, This will check bluetooth permission and redirect user to allow bluetooth permission, in case misssed
        }
    }

    private fun addObservers() {
        mDownloadBitmapFIleViewModel?.response?.observe(this, downloadBitmapFileResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mDownloadBitmapFIleViewModel?.response?.removeObserver(downloadBitmapFileResponseObserver)
    }

    private val downloadBitmapFileResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponseForCitationData(
            apiResponse,
            DynamicAPIPath.POST_DOWNLOAD_FILE
        )
    }

    /* Call Api For Ticket Cancel */
    private fun callDownloadBitmapApi(downloadLink : String) {
        if (isInternetAvailable(this@CitationHistoryPrinterActivity)) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = "CitationImages"
                val links = Links()
                links.img1 = downloadLink
                downloadBitmapRequest.links = links
                mDownloadBitmapFIleViewModel?.downloadBitmapAPI(downloadBitmapRequest)
            } else {
            LogUtil.printToastMSG(applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
            }
    }

    /*Api response */
    private fun consumeResponseForCitationData(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.POST_DOWNLOAD_FILE,
                                ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                if (responseModel.metadata!![0].url!!.length > 0) {
                                    if (responseModel != null && responseModel.isStatus) {
                                        if (PermissionUtils.requestCameraAndStoragePermission(this@CitationHistoryPrinterActivity) && responseModel.metadata!![0].url?.length!! > 0) {
                                            DownloadingPrintBitmapFromUrl().execute(
                                                responseModel.metadata!![0].url)
                                        }
                                    }
                                }
                            }
                            dismissLoader()
                        }

                    } catch (e: Exception) {
                        //token expires
                        dismissLoader()
//                        logout(mContext!!)
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
            else ->{}
        }

    }

    inner class DownloadingPrintBitmapFromUrl : AsyncTask<String?, Int?, String?>() {
        public override fun onPreExecute() {
            super.onPreExecute()
            showProgressLoader("downloading facsimile")
        }

        override fun doInBackground(vararg url: String?): String? {
            if (!TextUtils.isEmpty(url[0])) {
                try {
//                        val files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
//                        val files1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+File.separator+Constants.FILE_NAME+Constants.CAMERA).listFiles()

                    val mydir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Constants.FILE_NAME + Constants.CAMERA
                    )
                    mydir.deleteRecursively()
                    if (!mydir.exists()) {
                        mydir.mkdirs()
                    }

                    val file = File(mydir.absolutePath, "print_bitmap_download" + ".jpg")

                    if (file.exists()) file.delete()
                    val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val downloadUri = Uri.parse((url[0])!!)
                    val request = DownloadManager.Request(downloadUri)
                    request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                    )
                        .setAllowedOverRoaming(false)
                        .setTitle("Downloading")
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            Constants.FILE_NAME + Constants.CAMERA +File.separator+"print_bitmap_download" + ".jpg"
                        )
                    manager?.enqueue(request)
                    MediaScannerConnection.scanFile(
                        this@CitationHistoryPrinterActivity, arrayOf<String>(file.toString()), null
                    ) { path, uri -> }

                    return mydir.absolutePath + File.separator + "print_bitmap_download" + ".jpg"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                return ""
            }
            return ""
        }

        public override fun onPostExecute(s: String?) {
            try {
                if (s != null && s.length > 6) {
                    sharedPreference.write(SharedPrefKey.REPRINT_PRINT_BITMAP, s)
                    dismissLoader()
                }

                sendDataForPrint()

                //                getBitmapFromUrl(s);
            } catch (e: Exception) {
                LogUtil.printLog("error mesg", e.message)
                sendDataForPrint()
            }
            super.onPostExecute(s)
        }
    }


    override fun onResume() {
        super.onResume()
        addObservers()
    }

    override fun onStop() {
        super.onStop()
        removeObservers()

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            zebraPrinterUseCase?.disconnect()
        }
    }

    private fun init() {
        AppUtils.printQueryStringBuilder.clear()
        AppUtils.clearYAxisSet()
        AppUtils.clearDrawableElementList()
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        printcommand = StringBuilder()
        setToolbar()
        getCitationNumberId()

    }

    private fun getCitationNumberId() {
        val intent = intent
        if (intent != null) {
            if (intent.hasExtra("citationNumber")) {
                sharedPreference.write(
                    SharedPrefKey.CITATION_NUMBER_FOR_PRINT, intent.getStringExtra("citationNumber")
                )
            }

            if (intent.hasExtra("lpr_number")) {
                mLprNumber = intent.getStringExtra("lpr_number")
            }

            mState = if (intent.hasExtra("State")) intent.getStringExtra("State") else ""

            if (intent.hasExtra("print_bitmap")) {
                printBitmapUrl = intent.getStringExtra("print_bitmap")
            }

            if (AppUtils.isSiteSupportCommandPrinting(getSettingFileValuesForCMDPrinting())) {
                val textCommand = intent.getStringExtra("printerQuery")
                printcommand!!.append(textCommand)
                AppUtils.printQueryStringBuilder.append(textCommand)


                Handler(Looper.getMainLooper()).postDelayed({
                    sendDataForPrint()
                }, 100)
            } else {
                callDownloadBitmapApi(printBitmapUrl.nullSafety())
            }
        } else {

        }
    }

    fun sendDataForPrint() {
        BluetoothPermissionUtil.checkBluetoothPermissions(
            activity = this,
            permissionLauncher = bluetoothPermissionLauncher
        ) {
            if (AppUtils.isSiteSupportCommandPrinting(getSettingFileValuesForCMDPrinting())) {
                savePrintImagePath = File("")
            } else {
                val filePath = sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP, "")
                savePrintImagePath = File(filePath)
            }

            if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
//                zebraPrinterUseCase?.mPrintFromCitationHistory(
//                    savePrintImagePath!!, this@CitationHistoryPrinterActivity,
//                    "TicketDetails", "R", mCitationNumber,
//                    "", mState, mLprNumber, "", printcommand!!
//                )
                mainScope.launch {
                    delay(SAVEPRINTBITMAPDELAYTIME)
                    zebraPrinterUseCase?.mPrintDownloadFacsimileImage(
                        savePrintImagePath!!,
                        this@CitationHistoryPrinterActivity,
                        "TicketDetails",
                        "R",
                        mCitationNumber,
                        "",
                        mState,
                        mLprNumber,
                        ""
                    )
                }
            }
        }

    }

    override fun onActionSuccess(value: String) {
        Handler(Looper.getMainLooper()).postDelayed({
           finish()
        }, 300)
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

    override fun onYesButtonClick() {
        finish()
    }
    override fun onNoButtonClick() {
        finish()
    }
    override fun onYesButtonClickParam(msg: String?) {
        finish()
    }

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }

}