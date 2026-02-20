package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ActivityCountData(
    @field:JsonProperty("activity_name")
    @get:JsonProperty("activity_name")
    var activityName: String? = null,

    @field:JsonProperty("activity_type")
    @get:JsonProperty("activity_type")
    var activityType: String? = null,

    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null,

    @field:JsonProperty("initiator_id")
    @get:JsonProperty("initiator_id")
    var initiatorId: String? = null,

    @field:JsonProperty("initiator_role")
    @get:JsonProperty("initiator_role")
    var initiatorRole: String? = null,

    @field:JsonProperty("log_type")
    @get:JsonProperty("log_type")
    var logType: String? = null,

    @field:JsonProperty("server_timestamp")
    @get:JsonProperty("server_timestamp")
    var serverTimestamp: Long? = null,

    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null,
) : Parcelable