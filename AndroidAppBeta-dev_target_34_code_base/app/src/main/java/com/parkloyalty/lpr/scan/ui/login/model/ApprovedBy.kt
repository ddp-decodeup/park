package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ApprovedBy(
    @field:JsonProperty("initiator_id")
    @get:JsonProperty("initiator_id")
    var mInitiatorId: String? = null,
    @field:JsonProperty("initiator_role")
    @get:JsonProperty("initiator_role")
    var mInitiator_role: String? = null
) : Parcelable
