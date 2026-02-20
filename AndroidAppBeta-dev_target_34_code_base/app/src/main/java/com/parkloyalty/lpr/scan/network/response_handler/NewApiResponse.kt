package com.parkloyalty.lpr.scan.network.response_handler

import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException

sealed class NewApiResponse<out T> {
    object Idle : NewApiResponse<Nothing>()
    object Loading : NewApiResponse<Nothing>()

    data class Success<out T>(val data: T?, val code: Int, val apiNameTag: String) :
        NewApiResponse<T>()

    data class ApiError(val code: Int, val error: ErrorBody?, val apiNameTag: String) :
        NewApiResponse<Nothing>()

    data class NetworkError(val exception: IOException, val apiNameTag: String) :
        NewApiResponse<Nothing>()

    data class UnknownError(val throwable: Throwable, val apiNameTag: String) :
        NewApiResponse<Nothing>()

//    data class NotParsed(
//        val raw: String,
//        val code: Int,
//        val apiNameTag: String,
//        val parseError: Throwable? = null
//    ) : NewApiResponse<Nothing>()
}
