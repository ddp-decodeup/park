package com.parkloyalty.lpr.scan.ui.reprint.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.MotoristDetailsModel
import com.parkloyalty.lpr.scan.ui.ticket.model.AuditTrailItem
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataItem(
    @field:JsonProperty("payment_done")
    @get:JsonProperty("payment_done")
    var paymentDone: Boolean = false,
    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var code: String? = null,
    @field:JsonProperty("payment_details")
    @get:JsonProperty("payment_details")
    var paymentDetails: PaymentDetails? = null,
    @field:JsonProperty("violation_details")
    @get:JsonProperty("violation_details")
    var violationDetails: ViolationDetails? = null,
    @field:JsonProperty("comment_details")
    @get:JsonProperty("comment_details")
    var commentDetails: CommentDetails? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double = 0.0,
    @field:JsonProperty("citation_id")
    @get:JsonProperty("citation_id")
    var citationId: String? = null,
    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: Int = 0,
    @field:JsonProperty("time_limit_enforcement_observed_time")
    @get:JsonProperty("time_limit_enforcement_observed_time")
    var timeLimitEnforcementObservedTime: String? = null,
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,
    @field:JsonProperty("citation_issue_timestamp")
    @get:JsonProperty("citation_issue_timestamp")
    var citationIssueTimestamp: String? = null,
    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: String? = null,
    @field:JsonProperty("fine_amount")
    @get:JsonProperty("fine_amount")
    var fineAmount: Int = 0,
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,
    @field:JsonProperty("reissue")
    @get:JsonProperty("reissue")
    var reissue: Boolean = false,
    @field:JsonProperty("header_details")
    @get:JsonProperty("header_details")
    var headerDetails: HeaderDetails? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double = 0.0,
    @field:JsonProperty("images")
    @get:JsonProperty("images")
    var images: List<String>? = null,
    @field:JsonProperty("time_limit_enforcement")
    @get:JsonProperty("time_limit_enforcement")
    var timeLimitEnforcement: Boolean = false,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetails? = null,
    @field:JsonProperty("scofflaw_id")
    @get:JsonProperty("scofflaw_id")
    var scofflawId: String? = null,
    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null,
    @field:JsonProperty("tvr")
    @get:JsonProperty("tvr")
    var tvr: Boolean = false,
    @field:JsonProperty("citation_start_timestamp")
    @get:JsonProperty("citation_start_timestamp")
    var citationStartTimestamp: String? = null,
    @field:JsonProperty("audit_trail")
    @get:JsonProperty("audit_trail")
    var auditTrail: List<AuditTrailItem>? = null,
    @field:JsonProperty("time_limit_enforcement_id")
    @get:JsonProperty("time_limit_enforcement_id")
    var timeLimitEnforcementId: String? = null,
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: Location? = null,
    @field:JsonProperty("drive_off")
    @get:JsonProperty("drive_off")
    var driveOff: Boolean = false,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,
    @field:JsonProperty("vehicle_details")
    @get:JsonProperty("vehicle_details")
    var vehicleDetails: VehicleDetails? = null,
    @field:JsonProperty("motorist_details")
    @get:JsonProperty("motorist_details")
    var motoristDetails: MotoristDetailsModel? = null
) : Parcelable
