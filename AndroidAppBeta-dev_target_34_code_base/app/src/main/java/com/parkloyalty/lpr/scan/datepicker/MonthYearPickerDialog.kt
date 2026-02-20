package com.parkloyalty.lpr.scan.datepicker

import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.util.YEARS_IN_FUTURE
import com.parkloyalty.lpr.scan.util.YEARS_IN_PAST
import java.util.Calendar

class MonthYearPickerDialog: DialogFragment() {
    private var listener: OnDateSetListener? = null
    fun setListener(listener: OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        // Get the layout inflater
        val inflater = requireActivity().layoutInflater
        val cal = Calendar.getInstance()
        val dialog: View = inflater.inflate(R.layout.date_picker_dialog, null)
        val monthPicker = dialog.findViewById<View>(R.id.picker_month) as NumberPicker
        val yearPicker = dialog.findViewById<View>(R.id.picker_year) as NumberPicker

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)){
            monthPicker.visibility = View.GONE
        }
        monthPicker.minValue = 0
        monthPicker.maxValue = 12
        monthPicker.value = cal[Calendar.MONTH]

        val year = cal[Calendar.YEAR]
        yearPicker.minValue = year - YEARS_IN_PAST
        yearPicker.maxValue = year + YEARS_IN_FUTURE
        yearPicker.value = year
        builder.setView(dialog) // Add action buttons
            .setPositiveButton(R.string.alt_lbl_OK, DialogInterface.OnClickListener { dialog, id -> listener!!.onDateSet(null, yearPicker.value, monthPicker.value, 0) })
            .setNegativeButton(R.string.scr_btn_cancel, DialogInterface.OnClickListener { dialog, id -> this@MonthYearPickerDialog.dialog!!.cancel() })
        return builder.create()
    }
}