package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SpaceCollectionsDataSetItem(
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,
    @field:JsonProperty("loc")
    @get:JsonProperty("loc")
    var loc: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("space_id")
    @get:JsonProperty("space_id")
    var spaceId: Int? = null,
    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null
) : Parcelable
