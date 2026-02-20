package com.parkloyalty.lpr.scan.util

object DoubangoConstants {
    const val TIMEOUT_READ = 10 * 1000
    const val TIMEOUT_CONNECT = 10 * 1000
    const val LICENSE_TOKEN_FILE_NAME = "ultimateALPR-token.lic"

    const val FROM_POINT_AND_SCAN = "point_and_scan"
    const val FROM_CONTINUOUS_VEHICLE_MODE = "continuous_vehicle_mode"

    const val KEY_IS_LENS_STAB = "is_lens_stab"
    const val KEY_IS_VIDEO_STAB = "is_video_stab"
    const val KEY_IS_FLASH_REQUIRED = "is_flash_required"
    const val KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED = "is_landscape_orientation_required"

    const val KEY_PATH_TO_SAVE_IMAGE = "path_to_save_image"

    const val KEY_VEHICLE_DETAIL_DATA = "vehicle_detail_data"
    const val KEY_VEHICLE_DETAIL_DATA_LIST = "vehicle_detail_data_list"

    const val KEY_SCAN_RESULT = "scan_result"

    const val DOUBANGO_LPR_SCAN_RESULT = 1012

    const val CLASS_ALPR_VIDEO_PARALLEL_ACTIVITY = "com.parkloyalty.lpr.scan.doubangoultimatelpr.AlprVideoParallelActivity"
    const val MODEL_VEHICLE_DETAIL_DATA = "com.parkloyalty.lpr.scan.doubangoultimatelpr.model.VehicleDetailDataModel"
}