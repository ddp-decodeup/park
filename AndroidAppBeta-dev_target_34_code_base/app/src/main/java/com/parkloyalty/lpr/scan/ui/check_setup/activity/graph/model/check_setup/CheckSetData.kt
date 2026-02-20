package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CheckSetData(
    @field:JsonProperty("ExemptData")
    @get:JsonProperty("ExemptData")
    var exemptData: ExemptData? = null,
    @field:JsonProperty("PaymentData")
    @get:JsonProperty("PaymentData")
    var paymentData: PaymentData? = null,
    @field:JsonProperty("PermitData")
    @get:JsonProperty("PermitData")
    var permitData: PermitData? = null,
    @field:JsonProperty("ScofflawData")
    @get:JsonProperty("ScofflawData")
    var scofflawData: ScofflawData? = null,
    @field:JsonProperty("StolenData")
    @get:JsonProperty("StolenData")
    var stolenData: StolenData? = null,
    @field:JsonProperty("TimingData")
    @get:JsonProperty("TimingData")
    var timingData: TimingData? = null
) : Parcelable
