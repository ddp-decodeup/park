package com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class EquipmentItemDetail(
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,

    @field:JsonProperty("equipment_name")
    @get:JsonProperty("equipment_name")
    var equipmentName: String? = null,

    @field:JsonProperty("equipment_value")
    @get:JsonProperty("equipment_value")
    var equipmentValue: String? = null,

    @field:JsonProperty("is_required")
    @get:JsonProperty("is_required")
    var isRequired: Boolean? = null,

    @field:JsonProperty("is_checked_out")
    @get:JsonProperty("is_checked_out")
    var isCheckedOut: Boolean? = null,

    @field:JsonProperty("last_checked_out")
    @get:JsonProperty("last_checked_out")
    var lastCheckedOut: String? = null,

    @field:JsonProperty("scan_status")
    @get:JsonProperty("scan_status")
    var scanStatus: Int? = null
) : Parcelable
