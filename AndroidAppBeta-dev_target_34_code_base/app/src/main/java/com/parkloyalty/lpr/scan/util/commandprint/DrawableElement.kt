package com.parkloyalty.lpr.scan.util.commandprint

import android.graphics.Bitmap
import com.fasterxml.jackson.annotation.JsonIgnore

sealed class DrawableElement {
    data class Rectangle(
        val startX: Double,
        val startY: Double,
        val endX: Double,
        val endY: Double,
        val fillColor: Int,
        val borderColor: Int,
        val borderWidth: Double
    ) : DrawableElement()

    data class Text(
        val x: Double,
        val y: Double,
        val text: String,
        val textColor: Int,
        val textFont: Int,
        val textSize: Int,
        val isVertical: Boolean = false // default: horizontal
    ) : DrawableElement()

    data class Circle(
        val centerX: Float,
        val centerY: Float,
        val radius: Float,
        val fillColor: Int,
        val borderColor: Int,
        val borderWidth: Float
    ) : DrawableElement()

    data class Image(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        @JsonIgnore
        val bitmap: Bitmap
    ) : DrawableElement()

    data class Line(
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val color: Int,
        val strokeWidth: Float
    ) : DrawableElement()
}
