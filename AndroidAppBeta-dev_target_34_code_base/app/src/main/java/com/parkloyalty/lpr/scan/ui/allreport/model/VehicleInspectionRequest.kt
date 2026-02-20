package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleInspectionRequest(
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var detailsVehicleInspection: DetailsVehicleInspection? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetailsVehicleInspection: OfficerDetailsVehicleInspection? = null,
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetailsVehicleInspection: LocationDetailsVehicleInspection? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsVehicleInspection(
    @field:JsonProperty("visible_leaks")
    @get:JsonProperty("visible_leaks")
    var visibleLeaks: Boolean? = null,
    @field:JsonProperty("cones_six_per_vehicle")
    @get:JsonProperty("cones_six_per_vehicle")
    var conesSixPerVehicle: String? = null,
    @field:JsonProperty("interior_cleanliness")
    @get:JsonProperty("interior_cleanliness")
    var interiorCleanliness: String? = null,
    @field:JsonProperty("signature")
    @get:JsonProperty("signature")
    var signature: String? = null,
    @field:JsonProperty("officer")
    @get:JsonProperty("officer")
    var officer: String? = null,
    @field:JsonProperty("image_1")
    @get:JsonProperty("image_1")
    var image_1: String? = null,
    @field:JsonProperty("image_2")
    @get:JsonProperty("image_2")
    var image_2: String? = null,
    @field:JsonProperty("brakes")
    @get:JsonProperty("brakes")
    var brakes: String? = null,
    @field:JsonProperty("driver_side_photo")
    @get:JsonProperty("driver_side_photo")
    var driverSidePhoto: String? = null,
    @field:JsonProperty("light_bar")
    @get:JsonProperty("light_bar")
    var lightBar: String? = null,
    @field:JsonProperty("steering_wheel_operational")
    @get:JsonProperty("steering_wheel_operational")
    var steeringWheelOperational: String? = null,
    @field:JsonProperty("passenger_side_photo")
    @get:JsonProperty("passenger_side_photo")
    var passengerSidePhoto: String? = null,
    @field:JsonProperty("vehicle")
    @get:JsonProperty("vehicle")
    var vehicle: String? = null,
    @field:JsonProperty("windshield_wipers_operational")
    @get:JsonProperty("windshield_wipers_operational")
    var windshieldWipersOperational: String? = null,
    @field:JsonProperty("dashboard_indications")
    @get:JsonProperty("dashboard_indications")
    var dashboardIndications: String? = null,
    @field:JsonProperty("rear_photo")
    @get:JsonProperty("rear_photo")
    var rearPhoto: String? = null,
    @field:JsonProperty("first_aid_kit")
    @get:JsonProperty("first_aid_kit")
    var firstAidKit: Boolean? = null,
    @field:JsonProperty("starting_mileage")
    @get:JsonProperty("starting_mileage")
    var startingMileage: String? = null,
    @field:JsonProperty("front_photo")
    @get:JsonProperty("front_photo")
    var frontPhoto: String? = null,
    @field:JsonProperty("lpr_lens_free_of_debris")
    @get:JsonProperty("lpr_lens_free_of_debris")
    var lprLensFreeOfDebris: String? = null,
    @field:JsonProperty("seat_belt_operational")
    @get:JsonProperty("seat_belt_operational")
    var seatBeltOperational: String? = null,
    @field:JsonProperty("gas_level")
    @get:JsonProperty("gas_level")
    var gasLevel: String? = null,
    @field:JsonProperty("windshield_visibility")
    @get:JsonProperty("windshield_visibility")
    var windshieldVisibility: String? = null,
    @field:JsonProperty("brake_lights")
    @get:JsonProperty("brake_lights")
    var brakeLights: String? = null,
    @field:JsonProperty("turn_signals")
    @get:JsonProperty("turn_signals")
    var turnSignals: String? = null,
    @field:JsonProperty("comments")
    @get:JsonProperty("comments")
    var comments: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,
    @field:JsonProperty("headlights")
    @get:JsonProperty("headlights")
    var headlights: String? = null,
    @field:JsonProperty("vehicle_registration_and_insurance")
    @get:JsonProperty("vehicle_registration_and_insurance")
    var vehicleRegistrationAndInsurance: Boolean? = null,
    @field:JsonProperty("exterior_cleanliness")
    @get:JsonProperty("exterior_cleanliness")
    var exteriorCleanliness: String? = null,
    @field:JsonProperty("horn")
    @get:JsonProperty("horn")
    var horn: String? = null,
    @field:JsonProperty("tires_visual_inspection")
    @get:JsonProperty("tires_visual_inspection")
    var tiresVisualInspection: String? = null,
    @field:JsonProperty("side_and_rear_view_mirrors_operational")
    @get:JsonProperty("side_and_rear_view_mirrors_operational")
    var sideAndRearViewMirrorsOperational: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsVehicleInspection(
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
data class OfficerDetailsVehicleInspection(
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
