package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationIssuranceModel

object TypeConverterCitationInsurrance {

    @TypeConverter
    fun fromString(value: String?): CitationIssuranceModel {
        val listType = object : TypeToken<CitationIssuranceModel?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: CitationIssuranceModel?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}