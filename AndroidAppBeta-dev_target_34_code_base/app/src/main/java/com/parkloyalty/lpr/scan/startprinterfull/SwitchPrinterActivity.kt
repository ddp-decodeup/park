
package com.parkloyalty.lpr.scan.startprinterfull

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.startprinterfull.functions.PrinterFunctions
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.printer.UIHelper
import com.parkloyalty.lpr.scan.util.AppUtils
import com.starmicronics.stario.PortInfo
import com.starmicronics.stario.StarIOPort
import com.starmicronics.stario.StarIOPortException
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.comm.TcpConnection
import com.zebra.sdk.device.ZebraIllegalArgumentException
import com.zebra.sdk.graphics.internal.ZebraImageAndroid
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.printer.ZebraPrinterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


open class SwitchPrinterActivity : CommonActivity() {
//    private val connection: Connection? = null
    private var imageSelectionSpinner: Spinner? = null
    private var cb: CheckBox? = null
    private var angleSpinner: Spinner? = null
    private val myDialog: ProgressDialog? = null
    private var btRadioButton: RadioButton? = null
    private var macAddressEditText: EditText? = null
    private var ipAddressEditText: EditText? = null
    private var portNumberEditText: EditText? = null
    private var printStoragePath: EditText? = null
    private val helper: UIHelper = UIHelper(this)
    private var zebraFooterLogo: ImageView? = null
    private var rotationAngle: Int = 180
    private var printCallBack: PrintInterface? = null
    private var context: Context? = null
    private var mFrom: String? = null
    private var mLabel: String? = null
    private var mTicketNumber: String? = null
    private var mAmount: String? = null
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 2111
//    private var settingsList: List<DatasetResponse>? = null
    //    private var mDb: AppDatabase? = null
//    private var isBarCodePrintlayout: Boolean = true
//    private var isQRCodePrintlayout: Boolean = false
//    private var mQRCodeValue: String? = null
//    private var mIssuranceModel: CitationInsurranceDatabaseModel? =
//        CitationInsurranceDatabaseModel()

    private var mVehicleList = ArrayList<VehicleListModel>()
    private var isErrorUploading: String = ""


/**
     * Star
     */


//    private var printCallBack: PrintInterface? = null
    private var mBitmap: Bitmap? = null
    var imgFilePath: File? = null
//    var mFrom = ""
//    var mLabel = ""
    private val mModelIndex = 19
    private var mPortName = "test"
    private val mPortSettings = "Portable"
    private var mMacAddress = ""
    private var mModelName = ""
    private var isOnCreate = true
//    private var isErrorUploading = ""
    private var width = 0
    private var height = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imageprintlayout)
        context = this
        setScreenResolution()
//        mDb = BaseApplication.instance?.getAppDatabase()
        if (AppUtils.getPrinterSetting() == Constants.SETTING_VALUE_PRINTER) {
//        if (true) {
            zebraPrinterInit()
        }else {
//            starPrinterInit()
        }

    }

    private fun zebraPrinterInit()
    {
        val settings: SharedPreferences = getSharedPreferences(PREFS_NAME, 0)
        zebraFooterLogo = findViewById<View>(R.id.zebraFooterLogo) as ImageView?
        ipAddressEditText = findViewById<View>(R.id.ipAddressInput) as EditText?
        val ip: String? = settings.getString(tcpAddressKey, "")
        ipAddressEditText!!.setText(ip)
        portNumberEditText = findViewById<View>(R.id.portInput) as EditText?
        val port: String? = settings.getString(tcpPortKey, "")
        portNumberEditText!!.setText(port)
        macAddressEditText = findViewById<View>(R.id.macInput) as EditText?
        val mac: String? = settings.getString(bluetoothAddressKey, "")
        macAddressEditText!!.setText(mac)
        val t2: TextView = findViewById<View>(R.id.launchpad_link) as TextView
        t2.movementMethod = LinkMovementMethod.getInstance()
        printStoragePath = findViewById<View>(R.id.printerStorePath) as EditText?
        cb = findViewById<View>(R.id.checkBox) as CheckBox?
        cb!!.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    printStoragePath!!.visibility = View.VISIBLE
                } else {
                    printStoragePath!!.visibility = View.INVISIBLE
                }
            }
        })
        btRadioButton = findViewById<View>(R.id.bluetoothRadio) as RadioButton?
        btRadioButton!!.isChecked = true
        toggleEditField(macAddressEditText, true)
        toggleEditField(portNumberEditText, false)
        toggleEditField(ipAddressEditText, false)
        val radioGroup: RadioGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
                if (checkedId == R.id.bluetoothRadio) {
                    toggleEditField(macAddressEditText, true)
                    toggleEditField(portNumberEditText, false)
                    toggleEditField(ipAddressEditText, false)
                } else {
                    toggleEditField(portNumberEditText, true)
                    toggleEditField(ipAddressEditText, true)
                    toggleEditField(macAddressEditText, false)
                }
            }
        })
        angleSpinner = findViewById<View>(R.id.rotationSpinner) as Spinner?
        val angleAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.rotation_array,
            android.R.layout.simple_spinner_item
        )
        angleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        angleSpinner!!.adapter = angleAdapter
        angleSpinner!!.onItemSelectedListener = MyOnItemSelectedListener()
        imageSelectionSpinner = findViewById<View>(R.id.imageSelection) as Spinner?
        val imageAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.image_selection,
            android.R.layout.simple_spinner_item
        )
        imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        imageSelectionSpinner!!.adapter = imageAdapter
        imageSelectionSpinner!!.onItemSelectedListener = OnItemsSelectedListener()
        zebraFooterLogo!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
