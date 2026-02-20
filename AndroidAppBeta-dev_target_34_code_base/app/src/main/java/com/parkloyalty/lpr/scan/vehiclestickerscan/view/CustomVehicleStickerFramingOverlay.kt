package com.parkloyalty.lpr.scan.vehiclestickerscan.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Px

/**
 * Overlay that draws a green rounded "scan frame" with a red guide line.
 */
class CustomVehicleStickerFramingOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    // --- Frame size (relative to view) ---
    var relWidth = 0.86f      // 86% of view width
    var relHeight = 0.36f     // 36% of view height

    // --- Frame style ---
    var cornerRadius = 22f
    @Px
    var stroke = 6f
        set(value) { field = value; framePaint.strokeWidth = value; invalidate() }

    // --- Guide line style ---
    enum class GuideOrientation { HORIZONTAL, VERTICAL }
    var guideOrientation: GuideOrientation = GuideOrientation.HORIZONTAL
        set(value) { field = value; invalidate() }

    @Px
    var guideStroke = 4f
        set(value) { field = value; guidePaint.strokeWidth = value; invalidate() }

    var guideDashed: Boolean = true
        set(value) { field = value; guidePaint.pathEffect = dashEffect(); invalidate() }

    //offset in pixels for guide line
    @Px
    var guideLineOffset: Float = 0f
        set(value) { field = value; invalidate() }

    // colors
    var frameColor: Int = Color.parseColor("#21C26A") // green
        set(value) { field = value; framePaint.color = value; invalidate() }
    var guideColor: Int = Color.RED
        set(value) { field = value; guidePaint.color = value; invalidate() }
    var dimColor: Int = 0x77000000.toInt()
        set(value) { field = value; dimPaint.color = value; invalidate() }

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = frameColor
        strokeWidth = stroke
    }
    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = guideColor
        strokeWidth = guideStroke
        pathEffect = dashEffect()
    }
    private fun dashEffect(): PathEffect? =
        if (guideDashed) DashPathEffect(floatArrayOf(14f, 10f), 0f) else null

    private val dimPaint = Paint().apply { color = dimColor }
    private val path = Path()
    private val rect = RectF()

    /** Absolute frame rect in this view’s coordinates. */
    fun frameRect(): RectF {
        val w = width.toFloat()
        val h = height.toFloat()
        val fw = (w * relWidth).coerceAtMost(w)
        val fh = (h * relHeight).coerceAtMost(h)
        rect.set((w - fw) / 2f, (h - fh) / 2f, (w + fw) / 2f, (h + fh) / 2f)
        return rect
    }

    /** Relative rect (0..1) – use to map to camera buffers. */
    fun frameRectRelative(): RectF {
        val r = frameRect()
        return RectF(r.left / width, r.top / height, r.right / width, r.bottom / height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = frameRect()

        // 1) dim outside the frame
        path.reset()
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        path.addRoundRect(r, cornerRadius, cornerRadius, Path.Direction.CCW)
        canvas.drawPath(path, dimPaint)

        // 2) green frame
        canvas.drawRoundRect(r, cornerRadius, cornerRadius, framePaint)

        // 3) red guide line (with offset)
        val inset = stroke * 0.75f
        if (guideOrientation == GuideOrientation.HORIZONTAL) {
            val y = r.centerY() - guideLineOffset // shift up if positive
            canvas.drawLine(r.left + inset, y, r.right - inset, y, guidePaint)
        } else {
            val x = r.centerX() + guideLineOffset // shift right if positive
            canvas.drawLine(x, r.top + inset, x, r.bottom - inset, guidePaint)
        }
    }
}