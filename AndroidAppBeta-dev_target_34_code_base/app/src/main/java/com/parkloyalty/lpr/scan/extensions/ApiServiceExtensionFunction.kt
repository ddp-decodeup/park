package com.parkloyalty.lpr.scan.extensions

import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse

fun NewApiResponse.ApiError?.getErrorMessage(): String? {
    return this?.error?.message ?: this?.error?.raw
}

fun getDeviceIdForAPI(uniqueID: String, deviceName: String): String {
    return if (!deviceName.isNullOrEmpty()) "$uniqueID-$deviceName" else "$uniqueID-Device"
}