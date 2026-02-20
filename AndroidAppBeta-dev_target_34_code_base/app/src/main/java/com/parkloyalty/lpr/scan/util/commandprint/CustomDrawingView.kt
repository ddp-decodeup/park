//package com.parkloyalty.lpr.scan.util.commandprint
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.graphics.RectF
//import android.view.View
//import com.parkloyalty.lpr.scan.extensions.nullSafety
//
//@SuppressLint("ViewConstructor")
//class CustomDrawingView(context: Context, val elements: List<DrawableElement>) : View(context) {
//
//    private val fillPaint = Paint().apply { style = Paint.Style.FILL }
//    private val borderPaint = Paint().apply { style = Paint.Style.STROKE }
//    private val textPaint = Paint().apply { isAntiAlias = true }
//
//    @SuppressLint("DrawAllocation")
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        for (element in elements) {
//            when (element) {
//                is DrawableElement.Rectangle -> {
//                    fillPaint.color = element.fillColor
//                    borderPaint.color = element.borderColor
//                    borderPaint.strokeWidth = element.borderWidth.nullSafety().toFloat()
//
//                    val rect = RectF(
//                        element.x.nullSafety().toFloat(),
//                        element.y.nullSafety().toFloat(),
//                        element.xEnd.nullSafety().toFloat() ,
//                        element.yEnd.nullSafety().toFloat()
//                    )
//                    canvas.drawRect(rect, fillPaint)
//                    canvas.drawRect(rect, borderPaint)
//                }
//
//                is DrawableElement.Text -> {
//                    textPaint.color = element.textColor
//                    textPaint.textSize = element.textSize.nullSafety().toFloat()
//                    canvas.drawText(element.text, element.x.nullSafety().toFloat(), element.y.nullSafety().toFloat(), textPaint)
//                }
//
//                is DrawableElement.Circle -> {
//                    fillPaint.color = element.fillColor
//                    borderPaint.color = element.borderColor
//                    borderPaint.strokeWidth = element.borderWidth
//
//                    canvas.drawCircle(element.centerX, element.centerY, element.radius, fillPaint)
//                    canvas.drawCircle(element.centerX, element.centerY, element.radius, borderPaint)
//                }
//
//                is DrawableElement.Image -> {
//                    val dstRect = RectF(
//                        element.x, element.y,
//                        element.x + element.width,
//                        element.y + element.height
//                    )
//                    canvas.drawBitmap(element.bitmap, null, dstRect, null)
//                }
//
//                is DrawableElement.Line -> {
//                    paint.style = Paint.Style.STROKE
//                    paint.color = element.color
//                    paint.strokeWidth = element.strokeWidth
//                    canvas.drawLine(element.startX, element.startY, element.endX, element.endY, paint)
//                }
//
//            }
//        }
//    }
//}
