package com.parkloyalty.lpr.scan.ui.reprint.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponseFacsimileImages(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<DataItem>? = null,
    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var isSuccess: Boolean = false,
    @field:JsonProperty("length")
    @get:JsonProperty("length")
    var mLength: Int = 0
) : Parcelable