//                getBitmapFromPath(LprPreviewActivity.savePrintImagePath,PrinterActivity.this);
            }
        })

        if (ContextCompat.checkSelfPermission(
                this@SwitchPrinterActivity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(
                this@SwitchPrinterActivity,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this@SwitchPrinterActivity, arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ), BLUETOOTH_PERMISSION_REQUEST_CODE
                )
            }
        }
        getPairedPrintersTo()

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
                        this@SwitchPrinterActivity,
                        getString(R.string.error_bluetooth_permission_is_not_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    getPairedPrintersTo()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PICTURE) {
//                printRotatedPhotoFromExternal(BitmapFactory.decodeFile(file.getAbsolutePath()), rotationAngle);
            }
            if (requestCode == PICTURE_FROM_GALLERY) {
                val imgPath: Uri? = data!!.data
                var myBitmap: Bitmap? = null
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
                } catch (e: FileNotFoundException) {
                    helper.showErrorDialog(e.message)
                } catch (e: IOException) {
                    helper.showErrorDialog(e.message)
                }
                //                printRotatedPhotoFromExternal(myBitmap, rotationAngle);
            }
        }
    }

    fun getBitmapFromPath(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                          mTicketNumber: String?) {
        this.context = context
        this.mFrom = mFrom
        this.mLabel = mLabel
        this.mTicketNumber = mTicketNumber
        isOnCreate = false
//        getCitationDataFromDB()
        SetContextPrinterActivity()
        val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
        this.imgFilePath = imgFilePath
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

    fun getCitationDataFromDB()
    {
//        mIssuranceModel = mDb?.dbDAO?.getCitationWithTicket(mTicketNumber)
    }
    fun getBitmapFromPath(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                          mTicketNumber: String?, mAmount: String?, mErrorUploading: String?) {
        this.context = context
        this.mFrom = mFrom
        this.mLabel = mLabel
        this.mTicketNumber = mTicketNumber
        this.mAmount = mAmount
        this.isErrorUploading = mErrorUploading!!
        isOnCreate = false
        this.imgFilePath = imgFilePath
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

//        if (true) {
         if (AppUtils.getPrinterSetting() == Constants.SETTING_VALUE_PRINTER){
            printRotatedPhotoFromExternal(myBitmap, rotationAngle)
        }else {
            starPrinterInit()
        }
    }

    fun getBitmapFromPath(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                          mTicketNumber: String?, mAmount: String?, vehicleList:ArrayList<VehicleListModel>,
                          mPrintLayoutTitle: Array<String?>) {
        this.context = context
        this.mFrom = mFrom
        this.mLabel = mLabel
        this.mTicketNumber = mTicketNumber
        this.mAmount = mAmount
        this.mVehicleList = vehicleList
        isOnCreate = false
        this.imgFilePath = imgFilePath
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true
    }


/**
     * OnItemSelectedListener for angleSpinner
     */

    inner class MyOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            rotationAngle = parent.getItemAtPosition(pos).toString().toInt()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Do nothing.
        }
    }


