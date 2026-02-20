package com.parkloyalty.lpr.scan.ui.printer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.LogUtil
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.comm.TcpConnection
import com.zebra.sdk.device.ZebraIllegalArgumentException
import com.zebra.sdk.graphics.internal.ZebraImageAndroid
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.printer.ZebraPrinterFactory
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException


open class PrinterActivity1 : BaseActivity() {
    //    private val connection: Connection? = null
//    private var imageSelectionSpinner: Spinner? = null
//    private var cb: CheckBox? = null
//    private var angleSpinner: Spinner? = null
//    private val myDialog: ProgressDialog? = null
//    private var btRadioButton: RadioButton? = null
    private var macAddressEditText: EditText? = null
    private var ipAddressEditText: EditText? = null
    private var portNumberEditText: EditText? = null
    //    private var printStoragePath: EditText? = null
    private val helper: UIHelper = UIHelper(this)
    private var zebraFooterLogo: ImageView? = null
    private var rotationAngle: Int = 180
    private var printCallBack: PrintInterface? = null
    private var context: Context? = null
    private var mFrom: String? = null
    private var mLabel: String? = null
    private var mTicketNumber: String? = null
    private var mPrinterCommand: java.lang.StringBuilder? = null
    private var mAmount: String? = null
    private var mState: String? = null
    private var mLprNumber: String? = null
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 2111
    private var settingsList: List<DatasetResponse>? = null
    private var byteArray : ByteArray?= null
//    private var mDb: AppDatabase? = null
//    private var isBarCodePrintlayout: Boolean = true
//    private var isQRCodePrintlayout: Boolean = false
//    private var mQRCodeValue: String? = null
//    private var mIssuranceModel: CitationInsurranceDatabaseModel? =
//            CitationInsurranceDatabaseModel()

    //    private var mVehicleListPrint = ArrayList<VehicleListModel>()
    private var isErrorUploading: String = ""

    private var screenHeight = 0
    private var screenWidth = 0
    private var isTicketScreen = false

    private var XQRCode = ""
    private var YQRCode = ""
    private var mQrCodeSize = 2

    private var XQRCodeLable = ""
    private var YQRCodeLable = ""
    private var qrCodeLabel = ""
    private var FontQRCodeLable = ""
    private var mBottomAddressSecondLine = ""
    private var mBottomAddressThirdLine = ""
    private var mBottomAddressFourthLine = ""

    private var XBottomAddress = "0"
    private var YBottomAddress = "0"
    private var YBottomAddressLine1:Int = 0
    private var YBottomAddressLine2:Int = 0
    private var YBottomAddressLine3:Int = 0
    private var BottomAddressValue = ""
    private var BottomAddressValueArray = arrayOfNulls<String>(8)
    private var BottomAddress2ValueArray = arrayOfNulls<String>(5)
    private var BottomAddress3ValueArray = arrayOfNulls<String>(5)
    private var BottomAddress4ValueArray = arrayOfNulls<String>(8)
    private var FontBottomAddress = ""
    private var topSetFF = "150 4"
    private var bottomSetFF = "1 1"
    private var canvasHeight = "10"
    private var printintByCMD = false
    private var isPrintingCommandDataInTxtFile = false
    private var printerConnection: Connection? = null

    private var mCitationNumberValue = ""
    private var mCitationNumberLable = ""
    private var mCitationNumberX = ""
    private var mCitationNumberY = ""
    private var mCitationNumberFont = ""

    private var XBottomAddress2 = "0"
    private var YBottomAddress2 = "0"
    private var FontBottomAddress2 = ""
    private var BottomAddressValue2 = ""

    private var XBottomAddress3 = "0"
    private var YBottomAddress3 = "0"
    private var FontBottomAddress3 = ""
    private var BottomAddressValue3 = ""

    private var XBottomAddress4 = "0"
    private var YBottomAddress4 = "0"
    private var FontBottomAddress4 = ""
    private var BottomAddressValue4 = ""

    private var XBottomSchedule = "0"
    private var YBottomSchedule = "0"
    private var FontBottomSchedule = ""
    private var BottomScheduleValue =""
    private var mBottomAddressQuery = StringBuilder()

