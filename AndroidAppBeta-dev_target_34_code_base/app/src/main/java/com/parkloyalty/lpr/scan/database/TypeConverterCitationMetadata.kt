package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TypeConverterCitationMetadata {
    @TypeConverter
    fun fromString(value: String?): Any {
        val listType = object : TypeToken<Any?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: Any?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}