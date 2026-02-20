package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ForgotPasswordResponse(
    @PrimaryKey
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: String,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null,

    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: SiteOfficerLoginMetadata? = null
) : Parcelable
