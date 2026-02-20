package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MeterRequest(
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: MeterLocation? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null
) : Parcelable
