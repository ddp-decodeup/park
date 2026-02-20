package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class InvoiceFeeStructure(
    @field:JsonProperty("parking_fee")
    @get:JsonProperty("parking_fee")
    var mParkingFee: Double? = null,
    @field:JsonProperty("citation_fee")
    @get:JsonProperty("citation_fee")
    var mCitationFee: Double? = null,
    @field:JsonProperty("sale_tax")
    @get:JsonProperty("sale_tax")
    var mSaleTax: Double? = null
) : Parcelable
