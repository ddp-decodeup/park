package com.parkloyalty.lpr.scan.ui.check_setup.model.check_setup_model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CheckSetuptable(
    @field:JsonProperty("CitationData")
    @get:JsonProperty("CitationData")
    var citationData: CheckSetupDates? = null,

    @field:JsonProperty("ExemptData")
    @get:JsonProperty("ExemptData")
    var exemptData: CheckSetupDates? = null,

    @field:JsonProperty("PaymentData")
    @get:JsonProperty("PaymentData")
    var paymentData: CheckSetupDates? = null,

    @field:JsonProperty("PermitData")
    @get:JsonProperty("PermitData")
    var permitData: CheckSetupDates? = null,

    @field:JsonProperty("ScofflawData")
    @get:JsonProperty("ScofflawData")
    var scofflawData: CheckSetupDates? = null,

    @field:JsonProperty("TimingData")
    @get:JsonProperty("TimingData")
    var timingData: CheckSetupDates? = null,

    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: ServerMetadata? = null
) : Parcelable
