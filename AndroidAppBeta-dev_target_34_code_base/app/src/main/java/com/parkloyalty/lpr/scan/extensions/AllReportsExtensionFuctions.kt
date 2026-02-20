package com.parkloyalty.lpr.scan.extensions

import android.content.Context
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.interfaces.Constants

/*All Report*/
fun Context.getTrueFalseList(): Array<String> {
    return this.resources.getStringArray(R.array.list_true_false)
}

fun Context.getYesNoList(): Array<String> {
    return this.resources.getStringArray(R.array.list_yes_no)
}

fun Context.getPassFailedList(): Array<String> {
    return this.resources.getStringArray(R.array.list_pass_fail)
}

fun Context.getActiveInactiveList(): Array<String> {
    return this.resources.getStringArray(R.array.list_active_inactive)
}

fun Context.getAssignedBikeList(): Array<String> {
    val flavorsToGetBikeTypeTwoList = setOf(
        Constants.FLAVOR_TYPE_BURBANK,
    )

    return if (flavorsToGetBikeTypeTwoList.any { BuildConfig.FLAVOR.equals(it, true) }) {
        this.resources.getStringArray(R.array.list_assigned_bike_type_two)
    } else {
        this.resources.getStringArray(R.array.list_assigned_bike_type_one)
    }
}

fun Context.getOutInServiceList(): Array<String> {
    return this.resources.getStringArray(R.array.list_out_in_service)
}

fun Context.getColorList(): Array<String> {
    return this.resources.getStringArray(R.array.list_color)
}

fun Context.getGasList(): Array<String> {
    return this.resources.getStringArray(R.array.list_gas_level)
}

fun Context.getBatteryChargeList(): Array<String> {
    return this.resources.getStringArray(R.array.list_battery_charge)
}

fun Context.getMeterTypeList(): Array<String> {
    return this.resources.getStringArray(R.array.list_meter_type)
}

fun Context.getDeviceList(): Array<String> {
    val flavorsToGetDeviceTypeThreeList = setOf(
        Constants.FLAVOR_TYPE_LAMETRO,
        Constants.FLAVOR_TYPE_CORPUSCHRISTI,
    )
    val flavorsToGetDeviceTypeTwoList = setOf(
        Constants.FLAVOR_TYPE_BURBANK,
        Constants.FLAVOR_TYPE_WESTCHESTER,
    )

    return if (flavorsToGetDeviceTypeThreeList.any { BuildConfig.FLAVOR.equals(it, true) }) {
        this.resources.getStringArray(R.array.list_device_type_three)
    } else if (flavorsToGetDeviceTypeTwoList.any { BuildConfig.FLAVOR.equals(it, true) }) {
        this.resources.getStringArray(R.array.list_device_type_two)
    } else {
        this.resources.getStringArray(R.array.list_device_type_one)
    }
}

fun Context.getRequestToTowList(): Array<String> {
    return this.resources.getStringArray(R.array.list_request_to_tow)
}

fun Context.getUnitNumberList(): Array<String> {
    val flavorsToGetUnitNumberTypeTwoList = setOf(
        Constants.FLAVOR_TYPE_GLENDALE,
        Constants.FLAVOR_TYPE_GLENDALE_POLICE,
    )

    return if (flavorsToGetUnitNumberTypeTwoList.any { BuildConfig.FLAVOR.equals(it, true) }) {
        this.resources.getStringArray(R.array.list_unit_number_type_two)
    } else {
        this.resources.getStringArray(R.array.list_unit_number_type_one)
    }
}

fun Context.getAssignedAreaList(): Array<String> {
    return this.resources.getStringArray(R.array.list_assign_area)
}

fun Context.getDutyHourAreaList(): Array<String> {
    return this.resources.getStringArray(R.array.list_duty_hour_area)
}

fun Context.getReasonForTowList(): Array<String> {
    return this.resources.getStringArray(R.array.list_reason_for_tow)
}

fun Context.getVehicleStoredAtList(): Array<String> {
    return this.resources.getStringArray(R.array.list_vehicle_stored_at)
}

fun Context.getSafetyReportList(): Array<String> {
    return this.resources.getStringArray(R.array.list_safety_report)
}

fun Context.getRequiredServiceList(): Array<String> {
    return this.resources.getStringArray(R.array.list_required_services)
}/*All Report*/

