package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PayBySpacedataSetObject(
    @field:JsonProperty("record_count")
    @get:JsonProperty("record_count")
    var recordCount: Int = 0,
    @field:JsonProperty("space_collections")
    @get:JsonProperty("space_collections")
    var spaceCollectionsDataSet: List<SpaceCollectionsDataSetItem>? = null
) : Parcelable
