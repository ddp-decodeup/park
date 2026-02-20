package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MakeModelColorData(
    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: String? = null,

    @field:JsonProperty("int64_0")
    @get:JsonProperty("int64_0")
    var int640: Long? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,

    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,

    @field:JsonProperty("body_style")
    @get:JsonProperty("body_style")
    var bodyStyle: String? = null,

    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var vinNumber: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null
) : Parcelable
