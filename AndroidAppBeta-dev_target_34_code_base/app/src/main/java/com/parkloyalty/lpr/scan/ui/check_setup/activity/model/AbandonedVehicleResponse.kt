package com.parkloyalty.lpr.scan.ui.check_setup.activity.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class AbandonedVehicleResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: DataAbandoned? = null,

    @field:JsonProperty("counts")
    @get:JsonProperty("counts")
    var counts: Int? = null,

    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var success: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Location(
    @field:JsonProperty("Type")
    @get:JsonProperty("Type")
    var type: String? = null,

    @field:JsonProperty("Coordinates")
    @get:JsonProperty("Coordinates")
    var coordinates: List<Double?>? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataAbandoned(
    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: String? = null,

    @field:JsonProperty("regulation_time")
    @get:JsonProperty("regulation_time")
    var regulationTime: Long? = null,

    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,

    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: String? = null,

    @field:JsonProperty("remark")
    @get:JsonProperty("remark")
    var remark: String? = null,

    @field:JsonProperty("source")
    @get:JsonProperty("source")
    var source: String? = null,

    @field:JsonProperty("meter_number")
    @get:JsonProperty("meter_number")
    var meterNumber: String? = null,

    @field:JsonProperty("mark_timing_type")
    @get:JsonProperty("mark_timing_type")
    var markTimingType: Int? = null,

    @field:JsonProperty("space")
    @get:JsonProperty("space")
    var space: String? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,

    @field:JsonProperty("remark_2")
    @get:JsonProperty("remark_2")
    var remark2: String? = null,

    @field:JsonProperty("tire_stem_front")
    @get:JsonProperty("tire_stem_front")
    var tireStemFront: Int? = null,

    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,

    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,

    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,

    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var vinNumber: String? = null,

    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,

    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,

    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,

    @field:JsonProperty("abandon_status")
    @get:JsonProperty("abandon_status")
    var abandonStatus: Int? = null,

    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,

    @field:JsonProperty("tire_stem_back")
    @get:JsonProperty("tire_stem_back")
    var tireStemBack: Int? = null,

    @field:JsonProperty("first_mark_images")
    @get:JsonProperty("first_mark_images")
    var firstMarkImages: @RawValue List<Any?>? = null,

    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,

    @field:JsonProperty("address")
    @get:JsonProperty("address")
    var address: String? = null,

    @field:JsonProperty("latest_mark_images")
    @get:JsonProperty("latest_mark_images")
    var latestMarkImages: List<String?>? = null,

    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null,

    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null,

    @field:JsonProperty("lp_state")
    @get:JsonProperty("lp_state")
    var lpState: String? = null,

    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: Location? = null,

    @field:JsonProperty("supervisor")
    @get:JsonProperty("supervisor")
    var supervisor: String? = null
) : Parcelable
