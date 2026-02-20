package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.MediaService
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ActivityImageUploadRequest
import com.parkloyalty.lpr.scan.ui.continuousmode.model.UploadCsvLinksRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapRequest
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_ALERT_FILE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_FACSIMILE_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_STATIC_UPDATE_CSV
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_ALL_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_ALL_IMAGES_IN_BULK
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_CSV
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_DOC_TO_VERIFICATION
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_ACTIVITY_IMAGE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_DOC_TO_VERIFICATION
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_TEXT_FILE
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MediaServiceRepository @Inject constructor(
    private val mediaService: MediaService
) {
    suspend fun getFacsimileImages(
        ticketNo: String?, page: Int?, limit: String?
    ): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_GET_FACSIMILE_IMAGES, call = {
            mediaService.getFacsimileImages(
                ticketNo = ticketNo, page = page, limit = limit
            )
        })

    suspend fun uploadActivityImage(activityImageUploadRequest: ActivityImageUploadRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_UPLOAD_ACTIVITY_IMAGE,
            call = { mediaService.uploadActivityImage(activityImageUploadRequest = activityImageUploadRequest) })

    suspend fun downloadAlertFile(): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_DOWNLOAD_ALERT_FILE, call = { mediaService.downloadAlertFile() })

    suspend fun uploadImages(
        apiTagName: String,
        mIDList: Array<String>?,
        uploadType: RequestBody?,
        image: MultipartBody.Part?
    ): NewApiResponse<JsonNode> = safeApiCall(apiNameTag = apiTagName, call = {
        mediaService.uploadImages(
            mIDList = mIDList, uploadType = uploadType, image = image
        )
    })

    suspend fun uploadAllImages(
        mIDList: List<String?>, uploadType: RequestBody?, image: List<MultipartBody.Part?>
    ): NewApiResponse<JsonNode> = safeApiCall(apiNameTag = API_TAG_NAME_UPDATE_ALL_IMAGES, call = {
        mediaService.uploadAllImages(
            mIDList = mIDList, uploadType = uploadType, image = image
        )
    })

    suspend fun uploadAllImagesInBulk(
        data: RequestBody?, uploadType: RequestBody?, files: List<MultipartBody.Part?>
    ): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_UPDATE_ALL_IMAGES_IN_BULK, call = {
            mediaService.uploadAllImagesInBulk(
                data = data, uploadType = uploadType, files = files
            )
        })

    suspend fun uploadCsv(mIDList: UploadCsvLinksRequest?): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_UPDATE_CSV, call = { mediaService.uploadCsv(mIDList = mIDList) })

    suspend fun staticUploadCsv(
        mIDList: Array<String?>?, uploadType: RequestBody?, file: MultipartBody.Part?
    ): NewApiResponse<JsonNode> = safeApiCall(apiNameTag = API_TAG_NAME_STATIC_UPDATE_CSV, call = {
        mediaService.staticUploadCsv(
            mIDList = mIDList, uploadType = uploadType, file = file
        )
    })

    suspend fun uploadDocToVerification(
        docType: RequestBody?, frontImage: MultipartBody.Part?, backImage: MultipartBody.Part?
    ): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_UPLOAD_DOC_TO_VERIFICATION, call = {
            mediaService.uploadDocToVerification(
                docType = docType, frontImage = frontImage, backImage = backImage
            )
        })

    suspend fun updateDocToVerification(
        docType: RequestBody?, frontImage: MultipartBody.Part?, backImage: MultipartBody.Part?
    ): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_UPDATE_DOC_TO_VERIFICATION, call = {
            mediaService.updateDocToVerification(
                doc_type = docType, frontImage = frontImage, backImage = backImage
            )
        })

    suspend fun downloadBitmap(
        apiNameTag: String,
        downloadBitmapRequest: DownloadBitmapRequest?
    ): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = apiNameTag,
        call = { mediaService.downloadBitmap(downloadBitmapRequest = downloadBitmapRequest) })

    suspend fun downloadFile(apiNameTag: String, url: String): NewApiResponse<ResponseBody> =
        safeApiCall(
            apiNameTag = apiNameTag, call = { mediaService.downloadFile(url = url) })

    suspend fun uploadTextFile(
        mIDList: Array<String>?, uploadType: RequestBody?, image: MultipartBody.Part?
    ): NewApiResponse<JsonNode> = safeApiCall(apiNameTag = API_TAG_NAME_UPLOAD_TEXT_FILE, call = {
        mediaService.uploadTextFile(
            mIDList = mIDList, uploadType = uploadType, image = image
        )
    })
}
