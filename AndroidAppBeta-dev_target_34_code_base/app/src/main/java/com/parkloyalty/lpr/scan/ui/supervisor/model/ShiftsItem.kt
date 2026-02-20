package com.parkloyalty.lpr.scan.ui.supervisor.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ShiftsItem(
    @field:JsonProperty("scan_metrics")
    @get:JsonProperty("scan_metrics")
    var scanMetrics: List<ScanMetricsItem>? = null,

    @field:JsonProperty("issuance_metrics")
    @get:JsonProperty("issuance_metrics")
    var issuanceMetrics: List<IssuanceMetricsItem>? = null,

    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: List<OfficerDetailsItem>? = null,

    @field:JsonProperty("activity_details")
    @get:JsonProperty("activity_details")
    var activityDetails: List<ActivityDetailsItem>? = null,

    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null
) : Parcelable
