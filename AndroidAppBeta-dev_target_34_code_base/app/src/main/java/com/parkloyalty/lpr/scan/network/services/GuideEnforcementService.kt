package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GuideEnforcementService {

    @GET("analytics/mobile/pay_by_plate")
    suspend fun getPayByPlate(
        @Query("zone") zone: String? = null,
        @Query("lp_number") lpNumber: String? = null,
    ): Response<JsonNode>


    @GET("pull-vendors/payments_by_space")
    suspend fun getPayBySpace(
        @Query("zone") zone: String? = null,
        @Query("space_id") spaceId: String? = null,
        @Query("from_ts") fromTs: String? = null,
        @Query("page") page: Int? = null,
    ): Response<JsonNode>


    @GET("pull-vendors/space_location_list")
    suspend fun getPayBySpaceDataSet(
        @Query("zone") zone: String? = null,
    ): Response<JsonNode>


    @GET("violations/unlinked_feeds")
    suspend fun getCameraViolationDataSet(
        @Query("zone") zone: String? = null,
        @Query("lp_number") lpNumber: String? = null,
        @Query("time_from") timeFrom: String? = null,
        @Query("time_to") timeTo: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: String? = null,
    ): Response<JsonNode>

    @GET("vehicle-camera-job/get-genetic-hit-list")
    suspend fun getGeneticHitList(
        @Query("type_of_hit") typeOfHit: String?,
        @Query("lp_number") lprNumber: String?,
        @Query("page") page: String?,
        @Query("limit") limit: String?
    ): Response<JsonNode>

}