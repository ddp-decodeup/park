package com.parkloyalty.lpr.scan.doubangoultimatelpr.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleModelDataItem(
    @field:JsonProperty("vehicle_model")
    @get:JsonProperty("vehicle_model")
    var vehicleModel: String? = null
) : Parcelable
