package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import com.parkloyalty.lpr.scan.ui.ticket.model.CommentDetails

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationMixedList(
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

    @field:JsonProperty("vehicle_details")
    @get:JsonProperty("vehicle_details")
    var vehicleDetails: VehicleDetails? = null,

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
    var notes: String? = null,

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

    @field:JsonProperty("ticket_type")
    @get:JsonProperty("ticket_type")
    var ticketType: String? = null,

    @field:JsonProperty("upload_status")
    @get:JsonProperty("upload_status")
    var uploadStatus: Int = 0,

    @field:JsonProperty("print_bitmap")
    @get:JsonProperty("print_bitmap")
    var mPrintBitmap: String? = null
) : Parcelable
