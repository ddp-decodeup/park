package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Timeseries(
    @field:JsonProperty("to_ts")
    @get:JsonProperty("to_ts")
    var toTs: String? = null,
    @field:JsonProperty("from_ts")
    @get:JsonProperty("from_ts")
    var fromTs: String? = null
) : Parcelable
