package com.parkloyalty.lpr.scan.ui.boot.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ViolationDetailsBoot(
    @field:JsonProperty("violation_code")
    @get:JsonProperty("violation_code")
    var violationCode: String? = null,

    @field:JsonProperty("violation_description")
    @get:JsonProperty("violation_description")
    var violationDescription: String? = null,

    @field:JsonProperty("violation_fine")
    @get:JsonProperty("violation_fine")
    var violationFine: Int = 0
) : Parcelable
