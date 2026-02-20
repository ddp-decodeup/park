package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.checkBuildConfigForCitationFormFacsimileWithoutBox
import com.parkloyalty.lpr.scan.extensions.setXYforPrintTitle
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.xfprinter.TextAlignmentForCommandPrint
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.ALIGNMENT_FOR_COMMENT_TITLE
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.COLUMN_COUNT_FOR_COMMENT_TITLE
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.FONT_SIZE_FOR_COMMENT_TITLE
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils

class CommentesAdapter//      AppUtils.sectionFirst = ZebraCommandPrintUtils.setXYforPrintSectionLine(mListData!![mListData!!.size-1],1,"comment",AppUtils.sectionFirst!!)
    (
    val mContext: Context,
    val mListData: List<VehicleListModel>?,
    val listItemSelectListener: ListItemSelectListener?
) : RecyclerView.Adapter<CommentesAdapter.ViewHolder>() {

    init {
        if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)
        ) {
            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrintSectionHeader(
                mListData!![0],
                1,
                "comment",
                AppUtils.printQueryStringBuilder!!,
                1
            )
            if (checkBuildConfigForCitationFormFacsimileWithoutBox()) {
                if (AppUtils.getSectionLineInPrintOut()) {
                    AppUtils.printQueryStringBuilder =
                        ZebraCommandPrintUtils.setXYforPrintSectionLine(
                            mListData!![0],
                            1,
                            "comment",
                            AppUtils.printQueryStringBuilder!!
                        )
                } else {
                    var lastIndex = mListData!!.size - 1
//                    if (mListData!!.size>1 && mListData!![mListData!!.size - 1].offTypeFirst!!.isNotEmpty()) {
//                        lastIndex = mListData!!.size - 2
//                    }

                    if (mListData!![mListData!!.size - 1].offTypeFirst!!.trim().isEmpty()) {
                        if (mListData!![mListData!!.size - 2].offTypeFirst!!.trim().isEmpty()) {
                            lastIndex = mListData!!.size - 3
                        } else {
                            lastIndex = mListData!!.size - 2
                        }
                    }

                    AppUtils.printQueryStringBuilder =
                        ZebraCommandPrintUtils.setXYforPrintSectionBox(
                            mListData!![0], mListData!![lastIndex], 1, "comment",
                            AppUtils.printQueryStringBuilder!!
                        )

                }
            }
        }
