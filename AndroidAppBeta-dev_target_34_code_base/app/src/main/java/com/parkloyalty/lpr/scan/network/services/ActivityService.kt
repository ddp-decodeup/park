package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.ui.login.model.UpdateSiteOfficerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ActivityService {

    @GET("screens/welcome_page")
    suspend fun welcome(): Response<JsonNode>

    @GET("analytics/mobile/get_checksetup")
    suspend fun checkSetup(): Response<JsonNode>

    @GET("screens/dashboard_screen")
    suspend fun dashboard(): Response<JsonNode>

    @POST("l2-onboarder/update_site_officer")
    suspend fun updateSiteOfficer(
        @Body param: UpdateSiteOfficerRequest?
    ): Response<JsonNode>

    @GET("informatics/get_dataset_last_updated_timestamps")
    suspend fun updateTime(): Response<JsonNode>

    @GET("operations/mobile/get_counts")
    suspend fun getCount(): Response<JsonNode>

    @GET("analytics/mobile/get_activity_updates_by_officer")
    suspend fun activityCount(@Query("shift") shift: String?): Response<JsonNode>

    @GET("analytics/mobile/get_violation_counts_by_officer")
    suspend fun violationCount(@Query("shift") shift: String?): Response<JsonNode>

    @GET("analytics/mobile/get_counts")
    suspend fun getBarCount(@Query("shift") shift: String?): Response<JsonNode>

    @GET("analytics/mobile/get_array_counts?timeline=daily")
    suspend fun getLineCount(@Query("shift") shift: String?): Response<JsonNode>

    @GET("location-update/updates")
    suspend fun getRoute(@Query("shift") shift: String?): Response<JsonNode>
}