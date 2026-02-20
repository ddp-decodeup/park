package com.parkloyalty.lpr.scan.ui.check_setup.model.timing

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
@Entity(tableName = "timing_data")
data class AddTimingDatabaseModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0,

    @ColumnInfo(name = "badge_id")
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,

    @ColumnInfo(name = "form_status")
    @field:JsonProperty("form_status")
    @get:JsonProperty("form_status")
    var formStatus: Int = 0,

    @ColumnInfo(name = "block")
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,

    @ColumnInfo(name = "latitude")
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,

    @ColumnInfo(name = "longitiude")
    @field:JsonProperty("longitiude")
    @get:JsonProperty("longitiude")
    var longitiude: Double? = null,

    @ColumnInfo(name = "lpr_number")
    @field:JsonProperty("lpr_number")
    @get:JsonProperty("lpr_number")
    var lprNumber: String? = null,

    @ColumnInfo(name = "lpr_state")
    @field:JsonProperty("lpr_state")
    @get:JsonProperty("lpr_state")
    var lprState: String? = null,

    @ColumnInfo(name = "mark_issue_timestamp")
    @field:JsonProperty("mark_issue_timestamp")
    @get:JsonProperty("mark_issue_timestamp")
    var markIssueTimestamp: String? = null,

    @ColumnInfo(name = "mark_start_timestamp")
    @field:JsonProperty("mark_start_timestamp")
    @get:JsonProperty("mark_start_timestamp")
    var markStartTimestamp: String? = null,

    @ColumnInfo(name = "meter_number")
    @field:JsonProperty("meter_number")
    @get:JsonProperty("meter_number")
    var meterNumber: String? = null,

    @ColumnInfo(name = "officer_name")
    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null,

    @ColumnInfo(name = "regulation_time")
    @field:JsonProperty("regulation_time")
    @get:JsonProperty("regulation_time")
    var regulationTime: Long? = null,

    @ColumnInfo(name = "remark")
    @field:JsonProperty("remark")
    @get:JsonProperty("remark")
    var remark: String? = null,

    @ColumnInfo(name = "shift")
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shift: String? = null,

    @ColumnInfo(name = "side")
    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,

    @ColumnInfo(name = "source")
    @field:JsonProperty("source")
    @get:JsonProperty("source")
    var source: String? = null,

    @ColumnInfo(name = "status")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var mStatus: String? = null,

    @ColumnInfo(name = "street")
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,

    @ColumnInfo(name = "supervisor")
    @field:JsonProperty("supervisor")
    @get:JsonProperty("supervisor")
    var supervisor: String? = null,

    @ColumnInfo(name = "zone")
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,

    @ColumnInfo(name = "location")
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var mLocation: String? = null,

    @ColumnInfo(name = "make")
    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var mMake: String? = null,

    @ColumnInfo(name = "model")
    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var mModel: String? = null,

    @ColumnInfo(name = "color")
    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var mColor: String? = null,

    @ColumnInfo(name = "address")
    @field:JsonProperty("address")
    @get:JsonProperty("address")
    var mAddress: String? = null
) : Parcelable
