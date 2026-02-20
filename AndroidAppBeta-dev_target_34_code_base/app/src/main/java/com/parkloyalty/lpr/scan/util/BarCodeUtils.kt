package com.parkloyalty.lpr.scan.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.Writer
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.Hashtable
import androidx.core.graphics.set

object BarCodeUtils {

    /**
     * This method is used to generate barcode bitmap for print
     *
     * @param ticketNumber : Ticket number for which barcode need to generate
     * @param width        : Width of barcode
     * @param height       : Height of barcode
     * @return : Bitmap of barcode
     */
    fun generateBarCodeForPrint(ticketNumber: String, width: Int, height: Int): Bitmap {
        val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel?>()
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        val codeWriter = Code128Writer()
        var byteMatrix: BitMatrix? = null
        byteMatrix = codeWriter.encode(ticketNumber, BarcodeFormat.CODE_128, width, height, hintMap)

        val width = byteMatrix.width
        val height = byteMatrix.height
        val bitmap = createBitmap(width, height)
        for (i in 0 until width) {
            for (j in 0 until height) {
                bitmap[i, j] = if (byteMatrix[i, j]) Color.BLACK else Color.WHITE
            }
        }

        return bitmap;
    }
}