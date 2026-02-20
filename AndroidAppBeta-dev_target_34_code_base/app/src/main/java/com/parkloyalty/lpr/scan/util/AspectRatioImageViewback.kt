package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class AspectRatioImageViewback : AppCompatImageView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        try {
            if (drawable != null) {
                val width = MeasureSpec.getSize(widthMeasureSpec)
                val height = width * drawable.intrinsicHeight / drawable.intrinsicWidth
                setMeasuredDimension(width, height)
            } else {
                setMeasuredDimension(0, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}