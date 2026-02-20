package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PushEventResMetadata(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: String? = null,
    @field:JsonProperty("event_finish_timestamp")
    @get:JsonProperty("event_finish_timestamp")
    var eventFinishTimestamp: String? = null,
    @field:JsonProperty("event_start_timestamp")
    @get:JsonProperty("event_start_timestamp")
    var eventStartTimestamp: String? = null
) : Parcelable
