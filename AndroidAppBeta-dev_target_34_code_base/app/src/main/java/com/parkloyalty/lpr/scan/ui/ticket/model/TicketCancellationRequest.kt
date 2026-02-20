package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class TicketCancellationRequest(
    @field:JsonProperty("ticket_number")
    @get:JsonProperty("ticket_number")
    var ticketNumber: String? = null,

    @field:JsonProperty("request_type")
    @get:JsonProperty("request_type")
    var requestType: String? = null,

    @field:JsonProperty("notes")
    @get:JsonProperty("notes")
    var notes: String? = null
) : Parcelable