//      AppUtils.sectionFirst = AppUtils.setXYforPrintSectionLine(mListData!![mListData!!.size-1],1,"comment",AppUtils.sectionFirst!!)
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            /**             * Add Comment header for print command             */
            val vehicleListModel = VehicleListModel()
            vehicleListModel.offNameFirst = AppUtils.commentSectionTitle
            vehicleListModel.mTextAlignment = TextAlignmentForCommandPrint.CENTER
        //            vehicleListModel.fontTypeForXFPrinter = FontType.TYPE_BOLD
        // Axis X Value Used for textAlignment
        // Axis Y Value used for columnMaxSize
        // mFontSizeInt Value used for selection of font
         vehicleListModel.mAxisX = ALIGNMENT_FOR_COMMENT_TITLE.toDouble()
         vehicleListModel.mAxisY = COLUMN_COUNT_FOR_COMMENT_TITLE
         vehicleListModel.mFontSizeInt = FONT_SIZE_FOR_COMMENT_TITLE
         vehicleListModel.type = 3
         AppUtils.printQueryStringBuilder = setXYforPrintTitle(vehicleListModel,1,"title",AppUtils.printQueryStringBuilder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        var listItem :View? = null
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)
            ) {
             listItem = layoutInflater.inflate(R.layout.content_double_text_print_pittsburg, parent, false)
        }else{
             listItem = layoutInflater.inflate(R.layout.content_double_text_print, parent, false)
        }
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            val commentDataItem = mListData[position]

            LogUtil.printLog("CommentAdapter","${commentDataItem!!.offNameFirst}")
            LogUtil.printLog("CommentAdapter","${commentDataItem!!.offTypeFirst}")
            LogUtil.printLog("CommentAdapter","=======================")


            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STRATOS, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)
                    || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true)){
                holder.tvField.text = commentDataItem!!.offNameFirst
                holder.tvFieldValue.text = commentDataItem!!.offTypeFirst
                if (commentDataItem!!.offNameFirst!!.isEmpty()) {
                    holder.tvField.visibility = View.GONE
                }
                if (commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE,
                                ignoreCase = true) ||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,
                                ignoreCase = true)||
                        commentDataItem!!.offTypeFirst!!.isEmpty() &&
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,
                                ignoreCase = true)) {
                    holder.tvFieldValue.visibility = View.GONE
                }
            } else {
                holder.tvField.text = commentDataItem!!.offNameFirst
                holder.tvFieldValue.text = commentDataItem!!.offTypeFirst
                if (commentDataItem!!.offNameFirst!!.isEmpty()) {
                    holder.tvField.visibility = View.GONE
                }
                holder.rlSecondview.visibility = View.VISIBLE
                holder.tvFieldValue.visibility = View.GONE
                holder.tvField.text = commentDataItem!!.offNameFirst + " "
                holder.tvFieldValueTwo.text = " " + commentDataItem!!.offTypeFirst
            }
            AppUtils.printQueryStringBuilder = ZebraCommandPrintUtils.setXYforPrint(mListData!!,position,"comment",
                AppUtils.printQueryStringBuilder!!)
            //            holder.tvFieldTwo.setText(mListData.get(position).getOffNameSec());
//            holder.tvFieldValueTwo.setText(mListData.get(position).getOffTypeSec());
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)) {
                holder.tvFieldValue.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.print_label_9))
                holder.tvField.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.print_label_9));
        }

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)) {
            val textLength = holder.tvFieldValue.text?.length ?: 0
            val context = holder.tvFieldValue.context
            val threshold: Int = 30
            val smallSizeSp: Float = 4f
            if (textLength > threshold) {
                holder.tvFieldValue.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.print_label_9))
                holder.tvFieldValueTwo.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.print_label_9))
            } else {
                holder.tvFieldValue.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.print_label_12))
                holder.tvFieldValueTwo.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.print_label_12))
            }
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
        var rlSecondview: RelativeLayout

        init {
            tvField = itemView.findViewById(R.id.tvField)
            tvFieldValue = itemView.findViewById(R.id.tvFieldValue)
            //tvFieldTwo = itemView.findViewById(R.id.tvFieldTwo)
            tvFieldValueTwo = itemView.findViewById(R.id.tvFieldValueTwo)
            rlSecondview = itemView.findViewById(R.id.rl_secondview)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val commentDataItem = mListData?.get(position)

        return when (commentDataItem?.type) {
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

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }

    companion object {
        const val ONECOLUMN = 1
        const val TWOCOLUMN = 2
        const val THREECOLUMN = 3
    }


    fun adjustTextSizeForLength(textView: AppCompatTextView, threshold: Int = 30, smallSizeSp: Float = 4f) {
        val textLength = textView.text?.length ?: 0
        val context = textView.context

        if (textLength > threshold) {
            // Reduce font size if text is too long
            textView.post {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallSizeSp)
        }
//            LogUtil.printLog("TextAdjust", "Text too long ($textLength chars) → reduced to $smallSizeSp sp")
        } else {
            // Restore to XML-defined default (reapply from resources)
            val defaultSize = textView.resources.getDimension(R.dimen.print_label_10) /
                    textView.resources.displayMetrics.scaledDensity
            textView.post {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultSize)
            }
//            LogUtil.printLog("TextAdjust", "Normal text ($textLength chars) → default $defaultSize sp")
        }
    }
}