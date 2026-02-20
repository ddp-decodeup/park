package com.parkloyalty.lpr.scan.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class UpdateTimeData(
    @field:JsonProperty("ActivityLayout")
    @get:JsonProperty("ActivityLayout")
    var activityLayout: String? = null,
    @field:JsonProperty("ActivityList")
    @get:JsonProperty("ActivityList")
    var activityList: String? = null,
    @field:JsonProperty("AgencyList")
    @get:JsonProperty("AgencyList")
    var agencyList: String? = null,
    @field:JsonProperty("BeatList")
    @get:JsonProperty("BeatList")
    var beatList: String? = null,
    @field:JsonProperty("CancelReasonList")
    @get:JsonProperty("CancelReasonList")
    var cancelReasonList: String? = null,
    @field:JsonProperty("CarBodyStyleList")
    @get:JsonProperty("CarBodyStyleList")
    var carBodyStyleList: String? = null,
    @field:JsonProperty("CarColorList")
    @get:JsonProperty("CarColorList")
    var carColorList: String? = null,
    @field:JsonProperty("MakeModelList")
    @get:JsonProperty("MakeModelList")
    var carMakeList: String? = null,
    @field:JsonProperty("MakeModelList2")
    @get:JsonProperty("MakeModelList2")
    var carModelList: String? = null,
    @field:JsonProperty("CitationData")
    @get:JsonProperty("CitationData")
    var citationData: String? = null,
    @field:JsonProperty("CitationLayout")
    @get:JsonProperty("CitationLayout")
    var citationLayout: String? = null,
    @field:JsonProperty("CommentsList")
    @get:JsonProperty("CommentsList")
    var commentsList: String? = null,
    @field:JsonProperty("DecalYearList")
    @get:JsonProperty("DecalYearList")
    var decalYearList: String? = null,
    @field:JsonProperty("DirectionList")
    @get:JsonProperty("DirectionList")
    var directionList: String? = null,
    @field:JsonProperty("ExemptData")
    @get:JsonProperty("ExemptData")
    var exemptData: String? = null,
    @field:JsonProperty("LotList")
    @get:JsonProperty("LotList")
    var lotList: String? = null,
    @field:JsonProperty("MakeModelColorData")
    @get:JsonProperty("MakeModelColorData")
    var makeModelColorData: String? = null,
    @field:JsonProperty("MeterList")
    @get:JsonProperty("MeterList")
    var meterList: String? = null,
    @field:JsonProperty("NotesList")
    @get:JsonProperty("NotesList")
    var notesList: String? = null,
    @field:JsonProperty("PaymentData")
    @get:JsonProperty("PaymentData")
    var paymentData: String? = null,
    @field:JsonProperty("PermitData")
    @get:JsonProperty("PermitData")
    var permitData: String? = null,
    @field:JsonProperty("RadioList")
    @get:JsonProperty("RadioList")
    var radioList: String? = null,
    @field:JsonProperty("RegulationTimeList")
    @get:JsonProperty("RegulationTimeList")
    var regulationTimeList: String? = null,
    @field:JsonProperty("RemarksList")
    @get:JsonProperty("RemarksList")
    var remarksList: String? = null,
    @field:JsonProperty("ScofflawData")
    @get:JsonProperty("ScofflawData")
    var scofflawData: String? = null,
    @field:JsonProperty("ShiftList")
    @get:JsonProperty("ShiftList")
    var shiftList: String? = null,
    @field:JsonProperty("SideList")
    @get:JsonProperty("SideList")
    var sideList: String? = null,
    @field:JsonProperty("StateList")
    @get:JsonProperty("StateList")
    var stateList: String? = null,
    @field:JsonProperty("StolenData")
    @get:JsonProperty("StolenData")
    var stolenData: String? = null,
    @field:JsonProperty("StreetList")
    @get:JsonProperty("StreetList")
    var streetList: String? = null,
    @field:JsonProperty("SupervisorList")
    @get:JsonProperty("SupervisorList")
    var supervisorList: String? = null,
    @field:JsonProperty("TierStemList")
    @get:JsonProperty("TierStemList")
    var tierStemList: String? = null,
    @field:JsonProperty("TimingData")
    @get:JsonProperty("TimingData")
    var timingData: String? = null,
    @field:JsonProperty("TimingRecordLayout")
    @get:JsonProperty("TimingRecordLayout")
    var timingRecordLayout: String? = null,
    @field:JsonProperty("VehiclePlateTypeList")
    @get:JsonProperty("VehiclePlateTypeList")
    var vehiclePlateTypeList: String? = null,
    @field:JsonProperty("ViolationList")
    @get:JsonProperty("ViolationList")
    var violationList: String? = null,
    @field:JsonProperty("VioList")
    @get:JsonProperty("VioList")
    var vioList: String? = null,
    @field:JsonProperty("HolidayCalendarList")
    @get:JsonProperty("HolidayCalendarList")
    var holidayCalendarList: String? = null,
    @field:JsonProperty("ZoneList")
    @get:JsonProperty("ZoneList")
    var zoneList: String? = null,
    @field:JsonProperty("PBCZoneList")
    @get:JsonProperty("PBCZoneList")
    var mCityZoneList: String? = null,
    @field:JsonProperty("VoidAndReissueReasonList")
    @get:JsonProperty("VoidAndReissueReasonList")
    var mVoidAndReissueList: String? = null,
    @field:JsonProperty("DeviceList")
    @get:JsonProperty("DeviceList")
    var mDeviceList: String? = null,
    @field:JsonProperty("EquipmentList")
    @get:JsonProperty("EquipmentList")
    var mEquipmentList: String? = null,
    @field:JsonProperty("BlockList")
    @get:JsonProperty("BlockList")
    var mBlockList: String? = null,
    @field:JsonProperty("PrinterList")
    @get:JsonProperty("PrinterList")
    var mPrinterList: String? = null,
    @field:JsonProperty("SpaceList")
    @get:JsonProperty("SpaceList")
    var mSpaceList: String? = null,
    @field:JsonProperty("SquadList")
    @get:JsonProperty("SquadList")
    var mSquadList: String? = null,
    @field:JsonProperty("MunicipalViolationList")
    @get:JsonProperty("MunicipalViolationList")
    var municipalViolationList: String? = null,
    @field:JsonProperty("MunicipalBlockList")
    @get:JsonProperty("MunicipalBlockList")
    var municipalBlockList: String? = null,
    @field:JsonProperty("MunicipalStreetList")
    @get:JsonProperty("MunicipalStreetList")
    var municipalStreetList: String? = null,
    @field:JsonProperty("MunicipalCityList")
    @get:JsonProperty("MunicipalCityList")
    var municipalCityList: String? = null,
    @field:JsonProperty("MunicipalStateList")
    @get:JsonProperty("MunicipalStateList")
    var municipalStateList: String? = null
) : Parcelable