    var printerWriterCmd =java.lang.StringBuilder();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imageprintlayout)
        context = this
//        mDb = BaseApplication.instance?.getAppDatabase()
        val settings: SharedPreferences = getSharedPreferences(PREFS_NAME, 0)
        zebraFooterLogo = findViewById<View>(R.id.zebraFooterLogo) as ImageView?
        ipAddressEditText = findViewById<View>(R.id.ipAddressInput) as EditText?
        val ip: String? = settings.getString(tcpAddressKey, "")
        ipAddressEditText!!.setText(ip)
        portNumberEditText = findViewById<View>(R.id.portInput) as EditText?
        val port: String? = settings.getString(tcpPortKey, "")
        portNumberEditText!!.setText(port)
        macAddressEditText = findViewById<View>(R.id.macInput) as EditText?
//        val mac: String? = settings.getString(bluetoothAddressKey, "")
        val mac: String? = SettingsHelper.getBluetoothAddressKey(this@PrinterActivity1)
        macAddressEditText!!.setText(mac)
        val t2: TextView = findViewById<View>(R.id.launchpad_link) as TextView
        t2.movementMethod = LinkMovementMethod.getInstance()


        if (ContextCompat.checkSelfPermission(
                this@PrinterActivity1,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(
                this@PrinterActivity1,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this@PrinterActivity1, arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ), BLUETOOTH_PERMISSION_REQUEST_CODE
                )
            }
        }
        if(macAddressEditText!!.text!!.isEmpty()) {
            getPairedPrintersTo()
        }
        setScreenResolution()
        /**
         * Barcode and Qrcode setting
         */
//        setLayoutVisibilityBasedOnSettingResponse()

        XQRCodeLable = sharedPreference.read(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_X,"").toString()
        YQRCodeLable = sharedPreference.read(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_Y,"").toString()
        qrCodeLabel = sharedPreference.read(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT,"").toString()
        FontQRCodeLable = sharedPreference.read(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_FONT,"").toString()

        XBottomAddress = sharedPreference.read(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_X,"").toString()
        YBottomAddress = sharedPreference.read(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_Y,"").toString()
        BottomAddressValue = sharedPreference.read(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT,"").toString()
        FontBottomAddress = sharedPreference.read(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_FONT,"").toString()

        XBottomAddress2 = sharedPreference.read(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_X,"").toString()
        YBottomAddress2 = sharedPreference.read(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_Y,"").toString()
        BottomAddressValue2 = sharedPreference.read(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT,"").toString()
        FontBottomAddress2 = sharedPreference.read(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_FONT,"").toString()

        XBottomAddress3 = sharedPreference.read(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_X,"").toString()
        YBottomAddress3 = sharedPreference.read(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_Y,"").toString()
        BottomAddressValue3 = sharedPreference.read(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT,"").toString()
        FontBottomAddress3 = sharedPreference.read(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_FONT,"").toString()

        XBottomAddress4 = sharedPreference.read(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_X,"").toString()
        YBottomAddress4 = sharedPreference.read(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_Y,"").toString()
        BottomAddressValue4 = sharedPreference.read(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT,"").toString()
        FontBottomAddress4 = sharedPreference.read(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_FONT,"").toString()

        XBottomSchedule = sharedPreference.read(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_X,"").toString()
        YBottomSchedule = sharedPreference.read(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_Y,"").toString()
        BottomScheduleValue = sharedPreference.read(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT,"").toString()
        FontBottomSchedule = sharedPreference.read(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_FONT,"").toString()

        XQRCode = sharedPreference.read(
            SharedPrefKey.QRCODE_FOR_PRINT_X,"").toString()
        YQRCode = sharedPreference.read(
            SharedPrefKey.QRCODE_FOR_PRINT_Y,"").toString()

        mCitationNumberValue = sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_FOR_PRINT,"").toString()
        mCitationNumberLable= sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_LABEL_FOR_PRINT,"").toString()
        mCitationNumberX= sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_X,"").toString()
        mCitationNumberY= sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_Y,"").toString()
        mCitationNumberFont= sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_FONT,"").toString()

            getPrintSetFFValueFromSettingFile()

        if(YBottomAddress.isNotEmpty()  && BottomAddressValue!!.isNotEmpty()) {
            BottomAddressValueArray = BottomAddressValue.split("#").toTypedArray()
            YBottomAddressLine1 = Integer.parseInt(YBottomAddress) + 20
            YBottomAddressLine2 = YBottomAddressLine1 + 20
            YBottomAddressLine3 = YBottomAddressLine2 + 20
            mBottomAddressSecondLine = if (BottomAddressValueArray.size > 1) BottomAddressValueArray[1].toString() else ""
            mBottomAddressThirdLine = if (BottomAddressValueArray.size > 2) BottomAddressValueArray[2].toString() else ""
            mBottomAddressFourthLine = if (BottomAddressValueArray.size > 3) BottomAddressValueArray[3].toString() else ""
        }
