package com.parkloyalty.lpr.scan.ui.allreport.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class AfterSevenPMRequest(
    @field:JsonProperty("location_details")
    @get:JsonProperty("location_details")
    var locationDetails: LocationDetailsAfter? = null,
    @field:JsonProperty("form")
    @get:JsonProperty("form")
    var form: String? = null,
    @field:JsonProperty("officer_details")
    @get:JsonProperty("officer_details")
    var officerDetails: OfficerDetailsAfter? = null,
    @field:JsonProperty("details")
    @get:JsonProperty("details")
    var details: DetailsAfter? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DetailsAfter(
    @field:JsonProperty("comments")
    @get:JsonProperty("comments")
    var comments: String? = null,
    @field:JsonProperty("signature")
    @get:JsonProperty("signature")
    var signature: String? = null,
    @field:JsonProperty("photo6")
    @get:JsonProperty("photo6")
    var photo6: String? = null,
    @field:JsonProperty("photo7")
    @get:JsonProperty("photo7")
    var photo7: String? = null,
    @field:JsonProperty("vendor_count")
    @get:JsonProperty("vendor_count")
    var vendorCount: Int? = null,
    @field:JsonProperty("photo2")
    @get:JsonProperty("photo2")
    var photo2: String? = null,
    @field:JsonProperty("vendor_locations")
    @get:JsonProperty("vendor_locations")
    var vendorLocations: String? = null,
    @field:JsonProperty("photo3")
    @get:JsonProperty("photo3")
    var photo3: String? = null,
    @field:JsonProperty("photo4")
    @get:JsonProperty("photo4")
    var photo4: String? = null,
    @field:JsonProperty("photo5")
    @get:JsonProperty("photo5")
    var photo5: String? = null,
    @field:JsonProperty("photo1")
    @get:JsonProperty("photo1")
    var photo1: String? = null,
    @field:JsonProperty("report_number")
    @get:JsonProperty("report_number")
    var reportNumber: String? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("line")
    @get:JsonProperty("line")
    var line: String? = null,
    @field:JsonProperty("security_observation")
    @get:JsonProperty("security_observation")
    var securityObservation: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LocationDetailsAfter(
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var latitude: Double? = null,
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var longitude: Double? = null,
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var zone: String? = null,
    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null
) : Parcelable

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class OfficerDetailsAfter(
    @field:JsonProperty("shift_id")
    @get:JsonProperty("shift_id")
    var shiftId: String? = null,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var beat: String? = null,
    @field:JsonProperty("badge_id")
    @get:JsonProperty("badge_id")
    var badgeId: String? = null,
    @field:JsonProperty("officer_name")
    @get:JsonProperty("officer_name")
    var officerName: String? = null
) : Parcelable