/**
     * OnItemSelectedListener for imageSelectionSpinner
     */

    inner class OnItemsSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View,
            position: Int,
            id: Long
        ) {
            imageSelectionSpinner!!.setSelection(0)
            if (position == 1) {
                getPhotoFromCamera()
            } else if (position == 2) {
                getPhotosFromGallery()
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }


/**
     * Intents to make a call when image is captured using camera
     */

    private fun getPhotoFromCamera() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = File(Environment.getExternalStorageDirectory(), "tempPic.jpg")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
        startActivityForResult(intent, TAKE_PICTURE)
    }


/**
     * Intents to make a call when photos are selected from gallery
     */

    private fun getPhotosFromGallery() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICTURE_FROM_GALLERY)
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
            b = drawTextToBitmap(bitmap!!, mLabel!!)
        }
        printPhotoFromExternal(b)
//        printDataFromCommand(b)
    }

    private fun printPhotoFromExternal(bitmap: Bitmap?) {
        Thread(object : Runnable {
            override fun run() {
                try {
                    getAndSaveSettings()
                    Looper.prepare()
                    helper.showLoadingDialog("Sending image to printer")
                    val connection: Connection = getZebraPrinterConn()
                    val printQty: Int = 1 //or whatever number you want
                    connection.open()
//                    connection.write("! U1 do device.restore_defaults display\r\n".toByteArray())
//                    connection.write("! U1 do device.restore_defaults power\r\n".toByteArray())
//                    connection.write("! U1 do device.reset \r\n".toByteArray())

                    //with SETFF command we add 50 millimiters of margin at the end of the the print (without this the print is wasting a lot of paper)
//ref https://km.zebra.com/kb/index?page=forums&topic=021407fb4efb3012e55595f77007e8a
//                    connection.write("! U1 setvar \"device.languages\" \"CPCL\"\r\n".getBytes());
//                    connection.write("! U1 JOURNAL\r\n! U1 SETFF 50 2\r\n".getBytes());
                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,ignoreCase = true)) {
                        connection.write("! U1 FORM\r\n! U1 SETFF 5 4\r\n".toByteArray()) // change1
                    } else if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PARK, ignoreCase = true)) {
                        connection.write("! U1 FORM\r\n! U1 SETFF 35 4\r\n".toByteArray()) // change1
                    } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL,ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,ignoreCase = true)) {
                        connection.write("! U1 FORM\r\n! U1 SETFF 10 4\r\n".toByteArray()) // change1
                    } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH,ignoreCase = true)) {
                        connection.write("! U1 FORM\r\n! U1 SETFF 10 4\r\n".toByteArray()) // change1
                    } else {
//                        connection.write("! U1 FORM\r\n! U1 SETFF 15 4\r\n".getBytes()); //TODO
                        connection.write("! U1 FORM\r\n! U1 SETFF 10 2\r\n".toByteArray())
                        //                          runOnUiThread(new Runnable() {
                    }
//                                        connection.write("! U1 FORM\r\n".getBytes());
//                                        connection.write("! U1 setvar input.capture run);

                    val printer: ZebraPrinter =
                        ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, connection)
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
                    val newBitmapHeight: Int = bitmapToPrint1.height + 5
                    //get the new bitmap and add 20 pixel more of margin
//                    if(BuildConfig.FLAVOR.equalsIgnoreCase(Constants.FLAVOUR_TYPE_GLENDALE")) {
//                          newBitmapHeight = bitmapToPrint1.getHeight() + 5;
//                    }else {
//                          newBitmapHeight = bitmapToPrint1.getHeight() + 5;
//                    }
//create the Zebra object with the new Bitmap
                    val zebraImageToPrint: ZebraImageAndroid = ZebraImageAndroid(bitmapToPrint1)
                    //the image is sent to the printer and stored in R: folder
                    printer.storeImage("R:TEMP.PCX", zebraImageToPrint, -1, -1)

