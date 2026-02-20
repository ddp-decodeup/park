package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.CameraRawFeedDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.DataItemCameraRaw
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.ResultsItemCameraRaw
import com.parkloyalty.lpr.scan.util.AppUtils

class CameraRawFeedDataAdapter : RecyclerView.Adapter<CameraRawFeedDataAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<ResultsItemCameraRaw>? = null
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<ResultsItemCameraRaw>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_lpr_camera_raw_feed_list, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            try {
                val inTime = mListData!![position].inCarImageTimestamp?.let {
                     AppUtils.formatDateTimeForCameraRaw(it.toString())
                } ?: ""

                val outTime = mListData!![position].outCarImageTimestamp?.let {
                     AppUtils.formatDateTimeForCameraRaw(it.toString())
                } ?: ""

                holder.mState.text = mListData?.get(position)?.state?.toString()?.let { " $it" } ?: ""
                holder.mDirection.text = mListData?.get(position)?.direction?.toString()?.let { " $it" } ?: ""

                holder.mMake.text = mListData?.get(position)?.make?.toString()?.let { " $it" } ?: ""
                holder.mColor.text = mListData?.get(position)?.color?.toString()?.let { " $it" } ?: ""

                holder.rlRowMain.setOnClickListener {
                    listItemSelectListener?.onItemClick(
                        holder.rlRowMain, false, position
                    )
                }
                holder.mVehicleInTimeValue.text = inTime
                holder.mVehicleOutTimeValue.text = outTime
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mState: AppCompatTextView
        var mMake: AppCompatTextView
        var mColor: AppCompatTextView
        var mDirection: AppCompatTextView
        var mVehicleInTimeValue: AppCompatTextView
        var mVehicleOutTimeValue: AppCompatTextView
        var rlRowMain: LinearLayoutCompat

        init {
            mState = itemView.findViewById(R.id.tvStateValue)
            mDirection = itemView.findViewById(R.id.tvDirectionValue)

            mMake = itemView.findViewById(R.id.tvMakeValue)
            mColor = itemView.findViewById(R.id.tvColorValue)

            mVehicleInTimeValue = itemView.findViewById(R.id.tv_vehicle_in_time_value)
            mVehicleOutTimeValue = itemView.findViewById(R.id.tv_vehicle_out_time_value)
            rlRowMain = itemView.findViewById(R.id.layRowCameraRaw)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }
}