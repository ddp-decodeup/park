package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponsePermit(
    @field:JsonProperty("length")
    @get:JsonProperty("length")
    var length: Long? = null,

    @field:JsonProperty("results")
    @get:JsonProperty("results")
    var results: List<PermitDataResponse>? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null
) : Parcelable
