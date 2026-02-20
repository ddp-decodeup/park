package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
@Entity(tableName = "user_profile_details")
data class LoginResponseData(
    @ColumnInfo(name = "first_name")
    @field:JsonProperty("first_name")
    @get:JsonProperty("first_name")
    var first_name: String? = null,

    @ColumnInfo(name = "last_name")
    @field:JsonProperty("last_name")
    @get:JsonProperty("last_name")
    var last_name: String? = null,

    @ColumnInfo(name = "dateofBirth")
    @field:JsonProperty("dateofBirth")
    @get:JsonProperty("dateofBirth")
    var dateofBirth: String? = null,

    @ColumnInfo(name = "qr_code_image")
    @field:JsonProperty("qr_code_image")
    @get:JsonProperty("qr_code_image")
    var qrCodeImage: String? = null,

    @ColumnInfo(name = "city")
    @field:JsonProperty("city")
    @get:JsonProperty("city")
    var city: String? = null,

    @ColumnInfo(name = "address1")
    @field:JsonProperty("address1")
    @get:JsonProperty("address1")
    var address1: String? = null,

    @ColumnInfo(name = "postalCode")
    @field:JsonProperty("postalCode")
    @get:JsonProperty("postalCode")
    var postalCode: String? = null,

    @ColumnInfo(name = "zip_code")
    @field:JsonProperty("zip_code")
    @get:JsonProperty("zip_code")
    var zipcode: String? = null,

    @ColumnInfo(name = "address")
    @field:JsonProperty("address")
    @get:JsonProperty("address")
    var address: String? = null,

    @ColumnInfo(name = "otp")
    @field:JsonProperty("otp")
    @get:JsonProperty("otp")
    var otp: String? = null,

    @PrimaryKey
    @ColumnInfo(name = "mobile")
    @field:JsonProperty("mobile")
    @get:JsonProperty("mobile")
    var mobile: String? = null,

    @ColumnInfo(name = "token")
    @field:JsonProperty("token")
    @get:JsonProperty("token")
    var token: String? = null,

    @ColumnInfo(name = "profile_image")
    @field:JsonProperty("profile_image")
    @get:JsonProperty("profile_image")
    var profileImage: String? = null,

    @ColumnInfo(name = "user_id")
    @field:JsonProperty("user_id")
    @get:JsonProperty("user_id")
    var user_id: Int = 0,

    @ColumnInfo(name = "state")
    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,

    @ColumnInfo(name = "lang")
    @field:JsonProperty("lang")
    @get:JsonProperty("lang")
    var lang: String? = null,

    @ColumnInfo(name = "email")
    @field:JsonProperty("email")
    @get:JsonProperty("email")
    var email: String? = null,

    @ColumnInfo(name = "pin")
    @field:JsonProperty("pin")
    @get:JsonProperty("pin")
    var pin: String? = null
) : Parcelable
