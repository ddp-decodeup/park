package com.parkloyalty.lpr.scan.extensions

import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.xfprinter.SectionType
import com.parkloyalty.lpr.scan.ui.xfprinter.TextAlignmentForCommandPrint
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.ALIGNMENT_FOR_COMMENT_TITLE
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.ALIGNMENT_FOR_PRINT
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.COLUMN_COUNT_FOR_COMMENT_TITLE
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.COLUMN_COUNT_FOR_PRINT
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.CUSTOM_PRINT_COMMAND_SEPARATOR
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.FONT_SIZE_FOR_COMMENT_TITLE
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.FONT_SIZE_FOR_PRINT
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider


/**
 * This function is used to split first two char as font size for label
 */
fun Int.getFontSizeForLabel(): Int {
    val str = this.toString()
    return try {
        str.substring(0, 2).toInt()
    } catch (e: Exception) {
        FONT_SIZE_FOR_PRINT
    }
}

/**
 * This function is used to split last two char as font size for value
 */
fun Int.getFontSizeForValue(): Int {
    val str = this.toString()
    return try {
        str.substring(2, 4).toInt()
    } catch (e: Exception) {
        FONT_SIZE_FOR_PRINT
    }
}

/**
 * This function is used to split first two char as column width for label
 */
fun Int.getColumnWidthForLabel(): Int {
    val str = this.toString()
    return if (str.length == 3) {
        try {
            str.substring(0, 1).toInt()
        } catch (e: Exception) {
            COLUMN_COUNT_FOR_PRINT
        }
    } else {
        try {
            str.substring(0, 2).toInt()
        } catch (e: Exception) {
            COLUMN_COUNT_FOR_PRINT
        }
    }
}

/**
 * This function is used to split last two char as column width for value
 */
fun Int.getColumnWidthForValue(): Int {
    val str = this.toString()
    return if (str.length == 3) {
        try {
            str.substring(1, 3).toInt()
        } catch (e: Exception) {
            COLUMN_COUNT_FOR_PRINT
        }
    } else {
        try {
            str.substring(2, 4).toInt()
        } catch (e: Exception) {
            COLUMN_COUNT_FOR_PRINT
        }
    }
}
/**
 * This function is used to split first two char as column width for label
 */
fun Int.getColumnWidthForLabelSummery(): Int {
    val str = this.toString()
    return if (str.length == 3) {
        try {
            str.substring(0, 1).toInt()
        } catch (e: Exception) {
            12
        }
    } else {
        try {
            str.substring(0, 2).toInt()
        } catch (e: Exception) {
            12
        }
    }

//    val str = this.toString()
//    return if (str.length == 3) {
//        try {
//            str.substring(1, 3).toInt()
//        } catch (e: Exception) {
//            11
//        }
//    } else {
//        try {
//            str.substring(2, 4).toInt()
//        } catch (e: Exception) {
//            11
//        }
//    }
}

/**
 * This function is used to split last two char as column width for value
 */
fun Int.getColumnWidthForValueSummery(): Int {
    val str = this.toString()
    return if (str.length == 3) {
        try {
            12//str.substring(1, 3).toInt()
        } catch (e: Exception) {
            12
        }
    } else {
        try {
            12//str.substring(2, 4).toInt()
        } catch (e: Exception) {
            12
        }
    }
}


/**
 * This function is used to split first char as alignment for label
 */
fun Int.getTextAlignmentForLabel(): Int {
    val str = this.toString()
    return try {
        str.substring(0, 1).toInt()
    } catch (e: Exception) {
        ALIGNMENT_FOR_PRINT
    }
}

/**
 * This function is used to split second char as alignment for value
 */
fun Int.getTextAlignmentForValue(): Int {
    val str = this.toString()
    return try {
        str.substring(1, 2).toInt()
    } catch (e: Exception) {
        ALIGNMENT_FOR_PRINT
    }
}


/**
 * Function used to fill the empty around given max length for string
 */
fun String.fillInTheBlank(maxLength: Int, pad: String, textAlignment: String): String {
    val sb = StringBuilder()

    if (textAlignment == TextAlignmentForCommandPrint.CENTER) {
        for (i in 0 until (maxLength - this.length) / 2) {
            sb.append(pad)
        }
        sb.append(this)
        while (sb.length < maxLength) {
            sb.append(pad)
        }
    } else {
        if (textAlignment == TextAlignmentForCommandPrint.LEFT)
            sb.append(this)

        val rest: Int = maxLength - this.length

        for (i in 0 until rest) {
            sb.append(pad)
        }

        if (textAlignment == TextAlignmentForCommandPrint.RIGHT)
            sb.append(this)
    }

    return sb.toString()
}

fun setXYforPrintBarCode(
    mObject: VehicleListModel, sectionPosition: Int, sectionName: String,
    commandPrinter: java.lang.StringBuilder
): java.lang.StringBuilder {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
        mObject.sectionType = SectionType.BAR_CODE
        mObject.mAxisX = ALIGNMENT_FOR_COMMENT_TITLE.toDouble()
        mObject.mAxisY = COLUMN_COUNT_FOR_COMMENT_TITLE
        mObject.mFontSizeInt = FONT_SIZE_FOR_COMMENT_TITLE

        val json = ObjectMapperProvider.instance.writeValueAsString(mObject)
        commandPrinter.append(json)
        commandPrinter.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
    }

    return commandPrinter;
}

fun setXYforPrintNewLine(
    mObject: VehicleListModel, sectionPosition: Int, sectionName: String,
    commandPrinter: java.lang.StringBuilder
): java.lang.StringBuilder {

    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
        mObject.sectionType = SectionType.NEW_LINE
        mObject.mAxisX = ALIGNMENT_FOR_COMMENT_TITLE.toDouble()
        mObject.mAxisY = COLUMN_COUNT_FOR_COMMENT_TITLE
        mObject.mFontSizeInt = FONT_SIZE_FOR_COMMENT_TITLE

        val json = ObjectMapperProvider.instance.writeValueAsString(mObject)
        commandPrinter.append(json)
        commandPrinter.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
    }

    return commandPrinter;
}

fun setXYforPrintTitle(
    mObject: VehicleListModel, sectionPosition: Int, sectionName: String,
    commandPrinter: java.lang.StringBuilder
): java.lang.StringBuilder {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
        mObject.sectionType = SectionType.TITLE
        val json = ObjectMapperProvider.instance.writeValueAsString(mObject)
        commandPrinter.append(json)
        commandPrinter.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
    }

    return commandPrinter;
}