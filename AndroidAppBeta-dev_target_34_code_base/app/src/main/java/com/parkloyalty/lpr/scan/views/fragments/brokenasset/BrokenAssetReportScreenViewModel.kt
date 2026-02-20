package com.parkloyalty.lpr.scan.views.fragments.brokenasset

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.database.repository.ActivityDaoRepository
import com.parkloyalty.lpr.scan.network.repository.ReportServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.allreport.model.BrokenMeterReportRequest
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.BrokenMeterRequest
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
class BrokenAssetReportScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val reportServiceRepository: ReportServiceRepository,
    private val activityDaoRepository: ActivityDaoRepository,
) : ViewModel() {

    //Broken Assets Report Response StateFlow
    private val _brokenAssetsReportResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val brokenAssetsReportResponse: StateFlow<NewApiResponse<JsonNode>> =
        _brokenAssetsReportResponse.asStateFlow()

    //Start Of All API Calls
    fun callBrokenMeterReportAPI(brokenMeterRequest: BrokenMeterRequest?) {
        viewModelScope.launch {
            _brokenAssetsReportResponse.value = NewApiResponse.Loading

            val result =
                reportServiceRepository.brokenAssetsReport(brokenMeterRequest = brokenMeterRequest)
            _brokenAssetsReportResponse.value = result

            _brokenAssetsReportResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls

    //Start of Database Calls
    suspend fun getWelcomeForm(): WelcomeForm? {
        return withContext(Dispatchers.IO) {
            activityDaoRepository.getWelcomeForm()
        }
    }
    //End of Database Calls
}