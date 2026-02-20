package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutData

object TypeConverterCitationLayout {
    @TypeConverter
    fun fromString(value: String?): List<CitationLayoutData> {
        val listType = object : TypeToken<List<CitationLayoutData?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<CitationLayoutData?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}