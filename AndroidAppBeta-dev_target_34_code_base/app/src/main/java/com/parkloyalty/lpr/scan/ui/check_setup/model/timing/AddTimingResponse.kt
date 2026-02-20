package com.parkloyalty.lpr.scan.ui.check_setup.model.timing

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class AddTimingResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: AddTimingData? = null,

    @field:JsonProperty("differnece")
    @get:JsonProperty("differnece")
    var differnece: Long? = null,

    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var success: Boolean? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: String? = null
) : Parcelable
