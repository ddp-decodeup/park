package com.parkloyalty.lpr.scan.network

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
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
import com.parkloyalty.lpr.scan.ui.login.model.ActivityUpdateRequest
import com.parkloyalty.lpr.scan.ui.login.model.ForgetPasswordRequest
import com.parkloyalty.lpr.scan.ui.login.model.SiteOfficerLoginRequest
import com.parkloyalty.lpr.scan.ui.login.model.UpdateSiteOfficerRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import io.reactivex.Observable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Service @Inject constructor(private val networkAPIServices: NetworkAPIServices) {
    fun executeLoginAPI(loginRequest: SiteOfficerLoginRequest?): Observable<JsonNode?> {
        return networkAPIServices.login(DynamicAPIPath.POST_LOGIN, loginRequest)
    }

    fun executeGetCountAPI(): Observable<JsonNode?> {
        return networkAPIServices.getCount(DynamicAPIPath.GET_COUNT)
    }

    fun executeEventLoginAPI(loginRequest: UserEventRequest?): Observable<JsonNode?> {
        return networkAPIServices.eventLogin(DynamicAPIPath.POST_LOGIN, loginRequest)
    }

    fun executePushEventAPI(pushEventRequest: PushEventRequest?): Observable<JsonNode?> {
        return networkAPIServices.pushEvent(DynamicAPIPath.POST_PUSH_EVENT, pushEventRequest)
    }

    fun executeLocationUpdateAPI(pushEventRequest: LocUpdateRequest?): Observable<JsonNode?> {
        return networkAPIServices.locationUpdate(DynamicAPIPath.POST_EVENT_LOGGER, pushEventRequest)
    }
    fun executeInactiveMeterBuzzerAPI(pushEventRequest: InactiveMeterBuzzerRequest?): Observable<JsonNode?> {
        return networkAPIServices.inactiveMeterBuzzer(DynamicAPIPath.POST_INACTIVE_METER_BUZZER, pushEventRequest)
    }
    fun executeInventoryAPI(inventoryRequest: InventoryRequest?): Observable<JsonNode?> {
        return networkAPIServices.inactiventory(DynamicAPIPath.POST_INVENTORY, inventoryRequest)
    }

    fun executeWelcomeAPI(): Observable<JsonNode?> {
        return networkAPIServices.welcome(DynamicAPIPath.GET_WELCOME)
    }

    fun executeUpdateTimeAPI(): Observable<JsonNode?> {
        return networkAPIServices.updateTime(DynamicAPIPath.GET_UPDATE_TIME)
    }

    fun executeCheckSetupAPI(): Observable<JsonNode?> {
        return networkAPIServices.checkSetup(DynamicAPIPath.GET_CHECK_SETUP)
    }

    fun executeDashboardAPI(): Observable<JsonNode?> {
        return networkAPIServices.dashboard(DynamicAPIPath.GET_DASHBOARD)
    }

    fun executeUpdateSiteOfficerAPI(updateSiteOfficerRequest: UpdateSiteOfficerRequest?): Observable<JsonNode?> {
        return networkAPIServices.updateSiteOfficer(
            DynamicAPIPath.POST_UPDATE_SITE_OFFICER,
            updateSiteOfficerRequest
        )
    }

    fun executeForgetPassAPI(forgetPasswordRequest: ForgetPasswordRequest?): Observable<JsonNode?> {
        return networkAPIServices.forgetPassword(
            DynamicAPIPath.POST_FORGOT_PASSWORD,
            forgetPasswordRequest
        )
    }

    fun executeGetTimingAPI(timingRequest: TimingRequest?): Observable<JsonNode?> {
        return networkAPIServices.getTiming(DynamicAPIPath.GET_TIMING, timingRequest)
    }

    fun executeGetExemptAPI(exemptRequest: ExemptRequest?): Observable<JsonNode?> {
        return networkAPIServices.getExempt(DynamicAPIPath.GET_EXEMPT, exemptRequest)
    }

    fun executeGetSofflawAPI(sofflawRequest: SofflawRequest?): Observable<JsonNode?> {
        return networkAPIServices.getSofflaw(DynamicAPIPath.GET_SOFFLAW, sofflawRequest)
    }

    fun executeGetPermitAPI(permitRequest: PermitRequest?): Observable<JsonNode?> {
        return networkAPIServices.getPermit(DynamicAPIPath.GET_PERMIT, permitRequest)
    }

    fun executeTimingMarkBulkAPI(markRequest: TimingMarkBulkRequest?): Observable<JsonNode?> {
        return networkAPIServices.getTimingMarkBulk(DynamicAPIPath.PATCH_TIMING_MARK_BULK, markRequest)
    }

    fun executeDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?): Observable<JsonNode?> {
        return networkAPIServices.getDataFromLpr(
            DynamicAPIPath.GET_DATA_FROM_LPR,
            dataFromLprRequest
        )
    }

    fun executeTimingMarkAPI(dataFromLprRequest: String): Observable<JsonNode?> {
        return networkAPIServices.getTimingMark(DynamicAPIPath.GET_TIMING_MARK + dataFromLprRequest)
    }
    fun executeGenetecHitAPI(dataFromLprRequest: String): Observable<JsonNode?> {
        return networkAPIServices.getGenetecHit(DynamicAPIPath.GET_TIMING_MARK + dataFromLprRequest)
    }
    fun executeAbandonedHitAPI(dataFromLprRequest: String): Observable<JsonNode?> {
        return networkAPIServices.getAbandonedHit(DynamicAPIPath.GET_abandoned_HIT + dataFromLprRequest)
    }

    fun executeUpdateTicketAPI(
        updateMarkRequest: UpdateTicketRequest?,
        id: String
    ): Observable<JsonNode?> {
        return networkAPIServices.updateTicket(
            DynamicAPIPath.POST_UPDATE_TICKET + id,
            updateMarkRequest
        )
    }

    fun executeUpdateMarkAPI(
        updateMarkRequest: UpdateMarkRequest?,
        id: String
    ): Observable<JsonNode?> {
        return networkAPIServices.updateMark(
            DynamicAPIPath.POST_UPDATE_MARK + id,
            updateMarkRequest
        )
    }

    fun executeLprScanLoggerAPI(dataFromLprRequest: LprScanLoggerRequest?): Observable<JsonNode?> {
        return networkAPIServices.LprScanLogger(
            DynamicAPIPath.POST_LPR_SCAN_LOGGER,
            dataFromLprRequest
        )
    }

    fun executeGetMeterAPI(meterRequest: MeterRequest?): Observable<JsonNode?> {
        return networkAPIServices.getMeter(DynamicAPIPath.GET_METER, meterRequest)
    }

    //PHASE 2 APIS--------------------------->
    fun executeCitationNumberAPI(citationNumberRequest: CitationNumberRequest?): Observable<JsonNode?> {
        return networkAPIServices.getCitationNumber(
            DynamicAPIPath.POST_CITATION_NUMBER,
            citationNumberRequest
        )
    }

    fun executeCitationDatasetAPI(dropdownDatasetRequest: DropdownDatasetRequest?): Observable<JsonNode?> {
        return networkAPIServices.getCitationDataset(
            DynamicAPIPath.POST_CITATION_DATASET,
            dropdownDatasetRequest
        )
    }

    fun executeAuthTokenRefreshAPI(): Observable<JsonNode?> {
        return networkAPIServices.getAuthTokenRefresh(
            DynamicAPIPath.GET_REFRESH_AUTH_TOKEN
        )
    }

    fun executeCitationDatasetAPILoginPage(dropdownDatasetRequest: DropdownDatasetRequest?): Observable<JsonNode?> {
        return networkAPIServices.getCitationDataset(
            DynamicAPIPath.POST_CITATION_DATASET_LOGIN_PAGE,
            dropdownDatasetRequest
        )
    }

    fun executeCitationLayoutAPI(path: String?): Observable<JsonNode?> {
        return networkAPIServices.getCitationLayout(path)
    }

    fun executeMunicipalCitationLayoutAPI(path: String?): Observable<JsonNode?> {
        return networkAPIServices.getMunicipalCitationLayout(path)
    }

    fun executeCreateTicketAPI(request: CreateTicketRequest?): Observable<JsonNode?> {
        return networkAPIServices.createTicket(DynamicAPIPath.POST_CREATE_TICKET, request)
    }

    fun executeBootAPI(request: BootRequest?): Observable<JsonNode?> {
        return networkAPIServices.BootSubmit(DynamicAPIPath.SUMIT_BOOT, request)
    }

    /**
     * This method is used to create a warning scofflaw ticket from Boot Activity
     */
    fun executeBootInstanceTicketAPI(request: BootMetadataRequest?): Observable<JsonNode?> {
        return networkAPIServices.bootInstanceTicketAPI(
            DynamicAPIPath.SUBMIT_BOOT_INSTANCE_TICKET,
            request
        )
    }

    fun executeBrokenMeterAPI(request: BrokenMeterRequest?): Observable<JsonNode?> {
        return networkAPIServices.BrokenMeterSubmit(DynamicAPIPath.BROKEN_METER, request)
    }

    fun executeBrokenMeterReportAPI(request: BrokenMeterReportRequest?): Observable<JsonNode?> {
        return networkAPIServices.BrokenMeterReportSubmit(DynamicAPIPath.POST_BROKEN_METER_REPORT, request)
    }
    fun executeSignOffReportAPI(request: SignOffReportRequest?): Observable<JsonNode?> {
        return networkAPIServices.SignOffReportRequestSubmit(DynamicAPIPath.POST_SIGN_OFF_REPORT, request)
    }
    fun executeTowReportAPI(request: TowReportRequest?): Observable<JsonNode?> {
        return networkAPIServices.TowReportRequestSubmit(DynamicAPIPath.POST_TOW_REPORT, request)
    }
    fun executeNoticeToTowReportAPI(request: NoticeToTowRequest?): Observable<JsonNode?> {
        return networkAPIServices.NoticeToTowReportRequestSubmit(DynamicAPIPath.POST_NOTICE_TO_TOW_REPORT, request)
    }

    fun executeCurbReportAPI(request: CurbRequest?): Observable<JsonNode?> {
        return networkAPIServices.CurbReportSubmit(DynamicAPIPath.POST_CURB_REPORT, request)
    }

    fun executeFullTimeReportAPI(request: FullTimeRequest?): Observable<JsonNode?> {
        return networkAPIServices.FUllTimeReportSubmit(DynamicAPIPath.POST_FULLTIME_REPORT, request)
    }

    fun executePartTimeReportAPI(request: FullTimeRequest?): Observable<JsonNode?> {
        return networkAPIServices.PartTimeReportSubmit(DynamicAPIPath.POST_PARTTIME_REPORT, request)
    }

    fun executeHandHeldMalFunctionsReportAPI(request: HandHeldMalFunctionsRequest?): Observable<JsonNode?> {
        return networkAPIServices.HandHeldMalFunctionsReportSubmit(DynamicAPIPath.POST_HAND_HELD_MALFUNCTIONS_REPORT, request)
    }

    fun executeSignReportAPI(request: SignReportRequest?): Observable<JsonNode?> {
        return networkAPIServices.signReportSubmit(DynamicAPIPath.POST_SIGN_REPORT, request)
    }

    fun executeVehicleInspectionReportAPI(request: VehicleInspectionRequest?): Observable<JsonNode?> {
        return networkAPIServices.vehicleInspectionReportSubmit(DynamicAPIPath.POST_VEHICLE_INSPECTIONS_REPORT, request)
    }

    fun executeHourMarkedVehiclesReportAPI(request: HourMarkedVehiclesRequest?): Observable<JsonNode?> {
        return networkAPIServices.hourMarkedVehiclesReportSubmit(DynamicAPIPath.POST_HOUR_MARKED_VEHICLE_REPORT, request)
    }

    fun executeBikeInspectionReportAPI(request: BikeInspectionsRequest?): Observable<JsonNode?> {
        return networkAPIServices.bikeInspectionReportSubmit(DynamicAPIPath.POST_BIKE_INSPECTIONS_REPORT, request)
    }

    fun executeSupervisorReportAPI(request: SupervisorReportRequest?): Observable<JsonNode?> {
        return networkAPIServices.supervisorReportSubmit(DynamicAPIPath.POST_SUPERVISOR_REPORT, request)
    }
    fun executeSpecialAssignmentReportAPI(request: SpecialAssignementRequest?): Observable<JsonNode?> {
        return networkAPIServices.specialAssignmentReportSubmit(DynamicAPIPath.POST_SPECIAL_ASSIGNMENT_REPORT, request)
    }
    fun executeNFLReportAPI(request: NFLRequest?): Observable<JsonNode?> {
        return networkAPIServices.nflSpecialAssignmentReportSubmit(DynamicAPIPath.POST_NFL_REPORT, request)
    }
    fun executeLotCountVioRateReportAPI(request: LotCountVioRateRequest?): Observable<JsonNode?> {
        return networkAPIServices.lotCountVioRateReportSubmit(DynamicAPIPath.POST_LOT_COUNT_VIO_RATE_REPORT, request)
    }

    fun executeHardSummerReportAPI(request: NFLRequest?): Observable<JsonNode?> {
        return networkAPIServices.hardSummerFestivalReportSubmit(DynamicAPIPath.POST_HARD_SUMMER_REPORT, request)
    }
    fun executeAfterSevenReportAPI(request: AfterSevenPMRequest?): Observable<JsonNode?> {
        return networkAPIServices.afterSevenPMReportSubmit(DynamicAPIPath.POST_AFTER_SEVEN_REPORT, request)
    }
    fun executePayStationReportAPI(request: PayStationRequest?): Observable<JsonNode?> {
        return networkAPIServices.payStationReportSubmit(DynamicAPIPath.POST_PAY_STATION_REPORT, request)
    }
    fun executeSignageReportAPI(request: SignageReportRequest?): Observable<JsonNode?> {
        return networkAPIServices.paySignageReportSubmit(DynamicAPIPath.POST_SIGNAGE_REPORT, request)
    }
    fun executeHomelessReportAPI(request: HomelessRequest?): Observable<JsonNode?> {
        return networkAPIServices.homeLessReportSubmit(DynamicAPIPath.POST_HOMELESS_REPORT, request)
    }
    fun executeWorkOrderReportAPI(request: WorkOrderRequest?): Observable<JsonNode?> {
        return networkAPIServices.workOrderReportSubmit(DynamicAPIPath.POST_WORK_ORDER_REPORT, request)
    }
    fun executeSafetyIssueReportAPI(request: SafetyIssueRequest?): Observable<JsonNode?> {
        return networkAPIServices.safetyIssueReportSubmit(DynamicAPIPath.POST_SAFETY_ISSUE_REPORT, request)
    }
    fun executeTrashLotReportAPI(request: TrashLotRequest?): Observable<JsonNode?> {
        return networkAPIServices.trashLotReportSubmit(DynamicAPIPath.POST_TRACE_LOT_REPORT, request)
    }
    fun executeLotInspectionReportAPI(request: LotInspectionRequest?): Observable<JsonNode?> {
        return networkAPIServices.LotInspectionReportSubmit(DynamicAPIPath.POST_LOT_INSPECTION_REPORT, request)
    }

    fun executeTicketCancelAPI(
        request: TicketCancelRequest?,
        id: String
    ): Observable<JsonNode?> {
        return networkAPIServices.cancelTicket(DynamicAPIPath.POST_CANCEL + id, request)
    }

    fun executeTicketCancellationRequestAPI(
        request: TicketCancellationRequest?,
        id: String
    ): Observable<JsonNode?> {
        return networkAPIServices.cancellationTicket(DynamicAPIPath.POST_CANCELLATION_REQUEST, request)
    }
    fun executeTicketStatusAPI(
        request: TicketUploadStatusRequest?,
        id: String
    ): Observable<JsonNode?> {
        return networkAPIServices.getTicketStatus(DynamicAPIPath.POST_TICKET_UPLOADE_STATUS_META, request)
    }

    fun executeActivityCountAPI(request: String): Observable<JsonNode?> {
        return networkAPIServices.activityCount(DynamicAPIPath.POST_ACTIVITY_UPDATES + request)
    }

    fun executeViolationCountAPI(request: String): Observable<JsonNode?> {
        return networkAPIServices.violationCount(DynamicAPIPath.POST_VIOLATION_COUNT + request)
    }

    fun executeBarCountAPI(request: String): Observable<JsonNode?> {
        return networkAPIServices.getBarCount(DynamicAPIPath.POST_GET_BAR_COUNT + request)
    }

    fun executeCountLineAPI(shift: String): Observable<JsonNode?> {
        return networkAPIServices.getLineCount(DynamicAPIPath.POST_GET_COUNT_LINE + shift)
    }

    fun getTicketAPI(mLprNum: String): Observable<JsonNode?> {
        return networkAPIServices.getTicket(DynamicAPIPath.GET_TICKET + mLprNum)
    }

    fun executeAddTimingAPI(request: AddTimingRequest?): Observable<JsonNode?> {
        return networkAPIServices.addTiming(DynamicAPIPath.POST_ADD_TIMING, request)
    }

    fun executeActivityLogAPI(request: ActivityUpdateRequest?): Observable<JsonNode?> {
        return networkAPIServices.activityLog(DynamicAPIPath.POST_ACTIVITY_LOG, request)
    }

    fun executeUploadImagesAPI(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ): Observable<JsonNode?> {
        return networkAPIServices.uploadImages(
            DynamicAPIPath.POST_IMAGE,
            mIDList,
            mRequestBodyType,
            image
        )
    }

    fun executeUploadTextAPI(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ): Observable<JsonNode?> {
        return networkAPIServices.uploadImages(
            DynamicAPIPath.POST_TEXT,
            mIDList,
            mRequestBodyType,
            image
        )
    }
    fun executeUploadImagesAPIBC(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ): Observable<JsonNode?> {
        return networkAPIServices.uploadImagesBC(
            DynamicAPIPath.POST_IMAGE,
            mIDList,
            mRequestBodyType,
            image
        )
    }

    fun executeUploadAllImagesAPI(
        mIDList: List<String?>, mRequestBodyType: RequestBody?, image: List<MultipartBody.Part?>
    ): Observable<JsonNode?> {
        return networkAPIServices.uploadAllImages(
            DynamicAPIPath.POST_IMAGE,
            mIDList,
            mRequestBodyType,
            image
        )
    }

    fun executeUploadCsvAPI(mIDList: UploadCsvLinksRequest?): Observable<JsonNode?> {
        return networkAPIServices.uploadCsv(DynamicAPIPath.POST_CSV, mIDList)
    }

    fun executeStaticUploadCsvAPI(
        mIDList: Array<String?>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ): Observable<JsonNode?> {
        return networkAPIServices.staticUploadCsv(
            DynamicAPIPath.POST_IMAGE,
            mIDList,
            mRequestBodyType,
            image
        )
    }

    fun executeUploadDocToVerification(
        doc_type: RequestBody?,
        front_image: MultipartBody.Part?,
        back_image: MultipartBody.Part?
    ): Observable<JsonNode?> {
        return networkAPIServices.UploadDocToVerification(
            DynamicAPIPath.document_upload,
            doc_type,
            front_image,
            back_image
        )
    }


    fun executeUpdateDocToVerification(
        doc_type: RequestBody?,
        front_image: MultipartBody.Part?,
        back_image: MultipartBody.Part?
    ): Observable<JsonNode?> {
        return networkAPIServices.UpdateDocToVerification(
            DynamicAPIPath.DOCUMENT_UPDATE,
            doc_type,
            front_image,
            back_image
        )
    }

    fun executeRouteAPI(request: String): Observable<JsonNode?> {
        return networkAPIServices.getRoute(DynamicAPIPath.GET_ROUTE_DATA + request)
    }

    fun executeDownloadBitmapAPI(request: DownloadBitmapRequest?): Observable<JsonNode?> {
        return networkAPIServices.downloadBitmap(DynamicAPIPath.POST_DOWNLOAD_FILE, request)
    }

    fun executeDriveOffTvrAPI(
        request: DriveOffTvrRequest?,
        mTicketNumber: String
    ): Observable<JsonNode?> {
        return networkAPIServices.driveOffTvr(
            DynamicAPIPath.PATCH_DRIVE_OFF_TVR + mTicketNumber,
            request
        )
    }

    fun executePayByPlateAPI(endPoint: String): Observable<JsonNode?> {
        return networkAPIServices.getPayByPlate(DynamicAPIPath.GET_PAY_BY_PLATE + endPoint)
    }

    fun executePayBySpaceAPI(endPoint: String): Observable<JsonNode?> {
        return networkAPIServices.getPayBySpace(DynamicAPIPath.GET_PAY_BY_SPACE + endPoint)
    }

    fun executeGeneticHitListAPI(endPoint: String): Observable<JsonNode?> {
        return networkAPIServices.getGenticHitList(DynamicAPIPath.GET_GENETIC_HIT_LIST + endPoint)
    }

    fun executePayBySpaceDataSetAPI(endPoint: String): Observable<JsonNode?> {
        return networkAPIServices.getPayBySpaceDataSet(DynamicAPIPath.GET_PAY_BY_SPACE_DATA_SET + endPoint)
    }

    fun executeCameraViolationDataSetAPI(endPoint: String): Observable<JsonNode?> {
        return networkAPIServices.getCameraViolationDataSet(DynamicAPIPath.GET_CAMERA_VIOLATION_DATA_SET + endPoint)
    }

    fun executeCameraGuidedEnforcementAPI(is_violation: Boolean, time_from:String,time_to:String,
                                          plate_number:String,space_number:String,page:String, limit:String): Observable<JsonNode?> {
        return networkAPIServices.getCameraGuidedEnforcement(
            is_violation = is_violation,
            time_from = time_from,
            time_to = time_to,
            plate_number=plate_number,
            space_number=space_number,
            page = page,
            limit = limit
        )
//        return networkAPIServices.getCameraGuidedEnforcement(DynamicAPIPath.GET_CAMERA_VIOLATION_DATA_SET + endPoint)
    }

    fun executeGetGeneticHitListAPI(typeOfHit: String, lprNumber:String,page:String, limit:String): Observable<JsonNode?> {
    //fun executeGetGeneticHitListAPI(endPoint: String): Observable<JsonNode?> {
        //return networkAPIServices.getGeneticHitList(DynamicAPIPath.GET_GENETIC_HIT_LIST + endPoint)
        return networkAPIServices.getGeneticHitList(
            typeOfHit = typeOfHit,
            lprNumber = lprNumber,
            page = page,
            limit = limit
        )
    }

    fun executeLastSecondCheckAPI(endPoint: String): Observable<JsonNode?> {
        return networkAPIServices.getLastSecondCheck(DynamicAPIPath.GET_LAST_SECOND_CHECK + endPoint)
    }

    fun executeSimilarCitationCheckAPI(checkRequest: SimilarCitationCheckRequest?): Observable<JsonNode?> {
        return networkAPIServices.checkSimilarCitation(
            DynamicAPIPath.SIMILAR_CITATION_CHECK,
            checkRequest
        )
    }

    fun executeOfficerDailySummaryAPI(shift: String): Observable<JsonNode?> {
        return networkAPIServices.officerDailySummary(DynamicAPIPath.GET_OFFICER_DAILY_SUMMARY + shift)
    }

    fun executeLprStartSessionAPI(request: ContinuousDataObject?): Observable<JsonNode?> {
        return networkAPIServices.LprStartSession(DynamicAPIPath.POST_LPR_START_SESSION, request)
    }

    fun executeLprEndSessionAPI(request: LprEndSessionRequest?): Observable<JsonNode?> {
        return networkAPIServices.LprEndSession(DynamicAPIPath.POST_LPR_END_SESSION, request)
    }

    fun executeDownloadAlertFileAPI(): Observable<JsonNode?> {
        return networkAPIServices.downloadAlertFile(DynamicAPIPath.GET_DOWNLOAD_ALERT_FILE)
    }

    fun executeAddNotesAPI(
        endPoint: String,
        addNoteRequest: AddNoteRequest?
    ): Observable<JsonNode?> {
        return networkAPIServices.AddNotes(DynamicAPIPath.GET_ADD_NOTES + endPoint, addNoteRequest)
    }

    fun executeGetNotesAPI(endPoint: String): Observable<JsonNode?> {
        return networkAPIServices.GetNotes(DynamicAPIPath.GET_NOTES + endPoint)
    }

    fun executeAddImagesAPI(
        mIDList: AddImageRequest?, endPoint: String
    ): Observable<JsonNode?> {
        return networkAPIServices.addImages(DynamicAPIPath.POST_ADD_IMAGE + endPoint, mIDList)
    }

    fun executeSupervisorAPI(endPoint: String): Observable<JsonNode?> {
        return networkAPIServices.getSupervisor(DynamicAPIPath.GET_SUPERVISOR + endPoint)
    }

    fun executeUploadAllImagesAPI(
        data: Array<String?>,
        files: List<MultipartBody.Part?>,
        imageUploadType : String
    ): Call<ScannedImageUploadResponse> {
        val mRequestBodyType = RequestBody.create("text/plain".toMediaTypeOrNull(), imageUploadType)

        //This is the sample for it
        //val mRequestBodyImages = RequestBody.create(MediaType.parse("text/plain"), "['ABC7261_287648.jpg','BCD3657_36723687263']")
        val mRequestBodyImages = RequestBody.create("text/plain".toMediaTypeOrNull(), ObjectMapperProvider.instance.writeValueAsString(data))

        return networkAPIServices.uploadAllImagesInBulk(
            mRequestBodyImages,
            mRequestBodyType,
            files
        )
    }

    fun executeFacsimileImageAPI(endPoint: String?): Observable<JsonNode?> {
        return networkAPIServices.getFacsimileImages(
                DynamicAPIPath.GET_STATIC_FASCIMILE_IMAGE + endPoint
        )
    }

    fun executeActivityImagesUploadAPI(
        mIDList: ActivityImageUploadRequest?
    ): Observable<JsonNode?> {
        return networkAPIServices.UploadActivityImage(
            DynamicAPIPath.POST_UPDATE_ACTIVITY_IMAGE,
            mIDList
        )
    }

    fun getOfficersEquipmentList(): Observable<JsonNode?> {
        return networkAPIServices.getOfficersEquipmentList(DynamicAPIPath.GET_OFFICERS_EQUIPMENTS)
    }

    fun logEquipmentCheckedOut(equipmentCheckInOutRequest: EquipmentCheckInOutRequest?): Observable<JsonNode?> {
        return networkAPIServices.logEquipmentCheckedOut(
            DynamicAPIPath.POST_EQUIPMENT_CHECK_OUT,
            equipmentCheckInOutRequest
        )
    }

    fun logEquipmentCheckedIn(equipmentCheckInOutRequest: EquipmentCheckInOutRequest?): Observable<JsonNode?> {
        return networkAPIServices.logEquipmentCheckedIn(
            DynamicAPIPath.POST_EQUIPMENT_CHECK_IN,
            equipmentCheckInOutRequest
        )
    }

    fun addNoteForNotCheckedInEquipment(logoutNoteForEquipmentRequest: LogoutNoteForEquipmentRequest?): Observable<JsonNode?> {
        return networkAPIServices.addNoteForNotCheckedInEquipment(
            DynamicAPIPath.POST_LOG_NOTE_BEFORE_LOGOUT_WITH_EQUIPMENT,
            logoutNoteForEquipmentRequest
        )
    }

    /**
     * Generic function used to download any file wit full URL
     */
    fun downloadFile(url: String, callback: (Response<ResponseBody>?, Throwable?) -> Unit) {
        networkAPIServices.downloadFile(url).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                callback(response, null)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(null, t)
            }
        })
    }

    /*Start of Municipal Citation*/
    fun executeCreateMunicipalCitationTicketAPI(request: CreateMunicipalCitationTicketRequest?): Observable<JsonNode?> {
        return networkAPIServices.createMunicipalCitationTicket(DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET, request)
    }

    fun executeGetMunicipalCitationTicketHistoryAPI(values: Map<String, String>): Observable<JsonNode?> {
        return networkAPIServices.getMunicipalCitationTicketHistory(
            DynamicAPIPath.GET_MUNICIPAL_CITATION_TICKET_HISTORY,
            values
        )
    }

}