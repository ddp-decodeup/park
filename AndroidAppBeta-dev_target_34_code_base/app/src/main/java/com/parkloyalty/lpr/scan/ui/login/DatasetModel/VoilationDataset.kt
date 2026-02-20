package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VoilationDataset(
    @field:JsonProperty("violation_code")
    @get:JsonProperty("violation_code")
    var violationCode: String? = null,

    @field:JsonProperty("violation_description")
    @get:JsonProperty("violation_description")
    var violationDescription: String? = null,

    @field:JsonProperty("violation_fine")
    @get:JsonProperty("violation_fine")
    var violationFine: String? = null,

    @field:JsonProperty("violation_late_fee")
    @get:JsonProperty("violation_late_fee")
    var violationLateFee: String? = null,

    @field:JsonProperty("violation_query_type")
    @get:JsonProperty("violation_query_type")
    var violationQueryType: String? = null,

    @field:JsonProperty("violation_reference_code")
    @get:JsonProperty("violation_reference_code")
    var violationReferenceCode: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
