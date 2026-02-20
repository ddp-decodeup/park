package com.parkloyalty.lpr.scan.ui.continuousmode.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class StartSessionMetadata(
    @field:JsonProperty("session_id")
    @get:JsonProperty("session_id")
    var sessionId: String? = null
) : Parcelable
