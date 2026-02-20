package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class InactiveMeterBuzzerResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: Data? = null,
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Data(
    @field:JsonProperty("inactive")
    @get:JsonProperty("inactive")
    var inactive: Boolean? = null,
    @field:JsonProperty("inactive_meters")
    @get:JsonProperty("inactive_meters")
    var inactiveMeters: List<InactiveMetersItem?>? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class InactiveMetersItem(
    @field:JsonProperty("last_communication_date")
    @get:JsonProperty("last_communication_date")
    var lastCommunicationDate: String? = null,
    @field:JsonProperty("meter_guid")
    @get:JsonProperty("meter_guid")
    var meterGuid: String? = null,
    @field:JsonProperty("meter_id")
    @get:JsonProperty("meter_id")
    var meterId: String? = null
) : Parcelable
