package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetails(
    @field:JsonProperty("agency")
    @get:JsonProperty("agency")
    var agency: String? = null,

    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,

    @field:JsonProperty("officer_lookup_code")
    @get:JsonProperty("officer_lookup_code")
    var officer_lookup_code: String? = null,

    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,

    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officer_name: String? = null,

    @field:JsonProperty("peo_fname")
    @get:JsonProperty("peo_fname")
    var peo_fname: String? = null,

    @field:JsonProperty("peo_lname")
    @get:JsonProperty("peo_lname")
    var peo_lname: String? = null,

    @field:JsonProperty("peo_name")
    @get:JsonProperty("peo_name")
    var peo_name: String? = null,

    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var squad: String? = null,

    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,

    @field:JsonProperty("signature")
    @get:JsonProperty("signature")
    var signature: String? = null,

    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var mShift: String? = null,

    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var mDdeviceId: String? = null,

    @field:JsonProperty("device_friendly_name")
    @get:JsonProperty("device_friendly_name")
    var mDdeviceFriendlyName: String? = null
) : Parcelable
