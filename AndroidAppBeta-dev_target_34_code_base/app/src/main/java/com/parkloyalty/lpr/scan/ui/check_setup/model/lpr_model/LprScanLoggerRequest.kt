package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LprScanLoggerRequest(
    @field:JsonProperty("activity_type")
    @get:JsonProperty("activity_type")
    var activityType: String? = null,
    @field:JsonProperty("lp_number")
    @get:JsonProperty("lp_number")
    var lpNumber: String? = null,
    @field:JsonProperty("log_type")
    @get:JsonProperty("log_type")
    var logType: String? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double? = null,
    @field:JsonProperty("client_timestamp")
    @get:JsonProperty("client_timestamp")
    var clientTimestamp: String? = null,
    @field:JsonProperty("site_officer_name")
    @get:JsonProperty("site_officer_name")
    var siteOfficerName: String? = null,
    @field:JsonProperty("supervisor_name")
    @get:JsonProperty("supervisor_name")
    var supervisorName: String? = null,
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("ScofflawData")
    @get:JsonProperty("ScofflawData")
    var scofflawData: Long? = null,
    @field:JsonProperty("PermitData")
    @get:JsonProperty("PermitData")
    var permitData: Long? = null,
    @field:JsonProperty("PermitDataFuzzy")
    @get:JsonProperty("PermitDataFuzzy")
    var permitDataFuzzy: Long? = null,
    @field:JsonProperty("CitationData")
    @get:JsonProperty("CitationData")
    var citationData: Long? = null,
    @field:JsonProperty("TimingData")
    @get:JsonProperty("TimingData")
    var timingData: Long? = null,
    @field:JsonProperty("ExemptData")
    @get:JsonProperty("ExemptData")
    var exemptData: Long? = null,
    @field:JsonProperty("StolenData")
    @get:JsonProperty("StolenData")
    var stolenData: Long? = null,
    @field:JsonProperty("PaymentData")
    @get:JsonProperty("PaymentData")
    var paymentData: Long? = null,
    @field:JsonProperty("PaymentDataFuzzy")
    @get:JsonProperty("PaymentDataFuzzy")
    var paymentDataFuzzy: Long? = null,
    @field:JsonProperty("MakeModelColorData")
    @get:JsonProperty("MakeModelColorData")
    var makeModelColorData: Long? = null,
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var mShift: String? = null,
    @field:JsonProperty("site_officer_name ")
    @get:JsonProperty("site_officer_name ")
    var mSiteOfficerName: String? = null,
    @field:JsonProperty("agency")
    @get:JsonProperty("agency")
    var mAgency: String? = null
) : Parcelable
