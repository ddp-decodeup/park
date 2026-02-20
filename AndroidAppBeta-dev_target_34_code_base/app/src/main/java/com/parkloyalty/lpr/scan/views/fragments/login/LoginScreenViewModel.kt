package com.parkloyalty.lpr.scan.views.fragments.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.database.repository.ActivityDaoRepository
import com.parkloyalty.lpr.scan.database.repository.CitationDaoRepository
import com.parkloyalty.lpr.scan.database.repository.DatasetDaoRepository
import com.parkloyalty.lpr.scan.database.repository.InventoryDaoRepository
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.repository.ActivityServiceRepository
import com.parkloyalty.lpr.scan.network.repository.AuthServiceRepository
import com.parkloyalty.lpr.scan.network.repository.CitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.DatasetServiceRepository
import com.parkloyalty.lpr.scan.network.repository.MediaServiceRepository
import com.parkloyalty.lpr.scan.network.repository.MunicipalCitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.SiteVerifyRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImageModelOffline
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.model.SiteOfficerLoginRequest
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.AppUtils.getSiteId
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.TYPE_HEARING_TIME_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.TYPE_SHIFT_LIST
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
class LoginScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val authServiceRepository: AuthServiceRepository,
    private val citationServiceRepository: CitationServiceRepository,
    private val municipalCitationServiceRepository: MunicipalCitationServiceRepository,
    private val mediaServiceRepository: MediaServiceRepository,
    private val datasetServiceRepository: DatasetServiceRepository,
    private val activityServiceRepository: ActivityServiceRepository,
    private val siteVerifyRepository: SiteVerifyRepository,
    private val activityDaoRepository: ActivityDaoRepository,
    private val inventoryDaoRepository: InventoryDaoRepository,
    private val citationDaoRepository: CitationDaoRepository,
    private val datasetDaoRepository: DatasetDaoRepository,
    private val sharedPreference: SharedPref
) : ViewModel() {

    //Login Response StateFlow
    private val _loginResponse = MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val loginResponse: StateFlow<NewApiResponse<JsonNode>> = _loginResponse.asStateFlow()

    //Shift List Response StateFlow
    private val _shiftListResponse = MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val shiftListResponse: StateFlow<NewApiResponse<JsonNode>> = _shiftListResponse.asStateFlow()

    //Hearing Time List Response StateFlow
    private val _hearingListResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val hearingListResponse: StateFlow<NewApiResponse<JsonNode>> =
        _hearingListResponse.asStateFlow()

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

    //Upload Image Response StateFlow
    private val _uploadImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val uploadImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _uploadImageResponse.asStateFlow()

    //Get Citation Number Response StateFlow
    private val _citationNumberResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val citationNumberResponse: StateFlow<NewApiResponse<JsonNode>> =
        _citationNumberResponse.asStateFlow()

    //Get Citation Layout Response StateFlow
    private val _citationLayoutResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val citationLayoutResponse: StateFlow<NewApiResponse<JsonNode>> =
        _citationLayoutResponse.asStateFlow()

    //Get Municipal Citation Layout Response StateFlow
    private val _municipalCitationLayoutResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val municipalCitationLayoutResponse: StateFlow<NewApiResponse<JsonNode>> =
        _municipalCitationLayoutResponse.asStateFlow()

    //Get Activity Layout Response StateFlow
    private val _activityLayoutResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val activityLayoutResponse: StateFlow<NewApiResponse<JsonNode>> =
        _activityLayoutResponse.asStateFlow()

    //Get Timing Layout Response StateFlow
    private val _timingLayoutResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val timingLayoutResponse: StateFlow<NewApiResponse<JsonNode>> =
        _timingLayoutResponse.asStateFlow()

    //Update Time Response StateFlow
    private val _updateTimeResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val updateTimeResponse: StateFlow<NewApiResponse<JsonNode>> = _updateTimeResponse.asStateFlow()

    //Auth Token Refresh Response StateFlow
    private val _authTokenRefreshResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val authTokenRefreshResponse: StateFlow<NewApiResponse<JsonNode>> =
        _authTokenRefreshResponse.asStateFlow()

    //Site Verify Response StateFlow
    private val _siteVerifyResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val siteVerifyResponse: StateFlow<NewApiResponse<JsonNode>> = _siteVerifyResponse.asStateFlow()

    // Citation Booklet StateFlow
    private val _citationBookletList = MutableStateFlow<List<JsonNode>>(emptyList())
    val citationBookletList: StateFlow<List<JsonNode>> = _citationBookletList.asStateFlow()

    // StateFlow for deleteInventoryTables
    private val _deleteInventoryTablesState = MutableStateFlow(false)
    val deleteInventoryTablesState: StateFlow<Boolean> = _deleteInventoryTablesState.asStateFlow()


    //Start Of All API Calls
    fun callGetShiftListAPI() {
        viewModelScope.launch {
            _shiftListResponse.value = NewApiResponse.Loading

            val dropdownDatasetRequest = DropdownDatasetRequest().apply {
                type = TYPE_SHIFT_LIST
                shard = 1
                mSiteId = getSiteId(context)
            }

            val result =
                datasetServiceRepository.getShiftListDataset(dropdownDatasetRequest = dropdownDatasetRequest)
            _shiftListResponse.value = result

            _shiftListResponse.value = NewApiResponse.Idle
        }
    }

    fun callGetHearingListAPI() {
        viewModelScope.launch {
            _hearingListResponse.value = NewApiResponse.Loading

            val dropdownDatasetRequest = DropdownDatasetRequest().apply {
                type = TYPE_HEARING_TIME_LIST
                shard = 1
                mSiteId = getSiteId(context)
            }

            val result =
                datasetServiceRepository.getHearingListDataset(dropdownDatasetRequest = dropdownDatasetRequest)
            _hearingListResponse.value = result

            _hearingListResponse.value = NewApiResponse.Idle
        }
    }

    fun callLoginAPI(siteOfficerLoginRequest: SiteOfficerLoginRequest) {
        viewModelScope.launch {
            _loginResponse.value = NewApiResponse.Loading

            val result =
                authServiceRepository.login(siteOfficerLoginRequest = siteOfficerLoginRequest)
            _loginResponse.value = result

            _loginResponse.value = NewApiResponse.Idle
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

    fun callGetCitationNumberAPI(citationNumberRequest: CitationNumberRequest?) {
        viewModelScope.launch {
            _citationNumberResponse.value = NewApiResponse.Loading
            val result =
                citationServiceRepository.getCitationNumber(citationNumberRequest = citationNumberRequest)
            _citationNumberResponse.value = result
            _citationNumberResponse.value = NewApiResponse.Idle
        }
    }

    fun callGetCitationLayoutAPI() {
        viewModelScope.launch {
            _citationLayoutResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.getCitationLayout()
            _citationLayoutResponse.value = result
            _citationLayoutResponse.value = NewApiResponse.Idle
        }
    }

    fun callGetMunicipalCitationLayoutAPI() {
        viewModelScope.launch {
            _municipalCitationLayoutResponse.value = NewApiResponse.Loading
            val result = municipalCitationServiceRepository.getMunicipalCitationLayout()
            _municipalCitationLayoutResponse.value = result
            _municipalCitationLayoutResponse.value = NewApiResponse.Idle
        }
    }

    fun callGetActivityLayoutAPI() {
        viewModelScope.launch {
            _activityLayoutResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.getActivityLayout()
            _activityLayoutResponse.value = result
            _activityLayoutResponse.value = NewApiResponse.Idle
        }
    }

    fun callGetTimingLayoutAPI() {
        viewModelScope.launch {
            _timingLayoutResponse.value = NewApiResponse.Loading
            val result = citationServiceRepository.getTimingLayout()
            _timingLayoutResponse.value = result
            _timingLayoutResponse.value = NewApiResponse.Idle
        }
    }

    fun callUpdateTimeAPI() {
        viewModelScope.launch {
            _updateTimeResponse.value = NewApiResponse.Loading
            val result = activityServiceRepository.updateTime()
            _updateTimeResponse.value = result
            _updateTimeResponse.value = NewApiResponse.Idle
        }
    }

    fun callAuthTokenRefreshAPI() {
        viewModelScope.launch {
            _authTokenRefreshResponse.value = NewApiResponse.Loading
            val result = authServiceRepository.getAuthTokenRefresh()
            _authTokenRefreshResponse.value = result
            _authTokenRefreshResponse.value = NewApiResponse.Idle
        }
    }

    fun callVerifySite(secretKey: String, userResponseToken: String) {
        viewModelScope.launch {
            _siteVerifyResponse.value = NewApiResponse.Loading
            val result = siteVerifyRepository.verifySite(
                secretKey = secretKey, userResponseToken = userResponseToken
            )
            _siteVerifyResponse.value = result
            _siteVerifyResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls


    //Start Of Database Related Methods
    //Function to save booklet with status
    suspend fun saveBookletWithStatus(citationNumberDataResponse: CitationNumberData?) {
        val citationBookletList = citationNumberDataResponse?.response?.citationBooklet.orEmpty()

        withContext(Dispatchers.IO) {
            try {
                val bookletList = citationDaoRepository.getCitationBooklet(0)
                if (bookletList.isEmpty()) {
                    val bookletModelList = citationBookletList.map {
                        CitationBookletModel().apply {
                            citationBooklet = it.nullSafety()
                            mStatus = 0
                        }
                    }
                    if (bookletModelList.isNotEmpty()) {
                        citationDaoRepository.insertCitationBooklet(bookletModelList)
                    }

                    citationNumberDataResponse?.metadata?.let { meta ->
                        val citationNumberDatabaseModel = CitationNumberDatabaseModel().apply {
                            metadata = meta
                            response = CitationBookletDatabaseModel().apply {
                                latestCitationNumber =
                                    citationNumberDataResponse.response?.latestCitationNumber.nullSafety()
                            }
                        }
                        citationDaoRepository.insertCitationNumberResponse(
                            citationNumberDatabaseModel
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //save Activity layout in Database
    suspend fun saveActivityLayout(mResponse: ActivityLayoutResponse) =
        withContext(Dispatchers.IO) {
            citationDaoRepository.insertActivityLayout(
                mResponse.apply {
                    id = 1
                    uploadStatus = 0
                })
        }

    //save Timing layout in Database
    suspend fun saveTimingLayout(mResponse: TimingLayoutResponse) = withContext(Dispatchers.IO) {
        citationDaoRepository.insertTimingLayout(
            mResponse.apply {
                id = 1
                uploadStatus = 0
            })
    }


    //save Citation Layout Database
    suspend fun saveCitationLayout(mResponse: CitationLayoutResponse) =
        withContext(Dispatchers.IO) {
            citationDaoRepository.insertCitationLayout(
                mResponse.apply { id = 1 })
        }


    //save Citation Layout Database
    suspend fun saveMunicipalCitationLayout(mResponse: MunicipalCitationLayoutResponse) =
        withContext(Dispatchers.IO) {
            citationDaoRepository.insertMunicipalCitationLayout(
                mResponse.apply { id = 1 })
        }


    // Function to delete inventory tables
    suspend fun deleteInventoryTables() {
        withContext(Dispatchers.IO) {
            _deleteInventoryTablesState.value = false
            inventoryDaoRepository.deleteQrCodeInventoryTable()
            inventoryDaoRepository.deleteInventoryToShowTable()
            _deleteInventoryTablesState.value = true
        }
    }

    suspend fun getWelcomeForm(): WelcomeForm? {
        return withContext(Dispatchers.IO) {
            activityDaoRepository.getWelcomeForm()
        }
    }

    suspend fun getCitationInsurance(): List<CitationInsurranceDatabaseModel?>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationInsurance()
        }
    }

    suspend fun updateCitationUploadStatus(uploadStatus: Int, id: String): Unit? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.updateCitationUploadStatus(uploadStatus, id)
        }
    }

    suspend fun deleteTempImagesOfflineWithId(id: String): Unit? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.deleteTempImagesOfflineWithId(id)
        }
    }

    suspend fun getUpdateTimeResponse(): TimestampDatatbase? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getUpdateTimeResponse()
        }
    }

    suspend fun insertUpdatedTime(databaseModel: TimestampDatatbase) {
        withContext(Dispatchers.IO) {
            citationDaoRepository.insertUpdatedTime(databaseModel)
        }
    }

    suspend fun getCitationImageOffline(id: String): List<CitationImageModelOffline>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationImageOffline(id)
        }
    }

    suspend fun getCitationBooklet(status: Int): List<CitationBookletModel>? {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.getCitationBooklet(status)
        }
    }

    suspend fun insertCitationBooklet(databaseModel: List<CitationBookletModel>) {
        return withContext(Dispatchers.IO) {
            citationDaoRepository.insertCitationBooklet(databaseModel)
        }
    }

    //End Of Database Related Methods

    //Start Of Shared Preference Related Methods
    fun clearSharedPreferenceValues() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreference.write(
                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.QRCODE_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            //Clearing Footer 5 Value
            sharedPreference.write(
                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_FONT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_FONT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_FONT)

            //Clearing Shared Pref Value for Lines
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_HEIGHT)

            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_X)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_Y)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT)
            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_HEIGHT)


            sharedPreference.write(
                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_FONT, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.QRCODE_FOR_PRINT_X, ""
            )
            sharedPreference.write(
                SharedPrefKey.QRCODE_FOR_PRINT_Y, ""
            )

            sharedPreference.write(SharedPrefKey.HEADER_1_FOR_PRINT, "")
            sharedPreference.write(SharedPrefKey.HEADER_2_FOR_PRINT, "")

            sharedPreference.write(SharedPrefKey.HEADER_1_FOR_PRINT_X, "")
            sharedPreference.write(SharedPrefKey.HEADER_1_FOR_PRINT_Y, "")
            sharedPreference.write(SharedPrefKey.HEADER_1_FOR_PRINT_FONT, "")

            sharedPreference.write(SharedPrefKey.HEADER_2_FOR_PRINT_X, "")
            sharedPreference.write(SharedPrefKey.HEADER_2_FOR_PRINT_Y, "")
            sharedPreference.write(SharedPrefKey.HEADER_2_FOR_PRINT_FONT, "")

            sharedPreference.write(SharedPrefKey.LOGIN_HEARING_TIME, "")
            sharedPreference.write(SharedPrefKey.LOGIN_HEARING_DATE, "")

            sharedPreference.write(
                SharedPrefKey.APP_LOGO_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.APP_LOGO_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.APP_LOGO_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.APP_LOGO_FOR_PRINT_WIDTH, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.OFFICER_SIGNATURE_FOR_PRINT_WIDTH, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.LPR_IMAGE_FOR_PRINT_X, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.LPR_IMAGE_FOR_PRINT_Y, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.LPR_IMAGE_FOR_PRINT, ""
            ).toString()
            sharedPreference.write(
                SharedPrefKey.LPR_IMAGE_FOR_PRINT_WIDTH, ""
            ).toString()

            sharedPreference.write(
                SharedPrefKey.MOTORIST_INFORMATION_LABEL, ""
            )

            //Bar code
            sharedPreference.write(
                SharedPrefKey.BAR_CODE_FOR_PRINT_X, ""
            )
            sharedPreference.write(
                SharedPrefKey.BAR_CODE_FOR_PRINT_Y, ""
            )
            sharedPreference.write(
                SharedPrefKey.BAR_CODE_FOR_PRINT, ""
            )
            sharedPreference.write(
                SharedPrefKey.BAR_CODE_FOR_PRINT_HEIGHT, ""
            )
        }
    }

    //End Of Shared Preference Related Methods
}

// Optionally observe the state
//viewLifecycleOwner.lifecycleScope.launch {
//    loginScreenViewModel.deleteInventoryTablesState.collectLatest { deleted ->
//        if (deleted) {
//            // Optionally show a message or handle post-delete logic
//        }
//    }
//}