package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutRequest
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.LogoutNoteForEquipmentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface InventoryService {
    @GET("reports/inventory-reports/officer-equipments")
    suspend fun getOfficersEquipmentList(): Response<JsonNode>

    @POST("reports/inventory-reports/checkout")
    suspend fun logEquipmentCheckedOut(
        @Body equipmentCheckInOutRequest: EquipmentCheckInOutRequest?
    ): Response<JsonNode>

    @POST("reports/inventory-reports/checkin")
    suspend fun logEquipmentCheckedIn(
        @Body equipmentCheckInOutRequest: EquipmentCheckInOutRequest?
    ): Response<JsonNode>

    @POST("reports/inventory-reports/note")
    suspend fun addNoteForNotCheckedInEquipment(
        @Body logoutNoteForEquipmentRequest: LogoutNoteForEquipmentRequest?
    ): Response<JsonNode>
}