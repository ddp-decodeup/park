package com.parkloyalty.lpr.scan.network

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerRequest
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import com.parkloyalty.lpr.scan.common.model.PushEventRequest
import com.parkloyalty.lpr.scan.common.model.UserEventRequest
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutRequest
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.LogoutNoteForEquipmentRequest
import com.parkloyalty.lpr.scan.network.models.ScannedImageUploadResponse
import com.parkloyalty.lpr.scan.qrcode.model.InventoryRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.*
import com.parkloyalty.lpr.scan.ui.boot.model.BootMetadataRequest
import com.parkloyalty.lpr.scan.ui.boot.model.BootRequest
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.BrokenMeterRequest
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.SimilarCitationCheckRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.*
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ActivityImageUploadRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.TimingMarkBulkRequest
import com.parkloyalty.lpr.scan.ui.continuousmode.model.ContinuousDataObject
import com.parkloyalty.lpr.scan.ui.continuousmode.model.LprEndSessionRequest
import com.parkloyalty.lpr.scan.ui.continuousmode.model.UploadCsvLinksRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetRequest
import com.parkloyalty.lpr.scan.ui.login.model.*
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkAPIServices {
    @POST
    fun login(@Url url: String?, @Body param: SiteOfficerLoginRequest?): Observable<JsonNode?>

    @POST
    fun eventLogin(@Url url: String?, @Body param: UserEventRequest?): Observable<JsonNode?>

    @POST
    fun pushEvent(@Url url: String?, @Body param: PushEventRequest?): Observable<JsonNode?>

    @POST
    fun locationUpdate(@Url url: String?, @Body param: LocUpdateRequest?): Observable<JsonNode?>

    @POST
    fun inactiveMeterBuzzer(@Url url: String?, @Body param: InactiveMeterBuzzerRequest?): Observable<JsonNode?>

    @POST
    fun inactiventory(@Url url: String?, @Body param: InventoryRequest?): Observable<JsonNode?>

    @GET
    fun welcome(@Url url: String?): Observable<JsonNode?>

    @GET
    fun updateTime(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getCount(@Url url: String?): Observable<JsonNode?>

    @GET
    fun checkSetup(@Url url: String?): Observable<JsonNode?>

    @GET
    fun dashboard(@Url url: String?): Observable<JsonNode?>

    @POST
    fun updateSiteOfficer(
        @Url url: String?,
        @Body param: UpdateSiteOfficerRequest?
    ): Observable<JsonNode?>

    @POST
    fun forgetPassword(
        @Url url: String?,
        @Body param: ForgetPasswordRequest?
    ): Observable<JsonNode?>

    @POST
    fun getTiming(@Url url: String?, @Body param: TimingRequest?): Observable<JsonNode?>

    @POST
    fun getExempt(@Url url: String?, @Body param: ExemptRequest?): Observable<JsonNode?>

    @POST
    fun getSofflaw(@Url url: String?, @Body param: SofflawRequest?): Observable<JsonNode?>

    @POST
    fun getPermit(@Url url: String?, @Body param: PermitRequest?): Observable<JsonNode?>

    @PATCH
    fun getTimingMarkBulk(@Url url: String?, @Body param: TimingMarkBulkRequest?): Observable<JsonNode?>

    @POST
    fun getDataFromLpr(
        @Url url: String?,
        @Body param: DataFromLprRequest?
    ): Observable<JsonNode?>

    @GET
    fun getTimingMark(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getGenetecHit(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getAuthTokenRefresh(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getAbandonedHit(@Url url: String?): Observable<JsonNode?>

    @PATCH
    fun updateTicket(
        @Url url: String?,
        @Body param: UpdateTicketRequest?
    ): Observable<JsonNode?>

    @PATCH
    fun updateMark(@Url url: String?, @Body param: UpdateMarkRequest?): Observable<JsonNode?>

    @POST
    fun LprScanLogger(
        @Url url: String?,
        @Body param: LprScanLoggerRequest?
    ): Observable<JsonNode?>

    @POST
    fun getMeter(@Url url: String?, @Body param: MeterRequest?): Observable<JsonNode?>

    //PHASE 2 APIS ----------------------------------------->
    @POST
    fun getCitationNumber(
        @Url url: String?,
        @Body param: CitationNumberRequest?
    ): Observable<JsonNode?>

    @POST
    fun getCitationDataset(
        @Url url: String?,
        @Body param: DropdownDatasetRequest?
    ): Observable<JsonNode?>

    @POST
    fun createTicket(
        @Url url: String?,
        @Body param: CreateTicketRequest?
    ): Observable<JsonNode?>

    @POST
    fun BootSubmit(@Url url: String?, @Body param: BootRequest?): Observable<JsonNode?>

    @POST
    fun BrokenMeterSubmit(
        @Url url: String?,
        @Body param: BrokenMeterRequest?
    ): Observable<JsonNode?>

    @POST
    fun BrokenMeterReportSubmit(
        @Url url: String?,
        @Body param: BrokenMeterReportRequest?
    ): Observable<JsonNode?>

    @POST
    fun SignOffReportRequestSubmit(
        @Url url: String?,
        @Body param: SignOffReportRequest?
    ): Observable<JsonNode?>

    @POST
    fun TowReportRequestSubmit(
        @Url url: String?,
        @Body param: TowReportRequest?
    ): Observable<JsonNode?>

    @POST
    fun NoticeToTowReportRequestSubmit(
        @Url url: String?,
        @Body param: NoticeToTowRequest?
    ): Observable<JsonNode?>

    @POST
    fun CurbReportSubmit(
        @Url url: String?,
        @Body param: CurbRequest?
    ): Observable<JsonNode?>

    @POST
    fun HandHeldMalFunctionsReportSubmit(
        @Url url: String?,
        @Body param: HandHeldMalFunctionsRequest?
    ): Observable<JsonNode?>

    @POST
    fun signReportSubmit(
        @Url url: String?,
        @Body param: SignReportRequest?
    ): Observable<JsonNode?>

    @POST
    fun vehicleInspectionReportSubmit(
        @Url url: String?,
        @Body param: VehicleInspectionRequest?
    ): Observable<JsonNode?>

    @POST
    fun hourMarkedVehiclesReportSubmit(
        @Url url: String?,
        @Body param: HourMarkedVehiclesRequest?
    ): Observable<JsonNode?>

    @POST
    fun bikeInspectionReportSubmit(
        @Url url: String?,
        @Body param: BikeInspectionsRequest?
    ): Observable<JsonNode?>

    @POST
    fun supervisorReportSubmit(
        @Url url: String?,
        @Body param: SupervisorReportRequest?
    ): Observable<JsonNode?>

    @POST
    fun specialAssignmentReportSubmit(
        @Url url: String?,
        @Body param: SpecialAssignementRequest?
    ): Observable<JsonNode?>

    @POST
    fun FUllTimeReportSubmit(
        @Url url: String?,
        @Body param: FullTimeRequest?
    ): Observable<JsonNode?>


    @POST
    fun PartTimeReportSubmit(
        @Url url: String?,
        @Body param: FullTimeRequest?
    ): Observable<JsonNode?>

    @POST
    fun nflSpecialAssignmentReportSubmit(
        @Url url: String?,
        @Body param: NFLRequest?
    ): Observable<JsonNode?>

    @POST
    fun lotCountVioRateReportSubmit(
        @Url url: String?,
        @Body param: LotCountVioRateRequest?
    ): Observable<JsonNode?>

    @POST
    fun hardSummerFestivalReportSubmit(
        @Url url: String?,
        @Body param: NFLRequest?
    ): Observable<JsonNode?>

    @POST
    fun afterSevenPMReportSubmit(
        @Url url: String?,
        @Body param: AfterSevenPMRequest?
    ): Observable<JsonNode?>

    @POST
    fun payStationReportSubmit(
        @Url url: String?,
        @Body param: PayStationRequest?
    ): Observable<JsonNode?>
    @POST
    fun paySignageReportSubmit(
        @Url url: String?,
        @Body param: SignageReportRequest?
    ): Observable<JsonNode?>
    @POST
    fun homeLessReportSubmit(
        @Url url: String?,
        @Body param: HomelessRequest?
    ): Observable<JsonNode?>
    @POST
    fun workOrderReportSubmit(
        @Url url: String?,
        @Body param: WorkOrderRequest?
    ): Observable<JsonNode?>
    @POST
    fun safetyIssueReportSubmit(
        @Url url: String?,
        @Body param: SafetyIssueRequest?
    ): Observable<JsonNode?>
    @POST
    fun trashLotReportSubmit(
        @Url url: String?,
        @Body param: TrashLotRequest?
    ): Observable<JsonNode?>
    @POST
    fun LotInspectionReportSubmit(
        @Url url: String?,
        @Body param: LotInspectionRequest?
    ): Observable<JsonNode?>

    @GET
    fun activityCount(@Url url: String?): Observable<JsonNode?>

    @GET
    fun violationCount(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getBarCount(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getLineCount(@Url url: String?): Observable<JsonNode?>

    @PATCH
    fun cancelTicket(
        @Url url: String?,
        @Body param: TicketCancelRequest?
    ): Observable<JsonNode?>

    @POST
    fun cancellationTicket(
        @Url url: String?,
        @Body param: TicketCancellationRequest?
    ): Observable<JsonNode?>

    @POST
    fun getTicketStatus(
        @Url url: String?,
        @Body param: TicketUploadStatusRequest?
    ): Observable<JsonNode?>

    @GET
    fun getTicket(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getCitationLayout(@Url url: String?): Observable<JsonNode?>

    @POST
    fun addTiming(@Url url: String?, @Body param: AddTimingRequest?): Observable<JsonNode?>

    //dummy apis
    @POST
    fun resendOTP(@Url url: String?): Observable<JsonNode?>

    @POST
    fun FetchProfileInfo(@Url url: String?): Observable<JsonNode?>

    @POST
    fun activityLog(
        @Url url: String?,
        @Body param: ActivityUpdateRequest?
    ): Observable<JsonNode?>

    @Multipart
    @POST
    fun uploadImages(
        @Url url: String?,
        @Part("data") mIDList: Array<String>?,
        @Part("upload_type") uploadType: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Observable<JsonNode?>

    @Multipart
    @POST
    fun uploadImagesBC(
        @Url url: String?,
        @Part("data") mIDList: Array<String>?,
        @Part("upload_type") uploadType: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Observable<JsonNode?>

    @Multipart
    @POST
    fun uploadAllImages(
        @Url url: String?,
        @Part("data") mIDList: List<String?>,
        @Part("upload_type") uploadType: RequestBody?,
        @Part image: List<MultipartBody.Part?>
    ): Observable<JsonNode?>

    @Multipart
    @POST("static_file/bulk_upload")
    fun uploadAllImagesInBulk(
        @Part("data") data: RequestBody?,
        @Part("upload_type") uploadType: RequestBody?,
        @Part files: List<MultipartBody.Part?>
    ): Call<ScannedImageUploadResponse>

    @POST
    fun uploadCsv(
        @Url url: String?,
        @Body mIDList: UploadCsvLinksRequest?
    ): Observable<JsonNode?>

    @Multipart
    @POST
    fun staticUploadCsv(
        @Url url: String?,
        @Part("data") mIDList: Array<String?>?,
        @Part("upload_type") uploadType: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Observable<JsonNode?>

    @Multipart
    @POST
    fun UploadDocToVerification(
        @Url url: String?,
        @Query("doc_type") doc_type: RequestBody?,
        @Part front_image: MultipartBody.Part?,
        @Part back_image: MultipartBody.Part?
    ): Observable<JsonNode?>


    @Multipart
    @POST
    fun UpdateDocToVerification(
        @Url url: String?,
        @Part("doc_type") doc_type: RequestBody?,
        @Part front_image: MultipartBody.Part?,
        @Part back_image: MultipartBody.Part?
    ): Observable<JsonNode?>

    @GET
    fun getRoute(@Url url: String?): Observable<JsonNode?>

    @POST
    fun downloadBitmap(
        @Url url: String?,
        @Body param: DownloadBitmapRequest?
    ): Observable<JsonNode?>

    @PATCH
    fun driveOffTvr(@Url url: String?, @Body param: DriveOffTvrRequest?): Observable<JsonNode?>

    @GET
    fun getPayByPlate(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getPayBySpace(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getGenticHitList(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getPayBySpaceDataSet(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getCameraViolationDataSet(@Url url: String?): Observable<JsonNode?>

//     'https://devapi.parkloyalty.com/vehicle-camera-job/get-genetic-hit-list?type_of_hit=Scofflaw&page=1&limit=100' \
    @GET("violations/camera-feed")
    fun getCameraGuidedEnforcement(
        @Query("is_violation") is_violation: Boolean?,
        @Query("time_from") time_from: String?,
        @Query("time_to") time_to: String?,
        @Query("lp_number") plate_number: String?,
        @Query("space_number") space_number: String?,
        @Query("page") page: String?,
        @Query("limit") limit: String?
    ): Observable<JsonNode?>

    //fun getGeneticHitList(@Url url: String?): Observable<JsonNode?>

    //http://localhost:8000/vehicle-camera-job/get-genetic-hit-list?type_of_hit=Scofflaw&page=1&limit=100
    @GET("vehicle-camera-job/get-genetic-hit-list")
    fun getGeneticHitList(
        @Query("type_of_hit") typeOfHit: String?,
        @Query("lp_number") lprNumber: String?,
        @Query("page") page: String?,
        @Query("limit") limit: String?
    ): Observable<JsonNode?>

    @GET
    fun getLastSecondCheck(@Url url: String?): Observable<JsonNode?>

    @POST
    fun checkSimilarCitation(
        @Url url: String?,
        @Body param: SimilarCitationCheckRequest?
    ): Observable<JsonNode?>

    @GET
    fun officerDailySummary(@Url url: String?): Observable<JsonNode?>

    @POST
    fun LprStartSession(
        @Url url: String?,
        @Body param: ContinuousDataObject?
    ): Observable<JsonNode?>

    @POST
    fun LprEndSession(
        @Url url: String?,
        @Body param: LprEndSessionRequest?
    ): Observable<JsonNode?>

    @GET
    fun downloadAlertFile(@Url url: String?): Observable<JsonNode?>

    @POST
    fun AddNotes(@Url url: String?, @Body request: AddNoteRequest?): Observable<JsonNode?>

    @GET
    fun GetNotes(@Url url: String?): Observable<JsonNode?>

    @POST
    fun addImages(
        @Url url: String?,
        @Body mIDList: AddImageRequest?
    ): Observable<JsonNode?>

    @GET
    fun getSupervisor(@Url url: String?): Observable<JsonNode?>

    @GET
    fun getFacsimileImages(@Url url: String?): Observable<JsonNode?>

    @POST
    fun UploadActivityImage(@Url url: String?, @Body request: ActivityImageUploadRequest?): Observable<JsonNode?>

    /*Start of Inventory Management APIs*/
    @GET
    fun getOfficersEquipmentList(@Url url: String?): Observable<JsonNode?>

    @POST
    fun logEquipmentCheckedOut(
        @Url url: String?,
        @Body request: EquipmentCheckInOutRequest?
    ): Observable<JsonNode?>

    @POST
    fun logEquipmentCheckedIn(
        @Url url: String?,
        @Body request: EquipmentCheckInOutRequest?
    ): Observable<JsonNode?>

    @POST
    fun addNoteForNotCheckedInEquipment(
        @Url url: String?,
        @Body request: LogoutNoteForEquipmentRequest?
    ): Observable<JsonNode?>
    /*End of Inventory Management APIs*/

    //Generic function used to download file from full url
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

    /*Municipal Citation*/
    @GET
    fun getMunicipalCitationLayout(@Url url: String?): Observable<JsonNode?>

    @POST
    fun createMunicipalCitationTicket(
        @Url url: String?,
        @Body param: CreateMunicipalCitationTicketRequest?
    ): Observable<JsonNode?>

    @GET
    fun getMunicipalCitationTicketHistory(
        @Url url: String?,
        @QueryMap values: Map<String, String>
    ): Observable<JsonNode?>
    /*Municipal Citation*/

    /**
     * This method is used to create a warning scofflaw ticket from Boot Activity
     */
    @POST
    fun bootInstanceTicketAPI(@Url url: String?, @Body request: BootMetadataRequest?): Observable<JsonNode?>

}