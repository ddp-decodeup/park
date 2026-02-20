package com.parkloyalty.lpr.scan.doubangoultimatelpr.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleColorDataItem(
    @field:JsonProperty("vehicle_color")
    @get:JsonProperty("vehicle_color")
    var vehicleColor: String? = null
) : Parcelable
