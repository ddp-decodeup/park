package com.parkloyalty.lpr.scan.utils

import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.extensions.intToBool
import com.parkloyalty.lpr.scan.util.EQUIPMENT_CHECKED_OUT

object InventoryModuleUtil {
    /**
     * This function is used to check if all required equipment is being checked-out for the use or not
     */
    fun isRequiredEquipmentCheckedOut(qrCodeInventoryBannerList: MutableList<InventoryToShowTable?>?): Boolean {
        return qrCodeInventoryBannerList?.firstOrNull { it?.required.intToBool() && it?.checkedOut != EQUIPMENT_CHECKED_OUT } == null
    }

    /**
     * This function is used to check if the QR is already being checked out or not from that category
     */
    fun isScannedEquipmentCategoryAlreadyCheckedOut(
        scannedKey: String,
        qrCodeInventoryBannerList: MutableList<InventoryToShowTable?>?
    ): Boolean {
        return qrCodeInventoryBannerList?.firstOrNull {
            it?.equipmentName.equals(
                scannedKey,
                true
            ) && it?.checkedOut == EQUIPMENT_CHECKED_OUT
        } != null
    }

    /**
     * This function is used to check if the QR is already being checked out or not
     */
    fun isScannedEquipmentAlreadyCheckedOut(
        scannedKey: String,
        scannedValue: String,
        qrCodeInventoryBannerList: MutableList<InventoryToShowTable?>?
    ): Boolean {
        return qrCodeInventoryBannerList?.firstOrNull {
            it?.equipmentName.equals(
                scannedKey,
                true
            ) && it?.equipmentValue.equals(
                scannedValue,
                true
            ) && it?.checkedOut == EQUIPMENT_CHECKED_OUT
        } != null
    }

    /**
     * This function is used to check all equipment is checked-in back or not before logout
     */
    fun isAllEquipmentCheckedIn(qrCodeInventoryBannerList: MutableList<InventoryToShowTable?>?): Boolean {
        return qrCodeInventoryBannerList?.firstOrNull { it?.checkedOut == EQUIPMENT_CHECKED_OUT } == null
    }
}