package com.parkloyalty.lpr.scan.ui.boot.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BootRequest(
    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null,

    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetails? = null,

    @field:JsonProperty("citation_number")
    @get:JsonProperty("citation_number")
    var citationNumber: String? = null,

    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetails? = null,

    @field:JsonProperty("violation_details")
    @get:JsonProperty("violation_details")
    var violationDetails: ViolationDetails? = null,

    @field:JsonProperty("boot_tow_type")
    @get:JsonProperty("boot_tow_type")
    var bootTowType: String? = null,

    @field:JsonProperty("on_ground_dispatch")
    @get:JsonProperty("on_ground_dispatch")
    var isOnGroundDispatch: Boolean = false,

    @field:JsonProperty("dispatch_type")
    @get:JsonProperty("dispatch_type")
    var dispatchType: String? = null,

    @field:JsonProperty("boot_tow_reason")
    @get:JsonProperty("boot_tow_reason")
    var bootTowReason: String? = null,

    @field:JsonProperty("remarks")
    @get:JsonProperty("remarks")
    var remarks: String? = null,

    @field:JsonProperty("vehicle_details")
    @get:JsonProperty("vehicle_details")
    var vehicleDetails: VehicleDetails? = null
) : Parcelable
