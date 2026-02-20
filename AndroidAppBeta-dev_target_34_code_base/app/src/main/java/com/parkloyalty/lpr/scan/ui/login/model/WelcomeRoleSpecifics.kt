package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class WelcomeRoleSpecifics(
    @field:JsonProperty("enforcement_agency_id")
    @get:JsonProperty("enforcement_agency_id")
    var enforcementAgencyId: String? = null,
    @field:JsonProperty("enforcement_user_id")
    @get:JsonProperty("enforcement_user_id")
    var enforcementUserId: String? = null,
    @field:JsonProperty("officer_badge_id")
    @get:JsonProperty("officer_badge_id")
    var officerBadgeId: String? = null,
    @field:JsonProperty("officer_beat")
    @get:JsonProperty("officer_beat")
    var officerBeat: String? = null,
    @field:JsonProperty("officer_onboarding_timestamp")
    @get:JsonProperty("officer_onboarding_timestamp")
    var officerOnboardingTimestamp: String? = null,
    @field:JsonProperty("officer_squad")
    @get:JsonProperty("officer_squad")
    var officerSquad: String? = null,
    @field:JsonProperty("officer_superviser_id")
    @get:JsonProperty("officer_superviser_id")
    var officerSuperviserId: String? = null,
    @field:JsonProperty("radio")
    @get:JsonProperty("radio")
    var radio: String? = null,
    @field:JsonProperty("role_specific_form")
    @get:JsonProperty("role_specific_form")
    var roleSpecificForm: Boolean? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
