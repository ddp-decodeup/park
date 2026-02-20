package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class HourMarkedVehiclesRequest(
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetailsHourMarkedVehicle: OfficerDetailsHourMarkedVehicle? = null,
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetailsHourMarkedVehicle: LocationDetailsHourMarkedVehicle? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var detailsHourMarkedVehicle: DetailsHourMarkedVehicle? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsHourMarkedVehicle(
    @field:JsonProperty("comments")
    @get:JsonProperty("comments")
    var comments: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,
    @field:JsonProperty("red_station_copy_and_parking_warning")
    @get:JsonProperty("red_station_copy_and_parking_warning")
    var redStationCopyAndParkingWarning: String? = null,
    @field:JsonProperty("warning_citation")
    @get:JsonProperty("warning_citation")
    var warningCitation: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsHourMarkedVehicle(
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
data class LocationDetailsHourMarkedVehicle(
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
