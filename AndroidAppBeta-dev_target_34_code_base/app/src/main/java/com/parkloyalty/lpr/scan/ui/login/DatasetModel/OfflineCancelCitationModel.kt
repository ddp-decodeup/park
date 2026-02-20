package com.parkloyalty.lpr.scan.ui.login.DatasetModel

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
@Entity(tableName = "offline_cancel_citation")
data class OfflineCancelCitationModel(
    @ColumnInfo(name = "lprnumber")
    @field:JsonProperty("lprnumber")
    @get:JsonProperty("lprnumber")
    var lprNumber: String? = null,

    @ColumnInfo(name = "uploadedCitationId")
    @field:JsonProperty("uploadedCitationId")
    @get:JsonProperty("uploadedCitationId")
    var uploadedCitationId: String? = null,

    @PrimaryKey
    @ColumnInfo(name = "ticketNumber")
    @field:JsonProperty("ticketNumber")
    @get:JsonProperty("ticketNumber")
    var ticketNumber: String = "",

    @ColumnInfo(name = "status")
    @field:JsonProperty("status")
    @get:JsonProperty("status")
    var status: String? = null,

    @ColumnInfo(name = "note")
    @field:JsonProperty("note")
    @get:JsonProperty("note")
    var note: String? = null,

    @ColumnInfo(name = "reason")
    @field:JsonProperty("reason")
    @get:JsonProperty("reason")
    var reason: String? = null,

    @ColumnInfo(name = "type")
    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null,

    @ColumnInfo(name = "voidReasonLookupCode")
    @field:JsonProperty("voidReasonLookupCode")
    @get:JsonProperty("voidReasonLookupCode")
    var voidReasonLookupCode: String? = null
) : Parcelable
