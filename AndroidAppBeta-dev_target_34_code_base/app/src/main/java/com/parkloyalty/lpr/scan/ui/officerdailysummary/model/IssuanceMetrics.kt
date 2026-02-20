package com.parkloyalty.lpr.scan.ui.officerdailysummary.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class IssuanceMetrics(
    @field:JsonProperty("issuance_cancelled")
    @get:JsonProperty("issuance_cancelled")
    var issuanceCancelled: Int = 0,
    @field:JsonProperty("drive_off_count")
    @get:JsonProperty("drive_off_count")
    var driveOffCount: Int = 0,
    @field:JsonProperty("cancel")
    @get:JsonProperty("cancel")
    var cancel: Int = 0,
    @field:JsonProperty("total_cancel")
    @get:JsonProperty("total_cancel")
    var totalCancel: Int = 0,
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
    var issuanceTotal: Int = 0
) : Parcelable
