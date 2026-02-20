package com.parkloyalty.lpr.scan.ui.check_setup.model.timing

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class AddTimingRequest(
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitiude: Double? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lprNumber: String? = null,
    @field:JsonProperty("lp_state")
    @get:JsonProperty("lp_state")
    var lprState: String? = null,
    @field:JsonProperty("mark_issue_timestamp")
    @get:JsonProperty("mark_issue_timestamp")
    var markIssueTimestamp: String? = null,
    @field:JsonProperty("mark_start_timestamp")
    @get:JsonProperty("mark_start_timestamp")
    var markStartTimestamp: String? = null,
    @field:JsonProperty("meter_number")
    @get:JsonProperty("meter_number")
    var meterNumber: String? = null,
    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null,
    @field:JsonProperty("regulation_time")
    @get:JsonProperty("regulation_time")
    var regulationTime: Long? = null,
    @field:JsonProperty("regulation_time_value")
    @get:JsonProperty("regulation_time_value")
    var regulationTimeValue: String? = null,
    @field:JsonProperty("remark")
    @get:JsonProperty("remark")
    var remark: String? = null,
    @field:JsonProperty("remark_1")
    @get:JsonProperty("remark_1")
    var remark1: String? = null,
    @field:JsonProperty("remark_2")
    @get:JsonProperty("remark_2")
    var remark2: String? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,
    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,
    @field:JsonProperty("source")
    @get:JsonProperty("source")
    var source: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("supervisor")
    @get:JsonProperty("supervisor")
    var supervisor: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("pbc_zone")
    @get:JsonProperty("pbc_zone")
    var pbcZone: String? = null,
    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var mMake: String? = null,
    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var mModel: String? = null,
    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var mColor: String? = null,
    @field:JsonProperty("address")
    @get:JsonProperty("address")
    var mAddress: String? = null,
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var mLocation: String? = null,
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var mLot: String? = null,
    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var mVin: String? = null,
    @field:JsonProperty("violation")
    @get:JsonProperty("violation")
    var mViolation: String? = null,
    @field:JsonProperty("mark_time")
    @get:JsonProperty("mark_time")
    var mMarkTime: String? = null,
    @field:JsonProperty("tire_stem_front")
    @get:JsonProperty("tire_stem_front")
    var mTireStemFront: Int = 0,
    @field:JsonProperty("tire_stem_back")
    @get:JsonProperty("tire_stem_back")
    var mTireStemBack: Int = 0,
    @field:JsonProperty("images")
    @get:JsonProperty("images")
    var imageUrls: List<String>? = null
) : Parcelable
