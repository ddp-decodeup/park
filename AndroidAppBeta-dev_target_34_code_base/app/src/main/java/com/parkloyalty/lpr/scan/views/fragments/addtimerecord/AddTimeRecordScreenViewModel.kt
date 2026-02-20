package com.parkloyalty.lpr.scan.views.fragments.addtimerecord

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.database.NewSingletonDataSet
import com.parkloyalty.lpr.scan.database.repository.CitationDaoRepository
import com.parkloyalty.lpr.scan.network.repository.CitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.MediaServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.boot.model.BootMetadataRequest
import com.parkloyalty.lpr.scan.ui.boot.model.BootRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_IMAGES
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddTimeRecordScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val citationServiceRepository: CitationServiceRepository,
    private val mediaServiceRepository: MediaServiceRepository,
    private val citationDaoRepository: CitationDaoRepository,
    private val singletonDataSet: NewSingletonDataSet

) : ViewModel() {

    //Add Timing Response StateFlow
    private val _addTimingResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val addTimingResponse: StateFlow<NewApiResponse<JsonNode>> =
        _addTimingResponse.asStateFlow()

    //Upload Image Response StateFlow
    private val _uploadImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val uploadImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _uploadImageResponse.asStateFlow()

    //Start Of All API Calls
    fun callAddTimingAPI(addTimingRequest: AddTimingRequest?) {
        viewModelScope.launch {
            _addTimingResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.addTiming(addTimingRequest = addTimingRequest)
            _addTimingResponse.value = result

            _addTimingResponse.value = NewApiResponse.Idle
        }
    }

    fun callUploadImageAPI(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uploadImageResponse.value = NewApiResponse.Loading

            val result = mediaServiceRepository.uploadImages(
                apiTagName = API_TAG_NAME_UPLOAD_IMAGES,
                mIDList = mIDList,
                uploadType = mRequestBodyType,
                image = image
            )

            _uploadImageResponse.value = result
            _uploadImageResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls

    //Start of Database Calls
    suspend fun getTimingLayout() : TimingLayoutResponse? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getTimingLayout()
        }
    }

    suspend fun getLastIDFromTimingData() : Int? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getLastIDFromTimingData()
        }
    }

    suspend fun insertTimingImage(databaseModel: TimingImagesModel) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertTimingImage(databaseModel)
        }
    }

    suspend fun insertTimingData(databaseModel: AddTimingDatabaseModel) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertTimingData(databaseModel)
        }
    }

    suspend fun updateTimingUploadStatus(uploadStatus: Int, id: Int) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateTimingUploadStatus(uploadStatus, id)
        }
    }

    suspend fun deleteTimingImagesWithTimingRecordId(timingRecordId: Int) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteTimingImagesWithTimingRecordId(timingRecordId)
        }
    }

    //End of Database Calls

    suspend fun getWelcomeDbObject(): WelcomeListDatatbase? {
        return withContext(Dispatchers.IO) {
            singletonDataSet.getWelcomeDbObject()
        }
    }

}