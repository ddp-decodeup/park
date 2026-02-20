package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PushEventRequest(
    @field:JsonProperty("event_metadata")
    @get:JsonProperty("event_metadata")
    var eventMetadata: PushEventMetadata? = null,
    @field:JsonProperty("event_type")
    @get:JsonProperty("event_type")
    var eventType: String? = null,
    @field:JsonProperty("event_lat")
    @get:JsonProperty("event_lat")
    var mEventLat: Double? = null,
    @field:JsonProperty("event_lng")
    @get:JsonProperty("event_lng")
    var mEventLng: Double? = null
) : Parcelable
