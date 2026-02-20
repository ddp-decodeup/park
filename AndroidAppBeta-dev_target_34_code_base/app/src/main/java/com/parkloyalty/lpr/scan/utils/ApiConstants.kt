package com.parkloyalty.lpr.scan.utils

object ApiConstants {
    //Activity Service API Tags
    const val API_TAG_NAME_WELCOME = "welcome"
    const val API_TAG_NAME_CHECK_SETUP = "check_setup"
    const val API_TAG_NAME_DASHBOARD = "dashboard"
    const val API_TAG_NAME_UPDATE_SITE_OFFICER = "update_site_officer"
    const val API_TAG_NAME_UPDATE_TIME = "update_time"
    const val API_TAG_NAME_GET_COUNT = "get_count"
    const val API_TAG_NAME_ACTIVITY_COUNT = "activity_count"
    const val API_TAG_NAME_VIOLATION_COUNT = "violation_count"
    const val API_TAG_NAME_GET_BAR_COUNT = "get_bar_count"
    const val API_TAG_NAME_GET_LINE_COUNT = "get_line_count"
    const val API_TAG_NAME_GET_ROUTE = "get_route"

    //Auth Service API Tags
    const val API_TAG_NAME_LOGIN = "login"
    const val API_TAG_NAME_FORGOT_PASSWORD = "forgot_password"
    const val API_TAG_NAME_GET_REFRESH_TOKEN = "get_refresh_token"
    const val API_TAG_NAME_GET_SUPERVISOR = "get_supervisor"

    //Citation Service API Tags
    const val API_TAG_NAME_GET_TIMING = "get_timing"
    const val API_TAG_NAME_GET_EXEMPT = "get_exempt"
    const val API_TAG_NAME_GET_SCOFFLAW = "get_scofflaw"
    const val API_TAG_NAME_GET_PERMIT = "get_permit"
    const val API_TAG_NAME_TIMING_MARK_BULK = "timing_mark_bulk"
    const val API_TAG_NAME_GET_DATA_FROM_LPR = "get_data_from_lpr"
    const val API_TAG_NAME_GET_TIMING_MARK = "get_timing_mark"
    const val API_TAG_NAME_GET_GENETIC_HIT ="get_genetic_hit"
    const val API_TAG_NAME_GET_ABANDONED_HIT ="get_abandoned_hit"
    const val API_TAG_NAME_UPDATE_TICKET ="update_ticket"
    const val API_TAG_NAME_UPDATE_MARK ="update_mark"
    const val API_TAG_NAME_GET_METER ="get_meter"
    const val API_TAG_NAME_GET_CITATION_NUMBER ="get_citation_number"
    const val API_TAG_NAME_GET_CITATION_LAYOUT ="get_citation_layout"
    const val API_TAG_NAME_GET_ACTIVITY_LAYOUT ="get_activity_layout"
    const val API_TAG_NAME_GET_TIMING_LAYOUT ="get_timing_layout"
    const val API_TAG_NAME_BOOT_INSTANCE_TICKET ="boot_instance_ticket"
    const val API_TAG_NAME_CREATE_TICKET ="create_ticket"
    const val API_TAG_NAME_BOOT_SUBMIT ="boot_submit"
    const val API_TAG_NAME_CANCEL_TICKET ="cancel_ticket"
    const val API_TAG_NAME_GET_TICKET_STATUS ="get_ticket_status"
    const val API_TAG_NAME_GET_TICKET ="get_ticket"
    const val API_TAG_NAME_ADD_TIMING ="add_timing"
    const val API_TAG_NAME_DRIVE_OFF_TVR ="drive_off_tvr"
    const val API_TAG_NAME_GET_LAST_SECOND_CHECK ="get_last_second_check"
    const val API_TAG_NAME_CHECK_SIMILAR_CITATION ="check_similar_citation"
    const val API_TAG_NAME_ADD_NOTES ="add_notes"
    const val API_TAG_NAME_GET_NOTES ="get_notes"
    const val API_TAG_NAME_ADD_IMAGES ="add_images"

    //Dataset Service API Tags
    const val API_TAG_NAME_GET_SHIFT_LIST = "get_shift_list"
    const val API_TAG_NAME_GET_HEARING_TIME_LIST = "get_hearing_time_list"
    const val API_TAG_NAME_GET_CITATION_DATASET = "get_citation_dataset"
    const val API_TAG_NAME_GET_EQUIPMENT_INVENTORY_DATASET = "get_equipment_inventory_dataset"

