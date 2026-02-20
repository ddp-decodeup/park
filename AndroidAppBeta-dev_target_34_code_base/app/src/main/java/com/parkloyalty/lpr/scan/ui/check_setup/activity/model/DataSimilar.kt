package com.parkloyalty.lpr.scan.ui.check_setup.activity.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DataSimilar(
    @field:JsonProperty("similarity_check")
    @get:JsonProperty("similarity_check")
    var similarityCheck: SimilarityCheck? = null
) : Parcelable
