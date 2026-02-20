package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletDatabaseModel

object TypeConverterCitationNumber {
    @TypeConverter
    fun fromString(value: String?): CitationBookletDatabaseModel {
        val listType = object : TypeToken<CitationBookletDatabaseModel?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: CitationBookletDatabaseModel?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}