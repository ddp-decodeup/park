package com.parkloyalty.lpr.scan.ui.ticket.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UpdateMarkResponse(
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,

    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var success: Boolean? = null
) : Parcelable
