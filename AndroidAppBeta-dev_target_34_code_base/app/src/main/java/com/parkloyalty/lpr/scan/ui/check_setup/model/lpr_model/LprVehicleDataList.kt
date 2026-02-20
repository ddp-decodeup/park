package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model


import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import com.parkloyalty.lpr.scan.ui.check_setup.model.PaymentDataResponse




@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LprVehicleDataList(
    @field:JsonProperty("CitationData")
    @get:JsonProperty("CitationData")
    var citationData: List<CitationDataResponse>? = null,

    @field:JsonProperty("ExemptData")
    @get:JsonProperty("ExemptData")
    var exemptData: List<ExemptDataResponse>? = null,

    @field:JsonProperty("PaymentData")
    @get:JsonProperty("PaymentData")
    var paymentData: List<PaymentDataResponse>? = null,

    @field:JsonProperty("PermitData")
    @get:JsonProperty("PermitData")
    var permitData: List<PermitDataResponse>? = null,

    @field:JsonProperty("ScofflawData")
    @get:JsonProperty("ScofflawData")
    var scofflawData: List<ScofflawDataResponse>? = null,

    @field:JsonProperty("TimingData")
    @get:JsonProperty("TimingData")
    var timingData: List<TimingDataResponse>? = null,

    @field:JsonProperty("MakeModelColorData")
    @get:JsonProperty("MakeModelColorData")
    var mMakeModelColorData: List<MakeModelColorData>? = null
) : Parcelable
