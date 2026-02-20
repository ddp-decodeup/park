package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PushEventLocation(
    @field:JsonProperty("lat")
    @get:JsonProperty("lat")
    var lat: String? = null,
    @field:JsonProperty("lng")
    @get:JsonProperty("lng")
    var lng: String? = null,
    @field:JsonProperty("reverse_geo_coded_address")
    @get:JsonProperty("reverse_geo_coded_address")
    var reverseGeoCodedAddress: String? = null
) : Parcelable
