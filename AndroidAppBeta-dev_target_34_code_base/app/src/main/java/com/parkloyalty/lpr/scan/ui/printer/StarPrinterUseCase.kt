package com.parkloyalty.lpr.scan.ui.printer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.provider.MediaStore
import android.widget.Toast
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.startprinterfull.Communication
import com.parkloyalty.lpr.scan.startprinterfull.Communication.SendCallback
import com.parkloyalty.lpr.scan.startprinterfull.ImgInfoKotlin
import com.parkloyalty.lpr.scan.startprinterfull.ModelCapability
import com.parkloyalty.lpr.scan.startprinterfull.PrinterSettingConstant
import com.parkloyalty.lpr.scan.startprinterfull.PrinterSettingManager
import com.parkloyalty.lpr.scan.startprinterfull.PrinterSettings
import com.parkloyalty.lpr.scan.startprinterfull.TextInfoKotlin
import com.parkloyalty.lpr.scan.startprinterfull.functions.PrinterFunctions
import com.parkloyalty.lpr.scan.util.LogUtil
import com.starmicronics.stario.PortInfo
import com.starmicronics.stario.StarIOPort
import com.starmicronics.stario.StarIOPortException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class StarPrinterUseCase {
    private var printCallBack: PrintInterface? = null
    private var mBitmap: Bitmap? = null
    var imgFilePath: File? = null
    var mFrom: String = ""
    var mLabel: String = ""
    private val mModelIndex = 19
    private var mPortName = "test"
    private val mPortSettings = "Portable"
    private var mMacAddress = ""
    private var mModelName = ""
    private var isErrorUploading = ""
    private var context : Context? = null
    private var isOnCreate = true

    fun setPrintInterfaceCallback(printInterface : PrintInterface){
        LogUtil.printLog("==>Printer","set Printer callback")
        this.printCallBack = printInterface;
    }

    fun initialize(context: Context){
        this.context = context
        isOnCreate = true
        //updateListAndRegisterPrinter()
    }

    private fun updateListAndRegisterPrinter() {
        LogUtil.printLog("==>Printer","updateListAndRegisterPrinter")
        val settingManager = PrinterSettingManager(context)
        addPrinterInfo(settingManager.printerSettingsList)
    }

    private fun addPrinterInfo(settingsList: List<PrinterSettings>) {
        Handler().postDelayed({
            val searchTask: SearchTask = SearchTask()
            searchTask.execute(PrinterSettingConstant.IF_TYPE_BLUETOOTH)
        }, 500)
    }

    private fun print(selectedIndex: Int) {
        try {
            LogUtil.printLog("==>Printer","Print0")
            val commands: ByteArray

            val settingManager = PrinterSettingManager(context)
            val settings = settingManager.printerSettings

            if (settings != null) {
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

                LogUtil.printLog("==>Printer","Before Send Command")
                Communication.sendCommands(
                    this, commands, settings.portName, settings.portSettings, 10000, 30000,
                    context, mCallback
                ) // 10000mS!!!
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private val mCallback = SendCallback {
        LogUtil.printLog("==>Printer","SendCallBack")
        printCallBack?.onActionSuccess(isErrorUploading)
    }

    /**
     * Printer search task.
     */
    inner class SearchTask : AsyncTask<String?, Void?, Void?>() {
        private var mPortList: List<PortInfo>? = null

        override fun onPostExecute(doNotUse: Void?) {
            LogUtil.printLog("==>Printer","onPostExecute")
            if (mPortList != null && mPortList?.isEmpty().nullSafety() && !isOnCreate) {
                Toast.makeText(
                    context,
                    context?.getString(R.string.star_printer_error),
                    Toast.LENGTH_SHORT
                ).show()
                printCallBack?.onActionSuccess(isErrorUploading)
            } else {
                for (info in mPortList!!) {
                    addItem(info)
                }
            }
        }

        override fun doInBackground(vararg params: String?): Void? {
            LogUtil.printLog("==>Printer","doInBackground")
            mPortList = try {
                StarIOPort.searchPrinter(params[0], context)
            } catch (e: StarIOPortException) {
                ArrayList<PortInfo>()
            } catch (e: SecurityException) {
                ArrayList<PortInfo>()
            }

            return null
        }
    }

    private fun addItem(info: PortInfo) {
        val textList: MutableList<TextInfoKotlin> = ArrayList()
        val imgList: MutableList<ImgInfoKotlin> = ArrayList()

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

        val settingManager = PrinterSettingManager(context)
        val settings = settingManager.printerSettings

        if (settings != null && settings.portName == portName) {
            imgList.add(ImgInfoKotlin(R.drawable.ic_app_name, R.id.checkedIconImageView))
        } else {
            imgList.add(ImgInfoKotlin(R.drawable.ic_app_name, R.id.checkedIconImageView))
        }

        val portInfoList: List<TextInfoKotlin> = textList

        for (portInfo in portInfoList) {
            when (portInfo.getTextResourceID()) {
                R.id.modelNameTextView -> mModelName = portInfo.getText().nullSafety()
                R.id.portNameTextView -> mPortName = portInfo.getText().nullSafety()
                R.id.macAddressTextView -> {
                    mMacAddress = portInfo.getText().nullSafety()
                    if (mMacAddress.startsWith("(") && mMacAddress.endsWith(")")) {
                        mMacAddress = mMacAddress.substring(1, mMacAddress.length - 1)
                    }
                }
            }
        }
        val model = ModelCapability.getModel(mModelName)
        if (model == ModelCapability.NONE) {
            //Nothing To Implement
            LogUtil.printLog("==>Printer","Nothing to impl")
        } else {
            registerPrinter()
        }
    }

    /**
     * Register printer information to SharedPreference.
     */
    private fun registerPrinter() {
        LogUtil.printLog("==>Printer","Register Printer")
        val settingManager = PrinterSettingManager(context)

        settingManager.storePrinterSettings(
            0,
            PrinterSettings(
                mModelIndex, mPortName, mPortSettings, mMacAddress, mModelName,
                false, PrinterSettingConstant.PAPER_SIZE_THREE_INCH
            )
        )

        if (!isOnCreate) {
            if (!mPortName.isEmpty()) {
                if (imgFilePath != null && imgFilePath!!.exists()) {
                    LogUtil.printLog("==>Printer", "SendPrinterBitmap")
//                    sendPrinterBitmap(imgFilePath!!, context, mFrom, mLabel)
                }
            }

        } else {
            Toast.makeText(
                context,
                context?.getString(R.string.star_printer_error),
                Toast.LENGTH_SHORT
            )
                .show()
            printCallBack!!.onActionSuccess(isErrorUploading)
        }
    }

    private fun printLabelHeight(height: Int): Int {
        try {
            return if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,true) ||
                BuildConfig.FLAVOR.equals( Constants.FLAVOR_TYPE_GLENDALE_POLICE ,true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,true) ||
                BuildConfig.FLAVOR .equals( Constants.FLAVOR_TYPE_BURBANK,true) ||
                BuildConfig.FLAVOR .equals( Constants.FLAVOR_TYPE_WESTCHESTER,true) ||
                BuildConfig.FLAVOR .equals( Constants.FLAVOR_TYPE_LAZLB,true) ||
                BuildConfig.FLAVOR .equals( Constants.FLAVOR_TYPE_HILTONHEAD,true)
            ) {
                (height * 0.16).toInt()
            } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC,true)
            ) {
                (height * 0.11).toInt()
            } else if (BuildConfig.FLAVOR .equals( Constants.FLAVOR_TYPE_CITY_VIRGINIA,true)
            ) {
                (height * 0.13).toInt()
            } else if (BuildConfig.FLAVOR .equals( Constants.FLAVOR_TYPE_CARTA,true)) {
                (height * 0.29).toInt()
            } else {
                (height * 0.15).toInt()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 130
    }

    private fun drawTextToBitmap(bitmap: Bitmap, mText: String): Bitmap? {
        var bitmap = bitmap
        try {
            var bitmapConfig =
                bitmap.config
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
            paint.textSize = ((14 * 2) as Int).toFloat()
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.BLACK)

            try {
                paint.setTypeface(
                    Typeface.create(
                        context?.getResources()?.getFont(R.font.sf_pro_text_semibold),
                        Typeface.NORMAL
                    )
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            // draw text to the Canvas center
            val bounds = Rect()
            paint.getTextBounds(mText, 0, mText.length, bounds)
            val x = (bitmap.width)
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
            }
            return bitmap
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return bitmap
        }
    }

    /**
     * Call form officer activity
     */
    fun printDailySummery(
        imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
        mTicketNumber: String?
    ) {
        this.imgFilePath = imgFilePath
        this.mFrom = mFrom!!
        this.mLabel = mLabel!!
        isOnCreate = false
        updateListAndRegisterPrinter()
        sendPrinterBitmap(imgFilePath, context, mFrom, mLabel)
    }

    fun mPrintFacsimileImage(
        imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
        mTicketNumber: String?, mAmount: String?, state: String?, lprNumber: String?,
        mErrorUploading: String?, printerCommand: StringBuilder?
    ) {
        this.imgFilePath = imgFilePath
        this.mFrom = mFrom!!
        this.mLabel = mLabel!!
        this.isErrorUploading = mErrorUploading!!
        isOnCreate = false
        updateListAndRegisterPrinter()
        sendPrinterBitmap(imgFilePath, context, mFrom, mLabel)

    }

    fun mPrintDownloadFacsimileImage(
        imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?,
        mTicketNumber: String?, mAmount: String?, state: String?, lprNumber: String?,
        mErrorUploading: String?
    ) {
        this.imgFilePath = imgFilePath
        this.mFrom = mFrom!!
        this.mLabel = mLabel!!
        this.isErrorUploading = mErrorUploading!!
        isOnCreate = false
        updateListAndRegisterPrinter()
        sendPrinterBitmap(imgFilePath, context, mFrom, mLabel)
    }


    fun sendPrinterBitmap(imgFilePath: File, context: Context?, mFrom: String?, mLabel: String?) {
        val imgPath = Uri.fromFile((imgFilePath.absoluteFile))
        var myBitmap: Bitmap? = null
        try {
            myBitmap = MediaStore.Images.Media.getBitmap(context?.getContentResolver(), imgPath)
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
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
}