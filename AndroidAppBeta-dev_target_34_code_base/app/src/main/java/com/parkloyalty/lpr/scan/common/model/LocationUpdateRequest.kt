package com.parkloyalty.lpr.scan.common.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationUpdateRequest(
    @field:JsonProperty("activity_type")
    @get:JsonProperty("activity_type")
    var activityType: String? = null,

    @field:JsonProperty("initiator_id")
    @get:JsonProperty("initiator_id")
    var initiatorId: String? = null,

    @field:JsonProperty("initiator_role")
    @get:JsonProperty("initiator_role")
    var initiatorRole: String? = null,

    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,

    @field:JsonProperty("log_type")
    @get:JsonProperty("log_type")
    var logType: String? = null,

    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double? = null,

    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null
) : Parcelable
