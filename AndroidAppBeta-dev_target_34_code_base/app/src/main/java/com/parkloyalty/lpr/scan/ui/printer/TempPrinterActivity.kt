//package com.parkloyalty.lpr.scan.ui.printer
//
//import android.Manifest
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothClass
//import android.bluetooth.BluetoothDevice
//import android.content.Context
//import android.content.SharedPreferences
//import android.content.pm.PackageManager
//import android.graphics.*
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.os.Looper
//import android.provider.MediaStore
//import android.text.method.LinkMovementMethod
//import android.util.DisplayMetrics
//import android.util.Log
//import android.view.Menu
//import android.view.View
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.parkloyalty.lpr.scan.BuildConfig
//import com.parkloyalty.lpr.scan.R
//import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
//import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
//import com.parkloyalty.lpr.scan.database.AppDatabase
//import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
//import com.parkloyalty.lpr.scan.extensions.isEmpty
//import com.parkloyalty.lpr.scan.extensions.nullOrEmptySafety
//import com.parkloyalty.lpr.scan.extensions.nullSafety
//import com.parkloyalty.lpr.scan.interfaces.Constants
//import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.PRINT_LINE_HEIGHT
//import com.parkloyalty.lpr.scan.interfaces.PrintInterface
//import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
//import com.parkloyalty.lpr.scan.util.AppUtils
//import com.parkloyalty.lpr.scan.util.AppUtils.addYAxisToSet
//import com.parkloyalty.lpr.scan.util.AppUtils.getHeightBasedOnFont
//import com.parkloyalty.lpr.scan.util.AppUtils.getYAxisBasedOnQRCodeHeight
//import com.parkloyalty.lpr.scan.util.LogUtil
//import com.zebra.sdk.comm.BluetoothConnection
//import com.zebra.sdk.comm.Connection
//import com.zebra.sdk.comm.ConnectionException
//import com.zebra.sdk.comm.TcpConnection
//import com.zebra.sdk.device.ZebraIllegalArgumentException
//import com.zebra.sdk.graphics.internal.ZebraImageAndroid
//import com.zebra.sdk.printer.PrinterLanguage
//import com.zebra.sdk.printer.ZebraPrinter
//import com.zebra.sdk.printer.ZebraPrinterFactory
//import kotlinx.coroutines.async
//import java.io.File
//import java.io.FileNotFoundException
//import java.io.FileWriter
//import java.io.IOException
//
//
//open class TempPrinterActivity : BaseActivity() {
//    //    private val connection: Connection? = null
////    private var imageSelectionSpinner: Spinner? = null
////    private var cb: CheckBox? = null
////    private var angleSpinner: Spinner? = null
////    private val myDialog: ProgressDialog? = null
////    private var btRadioButton: RadioButton? = null
//    private var macAddressEditText: EditText? = null
//    private var ipAddressEditText: EditText? = null
//    private var portNumberEditText: EditText? = null
//    //    private var printStoragePath: EditText? = null
//    private val helper: UIHelper = UIHelper(this)
//    private var zebraFooterLogo: ImageView? = null
//    private var rotationAngle: Int = 180
//    private var printCallBack: PrintInterface? = null
//    private var context: Context? = null
//    private var mFrom: String? = null
//    private var mLabel: String? = null
//    private var mTicketNumber: String? = null
//    private var mPrinterCommand: java.lang.StringBuilder? = null
//    private var mAmount: String? = null
//    private var mState: String? = null
//    private var mLprNumber: String? = null
//    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 2111
//    private var settingsList: List<DatasetResponse>? = null
//    private var byteArray : ByteArray?= null
////    private var mDb: AppDatabase? = null
////    private var isBarCodePrintlayout: Boolean = true
////    private var isQRCodePrintlayout: Boolean = false
////    private var mQRCodeValue: String? = null
////    private var mIssuranceModel: CitationInsurranceDatabaseModel? =
////            CitationInsurranceDatabaseModel()
//
//    //    private var mVehicleListPrint = ArrayList<VehicleListModel>()
//    private var isErrorUploading: String = ""
//
//    private var screenHeight = 0
//    private var screenWidth = 0
//    private var isTextPrintingBySite = false
//
//
//    private var topSetFF = "150 4"
//    private var bottomSetFF = "1 1"
//    private var canvasHeight = "10"
//    private var printintByCMD = false
//    private var isPrintingCommandDataInTxtFile = false
//    private var printerConnection: Connection? = null
//
//    var printerWriterCmd =java.lang.StringBuilder();
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.imageprintlayout)
//        context = this
////        mDb = BaseApplication.instance?.getAppDatabase()
//        val settings: SharedPreferences = getSharedPreferences(PREFS_NAME, 0)
//        zebraFooterLogo = findViewById<View>(R.id.zebraFooterLogo) as ImageView?
//        ipAddressEditText = findViewById<View>(R.id.ipAddressInput) as EditText?
//        val ip: String? = settings.getString(tcpAddressKey, "")
//        ipAddressEditText!!.setText(ip)
//        portNumberEditText = findViewById<View>(R.id.portInput) as EditText?
//        val port: String? = settings.getString(tcpPortKey, "")
//        portNumberEditText!!.setText(port)
//        macAddressEditText = findViewById<View>(R.id.macInput) as EditText?
////        val mac: String? = settings.getString(bluetoothAddressKey, "")
//        val mac: String? = SettingsHelper.getBluetoothAddressKey(this@TempPrinterActivity)
//        macAddressEditText!!.setText(mac)
//        val t2: TextView = findViewById<View>(R.id.launchpad_link) as TextView
//        t2.movementMethod = LinkMovementMethod.getInstance()
//
//
//        if (ContextCompat.checkSelfPermission(
//                this@TempPrinterActivity,
//                Manifest.permission.BLUETOOTH_CONNECT
//            ) == PackageManager.PERMISSION_DENIED
//            || ContextCompat.checkSelfPermission(
//                this@TempPrinterActivity,
//                Manifest.permission.BLUETOOTH_SCAN
//            ) == PackageManager.PERMISSION_DENIED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                ActivityCompat.requestPermissions(
//                    this@TempPrinterActivity, arrayOf(
//                        Manifest.permission.BLUETOOTH_SCAN,
//                        Manifest.permission.BLUETOOTH_CONNECT
//                    ), BLUETOOTH_PERMISSION_REQUEST_CODE
//                )
//            }
//        }
//        if (macAddressEditText!!.text!!.isEmpty()) {
//            getPairedPrintersTo()
//        }
//        setScreenResolution()
//        try {
//            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
//                    ignoreCase = true
//                ) ||BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
//                    ignoreCase = true
//                ) ||BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_DANVILLE_VA,
//                    ignoreCase = true
//                ) ||BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_CAMDEN,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
//                    ignoreCase = true
//                ) ||BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_GREENBURGH_NY,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_MEMORIALHERMAN,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
//                    ignoreCase = true
//                ) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
//            ) {
//                getPrintSetFFValueFromSettingFile()
//            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        if (printerConnection != null && printerConnection!!.isConnected) {
//            printerConnection!!.close()
//        }
//    }
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
//                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(
//                        this@TempPrinterActivity,
//                        getString(R.string.error_bluetooth_permission_is_not_granted),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    if(macAddressEditText!!.text!!.isEmpty()) {
//                        getPairedPrintersTo()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun SetContextPrinterActivity() {
//        //initializing the callback object from the constructor
//        printCallBack = context as PrintInterface?
//    }
//
//    //        LogUtil.printToastMSG(this,"Get Mac Address by auto"+ mac);
//    private fun getPairedPrintersTo(): String {
//        try {
//            var mac: String = ""
//            val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//            val pairedDevices: Set<BluetoothDevice> = mBluetoothAdapter!!.bondedDevices
//            val pairedDevicesList: ArrayList<BluetoothDevice>? = ArrayList()
//            for (device: BluetoothDevice in pairedDevices) {
//                if (isBluetoothPrinter(device)) pairedDevicesList!!.add(device)
//            }
//            if (pairedDevicesList != null && pairedDevicesList.size > 0) {
//                mac = pairedDevicesList.get(0).address.toString()
//            }
//            macAddressEditText!!.setText(mac)
//            //        LogUtil.printToastMSG(this,"Get Mac Address by auto"+ mac);
//            return mac
//        } catch (e: Exception) {
//           e.printStackTrace()
//            return ""
//        }
//    }
//
//    private fun isBluetoothPrinter(bluetoothDevice: BluetoothDevice): Boolean {
//        return (bluetoothDevice.bluetoothClass
//            .majorDeviceClass == BluetoothClass.Device.Major.IMAGING
//                || bluetoothDevice.bluetoothClass
//            .majorDeviceClass == BluetoothClass.Device.Major.UNCATEGORIZED)
//    }
//
//    //    var byteA = null
//    fun printDailySummery(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
//                          mTicketNumber: String?) {
//        this.context = context
//        this.mFrom = mFrom
//        this.mLabel = mLabel
//        this.mTicketNumber = mTicketNumber
//        isTextPrintingBySite = true
//
//        SetContextPrinterActivity()
//        val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
//        var myBitmap: Bitmap? = null
//        try {
//            myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
//        } catch (e: FileNotFoundException) {
//            e.message
//        } catch (e: IOException) {
//            e.message
//        }
//        printRotatedPhotoFromExternal(myBitmap, rotationAngle,true)
//    }
//
//    /**
//     * Preview Activity
//     */
//    fun mPrintFacsimileImage(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
//                             mTicketNumber: String?,mAmount: String?,mState: String?,mLprNumber: String?
//                             ,mErrorUploading: String?,printerCommand : StringBuilder) {
//        this.context = context
//        this.mFrom = mFrom
//        this.mLabel = mLabel
//        this.mTicketNumber = mTicketNumber
//        this.mPrinterCommand = printerCommand
//        this.mAmount = mAmount
//        this.mState = mState
//        this.mLprNumber = mLprNumber
//        this.isErrorUploading = mErrorUploading!!
//        SetContextPrinterActivity()
//        if(AppUtils.printByCMD(printintByCMD))
//        {
//            isTextPrintingBySite = false
//            val filePath = sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP_OCR, "")
//            val savePrintImagePath = File(filePath)
//
////            var savePrintImagePath: File? = null
//            val imgPath: Uri = Uri.fromFile((savePrintImagePath.absoluteFile))
//            var myBitmap: Bitmap? = null
//            try {
//                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
//
//            } catch (e: FileNotFoundException) {
//                e.message
//            } catch (e: IOException) {
//                e.message
//            }
//            printRotatedPhotoFromExternal(myBitmap, rotationAngle,false)
//        }else{
//            val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
//            var myBitmap: Bitmap? = null
//            try {
//                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
//            } catch (e: FileNotFoundException) {
//                e.message
//            } catch (e: IOException) {
//                e.message
//            }
//            printRotatedPhotoFromExternal(myBitmap, rotationAngle,false)
//        }
//    }
//    /**
//     * Preview Activity
//     */
//    fun mPrintFromCitationHistory(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
//                                  mTicketNumber: String?,mAmount: String?,mState: String?,mLprNumber: String?
//                                  ,mErrorUploading: String?,printerCommand : StringBuilder) {
//        this.context = context
//        this.mFrom = mFrom
//        this.mLabel = mLabel
//        this.mTicketNumber = mTicketNumber
//        this.mPrinterCommand = printerCommand
//        this.mAmount = mAmount
//        this.mState = mState
//        this.mLprNumber = mLprNumber
//        this.isErrorUploading = mErrorUploading!!
//        SetContextPrinterActivity()
//        if(AppUtils.printByCMD(printintByCMD))
//        {
//            isTextPrintingBySite = false
//            val filePath = sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP_OCR, "")
//            val savePrintImagePath = File(filePath)
//            var myBitmap: Bitmap? = null
//
//            if (savePrintImagePath.exists()) {
//                try {
//                    val imgPath: Uri = Uri.fromFile((savePrintImagePath.absoluteFile))
//                    myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            } else {
//                try {
//                    val drawableBitmap = BitmapFactory.decodeResource(resources, R.drawable.white_print)
//                    myBitmap = drawableBitmap.copy(Bitmap.Config.ARGB_8888, true)
//                    Canvas(myBitmap)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
////            var savePrintImagePath: File? = null
////            val imgPath: Uri = Uri.fromFile((savePrintImagePath.absoluteFile))
////            var myBitmap: Bitmap? = null
////            try {
////                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
////
////                if(myBitmap!=null){
////                    val drawableBitmap = BitmapFactory.decodeResource(resources, R.drawable.white_print)
////                    myBitmap = drawableBitmap.copy(Bitmap.Config.ARGB_8888, true)
////                    Canvas(myBitmap)
////                }
////
////            } catch (e: FileNotFoundException) {
////                e.message
////            } catch (e: IOException) {
////                e.message
////            }
//            printRotatedPhotoFromExternal(myBitmap, rotationAngle,false)
//        }else{
//            val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
//            var myBitmap: Bitmap? = null
//            try {
//                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
//            } catch (e: FileNotFoundException) {
//                e.message
//            } catch (e: IOException) {
//                e.message
//            }
//            printRotatedPhotoFromExternal(myBitmap, rotationAngle,false)
//        }
//    }
//
//    fun mPrintDownloadFacsimileImage(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
//                                     mTicketNumber: String?,mAmount: String?,mState: String?,mLprNumber: String?
//                                     ,mErrorUploading:String?) {
//        this.context = context
//        this.mFrom = mFrom
//        this.mLabel = mLabel
//        this.mTicketNumber = mTicketNumber
//        this.mAmount = mAmount
//        this.mState = mState
//        this.mLprNumber = mLprNumber
//        this.isErrorUploading = mErrorUploading!!
//        isTextPrintingBySite = true
//        SetContextPrinterActivity()
//        if (AppUtils.printByCMD(printintByCMD))
//        {
//            isTextPrintingBySite = false
//            val filePath = sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP_OCR, "")
//            val savePrintImagePath = File(filePath)
//
//            val imgPath: Uri = Uri.fromFile((savePrintImagePath.absoluteFile))
//            var myBitmap: Bitmap? = null
//            try {
//                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
//            } catch (e: FileNotFoundException) {
//                e.message
//            } catch (e: IOException) {
//                e.message
//            }
//            printRotatedPhotoFromExternal(myBitmap, rotationAngle,false)
//
//        }else{
//            val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
//            var myBitmap: Bitmap? = null
//            try {
//                myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
//            } catch (e: FileNotFoundException) {
//                e.message
//            } catch (e: IOException) {
//                e.message
//            }
//            printRotatedPhotoFromExternal(myBitmap, rotationAngle,false)
//        }
//
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true
//    }
//
//
//    /**
//     * This method calls the rotated image and send it to the final method.
//     *
//     * @param bitmap
//     * @param rotationAngle
//     */
//    private fun printRotatedPhotoFromExternal(bitmap: Bitmap?, rotationAngle: Int, isDailySUmmaryPrint : Boolean) {
//        var b: Bitmap? = bitmap
//        if(!mLabel!!.isEmpty()) {
//            b = drawTextToBitmap(bitmap, mLabel)
//        }
//        if(isDailySUmmaryPrint)
//        {
//            printOfficerDailySummery(b)
//        }else {
//            if(AppUtils.isSiteSupportCommandPrinting(printintByCMD)){
//                printCommandPrinting(b)
//            }else{
//                printBitmap(b)
//            }
//        }
//    }
//
//
//    private fun printBitmap(bitmap: Bitmap?) {
//        Thread(object : Runnable {
//            override fun run() {
//                try {
//                    Looper.prepare()
//                    if(LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
//                        printerWriterCmd!!.clear()
//                        printerWriterCmd!!.append("-----------------------------\r\n")
//                    }
//                    getAndSaveSettings()
//                    helper.showLoadingDialog("Sending image to printer")
//                    try {
//                        if(printerConnection==null) {
//                            printerConnection = getZebraPrinterConn()
//                        }
//                    }catch ( e: Exception)
//                    {
//                        e.printStackTrace()
//                    }
//                    val printQty: Int = 1 //or whatever number you want
//                    printerConnection!!.open()
//                    if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 5 4\r\n".toByteArray()) // change1
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 5 4\r\n")
//                    } else if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PARK, ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 35 4\r\n".toByteArray()) // change1
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 35 4\r\n")
//                    }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 10 8\r\n".toByteArray()) // change1
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 10 8\r\n")
//                    } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 5 2\r\n".toByteArray()) // change1
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 5 2\r\n")
////                        printerConnection!!.write(" ! U1 PH 1456".toByteArray())
//                    } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH,ignoreCase = true) ||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true) ||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true))
//                    {
//                        printerConnection!!.write(("! U1 FORM\r\n! U1 SETFF "+topSetFF+"\r\n").toByteArray())
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF "+topSetFF+"\r\n")
//                    }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)){
////                        printerConnection!!.write(("! U1 FORM\r\n! U1 SETFF "+ "0 0"+" \r\n").toByteArray())
//                    }
//                    else {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 10 2\r\n".toByteArray())
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 10 2\r\n")
//                    }
//
//                    val printer: ZebraPrinter =
//                        ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, printerConnection!!)
//                    val ZEBRA_WIDTH: Float = 576f
//                    //                    float ZEBRA_WIDTH = 576;
//                    val width: Int = bitmap!!.width
//                    val height: Int = bitmap.height
//                    val aspectRatio: Float = width / ZEBRA_WIDTH //ZEBRA_RW420_WIDTH = 800f
//                    val multiplier: Float = 1 / aspectRatio
//                    //scale the bitmap to fit the ZEBRA_RW_420_WIDTH print
//                    val bitmapToPrint1: Bitmap = Bitmap.createScaledBitmap(
//                        (bitmap),
//                        (width * multiplier).toInt(),
//                        (height * multiplier).toInt(),
//                        false
//                    )
//                    bitmap.recycle()
//                    var newBitmapHeight: Int = bitmapToPrint1.height + 5
//
//                    if (LogUtil.isPrintHeightDynamicFromTicket) {
//                        try {
//                            if (canvasHeight.toInt() > newBitmapHeight) {
//                                newBitmapHeight = canvasHeight.toInt()
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    } else {
//                        try {
//                            if (canvasHeight.toInt() > 100) {
//                                newBitmapHeight = canvasHeight.toInt()
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//
//
//                    //create the Zebra object with the new Bitmap
//                    val zebraImageToPrint: ZebraImageAndroid = ZebraImageAndroid(bitmapToPrint1)
//                    //the image is sent to the printer and stored in R: folder
//                    printer.storeImage("R:TEMP.PCX", zebraImageToPrint, -1, -1)
//                    /**
//                     * {command} {type} {width} {ratio} {height} {x} {y} {data} = barcode
//                     * //create the print commands string
//                     * {command} {font} {size} {x} {y} {data} = text
//                     *
//                     * QRCODE
//                     * {command} {type} {x} {y} [M n] [U n]
//                     * {data}
//                     * <ENDQR
//                     *
//                     *
//                     * SETFF {command} {max-feed} {skip-length}
//                     * {command}: SETFF
//                     * {max-feed}: Maximum unit-length the printer advances searching for the next eye-sense mark to align top of form.
//                     * Valid values are 0-20,000.
//                     * {skip-length}: Unit-length printer advances past top of form. Valid values are 5-50.
//                     * **/
//
//                    if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(
//                            DuncanBrandingApp13()
//                        )&& !BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)) {
//                        if(mFrom.equals("OfficerDailySummary")){
//                            val printString: String =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        +"SETSP 1\r\n"
////                                        + "TEXT90 2 13 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
////                                        + "BARCODE 128 1 1 60 186 1100 " + mTicketNumber + "\r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                            printerConnection!!.write(printString.toByteArray())
//                            writePrinterCommand(printString)
//                        }else{
//                            val printString: String =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
////                                        +"SETMAG 0 0\r\n"
//                                        +"SETSP 1\r\n"
////                                        + "TEXT90 2 10 534 594 "+PHILA+   "  \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                        + "TEXT90 2 13 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
////                                        + "BARCODE 128 1 1 52 186 1110 " + mTicketNumber + "\r\n"
//                                        + "BARCODE 128 1 1 60 86 1110 " + mTicketNumber + "\r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                            printerConnection!!.write(printString.toByteArray())
//                            writePrinterCommand(printString)
//                        }
//
//                    }else if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)) {
//                        if(mFrom.equals("OfficerDailySummary")){
//                            val printString: String =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        +"SETSP 1\r\n"
////                                        + "TEXT90 2 13 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
////                                        + "BARCODE 128 1 1 60 186 1100 " + mTicketNumber + "\r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                            printerConnection!!.write(printString.toByteArray())
//                            writePrinterCommand(printString)
//                        }else{
//                            val printString: String =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
////                                        +"SETMAG 0 0\r\n"
//                                        +"SETSP 1\r\n"
////                                        + "TEXT90 2 13 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                        + "BARCODE 128 1 1 60 186 60 " + mTicketNumber + "\r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                            printerConnection!!.write(printString.toByteArray())
//                            writePrinterCommand(printString)
//                        }
//
//                    } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH,ignoreCase = true)){
//                        val printString: String =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "NO-PACE" + "\r\n"
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }else if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)) {
//                        val printString: String =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "NO-PACE" + "\r\n"
////                                        +"SETMAG 0 0\r\n"
//                                    +"SETSP 1\r\n"
////                                        + "TEXT90 2 10 534 594 "+PHILA+   "  \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                    + "TEXT90 2 11 543 444 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
////                                        + "BARCODE 128 1 1 52 186 1110 " + mTicketNumber + "\r\n"
////                                        + "BARCODE 128 1 1 60 186 1100 " + mTicketNumber + "\r\n"
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }else if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)&& isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PRRS, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)&& isTextPrintingBySite||
//
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_COB, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_Easton, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true)&& isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true)&& isTextPrintingBySite) {
//                        val printString: String =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "NO-PACE" + "\r\n"
//                                    + "SETSP 1\r\n"
////                                    + AppUtils.sectionFirst.toString()
////                                    + "TEXT 7 1 5.0 1160.0 Scan to pay \r\n"
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    } else if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)){
//
//                        val printString: String =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
////                                        + "LABEL" + "\r\n"
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "AUTO-PACE" + "\r\n" // This command can be used to instruct a printer equipped with a label presentation sensor to delay
////                                                                 printing until the previously printed label is removed.
////                                        + printer.printImage()
////                                        + "B QR 186 5 M 2 U 5" +"\r\n"
////                                        + "MA,QR code" + mTicketNumber + "\r\n"
////                                        + "ENDQR" + "\r\n"
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        //send the commands to the printer, the image will be printed now
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)){
//
//                        val printString: String =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
////                                    + "AUTO-PACE" + "\r\n"
////                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        //send the commands to the printer, the image will be printed now
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }else {
//
//                        val printString: String =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "AUTO-PACE" + "\r\n"
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        //send the commands to the printer, the image will be printed now
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }
//
//
////delete the image at the end to prevent printer memory sutaration
//                    printerConnection!!.write(("! U1 do \"file.delete\" \"R:TEMP.PCX\"\r\n").toByteArray())
//                    if( BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 20000 100\r\n".toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 20000 100\r\n")
//                    }else if( BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP,ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 20 8\r\n".toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 20 8\r\n")
//                    }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true) ||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH,ignoreCase = true)) {
//                        printerConnection!!.write(("! U1 FORM\n! U1 SETFF "+bottomSetFF+ "\r\n").toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\n! U1 SETFF "+bottomSetFF+ "\r\n")
//                    } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true) ||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true) ||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)) {
//                        printerConnection!!.write(("! U1 FORM\r\n! U1 SETFF "+bottomSetFF+ "\r\n").toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF "+bottomSetFF+ "\r\n")
//                    }
//                    else {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 20000 8\r\n".toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 20000 8\r\n")
//                    }
////
////close the printerConnection!! with the printer
//                    printerConnection!!.close()
//                    //recycle the bitmap
//                    bitmapToPrint1.recycle()
//                } catch (e: ConnectionException) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } catch (e: ZebraIllegalArgumentException) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } finally {
//                    if(LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
//                        savePrintCommand()
//                    }
//                    printCallBack!!.onActionSuccess(isErrorUploading)
////                    helper.dismissLoadingDialog()
//                    Looper.loop()
//                    Looper.myLooper()!!.quit()
//                }
//            }
//        }).start()
//    }
//
//    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
//
//    private fun printCommandPrinting(bitmap: Bitmap?) {
//        LogUtil.printLog("==>Printer:","Height:${bitmap?.height.nullSafety()}")
//        LogUtil.printLog("==>Printer:","HeightMaxY:${AppUtils.getMaxYAxisFromCommand()}")
//
//
//        Thread(object : Runnable {
//            override fun run() {
//                try {
//                    Looper.prepare()
//                    if(LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
//                        printerWriterCmd!!.clear()
//                        printerWriterCmd!!.append("-----------------------------\r\n")
//                    }
//                    getAndSaveSettings()
//                    helper.showLoadingDialog("Sending image to printer")
//
//                    LogUtil.printLogHeader("QUERY",AppUtils.printQueryStringBuilder.toString())
//
//                    try {
//                        if(printerConnection==null) {
//                            printerConnection = getZebraPrinterConn()
//                        }
//                    }catch ( e: Exception)
//                    {
//                        e.printStackTrace()
//                    }
//                    val printQty: Int = 1 //or whatever number you want
//                    printerConnection!!.open()
//                    if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 5 4\r\n".toByteArray()) // change1
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 5 4\r\n")
//                    } else if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PARK, ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 35 4\r\n".toByteArray()) // change1
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 35 4\r\n")
//                    }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL,ignoreCase = true)
//                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 10 8\r\n".toByteArray()) // change1
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 10 8\r\n")
//                    }   else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 5 2\r\n".toByteArray()) // change1
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 5 2\r\n")
////                        printerConnection!!.write(" ! U1 PH 1456".toByteArray())
//                    } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)
//                        ){
//                        printerConnection!!.write(("! U1 FORM\r\n! U1 SETFF "+topSetFF+"\r\n").toByteArray())
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF "+topSetFF+"\r\n")
//                    }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)){
////                        printerConnection!!.write(("! U1 FORM\r\n! U1 SETFF "+ "0 0"+" \r\n").toByteArray())
//                    }
//                    else {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 10 2\r\n".toByteArray())
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 10 2\r\n")
//                    }
//
//                    val printer: ZebraPrinter =
//                        ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, printerConnection!!)
//                    val ZEBRA_WIDTH: Float = 576f
//                    //                    float ZEBRA_WIDTH = 576;
//                    val width: Int = bitmap!!.width
//                    val height: Int = bitmap.height
//                    val aspectRatio: Float = width / ZEBRA_WIDTH //ZEBRA_RW420_WIDTH = 800f
//                    val multiplier: Float = 1 / aspectRatio
//                    //scale the bitmap to fit the ZEBRA_RW_420_WIDTH print
//                    val bitmapToPrint1: Bitmap = Bitmap.createScaledBitmap(
//                        (bitmap),
//                        (width * multiplier).toInt(),
//                        (height * multiplier).toInt(),
//                        false
//                    )
//
//                    var newBitmapHeight: Int = bitmapToPrint1.height + 5
//
//                    if (LogUtil.isPrintHeightDynamicFromTicket && AppUtils.isSiteSupportCommandPrinting(printintByCMD)) {
//                        newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//
//                        if (canvasHeight.toInt() > newBitmapHeight) {
//                            newBitmapHeight = canvasHeight.toInt()
//                        }
//                    }else{
//                        try {
//                            if(canvasHeight.toInt() > 100){
//                                newBitmapHeight = canvasHeight.toInt()
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)){
//                        newBitmapHeight = newBitmapHeight + 200
//                    }
//
//
//                    //create the Zebra object with the new Bitmap
//                    val zebraImageToPrint: ZebraImageAndroid = ZebraImageAndroid(bitmapToPrint1)
//                    //the image is sent to the printer and stored in R: folder
//                    printer.storeImage("R:TEMP.PCX", zebraImageToPrint, -1, -1)
//
//                    /**
//                     * {command} {type} {width} {ratio} {height} {x} {y} {data} = barcode
//                     * //create the print commands string
//                     * {command} {font} {size} {x} {y} {data} = text
//                     *
//                     * QRCODE
//                     * {command} {type} {x} {y} [M n] [U n]
//                     * {data}
//                     * <ENDQR
//                     *
//                     *
//                     * SETFF {command} {max-feed} {skip-length}
//                     * {command}: SETFF
//                     * {max-feed}: Maximum unit-length the printer advances searching for the next eye-sense mark to align top of form.
//                     * Valid values are 0-20,000.
//                     * {skip-length}: Unit-length printer advances past top of form. Valid values are 5-50.
//                     * **/
//
//                    if(BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)){
//                        if(printintByCMD)
//                        {
//                            var printString: String = ""
//                            if(!isTextPrintingBySite)
//                            {
//                                if(AppUtils.mFinalQRCodeValue!!.isEmpty()){
//
//
//                                    printString =
//                                        ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                                + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                                + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                                + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                                + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                                + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                                + "NO-PACE" + "\r\n"
//                                                + "SETSP 1\r\n"
//                                                + AppUtils.printQueryStringBuilder.toString()
//                                             + " \r\n"
//                                                 + "BAR-SENSE" + "\r\n"
//                                                + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                                + "FORM" + "\r\n"
//                                                + "PRINT" + "\r\n") //print
//                                }else {
//                                    printString =
//                                        ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                                + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                                + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                                + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                                + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                                + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                                + "NO-PACE" + "\r\n"
//                                                + "SETSP 1\r\n"
//                                                + AppUtils.printQueryStringBuilder.toString()
//                                            + " \r\n"
//                                                + "BAR-SENSE" + "\r\n"
//                                                + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                                + "FORM" + "\r\n"
//                                                + "PRINT" + "\r\n") //print
//                                }
//                                LogUtil.printLogPrinterQuery("QUERY",printString.toString().trimIndent())
//                                printerConnection!!.write(printString.toByteArray())
//                                writePrinterCommand(printString)
//                            }else{
//                                val printString: String =
//                                    ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                            + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                            + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                            + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                            + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                            + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                            + "NO-PACE" + "\r\n"
//                                            + "SETSP 1\r\n"
//                                            + "BAR-SENSE" + "\r\n"
//                                            + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                            + "FORM" + "\r\n"
//                                            + "PRINT" + "\r\n") //print
//                                printerConnection!!.write(printString.toByteArray())
//                                writePrinterCommand(printString)
//                            }
//                        }else{
//                            val printString: String =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                            printerConnection!!.write(printString.toByteArray())
//                            writePrinterCommand(printString)
//                        }
//
//                    }else if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||
//                            BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) && !isTextPrintingBySite||
//                            BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) && !isTextPrintingBySite) {
//
//
//
//                        val printString: String =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "NO-PACE" + "\r\n"
//                                    + "SETSP 1\r\n"
//                                    + AppUtils.printQueryStringBuilder.toString().trimIndent()+" \r\n"
//
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        LogUtil.printLogPrinterQuery("QUERY",printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }else if (
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) && !isTextPrintingBySite ||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)&& !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) && !isTextPrintingBySite ||
//
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) && !isTextPrintingBySite ||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) && !isTextPrintingBySite ||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_COB, ignoreCase = true) && !isTextPrintingBySite ||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_Easton, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true) && !isTextPrintingBySite
//                        ) {
//                        var printString: String = ""
//                        if(AppUtils.mFinalQRCodeValue!!.isEmpty()){
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }else {
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }
//                        LogUtil.printLogPrinterQuery("QUERY",printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }
//                    else if (
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true) && !isTextPrintingBySite
//                    ) {
//                        var printString: String = ""
//                        if(AppUtils.mFinalQRCodeValue.isEmpty()){
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }else {
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }
//                        LogUtil.printLogPrinterQuery("QUERY",printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }
//                    else if (
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PRRS, ignoreCase = true
//                        ) && !isTextPrintingBySite ||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true
//                        ) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true
//                        ) && !isTextPrintingBySite
//                    ) {
//                        var printString: String = ""
//                        if (AppUtils.mFinalQRCodeValue!!.isEmpty()) {
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString()
//                                    .trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        //+ "PCX 5 0 !<TEMP_LPR.PCX" + "\r\n"
////                                        + "PCX 5 500 !<TEMP_LPR.PCX" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        } else {
//                            //Adding dynamic Y Axis & Print Height
//                            if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                 newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                            }
//                            printString =
//                                ("! 0 200 200 " + (newBitmapHeight + 140) + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString()
//                                    .trimIndent() + " \r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }
//                        LogUtil.printLogPrinterQuery("QUERY", printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }
//                    else if ((BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true
//                        ) && !isTextPrintingBySite) || (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true
//                        ) && !isTextPrintingBySite)||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true
//                        ) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true
//                        ) && !isTextPrintingBySite
//                    ) {
//                        var printString: String = ""
//                        if (AppUtils.mFinalQRCodeValue.isEmpty()) {
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString()
//                                    .trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        } else {
//                            //Adding dynamic Y Axis & Print Height
//
//                            printString =
//                                ("! 0 200 200 " + (newBitmapHeight+140) + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString()
//                                    .trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }
//                        LogUtil.printLogPrinterQuery("QUERY", printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }
//                    else if(BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) && !isTextPrintingBySite) {
//
//                        var printString: String = ""
//                        if(AppUtils.mFinalQRCodeValue!!.isEmpty()){
//
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                         + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }else {
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }
//                        LogUtil.printLogPrinterQuery("QUERY",printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }
//                    else if(BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) && !isTextPrintingBySite) {
//                        var printString: String = ""
//                        if(AppUtils.mFinalQRCodeValue!!.isEmpty()){
//
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
////                                        + "TEXT90 2 13 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }else {
//
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                          + "TEXT90 2 13 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }
//                        LogUtil.printLogPrinterQuery("QUERY",printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }else if (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) && !isTextPrintingBySite) {
//
//                        val printString: String =
//                            ("! 0 200 200 " + canvasHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "NO-PACE" + "\r\n"
//                                    + "SETSP 1\r\n"
//                                    + AppUtils.printQueryStringBuilder.toString().trimIndent()+" \r\n"
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        LogUtil.printLogPrinterQuery("QUERY",printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }else if((
//                                BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) && !isTextPrintingBySite)
//                        ||  (
//                                BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) && !isTextPrintingBySite)
//                        ||    (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) && !isTextPrintingBySite)
//                        ||    (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) && !isTextPrintingBySite)
//                        || (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) && !isTextPrintingBySite)
//                        || (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) && !isTextPrintingBySite)
//                        || (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) && !isTextPrintingBySite)){
//
//
//                        val printString =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "NO-PACE" + "\r\n"
//                                    + "SETSP 1\r\n"
//                                    + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
////                                    + "BARCODE 128 1 1 60 "+XQRCode+" "+YQRCode+ " mTicketNumber"+ " \r\n"
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//
//                        LogUtil.printLogPrinterQuery("QUERY",printString.toString().trimIndent())
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }
//                    else {
//
//                        val printString: String =
//                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                    + "AUTO-PACE" + "\r\n"
//                                    + "BAR-SENSE" + "\r\n"
//                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                    + "FORM" + "\r\n"
//                                    + "PRINT" + "\r\n") //print
//                        //send the commands to the printer, the image will be printed now
//                        printerConnection!!.write(printString.toByteArray())
//                        writePrinterCommand(printString)
//                    }
//
//
////delete the image at the end to prevent printer memory sutaration
//                    printerConnection!!.write(("! U1 do \"file.delete\" \"R:TEMP.PCX\"\r\n").toByteArray())
//
//                    if(
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)
//                        ){
//                        printerConnection!!.write(("! U1 do \"file.delete\" \"R:TEMP_LPR_ONE.PCX\"\r\n").toByteArray())
//                        printerConnection!!.write(("! U1 do \"file.delete\" \"R:TEMP_LPR_TWO.PCX\"\r\n").toByteArray())
//                        printerConnection!!.write(("! U1 do \"file.delete\" \"R:TEMP_LPR_THREE.PCX\"\r\n").toByteArray())
//                    }
//
//                    if( BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 20000 100\r\n".toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 20000 100\r\n")
//                    }else if( BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP,ignoreCase = true)||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)) {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 20 8\r\n".toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 20 8\r\n")
//                    }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)) {
//                        printerConnection!!.write(("! U1 FORM\n! U1 SETFF "+bottomSetFF+ "\r\n").toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\n! U1 SETFF "+bottomSetFF+ "\r\n")
//                    } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true) ||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)
//                        ) {
//                        printerConnection!!.write(("! U1 FORM\r\n! U1 SETFF "+bottomSetFF+ "\r\n").toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF "+bottomSetFF+ "\r\n")
//                    }
//                    else {
//                        printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 20000 8\r\n".toByteArray()) // for eye sense mark
//                        writePrinterCommand("! U1 FORM\r\n! U1 SETFF 20000 8\r\n")
//                    }
////
////close the printerConnection!! with the printer
//                    printerConnection!!.close()
//                    //recycle the bitmap
//                    bitmap.recycle()
//                    bitmapToPrint1.recycle()
//                } catch (e: ConnectionException) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } catch (e: ZebraIllegalArgumentException) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } finally {
//                    if(LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
//                        savePrintCommand()
//                    }
//                    printCallBack!!.onActionSuccess(isErrorUploading)
////                    helper.dismissLoadingDialog()
//                    Looper.loop()
//                    Looper.myLooper()!!.quit()
//                }
//            }
//        }).start()
//    }
//
//    private fun printOfficerDailySummery(bitmap: Bitmap?) {
//        Thread(object : Runnable {
//            override fun run() {
//                try {
//                    Looper.prepare()
//                    getAndSaveSettings()
//                    helper.showLoadingDialog("Sending image to printer")
//                    try {
//                        if (printerConnection == null) {
//                            printerConnection = getZebraPrinterConn()
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    val printQty: Int = 1 //or whatever number you want
//                    printerConnection!!.open()
//                    printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 10 2\r\n".toByteArray())
//
//                    val printer: ZebraPrinter =
//                        ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, printerConnection!!)
//                    val ZEBRA_WIDTH: Float = 576f
//                    //                    float ZEBRA_WIDTH = 576;
//                    val width: Int = bitmap!!.width
//                    val height: Int = bitmap.height
//                    val aspectRatio: Float = width / ZEBRA_WIDTH //ZEBRA_RW420_WIDTH = 800f
//                    val multiplier: Float = 1 / aspectRatio
//                    //scale the bitmap to fit the ZEBRA_RW_420_WIDTH print
//                    val bitmapToPrint1: Bitmap = Bitmap.createScaledBitmap(
//                        (bitmap),
//                        (width * multiplier).toInt(),
//                        (height * multiplier).toInt(),
//                        false
//                    )
//                    bitmap.recycle()
//                    var newBitmapHeight: Int = bitmapToPrint1.height + 5
//
//                    //create the Zebra object with the new Bitmap
//                    val zebraImageToPrint: ZebraImageAndroid = ZebraImageAndroid(bitmapToPrint1)
//                    //the image is sent to the printer and stored in R: folder
//                    printer.storeImage("R:TEMP.PCX", zebraImageToPrint, -1, -1)
//
//                    val printString: String =
//                        ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                + "NO-PACE" + "\r\n"
//                                + "SETSP 1\r\n"
////                                    + AppUtils.sectionFirst.toString().trimIndent()
////                                    + "TEXT 7 1 5.0 1160.0 Scan to pay \r\n"
//                                + "BAR-SENSE" + "\r\n"
//                                + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                + "FORM" + "\r\n"
//                                + "PRINT" + "\r\n") //print
//                    printerConnection!!.write(printString.toByteArray())
//
//
////delete the image at the end to prevent printer memory sutaration
//                    printerConnection!!.write(("! U1 do \"file.delete\" \"R:TEMP.PCX\"\r\n").toByteArray())
//                    printerConnection!!.write("! U1 FORM\r\n! U1 SETFF 20000 8\r\n".toByteArray()) // for eye sense mark
////
////close the printerConnection!! with the printer
//                    printerConnection!!.close()
//                    //recycle the bitmap
//                    bitmapToPrint1.recycle()
//                } catch (e: ConnectionException) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } catch (e: ZebraIllegalArgumentException) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    helper.showErrorDialogOnGuiThread(e.message)
//                } finally {
//                    if (LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
//                        savePrintCommand()
//                    }
//                    printCallBack!!.onActionSuccess(isErrorUploading)
////                    helper.dismissLoadingDialog()
//                    Looper.loop()
//                    Looper.myLooper()!!.quit()
//                }
//            }
//        }).start()
//    }
//
//    /**
//     * {command} {type} {width} {ratio} {height} {x} {y} {data} = barcode
//     * //create the print commands string
//     * {command} {font} {size} {x} {y} {data} = text
//     *
//     * QRCODE
//     * {command} {type} {x} {y} [M n] [U n]
//     * {data}
//     * <ENDQR
//     *
//     * LINE {command} {x0} {y0} {x1} {y1} {width
//     *
//     * IMAGE {command} {width} {height} {x} {y} {data}
//     **/
//
//
//
//    private fun isBluetoothSelected(): Boolean {
//        return true
//    }
//
//    private fun getMacAddressFieldText(): String {
//        return macAddressEditText!!.text.toString()
//    }
//
//    private fun getTcpAddress(): String {
//        return ipAddressEditText!!.text.toString()
//    }
//
//    private fun tcpPortNumber(): String {
//        return portNumberEditText!!.text.toString()
//    }
//
//    /**
//     * This method checks the mode of connection.
//     *
//     * @return
//     */
//    private fun getZebraPrinterConn(): Connection {
//        var portNumber: Int
//        try {
//            portNumber = tcpPortNumber().toInt()
//        } catch (e: NumberFormatException) {
//            portNumber = 0
//        }
//        return if (isBluetoothSelected()) BluetoothConnection(getMacAddressFieldText()) else TcpConnection(
//            getTcpAddress(), portNumber
//        )
//    }
//
//    /**
//     * This method saves the entered address for the printer.
//     */
//    private fun getAndSaveSettings() {
//        SettingsHelper.saveBluetoothAddress(this@TempPrinterActivity, getMacAddressFieldText())
//        SettingsHelper.saveIp(this@TempPrinterActivity, getTcpAddress())
//        SettingsHelper.savePort(this@TempPrinterActivity, tcpPortNumber())
//
//    }
//
//    private fun createCancelProgressDialog(message: String) {}
//
//    fun drawTextToBitmap(bitmap: Bitmap?, mText: String?): Bitmap? {
//        var bitmap: Bitmap? = bitmap
//        try {
//            var bitmapConfig: Bitmap.Config? = bitmap!!.config
//            if (bitmapConfig == null) {
//                bitmapConfig = Bitmap.Config.ARGB_8888
//            }
//            // resource bitmaps are imutable,
//            // so we need to convert it to mutable one
//            bitmap = bitmap.copy(bitmapConfig, true)
//            val canvas: Canvas = Canvas(bitmap)
//            // new antialised Paint
//            val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
//            // text color - #3D3D3D
//            paint.color = Color.rgb(0, 0, 0)
//            // text size in pixels
//            if(BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)){
//                paint.textSize = (40 * 2).toFloat()
//            }else {
//                paint.textSize = (22 * 2).toFloat()
//            }
//            // text shadow
//            paint.setShadowLayer(1f, 0f, 1f, Color.BLACK)
//
////            paint.getTypeface(context.getResources().getFont(R.font.sf_pro_text_semibold));
//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    paint.typeface = Typeface.create(
//                        context!!.resources.getFont(R.font.timesnewromanpsmtregular),
//                        Typeface.NORMAL
//                    )
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            // draw text to the Canvas center
//            val bounds: Rect = Rect()
//            paint.getTextBounds(mText, 0, mText!!.length, bounds)
//            val x: Int = (bitmap.width)
//            val y: Int = bitmap.height - 70
//            if (mText.equals("R", ignoreCase = true)) {
//                canvas.drawText((mText), (x - 120).toFloat(),
//                    AppUtils.printLabelHeight(screenHeight).toFloat(), paint)
//            } else {
//                if(BuildConfig.FLAVOR.equals(
//                        Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)) {
//                    canvas.drawText(
//                        (mText), (x - 380).toFloat(),
//                        AppUtils.printLabelHeight(screenHeight).toFloat(), paint
//                    )
//                }else{
//                    canvas.drawText(
//                        (mText), (x - 280).toFloat(),
//                        AppUtils.printLabelHeight(screenHeight).toFloat(), paint)
//                }
//
//            }
//            return bitmap
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return bitmap
//        }
//    }
//
//    open fun setScreenResolution() {
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        screenHeight = displayMetrics.heightPixels
////        width = displayMetrics.widthPixels
////        val topSpace: Double = width * 0.22
//    }
//
//    companion object {
//        private val bluetoothAddressKey: String = "ZEBRA_DEMO_BLUETOOTH_ADDRESS"
//        private val tcpAddressKey: String = "ZEBRA_DEMO_TCP_ADDRESS"
//        private val tcpPortKey: String = "ZEBRA_DEMO_TCP_PORT"
//        private val PREFS_NAME: String = "OurSavedAddress"
//    }
//
//
//    private fun getPrintSetFFValueFromSettingFile() {
//        try {
//            var mDb: AppDatabase? = null
//            mDb = BaseApplication.instance?.getAppDatabase()
//            settingsList = java.util.ArrayList()
//            ioScope.async {
//                settingsList = mDb?.dbDAO?.getDataset()?.dataset?.settingsList
//                mainScope.async {
//                    if (settingsList != null && settingsList!!.size > 0) {
//                        for (i in settingsList!!.indices) {
//                            if (settingsList!![i].type.equals(
//                                    "PRINTER_TOP_SETFF",
//                                    ignoreCase = true
//                                )
//                            ) {
//                                topSetFF = settingsList!![i].mValue.toString()
//                            } else if (settingsList!![i].type.equals(
//                                    "PRINTER_BOTTOM_SETFF", ignoreCase = true
//                                )
//                            ) {
//                                bottomSetFF = settingsList!![i].mValue.toString()
//                            } else if (settingsList!![i].type.equals(
//                                    "PRINT_CANVAS_HEIGHT",
//                                    ignoreCase = true
//                                )
//                            ) {
//                                canvasHeight = settingsList!![i].mValue.toString()
//                            } else if (settingsList!![i].type.equals(
//                                    "PRINTINGBY",
//                                    ignoreCase = true
//                                ) &&
//                                settingsList!![i].mValue.equals("CMD", ignoreCase = true)
//                            ) {
//                                printintByCMD = true
//                            } else if (settingsList!![i].type.equals(
//                                    "IS_COPY_PRINTING_DATA_TXT_FILE",
//                                    ignoreCase = true
//                                ) &&
//                                settingsList!![i].mValue.equals("YES", ignoreCase = true)
//                            ) {
//                                isPrintingCommandDataInTxtFile = true
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun writePrinterCommand(content:String)
//    {
//        if(LogUtil.isSavePrintCommand || isPrintingCommandDataInTxtFile) {
//            printerWriterCmd.append(content)
//        }
//    }
//
//    private fun savePrintCommand(){
//        try
//        {
//            mainScope.async {
//                val localFolder =
//                    File(
//                        Environment.getExternalStorageDirectory().absolutePath,
//                        Constants.FILE_NAME
//                    )
//                if (!localFolder.exists()) {
//                    localFolder.mkdirs()
//                }
//                val fileName1 = "printer_command" + ".txt" //like 2016_01_12.txt
//                val file = File(localFolder, fileName1)
//                val writer = FileWriter(file, true)
//                writer.append(printerWriterCmd).append("\n\n")
//                writer.flush()
//                writer.close()
//                if (!file.exists()) {
//                    try {
//                        file.createNewFile()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//
//                    }
//                }
//                LogUtil.printLogPrinterQuery("printer CPCL ",printerWriterCmd.toString())
//            }
//
//        }
//        //Catching any file errors that could occur
//        catch(e: FileNotFoundException)
//        {
//            e.printStackTrace()
//        }
//        catch(e:NumberFormatException)
//        {
//            e.printStackTrace()
//        }
//        catch(e: IOException)
//        {
//            e.printStackTrace()
//        }
//        catch(e:Exception)
//        {
//            e.printStackTrace()
//        }
//    }
//
//
//}