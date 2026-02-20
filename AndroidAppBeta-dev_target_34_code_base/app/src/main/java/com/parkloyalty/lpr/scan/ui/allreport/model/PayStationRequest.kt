package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PayStationRequest(
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetailsPayStation? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetailsPayStation? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: DetailsPayStation? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsPayStation(
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
data class DetailsPayStation(
    @field:JsonProperty("machine_number3")
    @get:JsonProperty("machine_number3")
    var machineNumber3: Int? = null,
    @field:JsonProperty("machine_number1")
    @get:JsonProperty("machine_number1")
    var machineNumber1: Int? = null,
    @field:JsonProperty("machine_number2")
    @get:JsonProperty("machine_number2")
    var machineNumber2: Int? = null,
    @field:JsonProperty("description2")
    @get:JsonProperty("description2")
    var description2: String? = null,
    @field:JsonProperty("photo2")
    @get:JsonProperty("photo2")
    var photo2: String? = null,
    @field:JsonProperty("description3")
    @get:JsonProperty("description3")
    var description3: String? = null,
    @field:JsonProperty("photo3")
    @get:JsonProperty("photo3")
    var photo3: String? = null,
    @field:JsonProperty("description1")
    @get:JsonProperty("description1")
    var description1: String? = null,
    @field:JsonProperty("photo1")
    @get:JsonProperty("photo1")
    var photo1: String? = null,
    @field:JsonProperty("report_number")
    @get:JsonProperty("report_number")
    var reportNumber: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("comments3")
    @get:JsonProperty("comments3")
    var comments3: String? = null,
    @field:JsonProperty("comments2")
    @get:JsonProperty("comments2")
    var comments2: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("comments1")
    @get:JsonProperty("comments1")
    var comments1: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsPayStation(
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
