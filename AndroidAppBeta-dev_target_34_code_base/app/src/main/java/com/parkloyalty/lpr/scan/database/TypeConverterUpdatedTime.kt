package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeData

object TypeConverterUpdatedTime {
    @TypeConverter
    fun fromString(value: String?): List<UpdateTimeData> {
        val listType = object : TypeToken<List<UpdateTimeData?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<UpdateTimeData?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}