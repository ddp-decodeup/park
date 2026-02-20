package com.parkloyalty.lpr.scan.ui.continuousmode.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UploaddataItem(
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var uploadresponse: Uploadresponse? = null,
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var uploadmetadata: @RawValue Any? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var isStatus: Boolean = false
) : Parcelable
