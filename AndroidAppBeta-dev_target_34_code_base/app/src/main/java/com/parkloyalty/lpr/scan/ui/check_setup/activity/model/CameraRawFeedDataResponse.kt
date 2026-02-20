package com.parkloyalty.lpr.scan.ui.check_setup.activity.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CameraRawFeedDataResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<DataItemCameraRaw?>? = null,

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
data class Response(
    @field:JsonProperty("plates")
    @get:JsonProperty("plates")
    var plates: List<String?>? = null,

    @field:JsonProperty("length")
    @get:JsonProperty("length")
    var length: Int? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null,

    @field:JsonProperty("results")
    @get:JsonProperty("results")
    var results: List<ResultsItemCameraRaw>? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataItemCameraRaw(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: String? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: Response? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResultsItemCameraRaw(
    @field:JsonProperty("invoice_fee_structure")
    @get:JsonProperty("invoice_fee_structure")
    var invoiceFeeStructure: @RawValue Any? = null,

    @field:JsonProperty("payment_done")
    @get:JsonProperty("payment_done")
    var paymentDone: @RawValue Any? = null,

    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: @RawValue Any? = null,

    @field:JsonProperty("admin_fee")
    @get:JsonProperty("admin_fee")
    var adminFee: @RawValue Any? = null,

    @field:JsonProperty("in_plate_image")
    @get:JsonProperty("in_plate_image")
    var inPlateImage: String? = null,

    @field:JsonProperty("in_plate_image_timestamp")
    @get:JsonProperty("in_plate_image_timestamp")
    var inPlateImageTimestamp: String? = null,

    @field:JsonProperty("description")
    @get:JsonProperty("description")
    var description: @RawValue Any? = null,

    @field:JsonProperty("out_plate_image_timestamp")
    @get:JsonProperty("out_plate_image_timestamp")
    var outPlateImageTimestamp: @RawValue Any? = null,

    @field:JsonProperty("parking_fee")
    @get:JsonProperty("parking_fee")
    var parkingFee: @RawValue Any? = null,

    @field:JsonProperty("in_car_image_timestamp")
    @get:JsonProperty("in_car_image_timestamp")
    var inCarImageTimestamp: String? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: @RawValue Any? = null,

    @field:JsonProperty("violation_number")
    @get:JsonProperty("violation_number")
    var violationNumber: @RawValue Any? = null,

    @field:JsonProperty("create_timestamp")
    @get:JsonProperty("create_timestamp")
    var createTimestamp: String? = null,

    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,

    @field:JsonProperty("ticket_no")
    @get:JsonProperty("ticket_no")
    var ticketNo: @RawValue Any? = null,

    @field:JsonProperty("payment_start_time")
    @get:JsonProperty("payment_start_time")
    var paymentStartTime: @RawValue Any? = null,

    @field:JsonProperty("total_fee")
    @get:JsonProperty("total_fee")
    var totalFee: @RawValue Any? = null,

    @field:JsonProperty("processed_at")
    @get:JsonProperty("processed_at")
    var processedAt: @RawValue Any? = null,

    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: @RawValue Any? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: @RawValue Any? = null,

    @field:JsonProperty("payment_source")
    @get:JsonProperty("payment_source")
    var paymentSource: @RawValue Any? = null,

    @field:JsonProperty("remote_images")
    @get:JsonProperty("remote_images")
    var remoteImages: @RawValue Any? = null,

    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: @RawValue Any? = null,

    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: @RawValue Any? = null,

    @field:JsonProperty("body_style")
    @get:JsonProperty("body_style")
    var bodyStyle: @RawValue Any? = null,

    @field:JsonProperty("invoice_number")
    @get:JsonProperty("invoice_number")
    var invoiceNumber: @RawValue Any? = null,

    @field:JsonProperty("in_car_image")
    @get:JsonProperty("in_car_image")
    var inCarImage: String? = null,

    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: @RawValue Any? = null,

    @field:JsonProperty("is_processed")
    @get:JsonProperty("is_processed")
    var isProcessed: @RawValue Any? = null,

    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: @RawValue Any? = null,

    @field:JsonProperty("pl_images")
    @get:JsonProperty("pl_images")
    var plImages: @RawValue Any? = null,

    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: @RawValue Any? = null,

    @field:JsonProperty("out_car_image_timestamp")
    @get:JsonProperty("out_car_image_timestamp")
    var outCarImageTimestamp: String? = null,

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

    @field:JsonProperty("create_timestamp_utc")
    @get:JsonProperty("create_timestamp_utc")
    var createTimestampUtc: String? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null
) : Parcelable
