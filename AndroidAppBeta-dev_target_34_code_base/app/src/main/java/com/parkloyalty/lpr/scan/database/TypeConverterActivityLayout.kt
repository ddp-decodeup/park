package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutData

object TypeConverterActivityLayout {
    @TypeConverter
    fun fromString(value: String?): List<ActivityLayoutData> {
        val listType = object : TypeToken<List<ActivityLayoutData?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<ActivityLayoutData?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}

//package com.parkloyalty.lpr.scan.database
//
//import androidx.room.TypeConverter
//import com.fasterxml.jackson.module.kotlin.readValue
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutData
//import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
//
//object TypeConverterActivityLayout {
//    private val objectMapper = ObjectMapperProvider.instance
//
//    @TypeConverter
//    fun fromString(value: String?): List<ActivityLayoutData>? {
//        if (value.isNullOrEmpty()) return null
//        return objectMapper.readValue(value)
//    }
//
//    @TypeConverter
//    fun fromArrayList(list: List<ActivityLayoutData?>?): String? {
//        if (list == null) return null
//        return objectMapper.writeValueAsString(list)
//    }
//}