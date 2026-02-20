package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ActivityImageUploadRequest
import com.parkloyalty.lpr.scan.ui.continuousmode.model.UploadCsvLinksRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface MediaService {

    @GET("citations-issuer/ticket")
    suspend fun getFacsimileImages(
        @Query("ticket_no") ticketNo: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: String? = null,
    ): Response<JsonNode>

    @POST("analytics/mobile/update_activity_images")
    suspend fun uploadActivityImage(@Body activityImageUploadRequest: ActivityImageUploadRequest?): Response<JsonNode>

    @GET("continuouslpr/datasets_link")
    suspend fun downloadAlertFile(): Response<JsonNode>

    @Multipart
    @POST("static_file/bulk_upload")
    suspend fun uploadImages(
        @Part("data") mIDList: Array<String>?,
        @Part("upload_type") uploadType: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<JsonNode>

    @Multipart
    @POST("static_file/bulk_upload")
    suspend fun uploadAllImages(
        @Part("data") mIDList: List<String?>,
        @Part("upload_type") uploadType: RequestBody?,
        @Part image: List<MultipartBody.Part?>
    ): Response<JsonNode>

    @Multipart
    @POST("static_file/bulk_upload")
    suspend fun uploadAllImagesInBulk(
        @Part("data") data: RequestBody?,
        @Part("upload_type") uploadType: RequestBody?,
        @Part files: List<MultipartBody.Part?>
    ): Response<JsonNode>

    @POST("informatics/lpr_session_results")
    suspend fun uploadCsv(
        @Body mIDList: UploadCsvLinksRequest?
    ): Response<JsonNode>

    @Multipart
    @POST("static_file/bulk_upload")
    suspend fun staticUploadCsv(
        @Part("data") mIDList: Array<String?>?,
        @Part("upload_type") uploadType: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<JsonNode>

    @Multipart
    @POST("document-upload")
    suspend fun uploadDocToVerification(
        @Query("doc_type") docType: RequestBody?,
        @Part("front_image") frontImage: MultipartBody.Part?,
        @Part("back_image") backImage: MultipartBody.Part?
    ): Response<JsonNode>

    @Multipart
    @POST("document-update")
    suspend fun updateDocToVerification(
        @Part("doc_type") doc_type: RequestBody?,
        @Part("front_image") frontImage: MultipartBody.Part?,
        @Part("back_image") backImage: MultipartBody.Part?
    ): Response<JsonNode>

    @POST("static_file/download_files")
    suspend fun downloadBitmap(
        @Body downloadBitmapRequest: DownloadBitmapRequest?
    ): Response<JsonNode>

    //Generic function used to download file from full url
    @GET
    suspend fun downloadFile(
        @Url url: String
    ): Response<ResponseBody>


    @Multipart
    @POST("static_file/bulk_upload")
    suspend fun uploadTextFile(
        @Part("data") mIDList: Array<String>?,
        @Part("upload_type") uploadType: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<JsonNode>
}