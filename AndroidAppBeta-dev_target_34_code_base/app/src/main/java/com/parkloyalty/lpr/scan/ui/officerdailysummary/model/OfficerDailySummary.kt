package com.parkloyalty.lpr.scan.ui.officerdailysummary.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDailySummary(
    @field:JsonProperty("timeseries_info")
    @get:JsonProperty("timeseries_info")
    var timeseriesInfo: TimeseriesInfo? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetails? = null,
    @field:JsonProperty("shifts")
    @get:JsonProperty("shifts")
    var shifts: List<ShiftsItem?>? = null
) : Parcelable
