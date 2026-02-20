package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.LocationService
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOCATION_UPDATE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationServiceRepository @Inject constructor(
    private val locationService: LocationService
) {
    suspend fun locationUpdate(locUpdateRequest: LocUpdateRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_LOCATION_UPDATE,
            call = { locationService.locationUpdate(locUpdateRequest = locUpdateRequest) })
}