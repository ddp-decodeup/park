package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationBookletDatabaseModel(
    @field:JsonProperty("latest_citation_number")
    @get:JsonProperty("latest_citation_number")
    var latestCitationNumber: Long? = null
) : Parcelable
