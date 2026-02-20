package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerListModel(
    @field:JsonProperty("off_name_first")
    @get:JsonProperty("off_name_first")
    var offNameFirst: String? = null,

    @field:JsonProperty("off_type_first")
    @get:JsonProperty("off_type_first")
    var offTypeFirst: String? = null,

    @field:JsonProperty("print_order")
    @get:JsonProperty("print_order")
    var mPrintOrder: Double = 0.0
) : Parcelable
