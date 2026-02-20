package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocUpdateRequest(
    @field:JsonProperty("activity_type")
    @get:JsonProperty("activity_type")
    var activityType: String? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,
    @field:JsonProperty("location_update_type")
    @get:JsonProperty("location_update_type")
    var locationUpdateType: String? = null,
    @field:JsonProperty("log_type")
    @get:JsonProperty("log_type")
    var logType: String? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double? = null,
    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var mShift: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var mDeviceId: String? = null
) : Parcelable
