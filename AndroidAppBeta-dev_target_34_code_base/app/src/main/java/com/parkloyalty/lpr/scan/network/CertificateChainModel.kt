package com.parkloyalty.lpr.scan.network

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CertificateChainModel(
    @field:JsonProperty("shaPINHash")
    @get:JsonProperty("shaPINHash")
    var shaPINHash: String? = null,

    @field:JsonProperty("serialNumber")
    @get:JsonProperty("serialNumber")
    var serialNumber: String? = null
) : Parcelable
