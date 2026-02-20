package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ZoneStat(
    @field:JsonProperty("zone_name")
    @get:JsonProperty("zone_name")
    var zoneName: String? = null,
    @field:JsonProperty("zone_metadata")
    @get:JsonProperty("zone_metadata")
    var mCityZoneName: String? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
