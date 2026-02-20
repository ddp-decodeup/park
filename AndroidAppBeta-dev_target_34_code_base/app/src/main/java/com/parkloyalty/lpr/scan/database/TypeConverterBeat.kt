package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.model.OfficerBeat

object TypeConverterBeat {
    @TypeConverter
    fun fromString(value: String?): OfficerBeat {
        val listType = object : TypeToken<OfficerBeat?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: OfficerBeat?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}

//package com.parkloyalty.lpr.scan.database
//
//import androidx.room.TypeConverter
//import com.parkloyalty.lpr.scan.ui.login.model.OfficerBeat
//import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
//
//object TypeConverterBeat {
//    private val objectMapper = ObjectMapperProvider.instance
//
//    @TypeConverter
//    fun fromString(value: String?): OfficerBeat? {
//        if (value == null) return null
//        return objectMapper.readValue(value, OfficerBeat::class.java)
//    }
//
//    @TypeConverter
//    fun fromArrayList(list: OfficerBeat?): String? {
//        if (list == null) return null
//        return objectMapper.writeValueAsString(list)
//    }
//}