package com.parkloyalty.lpr.scan.camera2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.os.Environment
import android.util.Log
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Saves a JPEG [Image] into the specified [File].
 */
internal class ImageSaver(
        /**
         * The JPEG image
         */
        private val image: Image,

        /**
         * The file we save the image into.
         */
        private val file: File,private  val mDb: AppDatabase?) : Runnable {
    override fun run() {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file).apply {
                write(bytes)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        } finally {
            image.close()
            output?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())
                }
            }
        }
//VINOD MAKODE
//        val outStream: FileOutputStream
//        val fos: FileOutputStream;
        try {
            val myDir = File(
                    Environment.getExternalStorageDirectory().absolutePath,
                    Constants.FILE_NAME + Constants.CAMERA)
            myDir.mkdirs()
            val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
            val fname = "Image_" + timeStamp + "_capture.jpg"
//            val file = File(myDir, fname)

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, output!!);
//            val f = getResizedBitmap(bitmap, 1600, 2080)
            SaveImage(bitmap, fname)
//            if (f != null) {
//                SaveImage(f, fname)
//            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
//        if(width>height){
//            matrix.postScale(scaleHeight, scaleWidth)
//        }else {
            matrix.postScale(scaleWidth, scaleHeight)
//        }

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    private fun SaveImage(finalBitmap: Bitmap, imageName: String) {

        val myDir = File(
                Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.CAMERA)

        myDir.mkdirs()
        val fname = "${imageName?.trim()}" //"print_bitmap.jpg";
        val savePrintImagePath = File(myDir, fname)
        if (savePrintImagePath!!.exists()) savePrintImagePath!!.delete()
        try {

            val out = FileOutputStream(savePrintImagePath)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 40, out) //less than 300 kb

            out.flush()
            out.close()

            val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
            //save image to db
            val pathDb = savePrintImagePath.path
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = pathDb
            mImage.id = id.toInt()
            mDb?.dbDAO?.insertCitationImage(mImage)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        /**
         * Tag for the [Log].
         */
        private val TAG = "ImageSaver"
    }
}
