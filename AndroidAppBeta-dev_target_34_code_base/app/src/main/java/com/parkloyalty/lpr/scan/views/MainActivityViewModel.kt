package com.parkloyalty.lpr.scan.views

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerRequest
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import com.parkloyalty.lpr.scan.database.DbOperationStatus
import com.parkloyalty.lpr.scan.database.NewSingletonDataSet
import com.parkloyalty.lpr.scan.database.repository.ActivityDaoRepository
import com.parkloyalty.lpr.scan.database.repository.CitationDaoRepository
import com.parkloyalty.lpr.scan.database.repository.DatasetDaoRepository
import com.parkloyalty.lpr.scan.database.repository.InventoryDaoRepository
import com.parkloyalty.lpr.scan.databasetable.ActivityImageTable
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.toBooleanFromYesNo
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_HEADER_FOOTER_IN_FACSIMILE
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_INVENTORY_MODULE
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_IS_GENERATE_QR_CODE_VISIBILE
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_IS_GENERATE_QR_CODE_VISIBLE
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_IS_METER_BUZZER_ACTIVE
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.LogoutNoteForEquipmentRequest
import com.parkloyalty.lpr.scan.network.repository.CitationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.EventServiceRepository
import com.parkloyalty.lpr.scan.network.repository.InventoryServiceRepository
import com.parkloyalty.lpr.scan.network.repository.LocationServiceRepository
import com.parkloyalty.lpr.scan.network.repository.MediaServiceRepository
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ActivityImageUploadRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.UnUploadFacsimileImage
import com.parkloyalty.lpr.scan.ui.login.model.ActivityUpdateRequest
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageRequest
import com.parkloyalty.lpr.scan.util.DATASET_BLOCK_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_BODY_STYLE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_COLOR_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_MAKE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_MODEL_LIST
import com.parkloyalty.lpr.scan.util.DATASET_DECAL_YEAR_LIST
import com.parkloyalty.lpr.scan.util.DATASET_LOT_LIST
import com.parkloyalty.lpr.scan.util.DATASET_METER_LIST
import com.parkloyalty.lpr.scan.util.DATASET_NOTES_LIST
import com.parkloyalty.lpr.scan.util.DATASET_REGULATION_TIME_LIST
import com.parkloyalty.lpr.scan.util.DATASET_REMARKS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SIDE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SPACE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_STATE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_STREET_LIST
import com.parkloyalty.lpr.scan.util.DATASET_TIER_STEM_LIST
import com.parkloyalty.lpr.scan.util.DATASET_VIOLATION_LIST
import com.parkloyalty.lpr.scan.util.DATASET_VIO_LIST
import com.parkloyalty.lpr.scan.util.SCANNER_TYPE_DOUBANGO
import com.parkloyalty.lpr.scan.util.SCANNER_TYPE_IMENSE
import com.parkloyalty.lpr.scan.util.SETTING_MAX_IMAGES_COUNT
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_IMAGES
import com.parkloyalty.lpr.scan.utils.AppConstants.STR_YES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import kotlin.collections.firstOrNull

data class ToolbarState(
    val showToolbar: Boolean = false,
    val showToolbarLogo: Boolean = true,
    val showBackButton: Boolean = true,
    val showHemMenu: Boolean = true,
    val officerName: String = "",
    val enableHemMenuDrawer: Boolean = true
)

