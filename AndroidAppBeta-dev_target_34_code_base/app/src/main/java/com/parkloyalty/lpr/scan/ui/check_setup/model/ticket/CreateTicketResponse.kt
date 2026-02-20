package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CreateTicketResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: TicketResponseData? = null,

    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var success: Boolean? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: String? = null
) : Parcelable
