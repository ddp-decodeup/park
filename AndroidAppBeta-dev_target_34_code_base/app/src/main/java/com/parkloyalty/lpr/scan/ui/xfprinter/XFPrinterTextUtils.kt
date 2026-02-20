package com.parkloyalty.lpr.scan.ui.xfprinter

import android.util.Log
import com.parkloyalty.lpr.scan.extensions.fillInTheBlank
import com.parkloyalty.lpr.scan.extensions.getColumnWidthForLabel
import com.parkloyalty.lpr.scan.extensions.getColumnWidthForLabelSummery
import com.parkloyalty.lpr.scan.extensions.getColumnWidthForValue
import com.parkloyalty.lpr.scan.extensions.getColumnWidthForValueSummery
import com.parkloyalty.lpr.scan.extensions.getFontSizeForLabel
import com.parkloyalty.lpr.scan.extensions.getFontSizeForValue
import com.parkloyalty.lpr.scan.extensions.getTextAlignmentForLabel
import com.parkloyalty.lpr.scan.extensions.getTextAlignmentForValue
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.xfprinter.SectionType.NEW_LINE
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH_FOR_LABEL_VALUE
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.CUSTOM_PRINT_COMMAND_SEPARATOR
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.CUSTOM_PRINT_COMMAND_TEXT_ALIGNMENT_FOR_LABEL_VALUE
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.PAD_FILL_FOR_XF_PRINT_COMMAND
import com.parkloyalty.lpr.scan.util.AppUtils.getColumWidthUsingColumnCount
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.twotechnologies.n5library.printer.Fonts

/**
 * Used to define SectionType for print command
 */
object SectionType {
    const val TITLE = "[[TITLE]]"
    const val HEADER = "[[HEADER]]"
    const val BODY = "[[BODY]]"
    const val LINE = "[[LINE]]"
    const val BOX = "[[BOX]]"
    const val OCR_TEXT = "[[OCR_TEXT]]"
    const val BAR_CODE = "[[BAR_CODE]]"
    const val NEW_LINE = "[[NEW_LINE]]"
}

/**
 * Used to add text alignment for print command
 */
object TextAlignmentForCommandPrint {
    const val LEFT = "LEFT"
    const val CENTER = "CENTER"
    const val RIGHT = "RIGHT"
}

/**
 * This function used to get text alignment for print text using code from server
 */
fun getTextAlignmentToPrintFromServerTextAlignmentCode(value: Int): String {
    when (value) {
        1 -> {
            return TextAlignmentForCommandPrint.LEFT
        }

        2 -> {
            return TextAlignmentForCommandPrint.CENTER
        }

        3 -> {
            return TextAlignmentForCommandPrint.RIGHT
        }

        else -> {
            return TextAlignmentForCommandPrint.LEFT
        }
    }
}
/**
 * This function used to get text alignment for print text using code from server
 */
fun getTextAlignmentToPrintFromServerTextAlignmentCodeSummary(value: Int): String {
    when (value) {
        1 -> {
            return TextAlignmentForCommandPrint.LEFT
        }

        2 -> {
            return TextAlignmentForCommandPrint.CENTER
        }

        3 -> {
            return TextAlignmentForCommandPrint.CENTER
        }

        3 -> {
            return TextAlignmentForCommandPrint.RIGHT
        }

        else -> {
            return TextAlignmentForCommandPrint.LEFT
        }
    }
}

/**
 * This function is used to get font for print text using code form server
 */
fun getFontToPrintFromServerFontCode(value: Int): Fonts {
    when (value) {
        10 -> {
            return Fonts.COURIER_25_4_CPI
        }

        11 -> {
            return Fonts.COURIER_22_6_CPI
        }

        12 -> {
            return Fonts.COURIER_20_3_CPI
        }

        13 -> {
            return Fonts.COURIER_18_5_CPI
        }

        14 -> {
            return Fonts.COURIER_16_9_CPI
        }

        15 -> {
            return Fonts.COURIER_15_6_CPI
        }

        16 -> {
            return Fonts.COURIER_14_5_CPI
        }

        17 -> {
            return Fonts.COURIER_13_5_CPI
        }

        18 -> {
            return Fonts.COURIER_12_7_CPI
        }

        19 -> {
            return Fonts.SAN_SERIF_20_3_CPI
        }

        20 -> {
            return Fonts.SAN_SERIF_18_5_CPI
        }

        21 -> {
            return Fonts.SAN_SERIF_16_9_CPI
        }

        22 -> {
            return Fonts.SAN_SERIF_10_7_CPI
        }

        23 -> {
            return Fonts.SAN_SERIF_10_2_CPI
        }

        24 -> {
            return Fonts.SAN_SERIF_5_5_CPI
        }

        25 -> {
            return Fonts.SAN_SERIF_4_2_CPI
        }

        else -> {
            return Fonts.SAN_SERIF_18_5_CPI
        }
    }
}

