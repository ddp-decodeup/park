package com.parkloyalty.lpr.scan.views.fragments.scanresult

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerRequest
import com.parkloyalty.lpr.scan.database.repository.ActivityDaoRepository
import com.parkloyalty.lpr.scan.database.repository.CitationDaoRepository
import com.parkloyalty.lpr.scan.network.repository.CitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.EventServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.DataFromLprRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetDecalYearListModel
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScanResultScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val citationServiceRepository: CitationServiceRepository,
    private val eventServiceRepository: EventServiceRepository,
    private val activityDaoRepository: ActivityDaoRepository,
    private val citationDaoRepository: CitationDaoRepository,
) : ViewModel() {

    //Get Citation Data From LPR API Response StateFlow
    private val _getCitationDataFromLprResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val getCitationDataFromLprResponse: StateFlow<NewApiResponse<JsonNode>> =
        _getCitationDataFromLprResponse.asStateFlow()

    //Get Citation T2 Data From LPR API Response StateFlow
    private val _getCitationDataT2FromLprResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val getCitationDataT2FromLprResponse: StateFlow<NewApiResponse<JsonNode>> =
        _getCitationDataT2FromLprResponse.asStateFlow()

    //Get Make Model Color Data From LPR API Response StateFlow
    private val _getMakeModelColorDataFromLprResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val getMakeModelColorDataFromLprResponse: StateFlow<NewApiResponse<JsonNode>> =
        _getMakeModelColorDataFromLprResponse.asStateFlow()

    //Get Permit Data From LPR API Response Stateflow
    private val _getPermitDataFromLprResponse =
        MutableStateFlow<Triple<NewApiResponse<JsonNode>, String, String>>(Triple(NewApiResponse.Idle,"",""))
    val getPermitDataFromLprResponse: StateFlow<Triple<NewApiResponse<JsonNode>, String, String>> =
        _getPermitDataFromLprResponse.asStateFlow()

    //Get Camera Raw Feed Data From LPR Response Stateflow
    private val _getCameraRawFeedDataFromLprResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val getCameraRawFeedDataFromLprResponse: StateFlow<NewApiResponse<JsonNode>> =
        _getCameraRawFeedDataFromLprResponse.asStateFlow()

    //Get Payment Data From LPR Response Stateflow
    private val _getPaymentDataFromLprResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val getPaymentDataFromLprResponse: StateFlow<NewApiResponse<JsonNode>> =
        _getPaymentDataFromLprResponse.asStateFlow()

    //Get Scofflaw Data From LPR Response Stateflow
    private val _getScofflawDataFromLprResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val getScofflawDataFromLprResponse: StateFlow<NewApiResponse<JsonNode>> =
        _getScofflawDataFromLprResponse.asStateFlow()

    //Get Exempt Data From LPR Response Stateflow
    private val _getExemptDataFromLprResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val getExemptDataFromLprResponse: StateFlow<NewApiResponse<JsonNode>> =
        _getExemptDataFromLprResponse.asStateFlow()

    //Get Stolen Data From LPR Response Stateflow
    private val _getStolenDataFromLprResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val getStolenDataFromLprResponse: StateFlow<NewApiResponse<JsonNode>> =
        _getStolenDataFromLprResponse.asStateFlow()

    //LPR Scan Logger Response Stateflow
    private val _lprScanLoggerResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val lprScanLoggerResponse: StateFlow<NewApiResponse<JsonNode>> =
        _lprScanLoggerResponse.asStateFlow()

    //Timing Mark Response Stateflow
    private val _timingMarkResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val timingMarkResponse: StateFlow<NewApiResponse<JsonNode>> = _timingMarkResponse.asStateFlow()

    //Last Second Check Response Stateflow
    private val _lastSecondCheckResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val lastSecondCheckResponse: StateFlow<NewApiResponse<JsonNode>> =
        _lastSecondCheckResponse.asStateFlow()

    //Add Timing Response StateFlow
    private val _addTimingResponse = MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val addTimingResponse: StateFlow<NewApiResponse<JsonNode>> = _addTimingResponse.asStateFlow()

    //Create Ticket Response StateFlow
    private val _createTicketResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val createTicketResponse: StateFlow<NewApiResponse<JsonNode>> =
        _createTicketResponse.asStateFlow()

    //Inactive Meter Buzzer Response StateFlow
    private val _inactiveMeterBuzzerResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val inactiveMeterBuzzerResponse: StateFlow<NewApiResponse<JsonNode>> =
        _inactiveMeterBuzzerResponse.asStateFlow()


    fun getCitationDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                          lprNumber: String) {
        viewModelScope.launch {
            _getCitationDataFromLprResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getCitationDataFromLprResponse.value = result

            _getCitationDataFromLprResponse.value = NewApiResponse.Idle
        }
    }

    fun getCitationDataT2FromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                          lprNumber: String) {
        viewModelScope.launch {
            _getCitationDataT2FromLprResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getCitationDataT2FromLprResponse.value = result

            _getCitationDataT2FromLprResponse.value = NewApiResponse.Idle
        }
    }

    fun getMakeModelColorDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                          lprNumber: String) {
        viewModelScope.launch {
            _getMakeModelColorDataFromLprResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getMakeModelColorDataFromLprResponse.value = result

            _getMakeModelColorDataFromLprResponse.value = NewApiResponse.Idle
        }
    }

    fun getPermitDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                                lprNumber: String) {
        viewModelScope.launch {
            _getPermitDataFromLprResponse.value = Triple(NewApiResponse.Loading,type,lprNumber)
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getPermitDataFromLprResponse.value = Triple(result,type,lprNumber)

            _getPermitDataFromLprResponse.value = Triple(NewApiResponse.Idle,type,lprNumber)
        }
    }

    fun getCameraRawFeedDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                                       lprNumber: String) {
        viewModelScope.launch {
            _getCameraRawFeedDataFromLprResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getCameraRawFeedDataFromLprResponse.value = result

            _getCameraRawFeedDataFromLprResponse.value = NewApiResponse.Idle
        }
    }

    fun getPaymentDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                                 lprNumber: String) {
        viewModelScope.launch {
            _getPaymentDataFromLprResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getPaymentDataFromLprResponse.value = result

            _getPaymentDataFromLprResponse.value = NewApiResponse.Idle
        }
    }

    fun getScofflawDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                                  lprNumber: String) {
        viewModelScope.launch {
            _getScofflawDataFromLprResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getScofflawDataFromLprResponse.value = result

            _getScofflawDataFromLprResponse.value = NewApiResponse.Idle
        }
    }

    fun getExemptDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                                lprNumber: String) {
        viewModelScope.launch {
            _getExemptDataFromLprResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getExemptDataFromLprResponse.value = result

            _getExemptDataFromLprResponse.value = NewApiResponse.Idle
        }
    }

    fun getStolenDataFromLprAPI(dataFromLprRequest: DataFromLprRequest?, type: String,
                                lprNumber: String) {
        viewModelScope.launch {
            _getStolenDataFromLprResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getDataFromLpr(dataFromLprRequest = dataFromLprRequest)
            _getStolenDataFromLprResponse.value = result

            _getStolenDataFromLprResponse.value = NewApiResponse.Idle
        }
    }


    fun lprScanLoggerAPI(lprScanLoggerRequest: LprScanLoggerRequest?) {
        viewModelScope.launch {
            //_lprScanLoggerResponse.value = NewApiResponse.Loading
            val result =
                eventServiceRepository.lprScanLogger(lprScanLoggerRequest = lprScanLoggerRequest)
            _lprScanLoggerResponse.value = result

            _lprScanLoggerResponse.value = NewApiResponse.Idle
        }
    }

    fun timingMarkAPI(
        arrivalStatus: String? = null,
        issueTsFrom: String? = null,
        issueTsTo: String? = null,
        lpNumber: String? = null,
        page: Int? = null,
        siteOfficerId: String? = null,
        enforced: String? = null
    ) {
        viewModelScope.launch {
            _timingMarkResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.getTimingMark(
                arrivalStatus = arrivalStatus,
                issueTsFrom = issueTsFrom,
                issueTsTo = issueTsTo,
                lpNumber = lpNumber,
                page = page,
                siteOfficerId = siteOfficerId,
                enforced = enforced
            )
            _timingMarkResponse.value = result

            _timingMarkResponse.value = NewApiResponse.Idle
        }
    }

    fun getLastSecondCheckAPI(
        zone: String? = null,
        park: String? = null,
        lpNumber: String? = null,
        spaceId: String? = null,
    ) {
        viewModelScope.launch {
            _lastSecondCheckResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.getLastSecondCheck(
                zone = zone, park = park, lpNumber = lpNumber, spaceId = spaceId
            )
            _lastSecondCheckResponse.value = result

            _lastSecondCheckResponse.value = NewApiResponse.Idle
        }
    }

    fun callAddTimingAPI(addTimingRequest: AddTimingRequest) {
        viewModelScope.launch {
            _addTimingResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.addTiming(addTimingRequest = addTimingRequest)
            _addTimingResponse.value = result

            _addTimingResponse.value = NewApiResponse.Idle
        }
    }


    fun callCreateCitationTicketAPI(createTicketRequest: CreateTicketRequest) {
        viewModelScope.launch {
            _createTicketResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.createTicket(createTicketRequest = createTicketRequest)
            _createTicketResponse.value = result

            _createTicketResponse.value = NewApiResponse.Idle
        }
    }

    fun callInactiveMeterBuzzerAPI(inactiveMeterBuzzerRequest : InactiveMeterBuzzerRequest){
        viewModelScope.launch {
            _inactiveMeterBuzzerResponse.value = NewApiResponse.Loading
            val result =
                eventServiceRepository.inactiveMeterBuzzer(inactiveMeterBuzzerRequest = inactiveMeterBuzzerRequest)
            _inactiveMeterBuzzerResponse.value = result

            _inactiveMeterBuzzerResponse.value = NewApiResponse.Idle
        }
    }

    /*Start of Database Operation*/


 suspend fun getUpdateTimeResponse(): TimestampDatatbase? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getUpdateTimeResponse()
        }
    }


    suspend fun getCitationBooklet(status: Int): List<CitationBookletModel>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationBooklet(status)
        }
    }

    suspend fun getCountImages(): Int {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCountImages()
        }
    }

    suspend fun insertCitationImage(databaseModel: CitationImagesModel) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertCitationImage(databaseModel)
        }
    }

    suspend fun insertWelcomeForm(welcomeForm: WelcomeForm) {
        return withContext(Dispatchers.IO) {
            activityDaoRepository.insertWelcomeForm(welcomeForm)
        }
    }


    suspend fun updateCitationBooklet(status: Int, id: String?) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateCitationBooklet(status, id)
        }
    }

    /*End of Database Operation*/
}