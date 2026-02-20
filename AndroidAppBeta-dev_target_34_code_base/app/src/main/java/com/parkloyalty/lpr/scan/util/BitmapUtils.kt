package com.parkloyalty.lpr.scan.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import java.io.File
import java.util.ArrayList

object BitmapUtils {
    fun getBitmapImageFromFile(file: File): Bitmap? {
        var mImgaeBitmap: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, options)

            // Calculate inSampleSize
            options.inSampleSize = Util.calculateInSampleSize(options, 400, 400)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            val scaledBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

            //check the rotation of the image and display it properly
            val exif: ExifInterface = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            when (orientation) {
                6 -> {
                    matrix.postRotate(90f)
                }

                3 -> {
                    matrix.postRotate(180f)
                }

                8 -> {
                    matrix.postRotate(270f)
                }
            }
            mImgaeBitmap = Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
            return mImgaeBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return mImgaeBitmap
        }
    }

    /**
     * Function used to take colored bitmap as input and returns pixel by pixel black and white bitmap
     */
    fun convertBitmapToBWUsingPixelToPixelConversion(bmp: Bitmap): Bitmap {
        val width = bmp.width
        val height = bmp.height
        val pixels = IntArray(width * height)
        bmp.getPixels(pixels, 0, width, 0, 0, width, height)

        val alpha = 0xFF shl 24 // ?bitmap?24?
        for (i in 0 until height) {
            for (j in 0 until width) {
                var grey = pixels[width * i + j]

                val red = ((grey and 0x00FF0000) shr 16)
                val green = ((grey and 0x0000FF00) shr 8)
                val blue = (grey and 0x000000FF)

                grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
                grey = alpha or (grey shl 16) or (grey shl 8) or grey
                pixels[width * i + j] = grey
            }
        }
        val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBmp
    }

    /**
     * Function used to take colored bitmap as input and returns black and white image by default bitmap method
     */
    fun convertBitmapToBWUsingDefaultARGB(bmp: Bitmap): Bitmap {
        return bmp.copy(Bitmap.Config.ARGB_8888, true)
    }


    /**
     * Function used to scale bitmap considering aspect ratio
     */
    fun scale(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        // Determine the constrained dimension, which determines both dimensions.
        val width: Int
        val height: Int
        val widthRatio = bitmap.width.toFloat() / maxWidth
        val heightRatio = bitmap.height.toFloat() / maxHeight
        // Width constrained.
        if (widthRatio >= heightRatio) {
            width = maxWidth
            height = ((width.toFloat() / bitmap.width) * bitmap.height).toInt()
        } else {
            height = maxHeight
            width = ((height.toFloat() / bitmap.height) * bitmap.width).toInt()
        }
        val scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val ratioX = width.toFloat() / bitmap.width
        val ratioY = height.toFloat() / bitmap.height
        val middleX = width / 2.0f
        val middleY = height / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap,
            middleX - bitmap.width / 2,
            middleY - bitmap.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        return scaledBitmap
    }

    /**
     * Function used to combine bitmap side by side either vertically or horizontally
     * @param bitmaps: List of bitmaps you want to combine
     * @param isHorizontal: How you want to stack, vertically or side by side hosrizontally
     * @return bitmap : It will return final combined bitmap
     */
    @Suppress("KotlinConstantConditions")
    fun combineBitmapsSideBySide(bitmaps: ArrayList<Bitmap>, isHorizontal: Boolean): Bitmap {
        var w = 0
        var h = 0
        for (i in bitmaps.indices) {
            if (i == 0 || isHorizontal) {
                w += bitmaps[i].width
            } else if (!isHorizontal && bitmaps[i].width > w) {
                w = bitmaps[i].width
            }
            if (i == 0 || !isHorizontal) {
                h += bitmaps[i].height
            } else if (isHorizontal && bitmaps[i].height > h) {
                h = bitmaps[i].height
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        var pos = 0
        for (i in bitmaps.indices) {
            if (isHorizontal) {
                canvas.drawBitmap(bitmaps[i], pos.toFloat(), 0f, null)
            } else {
                canvas.drawBitmap(bitmaps[i], 0f, pos.toFloat(), null)
            }
            pos += if (isHorizontal) bitmaps[i].width else bitmaps[i].height
        }

        //return Bitmap.createScaledBitmap(bitmap, 500, 500, false)
        return bitmap
    }
}