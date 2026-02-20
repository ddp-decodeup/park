package com.parkloyalty.lpr.scan.utils.custom_views.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import com.parkloyalty.lpr.scan.utils.custom_views.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState

internal interface Shape {
    fun setDrawableState(newDrawableState: NeumorphShapeDrawableState)
    fun draw(canvas: Canvas, outlinePath: Path)
    fun updateShadowBitmap(bounds: Rect)
}
