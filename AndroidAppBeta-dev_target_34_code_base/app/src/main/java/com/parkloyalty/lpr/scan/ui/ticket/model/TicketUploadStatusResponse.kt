package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TicketUploadStatusResponse(
    @field:JsonProperty("citation_uploaded")
    @get:JsonProperty("citation_uploaded")
    var citationUploaded: Boolean? = null,

    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: Message? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Message(
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null
) : Parcelable
