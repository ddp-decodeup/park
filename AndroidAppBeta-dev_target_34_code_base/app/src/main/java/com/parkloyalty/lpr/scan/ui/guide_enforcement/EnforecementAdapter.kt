package com.parkloyalty.lpr.scan.ui.guide_enforcement

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.DataItemItem
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPR
import java.util.*
import kotlin.collections.ArrayList

class EnforecementAdapter(
    listData: MutableList<DataItemItem>?,
    itemClickListener: ListItemSelectListener?
) : RecyclerView.Adapter<EnforecementAdapter.ViewHolder>() {
    var listItemSelectListener: ListItemSelectListener? = itemClickListener
    private var mListData: MutableList<DataItemItem>? = listData
    private var mListDataFinal: MutableList<DataItemItem>? = listData


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_items_guide_enforcement, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            holder.appCompatTextViewLrpNumber.text = (mListData!![position].lpNumber
                    + ", " + mListData!![position].lpState + " " + mListData!![position].spaceId)
            holder.appCompatTextViewZone.text = mListData!![position].zone
            holder.appCompatTextViewStartDate.text =
                splitDateLPR(mListData!![position].startTimestamp!!)
            holder.appCompatTextViewEndDate.text =
                splitDateLPR(mListData!![position].expiryTimestamp!!)
        }
        holder.rlRowMain.setOnClickListener { //                listItemSelectListener.onItemClick(holder.rlRowMain,hasFocus,position);
            val `is` = holder.linearLayoutCompatExpandView.visibility
            if (`is` == 0) {
                holder.linearLayoutCompatExpandView.visibility = View.GONE
                holder.appCompatImageViewArrow.rotation = 180f
            } else {
                holder.linearLayoutCompatExpandView.visibility = View.VISIBLE
                holder.appCompatImageViewArrow.rotation = 360f
            }
        }
        holder.appCompatImageViewArrow.setOnClickListener {
            val `is` = holder.linearLayoutCompatExpandView.visibility
            if (`is` == 0) {
                holder.linearLayoutCompatExpandView.visibility = View.GONE
                holder.appCompatImageViewArrow.rotation = 180f
            } else {
                holder.linearLayoutCompatExpandView.visibility = View.VISIBLE
                holder.appCompatImageViewArrow.rotation = 360f
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var appCompatTextViewLrpNumber: AppCompatTextView
        var appCompatTextViewZone: AppCompatTextView
        var appCompatTextViewStartDate: AppCompatTextView
        var appCompatTextViewEndDate: AppCompatTextView
        var appCompatImageViewArrow: AppCompatImageView
        var linearLayoutCompatExpandView: LinearLayoutCompat
        var rlRowMain: LinearLayoutCompat

        init {
            appCompatTextViewLrpNumber = itemView.findViewById(R.id.tv_lrp_number)
            appCompatTextViewZone = itemView.findViewById(R.id.tv_zone_Value)
            appCompatTextViewStartDate = itemView.findViewById(R.id.tv_start_date_Value)
            appCompatTextViewEndDate = itemView.findViewById(R.id.tv_end_date_Value)
            appCompatImageViewArrow = itemView.findViewById(R.id.imv_arrow)
            linearLayoutCompatExpandView = itemView.findViewById(R.id.ll_expand_view)
            rlRowMain = itemView.findViewById(R.id.layRowEnforecement)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }

    fun filterOperation(charString: String): List<DataItemItem>? {
        var contactListFiltered: MutableList<DataItemItem>? = ArrayList()
        contactListFiltered = if (charString.isEmpty()) {
            mListDataFinal
        } else {
            val filteredList: MutableList<DataItemItem> = ArrayList()
            for (row in mListDataFinal!!) {

                // name match condition. this might differ depending on your requirement
                // here we are looking for name or phone number match
                if (row.lpNumber!!.lowercase(Locale.getDefault())
                        .contains(charString.lowercase(Locale.getDefault())) || row.lpNumber!!.contains(charString)
                ) {
                    filteredList.add(row)
                }
            }
            filteredList
        }
        mListData!!.clear()
        mListData = contactListFiltered
        return mListData
    }
}