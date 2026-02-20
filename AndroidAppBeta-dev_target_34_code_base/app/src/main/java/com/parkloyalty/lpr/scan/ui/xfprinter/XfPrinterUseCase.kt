package com.parkloyalty.lpr.scan.ui.xfprinter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.Writer
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.XF_PRINTER_BARCODE_HEIGHT
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.ioScope
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.mainScope
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.LogUtil
import com.twotechnologies.n5library.N5Information
import com.twotechnologies.n5library.N5Library
import com.twotechnologies.n5library.N5ReadyListener
import com.twotechnologies.n5library.printer.Code128CodeSets
import com.twotechnologies.n5library.printer.PrtActionRequest
import com.twotechnologies.n5library.printer.PrtBarcodeRequest
import com.twotechnologies.n5library.printer.PrtCompleteListener
import com.twotechnologies.n5library.printer.PrtContrastLevel
import com.twotechnologies.n5library.printer.PrtFormatting
import com.twotechnologies.n5library.printer.PrtSeekRequest
import com.twotechnologies.n5library.printer.PrtStatus
import com.twotechnologies.n5library.printer.PrtStatusChangeListener
import com.twotechnologies.n5library.printer.PrtTextStream
import com.twotechnologies.n5library.topology.DeviceDescriptor
import com.twotechnologies.xfexample.EventSupport.ListenerCallback
import com.twotechnologies.xfexample.EventSupport.ServiceEvents
import com.twotechnologies.xfexample.EventSupport.XFActivityCoroutine
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Hashtable

class XfPrinterUseCase(mContext: Context) : ListenerCallback, DefaultLifecycleObserver {

    private var printCallBack: PrintInterface? = null

    //This variable is used to define what print type we have to use for XF Print
//    private var isBitmapPrint = true

//    private var mContext: Context? = null
    private var titleBusy = 0
    //    private var barContrast: SeekBar? = null
    private var mLabel: String? = null
//    private var screenHeight = 0
    private var isErrorUploading: String = ""
    private var settingsList: List<DatasetResponse>? = null
    private var topSetFF = "2"
    private var bottomSetFF = "255"
    private var printintByCMD = false
    private var isOnCreate = true
    private var mContext : Context? = null


    private val contrastSetting: PrtContrastLevel
        //***********************************************************************
        get() = PrtContrastLevel.fromInt(8)

    private val contrastSettingForTextPrint: PrtContrastLevel
        //***********************************************************************
        get() = PrtContrastLevel.fromInt(9)
    //************************************************************************
//************************************************************************
    /** Listen for connection to N5 Platform Ready  */ //************************************************************************
    private val srvReady: N5ReadyListener = N5ReadyHandler(mContext!!, N5ReadyListener.ACTION_N5_READY)
    //************************************************************************
    /** Listen for lost connection to N5Service  */ //************************************************************************
    private val srvNotReady: N5ReadyListener =
        N5ReadyHandler(mContext, N5ReadyListener.ACTION_N5_NOT_READY)


    // -----------------------------------------------------------------------
    // Printer Listeners
    // -----------------------------------------------------------------------
    //***********************************************************************
    /** Print Complete  */ //***********************************************************************
    private val prtComplete: PrtCompleteListener = object : PrtCompleteListener(mContext) {
        override fun onReceive(context: Context, intent: Intent) {
            // restore normal title bar color
//            setPrtTitleColor(titleBar)

            // update elapsed time
//             getElapsedTime(intent).toString()
        }
    }

//***********************************************************************
    /** Print Status Change  */ //***********************************************************************
    private val prtStatusChangeListener: PrtStatusChangeListener =
        object : PrtStatusChangeListener(mContext) {
            override fun onReceive(context: Context, intent: Intent) {
                // When this listener is invoked, the printer
                // status has changed ...

                // update the status flags
//                updateStatusFlags()

                // cancel print job if there is a printer problem
                if (PrtStatus.isUnderTemp()
                    || PrtStatus.isPlatenOpen() // || PrtStatus.isOverTemperature()
                    || PrtStatus.isHardwareFault()
                ) {
                    // cancel print operations
                    PrtActionRequest.cancelPrinting()

                    // restore normal title bar color
//                    setPrtTitleColor(titleBar)
                }
            }
        }

