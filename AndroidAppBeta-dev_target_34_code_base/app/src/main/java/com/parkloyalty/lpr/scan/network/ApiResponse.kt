package com.parkloyalty.lpr.scan.network

import com.fasterxml.jackson.databind.JsonNode

/*this class is responsible for com.app.teketeke.network response*/
class ApiResponse {
    val status: Status
    var data: JsonNode? = null
    val error: Throwable?
    var accountAlias: String? = null

    var lprNumber: String? = null
    var type: String? = null

    private constructor(
        status: Status, data: JsonNode?,
        error: Throwable?
    ) {
        this.status = status
        this.data = data
        this.error = error
    }

    private constructor(
        status: Status, data: JsonNode?,
        error: Throwable?, accountAlias: String
    ) {
        this.status = status
        this.data = data
        this.error = error
        this.accountAlias = accountAlias
    }

    private constructor(
        status: Status, data: JsonNode?,
        error: Throwable?, lprNumber: String, type: String
    ) {
        this.status = status
        this.data = data
        this.error = error
        this.lprNumber = lprNumber
        this.type = type
    }

    companion object {
        @JvmStatic
        fun loading(): ApiResponse {
            return ApiResponse(Status.LOADING, null, null)
        }

        @JvmStatic
        fun success(data: JsonNode): ApiResponse {
            return ApiResponse(Status.SUCCESS, data, null)
        }

        @JvmStatic
        fun error(error: Throwable): ApiResponse {
            return ApiResponse(Status.ERROR, null, error)
        }


        //This needs to handle permit data API response
        fun successPermitData(data: JsonNode, lprNumber: String, type: String): ApiResponse {
            return ApiResponse(Status.SUCCESS, data, null, lprNumber, type)
        }

        //This needs to handle permit data API response
        fun errorPermitData(error: Throwable, lprNumber: String, type: String): ApiResponse {
            return ApiResponse(Status.ERROR, null, error, lprNumber, type)
        }

        fun successBalanceEnquiry(data: JsonNode, accountAlias: String): ApiResponse {
            return ApiResponse(Status.SUCCESS, data, null, accountAlias)
        }

        fun errorForBalanceEnquiry(error: Throwable, accountAlias: String): ApiResponse {
            return ApiResponse(Status.ERROR, null, error, accountAlias)
        }
    }
}