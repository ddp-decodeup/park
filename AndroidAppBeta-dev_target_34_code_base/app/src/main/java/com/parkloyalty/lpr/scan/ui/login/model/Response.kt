package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Response(
    @field:JsonProperty("enforcement_agency_id")
    @get:JsonProperty("enforcement_agency_id")
    var enforcementAgencyId: String? = null,

    @field:JsonProperty("exp")
    @get:JsonProperty("exp")
    var exp: Long? = null,

    @field:JsonProperty("role")
    @get:JsonProperty("role")
    var role: String? = null
) : Parcelable
