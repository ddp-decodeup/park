package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ShiftMetadata(
    @field:JsonProperty("end_time")
    @get:JsonProperty("end_time")
    var endTime: String? = null,

    @field:JsonProperty("start_time")
    @get:JsonProperty("start_time")
    var startTime: String? = null
) : Parcelable
