package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
@Entity(tableName = "welcome_list")
data class WelcomeListDatatbase(
    @ColumnInfo(name = "welcome_list")
    @field:JsonProperty("welcome_list")
    @get:JsonProperty("welcome_list")
    var welcomeList: WelcomeList? = null,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0
) : Parcelable
