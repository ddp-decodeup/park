package com.parkloyalty.lpr.scan.views.fragments.checksetup

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
class CheckSetupScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val activityServiceRepository: ActivityServiceRepository,

    ) : ViewModel() {

    //Check Setup Response StateFlow
    private val _checkSetupResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val checkSetupResponse: StateFlow<NewApiResponse<JsonNode>> = _checkSetupResponse.asStateFlow()

    //Start Of All API Calls
    fun callCheckSetupAPI() {
        viewModelScope.launch {
            _checkSetupResponse.value = NewApiResponse.Loading

            val result = activityServiceRepository.checkSetup()
            _checkSetupResponse.value = result

            _checkSetupResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls

}