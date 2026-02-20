package com.parkloyalty.lpr.scan.network.retry_util

import retrofit2.HttpException

fun Throwable.isHttpError(): Boolean {
    if (this is HttpException) {
        val exception: HttpException = this as HttpException
        return when (exception.code()) {
            in 400..499 -> {
                true
            }

            in 500..599 -> {
                true
            }

            else -> {
                false
            }
        }
    } else {
        return false
    }
}

fun Throwable.isHttp4xx(): Boolean {
    if (this is HttpException) {
        val exception: HttpException = this as HttpException
        return when (exception.code()) {
            in 400..499 -> {
                true
            }

            else -> {
                false
            }
        }
    } else {
        return false
    }
}