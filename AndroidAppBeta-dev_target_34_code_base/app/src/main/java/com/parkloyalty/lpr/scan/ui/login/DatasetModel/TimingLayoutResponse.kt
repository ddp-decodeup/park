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
@Entity(tableName = "timing_layout")
data class TimingLayoutResponse(
    @ColumnInfo(name = "data")
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<ActivityLayoutData>? = null,

    @ColumnInfo(name = "message")
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,

    @ColumnInfo(name = "status")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null,

    @ColumnInfo(name = "response")
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: String? = null,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0,

    @ColumnInfo(name = "upload_status")
    @field:JsonProperty("upload_status")
    @get:JsonProperty("upload_status")
    var uploadStatus: Int = 0
) : Parcelable
