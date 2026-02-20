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
@Entity(tableName = "dataset_void_and_reissue_reason_list")
data class DatasetVoidAndReissueReasonListModel(
    @ColumnInfo(name = "void_and_reissue_reason_list")
    @field:JsonProperty("VoidAndReissueReasonList")
    @get:JsonProperty("VoidAndReissueReasonList")
    var voidAndReissueReasonList: List<DatasetResponse>? = null,

    @PrimaryKey
    @ColumnInfo(name = "id")
    @field:JsonProperty("id")
    @get:JsonProperty("id")
    var id: Int = 1
) : Parcelable
