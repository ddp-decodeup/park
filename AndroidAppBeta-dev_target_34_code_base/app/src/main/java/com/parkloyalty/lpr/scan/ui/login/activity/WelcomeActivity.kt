package com.parkloyalty.lpr.scan.ui.login.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.TrafficStats
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.DataBaseUtil
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.databasetable.QrCodeInventoryTable
import com.parkloyalty.lpr.scan.databinding.ActivityWelcomeBinding
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.boolToInt
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showToast
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_FOOTER_URL_FOR_FACSIMILE
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_HEADER_URL_FOR_FACSIMILE
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.locationservice.BackgroundLocationUpdateService
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Resource
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutRequest
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutResponse
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentInventoryResponse
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentItemDetail
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.OfficerEquipmentHistoryResponse
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.OfficerEquipmentItemDetail
import com.parkloyalty.lpr.scan.network.models.ScannedImageUploadResponse
import com.parkloyalty.lpr.scan.qrcode.QRCodeScanner
import com.parkloyalty.lpr.scan.qrcode.ViewPagerBannerAdapterQrCodeInventory
import com.parkloyalty.lpr.scan.qrcode.model.InventoryViewModel
import com.parkloyalty.lpr.scan.vehiclestickerscan.VehicleStickerScanActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetails2Activity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetailsActivity
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.TimingViewPagerBannerAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.license.LprScanActivity
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationCommentsDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationHeaderDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationInvoiceFeeStructure
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLocationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationOfficerDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationVehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationViolationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CommentsDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.HeaderDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.InvoiceFeeStructure
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.LocationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.MotoristDetailsModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.OfficerDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.VehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.ViolationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingViewModel
import com.parkloyalty.lpr.scan.ui.dashboard.DashboardActivity
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationDatasetModel
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
import com.parkloyalty.lpr.scan.ui.login.model.ActivityLogViewModel
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
import com.parkloyalty.lpr.scan.ui.login.model.UpdateSiteOfficerViewModel
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDataList
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDb
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeList
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeUser
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeViewModel
import com.parkloyalty.lpr.scan.ui.login.model.ZoneResponse
import com.parkloyalty.lpr.scan.ui.login.model.ZoneStat
import com.parkloyalty.lpr.scan.ui.municipalcitation.MunicipalCitationDetailsActivity
import com.parkloyalty.lpr.scan.ui.ticket.TicketDetailsActivity
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapFIleViewModel
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.DownloadBitmapResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.Links
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketCancelViewModel
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusViewModel
import com.parkloyalty.lpr.scan.ui.unuploadimages.UnUploadImagesActivityView
import com.parkloyalty.lpr.scan.util.ACTIVITY_LOG_WELCOME_SCAN
import com.parkloyalty.lpr.scan.util.API_CONSTANT_EQUIPMENT_INVENTORY
import com.parkloyalty.lpr.scan.util.API_CONSTANT_FOOTER_IMAGE_URL_DOWNLOAD
import com.parkloyalty.lpr.scan.util.API_CONSTANT_HEADER_IMAGE_URL_DOWNLOAD
import com.parkloyalty.lpr.scan.util.API_CONSTANT_OFFICER_EQUIPMENT_INVENTORY
import com.parkloyalty.lpr.scan.util.API_CONSTANT_SIGNATURE_IMAGES
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET
import com.parkloyalty.lpr.scan.util.API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.getBeatSetEmptyFromSetting
import com.parkloyalty.lpr.scan.util.AppUtils.getDeviceId
import com.parkloyalty.lpr.scan.util.AppUtils.getLprLock
import com.parkloyalty.lpr.scan.util.AppUtils.getSiteId
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.setListOnly
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.AppUtils.showKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateWelcome
import com.parkloyalty.lpr.scan.util.AppUtils.splitID
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.CheckTypeOfField
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
import com.parkloyalty.lpr.scan.util.DATASET_VIO_LIST
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
import com.parkloyalty.lpr.scan.util.DATASET_VOID_AND_REISSUE_REASON_LIST
import com.parkloyalty.lpr.scan.util.DialogUtils
import com.parkloyalty.lpr.scan.util.EQUIPMENT_CHECKED_IN
import com.parkloyalty.lpr.scan.util.EQUIPMENT_CHECKED_OUT
import com.parkloyalty.lpr.scan.util.FROM_EQUIPMENT_CHECKIN
import com.parkloyalty.lpr.scan.util.FROM_EQUIPMENT_CHECKOUT
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.FileUtil.getSignatureFileNameWithExt
import com.parkloyalty.lpr.scan.util.INTENT_KEY_FROM
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNED_EQUIPMENT_KEY
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNED_EQUIPMENT_VALUE
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.util.PermissionUtils
import com.parkloyalty.lpr.scan.util.SETTING_MAX_IMAGES_COUNT
import com.parkloyalty.lpr.scan.util.SHOW_DELETE_BUTTON
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.util.Util.setLastUpdateDataSetStatus
import com.parkloyalty.lpr.scan.util.permissions.RequestMultiplePermissions
import com.parkloyalty.lpr.scan.util.setAsAccessibilityHeading
import com.parkloyalty.lpr.scan.util.setCustomAccessibility
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import se.warting.signatureview.views.SignaturePad
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.UUID
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.toString

@AndroidEntryPoint
class WelcomeActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, CustomDialogHelper {
    // The BroadcastReceiver used to listen from broadcasts from the service.
    lateinit var drawerLy: DrawerLayout
    lateinit var mLayLogout: LinearLayoutCompat
    lateinit var mTextViewUserName: AppCompatTextView
    lateinit var mTextViewOfficerId: AppCompatTextView
    lateinit var mTextViewBadgeId: AppCompatTextView
    lateinit var mTextViewPreLogin: AppCompatTextView
    lateinit var mTextViewOfficerDetails: AppCompatTextView
    lateinit var mTextViewAssetDetails: AppCompatTextView
    lateinit var mTextViewLocationDetails: AppCompatTextView
    lateinit var mTextViewOtherDetails: AppCompatTextView
    lateinit var mTextViewCurLogin: AppCompatTextView
    lateinit var mTextViewLoginDetails: AppCompatTextView
    lateinit var layBottomKeyboard: LinearLayoutCompat
    lateinit var layOtherDetails: LinearLayoutCompat
    lateinit var layMainOtherDetails: LinearLayoutCompat
    lateinit var layAssetDetails: LinearLayoutCompat
    lateinit var layLocationDetails: LinearLayoutCompat
    lateinit var layBadgeId: LinearLayoutCompat
    lateinit var layOfficerId: LinearLayoutCompat
    lateinit var layOfficer: LinearLayoutCompat
    lateinit var layButtons: LinearLayoutCompat
    lateinit var linearLayoutEmptyActivity: LinearLayoutCompat
    lateinit var cardCurLogin: CardView
    lateinit var cardPreLogin: CardView
    lateinit var btnDoneActivity: AppCompatButton
    lateinit var imageViewSignature: AppCompatImageView
    lateinit var mTextViewSignName: AppCompatTextView
    lateinit var mTextViewWelcomeBack: AppCompatTextView
    lateinit var appBarLayout: AppBarLayout
    lateinit var mViewPagerBanner: ViewPager
    lateinit var pagerIndicator: LinearLayoutCompat
    lateinit var mViewPagerBannerQrCode: ViewPager
    lateinit var pagerIndicatorQrCode: LinearLayoutCompat
    lateinit var ivCameraIcon: AppCompatImageView
    lateinit var textScanCount: AppCompatTextView
    lateinit var llQrcode: LinearLayoutCompat
    lateinit var btnScanSticker: AppCompatButton

    private var mBannerAdapter: TimingViewPagerBannerAdapter? = null
    private var mShowBannerCount = 0
    private var mDotsCount = 0
    private var mDots: Array<ImageView?>? = null
    private var tempUri: String? = null

    //    private var mList: MutableList<TimingImagesModel> = ArrayList()
    private var bannerList: MutableList<TimingImagesModel?>? = ArrayList()
//    private val mImages: MutableList<String> = ArrayList()

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
    private var latestLayoutOther = 0
    private var mActivityId: String = ""
    private var mContext: Context? = null
    private var googleApiClient: GoogleApiClient? = null
    private var myReceiver: MyReceiver? = null
    private var mRequestTimeStart: Long = 0
    private var mResponseTimeEnd: Long = 0
    private var mResponseTime: Long = 0
    private val mDelayMilis = 5000
    private var mDb: AppDatabase? = null
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
    private var UploadActivityImages = false
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
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 2111
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
    private var mSignaturePath: String = ""
    private var fileSignature: File? = null
    private var uploadSignatureLink: String? = ""
    private var mCombinationId: String? = ""

    private var qrCodeInventoryBannerList: MutableList<QrCodeInventoryTable?>? = ArrayList()
    private var qrCodeInventoryBannerAdapter: ViewPagerBannerAdapterQrCodeInventory? = null
    private var inventoryToShowList: MutableList<InventoryToShowTable?>? = ArrayList()


    private val mWelcomeViewModel: WelcomeViewModel? by viewModels()
    private val mUpdateOfficerViewModel: UpdateSiteOfficerViewModel? by viewModels()
    private val mActivityLogViewModel: ActivityLogViewModel? by viewModels()
    private val mCitationDatasetModel: CitationDatasetModel? by viewModels()
    private val mCreateTicketViewModel: CreateTicketViewModel? by viewModels()
    private val mCreateMunicipalCitationTicketViewModel: CreateMunicipalCitationTicketViewModel? by viewModels()
    private val mAddTimingViewModel: AddTimingViewModel? by viewModels()
    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()
    private val mUploadSignatureImageViewModel: UploadImageViewModel? by viewModels()
    private val mTicketCancelViewModel: TicketCancelViewModel? by viewModels()
    private val mTicketStatusViewModel: TicketUploadStatusViewModel? by viewModels()
    private val mDownloadBitmapFIleViewModel: DownloadBitmapFIleViewModel? by viewModels()
    private val inventoryViewModel: InventoryViewModel?  by viewModels()

    private val activityUpdateRequest = ActivityUpdateRequest()
    private var cancelTicketDataObject: OfflineCancelCitationModel? = null

    private var officerId: String? = null
    private var equipmentID: String? = null
    private var equipmentName: String? = null
    private var equipmentValue: String? = null

    private val cameraPermissions = setOf(
        Manifest.permission.CAMERA
    )

    private val requestMultiplePermissions = RequestMultiplePermissions(this)

    private lateinit var binding: ActivityWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        addObservers()
        init()
        setToolbar()

        //        getDeviceIdWithSerial()
        listenToToolbar()
        try {
            mUUID = getDeviceId(this)
            printLog("UUID", mUUID)
            selectedDeViceId.mAndroidId = mUUID.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (ContextCompat.checkSelfPermission(
                this@WelcomeActivity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(
                this@WelcomeActivity,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this@WelcomeActivity, arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ), BLUETOOTH_PERMISSION_REQUEST_CODE
                )
            }
        }

        uploadMissingActivityImages()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
            )
        } else {
//                TODO("VERSION.SDK_INT < R")
        }
//        getMyDatabase()?.dbDAO?.updateCitationBooklet(1, "21149729")

        setAccessibilityForComponents()
    }

    private fun findViewsByViewBinding() {
        drawerLy = binding.drawerLy
        mLayLogout = binding.layoutContentOptionLayout.layLogout
        mTextViewUserName =
            binding.layoutContentWelcome.layoutContentCollapseWelcomeHeader.tvHeaderUserName
        mTextViewOfficerId = binding.layoutContentWelcome.tvOfficerId
        mTextViewBadgeId = binding.layoutContentWelcome.tvBadgeId
        mTextViewPreLogin = binding.layoutContentWelcome.tvPreviousLogin
        mTextViewOfficerDetails = binding.layoutContentWelcome.tvOfficerDetails
        mTextViewAssetDetails = binding.layoutContentWelcome.tvAssetDetails
        mTextViewLocationDetails = binding.layoutContentWelcome.tvLocationDetails
        mTextViewOtherDetails = binding.layoutContentWelcome.tvOtherDetails
        mTextViewCurLogin = binding.layoutContentWelcome.tvCurrentLogin
        mTextViewLoginDetails = binding.layoutContentWelcome.tvLoginDetails
        layBottomKeyboard = binding.layoutContentWelcome.layBottomKeyboard
        layOtherDetails = binding.layoutContentWelcome.layOtherDetails
        layMainOtherDetails = binding.layoutContentWelcome.layMainOtherDetails
        layAssetDetails = binding.layoutContentWelcome.layAssetDetails
        layLocationDetails = binding.layoutContentWelcome.layLocationDetails
        layBadgeId = binding.layoutContentWelcome.layBadgeId
        layOfficerId = binding.layoutContentWelcome.layOfficerId
        layOfficer = binding.layoutContentWelcome.layMainOfficer
        layButtons = binding.layoutContentWelcome.layButtons
        linearLayoutEmptyActivity =
            binding.layoutContentWelcome.layoutContentEmptyActivityLayout.emptyLayoutActivity
        cardCurLogin = binding.layoutContentWelcome.cardCurLogin
        cardPreLogin = binding.layoutContentWelcome.cardPreLogin
        btnDoneActivity = binding.layoutContentWelcome.btnDoneActivity
        imageViewSignature = binding.layoutContentWelcome.imgSignature
        mTextViewSignName = binding.layoutContentWelcome.txtPersonSignature
        mTextViewWelcomeBack =
            binding.layoutContentWelcome.layoutContentCollapseWelcomeHeader.tvWelcomeBack
        appBarLayout = binding.layoutContentWelcome.layoutContentCollapseWelcomeHeader.layId
        mViewPagerBanner = binding.layoutContentWelcome.layoutContentBanner.pagerBanner
        pagerIndicator = binding.layoutContentWelcome.layoutContentBanner.viewPagerCountDots
        mViewPagerBannerQrCode =
            binding.layoutContentWelcome.layoutContentQrCodeInventoryBanner.pagerBannerQrCode
        pagerIndicatorQrCode =
            binding.layoutContentWelcome.layoutContentQrCodeInventoryBanner.viewPagerCountDotsQrCode
        ivCameraIcon = binding.layoutContentWelcome.ivCamera
        textScanCount = binding.layoutContentWelcome.scanCount
        llQrcode = binding.layoutContentWelcome.llQrcode
        btnScanSticker = binding.layoutContentWelcome.btnScanSticker
    }

    private fun setupClickListeners() {
        ivCameraIcon.setOnClickListener {
            if (PermissionUtils.requestCameraAndStoragePermission(this@WelcomeActivity)) {
                if (bannerList!!.size <= 2) {
                    launchCameraIntent()
                } else {
                    showCustomAlertDialog(
                        mContext, "Activity Images",
                        "Only 3 images are allowed.", getString(R.string.alt_lbl_OK),
                        getString(R.string.scr_btn_cancel), this
                    )
                }
            }
        }

        binding.layoutContentWelcome.ivQrscanner.setOnClickListener {
            //if (PermissionUtils.requestCameraAndStoragePermission(this@WelcomeActivity)) {
            val status =
                inventoryToShowList?.firstOrNull { it?.checkedOut != EQUIPMENT_CHECKED_OUT }

            if (status != null) {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        val (granted, denied) = requestMultiplePermissions.request(
                            cameraPermissions
                        )

                        if (granted.size == cameraPermissions.size) {
                            launchQRScannerActivityForResult(FROM_EQUIPMENT_CHECKOUT)
                        }
                    }
                }

            } else {
                DialogUtils.showInfoDialog(
                    context = this,
                    message = getString(R.string.error_all_inventory_items_are_scanned),
                    icon = R.drawable.ic_report,
                    callback = { _, _ ->

                    })
            }
        }

        imageViewSignature.setOnClickListener {
            setSignatureView()
        }

        btnDoneActivity.setOnClickListener {

        }

        binding.layoutContentWelcome.btnScan.setOnClickListener {
//            if (sharedPreference.read(SharedPrefKey.USER_NAME, "").equals("sbreyer")) {
//                launchScreen(
//                    this@WelcomeActivity,
//                    UnUploadImagesActivityView::class.java
//                )
//            } else {
                if (showAndEnableInventoryModule) {
                    if (isRequiredEquipmentCheckedOut(inventoryToShowList)) {
                        bntScanClick()
                    } else {
                        printToastMSG(
                            this,
                            getString(R.string.error_red_box_inventory_is_mandatory_to_proceed)
                        )
                    }
                } else {
                    bntScanClick()
                }
//            }
        }

        binding.layoutContentWelcome.btnDone.setOnClickListener {
            removeFocus()
            if (isFormValid("DONE")) {
                if (!isDetailsChanged()) {
                    if (showAndEnableInventoryModule
                    ) {
                        if (isRequiredEquipmentCheckedOut(inventoryToShowList)) {
                            btnSaveClick()
                        } else {
                            printToastMSG(
                                this,
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
            if (showAndEnableInventoryModule) {
                if (isRequiredEquipmentCheckedOut(inventoryToShowList)) {
                    btnScanStickerClick()
                } else {
                    printToastMSG(
                        this,
                        getString(R.string.error_red_box_inventory_is_mandatory_to_proceed)
                    )
                }
            } else {
                btnScanStickerClick()
            }
        }
    }

    fun setAccessibilityForComponents() {
        setAsAccessibilityHeading(mTextViewLoginDetails)
        setAsAccessibilityHeading(mTextViewOfficerDetails)
        setAsAccessibilityHeading(mTextViewAssetDetails)
        setAsAccessibilityHeading(mTextViewLocationDetails)
        setAsAccessibilityHeading(mTextViewOtherDetails)
        findViewById<AppCompatImageView>(R.id.ivQrscanner)?.setCustomAccessibility(contentDescription = getString(R.string.ada_content_description_scan_inventory_qr_code), role = getString(R.string.ada_role_button))
        ivCameraIcon.setCustomAccessibility(contentDescription = getString(R.string.ada_content_description_camera), role = getString(R.string.ada_role_button))
    }

    fun increaseCursorWindowSize() {
//        try {
//            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
//            field.isAccessible = true
//            field.set(null, 100 * 1024 * 1024) // 100MB
//            println("CursorWindow size increased to 100MB")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@WelcomeActivity,
                        getString(R.string.error_bluetooth_permission_is_not_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                }
            }
        }
    }

    private fun getDeviceIdWithSerial() {
        try {
            val tm: TelephonyManager =
                baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            val tmDevice: String
            val tmSerial: String
            val androidId: String
            tmDevice = "" + tm.getDeviceId()
            tmSerial = "" + tm.getSimSerialNumber()
            androidId = "" + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            mCombinationId = UUID(
                androidId.hashCode().toLong(),
                tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong()
            ).toString()

//            val androidDD = String
//                .format("%16s", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))
//                .replace(' ', '0');
            LogUtil.printLog("VINOD", tmSerial.toString() + "   ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //init toolbar navigation
    private fun setToolbar() {
        initToolbar(
            1,
            this,
            R.id.layHome,
            R.id.layTicketing,
            R.id.layMyActivity,
            R.id.laySetting,
            R.id.layReport,
            R.id.layLogout,
            R.id.drawerLy,
            R.id.imgBack,
            R.id.imgOptions,
            R.id.imgCross,
            R.id.cardTicketing,
            R.id.layIssue,
            R.id.layLookup,
            R.id.layScan,
            R.id.layMunicipalCitation,
            R.id.layGuideEnforcement,
            R.id.laySummary,
            R.id.cardMyActivity,
            R.id.layMap,
            R.id.layContinue,
            R.id.cardGuide,
            R.id.laypaybyplate,
            R.id.laypaybyspace,
            R.id.cardlookup,
            R.id.laycitation,
            R.id.laylpr,
            R.id.layClearcache,
            R.id.laySuperVisorView,
            R.id.layAllReport,
            R.id.layBrokenMeterReport,
            R.id.layCurbReport,
            R.id.layFullTimeReport,
            R.id.layHandHeldMalfunctionReport,
            R.id.laySignReport,
            R.id.layVehicleInspectionReport,
            R.id.lay72HourMarkedVehiclesReport,
            R.id.layBikeInspectionReport,
            R.id.cardAllReport,
            R.id.lay_eow_supervisor_shift_report,
            R.id.layPartTimeReport,
            R.id.layLprHits,
            R.id.laySpecialAssignmentReport,
            R.id.layQRCode,
            R.id.cardQRCode,
            R.id.layGenerateQRCode,
            R.id.layScanQRCode,
            R.id.laySunlight,
            R.id.imgSunlight,
            R.id.lay72hrNoticeToTowReport,
            R.id.layTowReport,
            R.id.laySignOffReport,
            R.id.layNFL,
            R.id.layHardSummer,
            R.id.layAfterSeven,
            R.id.layPayStationReport,
            R.id.laySignageReport,
            R.id.layHomelessReport,
            R.id.laySafetyReport,
            R.id.layTrashReport,
            R.id.layLotCountVioRateReport,
            R.id.layLotInspectionReport,
            R.id.layWordOrderReport,
            R.id.txtlogout,
            R.id.laycameraviolation,
            R.id.layScanSticker,
            R.id.laygenetichit,
            R.id.layDirectedEnforcement,
            R.id.layOwnerBill
        )
    }

    //save db to internal storage
    private fun saveDatabaseToInternal() {
        try {
            backupDatabase()
            createCredFolder()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getTimestampFromDb() {
        try {
            mDataSetTimeObject = getMyDatabase()?.dbDAO?.getUpdateTimeResponse()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun isCitationDatasetExits() {
        updateTimeDataset.add(UpdateTimeDb(DATASET_SETTINGS_LIST, true))
        getMyDatabase()?.dbDAO?.deleteDatasetSettingsListModel()
        Singleton.reset()

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
                        DATASET_STATE_LIST,
                        mDataSetTimeObject!!.timeList!!.stateList!!.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.stateList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_STATE_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.streetList != null && mDataSetTimeObject?.timeList?.streetList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_STREET_LIST,
                        mDataSetTimeObject?.timeList?.streetList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.streetList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_STREET_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.meterList != null && mDataSetTimeObject?.timeList?.meterList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_METER_LIST,
                        mDataSetTimeObject?.timeList?.meterList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.meterList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_METER_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.mSpaceList != null && mDataSetTimeObject?.timeList?.mSpaceList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_SPACE_LIST,
                        mDataSetTimeObject?.timeList?.mSpaceList?.status
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
                        DATASET_VIOLATION_LIST,
                        mDataSetTimeObject?.timeList?.violationList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.violationList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_VIOLATION_LIST, true))
            }

            if (mDataSetTimeObject?.timeList?.vioList != null && mDataSetTimeObject?.timeList?.vioList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_VIO_LIST,
                        mDataSetTimeObject?.timeList?.vioList?.status
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
                        DATASET_SIDE_LIST,
                        mDataSetTimeObject?.timeList?.sideList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.sideList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_SIDE_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.tierStemList != null && mDataSetTimeObject?.timeList?.tierStemList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_TIER_STEM_LIST,
                        mDataSetTimeObject?.timeList?.tierStemList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.tierStemList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_TIER_STEM_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.notesList != null && mDataSetTimeObject?.timeList?.notesList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_NOTES_LIST,
                        mDataSetTimeObject?.timeList?.notesList?.status
                    )
                )
            } else if (mDataSetTimeObject!!.timeList!!.notesList == null) {
                updateTimeDataset.add(UpdateTimeDb(DATASET_NOTES_LIST, true))
            }
            if (mDataSetTimeObject?.timeList?.remarksList != null && mDataSetTimeObject?.timeList?.remarksList?.status.nullSafety()) {
                updateTimeDataset.add(
                    UpdateTimeDb(
                        DATASET_REMARKS_LIST,
                        mDataSetTimeObject?.timeList?.remarksList?.status
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
                        DATASET_LOT_LIST,
                        mDataSetTimeObject?.timeList?.lotList?.status
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
                        DATASET_BLOCK_LIST,
                        mDataSetTimeObject?.timeList?.mBlockList?.status
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
        callApiParallelyRecursive(updateTimeDataset, 0, 1)
        if (mDataSetTimeObject == null ||
            mDataSetTimeObject?.timeList?.cancelReasonList == null ||
            mDataSetTimeObject?.timeList?.violationList == null ||
            mDataSetTimeObject?.timeList?.municipalViolationList == null ||
            mDataSetTimeObject?.timeList?.mBlockList == null ||
            mDataSetTimeObject?.timeList?.carColorList == null ||
            mDataSetTimeObject?.timeList?.remarksList == null
        ) {


            mDataSetTimeObject = TimestampDatatbase();
            mDataSetTimeObject?.timeList = UpdateTimeDataList();
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
            mDataSetTimeObject?.timeList?.vioList = UpdateTimeDb(DATASET_HOLIDAY_CALENDAR_LIST, false)
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
            getMyDatabase()!!.dbDAO!!.insertUpdatedTime(mDataSetTimeObject!!)
        }
    }

    private fun setAllDropdown() {
        CoroutineScope(Dispatchers.IO).async {
//            mDataSetModel = mDb?.dbDAO?.getDataset()
            mWelcomeListDataSet = Singleton.getWelcomeDbObject(getMyDatabase())
            if (mWelcomeListDataSet != null) {
                mDatabaseWelcomeList = mWelcomeListDataSet?.welcomeList
                setLicenseKeyForAlpr()
            }

            CoroutineScope(Dispatchers.Main).async {
                setLoginTimeStamp()
                setDropdownStreet(Singleton.getDataSetList(DATASET_STREET_LIST, getMyDatabase()))
                setDropdownBlockList(Singleton.getDataSetList(DATASET_BLOCK_LIST, getMyDatabase()))
                setDropdownSide(Singleton.getDataSetList(DATASET_SIDE_LIST, getMyDatabase()))

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
                                    SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API,
                                    true
                                )
                            ) {
                                callEventActivityLogApiForBaseActivity("Login")
                                sharedPreference.write(
                                    SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API,
                                    false
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        /**
                         * Upload API LOG File whenever officer come to activity form
                         */
//                        callUploadAPILogsTextFile()
                        /**
                         * In case violation list get empty then refill all the data set again
                         */
                        isDataSetListEmpty()

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


    }

    private fun listenToToolbar() {
        appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {   //  Collapsed
                mTextViewUserName.visibility = View.INVISIBLE
                mTextViewWelcomeBack.visibility = View.INVISIBLE
            } else {   //Expanded
                mTextViewUserName.visibility = View.VISIBLE
                mTextViewWelcomeBack.visibility = View.VISIBLE
            }
        })
    }


    private val welcomeResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.GET_WELCOME)
    }
    private val updateOfficerResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_UPDATE_SITE_OFFICER)
    }
    private val citationDatasetResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CITATION_DATASET)
    }
    private val activityLogResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_ACTIVITY_LOG)
    }

    private val createTicketResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CREATE_TICKET)
    }

    private val createMunicipalCitationTicketResponseObserver =
        Observer { apiResponse: ApiResponse ->
            consumeResponse(apiResponse, DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET)
        }

    private val ticketCancelResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CANCEL)
    }
    private val ticketStatusResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_TICKET_UPLOADE_STATUS_META)
    }

    private val addTimingResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_ADD_TIMING)
    }

    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_IMAGE)
    }

    private val uploadTimingImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES)
    }

    private val downloadBitmapResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_DOWNLOAD_FILE)
    }

    private val downloadHeaderResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, API_CONSTANT_HEADER_IMAGE_URL_DOWNLOAD)
    }

    private val downloadFooterResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, API_CONSTANT_FOOTER_IMAGE_URL_DOWNLOAD)
    }

    private val getEquipmentInventoryResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, API_CONSTANT_EQUIPMENT_INVENTORY)
    }

    private val officerEquipmentResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, API_CONSTANT_OFFICER_EQUIPMENT_INVENTORY)
    }

    private val equipmentCheckoutResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_EQUIPMENT_CHECK_OUT)
    }

    private val equipmentCheckInResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_EQUIPMENT_CHECK_IN)
    }

    private fun addObservers() {
        mWelcomeViewModel?.response?.observe(this, welcomeResponseObserver)
        mUpdateOfficerViewModel?.response?.observe(this, updateOfficerResponseObserver)
        mCitationDatasetModel?.response?.observe(this, citationDatasetResponseObserver)
        mActivityLogViewModel?.response?.observe(this, activityLogResponseObserver)
        mCreateTicketViewModel?.response?.observe(this, createTicketResponseObserver)
        mCreateMunicipalCitationTicketViewModel?.response?.observe(
            this,
            createMunicipalCitationTicketResponseObserver
        )
        mTicketCancelViewModel?.response?.observe(this, ticketCancelResponseObserver)
        mTicketStatusViewModel?.response?.observe(this, ticketStatusResponseObserver)
        mAddTimingViewModel?.response?.observe(this, addTimingResponseObserver)
        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
        mUploadImageViewModel?.responseTimingImageUpload?.observe(
            this,
            uploadTimingImageResponseObserver
        )
        mUploadSignatureImageViewModel?.uploadAllImagesAPIStatus?.observe(
            this,
            uploadScannedImagesAPIResponseObserver
        )
        mDownloadBitmapFIleViewModel?.response?.observe(this, downloadBitmapResponseObserver)
        mDownloadBitmapFIleViewModel?.responseHeaderFile?.observe(
            this,
            downloadHeaderResponseObserver
        )
        mDownloadBitmapFIleViewModel?.responseFooterFile?.observe(
            this,
            downloadFooterResponseObserver
        )
        inventoryViewModel?.responseForGetOfficerEquipment?.observe(
            this,
            officerEquipmentResponseObserver
        )
        inventoryViewModel?.responseForEquipmentCheckout?.observe(
            this,
            equipmentCheckoutResponseObserver
        )
        inventoryViewModel?.responseForEquipmentCheckIn?.observe(
            this,
            equipmentCheckInResponseObserver
        )

        mCitationDatasetModel?.responseForEquipmentInventoryAPI?.observe(
            this,
            getEquipmentInventoryResponseObserver
        )

        // Observe the download status
        mWelcomeViewModel?.downloadStatus?.observe(this, Observer { status ->
            LogUtil.printToastMSG(this, status)
        })
    }

    override fun removeObservers() {
        super.removeObservers()
        mWelcomeViewModel?.response?.removeObserver(welcomeResponseObserver)
        mUpdateOfficerViewModel?.response?.removeObserver(updateOfficerResponseObserver)
        mCitationDatasetModel?.response?.removeObserver(citationDatasetResponseObserver)
        mActivityLogViewModel?.response?.removeObserver(activityLogResponseObserver)
        mCreateTicketViewModel?.response?.removeObserver(createTicketResponseObserver)
        mCreateMunicipalCitationTicketViewModel?.response?.removeObserver(
            createMunicipalCitationTicketResponseObserver
        )
        mTicketCancelViewModel?.response?.removeObserver(ticketCancelResponseObserver)
        mTicketStatusViewModel?.response?.removeObserver(ticketStatusResponseObserver)
        mAddTimingViewModel?.response?.removeObserver(addTimingResponseObserver)
        mUploadImageViewModel?.response?.removeObserver(uploadImageResponseObserver)
        mUploadImageViewModel?.responseTimingImageUpload?.removeObserver(
            uploadTimingImageResponseObserver
        )
        mUploadSignatureImageViewModel?.uploadAllImagesAPIStatus?.observe(
            this,
            uploadScannedImagesAPIResponseObserver
        )
        mDownloadBitmapFIleViewModel?.response?.removeObserver(downloadBitmapResponseObserver)
        mDownloadBitmapFIleViewModel?.responseHeaderFile?.removeObserver(
            downloadHeaderResponseObserver
        )
        mDownloadBitmapFIleViewModel?.responseFooterFile?.removeObserver(
            downloadFooterResponseObserver
        )

        inventoryViewModel?.responseForGetOfficerEquipment?.removeObserver(
            officerEquipmentResponseObserver
        )
        inventoryViewModel?.responseForEquipmentCheckout?.removeObserver(
            equipmentCheckoutResponseObserver
        )
        inventoryViewModel?.responseForEquipmentCheckIn?.removeObserver(
            equipmentCheckInResponseObserver
        )

        mCitationDatasetModel?.responseForEquipmentInventoryAPI?.removeObserver(
            getEquipmentInventoryResponseObserver
        )
    }

    //Call Api For Event Activity Log
    @Throws(ParseException::class)
    private fun callEventActivityLogApi() {
        if (isInternetAvailable(this@WelcomeActivity)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
            if (mWelcomeResponseData!!.data != null && mWelcomeResponseData!!.data!!.size >= 0 && mWelcomeResponseData!!.data!![0].responsedata!!.user != null && mWelcomeResponseData!!.data!![0].responsedata!!.user!!.mApprovedBy != null) {
                activityUpdateRequest.initiatorId =
                    mWelcomeResponseData!!.data!![0].responsedata!!.user!!.mApprovedBy!!.mInitiatorId
            } else {
                activityUpdateRequest.initiatorId = ""
            }
            activityUpdateRequest.activity_id = mActivityId
            activityUpdateRequest.initiatorRole = "DEFAULT_SITE_OFFICER"
            activityUpdateRequest.activityType = "ActivityUpdate"
            activityUpdateRequest.image_1 = ""
            activityUpdateRequest.image_2 = ""
            activityUpdateRequest.image_3 = ""
            if (mImages != null && mImages.size >= 1) {
                activityUpdateRequest.image_1 = mImages!!.get(0)
            }
            if (mImages != null && mImages.size >= 2) {
                activityUpdateRequest.image_2 = mImages!!.get(1)
            }
            if (mImages != null && mImages.size >= 3) {
                activityUpdateRequest.image_3 = mImages!!.get(2)
            }
            activityUpdateRequest.siteId = getSiteId(this@WelcomeActivity)
            activityUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
            activityUpdateRequest.mShift =
                mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerShift
            try {
                //activityUpdateRequest.activityName = mAutoComTextViewActivity!!.text.toString()
                activityUpdateRequest.activityName = mSelectedSActivityStat?.activityKey
            } catch (e: Exception) {
                activityUpdateRequest.activityName = ""
            }
            try {
                activityUpdateRequest.mBlock =
                    if (mAutoComTextViewBlock != null) mAutoComTextViewBlock?.text.toString() else ""
            } catch (e: java.lang.Exception) {
                activityUpdateRequest.mBlock = ""
            }
            try {
                activityUpdateRequest.mDeviceId =
                    if (selectedDeViceId.mDeviceFriendlyName != null) selectedDeViceId.mDeviceFriendlyName.toString() else ""
            } catch (e: java.lang.Exception) {
                activityUpdateRequest.mDeviceId = ""
            }

            try {
                activityUpdateRequest.mStreet =
                    if (mAutoComTextViewStreet != null) mAutoComTextViewStreet?.text.toString() else ""
            } catch (e: java.lang.Exception) {
                activityUpdateRequest.mStreet = ""
            }

            try {
                activityUpdateRequest.mSide =
                    if (mAutoComTextViewSideofStreet != null) mAutoComTextViewSideofStreet?.text.toString() else ""
            } catch (e: java.lang.Exception) {
                activityUpdateRequest.mSide = ""
            }
            try {
                activityUpdateRequest.mSquad =
                    if (mAutoComTextViewSquad != null) mAutoComTextViewSquad?.text.toString() else ""
            } catch (e: java.lang.Exception) {
                activityUpdateRequest.mSquad = ""
            }
            activityUpdateRequest.latitude = mLat
            activityUpdateRequest.longitude = mLong
            activityUpdateRequest.clientTimestamp = splitDateLpr("")

            mActivityLogViewModel?.hitActivityLogApi(activityUpdateRequest)
        } else {
            dismissLoader()
            printToastMSG(applicationContext, getString(R.string.err_msg_connection_was_refused))
        }
    }

    //set login timestamp
    private fun setLoginTimeStamp() {
        /*  val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)

           if (settingsList != null && settingsList!!.size > 0) {
              for (i in settingsList!!.indices) {
                  if (settingsList!![i].type.equals("TIMEZONE",ignoreCase = true)) {
                      try {
                           mZone = settingsList!![i].mValue.toString()

                      } catch (e: java.lang.Exception) {
                          e.printStackTrace()
                      }
                      break
                  }
              }
          }*/

        var last: String? = ""
        var Current: String? = ""
        try {
            last = splitDateWelcome(sharedPreference.read(SharedPrefKey.PRE_TIME, ""), mZone)
            Current = splitDateWelcome(sharedPreference.read(SharedPrefKey.CURRENT_TIME, ""), mZone)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mTextViewPreLogin.text = last
        mTextViewCurLogin.text = Current
        //LogUtil.printToastMSG(mContext, String.valueOf(mResponse.getMetadata().getCurrentLogin()));
    }

    /* //Check if form is uploaded
     private fun isFormUploaded() {
         val mData = mDb?.dbDAO?.getWelcomeForm()
         if (mData != null) {
             if (!mData.uploadStatus!!) {
                 if (isInternetAvailable(this)) {
                     printToastMSG(applicationContext, "Your Offline saved data is Uploded!")
                     callUpdateSiteOfficerApi()
                     insertFormToDb(true, true, mData)
                 } else {
                     insertFormToDb(false, true, mData)
                 }
             }
         }
     }*/

    //set user details
    @Throws(ParseException::class)
    private fun setUserDetails() {
        //final WelcomeResponse mWelcomeResponseData1 = mWelcomeResponseData;
        if (mCitationLayout!!.size > 0) {
            showProgressLoader(getString(R.string.scr_message_please_wait))
            for (iCit in mCitationLayout!!.indices) {
                if (iCit == mCitationLayout!!.size - 1) {
                    linearLayoutEmptyActivity.visibility = View.GONE
                    layButtons.visibility = View.VISIBLE
                    btnDoneActivity.visibility = View.GONE

                    getTimestampFromDb()
                    callWelcomeApi()
                }

                val finalICit: Int = iCit
                if (mCitationLayout!![finalICit].component.equals("login", ignoreCase = true)) {
                    mTextViewLoginDetails.visibility = View.VISIBLE
                    val mHeaderSize = mCitationLayout!![finalICit].fields!!.size
                    if (mHeaderSize == 1) {
                        cardPreLogin.visibility = View.VISIBLE
                        cardCurLogin.visibility = View.GONE
                    }
                    if (mHeaderSize == 2) {
                        cardPreLogin.visibility = View.VISIBLE
                        cardCurLogin.visibility = View.VISIBLE
                    }
                }
                if (mCitationLayout!![finalICit].component.equals("officer", ignoreCase = true)
                    || mCitationLayout!![finalICit].component.equals(
                        "OfficerActivity", ignoreCase = true
                    )
                ) {
                    mTextViewOfficerDetails.visibility = View.VISIBLE
                    val mHeaderSize = mCitationLayout!![finalICit].fields!!.size
                    if (mHeaderSize == 1) {
                        layOfficerId.visibility = View.GONE
                        layBadgeId.visibility = View.VISIBLE
                    } else if (mHeaderSize == 2) {
                        layOfficerId.visibility = View.VISIBLE
                        layBadgeId.visibility = View.VISIBLE
                    } else {
                        layOfficerId.visibility = View.VISIBLE
                        layBadgeId.visibility = View.VISIBLE
                    }
                    for (iOff in mCitationLayout!![finalICit].fields!!.indices) {
                        if (mCitationLayout!![finalICit].fields?.get(iOff)?.name.equals(
                                "squad", true
                            )
                        ) {
                            mCitationLayout!![finalICit].fields?.get(iOff)!!.tag = "dropdown"
                            mAutoComTextViewSquad = ConstructLayoutBuilder.CheckTypeOfField(
                                mCitationLayout!![finalICit].fields?.get(iOff)!!,
                                layOfficer,
                                mCitationLayout!!.get(finalICit).component.nullSafety(),
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewSquad!!)
//                                setFocus(mAutoComTextViewDeviceId)
                        }
                    }
                }
                if (mCitationLayout!![finalICit].component.equals("other", ignoreCase = true)) {
                    layOtherDetails.visibility = View.VISIBLE
                    layMainOtherDetails.visibility = View.VISIBLE
                    for (iOff in mCitationLayout!![finalICit].fields!!.indices) {
                        if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "activity", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewActivity = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layOtherDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )

                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "comments", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewComments = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layOtherDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "notes", ignoreCase = true
                            )
                        ) {
                            mEdittextNote = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layOtherDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                        } else {
                            try {
                                latestLayoutOther++
                                val name: Array<AppCompatAutoCompleteTextView?>? = null
                                name!![latestLayoutOther] = CheckTypeOfField(
                                    mCitationLayout!![finalICit].fields!![iOff],
                                    layOtherDetails,
                                    mCitationLayout!![finalICit].component!!,
                                    mContext
                                )
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
                if (mCitationLayout!![finalICit].component.equals("asset", ignoreCase = true)) {
                    mTextViewAssetDetails.visibility = View.VISIBLE
                    for (iOff in mCitationLayout!![finalICit].fields!!.indices) {
                        if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "supervisor", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewSuper = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewSuper!!)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "beat", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewBeat = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewBeat!!)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "zone", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewZone = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewZone!!)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "city_zone", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewPbcZone = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewPbcZone!!)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "lot", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewLot = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewLot!!)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "radio", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewRadio = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewRadio!!)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "shift", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewShift = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewShift!!)
                            setFocus(mAutoComTextViewShift)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "agency", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewAgency = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewAgency!!)
                            setFocus(mAutoComTextViewAgency)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "signature", ignoreCase = true
                            )
                        ) {
                            mTextViewSignName.visibility = View.VISIBLE
                            //mTextViewSignName.setText(userName);
                            imageViewSignature.visibility = View.VISIBLE
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "deviceid", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewDeviceId = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewDeviceId!!)
                            setFocus(mAutoComTextViewDeviceId)
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "printer_equipment", ignoreCase = true
                            )
                            || mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "printer", ignoreCase = true
                            )
                            || mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "equipment", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewPrinterEquipment = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layAssetDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            Util.setFieldCaps(
                                this@WelcomeActivity,
                                mAutoComTextViewPrinterEquipment!!
                            )
                        } else if (mCitationLayout!![finalICit].fields?.get(iOff)?.name.equals(
                                "squad", true
                            )
                        ) {
                            mAutoComTextViewSquad = ConstructLayoutBuilder.CheckTypeOfField(
                                mCitationLayout!![finalICit].fields?.get(iOff)!!,
                                layAssetDetails,
                                mCitationLayout!!.get(finalICit).component.nullSafety(),
                                mContext
                            )
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewSquad!!)
                        } else {
                            try {
                                mAutoComTextViewExtra = CheckTypeOfField(
                                    mCitationLayout!![finalICit].fields!![iOff],
                                    layAssetDetails,
                                    mCitationLayout!![finalICit].component!!, mContext
                                )
                                setFocus(mAutoComTextViewExtra)
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
                if (mCitationLayout!![finalICit].component.equals(
                        "Location", ignoreCase = true
                    )
                ) {
                    mTextViewLocationDetails.visibility = View.VISIBLE
                    for (iOff in mCitationLayout!![finalICit].fields!!.indices) {
                        if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "block", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewBlock = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layLocationDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            mAutoComTextViewBlock?.isAllCaps = true
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewBlock!!)

                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "zone", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewZone = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layLocationDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                        } else if (mCitationLayout!![finalICit].fields!![iOff].name.equals(
                                "street", ignoreCase = true
                            )
                        ) {
                            mAutoComTextViewStreet = CheckTypeOfField(
                                mCitationLayout!![finalICit].fields!![iOff],
                                layLocationDetails,
                                mCitationLayout!![finalICit].component!!,
                                mContext
                            )
                            mAutoComTextViewStreet?.isAllCaps = true
                            Util.setFieldCaps(this@WelcomeActivity, mAutoComTextViewStreet!!)
                            getGeoAddress()
                        } else if (mCitationLayout?.get(finalICit)?.fields?.get(iOff)?.name?.equals(
                                "side", true
                            ).nullSafety()
                        ) {
                            mAutoComTextViewSideofStreet = CheckTypeOfField(
                                mCitationLayout?.get(finalICit)?.fields?.get(iOff)!!,
                                layLocationDetails,
                                mCitationLayout?.get(finalICit)?.component!!,
                                mContext
                            )
                        } else if (mCitationLayout?.get(finalICit)?.fields?.get(iOff)?.name?.equals(
                                "lot", true
                            ).nullSafety()
                        ) {
                            mAutoComTextViewLot = CheckTypeOfField(
                                mCitationLayout?.get(finalICit)?.fields?.get(iOff)!!,
                                layLocationDetails,
                                mCitationLayout?.get(finalICit)?.component!!,
                                mContext
                            )
                        }
                    }
                }
                //}, 0)
            }
        }
        if (mCitationLayout!!.size == 0) {
            linearLayoutEmptyActivity.visibility = View.VISIBLE
        }
        //user event logging
        //callEventActivityLogApi();
