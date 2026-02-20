package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Metadata(
    @field:JsonProperty("length")
    @get:JsonProperty("length")
    var mLength: Long? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null
) : Parcelable
