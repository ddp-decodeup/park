package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class GetTicketResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<GetTicketData>? = null,

    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var success: Boolean? = null
) : Parcelable
