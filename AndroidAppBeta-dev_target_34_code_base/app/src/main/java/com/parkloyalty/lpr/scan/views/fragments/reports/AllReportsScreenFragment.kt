package com.parkloyalty.lpr.scan.views.fragments.reports


import DialogUtil
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databinding.FragmentAllReportsScreenBinding
import com.parkloyalty.lpr.scan.extensions.getActiveInactiveList
import com.parkloyalty.lpr.scan.extensions.getAssignedAreaList
import com.parkloyalty.lpr.scan.extensions.getAssignedBikeList
import com.parkloyalty.lpr.scan.extensions.getBatteryChargeList
import com.parkloyalty.lpr.scan.extensions.getBoxStrokeColor
import com.parkloyalty.lpr.scan.extensions.getColorList
import com.parkloyalty.lpr.scan.extensions.getDeviceList
import com.parkloyalty.lpr.scan.extensions.getDutyHourAreaList
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.getGasList
import com.parkloyalty.lpr.scan.extensions.getIndexOfColor
import com.parkloyalty.lpr.scan.extensions.getIndexOfMakeText
import com.parkloyalty.lpr.scan.extensions.getIndexOfStateName
import com.parkloyalty.lpr.scan.extensions.getMeterTypeList
import com.parkloyalty.lpr.scan.extensions.getOutInServiceList
import com.parkloyalty.lpr.scan.extensions.getPassFailedList
import com.parkloyalty.lpr.scan.extensions.getReasonForTowList
import com.parkloyalty.lpr.scan.extensions.getRequestToTowList
import com.parkloyalty.lpr.scan.extensions.getRequiredServiceList
import com.parkloyalty.lpr.scan.extensions.getSafetyReportList
import com.parkloyalty.lpr.scan.extensions.getTrueFalseList
import com.parkloyalty.lpr.scan.extensions.getUnitNumberList
import com.parkloyalty.lpr.scan.extensions.getVehicleStoredAtList
import com.parkloyalty.lpr.scan.extensions.getYesNoList
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.setCrossClearButton
import com.parkloyalty.lpr.scan.extensions.setListOnlyDropDown
import com.parkloyalty.lpr.scan.extensions.setupTextInputLayout
import com.parkloyalty.lpr.scan.extensions.showErrorWithShake
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.models.ScannedImageUploadResponse
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.allreport.model.AfterSevenPMRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.BikeInspectionsRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.BrokenMeterReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.CurbRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.Details
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsAfter
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsBikeInspections
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsCurb
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsFulltime
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsHandHeldMalfunctions
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsHard
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsHomeless
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsHourMarkedVehicle
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsLotInspection
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsNfl
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsNoticeToTow
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsPayStation
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsSafetyIssue
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsSign
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsSignOff
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsSignage
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsSpecial
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsSupervisor
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsTowReport
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsTrashLot
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsVehicleInspection
import com.parkloyalty.lpr.scan.ui.allreport.model.DetailsWorkOrder
import com.parkloyalty.lpr.scan.ui.allreport.model.FullTimeRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.HandHeldMalFunctionsRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.HomelessRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.HourMarkedVehiclesRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetails
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsAfter
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsBikeInspections
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsCurb
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsFulltime
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsHandHeldMalfunctions
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsHard
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsHomeless
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsHourMarkedVehicle
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsLotInspection
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsNfl
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsNoticeToTow
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsPayStation
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsSafetyIssue
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsSign
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsSignOff
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsSignage
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsSpecial
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsSupervisor
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsTowReport
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsTrashLot
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsVehicleInspection
import com.parkloyalty.lpr.scan.ui.allreport.model.LocationDetailsWorkOrder
import com.parkloyalty.lpr.scan.ui.allreport.model.LotCountVioRateRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.LotInspectionRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.NFLRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.NoticeToTowRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetails
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsAfter
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsBikeInspections
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsCurb
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsFulltime
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsHandHeldMalfunctions
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsHard
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsHomeless
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsHourMarkedVehicle
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsLotInspection
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsNfl
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsNoticeToTow
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsPayStation
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsSafetyIssue
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsSign
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsSignOff
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsSignage
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsSpecial
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsSupervisor
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsTowReport
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsTrashLot
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsVehicleInspection
import com.parkloyalty.lpr.scan.ui.allreport.model.OfficerDetailsWorkOrder
import com.parkloyalty.lpr.scan.ui.allreport.model.PayStationRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SafetyIssueRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SignOffReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SignReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SignageReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SpecialAssignementRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.SupervisorReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.TowReportRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.TrashLotRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.VehicleInspectionRequest
import com.parkloyalty.lpr.scan.ui.allreport.model.WorkOrderRequest
import com.parkloyalty.lpr.scan.ui.boot.model.ResponseBoot
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ViewPagerBannerAdapterWithDefaultImage
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.BeatStat
import com.parkloyalty.lpr.scan.ui.login.model.DeviceResponseItem
import com.parkloyalty.lpr.scan.ui.login.model.OfficerDeviceIdObject
import com.parkloyalty.lpr.scan.ui.login.model.ResponseItem
import com.parkloyalty.lpr.scan.ui.login.model.ShiftStat
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeList
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.util.API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.ReportArrayClass
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_ID_TIMESTAMP
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_TIMESTAMP
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.AppConstants.TEMP_IMAGE_FILE_NAME
import com.parkloyalty.lpr.scan.utils.BundleConstants.BUNDLE_KEY_REPORT_TYPE
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_72_HOURS_MARKED_VEHICLE_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_AFTER_SEVEN_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_BIKE_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_BROKEN_METER_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_CURB_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_EOW_OFFICER_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_EOW_SUPERVISOR_SHIFT_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_HAND_HELD_MALFUNCTION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_HARD_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_NFL_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PART_EOW_OFFICER_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_HOMELESS_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_LOT_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_LOT_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_SAFETY_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_SIGNAGE_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_STATION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_PAY_TRASH_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_SIGN_OFF_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_SIGN_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_SPECIAL_ASSIGNMENT_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_TOW_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_VEHICLE_INSPECTION_REPORT
import com.parkloyalty.lpr.scan.utils.BundleConstants.REPORT_TYPE_WORK_ORDER_REPORT
import com.parkloyalty.lpr.scan.utils.CitationFormUtils.CITATION_FORM_FIELD_TYPE_LIST_ONLY
import com.parkloyalty.lpr.scan.utils.MultipartUtils
import com.parkloyalty.lpr.scan.utils.NoInternetDialogListener
import com.parkloyalty.lpr.scan.utils.NoInternetDialogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.ViewPagerUtils
import com.parkloyalty.lpr.scan.utils.camerahelper.CameraHelper
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.utils.permissions.PermissionUtils.getCameraPermission
import com.parkloyalty.lpr.scan.views.MainActivityAction
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import com.parkloyalty.lpr.scan.views.bottomsheets.signature.SignatureBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Arrays
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AllReportsScreenFragment : BaseFragment<FragmentAllReportsScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val allReportsScreenViewModel: AllReportsScreenViewModel by viewModels()

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var sharedPreference: SharedPref

    @Inject
    lateinit var cameraHelper: CameraHelper

    private var session: CameraHelper.Session? = null

    lateinit var mEditTextOfficerName: AppCompatTextView
    lateinit var mEditTextOfficerBeat: AppCompatTextView
    lateinit var mEditTextOfficerId: AppCompatTextView
    lateinit var mEditTextDateTime: AppCompatTextView
    lateinit var mEditTextReportNumber: AppCompatTextView
    lateinit var mLocationDetails: LinearLayoutCompat
    lateinit var mOtherDetails: LinearLayoutCompat
    lateinit var mCameraImagesLayout: LinearLayoutCompat
    lateinit var mViewPagerBanner: ViewPager
    lateinit var pagerIndicator: LinearLayoutCompat
    lateinit var tvCameraTitle: AppCompatTextView
    lateinit var appCompatImageViewCameraIcon: AppCompatImageView

    private var reportType = ""

    private val mSideItem = ""
    private var clientTime = ""

    private var bannerList: MutableList<CitationImagesModel?>? = ArrayList()
    private var mBannerAdapter: ViewPagerBannerAdapterWithDefaultImage? = null
    private var imageValidationCount = 0
    private var cameraCount = 0
    private var minimumImageRequired = 1
    private var mImagesLinks: MutableList<String> = ArrayList()
    private val myCalendar = Calendar.getInstance()
    var violationDescription: MutableList<String> = ArrayList()

    private var mWelcomeFormData: WelcomeForm? = null
    private var mWelcomeListDataSet: WelcomeListDatatbase? = null
    private var mDatabaseWelcomeList: WelcomeList? = WelcomeList()
    private var selectedDeViceId = OfficerDeviceIdObject()

    //Extras
    private var mSelectedMake: String? = ""
    private var mSelectedMakeValue: String? = ""
    private var mSelectedModel: String? = ""
    private var mSelecteModelLookupCode: String? = ""
    private var mSelectedColor: String? = ""
    private var mState2DigitCode: String? = ""
    private val mModelList: MutableList<DatasetResponse> = ArrayList()

    //List Variables
    private lateinit var mTureFalseList: Array<String>
    private lateinit var mYesNoList: Array<String>
    private lateinit var mPassFailedList: Array<String>
    private lateinit var mActiveInactiveList: Array<String>
    private lateinit var mAssignedBikeList: Array<String>
    private lateinit var mOutInServiceList: Array<String>
    private lateinit var mColorList: Array<String>
    private lateinit var mGasList: Array<String>
    private lateinit var mBatterChargeList: Array<String>
    private lateinit var mMeterTypeList: Array<String>
    private lateinit var mDeviceList: Array<String>
    private lateinit var mRequestToTowList: Array<String>
    private lateinit var mUnitNumberList: Array<String>
    private lateinit var mAssignAreaList: Array<String>
    private lateinit var mDutyHourAreaList: Array<String>
    private lateinit var mReasonForTowList: Array<String>
    private lateinit var mVehicleStoredAtList: Array<String>
    private lateinit var mSafetyReportList: Array<String>
    private lateinit var mRequiredServicesList: Array<String>
    //End of List Variables

    private var rootView: View? = null
    private var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatBlock: LinearLayoutCompat? = null
    private var textInputLayoutBlock: TextInputLayout? = null

    private var mAutoComTextViewStreet: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatStreet: LinearLayoutCompat? = null
    private var textInputLayoutStreet: TextInputLayout? = null


    private var mAutoComTextViewDirection: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatDirection: LinearLayoutCompat? = null
    private var textInputLayoutDirection: TextInputLayout? = null


    private var mAutoComTextViewDeviceId: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatDeviceId: LinearLayoutCompat? = null
    private var textInputLayoutDeviceId: TextInputLayout? = null


    private var mAutoComTextViewMeterNo: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatMeterNo: LinearLayoutCompat? = null
    private var textInputLayoutMeterNo: TextInputLayout? = null


    private var mAutoComTextViewLotArea: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatLotArea: LinearLayoutCompat? = null
    private var mInputLayoutLotArea: TextInputLayout? = null

    private var mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot: AppCompatAutoCompleteTextView? =
        null
    private var mLayoutCompatVehicleRelocatedToDifferentStallWithinTheLot: LinearLayoutCompat? =
        null
    private var textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot: TextInputLayout? = null

    private var mAutoComTextViewVehicleStoredAt: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatVehicleStoredAt: LinearLayoutCompat? = null
    private var mInputLayoutVehicleStoredAt: TextInputLayout? = null

    private var mAutoComTextViewReasonForTow: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatReasonForTow: LinearLayoutCompat? = null
    private var textInputLayoutReasonForTow: TextInputLayout? = null


    private var mAutoComTextViewTowFileNumber: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTowFileNumber: LinearLayoutCompat? = null
    private var mInputLayoutTowFileNumber: TextInputLayout? = null

    private var mAutoComTextViewLicensePlate: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatLicensePlate: LinearLayoutCompat? = null
    private var mInputLayoutLicensePlate: TextInputLayout? = null

    private var mAutoComTextViewVehicleWithInLot: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatVehicleWithInLot: LinearLayoutCompat? = null
    private var mInputLayoutVehicleWithInLot: TextInputLayout? = null

    private var mAutoComTextViewVin: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatVin: LinearLayoutCompat? = null
    private var textInputLayoutVin: TextInputLayout? = null


    private var mAutoComTextViewState: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatState: LinearLayoutCompat? = null
    private var textInputLayoutState: TextInputLayout? = null


    private var mAutoComTextViewMake: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatMake: LinearLayoutCompat? = null
    private var textInputLayoutMake: TextInputLayout? = null


    private var mAutoComTextViewModel: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatModel: LinearLayoutCompat? = null
    private var textInputLayoutModel: TextInputLayout? = null


    private var mAutoComTextViewColor: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatColor: LinearLayoutCompat? = null
    private var textInputLayoutColor: TextInputLayout? = null


    private var mAutoComTextViewZone: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatZone: LinearLayoutCompat? = null
    private var textInputLayoutZone: TextInputLayout? = null


    private var mAutoComTextViewPBCZone: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatPBCZone: LinearLayoutCompat? = null
    private var textInputLayoutPBCZone: TextInputLayout? = null


    private var mAutoComTextViewMeterType: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatMeterType: LinearLayoutCompat? = null
    private var textInputLayoutMeterType: TextInputLayout? = null


    private var mAutoComTextViewMeterLabelVisible: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatMeterLabelVisible: LinearLayoutCompat? = null
    private var textInputLayoutMeterLableVisible: TextInputLayout? = null


    private var mAutoComTextViewDigitalDisplayVisible: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatDigitalDisplayVisible: LinearLayoutCompat? = null
    private var textInputLayoutDigitalDisplayVisible: TextInputLayout? = null


    private var mAutoComTextViewCoinJam: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatCoinJam: LinearLayoutCompat? = null
    private var textInputLayoutCoinJam: TextInputLayout? = null


    private var mAutoComTextViewCreditCardJam: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatCreditCardJam: LinearLayoutCompat? = null
    private var textInputLayoutCreditCardJam: TextInputLayout? = null


    private var mAutoComTextViewComments: MaterialAutoCompleteTextView? = null
    private var mLayoutCompatComments: LinearLayoutCompat? = null
    private var textInputLayoutComments: TextInputLayout? = null


    private var mAutoComTextViewComment2: TextInputEditText? = null
    private var mLayoutCompatComment2: LinearLayoutCompat? = null
    private var textInputLayoutComment2: TextInputLayout? = null


    private var mAutoComTextViewComment3: TextInputEditText? = null
    private var mLayoutCompatComment3: LinearLayoutCompat? = null
    private var textInputLayoutComment3: TextInputLayout? = null


    private var mAutoComTextViewStaus: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatStaus: LinearLayoutCompat? = null
    private var textInputLayoutStatus: TextInputLayout? = null


    private var mAutoComTextViewEnforceable: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatEnforceable: LinearLayoutCompat? = null
    private var textInputLayoutEnforceable: TextInputLayout? = null


    private var mAutoComTextViewCurbColor: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatCurbColor: LinearLayoutCompat? = null
    private var textInputLayoutCurbColor: TextInputLayout? = null


    private var mAutoComTextViewDate: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatDate: LinearLayoutCompat? = null
    private var textInputLayoutDate: TextInputLayout? = null


    private var mAutoComTextViewDutyHours: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatDutyHours: LinearLayoutCompat? = null
    private var mInputLayoutDutyHours: TextInputLayout? = null

    private var mAutoComTextViewOfficer: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatOfficer: LinearLayoutCompat? = null
    private var textInputLayoutOfficer: TextInputLayout? = null


    private var mAutoComTextViewLunchTaken: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatLunchTaken: LinearLayoutCompat? = null
    private var textInputLayoutLunchTaken: TextInputLayout? = null


    private var mAutoComTextViewFirst10MinBreak: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatFirst10MinBreak: LinearLayoutCompat? = null
    private var textInputLayoutFirst10MinBreak: TextInputLayout? = null


    private var mAutoComTextViewSecond10MinBreak: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatSecond10MinBreak: LinearLayoutCompat? = null
    private var textInputLayoutSecond10MinBreak: TextInputLayout? = null


    private var mAutoComTextViewUnitNo: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatUnitNo: LinearLayoutCompat? = null
    private var textInputLayoutUnitNo: TextInputLayout? = null


    private var mAutoComTextViewAssignedArea: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatAssignedArea: LinearLayoutCompat? = null
    private var textInputLayoutAssignedArea: TextInputLayout? = null


    private var mAutoComTextViewViolationDescription: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatViolationDescription: LinearLayoutCompat? = null
    private var textInputLayoutViolationDescription: TextInputLayout? = null

    private var mAutoComTextViewHandheldUnitNo: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatHandheldUnitNo: LinearLayoutCompat? = null
    private var textInputLayoutHandheldUnitNo: TextInputLayout? = null


    private var mAutoComTextViewCitationsIssued: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatCitationsIssued: LinearLayoutCompat? = null
    private var mInputLayoutCitationsIssued: TextInputLayout? = null

    private var mAutoComTextViewSpecialEnforcementRequest: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatSpecialEnforcementRequest: LinearLayoutCompat? = null
    private var mInputLayoutSpecialEnforcementRequest: TextInputLayout? = null

    private var mAutoComTextViewGarageClearance: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatGarageClearance: LinearLayoutCompat? = null
    private var mInputLayoutGarageClearance: TextInputLayout? = null

    private var mAutoComTextViewFileNumber: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatFileNumber: LinearLayoutCompat? = null
    private var mInputLayoutFileNumber: TextInputLayout? = null

    private var mAutoComTextViewTriedToRestartHandHeld: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTriedToRestartHandHeld: LinearLayoutCompat? = null
    private var textInputLayoutTriedToRestartHandheld: TextInputLayout? = null

    private var mAutoComTextViewPrintingCorrectly: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatPrintingCorrectly: LinearLayoutCompat? = null
    private var textInputLayoutPrintingCorrectly: TextInputLayout? = null

    private var mAutoComTextViewOverHeating: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatOverHeating: LinearLayoutCompat? = null
    private var textInputLayoutOverHeating: TextInputLayout? = null

    private var mAutoComTextViewBatteryHoldCharge: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatBatteryHoldCharge: LinearLayoutCompat? = null
    private var textInputLayoutBatteryHoldCharge: TextInputLayout? = null

    private var mAutoComTextViewInternetConnectivity: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatInternetConnectivity: LinearLayoutCompat? = null
    private var textInputLayoutInternetConnectivity: TextInputLayout? = null

    private var mAutoComTextViewDescribeHandHeldMalfunctionInDetail: AppCompatAutoCompleteTextView? =
        null
    private var mLayoutCompatDescribeHandHeldMalfunctionInDetail: LinearLayoutCompat? = null
    private var mInputLayoutDescribeHandHeldMalfunctionInDetail: TextInputLayout? = null

    private var mAutoComTextViewGraffiti: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatGraffiti: LinearLayoutCompat? = null
    private var textInputLayoutGraffiti: TextInputLayout? = null

    private var mAutoComTextViewMissingSign: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatMissingSign: LinearLayoutCompat? = null
    private var textInputLayoutMissingSign: TextInputLayout? = null

    private var mAutoComTextViewVehicle: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatVehicle: LinearLayoutCompat? = null
    private var textInputLayoutVehicle: TextInputLayout? = null

    private var mAutoComTextViewVehicleMark: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatVehicleMark: LinearLayoutCompat? = null
    private var mInputLayoutVehicleMark: TextInputLayout? = null

    private var mAutoComTextViewVehicleCondition: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatVehicleCondition: LinearLayoutCompat? = null
    private var mInputLayoutVehicleCondition: TextInputLayout? = null

    private var mAutoComTextViewOfficerPhoneNumber: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatOfficerPhoneNumber: LinearLayoutCompat? = null
    private var mInputLayoutOfficerPhoneNumber: TextInputLayout? = null

    private var mAutoComTextViewTimeMark: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTimeMark: LinearLayoutCompat? = null
    private var mInputLayoutTimeMark: TextInputLayout? = null

    private var mAutoComTextViewTimeMark2: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTimeMark2: LinearLayoutCompat? = null
    private var mInputLayoutTimeMark2: TextInputLayout? = null

    private var mAutoComTextViewTimeMark3: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTimeMark3: LinearLayoutCompat? = null
    private var mInputLayoutTimeMark3: TextInputLayout? = null

    private var mAutoComTextViewTimeMark4: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTimeMark4: LinearLayoutCompat? = null
    private var mInputLayoutTimeMark4: TextInputLayout? = null

    private var mAutoComTextViewStartingMileage: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatStartingMileage: LinearLayoutCompat? = null
    private var textInputLayoutStartingMileage: TextInputLayout? = null

    private var mAutoComTextViewGasLevel: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatGasLevel: LinearLayoutCompat? = null
    private var textInputLayoutGasLevel: TextInputLayout? = null

    private var mAutoComTextViewLightBar: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatLightBar: LinearLayoutCompat? = null
    private var textInputLayoutLightBar: TextInputLayout? = null

    private var mAutoComTextViewDashBoardIndications: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatDashBoardIndications: LinearLayoutCompat? = null
    private var textInputLayoutDashBoardIndications: TextInputLayout? = null


    private var mAutoComTextViewSeatBeltOperational: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatSeatBeltOperational: LinearLayoutCompat? = null
    private var textInputLayoutSeatBeltOperational: TextInputLayout? = null


    private var mAutoComTextViewBrakes: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatBrakes: LinearLayoutCompat? = null
    private var textInputLayoutBrakes: TextInputLayout? = null


    private var mAutoComTextViewBrakeLights: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatBrakeLights: LinearLayoutCompat? = null
    private var textInputLayoutBrakeLights: TextInputLayout? = null


    private var mAutoComTextViewHeadLights: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatHeadLights: LinearLayoutCompat? = null
    private var textInputLayoutHeadLights: TextInputLayout? = null


    private var mAutoComTextViewTurnSignals: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTurnSignals: LinearLayoutCompat? = null
    private var textInputLayoutSignals: TextInputLayout? = null


    private var mAutoComTextViewSteeringWheelOperational: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatSteeringWheelOperational: LinearLayoutCompat? = null
    private var textInputLayoutSteeringWheelOperational: TextInputLayout? = null

    private var mAutoComTextViewWindshieldVisibility: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatWindshieldVisibility: LinearLayoutCompat? = null
    private var textInputLayoutWindshieldVisibility: TextInputLayout? = null


    private var mAutoComTextViewSideAndRearViewMirrorsOperational: AppCompatAutoCompleteTextView? =
        null
    private var mLayoutCompatSideAndRearViewMirrorsOperational: LinearLayoutCompat? = null
    private var textInputLayoutSideAndRearViewMirrorsOperational: TextInputLayout? = null


    private var mAutoComTextViewWindshieldWipersOperational: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatWindshieldWipersOperational: LinearLayoutCompat? = null
    private var textInputLayoutWindshieldWipersOperational: TextInputLayout? = null


    private var mAutoComTextViewVehicleRegistrationAndInsurance: AppCompatAutoCompleteTextView? =
        null
    private var mLayoutCompatVehicleRegistrationAndInsurance: LinearLayoutCompat? = null
    private var textInputLayoutVehicleRegistrationAndInsurance: TextInputLayout? = null


    private var mAutoComTextViewConesSixPerVehicle: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatConesSixPerVehicle: LinearLayoutCompat? = null
    private var textInputLayoutConesSixPerVehicle: TextInputLayout? = null


    private var mAutoComTextViewFirstAidKit: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatFirstAidKit: LinearLayoutCompat? = null
    private var textInputLayoutFirstAidKit: TextInputLayout? = null


    private var mAutoComTextViewHorn: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatHorn: LinearLayoutCompat? = null
    private var textInputLayoutHorn: TextInputLayout? = null


    private var mAutoComTextViewInteriorCleanliness: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatInteriorCleanliness: LinearLayoutCompat? = null
    private var mInputLayoutInteriorCleanliness: TextInputLayout? = null

    private var mAutoComTextViewExteriorCleanliness: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatExteriorCleanliness: LinearLayoutCompat? = null
    private var textInputLayoutExteriorCleanliness: TextInputLayout? = null


    private var mAutoComTextViewLprLensFreeOfDebris: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatLprLensFreeOfDebris: LinearLayoutCompat? = null
    private var textInputLayoutLprLensFreeOfDebris: TextInputLayout? = null

    private var mAutoComTextViewVisibleLeaks: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatVisibleLeaks: LinearLayoutCompat? = null
    private var textInputLayoutVisibleLeaks: TextInputLayout? = null

    private var mAutoComTextViewTiresVisualInspection: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTiresVisualInspection: LinearLayoutCompat? = null
    private var textInputLayoutTiresVisualInspection: TextInputLayout? = null

    //    Bike Inspection
    private var mAutoComTextViewTirePressure: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTirePressure: LinearLayoutCompat? = null
    private var textInputLayoutTirePressure: TextInputLayout? = null

    private var mAutoComTextViewAssignedBike: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatAssignedBike: LinearLayoutCompat? = null
    private var textInputLayoutAssignedBike: TextInputLayout? = null

    private var mAutoComTextViewBreaksRotors: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatBreaksRotors: LinearLayoutCompat? = null
    private var textInputLayoutBreakRotors: TextInputLayout? = null

    private var mAutoComTextViewLightsAndReflectors: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatLightsAndReflectors: LinearLayoutCompat? = null
    private var textInputLayoutLightsAndReflectors: TextInputLayout? = null

    private var mAutoComTextViewChainCrank: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatChainCrank: LinearLayoutCompat? = null
    private var textInputLayoutChainCrank: TextInputLayout? = null

    private var mAutoComTextViewBatteryFreeOfDebris: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatBatteryFreeOfDebris: LinearLayoutCompat? = null
    private var textInputLayoutBatteryFreeDebris: TextInputLayout? = null

    private var mAutoComTextViewFlatPack: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatFlatPack: LinearLayoutCompat? = null
    private var textInputLayoutFlatPack: TextInputLayout? = null

    private var mAutoComTextViewTotalEnforcementPersonnel: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTotalEnforcementPersonnel: LinearLayoutCompat? = null
    private var textInputLayoutTotalEnforcementPersonnel: TextInputLayout? = null

    private var mAutoComTextViewComplaintsTowardsOfficer: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatComplaintsTowardsOfficer: LinearLayoutCompat? = null
    private var mInputLayoutComplaintsTowardsOfficer: TextInputLayout? = null

    private var mAutoComTextViewResidentComplaints: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatResidentComplaints: LinearLayoutCompat? = null
    private var mInputLayoutResidentComplaints: TextInputLayout? = null

    private var mAutoComTextViewWarningsIssued: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatWarningsIssued: LinearLayoutCompat? = null
    private var mInputLayoutWarningsIssued: TextInputLayout? = null

    private var mAutoComTextViewRo: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatRo: LinearLayoutCompat? = null
    private var mInputLayoutRo: TextInputLayout? = null

    private var mAutoComTextViewDriverSideComment: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatDriverSideComment: LinearLayoutCompat? = null
    private var mInputLayoutDriverSideComment: TextInputLayout? = null

    private var mAutoComTextViewPassengerSideComment: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatPassengerSideComment: LinearLayoutCompat? = null
    private var mInputLayoutPassengerSideComment: TextInputLayout? = null

    private var mAutoComTextViewFrontSideComment: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatFrontSideComment: LinearLayoutCompat? = null
    private var mInputLayoutFrontSideComment: TextInputLayout? = null

    private var mAutoComTextViewRearSideComment: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatRearSideComment: LinearLayoutCompat? = null
    private var mInputLayoutRearSideComment: TextInputLayout? = null

    private var mAutoComTextViewTrailerComment: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTrailerComment: LinearLayoutCompat? = null
    private var mInputLayoutTrailerComment: TextInputLayout? = null

    private var mAutoComTextViewBikeGlassess: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatBikeGlassess: LinearLayoutCompat? = null
    private var textInputLayoutGlasses: TextInputLayout? = null

    private var mAutoComTextViewBatteryCharge: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatBatteryCharge: LinearLayoutCompat? = null
    private var textInputLayoutBatteryCharge: TextInputLayout? = null

    private var mAutoComTextViewGloveVisualInspection: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatGloveVisualInspection: LinearLayoutCompat? = null
    private var textInputLayoutGloveVisualInspection: TextInputLayout? = null

    private var mAutoComTextViewCarCount: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatCarCount: LinearLayoutCompat? = null
    private var mInputLayoutCarCount: TextInputLayout? = null

    private var mAutoComTextViewEmptySpace: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatEmptySpace: LinearLayoutCompat? = null
    private var mInputLayoutEmptySpace: TextInputLayout? = null

    private var mAutoComTextViewNumberOfViolatingVehicles: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatNumberOfViolatingVehicles: LinearLayoutCompat? = null
    private var mInputLayoutNumberOfViolatingVehicles: TextInputLayout? = null

    private var mAutoComTextViewViolationRate: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatViolationRate: LinearLayoutCompat? = null
    private var mInputLayoutViolationRate: TextInputLayout? = null

    private var mAutoComTextViewSecurityObservation: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatSecurityObservation: LinearLayoutCompat? = null
    private var mInputLayoutSecurityObservation: TextInputLayout? = null

    private var mAutoComTextViewSafetyIssue: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatSafetyIssue: LinearLayoutCompat? = null
    private var textInputLayoutSafetyIssue: TextInputLayout? = null

    private var mAutoComTextViewRequiredServices: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatRequiredServices: LinearLayoutCompat? = null
    private var textInputLayoutRequiredServices: TextInputLayout? = null

    private var mAutoComTextViewField1: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatField1: LinearLayoutCompat? = null
    private var mInputLayoutField1: TextInputLayout? = null

    private var mAutoComTextViewField2: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatField2: LinearLayoutCompat? = null
    private var mInputLayoutField2: TextInputLayout? = null

    private var mAutoComTextViewField3: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatField3: LinearLayoutCompat? = null
    private var mInputLayoutField3: TextInputLayout? = null

    private var mAutoComTextViewField4: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatField4: LinearLayoutCompat? = null
    private var mInputLayoutField4: TextInputLayout? = null

    private var mAutoComTextViewField5: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatField5: LinearLayoutCompat? = null
    private var mInputLayoutField5: TextInputLayout? = null

    private var mAutoComTextViewField6: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatField6: LinearLayoutCompat? = null
    private var mInputLayoutField6: TextInputLayout? = null

    private var mAutoComTextViewLine: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatLine: LinearLayoutCompat? = null
    private var mInputLayoutLine: TextInputLayout? = null

    private var mAutoComTextViewTowingOfficer: AppCompatAutoCompleteTextView? = null
    private var mLayoutCompatTowingOfficer: LinearLayoutCompat? = null
    private var textInputLayoutTowingOfficer: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager =
            permissionFactory.create(caller = this, context = requireContext(), fragment = this)

        // create a session tied to this fragment lifecycle
        session = cameraHelper.createSession(fragment = this)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAllReportsScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        mEditTextOfficerName = binding.officername
        mEditTextOfficerBeat = binding.txtBeat
        mEditTextOfficerId = binding.txtBadgeId
        mEditTextDateTime = binding.txtIssuedate
        mEditTextReportNumber = binding.txtReportNumber
        mLocationDetails = binding.linearLayoutLocation
        mOtherDetails = binding.linearLayoutDetails
        mCameraImagesLayout = binding.llImages
        mViewPagerBanner = binding.layoutContentBanner.pagerBanner
        pagerIndicator = binding.layoutContentBanner.viewPagerCountDots
        tvCameraTitle = binding.tvCameraTitle
        appCompatImageViewCameraIcon = binding.ivCameraBottom
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun initViewLifecycleScope() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainActivityViewModel.setToolbarVisibility(true)
                    mainActivityViewModel.setToolbarComponents(
                        showBackButton = true, showLogo = true, showHemMenu = false
                    )
                }

                launch {
                    allReportsScreenViewModel.brokenMeterReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.curbReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.fullTimeReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.partTimeReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.handHeldMalfunctionReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.signReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.vehicleInspectionReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.seventyTwoHourMarkedVehiclesReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.bikeInspectionReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.supervisorReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.specialAssignmentReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.signOffReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.noticeToTowReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.towReportReportResponse.collect(::consumeResponse)
                }
                launch {
                    allReportsScreenViewModel.nflReportServiceResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.lotInspectionReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.lotCountVioReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.hardSummerFestivalReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.afterSevenPmReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.payStationReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.signageReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.homelessReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.safetyIssueReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.workOrderReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.trashLotReportResponse.collect(::consumeResponse)
                }

                launch {
                    allReportsScreenViewModel.uploadAllImagesResponse.collect(::consumeResponseForImageUpload)
                }
            }
        }
    }

    override fun initialiseData() {
        reportType = arguments?.getString(BUNDLE_KEY_REPORT_TYPE) ?: ""
        initLists()

        FileUtil.removeTimingImagesFromFolder()
        DialogUtil.showLoader(requireContext())
        mViewPagerBanner.showView()


        mBannerAdapter = ViewPagerBannerAdapterWithDefaultImage(
            requireContext(),
            object : ViewPagerBannerAdapterWithDefaultImage.ListItemSelectListener {
                override fun onItemClick(position: Int) {
//                            setCameraImages()
                    if (reportType.equals(REPORT_TYPE_SIGN_OFF_REPORT) || (reportType.equals(
                            REPORT_TYPE_AFTER_SEVEN_REPORT
                        ) && position == 7)
                    ) {
                        setSignatureView()
                    } else {
                        cameraIntent()
                    }
                }
            })
        setDefaultImages()

        viewLifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.Main) {
                mWelcomeFormData = allReportsScreenViewModel.getWelcomeForm()
                mWelcomeListDataSet = allReportsScreenViewModel.getWelcomeDbObject()
            }

            if (mWelcomeListDataSet != null) {
                mDatabaseWelcomeList = mWelcomeListDataSet?.welcomeList
            }

            mEditTextDateTime.text = AppUtils.getCurrentDateTimeforBoot("UI")
            clientTime = AppUtils.getCurrentDateTimeforBoot("Normal").trim().replace(" ", "T")
            loadUIForm(reportType)
            setDropDowns()
        }
    }

    private fun initLists() {
        mTureFalseList = requireContext().getTrueFalseList()
        mYesNoList = requireContext().getYesNoList()
        mPassFailedList = requireContext().getPassFailedList()
        mActiveInactiveList = requireContext().getActiveInactiveList()
        mAssignedBikeList = requireContext().getAssignedBikeList()
        mOutInServiceList = requireContext().getOutInServiceList()
        mColorList = requireContext().getColorList()
        mGasList = requireContext().getGasList()
        mBatterChargeList = requireContext().getBatteryChargeList()
        mMeterTypeList = requireContext().getMeterTypeList()
        mDeviceList = requireContext().getDeviceList()
        mRequestToTowList = requireContext().getRequestToTowList()
        mUnitNumberList = requireContext().getUnitNumberList()
        mAssignAreaList = requireContext().getAssignedAreaList()
        mDutyHourAreaList = requireContext().getDutyHourAreaList()
        mReasonForTowList = requireContext().getReasonForTowList()
        mVehicleStoredAtList = requireContext().getVehicleStoredAtList()
        mSafetyReportList = requireContext().getSafetyReportList()
        mRequiredServicesList = requireContext().getRequiredServiceList()
    }

    override fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener {
            requireActivity().hideSoftKeyboard()

            if (requireContext().isInternetAvailable()) {
                if (isFormValid(reportType)) {
                    setRequest(reportType)
                }
            } else {
                NoInternetDialogUtil.showDialog(
                    context = requireContext(),
                    positiveButtonText = getString(R.string.button_text_ok),
                    negativeButtonText = getString(R.string.button_text_retry),
                    listener = object : NoInternetDialogListener {
                        override fun onNegativeButtonClicked() {
                            if (isFormValid(reportType)) {
                                setRequest(reportType)
                            }
                        }
                    })
            }

        }

        binding.ivCameraBottom.setOnClickListener {
            requireActivity().hideSoftKeyboard()

            viewLifecycleOwner.lifecycleScope.launch {
                permissionManager.ensurePermissionsThen(
                    permissions = getCameraPermission(),
                    rationaleMessage = getString(R.string.permission_message_all_permission_required_to_login)
                ) {
                    if (requireContext().isInternetAvailable()) {
                        if (cameraCount < imageValidationCount) {
                            if (reportType.equals(REPORT_TYPE_SIGN_OFF_REPORT) || (reportType.equals(
                                    REPORT_TYPE_AFTER_SEVEN_REPORT
                                ) && bannerList?.size == 7)
                            ) {
                                setSignatureView()
                            } else {
                                cameraIntent()
                            }
                        } else {
                            LogUtil.printToastMSGForErrorWarning(
                                requireContext(), getString(R.string.msg_min_image).replace(
                                    "#", minimumImageRequired.nullSafety().toString() + ""
                                )
                            )
                        }
                    } else {
                        NoInternetDialogUtil.showDialog(
                            context = requireContext(),
                            positiveButtonText = getString(R.string.button_text_ok),
                            negativeButtonText = getString(R.string.button_text_retry),
                            listener = object : NoInternetDialogListener {
                                override fun onNegativeButtonClicked() {

                                }
                            })
                    }
                }
            }


        }

    }


    private fun consumeResponse(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(context = requireContext())
            }

            is NewApiResponse.Success -> {
                DialogUtil.hideLoader()
                try {
                    val responseModel = ObjectMapperProvider.fromJson(
                        (newApiResponse.data as JsonNode).toString(), ResponseBoot::class.java
                    )

                    try {
                        if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                            ApiLogsClass.writeApiPayloadTex(
                                requireContext(), "Response  " + tag
                            )
                            ApiLogsClass.writeApiPayloadTex(
                                requireContext(),
                                "Response " + " :- " + ObjectMapperProvider.instance.writeValueAsString(
                                    responseModel
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (responseModel != null && responseModel.isSuccess) {
                        AlertDialogUtils.showDialog(
                            context = requireContext(),
                            icon = R.drawable.icon_success,
                            cancelable = false,
                            title = reportType,
                            message = "Submit " + reportType + " successfully",
                            positiveButtonText = getString(R.string.button_text_ok),
                            listener = object : AlertDialogListener {
                                override fun onPositiveButtonClicked() {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        mainActivityViewModel.backButtonPressed()
                                    }
                                }
                            })
                    } else {
                        AlertDialogUtils.showDialog(
                            context = requireContext(),
                            cancelable = false,
                            title = reportType,
                            message = getString(R.string.err_msg_something_went_wrong),
                            positiveButtonText = getString(R.string.button_text_ok),
                            listener = object : AlertDialogListener {
                                override fun onPositiveButtonClicked() {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        mainActivityViewModel.backButtonPressed()
                                    }
                                }
                            })
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

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "Response " + " :- " + newApiResponse.getErrorMessage().nullSafety()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "Response " + " :- " + newApiResponse.exception.message.nullSafety()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "Response " + " :- " + newApiResponse.throwable.localizedMessage.nullSafety()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun consumeResponseForImageUpload(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(context = requireContext())
            }

            is NewApiResponse.Success -> {
                DialogUtil.hideLoader()
                try {
                    val response = ObjectMapperProvider.fromJson(
                        (newApiResponse.data as JsonNode).toString(),
                        ScannedImageUploadResponse::class.java
                    )


                    when (response.status) {
                        true -> {
//                        mCitationImagesLinks = response.data.get(0).response.links
                            try {
                                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                    ApiLogsClass.writeApiPayloadTex(
                                        requireContext(),
                                        "Image Upload Response" + " :- " + response.data.get(0).response?.links
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            response.data.get(0).response?.links?.let { mImagesLinks.addAll(it) }
                            sendRequestWithImage()

                        }

                        else -> {
                            requireContext().toast(getString(R.string.err_msg_something_went_wrong_image))

                            try {
                                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                    ApiLogsClass.writeApiPayloadTex(
                                        requireContext(),
                                        "Image Upload ERROR Response" + " :- " + response.message.nullSafety()
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
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

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "Image Upload ERROR Response" + " :- " + newApiResponse.getErrorMessage()
                                .nullSafety()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "Image Upload ERROR Response" + " :- " + newApiResponse.exception.message.nullSafety()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "Image Upload ERROR Response" + " :- " + newApiResponse.throwable.localizedMessage.nullSafety()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadUIForm(reportType: String) {
        if (reportType.equals(REPORT_TYPE_BROKEN_METER_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            // Block
            mAutoComTextViewBlock =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBlock = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutBlock?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_block),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )
            mLocationDetails.addView(mLayoutCompatBlock)

            // Street
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewStreet =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStreet = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutStreet?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_street),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )

            mLocationDetails.addView(mLayoutCompatStreet)

            // direction
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewDirection =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDirection =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDirection = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutDirection?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_direction),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )

//            mAutoComTextViewDirection.setText("S")
            mLocationDetails.addView(mLayoutCompatDirection)

            // meter_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_meter_no),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )

            mOtherDetails.addView(mLayoutCompatMeterNo)

            // meter_type
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMeterType =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterType =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterType = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutMeterType?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_meter_type),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterType,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterType
            )

            mOtherDetails.addView(mLayoutCompatMeterType)

            // meter_label_visible
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewMeterLabelVisible =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterLabelVisible =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterLableVisible =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutMeterLableVisible?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_meter_label),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterLableVisible,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterLabelVisible
            )

            mOtherDetails.addView(mLayoutCompatMeterLabelVisible)

            // meter_label_visible
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDigitalDisplayVisible =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDigitalDisplayVisible =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDigitalDisplayVisible =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutDigitalDisplayVisible?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_digital_display_visible),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDigitalDisplayVisible,
                appCompatAutoCompleteTextView = mAutoComTextViewDigitalDisplayVisible
            )

            mOtherDetails.addView(mLayoutCompatDigitalDisplayVisible)

            // meter_label_visible
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewCoinJam =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCoinJam =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutCoinJam = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutCoinJam?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_coin_jam),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutCoinJam,
                appCompatAutoCompleteTextView = mAutoComTextViewCoinJam
            )

            mOtherDetails.addView(mLayoutCompatCoinJam)

            // meter_label_visible
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewCreditCardJam =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCreditCardJam =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutCreditCardJam = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutCreditCardJam?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_credit_card_jam),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutCreditCardJam,
                appCompatAutoCompleteTextView = mAutoComTextViewCreditCardJam
            )

            mOtherDetails.addView(mLayoutCompatCreditCardJam)

            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)

            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutComments?.setBoxStrokeColorStateList(requireContext().getBoxStrokeColor(R.color.light_yellow_30))
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(
                    R.string.hint_start_typing_your_value, getString(R.string.scr_lbl_comments)
                ),
                endIconModeType = null,
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )

            mOtherDetails.addView(mLayoutCompatComments)

            /* // status
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewStaus = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewStaus = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatStaus = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewStaus.setText(R.string.scr_lbl_status)
             mOtherDetails.addView(mLayoutCompatStaus)*/

        } else if (reportType.equals(REPORT_TYPE_CURB_REPORT, ignoreCase = true)) {
//            Front Photo, Back Photo, Passenger Side Photo, Driver Side Photo.
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 1

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewBlock =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBlock = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutBlock?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_block),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )

            mLocationDetails.addView(mLayoutCompatBlock)

            // Street
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewStreet =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStreet = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutStreet?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_street),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )

            mLocationDetails.addView(mLayoutCompatStreet)

            // direction
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDirection =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDirection =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDirection = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutDirection?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_direction),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )

            mLocationDetails.addView(mLayoutCompatDirection)

            /*// device_id
            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAppComTextViewDeviceId = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDeviceId = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewDeviceId.setText(R.string.scr_lbl_device)
            mOtherDetails.addView(mLayoutCompatDeviceId)*/

            // enforceable
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewEnforceable =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatEnforceable =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutEnforceable = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutEnforceable?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_enforceable),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutEnforceable,
                appCompatAutoCompleteTextView = mAutoComTextViewEnforceable
            )

            mOtherDetails.addView(mLayoutCompatEnforceable)

            // CurbColor
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewCurbColor =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCurbColor =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutCurbColor = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutCurbColor?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_curb_color),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutCurbColor,
                appCompatAutoCompleteTextView = mAutoComTextViewCurbColor
            )

            mOtherDetails.addView(mLayoutCompatCurbColor)


            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)

            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )

            mOtherDetails.addView(mLayoutCompatComments)

            /* // status
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewStaus = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewStaus = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatStaus = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewStaus.setText(R.string.scr_lbl_status)
             mOtherDetails.addView(mLayoutCompatStaus)*/

        } else if (reportType.equals(REPORT_TYPE_EOW_OFFICER_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            /* // Block
             mAppComTextViewBlock = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewBlock = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewBlock.setText(R.string.scr_lbl_block)
             mLocationDetails.addView(mLayoutCompatBlock)

             // Street
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewStreet = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewStreet = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewStreet.setText(R.string.scr_lbl_street)
             mLocationDetails.addView(mLayoutCompatStreet)*/

            // device_id
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewDeviceId =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDeviceId =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDeviceId = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutDeviceId?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_device),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDeviceId,
                appCompatAutoCompleteTextView = mAutoComTextViewDeviceId
            )

            mOtherDetails.addView(mLayoutCompatDeviceId)

