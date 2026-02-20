package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class HandHeldMalFunctionsRequest(
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var detailsHandHeldMalfunctions: DetailsHandHeldMalfunctions? = null,
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetailsHandHeldMalfunctions: LocationDetailsHandHeldMalfunctions? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetailsHandHeldMalfunctions: OfficerDetailsHandHeldMalfunctions? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsHandHeldMalfunctions(
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
data class OfficerDetailsHandHeldMalfunctions(
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
data class DetailsHandHeldMalfunctions(
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,
    @field:JsonProperty("overheating")
    @get:JsonProperty("overheating")
    var overheating: Boolean? = null,
    @field:JsonProperty("describe_hand_held_malfunction_in_detail")
    @get:JsonProperty("describe_hand_held_malfunction_in_detail")
    var describeHandHeldMalfunctionInDetail: String? = null,
    @field:JsonProperty("hand_held_unit_no")
    @get:JsonProperty("hand_held_unit_no")
    var handHeldUnitNo: String? = null,
    @field:JsonProperty("internet_connectivity")
    @get:JsonProperty("internet_connectivity")
    var internetConnectivity: Boolean? = null,
    @field:JsonProperty("tried_to_restart_hand_held")
    @get:JsonProperty("tried_to_restart_hand_held")
    var triedToRestartHandHeld: Boolean? = null,
    @field:JsonProperty("printing_correctly")
    @get:JsonProperty("printing_correctly")
    var printingCorrectly: Boolean? = null,
    @field:JsonProperty("battery_hold_charge")
    @get:JsonProperty("battery_hold_charge")
    var batteryHoldCharge: Boolean? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null
) : Parcelable
