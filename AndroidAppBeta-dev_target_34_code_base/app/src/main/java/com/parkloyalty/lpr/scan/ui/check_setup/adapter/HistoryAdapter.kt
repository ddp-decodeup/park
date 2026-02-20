package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.check_setup.activity.CitationHistoryPrinterActivity
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.CitationDataResponse
import com.parkloyalty.lpr.scan.ui.ticket.ViewFacsimileImageActivity
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.setCustomContentDescription
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPR
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<CitationDataResponse>? = null
    private var mContext: Context
    private var mDate: String? = null

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<CitationDataResponse>?,
        From:String,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_lpr_history_list, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            try {
                var code: String? = ""
                var desr = ""
                var fine = ""
                var lateFine = ""
                var due15 = ""
                var due30 = ""
                var due45 = ""
                var lot: String? = ""
                var street = ""
                var block = ""
                var direction = ""
                var side = ""
                var meter = ""
                if (!TextUtils.isEmpty(mListData!![position].violationDetails?.code)) {
                    code = mListData!![position].violationDetails?.code
                }
                if (!TextUtils.isEmpty(mListData!![position].violationDetails?.description)) {
                    desr = ", " + mListData!![position].violationDetails?.description
                }
                if (mListData!![position].violationDetails?.fine != 0.0) {
                    fine = ", Fine:" + mListData!![position].violationDetails?.fine
                }
                if (mListData!![position].violationDetails?.late_fine != 0.0) {
                    lateFine = ", Late fine:" + mListData!![position].violationDetails?.late_fine
                }
                if (mListData!![position].violationDetails?.due_15_days != 0.0) {
                    due15 = ", Due 15 days:" + mListData!![position].violationDetails?.due_15_days
                }
                if (mListData!![position].violationDetails?.due_30_days != 0.0) {
                    due30 =
                        ", Due 3position days:" + mListData!![position].violationDetails?.due_30_days
                }
                if (mListData!![position].violationDetails?.due_45_days != 0.0) {
                    due45 = ", Due 45 days:" + mListData!![position].violationDetails?.due_45_days
                }
                if (!TextUtils.isEmpty(mListData!![position].location!!.lot)) {
                    lot = mListData!![position].location!!.lot
                }
                if (!TextUtils.isEmpty(mListData!![position].location!!.street)) {
                    if (mListData!![position].location!!.street != "null") {
                        street = ", " + mListData!![position].location!!.street
                    }
                }
                if (!TextUtils.isEmpty(mListData!![position].location!!.block)) {
                    block = "Block:" + mListData!![position].location!!.block
                }
                if (!TextUtils.isEmpty(mListData!![position].location!!.direction)) {
                    direction = ", Direction:" + mListData!![position].location!!.direction
                }
                if (!TextUtils.isEmpty(mListData!![position].location!!.side)) {
                    side = ", Side:" + mListData!![position].location!!.side
                }
                if (!TextUtils.isEmpty(mListData!![position].location!!.meter)) {
                    meter = ", Meter" + mListData!![position].location!!.meter
                }
                holder.mViolation.text = code + desr + fine + lateFine + due15 + due30 + due45 //
                //            holder.mTicketAddress.setText(lot+street+block+direction+side+meter);
                holder.mTicketAddress.text = block + street
                holder.mTicketCreator.text =
                    "#" + splitDateLPR(mListData!![position].citationIssueTimestamp!!)
                holder.mTicketNum.text = mListData!![position].ticketNo
                //holder.mViolation.setText(splitDate(mListData.get(position).getCode()));
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true)) {
                    holder.mTicketType.text =
                        mListData!![position].ticketType!!.replace("Warning", "Interruption")
                } else {
                    holder.mTicketType.text = mListData!![position].ticketType
                }
//            holder.mTicketType.text = mListData!![position].ticketType
                holder.mTvTicketStatus.text = mListData!![position].status
                holder.tvLprNumber.text = mListData!![position].lpNumber
                holder.mtvTicketDriveOff!!.text =
                    mListData!![position].driveOff + "/" + mListData!![position].tvr

