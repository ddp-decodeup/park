package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetails(
    @field:JsonProperty("agency")
    @get:JsonProperty("agency")
    var agency: String? = null,

    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,

    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,

    @field:JsonProperty("name")
    @get:JsonProperty("name")
    var name: String? = null,

    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var squad: String? = null
) : Parcelable
