package com.parkloyalty.lpr.scan.views.fragments.welcome

import DialogUtil
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.DataBaseUtil
import com.parkloyalty.lpr.scan.database.DataBaseUtil.CITATION_UNUPLOADED_API_FAILED
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.databasetable.QrCodeInventoryTable
import com.parkloyalty.lpr.scan.databinding.FragmentWelcomeScreenBinding
import com.parkloyalty.lpr.scan.extensions.boolToInt
import com.parkloyalty.lpr.scan.extensions.getAndroidID
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.getImageFileName
import com.parkloyalty.lpr.scan.extensions.getIndexOfActivity
import com.parkloyalty.lpr.scan.extensions.getIndexOfAgency
import com.parkloyalty.lpr.scan.extensions.getIndexOfDeviceFriendlyName
import com.parkloyalty.lpr.scan.extensions.getIndexOfLocation
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.logE
import com.parkloyalty.lpr.scan.extensions.logI
import com.parkloyalty.lpr.scan.extensions.nav
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.safeNavigate
import com.parkloyalty.lpr.scan.extensions.setListOnlyDropDown
import com.parkloyalty.lpr.scan.extensions.showErrorWithShake
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_FOOTER_URL_FOR_FACSIMILE
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_HEADER_URL_FOR_FACSIMILE
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.locationservice.LocationUtils.getAddressFromLatLng
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.api.RequestHandler
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutRequest
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutResponse
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentInventoryResponse
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentItemDetail
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.OfficerEquipmentHistoryResponse
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.OfficerEquipmentItemDetail
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.qrcode.QRCodeScanner
import com.parkloyalty.lpr.scan.qrcode.ViewPagerBannerAdapterQrCodeInventory
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.TimingViewPagerBannerAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ActivityLogResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImageModelOffline
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetBlockListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCancelReasonListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarBodyStyleListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarColorListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarMakeListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetData
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
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
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
import com.parkloyalty.lpr.scan.ui.login.model.ActivityResponse
import com.parkloyalty.lpr.scan.ui.login.model.ActivityStat
import com.parkloyalty.lpr.scan.ui.login.model.ActivityUpdateRequest
import com.parkloyalty.lpr.scan.ui.login.model.AgencyResponse
import com.parkloyalty.lpr.scan.ui.login.model.BeatResponse
import com.parkloyalty.lpr.scan.ui.login.model.BeatStat
import com.parkloyalty.lpr.scan.ui.login.model.CommentResponse
import com.parkloyalty.lpr.scan.ui.login.model.CommentState
import com.parkloyalty.lpr.scan.ui.login.model.DeviceLicenseListResponse
import com.parkloyalty.lpr.scan.ui.login.model.DeviceListResponse
import com.parkloyalty.lpr.scan.ui.login.model.DeviceResponseItem
import com.parkloyalty.lpr.scan.ui.login.model.EquipmentResponse
import com.parkloyalty.lpr.scan.ui.login.model.OfficerDeviceIdObject
import com.parkloyalty.lpr.scan.ui.login.model.RadioResponse
import com.parkloyalty.lpr.scan.ui.login.model.RadioSt
import com.parkloyalty.lpr.scan.ui.login.model.ResponseItem
import com.parkloyalty.lpr.scan.ui.login.model.ResponseSquadItem
import com.parkloyalty.lpr.scan.ui.login.model.ShiftResponse
import com.parkloyalty.lpr.scan.ui.login.model.ShiftStat
import com.parkloyalty.lpr.scan.ui.login.model.SquadResponse
import com.parkloyalty.lpr.scan.ui.login.model.SupervisorResponse
import com.parkloyalty.lpr.scan.ui.login.model.SupervisorStat
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.UpdatePackage
import com.parkloyalty.lpr.scan.ui.login.model.UpdateSiteOfficerRequest
import com.parkloyalty.lpr.scan.ui.login.model.UpdateSiteOfficerResponse
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDataList
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDb
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeList
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeUser
import com.parkloyalty.lpr.scan.ui.login.model.ZoneResponse
import com.parkloyalty.lpr.scan.ui.login.model.ZoneStat
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.Links
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusResponse
import com.parkloyalty.lpr.scan.util.ACTIVITY_LOG_WELCOME_SCAN
import com.parkloyalty.lpr.scan.util.API_CONSTANT_DOWNLOAD_TYPE_LOGO_IMAGE
import com.parkloyalty.lpr.scan.util.API_CONSTANT_SIGNATURE_IMAGES
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET
import com.parkloyalty.lpr.scan.util.API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.getBeatSetEmptyFromSetting
import com.parkloyalty.lpr.scan.util.AppUtils.getLprLock
import com.parkloyalty.lpr.scan.util.AppUtils.getSiteId
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateWelcome
import com.parkloyalty.lpr.scan.util.AppUtils.splitID
import com.parkloyalty.lpr.scan.util.DATASET_BLOCK_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CANCEL_REASON_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_BODY_STYLE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_COLOR_LIST
import com.parkloyalty.lpr.scan.util.DATASET_DECAL_YEAR_LIST
import com.parkloyalty.lpr.scan.util.DATASET_HOLIDAY_CALENDAR_LIST
import com.parkloyalty.lpr.scan.util.DATASET_INVENTORY_REPORT_LIST
import com.parkloyalty.lpr.scan.util.DATASET_LOT_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MAKE_MODEL_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MAKE_MODEL_LIST2
import com.parkloyalty.lpr.scan.util.DATASET_METER_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_BLOCK_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_CITY_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_STATE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_STREET_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_VIOLATION_LIST
import com.parkloyalty.lpr.scan.util.DATASET_NOTES_LIST
import com.parkloyalty.lpr.scan.util.DATASET_PBC_ZONE_LIST
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
import com.parkloyalty.lpr.scan.util.DATASET_VOID_AND_REISSUE_REASON_LIST
import com.parkloyalty.lpr.scan.util.EQUIPMENT_CHECKED_IN
import com.parkloyalty.lpr.scan.util.EQUIPMENT_CHECKED_OUT
import com.parkloyalty.lpr.scan.util.FROM_EQUIPMENT_CHECKIN
import com.parkloyalty.lpr.scan.util.FROM_EQUIPMENT_CHECKOUT
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.FileUtil.createCredFolder
import com.parkloyalty.lpr.scan.util.FileUtil.createDirForContinuesMode
import com.parkloyalty.lpr.scan.util.FileUtil.createFolderForLprImages
import com.parkloyalty.lpr.scan.util.FileUtil.getHeaderFooterDirectory
import com.parkloyalty.lpr.scan.util.FileUtil.getHeaderFooterFileName
import com.parkloyalty.lpr.scan.util.FileUtil.getSignatureFileNameWithExt
import com.parkloyalty.lpr.scan.util.FileUtil.saveRequestBodyStreamToFileStorage
import com.parkloyalty.lpr.scan.util.INTENT_KEY_FROM
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPR_BUNDLE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPR_NUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNED_EQUIPMENT_KEY
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNED_EQUIPMENT_VALUE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNER_TYPE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VEHICLE_INFO
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VEHICLE_STICKER_URL
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_ID_TIMESTAMP
import com.parkloyalty.lpr.scan.util.SDF_ddHHmmss
import com.parkloyalty.lpr.scan.util.SHOW_DELETE_BUTTON
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.util.Util.setLastUpdateDataSetStatus
import com.parkloyalty.lpr.scan.util.setAsAccessibilityHeading
import com.parkloyalty.lpr.scan.util.setCustomAccessibility
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ACTIVITY_LOG
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_TIMING
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CANCEL_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CREATE_MUNICIPAL_CITATION_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CREATE_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_BITMAP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_FOOTER_BITMAP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_FOOTER_FILE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_HEADER_BITMAP
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_HEADER_FILE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_DOWNLOAD_SIGNATURE_FILE
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_CITATION_DATASET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_EQUIPMENT_INVENTORY_DATASET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_OFFICER_EQUIPMENT_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_TICKET_STATUS
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOG_EQUIPMENT_CHECKED_IN
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOG_EQUIPMENT_CHECKED_OUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_SITE_OFFICER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_SIGNATURE_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_TIME_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_WELCOME
import com.parkloyalty.lpr.scan.utils.AppConstants.CITATION_TICKET_TYPE_MUNICIPAL
import com.parkloyalty.lpr.scan.utils.AppConstants.CITATION_TICKET_TYPE_PARKING
import com.parkloyalty.lpr.scan.utils.AppConstants.DEFAULT_VALUE_ZERO_DOT_ZERO_STR
import com.parkloyalty.lpr.scan.utils.AppConstants.IMAGE_FILE_EXTENSION_JPG
import com.parkloyalty.lpr.scan.utils.AppConstants.TEMP_IMAGE_FILE_NAME
import com.parkloyalty.lpr.scan.utils.AppConstants.TIME_INTERVAL_2_SECONDS
import com.parkloyalty.lpr.scan.utils.AppConstants.TIME_INTERVAL_6_SECONDS
import com.parkloyalty.lpr.scan.utils.AppConstants.TIME_INTERVAL_700_MS
import com.parkloyalty.lpr.scan.utils.CitationFormUtils.CITATION_FORM_FIELD_TYPE_LIST_ONLY
import com.parkloyalty.lpr.scan.utils.InventoryModuleUtil
import com.parkloyalty.lpr.scan.utils.NewConstructLayoutBuilder
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.ViewPagerUtils
import com.parkloyalty.lpr.scan.utils.camerahelper.CameraHelper
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.views.MainActivityAction
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.NewLprScanActivity
import com.parkloyalty.lpr.scan.views.NewVehicleStickerScanActivity
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import com.parkloyalty.lpr.scan.views.bottomsheets.signature.SignatureBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.UUID
import javax.inject.Inject

