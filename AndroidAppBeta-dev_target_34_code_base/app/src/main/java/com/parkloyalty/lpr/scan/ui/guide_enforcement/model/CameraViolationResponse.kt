package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CameraViolationResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<DataItemCameraViolationFeed?>? = null,
    @field:JsonProperty("records")
    @get:JsonProperty("records")
    var records: Int? = null,
    @field:JsonProperty("total_records")
    @get:JsonProperty("total_records")
    var totalRecords: Int? = null,
    @field:JsonProperty("limit")
    @get:JsonProperty("limit")
    var limit: Int? = null,
    @field:JsonProperty("page")
    @get:JsonProperty("page")
    var page: Int? = null,
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataItemCameraViolationFeed(
    @field:JsonProperty("invoice_fee_structure")
    @get:JsonProperty("invoice_fee_structure")
    var invoiceFeeStructure: @RawValue Any? = null,
    @field:JsonProperty("payment_done")
    @get:JsonProperty("payment_done")
    var paymentDone: Boolean? = null,
    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: String? = null,
    @field:JsonProperty("in_plate_image")
    @get:JsonProperty("in_plate_image")
    var inPlateImage: String? = null,
    @field:JsonProperty("in_plate_image_timestamp")
    @get:JsonProperty("in_plate_image_timestamp")
    var inPlateImageTimestamp: String? = null,
    @field:JsonProperty("description")
    @get:JsonProperty("description")
    var description: String? = null,
    @field:JsonProperty("out_plate_image_timestamp")
    @get:JsonProperty("out_plate_image_timestamp")
    var outPlateImageTimestamp: @RawValue Any? = null,
    @field:JsonProperty("in_car_image_timestamp")
    @get:JsonProperty("in_car_image_timestamp")
    var inCarImageTimestamp: String? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,
    @field:JsonProperty("violation_number")
    @get:JsonProperty("violation_number")
    var violationNumber: String? = null,
    @field:JsonProperty("create_timestamp")
    @get:JsonProperty("create_timestamp")
    var createTimestamp: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: String? = null,
    @field:JsonProperty("payment_start_time")
    @get:JsonProperty("payment_start_time")
    var paymentStartTime: @RawValue Any? = null,
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,
    @field:JsonProperty("payment_source")
    @get:JsonProperty("payment_source")
    var paymentSource: String? = null,
    @field:JsonProperty("remote_images")
    @get:JsonProperty("remote_images")
    var remoteImages: @RawValue Any? = null,
    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,
    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,
    @field:JsonProperty("in_car_image")
    @get:JsonProperty("in_car_image")
    var inCarImage: String? = null,
    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null,
    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,
    @field:JsonProperty("pl_images")
    @get:JsonProperty("pl_images")
    var plImages: @RawValue Any? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("out_car_image_timestamp")
    @get:JsonProperty("out_car_image_timestamp")
    var outCarImageTimestamp: @RawValue Any? = null,
    @field:JsonProperty("out_car_image")
    @get:JsonProperty("out_car_image")
    var outCarImage: String? = null,
    @field:JsonProperty("duration_in_violation")
    @get:JsonProperty("duration_in_violation")
    var durationInViolation: Int? = null,
    @field:JsonProperty("out_plate_image")
    @get:JsonProperty("out_plate_image")
    var outPlateImage: String? = null,
    @field:JsonProperty("payment_end_time")
    @get:JsonProperty("payment_end_time")
    var paymentEndTime: @RawValue Any? = null,
    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,
    @field:JsonProperty("create_timestamp_utc")
    @get:JsonProperty("create_timestamp_utc")
    var createTimestampUtc: String? = null,
    @field:JsonProperty("space_number")
    @get:JsonProperty("space_number")
    var spaceNumber: String? = null,
    @field:JsonProperty("detection_id")
    @get:JsonProperty("detection_id")
    var detectionID: String? = null,
    @field:JsonProperty("violation_id")
    @get:JsonProperty("violation_id")
    var violationID: String? = null
) : Parcelable
