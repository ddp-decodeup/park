package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DatasetData(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: DatasetMetadata? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: List<DatasetResponse>? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable