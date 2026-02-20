package com.parkloyalty.lpr.scan.ui.supervisor.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class JsonMember912(
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
    var scanPaymentHit: Int = 0,

    @field:JsonProperty("issuance_cancelled")
    @get:JsonProperty("issuance_cancelled")
    var issuanceCancelled: Int = 0,

    @field:JsonProperty("drive_off_count")
    @get:JsonProperty("drive_off_count")
    var driveOffCount: Int = 0,

    @field:JsonProperty("last_issuance_timestamp")
    @get:JsonProperty("last_issuance_timestamp")
    var lastIssuanceTimestamp: String? = null,

    @field:JsonProperty("tvr_count")
    @get:JsonProperty("tvr_count")
    var tvrCount: Int = 0,

    @field:JsonProperty("pbc_cancel_count")
    @get:JsonProperty("pbc_cancel_count")
    var pbcCancelCount: Int = 0,

    @field:JsonProperty("issuance_VoidAndReissue")
    @get:JsonProperty("issuance_VoidAndReissue")
    var issuanceVoidAndReissue: Int = 0,

    @field:JsonProperty("first_issuance_timestamp")
    @get:JsonProperty("first_issuance_timestamp")
    var firstIssuanceTimestamp: String? = null,

    @field:JsonProperty("issuance_valid")
    @get:JsonProperty("issuance_valid")
    var issuanceValid: Int = 0,

    @field:JsonProperty("reissue_count")
    @get:JsonProperty("reissue_count")
    var reissueCount: Int = 0,

    @field:JsonProperty("issuance_rescind")
    @get:JsonProperty("issuance_rescind")
    var issuanceRescind: Int = 0,

    @field:JsonProperty("issuance_total")
    @get:JsonProperty("issuance_total")
    var issuanceTotal: Int = 0,

    @field:JsonProperty("officer_id")
    @get:JsonProperty("officer_id")
    var officerId: String? = null,

    @field:JsonProperty("device_name")
    @get:JsonProperty("device_name")
    var deviceName: String? = null,

    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var squad: String? = null,

    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,

    @field:JsonProperty("printer")
    @get:JsonProperty("printer")
    var printer: String? = null,

    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: Int = 0,

    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,

    @field:JsonProperty("username")
    @get:JsonProperty("username")
    var username: String? = null,

    @field:JsonProperty("lunch_timestamp")
    @get:JsonProperty("lunch_timestamp")
    var lunchTimestamp: String? = null,

    @field:JsonProperty("logout_timestamp")
    @get:JsonProperty("logout_timestamp")
    var logoutTimestamp: String? = null,

    @field:JsonProperty("break1_timestamp")
    @get:JsonProperty("break1_timestamp")
    var break1Timestamp: String? = null,

    @field:JsonProperty("break2_timestamp")
    @get:JsonProperty("break2_timestamp")
    var break2Timestamp: String? = null,

    @field:JsonProperty("login_timestamp")
    @get:JsonProperty("login_timestamp")
    var loginTimestamp: String? = null
) : Parcelable
