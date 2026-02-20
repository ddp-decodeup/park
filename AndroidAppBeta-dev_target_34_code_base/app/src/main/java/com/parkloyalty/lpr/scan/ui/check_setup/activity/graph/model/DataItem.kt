package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataItem(
    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null,

    @field:JsonProperty("initiator_role")
    @get:JsonProperty("initiator_role")
    var initiatorRole: String? = null,

    @field:JsonProperty("log_type")
    @get:JsonProperty("log_type")
    var logType: String? = null,

    @field:JsonProperty("server_timestamp")
    @get:JsonProperty("server_timestamp")
    var serverTimestamp: Int = 0,

    @field:JsonProperty("initiator_id")
    @get:JsonProperty("initiator_id")
    var initiatorId: String? = null,

    @field:JsonProperty("activity_type")
    @get:JsonProperty("activity_type")
    var activityType: String? = null,

    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double = 0.0,

    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,

    @field:JsonProperty("location_update_type")
    @get:JsonProperty("location_update_type")
    var locationUpdatetype: String? = null,

    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double = 0.0
) : Parcelable
