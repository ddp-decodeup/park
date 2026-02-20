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
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.ResponseItem
import com.parkloyalty.lpr.scan.util.AppUtils.dateFormateForSpace

class PayBySpaceAdapter : RecyclerView.Adapter<PayBySpaceAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<ResponseItem>? = null
    private var mContext: Context
    private val mDate: String? = null

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<ResponseItem>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.content_paybyspace_row, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (mListData != null) { //2021-08-20T06:27:56Z
            holder.mSpaceId.text = mListData!![position].spaceId
            holder.mPayment.text =
                (if (!mListData!![position].paymentStartTimestamp!!.isEmpty()) dateFormateForSpace(
                    mListData!![position].paymentStartTimestamp
                ) else "") + " - " + if (!mListData!![position].paymentExpiryTimestamp!!.isEmpty()) dateFormateForSpace(
                    mListData!![position].paymentExpiryTimestamp
                ) else ""
            holder.mOccupancy.text =
                if (!mListData!![position].occupancyStartTimestamp!!.isEmpty()) dateFormateForSpace(
                    mListData!![position].occupancyStartTimestamp
                ) else ""
            if (holder.mOccupancy.text.toString().isEmpty()) {
                holder.rlOccupany.visibility = View.GONE
            } else {
                holder.rlOccupany.visibility = View.VISIBLE
            }
            holder.mTicketStatus.text = mListData!![position].status
            if (mListData!![position].status.equals("Violation", ignoreCase = true)) {
                holder.mLin.background =
                    mContext.getDrawable(R.drawable.button_round_corner_shape_light_red)
            } else if (mListData!![position].status.equals("Paid", ignoreCase = true)) {
                holder.mLin.background =
                    mContext.getDrawable(R.drawable.button_round_corner_shape_light_green)
            } else {
                holder.mLin.background =
                    mContext.getDrawable(R.drawable.round_corner_shape_without_fill_gray)
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
        var mSpaceId: AppCompatTextView
        var mPayment: AppCompatTextView
        var mOccupancy: AppCompatTextView
        var mTicketStatus: AppCompatTextView
        var mLin: LinearLayoutCompat
        var rlOccupany: RelativeLayout

        init {
            mLin = itemView.findViewById(R.id.layRowCitation)
            mSpaceId = itemView.findViewById(R.id.tvspace)
            mPayment = itemView.findViewById(R.id.tvpayment)
            mOccupancy = itemView.findViewById(R.id.tvdatepay)
            mTicketStatus = itemView.findViewById(R.id.tvTicketStatus)
            rlOccupany = itemView.findViewById(R.id.rl_occupany)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(position: Int, responseItem: ResponseItem?)
    }
}