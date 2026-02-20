package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationOfficerModel(
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: String? = null,
    @field:JsonProperty("officer_details_label")
    @get:JsonProperty("officer_details_label")
    var officerDetailsLabel: String? = null,
    @field:JsonProperty("officer_details_column")
    @get:JsonProperty("officer_details_column")
    var officerDetailsColumn: Int? = 1,
    @field:JsonProperty("status_officer_details")
    @get:JsonProperty("status_officer_details")
    var isStatus_officer_details: Boolean = false,
    @field:JsonProperty("print_order_officer_details")
    @get:JsonProperty("print_order_officer_details")
    var mPrintOrderOfficerDetails: Double = 0.0,
    @field:JsonProperty("print_layout_order_officer_details")
    @get:JsonProperty("print_layout_order_officer_details")
    var mPrintLayoutOrderOfficerDetails: String? = null,
    @field:JsonProperty("officer_details_x")
    @get:JsonProperty("officer_details_x")
    var mOfficerDetailsX: Double = 0.0,
    @field:JsonProperty("officer_details_y")
    @get:JsonProperty("officer_details_y")
    var mOfficerDetailsY: Double = 0.0,
    @field:JsonProperty("officer_details_font")
    @get:JsonProperty("officer_details_font")
    var mOfficerDetailsFont: Int = 0,
    @field:JsonProperty("officer_details_column_size")
    @get:JsonProperty("officer_details_column_size")
    var mOfficerDetailsColumnSize: Int = 0,
    @field:JsonProperty("officer_id")
    @get:JsonProperty("officer_id")
    var officerId: String? = null,
    @field:JsonProperty("officer_id_label")
    @get:JsonProperty("officer_id_label")
    var officerIdLabel: String? = null,
    @field:JsonProperty("officer_id_column")
    @get:JsonProperty("officer_id_column")
    var officerIdColumn: Int? = 1,
    @field:JsonProperty("status_officer_id")
    @get:JsonProperty("status_officer_id")
    var isStatus_officer_id: Boolean = false,
    @field:JsonProperty("print_order_officer_id")
    @get:JsonProperty("print_order_officer_id")
    var mPrintOrderOfficerId: Double = 0.0,
    @field:JsonProperty("print_layout_order_officer_id")
    @get:JsonProperty("print_layout_order_officer_id")
    var mPrintLayoutOrderOfficerId: String? = null,
    @field:JsonProperty("due_date_30_x")
    @get:JsonProperty("due_date_30_x")
    var mDueDate30X: Double = 0.0,
    @field:JsonProperty("due_date_30_y")
    @get:JsonProperty("due_date_30_y")
    var mDueDate30Y: Double = 0.0,
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,
    @field:JsonProperty("officer_lookup_code")
    @get:JsonProperty("officer_lookup_code")
    var officer_lookup_code: String? = null,
    @field:JsonProperty("badge_id_label")
    @get:JsonProperty("badge_id_label")
    var badgeIdLabel: String? = null,
    @field:JsonProperty("badge_id_column")
    @get:JsonProperty("badge_id_column")
    var badgeIdColumn: Int? = 1,
    @field:JsonProperty("status_badge_id")
    @get:JsonProperty("status_badge_id")
    var isStatus_badge_id: Boolean = false,
    @field:JsonProperty("print_order_badge_id")
    @get:JsonProperty("print_order_badge_id")
    var mPrintOrderBadgeId: Double = 0.0,
    @field:JsonProperty("print_layout_order_badge_id")
    @get:JsonProperty("print_layout_order_badge_id")
    var mPrintLayoutOrderBadgeId: String? = null,
    @field:JsonProperty("badge_id_x")
    @get:JsonProperty("badge_id_x")
    var mBadgeIdX: Double = 0.0,
    @field:JsonProperty("badgeId_y")
    @get:JsonProperty("badgeId_y")
    var mBadgeIdY: Double = 0.0,
    @field:JsonProperty("badgeId_font")
    @get:JsonProperty("badgeId_font")
    var mBadgeIdFont: Int = 0,
    @field:JsonProperty("badge_id_column_size")
    @get:JsonProperty("badge_id_column_size")
    var mBadgeIdColumnSize: Int = 0,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,
    @field:JsonProperty("beat_label")
    @get:JsonProperty("beat_label")
    var beatLabel: String? = null,
    @field:JsonProperty("beat_column")
    @get:JsonProperty("beat_column")
    var beatColumn: Int? = 1,
    @field:JsonProperty("status_beat")
    @get:JsonProperty("status_beat")
    var isStatus_beat: Boolean = false,
    @field:JsonProperty("print_order_beat")
    @get:JsonProperty("print_order_beat")
    var mPrintOrderBeat: Double = 0.0,
    @field:JsonProperty("print_layout_order_beat")
    @get:JsonProperty("print_layout_order_beat")
    var mPrintLayoutOrderBeat: String? = null,
    @field:JsonProperty("beat_x")
    @get:JsonProperty("beat_x")
    var mBeatX: Double = 0.0,
    @field:JsonProperty("beat_y")
    @get:JsonProperty("beat_y")
    var mBeatY: Double = 0.0,
    @field:JsonProperty("beat_font")
    @get:JsonProperty("beat_font")
    var mBeatFont: Int = 0,
    @field:JsonProperty("beat_column_size")
    @get:JsonProperty("beat_column_size")
    var mBeatColumnSize: Int = 0,
    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var squad: String? = null,
    @field:JsonProperty("squad_label")
    @get:JsonProperty("squad_label")
    var squadLable: String? = null,
    @field:JsonProperty("squad_column")
    @get:JsonProperty("squad_column")
    var squadColumn: Int? = 1,
    @field:JsonProperty("status_squad")
    @get:JsonProperty("status_squad")
    var isStatus_squad: Boolean = false,
    @field:JsonProperty("print_order_squad")
    @get:JsonProperty("print_order_squad")
    var mPrintOrderSquad: Double = 0.0,
    @field:JsonProperty("print_layout_order_squad")
    @get:JsonProperty("print_layout_order_squad")
    var mPrintLayoutOrderSquad: String? = null,
    @field:JsonProperty("squad_x")
    @get:JsonProperty("squad_x")
    var mSquadX: Double = 0.0,
    @field:JsonProperty("squad_y")
    @get:JsonProperty("squad_y")
    var mSquadY: Double = 0.0,
    @field:JsonProperty("squad_font")
    @get:JsonProperty("squad_font")
    var mSquadFont: Int = 0,
    @field:JsonProperty("squad_column_size")
    @get:JsonProperty("squad_column_size")
    var mSquadColumnSize: Int = 0,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("zone_label")
    @get:JsonProperty("zone_label")
    var zoneLabel: String? = null,
    @field:JsonProperty("zone_column")
    @get:JsonProperty("zone_column")
    var zoneColumn: Int? = 1,
    @field:JsonProperty("status_zone")
    @get:JsonProperty("status_zone")
    var isStatus_zone: Boolean = false,
    @field:JsonProperty("print_order_zone")
    @get:JsonProperty("print_order_zone")
    var mPrintOrderZone: Double = 0.0,
    @field:JsonProperty("print_layout_order_zone")
    @get:JsonProperty("print_layout_order_zone")
    var mPrintLayoutOrderZone: String? = null,
    @field:JsonProperty("zone_x")
    @get:JsonProperty("zone_x")
    var mZoneX: Double = 0.0,
    @field:JsonProperty("zone_y")
    @get:JsonProperty("zone_y")
    var mZoneY: Double = 0.0,
    @field:JsonProperty("zone_font")
    @get:JsonProperty("zone_font")
    var mZoneFont: Int = 0,
    @field:JsonProperty("zone_column_size")
    @get:JsonProperty("zone_column_size")
    var mZoneColumnSize: Int = 0,
    @field:JsonProperty("agency")
    @get:JsonProperty("agency")
    var agency: String? = null,
    @field:JsonProperty("agency_label")
    @get:JsonProperty("agency_label")
    var agencyLabel: String? = null,
    @field:JsonProperty("agency_column")
    @get:JsonProperty("agency_column")
    var agencyColumn: Int? = 1,
    @field:JsonProperty("status_agency")
    @get:JsonProperty("status_agency")
    var isStatus_agency: Boolean = false,
    @field:JsonProperty("print_order_agency")
    @get:JsonProperty("print_order_agency")
    var mPrintOrderAgency: Double = 0.0,
    @field:JsonProperty("print_layout_order_agency")
    @get:JsonProperty("print_layout_order_agency")
    var mPrintLayoutOrderAgency: String? = null,
    @field:JsonProperty("agency_x")
    @get:JsonProperty("agency_x")
    var mAgencyX: Double = 0.0,
    @field:JsonProperty("agency_y")
    @get:JsonProperty("agency_y")
    var mAgencyY: Double = 0.0,
    @field:JsonProperty("agency_Font")
    @get:JsonProperty("agency_Font")
    var mAgencyFont: Int = 0,
    @field:JsonProperty("agency_column_size")
    @get:JsonProperty("agency_column_size")
    var mAgencyColumnSize: Int = 0,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,
    @field:JsonProperty("shift_label")
    @get:JsonProperty("shift_label")
    var shiftLabel: String? = null,
    @field:JsonProperty("shift_column")
    @get:JsonProperty("shift_column")
    var shiftColumn: Int? = 1,
    @field:JsonProperty("print_order_shift")
    @get:JsonProperty("print_order_shift")
    var mPrintOrderShift: Double = 0.0,
    @field:JsonProperty("print_layout_order_shift")
    @get:JsonProperty("print_layout_order_shift")
    var mPrintLayoutOrderShift: String? = null,
    @field:JsonProperty("shift_x")
    @get:JsonProperty("shift_x")
    var mShiftX: Double = 0.0,
    @field:JsonProperty("shift_y")
    @get:JsonProperty("shift_y")
    var mShiftY: Double = 0.0,
    @field:JsonProperty("shift_Font")
    @get:JsonProperty("shift_Font")
    var mShiftFont: Int = 0,
    @field:JsonProperty("shift_column_size")
    @get:JsonProperty("shift_column_size")
    var mShiftColumnSize: Int = 0,
    @field:JsonProperty("form_layout_order")
    @get:JsonProperty("form_layout_order")
    var mFormLayoutOrder: String? = null,
    @field:JsonProperty("observationtime")
    @get:JsonProperty("observationtime")
    var observationTime: String? = null,
    @field:JsonProperty("observationtime_label")
    @get:JsonProperty("observationtime_label")
    var observationTimeLabel: String? = null,
    @field:JsonProperty("observationtime_column")
    @get:JsonProperty("observationtime_column")
    var observationTimeColumn: Int? = 1,
    @field:JsonProperty("status_observationtime")
    @get:JsonProperty("status_observationtime")
    var isStatus_observationtime: Boolean = false,
    @field:JsonProperty("print_order_observationtime")
    @get:JsonProperty("print_order_observationtime")
    var mPrintOrderObservationtime: Double = 0.0,
    @field:JsonProperty("print_layout_order_observationtime")
    @get:JsonProperty("print_layout_order_observationtime")
    var mPrintLayoutOrderObservationTime: String? = null,
    @field:JsonProperty("observation_time_x")
    @get:JsonProperty("observation_time_x")
    var mObservationTimeX: Double = 0.0,
    @field:JsonProperty("observation_time_y")
    @get:JsonProperty("observation_time_y")
    var mObservationTimeY: Double = 0.0,
    @field:JsonProperty("observation_time_font")
    @get:JsonProperty("observation_time_font")
    var mObservationTimeFont: Int = 0,
    @field:JsonProperty("observation_column_size")
    @get:JsonProperty("observation_column_size")
    var mObservationColumnSize: Int = 0
) : Parcelable
