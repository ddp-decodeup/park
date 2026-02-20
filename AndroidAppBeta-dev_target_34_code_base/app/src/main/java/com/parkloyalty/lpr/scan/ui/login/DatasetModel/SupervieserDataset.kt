package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class SupervieserDataset(
    @field:JsonProperty("first_name")
    @get:JsonProperty("first_name")
    var firstName: String? = null,

    @field:JsonProperty("last_name")
    @get:JsonProperty("last_name")
    var lastName: String? = null,

    @field:JsonProperty("_id")
    @get:JsonProperty("_id")
    var m_id: String? = null
) : Parcelable
