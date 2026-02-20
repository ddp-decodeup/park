package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.ReportService
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
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_72_HOUR_MARKED_VEHICLE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_AFTER_SEVEN_PM_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_BIKE_INSPECTION
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_BROKEN_METER_REPORT_SUBMIT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_BROKEN_ASSETS_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CURB_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_FULL_TIME
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_HANDHELD_MALFUNCTIONS
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_HARD_SUMMER_FESTIVAL
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_HOMELESS_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOT_COUNT_VIO_RATE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOT_INSPECTION
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_NFL_SPECIAL_ASSIGNMENT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_NOTICE_TO_TOW_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_PART_TIME
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_PAY_SIGNAGE_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_PAY_STATION_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_SAFETY_ISSUE_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_SIGN_OFF_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_SIGN_REPORTS
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_SPECIAL_ASSIGNMENT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_SUPERVISOR
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_TOW_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_TRASH_LOT_REPORT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_VEHICLE_INSPECTION
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_WORK_ORDER_REPORT
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ReportServiceRepository @Inject constructor(
    private val reportService: ReportService
) {
    suspend fun brokenAssetsReport(brokenMeterRequest: BrokenMeterRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_BROKEN_ASSETS_REPORT,
            call = { reportService.brokenAssetsReport(brokenMeterRequest = brokenMeterRequest) })

    suspend fun brokenMeterReportSubmit(brokenMeterRequest: BrokenMeterReportRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_BROKEN_METER_REPORT_SUBMIT,
            call = { reportService.brokenMeterReportSubmit(brokenMeterRequest = brokenMeterRequest) })

    suspend fun signOffReportRequestSubmit(signOffReportRequest: SignOffReportRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_SIGN_OFF_REPORT,
            call = { reportService.signOffReportRequestSubmit(signOffReportRequest = signOffReportRequest) })

    suspend fun towReportRequestSubmit(towReportRequest: TowReportRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_TOW_REPORT,
            call = { reportService.towReportRequestSubmit(towReportRequest = towReportRequest) })

    suspend fun noticeToTowReportRequestSubmit(noticeToTowRequest: NoticeToTowRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_NOTICE_TO_TOW_REPORT,
            call = { reportService.noticeToTowReportRequestSubmit(noticeToTowRequest = noticeToTowRequest) })

    suspend fun curbReportSubmit(curbRequest: CurbRequest?): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_CURB_REPORT,
        call = { reportService.curbReportSubmit(curbRequest = curbRequest) })

    suspend fun handHeldMalfunctionsReportSubmit(handHeldMalfunctionsRequest: HandHeldMalFunctionsRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_HANDHELD_MALFUNCTIONS,
            call = { reportService.handHeldMalfunctionsReportSubmit(handHeldMalfunctionsRequest = handHeldMalfunctionsRequest) })

    suspend fun signReportSubmit(signReportRequest: SignReportRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_SIGN_REPORTS,
            call = { reportService.signReportSubmit(signReportRequest = signReportRequest) })

    suspend fun vehicleInspectionReportSubmit(vehicleInspectionRequest: VehicleInspectionRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_VEHICLE_INSPECTION,
            call = { reportService.vehicleInspectionReportSubmit(vehicleInspectionRequest = vehicleInspectionRequest) })

    suspend fun seventyTwoHourMarkedVehiclesReportSubmit(hourMarkedVehiclesRequest: HourMarkedVehiclesRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_72_HOUR_MARKED_VEHICLE,
            call = {
                reportService.seventyTwoHourMarkedVehiclesReportSubmit(hourMarkedVehiclesRequest = hourMarkedVehiclesRequest)
            })

    suspend fun bikeInspectionReportSubmit(bikeInspectionsRequest: BikeInspectionsRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_BIKE_INSPECTION,
            call = { reportService.bikeInspectionReportSubmit(bikeInspectionsRequest = bikeInspectionsRequest) })

    suspend fun supervisorReportSubmit(supervisorReportRequest: SupervisorReportRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_SUPERVISOR,
            call = { reportService.supervisorReportSubmit(supervisorReportRequest = supervisorReportRequest) })

    suspend fun specialAssignmentReportSubmit(specialAssignmentRequest: SpecialAssignementRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_SPECIAL_ASSIGNMENT,
            call = { reportService.specialAssignmentReportSubmit(specialAssignmentRequest = specialAssignmentRequest) })

    suspend fun fullTimeReportSubmit(fullTimeRequest: FullTimeRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_FULL_TIME,
            call = { reportService.fullTimeReportSubmit(fullTimeRequest = fullTimeRequest) })

    suspend fun partTimeReportSubmit(fullTimeRequest: FullTimeRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_PART_TIME,
            call = { reportService.partTimeReportSubmit(fullTimeRequest = fullTimeRequest) })

    suspend fun nflSpecialAssignmentReportSubmit(nflRequest: NFLRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_NFL_SPECIAL_ASSIGNMENT,
            call = { reportService.nflSpecialAssignmentReportSubmit(nflRequest = nflRequest) })

    suspend fun lotCountVioRateReportSubmit(lotCountVioRateRequest: LotCountVioRateRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_LOT_COUNT_VIO_RATE,
            call = { reportService.lotCountVioRateReportSubmit(lotCountVioRateRequest = lotCountVioRateRequest) })

    suspend fun hardSummerFestivalReportSubmit(nflRequest: NFLRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_HARD_SUMMER_FESTIVAL,
            call = { reportService.hardSummerFestivalReportSubmit(nflRequest = nflRequest) })

    suspend fun afterSevenPmReportSubmit(afterSevenPmRequest: AfterSevenPMRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_AFTER_SEVEN_PM_REPORT,
            call = { reportService.afterSevenPMReportSubmit(afterSevenPmRequest = afterSevenPmRequest) })


    suspend fun payStationReportSubmit(payStationRequest: PayStationRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_PAY_STATION_REPORT,
            call = { reportService.payStationReportSubmit(payStationRequest = payStationRequest) })


    suspend fun paySignageReportSubmit(signageReportRequest: SignageReportRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_PAY_SIGNAGE_REPORT,
            call = { reportService.paySignageReportSubmit(signageReportRequest = signageReportRequest) })


    suspend fun homeLessReportSubmit(homelessRequest: HomelessRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_HOMELESS_REPORT,
            call = { reportService.homeLessReportSubmit(homelessRequest = homelessRequest) })


    suspend fun workOrderReportSubmit(workOrderRequest: WorkOrderRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_WORK_ORDER_REPORT,
            call = { reportService.workOrderReportSubmit(workOrderRequest = workOrderRequest) })


    suspend fun safetyIssueReportSubmit(safetyIssueRequest: SafetyIssueRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_SAFETY_ISSUE_REPORT,
            call = { reportService.safetyIssueReportSubmit(safetyIssueRequest = safetyIssueRequest) })


    suspend fun trashLotReportSubmit(trashLotRequest: TrashLotRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_TRASH_LOT_REPORT,
            call = { reportService.trashLotReportSubmit(trashLotRequest = trashLotRequest) })


    suspend fun lotInspectionReportSubmit(lotInspectionRequest: LotInspectionRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_LOT_INSPECTION,
            call = { reportService.lotInspectionReportSubmit(lotInspectionRequest = lotInspectionRequest) })


}