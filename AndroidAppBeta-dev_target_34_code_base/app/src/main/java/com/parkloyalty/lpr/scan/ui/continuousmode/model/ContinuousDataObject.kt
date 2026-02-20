package com.parkloyalty.lpr.scan.ui.continuousmode.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ContinuousDataObject(
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var mBlock: String = "",
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var mStreet: String = "",
    @field:JsonProperty("regulation")
    @get:JsonProperty("regulation")
    var mRegulation: String = "",
    @field:JsonProperty("zone")
    @get:JsonProperty("zone")
    var mZone: String = "",
    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var mSide: String = "",
    @field:JsonProperty("start_time")
    @get:JsonProperty("start_time")
    var mStartTime: String = "",
    @field:JsonProperty("officer_id")
    @get:JsonProperty("officer_id")
    var mOfficerID: String = "",
    @field:JsonProperty("selected_radio")
    @get:JsonProperty("selected_radio")
    var mSelectedRadio: String = "",
    @field:JsonProperty("device_id")
    @get:JsonProperty("device_id")
    var mDeviceId: String = "",
    @field:JsonProperty("device_friendly_name")
    @get:JsonProperty("device_friendly_name")
    var mDeviceFriendlyName: String = "",
    @field:JsonProperty("latitude")
    @get:JsonProperty("latitude")
    var mLatitude: Double = 0.0,
    @field:JsonProperty("longitude")
    @get:JsonProperty("longitude")
    var mLongitude: Double = 0.0,
    @field:JsonProperty("beat")
    @get:JsonProperty("beat")
    var mBeat: String = "",
    @field:JsonProperty("shift")
    @get:JsonProperty("shift")
    var mShift: String = "",
    @field:JsonProperty("initiator_name")
    @get:JsonProperty("initiator_name")
    var mInitiatorName: String = "",
    @field:JsonProperty("supervisor_name")
    @get:JsonProperty("supervisor_name")
    var mSupervisor_name: String = ""
) : Parcelable