    fun setPrintInterfaceCallback(printInterface : PrintInterface){
        LogUtil.printLog("==>Printer","set Printer callback")
        this.printCallBack = printInterface;
    }

    fun initialize(context: Context){
        mContext = context
        N5Information.isPrinterAvailable()
        mCoroutine = XFActivityCoroutine(mContext, this)
        mCoroutine!!.start()
        getPrintSetFFValueFromSettingFile()
        //updateListAndRegisterPrinter()
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
                            } else if (settingsList!![i].type.equals("PRINTER_BOTTOM_SETFF", ignoreCase = true)) {
                                bottomSetFF = settingsList!![i].mValue.toString()
                            }else if (settingsList!![i].type.equals("PRINTINGBY", ignoreCase = true) &&
                                settingsList!![i].mValue.equals("CMD", ignoreCase = true) ) {
                                printintByCMD = true
                            }
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun printerStatus(){
        var deviceDescriptor = N5Information.getDeviceDescriptor(0)
        makeConnectionString(deviceDescriptor)
        makeBluetoothInfoString(deviceDescriptor)
        N5Information.getDeviceDescriptor(1)
        titleBusy = ContextCompat.getColor(mContext!!, R.color.titleBusy)
    }

    private fun makeConnectionString(dd: DeviceDescriptor): String {
        var connection = "Connection Info: "
        var bIsConnected = false
        if (dd.isUSBConnected) {
            connection += " USB "
            bIsConnected = true
        }
        if (dd.isBluetoothConnected) {
            connection += " Bluetooth "
            bIsConnected = true
        }
        connection += if (!bIsConnected) " Not Connected" else " -" + dd.getRevision(
            DeviceDescriptor.Revisions.UGC)
        return connection
    }

    private fun makeBluetoothInfoString(deviceDescriptor: DeviceDescriptor): String {
        var bluetoothInfo = "Bluetooth: "
        if (deviceDescriptor.bTMAC != null) {
            bluetoothInfo += "2T" + deviceDescriptor.bTMAC
            if (deviceDescriptor.isBTFirmwareVersionLoaded) {
                bluetoothInfo += " / " + deviceDescriptor.getmBluetoothVersion()
            }
        } else {
            bluetoothInfo += "N/A"
        }
        return bluetoothInfo
    }

