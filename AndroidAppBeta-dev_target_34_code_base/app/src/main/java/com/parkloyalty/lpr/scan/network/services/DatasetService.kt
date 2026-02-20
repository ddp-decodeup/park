package com.parkloyalty.lpr.scan.network.services

import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DatasetService {

    @POST("informatics/get_dataset_no_token")
    suspend fun getShiftListDataset(
        @Body dropdownDatasetRequest: DropdownDatasetRequest?
    ): Response<JsonNode>

    @POST("informatics/get_dataset_no_token")
    suspend fun getHearingListDataset(
        @Body dropdownDatasetRequest: DropdownDatasetRequest?
    ): Response<JsonNode>

    @POST("informatics/get_dataset")
    suspend fun getCitationDataset(
        @Body dropdownDatasetRequest: DropdownDatasetRequest?
    ): Response<JsonNode>
}