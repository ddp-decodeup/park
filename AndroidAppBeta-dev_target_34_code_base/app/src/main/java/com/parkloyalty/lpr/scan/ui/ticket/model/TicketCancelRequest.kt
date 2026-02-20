package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TicketCancelRequest(
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,
    @field:JsonProperty("status_comments")
    @get:JsonProperty("status_comments")
    var mNote: String? = null,
    @field:JsonProperty("status_reason")
    @get:JsonProperty("status_reason")
    var mReason: String? = null,
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var mType: String? = null,
    @field:JsonProperty("void_reason_lookup_code")
    @get:JsonProperty("void_reason_lookup_code")
    var void_reason_lookup_code: String? = null
) : Parcelable
