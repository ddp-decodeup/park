package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface RecaptchaApiService {
    @POST("/recaptcha/api/siteverify")
    suspend fun verifySite(
        @Query("secret") secret: String, @Query("response") response: String
    ): Response<JsonNode>
}


//@Singleton
//class SiteVerifyRepository @Inject constructor() {
//    suspend fun verifySite(secretKey: String, userResponseToken: String): NewApiResponse<SiteVerifyResponse> = safeApiCall {
//        val api = "https://www.google.com/recaptcha/api/siteverify?"
//        val newAPI = "$api" + "secret=$secretKey&response=$userResponseToken"
//        val url = URL(newAPI)
//        val httpURLConnection = url.openConnection() as HttpURLConnection
//        httpURLConnection.requestMethod = "GET"
//        httpURLConnection.doInput = true
//        httpURLConnection.connect()
//        val response = httpURLConnection.responseCode
//        if (response != HttpURLConnection.HTTP_OK) {
//            throw Exception("HTTP error $response")
//        }
//        val result = url.readText()
//        // Parse the result into SiteVerifyResponse
//        val jsonObject = JSONObject(result)
//        SiteVerifyResponse(
//            data = null, // No data field in Google reCAPTCHA response
//            message = jsonObject.optString("error-codes", null),
//            status = jsonObject.optBoolean("success", false)
//        )
//    }
//}
