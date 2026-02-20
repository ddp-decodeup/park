package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ActivityUpdateRequest(
    @field:JsonProperty("activity_type")
    @get:JsonProperty("activity_type")
    var activityType: String? = null,
    @field:JsonProperty("activity_id")
    @get:JsonProperty("activity_id")
    var activity_id: String? = null,
    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null,
    @field:JsonProperty("initiator_id")
    @get:JsonProperty("initiator_id")
    var initiatorId: String? = null,
    @field:JsonProperty("initiator_role")
    @get:JsonProperty("initiator_role")
    var initiatorRole: String? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,
    @field:JsonProperty("log_type")
    @get:JsonProperty("log_type")
    var logType: String? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double? = null,
    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,
    @field:JsonProperty("activity_name")
    @get:JsonProperty("activity_name")
    var activityName: String? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var mShift: String? = null,
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var mBlock: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var mStreet: String? = null,
    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var mSide: String? = null,
    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var mSquad: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var mDeviceId: String? = null,
    @field:JsonProperty("image_1")
    @get:JsonProperty("image_1")
    var image_1: String? = null,
    @field:JsonProperty("image_2")
    @get:JsonProperty("image_2")
    var image_2: String? = null,
    @field:JsonProperty("image_3")
    @get:JsonProperty("image_3")
    var image_3: String? = null,
    @field:JsonProperty("is_display")
    @get:JsonProperty("is_display")
    var isDisplay: Boolean? = null,
    @field:JsonProperty("android_id")
    @get:JsonProperty("android_id")
    var androidId: String? = ""
) : Parcelable
