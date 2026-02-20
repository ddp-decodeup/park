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
@Entity(tableName = "citation_issurance")
data class CitationInsurranceDatabaseModel(
    @PrimaryKey
    @ColumnInfo(name = "citation_number")
    @field:JsonProperty("citation_number")
    @get:JsonProperty("citation_number")
    var citationNumber: String = "",


    @ColumnInfo(name = "form_status")
    @field:JsonProperty("form_status")
    @get:JsonProperty("form_status")
    var formStatus: Int? = 0,

    @ColumnInfo(name = "citation_data")
    @field:JsonProperty("citation_data")
    @get:JsonProperty("citation_data")
    var citationData: CitationIssuranceModel? = null
) : Parcelable
