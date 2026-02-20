package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.ui.login.model.ForgetPasswordRequest
import com.parkloyalty.lpr.scan.ui.login.model.SiteOfficerLoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("auth/site_officer_login")
    suspend fun login(@Body param: SiteOfficerLoginRequest?): Response<JsonNode>

    @POST("forgot-password")
    suspend fun forgetPassword(
        @Body forgetPasswordRequest: ForgetPasswordRequest?
    ): Response<JsonNode>

    @GET("auth/refresh/officer")
    suspend fun getAuthTokenRefresh(): Response<JsonNode>

    @GET("analytics/supervisor_daily_summary")
    suspend fun getSupervisor(@Query("shift") shift: String?): Response<JsonNode>

}