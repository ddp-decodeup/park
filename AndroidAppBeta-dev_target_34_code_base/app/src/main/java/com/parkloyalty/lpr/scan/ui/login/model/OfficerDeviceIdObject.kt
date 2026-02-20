package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDeviceIdObject(
    @field:JsonProperty("device_friendly_name")
    @get:JsonProperty("device_friendly_name")
    var mDeviceFriendlyName: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var mDeviceId: String? = null,
    @field:JsonProperty("android_id")
    @get:JsonProperty("android_id")
    var mAndroidId: String? = null
) : Parcelable
