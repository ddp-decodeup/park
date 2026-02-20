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
@Entity(tableName = "citation_layout")
data class CitationLayoutResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    @ColumnInfo(name = "data")
    var data: List<CitationLayoutData>? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    @ColumnInfo(name = "success")
    var success: Boolean? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    @ColumnInfo(name = "response")
    var response: String? = null,

    @PrimaryKey
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    @ColumnInfo(name = "id")
    var id: Int = 0
) : Parcelable
