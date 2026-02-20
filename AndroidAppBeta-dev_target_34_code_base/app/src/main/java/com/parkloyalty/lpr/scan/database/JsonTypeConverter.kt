package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets

object JsonTypeConverter {
    @TypeConverter
    fun encodeJsonResponse(value: String): ByteArray {
        return value.toByteArray()
    }

    @TypeConverter
    fun decodeJsonResponse(encodedValue: ByteArray?): String {
        try {
            return String(encodedValue!!, StandardCharsets.UTF_8)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return ""
    }
}