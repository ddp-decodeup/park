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
@Entity(tableName = "dataset_block_list")
data class DatasetBlockListModel(
    @ColumnInfo(name = "block_list")
    @field:JsonProperty("BlockList")
    @get:JsonProperty("BlockList")
    var blockList: List<DatasetResponse>? = null,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 1
) : Parcelable
