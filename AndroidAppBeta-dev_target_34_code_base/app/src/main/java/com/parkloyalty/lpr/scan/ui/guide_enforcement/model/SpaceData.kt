package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SpaceData(
    @field:JsonProperty("page_number")
    @get:JsonProperty("page_number")
    var pageNumber: Int = 0,
    @field:JsonProperty("record_count")
    @get:JsonProperty("record_count")
    var recordCount: Int = 0,
    @field:JsonProperty("total_pages")
    @get:JsonProperty("total_pages")
    var totalPages: Int = 0,
    @field:JsonProperty("timeseries")
    @get:JsonProperty("timeseries")
    var timeseries: Timeseries? = null,
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: List<ResponseItem>? = null,
    @field:JsonProperty("space_collections")
    @get:JsonProperty("space_collections")
    var spaceCollections: List<SpaceCollectionsItem>? = null
) : Parcelable
