package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class GetGeneticHitListResponse(
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null,
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,
    @field:JsonProperty("total_count")
    @get:JsonProperty("total_count")
    var totalCount: Int? = null,
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<GeneticHitListData> = emptyList()
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class GeneticHitListData(
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,
    @field:JsonProperty("hit_id")
    @get:JsonProperty("hit_id")
    var hitId: String? = null,
    @field:JsonProperty("timestamp")
    @get:JsonProperty("timestamp")
    var timestamp: String? = null,
    @field:JsonProperty("timestamp_utc")
    @get:JsonProperty("timestamp_utc")
    var timestampUtc: String? = null,
    @field:JsonProperty("hot_list_category")
    @get:JsonProperty("hot_list_category")
    var hotListCategory: String? = null,
    @field:JsonProperty("parking_lot")
    @get:JsonProperty("parking_lot")
    var parkingLot: String? = null,
    @field:JsonProperty("patroller_id")
    @get:JsonProperty("patroller_id")
    var patrollerId: String? = null,
    @field:JsonProperty("patroller_name")
    @get:JsonProperty("patroller_name")
    var patrollerName: String? = null,
    @field:JsonProperty("is_post_matched")
    @get:JsonProperty("is_post_matched")
    var isPostMatched: String? = null,
    @field:JsonProperty("lp_state")
    @get:JsonProperty("lp_state")
    var lpState: String? = null,
    @field:JsonProperty("is_hit")
    @get:JsonProperty("is_hit")
    var isHit: String? = null,
    @field:JsonProperty("address")
    @get:JsonProperty("address")
    var address: String? = null,
    @field:JsonProperty("type_of_hit")
    @get:JsonProperty("type_of_hit")
    var typeOfHit: String? = null,
    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("date_of_violation")
    @get:JsonProperty("date_of_violation")
    var dateOfViolation: String? = null,
    @field:JsonProperty("images")
    @get:JsonProperty("images")
    var images: List<String>? = null
) : Parcelable
