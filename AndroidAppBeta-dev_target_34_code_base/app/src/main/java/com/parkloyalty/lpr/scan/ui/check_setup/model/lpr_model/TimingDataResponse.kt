package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TimingDataResponse(
    @field:JsonProperty("arrival_status")
    @get:JsonProperty("arrival_status")
    var arrivalStatus: String? = null,

    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,

    @field:JsonProperty("lat")
    @get:JsonProperty("lat")
    var lat: String? = null,

    @field:JsonProperty("lng")
    @get:JsonProperty("lng")
    var lng: String? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("meter_number")
    @get:JsonProperty("meter_number")
    var meterNumber: String? = null,

    @field:JsonProperty("record_id")
    @get:JsonProperty("record_id")
    var recordId: String? = null,

    @field:JsonProperty("regulation_time")
    @get:JsonProperty("regulation_time")
    var regulation: String? = null,

    @field:JsonProperty("remark")
    @get:JsonProperty("remark")
    var remark: String? = null,

    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,

    @field:JsonProperty("source")
    @get:JsonProperty("source")
    var source: String? = null,

    @field:JsonProperty("start_time")
    @get:JsonProperty("start_time")
    var startTime: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,

    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,

    @field:JsonProperty("timing_type")
    @get:JsonProperty("timing_type")
    var timingType: String? = null,

    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null
) : Parcelable
