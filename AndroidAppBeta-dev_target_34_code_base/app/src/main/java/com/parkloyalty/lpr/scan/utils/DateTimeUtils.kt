package com.parkloyalty.lpr.scan.utils

import android.app.DatePickerDialog
import android.content.Context
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.SDF_MM_DD_YYYY
import java.text.SimpleDateFormat
import java.util.Calendar

object DateTimeUtils {

    /**
     * Optimized, reusable date picker function.
     * @param context Activity or Fragment context
     * @param initialDate Calendar instance for pre-selected date (default: now)
     * @param dateFormat Output date format (default: SDF_MM_DD_YYYY)
     * @param onDateSelected Callback with formatted date string
     * @param minDate Optional minimum selectable date (in millis)
     * @param maxDate Optional maximum selectable date (in millis)
     */
    fun openDataPicker(
        context: Context,
        initialDate: Calendar = Calendar.getInstance(),
        dateFormat: SimpleDateFormat = SDF_MM_DD_YYYY,
        minDate: Long? = null,
        maxDate: Long? = null,
        onDateSelected: (String) -> Unit
    ) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val myCalendar = Calendar.getInstance()
            myCalendar.set(year, monthOfYear, dayOfMonth)
            onDateSelected(dateFormat.format(myCalendar.time))
        }
        val dialog = DatePickerDialog(
            context,
            dateSetListener,
            initialDate[Calendar.YEAR],
            initialDate[Calendar.MONTH],
            initialDate[Calendar.DAY_OF_MONTH]
        )
        minDate?.let { dialog.datePicker.minDate = it }
        maxDate?.let { dialog.datePicker.maxDate = it }
        dialog.show()
    }

    fun getClientTimestamp(checkSettingsFile: Boolean? = false): String {
        var zone: String? = "CST"
        if (Singleton.getDataSetList(DATASET_SETTINGS_LIST) != null && checkSettingsFile.nullSafety()) {
            zone = Singleton.getDataSetList(DATASET_SETTINGS_LIST)?.firstOrNull()?.mValue
                .nullSafety()
        }

        return splitDateLpr(zone)
    }
}