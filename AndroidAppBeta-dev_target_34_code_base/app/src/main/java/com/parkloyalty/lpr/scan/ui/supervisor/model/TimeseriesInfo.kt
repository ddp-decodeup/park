package com.parkloyalty.lpr.scan.ui.supervisor.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TimeseriesInfo(
    @field:JsonProperty("end_timestamp")
    @get:JsonProperty("end_timestamp")
    var endTimestamp: String? = null,

    @field:JsonProperty("start_timestamp")
    @get:JsonProperty("start_timestamp")
    var startTimestamp: String? = null
) : Parcelable
