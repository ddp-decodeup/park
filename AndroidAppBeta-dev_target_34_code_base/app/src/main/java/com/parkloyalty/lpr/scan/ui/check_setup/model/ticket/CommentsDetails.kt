package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CommentsDetails(
    @field:JsonProperty("note_1")
    @get:JsonProperty("note_1")
    var note1: String? = null,
    @field:JsonProperty("note_2")
    @get:JsonProperty("note_2")
    var note2: String? = null,
    @field:JsonProperty("note_3")
    @get:JsonProperty("note_3")
    var note3: String? = null,
    @field:JsonProperty("remark_1")
    @get:JsonProperty("remark_1")
    var remark1: String? = null,
    @field:JsonProperty("remark_2")
    @get:JsonProperty("remark_2")
    var remark2: String? = null
) : Parcelable
