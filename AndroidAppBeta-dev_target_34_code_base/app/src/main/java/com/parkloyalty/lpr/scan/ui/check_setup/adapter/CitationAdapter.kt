package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForCitationFormFacsimileWithoutBox
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils

class CitationAdapter(
    val mContext: Context,
    val mListData: List<VehicleListModel>?,
    val listItemSelectListener: ListItemSelectListener?
) : RecyclerView.Adapter<CitationAdapter.ViewHolder>() {

    init {
        if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)&&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)) {
            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionHeader(
                mListData!![0],
                1,
                "citation",
                AppUtils.printQueryStringBuilder!!,
                1
            )
            if (checkBuildConfigForCitationFormFacsimileWithoutBox()) {
                if (AppUtils.getSectionLineInPrintOut()) {
//                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionLine(
//                        mListData!![0],
//                        1,
//                        "citation",
//                        AppUtils.printQueryStringBuilder!!
//                    )
                } else {
                    var lastIndex = mListData!!.size - 1
                    if (mListData!![mListData!!.size - 1].offTypeFirst!=null &&
                        mListData!![mListData!!.size - 1].offTypeFirst!!.isEmpty()) {
                        if (mListData!![mListData!!.size - 2].offTypeFirst!!.isEmpty()) {
                            lastIndex = mListData!!.size - 3
                        } else {
                            lastIndex = mListData!!.size - 2
                        }
                    }
                    // Orleans create one box for cover all 5 section
                    AppUtils.mOrleansBoxInitialYValue = (mListData!![0].mAxisY)
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA,ignoreCase = true)) {
                        AppUtils.mOrleansBoxInitialYValue = (mListData!![3].mAxisY)
                    }
                    AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionBox(
                        mListData!![0], mListData!![lastIndex], 1, "citation",
                        AppUtils.printQueryStringBuilder!!
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.content_double_text_print, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            try {


                val citationDataItem = mListData[position]

                LogUtil.printLog("CitationAdapter", "${citationDataItem!!.offNameFirst}")
                LogUtil.printLog("CitationAdapter", "${citationDataItem!!.offTypeFirst}")
                LogUtil.printLog("CitationAdapter", "=======================")


                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)
                    && (citationDataItem!!.offNameFirst.equals("Sign Notes", ignoreCase = true)
                            || citationDataItem!!.offTypeFirst.equals("RPP", ignoreCase = true)
                            || citationDataItem!!.offTypeFirst.equals("PD", ignoreCase = true)
                            || citationDataItem!!.offTypeFirst.equals("TAZ", ignoreCase = true)) ||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
                    && (citationDataItem!!.offNameFirst.equals("Sign Notes", ignoreCase = true)
                            || citationDataItem!!.offTypeFirst.equals("RPP", ignoreCase = true)
                            || citationDataItem!!.offTypeFirst.equals("PD", ignoreCase = true)
                            || citationDataItem!!.offTypeFirst.equals("TAZ", ignoreCase = true))
                ) {
                    holder.tvField.text = citationDataItem!!.offNameFirst
                    holder.tvFieldValue.visibility = View.GONE
                    holder.linearLayoutCompatCheckBox.visibility = View.VISIBLE
                    if (citationDataItem!!.offTypeFirst!!.contains("1")) {
                        holder.appCompatCheckBox1.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_baseline_check_box_24,
                            0,
                            0,
                            0
                        )
                    }
                    if (citationDataItem!!.offTypeFirst!!.contains("2")) {
                        holder.appCompatCheckBox2.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_baseline_check_box_24,
                            0,
                            0,
                            0
                        )
                    }
                    if (citationDataItem!!.offTypeFirst!!.contains("3")) {
                        holder.appCompatCheckBox3.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_baseline_check_box_24,
                            0,
                            0,
                            0
                        )
                    }
                } else {
                    if (citationDataItem!!.offNameFirst!!.isEmpty()) {
                        holder.tvField!!.visibility = View.GONE
                    } else {
                        holder.tvField!!.visibility = View.VISIBLE
                    }
                    holder.tvField.text = citationDataItem!!.offNameFirst
                    holder.tvFieldValue.text =
                        citationDataItem!!.offTypeFirst?.replace(Constants.COMMA, "")
                    holder.linearLayoutCompatCheckBox.visibility = View.GONE
                }

                AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrint(
                    mListData!!,
                    position,
                    "citation",
                    AppUtils.printQueryStringBuilder!!
                )

                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_WOODSTOCK_GA,
                        ignoreCase = true
                    ) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)
                ) {
                    if (citationDataItem!!.offNameFirst.equals("Location") ||
                        citationDataItem!!.offNameFirst.equals("Meter") ||
                        citationDataItem!!.offNameFirst.equals("Beat/District")
                    ) {
                        if (citationDataItem!!.offNameFirst.equals("Beat/District")) {
                            holder.tvFieldValue.setTextSize(10.0f);
                        } else {
                            holder.tvFieldValue.setTextSize(11.0f);
                        }
                    } else {
                        holder.tvFieldValue.setTextSize(
                            TypedValue.COMPLEX_UNIT_PX,
                            mContext.getResources().getDimension(R.dimen.print_label_13)
                        );
                    }

                }
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_CHARLESTON,
                        ignoreCase = true
                    )
                ) {
                    if (citationDataItem!!.mFontSize.equals(Constants.PRINT_TEXT_LARGE)
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
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_NORWALK,
                        ignoreCase = true
                    )
                ) {
                    if (citationDataItem!!.offNameFirst.equals("Agency")||
                        citationDataItem!!.offNameFirst.equals("Location")) {
                        holder.tvFieldValue.setTextSize(10.0f);
                    } else {
                        holder.tvFieldValue.setTextSize(
                            TypedValue.COMPLEX_UNIT_PX,
                            mContext.getResources().getDimension(R.dimen.print_label_13)
                        );
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val citationDataItem = mListData?.get(position)

        return when (citationDataItem?.type) {
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
        var linearLayoutCompatCheckBox: LinearLayoutCompat
        var appCompatCheckBox1: AppCompatTextView
        var appCompatCheckBox2: AppCompatTextView
        var appCompatCheckBox3: AppCompatTextView

        init {
            tvField = itemView.findViewById(R.id.tvField)
            tvFieldValue = itemView.findViewById(R.id.tvFieldValue)
            //tvFieldTwo = itemView.findViewById(R.id.tvFieldTwo)
            tvFieldValueTwo = itemView.findViewById(R.id.tvFieldValueTwo)
            linearLayoutCompatCheckBox = itemView.findViewById(R.id.ll_tickettype)
            appCompatCheckBox1 = itemView.findViewById(R.id.check_1)
            appCompatCheckBox2 = itemView.findViewById(R.id.check_2)
            appCompatCheckBox3 = itemView.findViewById(R.id.check_3)
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