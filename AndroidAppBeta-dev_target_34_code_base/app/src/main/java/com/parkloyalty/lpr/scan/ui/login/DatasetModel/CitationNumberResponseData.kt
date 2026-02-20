package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationNumberResponseData(
    @field:JsonProperty("citation_booklet")
    @get:JsonProperty("citation_booklet")
    var citationBooklet: List<String>? = null,

    @field:JsonProperty("latest_citation_number")
    @get:JsonProperty("latest_citation_number")
    var latestCitationNumber: Long? = null
) : Parcelable