//TODO : permissons , loader scree , empty screen
//TODO : Old dialogs & toast
@AndroidEntryPoint
class WelcomeScreenFragment : BaseFragment<FragmentWelcomeScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val welcomeScreenViewModel: WelcomeScreenViewModel by viewModels()

    @Inject
    lateinit var sharedPreference: SharedPref

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var constructLayoutBuilder: NewConstructLayoutBuilder

    @Inject
    lateinit var cameraHelper: CameraHelper

    private var session: CameraHelper.Session? = null

    private lateinit var lprScanActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var vehicleStickerActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var qrScannerForResult: ActivityResultLauncher<Intent>


    lateinit var mTextViewUserName: AppCompatTextView
    lateinit var mTextViewOfficerId: TextInputEditText
    lateinit var mTextViewBadgeId: TextInputEditText
    lateinit var mTextViewPreLogin: AppCompatTextView
    lateinit var mTextViewOfficerDetails: AppCompatTextView
    lateinit var mTextViewAssetDetails: AppCompatTextView
    lateinit var mTextViewLocationDetails: AppCompatTextView
    lateinit var mTextViewOtherDetails: AppCompatTextView
    lateinit var mTextViewCurLogin: AppCompatTextView
    lateinit var mTextViewLoginDetails: AppCompatTextView
    lateinit var layOtherDetails: LinearLayoutCompat
    lateinit var layMainOtherDetails: MaterialCardView
    lateinit var layAssetDetails: LinearLayoutCompat
    lateinit var layLocationDetails: LinearLayoutCompat
    lateinit var layBadgeId: TextInputLayout
    lateinit var layOfficerId: TextInputLayout
    lateinit var layOfficer: LinearLayoutCompat
    lateinit var layButtons: LinearLayoutCompat
    lateinit var linearLayoutEmptyActivity: LinearLayoutCompat
    lateinit var cardCurLogin: MaterialCardView
    lateinit var cardPreLogin: MaterialCardView
    lateinit var btnDoneActivity: AppCompatButton
    lateinit var imageViewSignature: AppCompatImageView
    lateinit var mTextViewSignName: AppCompatTextView
    lateinit var mTextViewWelcomeBack: AppCompatTextView
    lateinit var mViewPagerBanner: ViewPager
    lateinit var pagerIndicator: LinearLayoutCompat
    lateinit var mViewPagerBannerQrCode: ViewPager
    lateinit var pagerIndicatorQrCode: LinearLayoutCompat
    lateinit var ivCameraIcon: AppCompatImageView
    lateinit var textScanCount: AppCompatTextView
    lateinit var cvQrCode: MaterialCardView
    lateinit var btnScanSticker: AppCompatButton

    private var mBannerAdapter: TimingViewPagerBannerAdapter? = null

    private var bannerList: MutableList<TimingImagesModel?>? = ArrayList()

    private var mAutoComTextViewActivity: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewComments: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewSuper: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewZone: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewPbcZone: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewLot: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewBeat: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewShift: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewRadio: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewAgency: AppCompatAutoCompleteTextView? = null
    private var mEdittextNote: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewDeviceId: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewPrinterEquipment: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewExtra: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewStreet: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewSideofStreet: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewSquad: AppCompatAutoCompleteTextView? = null

    //Add TextInputLayout Variable declaration of all about autocomplete text view
    private var mTextInputLayoutActivity: TextInputLayout? = null
    private var mTextInputLayoutComments: TextInputLayout? = null
    private var mTextInputLayoutSuper: TextInputLayout? = null
    private var mTextInputLayoutZone: TextInputLayout? = null
    private var mTextInputLayoutPbcZone: TextInputLayout? = null
    private var mTextInputLayoutLot: TextInputLayout? = null
    private var mTextInputLayoutBeat: TextInputLayout? = null
    private var mTextInputLayoutShift: TextInputLayout? = null
    private var mTextInputLayoutRadio: TextInputLayout? = null
    private var mTextInputLayoutAgency: TextInputLayout? = null
    private var mTextInputLayoutNote: TextInputLayout? = null
    private var mTextInputLayoutDeviceId: TextInputLayout? = null
    private var mTextInputLayoutPrinterEquipment: TextInputLayout? = null
    private var mTextInputLayoutExtra: TextInputLayout? = null
    private var mTextInputLayoutBlock: TextInputLayout? = null
    private var mTextInputLayoutStreet: TextInputLayout? = null
    private var mTextInputLayoutSideofStreet: TextInputLayout? = null
    private var mTextInputLayoutSquad: TextInputLayout? = null


    private var latestLayoutOther = 0
    private var mActivityId: String = ""
    private var mRequestTimeStart: Long = 0
    private var mWelcomeResponseData: WelcomeResponse? = null
    private var mSelectedSupervisorStat: SupervisorStat? = null
    private var mSelectedSActivityStat: ActivityStat? = null
    private var mSelectedBeatStat: BeatStat? = null
    private var mSelectedZoneStat: ZoneStat? = null
    private var mSelectedRadioStat: RadioSt? = null
    private var mSelectedShiftStat: ShiftStat? = null
    private var mDatasetApiCount = 0
    private var mSelectedAgency: String? = ""
    private var mDatabaseWelcomeList: WelcomeList? = WelcomeList()
    private var mDataSetTimeObject: TimestampDatatbase? = null
    private var mCitationLayout: List<CitationLayoutData>? = ArrayList()
    private var mUUID = ""
    private var scanStatus = false
    private var uploadActivityImages = false
    private var isSaveButtonClicked = false
    private var APICOUNT = 0
    private var pageIndex: Long = 1
    private var pageIndexActivity: Long = 1
    private var mAPIPageIndexActivity: Long = 0
    private val updateTimeDataset: MutableList<UpdateTimeDb> = ArrayList()
    private var mCitationNumberId: String? = null
    private var mAddTimingID = 0
    private var imageUploadSuccessCount = 0
    private val mImages: MutableList<String> = ArrayList()
    private val mImagesForTiming: MutableList<String> = ArrayList()
    private var offlineCitationImagesList: List<CitationImageModelOffline>? = null
    private var offlineCitationData: CitationInsurranceDatabaseModel? = null
    private var addTimingDatabaseModel: AddTimingDatabaseModel? = null

    //    private var mDataSetModel: DatasetDatabaseModel? = null
    private var mWelcomeListDataSet: WelcomeListDatatbase? = null
    private var activityIndex = 0
    private var updateTimeDatasetActivity: MutableList<UpdateTimeDb>? = null
    private var printAddress: String = ""
    private var mRoundOfAddress: String = ""

    private var timingBannerList: MutableList<TimingImagesModel?>? = ArrayList()
    private var selectedDeViceId = OfficerDeviceIdObject()
    private var mZone = "CST"
    private var mUploadCitationIdForCancel = ""
    private var makeListTotal: MutableList<DatasetResponse> = ArrayList()
    private var stateList: MutableList<DatasetResponse> = ArrayList()
    private var blockList: MutableList<DatasetResponse> = ArrayList()
    private var streetList: MutableList<DatasetResponse> = ArrayList()
    private var meterList: MutableList<DatasetResponse> = ArrayList()
    private var spaceList: MutableList<DatasetResponse> = ArrayList()
    private var violationList: MutableList<DatasetResponse> = ArrayList()
    private var vioList: MutableList<DatasetResponse> = ArrayList()
    private var holidayCalendarList: MutableList<DatasetResponse> = ArrayList()
    private var lotList: MutableList<DatasetResponse> = ArrayList()
    private var pbcZoneList: MutableList<DatasetResponse> = ArrayList()
    private var municipalViolationList: MutableList<DatasetResponse> = ArrayList()
    private var municipalBlockList: MutableList<DatasetResponse> = ArrayList()
    private var municipalStreetList: MutableList<DatasetResponse> = ArrayList()
    private var municipalCityList: MutableList<DatasetResponse> = ArrayList()
    private var municipalStateList: MutableList<DatasetResponse> = ArrayList()
    private var offlineStatus: Int = 0
    private var fileSignature: File? = null
    private var uploadSignatureLink: String? = ""
    private var mCombinationId: String? = ""

    private var qrCodeInventoryBannerList: MutableList<QrCodeInventoryTable?>? = ArrayList()
    private var qrCodeInventoryBannerAdapter: ViewPagerBannerAdapterQrCodeInventory? = null
    private var inventoryToShowList: MutableList<InventoryToShowTable?>? = ArrayList()


    private val activityUpdateRequest = ActivityUpdateRequest()
    private var cancelTicketDataObject: OfflineCancelCitationModel? = null

    private var officerId: String? = null
    private var equipmentID: String? = null
    private var equipmentName: String? = null
    private var equipmentValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager =
            permissionFactory.create(caller = this, context = requireContext(), fragment = this)

        // create a session tied to this fragment lifecycle
        session = cameraHelper.createSession(fragment = this)

        registerActivityResultLauncher()
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentWelcomeScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        mTextViewUserName = binding.tvHeaderUserName
        mTextViewOfficerId = binding.etOfficerId
        mTextViewBadgeId = binding.etBadgeId
        mTextViewPreLogin = binding.tvPreviousLogin
        mTextViewOfficerDetails = binding.tvOfficerDetails
        mTextViewAssetDetails = binding.tvAssetDetails
        mTextViewLocationDetails = binding.tvLocationDetails
        mTextViewOtherDetails = binding.tvOtherDetails
        mTextViewCurLogin = binding.tvCurrentLogin
        mTextViewLoginDetails = binding.tvLoginDetails
        //layBottomKeyboard = binding.layBottomKeyboard
        layOtherDetails = binding.layOtherDetails
        layMainOtherDetails = binding.cvMainOtherDetails
        layAssetDetails = binding.layAssetDetails
        layLocationDetails = binding.layLocationDetails
        layBadgeId = binding.inputBadgeId
        layOfficerId = binding.inputOfficerId
        layOfficer = binding.layMainOfficer
        layButtons = binding.layButtons
        linearLayoutEmptyActivity = binding.layoutContentEmptyActivityLayout.emptyLayoutActivity
        cardCurLogin = binding.cardCurLogin
        cardPreLogin = binding.cardPreLogin
        btnDoneActivity = binding.btnDoneActivity
        imageViewSignature = binding.imgSignature
        mTextViewSignName = binding.txtPersonSignature
        mTextViewWelcomeBack = binding.tvWelcomeBack
        mViewPagerBanner = binding.layoutContentBanner.pagerBanner
        pagerIndicator = binding.layoutContentBanner.viewPagerCountDots
        mViewPagerBannerQrCode = binding.layoutContentQrCodeInventoryBanner.pagerBannerQrCode
        pagerIndicatorQrCode = binding.layoutContentQrCodeInventoryBanner.viewPagerCountDotsQrCode
        ivCameraIcon = binding.ivCamera
        textScanCount = binding.scanCount
        cvQrCode = binding.cvQrCode
        btnScanSticker = binding.btnScanSticker
    }

    override fun initViewLifecycleScope() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    //Nothing to implement now
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    //Nothing to implement now
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainActivityViewModel.setToolbarVisibility(true)
                    mainActivityViewModel.setToolbarComponents(
                        showBackButton = true, showLogo = true, showHemMenu = true
                    )
                }

                launch {
                    welcomeScreenViewModel.welcomeResponse.collect(::consumeResponse)
                }

                launch {
                    welcomeScreenViewModel.updateSiteOfficerResponse.collect(::consumeResponse)
                }

                launch {
                    welcomeScreenViewModel.citationDatasetResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.activityLogResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.createTicketResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.createMunicipalCitationTicketResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.ticketCancelResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.ticketStatusResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.addTimingResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.uploadImageResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.uploadTimeImageResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.uploadSignatureImageResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.downloadBitmapImageResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.downloadHeaderImageResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.downloadFooterImageResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.officerEquipmentListResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.equipmentCheckInOutResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.equipmentCheckedInResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.equipmentInventoryResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.downloadHeaderFileResponse.collect(::consumeResponse)
                }
                launch {
                    welcomeScreenViewModel.downloadFooterFileResponse.collect(::consumeResponse)
                }
            }
        }
    }

    override fun initialiseData() {
        init()
        mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()

        // constants used only here to avoid repeated allocations
        val bluetoothPermissions = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT
        )

        viewLifecycleOwner.lifecycleScope.launch {
            // IO-bound setup tasks
            launch(Dispatchers.IO) {
                showHideScanVehicleStickerModule(isFromApiResponse = false)
                createFolderForLprImages()
            }

            // Read Android ID safely and log
            launch {
                mUUID = runCatching { requireContext().getAndroidID() }.getOrDefault("")
                if (mUUID.isNotEmpty()) {
                    logD("UUID", mUUID)
                    selectedDeViceId.mAndroidId = mUUID
                } else {
                    logD("UUID", "unavailable")
                }
            }

            // Ensure permissions (permissionManager will handle SDK specifics)
            launch {
                permissionManager.ensurePermissionsThen(
                    permissions = bluetoothPermissions,
                    rationaleMessage = getString(R.string.permission_message_camera_permission_required)
                ) {
                    // callback handled by permissionManager
                }
            }

            // Trigger processing of offline images on main thread
            launch {
                mainActivityViewModel.sendActionToMain(MainActivityAction.EventProcessUnUploadedActivityImages)
            }

            // Request MANAGE_EXTERNAL_STORAGE when needed (must run on main)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
            }

            // Accessibility setup
            setAccessibilityForComponents()
        }
    }

    override fun setupClickListeners() {
        ivCameraIcon.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                permissionManager.ensurePermissionsThen(
                    permissions = arrayOf(Manifest.permission.CAMERA),
                    rationaleMessage = getString(R.string.permission_message_camera_permission_required)
                ) {
                    if (bannerList?.size.nullSafety() <= 2) {
                        launchCameraForActivityImage()
                    } else {
                        AlertDialogUtils.showDialog(
                            context = requireContext(),
                            title = getString(R.string.error_title_max_image_reached),
                            message = getString(
                                R.string.error_desc_max_image_reached, "3"
                            ),
                            positiveButtonText = getString(R.string.button_text_ok),
                        )
                    }
                }
            }
        }

        binding.ivQrscanner.setOnClickListener {
            val status =
                inventoryToShowList?.firstOrNull { it?.checkedOut != EQUIPMENT_CHECKED_OUT }

            if (status != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    permissionManager.ensurePermissionsThen(
                        permissions = arrayOf(Manifest.permission.CAMERA),
                        rationaleMessage = getString(R.string.permission_message_camera_permission_required)
                    ) {
                        launchQRScannerActivityForResult(FROM_EQUIPMENT_CHECKOUT)
                    }
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.title_inventory_update),
                    message = getString(R.string.error_all_inventory_items_are_scanned),
                    positiveButtonText = getString(R.string.button_text_ok),
                    listener = object : AlertDialogListener {
                        override fun onPositiveButtonClicked() {

                        }
                    })
            }
        }

        imageViewSignature.setOnClickListener {
            setSignatureView()
        }

        btnDoneActivity.setOnClickListener {

        }

        binding.btnScan.setOnClickListener {
            if (mainActivityViewModel.showAndEnableInventoryModule) {
                if (InventoryModuleUtil.isRequiredEquipmentCheckedOut(inventoryToShowList)) {
                    bntScanClick()
                } else {
                    requireContext().toast(
                        getString(R.string.error_red_box_inventory_is_mandatory_to_proceed)
                    )
                }
            } else {
                bntScanClick()
            }
        }

        binding.btnDone.setOnClickListener {
            removeFocus()
            if (isFormValid("DONE")) {
                if (!isDetailsChanged()) {
                    if (mainActivityViewModel.showAndEnableInventoryModule) {
                        if (InventoryModuleUtil.isRequiredEquipmentCheckedOut(inventoryToShowList)) {
                            btnSaveClick()
                        } else {
                            requireContext().toast(
                                getString(R.string.error_red_box_inventory_is_mandatory_to_proceed)
                            )
                        }
                    } else {
                        btnSaveClick()
                    }
                }
            }
        }

        btnScanSticker.setOnClickListener {
            if (mainActivityViewModel.showAndEnableInventoryModule) {
                if (InventoryModuleUtil.isRequiredEquipmentCheckedOut(inventoryToShowList)) {
                    btnScanStickerClick()
                } else {
                    requireContext().toast(
                        getString(R.string.error_red_box_inventory_is_mandatory_to_proceed)
                    )
                }
            } else {
                btnScanStickerClick()
            }
        }
    }

    fun registerActivityResultLauncher() {
        qrScannerForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val from = intent?.getStringExtra(INTENT_KEY_FROM)
                val scannedKey = intent?.getStringExtra(INTENT_KEY_SCANNED_EQUIPMENT_KEY)
                val scannedValue = intent?.getStringExtra(INTENT_KEY_SCANNED_EQUIPMENT_VALUE)

                callLogEquipmentCheckInOutAPI(
                    from.nullSafety(), scannedKey.nullSafety(), scannedValue.nullSafety()
                )
            }
        }

        lprScanActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val bundle = intent?.getBundleExtra(INTENT_KEY_LPR_BUNDLE)
                    nav.safeNavigate(R.id.scanResultScreenFragment, bundle)
                } else { //if (result.resultCode == RESULT_CANCELLED)
                    nav.safeNavigate(R.id.scanResultScreenFragment)
                }
            }

        vehicleStickerActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val bundle = Bundle()
                    if (intent?.hasExtra(INTENT_KEY_VEHICLE_INFO).nullSafety()) {
                        bundle.apply {
                            putString(
                                INTENT_KEY_LPR_NUMBER, intent?.getStringExtra(INTENT_KEY_LPR_NUMBER)
                            )
                            putString(
                                INTENT_KEY_VEHICLE_STICKER_URL,
                                intent?.getStringExtra(INTENT_KEY_VEHICLE_STICKER_URL)
                            )
                            putSerializable(
                                INTENT_KEY_VEHICLE_INFO,
                                intent?.getSerializableExtra(INTENT_KEY_VEHICLE_INFO)
                            )
                        }
                    }

                    nav.safeNavigate(R.id.scanResultScreenFragment, bundle)
                }
            }
    }

    fun setAccessibilityForComponents() {
        setAsAccessibilityHeading(mTextViewLoginDetails)
        setAsAccessibilityHeading(mTextViewOfficerDetails)
        setAsAccessibilityHeading(mTextViewAssetDetails)
        setAsAccessibilityHeading(mTextViewLocationDetails)
        setAsAccessibilityHeading(mTextViewOtherDetails)

        binding.ivQrscanner.setCustomAccessibility(
            contentDescription = getString(
                R.string.ada_content_description_scan_inventory_qr_code
            ), role = getString(R.string.ada_role_button)
        )
        ivCameraIcon.setCustomAccessibility(
            contentDescription = getString(R.string.ada_content_description_camera),
            role = getString(R.string.ada_role_button)
        )
    }

    //API Response Consumer function for all APIs
    private fun consumeResponse(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                //DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(
                    context = requireContext(),
                    message = getString(R.string.loader_text_please_wait_we_are_loading_data)
                )
            }

            is NewApiResponse.Success -> {
                if (newApiResponse.apiNameTag == API_TAG_NAME_ACTIVITY_LOG || newApiResponse.apiNameTag == API_TAG_NAME_GET_TICKET_STATUS || newApiResponse.apiNameTag == API_TAG_NAME_DOWNLOAD_BITMAP || newApiResponse.apiNameTag == API_TAG_NAME_DOWNLOAD_HEADER_BITMAP || newApiResponse.apiNameTag == API_TAG_NAME_DOWNLOAD_FOOTER_BITMAP || newApiResponse.apiNameTag == API_TAG_NAME_UPDATE_SITE_OFFICER || newApiResponse.apiNameTag == API_TAG_NAME_CANCEL_TICKET || newApiResponse.apiNameTag == API_TAG_NAME_GET_EQUIPMENT_INVENTORY_DATASET || newApiResponse.apiNameTag == API_TAG_NAME_GET_OFFICER_EQUIPMENT_LIST) {
                    DialogUtil.hideLoader()
                }

                try {
                    when (newApiResponse.apiNameTag) {
                        API_TAG_NAME_WELCOME -> {
                            val welcomeResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                WelcomeResponse::class.java
                            )

                            viewLifecycleOwner.lifecycleScope.launch {
                                handleWelcomeResponse(welcomeResponse)
                            }

                        }

                        API_TAG_NAME_UPDATE_SITE_OFFICER -> {
                            val updateSiteOfficerResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                UpdateSiteOfficerResponse::class.java
                            )

                            handleUpdateSiteOfficerResponse(
                                updateSiteOfficerResponse
                            )
                        }

                        API_TAG_NAME_GET_CITATION_DATASET -> {
                            handleGetCitationDatasetResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_GET_EQUIPMENT_INVENTORY_DATASET -> {
                            handleGetEquipmentInventoryDatasetResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_ACTIVITY_LOG -> {
                            val activityLogResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                ActivityLogResponse::class.java
                            )
                            handleActivityLogResponse(activityLogResponse)
                        }

                        API_TAG_NAME_CREATE_TICKET -> {
                            val createTicketResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                CreateTicketResponse::class.java
                            )

                            handleCreateCitationTicketResponse(createTicketResponse)
                        }

                        API_TAG_NAME_CREATE_MUNICIPAL_CITATION_TICKET -> {
                            val createMunicipalCitationTicketResponse =
                                ObjectMapperProvider.fromJson(
                                    (newApiResponse.data as JsonNode).toString(),
                                    CreateMunicipalCitationTicketResponse::class.java
                                )

                            handleCreateMunicipalCitationTicketResponse(
                                createMunicipalCitationTicketResponse
                            )
                        }

                        API_TAG_NAME_CANCEL_TICKET -> {
                            val ticketCancelResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                TicketCancelResponse::class.java
                            )

                            handleTicketCancelResponse(ticketCancelResponse)
                        }

                        API_TAG_NAME_GET_TICKET_STATUS -> {
                            val ticketUploadStatusResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                TicketUploadStatusResponse::class.java
                            )

                            handleTicketUploadStatusResponse(ticketUploadStatusResponse)
                        }

                        API_TAG_NAME_ADD_TIMING -> {
                            val addTimingResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                AddTimingResponse::class.java
                            )

                            handleAddTimingResponse(addTimingResponse)
                        }

                        API_TAG_NAME_UPLOAD_IMAGES -> {
                            val uploadImagesResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                UploadImagesResponse::class.java
                            )

                            handleUploadImageResponse(uploadImagesResponse)
                        }

                        API_TAG_NAME_UPLOAD_SIGNATURE_IMAGES -> {
                            val uploadImagesResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                UploadImagesResponse::class.java
                            )

                            handleUploadSignatureResponse(uploadImagesResponse)
                        }

                        API_TAG_NAME_UPLOAD_TIME_IMAGES -> {
                            val uploadImagesResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                UploadImagesResponse::class.java
                            )

                            handleUploadTimeImageResponse(uploadImagesResponse)
                        }

                        API_TAG_NAME_DOWNLOAD_BITMAP -> {
                            val downloadBitmapResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                DownloadBitmapResponse::class.java
                            )

                            handleDownloadBitmapResponse(downloadBitmapResponse)
                        }

                        API_TAG_NAME_DOWNLOAD_HEADER_BITMAP -> {
                            val downloadBitmapResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                DownloadBitmapResponse::class.java
                            )
                            handleDownloadHeaderBitmapResponse(downloadBitmapResponse)
                        }

                        API_TAG_NAME_DOWNLOAD_FOOTER_BITMAP -> {
                            val downloadBitmapResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                DownloadBitmapResponse::class.java
                            )
                            handleDownloadFooterBitmapResponse(downloadBitmapResponse)
                        }

                        API_TAG_NAME_GET_OFFICER_EQUIPMENT_LIST -> {
                            val officerEquipmentHistoryResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                OfficerEquipmentHistoryResponse::class.java
                            )
                            handleOfficerEquipmentHistoryResponse(
                                officerEquipmentHistoryResponse
                            )
                        }

                        API_TAG_NAME_LOG_EQUIPMENT_CHECKED_OUT -> {
                            val equipmentCheckInOutResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                EquipmentCheckInOutResponse::class.java
                            )

                            handleEquipmentCheckOutResponse(equipmentCheckInOutResponse)
                        }

                        API_TAG_NAME_LOG_EQUIPMENT_CHECKED_IN -> {
                            val equipmentCheckInOutResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                EquipmentCheckInOutResponse::class.java
                            )

                            handleEquipmentCheckInResponse(equipmentCheckInOutResponse)
                        }

                        API_TAG_NAME_DOWNLOAD_HEADER_FILE -> {
                            handleDownloadFile(isHeader = true, newApiResponse.data as ResponseBody)
                        }

                        API_TAG_NAME_DOWNLOAD_FOOTER_FILE -> {
                            handleDownloadFile(
                                isHeader = false, newApiResponse.data as ResponseBody
                            )
                        }

                        API_TAG_NAME_DOWNLOAD_SIGNATURE_FILE -> {
                            handleDownloadSignatureFile(newApiResponse.data as ResponseBody)
                        }
                    }
                } catch (e: JsonMappingException) {
                    requireContext().toast(message = getString(R.string.error_desc_please_login_again_to_use_the_application))
                    viewLifecycleOwner.lifecycleScope.launch {
                        mainActivityViewModel.sendActionToMain(MainActivityAction.EventProcessLogout)
                    }
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is NewApiResponse.ApiError -> {
                DialogUtil.hideLoader()
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_api_error),
                    message = getString(
                        R.string.error_desc_api_error,
                        newApiResponse.code.toString(),
                        newApiResponse.getErrorMessage()
                            .nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            is NewApiResponse.NetworkError -> {
                DialogUtil.hideLoader()

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_network_error),
                    message = getString(
                        R.string.error_desc_network_error,
                        newApiResponse.exception.message.nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            is NewApiResponse.UnknownError -> {
                DialogUtil.hideLoader()

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_unknown_error),
                    message = getString(
                        R.string.error_desc_unknown_error,
                        newApiResponse.throwable.localizedMessage.nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    //Start of API Response Handling
    private suspend fun handleWelcomeResponse(welcomeResponse: WelcomeResponse?) {
        val user = welcomeResponse?.data?.firstOrNull()?.responsedata?.user
        if (welcomeResponse?.status == true && user != null) {
            mWelcomeResponseData = welcomeResponse
            insertFormToDbByAPIResponse(user)
            setActivityData()
            saveActivityList()
            user.mSignature?.takeIf { it.isNotBlank() }?.let { callDownloadBitmapApi(it) }
        } else {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_welcome_api_response),
                message = welcomeResponse?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }

    private fun handleUpdateSiteOfficerResponse(updateSiteOfficerResponse: UpdateSiteOfficerResponse?) {
        if (updateSiteOfficerResponse?.status == true) {
            scanStatus = false
        } else {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_update_site_officer_api_response),
                message = updateSiteOfficerResponse?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }

    private fun handleGetCitationDatasetResponse(jsonNodeValue: JsonNode) {
        val dropdownDatasetResponse = runCatching {
            ObjectMapperProvider.fromJson(
                jsonNodeValue.toString(), DropdownDatasetResponse::class.java
            )
        }.getOrNull()

        // Early guard
        val data0 = dropdownDatasetResponse?.data?.getOrNull(0) ?: return
        val metadata = data0.metadata ?: return
        val mType = metadata.type.nullSafety()
        logD("dataset response", "$mType --   ")

        if (!dropdownDatasetResponse.status.nullSafety()) {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_citation_dataset_api_response),
                message = dropdownDatasetResponse.message.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return
        }

        // Local helper to convert responseModel into typed response safely
        fun <T> parseAs(clazz: Class<T>): T? {
            return try {
                //ObjectMapperProvider.fromJson(ObjectMapperProvider.toJson(dropdownDatasetResponse), clazz)
                ObjectMapperProvider.parseFromStringAs(jsonNodeValue.toString(), clazz)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        // Handle bulk dataset types in one branch
        val bulkTypes = setOf(
            DATASET_DECAL_YEAR_LIST,
            DATASET_MAKE_MODEL_LIST,
            DATASET_MAKE_MODEL_LIST2,
            DATASET_CAR_COLOR_LIST,
            DATASET_STATE_LIST,
            DATASET_STREET_LIST,
            DATASET_METER_LIST,
            DATASET_METER_LIST,
            DATASET_CAR_BODY_STYLE_LIST,
            DATASET_VIOLATION_LIST,
            DATASET_VIO_LIST,
            DATASET_HOLIDAY_CALENDAR_LIST,
            DATASET_SPACE_LIST,
            DATASET_SIDE_LIST,
            DATASET_TIER_STEM_LIST,
            DATASET_NOTES_LIST,
            DATASET_REMARKS_LIST,
            DATASET_REGULATION_TIME_LIST,
            DATASET_LOT_LIST,
            DATASET_SETTINGS_LIST,
            DATASET_CANCEL_REASON_LIST,
            DATASET_VOID_AND_REISSUE_REASON_LIST,
            DATASET_BLOCK_LIST,
            DATASET_MUNICIPAL_VIOLATION_LIST,
            DATASET_MUNICIPAL_BLOCK_LIST,
            DATASET_MUNICIPAL_STREET_LIST,
            DATASET_MUNICIPAL_CITY_LIST,
            DATASET_MUNICIPAL_STATE_LIST
        )

        if (mType in bulkTypes) {
            mDatasetApiCount++
            saveDatasetTypeList(data0)
            val mAPIPageIndex = data0.metadata?.mTotalShards.nullSafety()
            logD(" API COUNT", "$mAPIPageIndex ---- $mType")
            if (mAPIPageIndex > pageIndex) {
                pageIndex += 1
                callApiParallellyRecursive(updateTimeDataset, APICOUNT, pageIndex)
            } else {
                APICOUNT++
                pageIndex = 1
                if (updateTimeDataset.isNotEmpty()) updateTimeDataset.removeAt(0)
                callApiParallellyRecursive(updateTimeDataset, APICOUNT, 1)
            }

            //show loader when 1 dataset is successfully fetched
            if (mDatasetApiCount == 2) {
                DialogUtil.showLoader(
                    context = requireContext(),
                    message = getString(R.string.loader_text_please_wait_we_are_loading_data)
                )
            }

            if (updateTimeDataset.isEmpty()) {
                DialogUtil.hideLoader()

                viewLifecycleOwner.lifecycleScope.launch {
                    mainActivityViewModel.getAndSetSettingFileValues()
                    showHideInventoryModule()
                    downloadHeaderFooterForFacsimile()
                    setAllDropdown()
                    showHideScanVehicleStickerModule(isFromApiResponse = true)
                    showHideDirectedEnforcementModule(isFromApiResponse = true)
                }
            }
            return
        }

        // Handle specific typed datasets
        when (mType) {
            "ActivityList" -> {
                val parsed = parseAs(ActivityResponse::class.java)
                if (parsed?.status == true) {
                    val resp = parsed.data?.getOrNull(0)
                    if (resp?.metadata?.type == "ActivityList") {
                        mDatabaseWelcomeList?.activityStats = resp.response
                    }
                }
            }

            "CommentsList" -> {
                val parsed = parseAs(CommentResponse::class.java)
                if (parsed?.status == true) {
                    val resp = parsed.data?.getOrNull(0)
                    if (resp?.metadata?.type == "CommentsList") {
                        mDatabaseWelcomeList?.commentStates = resp.response
                    }
                }
            }

            "SupervisorList" -> {
                val parsed = parseAs(SupervisorResponse::class.java)
                if (parsed?.status == true) {
                    val resp = parsed.data?.getOrNull(0)
                    if (resp?.metadata?.type == "SupervisorList") {
                        resp.response?.let { list ->
                            Collections.sort(list) { lhs, rhs ->
                                lhs?.mSuperName.nullSafety().compareTo(rhs?.mSuperName.nullSafety())
                            }
                            mDatabaseWelcomeList?.supervisorStats = list
                        }
                    }
                }
            }

            "BeatList" -> {
                val parsed = parseAs(BeatResponse::class.java)
                if (parsed?.status == true) {
                    mDatabaseWelcomeList?.beatStats = parsed.data?.getOrNull(0)?.response
                }
            }

            "ZoneList" -> {
                val parsed = parseAs(ZoneResponse::class.java)
                if (parsed?.status == true) {
                    mDatabaseWelcomeList?.zoneStats = parsed.data?.getOrNull(0)?.response
                }
            }

            DATASET_PBC_ZONE_LIST -> {
                val parsed = parseAs(ZoneResponse::class.java)
                if (parsed?.status == true) {
                    mDatabaseWelcomeList?.pbcZoneStats = parsed.data?.getOrNull(0)?.response
                }
            }

            "RadioList" -> {
                val parsed = parseAs(RadioResponse::class.java)
                if (parsed?.status == true) {
                    mDatabaseWelcomeList?.radioStats = parsed.data?.getOrNull(0)?.response
                } else {
                    logD("kalyani radio", "new")
                }
            }

            "ShiftList" -> {
                val parsed = parseAs(ShiftResponse::class.java)
                if (parsed?.status == true) {
                    mDatabaseWelcomeList?.shiftStats = parsed.data?.getOrNull(0)?.response
                } else {
                    logD("kalyani shift", "new")
                }
            }

            "AgencyList" -> {
                val parsed = parseAs(AgencyResponse::class.java)
                if (parsed?.status == true) {
                    mDatabaseWelcomeList?.agencyStats = parsed.data?.getOrNull(0)?.response
                }
            }

            "DeviceList" -> {
                val parsed = parseAs(DeviceListResponse::class.java)
                if (parsed?.isStatus == true) {
                    mDatabaseWelcomeList?.deviceStats = parsed.devicedata?.getOrNull(0)?.response
                }
            }

            "DeviceLicenseList" -> {
                val parsed = parseAs(DeviceLicenseListResponse::class.java)
                if (parsed?.status.nullSafety()) {
                    mDatabaseWelcomeList?.deviceLicenseStats = parsed?.dataDeviceLicense
                }
            }

            "EquipmentList" -> {
                val parsed = parseAs(EquipmentResponse::class.java)
                if (parsed?.isStatus == true) {
                    mDatabaseWelcomeList?.equipmentStates = parsed.data?.getOrNull(0)?.response
                }
            }

            "SquadList" -> {
                val parsed = parseAs(SquadResponse::class.java)
                if (parsed?.isStatus.nullSafety()) {
                    parsed?.dataSquad?.getOrNull(0)?.responseSquad?.let {
                        mDatabaseWelcomeList?.squadStates = it
                    }
                }
            }

            else -> {
                // Unknown type - just log
                logD("dataset response", "Unhandled type: $mType")
            }
        }

        // Activity dataset recursion handling (kept original behavior but null-safe)
        mAPIPageIndexActivity = data0.metadata?.mTotalShards.nullSafety()
        logD(" API COUNT Activity data set", "$mAPIPageIndexActivity ---- $mType")
        if (mAPIPageIndexActivity > pageIndexActivity) {
            pageIndexActivity += 1
            updateTimeDatasetActivity?.getOrNull(activityIndex)?.name?.let { name ->
                callActivityDataSetRecursive(name, pageIndexActivity)
            }
        } else {
            pageIndexActivity = 1
            activityIndex++
            if (activityIndex < (updateTimeDatasetActivity?.size ?: 0)) {
                updateTimeDatasetActivity?.getOrNull(activityIndex)?.name?.let { name ->
                    callActivityDataSetRecursive(name, pageIndexActivity)
                }
            } else {
                try {
                    lifecycleScope.launch {
                        val mDatabase = WelcomeListDatatbase().apply {
                            welcomeList = mDatabaseWelcomeList
                            id = 1
                        }
                        welcomeScreenViewModel.insertActivityList(mDatabase)
                        isCitationDatasetExits()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handleGetEquipmentInventoryDatasetResponse(jsonNodeValue: JsonNode) {
        val equipmentInventoryResponseModel = runCatching {
            ObjectMapperProvider.fromJson(
                jsonNodeValue.toString(), EquipmentInventoryResponse::class.java
            )
        }.getOrNull()

        val items =
            equipmentInventoryResponseModel?.takeIf { it.isStatus.nullSafety() }?.data?.firstOrNull()?.response

        if (!items.isNullOrEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                saveEquipmentInventoryInToDB(items)
            }

            return
        }

        AlertDialogUtils.showDialog(
            context = requireContext(),
            title = getString(R.string.err_title_server_error),
            message = getString(R.string.err_msg_something_went_wrong),
            positiveButtonText = getString(R.string.button_text_ok)
        )
    }

    private fun handleActivityLogResponse(activityLogResponse: ActivityLogResponse) {
        if (activityLogResponse.success != true) {
            val message =
                activityLogResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service))
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_activity_log_api_response),
                message = message,
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return
        }

        val activityId = activityLogResponse.activityId ?: return
        val bannerCount = bannerList?.size ?: 0

        if (mImages.size < bannerCount) {
            lifecycleScope.launch {
                mainActivityViewModel.sendActionToMain(
                    MainActivityAction.EventSaveActivityLogData(bannerList, activityId)
                )
            }
        }
    }

    private fun handleCreateCitationTicketResponse(createTicketResponse: CreateTicketResponse) {
        // Safe logging (won't crash on serialization errors)
        runCatching {
            ApiLogsClass.writeApiPayloadTex(
                requireContext(),
                "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(createTicketResponse)
            )
        }

        // Fail fast if API indicates error
        if (createTicketResponse.success != true) {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_citation_api_response),
                message = createTicketResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return
        }

        // Proceed with update and cancel only when required ids are available
        val ticketId = createTicketResponse.data?.id?.toString()
        val citationNumber = mCitationNumberId.nullSafety().takeIf { it.isNotBlank() }

        lifecycleScope.launch {
            welcomeScreenViewModel.updateCitationUploadStatus(0, mCitationNumberId.nullSafety())

            if (!ticketId.isNullOrBlank() && !citationNumber.isNullOrBlank()) {
                callTicketCancelApi(ticketId, citationNumber)
            } else {
                // missing ids: nothing to cancel — keep behavior silent or log if needed
            }
        }
    }

    private fun handleCreateMunicipalCitationTicketResponse(response: CreateMunicipalCitationTicketResponse) {
        // Safe logging - don't crash if serialization fails
        runCatching {
            ApiLogsClass.writeApiPayloadTex(
                requireContext(),
                "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(response)
            )
        }

        // Fail fast if API indicates error
        if (response.success != true) {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_municipal_citation_api_response),
                message = response.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return
        }

        // Prepare ids safely
        val ticketId = response.data?.id?.toString()
        val citationNumber = mCitationNumberId.nullSafety().takeIf { it.isNotBlank() }

        lifecycleScope.launch {
            welcomeScreenViewModel.updateCitationUploadStatus(0, mCitationNumberId.nullSafety())

            if (!ticketId.isNullOrBlank() && !citationNumber.isNullOrBlank()) {
                callTicketCancelApi(ticketId, citationNumber)
            }
        }
    }

    private fun handleTicketCancelResponse(ticketCancelResponse: TicketCancelResponse?) {
        // Safe logging without throwing
        runCatching {
            ApiLogsClass.writeApiPayloadTex(
                requireContext(), " RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(
                    ticketCancelResponse
                )
            )
        }

        // If not successful, hide loader and return early
        if (ticketCancelResponse?.success != true) {
            //DialogUtil.hideLoader()
            return
        }

        // Hide loader once and perform deletes asynchronously
        //DialogUtil.hideLoader()
        lifecycleScope.launch {
            cancelTicketDataObject?.let {
                welcomeScreenViewModel.deleteOfflineRescindCitation(it)
            }
            // Always attempt to delete cancel citation (id may be blank/null)
            welcomeScreenViewModel.deleteOfflineCancelCitation(mUploadCitationIdForCancel)
        }

        requireContext().toast(ticketCancelResponse.msg.nullSafety())
    }

    private fun handleTicketUploadStatusResponse(ticketUploadStatusResponse: TicketUploadStatusResponse?) {
        //DialogUtil.hideLoader()
        val response = ticketUploadStatusResponse ?: return

        if (response.citationUploaded == true) {
            sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)

            lifecycleScope.launch {
                welcomeScreenViewModel.updateCitationUploadStatus(0, mCitationNumberId.nullSafety())
            }

            val ticketId = response.message?.id?.toString().takeIf { !it.isNullOrBlank() }
            val citationNumber = mCitationNumberId.nullSafety().takeIf { it.isNotBlank() }

            if (!ticketId.isNullOrBlank() && !citationNumber.isNullOrBlank()) {
                callTicketCancelApi(ticketId, citationNumber)
            }

            return
        }

        when (offlineStatus) {
            2 -> moveToCitationFormWithUploadedCitation()
            1 -> offlineCitationData?.let { uploadOfflineImages(it) }
            else -> { /* no-op */
            }
        }
    }

    private fun handleAddTimingResponse(addTimingResponse: AddTimingResponse?) {
        lifecycleScope.launch {
            if (addTimingResponse?.success.nullSafety() == true) {
                welcomeScreenViewModel.updateTimingUploadStatus(0, mAddTimingID)
                if (!welcomeScreenViewModel.getLocalTimingDataList().isNullOrEmpty()) {
                    uploadOfflineTimingsToServer()
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_add_time_record_api_response),
                    message = addTimingResponse?.response.nullSafety(
                        getString(R.string.error_desc_unable_to_receive_data_from_service)
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    private fun handleUploadImageResponse(uploadImagesResponse: UploadImagesResponse) {
        // Early fail: show error and return if API indicates failure
        if (uploadImagesResponse.status != true) {
            //DialogUtil.hideLoader()
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_upload_image_api_response),
                message = uploadImagesResponse.message.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return
        }

        // Extract first available link safely
        val link = uploadImagesResponse.data?.firstOrNull()?.response?.links?.firstOrNull()

        if (link.isNullOrBlank()) {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_upload_image_api_response),
                message = getString(R.string.error_desc_getting_empty_image_array_from_server),
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return
        }

        // Activity images upload flow
        if (uploadActivityImages) {
            imageUploadSuccessCount++
            mImages.add(link)
            if (bannerList?.size == imageUploadSuccessCount) {
                uploadActivityImages = false
                saveActivityAPI()
            }
            return
        }

        // Offline citation image flow: perform deletion and continuation in coroutine
        lifecycleScope.launch {
            runCatching {
                val offlineId =
                    offlineCitationImagesList?.getOrNull(imageUploadSuccessCount)?.id.nullSafety()
                welcomeScreenViewModel.deleteTempImagesOfflineWithId(
                    offlineId.nullSafety().toString()
                )
            }.onFailure { it.printStackTrace() }

            imageUploadSuccessCount++
            mImages.add(link)

            if (imageUploadSuccessCount == (offlineCitationImagesList?.size ?: 0)) {
                offlineCitationData?.let { callCreateTicketApi(it) }
            }
        }
    }

    private fun handleUploadTimeImageResponse(uploadImagesResponse: UploadImagesResponse) {
        // Fail fast when API indicates error
        if (uploadImagesResponse.status != true) {
            //DialogUtil.hideLoader()
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_upload_time_image_api_response),
                message = uploadImagesResponse.message.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return
        }

        // Safely extract first link
        val link = uploadImagesResponse.data?.firstOrNull()?.response?.links?.firstOrNull()

        if (link.isNullOrBlank()) {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_upload_time_image_api_response),
                message = getString(R.string.error_desc_getting_empty_image_array_from_server),
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return
        }

        // Add and check if all timing images are uploaded
        mImagesForTiming.add(link)
        val expected = timingBannerList?.size ?: 0
        if (expected > 0 && mImagesForTiming.size.nullSafety() >= expected) {
            lifecycleScope.launch {
                runCatching {
                    removeTimingImagesFromFolder()
                    welcomeScreenViewModel.deleteTimingImagesWithTimingRecordId(mAddTimingID)
                    timingBannerList?.clear()
                    addTimingDatabaseModel?.let { callAddTimingsAPI(it) }
                }.onFailure { it.printStackTrace() }
            }
        }
    }

    private fun handleUploadSignatureResponse(uploadImagesResponse: UploadImagesResponse?) {
        // Fail fast on invalid response
        if (uploadImagesResponse?.status != true) {
            requireContext().toast(
                message = getString(R.string.err_msg_something_went_wrong)
            )
            return
        }

        // Safely get first link
        val link =
            uploadImagesResponse.data?.firstOrNull()?.response?.links?.firstOrNull()?.toString()

        if (link.isNullOrBlank()) {
            requireContext().toast(
                message = getString(R.string.err_msg_something_went_wrong)
            )
            return
        }

        uploadSignatureLink = link

        try {
            if (scanStatus) {
                callUpdateSiteOfficerApi()
                insertFormToDb(true, false, null)
            } else {
                insertFormToDb(false, false, null)
                callUpdateSiteOfficerApi()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            requireContext().toast(
                message = getString(R.string.err_msg_something_went_wrong)
            )
        }
    }

    private fun handleDownloadBitmapResponse(downloadBitmapResponse: DownloadBitmapResponse?) {
        val url =
            downloadBitmapResponse?.takeIf { it.isStatus }?.metadata?.firstOrNull()?.url.nullSafety()

        if (url.isNotBlank()) {
            welcomeScreenViewModel.callDownloadSignatureFile(url)
        }
    }

    private fun handleDownloadHeaderBitmapResponse(downloadBitmapResponse: DownloadBitmapResponse?) {
        val url =
            downloadBitmapResponse?.takeIf { it.isStatus }?.metadata?.firstOrNull()?.url.nullSafety()

        if (url.isNotBlank()) {
            downloadHeaderFooterForFacsimileFromSecureUrl(true, url)
        }
    }

    private fun handleDownloadFooterBitmapResponse(downloadBitmapResponse: DownloadBitmapResponse?) {
        val url =
            downloadBitmapResponse?.takeIf { it.isStatus }?.metadata?.firstOrNull()?.url.nullSafety()

        if (url.isNotBlank()) {
            downloadHeaderFooterForFacsimileFromSecureUrl(false, url)
        }
    }

    private fun handleOfficerEquipmentHistoryResponse(response: OfficerEquipmentHistoryResponse?) {
        val data = response?.takeIf { it.success.nullSafety() }?.data
        if (!data.isNullOrEmpty()) {
            updateOfficerEquipmentHistoryToDB(data)
        } else {
            updateOfficerEquipmentHistoryToDB()
        }
    }

    private fun handleEquipmentCheckOutResponse(response: EquipmentCheckInOutResponse?) {
        if (response?.success.nullSafety()) {
            lifecycleScope.launch {
                updateEquipmentInventory(
                    EQUIPMENT_CHECKED_OUT, equipmentName.nullSafety(), equipmentValue.nullSafety()
                )
                updateQrScanList()
            }
            return
        }

        AlertDialogUtils.showDialog(
            context = requireContext(),
            title = getString(R.string.error_title_inventory_api_response),
            message = response?.description.nullSafety(getString(R.string.error_desc_something_went_wrong)),
            positiveButtonText = getString(R.string.button_text_ok)
        )
    }

    private fun handleEquipmentCheckInResponse(response: EquipmentCheckInOutResponse?) {
        if (response?.success.nullSafety()) {
            lifecycleScope.launch {
                updateEquipmentInventory(EQUIPMENT_CHECKED_IN, equipmentName.nullSafety(), null)
                updateQrScanList()
            }
            return
        }

        AlertDialogUtils.showDialog(
            context = requireContext(),
            title = getString(R.string.error_title_inventory_api_response),
            message = response?.description.nullSafety(getString(R.string.error_desc_something_went_wrong)),
            positiveButtonText = getString(R.string.button_text_ok)
        )
    }

    private fun handleDownloadFile(isHeader: Boolean, responseBody: ResponseBody) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dir = getHeaderFooterDirectory()
            val fileName = getHeaderFooterFileName(isHeader)

            val saved = runCatching {
                responseBody.use { body ->
                    saveRequestBodyStreamToFileStorage(body, dir, fileName)
                }
            }.getOrDefault(false)

            if (!saved) {
                logE("WelcomeScreenFragment", "Failed to save header/footer file: $fileName")
            }
        }
    }

    private fun handleDownloadSignatureFile(responseBody: ResponseBody) {
        lifecycleScope.launch {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val targetDir = File(downloadsDir, Constants.FILE_NAME + Constants.SIGNATURE)

            val saved = withContext(Dispatchers.IO) {
                try {
                    // clean and ensure directory
                    targetDir.deleteRecursively()
                    if (!targetDir.exists()) targetDir.mkdirs()

                    // write stream and ensure it's closed
                    responseBody.use { body ->
                        saveRequestBodyStreamToFileStorage(
                            body, targetDir, getSignatureFileNameWithExt()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            if (saved) {
                fileSignature = File(targetDir, getSignatureFileNameWithExt())
                if (fileSignature?.exists() == true) {
                    imageViewSignature.setImageURI(Uri.fromFile(fileSignature))
                } else {
                    logE(
                        "WelcomeScreenFragment",
                        "Signature file saved but does not exist: ${fileSignature?.absolutePath}"
                    )
                }
            } else {
                logE(
                    "WelcomeScreenFragment",
                    "Failed to save signature file: ${getSignatureFileNameWithExt()}"
                )
            }
        }
    }
    //End of API Response Handling

    private fun getTimestampFromDb() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { welcomeScreenViewModel.getUpdateTimeResponse() }.getOrNull()
            }
            result?.let { mDataSetTimeObject = it } ?: logE(
                "WelcomeScreenFragment", "Failed to load update time from DB"
            )
        }
    }

    // kotlin
    private fun isCitationDatasetExits() {
        updateTimeDataset.add(UpdateTimeDb(DATASET_SETTINGS_LIST, true))
        viewLifecycleOwner.lifecycleScope.launch {
            welcomeScreenViewModel.deleteDatasetSettingsListModel()
        }

        mainActivityViewModel.resetSingletonData()

        if (mDataSetTimeObject != null && mDataSetTimeObject!!.timeList != null) {
            printLog("dataset", " if ")
            if (mDataSetTimeObject!!.timeList!!.cancelReasonList != null && mDataSetTimeObject!!.timeList!!.cancelReasonList!!.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_CANCEL_REASON_LIST,
                        mDataSetTimeObject!!.timeList!!.cancelReasonList!!.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.cancelReasonList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_CANCEL_REASON_LIST, true))
            }
            if (mDataSetTimeObject!!.timeList!!.decalYearList != null && mDataSetTimeObject!!.timeList!!.decalYearList!!.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_DECAL_YEAR_LIST,
                        mDataSetTimeObject!!.timeList!!.decalYearList!!.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.decalYearList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_DECAL_YEAR_LIST, true))
            }
            if (mDataSetTimeObject!!.timeList!!.carMakeList != null && mDataSetTimeObject!!.timeList!!.carMakeList!!.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_MAKE_MODEL_LIST,
                        mDataSetTimeObject!!.timeList!!.carMakeList!!.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.carMakeList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_MAKE_MODEL_LIST, true))
            }
