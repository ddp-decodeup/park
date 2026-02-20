package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ScofflawDataResponse
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.setCustomAbbreviatedContentDescription
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.setCustomContentDescription

class ScofflawAdapter : RecyclerView.Adapter<ScofflawAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<ScofflawDataResponse>? = null
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(context: Context,
        listData: List<ScofflawDataResponse>?,
        itemClickListener: ListItemSelectListener?) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_lpr_scofflaw_list, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            val scofflawData = mListData!![position]
            val fineAmount = mListData!![position].fineAmount.nullSafety(0.00)
            holder.mType.text = " "+scofflawData.type.nullSafety(" ")
            holder.mReason.text = " "+scofflawData.reason.nullSafety(" ")
            holder.mPlace.text = " "+scofflawData.lpNumber.nullSafety(" ")
            holder.tvAlerttypeValue.text = " "+scofflawData.alertType.nullSafety(" ")
            holder.mState.text = " "+scofflawData.state.nullSafety(" ")
            holder.mCitationCount.text = " "+scofflawData.citationCount.nullSafety(" ")
            holder.mAmountDue.text = ": $"+(String.format("%.2f", fineAmount))

            //            holder.mFineAmount.text = ": $"+(String.format("%.2f", fineAmount))

            //Set ADA
            holder.mType.setCustomContentDescription()
            holder.mReason.setCustomContentDescription()
            holder.mPlace.setCustomContentDescription()
            holder.tvAlerttypeValue.setCustomContentDescription()
            holder.mState.setCustomContentDescription()
            holder.mCitationCount.setCustomContentDescription()
            holder.mAmountDue.setCustomContentDescription()
            //holder.mFineAmount.setCustomContentDescription()
        }

        /*holder.rlRowMain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                listItemSelectListener.onItemClick(holder.rlRowMain,hasFocus,position);
            }
        });*/
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mType: AppCompatTextView
        var mReason: AppCompatTextView
        var mState: AppCompatTextView
        var mPlace: AppCompatTextView
        var mCitationCount: AppCompatTextView
        var mAmountDue: AppCompatTextView
        var mFineAmount: AppCompatTextView
        var tvAlerttypeValue: AppCompatTextView

        init {
            mPlace = itemView.findViewById(R.id.tvPlace)
            mReason = itemView.findViewById(R.id.tvReasonValue)
            mState = itemView.findViewById(R.id.tvStateVale)
            mType = itemView.findViewById(R.id.tvTypeValue)
            tvAlerttypeValue = itemView.findViewById(R.id.tv_alerttype_Value)
            mCitationCount = itemView.findViewById(R.id.tvCitationCountValue)
            mAmountDue = itemView.findViewById(R.id.tvAmountDueValue)
            mFineAmount = itemView.findViewById(R.id.tvFineAmountValue)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }
}