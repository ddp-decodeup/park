package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class RadioMetadata(
    @field:JsonProperty("name")
    @get:JsonProperty("name")
    var name: String? = null,

    @field:JsonProperty("radio_frequency")
    @get:JsonProperty("radio_frequency")
    var radioFrequency: Double? = null,

    @field:JsonProperty("screen_name")
    @get:JsonProperty("screen_name")
    var screenName: String? = null
) : Parcelable
