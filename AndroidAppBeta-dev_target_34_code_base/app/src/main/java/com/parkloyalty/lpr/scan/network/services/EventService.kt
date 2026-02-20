package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerRequest
import com.parkloyalty.lpr.scan.common.model.PushEventRequest
import com.parkloyalty.lpr.scan.common.model.UserEventRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerRequest
import com.parkloyalty.lpr.scan.ui.continuousmode.model.ContinuousDataObject
import com.parkloyalty.lpr.scan.ui.continuousmode.model.LprEndSessionRequest
import com.parkloyalty.lpr.scan.ui.login.model.ActivityUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EventService {
    @POST("event-logger/activity-update")
    suspend fun activityLog(
        @Body activityUpdateRequest: ActivityUpdateRequest?
    ): Response<JsonNode>


    @POST("lpr_session/start")
    suspend fun lprStartSession(
        @Body param: ContinuousDataObject?
    ): Response<JsonNode>

    @POST("lpr_session/end")
    suspend fun LprEndSession(
        @Body param: LprEndSessionRequest?
    ): Response<JsonNode>

    @GET("analytics/officer_daily_summary")
    suspend fun officerDailySummary(@Query("shift") shift: String?): Response<JsonNode>

    @POST("auth/site_officer_login")
    suspend fun eventLogin(@Body param: UserEventRequest?): Response<JsonNode>

    @POST("events/push_event")
    suspend fun pushEvent(@Body param: PushEventRequest?): Response<JsonNode>

    @POST("pull-vendors/get_meter_communications")
    suspend fun inactiveMeterBuzzer(@Body inactiveMeterBuzzerRequest: InactiveMeterBuzzerRequest?): Response<JsonNode>

    @POST("event-logger/lpr-scan")
    suspend fun lprScanLogger(
        @Body lprScanLoggerRequest: LprScanLoggerRequest?
    ): Response<JsonNode>

}