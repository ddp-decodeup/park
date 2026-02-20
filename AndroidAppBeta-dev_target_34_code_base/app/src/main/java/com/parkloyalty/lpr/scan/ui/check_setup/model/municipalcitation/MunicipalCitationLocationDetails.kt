package com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MunicipalCitationLocationDetails(
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("meter")
    @get:JsonProperty("meter")
    var meter: String? = null,
    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,
    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null,
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("street_lookup_code")
    @get:JsonProperty("street_lookup_code")
    var street_lookup_code: String? = null,
    @field:JsonProperty("space_id")
    @get:JsonProperty("space_id")
    var mSpaceId: String? = null,
    @field:JsonProperty("impound_code")
    @get:JsonProperty("impound_code")
    var mImpoundCode: String? = null
) : Parcelable
