package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.CitationService
import com.parkloyalty.lpr.scan.ui.boot.model.BootMetadataRequest
import com.parkloyalty.lpr.scan.ui.boot.model.BootRequest
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.SimilarCitationCheckRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.DataFromLprRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ExemptRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.MeterRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.PermitRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.SofflawRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.TimingMarkBulkRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.AddNoteRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.DriveOffTvrRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.UpdateMarkRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.UpdateTicketRequest
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_NOTES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_TIMING
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_BOOT_INSTANCE_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_BOOT_SUBMIT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CANCEL_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CHECK_SIMILAR_CITATION
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CREATE_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DRIVE_OFF_TVR
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_ABANDONED_HIT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_ACTIVITY_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_CITATION_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_CITATION_NUMBER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_DATA_FROM_LPR
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_EXEMPT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_GENETIC_HIT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_LAST_SECOND_CHECK
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_METER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_NOTES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_PERMIT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_SCOFFLAW
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_TICKET_STATUS
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_TIMING
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_TIMING_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_TIMING_MARK
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_TIMING_MARK_BULK
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_MARK
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_TICKET
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CitationServiceRepository @Inject constructor(
    private val citationService: CitationService
) {

    suspend fun getTiming(param: TimingRequest): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_TIMING,
            call = { citationService.getTiming(param = param) })
    }

    suspend fun getExempt(param: ExemptRequest): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_EXEMPT,
            call = { citationService.getExempt(param = param) })
    }

    suspend fun getSofflaw(param: SofflawRequest): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_SCOFFLAW,
            call = { citationService.getSofflaw(param = param) })
    }

    suspend fun getPermit(param: PermitRequest): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_PERMIT,
            call = { citationService.getPermit(param = param) })
    }

    suspend fun getTimingMarkBulk(param: TimingMarkBulkRequest): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_TIMING_MARK_BULK,
            call = { citationService.getTimingMarkBulk(param = param) })
    }

    suspend fun getDataFromLpr(dataFromLprRequest: DataFromLprRequest?): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_DATA_FROM_LPR,
            call = { citationService.getDataFromLpr(dataFromLprRequest = dataFromLprRequest) })
    }

    suspend fun getTimingMark(
        arrivalStatus: String? = null,
        issueTsFrom: String? = null,
        issueTsTo: String? = null,
        lpNumber: String? = null,
        page: Int? = null,
        siteOfficerId: String? = null,
        enforced: String? = null
    ): NewApiResponse<JsonNode> {
        return safeApiCall(apiNameTag = API_TAG_NAME_GET_TIMING_MARK, call = {
            citationService.getTimingMark(
                arrivalStatus = arrivalStatus,
                issueTsFrom = issueTsFrom,
                issueTsTo = issueTsTo,
                lpNumber = lpNumber,
                page = page,
                siteOfficerId = siteOfficerId,
                enforced = enforced
            )
        })
    }

    suspend fun getGenetecHit(
        arrivalStatus: String? = null,
        issueTsFrom: String? = null,
        issueTsTo: String? = null,
        vendorName: String? = null,
    ): NewApiResponse<JsonNode> {
        return safeApiCall(apiNameTag = API_TAG_NAME_GET_GENETIC_HIT, call = {
            citationService.getGenetecHit(
                arrivalStatus = arrivalStatus,
                issueTsFrom = issueTsFrom,
                issueTsTo = issueTsTo,
                vendorName = vendorName
            )
        })
    }

    suspend fun getAbandonedHit(
        lpNumber: String? = null
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_ABANDONED_HIT,
            call = { citationService.getAbandonedHit(lpNumber = lpNumber) })
    }

    suspend fun updateTicket(
        id: String?, param: UpdateTicketRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_UPDATE_TICKET,
            call = { citationService.updateTicket(id = id, param = param) })
    }

    suspend fun updateMark(
        id: String?, param: UpdateMarkRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_UPDATE_MARK,
            call = { citationService.updateMark(id = id, param = param) })
    }

    suspend fun getMeter(param: MeterRequest?): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_METER, call = { citationService.getMeter(param = param) })
    }

    suspend fun getCitationNumber(
        citationNumberRequest: CitationNumberRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_CITATION_NUMBER,
            call = { citationService.getCitationNumber(citationNumberRequest = citationNumberRequest) })
    }

    suspend fun getCitationLayout(): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_CITATION_LAYOUT,
            call = { citationService.getCitationLayout() })
    }

    suspend fun getActivityLayout(): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_ACTIVITY_LAYOUT,
            call = { citationService.getActivityLayout() })
    }

    suspend fun getTimingLayout(): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_TIMING_LAYOUT,
            call = { citationService.getTimingLayout() })
    }

    suspend fun bootInstanceTicketAPI(bootMetadataRequest: BootMetadataRequest?): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_BOOT_INSTANCE_TICKET,
            call = { citationService.bootInstanceTicketAPI(bootMetadataRequest = bootMetadataRequest) })
    }

    suspend fun createTicket(
        createTicketRequest: CreateTicketRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_CREATE_TICKET,
            call = { citationService.createTicket(createTicketRequest = createTicketRequest) })
    }

    suspend fun bootSubmit(bootRequest: BootRequest?): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_BOOT_SUBMIT,
            call = { citationService.bootSubmit(bootRequest = bootRequest) })
    }

    suspend fun cancelTicket(
        id: String?, ticketCancelRequest: TicketCancelRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_CANCEL_TICKET,
            call = {
                citationService.cancelTicket(
                    id = id,
                    ticketCancelRequest = ticketCancelRequest
                )
            })
    }

    suspend fun getTicketStatus(
        ticketUploadStatusRequest: TicketUploadStatusRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_TICKET_STATUS,
            call = { citationService.getTicketStatus(ticketUploadStatusRequest = ticketUploadStatusRequest) })
    }

    suspend fun getTicket(
        issueTsFrom: String? = null,
        issueTsTo: String? = null,
        siteOfficerId: String? = null,
        shift: String? = null,
        ticketNo: String? = null,
        block: String? = null,
        street: String? = null,
        side: String? = null,
        status: String? = null,
        lpNumber: String? = null,
        page: Int? = null,
        limit: String? = null,
    ): NewApiResponse<JsonNode> {
        return safeApiCall(apiNameTag = API_TAG_NAME_GET_TICKET, call = {
            citationService.getTicket(
                issueTsFrom = issueTsFrom,
                issueTsTo = issueTsTo,
                siteOfficerId = siteOfficerId,
                shift = shift,
                ticketNo = ticketNo,
                block = block,
                street = street,
                side = side,
                status = status,
                lpNumber = lpNumber,
                page = page,
                limit = limit
            )
        })
    }

    suspend fun addTiming(addTimingRequest: AddTimingRequest?): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_ADD_TIMING,
            call = { citationService.addTiming(addTimingRequest = addTimingRequest) })
    }

    suspend fun driveOffTvr(
        ticketNumber: String?, param: DriveOffTvrRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(apiNameTag = API_TAG_NAME_DRIVE_OFF_TVR, call = {
            citationService.driveOffTvr(
                ticketNumber = ticketNumber, param = param
            )
        })
    }

    suspend fun getLastSecondCheck(
        zone: String? = null,
        park: String? = null,
        lpNumber: String? = null,
        spaceId: String? = null,
    ): NewApiResponse<JsonNode> {
        return safeApiCall(apiNameTag = API_TAG_NAME_GET_LAST_SECOND_CHECK, call = {
            citationService.getLastSecondCheck(
                zone = zone, park = park, lpNumber = lpNumber, spaceId = spaceId
            )
        })
    }

    suspend fun checkSimilarCitation(
        param: SimilarCitationCheckRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_CHECK_SIMILAR_CITATION,
            call = { citationService.checkSimilarCitation(param = param) })
    }

    suspend fun addNotes(
        ticketNumber: String?, request: AddNoteRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(apiNameTag = API_TAG_NAME_ADD_NOTES, call = {
            citationService.addNotes(
                ticketNumber = ticketNumber, request = request
            )
        })
    }

    suspend fun getNotes(ticketNumber: String?): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_NOTES,
            call = { citationService.getNotes(ticketNumber = ticketNumber) })
    }

    suspend fun addImages(
        ticketNumber: String?, addImageRequest: AddImageRequest?
    ): NewApiResponse<JsonNode> {
        return safeApiCall(apiNameTag = API_TAG_NAME_ADD_IMAGES, call = {
            citationService.addImages(
                ticketNumber = ticketNumber, addImageRequest = addImageRequest
            )
        })
    }
}