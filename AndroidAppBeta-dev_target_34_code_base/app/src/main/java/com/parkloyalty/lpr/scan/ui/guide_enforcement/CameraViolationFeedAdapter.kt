package com.parkloyalty.lpr.scan.ui.guide_enforcement

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
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.DataItemCameraViolation
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.DataItemCameraViolationFeed
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.dateFormateForSpace

class CameraViolationFeedAdapter : RecyclerView.Adapter<CameraViolationFeedAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<DataItemCameraViolationFeed>? = null
    private var mContext: Context
    private val mDate: String? = null

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<DataItemCameraViolationFeed>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.content_camera_guided_enforcement_row, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (mListData != null) { //2021-08-20T06:27:56Z
            try {
                holder.mPlate.text = mListData?.get(position)?.lpNumber?.toString()?.let { " $it" } ?: ""
                holder.mTicketState.text = mListData?.get(position)?.state?.toString()?.let { " $it" } ?: ""

                holder.mMake.text = mListData?.get(position)?.make?.toString()?.let { " $it" } ?: ""
                holder.mColor.text = mListData?.get(position)?.color?.toString()?.let { " $it" } ?: ""

                holder.mOccurredAt.text = AppUtils.formatDateTimeForCameraViolation(mListData?.get(position)?.inCarImageTimestamp?.toString()?.let { " $it" } ?: "")

                if(mListData?.get(position)?.violationID?.isNullOrEmpty()==true||mListData?.get(position)?.spaceNumber?.isNullOrEmpty()==true) {
                    holder.spaceViolationSection?.visibility = View.GONE
                    }
                else{
                    holder.spaceViolationSection?.visibility= View.VISIBLE
                    holder.mViolationNumber.text = mListData?.get(position)?.violationID?.toString()?.let { " $it" } ?: ""
                    holder.mSpace.text = mListData?.get(position)?.spaceNumber?.toString()?.let { " $it" } ?: ""

                }
            } catch (e: Exception) {
               e.printStackTrace()
            }

        }
        holder.mLin.setOnClickListener {
            if (mListData != null) {
                listItemSelectListener!!.onItemClick(position, mListData!![position])
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mPlate: AppCompatTextView
        var mMake: AppCompatTextView
        var mColor: AppCompatTextView
        var mTicketState: AppCompatTextView
        var mStatus: AppCompatTextView
        var mOccurredAt: AppCompatTextView
        var mViolationNumber: AppCompatTextView
        var mSpace: AppCompatTextView
        var mLin: LinearLayoutCompat
        var spaceViolationSection: LinearLayoutCompat
        var rlOccupany: RelativeLayout

        init {
            mLin = itemView.findViewById(R.id.layRowCitation)
            mPlate = itemView.findViewById(R.id.tvPlate)
            mMake = itemView.findViewById(R.id.tvMake)
            mColor = itemView.findViewById(R.id.tv_color)
            mTicketState = itemView.findViewById(R.id.tvTicketState)
            mStatus = itemView.findViewById(R.id.tvstatus)
            mOccurredAt = itemView.findViewById(R.id.tv_occurred_at)
            rlOccupany = itemView.findViewById(R.id.rl_occupany)
            mViolationNumber = itemView.findViewById(R.id.tv_violation)
            mSpace = itemView.findViewById(R.id.tvSpace)
            spaceViolationSection = itemView.findViewById(R.id.spaceViolationSection)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(position: Int, responseItem: DataItemCameraViolationFeed?)
    }
}