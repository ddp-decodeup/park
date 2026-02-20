package com.parkloyalty.lpr.scan.ui.boot.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BootMetadataRequest(
    @field:JsonProperty("boot_metadata")
    @get:JsonProperty("boot_metadata")
    var bootMetadata: BootInstanceTicketRequest? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class BootInstanceTicketRequest(
    @field:JsonProperty("boot_type")
    @get:JsonProperty("boot_type")
    var bootType: String? = null,
    @field:JsonProperty("deployment_id")
    @get:JsonProperty("deployment_id")
    var deploymentId: String? = null,
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var deviceId: String? = null,
    @field:JsonProperty("license_plate_number")
    @get:JsonProperty("license_plate_number")
    var licensePlateNumber: String? = null,
    @field:JsonProperty("operator_id")
    @get:JsonProperty("operator_id")
    var operatorId: String? = null,
    @field:JsonProperty("plate_type")
    @get:JsonProperty("plate_type")
    var plateType: String? = null,
    @field:JsonProperty("release_code")
    @get:JsonProperty("release_code")
    var releaseCode: String? = null,
    @field:JsonProperty("license_plate_state")
    @get:JsonProperty("license_plate_state")
    var licensePlateState: String? = null,
    @field:JsonProperty("notes")
    @get:JsonProperty("notes")
    var notes: String? = null,
    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: String? = null
) : Parcelable
