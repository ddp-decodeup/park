package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ScofflawResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var dataScofflaw: List<ScofflawData>? = null,

    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: String? = null
) : Parcelable
