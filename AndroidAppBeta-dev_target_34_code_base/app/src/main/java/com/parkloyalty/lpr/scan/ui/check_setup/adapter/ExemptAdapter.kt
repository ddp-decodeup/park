package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ExemptDataResponse

class ExemptAdapter : RecyclerView.Adapter<ExemptAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<ExemptDataResponse>? = null
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<ExemptDataResponse>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_lpr_exempt_list, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            holder.mType.text = mListData!![position].state
            holder.mReason.text = mListData!![position].reason
            holder.mPlace.text = mListData!![position].exemptCode
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
        var mPlace: AppCompatTextView

        init {
            mPlace = itemView.findViewById(R.id.tvPlace)
            mReason = itemView.findViewById(R.id.tvReasonValue)
            mType = itemView.findViewById(R.id.tvTypeValue)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }
}