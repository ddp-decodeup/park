// "type":[
//     "ViolationList",
//     "StreetList",
//     "MeterList",
//     "BeatList",
//     "SquadList",
//     "HicerList",
//     "EquipmentList",
//     "ShiftList",
//     "ZoneList",
//     "AgencyList",
//     "CarMakeList",
//     "CarModelList",
//     "CarBodyStyleList",
//     "CarColorList",
//     "DecalYearList",
//     "StateList",
//     "RadioList",
//     "SupervisorList",
//     "ExpiryYearList",
//     "SideList",
//     "NotesList",
//     "CommentsList",
//     "RemarksList",
//     "ActivityList",
//     "PaymentData",
//     "ScofflawData",
//     "PermitData",
//     "TimingData",
//     "ExemptData",
//     "CitationData",
//     "MakeModelColorData",
//     "StolenData",
//     "CitationLayout",
//     "ActivityLayout",
//     "TimingRecordLayout",
//     "DeviceList",
//     "PBCZoneList",
//     "DeviceLicenseList",
//     "SettingsList",
//     "CancelReasonList",
//     "MakeModelList",
//     "SpaceList",
//     "VioTypeList",
//     "HolidayCalendarList",
//     "TierStemList",
//     "RegulationTimeList",
//     "LotList",
//     "VoidAndReissueReasonList",
//     "BlockList",
// ]
abstract class DataSetTypes {
  // -----------------------------
  // Master / Lookup Lists
  // -----------------------------
  static const String agencyList = 'AgencyList';
  static const String holidayCalendarList = 'HolidayCalendarList';
  static const String settingsList = 'SettingsList';
  static const String deviceList = 'DeviceList';
  static const String supervisorList = 'SupervisorList';
  static const String hearingTimeList = 'HearingTimeList';
  static const String shiftList = 'ShiftList';
  static const String sideList = 'SideList';
  static const String activityList = 'ActivityList';
  static const String beatList = 'BeatList';
  static const String zoneList = 'ZoneList';
  static const String pBCZoneList = 'PBCZoneList';
  static const String deviceLicenseList = 'DeviceLicenseList';
  static const String cancelReasonList = 'CancelReasonList';
  static const String decalYearList = 'DecalYearList';
  static const String carColorList = 'CarColorList';
  static const String stateList = 'StateList';
  static const String streetList = 'StreetList';
  static const String meterList = 'MeterList';
  static const String spaceList = 'SpaceList';
  static const String carBodyStyleList = 'CarBodyStyleList';
  static const String violationList = 'ViolationList';
  static const String vioTypeList = 'VioTypeList';
  static const String tierStemList = 'TierStemList';
  static const String notesList = 'NotesList';
  static const String commentsList = 'CommentsList';
  static const String remarksList = 'RemarksList';
  static const String regulationTimeList = 'RegulationTimeList';
  static const String lotList = 'LotList';
  static const String voidAndReissueReasonList = 'VoidAndReissueReasonList';
  static const String blockList = 'BlockList';
  static const String squadList = 'SquadList';
  static const String hicerList = 'HicerList';
  static const String equipmentList = 'EquipmentList';
  static const String carMakeList = 'CarMakeList';
  static const String carModelList = 'CarModelList';
  static const String makeModelList = 'MakeModelList';
  static const String radioList = 'RadioList';
  static const String expiryYearList = 'ExpiryYearList';
  static const String municipalBlockList = 'MunicipalBlockList';
  static const String municipalStreetList = 'MunicipalStreetList';
  static const String municipalCityList = 'MunicipalCityList';
  static const String municipalStateList = 'MunicipalStateList';
  static const String municipalViolationList = 'MunicipalViolationList';

  // -----------------------------
  // Data Sets
  // -----------------------------
  static const String paymentData = 'PaymentData';
  static const String scofflawData = 'ScofflawData';
  static const String permitData = 'PermitData';
  static const String timingData = 'TimingData';
  static const String exemptData = 'ExemptData';
  static const String citationData = 'CitationData';
  static const String makeModelColorData = 'MakeModelColorData';
  static const String stolenData = 'StolenData';

  // -----------------------------
  // Layout / Configuration
  // -----------------------------
  static const String citationLayout = 'CitationLayout';
  static const String activityLayout = 'ActivityLayout';
  static const String timingRecordLayout = 'TimingRecordLayout';

  // -----------------------------
  // Aggregated list of ALL dataset types
  // -----------------------------
  static const List<String> typeList = [
    // Lookup Lists
    violationList,
    streetList,
    meterList,
    beatList,
    squadList,
    hicerList,
    equipmentList,
    shiftList,
    zoneList,
    agencyList,
    carMakeList,
    carModelList,
    carBodyStyleList,
    carColorList,
    decalYearList,
    stateList,
    radioList,
    supervisorList,
    expiryYearList,
    sideList,
    notesList,
    commentsList,
    remarksList,
    activityList,
    municipalBlockList,
    municipalStreetList,
    municipalCityList,
    municipalStateList,
    municipalViolationList,

    // Data
    paymentData,
    scofflawData,
    permitData,
    timingData,
    exemptData,

    citationData,
    makeModelColorData,
    stolenData,

    // System / Device
    deviceList,
    pBCZoneList,
    deviceLicenseList,
    settingsList,
    cancelReasonList,
    makeModelList,
    spaceList,
    vioTypeList,
    holidayCalendarList,
    tierStemList,
    regulationTimeList,
    lotList,
    voidAndReissueReasonList,
    blockList,
  ];
  static const List<String> layoutTypeList = [citationLayout, activityLayout, timingRecordLayout];
  static const List<String> initialDataSetTypes = [shiftList, hearingTimeList];
}
