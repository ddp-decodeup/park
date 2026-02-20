package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CameraViolationFeedResponse(
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<DataItemCameraViolation?>? = null,

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
    var status: Boolean? = false
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MediaFilesItem(
    @field:JsonProperty("image")
    @get:JsonProperty("image")
    var image: String? = null,

    @field:JsonProperty("image_type")
    @get:JsonProperty("image_type")
    var imageType: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Vehicle(
    @field:JsonProperty("vehicle_make")
    @get:JsonProperty("vehicle_make")
    var vehicleMake: @RawValue Any? = null,

    @field:JsonProperty("vehicle_color")
    @get:JsonProperty("vehicle_color")
    var vehicleColor: @RawValue Any? = null,

    @field:JsonProperty("vehicle_plate")
    @get:JsonProperty("vehicle_plate")
    var vehiclePlate: String? = null,

    @field:JsonProperty("vehicle_year")
    @get:JsonProperty("vehicle_year")
    var vehicleYear: @RawValue Any? = null,

    @field:JsonProperty("vehicle_model")
    @get:JsonProperty("vehicle_model")
    var vehicleModel: String? = null,

    @field:JsonProperty("vehicle_body_type")
    @get:JsonProperty("vehicle_body_type")
    var vehicleBodyType: @RawValue Any? = null,

    @field:JsonProperty("vehicle_type")
    @get:JsonProperty("vehicle_type")
    var vehicleType: @RawValue Any? = null,

    @field:JsonProperty("vin")
    @get:JsonProperty("vin")
    var vin: @RawValue Any? = null,

    @field:JsonProperty("vehicle_state")
    @get:JsonProperty("vehicle_state")
    var vehicleState: @RawValue Any? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataItemCameraViolation(
    @field:JsonProperty("media_files")
    @get:JsonProperty("media_files")
    var mediaFiles: List<MediaFilesItem?>? = null,

    @field:JsonProperty("operator_id")
    @get:JsonProperty("operator_id")
    var operatorId: String? = null,

    @field:JsonProperty("space_number")
    @get:JsonProperty("space_number")
    var spaceNumber: String? = null,

    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Int? = null,

    @field:JsonProperty("received_timestamp_utc")
    @get:JsonProperty("received_timestamp_utc")
    var receivedTimestampUtc: @RawValue Any? = null,

    @field:JsonProperty("violation")
    @get:JsonProperty("violation")
    var violation: String? = null,

    @field:JsonProperty("vehicle")
    @get:JsonProperty("vehicle")
    var vehicle: Vehicle? = null,

    @field:JsonProperty("occurred_at")
    @get:JsonProperty("occurred_at")
    var occurredAt: String? = null,

    @field:JsonProperty("sensor_id")
    @get:JsonProperty("sensor_id")
    var sensorId: @RawValue Any? = null,

    @field:JsonProperty("zone_id")
    @get:JsonProperty("zone_id")
    var zoneId: String? = null,

    @field:JsonProperty("violation_id")
    @get:JsonProperty("violation_id")
    var violationId: String? = null,

    @field:JsonProperty("detection_type")
    @get:JsonProperty("detection_type")
    var detectionType: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var id: String? = null,

    @field:JsonProperty("detection_id")
    @get:JsonProperty("detection_id")
    var detectionId: @RawValue Any? = null,

    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: @RawValue Any? = null,

    @field:JsonProperty("received_timestamp")
    @get:JsonProperty("received_timestamp")
    var receivedTimestamp: @RawValue Any? = null,

    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Int? = null,

    @field:JsonProperty("violation_type")
    @get:JsonProperty("violation_type")
    var violationType: String? = null
) : Parcelable