//            if (mDataSetTimeObject!!.timeList!!.carModelList != null && mDataSetTimeObject!!.timeList!!.carModelList!!.status.nullSafety()) {
//                updateTimeDataset.add(
//                        UpdateTimeDb(
//                                "MakeModelList2",
//                                mDataSetTimeObject!!.timeList!!.carModelList!!.status
//                        )
//                )
//            }else if (mDataSetTimeObject!!.timeList!!.carModelList == null){
//                updateTimeDataset.add(UpdateTimeDb("MakeModelList2", true))
//            }
            if (mDataSetTimeObject!!.timeList!!.carColorList != null && mDataSetTimeObject!!.timeList!!.carColorList!!.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_CAR_COLOR_LIST,
                        mDataSetTimeObject!!.timeList!!.carColorList!!.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.carColorList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_CAR_COLOR_LIST, true))
            }
            if (mDataSetTimeObject!!.timeList!!.stateList != null && mDataSetTimeObject!!.timeList!!.stateList!!.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_STATE_LIST, mDataSetTimeObject!!.timeList!!.stateList!!.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.stateList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_STATE_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.streetList != null && mDataSetTimeObject?.timeList?.streetList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_STREET_LIST, mDataSetTimeObject?.timeList?.streetList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.streetList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_STREET_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.meterList != null && mDataSetTimeObject?.timeList?.meterList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_METER_LIST, mDataSetTimeObject?.timeList?.meterList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.meterList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_METER_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.mSpaceList != null && mDataSetTimeObject?.timeList?.mSpaceList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_SPACE_LIST, mDataSetTimeObject?.timeList?.mSpaceList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.mSpaceList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_SPACE_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.carBodyStyleList != null && mDataSetTimeObject?.timeList?.carBodyStyleList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_CAR_BODY_STYLE_LIST,
                        mDataSetTimeObject?.timeList?.carBodyStyleList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.carBodyStyleList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_CAR_BODY_STYLE_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.violationList != null && mDataSetTimeObject?.timeList?.violationList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_VIOLATION_LIST, mDataSetTimeObject?.timeList?.violationList?.status
                    )
                )
            } else if (mDataSetTimeObject?.timeList?.violationList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_VIOLATION_LIST, true))
            }

            if (mDataSetTimeObject?.timeList?.vioList != null && mDataSetTimeObject?.timeList?.vioList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_VIO_LIST, mDataSetTimeObject?.timeList?.vioList?.status
                    )
                )
            } else if (mDataSetTimeObject?.timeList?.vioList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_VIO_LIST, true))
            }

            if (mDataSetTimeObject?.timeList?.holidayCalendarList != null && mDataSetTimeObject?.timeList?.holidayCalendarList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_HOLIDAY_CALENDAR_LIST,
                        mDataSetTimeObject?.timeList?.holidayCalendarList?.status
                    )
                )
            } else if (mDataSetTimeObject?.timeList?.holidayCalendarList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_HOLIDAY_CALENDAR_LIST, true))
            }

            if (mDataSetTimeObject?.timeList?.sideList != null && mDataSetTimeObject?.timeList?.sideList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_SIDE_LIST, mDataSetTimeObject?.timeList?.sideList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.sideList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_SIDE_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.tierStemList != null && mDataSetTimeObject?.timeList?.tierStemList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_TIER_STEM_LIST, mDataSetTimeObject?.timeList?.tierStemList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.tierStemList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_TIER_STEM_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.notesList != null && mDataSetTimeObject?.timeList?.notesList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_NOTES_LIST, mDataSetTimeObject?.timeList?.notesList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.notesList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_NOTES_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.remarksList != null && mDataSetTimeObject?.timeList?.remarksList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_REMARKS_LIST, mDataSetTimeObject?.timeList?.remarksList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.remarksList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_REMARKS_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.regulationTimeList != null && mDataSetTimeObject?.timeList?.regulationTimeList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_REGULATION_TIME_LIST,
                        mDataSetTimeObject?.timeList?.regulationTimeList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.regulationTimeList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_REGULATION_TIME_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.lotList != null && mDataSetTimeObject?.timeList?.lotList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_LOT_LIST, mDataSetTimeObject?.timeList?.lotList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.lotList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_LOT_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.mVoidAndReissueList != null && mDataSetTimeObject?.timeList?.mVoidAndReissueList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_VOID_AND_REISSUE_REASON_LIST,
                        mDataSetTimeObject?.timeList?.mVoidAndReissueList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.mVoidAndReissueList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_VOID_AND_REISSUE_REASON_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.mBlockList != null && mDataSetTimeObject?.timeList?.mBlockList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_BLOCK_LIST, mDataSetTimeObject?.timeList?.mBlockList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.mBlockList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_BLOCK_LIST, true))
            }

            if (LogUtil.isMunicipalCitationEnabled()) {
                //Municipal Violation List
                if (mDataSetTimeObject?.timeList?.municipalViolationList != null && mDataSetTimeObject?.timeList?.municipalViolationList?.status.nullSafety()) {
                    updateTimeDataset.add(
                        UpdateTimeDb(
                            DATASET_MUNICIPAL_VIOLATION_LIST,
                            mDataSetTimeObject?.timeList?.municipalViolationList?.status
                        )
                    )
                    //mDatasetList?.municipalViolationList = null
                } else if (mDataSetTimeObject?.timeList?.municipalViolationList == null) {
                    updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_VIOLATION_LIST, true))
                }

                //Municipal Block List
                if (mDataSetTimeObject?.timeList?.municipalBlockList != null && mDataSetTimeObject?.timeList?.municipalBlockList?.status.nullSafety()) {
                    updateTimeDataset.add(
                        UpdateTimeDb(
                            DATASET_MUNICIPAL_BLOCK_LIST,
                            mDataSetTimeObject?.timeList?.municipalBlockList?.status
                        )
                    )
                } else if (mDataSetTimeObject?.timeList?.municipalBlockList == null) {
                    updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_BLOCK_LIST, true))
                }

                //Municipal Street List
                if (mDataSetTimeObject?.timeList?.municipalStreetList != null && mDataSetTimeObject?.timeList?.municipalStreetList?.status.nullSafety()) {
                    updateTimeDataset.add(
                        UpdateTimeDb(
                            DATASET_MUNICIPAL_STREET_LIST,
                            mDataSetTimeObject?.timeList?.municipalStreetList?.status
                        )
                    )
                } else if (mDataSetTimeObject?.timeList?.municipalStreetList == null) {
                    updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_STREET_LIST, true))
                }

                //Municipal City List
                if (mDataSetTimeObject?.timeList?.municipalCityList != null && mDataSetTimeObject?.timeList?.municipalCityList?.status.nullSafety()) {
                    updateTimeDataset.add(
                        UpdateTimeDb(
                            DATASET_MUNICIPAL_CITY_LIST,
                            mDataSetTimeObject?.timeList?.municipalCityList?.status
                        )
                    )
                } else if (mDataSetTimeObject?.timeList?.municipalCityList == null) {
                    updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_CITY_LIST, true))
                }

                //Municipal State List
                if (mDataSetTimeObject?.timeList?.municipalStateList != null && mDataSetTimeObject?.timeList?.municipalStateList?.status.nullSafety()) {
                    updateTimeDataset.add(
                        UpdateTimeDb(
                            DATASET_MUNICIPAL_STATE_LIST,
                            mDataSetTimeObject?.timeList?.municipalStateList?.status
                        )
                    )
                } else if (mDataSetTimeObject?.timeList?.municipalStateList == null) {
                    updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_STATE_LIST, true))
                }
            }

//            updateTimeDataset.add(UpdateTimeDb(DATASET_REGULATION_TIME_LIST, true))
//            updateTimeDataset.add(UpdateTimeDb(DATASET_SIDE_LIST, true))
//            updateTimeDataset.add(UpdateTimeDb(DATASET_TIER_STEM_LIST, true))
//            updateTimeDataset.add(UpdateTimeDb(DATASET_STREET_LIST, true))
//            mDatasetList!!.streetList = null
//            updateTimeDataset.add(UpdateTimeDb(DATASET_METER_LIST, true))
//            mDatasetList!!.meterList = null
        } else {
            printLog("dataset", " else ")
//                dismissLoader()
            updateTimeDataset.add(UpdateTimeDb(DATASET_CANCEL_REASON_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_DECAL_YEAR_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_MAKE_MODEL_LIST, true))
//                updateTimeDataset.add(UpdateTimeDb("MakeModelList2", true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_CAR_COLOR_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_STATE_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_STREET_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_METER_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_SPACE_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_CAR_BODY_STYLE_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_VIOLATION_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_VIO_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_HOLIDAY_CALENDAR_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_SIDE_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_TIER_STEM_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_NOTES_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_REMARKS_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_REGULATION_TIME_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_LOT_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_VOID_AND_REISSUE_REASON_LIST, true))
            updateTimeDataset.add(UpdateTimeDb(DATASET_BLOCK_LIST, true))

            if (LogUtil.isMunicipalCitationEnabled()) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_VIOLATION_LIST, true))
                updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_BLOCK_LIST, true))
                updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_STREET_LIST, true))
                updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_CITY_LIST, true))
                updateTimeDataset.add(UpdateTimeDb(DATASET_MUNICIPAL_STATE_LIST, true))
            }
        }
