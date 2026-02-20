package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.MunicipalCitationService
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CREATE_MUNICIPAL_CITATION_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_MUNICIPAL_CITATION_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_MUNICIPAL_CITATION_TICKET_HISTORY
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MunicipalCitationServiceRepository @Inject constructor(
    private val municipalCitationService: MunicipalCitationService
) {
    suspend fun getMunicipalCitationLayout(): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_MUNICIPAL_CITATION_LAYOUT,
        call = { municipalCitationService.getMunicipalCitationLayout() })

    suspend fun createMunicipalCitationTicket(createMunicipalCitationTicketRequest: CreateMunicipalCitationTicketRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_CREATE_MUNICIPAL_CITATION_TICKET, call = {
                municipalCitationService.createMunicipalCitationTicket(
                    createMunicipalCitationTicketRequest = createMunicipalCitationTicketRequest
                )
            })

    suspend fun getMunicipalCitationTicketHistory(values: Map<String, String>): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_GET_MUNICIPAL_CITATION_TICKET_HISTORY,
            call = { municipalCitationService.getMunicipalCitationTicketHistory(values = values) })

}