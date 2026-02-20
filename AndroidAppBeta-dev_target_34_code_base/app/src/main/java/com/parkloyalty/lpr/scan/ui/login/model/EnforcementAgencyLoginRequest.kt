package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class EnforcementAgencyLoginRequest(
    @field:JsonProperty("enforcement_agency_name")
    @get:JsonProperty("enforcement_agency_name")
    var enforcementAgencyName: String? = null,

    @field:JsonProperty("enforcement_agency_password")
    @get:JsonProperty("enforcement_agency_password")
    var enforcementAgencyPassword: String? = null
) : Parcelable
