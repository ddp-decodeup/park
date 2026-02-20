package com.parkloyalty.lpr.scan.ui.honorbill.responsemodel

import androidx.annotation.Keep
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.parkloyalty.lpr.scan.ui.honorbill.HonorBillCitationLayoutData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
@Entity(tableName = "owner_bill_layout")
data class HonorBillLayoutResponse(
    @ColumnInfo(name = "data")
    @field:JsonProperty("data")
    @get:JsonProperty("data")
    var data: List<HonorBillCitationLayoutData>? = null,

    @ColumnInfo(name = "success")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var success: Boolean? = null,

    @ColumnInfo(name = "response")
    @field:JsonProperty("response")
    @get:JsonProperty("response")
    var response: String? = null,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0
) : Parcelable
