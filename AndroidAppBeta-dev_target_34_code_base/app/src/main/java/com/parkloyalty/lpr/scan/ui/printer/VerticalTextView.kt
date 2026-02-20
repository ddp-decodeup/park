package com.parkloyalty.lpr.scan.ui.printer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class VerticalTextView : AppCompatTextView {
    private var _width: Int = 0
    private var _height: Int = 0
    private val _bounds: Rect = Rect()

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        (context)!!, attrs, defStyle
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super((context)!!, attrs) {}
    constructor(context: Context?) : super((context)!!) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // vise versa
        _height = getMeasuredWidth()
        _width = getMeasuredHeight()
        setMeasuredDimension(_width, _height)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(_width.toFloat(), _height.toFloat())
        canvas.rotate(-90f)
        val paint: TextPaint = getPaint()
        paint.setColor(getTextColors().getDefaultColor())
        val text: String = text()
        paint.getTextBounds(text, 0, text.length, _bounds)
        canvas.drawText(
            text,
            getCompoundPaddingRight().toFloat(),
            ((_bounds.height() - _width) / 2).toFloat(),
            paint
        )
        canvas.restore()
    }

    private fun text(): String {
        return super.getText().toString()
    }
}