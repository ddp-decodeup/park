import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CameraGuidedEnforcementResponse(

	@field:JsonProperty("page")
	@get:JsonProperty("page")
	val page: Int? = null,

	@field:JsonProperty("limit")
	@get:JsonProperty("limit")
	val limit: Int? = null,

	@field:JsonProperty("total_records")
	@get:JsonProperty("total_records")
	val totalRecords: Int? = null,

	@field:JsonProperty("records")
	@get:JsonProperty("records")
	val records: Int? = null,

	@field:JsonProperty("data")
	@get:JsonProperty("data")
	val data: List<CameraGuidedItem>? = null

) : Parcelable


@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class CameraGuidedItem(

	@field:JsonProperty("_id")
	@get:JsonProperty("_id")
	val id: String? = null,

	@field:JsonProperty("operator_id")
	@get:JsonProperty("operator_id")
	val operatorId: String? = null,

	@field:JsonProperty("occurred_at")
	@get:JsonProperty("occurred_at")
	val occurredAt: String? = null,

	@field:JsonProperty("zone_id")
	@get:JsonProperty("zone_id")
	val zoneId: String? = null,

	@field:JsonProperty("space_number")
	@get:JsonProperty("space_number")
	val spaceNumber: String? = null,

	@field:JsonProperty("direction")
	@get:JsonProperty("direction")
	val direction: String? = null,

	@field:JsonProperty("status")
	@get:JsonProperty("status")
	val status: String? = null,

	@field:JsonProperty("detection_type")
	@get:JsonProperty("detection_type")
	val detectionType: String? = null,

	@field:JsonProperty("detection_id")
	@get:JsonProperty("detection_id")
	val detectionId: String? = null,

	@field:JsonProperty("sensor_id")
	@get:JsonProperty("sensor_id")
	val sensorId: String? = null,

	@field:JsonProperty("violation_id")
	@get:JsonProperty("violation_id")
	val violationId: String? = null,

	@field:JsonProperty("violation_type")
	@get:JsonProperty("violation_type")
	val violationType: String? = null,

	@field:JsonProperty("received_timestamp")
	@get:JsonProperty("received_timestamp")
	val receivedTimestamp: String? = null,

	@field:JsonProperty("received_timestamp_utc")
	@get:JsonProperty("received_timestamp_utc")
	val receivedTimestampUtc: String? = null,

	@field:JsonProperty("vehicle")
	@get:JsonProperty("vehicle")
	val vehicle: CameraVehicle? = null,

	@field:JsonProperty("media_files")
	@get:JsonProperty("media_files")
	val mediaFiles: List<CameraMedia>? = null

) : Parcelable


@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class CameraVehicle(

	@field:JsonProperty("vehicle_plate")
	@get:JsonProperty("vehicle_plate")
	val plate: String? = null,

	@field:JsonProperty("vehicle_state")
	@get:JsonProperty("vehicle_state")
	val state: String? = null,

	@field:JsonProperty("vehicle_type")
	@get:JsonProperty("vehicle_type")
	val type: String? = null,

	@field:JsonProperty("vehicle_color")
	@get:JsonProperty("vehicle_color")
	val color: String? = null,

	@field:JsonProperty("vehicle_make")
	@get:JsonProperty("vehicle_make")
	val make: String? = null,

	@field:JsonProperty("vehicle_model")
	@get:JsonProperty("vehicle_model")
	val model: String? = null,

	@field:JsonProperty("vehicle_year")
	@get:JsonProperty("vehicle_year")
	val year: String? = null,

	@field:JsonProperty("vehicle_body_type")
	@get:JsonProperty("vehicle_body_type")
	val bodyType: String? = null,

	@field:JsonProperty("vin")
	@get:JsonProperty("vin")
	val vin: String? = null

) : Parcelable


@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class CameraMedia(

	@field:JsonProperty("image_type")
	@get:JsonProperty("image_type")
	val imageType: String? = null,

	@field:JsonProperty("image")
	@get:JsonProperty("image")
	val image: String? = null

) : Parcelable