//            // Date
//            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
//            mAppComTextViewDate = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewDate = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
//            mLayoutCompatDate = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
//            mAppComTextViewDate.setText(R.string.scr_lbl_date_report)
//            mOtherDetails.addView(mLayoutCompatDate)

            // DutyHours
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDutyHours =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDutyHours =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutDutyHours = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            mInputLayoutDutyHours?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_duty_hours),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutDutyHours,
                appCompatAutoCompleteTextView = mAutoComTextViewDutyHours
            )

            mOtherDetails.addView(mLayoutCompatDutyHours)

//            mInputLayoutDutyHours = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutDutyHours.endIconMode = END_ICON_NONE

            /* // Officer
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewOfficer = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewOfficer = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatOfficer = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewOfficer.setText(R.string.scr_lbl_officer)
             mOtherDetails.addView(mLayoutCompatOfficer)*/

            // lunch taken
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewLunchTaken =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLunchTaken =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutLunchTaken = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutLunchTaken?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_lunch_taken),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutLunchTaken,
                appCompatAutoCompleteTextView = mAutoComTextViewLunchTaken
            )

            mOtherDetails.addView(mLayoutCompatLunchTaken)

            // first 10 min break
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewFirst10MinBreak =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatFirst10MinBreak =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutFirst10MinBreak =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutFirst10MinBreak?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_first_10_mins_break),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutFirst10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewFirst10MinBreak
            )

            mOtherDetails.addView(mLayoutCompatFirst10MinBreak)

            // second 10 min break
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSecond10MinBreak =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSecond10MinBreak =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutSecond10MinBreak =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutSecond10MinBreak?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_second_10_mins_break),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutSecond10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewSecond10MinBreak
            )

            mOtherDetails.addView(mLayoutCompatSecond10MinBreak)

            // unit_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewUnitNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatUnitNo = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutUnitNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutUnitNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_unit_no),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutUnitNo,
                appCompatAutoCompleteTextView = mAutoComTextViewUnitNo
            )

            mOtherDetails.addView(mLayoutCompatUnitNo)

            // Assigned Area
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewAssignedArea =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatAssignedArea =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutAssignedArea = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutAssignedArea?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_assigned_area),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutAssignedArea,
                appCompatAutoCompleteTextView = mAutoComTextViewAssignedArea
            )

            mOtherDetails.addView(mLayoutCompatAssignedArea)

            // Ossi DeviceNo
            /*  rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
              mAppComTextViewOssiDeviceNo = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
              mAutoComTextViewOssiDeviceNo = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
              mLayoutCompatOssiDeviceNo = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
              mAppComTextViewOssiDeviceNo.setText(R.string.scr_lbl_ossi_device_no)
              mOtherDetails.addView(mLayoutCompatOssiDeviceNo)*/


            /* // handheld_unit_no
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewHandheldUnitNo = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewHandheldUnitNo = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatHandheldUnitNo = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewHandheldUnitNo.setText(R.string.scr_lbl_handheld_unit_no)
             mOtherDetails.addView(mLayoutCompatHandheldUnitNo)*/

            // citation issue
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewCitationsIssued =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCitationsIssued =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCitationsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            mInputLayoutCitationsIssued?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_citation_issue),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )

            mOtherDetails.addView(mLayoutCompatCitationsIssued)

            mInputLayoutCitationsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCitationsIssued?.endIconMode = END_ICON_NONE

            // special_enforcement_request
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSpecialEnforcementRequest =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSpecialEnforcementRequest =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutSpecialEnforcementRequest =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            mInputLayoutSpecialEnforcementRequest?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_special_enforcement_request),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutSpecialEnforcementRequest,
                appCompatAutoCompleteTextView = mAutoComTextViewSpecialEnforcementRequest
            )

            mOtherDetails.addView(mLayoutCompatSpecialEnforcementRequest)

            mInputLayoutSpecialEnforcementRequest =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutSpecialEnforcementRequest?.endIconMode = END_ICON_NONE

            /*// status
            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAppComTextViewStaus = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStaus = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewStaus.setText(R.string.scr_lbl_status)
            mOtherDetails.addView(mLayoutCompatStaus)*/

        } else if (reportType.equals(REPORT_TYPE_PART_EOW_OFFICER_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            /* // Block
             mAppComTextViewBlock = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewBlock = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewBlock.setText(R.string.scr_lbl_block)
             mLocationDetails.addView(mLayoutCompatBlock)

             // Street
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewStreet = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewStreet = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewStreet.setText(R.string.scr_lbl_street)
             mLocationDetails.addView(mLayoutCompatStreet)*/

            // device_id
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewDeviceId =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDeviceId =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDeviceId = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutDeviceId?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_device),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDeviceId,
                appCompatAutoCompleteTextView = mAutoComTextViewDeviceId
            )

            mOtherDetails.addView(mLayoutCompatDeviceId)

//            // Date
//            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
//            mAppComTextViewDate = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewDate = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
//            mLayoutCompatDate = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
//            mAppComTextViewDate.setText(R.string.scr_lbl_date_report)
//            mOtherDetails.addView(mLayoutCompatDate)

            // DutyHours
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDutyHours =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDutyHours =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutDutyHours = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            mInputLayoutDutyHours?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_duty_hours),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutDutyHours,
                appCompatAutoCompleteTextView = mAutoComTextViewDutyHours
            )

            mOtherDetails.addView(mLayoutCompatDutyHours)

//            mInputLayoutDutyHours = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutDutyHours.endIconMode = END_ICON_NONE

            /* // Officer
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewOfficer = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewOfficer = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatOfficer = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewOfficer.setText(R.string.scr_lbl_officer)
             mOtherDetails.addView(mLayoutCompatOfficer)*/

            /*// lunch taken
            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAppComTextViewLunchTaken = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLunchTaken = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLunchTaken = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewLunchTaken.setText(R.string.scr_lbl_lunch_taken)
            mOtherDetails.addView(mLayoutCompatLunchTaken)*/

            // first 10 min break
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewFirst10MinBreak =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatFirst10MinBreak =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutFirst10MinBreak =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutFirst10MinBreak?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_first_10_mins_break),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutFirst10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewFirst10MinBreak
            )

            mOtherDetails.addView(mLayoutCompatFirst10MinBreak)

            // second 10 min break
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSecond10MinBreak =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSecond10MinBreak =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutSecond10MinBreak =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutSecond10MinBreak?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_second_10_mins_break),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutSecond10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewSecond10MinBreak
            )

            mOtherDetails.addView(mLayoutCompatSecond10MinBreak)

            // unit_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewUnitNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatUnitNo = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutUnitNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutUnitNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_unit_no),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutUnitNo,
                appCompatAutoCompleteTextView = mAutoComTextViewUnitNo
            )

            mOtherDetails.addView(mLayoutCompatUnitNo)

            // Assigned Area
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewAssignedArea =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatAssignedArea =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutAssignedArea = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutAssignedArea?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_assigned_area),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutAssignedArea,
                appCompatAutoCompleteTextView = mAutoComTextViewAssignedArea
            )

            mOtherDetails.addView(mLayoutCompatAssignedArea)

            // Ossi DeviceNo
            /*  rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
              mAppComTextViewOssiDeviceNo = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
              mAutoComTextViewOssiDeviceNo = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
              mLayoutCompatOssiDeviceNo = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
              mAppComTextViewOssiDeviceNo.setText(R.string.scr_lbl_ossi_device_no)
              mOtherDetails.addView(mLayoutCompatOssiDeviceNo)*/


            /* // handheld_unit_no
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewHandheldUnitNo = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewHandheldUnitNo = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatHandheldUnitNo = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewHandheldUnitNo.setText(R.string.scr_lbl_handheld_unit_no)
             mOtherDetails.addView(mLayoutCompatHandheldUnitNo)*/

            // citation issue
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewCitationsIssued =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCitationsIssued =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCitationsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            mInputLayoutCitationsIssued?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_citation_issue),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )

            mOtherDetails.addView(mLayoutCompatCitationsIssued)

            mInputLayoutCitationsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCitationsIssued?.endIconMode = END_ICON_NONE

            // special_enforcement_request
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSpecialEnforcementRequest =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSpecialEnforcementRequest =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)

            mOtherDetails.addView(mLayoutCompatSpecialEnforcementRequest)

            mInputLayoutSpecialEnforcementRequest =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutSpecialEnforcementRequest?.endIconMode = END_ICON_NONE

            mInputLayoutSpecialEnforcementRequest?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_special_enforcement_request),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutSpecialEnforcementRequest,
                appCompatAutoCompleteTextView = mAutoComTextViewSpecialEnforcementRequest
            )

            /*// status
            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAppComTextViewStaus = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStaus = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewStaus.setText(R.string.scr_lbl_status)
            mOtherDetails.addView(mLayoutCompatStaus)*/

        } else if (reportType.equals(REPORT_TYPE_HAND_HELD_MALFUNCTION_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            /*// Block
            mAppComTextViewBlock = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewBlock.setText(R.string.scr_lbl_block)
            mLocationDetails.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAppComTextViewStreet = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewStreet.setText(R.string.scr_lbl_street)
            mLocationDetails.addView(mLayoutCompatStreet)*/

            // device_id
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDeviceId =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDeviceId =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDeviceId = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutDeviceId?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_device),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDeviceId,
                appCompatAutoCompleteTextView = mAutoComTextViewDeviceId
            )

            mOtherDetails.addView(mLayoutCompatDeviceId)

            /* // handheld_unit_no
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewHandheldUnitNo = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewHandheldUnitNo = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatHandheldUnitNo = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewHandheldUnitNo.setText(R.string.scr_lbl_handheld_unit_no)
             mOtherDetails.addView(mLayoutCompatHandheldUnitNo)*/

            // TriedToRestartHandHeld
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTriedToRestartHandHeld =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTriedToRestartHandHeld =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutTriedToRestartHandheld =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutTriedToRestartHandheld?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_tried_to_restart_hand_held),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutTriedToRestartHandheld,
                appCompatAutoCompleteTextView = mAutoComTextViewTriedToRestartHandHeld
            )

            mOtherDetails.addView(mLayoutCompatTriedToRestartHandHeld)

            // PrintingCorrectly
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewPrintingCorrectly =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPrintingCorrectly =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPrintingCorrectly =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutPrintingCorrectly?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_printing_correctly),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPrintingCorrectly,
                appCompatAutoCompleteTextView = mAutoComTextViewPrintingCorrectly
            )

            mOtherDetails.addView(mLayoutCompatPrintingCorrectly)

            // OverHeating
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewOverHeating =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatOverHeating =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutOverHeating = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutOverHeating?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_over_heating),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutOverHeating,
                appCompatAutoCompleteTextView = mAutoComTextViewOverHeating
            )

            mOtherDetails.addView(mLayoutCompatOverHeating)

            // Battery Hold Charge
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewBatteryHoldCharge =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBatteryHoldCharge =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBatteryHoldCharge =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutBatteryHoldCharge?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_battery_hold_charge),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBatteryHoldCharge,
                appCompatAutoCompleteTextView = mAutoComTextViewBatteryHoldCharge
            )

            mOtherDetails.addView(mLayoutCompatBatteryHoldCharge)

            // Internet Connectivity
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewInternetConnectivity =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatInternetConnectivity =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutInternetConnectivity =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutInternetConnectivity?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_internet_connectivity),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutInternetConnectivity,
                appCompatAutoCompleteTextView = mAutoComTextViewInternetConnectivity
            )

            mOtherDetails.addView(mLayoutCompatInternetConnectivity)

            // Describe Hand Held Malfunction In Detail
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDescribeHandHeldMalfunctionInDetail =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDescribeHandHeldMalfunctionInDetail =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutDescribeHandHeldMalfunctionInDetail =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutDescribeHandHeldMalfunctionInDetail?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_describe_hand_held_malfunction_in_detail),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutDescribeHandHeldMalfunctionInDetail,
                appCompatAutoCompleteTextView = mAutoComTextViewDescribeHandHeldMalfunctionInDetail
            )

            mOtherDetails.addView(mLayoutCompatDescribeHandHeldMalfunctionInDetail)

            mInputLayoutDescribeHandHeldMalfunctionInDetail =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutDescribeHandHeldMalfunctionInDetail?.endIconMode = END_ICON_NONE


            // status
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewStaus =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStaus = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStatus = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)

            textInputLayoutStatus?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_status),
                placeholder = getString(R.string.hint_select_or_type)
            )


            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStatus,
                appCompatAutoCompleteTextView = mAutoComTextViewStaus
            )

            mOtherDetails.addView(mLayoutCompatStaus)
        } else if (reportType.equals(REPORT_TYPE_SIGN_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            // Block
            mAutoComTextViewBlock =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBlock = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutBlock?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_block),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )

            mLocationDetails.addView(mLayoutCompatBlock)

            // Street
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewStreet =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStreet = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutStreet?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_street),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )

            mLocationDetails.addView(mLayoutCompatStreet)

            // direction
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDirection =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDirection =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDirection = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutDirection?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_direction),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )

