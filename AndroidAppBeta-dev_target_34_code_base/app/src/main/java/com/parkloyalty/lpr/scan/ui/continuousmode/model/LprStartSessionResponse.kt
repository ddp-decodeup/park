package com.parkloyalty.lpr.scan.ui.continuousmode.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LprStartSessionResponse(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: StartSessionMetadata? = null,
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: StartSessionResponse? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var isStatus: Boolean = false
) : Parcelable
