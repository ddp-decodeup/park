package com.parkloyalty.lpr.scan.ui.brokenmeter.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsBroken(
    @field:JsonProperty("reason")
    @get:JsonProperty("reason")
    var reason: String? = null,
    @field:JsonProperty("meter_no")
    @get:JsonProperty("meter_no")
    var meterNo: String? = null,
    @field:JsonProperty("remarks")
    @get:JsonProperty("remarks")
    var remarks: String? = null
) : Parcelable
