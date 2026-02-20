package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface MunicipalCitationService {

    @GET("templates/mobile/primary_template?template_type=municipal_citation")
    suspend fun getMunicipalCitationLayout(): Response<JsonNode>

    @POST("citations-issuer/ticket-municipal")
    fun createMunicipalCitationTicket(
        @Body createMunicipalCitationTicketRequest: CreateMunicipalCitationTicketRequest?
    ): Response<JsonNode>

    @GET("citations-issuer/ticket")
    fun getMunicipalCitationTicketHistory(
        @QueryMap values: Map<String, String>
    ): Response<JsonNode>

}