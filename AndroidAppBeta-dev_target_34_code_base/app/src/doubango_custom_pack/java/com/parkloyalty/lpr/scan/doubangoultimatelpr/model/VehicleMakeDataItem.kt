package com.parkloyalty.lpr.scan.doubangoultimatelpr.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleMakeDataItem(
    @field:JsonProperty("vehicle_make")
    @get:JsonProperty("vehicle_make")
    var vehicleMake: String? = null
) : Parcelable
