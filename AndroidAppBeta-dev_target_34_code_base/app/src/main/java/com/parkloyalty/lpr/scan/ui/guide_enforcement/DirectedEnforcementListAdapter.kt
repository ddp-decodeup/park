package com.parkloyalty.lpr.scan.ui.guide_enforcement

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.GeneticHitListData
import com.parkloyalty.lpr.scan.util.LoadingViewHolder

class DirectedEnforcementListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<GeneticHitListData>? = null
    private var mContext: Context


    constructor(
        context: Context,
        listData: List<GeneticHitListData>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Constants.VIEW_ITEM_LOADING -> {
                LoadingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_loading_progress_item, parent, false)
                )
            }

            else -> ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_directed_enforcement, parent, false)
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (mListData != null && holder is ViewHolder) {
            val listObj = mListData?.get(position)

            holder.tvLprNumber.text = listObj?.lpNumber
            holder.tvIsHit.text = listObj?.isHit
            holder.tvState.text = listObj?.lpState
            holder.tvPatroller.text = listObj?.patrollerName
            holder.tvTypeOfHits.text = listObj?.typeOfHit
            holder.tvAddress.text = listObj?.address


            holder.cvContainer.setOnClickListener {
                listItemSelectListener?.onItemClick(position, listObj)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<GeneticHitListData>?) {
        mListData = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    //Keep these two below methods when you are working with checkbox in recycler view adapter
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int) =
        if (mListData?.get(position) == null && !mListData.isNullOrEmpty()) Constants.VIEW_ITEM_LOADING else Constants.VIEW_ITEM_CONTAINER


    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cvContainer: CardView = itemView.findViewById(R.id.cvContainer)
        var tvLprNumber: AppCompatTextView = itemView.findViewById(R.id.tvLprNumber)
        var tvIsHit: AppCompatTextView = itemView.findViewById(R.id.tvIsHit)
        var tvState: AppCompatTextView = itemView.findViewById(R.id.tvState)
        var tvPatroller: AppCompatTextView = itemView.findViewById(R.id.tvPatroller)
        var tvTypeOfHits: AppCompatTextView = itemView.findViewById(R.id.tvTypeOfHits)
        var tvAddress: AppCompatTextView = itemView.findViewById(R.id.tvAddress)
    }

    interface ListItemSelectListener {
        fun onItemClick(position: Int, geneticHitListData: GeneticHitListData?)
    }
}