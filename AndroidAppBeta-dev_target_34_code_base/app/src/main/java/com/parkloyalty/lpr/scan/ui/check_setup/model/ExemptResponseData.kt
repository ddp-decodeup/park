package com.parkloyalty.lpr.scan.ui.check_setup.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ExemptResponseData(
    @field:JsonProperty("vehicle_exempt_data")
    @get:JsonProperty("vehicle_exempt_data")
    var vehicleExemptData: List<VehicleExemptData>? = null
) : Parcelable
