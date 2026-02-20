package com.parkloyalty.lpr.scan.extensions

import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.InvoiceFeeStructure
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.LocationDetails
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.getMyDatabase
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.mainScope
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.ExpiryDateTextWatcher.Companion.CARD_DATE_DIVIDER
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_TIMESTAMP
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Random
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.roundToInt

fun String?.nullSafety(defaultValue: String = ""): String {
    return if (this.isNullOrEmpty() || this.equals("null", ignoreCase = true)) {
        defaultValue
    } else {
        this
    }
}

fun String?.nullOrEmptySafety(defaultValue: String = ""): String {
    return if (this.isNullOrEmpty()) {
        defaultValue
    } else {
        this
    }
}

fun Int?.nullSafety(defaultValue: Int = 0): Int {
    return this ?: defaultValue
}

fun Long?.nullSafety(defaultValue: Long = 0L): Long {
    return this ?: defaultValue
}

fun Double?.nullSafety(defaultValue: Double = 0.0): Double {
    return this ?: defaultValue
}

fun Float?.nullSafety(defaultValue: Float = 0F): Float {
    return this ?: defaultValue
}

fun Boolean?.nullSafety(defaultValue: Boolean = false): Boolean {
    return this ?: defaultValue
}

fun BigDecimal?.nullSafety(defaultValue: BigDecimal = BigDecimal(0.0)): BigDecimal {
    return this ?: defaultValue
}

fun Double.convertToInt(): Int {
    return abs(this).nullSafety().toInt()
}

/**
 * This function is used to convert string YES or NO to boolean
 */
fun String?.toBooleanFromYesNo(): Boolean {
    return this.equals("YES", true)
}

fun Any.showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).apply { show() }
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).apply { show() }
}

fun Boolean?.boolToInt(): Int {
    return if (this.nullSafety()) {
        1
    } else {
        0
    }
}

fun Int?.intToBool(): Boolean {
    return this.nullSafety() == 1
}

fun String?.isEmpty(defaultValue : String): String {
    val result: String

    if (this == null || this.trim().isEmpty()) {
        result = defaultValue
    } else {
        result = this.toString()
    }
    return result
}

fun DatasetResponse.getEscalatedFine(unpaidCitation: Int): String {
    when (unpaidCitation) {
        -1, 0 -> {
            return this.violationFine.nullSafety().toString()
        }
        1 -> {
            return this.mViolationLateFine.nullSafety().toString()
        }
        2 -> {
            return this.mEscalated2.nullSafety().toString()
        }
        3 -> {
            return this.mEscalated3.nullSafety().toString()
        }
        4 -> {
            return this.mEscalated4.nullSafety().toString()
        }
        5 -> {
            return this.mEscalated5.nullSafety().toString()
        }
        else -> {
            return this.mEscalated5.nullSafety().toString()
        }
    }
}

fun DuncanBrandingApp13(): String{
    return if(BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOUNT_RAINIER,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PARK_RIDGE,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ALAMEDA_COUNTY_SHERIFF,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ALAMEDA_COUNTY_TRANSIT,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLA,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_NORWALK,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_COVINA,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_KENOSHA,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_WATSONVILLE,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTHOODRIVER,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VISTA_CA,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_INDIANA_BOROUGH,ignoreCase = true)||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOUNT_RAINIER,ignoreCase = true))
    {
        BuildConfig.FLAVOR
    }else{
        ""
    }
}
/**
 * Function used to truncate beat name after 8 digit
 */
fun String?.getOfficerBeatNameForPrint(): String? {
    return if (this?.length.nullSafety() > 8) {
        this?.substring(0, 8)
    } else {
        this
    }
}


fun String?.getFileNameWithExtension(): String {
    if (this.isNullOrEmpty()) {
        return ""
    } else {
        return this.substring(this.lastIndexOf('/') + 1, this.length)
    }
}

fun getCitaitonImageFormat(mCitationNumberId :String,num:Int ): String {
    if (mCitationNumberId.isNullOrEmpty() && mCitationNumberId.length>1) {
        return ""
    } else {
        return mCitationNumberId + "_" + num
    }
}

