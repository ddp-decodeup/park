package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ScofflawDataResponse(
    @field:JsonProperty("int64_field_0")
    @get:JsonProperty("int64_field_0")
    var int64Field0: Long? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("reason")
    @get:JsonProperty("reason")
    var reason: String? = null,

    @field:JsonProperty("timestamp")
    @get:JsonProperty("timestamp")
    var timestamp: String? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null,

    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var vinNumber: String? = null,

    @field:JsonProperty("body_style")
    @get:JsonProperty("body_style")
    var bodyStyle: String? = null,

    @field:JsonProperty("decal_number")
    @get:JsonProperty("decal_number")
    var decalNumber: String? = null,

    @field:JsonProperty("decal_year")
    @get:JsonProperty("decal_year")
    var decalYear: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,

    @field:JsonProperty("is_violation")
    @get:JsonProperty("is_violation")
    var mIsViolation: Boolean? = null,

    @field:JsonProperty("alert_type")
    @get:JsonProperty("alert_type")
    var alertType: String? = null,

    @field:JsonProperty("dc_hit")
    @get:JsonProperty("dc_hit")
    var dcHit: String? = null,

    @field:JsonProperty("vendor_name")
    @get:JsonProperty("vendor_name")
    var vendorName: String? = null,

    @field:JsonProperty("dc_delta_seconds")
    @get:JsonProperty("dc_delta_seconds")
    var dcDeltaSeconds: Int = 0,

    @field:JsonProperty("citation_count")
    @get:JsonProperty("citation_count")
    var citationCount: String? = null,

    @field:JsonProperty("amount_due")
    @get:JsonProperty("amount_due")
    var amountDue: String? = null,

    @field:JsonProperty("received_timestamp")
    @get:JsonProperty("received_timestamp")
    var receivedTimestamp: String? = null,

    @field:JsonProperty("fine_amount")
    @get:JsonProperty("fine_amount")
    var fineAmount: Double? = 0.00
) : Parcelable
