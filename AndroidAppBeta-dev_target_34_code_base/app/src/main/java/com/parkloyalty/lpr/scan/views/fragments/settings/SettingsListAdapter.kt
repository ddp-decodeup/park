package com.parkloyalty.lpr.scan.views.fragments.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse

class SettingsListAdapter() : ListAdapter<DatasetResponse, SettingsListAdapter.ViewHolder>(DIFF) {

    interface OnItemClickListener {
        fun onItemClick(item: DatasetResponse, position: Int)
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DatasetResponse>() {
            override fun areItemsTheSame(
                oldItem: DatasetResponse,
                newItem: DatasetResponse
            ): Boolean {
                // Prefer stable id if available
                if (!oldItem.m_id.isNullOrEmpty() || !newItem.m_id.isNullOrEmpty()) {
                    return oldItem.m_id == newItem.m_id
                }
                // Fallback to comparing name+value
                return (oldItem.name == newItem.name) && (oldItem.mValue == newItem.mValue)
            }

            override fun areContentsTheSame(
                oldItem: DatasetResponse,
                newItem: DatasetResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.content_setting_dynamic_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun updateList(newList: List<DatasetResponse>) {
        submitList(newList)
    }

    fun clear() {
        submitList(emptyList())
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTextViewTimeZoneLabel =
            itemView.findViewById<View>(R.id.tvtime_zone) as AppCompatTextView
        val mTextViewTimeZoneLabelValue =
            itemView.findViewById<View>(R.id.mTextViewTimeZone) as AppCompatTextView

        fun bind(item: DatasetResponse) {
            mTextViewTimeZoneLabel.text = item.type
            mTextViewTimeZoneLabelValue.text = item.mValue
        }
    }
}
