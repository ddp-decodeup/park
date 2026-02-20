package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PushEventMetadata(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: String? = null,
    @field:JsonProperty("description")
    @get:JsonProperty("description")
    var description: String? = null,
    @field:JsonProperty("event_finish_timestamp")
    @get:JsonProperty("event_finish_timestamp")
    var eventFinishTimestamp: String? = null,
    @field:JsonProperty("event_start_timestamp")
    @get:JsonProperty("event_start_timestamp")
    var eventStartTimestamp: String? = null,
    @field:JsonProperty("initiator_metadata")
    @get:JsonProperty("initiator_metadata")
    var initiatorMetadata: PushEventInitiatorMetadata? = null,
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: PushEventLocation? = null
) : Parcelable
