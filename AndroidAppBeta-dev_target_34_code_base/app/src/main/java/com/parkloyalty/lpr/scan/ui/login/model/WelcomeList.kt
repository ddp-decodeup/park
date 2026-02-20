package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentItemDetail
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class WelcomeList(
    @field:JsonProperty("activity")
    @get:JsonProperty("activity")
    var activityStats: List<ActivityStat>? = null,
    @field:JsonProperty("comments")
    @get:JsonProperty("comments")
    var commentStates: List<CommentState>? = null,
    @field:JsonProperty("supervisor")
    @get:JsonProperty("supervisor")
    var supervisorStats: List<SupervisorStat>? = null,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beatStats: List<BeatStat>? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zoneStats: List<ZoneStat>? = null,
    @field:JsonProperty("cityzone")
    @get:JsonProperty("cityzone")
    var pbcZoneStats: List<ZoneStat>? = null,
    @field:JsonProperty("radio")
    @get:JsonProperty("radio")
    var radioStats: List<RadioSt>? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var shiftStats: List<ShiftStat>? = null,
    @field:JsonProperty("agency")
    @get:JsonProperty("agency")
    var agencyStats: List<DatasetResponse>? = null,
    @field:JsonProperty("device")
    @get:JsonProperty("device")
    var deviceStats: List<DeviceResponseItem>? = null,
    @field:JsonProperty("DeviceLicense")
    @get:JsonProperty("DeviceLicense")
    var deviceLicenseStats: List<DataDeviceLicenseItem>? = null,
    @field:JsonProperty("equipment")
    @get:JsonProperty("equipment")
    var equipmentStates: List<ResponseItem>? = null,
    @field:JsonProperty("squad")
    @get:JsonProperty("squad")
    var squadStates: List<ResponseSquadItem>? = null
) : Parcelable
