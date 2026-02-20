package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class GetTimingLineResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<GetTimingLineData>? = null,

    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable
