package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class HistoryData(
    @field:JsonProperty("vehicle_data")
    @get:JsonProperty("vehicle_data")
    var dataOne: String? = null,

    @field:JsonProperty("vehicle_data")
    @get:JsonProperty("vehicle_data")
    var dataTwo: String? = null,

    @field:JsonProperty("vehicle_data")
    @get:JsonProperty("vehicle_data")
    var dataThree: String? = null
) : Parcelable