/*
                    val cpclConfigLabel = "! 0 200 200 210 1\r\n" //"
                    + "ON-FEED IGNORE\r\n"
                    + "BARCODE 128 1 1 50 150 10 1234567455\r\n"
                    + "PRINT\r\n";*//*

                    */
/**
                     * {command} {type} {width} {ratio} {height} {x} {y} {data} = barcode
                     * //create the print commands string
                     * {command} {font} {size} {x} {y} {data} = text
                     *
                     * QRCODE
                     * {command} {type} {x} {y} [M n] [U n]
                     * {data}
                     * <ENDQR
                     * **/

                    val PHILA: String = "     "

                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)) {
                        val printString: String =
                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
                                    + "NO-PACE" + "\r\n"
//                                        +"SETMAG 0 0\r\n"
                                    +"SETSP 1\r\n"
//                                        + "TEXT90 2 10 534 594 "+PHILA+   "  \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
                                    + "TEXT90 2 11 543 424 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                        + "BARCODE 128 1 1 52 186 1110 " + mTicketNumber + "\r\n"
                                    + "BARCODE 128 1 1 60 186 1100 " + mTicketNumber + "\r\n"
                                    + "BAR-SENSE" + "\r\n"
                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
                                    + "FORM" + "\r\n"
                                    + "PRINT" + "\r\n") //print
                        connection.write(printString.toByteArray())
                    } else if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)) {
                        val printString: String =
                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
                                    + "NO-PACE" + "\r\n"
//                                        +"SETMAG 0 0\r\n"
                                    +"SETSP 1\r\n"
//                                        + "TEXT90 2 10 534 594 "+PHILA+   "  \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
                                    + "TEXT90 2 11 543 444 \r" + mTicketNumber + "\r  " + "\r" + mAmount + "\r\n"
//                                        + "BARCODE 128 1 1 52 186 1110 " + mTicketNumber + "\r\n"
//                                        + "BARCODE 128 1 1 60 186 1100 " + mTicketNumber + "\r\n"
                                    + "BAR-SENSE" + "\r\n"
                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
                                    + "FORM" + "\r\n"
                                    + "PRINT" + "\r\n") //print
                        connection.write(printString.toByteArray())
                    } else {

                        val printString: String =
                            ("! 0 200 200 " + newBitmapHeight + " " + printQty + "\r\n" //set the height of the bitmap and the quantity to print
                                    + "PW " + (ZEBRA_WIDTH.toInt()) + "\r\n" //MAX_PRINT_WIDTH
                                    + "TONE 125" + "\r\n" //print intensity tone 0-200
                                    + "CONTRAST 3" + "\r\n" //0 =  Default  1 = Medium 2 = Dark 3 = Very Dark
                                    + "SPEED 1" + "\r\n" //print speed (less = more accurate) 1 2.5cm/s | 2 - 5cm/s | 3 - 7.6cm/s
                                    + "ON-FEED FEED" + "\r\n" //enable reprint on FEED button press
                                    + "NO-PACE" + "\r\n"
//                                        + "B QR 186 5 M 2 U 5" +"\r\n"
//                                        + "MA,QR code" + mTicketNumber + "\r\n"
//                                        + "ENDQR" + "\r\n"
                                    + "BAR-SENSE" + "\r\n"
                                    + "PCX 5 0 !<TEMP.PCX" + "\r\n" //get the image we stored before in the printer
                                    + "FORM" + "\r\n"
                                    + "PRINT" + "\r\n") //print
                        //send the commands to the printer, the image will be printed now
                        connection.write(printString.toByteArray())
                    }

//delete the image at the end to prevent printer memory sutaration
                    connection.write(("! U1 do \"file.delete\" \"R:TEMP.PCX\"\r\n").toByteArray())
                    connection.write("! U1 FORM\r\n! U1 SETFF 20000 8\r\n".toByteArray()) // for eye sense mark
