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
import com.parkloyalty.lpr.scan.ui.check_setup.model.PaymentDataResponse
import com.parkloyalty.lpr.scan.util.AppUtils.compareDates
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPR
import java.text.ParseException

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<PaymentDataResponse>? = null
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<PaymentDataResponse>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_lpr_payment_list, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            holder.mStarting.text = " "+splitDateLPR(mListData!![position].startTimestamp.nullSafety())
            holder.mEnding.text = " "+splitDateLPR(mListData!![position].mExpiryTimeStamp.nullSafety())
            try {
                if (mListData!![position].mExpiryTimeStamp!=null && compareDates(mListData!![position].mExpiryTimeStamp.nullSafety()) == 0) {
                    holder.mStatusValue.setText(" " + mContext.getString(R.string.scr_lbl_active))
                } else {
                    holder.mStatusValue.setText(" "+mContext.getString(R.string.scr_lbl_inactive))
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

//            holder.mZone.setText(splitID(String.valueOf(mListData.get(position).getmZoneName())));
            holder.mZone.text = mListData!![position]?.mZoneName?.takeIf { it != "null" } ?: ""
            holder.mStartTime.text =   " "+splitDateLPR(mListData!![position].startTimestamp.toString())
            holder.mEndTime.text =   " "+splitDateLPR(mListData!![position].mExpiryTimeStamp.toString())
            holder.mMeterInfo.text =   (mListData!![position]?.meterId?.takeIf { it != "null" } ?: "")
//            holder.mExpiredTime.text =   splitDateLPR(mListData!![position].mExpiryTimeStamp.toString())
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
        var mStarting: AppCompatTextView
        var mEnding: AppCompatTextView
        var mStatusValue: AppCompatTextView
        var mZone: AppCompatTextView
        var mStartTime: AppCompatTextView
        var mEndTime: AppCompatTextView
        var mExpiredTime: AppCompatTextView
        var mMeterInfo: AppCompatTextView

        init {
            mStarting = itemView.findViewById(R.id.tvStarting)
            mEnding = itemView.findViewById(R.id.tvEnding)
            mStatusValue = itemView.findViewById(R.id.tvStatusValue)
            mZone = itemView.findViewById(R.id.tvZoneNew)
            mEndTime = itemView.findViewById(R.id.tvEndTimeValue)
            mStartTime = itemView.findViewById(R.id.tvStartTimeValue)
            mExpiredTime = itemView.findViewById(R.id.tvExpireValue)
            mMeterInfo = itemView.findViewById(R.id.tvMeterValue)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }
}