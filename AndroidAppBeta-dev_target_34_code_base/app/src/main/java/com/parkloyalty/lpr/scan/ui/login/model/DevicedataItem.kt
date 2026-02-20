package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DevicedataItem(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: DeviceMetadata? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: List<DeviceResponseItem>? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var isStatus: Boolean = false
) : Parcelable
