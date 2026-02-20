package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class WelcomeUser(
    @field:JsonProperty("enable")
    @get:JsonProperty("enable")
    var enable: Boolean? = null,
    @field:JsonProperty("officer_badge_id")
    @get:JsonProperty("officer_badge_id")
    var officerBadgeId: String? = null,
    @field:JsonProperty("officer_first_name")
    @get:JsonProperty("officer_first_name")
    var officerFirstName: String? = null,
    @field:JsonProperty("officer_last_name")
    @get:JsonProperty("officer_last_name")
    var officerLastName: String? = null,
    @field:JsonProperty("officer_middle_name")
    @get:JsonProperty("officer_middle_name")
    var officerMiddleName: String? = null,
    @field:JsonProperty("officer_lookup_code")
    @get:JsonProperty("officer_lookup_code")
    var officer_lookup_code: String? = null,
    @field:JsonProperty("officer_squad")
    @get:JsonProperty("officer_squad")
    var officerSquad: String? = null,
    @field:JsonProperty("officer_superviser_id")
    @get:JsonProperty("officer_superviser_id")
    var officerSuperviserId: String? = null,
    @field:JsonProperty("officer_user_name")
    @get:JsonProperty("officer_user_name")
    var officerUserName: String? = null,
    @field:JsonProperty("radio")
    @get:JsonProperty("radio")
    var radio: String? = null,
    @field:JsonProperty("role")
    @get:JsonProperty("role")
    var role: String? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,
    @field:JsonProperty("officer_shift")
    @get:JsonProperty("officer_shift")
    var officerShift: String? = null,
    @field:JsonProperty("officer_supervisor")
    @get:JsonProperty("officer_supervisor")
    var officerSupervisor: String? = null,
    @field:JsonProperty("officer_radio")
    @get:JsonProperty("officer_radio")
    var officerRadio: String? = null,
    @field:JsonProperty("officer_beat")
    @get:JsonProperty("officer_beat")
    var officerBeat: String? = null,
    @field:JsonProperty("officer_zone")
    @get:JsonProperty("officer_zone")
    var officerZone: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,
    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null,
    @field:JsonProperty("officer_agency")
    @get:JsonProperty("officer_agency")
    var mOfficerAgency: String? = null,
    @field:JsonProperty("officer_device_id")
    @get:JsonProperty("officer_device_id")
    var mOfficerDeviceId: OfficerDeviceIdObject? = null,
    @field:JsonProperty("approved_by")
    @get:JsonProperty("approved_by")
    var mApprovedBy: ApprovedBy? = null,
    @field:JsonProperty("city_zone")
    @get:JsonProperty("city_zone")
    var mCityZone: String? = null,
    @field:JsonProperty("officer_equipment")
    @get:JsonProperty("officer_equipment")
    var mEquipment: String? = null,
    @field:JsonProperty("supervisor_badge_id")
    @get:JsonProperty("supervisor_badge_id")
    var supervisorBadgeId: String? = null,
    @field:JsonProperty("signature")
    @get:JsonProperty("signature")
    var mSignature: String? = null,
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var mLot: String? = null
) : Parcelable
