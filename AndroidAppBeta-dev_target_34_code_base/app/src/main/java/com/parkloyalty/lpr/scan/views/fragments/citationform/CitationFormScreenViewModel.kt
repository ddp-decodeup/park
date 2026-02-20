package com.parkloyalty.lpr.scan.views.fragments.citationform

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.database.repository.ActivityDaoRepository
import com.parkloyalty.lpr.scan.database.repository.CitationDaoRepository
import com.parkloyalty.lpr.scan.network.repository.CitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.EventServiceRepository
import com.parkloyalty.lpr.scan.network.repository.MediaServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.DataFromLprRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationIssuranceModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.UnUploadFacsimileImage
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapRequest
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_BITMAP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_HEADER_BITMAP
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
class CitationFormScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val citationServiceRepository: CitationServiceRepository,
    private val mediaServiceRepository: MediaServiceRepository,
    private val activityDaoRepository: ActivityDaoRepository,
    private val citationDaoRepository: CitationDaoRepository,
) : ViewModel() {

    //Last Second Check Response StateFlow
    private val _lastSecondCheckResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val lastSecondCheckResponse: StateFlow<NewApiResponse<JsonNode>> =
        _lastSecondCheckResponse.asStateFlow()

    //Download Header File Response StateFlow
    private val _downloadBitmapImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val downloadBitmapImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _downloadBitmapImageResponse.asStateFlow()

    //Upload All Photos Response StateFlow
    private val _uploadAllImagesResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val uploadAllImagesResponse: StateFlow<NewApiResponse<JsonNode>> =
        _uploadAllImagesResponse.asStateFlow()


    fun getLastSecondCheckAPI(
        zone: String? = null,
        park: String? = null,
        lpNumber: String? = null,
        spaceId: String? = null,
    ) {
        viewModelScope.launch {
            _lastSecondCheckResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.getLastSecondCheck(
                zone = zone, park = park, lpNumber = lpNumber, spaceId = spaceId
            )
            _lastSecondCheckResponse.value = result

            _lastSecondCheckResponse.value = NewApiResponse.Idle
        }
    }

    fun callDownloadBitmapAPI(
        downloadBitmapRequest: DownloadBitmapRequest?,
    ) {
        viewModelScope.launch {
            _downloadBitmapImageResponse.value = NewApiResponse.Loading

            val result = mediaServiceRepository.downloadBitmap(
                apiNameTag = API_TAG_NAME_DOWNLOAD_BITMAP,
                downloadBitmapRequest = downloadBitmapRequest
            )

            _downloadBitmapImageResponse.value = result
            _downloadBitmapImageResponse.value = NewApiResponse.Idle
        }
    }

    fun callUploadAllImagesInBulkAPI(
        data: RequestBody?,
        uploadType: RequestBody?,
        files: List<MultipartBody.Part?>
    ) {
        viewModelScope.launch {
            _uploadAllImagesResponse.value = NewApiResponse.Loading

            val result = mediaServiceRepository.uploadAllImagesInBulk(
                data = data,
                uploadType = uploadType,
                files = files
            )
            _uploadAllImagesResponse.value = result

            _uploadAllImagesResponse.value = NewApiResponse.Idle
        }
    }


    //Start of Database Calls
    suspend fun insertCitationImage(databaseModel: CitationImagesModel) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertCitationImage(databaseModel)
        }
    }

    suspend fun insertFacsimileImageObject(databaseModel: UnUploadFacsimileImage) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertFacsimileImageObject(databaseModel)
        }
    }
    suspend fun insertCitationInsurance(databaseModel: CitationInsurranceDatabaseModel) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertCitationInsurance(databaseModel)
        }
    }

    suspend fun getCitationLayout(): CitationLayoutResponse? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationLayout()
        }
    }

    suspend fun getCitationInsurranceUnuploadCitation(): List<CitationInsurranceDatabaseModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationInsuranceUnuploadCitation()
        }
    }

    suspend fun getCitationWithTicket(citationNumber: String?): CitationInsurranceDatabaseModel? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationWithTicket(citationNumber)
        }
    }

    suspend fun getCitationBooklet(status: Int): List<CitationBookletModel>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationBooklet(status)
        }
    }

    suspend fun getCitationBookletByCitation(id: String?): List<CitationBookletModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationBookletByCitation(id)
        }
    }

    suspend fun getCitationImage(): List<CitationImagesModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationImage()
        }
    }

    suspend fun getCountImages(): Int {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCountImages()
        }
    }

    suspend fun getBookletStatus(id: String?): Int {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getBookletStatus(id)
        }
    }
    suspend fun isImagePathExists(imagePath: String): Int {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.isImagePathExists(imagePath)
        }
    }

    suspend fun updateCitationBooklet(status: Int, id: String?) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateCitationBooklet(status, id)
        }
    }

    suspend fun updateCitationInsurance(model: CitationIssuranceModel?, id: String?) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateCitationInsurance(model, id)
        }
    }

    suspend fun updateCitationUploadStatus(uploadStatus: Int, id: String) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateCitationUploadStatus(uploadStatus, id)
        }
    }

    suspend fun deleteUnUploadCitationImages(imagePath: String) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteUnUploadCitationImages(imagePath)
        }
    }
    suspend fun deleteTempImagesWithId(id: Int) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteTempImagesWithId(id)
        }
    }
    //Start of Database Calls
}