package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
@Entity(tableName = "citation_number")
data class CitationNumberDatabaseModel(
    @ColumnInfo(name = "metadata")
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: @RawValue Any? = null,

    @ColumnInfo(name = "response")
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: CitationBookletDatabaseModel? = null,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0
) : Parcelable