//            mAutoComTextViewDirection.setText("S")
            mLocationDetails.addView(mLayoutCompatDirection)

            /* // device_id
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewDeviceId = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewDeviceId = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatDeviceId = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewDeviceId.setText(R.string.scr_lbl_device)
             mOtherDetails.addView(mLayoutCompatDeviceId)*/

            // enforceable
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewEnforceable =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatEnforceable =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutEnforceable = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutEnforceable?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_enforceable),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutEnforceable,
                appCompatAutoCompleteTextView = mAutoComTextViewEnforceable
            )

            mOtherDetails.addView(mLayoutCompatEnforceable)

            // Graffiti
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewGraffiti =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatGraffiti =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutGraffiti = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutGraffiti?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_graffiti),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutGraffiti,
                appCompatAutoCompleteTextView = mAutoComTextViewGraffiti
            )

            mOtherDetails.addView(mLayoutCompatGraffiti)

            // missing_sign
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewMissingSign =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMissingSign =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMissingSign = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMissingSign?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_missing_sign),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMissingSign,
                appCompatAutoCompleteTextView = mAutoComTextViewMissingSign
            )

            mOtherDetails.addView(mLayoutCompatMissingSign)

            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)

            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )

            mOtherDetails.addView(mLayoutCompatComments)

            /* // status
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewStaus = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewStaus = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatStaus = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewStaus.setText(R.string.scr_lbl_status)
             mOtherDetails.addView(mLayoutCompatStaus)*/

            tvCameraTitle.setText("Image Section")

        } else if (reportType.equals(REPORT_TYPE_VEHICLE_INSPECTION_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                )
            ) {
                imageValidationCount = 7
            } else {
                imageValidationCount = 4
            }
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            /*// Block
            mAppComTextViewBlock = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewBlock.setText(R.string.scr_lbl_block)
            mLocationDetails.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAppComTextViewStreet = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewStreet.setText(R.string.scr_lbl_street)
            mLocationDetails.addView(mLayoutCompatStreet)

            // device_id
            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAppComTextViewDeviceId = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDeviceId = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mAppComTextViewDeviceId.setText(R.string.scr_lbl_device)
            mOtherDetails.addView(mLayoutCompatDeviceId)*/

            /* // Officer
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewOfficer = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewOfficer = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatOfficer = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewOfficer.setText(R.string.scr_lbl_officer)
             mOtherDetails.addView(mLayoutCompatOfficer)*/

            // Vehicle
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewVehicle =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVehicle =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutVehicle = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutVehicle?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vehicle),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutVehicle,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicle
            )

            mOtherDetails.addView(mLayoutCompatVehicle)

            // StartingMileage
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewStartingMileage =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStartingMileage =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStartingMileage =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutStartingMileage?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_starting_mileage),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStartingMileage,
                appCompatAutoCompleteTextView = mAutoComTextViewStartingMileage
            )

            mOtherDetails.addView(mLayoutCompatStartingMileage)

            // GasLevel
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewGasLevel =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatGasLevel =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutGasLevel = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutGasLevel?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_gas_level),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutGasLevel,
                appCompatAutoCompleteTextView = mAutoComTextViewGasLevel
            )

            mOtherDetails.addView(mLayoutCompatGasLevel)

            // LightBar
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLightBar =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLightBar =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutLightBar = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutLightBar?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_light_bar),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutLightBar,
                appCompatAutoCompleteTextView = mAutoComTextViewLightBar
            )

            mOtherDetails.addView(mLayoutCompatLightBar)

            // DashBoardIndications
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDashBoardIndications =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDashBoardIndications =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDashBoardIndications =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutDashBoardIndications?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_dashboard_indications),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDashBoardIndications,
                appCompatAutoCompleteTextView = mAutoComTextViewDashBoardIndications
            )

            mOtherDetails.addView(mLayoutCompatDashBoardIndications)

            // SeatBeltOperational
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSeatBeltOperational =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSeatBeltOperational =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutSeatBeltOperational =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutSeatBeltOperational?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_seat_belt_operational),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutSeatBeltOperational,
                appCompatAutoCompleteTextView = mAutoComTextViewSeatBeltOperational
            )
            mOtherDetails.addView(mLayoutCompatSeatBeltOperational)

            if (!BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                ) && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                )
            ) {
                // brakes
                rootView =
                    View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
                mAutoComTextViewBrakes =
                    rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
                mLayoutCompatBrakes =
                    rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
                textInputLayoutBrakes = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
                textInputLayoutBrakes?.setupTextInputLayout(
                    hintText = getString(R.string.scr_lbl_brakes),
                    placeholder = getString(R.string.hint_select_or_type)
                )

                setCrossClearButton(
                    context = requireContext(),
                    textInputLayout = textInputLayoutBrakes,
                    appCompatAutoCompleteTextView = mAutoComTextViewBrakes
                )

                mOtherDetails.addView(mLayoutCompatBrakes)


                // BrakeLights
                rootView =
                    View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

                mAutoComTextViewBrakeLights =
                    rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
                mLayoutCompatBrakeLights =
                    rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
                textInputLayoutBrakeLights =
                    rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
                textInputLayoutBrakeLights?.setupTextInputLayout(
                    hintText = getString(R.string.scr_lbl_brake_lights),
                    placeholder = getString(R.string.hint_select_or_type)
                )

                setCrossClearButton(
                    context = requireContext(),
                    textInputLayout = textInputLayoutBrakeLights,
                    appCompatAutoCompleteTextView = mAutoComTextViewBrakeLights
                )

                mOtherDetails.addView(mLayoutCompatBrakeLights)
            }

            // HeadLights
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewHeadLights =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatHeadLights =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutHeadLights = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutHeadLights?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_head_lights),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutHeadLights,
                appCompatAutoCompleteTextView = mAutoComTextViewHeadLights
            )

            mOtherDetails.addView(mLayoutCompatHeadLights)

            // TurnSignals
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTurnSignals =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTurnSignals =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutSignals = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutSignals?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_turn_signals),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutSignals,
                appCompatAutoCompleteTextView = mAutoComTextViewTurnSignals
            )

            mOtherDetails.addView(mLayoutCompatTurnSignals)

            if (!BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                ) && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                )
            ) {
                // SteeringWheelOperational
                rootView =
                    View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

                mAutoComTextViewSteeringWheelOperational =
                    rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
                mLayoutCompatSteeringWheelOperational =
                    rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
                textInputLayoutSteeringWheelOperational =
                    rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
                textInputLayoutSteeringWheelOperational?.setupTextInputLayout(
                    hintText = getString(R.string.scr_lbl_steering_wheel_operational),
                    placeholder = getString(R.string.hint_select_or_type)
                )

                setCrossClearButton(
                    context = requireContext(),
                    textInputLayout = textInputLayoutSteeringWheelOperational,
                    appCompatAutoCompleteTextView = mAutoComTextViewSteeringWheelOperational
                )
                mOtherDetails.addView(mLayoutCompatSteeringWheelOperational)
            }

            // windshield_visibility
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewWindshieldVisibility =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatWindshieldVisibility =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutWindshieldVisibility =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutWindshieldVisibility?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_windshield_visibility),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutWindshieldVisibility,
                appCompatAutoCompleteTextView = mAutoComTextViewWindshieldVisibility
            )
            mOtherDetails.addView(mLayoutCompatWindshieldVisibility)

            // side_and_rear_view_mirrors_operational
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSideAndRearViewMirrorsOperational =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSideAndRearViewMirrorsOperational =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutSideAndRearViewMirrorsOperational =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutSideAndRearViewMirrorsOperational?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_side_and_rear_view_mirrors_operational),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutSideAndRearViewMirrorsOperational,
                appCompatAutoCompleteTextView = mAutoComTextViewSideAndRearViewMirrorsOperational
            )
            mOtherDetails.addView(mLayoutCompatSideAndRearViewMirrorsOperational)

            // windshield_wipers_operational
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewWindshieldWipersOperational =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatWindshieldWipersOperational =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutWindshieldWipersOperational =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutWindshieldWipersOperational?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_windshield_wipers_operational),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutWindshieldWipersOperational,
                appCompatAutoCompleteTextView = mAutoComTextViewWindshieldWipersOperational
            )

            mOtherDetails.addView(mLayoutCompatWindshieldWipersOperational)

            if (!BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                ) && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                )
            ) {
                // scr_lbl_vehicle_registration_and_insurance
                rootView =
                    View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

                mAutoComTextViewVehicleRegistrationAndInsurance =
                    rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
                mLayoutCompatVehicleRegistrationAndInsurance =
                    rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
                textInputLayoutVehicleRegistrationAndInsurance =
                    rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
                textInputLayoutVehicleRegistrationAndInsurance?.setupTextInputLayout(
                    hintText = getString(R.string.scr_lbl_vehicle_registration_and_insurance),
                    placeholder = getString(R.string.hint_select_or_type)
                )

                setCrossClearButton(
                    context = requireContext(),
                    textInputLayout = textInputLayoutVehicleRegistrationAndInsurance,
                    appCompatAutoCompleteTextView = mAutoComTextViewVehicleRegistrationAndInsurance
                )

                mOtherDetails.addView(mLayoutCompatVehicleRegistrationAndInsurance)
            }

            if (!BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                ) && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                )
            ) {
                // cones_six_per_vehicle
                rootView =
                    View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

                mAutoComTextViewConesSixPerVehicle =
                    rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
                mLayoutCompatConesSixPerVehicle =
                    rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
                textInputLayoutConesSixPerVehicle =
                    rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
                textInputLayoutConesSixPerVehicle?.setupTextInputLayout(
                    hintText = getString(R.string.scr_lbl_cones_six_per_vehicle),
                    placeholder = getString(R.string.hint_select_or_type)
                )

                setCrossClearButton(
                    context = requireContext(),
                    textInputLayout = textInputLayoutConesSixPerVehicle,
                    appCompatAutoCompleteTextView = mAutoComTextViewConesSixPerVehicle
                )

                mOtherDetails.addView(mLayoutCompatConesSixPerVehicle)

                // scr_lbl_first_aid_kit
                rootView =
                    View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

                mAutoComTextViewFirstAidKit =
                    rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
                mLayoutCompatFirstAidKit =
                    rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
                textInputLayoutFirstAidKit =
                    rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
                textInputLayoutFirstAidKit?.setupTextInputLayout(
                    hintText = getString(R.string.scr_lbl_first_aid_kit),
                    placeholder = getString(R.string.hint_select_or_type)
                )

                setCrossClearButton(
                    context = requireContext(),
                    textInputLayout = textInputLayoutFirstAidKit,
                    appCompatAutoCompleteTextView = mAutoComTextViewFirstAidKit
                )

                mOtherDetails.addView(mLayoutCompatFirstAidKit)
            }

            // horn
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewHorn =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatHorn = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutHorn = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutHorn?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_horn),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutHorn,
                appCompatAutoCompleteTextView = mAutoComTextViewHorn
            )

            mOtherDetails.addView(mLayoutCompatHorn)

            // scr_lbl_interior_cleanliness
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewInteriorCleanliness =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatInteriorCleanliness =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutInteriorCleanliness =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutInteriorCleanliness?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_interior_cleanliness),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutInteriorCleanliness,
                appCompatAutoCompleteTextView = mAutoComTextViewInteriorCleanliness
            )

            mOtherDetails.addView(mLayoutCompatInteriorCleanliness)

            // exterior_cleanliness
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewExteriorCleanliness =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatExteriorCleanliness =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutExteriorCleanliness =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutExteriorCleanliness?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_exterior_cleanliness),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutExteriorCleanliness,
                appCompatAutoCompleteTextView = mAutoComTextViewExteriorCleanliness
            )

            mOtherDetails.addView(mLayoutCompatExteriorCleanliness)

            // lpr_lens_free_of_debris
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewLprLensFreeOfDebris =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLprLensFreeOfDebris =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutLprLensFreeOfDebris =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutLprLensFreeOfDebris?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_lpr_lens_free_of_debris),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutLprLensFreeOfDebris,
                appCompatAutoCompleteTextView = mAutoComTextViewLprLensFreeOfDebris
            )
            mOtherDetails.addView(mLayoutCompatLprLensFreeOfDebris)

            // visible_leaks
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewVisibleLeaks =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVisibleLeaks =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutVisibleLeaks = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutVisibleLeaks?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_visible_leaks),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutVisibleLeaks,
                appCompatAutoCompleteTextView = mAutoComTextViewVisibleLeaks
            )
            mOtherDetails.addView(mLayoutCompatVisibleLeaks)

            // scr_lbl_tires_visual_inspection
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTiresVisualInspection =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTiresVisualInspection =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutTiresVisualInspection =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutTiresVisualInspection?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_tires_visual_inspection),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutTiresVisualInspection,
                appCompatAutoCompleteTextView = mAutoComTextViewTiresVisualInspection
            )

            mOtherDetails.addView(mLayoutCompatTiresVisualInspection)


            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )

            mOtherDetails.addView(mLayoutCompatComments)

            /* // status
             rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
             mAppComTextViewStaus = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
             mAutoComTextViewStaus = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
             mLayoutCompatStaus = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
             mAppComTextViewStaus.setText(R.string.scr_lbl_status)
             mOtherDetails.addView(mLayoutCompatStaus)*/
            tvCameraTitle.setText("Image Section")
        } else if (reportType.equals(
                REPORT_TYPE_72_HOURS_MARKED_VEHICLE_REPORT, ignoreCase = true
            )
        ) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 2

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            // Block
            mAutoComTextViewBlock =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBlock = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutBlock?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_block),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )

            mLocationDetails.addView(mLayoutCompatBlock)

            // Street
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewStreet =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStreet = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutStreet?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_street),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )

            mLocationDetails.addView(mLayoutCompatStreet)

            // device_id
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewDeviceId =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDeviceId =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDeviceId = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutDeviceId?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_device),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDeviceId,
                appCompatAutoCompleteTextView = mAutoComTextViewDeviceId
            )
            mOtherDetails.addView(mLayoutCompatDeviceId)

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )

            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true
                )
            ) {
                mLayoutCompatLine?.visibility = View.GONE
            }

            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )

            mOtherDetails.addView(mLayoutCompatComments)

            // status
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewStaus =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStaus = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStatus = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutStatus?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_status),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStatus,
                appCompatAutoCompleteTextView = mAutoComTextViewStaus
            )

            mOtherDetails.addView(mLayoutCompatStaus)
        } else if (reportType.equals(REPORT_TYPE_BIKE_INSPECTION_REPORT, ignoreCase = true)) {

            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 4

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            // scr_lbl_tire_pressure
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTirePressure =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTirePressure =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutTirePressure = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutTirePressure?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_tire_pressure),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutTirePressure,
                appCompatAutoCompleteTextView = mAutoComTextViewTirePressure
            )
            mOtherDetails.addView(mLayoutCompatTirePressure)

            // assigned_bike
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewAssignedBike =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatAssignedBike =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutAssignedBike = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutAssignedBike?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_assigned_bike),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutAssignedBike,
                appCompatAutoCompleteTextView = mAutoComTextViewAssignedBike
            )
            mOtherDetails.addView(mLayoutCompatAssignedBike)

            // BreaksRotors
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewBreaksRotors =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBreaksRotors =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBreakRotors = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutBreakRotors?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_breaks_rotors),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBreakRotors,
                appCompatAutoCompleteTextView = mAutoComTextViewBreaksRotors
            )
            mOtherDetails.addView(mLayoutCompatBreaksRotors)

            // scr_lbl_lights_and_reflectors
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewLightsAndReflectors =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLightsAndReflectors =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutLightsAndReflectors =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutLightsAndReflectors?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_lights_and_reflectors),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutLightsAndReflectors,
                appCompatAutoCompleteTextView = mAutoComTextViewLightsAndReflectors
            )

            mOtherDetails.addView(mLayoutCompatLightsAndReflectors)

            // scr_lbl_chain_crank
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewChainCrank =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatChainCrank =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutChainCrank = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutChainCrank?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_chain_crank),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutChainCrank,
                appCompatAutoCompleteTextView = mAutoComTextViewChainCrank
            )
            mOtherDetails.addView(mLayoutCompatChainCrank)

            // BatteryFreeOfDebris
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewBatteryFreeOfDebris =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBatteryFreeOfDebris =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBatteryFreeDebris =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutBatteryFreeDebris?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_battery_free_of_debris),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBatteryFreeDebris,
                appCompatAutoCompleteTextView = mAutoComTextViewBatteryFreeOfDebris
            )
            mOtherDetails.addView(mLayoutCompatBatteryFreeOfDebris)

            // scr_lbl_flat_pack
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewFlatPack =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatFlatPack =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutFlatPack = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutFlatPack?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_flat_pack),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutFlatPack,
                appCompatAutoCompleteTextView = mAutoComTextViewFlatPack
            )

            mOtherDetails.addView(mLayoutCompatFlatPack)

            // scr_lbl_first_aid_kit
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewFirstAidKit =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatFirstAidKit =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutFirstAidKit = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutFirstAidKit?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_first_aid_kit),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutFirstAidKit,
                appCompatAutoCompleteTextView = mAutoComTextViewFirstAidKit
            )

            mOtherDetails.addView(mLayoutCompatFirstAidKit)

            // scr_lbl_helmel_visual_inspection
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTiresVisualInspection =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTiresVisualInspection =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutTiresVisualInspection =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutTiresVisualInspection?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_helmet_visual_inspection),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutTiresVisualInspection,
                appCompatAutoCompleteTextView = mAutoComTextViewTiresVisualInspection
            )
            mOtherDetails.addView(mLayoutCompatTiresVisualInspection)

            // scr_lbl_Bike_Glassess
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewBikeGlassess =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBikeGlassess =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutGlasses = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutGlasses?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_bike_glassess),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutGlasses,
                appCompatAutoCompleteTextView = mAutoComTextViewBikeGlassess
            )

            mOtherDetails.addView(mLayoutCompatBikeGlassess)

            // scr_lbl_BatterCharge
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewBatteryCharge =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBatteryCharge =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBatteryCharge = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutBatteryCharge?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_battery_charge),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBatteryCharge,
                appCompatAutoCompleteTextView = mAutoComTextViewBatteryCharge
            )
            mOtherDetails.addView(mLayoutCompatBatteryCharge)

            // scr_lbl_BatterCharge
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewGloveVisualInspection =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatGloveVisualInspection =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutGloveVisualInspection =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutGloveVisualInspection?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_glove_visual_inspection),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutGloveVisualInspection,
                appCompatAutoCompleteTextView = mAutoComTextViewGloveVisualInspection
            )

            mOtherDetails.addView(mLayoutCompatGloveVisualInspection)


            tvCameraTitle.setText("Image Section")

        } else if (reportType.equals(REPORT_TYPE_EOW_SUPERVISOR_SHIFT_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            // Total Enforcement Personnel
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTotalEnforcementPersonnel =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTotalEnforcementPersonnel =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutTotalEnforcementPersonnel =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutTotalEnforcementPersonnel?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_total_enforcement_personnel),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutTotalEnforcementPersonnel,
                appCompatAutoCompleteTextView = mAutoComTextViewTotalEnforcementPersonnel
            )
            mOtherDetails.addView(mLayoutCompatTotalEnforcementPersonnel)

            // scr_lbl_first_10_mins_break
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewFirst10MinBreak =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatFirst10MinBreak =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutFirst10MinBreak =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutFirst10MinBreak?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_first_10_mins_break),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutFirst10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewFirst10MinBreak
            )
            mOtherDetails.addView(mLayoutCompatFirst10MinBreak)

            // scr_lbl_lunch
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLunchTaken =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLunchTaken =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutLunchTaken = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutLunchTaken?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_lunch),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutLunchTaken,
                appCompatAutoCompleteTextView = mAutoComTextViewLunchTaken
            )
            mOtherDetails.addView(mLayoutCompatLunchTaken)

            // scr_lbl_second_10_mins_break
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSecond10MinBreak =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSecond10MinBreak =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutSecond10MinBreak =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutSecond10MinBreak?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_second_10_mins_break),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutSecond10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewSecond10MinBreak
            )
            mOtherDetails.addView(mLayoutCompatSecond10MinBreak)

            // scr_lbl_complaints_towards_officer
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewComplaintsTowardsOfficer =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComplaintsTowardsOfficer =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutComplaintsTowardsOfficer =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutComplaintsTowardsOfficer?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_complaints_towards_officer),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutComplaintsTowardsOfficer,
                appCompatAutoCompleteTextView = mAutoComTextViewComplaintsTowardsOfficer
            )
            mOtherDetails.addView(mLayoutCompatComplaintsTowardsOfficer)

            mInputLayoutComplaintsTowardsOfficer?.endIconMode = END_ICON_NONE

            // scr_lbl_resident_complaints
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewResidentComplaints =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatResidentComplaints =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutResidentComplaints =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutResidentComplaints?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_resident_complaints),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutResidentComplaints,
                appCompatAutoCompleteTextView = mAutoComTextViewResidentComplaints
            )

            mOtherDetails.addView(mLayoutCompatResidentComplaints)

            mInputLayoutResidentComplaints?.endIconMode = END_ICON_NONE

            // scr_lbl_warnings_issued
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewWarningsIssued =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatWarningsIssued =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutWarningsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutWarningsIssued?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_warnings_issued),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutWarningsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewWarningsIssued
            )

            mOtherDetails.addView(mLayoutCompatWarningsIssued)

            mInputLayoutWarningsIssued?.endIconMode = END_ICON_NONE

            // scr_lbl_warnings_issued
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewCitationsIssued =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCitationsIssued =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCitationsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCitationsIssued?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_citation_issued),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )
            mOtherDetails.addView(mLayoutCompatCitationsIssued)

            mInputLayoutCitationsIssued?.endIconMode = END_ICON_NONE

            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_SPECIAL_ASSIGNMENT_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            // Block
            mAutoComTextViewBlock =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBlock = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutBlock?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_block),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )
            mLocationDetails.addView(mLayoutCompatBlock)

            // Street
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewStreet =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStreet = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutStreet?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_street),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )
            mLocationDetails.addView(mLayoutCompatStreet)

//            // Street
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewDirection =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDirection =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDirection = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutDirection?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_direction),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )
            mLocationDetails.addView(mLayoutCompatDirection)

// scr_lbl_warnings_issued
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewWarningsIssued =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatWarningsIssued =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutWarningsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutWarningsIssued?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_warnings_issued),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutWarningsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewWarningsIssued
            )
            mOtherDetails.addView(mLayoutCompatWarningsIssued)

            mInputLayoutWarningsIssued?.endIconMode = END_ICON_NONE

// scr_lbl_warnings_issued
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewCitationsIssued =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCitationsIssued =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCitationsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCitationsIssued?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_citation_issued),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )
            mOtherDetails.addView(mLayoutCompatCitationsIssued)

            mInputLayoutCitationsIssued?.endIconMode = END_ICON_NONE

// scr_lbl_vehicles_marked
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewVehicleMark =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVehicleMark =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutVehicleMark = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutVehicleMark?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vehicles_marked),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutVehicleMark,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleMark
            )

            mOtherDetails.addView(mLayoutCompatVehicleMark)

            mInputLayoutVehicleMark?.endIconMode = END_ICON_NONE

// scr_lbl_time_marked
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewTimeMark =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTimeMark =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutTimeMark = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutTimeMark?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_time_marked),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutTimeMark,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark
            )
            mOtherDetails.addView(mLayoutCompatTimeMark)
//            mAutoComTextViewTimeMark.setHint("dd/mm/yyyy HH:mm")
            mAutoComTextViewTimeMark?.setOnClickListener {
                requireActivity().hideSoftKeyboard()
                openTimePicker(mAutoComTextViewTimeMark)
            }

//            mInputLayoutTimeMark = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutTimeMark.endIconMode = END_ICON_NONE
//            mAutoComTextViewTimeMark.setHint("10PM to 6PM")

// scr_lbl_time_marked2
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewTimeMark2 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTimeMark2 =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutTimeMark2 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutTimeMark2?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_time_marked_2),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutTimeMark2,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark2
            )
            mOtherDetails.addView(mLayoutCompatTimeMark2)

//            mInputLayoutTimeMark2 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutTimeMark2.endIconMode = END_ICON_NONE
//            mAutoComTextViewTimeMark.setHint("10PM to 6PM")

// scr_lbl_time_marked3
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewTimeMark3 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTimeMark3 =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutTimeMark3 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutTimeMark3?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_time_marked_3),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutTimeMark3,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark3
            )
            mOtherDetails.addView(mLayoutCompatTimeMark3)

//            mInputLayoutTimeMark3 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutTimeMark3.endIconMode = END_ICON_NONE
//            mAutoComTextViewTimeMark.setHint("10PM to 6PM")

// scr_lbl_time_marked4
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewTimeMark4 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTimeMark4 =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutTimeMark4 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutTimeMark4?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_time_marked_4),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutTimeMark4,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark4
            )
            mOtherDetails.addView(mLayoutCompatTimeMark4)

//            mInputLayoutTimeMark4 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutTimeMark4.endIconMode = END_ICON_NONE
//            mAutoComTextViewTimeMark.setHint("10PM to 6PM")

//            scr_lbl_assigned_area
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewAssignedArea =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatAssignedArea =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutAssignedArea = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutAssignedArea?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_assigned_area),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutAssignedArea,
                appCompatAutoCompleteTextView = mAutoComTextViewAssignedArea
            )
            mOtherDetails.addView(mLayoutCompatAssignedArea)

//            scr_lbl_violation_descriptions
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewViolationDescription =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatViolationDescription =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutViolationDescription =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutViolationDescription?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_violation_descriptions),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutViolationDescription,
                appCompatAutoCompleteTextView = mAutoComTextViewViolationDescription
            )
            mOtherDetails.addView(mLayoutCompatViolationDescription)

// comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT, ignoreCase = true)) {
//            ioScope.launch {
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            // scr_lbl_zone
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )


            mCameraImagesLayout.visibility = View.GONE
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 2
            mLocationDetails.addView(mLayoutCompatPBCZone)

            //Meter Number
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            // scr_lbl_zone
            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_station),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )

            mLocationDetails.addView(mLayoutCompatMeterNo)

            // scr_lbl_vin_number
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewLicensePlate =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLicensePlate =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLicensePlate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLicensePlate?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_license_plate),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLicensePlate,
                appCompatAutoCompleteTextView = mAutoComTextViewLicensePlate
            )

            mOtherDetails.addView(mLayoutCompatLicensePlate)

            mInputLayoutLicensePlate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLicensePlate?.endIconMode = END_ICON_NONE
            mAutoComTextViewLicensePlate?.setAllCaps(true)
            mAutoComTextViewLicensePlate?.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)

            // scr_lbl_vin_number
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewVin =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVin = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutVin = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutVin?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vin_number),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutVin,
                appCompatAutoCompleteTextView = mAutoComTextViewVin
            )
            mOtherDetails.addView(mLayoutCompatVin)

            mInputLayoutWarningsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutWarningsIssued?.endIconMode = END_ICON_NONE
            val maxL = 17;
            mAutoComTextViewVin?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxL))

            // scr_lbl_State
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewState =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatState = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutState = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutState?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_state),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutState,
                appCompatAutoCompleteTextView = mAutoComTextViewState
            )
            mOtherDetails.addView(mLayoutCompatState)

//            mInputLayoutVehicleMark = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutVehicleMark.endIconMode = END_ICON_NONE

            // scr_lbl_make
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMake =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMake = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMake = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMake?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_make),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMake,
                appCompatAutoCompleteTextView = mAutoComTextViewMake
            )
            mOtherDetails.addView(mLayoutCompatMake)

            // scr_lbl_mode
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewModel =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatModel = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutModel = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutModel?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_model),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutModel,
                appCompatAutoCompleteTextView = mAutoComTextViewModel
            )
            mOtherDetails.addView(mLayoutCompatModel)

            // scr_lbl_color
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewColor =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatColor = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutColor = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutColor?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_color),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutColor,
                appCompatAutoCompleteTextView = mAutoComTextViewColor
            )
            mOtherDetails.addView(mLayoutCompatColor)

            // scr_lbl_time_marked
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewTimeMark =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTimeMark =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutTimeMark = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutTimeMark?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_time_marked),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutTimeMark,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark
            )
            mOtherDetails.addView(mLayoutCompatTimeMark)
            mAutoComTextViewTimeMark?.setHint("dd/mm/yyyy HH:mm")

            // scr_lbl_citation_issued
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewCitationsIssued =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCitationsIssued =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCitationsIssued = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCitationsIssued?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_citation_issued),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )
            mOtherDetails.addView(mLayoutCompatCitationsIssued)
            mAutoComTextViewCitationsIssued?.setHint("dd/mm/yyyy HH:mm")

//            mInputLayoutCitationsIssued =
//                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutCitationsIssued.endIconMode = END_ICON_NONE


//            scr_lbl_schedule_tow_date_time
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewDate =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDate = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutDate?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_schedule_tow_date_time),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDate,
                appCompatAutoCompleteTextView = mAutoComTextViewDate
            )
            mOtherDetails.addView(mLayoutCompatDate)
            mAutoComTextViewDate?.setHint("dd/mm/yyyy HH:mm")

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE
//            mAutoComTextViewLine.setHint("Enter")

        } else if (reportType.equals(REPORT_TYPE_SIGN_OFF_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 2
            mCameraImagesLayout.visibility = View.GONE
            tvCameraTitle.setText("Signature Section")

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            // scr_lbl_vehicle_stored_at
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewVehicleStoredAt =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVehicleStoredAt =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutVehicleStoredAt = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutVehicleStoredAt?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vehicle_stored_at),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutVehicleStoredAt,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleStoredAt
            )
            mLocationDetails.addView(mLayoutCompatVehicleStoredAt)

//            mInputLayoutVehicleStoredAt = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutVehicleStoredAt.endIconMode = END_ICON_NONE

            // scr_lbl_tow_file_number
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTowFileNumber =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTowFileNumber =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutTowFileNumber = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutTowFileNumber?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_tow_file_number),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutTowFileNumber,
                appCompatAutoCompleteTextView = mAutoComTextViewTowFileNumber
            )
            mOtherDetails.addView(mLayoutCompatTowFileNumber)

            mInputLayoutTowFileNumber?.endIconMode = END_ICON_NONE

//            scr_lbl_assigned_area
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVehicleRelocatedToDifferentStallWithinTheLot =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vehicle_relocated_to_different_stall_within_the_lot),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot
            )
            mOtherDetails.addView(mLayoutCompatVehicleRelocatedToDifferentStallWithinTheLot)

            // scr_lbl_line
//            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
//            mAppComTextViewLine = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewLine = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
//            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
//            mAppComTextViewLine.setText(R.string.scr_lbl_line)
//            mOtherDetails.addView(mLayoutCompatLine)
//
//            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutLine.endIconMode = END_ICON_NONE

        } else if (reportType.equals(REPORT_TYPE_TOW_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 8
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

            //Meter Number
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            // scr_lbl_zone
            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_station),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )

            mLocationDetails.addView(mLayoutCompatMeterNo)

            // scr_lbl_license_plate
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewLicensePlate =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLicensePlate =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLicensePlate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLicensePlate?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_license_plate),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLicensePlate,
                appCompatAutoCompleteTextView = mAutoComTextViewLicensePlate
            )
            mOtherDetails.addView(mLayoutCompatLicensePlate)

            mInputLayoutVehicleStoredAt = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutVehicleStoredAt?.endIconMode = END_ICON_NONE
            mAutoComTextViewLicensePlate?.setAllCaps(true)
            mAutoComTextViewLicensePlate?.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)


            // scr_lbl_state
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewState =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatState = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutState = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutState?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_state),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutState,
                appCompatAutoCompleteTextView = mAutoComTextViewState
            )
            mOtherDetails.addView(mLayoutCompatState)

//            mInputLayoutTowFileNumber = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutTowFileNumber.endIconMode = END_ICON_NONE

//            scr_lbl_vin
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewVin =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVin = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutVin = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutVin?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vin),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutVin,
                appCompatAutoCompleteTextView = mAutoComTextViewVin
            )
            mOtherDetails.addView(mLayoutCompatVin)
            val maxL = 17;
            mAutoComTextViewVin?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxL))

//            scr_lbl_make
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMake =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMake = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMake = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMake?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_make),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMake,
                appCompatAutoCompleteTextView = mAutoComTextViewMake
            )
            mOtherDetails.addView(mLayoutCompatMake)

//            scr_lbl_model
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewModel =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatModel = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutModel = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutModel?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_model),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutModel,
                appCompatAutoCompleteTextView = mAutoComTextViewModel
            )
            mOtherDetails.addView(mLayoutCompatModel)

//            scr_lbl_color
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewColor =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatColor = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutColor = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutColor?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_color),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutColor,
                appCompatAutoCompleteTextView = mAutoComTextViewColor
            )
            mOtherDetails.addView(mLayoutCompatColor)

//            scr_lbl_reason_for_tow
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewReasonForTow =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatReasonForTow =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutReasonForTow = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutReasonForTow?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_reason_for_tow),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutReasonForTow,
                appCompatAutoCompleteTextView = mAutoComTextViewReasonForTow
            )
            mOtherDetails.addView(mLayoutCompatReasonForTow)

//            scr_lbl_notice_to_tow_posted_on
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewDate =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDate = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutDate?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_notice_to_tow_posted_on),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDate,
                appCompatAutoCompleteTextView = mAutoComTextViewDate
            )
            mOtherDetails.addView(mLayoutCompatDate)

            val mInputLayoutDate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutDate?.endIconMode = END_ICON_NONE
            mAutoComTextViewDate?.setHint("dd/mm/yyyy")


//            scr_lbl_trailer_attached_to_vehicle
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewOverHeating =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatOverHeating =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutOverHeating = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutOverHeating?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_trailer_attached_to_vehicle),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutOverHeating,
                appCompatAutoCompleteTextView = mAutoComTextViewOverHeating
            )
            mOtherDetails.addView(mLayoutCompatOverHeating)

