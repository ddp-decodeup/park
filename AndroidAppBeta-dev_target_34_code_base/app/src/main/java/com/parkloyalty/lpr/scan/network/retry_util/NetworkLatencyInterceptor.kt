package com.parkloyalty.lpr.scan.network.retry_util

import com.parkloyalty.lpr.scan.util.LogUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//class NetworkLatencyInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//
//
//        val startTime = System.nanoTime()
//        val response = chain.proceed(request)
//        val endTime = System.nanoTime()
//
//        val durationMs = (endTime - startTime) / 1_000_000
//
//        LogUtil.printLog(
//            "NETWORK_LATENCY",
//            "URL: ${request.url} \n Method: ${request.method} \n Duration: ${durationMs}ms \n Code: ${response.code}"
//        )
//        LogUtil.printLog(
//            "NETWORK_LATENCY",
//            "=================================="
//        )
//
//        return response
//    }
//}

class NetworkLatencyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNs = System.nanoTime()
        val response = chain.proceed(request)
        val endNs = System.nanoTime()

        val latencyMs = (endNs - startNs) / 1_000_000
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        NetworkLogger.logs.add(
            NetworkLogEntry(
                url = request.url.toString(),
                method = request.method,
                latencyMs = latencyMs,
                timestamp = timestamp,
                responseCode = response.code
            )
        )

        LogUtil.printLog(
            "NETWORK_LATENCY",
            "URL: ${request.url} \n Method: ${request.method} \n LatencyMs: ${latencyMs}ms \n Code: ${response.code}"
        )
        LogUtil.printLog(
            "NETWORK_LATENCY",
            "=================================="
        )

        return response
    }
}

