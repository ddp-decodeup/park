package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.isEmpty
import com.parkloyalty.lpr.scan.extensions.nullOrEmptySafety
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.spaceCountForHeight
import com.parkloyalty.lpr.scan.extensions.spaceCountForWidth
import com.parkloyalty.lpr.scan.extensions.splitTextToMultiline
import com.parkloyalty.lpr.scan.extensions.textAlignInCenter
import com.parkloyalty.lpr.scan.extensions.toFormatInt
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FLAVOR_TYPE_OXFORD
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.PRINT_LINE_HEIGHT
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.PRINT_SECTION_VIOLATION
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_BAR_CODE_HEIGHT
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_BAR_CODE_X
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_BAR_CODE_Y
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.boot.model.PrintBootNoticeModel
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.printer.PaperWidthBasedPrinterType
import com.parkloyalty.lpr.scan.ui.xfprinter.SectionType
import com.parkloyalty.lpr.scan.ui.xfprinter.TextAlignmentForCommandPrint
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.CUSTOM_PRINT_COMMAND_SEPARATOR
import com.parkloyalty.lpr.scan.util.AppUtils.Horizontal
import com.parkloyalty.lpr.scan.util.AppUtils.addYAxisToSet
import com.parkloyalty.lpr.scan.util.AppUtils.drawableElements
import com.parkloyalty.lpr.scan.util.AppUtils.getHeightBasedOnFont
import com.parkloyalty.lpr.scan.util.AppUtils.getScaledQRBitmapBasedOnCommandBitmapSize
import com.parkloyalty.lpr.scan.util.AppUtils.getYAxisBasedOnQRCodeHeight
import com.parkloyalty.lpr.scan.util.AppUtils.isSiteSupportCommandPrinting
import com.parkloyalty.lpr.scan.util.AppUtils.isYAxisHeight
import com.parkloyalty.lpr.scan.util.AppUtils.mOrleansBoxInitialYValue
import com.parkloyalty.lpr.scan.util.BitmapUtils.convertBitmapToBWUsingDefaultARGB
import com.parkloyalty.lpr.scan.util.FileUtil.getSignatureFileNameWithExt
import com.parkloyalty.lpr.scan.util.LogUtil.getPaperWidthBasedPrinterType
import com.parkloyalty.lpr.scan.util.commandprint.DrawableElement
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import java.io.File
import java.util.Locale


object ZebraCommandPrintUtils {
    private const val ZEBRA_PAPER_THREE_INCH = 576
    private const val ZEBRA_PAPER_FOUR_INCH = 832

    fun getZebraPrinterPaperWidthSize(): Int {
        return if (getPaperWidthBasedPrinterType() == PaperWidthBasedPrinterType.ZEBRA_FOUR_INCH_PRINTER) {
            ZEBRA_PAPER_FOUR_INCH
        } else {
            ZEBRA_PAPER_THREE_INCH
        }
    }

    var ZEBRA_WIDTH: Float = getZebraPrinterPaperWidthSize().toFloat()

    private var lineXAxisEnd: Int =
        if (getPaperWidthBasedPrinterType() == PaperWidthBasedPrinterType.ZEBRA_FOUR_INCH_PRINTER) {
            ZEBRA_PAPER_FOUR_INCH - 24
        } else {
            ZEBRA_PAPER_THREE_INCH - 16
        }

