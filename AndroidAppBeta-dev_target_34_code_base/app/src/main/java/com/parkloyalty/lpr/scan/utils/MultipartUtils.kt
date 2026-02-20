package com.parkloyalty.lpr.scan.utils

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.parkloyalty.lpr.scan.extensions.getCitaitonImageFormat
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_TIMESTAMP
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import kotlin.collections.forEach

object MultipartUtils {
    fun getImageUploadRequestData(file: File, citationNumber:String, num: Int, contentType:String) : Triple<Array<String>, RequestBody, MultipartBody.Part> {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val files = MultipartBody.Part.createFormData("files", file.name, requestFile)
        val dropdownName = if (file.name.contains("_$FILE_NAME_FACSIMILE_PRINT_BITMAP")) {
            "${citationNumber}_${num}_$FILE_NAME_FACSIMILE_PRINT_BITMAP"
        } else {
            "${citationNumber}_$num"
        }
        val mDropdownList = arrayOf(dropdownName)
        val mRequestBodyType = contentType.toRequestBody("text/plain".toMediaTypeOrNull())

        return Triple( mDropdownList,mRequestBodyType, files)
    }

    fun getImageMultipart(
        isStatusCheck: Boolean,
        imageList: MutableList<CitationImagesModel?>?
    ): List<MultipartBody.Part?> {
        val imageMultipartList = ArrayList<MultipartBody.Part?>()

        imageList?.forEach {
            if (isStatusCheck && it?.status == 1) {
                val tempFile: File = File(it.citationImage.nullSafety())
                val requestFile = tempFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val files = MultipartBody.Part.createFormData(
                    "files", tempFile.name, requestFile
                )
                imageMultipartList.add(files)
            } else {
                val tempFile: File = File(it?.citationImage.nullSafety())
                val requestFile = tempFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val files = MultipartBody.Part.createFormData(
                    "files", tempFile.name, requestFile
                )
                imageMultipartList.add(files)
            }
        }

        return imageMultipartList
    }

    fun createImagesNameList(
        context: Context,
        namePrefix: String,
        isTimestamp: Boolean,
        apiPayloadTitle: String,
        imageList: MutableList<CitationImagesModel?>?
    ): Array<String?> {
        val imageNameList = arrayOfNulls<String>(imageList?.size.nullSafety())

        imageList?.forEachIndexed { index, scanDataModel ->
            if (imageList[index]?.status == 1) {
                if (isTimestamp) {
                    val timeStamp = SDF_IMAGE_TIMESTAMP.format(Date())
                    imageNameList[index] = getCitaitonImageFormat("${namePrefix}_$timeStamp", index)
                } else {
                    imageNameList[index] = getCitaitonImageFormat("${namePrefix}", index)
                }

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            context, apiPayloadTitle
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            context, "Request " + " :- " + imageNameList.get(index)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return imageNameList
    }
}