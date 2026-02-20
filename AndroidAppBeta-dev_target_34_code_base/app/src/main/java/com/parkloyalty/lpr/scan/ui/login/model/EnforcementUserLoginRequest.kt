package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class EnforcementUserLoginRequest(
    @field:JsonProperty("enforcement_user_id")
    @get:JsonProperty("enforcement_user_id")
    var enforcementUserId: String? = null,

    @field:JsonProperty("enforcement_user_password")
    @get:JsonProperty("enforcement_user_password")
    var enforcementUserPassword: String? = null
) : Parcelable
