package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataItemItem(
    @field:JsonProperty("expiry_timestamp")
    @get:JsonProperty("expiry_timestamp")
    var expiryTimestamp: String? = null,
    @field:JsonProperty("add_transaction")
    @get:JsonProperty("add_transaction")
    var isAddTransaction: Boolean = false,
    @field:JsonProperty("start_timestamp")
    @get:JsonProperty("start_timestamp")
    var startTimestamp: String? = null,
    @field:JsonProperty("latency")
    @get:JsonProperty("latency")
    var latency: Int = 0,
    @field:JsonProperty("receipt_id")
    @get:JsonProperty("receipt_id")
    var receiptId: String? = null,
    @field:JsonProperty("vendor_name")
    @get:JsonProperty("vendor_name")
    var vendorName: String? = null,
    @field:JsonProperty("transaction_timestamp")
    @get:JsonProperty("transaction_timestamp")
    var transactionTimestamp: String? = null,
    @field:JsonProperty("source")
    @get:JsonProperty("source")
    var source: String? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,
    @field:JsonProperty("amount_in_dollars")
    @get:JsonProperty("amount_in_dollars")
    var amountInDollars: Double = 0.0,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("lp_state")
    @get:JsonProperty("lp_state")
    var lpState: String? = null,
    @field:JsonProperty("vendor_id")
    @get:JsonProperty("vendor_id")
    var vendorId: String? = null,
    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,
    @field:JsonProperty("meter_id")
    @get:JsonProperty("meter_id")
    var meterId: String? = null,
    @field:JsonProperty("space_id")
    @get:JsonProperty("space_id")
    var spaceId: String? = null,
    @field:JsonProperty("user")
    @get:JsonProperty("user")
    var user: String? = null,
    @field:JsonProperty("recieved_timestamp")
    @get:JsonProperty("recieved_timestamp")
    var recievedTimestamp: String? = null
) : Parcelable
