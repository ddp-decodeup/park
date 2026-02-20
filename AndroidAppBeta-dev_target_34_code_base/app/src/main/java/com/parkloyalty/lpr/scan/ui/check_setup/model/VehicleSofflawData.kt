package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleSofflawData(
    @field:JsonProperty("agency_id")
    @get:JsonProperty("agency_id")
    var agencyId: String? = null,

    @field:JsonProperty("lat")
    @get:JsonProperty("lat")
    var lat: Long? = null,

    @field:JsonProperty("lng")
    @get:JsonProperty("lng")
    var lng: Long? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("officer_id")
    @get:JsonProperty("officer_id")
    var officerId: String? = null,

    @field:JsonProperty("reverse_geocoded_location")
    @get:JsonProperty("reverse_geocoded_location")
    var reverseGeocodedLocation: Long? = null,

    @field:JsonProperty("scofflaw_id")
    @get:JsonProperty("scofflaw_id")
    var scofflawId: Long? = null,

    @field:JsonProperty("scofflaw_type")
    @get:JsonProperty("scofflaw_type")
    var scofflawType: Long? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Long? = null,

    @field:JsonProperty("timestamp")
    @get:JsonProperty("timestamp")
    var timestamp: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
