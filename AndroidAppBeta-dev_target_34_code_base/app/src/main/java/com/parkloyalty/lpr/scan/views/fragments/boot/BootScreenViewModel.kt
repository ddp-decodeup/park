package com.parkloyalty.lpr.scan.views.fragments.boot

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.database.repository.ActivityDaoRepository
import com.parkloyalty.lpr.scan.database.repository.CitationDaoRepository
import com.parkloyalty.lpr.scan.network.repository.CitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.ReportServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.allreport.model.BrokenMeterReportRequest
import com.parkloyalty.lpr.scan.ui.boot.model.BootMetadataRequest
import com.parkloyalty.lpr.scan.ui.boot.model.BootRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
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
class BootScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val citationServiceRepository: CitationServiceRepository,
    private val citationDaoRepository: CitationDaoRepository,
    private val activityDaoRepository: ActivityDaoRepository,

    ) : ViewModel() {

    //Boot Submit Response StateFlow
    private val _bootSubmitResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val bootSubmitResponse: StateFlow<NewApiResponse<JsonNode>> =
        _bootSubmitResponse.asStateFlow()

    //Boot Instance Ticket Response StateFlow
    private val _bootInstanceTicketResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val bootInstanceTicketResponse: StateFlow<NewApiResponse<JsonNode>> =
        _bootInstanceTicketResponse.asStateFlow()

    //Citation Number Response StateFlow
    private val _citationNumberResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val citationNumberResponse: StateFlow<NewApiResponse<JsonNode>> =
        _citationNumberResponse.asStateFlow()

    //Start Of All API Calls
    fun callBootSubmitAPI(bootRequest: BootRequest?) {
        viewModelScope.launch {
            _bootSubmitResponse.value = NewApiResponse.Loading

            val result =
                citationServiceRepository.bootSubmit(bootRequest = bootRequest)
            _bootSubmitResponse.value = result

            _bootSubmitResponse.value = NewApiResponse.Idle
        }
    }

    fun callBootInstanceTicketAPI(bootMetadataRequest: BootMetadataRequest?) {
        viewModelScope.launch {
            _bootInstanceTicketResponse.value = NewApiResponse.Loading

            val result =
                citationServiceRepository.bootInstanceTicketAPI(bootMetadataRequest = bootMetadataRequest)
            _bootInstanceTicketResponse.value = result

            _bootInstanceTicketResponse.value = NewApiResponse.Idle
        }
    }

    fun callGetCitationNumberAPI(citationNumberRequest: CitationNumberRequest?) {
        viewModelScope.launch {
            _citationNumberResponse.value = NewApiResponse.Loading

            val result =
                citationServiceRepository.getCitationNumber(citationNumberRequest = citationNumberRequest)
            _citationNumberResponse.value = result

            _citationNumberResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls

    //Start of Database Calls
    suspend fun getWelcomeForm(): WelcomeForm? {
        return withContext(Dispatchers.IO) {
            activityDaoRepository.getWelcomeForm()
        }
    }

    suspend fun getCitationBooklet(status: Int): List<CitationBookletModel>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationBooklet(status)
        }
    }

    suspend fun insertCitationBooklet(databaseModel: List<CitationBookletModel>) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertCitationBooklet(databaseModel)
        }
    }


    suspend fun updateCitationBooklet(status: Int, id: String?) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateCitationBooklet(status, id)
        }
    }
    //End of Database Calls
}