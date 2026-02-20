package com.parkloyalty.lpr.scan.ui.check_setup.adapter

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
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.util.SHOW_DELETE_BUTTON
import java.io.File

class AbandonedVehicleBannerAdapter(
    private val mContext: Context,
    var listItemSelectListener: ListItemSelectListener
    ) : PagerAdapter() {
        private var bannerList: List<TimingImagesModel?>? = null
        fun setTimingBannerList(bannerList: List<TimingImagesModel?>?) {
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

//        override fun getPageWidth(position: Int): Float {
//            return 0.9f
//        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView =
                LayoutInflater.from(mContext).inflate(R.layout.row_pager_lpr_details, container, false)
            val mImageView: AppCompatImageView = itemView.findViewById(R.id.iv_pagerItem)
            val mImageViewDelete: AppCompatImageView = itemView.findViewById(R.id.ivImage1Delete)
            mImageViewDelete.visibility = View.GONE
            if (bannerList!![position]?.timingImage != null) {
                if (bannerList!![position]?.status == 1) {
                    Glide.with(mContext)
                        .load(bannerList!![position]?.timingImage)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        //                .placeholder(R.drawable.placeholder)
                        //                .error(R.drawable.imagenotfound)
                        .into(mImageView)
                } else {
                    mImageView.setImageURI(Uri.fromFile(File(bannerList!![position]?.timingImage)))
                }

                if (bannerList!![position]?.deleteButtonStatus == SHOW_DELETE_BUTTON){
                    mImageViewDelete.hideView()
                }else{
                    mImageViewDelete.hideView()
                }

                mImageViewDelete.setOnClickListener {
                    listItemSelectListener.onItemClick(position)
                }
            }

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