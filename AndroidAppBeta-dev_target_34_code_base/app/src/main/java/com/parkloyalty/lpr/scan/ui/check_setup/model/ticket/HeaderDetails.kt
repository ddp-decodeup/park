package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class HeaderDetails(
    @field:JsonProperty("citation_number")
    @get:JsonProperty("citation_number")
    var citationNumber: String? = null,

    @field:JsonProperty("timestamp")
    @get:JsonProperty("timestamp")
    var timestamp: String? = null
) : Parcelable
