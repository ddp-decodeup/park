package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ActivityLogRequest(
    @field:JsonProperty("count_array_timeline")
    @get:JsonProperty("count_array_timeline")
    var countArrayTimeline: BarCountArrayTimeline? = null
) : Parcelable
