package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForCitationFormFacsimileWithoutBox
import com.parkloyalty.lpr.scan.extensions.setXYforPrintNewLine
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.xfprinter.TextAlignmentForCommandPrint
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils

class OfficerListAdapter//        AppUtils.sectionFirst = ZebraCommandPrintUtils.setXYforPrintSectionLine(mListData!![mListData!!.size-1],1,"citation",AppUtils.sectionFirst!!)
    (
    val mContext: Context,
    val mListData: List<VehicleListModel>?,
    val listItemSelectListener: ListItemSelectListener?
) : RecyclerView.Adapter<OfficerListAdapter.ViewHolder>() {

    private var typefaceRobotoRegular: Typeface? = null
    private var typefaceRobotoBlack: Typeface? = null

    init {
        typefaceRobotoRegular = mContext.resources.getFont(R.font.robotomono_regular)
        typefaceRobotoBlack = mContext.resources.getFont(R.font.robotomono_semibold)
        if (mListData!!.size>0&&!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)) {
            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionHeader(
                mListData!![0],
                1,
                "officer",
                AppUtils.printQueryStringBuilder!!,
                1
            )
            if (checkBuildConfigForCitationFormFacsimileWithoutBox()) {
                if (AppUtils.getSectionLineInPrintOut()) {
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionLine(
                        mListData!![0],
                        1,
                        "officer",
                        AppUtils.printQueryStringBuilder!!
                    )
                } else {
                    var lastIndex = mListData!!.size - 1
                    if (mListData!![mListData!!.size - 1].offTypeFirst!!.isEmpty()) {
                        lastIndex = mListData!!.size - 2
                    }
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionBox(
                        mListData!![0], mListData!![lastIndex], 1, "Officer",
                        AppUtils.printQueryStringBuilder!!
                    )
                }
            }
        }
//        AppUtils.sectionFirst = ZebraCommandPrintUtils.setXYforPrintSectionLine(mListData!![mListData!!.size-1],1,"citation",AppUtils.sectionFirst!!)
        /**
         * Adding new line before new section
         */
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            val vehicleListModel = VehicleListModel()
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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.content_double_text_print, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            val officerDataItem = mListData[position]

            LogUtil.printLog("OfficerAdapter","${officerDataItem!!.offNameFirst}")
            LogUtil.printLog("OfficerAdapter","${officerDataItem!!.offTypeFirst}")
            LogUtil.printLog("OfficerAdapter","=======================")


            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())) {
                holder.rlSecondview.visibility = View.VISIBLE
                holder.tvFieldValue.visibility = View.GONE
                holder.tvField.text = officerDataItem!!.offNameFirst + " "
                holder.tvFieldValueTwo.text = " " + officerDataItem!!.offTypeFirst
            } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)) {
                if(officerDataItem!!.mTextAlignment!=null && officerDataItem!!.mTextAlignment!!.equals(Constants.PRINT_LAYOUT_VERTICAL)
                    && officerDataItem!!.mFontSize!=null && officerDataItem!!.mFontSize!!.equals(Constants.PRINT_TEXT_LARGE)) {
                    holder.rlSecondview.visibility = View.VISIBLE
                    holder.tvFieldValue.visibility = View.GONE

                    holder.tvField.text = officerDataItem!!.offNameFirst
                    holder.tvFieldValueTwo.text =  officerDataItem!!.offTypeFirst

//                    holder.tvFieldValueTwo.setTypeface(typefaceRobotoBlack)
                    holder.tvFieldValueTwo.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.print_label_14))

                }else{
                    holder.rlSecondview.visibility = View.GONE
                    holder.tvFieldValue.visibility = View.VISIBLE
                    if(officerDataItem!!.mFontSize!=null &&
                        officerDataItem!!.mFontSize!!.equals(Constants.PRINT_TEXT_LARGE)){
                        holder.tvFieldValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            mContext.getResources().getDimension(R.dimen.print_label_15))
//                        holder.tvField.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                            mContext.getResources().getDimension(R.dimen.print_label_11))
                        holder.tvFieldValue.setTypeface(typefaceRobotoBlack)
                    }else{
                        holder.tvFieldValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            mContext.getResources().getDimension(R.dimen.print_label_14))
//                        holder.tvField.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                            mContext.getResources().getDimension(R.dimen.print_label_10))
                        holder.tvFieldValue.setTypeface(typefaceRobotoRegular)
                    }

//                    if(officerDataItem!!.offNameFirst!!.length>1) {
                        holder.tvField.text = officerDataItem!!.offNameFirst
//                    }
                    holder.tvFieldValue.text =  officerDataItem!!.offTypeFirst

//                    holder.tvFieldValue.setTypeface(typefaceRobotoRegular)
//                    holder.tvFieldValue.setTextSize(
//                        TypedValue.COMPLEX_UNIT_PX,
//                        mContext.getResources().getDimension(R.dimen.print_label_9));
                }
            } else {
                holder.tvField.text = officerDataItem!!.offNameFirst
                holder.tvFieldValue.text = officerDataItem!!.offTypeFirst
                if (officerDataItem!!.offNameFirst!=null && officerDataItem!!.offNameFirst!!.isEmpty()) {
                    holder.tvField.visibility = View.GONE
                }
            }
            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrint(mListData!!,position,"Officer",
                AppUtils.printQueryStringBuilder!!)

        }
    }

    override fun getItemViewType(position: Int): Int {
        val officerDataItem = mListData?.get(position)

        return when (officerDataItem?.type) {
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
        var tvFieldTwo: AppCompatTextView? = null
        var tvFieldThird: AppCompatTextView
        var tvFieldValue: AppCompatTextView
        var tvFieldValueTwo: AppCompatTextView
        var tvFieldValueThird: AppCompatTextView
        var rlSecondview: RelativeLayout

        init {
            tvField = itemView.findViewById(R.id.tvField)
            tvFieldValue = itemView.findViewById(R.id.tvFieldValue)
            //            tvFieldTwo = itemView.findViewById(R.id.tvFieldTwo);
            tvFieldValueTwo = itemView.findViewById(R.id.tvFieldValueTwo)
            tvFieldThird = itemView.findViewById(R.id.tvFieldthree)
            tvFieldValueThird = itemView.findViewById(R.id.tvFieldThreeValue)
            rlSecondview = itemView.findViewById(R.id.rl_secondview)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }

    companion object {
        const val ONECOLUMN = 1
        const val TWOCOLUMN = 2
        const val THREECOLUMN = 3
    }
}