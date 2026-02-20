package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.parkloyalty.lpr.scan.R
import kotlin.math.floor

class CustomMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private var tvContent: TextView = findViewById<View>(R.id.tvContent) as TextView
    private var mOffset: MPPointF? = null

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        tvContent.text = floor(e.x.toDouble()).toString() + "," + floor(e.y.toDouble()).toString()
        // this will perform necessary layouting
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return mOffset!!
    }
}