package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLayoutData
import com.parkloyalty.lpr.scan.ui.honorbill.HonorBillCitationLayoutData

object TypeConverterOwnerBillLayout {
    @TypeConverter
    fun fromString(value: String?): List<HonorBillCitationLayoutData> {
        val listType = object : TypeToken<List<MunicipalCitationLayoutData?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<HonorBillCitationLayoutData?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
