package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LoginRequest(
    @field:JsonProperty("password")
    @get:JsonProperty("password")
    var password: String? = null,

    @field:JsonProperty("email")
    @get:JsonProperty("email")
    var email: String? = null
) : Parcelable
