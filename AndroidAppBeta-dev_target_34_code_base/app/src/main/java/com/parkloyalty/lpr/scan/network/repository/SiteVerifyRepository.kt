package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.RecaptchaApiService
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_VERIFY_SITE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SiteVerifyRepository @Inject constructor(
    private val recaptchaApiService: RecaptchaApiService
) {
    suspend fun verifySite(
        secretKey: String, userResponseToken: String
    ): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_VERIFY_SITE, call = {
            recaptchaApiService.verifySite(
                secret = secretKey, response = userResponseToken
            )
        })
}
