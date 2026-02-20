package com.parkloyalty.lpr.scan.ui.continuousmode

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.ui.continuousmode.model.ResultDataObject
import java.io.File
import java.io.IOException
import java.util.*

class ContinousResultAdapter : RecyclerView.Adapter<ContinousResultAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<ResultDataObject>? = null
    private var mContext: Context

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<ResultDataObject>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.content_continuous_result_row, parent, false)
        return ViewHolder(listItem)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            holder.txtLpNumber.text = mListData!![position].mLpNumber.nullSafety()
            holder.txtDiscription.text = if (!mListData!![position].mDes.nullSafety().isEmpty()
                && mListData!![position].mDes.nullSafety().length > 1
            ) mListData!![position].mDes.nullSafety() else "No Data"
            if (mListData!![position].mDes.nullSafety().equals("Rpp", ignoreCase = true)) {
                holder.txtLpNumber.background =
                    mContext.getDrawable(R.drawable.round_corner_shape_without_fill_orange_)
                holder.linearLayoutCompatMainView.background =
                    mContext.getDrawable(R.drawable.round_corner_fill_10gray)
                holder.linearLayoutCompatChild.setBackgroundColor(mContext.getColor(R.color.tran_new_orange))
            } else if (mListData!![position].mDes.nullSafety().equals("paid", ignoreCase = true) ||
                mListData!![position].mDes.nullSafety().equals("permit", ignoreCase = true)
            ) {
                holder.txtLpNumber.background =
                    mContext.getDrawable(R.drawable.round_corner_shape_without_fill_thin_green)
                holder.linearLayoutCompatMainView.background =
                    mContext.getDrawable(R.drawable.round_corner_fill_10gray)
                holder.linearLayoutCompatChild.setBackgroundColor(mContext.getColor(R.color.tran_deep_green))
                holder.linearLayoutCompatChild.setBackgroundColor(mContext.getColor(R.color.tran_deep_green))
            } else if (mListData!![position].mDes.nullSafety().equals("Scofflaw", ignoreCase = true)) {
                holder.txtLpNumber.background =
                    mContext.getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                holder.linearLayoutCompatMainView.background =
                    mContext.getDrawable(R.drawable.round_corner_fill_30red)
                holder.linearLayoutCompatChild.setBackgroundColor(mContext.getColor(R.color.trangraph_2Red))
            } else {
                holder.txtLpNumber.background =
                    mContext.getDrawable(R.drawable.round_corner_shape_without_fill_thin_grey)
                holder.linearLayoutCompatMainView.background =
                    mContext.getDrawable(R.drawable.round_corner_fill_20gray)
            }
            //                if(mListData.get(position).getmBackgroundColor()==0)
//                {
//                    holder.linearLayoutCompatMainView.setBackground(mContext.getDrawable(R.drawable.round_corner_fill_10gray));
//                }else{
//                    holder.linearLayoutCompatMainView.setBackground(mContext.getDrawable(R.drawable.round_corner_fill_20gray));
//                }
            try {
                val address = getGeoAddress(
                    mListData!![position].mLat.nullSafety(),
                    mListData!![position].mLong.nullSafety()
                )
                holder.txtAddress.text = address
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder.txtFirstTime.text = "First Time: " + mListData!![position].mFirstTime.nullSafety()
            holder.txtLastTime.text = "Last Time: " + mListData!![position].mLastTime.nullSafety()
            val imgFile = File(mListData!![position].mImagePath.nullSafety())
            if (imgFile.exists()) {
                holder.mImageViewNumberPlate.setImageURI(Uri.fromFile(imgFile))
            }
            holder.linearLayoutCompatMainView.setOnClickListener {
                listItemSelectListener!!.onItemClick(
                    holder.adapterPosition
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
        //        return   8  ;
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var txtLpNumber: AppCompatTextView
        var txtDiscription: AppCompatTextView
        var txtAddress: AppCompatTextView
        var txtFirstTime: AppCompatTextView
        var txtLastTime: AppCompatTextView
        var linearLayoutCompatMainView: LinearLayoutCompat
        var linearLayoutCompatChild: LinearLayoutCompat
        var mImageViewNumberPlate: AppCompatImageView

        init {
            txtLpNumber = itemView.findViewById(R.id.text_lp_number)
            txtDiscription = itemView.findViewById(R.id.text_des)
            txtAddress = itemView.findViewById(R.id.text_address)
            txtFirstTime = itemView.findViewById(R.id.text_first_time)
            txtLastTime = itemView.findViewById(R.id.text_end_time)
            linearLayoutCompatMainView = itemView.findViewById(R.id.ll_item)
            mImageViewNumberPlate = itemView.findViewById(R.id.ivNumberPlate)
            linearLayoutCompatChild = itemView.findViewById(R.id.ll_child_container)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(position: Int)
    }

    @Throws(IOException::class)
    private fun getGeoAddress(mLat: Double?, mLong: Double?): String {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(mContext, Locale.getDefault())
        addresses = geocoder.getFromLocation(
            mLat!!,
            mLong!!,
            1
        )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        val address = addresses[0]
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val separated = address.split(",").toTypedArray()
        return separated[0]
    }
}