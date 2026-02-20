package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

class NoFilterArrayAdapter<T>(
    context: Context,
    resource: Int,
    private val items: Array<T>
) : ArrayAdapter<T>(context, resource, items), Filterable {

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                results.values = items
                results.count = items.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return resultValue?.toString() ?: ""
            }
        }
    }
}