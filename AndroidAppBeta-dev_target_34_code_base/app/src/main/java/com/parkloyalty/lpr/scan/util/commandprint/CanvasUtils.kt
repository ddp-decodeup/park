package com.parkloyalty.lpr.scan.util.commandprint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withTranslation
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils.getZebraPrinterPaperWidthSize
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.fasterxml.jackson.annotation.JsonIgnore

object CanvasUtils {
    fun drawElementsToBitmapAutoSize(
        context: Context,
        elements: List<DrawableElement>,
        padding: Int = 10
    ): Bitmap {
//        var maxWidth = 576f
//        var maxHeight = 0f
//
//        // Step 1: Calculate required canvas size
//        for (element in elements) {
//            LogUtil.printLog("==>PrintElement:", ObjectMapperProvider.instance.writeValueAsString(element))
//            LogUtil.printLog("==>PrintElement:", "================")
//
//            when (element) {
//                is DrawableElement.Rectangle -> {
//                    //maxWidth = maxOf(maxWidth, element.startX.nullSafety().toFloat() + element.endX.nullSafety().toFloat())
//                    //maxHeight = maxOf(maxHeight, element.startY.nullSafety().toFloat() + element.endY.nullSafety().toFloat())
//                    maxHeight = maxOf(maxHeight, element.endY.nullSafety().toFloat())
//                }
//
//                is DrawableElement.Text -> {
//                    // Estimate text bounds (simplified)
//                    //maxWidth = maxOf(maxWidth, element.x.nullSafety().toFloat() + element.text.length.nullSafety().toFloat() * element.textSize.nullSafety().toFloat() * 0.6f)
//
//                    //maxHeight = maxOf(maxHeight, element.y.nullSafety().toFloat() + element.textSize.nullSafety().toFloat())
//                    maxHeight = maxOf(
//                        maxHeight,
//                        element.y.nullSafety().toFloat() + element.textSize.nullSafety().toFloat()
//                    )
//                }
//
//                is DrawableElement.Circle -> {
//                    //maxWidth = maxOf(maxWidth, element.centerX + element.radius)
//                    maxHeight = maxOf(maxHeight, element.centerY + element.radius)
//                }
//
//                is DrawableElement.Image -> {
//                    //maxWidth = maxOf(maxWidth, element.x + element.width)
//                    maxHeight = maxOf(maxHeight, element.y + element.height)
//                }
//
//                is DrawableElement.Line -> {
//                    //maxWidth = maxOf(maxWidth, element.startX, element.endX)
//                    maxHeight = maxOf(maxHeight, element.startY, element.endY)
//                }
//            }
//        }
//
//        val width = maxWidth.toInt()
//        //val width = (maxWidth + padding).toInt()
//        val height = (maxHeight + padding).toInt() + 500


        val maxX = getZebraPrinterPaperWidthSize().toFloat()
        var maxY = 0f
        val tempPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        for (element in elements) {
            LogUtil.printLog("==>PrintElement:", ObjectMapperProvider.instance.writeValueAsString(element))
            LogUtil.printLog("==>PrintElement:", "================")

            when (element) {
                is DrawableElement.Text -> {
                    tempPaint.textSize = element.textSize.nullSafety().toFloat()
                    val bounds = Rect()
                    if (element.isVertical) {
                        for (char in element.text) {
                            tempPaint.getTextBounds(char.toString(), 0, 1, bounds)
                            maxY = maxOf(
                                maxY,
                                element.y.toFloat() + (element.text.length * (tempPaint.textSize)) + 50
                            )
                        }
                        //maxX = maxOf(maxX, element.x.toFloat() + bounds.width() + 10)
                    } else {
                        tempPaint.getTextBounds(element.text, 0, element.text.length, bounds)
                        //maxX = maxOf(maxX, element.x.toFloat() + bounds.width() + 10)
                        maxY = maxOf(maxY, element.y.toFloat() + bounds.height() + 50)
                    }
                }

                is DrawableElement.Rectangle -> {
                    //maxX = maxOf(maxX, element.startX + element.width)
                    //maxY = maxOf(maxY, element.startY + element.height)
                    maxY = maxOf(
                        maxY,
                        element.startY.toFloat() + (element.endY - element.startY).toFloat()
                    )
                }

                is DrawableElement.Circle -> {
                    //maxX = maxOf(maxX, element.centerX + element.radius)
                    maxY = maxOf(maxY, element.centerY + element.radius)
                }

                is DrawableElement.Line -> {
                    //maxX = maxOf(maxX, element.endX)
                    maxY = maxOf(maxY, element.endY)
                }

                is DrawableElement.Image -> {
                    // maxX = maxOf(maxX, element.x + element.bitmap.width)
                    maxY = maxOf(maxY, element.y + element.bitmap.height)
                }
            }
        }

        //return drawElementsToBitmap(context, width, height, elements)
        return drawElementsToBitmap(
            context,
            maxX.nullSafety().toInt(),
            maxY.nullSafety().toInt(),
            elements
        )

    }


