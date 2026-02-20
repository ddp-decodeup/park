package com.parkloyalty.lpr.scan.util.imense

object ImenseConstants {
    //invocation codes for ANPR/ALPR Platform Plus
    const val INVOCATION_USER =
        "3AAdkhd8Bdsug551k87" //Standard user: not allowed to change preferences or view list entries
    const val INVOCATION_ADMIN =
        "Ndkp2kgs7JGs581Hka0" //Privileged user: able to change settings and/or edit list entries

    //return messages from ANPR/ALPR Platform Plus
    const val PT_SCAN_SUCCESS = 99999
    const val PT_INVALID_INVOCATION = 99
    const val PT_LICENSE_MISSING_OR_INVALID = 100
    const val PT_ANPR_NOTONWHITELIST = 101
    const val PT_ANPR_PERMITEXPIRED = 102
    const val PT_ANPR_SCANTIMEOUT = 103
}