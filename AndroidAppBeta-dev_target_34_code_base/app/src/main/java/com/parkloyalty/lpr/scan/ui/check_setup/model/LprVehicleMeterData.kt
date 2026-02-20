package com.parkloyalty.lpr.scan.ui.check_setup.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LprVehicleMeterData(
    @field:JsonProperty("amount_in_cents")
    @get:JsonProperty("amount_in_cents")
    var amountInCents: Long? = null,

    @field:JsonProperty("expiry_datetime")
    @get:JsonProperty("expiry_datetime")
    var expiryDatetime: String? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("reciept_id")
    @get:JsonProperty("reciept_id")
    var recieptId: Long? = null,

    @field:JsonProperty("recieved_datetime")
    @get:JsonProperty("recieved_datetime")
    var recievedDatetime: String? = null,

    @field:JsonProperty("start_datetime")
    @get:JsonProperty("start_datetime")
    var startDatetime: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,

    @field:JsonProperty("time_in_seconds")
    @get:JsonProperty("time_in_seconds")
    var timeInSeconds: Long? = null,

    @field:JsonProperty("trans_datetime")
    @get:JsonProperty("trans_datetime")
    var transDatetime: String? = null,

    @field:JsonProperty("zone_id")
    @get:JsonProperty("zone_id")
    var zoneId: Long? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
