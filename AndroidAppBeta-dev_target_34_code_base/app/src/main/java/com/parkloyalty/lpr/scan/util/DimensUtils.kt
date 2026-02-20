package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue


object DimensUtils {
    fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }
}