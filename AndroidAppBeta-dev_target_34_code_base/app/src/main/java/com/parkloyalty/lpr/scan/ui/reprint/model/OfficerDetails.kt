package com.parkloyalty.lpr.scan.ui.reprint.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetails(
    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var squad: String? = null,

    @field:JsonProperty("agency")
    @get:JsonProperty("agency")
    var agency: String? = null,

    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,

    @field:JsonProperty("signature")
    @get:JsonProperty("signature")
    var signature: String? = null,

    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,

    @field:JsonProperty("device_friendly_name")
    @get:JsonProperty("device_friendly_name")
    var deviceFriendlyName: String? = null,

    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,

    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,

    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,

    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null,

    @field:JsonProperty("peo_name")
    @get:JsonProperty("peo_name")
    var peoName: String? = null
) : Parcelable
