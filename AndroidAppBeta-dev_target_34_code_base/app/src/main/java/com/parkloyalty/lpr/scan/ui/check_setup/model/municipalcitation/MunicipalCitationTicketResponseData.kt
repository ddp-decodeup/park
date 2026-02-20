package com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MunicipalCitationTicketResponseData(
    @field:JsonProperty("citation_issue_timestamp")
    @get:JsonProperty("citation_issue_timestamp")
    var citationIssueTimestamp: String? = null,
    @field:JsonProperty("citation_start_timestamp")
    @get:JsonProperty("citation_start_timestamp")
    var citationStartTimestamp: String? = null,
    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var code: String? = null,
    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: String? = null,
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,
    @field:JsonProperty("images")
    @get:JsonProperty("images")
    var images: List<String>? = null,
    @field:JsonProperty("location_id")
    @get:JsonProperty("location_id")
    var locationId: String? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lprNumber: String? = null,
    @field:JsonProperty("notes")
    @get:JsonProperty("notes")
    var notes: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: MunicipalCitationOfficerDetails? = null,
    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: String? = null,
    @field:JsonProperty("ticket_type")
    @get:JsonProperty("ticket_type")
    var ticketType: String? = null,
    @field:JsonProperty("vehicle_lpr_number")
    @get:JsonProperty("vehicle_lpr_number")
    var vehicleLprNumber: String? = null
) : Parcelable
