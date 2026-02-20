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
//open class ZebraPrinterActivity : BaseActivity() {
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
//    private var XQRCode = ""
//    private var YQRCode = ""
//    private var mQrCodeSize = 2
//
//    private var XQRCodeLable = ""
//    private var YQRCodeLable = ""
//    private var qrCodeLabel = ""
//    private var FontQRCodeLable = ""
//    private var mBottomAddressSecondLine = ""
//    private var mBottomAddressThirdLine = ""
//    private var mBottomAddressFourthLine = ""
//
//    private var XBottomAddress = "0"
//    private var YBottomAddress = "0"
//    private var YBottomAddressLine1:Int = 0
//    private var YBottomAddressLine2:Int = 0
//    private var YBottomAddressLine3:Int = 0
//    private var BottomAddressValue = ""
//    private var BottomAddressValueArray = arrayOfNulls<String>(8)
//    private var BottomAddress2ValueArray = arrayOfNulls<String>(5)
//    private var BottomAddress3ValueArray = arrayOfNulls<String>(5)
//    private var BottomAddress4ValueArray = arrayOfNulls<String>(8)
//    private var BottomAddress5ValueArray = arrayOfNulls<String>(8)
//    private var BottomAddress6ValueArray = arrayOfNulls<String>(8)
//    private var BottomAddress7ValueArray = arrayOfNulls<String>(8)
//    private var FontBottomAddress = ""
//    private var topSetFF = "150 4"
//    private var bottomSetFF = "1 1"
//    private var canvasHeight = "10"
//    private var printintByCMD = false
//    private var isPrintingCommandDataInTxtFile = false
//    private var printerConnection: Connection? = null
//
//    private var mCitationNumberValue = ""
//    private var mCitationNumberLable = ""
//    private var mCitationNumberX = "5"
//    private var mCitationNumberY = "30"
//    private var mCitationNumberFont = "1"
//
//    private var XBottomAddress2 = "0"
//    private var YBottomAddress2 = "0"
//    private var FontBottomAddress2 = ""
//    private var BottomAddressValue2 = ""
//
//    private var XBottomAddress3 = "0"
//    private var YBottomAddress3 = "0"
//    private var FontBottomAddress3 = ""
//    private var BottomAddressValue3 = ""
//
//    private var XBottomAddress4 = "0"
//    private var YBottomAddress4 = "0"
//    private var FontBottomAddress4 = ""
//    private var BottomAddressValue4 = ""
//
//
//    private var XBottomAddress5 = "0"
//    private var YBottomAddress5 = "0"
//    private var FontBottomAddress5 = ""
//    private var BottomAddressValue5 = ""
//
//    private var XBottomAddress6 = "0"
//    private var YBottomAddress6 = "0"
//    private var FontBottomAddress6 = ""
//    private var BottomAddressValue6 = ""
//
//    private var XBottomAddress7 = "0"
//    private var YBottomAddress7 = "0"
//    private var FontBottomAddress7 = ""
//    private var BottomAddressValue7 = ""
//
//    private var XBottomSchedule = "0"
//    private var YBottomSchedule = "0"
//    private var FontBottomSchedule = ""
//    private var BottomScheduleValue =""
//    private var mBottomAddressQuery = StringBuilder()
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
//        val mac: String? = SettingsHelper.getBluetoothAddressKey(this@ZebraPrinterActivity)
//        macAddressEditText!!.setText(mac)
//        val t2: TextView = findViewById<View>(R.id.launchpad_link) as TextView
//        t2.movementMethod = LinkMovementMethod.getInstance()
//
//
//        if (ContextCompat.checkSelfPermission(
//                this@ZebraPrinterActivity,
//                Manifest.permission.BLUETOOTH_CONNECT
//            ) == PackageManager.PERMISSION_DENIED
//            || ContextCompat.checkSelfPermission(
//                this@ZebraPrinterActivity,
//                Manifest.permission.BLUETOOTH_SCAN
//            ) == PackageManager.PERMISSION_DENIED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                ActivityCompat.requestPermissions(
//                    this@ZebraPrinterActivity, arrayOf(
//                        Manifest.permission.BLUETOOTH_SCAN,
//                        Manifest.permission.BLUETOOTH_CONNECT
//                    ), BLUETOOTH_PERMISSION_REQUEST_CODE
//                )
//            }
//        }
//        if(macAddressEditText!!.text!!.isEmpty()) {
//            getPairedPrintersTo()
//        }
//        setScreenResolution()
//        /**
//         * Barcode and Qrcode setting
//         */
////        setLayoutVisibilityBasedOnSettingResponse()
//
//        XQRCodeLable = sharedPreference.read(
//            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_X,"").isEmpty("0")
//        YQRCodeLable = sharedPreference.read(
//            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_Y,"").isEmpty("0")
//        qrCodeLabel = sharedPreference.read(
//            SharedPrefKey.QRCODE_LABEL_FOR_PRINT,"").isEmpty("")
//        FontQRCodeLable = sharedPreference.read(
//            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_FONT,"").isEmpty("")
//
//        XBottomAddress = sharedPreference.read(
//            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_X,"").toString()
//        YBottomAddress = sharedPreference.read(
//            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_Y,"").toString()
//        BottomAddressValue = sharedPreference.read(
//            SharedPrefKey.FOOTER_LABEL_FOR_PRINT,"").toString()
//        FontBottomAddress = sharedPreference.read(
//            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_FONT,"").toString()
//
//        XBottomAddress2 = sharedPreference.read(
//            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_X,"").toString()
//        YBottomAddress2 = sharedPreference.read(
//            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_Y,"").toString()
//        BottomAddressValue2 = sharedPreference.read(
//            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT,"").toString()
//        FontBottomAddress2 = sharedPreference.read(
//            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_FONT,"").toString()
//
//        XBottomAddress3 = sharedPreference.read(
//            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_X,"").toString()
//        YBottomAddress3 = sharedPreference.read(
//            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_Y,"").toString()
//        BottomAddressValue3 = sharedPreference.read(
//            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT,"").toString()
//        FontBottomAddress3 = sharedPreference.read(
//            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_FONT,"").toString()
//
//        XBottomAddress4 = sharedPreference.read(
//            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_X,"").toString()
//        YBottomAddress4 = sharedPreference.read(
//            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_Y,"").toString()
//        BottomAddressValue4 = sharedPreference.read(
//            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT,"").toString()
//        FontBottomAddress4 = sharedPreference.read(
//            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_FONT,"").toString()
//
//        XBottomAddress5 = sharedPreference.read(
//            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_X,"").toString()
//        YBottomAddress5 = sharedPreference.read(
//            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_Y,"").toString()
//        BottomAddressValue5 = sharedPreference.read(
//            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT,"").toString()
//        FontBottomAddress5 = sharedPreference.read(
//            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_FONT,"").toString()
//
//        XBottomAddress6 = sharedPreference.read(
//            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_X,"").toString()
//        YBottomAddress6 = sharedPreference.read(
//            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_Y,"").toString()
//        BottomAddressValue6 = sharedPreference.read(
//            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT,"").toString()
//        FontBottomAddress6 = sharedPreference.read(
//            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_FONT,"").toString()
//
//        XBottomAddress7 = sharedPreference.read(
//            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_X,"").toString()
//        YBottomAddress7 = sharedPreference.read(
//            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_Y,"").toString()
//        BottomAddressValue7 = sharedPreference.read(
//            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT,"").toString()
//        FontBottomAddress7 = sharedPreference.read(
//            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_FONT,"").toString()
//
//        XBottomSchedule = sharedPreference.read(
//            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_X,"").toString()
//        YBottomSchedule = sharedPreference.read(
//            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_Y,"").toString()
//        BottomScheduleValue = sharedPreference.read(
//            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT,"").toString()
//        FontBottomSchedule = sharedPreference.read(
//            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_FONT,"").toString()
//
//        XQRCode = sharedPreference.read(
//            SharedPrefKey.QRCODE_FOR_PRINT_X,"").toString()
//        YQRCode = sharedPreference.read(
//            SharedPrefKey.QRCODE_FOR_PRINT_Y,"").toString()
//
//        mCitationNumberValue = sharedPreference.read(
//            SharedPrefKey.CITATION_NUMBER_FOR_PRINT,"").toString()
//        mCitationNumberLable= sharedPreference.read(
//            SharedPrefKey.CITATION_NUMBER_LABEL_FOR_PRINT,"").isEmpty("Citation#")
//        mCitationNumberX= sharedPreference.read(
//            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_X,"").isEmpty("5")
//        mCitationNumberY= sharedPreference.read(
//            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_Y,"").isEmpty("30")
//        mCitationNumberFont= sharedPreference.read(
//            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_FONT,"").isEmpty("1")
//
//
//        if(YBottomAddress.isNotEmpty()  && BottomAddressValue!!.isNotEmpty()) {
//            BottomAddressValueArray = BottomAddressValue.split("#").toTypedArray()
//            YBottomAddressLine1 = Integer.parseInt(YBottomAddress) + 20
//            YBottomAddressLine2 = YBottomAddressLine1 + 20
//            YBottomAddressLine3 = YBottomAddressLine2 + 20
//            mBottomAddressSecondLine = if (BottomAddressValueArray.size > 1) BottomAddressValueArray[1].toString() else ""
//            mBottomAddressThirdLine = if (BottomAddressValueArray.size > 2) BottomAddressValueArray[2].toString() else ""
//            mBottomAddressFourthLine = if (BottomAddressValueArray.size > 3) BottomAddressValueArray[3].toString() else ""
//        }
////        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
////        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
////            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true))
////        {
//        try {
//            BottomAddress2ValueArray = BottomAddressValue2.split("#").toTypedArray()
//            BottomAddress3ValueArray = BottomAddressValue3.split("#").toTypedArray()
//            BottomAddress4ValueArray = BottomAddressValue4.split("#").toTypedArray()
//            BottomAddress5ValueArray = BottomAddressValue5.split("#").toTypedArray()
//            BottomAddress6ValueArray = BottomAddressValue6.split("#").toTypedArray()
//            BottomAddress7ValueArray = BottomAddressValue7.split("#").toTypedArray()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
////        }
////            }
//
//        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
//        ) {
//            mQrCodeSize = 3
//        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true))
//        {
//            mQrCodeSize = 4
//        }
//        try {
//            if(printerConnection==null) {
//                printerConnection = getZebraPrinterConn()
//            }
////            if(BuildConfig.FLAVOR.equals(
////                    Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = false)||BuildConfig.FLAVOR.equals(
////                    Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = false)||
////                BuildConfig.FLAVOR.equals(
////                    Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = false)) {
//            if (mBottomAddressQuery != null) {
//                mBottomAddressQuery!!.clear()
//            }
//
//            mBottomAddressText()
//
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)) {
//                getPrintSetFFValueFromSettingFile()
//            }
//
//        }catch ( e: Exception)
//        {
//            e.printStackTrace()
//        }
//
//    }
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
//                        this@ZebraPrinterActivity,
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
//                    LogUtil.printLogHeader("QUERY",AppUtils.printQueryStringBuilder.toString())
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
//                            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)&& isTextPrintingBySite||
//                            BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)&& isTextPrintingBySite||
//                            BuildConfig.FLAVOR.equals(
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
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
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
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)||
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
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
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
//                                    //Adding dynamic Y Axis & Print Height
//                                    if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                        addYAxisToSet(YBottomAddress.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                                        addYAxisToSet(YBottomAddressLine1.nullSafety().toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                                        addYAxisToSet(YBottomAddressLine2.nullSafety().toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                                        newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                                    }
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
//                                            .trimIndent() + " \r\n"
////                                        + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
////                                        + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                                + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddress + " " + BottomAddressValueArray[0] + " \r\n"
//                                                + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine1 + " " + mBottomAddressSecondLine + " \r\n"
//                                                + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine2 + " " + mBottomAddressThirdLine + " \r\n"
//                                                + "BAR-SENSE" + "\r\n"
//                                                + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
//                                                + "FORM" + "\r\n"
//                                                + "PRINT" + "\r\n") //print
//                                }else {
//                                    //Adding dynamic Y Axis & Print Height
//                                    if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                        addYAxisToSet(YQRCode.nullOrEmptySafety("0").toDouble(), getYAxisBasedOnQRCodeHeight(mQrCodeSize))
//                                        addYAxisToSet(YQRCodeLable.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt()))
//                                        addYAxisToSet(YBottomAddress.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                                        addYAxisToSet(YBottomAddressLine1.nullSafety().toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                                        addYAxisToSet(YBottomAddressLine2.nullSafety().toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                                        addYAxisToSet(YBottomAddressLine3.nullSafety().toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                                        newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                                    }
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
//                                            .trimIndent() + " \r\n"
//                                                + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
//                                                + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                                + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddress + " " + BottomAddressValueArray[0] + " \r\n"
//                                                + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine1 + " " + mBottomAddressSecondLine + " \r\n"
//                                                + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine2 + " " + mBottomAddressThirdLine + " \r\n"
//                                                + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine3 + " " + mBottomAddressFourthLine + " \r\n"
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
//                            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) && !isTextPrintingBySite||BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) && !isTextPrintingBySite) {
//
//                        //Adding dynamic Y Axis & Print Height
//                        if (LogUtil.isPrintHeightDynamicFromTicket) {
//                            addYAxisToSet(YQRCode.nullOrEmptySafety("0").toDouble(), getYAxisBasedOnQRCodeHeight(2))
//                            addYAxisToSet(YQRCodeLable.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt()))
//                            addYAxisToSet(YBottomAddress.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                            addYAxisToSet(YBottomAddressLine1.nullSafety().toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                            addYAxisToSet(YBottomAddressLine2.nullSafety().toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                            addYAxisToSet(YBottomAddressLine3.nullSafety().toDouble(), getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt()))
//                            newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                        }
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
//                                    +"B QR "+XQRCode +" "+YQRCode +" M 2 U 2\r\n" + "M0A,QR code "+ AppUtils.mFinalQRCodeValue+" \r\n" + "ENDQR\r\n"
//                                    + "TEXT 7 "+FontQRCodeLable+" "+ XQRCodeLable+" "+ YQRCodeLable+" "+ qrCodeLabel+" \r\n"
//                                    + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                    + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddress + " " + BottomAddressValueArray[0] + " \r\n"
//                                    + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine1 + " " + mBottomAddressSecondLine + " \r\n"
//                                    + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine2 + " " + mBottomAddressThirdLine + " \r\n"
//                                    + "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine3 + " " + mBottomAddressFourthLine + " \r\n"
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
//                            Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) && !isTextPrintingBySite||
//                        BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) && !isTextPrintingBySite||
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
//                                        + mBottomAddressQuery
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }else {
//                            //Adding dynamic Y Axis & Print Height
//                            //TODO JANAK CRASH
//                            if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                addYAxisToSet(YQRCode.nullOrEmptySafety("0").toDouble(), getYAxisBasedOnQRCodeHeight(mQrCodeSize))
//                                addYAxisToSet(YQRCodeLable.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt()))
//                                newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                            }
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
//                                        + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
//                                        + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                        + mBottomAddressQuery
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
//                                        //+ mBottomAddressQuery
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
////                                        + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
////                                        + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                        //+ mBottomAddressQuery
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
//                        ) && !isTextPrintingBySite ||
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
//                                        + mBottomAddressQuery
//                                        + "BAR-SENSE" + "\r\n"
//                                        //+ "PCX 5 0 !<TEMP_LPR.PCX" + "\r\n"
////                                        + "PCX 5 500 !<TEMP_LPR.PCX" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        } else {
//                            //Adding dynamic Y Axis & Print Height
//                            if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                addYAxisToSet(YQRCode.nullOrEmptySafety("0").toDouble(), getYAxisBasedOnQRCodeHeight(mQrCodeSize))
//                                addYAxisToSet(YQRCodeLable.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt()))
//                                newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
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
//                                        + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
//                                        + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                        + mBottomAddressQuery
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
//                                        + mBottomAddressQuery
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        } else {
//                            //Adding dynamic Y Axis & Print Height
//                            if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                addYAxisToSet(YQRCode.nullOrEmptySafety("0").toDouble(), getYAxisBasedOnQRCodeHeight(mQrCodeSize))
//                                addYAxisToSet(YQRCodeLable.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt()))
//                                newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                            }
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
//                                        + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
//                                        + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                        + mBottomAddressQuery
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
//                            //Adding dynamic Y Axis & Print Height
//                            if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                addYAxisToSet(mCitationNumberY.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(mCitationNumberFont.nullOrEmptySafety("0").toInt()))
//                                newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                            }
//
//                            LogUtil.printLog("==>Printer:","HeightMaxInner:${AppUtils.getMaxYAxisFromCommand()}")
//
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
//                                        + "TEXT 7 " + mCitationNumberFont + " " + mCitationNumberX + " " + mCitationNumberY + " " + mCitationNumberLable + " \r\n"
//                                        + "TEXT 7 " + mCitationNumberFont + " " + (mCitationNumberX.toInt()+140) + " " + mCitationNumberY + " " + mCitationNumberValue + " \r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + mBottomAddressQuery
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }else {
//                            //Adding dynamic Y Axis & Print Height
//                            if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                addYAxisToSet(YQRCode.nullOrEmptySafety("0").toDouble(), getYAxisBasedOnQRCodeHeight(mQrCodeSize))
//                                addYAxisToSet(YQRCodeLable.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt()))
//                                addYAxisToSet(mCitationNumberY.nullOrEmptySafety().toDouble(), getHeightBasedOnFont(mCitationNumberFont.nullOrEmptySafety("0").toInt()))
//                                newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                            }
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
//                                        + "TEXT 7 " + mCitationNumberFont + " " + mCitationNumberX + " " + mCitationNumberY + " " + mCitationNumberLable + " \r\n"
//                                        + "TEXT 7 " + mCitationNumberFont + " " + (mCitationNumberX.toInt()+130) + " " + mCitationNumberY + " " + mCitationNumberValue + " \r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
//                                        + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                        + mBottomAddressQuery
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
//                            //Adding dynamic Y Axis & Print Height
//                            if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                addYAxisToSet(mCitationNumberY.nullOrEmptySafety().toDouble(), getHeightBasedOnFont(mCitationNumberFont.nullOrEmptySafety("0").toInt()))
//                                newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                            }
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
//                                        + "TEXT 7 " + mCitationNumberFont + " " + mCitationNumberX + " " + mCitationNumberY + " " + mCitationNumberLable + " \r\n"
//                                        + "TEXT 7 " + mCitationNumberFont + " " + (mCitationNumberX.toInt()+140) + " " + mCitationNumberY + " " + mCitationNumberValue + " \r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
////                                        + "TEXT90 2 13 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                        + mBottomAddressQuery
//                                        + "BAR-SENSE" + "\r\n"
//                                        + "FORM" + "\r\n"
//                                        + "PRINT" + "\r\n") //print
//                        }else {
//                            //Adding dynamic Y Axis & Print Height
//                            if (LogUtil.isPrintHeightDynamicFromTicket) {
//                                addYAxisToSet(YQRCode.nullOrEmptySafety("0").toDouble(), getYAxisBasedOnQRCodeHeight(mQrCodeSize))
//                                addYAxisToSet(YQRCodeLable.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt()))
//                                addYAxisToSet(mCitationNumberY.nullOrEmptySafety().toDouble(), getHeightBasedOnFont(mCitationNumberFont.nullOrEmptySafety("0").toInt()))
//                                newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                            }
//                            printString =
//                                ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
//                                        + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
//                                        + "TONE 125" + "\r\n" //print intensity tone 0-200
//                                        + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
//                                        + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
//                                        + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
//                                        + "NO-PACE" + "\r\n"
//                                        + "SETSP 1\r\n"
//                                        + "TEXT 7 " + mCitationNumberFont + " " + mCitationNumberX + " " + mCitationNumberY + " " + mCitationNumberLable + " \r\n"
//                                        + "TEXT 7 " + mCitationNumberFont + " " + (mCitationNumberX.toInt()+130) + " " + mCitationNumberY + " " + mCitationNumberValue + " \r\n"
//                                        + AppUtils.printQueryStringBuilder.toString().trimIndent() + " \r\n"
//                                        + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
//                                        + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
////                                        + "TEXT90 2 13 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                        + mBottomAddressQuery
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
////                                    + "B QR "+XQRCode +" "+YQRCode +" M 2 U "+mQrCodeSize+" \r\n" + "M0A,QR code "+ AppUtils.mFinalQRCodeValue+" \r\n" + "ENDQR\r\n"
////                                    + "TEXT 7 "+FontQRCodeLable+" "+ XQRCodeLable+" "+ YQRCodeLable+" "+ qrCodeLabel+" \r\n"
////                                    + "TEXT 7 "+FontBottomAddress+" "+ XBottomAddress+" "+ YBottomAddress+" "+ BottomAddressValueArray[0]+" \r\n"
////                                    + "TEXT 7 "+FontBottomAddress+" "+ XBottomAddress+" "+ YBottomAddressLine1+" "+ if(BottomAddressValueArray.size>1)BottomAddressValueArray[1] else ""+" \r\n"
////                                    + "TEXT 7 "+FontBottomAddress+" "+ XBottomAddress+" "+ YBottomAddressLine2+" "+ if(BottomAddressValueArray.size>2)BottomAddressValueArray[2] else ""+" \r\n"
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
//                        || (
//                                BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) && !isTextPrintingBySite)
//                        ||    (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) && !isTextPrintingBySite)
//                        || (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) && !isTextPrintingBySite)
//                        || (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) && !isTextPrintingBySite)
//                        || (BuildConfig.FLAVOR.equals(
//                            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) && !isTextPrintingBySite)){
//                        //Adding dynamic Y Axis & Print Height
//                        if (LogUtil.isPrintHeightDynamicFromTicket) {
//                            addYAxisToSet(YQRCode.nullOrEmptySafety("0").toDouble(), getYAxisBasedOnQRCodeHeight(mQrCodeSize))
//                            addYAxisToSet(YQRCodeLable.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt()))
//                            newBitmapHeight = AppUtils.getMaxYAxisFromCommand()
//                        }
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
//                                    + "B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n"
//                                    + "TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n"
//                                    +  mBottomAddressQuery
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
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
//                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
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
//        SettingsHelper.saveBluetoothAddress(this@ZebraPrinterActivity, getMacAddressFieldText())
//        SettingsHelper.saveIp(this@ZebraPrinterActivity, getTcpAddress())
//        SettingsHelper.savePort(this@ZebraPrinterActivity, tcpPortNumber())
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
//                            } else if (settingsList!![i].type.equals(
//                                    Constants.SETTINGS_FLAG_QR_CODE_SIZE,
//                                    ignoreCase = true
//                                )
//                            ) {
//                                mQrCodeSize = settingsList!![i].mValue!!.toInt()
//                            }
//                        }
//
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
//    private fun mBottomAddressText() :String{
//        try {
//            if (BottomScheduleValue != null && BottomScheduleValue!!.isNotEmpty()) {
//                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomSchedule + " " + XBottomSchedule + " " + YBottomSchedule + " " + BottomScheduleValue + " \r\n")
//                addYAxisToSet(YBottomSchedule.nullOrEmptySafety().toDouble() + 28, FontBottomSchedule.nullOrEmptySafety("0").toInt())
//            }
//            if (BottomAddressValueArray != null && BottomAddressValueArray!!.isNotEmpty() && BottomAddressValueArray[0] != null) {
//                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddress + " " + BottomAddressValueArray[0] + " \r\n")
//                addYAxisToSet(YBottomAddress.nullOrEmptySafety().toDouble() + 28, FontBottomAddress.nullOrEmptySafety("0").toInt())
//            }
//            if (mBottomAddressSecondLine != null && mBottomAddressSecondLine!!.isNotEmpty()) {
//                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine1 + " " + mBottomAddressSecondLine + " \r\n")
//                addYAxisToSet(YBottomAddressLine1.nullSafety().toDouble() + 28, FontBottomAddress.nullOrEmptySafety("0").toInt())
//            }
//            if (mBottomAddressThirdLine != null && mBottomAddressThirdLine!!.isNotEmpty()) {
//                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine2 + " " + mBottomAddressThirdLine + " \r\n")
//                addYAxisToSet(YBottomAddressLine2.nullSafety().toDouble() + 28, FontBottomAddress.nullOrEmptySafety("0").toInt())
//            }
//            if (mBottomAddressFourthLine != null && mBottomAddressFourthLine!!.isNotEmpty()) {
//                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine3 + " " + mBottomAddressFourthLine + " \r\n")
//                addYAxisToSet(YBottomAddressLine3.nullSafety().toDouble() + 28, FontBottomAddress.nullOrEmptySafety("0").toInt())
//            }
//            if (BottomAddressValueArray != null && BottomAddressValueArray.size > 4 && BottomAddressValueArray[4] != null && BottomAddressValueArray[4]!!.isNotEmpty()) {
//                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + (YBottomAddressLine3.toInt() + 20) + " " + BottomAddressValueArray[4] + " \r\n")
//                addYAxisToSet((YBottomAddressLine3 + 20).nullSafety().toDouble() + 28, FontBottomAddress.nullOrEmptySafety("0").toInt())
//            }
//            if (BottomAddressValueArray != null && BottomAddressValueArray.size > 5 && BottomAddressValueArray[5] != null && BottomAddressValueArray[5]!!.isNotEmpty()) {
//                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + (YBottomAddressLine3.toInt() + 40) + " " + BottomAddressValueArray[5] + " \r\n")
//                addYAxisToSet((YBottomAddressLine3 + 40).nullSafety().toDouble() + 28, FontBottomAddress.nullOrEmptySafety("0").toInt())
//            }
//            if (BottomAddressValueArray != null && BottomAddressValueArray.size > 6 && BottomAddressValueArray[6] != null && BottomAddressValueArray[6]!!.isNotEmpty()) {
//                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + (YBottomAddressLine3.toInt() + 60) + " " + BottomAddressValueArray[6] + " \r\n")
//                addYAxisToSet((YBottomAddressLine3 + 60).nullSafety().toDouble() + 28, FontBottomAddress.nullOrEmptySafety("0").toInt())
//            }
//
//            //Adding Footer 2 using loop to make it dynamic
//            BottomAddress2ValueArray.forEachIndexed { index, s ->
//                val yIndex = index * 20
//                mBottomAddressQuery.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt() + yIndex) + " " + s + " \r\n")
//                addYAxisToSet(yIndex.nullSafety().toDouble(), FontBottomAddress2.nullOrEmptySafety("0").toInt())
//            }
//
//
////            if(BottomAddress2ValueArray[0]!=null && BottomAddress2ValueArray[0]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + YBottomAddress2 + " " + BottomAddress2ValueArray[0]+ " \r\n")
////            if(BottomAddress2ValueArray!!.size>1&& BottomAddress2ValueArray[1]!=null  && BottomAddress2ValueArray[1]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+20) + " " + BottomAddress2ValueArray[1]+ " \r\n")
////            if(BottomAddress2ValueArray!!.size>2&& BottomAddress2ValueArray[2]!=null && BottomAddress2ValueArray[2]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+40) + " " + BottomAddress2ValueArray[2]+ " \r\n")
////            if(BottomAddress2ValueArray!!.size>3&& BottomAddress2ValueArray[3]!=null && BottomAddress2ValueArray[3]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+60) + " " + BottomAddress2ValueArray[3]+ " \r\n")
////            if(BottomAddress2ValueArray!!.size>4&& BottomAddress2ValueArray[4]!=null && BottomAddress2ValueArray[4]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+80) + " " + BottomAddress2ValueArray[4]+ " \r\n")
////            if(BottomAddress2ValueArray!!.size>5&& BottomAddress2ValueArray[5]!=null && BottomAddress2ValueArray[5]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + (YBottomAddress2.toInt()+100) + " " + BottomAddress2ValueArray[5]+ " \r\n")
//
//
//            //Adding Footer 3 using loop to make it dynamic
//            BottomAddress3ValueArray.forEachIndexed { index, s ->
//                val yIndex = index * 20
//                mBottomAddressQuery.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt() + yIndex) + " " + s + " \r\n")
//                addYAxisToSet(yIndex.nullSafety().toDouble(), FontBottomAddress3.nullOrEmptySafety("0").toInt())
//            }
//
//
////            if(BottomAddress3ValueArray[0]!=null && BottomAddress3ValueArray[0]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt()) + " " + BottomAddress3ValueArray[0] + " \r\n")
////            if(BottomAddress3ValueArray!!.size>1&& BottomAddress3ValueArray[1]!=null && BottomAddress3ValueArray[1]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt()+20) + " " + BottomAddress3ValueArray[1] + " \r\n")
////            if(BottomAddress3ValueArray!!.size>2&& BottomAddress3ValueArray[2]!=null && BottomAddress3ValueArray[2]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt()+40) + " " + BottomAddress3ValueArray[2] + " \r\n")
////            if(BottomAddress3ValueArray!!.size>3&& BottomAddress3ValueArray[3]!=null && BottomAddress3ValueArray[3]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + (YBottomAddress3.toInt()+60) + " " + BottomAddress3ValueArray[3] + " \r\n")
//
//
//            //Adding Footer 4 using loop to make it dynamic
//            BottomAddress4ValueArray.forEachIndexed { index, s ->
//                val yIndex = index * 20
//                mBottomAddressQuery.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+yIndex) + " " + s + " \r\n")
//                addYAxisToSet(yIndex.nullSafety().toDouble(), FontBottomAddress4.nullOrEmptySafety("0").toInt())
//            }
//
//
//
//
////            if(BottomAddress4ValueArray[0]!=null && BottomAddress4ValueArray[0]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + YBottomAddress4 + " " + BottomAddress4ValueArray[0] + " \r\n")
////            if(BottomAddress4ValueArray.size>1&& BottomAddress4ValueArray[1]!=null && BottomAddress4ValueArray[1]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+20) + " " + BottomAddress4ValueArray[1] + " \r\n")
////            if(BottomAddress4ValueArray.size>2&& BottomAddress4ValueArray[2]!=null && BottomAddress4ValueArray[2]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+40) + " " + BottomAddress4ValueArray[2] + " \r\n")
////            if(BottomAddress4ValueArray.size>3&& BottomAddress4ValueArray[3]!=null && BottomAddress4ValueArray[3]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+60) + " " + BottomAddress4ValueArray[3] + " \r\n")
////            if(BottomAddress4ValueArray.size>4&& BottomAddress4ValueArray[4]!=null && BottomAddress4ValueArray[4]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+80) + " " + BottomAddress4ValueArray[4] + " \r\n")
////            if(BottomAddress4ValueArray.size>5&& BottomAddress4ValueArray[5]!=null && BottomAddress4ValueArray[5]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+100) + " " + BottomAddress4ValueArray[5] + " \r\n")
////            if(BottomAddress4ValueArray.size>6&& BottomAddress4ValueArray[6]!=null && BottomAddress4ValueArray[6]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+120) + " " + BottomAddress4ValueArray[6] + " \r\n")
////            if(BottomAddress4ValueArray.size>6&& BottomAddress4ValueArray[6]!=null && BottomAddress4ValueArray[6]!!.isNotEmpty())
////                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + (YBottomAddress4.toInt()+120) + " " + BottomAddress4ValueArray[6] + " \r\n")
//
//            //Adding Footer 5 using loop to make it dynamic
//            BottomAddress5ValueArray.forEachIndexed { index, s ->
//                val yIndex = index * 20
//                mBottomAddressQuery.append("TEXT 7 " + FontBottomAddress5 + " " + XBottomAddress5 + " " + (YBottomAddress5.toInt()+yIndex) + " " + s + " \r\n")
//                addYAxisToSet(yIndex.nullSafety().toDouble(), FontBottomAddress5.nullOrEmptySafety("0").toInt())
//            }
//
//            //Adding Footer 6 using loop to make it dynamic
//            BottomAddress6ValueArray.forEachIndexed { index, s ->
//                val yIndex = index * 20
//                mBottomAddressQuery.append("TEXT 7 " + FontBottomAddress6 + " " + XBottomAddress6 + " " + (YBottomAddress6.toInt()+yIndex) + " " + s + " \r\n")
//                addYAxisToSet(yIndex.nullSafety().toDouble(), FontBottomAddress6.nullOrEmptySafety("0").toInt())
//            }
//
//
//            //Adding Footer 7 using loop to make it dynamic
//            BottomAddress7ValueArray.forEachIndexed { index, s ->
//                val yIndex = index * 20
//                mBottomAddressQuery.append("TEXT 7 " + FontBottomAddress7 + " " + XBottomAddress7 + " " + (YBottomAddress7.toInt()+yIndex) + " " + s + " \r\n")
//                addYAxisToSet(yIndex.nullSafety().toDouble(), FontBottomAddress7.nullOrEmptySafety("0").toInt())
//            }
//
//
//
//            if (
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
//                ){
//                //No Line is Needed at bottom when site is PRRS
//            }else{
//                if (BottomAddress5ValueArray.isNotEmpty()){
//                    val yAxisForAddressFive = YBottomAddress5.toInt() + 150
//                    mBottomAddressQuery.append("LINE " + 1 + " " + (YBottomAddress5.toInt() + 150) + " 560 " + yAxisForAddressFive + " 1 \r\n")
//                    addYAxisToSet(yAxisForAddressFive.nullSafety().toDouble(), PRINT_LINE_HEIGHT)
//                }else{
//                    val yAxisForAddressFour = YBottomAddress4.toInt() + 150
//                    mBottomAddressQuery.append("LINE " + 1 + " " + (YBottomAddress4.toInt() + 150) + " 560 " + yAxisForAddressFour + " 1 \r\n")
//                    addYAxisToSet(yAxisForAddressFour.nullSafety().toDouble(), PRINT_LINE_HEIGHT)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return ""
//    }
//}