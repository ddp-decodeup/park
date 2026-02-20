package com.parkloyalty.lpr.scan.ui.login.model

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UpdateTimeDataset(
    @field:JsonProperty("SettingsList")
    @get:JsonProperty("SettingsList")
    var settingsList: UpdateTimeDb? = null,

    @field:JsonProperty("AgencyList")
    @get:JsonProperty("AgencyList")
    var agencyList: UpdateTimeDb? = null,

    @field:JsonProperty("CarBodyStyleList")
    @get:JsonProperty("CarBodyStyleList")
    var carBodyStyleList: UpdateTimeDb? = null,

    @field:JsonProperty("CarColorList")
    @get:JsonProperty("CarColorList")
    var carColorList: UpdateTimeDb? = null,

    @field:JsonProperty("CarMakeList")
    @get:JsonProperty("CarMakeList")
    var carMakeList: UpdateTimeDb? = null,

    @field:JsonProperty("CarModelList")
    @get:JsonProperty("CarModelList")
    var carModelList: UpdateTimeDb? = null,

    @field:JsonProperty("DecalYearList")
    @get:JsonProperty("DecalYearList")
    var decalYearList: UpdateTimeDb? = null,

    @field:JsonProperty("LotList")
    @get:JsonProperty("LotList")
    var lotList: UpdateTimeDb? = null,

    @field:JsonProperty("MeterList")
    @get:JsonProperty("MeterList")
    var meterList: UpdateTimeDb? = null,

    @field:JsonProperty("NotesList")
    @get:JsonProperty("NotesList")
    var notesList: UpdateTimeDb? = null,

    @field:JsonProperty("RegulationTimeList")
    @get:JsonProperty("RegulationTimeList")
    var regulationTimeList: UpdateTimeDb? = null,

    @field:JsonProperty("RemarksList")
    @get:JsonProperty("RemarksList")
    var remarksList: UpdateTimeDb? = null,

    @field:JsonProperty("SideList")
    @get:JsonProperty("SideList")
    var sideList: UpdateTimeDb? = null,

    @field:JsonProperty("StateList")
    @get:JsonProperty("StateList")
    var stateList: UpdateTimeDb? = null,

    @field:JsonProperty("StreetList")
    @get:JsonProperty("StreetList")
    var streetList: UpdateTimeDb? = null,

    @field:JsonProperty("TierStemList")
    @get:JsonProperty("TierStemList")
    var tierStemList: UpdateTimeDb? = null,

    @field:JsonProperty("ViolationList")
    @get:JsonProperty("ViolationList")
    var violationList: UpdateTimeDb? = null
) : Parcelable
