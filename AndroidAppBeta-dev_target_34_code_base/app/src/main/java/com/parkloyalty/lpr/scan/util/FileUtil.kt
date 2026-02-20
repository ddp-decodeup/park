package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.util.BitmapUtils.combineBitmapsSideBySide
import okhttp3.ResponseBody
import java.io.*

object FileUtil {
    private const val EOF = -1
    private const val DEFAULT_BUFFER_SIZE = 1024 * 4

    //Name of json file located in asset folder
    const val ASSET_HEARING_DATES_ON_HOLIDAY_JSON = "hearing_dates_on_holiday.json"

    fun readJsonFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    @Throws(IOException::class)
    fun from(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileName(context, uri)
        val splitName = splitFileName(fileName)
        var tempFile = File.createTempFile(splitName[0], splitName[1])
        tempFile = rename(tempFile, fileName)
        tempFile.deleteOnExit()
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(tempFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (inputStream != null) {
            copy(inputStream, out)
            inputStream.close()
        }
        out?.close()
        return tempFile
    }

    private fun splitFileName(fileName: String?): Array<String?> {
        var name = fileName
        var extension: String? = ""
        val i = fileName!!.lastIndexOf(".")
        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }
        return arrayOf(name, extension)
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun rename(file: File, newName: String?): File {
        val newFile = File(file.parent, newName)
        if (newFile != file) {
            if (newFile.exists() && newFile.delete()) {
                //Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                //Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile
    }

    @Throws(IOException::class)
    private fun copy(input: InputStream, output: OutputStream?): Long {
        var count: Long = 0
        var n: Int
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (EOF != input.read(buffer).also { n = it }) {
            output!!.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }

    fun getFileNameWithoutExtension(fileName : String) : String{
        return if (fileName.indexOf(".") > 0) {
            fileName.substring(0, fileName.lastIndexOf("."))
        } else {
            fileName
        }
    }

    fun saveBitmapToStorage(finalBitmap: Bitmap, imageName: String) : String {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )

        myDir.mkdirs()
        val fname = "${imageName?.trim()}.jpg"
        val savePrintImagePath = File(myDir, fname)
        if (savePrintImagePath!!.exists()) savePrintImagePath!!.delete()
        try {

            val out = FileOutputStream(savePrintImagePath)
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS,ignoreCase = true)) {
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            } else {
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            }

            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return savePrintImagePath.absolutePath
    }

    /**
     * This is for Static Header Footer
     * Function to used to concat bitmaps, save to storage & returns file path of the image with header footer
     */
    fun getFilePathOfTicketWithHeaderFooter(context: Context,imagePostFix:String,citationNumber : String, path:String) : String{
        var bm1 : Bitmap? = null
        var bm3 : Bitmap? = null

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)){
             bm1 = BitmapFactory.decodeResource(context.resources, R.drawable.print_header_duncan)
             bm3 = BitmapFactory.decodeResource(context.resources, R.drawable.print_footer_duncan)
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)){
            bm1 = BitmapFactory.decodeResource(context.resources, R.drawable.print_header_surf_city)
            bm3 = BitmapFactory.decodeResource(context.resources, R.drawable.print_footer_surf_city)
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)){
            bm1 = BitmapFactory.decodeResource(context.resources, R.drawable.print_header_city_of_san_diago)
            bm3 = BitmapFactory.decodeResource(context.resources, R.drawable.print_footer_city_of_san_diago)
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)){
            bm1 = BitmapFactory.decodeResource(context.resources, R.drawable.print_header_city_of_burbank)
            bm3 = BitmapFactory.decodeResource(context.resources, R.drawable.print_footer_city_of_burbank)
        }
        else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)){
            bm1 = BitmapFactory.decodeResource(context.resources, R.drawable.print_header_burbank)
            bm3 = BitmapFactory.decodeResource(context.resources, R.drawable.print_footer_burbank)
        }
        else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)){
            bm1 = BitmapFactory.decodeResource(context.resources, R.drawable.print_header_volusia)
            bm3 = BitmapFactory.decodeResource(context.resources, R.drawable.print_footer_volusia)
        }
        else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)){
            bm1 = BitmapFactory.decodeResource(context.resources, R.drawable.print_header_carta)
            bm3 = BitmapFactory.decodeResource(context.resources, R.drawable.print_footer_carta)
        }
        else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)){
            bm1 = BitmapFactory.decodeResource(context.resources, R.drawable.print_header_ace_san_diego)
            bm3 = BitmapFactory.decodeResource(context.resources, R.drawable.print_footer_ace_san_diego)
        }
        else{

        }

        val printBitmap = BitmapFactory.decodeFile(path)

        val scaledBitmap1 = BitmapUtils.scale(bm1!!, printBitmap.width, printBitmap.width)
        val scaledBitmap2 = BitmapUtils.scale(bm3!!, printBitmap.width, printBitmap.width)

        val a = ArrayList<Bitmap>()
        a.add(scaledBitmap1)
        a.add(printBitmap)
        a.add(scaledBitmap2)

        val bitmapFinal = combineBitmapsSideBySide(a,false)
        val finalName = citationNumber +"_" +imagePostFix

        return saveBitmapToStorage(bitmapFinal,finalName)
    }


    /**
     * This is for Dynamic Header Footer
     * Function to used to concat bitmaps, save to storage & returns file path of the image with header footer
     */
    fun getFilePathOfTicketWithHeaderFooterDynamic(
        imagePostFix: String,
        citationNumber: String,
        path: String
    ): String {
        try {
            val headerBitmap = BitmapFactory.decodeFile(getHeaderFooterImageFileFullPath(true))
            val footerBitmap = BitmapFactory.decodeFile(getHeaderFooterImageFileFullPath(false))
            val printBitmap = BitmapFactory.decodeFile(path)

            val scaledHeaderBitmap =
                BitmapUtils.scale(headerBitmap, printBitmap.width, printBitmap.width)
            val scaledFooterBitmap =
                BitmapUtils.scale(footerBitmap, printBitmap.width, printBitmap.width)

            val listOfBitmaps = ArrayList<Bitmap>()
            listOfBitmaps.add(scaledHeaderBitmap)
            listOfBitmaps.add(printBitmap)
            listOfBitmaps.add(scaledFooterBitmap)

            val bitmapFinal = combineBitmapsSideBySide(listOfBitmaps, false)
            val finalName = citationNumber + "_" + imagePostFix

            return saveBitmapToStorage(bitmapFinal, finalName)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * Function used to check if header file exist on given place or not
     */
    fun checkHeaderImageFileExist(): Boolean {
        val file = File(getHeaderFooterDirectory(), getHeaderFooterFileName(true))
        return file.exists()
    }

    /**
     * Function used to check if footer file exist on given place or not
     */
    fun checkFooterImageFileExist(): Boolean {
        val file = File(getHeaderFooterDirectory(), getHeaderFooterFileName(false))
        return file.exists()
    }


    /**
     * Function used to get Full path of header and footer to use
     * @param isHeader: true if you want header file full path or false if footer file full path
     */
    fun getHeaderFooterImageFileFullPath(isHeader : Boolean) : String{
        val file = File(getHeaderFooterDirectory(), getHeaderFooterFileName(isHeader))
        return if (isHeader){
            file.absolutePath
        }else{
            file.absolutePath
        }
    }

    /**
     * Function will return directly file of where header footer image is going to be saved
     */
    fun getHeaderFooterDirectory(): File {
        return File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.HEADER_FOOTER
        )

        //Use app direct so it will delete the files when the app uninstall
        //Instead of Environment.DIRECTORY_DOCUMENTS you can pass null as well
//        return File(
//            BaseApplication.instance?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath,
//            Constants.FILE_NAME + Constants.HEADER_FOOTER
//        )
    }

    /**
     * Function used to get Header Footer image file name
     * @param isHeader: true if you want header file name or false if footer file name
     * @return : It will return image file name
     */
    fun getHeaderFooterFileName(isHeader: Boolean): String {
        return if (isHeader) {
            "${BuildConfig.FLAVOR}_header.jpg"
        } else {
            "${BuildConfig.FLAVOR}_footer.jpg"
        }
    }


    /**
     * Function used to save url responseBody stream to file storage with your given name
     * @param responseBody : Your API response body
     * @param directory : Your directory file where you want to save the file
     * @param fileName : Name of the file you want to define
     * @return : It will return true false to identify if the file is got saved or not
     */
    fun saveRequestBodyStreamToFileStorage(
        responseBody: ResponseBody,
        directory: File,
        fileName: String
    ): Boolean {
        directory.mkdirs()
        val file = File(directory, fileName)

        // Write the image file to internal storage
        try {
            val inputStream: InputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).also { len = it } != -1) {
                outputStream.write(buffer, 0, len)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getLprImageFileFromBannerList(bannerList: ArrayList<CitationImagesModel>?): File? {
        if (!bannerList.isNullOrEmpty()) {
            val lprImage = bannerList.firstOrNull {
                it.citationImage?.contains("anpr_", true).nullSafety()
            }?.citationImage
            return if (!lprImage.isNullOrEmpty()) {
                try {
                    File(lprImage)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        } else {
            return null
        }
    }

    fun isLprImageExists(lprNumber: String?): Pair<Boolean, String> {
        val imageName = "anpr_$lprNumber.jpg"
        val destFile = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.LPRSCANIMAGES + File.separator + imageName
        )

        return Pair(destFile.exists(), destFile.absolutePath)
    }

    fun isVehicleStickerImageExists(lprNumber: String?): Pair<Boolean, String> {
        val imageName = "ny_sticker_$lprNumber.jpg"
        val destFile = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.LPRSCANIMAGES + File.separator + imageName
        )

        return Pair(destFile.exists(), destFile.absolutePath)
    }

    fun getSignatureFileNameWithExt(): String {
        return "${BuildConfig.FLAVOR}_Image_Signature.jpg"
    }

    fun takeDatabaseBackUpAndSave(context: Context) {
        val dbFile: File = context.getDatabasePath("park_loyalty")
        val sDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.DATABASEBACKUP
        )
        val fileName = "park_loyalty"
        val sfPath = sDir.path + File.separator + fileName
        if (!sDir.exists()) {
            sDir.mkdirs()
        }
        val saveFile = File(sfPath)
        if (saveFile.exists()) {
            logD("LOGGER ", "File exists. Deleting it and then creating new file.")
            saveFile.delete()
        }
        try {
            if (saveFile.createNewFile()) {
                val bufferSize = 8 * 1024
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                val saveDb: OutputStream = FileOutputStream(sfPath)
                val indDb: InputStream = FileInputStream(dbFile)
                do {
                    bytesRead = indDb.read(buffer, 0, bufferSize)
                    if (bytesRead < 0)
                        break
                    saveDb.write(buffer, 0, bytesRead)
                } while (true)
                saveDb.flush()
                indDb.close()
                saveDb.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    fun removeTimingImagesFromFolder() {
        try {
            val directoryCamera = File(
                Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.ALLREPORTIMAGES
            )
            deleteRecursive(directoryCamera)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun deleteRecursive(directory: File) {
        try {
            if (directory.exists()) {
                if (directory.isDirectory) for (child in directory.listFiles()) deleteRecursive(
                    child
                )

                directory.delete()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun createDirForContinuesMode() {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.COTINOUS
        )
        myDir.mkdirs()
    }

    fun getSignatureDirectory(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Constants.FILE_NAME + Constants.SIGNATURE
        )
    }

    fun saveSignature(dirFile: File, finalBitmap: Bitmap) {
        dirFile.mkdirs()

        val file = File(dirFile.absolutePath, getSignatureFileNameWithExt())
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun createCredFolder(sharedPreference: SharedPref) {
        val mPass = sharedPreference.read(SharedPrefKey.PASSWORD_DB, "")
        val localFolder =
            File(Environment.getExternalStorageDirectory().absolutePath, Constants.FILE_NAME)
        if (!localFolder.exists()) {
            localFolder.mkdirs()
        }/* SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();*/
        val fileName = "credentials" + ".txt" //like 2016_01_12.txt
        val file = File(localFolder, fileName)
        val writer = FileWriter(file, false)
        writer.append("Password :- " + "park_loyalty")
        writer.flush()
        writer.close()
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
        //if file not exists the download
        val mFilepath = localFolder.absolutePath
        sharedPreference.write(SharedPrefKey.FILE_PATH, mFilepath)
    }

    fun createFolderForLprImages() {
        val localFolder = File(
            Environment.getExternalStorageDirectory().absolutePath,
            "/ParkLoyalty" + Constants.SCANNER
        )
        if (!localFolder.exists()) {
            localFolder.mkdirs()
        }
        val file = File(localFolder.toString())
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }

        }

        // scan copy images folder
        val localFolderScan = File(
            Environment.getExternalStorageDirectory().absolutePath,
            "/ParkLoyalty" + Constants.LPRSCANIMAGES
        )
        if (!localFolderScan.exists()) {
            localFolderScan.mkdirs()
        }
        val fileScan = File(localFolderScan.toString())
        if (!fileScan.exists()) {
            try {
                fileScan.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
    }
}