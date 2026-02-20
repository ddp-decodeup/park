package com.parkloyalty.lpr.scan.ui.login.DatasetModel

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
@Entity(tableName = "unUploadFacsimileImage")
data class UnUploadFacsimileImage(
    @ColumnInfo(name = "lprnumber")
    @field:JsonProperty("lprnumber")
    @get:JsonProperty("lprnumber")
    var lprNumber: String? = null,

    @ColumnInfo(name = "uploadedCitationId")
    @field:JsonProperty("uploadedCitationId")
    @get:JsonProperty("uploadedCitationId")
    var uploadedCitationId: String? = null,

    @PrimaryKey
    @ColumnInfo(name = "ticketNumber")
    @field:JsonProperty("ticketNumber")
    @get:JsonProperty("ticketNumber")
    var dateTime: Long = 0L,

    @ColumnInfo(name = "ticketNumberText")
    @field:JsonProperty("ticketNumberText")
    @get:JsonProperty("ticketNumberText")
    var ticketNumberText: String? = null,

    @ColumnInfo(name = "status")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Int = 0,

    @ColumnInfo(name = "imageLink")
    @field:JsonProperty("imageLink")
    @get:JsonProperty("imageLink")
    var imageLink: String? = null,

    @ColumnInfo(name = "imagePath")
    @field:JsonProperty("imagePath")
    @get:JsonProperty("imagePath")
    var imagePath: String? = null,

    @ColumnInfo(name = "imageCount")
    @field:JsonProperty("imageCount")
    @get:JsonProperty("imageCount")
    var imageCount: Int = 0,

    @ColumnInfo(name = "imageType")
    @field:JsonProperty("imageType")
    @get:JsonProperty("imageType")
    var imageType: String = ""
) : Parcelable
