package com.parkloyalty.lpr.scan.extensions

import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.ActivityStat
import com.parkloyalty.lpr.scan.ui.login.model.DeviceResponseItem

fun Array<String?>.getIndexOf(value: String): Int {
    return this.indexOfFirst { it.equals(value,true) }
}

fun List<DatasetResponse>.getIndexOfName(value: String): Int {
    return this.indexOfFirst { it.name.equals(value,true) }
}
fun List<DatasetResponse>.getIndexOfColor(value: String): Int {
    return this.indexOfFirst { it.description.equals(value,true) }
}

fun List<DatasetResponse>.getIndexOfMakeText(value: String): Int {
    return this.indexOfFirst { it.makeText.equals(value,true) }
}

fun List<DatasetResponse>.getIndexOfModelText(value: String): Int {
    return this.indexOfFirst { it.model.equals(value,true) }
}

fun List<ActivityStat>.getIndexOfActivity(value: String): Int {
    return this.indexOfFirst { it.activity.equals(value,true) }
}

fun List<DeviceResponseItem>.getIndexOfDeviceFriendlyName(value: String): Int {
    return this.indexOfFirst { it.friendlyName.equals(value,true) }
}

fun List<DatasetResponse>.getIndexOfAgency(value: String): Int {
    return this.indexOfFirst { it.agency_name.equals(value,true) }
}

fun List<DatasetResponse>.getIndexOfLocation(value: String): Int {
    return this.indexOfFirst { it.location.equals(value,true) }
}

fun List<DatasetResponse>.getIndexOfStateName(value: String): Int {
    return this.indexOfFirst { it.state_name.equals(value,true) }
}

fun List<DatasetResponse>.getIndexOfCityZone(value: String): Int {
    return this.indexOfFirst { it.zoneName.equals(value,true) }
}

fun List<DatasetResponse>.getIndexOfRegulation(value: String): Int {
    return this.indexOfFirst { it.regulation.equals(value,true) }
}