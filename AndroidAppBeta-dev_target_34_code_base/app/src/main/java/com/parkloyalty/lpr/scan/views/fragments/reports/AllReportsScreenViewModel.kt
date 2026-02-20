package com.parkloyalty.lpr.scan.views.fragments.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.database.NewSingletonDataSet
import com.parkloyalty.lpr.scan.database.repository.ActivityDaoRepository
import com.parkloyalty.lpr.scan.network.repository.MediaServiceRepository
import com.parkloyalty.lpr.scan.network.repository.ReportServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
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
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AllReportsScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val reportServiceRepository: ReportServiceRepository,
    private val mediaServiceRepository: MediaServiceRepository,
    private val activityDaoRepository: ActivityDaoRepository,
    private val singletonDataSet: NewSingletonDataSet
) : ViewModel() {

    //Broken Meter Report Response StateFlow
    private val _brokenMeterReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val brokenMeterReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _brokenMeterReportResponse.asStateFlow()

    //Curb Report Response StateFlow
    private val _curbReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val curbReportResponse: StateFlow<NewApiResponse<JsonNode>> = _curbReportResponse.asStateFlow()

    //Full Time Report Response StateFlow
    private val _fullTimeReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val fullTimeReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _fullTimeReportResponse.asStateFlow()

    //Part Time Report Response StateFlow
    private val _partTimeReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val partTimeReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _partTimeReportResponse.asStateFlow()

    //Hand Held MalFunctions Report Response StateFlow
    private val _handHeldMalfunctionReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val handHeldMalfunctionReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _handHeldMalfunctionReportResponse.asStateFlow()

    //Sign Report Response StateFlow
    private val _signReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val signReportResponse: StateFlow<NewApiResponse<JsonNode>> = _signReportResponse.asStateFlow()

    //Vehicle Inspection Report Response StateFlow
    private val _vehicleInspectionReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val vehicleInspectionReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _vehicleInspectionReportResponse.asStateFlow()

    //Hour Marked Vehicles Report Response StateFlow
    private val _seventyTwoHourMarkedVehiclesReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val seventyTwoHourMarkedVehiclesReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _seventyTwoHourMarkedVehiclesReportResponse.asStateFlow()

    //Bike Inspection Report Response StateFlow
    private val _bikeInspectionReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val bikeInspectionReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _bikeInspectionReportResponse.asStateFlow()

    //Supervisor Report Response StateFlow
    private val _supervisorReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val supervisorReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _supervisorReportResponse.asStateFlow()

    //Special Assignment Report Response StateFlow
    private val _specialAssignmentReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val specialAssignmentReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _specialAssignmentReportResponse.asStateFlow()

    //Sign Off Report Response StateFlow
    private val _signOffReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val signOffReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _signOffReportResponse.asStateFlow()

    //Notice To Tow Report Response StateFlow
    private val _noticeToTowReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val noticeToTowReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _noticeToTowReportResponse.asStateFlow()

    //Tow Report Response StateFlow
    private val _towReportReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val towReportReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _towReportReportResponse.asStateFlow()

    //Report Service Response StateFlow
    private val _nflReportServiceResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val nflReportServiceResponse: StateFlow<NewApiResponse<JsonNode>> =
        _nflReportServiceResponse.asStateFlow()

    //Lot Inspection Report Response StateFlow
    private val _lotInspectionReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val lotInspectionReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _lotInspectionReportResponse.asStateFlow()

    //Lot Count Vio Rate Report Response StateFlow
    private val _lotCountVioReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val lotCountVioReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _lotCountVioReportResponse.asStateFlow()

    //Hard Summer Festival Report Response StateFlow
    private val _hardSummerFestivalReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val hardSummerFestivalReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _hardSummerFestivalReportResponse.asStateFlow()

    //After Seven PM Report Response StateFlow
    private val _afterSevenPmReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val afterSevenPmReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _afterSevenPmReportResponse.asStateFlow()

    //Pay Station Report Response StateFlow
    private val _payStationReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val payStationReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _payStationReportResponse.asStateFlow()

    //Signage Report Response StateFlow
    private val _signageReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val signageReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _signageReportResponse.asStateFlow()

    //Homeless Report Response StateFlow
    private val _homelessReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val homelessReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _homelessReportResponse.asStateFlow()

    //Safety Issue Report Response StateFlow
    private val _safetyIssueReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val safetyIssueReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _safetyIssueReportResponse.asStateFlow()

    //Work Order Report Response StateFlow
    private val _workOrderReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val workOrderReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _workOrderReportResponse.asStateFlow()

    //Trash Lot Report Response StateFlow
    private val _trashLotReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val trashLotReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _trashLotReportResponse.asStateFlow()

    //Upload All Photos Response StateFlow
    private val _uploadAllImagesResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val uploadAllImagesResponse: StateFlow<NewApiResponse<JsonNode>> =
        _uploadAllImagesResponse.asStateFlow()


    //Start Of All API Calls
    fun callBrokenMeterReportAPI(brokenMeterRequest: BrokenMeterReportRequest?) {
        viewModelScope.launch {
            _brokenMeterReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.brokenMeterReportSubmit(brokenMeterRequest = brokenMeterRequest)
            _brokenMeterReportResponse.value = result

            _brokenMeterReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callCurbReportAPI(curbRequest: CurbRequest?) {
        viewModelScope.launch {
            _curbReportResponse.value = NewApiResponse.Loading

            val result = reportServiceRepository.curbReportSubmit(curbRequest = curbRequest)
            _curbReportResponse.value = result

            _curbReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callFullTimeReportAPI(fullTimeRequest: FullTimeRequest?) {
        viewModelScope.launch {
            _fullTimeReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.fullTimeReportSubmit(fullTimeRequest = fullTimeRequest)
            _fullTimeReportResponse.value = result

            _fullTimeReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callPartTimeReportAPI(fullTimeRequest: FullTimeRequest?) {
        viewModelScope.launch {
            _partTimeReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.partTimeReportSubmit(fullTimeRequest = fullTimeRequest)
            _partTimeReportResponse.value = result

            _partTimeReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callHandHeldMalfunctionReportAPI(handHeldMalfunctionsRequest: HandHeldMalFunctionsRequest?) {
        viewModelScope.launch {
            _handHeldMalfunctionReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.handHeldMalfunctionsReportSubmit(handHeldMalfunctionsRequest = handHeldMalfunctionsRequest)
            _handHeldMalfunctionReportResponse.value = result

            _handHeldMalfunctionReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callSignReportAPI(signReportRequest: SignReportRequest?) {
        viewModelScope.launch {
            _signReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.signReportSubmit(signReportRequest = signReportRequest)
            _signReportResponse.value = result

            _signReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callVehicleInspectionReportAPI(vehicleInspectionRequest: VehicleInspectionRequest?) {
        viewModelScope.launch {
            _vehicleInspectionReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.vehicleInspectionReportSubmit(vehicleInspectionRequest = vehicleInspectionRequest)
            _vehicleInspectionReportResponse.value = result

            _vehicleInspectionReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callSeventyTwoHourMarkedVehiclesReportAPI(hourMarkedVehiclesRequest: HourMarkedVehiclesRequest?) {
        viewModelScope.launch {
            _seventyTwoHourMarkedVehiclesReportResponse.value = NewApiResponse.Loading

            val result = reportServiceRepository.seventyTwoHourMarkedVehiclesReportSubmit(
                hourMarkedVehiclesRequest = hourMarkedVehiclesRequest
            )
            _seventyTwoHourMarkedVehiclesReportResponse.value = result

            _seventyTwoHourMarkedVehiclesReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callBikeInspectionReportAPI(bikeInspectionsRequest: BikeInspectionsRequest?) {
        viewModelScope.launch {
            _bikeInspectionReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.bikeInspectionReportSubmit(bikeInspectionsRequest = bikeInspectionsRequest)
            _bikeInspectionReportResponse.value = result

            _bikeInspectionReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callSupervisorReportAPI(supervisorReportRequest: SupervisorReportRequest?) {
        viewModelScope.launch {
            _supervisorReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.supervisorReportSubmit(supervisorReportRequest = supervisorReportRequest)
            _supervisorReportResponse.value = result

            _supervisorReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callSpecialAssignmentReportAPI(specialAssignmentRequest: SpecialAssignementRequest?) {
        viewModelScope.launch {
            _specialAssignmentReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.specialAssignmentReportSubmit(specialAssignmentRequest = specialAssignmentRequest)
            _specialAssignmentReportResponse.value = result

            _specialAssignmentReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callLotInspectionReportAPI(lotInspectionRequest: LotInspectionRequest?) {
        viewModelScope.launch {
            _lotInspectionReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.lotInspectionReportSubmit(lotInspectionRequest = lotInspectionRequest)
            _lotInspectionReportResponse.value = result

            _lotInspectionReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callLotCountVioRateReportAPI(lotCountVioRateRequest: LotCountVioRateRequest?) {
        viewModelScope.launch {
            _lotCountVioReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.lotCountVioRateReportSubmit(lotCountVioRateRequest = lotCountVioRateRequest)
            _lotCountVioReportResponse.value = result

            _lotCountVioReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callHardSummerFestivalReportAPI(nflRequest: NFLRequest?) {
        viewModelScope.launch {
            _hardSummerFestivalReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.hardSummerFestivalReportSubmit(nflRequest = nflRequest)
            _hardSummerFestivalReportResponse.value = result

            _hardSummerFestivalReportResponse.value = NewApiResponse.Idle
        }
    }


    fun callAfterSevenPmReportAPI(afterSevenPmRequest: AfterSevenPMRequest?) {
        viewModelScope.launch {
            _afterSevenPmReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.afterSevenPmReportSubmit(afterSevenPmRequest = afterSevenPmRequest)
            _afterSevenPmReportResponse.value = result

            _afterSevenPmReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callPayStationReportAPI(payStationRequest: PayStationRequest?) {
        viewModelScope.launch {
            _payStationReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.payStationReportSubmit(payStationRequest = payStationRequest)
            _payStationReportResponse.value = result

            _payStationReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callSignageReportAPI(signageReportRequest: SignageReportRequest?) {
        viewModelScope.launch {
            _signageReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.paySignageReportSubmit(signageReportRequest = signageReportRequest)
            _signageReportResponse.value = result

            _signageReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callHomelessReportAPI(homelessRequest: HomelessRequest?) {
        viewModelScope.launch {
            _homelessReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.homeLessReportSubmit(homelessRequest = homelessRequest)
            _homelessReportResponse.value = result

            _homelessReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callWorkOrderReportAPI(workOrderRequest: WorkOrderRequest?) {
        viewModelScope.launch {
            _workOrderReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.workOrderReportSubmit(workOrderRequest = workOrderRequest)
            _workOrderReportResponse.value = result

            _workOrderReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callSafetyIssueReportAPI(safetyIssueRequest: SafetyIssueRequest?) {
        viewModelScope.launch {
            _safetyIssueReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.safetyIssueReportSubmit(safetyIssueRequest = safetyIssueRequest)
            _safetyIssueReportResponse.value = result

            _safetyIssueReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callTrashLotReportAPI(trashLotRequest: TrashLotRequest?) {
        viewModelScope.launch {
            _trashLotReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.trashLotReportSubmit(trashLotRequest = trashLotRequest)
            _trashLotReportResponse.value = result

            _trashLotReportResponse.value = NewApiResponse.Idle
        }
    }


    fun callSignOffReportAPI(signOffReportRequest: SignOffReportRequest?) {
        viewModelScope.launch {
            _signOffReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.signOffReportRequestSubmit(signOffReportRequest = signOffReportRequest)
            _signOffReportResponse.value = result

            _signOffReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callNoticeToTowReportAPI(noticeToTowRequest: NoticeToTowRequest?) {
        viewModelScope.launch {
            _noticeToTowReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.noticeToTowReportRequestSubmit(noticeToTowRequest = noticeToTowRequest)
            _noticeToTowReportResponse.value = result

            _noticeToTowReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callTowReportAPI(towReportRequest: TowReportRequest?) {
        viewModelScope.launch {
            _towReportReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.towReportRequestSubmit(towReportRequest = towReportRequest)
            _towReportReportResponse.value = result

            _towReportReportResponse.value = NewApiResponse.Idle
        }
    }

    fun callNflReportServiceAPI(nflRequest: NFLRequest?) {
        viewModelScope.launch {
            _nflReportServiceResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.nflSpecialAssignmentReportSubmit(nflRequest = nflRequest)
            _nflReportServiceResponse.value = result

            _nflReportServiceResponse.value = NewApiResponse.Idle
        }
    }

    fun callUploadAllImagesInBulkAPI(
        data: RequestBody?,
        uploadType: RequestBody?,
        files: List<MultipartBody.Part?>
    ) {
        viewModelScope.launch {
            _uploadAllImagesResponse.value = NewApiResponse.Loading

            val result = mediaServiceRepository.uploadAllImagesInBulk(
                data = data,
                uploadType = uploadType,
                files = files
            )
            _uploadAllImagesResponse.value = result

            _uploadAllImagesResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls

    //Start of Database Calls
    suspend fun getWelcomeForm(): WelcomeForm? {
        return withContext(Dispatchers.IO) {
            activityDaoRepository.getWelcomeForm()
        }
    }

    suspend fun getWelcomeDbObject(): WelcomeListDatatbase? {
        return withContext(Dispatchers.IO) {
            singletonDataSet.getWelcomeDbObject()
        }
    }
    //End of Database Calls
}