//            scr_lbl_officer_phone_number
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewOfficerPhoneNumber =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatOfficerPhoneNumber =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutOfficerPhoneNumber =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutOfficerPhoneNumber?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_officer_phone_number),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutOfficerPhoneNumber,
                appCompatAutoCompleteTextView = mAutoComTextViewOfficerPhoneNumber
            )
            mOtherDetails.addView(mLayoutCompatOfficerPhoneNumber)

            val filterArray = arrayOfNulls<InputFilter>(1)
            filterArray[0] = LengthFilter(10)
            mAutoComTextViewOfficerPhoneNumber?.setFilters(filterArray)

            mInputLayoutOfficerPhoneNumber?.endIconMode = END_ICON_NONE

//            scr_lbl_vehicle_condition
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewVehicleCondition =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVehicleCondition =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutVehicleCondition = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutVehicleCondition?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vehicle_condition),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutVehicleCondition,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleCondition
            )
            mOtherDetails.addView(mLayoutCompatVehicleCondition)

            mInputLayoutVehicleCondition?.endIconMode = END_ICON_NONE

//            scr_lbl_request_to
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSpecialEnforcementRequest =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSpecialEnforcementRequest =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutSpecialEnforcementRequest =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutSpecialEnforcementRequest?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_request_to),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutSpecialEnforcementRequest,
                appCompatAutoCompleteTextView = mAutoComTextViewSpecialEnforcementRequest
            )

            mOtherDetails.addView(mLayoutCompatSpecialEnforcementRequest)

//            scr_lbl_garage_clearance
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewGarageClearance =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatGarageClearance =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutGarageClearance = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutGarageClearance?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_garage_clearance),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutGarageClearance,
                appCompatAutoCompleteTextView = mAutoComTextViewGarageClearance
            )
            mOtherDetails.addView(mLayoutCompatGarageClearance)

            mInputLayoutGarageClearance?.endIconMode = END_ICON_NONE

//            scr_lbl_file_number
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewFileNumber =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatFileNumber =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutFileNumber = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutFileNumber?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_file_number),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutFileNumber,
                appCompatAutoCompleteTextView = mAutoComTextViewFileNumber
            )
            mOtherDetails.addView(mLayoutCompatFileNumber)

            mInputLayoutFileNumber?.endIconMode = END_ICON_NONE

            // scr_lbl_ro_name_address
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewRo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatRo = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutRo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutRo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_ro_name_address),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutRo,
                appCompatAutoCompleteTextView = mAutoComTextViewRo
            )
            mOtherDetails.addView(mLayoutCompatRo)

            mInputLayoutRo?.endIconMode = END_ICON_NONE

            // scr_lbl_vehicle_within_the_lot
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewVehicleWithInLot =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatVehicleWithInLot =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutVehicleWithInLot = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutVehicleWithInLot?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vehicle_within_the_lot),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutVehicleWithInLot,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleWithInLot
            )
            mOtherDetails.addView(mLayoutCompatVehicleWithInLot)

            mInputLayoutVehicleWithInLot?.endIconMode = END_ICON_NONE


            // scr_lbl_comment_driver_side
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDriverSideComment =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDriverSideComment =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutDriverSideComment =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutDriverSideComment?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment_driver_side),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutDriverSideComment,
                appCompatAutoCompleteTextView = mAutoComTextViewDriverSideComment
            )
            mOtherDetails.addView(mLayoutCompatDriverSideComment)

            mInputLayoutDriverSideComment?.endIconMode = END_ICON_NONE

            // scr_lbl_comment_passenger_side
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewPassengerSideComment =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPassengerSideComment =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutPassengerSideComment =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutPassengerSideComment?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment_passenger_side),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutPassengerSideComment,
                appCompatAutoCompleteTextView = mAutoComTextViewPassengerSideComment
            )

            mOtherDetails.addView(mLayoutCompatPassengerSideComment)

            mInputLayoutPassengerSideComment?.endIconMode = END_ICON_NONE

            // scr_lbl_comment_front_side
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewFrontSideComment =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatFrontSideComment =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutFrontSideComment = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutFrontSideComment?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment_front_side),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutFrontSideComment,
                appCompatAutoCompleteTextView = mAutoComTextViewFrontSideComment
            )
            mOtherDetails.addView(mLayoutCompatFrontSideComment)

            mInputLayoutFrontSideComment?.endIconMode = END_ICON_NONE

            // scr_lbl_comment_rear_side
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewRearSideComment =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatRearSideComment =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutRearSideComment = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutRearSideComment?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment_rear_side),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutRearSideComment,
                appCompatAutoCompleteTextView = mAutoComTextViewRearSideComment
            )
            mOtherDetails.addView(mLayoutCompatRearSideComment)

            mInputLayoutRearSideComment?.endIconMode = END_ICON_NONE

            // scr_lbl_comment_rear_side
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTrailerComment =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTrailerComment =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutTrailerComment = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutTrailerComment?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment_trailer),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutTrailerComment,
                appCompatAutoCompleteTextView = mAutoComTextViewTrailerComment
            )
            mOtherDetails.addView(mLayoutCompatTrailerComment)

            mInputLayoutTrailerComment?.endIconMode = END_ICON_NONE


//            scr_lbl_visible_interior_item
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewInteriorCleanliness =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatInteriorCleanliness =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutInteriorCleanliness =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutInteriorCleanliness?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_visible_interior_item),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutInteriorCleanliness,
                appCompatAutoCompleteTextView = mAutoComTextViewInteriorCleanliness
            )

            mOtherDetails.addView(mLayoutCompatInteriorCleanliness)

            mInputLayoutInteriorCleanliness?.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE

            // scr_lbl_towing_officer
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewTowingOfficer =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatTowingOfficer =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutTowingOfficer = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutTowingOfficer?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_towing_officer),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutTowingOfficer,
                appCompatAutoCompleteTextView = mAutoComTextViewTowingOfficer
            )
            mOtherDetails.addView(mLayoutCompatTowingOfficer)

            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_NFL_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 3
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_station),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mLocationDetails.addView(mLayoutCompatMeterNo)


//            scr_lbl_car_count
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewCarCount =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCarCount =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCarCount = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCarCount?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_car_counted),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCarCount,
                appCompatAutoCompleteTextView = mAutoComTextViewCarCount
            )
            mOtherDetails.addView(mLayoutCompatCarCount)

            mInputLayoutCarCount?.endIconMode = END_ICON_NONE
            mAutoComTextViewCarCount?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

//            scr_lbl_empty_space
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewEmptySpace =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatEmptySpace =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutEmptySpace = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutEmptySpace?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_empty_space),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutEmptySpace,
                appCompatAutoCompleteTextView = mAutoComTextViewEmptySpace
            )

            mOtherDetails.addView(mLayoutCompatEmptySpace)

            mInputLayoutEmptySpace?.endIconMode = END_ICON_NONE
            mAutoComTextViewEmptySpace?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

//            scr_lbl_number_of_violating_vehicles
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewNumberOfViolatingVehicles =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatNumberOfViolatingVehicles =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutNumberOfViolatingVehicles =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutNumberOfViolatingVehicles?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_number_of_violating_vehicles),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutNumberOfViolatingVehicles,
                appCompatAutoCompleteTextView = mAutoComTextViewNumberOfViolatingVehicles
            )

            mOtherDetails.addView(mLayoutCompatNumberOfViolatingVehicles)

            mInputLayoutNumberOfViolatingVehicles?.endIconMode = END_ICON_NONE
            mAutoComTextViewNumberOfViolatingVehicles?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

            mAutoComTextViewNumberOfViolatingVehicles?.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus && mAutoComTextViewNumberOfViolatingVehicles?.text?.isNotEmpty()
                            .nullSafety()
                    ) {
                        val carCount: Double =
                            (mAutoComTextViewCarCount?.text.toString()).toDouble()
                        val violationCount: Double =
                            (mAutoComTextViewNumberOfViolatingVehicles?.text.toString()).toDouble()
                        val result: Double = (violationCount / (carCount * 100)).toDouble()
                        try {
                            mAutoComTextViewViolationRate?.setText(
                                " " + String.format(
                                    "%.2f", result
                                )
                            )
//                               mAutoComTextViewViolationRate.isFocusable = false
//                               mAppComTextViewComments.isFocusable = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

//            scr_lbl_violation_rate
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewViolationRate =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatViolationRate =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutViolationRate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutViolationRate?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_number_of_violating_rate),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutViolationRate,
                appCompatAutoCompleteTextView = mAutoComTextViewViolationRate
            )

            mOtherDetails.addView(mLayoutCompatViolationRate)

            mInputLayoutViolationRate?.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.endIconMode = END_ICON_NONE

            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_HARD_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 3
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
//
//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

//            scr_lbl_car_count
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewCarCount =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCarCount =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCarCount = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCarCount?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_car_counted),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCarCount,
                appCompatAutoCompleteTextView = mAutoComTextViewCarCount
            )
            mOtherDetails.addView(mLayoutCompatCarCount)
            mAutoComTextViewCarCount?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

            mInputLayoutCarCount?.endIconMode = END_ICON_NONE

//            scr_lbl_empty_space
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewEmptySpace =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatEmptySpace =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutEmptySpace = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutEmptySpace?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_empty_space),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutEmptySpace,
                appCompatAutoCompleteTextView = mAutoComTextViewEmptySpace
            )
            mOtherDetails.addView(mLayoutCompatEmptySpace)
            mAutoComTextViewEmptySpace?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

            mInputLayoutEmptySpace?.endIconMode = END_ICON_NONE

//            scr_lbl_number_of_violating_vehicles
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewNumberOfViolatingVehicles =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatNumberOfViolatingVehicles =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutNumberOfViolatingVehicles =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutNumberOfViolatingVehicles?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_number_of_violating_vehicles),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutNumberOfViolatingVehicles,
                appCompatAutoCompleteTextView = mAutoComTextViewNumberOfViolatingVehicles
            )

            mOtherDetails.addView(mLayoutCompatNumberOfViolatingVehicles)
            mAutoComTextViewNumberOfViolatingVehicles?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

            mInputLayoutNumberOfViolatingVehicles?.endIconMode = END_ICON_NONE

            mAutoComTextViewNumberOfViolatingVehicles?.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus && mAutoComTextViewNumberOfViolatingVehicles?.text?.isNotEmpty()
                            .nullSafety()
                    ) {
                        val carCount: Double =
                            (mAutoComTextViewCarCount?.text.toString()).toDouble()
                        val violationCount: Double =
                            (mAutoComTextViewNumberOfViolatingVehicles?.text.toString()).toDouble()
                        val result: Double = (violationCount / (carCount * 100)).toDouble()
                        try {
                            mAutoComTextViewViolationRate?.setText(
                                " " + String.format(
                                    "%.2f", result
                                )
                            )
//                            mAutoComTextViewViolationRate.isFocusable = false
//                            mAppComTextViewComments.isFocusable = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

//            scr_lbl_violation_rate
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewViolationRate =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatViolationRate =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutViolationRate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutViolationRate?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_number_of_violating_rate),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutViolationRate,
                appCompatAutoCompleteTextView = mAutoComTextViewViolationRate
            )
            mOtherDetails.addView(mLayoutCompatViolationRate)

            mInputLayoutViolationRate?.endIconMode = END_ICON_NONE

//            scr_lbl_event_name
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField1 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField1 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField1 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField1?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_event_name),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mOtherDetails.addView(mLayoutCompatField1)

            mInputLayoutField1?.endIconMode = END_ICON_NONE

            // scr_lbl_line
//            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
//            mAppComTextViewLine = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewLine = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
//            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
//            mAppComTextViewLine.setText(R.string.scr_lbl_line)
//            mOtherDetails.addView(mLayoutCompatLine)
//
//            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutLine.endIconMode = END_ICON_NONE

            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_AFTER_SEVEN_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 8
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

//            scr_lbl_vendor_count
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewCarCount =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCarCount =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCarCount = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCarCount?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vendor_counted),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCarCount,
                appCompatAutoCompleteTextView = mAutoComTextViewCarCount
            )
            mOtherDetails.addView(mLayoutCompatCarCount)

            mInputLayoutCarCount?.endIconMode = END_ICON_NONE
            mAutoComTextViewCarCount?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

//            scr_lbl_vendor_locations
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLotArea =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLotArea =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLotArea = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLotArea?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_vendor_locations),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLotArea,
                appCompatAutoCompleteTextView = mAutoComTextViewLotArea
            )
            mOtherDetails.addView(mLayoutCompatLotArea)

            mInputLayoutLotArea?.endIconMode = END_ICON_NONE

//            scr_lbl_number_of_security_observation
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSecurityObservation =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSecurityObservation =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutSecurityObservation =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutSecurityObservation?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_number_of_security_observation),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutSecurityObservation,
                appCompatAutoCompleteTextView = mAutoComTextViewSecurityObservation
            )

            mOtherDetails.addView(mLayoutCompatSecurityObservation)

            mInputLayoutSecurityObservation?.endIconMode = END_ICON_NONE

//            // scr_lbl_line
//            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
//            mAppComTextViewLine = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewLine = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
//            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
//            mAppComTextViewLine.setText(R.string.scr_lbl_line)
//            mOtherDetails.addView(mLayoutCompatLine)
//
//            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutLine.endIconMode = END_ICON_NONE

            // comment
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_PAY_STATION_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 1
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_station),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mLocationDetails.addView(mLayoutCompatMeterNo)

//            scr_lbl_machine_number1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField1 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField1 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField1 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField1?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_machine_number1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mOtherDetails.addView(mLayoutCompatField1)


            mInputLayoutField1?.endIconMode = END_ICON_NONE
            mAutoComTextViewField1?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

//            scr_lbl_machine_number2
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField2 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField2 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField2 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField2?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_machine_number2),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mOtherDetails.addView(mLayoutCompatField2)

            mInputLayoutField2?.endIconMode = END_ICON_NONE
            mAutoComTextViewField2?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

//            scr_lbl_machine3
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField3 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField3 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField3 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField3?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_machine_number3),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField3,
                appCompatAutoCompleteTextView = mAutoComTextViewField3
            )
            mOtherDetails.addView(mLayoutCompatField3)

            mInputLayoutField3?.endIconMode = END_ICON_NONE
            mAutoComTextViewField3?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

//            scr_lbl_description1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField4 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField4 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField4 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField4?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField4,
                appCompatAutoCompleteTextView = mAutoComTextViewField4
            )
            mOtherDetails.addView(mLayoutCompatField4)

            mInputLayoutField4?.endIconMode = END_ICON_NONE

//            scr_lbl_description2
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField5 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField5 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField5 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField5?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description2),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField5,
                appCompatAutoCompleteTextView = mAutoComTextViewField5
            )
            mOtherDetails.addView(mLayoutCompatField5)

            mInputLayoutField5?.endIconMode = END_ICON_NONE

//            scr_lbl_description3
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField6 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField6 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField6 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField6?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description3),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField6,
                appCompatAutoCompleteTextView = mAutoComTextViewField6
            )
            mOtherDetails.addView(mLayoutCompatField6)

            mInputLayoutField6?.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE

            // comment1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)


            // comment2
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComment2 =
                rootView?.findViewById<TextInputEditText>(R.id.autoCompleteTextView)
            mLayoutCompatComment2 =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComment2 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComment2?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment2),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComment2,
                textInputEditText = mAutoComTextViewComment2
            )
            mOtherDetails.addView(mLayoutCompatComment2)

            // comment3
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComment3 =
                rootView?.findViewById<TextInputEditText>(R.id.autoCompleteTextView)
            mLayoutCompatComment3 =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComment3 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComment3?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment3),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComment3,
                textInputEditText = mAutoComTextViewComment3
            )
            mOtherDetails.addView(mLayoutCompatComment3)

        } else if (reportType.equals(REPORT_TYPE_PAY_SIGNAGE_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 1
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)


//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_station),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mLocationDetails.addView(mLayoutCompatMeterNo)

//            scr_lbl_description1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField1 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField1 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField1 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField1?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mOtherDetails.addView(mLayoutCompatField1)

            mInputLayoutField1?.endIconMode = END_ICON_NONE

//            scr_lbl_description2
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField2 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField2 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField2 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField2?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description2),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mOtherDetails.addView(mLayoutCompatField2)

            mInputLayoutField2?.endIconMode = END_ICON_NONE

//            scr_lbl_description3
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField3 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField3 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField3 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField3?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description3),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField3,
                appCompatAutoCompleteTextView = mAutoComTextViewField3
            )
            mOtherDetails.addView(mLayoutCompatField3)

            mInputLayoutField3?.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE

            // comment1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_PAY_HOMELESS_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 4
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_station),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mLocationDetails.addView(mLayoutCompatMeterNo)

//            scr_lbl_description1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField1 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField1 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField1 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField1?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mOtherDetails.addView(mLayoutCompatField1)

            mInputLayoutField1?.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE

            // comment1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_PAY_LOT_INSPECTION_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE
            imageValidationCount = 0
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

            // scr_lbl_all_signs_reported
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewInternetConnectivity =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatInternetConnectivity =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutInternetConnectivity =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutInternetConnectivity?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_all_signs_reported),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutInternetConnectivity,
                appCompatAutoCompleteTextView = mAutoComTextViewInternetConnectivity
            )
            mOtherDetails.addView(mLayoutCompatInternetConnectivity)

            // all_paystations_reported
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewMeterLabelVisible =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterLabelVisible =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterLableVisible =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterLableVisible?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_all_paystations_reported),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterLableVisible,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterLabelVisible
            )
            mOtherDetails.addView(mLayoutCompatMeterLabelVisible)

            // any_homeless_reported
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewDigitalDisplayVisible =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDigitalDisplayVisible =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDigitalDisplayVisible =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutDigitalDisplayVisible?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_any_homeless_reported),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDigitalDisplayVisible,
                appCompatAutoCompleteTextView = mAutoComTextViewDigitalDisplayVisible
            )
            mOtherDetails.addView(mLayoutCompatDigitalDisplayVisible)

            // meter_label_visible
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewCoinJam =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCoinJam =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutCoinJam = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutCoinJam?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_any_safety_issues_reported),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutCoinJam,
                appCompatAutoCompleteTextView = mAutoComTextViewCoinJam
            )
            mOtherDetails.addView(mLayoutCompatCoinJam)

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE

//            // comment1
//            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
//            mAppComTextViewComments = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewComments = rootView?.findViewById<TextInputEditText>(R.id.autoCompleteTextView)
//            mLayoutCompatComments = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
//            mAppComTextViewComments.post{mAppComTextViewComments.setText(R.string.scr_lbl_comment1)}
//            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_PAY_LOT_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE
            imageValidationCount = 0
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_station),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mLocationDetails.addView(mLayoutCompatMeterNo)

//            scr_lbl_car_count
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewCarCount =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatCarCount =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutCarCount = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutCarCount?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_car_counted),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutCarCount,
                appCompatAutoCompleteTextView = mAutoComTextViewCarCount
            )
            mOtherDetails.addView(mLayoutCompatCarCount)
            mAutoComTextViewCarCount?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

            mInputLayoutCarCount?.endIconMode = END_ICON_NONE

//            scr_lbl_car_space
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField1 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField1 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField1 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField1?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_car_space),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mOtherDetails.addView(mLayoutCompatField1)
            mAutoComTextViewField1?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

            mInputLayoutField1?.endIconMode = END_ICON_NONE

//            scr_lbl_empty_space
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewEmptySpace =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatEmptySpace =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutEmptySpace = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutEmptySpace?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_empty_space),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutEmptySpace,
                appCompatAutoCompleteTextView = mAutoComTextViewEmptySpace
            )
            mOtherDetails.addView(mLayoutCompatEmptySpace)
            mAutoComTextViewEmptySpace?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

            mInputLayoutEmptySpace?.endIconMode = END_ICON_NONE

//            scr_lbl_number_of_violating_vehicles
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewNumberOfViolatingVehicles =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatNumberOfViolatingVehicles =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutNumberOfViolatingVehicles =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutNumberOfViolatingVehicles?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_number_of_violating_vehicles),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutNumberOfViolatingVehicles,
                appCompatAutoCompleteTextView = mAutoComTextViewNumberOfViolatingVehicles
            )

            mOtherDetails.addView(mLayoutCompatNumberOfViolatingVehicles)
            mAutoComTextViewNumberOfViolatingVehicles?.setInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            )

            mInputLayoutNumberOfViolatingVehicles?.endIconMode = END_ICON_NONE

            mAutoComTextViewNumberOfViolatingVehicles?.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus && mAutoComTextViewNumberOfViolatingVehicles?.text?.isNotEmpty()
                            .nullSafety()
                    ) {
                        val carCount: Double =
                            (mAutoComTextViewCarCount?.text.toString()).toDouble()
                        val violationCount: Double =
                            (mAutoComTextViewNumberOfViolatingVehicles?.text.toString()).toDouble()
                        val result: Double = (violationCount / (carCount * 100)).toDouble()
                        try {
                            mAutoComTextViewViolationRate?.setText(
                                " " + String.format(
                                    "%.2f", result
                                )
                            )
//                            mAutoComTextViewViolationRate.isFocusable = false
//                            mAppComTextViewComments.isFocusable = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

//            scr_lbl_violation_rate
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewViolationRate =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatViolationRate =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutViolationRate = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutViolationRate?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_number_of_violating_rate),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutViolationRate,
                appCompatAutoCompleteTextView = mAutoComTextViewViolationRate
            )
            mOtherDetails.addView(mLayoutCompatViolationRate)

            mInputLayoutViolationRate?.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )

            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE

//            // comment1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(REPORT_TYPE_WORK_ORDER_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE
            imageValidationCount = 1
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//          scr_lbl_block
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewBlock =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatBlock = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutBlock = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutBlock?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_block),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )
            mLocationDetails.addView(mLayoutCompatBlock)

//          scr_lbl_street
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewStreet =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatStreet = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutStreet = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutStreet?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_street),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )
            mLocationDetails.addView(mLayoutCompatStreet)

//          scr_lbl_side
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewDirection =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatDirection =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutDirection = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutDirection?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_side),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )
            mLocationDetails.addView(mLayoutCompatDirection)

//            scr_lbl_sign
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField1 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField1 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField1 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField1?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_sign),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mOtherDetails.addView(mLayoutCompatField1)

            mInputLayoutField1?.endIconMode = END_ICON_NONE

//            scr_lbl_actions_required
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField2 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField2 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField2 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField2?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_actions_required),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mOtherDetails.addView(mLayoutCompatField2)

            mInputLayoutField2?.endIconMode = END_ICON_NONE

//            // scr_lbl_line
//            rootView = View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
//            mAppComTextViewLine = rootView?.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewLine = rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
//            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
//            mAppComTextViewLine.setText(R.string.scr_lbl_line)
//            mOtherDetails.addView(mLayoutCompatLine)
//
//            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
//            mInputLayoutLine.endIconMode = END_ICON_NONE

//            // comment1
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_text_box, null)
            mAutoComTextViewComments =
                rootView?.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatComments =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutComments = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutComments?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comment1),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutComments,
                appCompatAutoCompleteTextView = mAutoComTextViewComments
            )
            mOtherDetails.addView(mLayoutCompatComments)

        } else if (reportType.equals(
                REPORT_TYPE_PAY_SAFETY_REPORT, ignoreCase = true
            )
        ) {
            mCameraImagesLayout.visibility = View.GONE
            imageValidationCount = 3
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mLocationDetails.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewMeterNo =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatMeterNo =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutMeterNo = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutMeterNo?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_station),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mLocationDetails.addView(mLayoutCompatMeterNo)

//            scr_lbl_safety_issue
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewSafetyIssue =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatSafetyIssue =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutSafetyIssue = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutSafetyIssue?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_safety_issue),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutSafetyIssue,
                appCompatAutoCompleteTextView = mAutoComTextViewSafetyIssue
            )
            mOtherDetails.addView(mLayoutCompatSafetyIssue)

//            scr_lbl_description_level_quadrant
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField2 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField2 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField2 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField2?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description_level_quadrant),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mOtherDetails.addView(mLayoutCompatField2)

            mInputLayoutField2?.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mOtherDetails.addView(mLayoutCompatLine)

            mInputLayoutLine?.endIconMode = END_ICON_NONE


        } else if (reportType.equals(REPORT_TYPE_PAY_TRASH_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.GONE
            imageValidationCount = 1
            mCameraImagesLayout.visibility = View.GONE

            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

//            scr_lbl_Zone
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewPBCZone =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatPBCZone =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutPBCZone = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutPBCZone?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_zone),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )

            mLocationDetails.addView(mLayoutCompatPBCZone)

//            scr_lbl_safety_issue
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)

            mAutoComTextViewRequiredServices =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatRequiredServices =
                rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            textInputLayoutRequiredServices =
                rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            textInputLayoutRequiredServices?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_safety_issue),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = textInputLayoutRequiredServices,
                appCompatAutoCompleteTextView = mAutoComTextViewRequiredServices
            )

            mOtherDetails.addView(mLayoutCompatRequiredServices)


