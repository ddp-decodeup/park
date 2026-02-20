package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class FullTimeRequest(
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetailsFulltime: OfficerDetailsFulltime? = null,
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetailsFulltime: LocationDetailsFulltime? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var detailsFulltime: DetailsFulltime? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsFulltime(
    @field:JsonProperty("date")
    @get:JsonProperty("date")
    var date: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,
    @field:JsonProperty("assigned_area")
    @get:JsonProperty("assigned_area")
    var assignedArea: String? = null,
    @field:JsonProperty("second_10_min_break")
    @get:JsonProperty("second_10_min_break")
    var second10MinBreak: String? = null,
    @field:JsonProperty("special_enforcement_request")
    @get:JsonProperty("special_enforcement_request")
    var specialEnforcementRequest: String? = null,
    @field:JsonProperty("officer")
    @get:JsonProperty("officer")
    var officer: String? = null,
    @field:JsonProperty("ossi_device_no")
    @get:JsonProperty("ossi_device_no")
    var ossiDeviceNo: String? = null,
    @field:JsonProperty("lunch_taken")
    @get:JsonProperty("lunch_taken")
    var lunchTaken: String? = null,
    @field:JsonProperty("first_10_min_break")
    @get:JsonProperty("first_10_min_break")
    var first10MinBreak: String? = null,
    @field:JsonProperty("unit_no")
    @get:JsonProperty("unit_no")
    var unitNo: String? = null,
    @field:JsonProperty("citations_issued")
    @get:JsonProperty("citations_issued")
    var citationsIssued: String? = null,
    @field:JsonProperty("handheld_unit_no")
    @get:JsonProperty("handheld_unit_no")
    var handheldUnitNo: String? = null,
    @field:JsonProperty("duty_hours")
    @get:JsonProperty("duty_hours")
    var dutyHours: String? = null,
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
data class LocationDetailsFulltime(
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
data class OfficerDetailsFulltime(
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
