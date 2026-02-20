package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class AddNoteRequest(
    @field:JsonProperty("note_1")
    @get:JsonProperty("note_1")
    var mNote1: String? = null,
    @field:JsonProperty("note_2")
    @get:JsonProperty("note_2")
    var mNote2: String? = null,
    @field:JsonProperty("note_3")
    @get:JsonProperty("note_3")
    var mNote3: String? = null
) : Parcelable
