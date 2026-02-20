package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.model.OfficerZone

object TypeConverterZone {
    @TypeConverter
    fun fromString(value: String?): OfficerZone {
        val listType = object : TypeToken<OfficerZone?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: OfficerZone?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}