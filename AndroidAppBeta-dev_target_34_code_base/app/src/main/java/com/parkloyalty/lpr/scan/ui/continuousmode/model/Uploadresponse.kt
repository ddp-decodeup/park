package com.parkloyalty.lpr.scan.ui.continuousmode.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Uploadresponse(
    @field:JsonProperty("links")
    @get:JsonProperty("links")
    var uploadlinks: List<String>? = null
) : Parcelable