    //Event Service API Tags
    const val API_TAG_NAME_ACTIVITY_LOG = "activity_log"
    const val API_TAG_NAME_LPR_START_SESSION = "lpr_start_session"
    const val API_TAG_NAME_LPR_END_SESSION = "lpr_end_session"
    const val API_TAG_NAME_OFFICER_DAILY_SUMMARY = "officer_daily_summary"
    const val API_TAG_NAME_EVENT_LOGIN = "event_login"
    const val API_TAG_NAME_PUSH_EVENT = "push_event"
    const val API_TAG_NAME_INACTIVE_METER_BUZZER = "inactive_meter_buzzer"
    const val API_TAG_NAME_LPR_SCAN_LOGGER = "lpr_scan_logger"

    //Guide Enforcement API Tags
    const val API_TAG_NAME_GET_PAY_BY_PLATE = "get_pay_by_plate"
    const val API_TAG_NAME_GET_PAY_BY_SPACE = "get_pay_by_space"
    const val API_TAG_NAME_GET_PAY_BY_SPACE_DATASET = "get_pay_by_space_dataset"
    const val API_TAG_NAME_GET_CAMERA_VIOLATION_DATASET = "get_camera_violation_dataset"
    const val API_TAG_NAME_GET_GENETIC_HIT_LIST = "get_genetic_hit_list"

    //Inventory Service API Tags
    const val API_TAG_NAME_GET_OFFICER_EQUIPMENT_LIST = "get_officer_equipment_list"
    const val API_TAG_NAME_LOG_EQUIPMENT_CHECKED_OUT = "log_equipment_checked_out"
    const val API_TAG_NAME_LOG_EQUIPMENT_CHECKED_IN = "log_equipment_checked_in"
    const val API_TAG_NAME_ADD_NOTE_FOR_NOT_CHECKED_IN_EQUIPMENT = "add_note_for_not_checked_in_equipment"

    //Location Service API Tags
    const val API_TAG_NAME_LOCATION_UPDATE = "location_update"

    //Media Service API Tags
    const val API_TAG_NAME_GET_FACSIMILE_IMAGES = "get_facsimile_images"
    const val API_TAG_NAME_UPLOAD_ACTIVITY_IMAGE = "upload_activity_image"
    const val API_TAG_NAME_DOWNLOAD_ALERT_FILE = "download_alert_file"
    const val API_TAG_NAME_UPLOAD_IMAGES = "update_images"
    const val API_TAG_NAME_UPLOAD_SIGNATURE_IMAGES = "upload_signature_images"
    const val API_TAG_NAME_UPLOAD_TIME_IMAGES = "upload_time_images"
    const val API_TAG_NAME_UPDATE_ALL_IMAGES = "update_all_images"
    const val API_TAG_NAME_UPDATE_ALL_IMAGES_IN_BULK = "update_all_images_in_bulk"
    const val API_TAG_NAME_UPDATE_CSV = "update_csv"
    const val API_TAG_NAME_STATIC_UPDATE_CSV = "static_update_csv"
    const val API_TAG_NAME_UPLOAD_DOC_TO_VERIFICATION = "upload_doc_to_verification"
    const val API_TAG_NAME_UPDATE_DOC_TO_VERIFICATION = "update_doc_to_verification"
    const val API_TAG_NAME_DOWNLOAD_BITMAP = "download_bitmap"
    const val API_TAG_NAME_DOWNLOAD_HEADER_BITMAP = "download_header_bitmap"
    const val API_TAG_NAME_DOWNLOAD_FOOTER_BITMAP = "download_footer_bitmap"
    const val API_TAG_NAME_DOWNLOAD_FILE = "download_file"
    const val API_TAG_NAME_UPLOAD_TEXT_FILE = "upload_text_file"
    const val API_TAG_NAME_DOWNLOAD_SIGNATURE_FILE = "download_signature_file"
    const val API_TAG_NAME_DOWNLOAD_HEADER_FILE = "download_header_file"
    const val API_TAG_NAME_DOWNLOAD_FOOTER_FILE = "download_footer_file"

    //Municipal Citation Service API Tags
    const val API_TAG_NAME_GET_MUNICIPAL_CITATION_LAYOUT = "get_municipal_citation_layout"
    const val API_TAG_NAME_CREATE_MUNICIPAL_CITATION_TICKET = "create_municipal_citation_ticket"
    const val API_TAG_NAME_GET_MUNICIPAL_CITATION_TICKET_HISTORY = "get_municipal_citation_ticket_history"

