package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
@Entity(tableName = "welcome_form")
data class WelcomeForm(
    @ColumnInfo(name = "upload_status")
    @field:JsonProperty("upload_status")
    @get:JsonProperty("upload_status")
    var uploadStatus: Boolean? = null,

    @ColumnInfo(name = "enable")
    @field:JsonProperty("enable")
    @get:JsonProperty("enable")
    var enable: Boolean? = null,

    @ColumnInfo(name = "officer_badge_id")
    @field:JsonProperty("officer_badge_id")
    @get:JsonProperty("officer_badge_id")
    var officerBadgeId: String? = null,

    @ColumnInfo(name = "officer_shift")
    @field:JsonProperty("officer_shift")
    @get:JsonProperty("officer_shift")
    var officerShift: String? = null,

    @ColumnInfo(name = "officer_supervisor")
    @field:JsonProperty("officer_supervisor")
    @get:JsonProperty("officer_supervisor")
    var officerSupervisor: String? = null,

    @ColumnInfo(name = "officer_supervisor_badge_id")
    @field:JsonProperty("officer_supervisor_badge_id")
    @get:JsonProperty("officer_supervisor_badge_id")
    var mOfficerSuperVisorBadgeId: String? = null,

    @ColumnInfo(name = "officer_radio")
    @field:JsonProperty("officer_radio")
    @get:JsonProperty("officer_radio")
    var officerRadio: String? = null,

    @ColumnInfo(name = "officer_beat")
    @field:JsonProperty("officer_beat")
    @get:JsonProperty("officer_beat")
    var officerBeat: String? = null,

    @ColumnInfo(name = "officer_beat_name")
    @field:JsonProperty("officer_beat_name")
    @get:JsonProperty("officer_beat_name")
    var officerBeatName: String? = null,

    @ColumnInfo(name = "agency")
    @field:JsonProperty("agency")
    @get:JsonProperty("agency")
    var agency: String? = null,

    @ColumnInfo(name = "officer_lookup_code")
    @field:JsonProperty("officer_lookup_code")
    @get:JsonProperty("officer_lookup_code")
    var officer_lookup_code: String? = null,

    @ColumnInfo(name = "officer_zone")
    @field:JsonProperty("officer_zone")
    @get:JsonProperty("officer_zone")
    var officerZone: String? = null,

    @ColumnInfo(name = "officer_zone_name")
    @field:JsonProperty("officer_zone_name")
    @get:JsonProperty("officer_zone_name")
    var officerZoneName: String? = null,

    @ColumnInfo(name = "city_zone_name")
    @field:JsonProperty("city_zone_name")
    @get:JsonProperty("city_zone_name")
    var cityZoneName: String? = null,

    @ColumnInfo(name = "city_zone_name_code")
    @field:JsonProperty("city_zone_name_code")
    @get:JsonProperty("city_zone_name_code")
    var cityZoneNameCode: String? = null,

    @ColumnInfo(name = "payment_zone_name")
    @field:JsonProperty("payment_zone_name")
    @get:JsonProperty("payment_zone_name")
    var paymentZoneName: String? = null,

    @ColumnInfo(name = "payment_zone_ID")
    @field:JsonProperty("payment_zone_ID")
    @get:JsonProperty("payment_zone_ID")
    var paymentZoneID: String? = null,

    @ColumnInfo(name = "officer_first_name")
    @field:JsonProperty("officer_first_name")
    @get:JsonProperty("officer_first_name")
    var officerFirstName: String? = null,

    @ColumnInfo(name = "officer_last_name")
    @field:JsonProperty("officer_last_name")
    @get:JsonProperty("officer_last_name")
    var officerLastName: String? = null,

    @ColumnInfo(name = "officer_middle_name")
    @field:JsonProperty("officer_middle_name")
    @get:JsonProperty("officer_middle_name")
    var officerMiddleName: String? = null,

    @ColumnInfo(name = "officer_squad")
    @field:JsonProperty("officer_squad")
    @get:JsonProperty("officer_squad")
    var officerSquad: String? = null,

    @ColumnInfo(name = "officer_superviser_id")
    @field:JsonProperty("officer_superviser_id")
    @get:JsonProperty("officer_superviser_id")
    var officerSuperviserId: String? = null,

    @ColumnInfo(name = "officer_user_name")
    @field:JsonProperty("officer_user_name")
    @get:JsonProperty("officer_user_name")
    var officerUserName: String? = null,

    @ColumnInfo(name = "radio")
    @field:JsonProperty("radio")
    @get:JsonProperty("radio")
    var radio: String? = null,

    @ColumnInfo(name = "role")
    @field:JsonProperty("role")
    @get:JsonProperty("role")
    var role: String? = null,

    @ColumnInfo(name = "shift")
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,

    @ColumnInfo(name = "site_id")
    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @ColumnInfo(name = "site_officer_id")
    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null,

    @ColumnInfo(name = "officer_device_id")
    @field:JsonProperty("officer_device_id")
    @get:JsonProperty("officer_device_id")
    var officerDeviceId: String? = null,

    @ColumnInfo(name = "officer_device_name")
    @field:JsonProperty("officer_device_name")
    @get:JsonProperty("officer_device_name")
    var officerDeviceName: String? = null,

    @ColumnInfo(name = "equipment_id")
    @field:JsonProperty("equipment_id")
    @get:JsonProperty("equipment_id")
    var equipmentId: String? = null,

    @ColumnInfo(name = "squad_id")
    @field:JsonProperty("squad_id")
    @get:JsonProperty("squad_id")
    var squadId: String? = null,

    @ColumnInfo(name = "initiator_id")
    @field:JsonProperty("initiator_id")
    @get:JsonProperty("initiator_id")
    var initiatorId: String? = null,

    @ColumnInfo(name = "initiator_role")
    @field:JsonProperty("initiator_role")
    @get:JsonProperty("initiator_role")
    var initiatorRole: String? = null,

    @ColumnInfo(name = "lot")
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,

    @ColumnInfo(name = "_id")
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "table_id")
    @field:JsonProperty("_table_id")
    @get:JsonProperty("_table_id")
    var table_id: Int = 1
) : Parcelable
