package com.parkloyalty.lpr.scan.siiprinter

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.seikoinstruments.sdk.thermalprinter.*
import com.seikoinstruments.sdk.thermalprinter.printerenum.*
import kotlinx.coroutines.async
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


abstract class SeikoPrinterActivity : BaseActivity(), CallbackFunctionListener, BarcodeScannerListener {

    private val REQUEST_ENABLE_BLUETOOTH = 1001
    private val REQUEST_SELECT_DEVICE = 1002
    private val REQUEST_SETTING_PROPERTY = 1003
    private val REQUEST_LOCATION_SETTING_RESOLUTION = 1004
    private val REQUEST_CODE_WIFI = 1005
    private val REQUEST_CODE_BLUETOOTH = 1006
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 2111
    private var printCallBack: PrintInterface? = null
    private var context: Context? = null
    private var isErrorUploading: String = ""
    private var mLineX2: Int = 604
    private var mPageWidth: Int = 576
    private var mPageHeight: Int = 1600
    private var yValue: Int = 30
    private var isQRCodePrintlayout: Boolean = false
    private var settingsList: List<DatasetResponse>? = null
    private var mQRCodeValue: String? = ""

    private var mIssuranceModel: CitationInsurranceDatabaseModel? =
        CitationInsurranceDatabaseModel()

    /** PrinterManager SDK  */
    private var mPrinterManager: PrinterManager? = null

    /** SmartLabelManager SDK  */
    private val mSmartLabelManager: SmartLabelManager? = null

    /** Select port  */
    private val mSelectPort = PrinterManager.PRINTER_TYPE_BLUETOOTH

    /** Select file  */
    private val mSelectPath = ""

    /** Set listener */
    private val mCallbackFunctionListener: CallbackFunctionListener? = null
    private val mBarcodeScannerListener: BarcodeScannerListener? = null

    /** Barcode data display method */
    private val mIsStringDisplay = false

    /** Handler to write on log */
    private val mHandler = Handler()

    private var macAddress: String = ""
    private var mDb: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imageprintlayout)
        context = this

        if (ContextCompat.checkSelfPermission(
                this@SeikoPrinterActivity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(
                this@SeikoPrinterActivity,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this@SeikoPrinterActivity, arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ), BLUETOOTH_PERMISSION_REQUEST_CODE
                )
            }
        }

        getPairedPrintersTo()
        connectToPrinter()
        getCitationDataFromDB()
        getQRCodeValueFromSetting()
    }
    fun getCitationDataFromDB()
    {
        mDb = BaseApplication.instance?.getAppDatabase()

    }
    private fun SetContextPrinterActivity() {
        //initializing the callback object from the constructor
        printCallBack = context as PrintInterface?
    }
    private fun getPairedPrintersTo(): String {

        val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices: Set<BluetoothDevice> = mBluetoothAdapter.bondedDevices
        val pairedDevicesList: ArrayList<BluetoothDevice>? = ArrayList()
        for (device: BluetoothDevice in pairedDevices) {
            if (isBluetoothPrinter(device)) pairedDevicesList!!.add(device)
        }
        if (pairedDevicesList != null && pairedDevicesList.size > 0) {
            macAddress = pairedDevicesList.get(0).address.toString()
        }

        if (mPrinterManager == null) {
            mPrinterManager = PrinterManager(applicationContext)
        }

//        mPrinterManager =
//            pairedDevicesList!!.get(0).address as PrinterManager
//        val application: SampleApplication = this.application as SampleApplication
        BaseApplication.instance?.setPrinterManager(mPrinterManager)
        Toast.makeText(context,macAddress.toString(),Toast.LENGTH_SHORT).show()

        return macAddress
    }

    private fun connectToPrinter()
    {
        setProperty()
        if(macAddress!!.isNotEmpty()) {
            try {
                var model: Int = 304
                val pref = PreferenceManager.getDefaultSharedPreferences(this@SeikoPrinterActivity)
                val secure = pref.getBoolean(getString(R.string.key_secure_connection), true)


                mPrinterManager!!.connect(model, macAddress, secure)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setProperty() {
        if (mPrinterManager != null) {
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            val sendTimeout: Int =  10000
            val receiveTimeout: Int = 10000
            val socketKeepingTime: Int =  300000

            mPrinterManager!!.setSendTimeout(sendTimeout)
            mPrinterManager!!.setReceiveTimeout(receiveTimeout)
            mPrinterManager!!.setSocketKeepingTime(socketKeepingTime)

            val internationalCharacter: Int = 0
            val codePage: Int = 16

            mPrinterManager!!.setInternationalCharacter(internationalCharacter)
            mPrinterManager!!.setCodePage(codePage)
        }
    }

    private fun isBluetoothPrinter(bluetoothDevice: BluetoothDevice): Boolean {
        return (bluetoothDevice.bluetoothClass
            .majorDeviceClass == BluetoothClass.Device.Major.IMAGING
                || bluetoothDevice.bluetoothClass
            .majorDeviceClass == BluetoothClass.Device.Major.UNCATEGORIZED)
    }


    /**
     * Implementation when pressing the [printPageMode] button.
     */
    protected fun getBitmapFromPath(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                                    mTicketNumber: String?,mAmount: String?,mState: String?,mLprNumber: String?
                                    ,mErrorUploading: String?,printerCommand : StringBuilder) {
        var ret = 0
        var msg: String = ""
        this.context = context
        this.isErrorUploading = mErrorUploading!!
        SetContextPrinterActivity()
//        writeLog(
//            getString(R.string.print_page_mode),
//            com.seikoinstruments.sdk.thermalprinter.sample.MainActivity.WRITE_LOG_IN
//        )

        val fileName = "white_print.png"
        val path = applicationContext.getExternalFilesDir(null)!!.absolutePath

        try {
            val `in` = assets.open(fileName)
            try {
                val file = File(path, fileName)
                val out = FileOutputStream(file.path)
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
            } catch (e: IOException) {
            }
            `in`.close()
        } catch (e: IOException) {
        }

        try {
            mIssuranceModel = mDb?.dbDAO?.getCitationWithTicket(mTicketNumber)
            val currentString = mIssuranceModel?.citationData?.ticketDatePrint.toString()
            val separated = currentString.split(" ").toTypedArray()
            mPrinterManager!!.enterPageMode()
            mPrinterManager!!.setPageModeArea(
                0,
                0,
                mPageWidth,
                mPageHeight
            )
//            ------------------------------CITATION-------------------------

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.mTicketNumberX!!.toInt(),
                mIssuranceModel!!.citationData!!.mTicketNumberY!!.toInt(),
                mIssuranceModel!!.citationData!!.ticketNumberLabel,
            )
            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.mTicketNumberX!!.toInt(),
                (mIssuranceModel!!.citationData!!.mTicketNumberY!!.toInt()+yValue),
                mIssuranceModel!!.citationData!!.ticketNumber,
            )

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.mTicketDateX.toInt(),
                mIssuranceModel!!.citationData!!.mTicketDateY.toInt(),
                getString(R.string.scr_lbl_date_without_colon)
            )

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.mTicketDateX.toInt(),
                (mIssuranceModel!!.citationData!!.mTicketDateY.toInt()+yValue),
                separated[1] + "/" + separated[0] + "/" + separated[2]
            )

            mPrinterManager!!.printPageModeText(
                (mIssuranceModel!!.citationData!!.mTicketDateX.toInt()+140),
                mIssuranceModel!!.citationData!!.mTicketDateY.toInt(),
                getString(R.string.scr_lbl_print_issue_time)
            )

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.mTicketDateX.toInt()+140,
                (mIssuranceModel!!.citationData!!.mTicketDateY.toInt()+yValue),
                separated[3] + " " + separated[4]
            )

                mPrinterManager!!.printPageModeLine(
                5,
                    (mIssuranceModel!!.citationData!!.mTicketNumberY!!.toInt()+40),
                    mLineX2,
                    (mIssuranceModel!!.citationData!!.mTicketNumberY!!.toInt()+40),
                LineStyle.LINESTYLE_THIN
            )
