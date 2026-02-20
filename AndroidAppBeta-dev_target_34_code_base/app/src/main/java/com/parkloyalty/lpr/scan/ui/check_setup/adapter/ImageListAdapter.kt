package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.ui.continuousmode.model.ResultDataObject
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import java.io.File
import java.io.IOException
import java.util.*

class ImageListAdapter: RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<CitationImagesModel>? = null
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
            context: Context,
            listData: List<CitationImagesModel>?,
            itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_image_adapter, parent, false)
        return ViewHolder(listItem)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {

            if (mListData!![position]?.status == 1) {
                Glide.with(mContext)
                        .load(mListData!![position]?.citationImage)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        //                .placeholder(R.drawable.placeholder)
                        //                .error(R.drawable.imagenotfound)
                        .into(holder.mImageView)
            } else {
                holder.mImageView.setImageURI(Uri.fromFile(File(mListData!![position]?.citationImage)))
            }
            if (mListData!![position]?.edit == 1) {
                holder.mImageViewDelete.visibility = View.GONE
            } else {
                holder.mImageViewDelete.visibility = View.GONE
            }

            holder.mImageViewDelete.setOnClickListener {
                listItemSelectListener?.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var mImageView: AppCompatImageView
        var mImageViewDelete: AppCompatImageView

        init {
            mImageView = itemView.findViewById(R.id.iv_pagerItem)
            mImageViewDelete = itemView.findViewById(R.id.ivImage1Delete)
        }

        }

    interface ListItemSelectListener {
        fun onItemClick(position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        if (mListData!![position].type == FULLSIZE) {
            return FULLSIZE
        } else {
            return ITEMTWO
        }
        return FULLSIZE
    }

    companion object {
        const val FULLSIZE = 1
        const val ITEMTWO = 0
    }

}
