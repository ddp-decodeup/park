package com.parkloyalty.lpr.scan.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeList

object TypeConverterWelcomeList {
    @TypeConverter
    fun fromString(value: String?): WelcomeList {
        val listType = object : TypeToken<WelcomeList?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: WelcomeList?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}