package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UpdatePackage(
    @field:JsonProperty("officer_beat")
    @get:JsonProperty("officer_beat")
    var officerBeat: String? = null,
    @field:JsonProperty("officer_radio")
    @get:JsonProperty("officer_radio")
    var officerRadio: String? = null,
    @field:JsonProperty("officer_shift")
    @get:JsonProperty("officer_shift")
    var officerShift: String? = null,
    @field:JsonProperty("officer_supervisor")
    @get:JsonProperty("officer_supervisor")
    var officerSupervisor: String? = null,
    @field:JsonProperty("officer_supervisor_badge_id")
    @get:JsonProperty("officer_supervisor_badge_id")
    var mOfficerSupervisorBadgeId: Int = 0,
    @field:JsonProperty("officer_zone")
    @get:JsonProperty("officer_zone")
    var officerZone: String? = null,
    @field:JsonProperty("officer_agency")
    @get:JsonProperty("officer_agency")
    var mofficerAgency: String? = null,
    @field:JsonProperty("officer_device_id")
    @get:JsonProperty("officer_device_id")
    var mofficerDeviceId: OfficerDeviceIdObject? = null,
    @field:JsonProperty("city_zone")
    @get:JsonProperty("city_zone")
    var mCityZone: String? = null,
    @field:JsonProperty("officer_equipment")
    @get:JsonProperty("officer_equipment")
    var mEquipment: String? = null,
    @field:JsonProperty("officer_squad")
    @get:JsonProperty("officer_squad")
    var mOfficerSquad: String? = null,
    @field:JsonProperty("signature")
    @get:JsonProperty("signature")
    var mSignature: String? = null,
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var mLot: String? = null
) : Parcelable
