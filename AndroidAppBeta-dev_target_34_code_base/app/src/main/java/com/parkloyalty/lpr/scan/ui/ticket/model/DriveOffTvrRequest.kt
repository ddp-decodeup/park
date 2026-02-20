package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class DriveOffTvrRequest(
    @field:JsonProperty("drive_off")
    @get:JsonProperty("drive_off")
    var isDriveOff: Boolean = false,
    @field:JsonProperty("tvr")
    @get:JsonProperty("tvr")
    var isTvr: Boolean = false
) : Parcelable