/**
 * Used to add custom font size to text for print command
 */
object FontSizeForCommandPrint {
    const val FONT_HEADER = 3
    const val FONT_TITLE = 2
    const val FONT_SUBTITLE = 1
}

object FontType {
    const val TYPE_BOLD = 2
    const val TYPE_NORMAL = 1
}

/**
 * Text Utils used for print command
 */
object TextUtils {
    const val PAD_FILL_FOR_XF_PRINT_COMMAND = " "
    const val NEW_LINE = "\n"

    const val CUSTOM_PRINT_COMMAND_SEPARATOR = "[[==]]"
    const val CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH = 13
    const val CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH_FOR_LABEL_VALUE = 808
    const val CUSTOM_PRINT_COMMAND_TEXT_ALIGNMENT_FOR_LABEL_VALUE = 11
    const val FONT_SIZE_FOR_COMMENT_TITLE = 2323 //SAN_SERIF_18_5_CPI
    const val ALIGNMENT_FOR_COMMENT_TITLE = 22 //CENTER
    const val COLUMN_COUNT_FOR_COMMENT_TITLE = CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH_FOR_LABEL_VALUE.toDouble()

    const val XF_PRINTER_BARCODE_HEIGHT = 50
    const val XF_PRINTER_BARCODE_WIDTH = 340


//    const val FONT_SIZE_FOR_COMMENT_TITLE = 11 //SAN_SERIF_18_5_CPI
//    const val ALIGNMENT_FOR_COMMENT_TITLE = 2.0 //CENTER
//    const val COLUMN_COUNT_FOR_COMMENT_TITLE = CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH.toDouble()

    const val FONT_SIZE_FOR_PRINT = 20 //SAN_SERIF_18_5_CPI
    const val ALIGNMENT_FOR_PRINT = 1 //LEFT
    const val COLUMN_COUNT_FOR_PRINT = CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH
}


/**
 * This function is used to generate list of statements to print
 * Basically it works around type, which is basically the column size
 * type = 0 & 1 stands for column size one
 * type = 2 stands for column size two
 * type = 3 stands for column size three
 *
 * this function generate new line of column size 3, so once it get 3 as size, it will move
 * to next line
 */
