package com.parkloyalty.lpr.scan.network.repository

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.network.response_handler.safeApiCall
import com.parkloyalty.lpr.scan.network.services.DatasetService
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetRequest
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_HEARING_TIME_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_SHIFT_LIST
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DatasetServiceRepository @Inject constructor(
    private val datasetService: DatasetService
) {
    suspend fun getShiftListDataset(
        dropdownDatasetRequest: DropdownDatasetRequest
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_SHIFT_LIST,
            call = { datasetService.getShiftListDataset(dropdownDatasetRequest = dropdownDatasetRequest) })
    }

    suspend fun getHearingListDataset(
        dropdownDatasetRequest: DropdownDatasetRequest
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = API_TAG_NAME_GET_HEARING_TIME_LIST,
            call = { datasetService.getHearingListDataset(dropdownDatasetRequest = dropdownDatasetRequest) })
    }

    suspend fun getCitationDataset(
        apiNameTag: String, dropdownDatasetRequest: DropdownDatasetRequest
    ): NewApiResponse<JsonNode> {
        return safeApiCall(
            apiNameTag = apiNameTag,
            call = { datasetService.getCitationDataset(dropdownDatasetRequest = dropdownDatasetRequest) })
    }
}