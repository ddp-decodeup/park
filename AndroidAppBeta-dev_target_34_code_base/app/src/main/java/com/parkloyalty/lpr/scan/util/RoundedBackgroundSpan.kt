package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.RectF
import android.text.style.ReplacementSpan
import com.parkloyalty.lpr.scan.R

class RoundedBackgroundSpan(context: Context) : ReplacementSpan() {
    private var backgroundColor = 0
    private var textColor = 0
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val rect =
            RectF(x, top.toFloat(), x + measureText(paint, text, start, end), bottom.toFloat())
        paint.color = backgroundColor
        canvas.drawRoundRect(rect, CORNER_RADIUS.toFloat(), CORNER_RADIUS.toFloat(), paint)
        paint.color = textColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        return Math.round(paint.measureText(text, start, end))
    }

    private fun measureText(paint: Paint, text: CharSequence, start: Int, end: Int): Float {
        return paint.measureText(text, start, end)
    }

    companion object {
        private const val CORNER_RADIUS = 8
    }

    init {
        backgroundColor = context.resources.getColor(R.color.gray)
        textColor = context.resources.getColor(R.color.white)
    }
}