//        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
//        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true))
//        {
        try {
            BottomAddress2ValueArray = BottomAddressValue2.split("#").toTypedArray()
            BottomAddress3ValueArray = BottomAddressValue3.split("#").toTypedArray()
            BottomAddress4ValueArray = BottomAddressValue4.split("#").toTypedArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        }
//            }

            mQrCodeSize = 4
        try {
            if(printerConnection==null) {
                printerConnection = getZebraPrinterConn()
            }
//            if(BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = false)||BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = false)||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = false)) {
            if (mBottomAddressQuery != null) {
                mBottomAddressQuery!!.clear()
            }

            mBottomAddressText()

        }catch ( e: Exception)
        {
            e.printStackTrace()
        }

    }
    var fos: FileOutputStream? = null
    override fun onStop() {
        super.onStop()
        if (printerConnection != null && printerConnection!!.isConnected) {
            printerConnection!!.close()
        }
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
                        this@PrinterActivity1,
                        getString(R.string.error_bluetooth_permission_is_not_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if(macAddressEditText!!.text!!.isEmpty()) {
                        getPairedPrintersTo()
                    }
                }
            }
        }
    }

    private fun SetContextPrinterActivity() {
        //initializing the callback object from the constructor
        printCallBack = context as PrintInterface?
    }

    //        LogUtil.printToastMSG(this,"Get Mac Address by auto"+ mac);
    private fun getPairedPrintersTo(): String {
        var mac: String = ""
        val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices: Set<BluetoothDevice> = mBluetoothAdapter.bondedDevices
        val pairedDevicesList: ArrayList<BluetoothDevice>? = ArrayList()
        for (device: BluetoothDevice in pairedDevices) {
            if (isBluetoothPrinter(device)) pairedDevicesList!!.add(device)
        }
        if (pairedDevicesList != null && pairedDevicesList.size > 0) {
            mac = pairedDevicesList.get(0).address.toString()
        }
        macAddressEditText!!.setText(mac)
        //        LogUtil.printToastMSG(this,"Get Mac Address by auto"+ mac);
        return mac
    }

    private fun isBluetoothPrinter(bluetoothDevice: BluetoothDevice): Boolean {
        return (bluetoothDevice.bluetoothClass
            .majorDeviceClass == BluetoothClass.Device.Major.IMAGING
                || bluetoothDevice.bluetoothClass
            .majorDeviceClass == BluetoothClass.Device.Major.UNCATEGORIZED)
    }

    //    var byteA = null
    fun printDialySummery(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                          mTicketNumber: String?) {
        this.context = context
        this.mFrom = mFrom
        this.mLabel = mLabel
        this.mTicketNumber = mTicketNumber
        isTicketScreen = true

        SetContextPrinterActivity()
        val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
        var myBitmap: Bitmap? = null
        try {
            myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
        } catch (e: FileNotFoundException) {
            e.message
        } catch (e: IOException) {
            e.message
        }
        printRotatedPhotoFromExternal(myBitmap, rotationAngle)
    }

    /**
     * Preview Activity
     */
    fun mPrintFacsimileImage(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                             mTicketNumber: String?,mAmount: String?,mState: String?,mLprNumber: String?
                             ,mErrorUploading: String?,printerCommand : StringBuilder) {
        this.context = context
        this.mFrom = mFrom
        this.mLabel = mLabel
        this.mTicketNumber = mTicketNumber
        this.mPrinterCommand = printerCommand
        this.mAmount = mAmount
        this.mState = mState
        this.mLprNumber = mLprNumber
        this.isErrorUploading = mErrorUploading!!
        SetContextPrinterActivity()
            isTicketScreen = false
            val filePath = sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP_OCR, "")
            val savePrintImagePath = File(filePath)

