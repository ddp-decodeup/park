package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BikeInspectionsRequest(
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetailsBikeInspections: OfficerDetailsBikeInspections? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetailsBikeInspections: LocationDetailsBikeInspections? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var detailsBikeInspections: DetailsBikeInspections? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsBikeInspections(
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
data class OfficerDetailsBikeInspections(
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
data class DetailsBikeInspections(
    @field:JsonProperty("battery_free_of_debris")
    @get:JsonProperty("battery_free_of_debris")
    var batteryFreeOfDebris: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,
    @field:JsonProperty("officer")
    @get:JsonProperty("officer")
    var officer: String? = null,
    @field:JsonProperty("assigned_bike")
    @get:JsonProperty("assigned_bike")
    var assignedBike: String? = null,
    @field:JsonProperty("back")
    @get:JsonProperty("back")
    var back: String? = null,
    @field:JsonProperty("lights_and_reflectors")
    @get:JsonProperty("lights_and_reflectors")
    var lightsAndReflectors: String? = null,
    @field:JsonProperty("chain_crank")
    @get:JsonProperty("chain_crank")
    var chainCrank: String? = null,
    @field:JsonProperty("right_side")
    @get:JsonProperty("right_side")
    var rightSide: String? = null,
    @field:JsonProperty("breaks_rotors")
    @get:JsonProperty("breaks_rotors")
    var breaksRotors: String? = null,
    @field:JsonProperty("tire_pressure")
    @get:JsonProperty("tire_pressure")
    var tirePressure: String? = null,
    @field:JsonProperty("left_side")
    @get:JsonProperty("left_side")
    var leftSide: String? = null,
    @field:JsonProperty("flat_pack")
    @get:JsonProperty("flat_pack")
    var flatPack: String? = null,
    @field:JsonProperty("font")
    @get:JsonProperty("font")
    var font: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,
    @field:JsonProperty("first_aid_kit")
    @get:JsonProperty("first_aid_kit")
    var firstAidKit: String? = null,
    @field:JsonProperty("helmet_visual_inspection")
    @get:JsonProperty("helmet_visual_inspection")
    var helmetVisualInspection: String? = null,
    @field:JsonProperty("bike_glasses")
    @get:JsonProperty("bike_glasses")
    var bikeGlasses: String? = null,
    @field:JsonProperty("battery_charge")
    @get:JsonProperty("battery_charge")
    var batterCharge: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("glove_visual_inspection")
    @get:JsonProperty("glove_visual_inspection")
    var gloveVisualInspection: String? = null
) : Parcelable
