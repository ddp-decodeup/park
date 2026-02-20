package com.parkloyalty.lpr.scan.ui.brokenmeter.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsBroken(
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,
    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null,
    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null
) : Parcelable
