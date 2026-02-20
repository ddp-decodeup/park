package com.parkloyalty.lpr.scan.ui.brokenmeter.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class InsertedMaintenanceData(
    @field:JsonProperty("server_timestamp")
    @get:JsonProperty("server_timestamp")
    var serverTimestamp: String? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var detailsBroken: DetailsBroken? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetailsBroken: OfficerDetailsBroken? = null,
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null,
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetailsBroken: LocationDetailsBroken? = null,
    @field:JsonProperty("client_timestamp_utc")
    @get:JsonProperty("client_timestamp_utc")
    var clientTimestampUtc: String? = null
) : Parcelable
