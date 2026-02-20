package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TimingMarkData(
    @field:JsonProperty("arrival_status")
    @get:JsonProperty("arrival_status")
    var arrialStatus: String? = null,

    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,

    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,

    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,

    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: Long? = null,

    @field:JsonProperty("difference")
    @get:JsonProperty("difference")
    var difference: Double? = null,

    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,

    @field:JsonProperty("violation")
    @get:JsonProperty("violation")
    var violation: String? = null,

    @field:JsonProperty("is_violation")
    @get:JsonProperty("is_violation")
    var isViolation: Boolean? = null,

    @field:JsonProperty("is_abandon_vehicle")
    @get:JsonProperty("is_abandon_vehicle")
    var isAbandonVehicle: Boolean? = null,

    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: TimingMarkLocation? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("lp_state")
    @get:JsonProperty("lp_state")
    var lpState: String? = null,

    @field:JsonProperty("mark_issue_timestamp")
    @get:JsonProperty("mark_issue_timestamp")
    var markIssueTimestamp: String? = null,

    @field:JsonProperty("mark_start_timestamp")
    @get:JsonProperty("mark_start_timestamp")
    var markStartTimestamp: String? = null,

    @field:JsonProperty("mark_timing_type")
    @get:JsonProperty("mark_timing_type")
    var markTimingType: Long? = null,

    @field:JsonProperty("meter_number")
    @get:JsonProperty("meter_number")
    var meterNumber: String? = null,

    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null,

    @field:JsonProperty("parent_mark_id")
    @get:JsonProperty("parent_mark_id")
    var parentMarkId: String? = null,

    @field:JsonProperty("regulation_time")
    @get:JsonProperty("regulation_time")
    var regulationTime: Long? = null,

    @field:JsonProperty("regulation_time_value")
    @get:JsonProperty("regulation_time_value")
    var regulationTimeValue: String? = null,

    @field:JsonProperty("remark")
    @get:JsonProperty("remark")
    var remark: String? = null,

    @field:JsonProperty("remark_2")
    @get:JsonProperty("remark_2")
    var remark2: String? = null,

    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,

    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,

    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null,

    @field:JsonProperty("source")
    @get:JsonProperty("source")
    var source: String? = null,

    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,

    @field:JsonProperty("supervisor")
    @get:JsonProperty("supervisor")
    var supervisor: String? = null,

    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,

    @field:JsonProperty("alert_type")
    @get:JsonProperty("alert_type")
    var alertType: String? = null,

    @field:JsonProperty("dc_hit")
    @get:JsonProperty("dc_hit")
    var dcHit: String? = null,

    @field:JsonProperty("vendor_name")
    @get:JsonProperty("vendor_name")
    var vendorName: String? = null,

    @field:JsonProperty("dc_delta_seconds")
    @get:JsonProperty("dc_delta_seconds")
    var dcDeltaSeconds: Int = 0,

    @field:JsonProperty("address")
    @get:JsonProperty("address")
    var address: String? = null,

    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: String? = null,

    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,

    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,

    @field:JsonProperty("ischecked")
    @get:JsonProperty("ischecked")
    var isChecked: Boolean = false,

    @field:JsonProperty("tire_stem_front")
    @get:JsonProperty("tire_stem_front")
    var tireStemFront: String? = null,

    @field:JsonProperty("tire_stem_back")
    @get:JsonProperty("tire_stem_back")
    var tireStemBack: String? = null,

    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var vinNumber: String? = null,

    @field:JsonProperty("time_limit_enforcement_observed_time")
    @get:JsonProperty("time_limit_enforcement_observed_time")
    var timeLimitEnforcementObservedTime: String? = null,

    @field:JsonProperty("first_observed_timestamp")
    @get:JsonProperty("first_observed_timestamp")
    var firstObservedTimestamp: String? = null,

    @field:JsonProperty("images")
    @get:JsonProperty("images")
    var images: List<String>? = null
) : Parcelable
