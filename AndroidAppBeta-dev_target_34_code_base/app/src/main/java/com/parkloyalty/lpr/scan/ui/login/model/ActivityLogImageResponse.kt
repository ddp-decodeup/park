package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ActivityLogImageResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<DataItemActivityImage?>? = null,

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
data class DataItemActivityImage(
    @field:JsonProperty("last_update_timestamp")
    @get:JsonProperty("last_update_timestamp")
    var lastUpdateTimestamp: String? = null,

    @field:JsonProperty("image_1")
    @get:JsonProperty("image_1")
    var image1: String? = null,

    @field:JsonProperty("image_3")
    @get:JsonProperty("image_3")
    var image3: String? = null,

    @field:JsonProperty("image_2")
    @get:JsonProperty("image_2")
    var image2: String? = null,

    @field:JsonProperty("updated")
    @get:JsonProperty("updated")
    var updated: Boolean? = null
) : Parcelable
