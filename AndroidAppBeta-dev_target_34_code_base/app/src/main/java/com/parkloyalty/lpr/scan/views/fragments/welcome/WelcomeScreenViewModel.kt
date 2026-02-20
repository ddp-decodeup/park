package com.parkloyalty.lpr.scan.views.fragments.welcome

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.database.NewSingletonDataSet
import com.parkloyalty.lpr.scan.database.repository.ActivityDaoRepository
import com.parkloyalty.lpr.scan.database.repository.CitationDaoRepository
import com.parkloyalty.lpr.scan.database.repository.DatasetDaoRepository
import com.parkloyalty.lpr.scan.database.repository.InventoryDaoRepository
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.databasetable.QrCodeInventoryTable
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutRequest
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutResponse
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.OfficerEquipmentHistoryResponse
import com.parkloyalty.lpr.scan.network.repository.ActivityServiceRepository
import com.parkloyalty.lpr.scan.network.repository.AuthServiceRepository
import com.parkloyalty.lpr.scan.network.repository.CitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.DatasetServiceRepository
import com.parkloyalty.lpr.scan.network.repository.EventServiceRepository
import com.parkloyalty.lpr.scan.network.repository.InventoryServiceRepository
import com.parkloyalty.lpr.scan.network.repository.MediaServiceRepository
import com.parkloyalty.lpr.scan.network.repository.MunicipalCitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.SiteVerifyRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ActivityLogResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImageModelOffline
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetBlockListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCancelReasonListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarBodyStyleListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarColorListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarMakeListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetDecalYearListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetHolidayCalendarList
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetLotListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMeterListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalBlockListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalCityListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalStateListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalStreetListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalViolationListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetNotesListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetPBCZoneListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetRegulationTimeListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetRemarksListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSettingsListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSideListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSpaceListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetStateListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetStreetListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetTierStemListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetVioListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetViolationListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetVoidAndReissueReasonListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.OfflineCancelCitationModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.ui.login.model.ActivityUpdateRequest
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.UpdateSiteOfficerRequest
import com.parkloyalty.lpr.scan.ui.login.model.UpdateSiteOfficerResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusResponse
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_BITMAP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_FOOTER_BITMAP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_FOOTER_FILE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_HEADER_BITMAP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_HEADER_FILE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_SIGNATURE_FILE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_CITATION_DATASET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_EQUIPMENT_INVENTORY_DATASET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_SIGNATURE_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_TIME_IMAGES
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
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val authServiceRepository: AuthServiceRepository,
    private val citationServiceRepository: CitationServiceRepository,
    private val municipalCitationServiceRepository: MunicipalCitationServiceRepository,
    private val mediaServiceRepository: MediaServiceRepository,
    private val datasetServiceRepository: DatasetServiceRepository,
    private val activityServiceRepository: ActivityServiceRepository,
    private val eventServiceRepository: EventServiceRepository,
    private val inventoryServiceRepository: InventoryServiceRepository,
    private val siteVerifyRepository: SiteVerifyRepository,
    private val activityDaoRepository: ActivityDaoRepository,
    private val inventoryDaoRepository: InventoryDaoRepository,
    private val citationDaoRepository: CitationDaoRepository,
    private val datasetDaoRepository: DatasetDaoRepository,
    private val sharedPreference: SharedPref,
    private val singletonDataSet: NewSingletonDataSet
) : ViewModel() {


    //Loading StateFlow
    private val _isLoading = MutableStateFlow<Boolean?>(false)
    val isLoading: StateFlow<Boolean?> = _isLoading.asStateFlow()

    //Welcome API Response StateFlow
    private val _welcomeResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val welcomeResponse: StateFlow<NewApiResponse<JsonNode>> = _welcomeResponse.asStateFlow()

    //Update Site Officer API Response StateFlow
    private val _updateSiteOfficerResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val updateSiteOfficerResponse: StateFlow<NewApiResponse<JsonNode>> =
        _updateSiteOfficerResponse.asStateFlow()

    //Citation Dataset API Response StateFlow
    private val _citationDatasetResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val citationDatasetResponse: StateFlow<NewApiResponse<JsonNode>> =
        _citationDatasetResponse.asStateFlow()

    //Activity Log Response StateFlow
    private val _activityLogResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val activityLogResponse: StateFlow<NewApiResponse<JsonNode>> =
        _activityLogResponse.asStateFlow()

    //Create Ticket Response StateFlow
    private val _createTicketResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val createTicketResponse: StateFlow<NewApiResponse<JsonNode>> =
        _createTicketResponse.asStateFlow()

    //Create Municipal Citation Ticket Response StateFlow
    private val _createMunicipalCitationTicketResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val createMunicipalCitationTicketResponse: StateFlow<NewApiResponse<JsonNode>> =
        _createMunicipalCitationTicketResponse.asStateFlow()

    //Ticket Cancel Response StateFlow
    private val _ticketCancelResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val ticketCancelResponse: StateFlow<NewApiResponse<JsonNode>> =
        _ticketCancelResponse.asStateFlow()

    //Ticket Status Response StateFlow
    private val _ticketStatusResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val ticketStatusResponse: StateFlow<NewApiResponse<JsonNode>> =
        _ticketStatusResponse.asStateFlow()

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

    //Upload Time Image Response StateFlow
    private val _uploadTimeImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val uploadTimeImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _uploadTimeImageResponse.asStateFlow()

    //Update All Images In Bulk Response StateFlow
    private val _uploadSignatureImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val uploadSignatureImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _uploadSignatureImageResponse.asStateFlow()

    //Download Bitmap Response StateFlow
    private val _downloadBitmapImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val downloadBitmapImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _downloadBitmapImageResponse.asStateFlow()

    //Download Header File Response StateFlow
    private val _downloadHeaderImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val downloadHeaderImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _downloadHeaderImageResponse.asStateFlow()

    //Download Footer File Response StateFlow
    private val _downloadFooterImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val downloadFooterImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _downloadFooterImageResponse.asStateFlow()

    //Get Officer Equipment List Response StateFlow
    private val _officerEquipmentListResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val officerEquipmentListResponse: StateFlow<NewApiResponse<JsonNode>> =
        _officerEquipmentListResponse.asStateFlow()

    //Log Equipment Check Out Response StateFlow
    private val _equipmentCheckedOutResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val equipmentCheckInOutResponse: StateFlow<NewApiResponse<JsonNode>> =
        _equipmentCheckedOutResponse.asStateFlow()

    //Log Equipment Check In Response StateFlow
    private val _equipmentCheckedInResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val equipmentCheckedInResponse: StateFlow<NewApiResponse<JsonNode>> =
        _equipmentCheckedInResponse.asStateFlow()

    //Citation Dataset API Response StateFlow
    private val _equipmentInventoryResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val equipmentInventoryResponse: StateFlow<NewApiResponse<JsonNode>> =
        _equipmentInventoryResponse.asStateFlow()

    //Download File Response StateFlow
    private val _downloadHeaderFileResponse =
        MutableStateFlow<NewApiResponse<ResponseBody>>(NewApiResponse.Idle)
    val downloadHeaderFileResponse: StateFlow<NewApiResponse<ResponseBody>> =
        _downloadHeaderFileResponse.asStateFlow()


    //Download File Response StateFlow
    private val _downloadFooterFileResponse =
        MutableStateFlow<NewApiResponse<ResponseBody>>(NewApiResponse.Idle)
    val downloadFooterFileResponse: StateFlow<NewApiResponse<ResponseBody>> =
        _downloadFooterFileResponse.asStateFlow()


    //Start Of All API Calls
    fun callWelcomeAPI() {
        viewModelScope.launch {
            _welcomeResponse.value = NewApiResponse.Loading
            val result = activityServiceRepository.welcome()
            _welcomeResponse.value = result

            _welcomeResponse.value = NewApiResponse.Idle
        }
    }

    fun callUpdateSiteOfficerAPI(updateSiteOfficerRequest: UpdateSiteOfficerRequest) {
        viewModelScope.launch {
            _updateSiteOfficerResponse.value = NewApiResponse.Loading
            val result =
                activityServiceRepository.updateSiteOfficer(updateSiteOfficerRequest = updateSiteOfficerRequest)
            _updateSiteOfficerResponse.value = result

            _updateSiteOfficerResponse.value = NewApiResponse.Idle
        }
    }

    fun callCitationDatasetAPI(dropdownDatasetRequest: DropdownDatasetRequest) {
        viewModelScope.launch {
            _citationDatasetResponse.value = NewApiResponse.Loading
            val result = datasetServiceRepository.getCitationDataset(
                apiNameTag = API_TAG_NAME_GET_CITATION_DATASET,
                dropdownDatasetRequest = dropdownDatasetRequest
            )
            _citationDatasetResponse.value = result

            _citationDatasetResponse.value = NewApiResponse.Idle
        }
    }

    fun callActivityLogAPI(activityUpdateRequest: ActivityUpdateRequest) {
        viewModelScope.launch {
            val result =
                eventServiceRepository.activityLog(activityUpdateRequest = activityUpdateRequest)
            _activityLogResponse.value = result

            _activityLogResponse.value = NewApiResponse.Idle
        }
    }

    fun callCreateCitationTicketAPI(createTicketRequest: CreateTicketRequest) {
        viewModelScope.launch {
            _createTicketResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.createTicket(createTicketRequest = createTicketRequest)
            _createTicketResponse.value = result

            _createTicketResponse.value = NewApiResponse.Idle
        }
    }

    fun callCreateMunicipalCitationTicketAPI(createMunicipalCitationTicketRequest: CreateMunicipalCitationTicketRequest) {
        viewModelScope.launch {
            _createMunicipalCitationTicketResponse.value = NewApiResponse.Loading
            val result = municipalCitationServiceRepository.createMunicipalCitationTicket(
                createMunicipalCitationTicketRequest = createMunicipalCitationTicketRequest
            )
            _createMunicipalCitationTicketResponse.value = result

            _createMunicipalCitationTicketResponse.value = NewApiResponse.Idle
        }
    }

    fun callTicketCancelAPI(
        id: String?, ticketCancelRequest: TicketCancelRequest?
    ) {
        viewModelScope.launch {
            _ticketCancelResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.cancelTicket(
                id = id, ticketCancelRequest = ticketCancelRequest
            )
            _ticketCancelResponse.value = result

            _ticketCancelResponse.value = NewApiResponse.Idle
        }
    }

    fun callGetTicketStatusAPI(ticketUploadStatusRequest: TicketUploadStatusRequest?) {
        viewModelScope.launch {
            _ticketStatusResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getTicketStatus(ticketUploadStatusRequest = ticketUploadStatusRequest)
            _ticketStatusResponse.value = result

            _ticketStatusResponse.value = NewApiResponse.Idle
        }
    }

    fun callAddTimingAPI(addTimingRequest: AddTimingRequest) {
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

    fun callUploadSignatureImageAPI(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uploadSignatureImageResponse.value = NewApiResponse.Loading

            val result = mediaServiceRepository.uploadImages(
                apiTagName = API_TAG_NAME_UPLOAD_SIGNATURE_IMAGES,
                mIDList = mIDList,
                uploadType = mRequestBodyType,
                image = image
            )

            _uploadSignatureImageResponse.value = result
            _uploadSignatureImageResponse.value = NewApiResponse.Idle
        }
    }

    fun callUploadTimeImageAPI(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uploadTimeImageResponse.value = NewApiResponse.Loading

            val result = mediaServiceRepository.uploadImages(
                apiTagName = API_TAG_NAME_UPLOAD_TIME_IMAGES,
                mIDList = mIDList,
                uploadType = mRequestBodyType,
                image = image
            )

            _uploadTimeImageResponse.value = result
            _uploadTimeImageResponse.value = NewApiResponse.Idle
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

    fun callDownloadHeaderBitmapAPI(
        downloadBitmapRequest: DownloadBitmapRequest?,
    ) {
        viewModelScope.launch {
            _downloadHeaderImageResponse.value = NewApiResponse.Loading

            val result = mediaServiceRepository.downloadBitmap(
                apiNameTag = API_TAG_NAME_DOWNLOAD_HEADER_BITMAP,
                downloadBitmapRequest = downloadBitmapRequest
            )

            _downloadHeaderImageResponse.value = result
            _downloadHeaderImageResponse.value = NewApiResponse.Idle
        }
    }

    fun callDownloadFooterBitmapAPI(
        downloadBitmapRequest: DownloadBitmapRequest?,
    ) {
        viewModelScope.launch {
            _downloadFooterImageResponse.value = NewApiResponse.Loading

            val result = mediaServiceRepository.downloadBitmap(
                apiNameTag = API_TAG_NAME_DOWNLOAD_FOOTER_BITMAP,
                downloadBitmapRequest = downloadBitmapRequest
            )

            _downloadFooterImageResponse.value = result
            _downloadFooterImageResponse.value = NewApiResponse.Idle
        }
    }


    fun callGetOfficerEquipmentList(
    ) {
        viewModelScope.launch {
            _officerEquipmentListResponse.value = NewApiResponse.Loading

            val result = inventoryServiceRepository.getOfficersEquipmentList(
            )

            _officerEquipmentListResponse.value = result
            _officerEquipmentListResponse.value = NewApiResponse.Idle
        }
    }


    fun callLogEquipmentCheckedOutAPI(
        equipmentCheckInOutRequest: EquipmentCheckInOutRequest?
    ) {
        viewModelScope.launch {
            _equipmentCheckedOutResponse.value = NewApiResponse.Loading

            val result = inventoryServiceRepository.logEquipmentCheckedOut(
                equipmentCheckInOutRequest = equipmentCheckInOutRequest
            )

            _equipmentCheckedOutResponse.value = result
            _equipmentCheckedOutResponse.value = NewApiResponse.Idle
        }
    }

    fun callLogEquipmentCheckedInAPI(
        equipmentCheckInOutRequest: EquipmentCheckInOutRequest?
    ) {
        viewModelScope.launch {
            _equipmentCheckedInResponse.value = NewApiResponse.Loading

            val result = inventoryServiceRepository.logEquipmentCheckedIn(
                equipmentCheckInOutRequest = equipmentCheckInOutRequest
            )

            _equipmentCheckedInResponse.value = result
            _equipmentCheckedInResponse.value = NewApiResponse.Idle
        }
    }

    fun callGetEquipmentInventoryAPI(dropdownDatasetRequest: DropdownDatasetRequest) {
        viewModelScope.launch {
            _equipmentInventoryResponse.value = NewApiResponse.Loading
            val result = datasetServiceRepository.getCitationDataset(
                apiNameTag = API_TAG_NAME_GET_EQUIPMENT_INVENTORY_DATASET,
                dropdownDatasetRequest = dropdownDatasetRequest
            )
            _equipmentInventoryResponse.value = result

            _equipmentInventoryResponse.value = NewApiResponse.Idle
        }
    }

    fun callDownloadSignatureFile(url: String) {
        viewModelScope.launch {
            _downloadHeaderFileResponse.value = NewApiResponse.Loading
            val result = mediaServiceRepository.downloadFile(
                apiNameTag = API_TAG_NAME_DOWNLOAD_SIGNATURE_FILE, url = url
            )
            _downloadHeaderFileResponse.value = result

            _downloadHeaderFileResponse.value = NewApiResponse.Idle
        }
    }

    fun callDownloadHeaderFile(url: String) {
        viewModelScope.launch {
            _downloadHeaderFileResponse.value = NewApiResponse.Loading
            val result = mediaServiceRepository.downloadFile(
                apiNameTag = API_TAG_NAME_DOWNLOAD_HEADER_FILE, url = url
            )
            _downloadHeaderFileResponse.value = result

            _downloadHeaderFileResponse.value = NewApiResponse.Idle
        }
    }

    fun callDownloadFooterFile(url: String) {
        viewModelScope.launch {
            _downloadFooterFileResponse.value = NewApiResponse.Loading
            val result = mediaServiceRepository.downloadFile(
                apiNameTag = API_TAG_NAME_DOWNLOAD_FOOTER_FILE, url = url
            )
            _downloadFooterFileResponse.value = result

            _downloadFooterFileResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls

    //Start of Database Operations
    suspend fun getUpdateTimeResponse(): TimestampDatatbase? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getUpdateTimeResponse()
        }
    }

    suspend fun getActivityLayout(): ActivityLayoutResponse? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getActivityLayout()
        }
    }

    suspend fun getCitationInsurrance(): List<CitationInsurranceDatabaseModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationInsurance()
        }
    }

    suspend fun getCitationImageOffline(id: String): List<CitationImageModelOffline>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationImageOffline(id)
        }
    }

    suspend fun getCitationInsurranceUnuploadCitation(): List<CitationInsurranceDatabaseModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationInsuranceUnuploadCitation()
        }
    }

    suspend fun getWelcomeForm(): WelcomeForm? {
        return withContext(Dispatchers.IO) {
            activityDaoRepository.getWelcomeForm()
        }
    }

    suspend fun getOfflineCancelCitation(): List<OfflineCancelCitationModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getOfflineCancelCitation()
        }
    }

    suspend fun getLocalTimingDataList(): List<AddTimingDatabaseModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getLocalTimingDataList()
        }
    }

    suspend fun getTimingImageUsingTimingRecordId(timingRecordId: Int): List<TimingImagesModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getTimingImageUsingTimingRecordId(timingRecordId)
        }
    }

    suspend fun getQrCodeInventoryData(): List<QrCodeInventoryTable?>? {
        return withContext(Dispatchers.IO) {
            inventoryDaoRepository.getQrCodeInventoryData()
        }
    }

    suspend fun getInventoryToShowData(): List<InventoryToShowTable?>? {
        return withContext(Dispatchers.IO) {
            inventoryDaoRepository.getInventoryToShowData()
        }
    }

    suspend fun insertActivityList(databaseModel: WelcomeListDatatbase) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertActivityList(databaseModel)
        }
    }

    suspend fun insertDatasetDecalYearListModel(datasetDecalYearListModel: DatasetDecalYearListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetDecalYearListModel(datasetDecalYearListModel)
        }
    }

    suspend fun insertDatasetCarMakeListModel(datasetCarMakeListModel: DatasetCarMakeListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetCarMakeListModel(datasetCarMakeListModel)
        }
    }

    suspend fun insertDatasetCarColorListModel(datasetCarColorListModel: DatasetCarColorListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetCarColorListModel(datasetCarColorListModel)
        }
    }

    suspend fun insertDatasetStateListModel(datasetStateListModel: DatasetStateListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetStateListModel(datasetStateListModel)
        }
    }

    suspend fun insertDatasetBlockListModel(datasetBlockListModel: DatasetBlockListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetBlockListModel(datasetBlockListModel)
        }
    }

    suspend fun insertDatasetStreetListModel(datasetStreetListModel: DatasetStreetListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetStreetListModel(datasetStreetListModel)
        }
    }

    suspend fun insertDatasetMeterListModel(datasetMeterListModel: DatasetMeterListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetMeterListModel(datasetMeterListModel)
        }
    }

    suspend fun insertDatasetSpaceListModel(datasetSpaceListModel: DatasetSpaceListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetSpaceListModel(datasetSpaceListModel)
        }
    }

    suspend fun insertDatasetCarBodyStyleListModel(datasetCarBodyStyleListModel: DatasetCarBodyStyleListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetCarBodyStyleListModel(datasetCarBodyStyleListModel)
        }
    }

    suspend fun insertDatasetViolationListModel(datasetViolationListModel: DatasetViolationListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetViolationListModel(datasetViolationListModel)
        }

    }

    suspend fun insertDatasetVioListModel(datasetVioListModel: DatasetVioListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetVioListModel(datasetVioListModel)
        }
    }

    suspend fun insertDatasetHolidayCalendarListModel(datasetHolidayCalendarList: DatasetHolidayCalendarList) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetHolidayCalendarListModel(datasetHolidayCalendarList)
        }
    }

    suspend fun insertDatasetSideListModel(datasetSideListModel: DatasetSideListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetSideListModel(datasetSideListModel)
        }
    }

    suspend fun insertDatasetTierStemListModel(datasetTierStemListModel: DatasetTierStemListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetTierStemListModel(datasetTierStemListModel)
        }
    }

    suspend fun insertDatasetNotesListModel(datasetNotesListModel: DatasetNotesListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetNotesListModel(datasetNotesListModel)
        }
    }


    suspend fun insertDatasetRemarksListModel(datasetRemarksListModel: DatasetRemarksListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetRemarksListModel(datasetRemarksListModel)
        }
    }

    suspend fun insertDatasetRegulationTimeListModel(datasetRegulationTimeListModel: DatasetRegulationTimeListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetRegulationTimeListModel(
                datasetRegulationTimeListModel
            )
        }
    }

    suspend fun insertDatasetLotListModel(datasetLotListModel: DatasetLotListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetLotListModel(datasetLotListModel)
        }
    }

    suspend fun insertDatasetSettingsListModel(datasetSettingsListModel: DatasetSettingsListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetSettingsListModel(datasetSettingsListModel)
        }
    }

    suspend fun insertDatasetCancelReasonListModel(datasetCancelReasonListModel: DatasetCancelReasonListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetCancelReasonListModel(datasetCancelReasonListModel)
        }
    }

    suspend fun insertDatasetPBCZoneListModel(datasetPBCZoneListModel: DatasetPBCZoneListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetPBCZoneListModel(datasetPBCZoneListModel)
        }
    }

    suspend fun insertDatasetVoidAndReissueReasonListModel(datasetVoidAndReissueReasonListModel: DatasetVoidAndReissueReasonListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetVoidAndReissueReasonListModel(
                datasetVoidAndReissueReasonListModel
            )
        }
    }

    suspend fun insertDatasetMunicipalViolationListModel(datasetMunicipalViolationListModel: DatasetMunicipalViolationListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetMunicipalViolationListModel(
                datasetMunicipalViolationListModel
            )
        }
    }

    suspend fun insertDatasetMunicipalBlockListModel(datasetMunicipalBlockListModel: DatasetMunicipalBlockListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetMunicipalBlockListModel(
                datasetMunicipalBlockListModel
            )
        }
    }

    suspend fun insertDatasetMunicipalStreetListModel(datasetMunicipalStreetListModel: DatasetMunicipalStreetListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetMunicipalStreetListModel(
                datasetMunicipalStreetListModel
            )
        }
    }

    suspend fun insertDatasetMunicipalCityListModel(datasetMunicipalCityListModel: DatasetMunicipalCityListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetMunicipalCityListModel(
                datasetMunicipalCityListModel
            )
        }
    }

    suspend fun insertDatasetMunicipalStateListModel(datasetMunicipalStateListModel: DatasetMunicipalStateListModel) {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.insertDatasetMunicipalStateListModel(
                datasetMunicipalStateListModel
            )
        }
    }

    suspend fun insertWelcomeForm(welcomeForm: WelcomeForm) {
        return withContext(Dispatchers.IO) {
            activityDaoRepository.insertWelcomeForm(welcomeForm)
        }
    }

    suspend fun insertUpdatedTime(databaseModel: TimestampDatatbase) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertUpdatedTime(databaseModel)
        }
    }

    suspend fun insertInventoryToShowData(databaseModel: InventoryToShowTable) {
        return withContext(Dispatchers.IO) {
            inventoryDaoRepository.insertInventoryToShowData(databaseModel)
        }
    }

    suspend fun insertQrCodeInventoryData(databaseModel: QrCodeInventoryTable) {
        return withContext(Dispatchers.IO) {
            inventoryDaoRepository.insertQrCodeInventoryData(databaseModel)
        }
    }

    suspend fun updateInventoryToShowDataByName(
        isCheckedOut: Int, equipmentName: String, equipmentValue: String
    ) {
        return withContext(Dispatchers.IO) {
            inventoryDaoRepository.updateInventoryToShowDataByName(
                isCheckedOut, equipmentName, equipmentValue
            )
        }
    }

    suspend fun updateCitationBooklet(status: Int, id: String?) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateCitationBooklet(status, id)
        }
    }

    suspend fun updateCitationUploadStatus(uploadStatus: Int, id: String) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateCitationUploadStatus(uploadStatus, id)
        }
    }

    suspend fun updateTimingUploadStatus(uploadStatus: Int, id: Int) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateTimingUploadStatus(uploadStatus, id)
        }
    }


    suspend fun deleteOfflineRescindCitation(deleteCitation: OfflineCancelCitationModel) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteOfflineRescindCitation(deleteCitation)
        }
    }

    suspend fun deleteOfflineCancelCitation(uploadedCitationId: String) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteOfflineCancelCitation(uploadedCitationId)
        }
    }

    suspend fun deleteTimingImagesWithTimingRecordId(timingRecordId: Int) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteTimingImagesWithTimingRecordId(timingRecordId)
        }
    }

    suspend fun deleteSaveCitation(id: String) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteSaveCitation(id)
        }
    }

    suspend fun deleteTempImagesOfflineWithId(id: String) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteTempImagesOfflineWithId(id)
        }
    }

    suspend fun deleteQrCodeInventoryTable() {
        return withContext(Dispatchers.IO) {
            inventoryDaoRepository.deleteQrCodeInventoryTable()
        }
    }

    suspend fun deleteInventoryToShowTable() {
        return withContext(Dispatchers.IO) {
            inventoryDaoRepository.deleteInventoryToShowTable()
        }
    }

    suspend fun deleteDatasetSettingsListModel() {
        return withContext(Dispatchers.IO) {
            datasetDaoRepository.deleteDatasetSettingsListModel()
        }
    }
    //End of Database Operations
}