package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.android.gms.maps.model.LatLng

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MarkColorModel(
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int,
    @field:JsonProperty("LatLng")
    @get:JsonProperty("LatLng")
    var latLng: LatLng
) : Parcelable
