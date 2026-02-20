package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SiteUserLoginRequest(
    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @field:JsonProperty("site_user_name")
    @get:JsonProperty("site_user_name")
    var siteUserName: String? = null,

    @field:JsonProperty("site_user_password")
    @get:JsonProperty("site_user_password")
    var siteUserPassword: String? = null
) : Parcelable
