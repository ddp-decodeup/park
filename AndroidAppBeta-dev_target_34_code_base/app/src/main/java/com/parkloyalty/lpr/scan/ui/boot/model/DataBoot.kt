package com.parkloyalty.lpr.scan.ui.boot.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataBoot(
    @field:JsonProperty("dispatch_queue_id")
    @get:JsonProperty("dispatch_queue_id")
    var dispatchQueueId: String? = null,

    @field:JsonProperty("boot_and_tow_request_details")
    @get:JsonProperty("boot_and_tow_request_details")
    var bootAndTowRequestDetails: BootAndTowRequestDetails? = null
) : Parcelable
