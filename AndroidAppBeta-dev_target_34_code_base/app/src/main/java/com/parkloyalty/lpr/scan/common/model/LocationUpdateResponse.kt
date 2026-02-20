package com.parkloyalty.lpr.scan.common.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationUpdateResponse(
    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var success: Boolean? = null
) : Parcelable
