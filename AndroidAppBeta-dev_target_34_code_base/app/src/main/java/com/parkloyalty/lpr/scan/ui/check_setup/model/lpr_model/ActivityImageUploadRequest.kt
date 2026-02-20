package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ActivityImageUploadRequest(
    @field:JsonProperty("update_payload")
    @get:JsonProperty("update_payload")
    var updatePayload: UpdatePayload? = null,

    @field:JsonProperty("activity_id")
    @get:JsonProperty("activity_id")
    var activityId: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UpdatePayload(
    @field:JsonProperty("image_1")
    @get:JsonProperty("image_1")
    var image1: String? = "",

    @field:JsonProperty("image_2")
    @get:JsonProperty("image_2")
    var image2: String? = "",

    @field:JsonProperty("image_3")
    @get:JsonProperty("image_3")
    var image3: String? = ""
) : Parcelable
