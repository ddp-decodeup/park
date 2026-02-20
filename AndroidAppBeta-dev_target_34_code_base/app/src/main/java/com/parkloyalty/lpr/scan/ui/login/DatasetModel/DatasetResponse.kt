package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DatasetResponse(
    @field:JsonProperty("name")
    @get:JsonProperty("name")
    var name: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null,

    @field:JsonProperty("year")
    @get:JsonProperty("year")
    var year: String? = null,

    @field:JsonProperty("side_name")
    @get:JsonProperty("side_name")
    var sideName: String? = null,

    @field:JsonProperty("enable")
    @get:JsonProperty("enable")
    var enable: Boolean? = null,

    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @field:JsonProperty("street_name")
    @get:JsonProperty("street_name")
    var street_name: String? = null,

    @field:JsonProperty("street_lookup_code")
    @get:JsonProperty("street_lookup_code")
    var street_lookup_code: String? = null,

    @field:JsonProperty("cancel_reason")
    @get:JsonProperty("cancel_reason")
    var cancelReason: String? = null,

    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var violationCode: String? = null,

    @field:JsonProperty("violation_description")
    @get:JsonProperty("violation_description")
    var violationDescription: String? = null,

    @field:JsonProperty("violation")
    @get:JsonProperty("violation")
    var violation: String? = null,

    @field:JsonProperty("amount")
    @get:JsonProperty("amount")
    var violationFine: Double? = null,

    @field:JsonProperty("late_fine")
    @get:JsonProperty("late_fine")
    var mViolationLateFine: Double? = null,

    @field:JsonProperty("regulation")
    @get:JsonProperty("regulation")
    var regulation: String? = null,

    @field:JsonProperty("violation_query_type")
    @get:JsonProperty("violation_query_type")
    var violationQueryType: String? = null,

    @field:JsonProperty("violation_reference_code")
    @get:JsonProperty("violation_reference_code")
    var violationReferenceCode: String? = null,

    @field:JsonProperty("supervisor_metadata")
    @get:JsonProperty("supervisor_metadata")
    var supervisorMetadata: SupervisorMetadata? = null,

    @field:JsonProperty("supervisor_name")
    @get:JsonProperty("supervisor_name")
    var supervisorName: String? = null,

    @field:JsonProperty("supervisor_badge_id")
    @get:JsonProperty("supervisor_badge_id")
    var supervisorBadgeId: String? = null,

    @field:JsonProperty("officer_supervisor")
    @get:JsonProperty("officer_supervisor")
    var officerSupervisor: String? = null,

    @field:JsonProperty("friendly_name")
    @get:JsonProperty("friendly_name")
    var friendlyName: String? = null,

    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,

    @field:JsonProperty("android_id")
    @get:JsonProperty("android_id")
    var androidId: String? = null,

    @field:JsonProperty("license")
    @get:JsonProperty("license")
    var license: String? = null,

    @field:JsonProperty("device_friendly_name")
    @get:JsonProperty("device_friendly_name")
    var deviceFriendlyName: String? = null,

    @field:JsonProperty("equipment_name")
    @get:JsonProperty("equipment_name")
    var equipmentName: String? = null,

    @field:JsonProperty("equipment_type")
    @get:JsonProperty("equipment_type")
    var equipmentType: String? = null,

    @field:JsonProperty("equipment_id")
    @get:JsonProperty("equipment_id")
    var equipmentId: String? = null,

    @field:JsonProperty("squad_name")
    @get:JsonProperty("squad_name")
    var squadName: String? = null,

    @field:JsonProperty("zone_name")
    @get:JsonProperty("zone_name")
    var zoneName: String? = null,

    @field:JsonProperty("shift_metadata")
    @get:JsonProperty("shift_metadata")
    var shiftMetadata: String? = null,

    @field:JsonProperty("shift_name")
    @get:JsonProperty("shift_name")
    var shiftName: String? = null,

    @field:JsonProperty("shift_start")
    @get:JsonProperty("shift_start")
    var shift_start: String? = null,

    @field:JsonProperty("shift_end")
    @get:JsonProperty("shift_end")
    var shift_end: String? = null,

    @field:JsonProperty("abbrev")
    @get:JsonProperty("abbrev")
    var abbrev: String? = null,

    @field:JsonProperty("activity")
    @get:JsonProperty("activity")
    var activityName: String? = null,

    @field:JsonProperty("activity_key")
    @get:JsonProperty("activity_key")
    var activityKey: String? = null,

    @field:JsonProperty("zone_metadata")
    @get:JsonProperty("zone_metadata")
    var zoneMetadata: String? = null,

    @field:JsonProperty("beat_metadata")
    @get:JsonProperty("beat_metadata")
    var beatMetadata: BeatMetadata? = null,

    @field:JsonProperty("beat_name")
    @get:JsonProperty("beat_name")
    var beatName: String? = null,

//    @field:JsonProperty("radio_metadata")
//    @get:JsonProperty("radio_metadata")
//    var radioMetadata: String? = null,

    @field:JsonProperty("radio_name")
    @get:JsonProperty("radio_name")
    var radioName: String? = null,

    @field:JsonProperty("remark")
    @get:JsonProperty("remark")
    var remark: String? = null,

    @field:JsonProperty("note")
    @get:JsonProperty("note")
    var note: String? = null,

    @field:JsonProperty("tier_stem_name")
    @get:JsonProperty("tier_stem_name")
    var tierStem: String? = null,

    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,

    @field:JsonProperty("make_full")
    @get:JsonProperty("make_full")
    var makeText: String? = null,

    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,

    @field:JsonProperty("model_lookup_code")
    @get:JsonProperty("model_lookup_code")
    var model_lookup_code: String? = null,

    @field:JsonProperty("color_code")
    @get:JsonProperty("color_code")
    var color_code: String? = null,

    @field:JsonProperty("description")
    @get:JsonProperty("description")
    var description: String? = null,

    @field:JsonProperty("state_abbreviated")
    @get:JsonProperty("state_abbreviated")
    var state_abbreviated: String? = null,

    @field:JsonProperty("state_name")
    @get:JsonProperty("state_name")
    var state_name: String? = null,

    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,

    @field:JsonProperty("body_style")
    @get:JsonProperty("body_style")
    var body_style: String? = null,

    @field:JsonProperty("body_style_lookup_code")
    @get:JsonProperty("body_style_lookup_code")
    var body_style_lookup_code: String? = null,

    @field:JsonProperty("last_name")
    @get:JsonProperty("last_name")
    var last_name: String? = null,

    @field:JsonProperty("first_name")
    @get:JsonProperty("first_name")
    var first_name: String? = null,

    @field:JsonProperty("agency_name")
    @get:JsonProperty("agency_name")
    var agency_name: String? = null,

    @field:JsonProperty("due_15_days")
    @get:JsonProperty("due_15_days")
    var due_15_days: String? = null,

    @field:JsonProperty("due_30_days")
    @get:JsonProperty("due_30_days")
    var due_30_days: String? = null,

    @field:JsonProperty("due_45_days")
    @get:JsonProperty("due_45_days")
    var due_45_days: String? = null,

    @field:JsonProperty("cost")
    @get:JsonProperty("cost")
    var cost: String? = null,

    @field:JsonProperty("parking_fee")
    @get:JsonProperty("parking_fee")
    var parkingFee: String? = null,

    @field:JsonProperty("citation_fee")
    @get:JsonProperty("citation_fee")
    var citationFee: String? = null,

    @field:JsonProperty("total_due_now")
    @get:JsonProperty("total_due_now")
    var total_due_now: String? = null,

    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,

    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null,

    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: String? = null,

    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,

    @field:JsonProperty("branch_lotid")
    @get:JsonProperty("branch_lotid")
    var lotBranchId: String? = null,

    @field:JsonProperty("lot_lookup_code")
    @get:JsonProperty("lot_lookup_code")
    var lotLookupCode: String? = null,

    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: SettingMetadata? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null,

    @field:JsonProperty("value")
    @get:JsonProperty("value")
    var mValue: String? = null,

    @field:JsonProperty("repr")
    @get:JsonProperty("repr")
    var repr: String? = null,

    @field:JsonProperty("regex")
    @get:JsonProperty("regex")
    var mRegex: String? = null,

    @field:JsonProperty("just_in_time_check")
    @get:JsonProperty("just_in_time_check")
    var mJustInTimeCheck: String? = null,

    @field:JsonProperty("zone_id")
    @get:JsonProperty("zone_id")
    var mZoneId: String? = null,

    @field:JsonProperty("time")
    @get:JsonProperty("time")
    var mTime: String? = null,

    @field:JsonProperty("void_and_reissue_reason")
    @get:JsonProperty("void_and_reissue_reason")
    var mVoidAndReiccueReason: String? = null,

    @field:JsonProperty("void_reason_lookup_code")
    @get:JsonProperty("void_reason_lookup_code")
    var void_reason_lookup_code: String? = null,

    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var lotZone: String? = null,

    @field:JsonProperty("meterlist_req")
    @get:JsonProperty("meterlist_req")
    var meterlistReq: String? = null,

    @field:JsonProperty("side_short")
    @get:JsonProperty("side_short")
    var sideshort: String? = null,

    @field:JsonProperty("block_name")
    @get:JsonProperty("block_name")
    var blockName: String? = null,

    @field:JsonProperty("timelimit_vio")
    @get:JsonProperty("timelimit_vio")
    var timelimitVio: String? = null,

    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: String? = null,

    @field:JsonProperty("space_name")
    @get:JsonProperty("space_name")
    var spaceName: String? = null,

    @field:JsonProperty("PBC_req")
    @get:JsonProperty("PBC_req")
    var pbcReq: String? = null,

    @field:JsonProperty("scofflaw_vio")
    @get:JsonProperty("scofflaw_vio")
    var scofflawVio: String? = null,

    @field:JsonProperty("permit_vio")
    @get:JsonProperty("permit_vio")
    var permitVio: String? = null,

    @field:JsonProperty("violationLateFee")
    @get:JsonProperty("violationLateFee")
    var violationLateFee: Long? = null,

    @field:JsonProperty("escalated2")
    @get:JsonProperty("escalated2")
    var mEscalated2: Double? = null,

    @field:JsonProperty("escalated3")
    @get:JsonProperty("escalated3")
    var mEscalated3: Double? = null,

    @field:JsonProperty("escalated4")
    @get:JsonProperty("escalated4")
    var mEscalated4: Double? = null,

    @field:JsonProperty("escalated5")
    @get:JsonProperty("escalated5")
    var mEscalated5: Double? = null,

    @field:JsonProperty("pbc_zone")
    @get:JsonProperty("pbc_zone")
    var mPbcZone: String? = null,

    @field:JsonProperty("export_code")
    @get:JsonProperty("export_code")
    var mExportCode: String? = null,

    @field:JsonProperty("amount_days")
    @get:JsonProperty("amount_days")
    var mAmountDays: String? = null,

    @field:JsonProperty("late_fine_days")
    @get:JsonProperty("late_fine_days")
    var mLateFineDays: String? = null,

    @field:JsonProperty("due_15_date_days")
    @get:JsonProperty("due_15_date_days")
    var mDue15DateDays: String? = null,

    @field:JsonProperty("due_30_date_days")
    @get:JsonProperty("due_30_date_days")
    var mDue30DateDays: String? = null,

    @field:JsonProperty("lock")
    @get:JsonProperty("lock")
    var mLock: Boolean? = false,

    @field:JsonProperty("mark_time")
    @get:JsonProperty("mark_time")
    var mMarkTime: String? = "0",

    @field:JsonProperty("zone_mandatory")
    @get:JsonProperty("zone_mandatory")
    var mZoneMandatory: String? = "0",

    @field:JsonProperty("warning_violation")
    @get:JsonProperty("warning_violation")
    var mWarningViolation: String? = "0",

    @field:JsonProperty("city_name")
    @get:JsonProperty("city_name")
    var mCityName: String? = null,

    @field:JsonProperty("sanctions_sticker")
    @get:JsonProperty("sanctions_sticker")
    var mSanctionsSticker: Int? = 0,

    @field:JsonProperty("Is_visible")
    @get:JsonProperty("Is_visible")
    var mIsVisible: Int? = 1,

    @field:JsonProperty("flat_fine_by_lot")
    @get:JsonProperty("flat_fine_by_lot")
    var mFlatFineByLot: Int? = 0, // 0 in active 1 add extra amount from lot list and add it to violation amount for that particular violation

    @field:JsonProperty("vio_type_code")
    @get:JsonProperty("vio_type_code")
    var mvio_type_code: String? = "",

    @field:JsonProperty("vio_type_description")
    @get:JsonProperty("vio_type_description")
    var mvio_type_description: String? = "",

    @field:JsonProperty("vio_type")
    @get:JsonProperty("vio_type")
    var mvio_type: String? = "",

    @field:JsonProperty("holiday")
    @get:JsonProperty("holiday")
    var holiday: String? = "",

    @field:JsonProperty("date")
    @get:JsonProperty("date")
    var date: String? = "",

    @field:JsonProperty("day")
    @get:JsonProperty("day")
    var day: String? = ""
) : Parcelable {

    companion object {
        fun setDirection(direction: String?): DatasetResponse {
            return DatasetResponse(direction = direction)
        }
        fun setSideName(sideName: String?, i: Int): DatasetResponse {
            return DatasetResponse(sideName = sideName)
        }
    }
}