//        if (!isError()) {
//            openErrorDialog();
//        }
    }

    private fun setFocus(mAutoCompleteTextView: AutoCompleteTextView?) {
        if (mAutoCompleteTextView != null) {
            mAutoCompleteTextView.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    layBottomKeyboard.visibility = View.VISIBLE
                    //myNestedScrollView.smoothScrollTo(layBottomKeyboard.left, layBottomKeyboard.top)
                } else {
                    layBottomKeyboard.visibility = View.GONE
                }
            }
        }
    }

    private fun setActivityData() {
        // mTextViewUserName.setText(userName);
        val userName =
            mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerFirstName + " " + mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerLastName
        mTextViewUserName.text = userName
        mTextViewSignName.text = userName
        mTextViewBadgeId.text =
            mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerBadgeId
        mTextViewOfficerId.text =
            splitID(mWelcomeResponseData!!.data!![0].responsedata!!.user!!.siteOfficerId!!)
        //setAllDropdown()

        /*if (!mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerUserName.toString()
                .equals(
                    sharedPreference.read(SharedPrefKey.USER_NAME, ""),
                    ignoreCase = true
                )
        ) {
            showCustomAlertDialog(
                mContext, "Login Alert",
                getString(R.string.error_login), getString(R.string.alt_lbl_OK),
                getString(R.string.scr_btn_cancel), this
            )
        }*/

        showHideInventoryModule()
    }

    private fun showHideScanVehicleStickerModule(isFromApiResponse : Boolean) {
        if (isFromApiResponse){
            setToolbar()
        }

        if (showAndEnableScanVehicleStickerModule) {
            btnScanSticker.showView()
        }else{
            btnScanSticker.hideView()
        }
    }

    private fun showHideDirectedEnforcementModule(isFromApiResponse : Boolean) {
        if (isFromApiResponse){
            setToolbar()
        }
    }

    private fun showHideInventoryModule() {
        if (showAndEnableInventoryModule) {
            getEquipmentInventory(mWelcomeResponseData?.data?.firstOrNull()?.responsedata?.user?.siteOfficerId.nullSafety())
        } else {
            llQrcode.hideView()
        }
    }

    /**
     * Function used to get header footer details from settings file and call download API
     */
    private fun downloadHeaderFooterForFacsimile() {
        if (showAndEnableHeaderFooterInFacsimile && (!FileUtil.checkHeaderImageFileExist() || !FileUtil.checkFooterImageFileExist())) {
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

            val headerUrl = settingsList?.firstOrNull {
                it.type?.trim().equals(
                    SETTINGS_FLAG_HEADER_URL_FOR_FACSIMILE,
                    true
                )
            }?.mValue?.trim().nullSafety()


            val footerUrl = settingsList?.firstOrNull {
                it.type?.trim().equals(
                    SETTINGS_FLAG_FOOTER_URL_FOR_FACSIMILE,
                    true
                )
            }?.mValue?.trim().nullSafety()

            callGetHeaderFooterDownloadLinkAPI(true, headerUrl)
            callGetHeaderFooterDownloadLinkAPI(false, footerUrl)
        }
    }


    /**
     * Function used to call download hedder footer from secure url
     */
    private fun downloadHeaderFooterForFacsimileFromSecureUrl(
        isHeader: Boolean,
        secureImageUrl: String
    ) {
        if (showAndEnableHeaderFooterInFacsimile && (!FileUtil.checkHeaderImageFileExist() || !FileUtil.checkFooterImageFileExist())) {
            mWelcomeViewModel?.downloadHeaderFooterImage(isHeader, secureImageUrl)
        }
    }

    //dialog to enable location service
    private fun openGPSStatusDialog() {
        val dialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
        dialog.setCancelable(true)
        val dialogView = layoutInflater.inflate(R.layout.dialog_gps_alert, null)
        dialog.setContentView(dialogView)
        val mClose: AppCompatImageView = dialogView.findViewById(R.id.img_close)
        val mBtnGo: AppCompatButton = dialogView.findViewById(R.id.btn_continue)
        val mTxtMessage: AppCompatTextView = dialogView.findViewById(R.id.txt_lable)
        mClose.setOnClickListener { view: View? ->
            dialog.dismiss()
            //Login event
            val iSLoginLogged = sharedPreference.read(SharedPrefKey.IS_LOGIN_LOGGED, false)
            if (iSLoginLogged) {
                //user event logging - login
                callPushEventLogin(Constants.FROM_SCREEN_LOGIN, mEventStartTimeStamp)
                callPushEventLogin(Constants.SESSION, mEventStartTimeStamp)
                //sharedPreference.getInstance(getApplicationContext()).write(SharedPrefKey.IS_LOGIN_LOGGED, false);
            }
        }
        mBtnGo.setOnClickListener { v: View? ->
            dialog.dismiss()
            settingsRequest()
            //refresh Activity
            mainScope.launch {
                delay(mDelayMilis.toLong())
                if (isGPSEnabled(mContext)) {
                    startLocationService()
                }
            }
        }
        dialog.show()
    }

    //starting foreground service and registering broadcast for lat long
    private fun startLocationService() {
        startService(Intent(this, BackgroundLocationUpdateService::class.java))
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            myReceiver!!,
            IntentFilter(BackgroundLocationUpdateService.ACTION_BROADCAST)
        )
    }

    //sending GPS request
    fun settingsRequest() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
            googleApiClient?.connect()
        }
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = Constants.INTERVAL
        locationRequest.fastestInterval = Constants.FASTEST_INTERVAL
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true) //this is the key ingredient
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient!!, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult?> {
            override fun onResult(result: LocationSettingsResult) {
                val status = result?.status
                val state = result?.locationSettingsStates
                when (status?.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> printLog(
                        "TAG",
                        "setResultCallback: " + LocationSettingsStatusCodes.SUCCESS
                    )

                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        printLog(
                            "TAG",
                            "setResultCallback: " + LocationSettingsStatusCodes.RESOLUTION_REQUIRED
                        )

                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                this@WelcomeActivity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: SendIntentException) {
                            // Ignore the error.
                        }
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> printLog(
                        "TAG",
                        "setResultCallback: " + LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
                    )
                }

            }
        })
    }

    //save network call details to txt file
    fun networkUsage() {
        // Get running processes
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps = manager.runningAppProcesses
        var mReceived: Long = 0
        var mSent: Long = 0
        try {
            for (runningApp in runningApps) {
                mReceived = TrafficStats.getUidRxBytes(runningApp.uid)
                mSent = TrafficStats.getUidTxBytes(runningApp.uid)
                Log.d(
                    "LOG_TAG", String.format(
                        Locale.getDefault(),
                        "uid: %1d - name: %s: Sent = %1d, Rcvd = %1d",
                        runningApp.uid,
                        runningApp.processName,
                        mSent,
                        mReceived
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //double received = mReceived / (1024);
        // double send = mSent / (1024);
        val total = ((mReceived + mSent) / 1024).toDouble()
        Log.d("mbps", String.format("%.2f", total) + " MB")
        try {
            createFolder(
                "Response Time: " + mResponseTime + "ms, Speed: " + String.format(
                    "%.2f",
                    total
                ) + "kb/s, Timestamp: " + AppUtils.getDateTime()
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //check if gps is enabled
    fun isGPSEnabled(context: Context?): Boolean {
        val lm = context!!.getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        val network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gps_enabled
    }

    /* Call Api For Citation Dataset For Activity page Type */
    private fun callCitationDatasetApiActivityForm(mType: String, index: Long) {
        if (isInternetAvailable(this@WelcomeActivity)) {
            val mDropdownDatasetRequest = DropdownDatasetRequest()
            mDropdownDatasetRequest.type = mType
            mDropdownDatasetRequest.shard = index
            mCitationDatasetModel!!.hitCitationDatasetApi(mDropdownDatasetRequest)
        } else {
            dismissLoader()
            printToastMSG(applicationContext, getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For Citation Dataset For Activity page Type */
    private fun callCitationDatasetApi(mType: String, pageIndex: Long) {
        if (isInternetAvailable(this@WelcomeActivity)) {
            val mDropdownDatasetRequest = DropdownDatasetRequest()
            mDropdownDatasetRequest.type = mType
            mDropdownDatasetRequest.shard = pageIndex
            mCitationDatasetModel!!.hitCitationDatasetApi(mDropdownDatasetRequest)
        } else {
            dismissLoader()
            printToastMSG(applicationContext, getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For Welcome page details */
    private fun callWelcomeApi() {
        mRequestTimeStart = System.currentTimeMillis()
        if (isInternetAvailable(this@WelcomeActivity)) {
            mWelcomeViewModel!!.hitWelcomeApi()
        } else {
            dismissLoader()
            printToastMSG(applicationContext, getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For update site officer */
    private fun callUpdateSiteOfficerApi() {
        showProgressLoader(getString(R.string.scr_message_please_wait))
        val mData = getMyDatabase()?.dbDAO?.getWelcomeForm()
        if (isInternetAvailable(this@WelcomeActivity)) {
            val mUpdateSiteOfficerRequest = UpdateSiteOfficerRequest()
            mUpdateSiteOfficerRequest.siteId = mData!!.siteId
            mUpdateSiteOfficerRequest.siteOfficerId = mData.siteOfficerId
            val mSiteOfficerPackage = UpdatePackage()
            try {
                mSiteOfficerPackage.officerBeat = mSelectedBeatStat!!.beatName
            } catch (e: Exception) {
                mSiteOfficerPackage.officerBeat = ""
            }
            try {
                mSiteOfficerPackage.officerRadio = mSelectedRadioStat!!.radioName
            } catch (e: Exception) {
                mSiteOfficerPackage.officerRadio = ""
            }
            try {
//                mSiteOfficerPackage.setOfficerShift(mSelectedShiftStat.getShiftName());
                mSiteOfficerPackage.officerShift =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            } catch (e: Exception) {
                mSiteOfficerPackage.officerShift = ""
            }
            try {
                mSiteOfficerPackage.officerSupervisor =
                    mSelectedSupervisorStat?.mSuperName.nullSafety()

            } catch (e: Exception) {
                mSiteOfficerPackage.officerSupervisor = ""
            }
            try {
                mSiteOfficerPackage.mOfficerSupervisorBadgeId =
                    if (mSelectedSupervisorStat?.mSuperBadgeId != null
                        && !mSelectedSupervisorStat?.mSuperBadgeId.nullSafety().isEmpty()
                    ) mSelectedSupervisorStat?.mSuperBadgeId.nullSafety().toInt() else 0
            } catch (e: Exception) {
                mSiteOfficerPackage.mOfficerSupervisorBadgeId = 0
            }
            try {
                mSiteOfficerPackage.officerZone = mSelectedZoneStat!!.zoneName
            } catch (e: Exception) {
                mSiteOfficerPackage.officerZone = ""
            }
            try {
                mSiteOfficerPackage.mofficerAgency = mSelectedAgency
            } catch (e: Exception) {
                mSiteOfficerPackage.mofficerAgency = ""
            }
            try {
                mSiteOfficerPackage.mofficerDeviceId = selectedDeViceId
            } catch (e: Exception) {
                mSiteOfficerPackage.mofficerDeviceId = null
            }
            try {
                mSiteOfficerPackage.mCityZone =
                    if (mAutoComTextViewPbcZone != null) mAutoComTextViewPbcZone?.text.toString() else ""
            } catch (e: Exception) {
                mSiteOfficerPackage.mCityZone = null
            }
            try {
                mSiteOfficerPackage.mEquipment =
                    if (mAutoComTextViewPrinterEquipment != null) mAutoComTextViewPrinterEquipment?.text.toString() else ""
            } catch (e: Exception) {
                mSiteOfficerPackage.mEquipment = null
            }

            try {
                mSiteOfficerPackage.mOfficerSquad =
                    if (mAutoComTextViewSquad != null) mAutoComTextViewSquad?.text.toString() else ""
            } catch (e: Exception) {
                mSiteOfficerPackage.mOfficerSquad = null
            }
            try {
                mSiteOfficerPackage.mSignature =
                    if (uploadSignatureLink != null && !uploadSignatureLink!!.isEmpty()) uploadSignatureLink else ""
            } catch (e: Exception) {
                mSiteOfficerPackage.mOfficerSquad = null
            }
            try {
                mSiteOfficerPackage.mLot =
                    if (mAutoComTextViewLot != null) mAutoComTextViewLot?.text.toString() else ""
            } catch (e: Exception) {
                mSiteOfficerPackage.mLot = null
            }


            mUpdateSiteOfficerRequest.updatePackage = mSiteOfficerPackage
//            printLog("mUpdateSiteOfficer", ObjectMapperProvider.instance.writeValueAsString(mUpdateSiteOfficerRequest))
            mUpdateOfficerViewModel?.hitUpdateSiteOfficerApi(mUpdateSiteOfficerRequest)

        } else {
            printToastMSG(applicationContext, getString(R.string.err_msg_connection_was_refused))
        }
    }

    private fun init() {
        showHideScanVehicleStickerModule(isFromApiResponse = false)
        createFolderForLprImages()
        mEventStartTimeStamp = AppUtils.getDateTime()
        //if upload fail
//        isFormUploaded();
        //init receiver for lat long
        myReceiver = getMyReceiver()
        if (!isGPSEnabled(this)) {
            openGPSStatusDialog()
        }
        try {
            ioScope.launch {

                val response = getMyDatabase()?.dbDAO?.getActivityLayout()
                printLog("activity layout", response.toString() + "")
                if (response != null) {
                    if (response.data!![0].response!!.size > 0) {
                        try {
                            mCitationLayout = response.data!![0].response
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                delay(2000)

                mainScope.launch {
                    try {
                        setUserDetails()

                    } catch (e: ParseException) {
                        e.printStackTrace()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // }, 2000)

        //getting signature path
//        mSignaturePath = sharedPreference.read(SharedPrefKey.FILE_PATH, "").toString()
//        val imageName = getSignatureFileNameWithExt()
//        mSignaturePath = mSignaturePath + Constants.CAMERA + "/" + imageName
//        fileSignature = File(mSignaturePath)
        val mydir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Constants.FILE_NAME + Constants.SIGNATURE
        )
        fileSignature = File(mydir.absolutePath, getSignatureFileNameWithExt())
        if (fileSignature!!.exists()) {
            imageViewSignature.setImageURI(Uri.fromFile(fileSignature))
        }
    }

    private fun createFolderForLprImages() {
        val localFolder = File(
            Environment.getExternalStorageDirectory().absolutePath,
            "/ParkLoyalty" + Constants.SCANNER
        )
        if (!localFolder.exists()) {
            localFolder.mkdirs()
        }
        val file = File(localFolder.toString())
        if (!file.exists()) {
            // file.delete(); //you might want to check if delete was successful
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            //if file not exists the download
            //mFilepath = file.getAbsolutePath();
        }

// scan copy images folder
        val localFolderScan = File(
            Environment.getExternalStorageDirectory().absolutePath,
            "/ParkLoyalty" + Constants.LPRSCANIMAGES
        )
        if (!localFolderScan.exists()) {
            localFolderScan.mkdirs()
        }
        val fileScan = File(localFolderScan.toString())
        if (!fileScan.exists()) {
            // file.delete(); //you might want to check if delete was successful
            try {
                fileScan.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            //if file not exists the download
            //mFilepath = file.getAbsolutePath();
        }
    }

    //set value to agency dropdown
    private fun setDropdownAgency(mApplicationList: List<DatasetResponse>?) {
        //init array list
        hideSoftKeyboard(this@WelcomeActivity)
        val mAgency = getMyDatabase()?.dbDAO?.getWelcomeForm()?.agency
        var pos = -1
        if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].agency_name.toString()
                    try {
                        if (!TextUtils.isEmpty(mAgency)) {
                            if (mApplicationList[i].agency_name.equals(
                                    mAgency,
                                    ignoreCase = true
                                )
                            ) {
                                pos = i
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                //            insertFormToDb(true, false, null);
                //  Arrays.sort(mDropdownList);
                mAutoComTextViewAgency?.post {
                    if (pos >= 0) {
                        mAutoComTextViewAgency!!.setText(mDropdownList[pos])
                        mSelectedAgency = mApplicationList[pos].agency_name
                        // KANSAS lock location and filter violation list based on selected agency
                        sharedPreference.write(
                            SharedPrefKey.LOCK_LOCATION_BASED_ON_AGENCY,
                            mApplicationList[pos].mLock!!
                        )

                        if (mApplicationList[pos].mLock != null && mApplicationList[pos].mLock == true) {
                            mAutoComTextViewAgency!!.isClickable = false
                            mAutoComTextViewAgency!!.setOnClickListener(null)
                            mAutoComTextViewAgency!!.dismissDropDown()
                            mAutoComTextViewAgency!!.setDropDownHeight(0)
                            mAutoComTextViewAgency!!.setFocusable(false)
                        }

                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewAgency!!.threshold = 1
                        mAutoComTextViewAgency!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewAgency!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = getIndexOfAgency(
                                    mApplicationList,
                                    parent.getItemAtPosition(position).toString()
                                )

                                mSelectedAgency = mApplicationList[index].agency_name
                                hideSoftKeyboard(this@WelcomeActivity)
                                setFocus(mAutoComTextViewAgency)
                                layBottomKeyboard.visibility = View.GONE
                                // KANSAS lock location and filter violation list based on selected agency
                                sharedPreference.write(
                                    SharedPrefKey.LOCK_LOCATION_BASED_ON_AGENCY,
                                    mApplicationList[index].mLock!!
                                )
                                if (mApplicationList[index].mLock != null && mApplicationList[index].mLock == true) {
                                    mAutoComTextViewAgency!!.isClickable = false
                                    mAutoComTextViewAgency!!.setOnClickListener(null)
                                    mAutoComTextViewAgency!!.dismissDropDown()
                                    mAutoComTextViewAgency!!.setDropDownHeight(0)
                                    mAutoComTextViewAgency!!.setFocusable(false)
                                }
                            }
                        if (mAutoComTextViewAgency?.tag != null && mAutoComTextViewAgency?.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewAgency!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getIndexOfAgency(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.agency_name, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to Device dropdown
    private fun setDropdownDevice(mApplicationList: List<DeviceResponseItem>?) {
        hideSoftKeyboard(this@WelcomeActivity)
        ioScope.launch {
            mCombinationId =
                getMyDatabase()?.dbDAO?.getWelcomeForm()?.officerDeviceName//comment when use esper device id
//            val mDevice = getDeviceId(mContext!!)
            var pos = -1
            var isLockedByAndroidID = false
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].friendlyName.toString()
                    try {
                        //Lock device id based on android id
                        if (mApplicationList[i].androidId != null && mApplicationList[i].androidId!!.equals(
                                mUUID.toString()
                            )
                        ) {
                            mAutoComTextViewDeviceId!!.isClickable = false
                            mAutoComTextViewDeviceId!!.setOnClickListener(null)
                            mAutoComTextViewDeviceId!!.dismissDropDown()
                            mAutoComTextViewDeviceId!!.setDropDownHeight(0)
                            mAutoComTextViewDeviceId!!.setFocusable(false)
                            pos = i
                            isLockedByAndroidID = true
                        } else if (!TextUtils.isEmpty(mCombinationId) && isLockedByAndroidID == false) {
                            if (mApplicationList[i].friendlyName.equals(
                                    mCombinationId,
                                    ignoreCase = true
                                ) ||
                                mApplicationList[i].deviceId.equals(
                                    mCombinationId,
                                    ignoreCase = true
                                )
                            ) {
                                pos = i
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                //            insertFormToDb(true, false, null);
                //  Arrays.sort(mDropdownList);
                mAutoComTextViewDeviceId?.post {
                    if (pos >= 0) {
                        mAutoComTextViewDeviceId!!.setText(mDropdownList[pos])
                        selectedDeViceId.mDeviceFriendlyName = mApplicationList[pos].friendlyName
                        selectedDeViceId.mDeviceId = mApplicationList[pos].friendlyName

                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item, mDropdownList
                    )
                    try {

                        mAutoComTextViewDeviceId!!.threshold = 1
                        mAutoComTextViewDeviceId!!.setAdapter<ArrayAdapter<String?>>(adapter)
                        //                mSelectedAgency = mApplicationList.get(pos).getAgency_name();
                        mAutoComTextViewDeviceId!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = getIndexOfDevice(
                                    mApplicationList,
                                    parent.getItemAtPosition(position).toString()
                                )
                                selectedDeViceId.mDeviceFriendlyName =
                                    mApplicationList[index].friendlyName
                                selectedDeViceId.mDeviceId = mApplicationList[index].friendlyName
                                hideSoftKeyboard(this@WelcomeActivity)
//                                    mAutoComTextViewDeviceId!!.setText(mApplicationList[index].friendlyName)
                                layBottomKeyboard.visibility = View.GONE
                                setFocus(mAutoComTextViewDeviceId)
                                hideSoftKeyboard(this@WelcomeActivity)
                            }

                        if (mAutoComTextViewDeviceId?.tag != null && mAutoComTextViewDeviceId?.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewDeviceId!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getIndexOfDevice(list: List<DeviceResponseItem>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.friendlyName, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to Device dropdown
    private fun setDropdownEquipment(mApplicationList: List<ResponseItem>?) {
        hideSoftKeyboard(this@WelcomeActivity)
        ioScope.launch {
            val mDevice = getMyDatabase()?.dbDAO?.getWelcomeForm()?.equipmentId
            var pos = -1
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].equipmentName.toString()
                    try {
                        if (!TextUtils.isEmpty(mDevice)) {
                            if (mApplicationList[i].equipmentId.equals(
                                    mDevice,
                                    ignoreCase = true
                                )
                            ) {
                                pos = i
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                mAutoComTextViewPrinterEquipment?.post {
                    if (pos >= 0) {
                        mAutoComTextViewPrinterEquipment!!.setText(mDropdownList[pos])
                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewPrinterEquipment!!.threshold = 1
                        mAutoComTextViewPrinterEquipment!!.setAdapter<ArrayAdapter<String?>>(adapter)
                        mAutoComTextViewPrinterEquipment!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                hideSoftKeyboard(
                                    this@WelcomeActivity
                                )
                            }
                        if (mAutoComTextViewPrinterEquipment?.tag != null && mAutoComTextViewPrinterEquipment?.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewPrinterEquipment!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to Device dropdown
    private fun setDropdownSquad(mApplicationList: List<ResponseSquadItem>?) {
        hideSoftKeyboard(this@WelcomeActivity)
        ioScope.launch {
            val mSquad = getMyDatabase()!!.dbDAO!!.getWelcomeForm()!!.officerSquad
            var pos = -1
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = java.lang.String.valueOf(mApplicationList[i].squadName)
                    try {
                        if (!TextUtils.isEmpty(mSquad)) {
                            if (mApplicationList[i].squadName.equals(mSquad, true)) {
                                pos = i

                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                mAutoComTextViewSquad?.post {
                    if (pos >= 0) {
                        mAutoComTextViewSquad?.setText(mDropdownList[pos])
                    }
                    Arrays.sort(mDropdownList)
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewSquad?.threshold = 1
                        mAutoComTextViewSquad?.setAdapter<ArrayAdapter<String?>>(adapter)
                        mAutoComTextViewSquad?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                hideSoftKeyboard(
                                    this@WelcomeActivity
                                )
                            }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet(mApplicationList: List<DatasetResponse>?) {
        val mDropdownList = arrayOfNulls<String>(mApplicationList?.size.nullSafety())
        val pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
//            CoroutineScope(Dispatchers.IO).async {
            ioScope.launch {
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].street_name.toString()
                }
                mAutoComTextViewStreet?.post {
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewStreet?.threshold = 1
                        mAutoComTextViewStreet?.setAdapter<ArrayAdapter<String?>>(adapter)
                        //mSelectedShiftStat = mApplicationList.get(pos);
                        mAutoComTextViewStreet?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id -> //mAutoComTextViewDirection.setText(mApplicationList.get(position).getDirection()); ;
                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        // listonly
                        if (mAutoComTextViewStreet?.tag != null && mAutoComTextViewStreet?.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewStreet!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
        }
    }

    //set value to side dropdown
    private fun setDropdownSide(mApplicationList: List<DatasetResponse>?) {
        val pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].sideName.toString()
                }
                Arrays.sort(mDropdownList)
                mAutoComTextViewSideofStreet?.post {
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewSideofStreet?.threshold = 1
                        mAutoComTextViewSideofStreet?.setAdapter<ArrayAdapter<String?>>(adapter)
                        mAutoComTextViewSideofStreet?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                hideSoftKeyboard(this@WelcomeActivity)
                            }

                        // listonly
                        if (mAutoComTextViewSideofStreet?.tag != null && mAutoComTextViewSideofStreet?.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewSideofStreet!!)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to Street dropdown
    private fun setDropdownBlockList(mApplicationList: List<DatasetResponse>?) {
        val pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].blockName.toString()
                }

                mAutoComTextViewBlock?.post {
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewBlock?.threshold = 1
                        mAutoComTextViewBlock?.setAdapter<ArrayAdapter<String?>>(adapter)
                        //mSelectedShiftStat = mApplicationList.get(pos);
                        mAutoComTextViewBlock?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id -> //mAutoComTextViewDirection.setText(mApplicationList.get(position).getDirection()); ;
                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        // listonly
                        if (mAutoComTextViewBlock?.tag != null && mAutoComTextViewBlock?.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewBlock!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
        }
    }

    //set value to Activity dropdown
    private fun setDropdownActivity(mApplicationList: List<ActivityStat>?) {
        hideSoftKeyboard(this@WelcomeActivity)
        if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                if (mApplicationList.size > 0) {
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].activity.toString()
                    }
                    Arrays.sort(mDropdownList)
                }

                mAutoComTextViewActivity?.post {
                    if (mApplicationList != null && mApplicationList.size > 0) {
                        //mAutoComTextViewActivity.setText(mDropdownList[0]);
                        val adapter = ArrayAdapter(
                            this@WelcomeActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                        )
                        try {
                            mAutoComTextViewActivity?.threshold = 1
                            mAutoComTextViewActivity?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mSelectedSActivityStat = mApplicationList[0]
                            mAutoComTextViewActivity?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    hideSoftKeyboard(this@WelcomeActivity)
                                    val index = getIndexOfActivity(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    mSelectedSActivityStat = mApplicationList[index];
                                    // setDropdownComments();
                                }
                            if (mAutoComTextViewActivity!!.tag != null && mAutoComTextViewActivity!!.tag == "listonly") {
                                setListOnly(this@WelcomeActivity, mAutoComTextViewActivity!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                    }

                }
            }
        } else {

        }
    }

    private fun getIndexOfActivity(list: List<ActivityStat>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.activity, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }


    //set value to Comments dropdown
    private fun setDropdownComments(mApplicationList: List<CommentState>?) {
        //init array list
        hideSoftKeyboard(this@WelcomeActivity)

        //LogUtil.printToastMSG(this,mSelectedSActivityStat.getName());
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            ioScope.launch {
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].name.toString()
                }

                mAutoComTextViewComments?.post {
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewComments?.threshold = 1
                        mAutoComTextViewComments?.setAdapter<ArrayAdapter<String?>>(adapter)
                        mAutoComTextViewComments?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                hideSoftKeyboard(
                                    this@WelcomeActivity
                                )
                            }
                        if (mAutoComTextViewComments?.tag != null && mAutoComTextViewComments?.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewComments!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        } else {
        }
    }

    //set value to Supervisor dropdown
    private fun setDropdownSupervisor(mApplicationList: List<SupervisorStat>?) {
        //init array list
        hideSoftKeyboard(this@WelcomeActivity)
        var pos = -1
        if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    try {
                        //if(String.valueOf(mApplicationList.get(i).getSupervisorName()).equals(""){
                        mDropdownList[i] = mApplicationList[i].mSuperName.toString()
                        if (mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerSupervisor != null) {
                            if (mApplicationList[i].mSuperName.equals(
                                    mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerSupervisor,
                                    ignoreCase = true
                                )
                            ) {
                                pos = i
                            }
                        }
                        // }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

//            Arrays.sort(mDropdownList);
                mAutoComTextViewSuper?.post {
                    if (pos >= 0) {
                        try {
                            mAutoComTextViewSuper!!.setText("" + mDropdownList[pos])
                            mSelectedSupervisorStat = mApplicationList[pos]
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewSuper!!.threshold = 1
                        mAutoComTextViewSuper!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewSuper!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = mApplicationList.indexOfFirst {
                                    it.mSuperName == mAutoComTextViewSuper?.text.toString()
                                }
                                mSelectedSupervisorStat = mApplicationList[index]
                                //                        mSelectedSupervisorStat = mApplicationList.get(position);
                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        if (mAutoComTextViewSuper!!.tag != null && mAutoComTextViewSuper!!.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewSuper!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to beat dropdown
    private fun setDropdownBeat(mApplicationList: List<BeatStat>?) {
        //init array list
        hideSoftKeyboard(this@WelcomeActivity)

        // = mWelcomeResponseData.getData().get(0).getResponsedata().getMetadata().getBeatStats();
        var pos = -1
        if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val setBeatFieldEmptyAfterEveryLogin = getBeatSetEmptyFromSetting()
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].beatName.toString()
                    try {
                        if (setBeatFieldEmptyAfterEveryLogin && sharedPreference.read(
                                SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API, true
                            )
                        ) {
                            pos = -1
                        } else {
                            if (mApplicationList[i].beatName.equals(
                                    mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerBeat,
                                    ignoreCase = true
                                )
                            ) {
                                pos = i
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                //  Arrays.sort(mDropdownList);
                mAutoComTextViewBeat?.post {
                    if (pos >= 0) {
                        try {
                            mAutoComTextViewBeat!!.setText("" + mDropdownList[pos])
                            mSelectedBeatStat = mApplicationList[pos]
                        } catch (e: Exception) {
                        }
                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item, mDropdownList
                    )
                    try {
                        mAutoComTextViewBeat!!.threshold = 1
                        mAutoComTextViewBeat!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewBeat!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = mApplicationList.indexOfFirst {
                                    it.beatName == mAutoComTextViewBeat?.text.toString()
                                }

                                mSelectedBeatStat = mApplicationList[index]

                                if (mWelcomeListDataSet?.welcomeList?.zoneStats != null) {
                                    for (i in mWelcomeListDataSet?.welcomeList?.zoneStats!!.indices) {
                                        if (mApplicationList[index].zone != null && mApplicationList[index].zone!!.trim()
                                                .equals(
                                                    mWelcomeListDataSet?.welcomeList?.zoneStats!![i].mCityZoneName,
                                                    ignoreCase = true
                                                )
                                        ) {
                                            mWelcomeListDataSet?.welcomeList?.zoneStats!![i].zoneName?.let {
                                                setDropdownZone(
                                                    mWelcomeListDataSet?.welcomeList?.zoneStats,
                                                    it
                                                )
                                            }
                                        }
                                    }
                                }

                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        if (mAutoComTextViewBeat!!.tag != null && mAutoComTextViewBeat!!.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewBeat!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to zone dropdown
    private fun setDropdownZone(mApplicationList: List<ZoneStat>?, value: String) {
        //init array list
        hideSoftKeyboard(this@WelcomeActivity)
        // = mWelcomeResponseData.getData().get(0).getResponsedata().getMetadata().getZoneStats();
        var pos = -1
        if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].zoneName.toString()
                    try {
                        if (value.isNotEmpty() && mApplicationList[i].zoneName.equals(
                                value,
                                ignoreCase = true
                            )
                        ) {
                            pos = i
                        } else if (value.isEmpty() && mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerZone != null) {
                            if (mApplicationList[i].zoneName.equals(
                                    mWelcomeResponseData!!.data!![0]
                                        .responsedata!!.user!!.officerZone, ignoreCase = true
                                )
                            ) {
                                pos = i

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                //insertFormToDb(true, false, null);
//                Arrays.sort(mDropdownList)
                mAutoComTextViewZone?.post {
                    if (pos >= 0) {
                        try {
                            mAutoComTextViewZone!!.setText(mDropdownList[pos])
                            mSelectedZoneStat = mApplicationList[pos]
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item, mDropdownList
                    )

                    try {
                        mAutoComTextViewZone!!.threshold = 1
                        mAutoComTextViewZone!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewZone!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = mApplicationList.indexOfFirst {
                                    it.zoneName == mAutoComTextViewZone?.text.toString()
                                }
                                mSelectedZoneStat = mApplicationList[index]
                                if (BuildConfig.FLAVOR.equals(
                                        Constants.FLAVOR_TYPE_CARTA,
                                        ignoreCase = true
                                    )
                                ) {
                                    mSelectedAgency = "CPA " + mApplicationList[index].zoneName
                                    mAutoComTextViewAgency!!.setText(mSelectedAgency)
                                }
                                //insertFormToDb(true, false, null);
                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        if (mAutoComTextViewZone!!.tag != null && mAutoComTextViewZone!!.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewZone!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to zone dropdown
    private fun setDropdownPBCZone(mApplicationList: List<ZoneStat>?) {
        //init array list
        hideSoftKeyboard(this@WelcomeActivity)
        // = mWelcomeResponseData.getData().get(0).getResponsedata().getMetadata().getZoneStats();
        var pos = -1
        if (mApplicationList != null && mApplicationList.size > 0 && mAutoComTextViewPbcZone != null) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].mCityZoneName.toString()
                    try {
                        if (mWelcomeResponseData!!.data!![0].responsedata!!.user!!.mCityZone != null) {
                            if (mApplicationList[i].zoneName.equals(
                                    mWelcomeResponseData!!.data!![0].responsedata!!.user!!.mCityZone,
                                    ignoreCase = true
                                )
                            ) {
                                pos = i

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                //insertFormToDb(true, false, null);
                Arrays.sort(mDropdownList)
                mAutoComTextViewPbcZone?.post {
                    if (pos >= 0) {
                        try {
                            mAutoComTextViewPbcZone!!.setText(mDropdownList[pos])
                            mSelectedZoneStat = mApplicationList[pos]
                        } catch (e: Exception) {
                        }
                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewPbcZone!!.threshold = 1
                        mAutoComTextViewPbcZone!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewPbcZone!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = mApplicationList.indexOfFirst {
                                    it.mCityZoneName == mAutoComTextViewPbcZone?.text.toString()
                                }
                                mSelectedZoneStat = mApplicationList[index]
                                //insertFormToDb(true, false, null);
                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        if (mAutoComTextViewPbcZone!!.tag != null && mAutoComTextViewPbcZone!!.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewPbcZone!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to radio dropdown
    private fun setDropdownRadio(mApplicationList: List<RadioSt>?) {
        //init array list
        hideSoftKeyboard(this@WelcomeActivity)

        // = mWelcomeResponseData.getData().get(0).getResponsedata().getMetadata().getRadioStats();
        var pos = -1
        if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].radioName.toString()
                    try {
                        if (mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerRadio != null) {
                            if (mApplicationList[i].radioName.equals(
                                    mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerRadio,
                                    ignoreCase = true
                                )
                            ) {
                                pos = i

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // Arrays.sort(mDropdownList);
                mAutoComTextViewRadio?.post {
                    if (pos >= 0) {
                        try {
                            mAutoComTextViewRadio!!.setText(mDropdownList[pos])
                            mSelectedRadioStat = mApplicationList[pos]
                        } catch (e: Exception) {
                        }
                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewRadio!!.threshold = 1
                        mAutoComTextViewRadio!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewRadio!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = mApplicationList.indexOfFirst {
                                    it.radioName == mAutoComTextViewRadio?.text.toString()
                                }
                                mSelectedRadioStat = mApplicationList[index]
                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        if (mAutoComTextViewRadio!!.tag != null && mAutoComTextViewRadio!!.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewRadio!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to shift dropdown
    private fun setDropdownShift(mApplicationList: List<ShiftStat>?) {
        //init array list
        hideSoftKeyboard(this@WelcomeActivity)
        var pos = -1
        if (mApplicationList != null && mApplicationList.size > 0 && mAutoComTextViewShift != null) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].shiftName.toString()
                    try {
                        if (mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerShift != null) {
                            if (mApplicationList[i].shiftName.equals(
                                    mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerShift,
                                    ignoreCase = true
                                )
                            ) {
                                pos = i

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // Arrays.sort(mDropdownList);
                mAutoComTextViewShift?.post {
                    if (pos >= 0) {
                        try {
                            mAutoComTextViewShift!!.setText(mDropdownList[pos])
                            sharedPreference.write(
                                SharedPrefKey.LOGIN_SHIFT,
                                mAutoComTextViewShift!!.text.toString()
                            )
                            mSelectedShiftStat = mApplicationList[pos]
                        } catch (e: Exception) {
                        }
                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewShift!!.threshold = 1
                        mAutoComTextViewShift!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewShift!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = mApplicationList.indexOfFirst {
                                    it.shiftName == mAutoComTextViewShift?.text.toString()
                                }
                                mSelectedShiftStat = mApplicationList[index]
                                hideSoftKeyboard(this@WelcomeActivity)
                                sharedPreference.write(
                                    SharedPrefKey.LOGIN_SHIFT,
                                    mAutoComTextViewShift!!.text.toString()
                                )
                                setFocus(mAutoComTextViewShift)
                                layBottomKeyboard.visibility = View.GONE
                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        if (mAutoComTextViewShift!!.tag != null && mAutoComTextViewShift!!.tag == "listonly") {
                            setListOnly(this@WelcomeActivity, mAutoComTextViewShift!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun setDropdownLot() {
        //init array list
//        hideSoftKeyboard(this@WelcomeActivity)
        // = mWelcomeResponseData.getData().get(0).getResponsedata().getMetadata().getZoneStats();
        var pos = -1

        ioScope.launch {
            val mApplicationList = Singleton.getDataSetList(DATASET_LOT_LIST, getMyDatabase())
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].location.toString()
                    try {
                        if (mWelcomeResponseData!!.data!![0].responsedata!!.user!!.mLot!!.isNotEmpty()
                            && mApplicationList[i].location.equals(
                                mWelcomeResponseData!!.data!![0].responsedata!!.user!!.mLot!!,
                                ignoreCase = true
                            )
                        ) {
                            pos = i
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                //insertFormToDb(true, false, null);
//                Arrays.sort(mDropdownList)
                mAutoComTextViewLot?.post {
                    if (pos >= 0) {
                        try {
                            mAutoComTextViewLot!!.setText(mDropdownList[pos])
//                            mLotItem = mApplicationList[pos].lot.toString()

                        } catch (e: Exception) {
                        }
                    }
                    val adapter = ArrayAdapter(
                        this@WelcomeActivity,
                        R.layout.row_dropdown_menu_popup_item, mDropdownList
                    )

                    try {
                        mAutoComTextViewLot!!.threshold = 1
                        mAutoComTextViewLot!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewLot!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index = getIndexOfLocation(
                                    mApplicationList,
                                    parent.getItemAtPosition(position).toString()
                                )
//                                mLotItem = mApplicationList[index].lot.toString()
                                hideSoftKeyboard(this@WelcomeActivity)
                            }
                        if (mAutoComTextViewLot!!.tag != null && mAutoComTextViewLot!!.tag == "listonly") {
                            AppUtils.setListOnly(this@WelcomeActivity, mAutoComTextViewLot!!)

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getIndexOfLocation(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.location, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    private fun removeFocus() {
        try {
            if (mAutoComTextViewShift != null) {
                mAutoComTextViewShift!!.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewBeat != null) {
                mAutoComTextViewBeat!!.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewSuper != null) {
                mAutoComTextViewSuper!!.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewAgency != null) {
                mAutoComTextViewAgency!!.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewPrinterEquipment != null) {
                mAutoComTextViewPrinterEquipment!!.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewDeviceId != null) {
                mAutoComTextViewDeviceId!!.clearFocus()
            }
        } catch (e: Exception) {
        }
    }

    private fun isFormValid(mValue: String): Boolean {
        try {
            if (mCitationLayout != null) {
                for (iComp in mCitationLayout!!.indices) {
                    if (mCitationLayout!![iComp].component.equals("asset", ignoreCase = true)) {
                        for (iOff in mCitationLayout!![iComp].fields!!.indices) {
                            if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "shift", ignoreCase = true
                                )
                            ) {

                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewShift!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        // mAutoComTextViewShift.setError(getString(R.string.val_msg_please_enter_shift));
                                        mAutoComTextViewShift!!.requestFocus()
                                        mAutoComTextViewShift!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_shift)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "beat", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewBeat!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mAutoComTextViewBeat!!.requestFocus()
                                        mAutoComTextViewBeat!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_beat)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "zone", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewZone!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mAutoComTextViewZone!!.requestFocus()
                                        mAutoComTextViewZone!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_zone)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "city_zone", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewPbcZone!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mAutoComTextViewPbcZone!!.requestFocus()
                                        mAutoComTextViewPbcZone!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_zone)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "radio", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewRadio!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mAutoComTextViewRadio!!.requestFocus()
                                        mAutoComTextViewRadio!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_radio)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "agency", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewAgency!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mAutoComTextViewAgency!!.requestFocus()
                                        mAutoComTextViewAgency!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            mContext,
                                            getString(R.string.val_msg_please_enter_agency)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "supervisor", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewSuper!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewSuper.setError(getString(R.string.val_msg_please_enter_supervisor));
                                        mAutoComTextViewSuper!!.requestFocus()
                                        mAutoComTextViewSuper!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            mContext,
                                            getString(R.string.val_msg_please_enter_supervisor)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "device_id", ignoreCase = true
                                ) ||
                                mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "deviceid", ignoreCase = true
                                )
                            ) {
                                try {
                                    if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                        if (TextUtils.isEmpty(
                                                mAutoComTextViewDeviceId!!.text.toString()
                                                    .trim()
                                            )
                                        ) {
                                            //mAutoComTextViewSuper.setError(getString(R.string.val_msg_please_enter_supervisor));
                                            mAutoComTextViewDeviceId!!.requestFocus()
                                            mAutoComTextViewDeviceId!!.isFocusable = true
                                            showKeyboard(this@WelcomeActivity)
                                            printToastMSG(
                                                mContext,
                                                getString(R.string.val_msg_please_enter_device_id)
                                            )
                                            return false
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "signature", ignoreCase = true
                                )
                            ) {
                                try {
                                    if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                        if (!fileSignature!!.exists()) {
                                            //mAutoComTextViewSuper.setError(getString(R.string.val_msg_please_enter_supervisor));
                                            printToastMSG(
                                                mContext,
                                                getString(R.string.scr_wrn_signature_must)
                                            )
                                            return false
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
//                            LogUtil.printToastMSG(this, "Please add your Signature!");
//                            return false;
//                        }
                        }
                    }
                    if (mCitationLayout!![iComp].component.equals("Location", ignoreCase = true)) {
                        for (iOff in mCitationLayout!![iComp].fields!!.indices) {
                            if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "block", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewBlock!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        // mAutoComTextViewShift.setError(getString(R.string.val_msg_please_enter_shift));
                                        mAutoComTextViewBlock!!.requestFocus()
                                        mAutoComTextViewBlock!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            mContext,
                                            getString(R.string.val_msg_please_enter_block)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "zone", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewZone!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mAutoComTextViewZone!!.requestFocus()
                                        mAutoComTextViewZone!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            mContext,
                                            getString(R.string.val_msg_please_enter_zone)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "street", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewStreet!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mAutoComTextViewStreet!!.requestFocus()
                                        mAutoComTextViewStreet!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            mContext,
                                            getString(R.string.val_msg_please_enter_street)
                                        )
                                        return false
                                    }
                                }
                            }
                        }
                    }
                    if (mCitationLayout!![iComp].component.equals("other", ignoreCase = true)) {
                        for (iOff in mCitationLayout!![iComp].fields!!.indices) {
                            if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "activity", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewActivity!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        // mAutoComTextViewShift.setError(getString(R.string.val_msg_please_enter_shift));
                                        mAutoComTextViewActivity!!.requestFocus()
                                        mAutoComTextViewActivity!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            mContext,
                                            getString(R.string.val_msg_please_enter_activity)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "comments", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewComments!!.text.toString()
                                                .trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mAutoComTextViewComments!!.requestFocus()
                                        mAutoComTextViewComments!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            mContext,
                                            getString(R.string.val_msg_please_enter_comments)
                                        )
                                        return false
                                    }
                                }
                            } else if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "notes", ignoreCase = true
                                )
                            ) {
                                if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                    if (TextUtils.isEmpty(
                                            mEdittextNote!!.text.toString().trim()
                                        )
                                    ) {
                                        //mAutoComTextViewBeat.setError(getString(R.string.val_msg_please_enter_beat));
                                        mEdittextNote!!.requestFocus()
                                        mEdittextNote!!.isFocusable = true
                                        showKeyboard(this@WelcomeActivity)
                                        printToastMSG(
                                            mContext,
                                            getString(R.string.val_msg_please_enter_note)
                                        )
                                        return false
                                    }
                                }
                            }
                        }
                    }
                    if (mCitationLayout!![iComp].component.equals("Officer", ignoreCase = true)) {
                        for (iOff in mCitationLayout!![iComp].fields!!.indices) {
                            if (mCitationLayout!![iComp].fields!![iOff].name.equals(
                                    "squad", ignoreCase = true
                                )
                            ) {
                                try {
                                    if (mCitationLayout!![iComp].fields!![iOff].isRequired.nullSafety()) {
                                        if (TextUtils.isEmpty(
                                                mAutoComTextViewSquad!!.text.toString()
                                                    .trim()
                                            )
                                        ) {
                                            //mAutoComTextViewSuper.setError(getString(R.string.val_msg_please_enter_supervisor));
                                            mAutoComTextViewSquad!!.requestFocus()
                                            mAutoComTextViewSquad!!.isFocusable = true
                                            showKeyboard(this@WelcomeActivity)
                                            printToastMSG(
                                                mContext,
                                                getString(R.string.val_msg_please_enter_squad)
                                            )
                                            return false
                                        }
                                    }
                                } catch (e: Exception) {
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
    }//Todo change hardcoded value//get form data from Db

    //check if details are changed
    private fun isDetailsChanged(): Boolean {
        //get form data from Db
        val mWelcomeData = getMyDatabase()?.dbDAO?.getWelcomeForm()
        if (mWelcomeData != null) {
            if (mWelcomeResponseData != null) {
                return try {
                    if (mSelectedZoneStat != null && !mSelectedZoneStat!!.m_id
                            .equals(mWelcomeData.officerZone, ignoreCase = true)
                    ) {
                        false
                        //Todo change hardcoded value
                    } else if (mSelectedSupervisorStat != null && !mSelectedSupervisorStat!!.m_id
                            .equals(mWelcomeData.officerSupervisor, ignoreCase = true)
                    ) {
                        false
                    } else if (mSelectedBeatStat != null && !mSelectedBeatStat!!.m_id
                            .equals(mWelcomeData.officerBeat, ignoreCase = true)
                    ) {
                        false
                    } else if (mSelectedShiftStat != null && !mSelectedShiftStat!!.m_id
                            .equals(mWelcomeData.officerShift, ignoreCase = true)
                    ) {
                        false
                    } else if (mSelectedAgency != null && !mSelectedAgency!!
                            .equals(mWelcomeData.agency, ignoreCase = true)
                    ) {
                        false
                    } else if (mCombinationId != null) {
                        false
                    } else mSelectedRadioStat == null || mSelectedRadioStat!!.id
                        .equals(mWelcomeData.officerRadio, ignoreCase = true)
                } catch (e: Exception) {
                    true
                }
            }
        }
        return true
    }

    //dialog - error
    private fun openErrorDialog() {
        val dialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
        dialog.setCancelable(true)
        val dialogView = layoutInflater.inflate(R.layout.dialog_gps_alert, null)
        dialog.setContentView(dialogView)
        val mClose: AppCompatImageView = dialogView.findViewById(R.id.img_close)
        val mBtnGo: AppCompatButton = dialogView.findViewById(R.id.btn_continue)
        val mTxtMessage: AppCompatTextView = dialogView.findViewById(R.id.txt_lable)
        val mTxtAlert: AppCompatImageView = dialogView.findViewById(R.id.iv_alert)
        val mTxtMessageSec: AppCompatTextView = dialogView.findViewById(R.id.txt_lable_sec)
        mTxtAlert.visibility = View.GONE
        mTxtMessage.visibility = View.GONE
        mTxtMessageSec.visibility = View.VISIBLE
        mTxtMessageSec.text = getString(R.string.err_msg_error)
        mBtnGo.text = "Re-Login"
        mClose.visibility = View.GONE
        mClose.setOnClickListener { view: View? -> dialog.dismiss() }
        mBtnGo.setOnClickListener { v: View? ->
            dialog.dismiss()
            logout(mContext!!)
        }
        dialog.show()
    }//Todo change hardcoded value//get form data from Db

    //check if error
    private fun isError(): Boolean {
        return try {
            //get form data from Db
            if (mSelectedZoneStat?.m_id == null) {
                false
                //Todo change hardcoded value
            } else if (mSelectedSupervisorStat == null) {
                false
            } else if (mSelectedBeatStat == null) {
                false
            } else if (mSelectedShiftStat == null) {
                false
            } else if (mSelectedRadioStat == null) {
                false
            } else if (mSelectedSActivityStat == null) {
                false
            } else if (mTextViewUserName.text.toString() == null || mTextViewUserName.text.toString() == "") {
                false
            } else if (mTextViewCurLogin.text.toString() == null || mTextViewCurLogin.text.toString() == "") {
                false
            } else mTextViewBadgeId.text.toString() != null && mTextViewBadgeId.text.toString() != ""
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }


    //save Activity log data to database - It contains offline and online conditions
    private fun insertFormToDb(uploadStatus: Boolean, isReUpload: Boolean, mForm: WelcomeForm?) {
        ioScope.launch {
            var mWelcomeForm: WelcomeForm? = WelcomeForm()
            if (isReUpload) {
                mWelcomeForm = mForm
                mWelcomeForm!!.uploadStatus = uploadStatus
            } else {
                val mData = mWelcomeResponseData!!.data!![0].responsedata!!.user
                mWelcomeForm!!.enable = mData!!.enable
                mWelcomeForm.siteOfficerId = mData.siteOfficerId
                mWelcomeForm.officerBadgeId = mData.officerBadgeId
                mWelcomeForm.enable = mData.enable
                mWelcomeForm.id = mData.m_id.nullSafety()
                if (mData.officerBeat != null) {
                    mWelcomeForm.officerBeat = mData.officerBeat
                }
                try {
                    mWelcomeForm.officerBeatName =
                        mAutoComTextViewBeat!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.officerBeatName = ""
                }
                try {
                    mWelcomeForm.officerZoneName =
                        mAutoComTextViewZone!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.officerZoneName = ""
                }
                try {
                    mWelcomeForm.cityZoneName =
                        mAutoComTextViewPbcZone!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.cityZoneName = ""
                }
                try {
                    mWelcomeForm.officerRadio =
                        mAutoComTextViewRadio!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.officerRadio = ""
                }
                try {
                    mWelcomeForm.officerSupervisor =
                        mAutoComTextViewSuper?.editableText.toString().trim()
                    mWelcomeForm.mOfficerSuperVisorBadgeId = mSelectedSupervisorStat?.mSuperBadgeId
                } catch (e: Exception) {
                    mWelcomeForm.officerSupervisor =
                        mAutoComTextViewSuper?.editableText.toString().trim()
                }
                try {
                    mWelcomeForm.officerShift =
                        mAutoComTextViewShift!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.officerShift = ""
                }
                try {
                    mWelcomeForm.officerZone =
                        mAutoComTextViewZone!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.officerZone = ""
                }
                try {
                    mWelcomeForm.cityZoneName =
                        mAutoComTextViewPbcZone!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.cityZoneName = ""
                }
                try {
                    mWelcomeForm.radio =
                        mAutoComTextViewRadio!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.radio = ""
                }
                try {
                    mWelcomeForm.cityZoneNameCode = mSelectedZoneStat!!.mCityZoneName
                } catch (e: Exception) {
                    mWelcomeForm.cityZoneNameCode = ""
                }
                try {
                    mWelcomeForm.officerSquad =
                        mAutoComTextViewSquad!!.editableText.toString().trim()
//                    mWelcomeForm.officerSquad = mData.officerSquad
                } catch (e: Exception) {
                    mWelcomeForm.officerSquad = ""
                }
                mWelcomeForm.officerFirstName = mData.officerFirstName
                mWelcomeForm.officerMiddleName = mData.officerMiddleName
                mWelcomeForm.officerLastName = mData.officerLastName
                mWelcomeForm.role = mData.role
                try {
                    mWelcomeForm.agency =
                        mAutoComTextViewAgency!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.agency = ""
                }
                try {
                    mWelcomeForm.officerDeviceId = selectedDeViceId.mDeviceId
                    mWelcomeForm.officerDeviceName = selectedDeViceId.mDeviceFriendlyName
                } catch (e: Exception) {
                    mWelcomeForm.officerDeviceId = ""
                }
                try {
                    mWelcomeForm.shift =
                        mAutoComTextViewShift!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.shift = ""
                }
                try {
                    mWelcomeForm.equipmentId =
                        mAutoComTextViewPrinterEquipment!!.editableText.toString().trim()
                } catch (e: Exception) {
                    mWelcomeForm.equipmentId = ""
                }
                try {
                    if (mData!!.mLot != null && mData!!.mLot!!.isNotEmpty()) {
                        mWelcomeForm.lot = mData!!.mLot
                    } else {
                        mWelcomeForm.lot =
                            mAutoComTextViewLot!!.editableText.toString().trim()
                    }
                } catch (e: Exception) {
                    mWelcomeForm.lot = ""
                }
//                mWelcomeForm.officerSquad = mData.officerSquad
                mWelcomeForm.officerSuperviserId = mData.officerSuperviserId
                mWelcomeForm.siteId = mData.siteId
                mWelcomeForm.uploadStatus = uploadStatus
            }
            try {
                getMyDatabase()!!.dbDAO!!.insertWelcomeForm(mWelcomeForm)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //save Activity log data to database - It contains offline and online conditions
    private fun insertFormToDbByAPIResponse(mForm: WelcomeUser?) {
        CoroutineScope(Dispatchers.IO).async {
            val mWelcomeForm = WelcomeForm()
            val mData = mForm!!
            mWelcomeForm.enable = mData!!.enable
            mWelcomeForm.siteOfficerId = mData.siteOfficerId
            mWelcomeForm.officerBadgeId = mData.officerBadgeId
            mWelcomeForm.enable = mData.enable
            mWelcomeForm.id = mData.m_id.nullSafety()
            if (mData.officerBeat != null) {
                mWelcomeForm.officerBeat = mData.officerBeat
            }
            try {
                mWelcomeForm.officerBeatName = mData.officerBeat
            } catch (e: Exception) {
                mWelcomeForm.officerBeatName = ""
            }
            try {
                mWelcomeForm.officerZoneName = mData.officerZone
            } catch (e: Exception) {
                mWelcomeForm.officerZoneName = ""
            }
            try {
                mWelcomeForm.officerRadio = mData.officerRadio
            } catch (e: Exception) {
                mWelcomeForm.officerRadio = ""
            }
            try {
                mWelcomeForm.officerSupervisor = mData.officerSupervisor
            } catch (e: Exception) {
                mWelcomeForm.officerSupervisor = ""
            }
            try {
                mWelcomeForm.officerShift = mData.officerShift
            } catch (e: Exception) {
                mWelcomeForm.officerShift = ""
            }
            try {
                mWelcomeForm.officerZone = mData.officerZone
            } catch (e: Exception) {
                mWelcomeForm.officerZone = ""
            }
            try {
                mWelcomeForm.radio = mData.officerRadio
            } catch (e: Exception) {
                mWelcomeForm.radio = ""
            }
            try {
                mWelcomeForm.officerDeviceId = mData.mOfficerDeviceId?.mDeviceId
                mWelcomeForm.officerDeviceName = mData.mOfficerDeviceId?.mDeviceFriendlyName
            } catch (e: Exception) {
                mWelcomeForm.officerDeviceId = ""
            }
            try {
                mWelcomeForm.equipmentId = mData.mEquipment
            } catch (e: Exception) {
                mWelcomeForm.equipmentId = ""
            }
            try {
                mWelcomeForm.officerSquad = mData.officerSquad
            } catch (e: java.lang.Exception) {
                mWelcomeForm.officerSquad = ""
            }
            mWelcomeForm.officerFirstName = mData.officerFirstName
            mWelcomeForm.officerMiddleName = mData.officerMiddleName
            mWelcomeForm.officerLastName = mData.officerLastName
            mWelcomeForm.role = mData.role
            mWelcomeForm.cityZoneName = mData.mCityZone
            try {
                mWelcomeForm.agency = mData.mOfficerAgency
            } catch (e: Exception) {
                mWelcomeForm.agency = ""
            }

            try {
                mWelcomeForm.officer_lookup_code = mData.officer_lookup_code
            } catch (e: Exception) {
                mWelcomeForm.officer_lookup_code = ""
            }
            try {
                mWelcomeForm.shift = mData.shift
            } catch (e: Exception) {
                mWelcomeForm.shift = ""
            }
            try {
                mWelcomeForm.lot = mData.mLot
            } catch (e: Exception) {
                mWelcomeForm.lot = ""
            }
            mWelcomeForm.officerSquad = mData.officerSquad
            mWelcomeForm.officerSuperviserId = mData.officerSuperviserId
            mWelcomeForm.siteId = mData.siteId
            mWelcomeForm.uploadStatus = true
            mWelcomeForm.initiatorId =
                if (mData.mApprovedBy != null && mData.mApprovedBy?.mInitiatorId != null) mData.mApprovedBy!!.mInitiatorId else ""
            mWelcomeForm.initiatorRole = if (mData.mApprovedBy != null && mData.mApprovedBy!!
                    .mInitiator_role != null
            ) mData.mApprovedBy?.mInitiator_role else ""
            try {
                getMyDatabase()?.dbDAO?.insertWelcomeForm(mWelcomeForm)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    fun backupDatabase() {
        //Open your local db as the input stream
        val currentDBPath = getDatabasePath("park_loyalty").absolutePath
        val dbFile = File(currentDBPath)
        if (dbFile != null) {
            val fis = FileInputStream(dbFile)
            val outFileName = Environment.getExternalStorageDirectory()
                .toString() + "/" + Constants.FILE_NAME + "/database.db"
            //Open the empty db as the output stream
            val output: OutputStream = FileOutputStream(outFileName)
            //transfer bytes from the inputfile to the outputfile
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
            //Close the streams
            output.flush()
            output.close()
            fis.close()
        } else {
            printToastMSG(this, "failed to save db!")
        }
    }

    //creating a folder and saving txt file with network data
    @Throws(IOException::class)
    private fun createFolder(mbps: String) {
        val localFolder =
            File(Environment.getExternalStorageDirectory().absolutePath, Constants.FILE_NAME)
        if (!localFolder.exists()) {
            localFolder.mkdirs()
        }
        /* SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();*/
        val fileName = "network_usage" + ".txt" //like 2016_01_12.txt
        val file = File(localFolder, fileName)
        val writer = FileWriter(file, true)
        writer.append(mbps).append("\n\n")
        writer.flush()
        writer.close()
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
        //if file not exists the download
        val mFilepath = file.absolutePath
        sharedPreference.write(SharedPrefKey.FILE_PATH, localFolder.absolutePath)
        printLog("filename", mFilepath)
    }

    //creating a folder and saving txt file with network data
    @Throws(IOException::class)
    private fun createCredFolder() {
        val mPass = sharedPreference.read(SharedPrefKey.PASSWORD_DB, "")
        val localFolder =
            File(Environment.getExternalStorageDirectory().absolutePath, Constants.FILE_NAME)
        if (!localFolder.exists()) {
            localFolder.mkdirs()
        }
        /* SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();*/
        val fileName = "credentials" + ".txt" //like 2016_01_12.txt
        val file = File(localFolder, fileName)
        val writer = FileWriter(file, false)
        writer.append("Password :- " + "park_loyalty")
        writer.flush()
        writer.close()
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
        //if file not exists the download
        val mFilepath = localFolder.absolutePath
        sharedPreference.write(SharedPrefKey.FILE_PATH, mFilepath)
        //LogUtil.printLog("filename",mFilepath);
    }

    private fun btnSaveClick() {
        //set dropdown values
        val mWelcomeRole = mWelcomeResponseData!!.data!![0].responsedata!!.user
        try {
            mWelcomeRole!!.officerZone = mSelectedZoneStat!!.zoneName
        } catch (e: Exception) {
            mWelcomeRole!!.officerZone = ""
        }
        try {
            mWelcomeRole!!.officerSupervisor =
                mSelectedSupervisorStat?.mSuperName.nullSafety()
        } catch (e: Exception) {
            mWelcomeRole!!.officerSupervisor = ""
        }
        try {
            mWelcomeRole?.officerBeat = mSelectedBeatStat?.beatName
        } catch (e: Exception) {
            mWelcomeRole?.officerBeat = ""
        }
        try {
//                            mWelcomeRole.setOfficerShift(mSelectedShiftStat.getShiftName());
            mWelcomeRole?.officerShift =
                sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
        } catch (e: Exception) {
            mWelcomeRole?.officerShift = ""
        }
        try {
            mWelcomeRole?.officerRadio = mSelectedRadioStat?.radioName
        } catch (e: Exception) {
            mWelcomeRole?.officerRadio = ""
        }
        try {
            mWelcomeRole?.mEquipment =
                mAutoComTextViewPrinterEquipment?.text.toString()
        } catch (e: Exception) {
            mWelcomeRole?.mEquipment = ""
        }
        try {
            if (mAutoComTextViewLot != null)
                mWelcomeRole!!.mLot = mAutoComTextViewLot!!.text!!.toString()
        } catch (e: Exception) {
            mWelcomeRole!!.mLot = ""
        }
        mWelcomeResponseData!!.data!![0].responsedata!!.user = mWelcomeRole
        if (isInternetAvailable(this)) {
            if (imageViewSignature.visibility == View.VISIBLE) {
                scanStatus = false
                callSignatureBulkImageUpload()
            } else {
                scanStatus = false
                insertFormToDb(false, false, null)
                callUpdateSiteOfficerApi()
            }
        } else {
//                            dismissLoader()
            printToastMSG(this, "You are Offline, Data Saved!")
            insertFormToDb(false, false, null)
        }
        uploadActivityImages()
    }

    private fun bntScanClick() {
        if (!sharedPreference.read(SharedPrefKey.LOCKED_LPR_BOOL, true).nullSafety()) {
            if (LogUtil.isEnableActivityLogs)
                callEventActivityLogApiForBaseActivity(
                    mValue = ACTIVITY_LOG_WELCOME_SCAN,
                    isDisplay = true
                )
            removeFocus()
            if (isFormValid("SCAN")) {
                if (!isDetailsChanged()) {
                    //set dropdown values
                    val mWelcomeRole =
                        mWelcomeResponseData!!.data!![0].responsedata!!.user
                    try {
                        mWelcomeRole!!.officerZone = mSelectedZoneStat!!.zoneName
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerZone = ""
                    }
                    try {
                        mWelcomeRole!!.officerSupervisor =
                            mSelectedSupervisorStat?.mSuperName.nullSafety()
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerSupervisor = ""
                    }
                    try {
                        mWelcomeRole!!.officerBeat = mSelectedBeatStat!!.beatName
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerBeat = ""
                    }
                    try {
//                            mWelcomeRole.setOfficerShift(mSelectedShiftStat.getShiftName());
                        mWelcomeRole!!.officerShift =
                            sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerShift = ""
                    }
                    try {
                        mWelcomeRole!!.officerRadio = mSelectedRadioStat!!.radioName
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerRadio = ""
                    }
                    try {

                        mWelcomeRole!!.mLot =
                            if (mAutoComTextViewLot != null) mAutoComTextViewLot!!.text!!.toString() else ""
                    } catch (e: Exception) {
                        mWelcomeRole!!.mLot = ""
                    }
                    mWelcomeResponseData!!.data!![0].responsedata!!.user = mWelcomeRole
                    // mWelcomeResponseData.getData().get(0).getResponsedata().setUser(mWelcomeRole);
                    if (isInternetAvailable(this)) {
                        if (imageViewSignature.visibility == View.VISIBLE) {
                            scanStatus = true
                            callSignatureBulkImageUpload()
                        } else {
                            scanStatus = true
                            callUpdateSiteOfficerApi()
                            insertFormToDb(true, false, null)
                        }

                    } else {
                        dismissLoader()
                        printToastMSG(
                            this,
                            getString(R.string.err_msg_connection_was_refused)
                        )
                        insertFormToDb(false, false, null)
                    }
                }
                //TODO add in above case
                //finish();

                sharedPreference.write(SharedPrefKey.IS_FROM_FILLED, true)
//                        mDb?.dbDAO?.getWelcomeFormList()
                val handler = Handler(Looper.getMainLooper())
                val runnable: java.lang.Runnable = object : java.lang.Runnable {
                    override fun run() {
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_BISMARCK,
                                ignoreCase = true
                            )
                        ) {
                            launchScreen(
                                this@WelcomeActivity,
                                LprDetailsActivity::class.java
                            )
                        } else {
                            launchScreen(
                                this@WelcomeActivity,
                                LprScanActivity::class.java
                            )
                        }

                    }
                }
                handler.postDelayed(runnable, 300)
            }
        }
    }

    private fun btnScanStickerClick() {
        if (!sharedPreference.read(SharedPrefKey.LOCKED_LPR_BOOL, true).nullSafety()) {
            if (LogUtil.isEnableActivityLogs)
                callEventActivityLogApiForBaseActivity(
                    mValue = ACTIVITY_LOG_WELCOME_SCAN,
                    isDisplay = true
                )
            removeFocus()
            if (isFormValid("SCAN")) {
                if (!isDetailsChanged()) {
                    //set dropdown values
                    val mWelcomeRole =
                        mWelcomeResponseData!!.data!![0].responsedata!!.user
                    try {
                        mWelcomeRole!!.officerZone = mSelectedZoneStat!!.zoneName
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerZone = ""
                    }
                    try {
                        mWelcomeRole!!.officerSupervisor =
                            mSelectedSupervisorStat?.mSuperName.nullSafety()
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerSupervisor = ""
                    }
                    try {
                        mWelcomeRole!!.officerBeat = mSelectedBeatStat!!.beatName
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerBeat = ""
                    }
                    try {
//                            mWelcomeRole.setOfficerShift(mSelectedShiftStat.getShiftName());
                        mWelcomeRole!!.officerShift =
                            sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerShift = ""
                    }
                    try {
                        mWelcomeRole!!.officerRadio = mSelectedRadioStat!!.radioName
                    } catch (e: Exception) {
                        mWelcomeRole!!.officerRadio = ""
                    }
                    try {

                        mWelcomeRole!!.mLot =
                            if (mAutoComTextViewLot != null) mAutoComTextViewLot!!.text!!.toString() else ""
                    } catch (e: Exception) {
                        mWelcomeRole!!.mLot = ""
                    }
                    mWelcomeResponseData!!.data!![0].responsedata!!.user = mWelcomeRole
                    // mWelcomeResponseData.getData().get(0).getResponsedata().setUser(mWelcomeRole);
                    if (isInternetAvailable(this)) {
                        if (imageViewSignature.visibility == View.VISIBLE) {
                            scanStatus = true
                            callSignatureBulkImageUpload()
                        } else {
                            scanStatus = true
                            callUpdateSiteOfficerApi()
                            insertFormToDb(true, false, null)
                        }

                    } else {
                        dismissLoader()
                        printToastMSG(
                            this,
                            getString(R.string.err_msg_connection_was_refused)
                        )
                        insertFormToDb(false, false, null)
                    }
                }
                //TODO add in above case
                //finish();

                sharedPreference.write(SharedPrefKey.IS_FROM_FILLED, true)
//                        mDb?.dbDAO?.getWelcomeFormList()
                val handler = Handler(Looper.getMainLooper())
                val runnable: java.lang.Runnable = object : java.lang.Runnable {
                    override fun run() {
                        if (BuildConfig.FLAVOR.equals(
                                Constants.FLAVOR_TYPE_BISMARCK,
                                ignoreCase = true
                            )
                        ) {
                            launchScreen(
                                this@WelcomeActivity,
                                LprDetailsActivity::class.java
                            )
                        } else {
                            launchScreen(
                                this@WelcomeActivity,
                                VehicleStickerScanActivity::class.java
                            )
                        }
                    }
                }
                handler.postDelayed(runnable, 300)
            }
        }
    }

    private fun saveActivityAPI() {
        /**
         * Activity form data other details
         *
         */
        if (!TextUtils.isEmpty(mAutoComTextViewActivity!!.text.toString())) {
            //user event logging
            try {
                isSaveButtonClicked = true
                callEventActivityLogApi()
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!
                    .toDouble()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!
                    .toDouble()
                val mLocationUpdateRequest = LocUpdateRequest()
                mLocationUpdateRequest.activityType =
                    Constants.ACTIVITY_TYPE_LOCATION_UPDATE
                mLocationUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
                mLocationUpdateRequest.locationUpdateType = "break"
                mLocationUpdateRequest.latitude = mLat
                mLocationUpdateRequest.longitude = mLong
                mLocationUpdateRequest.mShift =
                    mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerShift
                //                                mLocationUpdateRequest.setClientTimestamp(timeMilli1);
                mLocationUpdateRequest.clientTimestamp = splitDateLpr(mZone)
                callLocationApi(mLocationUpdateRequest)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
//                            LogUtil.printToastMSG(mContext, "Please select activity");
        }
        //TODO add in above case
        //finish();
        sharedPreference.write(SharedPrefKey.IS_FROM_FILLED, true)

        val handler = Handler(Looper.getMainLooper())
        val runnable: java.lang.Runnable = object : java.lang.Runnable {
            override fun run() {
//                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)){
//                    launchScreen(this@WelcomeActivity, LprDetailsActivity::class.java)
//                }else{
//                    launchScreen(this@WelcomeActivity, DashboardActivity::class.java)
//                }
                launchScreen(this@WelcomeActivity, DashboardActivity::class.java)
            }
        }
        handler.postDelayed(runnable, 700)
    }

    /* private void logout() {
        //set login status false
        sharedPreference.getInstance(this).write(SharedPrefKey.IS_LOGGED_IN, false);
        //stop location service
        stopService(new Intent(this, BackgroundLocationUpdateService.class));
        //user event logging
        callPushEventLogin(Constants.SESSION, mEventStartTimeStamp);
        //LogUtil.printToastMSG(WelcomeActivity.this, "Logout");
        //launch login screen
        launchScreen(WelcomeActivity.this, LoginActivity.class);
        finishAffinity();
    }*/
    override fun onBackPressed() {
        backpressCloseDrawer()
        openLogoutDialog(mContext)
    }

    override fun onConnected(bundle: Bundle?) {
        //startLocationService();
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {
        if (msg.equals(getString(R.string.warnings_lbl_dataset_empty_header))) {
            /**
             * If all dropdown list get empty to any reason relogin load all again
             */
            logout(mContext!!)
        }
    }

    /**
     * Receiver for broadcasts sent by [BackgroundLocationUpdateService].
     */
    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location =
                intent.getParcelableExtra<Location>(BackgroundLocationUpdateService.EXTRA_LOCATION)
            if (location != null) {
                printLog("lat welcome", location.latitude.toString() + "")
                printLog("long welcome", location.longitude.toString() + "")
                //LogUtil.printToastMSG(context,String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude()));
                //Login event
                val iSLoginLogged = sharedPreference.read(SharedPrefKey.IS_LOGIN_LOGGED, false)
                if (iSLoginLogged) {
                    //user event logging - login
                    callPushEventLogin(Constants.FROM_SCREEN_LOGIN, mEventStartTimeStamp)
                    callPushEventLogin(Constants.SESSION, mEventStartTimeStamp)
                    sharedPreference.write(SharedPrefKey.IS_LOGIN_LOGGED, false)
                }
            }
        }
    }

    private fun getMyReceiver(): MyReceiver {
        if (myReceiver == null) {
            myReceiver = MyReceiver()
        }
        return myReceiver!!
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
        dismissLoader()
    }

    override fun onRestart() {
        super.onRestart()
        dismissLoader()
        if (isSaveButtonClicked) {
            bannerList?.clear()
            mBannerAdapter?.setTimingBannerList(bannerList)
            mViewPagerBanner.adapter = mBannerAdapter
            mViewPagerBanner.currentItem = 0
            mBannerAdapter!!.notifyDataSetChanged()
            isSaveButtonClicked = false
            setCameraImages()
        }

    }

    override fun onResume() {
        super.onResume()
        mContext = this
        val myUuid = UUID.randomUUID()
        mActivityId = myUuid.toString()
        if (isGPSEnabled(this) && !AppUtils.isServiceRunning(
                "BackgroundLocationUpdateService",
                this
            ) && !isFinishing
        ) {
            startLocationService()
        } else {
            //Toast.makeText(mContext, "false",Toast.LENGTH_SHORT).show();
        }

        try {
            val mydir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                Constants.FILE_NAME + Constants.SIGNATURE
            )
            fileSignature = File(mydir.absolutePath, getSignatureFileNameWithExt())

//            mSignaturePath = sharedPreference.read(SharedPrefKey.FILE_PATH, "").toString()
//            val imageName = getSignatureFileNameWithExt()
//            mSignaturePath = mSignaturePath + Constants.CAMERA + "/" + imageName
//            fileSignature = File(mSignaturePath)
            if (fileSignature!!.exists()) {
                imageViewSignature.setImageURI(Uri.fromFile(fileSignature))
            }
        } catch (e: Exception) {
        }


        CreateDirForContinuesMode()
        try {
            createCredFolder()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        setBannerImageAdapter()

        if (isSaveButtonClicked) {
            bannerList?.clear()
            mBannerAdapter?.setTimingBannerList(bannerList)
            mViewPagerBanner.adapter = mBannerAdapter
            mViewPagerBanner.currentItem = 0
            mBannerAdapter!!.notifyDataSetChanged()
            isSaveButtonClicked = false
            setCameraImages()
        }

        try {
            uploadOfflineCitation()
            uploadOfflineTimingsToServer()
            callTicketCancelApi("", "")


            // Use lifecycleScope to launch a coroutine
            lifecycleScope.launch {
                delay(6000L) // Wait for 2 seconds
                /**
                 * Checks if the violation list is empty by calling [DataBaseUtil.getViolationListEmpty].
                 * If the list is not empty and citation is locked tehn move to officer in citation form
                 */
                if (!DataBaseUtil.getViolationListEmptyForOfflineCitation(getMyDatabase())) {
                    /**
                     * Upload Offline citation and cancel data to server
                     */
                    checkOfflineCitationAndCancelDataInDBForUpload()
                }

                if (sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")!!.toString().isEmpty()) {
                    isShiftEmpty()
                }
            }

//            sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkOfflineCitationAndCancelDataInDBForUpload() {
        val lockLprModel = getLprLock(this@WelcomeActivity, sharedPreference)
        ioScope.launch {
            delay(2000L)
            if (sharedPreference.read(SharedPrefKey.IS_LOGGED_IN, false)) {
                //TODO your background code
                val mIssuranceModel: List<CitationInsurranceDatabaseModel> =
                    getMyDatabase()?.dbDAO?.getCitationInsurranceUnuploadCitation() as List<CitationInsurranceDatabaseModel>

                if (lockLprModel != null && lockLprModel.mLprNumber != null &&
                    !TextUtils.isEmpty(lockLprModel.mLprNumber) && lockLprModel.mLprNumber.nullSafety().length > 1
                    || mIssuranceModel != null && mIssuranceModel.size > 0
                ) {
                    if (mIssuranceModel != null && mIssuranceModel.size > 0) {
                        mCitationNumberId = mIssuranceModel.get(0).citationNumber;
                        offlineStatus = 2
                        callTicketStatusApi(mIssuranceModel.get(0).citationNumber)
                    } else {
                        moveToCitationFormWithUploadedCitation()
                    }
                } else {
                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                    deleteImages()
                }
            }


        }
    }

    fun moveToCitationFormWithUploadedCitation() {
        val lockLprModel = getLprLock(this@WelcomeActivity, sharedPreference)
        val mIssuranceModel: List<CitationInsurranceDatabaseModel> =
            getMyDatabase()?.dbDAO?.getCitationInsurranceUnuploadCitation() as List<CitationInsurranceDatabaseModel>
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            getMyDatabase()?.dbDAO?.deleteSaveCitation(mCitationNumberId!!)
            getMyDatabase()?.dbDAO?.updateCitationBooklet(0, mCitationNumberId)
        } else {
            if (mIssuranceModel.isNotEmpty()) {
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, true)
                sharedPreference.writeOverTimeParkingTicketDetails(
                    SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                    AddTimingRequest()!!
                )
                val mIntent: Intent
                if ((mIssuranceModel.isNotEmpty() && mIssuranceModel.last().citationData?.municipalCitationMotoristDetailsModel != null)
                    || lockLprModel?.ticketCategory == API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET
                ) {
                    mIntent =
                        Intent(this@WelcomeActivity, MunicipalCitationDetailsActivity::class.java)
                } else {
                    mIntent = Intent(this@WelcomeActivity, LprDetails2Activity::class.java)
                }

                mIntent.putExtra("make", lockLprModel?.mMake)
                mIntent.putExtra(
                    "from_scr",
                    if (mIssuranceModel.isNotEmpty()) "WelcomeUnUpload" else "lpr_lock"
                )
                mIntent.putExtra("model", lockLprModel?.mModel)
                mIntent.putExtra("color", lockLprModel?.mColor)
                mIntent.putExtra("lpr_number", lockLprModel?.mLprNumber)
                mIntent.putExtra("violation_code", lockLprModel?.mViolationCode)
                mIntent.putExtra("address", lockLprModel?.mAddress)
                startActivity(mIntent)
            }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)){
                /**
                 * After discurssion with Sri 31 oct 25 after scan and go to the citation form then officer should be lock event there is plate number
                 */
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, true)
                sharedPreference.writeOverTimeParkingTicketDetails(
                    SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                    AddTimingRequest()!!
                )
                val mIntent: Intent
                if ((mIssuranceModel.isNotEmpty() && mIssuranceModel.last().citationData?.municipalCitationMotoristDetailsModel != null)
                    || lockLprModel?.ticketCategory == API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET
                ) {
                    mIntent =
                        Intent(this@WelcomeActivity, MunicipalCitationDetailsActivity::class.java)
                } else {
                    mIntent = Intent(this@WelcomeActivity, LprDetails2Activity::class.java)
                }

                mIntent.putExtra("make", lockLprModel?.mMake)
                mIntent.putExtra(
                    "from_scr",
                    if (mIssuranceModel.isNotEmpty()) "WelcomeUnUpload" else "lpr_lock"
                )
                mIntent.putExtra("model", lockLprModel?.mModel)
                mIntent.putExtra("color", lockLprModel?.mColor)
                mIntent.putExtra("lpr_number", lockLprModel?.mLprNumber)
                mIntent.putExtra("violation_code", lockLprModel?.mViolationCode)
                mIntent.putExtra("address", lockLprModel?.mAddress)
                startActivity(mIntent)
            }else{
                /**
                 * If there is no offline citation so scan and menu button not lock
                 */
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
            }
        }
    }

    override fun onDestroy() {
        layOtherDetails.removeAllViews()
        layAssetDetails.removeAllViews()
        layLocationDetails.removeAllViews()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(myReceiver!!)
        //mDatasetList =null
        super.onDestroy()
    }

    //dialog for signature view
    private fun setSignatureView() {
        //Util.hideKeyBoard(getActivity());
        // custom dialog
        val dialog = Dialog(this@WelcomeActivity)
        dialog.setContentView(R.layout.dialog_signature)
        dialog.setTitle("Title...")

        // set the custom dialog components - text, image and button
        val signatureView = dialog.findViewById<View>(R.id.signature_view) as SignaturePad
        val clear = dialog.findViewById<View>(R.id.clear) as Button
        val save = dialog.findViewById<View>(R.id.save) as Button
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        clear.setOnClickListener { signatureView.clear(); }
        save.setOnClickListener {
            dialog.dismiss()
            imageViewSignature.setImageBitmap(signatureView.getSignatureBitmap())
            SaveSignature(signatureView.getSignatureBitmap())
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    //TODO will add comments later
    private fun SaveSignature(finalBitmap: Bitmap) {
        val mydir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Constants.FILE_NAME + Constants.SIGNATURE
        )
//        val myDir = File(
//                Environment.getExternalStorageDirectory().absolutePath,
//                Constants.FILE_NAME + Constants.CAMERA
//        )
        mydir.mkdirs()
//        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())

        val file = File(mydir.absolutePath, getSignatureFileNameWithExt())
        if (file.exists()) file.delete()
        try {
            //new ImageCompression(this,file.getAbsolutePath()).execute(finalBitmap);
            val out = FileOutputStream(file)
            //finalBitmap = Bitmap.createScaledBitmap(finalBitmap,(int)1080/2,(int)1920/2, true);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO will add comments later
    private fun CreateDirForContinuesMode() {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.COTINOUS
        )
        myDir.mkdirs()
    }

    //call all dataset Apis with a sec of time interval
    private fun callApiParallelyRecursive(
        mUpdateTimeDataset: List<UpdateTimeDb>, arrayIndex: Int,
        pageIndex: Long
    ) {
        if (mUpdateTimeDataset.size > 0 && mUpdateTimeDataset[0].status.nullSafety()) {
            callCitationDatasetApi(mUpdateTimeDataset[0].name.nullSafety(), pageIndex)
        }
    }


    private fun saveDatasetTypeList(mResList: DatasetData) {
        try {
//
            when (mResList.metadata!!.type) {
                DATASET_DECAL_YEAR_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetDecalYearListModel = DatasetDecalYearListModel()
                            datasetDecalYearListModel.id = 1;
                            datasetDecalYearListModel.decalYearList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetDecalYearListModel(
                                datasetDecalYearListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MAKE_MODEL_LIST -> {

                    mResList.response?.let { makeListTotal.addAll(it) }

                    Collections.sort(makeListTotal, object : Comparator<DatasetResponse?> {
                        override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                            return (if (lhs?.make != null) lhs.make else "")!!.compareTo((if (rhs?.make != null) rhs.make else "")!!)
                        }
                    })

                    mainScope.launch {
                        try {
                            val datasetCarMakeListModel = DatasetCarMakeListModel()
                            datasetCarMakeListModel.id = 1;
                            datasetCarMakeListModel.carMakeList = makeListTotal
                            getMyDatabase()?.dbDAO?.insertDatasetCarMakeListModel(
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
                    mainScope.launch {
                        try {
                            val datasetCarColorListModel = DatasetCarColorListModel()
                            datasetCarColorListModel.id = 1;
                            datasetCarColorListModel.carColorList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetCarColorListModel(
                                datasetCarColorListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_STATE_LIST -> {
                    mResList.response?.let { stateList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetStateListModel = DatasetStateListModel()
                            datasetStateListModel.id = 1;
                            datasetStateListModel.stateList = stateList
                            getMyDatabase()?.dbDAO?.insertDatasetStateListModel(
                                datasetStateListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_BLOCK_LIST -> {
                    // Add response list
                    mResList.response?.let { blockList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetBlockListModel = DatasetBlockListModel()
                            datasetBlockListModel.id = 1;
                            datasetBlockListModel.blockList = blockList
                            getMyDatabase()?.dbDAO?.insertDatasetBlockListModel(
                                datasetBlockListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_STREET_LIST -> {
                    // Add response list
                    mResList.response?.let { streetList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetStreetListModel = DatasetStreetListModel();
                            datasetStreetListModel.id = 1
                            datasetStreetListModel.streetList = streetList
                            getMyDatabase()?.dbDAO?.insertDatasetStreetListModel(
                                datasetStreetListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }


                DATASET_METER_LIST -> {

                    mResList.response?.let { meterList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetMeterListModel = DatasetMeterListModel()
                            datasetMeterListModel.id = 1
                            datasetMeterListModel.meterList = meterList.distinctBy { it.name }
                            getMyDatabase()?.dbDAO?.insertDatasetMeterListModel(
                                datasetMeterListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_SPACE_LIST -> {
                    mResList.response?.let { spaceList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetSpaceListModel = DatasetSpaceListModel()
                            datasetSpaceListModel.id = 1
                            datasetSpaceListModel.spaceList = spaceList.distinctBy { it.spaceName }
                            getMyDatabase()?.dbDAO?.insertDatasetSpaceListModel(
                                datasetSpaceListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }


                DATASET_CAR_BODY_STYLE_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetCarBodyStyleListModel = DatasetCarBodyStyleListModel();
                            datasetCarBodyStyleListModel.id = 1
                            datasetCarBodyStyleListModel.carBodyStyleList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetCarBodyStyleListModel(
                                datasetCarBodyStyleListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_VIOLATION_LIST -> {
                    mResList.response?.let { violationList.addAll(it) }

                    Collections.sort(violationList, object : Comparator<DatasetResponse?> {
                        override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                            return lhs?.violation!!.compareTo(rhs?.violation!!)
                        }
                    })

                    mainScope.launch {
                        try {
                            val datasetViolationListModel = DatasetViolationListModel()
                            datasetViolationListModel.id = 1;
                            datasetViolationListModel.violationList = violationList
                            getMyDatabase()?.dbDAO?.insertDatasetViolationListModel(
                                datasetViolationListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_VIO_LIST -> {
                    mResList.response?.let { vioList.addAll(it) }
                    mainScope.launch {
                        try {
                            val datasetVioListModel = DatasetVioListModel()
                            datasetVioListModel.id = 1;
                            datasetVioListModel.vioList = vioList
                            getMyDatabase()?.dbDAO?.insertDatasetVioListModel(
                                datasetVioListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_HOLIDAY_CALENDAR_LIST -> {
                    mResList.response?.let { holidayCalendarList.addAll(it) }
                    mainScope.launch {
                        try {
                            val datasetHolidayCalendarListModel = DatasetHolidayCalendarList()
                            datasetHolidayCalendarListModel.id = 1;
                            datasetHolidayCalendarListModel.holidayCalendatList = holidayCalendarList
                            getMyDatabase()?.dbDAO?.insertDatasetHolidayCalendarListModel(
                                datasetHolidayCalendarListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                //TODO JANAK : Use below code to avoid diskReadViolation
//                DATASET_SIDE_LIST -> {
//                    mainScope.launch {
//                        try {
//                            val datasetSideListModel = DatasetSideListModel().apply {
//                                id = 1
//                                sideList = mResList.response
//                            }
//
//                            withContext(Dispatchers.IO) {
//                                getMyDatabase()?.dbDAO?.insertDatasetSideListModel(datasetSideListModel)
//                            }
//
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                }
//
                //ORRRRRRR Use Below
//                @Dao
//                interface DbDAO {
//                    @Insert(onConflict = OnConflictStrategy.REPLACE)
//                    suspend fun insertDatasetSideListModel(model: DatasetSideListModel)
//                }

//                mainScope.launch(Dispatchers.IO) {
//                    val datasetSideListModel = DatasetSideListModel().apply {
//                        id = 1
//                        sideList = mResList.response
//                    }
//                    getMyDatabase()?.dbDAO?.insertDatasetSideListModel(datasetSideListModel)
//                }

                DATASET_SIDE_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetSideListModel = DatasetSideListModel()
                            datasetSideListModel.id = 1
                            datasetSideListModel.sideList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetSideListModel(datasetSideListModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_TIER_STEM_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetTierStemListModel = DatasetTierStemListModel()
                            datasetTierStemListModel.id = 1
                            datasetTierStemListModel.tierStemList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetTierStemListModel(
                                datasetTierStemListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_NOTES_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetNotesListModel = DatasetNotesListModel()
                            datasetNotesListModel.id = 1
                            datasetNotesListModel.notesList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetNotesListModel(
                                datasetNotesListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_REMARKS_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetRemarksListModel = DatasetRemarksListModel()
                            datasetRemarksListModel.id = 1
                            datasetRemarksListModel.remarksList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetRemarksListModel(
                                datasetRemarksListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }


                DATASET_REGULATION_TIME_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetRegulationTimeListModel = DatasetRegulationTimeListModel()
                            datasetRegulationTimeListModel.id = 1
                            datasetRegulationTimeListModel.regulationTimeList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetRegulationTimeListModel(
                                datasetRegulationTimeListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                DATASET_LOT_LIST -> {
                    mResList.response?.let { lotList.addAll(it) }

                    Collections.sort(lotList, object : Comparator<DatasetResponse?> {
                        override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                            return lhs?.location!!.compareTo(rhs?.location!!)
                        }
                    })

                    mainScope.launch {
                        try {
                            val datasetLotListModel = DatasetLotListModel()
                            datasetLotListModel.id = 1
                            datasetLotListModel.lotList = lotList
                            getMyDatabase()?.dbDAO?.insertDatasetLotListModel(datasetLotListModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

                DATASET_SETTINGS_LIST -> {
                    try {
                        if (mResList.response != null && mResList.response?.size.nullSafety() > 0) {
                            setLoginTimeStamp()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                    mainScope.launch {
                        try {
                            val datasetSettingsListModel = DatasetSettingsListModel()
                            datasetSettingsListModel.id = 1
                            datasetSettingsListModel.settingsList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetSettingsListModel(
                                datasetSettingsListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_CANCEL_REASON_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetCancelReasonListModel = DatasetCancelReasonListModel()
                            datasetCancelReasonListModel.id = 1
                            datasetCancelReasonListModel.cancelReasonList = mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetCancelReasonListModel(
                                datasetCancelReasonListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_PBC_ZONE_LIST -> {
                    mResList.response?.let { pbcZoneList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetPBCZoneListModel = DatasetPBCZoneListModel()
                            datasetPBCZoneListModel.id = 1
                            datasetPBCZoneListModel.pbcZoneList = pbcZoneList
                            getMyDatabase()?.dbDAO?.insertDatasetPBCZoneListModel(
                                datasetPBCZoneListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_VOID_AND_REISSUE_REASON_LIST -> {
                    mainScope.launch {
                        try {
                            val datasetVoidAndReissueReasonListModel =
                                DatasetVoidAndReissueReasonListModel()
                            datasetVoidAndReissueReasonListModel.id = 1
                            datasetVoidAndReissueReasonListModel.voidAndReissueReasonList =
                                mResList.response
                            getMyDatabase()?.dbDAO?.insertDatasetVoidAndReissueReasonListModel(
                                datasetVoidAndReissueReasonListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_VIOLATION_LIST -> {
                    mResList.response?.let { municipalViolationList.addAll(it) }

                    Collections.sort(municipalViolationList, object : Comparator<DatasetResponse?> {
                        override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                            return lhs?.violation!!.compareTo(rhs?.violation!!)
                        }
                    })

                    mainScope.launch {
                        try {
                            val datasetMunicipalViolationListModel =
                                DatasetMunicipalViolationListModel()
                            datasetMunicipalViolationListModel.id = 1;
                            datasetMunicipalViolationListModel.municipalViolationList =
                                municipalViolationList
                            getMyDatabase()?.dbDAO?.insertDatasetMunicipalViolationListModel(
                                datasetMunicipalViolationListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_BLOCK_LIST -> {
                    mResList.response?.let { municipalBlockList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetMunicipalBlockListModel = DatasetMunicipalBlockListModel()
                            datasetMunicipalBlockListModel.id = 1
                            datasetMunicipalBlockListModel.municipalBlockList = municipalBlockList
                            getMyDatabase()?.dbDAO?.insertDatasetMunicipalBlockListModel(
                                datasetMunicipalBlockListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_STREET_LIST -> {
                    mResList.response?.let { municipalStreetList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetMunicipalStreetListModel = DatasetMunicipalStreetListModel()
                            datasetMunicipalStreetListModel.id = 1
                            datasetMunicipalStreetListModel.municipalStreetList = municipalStreetList
                            getMyDatabase()?.dbDAO?.insertDatasetMunicipalStreetListModel(
                                datasetMunicipalStreetListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_CITY_LIST -> {
                    mResList.response?.let { municipalCityList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetMunicipalCityListModel = DatasetMunicipalCityListModel()
                            datasetMunicipalCityListModel.id = 1
                            datasetMunicipalCityListModel.municipalCityList = municipalCityList
                            getMyDatabase()?.dbDAO?.insertDatasetMunicipalCityListModel(
                                datasetMunicipalCityListModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                DATASET_MUNICIPAL_STATE_LIST -> {
                    mResList.response?.let { municipalStateList.addAll(it) }

                    mainScope.launch {
                        try {
                            val datasetMunicipalStateListModel = DatasetMunicipalStateListModel()
                            datasetMunicipalStateListModel.id = 1
                            datasetMunicipalStateListModel.municipalStateList = municipalStateList
                            getMyDatabase()?.dbDAO?.insertDatasetMunicipalStateListModel(
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
//            getTimestampFromDb()
            printToastMSG(this, mResList.metadata!!.type + "Db error")
        }
    }

    //save Activity list to Databse
    private fun saveActivityList() {
        showProgressLoader(getString(R.string.scr_message_please_wait))
        updateTimeDatasetActivity = ArrayList()
        if (mDataSetTimeObject == null) {
            try {
                mDataSetTimeObject = getMyDatabase()?.dbDAO?.getUpdateTimeResponse()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            mWelcomeListDataSet = Singleton.getWelcomeDbObject(getMyDatabase());
            if (mWelcomeListDataSet != null) {
                mDatabaseWelcomeList = mWelcomeListDataSet?.welcomeList
                setLicenseKeyForAlpr()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        printLog("dataset", "  Act "+ mDataSetTimeObject)
        if (mDataSetTimeObject != null && mDataSetTimeObject!!.timeList != null) {
            printLog("dataset", "  Act IF ")
            if (mDataSetTimeObject?.timeList?.activityList != null && mDataSetTimeObject?.timeList?.activityList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "ActivityList",
                        mDataSetTimeObject?.timeList?.activityList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.commentsList != null && mDataSetTimeObject?.timeList?.commentsList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "CommentsList",
                        mDataSetTimeObject?.timeList?.commentsList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.supervisorList != null && mDataSetTimeObject?.timeList?.supervisorList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "SupervisorList",
                        mDataSetTimeObject?.timeList?.supervisorList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.beatList != null && mDataSetTimeObject?.timeList?.beatList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "BeatList",
                        mDataSetTimeObject?.timeList?.beatList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.zoneList != null && mDataSetTimeObject?.timeList?.zoneList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "ZoneList",
                        mDataSetTimeObject?.timeList?.zoneList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.radioList != null && mDataSetTimeObject?.timeList?.radioList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "RadioList",
                        mDataSetTimeObject?.timeList?.radioList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.shiftList != null && mDataSetTimeObject?.timeList?.shiftList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "ShiftList",
                        mDataSetTimeObject?.timeList?.shiftList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.agencyList != null && mDataSetTimeObject?.timeList?.agencyList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "AgencyList",
                        mDataSetTimeObject?.timeList?.agencyList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.mDeviceList != null && mDataSetTimeObject?.timeList?.mDeviceList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "DeviceList",
                        mDataSetTimeObject?.timeList?.mDeviceList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.mCityZoneList != null && mDataSetTimeObject?.timeList?.mCityZoneList?.status.nullSafety()) {
//                updateTimeDataset.add(new UpdateTimeDb("CityZoneList", mDataSetTimeObject?.timeList.mCityZoneList?.getStatus()));
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        DATASET_PBC_ZONE_LIST,
                        mDataSetTimeObject?.timeList?.mCityZoneList?.status
                    )
                )
            }
            if (mDataSetTimeObject?.timeList?.mEquipmentList != null && mDataSetTimeObject?.timeList?.mEquipmentList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "EquipmentList",
                        mDataSetTimeObject?.timeList?.mEquipmentList?.status
                    )
                )
            }

            if (mDataSetTimeObject?.timeList?.mSquadList != null && mDataSetTimeObject?.timeList?.mSquadList?.status.nullSafety()) {
                updateTimeDatasetActivity?.add(
                    UpdateTimeDb(
                        "SquadList",
                        mDataSetTimeObject?.timeList?.mSquadList?.status
                    )
                )
            }
        } else {
            printLog("dataset", "  Act else ")
            updateTimeDatasetActivity?.add(UpdateTimeDb("ActivityList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("CommentsList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("SupervisorList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("BeatList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("ZoneList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("RadioList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("ShiftList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("AgencyList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("DeviceList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb(DATASET_PBC_ZONE_LIST, true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("EquipmentList", true))
            updateTimeDatasetActivity?.add(UpdateTimeDb("SquadList", true))
        }
        if (mWelcomeListDataSet?.welcomeList?.deviceLicenseStats == null) {
            updateTimeDatasetActivity?.add(UpdateTimeDb("DeviceLicenseList", true))
        }
        if (updateTimeDatasetActivity?.size.nullSafety() > 0 && updateTimeDatasetActivity?.get(0)?.status.nullSafety()) {
            callCitationDatasetApiActivityForm(
                updateTimeDatasetActivity?.get(0)?.name.nullSafety(),
                1
            )
        } else {
            try {
                //call citation form data set if activity data set empty
                isCitationDatasetExits()
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.printLog("kalyani_db", "error")
            }
        }
    }

    private fun callActivityDataSetRecursive(name: String, index: Long) {
        callCitationDatasetApiActivityForm(name, index)
    }

    var loaderForDataSet = 0

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> if (tag.equals(
                    DynamicAPIPath.POST_CITATION_DATASET,
                    ignoreCase = true
                ) && loaderForDataSet == 20
            ) {
                loaderForDataSet++
//                if (tag.equals(DynamicAPIPath.POST_ACTIVITY_LOG, ignoreCase = true))
//                {
//                    showProgressLoader(getString(R.string.scr_message_please_wait))
//                }
            }

            Status.SUCCESS -> {
                // send request, wait for response (the simple socket calls are all blocking)
                mResponseTimeEnd = System.currentTimeMillis()
                mResponseTime = mResponseTimeEnd - mRequestTimeStart
                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.POST_ACTIVITY_LOG, ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LprScanLoggerResponse::class.java)

                            dismissLoader()
                            if (responseModel != null && responseModel.success.nullSafety()) {
                                // if activity images are not uploaded
                                if (mImages!!.size < bannerList!!.size) {
                                    saveActivityLogData(
                                        activityUpdateRequest,
                                        bannerList,
                                        responseModel!!.activityId!!
                                    )
                                }
                            } else {
                                val message: String
                                message = responseModel!!.response.nullSafety()
                                showCustomAlertDialog(
                                    mContext, getString(R.string.scr_lbl_activity),
                                    message, getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel), this
                                )
                            }
                        }
                        if (tag.equals(
                                DynamicAPIPath.POST_TICKET_UPLOADE_STATUS_META,
                                ignoreCase = true
                            )
                        ) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TicketUploadStatusResponse::class.java)

                            dismissLoader()
                            if (responseModel != null) {
                                if (responseModel.citationUploaded!!) {
                                    sharedPreference.write(
                                        SharedPrefKey.LOCKED_LPR_BOOL,
                                        false)
                                    getMyDatabase()!!.dbDAO!!.updateCitationUploadStatus(
                                        0,
                                        mCitationNumberId
                                    )

                                    callTicketCancelApi(
                                        responseModel.message!!.id.toString(),
                                        mCitationNumberId!!.toString()
                                    )
                                } else {
                                    if (offlineStatus == 2) {
                                        moveToCitationFormWithUploadedCitation()
                                    } else if (offlineStatus == 1) {
                                        offlineCitationData?.let { uploadOfflineImages(it) }
                                    }
                                }

                            } else {
//                                val message: String
//                                message = responseModel!!.message.nullSafety()
//                                showCustomAlertDialog(
//                                        mContext, getString(R.string.scr_lbl_activity),
//                                        message, getString(R.string.alt_lbl_OK),
//                                        getString(R.string.scr_btn_cancel), this
//                                )
                            }
                        }
                        if (tag.equals(DynamicAPIPath.GET_WELCOME, ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), WelcomeResponse::class.java)

                            if (responseModel != null && responseModel.status!!) {
                                mWelcomeResponseData = responseModel
                                insertFormToDbByAPIResponse(mWelcomeResponseData!!.data!![0].responsedata!!.user)
                                setActivityData()
                                saveActivityList()
                                if (checkPermission() && responseModel!!.data!![0].responsedata!!.user!!.mSignature != null &&
                                    responseModel!!.data!![0].responsedata!!.user!!.mSignature?.length!! > 0
                                ) {
                                    callDownloadBitmapApi(responseModel!!.data!![0].responsedata!!.user!!.mSignature)
                                }

//                                dismissLoader()
                            } else {
                                showCustomAlertDialog(
                                    mContext,
                                    APIConstant.GET_WELCOME,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            }
                        }
                        if (tag.equals(
                                DynamicAPIPath.POST_DOWNLOAD_FILE,
                                ignoreCase = true
                            )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                if (checkPermission() && responseModel.metadata!![0].url?.length!! > 0) {
                                    DownloadingPrintBitmapFromUrl().execute(
                                        responseModel.metadata!![0].url
                                    )
                                }
                            }
                            dismissLoader()
                        }
                        if (tag.equals(
                                DynamicAPIPath.POST_UPDATE_SITE_OFFICER,
                                ignoreCase = true
                            )
                        ) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UpdateSiteOfficerResponse::class.java)

                            if (responseModel != null && responseModel.status!!) {
//
                                if (scanStatus) {
                                    dismissLoader()
                                    scanStatus = false
                                    callPushEventLogin(
                                        Constants.SUCCESS_CITATION_FORM,
                                        mEventStartTimeStamp
                                    )
                                } else {
                                    callPushEventLogin(
                                        Constants.SUCCESS_CITATION_FORM,
                                        mEventStartTimeStamp
                                    )
                                }
                            } else {
                                showCustomAlertDialog(
                                    mContext,
                                    APIConstant.POST_UPDATE_SITE_OFFICER,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            }
                        }
                        if (tag.equals(DynamicAPIPath.POST_CITATION_DATASET, ignoreCase = true)) {
                            try {
                                if (apiResponse != null && apiResponse.data != null) {


//                                    val responseModel = ObjectMapperProvider.instance.readValue(
//                                        apiResponse.data.toString(),
//                                        DropdownDatasetResponse::class.java
//                                    ) //as DropdownDatasetResponse

                                    val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DropdownDatasetResponse::class.java)


//                                    3. If you are parsing a list, use TypeReference
//                                    val responseList: List<DatasetResponse> = ObjectMapperProvider.instance.readValue(
//                                        jsonString,
//                                        object : com.fasterxml.jackson.core.type.TypeReference<List<DatasetResponse>>() {}
//                                    )
                                    printLog(
                                        "dataset response",
                                        responseModel!!.data!![0].metadata!!.type + " --   "
                                    )
                                    if (responseModel != null && responseModel.status!!) {
                                        //when api is excuted successfully the counter will increment.
                                        val mType = responseModel.data!![0].metadata!!.type
                                        if (mType == DATASET_DECAL_YEAR_LIST || mType == DATASET_MAKE_MODEL_LIST || mType == DATASET_MAKE_MODEL_LIST2 ||
                                            mType == DATASET_CAR_COLOR_LIST || mType == DATASET_STATE_LIST || mType == DATASET_STREET_LIST ||
                                            mType == DATASET_METER_LIST || mType == DATASET_CAR_BODY_STYLE_LIST || mType == DATASET_VIOLATION_LIST ||
                                            mType == DATASET_VIO_LIST || mType == DATASET_HOLIDAY_CALENDAR_LIST || mType == DATASET_SPACE_LIST ||
                                            mType == DATASET_SIDE_LIST || mType == DATASET_TIER_STEM_LIST || mType == DATASET_NOTES_LIST ||
                                            mType == DATASET_REMARKS_LIST || mType == DATASET_REGULATION_TIME_LIST || mType == DATASET_LOT_LIST ||
                                            mType == DATASET_SETTINGS_LIST || mType == DATASET_CANCEL_REASON_LIST || mType == DATASET_VOID_AND_REISSUE_REASON_LIST ||
                                            mType == DATASET_BLOCK_LIST
                                            || mType == DATASET_MUNICIPAL_VIOLATION_LIST
                                            || mType == DATASET_MUNICIPAL_BLOCK_LIST
                                            || mType == DATASET_MUNICIPAL_STREET_LIST
                                            || mType == DATASET_MUNICIPAL_CITY_LIST
                                            || mType == DATASET_MUNICIPAL_STATE_LIST
                                        ) {
                                            mDatasetApiCount++
                                            saveDatasetTypeList(responseModel.data!![0])
                                            val mAPIPageIndex =
                                                responseModel.data!![0].metadata!!.mTotalShards.nullSafety()
                                            printLog(" API COUNT", "$mAPIPageIndex ---- $mType")
                                            if (mAPIPageIndex > pageIndex) {
                                                pageIndex += 1
                                                callApiParallelyRecursive(
                                                    updateTimeDataset,
                                                    APICOUNT,
                                                    pageIndex
                                                )
                                            } else {
                                                APICOUNT++
                                                pageIndex = 1
                                                updateTimeDataset.removeAt(0)
                                                callApiParallelyRecursive(
                                                    updateTimeDataset,
                                                    APICOUNT,
                                                    1
                                                )
                                            }

                                            //show loader when 1 dataset is successfully
                                            if (mDatasetApiCount == 2) {
                                                showProgressLoader(getString(R.string.scr_message_please_wait))
                                            }
                                            if (updateTimeDataset.size < 1) { //TODO TIN risetekInnova = 16 park = 17
                                                printLog("Save data in DB", " -- Save data in DB  ")
                                                //save dataset type for citation and timing dataset in database
                                                dismissLoader()
//                                            lifecycleScope.launch {
//                                                withContext(Dispatchers.Main){
//                                                    try {
//                                                        val mD = DatasetDatabaseModel()
//                                                        mD.dataset = mDatasetList
//                                                        mD.id = 1
//                                                        getMyDatabase()?.dbDAO?.insertDataset(mD)
//
////                                                        val datasetViolationListModel = DatasetViolationListModel();
////                                                        datasetViolationListModel.id = 1;
////                                                        datasetViolationListModel.violationList = mDatasetList?.violationList;
////                                                        getMyDatabase()?.dbDAO?.insertDatasetViolationListModel(datasetViolationListModel)
//                                                    } catch (e: Exception) {
//                                                        e.printStackTrace()
//                                                    }
//
//
//                                                }
//                                            }

//                                            //save dataset type for activity dataset in database
//                                            try {
//                                                val mDatabase = WelcomeListDatatbase()
//                                                mDatabase.welcomeList = mDatabaseWelcomeList
//                                                mDatabase.id = 1
//                                                mDb!!.dbDAO!!.insertActivityList(mDatabase)
//                                            } catch (e: Exception) {
//                                                e.printStackTrace()
//                                            }

//                                            delay(2000)
                                                getSettingFileValues()
                                                showHideInventoryModule()
                                                downloadHeaderFooterForFacsimile()
                                                setAllDropdown()
                                                showHideScanVehicleStickerModule(isFromApiResponse = true)
                                                showHideDirectedEnforcementModule(isFromApiResponse = true)
                                            }
                                        } else {
                                            if (mType == "ActivityList") {
                                                try {


                                                    val responseModelActivty = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ActivityResponse::class.java)

                                                    if (responseModelActivty != null && responseModelActivty.status!!) {
                                                        if (responseModelActivty.data!![0].metadata!!.type == "ActivityList") {
                                                            //setDropdownActivity(responseModelActivty.getData().get(0).getResponse());
                                                            mDatabaseWelcomeList!!.activityStats =
                                                                responseModelActivty.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "CommentsList") {
                                                try {

                                                    val responseModelComments = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CommentResponse::class.java)

                                                    if (responseModelComments != null && responseModelComments.status!!) {
                                                        if (responseModelComments.data!![0].metadata!!.type == "CommentsList") {
                                                            //setDropdownComments(responseModelComments.getData().get(0).getResponse());
                                                            mDatabaseWelcomeList!!.commentStates =
                                                                responseModelComments.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "SupervisorList") {
                                                try {

                                                    val responseModelSupervisor = ObjectMapperProvider.fromJson(apiResponse.data.toString(), SupervisorResponse::class.java)

                                                    if (responseModelSupervisor != null && responseModelSupervisor.status!!) {
                                                        if (responseModelSupervisor.data!![0].metadata!!.type == "SupervisorList") {
                                                            //setDropdownSupervisor(responseModelSupervisor.getData().get(0).getResponse());
                                                            Collections.sort(
                                                                responseModelSupervisor.data!![0].response,
                                                                object :
                                                                    Comparator<SupervisorStat?> {
                                                                    override fun compare(
                                                                        lhs: SupervisorStat?,
                                                                        rhs: SupervisorStat?
                                                                    ): Int {
                                                                        return lhs?.mSuperName.nullSafety()
                                                                            .compareTo(rhs?.mSuperName.nullSafety())
                                                                    }
                                                                })
                                                            mDatabaseWelcomeList!!.supervisorStats =
                                                                responseModelSupervisor.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "BeatList") {
                                                try {

                                                    val responseModelBeat = ObjectMapperProvider.fromJson(apiResponse.data.toString(), BeatResponse::class.java)

                                                    if (responseModelBeat != null && responseModelBeat.status!!) {
                                                        if (responseModelBeat.data!![0].metadata!!.type == "BeatList") {
                                                            //setDropdownBeat(responseModelBeat.getData().get(0).getResponse());
                                                            mDatabaseWelcomeList!!.beatStats =
                                                                responseModelBeat.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "ZoneList") {
                                                try {

                                                    val responseModelZone = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ZoneResponse::class.java)

                                                    if (responseModelZone != null && responseModelZone.status!!) {
                                                        if (responseModelZone.data!![0].metadata!!.type == "ZoneList") {
                                                            mDatabaseWelcomeList!!.zoneStats =
                                                                responseModelZone.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            //                                        if (mType.equals("CityZoneList")) {
                                            if (mType == DATASET_PBC_ZONE_LIST) {
                                                try {

                                                    val responseModelZone = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ZoneResponse::class.java)

                                                    if (responseModelZone != null && responseModelZone.status!!) {
                                                        if (responseModelZone.data!![0].metadata!!.type == DATASET_PBC_ZONE_LIST) {

//                                                        mDatabaseWelcomeList!!.pbcZoneStats =
//                                                                zoneStats
//                                                        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)){
//                                                            var zoneStats: MutableList<ZoneStat>? =
//                                                                mDatabaseWelcomeList!!.pbcZoneStats as MutableList<ZoneStat>?
//                                                            if (zoneStats == null) {
//                                                                zoneStats = ArrayList()
//                                                            }
//
//                                                            //Instead of adding all directly, now we are checking if that entry already exists or not before adding
//                                                            responseModelZone.data?.get(
//                                                                0
//                                                            )?.response?.forEach { zoneStat ->
//                                                                val zoneStatObj =
//                                                                    zoneStats.firstOrNull {
//                                                                        it.zoneName.equals(
//                                                                            zoneStat.zoneName,
//                                                                            true
//                                                                        ) && it.mCityZoneName.equals(
//                                                                            zoneStat.mCityZoneName
//                                                                        )
//                                                                    }
//
//                                                                //zoneStatObj null means, no existing entry found
//                                                                if (zoneStatObj == null) {
//                                                                    zoneStats.add(zoneStat)
//                                                                }
//                                                            }
//                                                        }else{
                                                            mDatabaseWelcomeList!!.pbcZoneStats =
                                                                responseModelZone.data?.get(0)?.response!!
                                                            //}
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "RadioList") {
                                                try {

                                                    val responseModelRadio = ObjectMapperProvider.fromJson(apiResponse.data.toString(), RadioResponse::class.java)

                                                    if (responseModelRadio != null && responseModelRadio.status!!) {
                                                        if (responseModelRadio.data!![0].metadata!!.type == "RadioList") {
                                                            //setDropdownRadio(responseModelRadio.getData().get(0).getResponse());
                                                            mDatabaseWelcomeList!!.radioStats =
                                                                responseModelRadio.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    printLog("kalyani radio", "new")
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "ShiftList") {
                                                try {

                                                    val responseModelShift = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ShiftResponse::class.java)

                                                    if (responseModelShift != null && responseModelShift.status!!) {
                                                        if (responseModelShift.data!![0].metadata!!.type == "ShiftList") {
                                                            //setDropdownShift(responseModelShift.getData().get(0).getResponse());
                                                            mDatabaseWelcomeList!!.shiftStats =
                                                                responseModelShift.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    printLog("kalyani shift", "new")
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "AgencyList") {
                                                try {


                                                    val responseModelAgency = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AgencyResponse::class.java)

                                                    if (responseModelAgency != null && responseModelAgency.status!!) {
                                                        if (responseModelAgency.data!![0].metadata!!.type == "AgencyList") {
                                                            mDatabaseWelcomeList!!.agencyStats =
                                                                responseModelAgency.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "DeviceList") {
                                                try {

                                                    val responseModelDevice = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DeviceListResponse::class.java)

                                                    if (responseModelDevice != null && responseModelDevice.isStatus) {
                                                        if (responseModelDevice.devicedata!![0].metadata!!.type == "DeviceList") {
                                                            mDatabaseWelcomeList!!.deviceStats =
                                                                responseModelDevice.devicedata!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "DeviceLicenseList") {
                                                try {

                                                    val responseModelDevice = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DeviceLicenseListResponse::class.java)

                                                    if (responseModelDevice != null && responseModelDevice.status.nullSafety()) {
                                                        if (responseModelDevice.dataDeviceLicense?.firstOrNull()?.metadataDeviceLicense?.type == "DeviceLicenseList") {
                                                            mDatabaseWelcomeList?.deviceLicenseStats =
                                                                responseModelDevice.dataDeviceLicense
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType == "EquipmentList") {
                                                try {

                                                    val equipmentResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), EquipmentResponse::class.java)

                                                    if (equipmentResponse != null && equipmentResponse.isStatus) {
                                                        if (equipmentResponse.data!![0].metadata!!.type == "EquipmentList") {
                                                            mDatabaseWelcomeList!!.equipmentStates =
                                                                equipmentResponse.data!![0].response
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (mType.equals("SquadList")) {
                                                try {

                                                    val squadResponse = ObjectMapperProvider.fromJson(apiResponse.data.toString(), SquadResponse::class.java)

                                                    if (squadResponse != null && squadResponse?.isStatus.nullSafety()) {
                                                        if (squadResponse?.dataSquad?.get(0)?.metadata?.type.equals(
                                                                "SquadList",
                                                                true
                                                            )
                                                        ) {
                                                            mDatabaseWelcomeList?.squadStates =
                                                                squadResponse.dataSquad?.get(0)?.responseSquad!!
                                                        }
                                                    }
                                                } catch (e: java.lang.Exception) {
                                                    e.printStackTrace()
                                                }
                                            }

                                            /**
                                             * recursive call Activity data set
                                             */
                                            mAPIPageIndexActivity =
                                                responseModel.data?.get(0)?.metadata?.mTotalShards.nullSafety()
                                            printLog(
                                                " API COUNT Activity data set",
                                                "$mAPIPageIndexActivity ---- $mType"
                                            )
                                            if (mAPIPageIndexActivity > pageIndexActivity) {
                                                pageIndexActivity += 1
                                                callActivityDataSetRecursive(
                                                    updateTimeDatasetActivity!![activityIndex].name!!,
                                                    pageIndexActivity
                                                )
                                            } else {
                                                pageIndexActivity = 1
                                                activityIndex++
                                                //updateTimeDataset.remove(0);
                                                if (activityIndex < updateTimeDatasetActivity!!.size) {
                                                    callActivityDataSetRecursive(
                                                        updateTimeDatasetActivity!![activityIndex].name!!,
                                                        pageIndexActivity
                                                    )
                                                } else {
                                                    try {
                                                        val mDatabase = WelcomeListDatatbase()
                                                        mDatabase.welcomeList = mDatabaseWelcomeList
                                                        mDatabase.id = 1
                                                        getMyDatabase()?.dbDAO?.insertActivityList(
                                                            mDatabase
                                                        )
                                                    } catch (e: java.lang.Exception) {
                                                        e.printStackTrace()
                                                    }
                                                    //setAllDropdown()
                                                    try {
                                                        mainScope.launch {
                                                            delay(100)
                                                            isCitationDatasetExits()
                                                        }
                                                    } catch (e: java.lang.Exception) {
                                                        e.printStackTrace()
                                                        printLog("kalyani_db", "error")
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        showCustomAlertDialog(
                                            mContext,
                                            APIConstant.POST_CITATION_DATASET,
                                            getString(R.string.err_msg_something_went_wrong),
                                            getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel),
                                            this
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                printLog("kalyani", "new")
                                e.printStackTrace()
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_IMAGE, true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status!!) {
                                    if (UploadActivityImages) {
                                        imageUploadSuccessCount++
                                        mImages.add(responseModel.data!![0].response!!.links!![0])
                                        if (bannerList != null && imageUploadSuccessCount == bannerList!!.size) {
                                            UploadActivityImages = false
                                            saveActivityAPI()
                                        }
                                    } else if (responseModel.data != null && responseModel.data!!.size > 0 &&
                                        responseModel.data!![0].response != null && responseModel.data!![0].response!!.links != null
                                        && responseModel.data!![0].response!!.links!!.size > 0
                                    ) {
                                        try {
                                            getMyDatabase()?.dbDAO?.deleteTempImagesOfflineWithId(
                                                offlineCitationImagesList?.get(
                                                    imageUploadSuccessCount
                                                )?.id.toString().nullSafety()
                                            )
                                        } catch (e: java.lang.Exception) {
                                            e.printStackTrace()
                                        }
                                        imageUploadSuccessCount++
                                        mImages.add(responseModel.data!![0].response!!.links!![0])
                                        if (imageUploadSuccessCount == offlineCitationImagesList!!.size) {
                                            callCreateTicketApi(offlineCitationData!!)
                                        }
                                    } else {
                                        showCustomAlertDialog(
                                            mContext,
                                            APIConstant.POST_IMAGE,
                                            getString(R.string.err_msg_something_went_wrong_imagearray),
                                            getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel),
                                            this
                                        )
                                    }
                                } else {
                                    dismissLoader()
                                    showCustomAlertDialog(
                                        mContext,
                                        APIConstant.POST_IMAGE,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }

                        if (tag.equals(API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES, true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status!!) {
                                    if (responseModel.data != null && responseModel.data!!.size > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response!!.links != null && responseModel.data!![0].response!!.links!!.size > 0) {

                                        //Code for sequential API call image upload
                                        mImagesForTiming.add(responseModel.data!![0].response!!.links!![0])
                                        if (mImagesForTiming.size.nullSafety() == timingBannerList?.size.nullSafety()) {
                                            removeTimingImagesFromFolder()
                                            getMyDatabase()?.dbDAO?.deleteTimingImagesWithTimingRecordId(
                                                mAddTimingID
                                            )
                                            timingBannerList?.clear()
                                            callAddTimingsAPI(addTimingDatabaseModel!!)
                                        }

                                    } else {
                                        showCustomAlertDialog(
                                            mContext,
                                            APIConstant.POST_IMAGE,
                                            getString(R.string.err_msg_something_went_wrong_imagearray),
                                            getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel),
                                            this
                                        )
                                    }
                                } else {
                                    dismissLoader()
                                    showCustomAlertDialog(
                                        mContext,
                                        APIConstant.POST_IMAGE,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_CREATE_TICKET, true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateTicketResponse::class.java)

                            try {
                                    ApiLogsClass.writeApiPayloadTex(
                                        BaseApplication.instance?.applicationContext!!,
                                        "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(
                                            responseModel
                                        )
                                    )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (responseModel != null && responseModel.success.nullSafety()) {
                                getMyDatabase()?.dbDAO?.updateCitationUploadStatus(
                                    0,
                                    mCitationNumberId
                                )
                                callTicketCancelApi(
                                    responseModel.data!!.id.toString(),
                                    mCitationNumberId!!.toString()
                                )
                            } else {
                                showCustomAlertDialog(
                                    mContext,
                                    APIConstant.POST_CREATE_TICKET,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this@WelcomeActivity
                                )
                            }
                        }

                        if (tag.equals(
                                DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET,
                                true
                            )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateMunicipalCitationTicketResponse::class.java)

                            try {
                                ApiLogsClass.writeApiPayloadTex(
                                    BaseApplication.instance?.applicationContext!!,
                                    "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(responseModel)
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (responseModel != null && responseModel.success.nullSafety()) {
                                getMyDatabase()?.dbDAO?.updateCitationUploadStatus(
                                    0,
                                    mCitationNumberId
                                )
                                callTicketCancelApi(
                                    responseModel.data!!.id.toString(),
                                    mCitationNumberId!!.toString()
                                )
                            } else {
                                showCustomAlertDialog(
                                    mContext,
                                    APIConstant.POST_CREATE_MUNICIPAL_CITATION_TICKET,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this@WelcomeActivity
                                )
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_CANCEL, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TicketCancelResponse::class.java)

                            try {
                                ApiLogsClass.writeApiPayloadTex(
                                    BaseApplication.instance?.applicationContext!!,
                                    " RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(responseModel)
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (responseModel != null && responseModel.success!!) {
                                dismissLoader()
                                cancelTicketDataObject!!?.let {
                                    getMyDatabase()!!.dbDAO!!.deleteOfflineRescindCitation(
                                        it
                                    )
                                }
                                getMyDatabase()!!.dbDAO!!.deleteOfflineCancelCitation(
                                    mUploadCitationIdForCancel
                                )
                                LogUtil.printToastMSG(this@WelcomeActivity, responseModel.msg)
                            } else {
                                dismissLoader()
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_ADD_TIMING, true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddTimingResponse::class.java)

                            if (responseModel != null && responseModel.success.nullSafety()) {
                                getMyDatabase()?.dbDAO?.updateTimingUploadStatus(0, mAddTimingID)
                                if (getMyDatabase()?.dbDAO?.getLocalTimingDataList()?.size.nullSafety() > 0) {
                                    uploadOfflineTimingsToServer()
                                }
                            } else {
                                showCustomAlertDialog(
                                    mContext,
                                    getString(R.string.scr_lbl_add_time_record),
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this@WelcomeActivity
                                )
                            }
                        }

                        if (tag.equals(API_CONSTANT_SIGNATURE_IMAGES, true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                            if (responseModel != null && responseModel.status.nullSafety()) {

                            } else {
                                showCustomAlertDialog(
                                    mContext,
                                    getString(R.string.scr_lbl_add_time_record),
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this@WelcomeActivity
                                )
                            }
                        }

                        if (tag.equals(API_CONSTANT_EQUIPMENT_INVENTORY, true)) {
                            dismissLoader()


                            val equipmentInventoryResponseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), EquipmentInventoryResponse::class.java)


                            if (equipmentInventoryResponseModel != null &&
                                equipmentInventoryResponseModel.isStatus.nullSafety() &&
                                equipmentInventoryResponseModel.data?.isNotEmpty().nullSafety()
                            ) {
                                saveEquipmentInventoryInToDB(equipmentInventoryResponseModel.data?.firstOrNull()?.response)
                            } else {
                                AppUtils.showCustomAlertDialogWithPositiveButton(
                                    mContext,
                                    getString(R.string.err_title_server_error),
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.btn_text_ok),
                                    object : CustomDialogHelper {
                                        override fun onYesButtonClick() {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onNoButtonClick() {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onYesButtonClickParam(msg: String?) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                            }
                        }

                        if (tag.equals(API_CONSTANT_OFFICER_EQUIPMENT_INVENTORY, true)) {
                            dismissLoader()


                            val officerEquipmentHistoryResponseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), OfficerEquipmentHistoryResponse::class.java)


                            if (officerEquipmentHistoryResponseModel != null &&
                                officerEquipmentHistoryResponseModel.success.nullSafety() &&
                                officerEquipmentHistoryResponseModel.data?.isNotEmpty().nullSafety()
                            ) {
                                updateOfficerEquipmentHistoryToDB(
                                    officerEquipmentHistoryResponseModel.data
                                )
                            } else {
                                updateOfficerEquipmentHistoryToDB()
                            }
                        }

                        if (tag.equals(
                                DynamicAPIPath.POST_EQUIPMENT_CHECK_OUT,
                                ignoreCase = true
                            )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), EquipmentCheckInOutResponse::class.java)


                            if (responseModel != null && responseModel.success.nullSafety()) {
                                updateEquipmentInventory(
                                    EQUIPMENT_CHECKED_OUT,
                                    equipmentName.nullSafety(),
                                    equipmentValue.nullSafety()
                                )
                                updateQrScanList()
                            } else {
                                DialogUtils.showInfoDialog(
                                    context = this,
                                    message = responseModel.description.nullSafety(),
                                    icon = R.drawable.ic_report,
                                    callback = { _, _ ->

                                    })
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_EQUIPMENT_CHECK_IN, ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), EquipmentCheckInOutResponse::class.java)


                                if (responseModel != null && responseModel.success.nullSafety()) {
                                    updateEquipmentInventory(
                                        EQUIPMENT_CHECKED_IN,
                                        equipmentName.nullSafety(),
                                        null
                                    )
                                    updateQrScanList()
                                } else {
                                    DialogUtils.showInfoDialog(
                                        context = this,
                                        message = responseModel.description.nullSafety(),
                                        icon = R.drawable.ic_report,
                                        callback = { _, _ ->

                                        })
                                }
                            } catch (e: Exception) {
                                DialogUtils.showInfoDialog(
                                    context = this,
                                    message = e.localizedMessage,
                                    icon = R.drawable.ic_report,
                                    callback = { _, _ ->

                                    })
                            }
                        }
                        if (tag.equals(
                                API_CONSTANT_HEADER_IMAGE_URL_DOWNLOAD,
                                ignoreCase = true
                            )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                if (responseModel.metadata?.firstOrNull()?.url.nullSafety()
                                        .isNotEmpty()
                                ) {
                                    downloadHeaderFooterForFacsimileFromSecureUrl(
                                        true,
                                        responseModel.metadata?.firstOrNull()?.url.nullSafety()
                                    )
                                }
                            }
                            dismissLoader()
                        }

                        if (tag.equals(
                                API_CONSTANT_FOOTER_IMAGE_URL_DOWNLOAD,
                                ignoreCase = true
                            )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                if (responseModel.metadata?.firstOrNull()?.url.nullSafety()
                                        .isNotEmpty()
                                ) {
                                    downloadHeaderFooterForFacsimileFromSecureUrl(
                                        false,
                                        responseModel.metadata?.firstOrNull()?.url.nullSafety()
                                    )
                                }
                            }
                            dismissLoader()
                        }

                        //check Lot
                    } catch (e: Exception) {
                        printLog("kalyani", "new")
                        e.printStackTrace()
                        dismissLoader()
                        //token expires
                        logout(mContext!!)
                    }
                }
            }

            Status.ERROR -> {
                dismissLoader()
//                if(apiResponse!!.status==401)
//                {
//
//                    launchScreen(mContext, RefreshTokenBaseActivity::class.java)
//                    finishAffinity()
//                }
                // send request, wait for response (the simple socket calls are all blocking)
                mResponseTimeEnd = System.currentTimeMillis()
                mResponseTime = mResponseTimeEnd - mRequestTimeStart
                if (tag.equals(
                        DynamicAPIPath.POST_CREATE_TICKET,
                        true
                    ) || tag.equals(DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET, true)
                ) {
                    /*val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateTicketResponse::class.java)


                     mDb?.dbDAO?.updateCitationUploadStatus(0, mCitationNumberId)*/

                } else {
                    printToastMSG(
                        this@WelcomeActivity,
                        getString(R.string.err_msg_connection_was_refused)
                    )
                }
            }

            else -> {}
        }
    }


    //save Citation Layout Databse
    private fun uploadOfflineCitation() {
        uploadOfflineMakeOneCitation().execute()
    }

    /**
     * only get status 1 citation which is fail by API
     * 2 Only preview screen citation
     */
    inner class uploadOfflineMakeOneCitation :
        AsyncTask<Void?, Int?, CitationInsurranceDatabaseModel?>() {
        override fun doInBackground(vararg voids: Void?): CitationInsurranceDatabaseModel? {
            try {
                var mIssuranceModel: List<CitationInsurranceDatabaseModel?>? = ArrayList()
                mIssuranceModel = getMyDatabase()?.dbDAO?.getCitationInsurrance()
                for (i in mIssuranceModel!!.indices) {
                    if (mIssuranceModel[i]!!.formStatus == 1) {
                        mCitationNumberId = mIssuranceModel[i]!!.citationNumber
                        return mIssuranceModel[i]
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        @SuppressLint("WrongThread")
        override fun onPostExecute(result: CitationInsurranceDatabaseModel?) {
            try {
                result?.let {
                    offlineStatus = 1
                    offlineCitationData = result
                    imageUploadSuccessCount = 0
                    callTicketStatusApi(offlineCitationData!!.citationNumber)
//                    uploadOfflineImages(result)
                    //callCreateTicketApi(it)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadOfflineImages(result: CitationInsurranceDatabaseModel) {
        try {
            offlineCitationImagesList = ArrayList()
            offlineCitationImagesList =
                getMyDatabase()?.dbDAO?.getCitationImageOffline(result.citationNumber!!.toString()) as List<CitationImageModelOffline>?
            if (offlineCitationImagesList!!.size == 0) {
                callCreateTicketApi(result)
            } else {
                for (i in offlineCitationImagesList!!.indices) {
                    callUploadImages(
                        File(offlineCitationImagesList!![i].citationImage),
                        i,
                        "CitationImages"
                    )
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /* Call Api For update profile */
    private fun callUploadImages(file: File?, num: Int, folderName: String) {
        if (isInternetAvailable(this@WelcomeActivity)) {
            val requestFile = file!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val files = MultipartBody.Part.createFormData(
                "files",
                if (file != null) file.name else "",
                requestFile
            )
            val mDropdownList: Array<String>
            if (folderName.equals("CitationImages")) {
                mDropdownList = if (file!!.name.contains("_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
                    arrayOf(mCitationNumberId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
                } else {
                    arrayOf(mCitationNumberId + "_" + num)
                }
            } else {
                val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
                mDropdownList = arrayOf(id + "_" + Random().nextInt(100) + "_activity_image")
            }
            val mRequestBodyType =
                RequestBody.create("text/plain".toMediaTypeOrNull(), folderName!!)
//                    RequestBody.create(MediaType.parse("text/plain"), "CitationImages")
            mUploadImageViewModel!!.hitUploadImagesApi(mDropdownList, mRequestBodyType, files)
        } else {
            LogUtil.printToastMSG(
                this@WelcomeActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }

        //SaveTask().execute()
    }


    /* Call Api For Ticket Cancel */
    private fun callTicketStatusApi(citationNumber: String?) {

        if (isInternetAvailable(this@WelcomeActivity)) {
            class checkTicketUploadStatus :
                AsyncTask<Void?, Int?, OfflineCancelCitationModel?>() {
                override fun doInBackground(vararg voids: Void?): OfflineCancelCitationModel? {
                    try {
//                        var cancelTicketData: List<OfflineCancelCitationModel?>? = ArrayList()
//                        cancelTicketData = mDb?.dbDAO?.getOfflineCancelCitation()
//                        for (i in cancelTicketData!!.indices) {
//                            return cancelTicketData[i]
//                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    return null
                }

                @SuppressLint("WrongThread")
                override fun onPostExecute(result: OfflineCancelCitationModel?) {
                    try {
//                        if(result!=null) {
//                            Log.i("==>Offline:", "Called${ObjectMapperProvider.instance.writeValueAsString(result)}")
                        val ticketUploadStatusRequest = TicketUploadStatusRequest()
                        ticketUploadStatusRequest.citationNumber = citationNumber

                        mTicketStatusViewModel?.getTicketStatusApi(
                            ticketUploadStatusRequest,
                            citationNumber
                        )

//                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            checkTicketUploadStatus().execute()
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For citation layout details */
    private fun callCreateTicketApi(mIssuranceModel: CitationInsurranceDatabaseModel) {
        if (isInternetAvailable(this@WelcomeActivity)) {
            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel == null) {
                val createTicketRequest = CreateTicketRequest()
                val locationDetails = LocationDetails()
                locationDetails.street = mIssuranceModel.citationData!!.location!!.street
                locationDetails.street_lookup_code =
                    mIssuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
                locationDetails.block = mIssuranceModel.citationData!!.location!!.block
                locationDetails.side = mIssuranceModel.citationData!!.location!!.side
                locationDetails.meter = mIssuranceModel.citationData!!.location!!.meterName
                locationDetails.direction = mIssuranceModel.citationData!!.location!!.direction
                locationDetails.lot = mIssuranceModel.citationData!!.location!!.lot
                createTicketRequest.locationDetails = locationDetails
                val vehicleDetails = VehicleDetails()
                vehicleDetails.body_style = mIssuranceModel.citationData!!.vehicle!!.bodyStyle
                vehicleDetails.body_style_lookup_code =
                    mIssuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
                vehicleDetails.decal_year = mIssuranceModel.citationData!!.vehicle!!.decalYear
                vehicleDetails.decal_number = mIssuranceModel.citationData!!.vehicle!!.decalNumber
                vehicleDetails.vin_number = mIssuranceModel.citationData!!.vehicle!!.vinNumber
                vehicleDetails.make = mIssuranceModel.citationData!!.vehicle!!.make
                vehicleDetails.model_lookup_code =
                    mIssuranceModel?.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
                vehicleDetails.color = mIssuranceModel.citationData!!.vehicle!!.color
                vehicleDetails.model = mIssuranceModel.citationData!!.vehicle!!.model
                vehicleDetails.lprNo = mIssuranceModel.citationData!!.vehicle!!.licensePlate
                vehicleDetails.state = mIssuranceModel.citationData!!.vehicle!!.state
                vehicleDetails.mLicenseExpiry =
                    if (mIssuranceModel.citationData!!.vehicle!!.expiration != null) mIssuranceModel.citationData!!.vehicle!!.expiration else ""
                createTicketRequest.vehicleDetails = vehicleDetails
                val violationDetails = ViolationDetails()
                violationDetails.code =
                    mIssuranceModel.citationData!!.voilation!!.code //mAutoComTextViewCode.getEditableText().toString().trim());
                violationDetails.violation =
                    mIssuranceModel?.citationData?.voilation?.violationCode.nullSafety()
                violationDetails.description =
                    mIssuranceModel.citationData!!.voilation!!.locationDescr
                try {
                    violationDetails.fine =
                        mIssuranceModel.citationData!!.voilation!!.amount.nullSafety("0").toDouble()
                    violationDetails.late_fine =
                        mIssuranceModel.citationData!!.voilation!!.amountDueDate.nullSafety("0")
                            .toDouble()
                    violationDetails.due_15_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate.nullSafety("0")
                            .toDouble()
                    violationDetails.due_30_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate30.nullSafety("0")
                            .toDouble()
                    violationDetails.due_45_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate45.nullSafety("0")
                            .toDouble()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                createTicketRequest.violationDetails = violationDetails

                try {
                    val invoiceFeeStructure = InvoiceFeeStructure()
                    invoiceFeeStructure.mSaleTax =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateCost != null && !mIssuranceModel?.citationData?.voilation?.dueDateCost.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety("0")
                            .toDouble() else 0.0
                    invoiceFeeStructure.mCitationFee =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateCitationFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.nullSafety("0")
                            .toDouble() else 0.0
                    invoiceFeeStructure.mParkingFee =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateParkingFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.nullSafety("0")
                            .toDouble() else 0.0
                    createTicketRequest.invoiceFeeStructure = invoiceFeeStructure
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val officerDetails = OfficerDetails()
                //officerDetails.setOfficerId(mData.getSiteOfficerId());
                officerDetails.badgeId = mIssuranceModel.citationData!!.officer!!.badgeId
                officerDetails.officer_lookup_code =
                    mIssuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
                officerDetails.officer_name = AppUtils.getOfficerName(
                    mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                        .nullSafety()
                )
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true)
                ) {
                    officerDetails.peo_fname = AppUtils.getPOEName(
                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                            .nullSafety(), 0
                    )
                    officerDetails.peo_lname = AppUtils.getPOEName(
                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                            .nullSafety(), 1
                    )
                    officerDetails.peo_name =
                        officerDetails.peo_fname + ", " + officerDetails.peo_lname
                }
                officerDetails.signature = ""
                try {
                    officerDetails.squad = mIssuranceModel.citationData!!.officer!!.squad
                    officerDetails.beat = mIssuranceModel.citationData!!.officer!!.beat
                    officerDetails.agency = mIssuranceModel.citationData!!.officer!!.agency
                    officerDetails.mShift = mIssuranceModel.citationData!!.officer!!.shift
                    officerDetails.zone =
                        if (mIssuranceModel.citationData!!.location!!.pcbZone != null)
                            mIssuranceModel.citationData!!.location!!.pcbZone else
                            if (mIssuranceModel.citationData!!.officer!!.zone != null)
                                mIssuranceModel.citationData!!.officer!!.zone else ""

                    val welcomeForm: WelcomeForm? = getMyDatabase()!!.dbDAO!!.getWelcomeForm()
//                officerDetails.mDdeviceId = welcomeForm!!.officerDeviceId
                    officerDetails.mDdeviceId = welcomeForm!!.officerDeviceName
                    officerDetails.mDdeviceFriendlyName = welcomeForm!!.officerDeviceName
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                val commentsDetails = CommentsDetails()
                commentsDetails.note1 = mIssuranceModel.citationData!!.locationNotes
                commentsDetails.note2 = mIssuranceModel.citationData!!.locationNotes1
                commentsDetails.remark1 = mIssuranceModel.citationData!!.locationRemarks
                commentsDetails.remark2 = mIssuranceModel.citationData!!.locationRemarks1
                createTicketRequest.commentsDetails = commentsDetails
                createTicketRequest.officerDetails = officerDetails
                val headerDetails = HeaderDetails()
                headerDetails.citationNumber = mIssuranceModel.citationData!!.ticketNumber
                headerDetails.timestamp = mIssuranceModel.citationData!!.ticketDate
                createTicketRequest.headerDetails = headerDetails
                createTicketRequest.lprNumber =
                    mIssuranceModel.citationData!!.vehicle!!.licensePlate
                createTicketRequest.code =
                    mIssuranceModel.citationData!!.voilation!!.code //mIssuranceModel.getCitationData().getCode());
                createTicketRequest.hearingDate =
                    mIssuranceModel?.citationData?.hearingDate
                createTicketRequest.ticketNo = mIssuranceModel.citationData!!.ticketNumber
                createTicketRequest.type = mIssuranceModel.citationData!!.ticketType
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,true)){
                createTicketRequest.timeLimitEnforcementObservedTime =
                    mIssuranceModel?.citationData?.officer?.observationTime
//            }else {
//                createTicketRequest.timeLimitEnforcementObservedTime =  sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
//            }
                createTicketRequest.imageUrls = mImages
                createTicketRequest.notes = mIssuranceModel.citationData!!.locationNotes
                createTicketRequest.status = "Valid"
                createTicketRequest.citationStartTimestamp =
                    mIssuranceModel.citationData!!.startTime
                createTicketRequest.citationIssueTimestamp =
                    mIssuranceModel.citationData!!.issueTime
                createTicketRequest.isReissue = false
                mCreateTicketViewModel!!.hitCreateTicketApi(createTicketRequest)

                try {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "------------WELCOME Create API-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(createTicketRequest)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val createTicketRequest = CreateMunicipalCitationTicketRequest()
                val locationDetails = MunicipalCitationLocationDetails()
                locationDetails.street = mIssuranceModel.citationData!!.location!!.street
                locationDetails.street_lookup_code =
                    mIssuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
                locationDetails.block = mIssuranceModel.citationData!!.location!!.block
                locationDetails.side = mIssuranceModel.citationData!!.location!!.side
                locationDetails.meter = mIssuranceModel.citationData!!.location!!.meterName
                locationDetails.direction = mIssuranceModel.citationData!!.location!!.direction
                locationDetails.lot = mIssuranceModel.citationData!!.location!!.lot
                createTicketRequest.locationDetails = locationDetails
                val vehicleDetails = MunicipalCitationVehicleDetails()
                vehicleDetails.body_style = mIssuranceModel.citationData!!.vehicle!!.bodyStyle
                vehicleDetails.body_style_lookup_code =
                    mIssuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
                vehicleDetails.decal_year = mIssuranceModel.citationData!!.vehicle!!.decalYear
                vehicleDetails.decal_number = mIssuranceModel.citationData!!.vehicle!!.decalNumber
                vehicleDetails.vin_number = mIssuranceModel.citationData!!.vehicle!!.vinNumber
                vehicleDetails.make = mIssuranceModel.citationData!!.vehicle!!.make
                vehicleDetails.model_lookup_code =
                    mIssuranceModel?.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
                vehicleDetails.color = mIssuranceModel.citationData!!.vehicle!!.color
                vehicleDetails.model = mIssuranceModel.citationData!!.vehicle!!.model
                vehicleDetails.lprNo = mIssuranceModel.citationData!!.vehicle!!.licensePlate
                vehicleDetails.state = mIssuranceModel.citationData!!.vehicle!!.state
                vehicleDetails.mLicenseExpiry =
                    if (mIssuranceModel.citationData!!.vehicle!!.expiration != null) mIssuranceModel.citationData!!.vehicle!!.expiration else ""
                createTicketRequest.vehicleDetails = vehicleDetails
                val violationDetails = MunicipalCitationViolationDetails()
                violationDetails.code =
                    mIssuranceModel.citationData!!.voilation!!.code //mAutoComTextViewCode.getEditableText().toString().trim());
                violationDetails.violation =
                    mIssuranceModel?.citationData?.voilation?.violationCode.nullSafety()
                violationDetails.description =
                    mIssuranceModel.citationData!!.voilation!!.locationDescr
                try {
                    violationDetails.fine =
                        mIssuranceModel.citationData!!.voilation!!.amount.nullSafety("0").toDouble()
                    violationDetails.late_fine =
                        mIssuranceModel.citationData!!.voilation!!.amountDueDate.nullSafety("0")
                            .toDouble()
                    violationDetails.due_15_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate.nullSafety("0")
                            .toDouble()
                    violationDetails.due_30_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate30.nullSafety("0")
                            .toDouble()
                    violationDetails.due_45_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate45.nullSafety("0")
                            .toDouble()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                createTicketRequest.violationDetails = violationDetails

                val motoristDetails = MotoristDetailsModel()
                motoristDetails.motoristFirstName =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstName.nullSafety().uppercase()
                motoristDetails.motoristMiddleName =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleName.nullSafety().uppercase()
                motoristDetails.motoristLastName =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristLastName.nullSafety().uppercase()
                motoristDetails.motoristDateOfBirth =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirth.nullSafety().uppercase()
                motoristDetails.motoristDlNumber =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumber.nullSafety().uppercase()
                motoristDetails.motoristAddressBlock =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlock.nullSafety().uppercase()
                motoristDetails.motoristAddressStreet =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreet.nullSafety().uppercase()
                motoristDetails.motoristAddressCity =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCity.nullSafety().uppercase()
                motoristDetails.motoristAddressState =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressState.nullSafety().uppercase()
                motoristDetails.motoristAddressZip =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZip.nullSafety().uppercase()

                createTicketRequest.motoristDetails = motoristDetails

                try {
                    val invoiceFeeStructure = MunicipalCitationInvoiceFeeStructure()
                    invoiceFeeStructure.mSaleTax =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateCost != null && !mIssuranceModel?.citationData?.voilation?.dueDateCost.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety("0")
                            .toDouble() else 0.0
                    invoiceFeeStructure.mCitationFee =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateCitationFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.nullSafety("0")
                            .toDouble() else 0.0
                    invoiceFeeStructure.mParkingFee =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateParkingFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.nullSafety("0")
                            .toDouble() else 0.0
                    createTicketRequest.invoiceFeeStructure = invoiceFeeStructure
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val officerDetails = MunicipalCitationOfficerDetails()
                //officerDetails.setOfficerId(mData.getSiteOfficerId());
                officerDetails.badgeId = mIssuranceModel.citationData!!.officer!!.badgeId
                officerDetails.officer_lookup_code =
                    mIssuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
                officerDetails.officer_name = AppUtils.getOfficerName(
                    mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                        .nullSafety()
                )
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true)
                ) {
                    officerDetails.peo_fname = AppUtils.getPOEName(
                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                            .nullSafety(), 0
                    )
                    officerDetails.peo_lname = AppUtils.getPOEName(
                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                            .nullSafety(), 1
                    )
                    officerDetails.peo_name =
                        officerDetails.peo_fname + ", " + officerDetails.peo_lname
                }
                officerDetails.signature = ""
                try {
                    officerDetails.squad = mIssuranceModel.citationData!!.officer!!.squad
                    officerDetails.beat = mIssuranceModel.citationData!!.officer!!.beat
                    officerDetails.agency = mIssuranceModel.citationData!!.officer!!.agency
                    officerDetails.mShift = mIssuranceModel.citationData!!.officer!!.shift
                    officerDetails.zone =
                        if (mIssuranceModel.citationData!!.location!!.pcbZone != null)
                            mIssuranceModel.citationData!!.location!!.pcbZone else
                            if (mIssuranceModel.citationData!!.officer!!.zone != null)
                                mIssuranceModel.citationData!!.officer!!.zone else ""

                    val welcomeForm: WelcomeForm? = getMyDatabase()!!.dbDAO!!.getWelcomeForm()
//                officerDetails.mDdeviceId = welcomeForm!!.officerDeviceId
                    officerDetails.mDdeviceId = welcomeForm!!.officerDeviceName
                    officerDetails.mDdeviceFriendlyName = welcomeForm!!.officerDeviceName
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                val commentsDetails = MunicipalCitationCommentsDetails()
                commentsDetails.note1 = mIssuranceModel.citationData!!.locationNotes
                commentsDetails.note2 = mIssuranceModel.citationData!!.locationNotes1
                commentsDetails.remark1 = mIssuranceModel.citationData!!.locationRemarks
                commentsDetails.remark2 = mIssuranceModel.citationData!!.locationRemarks1
                createTicketRequest.commentsDetails = commentsDetails
                createTicketRequest.officerDetails = officerDetails
                val headerDetails = MunicipalCitationHeaderDetails()
                headerDetails.citationNumber = mIssuranceModel.citationData!!.ticketNumber
                headerDetails.timestamp = mIssuranceModel.citationData!!.ticketDate
                createTicketRequest.headerDetails = headerDetails
                createTicketRequest.lprNumber =
                    mIssuranceModel.citationData!!.vehicle!!.licensePlate
                createTicketRequest.code =
                    mIssuranceModel.citationData!!.voilation!!.code //mIssuranceModel.getCitationData().getCode());
                createTicketRequest.hearingDate =
                    mIssuranceModel?.citationData?.hearingDate
                createTicketRequest.ticketNo = mIssuranceModel.citationData!!.ticketNumber
                createTicketRequest.type = mIssuranceModel.citationData!!.ticketType
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,true)){
                createTicketRequest.timeLimitEnforcementObservedTime =
                    mIssuranceModel?.citationData?.officer?.observationTime
//            }else {
//                createTicketRequest.timeLimitEnforcementObservedTime =  sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
//            }
                createTicketRequest.imageUrls = mImages
                createTicketRequest.notes = mIssuranceModel.citationData!!.locationNotes
                createTicketRequest.status = "Valid"
                createTicketRequest.citationStartTimestamp =
                    mIssuranceModel.citationData!!.startTime
                createTicketRequest.citationIssueTimestamp =
                    mIssuranceModel.citationData!!.issueTime
                createTicketRequest.isReissue = false
                mCreateMunicipalCitationTicketViewModel?.hitCreateMunicipalCitationTicketApi(
                    createTicketRequest
                )

                try {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "------------WELCOME Create Municipal Citation API-----------------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(createTicketRequest)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            getMyDatabase()?.dbDAO?.updateCitationUploadStatus(1, mIssuranceModel.citationNumber)
            //LogUtil.printToastMSG(LprPreviewActivity.this, getString(R.string.err_msg_connection_was_refused));
        }
    }

    /* Call Api For Ticket Cancel */
    private fun callTicketCancelApi(mTicketId: String, mCitationNumber: String) {
        if (isInternetAvailable(this@WelcomeActivity)) {
            class UploadCancelTicket :
                AsyncTask<Void?, Int?, OfflineCancelCitationModel?>() {
                override fun doInBackground(vararg voids: Void?): OfflineCancelCitationModel? {
                    try {
                        var cancelTicketDataList: List<OfflineCancelCitationModel?>? = ArrayList()
//                    var cancelTicketDataObject: OfflineCancelCitationModel?
                        if (mTicketId.toString().isEmpty()) {
                            cancelTicketDataList =
                                getMyDatabase()?.dbDAO?.getOfflineCancelCitation()
                            for (i in cancelTicketDataList!!.indices) {
                                if (cancelTicketDataList[i]!!.ticketNumber!!.equals(mCitationNumber)) {
                                    return cancelTicketDataList[i]
                                } else if (cancelTicketDataList[i]!!.uploadedCitationId!!.toString()
                                        .isNotEmpty()
                                ) {
                                    return cancelTicketDataList[i]
                                }
                            }
                        } else {
//
                            cancelTicketDataList =
                                getMyDatabase()?.dbDAO?.getOfflineCancelCitation()
//                        cancelTicketDataList = mDb?.dbDAO?.getOfflineCancelCitation("610023202")
                            for (i in cancelTicketDataList!!.indices) {
                                if (cancelTicketDataList[i]!!.ticketNumber!!.equals(mCitationNumber) &&
                                    cancelTicketDataList[i]!!.uploadedCitationId!!.equals("none")
                                ) {
                                    cancelTicketDataList!!.get(i)!!.uploadedCitationId = mTicketId
                                    return cancelTicketDataList[i]
                                }
                            }

//                        return cancelTicketDataList!!.get(0)!!
                        }

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    return null
                }

                @SuppressLint("WrongThread")
                override fun onPostExecute(result: OfflineCancelCitationModel?) {
                    try {
                        if (result != null) {
                            cancelTicketDataObject = result
                            if (!result!!.uploadedCitationId.toString().trim().isEmpty() &&
                                !result!!.uploadedCitationId.toString()
                                    .equals("none", ignoreCase = true)
                            ) {
                                Log.i("==>Offline:", "Called${ObjectMapperProvider.instance.writeValueAsString(result)}")
                                val ticketCancelRequest = TicketCancelRequest()
                                ticketCancelRequest.status = result!!.status.toString()
                                ticketCancelRequest.mNote = result!!.note.toString()
                                ticketCancelRequest.mReason = result!!.reason.toString()
                                ticketCancelRequest.mType = result!!.type.toString()
                                mUploadCitationIdForCancel = result!!.uploadedCitationId.toString()

                                mTicketCancelViewModel?.hitTicketCancelApi(
                                    ticketCancelRequest,
                                    result!!.uploadedCitationId.toString()
                                )

                                try {
                                    ApiLogsClass.writeApiPayloadTex(
                                        BaseApplication.instance?.applicationContext!!,
                                        "------------WELCOME TICKET  Cancelled API-----------------"
                                    )
                                    ApiLogsClass.writeApiPayloadTex(
                                        BaseApplication.instance?.applicationContext!!,
                                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(ticketCancelRequest)
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            UploadCancelTicket().execute()
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun uploadOfflineTimingsToServer() {
        class SaveTimingsToDBTask :
            AsyncTask<Void?, Int?, AddTimingDatabaseModel?>() {
            override fun doInBackground(vararg voids: Void?): AddTimingDatabaseModel? {
                try {
                    var addTimingDataModel: List<AddTimingDatabaseModel?>? = ArrayList()
                    addTimingDataModel = getMyDatabase()?.dbDAO?.getLocalTimingDataList()
                    for (i in addTimingDataModel!!.indices) {
                        if (addTimingDataModel[i]!!.formStatus == 1) {
                            mAddTimingID = addTimingDataModel[i]!!.id
                            return addTimingDataModel[i]
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                return null
            }

            @SuppressLint("WrongThread")
            override fun onPostExecute(result: AddTimingDatabaseModel?) {
                try {
                    Log.i("==>Offline:", "Called${ObjectMapperProvider.instance.writeValueAsString(result)}")
                    addTimingDatabaseModel = result
                    timingBannerList?.clear()
                    mImagesForTiming.clear()

                    getMyDatabase()?.dbDAO?.getTimingImageUsingTimingRecordId(mAddTimingID)
                        ?.let { timingBannerList?.addAll(it) }
                    if (timingBannerList?.isNotEmpty().nullSafety()) {
                        callUploadAllImages()
                    } else {
                        result?.let { callAddTimingsAPI(it) }
                    }

                    //result?.let { callAddTimingsAPI(it) }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        SaveTimingsToDBTask().execute()


        //TODO JANAK : What can be used instead of AsyncTask as it is deprecated & Old
//        fun saveTimingsToDB() {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val addTimingDataModel = getMyDatabase()?.dbDAO?.getLocalTimingDataList()
//                    var timingModel: AddTimingDatabaseModel? = null
//                    var images: List<TimingImage> = emptyList()
//
//                    for (item in addTimingDataModel.orEmpty()) {
//                        if (item?.formStatus == 1) {
//                            timingModel = item
//                            images = getMyDatabase()?.dbDAO?.getTimingImageUsingTimingRecordId(item.id) ?: emptyList()
//                            break
//                        }
//                    }
//
//                    withContext(Dispatchers.Main) {
//                        addTimingDatabaseModel = timingModel
//                        timingBannerList?.clear()
//                        timingBannerList?.addAll(images)
//                        mImagesForTiming.clear()
//
//                        if (timingBannerList?.isNotEmpty().nullSafety()) {
//                            callUploadAllImages()
//                        } else {
//                            timingModel?.let { callAddTimingsAPI(it) }
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }

    }

    private fun removeTimingImagesFromFolder() {
        try {
            if (timingBannerList?.size.nullSafety() > 0) {
                timingBannerList?.forEach {
                    val oldFile = File(it?.timingImage)
                    if (oldFile.exists()) oldFile.delete()
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun callUploadAllImages() {
        if (isInternetAvailable(this@WelcomeActivity)) {
//            val files: MutableList<MultipartBody.Part?> = ArrayList()
//            val fileNames: Array<String?> = Array()
            for (i in timingBannerList?.indices!!) {
                val file = File(timingBannerList!![i]?.timingImage.nullSafety())
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
                val mPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "files",
                    if (file != null) file.name else "",
                    requestFile
                )

                val fileNames = arrayOf(FileUtil.getFileNameWithoutExtension(file.name))

                //Code for one by one upload
                val mRequestBodyType = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES
                )
                mUploadImageViewModel?.hitUploadTimingImagesApi(fileNames, mRequestBodyType, mPart)
            }
        } else {
            LogUtil.printToastMSG(
                this@WelcomeActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For citation layout details */
    private fun callAddTimingsAPI(addTimingDatabaseModel: AddTimingDatabaseModel) {
        if (isInternetAvailable(this@WelcomeActivity)) {
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
            mAddTimingViewModel?.hitAddTimingApi(mAddTimingRequest)
        }
    }

    private fun getGeoAddress() {
        ioScope.launch {
            try {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!
                    .toDouble()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!
                    .toDouble()
                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(mContext!!, Locale.getDefault())
                addresses = geocoder.getFromLocation(
                    mLat,
                    mLong,
                    1
                )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                val address: String =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                val separated = address.split(",").toTypedArray()
                printAddress = separated[0] // this will contain "Fruit"
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            mainScope.async {
                try {
                    val mAdd = if (printAddress.split(" ").toTypedArray().size > 1)
                        printAddress.split(" ").toTypedArray()[0]
                    else
                        printAddress

//                    mRoundOfAddress = mAdd[0].toString() + ""
                    mRoundOfAddress = AppUtils.roundOfBlock(mAdd)
                    mAutoComTextViewBlock?.setText(mRoundOfAddress)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    mAutoComTextViewBlock?.setText(
                        if (printAddress.split(" ").toTypedArray().size > 1)
                            printAddress.split(" ").toTypedArray()[0]
                        else
                            printAddress
                    )
                }
                try {
                    val count = printAddress.split(" ").toTypedArray().size
                    if (count > 2) {
                        mAutoComTextViewStreet!!.setText(
                            if (count > 0)
                                printAddress.split(Regex(" "), count - 1.coerceAtLeast(1))
                                    .toTypedArray().get(1)
                            else
                                printAddress
                        )
                    } else {
                        mAutoComTextViewStreet!!.setText(
                            if (count > 1) printAddress.split(" ")
                                .toTypedArray()[1] else printAddress
                        )
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        protected const val REQUEST_CHECK_SETTINGS = 0x1
        private const val REQUEST_CAMERA = 0

        // Used in checking for runtime permissions.
        private const val REQUEST_LOCATIONS_PERMISSIONS_REQUEST_CODE = 34
    }


    private fun setScreenResolution() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val topSpace: Double = height * 0.22
    }


    private fun createSignatureImagesNameList(): Array<String?> {
        val imageNameList = arrayOfNulls<String>(1)
//        bannerListForUpload.forEachIndexed { index, scanDataModel ->
//            arrayOf(mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerBadgeId + "_" + "SignatureImages")
        imageNameList!!.set(
            0,
            mWelcomeResponseData!!.data!![0].responsedata!!.user!!.officerBadgeId + "_" + "SignatureImages"
        )
//        }

        return imageNameList
    }

    private fun createSignatureImageMultipart(): List<MultipartBody.Part?> {
        val imageMultipartList = ArrayList<MultipartBody.Part?>()
//        bannerListForUpload.forEach {
//        val tempFile: File = File(mSignaturePath.nullSafety())
        val requestFile =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), fileSignature!!)
        val files = MultipartBody.Part.createFormData(
            "files",
            fileSignature?.name,
            requestFile
        )
        imageMultipartList.add(files)
//        }

        return imageMultipartList
    }

    private fun callSignatureBulkImageUpload() {
        if (isInternetAvailable(this@WelcomeActivity)) {
            mUploadSignatureImageViewModel!!.callUploadScannedImagesAPI(
                this@WelcomeActivity,
                createSignatureImagesNameList(),
                createSignatureImageMultipart(), API_CONSTANT_SIGNATURE_IMAGES, "PreviewActivity"
            )
        } else {
            LogUtil.printToastMSG(
                this@WelcomeActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
//            saveCitationStatus1()
        }
    }

    private val uploadScannedImagesAPIResponseObserver = Observer<Any> {
        when (it) {
            is Resource.Error<*> -> {
                showToast(context = this@WelcomeActivity, message = it.message.nullSafety())
            }

            is Resource.Success<*> -> {
                val response = it.data as ScannedImageUploadResponse

                when (response.status) {
                    true -> {
                        uploadSignatureLink =
                            response.data.get(0).response!!.links.get(0).toString()
                        if (scanStatus) {
                            callUpdateSiteOfficerApi()
                            insertFormToDb(true, false, null)
                        } else {
                            insertFormToDb(false, false, null)
                            callUpdateSiteOfficerApi()
                        }
                    }

                    else -> {
                        showToast(
                            context = this@WelcomeActivity,
                            message = getString(R.string.err_msg_something_went_wrong)
                        )
                    }
                }
            }

            is Resource.Loading<*> -> {
                it.isLoadingShow.let {
                    if (it as Boolean) {
                        showProgressLoader(getString(R.string.scr_message_please_wait))
                    } else {
//                        dismissLoader()
                    }
                }
            }

            is Resource.NoInternetError<*> -> {
                showToast(context = this@WelcomeActivity, message = it.message.nullSafety())
            }
        }
    }

    /**
     * download officer signature image
     */

    private fun callDownloadBitmapApi(mPrintBitmapLink: String?) {
        if (isInternetAvailable(this@WelcomeActivity)) {
            if (mPrintBitmapLink != null && !mPrintBitmapLink!!.isEmpty() && mPrintBitmapLink!!.length > 5) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = "SignatureImages"
                val links = Links()
                links.img1 = mPrintBitmapLink
                downloadBitmapRequest.links = links
                mDownloadBitmapFIleViewModel?.downloadBitmapAPI(downloadBitmapRequest)
            } else {
                LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_down_load_image)
                )
            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /**
     * Function used to get download header footer link from secure URL
     */
    private fun callGetHeaderFooterDownloadLinkAPI(isHeader: Boolean, mPrintBitmapLink: String?) {
        if (isInternetAvailable(this@WelcomeActivity)) {
            if (mPrintBitmapLink != null && mPrintBitmapLink.isNotEmpty()) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = "LogoImage"
                val links = Links()
                links.img1 = mPrintBitmapLink
                downloadBitmapRequest.links = links
                if (isHeader) {
                    mDownloadBitmapFIleViewModel?.downloadHeaderImageURL(downloadBitmapRequest)
                } else {
                    mDownloadBitmapFIleViewModel?.downloadFooterImageURL(downloadBitmapRequest)
                }
            } else {
                LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_down_load_image)
                )
            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    inner class DownloadingPrintBitmapFromUrl : AsyncTask<String?, Int?, String?>() {
        public override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg url: String?): String? {
            if (!TextUtils.isEmpty(url[0])) {
                try {
//                    val mydir = File(
//                        Environment.getExternalStorageDirectory().absolutePath,
//                        Constants.FILE_NAME + Constants.SIGNATURE
//                    )
                    val mydir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Constants.FILE_NAME + Constants.SIGNATURE
                    )
                    mydir.deleteRecursively()
                    if (!mydir.exists()) {
                        mydir.mkdirs()
                    }
                    val file = File(mydir.absolutePath, getSignatureFileNameWithExt())
                    if (file.exists()) file.delete()
                    val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val downloadUri = Uri.parse(url[0])
                    val request = DownloadManager.Request(downloadUri)
                    request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                    )
                        .setAllowedOverRoaming(false)
                        .setTitle("Downloading")
//                        .setDestinationInExternalPublicDir(
//                            Constants.FILE_NAME + "" + Constants.CAMERA,
//                            getSignatureFileNameWithExt()
//                        )
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            Constants.FILE_NAME + Constants.SIGNATURE + File.separator + getSignatureFileNameWithExt()
                        )
                    manager.enqueue(request)
                    MediaScannerConnection.scanFile(
                        this@WelcomeActivity, arrayOf<String>(file.toString()), null
                    ) { path, uri -> }
                    return mydir.absolutePath + File.separator + getSignatureFileNameWithExt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                return ""
            }
            return ""
        }

        public override fun onPostExecute(s: String?) {
            try {
                val handler = Handler(Looper.getMainLooper())
                val runnable: java.lang.Runnable = object : java.lang.Runnable {
                    override fun run() {
                        imageViewSignature.post {
                            val mydir = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                Constants.FILE_NAME + Constants.SIGNATURE
                            )
                            fileSignature = File(mydir.absolutePath, getSignatureFileNameWithExt())

//                            mSignaturePath = sharedPreference.read(SharedPrefKey.FILE_PATH, "").toString()
//                            val imageName = getSignatureFileNameWithExt()
//                            mSignaturePath = mSignaturePath + Constants.SIGNATURE + "/" + imageName
//                            fileSignature = File(mSignaturePath)
                            if (fileSignature!!.exists()) {
                                imageViewSignature.setImageURI(Uri.fromFile(fileSignature))
                            }
                        }
                    }
                }
                handler.postDelayed(runnable, 300)

            } catch (e: Exception) {
                LogUtil.printLog("error mesg", e.message)
            }
            super.onPostExecute(s)
        }
    }

    private fun checkPermission(): Boolean {
        val READ_EXTERNAL_PERMISSION =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                TicketDetailsActivity.PERMISSION_WRITE
            )
            return false
        }
        return true
    }

    private fun uploadActivityImages() {
        if (bannerList != null && bannerList!!.size > 0) {
            mImages.clear()
            UploadActivityImages = true
            imageUploadSuccessCount = 0
            for (i in bannerList!!.indices) {
                callUploadImages(File(bannerList!!.get(i)!!.timingImage), i, "ActivityImages")
            }
        } else {
            UploadActivityImages = false
            mImages.clear()
            saveActivityAPI()
        }
    }

    private fun launchCameraIntent() {
        val mImageCount = bannerList?.size.nullSafety()
        val maxImageCount = AppUtils.maxImageCount(SETTING_MAX_IMAGES_COUNT)
        if (mImageCount < maxImageCount) {
            val picUri: Uri? = getOutputPhotoFile()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri)
            startActivityForResult(intent, REQUEST_CAMERA)
        } else {
            LogUtil.printToastMSGForErrorWarning(
                applicationContext,
                getString(R.string.msg_max_image).replace("#", maxImageCount.toString())
            )
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    ioScope.launch {
                        if (tempUri == null) {
                            getOutputPhotoFile()
                        }
                        mViewPagerBanner.post {
                            mViewPagerBanner.visibility = View.VISIBLE
                        }
                        val file = File("$tempUri/IMG_temp.jpg")

                        val options = BitmapFactory.Options()
                        options.inSampleSize = 8
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(file.absolutePath, options)

                        // Calculate inSampleSize
                        options.inSampleSize = Util.calculateInSampleSize(options, 400, 400)

                        // Decode bitmap with inSampleSize set
                        options.inJustDecodeBounds = false
                        val weakScaledBitmap = WeakReference<Bitmap>(
                            BitmapFactory.decodeFile(
                                file.absolutePath,
                                options
                            )
                        )

                        //var scaledBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

                        //check the rotation of the image and display it properly
                        val exif: ExifInterface
                        exif = ExifInterface(file.absolutePath)
                        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                        val matrix = Matrix()
                        if (orientation == 6) {
                            matrix.postRotate(90f)
                        } else if (orientation == 3) {
                            matrix.postRotate(180f)
                        } else if (orientation == 8) {
                            matrix.postRotate(270f)
                        }
                        val mImgaeBitmap = (weakScaledBitmap.get()?.let {
                            Bitmap.createBitmap(
                                it,
                                0,
                                0,
                                it.width.nullSafety(),
                                it.height.nullSafety(),
                                matrix,
                                true
                            )
                        })

                        val timeStampBitmap =
                            mImgaeBitmap?.let { AppUtils.timestampItAndSave(it) };
                        saveImageMM(timeStampBitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getOutputPhotoFile(): Uri? {
        val directory = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        tempUri = directory.path
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                LogUtil.printLog("getOutputPhotoFile", "Failed to create storage directory.")
                return null
            }
        }
        val path: Uri
        if (Build.VERSION.SDK_INT > 23) {
            val oldPath = File(directory.path + File.separator + "IMG_temp.jpg")
            var fileUrl = oldPath.path
            if (fileUrl.substring(0, 7).matches(Regex("file://"))) {
                fileUrl = fileUrl.substring(7)
            }
            val file = File(fileUrl)
            path = FileProvider.getUriForFile(mContext!!, this.packageName + ".provider", file)
        } else {
            path = Uri.fromFile(File(directory.path + File.separator + "IMG_temp.jpg"))
        }
        return path
    }

    private fun saveImageMM(finalBitmap: Bitmap?) {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname =
            AppUtils.getSiteId(this@WelcomeActivity) + "_" + mActivityId + "_Image_" + timeStamp + "_capture.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            //new ImageCompression(this,file.getAbsolutePath()).execute(finalBitmap);
            val out = FileOutputStream(file)
            //finalBitmap = Bitmap.createScaledBitmap(finalBitmap,(int)1080/2,(int)1920/2, true);
            finalBitmap?.compress(Bitmap.CompressFormat.JPEG, 45, out) //less than 300 kb
            out.flush()
            out.close()
            val oldFname = "IMG_temp.jpg"
            val oldFile = File(myDir, oldFname)
            if (oldFile.exists()) oldFile.delete()

//            val id = AppUtils.getTimeBasedRandomId()
            val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
            val pathDb = file.path
            val mImage = TimingImagesModel()
            mImage.timingImage = pathDb
            mImage.status = 0
            mImage.id = id.toInt()
            mImage.timingRecordId = id.toInt()
            mImage.deleteButtonStatus = SHOW_DELETE_BUTTON

            bannerList?.add(mImage)

            setCameraImages()

            finalBitmap?.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //set images to viewpager
    private fun setCameraImages() {
        mViewPagerBanner.post {
            //bannerList = mDb?.dbDAO?.getTimingImage()
            if (bannerList?.isNotEmpty().nullSafety()) {
                showImagesBanner(bannerList!!)
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
            this@WelcomeActivity,
            object : TimingViewPagerBannerAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
                    bannerList?.removeAt(position)
                    //mDb?.dbDAO?.deleteTempTimingImagesWithId(bannerList!![position]!!.id)
                    setCameraImages()
                    mBannerAdapter?.notifyDataSetChanged()
                }
            })
    }

    private fun showImagesBanner(mImageList: List<TimingImagesModel?>?) {
//        mList.clear()
//        mList.addAll(mImageList as MutableList<TimingImagesModel>)

        if (mBannerAdapter != null && mImageList!!.size > 0) {
            mBannerAdapter?.setTimingBannerList(mImageList)
            mViewPagerBanner.adapter = mBannerAdapter
            mViewPagerBanner.currentItem = 0
        }
        mViewPagerBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (mImageList!!.size == 0) {
                    return
                }
                try {
                    for (i in mImageList!!.indices) {
                        mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                    }
                    mDots!![position]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        try {
            if (mImageList!!.size > 0 && mBannerAdapter != null) {
                setUiPageViewController(mBannerAdapter?.count.nullSafety())

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //managing view pager ui
    private fun setUiPageViewController(count: Int) {
        try {
            mDotsCount = count
            mDots = arrayOfNulls(mDotsCount)
            pagerIndicator.removeAllViews()
            for (i in 0 until mDotsCount) {
                mDots!![i] = ImageView(this)
                mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                mDots!![i]?.setPadding(8, 0, 8, 0)
                params.setMargins(4, 0, 4, 0)
                pagerIndicator.addView(mDots!![i], params)
            }
            if (mShowBannerCount == 0) {
                mShowBannerCount += 1
            }
            mDots!![mShowBannerCount - 1]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    //save Timing Data form if offline
//    private fun saveActivityLogData(mResponse: ActivityUpdateRequest) {
//        class SaveTask : AsyncTask<Void?, Int?, String>() {
//            override fun doInBackground(vararg voids: Void?): String? {
//                try {
//                    val mSelectedImageFileUriList= ArrayList<String>()
//
//                    val model = ActivityImageTable()
//                    model.activityResponseId = mResponse.siteId
//                    model.id = Random().nextInt(1000)
//                    for(index in bannerList!!.indices){
//                        mSelectedImageFileUriList.add(bannerList!!.get(index)!!.timingImage.toString())
//                    }
//                    model.imagesList = mSelectedImageFileUriList
//                    mDb?.dbDAO?.insertActivityImageData(model)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                return "saved"
//            }
//
//            protected fun onPostExecute(result: CitationNumberDatabaseModel?) {
//                //LogUtil.printToastMSG(mContext,"Booklet saved!");
//            }
//        }
//        SaveTask().execute()
//    }

    /**
     * Function used to get equipment inventory either from backend or local database
     * if local equipment inventory null then it will call API get the same & later on success , it will call officer equipment
     * to sync with out list
     * else, it will call officer's equipment API to get all the equipment which is being used by the officer
     * along with this, it will setup the view pager for equipment inventory
     */
    private fun getEquipmentInventory(officerIdValue: String) {
        officerId = officerIdValue
        if (getMyDatabase()?.dbDAO?.getQrCodeInventoryData().isNullOrEmpty().nullSafety()) {
            if (isInternetAvailable(this@WelcomeActivity)) {
                val mDropdownDatasetRequest = DropdownDatasetRequest()
                mDropdownDatasetRequest.type = DATASET_INVENTORY_REPORT_LIST
                mDropdownDatasetRequest.shard = 1
                mCitationDatasetModel?.callGetEquipmentInventoryAPI(mDropdownDatasetRequest)
            } else {
                dismissLoader()
                printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
        } else {
            callGetOfficerEquipmentInventoryList()
        }

        setEquipmentInventoryListOnUI()
    }

    /**
     * Function to call get officer's equipment API, response contains all the equipments which is
     * checkout by the officer and now on officer's name
     */
    private fun callGetOfficerEquipmentInventoryList() {
        inventoryViewModel?.getOfficerEquipmentList()
    }

    /**
     * This API is used to checkout the equipment for use or checkin the equipment after the use.
     * before calling the API , it checks if the QR value is there in inventory system or not and also it checks
     * if the scanned QR code is already checkout or not.
     * Response contains success or error
     */
    private fun callLogEquipmentCheckInOutAPI(
        from: String,
        scannedKey: String,
        scannedValue: String
    ) {
        val qrInventoryDetailModel = qrCodeInventoryBannerList?.firstOrNull {
            it?.equipmentName.equals(
                scannedKey,
                true
            ) && it?.equipmentValue.equals(scannedValue, true)
        }

        val inventoryDataToShow =
            getMyDatabase()?.dbDAO?.getInventoryToShowData() as MutableList<InventoryToShowTable?>

        equipmentID = qrInventoryDetailModel?.equipmentID
        equipmentName = qrInventoryDetailModel?.equipmentName
        equipmentValue = qrInventoryDetailModel?.equipmentValue

        val equipmentCheckInOutRequest = EquipmentCheckInOutRequest()
        equipmentCheckInOutRequest.equipmentID = equipmentID

        if (qrInventoryDetailModel != null) {
            if (from == FROM_EQUIPMENT_CHECKOUT) {
                if (isScannedEquipmentAlreadyCheckedOut(
                        scannedKey,
                        scannedValue,
                        inventoryDataToShow
                    )
                ) {
                    DialogUtils.showInfoDialog(
                        context = this,
                        message = getString(R.string.msg_this_qr_code_is_already_scanned_and_equipment_is_checked_out),
                        icon = R.drawable.ic_report,
                        callback = { _, _ ->
                        })
                } else {
                    if (isScannedEquipmentCategoryAlreadyCheckedOut(
                            scannedKey,
                            inventoryDataToShow
                        )
                    ) {
                        DialogUtils.showInfoDialog(
                            context = this,
                            message = getString(
                                R.string.msg_one_of_the_inventory_is_already_checked_out_please_check_in_that_first,
                                scannedKey,
                                scannedKey
                            ),
                            icon = R.drawable.ic_report,
                            callback = { _, _ ->
                            })
                    } else {
                        inventoryViewModel?.callEquipmentCheckOutAPI(
                            equipmentCheckInOutRequest
                        )
                    }

                }
            } else {
                inventoryViewModel?.callEquipmentCheckInAPI(
                    equipmentCheckInOutRequest
                )
            }
        } else {
            DialogUtils.showInfoDialog(
                context = this,
                message = getString(R.string.msg_the_qr_code_you_scanned_is_not_found_in_the_our_inventory_system),
                icon = R.drawable.ic_report,
                callback = { _, _ ->
                })
        }
    }

    /**
     * Once we get success from get equipment API, it will save all the received equipment in the database for further use.
     */
    private fun saveEquipmentInventoryInToDB(equipmentItemDetailList: List<EquipmentItemDetail>?) {
        getMyDatabase()?.dbDAO?.deleteQrCodeInventoryTable()
        getMyDatabase()?.dbDAO?.deleteInventoryToShowTable()

        equipmentItemDetailList?.forEach { equipmentItemDetail ->
            val qrCodeInventoryTable = QrCodeInventoryTable()
            qrCodeInventoryTable.equipmentID = equipmentItemDetail.id
            qrCodeInventoryTable.equipmentName = equipmentItemDetail.equipmentName
            qrCodeInventoryTable.equipmentValue = equipmentItemDetail.equipmentValue
            qrCodeInventoryTable.required = equipmentItemDetail.isRequired.boolToInt()
            qrCodeInventoryTable.checkedOut = equipmentItemDetail.isCheckedOut.boolToInt()
            qrCodeInventoryTable.lastCheckedOut = equipmentItemDetail.lastCheckedOut
            getMyDatabase()?.dbDAO?.insertQrCodeInventoryData(qrCodeInventoryTable)
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
            getMyDatabase()?.dbDAO?.insertInventoryToShowData(inventoryToShowTable)
        }

        callGetOfficerEquipmentInventoryList()
    }

    /**
     * This used to update our local database for checking & checkout equipments
     */
    private fun updateEquipmentInventory(
        checkStatus: Int,
        equipmentName: String,
        equipmentValue: String?
    ) {
//        mDb?.dbDAO?.updateQrCodeInventoryData(
//            checkStatus,
//            equipmentID.nullSafety()
//        )

        getMyDatabase()?.dbDAO?.updateInventoryToShowDataByName(
            checkStatus,
            equipmentName.nullSafety(),
            equipmentValue.nullSafety()
        )
    }

    /**
     * This used to update our local database for checkin & checkout equipments along with officers equipment list
     */
    private fun updateOfficerEquipmentHistoryToDB(officerEquipmentItemDetailList: List<OfficerEquipmentItemDetail>? = null) {
        if (officerEquipmentItemDetailList != null) {
            qrCodeInventoryBannerList =
                getMyDatabase()?.dbDAO?.getQrCodeInventoryData() as MutableList<QrCodeInventoryTable?>?

//            qrCodeInventoryBannerList?.forEachIndexed { index, qrCodeInventoryTable ->
//                val officerEquipmentModel =
//                    officerEquipmentItemDetailList.firstOrNull { it.equipmentID == qrCodeInventoryTable?.equipmentID }
//
//                if (officerEquipmentModel?.equipmentID.isNullOrEmpty()) {
//                    updateEquipmentInventory(
//                        EQUIPMENT_CHECKED_IN,
//                        qrCodeInventoryTable?.equipmentName.nullSafety(),
//                        qrCodeInventoryTable?.equipmentValue.nullSafety()
//                    )
//                } else {
//                    updateEquipmentInventory(
//                        EQUIPMENT_CHECKED_OUT,
//                        qrCodeInventoryTable?.equipmentName.nullSafety(),
//                        qrCodeInventoryTable?.equipmentValue.nullSafety()
//                    )
//                }
//            }

            inventoryToShowList?.forEachIndexed { index, qrCodeInventoryTable ->
                val officerEquipmentModel =
                    officerEquipmentItemDetailList.firstOrNull {
                        it.equipmentName.equals(
                            qrCodeInventoryTable?.equipmentName,
                            true
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

        setScanAdapter()
    }

    private fun setEquipmentInventoryListOnUI() {
        llQrcode.visibility = View.VISIBLE
        setupEquipmentInventoryBannerAdapter()
    }

    /**
     * This used to update equipment adapter after getting the list or updating the equipment status like check in or checkout
     */
    private fun updateQrScanList() {
        qrCodeInventoryBannerList =
            getMyDatabase()?.dbDAO?.getQrCodeInventoryData() as MutableList<QrCodeInventoryTable?>?
        inventoryToShowList =
            getMyDatabase()?.dbDAO?.getInventoryToShowData() as MutableList<InventoryToShowTable?>?
        showImagesBanner()
        qrCodeInventoryBannerAdapter?.notifyDataSetChanged()
    }

    private fun setScanAdapter() {
        qrCodeInventoryBannerList =
            getMyDatabase()?.dbDAO?.getQrCodeInventoryData() as MutableList<QrCodeInventoryTable?>?
        inventoryToShowList =
            getMyDatabase()?.dbDAO?.getInventoryToShowData() as MutableList<InventoryToShowTable?>?

        mViewPagerBannerQrCode.post {
            if (inventoryToShowList?.isNotEmpty().nullSafety()) {
                mViewPagerBannerQrCode.showView()
                pagerIndicatorQrCode.showView()
            } else {
                mViewPagerBannerQrCode.hideView()
                pagerIndicatorQrCode.hideView()
            }
        }
        showImagesBanner()
    }

    private fun showImagesBanner() {
        if (inventoryToShowList?.isNotEmpty().nullSafety()) {
            qrCodeInventoryBannerAdapter?.setEquipmentList(inventoryToShowList)
            mViewPagerBannerQrCode.adapter = qrCodeInventoryBannerAdapter
            mViewPagerBannerQrCode.currentItem = 0
        }

        mViewPagerBannerQrCode.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (inventoryToShowList.isNullOrEmpty().nullSafety()) {
                    return
                }

                try {
                    for (i in inventoryToShowList!!.indices) {
                        mDots!![i]?.setImageDrawable(
                            AppCompatResources.getDrawable(
                                mContext!!,
                                R.drawable.ic_pager_unselected_dot
                            )
                        )
                    }

                    mDots!![position]?.setImageDrawable(
                        AppCompatResources.getDrawable(
                            mContext!!,
                            R.drawable.ic_pager_selected_dot
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        if (inventoryToShowList?.isNotEmpty().nullSafety()) {
            setUiPageViewControllerQrCode(qrCodeInventoryBannerAdapter?.count.nullSafety())
        }
    }

    //managing view pager ui
    private fun setUiPageViewControllerQrCode(count: Int) {
        try {
            mDotsCount = count
            mDots = arrayOfNulls(mDotsCount)
            pagerIndicatorQrCode.removeAllViews()
            for (i in 0 until mDotsCount) {
                mDots!![i] = ImageView(this)
                mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                mDots!![i]?.setPadding(12, 0, 12, 0)
                params.setMargins(6, 0, 6, 0)
                pagerIndicatorQrCode.addView(mDots!![i], params)
            }
            if (mShowBannerCount == 0) {
                mShowBannerCount += 1
            }
            mDots!![mShowBannerCount - 1]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupEquipmentInventoryBannerAdapter() {
        qrCodeInventoryBannerAdapter = ViewPagerBannerAdapterQrCodeInventory(
            this@WelcomeActivity,
            object : ViewPagerBannerAdapterQrCodeInventory.ListItemSelectListener {
                override fun onItemClick(position: Int, isCheckedOut: Int) {
                    if (isCheckedOut != EQUIPMENT_CHECKED_OUT) {
                        lifecycleScope.launch {
                            repeatOnLifecycle(Lifecycle.State.CREATED) {
                                val (granted, denied) = requestMultiplePermissions.request(
                                    cameraPermissions
                                )

                                if (granted.size == cameraPermissions.size) {
                                    launchQRScannerActivityForResult(FROM_EQUIPMENT_CHECKOUT)
                                }
                            }
                        }
                    } else {
                        DialogUtils.showConfirmationDialog(
                            context = this@WelcomeActivity,
                            title = getString(R.string.title_inventory_update),
                            message = getString(
                                R.string.error_inventory_already_checkout_out
                            ),
                            positiveText = getString(R.string.btn_text_yes),
                            negativeText = getString(R.string.btn_text_no),
                            callback = DialogInterface.OnClickListener { dialog, which ->
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    lifecycleScope.launch {
                                        repeatOnLifecycle(Lifecycle.State.CREATED) {
                                            val (granted, denied) = requestMultiplePermissions.request(
                                                cameraPermissions
                                            )

                                            if (granted.size == cameraPermissions.size) {
                                                launchQRScannerActivityForResult(
                                                    FROM_EQUIPMENT_CHECKIN
                                                )
                                            }
                                        }
                                    }
                                }
                            })
                    }
                }
            })

        mViewPagerBannerQrCode.adapter = qrCodeInventoryBannerAdapter
    }

    private fun launchQRScannerActivityForResult(from: String) {
        val intent = Intent(this@WelcomeActivity, QRCodeScanner::class.java)
        intent.putExtra(INTENT_KEY_FROM, from)
        qrScannerForResult.launch(intent)
    }

    private val qrScannerForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val from = intent?.getStringExtra(INTENT_KEY_FROM)
            val scannedKey = intent?.getStringExtra(INTENT_KEY_SCANNED_EQUIPMENT_KEY)
            val scannedValue = intent?.getStringExtra(INTENT_KEY_SCANNED_EQUIPMENT_VALUE)

            callLogEquipmentCheckInOutAPI(
                from.nullSafety(),
                scannedKey.nullSafety(),
                scannedValue.nullSafety()
            )
        }
    }

    private fun isShiftEmpty() {
        logout(this@WelcomeActivity)
    }

    private fun isDataSetListEmpty() {
        /**
         * Checks if the violation list is empty by calling [DataBaseUtil.getViolationListEmpty].
         * If the list is empty, it also clears the related dataset tables.
         * and refill again
         */
        if (DataBaseUtil.getViolationListEmpty(getMyDatabase())) {
//            saveActivityList()
            getMyDatabase()?.dbDAO?.apply {
                deleteTimeStampTable()
                deleteAllDataSet()
                deleteActivityList()
            }
            showCustomAlertDialog(
                mContext,
                getString(R.string.warnings_lbl_dataset_empty_header),
                getString(R.string.warnings_lbl_dataset_empty_message),
                getString(R.string.alt_lbl_OK),
                getString(R.string.scr_btn_cancel),
                this@WelcomeActivity
            )
        } else {
            /**
             * Upload Offline citation and cancel data to server
             */
            checkOfflineCitationAndCancelDataInDBForUpload()
        }
    }

    /**
     * We are setting up the ALPR license key in shared preference to avoid any conflict at the scan time
     * This will be called twice, where we get the activity data
     */
    private fun setLicenseKeyForAlpr() {
        val deviceAndroidId = AppUtils.getDeviceId(this@WelcomeActivity)
        //val deviceAndroidId = "a7590568da8bc45d" //Testing just only

        //We are matching above device friendly name wih list if licenses we are getting from backend to get the correct license for the device.
        val deviceLicenseObject =
            mWelcomeListDataSet?.welcomeList?.deviceLicenseStats?.firstOrNull()?.responseDeviceLicense?.firstOrNull { it.androidId == deviceAndroidId }

        printLog("==>LICENSE_NUMBER:", deviceLicenseObject?.license.nullSafety("NO LICENSE FOUND"))
        printLog("==>LICENSE_NUMBER:DeviceID", deviceAndroidId)
        printLog("==>LICENSE_NUMBER:DeviceID", deviceLicenseObject?.deviceFriendlyName)

        //If device's friendly name matches with any object's friendly name from backend response, then it will return the license for doubango
        //return deviceLicenseObject?.license.nullSafety("NO LICENSE FOUND")

        if (sharedPreference.read(SharedPrefKey.LICENSE_KEY_ALPR, "").isNullOrEmpty()) {
            sharedPreference.write(
                SharedPrefKey.LICENSE_KEY_ALPR,
                deviceLicenseObject?.license.nullSafety("")
            )

            printLog(
                "==>LICENSE_NUMBER:IN", sharedPreference.read(
                    SharedPrefKey.LICENSE_KEY_ALPR,
                    "INSIDE_NOT FOUND"
                )
            )

        }

        printLog(
            "==>LICENSE_NUMBER:OUT", sharedPreference.read(
                SharedPrefKey.LICENSE_KEY_ALPR,
                "OUTSIDE_NOT FOUND"
            )
        )
    }
}