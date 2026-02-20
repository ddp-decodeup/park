package com.parkloyalty.lpr.scan.ui.officerdailysummary.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ScanMetrics(
    @field:JsonProperty("first_scan_timestamp")
    @get:JsonProperty("first_scan_timestamp")
    var firstScanTimestamp: String? = null,
    @field:JsonProperty("scan_total_hits")
    @get:JsonProperty("scan_total_hits")
    var scanTotalHits: Int = 0,
    @field:JsonProperty("scan_scofflaw_hit")
    @get:JsonProperty("scan_scofflaw_hit")
    var scanScofflawHit: Int = 0,
    @field:JsonProperty("scan_timing_hit")
    @get:JsonProperty("scan_timing_hit")
    var scanTimingHit: Int = 0,
    @field:JsonProperty("scan_permit_hit")
    @get:JsonProperty("scan_permit_hit")
    var scanPermitHit: Int = 0,
    @field:JsonProperty("last_scan_timestamp")
    @get:JsonProperty("last_scan_timestamp")
    var lastScanTimestamp: String? = null,
    @field:JsonProperty("scan_payment_hit")
    @get:JsonProperty("scan_payment_hit")
    var scanPaymentHit: Int = 0
) : Parcelable
