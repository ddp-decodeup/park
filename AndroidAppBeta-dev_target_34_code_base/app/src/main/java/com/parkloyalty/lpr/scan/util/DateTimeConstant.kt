package com.parkloyalty.lpr.scan.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

val SDF_MM_DD_YYYY = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
val SDF_FULL_DATE_UTC = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH)
val SDF_IMAGE_TIMESTAMP = SimpleDateFormat("yyyMMdd_HHmmss", Locale.ENGLISH)
val SDF_IMAGE_ID_TIMESTAMP = SimpleDateFormat("HHmmss", Locale.ENGLISH)
val SDF_ddHHmmss = SimpleDateFormat("ddHHmmss", Locale.ENGLISH)
val SDF_HHmm = SimpleDateFormat("HH:mm", Locale.ENGLISH)
val SDF_hhmm_a = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
val SDF_MMM = SimpleDateFormat("MMM", Locale.ENGLISH)

val SDF_EEEE = SimpleDateFormat("EEEE", Locale.ENGLISH)
val SDF_MM_dd_YYYY_HH_mm_ss = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH)

const val DAY_TUESDAY = "Tuesday"
const val DAY_THURSDAY = "Thursday"

const val HEARING_FIXED_TIME = " 9AM-2:30PM";

const val HEARING_DATE_DAY_DIFFERENCE_THRESHOLD = 25

const val YEARS_IN_PAST = 15
const val YEARS_IN_FUTURE = 15

//const val MOTORIST_DATE_OF_BIRTH_FORMAT = "dd/MM/yyyy"
const val MOTORIST_DATE_OF_BIRTH_FORMAT = "MM/dd/yyyy"

/**
 * Checks if the given date string matches the specified format.
 * Supported tokens: dd, MM, yyyy, yy
 * Supported separators: /, -, .
 *
 * @param dateStr The date string to validate.
 * @param format The expected date format (e.g., "MM/dd/yyyy").
 * @return True if the date string matches the format, false otherwise.
 */
fun matchesFormat(dateStr: String, format: String): Boolean {
    // Map supported tokens -> regex
    val tokenRegex = mapOf(
        "dd" to "(0[1-9]|[12][0-9]|3[01])",
        "MM" to "(0[1-9]|1[0-2])",
        "yyyy" to "\\d{4}",
        "yy" to "\\d{2}"
    )

    // Escape separators and replace tokens with regex
    var regexPattern = Regex.escape(format)
    tokenRegex.forEach { (token, regex) ->
        regexPattern = regexPattern.replace(token, regex)
    }

    val regex = Regex("^$regexPattern$")
    return regex.matches(dateStr)
}

/**
 * Validates if the given date string matches the specified format using DateTimeFormatter.
 *
 * @param dateString The date string to validate.
 * @param format The expected date format (e.g., "MM/dd/yyyy").
 * @return True if the date string matches the format, false otherwise.
 */
fun isDateMatchingFormat(dateString: String, format: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern(format)
        LocalDate.parse(dateString, formatter)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}
