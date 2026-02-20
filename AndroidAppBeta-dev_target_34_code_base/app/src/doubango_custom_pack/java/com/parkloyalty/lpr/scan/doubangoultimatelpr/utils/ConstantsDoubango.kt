package com.parkloyalty.lpr.scan.doubangoultimatelpr.utils

import java.text.SimpleDateFormat
import java.util.*

object ConstantsDoubango {
    /*API Client*/
    const val API_HEADER_TOKEN = "token"
    const val API_HEADER_CONTENT_TYPE = "Content-Type"
    const val API_HEADER_CONTENT_TYPE_APPLICATION_JSON = "application/json"
    /*API Client*/

    /*Encryption*/
    const val ALGORITHM_AES = "AES"
    const val KEY_SIZE = 256
    /*Encryption*/

    /*DB*/
    const val DB_NAME = "park_loyalty"
    /*DB*/
    /*Regex*/
    val REGEX_HTTP = "http"
    /*Regex*/

    /*SDF*/
    val SDF_MARK_DATE = SimpleDateFormat("MMM dd", Locale.ENGLISH)
    val SDF_TIME_24_HOURS = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    val SDF_TIME_12_HOURS = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val SDF_TIMESTAMP_UTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
    val SDF_DATE_FOR_FILTER_UI = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    val SDF_DATE_TO_SHOW_IN_APP = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH)
    val SDF_DATE_MARK_TIME = SimpleDateFormat("MMM dd, hh:mm a", Locale.ENGLISH)
    /*SDF*/

    //Adapter
    const val VIEW_ITEM = 0
    const val VIEW_LOADING = 1

    //Common
    const val SPLASH_TIME_OUT = 1000

    /*From Screen*/
    const val FROM_WELCOME_SCREEN = "from_welcome_screen"
    const val FOR_CHANGE_LOCATION_HEADER_FOR_RESULT = "change_location_header_for_result"
    const val FOR_CHANGE_VEHICLE_DETAILS_FROM_RESULT_ITEM = "change_vehicle_details_from_result_item"
    /*From Screen*/

    /*Vehicle Mode*/
    const val KEY_SCANNED_TIMESTAMP = "scanned_timestamp"
    const val KEY_SELECTED_POSITION = "selected_position"
    const val KEY_CHECKED_LICENSE_PLATE_RESULT = "checked_license_plate_result"
    const val KEY_FROM = "from"
    const val KEY_FOR_OPERATION = "for_operation"
    const val KEY_LICENSE_PLATE_NUMBER = "license_plate_number"
    const val KEY_VEHICLE_MAKE = "vehicle_make"
    const val KEY_VEHICLE_MODEL = "vehicle_model"
    const val KEY_VEHICLE_COLOR = "vehicle_color"
    const val KEY_OFFICER_NAME = "officer_name"
    const val KEY_BADGE_ID = "badge_id"
    const val KEY_SQUAD = "squad"
    const val KEY_BEAT = "beat"
    const val KEY_BLOCK = "block"
    const val KEY_STREET = "street"
    const val KEY_SIDE = "side"
    const val KEY_SIDE_ID = "side_id"
    const val KEY_TIME_LIMIT_TEXT = "timing"
    const val KEY_TIME_LIMIT_VALUE = "timing_limit"
    const val KEY_VEHICLE_RUN_METHOD = "vehicle_run_method"
    const val KEY_BITMAP_URL = "bitmap_url"
    const val KEY_IS_LOCAL_FILE = "is_local_file"
    const val KEY_FROM_DATE_FOR_API = "from_date_for_api"
    const val KEY_TO_DATE_FOR_API = "to_date_for_api"
    const val KEY_RUN_METHOD = "key_run_method"
    /*Vehicle Mode*/

    const val KEY_VEHICLE_DETAIL_DATA = "vehicle_detail_data"
    const val KEY_VEHICLE_DETAIL_DATA_LIST = "vehicle_detail_data_list"

    const val KEY_SCAN_RESULT = "scan_result"

    /*Request*/
    const val REQUEST_CODE_EDIT_SCANNED_RESULT = 3001
    const val REQUEST_CODE_EDIT_SCANNED_ITEM = 3002
    const val REQUEST_CODE_EDIT_TIME_HEADER_ON_RESULT = 3003
    const val REQUEST_CODE_LOCATION_SETTING = 3004
    /*Request*/

    /*API Constants*/
    const val API_CONSTANT_RUN_TYPE_TIMING = "Timing"
    const val API_CONSTANT_RUN_TYPE_STREET_SWEEPING = "StreetSweeping"
    const val API_CONSTANT_RUN_TYPE_RPP = "RPP"
    const val API_CONSTANT_RUN_TYPE_ALL = "All"

    const val API_CONSTANT_IMAGE_UPLOAD_TYPE_LPR_SESSION_RESULTS = "LPRSessionResults"
    const val API_CONSTANT_SUCCESS_CITATION_FORM = "SUCCESS_CITATION_FORM"
    /*API Constants*/

    /*Screen Constant*/
    const val SCREEN_WELCOME = 1
    const val SCREEN_DASHBOARD = 2
    const val SCREEN_VEHICLE_MODE_FORM = 4
    /*Screen Constant*/



    /*Dialog Title*/
    const val TITLE_GET_WELCOME = "GET_WELCOME"
    const val TITLE_POST_UPDATE_SITE_OFFICER = "POST_UPDATE_SITE_OFFICER"
    const val TITLE_POST_CITATION_DATASET = "POST_CITATION_DATASET"
    const val TITLE_LOGIN = "LOGIN"
    const val TITLE_GET_UPDATE_TIME = "GET_UPDATE_TIME"
    const val TITLE_TIME_RECORD_LAYOUT = "TimingRecordLayout"
    const val TITLE_POST_FORGOT_PASSWORD = "POST_FORGOT_PASSWORD"
    const val TITLE_POST_GET_BAR_COUNT = "POST_GET_BAR_COUNT"
    const val TITLE_POST_GET_COUNT_LINE = "POST_GET_COUNT_LINE"
    const val TITLE_POST_ACTIVITY_UPDATES = "POST_ACTIVITY_UPDATES"
    const val TITLE_POST_VIOLATION_COUNT = "POST_VIOLATION_COUNT"
    const val TITLE_GET_ROUTE_DATA = "GET_ROUTE_DATA"
    /*Dialog Title*/

    /*Release Type*/
    const val RELEASE_TYPE_UAT = "UAT"
    const val RELEASE_TYPE_TRAINING = "TRAINING"
    const val RELEASE_TYPE_PROD = "PROD"
    /*Release Type*/

    /*Setting List Type*/
    const val SETTING_TYPE_LICENSE_PLATE_FORMAT = "LICENSE_PLATE_FORMAT"
    /*Setting List Type*/

    /*Common*/
    const val SPLASH_SCREEN_TIME_OUT = 1000
    const val TEXT_ANDROID = "Android"
    const val TEXT_TABLET = "Tablet"
    const val TEXT_PHONE = "Phone"

    //For decrypt Token
    const val SECRET_KEY = "TheBestSecretKey"
    const val INTERNAL_FOLDER_NAME = "send_bits"
    const val DEFAULT_IMAGE_SIZE = 240
    const val SESSION = "SESSION"

    /*SCREEEN NAME */
    const val FOLDER_CAMERA = "/CameraImages"
    const val FOLDER_POINT_AND_SCAN_IMAGES = "Point And Scan"
    const val FOLDER_CONTINUOUS_SCAN_IMAGES = "Continuous Scan"
    const val FOLDER_APP_NAME = "ParkLoyaltyVehicleMode"


    const val INTERVAL = (5 * 60 * 1000).toLong() //5 min
    const val FASTEST_INTERVAL = (2 * 60 * 1000).toLong() //2 min
    const val SERVICE_TIME: Long = 300 //5 min
    const val LOCATION_KEY = "location_key" //regular
    const val LOCATION_KEY_LOGIN = "location_key_login" //login
    const val LOCATION_KEY_LOGOUT = "location_key_login" //logout
    const val LOCATION_KEY_CITATION = "location_key_citation" //citation
    const val LOCATION_KEY_ACTIVITY = "location_key_activity" //break
    const val SEND_LOCATION_DATA = "SEND_LOCATION_DATA"

    /*Activity Type*/
    const val ACTIVITY_TYPE_LOCATION_UPDATE = "LocationUpdate"
    /*Activity Type*/

    /*Log Type*/
    const val LOG_TYPE_NODE_PORT = "NodePort"
    /*Log Type*/

}