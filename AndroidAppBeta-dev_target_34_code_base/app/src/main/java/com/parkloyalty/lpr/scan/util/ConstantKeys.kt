package com.parkloyalty.lpr.scan.util

/*Intent Keys*/
const val INTENT_KEY_LPR_BUNDLE = "LPR_BUNDLE"
const val INTENT_KEY_SCANNER_TYPE = "SCANNER_TYPE"
const val SCANNER_TYPE_IMENSE = "SCANNER_TYPE_IMENSE"
const val SCANNER_TYPE_DOUBANGO = "SCANNER_TYPE_DOUBANGO"

const val INTENT_KEY_MAKE = "MAKE"
const val INTENT_KEY_MODEL = "MODEL"
const val INTENT_KEY_COLOR = "COLOR"
const val INTENT_KEY_LPNUMBER = "LPNUMBER"
const val INTENT_KEY_SCCOFFLAW = "SCCOFFLAW"
const val INTENT_KEY_CITATION_NUMBER = "CITATION_NUMBER"
const val INTENT_KEY_VIOLATION_DATE = "VIOLATION_DATE"
const val INTENT_KEY_MESSAGE = "message"

const val INTENT_KEY_UNPAID_CITATION_COUNT = "KEY_UNPAID_CITATION_COUNT"
const val INTENT_KEY_TIMING_IMAGES = "KEY_TIMING_IMAGES"
const val INTENT_KEY_TIMING_IMAGES_BASE64 = "KEY_TIMING_IMAGES_BASE64"

const val INTENT_KEY_FROM = "from"
const val INTENT_KEY_SCANNED_EQUIPMENT_KEY = "ScannedEquipmentKey"
const val INTENT_KEY_SCANNED_EQUIPMENT_VALUE = "ScannedEquipmentValue"

const val INTENT_KEY_MOTORIST_NAME = "MOTORIST_NAME"
const val INTENT_KEY_MOTORIST_DOB = "MOTORIST_DOB"
const val INTENT_KEY_MOTORIST_DL_NUMBER = "MOTORIST_DL_NUMBER"
const val INTENT_KEY_MOTORIST_ADDRESS = "MOTORIST_ADDRESS"

const val INTENT_KEY_LPR_NUMBER = "LPR_NUMBER"
const val INTENT_KEY_VEHICLE_INFO = "VEHICLE_INFO"
const val INTENT_KEY_VEHICLE_STICKER_URL = "VEHICLE_VEHICLE_STICKER_URL"
/*Intent Keys*/


/*API Constant Values*/
const val API_CONSTANT_TYPE_OF_HITS_ALL = "All"
const val API_CONSTANT_TYPE_OF_HITS_PERMIT = "Permit"
const val API_CONSTANT_TYPE_OF_HITS_TIMING = "Timing"
const val API_CONSTANT_TYPE_OF_HITS_SCOFFLAW = "Scofflaw"

const val API_CONSTANT_CITATION_STATUS_VALID = "Valid"
const val API_CONSTANT_CALCULATED_FIELD_ESCALATED = "escalated"
const val API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES = "TimingImages"
const val API_CONSTANT_DOWNLOAD_TYPE_TIMING_IMAGES = "TimingImages"
const val API_CONSTANT_DOWNLOAD_TYPE_RAW_CAMERA_FEED_IMAGES = "RawCameraFeedImages"
const val API_CONSTANT_DOWNLOAD_TYPE_CITATION_IMAGES = "CitationImages"
const val API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES = "CitationImages"
const val API_CONSTANT_DOWNLOAD_TYPE_LOGO_IMAGE = "LogoImage"
const val API_CONSTANT_SIGNATURE_IMAGES = "SignatureImages"
const val API_CONSTANT_EQUIPMENT_INVENTORY = "EquipmentInventory"
const val API_CONSTANT_OFFICER_EQUIPMENT_INVENTORY = "OfficerEquipmentInventory"
const val API_CONSTANT_ADD_LOG_FOR_LOGOUT = "AddLogForLogout"
const val API_CONSTANT_HEADER_IMAGE_URL_DOWNLOAD = "HeaderImageUrlDownload"
const val API_CONSTANT_FOOTER_IMAGE_URL_DOWNLOAD = "FooterImageUrlDownload"


const val API_CONSTANT_TICKET_CATEGORY_CITATION = "citation"
const val API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET = "municipal_ticket"

const val API_CONSTANT_BOOT_TYPE_HANDHELD_INITIATED = "HANDHELD_INITIATED"
const val API_CONSTANT_PLATE_TYPE_PERSONAL = "Personal"