//                if(mListData!!.fineAmountSum!=null &&
//                    mListData!![position].fineAmountSum.nullSafety()>0){
//                    holder.mtvFineAmountSum!!.visibility = View.VISIBLE
//                    holder.mtvFineAmountSum!!.text = ""+mListData!![position].fineAmountSum
//                }else{
//                    holder.mtvFineAmountSum!!.visibility = View.GONE
//                }

                if (mListData!![position].vehicleDetails!!.vin_number != null &&
                    mListData!![position].vehicleDetails!!.vin_number!!.isNotEmpty()
                ) {
                    holder.rl_vin_number.visibility = View.VISIBLE
                    holder.tvVinNumber.setText(mListData!![position].vehicleDetails!!.vin_number)
                } else {
                    holder.rl_vin_number.visibility = View.GONE
                }
                if (mListData!![position].violationDetails!!.mSanctionsType != null &&
                    mListData!![position].violationDetails!!.mSanctionsType!!.isNotEmpty()
                ) {
                    holder.rl_sanction_type.visibility = View.VISIBLE
                    holder.txtSanctionType.setText(mListData!![position].violationDetails!!.mSanctionsType)
                } else {
                    holder.rl_sanction_type.visibility = View.GONE
                }
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)) {
                    holder.rlFascimileImage.visibility = View.VISIBLE
                    holder.txtPrint.setText(mContext.getString(R.string.scr_lbl_print_ticket))
                }
               /* holder.rlFascimileImage.setOnClickListener {
                    try {
                        val mIntent = Intent(mContext, CitationHistoryPrinterActivity::class.java)
                        if (mListData!![position]?.images?.size!! > 0) {
                            mIntent.putExtra("lpr_number", mListData!![position].lpNumber)
                            mIntent.putExtra("printerQuery", mListData!![position].printQuery)
                            mIntent.putExtra("State", "")
                            mIntent.putExtra("citationNumber", "")
                            mContext.startActivity(mIntent)
//                            finish()
                        }
                        //holder.mTicketAddress.setText(mListData.get(position).getLocation().getBlock()+","+mListData.get(position).getLocation().getStreet());
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }*/

                holder.rlRowMain.setOnClickListener {
                    listItemSelectListener?.onItemClick(
                        holder.rlRowMain, false, position
                    )
                }

                //Set ADA
                holder.mTicketNum.setCustomContentDescription()
                holder.mTicketCreator.setCustomContentDescription()
                holder.mTicketAddress.setCustomContentDescription()
                holder.mViolation.setCustomContentDescription()
                holder.mTicketType.setCustomContentDescription()
                holder.mTvTicketStatus.setCustomContentDescription()
                holder.tvLprNumber.setCustomContentDescription()
                holder.mtvTicketDriveOff.setCustomContentDescription()
                holder.mtvFineAmountSum.setCustomContentDescription()
                holder.tvVinNumber.setCustomContentDescription()
                holder.txtPrint.setCustomContentDescription()
                holder.txtSanctionType.setCustomContentDescription()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun splitsFormat(Date: String?): String {
        val separated = Date!!.split("-").toTypedArray()
        val YYYY = separated[0]
        val MM = separated[1]
        val DD = separated[2]
        val calendar = Calendar.getInstance()
        calendar[Calendar.MONTH] = MM.toInt()
        val simpleDateFormat = SimpleDateFormat("MMM")
        simpleDateFormat.calendar = calendar
        val monthName = simpleDateFormat.format(calendar.time)
        return MM + "-" + DD + "-" + YYYY.substring(2, 4)
    }

    private fun getHours(hours: String): String? {
        val separated = hours.split(":").toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvert(`val`)
    }

    fun splitDate(date: String): String? {
        return try {
            val separated = date.split("T").toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            getHours(`val`)
        } catch (e: Exception) {
            date
        }
    }

    private fun dateConvert(`val`: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            val _24HourDt = _24HourSDF.parse(`val`)
            return splitsFormat(mDate) + " " + _12HourSDF.format(_24HourDt)
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getItemCount(): Int {
        return if (mListData == null) 0 else mListData!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mTicketNum: AppCompatTextView
        var mTicketCreator: AppCompatTextView
        var mTicketAddress: AppCompatTextView
        var mViolation: AppCompatTextView
        var mTicketType: AppCompatTextView
        var mTvTicketStatus: AppCompatTextView
        var tvLprNumber: AppCompatTextView
        var mtvTicketDriveOff: AppCompatTextView
        var mtvFineAmountSum: AppCompatTextView
        var tvVinNumber: AppCompatTextView
        var txtPrint: AppCompatTextView
        var txtSanctionType: AppCompatTextView
        var rlRowMain: LinearLayoutCompat
        var rl_vin_number: RelativeLayout
        var rl_sanction_type: RelativeLayout
        var rlFascimileImage: RelativeLayout

        init {
            mTicketNum = itemView.findViewById(R.id.tvTicketNumber)
            tvLprNumber = itemView.findViewById(R.id.tvLprNumber)
            mTicketCreator = itemView.findViewById(R.id.tvTicketNum)
            mTicketAddress = itemView.findViewById(R.id.tvAddressValue)
            mViolation = itemView.findViewById(R.id.tvViolation)
            mTicketType = itemView.findViewById(R.id.tvTicketType)
            mTvTicketStatus = itemView.findViewById(R.id.tvTicketStatus)
            mtvTicketDriveOff = itemView.findViewById(R.id.tvTicketDriveOff)
            mtvFineAmountSum = itemView.findViewById(R.id.tvFineAmountSum)
            rlRowMain = itemView.findViewById(R.id.layRowCitation)
            rl_vin_number = itemView.findViewById(R.id.rl_vin_number)
            rl_sanction_type = itemView.findViewById(R.id.rl_sanction_type)
            tvVinNumber = itemView.findViewById(R.id.tvVinNumber)
            rlFascimileImage = itemView.findViewById(R.id.rl_facsimile)
            txtPrint = itemView.findViewById(R.id.txt_print)
            txtSanctionType = itemView.findViewById(R.id.tv_sanction_type)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(rlRowMain: LinearLayoutCompat?, mStatus: Boolean, position: Int)
    }
}