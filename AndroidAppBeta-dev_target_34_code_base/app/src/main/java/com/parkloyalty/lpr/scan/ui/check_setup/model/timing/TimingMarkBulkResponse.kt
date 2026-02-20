package com.parkloyalty.lpr.scan.ui.check_setup.model.timing

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TimingMarkBulkResponse(
    @field:JsonProperty("responseTimingMarkBulk")
    @get:JsonProperty("responseTimingMarkBulk")
    var responseTimingMarkBulk: List<ResponseTimingMarkBulkItem?>? = null,

    @field:JsonProperty("success")
    @get:JsonProperty("success")
    var success: Boolean? = null,

    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponseTimingMarkBulkItem(
    @field:JsonProperty("mark_id")
    @get:JsonProperty("mark_id")
    var markId: String? = null,

    @field:JsonProperty("error")
    @get:JsonProperty("error")
    var error: String? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable
