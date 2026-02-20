package com.parkloyalty.lpr.scan.ui.boot.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OnGroundStatus(
    @field:JsonProperty("tow_truck_operator")
    @get:JsonProperty("tow_truck_operator")
    var towTruckOperator: String? = null,

    @field:JsonProperty("eta")
    @get:JsonProperty("eta")
    var eta: Int = 0,

    @field:JsonProperty("phone")
    @get:JsonProperty("phone")
    var phone: String? = null,

    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Int = 0,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,

    @field:JsonProperty("current_location")
    @get:JsonProperty("current_location")
    var currentLocation: String? = null,

    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Int = 0
) : Parcelable
