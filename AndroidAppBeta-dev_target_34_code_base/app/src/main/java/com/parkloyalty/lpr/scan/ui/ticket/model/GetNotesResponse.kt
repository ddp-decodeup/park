package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class GetNotesResponse(
    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var isSuccess: Boolean = false,
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var dataNote: DataNote? = null
) : Parcelable
