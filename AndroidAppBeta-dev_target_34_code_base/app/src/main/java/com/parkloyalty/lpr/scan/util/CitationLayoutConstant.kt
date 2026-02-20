package com.parkloyalty.lpr.scan.util

import android.text.TextUtils
import com.parkloyalty.lpr.scan.extensions.nullSafety

//Component Names
const val COMPONENT_MOTORIST_INFORMATION = "Motorist information"
const val COMPONENT_PERSONAL_INFORMATION = "Personal information"

//Field Names
const val FIELD_NAME_MOTORIST_FIRST_NAME = "first_name"
const val FIELD_NAME_MOTORIST_MIDDLE_NAME = "middle_name"
const val FIELD_NAME_MOTORIST_LAST_NAME = "last_name"
const val FIELD_NAME_MOTORIST_DOB = "date_of_birth"
const val FIELD_NAME_MOTORIST_DL_NUMBER = "dl_number"
const val FIELD_NAME_MOTORIST_BLOCK = "block"
const val FIELD_NAME_MOTORIST_STREET = "street"
const val FIELD_NAME_MOTORIST_CITY = "city"
const val FIELD_NAME_MOTORIST_STATE = "state"
const val FIELD_NAME_MOTORIST_ZIP = "zip"

//Print Order Map Key
const val PRINT_ORDER_MAP_MOTORIST_FIRST_NAME = "motoristFirstNamePrint"
const val PRINT_ORDER_MAP_MOTORIST_MIDDLE_NAME = "motoristMiddleNamePrint"
const val PRINT_ORDER_MAP_MOTORIST_LAST_NAME = "motoristLastNamePrint"
const val PRINT_ORDER_MAP_MOTORIST_DOB = "motoristDobPrint"
const val PRINT_ORDER_MAP_MOTORIST_DL_NUMBER = "motoristDLNumberPrint"
const val PRINT_ORDER_MAP_MOTORIST_ADDRESS_BLOCK = "motoristAddressBlockPrint"
const val PRINT_ORDER_MAP_MOTORIST_ADDRESS_STREET = "motoristAddressStreetPrint"
const val PRINT_ORDER_MAP_MOTORIST_ADDRESS_CITY = "motoristAddressCityPrint"
const val PRINT_ORDER_MAP_MOTORIST_ADDRESS_STATE = "motoristAddressStatePrint"
const val PRINT_ORDER_MAP_MOTORIST_ADDRESS_ZIP = "motoristAddressZipPrint"

const val PRINT_LAYOUT_MAP_KEY_MOTORIST_INFORMATION = "MOTORIST_INFORMATION"


fun String?.getPrintOrder(): Double {
    return if (!this.isNullOrEmpty()) {
        if (this.contains("#")) {
            this.nullSafety()
                .split("#")[0].toDouble()
        } else {
            this.nullSafety().toDouble()
        }
    } else {
        "0".toDouble()
    }


//    return if (!this.isNullOrEmpty() && this.contains("#")) {
//        this.nullSafety()
//            .split("#")[0].toDouble()
//    } else {
//        "0".toDouble()
//    }
}

fun String?.getPrintColumn(): Int {
    return if (!this.isNullOrEmpty() && this.contains("#")) {
        this.nullSafety()
            .split("#")[1].toInt()
    } else {
        "1".toInt()
    }
}

fun String?.getLayoutOrder(): String {
    return if (!this.isNullOrEmpty()) {
        this.nullSafety()
    } else {
        "0"
    }
}

fun String?.getSectionTitle(): String {
    return if (!this.isNullOrEmpty() && this.contains("#") && this.split("#").isNotEmpty()) {
        this.split("#").toTypedArray()[1].nullSafety()
    } else {
        ""
    }
}