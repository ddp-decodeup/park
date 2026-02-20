package com.parkloyalty.lpr.scan.ui.continuousmode.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UploadStaticResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var uploaddata: List<UploaddataItem>? = null,
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var isStatus: Boolean = false
) : Parcelable
