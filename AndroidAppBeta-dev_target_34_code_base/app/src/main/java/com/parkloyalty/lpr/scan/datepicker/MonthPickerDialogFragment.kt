package com.parkloyalty.lpr.scan.datepicker

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MonthPickerDialogFragment(
    private val onMonthSelected: (month: Int, monthName: String) -> Unit
) : DialogFragment() {

    private val months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listView = ListView(requireContext()).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, months)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnItemClickListener { _, _, position, _ ->
                onMonthSelected(position, months[position])
                dismiss()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Select Month")
            .setView(listView)
            .create()
    }
}