package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SpecialAssignementRequest(
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetailsSpecial? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetailsSpecial? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: DetailsSpecial? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsSpecial(
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsSpecial(
    @field:JsonProperty("shift_id")
    @get:JsonProperty("shift_id")
    var shiftId: String? = null,
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,
    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsSpecial(
    @field:JsonProperty("shift_summary_comments")
    @get:JsonProperty("shift_summary_comments")
    var shiftSummaryComments: String? = null,
    @field:JsonProperty("citations_issued")
    @get:JsonProperty("citations_issued")
    var citationsIssued: String? = null,
    @field:JsonProperty("violation_descriptions")
    @get:JsonProperty("violation_descriptions")
    var violationDescriptions: List<String?>? = null,
    @field:JsonProperty("warnings_issued")
    @get:JsonProperty("warnings_issued")
    var warningsIssued: String? = null,
    @field:JsonProperty("comments")
    @get:JsonProperty("comments")
    var comments: String? = null,
    @field:JsonProperty("lunch_taken")
    @get:JsonProperty("lunch_taken")
    var lunchTaken: String? = null,
    @field:JsonProperty("first_time_marking")
    @get:JsonProperty("first_time_marking")
    var timeMarkedAt: String? = null,
    @field:JsonProperty("second_time_marking")
    @get:JsonProperty("second_time_marking")
    var timeMarkedAt2: String? = null,
    @field:JsonProperty("third_time_marking")
    @get:JsonProperty("third_time_marking")
    var timeMarkedAt3: String? = null,
    @field:JsonProperty("fourth_time_marking")
    @get:JsonProperty("fourth_time_marking")
    var timeMarkedAt4: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("vehicles_marked")
    @get:JsonProperty("vehicles_marked")
    var vehiclesMarked: String? = null
) : Parcelable