    fun drawElementsToBitmap(
        context: Context,
        width: Int,
        height: Int,
        elements: List<DrawableElement>
    ): Bitmap {
        val padding: Int = 10
        //val bitmap = createBitmap(width, height)
        val bitmap = createBitmap((width + 20).toInt(), (height + 20).toInt())

        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.WHITE)

        canvas.translate(10f, 10f)

        val paint = Paint().apply { isAntiAlias = true }

        for (element in elements) {
            when (element) {
                is DrawableElement.Rectangle -> {
                    val rect = RectF(
                        element.startX.nullSafety().toFloat(),
                        element.startY.nullSafety().toFloat(),
                        element.endX.nullSafety().toFloat(),
                        element.endY.nullSafety().toFloat()
                    )

                    // Fill
                    paint.style = Paint.Style.FILL
                    paint.color = element.fillColor
                    canvas.drawRect(rect, paint)

                    // Border
                    paint.style = Paint.Style.STROKE
                    paint.color = element.borderColor
                    paint.strokeWidth = element.borderWidth.nullSafety().toFloat()
                    canvas.drawRect(rect, paint)
                }

                is DrawableElement.Text -> {
                    paint.style = Paint.Style.FILL
                    paint.color = element.textColor

                    if (element.textFont == 0 && element.textSize == 0) {
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 14f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 0 && element.textSize == 1) {
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 18f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 0 && element.textSize == 2) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 22f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 0 && element.textSize == 3) {
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 24f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 0 && element.textSize == 4) {
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 26f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 0 && element.textSize == 5) {
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 30f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 0 && element.textSize == 6) {
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 34f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 1 && element.textSize == 0) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_1)
                        paint.textSize = 34f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 2 && element.textSize == 0) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_2)
                        paint.textSize = 26f
                        paint.letterSpacing = 0.20f
                    } else if (element.textFont == 2 && element.textSize == 1) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_2)
                        paint.textSize = 26f
                        paint.letterSpacing = 0.20f
                    } else if (element.textFont == 2 && element.textSize == 2) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_2)
                        paint.textSize = 26f
                        paint.letterSpacing = 0.20f
                    } else if (element.textFont == 4 && element.textSize == 0) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_4)
                        paint.textSize = 20f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 4 && element.textSize == 1) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_4)
                        paint.textSize = 24f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 4 && element.textSize == 2) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_4)
                        paint.textSize = 24f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 4 && element.textSize == 3) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_4)
                        paint.textSize = 34f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 4 && element.textSize == 4) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_4)
                        paint.textSize = 50f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 4 && element.textSize == 5) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_4)
                        paint.textSize = 70f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 4 && element.textSize == 6) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_4)
                        paint.textSize = 90f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 4 && element.textSize == 7) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_4)
                        paint.textSize = 110f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 5 && element.textSize == 0) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_a_1)
                        paint.textSize = 17f
                        paint.letterSpacing = 0.20f
                    } else if (element.textFont == 5 && element.textSize == 1) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_5_for_high_2)
                        paint.textSize = 34f
                        paint.letterSpacing = 0.07f
                    } else if (element.textFont == 5 && element.textSize == 2) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_5_big)
                        paint.textSize = 32f
                        paint.letterSpacing = 0.10f
                    } else if (element.textFont == 5 && element.textSize == 3) {
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_5_big)
                        paint.textSize = 32f
                        paint.letterSpacing = 0.10f
                    } else if (element.textFont == 5 && element.textSize == 4) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_5_4)
                        paint.textSize = 47f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 6 && element.textSize == 0) {
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_6)
                        paint.textSize = 10f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 7 && element.textSize == 0) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface = ResourcesCompat.getFont(context, R.font.zebra_font_7_bold)
                        paint.textSize = 20f
                        paint.letterSpacing = 0f
                    } else if (element.textFont == 7 && element.textSize == 1) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 35f
                        paint.letterSpacing = 0f
                        //paint.letterSpacing = -0.05f
                    } else if (element.textFont == 7 && element.textSize == 2) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 35f
                        paint.letterSpacing = 0f
                        //paint.letterSpacing = -0.05f
                    } else if (element.textFont == 7 && element.textSize == 3) {
                        //TESTED, NO NEED TO CHANGE
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 35f
                        paint.letterSpacing = 0f
                        //paint.letterSpacing = -0.05f
                    } else {
                        paint.typeface =
                            ResourcesCompat.getFont(context, R.font.zebra_font_0_con_med)
                        paint.textSize = 35f
                        paint.letterSpacing = 0f
                    }

                    val fm = paint.fontMetrics

                    if (element.isVertical) {
                        // Draw text vertically, character by character
                        val y = element.y
//                        for (char in element.text) {
//                            val bounds = Rect()
//                            paint.getTextBounds(char.toString(), 0, 1, bounds)
//                            val adjustedY = y - bounds.top  // top-left alignment
//                            canvas.drawText(
//                                char.toString(),
//                                element.x.nullSafety().toFloat(),
//                                adjustedY.nullSafety().toFloat(),
//                                paint
//                            )
//                            y += paint.textSize  // adjust vertical spacing
//
//                        }

                        var offsetY = 0f
                        for (char in element.text) {
                            val bounds = Rect()
                            paint.getTextBounds(char.toString(), 0, 1, bounds)
                            val adjustedY = y - bounds.top  // top-left alignment

                            canvas.withTranslation(
                                element.x.toFloat(),
                                (adjustedY - offsetY).toFloat()
                            ) {
                                rotate(270f)
                                drawText(char.toString(), 0f, 0f - paint.ascent(), paint)
                                //canvas.drawText(char.toString(), element.x.nullSafety().toFloat(), element.y.nullSafety().toFloat() - paint.ascent(), paint)
                            }
                            offsetY += paint.measureText(char.toString())
                        }

                    } else {
                        //val finalText = element.text.repeatLeadingWhitespaceOneAndHalf()
                        val finalText = element.text

                        val bounds = Rect()
                        paint.getTextBounds(finalText, 0, finalText.length, bounds)
                        val adjustedY = element.y - bounds.top

                        val baselineY = element.y - fm.ascent
                        canvas.drawText(
                            finalText,
                            element.x.nullSafety().toFloat(),
                            baselineY.toFloat(),
                            paint
                        )

//                        canvas.drawText(
//                            finalText,
//                            element.x.nullSafety().toFloat(),
//                            adjustedY.nullSafety().toFloat(),
//                            paint
//                        )
                    }
                }

                is DrawableElement.Circle -> {
                    paint.style = Paint.Style.FILL
                    paint.color = element.fillColor
                    canvas.drawCircle(element.centerX, element.centerY, element.radius, paint)
                }

                is DrawableElement.Image -> {
                    val destRect = RectF(
                        element.x,
                        element.y,
                        element.x + element.width,
                        element.y + element.height
                    )
                    canvas.drawBitmap(element.bitmap, null, destRect, null)
                }

                is DrawableElement.Line -> {
                    paint.style = Paint.Style.STROKE
                    paint.color = element.color
                    paint.strokeWidth = element.strokeWidth
                    canvas.drawLine(
                        element.startX,
                        element.startY,
                        element.endX,
                        element.endY,
                        paint
                    )
                }
            }
        }

        //canvas.translate(10f, 10f)

        return bitmap
    }
}