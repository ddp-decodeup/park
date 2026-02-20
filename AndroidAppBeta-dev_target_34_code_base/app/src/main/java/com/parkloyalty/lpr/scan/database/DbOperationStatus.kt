package com.parkloyalty.lpr.scan.database

sealed class DbOperationStatus<out T> {
    object Idle : DbOperationStatus<Nothing>()
    object Loading : DbOperationStatus<Nothing>()
    data class Success<out T>(val data: T?) : DbOperationStatus<T>()
    data class Error(val throwable: Throwable) : DbOperationStatus<Nothing>()
}