fun getPrintCommentStatements(printCommand: String) : ArrayList<TextToPrintData> {
    val printList = printCommand.split(CUSTOM_PRINT_COMMAND_SEPARATOR).filter {
        it.contains(SectionType.BODY) || it.contains(SectionType.TITLE) || it.contains(
            SectionType.BAR_CODE
        ) || it.contains(SectionType.NEW_LINE)
    }

    val textToPrint = ArrayList<TextToPrintData>()
    var txtLabel = ""
    var txtValue = ""
    var columCount = 0
    var fontSizeLabel = 0
    var fontSizeValue = 0
    var textAlignmentLabel = 0
    var textAlignmentValue = 0
    var columnWidthForLabel = 0
    var columnWidthForValue = 0
    var fontType = FontType.TYPE_NORMAL


    for (printListObj in printList) {
        try {
            val printStringObj = ObjectMapperProvider.fromJson(printListObj, VehicleListModel::class.java)
            if (LogUtil.isEnableAPILogs) {
                Log.i("Print==>", ObjectMapperProvider.toJson(printStringObj))
                Log.i("Print==>", printStringObj.offNameFirst.toString())
                Log.i("Print==>", printStringObj.offTypeFirst.toString())
                Log.i("Print==>Align", printStringObj.mAxisX.toString())
                Log.i("Print==>Colum", printStringObj.mAxisY.toString())
                Log.i("Print==>Font", printStringObj.mFontSizeInt.toString())
                Log.i("Print==>", "==================================")
            }

            if (printStringObj.sectionType == SectionType.NEW_LINE) {
                textToPrint.add(
                    TextToPrintData(
                        textToPrint = SectionType.NEW_LINE,
                        fontSize = fontSizeLabel
                    )
                )
            } else {

                //1#19#7
                //13#1919#1723

                //val textAlignment = printStringObj.mTextAlignment

                //Variable used to get column max width to split for label & value
                val columnMaxSize = if (printStringObj.mAxisY.nullSafety(
                        CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH_FOR_LABEL_VALUE.toDouble()
                    ).toInt() > 0
                ) {
                    printStringObj.mAxisY.nullSafety(
                        CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH_FOR_LABEL_VALUE.toDouble()
                    ).toInt()
                } else {
                    0
                }

                //Variable used to get text alignment to split for label & value
                val columnTextAlignment = if (printStringObj.mAxisX.nullSafety(
                        CUSTOM_PRINT_COMMAND_TEXT_ALIGNMENT_FOR_LABEL_VALUE.toDouble()
                    ).toInt() > 0
                ) {
                    printStringObj.mAxisX.nullSafety(
                        CUSTOM_PRINT_COMMAND_TEXT_ALIGNMENT_FOR_LABEL_VALUE.toDouble()
                    ).toInt()
                } else {
                    CUSTOM_PRINT_COMMAND_TEXT_ALIGNMENT_FOR_LABEL_VALUE
                }

//                val fontType = printStringObj.fontTypeForXFPrinter.nullSafety(FontType.TYPE_NORMAL)
                if (printStringObj.fontTypeForXFPrinter != null){
                    fontType = printStringObj.fontTypeForXFPrinter.nullSafety(FontType.TYPE_NORMAL)
                }

                when (printStringObj.type) {
                    0, 1 -> {
                        //If this is the last item of the list then we have to make it 3 to print whole line
                        if (printListObj == printList.last()) {
                            columCount = 3
                        }else{
                            columCount += 1
                        }

                        if (printStringObj.mFontSizeInt != null) {
                            fontSizeLabel =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForLabel()
                            fontSizeValue =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForValue()
                        }

                        textAlignmentLabel =
                            columnTextAlignment.nullSafety().getTextAlignmentForLabel()
                        textAlignmentValue =
                            columnTextAlignment.nullSafety().getTextAlignmentForValue()

                        if (columnMaxSize > 0) {
                            columnWidthForLabel =
                                columnMaxSize.nullSafety().getColumnWidthForLabel()
                            columnWidthForValue =
                                columnMaxSize.nullSafety().getColumnWidthForValue()
                        }


                        when (columCount) {
                            1 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )
                            }

                            2 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )
                            }

                            3 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )

                                //This will title only if this has some value in it
                                if (txtLabel.trim().isNotEmpty())
                                    textToPrint.add(
                                        TextToPrintData(
                                            textToPrint = txtLabel,
                                            fontSize = fontSizeLabel,
                                            fontType = fontType
                                        )
                                    )

                                //This will title only if this has some value in it
                                if (txtValue.trim().isNotEmpty())
                                    textToPrint.add(
                                        TextToPrintData(
                                            textToPrint = txtValue,
                                            fontSize = fontSizeValue,
                                            fontType = fontType
                                        )
                                    )

                                columCount = 0
                                txtLabel = ""
                                txtValue = ""
                                fontSizeLabel = 0
                                fontSizeValue = 0
                                textAlignmentLabel = 0
                                textAlignmentValue = 0
                                columnWidthForLabel = 0
                                columnWidthForValue = 0
                                fontType = FontType.TYPE_NORMAL
                            }
                        }
                    }

                    2 -> {
                        //If this is the last item of the list then we have to make it 3 to print whole line
                        if (printListObj == printList.last()) {
                            columCount = 3
                        }else{
                            columCount += 2
                        }

                        if (printStringObj.mFontSizeInt != null) {
                            fontSizeLabel =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForLabel()
                            fontSizeValue =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForValue()
                        }

                        textAlignmentLabel =
                            columnTextAlignment.nullSafety().getTextAlignmentForLabel()
                        textAlignmentValue =
                            columnTextAlignment.nullSafety().getTextAlignmentForValue()
                        if (columnMaxSize > 0) {
                            columnWidthForLabel =
                                columnMaxSize.nullSafety().getColumnWidthForLabel()
                            columnWidthForValue =
                                columnMaxSize.nullSafety().getColumnWidthForValue()
                        }

                        when (columCount) {
                            2 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )
                            }

                            3 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )

                                if (txtLabel.trim().isNotEmpty())
                                    textToPrint.add(
                                        TextToPrintData(
                                            textToPrint = txtLabel,
                                            fontSize = fontSizeLabel,
                                            fontType = fontType
                                        )
                                    )

                                if (txtValue.trim().isNotEmpty())
                                    textToPrint.add(
                                        TextToPrintData(
                                            textToPrint = txtValue,
                                            fontSize = fontSizeValue,
                                            fontType = fontType
                                        )
                                    )

                                columCount = 0
                                txtLabel = ""
                                txtValue = ""
                                fontSizeLabel = 0
                                fontSizeValue = 0
                                textAlignmentLabel = 0
                                textAlignmentValue = 0
                                columnWidthForLabel = 0
                                columnWidthForValue = 0
                                fontType = FontType.TYPE_NORMAL
                            }
                        }
                    }

                    3 -> {
                        if (printStringObj.mFontSizeInt != null) {
                            fontSizeLabel =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForLabel()
                            fontSizeValue =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForValue()
                        }

                        textAlignmentLabel =
                            columnTextAlignment.nullSafety().getTextAlignmentForLabel()
                        textAlignmentValue =
                            columnTextAlignment.nullSafety().getTextAlignmentForValue()
                        if (columnMaxSize > 0) {
                            columnWidthForLabel =
                                columnMaxSize.nullSafety().getColumnWidthForLabel()
                            columnWidthForValue =
                                columnMaxSize.nullSafety().getColumnWidthForValue()
                        }


                        if (printStringObj.sectionType == SectionType.NEW_LINE) {
                            textToPrint.add(
                                TextToPrintData(
                                    textToPrint = NEW_LINE,
                                    fontSize = fontSizeLabel,
                                    fontType = fontType
                                )
                            )
                        } else {
                            txtLabel = printStringObj.offNameFirst.nullSafety().fillInTheBlank(
                                getColumWidthUsingColumnCount(3, columnWidthForLabel),
                                PAD_FILL_FOR_XF_PRINT_COMMAND,
                                getTextAlignmentToPrintFromServerTextAlignmentCode(
                                    textAlignmentLabel.nullSafety()
                                )
                            )
                            txtValue = printStringObj.offTypeFirst.nullSafety().fillInTheBlank(
                                getColumWidthUsingColumnCount(3, columnWidthForValue),
                                PAD_FILL_FOR_XF_PRINT_COMMAND,
                                getTextAlignmentToPrintFromServerTextAlignmentCode(
                                    textAlignmentValue.nullSafety()
                                )
                            )

                            if (txtLabel.trim().isNotEmpty())
                                textToPrint.add(
                                    TextToPrintData(
                                        textToPrint = txtLabel,
                                        fontSize = fontSizeLabel,
                                        fontType = fontType
                                    )
                                )

                            if (txtValue.trim().isNotEmpty())
                                textToPrint.add(
                                    TextToPrintData(
                                        textToPrint = txtValue,
                                        fontSize = fontSizeValue,
                                        fontType = fontType
                                    )
                                )

                            columCount = 0
                            txtLabel = ""
                            txtValue = ""
                            fontSizeLabel = 0
                            fontSizeValue = 0
                            textAlignmentLabel = 0
                            textAlignmentValue = 0
                            columnWidthForLabel = 0
                            columnWidthForValue = 0
                            fontType = FontType.TYPE_NORMAL
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    LogUtil.printLog("QUERYTOPRINT==>:",ObjectMapperProvider.instance.writeValueAsString(textToPrint))

    return textToPrint;
}
/**
 * This function is used to generate list of statements to print
 * Basically it works around type, which is basically the column size
 * type = 0 & 1 stands for column size one
 * type = 2 stands for column size two
 * type = 3 stands for column size three
 *
 * this function generate new line of column size 3, so once it get 3 as size, it will move
 * to next line
 */
fun getPrintCommentStatementsSummay(printCommand: String) : ArrayList<TextToPrintData> {
    val printList = printCommand.split(CUSTOM_PRINT_COMMAND_SEPARATOR).filter {
        it.contains(SectionType.BODY) || it.contains(SectionType.TITLE) || it.contains(
            SectionType.BAR_CODE
        ) || it.contains(SectionType.NEW_LINE)
    }

    val textToPrint = ArrayList<TextToPrintData>()
    var txtLabel = ""
    var txtValue = ""
    var columCount = 0
    var fontSizeLabel = 0
    var fontSizeValue = 0
    var textAlignmentLabel = 0
    var textAlignmentValue = 0
    var columnWidthForLabel = 0
    var columnWidthForValue = 0
    var fontType = FontType.TYPE_NORMAL


    for (printListObj in printList) {
        try {
            val printStringObj = ObjectMapperProvider.fromJson(printListObj, VehicleListModel::class.java)

            if (LogUtil.isEnableAPILogs) {
                Log.i("Print==>", ObjectMapperProvider.toJson(printStringObj))
                Log.i("Print==>", printStringObj.offNameFirst.toString())
                Log.i("Print==>", printStringObj.offTypeFirst.toString())
                Log.i("Print==>Align", printStringObj.mAxisX.toString())
                Log.i("Print==>Colum", printStringObj.mAxisY.toString())
                Log.i("Print==>Font", printStringObj.mFontSizeInt.toString())
                Log.i("Print==>", "==================================")
            }

            if (printStringObj.sectionType == SectionType.NEW_LINE) {
                textToPrint.add(
                    TextToPrintData(
                        textToPrint = SectionType.NEW_LINE,
                        fontSize = fontSizeLabel
                    )
                )
            } else {

                //1#19#7
                //13#1919#1723

                //val textAlignment = printStringObj.mTextAlignment

                //Variable used to get column max width to split for label & value
                val columnMaxSize = if (printStringObj.mAxisY.nullSafety(
                        CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH_FOR_LABEL_VALUE.toDouble()
                    ).toInt() > 0
                ) {
                    printStringObj.mAxisY.nullSafety(
                        CUSTOM_PRINT_COMMAND_PER_COLUMN_WIDTH_FOR_LABEL_VALUE.toDouble()
                    ).toInt()
                } else {
                    0
                }

                //Variable used to get text alignment to split for label & value
                val columnTextAlignment = if (printStringObj.mAxisX.nullSafety(
                        CUSTOM_PRINT_COMMAND_TEXT_ALIGNMENT_FOR_LABEL_VALUE.toDouble()
                    ).toInt() > 0
                ) {
                    printStringObj.mAxisX.nullSafety(
                        CUSTOM_PRINT_COMMAND_TEXT_ALIGNMENT_FOR_LABEL_VALUE.toDouble()
                    ).toInt()
                } else {
                    CUSTOM_PRINT_COMMAND_TEXT_ALIGNMENT_FOR_LABEL_VALUE
                }

//                val fontType = printStringObj.fontTypeForXFPrinter.nullSafety(FontType.TYPE_NORMAL)
                if (printStringObj.fontTypeForXFPrinter != null){
                    fontType = printStringObj.fontTypeForXFPrinter.nullSafety(FontType.TYPE_NORMAL)
                }

                when (printStringObj.type) {
                    0, 1 -> {
                        //If this is the last item of the list then we have to make it 3 to print whole line
                        if (printListObj == printList.last()) {
                            columCount = 4
                        }else{
                            columCount += 1
                        }

                        if (printStringObj.mFontSizeInt != null) {
                            fontSizeLabel =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForLabel()
                            fontSizeValue =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForValue()
                        }

                        textAlignmentLabel = 1
//                            columnTextAlignment.nullSafety().getTextAlignmentForLabel()
                        textAlignmentValue = 1
//                            columnTextAlignment.nullSafety().getTextAlignmentForValue()

                        if (columnMaxSize > 0) {
                            columnWidthForLabel =
                                columnMaxSize.nullSafety().getColumnWidthForLabelSummery()
                            columnWidthForValue =
                                columnMaxSize.nullSafety().getColumnWidthForValueSummery()
                        }


                        when (columCount) {
                            1 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )
                            }

                            2 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )
                            }

                            3 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )

//                                //This will title only if this has some value in it
//                                if (txtLabel.trim().isNotEmpty())
//                                    textToPrint.add(
//                                        TextToPrintData(
//                                            textToPrint = txtLabel,
//                                            fontSize = fontSizeLabel,
//                                            fontType = fontType
//                                        )
//                                    )
//
//                                //This will title only if this has some value in it
//                                if (txtValue.trim().isNotEmpty())
//                                    textToPrint.add(
//                                        TextToPrintData(
//                                            textToPrint = txtValue,
//                                            fontSize = fontSizeValue,
//                                            fontType = fontType
//                                        )
//                                    )

                            }

                            4 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(1, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )

                                //This will title only if this has some value in it
                                if (txtLabel.trim().isNotEmpty())
                                    textToPrint.add(
                                        TextToPrintData(
                                            textToPrint = txtLabel,
                                            fontSize = fontSizeLabel,
                                            fontType = fontType
                                        )
                                    )

                                //This will title only if this has some value in it
                                if (txtValue.trim().isNotEmpty())
                                    textToPrint.add(
                                        TextToPrintData(
                                            textToPrint = txtValue,
                                            fontSize = fontSizeValue,
                                            fontType = fontType
                                        )
                                    )

                                columCount = 0
                                txtLabel = ""
                                txtValue = ""
                                fontSizeLabel = 0
                                fontSizeValue = 0
                                textAlignmentLabel = 0
                                textAlignmentValue = 0
                                columnWidthForLabel = 0
                                columnWidthForValue = 0
                                fontType = FontType.TYPE_NORMAL
                            }
                        }
                    }

                    2 -> {
                        //If this is the last item of the list then we have to make it 3 to print whole line
                        if (printListObj == printList.last()) {
                            columCount = 4
                        }else{
                            columCount += 2
                        }

                        if (printStringObj.mFontSizeInt != null) {
                            fontSizeLabel =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForLabel()
                            fontSizeValue =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForValue()
                        }

                        textAlignmentLabel =
                            columnTextAlignment.nullSafety().getTextAlignmentForLabel()
                        textAlignmentValue =
                            columnTextAlignment.nullSafety().getTextAlignmentForValue()
                        if (columnMaxSize > 0) {
                            columnWidthForLabel =
                                columnMaxSize.nullSafety().getColumnWidthForLabelSummery()
                            columnWidthForValue =
                                columnMaxSize.nullSafety().getColumnWidthForValueSummery()
                        }

                        when (columCount) {
                            2 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )
                            }

                            3 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )

