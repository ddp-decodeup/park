package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.ui.allreport.model.AfterSevenPMRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.BikeInspectionsRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.BrokenMeterReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.CurbRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.FullTimeRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.HandHeldMalFunctionsRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.HomelessRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.HourMarkedVehiclesRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.LotCountVioRateRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.LotInspectionRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.NFLRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.NoticeToTowRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.PayStationRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SafetyIssueRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SignOffReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SignReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SignageReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SpecialAssignementRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SupervisorReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.TowReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.TrashLotRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.VehicleInspectionRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.WorkOrderRequest
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.BrokenMeterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("maintenance/checks")
    suspend fun brokenAssetsReport(
        @Body brokenMeterRequest: BrokenMeterRequest?
    ): Response<JsonNode>

    @POST("reports/broken-meter-reports")
    suspend fun brokenMeterReportSubmit(
        @Body brokenMeterRequest: BrokenMeterReportRequest?
    ): Response<JsonNode>

    @POST("reports/sign-off-reports")
    suspend fun signOffReportRequestSubmit(
        @Body signOffReportRequest: SignOffReportRequest?
    ): Response<JsonNode>

    @POST("reports/tow-reports")
    suspend fun towReportRequestSubmit(
        @Body towReportRequest: TowReportRequest?
    ): Response<JsonNode>

    @POST("reports/seventytwo-hour-tow-notice-reports")
    suspend fun noticeToTowReportRequestSubmit(
        @Body noticeToTowRequest: NoticeToTowRequest?
    ): Response<JsonNode>

    @POST("reports/curbs")
    suspend fun curbReportSubmit(
        @Body curbRequest: CurbRequest?
    ): Response<JsonNode>

    @POST("reports/hand-held-malfunctions")
    suspend fun handHeldMalfunctionsReportSubmit(
        @Body handHeldMalfunctionsRequest: HandHeldMalFunctionsRequest?
    ): Response<JsonNode>

    @POST("reports/sign-reports")
    suspend fun signReportSubmit(
        @Body signReportRequest: SignReportRequest?
    ): Response<JsonNode>

    @POST("reports/vehicle-inspections")
    suspend fun vehicleInspectionReportSubmit(
        @Body vehicleInspectionRequest: VehicleInspectionRequest?
    ): Response<JsonNode>

    @POST("reports/seventytwo-hour-marked-vehicles")
    suspend fun seventyTwoHourMarkedVehiclesReportSubmit(
        @Body hourMarkedVehiclesRequest: HourMarkedVehiclesRequest?
    ): Response<JsonNode>

    @POST("reports/bike-inspections")
    suspend fun bikeInspectionReportSubmit(
        @Body bikeInspectionsRequest: BikeInspectionsRequest?
    ): Response<JsonNode>

    @POST("reports/supervisor-reports")
    suspend fun supervisorReportSubmit(
        @Body supervisorReportRequest: SupervisorReportRequest?
    ): Response<JsonNode>

    @POST("reports/special-assignment-reports")
    suspend fun specialAssignmentReportSubmit(
        @Body specialAssignmentRequest: SpecialAssignementRequest?
    ): Response<JsonNode>

    @POST("reports/full-time-reports")
    suspend fun fullTimeReportSubmit(
        @Body fullTimeRequest: FullTimeRequest?
    ): Response<JsonNode>

    @POST("reports/part-time-reports")
    suspend fun partTimeReportSubmit(
        @Body fullTimeRequest: FullTimeRequest?
    ): Response<JsonNode>

    @POST("reports/nfl-special-assignment")
    suspend fun nflSpecialAssignmentReportSubmit(
        @Body nflRequest: NFLRequest?
    ): Response<JsonNode>

    @POST("reports/lot-count-vio-rate-reports")
    suspend fun lotCountVioRateReportSubmit(
        @Body lotCountVioRateRequest: LotCountVioRateRequest?
    ): Response<JsonNode>

    @POST("reports/hard-summer-festival")
    suspend fun hardSummerFestivalReportSubmit(
        @Body nflRequest: NFLRequest?
    ): Response<JsonNode>

    @POST("reports/after-seven-reports")
    suspend fun afterSevenPMReportSubmit(
        @Body afterSevenPmRequest: AfterSevenPMRequest?
    ): Response<JsonNode>

    @POST("reports/pay-station-reports")
    suspend fun payStationReportSubmit(
        @Body payStationRequest: PayStationRequest?
    ): Response<JsonNode>

    @POST("reports/signage-reports")
    suspend fun paySignageReportSubmit(
        @Body signageReportRequest: SignageReportRequest?
    ): Response<JsonNode>

    @POST("reports/homeless-reports")
    suspend fun homeLessReportSubmit(
        @Body homelessRequest: HomelessRequest?
    ): Response<JsonNode>

    @POST("reports/work-order-reports")
    suspend fun workOrderReportSubmit(
        @Body workOrderRequest: WorkOrderRequest?
    ): Response<JsonNode>

    @POST("reports/safety-reports")
    suspend fun safetyIssueReportSubmit(
        @Body safetyIssueRequest: SafetyIssueRequest?
    ): Response<JsonNode>

    @POST("reports/trash-lot-maintenance-reports")
    suspend fun trashLotReportSubmit(
        @Body trashLotRequest: TrashLotRequest?
    ): Response<JsonNode>

    @POST("reports/lot-inspection-reports")
    suspend fun lotInspectionReportSubmit(
        @Body lotInspectionRequest: LotInspectionRequest?
    ): Response<JsonNode>

}