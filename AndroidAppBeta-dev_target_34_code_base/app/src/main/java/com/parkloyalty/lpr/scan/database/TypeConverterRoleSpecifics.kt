package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeRoleSpecifics

object TypeConverterRoleSpecifics {
    @TypeConverter
    fun fromString(value: String?): WelcomeRoleSpecifics {
        val listType = object : TypeToken<WelcomeRoleSpecifics?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: WelcomeRoleSpecifics?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}