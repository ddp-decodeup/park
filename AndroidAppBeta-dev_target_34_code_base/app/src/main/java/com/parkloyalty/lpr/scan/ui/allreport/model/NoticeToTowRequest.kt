package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class NoticeToTowRequest(
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetailsNoticeToTow? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetailsNoticeToTow? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: DetailsNoticeToTow? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsNoticeToTow(
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

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsNoticeToTow(
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
    var longitude: Double? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsNoticeToTow(
    @field:JsonProperty("driver_side_tire_air_valve")
    @get:JsonProperty("driver_side_tire_air_valve")
    var driverSideTireAirvalve: String? = null,
    @field:JsonProperty("vehicle_make")
    @get:JsonProperty("vehicle_make")
    var vehicleMake: String? = null,
    @field:JsonProperty("vehicle_color")
    @get:JsonProperty("vehicle_color")
    var vehicleColor: String? = null,
    @field:JsonProperty("vehicle_license_plate")
    @get:JsonProperty("vehicle_license_plate")
    var vehicleLicensePlate: String? = null,
    @field:JsonProperty("vehicle_model")
    @get:JsonProperty("vehicle_model")
    var vehicleModel: String? = null,
    @field:JsonProperty("scheduled_tow_date")
    @get:JsonProperty("scheduled_tow_date")
    var scheduledTowDate: String? = null,
    @field:JsonProperty("first_mark_timestamp")
    @get:JsonProperty("first_mark_timestamp")
    var firstMarkTimestamp: String? = null,
    @field:JsonProperty("photos")
    @get:JsonProperty("photos")
    var photos: List<String?>? = null,
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,
    @field:JsonProperty("citation_issue_timestamp")
    @get:JsonProperty("citation_issue_timestamp")
    var citationIssueTimestamp: String? = null,
    @field:JsonProperty("report_number")
    @get:JsonProperty("report_number")
    var reportNumber: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("vehicle_vin")
    @get:JsonProperty("vehicle_vin")
    var vehicleVin: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null
) : Parcelable