    //Report Service API Tags
    const val API_TAG_NAME_BROKEN_ASSETS_REPORT = "broken_meter_submit"
    const val API_TAG_NAME_BROKEN_METER_REPORT_SUBMIT = "broken_meter_report_submit"
    const val API_TAG_NAME_SIGN_OFF_REPORT = "sign_off_report"
    const val API_TAG_NAME_TOW_REPORT = "tow_report"
    const val API_TAG_NAME_NOTICE_TO_TOW_REPORT = "notice_to_tow_report"
    const val API_TAG_NAME_CURB_REPORT = "curb_report"
    const val API_TAG_NAME_HANDHELD_MALFUNCTIONS = "handheld_malfunctions"
    const val API_TAG_NAME_SIGN_REPORTS = "sign_reports"
    const val API_TAG_NAME_VEHICLE_INSPECTION = "vehicle_inspection"
    const val API_TAG_NAME_72_HOUR_MARKED_VEHICLE = "72_hour_marked_vehicle"
    const val API_TAG_NAME_BIKE_INSPECTION = "bike_inspection"
    const val API_TAG_NAME_SUPERVISOR = "supervisor"
    const val API_TAG_NAME_SPECIAL_ASSIGNMENT = "special_assignment"
    const val API_TAG_NAME_FULL_TIME = "full_time"
    const val API_TAG_NAME_PART_TIME = "part_time"
    const val API_TAG_NAME_NFL_SPECIAL_ASSIGNMENT = "nfl_special_assignment"
    const val API_TAG_NAME_LOT_COUNT_VIO_RATE = "lot_count_vio_rate"
    const val API_TAG_NAME_HARD_SUMMER_FESTIVAL = "hard_summer_festival"
    const val API_TAG_NAME_AFTER_SEVEN_PM_REPORT = "after_seven_pm_report"
    const val API_TAG_NAME_PAY_STATION_REPORT = "pay_station_report"
    const val API_TAG_NAME_PAY_SIGNAGE_REPORT = "pay_signage_report"
    const val API_TAG_NAME_HOMELESS_REPORT = "homeless_report"
    const val API_TAG_NAME_WORK_ORDER_REPORT = "work_order_report"
    const val API_TAG_NAME_SAFETY_ISSUE_REPORT = "safety_issue_report"
    const val API_TAG_NAME_TRASH_LOT_REPORT = "trash_lot_report"
    const val API_TAG_NAME_LOT_INSPECTION = "lot_inspection"



    //Site Verification API Tags
    const val API_TAG_NAME_VERIFY_SITE = "verify_site"

    /*API SUB TAG NAME*/
    const val API_SUB_TAG_NAME_GET_LPR_DATA = "get_lpr_data"
    const val API_SUB_TAG_NAME_GET_CAMERA_RAW_FEED_DATA = "get_camera_raw_feed_data"
    const val API_SUB_TAG_NAME_GET_SCOFFLAW_DATA = "get_scofflaw_data"
    const val API_SUB_TAG_NAME_GET_EXEMPT_DATA = "get_exempt_data"
    const val API_SUB_TAG_NAME_GET_STOLEN_DATA = "get_stolen_data"
    const val API_SUB_TAG_NAME_GET_PAYMENT_DATA = "get_payment_data"
    const val API_SUB_TAG_NAME_GET_PERMIT_DATA = "get_permit_data"
    const val API_SUB_TAG_NAME_GET_CITATION_DATA = "get_citation_data"
    const val API_SUB_TAG_NAME_GET_CITATION_T2_DATA = "get_citation_t2_data"
    const val API_SUB_TAG_NAME_GET_MAKE_MODEL_COLOR_DATA = "get_make_model_color_data"
    /*API SUB TAG NAME*/


    const val STATIC_USER_ID_FORGOT_PASSWORD = "lprscan_user"
    const val STATIC_EMAIL_ADDRESS_FORGOT_PASSWORD = "lprscan@gmail.com"

    const val TYPE_SHIFT_LIST = "ShiftList"
    const val TYPE_HEARING_TIME_LIST = "HearingTimeList"
    const val TYPE_ACTIVITY_LAYOUT = "ActivityLayout"
    const val TYPE_TIME_RECORD_LAYOUT = "TimingRecordLayout"

    const val MULTIPART_CONTENT_TYPE_CITATION_IMAGES = "CitationImages"


    const val TICKET_CATEGORY_HONOR_BILL_TICKET = "honor_bill_notice"
    const val TICKET_CATEGORY_MUNICIPAL_TICKET = "municipal_ticket"
    const val TICKET_TYPE_WARNING = "Warning"
    const val TICKET_STATUS_VALID = "Valid"

    const val ACTIVITY_TYPE_ACTIVITY_UPDATE = "ActivityUpdate"
}