//------------------------------------VEHICLE------------------------------------------------------
            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.vehicle!!.mLicensePlateX!!.toInt(),
                mIssuranceModel!!.citationData!!.vehicle!!.mLicensePlateY!!.toInt(),
                mIssuranceModel!!.citationData!!.vehicle!!.licensePlateLabel,
            )
            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.vehicle!!.mLicensePlateX!!.toInt(),
                (mIssuranceModel!!.citationData!!.vehicle!!.mLicensePlateY!!.toInt()+yValue),
                mIssuranceModel!!.citationData!!.vehicle!!.licensePlate,
            )

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.vehicle!!.mStateX!!.toInt(),
                mIssuranceModel!!.citationData!!.vehicle!!.mStateY!!.toInt(),
                mIssuranceModel!!.citationData!!.vehicle!!.stateLabel,
            )
            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.vehicle!!.mStateX!!.toInt(),
                (mIssuranceModel!!.citationData!!.vehicle!!.mStateY!!.toInt()+yValue),
                mIssuranceModel!!.citationData!!.vehicle!!.state,
            )

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.vehicle!!.mMakeX!!.toInt(),
                mIssuranceModel!!.citationData!!.vehicle!!.mMakeY!!.toInt(),
                mIssuranceModel!!.citationData!!.vehicle!!.makeFullNameLabel,
            )
            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.vehicle!!.mMakeX!!.toInt(),
                (mIssuranceModel!!.citationData!!.vehicle!!.mMakeY!!.toInt()+yValue),
                mIssuranceModel!!.citationData!!.vehicle!!.make,
            )

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.location!!.mBlockX!!.toInt(),
                mIssuranceModel!!.citationData!!.location!!.mBlockY!!.toInt(),
                mIssuranceModel!!.citationData!!.location!!.lotLabel,
            )
            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.location!!.mBlockX!!.toInt(),
                (mIssuranceModel!!.citationData!!.location!!.mBlockY!!.toInt()+yValue),
                mIssuranceModel!!.citationData!!.location!!.lot+" "+mIssuranceModel!!.citationData!!.location!!.block
                        +" "+mIssuranceModel!!.citationData!!.location!!.street,
            )

            mPrinterManager!!.printPageModeLine(
                5,
                (mIssuranceModel!!.citationData!!.location!!.mBlockY!!.toInt()+40),
                mLineX2,
                (mIssuranceModel!!.citationData!!.location!!.mBlockY!!.toInt()+40),
                LineStyle.LINESTYLE_THIN
            )
//            -------------------------VIOLATION----------------------------------

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.voilation!!.mViolationX!!.toInt(),
                mIssuranceModel!!.citationData!!.voilation!!.mViolationY!!.toInt(),
                mIssuranceModel!!.citationData!!.voilation!!.codeLabel,
            )

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.voilation!!.mViolationX!!.toInt(),
                mIssuranceModel!!.citationData!!.voilation!!.mViolationY!!.toInt()+yValue,
                mIssuranceModel!!.citationData!!.voilation!!.code,
            )

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.voilation!!.mLocationDescrX!!.toInt(),
                mIssuranceModel!!.citationData!!.voilation!!.mLocationDescrY!!.toInt(),
                mIssuranceModel!!.citationData!!.voilation!!.locationDescr,
            )

//            val amount =
//                mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
//                    .split(".").toTypedArray()[0]

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.voilation!!.mAmountX!!.toInt(),
                mIssuranceModel!!.citationData!!.voilation!!.mAmountY!!.toInt(),
                mIssuranceModel!!.citationData!!.voilation!!.amountLabel+" $ "+
                        mIssuranceModel?.citationData?.voilation?.amount,)

            mPrinterManager!!.printPageModeLine(
                5,
                (mIssuranceModel!!.citationData!!.voilation!!.mAmountY!!.toInt()+10),
                mLineX2,
                (mIssuranceModel!!.citationData!!.voilation!!.mAmountY!!.toInt()+10),
                LineStyle.LINESTYLE_THIN
            )

//            -------------------REMARK---------------------------------------------------------

            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.mRemarkX!!.toInt(),
                mIssuranceModel!!.citationData!!.mRemarkY!!.toInt(),
                mIssuranceModel!!.citationData!!.locationRemarksLabel,
            )
            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.mRemarkX!!.toInt(),
                mIssuranceModel!!.citationData!!.mRemarkY!!.toInt()+yValue,
                mIssuranceModel!!.citationData!!.locationRemarks,
            )
//-----------------------------------QR CODE
            mPrinterManager!!.printPageModeText(
                mIssuranceModel!!.citationData!!.mRemarkX!!.toInt(),
                mIssuranceModel!!.citationData!!.mRemarkY!!.toInt()+200,
                "Scan to Pay"
            )
            val errorCorrection = ErrorCorrection.valueOf("PDF417_ERROR_CORRECTION_0")
            val moduleSize = ModuleSize.valueOf("QR_MODULE_SIZE_2")
            val alignment = PrintAlignment.valueOf("ALIGNMENT_CENTER")
            val model = QrModel.valueOf("QR_MODEL_1")
            mPrinterManager!!.printPageModeQRcode(
                200,
                500,
                mQRCodeValue,
                errorCorrection,
                moduleSize
            )

//            ----------------------------BG IMAGE---------------------------------------------------
            mPrinterManager!!.printPageModeImageFile(
                10,
                212,
                "$path/$fileName",
                Dithering.DITHERING_DISABLE
            )
            mPrinterManager!!.setPageModeArea(
                104,
                60,
                145,
                145
            )
            mPrinterManager!!.setPageModeDirection(
                Direction.DIRECTION_BOTTOM_TO_TOP
            )
//            mPrinterManager!!.printPageModeBarcode(
//                20,
//                132,
//                BarcodeSymbol.BARCODE_SYMBOL_CODE128,
//                byteArrayOf(0x67, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x68),
//                ModuleSize.BARCODE_MODULE_WIDTH_2,
//                80,
//                HriPosition.HRI_POSITION_ABOVE,
//                CharacterFont.FONT_A
//            )
            try {
                mPrinterManager!!.printPageModeText(
                    mIssuranceModel!!.citationData!!.mRemarkX!!.toInt(),
                    mIssuranceModel!!.citationData!!.mRemarkY!!.toInt()+100,
                    "Scan to Pay"
                )
                val errorCorrection = ErrorCorrection.valueOf("PDF417_ERROR_CORRECTION_0")
                val moduleSize = ModuleSize.valueOf("QR_MODULE_SIZE_2")
                val alignment = PrintAlignment.valueOf("ALIGNMENT_CENTER")
                val model = QrModel.valueOf("QR_MODEL_1")
//                mPrinterManager!!.printQRcode(
//                    mQRCodeValue,
//                    errorCorrection,
//                    moduleSize,
//                    alignment,
//                    model
//                )
                msg =  "Qr Code" +  "Ok"
            } catch (e: PrinterException) {
                ret = e.errorCode
                msg =  "Qr Code" + getString(R.string.msg_ng, ret)
            }

            try {
//                val feedPosition = FeedPosition.valueOf("FEED_CUTTER")
                val feedPosition = FeedPosition.valueOf("FEED_NEXT_TOF")
                mPrinterManager!!.feedPosition(
                    feedPosition
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mPrinterManager!!.printPageMode(
                CuttingMethod.CUT_FULL
            )
            mPrinterManager!!.exitPageMode()
            msg = getString(R.string.print_page_mode) + "OK"
        } catch (e: PrinterException) {
            ret = e.errorCode
            msg = getString(R.string.print_page_mode) + getString(R.string.msg_ng, ret)
        }

        finally {
            printCallBack!!.onActionSuccess(isErrorUploading)
//                    helper.dismissLoadingDialog()
//                    Looper.myLooper()!!.quit()
        }
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onStatusChanged(p0: Int) {
        //TODO("Not yet implemented")
    }

    override fun onBarcodeScannerReadData(p0: ByteArray?) {
        //TODO("Not yet implemented")
    }

    override fun onBarcodeScannerChangedOnline() {
        //TODO("Not yet implemented")
    }

    override fun onBarcodeScannerChangedOffline() {
        //TODO("Not yet implemented")
    }

    private fun getQRCodeValueFromSetting() {
        try {
            settingsList = ArrayList()
            ioScope.async {
                settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
                mainScope.async {
                    if (settingsList != null && settingsList!!.size > 0) {
                        for (i in settingsList!!.indices) {
                            if (settingsList!![i].type.equals("QRCODE_URL", ignoreCase = true)
                                && !settingsList!![i].mValue!!.isEmpty() &&
                                !settingsList!![i].mValue.equals("NO", ignoreCase = true)
                                || settingsList!![i].type.equals("QRCODE_URL", ignoreCase = true) &&
                                settingsList!![i].mValue.equals("YES", ignoreCase = true)
                            ) {
                                isQRCodePrintlayout = true
                                mQRCodeValue = settingsList!![i].mValue
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