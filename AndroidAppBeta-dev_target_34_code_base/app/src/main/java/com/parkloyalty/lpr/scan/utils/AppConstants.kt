package com.parkloyalty.lpr.scan.utils

object AppConstants {
    //Citation Types
    const val CITATION_TICKET_TYPE_PARKING = "PARKING"
    const val CITATION_TICKET_TYPE_MUNICIPAL = "MUNICIPAL"
    const val CITATION_TICKET_TYPE_OWNER_BILL = "OWNER_BILL"

    //Images Values
    const val TEMP_IMAGE_FILE_NAME = "IMG_temp"
    const val IMAGE_FILE_EXTENSION_JPG = "jpg"

    //Static Values
    const val STR_TRUE = "true"
    const val STR_FALSE = "false"
    const val STR_YES = "YES"
    const val STR_NO = "NO"

    //Time Interval Constants
    const val TIME_INTERVAL_SPLASH_SCREEN : Long  = 2000
    const val TIME_INTERVAL_700_MS : Long  = 700
    const val TIME_INTERVAL_2_SECONDS : Long  = 2000
    const val TIME_INTERVAL_5_SECONDS : Long  = 5000
    const val TIME_INTERVAL_6_SECONDS : Long  = 6000
    const val TIME_INTERVAL_5_MINUTES = (5 * 60 * 1000).toLong()

    //Default Values
    const val DEFAULT_VALUE_ZERO_DOT_ZERO_STR = "0.0"
    const val DEFAULT_VALUE_ZERO_DOT_ZERO_DBL = 0.0

    //Date Values
    const val DAY_FRIDAY = "Friday"

    //Folder Values
    const val FOLDER_ACTIVITY_IMAGES = "ActivityImages"
}