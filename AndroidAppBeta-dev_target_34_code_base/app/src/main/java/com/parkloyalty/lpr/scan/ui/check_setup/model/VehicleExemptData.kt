package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleExemptData(
    @field:JsonProperty("Expiry DateTime")
    @get:JsonProperty("Expiry DateTime")
    var expiryDateTime: String? = null,

    @field:JsonProperty("LPLocation")
    @get:JsonProperty("LPLocation")
    var lPLocation: String? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("Start DateT ime")
    @get:JsonProperty("Start DateT ime")
    var startDateTIme: String? = null,

    @field:JsonProperty("ZoneId")
    @get:JsonProperty("ZoneId")
    var zoneId: Long? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
