package com.parkloyalty.lpr.scan.ui.check_setup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.ActivityCountData
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter : RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    var listItemSelectListener: ListItemSelectListener? = null
    private var mListData: List<ActivityCountData>? = null
    private var mContext: Context
    private var mDate: String? = null

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    constructor(
        context: Context,
        listData: List<ActivityCountData>?,
        itemClickListener: ListItemSelectListener?
    ) {
        mListData = listData
        mContext = context
        listItemSelectListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_recycler_activity, parent, false)
        return ViewHolder(listItem)
    }

    fun updateList(list: List<ActivityCountData>?) {
        mListData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mListData != null) {
            holder.mActivityName.text = mListData!![position].activityName
            holder.mTimeStamp.text = splitDate(mListData!![position].clientTimestamp)
        }

        /*holder.mActivityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemSelectListener.onItemClick(mListData.get(position));
            }
        });*/
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

    fun splitDate(date: String?): String? {
        return try {
            val separated = date!!.split("T").toTypedArray()
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
        var mTimeStamp: AppCompatTextView
        var mActivityName: AppCompatTextView

        init {
            mActivityName = itemView.findViewById(R.id.tvActivityName)
            mTimeStamp = itemView.findViewById(R.id.tvTimeStamp)
        }
    }

    interface ListItemSelectListener {
        fun onItemClick(mData: ActivityCountData?)
    }
}