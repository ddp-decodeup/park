package com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MunicipalCitationLayoutData(
    @field:JsonProperty("component")
    @get:JsonProperty("component")
    var component: String? = null,

    @field:JsonProperty("fields")
    @get:JsonProperty("fields")
    var fields: List<MunicipalCitationLayoutField>? = null,

    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: String? = null
) : Parcelable
