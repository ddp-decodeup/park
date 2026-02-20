package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutRequest
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.LogoutNoteForEquipmentRequest
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.InventoryService
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_NOTE_FOR_NOT_CHECKED_IN_EQUIPMENT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_OFFICER_EQUIPMENT_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOG_EQUIPMENT_CHECKED_IN
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOG_EQUIPMENT_CHECKED_OUT
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InventoryServiceRepository @Inject constructor(
    private val inventoryService: InventoryService
) {

    suspend fun getOfficersEquipmentList(): NewApiResponse<JsonNode> = safeApiCall(
        apiNameTag = API_TAG_NAME_GET_OFFICER_EQUIPMENT_LIST,
        call = { inventoryService.getOfficersEquipmentList() })

    suspend fun logEquipmentCheckedOut(equipmentCheckInOutRequest: EquipmentCheckInOutRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_LOG_EQUIPMENT_CHECKED_OUT,
            call = { inventoryService.logEquipmentCheckedOut(equipmentCheckInOutRequest = equipmentCheckInOutRequest) })

    suspend fun logEquipmentCheckedIn(equipmentCheckInOutRequest: EquipmentCheckInOutRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_LOG_EQUIPMENT_CHECKED_IN,
            call = { inventoryService.logEquipmentCheckedIn(equipmentCheckInOutRequest = equipmentCheckInOutRequest) })

    suspend fun addNoteForNotCheckedInEquipment(logoutNoteForEquipmentRequest: LogoutNoteForEquipmentRequest?): NewApiResponse<JsonNode> =
        safeApiCall(
            apiNameTag = API_TAG_NAME_ADD_NOTE_FOR_NOT_CHECKED_IN_EQUIPMENT,
            call = { inventoryService.addNoteForNotCheckedInEquipment(logoutNoteForEquipmentRequest = logoutNoteForEquipmentRequest) })

}