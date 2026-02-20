package com.parkloyalty.lpr.scan.network.retry_util

import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

class RetryingInterceptor : Interceptor {
    private val tryCnt = 3
    private val baseInterval = 4000L

    override fun intercept(chain: Interceptor.Chain): Response {
        return process(chain, attempt = 1)
    }

    private fun process(chain: Interceptor.Chain, attempt: Int): Response {
        var response: Response? = null
        try {
            val request = chain.request()
            response = chain.proceed(request)
            if (attempt < tryCnt && !response.isSuccessful) {
                return delayedAttempt(chain, response, attempt)
            }
            return response
        } catch (e: IOException) {
            if (attempt < tryCnt) {
                return delayedAttempt(chain, response, attempt)
            }
            throw e
        }
    }

    private fun delayedAttempt(
        chain: Interceptor.Chain,
        response: Response?,
        attempt: Int,
    ): Response {
        response?.body?.close()
        Thread.sleep(baseInterval * attempt)
        return process(chain, attempt = attempt + 1)
    }

    /**
     * Add this variable in catch block above , if you want to have only network error to be retry in catch
     * ex. if (attempt < tryCnt && networkRetryCheck)
     */
    private val networkRetryCheck: (Throwable) -> Boolean = {
        val shouldRetry = when {
            it.isHttpError() -> true
            else -> false
        }
        shouldRetry
    }
}