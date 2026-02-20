package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponseItem(
    @field:JsonProperty("equipment_name")
    @get:JsonProperty("equipment_name")
    var equipmentName: String? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,
    @field:JsonProperty("equipment_type")
    @get:JsonProperty("equipment_type")
    var equipmentType: String? = null,
    @field:JsonProperty("equipment_id")
    @get:JsonProperty("equipment_id")
    var equipmentId: String? = null
) : Parcelable
