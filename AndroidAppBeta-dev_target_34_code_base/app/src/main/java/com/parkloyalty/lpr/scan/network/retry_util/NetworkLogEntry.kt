package com.parkloyalty.lpr.scan.network.retry_util

data class NetworkLogEntry(
    val url: String,
    val method: String,
    val latencyMs: Long,
    val timestamp: String,
    val responseCode: Int
)
