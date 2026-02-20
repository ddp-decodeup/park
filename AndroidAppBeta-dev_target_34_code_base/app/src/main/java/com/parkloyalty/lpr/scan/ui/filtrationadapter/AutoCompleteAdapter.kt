package com.parkloyalty.lpr.scan.ui.filtrationadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse

class AutoCompleteAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    DatasetResponseMeterList: List<DatasetResponse>,
    itemClickListener: ListItemSelectListener?
) :
    ArrayAdapter<DatasetResponse>(mContext, mLayoutResourceId, DatasetResponseMeterList),
    Filterable {
    private var mMeterList: List<DatasetResponse> = DatasetResponseMeterList
    private val filteredItems: MutableList<DatasetResponse> = ArrayList()
    var listItemSelectListener: ListItemSelectListener? = null
    init {
        filteredItems.addAll(DatasetResponseMeterList)
        listItemSelectListener = itemClickListener
    }

    override fun getCount(): Int = mMeterList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context).inflate(mLayoutResourceId, parent, false)
        val holder = ViewHolder(view)
        if (mMeterList!!.size > 0 && mMeterList[position] != null) {
            try {
                val item = mMeterList[position]!!.name.nullSafety("")
                holder.bind(item.nullSafety(""))

                holder.llMainLayout!!.setOnClickListener {
                    listItemSelectListener?.onItemClick(
                        position,mMeterList!![position]
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    private class ViewHolder(view: View) {
        private val textView: AppCompatTextView? = view.findViewById(R.id.text_view)
        val llMainLayout: LinearLayoutCompat? = view.findViewById(R.id.llMain)

        fun bind(item: String?) {
            textView!!.setText(item)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: Filter.FilterResults
            ) {
                try {
                    mMeterList = filterResults.values as List<DatasetResponse>
                    notifyDataSetChanged()
                } catch (e: Exception) {
                   e.printStackTrace()
                }
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                try {
                    filterResults.values = if (queryString == null || queryString.isEmpty())
                        filteredItems
                    else {
                          filteredItems.filter {
                                    it.name!!.nullSafety("").toLowerCase().contains(queryString) ||
                                    (it.name!!.nullSafety("").toLowerCase().startsWith(queryString,2))||
                                    (false &&it.name!!.nullSafety("").toLowerCase().startsWith(queryString,4))
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return filterResults
            }

        }
    }
    interface ListItemSelectListener {
        fun onItemClick(position: Int, meterObject : DatasetResponse)
    }
}



