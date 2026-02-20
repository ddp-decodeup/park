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
@Entity(tableName = "citation_images")
data class CitationImagesModel(
    @ColumnInfo(name = "citation_image")
    @field:JsonProperty("citation_image")
    @get:JsonProperty("citation_image")
    var citationImage: String? = null,

    @ColumnInfo(name = "status")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Int = 0,

    @ColumnInfo(name = "type")
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: Int = 0,

    @ColumnInfo(name = "edit")
    @field:JsonProperty("edit")
    @get:JsonProperty("edit")
    var edit: Int = 0,

    @ColumnInfo(name = "timeImagePath")
    @field:JsonProperty("timeImagePath")
    @get:JsonProperty("timeImagePath")
    var timeImagePath: String? = null,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0
) : Parcelable
