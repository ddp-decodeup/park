package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UserEventRequest(
    @field:JsonProperty("date_time")
    @get:JsonProperty("date_time")
    var dateTime: String? = null,
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: String? = null,
    @field:JsonProperty("event")
    @get:JsonProperty("event")
    var event: String? = null
) : Parcelable
