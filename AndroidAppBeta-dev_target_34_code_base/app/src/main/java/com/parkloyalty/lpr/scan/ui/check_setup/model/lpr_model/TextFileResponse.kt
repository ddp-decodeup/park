package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TextFileResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var dataText: List<DataItemText?>? = null,
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataItemText(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: @kotlinx.parcelize.RawValue Any? = null,
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: ResponseText? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LinksItemText(
    @field:JsonProperty("filename")
    @get:JsonProperty("filename")
    var filename: String? = null,
    @field:JsonProperty("link")
    @get:JsonProperty("link")
    var link: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponseText(
    @field:JsonProperty("links")
    @get:JsonProperty("links")
    var links: List<LinksItemText?>? = null
) : Parcelable
