package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ActivityStat(
    @field:JsonProperty("activity")
    @get:JsonProperty("activity")
    var activity: String? = null,
    @field:JsonProperty("activity_key")
    @get:JsonProperty("activity_key")
    var activityKey: String? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var mId: String? = null
) : Parcelable
