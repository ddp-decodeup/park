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
@Entity(tableName = "citation_issurance_printer")
data class CitationInsurrancePrintData(
    @PrimaryKey
    @ColumnInfo(name = "citation_number")
    @field:JsonProperty("citation_number")
    @get:JsonProperty("citation_number")
    var citationNumber: String = "",

    @ColumnInfo(name = "form_status")
    @field:JsonProperty("form_status")
    @get:JsonProperty("form_status")
    var formStatus: Int = 0,

    @ColumnInfo(name = "citation_data")
    @field:JsonProperty("citation_data")
    @get:JsonProperty("citation_data")
    var citationData: CitationIssuranceModel? = null,

    @ColumnInfo(name = "print_command")
    @field:JsonProperty("print_command")
    @get:JsonProperty("print_command")
    var printCommand: String? = null
) : Parcelable
