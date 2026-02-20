package com.parkloyalty.lpr.scan.ui.supervisor.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Data(
    @field:JsonProperty("supervisor_daily_summary")
    @get:JsonProperty("supervisor_daily_summary")
    var supervisorDailySummary: SupervisorDailySummary? = null
) : Parcelable