// Define all possible actions Activity should handle
sealed class MainActivityAction {
    data class ShowLoader(val show: Boolean) : MainActivityAction()
    data class EventSaveActivityLogData(val bannerList: MutableList<TimingImagesModel?>? = ArrayList(), val activityId: String) : MainActivityAction()
    object EventProcessUnUploadedActivityImages : MainActivityAction()
    object EventProcessLogout : MainActivityAction()
    object EventSetupHemMenu : MainActivityAction()
    object EventUploadAPILogTextFile : MainActivityAction()
    data class EventCallLocationAPI(val locUpdateRequest: LocUpdateRequest) : MainActivityAction()
    data class EventActivityLogAPI(val mValue: String, val isDisplay: Boolean? = false) : MainActivityAction()
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle,
                                                private val sharedPref: SharedPref,
                                                private val citationServiceRepository: CitationServiceRepository,
                                                private val mediaServiceRepository: MediaServiceRepository,
                                                private val eventServiceRepository: EventServiceRepository,
                                                private val inventoryServiceRepository: InventoryServiceRepository,
                                                private val locationServiceRepository: LocationServiceRepository,

                                                private val activityDaoRepository: ActivityDaoRepository,
                                                private val citationDaoRepository: CitationDaoRepository,
                                                private val datasetDaoRepository: DatasetDaoRepository,
                                                private val inventoryDaoRepository: InventoryDaoRepository,

