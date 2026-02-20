package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ActivityLayoutData(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: ActivityLayouMetadata? = null,
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: @kotlinx.parcelize.RawValue List<CitationLayoutData>? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable
