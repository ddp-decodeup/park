package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class HearingTimeResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<DataItemHearingTime?>? = null,

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
data class ResponseItemHearingTime(
    @field:JsonProperty("hearing_time")
    @get:JsonProperty("hearing_time")
    var hearingTime: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MetadataHearingTime(
    @field:JsonProperty("total_shards")
    @get:JsonProperty("total_shards")
    var totalShards: Int? = null,

    @field:JsonProperty("length")
    @get:JsonProperty("length")
    var length: Int? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataItemHearingTime(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: MetadataHearingTime? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: List<ResponseItemHearingTime?>? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable
