package com.parkloyalty.lpr.scan.ui.supervisor.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ActivityDetailsItem(
    @field:JsonProperty("110")
    @get:JsonProperty("110")
    var jsonMember110: JsonMember110? = null,

    @field:JsonProperty("912")
    @get:JsonProperty("912")
    var jsonMember912: JsonMember912? = null
) : Parcelable
