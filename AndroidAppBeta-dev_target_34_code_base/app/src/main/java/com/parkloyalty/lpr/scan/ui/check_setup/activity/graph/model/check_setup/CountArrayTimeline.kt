package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CountArrayTimeline(
    @field:JsonProperty("timestamp_end")
    @get:JsonProperty("timestamp_end")
    var timestampEnd: Long? = null,

    @field:JsonProperty("timestamp_start")
    @get:JsonProperty("timestamp_start")
    var timestampStart: Long? = null,

    @field:JsonProperty("tz")
    @get:JsonProperty("tz")
    var tz: String? = null
) : Parcelable
