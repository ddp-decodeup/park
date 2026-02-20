package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ForgetPasswordRequest(
    @field:JsonProperty("site_officer_email")
    @get:JsonProperty("site_officer_email")
    var mSiteOfficerEmail: String? = null,
    @field:JsonProperty("site_officer_user_name")
    @get:JsonProperty("site_officer_user_name")
    var siteOfficerUserName: String? = null
) : Parcelable
