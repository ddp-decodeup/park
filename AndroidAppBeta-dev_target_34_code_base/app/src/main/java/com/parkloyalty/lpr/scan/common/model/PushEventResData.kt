package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PushEventResData(
    @field:JsonProperty("event_id")
    @get:JsonProperty("event_id")
    var eventId: String? = null,
    @field:JsonProperty("event_initiator_agency_id")
    @get:JsonProperty("event_initiator_agency_id")
    var eventInitiatorAgencyId: String? = null,
    @field:JsonProperty("event_initiator_id")
    @get:JsonProperty("event_initiator_id")
    var eventInitiatorId: String? = null,
    @field:JsonProperty("event_initiator_role")
    @get:JsonProperty("event_initiator_role")
    var eventInitiatorRole: String? = null,
    @field:JsonProperty("event_metadata")
    @get:JsonProperty("event_metadata")
    var eventMetadata: PushEventResMetadata? = null,
    @field:JsonProperty("event_timestamp")
    @get:JsonProperty("event_timestamp")
    var eventTimestamp: String? = null,
    @field:JsonProperty("event_type")
    @get:JsonProperty("event_type")
    var eventType: String? = null
) : Parcelable
