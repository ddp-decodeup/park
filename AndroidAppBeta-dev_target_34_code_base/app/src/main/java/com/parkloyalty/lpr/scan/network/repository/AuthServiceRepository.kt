package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.AuthService
import com.parkloyalty.lpr.scan.ui.login.model.ForgetPasswordRequest
import com.parkloyalty.lpr.scan.ui.login.model.SiteOfficerLoginRequest
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_FORGOT_PASSWORD
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_REFRESH_TOKEN
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_SUPERVISOR
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOGIN
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceRepository @Inject constructor(
    private val authService: AuthService
) {

    suspend fun login(siteOfficerLoginRequest: SiteOfficerLoginRequest): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_LOGIN,
            call = { authService.login(param = siteOfficerLoginRequest) })

    suspend fun forgetPassword(forgetPasswordRequest: ForgetPasswordRequest): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_FORGOT_PASSWORD,
            call = { authService.forgetPassword(forgetPasswordRequest = forgetPasswordRequest) })

    suspend fun getAuthTokenRefresh(): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_REFRESH_TOKEN, call = { authService.getAuthTokenRefresh() })

    suspend fun getSupervisor(shift: String): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_SUPERVISOR,
        call = { authService.getSupervisor(shift = shift) })
}