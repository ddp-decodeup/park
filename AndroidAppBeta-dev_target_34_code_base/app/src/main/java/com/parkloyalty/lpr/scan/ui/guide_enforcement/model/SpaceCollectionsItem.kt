package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SpaceCollectionsItem(
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,
    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("asset_id")
    @get:JsonProperty("asset_id")
    var assetId: String? = null,
    @field:JsonProperty("space_id")
    @get:JsonProperty("space_id")
    var spaceId: String? = null
) : Parcelable
