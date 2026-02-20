package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class AuditTrail(
    @field:JsonProperty("OldValue")
    @get:JsonProperty("OldValue")
    var oldValue: String? = null,

    @field:JsonProperty("NewValue")
    @get:JsonProperty("NewValue")
    var newValue: String? = null,

    @field:JsonProperty("UpdateType")
    @get:JsonProperty("UpdateType")
    var updateType: String? = null,

    @field:JsonProperty("TimestampUTC")
    @get:JsonProperty("TimestampUTC")
    var timestampUTC: String? = null,

    @field:JsonProperty("Timestamp")
    @get:JsonProperty("Timestamp")
    var timestamp: String? = null,

    @field:JsonProperty("InitiatorID")
    @get:JsonProperty("InitiatorID")
    var initiatorID: String? = null,

    @field:JsonProperty("InitiatorRole")
    @get:JsonProperty("InitiatorRole")
    var initiatorRole: String? = null,

    @field:JsonProperty("Reason")
    @get:JsonProperty("Reason")
    var reason: String? = null,

    @field:JsonProperty("Comment")
    @get:JsonProperty("Comment")
    var comment: String? = null
) : Parcelable
