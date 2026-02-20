package com.parkloyalty.lpr.scan.ui.guide_enforcement.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ResponseItem(
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,
    @field:JsonProperty("transaction_id")
    @get:JsonProperty("transaction_id")
    var transactionId: String? = null,
    @field:JsonProperty("payment_start_timestamp")
    @get:JsonProperty("payment_start_timestamp")
    var paymentStartTimestamp: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("occupancy_expiry_timestamp")
    @get:JsonProperty("occupancy_expiry_timestamp")
    var occupancyExpiryTimestamp: String? = null,
    @field:JsonProperty("receipt_id")
    @get:JsonProperty("receipt_id")
    var receiptId: String? = null,
    @field:JsonProperty("occupancy_start_timestamp")
    @get:JsonProperty("occupancy_start_timestamp")
    var occupancyStartTimestamp: String? = null,
    @field:JsonProperty("space_id")
    @get:JsonProperty("space_id")
    var spaceId: String? = null,
    @field:JsonProperty("payment_expiry_timestamp")
    @get:JsonProperty("payment_expiry_timestamp")
    var paymentExpiryTimestamp: String? = null,
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: String? = null,
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null
) : Parcelable
