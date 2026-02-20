package com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem


import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.parkloyalty.lpr.scan.ui.login.model.Metadata

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataInventoryEquipmentItem(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: Metadata? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: List<EquipmentItemDetail>? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var isStatus: Boolean = false
) : Parcelable
