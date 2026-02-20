package com.parkloyalty.lpr.scan.network.interceptor

import android.util.Log
import com.parkloyalty.lpr.scan.util.LogUtil
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.nio.charset.Charset

/**
 * Comprehensive API Logging Interceptor
 * Logs all API requests and responses with the following details:
 * - Request URL
 * - Request method (GET, POST, etc.)
 * - Request headers
 * - Request body
 * - Response status code
 * - Response headers
 * - Response body
 * - Request/Response duration
 */
class ApiLoggingInterceptor : Interceptor {
    
    companion object {
        private const val TAG = "API_LOGGING"
        private const val LOGGING_PREFIX = "═══════════════════════════════════"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.currentTimeMillis()
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method
        
        // Log Request Details
        logRequestDetails(url, method, request)
        
        // Execute the request
        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            logException("API Request Failed", url, method, e)
            throw e
        }
        
        val duration = System.currentTimeMillis() - startTime
        
        // Log Response Details
//        logResponseDetails(url, method, response, duration)
        
        return response
    }

    /**
     * Logs comprehensive request details including URL, headers, and body
     */
    private fun logRequestDetails(url: String, method: String, request: okhttp3.Request) {
        if (!LogUtil.isEnableAPILogs) return
        
        val separator = "$LOGGING_PREFIX REQUEST ═════════════════════"
        Log.e(TAG, separator)
        Log.e(TAG, "🔵 API URL: $url")
        Log.e(TAG, "🔵 METHOD: $method")
        
        // Log Request Headers
        val headers = request.headers
        if (headers.size > 0) {
            Log.e(TAG, "🔵 REQUEST HEADERS:")
            for (i in 0 until headers.size) {
                val headerName = headers.name(i)
                val headerValue = if (headerName.equals("Authorization", ignoreCase = true) || 
                                       headerName.equals("token", ignoreCase = true)) {
                    "***REDACTED***"
                } else {
                    headers.value(i)
                }
                Log.e(TAG, "   ├─ $headerName: $headerValue")
            }
        }
        
        // Log Request Body
        if (request.body != null) {
            try {
                val copy = request.newBuilder().build()
                val buffer = okio.Buffer()
                copy.body!!.writeTo(buffer)
                val requestBodyString = buffer.readString(Charset.forName("UTF-8"))
                Log.e(TAG, "🔵 REQUEST BODY:")
                Log.e(TAG, prettyPrintJson(requestBodyString))
            } catch (e: Exception) {
                Log.e(TAG, "🔵 REQUEST BODY: (Unable to read body)")
                Log.e(TAG, Log.getStackTraceString(e))
            }
        } else {
            Log.e(TAG, "🔵 REQUEST BODY: (Empty)")
        }
        
        Log.e(TAG, separator)
    }

    /**
     * Logs comprehensive response details including status, headers, and body
     */
   /** private fun logResponseDetails(url: String, method: String, response: Response, duration: Long) {
        if (!LogUtil.isEnableAPILogs) return
        
        val separator = "$LOGGING_PREFIX RESPONSE ═════════════════════"
        Log.e(TAG, separator)
        Log.e(TAG, "🟢 API URL: $url")
        Log.e(TAG, "🟢 METHOD: $method")
        Log.e(TAG, "🟢 STATUS CODE: ${response.code} (${response.message})")
        Log.e(TAG, "🟢 DURATION: ${duration}ms")

        // Log Response Body
        if (response.body != null) {
            try {
                val responseBody = response.body!!
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Request all bytes
                val buffer = source.buffer
                val responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"))
                
                Log.e(TAG, "🟢 RESPONSE BODY:")
                Log.e(TAG, responseBodyString)
                
                // Return a new response with the same body (since we consumed it)
                val mediaType = responseBody.contentType()
                val newBody = responseBodyString.toResponseBody(mediaType ?: "application/json".toMediaType())
                
            } catch (e: Exception) {
                Log.e(TAG, "🟢 RESPONSE BODY: (Unable to read body)")
                Log.e(TAG, Log.getStackTraceString(e))
            }
        } else {
            Log.e(TAG, "🟢 RESPONSE BODY: (Empty)")
        }
        
        Log.e(TAG, separator)
    }*/

    /**
     * Logs exception details for failed API requests
     */
    private fun logException(errorType: String, url: String, method: String, exception: Exception) {
        if (!LogUtil.isEnableAPILogs) return
        
        val separator = "$LOGGING_PREFIX ERROR ═════════════════════"
        Log.e(TAG, separator)
        Log.e(TAG, "🔴 $errorType")
        Log.e(TAG, "🔴 API URL: $url")
        Log.e(TAG, "🔴 METHOD: $method")
        Log.e(TAG, "🔴 ERROR MESSAGE: ${exception.message}")
        Log.e(TAG, "🔴 STACK TRACE:")
        Log.e(TAG, Log.getStackTraceString(exception))
        Log.e(TAG, separator)
    }

    /**
     * Attempts to pretty-print JSON string for better readability
     */
    private fun prettyPrintJson(json: String): String {
        return try {
            val trimmed = json.trim()
            if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                trimmed
            } else {
                // Simple JSON formatting with indentation
                var result = ""
                var indent = 0
                var inString = false
                var escapeNext = false

                for (char in trimmed) {
                    when {
                        escapeNext -> {
                            result += char
                            escapeNext = false
                        }
                        char == '\\' -> {
                            result += char
                            escapeNext = true
                        }
                        char == '"' && !escapeNext -> {
                            result += char
                            inString = !inString
                        }
                        !inString && (char == '{' || char == '[') -> {
                            indent++
                            result += char + "\n" + getIndent(indent)
                        }
                        !inString && (char == '}' || char == ']') -> {
                            indent--
                            result += "\n" + getIndent(indent) + char
                        }
                        !inString && char == ',' -> {
                            result += char + "\n" + getIndent(indent)
                        }
                        !inString && char == ':' -> {
                            result += ": "
                        }
                        !inString && char == ' ' -> {
                            // Skip spaces outside strings
                        }
                        else -> {
                            result += char
                        }
                    }
                }
                result
            }
        } catch (e: Exception) {
            json
        }
    }

    /**
     * Returns indentation string for JSON formatting
     */
    private fun getIndent(level: Int): String {
        return "   ".repeat(level)
    }
}
