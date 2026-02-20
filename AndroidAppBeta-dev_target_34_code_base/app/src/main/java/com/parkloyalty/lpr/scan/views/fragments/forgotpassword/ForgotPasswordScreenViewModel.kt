package com.parkloyalty.lpr.scan.views.fragments.forgotpassword

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.repository.AuthServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.login.model.ForgetPasswordRequest
import com.parkloyalty.lpr.scan.ui.login.model.ForgotPasswordResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val authServiceRepository: AuthServiceRepository,
) : ViewModel() {

    // Forgot Password Response SharedFlow (for one-time events)
    private val _forgotPasswordResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val forgotPasswordResponse: StateFlow<NewApiResponse<JsonNode>> =
        _forgotPasswordResponse.asStateFlow()

    //Start Of All API Calls
    fun callForgotPasswordAPI(forgetPasswordRequest: ForgetPasswordRequest) {
        viewModelScope.launch {
            _forgotPasswordResponse.value = NewApiResponse.Loading
            val result =
                authServiceRepository.forgetPassword(forgetPasswordRequest = forgetPasswordRequest)
            _forgotPasswordResponse.value = result
            _forgotPasswordResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls
}