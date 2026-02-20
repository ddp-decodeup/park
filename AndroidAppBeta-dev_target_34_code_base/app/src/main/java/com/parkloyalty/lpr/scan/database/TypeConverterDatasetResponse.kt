package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse

class TypeConverterDatasetResponse {

    @TypeConverter
    fun fromDatasetResponseList(value: List<DatasetResponse>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toDatasetResponseList(value: String): List<DatasetResponse>? {
        val listType = object : TypeToken<List<DatasetResponse>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