//            scr_lbl_description_level_quadrant
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewField2 =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatField2 = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutField2 = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutField2?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_description_level_quadrant),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )

            mInputLayoutField2?.endIconMode = END_ICON_NONE

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )

            mOtherDetails.addView(mLayoutCompatField2)


            // scr_lbl_line
            rootView =
                View.inflate(requireContext(), R.layout.construct_layout_content_dropdown, null)
            mAutoComTextViewLine =
                rootView?.findViewById<AppCompatAutoCompleteTextView>(R.id.autoCompleteTextView)
            mLayoutCompatLine = rootView?.findViewById<LinearLayoutCompat>(R.id.llParentContainer)
            mInputLayoutLine = rootView?.findViewById<TextInputLayout>(R.id.inputLayout)
            mInputLayoutLine?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_line),
                placeholder = getString(R.string.hint_select_or_type)
            )

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mInputLayoutLine?.endIconMode = END_ICON_NONE

            setCrossClearButton(
                context = requireContext(),
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )

            mOtherDetails.addView(mLayoutCompatLine)
        }

        DialogUtil.hideLoader()
    }

    private fun cameraIntent() {
        viewLifecycleOwner.lifecycleScope.launch {
            val mySession = session ?: return@launch
            mySession.takePicture(
                TEMP_IMAGE_FILE_NAME, CameraHelper.SaveLocation.APP_EXTERNAL_FILES
            ) { bmp ->
                bmp?.let {
                    saveImageMM(bmp)
                }
            }
        }

    }

    //TODO will add comments later
    private fun saveImageMM(finalBitmap: Bitmap?) {
        if (finalBitmap == null) {
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            return
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            return
        }

        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.ALLREPORTIMAGES
        )

        if (!myDir.exists()) myDir.mkdirs()

        val timeStamp = SDF_IMAGE_TIMESTAMP.format(Date())
        val fileName = "Image_${timeStamp}_report.jpg"
        val file = File(myDir, fileName)
        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)

            val compressQuality = 40
            val isSuccess = finalBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)
            out.flush()
            out.close()

            if (!isSuccess || file.length() == 0L) {
                logD("SaveImageMM", "Compression failed or file is empty.")
                file.delete()
                return
            }

            // Delete temp image if exists
            val oldFile = File(myDir, "IMG_temp.jpg")
            if (oldFile.exists()) oldFile.delete()

            // Save path to DB
            val id = SDF_IMAGE_ID_TIMESTAMP.format(Date())
            val pathDb = file.path
            val mImage = CitationImagesModel()
            mImage.status = 1
            mImage.citationImage = pathDb
            mImage.id = id.toInt()

            bannerList?.set(cameraCount, mImage)
            showImagesBanner()
            finalBitmap.recycle()
            cameraCount++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDefaultImages() {
        if (reportType.equals(REPORT_TYPE_CURB_REPORT, ignoreCase = true)) {
//            Front Photo, Back Photo, Passenger Side Photo, Driver Side Photo.
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 1
            minimumImageRequired = 1
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Curb Photo"
            mImage.id = 101
            bannerList?.add(mImage)
        } else if (reportType.equals(REPORT_TYPE_SIGN_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 1
            minimumImageRequired = 1
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Add Photo"
            mImage.id = 101
            bannerList?.add(mImage)
        } else if (reportType.equals(REPORT_TYPE_VEHICLE_INSPECTION_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 4
            minimumImageRequired = 4
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Front Photo"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "Back Photo"
            mImage2.id = 102
            bannerList?.add(mImage2)

            val mImage3 = CitationImagesModel()
            mImage3.status = 0
            mImage3.citationImage = "Passenger Side Photo"
            mImage3.id = 103
            bannerList?.add(mImage3)

            val mImage4 = CitationImagesModel()
            mImage4.status = 0
            mImage4.citationImage = "Driver Side Photo"
            mImage4.id = 104
            bannerList?.add(mImage4)

            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                )
            ) {
                imageValidationCount = 7
                minimumImageRequired = 7
                val mImage5 = CitationImagesModel()
                mImage5.status = 0
                mImage5.citationImage = "Photo"
                mImage5.id = 105
                bannerList?.add(mImage5)

                val mImage6 = CitationImagesModel()
                mImage6.status = 0
                mImage6.citationImage = "Photo"
                mImage6.id = 106
                bannerList?.add(mImage6)

                val mImage7 = CitationImagesModel()
                mImage7.status = 0
                mImage7.citationImage = "Photo"
                mImage7.id = 107
                bannerList?.add(mImage7)
            }

        } else if (reportType.equals(REPORT_TYPE_SIGN_OFF_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            appCompatImageViewCameraIcon.visibility = View.GONE
            imageValidationCount = 2
            minimumImageRequired = 2
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "US Tow Driver"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "SP Plus Officer"
            mImage2.id = 102
            bannerList?.add(mImage2)

        } else if (reportType.equals(REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT, ignoreCase = true)) {
            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 2
            minimumImageRequired = 2
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Driver side front tire air valve position"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "Photos"
            mImage2.id = 102
            bannerList?.add(mImage2)

        } else if (reportType.equals(REPORT_TYPE_BIKE_INSPECTION_REPORT, ignoreCase = true)) {

            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 4
            minimumImageRequired = 4

            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Front Photo"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "Back Photo"
            mImage2.id = 102
            bannerList?.add(mImage2)

            val mImage3 = CitationImagesModel()
            mImage3.status = 0
//            mImage3.citationImage = "Passenger Side Photo"
            mImage3.citationImage = "Right Side"
            mImage3.id = 103
            bannerList?.add(mImage3)

            val mImage4 = CitationImagesModel()
            mImage4.status = 0
//            mImage4.citationImage = "Driver Side Photo"
            mImage4.citationImage = "Left Side"
            mImage4.id = 104
            bannerList?.add(mImage4)
        } else if (reportType.equals(REPORT_TYPE_TOW_REPORT, ignoreCase = true)) {

            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 8
            minimumImageRequired = 8

            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Plate Photo"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "Curb Markings"
            mImage2.id = 102
            bannerList?.add(mImage2)

            val mImage3 = CitationImagesModel()
            mImage3.status = 0
            mImage3.citationImage = "Driver Side"
            mImage3.id = 103
            bannerList?.add(mImage3)

            val mImage4 = CitationImagesModel()
            mImage4.status = 0
            mImage4.citationImage = "Front"
            mImage4.id = 104
            bannerList?.add(mImage4)

            val mImage5 = CitationImagesModel()
            mImage5.status = 0
            mImage5.citationImage = "Passenger Side"
            mImage5.id = 105
            bannerList?.add(mImage5)

            val mImage6 = CitationImagesModel()
            mImage6.status = 0
            mImage6.citationImage = "Rear"
            mImage6.id = 106
            bannerList?.add(mImage6)

            val mImage7 = CitationImagesModel()
            mImage7.status = 0
            mImage7.citationImage = "Driver side front tire air valve"
            mImage7.id = 107
            bannerList?.add(mImage7)

            val mImage8 = CitationImagesModel()
            mImage8.status = 0
            mImage8.citationImage = "Interior"
            mImage8.id = 108
            bannerList?.add(mImage8)
        } else if (reportType.equals(
                REPORT_TYPE_NFL_REPORT, ignoreCase = true
            ) || reportType.equals(
                REPORT_TYPE_PAY_STATION_REPORT, ignoreCase = true
            ) || reportType.equals(
                REPORT_TYPE_PAY_SIGNAGE_REPORT, ignoreCase = true
            ) || reportType.equals(REPORT_TYPE_HARD_REPORT, ignoreCase = true)
        ) {

            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 3
            minimumImageRequired = 3
            if (reportType.equals(
                    REPORT_TYPE_PAY_SIGNAGE_REPORT, ignoreCase = true
                ) || reportType.equals(REPORT_TYPE_PAY_STATION_REPORT, ignoreCase = true)
            ) {
                imageValidationCount = 3
                minimumImageRequired = 1
            }

            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Photo 1"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "Photo 2"
            mImage2.id = 102
            bannerList?.add(mImage2)

            val mImage3 = CitationImagesModel()
            mImage3.status = 0
            mImage3.citationImage = "Photo 3"
            mImage3.id = 103
            bannerList?.add(mImage3)

        } else if (reportType.equals(REPORT_TYPE_PAY_HOMELESS_REPORT, ignoreCase = true)) {

            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 4
            minimumImageRequired = 1

            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Photo 1"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "Photo 2"
            mImage2.id = 102
            bannerList?.add(mImage2)

            val mImage3 = CitationImagesModel()
            mImage3.status = 0
            mImage3.citationImage = "Photo 3"
            mImage3.id = 103
            bannerList?.add(mImage3)

            val mImage4 = CitationImagesModel()
            mImage4.status = 0
            mImage4.citationImage = "Photo 4"
            mImage4.id = 104
            bannerList?.add(mImage4)

        } else if (reportType.equals(REPORT_TYPE_AFTER_SEVEN_REPORT, ignoreCase = true)) {

            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 8
            minimumImageRequired = 8

            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Photo 1"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "Photo 2"
            mImage2.id = 102
            bannerList?.add(mImage2)

            val mImage3 = CitationImagesModel()
            mImage3.status = 0
            mImage3.citationImage = "Photo 3"
            mImage3.id = 103
            bannerList?.add(mImage3)

            val mImage4 = CitationImagesModel()
            mImage4.status = 0
            mImage4.citationImage = "Photo 4"
            mImage4.id = 104
            bannerList?.add(mImage4)

            val mImage5 = CitationImagesModel()
            mImage5.status = 0
            mImage5.citationImage = "Photo 5"
            mImage5.id = 105
            bannerList?.add(mImage5)

            val mImage6 = CitationImagesModel()
            mImage6.status = 0
            mImage6.citationImage = "Photo 6"
            mImage6.id = 106
            bannerList?.add(mImage6)

            val mImage7 = CitationImagesModel()
            mImage7.status = 0
            mImage7.citationImage = "Photo 7"
            mImage7.id = 107
            bannerList?.add(mImage7)

            val mImage8 = CitationImagesModel()
            mImage8.status = 0
            mImage8.citationImage = "signature"
            mImage8.id = 108
            bannerList?.add(mImage8)

        } else if (reportType.equals(REPORT_TYPE_WORK_ORDER_REPORT, ignoreCase = true)) {

            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 1
            minimumImageRequired = 1

            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Photo 1"
            mImage.id = 101
            bannerList?.add(mImage)

        } else if (reportType.equals(
                REPORT_TYPE_PAY_SAFETY_REPORT, ignoreCase = true
            ) || reportType.equals(REPORT_TYPE_PAY_TRASH_REPORT, ignoreCase = true)
        ) {

            mCameraImagesLayout.visibility = View.VISIBLE
            imageValidationCount = 3
            minimumImageRequired = 1

            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Photo 1"
            mImage.id = 101
            bannerList?.add(mImage)

            val mImage2 = CitationImagesModel()
            mImage2.status = 0
            mImage2.citationImage = "Photo 2"
            mImage2.id = 102
            bannerList?.add(mImage2)

            val mImage3 = CitationImagesModel()
            mImage3.status = 0
            mImage3.citationImage = "Photo 3"
            mImage3.id = 103
            bannerList?.add(mImage3)

        }
        showImagesBanner()
    }

    //show banner images
    private fun showImagesBanner() {
        if (bannerList != null && bannerList?.isNotEmpty().nullSafety()) {
            mBannerAdapter?.setAnimalBannerList(bannerList)
            mViewPagerBanner.adapter = mBannerAdapter
            mViewPagerBanner.currentItem = 0

            ViewPagerUtils.setupViewPagerDots(
                context = requireContext(),
                viewPager = mViewPagerBanner,
                dotsContainer = pagerIndicator,
                totalCount = mBannerAdapter?.count.nullSafety()
            )
        }
    }

    private fun setDropDowns() {
        try {
            if (mAutoComTextViewComments != null) {
//                setDropdownRemark(Singleton.getDataSetList(DATASET_REMARKS_LIST, it) })
            }

            if (mAutoComTextViewMeterNo != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val meterList = withContext(Dispatchers.IO) {
                        mainActivityViewModel.getMeterListFromDataSet()
                    } ?: return@launch

                    setDropdownMeterName(meterList)
                }
            }
            if (mAutoComTextViewStreet != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val streetList = withContext(Dispatchers.IO) {
                        mainActivityViewModel.getStreetListFromDataSet()
                    } ?: return@launch

                    setDropdownStreet(streetList)
                }
            }
            if (mAutoComTextViewBlock != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val blockList = withContext(Dispatchers.IO) {
                        mainActivityViewModel.getBlockListFromDataSet()
                    } ?: return@launch

                    setDropdownBlock(blockList)
                }
            }

            if (mAutoComTextViewViolationDescription != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val violationList = withContext(Dispatchers.IO) {
                        mainActivityViewModel.getViolationListFromDataSet()
                    } ?: return@launch

                    setDropdownViolation(violationList)
                }
            }

            if (mAutoComTextViewDutyHours != null) {
                setDropdownDutyHour(mWelcomeListDataSet?.welcomeList?.shiftStats)
            }
            if (mAutoComTextViewMeterType != null) {
                setDropdownMeterType()
            }
            if (mAutoComTextViewMeterLabelVisible != null) {
                setDropdownMeterLabelVisible()
            }
            if (mAutoComTextViewDigitalDisplayVisible != null) {
                setDropdownDigitalDisplayVisible()
            }
            if (mAutoComTextViewCoinJam != null) {
                setDropdownCoinJam()
            }
            if (mAutoComTextViewCreditCardJam != null) {
                setDropdownCreditCardJam()
            }
            if (mAutoComTextViewDeviceId != null) {
                setDropdownDevice(mWelcomeListDataSet?.welcomeList?.deviceStats)
            }
            if (mAutoComTextViewCurbColor != null) {
                setDropdownCurbColor()
            }
            if (mAutoComTextViewEnforceable != null) {
                setDropdownEnforceable()
            }
            if (mAutoComTextViewTriedToRestartHandHeld != null) {
                setDropdownTriedToRestartHandHeld()
            }
            if (mAutoComTextViewPrintingCorrectly != null) {
                setDropdownPrintingCorrectly()
            }
            if (mAutoComTextViewOverHeating != null) {
                setDropdownOverHeating()
            }
            if (mAutoComTextViewBatteryHoldCharge != null) {
                setDropdownBatteryHoldCharge()
            }
            if (mAutoComTextViewInternetConnectivity != null) {
                setDropdownInternetConnectivity()
            }
            if (mAutoComTextViewGraffiti != null) {
                setDropdownGraffiti()
            }
            if (mAutoComTextViewMissingSign != null) {
                setDropdownMissingSign()
            }
            if (mAutoComTextViewTirePressure != null) {
                setDropdownTirePressure()
            }
            if (mAutoComTextViewAssignedBike != null) {
                setDropdownAssignedBike()
            }
            if (mAutoComTextViewBreaksRotors != null) {
                setDropdownBreaksRotors()
            }
            if (mAutoComTextViewLightsAndReflectors != null) {
                setDropdownLightsAndReflectors()
            }
            if (mAutoComTextViewChainCrank != null) {
                setDropdownChainCrank()
            }
            if (mAutoComTextViewBatteryFreeOfDebris != null) {
                setDropdownBatteryFreeOfDebris()
            }
            if (mAutoComTextViewFlatPack != null) {
                setDropdownFlatPack()
            }
            if (mAutoComTextViewLightBar != null) {
                setDropdownLightBar()
            }
            if (mAutoComTextViewDashBoardIndications != null) {
                setDropdownDashboardIndications()
            }
            if (mAutoComTextViewSeatBeltOperational != null) {
                setDropdownSeatBeltOperational()
            }
            if (mAutoComTextViewBrakes != null) {
                setDropdownBrakes()
            }
            if (mAutoComTextViewBrakeLights != null) {
                setDropdownBrakeLights()
            }
            if (mAutoComTextViewHeadLights != null) {
                setDropdownHeadLights()
            }
            if (mAutoComTextViewTurnSignals != null) {
                setDropdownTurnSignals()
            }
            if (mAutoComTextViewSteeringWheelOperational != null) {
                setDropdownSteeringWheelOperational()
            }
            if (mAutoComTextViewWindshieldVisibility != null) {
                setDropdownWindshieldVisibility()
            }
            if (mAutoComTextViewSideAndRearViewMirrorsOperational != null) {
                setDropdownSideAndRearViewMirrorsOperational()
            }
            if (mAutoComTextViewWindshieldWipersOperational != null) {
                setDropdownWindshieldWipersOperational()
            }
            if (mAutoComTextViewVehicleRegistrationAndInsurance != null) {
                setDropdownVehicleRegistrationAndInsurance()
            }
            if (mAutoComTextViewConesSixPerVehicle != null) {
                setDropdownConesSixPerVehicle()
            }
            if (mAutoComTextViewFirstAidKit != null) {
                setDropdownFirstAidKit()
            }
            if (mAutoComTextViewHorn != null) {
                setDropdownHorn()
            }
            if (mAutoComTextViewInteriorCleanliness != null) {
                setDropdownInteriorCleanliness()
            }
            if (mAutoComTextViewExteriorCleanliness != null) {
                setDropdownExteriorCleanliness()
            }
            if (mAutoComTextViewVisibleLeaks != null) {
                setDropdownVisibleLeaks()
            }
            if (mAutoComTextViewLprLensFreeOfDebris != null) {
                setDropdownLprLensFreeOfDebris()
            }
            if (mAutoComTextViewTiresVisualInspection != null) {
                setDropdownTiresVisualInspection()
            }
            if (mAutoComTextViewStaus != null) {
                setDropdownStatus()
            }
            if (mAutoComTextViewUnitNo != null) {
                setDropdownUnitNo(mWelcomeListDataSet?.welcomeList?.equipmentStates)
            }

            if (mAutoComTextViewAssignedArea != null) {
                setDropdownAssignedArea(mWelcomeListDataSet?.welcomeList?.beatStats)
            }

            if (mAutoComTextViewHandheldUnitNo != null) {
                setDropdownHandHeldUnit(mWelcomeListDataSet?.welcomeList?.deviceStats)
            }

            if (mAutoComTextViewDirection != null) {
                setDropdownDirection()
            }

            if (mAutoComTextViewVehicle != null) {
                setDropdownVehicle()
            }
            if (mAutoComTextViewGasLevel != null) {
                setDropdownGasLevel()
            }

            if (mAutoComTextViewDutyHours != null) {
                setDropdownDutyHour(mWelcomeListDataSet?.welcomeList?.shiftStats)
            }

            if (mAutoComTextViewBikeGlassess != null) {
                setDropdownBikeGlassess()
            }

            if (mAutoComTextViewGloveVisualInspection != null) {
                setDropdownGloveVisualInspection()
            }

            if (mAutoComTextViewBatteryCharge != null) {
                setDropdownBatterCharge()
            }
            if (mAutoComTextViewMake != null) {
                setDropdownMakeVehicle()
            }
            if (mAutoComTextViewColor != null) {
                setDropdownVehicleColour()
            }
            if (mAutoComTextViewLotArea != null) {
                setDropdownLot()
            }
            if (mAutoComTextViewState != null) {
                setDropdownState()
            }
            if (mAutoComTextViewZone != null) {
                setDropdownZone()
            }
            if (mAutoComTextViewPBCZone != null) {
                setDropdownPBCZone()
            }
            if (mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot != null) {
                setDropdownVehicleRelocatedToDifferentStallWithLot()
            }
            if (mAutoComTextViewVehicleStoredAt != null) {
                setDropdownVehicleStoredAtList()
            }
            if (mAutoComTextViewSafetyIssue != null) {
                setDropdownSafetyIssueList()
            }
            if (mAutoComTextViewRequiredServices != null) {
                setDropdownRequiredSevicesList()
            }
            if (mAutoComTextViewReasonForTow != null) {
                setDropdownReasonForTowList()
            }
            if (mAutoComTextViewSpecialEnforcementRequest != null) {
                setDropdownRequestToTowList()
            }
            if (mAutoComTextViewTowingOfficer != null) {
                setDropdownTowingOfficerList()
            }

            if (mAutoComTextViewDate != null) {
                if (!BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_LAMETRO, true
                    ) && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)
                ) {
                    mAutoComTextViewDate?.setOnClickListener {
                        requireActivity().hideSoftKeyboard()
                        AppUtils.openDataPicker(
                            mAutoComTextViewDate,
                            1,
                            childFragmentManager,
                            requireActivity(),
                            myCalendar
                        )
                    }
                }
            }
            if (mAutoComTextViewDutyHours != null) {
//                mAutoComTextViewDutyHours?.setOnClickListener {
//                    requireActivity().hideSoftKeyboard()
//                    openTimePicker(mAutoComTextViewDutyHours)
//                }
            }
            if (mAutoComTextViewLunchTaken != null) {
                mAutoComTextViewLunchTaken?.setOnClickListener {
                    requireActivity().hideSoftKeyboard()
                    openTimePicker(mAutoComTextViewLunchTaken)
                }
            }
            if (mAutoComTextViewFirst10MinBreak != null) {
                mAutoComTextViewFirst10MinBreak?.setOnClickListener {
                    requireActivity().hideSoftKeyboard()
                    openTimePicker(mAutoComTextViewFirst10MinBreak)
                }
            }
            if (mAutoComTextViewSecond10MinBreak != null) {
                mAutoComTextViewSecond10MinBreak?.setOnClickListener {
                    requireActivity().hideSoftKeyboard()
                    openTimePicker(mAutoComTextViewSecond10MinBreak)
                }
            }
            if (mAutoComTextViewTimeMark != null && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                ) && mAutoComTextViewTimeMark != null && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                )
            ) {
                mAutoComTextViewTimeMark?.setOnClickListener {
                    requireActivity().hideSoftKeyboard()
                    openTimePicker(mAutoComTextViewTimeMark)
                }
            }
            if (mAutoComTextViewTimeMark2 != null) {
                mAutoComTextViewTimeMark2?.setOnClickListener {
                    requireActivity().hideSoftKeyboard()
                    openTimePicker(mAutoComTextViewTimeMark2)
                }
            }
            if (mAutoComTextViewTimeMark3 != null) {
                mAutoComTextViewTimeMark3?.setOnClickListener {
                    requireActivity().hideSoftKeyboard()
                    openTimePicker(mAutoComTextViewTimeMark3)
                }
            }
            if (mAutoComTextViewTimeMark4 != null) {
                mAutoComTextViewTimeMark4?.setOnClickListener {
                    requireActivity().hideSoftKeyboard()
                    openTimePicker(mAutoComTextViewTimeMark4)
                }
            }
            if (mAutoComTextViewCitationsIssued != null && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                ) && mAutoComTextViewCitationsIssued != null && !BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                )
            ) {
                mAutoComTextViewCitationsIssued?.setOnClickListener {
                    requireActivity().hideSoftKeyboard()
                    openTimePicker(mAutoComTextViewCitationsIssued)
                }
            }
            if (mAutoComTextViewOfficer != null) {
                mAutoComTextViewOfficer?.setText(
                    mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName
                )
            }
