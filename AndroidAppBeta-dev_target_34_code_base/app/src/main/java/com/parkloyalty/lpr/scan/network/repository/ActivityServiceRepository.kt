package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.ActivityService
import com.parkloyalty.lpr.scan.ui.login.model.UpdateSiteOfficerRequest
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ACTIVITY_COUNT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CHECK_SETUP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DASHBOARD
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_BAR_COUNT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_COUNT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_LINE_COUNT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_ROUTE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_SITE_OFFICER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_TIME
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_VIOLATION_COUNT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_WELCOME
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ActivityServiceRepository @Inject constructor(
    private val activityService: ActivityService
) {

    suspend fun welcome(): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_WELCOME, call = { activityService.welcome() })

    suspend fun checkSetup(): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_CHECK_SETUP, call = { activityService.checkSetup() })

    suspend fun dashboard(): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_DASHBOARD, call = { activityService.dashboard() })

    suspend fun updateSiteOfficer(updateSiteOfficerRequest: UpdateSiteOfficerRequest): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_UPDATE_SITE_OFFICER,
            call = { activityService.updateSiteOfficer(param = updateSiteOfficerRequest) })

    suspend fun updateTime(): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_UPDATE_TIME, call = { activityService.updateTime() })

    suspend fun getCount(): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_GET_COUNT, call = { activityService.getCount() })

    suspend fun activityCount(shift: String): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_ACTIVITY_COUNT,
        call = { activityService.activityCount(shift = shift) })

    suspend fun violationCount(shift: String): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_VIOLATION_COUNT,
        call = { activityService.violationCount(shift = shift) })

    suspend fun getBarCount(shift: String): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_BAR_COUNT,
        call = { activityService.getBarCount(shift = shift) })

    suspend fun getLineCount(shift: String): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_LINE_COUNT,
        call = { activityService.getLineCount(shift = shift) })

    suspend fun getRoute(shift: String): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_ROUTE, call = { activityService.getRoute(shift = shift) })
}