package com.parkloyalty.lpr.scan.ui.brokenmeter.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Data(
    @field:JsonProperty("inserted_maintenance_data")
    @get:JsonProperty("inserted_maintenance_data")
    var insertedMaintenanceData: InsertedMaintenanceData? = null
) : Parcelable
