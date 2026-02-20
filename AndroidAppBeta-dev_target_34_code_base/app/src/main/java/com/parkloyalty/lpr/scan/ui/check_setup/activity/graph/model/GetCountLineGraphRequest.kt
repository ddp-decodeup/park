package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class GetCountLineGraphRequest(
    @field:JsonProperty("count_array_timeline")
    @get:JsonProperty("count_array_timeline")
    var countArrayTimeline: CountArrayTimeline? = null,

    @field:JsonProperty("count_type")
    @get:JsonProperty("count_type")
    var countType: String? = null
) : Parcelable
