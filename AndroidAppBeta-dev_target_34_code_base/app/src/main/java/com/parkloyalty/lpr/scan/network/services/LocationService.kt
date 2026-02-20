package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationService {
    @POST("event-logger/location-update")
    suspend fun locationUpdate(@Body locUpdateRequest: LocUpdateRequest?): Response<JsonNode>
}