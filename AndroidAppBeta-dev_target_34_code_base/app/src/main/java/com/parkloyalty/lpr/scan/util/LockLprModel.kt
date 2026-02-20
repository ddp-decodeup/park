package com.parkloyalty.lpr.scan.util

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class LockLprModel(
    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var mMake: String? = null,

    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var mModel: String? = null,

    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var mColor: String? = null,

    @field:JsonProperty("lpr_number")
    @get:JsonProperty("lpr_number")
    var mLprNumber: String? = null,

    @field:JsonProperty("violation_code")
    @get:JsonProperty("violation_code")
    var mViolationCode: String? = null,

    @field:JsonProperty("address")
    @get:JsonProperty("address")
    var mAddress: String? = null,

    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var mState: String? = null,

    @field:JsonProperty("ticketCategory")
    @get:JsonProperty("ticketCategory")
    var ticketCategory: String? = null
) : Parcelable
