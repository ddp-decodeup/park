package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.PermitDataResponse
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.setCustomContentDescription
import com.parkloyalty.lpr.scan.util.AppUtils.compareDates
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPR
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPRPermit
import com.parkloyalty.lpr.scan.util.AppUtils.splitID
import java.text.ParseException

class PermitAdapter : RecyclerView.Adapter<PermitAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<PermitDataResponse>? = null
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<PermitDataResponse>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_lpr_permit_list, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            holder.mStart.text = splitDateLPRPermit(mListData!![position].startTimestamp!!)
            holder.mEnd.text = splitDateLPRPermit(mListData!![position].endTimestamp!!)
            try {
                if (compareDates(mListData!![position].endTimestamp!!) == 0) {
                    holder.mStatusValue.setText(" "+mContext.getString(R.string.scr_lbl_active))
                } else {
                    holder.mStatusValue.setText(" "+mContext.getString(R.string.scr_lbl_inactive))
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
//            holder.mZone.text = " "+splitID(mListData!![position].zoneId.toString()) 17-jan-23 by Sri
            holder.mZone.text = (mListData!![position].zoneId?.takeIf { it != "null" } ?: "")
            if(!mListData?.get(position)?.permitType.isNullOrEmpty() && mListData?.get(position)?.permitType != "null") {
                holder.mPermitTypeValue.text =
                     mListData!![position].permitType?.takeIf { it != "null" } ?: ""
            }else{
                holder.rlPermitType.visibility= View.GONE
            }
            holder.mIdValue.text = " "+ splitID(mListData!![position].state?.takeIf { it != "null" } ?: "")
            if (!mListData?.get(position)?.banner.isNullOrEmpty() && mListData?.get(position)?.banner != "null") {
                holder.mPermitIdValue.text =
                     mListData?.get(position)?.banner?.takeIf { it != "null" } ?: ""
                holder.llBannerNote.visibility = View.VISIBLE
            }else{
                holder.llBannerNote.visibility = View.GONE
            }
            if (!mListData?.get(position)?.rateName.isNullOrEmpty() && mListData?.get(position)?.rateName != "null") {
                holder.mRateNameValue.text =
                    mListData?.get(position)?.rateName?.takeIf { it != "null" } ?: ""
            }else {
                holder.llRateName.visibility = View.GONE
            }
            holder.mPermitNumberValue.text = mListData?.get(position)?.permitNumber?.takeIf { it != "null" } ?: ""

            //Set ADA
            holder.mStart.setCustomContentDescription()
            holder.mEnd.setCustomContentDescription()
            holder.mStatusValue.setCustomContentDescription()
            holder.mZone.setCustomContentDescription()
            holder.mPermitTypeValue.setCustomContentDescription()
            holder.mIdValue.setCustomContentDescription()
            holder.mPermitIdValue.setCustomContentDescription()
            holder.mRateNameValue.setCustomContentDescription()
            holder.mPermitNumberValue.setCustomContentDescription()
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
        var mStart: AppCompatTextView
        var mEnd: AppCompatTextView
        var mStatusValue: AppCompatTextView
        var mZone: AppCompatTextView
        var mPermitTypeValue: AppCompatTextView
        var mIdValue: AppCompatTextView
        var mPermitIdValue: AppCompatTextView
        var mRateNameValue: AppCompatTextView
        var mPermitNumberValue: AppCompatTextView
        var llPermitNumber: LinearLayoutCompat
        var llRateName: LinearLayoutCompat
        var llBannerNote: LinearLayoutCompat
        var rlPermitType: RelativeLayout

        init {
            mStart = itemView.findViewById(R.id.tvStart)
            mEnd = itemView.findViewById(R.id.tvEnd)
            mStatusValue = itemView.findViewById(R.id.tvStatusValue)
            mZone = itemView.findViewById(R.id.tvZone)
            mPermitTypeValue = itemView.findViewById(R.id.tvPermitTypeValue)
            mIdValue = itemView.findViewById(R.id.tvIdValue)
            mPermitIdValue = itemView.findViewById(R.id.tvpermit_id_value)
            mRateNameValue = itemView.findViewById(R.id.tvRateNameValue)
            mPermitNumberValue = itemView.findViewById(R.id.tvPermitNumberValue)

            llPermitNumber = itemView.findViewById(R.id.llPermitNumber)
            llRateName = itemView.findViewById(R.id.llRateName)
            llBannerNote = itemView.findViewById(R.id.llBannerNote)
            rlPermitType = itemView.findViewById(R.id.rlPermitType)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }
}