package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TimingResponseData(
    @field:JsonProperty("vehicle_timing_data")
    @get:JsonProperty("vehicle_timing_data")
    var vehicleTimingData: List<VehicleTimingData>? = null
) : Parcelable
