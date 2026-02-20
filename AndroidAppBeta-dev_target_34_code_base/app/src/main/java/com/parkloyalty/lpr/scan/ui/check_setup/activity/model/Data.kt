package com.parkloyalty.lpr.scan.ui.check_setup.activity.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Data(
    @field:JsonProperty("last_second_check")
    @get:JsonProperty("last_second_check")
    var lastSecondCheck: LastSecondCheck? = null
) : Parcelable
