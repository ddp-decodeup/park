package com.parkloyalty.lpr.scan.ui.boot.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleDetailsBoot(
    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: String? = null,

    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var vinNumber: @RawValue Any? = null,

    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,

    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null
) : Parcelable
