package com.parkloyalty.lpr.scan.qrcode.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class InventoryRequest(
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetailsInventory? = null,

    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,

    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetailsInventory? = null,

    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: DetailsInventory? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsInventory(
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,

    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,

    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,

    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsInventory(
    @field:JsonProperty("inventory_value")
    @get:JsonProperty("inventory_value")
    var inventoryValue: String? = null,

    @field:JsonProperty("inventory_type")
    @get:JsonProperty("inventory_type")
    var inventoryType: String? = null,

    @field:JsonProperty("inventory_name")
    @get:JsonProperty("inventory_name")
    var inventoryName: String? = null,

    @field:JsonProperty("inventory_tag")
    @get:JsonProperty("inventory_tag")
    var inventoryTag: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsInventory(
    @field:JsonProperty("shift_id")
    @get:JsonProperty("shift_id")
    var shiftId: String? = null,

    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,

    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,

    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null
) : Parcelable
