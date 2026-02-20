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
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
@Entity(tableName = "timing_images")
data class TimingImagesModel(
    @ColumnInfo(name = "timing_image")
    @field:JsonProperty("timing_image")
    @get:JsonProperty("timing_image")
    var timingImage: String? = null,

    @ColumnInfo(name = "status")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Int = 0,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0,

    @ColumnInfo(name = "timingRecordId")
    @field:JsonProperty("timingRecordId")
    @get:JsonProperty("timingRecordId")
    var timingRecordId: Int = 0,

    @ColumnInfo(name = "showDeleteButton")
    @field:JsonProperty("showDeleteButton")
    @get:JsonProperty("showDeleteButton")
    var deleteButtonStatus: Int = 1
) : Parcelable
