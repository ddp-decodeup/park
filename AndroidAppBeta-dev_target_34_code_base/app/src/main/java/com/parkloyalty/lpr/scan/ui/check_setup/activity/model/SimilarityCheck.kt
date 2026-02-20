package com.parkloyalty.lpr.scan.ui.check_setup.activity.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SimilarityCheck(
    @field:JsonProperty("message")
    @get:JsonProperty("message")
    var message: String? = null,
    @field:JsonProperty("similarity_check_status")
    @get:JsonProperty("similarity_check_status")
    var isSimilarityCheckStatus: Boolean = false
) : Parcelable
