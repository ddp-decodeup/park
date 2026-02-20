package com.parkloyalty.lpr.scan.ui.check_setup.model.ticket

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MotoristDetailsModel(
    @field:JsonProperty("motorist_first_name")
    @get:JsonProperty("motorist_first_name")
    var motoristFirstName: String? = null,

    @field:JsonProperty("motorist_middle_name")
    @get:JsonProperty("motorist_middle_name")
    var motoristMiddleName: String? = null,

    @field:JsonProperty("motorist_last_name")
    @get:JsonProperty("motorist_last_name")
    var motoristLastName: String? = null,

    @field:JsonProperty("motorist_date_of_birth")
    @get:JsonProperty("motorist_date_of_birth")
    var motoristDateOfBirth: String? = null,

    @field:JsonProperty("motorist_dl_number")
    @get:JsonProperty("motorist_dl_number")
    var motoristDlNumber: String? = null,

    @field:JsonProperty("motorist_address_block")
    @get:JsonProperty("motorist_address_block")
    var motoristAddressBlock: String? = null,

    @field:JsonProperty("motorist_address_street")
    @get:JsonProperty("motorist_address_street")
    var motoristAddressStreet: String? = null,

    @field:JsonProperty("motorist_address_city")
    @get:JsonProperty("motorist_address_city")
    var motoristAddressCity: String? = null,

    @field:JsonProperty("motorist_address_state")
    @get:JsonProperty("motorist_address_state")
    var motoristAddressState: String? = null,

    @field:JsonProperty("motorist_address_zip")
    @get:JsonProperty("motorist_address_zip")
    var motoristAddressZip: String? = null
) : Parcelable
