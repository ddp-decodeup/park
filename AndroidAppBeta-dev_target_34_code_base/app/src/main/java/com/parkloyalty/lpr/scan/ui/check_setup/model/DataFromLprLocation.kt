package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataFromLprLocation(
    @field:JsonProperty("lat")
    @get:JsonProperty("lat")
    var lat: Double? = null,
    @field:JsonProperty("long")
    @get:JsonProperty("long")
    var long: Double? = null
) : Parcelable
