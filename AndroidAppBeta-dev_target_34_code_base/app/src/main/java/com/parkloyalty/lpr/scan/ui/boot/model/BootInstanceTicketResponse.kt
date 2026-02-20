package com.parkloyalty.lpr.scan.ui.boot.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BootInstanceTicketResponse(
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,

    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: Data? = Data(),

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BootMetadata(
    @field:JsonProperty("boot_type")
    @get:JsonProperty("boot_type")
    var bootType: String? = null,

    @field:JsonProperty("license_plate_number")
    @get:JsonProperty("license_plate_number")
    var licensePlateNumber: String? = null,

    @field:JsonProperty("license_plate_state")
    @get:JsonProperty("license_plate_state")
    var licensePlateState: String? = null,

    @field:JsonProperty("plate_type")
    @get:JsonProperty("plate_type")
    var plateType: String? = null,

    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class AuditTrail(
    @field:JsonProperty("action")
    @get:JsonProperty("action")
    var action: String? = null,

    @field:JsonProperty("initiator_name")
    @get:JsonProperty("initiator_name")
    var initiatorName: String? = null,

    @field:JsonProperty("initiator_id")
    @get:JsonProperty("initiator_id")
    var initiatorId: String? = null,

    @field:JsonProperty("timestamp")
    @get:JsonProperty("timestamp")
    var timestamp: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Data(
    @field:JsonProperty("boot_issue_timestamp")
    @get:JsonProperty("boot_issue_timestamp")
    var bootIssueTimestamp: String? = null,

    @field:JsonProperty("initiator_name")
    @get:JsonProperty("initiator_name")
    var initiatorName: String? = null,

    @field:JsonProperty("boot_release_timestamp")
    @get:JsonProperty("boot_release_timestamp")
    var bootReleaseTimestamp: String? = null,

    @field:JsonProperty("boot_paid_timestamp")
    @get:JsonProperty("boot_paid_timestamp")
    var bootPaidTimestamp: String? = null,

    @field:JsonProperty("boot_returned_timestamp")
    @get:JsonProperty("boot_returned_timestamp")
    var bootReturnedTimestamp: String? = null,

    @field:JsonProperty("boot_unreturned_charge_applied_timestamp")
    @get:JsonProperty("boot_unreturned_charge_applied_timestamp")
    var bootUnreturnedChargeAppliedTimestamp: String? = null,

    @field:JsonProperty("boot_fees")
    @get:JsonProperty("boot_fees")
    var bootFees: Int? = null,

    @field:JsonProperty("boot_non_return_fee")
    @get:JsonProperty("boot_non_return_fee")
    var bootNonReturnFee: Int? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,

    @field:JsonProperty("boot_metadata")
    @get:JsonProperty("boot_metadata")
    var bootMetadata: BootMetadata? = BootMetadata(),

    @field:JsonProperty("audit_trail")
    @get:JsonProperty("audit_trail")
    var auditTrail: ArrayList<AuditTrail> = arrayListOf(),

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null
) : Parcelable
