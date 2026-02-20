package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UpdateTimeDataList(
    @field:JsonProperty("ActivityLayout")
    @get:JsonProperty("ActivityLayout")
    var activityLayout: UpdateTimeDb? = null,
    @field:JsonProperty("ActivityList")
    @get:JsonProperty("ActivityList")
    var activityList: UpdateTimeDb? = null,
    @field:JsonProperty("AgencyList")
    @get:JsonProperty("AgencyList")
    var agencyList: UpdateTimeDb? = null,
    @field:JsonProperty("BeatList")
    @get:JsonProperty("BeatList")
    var beatList: UpdateTimeDb? = null,
    @field:JsonProperty("CancelReasonList")
    @get:JsonProperty("CancelReasonList")
    var cancelReasonList: UpdateTimeDb? = null,
    @field:JsonProperty("CarBodyStyleList")
    @get:JsonProperty("CarBodyStyleList")
    var carBodyStyleList: UpdateTimeDb? = null,
    @field:JsonProperty("CarColorList")
    @get:JsonProperty("CarColorList")
    var carColorList: UpdateTimeDb? = null,
    @field:JsonProperty("MakeModelList")
    @get:JsonProperty("MakeModelList")
    var carMakeList: UpdateTimeDb? = null,
    @field:JsonProperty("MakeModelList2")
    @get:JsonProperty("MakeModelList2")
    var carModelList: UpdateTimeDb? = null,
    @field:JsonProperty("CitationData")
    @get:JsonProperty("CitationData")
    var citationData: UpdateTimeDb? = null,
    @field:JsonProperty("CitationLayout")
    @get:JsonProperty("CitationLayout")
    var citationLayout: UpdateTimeDb? = null,
    @field:JsonProperty("CommentsList")
    @get:JsonProperty("CommentsList")
    var commentsList: UpdateTimeDb? = null,
    @field:JsonProperty("DecalYearList")
    @get:JsonProperty("DecalYearList")
    var decalYearList: UpdateTimeDb? = null,
    @field:JsonProperty("DirectionList")
    @get:JsonProperty("DirectionList")
    var directionList: UpdateTimeDb? = null,
    @field:JsonProperty("ExemptData")
    @get:JsonProperty("ExemptData")
    var exemptData: UpdateTimeDb? = null,
    @field:JsonProperty("LotList")
    @get:JsonProperty("LotList")
    var lotList: UpdateTimeDb? = null,
    @field:JsonProperty("MakeModelColorData")
    @get:JsonProperty("MakeModelColorData")
    var makeModelColorData: UpdateTimeDb? = null,
    @field:JsonProperty("MeterList")
    @get:JsonProperty("MeterList")
    var meterList: UpdateTimeDb? = null,
    @field:JsonProperty("NotesList")
    @get:JsonProperty("NotesList")
    var notesList: UpdateTimeDb? = null,
    @field:JsonProperty("PaymentData")
    @get:JsonProperty("PaymentData")
    var paymentData: UpdateTimeDb? = null,
    @field:JsonProperty("PermitData")
    @get:JsonProperty("PermitData")
    var permitData: UpdateTimeDb? = null,
    @field:JsonProperty("RadioList")
    @get:JsonProperty("RadioList")
    var radioList: UpdateTimeDb? = null,
    @field:JsonProperty("RegulationTimeList")
    @get:JsonProperty("RegulationTimeList")
    var regulationTimeList: UpdateTimeDb? = null,
    @field:JsonProperty("RemarksList")
    @get:JsonProperty("RemarksList")
    var remarksList: UpdateTimeDb? = null,
    @field:JsonProperty("ScofflawData")
    @get:JsonProperty("ScofflawData")
    var scofflawData: UpdateTimeDb? = null,
    @field:JsonProperty("ShiftList")
    @get:JsonProperty("ShiftList")
    var shiftList: UpdateTimeDb? = null,
    @field:JsonProperty("SideList")
    @get:JsonProperty("SideList")
    var sideList: UpdateTimeDb? = null,
    @field:JsonProperty("StateList")
    @get:JsonProperty("StateList")
    var stateList: UpdateTimeDb? = null,
    @field:JsonProperty("StolenData")
    @get:JsonProperty("StolenData")
    var stolenData: UpdateTimeDb? = null,
    @field:JsonProperty("StreetList")
    @get:JsonProperty("StreetList")
    var streetList: UpdateTimeDb? = null,
    @field:JsonProperty("SupervisorList")
    @get:JsonProperty("SupervisorList")
    var supervisorList: UpdateTimeDb? = null,
    @field:JsonProperty("TierStemList")
    @get:JsonProperty("TierStemList")
    var tierStemList: UpdateTimeDb? = null,
    @field:JsonProperty("TimingData")
    @get:JsonProperty("TimingData")
    var timingData: UpdateTimeDb? = null,
    @field:JsonProperty("TimingRecordLayout")
    @get:JsonProperty("TimingRecordLayout")
    var timingRecordLayout: UpdateTimeDb? = null,
    @field:JsonProperty("VehiclePlateTypeList")
    @get:JsonProperty("VehiclePlateTypeList")
    var vehiclePlateTypeList: UpdateTimeDb? = null,
    @field:JsonProperty("ViolationList")
    @get:JsonProperty("ViolationList")
    var violationList: UpdateTimeDb? = null,
    @field:JsonProperty("ZoneList")
    @get:JsonProperty("ZoneList")
    var zoneList: UpdateTimeDb? = null,
    @field:JsonProperty("DeviceList")
    @get:JsonProperty("DeviceList")
    var mDeviceList: UpdateTimeDb? = null,
    @field:JsonProperty("CityZoneList")
    @get:JsonProperty("CityZoneList")
    var mCityZoneList: UpdateTimeDb? = null,
    @field:JsonProperty("VoidAndReissueReasonList")
    @get:JsonProperty("VoidAndReissueReasonList")
    var mVoidAndReissueList: UpdateTimeDb? = null,
    @field:JsonProperty("EquipmentList")
    @get:JsonProperty("EquipmentList")
    var mEquipmentList: UpdateTimeDb? = null,
    @field:JsonProperty("BlockList")
    @get:JsonProperty("BlockList")
    var mBlockList: UpdateTimeDb? = null,
    @field:JsonProperty("PrinterList")
    @get:JsonProperty("PrinterList")
    var mPrinterList: UpdateTimeDb? = null,
    @field:JsonProperty("SpaceList")
    @get:JsonProperty("SpaceList")
    var mSpaceList: UpdateTimeDb? = null,
    @field:JsonProperty("SquadList")
    @get:JsonProperty("SquadList")
    var mSquadList: UpdateTimeDb? = null,
    @field:JsonProperty("MunicipalViolationList")
    @get:JsonProperty("MunicipalViolationList")
    var municipalViolationList: UpdateTimeDb? = null,
    @field:JsonProperty("MunicipalBlockList")
    @get:JsonProperty("MunicipalBlockList")
    var municipalBlockList: UpdateTimeDb? = null,
    @field:JsonProperty("MunicipalStreetList")
    @get:JsonProperty("MunicipalStreetList")
    var municipalStreetList: UpdateTimeDb? = null,
    @field:JsonProperty("MunicipalCityList")
    @get:JsonProperty("MunicipalCityList")
    var municipalCityList: UpdateTimeDb? = null,
    @field:JsonProperty("MunicipalStateList")
    @get:JsonProperty("MunicipalStateList")
    var municipalStateList: UpdateTimeDb? = null,
    @field:JsonProperty("VioList")
    @get:JsonProperty("VioList")
    var vioList: UpdateTimeDb? = null,
    @field:JsonProperty("HolidayCalendarList")
    @get:JsonProperty("HolidayCalendarList")
    var holidayCalendarList: UpdateTimeDb? = null
) : Parcelable
