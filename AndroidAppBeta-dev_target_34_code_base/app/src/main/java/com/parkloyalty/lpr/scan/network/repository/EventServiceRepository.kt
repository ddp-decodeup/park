package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerRequest
import com.parkloyalty.lpr.scan.common.model.PushEventRequest
import com.parkloyalty.lpr.scan.common.model.UserEventRequest
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.EventService
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerRequest
import com.parkloyalty.lpr.scan.ui.continuousmode.model.ContinuousDataObject
import com.parkloyalty.lpr.scan.ui.continuousmode.model.LprEndSessionRequest
import com.parkloyalty.lpr.scan.ui.login.model.ActivityUpdateRequest
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ACTIVITY_LOG
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_EVENT_LOGIN
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_INACTIVE_METER_BUZZER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LPR_END_SESSION
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LPR_SCAN_LOGGER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LPR_START_SESSION
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_OFFICER_DAILY_SUMMARY
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_PUSH_EVENT
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EventServiceRepository @Inject constructor(
    private val eventService: EventService
) {
    suspend fun activityLog(activityUpdateRequest: ActivityUpdateRequest): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_ACTIVITY_LOG,
            call = { eventService.activityLog(activityUpdateRequest = activityUpdateRequest) })

    suspend fun lprStartSession(param: ContinuousDataObject): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_LPR_START_SESSION,
            call = { eventService.lprStartSession(param = param) })

    suspend fun lprEndSession(param: LprEndSessionRequest): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_LPR_END_SESSION,
        call = { eventService.LprEndSession(param = param) })

    suspend fun officerDailySummary(shift: String): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_OFFICER_DAILY_SUMMARY,
        call = { eventService.officerDailySummary(shift = shift) })

    suspend fun eventLogin(param: UserEventRequest): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_EVENT_LOGIN, call = { eventService.eventLogin(param = param) })

    suspend fun pushEvent(param: PushEventRequest): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_PUSH_EVENT, call = { eventService.pushEvent(param = param) })

    suspend fun inactiveMeterBuzzer(inactiveMeterBuzzerRequest: InactiveMeterBuzzerRequest): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_INACTIVE_METER_BUZZER,
            call = { eventService.inactiveMeterBuzzer(inactiveMeterBuzzerRequest = inactiveMeterBuzzerRequest) })

    suspend fun lprScanLogger(lprScanLoggerRequest: LprScanLoggerRequest?): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_LPR_SCAN_LOGGER,
        call = { eventService.lprScanLogger(lprScanLoggerRequest = lprScanLoggerRequest) })

}