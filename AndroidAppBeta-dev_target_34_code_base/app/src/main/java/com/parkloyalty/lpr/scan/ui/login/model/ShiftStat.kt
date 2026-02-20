package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ShiftStat(
    @field:JsonProperty("shift_name")
    @get:JsonProperty("shift_name")
    var shiftName: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null,

    @field:JsonProperty("is_hide")
    @get:JsonProperty("is_hide")
    var isHide: Boolean = false
) : Parcelable
