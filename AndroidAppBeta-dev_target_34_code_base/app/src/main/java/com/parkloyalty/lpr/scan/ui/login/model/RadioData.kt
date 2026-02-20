package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.Metadata
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class RadioData(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: Metadata? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: List<RadioSt>? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable
