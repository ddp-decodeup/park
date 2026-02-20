package com.parkloyalty.lpr.scan.network

import com.parkloyalty.lpr.scan.util.LogUtil

object DynamicAPIPath {
    const val regenerateCallWithCode = "/profile-services/customers/regenerate-otp-dial"
    const val uploadPublicKey = "/shared/encryption-handler/device-key/upload"
    const val POST_LOGIN = "auth/site_officer_login"
    const val POST_PUSH_EVENT = "events/push_event"
    const val GET_WELCOME = "screens/welcome_page"
    const val GET_CHECK_SETUP = "analytics/mobile/get_checksetup"
    const val GET_DASHBOARD = "screens/dashboard_screen"
    const val RESEND_otp = "resend-otp"
    const val profile_update = "profile-update"
    const val user_info = "user-info"
    const val document_upload = "document-upload"
    const val USER_CHARGE = "user-charges"
    const val DOCUMENT_UPDATE = "document-update"
    const val POST_FORGOT_PASSWORD = "forgot-password"
    const val GET_TIMING = "get_timing_data_from_lpr"
    const val GET_EXEMPT = "get_exempt_data_from_lpr"
    const val GET_SOFFLAW = "get_scofflaw_data_from_lpr"
    const val GET_PERMIT = "get_permit_data_from_lpr"
    const val GET_DATA_FROM_LPR = "informatics/get_data_from_lpr"
    const val GET_METER = "get_citation_data_from_lpr"
    const val POST_UPDATE_SITE_OFFICER = "l2-onboarder/update_site_officer"
    const val POST_ADD_DATASET = "add_dataset"
    const val PATCH_TIMING_MARK_BULK = "parking-timing/mark/bulk"

    //PHASE 2 ------------------------------------------------>
    const val POST_CITATION_NUMBER = "citations/issue_citation_book"
    const val POST_CITATION_DATASET = "informatics/get_dataset"
    const val POST_CITATION_DATASET_LOGIN_PAGE = "informatics/get_dataset_no_token"
    const val GET_REFRESH_AUTH_TOKEN = "auth/refresh/officer"

    //    public static final String GET_CITATION_LAYOUT = "citations-issuer/citation_layout";
    const val GET_CITATION_LAYOUT = "templates/mobile/primary_template?template_type="
    const val GET_MUNICIPAL_CITATION_LAYOUT = "templates/mobile/primary_template?template_type=municipal_citation"
    const val POST_CREATE_TICKET = "citations-issuer/ticket"
    const val GET_TICKET = "citations-issuer/ticket?"
    const val POST_ADD_TIMING = "parking-timing/mark"
    const val POST_EVENT_LOGGER = "event-logger/location-update"
    const val POST_INACTIVE_METER_BUZZER = "pull-vendors/get_meter_communications"
    const val GET_COUNT = "operations/mobile/get_counts"
    const val GET_UPDATE_TIME = "informatics/get_dataset_last_updated_timestamps"
    const val POST_IMAGE = "static_file/bulk_upload"
    const val POST_TEXT = "static_file/bulk_upload"
    const val POST_CSV = "informatics/lpr_session_results"
    const val POST_CANCEL = "citations-issuer/ticket/"
    const val POST_CANCELLATION_REQUEST = "citations-issuer/cancellation-requests"
//    const val POST_TICKET_UPLOADE_STATUS = "citations-issuer/check_citation_upload"
    const val POST_TICKET_UPLOADE_STATUS_META = "citations-issuer/check_citation_upload_metadata"
    const val POST_ACTIVITY_UPDATES = "analytics/mobile/get_activity_updates_by_officer?"
    const val POST_VIOLATION_COUNT = "analytics/mobile/get_violation_counts_by_officer?"
    const val POST_GET_COUNT_LINE = "analytics/mobile/get_array_counts?timeline=daily"
    const val POST_GET_BAR_COUNT = "analytics/mobile/get_counts?"
    const val POST_LPR_SCAN_LOGGER = "event-logger/lpr-scan"
    const val POST_UPDATE_TICKET = "citations-issuer/ticket/"
    const val POST_UPDATE_MARK = "parking-timing/mark/"
    const val POST_ACTIVITY_LOG = "event-logger/activity-update"
    const val GET_TIMING_MARK = "parking-timing/mark?"
    const val GET_GENETEC_HIT = "parking-timing/mark?"
    const val GET_abandoned_HIT = "parking-timing/abandonVehicle?"
    const val GET_ROUTE_DATA = "location-update/updates?"
    const val POST_DOWNLOAD_FILE = "static_file/download_files"
    const val GET_PAY_BY_PLATE = "analytics/mobile/pay_by_plate?"
    const val GET_PAY_BY_SPACE = "pull-vendors/payments_by_space?"
//    const val GET_GENETIC_HIT_LIST = "vehicle-camera-job/get-genetic-hit-list?"
    const val GET_PAY_BY_SPACE_DATA_SET = "pull-vendors/space_location_list?"
    const val GET_CAMERA_VIOLATION_DATA_SET = "violations/unlinked_feeds?"
    const val GET_GENETIC_HIT_LIST = "vehicle-camera-job/get-genetic-hit-list?"//http://localhost:8000/vehicle-camera-job/get-genetic-hit-list?type_of_hit=Scofflaw&page=1&limit=100%lpr_number=ABC123
    const val GET_LAST_SECOND_CHECK = "analytics/mobile/last_second_check?"
    const val PATCH_DRIVE_OFF_TVR = "citations-issuer/post_ticket_issuance/"
    const val SIMILAR_CITATION_CHECK = "citations-issuer/citation_similarity_check"
    const val SUMIT_BOOT = "dispatch/"
    //const val SUBMIT_BOOT_INSTANCE_TICKET = "boot-lifecycle/boot-instance"
    const val SUBMIT_BOOT_INSTANCE_TICKET = "boot-lifecycle/boot-ticket"
    const val BROKEN_METER = "maintenance/checks"
    const val GET_OFFICER_DAILY_SUMMARY = "analytics/officer_daily_summary"
    const val POST_LPR_START_SESSION = "lpr_session/start"
    const val POST_LPR_END_SESSION = "lpr_session/end"
    const val GET_DOWNLOAD_ALERT_FILE = "continuouslpr/datasets_link"
    const val GET_ADD_NOTES = "citations-issuer/ticket/"
    const val POST_ADD_IMAGE = "citations-issuer/ticket/"
    const val GET_NOTES = "citations-issuer/ticket/"
    const val GET_SUPERVISOR = "analytics/supervisor_daily_summary?"
    const val GET_CAMERA_GUIDED_ENFORCEMENT = "violations/camera-feed?"

    const val POST_BROKEN_METER_REPORT = "reports/broken-meter-reports"
    const val POST_SIGN_OFF_REPORT = "reports/sign-off-reports"
    const val POST_NOTICE_TO_TOW_REPORT = "reports/seventytwo-hour-tow-notice-reports"
    const val POST_CURB_REPORT = "reports/curbs"
    const val POST_FULLTIME_REPORT = "reports/full-time-reports"
    const val POST_PARTTIME_REPORT = "reports/part-time-reports"
    const val POST_TOW_REPORT = "reports/tow-reports"
    const val POST_HAND_HELD_MALFUNCTIONS_REPORT = "reports/hand-held-malfunctions"
    const val POST_SIGN_REPORT = "reports/sign-reports"
    const val POST_VEHICLE_INSPECTIONS_REPORT = "reports/vehicle-inspections"
    const val POST_HOUR_MARKED_VEHICLE_REPORT = "reports/seventytwo-hour-marked-vehicles"
    const val POST_BIKE_INSPECTIONS_REPORT = "reports/bike-inspections"
    const val POST_NFL_REPORT = "reports/nfl-special-assignment"
    const val POST_HARD_SUMMER_REPORT = "reports/hard-summer-festival"
    const val POST_AFTER_SEVEN_REPORT = "reports/after-seven-reports"
    const val POST_PAY_STATION_REPORT = "reports/pay-station-reports"
    const val POST_SIGNAGE_REPORT = "reports/signage-reports"
    const val POST_HOMELESS_REPORT = "reports/homeless-reports"
    const val POST_LOT_INSPECTION_REPORT = "reports/lot-inspection-reports"
    const val POST_LOT_COUNT_VIO_RATE_REPORT = "reports/lot-count-vio-rate-reports"
    const val GET_STATIC_FASCIMILE_IMAGE  = "citations-issuer/ticket?page=1&limit=3&ticket_no="
    const val POST_SUPERVISOR_REPORT  = "reports/supervisor-reports"
    const val POST_WORK_ORDER_REPORT  = "reports/work-order-reports"
    const val POST_SAFETY_ISSUE_REPORT  = "reports/safety-reports"
    const val POST_TRACE_LOT_REPORT  = "reports/trash-lot-maintenance-reports"
    const val POST_SPECIAL_ASSIGNMENT_REPORT  = "reports/special-assignment-reports"
    const val POST_INVENTORY = "reports/inventory-reports"
    const val POST_UPDATE_ACTIVITY_IMAGE  = "analytics/mobile/update_activity_images"

    /*Inventory Management System*/
    const val POST_EQUIPMENT_CHECK_OUT = "reports/inventory-reports/checkout"
    const val POST_EQUIPMENT_CHECK_IN = "reports/inventory-reports/checkin"
    const val POST_LOG_NOTE_BEFORE_LOGOUT_WITH_EQUIPMENT = "reports/inventory-reports/note"
    const val GET_OFFICERS_EQUIPMENTS = "reports/inventory-reports/officer-equipments"
    /*Inventory Management System*/

    /*Municipal Citation*/
    const val POST_CREATE_MUNICIPAL_CITATION_TICKET = "citations-issuer/ticket-municipal"
    const val GET_MUNICIPAL_CITATION_TICKET_HISTORY = "citations-issuer/ticket"
    /*Municipal Citation*/


    /**
     * Make dynamic url method
     * there are some of the api is no need to dynamic url but rest of the api are dynamic url
     * This method add country code between the base url and rest path
     */
    fun makeDynamicEndpointAPIGateWay(baseUrl: String, path: String): String {
        var finalEndPoint = ""
        val tempBaseURL = ""
        try {
            finalEndPoint = baseUrl + path
            LogUtil.printLog("Base URL : ", tempBaseURL)
            LogUtil.printLog("path : ", path)
            LogUtil.printLog(" final end point : ", finalEndPoint)
        } catch (e: Exception) {
            e.printStackTrace()
            return finalEndPoint
        }
        return finalEndPoint
    }
}