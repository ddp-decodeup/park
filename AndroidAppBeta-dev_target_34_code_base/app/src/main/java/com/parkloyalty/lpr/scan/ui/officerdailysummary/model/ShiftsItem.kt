package com.parkloyalty.lpr.scan.ui.officerdailysummary.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ShiftsItem(
    @field:JsonProperty("scan_metrics")
    @get:JsonProperty("scan_metrics")
    var scanMetrics: ScanMetrics? = null,
    @field:JsonProperty("issuance_metrics")
    @get:JsonProperty("issuance_metrics")
    var issuanceMetrics: IssuanceMetrics? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,
    @field:JsonProperty("shift_details")
    @get:JsonProperty("shift_details")
    var shiftDetails: ShiftDetails? = null
) : Parcelable
