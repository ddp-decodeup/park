package com.parkloyalty.lpr.scan.extensions

import android.util.Log
import com.parkloyalty.lpr.scan.BuildConfig

/**
 * Global flag to enable/disable debug logging.
 */
var LOG_DEBUG_ENABLED = true && BuildConfig.DEBUG

/**
 * Print a debug log message with a custom tag.
 */
fun Any.logD(tag: String = this::class.java.simpleName, message: String) {
    if (LOG_DEBUG_ENABLED) {
        Log.d(tag, message)
    }
}

/**
 * Print an info log message with a custom tag.
 */
fun Any.logI(tag: String = this::class.java.simpleName, message: String) {
    if (LOG_DEBUG_ENABLED) {
        Log.i(tag, message)
    }
}

/**
 * Print a warning log message with a custom tag.
 */
fun Any.logW(tag: String = this::class.java.simpleName, message: String) {
    if (LOG_DEBUG_ENABLED) {
        Log.w(tag, message)
    }
}

/**
 * Print an error log message with a custom tag.
 */
fun Any.logE(tag: String = this::class.java.simpleName, message: String, throwable: Throwable? = null) {
    if (LOG_DEBUG_ENABLED) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}

/**
 * Print a verbose log message with a custom tag.
 */
fun Any.logV(tag: String = this::class.java.simpleName, message: String) {
    if (LOG_DEBUG_ENABLED) {
        Log.v(tag, message)
    }
}