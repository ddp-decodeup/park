package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Datum(
    @field:JsonProperty("citation_issue_timestamp")
    @get:JsonProperty("citation_issue_timestamp")
    var citationIssueTimestamp: String? = null,

    @field:JsonProperty("citation_start_timestamp")
    @get:JsonProperty("citation_start_timestamp")
    var citationStartTimestamp: String? = null,

    @field:JsonProperty("comment_details")
    @get:JsonProperty("comment_details")
    var commentDetails: CommentDetails? = null,

    @field:JsonProperty("violation_details")
    @get:JsonProperty("violation_details")
    var violationDetails: ViolationDetails? = null,

    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: Long? = null,

    @field:JsonProperty("header_details")
    @get:JsonProperty("header_details")
    var headerDetails: HeaderDetails? = null,

    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,

    @field:JsonProperty("images")
    @get:JsonProperty("images")
    var images: List<String>? = null,

    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: Location? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("notes")
    @get:JsonProperty("notes")
    var notes: List<NotesData>? = null,

    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetails? = null,

    @field:JsonProperty("officer_id")
    @get:JsonProperty("officer_id")
    var officerId: String? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,

    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: String? = null,

    @field:JsonProperty("print_query")
    @get:JsonProperty("print_query")
    var print_query: String? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var ticketType: String? = null,

    @field:JsonProperty("time_limit_enforcement_observed_time")
    @get:JsonProperty("time_limit_enforcement_observed_time")
    var timeLimitEnforcementObservedTime: String? = null,

    @field:JsonProperty("vehicle_details")
    @get:JsonProperty("vehicle_details")
    var vehicleDetails: VehicleDetails? = null,

    @field:JsonProperty("audit_trail")
    @get:JsonProperty("audit_trail")
    var auditTrail: List<AuditTrail>? = null,

    @field:JsonProperty("drive_off")
    @get:JsonProperty("drive_off")
    var isDriveOff: Boolean = false,

    @field:JsonProperty("tvr")
    @get:JsonProperty("tvr")
    var isTvr: Boolean = false,

    @field:JsonProperty("upload_status")
    @get:JsonProperty("upload_status")
    var uploadStatus: Int = 0,

    @field:JsonProperty("category")
    @get:JsonProperty("category")
    var category: String? = null,

    @field:JsonProperty("motorist_details")
    @get:JsonProperty("motorist_details")
    var municipalCitationMotoristDetailsModel: MotoristDetailsModel? = null
) : Parcelable
