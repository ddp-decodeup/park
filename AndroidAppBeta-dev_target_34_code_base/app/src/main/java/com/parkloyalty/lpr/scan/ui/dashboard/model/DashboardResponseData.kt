package com.parkloyalty.lpr.scan.ui.dashboard.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DashboardResponseData(
    @field:JsonProperty("total_enforcements")
    @get:JsonProperty("total_enforcements")
    var totalEnforcements: Long? = null,

    @field:JsonProperty("total_scans")
    @get:JsonProperty("total_scans")
    var totalScans: Long? = null
) : Parcelable
