package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MetadataItem(
    @field:JsonProperty("url")
    @get:JsonProperty("url")
    var url: String? = null,

    @field:JsonProperty("image_spec")
    @get:JsonProperty("image_spec")
    var imageSpec: String? = null
) : Parcelable