    private var lineWidthSize: Int =
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,ignoreCase = true)) {
            60
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)) {
            12
        } else {
            70
        }


    private var lineWidthSizeFacsimile: Float =
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)) {
            4f
        } else {
            10f
        }

    var boxXAxisEnd: Int =
        if (getPaperWidthBasedPrinterType() == PaperWidthBasedPrinterType.ZEBRA_FOUR_INCH_PRINTER) {
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ORLEANS_OLD,
                    ignoreCase = true
                )
            ) {
                ZEBRA_PAPER_FOUR_INCH - 18 //750
            } else if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BOSTON,
                    ignoreCase = true
                )
            ) {
                ZEBRA_PAPER_FOUR_INCH - 36 //732
            } else if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_OCEANCITY,
                    ignoreCase = true
                )
            ) {
                ZEBRA_PAPER_FOUR_INCH - 44 //728
            } else if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
                    ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_WINPARK_TX,
                    ignoreCase = true
                )
            ) {
                ZEBRA_PAPER_FOUR_INCH - 24
            } else {
                ZEBRA_PAPER_FOUR_INCH - 24
            }
        } else {
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ORLEANS_OLD,
                    ignoreCase = true
                )
            ) {
                ZEBRA_PAPER_THREE_INCH - 13 //563
            } else if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BOSTON,
                    ignoreCase = true
                )
            ) {
                ZEBRA_PAPER_THREE_INCH - 26 //550
            } else if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_OCEANCITY,
                    ignoreCase = true
                )
            ) {
                ZEBRA_PAPER_THREE_INCH - 31 //545
            } else {
                ZEBRA_PAPER_THREE_INCH - 11 //565
            }
        }



    const val ZEBRA_COMMAND_PRINT_HEIGHT: Float = 1100f

    const val SEPTA_TOW_NOTICE_HEADER_MARGIN = 80
    const val SEPTA_TOW_NOTICE_LINE_BREAKER_THICKNESS = 3
    const val SEPTA_TOW_NOTICE_DESC_MAX_LENGTH = 44

    //Fonts
    const val ZEBRA_PRINTER_FONT_HEADER = "TEXT 5 2"
    const val ZEBRA_PRINTER_FONT_EXTRA_SMALL_DESC = "TEXT 0 1"
    const val ZEBRA_PRINTER_FONT_SMALL_TITLE = "TEXT 7 0"
    const val ZEBRA_PRINTER_FONT_TITLE = "TEXT 5 0"

    private var barCodePrint = ""
    private var barCodeHeight = ""
    private var barCodeX = ""
    private var barCodeY = ""

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
    private var YBottomAddressLine1: Int = 0
    private var YBottomAddressLine2: Int = 0
    private var YBottomAddressLine3: Int = 0
    private var BottomAddressValue = ""
    private var BottomAddressValueArray = arrayOfNulls<String>(8)
    private var BottomAddress2ValueArray = arrayOfNulls<String>(5)
    private var BottomAddress3ValueArray = arrayOfNulls<String>(5)
    private var BottomAddress4ValueArray = arrayOfNulls<String>(8)
    private var BottomAddress5ValueArray = arrayOfNulls<String>(8)
    private var BottomAddress6ValueArray = arrayOfNulls<String>(8)
    private var BottomAddress7ValueArray = arrayOfNulls<String>(8)
    private var bottomAddress8ValueArray = arrayOfNulls<String>(8)
    private var bottomAddress9ValueArray = arrayOfNulls<String>(8)
    private var bottomAddress10ValueArray = arrayOfNulls<String>(8)
    private var bottomAddress11ValueArray = arrayOfNulls<String>(8)
    private var bottomAddress12ValueArray = arrayOfNulls<String>(8)
    private var FontBottomAddress = ""

    private var mCitationNumberValue = ""
    private var mCitationNumberLable = ""
    private var mCitationNumberX = "5"
    private var mCitationNumberY = "30"
    private var mCitationNumberFont = "1"

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


    private var XBottomAddress5 = "0"
    private var YBottomAddress5 = "0"
    private var FontBottomAddress5 = ""
    private var BottomAddressValue5 = ""

    private var XBottomAddress6 = "0"
    private var YBottomAddress6 = "0"
    private var FontBottomAddress6 = ""
    private var BottomAddressValue6 = ""

    private var XBottomAddress7 = "0"
    private var YBottomAddress7 = "0"
    private var FontBottomAddress7 = ""
    private var BottomAddressValue7 = ""

    private var xBottomAddress8 = "0"
    private var yBottomAddress8 = "0"
    private var fontBottomAddress8 = ""
    private var bottomAddressValue8 = ""

    private var xBottomAddress9 = "0"
    private var yBottomAddress9 = "0"
    private var fontBottomAddress9 = ""
    private var bottomAddressValue9 = ""

    private var xBottomAddress10 = "0"
    private var yBottomAddress10 = "0"
    private var fontBottomAddress10 = ""
    private var bottomAddressValue10 = ""

    private var xBottomAddress11 = "0"
    private var yBottomAddress11 = "0"
    private var fontBottomAddress11 = ""
    private var bottomAddressValue11 = ""

    private var xBottomAddress12 = "0"
    private var yBottomAddress12 = "0"
    private var fontBottomAddress12 = ""
    private var bottomAddressValue12 = ""


    private var XBottomSchedule = "0"
    private var YBottomSchedule = "0"
    private var FontBottomSchedule = ""
    private var BottomScheduleValue = ""
    private var mBottomAddressQuery = StringBuilder()

    private var bottomIncrementY:Int = 20

    fun isPrintLprImageInCmdPrint(): Boolean {
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true))
            return false
        else
            return false
    }

    fun isPrintLprImageInFacsimilePrint(mDB: AppDatabase?): Boolean {
        try {
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDB)
            if (settingsList != null && settingsList!!.isNotEmpty()) {
                for (i in settingsList!!.indices) {
                    if (settingsList!![i].type.equals(
                            "IS_PRINT_LPR_IMAGE_IN_FACSIMILE",
                            ignoreCase = true
                        )
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                    ) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false

//        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true))
//        return true
//        else
//        return false
    }

    fun lprImageHeightAdjustment(): Int {
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
        )
            return 600
        else if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)
        )
            return 700
        else
            return 600
    }

    /**
     * Function used to get EG CPCL command image to print.
     * You have to pass three input
     * @param myBitmap: Converted Black and White Image that you want to print
     * for black and white image you can use either option:
     * 1.) use function from BitmapUtils convertBitmapToBWUsingPixelToPixelConversion()
     * or
     * 2.) use function from BitmapUtils convertBitmapToBWUsingDefaultARGB()
     * @param xPosition : X position you want to position the image
     * @param yPosition : Y position you want to position the image
     * @return Command : It will return the expanded graphic command to append with your query
     */
    fun getExpandedGraphicImagePrintingCommandUsingCPCL(
        myBitmap: Bitmap,
        xPosition: Int,
        yPosition: Int
    ): String {
        var finalCommand = ""
        var color = 0
        var bit = 0
        var currentValue = 0
        var redValue = 0
        var blueValue = 0
        var greenValue = 0

        try {
            //Make sure the width is divisible by 8
            var loopWidth: Int = 8 - (myBitmap.getWidth() % 8)
            if (loopWidth == 8) loopWidth = myBitmap.getWidth()
            else loopWidth += myBitmap.getWidth()

            finalCommand = "EG" + " " + (loopWidth / 8).toString() + " " + myBitmap.getHeight()
                .toString() + " " + xPosition.toString() + " " + yPosition.toString() + " "

            for (y in 0 until myBitmap.getHeight()) {
                bit = 128
                currentValue = 0
                for (x in 0 until loopWidth) {
                    var intensity = 0

                    if (x < myBitmap.getWidth()) {
                        color = myBitmap.getPixel(x, y)

                        redValue = Color.red(color)
                        blueValue = Color.blue(color)
                        greenValue = Color.green(color)

                        intensity = 255 - ((redValue + greenValue + blueValue) / 3)
                    } else intensity = 0


                    if (intensity >= 128) currentValue = currentValue or bit
                    bit = bit shr 1
                    if (bit == 0) {
                        var hex = Integer.toHexString(currentValue)
                        hex = leftPad(hex)
                        //m_data = m_data + hex.uppercase(Locale.getDefault())
                        finalCommand += hex.uppercase(Locale.getDefault())
                        bit = 128
                        currentValue = 0

                        /****
                         * String dbg = "x,y" + "-"+ Integer.toString(x) + "," + Integer.toString(y) + "-" +
                         * "Col:" + Integer.toString(color) + "-" +
                         * "Red: " +  Integer.toString(redValue) + "-" +
                         * "Blue: " +  Integer.toString(blueValue) + "-" +
                         * "Green: " +  Integer.toString(greenValue) + "-" +
                         * "Hex: " + hex;
                         *
                         * Log.d(TAG,dbg);
                         */
                    }
                } //x
            } //y

            //m_data = m_data + "\r\n"
            finalCommand += "\r\n"
        } catch (e: java.lang.Exception) {
            finalCommand = e.message.nullSafety()
            return finalCommand
        }

        return finalCommand
    }

    private fun leftPad(num: String): String {
        var str = num

        if (num.length == 1) {
            str = "0$num"
        }

        return str
    }

    fun getFromPrefAndSetToPrintComment(sharedPreference: SharedPref, mFrom: String = "") {
        //Bar Code
        barCodePrint = sharedPreference.read(
            SharedPrefKey.BAR_CODE_FOR_PRINT, ""
        ).toString()

        barCodeX = sharedPreference.read(
            SharedPrefKey.BAR_CODE_FOR_PRINT_X, ""
        ).toString()
        barCodeY = sharedPreference.read(
            SharedPrefKey.BAR_CODE_FOR_PRINT_Y, ""
        ).toString()

        barCodeHeight = sharedPreference.read(
            SharedPrefKey.BAR_CODE_FOR_PRINT_HEIGHT, ""
        ).toString()
        //Bar Code

        XQRCodeLable = sharedPreference.read(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_X, ""
        ).isEmpty("0")
        YQRCodeLable = sharedPreference.read(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_Y, ""
        ).isEmpty("0")
        qrCodeLabel = sharedPreference.read(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT, ""
        ).isEmpty("")
        FontQRCodeLable = sharedPreference.read(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_FONT, ""
        ).isEmpty("")

        XBottomAddress = sharedPreference.read(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_X, ""
        ).toString()
        YBottomAddress = sharedPreference.read(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_Y, ""
        ).toString()
        BottomAddressValue = sharedPreference.read(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT, ""
        ).toString()
        FontBottomAddress = sharedPreference.read(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        XBottomAddress2 = sharedPreference.read(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_X, ""
        ).toString()
        YBottomAddress2 = sharedPreference.read(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_Y, ""
        ).toString()
        BottomAddressValue2 = sharedPreference.read(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT, ""
        ).toString()
        FontBottomAddress2 = sharedPreference.read(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        XBottomAddress3 = sharedPreference.read(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_X, ""
        ).toString()
        YBottomAddress3 = sharedPreference.read(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_Y, ""
        ).toString()
        BottomAddressValue3 = sharedPreference.read(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT, ""
        ).toString()
        FontBottomAddress3 = sharedPreference.read(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        XBottomAddress4 = sharedPreference.read(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_X, ""
        ).toString()
        YBottomAddress4 = sharedPreference.read(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_Y, ""
        ).toString()
        BottomAddressValue4 = sharedPreference.read(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT, ""
        ).toString()
        FontBottomAddress4 = sharedPreference.read(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        XBottomAddress5 = sharedPreference.read(
            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_X, ""
        ).toString()
        YBottomAddress5 = sharedPreference.read(
            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_Y, ""
        ).toString()
        BottomAddressValue5 = sharedPreference.read(
            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT, ""
        ).toString()
        FontBottomAddress5 = sharedPreference.read(
            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        XBottomAddress6 = sharedPreference.read(
            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_X, ""
        ).toString()
        YBottomAddress6 = sharedPreference.read(
            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_Y, ""
        ).toString()
        BottomAddressValue6 = sharedPreference.read(
            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT, ""
        ).toString()
        FontBottomAddress6 = sharedPreference.read(
            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        XBottomAddress7 = sharedPreference.read(
            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_X, ""
        ).toString()
        YBottomAddress7 = sharedPreference.read(
            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_Y, ""
        ).toString()
        BottomAddressValue7 = sharedPreference.read(
            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT, ""
        ).toString()
        FontBottomAddress7 = sharedPreference.read(
            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        xBottomAddress8 = sharedPreference.read(
            SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_X, ""
        ).toString()
        yBottomAddress8 = sharedPreference.read(
            SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_Y, ""
        ).toString()
        bottomAddressValue8 = sharedPreference.read(
            SharedPrefKey.FOOTER8_LABEL_FOR_PRINT, ""
        ).toString()
        fontBottomAddress8 = sharedPreference.read(
            SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        xBottomAddress9 = sharedPreference.read(
            SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_X, ""
        ).toString()
        yBottomAddress9 = sharedPreference.read(
            SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_Y, ""
        ).toString()
        bottomAddressValue9 = sharedPreference.read(
            SharedPrefKey.FOOTER9_LABEL_FOR_PRINT, ""
        ).toString()
        fontBottomAddress9 = sharedPreference.read(
            SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        xBottomAddress10 = sharedPreference.read(
            SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_X, ""
        ).toString()
        yBottomAddress10 = sharedPreference.read(
            SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_Y, ""
        ).toString()
        bottomAddressValue10 = sharedPreference.read(
            SharedPrefKey.FOOTER10_LABEL_FOR_PRINT, ""
        ).toString()
        fontBottomAddress10 = sharedPreference.read(
            SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        xBottomAddress11 = sharedPreference.read(
            SharedPrefKey.FOOTER11_LABEL_FOR_PRINT_X, ""
        ).toString()
        yBottomAddress11 = sharedPreference.read(
            SharedPrefKey.FOOTER11_LABEL_FOR_PRINT_Y, ""
        ).toString()
        bottomAddressValue11 = sharedPreference.read(
            SharedPrefKey.FOOTER11_LABEL_FOR_PRINT, ""
        ).toString()
        fontBottomAddress11 = sharedPreference.read(
            SharedPrefKey.FOOTER11_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        xBottomAddress12 = sharedPreference.read(
            SharedPrefKey.FOOTER12_LABEL_FOR_PRINT_X, ""
        ).toString()
        yBottomAddress12 = sharedPreference.read(
            SharedPrefKey.FOOTER12_LABEL_FOR_PRINT_Y, ""
        ).toString()
        bottomAddressValue12 = sharedPreference.read(
            SharedPrefKey.FOOTER12_LABEL_FOR_PRINT, ""
        ).toString()
        fontBottomAddress12 = sharedPreference.read(
            SharedPrefKey.FOOTER12_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        XBottomSchedule = sharedPreference.read(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_X, ""
        ).toString()
        YBottomSchedule = sharedPreference.read(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_Y, ""
        ).toString()
        BottomScheduleValue = sharedPreference.read(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT, ""
        ).toString()
        FontBottomSchedule = sharedPreference.read(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        XQRCode = sharedPreference.read(
            SharedPrefKey.QRCODE_FOR_PRINT_X, ""
        ).toString()
        YQRCode = sharedPreference.read(
            SharedPrefKey.QRCODE_FOR_PRINT_Y, ""
        ).toString()

        mCitationNumberValue = sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_FOR_PRINT, ""
        ).toString()
        mCitationNumberLable = sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_LABEL_FOR_PRINT, ""
        ).isEmpty("Citation#")
        mCitationNumberX = sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_X, ""
        ).isEmpty("5")
        mCitationNumberY = sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_Y, ""
        ).isEmpty("30")
        mCitationNumberFont = sharedPreference.read(
            SharedPrefKey.CITATION_NUMBER_FOR_PRINT_FONT, ""
        ).isEmpty("1")

        if (YBottomAddress.isNotEmpty() && BottomAddressValue!!.isNotEmpty()) {
            BottomAddressValueArray = BottomAddressValue.split("#").toTypedArray()
            YBottomAddressLine1 = Integer.parseInt(YBottomAddress) + 20
            YBottomAddressLine2 = YBottomAddressLine1 + 20
            YBottomAddressLine3 = YBottomAddressLine2 + 20
            mBottomAddressSecondLine =
                if (BottomAddressValueArray.size > 1) BottomAddressValueArray[1].toString() else ""
            mBottomAddressThirdLine =
                if (BottomAddressValueArray.size > 2) BottomAddressValueArray[2].toString() else ""
            mBottomAddressFourthLine =
                if (BottomAddressValueArray.size > 3) BottomAddressValueArray[3].toString() else ""
        }

        try {
            BottomAddress2ValueArray = BottomAddressValue2.split("#").toTypedArray()
            BottomAddress3ValueArray = BottomAddressValue3.split("#").toTypedArray()
            BottomAddress4ValueArray = BottomAddressValue4.split("#").toTypedArray()
            BottomAddress5ValueArray = BottomAddressValue5.split("#").toTypedArray()
            BottomAddress6ValueArray = BottomAddressValue6.split("#").toTypedArray()
            BottomAddress7ValueArray = BottomAddressValue7.split("#").toTypedArray()
            bottomAddress8ValueArray = bottomAddressValue8.split("#").toTypedArray()
            bottomAddress9ValueArray = bottomAddressValue9.split("#").toTypedArray()
            bottomAddress10ValueArray = bottomAddressValue10.split("#").toTypedArray()
            bottomAddress11ValueArray = bottomAddressValue11.split("#").toTypedArray()
            bottomAddress12ValueArray = bottomAddressValue12.split("#").toTypedArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
        ) {
            mQrCodeSize = 3
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)
        ) {
            mQrCodeSize = 4
        }
        try {
            if (mBottomAddressQuery != null) {
                mBottomAddressQuery!!.clear()
            }

            mBottomAddressText(mFrom)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Getting QR code size from settings file is there is any, else it will take previous size
        if (BaseActivity.getQRCodeSizeForCommandPrint() != 0){
            mQrCodeSize = BaseActivity.getQRCodeSizeForCommandPrint().nullSafety()
        }
    }

    private fun mBottomAddressText(mFrom: String = ""): String {
        try {
            if (BottomScheduleValue != null && BottomScheduleValue!!.isNotEmpty()) {
                mBottomAddressQuery!!.append("TEXT 7 " + FontBottomSchedule + " " + XBottomSchedule + " " + YBottomSchedule + " " + BottomScheduleValue + " \r\n")
                addYAxisToSet(
                    YBottomSchedule.nullSafety().toDouble(),
                    getHeightBasedOnFont(FontBottomSchedule.nullOrEmptySafety("0").toInt())
                )

                if (LogUtil.isEnableCommandBasedFacsimile) {
                    drawableElements.add(
                        DrawableElement.Text(
                            x = XBottomSchedule.nullOrEmptySafety("0").toDouble(),
                            y = YBottomSchedule.nullOrEmptySafety("0").toDouble(),
                            text = BottomScheduleValue.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = FontBottomSchedule.nullOrEmptySafety("0").toInt()
                        )
                    )
                }
            }

            bottomIncrementY = setBottomAddressYBasedOnFontSize(FontBottomAddress)
            //Adding Footer 1 using loop to make it dynamic
            BottomAddressValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if (s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY
                    val yAxis = (YBottomAddress.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)

                    mBottomAddressQuery.append(
                        "TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = XBottomAddress.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = FontBottomAddress.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            bottomIncrementY = setBottomAddressYBasedOnFontSize(FontBottomAddress2)
            //Adding Footer 2 using loop to make it dynamic
            BottomAddress2ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if (s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY
                    val yAxis = (YBottomAddress2.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)

                    mBottomAddressQuery.append(
                        "TEXT 7 " + FontBottomAddress2 + " " + XBottomAddress2 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(FontBottomAddress2.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = XBottomAddress2.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = FontBottomAddress2.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            bottomIncrementY = setBottomAddressYBasedOnFontSize(FontBottomAddress3)
            //Adding Footer 3 using loop to make it dynamic
            val textColorFooter3 = when {
                //  Both Impark + Municipal → Red
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) &&
                        mFrom.equals(Constants.MUNICIPAL_ACTIVITY, ignoreCase = true) -> Color.BLACK

                // 🖤 Honor Bill → Black
                mFrom.equals(Constants.HONOR_BILL_ACTIVITY, ignoreCase = true) -> Color.BLACK

                // 🖤 Citation → Black
                mFrom.equals(Constants.CITATION_ACTIVITY, ignoreCase = true) -> Color.BLACK

                // Default
                else -> Color.BLACK
            }
            BottomAddress3ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY
                    val yAxis = (YBottomAddress3.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)

                    mBottomAddressQuery.append(
                        "TEXT 7 " + FontBottomAddress3 + " " + XBottomAddress3 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(FontBottomAddress3.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = XBottomAddress3.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = textColorFooter3,
                                textFont = 7,
                                textSize = FontBottomAddress3.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            bottomIncrementY = setBottomAddressYBasedOnFontSize(FontBottomAddress4)
            //Adding Footer 4 using loop to make it dynamic
            BottomAddress4ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY
                    val yAxis = (YBottomAddress4.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)

                    mBottomAddressQuery.append(
                        "TEXT 7 " + FontBottomAddress4 + " " + XBottomAddress4 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(FontBottomAddress4.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = XBottomAddress4.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = FontBottomAddress4.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            bottomIncrementY = setBottomAddressYBasedOnFontSize(FontBottomAddress5)
            //Adding Footer 5 using loop to make it dynamic
            BottomAddress5ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY
                    val yAxis = (YBottomAddress5.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)

                    mBottomAddressQuery.append(
                        "TEXT 7 " + FontBottomAddress5 + " " + XBottomAddress5 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(FontBottomAddress5.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = XBottomAddress5.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = FontBottomAddress5.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            bottomIncrementY = setBottomAddressYBasedOnFontSize(FontBottomAddress6)
            //Adding Footer 6 using loop to make it dynamic
            BottomAddress6ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if (s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY

                    val yAxis = (YBottomAddress6.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)

                    mBottomAddressQuery.append(
                        "TEXT 7 " + FontBottomAddress6 + " " + XBottomAddress6 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(FontBottomAddress6.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = XBottomAddress6.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = FontBottomAddress6.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            bottomIncrementY = setBottomAddressYBasedOnFontSize(FontBottomAddress7)

            //Adding Footer 7 using loop to make it dynamic
            BottomAddress7ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY

                    val yAxis = (YBottomAddress7.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)


                    mBottomAddressQuery.append(
                        "TEXT 7 " + FontBottomAddress7 + " " + XBottomAddress7 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(FontBottomAddress7.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = XBottomAddress7.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = FontBottomAddress7.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            bottomIncrementY = setBottomAddressYBasedOnFontSize(fontBottomAddress8)

            //Adding Footer 7 using loop to make it dynamic
            bottomAddress8ValueArray.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY

                    val yAxis = (yBottomAddress8.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)


                    mBottomAddressQuery.append(
                        "TEXT 7 " + fontBottomAddress8 + " " + xBottomAddress8 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(fontBottomAddress8.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xBottomAddress8.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = fontBottomAddress8.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            //Footer 9
            bottomIncrementY = setBottomAddressYBasedOnFontSize(fontBottomAddress9)

            //Adding Footer 9 using loop to make it dynamic
            bottomAddress9ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY

                    val yAxis = (yBottomAddress9.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)


                    mBottomAddressQuery.append(
                        "TEXT 7 " + fontBottomAddress9 + " " + xBottomAddress9 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(fontBottomAddress9.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xBottomAddress9.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = fontBottomAddress9.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            //Footer 10
            bottomIncrementY = setBottomAddressYBasedOnFontSize(fontBottomAddress10)

            //Adding Footer 10 using loop to make it dynamic
            bottomAddress10ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY

                    val yAxis = (yBottomAddress10.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)


                    mBottomAddressQuery.append(
                        "TEXT 7 " + fontBottomAddress10 + " " + xBottomAddress10 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(fontBottomAddress10.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xBottomAddress10.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = fontBottomAddress10.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }

            //Footer 11
            bottomIncrementY = setBottomAddressYBasedOnFontSize(fontBottomAddress11)
            //Adding Footer 11 using loop to make it dynamic
            bottomAddress11ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY

                    val yAxis = (yBottomAddress11.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)


                    mBottomAddressQuery.append(
                        "TEXT 7 " + fontBottomAddress11 + " " + xBottomAddress11 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(fontBottomAddress11.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xBottomAddress11.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = fontBottomAddress11.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }


            //Footer 12
            bottomIncrementY = setBottomAddressYBasedOnFontSize(fontBottomAddress12)
            //Adding Footer 12 using loop to make it dynamic
            val textColorFooter12 = when {
                //  Both Impark + Municipal → Red
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) &&
                        mFrom.equals(Constants.MUNICIPAL_ACTIVITY, ignoreCase = true) -> Color.BLACK

                // 🖤 Honor Bill → Black
                mFrom.equals(Constants.HONOR_BILL_ACTIVITY, ignoreCase = true) -> Color.BLACK

                // 🖤 Citation → Black
                mFrom.equals(Constants.CITATION_ACTIVITY, ignoreCase = true) -> Color.BLACK

                // Default
                else -> Color.BLACK
            }
            bottomAddress12ValueArray?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, s ->
                if(s!!.isNotEmpty()) {
                    val yIndex = index * bottomIncrementY

                    val yAxis = (yBottomAddress12.nullOrEmptySafety(
                        "0"
                    ).toInt() + yIndex)


                    mBottomAddressQuery.append(
                        "TEXT 7 " + fontBottomAddress12 + " " + xBottomAddress12 + " " + yAxis + " " + s + " \r\n"
                    )
                    addYAxisToSet(
                        yAxis.toDouble(),
                        getHeightBasedOnFont(fontBottomAddress12.nullOrEmptySafety("0").toInt())
                    )

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xBottomAddress12.nullOrEmptySafety("0").toDouble(),
                                y = yAxis.nullSafety().toDouble(),
                                text = s.nullSafety(),
                                textColor = textColorFooter12,
                                textFont = 7,
                                textSize = fontBottomAddress12.nullOrEmptySafety("0").toInt()
                            )
                        )
                    }
                }
            }


            if (
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)
            ) {
                //No Line is Needed at bottom
            } else {
                if (BottomAddress5ValueArray.isNotEmpty()) {
                    val yAxisStart = YBottomAddress5.toInt() + 150
                    val yAxisForAddressFive = YBottomAddress5.toInt() + 150
                    mBottomAddressQuery.append("LINE " + 1 + " " + yAxisStart + " $lineXAxisEnd " + yAxisForAddressFive + " 1 \r\n")
                    addYAxisToSet(yAxisForAddressFive.nullSafety().toDouble(), PRINT_LINE_HEIGHT)

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(DrawableElement.Line(1f, yAxisStart.toFloat(), lineXAxisEnd.toFloat(),  yAxisForAddressFive.toFloat(), Color.BLACK, 1f))
                    }
                } else {
                    val yAxisStart = YBottomAddress4.toInt() + 150
                    val yAxisForAddressFour = YBottomAddress4.toInt() + 150
                    mBottomAddressQuery.append("LINE " + 1 + " " + yAxisStart + " $lineXAxisEnd " + yAxisForAddressFour + " 1 \r\n")
                    addYAxisToSet(yAxisForAddressFour.nullSafety().toDouble(), PRINT_LINE_HEIGHT)

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(DrawableElement.Line(1f, yAxisStart.toFloat(), lineXAxisEnd.toFloat(),  yAxisForAddressFour.toFloat(), Color.BLACK, 1f))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun setXYforPrintCitationHeader(
        commandPrinter: java.lang.StringBuilder
    ): java.lang.StringBuilder {
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ORLEANS,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
        ) {
            commandPrinter.append("TEXT 7 " + mCitationNumberFont + " " + mCitationNumberX + " " + mCitationNumberY + " " + mCitationNumberLable + " \r\n")
            commandPrinter.append("TEXT 7 " + mCitationNumberFont + " " + (mCitationNumberX.toInt() + 140) + " " + mCitationNumberY + " " + mCitationNumberValue + " \r\n")

            addYAxisToSet(mCitationNumberY.nullOrEmptySafety("0").toDouble(), getHeightBasedOnFont(
                mCitationNumberFont.nullSafety("0").toInt()))

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Text(
                        x = mCitationNumberX.nullOrEmptySafety("0").toDouble(),
                        y = mCitationNumberY.nullOrEmptySafety("0").toDouble(),
                        text = mCitationNumberLable,
                        textColor = Color.BLACK,
                        textFont = 7,
                        textSize = mCitationNumberFont.nullOrEmptySafety("0").toInt()
                    )
                )

                drawableElements.add(
                    DrawableElement.Text(
                        x = (mCitationNumberX.toInt() + 140).nullSafety().toDouble(),
                        y = mCitationNumberY.nullOrEmptySafety("0").toDouble(),
                        text = mCitationNumberValue,
                        textColor = Color.BLACK,
                        textFont = 7,
                        textSize = mCitationNumberFont.nullOrEmptySafety("0").toInt()
                    )
                )
            }
        }

        return commandPrinter
    }


    @JvmStatic
    fun setXYforPrintQRCode(
        context: Context,
        commandPrinter: java.lang.StringBuilder
    ): java.lang.StringBuilder {
        if (AppUtils.mFinalQRCodeValue.isNotEmpty()) {
            commandPrinter.append("B QR " + XQRCode + " " + YQRCode + " M 2 U " + mQrCodeSize + " \r\n" + "M0A,QR code " + AppUtils.mFinalQRCodeValue + " \r\n" + "ENDQR\r\n")

            if (LogUtil.isEnableCommandBasedFacsimile) {
                val bitmapQrCode =
                    QRCodeUtils.generateQRCodeForPrint(context, AppUtils.mFinalQRCodeValue)
                val scaledBitmap =
                    getScaledQRBitmapBasedOnCommandBitmapSize(mQrCodeSize, bitmapQrCode)

                drawableElements.add(
                    DrawableElement.Image(
                        x = XQRCode.nullOrEmptySafety("0").toFloat(),
                        y = YQRCode.nullOrEmptySafety("0").toFloat(),
                        width = scaledBitmap.width.nullSafety().toFloat(),
                        height = scaledBitmap.height.nullSafety().toFloat(),
                        bitmap = scaledBitmap
                    )
                )
            }

            addYAxisToSet(
                YQRCode.nullOrEmptySafety("0").toDouble(),
                getYAxisBasedOnQRCodeHeight(mQrCodeSize)
            )
        }

        return commandPrinter;
    }

    @JvmStatic
    fun setXYforPrintQRCodeLabel(
        commandPrinter: java.lang.StringBuilder
    ): java.lang.StringBuilder {
        if (AppUtils.mFinalQRCodeValue.isNotEmpty() && qrCodeLabel!!.isNotEmpty()) {
            commandPrinter.append("TEXT 7 " + FontQRCodeLable + " " + XQRCodeLable + " " + YQRCodeLable + " " + qrCodeLabel + " \r\n")

            addYAxisToSet(
                YQRCodeLable.nullOrEmptySafety("0").toDouble(),
                getHeightBasedOnFont(FontQRCodeLable.nullOrEmptySafety("0").toInt())
            )
            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Text(
                        x = XQRCodeLable.nullOrEmptySafety("0").toDouble(),
                        y = YQRCodeLable.nullOrEmptySafety("0").toDouble(),
                        text = qrCodeLabel,
                        textColor = Color.BLACK,
                        textFont = 7,
                        textSize = FontQRCodeLable.nullOrEmptySafety("0").toInt()
                    )
                )
            }
        }

        return commandPrinter;
    }

    @JvmStatic
    fun setXYforPrintBarCode(
        mDB: AppDatabase?,
        ticketNumber: String?,
        commandPrinter: java.lang.StringBuilder
    ): java.lang.StringBuilder {
        //Barcode values from settings file


        //In case if values are not set from shared pref file then it will take from settings file
        if (barCodeHeight.isEmpty()) {
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDB)

            barCodeHeight = settingsList?.firstOrNull {
                it.type?.trim().equals(
                    SETTINGS_FLAG_BAR_CODE_HEIGHT,
                    true
                )
            }?.mValue?.trim().nullSafety()

            barCodeX = settingsList?.firstOrNull {
                it.type?.trim().equals(
                    SETTINGS_FLAG_BAR_CODE_X,
                    true
                )
            }?.mValue?.trim().nullSafety("0")

            barCodeY = settingsList?.firstOrNull {
                it.type?.trim().equals(
                    SETTINGS_FLAG_BAR_CODE_Y,
                    true
                )
            }?.mValue?.trim().nullSafety("0")
        }

//        val barCodeHeight = "50"
//        val barCodeX = "50"
//        val barCodeY = "500"



        if (barCodeHeight.isNotEmpty() && barCodeX.isNotEmpty() && barCodeY.isNotEmpty() && ticketNumber.nullSafety()
                .isNotEmpty()
        ) {
            commandPrinter.append("BARCODE 128 1 1 $barCodeHeight $barCodeX $barCodeY $ticketNumber\r\n")

            if (LogUtil.isEnableCommandBasedFacsimile) {
                val width = AppUtils.getWidthForHeight(barCodeHeight.nullSafety("0").toInt(), 6, 1)
                val bitmapBarCode =
                    BarCodeUtils.generateBarCodeForPrint(
                        ticketNumber.nullSafety(),
                        width,
                        barCodeHeight.nullSafety("0").toInt()
                    )
//                val scaledBitmap =
//                    getScaledQRBitmapBasedOnCommandBitmapSize(mQrCodeSize, bitmapQrCode)

                drawableElements.add(
                    DrawableElement.Image(
                        x = barCodeX.nullSafety("0").toFloat(),
                        y = barCodeY.nullSafety("0").toFloat(),
                        width = bitmapBarCode.width.nullSafety().toFloat(),
                        height = bitmapBarCode.height.nullSafety().toFloat(),
                        bitmap = bitmapBarCode
                    )
                )
            }

            addYAxisToSet(
                barCodeY.nullOrEmptySafety("0").toDouble(),
                barCodeHeight.nullSafety("0").toInt()
            )
        }

        return commandPrinter;
    }

    @JvmStatic
    fun setBottomAddressInCommand(
        commandPrinter: java.lang.StringBuilder
    ): java.lang.StringBuilder {

        if(isSiteSupportCommandPrinting(true)) {
            commandPrinter.append(mBottomAddressQuery)
        }

        return commandPrinter;
    }

    @JvmStatic
    fun setXYForAddressLines(commandPrinter: java.lang.StringBuilder): java.lang.StringBuilder {
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
        ) {
            commandPrinter.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddress + " " + BottomAddressValueArray[0] + " \r\n")
            commandPrinter.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine1 + " " + mBottomAddressSecondLine + " \r\n")
            commandPrinter.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine2 + " " + mBottomAddressThirdLine + " \r\n")
            commandPrinter.append("TEXT 7 " + FontBottomAddress + " " + XBottomAddress + " " + YBottomAddressLine3 + " " + mBottomAddressFourthLine + " \r\n")


            addYAxisToSet(
                YBottomAddress.nullOrEmptySafety("0").toDouble(),
                getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt())
            )
            addYAxisToSet(
                YBottomAddressLine1.nullSafety().toDouble(),
                getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt())
            )
            addYAxisToSet(
                YBottomAddressLine2.nullSafety().toDouble(),
                getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt())
            )
            addYAxisToSet(
                YBottomAddressLine3.nullSafety().toDouble(),
                getHeightBasedOnFont(FontBottomAddress.nullOrEmptySafety("0").toInt())
            )


            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Text(
                        x = XBottomAddress.nullOrEmptySafety("0").toDouble(),
                        y = YBottomAddress.nullOrEmptySafety("0").toDouble(),
                        text = BottomAddressValueArray[0].nullSafety(),
                        textColor = Color.BLACK,
                        textFont = 7,
                        textSize = FontBottomAddress.nullOrEmptySafety("0").toInt()
                    )
                )


                drawableElements.add(
                    DrawableElement.Text(
                        x = XBottomAddress.nullOrEmptySafety("0").toDouble(),
                        y = YBottomAddressLine1.nullSafety().toDouble(),
                        text = mBottomAddressSecondLine.nullSafety(),
                        textColor = Color.BLACK,
                        textFont = 7,
                        textSize = FontBottomAddress.nullOrEmptySafety("0").toInt()
                    )
                )


                drawableElements.add(
                    DrawableElement.Text(
                        x = XBottomAddress.nullOrEmptySafety("0").toDouble(),
                        y = YBottomAddressLine2.nullSafety().toDouble(),
                        text = mBottomAddressThirdLine.nullSafety(),
                        textColor = Color.BLACK,
                        textFont = 7,
                        textSize = FontBottomAddress.nullOrEmptySafety("0").toInt()
                    )
                )


                drawableElements.add(
                    DrawableElement.Text(
                        x = XBottomAddress.nullOrEmptySafety("0").toDouble(),
                        y = YBottomAddressLine3.nullSafety().toDouble(),
                        text = mBottomAddressFourthLine.nullSafety(),
                        textColor = Color.BLACK,
                        textFont = 7,
                        textSize = FontBottomAddress.nullOrEmptySafety("0").toInt()
                    )
                )


            }

        }

        return commandPrinter;
    }



    @JvmStatic
    fun setXYforPrintHeaderAndLines(
        sharedPreference : SharedPref,
        commandPrinter: java.lang.StringBuilder
    ): java.lang.StringBuilder {
        var mAddoneY = 82

        if(
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS,ignoreCase = true)||
            sharedPreference.read(SharedPrefKey.HEADER_1_FOR_PRINT, "")!!.toString().isNotEmpty()) {

            var header1 = sharedPreference.read(SharedPrefKey.HEADER_1_FOR_PRINT, "")
            var header2 = sharedPreference.read(SharedPrefKey.HEADER_2_FOR_PRINT, "")
            var header3 = sharedPreference.read(SharedPrefKey.HEADER_3_FOR_PRINT, "")

            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true))
            {
                header2 = sharedPreference.read(SharedPrefKey.HEADER_2_FOR_PRINT, "")
                    ?.replace("#", "\n")
            }

            var header1X = sharedPreference.read(SharedPrefKey.HEADER_1_FOR_PRINT_X, "")
            var header1Y = sharedPreference.read(SharedPrefKey.HEADER_1_FOR_PRINT_Y, "")
            var header1Font = sharedPreference.read(SharedPrefKey.HEADER_1_FOR_PRINT_FONT, "")

            var header2X = sharedPreference.read(SharedPrefKey.HEADER_2_FOR_PRINT_X, "")
            var header2Y = sharedPreference.read(SharedPrefKey.HEADER_2_FOR_PRINT_Y, "")
            var header2Font = sharedPreference.read(SharedPrefKey.HEADER_2_FOR_PRINT_FONT, "")

            var header3X = sharedPreference.read(SharedPrefKey.HEADER_3_FOR_PRINT_X, "")
            var header3Y = sharedPreference.read(SharedPrefKey.HEADER_3_FOR_PRINT_Y, "")
            var header3Font = sharedPreference.read(SharedPrefKey.HEADER_3_FOR_PRINT_FONT, "")

            if (header1X!!.isNotEmpty()) {
                if (!header1.isNullOrEmpty() && header1!!.isNotEmpty()) {
                    // Split header1 into multiple lines by '#'
                    val headerLines = header1.split("#")

                    headerLines.forEachIndexed { index, line ->
                        val posX = if (!header1X.isNullOrEmpty()) header1X!!.toDouble() else 0.0
                        val baseY = if (!header1Y.isNullOrEmpty()) header1Y!!.toDouble() else 0.0

                        // Add vertical offset per line (adjust spacing depending on font size)
                        val lineSpacing = getHeightBasedOnFont(header1Font.nullSafety("0").toInt())
                        val posY = baseY + (index * lineSpacing)

                        // Append to printer command
                        commandPrinter.append(
                            "TEXT 5 $header1Font $posX $posY ${line.trim()} \r\n"
                        )

                        // Maintain Y-Axis calculation
                        addYAxisToSet(posY, lineSpacing)

                        // Add to drawable elements for facsimile/debug mode
                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(
                                DrawableElement.Text(
                                    x = posX,
                                    y = posY,
                                    text = line.trim(),
                                    textColor = Color.BLACK,
                                    textFont = 5,
                                    textSize = header1Font.nullSafety("0").toInt()
                                )
                            )
                        }
                    }
                }

                /**
                 * Header 2
                 */
                if (!header2.isNullOrEmpty() && header2!!.isNotEmpty()) {
                    // Split header2 into multiple lines by '#'
                    val headerLines = header2.split("#")

                    headerLines.forEachIndexed { index, line ->
                        val posX = if (!header2X.isNullOrEmpty()) header2X!!.toDouble() else 0.0
                        val baseY = if (!header2Y.isNullOrEmpty()) header2Y!!.toDouble() else 0.0

                        // Add vertical offset per line (adjust spacing depending on font size)
                        val lineSpacing = getHeightBasedOnFont(header2Font.nullSafety("0").toInt())
                        val posY = baseY + (index * lineSpacing)

                        // Append to printer command
                        commandPrinter.append(
                            "TEXT 5 $header2Font $posX $posY $line \r\n"
                        )

                        // Maintain Y-Axis calculation (only for the last line or all?)
                        addYAxisToSet(posY, lineSpacing)

                        // Add to drawable elements for facsimile/debug mode
                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(
                                DrawableElement.Text(
                                    x = posX,
                                    y = posY,
                                    text = line,
                                    textColor = Color.BLACK,
                                    textFont = 5,
                                    textSize = header2Font.nullSafety("0").toInt()
                                )
                            )
                        }
                    }
                }/**
                 * Header 3
                 */
                if (!header3.isNullOrEmpty() && header3!!.isNotEmpty()) {
                    // Split header2 into multiple lines by '#'
                    val headerLines = header3.split("#")

                    headerLines.forEachIndexed { index, line ->
                        val posX = if (!header3X.isNullOrEmpty()) header3X!!.toDouble() else 0.0
                        val baseY = if (!header3Y.isNullOrEmpty()) header3Y!!.toDouble() else 0.0

                        // Add vertical offset per line (adjust spacing depending on font size)
                        val lineSpacing = getHeightBasedOnFont(header3Font.nullSafety("0").toInt())
                        val posY = baseY + (index * lineSpacing)

                        // Append to printer command
                        commandPrinter.append(
                            "TEXT 5 $header3Font $posX $posY $line \r\n"
                        )

                        // Maintain Y-Axis calculation (only for the last line or all?)
                        addYAxisToSet(posY, lineSpacing)

                        // Add to drawable elements for facsimile/debug mode
                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(
                                DrawableElement.Text(
                                    x = posX,
                                    y = posY,
                                    text = line,
                                    textColor = Color.BLACK,
                                    textFont = 5,
                                    textSize = header3Font.nullSafety("0").toInt()
                                )
                            )
                        }
                    }
                }

                //Add Lines between headers
               /* if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) &&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
                    ) {
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ASHLAND,
                            ignoreCase = true
                        ) ||BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SOUTHMIAMI,
                            ignoreCase = true
                        ) ||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_KANSAS_CITY,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SEASTREAK,
                            ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
                            ignoreCase = true
                        )|| BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
                            ignoreCase = true
                        )
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
                            ignoreCase = true
                        )
                        ||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_OXFORD,
                            ignoreCase = true
                        ) ||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PRIME_PARKING,
                            ignoreCase = true
                        ) ||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_RIVEROAKS,
                            ignoreCase = true
                        )
                        ||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_BEAUFORT,
                            ignoreCase = true
                        )

                        ||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SURF_CITY,
                            ignoreCase = true
                        ) ||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_WOODSTOCK_GA,
                            ignoreCase = true
                        ) ||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ORLEANS,
                            ignoreCase = true
                        )||
                        BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_BOSTON,
                            ignoreCase = true
                        ) ||
                        header2Y == null || header2Y.isEmpty()
                    ) {

                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_KANSAS_CITY,
                                ignoreCase = true
                            )
                            ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                                ignoreCase = true
                            )
                            ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                                ignoreCase = true
                            )
                            ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                                ignoreCase = true
                            )
                            ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                                ignoreCase = true
                            )
                            ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_SEASTREAK,
                                ignoreCase = true
                            )||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
                                ignoreCase = true
                            )||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_OXFORD,
                                ignoreCase = true
                            )
                            ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_PRIME_PARKING,
                                ignoreCase = true
                            ) ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                                ignoreCase = true
                            )  ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                                ignoreCase = true
                            )  || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_DANVILLE_VA,
                                ignoreCase = true
                            ) ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_CAMDEN,
                                ignoreCase = true
                            ) ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_SOUTH_LAKE,
                                ignoreCase = true
                            ) ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_WINPARK_TX,
                                ignoreCase = true
                            ) ||BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                                ignoreCase = true
                            )
                        ) {
                            mAddoneY = 58
                        }else if(
                            BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
                                ignoreCase = true
                            ) || BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
                                ignoreCase = true
                            )
                        ){
                            mAddoneY = 88
                        }

                        var startYAxis = 0.0
                        if (header1Y!!.isNotEmpty()) {
                            startYAxis = header1Y!!.toDouble() + mAddoneY
                        }else{
                            startYAxis = 0.0
                        }

                        var endYAxis = 0.0
                        if (header1Y!!.isNotEmpty()) {
                            endYAxis = header1Y!!.toDouble() + mAddoneY
                        }else{
                            endYAxis = 0.0
                        }

                        commandPrinter.append("LINE " + 5 + " " + startYAxis + " $lineXAxisEnd " + endYAxis + " 2" + " \r\n")

                        addYAxisToSet(header1Y.nullSafety("0").toDouble() + mAddoneY, PRINT_LINE_HEIGHT)

                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(DrawableElement.Line(5f, startYAxis.toFloat(), lineXAxisEnd.toFloat(),  endYAxis.toFloat(), Color.BLACK, 2f))
                        }
                    } else {

                        var startYAxis = 0.0
                        if (header2Y!!.isNotEmpty()) {
                            startYAxis = header2Y!!.toDouble() + 50
                        }else{
                            startYAxis = 0.0
                        }

                        var endYAxis = 0.0
                        if (header2Y!!.isNotEmpty()) {
                            endYAxis = header2Y!!.toDouble() + 50
                        }else{
                            endYAxis = 0.0
                        }

                        commandPrinter.append("LINE " + 5 + " " + startYAxis + " $lineXAxisEnd " + endYAxis + " 2" + " \r\n")

                        addYAxisToSet(header2Y.nullSafety("0").toDouble() + 20, PRINT_LINE_HEIGHT)

                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(DrawableElement.Line(5f, startYAxis.toFloat(), lineXAxisEnd.toFloat(),  endYAxis.toFloat(), Color.BLACK, 2f))
                        }
                    }
                }*/
            }
        }


        return commandPrinter
    }

    /**
     * Function used to set app logo in command printing
     * It will append logo in command
     */
    @JvmStatic
    fun setAppLogoInCommandPrint(
        context: Context,
        sharedPreference: SharedPref,
        commandPrinter: java.lang.StringBuilder
    ) : java.lang.StringBuilder{

        try {
            val appLogoToPrint = sharedPreference.read(
                SharedPrefKey.APP_LOGO_FOR_PRINT, ""
            ).nullSafety()

            if (appLogoToPrint.isNotEmpty()) {
                val appLogoToPrintXPosition = sharedPreference.read(
                    SharedPrefKey.APP_LOGO_FOR_PRINT_X, "0"
                ).nullSafety()

                val appLogoToPrintYPosition = sharedPreference.read(
                    SharedPrefKey.APP_LOGO_FOR_PRINT_Y, "0"
                ).nullSafety()

                val appLogoToWidth = sharedPreference.read(
                    SharedPrefKey.APP_LOGO_FOR_PRINT_WIDTH, "200"
                ).nullSafety()

                if (appLogoToPrintXPosition == "0" &&
                    appLogoToPrintYPosition == "0" &&
                    appLogoToWidth == "0"
                ) {
                    return commandPrinter
                }

                var appLogo: Bitmap? = null
                appLogo =
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)
                    ) {
                        //We have different app logo for PRRS, so defined that here
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.app_logo_prrs
                        )
                    }
                    else {
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.app_icon
                        )
                    }

                val scaledBitmap = BitmapUtils.scale(appLogo, appLogoToWidth.toInt(), appLogoToWidth.toInt())

                setXYForImage(
                    bitmap = scaledBitmap,
                    xPosition = appLogoToPrintXPosition.toInt(),
                    yPosition = appLogoToPrintYPosition.toInt(),
                    commandPrinter
                )

            }
        } catch (e:Exception) {
            e.printStackTrace()
        }

        return commandPrinter
    }
    /**
     * Function used to set app logo in command printing
     * It will append logo in command
     */
    @JvmStatic
    fun setSignatureInCommandPrint(
        context: Context,
        sharedPreference: SharedPref,
        commandPrinter: java.lang.StringBuilder
    ) : java.lang.StringBuilder{

        val officerSignatureToPrint = sharedPreference.read(
            SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT, ""
        ).nullSafety()

        val imageName = getSignatureFileNameWithExt()
        val mSignaturePath = sharedPreference.read(SharedPrefKey.FILE_PATH, "") + Constants.CAMERA + "/" + imageName
        val file = File(mSignaturePath)

        if (officerSignatureToPrint.isNotEmpty()|| file.exists()) {
            val appLogoToPrintXPosition = sharedPreference.read(
                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_X, "0"
            ).nullSafety()

            val appLogoToPrintYPosition = sharedPreference.read(
                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_Y, "0"
            ).nullSafety()

            val appLogoToWidth = sharedPreference.read(
                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_WIDTH, "200"
            ).nullSafety()

            var appLogo: Bitmap? = null

            if (file.exists()) {
                //We have checking signature image is exsit or not and print

                if(file.exists()){
                    val bitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
                    appLogo = bitmap
                }else  {
                    appLogo = BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.signature_pen_for_print
                    )
                }
            }

//            val scaledBitmap =
//                appLogo?.let { BitmapUtils.scale(it, appLogoToWidth.toInt(), appLogoToWidth.toInt()) }

            val cleanBitmap = appLogo?.let { convertToMonochrome(it) }
            val scaledBitmap =
                cleanBitmap?.let { BitmapUtils.scale(it, appLogoToWidth.toInt(), appLogoToWidth.toInt()) }
            if (scaledBitmap != null) {
                setXYForImage(
                    bitmap = scaledBitmap,
                    xPosition = appLogoToPrintXPosition.toInt(),
                    yPosition = appLogoToPrintYPosition.toInt(),
                    commandPrinter
                )
            }

        }

        return commandPrinter
    }

    fun convertToMonochrome(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val monoBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(monoBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // Remove color
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        // Convert light pixels to white, dark to black
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = monoBitmap.getPixel(x, y)
                val gray = Color.red(pixel)
                monoBitmap.setPixel(x, y, if (gray < 128) Color.BLACK else Color.WHITE)
            }
        }
        return monoBitmap
    }
    @JvmStatic
    fun setLprImageInCommandPrint(
        mDB: AppDatabase?,
        lprImage: File?,
        sharedPreference: SharedPref,
        commandPrinter: java.lang.StringBuilder
    ): java.lang.StringBuilder {
        val lprImageToPrint = sharedPreference.read(
            SharedPrefKey.LPR_IMAGE_FOR_PRINT, ""
        ).nullSafety()

        if (lprImageToPrint.isNotEmpty() && lprImage != null && isPrintLprImageInFacsimilePrint(mDB)) {
            val lprImageToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LPR_IMAGE_FOR_PRINT_X, "0"
            ).nullSafety()

            val lprImageToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LPR_IMAGE_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lprImageToWidth = sharedPreference.read(
                SharedPrefKey.LPR_IMAGE_FOR_PRINT_WIDTH, "200"
            ).nullSafety()


            val scaledBitmap =
                BitmapUtils.scale(BitmapFactory.decodeFile(lprImage.absolutePath), lprImageToWidth.toInt(), lprImageToWidth.toInt())

//            commandPrinter.append(
//                setXYForImage(
//                    bitmap = scaledBitmap,
//                    xPosition = lprImageToPrintXPosition.toInt(),
//                    yPosition = lprImageToPrintYPosition.toInt(),
//                    AppUtils.printQueryStringBuilder
//                )
//            )

            setXYForImage(
                bitmap = scaledBitmap,
                xPosition = lprImageToPrintXPosition.toInt(),
                yPosition = lprImageToPrintYPosition.toInt(),
                commandPrinter
            )

//            AppUtils.printQueryStringBuilder = AppUtils.setXYForImage(
//                bitmap = scaledBitmap,
//                xPosition = lprImageToPrintXPosition.toInt(),
//                yPosition = lprImageToPrintYPosition.toInt(),
//                AppUtils.printQueryStringBuilder
//            )
        }
        return commandPrinter
    }

    /**
     * Function to test print height statically
     */
    @JvmStatic
    fun  setExtraXYForPrintHeightTesting(commandPrinter : java.lang.StringBuilder) : java.lang.StringBuilder
    {
        var yAxis = 1200
        val valueToPrint = "PrintHeight"
        for (i in 1..60) {
            yAxis += 40
            commandPrinter.append("TEXT 7 0 5" + " " + yAxis + " " + (valueToPrint+":$yAxis") + " \r\n")
        }

        isYAxisHeight = yAxis

        return commandPrinter
    }


    @JvmStatic
    fun  setXYforPrint(mList : List<VehicleListModel>, position : Int, sectionName : String,
                       commandPrinter : java.lang.StringBuilder) : java.lang.StringBuilder
    {

        val mObject : VehicleListModel = mList.get(position)

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)) {
            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + mObject.mAxisX + " " + mObject.mAxisY + " " + mObject.offTypeFirst + " \r\n")

                if (LogUtil.isEnableCommandBasedFacsimile) {
                    drawableElements.add(
                        DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = mObject.offTypeFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        )
                    )
                }

                addYAxisToSet(
                    mObject.mAxisY,
                    getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety())
                )
            }
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            mObject.sectionType = SectionType.BODY
//            mObject.mTextAlignment = TextAlignmentForCommandPrint.LEFT
            commandPrinter.append(ObjectMapperProvider.toJson(mObject))
            commandPrinter.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
            LogUtil.printLog("==>",ObjectMapperProvider.toJson(mObject))

        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)) {
            val valueX = (mObject.offNameFirst!!.length * 15)
            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + mObject.mAxisX + " " + mObject.mAxisY + " " + mObject.offNameFirst + " \r\n")

                if (LogUtil.isEnableCommandBasedFacsimile) {
                    drawableElements.add(
                        DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        )
                    )
                }

                addYAxisToSet(mObject.mAxisY,
                    getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                if (mObject.offTypeFirst!!.toString().length > 22) {

                    val array = mObject.offTypeFirst!!.split(" "); // split by space
                    var multilineFirstText: String = ""
                    var multilineSecondText: String = ""
                    for (value in array) {
                        if (multilineFirstText.length < 23) {
                            multilineFirstText = multilineFirstText.plus(value) + " "
                        } else {
                            multilineSecondText = multilineSecondText.plus(value) + " "
                        }
                    }
                    val isLableEmptyThenY =
                        if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY)

                    val xAxisValue = mObject.mAxisX + valueX

                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisValue + " " + isLableEmptyThenY + " " + multilineFirstText + " \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xAxisValue, y = isLableEmptyThenY,
                                text = multilineFirstText,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            )
                        )
                    }

                    addYAxisToSet(isLableEmptyThenY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    if (multilineSecondText!!.isNotEmpty()) {
                        val yAxis = isLableEmptyThenY + 40
                        val xAxisValueForSecondLine = mObject.mAxisX + valueX
                        commandPrinter.append(
                            "TEXT 7 " + mObject.mFontSizeInt + " " + xAxisValueForSecondLine + " " + yAxis + " " + multilineSecondText + " \r\n"
                        )

                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(
                                DrawableElement.Text(
                                    x = xAxisValue, y = yAxis,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                )
                            )
                        }

                        addYAxisToSet(yAxis,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }
                }else {
                    val xAxisValueFinal = mObject.mAxisX + valueX
                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisValueFinal + " " + mObject.mAxisY + " " + mObject.offTypeFirst + " \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xAxisValueFinal, y = mObject.mAxisY,
                                text = mObject.offTypeFirst.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            )
                        )
                    }
                    addYAxisToSet(mObject.mAxisY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                }
            }
        } else if(
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)){

            //We need to change first line max length as per the site it belongs
            //So , here we added a condition to different site if requiremed
            var firstLineMaxLength = 24
            var secondLineMaxLength = 24
            var thirdLineMaxLength = 24
            if (
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                    ignoreCase = true
                )||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DANVILLE_VA,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CAMDEN,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SOUTH_LAKE,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_WINPARK_TX,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_PARKX,
                    ignoreCase = true
                )
            ) {
                firstLineMaxLength = 20
                secondLineMaxLength = 24
                thirdLineMaxLength = 24
            }else if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_OXFORD,
                    ignoreCase = true
                )
            ) {
                firstLineMaxLength = 24
                secondLineMaxLength = 24
                thirdLineMaxLength = 24
            }else if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                    ignoreCase = true
                )
            ) {
                firstLineMaxLength = 28
                secondLineMaxLength = 28
                thirdLineMaxLength = 28
            }

            if (mObject.offTypeFirst != null && mObject.offNameFirst!!.isNotEmpty()) {
                val finalOffNameFirst = mObject.offNameFirst?.replace(Constants.COMMA,"")
                commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + mObject.mAxisX + " " + mObject.mAxisY + " " + finalOffNameFirst + " \r\n")
                if (LogUtil.isEnableCommandBasedFacsimile) {
                    drawableElements.add(
                        DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = finalOffNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        )
                    )
                }
                addYAxisToSet(mObject.mAxisY,
                    getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                if (mObject.offTypeFirst!!.toString().length > 20) {

                    val array = mObject.offTypeFirst!!.split(" ") // split by space
                    val maxLines = 6
                    val maxLengths = listOf(
                        firstLineMaxLength,
                        secondLineMaxLength,
                        thirdLineMaxLength,
                        secondLineMaxLength,
                        secondLineMaxLength,  // define this if needed
                        secondLineMaxLength   // define this if needed
                    )

                    val multilineTexts = MutableList(maxLines) { "" }

                    var currentLine = 0
                    for (value in array) {
                        if (currentLine >= maxLines) break
                        if (multilineTexts[currentLine].length + value.length + 1 <= maxLengths[currentLine]) {
                            multilineTexts[currentLine] += "$value "
                        } else {
                            currentLine++
                            if (currentLine >= maxLines) break
                            multilineTexts[currentLine] += "$value "
                        }
                    }

                    for (i in multilineTexts.indices) {
                        val lineText = multilineTexts[i].trim()
                        if (lineText.isNotEmpty()) {
                            val xAxis = setValueXWithColon(mObject, i + 1)
                            val yAxis = getYAxisForMultiline(mObject.mAxisY, i + 1)

                            val textToPrint = if (i == 0 && mObject.mHorizontalColon != 10) ": ${lineText.trimStart()}" else lineText.trimStart()
                            commandPrinter.append("TEXT 7 ${mObject.mFontSizeInt} $xAxis $yAxis $textToPrint \r\n")

                            if (LogUtil.isEnableCommandBasedFacsimile) {
                                drawableElements.add(
                                    DrawableElement.Text(
                                        x = xAxis,
                                        y = yAxis,
                                        text = textToPrint,
                                        textColor = Color.BLACK,
                                        textFont = 7,
                                        textSize = mObject.mFontSizeInt.nullSafety()
                                    )
                                )
                            }

                            addYAxisToSet(
                                yAxis,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety())
                            )
                        }
                    }
                }else {
                    addYAxisToSet(mObject.mAxisY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    //val finalValue = mObject.offTypeFirst?.replace(".","")
                    val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                    if (
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)){
                        val xAxis =
                            (if (mObject.mHorizontalColon == 10) (mObject.mAxisX)
                            else if (mObject.mHorizontalColon == 0) (mObject.mAxisX + 100)
                            else if (mObject.mHorizontalColon == 1) (mObject.mAxisX + 200)
                            else if (mObject.mHorizontalColon == 2) (mObject.mAxisX + 225)
                            else if (mObject.mHorizontalColon == 3) (mObject.mAxisX + 275)
                            else (mObject.mAxisX + 300))

                        val text = (if(mObject.mHorizontalColon == 10) " "  else " : ") + finalValue
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxis + " " + mObject.mAxisY + text + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(
                                DrawableElement.Text(
                                    x = xAxis, y = mObject.mAxisY,
                                    text = text,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                )
                            )
                        }

                    }else{
                        //val finalValue = mObject.offTypeFirst?.replace(".","")
                        val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                        val xAxis = when (mObject.mHorizontalColon) {
                            10 -> mObject.mAxisX
                            0  -> mObject.mAxisX + 100
                            1  -> mObject.mAxisX + 150
                            2  -> mObject.mAxisX + 250
                            else -> mObject.mAxisX + 285
                        }
                        val text = (if(mObject.mHorizontalColon == 10) " "  else " : ") + finalValue

                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxis + " " + mObject.mAxisY + text + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(
                                DrawableElement.Text(
                                    x = xAxis, y = mObject.mAxisY,
                                    text = text,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                )
                            )
                        }
                    }
                }
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)){
            if (mObject.offTypeFirst != null && mObject.offNameFirst!!.isNotEmpty()) {
                val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")

                if (LogUtil.isEnableCommandBasedFacsimile) {
                    drawableElements.add(
                        DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        )
                    )
                }
                addYAxisToSet(mObject.mAxisY,
                    getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                if (mObject.offTypeFirst!!.toString().length > 16) {

                    val array = mObject.offTypeFirst!!.split(" "); // split by space
                    var multilineFirstText: String = ""
                    var multilineSecondText: String = ""
                    var multilineThirdText: String = ""
                    for (value in array) {
                        if (multilineFirstText.length < 20) {
                            multilineFirstText = multilineFirstText.plus(value) + " "
                        } else if (multilineSecondText.length < 20) {
                            multilineSecondText = multilineSecondText.plus(value) + " "
                        } else {
                            multilineThirdText = multilineThirdText.plus(value) + " "
                        }
                    }
                    val xAxisLineOne = setValueXWithColon(mObject,1)
                    val yAxisLineOne = getYAxisForMultiline(mObject.mAxisY, 1)
                    val textLineOne = if(mObject.mHorizontalColon == 10)" "  else " : "+ multilineFirstText
                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisLineOne + " " + yAxisLineOne + textLineOne + " \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xAxisLineOne, y = yAxisLineOne,
                                text = textLineOne,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            )
                        )
                    }

                    addYAxisToSet(yAxisLineOne,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                    if (multilineSecondText!!.isNotEmpty()) {
                        val xAxisLineTwo = (if (mObject.mHorizontalColon == 0) (mObject.mAxisX +  145) else if (mObject.mHorizontalColon == 1) (mObject.mAxisX +  145) else (mObject.mAxisX +  250))
                        val yAxisLineTwo = getYAxisForMultiline(mObject.mAxisY, 2)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisLineTwo + " " + yAxisLineTwo + " " + multilineSecondText+ " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(
                                DrawableElement.Text(
                                    x = xAxisLineTwo, y = yAxisLineTwo,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                )
                            )
                        }

                        addYAxisToSet(yAxisLineTwo,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }
                    if (multilineThirdText!!.isNotEmpty()) {
                        val xAxisLineThree = (if (mObject.mHorizontalColon == 0) (mObject.mAxisX +  145) else if (mObject.mHorizontalColon == 1) (mObject.mAxisX +  145) else (mObject.mAxisX +  250))
                        val yAxisLineThree = getYAxisForMultiline(mObject.mAxisY, 3)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisLineThree + " " + yAxisLineThree + " " + multilineThirdText+ " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile) {
                            drawableElements.add(
                                DrawableElement.Text(
                                    x = xAxisLineThree, y = yAxisLineThree,
                                    text = multilineThirdText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                )
                            )
                        }

                        addYAxisToSet(yAxisLineThree,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }
                } else {
                    val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                    val xAxisFinal = (if (mObject.mHorizontalColon == 0) (mObject.mAxisX +  145) else if (mObject.mHorizontalColon == 1) (mObject.mAxisX +  145) else (mObject.mAxisX +  245))
                    val textFinal = (if(mObject.mHorizontalColon == 10) " "  else " : ") + finalValue
                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisFinal + " " + mObject.mAxisY + textFinal + " \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = xAxisFinal, y = mObject.mAxisY,
                                text = textFinal,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            )
                        )
                    }

                    addYAxisToSet(mObject.mAxisY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                }
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)) {
            var  txtLabel = ""
            var  txtValue = ""
            var lebelX2 = 20.0
            var valueX2 = 20.0

            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if(mObject.offNameFirst !=null && mObject.offNameFirst!!.isNotEmpty()) {
                    lebelX2 = spaceCountForWidth(mList, position,Constants.END_X_FOR_BOX_NOLA)
                    if(sectionName.equals("comment",ignoreCase = true))
                    {
//                        val xAxisForComment = (mObject.mAxisX-2)
//                        val yAxisForComment = mObject.mAxisY + 60
//                        commandPrinter.append(
//                            "BOX " + xAxisForComment + " " + (mObject.mAxisY) + " " +lebelX2 + " " + yAxisForComment + " 1 \r\n")
//                        addYAxisToSet(yAxisForComment, 0)
//
//                        if (LogUtil.isEnableCommandBasedFacsimile){
//                            drawableElements.add(DrawableElement.Rectangle(
//                                startX = xAxisForComment, startY = mObject.mAxisY, endX = lebelX2, endY = yAxisForComment,
//                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
//                            ))
//                        }
                    }

//                    val xAxisForBox = mObject.mAxisX-2
//                    val yEndAxisForBox = mObject.mAxisY + 28
//
//                    commandPrinter.append(
//                        "BOX " + xAxisForBox + " " + (mObject.mAxisY) + " " + lebelX2 + " " + yEndAxisForBox + " 1 \r\n")
//                    addYAxisToSet(yEndAxisForBox, 0)
//
//                    if (LogUtil.isEnableCommandBasedFacsimile){
//                        drawableElements.add(DrawableElement.Rectangle(
//                            startX = xAxisForBox, startY = mObject.mAxisY, endX = lebelX2, endY = yEndAxisForBox,
//                            fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
//                        ))
//                    }
                }

                val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                txtLabel += finalLabel//.nullSafety().textAlignInCenter(mObject.mAxisX,lebelX2,10)

                val xAxisText = (mObject.mAxisX+2)
                val yAxisText = (mObject.mAxisY+2)
                commandPrinter.append("TEXT 0 2 " + xAxisText + " " + yAxisText + " " + txtLabel + " \r\n")
                addYAxisToSet(yAxisText, getHeightBasedOnFont(2))

                if (LogUtil.isEnableCommandBasedFacsimile){
                    drawableElements.add(DrawableElement.Text(
                        x = xAxisText, y = yAxisText,
                        text = txtLabel,
                        textColor = Color.BLACK,
                        textFont = 0,
                        textSize = 2
                    ))
                }

                if(mObject.offTypeFirst!!.toString().contains("#")){
                    val xPosition = (mObject.mAxisX + 3)
                    var yPosition=mObject.mAxisY + 8
                    mObject.offTypeFirst!!.toString().split("#").toTypedArray().forEach {
                        commandPrinter.append(
                            "TEXT 0 " + mObject.mFontSizeInt + " " + xPosition + " " + (yPosition) + " " + it + " \r\n"
                        )
                        addYAxisToSet(yPosition,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xPosition, y = yPosition,
                                text = it,
                                textColor = Color.BLACK,
                                textFont = 0,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        yPosition = yPosition + 20
                    }
                    if(mObject.mNoBox == 0) {
                        val xAxisStart = (mObject.mAxisX - 2)
                        val yAxisEnd = spaceCountForHeight(mList, position)
                        val xAxisEnd = spaceCountForWidth(
                            mList,
                            position,Constants.END_X_FOR_BOX_NOLA
                        )
//                        commandPrinter.append(
//                            "BOX " + xAxisStart + " " + (yPosition) + " " + xAxisEnd + " " + yAxisEnd + " 1 \r\n"
//                        )
//                        addYAxisToSet(yAxisEnd, 0)
//
//                        if (LogUtil.isEnableCommandBasedFacsimile){
//                            drawableElements.add(DrawableElement.Rectangle(
//                                startX = xAxisStart, startY = yPosition, endX = xAxisEnd, endY = yAxisEnd,
//                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
//                            ))
//                        }
                    }
                }else {
                    //Multi line support text
                    if (mObject.offTypeFirst!!.toString().length > 35) {

                        val array = mObject.offTypeFirst!!.split(" "); // split by space
                        var multilineFirstText: String = ""
                        var multilineSecondText: String = ""
                        for (value in array) {
                            if (multilineFirstText.length < 36) {
                                multilineFirstText = multilineFirstText.plus(value) + " "
                            } else {
                                multilineSecondText = multilineSecondText.plus(value) + " "
                            }
                        }
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 17)
                        var mBoxY = isLableEmptyThenY
                        val xAxisFirstLine = (mObject.mAxisX+2)
                        val yAxisFirstLine = (isLableEmptyThenY+8)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisFirstLine + " " + yAxisFirstLine + " " + multilineFirstText + " \r\n")
                        addYAxisToSet(yAxisFirstLine,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisFirstLine, y = yAxisFirstLine,
                                text = multilineFirstText,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        if (multilineSecondText!!.isNotEmpty()) {
                            val xAxisSecondLine = (mObject.mAxisX+2)
                            val yAxisSecondLine = (isLableEmptyThenY + 20)

                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + xAxisSecondLine + " " + yAxisSecondLine + " " + multilineSecondText + " \r\n"
                            )
                            addYAxisToSet(yAxisSecondLine,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = xAxisSecondLine, y = yAxisSecondLine,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }

                            mBoxY = mBoxY + 20
                        }
                        if(mObject.mNoBox == 0) {
                            val xAxisStart = (mObject.mAxisX-2)
                            val yAxisEnd = spaceCountForHeight(mList, position)
                            val xAxisEnd = spaceCountForWidth(
                                mList,
                                position,Constants.END_X_FOR_BOX_NOLA
                            )
//                            commandPrinter.append(
//                                "BOX " + xAxisStart + " " + (isLableEmptyThenY) + " " + xAxisEnd + " " + yAxisEnd + " 1 \r\n"
//                            )
//                            addYAxisToSet(yAxisEnd, 0)
//
//                            if (LogUtil.isEnableCommandBasedFacsimile){
//                                drawableElements.add(DrawableElement.Rectangle(
//                                    startX = xAxisStart, startY = isLableEmptyThenY, endX = xAxisEnd, endY = yAxisEnd,
//                                    fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
//                                ))
//                            }
                        }

                    } else {
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 28)
                        txtValue = mObject.offTypeFirst.nullSafety()
                        valueX2 = ((spaceCountForWidth(mList, position,Constants.END_X_FOR_BOX_NOLA)))
                        if(mObject.mNoBox == 0) {
                            val xAxisStart = mObject.mAxisX-2
                            val yAxisEnd = spaceCountForHeight(mList, position)
                            txtValue = ""
//                            commandPrinter.append(
//                                "BOX " + xAxisStart + " " + (isLableEmptyThenY) + " " + valueX2 + " " + yAxisEnd + " 1 \r\n"
//                            )
//                            addYAxisToSet(yAxisEnd, 0)
//                            if (LogUtil.isEnableCommandBasedFacsimile){
//                                drawableElements.add(DrawableElement.Rectangle(
//                                    startX = xAxisStart, startY = isLableEmptyThenY, endX = valueX2, endY = yAxisEnd,
//                                    fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
//                                ))
//                            }
                            val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                            txtValue = finalValue!! //+= finalValue.nullSafety().textAlignInCenter(mObject.mAxisX,valueX2,14)
                        }


                        val xAxisFinalText = mObject.mAxisX+2
                        val yAxisFinalText = isLableEmptyThenY+8
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisFinalText + " " + yAxisFinalText + " " + txtValue + " \r\n")
                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisFinalText, y = yAxisFinalText,
                                text = txtValue,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }
                        addYAxisToSet(yAxisFinalText,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }
                }
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS_OLD,ignoreCase = true)) {
            var  txtLabel = ""
            var  txtValue = ""
            var lebelX2 = 20.0
            var valueX2 = 20.0

            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if(mObject.offNameFirst !=null && mObject.offNameFirst!!.isNotEmpty()) {
                    lebelX2 = spaceCountForWidth(mList, position,Constants.END_X_FOR_BOX_NOLA)
                    if(sectionName.equals("comment",ignoreCase = true))
                    {
                        val xAxisForComment = (mObject.mAxisX-2)
                        val yAxisForComment = mObject.mAxisY + 60
                        commandPrinter.append(
                            "BOX " + xAxisForComment + " " + (mObject.mAxisY) + " " +lebelX2 + " " + yAxisForComment + " 1 \r\n")
                        addYAxisToSet(yAxisForComment, 0)

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Rectangle(
                                startX = xAxisForComment, startY = mObject.mAxisY, endX = lebelX2, endY = yAxisForComment,
                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                            ))
                        }
                    }

                    val xAxisForBox = mObject.mAxisX-2
                    val yEndAxisForBox = mObject.mAxisY + 28

                    commandPrinter.append(
                        "BOX " + xAxisForBox + " " + (mObject.mAxisY) + " " + lebelX2 + " " + yEndAxisForBox + " 1 \r\n")
                    addYAxisToSet(yEndAxisForBox, 0)

                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Rectangle(
                            startX = xAxisForBox, startY = mObject.mAxisY, endX = lebelX2, endY = yEndAxisForBox,
                            fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                        ))
                    }
                }

                val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                txtLabel += finalLabel.nullSafety().textAlignInCenter(mObject.mAxisX,lebelX2,10)

                val xAxisText = (mObject.mAxisX+2)
                val yAxisText = (mObject.mAxisY+2)
                commandPrinter.append("TEXT 0 2 " + xAxisText + " " + yAxisText + " " + txtLabel + " \r\n")
                addYAxisToSet(yAxisText, getHeightBasedOnFont(2))

                if (LogUtil.isEnableCommandBasedFacsimile){
                    drawableElements.add(DrawableElement.Text(
                        x = xAxisText, y = yAxisText,
                        text = txtLabel,
                        textColor = Color.BLACK,
                        textFont = 0,
                        textSize = 2
                    ))
                }

                if(mObject.offTypeFirst!!.toString().contains("#")){
                    val xPosition = (mObject.mAxisX + 3)
                    var yPosition=mObject.mAxisY + 5
                    mObject.offTypeFirst!!.toString().split("#").toTypedArray().forEach {
                        commandPrinter.append(
                            "TEXT 0 " + mObject.mFontSizeInt + " " + xPosition + " " + (yPosition) + " " + it + " \r\n"
                        )
                        addYAxisToSet(yPosition,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xPosition, y = yPosition,
                                text = it,
                                textColor = Color.BLACK,
                                textFont = 0,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        yPosition = yPosition + 20
                    }
                    if(mObject.mNoBox == 0) {
                        val xAxisStart = (mObject.mAxisX - 2)
                        val yAxisEnd = spaceCountForHeight(mList, position)
                        val xAxisEnd = spaceCountForWidth(
                            mList,
                            position,Constants.END_X_FOR_BOX_NOLA
                        )
                        commandPrinter.append(
                            "BOX " + xAxisStart + " " + (yPosition) + " " + xAxisEnd + " " + yAxisEnd + " 1 \r\n"
                        )
                        addYAxisToSet(yAxisEnd, 0)

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Rectangle(
                                startX = xAxisStart, startY = yPosition, endX = xAxisEnd, endY = yAxisEnd,
                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                            ))
                        }
                    }
                }else {
                    //Multi line support text
                    if (mObject.offTypeFirst!!.toString().length > 35) {

                        val array = mObject.offTypeFirst!!.split(" "); // split by space
                        var multilineFirstText: String = ""
                        var multilineSecondText: String = ""
                        for (value in array) {
                            if (multilineFirstText.length < 36) {
                                multilineFirstText = multilineFirstText.plus(value) + " "
                            } else {
                                multilineSecondText = multilineSecondText.plus(value) + " "
                            }
                        }
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 17)
                        var mBoxY = isLableEmptyThenY
                        val xAxisFirstLine = (mObject.mAxisX+2)
                        val yAxisFirstLine = (isLableEmptyThenY+3)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisFirstLine + " " + yAxisFirstLine + " " + multilineFirstText + " \r\n")
                        addYAxisToSet(yAxisFirstLine,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisFirstLine, y = yAxisFirstLine,
                                text = multilineFirstText,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        if (multilineSecondText!!.isNotEmpty()) {
                            val xAxisSecondLine = (mObject.mAxisX+2)
                            val yAxisSecondLine = (isLableEmptyThenY + 20)

                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + xAxisSecondLine + " " + yAxisSecondLine + " " + multilineSecondText + " \r\n"
                            )
                            addYAxisToSet(yAxisSecondLine,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = xAxisSecondLine, y = yAxisSecondLine,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }

                            mBoxY = mBoxY + 20
                        }
                        if(mObject.mNoBox == 0) {
                            val xAxisStart = (mObject.mAxisX-2)
                            val yAxisEnd = spaceCountForHeight(mList, position)
                            val xAxisEnd = spaceCountForWidth(
                                mList,
                                position,Constants.END_X_FOR_BOX_NOLA
                            )
                            commandPrinter.append(
                                "BOX " + xAxisStart + " " + (isLableEmptyThenY) + " " + xAxisEnd + " " + yAxisEnd + " 1 \r\n"
                            )
                            addYAxisToSet(yAxisEnd, 0)

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Rectangle(
                                    startX = xAxisStart, startY = isLableEmptyThenY, endX = xAxisEnd, endY = yAxisEnd,
                                    fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                                ))
                            }
                        }

                    } else {
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 28)
                        txtValue = mObject.offTypeFirst.nullSafety()
                        valueX2 = ((spaceCountForWidth(mList, position,Constants.END_X_FOR_BOX_NOLA)))
                        if(mObject.mNoBox == 0) {
                            val xAxisStart = mObject.mAxisX-2
                            val yAxisEnd = spaceCountForHeight(mList, position)
                            txtValue = ""
                            commandPrinter.append(
                                "BOX " + xAxisStart + " " + (isLableEmptyThenY) + " " + valueX2 + " " + yAxisEnd + " 1 \r\n"
                            )
                            addYAxisToSet(yAxisEnd, 0)
                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Rectangle(
                                    startX = xAxisStart, startY = isLableEmptyThenY, endX = valueX2, endY = yAxisEnd,
                                    fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                                ))
                            }
                            val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                            txtValue += finalValue.nullSafety().textAlignInCenter(mObject.mAxisX,valueX2,14)
                        }


                        val xAxisFinalText = mObject.mAxisX+2
                        val yAxisFinalText = isLableEmptyThenY+3
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisFinalText + " " + yAxisFinalText + " " + txtValue + " \r\n")
                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisFinalText, y = yAxisFinalText,
                                text = txtValue,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }
                        addYAxisToSet(yAxisFinalText,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }
                }
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)) {
            var  txtLabel = ""
            var  txtValue = ""
            var lebelX2 = 20.0
            var valueX2 = 20.0

            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if(mObject.offNameFirst !=null && mObject.offNameFirst!!.isNotEmpty()) {
                    lebelX2 = ((spaceCountForWidth(mList, position,Constants.END_X_FOR_BOX_BOSTON)))
                    if(sectionName.equals("comment",ignoreCase = true))
                    {
                        val xAxisStartForComment = (mObject.mAxisX-2)
                        val yAxisStartForComment = (mObject.mAxisY)
                        val yAxisForComment = mObject.mAxisY + 60
                        commandPrinter.append(
                            "BOX " + xAxisStartForComment + " " + yAxisStartForComment + " " +lebelX2 + " " + yAxisForComment + " 1 \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Rectangle(
                                startX = xAxisStartForComment, startY = yAxisStartForComment, endX = lebelX2, endY = yAxisForComment,
                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                            ))
                        }

                        addYAxisToSet(yAxisForComment, 0)
                    }

                    val xAxisStartForBox = (mObject.mAxisX-2)
                    val yAxisStartForBox = (mObject.mAxisY)
                    val yAxisEndForBox = mObject.mAxisY + 28

                    commandPrinter.append(
                        "BOX " + xAxisStartForBox + " " + yAxisStartForBox + " " + lebelX2 + " " + yAxisEndForBox + " 1 \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Rectangle(
                            startX = xAxisStartForBox, startY = yAxisStartForBox, endX = lebelX2, endY = yAxisEndForBox,
                            fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                        ))
                    }

                    addYAxisToSet(mObject.mAxisY + 28, 0)
                }

                val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                txtLabel += finalLabel.nullSafety().textAlignInCenter(mObject.mAxisX,lebelX2,10)

                val xAxisForText = (mObject.mAxisX+2)
                val yAxisForText = (mObject.mAxisY+2)

                commandPrinter.append("TEXT 0 2 " + xAxisForText + " " + yAxisForText + " " + txtLabel + " \r\n")

                if (LogUtil.isEnableCommandBasedFacsimile){
                    drawableElements.add(DrawableElement.Text(
                        x = xAxisForText, y = yAxisForText,
                        text = txtLabel,
                        textColor = Color.BLACK,
                        textFont = 0,
                        textSize = 2
                    ))
                }

                addYAxisToSet(yAxisForText, getHeightBasedOnFont(2))

                if(mObject.offTypeFirst!!.toString().contains("#")){
                    var yPosition=mObject.mAxisY + 5
                    mObject.offTypeFirst!!.toString().split("#").toTypedArray().forEach {

                        val xAxisForOffTypeText = (mObject.mAxisX + 3)
                        commandPrinter.append(
                            "TEXT 0 " + mObject.mFontSizeInt + " " + xAxisForOffTypeText + " " + (yPosition) + " " + it + " \r\n"
                        )

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisForOffTypeText, y = yPosition,
                                text = it,
                                textColor = Color.BLACK,
                                textFont = 0,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(yPosition,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                        yPosition = yPosition + 20
                    }
                    if(mObject.mNoBox == 0) {
                        val xAxisStartBox = (mObject.mAxisX - 2)
                        val xAxisEndBox = spaceCountForWidth(
                            mList,
                            position,Constants.END_X_FOR_BOX_BOSTON
                        )
                        val yAxisEnd = spaceCountForHeight(mList, position)
                        commandPrinter.append(
                            "BOX " + xAxisStartBox + " " + (yPosition) + " " + xAxisEndBox + " " + yAxisEnd + " 1 \r\n"
                        )

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Rectangle(
                                startX = xAxisStartBox, startY = yPosition, endX = xAxisEndBox, endY = yAxisEnd,
                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                            ))
                        }

                        addYAxisToSet(yAxisEnd, 0)
                    }
                }else {
                    //Multi line support text
                    if (mObject.offTypeFirst!!.toString().length > 35) {

                        val array = mObject.offTypeFirst!!.split(" "); // split by space
                        var multilineFirstText: String = ""
                        var multilineSecondText: String = ""
                        for (value in array) {
                            if (multilineFirstText.length < 36) {
                                multilineFirstText = multilineFirstText.plus(value) + " "
                            } else {
                                multilineSecondText = multilineSecondText.plus(value) + " "
                            }
                        }
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 17)
                        var mBoxY = isLableEmptyThenY
                        val xAxisLineOne = (mObject.mAxisX+2)
                        val yAxisLineOne = (isLableEmptyThenY+3)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisLineOne + " " + yAxisLineOne + " " + multilineFirstText + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisLineOne, y = yAxisLineOne,
                                text = multilineFirstText,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(yAxisLineOne,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        if (multilineSecondText!!.isNotEmpty()) {
                            val xAxisLineTwo = (mObject.mAxisX+2)
                            val yAxisLineTwo = (isLableEmptyThenY + 20)

                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + xAxisLineTwo + " " + yAxisLineTwo + " " + multilineSecondText + " \r\n"
                            )

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = xAxisLineTwo, y = yAxisLineTwo,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }

                            addYAxisToSet(yAxisLineTwo,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                            mBoxY = mBoxY + 20
                        }
                        if(mObject.mNoBox == 0) {
                            val xAxisStart = (mObject.mAxisX-2)
                            val xAxisEnd = spaceCountForWidth(
                                mList,
                                position,Constants.END_X_FOR_BOX_BOSTON
                            )

                            val yAxisEnd = spaceCountForHeight(mList, position)

                            commandPrinter.append(
                                "BOX " + xAxisStart + " " + (isLableEmptyThenY) + " " + xAxisEnd + " " + yAxisEnd + " 1 \r\n"
                            )

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Rectangle(
                                    startX = xAxisStart, startY = isLableEmptyThenY, endX = xAxisEnd, endY = yAxisEnd,
                                    fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                                ))
                            }

                            addYAxisToSet(yAxisEnd, 0)
                        }

                    } else {
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 28)
                        val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                        txtValue = finalValue.nullSafety()
                        valueX2 = ((spaceCountForWidth(mList, position,Constants.END_X_FOR_BOX_BOSTON)))

                        val xAxisStart = (mObject.mAxisX-2)
                        if(mObject.mNoBox == 0) {
                            val yAxisEnd = spaceCountForHeight(mList, position)
                            txtValue = ""
                            commandPrinter.append(
                                "BOX " + xAxisStart + " " + (isLableEmptyThenY) + " " + valueX2 + " " + yAxisEnd + " 1 \r\n"
                            )

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Rectangle(
                                    startX = xAxisStart, startY = isLableEmptyThenY, endX = valueX2, endY = yAxisEnd,
                                    fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                                ))
                            }

                            addYAxisToSet(yAxisEnd, 0)
                            txtValue += mObject.offTypeFirst.nullSafety().textAlignInCenter(mObject.mAxisX,valueX2,14)
                        }

                        val xAxisText = (mObject.mAxisX+2)
                        val yAxisText = (isLableEmptyThenY+3)

                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisText + " " + yAxisText + " " + txtValue + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisText, y = yAxisText,
                                text = txtValue,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(yAxisText,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }
                }
            }
        }
        else if(
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
        ) {
            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if (Horizontal == 1) {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 "+ mObject.mFontSizeInt +" "+ mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")

                    val xAxisForOffTypeFirst = (if(mObject.mFontSizeInt==0||mObject.mFontSizeInt==1) (mObject.mAxisX +(mObject.offNameFirst!!.length*12)) else (mObject.mAxisX + (mObject.offNameFirst!!.length*15)))
                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisForOffTypeFirst + " " + mObject.mAxisY + " " + finalValue + " \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        ))

                        drawableElements.add(DrawableElement.Text(
                            x = xAxisForOffTypeFirst, y = mObject.mAxisY,
                            text = finalValue.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        ))
                    }

                    addYAxisToSet(mObject.mAxisY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                } else {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 0 " + mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = mObject.mAxisX, y = mObject.mAxisY,
                                text = mObject.offNameFirst.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = 0
                            )
                        )
                    }

                    addYAxisToSet(mObject.mAxisY, getHeightBasedOnFont(0))
                    //Multi line support text
                    var maxOffTypeFirstLimit = 35
                    var multiLineFirstLineLimit = 40

                    if (sectionName == PRINT_SECTION_VIOLATION){
                        maxOffTypeFirstLimit = 27
                        multiLineFirstLineLimit = 28
                    }

                    if (mObject.offTypeFirst!!.toString().length > maxOffTypeFirstLimit) {

                        val array = mObject.offTypeFirst!!.split(" "); // split by space
                        var multilineFirstText: String = ""
                        var multilineSecondText: String = ""
                        for (value in array) {
                            if (multilineFirstText.length < multiLineFirstLineLimit) {
                                multilineFirstText = multilineFirstText.plus(value) + " "
                            } else {
                                multilineSecondText = multilineSecondText.plus(value) + " "
                            }
                        }
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 22)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + isLableEmptyThenY + " " + multilineFirstText + " \r\n")
                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = mObject.mAxisX, y = isLableEmptyThenY,
                                text = multilineFirstText,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }
                        addYAxisToSet(isLableEmptyThenY,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        if (multilineSecondText!!.isNotEmpty()) {
                            val yAxisForLineTwo = (isLableEmptyThenY + 26)
                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + yAxisForLineTwo + " " + multilineSecondText + " \r\n"
                            )

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = mObject.mAxisX, y = yAxisForLineTwo,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }

                            addYAxisToSet(yAxisForLineTwo,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        }
                    } else {
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 25)
                        val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + isLableEmptyThenY + " " + finalValue + " \r\n")
                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = mObject.mAxisX, y = isLableEmptyThenY,
                                text = mObject.offTypeFirst.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(isLableEmptyThenY,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }

                }
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN,ignoreCase = true)){
            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if (Horizontal == 1) {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 "+ mObject.mFontSizeInt +" "+ mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")

                    val xAxisForOffTypeFirst = (if(mObject.mFontSizeInt==0||mObject.mFontSizeInt==1) (mObject.mAxisX +(mObject.offNameFirst!!.length*12)) else (mObject.mAxisX + (mObject.offNameFirst!!.length*15)))
                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisForOffTypeFirst + " " + mObject.mAxisY + " " + finalValue + " \r\n")
                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Text(
                                x = mObject.mAxisX, y = mObject.mAxisY,
                                text = mObject.offNameFirst.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            )
                        )

                        drawableElements.add(
                            DrawableElement.Text(
                                x = xAxisForOffTypeFirst, y = mObject.mAxisY,
                                text = mObject.offTypeFirst.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            )
                        )
                    }

                    addYAxisToSet(mObject.mAxisY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                } else {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 0 " + mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")
                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = 0
                        ))
                    }

                    addYAxisToSet(mObject.mAxisY, getHeightBasedOnFont(0))
                    //Multi line support text
                    if (!mObject.offTypeFirst.isNullOrEmpty() && mObject.offTypeFirst!!.length > 35) {

                        val words = mObject.offTypeFirst!!.split(" ")
                        val lines = mutableListOf("", "", "","","","","","","","")
                        var currentLine = 0
                        var mWordPerFont = 36
                        if(mObject.mFontSizeInt==0){
                            mWordPerFont = 40
                        }
                        for (word in words) {
                            val lineLimit = when (currentLine) {
                                0 -> mWordPerFont
                                else -> mWordPerFont
                            }

                            if ((lines[currentLine].length + word.length + 1) <= lineLimit) {
                                lines[currentLine] += "$word "
                            } else if (currentLine < 9) {
                                currentLine++
                                lines[currentLine] += "$word "
                            }
                        }

                        val baseY = if (mObject.offNameFirst.isNullOrEmpty()) mObject.mAxisY else mObject.mAxisY + 32

                        lines.forEachIndexed { index, line ->
                            if (line.trim().isNotEmpty()) {
                                val yOffset = baseY + (index * 32)
                                commandPrinter.append("TEXT 7 ${mObject.mFontSizeInt} ${mObject.mAxisX} $yOffset ${line.trim()} \r\n")

                                if (LogUtil.isEnableCommandBasedFacsimile){
                                    drawableElements.add(DrawableElement.Text(
                                        x = mObject.mAxisX, y = yOffset,
                                        text = line.trim(),
                                        textColor = Color.BLACK,
                                        textFont = 7,
                                        textSize = mObject.mFontSizeInt.nullSafety()
                                    ))
                                }

                                addYAxisToSet(yOffset,
                                    getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                            }
                        }
                    }else {
                        val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 26)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + isLableEmptyThenY + " " + finalValue + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = mObject.mAxisX, y = isLableEmptyThenY,
                                text = mObject.offTypeFirst.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(isLableEmptyThenY,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }
                }
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)){
            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if (Horizontal == 1) {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 "+ mObject.mFontSizeInt +" "+ mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")

                    val xAxisForOffTypeFirst = (if(mObject.mFontSizeInt==0||mObject.mFontSizeInt==1) (mObject.mAxisX +(mObject.offNameFirst!!.length*12)) else (mObject.mAxisX + (mObject.offNameFirst!!.length*15)))
                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisForOffTypeFirst + " " + mObject.mAxisY + " " + finalValue + " \r\n")
                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Text(
                            x = mObject.mAxisX , y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        ))

                        drawableElements.add(DrawableElement.Text(
                            x = xAxisForOffTypeFirst, y = mObject.mAxisY,
                            text = mObject.offTypeFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        ))
                    }

                    addYAxisToSet(mObject.mAxisY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                } else {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 0 " + mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")
                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = 0
                        ))
                    }
                    addYAxisToSet(mObject.mAxisY, 0)
                    //Multi line support text
                    if (mObject.offTypeFirst!!.toString().length > 35) {

                        val array = mObject.offTypeFirst!!.trim().split(" "); // split by space
                        var multilineFirstText: String = ""
                        var multilineSecondText: String = ""
                        var multilineThirdText: String = ""
                        for (value in array) {
                            if (multilineFirstText.length < 40) {
                                multilineFirstText = multilineFirstText.plus(value) + " "
                            } else if (multilineSecondText.length < 30) {
                                multilineSecondText = multilineSecondText.plus(value) + " "
                            } else {
                                multilineThirdText = multilineThirdText.plus(value) + " "
                            }
                        }
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 32)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + isLableEmptyThenY + " " + multilineFirstText + " \r\n")
                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = mObject.mAxisX, y = isLableEmptyThenY,
                                text = multilineFirstText,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }
                        addYAxisToSet(isLableEmptyThenY,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        if (multilineSecondText!!.isNotEmpty()) {
                            val yAxisForLineTwo = (isLableEmptyThenY + 32)
                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + yAxisForLineTwo + " " + multilineSecondText + " \r\n"
                            )
                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = mObject.mAxisX, y = yAxisForLineTwo,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }
                            addYAxisToSet(yAxisForLineTwo,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        }
                        if (multilineThirdText!!.isNotEmpty()) {
                            val yAxisForLineThree = (isLableEmptyThenY + 65)
                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + yAxisForLineThree + " " + multilineThirdText + " \r\n"
                            )
                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = mObject.mAxisX, y = yAxisForLineThree,
                                    text = multilineThirdText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }
                            addYAxisToSet(yAxisForLineThree,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        }
                    } else {
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 26)
                        val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + isLableEmptyThenY + " " + finalValue.toString().trim() + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = mObject.mAxisX, y = isLableEmptyThenY,
                                text = mObject.offTypeFirst.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(isLableEmptyThenY,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }

                }
            }
        }
        else {
            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if (Horizontal == 1) {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 "+ mObject.mFontSizeInt +" "+ mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")

                    val xAxisForOffTypeFirst = (if(mObject.mFontSizeInt==0||mObject.mFontSizeInt==1) (mObject.mAxisX +(mObject.offNameFirst!!.length*12)) else (mObject.mAxisX + (mObject.offNameFirst!!.length*15)))
                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisForOffTypeFirst + " " + mObject.mAxisY + " " + finalValue + " \r\n")
                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Text(
                            x = mObject.mAxisX , y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        ))

                        drawableElements.add(DrawableElement.Text(
                            x = xAxisForOffTypeFirst, y = mObject.mAxisY,
                            text = mObject.offTypeFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        ))
                    }

                    addYAxisToSet(mObject.mAxisY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                } else {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 0 " + mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")
                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = 0
                        ))
                    }
                    addYAxisToSet(mObject.mAxisY, 0)
                    //Multi line support text
                    /*if (mObject.offTypeFirst!!.toString().length > 35) {

                        val array = mObject.offTypeFirst!!.trim().split(" "); // split by space
                        var multilineFirstText: String = ""
                        var multilineSecondText: String = ""
                        var multilineThirdText: String = ""
                        for (value in array) {
                            if (multilineFirstText.length < 35) {
                                multilineFirstText = multilineFirstText.plus(value) + " "
                            } else if (multilineSecondText.length < 30) {
                                multilineSecondText = multilineSecondText.plus(value) + " "
                            } else {
                                multilineThirdText = multilineThirdText.plus(value) + " "
                            }
                        }
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 32)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + isLableEmptyThenY + " " + multilineFirstText + " \r\n")
                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = mObject.mAxisX, y = isLableEmptyThenY,
                                text = multilineFirstText,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }
                        addYAxisToSet(isLableEmptyThenY,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        if (multilineSecondText!!.isNotEmpty()) {
                            val yAxisForLineTwo = (isLableEmptyThenY + 32)
                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + yAxisForLineTwo + " " + multilineSecondText + " \r\n"
                            )
                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = mObject.mAxisX, y = yAxisForLineTwo,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }
                            addYAxisToSet(yAxisForLineTwo,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        }
                        if (multilineThirdText!!.isNotEmpty()) {
                            val yAxisForLineThree = (isLableEmptyThenY + 65)
                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + yAxisForLineThree + " " + multilineThirdText + " \r\n"
                            )
                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = mObject.mAxisX, y = yAxisForLineThree,
                                    text = multilineThirdText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }
                            addYAxisToSet(yAxisForLineThree,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        }
                    } */
                    if (!mObject.offTypeFirst.isNullOrEmpty() && mObject.offTypeFirst!!.length > 35) {

                        val words = mObject.offTypeFirst!!.split(" ")
                        val lines = mutableListOf("", "", "","","","","","","","")
                        var currentLine = 0
                        var mWordPerFont = 36
                        if(mObject.mFontSizeInt==0){
                            mWordPerFont = 40
                        }
                        for (word in words) {
                            val lineLimit = when (currentLine) {
                                0 -> mWordPerFont
                                else -> mWordPerFont
                            }

                            if ((lines[currentLine].length + word.length + 1) <= lineLimit) {
                                lines[currentLine] += "$word "
                            } else if (currentLine < 9) {
                                currentLine++
                                lines[currentLine] += "$word "
                            }
                        }

                        val baseY = if (mObject.offNameFirst.isNullOrEmpty()) mObject.mAxisY else mObject.mAxisY + 32

                        lines.forEachIndexed { index, line ->
                            if (line.trim().isNotEmpty()) {
                                val yOffset = baseY + (index * 32)
                                commandPrinter.append("TEXT 7 ${mObject.mFontSizeInt} ${mObject.mAxisX} $yOffset ${line.trim()} \r\n")

                                if (LogUtil.isEnableCommandBasedFacsimile){
                                    drawableElements.add(DrawableElement.Text(
                                        x = mObject.mAxisX, y = yOffset,
                                        text = line.trim(),
                                        textColor = Color.BLACK,
                                        textFont = 7,
                                        textSize = mObject.mFontSizeInt.nullSafety()
                                    ))
                                }

                                addYAxisToSet(yOffset,
                                    getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                            }
                        }
                    }
                    else {
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 26)
                        val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + isLableEmptyThenY + " " + finalValue.toString().trim() + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = mObject.mAxisX, y = isLableEmptyThenY,
                                text = finalValue.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(isLableEmptyThenY,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }

                }
            }
        }
        return commandPrinter;
    }
    @JvmStatic
    fun  setXYforMunicipalPrint(mList : List<VehicleListModel>, position : Int, sectionName : String,
                       commandPrinter : java.lang.StringBuilder) : java.lang.StringBuilder
    {

        val mObject : VehicleListModel = mList.get(position)
    if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA,ignoreCase = true)) {
            var  txtLabel = ""
            var  txtValue = ""
            var lebelX2 = 20.0
            var valueX2 = 20.0

            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if(mObject.offNameFirst !=null && mObject.offNameFirst!!.isNotEmpty()) {
                    lebelX2 = ((spaceCountForWidth(mList, position,Constants.END_X_FOR_BOX_BOSTON)))
                    if(sectionName.equals("comment",ignoreCase = true))
                    {
                        val xAxisStartForComment = (mObject.mAxisX-2)
                        val yAxisStartForComment = (mObject.mAxisY)
                        val yAxisForComment = mObject.mAxisY + 60
                        commandPrinter.append(
                            "BOX " + xAxisStartForComment + " " + yAxisStartForComment + " " +lebelX2 + " " + yAxisForComment + " 1 \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Rectangle(
                                startX = xAxisStartForComment, startY = yAxisStartForComment, endX = lebelX2, endY = yAxisForComment,
                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                            ))
                        }

                        addYAxisToSet(yAxisForComment, 0)
                    }

                    val xAxisStartForBox = (mObject.mAxisX-2)
                    val yAxisStartForBox = (mObject.mAxisY)
                    val yAxisEndForBox = mObject.mAxisY + 28

                    commandPrinter.append(
                        "BOX " + xAxisStartForBox + " " + yAxisStartForBox + " " + lebelX2 + " " + yAxisEndForBox + " 1 \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Rectangle(
                            startX = xAxisStartForBox, startY = yAxisStartForBox, endX = lebelX2, endY = yAxisEndForBox,
                            fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                        ))
                    }

                    addYAxisToSet(mObject.mAxisY + 28, 0)
                }

                val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                txtLabel += finalLabel.nullSafety().textAlignInCenter(mObject.mAxisX,lebelX2,10)

                val xAxisForText = (mObject.mAxisX+2)
                val yAxisForText = (mObject.mAxisY+2)

                commandPrinter.append("TEXT 0 2 " + xAxisForText + " " + yAxisForText + " " + txtLabel + " \r\n")

                if (LogUtil.isEnableCommandBasedFacsimile){
                    drawableElements.add(DrawableElement.Text(
                        x = xAxisForText, y = yAxisForText,
                        text = txtLabel,
                        textColor = Color.BLACK,
                        textFont = 0,
                        textSize = 2
                    ))
                }

                addYAxisToSet(yAxisForText, getHeightBasedOnFont(2))

                if(mObject.offTypeFirst!!.toString().contains("#")){
                    var yPosition=mObject.mAxisY + 5
                    mObject.offTypeFirst!!.toString().split("#").toTypedArray().forEach {

                        val xAxisForOffTypeText = (mObject.mAxisX + 3)
                        commandPrinter.append(
                            "TEXT 0 " + mObject.mFontSizeInt + " " + xAxisForOffTypeText + " " + (yPosition) + " " + it + " \r\n"
                        )

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisForOffTypeText, y = yPosition,
                                text = it,
                                textColor = Color.BLACK,
                                textFont = 0,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(yPosition,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))

                        yPosition = yPosition + 20
                    }
                    if(mObject.mNoBox == 0) {
                        val xAxisStartBox = (mObject.mAxisX - 2)
                        val xAxisEndBox = spaceCountForWidth(
                            mList,
                            position,Constants.END_X_FOR_BOX_BOSTON
                        )
                        val yAxisEnd = spaceCountForHeight(mList, position)
                        commandPrinter.append(
                            "BOX " + xAxisStartBox + " " + (yPosition) + " " + xAxisEndBox + " " + yAxisEnd + " 1 \r\n"
                        )

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Rectangle(
                                startX = xAxisStartBox, startY = yPosition, endX = xAxisEndBox, endY = yAxisEnd,
                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                            ))
                        }

                        addYAxisToSet(yAxisEnd, 0)
                    }
                }else {
                    //Multi line support text
                    if (mObject.offTypeFirst!!.toString().length > 35) {

                        val array = mObject.offTypeFirst!!.split(" "); // split by space
                        var multilineFirstText: String = ""
                        var multilineSecondText: String = ""
                        for (value in array) {
                            if (multilineFirstText.length < 36) {
                                multilineFirstText = multilineFirstText.plus(value) + " "
                            } else {
                                multilineSecondText = multilineSecondText.plus(value) + " "
                            }
                        }
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 17)
                        var mBoxY = isLableEmptyThenY
                        val xAxisLineOne = (mObject.mAxisX+2)
                        val yAxisLineOne = (isLableEmptyThenY+3)
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisLineOne + " " + yAxisLineOne + " " + multilineFirstText + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisLineOne, y = yAxisLineOne,
                                text = multilineFirstText,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(yAxisLineOne,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                        if (multilineSecondText!!.isNotEmpty()) {
                            val xAxisLineTwo = (mObject.mAxisX+2)
                            val yAxisLineTwo = (isLableEmptyThenY + 20)

                            commandPrinter.append(
                                "TEXT 7 " + mObject.mFontSizeInt + " " + xAxisLineTwo + " " + yAxisLineTwo + " " + multilineSecondText + " \r\n"
                            )

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Text(
                                    x = xAxisLineTwo, y = yAxisLineTwo,
                                    text = multilineSecondText,
                                    textColor = Color.BLACK,
                                    textFont = 7,
                                    textSize = mObject.mFontSizeInt.nullSafety()
                                ))
                            }

                            addYAxisToSet(yAxisLineTwo,
                                getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                            mBoxY = mBoxY + 20
                        }
                        if(mObject.mNoBox == 0) {
                            val xAxisStart = (mObject.mAxisX-2)
                            val xAxisEnd = spaceCountForWidth(
                                mList,
                                position,Constants.END_X_FOR_BOX_BOSTON
                            )

                            val yAxisEnd = spaceCountForHeight(mList, position)

                            commandPrinter.append(
                                "BOX " + xAxisStart + " " + (isLableEmptyThenY) + " " + xAxisEnd + " " + yAxisEnd + " 1 \r\n"
                            )

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Rectangle(
                                    startX = xAxisStart, startY = isLableEmptyThenY, endX = xAxisEnd, endY = yAxisEnd,
                                    fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                                ))
                            }

                            addYAxisToSet(yAxisEnd, 0)
                        }

                    } else {
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 28)
                        val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                        txtValue = finalValue.nullSafety()
                        valueX2 = ((spaceCountForWidth(mList, position,Constants.END_X_FOR_BOX_BOSTON)))

                        val xAxisStart = (mObject.mAxisX-2)
                        if(mObject.mNoBox == 0) {
                            val yAxisEnd = spaceCountForHeight(mList, position)
                            txtValue = ""
                            commandPrinter.append(
                                "BOX " + xAxisStart + " " + (isLableEmptyThenY) + " " + valueX2 + " " + yAxisEnd + " 1 \r\n"
                            )

                            if (LogUtil.isEnableCommandBasedFacsimile){
                                drawableElements.add(DrawableElement.Rectangle(
                                    startX = xAxisStart, startY = isLableEmptyThenY, endX = valueX2, endY = yAxisEnd,
                                    fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                                ))
                            }

                            addYAxisToSet(yAxisEnd, 0)
                            txtValue += mObject.offTypeFirst.nullSafety().textAlignInCenter(mObject.mAxisX,valueX2,14)
                        }

                        val xAxisText = (mObject.mAxisX+2)
                        val yAxisText = (isLableEmptyThenY+3)

                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisText + " " + yAxisText + " " + txtValue + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = xAxisText, y = yAxisText,
                                text = txtValue,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(yAxisText,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }
                }
            }
        }
        else {
            if (mObject.offTypeFirst != null && mObject.offTypeFirst!!.isNotEmpty()) {
                if (Horizontal == 1) {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 "+ mObject.mFontSizeInt +" "+ mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")

                    val xAxisForOffTypeFirst = (if(mObject.mFontSizeInt==0||mObject.mFontSizeInt==1) (mObject.mAxisX +(mObject.offNameFirst!!.length*12)) else (mObject.mAxisX + (mObject.offNameFirst!!.length*15)))
                    commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + xAxisForOffTypeFirst + " " + mObject.mAxisY + " " + finalValue + " \r\n")
                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Text(
                            x = mObject.mAxisX , y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        ))

                        drawableElements.add(DrawableElement.Text(
                            x = xAxisForOffTypeFirst, y = mObject.mAxisY,
                            text = mObject.offTypeFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = mObject.mFontSizeInt.nullSafety()
                        ))
                    }

                    addYAxisToSet(mObject.mAxisY,
                        getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                } else {
                    val finalLabel = mObject.offNameFirst?.replace(Constants.COMMA,"")
                    commandPrinter.append("TEXT 7 0 " + mObject.mAxisX + " " + mObject.mAxisY + " " + finalLabel + " \r\n")
                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Text(
                            x = mObject.mAxisX, y = mObject.mAxisY,
                            text = mObject.offNameFirst.nullSafety(),
                            textColor = Color.BLACK,
                            textFont = 7,
                            textSize = 0
                        ))
                    }
                    addYAxisToSet(mObject.mAxisY, 0)
                    if (!mObject.offTypeFirst.isNullOrEmpty() && mObject.offTypeFirst!!.length > 35) {

                        val words = mObject.offTypeFirst!!.split(" ")
                        val lines = mutableListOf("", "", "","","","","","","","")
                        var currentLine = 0
                        var mWordPerFont = 36
                        if(mObject.mFontSizeInt==0){
                            mWordPerFont = 40
                        }
                        for (word in words) {
                            val lineLimit = when (currentLine) {
                                0 -> mWordPerFont
                                else -> mWordPerFont
                            }

                            if ((lines[currentLine].length + word.length + 1) <= lineLimit) {
                                lines[currentLine] += "$word "
                            } else if (currentLine < 9) {
                                currentLine++
                                lines[currentLine] += "$word "
                            }
                        }

                        val baseY = if (mObject.offNameFirst.isNullOrEmpty()) mObject.mAxisY else mObject.mAxisY + 32

                        lines.forEachIndexed { index, line ->
                            if (line.trim().isNotEmpty()) {
                                val yOffset = baseY + (index * 32)
                                commandPrinter.append("TEXT 7 ${mObject.mFontSizeInt} ${mObject.mAxisX} $yOffset ${line.trim()} \r\n")

                                if (LogUtil.isEnableCommandBasedFacsimile){
                                    drawableElements.add(DrawableElement.Text(
                                        x = mObject.mAxisX, y = yOffset,
                                        text = line.trim(),
                                        textColor = Color.BLACK,
                                        textFont = 7,
                                        textSize = mObject.mFontSizeInt.nullSafety()
                                    ))
                                }

                                addYAxisToSet(yOffset,
                                    getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                            }
                        }
                    }
                    else {
                        val isLableEmptyThenY =
                            if (mObject.offNameFirst!!.isEmpty()) mObject.mAxisY else (mObject.mAxisY + 26)
                        val finalValue = mObject.offTypeFirst?.replace(Constants.COMMA,"")
                        commandPrinter.append("TEXT 7 " + mObject.mFontSizeInt + " " + (mObject.mAxisX) + " " + isLableEmptyThenY + " " + finalValue.toString().trim() + " \r\n")

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = mObject.mAxisX, y = isLableEmptyThenY,
                                text = finalValue.nullSafety(),
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = mObject.mFontSizeInt.nullSafety()
                            ))
                        }

                        addYAxisToSet(isLableEmptyThenY,
                            getHeightBasedOnFont(mObject.mFontSizeInt.nullSafety()))
                    }

                }
            }
        }
        return commandPrinter;
    }

    fun getYAxisForMultiline(initialYAxis: Double, lineNumber: Int): Double {
        when (lineNumber) {
            1 -> {
                return initialYAxis
            }

            2 -> {
                return initialYAxis + 30
            }

            3 -> {
                return initialYAxis + 58
            }

            4 -> {
                return initialYAxis + 86
            }

            5 -> {
                return initialYAxis + 114
            }

            else -> {
                return initialYAxis
            }
        }
    }

    /**
     * Horizontal space
     */
    fun setValueXWithColon(mObject: VehicleListModel, mLine:Int): Double {
        var xValue = 150.0;

        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)
        ){
            if (
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                    ignoreCase = true
                )||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DANVILLE_VA,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CAMDEN,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SOUTH_LAKE,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_WINPARK_TX,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                    ignoreCase = true
                )||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_PARKX,
                    ignoreCase = true
                )
            ) {
                xValue = if (mObject.mHorizontalColon == 0) (mObject.mAxisX + 100)
                else if (mObject.mHorizontalColon == 1) (mObject.mAxisX + 200)
                else if (mObject.mHorizontalColon == 2) (mObject.mAxisX + 225)
                else if (mObject.mHorizontalColon == 3) (mObject.mAxisX + 250) else (mObject.mAxisX + 275)

                if (mLine>1) {
                    xValue +=20
                }
//                if (mLine == 1) {
//                    xValue = if (mObject.mHorizontalColon == 0) (mObject.mAxisX + 100)
//                    else if (mObject.mHorizontalColon == 1) (mObject.mAxisX + 200)
//                    else if (mObject.mHorizontalColon == 2) (mObject.mAxisX + 225) else (mObject.mAxisX + 250)
//                } else {
//                    xValue = (if (mObject.mHorizontalColon == 0) (mObject.mAxisX + 125)
//                    else if (mObject.mHorizontalColon == 1) (mObject.mAxisX + 175)
//                    else if (mObject.mHorizontalColon == 2) (mObject.mAxisX + 175) else (mObject.mAxisX + 280))
//                }
            } else {
                if (mLine == 1) {
                    xValue = if (mObject.mHorizontalColon == 0) (mObject.mAxisX + 100)
                    else if (mObject.mHorizontalColon == 1) (mObject.mAxisX + 150)
                    else if (mObject.mHorizontalColon == 2) (mObject.mAxisX + 175)
                    else if (mObject.mHorizontalColon == 3) (mObject.mAxisX + 200) else (mObject.mAxisX + 250)
                } else {
                    xValue = (if (mObject.mHorizontalColon == 0) (mObject.mAxisX + 125)
                    else if (mObject.mHorizontalColon == 1) (mObject.mAxisX + 177)
                    else if (mObject.mHorizontalColon == 2) (mObject.mAxisX + 177)
                    else if (mObject.mHorizontalColon == 3) (mObject.mAxisX + 200) else (mObject.mAxisX + 280))
                }
            }

        } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)) {
            if (mObject.offTypeFirst != null && mObject.offNameFirst!!.isNotEmpty()) {
                xValue =   (if (mObject.mHorizontalColon == 0) (mObject.mAxisX + 145) else if (mObject.mHorizontalColon == 1) (mObject.mAxisX + 145) else if (mObject.mHorizontalColon == 2) (mObject.mAxisX + 175) else if (mObject.mHorizontalColon == 3) (mObject.mAxisX + 200) else (mObject.mAxisX + 245))
            }
        }
        return xValue
    }


    /**
     * mNoLine = 0 line 1 = noline
     */
    var boxTopY = 0.0

    @JvmStatic
    fun setXYforPrintSectionHeader(
        mObject: VehicleListModel, sectionPosition: Int, sectionName: String,
        commandPrinter: java.lang.StringBuilder, mNoLine: Int
    ): java.lang.StringBuilder {
        if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) &&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) &&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) &&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) &&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) &&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) &&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) &&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
        ) {
            boxTopY = mObject.mAxisY
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)){
                if (mObject.mSectionHeader.isNotEmpty()) {
                    if (mNoLine == 0) {
                        var txtLabel = ""
                        txtLabel += mObject.mSectionHeader.nullSafety()
                            .textAlignInCenter(3.0, 560.0, 13)

                        val yAxisForSectionHeader = mObject.mAxisY - 25
                        commandPrinter.append("TEXT 7 0 " + 10 + " " + yAxisForSectionHeader + " " + txtLabel + " \r\n")
                        commandPrinter.append("LINE " + 2 + " " + (mObject.mAxisY) + " $lineXAxisEnd " + (mObject.mAxisY) + " 1 \r\n")

                        addYAxisToSet(yAxisForSectionHeader, getHeightBasedOnFont(0))

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = 10.0, y = yAxisForSectionHeader,
                                text = txtLabel,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = 0
                            ))

                            drawableElements.add(DrawableElement.Line(2f, (mObject.mAxisY).toFloat(), lineXAxisEnd.toFloat(),  (mObject.mAxisY).toFloat(), Color.BLACK, 2f))
                        }
                    } else {
                        val yAxisForSectionHeader = mObject.mAxisY - 25
                        commandPrinter.append("TEXT 7 0 " + 10 + " " + yAxisForSectionHeader + " " + mObject.mSectionHeader + " \r\n")

                        addYAxisToSet(yAxisForSectionHeader, getHeightBasedOnFont(0))

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Text(
                                x = 10.0, y = yAxisForSectionHeader,
                                text = mObject.mSectionHeader,
                                textColor = Color.BLACK,
                                textFont = 7,
                                textSize = 0
                            ))
                        }
                    }
                }
            } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
                mObject.sectionType = SectionType.HEADER
                mObject.mTextAlignment = TextAlignmentForCommandPrint.LEFT
                commandPrinter.append(ObjectMapperProvider.toJson(mObject))
                commandPrinter.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
            }else {
                val yAxisFinalSectionHeader = mObject.mAxisY - 50
                commandPrinter.append("TEXT 7 1 " + 10 + " " + yAxisFinalSectionHeader + " " + mObject.mSectionHeader + " \r\n")
                addYAxisToSet(yAxisFinalSectionHeader, getHeightBasedOnFont(1))

                if (LogUtil.isEnableCommandBasedFacsimile){
                    drawableElements.add(DrawableElement.Text(
                        x = 10.0, y = yAxisFinalSectionHeader,
                        text = mObject.mSectionHeader,
                        textColor = Color.BLACK,
                        textFont = 7,
                        textSize = 1
                    ))
                }
            }
        }

        return commandPrinter;
    }

    @JvmStatic
    fun setXYforPrintOCRTEXT(
        mObject: String, sectionPosition: Int, sectionName: String,
        commandPrinter: java.lang.StringBuilder, X: Int, Y: Int
    ): java.lang.StringBuilder {
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
//            mObject.sectionType = SectionTypeEnum.HEADER.name
//            commandPrinter.append(ObjectMapperProvider.toJson(mObject))
//            commandPrinter.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
        } else {
            commandPrinter.append("VTEXT 2 2 " + X + " " + Y + " " + mObject + " \r\n")
        }
        if (LogUtil.isEnableCommandBasedFacsimile){
            drawableElements.add(DrawableElement.Text(
                x = X.nullSafety().toDouble(), y = Y.nullSafety().toDouble(),
                text = mObject,
                textColor = Color.BLACK,
                textFont = 2,
                textSize = 2,
                isVertical = true
            ))
        }

        addYAxisToSet(Y.nullSafety().toDouble(), 2)
        return commandPrinter;
    }

    @JvmStatic
    fun setXYforPrintSectionLine(
        mObject: VehicleListModel, sectionPosition: Int, sectionName: String,
        commandPrinter: java.lang.StringBuilder
    ): java.lang.StringBuilder {
        val yAxisStart = (mObject.mAxisY - lineWidthSize)
        val yAxisEnd = (mObject.mAxisY - lineWidthSize)
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
//            mObject.sectionType = SectionTypes.LINE
//            commandPrinter.append(ObjectMapperProvider.toJson(mObject))
//            commandPrinter.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) && sectionName.equals("officer")){
            commandPrinter.append("LINE " + 1 + " " + yAxisStart + " $lineXAxisEnd " + yAxisEnd + " 1 \r\n")
            commandPrinter.append("LINE " + 1 + " " + (yAxisStart+120) + " $lineXAxisEnd " + (yAxisEnd+120) + " 1 \r\n")

            if (LogUtil.isEnableCommandBasedFacsimile){
                drawableElements.add(DrawableElement.Line(1f, yAxisStart.toFloat(), lineXAxisEnd.toFloat(),  yAxisEnd.toFloat(), Color.BLACK, lineWidthSizeFacsimile))
                drawableElements.add(DrawableElement.Line(1f, (yAxisStart+120).toFloat(), lineXAxisEnd.toFloat(),  (yAxisEnd+120).toFloat(), Color.BLACK, lineWidthSizeFacsimile))
            }

            addYAxisToSet(mObject.mAxisY, PRINT_LINE_HEIGHT)
        }else {
            commandPrinter.append("LINE " + 1 + " " + yAxisStart + " $lineXAxisEnd " + yAxisEnd + " 1 \r\n")

            if (LogUtil.isEnableCommandBasedFacsimile){
                drawableElements.add(DrawableElement.Line(1f, yAxisStart.toFloat(), lineXAxisEnd.toFloat(),  yAxisEnd.toFloat(), Color.BLACK, lineWidthSizeFacsimile))
            }

            addYAxisToSet(mObject.mAxisY, PRINT_LINE_HEIGHT)
        }

        return commandPrinter;
    }

    @JvmStatic
    fun  setXYforPrintSectionBox(mObjectFirst : VehicleListModel, mObjectLast : VehicleListModel, sectionPosition : Int, sectionName : String,
                                 commandPrinter : java.lang.StringBuilder) : java.lang.StringBuilder
    {
        try {
            if(mObjectFirst!!.offTypeFirst!=null) {
                var BoxY = 0.0
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)) {
                    BoxY = mObjectLast.mAxisY + 35
                    if (mObjectFirst.offTypeFirst!!.length > 40) {
                        BoxY = mObjectLast.mAxisY + 50
                    }
                }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA,ignoreCase = true)) {
                    BoxY = mObjectLast.mAxisY + 55
                    if (mObjectFirst.offTypeFirst!!.length > 40) {
                        BoxY = mObjectLast.mAxisY + 75
                    }
                } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)) {
                    BoxY = mObjectLast.mAxisY + 68

                    if (mObjectFirst.offTypeFirst?.length.nullSafety() > 50) {
                        BoxY = mObjectLast.mAxisY + 80
                    }
                } else {
                    LogUtil.printLogHeader("text count"," "+mObjectFirst!!.offTypeFirst!!.length)
                    if(sectionName.equals("comment")) {
                        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)){
                            BoxY = mObjectLast.mAxisY + 200
                        }else {
                            if (mObjectLast.mFontSizeInt == 1) {
                                BoxY = when {
                                    mObjectFirst!!.offTypeFirst!!.length > 240 -> mObjectLast.mAxisY + 320
                                    mObjectFirst!!.offTypeFirst!!.length > 200 -> mObjectLast.mAxisY + 280
                                    mObjectFirst!!.offTypeFirst!!.length > 160 -> mObjectLast.mAxisY + 240
                                    mObjectFirst!!.offTypeFirst!!.length > 120 -> mObjectLast.mAxisY + 200
                                    mObjectFirst.offTypeFirst!!.length > 80 -> mObjectLast.mAxisY + 150
                                    mObjectFirst.offTypeFirst!!.length > 40 -> mObjectLast.mAxisY + 100
                                    else -> mObjectLast.mAxisY + 60
                                }
                            } else {
                                BoxY = when {
                                    mObjectFirst!!.offTypeFirst!!.length > 240 -> mObjectLast.mAxisY + 280
                                    mObjectFirst!!.offTypeFirst!!.length > 200 -> mObjectLast.mAxisY + 240
                                    mObjectFirst!!.offTypeFirst!!.length > 160 -> mObjectLast.mAxisY + 200
                                    mObjectFirst!!.offTypeFirst!!.length > 120 -> mObjectLast.mAxisY + 160
                                    mObjectFirst.offTypeFirst!!.length > 80 -> mObjectLast.mAxisY + 110
                                    mObjectFirst.offTypeFirst!!.length > 40 -> mObjectLast.mAxisY + 60
                                    else -> mObjectLast.mAxisY + 60
                                }
                            }
                        }
                    }else{
                        if (mObjectFirst!!.offTypeFirst!!.length > 270) {
                            BoxY = mObjectLast.mAxisY + 310
                        }
                        else if (mObjectFirst!!.offTypeFirst!!.length > 200) {
                            BoxY = mObjectLast.mAxisY + 250
                        }
                        else if (mObjectFirst!!.offTypeFirst!!.length > 160) {
                            BoxY = mObjectLast.mAxisY + 200
                        }
                        else if (mObjectFirst!!.offTypeFirst!!.length > 120) {
                            BoxY = mObjectLast.mAxisY + 160
                        }
                        else if (mObjectLast!!.offTypeFirst!!.length > 80) {
                            BoxY = mObjectLast.mAxisY + 106
                        } else {
                            BoxY = mObjectLast.mAxisY + 70
                            if (mObjectFirst!!.offTypeFirst!!.length > 50) {
                                BoxY = mObjectLast.mAxisY + 80
                            }
                            if (mObjectLast!!.offTypeFirst!!.length > 35) {
                                BoxY = mObjectLast.mAxisY + 90
                            }
                            if (mObjectFirst!!.offTypeFirst!!.length > 70) {
                                BoxY = mObjectLast.mAxisY + 106
                            }

                        }
                    }
                }

//        BOX 5 65 525 1300 4
//                if (BuildConfig.FLAVOR.equals(
//                        Constants.FLAVOR_TYPE_ISLEOFPALMS,
//                        ignoreCase = true
//                    ) && sectionName.equals("violation")
//                ) {
//                    val yAxisStartForViolation = (mObjectFirst.mAxisY - 10)
//                    val yAxisForViolation = mObjectLast.mAxisY + 110
//                    commandPrinter.append("BOX " + 0 + " " + yAxisStartForViolation + " $boxXAxisEnd " + yAxisForViolation + " 1 \r\n")
//                    addYAxisToSet(yAxisForViolation, 0)
//
//                    if (LogUtil.isEnableCommandBasedFacsimile){
//                        drawableElements.add(DrawableElement.Rectangle(
//                            startX = 0.0, startY = yAxisStartForViolation, endX = boxXAxisEnd.toDouble(), endY = yAxisForViolation,
//                            fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
//                        ))
//                    }
//                } else
                    if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_SANIBEL,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_SMYRNABEACH,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_ORLEANS,
                        ignoreCase = true
                    )
                ) {
                    val yAxisStart = (mObjectFirst.mAxisY - 10)
                    commandPrinter.append("BOX " + 0 + " " + yAxisStart + " $boxXAxisEnd " + BoxY + " 1 \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile) {
                        drawableElements.add(
                            DrawableElement.Rectangle(
                                startX = 0.0,
                                startY = yAxisStart,
                                endX = boxXAxisEnd.toDouble(),
                                endY = BoxY,
                                fillColor = Color.TRANSPARENT,
                                borderColor = Color.BLACK,
                                borderWidth = 1.0
                            )
                        )
                    }

                    addYAxisToSet(BoxY, 0)
                } else if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_BOSTON,
                        ignoreCase = true
                    )
                ) {
                    if (sectionName.equals("comment")) {
                        commandPrinter.append("BOX " + 3 + " " + mOrleansBoxInitialYValue + " "+ checkMainBoxWidthSiteWise()+" " + BoxY + " 1 \r\n")
                        addYAxisToSet(BoxY, 0)

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Rectangle(
                                startX = 3.0, startY = mOrleansBoxInitialYValue.nullSafety().toDouble(), endX = checkMainBoxWidthSiteWise().toDouble(), endY = BoxY,
                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                            ))
                        }
                    }
                } else if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_IMPARK_PHSA,
                        ignoreCase = true
                    )
                ) {
                    if (sectionName.equals("violation")) {
                        commandPrinter.append("BOX " + 3 + " " + mOrleansBoxInitialYValue + " "+ checkMainBoxWidthSiteWise()+" " + BoxY + " 1 \r\n")
                        addYAxisToSet(BoxY, 0)

                        if (LogUtil.isEnableCommandBasedFacsimile){
                            drawableElements.add(DrawableElement.Rectangle(
                                startX = 3.0, startY = mOrleansBoxInitialYValue.nullSafety().toDouble(), endX = checkMainBoxWidthSiteWise().toDouble(), endY = BoxY,
                                fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                            ))
                        }
                    }
                } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
