package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SupervisorStat(
    @field:JsonProperty("supervisor_badge_id")
    @get:JsonProperty("supervisor_badge_id")
    var mSuperBadgeId: String? = null,
    @field:JsonProperty("officer_supervisor")
    @get:JsonProperty("officer_supervisor")
    var mSuperName: String? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable