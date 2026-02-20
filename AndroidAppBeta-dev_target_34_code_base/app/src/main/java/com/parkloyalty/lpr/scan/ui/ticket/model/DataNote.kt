package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataNote(
    @field:JsonProperty("ticket_type")
    @get:JsonProperty("ticket_type")
    var ticketType: String? = null,
    @field:JsonProperty("payment_done")
    @get:JsonProperty("payment_done")
    var isPaymentDone: Boolean = false,
    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var code: String? = null,
    @field:JsonProperty("notes")
    @get:JsonProperty("notes")
    var notes: List<NotesItem>? = null,
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var locationNote: LocationNote? = null,
    @field:JsonProperty("payment_details")
    @get:JsonProperty("payment_details")
    var paymentDetails: @RawValue Any? = null,
    @field:JsonProperty("comment_details")
    @get:JsonProperty("comment_details")
    var commentDetailsNote: CommentDetailsNote? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double = 0.0,
    @field:JsonProperty("citation_id")
    @get:JsonProperty("citation_id")
    var citationId: String? = null,
    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: Int = 0,
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
    var fineAmount: Float = 0.0f,
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,
    @field:JsonProperty("reissue")
    @get:JsonProperty("reissue")
    var isReissue: Boolean = false,
    @field:JsonProperty("header_details")
    @get:JsonProperty("header_details")
    var headerDetailsNote: HeaderDetailsNote? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double = 0.0,
    @field:JsonProperty("images")
    @get:JsonProperty("images")
    var images: List<String>? = null,
    @field:JsonProperty("time_limit_enforcement")
    @get:JsonProperty("time_limit_enforcement")
    var isTimeLimitEnforcement: Boolean = false,
    @field:JsonProperty("vehicle_details")
    @get:JsonProperty("vehicle_details")
    var vehicleDetailsNote: VehicleDetailsNote? = null,
    @field:JsonProperty("scofflaw_id")
    @get:JsonProperty("scofflaw_id")
    var scofflawId: String? = null,
    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null,
    @field:JsonProperty("tvr")
    @get:JsonProperty("tvr")
    var isTvr: Boolean = false,
    @field:JsonProperty("citation_start_timestamp")
    @get:JsonProperty("citation_start_timestamp")
    var citationStartTimestamp: String? = null,
    @field:JsonProperty("audit_trail")
    @get:JsonProperty("audit_trail")
    var auditTrail: List<AuditTrailItem>? = null,
    @field:JsonProperty("violation_details")
    @get:JsonProperty("violation_details")
    var violationDetailsNote: ViolationDetailsNote? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetailsNote: OfficerDetailsNote? = null,
    @field:JsonProperty("drive_off")
    @get:JsonProperty("drive_off")
    var isDriveOff: Boolean = false,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,
    @field:JsonProperty("Note_1")
    @get:JsonProperty("Note_1")
    var mNote1: String? = null,
    @field:JsonProperty("Note_2")
    @get:JsonProperty("Note_2")
    var mNote2: String? = null,
    @field:JsonProperty("Note_3")
    @get:JsonProperty("Note_3")
    var mNote3: String? = null
) : Parcelable
