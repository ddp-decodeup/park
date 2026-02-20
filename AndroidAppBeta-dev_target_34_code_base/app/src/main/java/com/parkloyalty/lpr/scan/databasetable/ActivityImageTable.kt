package com.parkloyalty.lpr.scan.databasetable

import androidx.annotation.Keep
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
@Entity(tableName = "activity_image_table")
data class ActivityImageTable(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0,

    @ColumnInfo(name = "response_id")
    @field:JsonProperty("response_id")
    @get:JsonProperty("response_id")
    var activityResponseId: String? = null,

    @ColumnInfo(name = "from")
    @field:JsonProperty("from")
    @get:JsonProperty("from")
    var from: String? = null,

    @ColumnInfo(name = "upload_status")
    @field:JsonProperty("upload_status")
    @get:JsonProperty("upload_status")
    var uploadStatus: String? = "false",

    @ColumnInfo(name = "image1")
    @field:JsonProperty("image1")
    @get:JsonProperty("image1")
    var image1: String = "",

    @ColumnInfo(name = "image2")
    @field:JsonProperty("image2")
    @get:JsonProperty("image2")
    var image2: String = "",

    @ColumnInfo(name = "image3")
    @field:JsonProperty("image3")
    @get:JsonProperty("image3")
    var image3: String = ""
) : Parcelable