//            mObjectFirst.sectionType = SectionTypes.BOX
//            commandPrinter.append(ObjectMapperProvider.toJson(mObjectFirst))
//            commandPrinter.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
                }else {
                    val yAxisStart = (mObjectFirst.mAxisY - 10)
                    commandPrinter.append("BOX " + 0 + " " + yAxisStart +" "+ checkMainBoxWidthSiteWise() +" "+ BoxY + " 1 \r\n")

                    if (LogUtil.isEnableCommandBasedFacsimile){
                        drawableElements.add(DrawableElement.Rectangle(
                            startX = 0.0, startY = yAxisStart, endX = checkMainBoxWidthSiteWise().toDouble(), endY = BoxY,
                            fillColor = Color.TRANSPARENT, borderColor = Color.BLACK, borderWidth = 1.0
                        ))
                    }

                    addYAxisToSet(BoxY, 0)
                }
                return commandPrinter;
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return commandPrinter;
    }


    fun checkMainBoxWidthSiteWise():String{
//        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true))
//        {
//            return Constants.END_X_FOR_BOX_NOLA.toString()
//        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true))
//        {
//            return Constants.END_X_FOR_BOX_BOSTON.toString()
//        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true))
//        {
//            return Constants.END_X_FOR_BOX_OCEAN_CITY.toString()
//        }
//        return "565"

        return boxXAxisEnd.toString()
    }
    /**
     * Function used to append Expanded Graphic Command to Command Builder Text
     */
    @JvmStatic
    fun setXYForImage(
        bitmap: Bitmap, xPosition: Int, yPosition: Int,
        commandPrinterBuilder: java.lang.StringBuilder
    ): java.lang.StringBuilder {
        val bwBitmap = convertBitmapToBWUsingDefaultARGB(bitmap)
        commandPrinterBuilder.append(
            getExpandedGraphicImagePrintingCommandUsingCPCL(
                myBitmap = bwBitmap,
                xPosition = xPosition,
                yPosition = yPosition
            )
        )

        if (LogUtil.isEnableCommandBasedFacsimile) {
            drawableElements.add(
                DrawableElement.Image(
                    x = xPosition.nullSafety().toFloat(), y = yPosition.nullSafety().toFloat(),
                    width = bwBitmap.width.nullSafety().toFloat(),
                    height = bwBitmap.height.nullSafety().toFloat(),
                    bitmap = bwBitmap
                )
            )
        }
        addYAxisToSet(yPosition.nullSafety().toDouble(), bitmap.height.nullSafety())
        return commandPrinterBuilder
    }

    @JvmStatic
    fun setXYForLines(sharedPreference: SharedPref, commandPrinter: java.lang.StringBuilder): java.lang.StringBuilder {

        val lineOneForPrint = sharedPreference.read(
            SharedPrefKey.LINE_1_FOR_PRINT, ""
        ).nullSafety()
        val lineTwoForPrint = sharedPreference.read(
            SharedPrefKey.LINE_2_FOR_PRINT, ""
        ).nullSafety()
        val lineThreeForPrint = sharedPreference.read(
            SharedPrefKey.LINE_3_FOR_PRINT, ""
        ).nullSafety()
        val lineFourForPrint = sharedPreference.read(
            SharedPrefKey.LINE_4_FOR_PRINT, ""
        ).nullSafety()
        val lineFiveForPrint = sharedPreference.read(
            SharedPrefKey.LINE_5_FOR_PRINT, ""
        ).nullSafety()
        val lineSixForPrint = sharedPreference.read(
            SharedPrefKey.LINE_6_FOR_PRINT, ""
        ).nullSafety()
        val lineSevenForPrint = sharedPreference.read(
            SharedPrefKey.LINE_7_FOR_PRINT, ""
        ).nullSafety()
        val lineEightForPrint = sharedPreference.read(
            SharedPrefKey.LINE_8_FOR_PRINT, ""
        ).nullSafety()
        val lineNineForPrint = sharedPreference.read(
            SharedPrefKey.LINE_9_FOR_PRINT, ""
        ).nullSafety()
        val lineTenForPrint = sharedPreference.read(
            SharedPrefKey.LINE_10_FOR_PRINT, ""
        ).nullSafety()

        //Line One
        if (lineOneForPrint.isNotEmpty()) {
            val lineOneToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_1_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineOneToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_1_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineOneToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_1_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineOneToPrintXPosition $lineOneToPrintYPosition $lineXAxisEnd $lineOneToPrintYPosition $lineOneToPrintHeight \r\n")

            addYAxisToSet(lineOneToPrintYPosition.nullSafety().toDouble(), lineOneToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineOneToPrintXPosition.nullSafety("0").toFloat(),
                        lineOneToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineOneToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineOneToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Two
        if (lineTwoForPrint.isNotEmpty()) {
            val lineTwoToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_2_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineTwoToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_2_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineTwoToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_2_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineTwoToPrintXPosition $lineTwoToPrintYPosition $lineXAxisEnd $lineTwoToPrintYPosition $lineTwoToPrintHeight \r\n")

            addYAxisToSet(lineTwoToPrintYPosition.nullSafety().toDouble(), lineTwoToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineTwoToPrintXPosition.nullSafety("0").toFloat(),
                        lineTwoToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineTwoToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineTwoToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Three
        if (lineThreeForPrint.isNotEmpty()) {
            val lineThreeToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_3_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineThreeToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_3_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineThreeToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_3_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineThreeToPrintXPosition $lineThreeToPrintYPosition $lineXAxisEnd $lineThreeToPrintYPosition $lineThreeToPrintHeight \r\n")

            addYAxisToSet(lineThreeToPrintYPosition.nullSafety().toDouble(), lineThreeToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineThreeToPrintXPosition.nullSafety("0").toFloat(),
                        lineThreeToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineThreeToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineThreeToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Four
        if (lineFourForPrint.isNotEmpty()) {
            val lineFourToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_4_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineFourToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_4_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineFourToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_4_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineFourToPrintXPosition $lineFourToPrintYPosition $lineXAxisEnd $lineFourToPrintYPosition $lineFourToPrintHeight \r\n")

            addYAxisToSet(lineFourToPrintYPosition.nullSafety().toDouble(), lineFourToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineFourToPrintXPosition.nullSafety("0").toFloat(),
                        lineFourToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineFourToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineFourToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Five
        if (lineFiveForPrint.isNotEmpty()) {
            val lineFiveToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_5_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineFiveToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_5_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineFiveToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_5_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineFiveToPrintXPosition $lineFiveToPrintYPosition $lineXAxisEnd $lineFiveToPrintYPosition $lineFiveToPrintHeight \r\n")

            addYAxisToSet(lineFiveToPrintYPosition.nullSafety().toDouble(), lineFiveToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineFiveToPrintXPosition.nullSafety("0").toFloat(),
                        lineFiveToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineFiveToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineFiveToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Six
        if (lineSixForPrint.isNotEmpty()) {
            val lineSixToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_6_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineSixToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_6_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineSixToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_6_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineSixToPrintXPosition $lineSixToPrintYPosition $lineXAxisEnd $lineSixToPrintYPosition $lineSixToPrintHeight \r\n")

            addYAxisToSet(lineSixToPrintYPosition.nullSafety().toDouble(), lineSixToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineSixToPrintXPosition.nullSafety("0").toFloat(),
                        lineSixToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineSixToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineSixToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Seven
        if (lineSevenForPrint.isNotEmpty()) {
            val lineSevenToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_7_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineSevenToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_7_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineSevenToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_7_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineSevenToPrintXPosition $lineSevenToPrintYPosition $lineXAxisEnd $lineSevenToPrintYPosition $lineSevenToPrintHeight \r\n")

            addYAxisToSet(lineSevenToPrintYPosition.nullSafety().toDouble(), lineSevenToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineSevenToPrintXPosition.nullSafety("0").toFloat(),
                        lineSevenToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineSevenToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineSevenToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Eight
        if (lineEightForPrint.isNotEmpty()) {
            val lineEightToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_8_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineEightToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_8_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineEightToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_8_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineEightToPrintXPosition $lineEightToPrintYPosition $lineXAxisEnd $lineEightToPrintYPosition $lineEightToPrintHeight \r\n")

            addYAxisToSet(lineEightToPrintYPosition.nullSafety().toDouble(), lineEightToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineEightToPrintXPosition.nullSafety("0").toFloat(),
                        lineEightToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineEightToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineEightToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Nine
        if (lineNineForPrint.isNotEmpty()) {
            val lineNineToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_9_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineNineToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_9_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineNineToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_9_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineNineToPrintXPosition $lineNineToPrintYPosition $lineXAxisEnd $lineNineToPrintYPosition $lineNineToPrintHeight \r\n")

            addYAxisToSet(lineNineToPrintYPosition.nullSafety().toDouble(), lineNineToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineNineToPrintXPosition.nullSafety("0").toFloat(),
                        lineNineToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineNineToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineNineToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }

        //Line Ten
        if (lineTenForPrint.isNotEmpty()) {
            val lineTenToPrintXPosition = sharedPreference.read(
                SharedPrefKey.LINE_10_FOR_PRINT_X, "0"
            ).nullSafety()

            val lineTenToPrintYPosition = sharedPreference.read(
                SharedPrefKey.LINE_10_FOR_PRINT_Y, "0"
            ).nullSafety()

            val lineTenToPrintHeight = sharedPreference.read(
                SharedPrefKey.LINE_10_FOR_PRINT_HEIGHT, "1"
            ).nullSafety()


            commandPrinter.append("LINE $lineTenToPrintXPosition $lineTenToPrintYPosition $lineXAxisEnd $lineTenToPrintYPosition $lineTenToPrintHeight \r\n")

            addYAxisToSet(lineTenToPrintYPosition.nullSafety().toDouble(), lineTenToPrintHeight.nullSafety("0").toInt())

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        lineTenToPrintXPosition.nullSafety("0").toFloat(),
                        lineTenToPrintYPosition.nullSafety("0").toFloat(),
                        lineXAxisEnd.toFloat(),
                        lineTenToPrintYPosition.nullSafety("0").toFloat(),
                        Color.BLACK,
                        lineTenToPrintHeight.nullSafety("0").toFloat()
                    )
                )
            }
        }


        if ((BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_OXFORD,
                ignoreCase = true
            )) && LogUtil.isPrintHeightDynamicFromTicket
        ) {
            val yAxisStart = AppUtils.getMaxYAxisFromCommand() - 50
            val yAxisEnd = AppUtils.getMaxYAxisFromCommand() - 50
            commandPrinter.append("LINE " + 1 + " " + yAxisStart + " $lineXAxisEnd " + yAxisEnd + " 1 \r\n")

            addYAxisToSet(yAxisEnd.nullSafety().toDouble(), PRINT_LINE_HEIGHT)

            if (LogUtil.isEnableCommandBasedFacsimile) {
                drawableElements.add(
                    DrawableElement.Line(
                        1f,
                        yAxisStart.toFloat(),
                        lineXAxisEnd.toFloat(),
                        yAxisEnd.toFloat(),
                        Color.BLACK,
                        1f
                    )
                )
            }
        }

        return commandPrinter;
    }

    @JvmStatic
    fun getPrintCommandForBootTow(
        context: Context,
        printBootNoticeModel: PrintBootNoticeModel,
        citationNumber: String? = null,
        violationDate: String? = null,
        lotName: String? = null,
        spaceNumber: String? = null
    ): Pair<StringBuilder, Int> {
        val commandPrinter = java.lang.StringBuilder()

        val todaysDate = SDF_MM_DD_YYYY.format(System.currentTimeMillis())


        var xAxis = 0
        var yAxis = 0
        val xAxisInHalf = (ZEBRA_WIDTH / 2).toFormatInt()

        yAxis += SEPTA_TOW_NOTICE_HEADER_MARGIN

        //Start of Header in the print
        commandPrinter.append("CENTER" + "\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_HEADER $xAxis $yAxis " + context.getString(
                R.string.septa_tow_notice_header
            ) + "\r\n"
        )

        yAxis += 50
//        commandPrinter.append(
//            "$ZEBRA_PRINTER_FONT_EXTRA_SMALL_DESC $xAxis $yAxis " + context.getString(
//                R.string.septa_tow_notice_issued_with_value,
//                todaysDate
//            ) + "\r\n"
//        )
        //End of Header in the print

        xAxis = 15
        //yAxis += 30 //If you use issue date then please uncomment this & comment below line
        yAxis += 10
        commandPrinter.append("LEFT" + "\r\n")

        //Start of Citation Number Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_citation_number_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                citationNumber.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of Citation Number Label Print

        //Start of Violation Date Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_violation_date_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                violationDate.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of Violation Date Label Print

        //Start of Officer ID Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_officer_id_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                printBootNoticeModel.officerDetails?.badgeId.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of Officer ID Label Print

        //Start of Lot Name Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_lot_name_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                lotName.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of Lot Name Label Print

        //Start of Space Number Label Print
//        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_space_number_colon)}\r\n")
//        commandPrinter.append(
//            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
//                spaceNumber.nullOrEmptySafety(
//                    context.getString(R.string.lbl_not_available)
//                )
//            }\r\n"
//        )
//        yAxis += 32
        //End of Space Number Label Print

        //Start of License Plate Number Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_license_plate_number_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                printBootNoticeModel.vehicleDetails?.lpNumber.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of License Plate Number Label Print

        //Start of License Plate State Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_license_plate_state_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                printBootNoticeModel.vehicleDetails?.state.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of License Plate State Label Print

        //Start of Vehicle Make Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_vehicle_make_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                printBootNoticeModel.vehicleDetails?.make.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of Vehicle Make Label Print

        //Start of Vehicle Model Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_vehicle_model_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                printBootNoticeModel.vehicleDetails?.model.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of Vehicle Model Label Print

        //Start of Vehicle Color Label Print
        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_vehicle_color_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                printBootNoticeModel.vehicleDetails?.color.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        yAxis += 32
        //End of Vehicle Color Label Print

        //Start of Vehicle Type Label Print
//        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_violation_type_colon)}\r\n")
//
//        xAxis = 25
//        yAxis += 32
//        commandPrinter.append(
//            "$ZEBRA_PRINTER_FONT_TITLE $xAxis $yAxis ${
//                bootRequest.bootTowType.nullOrEmptySafety(
//                    context.getString(R.string.lbl_not_available)
//                )
//            }\r\n"
//        )
        //End of Vehicle Type Label Print

        xAxis = 15
//        yAxis += 32

        //Start of PEO Remarks Label Print
//        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_peo_remarks_colon)}\r\n")
//
//        xAxis = 25
//        yAxis += 32
//        commandPrinter.append(
//            "$ZEBRA_PRINTER_FONT_TITLE $xAxis $yAxis ${
//                bootRequest.remarks.nullOrEmptySafety(
//                    context.getString(R.string.lbl_not_available)
//                )
//            }\r\n"
//        )


        commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis ${context.getString(R.string.septa_tow_notice_peo_remarks_colon)}\r\n")
        commandPrinter.append(
            "$ZEBRA_PRINTER_FONT_TITLE $xAxisInHalf $yAxis ${
                printBootNoticeModel.remarks.nullOrEmptySafety(
                    context.getString(R.string.lbl_not_available)
                )
            }\r\n"
        )
        //yAxis += 32
        //End of PEO Remarks Label Print

        xAxis = 0

        //Start of Desc One on Tow Notice Print
        yAxis += 50
        context.getString(R.string.septa_tow_notice_desc_one)
            .splitTextToMultiline(SEPTA_TOW_NOTICE_DESC_MAX_LENGTH).forEach {
                commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis $it\r\n")
                yAxis += 27
            }
        //End of Desc One on Tow Notice Print

        //Start of Desc Two on Tow Notice Print
        yAxis += 25
        context.getString(R.string.septa_tow_notice_desc_two)
            .splitTextToMultiline(SEPTA_TOW_NOTICE_DESC_MAX_LENGTH).forEach {
                commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis $it\r\n")
                yAxis += 27
            }
        //End of Desc Two on Tow Notice Print

        //Start of Desc Three on Tow Notice Print
        yAxis += 25
        context.getString(R.string.septa_tow_notice_desc_three)
            .splitTextToMultiline(SEPTA_TOW_NOTICE_DESC_MAX_LENGTH).forEach {
                commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis $it\r\n")
                yAxis += 27
            }
        //End of Desc Three on Tow Notice Print

        yAxis += 25

        //Start of Line Print in Tow Notice Print
        commandPrinter.append("LINE $xAxis $yAxis ${ZEBRA_WIDTH.toFormatInt()} $yAxis $SEPTA_TOW_NOTICE_LINE_BREAKER_THICKNESS \r\n")
        //End of Line Print in Tow Notice Print

        yAxis += 25

        //Start of Footer of Tow Notice Print
        context.getString(R.string.septa_tow_notice_footer)
            .splitTextToMultiline(SEPTA_TOW_NOTICE_DESC_MAX_LENGTH).forEach {
                commandPrinter.append("$ZEBRA_PRINTER_FONT_SMALL_TITLE $xAxis $yAxis $it\r\n")
                yAxis += 27
            }
        //End of Footer of Tow Notice Print

        //Adding Margin from Bottom
        yAxis += 20

        LogUtil.printLog("PrintTowNotice==>:", commandPrinter.toString())

        return Pair(commandPrinter, yAxis)
    }

    fun setBottomAddressYBasedOnFontSize(fontSize: String?): Int {
        val size = fontSize?.toIntOrNull()

        bottomIncrementY = when (size) {
            0 -> 20
            1 -> 40
            2 -> 60
            else -> 20 // fallback default if null or unexpected input
        }

        return bottomIncrementY
    }
}