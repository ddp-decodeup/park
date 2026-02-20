package com.parkloyalty.lpr.scan.utility

enum class ApplicationMode(private val stringValue: String, private val intValue: Int) {
    DEVELOPMENT("Development", 0), PRODUCTION("Production", 1);

    override fun toString(): String {
        return stringValue
    }
}