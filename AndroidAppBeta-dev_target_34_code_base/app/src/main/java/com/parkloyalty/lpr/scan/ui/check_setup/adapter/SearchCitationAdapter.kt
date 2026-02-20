package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SPACE_WITH_COMMA
import com.parkloyalty.lpr.scan.interfaces.LookUpCitationInterfaces
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.Datum
import com.parkloyalty.lpr.scan.ui.ticket.SearchActivity
import com.parkloyalty.lpr.scan.ui.ticket.TicketDetailsActivity
import com.parkloyalty.lpr.scan.ui.ticket.ViewFacsimileImageActivity
import com.parkloyalty.lpr.scan.ui.ticket.fragment.CitationFragment
import com.parkloyalty.lpr.scan.util.AppUtils.geLookDateFormat
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLPR
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateWelcome
import java.text.SimpleDateFormat
import java.util.*


class SearchCitationAdapter : RecyclerView.Adapter<SearchCitationAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<Datum>? = null
    private var mContext: Context
    private var mDate: String? = null
    var citationInterface: LookUpCitationInterfaces? = null

    constructor(
        context: Context,
        listData: List<Datum>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
        citationInterface = (mContext as SearchActivity?)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_lpr_history_list, parent, false)
        return ViewHolder(listItem)
    }

    fun updateList(list: List<Datum>?) {
        mListData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (mListData != null) { //2021-08-20T06:27:56Z
            val itemData : Datum  = mListData!![position]
            var code: String? = ""
            var desr = ""
            var fine = ""
            var lateFine = ""
            var due15 = ""
            var due30 = ""
            var due45 = ""
            var lot: String? = ""
            var street = ""
            var block: String? = ""
            var direction = ""
            var side = ""
            var meter = ""
            if (!TextUtils.isEmpty(itemData!!.violationDetails!!.code)) {
                code = itemData!!.violationDetails!!.code
            }
            if (!TextUtils.isEmpty(itemData!!.violationDetails!!.description)) {
                desr = SPACE_WITH_COMMA + itemData!!.violationDetails!!.description
            }
            try {
                if (itemData!!.violationDetails!!.fine != 0.0) {
                    fine = SPACE_WITH_COMMA+"Fine:" + itemData!!.violationDetails!!.fine
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (itemData!!.violationDetails!!.late_fine != 0.0) {
                    lateFine = SPACE_WITH_COMMA+"Late fine:" + itemData!!.violationDetails!!.late_fine
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (itemData!!.violationDetails!!.due_15_days != 0.0) {
                    due15 = SPACE_WITH_COMMA+"Due 15 days:" + itemData!!.violationDetails!!.due_15_days
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (itemData!!.violationDetails!!.due_30_days != 0.0) {
                    due30 = SPACE_WITH_COMMA+"Due 30 days:" + itemData!!.violationDetails!!.due_30_days
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (itemData!!.violationDetails!!.due_45_days != 0.0) {
                    due45 = SPACE_WITH_COMMA+"Due 45 days:" + itemData!!.violationDetails!!.due_45_days
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (!TextUtils.isEmpty(itemData!!.location!!.lot)) {
                lot = itemData!!.location!!.lot
            }
            if (!TextUtils.isEmpty(itemData!!.location!!.street)) {
                if (itemData!!.location!!.street != "null") {
                    street = SPACE_WITH_COMMA + itemData!!.location!!.street
                }
            }
            if (!TextUtils.isEmpty(itemData!!.location!!.block)) {
                block = itemData!!.location!!.block
            }
            if (!TextUtils.isEmpty(itemData!!.location!!.direction)) {
                direction = SPACE_WITH_COMMA+"Direction:" + itemData!!.location!!.direction
            }
            if (!TextUtils.isEmpty(itemData!!.location!!.side)) {
                side = SPACE_WITH_COMMA+"Side:" + itemData!!.location!!.side
            }
            if (!TextUtils.isEmpty(itemData!!.location!!.meter)) {
                meter = SPACE_WITH_COMMA+"Meter" + itemData!!.location!!.meter
            }
            holder.mViolation.text = code + desr + fine + lateFine + due15 + due30 + due45 //
            //            holder.mTicketAddress.setText(lot+street+block+direction+side+meter);
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
                ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
                ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
                ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
                ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
                ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
                ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
                ||BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true)
                || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true)){
                holder.mTicketAddress.text = if(lot!!.isEmpty()) "" else  {lot+SPACE_WITH_COMMA} +block + street + direction + if(meter!!.isEmpty()) "" else  {meter+", "}
            }else{
                holder.mTicketAddress.text = block + street
            }

            //holder.mTicketCreator.text = "#" + splitDateLPR(itemData!!.citationIssueTimestamp!!) //mListData.get(position).getTicketNo()
            try {
                holder.mTicketCreator.text = "# " + geLookDateFormat(itemData!!.headerDetails!!.timestamp) //mListData.get(position).getTicketNo()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                holder.mTicketCreator.text = "# " + splitDateLPR(itemData!!.citationIssueTimestamp!!)
            }
            holder.mTicketNum.text = itemData!!.ticketNo
            //holder.mViolation.setText(mListData.get(position).getViolationDetails().getCode());
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,true)){
                holder.mTicketType.text = itemData!!.ticketType!!.replace("Warning", "Interruption")
            }else{
                holder.mTicketType.text = itemData!!.ticketType
            }
            holder.tvTicketDriveOff.text = if (itemData!!.isDriveOff) "Yes" else "No"+"/" + if (itemData!!.isTvr) "Yes" else "No"
            holder.mTvTicketStatus.text = itemData!!.status
            holder.tvLprNumber.text = itemData!!.lpNumber.toString()
            //holder.mTicketAddress.setText(mListData.get(position).getLocation().getBlock()+", "+mListData.get(position).getLocation().getStreet());
            holder.mLin.setBackgroundResource(R.drawable.round_corner_shape_border_grey_fill_gray)
            holder.layHide.visibility = View.VISIBLE

            if (itemData!!.location != null && itemData!!.location!!.spaceId != null && !TextUtils.isEmpty(
                            itemData!!.location!!.spaceId)) {
                holder.rlSpace.visibility = View.VISIBLE
                holder.tvSpace.text = itemData!!.location!!.spaceId
            }else{
                holder.rlSpace.visibility = View.GONE
            }

            if (itemData!!.vehicleDetails != null && itemData!!.vehicleDetails!!.vin_number != null && !TextUtils.isEmpty(
                            itemData!!.vehicleDetails!!.vin_number)) {
                holder.rlVinNumber.visibility = View.VISIBLE
                holder.tvVinNumber.text = itemData!!.vehicleDetails!!.vin_number
            }else{
                holder.rlVinNumber.visibility = View.GONE
            }

            if (itemData!!.commentDetails != null && itemData!!.commentDetails!!.remark_1 != null && !TextUtils.isEmpty(
                    itemData!!.commentDetails!!.remark_1)) {
                holder.rlRemark1.visibility = View.VISIBLE
                holder.tvRemark1.text = itemData!!.commentDetails!!.remark_1
            }else{
                holder.rlRemark1.visibility = View.GONE
            }

            if (itemData!!.commentDetails != null && itemData!!.commentDetails!!.remark_2 != null && !TextUtils.isEmpty(
                    itemData!!.commentDetails!!.remark_2)) {
                holder.rlRemark2.visibility = View.VISIBLE
                holder.tvRemark2.text = itemData!!.commentDetails!!.remark_2
            }else{
                holder.rlRemark2.visibility = View.GONE
            }

            if (itemData!!.commentDetails != null && itemData!!.commentDetails!!.note_1 != null && !TextUtils.isEmpty(
                    itemData!!.commentDetails!!.note_1)) {
                holder.rlNote1.visibility = View.VISIBLE
                holder.tvNote1.text = itemData!!.commentDetails!!.note_1
            }else{
                holder.rlNote1.visibility = View.GONE
            }

            if (itemData!!.commentDetails != null && itemData!!.commentDetails!!.note_2 != null && !TextUtils.isEmpty(
                    itemData!!.commentDetails!!.note_2)) {
                holder.rlNote2.visibility = View.VISIBLE
                holder.tvNote2.text = itemData!!.commentDetails!!.note_2
            }else{
                holder.rlNote2.visibility = View.GONE
            }

            if (itemData!!.vehicleDetails != null && itemData!!.vehicleDetails!!.mLicenseExpiry != null && !TextUtils.isEmpty(
                    itemData!!.vehicleDetails!!.mLicenseExpiry)) {
                holder.rlExpire.visibility = View.VISIBLE
                holder.tvExpire.text = itemData!!.vehicleDetails!!.mLicenseExpiry
            }else{
                holder.rlExpire.visibility = View.GONE
            }

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true) && itemData!!.auditTrail != null &&
                itemData!!.auditTrail!!.size>0 && itemData!!.auditTrail!!.get(0)!!.timestamp != null && !TextUtils.isEmpty(
                    itemData!!.auditTrail!!.get(0)!!.timestamp) && (itemData!!.auditTrail!!.get(0)!!.newValue!!.equals("Cancelled")
                            ||itemData!!.auditTrail!!.get(0)!!.newValue!!.equals("VoidAndReissue")||
                            itemData!!.auditTrail!!.get(0)!!.newValue!!.equals("Rescind"))||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) && itemData!!.auditTrail != null &&
                itemData!!.auditTrail!!.size>0 && itemData!!.auditTrail!!.get(0)!!.timestamp != null && !TextUtils.isEmpty(
                    itemData!!.auditTrail!!.get(0)!!.timestamp) && (itemData!!.auditTrail!!.get(0)!!.newValue!!.equals("Cancelled")
                            ||itemData!!.auditTrail!!.get(0)!!.newValue!!.equals("VoidAndReissue")||
                            itemData!!.auditTrail!!.get(0)!!.newValue!!.equals("Rescind"))) {
                holder.rlCancelTime.visibility = View.VISIBLE
                holder.tvCancelTime.text = splitDateWelcome(itemData!!.auditTrail!!.get(0)!!.timestamp,"CST")
            }else{
                holder.rlCancelTime.visibility = View.GONE
            }

            if (itemData!!.uploadStatus == 1) {
                holder.imgUpload.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_upload_fail))
            } else {
                holder.imgUpload.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_upload_done))
            }
        }
        holder.imgUpload.setOnClickListener{
            try {
               if (mListData != null && mListData!!.size > 0 &&
                                mListData!![position].uploadStatus == 1) {
                   citationInterface!!.onCitationData(mListData!![position])
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        holder.mLLMoveToTicket.setOnClickListener {
            try {
                if (mListData != null && mListData!!.size > 0 &&
                    !mListData!![position].status.equals("pending", ignoreCase = true)
                ) {
                    listItemSelectListener!!.onItemClick(mListData!![position])
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        holder.rlFascimileImage.visibility = View.VISIBLE
        holder.rlFascimileImage.setOnClickListener {
            try {
//                if (mListData != null && mListData!!.size > 0 &&
//                    !mListData!![position].status.equals("pending", ignoreCase = true)
//                ) {
//                    listItemSelectListener!!.onItemClick(mListData!![position])
//                }
                val mIntent = Intent(mContext, ViewFacsimileImageActivity::class.java)
                var print_bitmap: String? = ""
                if (mListData!![position]?.images?.size!! > 0) {
                    mIntent.putExtra("image_size", mListData!![position]?.images?.size)
                    for (i in mListData!![position]?.images?.indices!!) {
                        if (mListData!![position]?.images!![i].contains(FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
                            print_bitmap = mListData!![position]?.images!![i]
                            break
                        }
                    }
                }


                mIntent.putExtra("ticket_number", mListData!![position]?.ticketNo.toString())
                mIntent.putExtra("print_bitmap", print_bitmap)
                mContext.startActivity(mIntent)
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
        return try {
            val separated = hours.split(":").toTypedArray()
            val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
            dateConvert(`val`)
        } catch (e: Exception) {
            ""
        }
    }

    fun splitDate(date: String): String? {
        return try {
            val separated = date.split("T").toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            getHours(`val`)
        } catch (e: Exception) {
            ""
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
        var tvTicketDriveOff: AppCompatTextView
        var mLin: LinearLayoutCompat
        var mLLMoveToTicket: LinearLayoutCompat
        var layHide: LinearLayout
        var imgUpload: AppCompatImageView
        var rlRemark1: RelativeLayout
        var rlRemark2: RelativeLayout
        var rlNote1: RelativeLayout
        var rlNote2: RelativeLayout
        var rlExpire: RelativeLayout
        var rlSpace: RelativeLayout
        var rlFascimileImage: RelativeLayout
        var rlCancelTime: RelativeLayout
        var rlVinNumber: RelativeLayout
        var tvRemark1: AppCompatTextView
        var tvRemark2: AppCompatTextView
        var tvNote1: AppCompatTextView
        var tvNote2: AppCompatTextView
        var tvSpace: AppCompatTextView
        var tvVinNumber: AppCompatTextView
        var tvExpire: AppCompatTextView
        var tvCancelTime: AppCompatTextView

        init {
            mTicketNum = itemView.findViewById(R.id.tvTicketNumber)
            mTicketCreator = itemView.findViewById(R.id.tvTicketNum)
            mTicketAddress = itemView.findViewById(R.id.tvAddressValue)
            mViolation = itemView.findViewById(R.id.tvViolation)
            mTicketType = itemView.findViewById(R.id.tvTicketType)
            tvTicketDriveOff = itemView.findViewById(R.id.tvTicketDriveOff)
            mTvTicketStatus = itemView.findViewById(R.id.tvTicketStatus)
            tvLprNumber = itemView.findViewById(R.id.tvLprNumber)
            mLin = itemView.findViewById(R.id.layRowCitation)
            mLLMoveToTicket = itemView.findViewById(R.id.ll_ticket_move)
            layHide = itemView.findViewById(R.id.layHide)
            imgUpload = itemView.findViewById(R.id.imgUpload)
            rlRemark1 = itemView.findViewById(R.id.rl_remark_1)
            rlRemark2 = itemView.findViewById(R.id.rl_remark_2)
            rlNote1 = itemView.findViewById(R.id.rl_note_1)
            rlNote2 = itemView.findViewById(R.id.rl_note_2)
            rlSpace = itemView.findViewById(R.id.rl_space)
            rlFascimileImage = itemView.findViewById(R.id.rl_facsimile)
            rlExpire = itemView.findViewById(R.id.rl_expire)
            rlVinNumber = itemView.findViewById(R.id.rl_vin_number)
            tvRemark1 = itemView.findViewById(R.id.tv_remark1)
            tvRemark2 = itemView.findViewById(R.id.tv_remark2)
            tvNote1 = itemView.findViewById(R.id.tv_note1)
            tvNote2 = itemView.findViewById(R.id.tv_note2)
            tvSpace = itemView.findViewById(R.id.tvSpace)
            tvVinNumber = itemView.findViewById(R.id.tvVinNumber)
            tvExpire = itemView.findViewById(R.id.tvExpire)
            rlCancelTime = itemView.findViewById(R.id.rl_cancel_time)
            tvCancelTime = itemView.findViewById(R.id.tv_cancel_time)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(mData: Datum?)
    }
}