//                                if (txtLabel.trim().isNotEmpty())
//                                    textToPrint.add(
//                                        TextToPrintData(
//                                            textToPrint = txtLabel,
//                                            fontSize = fontSizeLabel,
//                                            fontType = fontType
//                                        )
//                                    )
//
//                                if (txtValue.trim().isNotEmpty())
//                                    textToPrint.add(
//                                        TextToPrintData(
//                                            textToPrint = txtValue,
//                                            fontSize = fontSizeValue,
//                                            fontType = fontType
//                                        )
//                                    )

                            }

                            4 -> {
                                txtLabel += printStringObj.offNameFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForLabel),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentLabel.nullSafety()
                                        )
                                    )
                                txtValue += printStringObj.offTypeFirst.nullSafety()
                                    .fillInTheBlank(
                                        getColumWidthUsingColumnCount(2, columnWidthForValue),
                                        PAD_FILL_FOR_XF_PRINT_COMMAND,
                                        getTextAlignmentToPrintFromServerTextAlignmentCode(
                                            textAlignmentValue.nullSafety()
                                        )
                                    )

                                if (txtLabel.trim().isNotEmpty())
                                    textToPrint.add(
                                        TextToPrintData(
                                            textToPrint = txtLabel,
                                            fontSize = fontSizeLabel,
                                            fontType = fontType
                                        )
                                    )

                                if (txtValue.trim().isNotEmpty())
                                    textToPrint.add(
                                        TextToPrintData(
                                            textToPrint = txtValue,
                                            fontSize = fontSizeValue,
                                            fontType = fontType
                                        )
                                    )

                                columCount = 0
                                txtLabel = ""
                                txtValue = ""
                                fontSizeLabel = 0
                                fontSizeValue = 0
                                textAlignmentLabel = 0
                                textAlignmentValue = 0
                                columnWidthForLabel = 0
                                columnWidthForValue = 0
                                fontType = FontType.TYPE_NORMAL
                            }
                        }
                    }

                    3 -> {
                        if (printStringObj.mFontSizeInt != null) {
                            fontSizeLabel =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForLabel()
                            fontSizeValue =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForValue()
                        }

                        textAlignmentLabel =
                            columnTextAlignment.nullSafety().getTextAlignmentForLabel()
                        textAlignmentValue =
                            columnTextAlignment.nullSafety().getTextAlignmentForValue()
                        if (columnMaxSize > 0) {
                            columnWidthForLabel =
                                columnMaxSize.nullSafety().getColumnWidthForLabelSummery()
                            columnWidthForValue =
                                columnMaxSize.nullSafety().getColumnWidthForValueSummery()
                        }


                        if (printStringObj.sectionType == SectionType.NEW_LINE) {
                            textToPrint.add(
                                TextToPrintData(
                                    textToPrint = NEW_LINE,
                                    fontSize = fontSizeLabel,
                                    fontType = fontType
                                )
                            )
                        } else {
                            txtLabel = printStringObj.offNameFirst.nullSafety().fillInTheBlank(
                                getColumWidthUsingColumnCount(3, columnWidthForLabel),
                                PAD_FILL_FOR_XF_PRINT_COMMAND,
                                getTextAlignmentToPrintFromServerTextAlignmentCode(
                                    textAlignmentLabel.nullSafety()
                                )
                            )
                            txtValue = printStringObj.offTypeFirst.nullSafety().fillInTheBlank(
                                getColumWidthUsingColumnCount(3, columnWidthForValue),
                                PAD_FILL_FOR_XF_PRINT_COMMAND,
                                getTextAlignmentToPrintFromServerTextAlignmentCode(
                                    textAlignmentValue.nullSafety()
                                )
                            )

//                            if (txtLabel.trim().isNotEmpty())
//                                textToPrint.add(
//                                    TextToPrintData(
//                                        textToPrint = txtLabel,
//                                        fontSize = fontSizeLabel,
//                                        fontType = fontType
//                                    )
//                                )
//
//                            if (txtValue.trim().isNotEmpty())
//                                textToPrint.add(
//                                    TextToPrintData(
//                                        textToPrint = txtValue,
//                                        fontSize = fontSizeValue,
//                                        fontType = fontType
//                                    )
//                                )

                        }
                    }

                    4 -> {
                        if (printStringObj.mFontSizeInt != null) {
                            fontSizeLabel =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForLabel()
                            fontSizeValue =
                                printStringObj.mFontSizeInt.nullSafety().getFontSizeForValue()
                        }

                        textAlignmentLabel =
                            columnTextAlignment.nullSafety().getTextAlignmentForLabel()
                        textAlignmentValue =
                            columnTextAlignment.nullSafety().getTextAlignmentForValue()
                        if (columnMaxSize > 0) {
                            columnWidthForLabel =
                                columnMaxSize.nullSafety().getColumnWidthForLabelSummery()
                            columnWidthForValue =
                                columnMaxSize.nullSafety().getColumnWidthForValueSummery()
                        }


                        if (printStringObj.sectionType == SectionType.NEW_LINE) {
                            textToPrint.add(
                                TextToPrintData(
                                    textToPrint = NEW_LINE,
                                    fontSize = fontSizeLabel,
                                    fontType = fontType
                                )
                            )
                        } else {
                            txtLabel = printStringObj.offNameFirst.nullSafety().fillInTheBlank(
                                getColumWidthUsingColumnCount(3, columnWidthForLabel),
                                PAD_FILL_FOR_XF_PRINT_COMMAND,
                                getTextAlignmentToPrintFromServerTextAlignmentCode(
                                    textAlignmentLabel.nullSafety()
                                )
                            )
                            txtValue = printStringObj.offTypeFirst.nullSafety().fillInTheBlank(
                                getColumWidthUsingColumnCount(3, columnWidthForValue),
                                PAD_FILL_FOR_XF_PRINT_COMMAND,
                                getTextAlignmentToPrintFromServerTextAlignmentCode(
                                    textAlignmentValue.nullSafety()
                                )
                            )

                            if (txtLabel.trim().isNotEmpty())
                                textToPrint.add(
                                    TextToPrintData(
                                        textToPrint = txtLabel,
                                        fontSize = fontSizeLabel,
                                        fontType = fontType
                                    )
                                )

                            if (txtValue.trim().isNotEmpty())
                                textToPrint.add(
                                    TextToPrintData(
                                        textToPrint = txtValue,
                                        fontSize = fontSizeValue,
                                        fontType = fontType
                                    )
                                )

                            columCount = 0
                            txtLabel = ""
                            txtValue = ""
                            fontSizeLabel = 0
                            fontSizeValue = 0
                            textAlignmentLabel = 0
                            textAlignmentValue = 0
                            columnWidthForLabel = 0
                            columnWidthForValue = 0
                            fontType = FontType.TYPE_NORMAL
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    LogUtil.printLog("QUERYTOPRINT==>:",ObjectMapperProvider.instance.writeValueAsString(textToPrint))

    return textToPrint;
}