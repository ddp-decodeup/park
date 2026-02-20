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
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import java.text.DecimalFormat

class TireStemAdapter : RecyclerView.Adapter<TireStemAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<DatasetResponse>? = null
    private var mContext: Context
    val formatter = DecimalFormat("00")

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
            context: Context,
            listData: List<DatasetResponse>?,
            itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_tire_stem_layout, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
//            holder.mType.text = mListData!![position].type
            holder.mValue.text = formatter.format(mListData!![position].tierStem?.toInt())
//            holder.mPlace.text = mListData!![position].lpNumber
//            holder.tvAlerttypeValue.text = mListData!![position].alertType
            holder.rlRowMain.setOnClickListener {
                listItemSelectListener?.onItemClick(mListData!![position],position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var mValue: AppCompatTextView
        var rlRowMain: LinearLayoutCompat

        init {
            mValue = itemView.findViewById(R.id.tvValue)
            rlRowMain = itemView.findViewById(R.id.rlRowMain)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(dataObject: DatasetResponse?, position: Int)
    }
}