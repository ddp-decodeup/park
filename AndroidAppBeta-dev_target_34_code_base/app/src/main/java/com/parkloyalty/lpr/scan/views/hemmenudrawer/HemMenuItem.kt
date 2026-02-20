package com.parkloyalty.lpr.scan.views.hemmenudrawer

import android.content.Context
import com.parkloyalty.lpr.scan.R


sealed class HemMenuItem(
    open val id: Int,
    open val title: String,
    open val contentDescription: String,
    open val iconRes: Int
) {
    data class Option(
        override val id: Int,
        override val title: String,
        override val contentDescription: String,
        override val iconRes: Int
    ) : HemMenuItem(id, title, contentDescription, iconRes)

    data class ExpandableOption(
        override val id: Int,
        override val title: String,
        override val contentDescription: String,
        override val iconRes: Int,
        val subOptions: List<Option>,
        var expanded: Boolean = false
    ) : HemMenuItem(id, title, contentDescription, iconRes)
}

//Menu Options IDs
val HEM_MAIN_MENU_HOME = 1

val HEM_MAIN_MENU_TICKETING = 2
val HEM_MENU_ISSUE = 21
val HEM_MENU_SCAN = 22
val HEM_MENU_SCAN_STICKER = 23
val HEM_MENU_LPR_MODE = 24
val HEM_MENU_MUNICIPAL_CITATION = 25
val HEM_MENU_OWNER_BILL = 26

val HEM_MAIN_MENU_MY_ACTIVITY = 3
val HEM_MENU_GRAPH_VIEW = 31
val HEM_MENU_DAILY_SUMMARY = 32
val HEM_MENU_SUPERVISOR_VIEW = 33
val HEM_MENU_LPR_HITS = 34

val HEM_MAIN_MENU_LOOKUP = 4
val HEM_MENU_CITATION_RESULT = 41
val HEM_MENU_LPR_RESULT = 42

val HEM_MAIN_MENU_GUIDE_ENFORCEMENT = 5
val HEM_MENU_PAY_BY_PLATE = 51
val HEM_MENU_PAY_BY_SPACE = 52
val HEM_MENU_CAMERA_FEED = 53
val HEM_MENU_DIRECTED_ENFORCEMENT = 54
val HEM_MENU_GENETIC_LIST = 55

val HEM_MAIN_MENU_SETTING = 6

val HEM_MAIN_MENU_BROKEN_ASSET_REPORTS = 7

val HEM_MAIN_MENU_HOT_RESTART = 8

val HEM_MAIN_MENU_REPORT_SERVICE = 9
val HEM_MENU_BROKEN_METER_REPORT = 901
val HEM_MENU_CURB_REPORT = 902
val HEM_MENU_EOW_OFFICER_REPORT = 903
val HEM_MENU_PART_EOW_OFFICER_REPORT = 904
val HEM_MENU_EOW_SUPERVISOR_SHIFT_REPORT = 905
val HEM_MENU_SPECIAL_ASSIGNMENT_REPORT = 906
val HEM_MENU_HAND_HELD_MALFUNCTION_REPORT = 907
val HEM_MENU_SIGN_REPORT = 908
val HEM_MENU_VEHICLE_INSPECTION_REPORT = 909
val HEM_MENU_72_HOURS_MARKED_VEHICLE_REPORT = 910
val HEM_MENU_BIKE_INSPECTION_REPORT = 911
val HEM_MENU_72_HOURS_NOTICE_TOW_REPORT = 912
val HEM_MENU_TOW_REPORT = 913
val HEM_MENU_SIGN_OFF_REPORT = 914
val HEM_MENU_NFL_REPORT = 915
val HEM_MENU_HARD_REPORT = 916
val HEM_MENU_AFTER_SEVEN_REPORT = 917
val HEM_MENU_PAY_STATION_REPORT = 918
val HEM_MENU_PAY_SIGNAGE_REPORT = 919
val HEM_MENU_PAY_HOMELESS_REPORT = 920
val HEM_MENU_PAY_SAFETY_REPORT = 921
val HEM_MENU_PAY_TRASH_REPORT = 922
val HEM_MENU_PAY_LOT_REPORT = 923
val HEM_MENU_PAY_LOT_INSPECTION_REPORT = 924
val HEM_MENU_WORK_ORDER_REPORT = 925

val HEM_MAIN_MENU_QR_CODE = 10
val HEM_MENU_GENERATE_QR_CODE = 101
val HEM_MENU_SCAN_QR_CODE = 102

val HEM_MAIN_MENU_PAPER_FEED = 11

