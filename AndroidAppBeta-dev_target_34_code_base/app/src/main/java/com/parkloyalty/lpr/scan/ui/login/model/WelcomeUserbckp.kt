package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class WelcomeUserbckp(
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
    var officerShift: OfficerShift? = null,

    @field:JsonProperty("officer_supervisor")
    @get:JsonProperty("officer_supervisor")
    var officerSupervisor: OfficerSupervisor? = null,

    @field:JsonProperty("officer_radio")
    @get:JsonProperty("officer_radio")
    var officerRadio: OfficerRadio? = null,

    @field:JsonProperty("officer_beat")
    @get:JsonProperty("officer_beat")
    var officerBeat: OfficerBeat? = null,

    @field:JsonProperty("officer_zone")
    @get:JsonProperty("officer_zone")
    var officerZone: OfficerZone? = null,

    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
