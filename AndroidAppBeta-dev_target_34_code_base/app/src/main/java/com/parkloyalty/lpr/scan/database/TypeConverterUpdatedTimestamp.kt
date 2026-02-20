package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDataList

object TypeConverterUpdatedTimestamp {
    @TypeConverter
    fun fromString(value: String?): UpdateTimeDataList {
        val listType = object : TypeToken<UpdateTimeDataList?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: UpdateTimeDataList?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}