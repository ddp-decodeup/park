package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TimingData(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: String? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: ResponseTiming? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable
