package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class GetViolationResonse(
    @field:JsonProperty("violation_counts")
    @get:JsonProperty("violation_counts")
    var violationCounts: Long? = null,

    @field:JsonProperty("violation_name")
    @get:JsonProperty("violation_name")
    var violationName: String? = null
) : Parcelable
