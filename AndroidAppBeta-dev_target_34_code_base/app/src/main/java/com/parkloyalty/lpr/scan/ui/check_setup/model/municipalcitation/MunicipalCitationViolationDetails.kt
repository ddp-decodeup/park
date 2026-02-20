package com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MunicipalCitationViolationDetails(
    @field:JsonProperty("violation")
    @get:JsonProperty("violation")
    var violation: String? = null,
    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var code: String? = null,
    @field:JsonProperty("description")
    @get:JsonProperty("description")
    var description: String? = null,
    @field:JsonProperty("fine")
    @get:JsonProperty("fine")
    var fine: Double? = null,
    @field:JsonProperty("late_fine")
    @get:JsonProperty("late_fine")
    var late_fine: Double? = null,
    @field:JsonProperty("due_15_days")
    @get:JsonProperty("due_15_days")
    var due_15_days: Double? = null,
    @field:JsonProperty("due_30_days")
    @get:JsonProperty("due_30_days")
    var due_30_days: Double? = null,
    @field:JsonProperty("due_45_days")
    @get:JsonProperty("due_45_days")
    var due_45_days: Double? = null,
    @field:JsonProperty("export_code")
    @get:JsonProperty("export_code")
    var export_code: String? = null,
    @field:JsonProperty("cost")
    @get:JsonProperty("cost")
    var mCost: Double? = null,
    @field:JsonProperty("invoice_fee_structure")
    @get:JsonProperty("invoice_fee_structure")
    var invoiceFeeStructure: Double? = null
) : Parcelable
