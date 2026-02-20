package com.parkloyalty.lpr.scan.database.repository

import com.parkloyalty.lpr.scan.database.services.InventoryDao
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.databasetable.QrCodeInventoryTable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryDaoRepository @Inject constructor(
    private val inventoryDao: InventoryDao
) {
    // QrCodeInventoryTable methods
    suspend fun insertQrCodeInventoryData(databaseModel: QrCodeInventoryTable) =
        inventoryDao.insertQrCodeInventoryData(databaseModel)

     fun getQrCodeInventoryData(): List<QrCodeInventoryTable?>? =
        inventoryDao.getQrCodeInventoryData()

     fun updateQrCodeInventoryData(isCheckedOut: Int, equipmentId: String) =
        inventoryDao.updateQrCodeInventoryData(isCheckedOut, equipmentId)

     fun deleteQrCodeInventoryTable() =
        inventoryDao.deleteQrCodeInventoryTable()

    // InventoryToShowTable methods
    suspend fun insertInventoryToShowData(databaseModel: InventoryToShowTable) =
        inventoryDao.insertInventoryToShowData(databaseModel)

    suspend fun insertAllInventoryToShowData(databaseModelList: List<InventoryToShowTable>): LongArray =
        inventoryDao.insertAllInventoryToShowData(databaseModelList)

     fun getInventoryToShowData(): List<InventoryToShowTable?>? =
        inventoryDao.getInventoryToShowData()

     fun updateInventoryToShowData(isCheckedOut: Int, equipmentId: String) =
        inventoryDao.updateInventoryToShowData(isCheckedOut, equipmentId)

     fun updateInventoryToShowDataByName(isCheckedOut: Int, equipmentName: String, equipmentValue: String) =
        inventoryDao.updateInventoryToShowDataByName(isCheckedOut, equipmentName, equipmentValue)

     fun deleteInventoryToShowTable() =
        inventoryDao.deleteInventoryToShowTable()
}