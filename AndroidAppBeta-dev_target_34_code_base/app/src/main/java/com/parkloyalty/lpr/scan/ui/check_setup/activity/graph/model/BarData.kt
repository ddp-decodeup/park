package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BarData(
    @field:JsonProperty("drive_offs")
    @get:JsonProperty("drive_offs")
    var driveOffs: Long? = null,

    @field:JsonProperty("permits")
    @get:JsonProperty("permits")
    var permits: Long? = null,

    @field:JsonProperty("revenues")
    @get:JsonProperty("revenues")
    var revenues: Long? = null,

    @field:JsonProperty("scans")
    @get:JsonProperty("scans")
    var scans: Long? = null,

    @field:JsonProperty("scofflaws")
    @get:JsonProperty("scofflaws")
    var scofflaws: Long? = null,

    @field:JsonProperty("tickets")
    @get:JsonProperty("tickets")
    var tickets: Long? = null,

    @field:JsonProperty("timings")
    @get:JsonProperty("timings")
    var timings: Long? = null
) : Parcelable
