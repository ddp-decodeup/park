package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SofflawResponseData(
    @field:JsonProperty("vehicle_exempt_data")
    @get:JsonProperty("vehicle_exempt_data")
    var vehicleExemptData: List<VehicleSofflawData>? = null
) : Parcelable
