package com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MunicipalCitationVehicleDetails(
    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: String? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lprNo: String? = null,
    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,
    @field:JsonProperty("body_style")
    @get:JsonProperty("body_style")
    var body_style: String? = null,
    @field:JsonProperty("body_style_lookup_code")
    @get:JsonProperty("body_style_lookup_code")
    var body_style_lookup_code: String? = null,
    @field:JsonProperty("decal_year")
    @get:JsonProperty("decal_year")
    var decal_year: String? = null,
    @field:JsonProperty("decal_number")
    @get:JsonProperty("decal_number")
    var decal_number: String? = null,
    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var vin_number: String? = null,
    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,
    @field:JsonProperty("model_lookup_code")
    @get:JsonProperty("model_lookup_code")
    var model_lookup_code: String? = null,
    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,
    @field:JsonProperty("license_expiry")
    @get:JsonProperty("license_expiry")
    var mLicenseExpiry: String? = null
) : Parcelable
