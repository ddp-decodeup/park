package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class RadioResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    val data: List<DatasetData>? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    val status: Boolean? = null,

    @field:JsonProperty("message")
    @get:JsonProperty("message")
    val message: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DatasetData(
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    val status: Boolean? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    val response: List<RadioSt>? = null,

    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    val metadata: DatasetMetaData? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class RadioSt(
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    val id: String? = null,


//    @field:JsonProperty("radio_metadata")
//    @get:JsonProperty("radio_metadata")
//    @JsonDeserialize(using = RadioMetadataDeserializer::class)
//    val radioMetadata: RadioMetadata? = null,
//    val radioMetadata: Int? = null,   //  changed from class to Int

    @field:JsonProperty("radio_name")
    @get:JsonProperty("radio_name")
    val radioName: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DatasetMetaData(
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    val type: String? = null,

    @field:JsonProperty("total_shards")
    @get:JsonProperty("total_shards")
    val totalShards: Int? = null,

    @field:JsonProperty("length")
    @get:JsonProperty("length")
    val length: Int? = null
) : Parcelable

@Keep
@Parcelize
data class RadioMetadata(
    val name: String? = null,
    val screen_name: String? = null,
    val id: Int? = null
) : Parcelable
