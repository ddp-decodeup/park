package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DeviceLicenseListResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var dataDeviceLicense: List<DataDeviceLicenseItem>? = null,
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = false
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataDeviceLicenseItem(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadataDeviceLicense: MetadataDeviceLicense? = null,
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var responseDeviceLicense: List<ResponseDeviceLicenseItem>? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponseDeviceLicenseItem(
    @field:JsonProperty("license")
    @get:JsonProperty("license")
    var license: String? = null,
    @field:JsonProperty("device_friendly_name")
    @get:JsonProperty("device_friendly_name")
    var deviceFriendlyName: String? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,
    @field:JsonProperty("android_id")
    @get:JsonProperty("android_id")
    var androidId: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MetadataDeviceLicense(
    @field:JsonProperty("total_shards")
    @get:JsonProperty("total_shards")
    var totalShards: Int? = null,
    @field:JsonProperty("length")
    @get:JsonProperty("length")
    var mLength: Int? = null,
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null
) : Parcelable
