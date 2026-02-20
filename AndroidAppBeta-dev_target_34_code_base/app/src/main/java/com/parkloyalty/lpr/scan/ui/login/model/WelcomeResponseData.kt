package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class WelcomeResponseData(
    @field:JsonProperty("user")
    @get:JsonProperty("user")
    var user: WelcomeUser? = null,
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: WelcomeMetadata? = null
) : Parcelable
