package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CitationService {

    @POST("get_timing_data_from_lpr")
    suspend fun getTiming(@Body param: TimingRequest?): Response<JsonNode>

    @POST("get_exempt_data_from_lpr")
    suspend fun getExempt(@Body param: ExemptRequest?): Response<JsonNode>

    @POST("get_scofflaw_data_from_lpr")
    suspend fun getSofflaw(@Body param: SofflawRequest?): Response<JsonNode>

    @POST("get_permit_data_from_lpr")
    suspend fun getPermit(@Body param: PermitRequest?): Response<JsonNode>

    @PATCH("parking-timing/mark/bulk")
    suspend fun getTimingMarkBulk(@Body param: TimingMarkBulkRequest?): Response<JsonNode>

    @POST("informatics/get_data_from_lpr")
    suspend fun getDataFromLpr(
        @Body dataFromLprRequest: DataFromLprRequest?
    ): Response<JsonNode>


    @GET("parking-timing/mark")
    suspend fun getTimingMark(
        @Query("arrival_status") arrivalStatus: String? = null,
        @Query("issue_ts_from") issueTsFrom: String? = null,
        @Query("issue_ts_to") issueTsTo: String? = null,
        @Query("lp_number") lpNumber: String? = null,
        @Query("page") page: Int? = null,
        @Query("site_officer_id") siteOfficerId: String? = null,
        @Query("enforced") enforced: String? = null
    ): Response<JsonNode>

    @GET("parking-timing/mark")
    suspend fun getGenetecHit(
        @Query("arrival_status") arrivalStatus: String? = null,
        @Query("issue_ts_from") issueTsFrom: String? = null,
        @Query("issue_ts_to") issueTsTo: String? = null,
        @Query("vendor_name") vendorName: String? = null,
    ): Response<JsonNode>


    @GET("parking-timing/abandonVehicle")
    suspend fun getAbandonedHit(
        @Query("lp_number") lpNumber: String? = null
    ): Response<JsonNode>

    @PATCH("citations-issuer/ticket/{id}")
    suspend fun updateTicket(
        @Path("id") id: String?, @Body param: UpdateTicketRequest?
    ): Response<JsonNode>

    @PATCH("parking-timing/mark/{id}")
    suspend fun updateMark(
        @Path("id") id: String?, @Body param: UpdateMarkRequest?
    ): Response<JsonNode>


    @POST("get_citation_data_from_lpr")
    suspend fun getMeter(@Body param: MeterRequest?): Response<JsonNode>


    @POST("citations/issue_citation_book")
    suspend fun getCitationNumber(
        @Body citationNumberRequest: CitationNumberRequest?
    ): Response<JsonNode>

    @GET("templates/mobile/primary_template?template_type=citation")
    suspend fun getCitationLayout(): Response<JsonNode>

    @GET("templates/mobile/primary_template?template_type=activity")
    suspend fun getActivityLayout(): Response<JsonNode>

    @GET("templates/mobile/primary_template?template_type=timing")
    suspend fun getTimingLayout(): Response<JsonNode>

    @POST("boot-lifecycle/boot-ticket")
    suspend fun bootInstanceTicketAPI(@Body bootMetadataRequest: BootMetadataRequest?): Response<JsonNode>

    @POST("citations-issuer/ticket")
    suspend fun createTicket(
        @Body createTicketRequest: CreateTicketRequest?
    ): Response<JsonNode>

    @POST("dispatch/")
    suspend fun bootSubmit(@Body bootRequest: BootRequest?): Response<JsonNode>

    @PATCH("citations-issuer/ticket/{id}")
    suspend fun cancelTicket(
        @Path("id") id: String?, @Body ticketCancelRequest: TicketCancelRequest?
    ): Response<JsonNode>

    @POST("citations-issuer/check_citation_upload_metadata")
    suspend fun getTicketStatus(
        @Body ticketUploadStatusRequest: TicketUploadStatusRequest?
    ): Response<JsonNode>


    @GET("citations-issuer/ticket")
    suspend fun getTicket(
        @Query("issue_ts_from") issueTsFrom: String? = null,
        @Query("issue_ts_to") issueTsTo: String? = null,
        @Query("site_officer_id") siteOfficerId: String? = null,
        @Query("shift") shift: String? = null,
        @Query("ticket_no") ticketNo: String? = null,
        @Query("block") block: String? = null,
        @Query("street") street: String? = null,
        @Query("side") side: String? = null,
        @Query("status") status: String? = null,
        @Query("lp_number") lpNumber: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: String? = null,
    ): Response<JsonNode>


    @POST("parking-timing/mark")
    suspend fun addTiming(@Body addTimingRequest: AddTimingRequest?): Response<JsonNode>

    @PATCH("citations-issuer/post_ticket_issuance/{ticketNumber}")
    suspend fun driveOffTvr(
        @Path("ticketNumber") ticketNumber: String?, @Body param: DriveOffTvrRequest?
    ): Response<JsonNode>


    @GET("analytics/mobile/last_second_check")
    suspend fun getLastSecondCheck(
        @Query("zone") zone: String? = null,
        @Query("Park") park: String? = null,
        @Query("lp_number") lpNumber: String? = null,
        @Query("space_id") spaceId: String? = null,
    ): Response<JsonNode>

    @POST("citations-issuer/citation_similarity_check")
    suspend fun checkSimilarCitation(
        @Body param: SimilarCitationCheckRequest?
    ): Response<JsonNode>

    @POST("citations-issuer/ticket/{ticketNumber}/note")
    suspend fun addNotes(
        @Path("ticketNumber") ticketNumber: String?, @Body request: AddNoteRequest?
    ): Response<JsonNode>

    @GET("citations-issuer/ticket/{ticketNumber}")
    suspend fun getNotes(@Path("ticketNumber") ticketNumber: String?): Response<JsonNode>

    @POST("citations-issuer/ticket/{ticketNumber}/images")
    suspend fun addImages(
        @Path("ticketNumber") ticketNumber: String?, @Body addImageRequest: AddImageRequest?
    ): Response<JsonNode>
}