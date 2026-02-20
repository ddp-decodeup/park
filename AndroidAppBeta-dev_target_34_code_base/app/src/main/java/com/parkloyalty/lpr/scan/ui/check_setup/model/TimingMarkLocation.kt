package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TimingMarkLocation(
    @field:JsonProperty("Coordinates")
    @get:JsonProperty("Coordinates")
    var coordinates: List<Double>? = null,

    @field:JsonProperty("Type")
    @get:JsonProperty("Type")
    var type: String? = null
) : Parcelable
