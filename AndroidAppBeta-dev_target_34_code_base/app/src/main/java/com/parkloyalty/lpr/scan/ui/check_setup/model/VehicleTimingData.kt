package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleTimingData(
    @field:JsonProperty("agency_id")
    @get:JsonProperty("agency_id")
    var agencyId: String? = null,

    @field:JsonProperty("first_timestamp")
    @get:JsonProperty("first_timestamp")
    var firstTimestamp: String? = null,

    @field:JsonProperty("last_timestamp")
    @get:JsonProperty("last_timestamp")
    var lastTimestamp: String? = null,

    @field:JsonProperty("lat")
    @get:JsonProperty("lat")
    var lat: Long? = null,

    @field:JsonProperty("lng")
    @get:JsonProperty("lng")
    var lng: Long? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("marked")
    @get:JsonProperty("marked")
    var marked: Long? = null,

    @field:JsonProperty("officer_id")
    @get:JsonProperty("officer_id")
    var officerId: String? = null,

    @field:JsonProperty("reverse_geocoded_location")
    @get:JsonProperty("reverse_geocoded_location")
    var reverseGeocodedLocation: Long? = null,

    @field:JsonProperty("violated")
    @get:JsonProperty("violated")
    var violated: Long? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
