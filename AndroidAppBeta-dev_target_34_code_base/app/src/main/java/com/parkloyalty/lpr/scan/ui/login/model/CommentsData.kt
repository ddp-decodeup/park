package com.parkloyalty.lpr.scan.ui.login.model


import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.Metadata




@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CommentsData(
    @field:JsonProperty("metadata")
    @get:JsonProperty("metadata")
    var metadata: Metadata? = null,

    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: List<CommentState>? = null,

    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: Boolean? = null
) : Parcelable