const val API_QUERY_PARAMETER_LIMIT = "limit"
const val API_QUERY_PARAMETER_PAGE = "page"
const val API_QUERY_PARAMETER_MOTORIST_FIRST_NAME = "motorist_first_name"
const val API_QUERY_PARAMETER_MOTORIST_LAST_NAME = "motorist_last_name"
const val API_QUERY_PARAMETER_MOTORIST_DATE_OF_BIRTH = "motorist_date_of_birth"
/*API Constant Values*/

/*Fix Value Constant*/
const val DEFAULT_AMOUNT_VALUE = "0"
const val HIDE_DELETE_BUTTON = 0
const val SHOW_DELETE_BUTTON = 1
const val PAGE_FIX_ONE = 1
const val LIMIT_FIX = 25
const val STATE_NEW_YORK = "NEW YORK"
/*Fix Value Constant*/

/*Default Setting Keys*/
const val SETTING_MAX_IMAGES_COUNT = "MAX_IMAGES"

/*Dataset Keys*/
const val DATASET_DECAL_YEAR_LIST = "DecalYearList"
const val DATASET_MAKE_MODEL_LIST = "MakeModelList"
const val DATASET_MAKE_MODEL_LIST2 = "MakeModelList2"
const val DATASET_CAR_COLOR_LIST = "CarColorList"
const val DATASET_STATE_LIST = "StateList"
const val DATASET_BLOCK_LIST = "BlockList"
const val DATASET_STREET_LIST = "StreetList"
const val DATASET_METER_LIST = "MeterList"
const val DATASET_SPACE_LIST = "SpaceList"
const val DATASET_CAR_BODY_STYLE_LIST = "CarBodyStyleList"
const val DATASET_VIOLATION_LIST = "ViolationList"
const val DATASET_VIO_LIST = "VioTypeList"
const val DATASET_SIDE_LIST = "SideList"
const val DATASET_TIER_STEM_LIST = "TierStemList"
const val DATASET_NOTES_LIST = "NotesList"
const val DATASET_REMARKS_LIST = "RemarksList"
const val DATASET_REGULATION_TIME_LIST = "RegulationTimeList"
const val DATASET_LOT_LIST = "LotList"
const val DATASET_SETTINGS_LIST = "SettingsList"
const val DATASET_CANCEL_REASON_LIST = "CancelReasonList"
const val DATASET_PBC_ZONE_LIST = "PBCZoneList"
const val DATASET_VOID_AND_REISSUE_REASON_LIST = "VoidAndReissueReasonList"
const val DATASET_INVENTORY_REPORT_LIST = "InventoryReportList"
const val DATASET_MUNICIPAL_VIOLATION_LIST = "MunicipalViolationList"
const val DATASET_MUNICIPAL_BLOCK_LIST = "MunicipalBlockList"
const val DATASET_MUNICIPAL_STREET_LIST = "MunicipalStreetList"
const val DATASET_MUNICIPAL_CITY_LIST = "MunicipalCityList"
const val DATASET_MUNICIPAL_STATE_LIST = "MunicipalStateList"
const val DATASET_CAR_MAKE_LIST = "CarMakeList"
const val DATASET_CAR_MODEL_LIST = "CarModelList"
const val DATASET_HOLIDAY_CALENDAR_LIST = "HolidayCalendarList"

/*Dataset Keys*/


/*Inventory Management System*/
const val FROM_EQUIPMENT_CHECKOUT = "FromEquipmentCheckout"
const val FROM_EQUIPMENT_CHECKIN = "FromEquipmentCheckIn"

const val EQUIPMENT_CHECKED_OUT = 1
const val EQUIPMENT_CHECKED_IN = 0

const val EQUIPMENT_REQUIRED = 1
const val EQUIPMENT_NOT_REQUIRED = 0
/*Inventory Management System*/

/*Additional Activity Log we wanted to log from the application*/
const val ACTIVITY_LOG_WELCOME_SCAN = "Welcome Scan" //When officer hit Scan CTA from welcome screen
const val ACTIVITY_LOG_MENU_SCAN = "Menu Scan" //When officer hit Scan CTA from Hem Menu. Ticketing -> Scan
const val ACTIVITY_LOG_MENU_ISSUE = "Menu Issue" //When officer hit Issue CTA from Hem Menu. Ticketing -> Issue
const val ACTIVITY_LOG_LOOKUP = "Lookup" //When officer hit lookup from Hem. Menu
const val ACTIVITY_LOG_REISSUE = "Reissue" //When officer hit Reissue CTA from Ticket Details Screen
const val ACTIVITY_LOG_CANCELLATION_REQUEST = "Cancellation Request" //When officer hit Reissue CTA from Ticket Details Screen
const val ACTIVITY_LOG_ISSUE_MORE = "Issue More" //When officer hit Issue More CTA from Ticket Details Screen
const val ACTIVITY_LOG_UPDATE_PLATE = "Update Plate" //When officer change or try to change the Lpr Number after Reissue CTA