    fun printDailySummery(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                          mTicketNumber: String?,printerCommand : StringBuilder) {
        this.mLabel = mLabel
        SetContextPrinterActivity()
        if(false) {
            printTextToPrinter(getPrintCommentStatementsSummay(printerCommand.toString()),mTicketNumber)
        }else {
            val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
            var myBitmap: Bitmap? = null
            var paddedBitmap:Bitmap? = null
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(mContext?.contentResolver, imgPath)
                val originalBitmap: Bitmap = myBitmap
//                val newWidth = 700
//                val newHeight = 900
//
//                scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

                val targetWidth = 520
                val aspectRatio = originalBitmap.height.toFloat() / originalBitmap.width
                val targetHeight = (targetWidth * aspectRatio).toInt()

                val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
                 paddedBitmap = addTopPaddingToBitmap(scaledBitmap, 100) // adds 50px white space to the top

            } catch (e: FileNotFoundException) {
                e.message
            } catch (e: IOException) {
                e.message
            }
            printRotatedPhotoFromExternal(paddedBitmap, 180)
        }
    }
    fun mPrintFacsimileImage(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                          mTicketNumber: String?, mAmount: String?, mState: String?, mLprNumber: String?
                          , mErrorUploading: String?, printerCommand : StringBuilder) {

        this.isErrorUploading = mErrorUploading!!
        SetContextPrinterActivity()
        if(printintByCMD) {
            printTextToPrinter(getPrintCommentStatements(printerCommand.toString()),mTicketNumber)
        }else {
            this.mLabel = mLabel
            val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
            var myBitmap: Bitmap? = null
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(mContext?.contentResolver, imgPath)
            } catch (e: FileNotFoundException) {
                e.message
            } catch (e: IOException) {
                e.message
            }
            printRotatedPhotoFromExternal(myBitmap, 180)
        }
    }

    fun mPrintDownloadFacsimileImage(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
                               mTicketNumber: String?, mAmount: String?, mState: String?, mLprNumber: String?
                               , mErrorUploading:String?) {
//        mContext = context
        this.isErrorUploading = mErrorUploading!!
        this.mLabel = mLabel

        SetContextPrinterActivity()
        if(printintByCMD) {
            printTextToPrinter(getPrintCommentStatements(AppUtils.printQueryStringBuilder.toString()),mTicketNumber)

        }else {
            val imgPath: Uri = Uri.fromFile((imgFilePath.absoluteFile))
            var myBitmap: Bitmap? = null
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(mContext?.contentResolver, imgPath)
            } catch (e: FileNotFoundException) {
                e.message
            } catch (e: IOException) {
                e.message
            }
            printRotatedPhotoFromExternal(myBitmap, 180)
          }
    }

    fun printerFeedButton ()
    {
        try {
            GlobalScope.launch {
                // clear elapsed timer
                //PrtActionRequest.resetElapsedTime();

                // send formfeed
//            PrtTextStream.formfeed()

//            PrtActionRequest.forwardFeed(255)

                PrtSeekRequest.forwardSeek(255)
                PrtTextStream.formfeed()
                delay(100L)
                PrtSeekRequest.forwardSeek(255)
                delay(100L)
                PrtSeekRequest.forwardSeek(255)
                delay(100L)
                PrtSeekRequest.forwardSeek(255)
                delay(100L)
                PrtSeekRequest.forwardSeek(255)
                delay(100L)
                PrtSeekRequest.forwardSeek(255)
                delay(100L)
                PrtSeekRequest.forwardSeek(255)

                // flush data to printer
                PrtTextStream.flush()

                // refresh printer status
                PrtActionRequest.refreshStatus()

//            val stream = PrtTextStream()
//
//            try {
//                PrtTextStream.open()
//
//
//                // Simulate feed: print empty lines
//                for (i in 0..55) {
//                    stream.println("") // each println() = 1 line feed
//                }
//
//                stream.close()
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }

            }
        } catch (e: IOException) {
            Log.d("XFExample", "print failed")
        }
    }
    private fun SetContextPrinterActivity() {
        //initializing the callback object from the constructor
        printCallBack = mContext as PrintInterface?
    }

    private fun printRotatedPhotoFromExternal(bitmap: Bitmap?, rotationAngle: Int) {
        try {
            var b: Bitmap? = bitmap
            if(!mLabel!!.isEmpty()) {
                b = drawTextToBitmap(bitmap, mLabel)
            }
            ImageDemo().printBitmap(mContext, contrastSetting,b,topSetFF.toInt(),bottomSetFF.toInt())
//        printDemo(mContext, contrastSetting,b)

            // refresh printer status
            PrtActionRequest.refreshStatus()

            printCallBack!!.onActionSuccess(isErrorUploading)
        } catch (e: Exception) {
            e.printStackTrace()
            printCallBack!!.onActionSuccess(isErrorUploading)
        }
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
                        mContext!!.resources.getFont(R.font.timesnewromanpsmtregular),
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
                canvas.drawText((mText), (x - 230).toFloat(),
                    AppUtils.printLabelHeight(bitmap.height).toFloat(), paint)
            } else {
                canvas.drawText((mText), (x - 240).toFloat(),
                    AppUtils.printLabelHeight(bitmap.height).toFloat(), paint)
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return bitmap
        }
    }

//    open fun setScreenResolution() {
//        val displayMetrics = DisplayMetrics()
//        mContext?.windowManager.defaultDisplay.getMetrics(displayMetrics)
//        screenHeight = displayMetrics.heightPixels
////        width = displayMetrics.widthPixels
////        val topSpace: Double = width * 0.22
//    }


    //************************************************************************
    /**
     * On Start Method
     */
    //************************************************************************
    override fun onStart(owner: LifecycleOwner) {

        mCoroutine!!.setListenerCallback(this)
        Log.d("XFExample", "onStart")

        // initialize N5 API
        N5Library.initialize(mContext)

        // Update Action Bar title
        try {
            val title = N5Information.getLibraryIdentity()
//            actionBar!!.subtitle = title
        } catch (expt: NullPointerException) {
            Log.d("XFExample", "Could not set action bar title")
        }

        // start listening for connection events
        srvReady.startListening()
        srvNotReady.startListening()

        // UGC key listener
//        actionKeyA.startListening()

        // start printer listeners
        prtComplete.startListening()
//        prtActionListener.startListening()
//        prtSeekListener.startListening()
        prtStatusChangeListener.startListening()

        // read up latest printer status
        PrtActionRequest.refreshStatus()
        super.onStart(owner)
    }
    //************************************************************************
    /**
     * On Stop Method
     */
    //************************************************************************
    override fun onStop(owner: LifecycleOwner) {
        Log.d("XFExample", "onStop")

        // stop printer listeners
        prtComplete.stopListening()
//        prtActionListener.stopListening()
//        prtSeekListener.stopListening()
        prtStatusChangeListener.stopListening()

        // stop UI updates
//        updateTimer.cancel()
//        actionKeyA.stopListening()
        // stop listening for connection events
        srvReady.stopListening()
        srvNotReady.stopListening()
        super.onStop(owner)
    }
    //************************************************************************
    /**
     * On Desctroy Method
     */
    //************************************************************************
    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("XFExample", "onDestroy")
        mCoroutine!!.destroy() // unbind library an stop listening
        // invoke super
        super.onDestroy(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
//        super.onResume()
        PrtActionRequest.refreshStatus()
        refreshValues()
        super.onResume(owner)
    }

    //**************************************************************************************************
    //   listenerEvent
    //     hmiQService listener events come to here via coroutine - see ServiceEvents enum
    //     the intent is directly from hmiQService - see the SDK documentation
    //**************************************************************************************************
    override fun listenerEvent(eEvent: ServiceEvents?) {
        when (eEvent!!) {
            ServiceEvents.PlatformConnect -> {}
            ServiceEvents.PlatformDisconnect -> {}
            ServiceEvents.KeyEvent -> TODO()
        }
    }

    //************************************************************************
    /** Common class to handle N5 Platform Ready  */ //************************************************************************
    private inner class N5ReadyHandler  //************************************************************
    /**
     * Constuctor
     */
    //************************************************************
        (context: Context?, s: String?) : N5ReadyListener(context, s) {
        //************************************************************
        /**
         * Handle Service connected event
         */
        //************************************************************
        override fun onReceive(context: Context, intent: Intent) {
            refreshValues()
        }
    }

    private fun refreshValues() {
        // update service connection
//        chkService!!.isChecked = N5Information.isServiceAvailable()


        // update printer status
        if (N5Information.isPlatformAvailable()) {
            // When platform becomes available,
            // get the current printer status values from the N5Library
            // these may be different than the app's current knowledge
            // if status changes while disconnected

            // show current print status
//            updateStatusFlags()

            // When platform becomes available,
            // prompt for update of print status
            // if status changes from current values
            // PrtStatusChangeListener will fire
            //xxPrtActionRequest.refreshStatus();
        }
        printerStatus()
//        updateLocalRemoteStatus() // use DeviceDescriptor (which contains device number)
//        runOnUiThread(uiUpdate)

    }
    companion object {
        @JvmField
        var mCoroutine: XFActivityCoroutine? = null
    }

    private fun printTextToPrinter(textToPrint: ArrayList<TextToPrintData>,ticketNumber : String?) {
        try {
            /**
             * This is to check formatted text for printer
             */
            if(LogUtil.isEnableAPILogs) {
                for (text in textToPrint) {
                    Log.i("PrinterLogTEXT_TO_PRINT:==>", "==========================")
                    Log.i("PrinterLogTEXT_TO_PRINT:==>", text.textToPrint)
                    Log.i("PrinterLogTEXT_TO_PRINT:==>", text.fontSize.nullSafety(FontSizeForCommandPrint.FONT_TITLE).toString())
                    Log.i("PrinterLogTEXT_TO_PRINT:==>", text.fontType.toString())
                    Log.i("PrinterLogTEXT_TO_PRINT:==>", "==========================")

                }
            }
            val barcodeValue = textToPrint.first().textToPrint

            // Reset parameters
            PrtActionRequest.resetPrinter()

            // Set contrast
            PrtActionRequest.setPrintContrast(contrastSettingForTextPrint)

            // clear elapsed timer
            PrtActionRequest.resetElapsedTime()
//--------------------------------------
            /* // Code 39
             PrtTextStream.write( "Code 39\n" );
             PrtTextStream.write( "*****************************\n" );
             PrtBarcodeRequest.code39( textToPrint.first().textToPrint, true, XF_PRINTER_BARCODE_HEIGHT);
             PrtTextStream.write( "*****************************\n" );
             PrtTextStream.newlines( 2 );

             //--------------------------------------
             // Code 128

             PrtTextStream.write( "Code 128\n" );
             PrtTextStream.write( "*****************************\n" );
             PrtBarcodeRequest.code128( textToPrint.first().textToPrint,
                 true,
                 XF_PRINTER_BARCODE_HEIGHT,
                 Code128CodeSets.CODE128_CODESET_B );
             PrtTextStream.write( "*****************************\n" );
             PrtTextStream.newlines( 2 );
 */
            //--------------------------------------
            // Codabar

//            PrtTextStream.write( "Codabar\n" );
//            PrtTextStream.write( "*****************************\n" );
//            PrtBarcodeRequest.Codabar(barcodeValue,
//                true,
//                XF_PRINTER_BARCODE_HEIGHT,
//                CodabarStartStop.PAIR_A_T );
//            PrtTextStream.write( "*****************************\n" );
//            PrtTextStream.newlines( 2 );

//            PrtTextStream.write("Demo")

//            PrtBarcodeRequest.code128(
//                textToPrint.first().textToPrint,
//                false,
//                XF_PRINTER_BARCODE_HEIGHT,
//                Code128CodeSets.CODE128_CODESET_B
//            )
            PrtTextStream.newlines(3)
            PrtTextStream.write( "." );
            PrtBarcodeRequest.code128(
                ticketNumber.nullSafety(),
                false,
                XF_PRINTER_BARCODE_HEIGHT,
                Code128CodeSets.CODE128_CODESET_B
            )
//            Log.i("PrinterLogTEXT_TO_BARCODE:==>", textToPrint.first().textToPrint)
//            PrtGraphics.printImage(getBarCodeBitmap(barcodeValue),
//                ImageScale.SCALE_ONE_TO_ONE,
//                ImageAlignment.IMAGE_CENTER)
//
//            PrtTextStream.newlines(1)
            runBlocking {
                textToPrint.forEachIndexed { index, data ->
                    if (index == 0) {

                    } else {
                        PrtFormatting.setEmphasize(false)
                        if (data.textToPrint == SectionType.NEW_LINE){
                            PrtTextStream.newlines(1)
//                        PrtTextStream.write("\n")
                        }else {
                            PrtFormatting.setFont(getFontToPrintFromServerFontCode(data.fontSize))
                            if (data.fontType == FontType.TYPE_BOLD) {
                                PrtFormatting.setEmphasize(true)
                            }
                            PrtTextStream.write(data.textToPrint.nullSafety())
//                    when(data.fontSize.nullSafety()){
//                        FontSizeForCommandPrint.FONT_HEADER -> {
////                            PrtFormatting.setFont( Fonts.SAN_SERIF_10_2_CPI)
//                            PrtFormatting.setFont( Fonts.SAN_SERIF_16_9_CPI)
//                            PrtFormatting.setDoubleSize(false)
//                            PrtFormatting.setEmphasize(false)
//                        }
//                        FontSizeForCommandPrint.FONT_SUBTITLE_WITH_BOLD -> {
//                            PrtFormatting.setFont( Fonts.COURIER_14_5_CPI)
//                            PrtFormatting.setDoubleSize(false)
//                            PrtFormatting.setEmphasize(true)
//                        }
//                        FontSizeForCommandPrint.FONT_SUBTITLE -> {
//                            PrtFormatting.setFont(Fonts.COURIER_14_5_CPI)
//                            PrtFormatting.setDoubleSize(false)
//                            PrtFormatting.setEmphasize(false)
//                        }
//                        else -> {
//                            //Log.i("PrinterLogTEXT_TO_PRINT:==>", "Normal: SAN_SERIF_20_3_CPI")
////                            PrtFormatting.setFont( Fonts.COURIER_14_5_CPI)
//                            PrtFormatting.setFont( Fonts.COURIER_15_6_CPI)
//                            PrtFormatting.setDoubleSize(false)
//                            PrtFormatting.setEmphasize(false)
//                        }
//                    }
                        }

                    }
                    delay(100)
                }

            }

            // flush data to printer
            PrtTextStream.flush()

            PrtActionRequest.forwardFeed(bottomSetFF.toInt())
//            PrtSeekRequest.forwardSeek(255);
//            PrtActions.FWD_SEEK

            // mark EOJ
            PrtActionRequest.markEOJ()
            printCallBack!!.onActionSuccess(isErrorUploading)
        } catch (e: IOException) {
            e.printStackTrace()
            printCallBack!!.onActionSuccess(isErrorUploading)
        }catch (e: Exception) {
            e.printStackTrace()
            printCallBack!!.onActionSuccess(isErrorUploading)
        }
    }

    fun getBarCodeBitmap(citation:String): Bitmap {
        val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel?>()
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        val codeWriter: Writer
        codeWriter = Code128Writer()
        var byteMatrix: BitMatrix = codeWriter.encode(citation, BarcodeFormat.CODE_128, 340, 44, hintMap)
        val width = byteMatrix.width
        val height = byteMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (i in 0 until width) {
            for (j in 0 until height) {
                bitmap.setPixel(i, j, if (byteMatrix[i, j]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap;
    }

    fun addTopPaddingToBitmap(original: Bitmap, topPaddingPx: Int, paddingColor: Int = Color.WHITE): Bitmap {
        val newWidth = original.width
        val newHeight = original.height + topPaddingPx

        // Create a new bitmap with extra height
        val paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, original.config)

        // Create a canvas to draw onto the new bitmap
        val canvas = Canvas(paddedBitmap)

        // Fill the top padding area with the padding color
        val paint = Paint()
        paint.color = paddingColor
        canvas.drawRect(0f, 0f, newWidth.toFloat(), topPaddingPx.toFloat(), paint)

        // Draw the original bitmap below the padding
        canvas.drawBitmap(original, 0f, topPaddingPx.toFloat(), null)

        return paddedBitmap
    }
}