// lable 160 /10 = 10 for 0 2 fonty style
// data 160 /13 = 12 for 7 0 fonty style
fun String.textAlignInCenter(X1 : Double,X2:Double,division:Int ): String {
    var centerAlignedText:String = " "//mList.get(position).offNameFirst.toString()
//    var endX = 563.0
    val sb = StringBuilder()
    try {
        var maxLength =  (X2 - X1)/division
        for (i in 0 until ((maxLength - this.length) / 2).toInt()) {
            sb.append(centerAlignedText)
        }
        sb.append(this)
        while (sb.length < maxLength) {
            sb.append(centerAlignedText)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return sb.toString()
}

fun spaceCountForWidth(mList : List<VehicleListModel> , position : Int,mEndX : Double ): Double {
    //var endX = mEndX//563.0
    var endX = ZebraCommandPrintUtils.boxXAxisEnd.toDouble()
    try {
//        val positionInfo = position%2

        LogUtil.printLogCommand("===>", "----------------------------------------------")
//        LogUtil.printLogCommand("===>", "$position  ==  $positionInfo ")
        if(mList!!.size>=position || mList.get(position).type == 0||mList.get(position).type == 2) {
            val nextValidIndex = position+1

            for ((index, value) in mList.withIndex().drop(nextValidIndex)) {
                if(mList.get(position).mAxisY == mList.get(index).mAxisY && mList.get(index).mAxisX>0 && mList.get(position).mAxisX<mList.get(index).mAxisX){
                    endX = mList.get(index).mAxisX
                    LogUtil.printLogCommand("if ===>", "$position > $index > ${mList.get(position).mAxisX} = ${value.mAxisX} >> KEY = ${mList.get(position).offNameFirst} > value = ${mList.get(position).offTypeFirst}")
                    break
//                     }else if(mList.get(position).mPrintOrder.toString().substring(1).equals(".5")){
                }else if(mList.get(index).mAxisX>0){
                    //endX = mEndX//563.0
                    endX = ZebraCommandPrintUtils.boxXAxisEnd.toDouble()
                    LogUtil.printLogCommand("else ===>", "$position > $index > ${mList.get(position).mAxisX} = ${endX} > KEY = ${mList.get(position).offNameFirst} >> value = ${mList.get(position).offTypeFirst}")
                    break
                }
            }
        }else {

//                 endX =  560 - mList.get(position).mAxisX // last index
            endX =  endX//563.0 // last index
            LogUtil.printLogCommand("2 else ===>", "$position  > ${mList.get(position).mAxisX} = ${endX} > KEY = ${mList.get(position).offNameFirst} >  value = ${mList.get(position).offTypeFirst}")

        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return endX as Double
}

fun spaceCountForHeight(mList : List<VehicleListModel>, position : Int ): Double {
    var endY = 40.0
    try {
        endY = mList.get(position).mAxisY + 70 //for last line of section like location and zone
        if(mList!!.size!=(position+1)) {
            val nextValidIndex = position+1

            for ((index, value) in mList.withIndex().drop(nextValidIndex)) {
                println("the element at $index is ${value.mAxisY}")
                if(mList.get(index).mAxisY>0 && mList.get(index).mAxisY>mList.get(position).mAxisY){
                    endY = mList.get(index).mAxisY
                    break
                }
            }
        }else {
            endY =  mList.get(position).mAxisY + 70 // last index
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return endY as Double
}

/**
 * This function is used get Equipment title name from the given value
 */
fun String.getEquipmentName(): String? {
    return this.split(":").firstOrNull()
}

/**
 * This function is used to get equipment value from given nvalue
 */
fun String.getEquipmentValue(): String? {
    val splittedValue = this.split(":")
    return if (splittedValue.size == 1) {
        this
    } else {
        splittedValue.lastOrNull()
    }
}

/**
 * Function used to get first char of string
 */
fun String?.getInitials(): Char? {
    return this?.trim()?.firstOrNull()
}

fun openTimePicker(timePickerField: AppCompatAutoCompleteTextView?,mContext : Context,timeFormat:String) {
    val mTimePicker: TimePickerDialog
    val mcurrentTime = Calendar.getInstance()
    val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
    val minute = mcurrentTime.get(Calendar.MINUTE)
//    val df1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val df1 = SimpleDateFormat("yyyy-MM-dd")
    var specialReportTimeFormat = ""
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val current = LocalDateTime.now()
        specialReportTimeFormat = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    } else {
        specialReportTimeFormat = df1.format(mcurrentTime.time)
    }
//    val specialReportTimeFormat = current.format(DateTimeFormatter.ofPattern("dd-MM-yy'T'HH:mm:ss'Z'"))

    mTimePicker = TimePickerDialog(mContext, object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            if (timeFormat.equals("MILITARY")) {
                mainScope.launch {
                    timePickerField!!.setText(String.format("%02d%02d", hourOfDay, minute))
//                    var am_pm = ""
//                    val datetime = Calendar.getInstance()
//                    datetime[Calendar.HOUR_OF_DAY] = hourOfDay
//                    datetime[Calendar.MINUTE] = minute
//
//                    if (datetime[Calendar.AM_PM] === Calendar.AM) am_pm =
//                        "AM" else if (datetime[Calendar.AM_PM] === Calendar.PM) am_pm = "PM"
//
//                    val strHrsToShow =
//                        if (datetime[Calendar.HOUR] === 0) "12" else datetime[Calendar.HOUR]
//
//                    timePickerField!!.post {
//                        timePickerField!!.setText(
//                            "" + strHrsToShow + ":" + String.format("%02d", datetime[Calendar.MINUTE]) + " " + am_pm
//                        )
//                    }
                }
            } else {
                mainScope.launch {
                    var am_pm = ""
                    val datetime = Calendar.getInstance()
                    datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                    datetime[Calendar.MINUTE] = minute

                    if (datetime[Calendar.AM_PM] === Calendar.AM) am_pm =
                        "AM" else if (datetime[Calendar.AM_PM] === Calendar.PM) am_pm = "PM"

                    val strHrsToShow =
                        if (datetime[Calendar.HOUR] === 0) "12" else datetime[Calendar.HOUR]

                    timePickerField!!.post {
                        timePickerField!!.setText(
                            "" + strHrsToShow + ":" + String.format("%02d", datetime[Calendar.MINUTE]) + " " + am_pm
                        )
                    }
                }
            }
//            timePickerField!!.setTag(specialReportTimeFormat + "T" + hourOfDay + ":" + minute + ":11Z")
            timePickerField!!.setError(null)
        }
    }, hour, minute, false)
    mTimePicker.show()
}

//Function used to get Month from the string by spliting using / and take first index
fun String.getMonth(): String {
    val splitMMYY = this.split(CARD_DATE_DIVIDER)
    return splitMMYY.first()
}

//Function used to get Year from the string by spliting using / and take last index
fun String.getYear(): String {
    val splitMMYY = this.split(CARD_DATE_DIVIDER)
    return splitMMYY.last()
}

fun buildLocationDetails(): LocationDetails {
    return LocationDetails().apply {
        street = ""
        block = ""
        meter = ""
        side = ""
        lot = ""
        street_lookup_code = ""
        mSpaceId = ""
        mImpoundCode = ""
    }
}

fun buildInvoiceFeeStructure(): InvoiceFeeStructure {
    return InvoiceFeeStructure().apply {
        mParkingFee = 0.0
        mCitationFee = 0.0
        mSaleTax = 0.0
    }
}

fun buildTicketType(): String {
    val typeBuilder = StringBuilder()
     typeBuilder.append("Warning")
    return typeBuilder.toString()
}


fun String.doubleLeadingWhitespace(): String {
    val leadingWhitespace = this.takeWhile { it == ' ' }
    val doubledWhitespace = leadingWhitespace.repeat(2)
    val trimmedText = this.removePrefix(leadingWhitespace)
    return doubledWhitespace + trimmedText
}

fun String.repeatLeadingWhitespaceOneAndHalf(): String {
    val leadingWhitespace = this.takeWhile { it == ' ' }
    val count = leadingWhitespace.length
    val newCount = (count * 1.5).roundToInt()  // Round down, or use `roundToInt()` if needed

    val newPrefix = " ".repeat(newCount)
    val trimmed = this.removePrefix(leadingWhitespace)

    return newPrefix + trimmed
}

/**
 * Function used to verify month value & convert it to "00" if empty
 */
fun String?.verifyAndConvertMonth(): String {
    return if (this.isNullOrEmpty()|| this.equals("null", ignoreCase = true)) {
        "00"
    } else {
        this
    }
}

/**
 * Function used to verify year value & convert it to "00" if empty
 */
fun String?.verifyAndConvertYear(): String {
    return if (this.isNullOrEmpty()|| this.equals("null", ignoreCase = true)) {
        "00"
    } else {
        this
    }
}

/**
 * Function used to verify month value & convert it to null to set text if it is "00"
 */
fun String?.getMonthForSetText(): String? {
    val month = this?.split("/")?.firstOrNull()
    return if (month.isNullOrEmpty()) {
        ""
    } else {
        if (month == "00") {
            ""
        } else {
            month
        }
    }
}
/**
 * Function used to verify month value & convert it to null to set text if it is "00"
 */
fun String?.getMonthForSetTextByIndex(): String? {
    val monthIndex = this?.split("/")?.firstOrNull()
    val monthsList = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December", ""
    )

    val expirationMonth = monthIndex?.getMonthForSetText()

    val monthIndexFinal = expirationMonth?.toIntOrNull()?.let {
        // Subtract 1 to convert "04" -> index 3 (April)
        if (it in 1..12) it - 1 else 12  // if not in 1..12, set to last item (empty string)
    } ?: 12 // default to last item if null or invalid

    return monthsList[monthIndexFinal]
}

/**
 * Function used to verify year value & convert it to null to set text if it is "00"
 */
fun String?.getYearFotSetText(): String? {
    val year = this?.split("/")?.lastOrNull()
    return if (year.isNullOrEmpty()) {
        ""
    } else {
        if (year == "00") {
            ""
        } else {
            year
        }
    }
}

/**
 * This function take text as input and returns a multiline set of list based on limit
 */
fun String.splitTextToMultiline(limit: Int): ArrayList<String> {
    val multilineStringList = ArrayList<String>()

    val regex = "\\s*(.{1,$limit})(?!\\S)|\\s*(.{$limit})"
    val pattern = Pattern.compile(regex);


    val matcher: Matcher = pattern.matcher(this)
    while (matcher.find()) {
        val group1: String? = matcher.group(1)
        val group2: String? = matcher.group(2)

        if (group1 != null) {
            multilineStringList.add(group1)
        } else {
            multilineStringList.add(group2.nullSafety())
        }
    }

    return multilineStringList
}

/**
 * This function is used to reformat float value and return abs value
 */
fun Float.toFormatInt(): Int {
    val decimalFormat = DecimalFormat("0.#")
    return decimalFormat.format(this.toDouble()).toInt().nullSafety(0)
}

/**
 * Function used to fetch setting files values
 */
fun getSettingFileValuesForCrossButton(): Boolean {
    val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

    val isEnabled = settingsList?.firstOrNull {
        it.type.equals(Constants.SETTINGS_FLAG_SHOW_CLEAR_ICON_FOR_INPUT_FIELDS, ignoreCase = true)
                && it.mValue.toBooleanFromYesNo()
    }?.mValue.toBooleanFromYesNo() ?: false

    return isEnabled
}

/**
 * Function used to fetch setting files values
 */
fun getSettingFileValuesForCMDPrinting(): Boolean {
    val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

    val isEnabled = settingsList?.firstOrNull {
        it.type.equals(Constants.SETTINGS_FLAG_PRINTINGBY, ignoreCase = true)
                && it.mValue.equals("CMD",true)
    }

    return isEnabled != null
}

fun String.capitaliseEachWord(): String {
    val regex = "(\\b[a-z](?!\\s))".toRegex()
    return this.replace(regex) { it.value.uppercase() }
}

fun String.capitalizeWords(delimiter: String = " ") =
    split(delimiter).joinToString(delimiter) { word ->

        val smallCaseWord = word.lowercase()
        smallCaseWord.replaceFirstChar(Char::titlecaseChar)

    }

// Extension functions for AppCompatButton to disable the button with visual feedback
fun AppCompatButton.disableButton() {
    isEnabled = false
    alpha = 0.5f
}

// Extension function to enable the button with visual feedback
fun AppCompatButton.enableButton() {
    isEnabled = true
    alpha = 1.0f
}

// Extension functions for Material Button to disable the button with visual feedback
fun MaterialButton.disableButton() {
    isEnabled = false
    alpha = 0.5f
}

// Extension function to enable the button with visual feedback
fun MaterialButton.enableButton() {
    isEnabled = true
    alpha = 1.0f
}

/**
 * Function used to fetch setting files values
 */
fun getSettingFileValuesForRemarkAutoFilledWithElapsedTime(): Boolean {
    val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

    val isEnabled = settingsList?.firstOrNull {
        it.type.equals(Constants.SETTINGS_FLAG_REMARK_AUTO_FILLED_WITH_ELAPSED_TIME, ignoreCase = true)
                && it.mValue.toBooleanFromYesNo()
    }?.mValue.toBooleanFromYesNo() ?: false

    return isEnabled
}
/**
 * Function used to fetch setting files values for Hearing date THRESHOLD
 */
fun getSettingFileValuesForHearingDateThresholdDays(): Int {
    val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

    val value = settingsList?.firstOrNull {
        it.type.equals(Constants.SETTINGS_FLAG_HEARING_DATE_THRESHOLD_DAY, ignoreCase = true)
    }?.mValue

    return value?.toIntOrNull() ?: 0
}
/**
 * Function used to fetch setting files values for Narrow Down Street and Block based on ZOne selectoin
 */
fun getSettingFileValuesForNarrowDownStreetAndBlockListBasedOnSelectionZone(): Boolean {
    val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

    val isEnabled = settingsList?.firstOrNull {
        it.type.equals(Constants.SETTINGS_FLAG_NARROW_DOWN_STREET_BLOCK_TO_SELECTION_ZONE, ignoreCase = true)
                && it.mValue.toBooleanFromYesNo()
    }?.mValue.toBooleanFromYesNo() ?: false

    return isEnabled
}

fun String.getYearForExpiryDate(actualYear: Int): String {
    // Check year format
    val yearFormat = when {
        this.contains("yyyy") -> "yyyy"
        this.contains("yy") -> "yy"
        else -> "yyyy" // default fallback
    }

    // Convert year accordingly
    val formattedYear = when (yearFormat) {
        "yy" -> actualYear % 100   // → 56 if year=2056
        else -> actualYear         // → 2056
    }

    return formattedYear.toString()
}

fun clearShardValueForPrintValue(sharedPreference:SharedPref)
{
    try {
        sharedPreference.write(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.QRCODE_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.HEADER_1_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_1_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_1_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_1_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.HEADER_2_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_2_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_2_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_2_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.HEADER_3_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_3_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_3_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.HEADER_3_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        //Clearing Footer 5 Value
        sharedPreference.write(
            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_FONT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_FONT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_FONT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER11_LABEL_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER11_LABEL_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER11_LABEL_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER11_LABEL_FOR_PRINT_FONT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER12_LABEL_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER12_LABEL_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER12_LABEL_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER12_LABEL_FOR_PRINT_FONT)

        //Clearing Shared Pref Value for Lines
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_HEIGHT)

        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_X)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_Y)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT)
        sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_HEIGHT)


        sharedPreference.write(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_X, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_Y, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT, ""
        ).toString()
        sharedPreference.write(
            SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_FONT, ""
        ).toString()

        sharedPreference.write(
            SharedPrefKey.QRCODE_FOR_PRINT_X, ""
        )
        sharedPreference.write(
            SharedPrefKey.QRCODE_FOR_PRINT_Y, ""
        )


        sharedPreference.write(SharedPrefKey.LOGIN_HEARING_TIME,"")
        sharedPreference.write(SharedPrefKey.LOGIN_HEARING_DATE,"")

