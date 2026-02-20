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
@Entity(tableName = "citation_booklet")
data class CitationBookletModel(
    @PrimaryKey
    @ColumnInfo(name = "citation_booklet")
    @field:JsonProperty("citation_booklet")
    @get:JsonProperty("citation_booklet")
    var citationBooklet: String = "",

    @ColumnInfo(name = "status")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var mStatus: Int? = 0
) : Parcelable
