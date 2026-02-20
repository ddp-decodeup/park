package com.parkloyalty.lpr.scan.ui.officerdailysummary.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ShiftDetails(
    @field:JsonProperty("lunch_timestamp")
    @get:JsonProperty("lunch_timestamp")
    var lunchTimestamp: String? = null,
    @field:JsonProperty("logout_timestamp")
    @get:JsonProperty("logout_timestamp")
    var logoutTimestamp: String? = null,
    @field:JsonProperty("break1_timestamp")
    @get:JsonProperty("break1_timestamp")
    var break1Timestamp: String? = null,
    @field:JsonProperty("break2_timestamp")
    @get:JsonProperty("break2_timestamp")
    var break2Timestamp: String? = null,
    @field:JsonProperty("login_timestamp")
    @get:JsonProperty("login_timestamp")
    var loginTimestamp: String? = null
) : Parcelable
