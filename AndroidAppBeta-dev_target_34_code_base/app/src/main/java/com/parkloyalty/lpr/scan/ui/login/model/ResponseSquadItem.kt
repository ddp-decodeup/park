package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponseSquadItem(
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,
    @field:JsonProperty("squad_name")
    @get:JsonProperty("squad_name")
    var squadName: String? = null
) : Parcelable
