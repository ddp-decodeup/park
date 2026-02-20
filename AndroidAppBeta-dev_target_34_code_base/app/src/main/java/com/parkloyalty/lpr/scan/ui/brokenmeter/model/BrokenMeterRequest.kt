package com.parkloyalty.lpr.scan.ui.brokenmeter.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BrokenMeterRequest(
    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null,
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetails? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetails? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: Details? = null,
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null
) : Parcelable
