package com.parkloyalty.lpr.scan.ui.reprint.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PaymentDetails(
    @field:JsonProperty("payment_mode")
    @get:JsonProperty("payment_mode")
    var paymentMode: String? = null,
    @field:JsonProperty("amount")
    @get:JsonProperty("amount")
    var amount: Int = 0,
    @field:JsonProperty("payment_datetime")
    @get:JsonProperty("payment_datetime")
    var paymentDatetime: String? = null,
    @field:JsonProperty("citation_id")
    @get:JsonProperty("citation_id")
    var citationId: String? = null,
    @field:JsonProperty("payment_status")
    @get:JsonProperty("payment_status")
    var paymentStatus: String? = null,
    @field:JsonProperty("receipt_id")
    @get:JsonProperty("receipt_id")
    var receiptId: String? = null
) : Parcelable
