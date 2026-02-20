package com.parkloyalty.lpr.scan.ui.ticket

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import java.io.File

class ViewPagerBannerAddNoteAdapter(
    private val mContext: Context,
    var listItemSelectListener: ListItemSelectListener
) : PagerAdapter() {
    private var bannerList: List<CitationImagesModel>? = null

    fun setAnimalBannerList(bannerList: List<CitationImagesModel>?) {
        this.bannerList = bannerList // Initilize banner list inside the method
    }

    override fun getCount(): Int {
        return if (bannerList != null && !bannerList!!.isEmpty()) {
            bannerList!!.size
        } else 1
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayoutCompat
    }

    override fun getPageWidth(position: Int): Float {
        return 0.5f
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            LayoutInflater.from(mContext).inflate(R.layout.row_pager_lpr_details, container, false)
        val url = ""
        /*
           Check banner list is empty or not if list is not empty then get image url from list
         */if (bannerList != null && !bannerList!!.isEmpty()) {
            //url = bannerList.get(position);
        }
        val mImageView: AppCompatImageView = itemView.findViewById(R.id.iv_pagerItem)
        val mImageViewDelete: AppCompatImageView = itemView.findViewById(R.id.ivImage1Delete)

        mImageViewDelete.contentDescription = mContext.getString(R.string.ada_content_description_remove)

        if (bannerList!![position].citationImage != null) {
            if (bannerList!![position].status == 1) {
                Glide.with(mContext)
                    .load(bannerList!![position].citationImage)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //                .placeholder(R.drawable.placeholder)
                    //                .error(R.drawable.imagenotfound)
                    .into(mImageView)
            } else {
                mImageView.setImageURI(Uri.fromFile(File(bannerList!![position].citationImage)))
            }
            if (bannerList!![position].status == 1) {
                mImageViewDelete.visibility = View.GONE
            } else {
                mImageViewDelete.visibility = View.VISIBLE
            }
            mImageViewDelete.setOnClickListener {
                if (bannerList!![position].status == 0) {
                    listItemSelectListener.onItemClick(position)
                    //                        bannerList.remove(position);
                    notifyDataSetChanged()
                }
            }
        }

        /* Glide.with(mContext) //Download image from server and set into image view
                .load(url)
                .placeholder(R.color.white)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fitCenter()
                .into(mImageView);*/
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayoutCompat)
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    interface ListItemSelectListener {
        fun onItemClick(position: Int)
    }
}