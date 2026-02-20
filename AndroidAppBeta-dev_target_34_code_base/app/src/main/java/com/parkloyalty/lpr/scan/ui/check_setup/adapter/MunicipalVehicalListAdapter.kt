package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.setXYforPrintNewLine
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.xfprinter.TextAlignmentForCommandPrint
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils

class MunicipalVehicalListAdapter//        AppUtils.sectionFirst = AppUtils.setXYforPrintSectionLine(mListData!![mListData!!.size-1],1,"citation",AppUtils.sectionFirst!!)
    (
    val mContext: Context,
    val mListData: List<VehicleListModel>?,
    val listItemSelectListener: ListItemSelectListener?
) : RecyclerView.Adapter<MunicipalVehicalListAdapter.ViewHolder>() {

    init {
        if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)&&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)) {
            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionHeader(
                mListData!![0],
                1,
                "vehicle",
                AppUtils.printQueryStringBuilder!!,
                1
            )
            if (
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)) {
            if (AppUtils.getSectionLineInPrintOut()) {
                AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionLine(
                    mListData!![0],
                    1,
                    "vehicle",
                    AppUtils.printQueryStringBuilder!!
                )
            } else {
                var lastIndex = mListData!!.size - 1
                if (mListData!![mListData!!.size - 1].offTypeFirst!!.isEmpty()) {
                    if (mListData!![mListData!!.size - 2].offTypeFirst!!.isEmpty()) {
                        lastIndex = mListData!!.size - 3
                    } else {
                        lastIndex = mListData!!.size - 2
                    }
                }
                AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionBox(
                    mListData!![0], mListData!![lastIndex], 1, "vehicle",
                    AppUtils.printQueryStringBuilder!!
                )

            }
            }
        }

        /**
         * Adding new line before new section
         */
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            val vehicleListModel = VehicleListModel()
            //This offNameFirst has no use, just added for reference
            vehicleListModel.offNameFirst = "NewLine"
            vehicleListModel.mTextAlignment = TextAlignmentForCommandPrint.CENTER
            vehicleListModel.type = 3

            AppUtils.printQueryStringBuilder = setXYforPrintNewLine(
                vehicleListModel,
                1,
                "newline",
                AppUtils.printQueryStringBuilder
            )
        }
//        AppUtils.sectionFirst = AppUtils.setXYforPrintSectionLine(mListData!![mListData!!.size-1],1,"citation",AppUtils.sectionFirst!!)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.content_double_text_print, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            try{
            val vehicleDataItem = mListData[position]

            LogUtil.printLog("VehicleAdapter","${vehicleDataItem!!.offNameFirst}")
            LogUtil.printLog("VehicleAdapter","${vehicleDataItem!!.offTypeFirst}")
            LogUtil.printLog("VehicleAdapter","${vehicleDataItem!!.mAxisX}")
            LogUtil.printLog("VehicleAdapter","${vehicleDataItem!!.mAxisY}")
            LogUtil.printLog("VehicleAdapter","=======================")

            holder.tvField.text = vehicleDataItem!!.offNameFirst
            holder.tvFieldValue.text = vehicleDataItem!!.offTypeFirst
            if (vehicleDataItem!!.offNameFirst != null && vehicleDataItem!!.offNameFirst!!.isEmpty()) {
                holder.tvField.visibility = View.GONE
            }

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)) {
                if (vehicleDataItem!!.mFontSize.equals(Constants.PRINT_TEXT_LARGE)
                ) {
                    holder.tvFieldValue.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.print_label_20)
                    )
                    holder.tvField.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.print_label_16)
                    )
                } else {
                    holder.tvFieldValue.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.print_label_13)
                    )
                    holder.tvField.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.print_label_11)
                    );
                }
            }

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)
            ) {
                if (vehicleDataItem!!.offNameFirst.equals("Issue Type")) {
                    holder.tvFieldValue.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.print_label_11)
                    );
//                        holder.tvField.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                            mContext.getResources().getDimension(R.dimen.print_label_11));
                } else {
                    holder.tvFieldValue.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.print_label_12)
                    );
                }
            }
            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforMunicipalPrint(
                mListData!!, position, "vehicle",
                AppUtils.printQueryStringBuilder!!
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val vehicleDataItem = mListData?.get(position)
        return when (vehicleDataItem?.type) {
            ONECOLUMN -> {
                ONECOLUMN
            }

            TWOCOLUMN -> {
                TWOCOLUMN
            }

            THREECOLUMN -> {
                THREECOLUMN
            }

            else -> ONECOLUMN
        }
    }

    override fun getItemCount(): Int {
        return mListData?.size ?: 0
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvField: AppCompatTextView
        //var tvFieldTwo: AppCompatTextView
        var tvFieldValue: AppCompatTextView
        var tvFieldValueTwo: AppCompatTextView

        init {
            tvField = itemView.findViewById(R.id.tvField)
            tvFieldValue = itemView.findViewById(R.id.tvFieldValue)
            //tvFieldTwo = itemView.findViewById(R.id.tvFieldTwo)
            tvFieldValueTwo = itemView.findViewById(R.id.tvFieldValueTwo)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }

    companion object {
        const val THREECOLUMN = 3
        const val ONECOLUMN = 1
        const val TWOCOLUMN = 2
    }
}