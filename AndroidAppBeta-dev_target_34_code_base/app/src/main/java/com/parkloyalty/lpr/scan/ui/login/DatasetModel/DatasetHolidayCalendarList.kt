package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "dataset_holiday_calendar_list")
data class DatasetHolidayCalendarList(
    @ColumnInfo(name = "holiday_calendar_list")
    @field:JsonProperty("HolidayCalendarList")
    @get:JsonProperty("HolidayCalendarList")
    var holidayCalendatList: List<DatasetResponse>? = null,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 1
) : Parcelable
