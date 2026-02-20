package com.parkloyalty.lpr.scan.ui.check_setup.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PaymentDataResponse(
    @field:JsonProperty("amount")
    @get:JsonProperty("amount")
    var amount: Long? = null,

    @field:JsonProperty("end_timestamp")
    @get:JsonProperty("end_timestamp")
    var endTimestamp: String? = null,

    @field:JsonProperty("int64_field_0")
    @get:JsonProperty("int64_field_0")
    var int64Field0: Long? = null,

    @field:JsonProperty("latency")
    @get:JsonProperty("latency")
    var latency: Double? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("meter_id")
    @get:JsonProperty("meter_id")
    var meterId: String? = null,

    @field:JsonProperty("reciept_id")
    @get:JsonProperty("reciept_id")
    var recieptId: String? = null,

    @field:JsonProperty("recieved_timestamp")
    @get:JsonProperty("recieved_timestamp")
    var recievedTimestamp: String? = null,

    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,

    @field:JsonProperty("start_timestamp")
    @get:JsonProperty("start_timestamp")
    var startTimestamp: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,

    @field:JsonProperty("time_in_seconds")
    @get:JsonProperty("time_in_seconds")
    var timeInSeconds: Double? = null,

    @field:JsonProperty("transaction_timestamp")
    @get:JsonProperty("transaction_timestamp")
    var transactionTimestamp: String? = null,

    @field:JsonProperty("vendor_id")
    @get:JsonProperty("vendor_id")
    var vendorId: String? = null,

    @field:JsonProperty("zone_id")
    @get:JsonProperty("zone_id")
    var zoneId: String? = null,

    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var mZoneName: String? = null,

    @field:JsonProperty("expiry_timestamp")
    @get:JsonProperty("expiry_timestamp")
    var mExpiryTimeStamp: String? = null,

    @field:JsonProperty("received_timestamp")
    @get:JsonProperty("received_timestamp")
    var mReceivedTimestamp: String? = null
) : Parcelable