//
//close the connection with the printer
                    connection.close()
                    //recycle the bitmap
                    bitmapToPrint1.recycle()
                    if (file != null) {
                        file!!.delete()
                        file = null
                    }
                } catch (e: ConnectionException) {
                    helper.showErrorDialogOnGuiThread(e.message)
                } catch (e: ZebraIllegalArgumentException) {
                    helper.showErrorDialogOnGuiThread(e.message)
                } catch (e: Exception) {
                    helper.showErrorDialogOnGuiThread(e.message)
                } finally {
                    printCallBack!!.onActionSuccess(isErrorUploading)
//                    helper.dismissLoadingDialog()
//                    Looper.myLooper()!!.quit()
                }
            }
        }).start()
    }


    private fun toggleEditField(editText: EditText?, set: Boolean) {

/*
         * Note: Disabled EditText fields may still get focus by some other means, and allow text input.
         *       See http://code.google.com/p/android/issues/detail?id=2771
         */

        editText!!.isEnabled = set
        editText.isFocusable = set
        editText.isFocusableInTouchMode = set
    }

    private fun isBluetoothSelected(): Boolean {
        return btRadioButton!!.isChecked
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
//        SettingsHelper.saveBluetoothAddress(this@PrinterActivity, getMacAddressFieldText())
//        SettingsHelper.saveIp(this@PrinterActivity, getTcpAddress())
//        SettingsHelper.savePort(this@PrinterActivity, tcpPortNumber())
    }

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
            paint.textSize = (11 * 2).toFloat()
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
                canvas.drawText((mText), (x - 190).toFloat(),
                    printLabelHeight(bitmap.height).toFloat(), paint)
            } else {
                canvas.drawText((mText), (x - 200).toFloat(),
                    printLabelHeight(bitmap.height).toFloat(), paint)
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return bitmap
        }
    }

    private fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var image: Bitmap = image
        if (maxHeight > 0 && maxWidth > 0) {
            val width: Int = image.width
            val height: Int = image.height
            val ratioBitmap: Float = width.toFloat() / height.toFloat()
            val ratioMax: Float = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth: Int = maxWidth
            var finalHeight: Int = maxHeight
            if (ratioMax > 1) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            return image
        } else {
            return image
        }
    }

    companion object {
        private val bluetoothAddressKey: String = "ZEBRA_DEMO_BLUETOOTH_ADDRESS"
        private val tcpAddressKey: String = "ZEBRA_DEMO_TCP_ADDRESS"
        private val tcpPortKey: String = "ZEBRA_DEMO_TCP_PORT"
        private val PREFS_NAME: String = "OurSavedAddress"
        private val TAKE_PICTURE: Int = 1
        private val PICTURE_FROM_GALLERY: Int = 2
        private var file: File? = null
    }



    /**
     * STAR PRINTER CODE
     */


    private fun starPrinterInit() {
        val settingManager = PrinterSettingManager(this)
        addPrinterInfo(settingManager.printerSettingsList)
    }

    private fun addPrinterInfo(settingsList: List<PrinterSettings>) {
//        if (settingsList.size() == 0) {

//        if (settingsList.size() == 0) {
        val mainTextList1: List<TextInfoKotlin>  =  ArrayList<TextInfoKotlin>()
//        val mainTextList: List<TextInfo> = java.util.ArrayList()
//        val mainTextList: List<com.parkloyalty.lpr.scan.startprinterfull.TextInfo> = ArrayList<com.parkloyalty.lpr.scan.startprinterfull.TextInfo>()
        Handler().postDelayed({
            //                    if (settingsList!=null && settingsList.size() == 0) {
            val searchTask = SearchTask()
            searchTask.execute(PrinterSettingConstant.IF_TYPE_BLUETOOTH)
            //                    }
        }, 500)

    }



/**
     * Printer search task.
     */

