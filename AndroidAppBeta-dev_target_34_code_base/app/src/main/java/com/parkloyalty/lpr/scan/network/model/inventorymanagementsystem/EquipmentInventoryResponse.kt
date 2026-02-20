package com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class EquipmentInventoryResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<DataInventoryEquipmentItem>? = null,

    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var isStatus: Boolean = false
) : Parcelable
