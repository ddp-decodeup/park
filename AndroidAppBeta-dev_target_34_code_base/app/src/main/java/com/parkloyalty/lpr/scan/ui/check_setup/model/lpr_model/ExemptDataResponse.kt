package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ExemptDataResponse(
    @field:JsonProperty("exempt_code")
    @get:JsonProperty("exempt_code")
    var exemptCode: String? = null,

    @field:JsonProperty("exempt_type")
    @get:JsonProperty("exempt_type")
    var exemptType: String? = null,

    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,

    @field:JsonProperty("reason")
    @get:JsonProperty("reason")
    var reason: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,

    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null,

    @field:JsonProperty("latency")
    @get:JsonProperty("latency")
    var mLatency: String? = null,

    @field:JsonProperty("received_timestamp")
    @get:JsonProperty("received_timestamp")
    var mReceivedTimestamp: String? = null,

    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var mSiteId: String? = null,

    @field:JsonProperty("vendor_id")
    @get:JsonProperty("vendor_id")
    var mVendorId: String? = null,

    @field:JsonProperty("vendor_name")
    @get:JsonProperty("vendor_name")
    var mVendorName: String? = null,

    @field:JsonProperty("reported_timestamp")
    @get:JsonProperty("reported_timestamp")
    var mReportedTimestamp: String? = null
) : Parcelable
