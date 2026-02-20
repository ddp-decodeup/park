package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Metadata(
    @field:JsonProperty("total_shards")
    @get:JsonProperty("total_shards")
    var totalShards: Int = 0,

    @field:JsonProperty("length")
    @get:JsonProperty("length")
    var length: Int = 0,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null
) : Parcelable
