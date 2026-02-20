package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SiteOfficerLoginMetadata(
    @field:JsonProperty("current_login")
    @get:JsonProperty("current_login")
    var currentLogin: String? = null,

    @field:JsonProperty("last_login")
    @get:JsonProperty("last_login")
    var lastLogin: String? = null
) : Parcelable
