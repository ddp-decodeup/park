package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SettingMetadata(
    @field:JsonProperty("timezone_delta")
    @get:JsonProperty("timezone_delta")
    var timezoneDelta: String? = null,

    @field:JsonProperty("timezone_difference")
    @get:JsonProperty("timezone_difference")
    var timezoneDifference: String? = null,

    @field:JsonProperty("timezone_name")
    @get:JsonProperty("timezone_name")
    var timezoneName: String? = null
) : Parcelable