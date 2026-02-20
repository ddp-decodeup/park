package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponseLineGraph(
    @field:JsonProperty("aggregate")
    @get:JsonProperty("aggregate")
    var aggregate: List<Long>? = null,

    @field:JsonProperty("dataset_name")
    @get:JsonProperty("dataset_name")
    var datasetName: String? = null
) : Parcelable
