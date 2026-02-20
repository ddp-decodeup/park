package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ZoneDataset(
    @field:JsonProperty("enable")
    @get:JsonProperty("enable")
    var enable: Boolean? = null,

    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @field:JsonProperty("street_id")
    @get:JsonProperty("street_id")
    var streetId: String? = null,

    @field:JsonProperty("zone_metadata")
    @get:JsonProperty("zone_metadata")
    var zoneMetadata: ZoneMetadata? = null,

    @field:JsonProperty("zone_name")
    @get:JsonProperty("zone_name")
    var zoneName: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
