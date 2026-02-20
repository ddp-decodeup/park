package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class PaymentData(
    @field:JsonProperty("last_modified")
    @get:JsonProperty("last_modified")
    var lastModified: String? = null,
    @field:JsonProperty("table_name")
    @get:JsonProperty("table_name")
    var tableName: String? = null,
    @field:JsonProperty("total_records")
    @get:JsonProperty("total_records")
    var totalRecords: Long? = null
) : Parcelable
