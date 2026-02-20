package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class GetScanLineData(
    @field:JsonProperty("scans")
    @get:JsonProperty("scans")
    var scans: List<Long>? = null
) : Parcelable
