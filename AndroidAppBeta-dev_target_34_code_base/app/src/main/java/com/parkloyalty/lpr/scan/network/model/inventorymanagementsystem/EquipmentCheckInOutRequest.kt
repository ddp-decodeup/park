package com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class EquipmentCheckInOutRequest(
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var equipmentID: String? = null
) : Parcelable
