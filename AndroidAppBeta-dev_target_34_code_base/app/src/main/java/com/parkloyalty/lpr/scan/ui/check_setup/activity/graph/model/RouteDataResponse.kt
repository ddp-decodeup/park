package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class RouteDataResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: ArrayList<DataItem>? = null,

    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var isSuccess: Boolean = false
) : Parcelable
