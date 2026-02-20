package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimingMarkData
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.isElapsTime
import com.parkloyalty.lpr.scan.util.AppUtils.isElapseTime
import com.parkloyalty.lpr.scan.util.AppUtils.isRemainingTimeForUI
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPR
import com.parkloyalty.lpr.scan.util.LoadingViewHolder
import java.io.IOException
import java.text.DecimalFormat
import java.util.*


class TimingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<TimingMarkData>? = null
    private var mContext: Context
    private var isTiming = false
    private var isTireStemWithImageView = false
    private var mFrom: String? = null
    private  var width: Int = 0
    private var height: Int = 0

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(context: Context, listData: List<TimingMarkData>?, mFrom : String,
        isTiming:Boolean,isTireStemWithImageView:Boolean, itemClickListener: ListItemSelectListener?) {
        mListData = listData
        mContext = context
        this.isTiming = isTiming
        this.mFrom = mFrom
        this.isTireStemWithImageView = isTireStemWithImageView
        listItemSelectListener = itemClickListener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val listItem = layoutInflater.inflate(R.layout.row_lpr_timing_list, parent, false)
//        return ViewHolder(listItem)

        return when (viewType) {
            Constants.VIEW_ITEM_LOADING -> {
                LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_loading_progress_item, parent, false))
            }
            else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_lpr_timing_list, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (mListData != null && holder is ViewHolder) {
            try {
                holder.tvDateValue.text =
                        splitDateLPR(mListData!![position].markStartTimestamp.toString()) //16/06/21 13:26:00
//                 holder.tvDateValue.text =
//                        splitDateLPR(mListData!![position].markIssueTimestamp.toString()) //16/06/21 13:26:00
                holder.mRegulation.text = isElapseTime(mListData!![position].regulationTime!!,mContext)
//                holder.mRegulation.text = isElapseTime(240,mContext) + " min"
                holder.tvLprNumber.text = mListData!![position].lpNumber.toString()
                if(mListData!![position].block!!.isNotEmpty() || mListData!![position].street!!.isNotEmpty()) {
                    holder.mLocation.text =
                        (if(mListData!![position].block!!.isNotEmpty())mListData!![position].block else "" )+ ", " + mListData!![position].street
                }
                holder.tvElapsedValue.text = isElapsTime(
                        mListData!![position].markStartTimestamp!!,
                        mListData!![position].regulationTime!!, mContext
                )
                holder.mStatus.text = mListData!![position].arrialStatus

                if (mListData!![position].zone?.isNotEmpty() ?: true && !mListData!![position].zone?.toString().equals("null")) {
                    holder.mZone.text = (mListData!![position].zone)
                }else{
                    holder.mZone.hideView()
                    holder.rlZoneView.visibility=View.GONE
                }
                if (mListData!![position].side?.isNotEmpty() ?: true && !mListData!![position].side?.toString().equals("null")) {
                    holder.tvSide.text = (mListData!![position].side)
                }else{
                    holder.rlSideView.visibility=View.GONE
//                    (holder.rlSideView.layoutParams as LinearLayoutCompat.LayoutParams).weight = 0.0f
                }
                if (mListData!![position].vinNumber?.isNotEmpty() ?: true &&
                        !mListData!![position].vinNumber?.toString()!!.trim().equals("null")) {
                    holder.tvVin.text = (": " + mListData!![position].vinNumber)
                    holder.llVin.visibility = View.VISIBLE
                }else{
                    holder.llVin.visibility = View.GONE
                }
                try {
                    if (mListData!![position].tireStemFront?.isNotEmpty() ?: true &&
                            !mListData!![position].tireStemFront?.toString()!!.trim().equals("null") &&
                            mListData!![position].tireStemFront?.toInt()!! > 0 && isTireStemWithImageView == false) {
                        holder.tvTierStem.text = (": " + mListData!![position].tireStemFront+"/"+ mListData!![position].tireStemBack)
                        holder.llTierStem.visibility = View.VISIBLE
                    }else{
                        holder.llTierStem.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    if (mListData!![position].lot?.isNotEmpty() ?: true &&
                            !mListData!![position].lot?.toString()!!.trim().equals("null")) {
                        holder.tvLotValue.text = (": " + mListData!![position].lot)
                        holder.rlLot.visibility = View.VISIBLE
                    }else{
                        holder.rlLot.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                holder.tvAlertType.text = mListData!![position].alertType
                holder.tvLRemainingTime.text = isRemainingTimeForUI(
                        mListData!![position].markStartTimestamp!!,
                        mListData!![position].regulationTime!!, mContext)

                if (mFrom.equals("LprResultScreen", true)) {
                    holder.ll_car_alert!!.visibility = View.GONE
                    if (isTiming) {
                        holder.layRowTiming.setBackground(mContext.getResources().getDrawable(R.drawable.round_corner_shape_without_fill_red))
                    } else {
                        holder.layRowTiming.setBackground(mContext.getResources().getDrawable(R.drawable.round_corner_shape_without_fill_green))
                    }
                    holder.checkbox.visibility = View.GONE;
                    try {
                        if(mListData!![position].block!!.isNotEmpty() || mListData!![position].street!!.isNotEmpty()) {
                            holder.mLocation.text =
                                (if(mListData!![position].block!!.isNotEmpty())mListData!![position].block else "") + ", " + mListData!![position].street
                        }
//                        if(holder.mLocation.text.isEmpty() && mListData!![position].location!=null &&
//                                mListData!![position].location!!.coordinates!![0]!=null)
//                        {
//                            holder.mLocation.text = getGeoAddress(mListData!![position].location!!.coordinates!![1].toDouble(),
//                                    mListData!![position].location!!.coordinates!![0].toDouble());
//                        }else{
////                            holder.mLocation.hideView()
//                        }
                        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)||
                            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)){
                            holder.rllocation!!.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        holder.checkbox.setOnCheckedChangeListener(null)
                        holder.checkbox.isChecked = mListData!![position].isChecked.nullSafety()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (mListData!![position].arrialStatus.equals("open", ignoreCase = true)) {
                        if (AppUtils.isTimingExpired(mListData!![position].markStartTimestamp!!, mListData!![position].regulationTime!!.toFloat())) {
                            holder.layRowTiming.background =
                                    mContext.resources.getDrawable(R.drawable.round_corner_shape_without_fill_red)
                        } else {
                            holder.layRowTiming.background =
                                    mContext.resources.getDrawable(R.drawable.round_corner_shape_without_fill_green)
                        }
                    } else if (mListData!![position].arrialStatus.equals("Enforced", ignoreCase = true)
                            || mListData!![position].arrialStatus.equals("GOA", ignoreCase = true)
                    ) {
                        holder.layRowTiming.background =
                                mContext.resources.getDrawable(R.drawable.round_corner_shape_without_fill_red)
                    } else {
                        holder.layRowTiming.background =
                                mContext.resources.getDrawable(R.drawable.round_corner_shape_without_fill_gray)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if(isTireStemWithImageView==true) {
                    if (mListData!![position].tireStemFront?.isNotEmpty() ?: true &&
                            !mListData!![position].tireStemFront?.toString()!!.trim().equals("null") &&
                            mListData!![position].tireStemFront?.toInt()!! > 0) {

                        setPositionOfStemValue(mListData!![position].tireStemFront!!.toInt(), holder.textStemValueFront)
                        holder.textStemValueFront.text = mListData!![position].tireStemFront!!
//                holder.textStemValueFront.text ="12"

                        setPositionOfStemValue(mListData!![position].tireStemBack!!.toInt(), holder.textStemValueRear)
                        holder.textStemValueRear.text = mListData!![position].tireStemBack!!

                        setPositionOfStemImage(mListData!![position].tireStemFront!!.toInt(),
                                mListData!![position].tireStemBack!!.toInt(), holder.appComImgViewTireIcon)
                        if (mFrom.equals("LprResultScreen", true)) {
                            (holder.ll_car_wheel.layoutParams as LinearLayoutCompat.LayoutParams).weight = 1.05f
                            (holder.ll_car_chassis.layoutParams as LinearLayoutCompat.LayoutParams).weight = 0.6f
                            holder.ll_car_alert!!.visibility = View.INVISIBLE
                        }
                    } else {
                        if (mFrom.equals("LprResultScreen", true)) {
                            holder.ll_tire_stem!!.visibility = View.GONE
                        }
                        (holder.ll_car_wheel.layoutParams as LinearLayoutCompat.LayoutParams).weight = 0f
                        (holder.ll_car_chassis.layoutParams as LinearLayoutCompat.LayoutParams).weight = 0f

                    }
                }else{
                    holder.ll_tire_stem!!.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            holder.layRowTiming.setOnClickListener {
                listItemSelectListener?.onItemClick(
                    mListData!![position]
                )
            }

            holder.checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                listItemSelectListener?.onItemChecked(
                    holder.adapterPosition, isChecked
                )
//            mListData!![position].isChecked = isChecked
            }
            )
        }

    }

    fun updateList(list: List<TimingMarkData>?,isTireStemWithImageView:Boolean) {
        mListData = list
        this.isTireStemWithImageView = isTireStemWithImageView;
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    //Keep these two below methods whebn you are working with checkbox in recycler view adapter
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

//    override fun getItemViewType(position: Int): Int {
//        return position
//    }

    override fun getItemViewType(position: Int) = if (mListData!![position] == null && mListData!!.isNotEmpty()) Constants.VIEW_ITEM_LOADING else Constants.VIEW_ITEM_CONTAINER


    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mLocation: AppCompatTextView
        var mRegulation: AppCompatTextView
        var tvDateValue: AppCompatTextView
        var tvElapsedValue: AppCompatTextView
        var tvLprNumber: AppCompatTextView
        var tvSide: AppCompatTextView
        var tvVin: AppCompatTextView
        var llVin: RelativeLayout
        var tvTierStem: AppCompatTextView
        var llTierStem: RelativeLayout
        var tvLRemainingTime: AppCompatTextView
        var tvAlertType: AppCompatTextView
        var tvLotValue: AppCompatTextView
        var rlLot: RelativeLayout
        var rllocation: RelativeLayout
        var mZone: AppCompatTextView
        var mStatus: AppCompatTextView
        var layRowTiming: LinearLayoutCompat
        var llBottomView: LinearLayoutCompat
        var ll_car_alert: LinearLayoutCompat
        var ll_car_chassis: LinearLayoutCompat
        var ll_car_wheel: LinearLayoutCompat
        var ll_tire_stem: LinearLayoutCompat
        var checkbox : CheckBox
        var textStemValueFront : AppCompatTextView
        var textStemValueRear : AppCompatTextView
        var appComImgViewTireIcon : AppCompatImageView
        var rlZoneView : RelativeLayout
        var rlSideView : RelativeLayout

        init {
            mLocation = itemView.findViewById(R.id.tvLocationValue)
            tvElapsedValue = itemView.findViewById(R.id.tvElapsedValue)
            mRegulation = itemView.findViewById(R.id.tvRegulationValue)
            tvDateValue = itemView.findViewById(R.id.tvDateValue)
            tvLprNumber = itemView.findViewById(R.id.tvLprNumber)
            layRowTiming = itemView.findViewById(R.id.layRowTiming)
            mZone = itemView.findViewById(R.id.tv_zoneValue)
            mStatus = itemView.findViewById(R.id.tv_status_value)
            tvSide = itemView.findViewById(R.id.tv_side_value)
            tvVin = itemView.findViewById(R.id.tv_vin_value)
            llVin = itemView.findViewById(R.id.ll_vin)
            tvLRemainingTime = itemView.findViewById(R.id.tv_remaining_value)
            tvAlertType = itemView.findViewById(R.id.tv_alerttypeLocationValue)
            checkbox = itemView.findViewById(R.id.checkbox)
            llTierStem = itemView.findViewById(R.id.rl_tier)
            tvTierStem = itemView.findViewById(R.id.tv_tier_value1)
            llBottomView = itemView.findViewById(R.id.ll_bottom_view)
            tvLotValue = itemView.findViewById(R.id.tvLotValue)
            rlLot = itemView.findViewById(R.id.rl_lot)
            rllocation = itemView.findViewById(R.id.rllocation)
            textStemValueFront = itemView.findViewById(R.id.textstemvaluefront)
            textStemValueRear = itemView.findViewById(R.id.textstemvaluerear)
            appComImgViewTireIcon = itemView.findViewById(R.id.appcomimgview_front)

            ll_car_alert = itemView.findViewById(R.id.ll_car_alert)
            ll_car_chassis = itemView.findViewById(R.id.ll_car_chassis)
            ll_car_wheel = itemView.findViewById(R.id.ll_car_wheel)
            ll_tire_stem = itemView.findViewById(R.id.ll_tire_stem)
            rlZoneView = itemView.findViewById(R.id.rlzoneview)
            rlSideView = itemView.findViewById(R.id.rlsideview)

          if(isTireStemWithImageView==true) {
              ll_tire_stem.visibility = View.VISIBLE
          val viewTreeObserver: ViewTreeObserver = ll_car_wheel.getViewTreeObserver()
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if(width<=0) {
                        ll_car_wheel.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                        width = ll_car_wheel.getMeasuredWidth()
                        height = ll_car_wheel.getMeasuredHeight()
                        notifyDataSetChanged()
//                        ll_car_wheel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            })
            }
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(markData: TimingMarkData?)
        fun onItemChecked(position: Int, isChecked: Boolean?){}
    }

    data class Something(val mandatory: String) {
        companion object {
            operator fun invoke(s : String? = null) = Something( s ?: "")
        }
    }

    @Throws(IOException::class)
    private fun getGeoAddress(mLat : Double, mLong: Double):String {
        try {
              /*  var mLat = geoLat
                var mLong = geoLon
                if (mLat > 0 && mLong > 0) {
                    mLat = geoLat
                    mLong = geoLon
                } else {
                    mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
                    mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
                }*/

            val dFormat = DecimalFormat("#.######")

            var mLati = java.lang.Double.valueOf(dFormat.format(mLat))
            var mLongi = java.lang.Double.valueOf(dFormat.format(mLong))

                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(mContext, Locale.getDefault())


                addresses = geocoder.getFromLocation(
                        mLati,
                    mLongi,
                        1
                )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
             var addressGeo:String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
             var separated:Array<String>? = addressGeo.nullSafety().split(",").toTypedArray()
             val printAddress = separated!![0] // this will contain "Fruit"
//                mTvReverseCoded.text = printAddress
            return printAddress
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun setPositionOfStemValue(selectedValue:Int,appCompatTextViewStemValue:AppCompatTextView)
    {
        try {
            appCompatTextViewStemValue.visibility = View.VISIBLE
            when(selectedValue)
            {
                1,15-> {
                    appCompatTextViewStemValue.x = (width*0.55f)
                    appCompatTextViewStemValue.y = (10f)
                }
                2,30 -> {
                    appCompatTextViewStemValue.x = (width*0.65f)
                    appCompatTextViewStemValue.y = (height*0.2f)
                }
                3,45 -> {
                    appCompatTextViewStemValue.x = (width*0.7f)
                    appCompatTextViewStemValue.y = (height*0.38f)
                }
                4,60 -> {
                    appCompatTextViewStemValue.x = (width*0.65f)
                    appCompatTextViewStemValue.y = (height*0.6f)
                }
                5,75 -> {
                    appCompatTextViewStemValue.x = (width*0.55f)
                    appCompatTextViewStemValue.y = (height*0.72f)
                }
                6,90 -> {
                    appCompatTextViewStemValue.x = (width*0.41f)
                    appCompatTextViewStemValue.y = (height*0.8f)
                }
                7,105 -> {
                    appCompatTextViewStemValue.x = (width*0.24f)
                    appCompatTextViewStemValue.y = (height*0.72f)
                }
                8,120 -> {
                    appCompatTextViewStemValue.x = (width*0.16f)
                    appCompatTextViewStemValue.y = (height*0.6f)
                }
                9,135 -> {
                    appCompatTextViewStemValue.x = (width*0.12f)
                    appCompatTextViewStemValue.y = (height*0.4f)
                }
                10,150 -> {
                    appCompatTextViewStemValue.x = (width*0.16f)
                    appCompatTextViewStemValue.y = (height*0.2f)
                }
                11,165 -> {
                    appCompatTextViewStemValue.x = (width*0.27f)
                    appCompatTextViewStemValue.y = (height*0.1f)
                }
                12,180 -> {
                    appCompatTextViewStemValue.x = (width*0.41f)
                    appCompatTextViewStemValue.y = (height*0.06f)
                }
        }
//        appCompatTextViewStemValue.x = (65f-26f)
//        appCompatTextViewStemValue.y = (8f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setPositionOfStemImage(selectedValueFront:Int,selectedValueRear:Int,
                                       appComImgViewTireIcon:AppCompatImageView) {

        try {
            if (selectedValueFront>0 && selectedValueRear>0) {
                appComImgViewTireIcon.setImageResource(R.drawable.look_both_tire_red)
            } else if (selectedValueFront>0 && selectedValueRear<=0) {
                appComImgViewTireIcon.setImageResource(R.drawable.look_front_tire_red)
            } else if (selectedValueFront<=0 && selectedValueRear>0) {
                appComImgViewTireIcon.setImageResource(R.drawable.look_rear_tire_red)
            }else{
                appComImgViewTireIcon.setImageResource(R.drawable.both_tire_black)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}