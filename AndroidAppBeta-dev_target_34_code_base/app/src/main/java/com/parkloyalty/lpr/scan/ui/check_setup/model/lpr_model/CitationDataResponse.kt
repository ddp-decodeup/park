package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.Location
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.NotesData
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.VehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.ViolationDetails
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationDataResponse(
    @field:JsonProperty("citation_issue_timestamp")
    @get:JsonProperty("citation_issue_timestamp")
    var citationIssueTimestamp: String? = null,

    @field:JsonProperty("citation_start_timestamp")
    @get:JsonProperty("citation_start_timestamp")
    var citationStartTimestamp: String? = null,

    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var code: String? = null,

    @field:JsonProperty("comment_details")
    @get:JsonProperty("comment_details")
    var commentDetails: CommentDetails? = null,

    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: Long? = null,

    @field:JsonProperty("header_details")
    @get:JsonProperty("header_details")
    var headerDetails: HeaderDetails? = null,

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

    @field:JsonProperty("violation_details")
    @get:JsonProperty("violation_details")
    var violationDetails: ViolationDetails? = null,

    @field:JsonProperty("vehicle_details")
    @get:JsonProperty("vehicle_details")
    var vehicleDetails: VehicleDetails? = null,

    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: String? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var ticketType: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,

    @field:JsonProperty("tvr")
    @get:JsonProperty("tvr")
    var tvr: String? = null,

    @field:JsonProperty("drive_off")
    @get:JsonProperty("drive_off")
    var driveOff: String? = null,

    @field:JsonProperty("print_query")
    @get:JsonProperty("print_query")
    var printQuery: String? = null
) : Parcelable
