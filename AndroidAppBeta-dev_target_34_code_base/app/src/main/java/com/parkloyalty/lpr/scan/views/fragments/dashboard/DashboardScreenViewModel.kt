package com.parkloyalty.lpr.scan.views.fragments.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.repository.ActivityServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val activityServiceRepository: ActivityServiceRepository,

    ) : ViewModel() {

    //Upload Image Response StateFlow
    private val _barCountResponse = MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val barCountResponse: StateFlow<NewApiResponse<JsonNode>> = _barCountResponse.asStateFlow()


    //Start Of All API Calls
    fun callGetBarCountAPI(shift: String) {
        viewModelScope.launch {
            _barCountResponse.value = NewApiResponse.Loading

            val result = activityServiceRepository.getBarCount(shift = shift)
            _barCountResponse.value = result

            _barCountResponse.value = NewApiResponse.Idle
        }
    }
}