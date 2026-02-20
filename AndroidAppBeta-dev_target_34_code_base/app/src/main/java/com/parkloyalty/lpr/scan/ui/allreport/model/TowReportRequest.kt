package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TowReportRequest(
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetailsTowReport? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetailsTowReport? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: DetailsTowReport? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsTowReport(
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
data class DetailsTowReport(
    @field:JsonProperty("vehicle_color")
    @get:JsonProperty("vehicle_color")
    var vehicleColor: String? = null,
    @field:JsonProperty("plate_photo")
    @get:JsonProperty("plate_photo")
    var platePhoto: String? = null,
    @field:JsonProperty("trailer_photo_two")
    @get:JsonProperty("trailer_photo_two")
    var trailerPhotoTwo: String? = null,
    @field:JsonProperty("driver_side_photo")
    @get:JsonProperty("driver_side_photo")
    var driverSidePhoto: String? = null,
    @field:JsonProperty("trailer_attached")
    @get:JsonProperty("trailer_attached")
    var trailerAttached: Boolean? = null,
    @field:JsonProperty("vehicle_model")
    @get:JsonProperty("vehicle_model")
    var vehicleModel: String? = null,
    @field:JsonProperty("rear_side_comments")
    @get:JsonProperty("rear_side_comments")
    var rearSideComments: String? = null,
    @field:JsonProperty("trailer_photo_three")
    @get:JsonProperty("trailer_photo_three")
    var trailerPhotoThree: String? = null,
    @field:JsonProperty("driver_side_comments")
    @get:JsonProperty("driver_side_comments")
    var driverSideComments: String? = null,
    @field:JsonProperty("passenger_side_photo")
    @get:JsonProperty("passenger_side_photo")
    var passengerSidePhoto: String? = null,
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,
    @field:JsonProperty("file_number")
    @get:JsonProperty("file_number")
    var fileNumber: String? = null,
    @field:JsonProperty("general_comments")
    @get:JsonProperty("general_comments")
    var generalComments: String? = null,
    @field:JsonProperty("tow_reason")
    @get:JsonProperty("tow_reason")
    var towReason: String? = null,
    @field:JsonProperty("rear_photo")
    @get:JsonProperty("rear_photo")
    var rearPhoto: String? = null,
    @field:JsonProperty("vehicle_vin")
    @get:JsonProperty("vehicle_vin")
    var vehicleVin: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("front_photo")
    @get:JsonProperty("front_photo")
    var frontPhoto: String? = null,
    @field:JsonProperty("curb_photo")
    @get:JsonProperty("curb_photo")
    var curbPhoto: String? = null,
    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,
    @field:JsonProperty("officer_phone_number")
    @get:JsonProperty("officer_phone_number")
    var officerPhoneNumber: String? = null,
    @field:JsonProperty("interior_photo_one")
    @get:JsonProperty("interior_photo_one")
    var interiorPhotoOne: String? = null,
    @field:JsonProperty("request_to")
    @get:JsonProperty("request_to")
    var requestTo: String? = null,
    @field:JsonProperty("driver_side_tire_air_valve")
    @get:JsonProperty("driver_side_tire_air_valve")
    var driverSideTireAirValve: String? = null,
    @field:JsonProperty("vehicle_make")
    @get:JsonProperty("vehicle_make")
    var vehicleMake: String? = null,
    @field:JsonProperty("tow_notice_date")
    @get:JsonProperty("tow_notice_date")
    var towNoticeDate: String? = null,
    @field:JsonProperty("visible_interior_items")
    @get:JsonProperty("visible_interior_items")
    var visibleInteriorItems: String? = null,
    @field:JsonProperty("interior_photo_two")
    @get:JsonProperty("interior_photo_two")
    var interiorPhotoTwo: String? = null,
    @field:JsonProperty("vehicle_license_plate")
    @get:JsonProperty("vehicle_license_plate")
    var vehicleLicensePlate: String? = null,
    @field:JsonProperty("passenger_side_comments")
    @get:JsonProperty("passenger_side_comments")
    var passengerSideComments: String? = null,
    @field:JsonProperty("front_side_comments")
    @get:JsonProperty("front_side_comments")
    var frontSideComments: String? = null,
    @field:JsonProperty("trailer_comments")
    @get:JsonProperty("trailer_comments")
    var trailerComments: String? = null,
    @field:JsonProperty("garage_clearance")
    @get:JsonProperty("garage_clearance")
    var garageClearance: String? = null,
    @field:JsonProperty("ro_address")
    @get:JsonProperty("ro_address")
    var roAddress: String? = null,
    @field:JsonProperty("vehicle_within_lot")
    @get:JsonProperty("vehicle_within_lot")
    var vehicleWithinTheLot: String? = null,
    @field:JsonProperty("trailer_photo_one")
    @get:JsonProperty("trailer_photo_one")
    var trailerPhotoOne: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("towing_officer")
    @get:JsonProperty("towing_officer")
    var towingOfficer: String? = null,
    @field:JsonProperty("vehicle_condition")
    @get:JsonProperty("vehicle_condition")
    var vehicleCondition: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsTowReport(
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
