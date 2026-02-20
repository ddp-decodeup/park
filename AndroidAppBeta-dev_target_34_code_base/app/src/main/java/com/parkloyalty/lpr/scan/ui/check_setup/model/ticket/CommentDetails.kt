package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CommentDetails(
    @field:JsonProperty("note_1")
    @get:JsonProperty("note_1")
    var note_1: String? = null,

    @field:JsonProperty("note_2")
    @get:JsonProperty("note_2")
    var note_2: String? = null,

    @field:JsonProperty("remark_1")
    @get:JsonProperty("remark_1")
    var remark_1: String? = null,

    @field:JsonProperty("remark_2")
    @get:JsonProperty("remark_2")
    var remark_2: String? = null
) : Parcelable
