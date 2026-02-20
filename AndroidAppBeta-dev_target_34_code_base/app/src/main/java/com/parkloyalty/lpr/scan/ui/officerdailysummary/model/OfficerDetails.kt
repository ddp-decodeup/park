package com.parkloyalty.lpr.scan.ui.officerdailysummary.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetails(
    @field:JsonProperty("device_name")
    @get:JsonProperty("device_name")
    var deviceName: String? = null,
    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var squad: String? = null,
    @field:JsonProperty("printer")
    @get:JsonProperty("printer")
    var printer: String? = null,
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,
    @field:JsonProperty("supervisor")
    @get:JsonProperty("supervisor")
    var supervisor: String? = null,
    @field:JsonProperty("username")
    @get:JsonProperty("username")
    var username: String? = null,
    @field:JsonProperty("radio")
    @get:JsonProperty("radio")
    var radio: String? = null
) : Parcelable
