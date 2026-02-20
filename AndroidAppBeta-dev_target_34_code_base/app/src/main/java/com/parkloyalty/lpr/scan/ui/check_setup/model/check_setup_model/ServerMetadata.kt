package com.parkloyalty.lpr.scan.ui.check_setup.model.check_setup_model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ServerMetadata(
    @field:JsonProperty("server_timestamp")
    @get:JsonProperty("server_timestamp")
    var serverTimestamp: String? = null
) : Parcelable
