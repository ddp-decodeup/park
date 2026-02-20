package com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.MotoristDetailsModel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CreateMunicipalCitationTicketRequest(
    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var code: String? = null,
    @field:JsonProperty("hearing_date")
    @get:JsonProperty("hearing_date")
    var hearingDate: String? = null,
    @field:JsonProperty("image_urls")
    @get:JsonProperty("image_urls")
    var imageUrls: List<String>? = null,
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: MunicipalCitationLocationDetails? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lprNumber: String? = null,
    @field:JsonProperty("notes")
    @get:JsonProperty("notes")
    var notes: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: MunicipalCitationOfficerDetails? = null,
    @field:JsonProperty("comment_details")
    @get:JsonProperty("comment_details")
    var commentsDetails: MunicipalCitationCommentsDetails? = null,
    @field:JsonProperty("invoice_fee_structure")
    @get:JsonProperty("invoice_fee_structure")
    var invoiceFeeStructure: MunicipalCitationInvoiceFeeStructure? = null,
    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: String? = null,
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,
    @field:JsonProperty("time_limit_enforcement_observed_time")
    @get:JsonProperty("time_limit_enforcement_observed_time")
    var timeLimitEnforcementObservedTime: String? = null,
    @field:JsonProperty("vehicle_details")
    @get:JsonProperty("vehicle_details")
    var vehicleDetails: MunicipalCitationVehicleDetails? = null,
    @field:JsonProperty("violation_details")
    @get:JsonProperty("violation_details")
    var violationDetails: MunicipalCitationViolationDetails? = null,
    @field:JsonProperty("header_details")
    @get:JsonProperty("header_details")
    var headerDetails: MunicipalCitationHeaderDetails? = null,
    @field:JsonProperty("motorist_details")
    @get:JsonProperty("motorist_details")
    var motoristDetails: MotoristDetailsModel? = null,
    @field:JsonProperty("citation_issue_timestamp")
    @get:JsonProperty("citation_issue_timestamp")
    var citationIssueTimestamp: String? = null,
    @field:JsonProperty("citation_start_timestamp")
    @get:JsonProperty("citation_start_timestamp")
    var citationStartTimestamp: String? = null,
    @field:JsonProperty("reissue")
    @get:JsonProperty("reissue")
    var isReissue: Boolean = false,
    @field:JsonProperty("time_limit_enforcement")
    @get:JsonProperty("time_limit_enforcement")
    var isTimeLimitEnforcement: Boolean = false,
    @field:JsonProperty("time_limit_enforcement_id")
    @get:JsonProperty("time_limit_enforcement_id")
    var timeLimitEnforcementId: String? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var mLatitude: Double? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var mLongitiude: Double? = null,
    @field:JsonProperty("print_query")
    @get:JsonProperty("print_query")
    var printQuery: String? = null,
    @field:JsonProperty("category")
    @get:JsonProperty("category")
    var category: String? = null
) : Parcelable
