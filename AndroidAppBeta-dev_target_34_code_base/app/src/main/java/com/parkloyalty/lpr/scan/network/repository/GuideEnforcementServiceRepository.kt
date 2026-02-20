package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.GuideEnforcementService
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_CAMERA_VIOLATION_DATASET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_GENETIC_HIT_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_PAY_BY_PLATE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_PAY_BY_SPACE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_PAY_BY_SPACE_DATASET
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GuideEnforcementServiceRepository @Inject constructor(
    private val guideEnforcementService: GuideEnforcementService
) {

    suspend fun getPayByPlate(zone: String, lpNumber: String): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_GET_PAY_BY_PLATE,
            call = { guideEnforcementService.getPayByPlate(zone = zone, lpNumber = lpNumber) })

    suspend fun getPayBySpace(
        zone: String? = null,
        spaceId: String? = null,
        fromTs: String? = null,
        page: Int? = null,
    ): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_PAY_BY_SPACE,
        call = { guideEnforcementService.getPayBySpace(zone, spaceId, fromTs, page) })

    suspend fun getPayBySpaceDataSet(zone: String? = null): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_PAY_BY_SPACE_DATASET,
        call = { guideEnforcementService.getPayBySpaceDataSet(zone) })

    suspend fun getCameraViolationDataSet(
        zone: String? = null,
        lpNumber: String? = null,
        timeFrom: String? = null,
        timeTo: String? = null,
        page: Int? = null,
        limit: String? = null,
    ): NewApiResponse<JsonNode> =
        safeApiCall(apiNameTag = API_TAG_NAME_GET_CAMERA_VIOLATION_DATASET, call = {
            guideEnforcementService.getCameraViolationDataSet(
                zone, lpNumber, timeFrom, timeTo, page, limit
            )
        })

    suspend fun getGeneticHitList(
        typeOfHit: String?, lprNumber: String?, page: String?, limit: String?
    ): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_GENETIC_HIT_LIST,
        call = { guideEnforcementService.getGeneticHitList(typeOfHit, lprNumber, page, limit) })


}