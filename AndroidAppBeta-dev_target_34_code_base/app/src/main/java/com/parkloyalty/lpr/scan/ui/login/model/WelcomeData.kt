package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class WelcomeData(
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var isUser: Boolean = false,
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var responsedata: WelcomeResponseData? = null
) : Parcelable