//            var savePrintImagePath: File? = null
            val imgPath: Uri = Uri.fromFile((savePrintImagePath.absoluteFile))
            var myBitmap: Bitmap? = null
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)

            } catch (e: FileNotFoundException) {
                e.message
            } catch (e: IOException) {
                e.message
            }
            printRotatedPhotoFromExternal(myBitmap, rotationAngle)


    }
    fun mPrintDownloadFacsimileImage(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                                     mTicketNumber: String?,mAmount: String?,mState: String?,mLprNumber: String?
                                     ,mErrorUploading:String?) {
        this.context = context
        this.mFrom = mFrom
        this.mLabel = mLabel
        this.mTicketNumber = mTicketNumber
        this.mAmount = mAmount
        this.mState = mState
        this.mLprNumber = mLprNumber
        this.isErrorUploading = mErrorUploading!!
        isTicketScreen = true
        SetContextPrinterActivity()
        if (AppUtils.printByCMD(printintByCMD))
        {
            isTicketScreen = false
            val filePath = sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP_OCR, "")
            val savePrintImagePath = File(filePath)

            val imgPath: Uri = Uri.fromFile((savePrintImagePath.absoluteFile))
            var myBitmap: Bitmap? = null
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
            } catch (e: FileNotFoundException) {
                e.message
            } catch (e: IOException) {
                e.message
            }
            printRotatedPhotoFromExternal(myBitmap, rotationAngle)

        }else{
            val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
            var myBitmap: Bitmap? = null
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
            } catch (e: FileNotFoundException) {
                e.message
            } catch (e: IOException) {
                e.message
            }
            printRotatedPhotoFromExternal(myBitmap, rotationAngle)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true
    }


    /**
     * This method calls the rotated image and send it to the final method.
     *
     * @param bitmap
     * @param rotationAngle
     */
    private fun printRotatedPhotoFromExternal(bitmap: Bitmap?, rotationAngle: Int) {
        var b: Bitmap? = bitmap
        if(!mLabel!!.isEmpty()) {
            b = drawTextToBitmap(bitmap, mLabel)
        }
        printBitmap(b)
    }

    private fun printBitmap(bitmap: Bitmap?) {
        Thread(object : Runnable {
            override fun run() {
                try {
                    Looper.prepare()
                    if(LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
                        printerWriterCmd!!.clear()
                        printerWriterCmd!!.append("-----------------------------\r\n")
                    }
                    getAndSaveSettings()
                    helper.showLoadingDialog("Sending image to printer")
                    LogUtil.printLogHeader("QUERY",AppUtils.printQueryStringBuilder.toString())
                    try {
                        if(printerConnection==null) {
                            printerConnection = getZebraPrinterConn()
                        }
                    }catch ( e: Exception)
                    {
                        e.printStackTrace()
                    }
                    val printQty: Int = 1 //or whatever number you want
                    printerConnection!!.open()
                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 10 2\r\n".toByteArray())
                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 10 2\r\n")

                    val printer: ZebraPrinter =
                        ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, printerConnection!!)
                    val ZEBRA_WIDTH: Float = 576f
                    //                    float ZEBRA_WIDTH = 576;
                    val width: Int = bitmap!!.width
                    val height: Int = bitmap.height
                    val aspectRatio: Float = width / ZEBRA_WIDTH //ZEBRA_RW420_WIDTH = 800f
                    val multiplier: Float = 1 / aspectRatio
                    //scale the bitmap to fit the ZEBRA_RW_420_WIDTH print
                    val bitmapToPrint1: Bitmap = Bitmap.createScaledBitmap(
                        (bitmap),
                        (width * multiplier).toInt(),
                        (height * multiplier).toInt(),
                        false
                    )
                    bitmap.recycle()
                    var newBitmapHeight: Int = bitmapToPrint1.height + 5
                    try {
                        if(canvasHeight.toInt()>100){
                            newBitmapHeight = canvasHeight.toInt()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

//                    newBitmapHeight = 1152
                    //create the Zebra object with the new Bitmap
                    val zebraImageToPrint: ZebraImageAndroid = ZebraImageAndroid(bitmapToPrint1)
                    //the image is sent to the printer and stored in R: folder
                    printer.storeImage("R:TEMP.PCX", zebraImageToPrint, -1, -1)
// garbage printing line
                    /**
                     * {command} {type} {width} {ratio} {height} {x} {y} {data} = barcode
                     * //create the print commands string
                     * {command} {font} {size} {x} {y} {data} = text
                     *
                     * QRCODE
                     * {command} {type} {x} {y} [M n] [U n]
                     * {data}
                     * <ENDQR
                     *
                     *
                     * SETFF {command} {max-feed} {skip-length}
                     * {command}: SETFF
                     * {max-feed}: Maximum unit-length the printer advances searching for the next eye-sense mark to align top of form.
                     * Valid values are 0-20,000.
                     * {skip-length}: Unit-length printer advances past top of form. Valid values are 5-50.
                     * **/
                    val PHILA: String = "     "

                    var printString: String = ""
                    if(AppUtils.mFinalQRCodeValue!!.isEmpty()){
                        printString  =
                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
                                    + "NO-PACE" + "\r\n"
                                    + "SETSP 1\r\n"
                                    + AppUtils.printQueryStringBuilder.toString() + " \r\n"
                                    + mBottomAddressQuery
                                    + "BAR-SENSE" + "\r\n"
                                    + "FORM" + "\r\n"
                                    + "PRINT" + "\r\n") //print
                    }else {
                        printString =
                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
                                    + "NO-PACE" + "\r\n"
                                    + "SETSP 1\r\n"
                                    + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
                                    + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
//                                        + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                        + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddress + " " + BottomAddressValueArray[0] + " \r\n"
//                                        + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine1 + " " + mBottomAddressSecondLine + " \r\n"
//                                        + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine2 + " " + mBottomAddressThirdLine + " \r\n"
                                    + mBottomAddressQuery
                                    + "BAR-SENSE" + "\r\n"
                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
                                    + "FORM" + "\r\n"
                                    + "PRINT" + "\r\n") //print
                    }
                    LogUtil.printLogPrinterQuery("QUERY",printString.toString())
                    printerConnection!!.write(printString.toByteArray())
                    writePrinterCommand(printString)




//delete the image at the end to prevent printer memory sutaration
                    printerConnection!!.write(("! U1 do \"file.delete\" \"R:TEMP.PCX\"\r\n").toByteArray())
                    printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 20 8\r\n".toByteArray())
//
//close the printerConnection!! with the printer
                    printerConnection!!.close()
                    //recycle the bitmap
                    bitmapToPrint1.recycle()
                } catch (e: ConnectionException) {
                    e.printStackTrace()
                    helper.showErrorDialogOnGuiThread(e.message)
                } catch (e: ZebraIllegalArgumentException) {
                    e.printStackTrace()
                    helper.showErrorDialogOnGuiThread(e.message)
                } catch (e: Exception) {
                    e.printStackTrace()
                    helper.showErrorDialogOnGuiThread(e.message)
                } finally {
                    if(LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
                        savePrintCommand()
                    }
                    printCallBack!!.onActionSuccess(isErrorUploading)
//                    helper.dismissLoadingDialog()
                    Looper.loop()
                    Looper.myLooper()!!.quit()
                }
            }
        }).start()
    }

    /**
     * {command} {type} {width} {ratio} {height} {x} {y} {data} = barcode
     * //create the print commands string
     * {command} {font} {size} {x} {y} {data} = text
     *
     * QRCODE
     * {command} {type} {x} {y} [M n] [U n]
     * {data}
     * <ENDQR
     *
     * LINE {command} {x0} {y0} {x1} {y1} {width
     *
     * IMAGE {command} {width} {height} {x} {y} {data}
     **/



    private fun isBluetoothSelected(): Boolean {
        return true
    }

    private fun getMacAddressFieldText(): String {
        return macAddressEditText!!.text.toString()
    }

    private fun getTcpAddress(): String {
        return ipAddressEditText!!.text.toString()
    }

    private fun tcpPortNumber(): String {
        return portNumberEditText!!.text.toString()
    }

    /**
     * This method checks the mode of connection.
     *
     * @return
     */
    private fun getZebraPrinterConn(): Connection {
        var portNumber: Int
        try {
            portNumber = tcpPortNumber().toInt()
        } catch (e: NumberFormatException) {
            portNumber = 0
        }
        return if (isBluetoothSelected()) BluetoothConnection(getMacAddressFieldText()) else TcpConnection(
            getTcpAddress(), portNumber
        )
    }

    /**
     * This method saves the entered address for the printer.
     */
    private fun getAndSaveSettings() {
        SettingsHelper.saveBluetoothAddress(this@PrinterActivity1, getMacAddressFieldText())
        SettingsHelper.saveIp(this@PrinterActivity1, getTcpAddress())
        SettingsHelper.savePort(this@PrinterActivity1, tcpPortNumber())

    }

    private fun createCancelProgressDialog(message: String) {}

    fun drawTextToBitmap(bitmap: Bitmap?, mText: String?): Bitmap? {
        var bitmap: Bitmap? = bitmap
        try {
            var bitmapConfig: Bitmap.Config? = bitmap!!.config
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true)
            val canvas: Canvas = Canvas(bitmap)
            // new antialised Paint
            val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
            // text color - #3D3D3D
            paint.color = Color.rgb(0, 0, 0)
            // text size in pixels
            if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)){
                paint.textSize = (40 * 2).toFloat()
            }else {
                paint.textSize = (22 * 2).toFloat()
            }
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.BLACK)

//            paint.getTypeface(context.getResources().getFont(R.font.sf_pro_text_semibold));
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    paint.typeface = Typeface.create(
                        context!!.resources.getFont(R.font.timesnewromanpsmtregular),
                        Typeface.NORMAL
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // draw text to the Canvas center
            val bounds: Rect = Rect()
            paint.getTextBounds(mText, 0, mText!!.length, bounds)
            val x: Int = (bitmap.width)
            val y: Int = bitmap.height - 70
            if (mText.equals("R", ignoreCase = true)) {
                canvas.drawText((mText), (x - 120).toFloat(),
                    AppUtils.printLabelHeight(screenHeight).toFloat(), paint)
            } else {
                if(BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)) {
                    canvas.drawText(
                        (mText), (x - 380).toFloat(),
                        AppUtils.printLabelHeight(screenHeight).toFloat(), paint
                    )
                }else{
                    canvas.drawText(
                        (mText), (x - 280).toFloat(),
                        AppUtils.printLabelHeight(screenHeight).toFloat(), paint)
                }

            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return bitmap
        }
    }

    open fun setScreenResolution() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
//        width = displayMetrics.widthPixels
//        val topSpace: Double = width * 0.22
    }

    companion object {
        private val bluetoothAddressKey: String = "ZEBRA_DEMO_BLUETOOTH_ADDRESS"
        private val tcpAddressKey: String = "ZEBRA_DEMO_TCP_ADDRESS"
        private val tcpPortKey: String = "ZEBRA_DEMO_TCP_PORT"
        private val PREFS_NAME: String = "OurSavedAddress"
    }


    private fun getPrintSetFFValueFromSettingFile() {
        try {
            var mDb: AppDatabase? = null
            mDb = BaseApplication.instance?.getAppDatabase()
            settingsList = java.util.ArrayList()
            ioScope.async {
                settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
                mainScope.async {
                    if (settingsList != null && settingsList!!.size > 0) {
                        for (i in settingsList!!.indices) {
                            if (settingsList!![i].type.equals("PRINTER_TOP_SETFF", ignoreCase = true)) {
                                topSetFF = settingsList!![i].mValue.toString()
                            } else if (settingsList!![i].type.equals(
                                    "PRINTER_BOTTOM_SETFF", ignoreCase = true)) {
                                bottomSetFF = settingsList!![i].mValue.toString()
                            } else if (settingsList!![i].type.equals("PRINT_CANVAS_HEIGHT", ignoreCase = true)) {
                                canvasHeight = settingsList!![i].mValue.toString()
                            } else if (settingsList!![i].type.equals("PRINTINGBY", ignoreCase = true) &&
                                settingsList!![i].mValue.equals("CMD", ignoreCase = true) ) {
                                printintByCMD = true
                            }else if (settingsList!![i].type.equals("IS_COPY_PRINTING_DATA_TXT_FILE", ignoreCase = true) &&
                                settingsList!![i].mValue.equals("YES", ignoreCase = true) ) {
                                isPrintingCommandDataInTxtFile = true
                            }
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun writePrinterCommand(content:String)
    {
        if(LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
            printerWriterCmd.append(content)
        }
    }

    private fun savePrintCommand(){
        try
        {
            mainScope.async {
                val localFolder =
                    File(
                        Environment.getExternalStorageDirectory().absolutePath,
                        Constants.FILE_NAME
                    )
                if (!localFolder.exists()) {
                    localFolder.mkdirs()
                }
                val fileName1 = "printer_command" + ".txt" //like 2016_01_12.txt
                val file = File(localFolder, fileName1)
                val writer = FileWriter(file, true)
                writer.append(printerWriterCmd).append("\n\n")
                writer.flush()
                writer.close()
                if (!file.exists()) {
                    try {
                        file.createNewFile()
                    } catch (e: IOException) {
                        e.printStackTrace()

                    }
                }
                LogUtil.printLogPrinterQuery("printer CPCL ",printerWriterCmd.toString())
            }

        }
        //Catching any file errors that could occur
        catch(e: FileNotFoundException)
        {
            e.printStackTrace()
        }
        catch(e:NumberFormatException)
        {
            e.printStackTrace()
        }
        catch(e: IOException)
        {
            e.printStackTrace()
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun mBottomAddressText() :String{
        try {
            if(BottomScheduleValue!=null && BottomScheduleValue!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomSchedule + " " + YBottomSchedule + " " + BottomScheduleValue + " \r\n")
            if(BottomAddressValueArray!=null && BottomAddressValueArray!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddress + " " + BottomAddressValueArray[0] + " \r\n")
            if(mBottomAddressSecondLine!=null && mBottomAddressSecondLine!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine1 + " " + mBottomAddressSecondLine + " \r\n")
            if(mBottomAddressThirdLine!=null && mBottomAddressThirdLine!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine2 + " " + mBottomAddressThirdLine + " \r\n")
            if(mBottomAddressFourthLine!=null && mBottomAddressFourthLine!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine3 + " " + mBottomAddressFourthLine + " \r\n")
            if(BottomAddressValueArray!=null && BottomAddressValueArray.size>4 && BottomAddressValueArray[4]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + (YBottomAddressLine3.toInt()+20) + " " + BottomAddressValueArray[4] + " \r\n")
            if(BottomAddressValueArray!=null && BottomAddressValueArray.size>5 && BottomAddressValueArray[5]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + (YBottomAddressLine3.toInt()+40) + " " + BottomAddressValueArray[5] + " \r\n")
            if(BottomAddressValueArray!=null && BottomAddressValueArray.size>6 && BottomAddressValueArray[6]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + (YBottomAddressLine3.toInt()+60) + " " + BottomAddressValueArray[6] + " \r\n")
            if(BottomAddress2ValueArray[0]!=null && BottomAddress2ValueArray[0]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + YBottomAddress2 + " " + BottomAddress2ValueArray[0]+ " \r\n")
            if(BottomAddress2ValueArray!!.size>1&& BottomAddress2ValueArray[1]!=null && BottomAddress2ValueArray[1]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+20) + " " + BottomAddress2ValueArray[1]+ " \r\n")
            if(BottomAddress2ValueArray!!.size>2&& BottomAddress2ValueArray[2]!=null && BottomAddress2ValueArray[2]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+40) + " " + BottomAddress2ValueArray[2]+ " \r\n")
            if(BottomAddress2ValueArray!!.size>3&& BottomAddress2ValueArray[3]!=null && BottomAddress2ValueArray[3]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+60) + " " + BottomAddress2ValueArray[3]+ " \r\n")
            if(BottomAddress2ValueArray!!.size>4&& BottomAddress2ValueArray[4]!=null && BottomAddress2ValueArray[4]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+80) + " " + BottomAddress2ValueArray[4]+ " \r\n")
            if(BottomAddress2ValueArray!!.size>5&& BottomAddress2ValueArray[5]!=null && BottomAddress2ValueArray[5]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+100) + " " + BottomAddress2ValueArray[5]+ " \r\n")


//            //Adding Footer 3 using loop to make it dynamic
//            BottomAddress3ValueArray.forEachIndexed { index, s ->
//                val yIndex = index * 20
//                mBottomAddressQuery.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt() + yIndex) + " " + s + " \r\n")
//                Log.e("==>Query:", mBottomAddressQuery.toString())
//            }




            if(BottomAddress3ValueArray[0]!=null && BottomAddress3ValueArray[0]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt()) + " " + BottomAddress3ValueArray[0] + " \r\n")
            if(BottomAddress3ValueArray!!.size>1&& BottomAddress3ValueArray[1]!=null && BottomAddress3ValueArray[1]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt()+20) + " " + BottomAddress3ValueArray[1] + " \r\n")
            if(BottomAddress3ValueArray!!.size>2&& BottomAddress3ValueArray[2]!=null && BottomAddress3ValueArray[2]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt()+40) + " " + BottomAddress3ValueArray[2] + " \r\n")
            if(BottomAddress3ValueArray!!.size>3&& BottomAddress3ValueArray[3]!=null && BottomAddress3ValueArray[3]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt()+60) + " " + BottomAddress3ValueArray[3] + " \r\n")


//            //Adding Footer 4 using loop to make it dynamic
//            BottomAddress4ValueArray.forEachIndexed { index, s ->
//                val yIndex = index * 20
//                mBottomAddressQuery.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+yIndex) + " " + s + " \r\n")
//                Log.e("==>Query:", mBottomAddressQuery.toString())
//            }


            if(BottomAddress4ValueArray[0]!=null && BottomAddress4ValueArray[0]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + YBottomAddress4 + " " + BottomAddress4ValueArray[0] + " \r\n")
            if(BottomAddress4ValueArray.size>1&& BottomAddress4ValueArray[1]!=null && BottomAddress4ValueArray[1]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+20) + " " + BottomAddress4ValueArray[1] + " \r\n")
            if(BottomAddress4ValueArray.size>2&& BottomAddress4ValueArray[2]!=null && BottomAddress4ValueArray[2]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+40) + " " + BottomAddress4ValueArray[2] + " \r\n")
            if(BottomAddress4ValueArray.size>3&& BottomAddress4ValueArray[3]!=null && BottomAddress4ValueArray[3]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+60) + " " + BottomAddress4ValueArray[3] + " \r\n")
            if(BottomAddress4ValueArray.size>4&& BottomAddress4ValueArray[4]!=null && BottomAddress4ValueArray[4]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+80) + " " + BottomAddress4ValueArray[4] + " \r\n")
            if(BottomAddress4ValueArray.size>5&& BottomAddress4ValueArray[5]!=null && BottomAddress4ValueArray[5]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+100) + " " + BottomAddress4ValueArray[5] + " \r\n")
            if(BottomAddress4ValueArray.size>6&& BottomAddress4ValueArray[6]!=null && BottomAddress4ValueArray[6]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+120) + " " + BottomAddress4ValueArray[6] + " \r\n")
            if(BottomAddress4ValueArray.size>6&& BottomAddress4ValueArray[6]!=null && BottomAddress4ValueArray[6]!!.isNotEmpty())
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+120) + " " + BottomAddress4ValueArray[6] + " \r\n")



            mBottomAddressQuery!!.append("LINE " + 1 + " " + (YBottomAddress4.toInt() + 150) + " 560 " + (YBottomAddress4.toInt() + 150) + " 1 \r\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }



}