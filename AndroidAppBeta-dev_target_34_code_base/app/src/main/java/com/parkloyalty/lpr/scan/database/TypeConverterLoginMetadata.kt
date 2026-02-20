package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.model.SiteOfficerLoginMetadata

object TypeConverterLoginMetadata {
    @TypeConverter
    fun fromString(value: String?): SiteOfficerLoginMetadata {
        val listType = object : TypeToken<SiteOfficerLoginMetadata?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: SiteOfficerLoginMetadata?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}