//            geoAddress()
            mEditTextOfficerName?.text =
                mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName

            mEditTextOfficerId?.text = mWelcomeFormData?.officerBadgeId
            mEditTextOfficerBeat?.text = mWelcomeFormData?.officerBeatName
            val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
            mEditTextReportNumber.setText(timeStamp.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /* //set value to Body Style dropdown
     private fun setDropdownRemark(mApplicationList: List<DatasetResponse>?) {
         val addZoneValueInZeroIndex = DatasetResponse()
         Collections.sort(mApplicationList) { lhs, rhs -> lhs.remark.nullSafety().compareTo(rhs.remark.nullSafety()) }
         val pos = 0
         if (mApplicationList != null && mApplicationList.isNotEmpty()) {
             val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
             for (i in mApplicationList.indices) {
                 mDropdownList[i] = mApplicationList[i].remark.toString()
             }

 //            Arrays.sort(mDropdownList);
             val adapter = ArrayAdapter(
                     requireContext(),
                     R.layout.row_dropdown_menu_popup_item,
                     mDropdownList
             )
            try {
                mAutoComTextViewComments?.threshold = 1
                mAutoComTextViewComments?.setAdapter<ArrayAdapter<String?>>(adapter)
                 //mSelectedShiftStat = mApplicationList.get(pos);
                mAutoComTextViewComments?.onItemClickListener =
                         OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                             requireActivity().hideSoftKeyboard()
                             mAutoComTextViewComments?.error = null
                         }
             } catch (e: Exception) {
                 e.printStackTrace()
             }
         }
     }*/

    //set value to Street dropdown
    private fun setDropdownStreet(mApplicationList: List<DatasetResponse>?) {
//        hideSoftKeyboard(BootActivity.this);
        val pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].street_name.toString()
            }

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mAutoComTextViewStreet?.threshold = 1
                mAutoComTextViewStreet?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewStreet?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> //mAutoComTextViewDirection.setText(mApplicationList.get(position).getDirection()); ;
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewStreet?.setError(null)
                    }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownBlock(mApplicationList: List<DatasetResponse>?) {
//        hideSoftKeyboard(requireContext()@LprDetails2Activity)
        var pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].blockName.toString()
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mAutoComTextViewBlock?.threshold = 1
                mAutoComTextViewBlock?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewBlock?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        //mAppComTextViewBlock.setError(null)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownViolation(mApplicationList: List<DatasetResponse>?) {
        var pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].violation.toString()
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mAutoComTextViewViolationDescription?.threshold = 1
                mAutoComTextViewViolationDescription?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewViolationDescription?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewViolationDescription?.setError(null)
                        val index = getIndexOfViotiona(
                            mApplicationList, parent.getItemAtPosition(position).toString()
                        )

                        violationDescription.add(mApplicationList.get(index).violation.toString())
                        violationDescription.add(mApplicationList.get(index).violationDescription.toString())
                        violationDescription.add(mApplicationList.get(index).abbrev.toString())
                        violationDescription.add(mApplicationList.get(index).violationCode.toString())
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getIndexOfViotiona(list: List<DatasetResponse>, name: String): Int {

        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.violation, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to Meter Name dropdown
    private fun setDropdownMeterName(mApplicationList: List<DatasetResponse>?) {
        val pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].name.toString()
            }
            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mAutoComTextViewMeterNo?.threshold = 1
                mAutoComTextViewMeterNo?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewMeterNo?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        try {
                            mAutoComTextViewMeterNo?.error = null
//                                val index: Int = getIndexOfMeter(mApplicationList,
//                                        mAutoComTextViewMeterNo.text.toString())

//                                mAutoComTextViewBlock?.setText(mApplicationList[index].block.nullSafety())
//                                mAutoComTextViewDirection?.setText(mApplicationList[index].direction.toString())
//                                mAutoComTextViewStreet?.setText(mApplicationList[index].street.toString())

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getIndexOfMeter(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.name, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to ture false dropdown
    private fun setDropdownMeterLabelVisible() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewMeterLabelVisible?.threshold = 1
                mAutoComTextViewMeterLabelVisible?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewMeterLabelVisible?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewMeterLabelVisible?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //set value to ture false dropdown
    private fun setDropdownVehicleRelocatedToDifferentStallWithLot() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.threshold = 1
                mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.setAdapter<ArrayAdapter<String?>>(
                    adapter
                )
                mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownVehicleStoredAtList() {
        val pos = 0
        if (mVehicleStoredAtList != null && mVehicleStoredAtList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mVehicleStoredAtList
            )
            try {
                mAutoComTextViewVehicleStoredAt?.threshold = 1
                mAutoComTextViewVehicleStoredAt?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewVehicleStoredAt?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewVehicleStoredAt?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } //set value to ture false dropdown//set value to ture false dropdown

    private fun setDropdownSafetyIssueList() {
        val pos = 0
        if (mSafetyReportList != null && mSafetyReportList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mSafetyReportList
            )
            try {
                mAutoComTextViewSafetyIssue?.threshold = 1
                mAutoComTextViewSafetyIssue?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewSafetyIssue?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewSafetyIssue?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setDropdownRequiredSevicesList() {
        val pos = 0
        if (mRequiredServicesList != null && mRequiredServicesList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mRequiredServicesList
            )
            try {
                mAutoComTextViewRequiredServices?.threshold = 1
                mAutoComTextViewRequiredServices?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewRequiredServices?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewRequiredServices?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownReasonForTowList() {
        val pos = 0
        if (mReasonForTowList != null && mReasonForTowList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mReasonForTowList
            )
            try {
                mAutoComTextViewReasonForTow?.threshold = 1
                mAutoComTextViewReasonForTow?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewReasonForTow?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewReasonForTow?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set Request to tow dropdown
    private fun setDropdownRequestToTowList() {
        val pos = 0
        if (mRequestToTowList != null && mRequestToTowList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mRequestToTowList
            )
            try {
                mAutoComTextViewSpecialEnforcementRequest?.threshold = 1
                mAutoComTextViewSpecialEnforcementRequest?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewSpecialEnforcementRequest?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewSpecialEnforcementRequest?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set Request to towing officer dropdown
    private fun setDropdownTowingOfficerList() {
        val pos = 0
        if (ReportArrayClass.mTowingOfficerListTowReport != null && ReportArrayClass.mTowingOfficerListTowReport.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.row_dropdown_menu_popup_item,
                ReportArrayClass.mTowingOfficerListTowReport
            )
            try {
                mAutoComTextViewTowingOfficer?.threshold = 1
                mAutoComTextViewTowingOfficer?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewTowingOfficer?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewTowingOfficer?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownDigitalDisplayVisible() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewDigitalDisplayVisible?.threshold = 1
                mAutoComTextViewDigitalDisplayVisible?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewDigitalDisplayVisible?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewDigitalDisplayVisible?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownCoinJam() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewCoinJam?.threshold = 1
                mAutoComTextViewCoinJam?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewCoinJam?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewCoinJam?.error = null

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownCreditCardJam() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {
            mAutoComTextViewCreditCardJam?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
                )
                try {
                    mAutoComTextViewCreditCardJam?.threshold = 1
                    mAutoComTextViewCreditCardJam?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewCreditCardJam?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewCreditCardJam?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownCurbColor() {
        val pos = 0
        if (mColorList != null && mColorList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mColorList
            )
            try {
                mAutoComTextViewCurbColor?.threshold = 1
                mAutoComTextViewCurbColor?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewCurbColor?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewCurbColor?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownMeterType() {
        val pos = 0
        if (mMeterTypeList != null && mMeterTypeList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mMeterTypeList
            )
            try {
                mAutoComTextViewMeterType?.threshold = 1
                mAutoComTextViewMeterType?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewMeterType?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewMeterType?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownEnforceable() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewEnforceable?.threshold = 1
                mAutoComTextViewEnforceable?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewEnforceable?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewEnforceable?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownTriedToRestartHandHeld() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewTriedToRestartHandHeld?.threshold = 1
                mAutoComTextViewTriedToRestartHandHeld?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewTriedToRestartHandHeld?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewTriedToRestartHandHeld?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownPrintingCorrectly() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewPrintingCorrectly?.threshold = 1
                mAutoComTextViewPrintingCorrectly?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewPrintingCorrectly?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewPrintingCorrectly?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownOverHeating() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewOverHeating?.threshold = 1
                mAutoComTextViewOverHeating?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewOverHeating?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewOverHeating?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownBatteryHoldCharge() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewBatteryHoldCharge?.threshold = 1
                mAutoComTextViewBatteryHoldCharge?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewBatteryHoldCharge?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewBatteryHoldCharge?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownInternetConnectivity() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewInternetConnectivity?.threshold = 1
                mAutoComTextViewInternetConnectivity?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewInternetConnectivity?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewInternetConnectivity?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownGraffiti() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewGraffiti?.threshold = 1
                mAutoComTextViewGraffiti?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewGraffiti?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewGraffiti?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownMissingSign() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
            )
            try {
                mAutoComTextViewMissingSign?.threshold = 1
                mAutoComTextViewMissingSign?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewMissingSign?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        mAutoComTextViewMissingSign?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownTirePressure() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewTirePressure?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewTirePressure?.threshold = 1
                    mAutoComTextViewTirePressure?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewTirePressure?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewTirePressure?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownAssignedBike() {
        val pos = 0
        if (mAssignedBikeList != null && mAssignedBikeList.isNotEmpty()) {

            mAutoComTextViewAssignedBike?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mAssignedBikeList
                )
                try {
                    mAutoComTextViewAssignedBike?.threshold = 1
                    mAutoComTextViewAssignedBike?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewAssignedBike?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewAssignedBike?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownBreaksRotors() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewBreaksRotors?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewBreaksRotors?.threshold = 1
                    mAutoComTextViewBreaksRotors?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBreaksRotors?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewBreaksRotors?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownLightsAndReflectors() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewLightsAndReflectors?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewLightsAndReflectors?.threshold = 1
                    mAutoComTextViewLightsAndReflectors?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewLightsAndReflectors?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewLightsAndReflectors?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownChainCrank() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewChainCrank?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewChainCrank?.threshold = 1
                    mAutoComTextViewChainCrank?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewChainCrank?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewChainCrank?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownBatteryFreeOfDebris() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewBatteryFreeOfDebris?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewBatteryFreeOfDebris?.threshold = 1
                    mAutoComTextViewBatteryFreeOfDebris?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBatteryFreeOfDebris?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewBatteryFreeOfDebris?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownFlatPack() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewFlatPack?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewFlatPack?.threshold = 1
                    mAutoComTextViewFlatPack?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewFlatPack?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewFlatPack?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownLightBar() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewLightBar?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewLightBar?.threshold = 1
                    mAutoComTextViewLightBar?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewLightBar?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewLightBar?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownDashboardIndications() {
        val pos = 0
        if (mPassFailedList.isNotEmpty()) {

            mAutoComTextViewDashBoardIndications?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewDashBoardIndications?.threshold = 1
                    mAutoComTextViewDashBoardIndications?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewDashBoardIndications?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewDashBoardIndications?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownSeatBeltOperational() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewSeatBeltOperational?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewSeatBeltOperational?.threshold = 1
                    mAutoComTextViewSeatBeltOperational?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewSeatBeltOperational?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewSeatBeltOperational?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownBrakes() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewBrakes?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewBrakes?.threshold = 1
                    mAutoComTextViewBrakes?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBrakes?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewBrakes?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownBrakeLights() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewBrakeLights?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewBrakeLights?.threshold = 1
                    mAutoComTextViewBrakeLights?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBrakeLights?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewBrakeLights?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownHeadLights() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewHeadLights?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewHeadLights?.threshold = 1
                    mAutoComTextViewHeadLights?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewHeadLights?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewHeadLights?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownTurnSignals() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewTurnSignals?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewTurnSignals?.threshold = 1
                    mAutoComTextViewTurnSignals?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewTurnSignals?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewTurnSignals?.error = null

                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownSteeringWheelOperational() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewSteeringWheelOperational?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewSteeringWheelOperational?.threshold = 1
                    mAutoComTextViewSteeringWheelOperational?.setAdapter<ArrayAdapter<String?>>(
                        adapter
                    )
                    mAutoComTextViewSteeringWheelOperational?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewSteeringWheelOperational?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownWindshieldVisibility() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewWindshieldVisibility?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewWindshieldVisibility?.threshold = 1
                    mAutoComTextViewWindshieldVisibility?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewWindshieldVisibility?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewWindshieldVisibility?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownSideAndRearViewMirrorsOperational() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewSideAndRearViewMirrorsOperational?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewSideAndRearViewMirrorsOperational?.threshold = 1
                    mAutoComTextViewSideAndRearViewMirrorsOperational?.setAdapter<ArrayAdapter<String?>>(
                        adapter
                    )
                    mAutoComTextViewSideAndRearViewMirrorsOperational?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewSideAndRearViewMirrorsOperational?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownWindshieldWipersOperational() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewWindshieldWipersOperational?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewWindshieldWipersOperational?.threshold = 1
                    mAutoComTextViewWindshieldWipersOperational?.setAdapter<ArrayAdapter<String?>>(
                        adapter
                    )
                    mAutoComTextViewWindshieldWipersOperational?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewWindshieldWipersOperational?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownVehicleRegistrationAndInsurance() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            mAutoComTextViewVehicleRegistrationAndInsurance?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
                )
                try {
                    mAutoComTextViewVehicleRegistrationAndInsurance?.threshold = 1
                    mAutoComTextViewVehicleRegistrationAndInsurance?.setAdapter<ArrayAdapter<String?>>(
                        adapter
                    )
                    mAutoComTextViewVehicleRegistrationAndInsurance?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewVehicleRegistrationAndInsurance?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownFirstAidKit() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            mAutoComTextViewFirstAidKit?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
                )
                try {
                    mAutoComTextViewFirstAidKit?.threshold = 1
                    mAutoComTextViewFirstAidKit?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewFirstAidKit?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewFirstAidKit?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownConesSixPerVehicle() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewConesSixPerVehicle?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewConesSixPerVehicle?.threshold = 1
                    mAutoComTextViewConesSixPerVehicle?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewConesSixPerVehicle?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewConesSixPerVehicle?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownHorn() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewHorn?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewHorn?.threshold = 1
                    mAutoComTextViewHorn?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewHorn?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewHorn?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownVehicle() {
        val pos = 0
        if (mDeviceList != null && mDeviceList.isNotEmpty()) {

            mAutoComTextViewVehicle?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mDeviceList
                )
                try {
                    mAutoComTextViewVehicle?.threshold = 1
                    mAutoComTextViewVehicle?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewVehicle?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewVehicle?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownGasLevel() {
        val pos = 0
        if (mGasList != null && mGasList.isNotEmpty()) {

            mAutoComTextViewGasLevel?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mGasList
                )
                try {
                    mAutoComTextViewGasLevel?.threshold = 1
                    mAutoComTextViewGasLevel?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewGasLevel?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewGasLevel?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownDutyHour(mApplicationList: List<ShiftStat>?) {
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val pos = 0
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].shiftName.toString()
                }
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                )
                try {
                    mAutoComTextViewDutyHours?.threshold = 1
                    mAutoComTextViewDutyHours?.setAdapter<ArrayAdapter<String?>>(adapter)
                    //                mSelectedShiftStat = mApplicationList.get(pos);
                    mAutoComTextViewDutyHours?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id -> //                        mSelectedShiftStat = mApplicationList.get(position);
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewDutyHours?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
            }
        }


//        if (mDutyHourAreaList != null && mDutyHourAreaList.isNotEmpty()) {
//
//            mAutoComTextViewDutyHours.post {
//                val adapter = ArrayAdapter(
//                        requireContext(),
//                        R.layout.row_dropdown_menu_popup_item,
//                        mDutyHourAreaList
//                )
//                try {
//                    mAutoComTextViewDutyHours?.threshold = 1
//                    mAutoComTextViewDutyHours?.setAdapter<ArrayAdapter<String?>>(adapter)
//                    mAutoComTextViewDutyHours?.onItemClickListener =
//                            OnItemClickListener { parent, view, position, id ->
//                                requireActivity().hideSoftKeyboard()
//                                mAutoComTextViewDutyHours?.error = null
//                            }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    //set value to ture false dropdown
    private fun setDropdownInteriorCleanliness() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewInteriorCleanliness?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewInteriorCleanliness?.threshold = 1
                    mAutoComTextViewInteriorCleanliness?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewInteriorCleanliness?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewInteriorCleanliness?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownExteriorCleanliness() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewExteriorCleanliness?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewExteriorCleanliness?.threshold = 1
                    mAutoComTextViewExteriorCleanliness?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewExteriorCleanliness?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewExteriorCleanliness?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownVisibleLeaks() {
        val pos = 0
        if (mYesNoList != null && mYesNoList.isNotEmpty()) {

            mAutoComTextViewVisibleLeaks?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mYesNoList
                )
                try {
                    mAutoComTextViewVisibleLeaks?.threshold = 1
                    mAutoComTextViewVisibleLeaks?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewVisibleLeaks?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewVisibleLeaks?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownLprLensFreeOfDebris() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewLprLensFreeOfDebris?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewLprLensFreeOfDebris?.threshold = 1
                    mAutoComTextViewLprLensFreeOfDebris?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewLprLensFreeOfDebris?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewLprLensFreeOfDebris?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownTiresVisualInspection() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewTiresVisualInspection?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewTiresVisualInspection?.threshold = 1
                    mAutoComTextViewTiresVisualInspection?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewTiresVisualInspection?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewTiresVisualInspection?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownStatus() {
        val pos = 0
        if (mOutInServiceList != null && mOutInServiceList.isNotEmpty()) {

            mAutoComTextViewStaus?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mOutInServiceList
                )
                try {
                    mAutoComTextViewStaus?.threshold = 1
                    mAutoComTextViewStaus?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewStaus?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewStaus?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to Device dropdown
    private fun setDropdownDevice(mApplicationList: List<DeviceResponseItem>?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val officerDeviceName = withContext(Dispatchers.IO) {
                allReportsScreenViewModel.getWelcomeForm()?.officerDeviceName
            } ?: return@launch

            var pos = -1
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].friendlyName.toString()
                    try {
                        if (!TextUtils.isEmpty(officerDeviceName)) {
                            if (mApplicationList[i].friendlyName.equals(
                                    officerDeviceName, ignoreCase = true
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
                Arrays.sort(mDropdownList);
                mAutoComTextViewDeviceId?.post {
                    if (pos >= 0) {
//                        mAutoComTextViewDeviceId.setText(mDropdownList[pos])
//                        selectedDeViceId.mDeviceFriendlyName = mApplicationList[pos].friendlyName
//                        selectedDeViceId.mDeviceId = mApplicationList[pos].friendlyName
                    }
                    val adapter = ArrayAdapter(
                        requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                    )
                    try {
                        mAutoComTextViewDeviceId?.threshold = 1
                        mAutoComTextViewDeviceId?.setAdapter<ArrayAdapter<String?>>(adapter)
                        //                mSelectedAgency = mApplicationList.get(pos).getAgency_name();
                        mAutoComTextViewDeviceId?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                mAutoComTextViewDeviceId?.error = null
//                                    val index = getIndexOfDevice(
//                                            mApplicationList,
//                                            parent.getItemAtPosition(position).toString()
//                                    )
//                                    selectedDeViceId.mDeviceFriendlyName =
//                                            mApplicationList[index].friendlyName
//                                    selectedDeViceId.mDeviceId = mApplicationList[index].friendlyName
//                                    requireActivity().hideSoftKeyboard()
//                                    mAutoComTextViewDeviceId.setText(mApplicationList[index].friendlyName)
//                                    requireActivity().hideSoftKeyboard()
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to Device dropdown
    private fun setDropdownHandHeldUnit(mApplicationList: List<DeviceResponseItem>?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val officerDeviceName = withContext(Dispatchers.IO) {
                allReportsScreenViewModel.getWelcomeForm()?.officerDeviceName
            } ?: return@launch

            var pos = -1
            if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].friendlyName.toString()
                    try {
                        if (!TextUtils.isEmpty(officerDeviceName)) {
                            if (mApplicationList[i].friendlyName.equals(
                                    officerDeviceName, ignoreCase = true
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
                mAutoComTextViewHandheldUnitNo?.post {
                    if (pos >= 0) {
                        mAutoComTextViewHandheldUnitNo?.setText(mDropdownList[pos])
                        selectedDeViceId.mDeviceFriendlyName = mApplicationList[pos].friendlyName
                        selectedDeViceId.mDeviceId = mApplicationList[pos].friendlyName
                    }
                    val adapter = ArrayAdapter(
                        requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                    )
                    try {
                        mAutoComTextViewDeviceId?.threshold = 1
                        mAutoComTextViewDeviceId?.setAdapter<ArrayAdapter<String?>>(adapter)
                        //                mSelectedAgency = mApplicationList.get(pos).getAgency_name();
                        mAutoComTextViewHandheldUnitNo?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                mAutoComTextViewHandheldUnitNo?.error = null

                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to Direction dropdown
    private fun setDropdownDirection() {
        viewLifecycleOwner.lifecycleScope.launch {
            val sideList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getSideListFromDataSet()
            } ?: return@launch

            val pos = -1

            if (sideList.isNotEmpty()) {
                try {
                    Collections.sort(
                        sideList, object : java.util.Comparator<DatasetResponse?> {
                            override fun compare(
                                lhs: DatasetResponse?, rhs: DatasetResponse?
                            ): Int {
                                return lhs?.sideName.nullSafety()
                                    .compareTo(rhs?.sideName.nullSafety())
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val mDropdownList = arrayOfNulls<String>(sideList.size)
                for (i in sideList.indices) {
                    mDropdownList[i] = sideList[i].sideName.toString()
                }
                mAutoComTextViewDirection?.post {
                    val adapter = ArrayAdapter(
                        requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                    )
                    try {
                        mAutoComTextViewDirection?.threshold = 1
                        mAutoComTextViewDirection?.setAdapter<ArrayAdapter<String?>>(adapter)
                        //mSelectedShiftStat = mApplicationList.get(pos);
                        mAutoComTextViewDirection?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                requireActivity().hideSoftKeyboard()

                            }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    //set value to Device dropdown
    private fun setDropdownUnitNo(mApplicationList: List<ResponseItem>?) {
//        requireActivity().hideSoftKeyboard()

//        if (mUnitNumberList != null && mUnitNumberList.isNotEmpty()) {
        var unitList = mDeviceList
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
        ) {
            unitList = mUnitNumberList
        }
        if (unitList != null && unitList.isNotEmpty()) {
            mAutoComTextViewUnitNo?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, unitList
                )
                try {
                    mAutoComTextViewUnitNo?.threshold = 1
                    mAutoComTextViewUnitNo?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewUnitNo?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewUnitNo?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to beat dropdown
    private fun setDropdownAssignedArea(mApplicationList: List<BeatStat>?) {
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {

            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].beatName.toString()
            }
            //  Arrays.sort(mDropdownList);
            mAutoComTextViewAssignedArea?.post {

                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                )
                try {
                    mAutoComTextViewAssignedArea?.threshold = 1
                    mAutoComTextViewAssignedArea?.setAdapter<ArrayAdapter<String?>>(adapter)

                    mAutoComTextViewAssignedArea?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
//                                    val index = mApplicationList.indexOfFirst{
//                                        it.beatName == mAutoComTextViewAssignedArea?.text.toString()
//                                    }
                            mAutoComTextViewAssignedArea?.error = null
                            requireActivity().hideSoftKeyboard()
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
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

    //set value to ture false dropdown
    private fun setDropdownBikeGlassess() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewBikeGlassess?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewBikeGlassess?.threshold = 1
                    mAutoComTextViewBikeGlassess?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBikeGlassess?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewBikeGlassess?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to ture false dropdown
    private fun setDropdownGloveVisualInspection() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewGloveVisualInspection?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mPassFailedList
                )
                try {
                    mAutoComTextViewGloveVisualInspection?.threshold = 1
                    mAutoComTextViewGloveVisualInspection?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewGloveVisualInspection?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewGloveVisualInspection?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    //set value to ture false dropdown
    private fun setDropdownBatterCharge() {
        val pos = 0
        if (mGasList != null && mGasList.isNotEmpty()) {

            mAutoComTextViewBatteryCharge?.post {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, mBatterChargeList
                )
                try {
                    mAutoComTextViewBatteryCharge?.threshold = 1
                    mAutoComTextViewBatteryCharge?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBatteryCharge?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            requireActivity().hideSoftKeyboard()
                            mAutoComTextViewBatteryCharge?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }/* //set value to side dropdown
     private fun setDropdownSide(mApplicationList: List<DatasetResponse>?) {
         var pos = 0
         if (mApplicationList != null && mApplicationList.isNotEmpty()) {
             val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
             for (i in mApplicationList.indices) {
                 mDropdownList[i] = mApplicationList[i].sideName.toString()
                 if (mApplicationList[i].sideName.equals(mSideItem, ignoreCase = true)) {
                     pos = i
                     try {
                         mEditTextViewTextViewSideOfState?.setText(mDropdownList[pos])
                     } catch (e: Exception) {
                     }
                 }
             }
             Arrays.sort(mDropdownList)
             val adapter = ArrayAdapter(
                     requireContext(),
                     R.layout.row_dropdown_menu_popup_item,
                     mDropdownList
             )
             try {
                 mEditTextViewTextViewSideOfState?.threshold = 1
                 mEditTextViewTextViewSideOfState?.setAdapter<ArrayAdapter<String?>>(adapter)
                 //mSelectedShiftStat = mApplicationList.get(pos);
                 mEditTextViewTextViewSideOfState?.onItemClickListener =
                         OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                             AppUtils.hideSoftKeyboard(requireContext()@BrokenMeterActivity)
                             mEditTextViewTextViewSideOfState?.error = null
                         }
             } catch (e: Exception) {
             }
         } else {
         }
     }



     private fun getIndexOfMeter(list: List<DatasetResponse>, name: String): Int {
         var pos = 0
         for (myObj in list) {
             if (name.equals(myObj.name, ignoreCase = true)) return pos
             pos++
         }
         return -1
     }

     //set value to Meter Name dropdown
     private fun setDropdownMeteroutage(mApplicationList: Array<String>) {
 //        Arrays.sort(mApplicationList);
         val adapter = ArrayAdapter(
                 requireContext(),
                 R.layout.row_dropdown_menu_popup_item,
                 mApplicationList
         )
         try {
             mEditTextViewTextViewMeterOutage?.threshold = 1
             mEditTextViewTextViewMeterOutage?.setAdapter(adapter)
             //mSelectedShiftStat = mApplicationList.get(pos);
             mEditTextViewTextViewMeterOutage?.onItemClickListener =
                     OnItemClickListener { parent, view, position, id ->
                         AppUtils.hideSoftKeyboard(requireContext()@BrokenMeterActivity)
                         outageValue = meterOutageListValue[position]
                         outageTypeValue = meterOutageListTypeValue[position]
                     }
         } catch (e: Exception) {
         }
     }


 */


    private fun setRequest(isFrom: String) {
        if (NetworkCheck.isInternetAvailable(requireContext())) {
            if (isFrom.equals(REPORT_TYPE_BROKEN_METER_REPORT, ignoreCase = true)) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong =
                    sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val brokenRequest = BrokenMeterReportRequest()
                brokenRequest.form = isFrom

                val locationDetail = LocationDetails()
                locationDetail.latitude = mLat
                locationDetail.longitude = mLong
                locationDetail.block = mAutoComTextViewBlock?.text.toString()
                locationDetail.street = mAutoComTextViewStreet?.text.toString()
                locationDetail.direction = mAutoComTextViewDirection?.text.toString()
                brokenRequest.locationDetails = locationDetail


                val officerDetails = OfficerDetails()
                officerDetails.officerName =
                    (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails.shiftId =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails.beat = mWelcomeFormData?.officerBeat
                brokenRequest.officerDetails = officerDetails

                val details = Details()
                details.deviceId = ""//mAutoComTextViewDeviceId.text.toString()
                details.meterNo = mAutoComTextViewMeterNo?.text.toString()
                details.meterType = mAutoComTextViewMeterType?.text.toString()
                details.meterLabelVisible = mAutoComTextViewMeterLabelVisible?.text.toString()
                    .equals("YES", ignoreCase = true)
                details.digitalDisplayVisible =
                    mAutoComTextViewDigitalDisplayVisible?.text.toString()
                        .equals("YES", ignoreCase = true)
                details.coinJam =
                    mAutoComTextViewCoinJam?.text.toString().equals("YES", ignoreCase = true)
                details.creditCardJam =
                    mAutoComTextViewCreditCardJam?.text.toString().equals("YES", ignoreCase = true)
                details.comments = mAutoComTextViewComments?.text.toString()
                details.status = ""
                brokenRequest.details = details

                allReportsScreenViewModel.callBrokenMeterReportAPI(brokenRequest)
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "---------Broken Meter Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                brokenRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (isFrom.equals(REPORT_TYPE_CURB_REPORT, ignoreCase = true)) {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (requireContext().isInternetAvailable()) {
                        callBulkImageUploadAPI(
                            uploadType = API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                            bannerList = bannerList
                        )
                    } else {
                        LogUtil.printToastMSG(
                            requireContext(), getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)


            } else if (isFrom.equals(REPORT_TYPE_EOW_OFFICER_REPORT, ignoreCase = true)) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong =
                    sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val fullTimeRequest = FullTimeRequest()
                fullTimeRequest.form = "FULL TIME"

                val locationDetail = LocationDetailsFulltime()
                locationDetail.latitude = mLat
                locationDetail.longitude = mLong
                locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
                locationDetail.street = ""//mAutoComTextViewStreet.text.toString()

                fullTimeRequest.locationDetailsFulltime = locationDetail


                val officerDetails = OfficerDetailsFulltime()
                officerDetails.officerName =
                    (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails.shiftId =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails.beat = mWelcomeFormData?.officerBeat
                fullTimeRequest.officerDetailsFulltime = officerDetails

                val details = DetailsFulltime()
                details.deviceId = mAutoComTextViewDeviceId?.text.toString()
                details.date = ""// mAutoComTextViewDate.text.toString()
                details.dutyHours = mAutoComTextViewDutyHours?.text.toString()
                details.officer = ""//mAutoComTextViewOfficer.text.toString()
                details.lunchTaken = (mAutoComTextViewLunchTaken?.tag.toString())
                details.first10MinBreak = (mAutoComTextViewFirst10MinBreak?.tag.toString())
                details.second10MinBreak = (mAutoComTextViewSecond10MinBreak?.tag.toString())
                details.unitNo = mAutoComTextViewUnitNo?.text.toString()
                details.assignedArea = mAutoComTextViewAssignedArea?.text.toString()
                details.ossiDeviceNo = ""//mAutoComTextViewOssiDeviceNo.text.toString()
                details.handheldUnitNo = ""//mAutoComTextViewHandheldUnitNo.text.toString()
                details.citationsIssued = mAutoComTextViewCitationsIssued?.text.toString()
                details.specialEnforcementRequest =
                    mAutoComTextViewSpecialEnforcementRequest?.text.toString()
                details.status = ""//mAutoComTextViewStaus.text.toString()
                fullTimeRequest.detailsFulltime = details
                allReportsScreenViewModel.callFullTimeReportAPI(fullTimeRequest)
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "---------Full Time Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                fullTimeRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (isFrom.equals(REPORT_TYPE_PART_EOW_OFFICER_REPORT, ignoreCase = true)) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong =
                    sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val fullTimeRequest = FullTimeRequest()
                fullTimeRequest.form = "PART TIME"

                val locationDetail = LocationDetailsFulltime()
                locationDetail.latitude = mLat
                locationDetail.longitude = mLong
                locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
                locationDetail.street = ""//mAutoComTextViewStreet.text.toString()
                fullTimeRequest.locationDetailsFulltime = locationDetail


                val officerDetails = OfficerDetailsFulltime()
                officerDetails.officerName =
                    (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails.shiftId =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails.beat = mWelcomeFormData?.officerBeat
                fullTimeRequest.officerDetailsFulltime = officerDetails

                val details = DetailsFulltime()
                details.deviceId = mAutoComTextViewDeviceId?.text.toString()
                details.date = ""// mAutoComTextViewDate.text.toString()
                details.dutyHours = mAutoComTextViewDutyHours?.text.toString()
                details.officer = ""//mAutoComTextViewOfficer.text.toString()
//                details.lunchTaken ="remove"// mAutoComTextViewLunchTaken.text.toString()
                details.first10MinBreak = (mAutoComTextViewFirst10MinBreak?.tag.toString())
                details.second10MinBreak = (mAutoComTextViewSecond10MinBreak?.tag.toString())
                details.unitNo = mAutoComTextViewUnitNo?.text.toString()
                details.assignedArea = mAutoComTextViewAssignedArea?.text.toString()
                details.ossiDeviceNo = ""//mAutoComTextViewOssiDeviceNo.text.toString()
                details.handheldUnitNo = ""//mAutoComTextViewHandheldUnitNo.text.toString()
                details.citationsIssued = mAutoComTextViewCitationsIssued?.text.toString()
                details.specialEnforcementRequest =
                    mAutoComTextViewSpecialEnforcementRequest?.text.toString()
                details.status = ""//mAutoComTextViewStaus.text.toString()
                fullTimeRequest.detailsFulltime = details
                allReportsScreenViewModel.callPartTimeReportAPI(fullTimeRequest)

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "---------Part Time Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                fullTimeRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (isFrom.equals(REPORT_TYPE_HAND_HELD_MALFUNCTION_REPORT, ignoreCase = true)) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong =
                    sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val handHeldMalFunctionsRequest = HandHeldMalFunctionsRequest()
                handHeldMalFunctionsRequest.form = isFrom

                val locationDetail = LocationDetailsHandHeldMalfunctions()
                locationDetail.latitude = mLat
                locationDetail.longitude = mLong
                locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
                locationDetail.street = ""//mAutoComTextViewStreet.text.toString()
                handHeldMalFunctionsRequest.locationDetailsHandHeldMalfunctions = locationDetail


                val officerDetails = OfficerDetailsHandHeldMalfunctions()
                officerDetails.officerName =
                    (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails.shiftId =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails.beat = mWelcomeFormData?.officerBeat
                handHeldMalFunctionsRequest.officerDetailsHandHeldMalfunctions = officerDetails

                val details = DetailsHandHeldMalfunctions()
                details.deviceId = mAutoComTextViewDeviceId?.text.toString()
                details.handHeldUnitNo = ""//mAutoComTextViewHandheldUnitNo.text.toString()
                details.triedToRestartHandHeld =
                    mAutoComTextViewTriedToRestartHandHeld?.text.toString()
                        .equals("YES", ignoreCase = true)
                details.printingCorrectly = mAutoComTextViewPrintingCorrectly?.text.toString()
                    .equals("YES", ignoreCase = true)
                details.overheating =
                    mAutoComTextViewOverHeating?.text.toString().equals("YES", ignoreCase = true)
                details.batteryHoldCharge = mAutoComTextViewBatteryHoldCharge?.text.toString()
                    .equals("YES", ignoreCase = true)
                details.internetConnectivity = mAutoComTextViewInternetConnectivity?.text.toString()
                    .equals("YES", ignoreCase = true)
                details.describeHandHeldMalfunctionInDetail =
                    mAutoComTextViewDescribeHandHeldMalfunctionInDetail?.text.toString()
                details.status = mAutoComTextViewStaus?.text.toString()
                handHeldMalFunctionsRequest.detailsHandHeldMalfunctions = details
                allReportsScreenViewModel.callHandHeldMalfunctionReportAPI(
                    handHeldMalFunctionsRequest
                )
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "---------Hand Held Malfunction Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                handHeldMalFunctionsRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (isFrom.equals(REPORT_TYPE_SIGN_REPORT, ignoreCase = true)) {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (requireContext().isInternetAvailable()) {
                        callBulkImageUploadAPI(
                            uploadType = API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                            bannerList = bannerList
                        )
                    } else {
                        LogUtil.printToastMSG(
                            requireContext(), getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)

            } else if (isFrom.equals(REPORT_TYPE_VEHICLE_INSPECTION_REPORT, ignoreCase = true)) {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (requireContext().isInternetAvailable()) {
                        callBulkImageUploadAPI(
                            uploadType = API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                            bannerList = bannerList
                        )
                    } else {
                        LogUtil.printToastMSG(
                            requireContext(), getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)

            } else if (isFrom.equals(
                    REPORT_TYPE_72_HOURS_MARKED_VEHICLE_REPORT, ignoreCase = true
                )
            ) {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (requireContext().isInternetAvailable()) {
                        callBulkImageUploadAPI(
                            uploadType = API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                            bannerList = bannerList
                        )
                    } else {
                        LogUtil.printToastMSG(
                            requireContext(), getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)

            } else if (reportType.equals(REPORT_TYPE_BIKE_INSPECTION_REPORT, ignoreCase = true)) {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (requireContext().isInternetAvailable()) {
                        callBulkImageUploadAPI(
                            uploadType = API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                            bannerList = bannerList
                        )
                    } else {
                        LogUtil.printToastMSG(
                            requireContext(), getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)

            } else if (reportType.equals(
                    REPORT_TYPE_EOW_SUPERVISOR_SHIFT_REPORT, ignoreCase = true
                )
            ) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong =
                    sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val supervisorReportRequest = SupervisorReportRequest()
                supervisorReportRequest.form = "Supervisor Report"

                val locationDetail = LocationDetailsSupervisor()
                locationDetail.latitude = mLat
                locationDetail.longitude = mLong
                locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
                locationDetail.street = ""//mAutoComTextViewStreet.text.toString()
                supervisorReportRequest.locationDetailsSupervisor = locationDetail


                val officerDetails = OfficerDetailsSupervisor()
                officerDetails.officerName =
                    (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails.shiftId =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails.beat = mWelcomeFormData?.officerBeat
                supervisorReportRequest.officerDetailsSupervisor = officerDetails

                val details = DetailsSupervisor()
//                details.deviceId = "remove"//mAutoComTextViewDeviceId.text.toString()
//                details.date = "remove"//mAutoComTextViewHandheldUnitNo.text.toString()
//                details.dutyHours = "remove"//mAutoComTextViewTriedToRestartHandHeld.text.toString().equals("YES", ignoreCase = true)
//                details.officer = "remove"//mAutoComTextViewPrintingCorrectly.text.toString().equals("YES", ignoreCase = true)
                details.lunchTaken = (mAutoComTextViewLunchTaken?.tag.toString())
                details.first10MinBreak = (mAutoComTextViewFirst10MinBreak?.tag.toString())
                details.second10MinBreak = (mAutoComTextViewSecond10MinBreak?.tag.toString())
//                details.unitNo = "remove"//mAutoComTextViewDescribeHandHeldMalfunctionInDetail.text.toString()
//                details.assignedArea = "remove"
//                details.ossiDeviceNo = "remove"
//                details.handheldUnitNo = "remove"
                details.citationsIssued = mAutoComTextViewCitationsIssued?.text.toString()
//                details.specialEnforcementRequest = "remove"//mAutoComTextViewCitationsIssued.text.toString()
                details.shiftSummaryComments = mAutoComTextViewComments?.text.toString()
                details.warningsIssued = mAutoComTextViewWarningsIssued?.text.toString()
                details.warningsIssued = mAutoComTextViewWarningsIssued?.text.toString()
                details.residentComplaints = mAutoComTextViewResidentComplaints?.text.toString()
                details.complaintsTowardsOfficers =
                    mAutoComTextViewComplaintsTowardsOfficer?.text.toString()
                details.totalEnforcementPersonnel =
                    mAutoComTextViewTotalEnforcementPersonnel?.text.toString()
//                details.status = "remove"//mAutoComTextViewStaus.text.toString()
                supervisorReportRequest.detailsSupervisor = details
                allReportsScreenViewModel.callSupervisorReportAPI(supervisorReportRequest)

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "---------EOW Supervisor Shift Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                supervisorReportRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (reportType.equals(
                    REPORT_TYPE_SPECIAL_ASSIGNMENT_REPORT, ignoreCase = true
                )
            ) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong =
                    sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val supervisorReportRequest = SpecialAssignementRequest()
                supervisorReportRequest.form = REPORT_TYPE_SPECIAL_ASSIGNMENT_REPORT

                val locationDetail = LocationDetailsSpecial()
                locationDetail.latitude = mLat
                locationDetail.longitude = mLong
                locationDetail.block = mAutoComTextViewBlock?.text.toString()
                locationDetail.street = mAutoComTextViewStreet?.text.toString()
                locationDetail.direction = mAutoComTextViewDirection?.text.toString()
                supervisorReportRequest.locationDetails = locationDetail


                val officerDetails = OfficerDetailsSpecial()
                officerDetails.officerName =
                    (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails.shiftId =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails.beat = mWelcomeFormData?.officerBeat
                supervisorReportRequest.officerDetails = officerDetails

                val details = DetailsSpecial()
                details.lunchTaken =
                    ""//AppUtils.timeFormatForReport(mAutoComTextViewLunchTaken.tag.toString())
                details.citationsIssued = mAutoComTextViewCitationsIssued?.text.toString()
                details.shiftSummaryComments = mAutoComTextViewComments?.text.toString()
                details.comments = mAutoComTextViewComments?.text.toString()
                details.warningsIssued = mAutoComTextViewWarningsIssued?.text.toString()
                details.warningsIssued = mAutoComTextViewWarningsIssued?.text.toString()
//                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)) {
//                    details.timeMarkedAt = mAutoComTextViewTimeMark.text.toString()
//                }else {
                details.timeMarkedAt =
                    if (mAutoComTextViewTimeMark?.tag != null) mAutoComTextViewTimeMark?.tag.toString() else ""//mAutoComTextViewResidentComplaints.text.toString()
//                }
                details.timeMarkedAt2 =
                    if (mAutoComTextViewTimeMark2?.tag != null) mAutoComTextViewTimeMark2?.tag.toString() else ""//mAutoComTextViewResidentComplaints.text.toString()
                details.timeMarkedAt3 =
                    if (mAutoComTextViewTimeMark3?.tag != null) mAutoComTextViewTimeMark3?.tag.toString() else ""//mAutoComTextViewResidentComplaints.text.toString()
                details.timeMarkedAt4 =
                    if (mAutoComTextViewTimeMark4?.tag != null) mAutoComTextViewTimeMark4?.tag.toString() else ""//mAutoComTextViewResidentComplaints.text.toString()
                details.vehiclesMarked = mAutoComTextViewVehicleMark?.text.toString()
                details.violationDescriptions = violationDescription
                supervisorReportRequest.details = details
                allReportsScreenViewModel.callSpecialAssignmentReportAPI(supervisorReportRequest)

                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "---------Special Assignment Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                supervisorReportRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (reportType.equals(
                    REPORT_TYPE_PAY_LOT_INSPECTION_REPORT, ignoreCase = true
                )
            ) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong =
                    sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val lotInspectionRequest = LotInspectionRequest()
                lotInspectionRequest.form = reportType

                val locationDetail = LocationDetailsLotInspection()
                locationDetail.latitude = mLat
                locationDetail.longitude = mLong
//                locationDetail.block = mAutoComTextViewBlock.text.toString()
//                locationDetail.street = mAutoComTextViewStreet.text.toString()
//                locationDetail.direction = mAutoComTextViewDirection.text.toString()
                locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
                lotInspectionRequest.locationDetails = locationDetail


                val officerDetails = OfficerDetailsLotInspection()
                officerDetails.officerName =
                    (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails.shiftId =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails.beat = mWelcomeFormData?.officerBeat
                lotInspectionRequest.officerDetails = officerDetails

                val details = DetailsLotInspection()
                details.reportNumber = mEditTextReportNumber.text.toString()
                details.zone = mAutoComTextViewPBCZone?.text.toString()
                details.allSignsReported = mAutoComTextViewInternetConnectivity?.text.toString()
                    .equals("YES", ignoreCase = true)
                details.allPaystationsReported = mAutoComTextViewMeterLabelVisible?.text.toString()
                    .equals("YES", ignoreCase = true)
                details.anyHomelessReported = mAutoComTextViewDigitalDisplayVisible?.text.toString()
                    .equals("YES", ignoreCase = true)
                details.anySafetyIssuesReported =
                    mAutoComTextViewCoinJam?.text.toString().equals("YES", ignoreCase = true)
                details.line = mAutoComTextViewLine?.text.toString()

                lotInspectionRequest.details = details
                allReportsScreenViewModel.callLotInspectionReportAPI(lotInspectionRequest)
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "---------Lot Inspection  Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                lotInspectionRequest
                            )
                        )

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (reportType.equals(REPORT_TYPE_PAY_LOT_REPORT, ignoreCase = true)) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong =
                    sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val nflRequest = LotCountVioRateRequest()
                nflRequest.form = reportType

                val locationDetail = LocationDetailsHard()
                locationDetail.latitude = mLat
                locationDetail.longitude = mLong
//            locationDetail.block = mAutoComTextViewBlock.text.toString()
//            locationDetail.street = mAutoComTextViewStreet.text.toString()
                locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
                nflRequest.locationDetails = locationDetail


                val officerDetails = OfficerDetailsHard()
                officerDetails.officerName =
                    (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails.shiftId =
                    sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails.beat = mWelcomeFormData?.officerBeat
                nflRequest.officerDetails = officerDetails

                val details = DetailsHard()
                details.reportNumber = mEditTextReportNumber.text.toString()
                details.zone = mAutoComTextViewPBCZone?.text.toString()
                details.meter =
                    if (mAutoComTextViewMeterNo != null) mAutoComTextViewMeterNo?.text.toString() else " "
                details.carSpace = (mAutoComTextViewField1?.text.toString())
                details.carsCounted = (mAutoComTextViewCarCount?.text.toString())
                details.emptySpaces = (mAutoComTextViewEmptySpace?.text.toString())
                details.numberOfViolatingVehicles =
                    (mAutoComTextViewNumberOfViolatingVehicles?.text.toString())
                details.violationRate = mAutoComTextViewViolationRate?.text.toString()
                details.comments = mAutoComTextViewComments?.text.toString()
                details.line = mAutoComTextViewLine?.text.toString()

                try {
//                    details.photo1 = mImagesLinks[0]
//                    details.photo2 = mImagesLinks[1]
//                    details.photo3 = mImagesLinks[2]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                nflRequest.details = details
                allReportsScreenViewModel.callLotCountVioRateReportAPI(nflRequest)
                try {
                    if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(), "---------Lot Count Vio Rate  Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            requireContext(),
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                nflRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (reportType.equals(
                    REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_PAY_SIGNAGE_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_TOW_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_PAY_STATION_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_NFL_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_HARD_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_AFTER_SEVEN_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_PAY_HOMELESS_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_WORK_ORDER_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_PAY_SAFETY_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_PAY_TRASH_REPORT, ignoreCase = true
                ) || reportType.equals(REPORT_TYPE_SIGN_OFF_REPORT, ignoreCase = true)
            ) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (requireContext().isInternetAvailable()) {
                        callBulkImageUploadAPI(
                            uploadType = API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                            bannerList = bannerList
                        )
                    } else {
                        LogUtil.printToastMSG(
                            requireContext(), getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)
            }
        } else {
            LogUtil.printToastMSG(
                requireContext(), getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun sendRequestWithImage() {
        if (reportType.equals(REPORT_TYPE_CURB_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val curbRequest = CurbRequest()
            curbRequest.form = reportType

            val locationDetail = LocationDetailsCurb()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = mAutoComTextViewBlock?.text.toString()
            locationDetail.street = mAutoComTextViewStreet?.text.toString()
            locationDetail.direction = mAutoComTextViewDirection?.text.toString()
            curbRequest.locationDetailsCurb = locationDetail

            val officerDetails = OfficerDetailsCurb()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            curbRequest.officerDetailsCurb = officerDetails

            val details = DetailsCurb()
            details.deviceId = ""//mAutoComTextViewDeviceId.text.toString()
            details.enforceable =
                mAutoComTextViewEnforceable?.text.toString().equals("YES", ignoreCase = true)
            details.curbColor = mAutoComTextViewCurbColor?.text.toString()
            details.comments = mAutoComTextViewComments?.text.toString()
            details.status = ""//mAutoComTextViewStaus.text.toString()

            details.pictureOfCurb = mImagesLinks[0]
            curbRequest.detailsCurb = details

            allReportsScreenViewModel.callCurbReportAPI(curbRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Curb Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(curbRequest)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_SIGN_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val signReportRequest = SignReportRequest()
            signReportRequest.form = reportType

            val locationDetail = LocationDetailsSign()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = mAutoComTextViewBlock?.text.toString()
            locationDetail.street = mAutoComTextViewStreet?.text.toString()
            locationDetail.direction = mAutoComTextViewDirection?.text.toString()
            signReportRequest.locationDetailsSign = locationDetail


            val officerDetails = OfficerDetailsSign()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            signReportRequest.officerDetailsSign = officerDetails

            val details = DetailsSign()
            details.deviceId = ""//mAutoComTextViewDeviceId.text.toString()
            details.enforceable =
                mAutoComTextViewEnforceable?.text.toString().equals("YES", ignoreCase = true)
            details.graffiti =
                mAutoComTextViewGraffiti?.text.toString().equals("YES", ignoreCase = true)
            details.missingSign =
                mAutoComTextViewMissingSign?.text.toString().equals("YES", ignoreCase = true)
            details.comments = mAutoComTextViewComments?.text.toString()
            details.status = ""//mAutoComTextViewStaus.text.toString()
            details.picture = mImagesLinks[0]
            signReportRequest.detailsSign = details
            allReportsScreenViewModel.callSignReportAPI(signReportRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Sign Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            signReportRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_VEHICLE_INSPECTION_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val vehicleInspectionRequest = VehicleInspectionRequest()
            vehicleInspectionRequest.form = reportType

            val locationDetail = LocationDetailsVehicleInspection()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
            locationDetail.street = ""//mAutoComTextViewStreet.text.toString()
            vehicleInspectionRequest.locationDetailsVehicleInspection = locationDetail


            val officerDetails = OfficerDetailsVehicleInspection()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            vehicleInspectionRequest.officerDetailsVehicleInspection = officerDetails

            val details = DetailsVehicleInspection()
            details.deviceId = ""//mAutoComTextViewDeviceId.text.toString()
            details.officer = ""//mAutoComTextViewOfficer.text.toString()
            details.vehicle = mAutoComTextViewVehicle?.text.toString()
            details.startingMileage = mAutoComTextViewStartingMileage?.text.toString()
            details.gasLevel = mAutoComTextViewGasLevel?.text.toString()
            details.lightBar = mAutoComTextViewLightBar?.text.toString()
            details.dashboardIndications = mAutoComTextViewDashBoardIndications?.text.toString()
            details.seatBeltOperational = mAutoComTextViewSeatBeltOperational?.text.toString()
            details.brakes =
                if (mAutoComTextViewBrakes != null) mAutoComTextViewBrakes?.text.toString() else ""
            details.brakeLights =
                if (mAutoComTextViewBrakeLights != null) mAutoComTextViewBrakeLights?.text.toString() else ""
            details.headlights = mAutoComTextViewHeadLights?.text.toString()
            details.turnSignals = mAutoComTextViewTurnSignals?.text.toString()
            details.turnSignals = mAutoComTextViewTurnSignals?.text.toString()
            details.steeringWheelOperational =
                if (mAutoComTextViewSteeringWheelOperational != null) mAutoComTextViewSteeringWheelOperational?.text.toString() else ""
            details.windshieldVisibility = mAutoComTextViewWindshieldVisibility?.text.toString()
            details.sideAndRearViewMirrorsOperational =
                mAutoComTextViewSideAndRearViewMirrorsOperational?.text.toString()
            details.windshieldWipersOperational =
                mAutoComTextViewWindshieldWipersOperational?.text.toString()
            details.vehicleRegistrationAndInsurance =
                if (mAutoComTextViewVehicleRegistrationAndInsurance != null) mAutoComTextViewVehicleRegistrationAndInsurance?.text.toString()
                    .equals("YES", ignoreCase = true) else false
            details.conesSixPerVehicle =
                if (mAutoComTextViewConesSixPerVehicle != null) mAutoComTextViewConesSixPerVehicle?.text.toString() else ""
            details.firstAidKit =
                if (mAutoComTextViewFirstAidKit != null) mAutoComTextViewFirstAidKit?.text.toString()
                    .equals("YES", ignoreCase = true) else false
            details.horn = mAutoComTextViewHorn?.text.toString()
            details.interiorCleanliness = mAutoComTextViewInteriorCleanliness?.text.toString()
            details.exteriorCleanliness = mAutoComTextViewExteriorCleanliness?.text.toString()
            details.lprLensFreeOfDebris = mAutoComTextViewLprLensFreeOfDebris?.text.toString()
            details.visibleLeaks =
                mAutoComTextViewVisibleLeaks?.text.toString().equals("YES", ignoreCase = true)
            details.tiresVisualInspection = mAutoComTextViewTiresVisualInspection?.text.toString()
            details.comments = mAutoComTextViewComments?.text.toString()
            details.status = ""//mAutoComTextViewStaus.text.toString()
            try {
                details.frontPhoto = mImagesLinks[0]
                details.passengerSidePhoto = mImagesLinks[1]
                details.driverSidePhoto = mImagesLinks[2]
                details.rearPhoto = mImagesLinks[3]
                details.signature = mImagesLinks[4]
                details.image_1 = mImagesLinks[5]
                details.image_2 = mImagesLinks[6]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            vehicleInspectionRequest.detailsVehicleInspection = details
            allReportsScreenViewModel.callVehicleInspectionReportAPI(vehicleInspectionRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Vehicle Inspection Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            vehicleInspectionRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(
                REPORT_TYPE_72_HOURS_MARKED_VEHICLE_REPORT, ignoreCase = true
            )
        ) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val hourMarkedVehiclesRequest = HourMarkedVehiclesRequest()
            hourMarkedVehiclesRequest.form = reportType

            val locationDetail = LocationDetailsHourMarkedVehicle()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = mAutoComTextViewBlock?.text.toString()
            locationDetail.street = mAutoComTextViewStreet?.text.toString()
            hourMarkedVehiclesRequest.locationDetailsHourMarkedVehicle = locationDetail


            val officerDetails = OfficerDetailsHourMarkedVehicle()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            hourMarkedVehiclesRequest.officerDetailsHourMarkedVehicle = officerDetails

            val details = DetailsHourMarkedVehicle()
            details.deviceId = mAutoComTextViewDeviceId?.text.toString()
            details.comments = mAutoComTextViewComments?.text.toString()
            details.status = mAutoComTextViewStaus?.text.toString()
            try {
                details.redStationCopyAndParkingWarning = mImagesLinks[0]
                details.warningCitation = mImagesLinks[1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            hourMarkedVehiclesRequest.detailsHourMarkedVehicle = details
            allReportsScreenViewModel.callSeventyTwoHourMarkedVehiclesReportAPI(
                hourMarkedVehiclesRequest
            )
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------72 Hour Marked Vehicles Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            hourMarkedVehiclesRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_BIKE_INSPECTION_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val bikeInspectionsRequest = BikeInspectionsRequest()
            bikeInspectionsRequest.form = reportType

            val locationDetail = LocationDetailsBikeInspections()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
            locationDetail.street = ""//mAutoComTextViewStreet.text.toString()
            bikeInspectionsRequest.locationDetailsBikeInspections = locationDetail


            val officerDetails = OfficerDetailsBikeInspections()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            bikeInspectionsRequest.officerDetailsBikeInspections = officerDetails

            val details = DetailsBikeInspections()
            details.deviceId = ""//mAutoComTextViewDeviceId.text.toString()
            details.officer = ""//mAutoComTextViewOfficer.text.toString()
            details.tirePressure = mAutoComTextViewTirePressure?.text.toString()
            details.assignedBike = mAutoComTextViewAssignedBike?.text.toString()
            details.breaksRotors = mAutoComTextViewBreaksRotors?.text.toString()
            details.lightsAndReflectors = mAutoComTextViewLightsAndReflectors?.text.toString()
            details.chainCrank = mAutoComTextViewChainCrank?.text.toString()
            details.batteryFreeOfDebris = mAutoComTextViewBatteryFreeOfDebris?.text.toString()
            details.flatPack = mAutoComTextViewFlatPack?.text.toString()
            details.firstAidKit = mAutoComTextViewFirstAidKit?.text.toString()
            details.helmetVisualInspection = mAutoComTextViewTiresVisualInspection?.text.toString()
            details.bikeGlasses = mAutoComTextViewBikeGlassess?.text.toString()
            details.batterCharge = mAutoComTextViewBatteryCharge?.text.toString()
            details.gloveVisualInspection = mAutoComTextViewGloveVisualInspection?.text.toString()
            details.status = ""//mAutoComTextViewStaus.text.toString()
            try {
                details.font = mImagesLinks[0]
                details.back = mImagesLinks[1]
                details.leftSide = mImagesLinks[2]
                details.rightSide = mImagesLinks[3]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bikeInspectionsRequest.detailsBikeInspections = details
            allReportsScreenViewModel.callBikeInspectionReportAPI(bikeInspectionsRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Bike Inspection Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            bikeInspectionsRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_SIGN_OFF_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val signOffReportRequest = SignOffReportRequest()
            signOffReportRequest.form = reportType

            val locationDetail = LocationDetailsSignOff()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
            locationDetail.street = ""//mAutoComTextViewStreet.text.toString()
            signOffReportRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsSignOff()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            signOffReportRequest.officerDetails = officerDetails

            val details = DetailsSignOff()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.vehicleStoredAt = mAutoComTextViewVehicleStoredAt?.text.toString()
            details.vehicleRelocated =
                mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.text.toString()
                    .equals("YES")
            details.towFileNumber = mAutoComTextViewTowFileNumber?.text.toString()
            try {
                details.officerPhoto = mImagesLinks[0]
                details.towDriver = mImagesLinks[1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            signOffReportRequest.details = details
            allReportsScreenViewModel.callSignOffReportAPI(signOffReportRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Sign Off Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            signOffReportRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val noticeToTowRequest = NoticeToTowRequest()
            noticeToTowRequest.form = "72 Hours Tow Notice"

            val locationDetail = LocationDetailsNoticeToTow()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
            locationDetail.street = ""//mAutoComTextViewStreet.text.toString()
            noticeToTowRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsNoticeToTow()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            noticeToTowRequest.officerDetails = officerDetails

            val details = DetailsNoticeToTow()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.meter = mAutoComTextViewMeterNo?.text.toString()
            details.lot = ""//mAutoComTextViewLotArea.text.toString()
            details.vehicleLicensePlate = mAutoComTextViewLicensePlate?.text.toString()
            details.vehicleVin = mAutoComTextViewVin?.text.toString()
            details.state = mAutoComTextViewState?.text.toString()
            details.vehicleMake = mAutoComTextViewMake?.text.toString()
            details.vehicleModel = mAutoComTextViewModel?.text.toString()
            details.vehicleColor = mAutoComTextViewColor?.text.toString()
            details.firstMarkTimestamp = mAutoComTextViewTimeMark?.text.toString()
            details.citationIssueTimestamp = mAutoComTextViewCitationsIssued?.text.toString()
            details.scheduledTowDate = mAutoComTextViewDate?.text.toString()
            details.line = mAutoComTextViewLine?.text.toString()

            try {
                details.driverSideTireAirvalve = mImagesLinks[0]
                details.photos = mImagesLinks
            } catch (e: Exception) {
                e.printStackTrace()
            }
            noticeToTowRequest.details = details
            allReportsScreenViewModel.callNoticeToTowReportAPI(noticeToTowRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------72hrs Notice To Tow Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            noticeToTowRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_TOW_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val towReportRequest = TowReportRequest()
            towReportRequest.form = reportType

            val locationDetail = LocationDetailsTowReport()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = ""//mAutoComTextViewBlock.text.toString()
            locationDetail.street = ""//mAutoComTextViewStreet.text.toString()
            towReportRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsTowReport()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            towReportRequest.officerDetails = officerDetails

            val details = DetailsTowReport()
            details.vehicleLicensePlate = mAutoComTextViewLicensePlate?.text.toString()
            details.vehicleVin = mAutoComTextViewVin?.text.toString()
            details.state = mAutoComTextViewState?.text.toString()
            details.vehicleMake = mAutoComTextViewMake?.text.toString()
            details.vehicleModel = mAutoComTextViewModel?.text.toString()
            details.vehicleColor = mAutoComTextViewColor?.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.meter = mAutoComTextViewMeterNo?.text.toString()
            details.lot = ""//mAutoComTextViewLotArea.text.toString()
            details.towReason = mAutoComTextViewReasonForTow?.text.toString()
            details.towNoticeDate = mAutoComTextViewDate?.text.toString()
            details.visibleInteriorItems = mAutoComTextViewInteriorCleanliness?.text.toString()
            details.trailerAttached = mAutoComTextViewOverHeating?.text.toString() == "YES"
            details.requestTo = mAutoComTextViewSpecialEnforcementRequest?.text.toString()
            details.driverSideTireAirValve = mAutoComTextViewDriverSideComment?.text.toString()
            details.garageClearance = mAutoComTextViewGarageClearance?.text.toString()
            details.fileNumber = mAutoComTextViewFileNumber?.text.toString()
            details.roAddress = mAutoComTextViewRo?.text.toString()
            details.vehicleWithinTheLot = mAutoComTextViewVehicleWithInLot?.text.toString()
            details.driverSideComments = mAutoComTextViewDriverSideComment?.text.toString()
            details.passengerSideComments = mAutoComTextViewPassengerSideComment?.text.toString()
            details.frontSideComments = mAutoComTextViewFrontSideComment?.text.toString()
            details.rearSideComments = mAutoComTextViewRearSideComment?.text.toString()
            details.trailerComments = mAutoComTextViewTrailerComment?.text.toString()
            details.vehicleCondition = mAutoComTextViewVehicleCondition?.text.toString()
            details.officerPhoneNumber = mAutoComTextViewOfficerPhoneNumber?.text.toString()
            details.generalComments = mAutoComTextViewComments?.text.toString()
            details.towingOfficer = mAutoComTextViewTowingOfficer?.text.toString()
            details.line = mAutoComTextViewLine?.text.toString()

            try {
                details.platePhoto = mImagesLinks[0]
                details.curbPhoto = mImagesLinks[1]
                details.driverSidePhoto = mImagesLinks[2]
                details.frontPhoto = mImagesLinks[3]
                details.passengerSidePhoto = mImagesLinks[4]
                details.rearPhoto = mImagesLinks[5]
                details.interiorPhotoOne = mImagesLinks[6]
                details.interiorPhotoTwo = mImagesLinks[7]
                details.trailerPhotoOne = mImagesLinks[8]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            towReportRequest.details = details
            allReportsScreenViewModel.callTowReportAPI(towReportRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Tow Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            towReportRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(
                REPORT_TYPE_NFL_REPORT, ignoreCase = true
            ) || reportType.equals(REPORT_TYPE_HARD_REPORT, ignoreCase = true)
        ) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val nflRequest = NFLRequest()
            nflRequest.form = reportType

            val locationDetail = LocationDetailsNfl()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong

            locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
            nflRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsNfl()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            nflRequest.officerDetails = officerDetails

            val details = DetailsNfl()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.meter =
                if (mAutoComTextViewMeterNo != null) mAutoComTextViewMeterNo?.text.toString() else " "
            details.carsCounted = (mAutoComTextViewCarCount?.text.toString()).toInt()
            details.emptySpaces = (mAutoComTextViewEmptySpace?.text.toString()).toInt()
            details.numberOfViolatingVehicles =
                (mAutoComTextViewNumberOfViolatingVehicles?.text.toString()).toInt()
            details.violationRate = mAutoComTextViewViolationRate?.text.toString()
            details.comments = mAutoComTextViewComments?.text.toString()
            if (reportType.equals(REPORT_TYPE_NFL_REPORT, ignoreCase = true)) {
                details.line = mAutoComTextViewLine?.text.toString()
            }
            if (reportType.equals(REPORT_TYPE_HARD_REPORT, ignoreCase = true)) {
                details.eventName = mAutoComTextViewField1?.text.toString()
            }

            try {
                details.photo1 = mImagesLinks[0]
                details.photo2 = mImagesLinks[1]
                details.photo3 = mImagesLinks[2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            nflRequest.details = details
            if (reportType.equals(REPORT_TYPE_NFL_REPORT, ignoreCase = true)) {
                allReportsScreenViewModel.callNflReportServiceAPI(nflRequest)
            } else {
//                nflRequest.form = "Intuit Dome Report"
                allReportsScreenViewModel.callHardSummerFestivalReportAPI(nflRequest)
            }
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------" + reportType + " Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(nflRequest)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_AFTER_SEVEN_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val afterRequest = AfterSevenPMRequest()
            afterRequest.form = reportType

            val locationDetail = LocationDetailsAfter()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong

            locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
            afterRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsAfter()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            afterRequest.officerDetails = officerDetails

            val details = DetailsAfter()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.vendorCount = (mAutoComTextViewCarCount?.text.toString()).toInt()
            details.vendorLocations = (mAutoComTextViewLotArea?.text.toString())
            details.securityObservation = (mAutoComTextViewSecurityObservation?.text.toString())
            details.comments = mAutoComTextViewComments?.text.toString()

            try {
                details.photo1 = mImagesLinks[0]
                details.photo2 = mImagesLinks[1]
                details.photo3 = mImagesLinks[2]
                details.photo4 = mImagesLinks[3]
                details.photo5 = mImagesLinks[4]
                details.photo6 = mImagesLinks[5]
                details.photo7 = mImagesLinks[6]
                details.signature = mImagesLinks[7]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            afterRequest.details = details
            allReportsScreenViewModel.callAfterSevenPmReportAPI(afterRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------PCH Daily Updates Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(afterRequest)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_PAY_STATION_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val afterRequest = PayStationRequest()
            afterRequest.form = reportType

            val locationDetail = LocationDetailsPayStation()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong

            locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
            afterRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsPayStation()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            afterRequest.officerDetails = officerDetails

            val details = DetailsPayStation()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.meter =
                if (mAutoComTextViewMeterNo != null) mAutoComTextViewMeterNo?.text.toString() else " "
            details.machineNumber1 = (mAutoComTextViewField1?.text.toString()).toInt()
            details.machineNumber2 = if (mAutoComTextViewField2?.text?.isNotEmpty()
                    .nullSafety()
            ) (mAutoComTextViewField2?.text.toString()).toInt() else 0
            details.machineNumber3 = if (mAutoComTextViewField2?.text?.isNotEmpty()
                    .nullSafety()
            ) (mAutoComTextViewField3?.text.toString()).toInt() else 0
            details.description1 = (mAutoComTextViewField4?.text.toString())
            details.description2 = mAutoComTextViewField5?.text.toString()
            details.description3 = mAutoComTextViewField6?.text.toString()
            details.comments1 = mAutoComTextViewComments?.text.toString()
            details.comments2 = mAutoComTextViewComment2?.text.toString()
            details.comments3 = mAutoComTextViewComment3?.text.toString()
            details.line = mAutoComTextViewLine?.text.toString()

            try {
                details.photo1 = mImagesLinks[0]
                details.photo2 = mImagesLinks[1]
                details.photo3 = mImagesLinks[2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            afterRequest.details = details
            allReportsScreenViewModel.callPayStationReportAPI(afterRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Pay Station Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(afterRequest)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_PAY_SIGNAGE_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val signageRequest = SignageReportRequest()
            signageRequest.form = reportType

            val locationDetail = LocationDetailsSignage()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong

            locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
            signageRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsSignage()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            signageRequest.officerDetails = officerDetails

            val details = DetailsSignage()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.meter =
                if (mAutoComTextViewMeterNo != null) mAutoComTextViewMeterNo?.text.toString() else " "
            details.description1 = (mAutoComTextViewField1?.text.toString())
            details.description2 = (mAutoComTextViewField2?.text.toString())
            details.description3 = (mAutoComTextViewField3?.text.toString())
            details.comments = mAutoComTextViewComments?.text.toString()
            details.line = mAutoComTextViewLine?.text.toString()

            try {
                details.photo1 = mImagesLinks[0]
                details.photo2 = mImagesLinks[1]
                details.photo3 = mImagesLinks[2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            signageRequest.details = details
            allReportsScreenViewModel.callSignageReportAPI(signageRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Signage Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            signageRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_PAY_HOMELESS_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val homelessRequest = HomelessRequest()
            homelessRequest.form = reportType

            val locationDetail = LocationDetailsHomeless()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong

            locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
            locationDetail.meter = mAutoComTextViewMeterNo?.text.toString()
            homelessRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsHomeless()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            homelessRequest.officerDetails = officerDetails

            val details = DetailsHomeless()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.meter =
                if (mAutoComTextViewMeterNo != null) mAutoComTextViewMeterNo?.text.toString() else " "
            details.description = (mAutoComTextViewField1?.text.toString())
            details.comments = mAutoComTextViewComments?.text.toString()
            details.line = mAutoComTextViewLine?.text.toString()

            try {
                details.photo1 = mImagesLinks[0]
                details.photo2 = mImagesLinks[1]
                details.photo3 = mImagesLinks[2]
                details.photo4 = mImagesLinks[3]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            homelessRequest.details = details
            allReportsScreenViewModel.callHomelessReportAPI(homelessRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Homeless Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            homelessRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_WORK_ORDER_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val workOrderRequest = WorkOrderRequest()
            workOrderRequest.form = reportType

            val locationDetail = LocationDetailsWorkOrder()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.block = mAutoComTextViewBlock?.text.toString()
            locationDetail.street = mAutoComTextViewStreet?.text.toString()
            locationDetail.direction = mAutoComTextViewDirection?.text.toString()
//            locationDetail.zone = mAutoComTextViewPBCZone.text.toString()
            workOrderRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsWorkOrder()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            workOrderRequest.officerDetails = officerDetails

            val details = DetailsWorkOrder()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.sign = mAutoComTextViewField1?.text.toString()
            details.actionsRequired = mAutoComTextViewField2?.text.toString()
            details.comments = mAutoComTextViewComments?.text.toString()

            try {
                details.photo = mImagesLinks[0]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            workOrderRequest.details = details
            allReportsScreenViewModel.callWorkOrderReportAPI(workOrderRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Work Order Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            workOrderRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(
                REPORT_TYPE_PAY_SAFETY_REPORT, ignoreCase = true
            )
        ) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val safetyIssueRequest = SafetyIssueRequest()
            safetyIssueRequest.form = reportType

            val locationDetail = LocationDetailsSafetyIssue()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
            safetyIssueRequest.locationDetails = locationDetail

            val officerDetails = OfficerDetailsSafetyIssue()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            safetyIssueRequest.officerDetails = officerDetails

            val details = DetailsSafetyIssue()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.safetyIssue = mAutoComTextViewSafetyIssue?.text.toString()
            details.description = mAutoComTextViewField2?.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.meter =
                if (mAutoComTextViewMeterNo != null) mAutoComTextViewMeterNo?.text.toString() else " "
            details.line = mAutoComTextViewLine?.text.toString()
//            details.comments = mAutoComTextViewComments.text.toString()

            try {
                details.photo1 = mImagesLinks[0]
                details.photo2 = mImagesLinks[1]
                details.photo3 = mImagesLinks[2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            safetyIssueRequest.details = details
            allReportsScreenViewModel.callSafetyIssueReportAPI(safetyIssueRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "---------Safety Report Immediate Attention Required Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            safetyIssueRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (reportType.equals(REPORT_TYPE_PAY_TRASH_REPORT, ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val trashLotRequest = TrashLotRequest()
            trashLotRequest.form = reportType

            val locationDetail = LocationDetailsTrashLot()
            locationDetail.latitude = mLat
            locationDetail.longitude = mLong
            locationDetail.zone = mAutoComTextViewPBCZone?.text.toString()
            trashLotRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsTrashLot()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            trashLotRequest.officerDetails = officerDetails

            val details = DetailsTrashLot()
            details.reportNumber = mEditTextReportNumber.text.toString()
            details.requiredServices = mAutoComTextViewRequiredServices?.text.toString()
            details.description = mAutoComTextViewField2?.text.toString()
            details.zone = mAutoComTextViewPBCZone?.text.toString()
            details.line = mAutoComTextViewLine?.text.toString()
//            details.comments = mAutoComTextViewComments.text.toString()

            try {
                details.photo1 = mImagesLinks[0]
                details.photo2 = mImagesLinks[1]
                details.photo3 = mImagesLinks[2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            trashLotRequest.details = details
            allReportsScreenViewModel.callTrashLotReportAPI(trashLotRequest)
            try {
                if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(), "---------Trash Lot Maintenance Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        requireContext(),
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            trashLotRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun callBulkImageUploadAPI(
        uploadType: String, bannerList: MutableList<CitationImagesModel?>?
    ) {
        val uploadType = uploadType.toRequestBody("text/plain".toMediaTypeOrNull())
        val mRequestBodyImages = ObjectMapperProvider.instance.writeValueAsString(
            MultipartUtils.createImagesNameList(
                requireContext(),
                "ReportIssue",
                true,
                "---------Report Request Image Upload--------",
                bannerList
            )
        ).toRequestBody("text/plain".toMediaTypeOrNull())

        allReportsScreenViewModel.callUploadAllImagesInBulkAPI(
            data = mRequestBodyImages,
            uploadType = uploadType,
            files = MultipartUtils.getImageMultipart(isStatusCheck = true, imageList = bannerList)
        )
    }

    fun isFormValid(reportType: String): Boolean {
        try {
            if (reportType.equals(REPORT_TYPE_BROKEN_METER_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    textInputLayoutBlock?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBlock?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    textInputLayoutStreet?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutStreet?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    textInputLayoutDeviceId?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDeviceId?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    textInputLayoutMeterNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterNo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterType?.text.toString().trim())) {
                    textInputLayoutMeterType?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterType?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterLabelVisible?.text.toString().trim())) {
                    textInputLayoutMeterLableVisible?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterLableVisible?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewDigitalDisplayVisible?.text.toString().trim()
                    )
                ) {
                    textInputLayoutDigitalDisplayVisible?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDigitalDisplayVisible?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewDigitalDisplayVisible?.text.toString().trim()
                    )
                ) {
                    textInputLayoutDigitalDisplayVisible?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDigitalDisplayVisible?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCoinJam?.text.toString().trim())) {
                    textInputLayoutCoinJam?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutCoinJam?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCreditCardJam?.text.toString().trim())) {
                    textInputLayoutCreditCardJam?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutCreditCardJam?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(REPORT_TYPE_CURB_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    textInputLayoutBlock?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBlock?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    textInputLayoutStreet?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutStreet?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewEnforceable?.text.toString().trim())) {
                    textInputLayoutEnforceable?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutEnforceable?.hint?.toString())

                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCurbColor?.text.toString().trim())) {
                    textInputLayoutCurbColor?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutCurbColor?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(REPORT_TYPE_EOW_OFFICER_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    textInputLayoutDeviceId?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDeviceId?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewDutyHours?.text.toString().trim())) {
                    mInputLayoutDutyHours?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutDutyHours?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewLunchTaken?.text.toString().trim())) {
                    textInputLayoutLunchTaken?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutLunchTaken?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFirst10MinBreak?.text.toString().trim())) {
                    textInputLayoutFirst10MinBreak?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutFirst10MinBreak?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSecond10MinBreak?.text.toString().trim())) {
                    textInputLayoutSecond10MinBreak?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSecond10MinBreak?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewUnitNo?.text.toString().trim())) {
                    textInputLayoutUnitNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutUnitNo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewAssignedArea?.text.toString().trim())) {
                    textInputLayoutAssignedArea?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutAssignedArea?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    mInputLayoutCitationsIssued?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutCitationsIssued?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewSpecialEnforcementRequest?.text.toString().trim()
                    )
                ) {
                    mInputLayoutSpecialEnforcementRequest?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutSpecialEnforcementRequest?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(REPORT_TYPE_PART_EOW_OFFICER_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    textInputLayoutDeviceId?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDeviceId?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewDutyHours?.text.toString().trim())) {
                    mInputLayoutDutyHours?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutDutyHours?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFirst10MinBreak?.text.toString().trim())) {
                    textInputLayoutFirst10MinBreak?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutFirst10MinBreak?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSecond10MinBreak?.text.toString().trim())) {
                    textInputLayoutSecond10MinBreak?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSecond10MinBreak?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewUnitNo?.text.toString().trim())) {
                    textInputLayoutUnitNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutUnitNo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewAssignedArea?.text.toString().trim())) {
                    textInputLayoutAssignedArea?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutAssignedArea?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    mInputLayoutCitationsIssued?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutCitationsIssued?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewSpecialEnforcementRequest?.text.toString().trim()
                    )
                ) {
                    mInputLayoutSpecialEnforcementRequest?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutSpecialEnforcementRequest?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(
                    REPORT_TYPE_HAND_HELD_MALFUNCTION_REPORT, ignoreCase = true
                )
            ) {
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    textInputLayoutDeviceId?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDeviceId?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewTriedToRestartHandHeld?.text.toString().trim()
                    )
                ) {
                    textInputLayoutTriedToRestartHandheld?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutTriedToRestartHandheld?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewPrintingCorrectly?.text.toString().trim())) {
                    textInputLayoutPrintingCorrectly?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPrintingCorrectly?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOverHeating?.text.toString().trim())) {
                    textInputLayoutOverHeating?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutOverHeating?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOverHeating?.text.toString().trim())) {
                    textInputLayoutOverHeating?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutOverHeating?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBatteryHoldCharge?.text.toString().trim())) {
                    textInputLayoutBatteryHoldCharge?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBatteryHoldCharge?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewInternetConnectivity?.text.toString().trim()
                    )
                ) {
                    textInputLayoutInternetConnectivity?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutInternetConnectivity?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewDescribeHandHeldMalfunctionInDetail?.text.toString().trim()
                    )
                ) {
                    mInputLayoutDescribeHandHeldMalfunctionInDetail?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutDescribeHandHeldMalfunctionInDetail?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    textInputLayoutStatus?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutStatus?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(REPORT_TYPE_SIGN_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    textInputLayoutBlock?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBlock?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    textInputLayoutStreet?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutStreet?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewEnforceable?.text.toString().trim())) {
                    textInputLayoutEnforceable?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutEnforceable?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewGraffiti?.text.toString().trim())) {
                    textInputLayoutGraffiti?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutGraffiti?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMissingSign?.text.toString().trim())) {
                    textInputLayoutMissingSign?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMissingSign?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(
                    REPORT_TYPE_VEHICLE_INSPECTION_REPORT, ignoreCase = true
                )
            ) {
                if (TextUtils.isEmpty(mAutoComTextViewVehicle?.text.toString().trim())) {
                    textInputLayoutVehicle?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutVehicle?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStartingMileage?.text.toString().trim())) {
                    textInputLayoutStartingMileage?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutStartingMileage?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewGasLevel?.text.toString().trim())) {
                    textInputLayoutGasLevel?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutGasLevel?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewLightBar?.text.toString().trim())) {
                    textInputLayoutLightBar?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutLightBar?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewDashBoardIndications?.text.toString().trim()
                    )
                ) {
                    textInputLayoutDashBoardIndications?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDashBoardIndications?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewSeatBeltOperational?.text.toString().trim()
                    )
                ) {
                    textInputLayoutSeatBeltOperational?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSeatBeltOperational?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBrakes?.text.toString().trim())) {
                    textInputLayoutBrakes?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBrakes?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBrakeLights?.text.toString().trim())) {
                    textInputLayoutBrakeLights?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBrakeLights?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewHeadLights?.text.toString().trim())) {
                    textInputLayoutHeadLights?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutHeadLights?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTurnSignals?.text.toString().trim())) {
                    textInputLayoutSignals?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSignals?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewSteeringWheelOperational?.text.toString().trim()
                    )
                ) {
                    textInputLayoutSteeringWheelOperational?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSteeringWheelOperational?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewWindshieldVisibility?.text.toString().trim()
                    )
                ) {
                    textInputLayoutWindshieldVisibility?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutWindshieldVisibility?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewSideAndRearViewMirrorsOperational?.text.toString().trim()
                    )
                ) {
                    textInputLayoutSideAndRearViewMirrorsOperational?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSideAndRearViewMirrorsOperational?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewWindshieldWipersOperational?.text.toString().trim()
                    )
                ) {
                    textInputLayoutWindshieldWipersOperational?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutWindshieldWipersOperational?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewVehicleRegistrationAndInsurance?.text.toString().trim()
                    )
                ) {
                    textInputLayoutVehicleRegistrationAndInsurance?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutVehicleRegistrationAndInsurance?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewConesSixPerVehicle?.text.toString().trim())) {
                    textInputLayoutConesSixPerVehicle?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutConesSixPerVehicle?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFirstAidKit?.text.toString().trim())) {
                    textInputLayoutFirstAidKit?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutFirstAidKit?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewHorn?.text.toString().trim())) {
                    textInputLayoutHorn?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutHorn?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewInteriorCleanliness?.text.toString().trim()
                    )
                ) {
                    mInputLayoutInteriorCleanliness?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutInteriorCleanliness?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewExteriorCleanliness?.text.toString().trim()
                    )
                ) {
                    textInputLayoutExteriorCleanliness?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutExteriorCleanliness?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewLprLensFreeOfDebris?.text.toString().trim()
                    )
                ) {
                    textInputLayoutLprLensFreeOfDebris?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutLprLensFreeOfDebris?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVisibleLeaks?.text.toString().trim())) {
                    textInputLayoutVisibleLeaks?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutVisibleLeaks?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewTiresVisualInspection?.text.toString().trim()
                    )
                ) {
                    textInputLayoutTiresVisualInspection?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutTiresVisualInspection?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(
                    REPORT_TYPE_72_HOURS_MARKED_VEHICLE_REPORT, ignoreCase = true
                )
            ) {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    textInputLayoutBlock?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBlock?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    textInputLayoutStreet?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutStreet?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    textInputLayoutDeviceId?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDeviceId?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    textInputLayoutStatus?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutStatus?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(REPORT_TYPE_BIKE_INSPECTION_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewTirePressure?.text.toString().trim())) {
                    textInputLayoutTirePressure?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutTirePressure?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewAssignedBike?.text.toString().trim())) {
                    textInputLayoutAssignedBike?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutAssignedBike?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBreaksRotors?.text.toString().trim())) {
                    textInputLayoutBreakRotors?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBreakRotors?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewLightsAndReflectors?.text.toString().trim()
                    )
                ) {
                    textInputLayoutLightsAndReflectors?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutLightsAndReflectors?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewChainCrank?.text.toString().trim())) {
                    textInputLayoutChainCrank?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutChainCrank?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewBatteryFreeOfDebris?.text.toString().trim()
                    )
                ) {
                    textInputLayoutBatteryFreeDebris?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBatteryFreeDebris?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFlatPack?.text.toString().trim())) {
                    textInputLayoutFlatPack?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutFlatPack?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewFirstAidKit?.text.toString().trim())) {
                    textInputLayoutFirstAidKit?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutFirstAidKit?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(
                        mAutoComTextViewTiresVisualInspection?.text.toString().trim()
                    )
                ) {
                    textInputLayoutTiresVisualInspection?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutTiresVisualInspection?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewBikeGlassess?.text.toString().trim())) {
                    textInputLayoutGlasses?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutGlasses?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewBatteryCharge?.text.toString().trim())) {
                    textInputLayoutBatteryCharge?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBatteryCharge?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(
                        mAutoComTextViewGloveVisualInspection?.text.toString().trim()
                    )
                ) {
                    textInputLayoutGloveVisualInspection?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutGloveVisualInspection?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(
                    REPORT_TYPE_EOW_SUPERVISOR_SHIFT_REPORT, ignoreCase = true
                )
            ) {
                if (TextUtils.isEmpty(
                        mAutoComTextViewTotalEnforcementPersonnel?.text.toString().trim()
                    )
                ) {
                    textInputLayoutTotalEnforcementPersonnel?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutTotalEnforcementPersonnel?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewFirst10MinBreak?.text.toString().trim())) {
                    textInputLayoutFirst10MinBreak?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutFirst10MinBreak?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewLunchTaken?.text.toString().trim())) {
                    textInputLayoutLunchTaken?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutLunchTaken?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewSecond10MinBreak?.text.toString().trim())) {
                    textInputLayoutSecond10MinBreak?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSecond10MinBreak?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(
                        mAutoComTextViewComplaintsTowardsOfficer?.text.toString().trim()
                    )
                ) {
                    mInputLayoutComplaintsTowardsOfficer?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutComplaintsTowardsOfficer?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewResidentComplaints?.text.toString().trim())) {
                    mInputLayoutResidentComplaints?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutResidentComplaints?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewWarningsIssued?.text.toString().trim())) {
                    mInputLayoutWarningsIssued?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutWarningsIssued?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    mInputLayoutCitationsIssued?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutCitationsIssued?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(
                    REPORT_TYPE_SPECIAL_ASSIGNMENT_REPORT, ignoreCase = true
                )
            ) {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    textInputLayoutBlock?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutBlock?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    textInputLayoutStreet?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutStreet?.hint?.toString())
                    return false
                }


                if (TextUtils.isEmpty(
                        mAutoComTextViewTotalEnforcementPersonnel?.text.toString().trim()
                    )
                ) {
                    textInputLayoutTotalEnforcementPersonnel?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutTotalEnforcementPersonnel?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewFirst10MinBreak?.text.toString().trim())) {
                    textInputLayoutFirst10MinBreak?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutFirst10MinBreak?.hint?.toString())
                    return false
                }


                if (TextUtils.isEmpty(mAutoComTextViewSecond10MinBreak?.text.toString().trim())) {
                    textInputLayoutSecond10MinBreak?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSecond10MinBreak?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(
                        mAutoComTextViewComplaintsTowardsOfficer?.text.toString().trim()
                    )
                ) {
                    mInputLayoutComplaintsTowardsOfficer?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutComplaintsTowardsOfficer?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewResidentComplaints?.text.toString().trim())) {
                    mInputLayoutResidentComplaints?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutResidentComplaints?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewWarningsIssued?.text.toString().trim())) {
                    mInputLayoutWarningsIssued?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutWarningsIssued?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    mInputLayoutCitationsIssued?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutCitationsIssued?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVehicleMark?.text.toString().trim())) {
                    mInputLayoutVehicleMark?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutVehicleMark?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewAssignedArea?.text.toString().trim())) {
                    textInputLayoutAssignedArea?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutAssignedArea?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewViolationDescription?.text.toString().trim()
                    )
                ) {
                    textInputLayoutViolationDescription?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutViolationDescription?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(
                    REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT, ignoreCase = true
                )
            ) {
                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    textInputLayoutMeterNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterNo?.hint?.toString())
                    return false
                }


                if (TextUtils.isEmpty(mAutoComTextViewLicensePlate?.text.toString().trim())) {
                    mInputLayoutLicensePlate?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutLicensePlate?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewVin?.text.toString().trim())) {
                    textInputLayoutVin?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutVin?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewState?.text.toString().trim())) {
                    textInputLayoutState?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutState?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewMake?.text.toString().trim())) {
                    textInputLayoutMake?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMake?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewModel?.text.toString().trim())) {
                    textInputLayoutModel?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutModel?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewColor?.text.toString().trim())) {
                    textInputLayoutColor?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutColor?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewTimeMark?.text.toString().trim())) {
                    mInputLayoutTimeMark?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutTimeMark?.hint?.toString())
                    return false
                }
                if (mAutoComTextViewTimeMark?.text?.isNotEmpty()
                        .nullSafety() && mAutoComTextViewTimeMark?.text?.contains(
                        "/"
                    ).nullSafety()
                ) {
                    val stringArrya = mAutoComTextViewTimeMark?.text?.split("/")
                    if (stringArrya?.isNotEmpty().nullSafety()) {
                        if (stringArrya?.firstOrNull().nullSafety().toInt() > 31) {
                            mAutoComTextViewTimeMark?.error =
                                getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }

                        if (stringArrya?.getOrNull(1).nullSafety().toInt() > 12) {
                            mAutoComTextViewTimeMark?.error =
                                getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }
                    }
                }

                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    mInputLayoutCitationsIssued?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutCitationsIssued?.hint?.toString())
                    return false
                }
                if (mAutoComTextViewCitationsIssued?.text?.isNotEmpty()
                        .nullSafety() && mAutoComTextViewCitationsIssued?.text?.contains(
                        "/"
                    ).nullSafety()
                ) {
                    val stringArrya = mAutoComTextViewCitationsIssued?.text?.split("/")
                    if (stringArrya?.isNotEmpty().nullSafety()) {
                        if (stringArrya?.firstOrNull().nullSafety().toInt() > 31) {
                            mAutoComTextViewCitationsIssued?.error =
                                getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }

                        if (stringArrya?.getOrNull(1).nullSafety().toInt() > 12) {
                            mAutoComTextViewCitationsIssued?.error =
                                getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }
                    }
                }
                if (TextUtils.isEmpty(mAutoComTextViewDate?.text.toString().trim())) {
                    textInputLayoutDate?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDate?.hint?.toString())
                    return false
                }

                if (mAutoComTextViewDate?.text?.isNotEmpty()
                        .nullSafety() && mAutoComTextViewDate?.text?.contains(
                        "/"
                    ).nullSafety()
                ) {
                    val stringArrya = mAutoComTextViewDate?.text?.split("/")
                    if (stringArrya?.isNotEmpty().nullSafety()) {
                        if (stringArrya?.firstOrNull().nullSafety().toInt() > 31) {
                            mAutoComTextViewDate?.error =
                                getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }

                        if (stringArrya?.getOrNull(1).nullSafety().toInt() > 12) {
                            mAutoComTextViewDate?.error =
                                getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }
                    }
                }
            } else if (reportType.equals(REPORT_TYPE_SIGN_OFF_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewVehicleStoredAt?.text.toString().trim())) {
                    mInputLayoutVehicleStoredAt?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutVehicleStoredAt?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTowFileNumber?.text.toString().trim())) {
                    mInputLayoutTowFileNumber?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutTowFileNumber?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(
                        mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.text.toString()
                            .trim()
                    )
                ) {
                    textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot?.showErrorWithShake(
                        getString(R.string.val_msg_please_enter) + " " + textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot?.hint?.toString()
                    )
                    return false
                }

            } else if (reportType.equals(REPORT_TYPE_TOW_REPORT, ignoreCase = true)) {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    textInputLayoutMeterNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterNo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewLicensePlate?.text.toString().trim())) {
                    mInputLayoutLicensePlate?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutLicensePlate?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewState?.text.toString().trim())) {
                    textInputLayoutState?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutState?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewVin?.text.toString().trim())) {
                    textInputLayoutVin?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutVin?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMake?.text.toString().trim())) {
                    textInputLayoutMake?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMake?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewModel?.text.toString().trim())) {
                    textInputLayoutModel?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutModel?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewColor?.text.toString().trim())) {
                    textInputLayoutColor?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutColor?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewReasonForTow?.text.toString().trim())) {
                    textInputLayoutReasonForTow?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutReasonForTow?.hint?.toString())
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewDate?.text.toString().trim())) {
                    textInputLayoutDate?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDate?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewInteriorCleanliness?.text.toString().trim()
                    )
                ) {
                    mInputLayoutInteriorCleanliness?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutInteriorCleanliness?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOverHeating?.text.toString().trim())) {
                    textInputLayoutOverHeating?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutOverHeating?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewSpecialEnforcementRequest?.text.toString().trim()
                    )
                ) {
                    mInputLayoutSpecialEnforcementRequest?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutSpecialEnforcementRequest?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewGarageClearance?.text.toString().trim())) {
                    mInputLayoutGarageClearance?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutGarageClearance?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFileNumber?.text.toString().trim())) {
                    mInputLayoutFileNumber?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutFileNumber?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVehicleCondition?.text.toString().trim())) {
                    mInputLayoutVehicleCondition?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutVehicleCondition?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOfficerPhoneNumber?.text.toString().trim())) {
                    mInputLayoutOfficerPhoneNumber?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutOfficerPhoneNumber?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewRo?.text.toString().trim())) {
                    mInputLayoutRo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutRo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVehicleWithInLot?.text.toString().trim())) {
                    mInputLayoutVehicleWithInLot?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutVehicleWithInLot?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTowingOfficer?.text.toString().trim())) {
                    textInputLayoutTowingOfficer?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutTowingOfficer?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDriverSideComment?.text.toString().trim())) {
                    mInputLayoutDriverSideComment?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutDriverSideComment?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewPassengerSideComment?.text.toString().trim()
                    )
                ) {
                    mInputLayoutPassengerSideComment?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutPassengerSideComment?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFrontSideComment?.text.toString().trim())) {
                    mInputLayoutFrontSideComment?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutFrontSideComment?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewRearSideComment?.text.toString().trim())) {
                    mInputLayoutRearSideComment?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutRearSideComment?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTrailerComment?.text.toString().trim())) {
                    mInputLayoutTrailerComment?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutTrailerComment?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(
                    REPORT_TYPE_NFL_REPORT, ignoreCase = true
                ) || reportType.equals(
                    REPORT_TYPE_HARD_REPORT, ignoreCase = true
                ) || reportType.equals(REPORT_TYPE_PAY_LOT_REPORT, ignoreCase = true)
            ) {
                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (mAutoComTextViewMeterNo != null && TextUtils.isEmpty(
                        mAutoComTextViewMeterNo?.text.toString().trim()
                    )
                ) {
                    textInputLayoutMeterNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterNo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
                    mInputLayoutField1?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutField1?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCarCount?.text.toString().trim())) {
                    mInputLayoutCarCount?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutCarCount?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewEmptySpace?.text.toString().trim())) {
                    mInputLayoutEmptySpace?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutEmptySpace?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewNumberOfViolatingVehicles?.text.toString().trim()
                    )
                ) {
                    mInputLayoutNumberOfViolatingVehicles?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutNumberOfViolatingVehicles?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(REPORT_TYPE_AFTER_SEVEN_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCarCount?.text.toString().trim())) {
                    mInputLayoutCarCount?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutCarCount?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewLotArea?.text.toString().trim())) {
                    mInputLayoutLotArea?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutLotArea?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewSecurityObservation?.text.toString().trim()
                    )
                ) {
                    mInputLayoutSecurityObservation?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutSecurityObservation?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(REPORT_TYPE_PAY_STATION_REPORT, ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    textInputLayoutMeterNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterNo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
                    mInputLayoutField1?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutField1?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField4?.text.toString().trim())) {
                    mInputLayoutField4?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutField4?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(REPORT_TYPE_PAY_SIGNAGE_REPORT, ignoreCase = true)) {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    textInputLayoutMeterNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterNo?.hint?.toString())
                    return false
                }
            } else if (reportType.equals(REPORT_TYPE_PAY_HOMELESS_REPORT, ignoreCase = true)) {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    textInputLayoutMeterNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterNo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
                    mInputLayoutField1?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutField1?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(
                    REPORT_TYPE_PAY_LOT_INSPECTION_REPORT, ignoreCase = true
                )
            ) {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewInternetConnectivity?.text.toString().trim()
                    )
                ) {
                    textInputLayoutInternetConnectivity?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutInternetConnectivity?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterLabelVisible?.text.toString().trim())) {
                    textInputLayoutMeterLableVisible?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterLableVisible?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewDigitalDisplayVisible?.text.toString().trim()
                    )
                ) {
                    textInputLayoutDigitalDisplayVisible?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutDigitalDisplayVisible?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCoinJam?.text.toString().trim())) {
                    textInputLayoutCoinJam?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutCoinJam?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(REPORT_TYPE_WORK_ORDER_REPORT, ignoreCase = true)) {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
                    mInputLayoutField1?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutField1?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField2?.text.toString().trim())) {
                    mInputLayoutField2?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutField2?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(REPORT_TYPE_PAY_TRASH_REPORT, ignoreCase = true)) {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewRequiredServices?.text.toString().trim())) {
                    textInputLayoutRequiredServices?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutRequiredServices?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField2?.text.toString().trim())) {
                    mInputLayoutField2?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutField2?.hint?.toString())
                    return false
                }

            } else if (reportType.equals(
                    REPORT_TYPE_PAY_SAFETY_REPORT, ignoreCase = true
                )
            ) {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    textInputLayoutPBCZone?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutPBCZone?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    textInputLayoutMeterNo?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutMeterNo?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSafetyIssue?.text.toString().trim())) {
                    textInputLayoutSafetyIssue?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + textInputLayoutSafetyIssue?.hint?.toString())
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField2?.text.toString().trim())) {
                    mInputLayoutField2?.showErrorWithShake(getString(R.string.val_msg_please_enter) + " " + mInputLayoutField2?.hint?.toString())
                    return false
                }

            }

            if (cameraCount < imageValidationCount && cameraCount < minimumImageRequired) {
                requireContext().toast(
                    getString(
                        R.string.error_desc_max_image_reached, minimumImageRequired.toString()
                    )
                )
                return false
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    private fun openTimePicker(timePickerField: AppCompatAutoCompleteTextView?) {
        val auto = timePickerField ?: return

        val nowCal = Calendar.getInstance()
        val hour = nowCal.get(Calendar.HOUR_OF_DAY)
        val minute = nowCal.get(Calendar.MINUTE)

        val specialReportTimeFormat =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfHour ->
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAMETRO, true
                ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)
            ) {
                val datetime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minuteOfHour)
                }
                val amPm = if (datetime.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
                val hourToShow = datetime.get(Calendar.HOUR).let { if (it == 0) 12 else it }

                auto.post {
                    auto.setText(
                        String.format(
                            Locale.US, "%d:%02d %s", hourToShow, datetime.get(Calendar.MINUTE), amPm
                        )
                    )
                }
            } else {
                auto.setText(String.format(Locale.US, "%02d%02d", hourOfDay, minuteOfHour))
            }

            auto.tag = "${specialReportTimeFormat}T$hourOfDay:$minuteOfHour:11Z"
            auto.error = null
        }

        TimePickerDialog(requireContext(), listener, hour, minute, false).show()
    }

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle() {
        val auto = mAutoComTextViewMake ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val carMakeList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getCarMakeListFromDataSet()
            } ?: return@launch

            val unique =
                carMakeList.map { "${it.make.nullSafety()}#${it.makeText.nullSafety()}" }.distinct()
                    .sorted()

            if (unique.isEmpty()) {
                auto.setText("")
                auto.setAdapter(null)
                return@launch
            }

            val dropdown = unique.map { it.split("#", limit = 2)[1] }.toTypedArray()
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_lpr_details_item, dropdown)

            try {
                auto.threshold = 1
                auto.setAdapter(adapter)
                auto.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    requireActivity().hideSoftKeyboard()
                    val selected = parent.getItemAtPosition(position)?.toString().orEmpty()
                    val index = carMakeList.getIndexOfMakeText(selected)
                    if (index >= 0) {
                        mSelectedMake = carMakeList[index].make
                        mSelectedMakeValue = carMakeList[index].makeText
                        setDropdownVehicleModel(mSelectedMake, false)
                    } else {
                        setDropdownVehicleModel("", false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?, isAutoSelect: Boolean?) {
        val auto = mAutoComTextViewModel ?: return
        mModelList.clear()

        viewLifecycleOwner.lifecycleScope.launch {
            val carModelList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getCarModelListFromDataSet()
            } ?: return@launch

            if (carModelList.isEmpty()) {
                auto.setText("")
                auto.setAdapter(null)
                return@launch
            }

            val filtered = try {
                carModelList.filter { it.make == mSelectedMake }.map { ds ->
                    DatasetResponse().apply {
                        model = ds.model
                        model_lookup_code = ds.model_lookup_code.nullSafety("")
                        make = ds.make
                        makeText = ds.makeText
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }

            if (filtered.isEmpty()) {
                auto.setText("")
                auto.setAdapter(null)
                return@launch
            }

            val sorted = filtered.sortedBy { it.model.nullSafety().toString() }

            mModelList.apply {
                clear()
                addAll(sorted)
            }

            val dropdown = sorted.map { it.model.nullSafety().toString() }.toTypedArray()
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_lpr_details_item, dropdown)

            auto.threshold = 1
            auto.setAdapter(adapter)

            val pos = if (!value.isNullOrEmpty()) {
                sorted.indexOfFirst { it.make == value || it.model == value }
            } else -1

            if (pos >= 0 && isAutoSelect == true) {
                auto.setText(dropdown[pos])
                mSelectedModel = sorted[pos].model
                mSelecteModelLookupCode = sorted[pos].model_lookup_code
            } else {
                auto.setText("")
            }

            auto.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                requireActivity().hideSoftKeyboard()
                val selected = parent.getItemAtPosition(position)?.toString().orEmpty()
                val index = dropdown.indexOf(selected)
                if (index >= 0) {
                    mSelectedModel = sorted[index].model
                    mSelecteModelLookupCode = sorted[index].model_lookup_code
                }
            }
        }
    }

    //set value to vehicle colour dropdown
    private fun setDropdownVehicleColour() {
        val auto = mAutoComTextViewColor ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val carColorList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getCarColorListFromDataSet()
            } ?: return@launch

            if (carColorList.isEmpty()) return@launch

            val sorted = try {
                carColorList.sortedBy { it.description.nullSafety().toString() }
            } catch (e: Exception) {
                e.printStackTrace()
                carColorList
            }

            val dropdown = sorted.map { it.description.nullSafety().toString() }.toTypedArray()
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_lpr_details_item, dropdown)

            try {
                auto.threshold = 1
                auto.setAdapter(adapter)
                auto.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    val selected = parent.getItemAtPosition(position)?.toString().orEmpty()
                    val index = sorted.getIndexOfColor(selected)
                    if (index >= 0) {
                        mSelectedColor = sorted[index].color_code
                    }
                    requireActivity().hideSoftKeyboard()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to Lot dropdown.
    private fun setDropdownLot() {
        val auto = mAutoComTextViewLotArea ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val lotList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getLotListFromDataSet()
            } ?: return@launch

            if (lotList.isEmpty()) return@launch

            val sorted = try {
                lotList.sortedWith(compareBy { it.location.nullSafety() })
            } catch (e: Exception) {
                e.printStackTrace()
                lotList
            }

            val dropdown = sorted.map { it.location.nullSafety() }.toTypedArray()
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdown)

            try {
                auto.threshold = 1
                auto.setAdapter(adapter)
                auto.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    requireActivity().hideSoftKeyboard()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to State dropdown
    private fun setDropdownState() {
        val auto = mAutoComTextViewState ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val stateList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getStateListFromDataSet()
            } ?: return@launch

            if (stateList.isEmpty()) return@launch

            val sorted = try {
                stateList.sortedBy { it.state_name.nullSafety() }
            } catch (e: Exception) {
                stateList
            }

            val dropdown = sorted.map { it.state_name.nullSafety().toString() }.toTypedArray()
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdown)

            try {
                auto.threshold = 1
                auto.setAdapter(adapter)
                auto.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    requireActivity().hideSoftKeyboard()
                    val index =
                        sorted.getIndexOfStateName(parent.getItemAtPosition(position).toString())
                    mState2DigitCode = sorted.getOrNull(index)?.state_abbreviated
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setDropdownZone() {
        val view = mAutoComTextViewZone ?: return
        val dropdownList = ReportArrayClass.mZone2ndForAllReport

        view.post {
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList)
            try {
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setDropdownPBCZone() {
        val view = mAutoComTextViewPBCZone ?: return

        val dropdownList = if (reportType.equals(
                REPORT_TYPE_72_HOURS_NOTICE_TOW_REPORT, ignoreCase = true
            ) || reportType.equals(REPORT_TYPE_TOW_REPORT, ignoreCase = true)
        ) {
            ReportArrayClass.mZoneForTowAnd72Hour
        } else {
            ReportArrayClass.mZone2ndForAllReport
        }

        view.post {
            try {
                view.threshold = 1
                view.setAdapter(
                    ArrayAdapter(
                        requireContext(), R.layout.row_dropdown_menu_popup_item, dropdownList
                    )
                )
                view.setOnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }

                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutPBCZone
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setSignatureView() {
        requireActivity().hideSoftKeyboard()
        SignatureBottomSheet().show(
            parentFragmentManager, getString(R.string.bottom_sheet_signature_sheet)
        )

        setFragmentResultListener(SignatureBottomSheet.REQUEST_KEY) { _, bundle ->
            val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(SignatureBottomSheet.BUNDLE_KEY_BITMAP, Bitmap::class.java)
            } else {
                @Suppress("DEPRECATION") bundle.getParcelable(SignatureBottomSheet.BUNDLE_KEY_BITMAP) as Bitmap?
            }

            saveImageMM(bmp)
        }
    }
}