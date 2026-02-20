package com.parkloyalty.lpr.scan.ui.guide_enforcement

import CameraGuidedItem
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.util.AppUtils


class CameraGuidedEnforcementAdapter(
    private val mContext: Context,
    private var mListData: MutableList<CameraGuidedItem> = mutableListOf(),
    private val listItemSelectListener: ListItemSelectListener? = null
) : RecyclerView.Adapter<CameraGuidedEnforcementAdapter.ViewHolder>() {

    // 🔁 Use this function to update the adapter any time data changes
    fun setData(newList: List<CameraGuidedItem>?) {
        mListData.clear()
        if (!newList.isNullOrEmpty()) {
            mListData.addAll(newList.filterNotNull())
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.content_camera_feed, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mListData.getOrNull(position) ?: return

        try {
            holder.mPlate.text = " ${item.vehicle?.plate ?: ""}"
            holder.mTicketState.text = " ${item.vehicle?.state ?: ""}"
            holder.mMake.text = " ${item.vehicle?.make ?: ""}"
            holder.mColor.text = " ${item.vehicle?.color ?: ""}"
            holder.mOccurredAt.text = AppUtils.formatDateTimeForCameraViolation(item.receivedTimestamp ?: "")

            // Hide or show violation section
            if (item.violationId.isNullOrEmpty() || item.spaceNumber.isNullOrEmpty()) {
                holder.spaceViolationSection.visibility = View.GONE
            } else {
                holder.spaceViolationSection.visibility = View.VISIBLE
                holder.mViolationNumber.text = " ${item.violationId}"
                holder.mSpace.text = " ${item.spaceNumber}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.mLin.setOnClickListener {
            listItemSelectListener?.onItemClick(position, item)
        }
    }

    override fun getItemCount(): Int = mListData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mPlate: AppCompatTextView = itemView.findViewById(R.id.tvPlate)
        val mMake: AppCompatTextView = itemView.findViewById(R.id.tvMake)
        val mColor: AppCompatTextView = itemView.findViewById(R.id.tv_color)
        val mTicketState: AppCompatTextView = itemView.findViewById(R.id.tvTicketState)
        val mStatus: AppCompatTextView = itemView.findViewById(R.id.tvstatus)
        val mOccurredAt: AppCompatTextView = itemView.findViewById(R.id.tv_occurred_at)
        val mViolationNumber: AppCompatTextView = itemView.findViewById(R.id.tv_violation)
        val mSpace: AppCompatTextView = itemView.findViewById(R.id.tvSpace)
        val mLin: LinearLayoutCompat = itemView.findViewById(R.id.layRowCitation)
        val spaceViolationSection: LinearLayoutCompat = itemView.findViewById(R.id.spaceViolationSection)
        val rlOccupany: RelativeLayout = itemView.findViewById(R.id.rl_occupany)
    }

    interface ListItemSelectListener {
        fun onItemClick(position: Int, responseItem: CameraGuidedItem?)
    }
}
