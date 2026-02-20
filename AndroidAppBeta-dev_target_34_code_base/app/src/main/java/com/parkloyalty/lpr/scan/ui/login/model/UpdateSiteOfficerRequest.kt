package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UpdateSiteOfficerRequest(
    @field:JsonProperty("site_id")
    @get:JsonProperty("site_id")
    var siteId: String? = null,
    @field:JsonProperty("site_officer_id")
    @get:JsonProperty("site_officer_id")
    var siteOfficerId: String? = null,
    @field:JsonProperty("update_package")
    @get:JsonProperty("update_package")
    var updatePackage: UpdatePackage? = null
) : Parcelable