//        updateTimeDataset.add(UpdateTimeDb(DATASET_LOT_LIST, true))
//        updateTimeDataset.add(UpdateTimeDb(DATASET_STREET_LIST, true))
        callApiParallellyRecursive(updateTimeDataset, 0, 1)
        if (mDataSetTimeObject == null || mDataSetTimeObject?.timeList?.cancelReasonList == null || mDataSetTimeObject?.timeList?.violationList == null || mDataSetTimeObject?.timeList?.municipalViolationList == null || mDataSetTimeObject?.timeList?.mBlockList == null || mDataSetTimeObject?.timeList?.carColorList == null || mDataSetTimeObject?.timeList?.remarksList == null) {


            mDataSetTimeObject = TimestampDatatbase()
            mDataSetTimeObject?.timeList = UpdateTimeDataList()

            mDataSetTimeObject?.timeList?.mBlockList = UpdateTimeDb(DATASET_BLOCK_LIST, false)
            mDataSetTimeObject?.timeList?.mCityZoneList = UpdateTimeDb("CityZoneList", false)
            mDataSetTimeObject?.timeList?.mDeviceList = UpdateTimeDb("DeviceList", false)
            mDataSetTimeObject?.timeList?.mEquipmentList = UpdateTimeDb("EquipmentList", false)
            mDataSetTimeObject?.timeList?.mSpaceList = UpdateTimeDb(DATASET_SPACE_LIST, false)
            mDataSetTimeObject?.timeList?.mPrinterList = UpdateTimeDb("PrinterList", false)
            mDataSetTimeObject?.timeList?.mSquadList = UpdateTimeDb("SquadList", false)
            mDataSetTimeObject?.timeList?.mVoidAndReissueList =
                UpdateTimeDb(DATASET_VOID_AND_REISSUE_REASON_LIST, false)
            mDataSetTimeObject?.timeList?.makeModelColorData =
                UpdateTimeDb("MakeModelColorData", false)
            mDataSetTimeObject?.timeList?.activityLayout = UpdateTimeDb("ActivityLayout", false)
            mDataSetTimeObject?.timeList?.activityList = UpdateTimeDb("ActivityList", false)
            mDataSetTimeObject?.timeList?.agencyList = UpdateTimeDb("AgencyList", false)
            mDataSetTimeObject?.timeList?.beatList = UpdateTimeDb("BeatList", false)
            mDataSetTimeObject?.timeList?.cancelReasonList =
                UpdateTimeDb(DATASET_CANCEL_REASON_LIST, false)
            mDataSetTimeObject?.timeList?.carBodyStyleList =
                UpdateTimeDb(DATASET_CAR_BODY_STYLE_LIST, false)
            mDataSetTimeObject?.timeList?.carColorList = UpdateTimeDb(DATASET_CAR_COLOR_LIST, false)
            mDataSetTimeObject?.timeList?.carMakeList = UpdateTimeDb(DATASET_MAKE_MODEL_LIST, false)
//            mDataSetTimeObject?.timeList?.carModelList = UpdateTimeDb("MakeModelList2", false)
            mDataSetTimeObject?.timeList?.citationData = UpdateTimeDb("CitationData", false)
            mDataSetTimeObject?.timeList?.commentsList = UpdateTimeDb("CommentsList", false)
            mDataSetTimeObject?.timeList?.decalYearList =
                UpdateTimeDb(DATASET_DECAL_YEAR_LIST, false)
            mDataSetTimeObject?.timeList?.directionList = UpdateTimeDb("DirectionList", false)
            mDataSetTimeObject?.timeList?.lotList = UpdateTimeDb(DATASET_LOT_LIST, false)
            mDataSetTimeObject?.timeList?.stateList = UpdateTimeDb(DATASET_STATE_LIST, false)
            mDataSetTimeObject?.timeList?.streetList = UpdateTimeDb(DATASET_STREET_LIST, false)
            mDataSetTimeObject?.timeList?.meterList = UpdateTimeDb(DATASET_METER_LIST, false)
            mDataSetTimeObject?.timeList?.violationList =
                UpdateTimeDb(DATASET_VIOLATION_LIST, false)
            mDataSetTimeObject?.timeList?.vioList = UpdateTimeDb(DATASET_VIO_LIST, false)
            mDataSetTimeObject?.timeList?.holidayCalendarList = UpdateTimeDb(DATASET_HOLIDAY_CALENDAR_LIST, false)
            mDataSetTimeObject?.timeList?.sideList = UpdateTimeDb(DATASET_SIDE_LIST, false)
            mDataSetTimeObject?.timeList?.tierStemList = UpdateTimeDb(DATASET_TIER_STEM_LIST, false)
            mDataSetTimeObject?.timeList?.notesList = UpdateTimeDb(DATASET_NOTES_LIST, false)
            mDataSetTimeObject?.timeList?.remarksList = UpdateTimeDb(DATASET_REMARKS_LIST, false)
            mDataSetTimeObject?.timeList?.regulationTimeList =
                UpdateTimeDb(DATASET_REGULATION_TIME_LIST, false)
            mDataSetTimeObject?.timeList?.radioList = UpdateTimeDb("RadioList", false)

            if (LogUtil.isMunicipalCitationEnabled()) {
                mDataSetTimeObject?.timeList?.municipalViolationList = UpdateTimeDb(
                    DATASET_MUNICIPAL_VIOLATION_LIST, false
                )
                mDataSetTimeObject?.timeList?.municipalBlockList = UpdateTimeDb(
                    DATASET_MUNICIPAL_BLOCK_LIST, false
                )
                mDataSetTimeObject?.timeList?.municipalStreetList = UpdateTimeDb(
                    DATASET_MUNICIPAL_STREET_LIST, false
                )
                mDataSetTimeObject?.timeList?.municipalCityList = UpdateTimeDb(
                    DATASET_MUNICIPAL_CITY_LIST, false
                )
                mDataSetTimeObject?.timeList?.municipalStateList = UpdateTimeDb(
                    DATASET_MUNICIPAL_STATE_LIST, false
                )
            }
        }
        if (mDataSetTimeObject != null) {
            mDataSetTimeObject = setLastUpdateDataSetStatus(mDataSetTimeObject, false)
        }
        if (mDataSetTimeObject != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                welcomeScreenViewModel.insertUpdatedTime(mDataSetTimeObject!!)
            }

        }
    }

    //Start of set all dropdowns
    private suspend fun setAllDropdown() {
        mWelcomeListDataSet = mainActivityViewModel.getWelcomeObject()
        if (mWelcomeListDataSet != null) {
            mDatabaseWelcomeList = mWelcomeListDataSet?.welcomeList ?: WelcomeList()
            setLicenseKeyForAlpr()
        }

        setLoginTimeStamp()
        setDropdownStreet(mainActivityViewModel.getStreetListFromDataSet())
        setDropdownBlockList(mainActivityViewModel.getBlockListFromDataSet())
        setDropdownSide(mainActivityViewModel.getSideListFromDataSet())

        if (mWelcomeListDataSet != null) {
            try {
                setDropdownActivity(mWelcomeListDataSet?.welcomeList?.activityStats)
                setDropdownComments(mWelcomeListDataSet?.welcomeList?.commentStates)
                setDropdownSupervisor(mWelcomeListDataSet?.welcomeList?.supervisorStats)
                setDropdownBeat(mWelcomeListDataSet?.welcomeList?.beatStats)
                setDropdownZone(mWelcomeListDataSet?.welcomeList?.zoneStats, "")
                setDropdownPBCZone(mWelcomeListDataSet?.welcomeList?.pbcZoneStats)
                setDropdownRadio(mWelcomeListDataSet?.welcomeList?.radioStats)
                setDropdownShift(mWelcomeListDataSet?.welcomeList?.shiftStats)
                setDropdownAgency(mWelcomeListDataSet?.welcomeList?.agencyStats)
                setDropdownDevice(mWelcomeListDataSet?.welcomeList?.deviceStats)
                setDropdownEquipment(mWelcomeListDataSet?.welcomeList?.equipmentStates)
                setDropdownSquad(mWelcomeListDataSet?.welcomeList?.squadStates)
                if (mAutoComTextViewLot != null) {
                    setDropdownLot()
                }
                if (AppUtils.sixActionButtonVisibilityCheck("ACTIVITY_KEY_IMAGES")) {
                    ivCameraIcon.post {
                        ivCameraIcon.visibility = View.VISIBLE
                    }
                } else {
                    ivCameraIcon.post {
                        ivCameraIcon.visibility = View.GONE
                    }
                }

                try {
                    if (sharedPreference.read(
                            SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API, true
                        )
                    ) {
                        lifecycleScope.launch {
                            mainActivityViewModel.sendActionToMain(
                                MainActivityAction.EventActivityLogAPI(
                                    "Login"
                                )
                            )
                            sharedPreference.write(
                                SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API, false
                            )
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                /**
                 * Upload API LOG File whenever officer come to activity form
                 */
                lifecycleScope.launch {
                    //mainActivityViewModel.sendActionToMain(MainActivityAction.EventUploadAPILogTextFile)
                }
                /**
                 * In case violation list get empty then refill all the data set again
                 */
                isDataSetListEmpty()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    //Call Api For Event Activity Log
    private fun callEventActivityLogApi() {
        if (!requireContext().isInternetAvailable()) {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
            return
        }

        val lat =
            sharedPreference.read(SharedPrefKey.LAT, "0.0")?.nullSafety()?.toDoubleOrNull() ?: 0.0
        val long =
            sharedPreference.read(SharedPrefKey.LONG, "0.0")?.nullSafety()?.toDoubleOrNull() ?: 0.0

        val user = mWelcomeResponseData?.data?.getOrNull(0)?.responsedata?.user
        val initiatorId = user?.mApprovedBy?.mInitiatorId.orEmpty()

        activityUpdateRequest.apply {
            this.initiatorId = initiatorId
            activity_id = mActivityId
            initiatorRole = "DEFAULT_SITE_OFFICER"
            activityType = "ActivityUpdate"
            image_1 = mImages.getOrNull(0).orEmpty()
            image_2 = mImages.getOrNull(1).orEmpty()
            image_3 = mImages.getOrNull(2).orEmpty()
            siteId = getSiteId(requireContext())
            logType = Constants.LOG_TYPE_NODE_PORT
            mShift = user?.officerShift.orEmpty()
            activityName = mSelectedSActivityStat?.activityKey.orEmpty()
            mBlock = mAutoComTextViewBlock?.text?.toString().orEmpty()
            mDeviceId = selectedDeViceId.mDeviceFriendlyName?.toString().orEmpty()
            mStreet = mAutoComTextViewStreet?.text?.toString().orEmpty()
            mSide = mAutoComTextViewSideofStreet?.text?.toString().orEmpty()
            mSquad = mAutoComTextViewSquad?.text?.toString().orEmpty()
            latitude = lat
            longitude = long
            clientTimestamp = splitDateLpr("")
        }

        welcomeScreenViewModel.callActivityLogAPI(activityUpdateRequest)
    }

    //set login timestamp
    private fun setLoginTimeStamp() {
        val last = runCatching {
            splitDateWelcome(sharedPreference.read(SharedPrefKey.PRE_TIME, ""), mZone)
        }.getOrDefault("")

        val current = runCatching {
            splitDateWelcome(sharedPreference.read(SharedPrefKey.CURRENT_TIME, ""), mZone)
        }.getOrDefault("")

        mTextViewPreLogin.text = last
        mTextViewCurLogin.text = current
    }

    private fun setUserDetails() {
        val layouts = mCitationLayout
        if (layouts.isNullOrEmpty()) {
            //TODO Empty Layout
            //linearLayoutEmptyActivity.visibility = View.VISIBLE
            return
        }

        DialogUtil.showLoader(
            requireContext(),
            message = getString(R.string.loader_text_please_wait_we_are_loading_data)
        )

        layouts.forEachIndexed { iCit, layout ->
            // last item actions
            if (iCit == layouts.size - 1) {
                //TODO Empty Layout
                //linearLayoutEmptyActivity.visibility = View.GONE
                layButtons.showView()
                btnDoneActivity.hideView()

                getTimestampFromDb()
                callWelcomeApi()
            }

            val comp = layout.component?.lowercase(Locale.getDefault()).orEmpty()
            val fields = layout.fields.orEmpty()

            when (comp.lowercase(Locale.getDefault())) {
                "login" -> {
                    mTextViewLoginDetails.visibility = View.VISIBLE
                    val headerSize = fields.size
                    cardPreLogin.visibility = View.VISIBLE
                    cardCurLogin.visibility = if (headerSize >= 2) View.VISIBLE else View.GONE
                }

                "officer", "OfficerActivity" -> {
                    mTextViewOfficerDetails.visibility = View.VISIBLE
                    val headerSize = fields.size
                    layOfficerId.visibility = if (headerSize == 1) View.GONE else View.VISIBLE
                    layBadgeId.visibility = View.VISIBLE

                    fields.forEach { field ->
                        if (field.name.equals("squad", ignoreCase = true)) {
                            field.tag = "dropdown"
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layOfficer,
                                component = layout.component.nullSafety(),
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewSquad = autoCompleteTextView
                            mTextInputLayoutSquad = textInputLayout
                            Util.setFieldCaps(requireContext(), mAutoComTextViewSquad!!)
                        }
                    }
                }

                "other" -> {
                    layOtherDetails.visibility = View.VISIBLE
                    layMainOtherDetails.visibility = View.VISIBLE

                    fields.forEach { field ->
                        when (field.name?.lowercase(Locale.getDefault())) {
                            "activity" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layOtherDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewActivity = autoCompleteTextView
                                mTextInputLayoutActivity = textInputLayout
                            }

                            "comments" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layOtherDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewComments = autoCompleteTextView
                                mTextInputLayoutComments = textInputLayout
                            }

                            "notes" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layOtherDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mEdittextNote = autoCompleteTextView
                                mTextInputLayoutNote = textInputLayout
                            }

                            else -> {
                                // safely collect extras instead of the broken null-array approach
                                try {
                                    latestLayoutOther++
                                    constructLayoutBuilder.checkTypeOfField(
                                        field, layOtherDetails, layout.component!!, requireContext()
                                    )
                                    // store/use `extra` if needed (example: ignore or keep in a list)
                                } catch (_: Exception) { /* ignore malformed field */
                                }
                            }
                        }
                    }
                }

                "asset" -> {
                    mTextViewAssetDetails.visibility = View.VISIBLE
                    fields.forEach { field ->
                        when (field.name?.lowercase(Locale.getDefault())) {
                            "supervisor" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewSuper = autoCompleteTextView
                                mTextInputLayoutSuper = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewSuper!!)
                            }

                            "beat" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewBeat = autoCompleteTextView
                                mTextInputLayoutBeat = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewBeat!!)
                            }

                            "zone" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewZone = autoCompleteTextView
                                mTextInputLayoutZone = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewZone!!)
                            }

                            "city_zone" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewPbcZone = autoCompleteTextView
                                mTextInputLayoutPbcZone = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewPbcZone!!)
                            }

                            "lot" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewLot = autoCompleteTextView
                                mTextInputLayoutLot = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewLot!!)
                            }

                            "radio" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewRadio = autoCompleteTextView
                                mTextInputLayoutRadio = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewRadio!!)
                            }

                            "shift" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewShift = autoCompleteTextView
                                mTextInputLayoutShift = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewShift!!)
                            }

                            "agency" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewAgency = autoCompleteTextView
                                mTextInputLayoutAgency = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewAgency!!)
                            }

                            "signature" -> {
                                mTextViewSignName.visibility = View.VISIBLE
                                imageViewSignature.visibility = View.VISIBLE
                            }

                            "deviceid" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewDeviceId = autoCompleteTextView
                                mTextInputLayoutDeviceId = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewDeviceId!!)
                            }

                            "printer_equipment", "printer", "equipment" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layAssetDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewPrinterEquipment = autoCompleteTextView
                                mTextInputLayoutPrinterEquipment = textInputLayout

                                Util.setFieldCaps(
                                    requireContext(), mAutoComTextViewPrinterEquipment!!
                                )
                            }

                            "squad" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field,
                                    layAssetDetails,
                                    layout.component.nullSafety(),
                                    requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewSquad = autoCompleteTextView
                                mTextInputLayoutSquad = textInputLayout

                                Util.setFieldCaps(requireContext(), mAutoComTextViewSquad!!)
                            }

                            else -> {
                                try {
                                    val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                        field, layAssetDetails, layout.component!!, requireContext()
                                    ) ?: Triple(null, null, null)

                                    mAutoComTextViewExtra = autoCompleteTextView
                                    mTextInputLayoutExtra = textInputLayout
                                } catch (_: Exception) { /* ignore */
                                }
                            }
                        }
                    }
                }

                "location" -> {
                    mTextViewLocationDetails.visibility = View.VISIBLE
                    fields.forEach { field ->
                        when (field.name?.lowercase(Locale.getDefault())) {
                            "block" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layLocationDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewBlock = autoCompleteTextView
                                mTextInputLayoutBlock = textInputLayout

                                mAutoComTextViewBlock?.isAllCaps = true
                                Util.setFieldCaps(requireContext(), mAutoComTextViewBlock!!)
                            }

                            "zone" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layLocationDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewZone = autoCompleteTextView
                                mTextInputLayoutZone = textInputLayout
                            }

                            "street" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layLocationDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)
                                mAutoComTextViewStreet = autoCompleteTextView
                                mTextInputLayoutStreet = textInputLayout

                                mAutoComTextViewStreet?.isAllCaps = true
                                Util.setFieldCaps(requireContext(), mAutoComTextViewStreet!!)
                                getGeoAddress()
                            }

                            "side" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layLocationDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewSideofStreet = autoCompleteTextView
                                mTextInputLayoutSideofStreet = textInputLayout
                            }

                            "lot" -> {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    field, layLocationDetails, layout.component!!, requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewLot = autoCompleteTextView
                                mTextInputLayoutLot = textInputLayout
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setActivityData() {
        val user = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user

        val first = user?.officerFirstName.orEmpty().trim()
        val last = user?.officerLastName.orEmpty().trim()
        val userName = listOf(first, last).filter { it.isNotEmpty() }.joinToString(" ")

        mTextViewUserName.text = userName
        mTextViewSignName.text = userName
        mTextViewBadgeId.setText(user?.officerBadgeId.orEmpty())
        mTextViewOfficerId.setText(splitID(user?.siteOfficerId.orEmpty()))

        mainActivityViewModel.setToolbarOfficerName("$first $last".trim())

        showHideInventoryModule()
    }

    private fun showHideScanVehicleStickerModule(isFromApiResponse: Boolean) {
        if (isFromApiResponse) {
            lifecycleScope.launch {
                mainActivityViewModel.sendActionToMain(MainActivityAction.EventSetupHemMenu)
            }
        }

        if (mainActivityViewModel.showAndEnableScanVehicleStickerModule) {
            btnScanSticker.showView()
        } else {
            btnScanSticker.hideView()
        }
    }

    private fun showHideDirectedEnforcementModule(@Suppress("SameParameterValue") isFromApiResponse: Boolean) {
        if (isFromApiResponse) {
            lifecycleScope.launch {
                mainActivityViewModel.sendActionToMain(MainActivityAction.EventSetupHemMenu)
            }
        }
    }

    private fun showHideInventoryModule() {
        if (mainActivityViewModel.showAndEnableInventoryModule) {
            getEquipmentInventory(mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user?.siteOfficerId.nullSafety())
        } else {
            cvQrCode.hideView()
        }
    }

    /**
     * Function used to get header footer details from settings file and call download API
     */
    private fun downloadHeaderFooterForFacsimile() {
        if (mainActivityViewModel.showAndEnableHeaderFooterInFacsimile && (!FileUtil.checkHeaderImageFileExist() || !FileUtil.checkFooterImageFileExist())) {
            viewLifecycleOwner.lifecycleScope.launch {
                val settingsList = mainActivityViewModel.getSettingsListFromDataSet()

                val headerUrl = settingsList?.firstOrNull {
                    it.type?.trim().equals(
                        SETTINGS_FLAG_HEADER_URL_FOR_FACSIMILE, true
                    )
                }?.mValue?.trim().nullSafety()


                val footerUrl = settingsList?.firstOrNull {
                    it.type?.trim().equals(
                        SETTINGS_FLAG_FOOTER_URL_FOR_FACSIMILE, true
                    )
                }?.mValue?.trim().nullSafety()

                callGetHeaderFooterDownloadLinkAPI(true, headerUrl)
                callGetHeaderFooterDownloadLinkAPI(false, footerUrl)
            }
        }
    }


    /**
     * Function used to call download header footer from secure url
     */
    private fun downloadHeaderFooterForFacsimileFromSecureUrl(
        isHeader: Boolean, secureImageUrl: String
    ) {
        if (mainActivityViewModel.showAndEnableHeaderFooterInFacsimile && (!FileUtil.checkHeaderImageFileExist() || !FileUtil.checkFooterImageFileExist())) {
            if (isHeader) {
                welcomeScreenViewModel.callDownloadHeaderFile(secureImageUrl)
            } else {
                welcomeScreenViewModel.callDownloadFooterFile(secureImageUrl)
            }

        }
    }


    /* Call Api For Citation Dataset For Activity page Type */
    private fun callCitationDatasetApiActivityForm(mType: String, index: Long) {
        if (isInternetAvailable(requireContext())) {
            val mDropdownDatasetRequest = DropdownDatasetRequest()
            mDropdownDatasetRequest.type = mType
            mDropdownDatasetRequest.shard = index
            welcomeScreenViewModel.callCitationDatasetAPI(mDropdownDatasetRequest)
        } else {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For Citation Dataset For Activity page Type */
    private fun callCitationDatasetApi(mType: String, pageIndex: Long) {
        if (isInternetAvailable(requireContext())) {
            val mDropdownDatasetRequest = DropdownDatasetRequest()
            mDropdownDatasetRequest.type = mType
            mDropdownDatasetRequest.shard = pageIndex
            welcomeScreenViewModel.callCitationDatasetAPI(mDropdownDatasetRequest)
        } else {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For Welcome page details */
    private fun callWelcomeApi() {
        mRequestTimeStart = System.currentTimeMillis()
        if (isInternetAvailable(requireContext())) {
            welcomeScreenViewModel.callWelcomeAPI()
        } else {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For update site officer */
    private fun callUpdateSiteOfficerApi() {
        DialogUtil.showLoader(
            requireContext(),
            message = getString(R.string.loader_text_please_wait_we_are_loading_data)
        )

        lifecycleScope.launch {
            val mData = welcomeScreenViewModel.getWelcomeForm()
            if (mData == null) {
                requireContext().toast(getString(R.string.err_msg_connection_was_refused))
                return@launch
            }

            if (!requireContext().isInternetAvailable()) {
                requireContext().toast(getString(R.string.err_msg_connection_was_refused))
                return@launch
            }

            val updateReq = UpdateSiteOfficerRequest().apply {
                siteId = mData.siteId
                siteOfficerId = mData.siteOfficerId
                updatePackage = UpdatePackage().apply {
                    officerBeat = mSelectedBeatStat?.beatName.orEmpty()
                    officerRadio = mSelectedRadioStat?.radioName.orEmpty()
                    officerShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                    officerSupervisor = mSelectedSupervisorStat?.mSuperName.nullSafety()
                    mOfficerSupervisorBadgeId =
                        mSelectedSupervisorStat?.mSuperBadgeId.nullSafety().toIntOrNull() ?: 0
                    officerZone = mSelectedZoneStat?.zoneName.orEmpty()
                    mofficerAgency = mSelectedAgency.orEmpty()
                    mofficerDeviceId = selectedDeViceId
                    mCityZone = mAutoComTextViewPbcZone?.text?.toString().orEmpty()
                    mEquipment = mAutoComTextViewPrinterEquipment?.text?.toString().orEmpty()
                    mOfficerSquad = mAutoComTextViewSquad?.text?.toString().orEmpty()
                    mSignature = uploadSignatureLink?.takeIf { it.isNotEmpty() } ?: ""
                    mLot = mAutoComTextViewLot?.text?.toString().orEmpty()
                }
            }

            welcomeScreenViewModel.callUpdateSiteOfficerAPI(updateReq)
        }
    }

    private fun init() {
        try {
            lifecycleScope.launch {
                val activityResponse = welcomeScreenViewModel.getActivityLayout()
                val response = activityResponse?.data?.firstOrNull()?.response
                if (activityResponse != null && response != null) {
                    if (response.isNotEmpty().nullSafety()) {
                        mCitationLayout = response
                    }
                }

                //delay(2000)
                setUserDetails()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val mydir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Constants.FILE_NAME + Constants.SIGNATURE
        )
        fileSignature = File(mydir.absolutePath, getSignatureFileNameWithExt())
        if (fileSignature!!.exists()) {
            imageViewSignature.setImageURI(Uri.fromFile(fileSignature))
        }
    }


    //Start of set Dropdown values
    private fun setDropdownAgency(mApplicationList: List<DatasetResponse>?) {
        requireActivity().hideSoftKeyboard()

        if (mApplicationList.isNullOrEmpty() || mAutoComTextViewAgency == null) return

        lifecycleScope.launch {
            val mAgency = welcomeScreenViewModel.getWelcomeForm()?.agency
            val dropdownList = mApplicationList.map { it.agency_name.orEmpty() }
            val preselectIndex = mApplicationList.indexOfFirst {
                !mAgency.isNullOrEmpty() && it.agency_name.equals(
                    mAgency, ignoreCase = true
                )
            }

            withContext(Dispatchers.Main) {
                val autoTv = mAutoComTextViewAgency!!
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList
                )

                autoTv.threshold = 1
                autoTv.setAdapter(adapter)

                if (preselectIndex >= 0) {
                    autoTv.setText(dropdownList[preselectIndex], false)
                    mSelectedAgency = mApplicationList[preselectIndex].agency_name
                    mApplicationList[preselectIndex].mLock?.let { lock ->
                        sharedPreference.write(SharedPrefKey.LOCK_LOCATION_BASED_ON_AGENCY, lock)
                        if (lock) {
                            autoTv.isClickable = false
                            autoTv.setOnClickListener(null)
                            autoTv.dismissDropDown()
                            autoTv.dropDownHeight = 0
                            autoTv.isFocusable = false
                        }
                    }
                }

                autoTv.setOnItemClickListener { parent, _, position, _ ->
                    val selected = parent.getItemAtPosition(position) as String
                    val index = mApplicationList.getIndexOfAgency(selected)
                    if (index >= 0) {
                        mSelectedAgency = mApplicationList[index].agency_name
                        requireActivity().hideSoftKeyboard()
                        mApplicationList[index].mLock?.let { lock ->
                            sharedPreference.write(
                                SharedPrefKey.LOCK_LOCATION_BASED_ON_AGENCY, lock
                            )
                            if (lock) {
                                autoTv.isClickable = false
                                autoTv.setOnClickListener(null)
                                autoTv.dismissDropDown()
                                autoTv.dropDownHeight = 0
                                autoTv.isFocusable = false
                            }
                        }
                    }
                }

                if (autoTv.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutAgency
                    )
                }
            }
        }
    }

    //set value to Device dropdown
    private fun setDropdownDevice(mApplicationList: List<DeviceResponseItem>?) {
        requireActivity().hideSoftKeyboard()
        if (mApplicationList.isNullOrEmpty() || mAutoComTextViewDeviceId == null) return

        lifecycleScope.launch {
            mCombinationId = welcomeScreenViewModel.getWelcomeForm()?.officerDeviceName

            val list = mApplicationList
            val dropdownList = list.map { it.friendlyName.orEmpty() }

            val lockedIndex = list.indexOfFirst { it.androidId?.equals(mUUID) == true }
            val preselectIndex = if (lockedIndex >= 0) {
                lockedIndex
            } else {
                if (mCombinationId.isNullOrEmpty()) -1
                else list.indexOfFirst {
                    it.friendlyName.equals(mCombinationId, ignoreCase = true) || it.deviceId.equals(
                        mCombinationId, ignoreCase = true
                    )
                }
            }

            withContext(Dispatchers.Main) {
                val autoTv = mAutoComTextViewDeviceId!!

                if (lockedIndex >= 0) {
                    autoTv.isClickable = false
                    autoTv.setOnClickListener(null)
                    autoTv.dismissDropDown()
                    autoTv.dropDownHeight = 0
                    autoTv.isFocusable = false
                }

                if (preselectIndex >= 0) {
                    autoTv.setText(dropdownList[preselectIndex], false)
                    selectedDeViceId.mDeviceFriendlyName = list[preselectIndex].friendlyName
                    selectedDeViceId.mDeviceId = list[preselectIndex].friendlyName
                }

                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList
                )
                autoTv.threshold = 1
                autoTv.setAdapter(adapter)

                autoTv.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    val selected =
                        parent.getItemAtPosition(position) as? String ?: return@OnItemClickListener
                    val index = list.getIndexOfDeviceFriendlyName(selected)
                    if (index >= 0) {
                        selectedDeViceId.mDeviceFriendlyName = list[index].friendlyName
                        selectedDeViceId.mDeviceId = list[index].friendlyName
                        requireActivity().hideSoftKeyboard()
                    }
                }

                if (autoTv.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutDeviceId
                    )
                }
            }
        }
    }

    //set value to Device dropdown
    private fun setDropdownEquipment(mApplicationList: List<ResponseItem>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewPrinterEquipment ?: return
        if (mApplicationList.isNullOrEmpty()) return

        lifecycleScope.launch(Dispatchers.Main) {
            val selectedDeviceId = welcomeScreenViewModel.getWelcomeForm()?.equipmentId
            val dropdownList = mApplicationList.map { it.equipmentName.orEmpty() }
            val preselectIndex = if (selectedDeviceId.isNullOrBlank()) {
                -1
            } else {
                mApplicationList.indexOfFirst {
                    it.equipmentId.equals(
                        selectedDeviceId, ignoreCase = true
                    )
                }
            }

            autoTv.post {
                if (preselectIndex >= 0) {
                    autoTv.setText(dropdownList[preselectIndex], false)
                }

                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList
                )
                autoTv.threshold = 1
                autoTv.setAdapter(adapter)
                autoTv.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }

                if (autoTv.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(),
                        textInputLayout = mTextInputLayoutPrinterEquipment
                    )
                }
            }
        }
    }


    //set value to Device dropdown
    private fun setDropdownSquad(mApplicationList: List<ResponseSquadItem>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewSquad ?: return
        if (mApplicationList.isNullOrEmpty()) return

        lifecycleScope.launch(Dispatchers.Main) {
            val selectedSquad = welcomeScreenViewModel.getWelcomeForm()?.officerSquad
            val preselectValue = if (!selectedSquad.isNullOrBlank()) {
                mApplicationList.firstOrNull {
                    it.squadName.orEmpty().equals(selectedSquad, true)
                }?.squadName
            } else null

            val dropdownList = mApplicationList.map { it.squadName.orEmpty() }
                .sortedWith(String.CASE_INSENSITIVE_ORDER)

            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList)
            autoTv.apply {
                threshold = 1
                setAdapter(adapter)
                preselectValue?.let { setText(it, false) }
                onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
            }
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet(mApplicationList: List<DatasetResponse>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewStreet ?: return
        if (mApplicationList.isNullOrEmpty()) return

        lifecycleScope.launch(Dispatchers.Main) {
            val dropdownList = mApplicationList.map { it.street_name.orEmpty() }
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList)

            autoTv.apply {
                threshold = 1
                setAdapter(adapter)
                onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }

                if (tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutStreet
                    )
                }
            }
        }
    }


    //set value to side dropdown
    private fun setDropdownSide(mApplicationList: List<DatasetResponse>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewSideofStreet ?: return
        if (mApplicationList.isNullOrEmpty()) return

        lifecycleScope.launch(Dispatchers.Main) {
            val dropdownList = mApplicationList.map { it.sideName.orEmpty() }.sorted()
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList)

            autoTv.apply {
                threshold = 1
                setAdapter(adapter)
                onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
                if (tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutSideofStreet
                    )
                }
            }
        }
    }


    //set value to Street dropdown
    private fun setDropdownBlockList(mApplicationList: List<DatasetResponse>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewBlock ?: return
        if (mApplicationList.isNullOrEmpty()) return

        lifecycleScope.launch(Dispatchers.Main) {
            val dropdownList = mApplicationList.map { it.blockName.orEmpty() }
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList)

            autoTv.apply {
                threshold = 1
                setAdapter(adapter)
                onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
                if (tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutBlock
                    )
                }
            }
        }
    }

    //set value to Activity dropdown
    private fun setDropdownActivity(mApplicationList: List<ActivityStat>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewActivity ?: return
        if (mApplicationList.isNullOrEmpty()) return

        lifecycleScope.launch(Dispatchers.Main) {
            val dropdownList = mApplicationList.map { it.activity.orEmpty() }.sorted()
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList)

            autoTv.apply {
                threshold = 1
                setAdapter(adapter)

                // preserve previous behavior: default to first item from the original list if available
                mSelectedSActivityStat = mApplicationList.firstOrNull()

                onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    requireActivity().hideSoftKeyboard()
                    val selected = parent.getItemAtPosition(position).toString()
                    val index = mApplicationList.getIndexOfActivity(selected)
                    if (index in mApplicationList.indices) {
                        mSelectedSActivityStat = mApplicationList[index]
                    }
                }

                if (tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutActivity
                    )
                }
            }
        }
    }

    //set value to Comments dropdown
    private fun setDropdownComments(mApplicationList: List<CommentState>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewComments ?: return
        if (mApplicationList.isNullOrEmpty()) return

        lifecycleScope.launch(Dispatchers.Main) {
            val dropdownList = mApplicationList.map { it.name.orEmpty() }
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList)

            autoTv.apply {
                threshold = 1
                setAdapter(adapter)
                onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
                if (tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutComments
                    )
                }
            }
        }
    }

    //set value to Supervisor dropdown
    private fun setDropdownSupervisor(mApplicationList: List<SupervisorStat>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewSuper ?: return
        if (mApplicationList.isNullOrEmpty()) return

        lifecycleScope.launch(Dispatchers.Main) {
            val names = mApplicationList.map { it.mSuperName.orEmpty() }
            val defaultSupervisor =
                mWelcomeResponseData?.data?.getOrNull(0)?.responsedata?.user?.officerSupervisor
            val defaultPos = defaultSupervisor?.let { def ->
                names.indexOfFirst { it.equals(def, ignoreCase = true) }
            } ?: -1

            if (defaultPos >= 0) {
                autoTv.setText(names[defaultPos])
                mSelectedSupervisorStat = mApplicationList[defaultPos]
            }

            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, names)
            autoTv.threshold = 1
            autoTv.setAdapter(adapter)

            autoTv.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                val selectedName = parent.getItemAtPosition(position)?.toString().orEmpty()
                val index = mApplicationList.indexOfFirst {
                    it.mSuperName.equals(
                        selectedName, ignoreCase = true
                    )
                }
                if (index >= 0) mSelectedSupervisorStat = mApplicationList[index]
                requireActivity().hideSoftKeyboard()
            }

            if (autoTv.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                autoTv.setListOnlyDropDown(
                    context = requireContext(), textInputLayout = mTextInputLayoutSuper
                )
            }
        }
    }

    //set value to beat dropdown
    private fun setDropdownBeat(mApplicationList: List<BeatStat>?) {
        requireActivity().hideSoftKeyboard()
        val autoTv = mAutoComTextViewBeat ?: return
        if (mApplicationList.isNullOrEmpty()) return

        val setBeatFieldEmptyAfterEveryLogin = getBeatSetEmptyFromSetting()
        lifecycleScope.launch(Dispatchers.Main) {
            val names = mApplicationList.map { it.beatName.orEmpty() }

            val officerBeat =
                mWelcomeResponseData?.data?.getOrNull(0)?.responsedata?.user?.officerBeat.orEmpty()

            val defaultPos = if (setBeatFieldEmptyAfterEveryLogin && sharedPreference.read(
                    SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API, true
                )
            ) {
                -1
            } else {
                names.indexOfFirst { it.equals(officerBeat, ignoreCase = true) }
            }

            if (defaultPos >= 0) {
                autoTv.setText(names[defaultPos])
                mSelectedBeatStat = mApplicationList[defaultPos]
            }

            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, names)
            autoTv.threshold = 1
            autoTv.setAdapter(adapter)

            autoTv.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                val selectedName = parent.getItemAtPosition(position)?.toString().orEmpty()
                val index = mApplicationList.indexOfFirst {
                    it.beatName.equals(selectedName, ignoreCase = true)
                }
                if (index >= 0) {
                    mSelectedBeatStat = mApplicationList[index]

                    mWelcomeListDataSet?.welcomeList?.zoneStats?.forEach { zoneStat ->
                        val beatZone = mApplicationList[index].zone?.trim().orEmpty()
                        if (beatZone.equals(zoneStat.mCityZoneName, ignoreCase = true)) {
                            zoneStat.zoneName?.let {
                                setDropdownZone(
                                    mWelcomeListDataSet?.welcomeList?.zoneStats, it
                                )
                            }
                        }
                    }
                }
                requireActivity().hideSoftKeyboard()
            }

            if (autoTv.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                autoTv.setListOnlyDropDown(
                    context = requireContext(), textInputLayout = mTextInputLayoutBeat
                )
            }
        }
    }

    //set value to zone dropdown
    private fun setDropdownZone(mApplicationList: List<ZoneStat>?, value: String) {
        requireActivity().hideSoftKeyboard()

        val items = mApplicationList.orEmpty()
        val autoTv = mAutoComTextViewZone ?: return
        if (items.isEmpty()) return

        val userZone = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user?.officerZone

        lifecycleScope.launch {
            val names = withContext(Dispatchers.Default) {
                items.map { it.zoneName.orEmpty() }
            }

            val target = value.ifBlank { userZone.orEmpty() }
            val defaultIndex = names.indexOfFirst { it.equals(target, ignoreCase = true) }

            withContext(Dispatchers.Main) {
                if (defaultIndex >= 0) {
                    autoTv.setText(names[defaultIndex], false)
                    mSelectedZoneStat = items[defaultIndex]
                }

                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, names)
                autoTv.threshold = 1
                autoTv.setAdapter(adapter)

                autoTv.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                    // adapter position maps to items[position]
                    if (position in items.indices) {
                        mSelectedZoneStat = items[position]
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
                            )
                        ) {
                            mSelectedAgency = "CPA " + items[position].zoneName.orEmpty()
                            mAutoComTextViewAgency?.setText(mSelectedAgency)
                        }
                    }
                    requireActivity().hideSoftKeyboard()
                }

                if (autoTv.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutZone
                    )
                }
            }
        }
    }

    private fun setDropdownPBCZone(mApplicationList: List<ZoneStat>?) {
        requireActivity().hideSoftKeyboard()

        val items = mApplicationList.orEmpty()
        val autoTv = mAutoComTextViewPbcZone ?: return
        if (items.isEmpty()) return

        val userCityZone = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user?.mCityZone

        lifecycleScope.launch {
            val sortedNames = withContext(Dispatchers.Default) {
                items.map { it.mCityZoneName.orEmpty() }.sortedWith(String.CASE_INSENSITIVE_ORDER)
            }

            val defaultIndex =
                sortedNames.indexOfFirst { it.equals(userCityZone, ignoreCase = true) }

            withContext(Dispatchers.Main) {
                if (defaultIndex >= 0) {
                    autoTv.setText(sortedNames[defaultIndex], false)
                    mSelectedZoneStat = items.firstOrNull {
                        it.mCityZoneName.equals(sortedNames[defaultIndex], ignoreCase = true)
                    }
                }

                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, sortedNames
                )
                autoTv.threshold = 1
                autoTv.setAdapter(adapter)

                autoTv.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                    val selectedName = sortedNames.getOrNull(position).orEmpty()
                    mSelectedZoneStat = items.firstOrNull {
                        it.mCityZoneName.equals(
                            selectedName, ignoreCase = true
                        )
                    }
                    requireActivity().hideSoftKeyboard()
                }

                if (autoTv.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutZone
                    )
                }
            }
        }
    }

    private fun setDropdownRadio(mApplicationList: List<RadioSt>?) {
        requireActivity().hideSoftKeyboard()

        val items = mApplicationList.orEmpty()
        val autoTv = mAutoComTextViewRadio ?: return
        if (items.isEmpty()) return

        val userRadio = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user?.officerRadio

        lifecycleScope.launch {
            val dropdown = withContext(Dispatchers.Default) {
                items.map { it.radioName.orEmpty() }
            }

            val defaultIndex = dropdown.indexOfFirst { it.equals(userRadio, ignoreCase = true) }

            withContext(Dispatchers.Main) {
                if (defaultIndex >= 0) {
                    autoTv.setText(dropdown[defaultIndex])
                    mSelectedRadioStat = items.getOrNull(defaultIndex)
                }

                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdown)
                autoTv.threshold = 1
                autoTv.setAdapter(adapter)

                autoTv.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                    mSelectedRadioStat = items.getOrNull(position)
                    requireActivity().hideSoftKeyboard()
                }

                if (autoTv.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    autoTv.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = mTextInputLayoutRadio
                    )
                }
            }
        }
    }

    private fun setDropdownShift(mApplicationList: List<ShiftStat>?) {
        requireActivity().hideSoftKeyboard()

        val items = mApplicationList.orEmpty()
        if (items.isEmpty() || mAutoComTextViewShift == null) return

        val userShift = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user?.officerShift

        lifecycleScope.launch {
            // prepare display strings off the main thread
            val dropdown = withContext(Dispatchers.Default) {
                items.map { it.shiftName.orEmpty() }.toTypedArray()
            }

            val defaultIndex =
                items.indexOfFirst { it.shiftName.equals(userShift, ignoreCase = true) }

            mAutoComTextViewShift?.post {
                // set default selection if available
                if (defaultIndex >= 0) {
                    mAutoComTextViewShift!!.setText(dropdown[defaultIndex])
                    sharedPreference.write(SharedPrefKey.LOGIN_SHIFT, dropdown[defaultIndex])
                    mSelectedShiftStat = items[defaultIndex]
                }

                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdown)

                mAutoComTextViewShift!!.apply {
                    threshold = 1
                    setAdapter(adapter)

                    onItemClickListener = OnItemClickListener { _, _, position, _ ->
                        items.getOrNull(position)?.let { selected ->
                            mSelectedShiftStat = selected
                            sharedPreference.write(
                                SharedPrefKey.LOGIN_SHIFT, selected.shiftName.orEmpty()
                            )
                            requireActivity().hideSoftKeyboard()
                        }
                    }

                    if (tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                        setListOnlyDropDown(
                            context = requireContext(), textInputLayout = mTextInputLayoutShift
                        )
                    }
                }
            }
        }
    }

    private fun setDropdownLot() {
        lifecycleScope.launch {
            val mApplicationList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getLotListFromDataSet()
            } ?: return@launch

            if (mApplicationList.isEmpty()) return@launch

            val locations = mApplicationList.map { it.location.orEmpty() }.toTypedArray()
            val savedLot =
                mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user?.mLot?.takeIf { it.isNotBlank() }

            val pos = savedLot?.let { sl ->
                locations.indexOfFirst { it.equals(sl, ignoreCase = true) }
            } ?: -1

            withContext(Dispatchers.Main) {
                mAutoComTextViewLot?.let { auto ->
                    if (pos >= 0) {
                        try {
                            auto.setText(locations[pos], false)
                        } catch (_: Exception) {
                        }
                    }

                    val adapter = ArrayAdapter(
                        requireContext(), R.layout.row_dropdown_menu_popup_item, locations
                    )
                    try {
                        auto.threshold = 1
                        auto.setAdapter(adapter)
                        auto.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                            // keep original behavior: convert selected text to index and hide keyboard
                            mApplicationList.getIndexOfLocation(
                                parent.getItemAtPosition(position).toString()
                            )
                            requireActivity().hideSoftKeyboard()
                        }
                        if (auto.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                            auto.setListOnlyDropDown(
                                context = requireContext(), textInputLayout = mTextInputLayoutLot
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    //End of set dropdown list

    private fun removeFocus() {
        mAutoComTextViewShift?.clearFocus()
        mAutoComTextViewBeat?.clearFocus()
        mAutoComTextViewSuper?.clearFocus()
        mAutoComTextViewAgency?.clearFocus()
        mAutoComTextViewPrinterEquipment?.clearFocus()
        mAutoComTextViewDeviceId?.clearFocus()
    }

    private fun isFormValid(mValue: String): Boolean {
        try {
            val layouts = mCitationLayout ?: return true
            for (comp in layouts) {
                val compName = comp.component?.lowercase(Locale.US) ?: continue
                val fields = comp.fields.orEmpty()

                when (compName) {
                    "asset" -> {
                        for (field in fields) {
                            if (!field.isRequired.nullSafety()) continue
                            when (field.name?.lowercase(Locale.US)) {
                                "shift" -> {
                                    if (mAutoComTextViewShift?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutShift?.showErrorWithShake(getString(R.string.val_msg_please_enter_shift))
                                        mAutoComTextViewShift?.requestFocus()
                                        return false
                                    }
                                }

                                "beat" -> {
                                    if (mAutoComTextViewBeat?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutBeat?.showErrorWithShake(getString(R.string.val_msg_please_enter_beat))
                                        mAutoComTextViewBeat?.requestFocus()
                                        return false
                                    }
                                }

                                "zone" -> {
                                    if (mAutoComTextViewZone?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutZone?.showErrorWithShake(getString(R.string.val_msg_please_enter_zone))
                                        mAutoComTextViewZone?.requestFocus()
                                        return false
                                    }
                                }

                                "city_zone" -> {
                                    if (mAutoComTextViewPbcZone?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutPbcZone?.showErrorWithShake(getString(R.string.val_msg_please_enter_zone))
                                        mAutoComTextViewPbcZone?.requestFocus()
                                        return false
                                    }
                                }

                                "radio" -> {
                                    if (mAutoComTextViewRadio?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutRadio?.showErrorWithShake(getString(R.string.val_msg_please_enter_radio))
                                        mAutoComTextViewRadio?.requestFocus()
                                        return false
                                    }
                                }

                                "agency" -> {
                                    if (mAutoComTextViewAgency?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutAgency?.showErrorWithShake(getString(R.string.val_msg_please_enter_agency))
                                        mAutoComTextViewAgency?.requestFocus()
                                        return false
                                    }
                                }

                                "supervisor" -> {
                                    if (mAutoComTextViewSuper?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutSuper?.showErrorWithShake(getString(R.string.val_msg_please_enter_supervisor))
                                        mAutoComTextViewSuper?.requestFocus()
                                        return false
                                    }
                                }

                                "device_id", "deviceid" -> {
                                    if (mAutoComTextViewDeviceId?.text.toString().trim()
                                            .isEmpty()
                                    ) {
                                        mTextInputLayoutDeviceId?.showErrorWithShake(getString(R.string.val_msg_please_enter_device_id))
                                        mAutoComTextViewDeviceId?.requestFocus()
                                        return false
                                    }
                                }

                                "signature" -> {
                                    try {
                                        if (fileSignature == null || !fileSignature?.exists()
                                                .nullSafety()
                                        ) {
                                            requireContext().toast(getString(R.string.scr_wrn_signature_must))
                                            return false
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        // if file check fails, consider invalid
                                        return false
                                    }
                                }
                            }
                        }
                    }

                    "location" -> {
                        for (field in fields) {
                            if (!field.isRequired.nullSafety()) continue
                            when (field.name?.lowercase(Locale.US)) {
                                "block" -> {
                                    if (mAutoComTextViewBlock?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutBlock?.showErrorWithShake(getString(R.string.val_msg_please_enter_block))
                                        mAutoComTextViewBlock?.requestFocus()
                                        return false
                                    }
                                }

                                "zone" -> {
                                    if (mAutoComTextViewZone?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutZone?.showErrorWithShake(getString(R.string.val_msg_please_enter_zone))
                                        mAutoComTextViewZone?.requestFocus()
                                        return false
                                    }
                                }

                                "street" -> {
                                    if (mAutoComTextViewStreet?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutStreet?.showErrorWithShake(getString(R.string.val_msg_please_enter_street))
                                        mAutoComTextViewStreet?.requestFocus()
                                        return false
                                    }
                                }
                            }
                        }
                    }

                    "other" -> {
                        for (field in fields) {
                            if (!field.isRequired.nullSafety()) continue
                            when (field.name?.lowercase(Locale.US)) {
                                "activity" -> {
                                    if (mAutoComTextViewActivity?.text.toString().trim()
                                            .isEmpty()
                                    ) {
                                        mTextInputLayoutActivity?.showErrorWithShake(getString(R.string.val_msg_please_enter_activity))
                                        mAutoComTextViewActivity?.requestFocus()
                                        return false
                                    }
                                }

                                "comments" -> {
                                    if (mAutoComTextViewComments?.text.toString().trim()
                                            .isEmpty()
                                    ) {
                                        mTextInputLayoutComments?.showErrorWithShake(getString(R.string.val_msg_please_enter_comments))
                                        mAutoComTextViewComments?.requestFocus()
                                        return false
                                    }
                                }

                                "notes" -> {
                                    if (mEdittextNote?.text.toString().trim().isEmpty()) {
                                        mTextInputLayoutNote?.showErrorWithShake(getString(R.string.val_msg_please_enter_note))
                                        mEdittextNote?.requestFocus()
                                        return false
                                    }
                                }
                            }
                        }
                    }

                    "officer" -> {
                        for (field in fields) {
                            if (!field.isRequired.nullSafety()) continue
                            if (field.name?.equals("squad", ignoreCase = true) == true) {
                                if (mAutoComTextViewSquad?.text.toString().trim().isEmpty()) {
                                    mTextInputLayoutSquad?.showErrorWithShake(getString(R.string.val_msg_please_enter_squad))
                                    mAutoComTextViewSquad?.requestFocus()
                                    return false
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
        return true
    }

    private fun isDetailsChanged(): Boolean {
        // Synchronously fetch saved form (blocks briefly on IO dispatcher to ensure value is available)
        val mWelcomeData: WelcomeForm? = try {
            kotlinx.coroutines.runBlocking {
                withContext(Dispatchers.IO) { welcomeScreenViewModel.getWelcomeForm() }
            }
        } catch (e: Exception) {
            return true
        }

        // If either saved data or current response is missing, keep original behavior (return true)
        val welcome = mWelcomeData ?: return true
        //if (mWelcomeResponseData == null) return true

        return try {
            // If combination id set => treated as "changed" (original returned false in that branch, keep same)
            if (mCombinationId != null) {
                false
            } else if (mSelectedZoneStat?.m_id?.equals(
                    welcome.officerZone, ignoreCase = true
                ) == false
            ) {
                false
            } else if (mSelectedSupervisorStat?.m_id?.equals(
                    welcome.officerSupervisor, ignoreCase = true
                ) == false
            ) {
                false
            } else if (mSelectedBeatStat?.m_id?.equals(
                    welcome.officerBeat, ignoreCase = true
                ) == false
            ) {
                false
            } else if (mSelectedShiftStat?.m_id?.equals(
                    welcome.officerShift, ignoreCase = true
                ) == false
            ) {
                false
            } else if (mSelectedAgency?.equals(welcome.agency, ignoreCase = true) == false) {
                false
            } else {
                // original final expression: mSelectedRadioStat == null || mSelectedRadioStat!!.m_id.equals(...)
                mSelectedRadioStat == null || (mSelectedRadioStat?.id?.equals(
                    welcome.officerRadio, ignoreCase = true
                ) == true)
            }
        } catch (e: Exception) {
            true
        }
    }

    //save Activity log data to database - It contains offline and online conditions
    private fun insertFormToDb(uploadStatus: Boolean, isReUpload: Boolean, mForm: WelcomeForm?) {
        lifecycleScope.launch {
            var mWelcomeForm: WelcomeForm? = WelcomeForm()
            if (isReUpload) {
                mWelcomeForm = mForm
                mWelcomeForm?.uploadStatus = uploadStatus
            } else {
                val mData = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user
                mWelcomeForm?.enable = mData?.enable.nullSafety()
                mWelcomeForm?.siteOfficerId = mData?.siteOfficerId.nullSafety()
                mWelcomeForm?.officerBadgeId = mData?.officerBadgeId.nullSafety()
                mWelcomeForm?.enable = mData?.enable.nullSafety()
                mWelcomeForm?.id = mData?.m_id.nullSafety()
                if (mData?.officerBeat != null) {
                    mWelcomeForm?.officerBeat = mData.officerBeat.nullSafety()
                }

                mWelcomeForm?.officerBeatName =
                    mAutoComTextViewBeat?.editableText?.trim().toString().nullSafety()
                mWelcomeForm?.officerZoneName =
                    mAutoComTextViewZone?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.cityZoneName =
                    mAutoComTextViewPbcZone?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.officerRadio =
                    mAutoComTextViewRadio?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.officerSupervisor =
                    mAutoComTextViewSuper?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.mOfficerSuperVisorBadgeId = mSelectedSupervisorStat?.mSuperBadgeId
                mWelcomeForm?.officerShift =
                    mAutoComTextViewShift?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.officerZone =
                    mAutoComTextViewZone?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.cityZoneName =
                    mAutoComTextViewPbcZone?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.radio =
                    mAutoComTextViewRadio?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.cityZoneNameCode = mSelectedZoneStat?.mCityZoneName
                mWelcomeForm?.officerSquad =
                    mAutoComTextViewSquad?.editableText.toString().trim().nullSafety()

                mWelcomeForm?.officerFirstName = mData?.officerFirstName.nullSafety()
                mWelcomeForm?.officerMiddleName = mData?.officerMiddleName.nullSafety()
                mWelcomeForm?.officerLastName = mData?.officerLastName.nullSafety()
                mWelcomeForm?.role = mData?.role.nullSafety()
                mWelcomeForm?.agency =
                    mAutoComTextViewAgency?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.officerDeviceId = selectedDeViceId.mDeviceId.nullSafety()
                mWelcomeForm?.officerDeviceName = selectedDeViceId.mDeviceFriendlyName.nullSafety()
                mWelcomeForm?.shift =
                    mAutoComTextViewShift?.editableText.toString().trim().nullSafety()
                mWelcomeForm?.equipmentId =
                    mAutoComTextViewPrinterEquipment?.editableText.toString().trim().nullSafety()

                if (mData?.mLot?.isNotEmpty().nullSafety()) {
                    mWelcomeForm?.lot = mData?.mLot.nullSafety()
                } else {
                    mWelcomeForm?.lot =
                        mAutoComTextViewLot?.editableText.toString().trim().nullSafety()
                }

                mWelcomeForm?.officerSuperviserId = mData?.officerSuperviserId.nullSafety()
                mWelcomeForm?.siteId = mData?.siteId.nullSafety()
                mWelcomeForm?.uploadStatus = uploadStatus.nullSafety()
            }

            try {
                mWelcomeForm?.let { welcomeScreenViewModel.insertWelcomeForm(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //save Activity log data to database - It contains offline and online conditions
    private suspend fun insertFormToDbByAPIResponse(mForm: WelcomeUser?) {
        val mWelcomeForm = WelcomeForm()
        mForm.let { mData ->
            mWelcomeForm.enable = mData?.enable.nullSafety()
            mWelcomeForm.siteOfficerId = mData?.siteOfficerId.nullSafety()
            mWelcomeForm.officerBadgeId = mData?.officerBadgeId.nullSafety()
            mWelcomeForm.enable = mData?.enable.nullSafety()
            mWelcomeForm.id = mData?.m_id.nullSafety()

            if (mData?.officerBeat?.isNotEmpty().nullSafety()) {
                mWelcomeForm.officerBeat = mData?.officerBeat.nullSafety()
            }

            mWelcomeForm.officerBeatName = mData?.officerBeat.nullSafety()
            mWelcomeForm.officerZoneName = mData?.officerZone.nullSafety()
            mWelcomeForm.officerRadio = mData?.officerRadio.nullSafety()
            mWelcomeForm.officerSupervisor = mData?.officerSupervisor.nullSafety()
            mWelcomeForm.officerShift = mData?.officerShift.nullSafety()
            mWelcomeForm.officerZone = mData?.officerZone.nullSafety()
            mWelcomeForm.radio = mData?.officerRadio.nullSafety()
            mWelcomeForm.officerDeviceId = mData?.mOfficerDeviceId?.mDeviceId.nullSafety()
            mWelcomeForm.officerDeviceName =
                mData?.mOfficerDeviceId?.mDeviceFriendlyName.nullSafety()
            mWelcomeForm.equipmentId = mData?.mEquipment.nullSafety()
            mWelcomeForm.officerSquad = mData?.officerSquad.nullSafety()
            mWelcomeForm.officerFirstName = mData?.officerFirstName.nullSafety()
            mWelcomeForm.officerMiddleName = mData?.officerMiddleName.nullSafety()
            mWelcomeForm.officerLastName = mData?.officerLastName.nullSafety()
            mWelcomeForm.role = mData?.role.nullSafety()
            mWelcomeForm.cityZoneName = mData?.mCityZone.nullSafety()
            mWelcomeForm.agency = mData?.mOfficerAgency.nullSafety()
            mWelcomeForm.officer_lookup_code = mData?.officer_lookup_code.nullSafety()
            mWelcomeForm.shift = mData?.shift.nullSafety()
            mWelcomeForm.lot = mData?.mLot.nullSafety()
            mWelcomeForm.officerSquad = mData?.officerSquad.nullSafety()
            mWelcomeForm.officerSuperviserId = mData?.officerSuperviserId.nullSafety()
            mWelcomeForm.siteId = mData?.siteId.nullSafety()
            mWelcomeForm.uploadStatus = true
            mWelcomeForm.initiatorId = mData?.mApprovedBy?.mInitiatorId.nullSafety()
            mWelcomeForm.initiatorRole = mData?.mApprovedBy?.mInitiator_role.nullSafety()
            try {
                welcomeScreenViewModel.insertWelcomeForm(mWelcomeForm)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun btnSaveClick() {
        val mWelcomeRole = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user

        mWelcomeRole?.officerZone = mSelectedZoneStat?.zoneName.nullSafety()
        mWelcomeRole?.officerSupervisor = mSelectedSupervisorStat?.mSuperName.nullSafety()
        mWelcomeRole?.officerBeat = mSelectedBeatStat?.beatName.nullSafety()
        mWelcomeRole?.officerShift =
            sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").nullSafety()
        mWelcomeRole?.officerRadio = mSelectedRadioStat?.radioName.nullSafety()
        mWelcomeRole?.mEquipment = mAutoComTextViewPrinterEquipment?.text.toString().nullSafety()

        try {
            if (mAutoComTextViewLot != null) mWelcomeRole?.mLot =
                mAutoComTextViewLot?.text?.toString()
        } catch (e: Exception) {
            mWelcomeRole?.mLot = ""
            e.printStackTrace()
        }

        mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user = mWelcomeRole

        if (requireContext().isInternetAvailable()) {
            if (imageViewSignature.isVisible.nullSafety()) {
                scanStatus = false
                callSignatureBulkImageUpload()
            } else {
                scanStatus = false
                insertFormToDb(uploadStatus = false, isReUpload = false, mForm = null)
                callUpdateSiteOfficerApi()
            }
        } else {
            requireContext().toast("You are Offline, Data Saved!")
            insertFormToDb(uploadStatus = false, isReUpload = false, mForm = null)
        }
        uploadActivityImages()
    }

    private fun bntScanClick() {
        // return early if locked
        if (sharedPreference.read(SharedPrefKey.LOCKED_LPR_BOOL, true).nullSafety()) return

        if (LogUtil.isEnableActivityLogs) {
            lifecycleScope.launch {
                mainActivityViewModel.sendActionToMain(
                    MainActivityAction.EventActivityLogAPI(
                        mValue = ACTIVITY_LOG_WELCOME_SCAN, isDisplay = true
                    )
                )
            }
        }

        removeFocus()
        if (!isFormValid("SCAN")) return

        if (!isDetailsChanged()) {
            val user = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user
            user?.apply {
                officerZone = mSelectedZoneStat?.zoneName.orEmpty()
                officerSupervisor = mSelectedSupervisorStat?.mSuperName.nullSafety()
                officerBeat = mSelectedBeatStat?.beatName.orEmpty()
                officerShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").orEmpty()
                officerRadio = mSelectedRadioStat?.radioName.orEmpty()
                mLot = mAutoComTextViewLot?.text?.toString().orEmpty()
            }?.also {
                mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user = it
            }

            if (isInternetAvailable(requireContext())) {
                scanStatus = true
                if (imageViewSignature.isVisible.nullSafety()) {
                    callSignatureBulkImageUpload()
                } else {
                    callUpdateSiteOfficerApi()
                    insertFormToDb(uploadStatus = true, isReUpload = false, mForm = null)
                }
            } else {
                requireContext().toast(getString(R.string.err_msg_connection_was_refused))
                insertFormToDb(uploadStatus = false, isReUpload = false, mForm = null)
            }
        }

        sharedPreference.write(SharedPrefKey.IS_FROM_FILLED, true)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(300)
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)) {
                nav.safeNavigate(R.id.scanResultScreenFragment)
            } else {
                val intent = Intent(requireContext(), NewLprScanActivity::class.java)
                intent.putExtra(INTENT_KEY_SCANNER_TYPE, mainActivityViewModel.getLprScannerType())
                lprScanActivityLauncher.launch(intent)
            }
        }
    }

    private fun btnScanStickerClick() {
        // if locked, do nothing
        if (sharedPreference.read(SharedPrefKey.LOCKED_LPR_BOOL, true).nullSafety()) return

        if (LogUtil.isEnableActivityLogs) {
            lifecycleScope.launch {
                mainActivityViewModel.sendActionToMain(
                    MainActivityAction.EventActivityLogAPI(
                        mValue = ACTIVITY_LOG_WELCOME_SCAN, isDisplay = true
                    )
                )
            }
        }

        removeFocus()
        if (!isFormValid("SCAN")) return

        if (!isDetailsChanged()) {
            val user = mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user

            user?.apply {
                officerZone = mSelectedZoneStat?.zoneName.orEmpty()
                officerSupervisor = mSelectedSupervisorStat?.mSuperName.nullSafety()
                officerBeat = mSelectedBeatStat?.beatName.orEmpty()
                officerShift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").orEmpty()
                officerRadio = mSelectedRadioStat?.radioName.orEmpty()
                mLot = mAutoComTextViewLot?.text?.toString().orEmpty()
            }

            // update back only if non-null
            mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user = user

            if (isInternetAvailable(requireContext())) {
                scanStatus = true
                if (imageViewSignature.isVisible.nullSafety()) {
                    callSignatureBulkImageUpload()
                } else {
                    callUpdateSiteOfficerApi()
                    insertFormToDb(uploadStatus = true, isReUpload = false, mForm = null)
                }
            } else {
                requireContext().toast(getString(R.string.err_msg_connection_was_refused))
                insertFormToDb(uploadStatus = false, isReUpload = false, mForm = null)
            }
        }

        sharedPreference.write(SharedPrefKey.IS_FROM_FILLED, true)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(300)
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)) {
                nav.safeNavigate(R.id.scanResultScreenFragment)
            } else {
                val intent = Intent(requireContext(), NewVehicleStickerScanActivity::class.java)
                vehicleStickerActivityLauncher.launch(intent)
            }
        }
    }

    private fun saveActivityAPI() {
        val activityText = mAutoComTextViewActivity?.text?.toString()?.trim().orEmpty()
        if (activityText.isNotEmpty()) {
            isSaveButtonClicked = true
            callEventActivityLogApi()

            val lat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDoubleOrNull() ?: 0.0
            val long = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDoubleOrNull() ?: 0.0
            val officerShift =
                mWelcomeResponseData?.data?.getOrNull(0)?.responsedata?.user?.officerShift.orEmpty()

            val mLocationUpdateRequest = LocUpdateRequest().apply {
                activityType = Constants.ACTIVITY_TYPE_LOCATION_UPDATE
                logType = Constants.LOG_TYPE_NODE_PORT
                locationUpdateType = "break"
                latitude = lat
                longitude = long
                mShift = officerShift
                clientTimestamp = try {
                    splitDateLpr(mZone)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    ""
                }
            }

            lifecycleScope.launch {
                runCatching {
                    mainActivityViewModel.sendActionToMain(
                        MainActivityAction.EventCallLocationAPI(mLocationUpdateRequest)
                    )
                }.onFailure { it.printStackTrace() }
            }
        }

        sharedPreference.write(SharedPrefKey.IS_FROM_FILLED, true)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(TIME_INTERVAL_700_MS)
            nav.safeNavigate(R.id.action_welcomeScreenFragment_to_dashboardScreenFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            mActivityId = UUID.randomUUID().toString()

            fileSignature =
                File(FileUtil.getSignatureDirectory().absolutePath, getSignatureFileNameWithExt())
            if (fileSignature?.exists().nullSafety()) {
                imageViewSignature.setImageURI(Uri.fromFile(fileSignature))
            }

            createDirForContinuesMode()

            createCredFolder(sharedPreference = sharedPreference)

            setBannerImageAdapter()

            if (isSaveButtonClicked) {
                bannerList?.clear()
                mBannerAdapter?.setTimingBannerList(bannerList)
                mViewPagerBanner.adapter = mBannerAdapter
                mViewPagerBanner.currentItem = 0
                mBannerAdapter?.notifyDataSetChanged()
                isSaveButtonClicked = false
                setCameraImages()
            }

            uploadOfflineMakeOneCitation()
            uploadOfflineTimingsToServer()
            callTicketCancelApi("", "")

            lifecycleScope.launch {
                delay(TIME_INTERVAL_6_SECONDS) // Wait for 2 seconds
                /**
                 * Checks if the violation list is empty by calling [DataBaseUtil.getViolationListEmpty].
                 * If the list is not empty and citation is locked then move to officer in citation form
                 */
                if (!DataBaseUtil.isViolationListEmpty()) {
                    /**
                     * Upload Offline citation and cancel data to server
                     */
                    checkOfflineCitationAndCancelDataInDBForUpload()
                }

                if (sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").nullSafety().isEmpty()) {
                    lifecycleScope.launch {
                        mainActivityViewModel.sendActionToMain(MainActivityAction.EventProcessLogout)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Start of Dataset Related Methods
    private fun isDataSetListEmpty() {
        /**
         * Checks if the violation list is empty by calling [DataBaseUtil.getViolationListEmpty].
         * If the list is empty, it also clears the related dataset tables.
         * and refill again
         */
        if (DataBaseUtil.isViolationListEmpty()) {
            mainActivityViewModel.deleteDatasetAndActivityTables(isManual = false)

            AlertDialogUtils.showDialog(
                context = requireContext(),
                cancelable = false,
                title = getString(R.string.warnings_lbl_dataset_empty_header),
                message = getString(R.string.warnings_lbl_dataset_empty_message),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_cancel),
                listener = object : AlertDialogListener {
                    override fun onPositiveButtonClicked() {
                        lifecycleScope.launch {
                            mainActivityViewModel.sendActionToMain(MainActivityAction.EventProcessLogout)
                        }
                    }
                })
        } else {
            /**
             * Upload Offline citation and cancel data to server
             */
            checkOfflineCitationAndCancelDataInDBForUpload()
        }
    }
    //End of Dataset Related Methods

    fun checkOfflineCitationAndCancelDataInDBForUpload() {
        val lockLprModel = getLprLock(requireContext(), sharedPreference)
        lifecycleScope.launch {
            delay(TIME_INTERVAL_2_SECONDS)
            if (sharedPreference.read(SharedPrefKey.IS_LOGGED_IN, false)) {
                val mIssuranceModel: List<CitationInsurranceDatabaseModel?>? =
                    welcomeScreenViewModel.getCitationInsurranceUnuploadCitation()

                if (lockLprModel != null && lockLprModel.mLprNumber != null && !TextUtils.isEmpty(
                        lockLprModel.mLprNumber
                    ) && lockLprModel.mLprNumber.nullSafety().length > 1 || mIssuranceModel?.isNotEmpty()
                        .nullSafety()
                ) {
                    if (mIssuranceModel?.isNotEmpty().nullSafety()) {
                        mCitationNumberId = mIssuranceModel?.firstOrNull()?.citationNumber
                        offlineStatus = 2
                        callTicketStatusApi(mIssuranceModel?.firstOrNull()?.citationNumber)
                    } else {
                        moveToCitationFormWithUploadedCitation()
                    }
                } else {
                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                    mainActivityViewModel.deleteTempImages()
                }
            }
        }
    }

    fun moveToCitationFormWithUploadedCitation() {
        val lockLprModel = getLprLock(requireContext(), sharedPreference)
        lifecycleScope.launch {
            val mIssuranceModel: List<CitationInsurranceDatabaseModel?>? =
                welcomeScreenViewModel.getCitationInsurranceUnuploadCitation()
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
                welcomeScreenViewModel.deleteSaveCitation(mCitationNumberId!!)
                welcomeScreenViewModel.updateCitationBooklet(0, mCitationNumberId)
            } else {
                if (mIssuranceModel?.isNotEmpty().nullSafety()) {
                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, true)
                    sharedPreference.writeOverTimeParkingTicketDetails(
                        SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION, AddTimingRequest()
                    )
                    val citationTicketType: String =
                        if ((mIssuranceModel?.last()?.citationData?.municipalCitationMotoristDetailsModel != null) || lockLprModel?.ticketCategory == API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET) {
                            CITATION_TICKET_TYPE_MUNICIPAL //Municipal
                        } else {
                            CITATION_TICKET_TYPE_PARKING //Normal
                        }

                    val bundle = Bundle().apply {
                        putString("citation_ticket_type", citationTicketType)
                        putString("make", lockLprModel?.mMake)
                        putString(
                            "from_scr",
                            if (mIssuranceModel?.isNotEmpty()
                                    .nullSafety()
                            ) "WelcomeUnUpload" else "lpr_lock"
                        )
                        putString("model", lockLprModel?.mModel)
                        putString("color", lockLprModel?.mColor)
                        putString("lpr_number", lockLprModel?.mLprNumber)
                        putString("violation_code", lockLprModel?.mViolationCode)
                        putString("address", lockLprModel?.mAddress)
                    }

                    nav.safeNavigate(R.id.citationFormScreenFragment, bundle)
                } else if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_DUNCAN,
                        ignoreCase = true
                    )
                ) {
                    /**
                     * After discurssion with Sri 31 oct 25 after scan and go to the citation form then officer should be lock event there is plate number
                     */
                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, true)
                    sharedPreference.writeOverTimeParkingTicketDetails(
                        SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION, AddTimingRequest()
                    )

                    val citationTicketType: String =
                        if ((mIssuranceModel?.last()?.citationData?.municipalCitationMotoristDetailsModel != null) || lockLprModel?.ticketCategory == API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET) {
                            CITATION_TICKET_TYPE_MUNICIPAL //Municipal
                        } else {
                            CITATION_TICKET_TYPE_PARKING //Normal
                        }

                    val bundle = Bundle().apply {
                        putString("citation_ticket_type", citationTicketType)
                        putString("make", lockLprModel?.mMake)
                        putString(
                            "from_scr",
                            if (mIssuranceModel?.isNotEmpty()
                                    .nullSafety()
                            ) "WelcomeUnUpload" else "lpr_lock"
                        )
                        putString("model", lockLprModel?.mModel)
                        putString("color", lockLprModel?.mColor)
                        putString("lpr_number", lockLprModel?.mLprNumber)
                        putString("violation_code", lockLprModel?.mViolationCode)
                        putString("address", lockLprModel?.mAddress)
                    }

                    nav.safeNavigate(R.id.citationFormScreenFragment, bundle)
                } else {
                    /**
                     * If there is no offline citation so scan and menu button not lock
                     */
                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                }
            }
        }
    }

    override fun onDestroy() {
        layOtherDetails.removeAllViews()
        layAssetDetails.removeAllViews()
        layLocationDetails.removeAllViews()
        DialogUtil.hideLoader()
        super.onDestroy()
    }

    private fun setSignatureView() {
        requireActivity().hideSoftKeyboard()
        SignatureBottomSheet().show(
            parentFragmentManager, getString(R.string.bottom_sheet_signature_sheet)
        )

        setFragmentResultListener(SignatureBottomSheet.REQUEST_KEY) { _, bundle ->
            val bmp =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable(SignatureBottomSheet.BUNDLE_KEY_BITMAP, Bitmap::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    bundle.getParcelable(SignatureBottomSheet.BUNDLE_KEY_BITMAP) as Bitmap?
                }

            bmp.let { finalBitmap ->
                imageViewSignature.setImageBitmap(finalBitmap)
                finalBitmap?.let { FileUtil.saveSignature(FileUtil.getSignatureDirectory(), it) }
            }
        }
    }

    //call all dataset Apis with a sec of time interval
    private fun callApiParallellyRecursive(
        mUpdateTimeDataset: List<UpdateTimeDb>, arrayIndex: Int, pageIndex: Long
    ) {
        if (mUpdateTimeDataset.isNotEmpty() && mUpdateTimeDataset.firstOrNull()?.status.nullSafety()) {
            callCitationDatasetApi(mUpdateTimeDataset.firstOrNull()?.name.nullSafety(), pageIndex)
        }
    }


    //Save dataset type list to database
    private fun saveDatasetTypeList(mResList: DatasetData) {
        try {
            when (mResList.metadata!!.type) {
                DATASET_DECAL_YEAR_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetDecalYearListModel = DatasetDecalYearListModel()
                            datasetDecalYearListModel.id = 1
                            datasetDecalYearListModel.decalYearList = mResList.response
                            welcomeScreenViewModel.insertDatasetDecalYearListModel(
                                datasetDecalYearListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                DATASET_MAKE_MODEL_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { makeListTotal.addAll(it) }

                        Collections.sort(makeListTotal, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return (if (lhs?.make != null) lhs.make else "")!!.compareTo((if (rhs?.make != null) rhs.make else "")!!)
                            }
                        })

                        try {
                            val datasetCarMakeListModel = DatasetCarMakeListModel()
                            datasetCarMakeListModel.id = 1
                            datasetCarMakeListModel.carMakeList = makeListTotal
                            welcomeScreenViewModel.insertDatasetCarMakeListModel(
                                datasetCarMakeListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MAKE_MODEL_LIST2 -> {
                }

                DATASET_CAR_COLOR_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetCarColorListModel = DatasetCarColorListModel()
                            datasetCarColorListModel.id = 1
                            datasetCarColorListModel.carColorList = mResList.response
                            welcomeScreenViewModel.insertDatasetCarColorListModel(
                                datasetCarColorListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_STATE_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { stateList.addAll(it) }

                        try {
                            val datasetStateListModel = DatasetStateListModel()
                            datasetStateListModel.id = 1
                            datasetStateListModel.stateList = stateList
                            welcomeScreenViewModel.insertDatasetStateListModel(
                                datasetStateListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_BLOCK_LIST -> {
                    lifecycleScope.launch {
                        // Add response list
                        mResList.response?.let { blockList.addAll(it) }

                        try {
                            val datasetBlockListModel = DatasetBlockListModel()
                            datasetBlockListModel.id = 1
                            datasetBlockListModel.blockList = blockList
                            welcomeScreenViewModel.insertDatasetBlockListModel(
                                datasetBlockListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_STREET_LIST -> {
                    lifecycleScope.launch {
                        // Add response list
                        mResList.response?.let { streetList.addAll(it) }

                        try {
                            val datasetStreetListModel = DatasetStreetListModel()
                            datasetStreetListModel.id = 1
                            datasetStreetListModel.streetList = streetList
                            welcomeScreenViewModel.insertDatasetStreetListModel(
                                datasetStreetListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }


                DATASET_METER_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { meterList.addAll(it) }

                        try {
                            val datasetMeterListModel = DatasetMeterListModel()
                            datasetMeterListModel.id = 1
                            datasetMeterListModel.meterList = meterList.distinctBy { it.name }
                            welcomeScreenViewModel.insertDatasetMeterListModel(
                                datasetMeterListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_SPACE_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { spaceList.addAll(it) }

                        try {
                            val datasetSpaceListModel = DatasetSpaceListModel()
                            datasetSpaceListModel.id = 1
                            datasetSpaceListModel.spaceList = spaceList.distinctBy { it.spaceName }
                            welcomeScreenViewModel.insertDatasetSpaceListModel(
                                datasetSpaceListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }


                DATASET_CAR_BODY_STYLE_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetCarBodyStyleListModel = DatasetCarBodyStyleListModel()
                            datasetCarBodyStyleListModel.id = 1
                            datasetCarBodyStyleListModel.carBodyStyleList = mResList.response
                            welcomeScreenViewModel.insertDatasetCarBodyStyleListModel(
                                datasetCarBodyStyleListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_VIOLATION_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { violationList.addAll(it) }

                        Collections.sort(violationList, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return lhs?.violation!!.compareTo(rhs?.violation!!)
                            }
                        })

                        try {
                            val datasetViolationListModel = DatasetViolationListModel()
                            datasetViolationListModel.id = 1
                            datasetViolationListModel.violationList = violationList
                            welcomeScreenViewModel.insertDatasetViolationListModel(
                                datasetViolationListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_VIO_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { vioList.addAll(it) }

                        try {
                            val datasetVioListModel = DatasetVioListModel()
                            datasetVioListModel.id = 1
                            datasetVioListModel.vioList = vioList
                            welcomeScreenViewModel.insertDatasetVioListModel(
                                datasetVioListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_HOLIDAY_CALENDAR_LIST -> {

                    lifecycleScope.launch {
                        mResList.response?.let { holidayCalendarList.addAll(it) }

                        try {
                            val datasetHolidayCalendarListModel = DatasetHolidayCalendarList()
                            datasetHolidayCalendarListModel.id = 1
                            datasetHolidayCalendarListModel.holidayCalendatList = holidayCalendarList
                            welcomeScreenViewModel.insertDatasetHolidayCalendarListModel(
                                datasetHolidayCalendarListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_SIDE_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetSideListModel = DatasetSideListModel()
                            datasetSideListModel.id = 1
                            datasetSideListModel.sideList = mResList.response
                            welcomeScreenViewModel.insertDatasetSideListModel(datasetSideListModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_TIER_STEM_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetTierStemListModel = DatasetTierStemListModel()
                            datasetTierStemListModel.id = 1
                            datasetTierStemListModel.tierStemList = mResList.response
                            welcomeScreenViewModel.insertDatasetTierStemListModel(
                                datasetTierStemListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_NOTES_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetNotesListModel = DatasetNotesListModel()
                            datasetNotesListModel.id = 1
                            datasetNotesListModel.notesList = mResList.response
                            welcomeScreenViewModel.insertDatasetNotesListModel(
                                datasetNotesListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_REMARKS_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetRemarksListModel = DatasetRemarksListModel()
                            datasetRemarksListModel.id = 1
                            datasetRemarksListModel.remarksList = mResList.response
                            welcomeScreenViewModel.insertDatasetRemarksListModel(
                                datasetRemarksListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }


                DATASET_REGULATION_TIME_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetRegulationTimeListModel = DatasetRegulationTimeListModel()
                            datasetRegulationTimeListModel.id = 1
                            datasetRegulationTimeListModel.regulationTimeList = mResList.response
                            welcomeScreenViewModel.insertDatasetRegulationTimeListModel(
                                datasetRegulationTimeListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                DATASET_LOT_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { lotList.addAll(it) }

                        Collections.sort(lotList, object : Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return lhs?.location!!.compareTo(rhs?.location!!)
                            }
                        })


                        try {
                            val datasetLotListModel = DatasetLotListModel()
                            datasetLotListModel.id = 1
                            datasetLotListModel.lotList = lotList
                            welcomeScreenViewModel.insertDatasetLotListModel(datasetLotListModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                DATASET_SETTINGS_LIST -> {
                    lifecycleScope.launch {
                        try {
                            if (mResList.response != null && mResList.response?.size.nullSafety() > 0) {
                                setLoginTimeStamp()
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }


                        try {
                            val datasetSettingsListModel = DatasetSettingsListModel()
                            datasetSettingsListModel.id = 1
                            datasetSettingsListModel.settingsList = mResList.response
                            welcomeScreenViewModel.insertDatasetSettingsListModel(
                                datasetSettingsListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_CANCEL_REASON_LIST -> {
                    lifecycleScope.launch {

                        try {
                            val datasetCancelReasonListModel = DatasetCancelReasonListModel()
                            datasetCancelReasonListModel.id = 1
                            datasetCancelReasonListModel.cancelReasonList = mResList.response
                            welcomeScreenViewModel.insertDatasetCancelReasonListModel(
                                datasetCancelReasonListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_PBC_ZONE_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { pbcZoneList.addAll(it) }


                        try {
                            val datasetPBCZoneListModel = DatasetPBCZoneListModel()
                            datasetPBCZoneListModel.id = 1
                            datasetPBCZoneListModel.pbcZoneList = pbcZoneList
                            welcomeScreenViewModel.insertDatasetPBCZoneListModel(
                                datasetPBCZoneListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_VOID_AND_REISSUE_REASON_LIST -> {
                    lifecycleScope.launch {
                        try {
                            val datasetVoidAndReissueReasonListModel =
                                DatasetVoidAndReissueReasonListModel()
                            datasetVoidAndReissueReasonListModel.id = 1
                            datasetVoidAndReissueReasonListModel.voidAndReissueReasonList =
                                mResList.response
                            welcomeScreenViewModel.insertDatasetVoidAndReissueReasonListModel(
                                datasetVoidAndReissueReasonListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_VIOLATION_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { municipalViolationList.addAll(it) }

                        Collections.sort(
                            municipalViolationList, object : Comparator<DatasetResponse?> {
                                override fun compare(
                                    lhs: DatasetResponse?, rhs: DatasetResponse?
                                ): Int {
                                    return lhs?.violation!!.compareTo(rhs?.violation!!)
                                }
                            })


                        try {
                            val datasetMunicipalViolationListModel =
                                DatasetMunicipalViolationListModel()
                            datasetMunicipalViolationListModel.id = 1
                            datasetMunicipalViolationListModel.municipalViolationList =
                                municipalViolationList
                            welcomeScreenViewModel.insertDatasetMunicipalViolationListModel(
                                datasetMunicipalViolationListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_BLOCK_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { municipalBlockList.addAll(it) }

                        try {
                            val datasetMunicipalBlockListModel = DatasetMunicipalBlockListModel()
                            datasetMunicipalBlockListModel.id = 1
                            datasetMunicipalBlockListModel.municipalBlockList = municipalBlockList
                            welcomeScreenViewModel.insertDatasetMunicipalBlockListModel(
                                datasetMunicipalBlockListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_STREET_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { municipalStreetList.addAll(it) }


                        try {
                            val datasetMunicipalStreetListModel = DatasetMunicipalStreetListModel()
                            datasetMunicipalStreetListModel.id = 1
                            datasetMunicipalStreetListModel.municipalStreetList =
                                municipalStreetList
                            welcomeScreenViewModel.insertDatasetMunicipalStreetListModel(
                                datasetMunicipalStreetListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_CITY_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { municipalCityList.addAll(it) }

                        try {
                            val datasetMunicipalCityListModel = DatasetMunicipalCityListModel()
                            datasetMunicipalCityListModel.id = 1
                            datasetMunicipalCityListModel.municipalCityList = municipalCityList
                            welcomeScreenViewModel.insertDatasetMunicipalCityListModel(
                                datasetMunicipalCityListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_STATE_LIST -> {
                    lifecycleScope.launch {
                        mResList.response?.let { municipalStateList.addAll(it) }

                        try {
                            val datasetMunicipalStateListModel = DatasetMunicipalStateListModel()
                            datasetMunicipalStateListModel.id = 1
                            datasetMunicipalStateListModel.municipalStateList = municipalStateList
                            welcomeScreenViewModel.insertDatasetMunicipalStateListModel(
                                datasetMunicipalStateListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            requireContext().toast(mResList.metadata!!.type + "Db error")
        }
    }

    //save Activity list to Database
    private suspend fun saveActivityList() {
        DialogUtil.showLoader(
            requireContext(),
            message = getString(R.string.loader_text_please_wait_we_are_loading_data)
        )
        updateTimeDatasetActivity = ArrayList()

        // Load update time object (safe)
        if (mDataSetTimeObject == null) {
            mDataSetTimeObject = try {
                welcomeScreenViewModel.getUpdateTimeResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        // Load welcome DB object and set license if available
        try {
            mWelcomeListDataSet = mainActivityViewModel.getWelcomeObject()
            mDatabaseWelcomeList = mWelcomeListDataSet?.welcomeList ?: WelcomeList()
            setLicenseKeyForAlpr()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val timeList = mDataSetTimeObject?.timeList
        if (timeList != null) {
            // map dataset names to their status getters on timeList
            val entries = listOf(
                "ActivityList" to timeList.activityList?.status,
                "CommentsList" to timeList.commentsList?.status,
                "SupervisorList" to timeList.supervisorList?.status,
                "BeatList" to timeList.beatList?.status,
                "ZoneList" to timeList.zoneList?.status,
                "RadioList" to timeList.radioList?.status,
                "ShiftList" to timeList.shiftList?.status,
                "AgencyList" to timeList.agencyList?.status,
                "DeviceList" to timeList.mDeviceList?.status,
                DATASET_PBC_ZONE_LIST to timeList.mCityZoneList?.status,
                "EquipmentList" to timeList.mEquipmentList?.status,
                "SquadList" to timeList.mSquadList?.status
            )

            for ((name, status) in entries) {
                if (status.nullSafety()) {
                    updateTimeDatasetActivity?.add(UpdateTimeDb(name, status))
                }
            }
        } else {
            val defaultNames = listOf(
                "ActivityList",
                "CommentsList",
                "SupervisorList",
                "BeatList",
                "ZoneList",
                "RadioList",
                "ShiftList",
                "AgencyList",
                "DeviceList",
                DATASET_PBC_ZONE_LIST,
                "EquipmentList",
                "SquadList"
            )
            defaultNames.forEach { updateTimeDatasetActivity?.add(UpdateTimeDb(it, true)) }
        }

        // ensure device license dataset present when missing
        if (mWelcomeListDataSet?.welcomeList?.deviceLicenseStats == null) {
            updateTimeDatasetActivity?.add(UpdateTimeDb("DeviceLicenseList", true))
        }

        // proceed with first dataset if available and enabled, otherwise fallback
        if (updateTimeDatasetActivity?.isNotEmpty() == true && updateTimeDatasetActivity?.get(0)?.status.nullSafety()) {
            callCitationDatasetApiActivityForm(
                updateTimeDatasetActivity?.get(0)?.name.nullSafety(), 1
            )
        } else {
            try {
                isCitationDatasetExits()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun callActivityDataSetRecursive(name: String, index: Long) {
        callCitationDatasetApiActivityForm(name, index)
    }


    /**
     * only get status 1 citation which is fail by API
     * 2 Only preview screen citation
     */
    private fun uploadOfflineMakeOneCitation() {
        lifecycleScope.launch {
            val result = try {
                withContext(Dispatchers.IO) {
                    welcomeScreenViewModel.getCitationInsurrance()
                        ?.firstOrNull { it?.formStatus == 1 }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            result?.let {
                try {
                    mCitationNumberId = it.citationNumber
                    offlineStatus = 1
                    offlineCitationData = it
                    imageUploadSuccessCount = 0
                    callTicketStatusApi(it.citationNumber)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun uploadOfflineImages(result: CitationInsurranceDatabaseModel) {
        lifecycleScope.launch {
            val images = try {
                withContext(Dispatchers.IO) {
                    welcomeScreenViewModel.getCitationImageOffline(result.citationNumber)
                        ?: emptyList()
                }
            } catch (t: Throwable) {
                logE("WelcomeScreenFragment", "Failed to load offline images: ${t.message}")
                emptyList<CitationImageModelOffline>()
            }

            if (images.isEmpty()) {
                callCreateTicketApi(result)
                return@launch
            }

            images.forEachIndexed { index, image ->
                try {
                    val path = image.citationImage.orEmpty()
                    if (path.isBlank()) {
                        logE("WelcomeScreenFragment", "Skipping empty image path at index $index")
                        return@forEachIndexed
                    }

                    val file = File(path)
                    if (!file.exists() || !file.canRead()) {
                        logE(
                            "WelcomeScreenFragment",
                            "Skipping missing/unreadable file: ${file.absolutePath}"
                        )
                        return@forEachIndexed
                    }

                    callUploadImages(file, index, "CitationImages")
                } catch (t: Throwable) {
                    logE(
                        "WelcomeScreenFragment",
                        "uploadOfflineImages failed at index $index: ${t.message}"
                    )
                }
            }
        }
    }


    /* Call Api For update profile */
    private fun callUploadImages(file: File?, num: Int, folderName: String) {
        if (requireContext().isInternetAvailable()) {
            if (file == null || !file.exists()) {
                return
            }

            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val files = MultipartBody.Part.createFormData(
                "files", file.name, requestFile
            )
            val mDropdownList: Array<String>
            if (folderName == "CitationImages") {
                mDropdownList = if (file.name.contains("_$FILE_NAME_FACSIMILE_PRINT_BITMAP")) {
                    arrayOf(mCitationNumberId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
                } else {
                    arrayOf(mCitationNumberId + "_" + num)
                }
            } else {
                val id = SDF_ddHHmmss.format(Date())
                mDropdownList = arrayOf(id + "_" + Random().nextInt(100) + "_activity_image")
            }
            val mRequestBodyType = folderName.toRequestBody("text/plain".toMediaTypeOrNull())
            welcomeScreenViewModel.callUploadImageAPI(mDropdownList, mRequestBodyType, files)
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }


    /* Call Api For Ticket Cancel */
    private fun callTicketStatusApi(citationNumber: String?) {
        if (!requireContext().isInternetAvailable()) {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
            return
        }

        val ticketUploadStatusRequest = TicketUploadStatusRequest().apply {
            this.citationNumber = citationNumber
        }

        lifecycleScope.launch {
            try {
                welcomeScreenViewModel.callGetTicketStatusAPI(ticketUploadStatusRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                requireContext().toast(e.message ?: "Error")
            }
        }
    }

    /* Call Api For citation layout details */
    private fun callCreateTicketApi(mIssuranceModel: CitationInsurranceDatabaseModel) {
        if (requireContext().isInternetAvailable()) {
            lifecycleScope.launch {
                val welcomeForm: WelcomeForm? = welcomeScreenViewModel.getWelcomeForm()
                if (mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel == null) {
                    val createTicketRequest = RequestHandler.getCreateCitationTicketRequest(
                        welcomeForm = welcomeForm,
                        insuranceModel = mIssuranceModel,
                        mImages = mImages,
                        isReissue = false,
                        timeLimitEnforcementObservedTime = mIssuranceModel.citationData?.officer?.observationTime.nullSafety(),
                    )

                    welcomeScreenViewModel.callCreateCitationTicketAPI(createTicketRequest)

                    try {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "------------LOGIN Create API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                createTicketRequest
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val createTicketRequest =
                        RequestHandler.getCreateMunicipalCitationTicketRequest(
                            welcomeForm = welcomeForm,
                            insuranceModel = mIssuranceModel,
                            mImages = mImages,
                            isReissue = false,
                            timeLimitEnforcementObservedTime = mIssuranceModel.citationData?.officer?.observationTime.nullSafety(),
                            ticketCategory = API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET
                        )

                    welcomeScreenViewModel.callCreateMunicipalCitationTicketAPI(createTicketRequest)
                    try {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "------------LOGIN Create Municipal Citation API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                createTicketRequest
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            lifecycleScope.launch {
                welcomeScreenViewModel.updateCitationUploadStatus(
                    CITATION_UNUPLOADED_API_FAILED, mIssuranceModel.citationNumber
                )
            }
        }
    }

    /* Call Api For Ticket Cancel */
    private fun callTicketCancelApi(mTicketId: String, mCitationNumber: String) {
        if (!requireContext().isInternetAvailable()) {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
            return
        }

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val cancelList =
                        welcomeScreenViewModel.getOfflineCancelCitation() ?: emptyList()
                    if (mTicketId.isEmpty()) {
                        cancelList.firstOrNull { item ->
                            item?.ticketNumber == mCitationNumber || (item?.uploadedCitationId?.isNotEmpty() == true)
                        }
                    } else {
                        val found = cancelList.firstOrNull { item ->
                            item?.ticketNumber == mCitationNumber && item.uploadedCitationId == "none"
                        }
                        if (found != null) {
                            found.uploadedCitationId = mTicketId
                            found
                        } else null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            result?.let { res ->
                cancelTicketDataObject = res
                val uploadedId = res.uploadedCitationId?.trim()
                if (!uploadedId.isNullOrEmpty() && !uploadedId.equals("none", ignoreCase = true)) {
                    logE("==>Offline:", ObjectMapperProvider.instance.writeValueAsString(res))

                    val ticketCancelRequest = TicketCancelRequest().apply {
                        status = res.status.toString()
                        mNote = res.note.toString()
                        mReason = res.reason.toString()
                        mType = res.type.toString()
                    }
                    mUploadCitationIdForCancel = uploadedId

                    welcomeScreenViewModel.callTicketCancelAPI(uploadedId, ticketCancelRequest)

                    try {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "------------WELCOME TICKET  Cancelled API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                ticketCancelRequest
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun uploadOfflineTimingsToServer() {
        lifecycleScope.launch {
            val result = try {
                withContext(Dispatchers.IO) {
                    welcomeScreenViewModel.getLocalTimingDataList()
                        ?.firstOrNull { it?.formStatus == 1 }?.also { mAddTimingID = it.id }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            try {
                logI(
                    "==>Offline:",
                    "Called${ObjectMapperProvider.instance.writeValueAsString(result)}"
                )
                addTimingDatabaseModel = result
                timingBannerList?.clear()
                mImagesForTiming.clear()

                val images = try {
                    withContext(Dispatchers.IO) {
                        welcomeScreenViewModel.getTimingImageUsingTimingRecordId(mAddTimingID)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

                images?.let { timingBannerList?.addAll(it) }

                if (timingBannerList?.isNotEmpty().nullSafety()) {
                    callUploadAllImages()
                } else {
                    result?.let { callAddTimingsAPI(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun removeTimingImagesFromFolder() {
        val banners = timingBannerList ?: return
        if (banners.isEmpty()) return

        lifecycleScope.launch(Dispatchers.IO) {
            for (item in banners) {
                val path = item?.timingImage.nullSafety()
                if (path.isBlank()) continue

                try {
                    val oldFile = File(path)
                    if (oldFile.exists() && !oldFile.delete()) {
                        logD(
                            "WelcomeScreenFragment",
                            "Failed to delete timing image: ${oldFile.absolutePath}"
                        )
                    }
                } catch (t: Throwable) {
                    logD("WelcomeScreenFragment", "Error deleting timing image: $path" + t.message)
                }
            }
        }
    }

    private fun callUploadAllImages() {
        if (requireContext().isInternetAvailable()) {
            val banners = timingBannerList ?: return

            for (item in banners) {
                val file = File(item?.timingImage.nullSafety())
                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val mPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "files", file.name, requestFile
                )

                val fileNames = arrayOf(FileUtil.getFileNameWithoutExtension(file.name))

                //Code for one by one upload
                val mRequestBodyType =
                    API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES.toRequestBody("text/plain".toMediaTypeOrNull())
                welcomeScreenViewModel.callUploadTimeImageAPI(fileNames, mRequestBodyType, mPart)
            }
        } else {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For citation layout details */
    private fun callAddTimingsAPI(addTimingDatabaseModel: AddTimingDatabaseModel) {
        if (requireContext().isInternetAvailable()) {
            val mAddTimingRequest = AddTimingRequest()
            mAddTimingRequest.lprState = addTimingDatabaseModel.lprState
            mAddTimingRequest.lprNumber = addTimingDatabaseModel.lprNumber
            mAddTimingRequest.meterNumber = addTimingDatabaseModel.meterNumber
            mAddTimingRequest.mLocation = addTimingDatabaseModel.mLocation
            mAddTimingRequest.block = addTimingDatabaseModel.block
            mAddTimingRequest.regulationTime = addTimingDatabaseModel.regulationTime
            mAddTimingRequest.street = addTimingDatabaseModel.street
            mAddTimingRequest.side = addTimingDatabaseModel.side
            mAddTimingRequest.zone = addTimingDatabaseModel.zone
            mAddTimingRequest.pbcZone = ""
            mAddTimingRequest.remark = addTimingDatabaseModel.remark
            mAddTimingRequest.status = addTimingDatabaseModel.mStatus
            mAddTimingRequest.latitude = addTimingDatabaseModel.latitude
            mAddTimingRequest.longitiude = addTimingDatabaseModel.longitiude
            mAddTimingRequest.source = addTimingDatabaseModel.source
            mAddTimingRequest.officerName = addTimingDatabaseModel.officerName
            mAddTimingRequest.badgeId = addTimingDatabaseModel.badgeId
            mAddTimingRequest.shift = addTimingDatabaseModel.shift
            mAddTimingRequest.supervisor = addTimingDatabaseModel.supervisor
            mAddTimingRequest.markStartTimestamp = addTimingDatabaseModel.markStartTimestamp
            mAddTimingRequest.markIssueTimestamp = addTimingDatabaseModel.markIssueTimestamp
            mAddTimingRequest.mMake = addTimingDatabaseModel.mMake
            mAddTimingRequest.mModel = addTimingDatabaseModel.mModel
            mAddTimingRequest.mColor = addTimingDatabaseModel.mColor
            mAddTimingRequest.mAddress = addTimingDatabaseModel.mAddress
            mAddTimingRequest.imageUrls = mImagesForTiming
            welcomeScreenViewModel.callAddTimingAPI(mAddTimingRequest)
        }
    }

    private fun getGeoAddress() {
        lifecycleScope.launch {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")
                .nullSafety(defaultValue = DEFAULT_VALUE_ZERO_DOT_ZERO_STR).toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")
                .nullSafety(defaultValue = DEFAULT_VALUE_ZERO_DOT_ZERO_STR).toDouble()

            val addressLine = getAddressFromLatLng(requireContext(), mLat, mLong)

            printAddress = addressLine?.substringBefore(",").nullSafety().trim()

            val tokens = printAddress.split(Regex("\\s+")).filter { it.isNotBlank() }
            val firstToken = tokens.firstOrNull().orEmpty()

            // Block (rounded) - safely attempt rounding, fallback to first token
            mRoundOfAddress =
                runCatching { AppUtils.roundOfBlock(firstToken) }.getOrDefault(firstToken)
            mAutoComTextViewBlock?.setText(mRoundOfAddress)

            // Street - everything after the first token or fallback to full primary address
            val street = if (tokens.size > 1) tokens.drop(1).joinToString(" ") else printAddress
            mAutoComTextViewStreet?.setText(street)
        }
    }

    private fun createSignatureImagesNameList(): Array<String>? {
        val badge =
            mWelcomeResponseData?.data?.getOrNull(0)?.responsedata?.user?.officerBadgeId?.takeIf { it.isNotBlank() }

        return if (badge != null) arrayOf("${badge}_${API_CONSTANT_SIGNATURE_IMAGES}") else emptyArray()
    }

    private fun createSignatureImageMultipart(): MultipartBody.Part? {
        val file = fileSignature ?: return null
        if (!file.exists()) return null

        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("files", file.name, requestFile)
    }

    private fun callSignatureBulkImageUpload() {
        if (requireContext().isInternetAvailable()) {
            val mRequestBodyType =
                API_CONSTANT_SIGNATURE_IMAGES.toRequestBody("text/plain".toMediaTypeOrNull())

            DialogUtil.showLoader(
                context = requireContext(),
                message = getString(R.string.loader_text_please_wait_we_are_loading_data)
            )

            welcomeScreenViewModel.callUploadSignatureImageAPI(
                createSignatureImagesNameList(), mRequestBodyType, createSignatureImageMultipart()
            )
        } else {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
        }
    }


    /**
     * download officer signature image
     */
    private fun callDownloadBitmapApi(mPrintBitmapLink: String?) {
        if (isInternetAvailable(requireContext())) {
            if (mPrintBitmapLink != null && !mPrintBitmapLink.isEmpty() && mPrintBitmapLink.length > 5) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = API_CONSTANT_SIGNATURE_IMAGES
                val links = Links()
                links.img1 = mPrintBitmapLink
                downloadBitmapRequest.links = links
                welcomeScreenViewModel.callDownloadBitmapAPI(downloadBitmapRequest)
            } else {
                requireContext().toast(
                    getString(R.string.err_msg_down_load_image)
                )
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * Function used to get download header footer link from secure URL
     */
    private fun callGetHeaderFooterDownloadLinkAPI(isHeader: Boolean, mPrintBitmapLink: String?) {
        if (isInternetAvailable(requireContext())) {
            if (mPrintBitmapLink != null && mPrintBitmapLink.isNotEmpty()) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = API_CONSTANT_DOWNLOAD_TYPE_LOGO_IMAGE
                val links = Links()
                links.img1 = mPrintBitmapLink
                downloadBitmapRequest.links = links
                if (isHeader) {
                    welcomeScreenViewModel.callDownloadHeaderBitmapAPI(downloadBitmapRequest)
                } else {
                    welcomeScreenViewModel.callDownloadFooterBitmapAPI(downloadBitmapRequest)
                }
            } else {
                requireContext().toast(
                    getString(R.string.err_msg_down_load_image)
                )
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun uploadActivityImages() {
        mImages.clear()

        val banners = bannerList.orEmpty().filterNotNull()
        if (banners.isEmpty()) {
            uploadActivityImages = false
            saveActivityAPI()
            return
        }

        uploadActivityImages = true
        imageUploadSuccessCount = 0

        banners.forEachIndexed { index, item ->
            val path = item.timingImage
            if (!path.isNullOrBlank()) {
                callUploadImages(File(path), index, "ActivityImages")
            }
        }
    }

    private fun launchCameraForActivityImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentCount = bannerList?.size ?: 0
            val maxCount = mainActivityViewModel.getMaxImageCount()
            if (currentCount >= maxCount) {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_max_image_reached),
                    message = getString(
                        R.string.error_desc_max_image_reached, maxCount.toString()
                    ),
                    positiveButtonText = getString(R.string.button_text_ok),
                )
                return@launch
            }

            val mySession = session ?: return@launch
            mySession.takePicture(
                TEMP_IMAGE_FILE_NAME, CameraHelper.SaveLocation.APP_EXTERNAL_FILES
            ) { bmp ->
                bmp?.let {
                    mViewPagerBanner.post { mViewPagerBanner.visibility = View.VISIBLE }
                    val timeStampBitmap = AppUtils.timestampItAndSave(it)
                    saveActivityImage(timeStampBitmap)
                }
            }
        }
    }

    private fun saveActivityImage(finalBitmap: Bitmap?) {
        if (finalBitmap == null) return

        val ctx = requireContext()
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        ).apply {
            if (!exists()) mkdirs()
        }

        val fileName = "${getSiteId(ctx)}_${mActivityId}_Image_".getImageFileName()
        val file = File(myDir, fileName).also { if (it.exists()) it.delete() }

        try {
            FileOutputStream(file).use { out ->
                val compressed = finalBitmap.compress(Bitmap.CompressFormat.JPEG, 45, out)
                out.flush()
                if (!compressed) throw IOException("Bitmap compression failed")
            }

            val oldFile = File(myDir, "$TEMP_IMAGE_FILE_NAME.$IMAGE_FILE_EXTENSION_JPG")
            if (oldFile.exists()) oldFile.delete()

            val idStr = SDF_IMAGE_ID_TIMESTAMP.format(Date())
            val idInt = idStr.toIntOrNull() ?: idStr.hashCode()

            val mImage = TimingImagesModel().apply {
                timingImage = file.path
                status = 0
                id = idInt
                timingRecordId = idInt
                deleteButtonStatus = SHOW_DELETE_BUTTON
            }

            bannerList?.add(mImage)
            setCameraImages()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (!finalBitmap.isRecycled) finalBitmap.recycle()
            } catch (_: Exception) {
            }
        }
    }


    //-----Start of Timing Image Module-----//
    private fun setCameraImages() {
        mViewPagerBanner.post {
            if (bannerList?.isNotEmpty().nullSafety()) {
                showImagesBanner(bannerList)
                mViewPagerBanner.showView()
                pagerIndicator.showView()
            } else {
                mViewPagerBanner.hideView()
                pagerIndicator.hideView()
            }
        }
    }

    private fun setBannerImageAdapter() {
        mBannerAdapter = TimingViewPagerBannerAdapter(
            requireContext(), object : TimingViewPagerBannerAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        icon = R.drawable.icon_delete,
                        title = getString(R.string.error_title_delete_image),
                        message = getString(
                            R.string.error_desc_delete_image
                        ),
                        positiveButtonText = getString(R.string.button_text_yes),
                        negativeButtonText = getString(R.string.button_text_no),
                        listener = object : AlertDialogListener {
                            override fun onPositiveButtonClicked() {
                                bannerList?.removeAt(position)
                                setCameraImages()
                                mBannerAdapter?.notifyDataSetChanged()
                            }
                        })
                }
            })
    }

    private fun showImagesBanner(mImageList: List<TimingImagesModel?>?) {
        val images = mImageList.orEmpty()
        val adapter = mBannerAdapter ?: return
        if (images.isEmpty()) return

        adapter.setTimingBannerList(images)
        mViewPagerBanner.adapter = adapter
        mViewPagerBanner.currentItem = 0

        runCatching {
            ViewPagerUtils.setupViewPagerDots(
                context = requireContext(),
                viewPager = mViewPagerBanner,
                dotsContainer = pagerIndicator,
                totalCount = adapter.count.nullSafety()
            )
        }.onFailure { it.printStackTrace() }
    }
    //-----End of Timing Image Module-----//


    //-----Start of Equipment Inventory Module-----//
    /**
     * Function used to get equipment inventory either from backend or local database
     * if local equipment inventory null then it will call API get the same & later on success , it will call officer equipment
     * to sync with out list
     * else, it will call officer's equipment API to get all the equipment which is being used by the officer
     * along with requireContext(), it will setup the view pager for equipment inventory
     */
    private fun getEquipmentInventory(officerIdValue: String) {
        officerId = officerIdValue
        lifecycleScope.launch {
            if (welcomeScreenViewModel.getQrCodeInventoryData().isNullOrEmpty().nullSafety()) {
                if (isInternetAvailable(requireContext())) {
                    val mDropdownDatasetRequest = DropdownDatasetRequest()
                    mDropdownDatasetRequest.type = DATASET_INVENTORY_REPORT_LIST
                    mDropdownDatasetRequest.shard = 1
                    welcomeScreenViewModel.callGetEquipmentInventoryAPI(mDropdownDatasetRequest)
                } else {
                    requireContext().toast(
                        getString(R.string.err_msg_connection_was_refused)
                    )
                }
            } else {
                callGetOfficerEquipmentInventoryList()
            }
        }

        setEquipmentInventoryListOnUI()
    }

    /**
     * Function to call get officer's equipment API, response contains all the equipments which is
     * checkout by the officer and now on officer's name
     */
    private fun callGetOfficerEquipmentInventoryList() {
        welcomeScreenViewModel.callGetOfficerEquipmentList()
    }

    /**
     * This API is used to checkout the equipment for use or checkin the equipment after the use.
     * before calling the API , it checks if the QR value is there in inventory system or not and also it checks
     * if the scanned QR code is already checkout or not.
     * Response contains success or error
     */
    private fun callLogEquipmentCheckInOutAPI(
        from: String, scannedKey: String, scannedValue: String
    ) {
        lifecycleScope.launch {
            val qrInventoryDetailModel = qrCodeInventoryBannerList?.firstOrNull {
                it?.equipmentName.equals(
                    scannedKey, true
                ) && it?.equipmentValue.equals(scannedValue, true)
            }

            val inventoryDataToShow =
                welcomeScreenViewModel.getInventoryToShowData() as MutableList<InventoryToShowTable?>

            equipmentID = qrInventoryDetailModel?.equipmentID
            equipmentName = qrInventoryDetailModel?.equipmentName
            equipmentValue = qrInventoryDetailModel?.equipmentValue

            val equipmentCheckInOutRequest = EquipmentCheckInOutRequest()
            equipmentCheckInOutRequest.equipmentID = equipmentID

            if (qrInventoryDetailModel != null) {
                if (from == FROM_EQUIPMENT_CHECKOUT) {
                    if (InventoryModuleUtil.isScannedEquipmentAlreadyCheckedOut(
                            scannedKey, scannedValue, inventoryDataToShow
                        )
                    ) {
                        AlertDialogUtils.showDialog(
                            context = requireContext(),
                            title = getString(R.string.title_inventory_update),
                            message = getString(
                                R.string.msg_this_qr_code_is_already_scanned_and_equipment_is_checked_out
                            ),
                            positiveButtonText = getString(R.string.button_text_ok)
                        )

                    } else {
                        if (InventoryModuleUtil.isScannedEquipmentCategoryAlreadyCheckedOut(
                                scannedKey, inventoryDataToShow
                            )
                        ) {
                            AlertDialogUtils.showDialog(
                                context = requireContext(),
                                title = getString(R.string.title_inventory_update),
                                message = getString(
                                    R.string.msg_one_of_the_inventory_is_already_checked_out_please_check_in_that_first
                                ),
                                positiveButtonText = getString(R.string.button_text_ok)
                            )
                        } else {
                            welcomeScreenViewModel.callLogEquipmentCheckedOutAPI(
                                equipmentCheckInOutRequest
                            )
                        }

                    }
                } else {
                    welcomeScreenViewModel.callLogEquipmentCheckedInAPI(
                        equipmentCheckInOutRequest
                    )
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.title_inventory_update),
                    message = getString(
                        R.string.msg_the_qr_code_you_scanned_is_not_found_in_the_our_inventory_system
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    /**
     * Once we get success from get equipment API, it will save all the received equipment in the database for further use.
     */
    suspend fun saveEquipmentInventoryInToDB(equipmentItemDetailList: List<EquipmentItemDetail>?) {
        welcomeScreenViewModel.deleteQrCodeInventoryTable()
        welcomeScreenViewModel.deleteInventoryToShowTable()

        equipmentItemDetailList?.forEach { equipmentItemDetail ->
            val qrCodeInventoryTable = QrCodeInventoryTable()
            qrCodeInventoryTable.equipmentID = equipmentItemDetail.id
            qrCodeInventoryTable.equipmentName = equipmentItemDetail.equipmentName
            qrCodeInventoryTable.equipmentValue = equipmentItemDetail.equipmentValue
            qrCodeInventoryTable.required = equipmentItemDetail.isRequired.boolToInt()
            qrCodeInventoryTable.checkedOut = equipmentItemDetail.isCheckedOut.boolToInt()
            qrCodeInventoryTable.lastCheckedOut = equipmentItemDetail.lastCheckedOut
            welcomeScreenViewModel.insertQrCodeInventoryData(qrCodeInventoryTable)
        }

        //Setting up QR code Inventory used to show to the user
        val equipmentToShow = equipmentItemDetailList?.distinctBy { it.equipmentName }
        equipmentToShow?.forEach { equipmentItemToShowDetail ->
            val inventoryToShowTable = InventoryToShowTable()
            inventoryToShowTable.equipmentID = ""//equipmentItemToShowDetail.id
            inventoryToShowTable.equipmentName = equipmentItemToShowDetail.equipmentName
            inventoryToShowTable.equipmentValue = ""//equipmentItemToShowDetail.equipmentValue
            inventoryToShowTable.required = equipmentItemToShowDetail.isRequired.boolToInt()
            inventoryToShowTable.checkedOut = 0//equipmentItemToShowDetail.isCheckedOut.boolToInt()
            inventoryToShowTable.lastCheckedOut = ""//equipmentItemToShowDetail.lastCheckedOut
            welcomeScreenViewModel.insertInventoryToShowData(inventoryToShowTable)
        }

        callGetOfficerEquipmentInventoryList()
    }

    /**
     * This used to update our local database for checking & checkout equipments
     */
    suspend fun updateEquipmentInventory(
        checkStatus: Int, equipmentName: String, equipmentValue: String?
    ) {
        welcomeScreenViewModel.updateInventoryToShowDataByName(
            checkStatus, equipmentName.nullSafety(), equipmentValue.nullSafety()
        )
    }

    /**
     * This used to update our local database for checkin & checkout equipments along with officers equipment list
     */
    private fun updateOfficerEquipmentHistoryToDB(officerEquipmentItemDetailList: List<OfficerEquipmentItemDetail>? = null) {
        lifecycleScope.launch {
            if (officerEquipmentItemDetailList != null) {

                qrCodeInventoryBannerList =
                    welcomeScreenViewModel.getQrCodeInventoryData() as MutableList<QrCodeInventoryTable?>?

                inventoryToShowList?.forEachIndexed { index, qrCodeInventoryTable ->
                    val officerEquipmentModel = officerEquipmentItemDetailList.firstOrNull {
                        it.equipmentName.equals(
                            qrCodeInventoryTable?.equipmentName, true
                        )
                    }

                    if (officerEquipmentModel == null) {
                        updateEquipmentInventory(
                            EQUIPMENT_CHECKED_IN,
                            qrCodeInventoryTable?.equipmentName.nullSafety(),
                            null
                        )
                    } else {
                        updateEquipmentInventory(
                            EQUIPMENT_CHECKED_OUT,
                            qrCodeInventoryTable?.equipmentName.nullSafety(),
                            qrCodeInventoryTable?.equipmentValue.nullSafety()
                        )
                    }
                }
            }

            setEquipmentInventoryAdapter()
        }
    }

    private fun setEquipmentInventoryListOnUI() {
        cvQrCode.visibility = View.VISIBLE
        setupEquipmentInventoryBannerAdapter()
    }

    private fun updateQrScanList() {
        lifecycleScope.launch {
            qrCodeInventoryBannerList =
                welcomeScreenViewModel.getQrCodeInventoryData() as MutableList<QrCodeInventoryTable?>?
            inventoryToShowList =
                welcomeScreenViewModel.getInventoryToShowData() as MutableList<InventoryToShowTable?>?

            showEquipmentInventoryBanner()
            qrCodeInventoryBannerAdapter?.notifyDataSetChanged()
        }
    }

    private fun setEquipmentInventoryAdapter() {
        lifecycleScope.launch {
            qrCodeInventoryBannerList =
                welcomeScreenViewModel.getQrCodeInventoryData() as MutableList<QrCodeInventoryTable?>?
            inventoryToShowList =
                welcomeScreenViewModel.getInventoryToShowData() as MutableList<InventoryToShowTable?>?

            mViewPagerBannerQrCode.post {
                if (inventoryToShowList?.isNotEmpty().nullSafety()) {
                    mViewPagerBannerQrCode.showView()
                    pagerIndicatorQrCode.showView()
                } else {
                    mViewPagerBannerQrCode.hideView()
                    pagerIndicatorQrCode.hideView()
                }
            }
            showEquipmentInventoryBanner()
        }
    }

    private fun showEquipmentInventoryBanner() {
        if (inventoryToShowList?.isNotEmpty().nullSafety()) {
            qrCodeInventoryBannerAdapter?.setEquipmentList(inventoryToShowList)
            mViewPagerBannerQrCode.adapter = qrCodeInventoryBannerAdapter
            mViewPagerBannerQrCode.currentItem = 0
        }

        if (inventoryToShowList?.isNotEmpty().nullSafety()) {
            try {
                ViewPagerUtils.setupViewPagerDots(
                    context = requireContext(),
                    viewPager = mViewPagerBannerQrCode,
                    dotsContainer = pagerIndicatorQrCode,
                    totalCount = qrCodeInventoryBannerAdapter?.count.nullSafety()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupEquipmentInventoryBannerAdapter() {
        qrCodeInventoryBannerAdapter = ViewPagerBannerAdapterQrCodeInventory(
            requireContext(),
            object : ViewPagerBannerAdapterQrCodeInventory.ListItemSelectListener {
                override fun onItemClick(position: Int, isCheckedOut: Int) {
                    if (isCheckedOut != EQUIPMENT_CHECKED_OUT) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            permissionManager.ensurePermissionsThen(
                                permissions = arrayOf(Manifest.permission.CAMERA),
                                rationaleMessage = getString(R.string.permission_message_camera_permission_required)
                            ) {
                                launchQRScannerActivityForResult(FROM_EQUIPMENT_CHECKOUT)
                            }
                        }
                    } else {
                        AlertDialogUtils.showDialog(
                            context = requireContext(),
                            cancelable = false,
                            title = getString(R.string.title_inventory_update),
                            message = getString(R.string.error_inventory_already_checkout_out),
                            positiveButtonText = getString(R.string.btn_text_yes),
                            negativeButtonText = getString(R.string.btn_text_no),
                            listener = object : AlertDialogListener {
                                override fun onPositiveButtonClicked() {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        permissionManager.ensurePermissionsThen(
                                            permissions = arrayOf(Manifest.permission.CAMERA),
                                            rationaleMessage = getString(R.string.permission_message_camera_permission_required)
                                        ) {
                                            launchQRScannerActivityForResult(
                                                FROM_EQUIPMENT_CHECKIN
                                            )
                                        }
                                    }
                                }
                            })

                    }
                }
            })

        mViewPagerBannerQrCode.adapter = qrCodeInventoryBannerAdapter
    }
    //-----End of Equipment Inventory Module-----

    //-----Start of activity for result-----//
    //QR Scanner activity for result launcher
    private fun launchQRScannerActivityForResult(from: String) {
        val intent = Intent(requireContext(), QRCodeScanner::class.java)
        intent.putExtra(INTENT_KEY_FROM, from)
        qrScannerForResult.launch(intent)
    }
    //-----End of activity for result-----//

    /**
     * We are setting up the ALPR license key in shared preference to avoid any conflict at the scan time
     * This will be called twice, where we get the activity data
     */
    private fun setLicenseKeyForAlpr() {
        val deviceAndroidId = requireContext().getAndroidID()
        //val deviceAndroidId = "a7590568da8bc45d" //Testing just only

        //We are matching above device friendly name wih list if licenses we are getting from backend to get the correct license for the device.
        val deviceLicenseObject =
            mWelcomeListDataSet?.welcomeList?.deviceLicenseStats?.firstOrNull()?.responseDeviceLicense?.firstOrNull { it.androidId == deviceAndroidId }

        logD("==>LICENSE_NUMBER:", deviceLicenseObject?.license.nullSafety("NO LICENSE FOUND"))
        logD("==>LICENSE_NUMBER:DeviceID", deviceAndroidId)
        logD("==>LICENSE_NUMBER:DeviceID", deviceLicenseObject?.deviceFriendlyName.nullSafety())

        //If device's friendly name matches with any object's friendly name from backend response, then it will return the license for doubango
        //return deviceLicenseObject?.license.nullSafety("NO LICENSE FOUND")

        if (sharedPreference.read(SharedPrefKey.LICENSE_KEY_ALPR, "").isNullOrEmpty()) {
            sharedPreference.write(
                SharedPrefKey.LICENSE_KEY_ALPR, deviceLicenseObject?.license.nullSafety("")
            )

            logD(
                "==>LICENSE_NUMBER:IN", sharedPreference.read(
                    SharedPrefKey.LICENSE_KEY_ALPR, "INSIDE_NOT FOUND"
                ).nullSafety()
            )
        }

        logD(
            "==>LICENSE_NUMBER:OUT", sharedPreference.read(
                SharedPrefKey.LICENSE_KEY_ALPR, "OUTSIDE_NOT FOUND"
            ).nullSafety()
        )
    }
}