package com.parkloyalty.lpr.scan.utils.custom_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Base64
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import com.parkloyalty.lpr.scan.R
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * SignaturePad with dynamic stroke width using velocity and pressure and optional border.
 */
class CustomSignaturePad @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    // --- Data classes ---
    private data class PointW(val x: Float, val y: Float, val width: Float, val time: Long)
    private data class Stroke(val points: MutableList<PointW>, val paintColor: Int)

    // --- Paint config ---
    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    // border paint
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    // attributes (defaults)
    private var penMinWidthPx = dpToPx(2f)
    private var penMaxWidthPx = dpToPx(6f)
    private var penColor = Color.BLACK
    private var bgColor: Int? = null
    private var bgBitmap: Bitmap? = null

    // border attributes
    private var borderEnabled = false
    private var borderColor = Color.LTGRAY
    private var borderWidthPx = dpToPx(1f)
    private var borderRadiusPx = 0f
    private var borderPaddingPx = dpToPx(8f) // space between border and drawing area

    // strokes
    private val strokes = mutableListOf<Stroke>()
    private val undone = mutableListOf<Stroke>()

    // current stroke
    private var currentPoints = mutableListOf<PointW>()

    // touch tracking
    private var lastX = 0f
    private var lastY = 0f
    private var lastTime = 0L

    // smoothing params
    private val velocityFilterWeight = 0.9f
    private var lastVelocity = 0f

    // tolerance
    private var touchTolerance = dpToPx(2f)

    init {
        // read attributes
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.CustomSignaturePad)
            try {
                penMaxWidthPx = a.getDimension(
                    R.styleable.CustomSignaturePad_signPenMaxWidth, penMaxWidthPx
                )
                penMinWidthPx = a.getDimension(
                    R.styleable.CustomSignaturePad_signPenMinWidth, penMinWidthPx
                )
                penColor = a.getColor(R.styleable.CustomSignaturePad_signPenColor, penColor)
                val bg = a.getColor(
                    R.styleable.CustomSignaturePad_signPadBackgroundColor, Color.TRANSPARENT
                )
                bgColor = if (bg == Color.TRANSPARENT) null else bg

                // border attrs
                borderEnabled = a.getBoolean(R.styleable.CustomSignaturePad_signBorderEnabled, borderEnabled)
                borderColor = a.getColor(R.styleable.CustomSignaturePad_signBorderColor, borderColor)
                borderWidthPx = a.getDimension(R.styleable.CustomSignaturePad_signBorderWidth, borderWidthPx)
                borderRadiusPx = a.getDimension(R.styleable.CustomSignaturePad_signBorderRadius, borderRadiusPx)
                borderPaddingPx = a.getDimension(R.styleable.CustomSignaturePad_signBorderPadding, borderPaddingPx)
            } finally {
                a.recycle()
            }
        }
        drawPaint.color = penColor

        // configure border paint
        borderPaint.color = borderColor
        borderPaint.strokeWidth = borderWidthPx
        borderPaint.style = Paint.Style.STROKE
    }

    // ---------- Public API ----------
    fun setPenMinWidthDp(dp: Float) { penMinWidthPx = dpToPx(dp) }
    fun setPenMaxWidthDp(dp: Float) { penMaxWidthPx = dpToPx(dp) }
    fun setPenColor(color: Int) { penColor = color; drawPaint.color = color; invalidate() }

    override fun setBackgroundColor(color: Int) {
        bgColor = color; bgBitmap = null; invalidate()
    }

    fun setBackgroundBitmap(bitmap: Bitmap) { bgBitmap = bitmap; bgColor = null; invalidate() }

    // border setters
    fun setBorderEnabled(enabled: Boolean) { borderEnabled = enabled; invalidate() }
    fun setBorderColor(color: Int) { borderColor = color; borderPaint.color = color; invalidate() }
    fun setBorderWidthDp(dp: Float) { borderWidthPx = dpToPx(dp); borderPaint.strokeWidth = borderWidthPx; invalidate() }
    fun setBorderRadiusDp(dp: Float) { borderRadiusPx = dpToPx(dp); invalidate() }
    fun setBorderPaddingDp(dp: Float) { borderPaddingPx = dpToPx(dp); invalidate() }

    fun clear() {
        strokes.clear()
        undone.clear()
        currentPoints.clear()
        invalidate()
    }

    fun undo(): Boolean {
        if (strokes.isEmpty()) return false
        undone.add(strokes.removeAt(strokes.size - 1))
        invalidate()
        return true
    }

    fun redo(): Boolean {
        if (undone.isEmpty()) return false
        strokes.add(undone.removeAt(undone.size - 1))
        invalidate()
        return true
    }

    fun isEmpty(): Boolean = strokes.isEmpty() && currentPoints.isEmpty()

    /**
     * Returns a bitmap of the signature cropped to bounds of strokes (with padding).
     * If includeBorder is true and borderEnabled is true, output includes border+padding.
     * Returns null if nothing drawn.
     */
    fun getSignatureBitmap(
        paddingPx: Int = 50,
        backgroundColorIfNull: Int = Color.WHITE,
        includeBorder: Boolean = false
    ): Bitmap? {
        val bounds = computeCombinedBounds() ?: return null

        val extra = penMaxWidthPx / 2f
        var left = (bounds.left - extra - paddingPx).toInt().coerceAtLeast(0)
        var top = (bounds.top - extra - paddingPx).toInt().coerceAtLeast(0)
        var right = (bounds.right + extra + paddingPx).toInt().coerceAtMost(width)
        var bottom = (bounds.bottom + extra + paddingPx).toInt().coerceAtMost(height)

        if (includeBorder && borderEnabled) {
            // expand to include border area and padding
            left = (min(left.toFloat(), borderPaddingPx + borderWidthPx).toInt()).coerceAtLeast(0)
            top = (min(top.toFloat(), borderPaddingPx + borderWidthPx).toInt()).coerceAtLeast(0)
            // but to be safe, expand edges to include the full view so border is visible
            left = 0
            top = 0
            right = width
            bottom = height
        }

        val w = max(1, right - left)
        val h = max(1, bottom - top)

        val bmp = createBitmap(w, h)
        bmp.applyCanvas {
            // draw background
            if (bgBitmap != null) {
                drawBitmap(bgBitmap!!, null, Rect(0, 0, w, h), null)
            } else {
                drawColor(bgColor ?: backgroundColorIfNull)
            }

            withSave {
                translate((-left).toFloat(), (-top).toFloat())

                // draw border (if asked to include)
                if (includeBorder && borderEnabled) {
                    val borderRect = RectF(
                        borderWidthPx / 2f,
                        borderWidthPx / 2f,
                        width - borderWidthPx / 2f,
                        height - borderWidthPx / 2f
                    )
                    if (borderRadiusPx > 0f) {
                        drawRoundRect(borderRect, borderRadiusPx, borderRadiusPx, borderPaint)
                    } else {
                        drawRect(borderRect, borderPaint)
                    }
                }

                // draw strokes
                for (s in strokes) {
                    drawStrokePoints(this, s.points, s.paintColor)
                }
                // draw current ongoing stroke
                if (currentPoints.isNotEmpty()) {
                    drawStrokePoints(this, currentPoints, penColor)
                }
            }
        }
        return bmp
    }

    private fun computeCombinedBounds(): RectF? {
        var r: RectF? = null
        fun includePoints(points: List<PointW>) {
            if (points.isEmpty()) return
            val left = points.minOf { it.x }
            val right = points.maxOf { it.x }
            val top = points.minOf { it.y }
            val bottom = points.maxOf { it.y }
            val rect = RectF(left, top, right, bottom)
            if (r == null) r = rect else r!!.union(rect)
        }

        includePoints(currentPoints)
        for (s in strokes) includePoints(s.points)
        return r
    }

    // draw list of points onto given canvas (helper)
    private fun drawStrokePoints(canvas: Canvas, points: List<PointW>, color: Int) {
        if (points.size < 2) {
            // draw single dot
            val p = points.firstOrNull() ?: return
            drawPaint.strokeWidth = p.width
            drawPaint.color = color
            canvas.drawPoint(p.x, p.y, drawPaint)
            return
        }

        // draw segments
        val localPaint = Paint(drawPaint)
        localPaint.color = color

        for (i in 1 until points.size) {
            val p0 = points[i - 1]
            val p1 = points[i]
            val strokeW = (p0.width + p1.width) / 2f
            localPaint.strokeWidth = strokeW
            // draw a line segment
            canvas.drawLine(p0.x, p0.y, p1.x, p1.y, localPaint)
        }
    }

    // ---------- Touch handling ----------
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.coerceIn(0f, width.toFloat())
        val y = event.y.coerceIn(0f, height.toFloat())
        val t = System.currentTimeMillis()

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                undone.clear()
                currentPoints = mutableListOf()
                lastX = x
                lastY = y
                lastTime = t
                lastVelocity = 0f

                // initial width: mid of min/max or use pressure if available
                val pressure = event.pressure.takeIf { it > 0f } ?: 0.5f
                val w = pressureToWidth(pressure)
                currentPoints.add(PointW(x, y, w, t))
                invalidateDirty(x, y, w)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val historySize = event.historySize
                // iterate through history for smoother capture
                for (h in 0 until historySize) {
                    val hx = event.getHistoricalX(h)
                    val hy = event.getHistoricalY(h)
                    val ht = event.getHistoricalEventTime(h)
                    addPoint(hx, hy, ht, event)
                }
                addPoint(x, y, t, event)
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // finalize stroke
                if (currentPoints.isNotEmpty()) {
                    // store stroke
                    strokes.add(Stroke(currentPoints, penColor))
                    currentPoints = mutableListOf()
                    invalidate()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun addPoint(x: Float, y: Float, t: Long, event: MotionEvent) {
        val dx = x - lastX
        val dy = y - lastY
        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val dt = max(1L, t - lastTime) // ms
        val velocity = dist / dt * 1000f // pixels per second

        // low-pass filter the velocity to smooth it
        val v = velocityFilterWeight * velocity + (1 - velocityFilterWeight) * lastVelocity
        lastVelocity = v
        lastX = x
        lastY = y
        lastTime = t

        // pressure if available (use event.pressure)
        val pressure = event.pressure.takeIf { it > 0f } ?: 0f

        // compute width using velocity & pressure: faster -> thinner, slower -> thicker
        val velocityNorm = 1f / (1f + v / 2000f) // heuristic mapping
        val pressureFactor = if (pressure > 0f) pressure else 0.5f
        val width =
            (penMinWidthPx + (penMaxWidthPx - penMinWidthPx) * (velocityNorm * 0.7f + pressureFactor * 0.3f)).coerceIn(
                penMinWidthPx, penMaxWidthPx
            )

        currentPoints.add(PointW(x, y, width, t))
        invalidateDirty(x, y, width)
    }

    // ---------- Drawing ----------
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // background
        if (bgBitmap != null) {
            canvas.drawBitmap(bgBitmap!!, null, Rect(0, 0, width, height), null)
        } else {
            bgColor?.let { canvas.drawColor(it) }
        }

        // draw border first (so strokes are above border inner area)
        if (borderEnabled) {
            val left = borderWidthPx / 2f
            val top = borderWidthPx / 2f
            val right = width - borderWidthPx / 2f
            val bottom = height - borderWidthPx / 2f
            val rect = RectF(left, top, right, bottom)
            if (borderRadiusPx > 0f) {
                canvas.drawRoundRect(rect, borderRadiusPx, borderRadiusPx, borderPaint)
            } else {
                canvas.drawRect(rect, borderPaint)
            }
        }

        // draw saved strokes
        for (s in strokes) {
            drawStrokePoints(canvas, s.points, s.paintColor)
        }

        // draw current
        if (currentPoints.isNotEmpty()) {
            drawStrokePoints(canvas, currentPoints, penColor)
        }
    }

    private fun invalidateDirty(x: Float, y: Float, width: Float) {
        val pad = (penMaxWidthPx / 2f).toInt() + 4
        invalidate((x - pad).toInt(), (y - pad).toInt(), (x + pad).toInt(), (y + pad).toInt())
    }

    // --- Export helpers ---
    fun exportToPngBytes(paddingPx: Int = 0, backgroundColorIfNull: Int = Color.WHITE): ByteArray? {
        val bmp = getSignatureBitmap(paddingPx, backgroundColorIfNull, includeBorder = false) ?: return null
        val baos = java.io.ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    fun exportToBase64(paddingPx: Int = 0, backgroundColorIfNull: Int = Color.WHITE): String? {
        val bytes = exportToPngBytes(paddingPx, backgroundColorIfNull) ?: return null
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    // --- Utils ---
    private fun dpToPx(dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

    private fun pressureToWidth(pressure: Float): Float {
        val p = pressure.coerceIn(0f, 1f)
        return (penMinWidthPx + (penMaxWidthPx - penMinWidthPx) * p).coerceIn(
            penMinWidthPx, penMaxWidthPx
        )
    }
}
