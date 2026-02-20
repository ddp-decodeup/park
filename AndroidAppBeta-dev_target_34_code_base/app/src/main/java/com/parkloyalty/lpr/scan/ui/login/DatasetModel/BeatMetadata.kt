package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BeatMetadata(
    @field:JsonProperty("beat_hq")
    @get:JsonProperty("beat_hq")
    var beatHq: Double? = null,

    @field:JsonProperty("name")
    @get:JsonProperty("name")
    var name: Long? = null
) : Parcelable