inner class SearchTask internal constructor() :
    AsyncTask<String?, Void?, Void?>() {
    private var mPortList: List<PortInfo>? = null
    override fun doInBackground(vararg interfaceType: String?): Void? {
        mPortList = try {
            StarIOPort.searchPrinter(interfaceType[0], this@SwitchPrinterActivity)
        } catch (e: StarIOPortException) {
            java.util.ArrayList<PortInfo>()
        } catch (e: SecurityException) {
            java.util.ArrayList<PortInfo>()
        }
        return null
    }

    override fun onPostExecute(doNotUse: Void?) {
        if (mPortList != null && mPortList!!.size== 0 && !isOnCreate) {
            Toast.makeText(
                this@SwitchPrinterActivity,
                getString(R.string.star_printer_error),
                Toast.LENGTH_SHORT
            ).show()
            printCallBack?.onActionSuccess(isErrorUploading)
        } else {
            for (info in mPortList!!) {
                addItem(info)
            }
        }
    }
}

    private fun addItem(info: PortInfo) {
        val textList: MutableList<TextInfoKotlin> = java.util.ArrayList()
        val imgList: MutableList<ImgInfoKotlin> = java.util.ArrayList()
        val modelName: String
        val portName: String
        val macAddress: String

        // --- Bluetooth ---
        // It can communication used device name(Ex.BT:Star Micronics) at bluetooth.
        // If android device has paired two same name device, can't choose destination target.
        // If used Mac Address(Ex. BT:00:12:3f:XX:XX:XX) at Bluetooth, can choose destination target.
        if (info.portName.startsWith(PrinterSettingConstant.IF_TYPE_BLUETOOTH)) {
            modelName = info.portName.substring(PrinterSettingConstant.IF_TYPE_BLUETOOTH.length)
            portName = PrinterSettingConstant.IF_TYPE_BLUETOOTH + info.macAddress
            macAddress = info.macAddress
        } else {
            modelName = info.modelName
            portName = info.portName
            macAddress = info.macAddress
        }
        textList.add(TextInfoKotlin(modelName, R.id.modelNameTextView))
        textList.add(TextInfoKotlin(portName, R.id.portNameTextView))
        if (info.portName.startsWith(PrinterSettingConstant.IF_TYPE_ETHERNET)
            || info.portName.startsWith(PrinterSettingConstant.IF_TYPE_BLUETOOTH)
        ) {
            textList.add(TextInfoKotlin("($macAddress)", R.id.macAddressTextView))
        }
        val settingManager = PrinterSettingManager(this@SwitchPrinterActivity)
        val settings = settingManager.printerSettings
        if (settings != null && settings.portName == portName) {
            imgList.add(ImgInfoKotlin(R.drawable.ic_app_name, R.id.checkedIconImageView))
        } else {
            imgList.add(ImgInfoKotlin(R.drawable.ic_app_name, R.id.checkedIconImageView))
        }
        val portInfoList: List<TextInfoKotlin> = textList
        for (portInfo in portInfoList) {
            when (portInfo.getTextResourceID()) {
                R.id.modelNameTextView -> mModelName = portInfo.getText()!!.toString()
                R.id.portNameTextView -> mPortName = portInfo.getText()!!.toString()
                R.id.macAddressTextView -> {
                    mMacAddress = portInfo.getText()!!.toString()
                    if (mMacAddress.startsWith("(") && mMacAddress.endsWith(")")) {
                        mMacAddress = mMacAddress.substring(1, mMacAddress.length - 1)
                    }
                }
            }
        }
        val model = ModelCapability.getModel(mModelName)
        if (model == ModelCapability.NONE) {
//            ModelSelectDialogFragment dialog = ModelSelectDialogFragment.newInstance(MODEL_SELECT_DIALOG_0);
//            dialog.show(getChildFragmentManager());
        } else {
//            ModelConfirmDialogFragment dialog = ModelConfirmDialogFragment.newInstance(MODEL_CONFIRM_DIALOG, model);
//            dialog.show(getChildFragmentManager());
            registerPrinter()
        }
    }



