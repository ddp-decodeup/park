package com.parkloyalty.lpr.scan.ui.check_setup.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MeterResponseData(
    @field:JsonProperty("vehicle_meter_data")
    @get:JsonProperty("vehicle_meter_data")
    var vehicleMeterData: List<LprVehicleMeterData>? = null
) : Parcelable
