package com.parkloyalty.lpr.scan.database.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.databasetable.QrCodeInventoryTable

@Dao
interface InventoryDao {
    @Insert
    suspend fun insertQrCodeInventoryData(databaseModel: QrCodeInventoryTable)

    @Query("SELECT * FROM qr_code_inventory_table")
    fun getQrCodeInventoryData(): List<QrCodeInventoryTable?>?

    @Query("UPDATE qr_code_inventory_table SET is_checked_out=:isCheckedOut WHERE equipment_id=:equipmentId")
    fun updateQrCodeInventoryData(isCheckedOut: Int, equipmentId: String)

    @Query("DELETE FROM qr_code_inventory_table")
    fun deleteQrCodeInventoryTable()

    @Insert
    suspend fun insertInventoryToShowData(databaseModel: InventoryToShowTable)

    @Insert
    suspend fun insertAllInventoryToShowData(databaseModelList: List<InventoryToShowTable>): LongArray

    @Query("SELECT * FROM inventory_to_show_table")
    fun getInventoryToShowData(): List<InventoryToShowTable?>?

    @Query("UPDATE inventory_to_show_table SET is_checked_out=:isCheckedOut WHERE equipment_id=:equipmentId")
    fun updateInventoryToShowData(isCheckedOut: Int, equipmentId: String)

    @Query("UPDATE inventory_to_show_table SET is_checked_out=:isCheckedOut, equipment_value=:equipmentValue WHERE equipment_name=:equipmentName")
    fun updateInventoryToShowDataByName(isCheckedOut: Int, equipmentName: String, equipmentValue: String)

    @Query("DELETE FROM inventory_to_show_table")
    fun deleteInventoryToShowTable()
}