/**
     * Register printer information to SharedPreference.
     */

    private fun registerPrinter() {
        val settingManager = PrinterSettingManager(this@SwitchPrinterActivity)
        settingManager.storePrinterSettings(
            0,
            PrinterSettings(
                mModelIndex, mPortName, mPortSettings, mMacAddress, mModelName,
                false, PrinterSettingConstant.PAPER_SIZE_THREE_INCH
            )
        )
        //        Toast.makeText(this,mPortName+" "+imgFilePath.exists(),Toast.LENGTH_SHORT).show();
        if (!isOnCreate) {
            if (!mPortName.isEmpty()) {
                if (imgFilePath != null && imgFilePath!!.exists()) {
                    sendPrinterBitmap(imgFilePath!!, this@SwitchPrinterActivity, mFrom, mLabel)
                }
                else {
                    Toast.makeText(this, getString(R.string.star_printer_error), Toast.LENGTH_SHORT)
                        .show()
                    printCallBack!!.onActionSuccess(isErrorUploading)
                }
            } else {
                Toast.makeText(this, getString(R.string.star_printer_error), Toast.LENGTH_SHORT)
                    .show()
                printCallBack!!.onActionSuccess(isErrorUploading)
            }
        }
    }


    private fun drawTextToBitmapStar(bitmap: Bitmap, mText: String): Bitmap? {
        var bitmap = bitmap
        return try {
            var bitmapConfig = bitmap.config
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true)
            val canvas = Canvas(bitmap)
            // new antialised Paint
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            // text color - #3D3D3D
            paint.color = Color.rgb(0, 0, 0)
            // text size in pixels
            paint.setTextSize((10 * 2) .toFloat())
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.BLACK)

            //            paint.getTypeface(context.getResources().getFont(R.font.sf_pro_text_semibold));
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    paint.typeface =
                        Typeface.create(
                            resources.getFont(R.font.sf_pro_text_semibold),
                            Typeface.NORMAL
                        )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            // draw text to the Canvas center
            val bounds = Rect()
            paint.getTextBounds(mText, 0, mText.length, bounds)
            val x = bitmap.width
            if (mText.equals("R", ignoreCase = true)) {
                canvas.drawText(
                    mText,
                    (x - 190).toFloat(),
                    printLabelHeight(bitmap.height).toFloat(),
                    paint
                )
            } else {
                canvas.drawText(
                    mText,
                    (x - 200).toFloat(),
                    printLabelHeight(bitmap.height).toFloat(),
                    paint
                )
                //                canvas.drawText(mText, x - 200, AppUtils.printLabelHeight().toFloat(), paint);
            }
            bitmap
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            bitmap
        }
    }


    private fun printLabelHeight(height: Int): Int {
        try {
            return if (BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_GLENDALE||
                BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_GLENDALE_POLICE||
                BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_LAMETRO||
                BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_CORPUSCHRISTI||
                BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_LAZLB||
                BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_WESTCHESTER||
                BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_BURBANK) {
                (height * 0.16).toInt()
            } else if (BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_RISE_TEK_OKC||
                BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_CITY_VIRGINIA) {
                (height * 0.13).toInt()
            } else if (BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_CARTA) {
                (height * 0.29).toInt()
            } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)){
                (height * 0.1).toInt()
            }else {
                (height * 0.15).toInt()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 130
    }

    fun sendPrinterBitmap(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?) {
        val imgPath = Uri.fromFile(imgFilePath.absoluteFile)
        var myBitmap: Bitmap? = null
        try {
//            mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
            myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgPath)
            mBitmap = if (mLabel != null && !mLabel.isEmpty()) {
                drawTextToBitmap(myBitmap, mLabel)
            } else {
                myBitmap
            }
            print(8)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun print(selectedIndex: Int) {
//        mProgressDialog.show();
        val commands: ByteArray
        val settingManager = PrinterSettingManager(this@SwitchPrinterActivity)
        val settings = settingManager.printerSettings
        val emulation = ModelCapability.getEmulation(settings.modelIndex)
        val paperSize = settings.paperSize
        commands = when (selectedIndex) {
            8 -> if (mBitmap != null) {
                PrinterFunctions.createRasterData(emulation, mBitmap, paperSize, true)
            } else {
                ByteArray(0)
            }
            else -> if (mBitmap != null) {
                PrinterFunctions.createRasterData(emulation, mBitmap, paperSize, true)
            } else {
                ByteArray(0)
            }
        }
        Communication.sendCommands(
            this,
            commands,
            settings.portName,
            settings.portSettings,
            10000,
            30000,
            this@SwitchPrinterActivity,
            mCallback
        ) // 10000mS!!!

    }

    private val mCallback: Communication.SendCallback = object : Communication.SendCallback {
        override fun onStatus(communicationResult: Communication.CommunicationResult) {
//            if (!mIsForeground) {
//                return;
//            }

//            if (mProgressDialog != null) {
//                mProgressDialog.dismiss();
//            }
            printCallBack!!.onActionSuccess(isErrorUploading)

//            Toast.makeText(StarPrinterActivity.this, "Print bitmap success", Toast.LENGTH_SHORT).show();
        }
    }


    private fun setScreenResolution() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        val topSpace = width * 0.22
    }


}