                                                private val singletonDataSet: NewSingletonDataSet,
    ) : ViewModel() {

    // SharedFlow used for one-time events or commands
    private val _actions = MutableSharedFlow<MainActivityAction>()
    val actions = _actions.asSharedFlow()

    // Fragment can call this to request an operation in Activity
    suspend fun sendActionToMain(action: MainActivityAction) {
        _actions.emit(action)
    }

    var eventStartTimeStamp: String? = null

    private val _toolbarState = MutableStateFlow(ToolbarState())
    val toolbarState: StateFlow<ToolbarState> = _toolbarState.asStateFlow()
    
    // Splash screen control
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Login state control
//    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
//    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    private val _isLoggedIn = MutableSharedFlow<Boolean?>()
    val isLoggedIn = _isLoggedIn.asSharedFlow()

    // Back button control
    private val _backBlocked = MutableStateFlow(false)
    val backBlocked: StateFlow<Boolean> = _backBlocked.asStateFlow()

    private val _backEvent = MutableSharedFlow<Unit>()
    val backEvent = _backEvent.asSharedFlow()

    //StateFlow for deleteDatasetAndActivityTables
    private val _deleteDatasetAndActivityTablesState =
        MutableStateFlow<DbOperationStatus<Unit>>(DbOperationStatus.Idle)
    val deleteDatasetAndActivityTablesState: StateFlow<DbOperationStatus<Unit>> =
        _deleteDatasetAndActivityTablesState.asStateFlow()

    //Activity Log Image Response StateFlow
    private val _activityLogImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val activityLogImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _activityLogImageResponse.asStateFlow()

    //Inactive Meter Buzzer Response StateFlow
    private val _inactiveMeterBuzzerResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val inactiveMeterBuzzerResponse: StateFlow<NewApiResponse<JsonNode>> =
        _inactiveMeterBuzzerResponse.asStateFlow()

    //Activity Log Response StateFlow
    private val _activityLogResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val activityLogResponse: StateFlow<NewApiResponse<JsonNode>> =
        _activityLogResponse.asStateFlow()

    //Upload Image Response StateFlow
    private val _uploadImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val uploadImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _uploadImageResponse.asStateFlow()

    //Upload TextFile Response StateFlow
    private val _uploadTextFileResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val uploadTextFileResponse: StateFlow<NewApiResponse<JsonNode>> =
        _uploadTextFileResponse.asStateFlow()

    //Add Image Response StateFlow
    private val _addImageResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val addImageResponse: StateFlow<NewApiResponse<JsonNode>> =
        _addImageResponse.asStateFlow()

    //Add Note For Not Checked In Equipment Response StateFlow
    private val _addNoteForNotCheckedInEquipmentResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val addNoteForNotCheckedInEquipmentResponse: StateFlow<NewApiResponse<JsonNode>> =
        _addNoteForNotCheckedInEquipmentResponse.asStateFlow()

    //Location Update Response StateFlow
    private val _locationUpdateResponse =
        MutableStateFlow<NewApiResponse<JsonNode>>(NewApiResponse.Idle)
    val locationUpdateResponse: StateFlow<NewApiResponse<JsonNode>> =
        _locationUpdateResponse.asStateFlow()


    fun setBackBlocked(block: Boolean) {
        _backBlocked.value = block
    }

    suspend fun backButtonPressed() {
        _backEvent.emit(Unit)
        //_backEvent.tryEmit(Unit)
    }


    // Call this when you've completed data loading for splash screen
    fun finishLoading() {
        _isLoading.value = false
    }

    fun setToolbarState(state: ToolbarState) {
        _toolbarState.value = state
    }

    fun setToolbarComponents(showLogo : Boolean, showBackButton: Boolean, showHemMenu: Boolean) {
        _toolbarState.value = _toolbarState.value.copy(showToolbarLogo = showLogo, showBackButton = showBackButton, showHemMenu = showHemMenu)

    }

    fun setToolbarVisibility(show: Boolean) {
        _toolbarState.value = _toolbarState.value.copy(showToolbar = show, enableHemMenuDrawer = show)
    }

    fun showToolbarLogo(show: Boolean) {
        _toolbarState.value = _toolbarState.value.copy(showToolbarLogo = show)
    }

    fun showToolbarBackButton(show: Boolean) {
        _toolbarState.value = _toolbarState.value.copy(showBackButton = show)
    }

    fun showToolbarHemMenu(show: Boolean) {
        _toolbarState.value = _toolbarState.value.copy(showHemMenu = show)
    }

    fun setToolbarOfficerName(officerName: String) {
        _toolbarState.value = _toolbarState.value.copy(officerName = officerName)
    }

    /**
     * Starts the splash timer and emits a navigation event after the delay.
     * Uses viewModelScope for coroutine launching.
     */
    fun performSplashOperation() {
        viewModelScope.launch {
            //delay(AppConstants.TIME_INTERVAL_SPLASH_SCREEN.toLong())
            try {
                val isLoggedIn = sharedPref.read(SharedPrefKey.IS_LOGGED_IN, false)
                //_isLoggedIn.value = isLoggedIn.nullSafety()
                _isLoggedIn.emit(isLoggedIn.nullSafety())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Extras
    //Setting Flags
    //This flag is used to show hide inventory module in the app
    var showAndEnableInventoryModule: Boolean = false
    var showAndEnableHeaderFooterInFacsimile: Boolean = false
    var mQrCodeSizeForCommandFacsimile : Int = 0
    var showAndEnableScanVehicleStickerModule: Boolean = false
    var showAndEnableDirectedEnforcementModule: Boolean = false
    var showAndEnableCameraFeedUnderGuideEnforecement: Boolean = false
    var showAndEnableCameraGuidedEnforcementModule: Boolean = false //leavanworth

    fun getAndSetSettingFileValues() {
        viewModelScope.launch {
            //val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST)
            val settingsList = getSettingsListFromDataSet()

            if (settingsList != null) {
                showAndEnableInventoryModule = settingsList.firstOrNull {
                    it.type.equals(
                        SETTINGS_FLAG_INVENTORY_MODULE,
                        true
                    ) && it.mValue.toBooleanFromYesNo()
                }?.mValue.toBooleanFromYesNo()

                showAndEnableHeaderFooterInFacsimile = settingsList.firstOrNull {
                    it.type?.trim().equals(
                        SETTINGS_FLAG_HEADER_FOOTER_IN_FACSIMILE,
                        true
                    ) && it.mValue?.trim().toBooleanFromYesNo()
                }?.mValue?.trim().toBooleanFromYesNo()


                mQrCodeSizeForCommandFacsimile = settingsList.firstOrNull {
                    it.type?.trim().equals(
                        Constants.SETTINGS_FLAG_QR_CODE_SIZE,
                        true
                    )
                }?.mValue.nullSafety("0").toInt()

                showAndEnableScanVehicleStickerModule = settingsList.firstOrNull {
                    it.type.equals(
                        Constants.SETTINGS_FLAG_SCAN_VEHICLE_REGISTRATION_STICKER,
                        true
                    ) && it.mValue.toBooleanFromYesNo()
                }?.mValue.toBooleanFromYesNo()

                showAndEnableDirectedEnforcementModule = settingsList.firstOrNull {
                    it.type.equals(
                        Constants.SETTINGS_FLAG_DIRECTED_ENFORCEMENT_MODULE,
                        true
                    ) && it.mValue.toBooleanFromYesNo()
                }?.mValue.toBooleanFromYesNo()
                showAndEnableDirectedEnforcementModule = false

                showAndEnableCameraFeedUnderGuideEnforecement = settingsList.firstOrNull {
                    it.type.equals(
                        Constants.SETTINGS_FLAG_CAMERA_FEED_GUIDE_ENFORCEMENT,
                        true
                    ) && it.mValue.toBooleanFromYesNo()
                }?.mValue.toBooleanFromYesNo()

                showAndEnableCameraGuidedEnforcementModule = settingsList.firstOrNull {
                    it.type.equals(
                        Constants.SETTINGS_FLAG_CAMERA_GUIDE_ENFORCEMENT,//leaven worth
                        true
                    ) && it.mValue.toBooleanFromYesNo()
                }?.mValue.toBooleanFromYesNo()
            }
//        isEnableCrossClearButton = settingsList?.firstOrNull {
//            it.type.equals(
//                SETTINGS_FLAG_SHOW_CLEAR_ICON_FOR_INPUT_FIELDS,
//                true
//            ) && it.mValue.toBooleanFromYesNo()
//        }?.mValue.toBooleanFromYesNo()
//
//        isEnableCrossClearButton = false
        }

        showAndEnableScanVehicleStickerModule = true
    }

    suspend fun getMeterBuzzerAPICallStatusFormSettingFile(): Boolean {
        return try {
            getSettingsListFromDataSet()?.any { dataset ->
                dataset.type.equals(
                    SETTINGS_FLAG_IS_METER_BUZZER_ACTIVE, ignoreCase = true
                ) && dataset.mValue.equals(STR_YES, ignoreCase = true)
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // kotlin
    suspend fun getGenerateQRCodeVisibilityStatusFormSettingFile(): Boolean {
        return try {
            getSettingsListFromDataSet()?.any { dataset ->
                val isTypeMatch = dataset.type?.equals(
                    SETTINGS_FLAG_IS_GENERATE_QR_CODE_VISIBILE, true
                ) == true || dataset.type?.equals(
                    SETTINGS_FLAG_IS_GENERATE_QR_CODE_VISIBLE, true
                ) == true
                isTypeMatch && dataset.mValue?.equals(STR_YES, true) == true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getLprScannerType(): String {
        return if ((BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_SEPTA ||
                    BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_CITYOFSANDIEGO ||
                    BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_STORMWATER_DIVISION ||
                    BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING)
            && BuildConfig.ENABLE_DOUBANGO_FOR_LPR) {
            SCANNER_TYPE_DOUBANGO
        }else{
            SCANNER_TYPE_IMENSE
        }
    }
    //Extras

    //Start Of All API Calls
//    fun callLoggerEventAPI(lprScanLoggerRequest: LprScanLoggerRequest?) {
//        viewModelScope.launch {
//            _shiftListResponse.value = NewApiResponse.Loading
//
//            val result =
//                datasetServiceRepository.getShiftListDataset(lprScanLoggerRequest = lprScanLoggerRequest)
//            _shiftListResponse.value = result
//
//            _shiftListResponse.value = NewApiResponse.Idle
//        }
//    }

    fun callUploadActivityImageAPI(activityImageUploadRequest: ActivityImageUploadRequest?) {
        viewModelScope.launch {
            val result =
                mediaServiceRepository.uploadActivityImage(activityImageUploadRequest = activityImageUploadRequest)
            _activityLogImageResponse.value = result

            _activityLogImageResponse.value = NewApiResponse.Idle
        }
    }

    fun callInactiveMeterBuzzerAPI(inactiveMeterBuzzerRequest : InactiveMeterBuzzerRequest){
        viewModelScope.launch {
            val result =
                eventServiceRepository.inactiveMeterBuzzer(inactiveMeterBuzzerRequest = inactiveMeterBuzzerRequest)
            _inactiveMeterBuzzerResponse.value = result

            _inactiveMeterBuzzerResponse.value = NewApiResponse.Idle
        }
    }


    fun callActivityLogAPI(activityUpdateRequest : ActivityUpdateRequest){
        viewModelScope.launch {
            val result =
                eventServiceRepository.activityLog(activityUpdateRequest = activityUpdateRequest)
            _activityLogResponse.value = result

            _activityLogResponse.value = NewApiResponse.Idle
        }
    }

    fun callUploadImageAPI(
        mIDList: Array<String>?,
        mRequestBodyType: RequestBody?,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            val result =
                mediaServiceRepository.uploadImages(
                    apiTagName = API_TAG_NAME_UPLOAD_IMAGES,
                    mIDList = mIDList,
                    uploadType = mRequestBodyType,
                    image = image
                )

            _uploadImageResponse.value = result
            _uploadImageResponse.value = NewApiResponse.Idle
        }
    }

    fun callUploadTextFileAPI(
        mIDList: Array<String>?,
        mRequestBodyType: RequestBody?,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            val result =
                mediaServiceRepository.uploadTextFile(
                    mIDList = mIDList,
                    uploadType = mRequestBodyType,
                    image = image
                )

            _uploadTextFileResponse.value = result
            _uploadTextFileResponse.value = NewApiResponse.Idle
        }
    }

    fun callAddImageAPI(ticketNumber : String ,addImageRequest :AddImageRequest){
        viewModelScope.launch {
            val result =
                citationServiceRepository.addImages(
                    ticketNumber = ticketNumber,
                    addImageRequest = addImageRequest,
                )

            _addImageResponse.value = result
            _addImageResponse.value = NewApiResponse.Idle
        }
    }

    fun callAddNoteForNotCheckedInEquipmentAPI(logoutNoteForEquipmentRequest :LogoutNoteForEquipmentRequest){
        viewModelScope.launch {
            _addNoteForNotCheckedInEquipmentResponse.value = NewApiResponse.Loading

            val result =
                inventoryServiceRepository.addNoteForNotCheckedInEquipment(
                    logoutNoteForEquipmentRequest = logoutNoteForEquipmentRequest,
                )

            _addNoteForNotCheckedInEquipmentResponse.value = result
            _addNoteForNotCheckedInEquipmentResponse.value = NewApiResponse.Idle
        }
    }

    fun callLocationUpdateAPI(locUpdateRequest : LocUpdateRequest){
        viewModelScope.launch {
            val result =
                locationServiceRepository.locationUpdate(
                    locUpdateRequest = locUpdateRequest,
                )

            _locationUpdateResponse.value = result
            _locationUpdateResponse.value = NewApiResponse.Idle
        }
    }
    //End Of All API Calls

    //Start Of Database Related Methods
    fun insertActivityImageData(activityImageTable: ActivityImageTable) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                citationDaoRepository.insertActivityImageData(activityImageTable)
            }
        }
    }

    fun updateFacsimileStatus(status: Int, citationNumber: String, dateTime: Long) : Unit?{
        return try {
            citationDaoRepository.updateFacsimileStatus(status, citationNumber, dateTime)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteActivityImageData(activityResponseId: String) : Unit?{
        return try {
            citationDaoRepository.deleteActivityImageData(activityResponseId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteTempImages() : Unit?{
        return try {
            citationDaoRepository.deleteTempImages()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getInventoryToShowData() : List<InventoryToShowTable?>?{
        return try {
            inventoryDaoRepository.getInventoryToShowData()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getWelcomeForm(): WelcomeForm? {
        return try {
            activityDaoRepository.getWelcomeForm()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getActivityImageData(): List<ActivityImageTable?>? {
        return try {
            citationDaoRepository.getActivityImageData()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getUnUploadFacsimile(): UnUploadFacsimileImage? {
        return try {
            citationDaoRepository.getUnUploadFacsimile()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteDatasetAndActivityTables(isManual: Boolean) {
        viewModelScope.launch {
            if (isManual) {
                _deleteDatasetAndActivityTablesState.value = DbOperationStatus.Loading
            }

            try {
                // Room operation (should be on Dispatchers.IO)
                withContext(Dispatchers.IO) {
                    citationDaoRepository.deleteTimeStampTable()
                    datasetDaoRepository.deleteAllDataSet()
                    citationDaoRepository.deleteActivityList()
                }
                if (isManual) {
                    _deleteDatasetAndActivityTablesState.value = DbOperationStatus.Success(Unit)
                }
            } catch (e: Exception) {
                if (isManual) {
                    _deleteDatasetAndActivityTablesState.value = DbOperationStatus.Error(e)
                }
            } finally {
                if (isManual) {
                    _deleteDatasetAndActivityTablesState.value = DbOperationStatus.Idle
                }
            }
        }
    }
    //End Of Database Related Methods

    //Start of Singleton Related Methods
    suspend fun getLicensePlateFormat(): String {
        return try {
            getSettingsListFromDataSet()
                ?.firstOrNull { it.type.equals("LICENSE_PLATE_FORMAT", ignoreCase = true) }
                ?.mRegex.nullSafety()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    suspend fun getMaxImageCount(): Int {
        return try {
            getSettingsListFromDataSet()
                ?.firstOrNull { it.type.equals(SETTING_MAX_IMAGES_COUNT, ignoreCase = true) }
                ?.mValue
                .nullSafety("0")
                .toIntOrNull()
                ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    suspend fun getWelcomeObject(): WelcomeListDatatbase? {
        return withContext(Dispatchers.IO) {
            singletonDataSet.getWelcomeDbObject()
        }
    }

    suspend fun getCarMakeListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_CAR_MAKE_LIST)
    }
    suspend fun getCarModelListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_CAR_MODEL_LIST)
    }

    suspend fun getCarBodyStyleListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_CAR_BODY_STYLE_LIST)
    }
    suspend fun getCarColorListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_CAR_COLOR_LIST)
    }

    suspend fun getDecalYearListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_DECAL_YEAR_LIST)
    }

    suspend fun getStreetListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_STREET_LIST)
    }

    suspend fun getViolationListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_VIOLATION_LIST)
    }
    suspend fun getVioListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_VIO_LIST)
    }
    suspend fun getMeterListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_METER_LIST)
    }

    suspend fun getBlockListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_BLOCK_LIST)
    }

    suspend fun getSideListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_SIDE_LIST)
    }

    suspend fun getLotListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_LOT_LIST)
    }

    suspend fun getStateListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_STATE_LIST)
    }

    suspend fun getSpaceListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_SPACE_LIST)
    }

    suspend fun getRemarkListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_REMARKS_LIST)
    }

    suspend fun getTierStemListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_TIER_STEM_LIST)
    }

    suspend fun getRegulationTimeListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_REGULATION_TIME_LIST)
    }

    suspend fun getNotesListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_NOTES_LIST)
    }

    suspend fun getSettingsListFromDataSet(): List<DatasetResponse>? {
        return singletonDataSet.getDataSetList(dataSetType = DATASET_SETTINGS_LIST)
    }

    fun resetSingletonData() {
        singletonDataSet.reset()
    }
    //End of Singleton Related Methods
}
