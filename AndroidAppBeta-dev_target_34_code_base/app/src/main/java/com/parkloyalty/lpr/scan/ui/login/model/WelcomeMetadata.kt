package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class WelcomeMetadata(
    @field:JsonProperty("beat_stats")
    @get:JsonProperty("beat_stats")
    var beatStats: List<BeatStat>? = null,
    @field:JsonProperty("radio_stats")
    @get:JsonProperty("radio_stats")
    var radioStats: List<RadioSt>? = null,
    @field:JsonProperty("shift_stats")
    @get:JsonProperty("shift_stats")
    var shiftStats: List<ShiftStat>? = null,
    @field:JsonProperty("supervisor_stats")
    @get:JsonProperty("supervisor_stats")
    var supervisorStats: List<SupervisorStat>? = null,
    @field:JsonProperty("zone_stats")
    @get:JsonProperty("zone_stats")
    var zoneStats: List<ZoneStat>? = null,
    @field:JsonProperty("acivity_stats")
    @get:JsonProperty("acivity_stats")
    var activityStats: List<ActivityStat>? = null
) : Parcelable
