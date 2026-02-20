package com.parkloyalty.lpr.scan.databasetable

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
@Entity(tableName = "qr_code_inventory_table")
data class QrCodeInventoryTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 0,

    @ColumnInfo(name = "equipment_id")
    @field:JsonProperty("equipment_id")
    @get:JsonProperty("equipment_id")
    var equipmentID: String? = null,

    @ColumnInfo(name = "equipment_name")
    @field:JsonProperty("equipment_name")
    @get:JsonProperty("equipment_name")
    var equipmentName: String? = null,

    @ColumnInfo(name = "equipment_value")
    @field:JsonProperty("equipment_value")
    @get:JsonProperty("equipment_value")
    var equipmentValue: String? = null,

    @ColumnInfo(name = "is_required")
    @field:JsonProperty("is_required")
    @get:JsonProperty("is_required")
    var required: Int? = null,

    @ColumnInfo(name = "is_checked_out")
    @field:JsonProperty("is_checked_out")
    @get:JsonProperty("is_checked_out")
    var checkedOut: Int? = null,

    @ColumnInfo(name = "last_checked_out")
    @field:JsonProperty("last_checked_out")
    @get:JsonProperty("last_checked_out")
    var lastCheckedOut: String? = null
) : Parcelable
