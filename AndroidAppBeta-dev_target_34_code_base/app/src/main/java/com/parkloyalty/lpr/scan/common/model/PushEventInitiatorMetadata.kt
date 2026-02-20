package com.parkloyalty.lpr.scan.common.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PushEventInitiatorMetadata(
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,
    @field:JsonProperty("officer_id")
    @get:JsonProperty("officer_id")
    var officerId: String? = null,
    @field:JsonProperty("radio")
    @get:JsonProperty("radio")
    var radio: String? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,
    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var squad: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null
) : Parcelable
