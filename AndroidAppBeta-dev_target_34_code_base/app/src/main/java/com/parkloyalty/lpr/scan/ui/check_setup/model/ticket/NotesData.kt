package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class NotesData(
    @field:JsonProperty("note")
    @get:JsonProperty("note")
    var note: String? = null,

    @field:JsonProperty("created_at")
    @get:JsonProperty("created_at")
    var createdAt: String? = null
) : Parcelable
