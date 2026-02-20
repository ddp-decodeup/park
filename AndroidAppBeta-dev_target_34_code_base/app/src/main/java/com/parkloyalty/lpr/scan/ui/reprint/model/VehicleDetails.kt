package com.parkloyalty.lpr.scan.ui.reprint.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleDetails(
    @field:JsonProperty("decal_number")
    @get:JsonProperty("decal_number")
    var decalNumber: String? = null,

    @field:JsonProperty("decal_year")
    @get:JsonProperty("decal_year")
    var decalYear: String? = null,

    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: String? = null,

    @field:JsonProperty("color_full")
    @get:JsonProperty("color_full")
    var tempColorFull: String? = null,

    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var vinNumber: String? = null,

    @field:JsonProperty("license_expiry")
    @get:JsonProperty("license_expiry")
    var licenseExpiry: String? = null,

    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,

    @field:JsonProperty("body_style")
    @get:JsonProperty("body_style")
    var bodyStyle: String? = null,

    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,

    @field:JsonProperty("make_full")
    @get:JsonProperty("make_full")
    var tempMakeFull: String? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null
) : Parcelable
