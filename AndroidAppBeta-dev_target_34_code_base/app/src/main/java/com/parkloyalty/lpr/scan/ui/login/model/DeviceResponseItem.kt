package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DeviceResponseItem(
    @field:JsonProperty("friendly_name")
    @get:JsonProperty("friendly_name")
    var friendlyName: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,
    @field:JsonProperty("android_id")
    @get:JsonProperty("android_id")
    var androidId: String? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null
) : Parcelable
