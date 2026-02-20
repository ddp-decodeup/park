package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DownloadBitmapRequest(
    @field:JsonProperty("download_type")
    @get:JsonProperty("download_type")
    var downloadType: String? = null,
    @field:JsonProperty("links")
    @get:JsonProperty("links")
    var links: Links? = null
) : Parcelable
