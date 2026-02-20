package com.parkloyalty.lpr.scan.network.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ScannedImageUploadResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: ArrayList<Data> = arrayListOf(),
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null,
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Data(
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null,
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: Response? = Response(),
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Response(
    @field:JsonProperty("links")
    @get:JsonProperty("links")
    var links: ArrayList<String> = arrayListOf()
) : Parcelable
