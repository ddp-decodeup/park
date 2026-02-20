package com.parkloyalty.lpr.scan.interfaces

interface FingerDetector {
    fun fingerDetected(message: String?, success: Boolean)
}