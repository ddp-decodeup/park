package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class NFLRequest(
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetailsNfl? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetailsNfl? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: DetailsNfl? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsNfl(
    @field:JsonProperty("empty_spaces")
    @get:JsonProperty("empty_spaces")
    var emptySpaces: Int? = null,
    @field:JsonProperty("photo1")
    @get:JsonProperty("photo1")
    var photo1: String? = null,
    @field:JsonProperty("comments")
    @get:JsonProperty("comments")
    var comments: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("report_number")
    @get:JsonProperty("report_number")
    var reportNumber: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("photo2")
    @get:JsonProperty("photo2")
    var photo2: String? = null,
    @field:JsonProperty("cars_counted")
    @get:JsonProperty("cars_counted")
    var carsCounted: Int? = null,
    @field:JsonProperty("number_of_violating_vehicles")
    @get:JsonProperty("number_of_violating_vehicles")
    var numberOfViolatingVehicles: Int? = null,
    @field:JsonProperty("photo3")
    @get:JsonProperty("photo3")
    var photo3: String? = null,
    @field:JsonProperty("violation_rate")
    @get:JsonProperty("violation_rate")
    var violationRate: String? = null,
    @field:JsonProperty("event_name")
    @get:JsonProperty("event_name")
    var eventName: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsNfl(
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsNfl(
    @field:JsonProperty("shift_id")
    @get:JsonProperty("shift_id")
    var shiftId: String? = null,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,
    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null
) : Parcelable
