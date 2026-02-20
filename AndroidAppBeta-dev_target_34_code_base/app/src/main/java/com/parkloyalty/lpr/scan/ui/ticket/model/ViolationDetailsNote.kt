package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ViolationDetailsNote(
    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var code: String? = null,
    @field:JsonProperty("late_fine")
    @get:JsonProperty("late_fine")
    var lateFine: Float = 0.0f,
    @field:JsonProperty("fine")
    @get:JsonProperty("fine")
    var fine: Float = 0.0f,
    @field:JsonProperty("due_15_days")
    @get:JsonProperty("due_15_days")
    var due15Days: Float = 0.0f,
    @field:JsonProperty("due_30_days")
    @get:JsonProperty("due_30_days")
    var due30Days: Float = 0.0f,
    @field:JsonProperty("description")
    @get:JsonProperty("description")
    var description: String? = null,
    @field:JsonProperty("due_45_days")
    @get:JsonProperty("due_45_days")
    var due45Days: Float = 0.0f
) : Parcelable