//        sharedPreference.write(
//            SharedPrefKey.APP_LOGO_FOR_PRINT_X, ""
//        ).toString()
//        sharedPreference.write(
//            SharedPrefKey.APP_LOGO_FOR_PRINT_Y, ""
//        ).toString()
//        sharedPreference.write(
//            SharedPrefKey.APP_LOGO_FOR_PRINT, ""
//        ).toString()
//        sharedPreference.write(
//            SharedPrefKey.APP_LOGO_FOR_PRINT_WIDTH, ""
//        ).toString()
//
//        sharedPreference.write(
//            SharedPrefKey.LPR_IMAGE_FOR_PRINT_X, ""
//        ).toString()
//        sharedPreference.write(
//            SharedPrefKey.LPR_IMAGE_FOR_PRINT_Y, ""
//        ).toString()
//        sharedPreference.write(
//            SharedPrefKey.LPR_IMAGE_FOR_PRINT, ""
//        ).toString()
//        sharedPreference.write(
//            SharedPrefKey.LPR_IMAGE_FOR_PRINT_WIDTH, ""
//        ).toString()

        sharedPreference.write(
            SharedPrefKey.MOTORIST_INFORMATION_LABEL,
            ""
        )

        //Bar code
        sharedPreference.write(
            SharedPrefKey.BAR_CODE_FOR_PRINT_X, ""
        )
        sharedPreference.write(
            SharedPrefKey.BAR_CODE_FOR_PRINT_Y, ""
        )
        sharedPreference.write(
            SharedPrefKey.BAR_CODE_FOR_PRINT, ""
        )
        sharedPreference.write(
            SharedPrefKey.BAR_CODE_FOR_PRINT_HEIGHT, ""
        )

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun String?.safeDoubleFromLayout(): Double {
    if (this.isNullOrBlank()) return 0.0
    val clean = this.trim().lowercase()
    if (clean == "null") return 0.0
    return if (clean.contains("#")) {
        clean.split("#").firstOrNull()?.toDoubleOrNull() ?: 0.0
    } else {
        clean.toDoubleOrNull() ?: 0.0
    }
}

