package com.parkloyalty.lpr.scan.ui.boot.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BootAndTowRequestDetails(
    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null,

    @field:JsonProperty("server_timestamp")
    @get:JsonProperty("server_timestamp")
    var serverTimestamp: String? = null,

    @field:JsonProperty("ticket_image")
    @get:JsonProperty("ticket_image")
    var ticketImage: @RawValue Any? = null,

    @field:JsonProperty("dispatch_failure_reason")
    @get:JsonProperty("dispatch_failure_reason")
    var dispatchFailureReason: String? = null,

    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetailsBoot: LocationDetailsBoot? = null,

    @field:JsonProperty("boot_tow_type")
    @get:JsonProperty("boot_tow_type")
    var bootTowType: String? = null,

    @field:JsonProperty("vendor_name")
    @get:JsonProperty("vendor_name")
    var vendorName: String? = null,

    @field:JsonProperty("dispatch_type")
    @get:JsonProperty("dispatch_type")
    var dispatchType: String? = null,

    @field:JsonProperty("violation_details")
    @get:JsonProperty("violation_details")
    var violationDetailsBoot: ViolationDetailsBoot? = null,

    @field:JsonProperty("dispatch_request_status")
    @get:JsonProperty("dispatch_request_status")
    var dispatchRequestStatus: String? = null,

    @field:JsonProperty("client_timestamp_utc")
    @get:JsonProperty("client_timestamp_utc")
    var clientTimestampUtc: String? = null,

    @field:JsonProperty("cancel_reason")
    @get:JsonProperty("cancel_reason")
    var cancelReason: String? = null,

    @field:JsonProperty("vehicle_details")
    @get:JsonProperty("vehicle_details")
    var vehicleDetailsBoot: VehicleDetailsBoot? = null,

    @field:JsonProperty("cancel_status")
    @get:JsonProperty("cancel_status")
    var cancelStatus: String? = null,

    @field:JsonProperty("citation_number")
    @get:JsonProperty("citation_number")
    var citationNumber: String? = null,

    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetailsBoot: OfficerDetailsBoot? = null,

    @field:JsonProperty("vendor_id")
    @get:JsonProperty("vendor_id")
    var vendorId: String? = null,

    @field:JsonProperty("on_ground_status")
    @get:JsonProperty("on_ground_status")
    var onGroundStatus: OnGroundStatus? = null,

    @field:JsonProperty("cancel_failure_reason")
    @get:JsonProperty("cancel_failure_reason")
    var cancelFailureReason: String? = null,

    @field:JsonProperty("cancelled")
    @get:JsonProperty("cancelled")
    var isCancelled: Boolean = false,

    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null,

    @field:JsonProperty("boot_tow_reason")
    @get:JsonProperty("boot_tow_reason")
    var bootTowReason: String? = null,

    @field:JsonProperty("remarks")
    @get:JsonProperty("remarks")
    var remarks: String? = null
) : Parcelable
