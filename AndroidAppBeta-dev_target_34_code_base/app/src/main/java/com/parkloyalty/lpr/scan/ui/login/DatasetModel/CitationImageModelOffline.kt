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
@Entity(tableName = "citation_images_offline")
data class CitationImageModelOffline(
    @ColumnInfo(name = "citation_image")
    @field:JsonProperty("citation_image")
    @get:JsonProperty("citation_image")
    var citationImage: String? = null,

    @ColumnInfo(name = "citation_number")
    @field:JsonProperty("citation_number")
    @get:JsonProperty("citation_number")
    var mCitationNumber: Int = 0,

    @ColumnInfo(name = "citation_number_text")
    @field:JsonProperty("citation_number_text")
    @get:JsonProperty("citation_number_text")
    var mCitationNumberText: String? = null,

    @ColumnInfo(name = "status")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Int = 0,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0
) : Parcelable