fun getRandomID(): Int {
    return Random().nextInt(1000)
}

fun Context.getBoxStrokeColor(color: Int): ColorStateList {
    val focusedColor = ContextCompat.getColor(this, color)
    val defaultColor = ContextCompat.getColor(this, color)
    val disabledColor = ContextCompat.getColor(this, color)
    val errorColor = ContextCompat.getColor(this, color)

    // Define states: focused, disabled, default (unfocused)
    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled, android.R.attr.state_focused),
        intArrayOf(-android.R.attr.state_enabled), // disabled
        intArrayOf() // default
    )

    val colors = intArrayOf(
        focusedColor, disabledColor, defaultColor
    )

    return ColorStateList(states, colors)
}

fun String.getImageFileName(): String {
    return "${this}_${SDF_IMAGE_TIMESTAMP.format(Date())}_capture.jpg"
}

fun getFuzzyStringList(): List<String> {
    return listOf<String>(
        "0",
        "O",
        "Q",
        "I",
        "1",
        "5",
        "S",
        "Z",
        "2",
        "B",
        "8",
        "T",
        "H",
        "M",
        "K",
        "V",
        "Y",
        "F",
        "P",
        "R",
        "E",
        "3",
        "D",
        "A",
        "4",
        "6",
        "G"
    )
}

fun Double.getFormattedAmount():String{
    return String.format("%.2f", this)
}

//fun Double.getFormattedAmount(): String {
//    if (!this.isFinite()) return "0.00"
//    return java.math.BigDecimal.valueOf(this)
//        .setScale(2, java.math.RoundingMode.HALF_UP)
//        .toPlainString()
//}
