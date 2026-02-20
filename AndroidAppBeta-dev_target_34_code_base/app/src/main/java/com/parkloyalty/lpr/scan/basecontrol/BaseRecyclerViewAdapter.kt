package com.parkloyalty.lpr.scan.basecontrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.util.LoadingViewHolder

abstract class BaseRecyclerViewAdapter(private var adapterItemList: MutableList<*>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @LayoutRes
    abstract fun getRowLayout(): Int

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Constants.VIEW_ITEM_LOADING -> {
                val emptyView = LayoutInflater.from(parent.context).inflate(R.layout.layout_loading_progress_item, parent, false)
                LoadingViewHolder(emptyView)
            }
            else -> MyViewHolder(LayoutInflater.from(parent.context).inflate(getRowLayout(), parent, false))
        }
    }

    override fun getItemCount(): Int {
        return adapterItemList.size
    }

    override fun getItemViewType(position: Int) = if (adapterItemList[position] == null && adapterItemList.isNotEmpty()) Constants.VIEW_ITEM_LOADING else Constants.VIEW_ITEM_CONTAINER

    //fun getMyItemViewType(position: Int) = if (itemList[position] == null && itemList.isNotEmpty()) Constants.VIEW_ITEM_LOADING else Constants.VIEW_ITEM_CONTAINER

    open fun onMyViewHolderInit(viewHolder: MyViewHolder, itemView: View) = Unit

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            onMyViewHolderInit(this, itemView)
        }
    }
}